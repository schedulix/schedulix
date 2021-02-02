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

public class SDMSUserParameterGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_uId = 2;
	public final static int nr_name = 3;
	public final static int nr_value = 4;
	public final static int nr_isLong = 5;
	public final static int nr_creatorUId = 6;
	public final static int nr_createTs = 7;
	public final static int nr_changerUId = 8;
	public final static int nr_changeTs = 9;

	public static String tableName = SDMSUserParameterTableGeneric.tableName;

	protected Long uId;
	protected String name;
	protected String value;
	protected Boolean isLong;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSUserParameterGeneric(
	        SystemEnvironment env,
	        Long p_uId,
	        String p_name,
	        String p_value,
	        Boolean p_isLong,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSUserParameterTableGeneric.table);
		uId = p_uId;
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(UserParameter) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		if (p_value != null && p_value.length() > 256) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(UserParameter) Length of $1 exceeds maximum length $2", "value", "256")
			);
		}
		value = p_value;
		isLong = p_isLong;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getUId (SystemEnvironment env)
	throws SDMSException
	{
		return (uId);
	}

	public	void setUId (SystemEnvironment env, Long p_uId)
	throws SDMSException
	{
		if(uId.equals(p_uId)) return;
		SDMSUserParameterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(UserParameter) Change of system object not allowed")
				);
			}
			o = (SDMSUserParameterGeneric) change(env);
			o.uId = p_uId;
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

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		return (name);
	}

	public	void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return;
		SDMSUserParameterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(UserParameter) Change of system object not allowed")
				);
			}
			o = (SDMSUserParameterGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(UserParameter) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
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

	public String getValue (SystemEnvironment env)
	throws SDMSException
	{
		return (value);
	}

	public	void setValue (SystemEnvironment env, String p_value)
	throws SDMSException
	{
		if(p_value != null && p_value.equals(value)) return;
		if(p_value == null && value == null) return;
		SDMSUserParameterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(UserParameter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSUserParameterGeneric) change(env);
		if (p_value != null && p_value.length() > 256) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(UserParameter) Length of $1 exceeds maximum length $2", "value", "256")
			);
		}
		o.value = p_value;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsLong (SystemEnvironment env)
	throws SDMSException
	{
		return (isLong);
	}

	public	void setIsLong (SystemEnvironment env, Boolean p_isLong)
	throws SDMSException
	{
		if(isLong.equals(p_isLong)) return;
		SDMSUserParameterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(UserParameter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSUserParameterGeneric) change(env);
		o.isLong = p_isLong;
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
		SDMSUserParameterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(UserParameter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSUserParameterGeneric) change(env);
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
		SDMSUserParameterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(UserParameter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSUserParameterGeneric) change(env);
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
		SDMSUserParameterGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSUserParameterGeneric) change(env);
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
		SDMSUserParameterGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSUserParameterGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSUserParameterGeneric set_UIdName (SystemEnvironment env, Long p_uId, String p_name)
	throws SDMSException
	{
		SDMSUserParameterGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(UserParameter) Change of system object not allowed")
				);
			}
			o = (SDMSUserParameterGeneric) change(env);
			o.uId = p_uId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290026",
				                        "(UserParameter) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.name = p_name;
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
		return new SDMSUserParameter(this);
	}

	protected SDMSUserParameterGeneric(Long p_id,
	                                   Long p_uId,
	                                   String p_name,
	                                   String p_value,
	                                   Boolean p_isLong,
	                                   Long p_creatorUId,
	                                   Long p_createTs,
	                                   Long p_changerUId,
	                                   Long p_changeTs,
	                                   long p_validFrom, long p_validTo)
	{
		id     = p_id;
		uId = p_uId;
		name = p_name;
		value = p_value;
		isLong = p_isLong;
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
				        "INSERT INTO " + squote + "USER_PARAMETER" + equote + " (" +
				        "ID" +
				        ", " + squote + "U_ID" + equote +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "VALUE" + equote +
				        ", " + squote + "IS_LONG" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "UserParameter: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, uId.longValue());
			myInsert.setString(3, name);
			if (value == null)
				myInsert.setNull(4, Types.VARCHAR);
			else
				myInsert.setString(4, value);
			myInsert.setInt (5, isLong.booleanValue() ? 1 : 0);
			myInsert.setLong (6, creatorUId.longValue());
			myInsert.setLong (7, createTs.longValue());
			myInsert.setLong (8, changerUId.longValue());
			myInsert.setLong (9, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "UserParameter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM " + squote + "USER_PARAMETER" + equote + " WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "UserParameter: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "UserParameter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE " + squote + "USER_PARAMETER" + equote + " SET " +
				        "" + squote + "U_ID" + equote + " = ? " +
				        ", " + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "VALUE" + equote + " = ? " +
				        ", " + squote + "IS_LONG" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "UserParameter: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, uId.longValue());
			myUpdate.setString(2, name);
			if (value == null)
				myUpdate.setNull(3, Types.VARCHAR);
			else
				myUpdate.setString(3, value);
			myUpdate.setInt (4, isLong.booleanValue() ? 1 : 0);
			myUpdate.setLong (5, creatorUId.longValue());
			myUpdate.setLong (6, createTs.longValue());
			myUpdate.setLong (7, changerUId.longValue());
			myUpdate.setLong (8, changeTs.longValue());
			myUpdate.setLong(9, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "UserParameter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : UserParameter", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "uId : " + uId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "value : " + value, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isLong : " + isLong, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "uId        : " + uId + "\n" +
		        indentString + "name       : " + name + "\n" +
		        indentString + "value      : " + value + "\n" +
		        indentString + "isLong     : " + isLong + "\n" +
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
