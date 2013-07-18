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

public class SDMSParameterDefinitionGeneric extends SDMSObject
	implements Cloneable
{

	public final static String __version = "SDMSParameterDefinitionGeneric $Revision: 2.11 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public static final int REFERENCE = 10;
	public static final int CHILDREFERENCE = 20;
	public static final int CONSTANT = 30;
	public static final int RESULT = 40;
	public static final int PARAMETER = 50;
	public static final int EXPRESSION = 60;
	public static final int IMPORT = 70;
	public static final int DYNAMIC = 80;
	public static final int DYNAMICVALUE = 81;
	public static final int LOCAL_CONSTANT = 90;
	public static final int RESOURCEREFERENCE = 91;
	public static final int NONE = 0;
	public static final int AVG = 61;
	public static final int COUNT = 62;
	public static final int MIN = 63;
	public static final int MAX = 64;
	public static final int SUM = 65;

	public final static int nr_id = 1;
	public final static int nr_seId = 2;
	public final static int nr_name = 3;
	public final static int nr_type = 4;
	public final static int nr_aggFunction = 5;
	public final static int nr_defaultValue = 6;
	public final static int nr_isLocal = 7;
	public final static int nr_linkPdId = 8;
	public final static int nr_creatorUId = 9;
	public final static int nr_createTs = 10;
	public final static int nr_changerUId = 11;
	public final static int nr_changeTs = 12;

	public static String tableName = SDMSParameterDefinitionTableGeneric.tableName;

	protected Long seId;
	protected String name;
	protected Integer type;
	protected Integer aggFunction;
	protected String defaultValue;
	protected Boolean isLocal;
	protected Long linkPdId;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSParameterDefinitionGeneric(
	        SystemEnvironment env,
	        Long p_seId,
	        String p_name,
	        Integer p_type,
	        Integer p_aggFunction,
	        String p_defaultValue,
	        Boolean p_isLocal,
	        Long p_linkPdId,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSParameterDefinitionTableGeneric.table);
		seId = p_seId;
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ParameterDefinition) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		type = p_type;
		aggFunction = p_aggFunction;
		if (p_defaultValue != null && p_defaultValue.length() > 256) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ParameterDefinition) Length of $1 exceeds maximum length $2", "defaultValue", "256")
			);
		}
		defaultValue = p_defaultValue;
		isLocal = p_isLocal;
		linkPdId = p_linkPdId;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	SDMSParameterDefinitionGeneric setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			o.seId = p_seId;
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

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		return (name);
	}

	public	SDMSParameterDefinitionGeneric setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(ParameterDefinition) Length of $1 exceeds maximum length $2", "name", "64")
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

	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		return (type);
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getType (env);
		switch (v.intValue()) {
		case SDMSParameterDefinition.REFERENCE:
			return "REFERENCE";
		case SDMSParameterDefinition.CHILDREFERENCE:
			return "CHILDREFERENCE";
		case SDMSParameterDefinition.CONSTANT:
			return "CONSTANT";
		case SDMSParameterDefinition.RESULT:
			return "RESULT";
		case SDMSParameterDefinition.PARAMETER:
			return "PARAMETER";
		case SDMSParameterDefinition.EXPRESSION:
			return "EXPRESSION";
		case SDMSParameterDefinition.IMPORT:
			return "IMPORT";
		case SDMSParameterDefinition.DYNAMIC:
			return "DYNAMIC";
		case SDMSParameterDefinition.DYNAMICVALUE:
			return "DYNAMICVALUE";
		case SDMSParameterDefinition.LOCAL_CONSTANT:
			return "LOCAL_CONSTANT";
		case SDMSParameterDefinition.RESOURCEREFERENCE:
			return "RESOURCEREFERENCE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ParameterDefinition.type: $1",
		                          getType (env)));
	}

	public	SDMSParameterDefinitionGeneric setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		if(type.equals(p_type)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			o.type = p_type;
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

	public Integer getAggFunction (SystemEnvironment env)
	throws SDMSException
	{
		return (aggFunction);
	}

	public String getAggFunctionAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getAggFunction (env);
		switch (v.intValue()) {
		case SDMSParameterDefinition.NONE:
			return "NONE";
		case SDMSParameterDefinition.AVG:
			return "AVG";
		case SDMSParameterDefinition.COUNT:
			return "COUNT";
		case SDMSParameterDefinition.MIN:
			return "MIN";
		case SDMSParameterDefinition.MAX:
			return "MAX";
		case SDMSParameterDefinition.SUM:
			return "SUM";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ParameterDefinition.aggFunction: $1",
		                          getAggFunction (env)));
	}

	public	SDMSParameterDefinitionGeneric setAggFunction (SystemEnvironment env, Integer p_aggFunction)
	throws SDMSException
	{
		if(aggFunction.equals(p_aggFunction)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			o.aggFunction = p_aggFunction;
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

	public String getDefaultValue (SystemEnvironment env)
	throws SDMSException
	{
		return (defaultValue);
	}

	public	SDMSParameterDefinitionGeneric setDefaultValue (SystemEnvironment env, String p_defaultValue)
	throws SDMSException
	{
		if(p_defaultValue != null && p_defaultValue.equals(defaultValue)) return this;
		if(p_defaultValue == null && defaultValue == null) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			if (p_defaultValue != null && p_defaultValue.length() > 256) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(ParameterDefinition) Length of $1 exceeds maximum length $2", "defaultValue", "256")
				);
			}
			o.defaultValue = p_defaultValue;
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

	public Boolean getIsLocal (SystemEnvironment env)
	throws SDMSException
	{
		return (isLocal);
	}

	public	SDMSParameterDefinitionGeneric setIsLocal (SystemEnvironment env, Boolean p_isLocal)
	throws SDMSException
	{
		if(isLocal.equals(p_isLocal)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			o.isLocal = p_isLocal;
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

	public Long getLinkPdId (SystemEnvironment env)
	throws SDMSException
	{
		return (linkPdId);
	}

	public	SDMSParameterDefinitionGeneric setLinkPdId (SystemEnvironment env, Long p_linkPdId)
	throws SDMSException
	{
		if(p_linkPdId != null && p_linkPdId.equals(linkPdId)) return this;
		if(p_linkPdId == null && linkPdId == null) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			o.linkPdId = p_linkPdId;
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

	SDMSParameterDefinitionGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
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

	SDMSParameterDefinitionGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
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

	public	SDMSParameterDefinitionGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSParameterDefinitionGeneric) change(env);
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

	SDMSParameterDefinitionGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSParameterDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSParameterDefinitionGeneric) change(env);
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

	public SDMSParameterDefinitionGeneric set_SeIdName (SystemEnvironment env, Long p_seId, String p_name)
	throws SDMSException
	{
		SDMSParameterDefinitionGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ParameterDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSParameterDefinitionGeneric) change(env);
			o.seId = p_seId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290026",
				                        "(ParameterDefinition) Length of $1 exceeds maximum length $2", "changeTs", "64")
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

	protected SDMSProxy toProxy()
	{
		return new SDMSParameterDefinition(this);
	}

	protected SDMSParameterDefinitionGeneric(Long p_id,
	                Long p_seId,
	                String p_name,
	                Integer p_type,
	                Integer p_aggFunction,
	                String p_defaultValue,
	                Boolean p_isLocal,
	                Long p_linkPdId,
	                Long p_creatorUId,
	                Long p_createTs,
	                Long p_changerUId,
	                Long p_changeTs,
	                long p_validFrom, long p_validTo)
	{
		id     = p_id;
		seId = p_seId;
		name = p_name;
		type = p_type;
		aggFunction = p_aggFunction;
		defaultValue = p_defaultValue;
		isLocal = p_isLocal;
		linkPdId = p_linkPdId;
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
				        "INSERT INTO PARAMETER_DEFINITION (" +
				        "ID" +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "TYPE" + equote +
				        ", " + squote + "AGG_FUNCTION" + equote +
				        ", " + squote + "DEFAULTVALUE" + equote +
				        ", " + squote + "IS_LOCAL" + equote +
				        ", " + squote + "LINK_PD_ID" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "ParameterDefinition: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, seId.longValue());
			pInsert.setString(3, name);
			pInsert.setInt(4, type.intValue());
			pInsert.setInt(5, aggFunction.intValue());
			if (defaultValue == null)
				pInsert.setNull(6, Types.VARCHAR);
			else
				pInsert.setString(6, defaultValue);
			pInsert.setInt (7, isLocal.booleanValue() ? 1 : 0);
			if (linkPdId == null)
				pInsert.setNull(8, Types.INTEGER);
			else
				pInsert.setLong (8, linkPdId.longValue());
			pInsert.setLong (9, creatorUId.longValue());
			pInsert.setLong (10, createTs.longValue());
			pInsert.setLong (11, changerUId.longValue());
			pInsert.setLong (12, changeTs.longValue());
			pInsert.setLong(13, env.tx.versionId);
			pInsert.setLong(14, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "ParameterDefinition: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE PARAMETER_DEFINITION " +
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
				throw new FatalException(new SDMSMessage(env, "01110181955", "ParameterDefinition : $1\n$2", stmt, sqle.toString()));
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

			throw new FatalException(new SDMSMessage(env, "01110181956", "ParameterDefinition: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkType(Integer p)
	{
		switch (p.intValue()) {
		case SDMSParameterDefinition.REFERENCE:
		case SDMSParameterDefinition.CHILDREFERENCE:
		case SDMSParameterDefinition.CONSTANT:
		case SDMSParameterDefinition.RESULT:
		case SDMSParameterDefinition.PARAMETER:
		case SDMSParameterDefinition.EXPRESSION:
		case SDMSParameterDefinition.IMPORT:
		case SDMSParameterDefinition.DYNAMIC:
		case SDMSParameterDefinition.DYNAMICVALUE:
		case SDMSParameterDefinition.LOCAL_CONSTANT:
		case SDMSParameterDefinition.RESOURCEREFERENCE:
			return true;
		}
		return false;
	}
	static public boolean checkAggFunction(Integer p)
	{
		switch (p.intValue()) {
		case SDMSParameterDefinition.NONE:
		case SDMSParameterDefinition.AVG:
		case SDMSParameterDefinition.COUNT:
		case SDMSParameterDefinition.MIN:
		case SDMSParameterDefinition.MAX:
		case SDMSParameterDefinition.SUM:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ParameterDefinition", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "type : " + type, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "aggFunction : " + aggFunction, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "defaultValue : " + defaultValue, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isLocal : " + isLocal, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "linkPdId : " + linkPdId, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "seId         : " + seId + "\n" +
		        indentString + "name         : " + name + "\n" +
		        indentString + "type         : " + type + "\n" +
		        indentString + "aggFunction  : " + aggFunction + "\n" +
		        indentString + "defaultValue : " + defaultValue + "\n" +
		        indentString + "isLocal      : " + isLocal + "\n" +
		        indentString + "linkPdId     : " + linkPdId + "\n" +
		        indentString + "creatorUId   : " + creatorUId + "\n" +
		        indentString + "createTs     : " + createTs + "\n" +
		        indentString + "changerUId   : " + changerUId + "\n" +
		        indentString + "changeTs     : " + changeTs + "\n" +
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
