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

public class SDMSIntervalDispatcherProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int name_size = 64;

	private static SDMSTable masterTables[] = null;

	protected SDMSIntervalDispatcherProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSIntervalDispatcher getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSIntervalDispatcherTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSIntervalDispatcher (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSIntervalDispatcher)p;
	}

	public Long getIntId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getIntId (env));
	}

	public void setIntId (SystemEnvironment env, Long p_intId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setIntId (env, p_intId);
		return ;
	}
	public Integer getSeqNo (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getSeqNo (env));
	}

	public void setSeqNo (SystemEnvironment env, Integer p_seqNo)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setSeqNo (env, p_seqNo);
		return ;
	}
	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setName (env, p_name);
		return ;
	}
	public static int getNameMaxLength ()
	{
		return (64);
	}
	public Long getSelectIntId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getSelectIntId (env));
	}

	public void setSelectIntId (SystemEnvironment env, Long p_selectIntId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setSelectIntId (env, p_selectIntId);
		return ;
	}
	public Long getFilterIntId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getFilterIntId (env));
	}

	public void setFilterIntId (SystemEnvironment env, Long p_filterIntId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setFilterIntId (env, p_filterIntId);
		return ;
	}
	public Boolean getIsEnabled (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getIsEnabled (env));
	}

	public void setIsEnabled (SystemEnvironment env, Boolean p_isEnabled)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setIsEnabled (env, p_isEnabled);
		return ;
	}
	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getIsActive (env));
	}

	public void setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setIsActive (env, p_isActive);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSIntervalDispatcher setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSIntervalDispatcher)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalDispatcherGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSIntervalDispatcher setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSIntervalDispatcher)this;
	}
	public SDMSIntervalDispatcher set_IntIdSeqNo (SystemEnvironment env, Long p_intId, Integer p_seqNo)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSIntervalDispatcherGeneric)(object)).set_IntIdSeqNo (env, p_intId, p_seqNo);
		return (SDMSIntervalDispatcher)this;
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

		gotIt = false;
		Long intId = getIntId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSIntervalTable.getObject(sysEnv, intId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		s.add(getSeqNo(sysEnv));

		sysEnv.tx.sortKeyMap.put(myId, s);
		return s;
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

		t = SystemEnvironment.repository.getTable(env, SDMSIntervalTable.tableName);
		try {
			SDMSProxy o = t.get(env, getIntId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing IntervalDispatcher $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSIntervalTable.tableName);
		try {
			SDMSProxy p = t.get(env, getIntId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.uid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSIntervalDispatcherGeneric) object).print();
	}
}
