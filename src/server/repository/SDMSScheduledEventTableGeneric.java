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

public class SDMSScheduledEventTableGeneric extends SDMSTable
{

	public final static String __version = "SDMSScheduledEventTableGeneric $Revision: 2.11 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "SCHEDULED_EVENT";
	public static SDMSScheduledEventTable table  = null;

	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_sceId;
	public static SDMSIndex idx_evtId;
	public static SDMSIndex idx_sceId_evtId;

	public SDMSScheduledEventTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ScheduledEvent"));
		}
		table = (SDMSScheduledEventTable) this;
		SDMSScheduledEventTableGeneric.table = (SDMSScheduledEventTable) this;
		isVersioned = false;
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_sceId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_evtId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_sceId_evtId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSScheduledEvent create(SystemEnvironment env
	                                 ,Long p_ownerId
	                                 ,Long p_sceId
	                                 ,Long p_evtId
	                                 ,Boolean p_isActive
	                                 ,Boolean p_isBroken
	                                 ,String p_errorCode
	                                 ,String p_errorMsg
	                                 ,Long p_lastStartTime
	                                 ,Long p_nextActivityTime
	                                 ,Boolean p_nextActivityIsTrigger
	                                 ,Integer p_backlogHandling
	                                 ,Integer p_suspendLimit
	                                 ,Integer p_suspendLimitMultiplier
	                                 ,Boolean p_isCalendar
	                                 ,Integer p_calendarHorizon
	                                )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ScheduledEvent"));
		}
		validate(env
		         , p_ownerId
		         , p_sceId
		         , p_evtId
		         , p_isActive
		         , p_isBroken
		         , p_errorCode
		         , p_errorMsg
		         , p_lastStartTime
		         , p_nextActivityTime
		         , p_nextActivityIsTrigger
		         , p_backlogHandling
		         , p_suspendLimit
		         , p_suspendLimitMultiplier
		         , p_isCalendar
		         , p_calendarHorizon
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSScheduledEventGeneric o = new SDMSScheduledEventGeneric(env
		                , p_ownerId
		                , p_sceId
		                , p_evtId
		                , p_isActive
		                , p_isBroken
		                , p_errorCode
		                , p_errorMsg
		                , p_lastStartTime
		                , p_nextActivityTime
		                , p_nextActivityIsTrigger
		                , p_backlogHandling
		                , p_suspendLimit
		                , p_suspendLimitMultiplier
		                , p_isCalendar
		                , p_calendarHorizon
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                           );

		SDMSScheduledEvent p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSScheduledEvent)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSScheduledEvent)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSScheduledEvent p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_ownerId
	                        ,Long p_sceId
	                        ,Long p_evtId
	                        ,Boolean p_isActive
	                        ,Boolean p_isBroken
	                        ,String p_errorCode
	                        ,String p_errorMsg
	                        ,Long p_lastStartTime
	                        ,Long p_nextActivityTime
	                        ,Boolean p_nextActivityIsTrigger
	                        ,Integer p_backlogHandling
	                        ,Integer p_suspendLimit
	                        ,Integer p_suspendLimitMultiplier
	                        ,Boolean p_isCalendar
	                        ,Integer p_calendarHorizon
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSScheduledEventGeneric.checkIsActive(p_isActive))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ScheduledEvent: $1 $2", "isActive", p_isActive));
		if (!SDMSScheduledEventGeneric.checkIsBroken(p_isBroken))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ScheduledEvent: $1 $2", "isBroken", p_isBroken));
		if (!SDMSScheduledEventGeneric.checkBacklogHandling(p_backlogHandling))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ScheduledEvent: $1 $2", "backlogHandling", p_backlogHandling));
		if (!SDMSScheduledEventGeneric.checkSuspendLimit(p_suspendLimit))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ScheduledEvent: $1 $2", "suspendLimit", p_suspendLimit));
		if (!SDMSScheduledEventGeneric.checkIsCalendar(p_isCalendar))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ScheduledEvent: $1 $2", "isCalendar", p_isCalendar));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long ownerId;
		Long sceId;
		Long evtId;
		Boolean isActive;
		Boolean isBroken;
		String errorCode;
		String errorMsg;
		Long lastStartTime;
		Long nextActivityTime;
		Boolean nextActivityIsTrigger;
		Integer backlogHandling;
		Integer suspendLimit;
		Integer suspendLimitMultiplier;
		Boolean isCalendar;
		Integer calendarHorizon;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			ownerId = new Long (r.getLong(2));
			sceId = new Long (r.getLong(3));
			evtId = new Long (r.getLong(4));
			isActive = new Boolean ((r.getInt(5) == 0 ? false : true));
			isBroken = new Boolean ((r.getInt(6) == 0 ? false : true));
			errorCode = r.getString(7);
			if (r.wasNull()) errorCode = null;
			errorMsg = r.getString(8);
			if (r.wasNull()) errorMsg = null;
			lastStartTime = new Long (r.getLong(9));
			if (r.wasNull()) lastStartTime = null;
			nextActivityTime = new Long (r.getLong(10));
			if (r.wasNull()) nextActivityTime = null;
			nextActivityIsTrigger = new Boolean ((r.getInt(11) == 0 ? false : true));
			if (r.wasNull()) nextActivityIsTrigger = null;
			backlogHandling = new Integer (r.getInt(12));
			suspendLimit = new Integer (r.getInt(13));
			if (r.wasNull()) suspendLimit = null;
			suspendLimitMultiplier = new Integer (r.getInt(14));
			if (r.wasNull()) suspendLimitMultiplier = null;
			isCalendar = new Boolean ((r.getInt(15) == 0 ? false : true));
			calendarHorizon = new Integer (r.getInt(16));
			if (r.wasNull()) calendarHorizon = null;
			creatorUId = new Long (r.getLong(17));
			createTs = new Long (r.getLong(18));
			changerUId = new Long (r.getLong(19));
			changeTs = new Long (r.getLong(20));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ScheduledEvent: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSScheduledEventGeneric(id,
		                                     ownerId,
		                                     sceId,
		                                     evtId,
		                                     isActive,
		                                     isBroken,
		                                     errorCode,
		                                     errorMsg,
		                                     lastStartTime,
		                                     nextActivityTime,
		                                     nextActivityIsTrigger,
		                                     backlogHandling,
		                                     suspendLimit,
		                                     suspendLimitMultiplier,
		                                     isCalendar,
		                                     calendarHorizon,
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

		final String driverName = env.dbConnection.getMetaData().getDriverName();
		final boolean postgres = driverName.startsWith("PostgreSQL");
		String squote = "";
		String equote = "";
		if (driverName.startsWith("MySQL")) {
			squote = "`";
			equote = "`";
		}
		if (driverName.startsWith("Microsoft")) {
			squote = "[";
			equote = "]";
		}
		Statement stmt = env.dbConnection.createStatement();

		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   "ID" +
		                                   ", " + squote + "OWNER_ID" + equote +
		                                   ", " + squote + "SCE_ID" + equote +
		                                   ", " + squote + "EVT_ID" + equote +
		                                   ", " + squote + "ACTIVE" + equote +
		                                   ", " + squote + "BROKEN" + equote +
		                                   ", " + squote + "ERROR_CODE" + equote +
		                                   ", " + squote + "ERROR_MSG" + equote +
		                                   ", " + squote + "LAST_START_TIME" + equote +
		                                   ", " + squote + "NEXT_START_TIME" + equote +
		                                   ", " + squote + "NEXT_IS_TRIGGER" + equote +
		                                   ", " + squote + "BACKLOG_HANDLING" + equote +
		                                   ", " + squote + "SUSPEND_LIMIT" + equote +
		                                   ", " + squote + "SUSPEND_LIMIT_MULTIPLIER" + equote +
		                                   ", " + squote + "IS_CALENDAR" + equote +
		                                   ", " + squote + "CALENDAR_HORIZON" + equote +
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

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_ownerId.put(env, ((SDMSScheduledEventGeneric) o).ownerId, o);
		idx_sceId.put(env, ((SDMSScheduledEventGeneric) o).sceId, o);
		idx_evtId.put(env, ((SDMSScheduledEventGeneric) o).evtId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScheduledEventGeneric) o).sceId);
		k.add(((SDMSScheduledEventGeneric) o).evtId);
		idx_sceId_evtId.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_ownerId.remove(env, ((SDMSScheduledEventGeneric) o).ownerId, o);
		idx_sceId.remove(env, ((SDMSScheduledEventGeneric) o).sceId, o);
		idx_evtId.remove(env, ((SDMSScheduledEventGeneric) o).evtId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScheduledEventGeneric) o).sceId);
		k.add(((SDMSScheduledEventGeneric) o).evtId);
		idx_sceId_evtId.remove(env, k, o);
	}

	public static SDMSScheduledEvent getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSScheduledEvent) table.get(env, id);
	}

	public static SDMSScheduledEvent getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSScheduledEvent) table.get(env, id, version);
	}

	public static SDMSScheduledEvent idx_sceId_evtId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSScheduledEvent)  SDMSScheduledEventTableGeneric.idx_sceId_evtId.getUnique(env, key);
	}

	public static SDMSScheduledEvent idx_sceId_evtId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSScheduledEvent)  SDMSScheduledEventTableGeneric.idx_sceId_evtId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
