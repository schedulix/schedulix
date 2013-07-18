/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

	public final static String __version = "SDMSUserGeneric $Revision: 2.6 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String SYSTEM = "SYSTEM";
	public final static String INTERNAL = "INTERNAL";
	public final static String NOBODY = "NOBODY";

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_passwd = 3;
	public final static int nr_isEnabled = 4;
	public final static int nr_defaultGId = 5;
	public final static int nr_deleteVersion = 6;
	public final static int nr_creatorUId = 7;
	public final static int nr_createTs = 8;
	public final static int nr_changerUId = 9;
	public final static int nr_changeTs = 10;

	public static String tableName = SDMSUserTableGeneric.tableName;

	protected String name;
	protected String passwd;
	protected Boolean isEnabled;
	protected Long defaultGId;
	protected Long deleteVersion;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSUserGeneric(
	        SystemEnvironment env,
	        String p_name,
	        String p_passwd,
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

	public	SDMSUserGeneric setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getPasswd (SystemEnvironment env)
	throws SDMSException
	{
		return (passwd);
	}

	public	SDMSUserGeneric setPasswd (SystemEnvironment env, String p_passwd)
	throws SDMSException
	{
		if(passwd.equals(p_passwd)) return this;
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSUserGeneric) change(env);
			if (p_passwd != null && p_passwd.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(User) Length of $1 exceeds maximum length $2", "passwd", "64")
				);
			}
			o.passwd = p_passwd;
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

	public Boolean getIsEnabled (SystemEnvironment env)
	throws SDMSException
	{
		return (isEnabled);
	}

	public	SDMSUserGeneric setIsEnabled (SystemEnvironment env, Boolean p_isEnabled)
	throws SDMSException
	{
		if(isEnabled.equals(p_isEnabled)) return this;
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
				);
			}
			o = (SDMSUserGeneric) change(env);
			o.isEnabled = p_isEnabled;
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

	public Long getDefaultGId (SystemEnvironment env)
	throws SDMSException
	{
		return (defaultGId);
	}

	public	SDMSUserGeneric setDefaultGId (SystemEnvironment env, Long p_defaultGId)
	throws SDMSException
	{
		if(defaultGId.equals(p_defaultGId)) return this;
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSUserGeneric) change(env);
			o.defaultGId = p_defaultGId;
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

	public Long getDeleteVersion (SystemEnvironment env)
	throws SDMSException
	{
		return (deleteVersion);
	}

	public	SDMSUserGeneric setDeleteVersion (SystemEnvironment env, Long p_deleteVersion)
	throws SDMSException
	{
		if(deleteVersion.equals(p_deleteVersion)) return this;
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

	SDMSUserGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
				);
			}
			o = (SDMSUserGeneric) change(env);
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

	SDMSUserGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(User) Change of system object not allowed")
				);
			}
			o = (SDMSUserGeneric) change(env);
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

	public	SDMSUserGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSUserGeneric) change(env);
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

	SDMSUserGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSUserGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSUserGeneric) change(env);
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
				        "INSERT INTO USERS (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "PASSWD" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "User: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setString(2, name);
			pInsert.setString(3, passwd);
			pInsert.setInt (4, isEnabled.booleanValue() ? 1 : 0);
			pInsert.setLong (5, defaultGId.longValue());
			pInsert.setLong (6, deleteVersion.longValue());
			pInsert.setLong (7, creatorUId.longValue());
			pInsert.setLong (8, createTs.longValue());
			pInsert.setLong (9, changerUId.longValue());
			pInsert.setLong (10, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "User: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM USERS WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "User: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "User: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				if (driverName.startsWith("MySQL")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "UPDATE USERS SET " +
				        "" + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "PASSWD" + equote + " = ? " +
				        ", " + squote + "IS_ENABLED" + equote + " = ? " +
				        ", " + squote + "DEFAULT_G_ID" + equote + " = ? " +
				        ", " + squote + "DELETE_VERSION" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "User: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setString(1, name);
			pUpdate.setString(2, passwd);
			pUpdate.setInt (3, isEnabled.booleanValue() ? 1 : 0);
			pUpdate.setLong (4, defaultGId.longValue());
			pUpdate.setLong (5, deleteVersion.longValue());
			pUpdate.setLong (6, creatorUId.longValue());
			pUpdate.setLong (7, createTs.longValue());
			pUpdate.setLong (8, changerUId.longValue());
			pUpdate.setLong (9, changeTs.longValue());
			pUpdate.setLong(10, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "User: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : User", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "passwd : " + passwd, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isEnabled : " + isEnabled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "defaultGId : " + defaultGId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "deleteVersion : " + deleteVersion, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "name          : " + name + "\n" +
		        indentString + "passwd        : " + passwd + "\n" +
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
