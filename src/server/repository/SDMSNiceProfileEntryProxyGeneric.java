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

public class SDMSNiceProfileEntryProxyGeneric extends SDMSProxy
{

	public static final int NOSUSPEND = 0;
	public static final int SUSPEND = 1;
	public static final int ADMINSUSPEND = 2;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	private static SDMSTable masterTables[] = null;

	protected SDMSNiceProfileEntryProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSNiceProfileEntry getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSNiceProfileEntryTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSNiceProfileEntry (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSNiceProfileEntry)p;
	}

	public Long getNpId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getNpId (env));
	}

	public void setNpId (SystemEnvironment env, Long p_npId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setNpId (env, p_npId);
		return ;
	}
	public Integer getPreference (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getPreference (env));
	}

	public void setPreference (SystemEnvironment env, Integer p_preference)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setPreference (env, p_preference);
		return ;
	}
	public Long getFolderId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getFolderId (env));
	}

	public void setFolderId (SystemEnvironment env, Long p_folderId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setFolderId (env, p_folderId);
		return ;
	}
	public Integer getIsSuspended (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getIsSuspended (env));
	}

	public String getIsSuspendedAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSNiceProfileEntryGeneric) object).getIsSuspendedAsString (env);
	}

	public void setIsSuspended (SystemEnvironment env, Integer p_isSuspended)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setIsSuspended (env, p_isSuspended);
		return ;
	}
	public Integer getRenice (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getRenice (env));
	}

	public void setRenice (SystemEnvironment env, Integer p_renice)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setRenice (env, p_renice);
		return ;
	}
	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getIsActive (env));
	}

	public void setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setIsActive (env, p_isActive);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSNiceProfileEntry setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSNiceProfileEntry)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNiceProfileEntryGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSNiceProfileEntry setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSNiceProfileEntryGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSNiceProfileEntry)this;
	}
	public void delete (SystemEnvironment env)
	throws SDMSException
	{
		touchMaster(env);
		super.delete(env);
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
		SDMSTable t;

		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing NiceProfileEntry $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.uid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSNiceProfileEntryGeneric) object).print();
	}
}
