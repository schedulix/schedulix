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

	public static void main(String[] args) throws InterruptedException
	{
		String config = null;
		boolean silent = false;
		boolean ignoreGuiFailure = false;
		int exitCode = 0;
		Integer runTimeSecs = null;
		Vector<String> displayArgs = new Vector<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c") || args[i].equals("--config")) {
				config = args[i+1];
				i++;
			} else if (args[i].equals("-t") || args[i].equals("--time")) {
				runTimeSecs = new Integer(rndConf(args[i+1]));
				i++;
			} else if (args[i].equals("-e") || args[i].equals("--exit")) {
				exitCode = rndConf(args[i+1]);
				i++;
			} else if (args[i].equals("-I") || args[i].equals("--ignore_gui_failure")) {
				ignoreGuiFailure = true;
			} else if (args[i].equals("-s") || args[i].equals("--silent")) {
				silent = true;
			} else if (args[i].equals("-?") || args[i].equals("--help")) {
				printOptions();
				System.exit(0);
			} else if (args[i].startsWith("-")) {
				String[] arg = args[i].split("\\s");
				if (arg.length == 2 && (arg[0].equals("-c") || arg[0].equals("--config"))) {
					config = arg[1];
				} else if (arg.length == 2 && (arg[0].equals("-t") || arg[0].equals("--time"))) {
					runTimeSecs = new Integer(SDMSpopup.rndConf(arg[1]));
				} else if (arg.length == 2 && (arg[0].equals("-e") || arg[0].equals("--exit"))) {
					exitCode = rndConf(arg[1]);
				} else {
					System.err.println("Illegal option: " + args[i]);
					printOptions();
					System.exit(ERRORCODE);
				}
			} else {
				displayArgs.add(args[i]);
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
				}
			}
		}

		if (runTimeSecs == null) {
			if (guiThread != null)
				guiThread.join();
		} else {
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
