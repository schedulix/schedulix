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

public class SDMSExitStateProxyGeneric extends SDMSProxy
{

	public static final int RESTARTABLE = 1;
	public static final int PENDING = 2;
	public static final int FINAL = 3;
	public static final int UNREACHABLE = 4;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	private static SDMSTable masterTables[] = null;

	protected SDMSExitStateProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Integer getPreference (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getPreference (env));
	}

	public void setPreference (SystemEnvironment env, Integer p_preference)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setPreference (env, p_preference);
		return ;
	}
	public Boolean getIsFinal (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getIsFinal (env));
	}

	public void setIsFinal (SystemEnvironment env, Boolean p_isFinal)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setIsFinal (env, p_isFinal);
		return ;
	}
	public Boolean getIsRestartable (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getIsRestartable (env));
	}

	public void setIsRestartable (SystemEnvironment env, Boolean p_isRestartable)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setIsRestartable (env, p_isRestartable);
		return ;
	}
	public Boolean getIsUnreachable (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getIsUnreachable (env));
	}

	public void setIsUnreachable (SystemEnvironment env, Boolean p_isUnreachable)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setIsUnreachable (env, p_isUnreachable);
		return ;
	}
	public Boolean getIsBroken (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getIsBroken (env));
	}

	public void setIsBroken (SystemEnvironment env, Boolean p_isBroken)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setIsBroken (env, p_isBroken);
		return ;
	}
	public Boolean getIsBatchDefault (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getIsBatchDefault (env));
	}

	public void setIsBatchDefault (SystemEnvironment env, Boolean p_isBatchDefault)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setIsBatchDefault (env, p_isBatchDefault);
		return ;
	}
	public Boolean getIsDependencyDefault (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getIsDependencyDefault (env));
	}

	public void setIsDependencyDefault (SystemEnvironment env, Boolean p_isDependencyDefault)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setIsDependencyDefault (env, p_isDependencyDefault);
		return ;
	}
	public Long getEspId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getEspId (env));
	}

	public void setEspId (SystemEnvironment env, Long p_espId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setEspId (env, p_espId);
		return ;
	}
	public Long getEsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getEsdId (env));
	}

	public void setEsdId (SystemEnvironment env, Long p_esdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setEsdId (env, p_esdId);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSExitState setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSExitState)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSExitStateGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSExitState setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSExitStateGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSExitState)this;
	}
	public SDMSExitState set_EspIdEsdId (SystemEnvironment env, Long p_espId, Long p_esdId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSExitStateGeneric)(object)).set_EspIdEsdId (env, p_espId, p_esdId);
		return (SDMSExitState)this;
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
		Long espId = getEspId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSExitStateProfileTable.getObject(sysEnv, espId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		s.add(getPreference(sysEnv));

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

		t = SystemEnvironment.repository.getTable(env, SDMSExitStateProfileTable.tableName);
		try {
			SDMSProxy o = t.get(env, getEspId(env));
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

		t = SystemEnvironment.repository.getTable(env, SDMSExitStateProfileTable.tableName);
		try {

			SDMSProxy o = t.get(env, getEspId(env), version);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing ExitState $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSExitStateProfileTable.tableName);
		try {
			SDMSProxy p = t.get(env, getEspId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.euid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSExitStateGeneric) object).print();
	}
}
