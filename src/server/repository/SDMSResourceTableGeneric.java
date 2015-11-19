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

public class SDMSResourceTableGeneric extends SDMSTable
{

	public final static String tableName = "RESSOURCE";
	public static SDMSResourceTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NR_ID"
		, "SCOPE_ID"
		, "MASTER_ID"
		, "OWNER_ID"
		, "LINK_ID"
		, "MANAGER_ID"
		, "TAG"
		, "RSD_ID"
		, "RSD_TIME"
		, "DEFINED_AMOUNT"
		, "REQUESTABLE_AMOUNT"
		, "AMOUNT"
		, "FREE_AMOUNT"
		, "IS_ONLINE"
		, "FACTOR"
		, "TRACE_INTERVAL"
		, "TRACE_BASE"
		, "TRACE_BASE_MULTIPLIER"
		, "TD0_AVG"
		, "TD1_AVG"
		, "TD2_AVG"
		, "LW_AVG"
		, "LAST_EVAL"
		, "LAST_WRITE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_nrId;
	public static SDMSIndex idx_scopeId;
	public static SDMSIndex idx_masterId;
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_linkId;
	public static SDMSIndex idx_managerId;
	public static SDMSIndex idx_tag;
	public static SDMSIndex idx_rsdId;
	public static SDMSIndex idx_nrId_scopeId;

