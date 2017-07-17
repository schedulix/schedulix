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

public class SDMSIgnoredDependencyGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_shId = 2;
	public final static int nr_ddName = 3;
	public final static int nr_creatorUId = 4;
	public final static int nr_createTs = 5;
	public final static int nr_changerUId = 6;
	public final static int nr_changeTs = 7;

	public static String tableName = SDMSIgnoredDependencyTableGeneric.tableName;

	protected Long shId;
	protected String ddName;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSIgnoredDependencyGeneric(
	        SystemEnvironment env,
	        Long p_shId,
	        String p_ddName,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSIgnoredDependencyTableGeneric.table);
		shId = p_shId;
		if (p_ddName != null && p_ddName.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(IgnoredDependency) Length of $1 exceeds maximum length $2", "ddName", "64")
			);
		}
		ddName = p_ddName;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getShId (SystemEnvironment env)
	throws SDMSException
	{
		return (shId);
	}

	public	void setShId (SystemEnvironment env, Long p_shId)
	throws SDMSException
	{
		if(shId.equals(p_shId)) return;
		SDMSIgnoredDependencyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IgnoredDependency) Change of system object not allowed")
				);
			}
			o = (SDMSIgnoredDependencyGeneric) change(env);
			o.shId = p_shId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 3);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getDdName (SystemEnvironment env)
	throws SDMSException
	{
		return (ddName);
	}

	public	void setDdName (SystemEnvironment env, String p_ddName)
	throws SDMSException
	{
		if(ddName.equals(p_ddName)) return;
		SDMSIgnoredDependencyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IgnoredDependency) Change of system object not allowed")
				);
			}
			o = (SDMSIgnoredDependencyGeneric) change(env);
			if (p_ddName != null && p_ddName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(IgnoredDependency) Length of $1 exceeds maximum length $2", "ddName", "64")
				);
			}
			o.ddName = p_ddName;
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

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return;
		SDMSIgnoredDependencyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(IgnoredDependency) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSIgnoredDependencyGeneric) change(env);
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
		SDMSIgnoredDependencyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(IgnoredDependency) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSIgnoredDependencyGeneric) change(env);
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
		SDMSIgnoredDependencyGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSIgnoredDependencyGeneric) change(env);
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
		SDMSIgnoredDependencyGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSIgnoredDependencyGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSIgnoredDependencyGeneric set_ShIdDdName (SystemEnvironment env, Long p_shId, String p_ddName)
	throws SDMSException
	{
		SDMSIgnoredDependencyGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(IgnoredDependency) Change of system object not allowed")
				);
			}
			o = (SDMSIgnoredDependencyGeneric) change(env);
			o.shId = p_shId;
			if (p_ddName != null && p_ddName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(IgnoredDependency) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.ddName = p_ddName;
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
		return SDMSIgnoredDependency.getProxy(sysEnv, this);
	}

	protected SDMSIgnoredDependencyGeneric(Long p_id,
	                                       Long p_shId,
	                                       String p_ddName,
	                                       Long p_creatorUId,
	                                       Long p_createTs,
	                                       Long p_changerUId,
	                                       Long p_changeTs,
	                                       long p_validFrom, long p_validTo)
	{
		id     = p_id;
		shId = p_shId;
		ddName = p_ddName;
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
				        "INSERT INTO IGNORED_DEPENDENCY (" +
				        "ID" +
				        ", " + squote + "SH_ID" + equote +
				        ", " + squote + "DD_NAME" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "IgnoredDependency: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, shId.longValue());
			myInsert.setString(3, ddName);
			myInsert.setLong (4, creatorUId.longValue());
			myInsert.setLong (5, createTs.longValue());
			myInsert.setLong (6, changerUId.longValue());
			myInsert.setLong (7, changeTs.longValue());
			myInsert.setLong(8, env.tx.versionId);
			myInsert.setLong(9, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "IgnoredDependency: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				stmt =
				        "UPDATE IGNORED_DEPENDENCY " +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				// Can't prepare statement
				throw new FatalException(new SDMSMessage(env, "01110181955", "IgnoredDependency : $1\n$2", stmt, sqle.toString()));
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
			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "IgnoredDependency: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : IgnoredDependency", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "shId : " + shId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ddName : " + ddName, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "shId       : " + shId + "\n" +
		        indentString + "ddName     : " + ddName + "\n" +
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
