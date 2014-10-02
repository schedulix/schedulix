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

class get_variable extends App
{
	public static final String __version = "@(#) $Id: get_variable.java,v 2.3.6.1 2013/03/14 10:25:31 ronald Exp $";

	public final static String JID = App.JID;
	public final static String NAME = "NAME";
	public final static String MODE = "MODE";

	public void addOptions()
	{
		addOption("j", "jid" , null, JID , null, "jobid"       , true , "Id of job to get variable from");
		addOption("n", "name", null, NAME, null, "variablename", true , "Name of variable to get");
		addOption("m", "mode", null, MODE, null, "mode"        , false, "Override retrieve mode (strict, warn or liberal)");
	}
	public String getName()
	{
		return "get_variable";
	}
	public boolean canRetry()
	{
		return true;
	}
	public boolean validateOptions()
	{
		if (options.isSet(MODE)) {
			String mode = options.getValue(MODE);
			if (!mode.equals("strict") && !mode.equals("warn") && !mode.equals("liberal")) {
				if (!silent) System.err.println("Invalid mode option");
				return false;
			}
		}
		return true;
	}
	public int go()
		throws RetryException
	{
		String cmd = "GET PARAMETER ";
		if (options.isSet(App.USER)) cmd = cmd + "OF " + options.getValue(JID);
		cmd = cmd + " '" + options.getValue(NAME) + "'";
		if (options.isSet(MODE)) cmd = cmd + " " + options.getValue(MODE).toUpperCase();
		SDMSOutput o = execute(cmd);
		if (o.error != null) {
			printError(o.error);
			return 1;
		}
		System.out.println(SDMSOutputUtil.getFromRecord(o,"VALUE"));
		return 0;
	}
	public static void main (String[] argv)
	{
		System.exit(new get_variable().run(argv));
	}
}
