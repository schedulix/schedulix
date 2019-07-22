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

public class SDMSTriggerParameterTableGeneric extends SDMSTable
{

	public final static String tableName = "TRIGGER_PARAMETER";
	public static SDMSTriggerParameterTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "EXPRESSION"
		, "TRIGGER_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_triggerId;
	public static SDMSIndex idx_triggerId_name;

	public SDMSTriggerParameterTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "TriggerParameter"));
		}
		table = (SDMSTriggerParameterTable) this;
		SDMSTriggerParameterTableGeneric.table = (SDMSTriggerParameterTable) this;
		isVersioned = true;
		idx_triggerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "triggerId");
		idx_triggerId_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "triggerId_name");
	}
	public SDMSTriggerParameter create(SystemEnvironment env
	                                   ,String p_name
	                                   ,String p_expression
	                                   ,Long p_triggerId
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "TriggerParameter"));
		}
		validate(env
		         , p_name
		         , p_expression
		         , p_triggerId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSTriggerParameterGeneric o = new SDMSTriggerParameterGeneric(env
		                , p_name
		                , p_expression
		                , p_triggerId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSTriggerParameter p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSTriggerParameter)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSTriggerParameter)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSTriggerParameter p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,String p_expression
	                        ,Long p_triggerId
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
		String expression;
		Long triggerId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			expression = r.getString(3);
			triggerId = new Long (r.getLong(4));
			creatorUId = new Long (r.getLong(5));
			createTs = new Long (r.getLong(6));
			changerUId = new Long (r.getLong(7));
			changeTs = new Long (r.getLong(8));
			validFrom = r.getLong(9);
			validTo = r.getLong(10);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "TriggerParameter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSTriggerParameterGeneric(id,
		                                       name,
		                                       expression,
		                                       triggerId,
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
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "EXPRESSION" + equote +
		                                   ", " + squote + "TRIGGER_ID" + equote +
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
		ok =  idx_triggerId.check(((SDMSTriggerParameterGeneric) o).triggerId, o);
		out = out + "idx_triggerId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerParameterGeneric) o).triggerId);
		k.add(((SDMSTriggerParameterGeneric) o).name);
		ok =  idx_triggerId_name.check(k, o);
		out = out + "idx_triggerId_name: " + (ok ? "ok" : "missing") + "\n";
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
		idx_triggerId.put(env, ((SDMSTriggerParameterGeneric) o).triggerId, o, ((1 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerParameterGeneric) o).triggerId);
		k.add(((SDMSTriggerParameterGeneric) o).name);
		idx_triggerId_name.put(env, k, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_triggerId.remove(env, ((SDMSTriggerParameterGeneric) o).triggerId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTriggerParameterGeneric) o).triggerId);
		k.add(((SDMSTriggerParameterGeneric) o).name);
		idx_triggerId_name.remove(env, k, o);
	}

	public static SDMSTriggerParameter getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTriggerParameter) table.get(env, id);
	}

	public static SDMSTriggerParameter getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTriggerParameter) table.getForUpdate(env, id);
	}

	public static SDMSTriggerParameter getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSTriggerParameter) table.get(env, id, version);
	}

	public static SDMSTriggerParameter idx_triggerId_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSTriggerParameter)  SDMSTriggerParameterTableGeneric.idx_triggerId_name.getUnique(env, key);
	}

	public static SDMSTriggerParameter idx_triggerId_name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSTriggerParameter)  SDMSTriggerParameterTableGeneric.idx_triggerId_name.getUniqueForUpdate(env, key);
	}

	public static SDMSTriggerParameter idx_triggerId_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSTriggerParameter)  SDMSTriggerParameterTableGeneric.idx_triggerId_name.getUnique(env, key, version);
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
