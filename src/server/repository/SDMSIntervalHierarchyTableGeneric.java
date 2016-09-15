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

public class SDMSIntervalHierarchyTableGeneric extends SDMSTable
{

	public final static String tableName = "INTERVAL_HIERARCHY";
	public static SDMSIntervalHierarchyTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "CHILD_ID"
		, "PARENT_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_childId;
	public static SDMSIndex idx_parentId;

	public SDMSIntervalHierarchyTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "IntervalHierarchy"));
		}
		table = (SDMSIntervalHierarchyTable) this;
		SDMSIntervalHierarchyTableGeneric.table = (SDMSIntervalHierarchyTable) this;
		isVersioned = false;
		idx_childId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "childId");
		idx_parentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId");
	}
	public SDMSIntervalHierarchy create(SystemEnvironment env
	                                    ,Long p_childId
	                                    ,Long p_parentId
	                                   )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "IntervalHierarchy"));
		}
		validate(env
		         , p_childId
		         , p_parentId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSIntervalHierarchyGeneric o = new SDMSIntervalHierarchyGeneric(env
		                , p_childId
		                , p_parentId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                 );

		SDMSIntervalHierarchy p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSIntervalHierarchy)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSIntervalHierarchy)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSIntervalHierarchy p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_childId
	                        ,Long p_parentId
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
		Long childId;
		Long parentId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			childId = new Long (r.getLong(2));
			parentId = new Long (r.getLong(3));
			creatorUId = new Long (r.getLong(4));
			createTs = new Long (r.getLong(5));
			changerUId = new Long (r.getLong(6));
			changeTs = new Long (r.getLong(7));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "IntervalHierarchy: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSIntervalHierarchyGeneric(id,
		                                        childId,
		                                        parentId,
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
		                                   ", " + squote + "CHILD_ID" + equote +
		                                   ", " + squote + "PARENT_ID" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + tableName() +
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
		ok =  idx_childId.check(((SDMSIntervalHierarchyGeneric) o).childId, o);
		out = out + "idx_childId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_parentId.check(((SDMSIntervalHierarchyGeneric) o).parentId, o);
		out = out + "idx_parentId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_childId.put(env, ((SDMSIntervalHierarchyGeneric) o).childId, o, ((1 & indexMember) != 0));
		idx_parentId.put(env, ((SDMSIntervalHierarchyGeneric) o).parentId, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_childId.remove(env, ((SDMSIntervalHierarchyGeneric) o).childId, o);
		idx_parentId.remove(env, ((SDMSIntervalHierarchyGeneric) o).parentId, o);
	}

	public static SDMSIntervalHierarchy getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSIntervalHierarchy) table.get(env, id);
	}

	public static SDMSIntervalHierarchy getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSIntervalHierarchy) table.getForUpdate(env, id);
	}

	public static SDMSIntervalHierarchy getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSIntervalHierarchy) table.get(env, id, version);
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
