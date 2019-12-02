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

public class SDMSObjectCommentGeneric extends SDMSObject
	implements Cloneable
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

	public final static int nr_id = 1;
	public final static int nr_objectId = 2;
	public final static int nr_objectType = 3;
	public final static int nr_infoType = 4;
	public final static int nr_sequenceNumber = 5;
	public final static int nr_tag = 6;
	public final static int nr_description = 7;
	public final static int nr_creatorUId = 8;
	public final static int nr_createTs = 9;
	public final static int nr_changerUId = 10;
	public final static int nr_changeTs = 11;

	public static String tableName = SDMSObjectCommentTableGeneric.tableName;

	protected Long objectId;
	protected Integer objectType;
	protected Integer infoType;
	protected Integer sequenceNumber;
	protected String tag;
	protected String description;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSObjectCommentGeneric(
	        SystemEnvironment env,
	        Long p_objectId,
	        Integer p_objectType,
	        Integer p_infoType,
	        Integer p_sequenceNumber,
	        String p_tag,
	        String p_description,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSObjectCommentTableGeneric.table);
		objectId = p_objectId;
		objectType = p_objectType;
		infoType = p_infoType;
		sequenceNumber = p_sequenceNumber;
		if (p_tag != null && p_tag.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ObjectComment) Length of $1 exceeds maximum length $2", "tag", "64")
			);
		}
		tag = p_tag;
		if (p_description != null && p_description.length() > 1900) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ObjectComment) Length of $1 exceeds maximum length $2", "description", "1900")
			);
		}
		description = p_description;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getObjectId (SystemEnvironment env)
	throws SDMSException
	{
		return (objectId);
	}

	public	void setObjectId (SystemEnvironment env, Long p_objectId)
	throws SDMSException
	{
		if(objectId.equals(p_objectId)) return;
		SDMSObjectCommentGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
				);
			}
			o = (SDMSObjectCommentGeneric) change(env);
			o.objectId = p_objectId;
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
			case SDMSObjectComment.JOB_DEFINITION:
				return "JOB_DEFINITION";
			case SDMSObjectComment.EXIT_STATE_DEFINITION:
				return "EXIT_STATE_DEFINITION";
			case SDMSObjectComment.EXIT_STATE_PROFILE:
				return "EXIT_STATE_PROFILE";
			case SDMSObjectComment.EXIT_STATE_MAPPING:
				return "EXIT_STATE_MAPPING";
			case SDMSObjectComment.EXIT_STATE_TRANSLATION:
				return "EXIT_STATE_TRANSLATION";
			case SDMSObjectComment.FOLDER:
				return "FOLDER";
			case SDMSObjectComment.SCOPE:
				return "SCOPE";
			case SDMSObjectComment.NAMED_RESOURCE:
				return "NAMED_RESOURCE";
			case SDMSObjectComment.NICE_PROFILE:
				return "NICE_PROFILE";
			case SDMSObjectComment.RESOURCE:
				return "RESOURCE";
			case SDMSObjectComment.ENVIRONMENT:
				return "ENVIRONMENT";
			case SDMSObjectComment.FOOTPRINT:
				return "FOOTPRINT";
			case SDMSObjectComment.RESOURCE_STATE_DEFINITION:
				return "RESOURCE_STATE_DEFINITION";
			case SDMSObjectComment.RESOURCE_STATE_PROFILE:
				return "RESOURCE_STATE_PROFILE";
			case SDMSObjectComment.RESOURCE_STATE_MAPPING:
				return "RESOURCE_STATE_MAPPING";
			case SDMSObjectComment.USER:
				return "USER";
			case SDMSObjectComment.TRIGGER:
				return "TRIGGER";
			case SDMSObjectComment.JOB:
				return "JOB";
			case SDMSObjectComment.EVENT:
				return "EVENT";
			case SDMSObjectComment.INTERVAL:
				return "INTERVAL";
			case SDMSObjectComment.SCHEDULE:
				return "SCHEDULE";
			case SDMSObjectComment.SCHEDULED_EVENT:
				return "SCHEDULED_EVENT";
			case SDMSObjectComment.GROUP:
				return "GROUP";
			case SDMSObjectComment.PARAMETER:
				return "PARAMETER";
			case SDMSObjectComment.POOL:
				return "POOL";
			case SDMSObjectComment.DISTRIBUTION:
				return "DISTRIBUTION";
			case SDMSObjectComment.WATCH_TYPE:
				return "WATCH_TYPE";
			case SDMSObjectComment.OBJECT_MONITOR:
				return "OBJECT_MONITOR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ObjectComment.objectType: $1",
		                          getObjectType (env)));
	}

	public	void setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		if(objectType.equals(p_objectType)) return;
		SDMSObjectCommentGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
				);
			}
			o = (SDMSObjectCommentGeneric) change(env);
			o.objectType = p_objectType;
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

	public Integer getInfoType (SystemEnvironment env)
	throws SDMSException
	{
		return (infoType);
	}

	public String getInfoTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getInfoType (env);
		switch (v.intValue()) {
			case SDMSObjectComment.TEXT:
				return "TEXT";
			case SDMSObjectComment.URL:
				return "URL";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ObjectComment.infoType: $1",
		                          getInfoType (env)));
	}

	public	void setInfoType (SystemEnvironment env, Integer p_infoType)
	throws SDMSException
	{
		if(infoType.equals(p_infoType)) return;
		SDMSObjectCommentGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
		o.infoType = p_infoType;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getSequenceNumber (SystemEnvironment env)
	throws SDMSException
	{
		return (sequenceNumber);
	}

	public	void setSequenceNumber (SystemEnvironment env, Integer p_sequenceNumber)
	throws SDMSException
	{
		if(sequenceNumber.equals(p_sequenceNumber)) return;
		SDMSObjectCommentGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
		o.sequenceNumber = p_sequenceNumber;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getTag (SystemEnvironment env)
	throws SDMSException
	{
		return (tag);
	}

	public	void setTag (SystemEnvironment env, String p_tag)
	throws SDMSException
	{
		if(p_tag != null && p_tag.equals(tag)) return;
		if(p_tag == null && tag == null) return;
		SDMSObjectCommentGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
		if (p_tag != null && p_tag.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(ObjectComment) Length of $1 exceeds maximum length $2", "tag", "64")
			);
		}
		o.tag = p_tag;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getDescription (SystemEnvironment env)
	throws SDMSException
	{
		return (description);
	}

	public	void setDescription (SystemEnvironment env, String p_description)
	throws SDMSException
	{
		if(description.equals(p_description)) return;
		SDMSObjectCommentGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
		if (p_description != null && p_description.length() > 1900) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(ObjectComment) Length of $1 exceeds maximum length $2", "description", "1900")
			);
		}
		o.description = p_description;
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
		SDMSObjectCommentGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
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
		SDMSObjectCommentGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ObjectComment) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
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
		SDMSObjectCommentGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
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
		SDMSObjectCommentGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSObjectCommentGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return new SDMSObjectComment(this);
	}

	protected SDMSObjectCommentGeneric(Long p_id,
	                                   Long p_objectId,
	                                   Integer p_objectType,
	                                   Integer p_infoType,
	                                   Integer p_sequenceNumber,
	                                   String p_tag,
	                                   String p_description,
	                                   Long p_creatorUId,
	                                   Long p_createTs,
	                                   Long p_changerUId,
	                                   Long p_changeTs,
	                                   long p_validFrom, long p_validTo)
	{
		id     = p_id;
		objectId = p_objectId;
		objectType = p_objectType;
		infoType = p_infoType;
		sequenceNumber = p_sequenceNumber;
		tag = p_tag;
		description = p_description;
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
				        "INSERT INTO " + squote + "OBJECT_COMMENT" + equote + " (" +
				        "ID" +
				        ", " + squote + "OBJECT_ID" + equote +
				        ", " + squote + "OBJECT_TYPE" + equote +
				        ", " + squote + "INFO_TYPE" + equote +
				        ", " + squote + "SEQUENCE_NUMBER" + equote +
				        ", " + squote + "TAG" + equote +
				        ", " + squote + "DESCRIPTION" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "ObjectComment: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, objectId.longValue());
			myInsert.setInt(3, objectType.intValue());
			myInsert.setInt(4, infoType.intValue());
			myInsert.setInt(5, sequenceNumber.intValue());
			if (tag == null)
				myInsert.setNull(6, Types.VARCHAR);
			else
				myInsert.setString(6, tag);
			myInsert.setString(7, description);
			myInsert.setLong (8, creatorUId.longValue());
			myInsert.setLong (9, createTs.longValue());
			myInsert.setLong (10, changerUId.longValue());
			myInsert.setLong (11, changeTs.longValue());
			myInsert.setLong(12, env.tx.versionId);
			myInsert.setLong(13, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "ObjectComment: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE " + squote + "OBJECT_COMMENT" + equote +
				        " SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181955", "ObjectComment : $1\n$2", stmt, sqle.toString()));
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
			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "ObjectComment: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkObjectType(Integer p)
	{
		switch (p.intValue()) {
			case SDMSObjectComment.JOB_DEFINITION:
			case SDMSObjectComment.EXIT_STATE_DEFINITION:
			case SDMSObjectComment.EXIT_STATE_PROFILE:
			case SDMSObjectComment.EXIT_STATE_MAPPING:
			case SDMSObjectComment.EXIT_STATE_TRANSLATION:
			case SDMSObjectComment.FOLDER:
			case SDMSObjectComment.SCOPE:
			case SDMSObjectComment.NAMED_RESOURCE:
			case SDMSObjectComment.NICE_PROFILE:
			case SDMSObjectComment.RESOURCE:
			case SDMSObjectComment.ENVIRONMENT:
			case SDMSObjectComment.FOOTPRINT:
			case SDMSObjectComment.RESOURCE_STATE_DEFINITION:
			case SDMSObjectComment.RESOURCE_STATE_PROFILE:
			case SDMSObjectComment.RESOURCE_STATE_MAPPING:
			case SDMSObjectComment.USER:
			case SDMSObjectComment.TRIGGER:
			case SDMSObjectComment.JOB:
			case SDMSObjectComment.EVENT:
			case SDMSObjectComment.INTERVAL:
			case SDMSObjectComment.SCHEDULE:
			case SDMSObjectComment.SCHEDULED_EVENT:
			case SDMSObjectComment.GROUP:
			case SDMSObjectComment.PARAMETER:
			case SDMSObjectComment.POOL:
			case SDMSObjectComment.DISTRIBUTION:
			case SDMSObjectComment.WATCH_TYPE:
			case SDMSObjectComment.OBJECT_MONITOR:
				return true;
		}
		return false;
	}
	static public boolean checkInfoType(Integer p)
	{
		switch (p.intValue()) {
			case SDMSObjectComment.TEXT:
			case SDMSObjectComment.URL:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ObjectComment", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "objectId : " + objectId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "objectType : " + objectType, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "infoType : " + infoType, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "sequenceNumber : " + sequenceNumber, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "tag : " + tag, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "description : " + description, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "objectId       : " + objectId + "\n" +
		        indentString + "objectType     : " + objectType + "\n" +
		        indentString + "infoType       : " + infoType + "\n" +
		        indentString + "sequenceNumber : " + sequenceNumber + "\n" +
		        indentString + "tag            : " + tag + "\n" +
		        indentString + "description    : " + description + "\n" +
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
