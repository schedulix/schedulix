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

public class SDMSSchedulingHierarchyProxyGeneric extends SDMSProxy
{

	public final static String __version = "SDMSSchedulingHierarchyProxyGeneric $Revision: 2.6 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public static final int CHILDSUSPEND = 1;
	public static final int NOSUSPEND = 2;
	public static final int SUSPEND = 3;
	public static final int MERGE_LOCAL = 1;
	public static final int MERGE_GLOBAL = 2;
	public static final int NOMERGE = 3;
	public static final int FAILURE = 4;
	public static final boolean STATIC = true;
	public static final boolean DYNAMIC = false;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int aliasName_size = 64;
	static final public int resumeAt_size = 20;

	private static SDMSTable masterTables[] = null;

	protected SDMSSchedulingHierarchyProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getSeParentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getSeParentId (env));
	}

	public void setSeParentId (SystemEnvironment env, Long p_seParentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setSeParentId (env, p_seParentId);
		return ;
	}
	public Long getSeChildId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getSeChildId (env));
	}

	public void setSeChildId (SystemEnvironment env, Long p_seChildId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setSeChildId (env, p_seChildId);
		return ;
	}
	public String getAliasName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getAliasName (env));
	}

	public void setAliasName (SystemEnvironment env, String p_aliasName)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setAliasName (env, p_aliasName);
		return ;
	}
	public Boolean getIsStatic (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getIsStatic (env));
	}

	public String getIsStaticAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingHierarchyGeneric) object).getIsStaticAsString (env);
	}

	public void setIsStatic (SystemEnvironment env, Boolean p_isStatic)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setIsStatic (env, p_isStatic);
		return ;
	}
	public Integer getPriority (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getPriority (env));
	}

	public void setPriority (SystemEnvironment env, Integer p_priority)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setPriority (env, p_priority);
		return ;
	}
	public Integer getSuspend (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getSuspend (env));
	}

	public String getSuspendAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingHierarchyGeneric) object).getSuspendAsString (env);
	}

	public void setSuspend (SystemEnvironment env, Integer p_suspend)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setSuspend (env, p_suspend);
		return ;
	}
	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getResumeAt (env));
	}

	public void setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setResumeAt (env, p_resumeAt);
		return ;
	}
	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getResumeIn (env));
	}

	public void setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setResumeIn (env, p_resumeIn);
		return ;
	}
	public Integer getResumeBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getResumeBase (env));
	}

	public String getResumeBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingHierarchyGeneric) object).getResumeBaseAsString (env);
	}

	public void setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setResumeBase (env, p_resumeBase);
		return ;
	}
	public Integer getMergeMode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getMergeMode (env));
	}

	public String getMergeModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingHierarchyGeneric) object).getMergeModeAsString (env);
	}

	public void setMergeMode (SystemEnvironment env, Integer p_mergeMode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setMergeMode (env, p_mergeMode);
		return ;
	}
	public Long getEstpId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getEstpId (env));
	}

	public void setEstpId (SystemEnvironment env, Long p_estpId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setEstpId (env, p_estpId);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSSchedulingHierarchy setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSSchedulingHierarchy)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingHierarchyGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSSchedulingHierarchy setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSSchedulingHierarchy)this;
	}
	public SDMSSchedulingHierarchy set_SeParentIdSeChildId (SystemEnvironment env, Long p_seParentId, Long p_seChildId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).set_SeParentIdSeChildId (env, p_seParentId, p_seChildId);
		return (SDMSSchedulingHierarchy)this;
	}

	public SDMSSchedulingHierarchy set_SeParentIdAliasName (SystemEnvironment env, Long p_seParentId, String p_aliasName)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSSchedulingHierarchyGeneric)(object)).set_SeParentIdAliasName (env, p_seParentId, p_aliasName);
		return (SDMSSchedulingHierarchy)this;
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
			SDMSProxy o = t.get(env, getSeParentId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
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

			SDMSProxy o = t.get(env, getSeParentId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing SchedulingHierarchy $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeParentId(env));
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
		((SDMSSchedulingHierarchyGeneric) object).print();
	}
}
