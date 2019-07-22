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

public class SDMSNiceProfileEntryGeneric extends SDMSObject
	implements Cloneable
{

	public static final int NOSUSPEND = 0;
	public static final int SUSPEND = 1;
	public static final int ADMINSUSPEND = 2;

	public final static int nr_id = 1;
	public final static int nr_npId = 2;
	public final static int nr_preference = 3;
	public final static int nr_folderId = 4;
	public final static int nr_isSuspended = 5;
	public final static int nr_renice = 6;
	public final static int nr_isActive = 7;
	public final static int nr_creatorUId = 8;
	public final static int nr_createTs = 9;
	public final static int nr_changerUId = 10;
	public final static int nr_changeTs = 11;

	public static String tableName = SDMSNiceProfileEntryTableGeneric.tableName;

	protected Long npId;
	protected Integer preference;
	protected Long folderId;
	protected Integer isSuspended;
	protected Integer renice;
	protected Boolean isActive;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSNiceProfileEntryGeneric(
	        SystemEnvironment env,
	        Long p_npId,
	        Integer p_preference,
	        Long p_folderId,
	        Integer p_isSuspended,
	        Integer p_renice,
	        Boolean p_isActive,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSNiceProfileEntryTableGeneric.table);
		npId = p_npId;
		preference = p_preference;
		folderId = p_folderId;
		isSuspended = p_isSuspended;
		renice = p_renice;
		isActive = p_isActive;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getNpId (SystemEnvironment env)
	throws SDMSException
	{
		return (npId);
	}

	public	void setNpId (SystemEnvironment env, Long p_npId)
	throws SDMSException
	{
		if(npId.equals(p_npId)) return;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
			o.npId = p_npId;
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

	public Integer getPreference (SystemEnvironment env)
	throws SDMSException
	{
		return (preference);
	}

	public	void setPreference (SystemEnvironment env, Integer p_preference)
	throws SDMSException
	{
		if(preference.equals(p_preference)) return;
		SDMSNiceProfileEntryGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
		o.preference = p_preference;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getFolderId (SystemEnvironment env)
	throws SDMSException
	{
		return (folderId);
	}

	public	void setFolderId (SystemEnvironment env, Long p_folderId)
	throws SDMSException
	{
		if(p_folderId != null && p_folderId.equals(folderId)) return;
		if(p_folderId == null && folderId == null) return;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
			o.folderId = p_folderId;
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

	public Integer getIsSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (isSuspended);
	}

	public String getIsSuspendedAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getIsSuspended (env);
		switch (v.intValue()) {
			case SDMSNiceProfileEntry.NOSUSPEND:
				return "NOSUSPEND";
			case SDMSNiceProfileEntry.SUSPEND:
				return "SUSPEND";
			case SDMSNiceProfileEntry.ADMINSUSPEND:
				return "ADMINSUSPEND";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown NiceProfileEntry.isSuspended: $1",
		                          getIsSuspended (env)));
	}

	public	void setIsSuspended (SystemEnvironment env, Integer p_isSuspended)
	throws SDMSException
	{
		if(isSuspended.equals(p_isSuspended)) return;
		SDMSNiceProfileEntryGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
		o.isSuspended = p_isSuspended;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getRenice (SystemEnvironment env)
	throws SDMSException
	{
		return (renice);
	}

	public	void setRenice (SystemEnvironment env, Integer p_renice)
	throws SDMSException
	{
		if(renice.equals(p_renice)) return;
		SDMSNiceProfileEntryGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
		o.renice = p_renice;
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
		SDMSNiceProfileEntryGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
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
		SDMSNiceProfileEntryGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
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
		SDMSNiceProfileEntryGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
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
		SDMSNiceProfileEntryGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
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
		SDMSNiceProfileEntryGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSNiceProfileEntryGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return SDMSNiceProfileEntry.getProxy(sysEnv, this);
	}

	protected SDMSNiceProfileEntryGeneric(Long p_id,
	                                      Long p_npId,
	                                      Integer p_preference,
	                                      Long p_folderId,
	                                      Integer p_isSuspended,
	                                      Integer p_renice,
	                                      Boolean p_isActive,
	                                      Long p_creatorUId,
	                                      Long p_createTs,
	                                      Long p_changerUId,
	                                      Long p_changeTs,
	                                      long p_validFrom, long p_validTo)
	{
		id     = p_id;
		npId = p_npId;
		preference = p_preference;
		folderId = p_folderId;
		isSuspended = p_isSuspended;
		renice = p_renice;
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
				        "INSERT INTO " + squote + "NICE_PROFILE_ENTRY" + equote + " (" +
				        "ID" +
				        ", " + squote + "NP_ID" + equote +
				        ", " + squote + "PREFERENCE" + equote +
				        ", " + squote + "FOLDER_ID" + equote +
				        ", " + squote + "IS_SUSPENDED" + equote +
				        ", " + squote + "RENICE" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "NiceProfileEntry: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, npId.longValue());
			myInsert.setInt(3, preference.intValue());
			if (folderId == null)
				myInsert.setNull(4, Types.INTEGER);
			else
				myInsert.setLong (4, folderId.longValue());
			myInsert.setInt(5, isSuspended.intValue());
			myInsert.setInt(6, renice.intValue());
			myInsert.setInt (7, isActive.booleanValue() ? 1 : 0);
			myInsert.setLong (8, creatorUId.longValue());
			myInsert.setLong (9, createTs.longValue());
			myInsert.setLong (10, changerUId.longValue());
			myInsert.setLong (11, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "NiceProfileEntry: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM " + squote + "NICE_PROFILE_ENTRY" + equote + " WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "NiceProfileEntry: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "NiceProfileEntry: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE " + squote + "NICE_PROFILE_ENTRY" + equote + " SET " +
				        "" + squote + "NP_ID" + equote + " = ? " +
				        ", " + squote + "PREFERENCE" + equote + " = ? " +
				        ", " + squote + "FOLDER_ID" + equote + " = ? " +
				        ", " + squote + "IS_SUSPENDED" + equote + " = ? " +
				        ", " + squote + "RENICE" + equote + " = ? " +
				        ", " + squote + "IS_ACTIVE" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "NiceProfileEntry: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, npId.longValue());
			myUpdate.setInt(2, preference.intValue());
			if (folderId == null)
				myUpdate.setNull(3, Types.INTEGER);
			else
				myUpdate.setLong (3, folderId.longValue());
			myUpdate.setInt(4, isSuspended.intValue());
			myUpdate.setInt(5, renice.intValue());
			myUpdate.setInt (6, isActive.booleanValue() ? 1 : 0);
			myUpdate.setLong (7, creatorUId.longValue());
			myUpdate.setLong (8, createTs.longValue());
			myUpdate.setLong (9, changerUId.longValue());
			myUpdate.setLong (10, changeTs.longValue());
			myUpdate.setLong(11, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "NiceProfileEntry: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkIsSuspended(Integer p)
	{
		switch (p.intValue()) {
			case SDMSNiceProfileEntry.NOSUSPEND:
			case SDMSNiceProfileEntry.SUSPEND:
			case SDMSNiceProfileEntry.ADMINSUSPEND:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : NiceProfileEntry", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "npId : " + npId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "preference : " + preference, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "folderId : " + folderId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSuspended : " + isSuspended, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "renice : " + renice, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "npId        : " + npId + "\n" +
		        indentString + "preference  : " + preference + "\n" +
		        indentString + "folderId    : " + folderId + "\n" +
		        indentString + "isSuspended : " + isSuspended + "\n" +
		        indentString + "renice      : " + renice + "\n" +
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
