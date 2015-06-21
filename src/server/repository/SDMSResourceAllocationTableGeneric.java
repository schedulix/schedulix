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

public class SDMSResourceAllocationTableGeneric extends SDMSTable
{

	public final static String tableName = "RESOURCE_ALLOCATION";
	public static SDMSResourceAllocationTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "R_ID"
		, "SME_ID"
		, "NR_ID"
		, "AMOUNT"
		, "ORIG_AMOUNT"
		, "KEEP_MODE"
		, "IS_STICKY"
		, "STICKY_NAME"
		, "STICKY_PARENT"
		, "ALLOCATION_TYPE"
		, "RSMP_ID"
		, "LOCKMODE"
		, "REFCOUNT"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_rId;
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_nrId;
	public static SDMSIndex idx_smeId_rId_stickyName;
	public static SDMSIndex idx_stickyParent_rId_stickyName;
	public static SDMSIndex idx_smeId_nrId;

	public SDMSResourceAllocationTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ResourceAllocation"));
		}
		table = (SDMSResourceAllocationTable) this;
		SDMSResourceAllocationTableGeneric.table = (SDMSResourceAllocationTable) this;
		isVersioned = false;
		idx_rId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rId");
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId");
		idx_nrId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "nrId");
		idx_smeId_rId_stickyName = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "smeId_rId_stickyName");
		idx_stickyParent_rId_stickyName = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "stickyParent_rId_stickyName");
		idx_smeId_nrId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId_nrId");
	}
	public SDMSResourceAllocation create(SystemEnvironment env
	                                     ,Long p_rId
	                                     ,Long p_smeId
	                                     ,Long p_nrId
	                                     ,Integer p_amount
	                                     ,Integer p_origAmount
	                                     ,Integer p_keepMode
	                                     ,Boolean p_isSticky
	                                     ,String p_stickyName
	                                     ,Long p_stickyParent
	                                     ,Integer p_allocationType
	                                     ,Long p_rsmpId
	                                     ,Integer p_lockmode
	                                     ,Integer p_refcount
	                                    )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ResourceAllocation"));
		}
		validate(env
		         , p_rId
		         , p_smeId
		         , p_nrId
		         , p_amount
		         , p_origAmount
		         , p_keepMode
		         , p_isSticky
		         , p_stickyName
		         , p_stickyParent
		         , p_allocationType
		         , p_rsmpId
		         , p_lockmode
		         , p_refcount
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceAllocationGeneric o = new SDMSResourceAllocationGeneric(env
		                , p_rId
		                , p_smeId
		                , p_nrId
		                , p_amount
		                , p_origAmount
		                , p_keepMode
		                , p_isSticky
		                , p_stickyName
		                , p_stickyParent
		                , p_allocationType
		                , p_rsmpId
		                , p_lockmode
		                , p_refcount
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                   );

		SDMSResourceAllocation p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResourceAllocation)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResourceAllocation)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResourceAllocation p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_rId
	                        ,Long p_smeId
	                        ,Long p_nrId
	                        ,Integer p_amount
	                        ,Integer p_origAmount
	                        ,Integer p_keepMode
	                        ,Boolean p_isSticky
	                        ,String p_stickyName
	                        ,Long p_stickyParent
	                        ,Integer p_allocationType
	                        ,Long p_rsmpId
	                        ,Integer p_lockmode
	                        ,Integer p_refcount
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSResourceAllocationGeneric.checkKeepMode(p_keepMode))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ResourceAllocation: $1 $2", "keepMode", p_keepMode));
		if (!SDMSResourceAllocationGeneric.checkAllocationType(p_allocationType))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ResourceAllocation: $1 $2", "allocationType", p_allocationType));
		if (!SDMSResourceAllocationGeneric.checkLockmode(p_lockmode))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ResourceAllocation: $1 $2", "lockmode", p_lockmode));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long rId;
		Long smeId;
		Long nrId;
		Integer amount;
		Integer origAmount;
		Integer keepMode;
		Boolean isSticky;
		String stickyName;
		Long stickyParent;
		Integer allocationType;
		Long rsmpId;
		Integer lockmode;
		Integer refcount;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			rId = new Long (r.getLong(2));
			smeId = new Long (r.getLong(3));
			nrId = new Long (r.getLong(4));
			amount = new Integer (r.getInt(5));
			if (r.wasNull()) amount = null;
			origAmount = new Integer (r.getInt(6));
			if (r.wasNull()) origAmount = null;
			keepMode = new Integer (r.getInt(7));
			isSticky = new Boolean ((r.getInt(8) == 0 ? false : true));
			stickyName = r.getString(9);
			if (r.wasNull()) stickyName = null;
			stickyParent = new Long (r.getLong(10));
			if (r.wasNull()) stickyParent = null;
			allocationType = new Integer (r.getInt(11));
			rsmpId = new Long (r.getLong(12));
			if (r.wasNull()) rsmpId = null;
			lockmode = new Integer (r.getInt(13));
			if (r.wasNull()) lockmode = null;
			refcount = new Integer (r.getInt(14));
			creatorUId = new Long (r.getLong(15));
			createTs = new Long (r.getLong(16));
			changerUId = new Long (r.getLong(17));
			changeTs = new Long (r.getLong(18));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ResourceAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceAllocationGeneric(id,
		                rId,
		                smeId,
		                nrId,
		                amount,
		                origAmount,
		                keepMode,
		                isSticky,
		                stickyName,
		                stickyParent,
		                allocationType,
		                rsmpId,
		                lockmode,
		                refcount,
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
		                                   ", " + squote + "R_ID" + equote +
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "NR_ID" + equote +
		                                   ", " + squote + "AMOUNT" + equote +
		                                   ", " + squote + "ORIG_AMOUNT" + equote +
		                                   ", " + squote + "KEEP_MODE" + equote +
		                                   ", " + squote + "IS_STICKY" + equote +
		                                   ", " + squote + "STICKY_NAME" + equote +
		                                   ", " + squote + "STICKY_PARENT" + equote +
		                                   ", " + squote + "ALLOCATION_TYPE" + equote +
		                                   ", " + squote + "RSMP_ID" + equote +
		                                   ", " + squote + "LOCKMODE" + equote +
		                                   ", " + squote + "REFCOUNT" + equote +
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
		ok =  idx_rId.check(((SDMSResourceAllocationGeneric) o).rId, o);
		out = out + "idx_rId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_smeId.check(((SDMSResourceAllocationGeneric) o).smeId, o);
		out = out + "idx_smeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_nrId.check(((SDMSResourceAllocationGeneric) o).nrId, o);
		out = out + "idx_nrId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).smeId);
		k.add(((SDMSResourceAllocationGeneric) o).rId);
		k.add(((SDMSResourceAllocationGeneric) o).stickyName);
		ok =  idx_smeId_rId_stickyName.check(k, o);
		out = out + "idx_smeId_rId_stickyName: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).stickyParent);
		k.add(((SDMSResourceAllocationGeneric) o).rId);
		k.add(((SDMSResourceAllocationGeneric) o).stickyName);
		ok =  idx_stickyParent_rId_stickyName.check(k, o);
		out = out + "idx_stickyParent_rId_stickyName: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).smeId);
		k.add(((SDMSResourceAllocationGeneric) o).nrId);
		ok =  idx_smeId_nrId.check(k, o);
		out = out + "idx_smeId_nrId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_rId.put(env, ((SDMSResourceAllocationGeneric) o).rId, o, ((1 & indexMember) != 0));
		idx_smeId.put(env, ((SDMSResourceAllocationGeneric) o).smeId, o, ((2 & indexMember) != 0));
		idx_nrId.put(env, ((SDMSResourceAllocationGeneric) o).nrId, o, ((4 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).smeId);
		k.add(((SDMSResourceAllocationGeneric) o).rId);
		k.add(((SDMSResourceAllocationGeneric) o).stickyName);
		idx_smeId_rId_stickyName.put(env, k, o, ((8 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).stickyParent);
		k.add(((SDMSResourceAllocationGeneric) o).rId);
		k.add(((SDMSResourceAllocationGeneric) o).stickyName);
		idx_stickyParent_rId_stickyName.put(env, k, o, ((16 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).smeId);
		k.add(((SDMSResourceAllocationGeneric) o).nrId);
		idx_smeId_nrId.put(env, k, o, ((32 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_rId.remove(env, ((SDMSResourceAllocationGeneric) o).rId, o);
		idx_smeId.remove(env, ((SDMSResourceAllocationGeneric) o).smeId, o);
		idx_nrId.remove(env, ((SDMSResourceAllocationGeneric) o).nrId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).smeId);
		k.add(((SDMSResourceAllocationGeneric) o).rId);
		k.add(((SDMSResourceAllocationGeneric) o).stickyName);
		idx_smeId_rId_stickyName.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).stickyParent);
		k.add(((SDMSResourceAllocationGeneric) o).rId);
		k.add(((SDMSResourceAllocationGeneric) o).stickyName);
		idx_stickyParent_rId_stickyName.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSResourceAllocationGeneric) o).smeId);
		k.add(((SDMSResourceAllocationGeneric) o).nrId);
		idx_smeId_nrId.remove(env, k, o);
	}

	public static SDMSResourceAllocation getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceAllocation) table.get(env, id);
	}

	public static SDMSResourceAllocation getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResourceAllocation) table.get(env, id, version);
	}

	public static SDMSResourceAllocation idx_smeId_rId_stickyName_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceAllocation)  SDMSResourceAllocationTableGeneric.idx_smeId_rId_stickyName.getUnique(env, key);
	}

	public static SDMSResourceAllocation idx_smeId_rId_stickyName_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResourceAllocation)  SDMSResourceAllocationTableGeneric.idx_smeId_rId_stickyName.getUnique(env, key, version);
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
