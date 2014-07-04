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
import de.independit.scheduler.server.exception.*;

class rerun extends App
{
	public static final String __version = "@(#) $Id: rerun.java,v 1.5.6.1 2013/03/14 10:25:31 ronald Exp $";

	public final static String JID = App.JID;
	public final static String SUSPEND = App.SUSPEND;
	public final static String DELAY = App.DELAY;
	public final static String UNIT = App.UNIT;
	public final static String AT = App.AT;

	public void addOptions()
	{
		addOption("j", "jid"    , null, JID    , null, "jobid", true , "Id of job to rerun");
		addOption("S", "suspend", null, SUSPEND, null, null   , false, "Submit job [not] suspended");
		addOption("D", "delay"  , null, DELAY  , null, "delay", false, "Delay after Job will be resumed (only valid with suspend option)");
		addOption("U", "unit"   , null, UNIT   , null, "unit" , false, "Delay Unit (MINUTE, HOUR or DAY) defaults to MINUTE");
		addOption("A", "at"     , null, AT     , null, "at"   , false, "Timestamp (YYYY-MM-DDTHH:MM) when job should be resumed (only valid with suspend option)");

	}
	public String getName() { return "rerun"; }
	public boolean userOnly() { return true; }
	public boolean canRetry() { return true; }
	public boolean validateOptions()
	{
		if (!options.isSet(SUSPEND) || !options.getOption(SUSPEND).getBValue()) {
			if (options.isSet(DELAY)) {
				System.err.println("delay option only valid with suspend option");
				return false;
			}
			if (options.isSet(AT)) {
				System.err.println("at option only valid with suspend option");
				return false;
			}
		} else {
			if (options.isSet(DELAY) && options.isSet(AT)) {
				System.err.println("combination of delay and at option not allowed");
				return false;
			}
			if (options.isSet(DELAY) && !options.isSet(UNIT)) {
				System.err.println("delay option requires unit option");
				return false;
			}
			if (options.isSet(UNIT) &&
			    !options.getValue(UNIT).equals("MINUTE") &&
			    !options.getValue(UNIT).equals("HOUR") &&
			    !options.getValue(UNIT).equals("DAY")) {
				System.err.println("unit must be MINUTE, HOUR or DAY");
				return false;
			}

		}
		if (options.isSet(UNIT) && !options.isSet(DELAY)) {
			System.err.println("unit option only valid with delay option");
			return false;
		}
		return true;
	}

	public int go()
		throws RetryException
	{
		String cmd = "ALTER JOB " + options.getValue(JID) + " WITH RERUN";
		if (options.isSet(SUSPEND) && options.getOption(SUSPEND).getBValue()) { cmd = cmd + ",\n    " + "SUSPEND"; }
		if (options.isSet(DELAY) || options.isSet(AT) ) { cmd = cmd + ",\n    " + "RESUME"; }
		if (options.isSet(DELAY)) { cmd = cmd + " IN " + options.getValue(DELAY) + " " + options.getValue(UNIT); }
		if (options.isSet(AT)) { cmd = cmd + " AT '" + options.getValue(AT) + "'"; }

		SDMSOutput o = execute(cmd);
		if (o.error != null) {

			if (o.error.code.equals("03205191052") && executions > 1) return 0;
			printError(o.error);
			return 1;
		} else return 0;
	}
	public static void main (String[] argv) { System.exit(new rerun().run(argv)); }
}
