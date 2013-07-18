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

public class SDMSSchedulingHierarchyTableGeneric extends SDMSTable
{

	public final static String __version = "SDMSSchedulingHierarchyTableGeneric $Revision: 2.6 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "SCHEDULING_HIERARCHY";
	public static SDMSSchedulingHierarchyTable table  = null;

	public static SDMSIndex idx_seParentId;
	public static SDMSIndex idx_seChildId;
	public static SDMSIndex idx_estpId;
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
		idx_seParentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_seChildId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_estpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_parentId_childId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
		idx_parentId_aliasName = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
	}
	public SDMSSchedulingHierarchy create(SystemEnvironment env
	                                      ,Long p_seParentId
	                                      ,Long p_seChildId
	                                      ,String p_aliasName
	                                      ,Boolean p_isStatic
	                                      ,Integer p_priority
	                                      ,Integer p_suspend
	                                      ,String p_resumeAt
	                                      ,Integer p_resumeIn
	                                      ,Integer p_resumeBase
	                                      ,Integer p_mergeMode
	                                      ,Long p_estpId
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
		         , p_priority
		         , p_suspend
		         , p_resumeAt
		         , p_resumeIn
		         , p_resumeBase
		         , p_mergeMode
		         , p_estpId
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
		                , p_priority
		                , p_suspend
		                , p_resumeAt
		                , p_resumeIn
		                , p_resumeBase
		                , p_mergeMode
		                , p_estpId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                     );

		SDMSSchedulingHierarchy p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSchedulingHierarchy)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSchedulingHierarchy)(o.toProxy());
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
	                        ,Integer p_priority
	                        ,Integer p_suspend
	                        ,String p_resumeAt
	                        ,Integer p_resumeIn
	                        ,Integer p_resumeBase
	                        ,Integer p_mergeMode
	                        ,Long p_estpId
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSSchedulingHierarchyGeneric.checkIsStatic(p_isStatic))

			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "isStatic", p_isStatic));
		if (!SDMSSchedulingHierarchyGeneric.checkSuspend(p_suspend))

			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "suspend", p_suspend));
		if (!SDMSSchedulingHierarchyGeneric.checkResumeBase(p_resumeBase))

			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "resumeBase", p_resumeBase));
		if (!SDMSSchedulingHierarchyGeneric.checkMergeMode(p_mergeMode))

			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingHierarchy: $1 $2", "mergeMode", p_mergeMode));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long seParentId;
		Long seChildId;
		String aliasName;
		Boolean isStatic;
		Integer priority;
		Integer suspend;
		String resumeAt;
		Integer resumeIn;
		Integer resumeBase;
		Integer mergeMode;
		Long estpId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			seParentId = new Long (r.getLong(2));
			if (r.wasNull()) seParentId = null;
			seChildId = new Long (r.getLong(3));
			if (r.wasNull()) seChildId = null;
			aliasName = r.getString(4);
			if (r.wasNull()) aliasName = null;
			isStatic = new Boolean ((r.getInt(5) == 0 ? false : true));
			priority = new Integer (r.getInt(6));
			suspend = new Integer (r.getInt(7));
			resumeAt = r.getString(8);
			if (r.wasNull()) resumeAt = null;
			resumeIn = new Integer (r.getInt(9));
			if (r.wasNull()) resumeIn = null;
			resumeBase = new Integer (r.getInt(10));
			if (r.wasNull()) resumeBase = null;
			mergeMode = new Integer (r.getInt(11));
			estpId = new Long (r.getLong(12));
			if (r.wasNull()) estpId = null;
			creatorUId = new Long (r.getLong(13));
			createTs = new Long (r.getLong(14));
			changerUId = new Long (r.getLong(15));
			changeTs = new Long (r.getLong(16));
			validFrom = r.getLong(17);
			validTo = r.getLong(18);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "SchedulingHierarchy: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSSchedulingHierarchyGeneric(id,
		                seParentId,
		                seChildId,
		                aliasName,
		                isStatic,
		                priority,
		                suspend,
		                resumeAt,
		                resumeIn,
		                resumeBase,
		                mergeMode,
		                estpId,
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

		final String driverName = env.dbConnection.getMetaData().getDriverName();
		final boolean postgres = driverName.startsWith("PostgreSQL");
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
		Statement stmt = env.dbConnection.createStatement();

		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   "ID" +
		                                   ", " + squote + "SE_PARENT_ID" + equote +
		                                   ", " + squote + "SE_CHILD_ID" + equote +
		                                   ", " + squote + "ALIAS_NAME" + equote +
		                                   ", " + squote + "IS_STATIC" + equote +
		                                   ", " + squote + "PRIORITY" + equote +
		                                   ", " + squote + "SUSPEND" + equote +
		                                   ", " + squote + "RESUME_AT" + equote +
		                                   ", " + squote + "RESUME_IN" + equote +
		                                   ", " + squote + "RESUME_BASE" + equote +
		                                   ", " + squote + "MERGE_MODE" + equote +
		                                   ", " + squote + "ESTP_ID" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", VALID_FROM, VALID_TO " +
		                                   " FROM " + tableName() +
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

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_seParentId.put(env, ((SDMSSchedulingHierarchyGeneric) o).seParentId, o);
		idx_seChildId.put(env, ((SDMSSchedulingHierarchyGeneric) o).seChildId, o);
		idx_estpId.put(env, ((SDMSSchedulingHierarchyGeneric) o).estpId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).seChildId);
		idx_parentId_childId.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSchedulingHierarchyGeneric) o).seParentId);
		k.add(((SDMSSchedulingHierarchyGeneric) o).aliasName);
		idx_parentId_aliasName.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_seParentId.remove(env, ((SDMSSchedulingHierarchyGeneric) o).seParentId, o);
		idx_seChildId.remove(env, ((SDMSSchedulingHierarchyGeneric) o).seChildId, o);
		idx_estpId.remove(env, ((SDMSSchedulingHierarchyGeneric) o).estpId, o);
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

	public static SDMSSchedulingHierarchy idx_parentId_childId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSSchedulingHierarchy)  SDMSSchedulingHierarchyTableGeneric.idx_parentId_childId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
