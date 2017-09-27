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

public class SDMSDependencyInstanceGeneric extends SDMSObject
	implements Cloneable
{

	public static final int OPEN = 0;
	public static final int FULFILLED = 1;
	public static final int FAILED = 2;
	public static final int BROKEN = 3;
	public static final int DEFERRED = 4;
	public static final int CANCELLED = 8;
	public static final int NO = 0;
	public static final int YES = 1;
	public static final int RECURSIVE = 2;
	public static final int AND = 1;
	public static final int OR = 2;

	public final static int nr_id = 1;
	public final static int nr_ddId = 2;
	public final static int nr_dependentId = 3;
	public final static int nr_dependentIdOrig = 4;
	public final static int nr_dependencyOperation = 5;
	public final static int nr_requiredId = 6;
	public final static int nr_state = 7;
	public final static int nr_ignore = 8;
	public final static int nr_diIdOrig = 9;
	public final static int nr_seVersion = 10;
	public final static int nr_creatorUId = 11;
	public final static int nr_createTs = 12;
	public final static int nr_changerUId = 13;
	public final static int nr_changeTs = 14;

	public static String tableName = SDMSDependencyInstanceTableGeneric.tableName;

	protected Long ddId;
	protected Long dependentId;
	protected Long dependentIdOrig;
	protected Integer dependencyOperation;
	protected Long requiredId;
	protected Integer state;
	protected Integer ignore;
	protected Long diIdOrig;
	protected Long seVersion;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSDependencyInstanceGeneric(
	        SystemEnvironment env,
	        Long p_ddId,
	        Long p_dependentId,
	        Long p_dependentIdOrig,
	        Integer p_dependencyOperation,
	        Long p_requiredId,
	        Integer p_state,
	        Integer p_ignore,
	        Long p_diIdOrig,
	        Long p_seVersion,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSDependencyInstanceTableGeneric.table);
		ddId = p_ddId;
		dependentId = p_dependentId;
		dependentIdOrig = p_dependentIdOrig;
		dependencyOperation = p_dependencyOperation;
		requiredId = p_requiredId;
		state = p_state;
		ignore = p_ignore;
		diIdOrig = p_diIdOrig;
		seVersion = p_seVersion;
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

	public	void setDdId (SystemEnvironment env, Long p_ddId)
	throws SDMSException
	{
		if(ddId.equals(p_ddId)) return;
		SDMSDependencyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyInstanceGeneric) change(env);
			o.ddId = p_ddId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 17);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getDependentId (SystemEnvironment env)
	throws SDMSException
	{
		return (dependentId);
	}

	public	void setDependentId (SystemEnvironment env, Long p_dependentId)
	throws SDMSException
	{
		if(dependentId.equals(p_dependentId)) return;
		SDMSDependencyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyInstanceGeneric) change(env);
			o.dependentId = p_dependentId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 50);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getDependentIdOrig (SystemEnvironment env)
	throws SDMSException
	{
		return (dependentIdOrig);
	}

	public	void setDependentIdOrig (SystemEnvironment env, Long p_dependentIdOrig)
	throws SDMSException
	{
		if(dependentIdOrig.equals(p_dependentIdOrig)) return;
		SDMSDependencyInstanceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
		o.dependentIdOrig = p_dependentIdOrig;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getDependencyOperation (SystemEnvironment env)
	throws SDMSException
	{
		return (dependencyOperation);
	}

	public String getDependencyOperationAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getDependencyOperation (env);
		switch (v.intValue()) {
			case SDMSDependencyInstance.AND:
				return "AND";
			case SDMSDependencyInstance.OR:
				return "OR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown DependencyInstance.dependencyOperation: $1",
		                          getDependencyOperation (env)));
	}

	public	void setDependencyOperation (SystemEnvironment env, Integer p_dependencyOperation)
	throws SDMSException
	{
		if(dependencyOperation.equals(p_dependencyOperation)) return;
		SDMSDependencyInstanceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
		o.dependencyOperation = p_dependencyOperation;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getRequiredId (SystemEnvironment env)
	throws SDMSException
	{
		return (requiredId);
	}

	public	void setRequiredId (SystemEnvironment env, Long p_requiredId)
	throws SDMSException
	{
		if(requiredId.equals(p_requiredId)) return;
		SDMSDependencyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyInstanceGeneric) change(env);
			o.requiredId = p_requiredId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 52);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		return (state);
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getState (env);
		switch (v.intValue()) {
			case SDMSDependencyInstance.OPEN:
				return "OPEN";
			case SDMSDependencyInstance.FULFILLED:
				return "FULFILLED";
			case SDMSDependencyInstance.FAILED:
				return "FAILED";
			case SDMSDependencyInstance.BROKEN:
				return "BROKEN";
			case SDMSDependencyInstance.DEFERRED:
				return "DEFERRED";
			case SDMSDependencyInstance.CANCELLED:
				return "CANCELLED";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown DependencyInstance.state: $1",
		                          getState (env)));
	}

	public	void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		if(state.equals(p_state)) return;
		SDMSDependencyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyInstanceGeneric) change(env);
			o.state = p_state;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 32);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getIgnore (SystemEnvironment env)
	throws SDMSException
	{
		return (ignore);
	}

	public String getIgnoreAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getIgnore (env);
		switch (v.intValue()) {
			case SDMSDependencyInstance.NO:
				return "NO";
			case SDMSDependencyInstance.YES:
				return "YES";
			case SDMSDependencyInstance.RECURSIVE:
				return "RECURSIVE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown DependencyInstance.ignore: $1",
		                          getIgnore (env)));
	}

	public	void setIgnore (SystemEnvironment env, Integer p_ignore)
	throws SDMSException
	{
		if(ignore.equals(p_ignore)) return;
		SDMSDependencyInstanceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
		o.ignore = p_ignore;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getDiIdOrig (SystemEnvironment env)
	throws SDMSException
	{
		return (diIdOrig);
	}

	public	void setDiIdOrig (SystemEnvironment env, Long p_diIdOrig)
	throws SDMSException
	{
		if(diIdOrig.equals(p_diIdOrig)) return;
		SDMSDependencyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyInstanceGeneric) change(env);
			o.diIdOrig = p_diIdOrig;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 8);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getSeVersion (SystemEnvironment env)
	throws SDMSException
	{
		return (seVersion);
	}

	public	void setSeVersion (SystemEnvironment env, Long p_seVersion)
	throws SDMSException
	{
		if(seVersion.equals(p_seVersion)) return;
		SDMSDependencyInstanceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
		o.seVersion = p_seVersion;
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
		SDMSDependencyInstanceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
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
		SDMSDependencyInstanceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(DependencyInstance) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
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
		SDMSDependencyInstanceGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
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
		SDMSDependencyInstanceGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSDependencyInstanceGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSDependencyInstanceGeneric set_DdIdDependentIdRequiredId (SystemEnvironment env, Long p_ddId, Long p_dependentId, Long p_requiredId)
	throws SDMSException
	{
		SDMSDependencyInstanceGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(DependencyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyInstanceGeneric) change(env);
			o.ddId = p_ddId;
			o.dependentId = p_dependentId;
			o.requiredId = p_requiredId;
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

	public SDMSDependencyInstanceGeneric set_DependentIdRequiredIdState (SystemEnvironment env, Long p_dependentId, Long p_requiredId, Integer p_state)
	throws SDMSException
	{
		SDMSDependencyInstanceGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(DependencyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSDependencyInstanceGeneric) change(env);
			o.dependentId = p_dependentId;
			o.requiredId = p_requiredId;
			o.state = p_state;
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
		return SDMSDependencyInstance.getProxy(sysEnv, this);
	}

	protected SDMSDependencyInstanceGeneric(Long p_id,
	                                        Long p_ddId,
	                                        Long p_dependentId,
	                                        Long p_dependentIdOrig,
	                                        Integer p_dependencyOperation,
	                                        Long p_requiredId,
	                                        Integer p_state,
	                                        Integer p_ignore,
	                                        Long p_diIdOrig,
	                                        Long p_seVersion,
	                                        Long p_creatorUId,
	                                        Long p_createTs,
	                                        Long p_changerUId,
	                                        Long p_changeTs,
	                                        long p_validFrom, long p_validTo)
	{
		id     = p_id;
		ddId = p_ddId;
		dependentId = p_dependentId;
		dependentIdOrig = p_dependentIdOrig;
		dependencyOperation = p_dependencyOperation;
		requiredId = p_requiredId;
		state = p_state;
		ignore = p_ignore;
		diIdOrig = p_diIdOrig;
		seVersion = p_seVersion;
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
				        "INSERT INTO DEPENDENCY_INSTANCE (" +
				        "ID" +
				        ", " + squote + "DD_ID" + equote +
				        ", " + squote + "DEPENDENT_ID" + equote +
				        ", " + squote + "DEPENDENT_ID_ORIG" + equote +
				        ", " + squote + "DEPENDENCY_OPERATION" + equote +
				        ", " + squote + "REQUIRED_ID" + equote +
				        ", " + squote + "STATE" + equote +
				        ", " + squote + "IGNORE" + equote +
				        ", " + squote + "DI_ID_ORIG" + equote +
				        ", " + squote + "SE_VERSION" + equote +
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
				        ", ?" +
				        ", ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "DependencyInstance: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, ddId.longValue());
			myInsert.setLong (3, dependentId.longValue());
			myInsert.setLong (4, dependentIdOrig.longValue());
			myInsert.setInt(5, dependencyOperation.intValue());
			myInsert.setLong (6, requiredId.longValue());
			myInsert.setInt(7, state.intValue());
			myInsert.setInt(8, ignore.intValue());
			myInsert.setLong (9, diIdOrig.longValue());
			myInsert.setLong (10, seVersion.longValue());
			myInsert.setLong (11, creatorUId.longValue());
			myInsert.setLong (12, createTs.longValue());
			myInsert.setLong (13, changerUId.longValue());
			myInsert.setLong (14, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "DependencyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM DEPENDENCY_INSTANCE WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "DependencyInstance: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "DependencyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE DEPENDENCY_INSTANCE SET " +
				        "" + squote + "DD_ID" + equote + " = ? " +
				        ", " + squote + "DEPENDENT_ID" + equote + " = ? " +
				        ", " + squote + "DEPENDENT_ID_ORIG" + equote + " = ? " +
				        ", " + squote + "DEPENDENCY_OPERATION" + equote + " = ? " +
				        ", " + squote + "REQUIRED_ID" + equote + " = ? " +
				        ", " + squote + "STATE" + equote + " = ? " +
				        ", " + squote + "IGNORE" + equote + " = ? " +
				        ", " + squote + "DI_ID_ORIG" + equote + " = ? " +
				        ", " + squote + "SE_VERSION" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "DependencyInstance: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, ddId.longValue());
			myUpdate.setLong (2, dependentId.longValue());
			myUpdate.setLong (3, dependentIdOrig.longValue());
			myUpdate.setInt(4, dependencyOperation.intValue());
			myUpdate.setLong (5, requiredId.longValue());
			myUpdate.setInt(6, state.intValue());
			myUpdate.setInt(7, ignore.intValue());
			myUpdate.setLong (8, diIdOrig.longValue());
			myUpdate.setLong (9, seVersion.longValue());
			myUpdate.setLong (10, creatorUId.longValue());
			myUpdate.setLong (11, createTs.longValue());
			myUpdate.setLong (12, changerUId.longValue());
			myUpdate.setLong (13, changeTs.longValue());
			myUpdate.setLong(14, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "DependencyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkDependencyOperation(Integer p)
	{
		switch (p.intValue()) {
			case SDMSDependencyInstance.AND:
			case SDMSDependencyInstance.OR:
				return true;
		}
		return false;
	}
	static public boolean checkState(Integer p)
	{
		switch (p.intValue()) {
			case SDMSDependencyInstance.OPEN:
			case SDMSDependencyInstance.FULFILLED:
			case SDMSDependencyInstance.FAILED:
			case SDMSDependencyInstance.BROKEN:
			case SDMSDependencyInstance.DEFERRED:
			case SDMSDependencyInstance.CANCELLED:
				return true;
		}
		return false;
	}
	static public boolean checkIgnore(Integer p)
	{
		switch (p.intValue()) {
			case SDMSDependencyInstance.NO:
			case SDMSDependencyInstance.YES:
			case SDMSDependencyInstance.RECURSIVE:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : DependencyInstance", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ddId : " + ddId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "dependentId : " + dependentId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "dependentIdOrig : " + dependentIdOrig, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "dependencyOperation : " + dependencyOperation, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "requiredId : " + requiredId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "state : " + state, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ignore : " + ignore, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "diIdOrig : " + diIdOrig, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seVersion : " + seVersion, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "ddId                : " + ddId + "\n" +
		        indentString + "dependentId         : " + dependentId + "\n" +
		        indentString + "dependentIdOrig     : " + dependentIdOrig + "\n" +
		        indentString + "dependencyOperation : " + dependencyOperation + "\n" +
		        indentString + "requiredId          : " + requiredId + "\n" +
		        indentString + "state               : " + state + "\n" +
		        indentString + "ignore              : " + ignore + "\n" +
		        indentString + "diIdOrig            : " + diIdOrig + "\n" +
		        indentString + "seVersion           : " + seVersion + "\n" +
		        indentString + "creatorUId          : " + creatorUId + "\n" +
		        indentString + "createTs            : " + createTs + "\n" +
		        indentString + "changerUId          : " + changerUId + "\n" +
		        indentString + "changeTs            : " + changeTs + "\n" +
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
