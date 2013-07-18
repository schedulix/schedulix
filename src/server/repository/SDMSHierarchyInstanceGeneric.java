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

public class SDMSHierarchyInstanceGeneric extends SDMSObject
	implements Cloneable
{

	public final static String __version = "SDMSHierarchyInstanceGeneric $Revision: 2.3 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static int nr_id = 1;
	public final static int nr_parentId = 2;
	public final static int nr_childId = 3;
	public final static int nr_shId = 4;
	public final static int nr_nice = 5;
	public final static int nr_childEsdId = 6;
	public final static int nr_childEsPreference = 7;
	public final static int nr_seVersionHi = 8;
	public final static int nr_creatorUId = 9;
	public final static int nr_createTs = 10;
	public final static int nr_changerUId = 11;
	public final static int nr_changeTs = 12;

	public static String tableName = SDMSHierarchyInstanceTableGeneric.tableName;

	protected Long parentId;
	protected Long childId;
	protected Long shId;
	protected Integer nice;
	protected Long childEsdId;
	protected Integer childEsPreference;
	protected Long seVersionHi;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSHierarchyInstanceGeneric(
	        SystemEnvironment env,
	        Long p_parentId,
	        Long p_childId,
	        Long p_shId,
	        Integer p_nice,
	        Long p_childEsdId,
	        Integer p_childEsPreference,
	        Long p_seVersionHi,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSHierarchyInstanceTableGeneric.table);
		parentId = p_parentId;
		childId = p_childId;
		shId = p_shId;
		nice = p_nice;
		childEsdId = p_childEsdId;
		childEsPreference = p_childEsPreference;
		seVersionHi = p_seVersionHi;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		return (parentId);
	}

	public	SDMSHierarchyInstanceGeneric setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		if(parentId.equals(p_parentId)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.parentId = p_parentId;
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

	public Long getChildId (SystemEnvironment env)
	throws SDMSException
	{
		return (childId);
	}

	public	SDMSHierarchyInstanceGeneric setChildId (SystemEnvironment env, Long p_childId)
	throws SDMSException
	{
		if(childId.equals(p_childId)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.childId = p_childId;
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

	public Long getShId (SystemEnvironment env)
	throws SDMSException
	{
		return (shId);
	}

	public	SDMSHierarchyInstanceGeneric setShId (SystemEnvironment env, Long p_shId)
	throws SDMSException
	{
		if(shId.equals(p_shId)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.shId = p_shId;
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

	public Integer getNice (SystemEnvironment env)
	throws SDMSException
	{
		return (nice);
	}

	public	SDMSHierarchyInstanceGeneric setNice (SystemEnvironment env, Integer p_nice)
	throws SDMSException
	{
		if(nice.equals(p_nice)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.nice = p_nice;
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

	public Long getChildEsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (childEsdId);
	}

	public	SDMSHierarchyInstanceGeneric setChildEsdId (SystemEnvironment env, Long p_childEsdId)
	throws SDMSException
	{
		if(p_childEsdId != null && p_childEsdId.equals(childEsdId)) return this;
		if(p_childEsdId == null && childEsdId == null) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.childEsdId = p_childEsdId;
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

	public Integer getChildEsPreference (SystemEnvironment env)
	throws SDMSException
	{
		return (childEsPreference);
	}

	public	SDMSHierarchyInstanceGeneric setChildEsPreference (SystemEnvironment env, Integer p_childEsPreference)
	throws SDMSException
	{
		if(p_childEsPreference != null && p_childEsPreference.equals(childEsPreference)) return this;
		if(p_childEsPreference == null && childEsPreference == null) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.childEsPreference = p_childEsPreference;
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

	public Long getSeVersionHi (SystemEnvironment env)
	throws SDMSException
	{
		return (seVersionHi);
	}

	public	SDMSHierarchyInstanceGeneric setSeVersionHi (SystemEnvironment env, Long p_seVersionHi)
	throws SDMSException
	{
		if(seVersionHi.equals(p_seVersionHi)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.seVersionHi = p_seVersionHi;
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

	SDMSHierarchyInstanceGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
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

	SDMSHierarchyInstanceGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
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

	public	SDMSHierarchyInstanceGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSHierarchyInstanceGeneric) change(env);
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

	SDMSHierarchyInstanceGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSHierarchyInstanceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSHierarchyInstanceGeneric) change(env);
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

	public SDMSHierarchyInstanceGeneric set_ParentIdChildId (SystemEnvironment env, Long p_parentId, Long p_childId)
	throws SDMSException
	{
		SDMSHierarchyInstanceGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(HierarchyInstance) Change of system object not allowed")
				);
			}
			o = (SDMSHierarchyInstanceGeneric) change(env);
			o.parentId = p_parentId;
			o.childId = p_childId;
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
		return new SDMSHierarchyInstance(this);
	}

	protected SDMSHierarchyInstanceGeneric(Long p_id,
	                                       Long p_parentId,
	                                       Long p_childId,
	                                       Long p_shId,
	                                       Integer p_nice,
	                                       Long p_childEsdId,
	                                       Integer p_childEsPreference,
	                                       Long p_seVersionHi,
	                                       Long p_creatorUId,
	                                       Long p_createTs,
	                                       Long p_changerUId,
	                                       Long p_changeTs,
	                                       long p_validFrom, long p_validTo)
	{
		id     = p_id;
		parentId = p_parentId;
		childId = p_childId;
		shId = p_shId;
		nice = p_nice;
		childEsdId = p_childEsdId;
		childEsPreference = p_childEsPreference;
		seVersionHi = p_seVersionHi;
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
				        "INSERT INTO HIERARCHY_INSTANCE (" +
				        "ID" +
				        ", " + squote + "PARENT_ID" + equote +
				        ", " + squote + "CHILD_ID" + equote +
				        ", " + squote + "SH_ID" + equote +
				        ", " + squote + "NICE" + equote +
				        ", " + squote + "CHILD_ESD_ID" + equote +
				        ", " + squote + "CHILD_ES_PREFERENCE" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "HierarchyInstance: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, parentId.longValue());
			pInsert.setLong (3, childId.longValue());
			pInsert.setLong (4, shId.longValue());
			pInsert.setInt(5, nice.intValue());
			if (childEsdId == null)
				pInsert.setNull(6, Types.INTEGER);
			else
				pInsert.setLong (6, childEsdId.longValue());
			if (childEsPreference == null)
				pInsert.setNull(7, Types.INTEGER);
			else
				pInsert.setInt(7, childEsPreference.intValue());
			pInsert.setLong (8, seVersionHi.longValue());
			pInsert.setLong (9, creatorUId.longValue());
			pInsert.setLong (10, createTs.longValue());
			pInsert.setLong (11, changerUId.longValue());
			pInsert.setLong (12, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "HierarchyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM HIERARCHY_INSTANCE WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "HierarchyInstance: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "HierarchyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE HIERARCHY_INSTANCE SET " +
				        "" + squote + "PARENT_ID" + equote + " = ? " +
				        ", " + squote + "CHILD_ID" + equote + " = ? " +
				        ", " + squote + "SH_ID" + equote + " = ? " +
				        ", " + squote + "NICE" + equote + " = ? " +
				        ", " + squote + "CHILD_ESD_ID" + equote + " = ? " +
				        ", " + squote + "CHILD_ES_PREFERENCE" + equote + " = ? " +
				        ", " + squote + "SE_VERSION" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "HierarchyInstance: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, parentId.longValue());
			pUpdate.setLong (2, childId.longValue());
			pUpdate.setLong (3, shId.longValue());
			pUpdate.setInt(4, nice.intValue());
			if (childEsdId == null)
				pUpdate.setNull(5, Types.INTEGER);
			else
				pUpdate.setLong (5, childEsdId.longValue());
			if (childEsPreference == null)
				pUpdate.setNull(6, Types.INTEGER);
			else
				pUpdate.setInt(6, childEsPreference.intValue());
			pUpdate.setLong (7, seVersionHi.longValue());
			pUpdate.setLong (8, creatorUId.longValue());
			pUpdate.setLong (9, createTs.longValue());
			pUpdate.setLong (10, changerUId.longValue());
			pUpdate.setLong (11, changeTs.longValue());
			pUpdate.setLong(12, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "HierarchyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : HierarchyInstance", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "parentId : " + parentId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "childId : " + childId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "shId : " + shId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nice : " + nice, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "childEsdId : " + childEsdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "childEsPreference : " + childEsPreference, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seVersionHi : " + seVersionHi, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "parentId          : " + parentId + "\n" +
		        indentString + "childId           : " + childId + "\n" +
		        indentString + "shId              : " + shId + "\n" +
		        indentString + "nice              : " + nice + "\n" +
		        indentString + "childEsdId        : " + childEsdId + "\n" +
		        indentString + "childEsPreference : " + childEsPreference + "\n" +
		        indentString + "seVersionHi       : " + seVersionHi + "\n" +
		        indentString + "creatorUId        : " + creatorUId + "\n" +
		        indentString + "createTs          : " + createTs + "\n" +
		        indentString + "changerUId        : " + changerUId + "\n" +
		        indentString + "changeTs          : " + changeTs + "\n" +
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
