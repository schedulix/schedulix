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

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

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

	public	SDMSNiceProfileEntryGeneric setNpId (SystemEnvironment env, Long p_npId)
	throws SDMSException
	{
		if(npId.equals(p_npId)) return this;
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

	public Integer getPreference (SystemEnvironment env)
	throws SDMSException
	{
		return (preference);
	}

	public	SDMSNiceProfileEntryGeneric setPreference (SystemEnvironment env, Integer p_preference)
	throws SDMSException
	{
		if(preference.equals(p_preference)) return this;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
			o.preference = p_preference;
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

	public Long getFolderId (SystemEnvironment env)
	throws SDMSException
	{
		return (folderId);
	}

	public	SDMSNiceProfileEntryGeneric setFolderId (SystemEnvironment env, Long p_folderId)
	throws SDMSException
	{
		if(p_folderId != null && p_folderId.equals(folderId)) return this;
		if(p_folderId == null && folderId == null) return this;
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

	public	SDMSNiceProfileEntryGeneric setIsSuspended (SystemEnvironment env, Integer p_isSuspended)
	throws SDMSException
	{
		if(isSuspended.equals(p_isSuspended)) return this;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
			o.isSuspended = p_isSuspended;
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

	public Integer getRenice (SystemEnvironment env)
	throws SDMSException
	{
		return (renice);
	}

	public	SDMSNiceProfileEntryGeneric setRenice (SystemEnvironment env, Integer p_renice)
	throws SDMSException
	{
		if(renice.equals(p_renice)) return this;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
			o.renice = p_renice;
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

	public	SDMSNiceProfileEntryGeneric setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		if(isActive.equals(p_isActive)) return this;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
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

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	SDMSNiceProfileEntryGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
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

	SDMSNiceProfileEntryGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(NiceProfileEntry) Change of system object not allowed")
				);
			}
			o = (SDMSNiceProfileEntryGeneric) change(env);
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

	public	SDMSNiceProfileEntryGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSNiceProfileEntryGeneric) change(env);
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

	SDMSNiceProfileEntryGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSNiceProfileEntryGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSNiceProfileEntryGeneric) change(env);
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
		return new SDMSNiceProfileEntry(this);
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
				        "INSERT INTO NICE_PROFILE_ENTRY (" +
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
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "NiceProfileEntry: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, npId.longValue());
			pInsert.setInt(3, preference.intValue());
			if (folderId == null)
				pInsert.setNull(4, Types.INTEGER);
			else
				pInsert.setLong (4, folderId.longValue());
			pInsert.setInt(5, isSuspended.intValue());
			pInsert.setInt(6, renice.intValue());
			pInsert.setInt (7, isActive.booleanValue() ? 1 : 0);
			pInsert.setLong (8, creatorUId.longValue());
			pInsert.setLong (9, createTs.longValue());
			pInsert.setLong (10, changerUId.longValue());
			pInsert.setLong (11, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "NiceProfileEntry: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM NICE_PROFILE_ENTRY WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "NiceProfileEntry: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "NiceProfileEntry: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE NICE_PROFILE_ENTRY SET " +
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
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "NiceProfileEntry: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, npId.longValue());
			pUpdate.setInt(2, preference.intValue());
			if (folderId == null)
				pUpdate.setNull(3, Types.INTEGER);
			else
				pUpdate.setLong (3, folderId.longValue());
			pUpdate.setInt(4, isSuspended.intValue());
			pUpdate.setInt(5, renice.intValue());
			pUpdate.setInt (6, isActive.booleanValue() ? 1 : 0);
			pUpdate.setLong (7, creatorUId.longValue());
			pUpdate.setLong (8, createTs.longValue());
			pUpdate.setLong (9, changerUId.longValue());
			pUpdate.setLong (10, changeTs.longValue());
			pUpdate.setLong(11, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "NiceProfileEntry: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
