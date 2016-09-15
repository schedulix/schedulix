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

public class SDMSTriggerProxyGeneric extends SDMSProxy
{

	public static final int JOB_DEFINITION = 0;
	public static final int RESOURCE = 1;
	public static final int NAMED_RESOURCE = 2;
	public static final int OBJECT_MONITOR = 3;
	public static final int CREATE = 0;
	public static final int CHANGE = 1;
	public static final int DELETE = 2;
	public static final int SUBMIT = 0;
	public static final int RERUN = 1;
	public static final int IMMEDIATE_LOCAL = 0;
	public static final int IMMEDIATE_MERGE = 1;
	public static final int BEFORE_FINAL = 2;
	public static final int AFTER_FINAL = 3;
	public static final int FINISH_CHILD = 4;
	public static final int UNTIL_FINISHED = 5;
	public static final int UNTIL_FINAL = 6;
	public static final int WARNING = 7;
	public static final boolean MASTER = true;
	public static final boolean NOMASTER = false;
	public static final boolean SUSPEND = true;
	public static final boolean NOSUSPEND = false;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int name_size = 64;
	static final public int resumeAt_size = 20;
	static final public int condition_size = 1024;

	private static SDMSTable masterTables[] = null;

	protected SDMSTriggerProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSTrigger getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSTriggerTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSTrigger (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSTrigger)p;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setName (env, p_name);
		return ;
	}
	public Long getFireId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getFireId (env));
	}

	public void setFireId (SystemEnvironment env, Long p_fireId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setFireId (env, p_fireId);
		return ;
	}
	public Integer getObjectType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getObjectType (env));
	}

	public String getObjectTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSTriggerGeneric) object).getObjectTypeAsString (env);
	}

	public void setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setObjectType (env, p_objectType);
		return ;
	}
	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public Long getMainSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getMainSeId (env));
	}

	public void setMainSeId (SystemEnvironment env, Long p_mainSeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setMainSeId (env, p_mainSeId);
		return ;
	}
	public Long getParentSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getParentSeId (env));
	}

	public void setParentSeId (SystemEnvironment env, Long p_parentSeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setParentSeId (env, p_parentSeId);
		return ;
	}
	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsActive (env));
	}

	public void setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsActive (env, p_isActive);
		return ;
	}
	public Boolean getIsInverse (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsInverse (env));
	}

	public void setIsInverse (SystemEnvironment env, Boolean p_isInverse)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsInverse (env, p_isInverse);
		return ;
	}
	public Integer getAction (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getAction (env));
	}

	public String getActionAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSTriggerGeneric) object).getActionAsString (env);
	}

	public void setAction (SystemEnvironment env, Integer p_action)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setAction (env, p_action);
		return ;
	}
	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getType (env));
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSTriggerGeneric) object).getTypeAsString (env);
	}

	public void setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setType (env, p_type);
		return ;
	}
	public Boolean getIsMaster (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsMaster (env));
	}

	public String getIsMasterAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSTriggerGeneric) object).getIsMasterAsString (env);
	}

	public void setIsMaster (SystemEnvironment env, Boolean p_isMaster)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsMaster (env, p_isMaster);
		return ;
	}
	public Boolean getIsSuspend (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsSuspend (env));
	}

	public String getIsSuspendAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSTriggerGeneric) object).getIsSuspendAsString (env);
	}

	public void setIsSuspend (SystemEnvironment env, Boolean p_isSuspend)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsSuspend (env, p_isSuspend);
		return ;
	}
	public Boolean getIsCreate (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsCreate (env));
	}

	public void setIsCreate (SystemEnvironment env, Boolean p_isCreate)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsCreate (env, p_isCreate);
		return ;
	}
	public Boolean getIsChange (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsChange (env));
	}

	public void setIsChange (SystemEnvironment env, Boolean p_isChange)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsChange (env, p_isChange);
		return ;
	}
	public Boolean getIsDelete (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsDelete (env));
	}

	public void setIsDelete (SystemEnvironment env, Boolean p_isDelete)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsDelete (env, p_isDelete);
		return ;
	}
	public Boolean getIsGroup (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsGroup (env));
	}

	public void setIsGroup (SystemEnvironment env, Boolean p_isGroup)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsGroup (env, p_isGroup);
		return ;
	}
	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getResumeAt (env));
	}

	public void setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setResumeAt (env, p_resumeAt);
		return ;
	}
	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getResumeIn (env));
	}

	public void setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setResumeIn (env, p_resumeIn);
		return ;
	}
	public Integer getResumeBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getResumeBase (env));
	}

	public String getResumeBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSTriggerGeneric) object).getResumeBaseAsString (env);
	}

	public void setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setResumeBase (env, p_resumeBase);
		return ;
	}
	public Boolean getIsWarnOnLimit (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getIsWarnOnLimit (env));
	}

	public void setIsWarnOnLimit (SystemEnvironment env, Boolean p_isWarnOnLimit)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setIsWarnOnLimit (env, p_isWarnOnLimit);
		return ;
	}
	public Long getLimitState (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getLimitState (env));
	}

	public void setLimitState (SystemEnvironment env, Long p_limitState)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setLimitState (env, p_limitState);
		return ;
	}
	public Integer getMaxRetry (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getMaxRetry (env));
	}

	public void setMaxRetry (SystemEnvironment env, Integer p_maxRetry)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setMaxRetry (env, p_maxRetry);
		return ;
	}
	public Long getSubmitOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getSubmitOwnerId (env));
	}

	public void setSubmitOwnerId (SystemEnvironment env, Long p_submitOwnerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setSubmitOwnerId (env, p_submitOwnerId);
		return ;
	}
	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getCondition (env));
	}

	public void setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setCondition (env, p_condition);
		return ;
	}
	public Integer getCheckAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getCheckAmount (env));
	}

	public void setCheckAmount (SystemEnvironment env, Integer p_checkAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setCheckAmount (env, p_checkAmount);
		return ;
	}
	public Integer getCheckBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getCheckBase (env));
	}

	public String getCheckBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSTriggerGeneric) object).getCheckBaseAsString (env);
	}

	public void setCheckBase (SystemEnvironment env, Integer p_checkBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setCheckBase (env, p_checkBase);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSTrigger setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSTrigger)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSTriggerGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSTrigger setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSTriggerGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSTrigger)this;
	}
	public SDMSTrigger set_FireIdType (SystemEnvironment env, Long p_fireId, Integer p_type)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).set_FireIdType (env, p_fireId, p_type);
		return (SDMSTrigger)this;
	}

	public SDMSTrigger set_FireIdName (SystemEnvironment env, Long p_fireId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).set_FireIdName (env, p_fireId, p_name);
		return (SDMSTrigger)this;
	}

	public SDMSTrigger set_SeIdName (SystemEnvironment env, Long p_seId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).set_SeIdName (env, p_seId, p_name);
		return (SDMSTrigger)this;
	}

	public SDMSTrigger set_FireIdSeIdNameIsInverse (SystemEnvironment env, Long p_fireId, Long p_seId, String p_name, Boolean p_isInverse)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSTriggerGeneric)(object)).set_FireIdSeIdNameIsInverse (env, p_fireId, p_seId, p_name, p_isInverse);
		return (SDMSTrigger)this;
	}

	public void delete (SystemEnvironment env)
	throws SDMSException
	{
		SDMSObjectCommentTable.dropComment (env, getId (env));
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
			SDMSProxy o = t.get(env, getFireId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceTable.tableName);
		try {
			SDMSProxy o = t.get(env, getFireId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {
			SDMSProxy o = t.get(env, getFireId(env));
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

			SDMSProxy o = t.get(env, getFireId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceTable.tableName);
		try {

			SDMSProxy o = t.get(env, getFireId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {

			SDMSProxy o = t.get(env, getFireId(env), version);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Trigger $1", getName(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getFireId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceTable.tableName);
		try {
			SDMSProxy p = t.get(env, getFireId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {
			SDMSProxy p = t.get(env, getFireId(env));
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
		((SDMSTriggerGeneric) object).print();
	}
}
