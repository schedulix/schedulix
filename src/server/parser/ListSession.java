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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.util.*;

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
		boolean isInternal;
		boolean isSuspended = false;

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
		desc.add("WAIT");

		tg = env.getMe().getThreadGroup();
		list = new SDMSThread[tg.activeCount() + 5];
		nt = tg.enumerate(list);
		list[nt] = SystemEnvironment.sched;
		nt++;
		list[nt] = SystemEnvironment.tt;
		nt++;
		list[nt] = SystemEnvironment.garb;
		nt++;
		list[nt] = SystemEnvironment.timer;
		nt++;

		d_container = new SDMSOutputContainer(sysEnv, "List of Sessions", desc);
		sessionCtr = 0;
		for(i=0; i<nt; i++) {
			if (list[i] instanceof ListenThread) continue;
			if (!list[i].isAlive()) continue;
			if (list[i] instanceof UserConnection) {
				cEnv = ((UserConnection) list[i]).getEnv();
				isInternal = false;
			} else {
				InternalSession is = (InternalSession) list[i];
				cEnv = is.getEnv();
				isInternal = true;
				isSuspended = is.isSuspended();
			}
			Vector data = new Vector();
			if (fillVector(sysEnv, cEnv, data, isInternal, isSuspended)) {
				d_container.addData(sysEnv, data);
				sessionCtr++;
			}
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setFeedback(new SDMSMessage (sysEnv, "03112181812", "$1 Session(s) found", new Integer(sessionCtr)));
		result.setOutputContainer(d_container);
	}

	private boolean fillVector(SystemEnvironment sysEnv, ConnectionEnvironment cEnv, Vector data, boolean isInternal, boolean isSuspended)
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
				try {
					String userName = SDMSUserTable.getObject(sysEnv, cEnv.uid()).getName(sysEnv);
					data.add("USER");
					if (isInternal) {
						String s = cEnv.getMe().getClass().getCanonicalName();
						data.add(s.substring(s.lastIndexOf('.')+1) + (isSuspended ? "[S]" : ""));
					} else
						data.add(userName);
				} catch (NotFoundException nfe) {
					String jsName = SDMSScopeTable.getObject(sysEnv, cEnv.uid()).pathString(sysEnv);
					data.add("JOBSERVER");
					data.add(jsName);
				}
			} else if(cEnv.isJobServer()) {
				String jsName = SDMSScopeTable.getObject(sysEnv, cEnv.uid()).pathString(sysEnv);
				data.add("JOBSERVER");
				data.add(jsName);
			} else {
				SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, cEnv.uid());
				long actVersion = sme.getSeVersion(sysEnv).longValue();
				String seName = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getName(sysEnv);
				data.add("JOB");
				data.add(seName);
			}
			data.add(cEnv.uid());
			data.add(cEnv.ip());
			data.add(new Long(cEnv.txId()));
			data.add(new Long(cEnv.idle()));
			String state = (cEnv.getState()).toString();
			String waitInfo = "";
			if (sysEnv.maxWriter > 1 && cEnv.worker != null) {
				waitInfo = LockingSystemSynchronized.waitInfo(cEnv.worker);
				if (!waitInfo.equals("")) {
					state = state + "[W]";
				}
			}
			data.add(state);
			data.add(new Integer(cEnv.getMe().getTimeout()));
			data.add(cEnv.getInfo());
			try {
				if(fullView) {
					if (cEnv.firstToken != null)
						data.add(cEnv.firstToken);
					else
						data.add(new String(cEnv.actstmt == null ? "" : cEnv.actstmt));
				} else		data.add("");
			} catch (Exception e) {

				data.add("");
			}
			data.add(waitInfo);
			return true;
		} catch (NotFoundException nfe) {
			return false;
		}
	}
}

