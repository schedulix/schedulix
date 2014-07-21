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

public class SDMSExitStateGeneric extends SDMSObject
	implements Cloneable
{

	public static final int RESTARTABLE = 1;
	public static final int PENDING = 2;
	public static final int FINAL = 3;
	public static final int UNREACHABLE = 4;

	public final static int nr_id = 1;
	public final static int nr_preference = 2;
	public final static int nr_isFinal = 3;
	public final static int nr_isRestartable = 4;
	public final static int nr_isUnreachable = 5;
	public final static int nr_isBroken = 6;
	public final static int nr_isBatchDefault = 7;
	public final static int nr_isDependencyDefault = 8;
	public final static int nr_espId = 9;
	public final static int nr_esdId = 10;
	public final static int nr_creatorUId = 11;
	public final static int nr_createTs = 12;
	public final static int nr_changerUId = 13;
	public final static int nr_changeTs = 14;

	public static String tableName = SDMSExitStateTableGeneric.tableName;

	protected Integer preference;
	protected Boolean isFinal;
	protected Boolean isRestartable;
	protected Boolean isUnreachable;
	protected Boolean isBroken;
	protected Boolean isBatchDefault;
	protected Boolean isDependencyDefault;
	protected Long espId;
	protected Long esdId;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSExitStateGeneric(
	        SystemEnvironment env,
	        Integer p_preference,
	        Boolean p_isFinal,
	        Boolean p_isRestartable,
	        Boolean p_isUnreachable,
	        Boolean p_isBroken,
	        Boolean p_isBatchDefault,
	        Boolean p_isDependencyDefault,
	        Long p_espId,
	        Long p_esdId,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSExitStateTableGeneric.table);
		preference = p_preference;
		isFinal = p_isFinal;
		isRestartable = p_isRestartable;
		isUnreachable = p_isUnreachable;
		isBroken = p_isBroken;
		isBatchDefault = p_isBatchDefault;
		isDependencyDefault = p_isDependencyDefault;
		espId = p_espId;
		esdId = p_esdId;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Integer getPreference (SystemEnvironment env)
	throws SDMSException
	{
		return (preference);
	}

	public	SDMSExitStateGeneric setPreference (SystemEnvironment env, Integer p_preference)
	throws SDMSException
	{
		if(preference.equals(p_preference)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
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

	public Boolean getIsFinal (SystemEnvironment env)
	throws SDMSException
	{
		return (isFinal);
	}

	public	SDMSExitStateGeneric setIsFinal (SystemEnvironment env, Boolean p_isFinal)
	throws SDMSException
	{
		if(isFinal.equals(p_isFinal)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.isFinal = p_isFinal;
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

	public Boolean getIsRestartable (SystemEnvironment env)
	throws SDMSException
	{
		return (isRestartable);
	}

	public	SDMSExitStateGeneric setIsRestartable (SystemEnvironment env, Boolean p_isRestartable)
	throws SDMSException
	{
		if(isRestartable.equals(p_isRestartable)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.isRestartable = p_isRestartable;
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

	public Boolean getIsUnreachable (SystemEnvironment env)
	throws SDMSException
	{
		return (isUnreachable);
	}

	public	SDMSExitStateGeneric setIsUnreachable (SystemEnvironment env, Boolean p_isUnreachable)
	throws SDMSException
	{
		if(isUnreachable.equals(p_isUnreachable)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.isUnreachable = p_isUnreachable;
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

	public Boolean getIsBroken (SystemEnvironment env)
	throws SDMSException
	{
		return (isBroken);
	}

	public	SDMSExitStateGeneric setIsBroken (SystemEnvironment env, Boolean p_isBroken)
	throws SDMSException
	{
		if(isBroken.equals(p_isBroken)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.isBroken = p_isBroken;
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

	public Boolean getIsBatchDefault (SystemEnvironment env)
	throws SDMSException
	{
		return (isBatchDefault);
	}

	public	SDMSExitStateGeneric setIsBatchDefault (SystemEnvironment env, Boolean p_isBatchDefault)
	throws SDMSException
	{
		if(isBatchDefault.equals(p_isBatchDefault)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.isBatchDefault = p_isBatchDefault;
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

	public Boolean getIsDependencyDefault (SystemEnvironment env)
	throws SDMSException
	{
		return (isDependencyDefault);
	}

	public	SDMSExitStateGeneric setIsDependencyDefault (SystemEnvironment env, Boolean p_isDependencyDefault)
	throws SDMSException
	{
		if(isDependencyDefault.equals(p_isDependencyDefault)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.isDependencyDefault = p_isDependencyDefault;
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

	public Long getEspId (SystemEnvironment env)
	throws SDMSException
	{
		return (espId);
	}

	public	SDMSExitStateGeneric setEspId (SystemEnvironment env, Long p_espId)
	throws SDMSException
	{
		if(espId.equals(p_espId)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.espId = p_espId;
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

	public Long getEsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (esdId);
	}

	public	SDMSExitStateGeneric setEsdId (SystemEnvironment env, Long p_esdId)
	throws SDMSException
	{
		if(esdId.equals(p_esdId)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.esdId = p_esdId;
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

	SDMSExitStateGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
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

	SDMSExitStateGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
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

	public	SDMSExitStateGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSExitStateGeneric) change(env);
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

	SDMSExitStateGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSExitStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSExitStateGeneric) change(env);
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

	public SDMSExitStateGeneric set_EspIdEsdId (SystemEnvironment env, Long p_espId, Long p_esdId)
	throws SDMSException
	{
		SDMSExitStateGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ExitState) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateGeneric) change(env);
			o.espId = p_espId;
			o.esdId = p_esdId;
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
		return new SDMSExitState(this);
	}

	protected SDMSExitStateGeneric(Long p_id,
	                               Integer p_preference,
	                               Boolean p_isFinal,
	                               Boolean p_isRestartable,
	                               Boolean p_isUnreachable,
	                               Boolean p_isBroken,
	                               Boolean p_isBatchDefault,
	                               Boolean p_isDependencyDefault,
	                               Long p_espId,
	                               Long p_esdId,
	                               Long p_creatorUId,
	                               Long p_createTs,
	                               Long p_changerUId,
	                               Long p_changeTs,
	                               long p_validFrom, long p_validTo)
	{
		id     = p_id;
		preference = p_preference;
		isFinal = p_isFinal;
		isRestartable = p_isRestartable;
		isUnreachable = p_isUnreachable;
		isBroken = p_isBroken;
		isBatchDefault = p_isBatchDefault;
		isDependencyDefault = p_isDependencyDefault;
		espId = p_espId;
		esdId = p_esdId;
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
				        "INSERT INTO EXIT_STATE (" +
				        "ID" +
				        ", " + squote + "PREFERENCE" + equote +
				        ", " + squote + "IS_FINAL" + equote +
				        ", " + squote + "IS_RESTARTABLE" + equote +
				        ", " + squote + "IS_UNREACHABLE" + equote +
				        ", " + squote + "IS_BROKEN" + equote +
				        ", " + squote + "IS_BATCH_DEFAULT" + equote +
				        ", " + squote + "IS_DEPENDENCY_DEFAULT" + equote +
				        ", " + squote + "ESP_ID" + equote +
				        ", " + squote + "ESD_ID" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "ExitState: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setInt(2, preference.intValue());
			pInsert.setInt (3, isFinal.booleanValue() ? 1 : 0);
			pInsert.setInt (4, isRestartable.booleanValue() ? 1 : 0);
			pInsert.setInt (5, isUnreachable.booleanValue() ? 1 : 0);
			pInsert.setInt (6, isBroken.booleanValue() ? 1 : 0);
			pInsert.setInt (7, isBatchDefault.booleanValue() ? 1 : 0);
			pInsert.setInt (8, isDependencyDefault.booleanValue() ? 1 : 0);
			pInsert.setLong (9, espId.longValue());
			pInsert.setLong (10, esdId.longValue());
			pInsert.setLong (11, creatorUId.longValue());
			pInsert.setLong (12, createTs.longValue());
			pInsert.setLong (13, changerUId.longValue());
			pInsert.setLong (14, changeTs.longValue());
			pInsert.setLong(15, env.tx.versionId);
			pInsert.setLong(16, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "ExitState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
		if(pUpdate == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				final boolean postgres = driverName.startsWith("PostgreSQL");
				stmt =
				        "UPDATE EXIT_STATE " +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				// Can't prepare statement
				throw new FatalException(new SDMSMessage(env, "01110181955", "ExitState : $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong(1, env.tx.versionId);
			pUpdate.setLong(2, changeTs.longValue());
			pUpdate.setLong(3, changerUId.longValue());
			pUpdate.setLong(4, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181956", "ExitState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ExitState", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "preference : " + preference, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isFinal : " + isFinal, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isRestartable : " + isRestartable, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isUnreachable : " + isUnreachable, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isBroken : " + isBroken, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isBatchDefault : " + isBatchDefault, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isDependencyDefault : " + isDependencyDefault, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "espId : " + espId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "esdId : " + esdId, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "preference          : " + preference + "\n" +
		        indentString + "isFinal             : " + isFinal + "\n" +
		        indentString + "isRestartable       : " + isRestartable + "\n" +
		        indentString + "isUnreachable       : " + isUnreachable + "\n" +
		        indentString + "isBroken            : " + isBroken + "\n" +
		        indentString + "isBatchDefault      : " + isBatchDefault + "\n" +
		        indentString + "isDependencyDefault : " + isDependencyDefault + "\n" +
		        indentString + "espId               : " + espId + "\n" +
		        indentString + "esdId               : " + esdId + "\n" +
		        indentString + "creatorUId          : " + creatorUId + "\n" +
		        indentString + "createTs            : " + createTs + "\n" +
		        indentString + "changerUId          : " + changerUId + "\n" +
		        indentString + "changeTs            : " + changeTs + "\n" +
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
