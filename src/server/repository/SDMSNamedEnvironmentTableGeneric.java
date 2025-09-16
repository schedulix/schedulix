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

public class SDMSNamedEnvironmentTableGeneric extends SDMSTable
{

	public final static String tableName = "NAMED_ENVIRONMENT";
	public static SDMSNamedEnvironmentTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_name;

	public SDMSNamedEnvironmentTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "NamedEnvironment"));
		}
		table = (SDMSNamedEnvironmentTable) this;
		SDMSNamedEnvironmentTableGeneric.table = (SDMSNamedEnvironmentTable) this;
		isVersioned = true;
		idx_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "name");
	}
	public SDMSNamedEnvironment create(SystemEnvironment env
	                                   ,String p_name
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "NamedEnvironment"));
		}
		validate(env
		         , p_name
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSNamedEnvironmentGeneric o = new SDMSNamedEnvironmentGeneric(env
		                , p_name
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSNamedEnvironment p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSNamedEnvironment)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSNamedEnvironment)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSNamedEnvironment p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
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
		String name;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			creatorUId = new Long (r.getLong(3));
			createTs = new Long (r.getLong(4));
			changerUId = new Long (r.getLong(5));
			changeTs = new Long (r.getLong(6));
			validFrom = r.getLong(7);
			validTo = r.getLong(8);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "NamedEnvironment: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSNamedEnvironmentGeneric(id,
		                                       name,
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
		                                   ", " + squote + "NAME" + equote +
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
		ok =  idx_name.check(((SDMSNamedEnvironmentGeneric) o).name, o);
		out = out + "idx_name: " + (ok ? "ok" : "missing") + "\n";
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
		idx_name.put(env, ((SDMSNamedEnvironmentGeneric) o).name, o, ((1 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_name.remove(env, ((SDMSNamedEnvironmentGeneric) o).name, o);
	}

	public static SDMSNamedEnvironment getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSNamedEnvironment) table.get(env, id);
	}

	public static SDMSNamedEnvironment getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSNamedEnvironment) table.getForUpdate(env, id);
	}

	public static SDMSNamedEnvironment getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSNamedEnvironment) table.get(env, id, version);
	}

	public static SDMSNamedEnvironment idx_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSNamedEnvironment) SDMSNamedEnvironmentTableGeneric.idx_name.getUnique(env, key);
	}

	public static SDMSNamedEnvironment idx_name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSNamedEnvironment) SDMSNamedEnvironmentTableGeneric.idx_name.getUniqueForUpdate(env, key);
	}

	public static SDMSNamedEnvironment idx_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSNamedEnvironment) SDMSNamedEnvironmentTableGeneric.idx_name.getUnique(env, key, version);
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
