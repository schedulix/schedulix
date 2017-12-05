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

package de.independit.scheduler.SDMSApp;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

import de.independit.scheduler.shell.*;
import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.exception.*;

public class App
{
	public static final String __version = "@(#) $Id: App.java,v 1.21.4.2 2013/03/15 12:17:20 ronald Exp $";
	public static final String HOST = "HOST";
	public static final String PORT = "PORT";
	public static final String USER = "USER";
	public static final String PASS = "PASS";
	public static final String JID = "JID";
	public static final String KEY = "KEY";
	public static final String SILENT = "SILENT";
	public static final String VERBOSE = "VERBOSE";
	public static final String TIMEOUT = "TIMEOUT";
	public static final String CONNTIMEOUT = "CONNTIMEOUT";
	public static final String CYCLE = "CYCLE";
	public static final String INI = "INI";
	public static final String HELP = "HELP";
	public static final String SUSPEND = "SUSPEND";
	public static final String DELAY = "DELAY";
	public static final String UNIT = "UNIT";
	public static final String AT = "AT";
	public static final String WARNING = "WARNING";
	public static final String INFO = "INFO";

	static App app;

	protected SDMSServerConnection serverConnection = null;

	public Options options;
	public int executions;
	public boolean silent = false;
	public boolean verbose = false;

	public App () { }

	public void addOption(String shortopt, String longopt, String iniopt, String key, String defaultvalue, String valuestring, boolean mandatory, String doc)
	{
		options.add (shortopt, longopt, iniopt, key, defaultvalue, valuestring, mandatory, doc);
	}

	private void addStandardOptions()
	{
		boolean uo = this.userOnly();

		addOption("h", "host",     "Host",     HOST,     null, "hostname",   true,
		          "BICsuite!server Host");
		addOption("p", "port",     "Port",     PORT,   "2506", "portnumber", false,
		          "BICsuite!server Port (defaults to 2506)");
		addOption("u", "user",     "User",     USER,     null, "username",   uo,
		          "Username (user or jid must be specified)");
		addOption("w", "pass",     "Password", PASS,     null, "password", uo,
		          "Password (must be specified if user is specified)");
		if (!uo) {
			addOption("j", "jid",      null,      JID,      null, "jobid",      false,
			          "Job Id (user or jid must be specified)");
			addOption("k", "key",      null,      KEY,      null, "jobkey",     false,
			          "Job Key (must be specified if user is not specified)");
		}
		addOption("s", "silent",   "Silent",   SILENT,   null, null	, false,
		          "[No] (error) messages, feedbacks and additional messages are printed");
		addOption("v", "verbose",  "Verbose",  VERBOSE,  null, null	, false,
		          "[No] commands are printed");
		if (this.canRetry()) {
			addOption("t", "timeout",  "Timeout", TIMEOUT,  null, "minutes",    false,
				  "Number of minutes to retry on connection problems." +
				  " 0 disables retry (default is wait forever)");
			addOption("c", "cycle",    "Cycle",   CYCLE,    "1",  "minutes",    false,
				  "Number of minutes to wait between retries." +
				  " Minimal value and default is 1 minute");
		}
		addOption("ini",   "ini",   null,		INI,      null, "inifile", false, "Use inifile for configuration of standard options");
		addOption(null,    "help",  null,		HELP,     null, null, false, "Displays this help");
		addOption(null,    "info",  "Info",		INFO,     null, "sessioninfo", false, "Additional information for identifying the session");
	}

