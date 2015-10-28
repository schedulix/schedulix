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

public class SDMSTriggerQueueTableGeneric extends SDMSTable
{

	public final static String tableName = "TRIGGER_QUEUE";
	public static SDMSTriggerQueueTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SME_ID"
		, "TR_ID"
		, "NEXT_TRIGGER_TIME"
		, "TIMES_CHECKED"
		, "TIMES_TRIGGERED"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_trId;
	public static SDMSIndex idx_smeId_trId;

	public SDMSTriggerQueueTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "TriggerQueue"));
		}
		table = (SDMSTriggerQueueTable) this;
		SDMSTriggerQueueTableGeneric.table = (SDMSTriggerQueueTable) this;
		isVersioned = false;
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId");
		idx_trId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "trId");
		idx_smeId_trId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "smeId_trId");
	}
	public SDMSTriggerQueue create(SystemEnvironment env
	                               ,Long p_smeId
	                               ,Long p_trId
	                               ,Long p_nextTriggerTime
	                               ,Integer p_timesChecked
	                               ,Integer p_timesTriggered
	                              )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "TriggerQueue"));
		}
		validate(env
		         , p_smeId
		         , p_trId
		         , p_nextTriggerTime
		         , p_timesChecked
		         , p_timesTriggered
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSTriggerQueueGeneric o = new SDMSTriggerQueueGeneric(env
		                , p_smeId
		                , p_trId
		                , p_nextTriggerTime
		                , p_timesChecked
		                , p_timesTriggered
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                       );

		SDMSTriggerQueue p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSTriggerQueue)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSTriggerQueue)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSTriggerQueue p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_smeId
	                        ,Long p_trId
	                        ,Long p_nextTriggerTime
	                        ,Integer p_timesChecked
	                        ,Integer p_timesTriggered
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
		Long smeId;
		Long trId;
		Long nextTriggerTime;
		Integer timesChecked;
		Integer timesTriggered;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			smeId = new Long (r.getLong(2));
			trId = new Long (r.getLong(3));
			nextTriggerTime = new Long (r.getLong(4));
			timesChecked = new Integer (r.getInt(5));
			timesTriggered = new Integer (r.getInt(6));
			creatorUId = new Long (r.getLong(7));
			createTs = new Long (r.getLong(8));
			changerUId = new Long (r.getLong(9));
			changeTs = new Long (r.getLong(10));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "TriggerQueue: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSTriggerQueueGeneric(id,
		                                   smeId,
		                                   trId,
		                                   nextTriggerTime,
		                                   timesChecked,
		                                   timesTriggered,
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
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "TR_ID" + equote +
		                                   ", " + squote + "NEXT_TRIGGER_TIME" + equote +
		                                   ", " + squote + "TIMES_CHECKED" + equote +
		                                   ", " + squote + "TIMES_TRIGGERED" + equote +
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
		ok =  idx_smeId.check(((SDMSTriggerQueueGeneric) o).smeId, o);
		out = out + "idx_smeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_trId.check(((SDMSTriggerQueueGeneric) o).trId, o);
		out = out + "idx_trId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerQueueGeneric) o).smeId);
		k.add(((SDMSTriggerQueueGeneric) o).trId);
		ok =  idx_smeId_trId.check(k, o);
		out = out + "idx_smeId_trId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_smeId.put(env, ((SDMSTriggerQueueGeneric) o).smeId, o, ((1 & indexMember) != 0));
		idx_trId.put(env, ((SDMSTriggerQueueGeneric) o).trId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerQueueGeneric) o).smeId);
		k.add(((SDMSTriggerQueueGeneric) o).trId);
		idx_smeId_trId.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_smeId.remove(env, ((SDMSTriggerQueueGeneric) o).smeId, o);
		idx_trId.remove(env, ((SDMSTriggerQueueGeneric) o).trId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerQueueGeneric) o).smeId);
		k.add(((SDMSTriggerQueueGeneric) o).trId);
		idx_smeId_trId.remove(env, k, o);
	}

	public static SDMSTriggerQueue getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTriggerQueue) table.get(env, id);
	}

	public static SDMSTriggerQueue getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSTriggerQueue) table.get(env, id, version);
	}

	public static SDMSTriggerQueue idx_smeId_trId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSTriggerQueue)  SDMSTriggerQueueTableGeneric.idx_smeId_trId.getUnique(env, key);
	}

	public static SDMSTriggerQueue idx_smeId_trId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSTriggerQueue)  SDMSTriggerQueueTableGeneric.idx_smeId_trId.getUnique(env, key, version);
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
