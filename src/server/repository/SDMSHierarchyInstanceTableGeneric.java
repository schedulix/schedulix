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

public class SDMSHierarchyInstanceTableGeneric extends SDMSTable
{

	public final static String tableName = "HIERARCHY_INSTANCE";
	public static SDMSHierarchyInstanceTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "PARENT_ID"
		, "CHILD_ID"
		, "SH_ID"
		, "NICE"
		, "CHILD_ESD_ID"
		, "CHILD_ES_PREFERENCE"
		, "SE_VERSION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_parentId;
	public static SDMSIndex idx_childId;
	public static SDMSIndex idx_parentId_childId;

	public SDMSHierarchyInstanceTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "HierarchyInstance"));
		}
		table = (SDMSHierarchyInstanceTable) this;
		SDMSHierarchyInstanceTableGeneric.table = (SDMSHierarchyInstanceTable) this;
		isVersioned = false;
		idx_parentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId");
		idx_childId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "childId");
		idx_parentId_childId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "parentId_childId");
	}
	public SDMSHierarchyInstance create(SystemEnvironment env
	                                    ,Long p_parentId
	                                    ,Long p_childId
	                                    ,Long p_shId
	                                    ,Integer p_nice
	                                    ,Long p_childEsdId
	                                    ,Integer p_childEsPreference
	                                    ,Long p_seVersionHi
	                                   )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "HierarchyInstance"));
		}
		validate(env
		         , p_parentId
		         , p_childId
		         , p_shId
		         , p_nice
		         , p_childEsdId
		         , p_childEsPreference
		         , p_seVersionHi
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSHierarchyInstanceGeneric o = new SDMSHierarchyInstanceGeneric(env
		                , p_parentId
		                , p_childId
		                , p_shId
		                , p_nice
		                , p_childEsdId
		                , p_childEsPreference
		                , p_seVersionHi
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                 );

		SDMSHierarchyInstance p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSHierarchyInstance)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSHierarchyInstance)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSHierarchyInstance p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_parentId
	                        ,Long p_childId
	                        ,Long p_shId
	                        ,Integer p_nice
	                        ,Long p_childEsdId
	                        ,Integer p_childEsPreference
	                        ,Long p_seVersionHi
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long parentId;
		Long childId;
		Long shId;
		Integer nice;
		Long childEsdId;
		Integer childEsPreference;
		Long seVersionHi;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			parentId = new Long (r.getLong(2));
			childId = new Long (r.getLong(3));
			shId = new Long (r.getLong(4));
			nice = new Integer (r.getInt(5));
			childEsdId = new Long (r.getLong(6));
			if (r.wasNull()) childEsdId = null;
			childEsPreference = new Integer (r.getInt(7));
			if (r.wasNull()) childEsPreference = null;
			seVersionHi = new Long (r.getLong(8));
			creatorUId = new Long (r.getLong(9));
			createTs = new Long (r.getLong(10));
			changerUId = new Long (r.getLong(11));
			changeTs = new Long (r.getLong(12));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "HierarchyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSHierarchyInstanceGeneric(id,
		                                        parentId,
		                                        childId,
		                                        shId,
		                                        nice,
		                                        childEsdId,
		                                        childEsPreference,
		                                        seVersionHi,
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
		                                   tableName() + ".ID" +
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
		                                   "  FROM " + tableName() + ", " +
		                                   "       SME2LOAD " +
		                                   " WHERE " + tableName() + ".PARENT_ID = SME2LOAD.ID"
		                                  );
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
		ok =  idx_parentId.check(((SDMSHierarchyInstanceGeneric) o).parentId, o);
		out = out + "idx_parentId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_childId.check(((SDMSHierarchyInstanceGeneric) o).childId, o);
		out = out + "idx_childId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSHierarchyInstanceGeneric) o).parentId);
		k.add(((SDMSHierarchyInstanceGeneric) o).childId);
		ok =  idx_parentId_childId.check(k, o);
		out = out + "idx_parentId_childId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_parentId.put(env, ((SDMSHierarchyInstanceGeneric) o).parentId, o, ((1 & indexMember) != 0));
		idx_childId.put(env, ((SDMSHierarchyInstanceGeneric) o).childId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSHierarchyInstanceGeneric) o).parentId);
		k.add(((SDMSHierarchyInstanceGeneric) o).childId);
		idx_parentId_childId.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_parentId.remove(env, ((SDMSHierarchyInstanceGeneric) o).parentId, o);
		idx_childId.remove(env, ((SDMSHierarchyInstanceGeneric) o).childId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSHierarchyInstanceGeneric) o).parentId);
		k.add(((SDMSHierarchyInstanceGeneric) o).childId);
		idx_parentId_childId.remove(env, k, o);
	}

	public static SDMSHierarchyInstance getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSHierarchyInstance) table.get(env, id);
	}

	public static SDMSHierarchyInstance getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSHierarchyInstance) table.get(env, id, version);
	}

	public static SDMSHierarchyInstance idx_parentId_childId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSHierarchyInstance)  SDMSHierarchyInstanceTableGeneric.idx_parentId_childId.getUnique(env, key);
	}

	public static SDMSHierarchyInstance idx_parentId_childId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSHierarchyInstance)  SDMSHierarchyInstanceTableGeneric.idx_parentId_childId.getUnique(env, key, version);
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
