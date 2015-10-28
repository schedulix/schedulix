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

public class SDMSCalendarTableGeneric extends SDMSTable
{

	public final static String tableName = "CALENDAR";
	public static SDMSCalendarTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SCEV_ID"
		, "STARTTIME"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_scevId;
	public static SDMSIndex idx_scevId_starttime;

	public SDMSCalendarTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "Calendar"));
		}
		table = (SDMSCalendarTable) this;
		SDMSCalendarTableGeneric.table = (SDMSCalendarTable) this;
		isVersioned = false;
		idx_scevId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "scevId");
		idx_scevId_starttime = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "scevId_starttime");
	}
	public SDMSCalendar create(SystemEnvironment env
	                           ,Long p_scevId
	                           ,Long p_starttime
	                          )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "Calendar"));
		}
		validate(env
		         , p_scevId
		         , p_starttime
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSCalendarGeneric o = new SDMSCalendarGeneric(env
		                , p_scevId
		                , p_starttime
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                               );

		SDMSCalendar p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSCalendar)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSCalendar)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSCalendar p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_scevId
	                        ,Long p_starttime
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
		Long scevId;
		Long starttime;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			scevId = new Long (r.getLong(2));
			starttime = new Long (r.getLong(3));
			creatorUId = new Long (r.getLong(4));
			createTs = new Long (r.getLong(5));
			changerUId = new Long (r.getLong(6));
			changeTs = new Long (r.getLong(7));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "Calendar: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSCalendarGeneric(id,
		                               scevId,
		                               starttime,
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
		                                   ", " + squote + "SCEV_ID" + equote +
		                                   ", " + squote + "STARTTIME" + equote +
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
		ok =  idx_scevId.check(((SDMSCalendarGeneric) o).scevId, o);
		out = out + "idx_scevId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSCalendarGeneric) o).scevId);
		k.add(((SDMSCalendarGeneric) o).starttime);
		ok =  idx_scevId_starttime.check(k, o);
		out = out + "idx_scevId_starttime: " + (ok ? "ok" : "missing") + "\n";
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
		idx_scevId.put(env, ((SDMSCalendarGeneric) o).scevId, o, ((1 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSCalendarGeneric) o).scevId);
		k.add(((SDMSCalendarGeneric) o).starttime);
		idx_scevId_starttime.put(env, k, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_scevId.remove(env, ((SDMSCalendarGeneric) o).scevId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSCalendarGeneric) o).scevId);
		k.add(((SDMSCalendarGeneric) o).starttime);
		idx_scevId_starttime.remove(env, k, o);
	}

	public static SDMSCalendar getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSCalendar) table.get(env, id);
	}

	public static SDMSCalendar getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSCalendar) table.get(env, id, version);
	}

	public static SDMSCalendar idx_scevId_starttime_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSCalendar)  SDMSCalendarTableGeneric.idx_scevId_starttime.getUnique(env, key);
	}

	public static SDMSCalendar idx_scevId_starttime_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSCalendar)  SDMSCalendarTableGeneric.idx_scevId_starttime.getUnique(env, key, version);
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
