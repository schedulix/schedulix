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

public class SDMSIgnoredDependencyTableGeneric extends SDMSTable
{

	public final static String tableName = "IGNORED_DEPENDENCY";
	public static SDMSIgnoredDependencyTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SH_ID"
		, "DD_NAME"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_shId;
	public static SDMSIndex idx_shId_ddName;

	public SDMSIgnoredDependencyTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "IgnoredDependency"));
		}
		table = (SDMSIgnoredDependencyTable) this;
		SDMSIgnoredDependencyTableGeneric.table = (SDMSIgnoredDependencyTable) this;
		isVersioned = true;
		idx_shId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "shId");
		idx_shId_ddName = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "shId_ddName");
	}
	public SDMSIgnoredDependency create(SystemEnvironment env
	                                    ,Long p_shId
	                                    ,String p_ddName
	                                   )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "IgnoredDependency"));
		}
		validate(env
		         , p_shId
		         , p_ddName
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSIgnoredDependencyGeneric o = new SDMSIgnoredDependencyGeneric(env
		                , p_shId
		                , p_ddName
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                 );

		SDMSIgnoredDependency p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSIgnoredDependency)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSIgnoredDependency)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSIgnoredDependency p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_shId
	                        ,String p_ddName
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
		Long shId;
		String ddName;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			shId = Long.valueOf (r.getLong(2));
			ddName = r.getString(3);
			creatorUId = Long.valueOf (r.getLong(4));
			createTs = Long.valueOf (r.getLong(5));
			changerUId = Long.valueOf (r.getLong(6));
			changeTs = Long.valueOf (r.getLong(7));
			validFrom = r.getLong(8);
			validTo = r.getLong(9);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "IgnoredDependency: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSIgnoredDependencyGeneric(id,
		                                        shId,
		                                        ddName,
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
		                                   ", " + squote + "SH_ID" + equote +
		                                   ", " + squote + "DD_NAME" + equote +
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
			try {
				if(loadObject(env, rset)) ++loaded;
				++read;
			} catch (Exception e) {
				SDMSThread.doTrace(null, "Exception caught while loading table " + tableName() + ", ID = " + Long.valueOf (rset.getLong(1)), SDMSThread.SEVERITY_ERROR);
				throw(e);
			}
		}
		stmt.close();
		SDMSThread.doTrace(null, "Read " + read + ", Loaded " + loaded + " rows for " + tableName(), SDMSThread.SEVERITY_INFO);
	}

	public String checkIndex(SDMSObject o)
	throws SDMSException
	{
		String out = "";
		boolean ok;
		ok =  idx_shId.check(((SDMSIgnoredDependencyGeneric) o).shId, o);
		out = out + "idx_shId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIgnoredDependencyGeneric) o).shId);
		k.add(((SDMSIgnoredDependencyGeneric) o).ddName);
		ok =  idx_shId_ddName.check(k, o);
		out = out + "idx_shId_ddName: " + (ok ? "ok" : "missing") + "\n";
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
		idx_shId.put(env, ((SDMSIgnoredDependencyGeneric) o).shId, o, ((1 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIgnoredDependencyGeneric) o).shId);
		k.add(((SDMSIgnoredDependencyGeneric) o).ddName);
		idx_shId_ddName.put(env, k, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_shId.remove(env, ((SDMSIgnoredDependencyGeneric) o).shId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIgnoredDependencyGeneric) o).shId);
		k.add(((SDMSIgnoredDependencyGeneric) o).ddName);
		idx_shId_ddName.remove(env, k, o);
	}

	public static SDMSIgnoredDependency getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSIgnoredDependency) table.get(env, id);
	}

	public static SDMSIgnoredDependency getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSIgnoredDependency) table.getForUpdate(env, id);
	}

	public static SDMSIgnoredDependency getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSIgnoredDependency) table.get(env, id, version);
	}

	public static SDMSIgnoredDependency idx_shId_ddName_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSIgnoredDependency)  SDMSIgnoredDependencyTableGeneric.idx_shId_ddName.getUnique(env, key);
	}

	public static SDMSIgnoredDependency idx_shId_ddName_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSIgnoredDependency)  SDMSIgnoredDependencyTableGeneric.idx_shId_ddName.getUniqueForUpdate(env, key);
	}

	public static SDMSIgnoredDependency idx_shId_ddName_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSIgnoredDependency)  SDMSIgnoredDependencyTableGeneric.idx_shId_ddName.getUnique(env, key, version);
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
