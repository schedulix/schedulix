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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ListSession extends Node
{

	public final static String __version = "@(#) $Id: ListSession.java,v 2.12.6.2 2013/06/18 09:49:34 ronald Exp $";

	private boolean fullView;

	public ListSession()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		ThreadGroup tg;
		SDMSThread[]    list;
		int i, nt, sessionCtr;
		ConnectionEnvironment cEnv;
		SDMSOutputContainer d_container = null;

		if(sysEnv.cEnv.gid().contains(SDMSObject.adminGId))	fullView = true;
		else {
			Iterator gi = sysEnv.cEnv.gid().iterator();
			SDMSPrivilege p = new SDMSPrivilege();
			while (gi.hasNext()) {
				Long gId = (Long) gi.next();
				try {
					SDMSGrant g = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(SDMSProxy.ZERO, gId));
					p.addPriv(sysEnv, g.getPrivs(sysEnv).longValue());
				} catch (NotFoundException nfe) {

				}
			}
			if (!p.can(SDMSPrivilege.MANAGE_SYS))
				fullView = false;
			else
				fullView = true;
		}

		Vector desc = new Vector();

		desc.add("THIS");

		desc.add("SESSIONID");

		desc.add("PORT");

		desc.add("START");

		desc.add("TYPE");

		desc.add("USER");

		desc.add("UID");

		desc.add("IP");

		desc.add("TXID");

		desc.add("IDLE");

		desc.add("STATE");

		desc.add("TIMEOUT");

		desc.add("INFORMATION");

		desc.add("STATEMENT");

		tg = env.getMe().getThreadGroup();
		list = new SDMSThread[tg.activeCount()];
		nt = tg.enumerate(list);

		d_container = new SDMSOutputContainer(sysEnv, "List of Sessions", desc);
		sessionCtr = 0;
		for(i=0; i<nt; i++) {
			if(list[i] instanceof ListenThread) continue;
			if(!list[i].isAlive()) continue;
			cEnv = ((UserConnection) list[i]).getEnv();
			Vector data = new Vector();
			fillVector(sysEnv, cEnv, data);
			d_container.addData(sysEnv, data);
			sessionCtr++;
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setFeedback(new SDMSMessage (sysEnv, "03112181812", "$1 Session(s) found", new Integer(sessionCtr)));
		result.setOutputContainer(d_container);
	}

	private void fillVector(SystemEnvironment sysEnv, ConnectionEnvironment cEnv, Vector data)
		throws SDMSException
	{
		if(cEnv.id() == env.id()) {
			data.add(" * ");
		} else {
			data.add(" ");
		}
		data.add(new Integer(cEnv.id()));
		data.add(new Integer(cEnv.port()));
		data.add(cEnv.dStart());
		try {
			if(cEnv.isUser()) {
				data.add("USER");
				data.add(SDMSUserTable.getObject(sysEnv, cEnv.uid()).getName(sysEnv));
			} else if(cEnv.isJobServer()) {
				data.add("JOBSERVER");
				data.add(SDMSScopeTable.getObject(sysEnv, cEnv.uid()).pathString(sysEnv));
			} else {
				data.add("JOB");
				SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, cEnv.uid());
				long actVersion = sme.getSeVersion(sysEnv).longValue();
				data.add(SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getName(sysEnv));
			}
			data.add(cEnv.uid());
			data.add(cEnv.ip());
			data.add(new Long(cEnv.txId()));
			data.add(new Long(cEnv.idle()));
			data.add(cEnv.getState());
			data.add(new Integer(cEnv.getMe().getTimeout()));
			data.add(cEnv.getInfo());
			try {
				if(fullView)	data.add(new String(cEnv.actstmt == null ? "" : cEnv.actstmt));
				else		data.add("");
			} catch (Exception e) {

				data.add("");
			}
		} catch (NotFoundException nfe) {
			for(int i = data.size(); i < 14; i++)
				data.add(null);
		}
	}
}

