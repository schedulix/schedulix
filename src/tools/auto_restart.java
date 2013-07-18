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
package de.independit.scheduler.tools;

import java.lang.*;
import java.util.*;

import de.independit.scheduler.SDMSApp.*;
import de.independit.scheduler.server.output.*;

class auto_restart extends App
{
	public static final String __version = "@(#) $Id: auto_restart.java,v 2.3.6.1 2013/03/14 10:25:30 ronald Exp $";

	public final static String FAILED = "FAILED";
	public final static String DELAY  = App.DELAY;
	public final static String MAX    = "MAX";
	public final static String WARN   = App.WARNING;

	public auto_restart()
	{
		super();
	}

	public void addOptions()
	{
		addOption("f", "failed" , null, FAILED , null, "jobid"    , true , "Id of the failed job which might get restarted");
		addOption("d", "delay"  , null, DELAY  , null, "minutes"  , false, "Default number of minutes the job should be delayed if job does not define AUTORESTART_DELAY");
		addOption("m", "max"    , null, MAX    , null, "number"   , false, "Default max number of times the job should be restarted if job does not define AUTORESTART_MAX");
		addOption("W", "warn"   , null, WARN   , null, null       , false, "[Don't] Set Warning if maximum number of restarts was reached");
	}
	public String getName()
	{
		return "auto_restart";
	}
	public boolean userOnly()
	{
		return true;
	}
	public boolean canRetry()
	{
		return true;
	}

	public int go()
	throws RetryException
	{
		String failedJob = options.getValue(FAILED);

		String cmd = "GET PARAMETER OF " + failedJob + " AUTORESTART LIBERAL";
		SDMSOutput o = execute(cmd);
		if (o.error != null) {

			if (!o.error.code.equals("03209231453")) {
				System.err.println("Error executing: " + cmd);
				printError(o.error);
				return 1;
			}
		}
		boolean autorestart = false;
		if (o.error == null && SDMSOutputUtil.getFromRecord(o,"VALUE").toUpperCase().equals("TRUE")) {
			autorestart = true;
		}
		if (!autorestart) return 0;

		int max_restarts = -1;
		if (options.isSet(MAX)) max_restarts = Integer.parseInt(options.getValue(MAX));
		cmd = "GET PARAMETER OF " + failedJob + " AUTORESTART_MAX LIBERAL";
		o = execute(cmd);
		if (o.error != null) {

			if (!o.error.code.equals("03209231453")) {
				System.err.println("Error executing: " + cmd);
				printError(o.error);
				return 1;
			}
		}
		if (o.error == null) {
			max_restarts = Integer.parseInt(SDMSOutputUtil.getFromRecord(o,"VALUE"));
		}
		if (max_restarts != -1) {

			cmd = "GET PARAMETER OF " + failedJob + " AUTORESTART_COUNT LIBERAL";
			o = execute(cmd);
			if (o.error != null) {

				if (!o.error.code.equals("03209231453")) {
					System.err.println("Error executing: " + cmd);
					printError(o.error);
					return 1;
				}
			}
			int restarts = 0;
			if (o.error == null) {
				restarts = Integer.parseInt(SDMSOutputUtil.getFromRecord(o,"VALUE"));
			}
			if (restarts >= max_restarts) {
				if (restarts == max_restarts) {
					if (options.isSet(WARN) && options.getOption(WARN).getBValue()) {
						cmd = "ALTER JOB " + failedJob + " WITH WARNING = 'Maximum number of automatic restarts reached'";
						o = execute(cmd);
						if (o.error != null) {
							System.err.println("Error executing: " + cmd);
							printError(o.error);
							return 1;
						}

						cmd = "SET PARAMETER OF " + failedJob + " AUTORESTART_COUNT = '" + Integer.toString(restarts + 1) + "'";
						o = execute(cmd);
						if (o.error != null) {
							System.err.println("Error executing: " + cmd);
							printError(o.error);
							return 1;
						}
					}
				}
				return 0;
			}
			restarts = restarts + 1;
			cmd = "SET PARAMETER OF " + failedJob + " AUTORESTART_COUNT = '" + Integer.toString(restarts) + "'";
			o = execute(cmd);
			if (o.error != null) {
				System.err.println("Error executing: " + cmd);
				printError(o.error);
				return 1;
			}
		}

		int delay = 0;
		if (options.isSet(DELAY)) delay = Integer.parseInt(options.getValue(DELAY));
		cmd = "GET PARAMETER OF " + failedJob + " AUTORESTART_DELAY LIBERAL";
		o = execute(cmd);
		if (o.error != null) {

			if (!o.error.code.equals("03209231453")) {
				System.err.println("Error executing: " + cmd);
				printError(o.error);
				return 1;
			}
		}
		if (o.error == null) {
			delay = Integer.parseInt(SDMSOutputUtil.getFromRecord(o,"VALUE"));
		}

		cmd = "ALTER JOB " + failedJob + " WITH RERUN, COMMENT = 'auto_restart'";
		if (delay > 0) cmd = cmd + ", SUSPEND, RESUME IN " + Integer.toString(delay) + " MINUTE";
		o = execute(cmd);
		if (o.error != null) {
			System.err.println("Error executing: " + cmd);
			printError(o.error);
			return 1;
		}
		return 0;
	}

	public static void main (String[] argv)
	{
		System.exit(new auto_restart().run(argv));
	}
}
