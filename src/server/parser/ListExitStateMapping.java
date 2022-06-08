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

public class ListExitStateMapping extends Node
{

	public final static String __version = "@(#) $Id: ListExitStateMapping.java,v 2.2.8.1 2013/03/14 10:24:36 ronald Exp $";

	public ListExitStateMapping()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateMappingProfile o;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("NAME");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage (sysEnv, "03201292004", "List of Exit State Mappings"), desc);

		Iterator i = SDMSExitStateMappingProfileTable.table.iterator(sysEnv);
		while(i.hasNext()) {
			Vector v = new Vector();
			o = (SDMSExitStateMappingProfile)(i.next());

			v.add(o.getId(sysEnv));
			v.add(o.getName(sysEnv));
			v.add(o.getPrivileges(sysEnv).toString());

			d_container.addData(sysEnv, v);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "03111272344", "$1 Exit State Mapping(s) found",
		                        Integer.valueOf(d_container.lines)));
	}
}