	public SDMSResourceTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "Resource"));
		}
		table = (SDMSResourceTable) this;
		SDMSResourceTableGeneric.table = (SDMSResourceTable) this;
		isVersioned = false;
		idx_nrId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "nrId");
		idx_scopeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "scopeId");
		idx_masterId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "masterId");
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_linkId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "linkId");
		idx_managerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "managerId");
		idx_tag = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "tag");
		idx_rsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rsdId");
		idx_nrId_scopeId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "nrId_scopeId");
	}
	public SDMSResource create(SystemEnvironment env
	                           ,Long p_nrId
	                           ,Long p_scopeId
	                           ,Long p_masterId
	                           ,Long p_ownerId
	                           ,Long p_linkId
	                           ,Long p_managerId
	                           ,String p_tag
	                           ,Long p_rsdId
	                           ,Long p_rsdTime
	                           ,Integer p_definedAmount
	                           ,Integer p_requestableAmount
	                           ,Integer p_amount
	                           ,Integer p_freeAmount
	                           ,Boolean p_isOnline
	                           ,Float p_factor
	                           ,Integer p_traceInterval
	                           ,Integer p_traceBase
	                           ,Integer p_traceBaseMultiplier
	                           ,Float p_td0Avg
	                           ,Float p_td1Avg
	                           ,Float p_td2Avg
	                           ,Float p_lwAvg
	                           ,Long p_lastEval
	                           ,Long p_lastWrite
	                          )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "Resource"));
		}
		validate(env
		         , p_nrId
		         , p_scopeId
		         , p_masterId
		         , p_ownerId
		         , p_linkId
		         , p_managerId
		         , p_tag
		         , p_rsdId
		         , p_rsdTime
		         , p_definedAmount
		         , p_requestableAmount
		         , p_amount
		         , p_freeAmount
		         , p_isOnline
		         , p_factor
		         , p_traceInterval
		         , p_traceBase
		         , p_traceBaseMultiplier
		         , p_td0Avg
		         , p_td1Avg
		         , p_td2Avg
		         , p_lwAvg
		         , p_lastEval
		         , p_lastWrite
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceGeneric o = new SDMSResourceGeneric(env
		                , p_nrId
		                , p_scopeId
		                , p_masterId
		                , p_ownerId
		                , p_linkId
		                , p_managerId
		                , p_tag
		                , p_rsdId
		                , p_rsdTime
		                , p_definedAmount
		                , p_requestableAmount
		                , p_amount
		                , p_freeAmount
		                , p_isOnline
		                , p_factor
		                , p_traceInterval
		                , p_traceBase
		                , p_traceBaseMultiplier
		                , p_td0Avg
		                , p_td1Avg
		                , p_td2Avg
		                , p_lwAvg
		                , p_lastEval
		                , p_lastWrite
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                               );

		SDMSResource p;
		try {

			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResource)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResource)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResource p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_nrId
	                        ,Long p_scopeId
	                        ,Long p_masterId
	                        ,Long p_ownerId
	                        ,Long p_linkId
	                        ,Long p_managerId
	                        ,String p_tag
	                        ,Long p_rsdId
	                        ,Long p_rsdTime
	                        ,Integer p_definedAmount
	                        ,Integer p_requestableAmount
	                        ,Integer p_amount
	                        ,Integer p_freeAmount
	                        ,Boolean p_isOnline
	                        ,Float p_factor
	                        ,Integer p_traceInterval
	                        ,Integer p_traceBase
	                        ,Integer p_traceBaseMultiplier
	                        ,Float p_td0Avg
	                        ,Float p_td1Avg
	                        ,Float p_td2Avg
	                        ,Float p_lwAvg
	                        ,Long p_lastEval
	                        ,Long p_lastWrite
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
		Long scopeId;
		Long masterId;
		Long ownerId;
		Long linkId;
		Long managerId;
		String tag;
		Long rsdId;
		Long rsdTime;
		Integer definedAmount;
		Integer requestableAmount;
		Integer amount;
		Integer freeAmount;
		Boolean isOnline;
		Float factor;
		Integer traceInterval;
		Integer traceBase;
		Integer traceBaseMultiplier;
		Float td0Avg;
		Float td1Avg;
		Float td2Avg;
		Float lwAvg;
		Long lastEval;
		Long lastWrite;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			nrId = new Long (r.getLong(2));
			scopeId = new Long (r.getLong(3));
			if (r.wasNull()) scopeId = null;
			masterId = new Long (r.getLong(4));
			if (r.wasNull()) masterId = null;
			ownerId = new Long (r.getLong(5));
			linkId = new Long (r.getLong(6));
			if (r.wasNull()) linkId = null;
			managerId = new Long (r.getLong(7));
			if (r.wasNull()) managerId = null;
			tag = r.getString(8);
			if (r.wasNull()) tag = null;
			rsdId = new Long (r.getLong(9));
			if (r.wasNull()) rsdId = null;
			rsdTime = new Long (r.getLong(10));
			if (r.wasNull()) rsdTime = null;
			definedAmount = new Integer (r.getInt(11));
			if (r.wasNull()) definedAmount = null;
			requestableAmount = new Integer (r.getInt(12));
			if (r.wasNull()) requestableAmount = null;
			amount = new Integer (r.getInt(13));
			if (r.wasNull()) amount = null;
			freeAmount = new Integer (r.getInt(14));
			if (r.wasNull()) freeAmount = null;
			isOnline = new Boolean ((r.getInt(15) == 0 ? false : true));
			if (r.wasNull()) isOnline = null;
			factor = new Float (r.getFloat(16));
			if (r.wasNull()) factor = null;
			traceInterval = new Integer (r.getInt(17));
			if (r.wasNull()) traceInterval = null;
			traceBase = new Integer (r.getInt(18));
			if (r.wasNull()) traceBase = null;
			traceBaseMultiplier = new Integer (r.getInt(19));
			td0Avg = new Float (r.getFloat(20));
			td1Avg = new Float (r.getFloat(21));
			td2Avg = new Float (r.getFloat(22));
			lwAvg = new Float (r.getFloat(23));
			lastEval = new Long (r.getLong(24));
			lastWrite = new Long (r.getLong(25));
			creatorUId = new Long (r.getLong(26));
			createTs = new Long (r.getLong(27));
			changerUId = new Long (r.getLong(28));
			changeTs = new Long (r.getLong(29));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "Resource: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceGeneric(id,
		                               nrId,
		                               scopeId,
		                               masterId,
		                               ownerId,
		                               linkId,
		                               managerId,
		                               tag,
		                               rsdId,
		                               rsdTime,
		                               definedAmount,
		                               requestableAmount,
		                               amount,
		                               freeAmount,
		                               isOnline,
		                               factor,
		                               traceInterval,
		                               traceBase,
		                               traceBaseMultiplier,
		                               td0Avg,
		                               td1Avg,
		                               td2Avg,
		                               lwAvg,
		                               lastEval,
		                               lastWrite,
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
		                                   ", " + squote + "NR_ID" + equote +
		                                   ", " + squote + "SCOPE_ID" + equote +
		                                   ", " + squote + "MASTER_ID" + equote +
		                                   ", " + squote + "OWNER_ID" + equote +
		                                   ", " + squote + "LINK_ID" + equote +
		                                   ", " + squote + "MANAGER_ID" + equote +
		                                   ", " + squote + "TAG" + equote +
		                                   ", " + squote + "RSD_ID" + equote +
		                                   ", " + squote + "RSD_TIME" + equote +
		                                   ", " + squote + "DEFINED_AMOUNT" + equote +
		                                   ", " + squote + "REQUESTABLE_AMOUNT" + equote +
		                                   ", " + squote + "AMOUNT" + equote +
		                                   ", " + squote + "FREE_AMOUNT" + equote +
		                                   ", " + squote + "IS_ONLINE" + equote +
		                                   ", " + squote + "FACTOR" + equote +
		                                   ", " + squote + "TRACE_INTERVAL" + equote +
		                                   ", " + squote + "TRACE_BASE" + equote +
		                                   ", " + squote + "TRACE_BASE_MULTIPLIER" + equote +
		                                   ", " + squote + "TD0_AVG" + equote +
		                                   ", " + squote + "TD1_AVG" + equote +
		                                   ", " + squote + "TD2_AVG" + equote +
		                                   ", " + squote + "LW_AVG" + equote +
		                                   ", " + squote + "LAST_EVAL" + equote +
		                                   ", " + squote + "LAST_WRITE" + equote +
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
		ok =  idx_nrId.check(((SDMSResourceGeneric) o).nrId, o);
		out = out + "idx_nrId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_scopeId.check(((SDMSResourceGeneric) o).scopeId, o);
		out = out + "idx_scopeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_masterId.check(((SDMSResourceGeneric) o).masterId, o);
		out = out + "idx_masterId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_ownerId.check(((SDMSResourceGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_linkId.check(((SDMSResourceGeneric) o).linkId, o);
		out = out + "idx_linkId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_managerId.check(((SDMSResourceGeneric) o).managerId, o);
		out = out + "idx_managerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_tag.check(((SDMSResourceGeneric) o).tag, o);
		out = out + "idx_tag: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_rsdId.check(((SDMSResourceGeneric) o).rsdId, o);
		out = out + "idx_rsdId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceGeneric) o).nrId);
		k.add(((SDMSResourceGeneric) o).scopeId);
		ok =  idx_nrId_scopeId.check(k, o);
		out = out + "idx_nrId_scopeId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_nrId.put(env, ((SDMSResourceGeneric) o).nrId, o, ((1 & indexMember) != 0));
		idx_scopeId.put(env, ((SDMSResourceGeneric) o).scopeId, o, ((2 & indexMember) != 0));
		idx_masterId.put(env, ((SDMSResourceGeneric) o).masterId, o, ((4 & indexMember) != 0));
		idx_ownerId.put(env, ((SDMSResourceGeneric) o).ownerId, o, ((8 & indexMember) != 0));
		idx_linkId.put(env, ((SDMSResourceGeneric) o).linkId, o, ((16 & indexMember) != 0));
		idx_managerId.put(env, ((SDMSResourceGeneric) o).managerId, o, ((32 & indexMember) != 0));
		idx_tag.put(env, ((SDMSResourceGeneric) o).tag, o, ((64 & indexMember) != 0));
		idx_rsdId.put(env, ((SDMSResourceGeneric) o).rsdId, o, ((128 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceGeneric) o).nrId);
		k.add(((SDMSResourceGeneric) o).scopeId);
		idx_nrId_scopeId.put(env, k, o, ((256 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_nrId.remove(env, ((SDMSResourceGeneric) o).nrId, o);
		idx_scopeId.remove(env, ((SDMSResourceGeneric) o).scopeId, o);
		idx_masterId.remove(env, ((SDMSResourceGeneric) o).masterId, o);
		idx_ownerId.remove(env, ((SDMSResourceGeneric) o).ownerId, o);
		idx_linkId.remove(env, ((SDMSResourceGeneric) o).linkId, o);
		idx_managerId.remove(env, ((SDMSResourceGeneric) o).managerId, o);
		idx_tag.remove(env, ((SDMSResourceGeneric) o).tag, o);
		idx_rsdId.remove(env, ((SDMSResourceGeneric) o).rsdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceGeneric) o).nrId);
		k.add(((SDMSResourceGeneric) o).scopeId);
		idx_nrId_scopeId.remove(env, k, o);
	}

	public static SDMSResource getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResource) table.get(env, id);
	}

	public static SDMSResource getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResource) table.getForUpdate(env, id);
	}

	public static SDMSResource getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResource) table.get(env, id, version);
	}

	public static SDMSResource idx_nrId_scopeId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResource)  SDMSResourceTableGeneric.idx_nrId_scopeId.getUnique(env, key);
	}

	public static SDMSResource idx_nrId_scopeId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResource)  SDMSResourceTableGeneric.idx_nrId_scopeId.getUniqueForUpdate(env, key);
	}

	public static SDMSResource idx_nrId_scopeId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResource)  SDMSResourceTableGeneric.idx_nrId_scopeId.getUnique(env, key, version);
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
