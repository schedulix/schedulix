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

public class SDMSUserGeneric extends SDMSObject
	implements Cloneable
{

	public final static String SYSTEM = "SYSTEM";
	public final static String INTERNAL = "INTERNAL";
	public final static String NOBODY = "NOBODY";
	public final static int MD5 = 0;
	public final static int SHA256 = 1;
	public final static int SALT_LENGTH = 64;

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_passwd = 3;
	public final static int nr_salt = 4;
	public final static int nr_method = 5;
	public final static int nr_isEnabled = 6;
	public final static int nr_defaultGId = 7;
	public final static int nr_deleteVersion = 8;
	public final static int nr_creatorUId = 9;
	public final static int nr_createTs = 10;
	public final static int nr_changerUId = 11;
	public final static int nr_changeTs = 12;

	public static String tableName = SDMSUserTableGeneric.tableName;

	protected String name;
	protected String passwd;
	protected String salt;
	protected Integer method;
	protected Boolean isEnabled;
	protected Long defaultGId;
	protected Long deleteVersion;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSUserGeneric(
		SystemEnvironment env,
		String p_name,
		String p_passwd,
		String p_salt,
		Integer p_method,
		Boolean p_isEnabled,
		Long p_defaultGId,
		Long p_deleteVersion,
		Long p_creatorUId,
		Long p_createTs,
		Long p_changerUId,
		Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSUserTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(User) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		if (p_passwd != null && p_passwd.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(User) Length of $1 exceeds maximum length $2", "passwd", "64")
			);
		}
		passwd = p_passwd;
		if (p_salt != null && p_salt.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(User) Length of $1 exceeds maximum length $2", "salt", "64")
			);
		}
		salt = p_salt;
		method = p_method;
		isEnabled = p_isEnabled;
		defaultGId = p_defaultGId;
		deleteVersion = p_deleteVersion;
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
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
				);
			}
			o = (SDMSUserGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(User) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 3);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getPasswd (SystemEnvironment env)
	throws SDMSException
	{
		return (passwd);
	}

	public	void setPasswd (SystemEnvironment env, String p_passwd)
	throws SDMSException
	{
		if(passwd.equals(p_passwd)) return;
		SDMSUserGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
		if (p_passwd != null && p_passwd.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(User) Length of $1 exceeds maximum length $2", "passwd", "64")
			);
		}
		o.passwd = p_passwd;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getSalt (SystemEnvironment env)
	throws SDMSException
	{
		return (salt);
	}

	public	void setSalt (SystemEnvironment env, String p_salt)
	throws SDMSException
	{
		if(p_salt != null && p_salt.equals(salt)) return;
		if(p_salt == null && salt == null) return;
		SDMSUserGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
		if (p_salt != null && p_salt.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(User) Length of $1 exceeds maximum length $2", "salt", "64")
			);
		}
		o.salt = p_salt;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getMethod (SystemEnvironment env)
	throws SDMSException
	{
		return (method);
	}

	public String getMethodAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getMethod (env);
		switch (v.intValue()) {
		case SDMSUser.MD5:
			return "MD5";
		case SDMSUser.SHA256:
			return "SHA256";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown User.method: $1",
		                          getMethod (env)));
	}

	public	void setMethod (SystemEnvironment env, Integer p_method)
	throws SDMSException
	{
		if(method.equals(p_method)) return;
		SDMSUserGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
		o.method = p_method;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
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
		SDMSUserGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
		o.isEnabled = p_isEnabled;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getDefaultGId (SystemEnvironment env)
	throws SDMSException
	{
		return (defaultGId);
	}

	public	void setDefaultGId (SystemEnvironment env, Long p_defaultGId)
	throws SDMSException
	{
		if(defaultGId.equals(p_defaultGId)) return;
		SDMSUserGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
		o.defaultGId = p_defaultGId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getDeleteVersion (SystemEnvironment env)
	throws SDMSException
	{
		return (deleteVersion);
	}

	public	void setDeleteVersion (SystemEnvironment env, Long p_deleteVersion)
	throws SDMSException
	{
		if(deleteVersion.equals(p_deleteVersion)) return;
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
				);
			}
			o = (SDMSUserGeneric) change(env);
			o.deleteVersion = p_deleteVersion;
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

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return;
		SDMSUserGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
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
		SDMSUserGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
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
		SDMSUserGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
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
		SDMSUserGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSUserGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSUserGeneric set_NameDeleteVersion (SystemEnvironment env, String p_name, Long p_deleteVersion)
	throws SDMSException
	{
		SDMSUserGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(User) Change of system object not allowed")
				);
			}
			o = (SDMSUserGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290026",
				                        "(User) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.name = p_name;
			o.deleteVersion = p_deleteVersion;
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
		return new SDMSUser(this);
	}

	protected SDMSUserGeneric(Long p_id,
	                          String p_name,
	                          String p_passwd,
	                          String p_salt,
	                          Integer p_method,
	                          Boolean p_isEnabled,
	                          Long p_defaultGId,
	                          Long p_deleteVersion,
	                          Long p_creatorUId,
	                          Long p_createTs,
	                          Long p_changerUId,
	                          Long p_changeTs,
	                          long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		passwd = p_passwd;
		salt = p_salt;
		method = p_method;
		isEnabled = p_isEnabled;
		defaultGId = p_defaultGId;
		deleteVersion = p_deleteVersion;
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
				        "INSERT INTO USERS (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "PASSWD" + equote +
				        ", " + squote + "SALT" + equote +
				        ", " + squote + "METHOD" + equote +
				        ", " + squote + "IS_ENABLED" + equote +
				        ", " + squote + "DEFAULT_G_ID" + equote +
				        ", " + squote + "DELETE_VERSION" + equote +
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

				throw new FatalException(new SDMSMessage(env, "01110181952", "User: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];

		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setString(2, name);
			myInsert.setString(3, passwd);
			if (salt == null)
				myInsert.setNull(4, Types.VARCHAR);
			else
				myInsert.setString(4, salt);
			myInsert.setInt(5, method.intValue());
			myInsert.setInt (6, isEnabled.booleanValue() ? 1 : 0);
			myInsert.setLong (7, defaultGId.longValue());
			myInsert.setLong (8, deleteVersion.longValue());
			myInsert.setLong (9, creatorUId.longValue());
			myInsert.setLong (10, createTs.longValue());
			myInsert.setLong (11, changerUId.longValue());
			myInsert.setLong (12, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "User: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM USERS WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "User: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "User: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE USERS SET " +
				        "" + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "PASSWD" + equote + " = ? " +
				        ", " + squote + "SALT" + equote + " = ? " +
				        ", " + squote + "METHOD" + equote + " = ? " +
				        ", " + squote + "IS_ENABLED" + equote + " = ? " +
				        ", " + squote + "DEFAULT_G_ID" + equote + " = ? " +
				        ", " + squote + "DELETE_VERSION" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "User: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setString(1, name);
			myUpdate.setString(2, passwd);
			if (salt == null)
				myUpdate.setNull(3, Types.VARCHAR);
			else
				myUpdate.setString(3, salt);
			myUpdate.setInt(4, method.intValue());
			myUpdate.setInt (5, isEnabled.booleanValue() ? 1 : 0);
			myUpdate.setLong (6, defaultGId.longValue());
			myUpdate.setLong (7, deleteVersion.longValue());
			myUpdate.setLong (8, creatorUId.longValue());
			myUpdate.setLong (9, createTs.longValue());
			myUpdate.setLong (10, changerUId.longValue());
			myUpdate.setLong (11, changeTs.longValue());
			myUpdate.setLong(12, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "User: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkMethod(Integer p)
	{
		switch (p.intValue()) {
		case SDMSUser.MD5:
		case SDMSUser.SHA256:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : User", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "passwd : " + passwd, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "salt : " + salt, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "method : " + method, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isEnabled : " + isEnabled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "defaultGId : " + defaultGId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "deleteVersion : " + deleteVersion, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "name          : " + name + "\n" +
		        indentString + "passwd        : " + passwd + "\n" +
		        indentString + "salt          : " + salt + "\n" +
		        indentString + "method        : " + method + "\n" +
		        indentString + "isEnabled     : " + isEnabled + "\n" +
		        indentString + "defaultGId    : " + defaultGId + "\n" +
		        indentString + "deleteVersion : " + deleteVersion + "\n" +
		        indentString + "creatorUId    : " + creatorUId + "\n" +
		        indentString + "createTs      : " + createTs + "\n" +
		        indentString + "changerUId    : " + changerUId + "\n" +
		        indentString + "changeTs      : " + changeTs + "\n" +
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
