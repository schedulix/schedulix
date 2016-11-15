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

import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Text;
import de.independit.scheduler.server.output.SDMSOutput;
import de.independit.scheduler.server.output.SDMSOutputContainer;
import de.independit.scheduler.server.output.SDMSOutputError;
import de.independit.scheduler.shell.SDMSServerConnection;

public class SDMSsubmitThreads
{
	static String host  = null;
	static String port  = null;
	static String id    = null;
	static String key   = null;
	static String child = null;
	static String delay = "0";
	static int number   = 3;
	static boolean silent = false;
	static boolean ignoreGuiFailure = false;
	static Integer waitTime = null;

	static SDMSServerConnection connection = null;
	static SDMSOutput output = null;
	static boolean failure = false;
	static boolean gui_failure = false;

	public static void main(String[] args)
	{
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
			if (arg.equals("")) {
				continue;
			} else if (arg.equals("-h") || arg.equals("--host")) {
				if (next == null) errorExit(arg + "option without value");
				host = next;
				i++;
			} else if (arg.equals("-p") || arg.equals("--port")) {
				if (next == null) errorExit(arg + "option without value");
				port = next;
				i++;
			} else if (arg.equals("-i") || arg.equals("--id")) {
				if (next == null) errorExit(arg + "option without value");
				id = next;
				i++;
			} else if (arg.equals("-k") || arg.equals("--key")) {
				if (next == null) errorExit(arg + "option without value");
				key = next;
				i++;
			} else if (arg.equals("-c") || arg.equals("--child")) {
				if (next == null) errorExit(arg + "option without value");
				child = next;
				i++;
			} else if (arg.equals("-d") || arg.equals("--delay")) {
				if (next == null) errorExit(arg + "option without value");
				delay = next;
				i++;
			} else if (arg.equals("-I") || arg.equals("--ignore_gui_failure")) {
				ignoreGuiFailure = true;
			} else if (arg.equals("-s") || arg.equals("--silent")) {
				silent = true;
			} else if (arg.equals("-n") || arg.equals("--number")) {
				if (next == null) errorExit(arg + "option without value");
				number = SDMSpopup.rndConf(next);
				i++;
			} else if (arg.equals("-t") || arg.equals("--time")) {
				if (next == null) errorExit(arg + "option without value");
				waitTime = new Integer(SDMSpopup.rndConf(next));
				i++;
			} else if (arg.equals("-?") || arg.equals("--help")) {
				printOptions();
				System.exit(0);
			} else
				errorExit("Illegal option: " + arg);
		}
		if (host == null || port == null || id == null || key == null || child == null) {
			printOptions();
			System.exit(1);
		}

		connection = new SDMSServerConnection(host, new Integer(port).intValue(), id, key);
		try {
			output = connection.connect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (output.error != null) {
			System.err.println("Error Connection to BICsuite!server !");
			System.err.println("Error(" + output.error.code + "):" + output.error.message);
			System.exit(1);
		}

		Thread guiThread = null;
		if (!silent) {
			try {
				Class guiThreadClass = Class.forName("de.independit.scheduler.demo.SDMSsubmitThreadsGuiThread");
				guiThread = (Thread)(guiThreadClass.getDeclaredConstructor().newInstance());
				guiThread.start();
				guiThread.join();
			} catch (Throwable t) {
				System.err.println("Cannot open GUI (Throwable:" + t.toString() + ")");
				if (!ignoreGuiFailure) {
					System.err.println("exit (1)");
					System.exit(1);
				} else {
					System.err.println("ignored (-I flag is set) , running in silent mode");
					gui_failure = true;
				}
			}
		}
		if (silent || gui_failure) {
			for (int i = 1; i <= number; i++) {
				System.err.println("Submit thread number " + i);
				submitThread(i);
				int d = SDMSpopup.rndConf(delay);
				if (d > 0 && i < number) {
					System.err.println("Waiting " + d + " seconds");
					try {
						Thread.sleep(d * 1000);
					} catch (InterruptedException e1) {
						System.err.println("Exception (" + e1.toString() + ") during sleep() !");
					}
				}
			}
		}

		try {
			connection.finish();
		} catch (IOException e) {
			System.err.println("Warning: Exceptiom closing server connection (" + e.toString() + ") !");
		}
		if (failure) {
			System.err.println("One or more child submit failed !");
			System.exit(1);
		}
	}

	private static void printOptions()
	{
		System.err.println ("allowed options:");
		System.err.println ("  -h, --host   <hostname>     host name of the scheduling server");
		System.err.println ("  -p, --port   <portnumber>   port number of the scheduling server");
		System.err.println ("  -i, --id     <jobid>        job id of the job running this command");
		System.err.println ("  -k, --key    <jobkey>       job key of the job running this command");
		System.err.println ("  -I, --ignore_gui_failure    ignore GUI failure");
		System.err.println ("  -s, --silent                silent, run without GUI");
		System.err.println ("  -c, --child  <alias>        job name or alias of the child to submit dynically");
		System.err.println ("  -d, --delay  histogram      time between job submissions");
		System.err.println ("  -n, --number histogram      number of jobs to submit");
		System.err.println ("  -t, --time   histogram      time to wait for gui input before running automaticly");
		System.err.println ("  -?, --help                  number of jobs to submit");
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
		System.exit(1);
	}

	static void submitThread(int tag)
	{
		String strTag = new Integer(tag).toString();
		output = connection.execute("SUBMIT '" + child + "' WITH CHILDTAG = '" + strTag + "'");
		if (output.error != null) {
			if (output.error.code.equals("03110181528")) {
				System.out.println("");
				System.err.println("Error submitting child (ignored) !");
				System.err.println("Error(" + output.error.code + "):" + output.error.message);
			} else {
				System.out.println("");
				System.err.println("Error submitting child !");
				System.err.println("Error(" + output.error.code + "):" + output.error.message);
				failure = true;
			}
		}
	}
}
