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

public class SDMSGrantGeneric extends SDMSObject
	implements Cloneable
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

	public final static int nr_id = 1;
	public final static int nr_objectId = 2;
	public final static int nr_gId = 3;
	public final static int nr_objectType = 4;
	public final static int nr_privs = 5;
	public final static int nr_deleteVersion = 6;
	public final static int nr_creatorUId = 7;
	public final static int nr_createTs = 8;
	public final static int nr_changerUId = 9;
	public final static int nr_changeTs = 10;

	public static String tableName = SDMSGrantTableGeneric.tableName;

	protected Long objectId;
	protected Long gId;
	protected Integer objectType;
	protected Long privs;
	protected Long deleteVersion;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSGrantGeneric(
	        SystemEnvironment env,
	        Long p_objectId,
	        Long p_gId,
	        Integer p_objectType,
	        Long p_privs,
	        Long p_deleteVersion,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSGrantTableGeneric.table);
		objectId = p_objectId;
		gId = p_gId;
		objectType = p_objectType;
		privs = p_privs;
		deleteVersion = p_deleteVersion;
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
		SDMSGrantGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Grant) Change of system object not allowed")
				);
			}
			o = (SDMSGrantGeneric) change(env);
			o.objectId = p_objectId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 5);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getGId (SystemEnvironment env)
	throws SDMSException
	{
		return (gId);
	}

	public	void setGId (SystemEnvironment env, Long p_gId)
	throws SDMSException
	{
		if(gId.equals(p_gId)) return;
		SDMSGrantGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Grant) Change of system object not allowed")
				);
			}
			o = (SDMSGrantGeneric) change(env);
			o.gId = p_gId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 6);
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
			case SDMSGrant.ENVIRONMENT:
				return "ENVIRONMENT";
			case SDMSGrant.EVENT:
				return "EVENT";
			case SDMSGrant.FOLDER:
				return "FOLDER";
			case SDMSGrant.INTERVAL:
				return "INTERVAL";
			case SDMSGrant.JOB:
				return "JOB";
			case SDMSGrant.JOB_DEFINITION:
				return "JOB_DEFINITION";
			case SDMSGrant.NAMED_RESOURCE:
				return "NAMED_RESOURCE";
			case SDMSGrant.SCHEDULE:
				return "SCHEDULE";
			case SDMSGrant.SCHEDULED_EVENT:
				return "SCHEDULED_EVENT";
			case SDMSGrant.SCOPE:
				return "SCOPE";
			case SDMSGrant.GROUP:
				return "GROUP";
			case SDMSGrant.RESOURCE:
				return "RESOURCE";
			case SDMSGrant.EXIT_STATE_DEFINITION:
				return "EXIT_STATE_DEFINITION";
			case SDMSGrant.EXIT_STATE_PROFILE:
				return "EXIT_STATE_PROFILE";
			case SDMSGrant.EXIT_STATE_MAPPING:
				return "EXIT_STATE_MAPPING";
			case SDMSGrant.EXIT_STATE_TRANSLATION:
				return "EXIT_STATE_TRANSLATION";
			case SDMSGrant.RESOURCE_STATE_DEFINITION:
				return "RESOURCE_STATE_DEFINITION";
			case SDMSGrant.RESOURCE_STATE_PROFILE:
				return "RESOURCE_STATE_PROFILE";
			case SDMSGrant.RESOURCE_STATE_MAPPING:
				return "RESOURCE_STATE_MAPPING";
			case SDMSGrant.FOOTPRINT:
				return "FOOTPRINT";
			case SDMSGrant.USER:
				return "USER";
			case SDMSGrant.OBJECT_MONITOR:
				return "OBJECT_MONITOR";
			case SDMSGrant.SYSTEM:
				return "SYSTEM";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Grant.objectType: $1",
		                          getObjectType (env)));
	}

	public	void setObjectType (SystemEnvironment env, Integer p_objectType)
	throws SDMSException
	{
		if(objectType.equals(p_objectType)) return;
		SDMSGrantGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Grant) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSGrantGeneric) change(env);
		o.objectType = p_objectType;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getPrivs (SystemEnvironment env)
	throws SDMSException
	{
		return (privs);
	}

	public	void setPrivs (SystemEnvironment env, Long p_privs)
	throws SDMSException
	{
		if(privs.equals(p_privs)) return;
		SDMSGrantGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Grant) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSGrantGeneric) change(env);
		o.privs = p_privs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getDeleteVersion (SystemEnvironment env)
	throws SDMSException
	{
		return (deleteVersion);
	}

	public	void setDeleteVersion (SystemEnvironment env, Long p_deleteVersion)
	throws SDMSException
	{
		if(p_deleteVersion != null && p_deleteVersion.equals(deleteVersion)) return;
		if(p_deleteVersion == null && deleteVersion == null) return;
		SDMSGrantGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Grant) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSGrantGeneric) change(env);
		o.deleteVersion = p_deleteVersion;
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
		SDMSGrantGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Grant) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSGrantGeneric) change(env);
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
		SDMSGrantGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Grant) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSGrantGeneric) change(env);
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
		SDMSGrantGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSGrantGeneric) change(env);
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
		SDMSGrantGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSGrantGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSGrantGeneric set_ObjectIdGId (SystemEnvironment env, Long p_objectId, Long p_gId)
	throws SDMSException
	{
		SDMSGrantGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(Grant) Change of system object not allowed")
				);
			}
			o = (SDMSGrantGeneric) change(env);
			o.objectId = p_objectId;
			o.gId = p_gId;
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
		return SDMSGrant.getProxy(sysEnv, this);
	}

	protected SDMSGrantGeneric(Long p_id,
	                           Long p_objectId,
	                           Long p_gId,
	                           Integer p_objectType,
	                           Long p_privs,
	                           Long p_deleteVersion,
	                           Long p_creatorUId,
	                           Long p_createTs,
	                           Long p_changerUId,
	                           Long p_changeTs,
	                           long p_validFrom, long p_validTo)
	{
		id     = p_id;
		objectId = p_objectId;
		gId = p_gId;
		objectType = p_objectType;
		privs = p_privs;
		deleteVersion = p_deleteVersion;
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
				        "INSERT INTO GRANTS (" +
				        "ID" +
				        ", " + squote + "OBJECT_ID" + equote +
				        ", " + squote + "G_ID" + equote +
				        ", " + squote + "OBJECT_TYPE" + equote +
				        ", " + squote + "PRIVS" + equote +
				        ", " + squote + "DELETE_VERSION" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "Grant: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, objectId.longValue());
			myInsert.setLong (3, gId.longValue());
			myInsert.setInt(4, objectType.intValue());
			myInsert.setLong (5, privs.longValue());
			if (deleteVersion == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setLong (6, deleteVersion.longValue());
			myInsert.setLong (7, creatorUId.longValue());
			myInsert.setLong (8, createTs.longValue());
			myInsert.setLong (9, changerUId.longValue());
			myInsert.setLong (10, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "Grant: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				stmt =
				        "DELETE FROM GRANTS WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "Grant: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "Grant: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE GRANTS SET " +
				        "" + squote + "OBJECT_ID" + equote + " = ? " +
				        ", " + squote + "G_ID" + equote + " = ? " +
				        ", " + squote + "OBJECT_TYPE" + equote + " = ? " +
				        ", " + squote + "PRIVS" + equote + " = ? " +
				        ", " + squote + "DELETE_VERSION" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "Grant: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, objectId.longValue());
			myUpdate.setLong (2, gId.longValue());
			myUpdate.setInt(3, objectType.intValue());
			myUpdate.setLong (4, privs.longValue());
			if (deleteVersion == null)
				myUpdate.setNull(5, Types.INTEGER);
			else
				myUpdate.setLong (5, deleteVersion.longValue());
			myUpdate.setLong (6, creatorUId.longValue());
			myUpdate.setLong (7, createTs.longValue());
			myUpdate.setLong (8, changerUId.longValue());
			myUpdate.setLong (9, changeTs.longValue());
			myUpdate.setLong(10, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "Grant: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkObjectType(Integer p)
	{
		switch (p.intValue()) {
			case SDMSGrant.ENVIRONMENT:
			case SDMSGrant.EVENT:
			case SDMSGrant.FOLDER:
			case SDMSGrant.INTERVAL:
			case SDMSGrant.JOB:
			case SDMSGrant.JOB_DEFINITION:
			case SDMSGrant.NAMED_RESOURCE:
			case SDMSGrant.SCHEDULE:
			case SDMSGrant.SCHEDULED_EVENT:
			case SDMSGrant.SCOPE:
			case SDMSGrant.GROUP:
			case SDMSGrant.RESOURCE:
			case SDMSGrant.EXIT_STATE_DEFINITION:
			case SDMSGrant.EXIT_STATE_PROFILE:
			case SDMSGrant.EXIT_STATE_MAPPING:
			case SDMSGrant.EXIT_STATE_TRANSLATION:
			case SDMSGrant.RESOURCE_STATE_DEFINITION:
			case SDMSGrant.RESOURCE_STATE_PROFILE:
			case SDMSGrant.RESOURCE_STATE_MAPPING:
			case SDMSGrant.FOOTPRINT:
			case SDMSGrant.USER:
			case SDMSGrant.OBJECT_MONITOR:
			case SDMSGrant.SYSTEM:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : Grant", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "objectId : " + objectId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "gId : " + gId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "objectType : " + objectType, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "privs : " + privs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "deleteVersion : " + deleteVersion, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "objectId      : " + objectId + "\n" +
		        indentString + "gId           : " + gId + "\n" +
		        indentString + "objectType    : " + objectType + "\n" +
		        indentString + "privs         : " + privs + "\n" +
		        indentString + "deleteVersion : " + deleteVersion + "\n" +
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
