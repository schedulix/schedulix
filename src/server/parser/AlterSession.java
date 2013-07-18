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

public class AlterSession extends Node
{

	public final static String __version = "@(#) $Id: AlterSession.java,v 2.6.2.1 2013/03/14 10:24:23 ronald Exp $";

	private Integer sid;
	private boolean trc;
	private WithHash withs;

	public AlterSession(Integer id, WithHash wh)
	{
		super();
		sid = id;
		withs = wh;
		cmdtype = Node.ANY_COMMAND;
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
		int searchedId;

		tg = env.getMe().getThreadGroup();
		list = new SDMSThread[tg.activeCount()];
		nt = tg.enumerate(list);

		if(sid == null) sid = new Integer(env.id());
		searchedId = sid.intValue();

		for(i=0; i<nt; i++) {

			if(list[i] instanceof ListenThread) continue;

			cEnv = ((UserConnection) list[i]).getEnv();
			if(cEnv == null) continue;
			if(searchedId != list[i].id()) continue;

			if(withs.containsKey(ParseStr.S_TRACE_LEVEL)) {
				Object tmptrc = withs.get(ParseStr.S_TRACE_LEVEL);
				if (tmptrc instanceof Boolean) {
					trc = ((Boolean) tmptrc).booleanValue();
					if(trc) cEnv.trace_on();
					else	cEnv.trace_off();
				} else {
					cEnv.setTraceLevel(((Integer) tmptrc).intValue());
				}
			}
			if(withs.containsKey(ParseStr.S_PROTOCOL)) {
				cEnv.setRenderer((SDMSOutputRenderer) withs.get(ParseStr.S_PROTOCOL));
			}

			if(withs.containsKey(ParseStr.S_TIMEOUT)) {
				env.getMe().setTimeout(((Integer) withs.get(ParseStr.S_TIMEOUT)).intValue());
			}

			if (withs.containsKey(ParseStr.S_SESSION)) {
				cEnv.setInfo((String) withs.get(ParseStr.S_SESSION));
			}

			break;
		}
		result.setFeedback(new SDMSMessage(sysEnv, "03203182357", "Session altered"));
	}

}

