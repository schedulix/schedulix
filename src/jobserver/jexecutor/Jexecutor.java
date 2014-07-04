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


package de.independit.scheduler.jobserver.jexecutor;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.nio.channels.*;
import de.independit.scheduler.jobserver.*;
import de.independit.scheduler.jobserver.Utils.*;

public abstract class Jexecutor
{
	public static final char BT_SYSTEM = 'S';
	public static final char BT_FILE   = 'F';
	public static final char BT_NONE   = 'N';

	public static final char PID_SEP   = '@';
	public static final char JIF_SEP   = '+';

	public static final int ARGS_NOTREAD = -1;
	public static final int ARGS_RUN     = 0;
	public static final int ARGS_VERSION = 1;
	public static final int ARGS_HELP    = 2;

	public String boottime = "0";
	public char how = 'N';
	public String taskfileName = null;

	protected Feil taskfile;
	protected Map<String,String> env = null;

	protected int argsType = ARGS_NOTREAD;

	public Jexecutor()
	{
		initialize();
	}

	public Jexecutor(Map<String,String> env)
	{
		initialize();
		this.env = env;
	}

	private void initialize ()
	{
		argsType = ARGS_NOTREAD;
	}

	public abstract String getMyStarttime();
	public abstract String getUserStarttime();

	public abstract String getPid();

	public abstract String getPid(Process p);

	public String getProcessId()
	{
		String pid = getPid();
		String starttime = getMyStarttime();

		return pid + PID_SEP + how + boottime + JIF_SEP + starttime;
	}

	public String getProcessId(Process p)
	{
		String pid = getPid(p);
		String starttime = getUserStarttime();

		return pid + PID_SEP + how + boottime + JIF_SEP + starttime;
	}

	protected static String getUsage()
	{
		return "Usage:\n" +
		       "Jexecutor [--version|-v] [--help|-h] [<boottime_how> <taskfileName> [boottime]]\n" +
		       "\n" +
		       "Exactly one of the optional argument sets must be specified\n" +
		       "i.e. either the version request, or this help request or some specification on what to do";
	}

	protected static String getVersion()
	{
		return "Jobserver (executor) 2.6\n" +
		       "Copyright (C) 2013 independIT Integrative Technologies GmbH\n" +
		       "All rights reserved";
	}

	protected int checkArgs(String[] args)
		throws IllegalArgumentException, RuntimeException
	{

		if (args.length >= 1 && (args[0].equals("--version") || args[0].equals("-v"))) {
			argsType = ARGS_VERSION;
			return ARGS_VERSION;
		}
		if (args.length >= 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
			argsType = ARGS_HELP;
			return ARGS_HELP;
		}
		if (args.length == 2 || args.length == 3) {
			if (args.length == 2) {

				taskfileName = args[1];
			} else {
				how = args[0].charAt(0);
				taskfileName = args[1];
				boottime = args[2];
			}
			taskfile = new Feil(taskfileName);
			try {
				taskfile.open();
				taskfile.scan();
				taskfile.close();
			} catch (Exception e) {
				throw new RuntimeException("Error occurred during initial taskfile processing");
			}
			argsType = ARGS_RUN;
			return ARGS_RUN;
		}
		throw new IllegalArgumentException();
	}

	public abstract void run ();

	private static void sleep(long t)
	{
		try {
			Thread.sleep(t);
		} catch (Exception e) {}
	}

}
