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

public class SDMSDependencyStateProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int condition_size = 1024;

	private static SDMSTable masterTables[] = null;

	protected SDMSDependencyStateProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSDependencyState getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSDependencyStateTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSDependencyState (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSDependencyState)p;
	}

	public Long getDdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyStateGeneric)(object)).getDdId (env));
	}

	public void setDdId (SystemEnvironment env, Long p_ddId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setDdId (env, p_ddId);
		return ;
	}
	public Long getEsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyStateGeneric)(object)).getEsdId (env));
	}

	public void setEsdId (SystemEnvironment env, Long p_esdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setEsdId (env, p_esdId);
		return ;
	}
	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyStateGeneric)(object)).getCondition (env));
	}

	public void setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setCondition (env, p_condition);
		return ;
	}
	public static int getConditionMaxLength ()
	{
		return (1024);
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyStateGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyStateGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyStateGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSDependencyState setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSDependencyState)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyStateGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSDependencyState setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSDependencyState)this;
	}
	public SDMSDependencyState set_DdIdEsdId (SystemEnvironment env, Long p_ddId, Long p_esdId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSDependencyStateGeneric)(object)).set_DdIdEsdId (env, p_ddId, p_esdId);
		return (SDMSDependencyState)this;
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
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSDependencyDefinitionTable.tableName);
		try {
			SDMSProxy o = t.get(env, getDdId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
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
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSDependencyDefinitionTable.tableName);
		try {

			SDMSProxy o = t.get(env, getDdId(env), version);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing DependencyState $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSDependencyDefinitionTable.tableName);
		try {
			SDMSProxy p = t.get(env, getDdId(env));
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
		((SDMSDependencyStateGeneric) object).print();
	}
}
