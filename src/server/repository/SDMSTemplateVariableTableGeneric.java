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

public class SDMSTemplateVariableTableGeneric extends SDMSTable
{

	public final static String tableName = "TEMPLATE_VARIABLE";
	public static SDMSTemplateVariableTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "PD_ID"
		, "RT_ID"
		, "VALUE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_pdId;
	public static SDMSIndex idx_rtId;
	public static SDMSIndex idx_pdId_rtId;

	public SDMSTemplateVariableTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "TemplateVariable"));
		}
		table = (SDMSTemplateVariableTable) this;
		SDMSTemplateVariableTableGeneric.table = (SDMSTemplateVariableTable) this;
		isVersioned = true;
		idx_pdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "pdId");
		idx_rtId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rtId");
		idx_pdId_rtId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "pdId_rtId");
	}
	public SDMSTemplateVariable create(SystemEnvironment env
	                                   ,Long p_pdId
	                                   ,Long p_rtId
	                                   ,String p_value
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "TemplateVariable"));
		}
		validate(env
		         , p_pdId
		         , p_rtId
		         , p_value
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSTemplateVariableGeneric o = new SDMSTemplateVariableGeneric(env
		                , p_pdId
		                , p_rtId
		                , p_value
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSTemplateVariable p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSTemplateVariable)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSTemplateVariable)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSTemplateVariable p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_pdId
	                        ,Long p_rtId
	                        ,String p_value
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
		Long pdId;
		Long rtId;
		String value;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			pdId = new Long (r.getLong(2));
			rtId = new Long (r.getLong(3));
			value = r.getString(4);
			creatorUId = new Long (r.getLong(5));
			createTs = new Long (r.getLong(6));
			changerUId = new Long (r.getLong(7));
			changeTs = new Long (r.getLong(8));
			validFrom = r.getLong(9);
			validTo = r.getLong(10);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "TemplateVariable: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSTemplateVariableGeneric(id,
		                                       pdId,
		                                       rtId,
		                                       value,
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
		                                   ", " + squote + "PD_ID" + equote +
		                                   ", " + squote + "RT_ID" + equote +
		                                   ", " + squote + "VALUE" + equote +
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
		ok =  idx_pdId.check(((SDMSTemplateVariableGeneric) o).pdId, o);
		out = out + "idx_pdId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_rtId.check(((SDMSTemplateVariableGeneric) o).rtId, o);
		out = out + "idx_rtId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTemplateVariableGeneric) o).pdId);
		k.add(((SDMSTemplateVariableGeneric) o).rtId);
		ok =  idx_pdId_rtId.check(k, o);
		out = out + "idx_pdId_rtId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_pdId.put(env, ((SDMSTemplateVariableGeneric) o).pdId, o, ((1 & indexMember) != 0));
		idx_rtId.put(env, ((SDMSTemplateVariableGeneric) o).rtId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTemplateVariableGeneric) o).pdId);
		k.add(((SDMSTemplateVariableGeneric) o).rtId);
		idx_pdId_rtId.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_pdId.remove(env, ((SDMSTemplateVariableGeneric) o).pdId, o);
		idx_rtId.remove(env, ((SDMSTemplateVariableGeneric) o).rtId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSTemplateVariableGeneric) o).pdId);
		k.add(((SDMSTemplateVariableGeneric) o).rtId);
		idx_pdId_rtId.remove(env, k, o);
	}

	public static SDMSTemplateVariable getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTemplateVariable) table.get(env, id);
	}

	public static SDMSTemplateVariable getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSTemplateVariable) table.getForUpdate(env, id);
	}

	public static SDMSTemplateVariable getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSTemplateVariable) table.get(env, id, version);
	}

	public static SDMSTemplateVariable idx_pdId_rtId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSTemplateVariable)  SDMSTemplateVariableTableGeneric.idx_pdId_rtId.getUnique(env, key);
	}

	public static SDMSTemplateVariable idx_pdId_rtId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSTemplateVariable)  SDMSTemplateVariableTableGeneric.idx_pdId_rtId.getUniqueForUpdate(env, key);
	}

	public static SDMSTemplateVariable idx_pdId_rtId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSTemplateVariable)  SDMSTemplateVariableTableGeneric.idx_pdId_rtId.getUnique(env, key, version);
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
