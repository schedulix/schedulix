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

public class SDMSIntervalDispatcherGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_intId = 2;
	public final static int nr_seqNo = 3;
	public final static int nr_name = 4;
	public final static int nr_selectIntId = 5;
	public final static int nr_filterIntId = 6;
	public final static int nr_isEnabled = 7;
	public final static int nr_isActive = 8;
	public final static int nr_creatorUId = 9;
	public final static int nr_createTs = 10;
	public final static int nr_changerUId = 11;
	public final static int nr_changeTs = 12;

	public static String tableName = SDMSIntervalDispatcherTableGeneric.tableName;

	protected Long intId;
	protected Integer seqNo;
	protected String name;
	protected Long selectIntId;
	protected Long filterIntId;
	protected Boolean isEnabled;
	protected Boolean isActive;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSIntervalDispatcherGeneric(
	        SystemEnvironment env,
	        Long p_intId,
	        Integer p_seqNo,
	        String p_name,
	        Long p_selectIntId,
	        Long p_filterIntId,
	        Boolean p_isEnabled,
	        Boolean p_isActive,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSIntervalDispatcherTableGeneric.table);
		intId = p_intId;
		seqNo = p_seqNo;
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(IntervalDispatcher) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		selectIntId = p_selectIntId;
		filterIntId = p_filterIntId;
		isEnabled = p_isEnabled;
		isActive = p_isActive;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getIntId (SystemEnvironment env)
	throws SDMSException
	{
		return (intId);
	}

	public	void setIntId (SystemEnvironment env, Long p_intId)
	throws SDMSException
	{
		if(intId.equals(p_intId)) return;
		SDMSIntervalDispatcherGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalDispatcherGeneric) change(env);
			o.intId = p_intId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 9);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getSeqNo (SystemEnvironment env)
	throws SDMSException
	{
		return (seqNo);
	}

	public	void setSeqNo (SystemEnvironment env, Integer p_seqNo)
	throws SDMSException
	{
		if(seqNo.equals(p_seqNo)) return;
		SDMSIntervalDispatcherGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalDispatcherGeneric) change(env);
			o.seqNo = p_seqNo;
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

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		return (name);
	}

	public	void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return;
		SDMSIntervalDispatcherGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSIntervalDispatcherGeneric) change(env);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(IntervalDispatcher) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		o.name = p_name;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSelectIntId (SystemEnvironment env)
	throws SDMSException
	{
		return (selectIntId);
	}

	public	void setSelectIntId (SystemEnvironment env, Long p_selectIntId)
	throws SDMSException
	{
		if(p_selectIntId != null && p_selectIntId.equals(selectIntId)) return;
		if(p_selectIntId == null && selectIntId == null) return;
		SDMSIntervalDispatcherGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalDispatcherGeneric) change(env);
			o.selectIntId = p_selectIntId;
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

	public Long getFilterIntId (SystemEnvironment env)
	throws SDMSException
	{
		return (filterIntId);
	}

	public	void setFilterIntId (SystemEnvironment env, Long p_filterIntId)
	throws SDMSException
	{
		if(p_filterIntId != null && p_filterIntId.equals(filterIntId)) return;
		if(p_filterIntId == null && filterIntId == null) return;
		SDMSIntervalDispatcherGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalDispatcherGeneric) change(env);
			o.filterIntId = p_filterIntId;
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

	public Boolean getIsEnabled (SystemEnvironment env)
	throws SDMSException
	{
		return (isEnabled);
	}

	public	void setIsEnabled (SystemEnvironment env, Boolean p_isEnabled)
	throws SDMSException
	{
		if(isEnabled.equals(p_isEnabled)) return;
		SDMSIntervalDispatcherGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSIntervalDispatcherGeneric) change(env);
		o.isEnabled = p_isEnabled;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
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
		SDMSIntervalDispatcherGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSIntervalDispatcherGeneric) change(env);
		o.isActive = p_isActive;
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
		SDMSIntervalDispatcherGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSIntervalDispatcherGeneric) change(env);
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
		SDMSIntervalDispatcherGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(IntervalDispatcher) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSIntervalDispatcherGeneric) change(env);
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
		SDMSIntervalDispatcherGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSIntervalDispatcherGeneric) change(env);
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
		SDMSIntervalDispatcherGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSIntervalDispatcherGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSIntervalDispatcherGeneric set_IntIdSeqNo (SystemEnvironment env, Long p_intId, Integer p_seqNo)
	throws SDMSException
	{
		SDMSIntervalDispatcherGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(IntervalDispatcher) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalDispatcherGeneric) change(env);
			o.intId = p_intId;
			o.seqNo = p_seqNo;
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
		return SDMSIntervalDispatcher.getProxy(sysEnv, this);
	}

	protected SDMSIntervalDispatcherGeneric(Long p_id,
	                                        Long p_intId,
	                                        Integer p_seqNo,
	                                        String p_name,
	                                        Long p_selectIntId,
	                                        Long p_filterIntId,
	                                        Boolean p_isEnabled,
	                                        Boolean p_isActive,
	                                        Long p_creatorUId,
	                                        Long p_createTs,
	                                        Long p_changerUId,
	                                        Long p_changeTs,
	                                        long p_validFrom, long p_validTo)
	{
		id     = p_id;
		intId = p_intId;
		seqNo = p_seqNo;
		name = p_name;
		selectIntId = p_selectIntId;
		filterIntId = p_filterIntId;
		isEnabled = p_isEnabled;
		isActive = p_isActive;
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
				        "INSERT INTO INTERVAL_DISPATCHER (" +
				        "ID" +
				        ", " + squote + "INT_ID" + equote +
				        ", " + squote + "SEQ_NO" + equote +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "SELECT_INT_ID" + equote +
				        ", " + squote + "FILTER_INT_ID" + equote +
				        ", " + squote + "IS_ENABLED" + equote +
				        ", " + squote + "IS_ACTIVE" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "IntervalDispatcher: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, intId.longValue());
			myInsert.setInt(3, seqNo.intValue());
			myInsert.setString(4, name);
			if (selectIntId == null)
				myInsert.setNull(5, Types.INTEGER);
			else
				myInsert.setLong (5, selectIntId.longValue());
			if (filterIntId == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setLong (6, filterIntId.longValue());
			myInsert.setInt (7, isEnabled.booleanValue() ? 1 : 0);
			myInsert.setInt (8, isActive.booleanValue() ? 1 : 0);
			myInsert.setLong (9, creatorUId.longValue());
			myInsert.setLong (10, createTs.longValue());
			myInsert.setLong (11, changerUId.longValue());
			myInsert.setLong (12, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "IntervalDispatcher: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM INTERVAL_DISPATCHER WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "IntervalDispatcher: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "IntervalDispatcher: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE INTERVAL_DISPATCHER SET " +
				        "" + squote + "INT_ID" + equote + " = ? " +
				        ", " + squote + "SEQ_NO" + equote + " = ? " +
				        ", " + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "SELECT_INT_ID" + equote + " = ? " +
				        ", " + squote + "FILTER_INT_ID" + equote + " = ? " +
				        ", " + squote + "IS_ENABLED" + equote + " = ? " +
				        ", " + squote + "IS_ACTIVE" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "IntervalDispatcher: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, intId.longValue());
			myUpdate.setInt(2, seqNo.intValue());
			myUpdate.setString(3, name);
			if (selectIntId == null)
				myUpdate.setNull(4, Types.INTEGER);
			else
				myUpdate.setLong (4, selectIntId.longValue());
			if (filterIntId == null)
				myUpdate.setNull(5, Types.INTEGER);
			else
				myUpdate.setLong (5, filterIntId.longValue());
			myUpdate.setInt (6, isEnabled.booleanValue() ? 1 : 0);
			myUpdate.setInt (7, isActive.booleanValue() ? 1 : 0);
			myUpdate.setLong (8, creatorUId.longValue());
			myUpdate.setLong (9, createTs.longValue());
			myUpdate.setLong (10, changerUId.longValue());
			myUpdate.setLong (11, changeTs.longValue());
			myUpdate.setLong(12, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "IntervalDispatcher: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : IntervalDispatcher", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "intId : " + intId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seqNo : " + seqNo, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "selectIntId : " + selectIntId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "filterIntId : " + filterIntId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isEnabled : " + isEnabled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isActive : " + isActive, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "intId       : " + intId + "\n" +
		        indentString + "seqNo       : " + seqNo + "\n" +
		        indentString + "name        : " + name + "\n" +
		        indentString + "selectIntId : " + selectIntId + "\n" +
		        indentString + "filterIntId : " + filterIntId + "\n" +
		        indentString + "isEnabled   : " + isEnabled + "\n" +
		        indentString + "isActive    : " + isActive + "\n" +
		        indentString + "creatorUId  : " + creatorUId + "\n" +
		        indentString + "createTs    : " + createTs + "\n" +
		        indentString + "changerUId  : " + changerUId + "\n" +
		        indentString + "changeTs    : " + changeTs + "\n" +
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
