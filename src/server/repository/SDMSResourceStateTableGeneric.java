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

public class SDMSResourceStateTableGeneric extends SDMSTable
{

	public final static String tableName = "RESOURCE_STATE";
	public static SDMSResourceStateTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "RSD_ID"
		, "RSP_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_rsdId;
	public static SDMSIndex idx_rspId;
	public static SDMSIndex idx_rsdId_rspId;

	public SDMSResourceStateTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ResourceState"));
		}
		table = (SDMSResourceStateTable) this;
		SDMSResourceStateTableGeneric.table = (SDMSResourceStateTable) this;
		isVersioned = false;
		idx_rsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_rspId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_rsdId_rspId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSResourceState create(SystemEnvironment env
	                                ,Long p_rsdId
	                                ,Long p_rspId
	                               )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ResourceState"));
		}
		validate(env
		         , p_rsdId
		         , p_rspId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceStateGeneric o = new SDMSResourceStateGeneric(env
		                , p_rsdId
		                , p_rspId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                         );

		SDMSResourceState p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResourceState)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResourceState)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResourceState p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_rsdId
	                        ,Long p_rspId
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
		Long rsdId;
		Long rspId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			rsdId = new Long (r.getLong(2));
			rspId = new Long (r.getLong(3));
			creatorUId = new Long (r.getLong(4));
			createTs = new Long (r.getLong(5));
			changerUId = new Long (r.getLong(6));
			changeTs = new Long (r.getLong(7));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ResourceState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceStateGeneric(id,
		                                    rsdId,
		                                    rspId,
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
		if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
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
		                                   ", " + squote + "RSD_ID" + equote +
		                                   ", " + squote + "RSP_ID" + equote +
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
		idx_rsdId.put(env, ((SDMSResourceStateGeneric) o).rsdId, o);
		idx_rspId.put(env, ((SDMSResourceStateGeneric) o).rspId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceStateGeneric) o).rsdId);
		k.add(((SDMSResourceStateGeneric) o).rspId);
		idx_rsdId_rspId.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_rsdId.remove(env, ((SDMSResourceStateGeneric) o).rsdId, o);
		idx_rspId.remove(env, ((SDMSResourceStateGeneric) o).rspId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceStateGeneric) o).rsdId);
		k.add(((SDMSResourceStateGeneric) o).rspId);
		idx_rsdId_rspId.remove(env, k, o);
	}

	public static SDMSResourceState getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceState) table.get(env, id);
	}

	public static SDMSResourceState getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResourceState) table.get(env, id, version);
	}

	public static SDMSResourceState idx_rsdId_rspId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceState)  SDMSResourceStateTableGeneric.idx_rsdId_rspId.getUnique(env, key);
	}

	public static SDMSResourceState idx_rsdId_rspId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResourceState)  SDMSResourceStateTableGeneric.idx_rsdId_rspId.getUnique(env, key, version);
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
