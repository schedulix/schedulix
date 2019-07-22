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

public class SDMSDependencyStateTableGeneric extends SDMSTable
{

	public final static String tableName = "DEPENDENCY_STATE";
	public static SDMSDependencyStateTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "DD_ID"
		, "ESD_ID"
		, "CONDITION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_ddId;
	public static SDMSIndex idx_ddId_esdId;

	public SDMSDependencyStateTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "DependencyState"));
		}
		table = (SDMSDependencyStateTable) this;
		SDMSDependencyStateTableGeneric.table = (SDMSDependencyStateTable) this;
		isVersioned = true;
		idx_ddId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ddId");
		idx_ddId_esdId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "ddId_esdId");
	}
	public SDMSDependencyState create(SystemEnvironment env
	                                  ,Long p_ddId
	                                  ,Long p_esdId
	                                  ,String p_condition
	                                 )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "DependencyState"));
		}
		validate(env
		         , p_ddId
		         , p_esdId
		         , p_condition
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSDependencyStateGeneric o = new SDMSDependencyStateGeneric(env
		                , p_ddId
		                , p_esdId
		                , p_condition
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                             );

		SDMSDependencyState p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSDependencyState)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSDependencyState)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSDependencyState p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_ddId
	                        ,Long p_esdId
	                        ,String p_condition
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
		Long ddId;
		Long esdId;
		String condition;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			ddId = new Long (r.getLong(2));
			esdId = new Long (r.getLong(3));
			condition = r.getString(4);
			if (r.wasNull()) condition = null;
			creatorUId = new Long (r.getLong(5));
			createTs = new Long (r.getLong(6));
			changerUId = new Long (r.getLong(7));
			changeTs = new Long (r.getLong(8));
			validFrom = r.getLong(9);
			validTo = r.getLong(10);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "DependencyState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSDependencyStateGeneric(id,
		                                      ddId,
		                                      esdId,
		                                      condition,
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
		                                   ", " + squote + "DD_ID" + equote +
		                                   ", " + squote + "ESD_ID" + equote +
		                                   ", " + squote + "CONDITION" + equote +
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
		ok =  idx_ddId.check(((SDMSDependencyStateGeneric) o).ddId, o);
		out = out + "idx_ddId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyStateGeneric) o).ddId);
		k.add(((SDMSDependencyStateGeneric) o).esdId);
		ok =  idx_ddId_esdId.check(k, o);
		out = out + "idx_ddId_esdId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_ddId.put(env, ((SDMSDependencyStateGeneric) o).ddId, o, ((1 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyStateGeneric) o).ddId);
		k.add(((SDMSDependencyStateGeneric) o).esdId);
		idx_ddId_esdId.put(env, k, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_ddId.remove(env, ((SDMSDependencyStateGeneric) o).ddId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyStateGeneric) o).ddId);
		k.add(((SDMSDependencyStateGeneric) o).esdId);
		idx_ddId_esdId.remove(env, k, o);
	}

	public static SDMSDependencyState getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSDependencyState) table.get(env, id);
	}

	public static SDMSDependencyState getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSDependencyState) table.getForUpdate(env, id);
	}

	public static SDMSDependencyState getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSDependencyState) table.get(env, id, version);
	}

	public static SDMSDependencyState idx_ddId_esdId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSDependencyState)  SDMSDependencyStateTableGeneric.idx_ddId_esdId.getUnique(env, key);
	}

	public static SDMSDependencyState idx_ddId_esdId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSDependencyState)  SDMSDependencyStateTableGeneric.idx_ddId_esdId.getUniqueForUpdate(env, key);
	}

	public static SDMSDependencyState idx_ddId_esdId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSDependencyState)  SDMSDependencyStateTableGeneric.idx_ddId_esdId.getUnique(env, key, version);
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
