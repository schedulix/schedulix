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


package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ShowObjectMonitor extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowObjectMonitor.java,v 2.4.4.5 2013/09/13 08:58:44 dieter Exp $";

	ObjectURL url;

	public ShowObjectMonitor(ObjectURL url)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		this.url = url;
		cmdtype |= Node.JOB_COMMAND;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_OBJECT_MONITOR);
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");

		desc.add("NAME");

		desc.add("OWNER");

		desc.add("WATCH_TYPE");

		desc.add("RECREATE");

		desc.add("WATCHER");

		desc.add("DELETE_AMOUNT");

		desc.add("DELETE_BASE");

		desc.add("EVENT_DELETE_AMOUNT");

		desc.add("EVENT_DELETE_BASE");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("PARAMETERS");

		desc.add("INSTANCES");

		Vector data = new Vector();

		d_container = new SDMSOutputContainer(sysEnv,
					new SDMSMessage(sysEnv, "02108251021", "Object Type"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "02108251022", "Object Type shown"));

	}

}

