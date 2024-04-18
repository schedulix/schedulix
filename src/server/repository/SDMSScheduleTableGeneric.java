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

public class SDMSScheduleTableGeneric extends SDMSTable
{

	public final static String tableName = "SCHEDULE";
	public static SDMSScheduleTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "OWNER_ID"
		, "INT_ID"
		, "PARENT_ID"
		, "TIME_ZONE"
		, "SE_ID"
		, "ACTIVE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
		, "INHERIT_PRIVS"
	};
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_intId;
	public static SDMSIndex idx_parentId;
	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_parentId_name;

	public SDMSScheduleTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "Schedule"));
		}
		table = (SDMSScheduleTable) this;
		SDMSScheduleTableGeneric.table = (SDMSScheduleTable) this;
		isVersioned = false;
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_intId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "intId");
		idx_parentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId");
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seId");
		idx_parentId_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "parentId_name");
	}
	public SDMSSchedule create(SystemEnvironment env
	                           ,String p_name
	                           ,Long p_ownerId
	                           ,Long p_intId
	                           ,Long p_parentId
	                           ,String p_timeZone
	                           ,Long p_seId
	                           ,Boolean p_isActive
	                           ,Long p_inheritPrivs
	                          )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "Schedule"));
		}
		validate(env
		         , p_name
		         , p_ownerId
		         , p_intId
		         , p_parentId
		         , p_timeZone
		         , p_seId
		         , p_isActive
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		         , p_inheritPrivs
		        );

		env.tx.beginSubTransaction(env);
		SDMSScheduleGeneric o = new SDMSScheduleGeneric(env
		                , p_name
		                , p_ownerId
		                , p_intId
		                , p_parentId
		                , p_timeZone
		                , p_seId
		                , p_isActive
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                , p_inheritPrivs
		                                               );

		SDMSSchedule p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSchedule)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSchedule)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSSchedule p)
	throws SDMSException
	{
		final Long parentId = p.getParentId(env);
		final SDMSSchedule parent = SDMSScheduleTable.getObject(env, parentId);
		if(!parent.checkPrivileges(env, SDMSPrivilege.CREATE_CONTENT))
			return false;
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_ownerId
	                        ,Long p_intId
	                        ,Long p_parentId
	                        ,String p_timeZone
	                        ,Long p_seId
	                        ,Boolean p_isActive
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                        ,Long p_inheritPrivs
	                       )
	throws SDMSException
	{
		if (!SDMSScheduleGeneric.checkIsActive(p_isActive))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Schedule: $1 $2", "isActive", p_isActive));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		String name;
		Long ownerId;
		Long intId;
		Long parentId;
		String timeZone;
		Long seId;
		Boolean isActive;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		Long inheritPrivs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			name = r.getString(2);
			ownerId = Long.valueOf (r.getLong(3));
			intId = Long.valueOf (r.getLong(4));
			if (r.wasNull()) intId = null;
			parentId = Long.valueOf (r.getLong(5));
			if (r.wasNull()) parentId = null;
			timeZone = r.getString(6);
			seId = Long.valueOf (r.getLong(7));
			if (r.wasNull()) seId = null;
			isActive = Boolean.valueOf ((r.getInt(8) == 0 ? false : true));
			creatorUId = Long.valueOf (r.getLong(9));
			createTs = Long.valueOf (r.getLong(10));
			changerUId = Long.valueOf (r.getLong(11));
			changeTs = Long.valueOf (r.getLong(12));
			inheritPrivs = Long.valueOf (r.getLong(13));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "Schedule: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSScheduleGeneric(id,
		                               name,
		                               ownerId,
		                               intId,
		                               parentId,
		                               timeZone,
		                               seId,
		                               isActive,
		                               creatorUId,
		                               createTs,
		                               changerUId,
		                               changeTs,
		                               inheritPrivs,
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
		                                   ", " + squote + "INT_ID" + equote +
		                                   ", " + squote + "PARENT_ID" + equote +
		                                   ", " + squote + "TIME_ZONE" + equote +
		                                   ", " + squote + "SE_ID" + equote +
		                                   ", " + squote + "ACTIVE" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", " + squote + "INHERIT_PRIVS" + equote +
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
		ok =  idx_ownerId.check(((SDMSScheduleGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_intId.check(((SDMSScheduleGeneric) o).intId, o);
		out = out + "idx_intId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_parentId.check(((SDMSScheduleGeneric) o).parentId, o);
		out = out + "idx_parentId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_seId.check(((SDMSScheduleGeneric) o).seId, o);
		out = out + "idx_seId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScheduleGeneric) o).parentId);
		k.add(((SDMSScheduleGeneric) o).name);
		ok =  idx_parentId_name.check(k, o);
		out = out + "idx_parentId_name: " + (ok ? "ok" : "missing") + "\n";
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
		idx_ownerId.put(env, ((SDMSScheduleGeneric) o).ownerId, o, ((1 & indexMember) != 0));
		idx_intId.put(env, ((SDMSScheduleGeneric) o).intId, o, ((2 & indexMember) != 0));
		idx_parentId.put(env, ((SDMSScheduleGeneric) o).parentId, o, ((4 & indexMember) != 0));
		idx_seId.put(env, ((SDMSScheduleGeneric) o).seId, o, ((8 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScheduleGeneric) o).parentId);
		k.add(((SDMSScheduleGeneric) o).name);
		idx_parentId_name.put(env, k, o, ((16 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_ownerId.remove(env, ((SDMSScheduleGeneric) o).ownerId, o);
		idx_intId.remove(env, ((SDMSScheduleGeneric) o).intId, o);
		idx_parentId.remove(env, ((SDMSScheduleGeneric) o).parentId, o);
		idx_seId.remove(env, ((SDMSScheduleGeneric) o).seId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScheduleGeneric) o).parentId);
		k.add(((SDMSScheduleGeneric) o).name);
		idx_parentId_name.remove(env, k, o);
	}

	public static SDMSSchedule getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSchedule) table.get(env, id);
	}

	public static SDMSSchedule getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSchedule) table.getForUpdate(env, id);
	}

	public static SDMSSchedule getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSSchedule) table.get(env, id, version);
	}

	public static SDMSSchedule idx_parentId_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSchedule)  SDMSScheduleTableGeneric.idx_parentId_name.getUnique(env, key);
	}

	public static SDMSSchedule idx_parentId_name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSchedule)  SDMSScheduleTableGeneric.idx_parentId_name.getUniqueForUpdate(env, key);
	}

	public static SDMSSchedule idx_parentId_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSSchedule)  SDMSScheduleTableGeneric.idx_parentId_name.getUnique(env, key, version);
	}

	public static Long pathToId(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		return getSchedule(sysEnv, path).getId(sysEnv);
	}

	public static Long pathToIdForUpdate(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		return getScheduleForUpdate(sysEnv, path).getId(sysEnv);
	}

	public static SDMSSchedule getSchedule(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSSchedule f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSSchedule) (SDMSScheduleTable.idx_parentId_name.getUnique(sysEnv, new SDMSKey(parentId, s)));
			parentId = f.getId(sysEnv);
		}
		return f;
	}

	public static SDMSSchedule getScheduleForUpdate(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSSchedule f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSSchedule) (SDMSScheduleTable.idx_parentId_name.getUniqueForUpdate(sysEnv, new SDMSKey(parentId, s)));
			parentId = f.getId(sysEnv);
		}
		return f;
	}

	public static Long pathToId(SystemEnvironment sysEnv, Vector path, long version)
	throws SDMSException
	{
		return getSchedule(sysEnv, path, version).getId(sysEnv);
	}

	public static SDMSSchedule getSchedule(SystemEnvironment sysEnv, Vector path, long version)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSSchedule f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSSchedule) (SDMSScheduleTable.idx_parentId_name.getUnique(sysEnv, new SDMSKey(parentId, s), version));
			parentId = f.getId(sysEnv);
		}
		return f;
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