	private boolean validateStandardOptions()
	{
		try {
			int port = Integer.parseInt (options.getValue(PORT));
		} catch (Exception e) {
			if (!silent) System.err.println("port must be an integer !");
			return false;
		}
		if (!options.isSet(USER) && !options.isSet(JID)) {
			if (!silent) System.err.println("Either user or jid option must be given !");
			return false;
		}
		if (options.isSet(USER) && !options.isSet(PASS)) {
			if (!silent) System.err.println("user option requires pass option !");
			return false;
		}
		if (options.isSet(USER) && options.isSet(KEY)) {
			if (!silent) System.err.println("user option and key option not compatible !");
			return false;
		}
		if (!options.isSet(USER) && options.isSet(JID) && !options.isSet(KEY)) {
			if (!silent) System.err.println("jid option without user option requires key option !");
			return false;
		}
		if (options.isSet(JID)) {
			try {
				long dummy = Long.parseLong (options.getValue(JID));
			} catch (Exception e) {
				if (!silent) System.err.println("jid must be a (long)integer !");
				return false;
			}
		}
		if (this.canRetry()) {
			if (options.isSet(TIMEOUT)) {
				try {
					int timeout_min = Integer.parseInt (options.getValue(TIMEOUT));
				} catch (Exception e) {
					if (!silent) System.err.println("timeout must be an integer !");
					return false;
				}
			}
			try {
				int cycle_min = Integer.parseInt (options.getValue(CYCLE));
				if (cycle_min < 1) {
					if (!silent) System.err.println("cycle must be >= 1 !");
					return false;
				}
			} catch (Exception e) {
				if (!silent) System.err.println("cycle must be an integer !");
				return false;
			}
		}
		if (silent && verbose) {
			System.err.println("silent and verbose options cannot be used together !");
			return false;
		}

		return true;
	}

	public void    addOptions()				{ }
	public int     go() throws RetryException		{ return 0; }
	public String  getName()				{ return "?"; }
	public String  getUsageArguments()			{ return ""; }
	public boolean validateOptions()			{ return true; }
	public boolean canRetry()				{ return false; }
	public boolean userOnly()				{ return false; }
	public boolean specificParse()				{ return true; }

	public void render(SDMSOutput o) throws SDMSException	{ return; }

	public boolean setupApp()				{ return true; }

	private String getUsage()
	{
		return "\nusage: " + this.getName() + " { options } " + this.getUsageArguments() + "\n\noptions are:\n" + options.list();
	}

	public boolean isRetryError(String errorcode)
	{
		if (errorcode.equals("Desktop-0001") ||
		    errorcode.equals("Desktop-0002") ||
		    errorcode.equals("Desktop-0003") ||
		    errorcode.equals("Desktop-0004") ||
		    errorcode.equals("03202081740")  ||
		    errorcode.equals("03202081739")
		   )
			return true;
		else
			return false;
	}

	private boolean connect()
		throws RetryException
	{
		int port = Integer.parseInt (options.getValue(PORT));
		if (options.isSet(USER)) {
			serverConnection = new SDMSServerConnection ( options );
		} else {
			serverConnection = new SDMSServerConnection (
				options.getValue(HOST),
				port,
				options.getValue(JID),
				options.getValue(KEY),
				0,
				false
			);
		}
		try {
			SDMSOutput o = serverConnection.connect();
			if (o.error != null) {
				if (!silent) System.err.println("Connect Error: " + o.error.code + ", " + o.error.message);
				if (isRetryError(o.error.code)) {
					throw new RetryException();
				}
				return false;
			}
			if (!silent) render(o);
		} catch (IOException e) {
			if (!silent) System.err.println("Connect Error: " + e.toString());
			throw new RetryException();
		} catch (SDMSException e) {
			if (!silent) System.err.println("Render Error: " + e.toString());
			throw new RetryException();
		}
		return true;
	}

	public  SDMSOutput execute(String cmd)
		throws RetryException
	{
		if (verbose) System.err.println("Executing command:\n" + cmd);
		SDMSOutput o = serverConnection.execute(cmd);
		if (o.error != null) {
			if (isRetryError(o.error.code)) {
				if (this.canRetry() && verbose) printError(o.error,"RETRY");
				throw new RetryException();
			}
		} else {
			if (verbose && !silent) System.err.println("Feedback: " + o.feedback);
		}
		return o;
	}

