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

public class SDMSGrantProxyGeneric extends SDMSProxy
{

	public static final int SYSTEM = SDMSObjectComment.SYSTEM;
	public static final int SELECT = SDMSObjectComment.SELECT;
	public static final int ENVIRONMENT = SDMSObjectComment.ENVIRONMENT;
	public static final int EVENT = SDMSObjectComment.EVENT;
	public static final int FOLDER = SDMSObjectComment.FOLDER;
	public static final int INTERVAL = SDMSObjectComment.INTERVAL;
	public static final int JOB = SDMSObjectComment.JOB;
	public static final int JOB_DEFINITION = SDMSObjectComment.JOB_DEFINITION;
	public static final int NAMED_RESOURCE = SDMSObjectComment.NAMED_RESOURCE;
	public static final int SCHEDULE = SDMSObjectComment.SCHEDULE;
	public static final int SCHEDULED_EVENT = SDMSObjectComment.SCHEDULED_EVENT;
	public static final int SCOPE = SDMSObjectComment.SCOPE;
	public static final int GROUP = SDMSObjectComment.GROUP;
	public static final int RESOURCE = SDMSObjectComment.RESOURCE;
	public static final int EXIT_STATE_DEFINITION = SDMSObjectComment.EXIT_STATE_DEFINITION;
	public static final int EXIT_STATE_PROFILE = SDMSObjectComment.EXIT_STATE_PROFILE;
	public static final int EXIT_STATE_MAPPING = SDMSObjectComment.EXIT_STATE_MAPPING;
	public static final int EXIT_STATE_TRANSLATION = SDMSObjectComment.EXIT_STATE_TRANSLATION;
	public static final int RESOURCE_STATE_DEFINITION = SDMSObjectComment.RESOURCE_STATE_DEFINITION;
	public static final int RESOURCE_STATE_PROFILE = SDMSObjectComment.RESOURCE_STATE_PROFILE;
	public static final int RESOURCE_STATE_MAPPING = SDMSObjectComment.RESOURCE_STATE_MAPPING;
	public static final int FOOTPRINT = SDMSObjectComment.FOOTPRINT;
	public static final int USER = SDMSObjectComment.USER;
	public static final int OBJECT_MONITOR = SDMSObjectComment.OBJECT_MONITOR;
	public static final int NICE_PROFILE = SDMSObjectComment.NICE_PROFILE;
	public static final Boolean GRANT = Boolean.TRUE;
	public static final Boolean REVOKE = Boolean.FALSE;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	private static SDMSTable masterTables[] = null;

	protected SDMSGrantProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getObjectId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getObjectId (env));
	}

	public void setObjectId (SystemEnvironment env, Long p_objectId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setObjectId (env, p_objectId);
		return ;
	}
	public Long getGId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getGId (env));
	}

	public void setGId (SystemEnvironment env, Long p_gId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setGId (env, p_gId);
		return ;
	}
	public Integer getObjectType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getObjectType (env));
	}

	public String getObjectTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSGrantGeneric) object).getObjectTypeAsString (env);
	}

	public void setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setObjectType (env, p_objectType);
		return ;
	}
	public Long getPrivs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getPrivs (env));
	}

	public void setPrivs (SystemEnvironment env, Long p_privs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setPrivs (env, p_privs);
		return ;
	}
	public Long getDeleteVersion (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getDeleteVersion (env));
	}

	public void setDeleteVersion (SystemEnvironment env, Long p_deleteVersion)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setDeleteVersion (env, p_deleteVersion);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSGrant setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSGrantGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSGrant)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSGrantGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSGrant setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSGrantGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSGrant)this;
	}
	public SDMSGrant set_ObjectIdGId (SystemEnvironment env, Long p_objectId, Long p_gId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSGrantGeneric)(object)).set_ObjectIdGId (env, p_objectId, p_gId);
		return (SDMSGrant)this;
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

		t = SystemEnvironment.repository.getTable(env, SDMSEventTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFolderTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSGroupTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSIntervalTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedEnvironmentTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduleTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduledEventTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScopeTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Grant $1", getId(sysEnv));
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
		setChangerUIdNoCheck (env, env.cEnv.euid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSGrantGeneric) object).print();
	}
}
