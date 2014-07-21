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

public class SDMSExitStateMappingGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_esmpId = 2;
	public final static int nr_esdId = 3;
	public final static int nr_ecrStart = 4;
	public final static int nr_ecrEnd = 5;
	public final static int nr_creatorUId = 6;
	public final static int nr_createTs = 7;
	public final static int nr_changerUId = 8;
	public final static int nr_changeTs = 9;

	public static String tableName = SDMSExitStateMappingTableGeneric.tableName;

	protected Long esmpId;
	protected Long esdId;
	protected Integer ecrStart;
	protected Integer ecrEnd;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSExitStateMappingGeneric(
	        SystemEnvironment env,
	        Long p_esmpId,
	        Long p_esdId,
	        Integer p_ecrStart,
	        Integer p_ecrEnd,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSExitStateMappingTableGeneric.table);
		esmpId = p_esmpId;
		esdId = p_esdId;
		ecrStart = p_ecrStart;
		ecrEnd = p_ecrEnd;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getEsmpId (SystemEnvironment env)
	throws SDMSException
	{
		return (esmpId);
	}

	public	SDMSExitStateMappingGeneric setEsmpId (SystemEnvironment env, Long p_esmpId)
	throws SDMSException
	{
		if(esmpId.equals(p_esmpId)) return this;
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateMapping) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateMappingGeneric) change(env);
			o.esmpId = p_esmpId;
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

	public	SDMSExitStateMappingGeneric setEsdId (SystemEnvironment env, Long p_esdId)
	throws SDMSException
	{
		if(esdId.equals(p_esdId)) return this;
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateMapping) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateMappingGeneric) change(env);
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

	public Integer getEcrStart (SystemEnvironment env)
	throws SDMSException
	{
		return (ecrStart);
	}

	public	SDMSExitStateMappingGeneric setEcrStart (SystemEnvironment env, Integer p_ecrStart)
	throws SDMSException
	{
		if(p_ecrStart != null && p_ecrStart.equals(ecrStart)) return this;
		if(p_ecrStart == null && ecrStart == null) return this;
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateMapping) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateMappingGeneric) change(env);
			o.ecrStart = p_ecrStart;
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

	public Integer getEcrEnd (SystemEnvironment env)
	throws SDMSException
	{
		return (ecrEnd);
	}

	public	SDMSExitStateMappingGeneric setEcrEnd (SystemEnvironment env, Integer p_ecrEnd)
	throws SDMSException
	{
		if(p_ecrEnd != null && p_ecrEnd.equals(ecrEnd)) return this;
		if(p_ecrEnd == null && ecrEnd == null) return this;
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateMapping) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateMappingGeneric) change(env);
			o.ecrEnd = p_ecrEnd;
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

	SDMSExitStateMappingGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateMapping) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateMappingGeneric) change(env);
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

	SDMSExitStateMappingGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateMapping) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateMappingGeneric) change(env);
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

	public	SDMSExitStateMappingGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSExitStateMappingGeneric) change(env);
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

	SDMSExitStateMappingGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSExitStateMappingGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSExitStateMappingGeneric) change(env);
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

	public SDMSExitStateMappingGeneric set_EsmpIdEsdId (SystemEnvironment env, Long p_esmpId, Long p_esdId)
	throws SDMSException
	{
		SDMSExitStateMappingGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ExitStateMapping) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateMappingGeneric) change(env);
			o.esmpId = p_esmpId;
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
		return new SDMSExitStateMapping(this);
	}

	protected SDMSExitStateMappingGeneric(Long p_id,
	                                      Long p_esmpId,
	                                      Long p_esdId,
	                                      Integer p_ecrStart,
	                                      Integer p_ecrEnd,
	                                      Long p_creatorUId,
	                                      Long p_createTs,
	                                      Long p_changerUId,
	                                      Long p_changeTs,
	                                      long p_validFrom, long p_validTo)
	{
		id     = p_id;
		esmpId = p_esmpId;
		esdId = p_esdId;
		ecrStart = p_ecrStart;
		ecrEnd = p_ecrEnd;
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
				        "INSERT INTO EXIT_STATE_MAPPING (" +
				        "ID" +
				        ", " + squote + "ESMP_ID" + equote +
				        ", " + squote + "ESD_ID" + equote +
				        ", " + squote + "ECR_START" + equote +
				        ", " + squote + "ECR_END" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "ExitStateMapping: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, esmpId.longValue());
			pInsert.setLong (3, esdId.longValue());
			if (ecrStart == null)
				pInsert.setNull(4, Types.INTEGER);
			else
				pInsert.setInt(4, ecrStart.intValue());
			if (ecrEnd == null)
				pInsert.setNull(5, Types.INTEGER);
			else
				pInsert.setInt(5, ecrEnd.intValue());
			pInsert.setLong (6, creatorUId.longValue());
			pInsert.setLong (7, createTs.longValue());
			pInsert.setLong (8, changerUId.longValue());
			pInsert.setLong (9, changeTs.longValue());
			pInsert.setLong(10, env.tx.versionId);
			pInsert.setLong(11, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "ExitStateMapping: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE EXIT_STATE_MAPPING " +
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
				throw new FatalException(new SDMSMessage(env, "01110181955", "ExitStateMapping : $1\n$2", stmt, sqle.toString()));
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

			throw new FatalException(new SDMSMessage(env, "01110181956", "ExitStateMapping: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ExitStateMapping", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "esmpId : " + esmpId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "esdId : " + esdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ecrStart : " + ecrStart, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ecrEnd : " + ecrEnd, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "esmpId     : " + esmpId + "\n" +
		        indentString + "esdId      : " + esdId + "\n" +
		        indentString + "ecrStart   : " + ecrStart + "\n" +
		        indentString + "ecrEnd     : " + ecrEnd + "\n" +
		        indentString + "creatorUId : " + creatorUId + "\n" +
		        indentString + "createTs   : " + createTs + "\n" +
		        indentString + "changerUId : " + changerUId + "\n" +
		        indentString + "changeTs   : " + changeTs + "\n" +
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
