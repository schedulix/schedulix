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

public class SDMSIntervalDispatcherTableGeneric extends SDMSTable
{

	public final static String tableName = "INTERVAL_DISPATCHER";
	public static SDMSIntervalDispatcherTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "INT_ID"
		, "SEQ_NO"
		, "NAME"
		, "SELECT_INT_ID"
		, "FILTER_INT_ID"
		, "IS_ENABLED"
		, "IS_ACTIVE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_intId;
	public static SDMSIndex idx_selectIntId;
	public static SDMSIndex idx_filterIntId;
	public static SDMSIndex idx_intId_seqNo;

	public SDMSIntervalDispatcherTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "IntervalDispatcher"));
		}
		table = (SDMSIntervalDispatcherTable) this;
		SDMSIntervalDispatcherTableGeneric.table = (SDMSIntervalDispatcherTable) this;
		isVersioned = false;
		idx_intId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "intId");
		idx_selectIntId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "selectIntId");
		idx_filterIntId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "filterIntId");
		idx_intId_seqNo = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "intId_seqNo");
	}
	public SDMSIntervalDispatcher create(SystemEnvironment env
	                                     ,Long p_intId
	                                     ,Integer p_seqNo
	                                     ,String p_name
	                                     ,Long p_selectIntId
	                                     ,Long p_filterIntId
	                                     ,Boolean p_isEnabled
	                                     ,Boolean p_isActive
	                                    )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "IntervalDispatcher"));
		}
		validate(env
		         , p_intId
		         , p_seqNo
		         , p_name
		         , p_selectIntId
		         , p_filterIntId
		         , p_isEnabled
		         , p_isActive
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSIntervalDispatcherGeneric o = new SDMSIntervalDispatcherGeneric(env
		                , p_intId
		                , p_seqNo
		                , p_name
		                , p_selectIntId
		                , p_filterIntId
		                , p_isEnabled
		                , p_isActive
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                   );

		SDMSIntervalDispatcher p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSIntervalDispatcher)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSIntervalDispatcher)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSIntervalDispatcher p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_intId
	                        ,Integer p_seqNo
	                        ,String p_name
	                        ,Long p_selectIntId
	                        ,Long p_filterIntId
	                        ,Boolean p_isEnabled
	                        ,Boolean p_isActive
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
		Long intId;
		Integer seqNo;
		String name;
		Long selectIntId;
		Long filterIntId;
		Boolean isEnabled;
		Boolean isActive;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			intId = Long.valueOf (r.getLong(2));
			seqNo = Integer.valueOf (r.getInt(3));
			name = r.getString(4);
			selectIntId = Long.valueOf (r.getLong(5));
			if (r.wasNull()) selectIntId = null;
			filterIntId = Long.valueOf (r.getLong(6));
			if (r.wasNull()) filterIntId = null;
			isEnabled = Boolean.valueOf ((r.getInt(7) == 0 ? false : true));
			isActive = Boolean.valueOf ((r.getInt(8) == 0 ? false : true));
			creatorUId = Long.valueOf (r.getLong(9));
			createTs = Long.valueOf (r.getLong(10));
			changerUId = Long.valueOf (r.getLong(11));
			changeTs = Long.valueOf (r.getLong(12));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "IntervalDispatcher: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSIntervalDispatcherGeneric(id,
		                intId,
		                seqNo,
		                name,
		                selectIntId,
		                filterIntId,
		                isEnabled,
		                isActive,
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
		                                   ", " + squote + "INT_ID" + equote +
		                                   ", " + squote + "SEQ_NO" + equote +
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "SELECT_INT_ID" + equote +
		                                   ", " + squote + "FILTER_INT_ID" + equote +
		                                   ", " + squote + "IS_ENABLED" + equote +
		                                   ", " + squote + "IS_ACTIVE" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote +
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
		ok =  idx_intId.check(((SDMSIntervalDispatcherGeneric) o).intId, o);
		out = out + "idx_intId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_selectIntId.check(((SDMSIntervalDispatcherGeneric) o).selectIntId, o);
		out = out + "idx_selectIntId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_filterIntId.check(((SDMSIntervalDispatcherGeneric) o).filterIntId, o);
		out = out + "idx_filterIntId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIntervalDispatcherGeneric) o).intId);
		k.add(((SDMSIntervalDispatcherGeneric) o).seqNo);
		ok =  idx_intId_seqNo.check(k, o);
		out = out + "idx_intId_seqNo: " + (ok ? "ok" : "missing") + "\n";
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
		idx_intId.put(env, ((SDMSIntervalDispatcherGeneric) o).intId, o, ((1 & indexMember) != 0));
		idx_selectIntId.put(env, ((SDMSIntervalDispatcherGeneric) o).selectIntId, o, ((2 & indexMember) != 0));
		idx_filterIntId.put(env, ((SDMSIntervalDispatcherGeneric) o).filterIntId, o, ((4 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIntervalDispatcherGeneric) o).intId);
		k.add(((SDMSIntervalDispatcherGeneric) o).seqNo);
		idx_intId_seqNo.put(env, k, o, ((8 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_intId.remove(env, ((SDMSIntervalDispatcherGeneric) o).intId, o);
		idx_selectIntId.remove(env, ((SDMSIntervalDispatcherGeneric) o).selectIntId, o);
		idx_filterIntId.remove(env, ((SDMSIntervalDispatcherGeneric) o).filterIntId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIntervalDispatcherGeneric) o).intId);
		k.add(((SDMSIntervalDispatcherGeneric) o).seqNo);
		idx_intId_seqNo.remove(env, k, o);
	}

	public static SDMSIntervalDispatcher getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSIntervalDispatcher) table.get(env, id);
	}

	public static SDMSIntervalDispatcher getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSIntervalDispatcher) table.getForUpdate(env, id);
	}

	public static SDMSIntervalDispatcher getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSIntervalDispatcher) table.get(env, id, version);
	}

	public static SDMSIntervalDispatcher idx_intId_seqNo_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSIntervalDispatcher)  SDMSIntervalDispatcherTableGeneric.idx_intId_seqNo.getUnique(env, key);
	}

	public static SDMSIntervalDispatcher idx_intId_seqNo_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSIntervalDispatcher)  SDMSIntervalDispatcherTableGeneric.idx_intId_seqNo.getUniqueForUpdate(env, key);
	}

	public static SDMSIntervalDispatcher idx_intId_seqNo_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSIntervalDispatcher)  SDMSIntervalDispatcherTableGeneric.idx_intId_seqNo.getUnique(env, key, version);
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
