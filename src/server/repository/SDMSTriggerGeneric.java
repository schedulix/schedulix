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

public class SDMSTriggerGeneric extends SDMSObject
	implements Cloneable
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

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_fireId = 3;
	public final static int nr_objectType = 4;
	public final static int nr_seId = 5;
	public final static int nr_mainSeId = 6;
	public final static int nr_parentSeId = 7;
	public final static int nr_isActive = 8;
	public final static int nr_action = 9;
	public final static int nr_type = 10;
	public final static int nr_isMaster = 11;
	public final static int nr_isSuspend = 12;
	public final static int nr_isCreate = 13;
	public final static int nr_isChange = 14;
	public final static int nr_isDelete = 15;
	public final static int nr_isGroup = 16;
	public final static int nr_resumeAt = 17;
	public final static int nr_resumeIn = 18;
	public final static int nr_resumeBase = 19;
	public final static int nr_isWarnOnLimit = 20;
	public final static int nr_maxRetry = 21;
	public final static int nr_submitOwnerId = 22;
	public final static int nr_condition = 23;
	public final static int nr_checkAmount = 24;
	public final static int nr_checkBase = 25;
	public final static int nr_creatorUId = 26;
	public final static int nr_createTs = 27;
	public final static int nr_changerUId = 28;
	public final static int nr_changeTs = 29;

	public static String tableName = SDMSTriggerTableGeneric.tableName;

	protected String name;
	protected Long fireId;
	protected Integer objectType;
	protected Long seId;
	protected Long mainSeId;
	protected Long parentSeId;
	protected Boolean isActive;
	protected Integer action;
	protected Integer type;
	protected Boolean isMaster;
	protected Boolean isSuspend;
	protected Boolean isCreate;
	protected Boolean isChange;
	protected Boolean isDelete;
	protected Boolean isGroup;
	protected String resumeAt;
	protected Integer resumeIn;
	protected Integer resumeBase;
	protected Boolean isWarnOnLimit;
	protected Integer maxRetry;
	protected Long submitOwnerId;
	protected String condition;
	protected Integer checkAmount;
	protected Integer checkBase;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSTriggerGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Long p_fireId,
	        Integer p_objectType,
	        Long p_seId,
	        Long p_mainSeId,
	        Long p_parentSeId,
	        Boolean p_isActive,
	        Integer p_action,
	        Integer p_type,
	        Boolean p_isMaster,
	        Boolean p_isSuspend,
	        Boolean p_isCreate,
	        Boolean p_isChange,
	        Boolean p_isDelete,
	        Boolean p_isGroup,
	        String p_resumeAt,
	        Integer p_resumeIn,
	        Integer p_resumeBase,
	        Boolean p_isWarnOnLimit,
	        Integer p_maxRetry,
	        Long p_submitOwnerId,
	        String p_condition,
	        Integer p_checkAmount,
	        Integer p_checkBase,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSTriggerTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Trigger) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		fireId = p_fireId;
		objectType = p_objectType;
		seId = p_seId;
		mainSeId = p_mainSeId;
		parentSeId = p_parentSeId;
		isActive = p_isActive;
		action = p_action;
		type = p_type;
		isMaster = p_isMaster;
		isSuspend = p_isSuspend;
		isCreate = p_isCreate;
		isChange = p_isChange;
		isDelete = p_isDelete;
		isGroup = p_isGroup;
		if (p_resumeAt != null && p_resumeAt.length() > 20) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Trigger) Length of $1 exceeds maximum length $2", "resumeAt", "20")
			);
		}
		resumeAt = p_resumeAt;
		resumeIn = p_resumeIn;
		resumeBase = p_resumeBase;
		isWarnOnLimit = p_isWarnOnLimit;
		maxRetry = p_maxRetry;
		submitOwnerId = p_submitOwnerId;
		if (p_condition != null && p_condition.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Trigger) Length of $1 exceeds maximum length $2", "condition", "1024")
			);
		}
		condition = p_condition;
		checkAmount = p_checkAmount;
		checkBase = p_checkBase;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		return (name);
	}

	public	SDMSTriggerGeneric setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Trigger) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getFireId (SystemEnvironment env)
	throws SDMSException
	{
		return (fireId);
	}

	public	SDMSTriggerGeneric setFireId (SystemEnvironment env, Long p_fireId)
	throws SDMSException
	{
		if(fireId.equals(p_fireId)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.fireId = p_fireId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getObjectType (SystemEnvironment env)
	throws SDMSException
	{
		return (objectType);
	}

	public String getObjectTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getObjectType (env);
		switch (v.intValue()) {
		case SDMSTrigger.JOB_DEFINITION:
			return "JOB_DEFINITION";
		case SDMSTrigger.RESOURCE:
			return "RESOURCE";
		case SDMSTrigger.NAMED_RESOURCE:
			return "NAMED_RESOURCE";
		case SDMSTrigger.OBJECT_MONITOR:
			return "OBJECT_MONITOR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Trigger.objectType: $1",
		                          getObjectType (env)));
	}

	public	SDMSTriggerGeneric setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		if(objectType.equals(p_objectType)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.objectType = p_objectType;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	SDMSTriggerGeneric setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.seId = p_seId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getMainSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (mainSeId);
	}

	public	SDMSTriggerGeneric setMainSeId (SystemEnvironment env, Long p_mainSeId)
	throws SDMSException
	{
		if(p_mainSeId != null && p_mainSeId.equals(mainSeId)) return this;
		if(p_mainSeId == null && mainSeId == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.mainSeId = p_mainSeId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getParentSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (parentSeId);
	}

	public	SDMSTriggerGeneric setParentSeId (SystemEnvironment env, Long p_parentSeId)
	throws SDMSException
	{
		if(p_parentSeId != null && p_parentSeId.equals(parentSeId)) return this;
		if(p_parentSeId == null && parentSeId == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.parentSeId = p_parentSeId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		return (isActive);
	}

	public	SDMSTriggerGeneric setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		if(isActive.equals(p_isActive)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isActive = p_isActive;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getAction (SystemEnvironment env)
	throws SDMSException
	{
		return (action);
	}

	public String getActionAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getAction (env);
		switch (v.intValue()) {
		case SDMSTrigger.SUBMIT:
			return "SUBMIT";
		case SDMSTrigger.RERUN:
			return "RERUN";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Trigger.action: $1",
		                          getAction (env)));
	}

	public	SDMSTriggerGeneric setAction (SystemEnvironment env, Integer p_action)
	throws SDMSException
	{
		if(action.equals(p_action)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.action = p_action;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		return (type);
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getType (env);
		switch (v.intValue()) {
		case SDMSTrigger.IMMEDIATE_LOCAL:
			return "IMMEDIATE_LOCAL";
		case SDMSTrigger.BEFORE_FINAL:
			return "BEFORE_FINAL";
		case SDMSTrigger.AFTER_FINAL:
			return "AFTER_FINAL";
		case SDMSTrigger.IMMEDIATE_MERGE:
			return "IMMEDIATE_MERGE";
		case SDMSTrigger.FINISH_CHILD:
			return "FINISH_CHILD";
		case SDMSTrigger.UNTIL_FINISHED:
			return "UNTIL_FINISHED";
		case SDMSTrigger.UNTIL_FINAL:
			return "UNTIL_FINAL";
		case SDMSTrigger.WARNING:
			return "WARNING";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Trigger.type: $1",
		                          getType (env)));
	}

	public	SDMSTriggerGeneric setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		if(type.equals(p_type)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.type = p_type;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsMaster (SystemEnvironment env)
	throws SDMSException
	{
		return (isMaster);
	}

	public String getIsMasterAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsMaster (env);
		final boolean b = v.booleanValue();
		if (b == SDMSTrigger.MASTER)
			return "MASTER";
		if (b == SDMSTrigger.NOMASTER)
			return "NOMASTER";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Trigger.isMaster: $1",
		                          getIsMaster (env)));
	}

	public	SDMSTriggerGeneric setIsMaster (SystemEnvironment env, Boolean p_isMaster)
	throws SDMSException
	{
		if(isMaster.equals(p_isMaster)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isMaster = p_isMaster;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsSuspend (SystemEnvironment env)
	throws SDMSException
	{
		return (isSuspend);
	}

	public String getIsSuspendAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsSuspend (env);
		final boolean b = v.booleanValue();
		if (b == SDMSTrigger.SUSPEND)
			return "SUSPEND";
		if (b == SDMSTrigger.NOSUSPEND)
			return "NOSUSPEND";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Trigger.isSuspend: $1",
		                          getIsSuspend (env)));
	}

	public	SDMSTriggerGeneric setIsSuspend (SystemEnvironment env, Boolean p_isSuspend)
	throws SDMSException
	{
		if(isSuspend.equals(p_isSuspend)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isSuspend = p_isSuspend;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsCreate (SystemEnvironment env)
	throws SDMSException
	{
		return (isCreate);
	}

	public	SDMSTriggerGeneric setIsCreate (SystemEnvironment env, Boolean p_isCreate)
	throws SDMSException
	{
		if(p_isCreate != null && p_isCreate.equals(isCreate)) return this;
		if(p_isCreate == null && isCreate == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isCreate = p_isCreate;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsChange (SystemEnvironment env)
	throws SDMSException
	{
		return (isChange);
	}

	public	SDMSTriggerGeneric setIsChange (SystemEnvironment env, Boolean p_isChange)
	throws SDMSException
	{
		if(p_isChange != null && p_isChange.equals(isChange)) return this;
		if(p_isChange == null && isChange == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isChange = p_isChange;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsDelete (SystemEnvironment env)
	throws SDMSException
	{
		return (isDelete);
	}

	public	SDMSTriggerGeneric setIsDelete (SystemEnvironment env, Boolean p_isDelete)
	throws SDMSException
	{
		if(p_isDelete != null && p_isDelete.equals(isDelete)) return this;
		if(p_isDelete == null && isDelete == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isDelete = p_isDelete;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsGroup (SystemEnvironment env)
	throws SDMSException
	{
		return (isGroup);
	}

	public	SDMSTriggerGeneric setIsGroup (SystemEnvironment env, Boolean p_isGroup)
	throws SDMSException
	{
		if(p_isGroup != null && p_isGroup.equals(isGroup)) return this;
		if(p_isGroup == null && isGroup == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isGroup = p_isGroup;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeAt);
	}

	public	SDMSTriggerGeneric setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		if(p_resumeAt != null && p_resumeAt.equals(resumeAt)) return this;
		if(p_resumeAt == null && resumeAt == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			if (p_resumeAt != null && p_resumeAt.length() > 20) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Trigger) Length of $1 exceeds maximum length $2", "resumeAt", "20")
				);
			}
			o.resumeAt = p_resumeAt;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeIn);
	}

	public	SDMSTriggerGeneric setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		if(p_resumeIn != null && p_resumeIn.equals(resumeIn)) return this;
		if(p_resumeIn == null && resumeIn == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.resumeIn = p_resumeIn;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getResumeBase (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeBase);
	}

	public String getResumeBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getResumeBase (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSTrigger.MINUTE:
			return "MINUTE";
		case SDMSTrigger.HOUR:
			return "HOUR";
		case SDMSTrigger.DAY:
			return "DAY";
		case SDMSTrigger.WEEK:
			return "WEEK";
		case SDMSTrigger.MONTH:
			return "MONTH";
		case SDMSTrigger.YEAR:
			return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Trigger.resumeBase: $1",
		                          getResumeBase (env)));
	}

	public	SDMSTriggerGeneric setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		if(p_resumeBase != null && p_resumeBase.equals(resumeBase)) return this;
		if(p_resumeBase == null && resumeBase == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.resumeBase = p_resumeBase;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsWarnOnLimit (SystemEnvironment env)
	throws SDMSException
	{
		return (isWarnOnLimit);
	}

	public	SDMSTriggerGeneric setIsWarnOnLimit (SystemEnvironment env, Boolean p_isWarnOnLimit)
	throws SDMSException
	{
		if(isWarnOnLimit.equals(p_isWarnOnLimit)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isWarnOnLimit = p_isWarnOnLimit;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getMaxRetry (SystemEnvironment env)
	throws SDMSException
	{
		return (maxRetry);
	}

	public	SDMSTriggerGeneric setMaxRetry (SystemEnvironment env, Integer p_maxRetry)
	throws SDMSException
	{
		if(maxRetry.equals(p_maxRetry)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.maxRetry = p_maxRetry;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getSubmitOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (submitOwnerId);
	}

	public	SDMSTriggerGeneric setSubmitOwnerId (SystemEnvironment env, Long p_submitOwnerId)
	throws SDMSException
	{
		if(p_submitOwnerId != null && p_submitOwnerId.equals(submitOwnerId)) return this;
		if(p_submitOwnerId == null && submitOwnerId == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.submitOwnerId = p_submitOwnerId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		return (condition);
	}

	public	SDMSTriggerGeneric setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		if(p_condition != null && p_condition.equals(condition)) return this;
		if(p_condition == null && condition == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			if (p_condition != null && p_condition.length() > 1024) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Trigger) Length of $1 exceeds maximum length $2", "condition", "1024")
				);
			}
			o.condition = p_condition;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getCheckAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (checkAmount);
	}

	public	SDMSTriggerGeneric setCheckAmount (SystemEnvironment env, Integer p_checkAmount)
	throws SDMSException
	{
		if(p_checkAmount != null && p_checkAmount.equals(checkAmount)) return this;
		if(p_checkAmount == null && checkAmount == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.checkAmount = p_checkAmount;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getCheckBase (SystemEnvironment env)
	throws SDMSException
	{
		return (checkBase);
	}

	public String getCheckBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getCheckBase (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSTrigger.MINUTE:
			return "MINUTE";
		case SDMSTrigger.HOUR:
			return "HOUR";
		case SDMSTrigger.DAY:
			return "DAY";
		case SDMSTrigger.WEEK:
			return "WEEK";
		case SDMSTrigger.MONTH:
			return "MONTH";
		case SDMSTrigger.YEAR:
			return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Trigger.checkBase: $1",
		                          getCheckBase (env)));
	}

	public	SDMSTriggerGeneric setCheckBase (SystemEnvironment env, Integer p_checkBase)
	throws SDMSException
	{
		if(p_checkBase != null && p_checkBase.equals(checkBase)) return this;
		if(p_checkBase == null && checkBase == null) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.checkBase = p_checkBase;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	SDMSTriggerGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.creatorUId = p_creatorUId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	SDMSTriggerGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.createTs = p_createTs;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	SDMSTriggerGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSTriggerGeneric) change(env);
			o.changerUId = p_changerUId;
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	SDMSTriggerGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSTriggerGeneric) change(env);
			o.changeTs = p_changeTs;
			o.changerUId = env.cEnv.euid();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSTriggerGeneric set_FireIdType (SystemEnvironment env, Long p_fireId, Integer p_type)
	throws SDMSException
	{
		SDMSTriggerGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.fireId = p_fireId;
			o.type = p_type;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSTriggerGeneric set_FireIdName (SystemEnvironment env, Long p_fireId, String p_name)
	throws SDMSException
	{
		SDMSTriggerGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.fireId = p_fireId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(Trigger) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy()
	{
		return new SDMSTrigger(this);
	}

	protected SDMSTriggerGeneric(Long p_id,
	                             String p_name,
	                             Long p_fireId,
	                             Integer p_objectType,
	                             Long p_seId,
	                             Long p_mainSeId,
	                             Long p_parentSeId,
	                             Boolean p_isActive,
	                             Integer p_action,
	                             Integer p_type,
	                             Boolean p_isMaster,
	                             Boolean p_isSuspend,
	                             Boolean p_isCreate,
	                             Boolean p_isChange,
	                             Boolean p_isDelete,
	                             Boolean p_isGroup,
	                             String p_resumeAt,
	                             Integer p_resumeIn,
	                             Integer p_resumeBase,
	                             Boolean p_isWarnOnLimit,
	                             Integer p_maxRetry,
	                             Long p_submitOwnerId,
	                             String p_condition,
	                             Integer p_checkAmount,
	                             Integer p_checkBase,
	                             Long p_creatorUId,
	                             Long p_createTs,
	                             Long p_changerUId,
	                             Long p_changeTs,
	                             long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		fireId = p_fireId;
		objectType = p_objectType;
		seId = p_seId;
		mainSeId = p_mainSeId;
		parentSeId = p_parentSeId;
		isActive = p_isActive;
		action = p_action;
		type = p_type;
		isMaster = p_isMaster;
		isSuspend = p_isSuspend;
		isCreate = p_isCreate;
		isChange = p_isChange;
		isDelete = p_isDelete;
		isGroup = p_isGroup;
		resumeAt = p_resumeAt;
		resumeIn = p_resumeIn;
		resumeBase = p_resumeBase;
		isWarnOnLimit = p_isWarnOnLimit;
		maxRetry = p_maxRetry;
		submitOwnerId = p_submitOwnerId;
		condition = p_condition;
		checkAmount = p_checkAmount;
		checkBase = p_checkBase;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		validFrom = p_validFrom;
		validTo   = p_validTo;
	}

	protected String tableName()
	{
		return tableName;
	}

	protected void insertDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pInsert == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				String squote = "";
				String equote = "";
				if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "INSERT INTO TRIGGER_DEFINITION (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "FIRE_ID" + equote +
				        ", " + squote + "OBJECT_TYPE" + equote +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "MAIN_SE_ID" + equote +
				        ", " + squote + "PARENT_SE_ID" + equote +
				        ", " + squote + "IS_ACTIVE" + equote +
				        ", " + squote + "ACTION" + equote +
				        ", " + squote + "TYPE" + equote +
				        ", " + squote + "IS_MASTER" + equote +
				        ", " + squote + "IS_SUSPEND" + equote +
				        ", " + squote + "IS_CREATE" + equote +
				        ", " + squote + "IS_CHANGE" + equote +
				        ", " + squote + "IS_DELETE" + equote +
				        ", " + squote + "IS_GROUP" + equote +
				        ", " + squote + "RESUME_AT" + equote +
				        ", " + squote + "RESUME_IN" + equote +
				        ", " + squote + "RESUME_BASE" + equote +
				        ", " + squote + "IS_WARN_ON_LIMIT" + equote +
				        ", " + squote + "MAX_RETRY" + equote +
				        ", " + squote + "SUBMIT_OWNER_ID" + equote +
				        ", " + squote + "CONDITION" + equote +
				        ", " + squote + "CHECK_AMOUNT" + equote +
				        ", " + squote + "CHECK_BASE" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ", VALID_FROM, VALID_TO" +
				        ") VALUES (?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?, ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "Trigger: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setString(2, name);
			pInsert.setLong (3, fireId.longValue());
			pInsert.setInt(4, objectType.intValue());
			pInsert.setLong (5, seId.longValue());
			if (mainSeId == null)
				pInsert.setNull(6, Types.INTEGER);
			else
				pInsert.setLong (6, mainSeId.longValue());
			if (parentSeId == null)
				pInsert.setNull(7, Types.INTEGER);
			else
				pInsert.setLong (7, parentSeId.longValue());
			pInsert.setInt (8, isActive.booleanValue() ? 1 : 0);
			pInsert.setInt(9, action.intValue());
			pInsert.setInt(10, type.intValue());
			pInsert.setInt (11, isMaster.booleanValue() ? 1 : 0);
			pInsert.setInt (12, isSuspend.booleanValue() ? 1 : 0);
			if (isCreate == null)
				pInsert.setNull(13, Types.INTEGER);
			else
				pInsert.setInt (13, isCreate.booleanValue() ? 1 : 0);
			if (isChange == null)
				pInsert.setNull(14, Types.INTEGER);
			else
				pInsert.setInt (14, isChange.booleanValue() ? 1 : 0);
			if (isDelete == null)
				pInsert.setNull(15, Types.INTEGER);
			else
				pInsert.setInt (15, isDelete.booleanValue() ? 1 : 0);
			if (isGroup == null)
				pInsert.setNull(16, Types.INTEGER);
			else
				pInsert.setInt (16, isGroup.booleanValue() ? 1 : 0);
			if (resumeAt == null)
				pInsert.setNull(17, Types.VARCHAR);
			else
				pInsert.setString(17, resumeAt);
			if (resumeIn == null)
				pInsert.setNull(18, Types.INTEGER);
			else
				pInsert.setInt(18, resumeIn.intValue());
			if (resumeBase == null)
				pInsert.setNull(19, Types.INTEGER);
			else
				pInsert.setInt(19, resumeBase.intValue());
			pInsert.setInt (20, isWarnOnLimit.booleanValue() ? 1 : 0);
			pInsert.setInt(21, maxRetry.intValue());
			if (submitOwnerId == null)
				pInsert.setNull(22, Types.INTEGER);
			else
				pInsert.setLong (22, submitOwnerId.longValue());
			if (condition == null)
				pInsert.setNull(23, Types.VARCHAR);
			else
				pInsert.setString(23, condition);
			if (checkAmount == null)
				pInsert.setNull(24, Types.INTEGER);
			else
				pInsert.setInt(24, checkAmount.intValue());
			if (checkBase == null)
				pInsert.setNull(25, Types.INTEGER);
			else
				pInsert.setInt(25, checkBase.intValue());
			pInsert.setLong (26, creatorUId.longValue());
			pInsert.setLong (27, createTs.longValue());
			pInsert.setLong (28, changerUId.longValue());
			pInsert.setLong (29, changeTs.longValue());
			pInsert.setLong(30, env.tx.versionId);
			pInsert.setLong(31, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "Trigger: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		closeDBObject(env);
		insertDBObject(env);
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		closeDBObject(env);
	}

	private void closeDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pUpdate == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				final boolean postgres = driverName.startsWith("PostgreSQL");
				stmt =
				        "UPDATE TRIGGER_DEFINITION " +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				// Can't prepare statement
				throw new FatalException(new SDMSMessage(env, "01110181955", "Trigger : $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong(1, env.tx.versionId);
			pUpdate.setLong(2, changeTs.longValue());
			pUpdate.setLong(3, changerUId.longValue());
			pUpdate.setLong(4, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181956", "Trigger: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkObjectType(Integer p)
	{
		switch (p.intValue()) {
		case SDMSTrigger.JOB_DEFINITION:
		case SDMSTrigger.RESOURCE:
		case SDMSTrigger.NAMED_RESOURCE:
		case SDMSTrigger.OBJECT_MONITOR:
			return true;
		}
		return false;
	}
	static public boolean checkAction(Integer p)
	{
		switch (p.intValue()) {
		case SDMSTrigger.SUBMIT:
		case SDMSTrigger.RERUN:
			return true;
		}
		return false;
	}
	static public boolean checkType(Integer p)
	{
		switch (p.intValue()) {
		case SDMSTrigger.IMMEDIATE_LOCAL:
		case SDMSTrigger.BEFORE_FINAL:
		case SDMSTrigger.AFTER_FINAL:
		case SDMSTrigger.IMMEDIATE_MERGE:
		case SDMSTrigger.FINISH_CHILD:
		case SDMSTrigger.UNTIL_FINISHED:
		case SDMSTrigger.UNTIL_FINAL:
		case SDMSTrigger.WARNING:
			return true;
		}
		return false;
	}
	static public boolean checkIsMaster(Boolean p)
	{
		if(p.booleanValue() == SDMSTrigger.MASTER) return true;
		if(p.booleanValue() == SDMSTrigger.NOMASTER) return true;
		return false;
	}
	static public boolean checkIsSuspend(Boolean p)
	{
		if(p.booleanValue() == SDMSTrigger.SUSPEND) return true;
		if(p.booleanValue() == SDMSTrigger.NOSUSPEND) return true;
		return false;
	}
	static public boolean checkResumeBase(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSTrigger.MINUTE:
		case SDMSTrigger.HOUR:
		case SDMSTrigger.DAY:
		case SDMSTrigger.WEEK:
		case SDMSTrigger.MONTH:
		case SDMSTrigger.YEAR:
			return true;
		}
		return false;
	}
	static public boolean checkCheckBase(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSTrigger.MINUTE:
		case SDMSTrigger.HOUR:
		case SDMSTrigger.DAY:
		case SDMSTrigger.WEEK:
		case SDMSTrigger.MONTH:
		case SDMSTrigger.YEAR:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : Trigger", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fireId : " + fireId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "objectType : " + objectType, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "mainSeId : " + mainSeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "parentSeId : " + parentSeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isActive : " + isActive, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "action : " + action, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "type : " + type, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isMaster : " + isMaster, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSuspend : " + isSuspend, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isCreate : " + isCreate, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isChange : " + isChange, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isDelete : " + isDelete, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isGroup : " + isGroup, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeAt : " + resumeAt, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeIn : " + resumeIn, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeBase : " + resumeBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isWarnOnLimit : " + isWarnOnLimit, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "maxRetry : " + maxRetry, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "submitOwnerId : " + submitOwnerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "condition : " + condition, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "checkAmount : " + checkAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "checkBase : " + checkBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validTo : " + validTo, SDMSThread.SEVERITY_MESSAGE);
		dumpVersions(SDMSThread.SEVERITY_MESSAGE);
	}

	public String toString(int indent)
	{
		StringBuffer sb = new StringBuffer(indent + 1);
		for(int i = 0; i < indent; ++i) sb.append(" ");
		String indentString = new String(sb);
		String result =
		        indentString + "id : " + id + "\n" +
		        indentString + "name          : " + name + "\n" +
		        indentString + "fireId        : " + fireId + "\n" +
		        indentString + "objectType    : " + objectType + "\n" +
		        indentString + "seId          : " + seId + "\n" +
		        indentString + "mainSeId      : " + mainSeId + "\n" +
		        indentString + "parentSeId    : " + parentSeId + "\n" +
		        indentString + "isActive      : " + isActive + "\n" +
		        indentString + "action        : " + action + "\n" +
		        indentString + "type          : " + type + "\n" +
		        indentString + "isMaster      : " + isMaster + "\n" +
		        indentString + "isSuspend     : " + isSuspend + "\n" +
		        indentString + "isCreate      : " + isCreate + "\n" +
		        indentString + "isChange      : " + isChange + "\n" +
		        indentString + "isDelete      : " + isDelete + "\n" +
		        indentString + "isGroup       : " + isGroup + "\n" +
		        indentString + "resumeAt      : " + resumeAt + "\n" +
		        indentString + "resumeIn      : " + resumeIn + "\n" +
		        indentString + "resumeBase    : " + resumeBase + "\n" +
		        indentString + "isWarnOnLimit : " + isWarnOnLimit + "\n" +
		        indentString + "maxRetry      : " + maxRetry + "\n" +
		        indentString + "submitOwnerId : " + submitOwnerId + "\n" +
		        indentString + "condition     : " + condition + "\n" +
		        indentString + "checkAmount   : " + checkAmount + "\n" +
		        indentString + "checkBase     : " + checkBase + "\n" +
		        indentString + "creatorUId    : " + creatorUId + "\n" +
		        indentString + "createTs      : " + createTs + "\n" +
		        indentString + "changerUId    : " + changerUId + "\n" +
		        indentString + "changeTs      : " + changeTs + "\n" +
		        indentString + "validFrom : " + validFrom + "\n" +
		        indentString + "validTo : " + validTo + "\n";
		return result;
	}

	public String toString()
	{
		String result = toString(0);
		return result;
	}
}
