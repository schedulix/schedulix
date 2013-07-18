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

public class SDMSDependencyStateGeneric extends SDMSObject
	implements Cloneable
{

	public final static String __version = "SDMSDependencyStateGeneric $Revision: 2.4 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static int nr_id = 1;
	public final static int nr_ddId = 2;
	public final static int nr_esdId = 3;
	public final static int nr_condition = 4;
	public final static int nr_creatorUId = 5;
	public final static int nr_createTs = 6;
	public final static int nr_changerUId = 7;
	public final static int nr_changeTs = 8;

	public static String tableName = SDMSDependencyStateTableGeneric.tableName;

	protected Long ddId;
	protected Long esdId;
	protected String condition;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSDependencyStateGeneric(
	        SystemEnvironment env,
	        Long p_ddId,
	        Long p_esdId,
	        String p_condition,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSDependencyStateTableGeneric.table);
		ddId = p_ddId;
		esdId = p_esdId;
		if (p_condition != null && p_condition.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(DependencyState) Length of $1 exceeds maximum length $2", "condition", "1024")
			);
		}
		condition = p_condition;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getDdId (SystemEnvironment env)
	throws SDMSException
	{
		return (ddId);
	}

	public	SDMSDependencyStateGeneric setDdId (SystemEnvironment env, Long p_ddId)
	throws SDMSException
	{
		if(ddId.equals(p_ddId)) return this;
		SDMSDependencyStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyState) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyStateGeneric) change(env);
			o.ddId = p_ddId;
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

	public	SDMSDependencyStateGeneric setEsdId (SystemEnvironment env, Long p_esdId)
	throws SDMSException
	{
		if(esdId.equals(p_esdId)) return this;
		SDMSDependencyStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyState) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyStateGeneric) change(env);
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

	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		return (condition);
	}

	public	SDMSDependencyStateGeneric setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		if(p_condition != null && p_condition.equals(condition)) return this;
		if(p_condition == null && condition == null) return this;
		SDMSDependencyStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyState) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyStateGeneric) change(env);
			if (p_condition != null && p_condition.length() > 1024) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(DependencyState) Length of $1 exceeds maximum length $2", "condition", "1024")
				);
			}
			o.condition = p_condition;
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

	SDMSDependencyStateGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSDependencyStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyState) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyStateGeneric) change(env);
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

	SDMSDependencyStateGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSDependencyStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyState) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyStateGeneric) change(env);
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

	public	SDMSDependencyStateGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSDependencyStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSDependencyStateGeneric) change(env);
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

	SDMSDependencyStateGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSDependencyStateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSDependencyStateGeneric) change(env);
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

	public SDMSDependencyStateGeneric set_DdIdEsdId (SystemEnvironment env, Long p_ddId, Long p_esdId)
	throws SDMSException
	{
		SDMSDependencyStateGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(DependencyState) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyStateGeneric) change(env);
			o.ddId = p_ddId;
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
		return new SDMSDependencyState(this);
	}

	protected SDMSDependencyStateGeneric(Long p_id,
	                                     Long p_ddId,
	                                     Long p_esdId,
	                                     String p_condition,
	                                     Long p_creatorUId,
	                                     Long p_createTs,
	                                     Long p_changerUId,
	                                     Long p_changeTs,
	                                     long p_validFrom, long p_validTo)
	{
		id     = p_id;
		ddId = p_ddId;
		esdId = p_esdId;
		condition = p_condition;
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
				if (driverName.startsWith("MySQL")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "INSERT INTO DEPENDENCY_STATE (" +
				        "ID" +
				        ", " + squote + "DD_ID" + equote +
				        ", " + squote + "ESD_ID" + equote +
				        ", " + squote + "CONDITION" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "DependencyState: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, ddId.longValue());
			pInsert.setLong (3, esdId.longValue());
			if (condition == null)
				pInsert.setNull(4, Types.VARCHAR);
			else
				pInsert.setString(4, condition);
			pInsert.setLong (5, creatorUId.longValue());
			pInsert.setLong (6, createTs.longValue());
			pInsert.setLong (7, changerUId.longValue());
			pInsert.setLong (8, changeTs.longValue());
			pInsert.setLong(9, env.tx.versionId);
			pInsert.setLong(10, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "DependencyState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE DEPENDENCY_STATE " +
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
				throw new FatalException(new SDMSMessage(env, "01110181955", "DependencyState : $1\n$2", stmt, sqle.toString()));
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

			throw new FatalException(new SDMSMessage(env, "01110181956", "DependencyState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : DependencyState", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ddId : " + ddId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "esdId : " + esdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "condition : " + condition, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validTo : " + validTo, SDMSThread.SEVERITY_MESSAGE);
	}

	public String toString(int indent)
	{
		StringBuffer sb = new StringBuffer(indent + 1);
		for(int i = 0; i < indent; ++i) sb.append(" ");
		String indentString = new String(sb);
		String result =
		        indentString + "id : " + id + "\n" +
		        indentString + "ddId       : " + ddId + "\n" +
		        indentString + "esdId      : " + esdId + "\n" +
		        indentString + "condition  : " + condition + "\n" +
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
