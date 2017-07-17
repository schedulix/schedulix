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

public class SDMSIntervalSelectionProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	private static SDMSTable masterTables[] = null;

	protected SDMSIntervalSelectionProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSIntervalSelection getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSIntervalSelectionTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSIntervalSelection (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSIntervalSelection)p;
	}

	public Long getIntId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getIntId (env));
	}

	public void setIntId (SystemEnvironment env, Long p_intId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setIntId (env, p_intId);
		return ;
	}
	public Integer getValue (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getValue (env));
	}

	public void setValue (SystemEnvironment env, Integer p_value)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setValue (env, p_value);
		return ;
	}
	public Long getPeriodFrom (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getPeriodFrom (env));
	}

	public void setPeriodFrom (SystemEnvironment env, Long p_periodFrom)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setPeriodFrom (env, p_periodFrom);
		return ;
	}
	public Long getPeriodTo (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getPeriodTo (env));
	}

	public void setPeriodTo (SystemEnvironment env, Long p_periodTo)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setPeriodTo (env, p_periodTo);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSIntervalSelection setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSIntervalSelection)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalSelectionGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSIntervalSelection setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSIntervalSelectionGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSIntervalSelection)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing IntervalSelection $1", getId(sysEnv));
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
		((SDMSIntervalSelectionGeneric) object).print();
	}
}
