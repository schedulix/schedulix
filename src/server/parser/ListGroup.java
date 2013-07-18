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

public class ListGroup extends Node
{

	public final static String __version = "@(#) $Id: ListGroup.java,v 2.4.4.1 2013/03/14 10:24:37 ronald Exp $";

	public ListGroup()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSGroup g;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("NAME");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03312091454", "List of Groups"), desc);

		Iterator i = SDMSGroupTable.table.iterator(sysEnv);
		while(i.hasNext()) {
			g = (SDMSGroup)(i.next());
			if(g.getDeleteVersion(sysEnv).longValue() != 0) continue;
			Long gId = g.getId(sysEnv);
			SDMSPrivilege p = g.getPrivileges(sysEnv);

			if (!p.can(SDMSPrivilege.VIEW) && !p.can(SDMSPrivilege.MANAGE_GROUP)) {

				if(!sysEnv.cEnv.gid().contains(gId))
					continue;
			}

			Vector v = new Vector();

			v.add(gId);
			v.add(g.getName(sysEnv));
			v.add(g.getPrivileges(sysEnv).toString());

			d_container.addData(sysEnv, v);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(
		        new SDMSMessage(sysEnv, "03312091455", "$1 Group(s) found", new Integer(d_container.lines)));
	}
}

