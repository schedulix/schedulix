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

public class SDMSEnvironmentTableGeneric extends SDMSTable
{

	public final static String tableName = "ENVIRONMENT";
	public static SDMSEnvironmentTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NE_ID"
		, "NR_ID"
		, "CONDITION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_neId;
	public static SDMSIndex idx_nrId;
	public static SDMSIndex idx_neId_nrId;

	public SDMSEnvironmentTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "Environment"));
		}
		table = (SDMSEnvironmentTable) this;
		SDMSEnvironmentTableGeneric.table = (SDMSEnvironmentTable) this;
		isVersioned = true;
		idx_neId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "neId");
		idx_nrId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "nrId");
		idx_neId_nrId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "neId_nrId");
	}
	public SDMSEnvironment create(SystemEnvironment env
	                              ,Long p_neId
	                              ,Long p_nrId
	                              ,String p_condition
	                             )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "Environment"));
		}
		validate(env
		         , p_neId
		         , p_nrId
		         , p_condition
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSEnvironmentGeneric o = new SDMSEnvironmentGeneric(env
		                , p_neId
		                , p_nrId
		                , p_condition
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                     );

		SDMSEnvironment p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSEnvironment)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSEnvironment)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSEnvironment p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_neId
	                        ,Long p_nrId
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
		Long neId;
		Long nrId;
		String condition;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			neId = new Long (r.getLong(2));
			nrId = new Long (r.getLong(3));
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
			throw new FatalException(new SDMSMessage(env, "01110182045", "Environment: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSEnvironmentGeneric(id,
		                                  neId,
		                                  nrId,
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
		                                   ", " + squote + "NE_ID" + equote +
		                                   ", " + squote + "NR_ID" + equote +
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
		ok =  idx_neId.check(((SDMSEnvironmentGeneric) o).neId, o);
		out = out + "idx_neId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_nrId.check(((SDMSEnvironmentGeneric) o).nrId, o);
		out = out + "idx_nrId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSEnvironmentGeneric) o).neId);
		k.add(((SDMSEnvironmentGeneric) o).nrId);
		ok =  idx_neId_nrId.check(k, o);
		out = out + "idx_neId_nrId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_neId.put(env, ((SDMSEnvironmentGeneric) o).neId, o, ((1 & indexMember) != 0));
		idx_nrId.put(env, ((SDMSEnvironmentGeneric) o).nrId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSEnvironmentGeneric) o).neId);
		k.add(((SDMSEnvironmentGeneric) o).nrId);
		idx_neId_nrId.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_neId.remove(env, ((SDMSEnvironmentGeneric) o).neId, o);
		idx_nrId.remove(env, ((SDMSEnvironmentGeneric) o).nrId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSEnvironmentGeneric) o).neId);
		k.add(((SDMSEnvironmentGeneric) o).nrId);
		idx_neId_nrId.remove(env, k, o);
	}

	public static SDMSEnvironment getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSEnvironment) table.get(env, id);
	}

	public static SDMSEnvironment getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSEnvironment) table.getForUpdate(env, id);
	}

	public static SDMSEnvironment getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSEnvironment) table.get(env, id, version);
	}

	public static SDMSEnvironment idx_neId_nrId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSEnvironment)  SDMSEnvironmentTableGeneric.idx_neId_nrId.getUnique(env, key);
	}

	public static SDMSEnvironment idx_neId_nrId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSEnvironment)  SDMSEnvironmentTableGeneric.idx_neId_nrId.getUniqueForUpdate(env, key);
	}

	public static SDMSEnvironment idx_neId_nrId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSEnvironment)  SDMSEnvironmentTableGeneric.idx_neId_nrId.getUnique(env, key, version);
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
