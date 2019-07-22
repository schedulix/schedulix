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

public class SDMSResourceReqStatesTableGeneric extends SDMSTable
{

	public final static String tableName = "RESOURCE_REQ_STATES";
	public static SDMSResourceReqStatesTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "RR_ID"
		, "RSD_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_rrId;
	public static SDMSIndex idx_rsdId;
	public static SDMSIndex idx_rr_rsd_pk;

	public SDMSResourceReqStatesTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "ResourceReqStates"));
		}
		table = (SDMSResourceReqStatesTable) this;
		SDMSResourceReqStatesTableGeneric.table = (SDMSResourceReqStatesTable) this;
		isVersioned = true;
		idx_rrId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rrId");
		idx_rsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rsdId");
		idx_rr_rsd_pk = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "rr_rsd_pk");
	}
	public SDMSResourceReqStates create(SystemEnvironment env
	                                    ,Long p_rrId
	                                    ,Long p_rsdId
	                                   )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "ResourceReqStates"));
		}
		validate(env
		         , p_rrId
		         , p_rsdId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceReqStatesGeneric o = new SDMSResourceReqStatesGeneric(env
		                , p_rrId
		                , p_rsdId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                 );

		SDMSResourceReqStates p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResourceReqStates)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResourceReqStates)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResourceReqStates p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_rrId
	                        ,Long p_rsdId
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
		Long rrId;
		Long rsdId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			rrId = new Long (r.getLong(2));
			rsdId = new Long (r.getLong(3));
			creatorUId = new Long (r.getLong(4));
			createTs = new Long (r.getLong(5));
			changerUId = new Long (r.getLong(6));
			changeTs = new Long (r.getLong(7));
			validFrom = r.getLong(8);
			validTo = r.getLong(9);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "ResourceReqStates: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceReqStatesGeneric(id,
		                                        rrId,
		                                        rsdId,
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
		                                   ", " + squote + "RR_ID" + equote +
		                                   ", " + squote + "RSD_ID" + equote +
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
		ok =  idx_rrId.check(((SDMSResourceReqStatesGeneric) o).rrId, o);
		out = out + "idx_rrId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_rsdId.check(((SDMSResourceReqStatesGeneric) o).rsdId, o);
		out = out + "idx_rsdId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceReqStatesGeneric) o).rrId);
		k.add(((SDMSResourceReqStatesGeneric) o).rsdId);
		ok =  idx_rr_rsd_pk.check(k, o);
		out = out + "idx_rr_rsd_pk: " + (ok ? "ok" : "missing") + "\n";
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
		idx_rrId.put(env, ((SDMSResourceReqStatesGeneric) o).rrId, o, ((1 & indexMember) != 0));
		idx_rsdId.put(env, ((SDMSResourceReqStatesGeneric) o).rsdId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceReqStatesGeneric) o).rrId);
		k.add(((SDMSResourceReqStatesGeneric) o).rsdId);
		idx_rr_rsd_pk.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_rrId.remove(env, ((SDMSResourceReqStatesGeneric) o).rrId, o);
		idx_rsdId.remove(env, ((SDMSResourceReqStatesGeneric) o).rsdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceReqStatesGeneric) o).rrId);
		k.add(((SDMSResourceReqStatesGeneric) o).rsdId);
		idx_rr_rsd_pk.remove(env, k, o);
	}

	public static SDMSResourceReqStates getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceReqStates) table.get(env, id);
	}

	public static SDMSResourceReqStates getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceReqStates) table.getForUpdate(env, id);
	}

	public static SDMSResourceReqStates getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResourceReqStates) table.get(env, id, version);
	}

	public static SDMSResourceReqStates idx_rr_rsd_pk_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceReqStates)  SDMSResourceReqStatesTableGeneric.idx_rr_rsd_pk.getUnique(env, key);
	}

	public static SDMSResourceReqStates idx_rr_rsd_pk_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceReqStates)  SDMSResourceReqStatesTableGeneric.idx_rr_rsd_pk.getUniqueForUpdate(env, key);
	}

	public static SDMSResourceReqStates idx_rr_rsd_pk_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResourceReqStates)  SDMSResourceReqStatesTableGeneric.idx_rr_rsd_pk.getUnique(env, key, version);
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
