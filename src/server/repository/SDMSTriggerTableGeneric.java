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

public class SDMSTriggerTableGeneric extends SDMSTable
{

	public final static String tableName = "TRIGGER_DEFINITION";
	public static SDMSTriggerTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "FIRE_ID"
		, "OBJECT_TYPE"
		, "SE_ID"
		, "MAIN_SE_ID"
		, "PARENT_SE_ID"
		, "IS_ACTIVE"
		, "ACTION"
		, "TYPE"
		, "IS_MASTER"
		, "IS_SUSPEND"
		, "IS_CREATE"
		, "IS_CHANGE"
		, "IS_DELETE"
		, "IS_GROUP"
		, "RESUME_AT"
		, "RESUME_IN"
		, "RESUME_BASE"
		, "IS_WARN_ON_LIMIT"
		, "MAX_RETRY"
		, "SUBMIT_OWNER_ID"
		, "CONDITION"
		, "CHECK_AMOUNT"
		, "CHECK_BASE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_fireId;
	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_mainSeId;
	public static SDMSIndex idx_parentSeId;
	public static SDMSIndex idx_submitOwnerId;
	public static SDMSIndex idx_fireId_type;
	public static SDMSIndex idx_fireId_name;

	public SDMSTriggerTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "Trigger"));
		}
		table = (SDMSTriggerTable) this;
		SDMSTriggerTableGeneric.table = (SDMSTriggerTable) this;
		isVersioned = true;
		idx_fireId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_mainSeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_parentSeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_submitOwnerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_fireId_type = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_fireId_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSTrigger create(SystemEnvironment env
	                          ,String p_name
	                          ,Long p_fireId
	                          ,Integer p_objectType
	                          ,Long p_seId
	                          ,Long p_mainSeId
	                          ,Long p_parentSeId
	                          ,Boolean p_isActive
	                          ,Integer p_action
	                          ,Integer p_type
	                          ,Boolean p_isMaster
	                          ,Boolean p_isSuspend
	                          ,Boolean p_isCreate
	                          ,Boolean p_isChange
	                          ,Boolean p_isDelete
	                          ,Boolean p_isGroup
	                          ,String p_resumeAt
	                          ,Integer p_resumeIn
	                          ,Integer p_resumeBase
	                          ,Boolean p_isWarnOnLimit
	                          ,Integer p_maxRetry
	                          ,Long p_submitOwnerId
	                          ,String p_condition
	                          ,Integer p_checkAmount
	                          ,Integer p_checkBase
	                         )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "Trigger"));
		}
		validate(env
		         , p_name
		         , p_fireId
		         , p_objectType
		         , p_seId
		         , p_mainSeId
		         , p_parentSeId
		         , p_isActive
		         , p_action
		         , p_type
		         , p_isMaster
		         , p_isSuspend
		         , p_isCreate
		         , p_isChange
		         , p_isDelete
		         , p_isGroup
		         , p_resumeAt
		         , p_resumeIn
		         , p_resumeBase
		         , p_isWarnOnLimit
		         , p_maxRetry
		         , p_submitOwnerId
		         , p_condition
		         , p_checkAmount
		         , p_checkBase
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSTriggerGeneric o = new SDMSTriggerGeneric(env
		                , p_name
		                , p_fireId
		                , p_objectType
		                , p_seId
		                , p_mainSeId
		                , p_parentSeId
		                , p_isActive
		                , p_action
		                , p_type
		                , p_isMaster
		                , p_isSuspend
		                , p_isCreate
		                , p_isChange
		                , p_isDelete
		                , p_isGroup
		                , p_resumeAt
		                , p_resumeIn
		                , p_resumeBase
		                , p_isWarnOnLimit
		                , p_maxRetry
		                , p_submitOwnerId
		                , p_condition
		                , p_checkAmount
		                , p_checkBase
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                             );

		SDMSTrigger p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSTrigger)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSTrigger)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSTrigger p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_fireId
	                        ,Integer p_objectType
	                        ,Long p_seId
	                        ,Long p_mainSeId
	                        ,Long p_parentSeId
	                        ,Boolean p_isActive
	                        ,Integer p_action
	                        ,Integer p_type
	                        ,Boolean p_isMaster
	                        ,Boolean p_isSuspend
	                        ,Boolean p_isCreate
	                        ,Boolean p_isChange
	                        ,Boolean p_isDelete
	                        ,Boolean p_isGroup
	                        ,String p_resumeAt
	                        ,Integer p_resumeIn
	                        ,Integer p_resumeBase
	                        ,Boolean p_isWarnOnLimit
	                        ,Integer p_maxRetry
	                        ,Long p_submitOwnerId
	                        ,String p_condition
	                        ,Integer p_checkAmount
	                        ,Integer p_checkBase
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSTriggerGeneric.checkObjectType(p_objectType))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Trigger: $1 $2", "objectType", p_objectType));
		if (!SDMSTriggerGeneric.checkAction(p_action))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Trigger: $1 $2", "action", p_action));
		if (!SDMSTriggerGeneric.checkType(p_type))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Trigger: $1 $2", "type", p_type));
		if (!SDMSTriggerGeneric.checkIsMaster(p_isMaster))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Trigger: $1 $2", "isMaster", p_isMaster));
		if (!SDMSTriggerGeneric.checkIsSuspend(p_isSuspend))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Trigger: $1 $2", "isSuspend", p_isSuspend));
		if (!SDMSTriggerGeneric.checkResumeBase(p_resumeBase))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Trigger: $1 $2", "resumeBase", p_resumeBase));
		if (!SDMSTriggerGeneric.checkCheckBase(p_checkBase))

			throw new FatalException(new SDMSMessage(env, "01110182023", "Trigger: $1 $2", "checkBase", p_checkBase));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		String name;
		Long fireId;
		Integer objectType;
		Long seId;
		Long mainSeId;
		Long parentSeId;
		Boolean isActive;
		Integer action;
		Integer type;
		Boolean isMaster;
		Boolean isSuspend;
		Boolean isCreate;
		Boolean isChange;
		Boolean isDelete;
		Boolean isGroup;
		String resumeAt;
		Integer resumeIn;
		Integer resumeBase;
		Boolean isWarnOnLimit;
		Integer maxRetry;
		Long submitOwnerId;
		String condition;
		Integer checkAmount;
		Integer checkBase;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			fireId = new Long (r.getLong(3));
			objectType = new Integer (r.getInt(4));
			seId = new Long (r.getLong(5));
			mainSeId = new Long (r.getLong(6));
			if (r.wasNull()) mainSeId = null;
			parentSeId = new Long (r.getLong(7));
			if (r.wasNull()) parentSeId = null;
			isActive = new Boolean ((r.getInt(8) == 0 ? false : true));
			action = new Integer (r.getInt(9));
			type = new Integer (r.getInt(10));
			isMaster = new Boolean ((r.getInt(11) == 0 ? false : true));
			isSuspend = new Boolean ((r.getInt(12) == 0 ? false : true));
			isCreate = new Boolean ((r.getInt(13) == 0 ? false : true));
			if (r.wasNull()) isCreate = null;
			isChange = new Boolean ((r.getInt(14) == 0 ? false : true));
			if (r.wasNull()) isChange = null;
			isDelete = new Boolean ((r.getInt(15) == 0 ? false : true));
			if (r.wasNull()) isDelete = null;
			isGroup = new Boolean ((r.getInt(16) == 0 ? false : true));
			if (r.wasNull()) isGroup = null;
			resumeAt = r.getString(17);
			if (r.wasNull()) resumeAt = null;
			resumeIn = new Integer (r.getInt(18));
			if (r.wasNull()) resumeIn = null;
			resumeBase = new Integer (r.getInt(19));
			if (r.wasNull()) resumeBase = null;
			isWarnOnLimit = new Boolean ((r.getInt(20) == 0 ? false : true));
			maxRetry = new Integer (r.getInt(21));
			submitOwnerId = new Long (r.getLong(22));
			if (r.wasNull()) submitOwnerId = null;
			condition = r.getString(23);
			if (r.wasNull()) condition = null;
			checkAmount = new Integer (r.getInt(24));
			if (r.wasNull()) checkAmount = null;
			checkBase = new Integer (r.getInt(25));
			if (r.wasNull()) checkBase = null;
			creatorUId = new Long (r.getLong(26));
			createTs = new Long (r.getLong(27));
			changerUId = new Long (r.getLong(28));
			changeTs = new Long (r.getLong(29));
			validFrom = r.getLong(30);
			validTo = r.getLong(31);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "Trigger: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSTriggerGeneric(id,
		                              name,
		                              fireId,
		                              objectType,
		                              seId,
		                              mainSeId,
		                              parentSeId,
		                              isActive,
		                              action,
		                              type,
		                              isMaster,
		                              isSuspend,
		                              isCreate,
		                              isChange,
		                              isDelete,
		                              isGroup,
		                              resumeAt,
		                              resumeIn,
		                              resumeBase,
		                              isWarnOnLimit,
		                              maxRetry,
		                              submitOwnerId,
		                              condition,
		                              checkAmount,
		                              checkBase,
		                              creatorUId,
		                              createTs,
		                              changerUId,
		                              changeTs,
		                              validFrom, validTo);
	}

	protected void loadTable(SystemEnvironment env)
	throws SQLException, SDMSException
	{
		int read = 0;
		int loaded = 0;

		final String driverName = env.dbConnection.getMetaData().getDriverName();
		final boolean postgres = driverName.startsWith("PostgreSQL");
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
		Statement stmt = env.dbConnection.createStatement();

		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   tableName() + ".ID" +
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
		                                   ", VALID_FROM, VALID_TO " +
		                                   " FROM " + tableName() +
		                                   " WHERE VALID_TO >= " + (postgres ?
		                                                   "CAST (\'" + env.lowestActiveVersion + "\' AS DECIMAL)" :
		                                                   "" + env.lowestActiveVersion) +
		                                   ""						  );
		while(rset.next()) {
			if(loadObject(env, rset)) ++loaded;
			++read;
		}
		stmt.close();
		SDMSThread.doTrace(null, "Read " + read + ", Loaded " + loaded + " rows for " + tableName(), SDMSThread.SEVERITY_INFO);
	}

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_fireId.put(env, ((SDMSTriggerGeneric) o).fireId, o);
		idx_seId.put(env, ((SDMSTriggerGeneric) o).seId, o);
		idx_mainSeId.put(env, ((SDMSTriggerGeneric) o).mainSeId, o);
		idx_parentSeId.put(env, ((SDMSTriggerGeneric) o).parentSeId, o);
		idx_submitOwnerId.put(env, ((SDMSTriggerGeneric) o).submitOwnerId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerGeneric) o).fireId);
		k.add(((SDMSTriggerGeneric) o).type);
		idx_fireId_type.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSTriggerGeneric) o).fireId);
		k.add(((SDMSTriggerGeneric) o).name);
		idx_fireId_name.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_fireId.remove(env, ((SDMSTriggerGeneric) o).fireId, o);
		idx_seId.remove(env, ((SDMSTriggerGeneric) o).seId, o);
		idx_mainSeId.remove(env, ((SDMSTriggerGeneric) o).mainSeId, o);
		idx_parentSeId.remove(env, ((SDMSTriggerGeneric) o).parentSeId, o);
		idx_submitOwnerId.remove(env, ((SDMSTriggerGeneric) o).submitOwnerId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerGeneric) o).fireId);
		k.add(((SDMSTriggerGeneric) o).type);
		idx_fireId_type.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSTriggerGeneric) o).fireId);
		k.add(((SDMSTriggerGeneric) o).name);
		idx_fireId_name.remove(env, k, o);
	}

	public static SDMSTrigger getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTrigger) table.get(env, id);
	}

	public static SDMSTrigger getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSTrigger) table.get(env, id, version);
	}

	public static SDMSTrigger idx_fireId_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSTrigger)  SDMSTriggerTableGeneric.idx_fireId_name.getUnique(env, key);
	}

	public static SDMSTrigger idx_fireId_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSTrigger)  SDMSTriggerTableGeneric.idx_fireId_name.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
	public String[] columnNames()
	{
		return columnNames;
	}
}
