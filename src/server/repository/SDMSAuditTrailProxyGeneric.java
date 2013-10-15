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

public class SDMSAuditTrailProxyGeneric extends SDMSProxy
{

	public static final int ENVIRONMENT = SDMSObjectComment.ENVIRONMENT;
	public static final int EVENT = SDMSObjectComment.EVENT;
	public static final int EXIT_STATE_DEFINITION = SDMSObjectComment.EXIT_STATE_DEFINITION;
	public static final int EXIT_STATE_PROFILE = SDMSObjectComment.EXIT_STATE_PROFILE;
	public static final int EXIT_STATE_MAPPING = SDMSObjectComment.EXIT_STATE_MAPPING;
	public static final int EXIT_STATE_TRANSLATION = SDMSObjectComment.EXIT_STATE_TRANSLATION;
	public static final int FOLDER = SDMSObjectComment.FOLDER;
	public static final int FOOTPRINT = SDMSObjectComment.FOOTPRINT;
	public static final int GROUP = SDMSObjectComment.GROUP;
	public static final int INTERVAL = SDMSObjectComment.INTERVAL;
	public static final int JOB = SDMSObjectComment.JOB;
	public static final int JOB_DEFINITION = SDMSObjectComment.JOB_DEFINITION;
	public static final int NAMED_RESOURCE = SDMSObjectComment.NAMED_RESOURCE;
	public static final int RESOURCE = SDMSObjectComment.RESOURCE;
	public static final int RESOURCE_STATE_DEFINITION = SDMSObjectComment.RESOURCE_STATE_DEFINITION;
	public static final int RESOURCE_STATE_MAPPING = SDMSObjectComment.RESOURCE_STATE_MAPPING;
	public static final int RESOURCE_STATE_PROFILE = SDMSObjectComment.RESOURCE_STATE_PROFILE;
	public static final int SCHEDULE = SDMSObjectComment.SCHEDULE;
	public static final int SCOPE = SDMSObjectComment.SCOPE;
	public static final int TRIGGER = SDMSObjectComment.TRIGGER;
	public static final int USER = SDMSObjectComment.USER;
	public static final int RERUN = 1;
	public static final int RERUN_RECURSIVE = 2;
	public static final int CANCEL = 3;
	public static final int SUSPEND = 4;
	public static final int RESUME = 5;
	public static final int SET_STATE = 6;
	public static final int SET_EXIT_STATE = 7;
	public static final int IGNORE_DEPENDENCY = 8;
	public static final int IGNORE_DEP_RECURSIVE = 9;
	public static final int IGNORE_RESOURCE = 10;
	public static final int KILL = 11;
	public static final int ALTER_RUN_PROGRAM = 12;
	public static final int ALTER_RERUN_PROGRAM = 13;
	public static final int COMMENT_JOB = 14;
	public static final int SUBMITTED = 15;
	public static final int TRIGGER_FAILED = 16;
	public static final int TRIGGER_SUBMIT = 17;
	public static final int JOB_RESTARTABLE = 18;
	public static final int CHANGE_PRIORITY = 19;
	public static final int RENICE = 20;
	public static final int SUBMIT_SUSPENDED = 21;
	public static final int IGNORE_NAMED_RESOURCE = 22;
	public static final int TIMEOUT = 23;
	public static final int SET_RESOURCE_STATE = 24;
	public static final int JOB_IN_ERROR = 25;
	public static final int CLEAR_WARNING = 26;
	public static final int SET_WARNING = 27;
	public static final int JOB_UNREACHABLE = 28;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW;

	static final public int actionInfo_size = 1024;
	static final public int actionComment_size = 1024;

	private static SDMSTable masterTables[] = null;

	protected SDMSAuditTrailProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getUserId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getUserId (env));
	}

	public void setUserId (SystemEnvironment env, Long p_userId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setUserId (env, p_userId);
		return ;
	}
	public Long getTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getTs (env));
	}

	public void setTs (SystemEnvironment env, Long p_ts)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setTs (env, p_ts);
		return ;
	}
	public Long getTxId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getTxId (env));
	}

	public void setTxId (SystemEnvironment env, Long p_txId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setTxId (env, p_txId);
		return ;
	}
	public Integer getAction (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getAction (env));
	}

	public String getActionAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSAuditTrailGeneric) object).getActionAsString (env);
	}

	public void setAction (SystemEnvironment env, Integer p_action)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setAction (env, p_action);
		return ;
	}
	public Integer getObjectType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getObjectType (env));
	}

	public String getObjectTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSAuditTrailGeneric) object).getObjectTypeAsString (env);
	}

	public void setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setObjectType (env, p_objectType);
		return ;
	}
	public Long getObjectId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getObjectId (env));
	}

	public void setObjectId (SystemEnvironment env, Long p_objectId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setObjectId (env, p_objectId);
		return ;
	}
	public Long getOriginId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getOriginId (env));
	}

	public void setOriginId (SystemEnvironment env, Long p_originId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setOriginId (env, p_originId);
		return ;
	}
	public Boolean getIsSetWarning (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getIsSetWarning (env));
	}

	public void setIsSetWarning (SystemEnvironment env, Boolean p_isSetWarning)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setIsSetWarning (env, p_isSetWarning);
		return ;
	}
	public String getActionInfo (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getActionInfo (env));
	}

	public void setActionInfo (SystemEnvironment env, String p_actionInfo)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setActionInfo (env, p_actionInfo);
		return ;
	}
	public String getActionComment (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getActionComment (env));
	}

	public void setActionComment (SystemEnvironment env, String p_actionComment)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setActionComment (env, p_actionComment);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSAuditTrail setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSAuditTrail)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSAuditTrailGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSAuditTrail setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSAuditTrailGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSAuditTrail)this;
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
			SDMSProxy o = t.get(env, getObjectId(env));
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing AuditTrail $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
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
		((SDMSAuditTrailGeneric) object).print();
	}
}
