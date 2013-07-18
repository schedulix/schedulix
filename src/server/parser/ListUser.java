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


package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ListUser extends Node
{

	public final static String __version = "@(#) $Id: ListUser.java,v 2.6.2.2 2013/06/18 09:49:34 ronald Exp $";

	public ListUser()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSUser u;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("NAME");

		desc.add("IS_ENABLED");

		desc.add("DEFAULT_GROUP");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv,
		                                      "02111101418", "List of Users"), desc);

		Vector groups = new Vector();
		groups.addAll(sysEnv.cEnv.gid());
		SDMSPrivilege sysPrivs = new SDMSPrivilege();
		for(int i = 0; i < groups.size(); ++i) {
			try {
				SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(SDMSProxy.ZERO, (Long)groups.get(i)));
				sysPrivs.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
			} catch (NotFoundException nfe) {  }
		}

		Iterator i = SDMSUserTable.table.iterator(sysEnv);
		while(i.hasNext()) {
			u = (SDMSUser)(i.next());
			if(u.getDeleteVersion(sysEnv).longValue() != 0) continue;
			if(u.getName(sysEnv).equals(SDMSUser.INTERNAL)) continue;
			if(u.getName(sysEnv).equals(SDMSUser.NOBODY))	continue;

			SDMSPrivilege p = u.getPrivileges(sysEnv);
			if (!p.can(SDMSPrivilege.VIEW) && !p.can(SDMSPrivilege.MANAGE_USER) && !sysPrivs.can(SDMSPrivilege.MANAGE_GROUP)) {
				continue;
			}

			Vector v = new Vector();

			v.add(u.getId(sysEnv));
			v.add(u.getName(sysEnv));
			v.add(u.getIsEnabled(sysEnv));
			v.add(SDMSGroupTable.getObject(sysEnv, u.getDefaultGId(sysEnv)).getName(sysEnv));
			v.add(p.toString());

			d_container.addData(sysEnv, v);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(
		        new SDMSMessage(sysEnv, "02111101419", "$1 User(s) found", new Integer(d_container.lines)));
	}
}

