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

public class SDMSResourceStateProfileTableGeneric extends SDMSTable
{

	public final static String __version = "SDMSResourceStateProfileTableGeneric $Revision: 2.4 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "RESOURCE_STATE_PROFILE";
	public static SDMSResourceStateProfileTable table  = null;

	public static SDMSIndex idx_name;
	public static SDMSIndex idx_initialRsdId;

	public SDMSResourceStateProfileTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ResourceStateProfile"));
		}
		table = (SDMSResourceStateProfileTable) this;
		SDMSResourceStateProfileTableGeneric.table = (SDMSResourceStateProfileTable) this;
		isVersioned = false;
		idx_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
		idx_initialRsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
	}
	public SDMSResourceStateProfile create(SystemEnvironment env
	                                       ,String p_name
	                                       ,Long p_initialRsdId
	                                      )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ResourceStateProfile"));
		}
		validate(env
		         , p_name
		         , p_initialRsdId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceStateProfileGeneric o = new SDMSResourceStateProfileGeneric(env
		                , p_name
		                , p_initialRsdId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                       );

		SDMSResourceStateProfile p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResourceStateProfile)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResourceStateProfile)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResourceStateProfile p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_initialRsdId
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
		String name;
		Long initialRsdId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			initialRsdId = new Long (r.getLong(3));
			if (r.wasNull()) initialRsdId = null;
			creatorUId = new Long (r.getLong(4));
			createTs = new Long (r.getLong(5));
			changerUId = new Long (r.getLong(6));
			changeTs = new Long (r.getLong(7));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ResourceStateProfile: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceStateProfileGeneric(id,
		                name,
		                initialRsdId,
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
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "INITIAL_RSD_ID" + equote +
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
		idx_name.put(env, ((SDMSResourceStateProfileGeneric) o).name, o);
		idx_initialRsdId.put(env, ((SDMSResourceStateProfileGeneric) o).initialRsdId, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_name.remove(env, ((SDMSResourceStateProfileGeneric) o).name, o);
		idx_initialRsdId.remove(env, ((SDMSResourceStateProfileGeneric) o).initialRsdId, o);
	}

	public static SDMSResourceStateProfile getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceStateProfile) table.get(env, id);
	}

	public static SDMSResourceStateProfile getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResourceStateProfile) table.get(env, id, version);
	}

	public static SDMSResourceStateProfile idx_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceStateProfile) SDMSResourceStateProfileTableGeneric.idx_name.getUnique(env, key);
	}

	public static SDMSResourceStateProfile idx_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResourceStateProfile) SDMSResourceStateProfileTableGeneric.idx_name.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
