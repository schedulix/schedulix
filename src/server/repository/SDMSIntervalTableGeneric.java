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

public class SDMSIntervalTableGeneric extends SDMSTable
{

	public final static String tableName = "INTERVALL";
	public static SDMSIntervalTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "OWNER_ID"
		, "START_TIME"
		, "END_TIME"
		, "DELAY"
		, "BASE_INTERVAL"
		, "BASE_INTERVAL_MULTIPLIER"
		, "DURATION"
		, "DURATION_MULTIPLIER"
		, "SYNC_TIME"
		, "IS_INVERSE"
		, "IS_MERGE"
		, "EMBEDDED_INT_ID"
		, "SE_ID"
		, "OBJ_ID"
		, "OBJ_TYPE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_name;
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_embeddedIntervalId;
	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_objId;
	public static SDMSIndex idx_name_objId;

	public SDMSIntervalTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "Interval"));
		}
		table = (SDMSIntervalTable) this;
		SDMSIntervalTableGeneric.table = (SDMSIntervalTable) this;
		isVersioned = false;
		idx_name = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "name");
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_embeddedIntervalId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "embeddedIntervalId");
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seId");
		idx_objId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "objId");
		idx_name_objId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "name_objId");
	}
	public SDMSInterval create(SystemEnvironment env
	                           ,String p_name
	                           ,Long p_ownerId
	                           ,Long p_startTime
	                           ,Long p_endTime
	                           ,Long p_delay
	                           ,Integer p_baseInterval
	                           ,Integer p_baseIntervalMultiplier
	                           ,Integer p_duration
	                           ,Integer p_durationMultiplier
	                           ,Long p_syncTime
	                           ,Boolean p_isInverse
	                           ,Boolean p_isMerge
	                           ,Long p_embeddedIntervalId
	                           ,Long p_seId
	                           ,Long p_objId
	                           ,Integer p_objType
	                          )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "Interval"));
		}
		validate(env
		         , p_name
		         , p_ownerId
		         , p_startTime
		         , p_endTime
		         , p_delay
		         , p_baseInterval
		         , p_baseIntervalMultiplier
		         , p_duration
		         , p_durationMultiplier
		         , p_syncTime
		         , p_isInverse
		         , p_isMerge
		         , p_embeddedIntervalId
		         , p_seId
		         , p_objId
		         , p_objType
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSIntervalGeneric o = new SDMSIntervalGeneric(env
		                , p_name
		                , p_ownerId
		                , p_startTime
		                , p_endTime
		                , p_delay
		                , p_baseInterval
		                , p_baseIntervalMultiplier
		                , p_duration
		                , p_durationMultiplier
		                , p_syncTime
		                , p_isInverse
		                , p_isMerge
		                , p_embeddedIntervalId
		                , p_seId
		                , p_objId
		                , p_objType
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                               );

		SDMSInterval p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSInterval)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSInterval)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSInterval p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_ownerId
	                        ,Long p_startTime
	                        ,Long p_endTime
	                        ,Long p_delay
	                        ,Integer p_baseInterval
	                        ,Integer p_baseIntervalMultiplier
	                        ,Integer p_duration
	                        ,Integer p_durationMultiplier
	                        ,Long p_syncTime
	                        ,Boolean p_isInverse
	                        ,Boolean p_isMerge
	                        ,Long p_embeddedIntervalId
	                        ,Long p_seId
	                        ,Long p_objId
	                        ,Integer p_objType
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSIntervalGeneric.checkBaseInterval(p_baseInterval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Interval: $1 $2", "baseInterval", p_baseInterval));
		if (!SDMSIntervalGeneric.checkDuration(p_duration))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Interval: $1 $2", "duration", p_duration));
		if (!SDMSIntervalGeneric.checkObjType(p_objType))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Interval: $1 $2", "objType", p_objType));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		String name;
		Long ownerId;
		Long startTime;
		Long endTime;
		Long delay;
		Integer baseInterval;
		Integer baseIntervalMultiplier;
		Integer duration;
		Integer durationMultiplier;
		Long syncTime;
		Boolean isInverse;
		Boolean isMerge;
		Long embeddedIntervalId;
		Long seId;
		Long objId;
		Integer objType;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			ownerId = new Long (r.getLong(3));
			startTime = new Long (r.getLong(4));
			if (r.wasNull()) startTime = null;
			endTime = new Long (r.getLong(5));
			if (r.wasNull()) endTime = null;
			delay = new Long (r.getLong(6));
			if (r.wasNull()) delay = null;
			baseInterval = new Integer (r.getInt(7));
			if (r.wasNull()) baseInterval = null;
			baseIntervalMultiplier = new Integer (r.getInt(8));
			if (r.wasNull()) baseIntervalMultiplier = null;
			duration = new Integer (r.getInt(9));
			if (r.wasNull()) duration = null;
			durationMultiplier = new Integer (r.getInt(10));
			if (r.wasNull()) durationMultiplier = null;
			syncTime = new Long (r.getLong(11));
			isInverse = new Boolean ((r.getInt(12) == 0 ? false : true));
			isMerge = new Boolean ((r.getInt(13) == 0 ? false : true));
			embeddedIntervalId = new Long (r.getLong(14));
			if (r.wasNull()) embeddedIntervalId = null;
			seId = new Long (r.getLong(15));
			if (r.wasNull()) seId = null;
			objId = new Long (r.getLong(16));
			if (r.wasNull()) objId = null;
			objType = new Integer (r.getInt(17));
			if (r.wasNull()) objType = null;
			creatorUId = new Long (r.getLong(18));
			createTs = new Long (r.getLong(19));
			changerUId = new Long (r.getLong(20));
			changeTs = new Long (r.getLong(21));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "Interval: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSIntervalGeneric(id,
		                               name,
		                               ownerId,
		                               startTime,
		                               endTime,
		                               delay,
		                               baseInterval,
		                               baseIntervalMultiplier,
		                               duration,
		                               durationMultiplier,
		                               syncTime,
		                               isInverse,
		                               isMerge,
		                               embeddedIntervalId,
		                               seId,
		                               objId,
		                               objType,
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
		                                   ", " + squote + "OWNER_ID" + equote +
		                                   ", " + squote + "START_TIME" + equote +
		                                   ", " + squote + "END_TIME" + equote +
		                                   ", " + squote + "DELAY" + equote +
		                                   ", " + squote + "BASE_INTERVAL" + equote +
		                                   ", " + squote + "BASE_INTERVAL_MULTIPLIER" + equote +
		                                   ", " + squote + "DURATION" + equote +
		                                   ", " + squote + "DURATION_MULTIPLIER" + equote +
		                                   ", " + squote + "SYNC_TIME" + equote +
		                                   ", " + squote + "IS_INVERSE" + equote +
		                                   ", " + squote + "IS_MERGE" + equote +
		                                   ", " + squote + "EMBEDDED_INT_ID" + equote +
		                                   ", " + squote + "SE_ID" + equote +
		                                   ", " + squote + "OBJ_ID" + equote +
		                                   ", " + squote + "OBJ_TYPE" + equote +
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
		ok =  idx_name.check(((SDMSIntervalGeneric) o).name, o);
		out = out + "idx_name: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_ownerId.check(((SDMSIntervalGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_embeddedIntervalId.check(((SDMSIntervalGeneric) o).embeddedIntervalId, o);
		out = out + "idx_embeddedIntervalId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_seId.check(((SDMSIntervalGeneric) o).seId, o);
		out = out + "idx_seId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_objId.check(((SDMSIntervalGeneric) o).objId, o);
		out = out + "idx_objId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIntervalGeneric) o).name);
		k.add(((SDMSIntervalGeneric) o).objId);
		ok =  idx_name_objId.check(k, o);
		out = out + "idx_name_objId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_name.put(env, ((SDMSIntervalGeneric) o).name, o, ((1 & indexMember) != 0));
		idx_ownerId.put(env, ((SDMSIntervalGeneric) o).ownerId, o, ((2 & indexMember) != 0));
		idx_embeddedIntervalId.put(env, ((SDMSIntervalGeneric) o).embeddedIntervalId, o, ((4 & indexMember) != 0));
		idx_seId.put(env, ((SDMSIntervalGeneric) o).seId, o, ((8 & indexMember) != 0));
		idx_objId.put(env, ((SDMSIntervalGeneric) o).objId, o, ((16 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIntervalGeneric) o).name);
		k.add(((SDMSIntervalGeneric) o).objId);
		idx_name_objId.put(env, k, o, ((32 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_name.remove(env, ((SDMSIntervalGeneric) o).name, o);
		idx_ownerId.remove(env, ((SDMSIntervalGeneric) o).ownerId, o);
		idx_embeddedIntervalId.remove(env, ((SDMSIntervalGeneric) o).embeddedIntervalId, o);
		idx_seId.remove(env, ((SDMSIntervalGeneric) o).seId, o);
		idx_objId.remove(env, ((SDMSIntervalGeneric) o).objId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSIntervalGeneric) o).name);
		k.add(((SDMSIntervalGeneric) o).objId);
		idx_name_objId.remove(env, k, o);
	}

	public static SDMSInterval getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSInterval) table.get(env, id);
	}

	public static SDMSInterval getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSInterval) table.getForUpdate(env, id);
	}

	public static SDMSInterval getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSInterval) table.get(env, id, version);
	}

	public static SDMSInterval idx_name_objId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSInterval)  SDMSIntervalTableGeneric.idx_name_objId.getUnique(env, key);
	}

	public static SDMSInterval idx_name_objId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSInterval)  SDMSIntervalTableGeneric.idx_name_objId.getUniqueForUpdate(env, key);
	}

	public static SDMSInterval idx_name_objId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSInterval)  SDMSIntervalTableGeneric.idx_name_objId.getUnique(env, key, version);
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
