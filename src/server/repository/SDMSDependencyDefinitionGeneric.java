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

public class SDMSDependencyDefinitionGeneric extends SDMSObject
	implements Cloneable
{

	public static final int IGNORE = 1;
	public static final int UH_IGNORE = 1;
	public static final int ERROR = 2;
	public static final int UH_ERROR = 2;
	public static final int SUSPEND = 3;
	public static final int UH_SUSPEND = 3;
	public static final int DEFER = 4;
	public static final int DEFER_IGNORE = 5;
	public static final int ALL_FINAL = 1;
	public static final int JOB_FINAL = 2;
	public static final int FINAL = 0;
	public static final int ALL_REACHABLE = 1;
	public static final int UNREACHABLE = 2;
	public static final int DEFAULT = 3;

	public final static int nr_id = 1;
	public final static int nr_seDependentId = 2;
	public final static int nr_seRequiredId = 3;
	public final static int nr_name = 4;
	public final static int nr_unresolvedHandling = 5;
	public final static int nr_mode = 6;
	public final static int nr_stateSelection = 7;
	public final static int nr_condition = 8;
	public final static int nr_creatorUId = 9;
	public final static int nr_createTs = 10;
	public final static int nr_changerUId = 11;
	public final static int nr_changeTs = 12;

	public static String tableName = SDMSDependencyDefinitionTableGeneric.tableName;

	protected Long seDependentId;
	protected Long seRequiredId;
	protected String name;
	protected Integer unresolvedHandling;
	protected Integer mode;
	protected Integer stateSelection;
	protected String condition;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSDependencyDefinitionGeneric(
	        SystemEnvironment env,
	        Long p_seDependentId,
	        Long p_seRequiredId,
	        String p_name,
	        Integer p_unresolvedHandling,
	        Integer p_mode,
	        Integer p_stateSelection,
	        String p_condition,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSDependencyDefinitionTableGeneric.table);
		seDependentId = p_seDependentId;
		seRequiredId = p_seRequiredId;
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(DependencyDefinition) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		unresolvedHandling = p_unresolvedHandling;
		mode = p_mode;
		stateSelection = p_stateSelection;
		if (p_condition != null && p_condition.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(DependencyDefinition) Length of $1 exceeds maximum length $2", "condition", "1024")
			);
		}
		condition = p_condition;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getSeDependentId (SystemEnvironment env)
	throws SDMSException
	{
		return (seDependentId);
	}

	public	void setSeDependentId (SystemEnvironment env, Long p_seDependentId)
	throws SDMSException
	{
		if(seDependentId.equals(p_seDependentId)) return;
		SDMSDependencyDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyDefinitionGeneric) change(env);
			o.seDependentId = p_seDependentId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 9);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getSeRequiredId (SystemEnvironment env)
	throws SDMSException
	{
		return (seRequiredId);
	}

	public	void setSeRequiredId (SystemEnvironment env, Long p_seRequiredId)
	throws SDMSException
	{
		if(seRequiredId.equals(p_seRequiredId)) return;
		SDMSDependencyDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyDefinitionGeneric) change(env);
			o.seRequiredId = p_seRequiredId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 10);
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
		if(p_name != null && p_name.equals(name)) return;
		if(p_name == null && name == null) return;
		SDMSDependencyDefinitionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyDefinitionGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(DependencyDefinition) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getUnresolvedHandling (SystemEnvironment env)
	throws SDMSException
	{
		return (unresolvedHandling);
	}

	public String getUnresolvedHandlingAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getUnresolvedHandling (env);
		switch (v.intValue()) {
			case SDMSDependencyDefinition.IGNORE:
				return "IGNORE";
			case SDMSDependencyDefinition.ERROR:
				return "ERROR";
			case SDMSDependencyDefinition.SUSPEND:
				return "SUSPEND";
			case SDMSDependencyDefinition.DEFER:
				return "DEFER";
			case SDMSDependencyDefinition.DEFER_IGNORE:
				return "DEFER_IGNORE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown DependencyDefinition.unresolvedHandling: $1",
		                          getUnresolvedHandling (env)));
	}

	public	void setUnresolvedHandling (SystemEnvironment env, Integer p_unresolvedHandling)
	throws SDMSException
	{
		if(unresolvedHandling.equals(p_unresolvedHandling)) return;
		SDMSDependencyDefinitionGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
		o.unresolvedHandling = p_unresolvedHandling;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getMode (SystemEnvironment env)
	throws SDMSException
	{
		return (mode);
	}

	public String getModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getMode (env);
		switch (v.intValue()) {
			case SDMSDependencyDefinition.ALL_FINAL:
				return "ALL_FINAL";
			case SDMSDependencyDefinition.JOB_FINAL:
				return "JOB_FINAL";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown DependencyDefinition.mode: $1",
		                          getMode (env)));
	}

	public	void setMode (SystemEnvironment env, Integer p_mode)
	throws SDMSException
	{
		if(mode.equals(p_mode)) return;
		SDMSDependencyDefinitionGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
		o.mode = p_mode;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getStateSelection (SystemEnvironment env)
	throws SDMSException
	{
		return (stateSelection);
	}

	public String getStateSelectionAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getStateSelection (env);
		switch (v.intValue()) {
			case SDMSDependencyDefinition.FINAL:
				return "FINAL";
			case SDMSDependencyDefinition.ALL_REACHABLE:
				return "ALL_REACHABLE";
			case SDMSDependencyDefinition.UNREACHABLE:
				return "UNREACHABLE";
			case SDMSDependencyDefinition.DEFAULT:
				return "DEFAULT";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown DependencyDefinition.stateSelection: $1",
		                          getStateSelection (env)));
	}

	public	void setStateSelection (SystemEnvironment env, Integer p_stateSelection)
	throws SDMSException
	{
		if(stateSelection.equals(p_stateSelection)) return;
		SDMSDependencyDefinitionGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
		o.stateSelection = p_stateSelection;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		return (condition);
	}

	public	void setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		if(p_condition != null && p_condition.equals(condition)) return;
		if(p_condition == null && condition == null) return;
		SDMSDependencyDefinitionGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
		if (p_condition != null && p_condition.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(DependencyDefinition) Length of $1 exceeds maximum length $2", "condition", "1024")
			);
		}
		o.condition = p_condition;
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
		SDMSDependencyDefinitionGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
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
		SDMSDependencyDefinitionGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyDefinition) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
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
		SDMSDependencyDefinitionGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
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
		SDMSDependencyDefinitionGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyDefinitionGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSDependencyDefinitionGeneric set_SeDependentIdSeRequiredId (SystemEnvironment env, Long p_seDependentId, Long p_seRequiredId)
	throws SDMSException
	{
		SDMSDependencyDefinitionGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(DependencyDefinition) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyDefinitionGeneric) change(env);
			o.seDependentId = p_seDependentId;
			o.seRequiredId = p_seRequiredId;
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
		return SDMSDependencyDefinition.getProxy(sysEnv, this);
	}

	protected SDMSDependencyDefinitionGeneric(Long p_id,
	                Long p_seDependentId,
	                Long p_seRequiredId,
	                String p_name,
	                Integer p_unresolvedHandling,
	                Integer p_mode,
	                Integer p_stateSelection,
	                String p_condition,
	                Long p_creatorUId,
	                Long p_createTs,
	                Long p_changerUId,
	                Long p_changeTs,
	                long p_validFrom, long p_validTo)
	{
		id     = p_id;
		seDependentId = p_seDependentId;
		seRequiredId = p_seRequiredId;
		name = p_name;
		unresolvedHandling = p_unresolvedHandling;
		mode = p_mode;
		stateSelection = p_stateSelection;
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
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "INSERT INTO DEPENDENCY_DEFINITION (" +
				        "ID" +
				        ", " + squote + "SE_DEPENDENT_ID" + equote +
				        ", " + squote + "SE_REQUIRED_ID" + equote +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "UNRESOLVED_HANDLING" + equote +
				        ", " + squote + "DMODE" + equote +
				        ", " + squote + "STATE_SELECTION" + equote +
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
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "DependencyDefinition: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, seDependentId.longValue());
			myInsert.setLong (3, seRequiredId.longValue());
			if (name == null)
				myInsert.setNull(4, Types.VARCHAR);
			else
				myInsert.setString(4, name);
			myInsert.setInt(5, unresolvedHandling.intValue());
			myInsert.setInt(6, mode.intValue());
			myInsert.setInt(7, stateSelection.intValue());
			if (condition == null)
				myInsert.setNull(8, Types.VARCHAR);
			else
				myInsert.setString(8, condition);
			myInsert.setLong (9, creatorUId.longValue());
			myInsert.setLong (10, createTs.longValue());
			myInsert.setLong (11, changerUId.longValue());
			myInsert.setLong (12, changeTs.longValue());
			myInsert.setLong(13, env.tx.versionId);
			myInsert.setLong(14, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "DependencyDefinition: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE DEPENDENCY_DEFINITION " +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181955", "DependencyDefinition : $1\n$2", stmt, sqle.toString()));
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
			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "DependencyDefinition: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkUnresolvedHandling(Integer p)
	{
		switch (p.intValue()) {
			case SDMSDependencyDefinition.IGNORE:
			case SDMSDependencyDefinition.ERROR:
			case SDMSDependencyDefinition.SUSPEND:
			case SDMSDependencyDefinition.DEFER:
			case SDMSDependencyDefinition.DEFER_IGNORE:
				return true;
		}
		return false;
	}
	static public boolean checkMode(Integer p)
	{
		switch (p.intValue()) {
			case SDMSDependencyDefinition.ALL_FINAL:
			case SDMSDependencyDefinition.JOB_FINAL:
				return true;
		}
		return false;
	}
	static public boolean checkStateSelection(Integer p)
	{
		switch (p.intValue()) {
			case SDMSDependencyDefinition.FINAL:
			case SDMSDependencyDefinition.ALL_REACHABLE:
			case SDMSDependencyDefinition.UNREACHABLE:
			case SDMSDependencyDefinition.DEFAULT:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : DependencyDefinition", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seDependentId : " + seDependentId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seRequiredId : " + seRequiredId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "unresolvedHandling : " + unresolvedHandling, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "mode : " + mode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "stateSelection : " + stateSelection, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "condition : " + condition, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "seDependentId      : " + seDependentId + "\n" +
		        indentString + "seRequiredId       : " + seRequiredId + "\n" +
		        indentString + "name               : " + name + "\n" +
		        indentString + "unresolvedHandling : " + unresolvedHandling + "\n" +
		        indentString + "mode               : " + mode + "\n" +
		        indentString + "stateSelection     : " + stateSelection + "\n" +
		        indentString + "condition          : " + condition + "\n" +
		        indentString + "creatorUId         : " + creatorUId + "\n" +
		        indentString + "createTs           : " + createTs + "\n" +
		        indentString + "changerUId         : " + changerUId + "\n" +
		        indentString + "changeTs           : " + changeTs + "\n" +
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
