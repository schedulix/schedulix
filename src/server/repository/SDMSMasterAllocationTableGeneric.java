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

public class SDMSMasterAllocationTableGeneric extends SDMSTable
{

	public final static String tableName = "MASTER_ALLOCATION";
	public static SDMSMasterAllocationTable table  = null;

	public static SDMSIndex idx_raId;
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_raId_stickyParent_stickyName;

	public SDMSMasterAllocationTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "MasterAllocation"));
		}
		table = (SDMSMasterAllocationTable) this;
		SDMSMasterAllocationTableGeneric.table = (SDMSMasterAllocationTable) this;
		isVersioned = false;
		idx_raId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_raId_stickyParent_stickyName = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSMasterAllocation create(SystemEnvironment env
	                                   ,Long p_raId
	                                   ,Long p_smeId
	                                   ,Integer p_amount
	                                   ,String p_stickyName
	                                   ,Long p_stickyParent
	                                   ,Integer p_lockmode
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "MasterAllocation"));
		}
		validate(env
		         , p_raId
		         , p_smeId
		         , p_amount
		         , p_stickyName
		         , p_stickyParent
		         , p_lockmode
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSMasterAllocationGeneric o = new SDMSMasterAllocationGeneric(env
		                , p_raId
		                , p_smeId
		                , p_amount
		                , p_stickyName
		                , p_stickyParent
		                , p_lockmode
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSMasterAllocation p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSMasterAllocation)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSMasterAllocation)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSMasterAllocation p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_raId
	                        ,Long p_smeId
	                        ,Integer p_amount
	                        ,String p_stickyName
	                        ,Long p_stickyParent
	                        ,Integer p_lockmode
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSMasterAllocationGeneric.checkLockmode(p_lockmode))

			throw new FatalException(new SDMSMessage(env, "01110182023", "MasterAllocation: $1 $2", "lockmode", p_lockmode));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long raId;
		Long smeId;
		Integer amount;
		String stickyName;
		Long stickyParent;
		Integer lockmode;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			raId = new Long (r.getLong(2));
			smeId = new Long (r.getLong(3));
			amount = new Integer (r.getInt(4));
			if (r.wasNull()) amount = null;
			stickyName = r.getString(5);
			if (r.wasNull()) stickyName = null;
			stickyParent = new Long (r.getLong(6));
			if (r.wasNull()) stickyParent = null;
			lockmode = new Integer (r.getInt(7));
			if (r.wasNull()) lockmode = null;
			creatorUId = new Long (r.getLong(8));
			createTs = new Long (r.getLong(9));
			changerUId = new Long (r.getLong(10));
			changeTs = new Long (r.getLong(11));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "MasterAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSMasterAllocationGeneric(id,
		                                       raId,
		                                       smeId,
		                                       amount,
		                                       stickyName,
		                                       stickyParent,
		                                       lockmode,
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
		                                   tableName() + ".ID" +
		                                   ", " + squote + "RA_ID" + equote +
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "AMOUNT" + equote +
		                                   ", " + squote + "STICKY_NAME" + equote +
		                                   ", " + squote + "STICKY_PARENT" + equote +
		                                   ", " + squote + "LOCKMODE" + equote +
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
		idx_raId.put(env, ((SDMSMasterAllocationGeneric) o).raId, o);
		idx_smeId.put(env, ((SDMSMasterAllocationGeneric) o).smeId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSMasterAllocationGeneric) o).raId);
		k.add(((SDMSMasterAllocationGeneric) o).stickyParent);
		k.add(((SDMSMasterAllocationGeneric) o).stickyName);
		idx_raId_stickyParent_stickyName.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_raId.remove(env, ((SDMSMasterAllocationGeneric) o).raId, o);
		idx_smeId.remove(env, ((SDMSMasterAllocationGeneric) o).smeId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSMasterAllocationGeneric) o).raId);
		k.add(((SDMSMasterAllocationGeneric) o).stickyParent);
		k.add(((SDMSMasterAllocationGeneric) o).stickyName);
		idx_raId_stickyParent_stickyName.remove(env, k, o);
	}

	public static SDMSMasterAllocation getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSMasterAllocation) table.get(env, id);
	}

	public static SDMSMasterAllocation getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSMasterAllocation) table.get(env, id, version);
	}

	public static SDMSMasterAllocation idx_raId_stickyParent_stickyName_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSMasterAllocation)  SDMSMasterAllocationTableGeneric.idx_raId_stickyParent_stickyName.getUnique(env, key);
	}

	public static SDMSMasterAllocation idx_raId_stickyParent_stickyName_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSMasterAllocation)  SDMSMasterAllocationTableGeneric.idx_raId_stickyParent_stickyName.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
