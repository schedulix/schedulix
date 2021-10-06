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

public class SDMSSystemMessageGeneric extends SDMSObject
	implements Cloneable
{

	public static final int CANCEL = 1;
	public static final int RERUN = 2;
	public static final int ENABLE = 3;
	public static final int SET_STATE = 4;
	public static final int IGN_DEPENDENCY = 5;
	public static final int IGN_RESOURCE = 6;
	public static final int CLONE = 7;
	public static final int SUSPEND = 8;
	public static final int CLEAR_WARNING = 9;
	public static final int SET_WARNING = 29;
	public static final int PRIORITY = 10;
	public static final int MODIFY_PARAMETER = 11;
	public static final int KILL = 12;
	public static final int SET_JOB_STATE = 13;
	public static final int DISABLE = 23;
	public static final int RESUME = 28;
	public static final int RENICE = 30;
	public static final int NICEVALUE = 50;
	public static final int APPROVAL = 1;

	public final static int nr_id = 1;
	public final static int nr_msgType = 2;
	public final static int nr_smeId = 3;
	public final static int nr_masterId = 4;
	public final static int nr_operation = 5;
	public final static int nr_isMandatory = 6;
	public final static int nr_requestUId = 7;
	public final static int nr_requestTs = 8;
	public final static int nr_requestMsg = 9;
	public final static int nr_additionalLong = 10;
	public final static int nr_additionalBool = 11;
	public final static int nr_secondLong = 12;
	public final static int nr_comment = 13;
	public final static int nr_creatorUId = 14;
	public final static int nr_createTs = 15;
	public final static int nr_changerUId = 16;
	public final static int nr_changeTs = 17;

	public static String tableName = SDMSSystemMessageTableGeneric.tableName;

	protected Integer msgType;
	protected Long smeId;
	protected Long masterId;
	protected Integer operation;
	protected Boolean isMandatory;
	protected Long requestUId;
	protected Long requestTs;
	protected String requestMsg;
	protected Long additionalLong;
	protected Boolean additionalBool;
	protected Long secondLong;
	protected String comment;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSSystemMessageGeneric(
	        SystemEnvironment env,
	        Integer p_msgType,
	        Long p_smeId,
	        Long p_masterId,
	        Integer p_operation,
	        Boolean p_isMandatory,
	        Long p_requestUId,
	        Long p_requestTs,
	        String p_requestMsg,
	        Long p_additionalLong,
	        Boolean p_additionalBool,
	        Long p_secondLong,
	        String p_comment,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSSystemMessageTableGeneric.table);
		msgType = p_msgType;
		smeId = p_smeId;
		masterId = p_masterId;
		operation = p_operation;
		isMandatory = p_isMandatory;
		requestUId = p_requestUId;
		requestTs = p_requestTs;
		if (p_requestMsg != null && p_requestMsg.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SystemMessage) Length of $1 exceeds maximum length $2", "requestMsg", "512")
			);
		}
		requestMsg = p_requestMsg;
		additionalLong = p_additionalLong;
		additionalBool = p_additionalBool;
		secondLong = p_secondLong;
		if (p_comment != null && p_comment.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SystemMessage) Length of $1 exceeds maximum length $2", "comment", "1024")
			);
		}
		comment = p_comment;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Integer getMsgType (SystemEnvironment env)
	throws SDMSException
	{
		return (msgType);
	}

	public String getMsgTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getMsgType (env);
		switch (v.intValue()) {
			case SDMSSystemMessage.APPROVAL:
				return "APPROVAL";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SystemMessage.msgType: $1",
		                          getMsgType (env)));
	}

	public	void setMsgType (SystemEnvironment env, Integer p_msgType)
	throws SDMSException
	{
		if(msgType.equals(p_msgType)) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.msgType = p_msgType;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return;
		SDMSSystemMessageGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
				);
			}
			o = (SDMSSystemMessageGeneric) change(env);
			o.smeId = p_smeId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		return (masterId);
	}

	public	void setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		if(masterId.equals(p_masterId)) return;
		SDMSSystemMessageGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
				);
			}
			o = (SDMSSystemMessageGeneric) change(env);
			o.masterId = p_masterId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 2);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getOperation (SystemEnvironment env)
	throws SDMSException
	{
		return (operation);
	}

	public String getOperationAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getOperation (env);
		switch (v.intValue()) {
			case SDMSSystemMessage.CANCEL:
				return "CANCEL";
			case SDMSSystemMessage.RERUN:
				return "RERUN";
			case SDMSSystemMessage.ENABLE:
				return "ENABLE";
			case SDMSSystemMessage.SET_STATE:
				return "SET_STATE";
			case SDMSSystemMessage.IGN_DEPENDENCY:
				return "IGN_DEPENDENCY";
			case SDMSSystemMessage.IGN_RESOURCE:
				return "IGN_RESOURCE";
			case SDMSSystemMessage.CLONE:
				return "CLONE";
			case SDMSSystemMessage.SUSPEND:
				return "SUSPEND";
			case SDMSSystemMessage.CLEAR_WARNING:
				return "CLEAR_WARNING";
			case SDMSSystemMessage.PRIORITY:
				return "PRIORITY";
			case SDMSSystemMessage.MODIFY_PARAMETER:
				return "MODIFY_PARAMETER";
			case SDMSSystemMessage.KILL:
				return "KILL";
			case SDMSSystemMessage.DISABLE:
				return "DISABLE";
			case SDMSSystemMessage.RESUME:
				return "RESUME";
			case SDMSSystemMessage.SET_WARNING:
				return "SET_WARNING";
			case SDMSSystemMessage.RENICE:
				return "RENICE";
			case SDMSSystemMessage.NICEVALUE:
				return "NICEVALUE";
			case SDMSSystemMessage.SET_JOB_STATE:
				return "SET_JOB_STATE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SystemMessage.operation: $1",
		                          getOperation (env)));
	}

	public	void setOperation (SystemEnvironment env, Integer p_operation)
	throws SDMSException
	{
		if(operation.equals(p_operation)) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.operation = p_operation;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsMandatory (SystemEnvironment env)
	throws SDMSException
	{
		return (isMandatory);
	}

	public	void setIsMandatory (SystemEnvironment env, Boolean p_isMandatory)
	throws SDMSException
	{
		if(isMandatory.equals(p_isMandatory)) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.isMandatory = p_isMandatory;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getRequestUId (SystemEnvironment env)
	throws SDMSException
	{
		return (requestUId);
	}

	public	void setRequestUId (SystemEnvironment env, Long p_requestUId)
	throws SDMSException
	{
		if(requestUId.equals(p_requestUId)) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.requestUId = p_requestUId;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getRequestTs (SystemEnvironment env)
	throws SDMSException
	{
		return (requestTs);
	}

	public	void setRequestTs (SystemEnvironment env, Long p_requestTs)
	throws SDMSException
	{
		if(requestTs.equals(p_requestTs)) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.requestTs = p_requestTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getRequestMsg (SystemEnvironment env)
	throws SDMSException
	{
		return (requestMsg);
	}

	public	void setRequestMsg (SystemEnvironment env, String p_requestMsg)
	throws SDMSException
	{
		if(p_requestMsg != null && p_requestMsg.equals(requestMsg)) return;
		if(p_requestMsg == null && requestMsg == null) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		if (p_requestMsg != null && p_requestMsg.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SystemMessage) Length of $1 exceeds maximum length $2", "requestMsg", "512")
			);
		}
		o.requestMsg = p_requestMsg;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getAdditionalLong (SystemEnvironment env)
	throws SDMSException
	{
		return (additionalLong);
	}

	public	void setAdditionalLong (SystemEnvironment env, Long p_additionalLong)
	throws SDMSException
	{
		if(p_additionalLong != null && p_additionalLong.equals(additionalLong)) return;
		if(p_additionalLong == null && additionalLong == null) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.additionalLong = p_additionalLong;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getAdditionalBool (SystemEnvironment env)
	throws SDMSException
	{
		return (additionalBool);
	}

	public	void setAdditionalBool (SystemEnvironment env, Boolean p_additionalBool)
	throws SDMSException
	{
		if(p_additionalBool != null && p_additionalBool.equals(additionalBool)) return;
		if(p_additionalBool == null && additionalBool == null) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.additionalBool = p_additionalBool;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSecondLong (SystemEnvironment env)
	throws SDMSException
	{
		return (secondLong);
	}

	public	void setSecondLong (SystemEnvironment env, Long p_secondLong)
	throws SDMSException
	{
		if(p_secondLong != null && p_secondLong.equals(secondLong)) return;
		if(p_secondLong == null && secondLong == null) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.secondLong = p_secondLong;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getComment (SystemEnvironment env)
	throws SDMSException
	{
		return (comment);
	}

	public	void setComment (SystemEnvironment env, String p_comment)
	throws SDMSException
	{
		if(p_comment != null && p_comment.equals(comment)) return;
		if(p_comment == null && comment == null) return;
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		if (p_comment != null && p_comment.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SystemMessage) Length of $1 exceeds maximum length $2", "comment", "1024")
			);
		}
		o.comment = p_comment;
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
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
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
		SDMSSystemMessageGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SystemMessage) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
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
		SDMSSystemMessageGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
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
		SDMSSystemMessageGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSystemMessageGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return new SDMSSystemMessage(this);
	}

	protected SDMSSystemMessageGeneric(Long p_id,
	                                   Integer p_msgType,
	                                   Long p_smeId,
	                                   Long p_masterId,
	                                   Integer p_operation,
	                                   Boolean p_isMandatory,
	                                   Long p_requestUId,
	                                   Long p_requestTs,
	                                   String p_requestMsg,
	                                   Long p_additionalLong,
	                                   Boolean p_additionalBool,
	                                   Long p_secondLong,
	                                   String p_comment,
	                                   Long p_creatorUId,
	                                   Long p_createTs,
	                                   Long p_changerUId,
	                                   Long p_changeTs,
	                                   long p_validFrom, long p_validTo)
	{
		id     = p_id;
		msgType = p_msgType;
		smeId = p_smeId;
		masterId = p_masterId;
		operation = p_operation;
		isMandatory = p_isMandatory;
		requestUId = p_requestUId;
		requestTs = p_requestTs;
		requestMsg = p_requestMsg;
		additionalLong = p_additionalLong;
		additionalBool = p_additionalBool;
		secondLong = p_secondLong;
		comment = p_comment;
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
				        "INSERT INTO " + squote + "SYSTEM_MESSAGE" + equote + " (" +
				        "ID" +
				        ", " + squote + "MSG_TYPE" + equote +
				        ", " + squote + "SME_ID" + equote +
				        ", " + squote + "MASTER_ID" + equote +
				        ", " + squote + "OPERATION" + equote +
				        ", " + squote + "IS_MANDATORY" + equote +
				        ", " + squote + "REQUEST_U_ID" + equote +
				        ", " + squote + "REQUEST_TS" + equote +
				        ", " + squote + "REQUEST_MSG" + equote +
				        ", " + squote + "ADDITIONAL_LONG" + equote +
				        ", " + squote + "ADDITIONAL_BOOL" + equote +
				        ", " + squote + "SECOND_LONG" + equote +
				        ", " + squote + "COMMENT" + equote +
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
				        ", ?" +
				        ", ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "SystemMessage: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setInt(2, msgType.intValue());
			myInsert.setLong (3, smeId.longValue());
			myInsert.setLong (4, masterId.longValue());
			myInsert.setInt(5, operation.intValue());
			myInsert.setInt (6, isMandatory.booleanValue() ? 1 : 0);
			myInsert.setLong (7, requestUId.longValue());
			myInsert.setLong (8, requestTs.longValue());
			if (requestMsg == null)
				myInsert.setNull(9, Types.VARCHAR);
			else
				myInsert.setString(9, requestMsg);
			if (additionalLong == null)
				myInsert.setNull(10, Types.INTEGER);
			else
				myInsert.setLong (10, additionalLong.longValue());
			if (additionalBool == null)
				myInsert.setNull(11, Types.INTEGER);
			else
				myInsert.setInt (11, additionalBool.booleanValue() ? 1 : 0);
			if (secondLong == null)
				myInsert.setNull(12, Types.INTEGER);
			else
				myInsert.setLong (12, secondLong.longValue());
			if (comment == null)
				myInsert.setNull(13, Types.VARCHAR);
			else
				myInsert.setString(13, comment);
			myInsert.setLong (14, creatorUId.longValue());
			myInsert.setLong (15, createTs.longValue());
			myInsert.setLong (16, changerUId.longValue());
			myInsert.setLong (17, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "SystemMessage: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "DELETE FROM " + squote + "SYSTEM_MESSAGE" + equote + " WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "SystemMessage: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "SystemMessage: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE " + squote + "SYSTEM_MESSAGE" + equote + " SET " +
				        "" + squote + "MSG_TYPE" + equote + " = ? " +
				        ", " + squote + "SME_ID" + equote + " = ? " +
				        ", " + squote + "MASTER_ID" + equote + " = ? " +
				        ", " + squote + "OPERATION" + equote + " = ? " +
				        ", " + squote + "IS_MANDATORY" + equote + " = ? " +
				        ", " + squote + "REQUEST_U_ID" + equote + " = ? " +
				        ", " + squote + "REQUEST_TS" + equote + " = ? " +
				        ", " + squote + "REQUEST_MSG" + equote + " = ? " +
				        ", " + squote + "ADDITIONAL_LONG" + equote + " = ? " +
				        ", " + squote + "ADDITIONAL_BOOL" + equote + " = ? " +
				        ", " + squote + "SECOND_LONG" + equote + " = ? " +
				        ", " + squote + "COMMENT" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "SystemMessage: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setInt(1, msgType.intValue());
			myUpdate.setLong (2, smeId.longValue());
			myUpdate.setLong (3, masterId.longValue());
			myUpdate.setInt(4, operation.intValue());
			myUpdate.setInt (5, isMandatory.booleanValue() ? 1 : 0);
			myUpdate.setLong (6, requestUId.longValue());
			myUpdate.setLong (7, requestTs.longValue());
			if (requestMsg == null)
				myUpdate.setNull(8, Types.VARCHAR);
			else
				myUpdate.setString(8, requestMsg);
			if (additionalLong == null)
				myUpdate.setNull(9, Types.INTEGER);
			else
				myUpdate.setLong (9, additionalLong.longValue());
			if (additionalBool == null)
				myUpdate.setNull(10, Types.INTEGER);
			else
				myUpdate.setInt (10, additionalBool.booleanValue() ? 1 : 0);
			if (secondLong == null)
				myUpdate.setNull(11, Types.INTEGER);
			else
				myUpdate.setLong (11, secondLong.longValue());
			if (comment == null)
				myUpdate.setNull(12, Types.VARCHAR);
			else
				myUpdate.setString(12, comment);
			myUpdate.setLong (13, creatorUId.longValue());
			myUpdate.setLong (14, createTs.longValue());
			myUpdate.setLong (15, changerUId.longValue());
			myUpdate.setLong (16, changeTs.longValue());
			myUpdate.setLong(17, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "SystemMessage: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkMsgType(Integer p)
	{
		switch (p.intValue()) {
			case SDMSSystemMessage.APPROVAL:
				return true;
		}
		return false;
	}
	static public boolean checkOperation(Integer p)
	{
		switch (p.intValue()) {
			case SDMSSystemMessage.CANCEL:
			case SDMSSystemMessage.RERUN:
			case SDMSSystemMessage.ENABLE:
			case SDMSSystemMessage.SET_STATE:
			case SDMSSystemMessage.IGN_DEPENDENCY:
			case SDMSSystemMessage.IGN_RESOURCE:
			case SDMSSystemMessage.CLONE:
			case SDMSSystemMessage.SUSPEND:
			case SDMSSystemMessage.CLEAR_WARNING:
			case SDMSSystemMessage.PRIORITY:
			case SDMSSystemMessage.MODIFY_PARAMETER:
			case SDMSSystemMessage.KILL:
			case SDMSSystemMessage.DISABLE:
			case SDMSSystemMessage.RESUME:
			case SDMSSystemMessage.SET_WARNING:
			case SDMSSystemMessage.RENICE:
			case SDMSSystemMessage.NICEVALUE:
			case SDMSSystemMessage.SET_JOB_STATE:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : SystemMessage", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "msgType : " + msgType, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "smeId : " + smeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "masterId : " + masterId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "operation : " + operation, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isMandatory : " + isMandatory, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "requestUId : " + requestUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "requestTs : " + requestTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "requestMsg : " + requestMsg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "additionalLong : " + additionalLong, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "additionalBool : " + additionalBool, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "secondLong : " + secondLong, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "comment : " + comment, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "msgType        : " + msgType + "\n" +
		        indentString + "smeId          : " + smeId + "\n" +
		        indentString + "masterId       : " + masterId + "\n" +
		        indentString + "operation      : " + operation + "\n" +
		        indentString + "isMandatory    : " + isMandatory + "\n" +
		        indentString + "requestUId     : " + requestUId + "\n" +
		        indentString + "requestTs      : " + requestTs + "\n" +
		        indentString + "requestMsg     : " + requestMsg + "\n" +
		        indentString + "additionalLong : " + additionalLong + "\n" +
		        indentString + "additionalBool : " + additionalBool + "\n" +
		        indentString + "secondLong     : " + secondLong + "\n" +
		        indentString + "comment        : " + comment + "\n" +
		        indentString + "creatorUId     : " + creatorUId + "\n" +
		        indentString + "createTs       : " + createTs + "\n" +
		        indentString + "changerUId     : " + changerUId + "\n" +
		        indentString + "changeTs       : " + changeTs + "\n" +
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
