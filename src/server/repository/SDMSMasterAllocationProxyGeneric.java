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

public class SDMSMasterAllocationProxyGeneric extends SDMSProxy
{

	public static final int N = Lockmode.N;
	public static final int X = Lockmode.X;
	public static final int SX = Lockmode.SX;
	public static final int S = Lockmode.S;
	public static final int SC = Lockmode.SC;
	public final static long privilegeMask = SDMSPrivilege.ALL;

	static final public int stickyName_size = 64;

	protected SDMSMasterAllocationProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getRaId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getRaId (env));
	}

	public void setRaId (SystemEnvironment env, Long p_raId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setRaId (env, p_raId);
		return ;
	}
	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getAmount (env));
	}

	public void setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setAmount (env, p_amount);
		return ;
	}
	public String getStickyName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getStickyName (env));
	}

	public void setStickyName (SystemEnvironment env, String p_stickyName)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setStickyName (env, p_stickyName);
		return ;
	}
	public Long getStickyParent (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getStickyParent (env));
	}

	public void setStickyParent (SystemEnvironment env, Long p_stickyParent)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setStickyParent (env, p_stickyParent);
		return ;
	}
	public Integer getLockmode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getLockmode (env));
	}

	public String getLockmodeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSMasterAllocationGeneric) object).getLockmodeAsString (env);
	}

	public void setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setLockmode (env, p_lockmode);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSMasterAllocation setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSMasterAllocation)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSMasterAllocationGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSMasterAllocation setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSMasterAllocationGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSMasterAllocation)this;
	}
	public SDMSMasterAllocation set_RaIdStickyParentStickyName (SystemEnvironment env, Long p_raId, Long p_stickyParent, String p_stickyName)
	throws SDMSException
	{
		checkRead(env);
		((SDMSMasterAllocationGeneric)(object)).set_RaIdStickyParentStickyName (env, p_raId, p_stickyParent, p_stickyName);
		return (SDMSMasterAllocation)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing MasterAllocation $1", getId(sysEnv));
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
		((SDMSMasterAllocationGeneric) object).print();
	}
}
