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

public class ListRsp extends Node
{

	public final static String __version = "@(#) $Id: ListRsp.java,v 2.3.8.2 2013/06/18 09:49:33 ronald Exp $";

	public ListRsp()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateProfile o;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("NAME");

		desc.add("INITIAL_STATE");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv, "List of Resource State Profiles", desc);

		Iterator i = SDMSResourceStateProfileTable.table.iterator(sysEnv);
		while(i.hasNext()) {
			Vector v = new Vector();
			o = (SDMSResourceStateProfile)(i.next());

			v.add(o.getId(sysEnv));
			v.add(o.getName(sysEnv));
			Long rsdId = o.getInitialRsdId(sysEnv);
			if (rsdId != null) {
				SDMSResourceStateDefinition rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, rsdId);
				v.add(rsd.getName(sysEnv));
			} else {
				v.add(null);
			}
			v.add(o.getPrivileges(sysEnv).toString());

			d_container.addData(sysEnv, v);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "03201101444", "$1 Resource State Profile(s) found",
		                        Integer.valueOf(d_container.lines)));

	}

}

