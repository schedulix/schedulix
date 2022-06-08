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

public class SDMSSchedulingHierarchyTableGeneric extends SDMSTable
{

	public final static String tableName = "SCHEDULING_HIERARCHY";
	public static SDMSSchedulingHierarchyTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SE_PARENT_ID"
		, "SE_CHILD_ID"
		, "ALIAS_NAME"
		, "IS_STATIC"
		, "IS_DISABLED"
		, "PRIORITY"
		, "SUSPEND"
		, "RESUME_AT"
		, "RESUME_IN"
		, "RESUME_BASE"
		, "MERGE_MODE"
		, "ESTP_ID"
		, "INT_ID"
		, "ENABLE_CONDITION"
		, "ENABLE_MODE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_seParentId;
	public static SDMSIndex idx_seChildId;
	public static SDMSIndex idx_estpId;
	public static SDMSIndex idx_intId;
	public static SDMSIndex idx_parentId_childId;
	public static SDMSIndex idx_parentId_aliasName;

	public SDMSSchedulingHierarchyTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "SchedulingHierarchy"));
		}
		table = (SDMSSchedulingHierarchyTable) this;
		SDMSSchedulingHierarchyTableGeneric.table = (SDMSSchedulingHierarchyTable) this;
		isVersioned = true;
		idx_seParentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seParentId");
		idx_seChildId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seChildId");
		idx_estpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "estpId");
		idx_intId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "intId");
		idx_parentId_childId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "parentId_childId");
		idx_parentId_aliasName = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId_aliasName");
	}
	public SDMSSchedulingHierarchy create(SystemEnvironment env
	                                      ,Long p_seParentId
	                                      ,Long p_seChildId
	                                      ,String p_aliasName
	                                      ,Boolean p_isStatic
	                                      ,Boolean p_isDisabled
	                                      ,Integer p_priority
	                                      ,Integer p_suspend
	                                      ,String p_resumeAt
	                                      ,Integer p_resumeIn
	                                      ,Integer p_resumeBase
	                                      ,Integer p_mergeMode
	                                      ,Long p_estpId
	                                      ,Long p_intId
	                                      ,String p_enableCondition
	                                      ,Integer p_enableMode
	                                     )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "SchedulingHierarchy"));
		}
		validate(env
		         , p_seParentId
		         , p_seChildId
		         , p_aliasName
		         , p_isStatic
		         , p_isDisabled
		         , p_priority
		         , p_suspend
		         , p_resumeAt
		         , p_resumeIn
		         , p_resumeBase
		         , p_mergeMode
		         , p_estpId
		         , p_intId
		         , p_enableCondition
		         , p_enableMode
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSSchedulingHierarchyGeneric o = new SDMSSchedulingHierarchyGeneric(env
		                , p_seParentId
		                , p_seChildId
		                , p_aliasName
		                , p_isStatic
		                , p_isDisabled
		                , p_priority
		                , p_suspend
		                , p_resumeAt
		                , p_resumeIn
		                , p_resumeBase
		                , p_mergeMode
		                , p_estpId
		                , p_intId
		                , p_enableCondition
		                , p_enableMode
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                     );

		SDMSSchedulingHierarchy p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSchedulingHierarchy)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSchedulingHierarchy)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSSchedulingHierarchy p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_seParentId
	                        ,Long p_seChildId
	                        ,String p_aliasName
	                        ,Boolean p_isStatic
	                        ,Boolean p_isDisabled
	                        ,Integer p_priority
	                        ,Integer p_suspend
	                        ,String p_resumeAt
	                        ,Integer p_resumeIn
	                        ,Integer p_resumeBase
	                        ,Integer p_mergeMode
	                        ,Long p_estpId
	                        ,Long p_intId
	                        ,String p_enableCondition
	                        ,Integer p_enableMode
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSSchedulingHierarchyGeneric.checkIsStatic(p_isStatic))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "isStatic", p_isStatic));
		if (!SDMSSchedulingHierarchyGeneric.checkIsDisabled(p_isDisabled))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "isDisabled", p_isDisabled));
		if (!SDMSSchedulingHierarchyGeneric.checkSuspend(p_suspend))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "suspend", p_suspend));
		if (!SDMSSchedulingHierarchyGeneric.checkResumeBase(p_resumeBase))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "resumeBase", p_resumeBase));
		if (!SDMSSchedulingHierarchyGeneric.checkMergeMode(p_mergeMode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "mergeMode", p_mergeMode));
		if (!SDMSSchedulingHierarchyGeneric.checkEnableMode(p_enableMode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "enableMode", p_enableMode));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long seParentId;
		Long seChildId;
		String aliasName;
		Boolean isStatic;
		Boolean isDisabled;
		Integer priority;
		Integer suspend;
		String resumeAt;
		Integer resumeIn;
		Integer resumeBase;
		Integer mergeMode;
		Long estpId;
		Long intId;
		String enableCondition;
		Integer enableMode;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			seParentId = Long.valueOf (r.getLong(2));
			if (r.wasNull()) seParentId = null;
			seChildId = Long.valueOf (r.getLong(3));
			if (r.wasNull()) seChildId = null;
			aliasName = r.getString(4);
			if (r.wasNull()) aliasName = null;
			isStatic = Boolean.valueOf ((r.getInt(5) == 0 ? false : true));
			isDisabled = Boolean.valueOf ((r.getInt(6) == 0 ? false : true));
			priority = Integer.valueOf (r.getInt(7));
			suspend = Integer.valueOf (r.getInt(8));
			resumeAt = r.getString(9);
			if (r.wasNull()) resumeAt = null;
			resumeIn = Integer.valueOf (r.getInt(10));
			if (r.wasNull()) resumeIn = null;
			resumeBase = Integer.valueOf (r.getInt(11));
			if (r.wasNull()) resumeBase = null;
			mergeMode = Integer.valueOf (r.getInt(12));
			estpId = Long.valueOf (r.getLong(13));
			if (r.wasNull()) estpId = null;
			intId = Long.valueOf (r.getLong(14));
			if (r.wasNull()) intId = null;
			enableCondition = r.getString(15);
			if (r.wasNull()) enableCondition = null;
			enableMode = Integer.valueOf (r.getInt(16));
			creatorUId = Long.valueOf (r.getLong(17));
			createTs = Long.valueOf (r.getLong(18));
			changerUId = Long.valueOf (r.getLong(19));
			changeTs = Long.valueOf (r.getLong(20));
			validFrom = r.getLong(21);
			validTo = r.getLong(22);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "SchedulingHierarchy: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSSchedulingHierarchyGeneric(id,
		                seParentId,
		                seChildId,
		                aliasName,
		                isStatic,
		                isDisabled,
		                priority,
		                suspend,
		                resumeAt,
		                resumeIn,
		                resumeBase,
		                mergeMode,
		                estpId,
		                intId,
		                enableCondition,
		                enableMode,
		                creatorUId,
		                createTs,
		                changerUId,
		                changeTs,
		                validFrom, validTo);
	}

	protected void loadTable(SystemEnvironment env)
	throws SQLException, SDMSException
	{
		int read = 0;
		int loaded = 0;

		final boolean postgres = SystemEnvironment.isPostgreSQL;
		String squote = SystemEnvironment.SQUOTE;
		String equote = SystemEnvironment.EQUOTE;
		Statement stmt = env.dbConnection.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   squote + tableName() + equote + ".ID" +
		                                   ", " + squote + "SE_PARENT_ID" + equote +
		                                   ", " + squote + "SE_CHILD_ID" + equote +
		                                   ", " + squote + "ALIAS_NAME" + equote +
		                                   ", " + squote + "IS_STATIC" + equote +
		                                   ", " + squote + "IS_DISABLED" + equote +
		                                   ", " + squote + "PRIORITY" + equote +
		                                   ", " + squote + "SUSPEND" + equote +
		                                   ", " + squote + "RESUME_AT" + equote +
		                                   ", " + squote + "RESUME_IN" + equote +
		                                   ", " + squote + "RESUME_BASE" + equote +
		                                   ", " + squote + "MERGE_MODE" + equote +
		                                   ", " + squote + "ESTP_ID" + equote +
		                                   ", " + squote + "INT_ID" + equote +
		                                   ", " + squote + "ENABLE_CONDITION" + equote +
		                                   ", " + squote + "ENABLE_MODE" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", VALID_FROM, VALID_TO " +
		                                   " FROM " + squote + tableName() + equote +
		                                   " WHERE VALID_TO >= " + (postgres ?
		                                                   "CAST (\'" + env.lowestActiveVersion + "\' AS DECIMAL)" :
		                                                   "" + env.lowestActiveVersion) +
		                                   ""						  );
		while(rset.next()) {
			if(loadObject(env, rset)) ++loaded;
			++read;
		}
		stmt.close();
		SDMSThread.doTrace(null, "Read " + read + ", Loaded " + loaded + " rows for " + tableName(), SDMSThread.SEVERITY_INFO);
	}

	public String checkIndex(SDMSObject o)
	throws SDMSException
	{
		String out = "";
		boolean ok;
		ok =  idx_seParentId.check(((SDMSSchedulingHierarchyGeneric) o).seParentId, o);
		out = out + "idx_seParentId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_seChildId.check(((SDMSSchedulingHierarchyGeneric) o).seChildId, o);
		out = out + "idx_seChildId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_estpId.check(((SDMSSchedulingHierarchyGeneric) o).estpId, o);
		out = out + "idx_estpId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_intId.check(((SDMSSchedulingHierarchyGeneric) o).intId, o);
		out = out + "idx_intId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).seChildId);
		ok =  idx_parentId_childId.check(k, o);
		out = out + "idx_parentId_childId: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).aliasName);
		ok =  idx_parentId_aliasName.check(k, o);
		out = out + "idx_parentId_aliasName: " + (ok ? "ok" : "missing") + "\n";
		return out;
	}

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		index(env, o, -1);
	}

	protected void index(SystemEnvironment env, SDMSObject o, long indexMember)
	throws SDMSException
	{
		idx_seParentId.put(env, ((SDMSSchedulingHierarchyGeneric) o).seParentId, o, ((1 & indexMember) != 0));
		idx_seChildId.put(env, ((SDMSSchedulingHierarchyGeneric) o).seChildId, o, ((2 & indexMember) != 0));
		idx_estpId.put(env, ((SDMSSchedulingHierarchyGeneric) o).estpId, o, ((4 & indexMember) != 0));
		idx_intId.put(env, ((SDMSSchedulingHierarchyGeneric) o).intId, o, ((8 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).seChildId);
		idx_parentId_childId.put(env, k, o, ((16 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).aliasName);
		idx_parentId_aliasName.put(env, k, o, ((32 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_seParentId.remove(env, ((SDMSSchedulingHierarchyGeneric) o).seParentId, o);
		idx_seChildId.remove(env, ((SDMSSchedulingHierarchyGeneric) o).seChildId, o);
		idx_estpId.remove(env, ((SDMSSchedulingHierarchyGeneric) o).estpId, o);
		idx_intId.remove(env, ((SDMSSchedulingHierarchyGeneric) o).intId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).seChildId);
		idx_parentId_childId.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).aliasName);
		idx_parentId_aliasName.remove(env, k, o);
	}

	public static SDMSSchedulingHierarchy getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSchedulingHierarchy) table.get(env, id);
	}

	public static SDMSSchedulingHierarchy getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSchedulingHierarchy) table.getForUpdate(env, id);
	}

	public static SDMSSchedulingHierarchy getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSSchedulingHierarchy) table.get(env, id, version);
	}

	public static SDMSSchedulingHierarchy idx_parentId_childId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSchedulingHierarchy)  SDMSSchedulingHierarchyTableGeneric.idx_parentId_childId.getUnique(env, key);
	}

	public static SDMSSchedulingHierarchy idx_parentId_childId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSchedulingHierarchy)  SDMSSchedulingHierarchyTableGeneric.idx_parentId_childId.getUniqueForUpdate(env, key);
	}

	public static SDMSSchedulingHierarchy idx_parentId_childId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSSchedulingHierarchy)  SDMSSchedulingHierarchyTableGeneric.idx_parentId_childId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
	public String[] columnNames()
	{
		return columnNames;
	}
}
