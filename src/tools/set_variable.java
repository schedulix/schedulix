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

class set_variable extends App
{
	public static final String __version = "@(#) $Id: set_variable.java,v 1.7.6.1 2013/03/14 10:25:31 ronald Exp $";

	public final static String JID = App.JID;
	public final static String CASE = "CASE";

	public void addOptions()
	{
		addOption("j", "jid",  null, JID,  null, "jobid", true,  "Id of job to set variable for");
		addOption("C", "case", null, CASE, null, null,    false, "[Don't] Treat variable names case sensitive");
	}
	public String getUsageArguments() { return "variable value { variable value }"; }
	public String getName() { return "set_variable"; }
	public boolean canRetry() { return true; }
	public boolean validateOptions()
	{
		if (options.rest.size() == 0) {
			if (!silent) System.err.println("Need at least one variable value pair !");
			return false;
		}
		if (options.rest.size() % 2 != 0) {
			if (!silent) System.err.println("Incomplete variable value pairs !");
			return false;
		}
		return true;
	}
	public int go()
		throws RetryException
	{
		final String quote;
		String cmd = "SET PARAMETER ";
		if (options.isSet(App.USER)) cmd = cmd + "ON " + options.getValue(JID);
		String sep = "";
		if (options.isSet(CASE) && options.getOption(CASE).getBValue())	quote = "'";
		else								quote = "";
		Iterator i = options.rest.iterator();
		while (i.hasNext()) {
			String variable = (String)i.next();
			String value    = (String)i.next();
			value = value.replaceAll("\\\\", "\\\\\\\\");
			value = value.replaceAll("'", "\\\\'");
			cmd = cmd + sep + " " + quote + variable + quote + " = '" + value + "'";
			sep = ",";
		}
		SDMSOutput o = execute(cmd);
		if (o.error != null) {
			printError(o.error);
			return 1;
		} else return 0;
	}
	public static void main (String[] argv) { System.exit(new set_variable().run(argv)); }
}
