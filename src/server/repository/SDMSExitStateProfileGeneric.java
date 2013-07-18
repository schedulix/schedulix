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

public class SDMSExitStateProfileGeneric extends SDMSObject
	implements Cloneable
{

	public final static String __version = "SDMSExitStateProfileGeneric $Revision: 2.4 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_defaultEsmpId = 3;
	public final static int nr_isValid = 4;
	public final static int nr_creatorUId = 5;
	public final static int nr_createTs = 6;
	public final static int nr_changerUId = 7;
	public final static int nr_changeTs = 8;

	public static String tableName = SDMSExitStateProfileTableGeneric.tableName;

	protected String name;
	protected Long defaultEsmpId;
	protected Boolean isValid;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSExitStateProfileGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Long p_defaultEsmpId,
	        Boolean p_isValid,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSExitStateProfileTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ExitStateProfile) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		defaultEsmpId = p_defaultEsmpId;
		isValid = p_isValid;
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

	public	SDMSExitStateProfileGeneric setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return this;
		SDMSExitStateProfileGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateProfile) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateProfileGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(ExitStateProfile) Length of $1 exceeds maximum length $2", "name", "64")
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

	public Long getDefaultEsmpId (SystemEnvironment env)
	throws SDMSException
	{
		return (defaultEsmpId);
	}

	public	SDMSExitStateProfileGeneric setDefaultEsmpId (SystemEnvironment env, Long p_defaultEsmpId)
	throws SDMSException
	{
		if(p_defaultEsmpId != null && p_defaultEsmpId.equals(defaultEsmpId)) return this;
		if(p_defaultEsmpId == null && defaultEsmpId == null) return this;
		SDMSExitStateProfileGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateProfile) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateProfileGeneric) change(env);
			o.defaultEsmpId = p_defaultEsmpId;
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

	public Boolean getIsValid (SystemEnvironment env)
	throws SDMSException
	{
		return (isValid);
	}

	public	SDMSExitStateProfileGeneric setIsValid (SystemEnvironment env, Boolean p_isValid)
	throws SDMSException
	{
		if(isValid.equals(p_isValid)) return this;
		SDMSExitStateProfileGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateProfile) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateProfileGeneric) change(env);
			o.isValid = p_isValid;
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

	SDMSExitStateProfileGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSExitStateProfileGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateProfile) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateProfileGeneric) change(env);
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

	SDMSExitStateProfileGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSExitStateProfileGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ExitStateProfile) Change of system object not allowed")
				);
			}
			o = (SDMSExitStateProfileGeneric) change(env);
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

	public	SDMSExitStateProfileGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSExitStateProfileGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSExitStateProfileGeneric) change(env);
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

	SDMSExitStateProfileGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSExitStateProfileGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSExitStateProfileGeneric) change(env);
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
		return new SDMSExitStateProfile(this);
	}

	protected SDMSExitStateProfileGeneric(Long p_id,
	                                      String p_name,
	                                      Long p_defaultEsmpId,
	                                      Boolean p_isValid,
	                                      Long p_creatorUId,
	                                      Long p_createTs,
	                                      Long p_changerUId,
	                                      Long p_changeTs,
	                                      long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		defaultEsmpId = p_defaultEsmpId;
		isValid = p_isValid;
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
				        "INSERT INTO EXIT_STATE_PROFILE (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "DEFAULT_ESMP_ID" + equote +
				        ", " + squote + "IS_VALID" + equote +
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

				throw new FatalException(new SDMSMessage(env, "01110181952", "ExitStateProfile: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setString(2, name);
			if (defaultEsmpId == null)
				pInsert.setNull(3, Types.INTEGER);
			else
				pInsert.setLong (3, defaultEsmpId.longValue());
			pInsert.setInt (4, isValid.booleanValue() ? 1 : 0);
			pInsert.setLong (5, creatorUId.longValue());
			pInsert.setLong (6, createTs.longValue());
			pInsert.setLong (7, changerUId.longValue());
			pInsert.setLong (8, changeTs.longValue());
			pInsert.setLong(9, env.tx.versionId);
			pInsert.setLong(10, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "ExitStateProfile: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE EXIT_STATE_PROFILE " +
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
				throw new FatalException(new SDMSMessage(env, "01110181955", "ExitStateProfile : $1\n$2", stmt, sqle.toString()));
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

			throw new FatalException(new SDMSMessage(env, "01110181956", "ExitStateProfile: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ExitStateProfile", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "defaultEsmpId : " + defaultEsmpId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isValid : " + isValid, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "defaultEsmpId : " + defaultEsmpId + "\n" +
		        indentString + "isValid       : " + isValid + "\n" +
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
