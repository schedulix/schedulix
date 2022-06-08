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

public class SDMSRunnableQueueTableGeneric extends SDMSTable
{

	public final static String tableName = "RUNNABLE_QUEUE";
	public static SDMSRunnableQueueTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SME_ID"
		, "SCOPE_ID"
		, "STATE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_scopeId;
	public static SDMSIndex idx_state;
	public static SDMSIndex idx_smeId_scopeId;
	public static SDMSIndex idx_scopeId_state;

	public SDMSRunnableQueueTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "RunnableQueue"));
		}
		table = (SDMSRunnableQueueTable) this;
		SDMSRunnableQueueTableGeneric.table = (SDMSRunnableQueueTable) this;
		isVersioned = false;
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId");
		idx_scopeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "scopeId");
		idx_state = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "state");
		idx_smeId_scopeId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "smeId_scopeId");
		idx_scopeId_state = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "scopeId_state");
	}
	public SDMSRunnableQueue create(SystemEnvironment env
	                                ,Long p_smeId
	                                ,Long p_scopeId
	                                ,Integer p_state
	                               )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "RunnableQueue"));
		}
		validate(env
		         , p_smeId
		         , p_scopeId
		         , p_state
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSRunnableQueueGeneric o = new SDMSRunnableQueueGeneric(env
		                , p_smeId
		                , p_scopeId
		                , p_state
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                         );

		SDMSRunnableQueue p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSRunnableQueue)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSRunnableQueue)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSRunnableQueue p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_smeId
	                        ,Long p_scopeId
	                        ,Integer p_state
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSRunnableQueueGeneric.checkState(p_state))
			throw new FatalException(new SDMSMessage(env, "01110182023", "RunnableQueue: $1 $2", "state", p_state));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long smeId;
		Long scopeId;
		Integer state;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			smeId = Long.valueOf (r.getLong(2));
			scopeId = Long.valueOf (r.getLong(3));
			if (r.wasNull()) scopeId = null;
			state = Integer.valueOf (r.getInt(4));
			creatorUId = Long.valueOf (r.getLong(5));
			createTs = Long.valueOf (r.getLong(6));
			changerUId = Long.valueOf (r.getLong(7));
			changeTs = Long.valueOf (r.getLong(8));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "RunnableQueue: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSRunnableQueueGeneric(id,
		                                    smeId,
		                                    scopeId,
		                                    state,
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
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "SCOPE_ID" + equote +
		                                   ", " + squote + "STATE" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote +
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
		ok =  idx_smeId.check(((SDMSRunnableQueueGeneric) o).smeId, o);
		out = out + "idx_smeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_scopeId.check(((SDMSRunnableQueueGeneric) o).scopeId, o);
		out = out + "idx_scopeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_state.check(((SDMSRunnableQueueGeneric) o).state, o);
		out = out + "idx_state: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSRunnableQueueGeneric) o).smeId);
		k.add(((SDMSRunnableQueueGeneric) o).scopeId);
		ok =  idx_smeId_scopeId.check(k, o);
		out = out + "idx_smeId_scopeId: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSRunnableQueueGeneric) o).scopeId);
		k.add(((SDMSRunnableQueueGeneric) o).state);
		ok =  idx_scopeId_state.check(k, o);
		out = out + "idx_scopeId_state: " + (ok ? "ok" : "missing") + "\n";
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
		idx_smeId.put(env, ((SDMSRunnableQueueGeneric) o).smeId, o, ((1 & indexMember) != 0));
		idx_scopeId.put(env, ((SDMSRunnableQueueGeneric) o).scopeId, o, ((2 & indexMember) != 0));
		idx_state.put(env, ((SDMSRunnableQueueGeneric) o).state, o, ((4 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSRunnableQueueGeneric) o).smeId);
		k.add(((SDMSRunnableQueueGeneric) o).scopeId);
		idx_smeId_scopeId.put(env, k, o, ((8 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSRunnableQueueGeneric) o).scopeId);
		k.add(((SDMSRunnableQueueGeneric) o).state);
		idx_scopeId_state.put(env, k, o, ((16 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_smeId.remove(env, ((SDMSRunnableQueueGeneric) o).smeId, o);
		idx_scopeId.remove(env, ((SDMSRunnableQueueGeneric) o).scopeId, o);
		idx_state.remove(env, ((SDMSRunnableQueueGeneric) o).state, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSRunnableQueueGeneric) o).smeId);
		k.add(((SDMSRunnableQueueGeneric) o).scopeId);
		idx_smeId_scopeId.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSRunnableQueueGeneric) o).scopeId);
		k.add(((SDMSRunnableQueueGeneric) o).state);
		idx_scopeId_state.remove(env, k, o);
	}

	public static SDMSRunnableQueue getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSRunnableQueue) table.get(env, id);
	}

	public static SDMSRunnableQueue getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSRunnableQueue) table.getForUpdate(env, id);
	}

	public static SDMSRunnableQueue getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSRunnableQueue) table.get(env, id, version);
	}

	public static SDMSRunnableQueue idx_smeId_scopeId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSRunnableQueue)  SDMSRunnableQueueTableGeneric.idx_smeId_scopeId.getUnique(env, key);
	}

	public static SDMSRunnableQueue idx_smeId_scopeId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSRunnableQueue)  SDMSRunnableQueueTableGeneric.idx_smeId_scopeId.getUniqueForUpdate(env, key);
	}

	public static SDMSRunnableQueue idx_smeId_scopeId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSRunnableQueue)  SDMSRunnableQueueTableGeneric.idx_smeId_scopeId.getUnique(env, key, version);
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
