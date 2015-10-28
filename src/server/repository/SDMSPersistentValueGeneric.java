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

public class SDMSPersistentValueGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_intValue = 3;
	public final static int nr_creatorUId = 4;
	public final static int nr_createTs = 5;
	public final static int nr_changerUId = 6;
	public final static int nr_changeTs = 7;

	public static String tableName = SDMSPersistentValueTableGeneric.tableName;

	protected String name;
	protected Integer intValue;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[50];
	private static PreparedStatement pDelete[] = new PreparedStatement[50];
	private static PreparedStatement pInsert[] = new PreparedStatement[50];

	public SDMSPersistentValueGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Integer p_intValue,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSPersistentValueTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(PersistentValue) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		intValue = p_intValue;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
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
		SDMSPersistentValueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(PersistentValue) Change of system object not allowed")
				);
			}
			o = (SDMSPersistentValueGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(PersistentValue) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
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

	public Integer getIntValue (SystemEnvironment env)
	throws SDMSException
	{
		return (intValue);
	}

	public	void setIntValue (SystemEnvironment env, Integer p_intValue)
	throws SDMSException
	{
		if(p_intValue != null && p_intValue.equals(intValue)) return;
		if(p_intValue == null && intValue == null) return;
		SDMSPersistentValueGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(PersistentValue) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSPersistentValueGeneric) change(env);
		o.intValue = p_intValue;
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
		SDMSPersistentValueGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(PersistentValue) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSPersistentValueGeneric) change(env);
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
		SDMSPersistentValueGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(PersistentValue) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSPersistentValueGeneric) change(env);
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
		SDMSPersistentValueGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSPersistentValueGeneric) change(env);
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
		SDMSPersistentValueGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSPersistentValueGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	protected SDMSProxy toProxy()
	{
		return new SDMSPersistentValue(this);
	}

	protected SDMSPersistentValueGeneric(Long p_id,
	                                     String p_name,
	                                     Integer p_intValue,
	                                     Long p_creatorUId,
	                                     Long p_createTs,
	                                     Long p_changerUId,
	                                     Long p_changeTs,
	                                     long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		intValue = p_intValue;
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
				        "INSERT INTO PERSISTENT_VALUE (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "INT_VALUE" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "PersistentValue: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];

		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setString(2, name);
			if (intValue == null)
				myInsert.setNull(3, Types.INTEGER);
			else
				myInsert.setInt(3, intValue.intValue());
			myInsert.setLong (4, creatorUId.longValue());
			myInsert.setLong (5, createTs.longValue());
			myInsert.setLong (6, changerUId.longValue());
			myInsert.setLong (7, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "PersistentValue: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM PERSISTENT_VALUE WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "PersistentValue: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "PersistentValue: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE PERSISTENT_VALUE SET " +
				        "" + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "INT_VALUE" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "PersistentValue: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setString(1, name);
			if (intValue == null)
				myUpdate.setNull(2, Types.INTEGER);
			else
				myUpdate.setInt(2, intValue.intValue());
			myUpdate.setLong (3, creatorUId.longValue());
			myUpdate.setLong (4, createTs.longValue());
			myUpdate.setLong (5, changerUId.longValue());
			myUpdate.setLong (6, changeTs.longValue());
			myUpdate.setLong(7, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "PersistentValue: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : PersistentValue", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "intValue : " + intValue, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "name       : " + name + "\n" +
		        indentString + "intValue   : " + intValue + "\n" +
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
