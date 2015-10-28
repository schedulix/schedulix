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

public class SDMSResourceStateMappingTableGeneric extends SDMSTable
{

	public final static String tableName = "RESOURCE_STATE_MAPPING";
	public static SDMSResourceStateMappingTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "RSMP_ID"
		, "ESD_ID"
		, "FROM_RSD_ID"
		, "TO_RSD_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_rsmpId;
	public static SDMSIndex idx_esdId;
	public static SDMSIndex idx_fromRsdId;
	public static SDMSIndex idx_toRsdId;
	public static SDMSIndex idx_rsmpId__esdId_fromRsdId;

	public SDMSResourceStateMappingTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ResourceStateMapping"));
		}
		table = (SDMSResourceStateMappingTable) this;
		SDMSResourceStateMappingTableGeneric.table = (SDMSResourceStateMappingTable) this;
		isVersioned = true;
		idx_rsmpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rsmpId");
		idx_esdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "esdId");
		idx_fromRsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "fromRsdId");
		idx_toRsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "toRsdId");
		idx_rsmpId__esdId_fromRsdId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "rsmpId__esdId_fromRsdId");
	}
	public SDMSResourceStateMapping create(SystemEnvironment env
	                                       ,Long p_rsmpId
	                                       ,Long p_esdId
	                                       ,Long p_fromRsdId
	                                       ,Long p_toRsdId
	                                      )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ResourceStateMapping"));
		}
		validate(env
		         , p_rsmpId
		         , p_esdId
		         , p_fromRsdId
		         , p_toRsdId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceStateMappingGeneric o = new SDMSResourceStateMappingGeneric(env
		                , p_rsmpId
		                , p_esdId
		                , p_fromRsdId
		                , p_toRsdId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                       );

		SDMSResourceStateMapping p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResourceStateMapping)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResourceStateMapping)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResourceStateMapping p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_rsmpId
	                        ,Long p_esdId
	                        ,Long p_fromRsdId
	                        ,Long p_toRsdId
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
		Long rsmpId;
		Long esdId;
		Long fromRsdId;
		Long toRsdId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			rsmpId = new Long (r.getLong(2));
			esdId = new Long (r.getLong(3));
			fromRsdId = new Long (r.getLong(4));
			if (r.wasNull()) fromRsdId = null;
			toRsdId = new Long (r.getLong(5));
			creatorUId = new Long (r.getLong(6));
			createTs = new Long (r.getLong(7));
			changerUId = new Long (r.getLong(8));
			changeTs = new Long (r.getLong(9));
			validFrom = r.getLong(10);
			validTo = r.getLong(11);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ResourceStateMapping: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceStateMappingGeneric(id,
		                rsmpId,
		                esdId,
		                fromRsdId,
		                toRsdId,
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
		                                   ", " + squote + "RSMP_ID" + equote +
		                                   ", " + squote + "ESD_ID" + equote +
		                                   ", " + squote + "FROM_RSD_ID" + equote +
		                                   ", " + squote + "TO_RSD_ID" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", VALID_FROM, VALID_TO " +
		                                   " FROM " + tableName() +
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
		ok =  idx_rsmpId.check(((SDMSResourceStateMappingGeneric) o).rsmpId, o);
		out = out + "idx_rsmpId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_esdId.check(((SDMSResourceStateMappingGeneric) o).esdId, o);
		out = out + "idx_esdId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_fromRsdId.check(((SDMSResourceStateMappingGeneric) o).fromRsdId, o);
		out = out + "idx_fromRsdId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_toRsdId.check(((SDMSResourceStateMappingGeneric) o).toRsdId, o);
		out = out + "idx_toRsdId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceStateMappingGeneric) o).rsmpId);
		k.add(((SDMSResourceStateMappingGeneric) o).esdId);
		k.add(((SDMSResourceStateMappingGeneric) o).fromRsdId);
		ok =  idx_rsmpId__esdId_fromRsdId.check(k, o);
		out = out + "idx_rsmpId__esdId_fromRsdId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_rsmpId.put(env, ((SDMSResourceStateMappingGeneric) o).rsmpId, o, ((1 & indexMember) != 0));
		idx_esdId.put(env, ((SDMSResourceStateMappingGeneric) o).esdId, o, ((2 & indexMember) != 0));
		idx_fromRsdId.put(env, ((SDMSResourceStateMappingGeneric) o).fromRsdId, o, ((4 & indexMember) != 0));
		idx_toRsdId.put(env, ((SDMSResourceStateMappingGeneric) o).toRsdId, o, ((8 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceStateMappingGeneric) o).rsmpId);
		k.add(((SDMSResourceStateMappingGeneric) o).esdId);
		k.add(((SDMSResourceStateMappingGeneric) o).fromRsdId);
		idx_rsmpId__esdId_fromRsdId.put(env, k, o, ((16 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_rsmpId.remove(env, ((SDMSResourceStateMappingGeneric) o).rsmpId, o);
		idx_esdId.remove(env, ((SDMSResourceStateMappingGeneric) o).esdId, o);
		idx_fromRsdId.remove(env, ((SDMSResourceStateMappingGeneric) o).fromRsdId, o);
		idx_toRsdId.remove(env, ((SDMSResourceStateMappingGeneric) o).toRsdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceStateMappingGeneric) o).rsmpId);
		k.add(((SDMSResourceStateMappingGeneric) o).esdId);
		k.add(((SDMSResourceStateMappingGeneric) o).fromRsdId);
		idx_rsmpId__esdId_fromRsdId.remove(env, k, o);
	}

	public static SDMSResourceStateMapping getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceStateMapping) table.get(env, id);
	}

	public static SDMSResourceStateMapping getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResourceStateMapping) table.get(env, id, version);
	}

	public static SDMSResourceStateMapping idx_rsmpId__esdId_fromRsdId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceStateMapping)  SDMSResourceStateMappingTableGeneric.idx_rsmpId__esdId_fromRsdId.getUnique(env, key);
	}

	public static SDMSResourceStateMapping idx_rsmpId__esdId_fromRsdId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResourceStateMapping)  SDMSResourceStateMappingTableGeneric.idx_rsmpId__esdId_fromRsdId.getUnique(env, key, version);
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
