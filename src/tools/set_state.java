/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

class set_state extends App
{
	public static final String __version = "@(#) $Id: set_state.java,v 2.5.6.1 2013/03/14 10:25:31 ronald Exp $";

	public final static String JID = App.JID;
	public final static String STATE = "STATE";
	public final static String FORCE = "FORCE";

	public void addOptions()
	{
		addOption("j", "jid"  , null, JID  , null, "jobid", true , "Id of job to set state for");
		addOption("S", "state", null, STATE, null, "state", true , "Exit State to set");
		addOption("f", "force", null, FORCE, null, null   , false, "[Don't] Force if job does not define a mapping for that state");
	}
	public String getName()
	{
		return "set_state";
	}
	public boolean canRetry()
	{
		return true;
	}
	public int go()
	throws RetryException
	{
		String cmd = "ALTER JOB";
		if (options.isSet(App.USER)) cmd = cmd + " " + options.getValue(JID);
		cmd = cmd + " WITH EXIT STATE = " + options.getValue(STATE);
		if (options.isSet(FORCE) && options.getOption(FORCE).getBValue()) cmd = cmd + " FORCE";
		SDMSOutput o = execute(cmd);
		if (o.error != null) {

			if (o.error.code.equals("03207082043") && executions > 1) return 0;
			printError(o.error);
			return 1;
		} else return 0;
	}
	public static void main (String[] argv)
	{
		System.exit(new set_state().run(argv));
	}
}
