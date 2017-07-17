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

public class SDMSVersionedExtentsProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.ALL;

	static final public int extent_size = 256;

	protected SDMSVersionedExtentsProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSVersionedExtents getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSVersionedExtentsTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSVersionedExtents (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSVersionedExtents)p;
	}

	public Long getOId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSVersionedExtentsGeneric)(object)).getOId (env));
	}

	public void setOId (SystemEnvironment env, Long p_oId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setOId (env, p_oId);
		return ;
	}
	public Integer getSequence (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSVersionedExtentsGeneric)(object)).getSequence (env));
	}

	public void setSequence (SystemEnvironment env, Integer p_sequence)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setSequence (env, p_sequence);
		return ;
	}
	public String getExtent (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSVersionedExtentsGeneric)(object)).getExtent (env));
	}

	public void setExtent (SystemEnvironment env, String p_extent)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setExtent (env, p_extent);
		return ;
	}
	public static int getExtentMaxLength ()
	{
		return (256);
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSVersionedExtentsGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSVersionedExtentsGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSVersionedExtentsGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSVersionedExtents setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSVersionedExtents)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSVersionedExtentsGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSVersionedExtents setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSVersionedExtentsGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSVersionedExtents)this;
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null) & p) == p;
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p, long version)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null, version) & p) == p;
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

	public final SDMSPrivilege getPrivileges(SystemEnvironment env, long version)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, null, version));
	}

	public final SDMSPrivilege getPrivilegesForGroups(SystemEnvironment env, Vector checkGroups, long version)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, checkGroups, version));
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups, long version)
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Versioned Extents $1", getId(sysEnv));
		return m;
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.uid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSVersionedExtentsGeneric) object).print();
	}
}
