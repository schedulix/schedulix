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

public class SDMSRunnableQueueProxyGeneric extends SDMSProxy
{

	public final static String __version = "SDMSRunnableQueueProxyGeneric $Revision: 2.3 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public static final int DEPENDENCY_WAIT = SDMSSubmittedEntity.DEPENDENCY_WAIT;
	public static final int SYNCHRONIZE_WAIT = SDMSSubmittedEntity.SYNCHRONIZE_WAIT;
	public static final int RESOURCE_WAIT = SDMSSubmittedEntity.RESOURCE_WAIT;
	public static final int RUNNABLE = SDMSSubmittedEntity.RUNNABLE;
	public static final int STARTING = SDMSSubmittedEntity.STARTING;
	public final static long privilegeMask = SDMSPrivilege.ALL;

	protected SDMSRunnableQueueProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSRunnableQueueGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSRunnableQueueGeneric)(object)).getScopeId (env));
	}

	public void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setScopeId (env, p_scopeId);
		return ;
	}
	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSRunnableQueueGeneric)(object)).getState (env));
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSRunnableQueueGeneric) object).getStateAsString (env);
	}

	public void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setState (env, p_state);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSRunnableQueueGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSRunnableQueueGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSRunnableQueueGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSRunnableQueue setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSRunnableQueue)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSRunnableQueueGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSRunnableQueue setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSRunnableQueueGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSRunnableQueue)this;
	}
	public SDMSRunnableQueue set_SmeIdScopeId (SystemEnvironment env, Long p_smeId, Long p_scopeId)
	throws SDMSException
	{
		checkRead(env);
		((SDMSRunnableQueueGeneric)(object)).set_SmeIdScopeId (env, p_smeId, p_scopeId);
		return (SDMSRunnableQueue)this;
	}

	public SDMSRunnableQueue set_ScopeIdState (SystemEnvironment env, Long p_scopeId, Integer p_state)
	throws SDMSException
	{
		checkRead(env);
		((SDMSRunnableQueueGeneric)(object)).set_ScopeIdState (env, p_scopeId, p_state);
		return (SDMSRunnableQueue)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing RunnableQueue $1", getId(sysEnv));
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
		((SDMSRunnableQueueGeneric) object).print();
	}
}
