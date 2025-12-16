/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

schedulix Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of schedulix

schedulix is free software:
you can redistribute it and/or modify it under the terms of the
GNU Affero General Public License as published by the
Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package de.independit.scheduler.demo;

import java.util.Vector;
import java.util.Random;

public class SDMSpopup
{
	public static int ERRORCODE = 19;
	public static boolean silent = false;

	public static void main(String[] args) throws InterruptedException
	{
		String config = null;
		boolean ignoreGuiFailure = false;
		int exitCode = 0;
		Integer runTimeSecs = null;
		Vector<String> displayArgs = new Vector<String>();

		Vector<String> v_args = new Vector<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				String[] arg = args[i].split("\\s+");
				for (int j = 0; j < arg.length; j++) {
					v_args.add(arg[j]);
				}
			} else
				v_args.add(args[i]);
		}

		for (int i = 0; i < v_args.size(); i++) {
			String arg = v_args.elementAt(i);
			String next = null;
			if (i + 1 < v_args.size())
				next = v_args.elementAt(i + 1);

			if (arg.equals("-c") || arg.equals("--config")) {
				if (next == null) errorExit(arg + "option without value");
				config = next;
				i++;
			} else if (arg.equals("-t") || arg.equals("--time")) {
				if (next == null) errorExit(arg + "option without value");
				runTimeSecs = Integer.valueOf(rndConf(next));
				i++;
			} else if (arg.equals("-e") || arg.equals("--exit")) {
				if (next == null) errorExit(arg + "option without value");
				exitCode = rndConf(next);
				i++;
			} else if (arg.equals("-I") || arg.equals("--ignore_gui_failure")) {
				ignoreGuiFailure = true;
			} else if (arg.equals("-s") || arg.equals("--silent")) {
				silent = true;
			} else if (arg.equals("-?") || arg.equals("--help")) {
				printOptions();
				System.exit(0);
			} else {
				displayArgs.add(arg);
			}
		}
		if (config == null) {
			config = "?:1=FAILURE:0=SUCCESS";
		}

		Thread guiThread = null;
		if (!silent) {
			try {
				Class guiThreadClass = Class.forName("de.independit.scheduler.demo.SDMSpopupGuiThread");
				Class[] cArg = new Class[5];
				cArg[0] = Vector.class;
				cArg[1] = String.class;
				cArg[2] = boolean.class;
				cArg[3] = Integer.class;
				cArg[4] = int.class;
				guiThread = (Thread)(guiThreadClass.getDeclaredConstructor(cArg).newInstance(displayArgs, config, ignoreGuiFailure, runTimeSecs, exitCode));
				guiThread.start();
			} catch (Throwable t) {
				System.err.println("Cannot open GUI (Throwable:" + t.toString() + ")");
				if (!ignoreGuiFailure) {
					System.err.println("exit (" + ERRORCODE + ")");
					System.exit(ERRORCODE);
				} else {
					System.err.println("ignored (-I flag is set) , running in silent mode");
					silent = true;
				}
			}
		}

		if (!silent) {
			guiThread.join();
		}
		if (silent && runTimeSecs != null) {
			System.err.println("auto exit in " + runTimeSecs.intValue() + " seconds with exit code " + exitCode);
			Thread.sleep(runTimeSecs.intValue() * 1000);
		}
		System.exit (exitCode);
	}

	private static void printOptions()
	{
		System.err.println ("allowed options:");
		System.err.println ("  -c, --config buttonconf    exit code field and button configuration");
		System.err.println ("  -e, --exit   histogram     exit code probability configuration");
		System.err.println ("  -t, --time   histogram     runtime probability configuration in seconds");
		System.err.println ("  -I, --ignore_gui_failure   ignore GUI failure");
		System.err.println ("  -s, --silent               silent, run without GUI");
		System.err.println ("  -?, --help                 print options");
		System.err.println ("buttonconf:");
		System.err.println ("   buttonconfElement { : buttonconfElement }");
		System.err.println ("   buttonconfElement:");
		System.err.println ("      ? | <exitcode>=<buttontext>");
		System.err.println ("   ? will display a entry field to manually set the exit code");
		System.err.println ("   buttonconf defaults to '?:1=FAILURE:0=SUCCESS'");
		System.err.println ("histogram:");
		System.err.println ("   histogramElement { : histogramElement }");
		System.err.println ("   histogramElement:");
		System.err.println ("      histogramValue | histogramValue=histogramCount");
		System.err.println ("      histogramValue:");
		System.err.println ("         <integer> | <integer>-<integer>");
		System.err.println ("   if more than one histogramElement is given, all elements must have a histogramCount");
	}

	private static void errorExit(String msg)
	{
		System.err.println(msg + "!");
		printOptions();
		System.exit(ERRORCODE);
	}

	public static int rndConf(String conf)
	{
		int ret = 0;
		try {
			Random rnd = new Random();
			Vector<HistogramElement> histElements = new Vector<HistogramElement>();
			String[] strHist = conf.split(":");
			long histTotal = 0;
			for (int i = 0; i < strHist.length; i ++) {
				String[] strHistElement = strHist[i].split("=");
				String[] strValueRange = strHistElement[0].split("-");
				int histValue;
				if (strValueRange.length == 1)
					histValue = Integer.parseInt(strValueRange[0].trim());
				else {
					int histValueMin = Integer.parseInt(strValueRange[0].trim());
					int histValueMax = Integer.parseInt(strValueRange[1].trim());
					histValue = (int)(rnd.nextDouble() * (histValueMax - histValueMin + 1)) + histValueMin;
				}
				if (strHist.length == 1)
					return histValue;
				int histCount = Integer.parseInt(strHistElement[1].trim());
				histElements.add(new HistogramElement(histValue, histCount));
				histTotal += histCount;
			}
			long rndTotal = (long)((new Random()).nextDouble() * histTotal) + 1;
			for (int i = 0; i < histElements.size(); i ++) {
				if (histElements.elementAt(i).count >= rndTotal) {
					ret = histElements.elementAt(i).value;
					break;
				}
				rndTotal -= histElements.elementAt(i).count;
			}
		} catch (Exception e) {
			System.err.println ("Illegal random configuration:" + conf);
			printOptions();
			System.exit(ERRORCODE);
		}

		return ret;
	}
}

class HistogramElement
{
	public int value;
	public int count;

	public HistogramElement (int value, int count)
	{
		this.value = value;
		this.count = count;
	}
}
