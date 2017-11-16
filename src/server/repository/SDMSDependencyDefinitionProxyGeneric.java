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

public class SDMSDependencyDefinitionProxyGeneric extends SDMSProxy
{

	public static final int IGNORE = 1;
	public static final int UH_IGNORE = 1;
	public static final int ERROR = 2;
	public static final int UH_ERROR = 2;
	public static final int SUSPEND = 3;
	public static final int UH_SUSPEND = 3;
	public static final int DEFER = 4;
	public static final int ALL_FINAL = 1;
	public static final int JOB_FINAL = 2;
	public static final int FINAL = 0;
	public static final int ALL_REACHABLE = 1;
	public static final int UNREACHABLE = 2;
	public static final int DEFAULT = 3;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int name_size = 64;
	static final public int condition_size = 1024;

	private static SDMSTable masterTables[] = null;

	protected SDMSDependencyDefinitionProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getSeDependentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getSeDependentId (env));
	}

	public void setSeDependentId (SystemEnvironment env, Long p_seDependentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setSeDependentId (env, p_seDependentId);
		return ;
	}
	public Long getSeRequiredId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getSeRequiredId (env));
	}

	public void setSeRequiredId (SystemEnvironment env, Long p_seRequiredId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setSeRequiredId (env, p_seRequiredId);
		return ;
	}
	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setName (env, p_name);
		return ;
	}
	public Integer getUnresolvedHandling (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getUnresolvedHandling (env));
	}

	public String getUnresolvedHandlingAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSDependencyDefinitionGeneric) object).getUnresolvedHandlingAsString (env);
	}

	public void setUnresolvedHandling (SystemEnvironment env, Integer p_unresolvedHandling)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setUnresolvedHandling (env, p_unresolvedHandling);
		return ;
	}
	public Integer getMode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getMode (env));
	}

	public String getModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSDependencyDefinitionGeneric) object).getModeAsString (env);
	}

	public void setMode (SystemEnvironment env, Integer p_mode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setMode (env, p_mode);
		return ;
	}
	public Integer getStateSelection (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getStateSelection (env));
	}

	public String getStateSelectionAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSDependencyDefinitionGeneric) object).getStateSelectionAsString (env);
	}

	public void setStateSelection (SystemEnvironment env, Integer p_stateSelection)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setStateSelection (env, p_stateSelection);
		return ;
	}
	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getCondition (env));
	}

	public void setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setCondition (env, p_condition);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSDependencyDefinition setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSDependencyDefinition)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyDefinitionGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSDependencyDefinition setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSDependencyDefinition)this;
	}
	public SDMSDependencyDefinition set_SeDependentIdSeRequiredId (SystemEnvironment env, Long p_seDependentId, Long p_seRequiredId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSDependencyDefinitionGeneric)(object)).set_SeDependentIdSeRequiredId (env, p_seDependentId, p_seRequiredId);
		return (SDMSDependencyDefinition)this;
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
		Long seDependentId = getSeDependentId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSSchedulingEntityTable.getObject(sysEnv, seDependentId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		gotIt = false;
		Long seRequiredId = getSeRequiredId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSSchedulingEntityTable.getObject(sysEnv, seRequiredId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

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

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSeDependentId(env));
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

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {

			SDMSProxy o = t.get(env, getSeDependentId(env), version);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing DependencyDefinition $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeDependentId(env));
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
		((SDMSDependencyDefinitionGeneric) object).print();
	}
}
