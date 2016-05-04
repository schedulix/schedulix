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

public class SDMSDependencyInstanceProxyGeneric extends SDMSProxy
{

	public static final int OPEN = 0;
	public static final int FULFILLED = 1;
	public static final int FAILED = 2;
	public static final int BROKEN = 3;
	public static final int DEFERED = 4;
	public static final int CANCELLED = 8;
	public static final int NO = 0;
	public static final int YES = 1;
	public static final int RECURSIVE = 2;
	public static final int AND = 1;
	public static final int OR = 2;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	private static SDMSTable masterTables[] = null;

	protected SDMSDependencyInstanceProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getDdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getDdId (env));
	}

	public void setDdId (SystemEnvironment env, Long p_ddId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setDdId (env, p_ddId);
		return ;
	}
	public Long getDependentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getDependentId (env));
	}

	public void setDependentId (SystemEnvironment env, Long p_dependentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setDependentId (env, p_dependentId);
		return ;
	}
	public Long getDependentIdOrig (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getDependentIdOrig (env));
	}

	public void setDependentIdOrig (SystemEnvironment env, Long p_dependentIdOrig)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setDependentIdOrig (env, p_dependentIdOrig);
		return ;
	}
	public Integer getDependencyOperation (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getDependencyOperation (env));
	}

	public String getDependencyOperationAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSDependencyInstanceGeneric) object).getDependencyOperationAsString (env);
	}

	public void setDependencyOperation (SystemEnvironment env, Integer p_dependencyOperation)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setDependencyOperation (env, p_dependencyOperation);
		return ;
	}
	public Long getRequiredId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getRequiredId (env));
	}

	public void setRequiredId (SystemEnvironment env, Long p_requiredId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setRequiredId (env, p_requiredId);
		return ;
	}
	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getState (env));
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSDependencyInstanceGeneric) object).getStateAsString (env);
	}

	public void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setState (env, p_state);
		return ;
	}
	public Integer getIgnore (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getIgnore (env));
	}

	public String getIgnoreAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSDependencyInstanceGeneric) object).getIgnoreAsString (env);
	}

	public void setIgnore (SystemEnvironment env, Integer p_ignore)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setIgnore (env, p_ignore);
		return ;
	}
	public Long getDiIdOrig (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getDiIdOrig (env));
	}

	public void setDiIdOrig (SystemEnvironment env, Long p_diIdOrig)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setDiIdOrig (env, p_diIdOrig);
		return ;
	}
	public Long getSeVersion (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getSeVersion (env));
	}

	public void setSeVersion (SystemEnvironment env, Long p_seVersion)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setSeVersion (env, p_seVersion);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSDependencyInstance setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSDependencyInstance)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSDependencyInstanceGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSDependencyInstance setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSDependencyInstance)this;
	}
	public SDMSDependencyInstance set_DdIdDependentIdRequiredId (SystemEnvironment env, Long p_ddId, Long p_dependentId, Long p_requiredId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).set_DdIdDependentIdRequiredId (env, p_ddId, p_dependentId, p_requiredId);
		return (SDMSDependencyInstance)this;
	}

	public SDMSDependencyInstance set_DependentIdRequiredIdState (SystemEnvironment env, Long p_dependentId, Long p_requiredId, Integer p_state)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSDependencyInstanceGeneric)(object)).set_DependentIdRequiredIdState (env, p_dependentId, p_requiredId, p_state);
		return (SDMSDependencyInstance)this;
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

		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getDependentId(env));
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing DependencyInstance $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getDependentId(env));
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
		((SDMSDependencyInstanceGeneric) object).print();
	}
}
