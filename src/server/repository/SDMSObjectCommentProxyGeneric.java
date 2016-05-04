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

public class SDMSObjectCommentProxyGeneric extends SDMSProxy
{

	public static final int SYSTEM = 0;
	public static final int SELECT = 99;
	public static final int DISTRIBUTION = 25;
	public static final int ENVIRONMENT = 1;
	public static final int EXIT_STATE_DEFINITION = 2;
	public static final int EXIT_STATE_PROFILE = 3;
	public static final int EXIT_STATE_MAPPING = 4;
	public static final int EXIT_STATE_TRANSLATION = 5;
	public static final int FOLDER = 6;
	public static final int FOOTPRINT = 7;
	public static final int USER = 8;
	public static final int JOB_DEFINITION = 9;
	public static final int NAMED_RESOURCE = 10;
	public static final int NICE_PROFILE = 31;
	public static final int PARAMETER = 23;
	public static final int POOL = 24;
	public static final int RESOURCE = 11;
	public static final int RESOURCE_STATE_MAPPING = 12;
	public static final int RESOURCE_STATE_DEFINITION = 13;
	public static final int RESOURCE_STATE_PROFILE = 14;
	public static final int SCOPE = 15;
	public static final int TRIGGER = 16;
	public static final int JOB = 17;
	public static final int EVENT = 18;
	public static final int INTERVAL = 19;
	public static final int SCHEDULE = 20;
	public static final int GROUP = 21;
	public static final int SCHEDULED_EVENT = 22;
	public static final int COMMENT = 26;
	public static final int GRANT = 27;
	public static final int RESOURCE_TEMPLATE = 28;
	public static final int WATCH_TYPE = 29;
	public static final int OBJECT_MONITOR = 30;
	public static final int TEXT = 0;
	public static final int URL = 1;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int tag_size = 64;
	static final public int description_size = 1900;

	private static SDMSTable masterTables[] = null;

	protected SDMSObjectCommentProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getObjectId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getObjectId (env));
	}

	public void setObjectId (SystemEnvironment env, Long p_objectId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setObjectId (env, p_objectId);
		return ;
	}
	public Integer getObjectType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getObjectType (env));
	}

	public String getObjectTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSObjectCommentGeneric) object).getObjectTypeAsString (env);
	}

	public void setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setObjectType (env, p_objectType);
		return ;
	}
	public Integer getInfoType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getInfoType (env));
	}

	public String getInfoTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSObjectCommentGeneric) object).getInfoTypeAsString (env);
	}

	public void setInfoType (SystemEnvironment env, Integer p_infoType)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setInfoType (env, p_infoType);
		return ;
	}
	public Integer getSequenceNumber (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getSequenceNumber (env));
	}

	public void setSequenceNumber (SystemEnvironment env, Integer p_sequenceNumber)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setSequenceNumber (env, p_sequenceNumber);
		return ;
	}
	public String getTag (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getTag (env));
	}

	public void setTag (SystemEnvironment env, String p_tag)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setTag (env, p_tag);
		return ;
	}
	public String getDescription (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getDescription (env));
	}

	public void setDescription (SystemEnvironment env, String p_description)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setDescription (env, p_description);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSObjectComment setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSObjectComment)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSObjectCommentGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSObjectComment setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSObjectCommentGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSObjectComment)this;
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

		t = SystemEnvironment.repository.getTable(env, SDMSNamedEnvironmentTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSEventTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateDefinitionTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateProfileTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateMappingProfileTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFolderTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFootprintTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSGroupTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSIntervalTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSParameterDefinitionTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateDefinitionTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateMappingProfileTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateProfileTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduledEventTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduleTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScopeTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSTriggerTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSUserTable.tableName);
		try {
			SDMSProxy o = t.get(env, getObjectId(env));
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

		t = SystemEnvironment.repository.getTable(env, SDMSNamedEnvironmentTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSEventTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateDefinitionTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateProfileTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateMappingProfileTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFolderTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFootprintTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSGroupTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSIntervalTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSParameterDefinitionTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateDefinitionTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateMappingProfileTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateProfileTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduledEventTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduleTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScopeTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSTriggerTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSUserTable.tableName);
		try {

			SDMSProxy o = t.get(env, getObjectId(env), version);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing ObjectComment $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSNamedEnvironmentTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSEventTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateDefinitionTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateProfileTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSExitStateMappingProfileTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFolderTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFootprintTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSGroupTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSIntervalTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSParameterDefinitionTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateDefinitionTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateMappingProfileTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSResourceStateProfileTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduledEventTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScheduleTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSScopeTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSTriggerTable.tableName);
		try {
			SDMSProxy p = t.get(env, getObjectId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSUserTable.tableName);
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
		((SDMSObjectCommentGeneric) object).print();
	}
}
