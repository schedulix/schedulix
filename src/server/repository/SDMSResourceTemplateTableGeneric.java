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

public class SDMSResourceTemplateTableGeneric extends SDMSTable
{

	public final static String tableName = "RESOURCE_TEMPLATE";
	public static SDMSResourceTemplateTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NR_ID"
		, "SE_ID"
		, "OWNER_ID"
		, "RSD_ID"
		, "REQUESTABLE_AMOUNT"
		, "AMOUNT"
		, "IS_ONLINE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_nrId;
	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_rsdId;
	public static SDMSIndex idx_nrId_seId;

	public SDMSResourceTemplateTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "ResourceTemplate"));
		}
		table = (SDMSResourceTemplateTable) this;
		SDMSResourceTemplateTableGeneric.table = (SDMSResourceTemplateTable) this;
		isVersioned = true;
		idx_nrId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "nrId");
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seId");
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_rsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rsdId");
		idx_nrId_seId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "nrId_seId");
	}
	public SDMSResourceTemplate create(SystemEnvironment env
	                                   ,Long p_nrId
	                                   ,Long p_seId
	                                   ,Long p_ownerId
	                                   ,Long p_rsdId
	                                   ,Integer p_RequestableAmount
	                                   ,Integer p_amount
	                                   ,Boolean p_isOnline
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "ResourceTemplate"));
		}
		validate(env
		         , p_nrId
		         , p_seId
		         , p_ownerId
		         , p_rsdId
		         , p_RequestableAmount
		         , p_amount
		         , p_isOnline
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceTemplateGeneric o = new SDMSResourceTemplateGeneric(env
		                , p_nrId
		                , p_seId
		                , p_ownerId
		                , p_rsdId
		                , p_RequestableAmount
		                , p_amount
		                , p_isOnline
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSResourceTemplate p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResourceTemplate)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResourceTemplate)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResourceTemplate p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_nrId
	                        ,Long p_seId
	                        ,Long p_ownerId
	                        ,Long p_rsdId
	                        ,Integer p_RequestableAmount
	                        ,Integer p_amount
	                        ,Boolean p_isOnline
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
		Long nrId;
		Long seId;
		Long ownerId;
		Long rsdId;
		Integer RequestableAmount;
		Integer amount;
		Boolean isOnline;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			nrId = new Long (r.getLong(2));
			seId = new Long (r.getLong(3));
			ownerId = new Long (r.getLong(4));
			rsdId = new Long (r.getLong(5));
			if (r.wasNull()) rsdId = null;
			RequestableAmount = new Integer (r.getInt(6));
			if (r.wasNull()) RequestableAmount = null;
			amount = new Integer (r.getInt(7));
			if (r.wasNull()) amount = null;
			isOnline = new Boolean ((r.getInt(8) == 0 ? false : true));
			creatorUId = new Long (r.getLong(9));
			createTs = new Long (r.getLong(10));
			changerUId = new Long (r.getLong(11));
			changeTs = new Long (r.getLong(12));
			validFrom = r.getLong(13);
			validTo = r.getLong(14);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "ResourceTemplate: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceTemplateGeneric(id,
		                                       nrId,
		                                       seId,
		                                       ownerId,
		                                       rsdId,
		                                       RequestableAmount,
		                                       amount,
		                                       isOnline,
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
		                                   ", " + squote + "NR_ID" + equote +
		                                   ", " + squote + "SE_ID" + equote +
		                                   ", " + squote + "OWNER_ID" + equote +
		                                   ", " + squote + "RSD_ID" + equote +
		                                   ", " + squote + "REQUESTABLE_AMOUNT" + equote +
		                                   ", " + squote + "AMOUNT" + equote +
		                                   ", " + squote + "IS_ONLINE" + equote +
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
		ok =  idx_nrId.check(((SDMSResourceTemplateGeneric) o).nrId, o);
		out = out + "idx_nrId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_seId.check(((SDMSResourceTemplateGeneric) o).seId, o);
		out = out + "idx_seId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_ownerId.check(((SDMSResourceTemplateGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_rsdId.check(((SDMSResourceTemplateGeneric) o).rsdId, o);
		out = out + "idx_rsdId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceTemplateGeneric) o).nrId);
		k.add(((SDMSResourceTemplateGeneric) o).seId);
		ok =  idx_nrId_seId.check(k, o);
		out = out + "idx_nrId_seId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_nrId.put(env, ((SDMSResourceTemplateGeneric) o).nrId, o, ((1 & indexMember) != 0));
		idx_seId.put(env, ((SDMSResourceTemplateGeneric) o).seId, o, ((2 & indexMember) != 0));
		idx_ownerId.put(env, ((SDMSResourceTemplateGeneric) o).ownerId, o, ((4 & indexMember) != 0));
		idx_rsdId.put(env, ((SDMSResourceTemplateGeneric) o).rsdId, o, ((8 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceTemplateGeneric) o).nrId);
		k.add(((SDMSResourceTemplateGeneric) o).seId);
		idx_nrId_seId.put(env, k, o, ((16 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_nrId.remove(env, ((SDMSResourceTemplateGeneric) o).nrId, o);
		idx_seId.remove(env, ((SDMSResourceTemplateGeneric) o).seId, o);
		idx_ownerId.remove(env, ((SDMSResourceTemplateGeneric) o).ownerId, o);
		idx_rsdId.remove(env, ((SDMSResourceTemplateGeneric) o).rsdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceTemplateGeneric) o).nrId);
		k.add(((SDMSResourceTemplateGeneric) o).seId);
		idx_nrId_seId.remove(env, k, o);
	}

	public static SDMSResourceTemplate getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceTemplate) table.get(env, id);
	}

	public static SDMSResourceTemplate getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceTemplate) table.getForUpdate(env, id);
	}

	public static SDMSResourceTemplate getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResourceTemplate) table.get(env, id, version);
	}

	public static SDMSResourceTemplate idx_nrId_seId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceTemplate)  SDMSResourceTemplateTableGeneric.idx_nrId_seId.getUnique(env, key);
	}

	public static SDMSResourceTemplate idx_nrId_seId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceTemplate)  SDMSResourceTemplateTableGeneric.idx_nrId_seId.getUniqueForUpdate(env, key);
	}

	public static SDMSResourceTemplate idx_nrId_seId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResourceTemplate)  SDMSResourceTemplateTableGeneric.idx_nrId_seId.getUnique(env, key, version);
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
