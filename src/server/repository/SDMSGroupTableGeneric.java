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

public class SDMSGroupTableGeneric extends SDMSTable
{

	public final static String tableName = "GROUPS";
	public static SDMSGroupTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "DELETE_VERSION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_name;
	public static SDMSIndex idx_name_deleteVersion;

	public SDMSGroupTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "Group"));
		}
		table = (SDMSGroupTable) this;
		SDMSGroupTableGeneric.table = (SDMSGroupTable) this;
		isVersioned = false;
		idx_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "name");
		idx_name_deleteVersion = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "name_deleteVersion");
	}
	public SDMSGroup create(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_deleteVersion
	                       )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "Group"));
		}
		validate(env
		         , p_name
		         , p_deleteVersion
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSGroupGeneric o = new SDMSGroupGeneric(env
		                , p_name
		                , p_deleteVersion
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                         );

		SDMSGroup p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSGroup)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSGroup)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSGroup p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_deleteVersion
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
		Long deleteVersion;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			deleteVersion = new Long (r.getLong(3));
			creatorUId = new Long (r.getLong(4));
			createTs = new Long (r.getLong(5));
			changerUId = new Long (r.getLong(6));
			changeTs = new Long (r.getLong(7));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "Group: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSGroupGeneric(id,
		                            name,
		                            deleteVersion,
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
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "DELETE_VERSION" + equote +
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
		ok =  idx_name.check(((SDMSGroupGeneric) o).name, o);
		out = out + "idx_name: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGroupGeneric) o).name);
		k.add(((SDMSGroupGeneric) o).deleteVersion);
		ok =  idx_name_deleteVersion.check(k, o);
		out = out + "idx_name_deleteVersion: " + (ok ? "ok" : "missing") + "\n";
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
		idx_name.put(env, ((SDMSGroupGeneric) o).name, o, ((1 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGroupGeneric) o).name);
		k.add(((SDMSGroupGeneric) o).deleteVersion);
		idx_name_deleteVersion.put(env, k, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_name.remove(env, ((SDMSGroupGeneric) o).name, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGroupGeneric) o).name);
		k.add(((SDMSGroupGeneric) o).deleteVersion);
		idx_name_deleteVersion.remove(env, k, o);
	}

	public static SDMSGroup getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSGroup) table.get(env, id);
	}

	public static SDMSGroup getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSGroup) table.getForUpdate(env, id);
	}

	public static SDMSGroup getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSGroup) table.get(env, id, version);
	}

	public static SDMSGroup idx_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSGroup) SDMSGroupTableGeneric.idx_name.getUnique(env, key);
	}

	public static SDMSGroup idx_name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSGroup) SDMSGroupTableGeneric.idx_name.getUniqueForUpdate(env, key);
	}

	public static SDMSGroup idx_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSGroup) SDMSGroupTableGeneric.idx_name.getUnique(env, key, version);
	}

	public static SDMSGroup idx_name_deleteVersion_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSGroup)  SDMSGroupTableGeneric.idx_name_deleteVersion.getUnique(env, key);
	}

	public static SDMSGroup idx_name_deleteVersion_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSGroup)  SDMSGroupTableGeneric.idx_name_deleteVersion.getUniqueForUpdate(env, key);
	}

	public static SDMSGroup idx_name_deleteVersion_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSGroup)  SDMSGroupTableGeneric.idx_name_deleteVersion.getUnique(env, key, version);
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