	public	void printError(SDMSOutputError error)
	{
		printError(error,"ERROR");
	}

	public	void printError(SDMSOutputError error, String label)
	{
		if (!silent) System.err.println(label + ":" + error.code+ "," + error.message);
	}

	private int doTry()
		throws RetryException
	{
		int r = 1;
		if (connect()) {
			r = this.go();
			try {
				serverConnection.finish();
			} catch (IOException ie) {}
		}
		return r;
	}

	private int reTry()
	{
		long stime_ms = 0;
		int  cycle_min = 0;
		int  timeout_min = 0;
		if (this.canRetry()) {
			if (options.isSet(TIMEOUT)) {
				stime_ms = new Date().getTime();
				try {
					timeout_min = Integer.parseInt (options.getValue(TIMEOUT));
				} catch (Exception e) {}
			}
			try {
				cycle_min = Integer.parseInt (options.getValue(CYCLE));
			} catch (Exception e) {}
		}
		executions = 0;
		boolean retry = true;
		while (retry) {
			executions ++;
			retry = false;
			try {
				return doTry();
			} catch (RetryException re) {
				if (this.canRetry()) {
					retry = true;
					if (options.isSet(TIMEOUT)) {
						long time_ms = new Date().getTime();
						if (time_ms > stime_ms + timeout_min * 60 * 1000) {
							if (!silent) System.err.println("Timeout reached !");
							retry = false;
						}
					}
					if (retry) try {
						Thread.sleep (cycle_min * 60 * 1000);
					} catch (InterruptedException ie) {}
				}
			}
		}
		return 1;
	}

	public int run (String[] argv)
	{
		String inifile;
		String[] ignoreKeys = null;
		options = new Options();
		addStandardOptions();
		addOptions();

		options.parse(argv);
		if (options.isSet(KEY)) {
			ignoreKeys = new String[2];
			ignoreKeys[0] = options.getOption(USER).iniopt;
			ignoreKeys[1] = options.getOption(PASS).iniopt;
		}
		if (options.isSet(INI)) {
			inifile = options.getValue(INI);
			options.evaluateInifile(inifile, false, ignoreKeys);
		}
		inifile = System.getenv("HOME");
		if (inifile  != null) {
			options.evaluateInifile(inifile + "/.sdmshrc", true, ignoreKeys);
		}
		inifile = System.getenv("BICSUITECONFIG");
		if (inifile != null) {
			options.evaluateInifile(inifile + "/sdmshrc", true, ignoreKeys);
		} else {
			inifile = System.getenv("BICSUITEHOME");
			if (inifile == null) {
				if (!silent) System.err.println("BICSUITEHOME is not set");
				return (1);
			}
			options.evaluateInifile(inifile + "/etc/sdmshrc", true, ignoreKeys);
		}

		silent = (options.getOption(SILENT)).bvalue;
		verbose = (options.getOption(VERBOSE)).bvalue;

		if (!specificParse()) {
			if (!silent) System.err.println(this.getUsage());
			return 1;
		}
		silent = (options.getOption(SILENT)).bvalue;
		verbose = (options.getOption(VERBOSE)).bvalue;

		if (options.isSet(HELP) && options.getOption(HELP).bvalue) {
			System.out.println(this.getUsage());
			return 0;
		}
		if (!options.validate()) {
			if (!silent) System.err.println(this.getUsage());
			return (1);
		}
		if (!validateStandardOptions()) {
			if (!silent) System.err.println(this.getUsage());
			return (1);
		}
		if (!validateOptions()) {
			if (!silent) System.err.println(this.getUsage());
			return (1);
		}
		if (!setupApp()) {
			if (!silent) System.err.println("An error occurred while initializing application");
			return (1);
		}

		int r = reTry();

		return r;
	}

	public static void main (String[] argv)
	{
		System.exit(new App().run(argv));
	}
}
