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

class submit extends App
{
	public static final String __version = "@(#) $Id: submit.java,v 2.7.6.1 2013/03/14 10:25:32 ronald Exp $";

	public final static String JOB = "JOB";
	public final static String TAG = "TAG";
	public final static String MASTER = "MASTER";
	public final static String SUSPEND = App.SUSPEND;
	public final static String ENABLE = "ENABLE";
	public final static String DISABLE = "DISABLE";
	public final static String DELAY = App.DELAY;
	public final static String UNIT = App.UNIT;
	public final static String AT = App.AT;
	public final static String GROUP = "GROUP";
	public final static String NICE = "NICE";

	private String submitTag;

	public submit()
	{
		super();
		this.submitTag = null;
	}

	public void addOptions()
	{
		addOption("J", "job"    , null, JOB    , null, "jobname"  , true , "Name or alias of job to submit");
		addOption("T", "tag"    , null, TAG    , null, "tag"      , false, "Child tag for dynamic child submits");
		addOption("m", "master" , null, MASTER , null, null       , false, "The job will be submitted as master (job option only)");
		addOption("N", "nice"   , null, NICE   , null, "nice"     , false, "Nice value of the job or batch");
		addOption("S", "suspend", null, SUSPEND, null, null       , false, "Submit job [not] suspended");
		addOption("D", "delay"  , null, DELAY  , null, "delay"    , false, "Delay after Job will be resumed (only valid with suspend option)");
		addOption("U", "unit"   , null, UNIT   , null, "unit"     , false, "Delay Unit (MINUTE, HOUR or DAY) defaults to MINUTE");
		addOption("A", "at"     , null, AT     , null, "at"       , false, "Timestamp (YYYY-MM-DDTHH:MM) when job should be resumed (only valid with suspend option)");
		addOption("g", "group"  , null, GROUP  , null, "groupname", false, "Group to own the submitted job (not allows with jid option)");
		addOption("d", "disable", null, DISABLE, null, null, false, "The disable option can be used in dynamic child submits and will cause the child to be submitted disabled");
	}
	public String getName()
	{
		return "submit";
	}
	public boolean canRetry()
	{
		return true;
	}
	public String getUsageArguments()
	{
		return "{ parameter value }";
	}
	public boolean validateOptions()
	{
		if (options.isSet(GROUP) && !options.isSet(App.USER)) {
			if (!silent) System.err.println("group option only allowed with user option !");
			return false;
		}
		if (options.isSet(MASTER)) {
			if (options.isSet(App.USER)) {
				if (!silent) System.err.println("WARNING: master option has no effect for submits by user");
			}
			if (options.isSet(DISABLE)) {
				if (!silent) System.err.println("disable option not allowed for master submits !");
				return false;
			}
		}
		if (options.isSet(NICE)) {
			int nicevalue;
			try {
				nicevalue = Integer.parseInt (options.getValue(NICE));
			} catch (Exception e) {
				if (!silent) System.err.println("nicevalue be an integer !");
				return false;
			}
			if (nicevalue < -100 || nicevalue > 100) {
				if (!silent) System.err.println("nicevalue must be >= -100 and <= 100 !");
				return false;
			}
		}

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

		if (options.rest.size() % 2 != 0) {
			if (!silent) System.err.println("Incomplete parameter value pairs !");
			return false;
		}
		return true;
	}
	public int go()
		throws RetryException
	{
		String cmd;
		SDMSOutput o;
		if (submitTag == null) {
			cmd = "GET SUBMITTAG";
			o = execute(cmd);
			this.submitTag = SDMSOutputUtil.getFromRecord(o,"VALUE");
		}
		cmd = "SUBMIT " + options.getValue(JOB) + "\nWITH\n    SUBMITTAG = '" + submitTag + "'";
		if (options.isSet(SUSPEND) && options.getOption(SUSPEND).getBValue()) {
			cmd = cmd + ",\n    " + "SUSPEND";
		}
		if (options.isSet(DELAY) || options.isSet(AT) ) {
			cmd = cmd + ",\n    " + "RESUME";
		}
		if (options.isSet(DELAY)) {
			cmd = cmd + " IN " + options.getValue(DELAY) + " " + options.getValue(UNIT);
		}
		if (options.isSet(AT)) {
			cmd = cmd + " AT '" + options.getValue(AT) + "'";
		}
		if (options.isSet(DISABLE) && options.getOption(DISABLE).getBValue()) {
			cmd = cmd + ",\n    " + "DISABLE";
		}
		if (options.isSet(NICE))    {
			cmd = cmd + ",\n    " + "NICEVALUE = " + options.getValue(NICE);
		}
		if (options.isSet(TAG))     {
			cmd = cmd + ",\n    " + "CHILDTAG = '" + options.getValue(TAG) + "'";
		}
		if (options.isSet(MASTER) && !options.isSet(App.USER))  {
			cmd = cmd + ",\n    " + "MASTER";
		}
		if (options.rest.size() > 0 ) {
			cmd = cmd + ",\n    " + "PARAMETERS = (\n";
			String sep = "";
			Iterator i = options.rest.iterator();
			while (i.hasNext()) {
				String parameter = (String)i.next();
				String value     = (String)i.next();
				value = value.replaceAll("\\\\", "\\\\\\\\");
				value = value.replaceAll("'", "\\\\'");
				cmd = cmd + sep + "        '" + parameter + "' = '" + value + "'";
				sep = ",\n";
			}
			cmd = cmd + "\n    )";
		}
		o = execute(cmd);
		if (o.error != null) {
			if (o.error.code.equals("03406031553")) return 0;
			printError(o.error);
			return 1;
		}
		System.out.println(SDMSOutputUtil.getFromRecord(o,"ID"));
		return 0;
	}
	public static void main (String[] argv)
	{
		System.exit(new submit().run(argv));
	}
}
