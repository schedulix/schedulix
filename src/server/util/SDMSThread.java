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
package de.independit.scheduler.server.util;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;

public abstract class SDMSThread extends Thread
{
	public SerializationException lastSerializationException = null;

	public static final String __version = "@(#) $Id: SDMSThread.java,v 2.9.2.1 2013/03/14 10:25:29 ronald Exp $";

	public final static int SEVERITY_DEBUG   =  3;
	public final static int SEVERITY_MESSAGE =  2;
	public final static int SEVERITY_WARNING =  1;
	public final static int SEVERITY_INFO    =  0;
	public final static int SEVERITY_ERROR   = -1;
	public final static int SEVERITY_FATAL   = -2;

	protected volatile boolean run;

	private static final SimpleDateFormat sysDateFmt = (SimpleDateFormat) SystemEnvironment.staticSystemDateFormat.clone();

	public int readLock = ObjectLock.SHARED;
	public SDMSThread lockThread = null;

	public SDMSThread()
	{
		super();
		run = true;
	}

	public SDMSThread(String s)
	{
		super(s);
		run = true;
	}

	public SDMSThread(Runnable r)
	{
		super(r);
		run = true;
	}

	public SDMSThread  (ThreadGroup t, String s)
	{
		super(t, s);
		run = true;
	}

	public void print(PrintStream o)
	{
		o.println(getName());
	}

	public abstract int id();
	public void do_stop()
	{
		run = false;
		interrupt();
	}

	public void setTimeout(int newTimeout)
	{
	}

	public int getTimeout()
	{
		return 0;
	}

	public static String severityName(int severity)
	{
		switch(severity)  {
			case SEVERITY_DEBUG:	return "DEBUG  ";
			case SEVERITY_MESSAGE:	return "MESSAGE";
			case SEVERITY_WARNING:	return "WARNING";
			case SEVERITY_INFO:	return "INFO   ";
			case SEVERITY_ERROR:	return "ERROR  ";
			case SEVERITY_FATAL:	return "FATAL  ";
		}
		return "UNKNOWN";
	}

	public static final String getHeader (final ConnectionEnvironment cEnv, final int severity)
	{
		String name;
		String thread = Thread.currentThread().getName();
		if (cEnv != null) {
			final Long uid = cEnv.uid();
			if (uid != null)
				name = cEnv.uid() + "," + cEnv.name() + "(" + thread + ")";
			else
				name = cEnv.name() + "(" + thread + ")";
		} else
			name = thread;

		String header = severityName (severity) + " [" + name + "]";
		if (name.length() < 6)
			header += "\t";

		synchronized (sysDateFmt) {
			return header + "\t" + sysDateFmt.format (new java.util.Date (System.currentTimeMillis())) + " ";
		}
	}

	public static void doTrace(ConnectionEnvironment cEnv, String msg, int severity)
	{
		doTrace(cEnv, msg, null, severity, false);
	}
	public static void doTrace(ConnectionEnvironment cEnv, String txt, Object msg[], int severity)
	{
		doTrace(cEnv, txt, msg, severity, false);
	}
	public static void doTrace(ConnectionEnvironment cEnv, String txt, Object msg[], int severity, boolean fatalIsError)
	{
		String header = getHeader(cEnv, severity);
		if(severity <= SystemEnvironment.getTraceLevel() || (cEnv != null && severity <= cEnv.getTraceLevel())) {
			System.err.println(header + txt);
			if (msg != null) {
				for(int i = 0; i < msg.length; i++) {
					System.err.println(header + msg[i].toString());
				}
			}
		}

		if(severity <= SEVERITY_ERROR) {
			printStackTrace(header);
		}
		if(severity == SEVERITY_FATAL && !fatalIsError) {
			System.exit(1);
		}
	}

	public static void printMyStackTrace(String header)
	{
		StackTraceElement[] ste = (new Throwable()).getStackTrace();

		System.err.println(header + "****************** Start Stacktrace *********************");
		for(int i = 1; i < ste.length; i++) {
			System.err.println(header + ste[i].toString());
		}
		System.err.println(header + "****************** End Stacktrace   *********************");
	}

	private static void printStackTrace(String header)
	{
		StackTraceElement[] ste = (new Throwable()).getStackTrace();

		System.err.println(header + "****************** Start Stacktrace *********************");
		for(int i = 2; i < ste.length; i++) {
			System.err.println(header + ste[i].toString());
		}
		System.err.println(header + "****************** End Stacktrace   *********************");
	}

	public abstract void SDMSrun();

	public void run()
	{
		if (lockThread != null) {
			super.run();
			return;
		}
		try {
			SDMSrun();
		} catch (Exception e) {
			try {
				doTrace(null, e.toString(), e.getStackTrace(), SEVERITY_FATAL);
			} catch (Error e2) {
				try {
					System.err.println("FATAL\t *****************************************");
					System.err.println("FATAL\t *                                       *");
					System.err.println("FATAL\t * SEVERE ERROR, PROBABLY OUT OF MEMORY! *");
					System.err.println("FATAL\t *                                       *");
					System.err.println("FATAL\t *****************************************");
				} catch (Error e3) {

				}
				Runtime.getRuntime().halt(1);
			}
		} catch (Error e) {
			try {
				doTrace(null, e.toString(), e.getStackTrace(), SEVERITY_FATAL);
			} catch (Error e2) {
				try {
					System.err.println("FATAL\t *****************************************");
					System.err.println("FATAL\t *                                       *");
					System.err.println("FATAL\t * SEVERE ERROR, PROBABLY OUT OF MEMORY! *");
					System.err.println("FATAL\t *                                       *");
					System.err.println("FATAL\t *****************************************");
				} catch (Error e3) {

				}
			}
			Runtime.getRuntime().halt(1);
		}
	}
}
