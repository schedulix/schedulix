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

	private final static Long zero = new Long(0);
	private Integer sid;
	private String userName;
	private String baseUser;
	private boolean trc;
	private WithHash withs;
	boolean resetUser = false;

	public AlterSession(Integer id, WithHash wh)
	{
		super();
		sid = id;
		withs = wh;
		userName = null;
		baseUser = null;
		cmdtype = Node.ANY_COMMAND;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public AlterSession(String userName, WithHash wh)
	{
		super();
		sid = null;
		withs = wh;
		this.userName = userName;
		this.baseUser = null;
		cmdtype = Node.USER_COMMAND;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public AlterSession(String userName, String baseUser, WithHash wh)
	{
		super();
		sid = null;
		withs = wh;
		this.userName = userName;
		this.baseUser = baseUser;
		cmdtype = Node.USER_COMMAND;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public AlterSession()
	{
		super();
		sid = null;
		this.userName = null;
		resetUser = true;
		withs = null;
		cmdtype = Node.USER_COMMAND;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	private boolean setUser(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSUser c = null;
		SDMSUser b = null;
		Long bId;
		Long cId;
		Long aId;

		try {
			c = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(userName, zero));
			if (!c.getIsEnabled(sysEnv).booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03707161120", "User disabled"));
			}
			if (baseUser != null) {
				b = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(baseUser, zero));
				if (!b.getIsEnabled(sysEnv).booleanValue()) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03708010942", "User disabled"));
				}
			}
		} catch (NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03707161121", "User not found"));
		}

		if (userName.equals(baseUser) || (baseUser == null)) {
			b = c;
			baseUser = userName;
		}

		boolean aIsAdmin = sysEnv.cEnv.gid().contains(SDMSObject.adminGId);
		boolean bIsAdmin = false;
		aId = new Long(sysEnv.cEnv.uid());
		bId = b.getId(sysEnv);
		cId = c.getId(sysEnv);

		if (SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, bId)))
			bIsAdmin = true;

		if (!bId.equals(cId) && !(SDMSUserEquivTable.idx_uId_altUId.containsKey(sysEnv, new SDMSKey(bId, cId)) || bIsAdmin)) {
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03708011016", "Insufficient privileges"));
		}

		if (!aIsAdmin) {
			if (!bId.equals(aId)) {
				if (!bId.equals(cId)) {
					throw new AccessViolationException(new SDMSMessage(sysEnv, "03707071405", "Insufficient privileges"));
				} else {
					if (!SDMSUserEquivTable.idx_uId_altUId.containsKey(sysEnv, new SDMSKey(aId, cId)))
						throw new AccessViolationException(new SDMSMessage(sysEnv, "03708011035", "Insufficient privileges"));
				}
		}
		}

		sysEnv.cEnv.setConnectedUser(sysEnv, cId, SDMSMemberTable.idx_uId.getVector(sysEnv, cId));
		return true;
	}

	private boolean resetUser(SystemEnvironment sysEnv)
	{
		return sysEnv.cEnv.resetConnectedUser();
	}

	private void alterSession(SystemEnvironment sysEnv)
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
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		if (userName != null) {
			sysEnv.cEnv.resetConnectedUser();
			if (setUser(sysEnv)) {
				alterSession(sysEnv);
				result.setFeedback(new SDMSMessage(sysEnv, "03203182358", "Session altered"));
			} else {
				result.setFeedback(new SDMSMessage(sysEnv, "03707141429", "Session unchanged"));
			}
		} else if (resetUser) {
			if (resetUser(sysEnv))
				result.setFeedback(new SDMSMessage(sysEnv, "03203182359", "Session altered"));
			else
				result.setFeedback(new SDMSMessage(sysEnv, "03707141428", "Session unchanged"));
		} else {
			alterSession(sysEnv);
		result.setFeedback(new SDMSMessage(sysEnv, "03203182357", "Session altered"));
		}
	}

}

