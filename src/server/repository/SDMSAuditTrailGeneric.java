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

public class SDMSAuditTrailGeneric extends SDMSObject
	implements Cloneable
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

	public final static int nr_id = 1;
	public final static int nr_userId = 2;
	public final static int nr_ts = 3;
	public final static int nr_txId = 4;
	public final static int nr_action = 5;
	public final static int nr_objectType = 6;
	public final static int nr_objectId = 7;
	public final static int nr_originId = 8;
	public final static int nr_isSetWarning = 9;
	public final static int nr_actionInfo = 10;
	public final static int nr_actionComment = 11;
	public final static int nr_creatorUId = 12;
	public final static int nr_createTs = 13;
	public final static int nr_changerUId = 14;
	public final static int nr_changeTs = 15;

	public static String tableName = SDMSAuditTrailTableGeneric.tableName;

	protected Long userId;
	protected Long ts;
	protected Long txId;
	protected Integer action;
	protected Integer objectType;
	protected Long objectId;
	protected Long originId;
	protected Boolean isSetWarning;
	protected String actionInfo;
	protected String actionComment;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSAuditTrailGeneric(
	        SystemEnvironment env,
	        Long p_userId,
	        Long p_ts,
	        Long p_txId,
	        Integer p_action,
	        Integer p_objectType,
	        Long p_objectId,
	        Long p_originId,
	        Boolean p_isSetWarning,
	        String p_actionInfo,
	        String p_actionComment,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSAuditTrailTableGeneric.table);
		userId = p_userId;
		ts = p_ts;
		txId = p_txId;
		action = p_action;
		objectType = p_objectType;
		objectId = p_objectId;
		originId = p_originId;
		isSetWarning = p_isSetWarning;
		if (p_actionInfo != null && p_actionInfo.length() > 1024) {
			p_actionInfo = p_actionInfo.substring(0,1024);
		}
		actionInfo = p_actionInfo;
		if (p_actionComment != null && p_actionComment.length() > 1024) {
			p_actionComment = p_actionComment.substring(0,1024);
		}
		actionComment = p_actionComment;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getUserId (SystemEnvironment env)
	throws SDMSException
	{
		return (userId);
	}

	public	SDMSAuditTrailGeneric setUserId (SystemEnvironment env, Long p_userId)
	throws SDMSException
	{
		if(userId.equals(p_userId)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			o.userId = p_userId;
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

	public Long getTs (SystemEnvironment env)
	throws SDMSException
	{
		return (ts);
	}

	public	SDMSAuditTrailGeneric setTs (SystemEnvironment env, Long p_ts)
	throws SDMSException
	{
		if(ts.equals(p_ts)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			o.ts = p_ts;
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

	public Long getTxId (SystemEnvironment env)
	throws SDMSException
	{
		return (txId);
	}

	public	SDMSAuditTrailGeneric setTxId (SystemEnvironment env, Long p_txId)
	throws SDMSException
	{
		if(txId.equals(p_txId)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			o.txId = p_txId;
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
		case SDMSAuditTrail.RERUN:
			return "RERUN";
		case SDMSAuditTrail.RERUN_RECURSIVE:
			return "RERUN_RECURSIVE";
		case SDMSAuditTrail.CANCEL:
			return "CANCEL";
		case SDMSAuditTrail.SUSPEND:
			return "SUSPEND";
		case SDMSAuditTrail.RESUME:
			return "RESUME";
		case SDMSAuditTrail.SET_STATE:
			return "SET_STATE";
		case SDMSAuditTrail.SET_EXIT_STATE:
			return "SET_EXIT_STATE";
		case SDMSAuditTrail.IGNORE_DEPENDENCY:
			return "IGNORE_DEPENDENCY";
		case SDMSAuditTrail.IGNORE_DEP_RECURSIVE:
			return "IGNORE_DEP_RECURSIVE";
		case SDMSAuditTrail.IGNORE_RESOURCE:
			return "IGNORE_RESOURCE";
		case SDMSAuditTrail.KILL:
			return "KILL";
		case SDMSAuditTrail.ALTER_RUN_PROGRAM:
			return "ALTER_RUN_PROGRAM";
		case SDMSAuditTrail.ALTER_RERUN_PROGRAM:
			return "ALTER_RERUN_PROGRAM";
		case SDMSAuditTrail.COMMENT_JOB:
			return "COMMENT_JOB";
		case SDMSAuditTrail.SUBMITTED:
			return "SUBMITTED";
		case SDMSAuditTrail.TRIGGER_FAILED:
			return "TRIGGER_FAILED";
		case SDMSAuditTrail.TRIGGER_SUBMIT:
			return "TRIGGER_SUBMIT";
		case SDMSAuditTrail.JOB_RESTARTABLE:
			return "JOB_RESTARTABLE";
		case SDMSAuditTrail.CHANGE_PRIORITY:
			return "CHANGE_PRIORITY";
		case SDMSAuditTrail.RENICE:
			return "RENICE";
		case SDMSAuditTrail.SUBMIT_SUSPENDED:
			return "SUBMIT_SUSPENDED";
		case SDMSAuditTrail.IGNORE_NAMED_RESOURCE:
			return "IGNORE_NAMED_RESOURCE";
		case SDMSAuditTrail.TIMEOUT:
			return "TIMEOUT";
		case SDMSAuditTrail.SET_RESOURCE_STATE:
			return "SET_RESOURCE_STATE";
		case SDMSAuditTrail.JOB_IN_ERROR:
			return "JOB_IN_ERROR";
		case SDMSAuditTrail.CLEAR_WARNING:
			return "CLEAR_WARNING";
		case SDMSAuditTrail.SET_WARNING:
			return "SET_WARNING";
		case SDMSAuditTrail.JOB_UNREACHABLE:
			return "JOB_UNREACHABLE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown AuditTrail.action: $1",
		                          getAction (env)));
	}

	public	SDMSAuditTrailGeneric setAction (SystemEnvironment env, Integer p_action)
	throws SDMSException
	{
		if(action.equals(p_action)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
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
		case SDMSAuditTrail.JOB:
			return "JOB";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown AuditTrail.objectType: $1",
		                          getObjectType (env)));
	}

	public	SDMSAuditTrailGeneric setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		if(objectType.equals(p_objectType)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
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

	public Long getObjectId (SystemEnvironment env)
	throws SDMSException
	{
		return (objectId);
	}

	public	SDMSAuditTrailGeneric setObjectId (SystemEnvironment env, Long p_objectId)
	throws SDMSException
	{
		if(objectId.equals(p_objectId)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			o.objectId = p_objectId;
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

	public Long getOriginId (SystemEnvironment env)
	throws SDMSException
	{
		return (originId);
	}

	public	SDMSAuditTrailGeneric setOriginId (SystemEnvironment env, Long p_originId)
	throws SDMSException
	{
		if(originId.equals(p_originId)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			o.originId = p_originId;
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

	public Boolean getIsSetWarning (SystemEnvironment env)
	throws SDMSException
	{
		return (isSetWarning);
	}

	public	SDMSAuditTrailGeneric setIsSetWarning (SystemEnvironment env, Boolean p_isSetWarning)
	throws SDMSException
	{
		if(isSetWarning.equals(p_isSetWarning)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			o.isSetWarning = p_isSetWarning;
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

	public String getActionInfo (SystemEnvironment env)
	throws SDMSException
	{
		return (actionInfo);
	}

	public	SDMSAuditTrailGeneric setActionInfo (SystemEnvironment env, String p_actionInfo)
	throws SDMSException
	{
		if(p_actionInfo != null && p_actionInfo.equals(actionInfo)) return this;
		if(p_actionInfo == null && actionInfo == null) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			if (p_actionInfo != null && p_actionInfo.length() > 1024) {
				p_actionInfo = p_actionInfo.substring(0,1024);
			}
			o.actionInfo = p_actionInfo;
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

	public String getActionComment (SystemEnvironment env)
	throws SDMSException
	{
		return (actionComment);
	}

	public	SDMSAuditTrailGeneric setActionComment (SystemEnvironment env, String p_actionComment)
	throws SDMSException
	{
		if(p_actionComment != null && p_actionComment.equals(actionComment)) return this;
		if(p_actionComment == null && actionComment == null) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
			if (p_actionComment != null && p_actionComment.length() > 1024) {
				p_actionComment = p_actionComment.substring(0,1024);
			}
			o.actionComment = p_actionComment;
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

	SDMSAuditTrailGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
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

	SDMSAuditTrailGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(AuditTrail) Change of system object not allowed")
				);
			}
			o = (SDMSAuditTrailGeneric) change(env);
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

	public	SDMSAuditTrailGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSAuditTrailGeneric) change(env);
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

	SDMSAuditTrailGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSAuditTrailGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSAuditTrailGeneric) change(env);
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

	protected SDMSProxy toProxy()
	{
		return new SDMSAuditTrail(this);
	}

	protected SDMSAuditTrailGeneric(Long p_id,
	                                Long p_userId,
	                                Long p_ts,
	                                Long p_txId,
	                                Integer p_action,
	                                Integer p_objectType,
	                                Long p_objectId,
	                                Long p_originId,
	                                Boolean p_isSetWarning,
	                                String p_actionInfo,
	                                String p_actionComment,
	                                Long p_creatorUId,
	                                Long p_createTs,
	                                Long p_changerUId,
	                                Long p_changeTs,
	                                long p_validFrom, long p_validTo)
	{
		id     = p_id;
		userId = p_userId;
		ts = p_ts;
		txId = p_txId;
		action = p_action;
		objectType = p_objectType;
		objectId = p_objectId;
		originId = p_originId;
		isSetWarning = p_isSetWarning;
		actionInfo = p_actionInfo;
		actionComment = p_actionComment;
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
				        "INSERT INTO AUDIT_TRAIL (" +
				        "ID" +
				        ", " + squote + "USER_ID" + equote +
				        ", " + squote + "TS" + equote +
				        ", " + squote + "TXID" + equote +
				        ", " + squote + "ACTION" + equote +
				        ", " + squote + "OBJECT_TYPE" + equote +
				        ", " + squote + "OBJECT_ID" + equote +
				        ", " + squote + "ORIGIN_ID" + equote +
				        ", " + squote + "IS_SET_WARNING" + equote +
				        ", " + squote + "ACTION_INFO" + equote +
				        ", " + squote + "ACTION_COMMENT" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "AuditTrail: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, userId.longValue());
			pInsert.setLong (3, ts.longValue());
			pInsert.setLong (4, txId.longValue());
			pInsert.setInt(5, action.intValue());
			pInsert.setInt(6, objectType.intValue());
			pInsert.setLong (7, objectId.longValue());
			pInsert.setLong (8, originId.longValue());
			pInsert.setInt (9, isSetWarning.booleanValue() ? 1 : 0);
			if (actionInfo == null)
				pInsert.setNull(10, Types.VARCHAR);
			else
				pInsert.setString(10, actionInfo);
			if (actionComment == null)
				pInsert.setNull(11, Types.VARCHAR);
			else
				pInsert.setString(11, actionComment);
			pInsert.setLong (12, creatorUId.longValue());
			pInsert.setLong (13, createTs.longValue());
			pInsert.setLong (14, changerUId.longValue());
			pInsert.setLong (15, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "AuditTrail: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM AUDIT_TRAIL WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "AuditTrail: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "AuditTrail: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		if(pUpdate == null) {
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
				        "UPDATE AUDIT_TRAIL SET " +
				        "" + squote + "USER_ID" + equote + " = ? " +
				        ", " + squote + "TS" + equote + " = ? " +
				        ", " + squote + "TXID" + equote + " = ? " +
				        ", " + squote + "ACTION" + equote + " = ? " +
				        ", " + squote + "OBJECT_TYPE" + equote + " = ? " +
				        ", " + squote + "OBJECT_ID" + equote + " = ? " +
				        ", " + squote + "ORIGIN_ID" + equote + " = ? " +
				        ", " + squote + "IS_SET_WARNING" + equote + " = ? " +
				        ", " + squote + "ACTION_INFO" + equote + " = ? " +
				        ", " + squote + "ACTION_COMMENT" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "AuditTrail: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, userId.longValue());
			pUpdate.setLong (2, ts.longValue());
			pUpdate.setLong (3, txId.longValue());
			pUpdate.setInt(4, action.intValue());
			pUpdate.setInt(5, objectType.intValue());
			pUpdate.setLong (6, objectId.longValue());
			pUpdate.setLong (7, originId.longValue());
			pUpdate.setInt (8, isSetWarning.booleanValue() ? 1 : 0);
			if (actionInfo == null)
				pUpdate.setNull(9, Types.VARCHAR);
			else
				pUpdate.setString(9, actionInfo);
			if (actionComment == null)
				pUpdate.setNull(10, Types.VARCHAR);
			else
				pUpdate.setString(10, actionComment);
			pUpdate.setLong (11, creatorUId.longValue());
			pUpdate.setLong (12, createTs.longValue());
			pUpdate.setLong (13, changerUId.longValue());
			pUpdate.setLong (14, changeTs.longValue());
			pUpdate.setLong(15, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "AuditTrail: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkAction(Integer p)
	{
		switch (p.intValue()) {
		case SDMSAuditTrail.RERUN:
		case SDMSAuditTrail.RERUN_RECURSIVE:
		case SDMSAuditTrail.CANCEL:
		case SDMSAuditTrail.SUSPEND:
		case SDMSAuditTrail.RESUME:
		case SDMSAuditTrail.SET_STATE:
		case SDMSAuditTrail.SET_EXIT_STATE:
		case SDMSAuditTrail.IGNORE_DEPENDENCY:
		case SDMSAuditTrail.IGNORE_DEP_RECURSIVE:
		case SDMSAuditTrail.IGNORE_RESOURCE:
		case SDMSAuditTrail.KILL:
		case SDMSAuditTrail.ALTER_RUN_PROGRAM:
		case SDMSAuditTrail.ALTER_RERUN_PROGRAM:
		case SDMSAuditTrail.COMMENT_JOB:
		case SDMSAuditTrail.SUBMITTED:
		case SDMSAuditTrail.TRIGGER_FAILED:
		case SDMSAuditTrail.TRIGGER_SUBMIT:
		case SDMSAuditTrail.JOB_RESTARTABLE:
		case SDMSAuditTrail.CHANGE_PRIORITY:
		case SDMSAuditTrail.RENICE:
		case SDMSAuditTrail.SUBMIT_SUSPENDED:
		case SDMSAuditTrail.IGNORE_NAMED_RESOURCE:
		case SDMSAuditTrail.TIMEOUT:
		case SDMSAuditTrail.SET_RESOURCE_STATE:
		case SDMSAuditTrail.JOB_IN_ERROR:
		case SDMSAuditTrail.CLEAR_WARNING:
		case SDMSAuditTrail.SET_WARNING:
		case SDMSAuditTrail.JOB_UNREACHABLE:
			return true;
		}
		return false;
	}
	static public boolean checkObjectType(Integer p)
	{
		switch (p.intValue()) {
		case SDMSAuditTrail.JOB:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : AuditTrail", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "userId : " + userId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ts : " + ts, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "txId : " + txId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "action : " + action, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "objectType : " + objectType, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "objectId : " + objectId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "originId : " + originId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSetWarning : " + isSetWarning, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "actionInfo : " + actionInfo, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "actionComment : " + actionComment, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "userId        : " + userId + "\n" +
		        indentString + "ts            : " + ts + "\n" +
		        indentString + "txId          : " + txId + "\n" +
		        indentString + "action        : " + action + "\n" +
		        indentString + "objectType    : " + objectType + "\n" +
		        indentString + "objectId      : " + objectId + "\n" +
		        indentString + "originId      : " + originId + "\n" +
		        indentString + "isSetWarning  : " + isSetWarning + "\n" +
		        indentString + "actionInfo    : " + actionInfo + "\n" +
		        indentString + "actionComment : " + actionComment + "\n" +
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
