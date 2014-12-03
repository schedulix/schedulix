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

public class SDMSScopeConfigEnvMappingTableGeneric extends SDMSTable
{

	public final static String tableName = "SCOPE_CONFIG_ENVMAPPING";
	public static SDMSScopeConfigEnvMappingTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "KEY"
		, "VALUE"
		, "S_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_sId;
	public static SDMSIndex idx_scopeId_key;

	public SDMSScopeConfigEnvMappingTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ScopeConfigEnvMapping"));
		}
		table = (SDMSScopeConfigEnvMappingTable) this;
		SDMSScopeConfigEnvMappingTableGeneric.table = (SDMSScopeConfigEnvMappingTable) this;
		isVersioned = false;
		idx_sId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_scopeId_key = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSScopeConfigEnvMapping create(SystemEnvironment env
	                                        ,String p_key
	                                        ,String p_value
	                                        ,Long p_sId
	                                       )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ScopeConfigEnvMapping"));
		}
		validate(env
		         , p_key
		         , p_value
		         , p_sId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSScopeConfigEnvMappingGeneric o = new SDMSScopeConfigEnvMappingGeneric(env
		                , p_key
		                , p_value
		                , p_sId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                         );

		SDMSScopeConfigEnvMapping p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSScopeConfigEnvMapping)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSScopeConfigEnvMapping)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSScopeConfigEnvMapping p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_key
	                        ,String p_value
	                        ,Long p_sId
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
		String key;
		String value;
		Long sId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			key = r.getString(2);
			value = r.getString(3);
			sId = new Long (r.getLong(4));
			creatorUId = new Long (r.getLong(5));
			createTs = new Long (r.getLong(6));
			changerUId = new Long (r.getLong(7));
			changeTs = new Long (r.getLong(8));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ScopeConfigEnvMapping: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSScopeConfigEnvMappingGeneric(id,
		                key,
		                value,
		                sId,
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
		                                   ", " + squote + "KEY" + equote +
		                                   ", " + squote + "VALUE" + equote +
		                                   ", " + squote + "S_ID" + equote +
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
		idx_sId.put(env, ((SDMSScopeConfigEnvMappingGeneric) o).sId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScopeConfigEnvMappingGeneric) o).sId);
		k.add(((SDMSScopeConfigEnvMappingGeneric) o).key);
		idx_scopeId_key.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_sId.remove(env, ((SDMSScopeConfigEnvMappingGeneric) o).sId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScopeConfigEnvMappingGeneric) o).sId);
		k.add(((SDMSScopeConfigEnvMappingGeneric) o).key);
		idx_scopeId_key.remove(env, k, o);
	}

	public static SDMSScopeConfigEnvMapping getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSScopeConfigEnvMapping) table.get(env, id);
	}

	public static SDMSScopeConfigEnvMapping getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSScopeConfigEnvMapping) table.get(env, id, version);
	}

	public static SDMSScopeConfigEnvMapping idx_scopeId_key_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSScopeConfigEnvMapping)  SDMSScopeConfigEnvMappingTableGeneric.idx_scopeId_key.getUnique(env, key);
	}

	public static SDMSScopeConfigEnvMapping idx_scopeId_key_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSScopeConfigEnvMapping)  SDMSScopeConfigEnvMappingTableGeneric.idx_scopeId_key.getUnique(env, key, version);
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
