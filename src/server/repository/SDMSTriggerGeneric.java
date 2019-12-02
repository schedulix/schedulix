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
	public final static int nr_isInverse = 9;
	public final static int nr_action = 10;
	public final static int nr_type = 11;
	public final static int nr_isMaster = 12;
	public final static int nr_isSuspend = 13;
	public final static int nr_isCreate = 14;
	public final static int nr_isChange = 15;
	public final static int nr_isDelete = 16;
	public final static int nr_isGroup = 17;
	public final static int nr_resumeAt = 18;
	public final static int nr_resumeIn = 19;
	public final static int nr_resumeBase = 20;
	public final static int nr_isWarnOnLimit = 21;
	public final static int nr_limitState = 22;
	public final static int nr_maxRetry = 23;
	public final static int nr_submitOwnerId = 24;
	public final static int nr_condition = 25;
	public final static int nr_checkAmount = 26;
	public final static int nr_checkBase = 27;
	public final static int nr_creatorUId = 28;
	public final static int nr_createTs = 29;
	public final static int nr_changerUId = 30;
	public final static int nr_changeTs = 31;

	public static String tableName = SDMSTriggerTableGeneric.tableName;

	protected String name;
	protected Long fireId;
	protected Integer objectType;
	protected Long seId;
	protected Long mainSeId;
	protected Long parentSeId;
	protected Boolean isActive;
	protected Boolean isInverse;
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
	protected Long limitState;
	protected Integer maxRetry;
	protected Long submitOwnerId;
	protected String condition;
	protected Integer checkAmount;
	protected Integer checkBase;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSTriggerGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Long p_fireId,
	        Integer p_objectType,
	        Long p_seId,
	        Long p_mainSeId,
	        Long p_parentSeId,
	        Boolean p_isActive,
	        Boolean p_isInverse,
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
	        Long p_limitState,
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
		isInverse = p_isInverse;
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
		limitState = p_limitState;
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

	public	void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 448);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getFireId (SystemEnvironment env)
	throws SDMSException
	{
		return (fireId);
	}

	public	void setFireId (SystemEnvironment env, Long p_fireId)
	throws SDMSException
	{
		if(fireId.equals(p_fireId)) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 353);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
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

	public	void setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		if(objectType.equals(p_objectType)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.objectType = p_objectType;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 386);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getMainSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (mainSeId);
	}

	public	void setMainSeId (SystemEnvironment env, Long p_mainSeId)
	throws SDMSException
	{
		if(p_mainSeId != null && p_mainSeId.equals(mainSeId)) return;
		if(p_mainSeId == null && mainSeId == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getParentSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (parentSeId);
	}

	public	void setParentSeId (SystemEnvironment env, Long p_parentSeId)
	throws SDMSException
	{
		if(p_parentSeId != null && p_parentSeId.equals(parentSeId)) return;
		if(p_parentSeId == null && parentSeId == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 8);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		return (isActive);
	}

	public	void setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		if(isActive.equals(p_isActive)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isActive = p_isActive;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsInverse (SystemEnvironment env)
	throws SDMSException
	{
		return (isInverse);
	}

	public	void setIsInverse (SystemEnvironment env, Boolean p_isInverse)
	throws SDMSException
	{
		if(isInverse.equals(p_isInverse)) return;
		SDMSTriggerGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerGeneric) change(env);
			o.isInverse = p_isInverse;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 256);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
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

	public	void setAction (SystemEnvironment env, Integer p_action)
	throws SDMSException
	{
		if(action.equals(p_action)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.action = p_action;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		if(type.equals(p_type)) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 32);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
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

	public	void setIsMaster (SystemEnvironment env, Boolean p_isMaster)
	throws SDMSException
	{
		if(isMaster.equals(p_isMaster)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isMaster = p_isMaster;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setIsSuspend (SystemEnvironment env, Boolean p_isSuspend)
	throws SDMSException
	{
		if(isSuspend.equals(p_isSuspend)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isSuspend = p_isSuspend;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsCreate (SystemEnvironment env)
	throws SDMSException
	{
		return (isCreate);
	}

	public	void setIsCreate (SystemEnvironment env, Boolean p_isCreate)
	throws SDMSException
	{
		if(p_isCreate != null && p_isCreate.equals(isCreate)) return;
		if(p_isCreate == null && isCreate == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isCreate = p_isCreate;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsChange (SystemEnvironment env)
	throws SDMSException
	{
		return (isChange);
	}

	public	void setIsChange (SystemEnvironment env, Boolean p_isChange)
	throws SDMSException
	{
		if(p_isChange != null && p_isChange.equals(isChange)) return;
		if(p_isChange == null && isChange == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isChange = p_isChange;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsDelete (SystemEnvironment env)
	throws SDMSException
	{
		return (isDelete);
	}

	public	void setIsDelete (SystemEnvironment env, Boolean p_isDelete)
	throws SDMSException
	{
		if(p_isDelete != null && p_isDelete.equals(isDelete)) return;
		if(p_isDelete == null && isDelete == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isDelete = p_isDelete;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsGroup (SystemEnvironment env)
	throws SDMSException
	{
		return (isGroup);
	}

	public	void setIsGroup (SystemEnvironment env, Boolean p_isGroup)
	throws SDMSException
	{
		if(p_isGroup != null && p_isGroup.equals(isGroup)) return;
		if(p_isGroup == null && isGroup == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isGroup = p_isGroup;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeAt);
	}

	public	void setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		if(p_resumeAt != null && p_resumeAt.equals(resumeAt)) return;
		if(p_resumeAt == null && resumeAt == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		if (p_resumeAt != null && p_resumeAt.length() > 20) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(Trigger) Length of $1 exceeds maximum length $2", "resumeAt", "20")
			);
		}
		o.resumeAt = p_resumeAt;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeIn);
	}

	public	void setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		if(p_resumeIn != null && p_resumeIn.equals(resumeIn)) return;
		if(p_resumeIn == null && resumeIn == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.resumeIn = p_resumeIn;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		if(p_resumeBase != null && p_resumeBase.equals(resumeBase)) return;
		if(p_resumeBase == null && resumeBase == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.resumeBase = p_resumeBase;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsWarnOnLimit (SystemEnvironment env)
	throws SDMSException
	{
		return (isWarnOnLimit);
	}

	public	void setIsWarnOnLimit (SystemEnvironment env, Boolean p_isWarnOnLimit)
	throws SDMSException
	{
		if(isWarnOnLimit.equals(p_isWarnOnLimit)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.isWarnOnLimit = p_isWarnOnLimit;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getLimitState (SystemEnvironment env)
	throws SDMSException
	{
		return (limitState);
	}

	public	void setLimitState (SystemEnvironment env, Long p_limitState)
	throws SDMSException
	{
		if(p_limitState != null && p_limitState.equals(limitState)) return;
		if(p_limitState == null && limitState == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.limitState = p_limitState;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getMaxRetry (SystemEnvironment env)
	throws SDMSException
	{
		return (maxRetry);
	}

	public	void setMaxRetry (SystemEnvironment env, Integer p_maxRetry)
	throws SDMSException
	{
		if(maxRetry.equals(p_maxRetry)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.maxRetry = p_maxRetry;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSubmitOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (submitOwnerId);
	}

	public	void setSubmitOwnerId (SystemEnvironment env, Long p_submitOwnerId)
	throws SDMSException
	{
		if(p_submitOwnerId != null && p_submitOwnerId.equals(submitOwnerId)) return;
		if(p_submitOwnerId == null && submitOwnerId == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 16);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		return (condition);
	}

	public	void setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		if(p_condition != null && p_condition.equals(condition)) return;
		if(p_condition == null && condition == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		if (p_condition != null && p_condition.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(Trigger) Length of $1 exceeds maximum length $2", "condition", "1024")
			);
		}
		o.condition = p_condition;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCheckAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (checkAmount);
	}

	public	void setCheckAmount (SystemEnvironment env, Integer p_checkAmount)
	throws SDMSException
	{
		if(p_checkAmount != null && p_checkAmount.equals(checkAmount)) return;
		if(p_checkAmount == null && checkAmount == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.checkAmount = p_checkAmount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setCheckBase (SystemEnvironment env, Integer p_checkBase)
	throws SDMSException
	{
		if(p_checkBase != null && p_checkBase.equals(checkBase)) return;
		if(p_checkBase == null && checkBase == null) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.checkBase = p_checkBase;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return;
		SDMSTriggerGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Trigger) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSTriggerGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.changerUId = p_changerUId;
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return;
		SDMSTriggerGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSTriggerGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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
			o.changerUId = env.cEnv.uid();
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSTriggerGeneric set_SeIdName (SystemEnvironment env, Long p_seId, String p_name)
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
			o.seId = p_seId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(Trigger) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSTriggerGeneric set_FireIdSeIdNameIsInverse (SystemEnvironment env, Long p_fireId, Long p_seId, String p_name, Boolean p_isInverse)
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
			o.seId = p_seId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(Trigger) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.name = p_name;
			o.isInverse = p_isInverse;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
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
	                             Boolean p_isInverse,
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
	                             Long p_limitState,
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
		isInverse = p_isInverse;
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
		limitState = p_limitState;
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
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "INSERT INTO " + squote + "TRIGGER_DEFINITION" + equote + " (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "FIRE_ID" + equote +
				        ", " + squote + "OBJECT_TYPE" + equote +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "MAIN_SE_ID" + equote +
				        ", " + squote + "PARENT_SE_ID" + equote +
				        ", " + squote + "IS_ACTIVE" + equote +
				        ", " + squote + "IS_INVERSE" + equote +
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
				        ", " + squote + "LIMIT_STATE" + equote +
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
				        ", ?" +
				        ", ?" +
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "Trigger: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setString(2, name);
			myInsert.setLong (3, fireId.longValue());
			myInsert.setInt(4, objectType.intValue());
			myInsert.setLong (5, seId.longValue());
			if (mainSeId == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setLong (6, mainSeId.longValue());
			if (parentSeId == null)
				myInsert.setNull(7, Types.INTEGER);
			else
				myInsert.setLong (7, parentSeId.longValue());
			myInsert.setInt (8, isActive.booleanValue() ? 1 : 0);
			myInsert.setInt (9, isInverse.booleanValue() ? 1 : 0);
			myInsert.setInt(10, action.intValue());
			myInsert.setInt(11, type.intValue());
			myInsert.setInt (12, isMaster.booleanValue() ? 1 : 0);
			myInsert.setInt (13, isSuspend.booleanValue() ? 1 : 0);
			if (isCreate == null)
				myInsert.setNull(14, Types.INTEGER);
			else
				myInsert.setInt (14, isCreate.booleanValue() ? 1 : 0);
			if (isChange == null)
				myInsert.setNull(15, Types.INTEGER);
			else
				myInsert.setInt (15, isChange.booleanValue() ? 1 : 0);
			if (isDelete == null)
				myInsert.setNull(16, Types.INTEGER);
			else
				myInsert.setInt (16, isDelete.booleanValue() ? 1 : 0);
			if (isGroup == null)
				myInsert.setNull(17, Types.INTEGER);
			else
				myInsert.setInt (17, isGroup.booleanValue() ? 1 : 0);
			if (resumeAt == null)
				myInsert.setNull(18, Types.VARCHAR);
			else
				myInsert.setString(18, resumeAt);
			if (resumeIn == null)
				myInsert.setNull(19, Types.INTEGER);
			else
				myInsert.setInt(19, resumeIn.intValue());
			if (resumeBase == null)
				myInsert.setNull(20, Types.INTEGER);
			else
				myInsert.setInt(20, resumeBase.intValue());
			myInsert.setInt (21, isWarnOnLimit.booleanValue() ? 1 : 0);
			if (limitState == null)
				myInsert.setNull(22, Types.INTEGER);
			else
				myInsert.setLong (22, limitState.longValue());
			myInsert.setInt(23, maxRetry.intValue());
			if (submitOwnerId == null)
				myInsert.setNull(24, Types.INTEGER);
			else
				myInsert.setLong (24, submitOwnerId.longValue());
			if (condition == null)
				myInsert.setNull(25, Types.VARCHAR);
			else
				myInsert.setString(25, condition);
			if (checkAmount == null)
				myInsert.setNull(26, Types.INTEGER);
			else
				myInsert.setInt(26, checkAmount.intValue());
			if (checkBase == null)
				myInsert.setNull(27, Types.INTEGER);
			else
				myInsert.setInt(27, checkBase.intValue());
			myInsert.setLong (28, creatorUId.longValue());
			myInsert.setLong (29, createTs.longValue());
			myInsert.setLong (30, changerUId.longValue());
			myInsert.setLong (31, changeTs.longValue());
			myInsert.setLong(32, env.tx.versionId);
			myInsert.setLong(33, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "Trigger: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				final boolean postgres = driverName.startsWith("PostgreSQL");
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE " + squote + "TRIGGER_DEFINITION" + equote +
				        " SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181955", "Trigger : $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong(1, env.tx.versionId);
			myUpdate.setLong(2, changeTs.longValue());
			myUpdate.setLong(3, changerUId.longValue());
			myUpdate.setLong(4, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "Trigger: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
		SDMSThread.doTrace(null, "isInverse : " + isInverse, SDMSThread.SEVERITY_MESSAGE);
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
		SDMSThread.doTrace(null, "limitState : " + limitState, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "isInverse     : " + isInverse + "\n" +
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
		        indentString + "limitState    : " + limitState + "\n" +
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
