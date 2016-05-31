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

public class SDMSScheduleGeneric extends SDMSObject
	implements Cloneable
{

	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_ownerId = 3;
	public final static int nr_intId = 4;
	public final static int nr_parentId = 5;
	public final static int nr_timeZone = 6;
	public final static int nr_seId = 7;
	public final static int nr_isActive = 8;
	public final static int nr_creatorUId = 9;
	public final static int nr_createTs = 10;
	public final static int nr_changerUId = 11;
	public final static int nr_changeTs = 12;
	public final static int nr_inheritPrivs = 13;

	public static String tableName = SDMSScheduleTableGeneric.tableName;

	protected String name;
	protected Long ownerId;
	protected Long intId;
	protected Long parentId;
	protected String timeZone;
	protected Long seId;
	protected Boolean isActive;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;
	protected Long inheritPrivs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSScheduleGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Long p_ownerId,
	        Long p_intId,
	        Long p_parentId,
	        String p_timeZone,
	        Long p_seId,
	        Boolean p_isActive,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs,
	        Long p_inheritPrivs
	)
	throws SDMSException
	{
		super(env, SDMSScheduleTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Schedule) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		ownerId = p_ownerId;
		intId = p_intId;
		parentId = p_parentId;
		if (p_timeZone != null && p_timeZone.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Schedule) Length of $1 exceeds maximum length $2", "timeZone", "32")
			);
		}
		timeZone = p_timeZone;
		seId = p_seId;
		isActive = p_isActive;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		inheritPrivs = p_inheritPrivs;
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
		SDMSScheduleGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
				);
			}
			o = (SDMSScheduleGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Schedule) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 16);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (ownerId);
	}

	public	void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return;
		SDMSScheduleGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSScheduleGeneric) change(env);
			o.ownerId = p_ownerId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getIntId (SystemEnvironment env)
	throws SDMSException
	{
		return (intId);
	}

	public	void setIntId (SystemEnvironment env, Long p_intId)
	throws SDMSException
	{
		if(p_intId != null && p_intId.equals(intId)) return;
		if(p_intId == null && intId == null) return;
		SDMSScheduleGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
				);
			}
			o = (SDMSScheduleGeneric) change(env);
			o.intId = p_intId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 2);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		return (parentId);
	}

	public	void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		if(p_parentId != null && p_parentId.equals(parentId)) return;
		if(p_parentId == null && parentId == null) return;
		SDMSScheduleGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
				);
			}
			o = (SDMSScheduleGeneric) change(env);
			o.parentId = p_parentId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 20);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getTimeZone (SystemEnvironment env)
	throws SDMSException
	{
		return (timeZone);
	}

	public	void setTimeZone (SystemEnvironment env, String p_timeZone)
	throws SDMSException
	{
		if(timeZone.equals(p_timeZone)) return;
		SDMSScheduleGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSScheduleGeneric) change(env);
		if (p_timeZone != null && p_timeZone.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(Schedule) Length of $1 exceeds maximum length $2", "timeZone", "32")
			);
		}
		o.timeZone = p_timeZone;
		o.changerUId = env.cEnv.euid();
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
		if(p_seId != null && p_seId.equals(seId)) return;
		if(p_seId == null && seId == null) return;
		SDMSScheduleGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
				);
			}
			o = (SDMSScheduleGeneric) change(env);
			o.seId = p_seId;
			o.changerUId = env.cEnv.euid();
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

	public String getIsActiveAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsActive (env);
		final boolean b = v.booleanValue();
		if (b == SDMSSchedule.ACTIVE)
			return "ACTIVE";
		if (b == SDMSSchedule.INACTIVE)
			return "INACTIVE";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Schedule.isActive: $1",
		                          getIsActive (env)));
	}

	public	void setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		if(isActive.equals(p_isActive)) return;
		SDMSScheduleGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSScheduleGeneric) change(env);
		o.isActive = p_isActive;
		o.changerUId = env.cEnv.euid();
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
		SDMSScheduleGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSScheduleGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.euid();
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
		SDMSScheduleGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSScheduleGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.euid();
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
		SDMSScheduleGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSScheduleGeneric) change(env);
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
		SDMSScheduleGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSScheduleGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		return (inheritPrivs);
	}

	public	void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		if(inheritPrivs.equals(p_inheritPrivs)) return;
		SDMSScheduleGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Schedule) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSScheduleGeneric) change(env);
		o.inheritPrivs = p_inheritPrivs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSScheduleGeneric set_ParentIdName (SystemEnvironment env, Long p_parentId, String p_name)
	throws SDMSException
	{
		SDMSScheduleGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(Schedule) Change of system object not allowed")
				);
			}
			o = (SDMSScheduleGeneric) change(env);
			o.parentId = p_parentId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290026",
				                        "(Schedule) Length of $1 exceeds maximum length $2", "inheritPrivs", "64")
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
		return new SDMSSchedule(this);
	}

	protected SDMSScheduleGeneric(Long p_id,
	                              String p_name,
	                              Long p_ownerId,
	                              Long p_intId,
	                              Long p_parentId,
	                              String p_timeZone,
	                              Long p_seId,
	                              Boolean p_isActive,
	                              Long p_creatorUId,
	                              Long p_createTs,
	                              Long p_changerUId,
	                              Long p_changeTs,
	                              Long p_inheritPrivs,
	                              long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		ownerId = p_ownerId;
		intId = p_intId;
		parentId = p_parentId;
		timeZone = p_timeZone;
		seId = p_seId;
		isActive = p_isActive;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		inheritPrivs = p_inheritPrivs;
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
				        "INSERT INTO SCHEDULE (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "INT_ID" + equote +
				        ", " + squote + "PARENT_ID" + equote +
				        ", " + squote + "TIME_ZONE" + equote +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "ACTIVE" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ", " + squote + "INHERIT_PRIVS" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "Schedule: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];

		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setString(2, name);
			myInsert.setLong (3, ownerId.longValue());
			if (intId == null)
				myInsert.setNull(4, Types.INTEGER);
			else
				myInsert.setLong (4, intId.longValue());
			if (parentId == null)
				myInsert.setNull(5, Types.INTEGER);
			else
				myInsert.setLong (5, parentId.longValue());
			myInsert.setString(6, timeZone);
			if (seId == null)
				myInsert.setNull(7, Types.INTEGER);
			else
				myInsert.setLong (7, seId.longValue());
			myInsert.setInt (8, isActive.booleanValue() ? 1 : 0);
			myInsert.setLong (9, creatorUId.longValue());
			myInsert.setLong (10, createTs.longValue());
			myInsert.setLong (11, changerUId.longValue());
			myInsert.setLong (12, changeTs.longValue());
			myInsert.setLong (13, inheritPrivs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "Schedule: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM SCHEDULE WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "Schedule: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "Schedule: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE SCHEDULE SET " +
				        "" + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "OWNER_ID" + equote + " = ? " +
				        ", " + squote + "INT_ID" + equote + " = ? " +
				        ", " + squote + "PARENT_ID" + equote + " = ? " +
				        ", " + squote + "TIME_ZONE" + equote + " = ? " +
				        ", " + squote + "SE_ID" + equote + " = ? " +
				        ", " + squote + "ACTIVE" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        ", " + squote + "INHERIT_PRIVS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "Schedule: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setString(1, name);
			myUpdate.setLong (2, ownerId.longValue());
			if (intId == null)
				myUpdate.setNull(3, Types.INTEGER);
			else
				myUpdate.setLong (3, intId.longValue());
			if (parentId == null)
				myUpdate.setNull(4, Types.INTEGER);
			else
				myUpdate.setLong (4, parentId.longValue());
			myUpdate.setString(5, timeZone);
			if (seId == null)
				myUpdate.setNull(6, Types.INTEGER);
			else
				myUpdate.setLong (6, seId.longValue());
			myUpdate.setInt (7, isActive.booleanValue() ? 1 : 0);
			myUpdate.setLong (8, creatorUId.longValue());
			myUpdate.setLong (9, createTs.longValue());
			myUpdate.setLong (10, changerUId.longValue());
			myUpdate.setLong (11, changeTs.longValue());
			myUpdate.setLong (12, inheritPrivs.longValue());
			myUpdate.setLong(13, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "Schedule: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkIsActive(Boolean p)
	{
		if(p.booleanValue() == SDMSSchedule.ACTIVE) return true;
		if(p.booleanValue() == SDMSSchedule.INACTIVE) return true;
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : Schedule", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "intId : " + intId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "parentId : " + parentId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "timeZone : " + timeZone, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isActive : " + isActive, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "inheritPrivs : " + inheritPrivs, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "name         : " + name + "\n" +
		        indentString + "ownerId      : " + ownerId + "\n" +
		        indentString + "intId        : " + intId + "\n" +
		        indentString + "parentId     : " + parentId + "\n" +
		        indentString + "timeZone     : " + timeZone + "\n" +
		        indentString + "seId         : " + seId + "\n" +
		        indentString + "isActive     : " + isActive + "\n" +
		        indentString + "creatorUId   : " + creatorUId + "\n" +
		        indentString + "createTs     : " + createTs + "\n" +
		        indentString + "changerUId   : " + changerUId + "\n" +
		        indentString + "changeTs     : " + changeTs + "\n" +
		        indentString + "inheritPrivs : " + inheritPrivs + "\n" +
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
