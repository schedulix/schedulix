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

public class ShowSession extends Node
{

	public final static String __version = "@(#) $Id: ShowSession.java,v 2.7.8.2 2013/06/18 09:49:38 ronald Exp $";

	private Integer sid;

	public ShowSession(Integer id)
	{
		super();
		sid = id;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		ThreadGroup tg;
		SDMSThread[]    list;
		int i, nt;
		ConnectionEnvironment cEnv;
		SDMSOutputContainer d_container = null;

		Vector desc = new Vector();
		Vector t_desc = new Vector();

		desc.add("THIS");

		desc.add("SESSIONID");

		desc.add("START");

		desc.add("USER");

		desc.add("UID");

		desc.add("IP");

		desc.add("TXID");

		desc.add("IDLE");

		desc.add("TIMEOUT");

		desc.add("STATEMENT");

		tg = env.getMe().getThreadGroup();
		list = new SDMSThread[tg.activeCount()];
		nt = tg.enumerate(list);

		if(sid == null) sid = new Integer(env.id());

		Vector data = new Vector();
		for(i=0; i<nt; i++) {

			if(list[i] instanceof ListenThread) continue;

			cEnv = ((UserConnection) list[i]).getEnv();
			if(sid.intValue() != list[i].id()) continue;

			fillVector(sysEnv, cEnv, data);

			d_container = new SDMSOutputContainer(sysEnv, "Session", desc, data);
			break;
		}
		result.setFeedback(new SDMSMessage(sysEnv, "03112182120", "Session shown"));
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
		data.add(cEnv.dStart());
		if(cEnv.isJobServer()) {
			data.add(SDMSScopeTable.getObject(sysEnv, (Long) cEnv.uid()).pathString(sysEnv));
		} else {
			data.add(SDMSUserTable.getObject(sysEnv, (Long) cEnv.uid()).getName(sysEnv));
		}
		data.add(cEnv.uid());
		data.add(cEnv.ip());
		data.add(new Long(cEnv.txId()));
		data.add(new Long(cEnv.idle()));
		data.add(new Long(cEnv.timeout()));
		data.add(new String(cEnv.actstmt == null ? "" : cEnv.actstmt));
	}
}

