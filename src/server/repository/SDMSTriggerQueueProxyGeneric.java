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

package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSTriggerQueueProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.ALL;

	protected SDMSTriggerQueueProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public Long getTrId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getTrId (env));
	}

	public void setTrId (SystemEnvironment env, Long p_trId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setTrId (env, p_trId);
		return ;
	}
	public Long getNextTriggerTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getNextTriggerTime (env));
	}

	public void setNextTriggerTime (SystemEnvironment env, Long p_nextTriggerTime)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setNextTriggerTime (env, p_nextTriggerTime);
		return ;
	}
	public Integer getTimesChecked (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getTimesChecked (env));
	}

	public void setTimesChecked (SystemEnvironment env, Integer p_timesChecked)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setTimesChecked (env, p_timesChecked);
		return ;
	}
	public Integer getTimesTriggered (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getTimesTriggered (env));
	}

	public void setTimesTriggered (SystemEnvironment env, Integer p_timesTriggered)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setTimesTriggered (env, p_timesTriggered);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSTriggerQueue setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSTriggerQueue)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerQueueGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSTriggerQueue setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSTriggerQueueGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSTriggerQueue)this;
	}
	public SDMSTriggerQueue set_SmeIdTrId (SystemEnvironment env, Long p_smeId, Long p_trId)
	throws SDMSException
	{
		checkRead(env);
		((SDMSTriggerQueueGeneric)(object)).set_SmeIdTrId (env, p_smeId, p_trId);
		return (SDMSTriggerQueue)this;
	}

	public SDMSKey getSortKey(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSKey s = null;
		Long myId = getId(sysEnv);
		if (sysEnv.tx.sortKeyMap == null)
			sysEnv.tx.sortKeyMap = new HashMap();
		else
			s = (SDMSKey) sysEnv.tx.sortKeyMap.get(myId);
		if (s != null) return s;
		boolean gotIt = false;
		s = new SDMSKey();

		s.add(getSmeId(sysEnv));

		gotIt = false;
		Long trId = getTrId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSTriggerTable.getObject(sysEnv, trId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		sysEnv.tx.sortKeyMap.put(myId, s);
		return s;
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null) & p) == p;
	}

	public long getPrivilegeMask()
	{
		return privilegeMask;
	}

	public final SDMSPrivilege getPrivileges(SystemEnvironment env)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, null));
	}

	public final SDMSPrivilege getPrivilegesForGroups(SystemEnvironment env, Vector checkGroups)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, checkGroups));
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		Vector groups;
		if (checkGroups == null) groups = new Vector();
		else groups = checkGroups;

		long p = 0;
		p = checkPrivs;
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing TriggerQueue $1", getId(sysEnv));
		return m;
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.euid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSTriggerQueueGeneric) object).print();
	}
}
