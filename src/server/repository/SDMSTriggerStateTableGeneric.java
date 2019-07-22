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

public class SDMSTriggerStateTableGeneric extends SDMSTable
{

	public final static String tableName = "TRIGGER_STATE";
	public static SDMSTriggerStateTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "TRIGGER_ID"
		, "FROM_STATE_ID"
		, "TO_STATE_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_triggerId;
	public static SDMSIndex idx_fromStateId;
	public static SDMSIndex idx_toStateId;

	public SDMSTriggerStateTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "TriggerState"));
		}
		table = (SDMSTriggerStateTable) this;
		SDMSTriggerStateTableGeneric.table = (SDMSTriggerStateTable) this;
		isVersioned = true;
		idx_triggerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "triggerId");
		idx_fromStateId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "fromStateId");
		idx_toStateId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "toStateId");
	}
	public SDMSTriggerState create(SystemEnvironment env
	                               ,Long p_triggerId
	                               ,Long p_fromStateId
	                               ,Long p_toStateId
	                              )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "TriggerState"));
		}
		validate(env
		         , p_triggerId
		         , p_fromStateId
		         , p_toStateId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSTriggerStateGeneric o = new SDMSTriggerStateGeneric(env
		                , p_triggerId
		                , p_fromStateId
		                , p_toStateId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                       );

		SDMSTriggerState p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSTriggerState)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSTriggerState)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSTriggerState p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_triggerId
	                        ,Long p_fromStateId
	                        ,Long p_toStateId
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
		Long triggerId;
		Long fromStateId;
		Long toStateId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			triggerId = new Long (r.getLong(2));
			fromStateId = new Long (r.getLong(3));
			if (r.wasNull()) fromStateId = null;
			toStateId = new Long (r.getLong(4));
			if (r.wasNull()) toStateId = null;
			creatorUId = new Long (r.getLong(5));
			createTs = new Long (r.getLong(6));
			changerUId = new Long (r.getLong(7));
			changeTs = new Long (r.getLong(8));
			validFrom = r.getLong(9);
			validTo = r.getLong(10);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "TriggerState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSTriggerStateGeneric(id,
		                                   triggerId,
		                                   fromStateId,
		                                   toStateId,
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
		                                   ", " + squote + "TRIGGER_ID" + equote +
		                                   ", " + squote + "FROM_STATE_ID" + equote +
		                                   ", " + squote + "TO_STATE_ID" + equote +
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
		ok =  idx_triggerId.check(((SDMSTriggerStateGeneric) o).triggerId, o);
		out = out + "idx_triggerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_fromStateId.check(((SDMSTriggerStateGeneric) o).fromStateId, o);
		out = out + "idx_fromStateId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_toStateId.check(((SDMSTriggerStateGeneric) o).toStateId, o);
		out = out + "idx_toStateId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
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
		idx_triggerId.put(env, ((SDMSTriggerStateGeneric) o).triggerId, o, ((1 & indexMember) != 0));
		idx_fromStateId.put(env, ((SDMSTriggerStateGeneric) o).fromStateId, o, ((2 & indexMember) != 0));
		idx_toStateId.put(env, ((SDMSTriggerStateGeneric) o).toStateId, o, ((4 & indexMember) != 0));
		SDMSKey k;
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_triggerId.remove(env, ((SDMSTriggerStateGeneric) o).triggerId, o);
		idx_fromStateId.remove(env, ((SDMSTriggerStateGeneric) o).fromStateId, o);
		idx_toStateId.remove(env, ((SDMSTriggerStateGeneric) o).toStateId, o);
		SDMSKey k;
	}

	public static SDMSTriggerState getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTriggerState) table.get(env, id);
	}

	public static SDMSTriggerState getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTriggerState) table.getForUpdate(env, id);
	}

	public static SDMSTriggerState getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSTriggerState) table.get(env, id, version);
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
