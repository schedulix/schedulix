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

public class SDMSEntityVariableTableGeneric extends SDMSTable
{

	public final static String tableName = "ENTITY_VARIABLE";
	public static SDMSEntityVariableTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SME_ID"
		, "NAME"
		, "VALUE"
		, "IS_LOCAL"
		, "EV_LINK"
		, "IS_LONG"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_smeId_Name;

	public SDMSEntityVariableTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "EntityVariable"));
		}
		table = (SDMSEntityVariableTable) this;
		SDMSEntityVariableTableGeneric.table = (SDMSEntityVariableTable) this;
		isVersioned = false;
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId");
		idx_smeId_Name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "smeId_Name");
	}
	public SDMSEntityVariable create(SystemEnvironment env
	                                 ,Long p_smeId
	                                 ,String p_name
	                                 ,String p_value
	                                 ,Boolean p_isLocal
	                                 ,Long p_evLink
	                                 ,Boolean p_isLong
	                                )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "EntityVariable"));
		}
		validate(env
		         , p_smeId
		         , p_name
		         , p_value
		         , p_isLocal
		         , p_evLink
		         , p_isLong
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSEntityVariableGeneric o = new SDMSEntityVariableGeneric(env
		                , p_smeId
		                , p_name
		                , p_value
		                , p_isLocal
		                , p_evLink
		                , p_isLong
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                           );

		SDMSEntityVariable p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSEntityVariable)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSEntityVariable)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSEntityVariable p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_smeId
	                        ,String p_name
	                        ,String p_value
	                        ,Boolean p_isLocal
	                        ,Long p_evLink
	                        ,Boolean p_isLong
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
		Long smeId;
		String name;
		String value;
		Boolean isLocal;
		Long evLink;
		Boolean isLong;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			smeId = Long.valueOf (r.getLong(2));
			name = r.getString(3);
			value = r.getString(4);
			if (r.wasNull()) value = null;
			isLocal = Boolean.valueOf ((r.getInt(5) == 0 ? false : true));
			evLink = Long.valueOf (r.getLong(6));
			if (r.wasNull()) evLink = null;
			isLong = Boolean.valueOf ((r.getInt(7) == 0 ? false : true));
			creatorUId = Long.valueOf (r.getLong(8));
			createTs = Long.valueOf (r.getLong(9));
			changerUId = Long.valueOf (r.getLong(10));
			changeTs = Long.valueOf (r.getLong(11));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "EntityVariable: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSEntityVariableGeneric(id,
		                                     smeId,
		                                     name,
		                                     value,
		                                     isLocal,
		                                     evLink,
		                                     isLong,
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
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "VALUE" + equote +
		                                   ", " + squote + "IS_LOCAL" + equote +
		                                   ", " + squote + "EV_LINK" + equote +
		                                   ", " + squote + "IS_LONG" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote + ", " +
		                                   "       SME2LOAD " +
		                                   " WHERE " + squote + tableName() + equote + ".SME_ID = SME2LOAD.ID"
		                                  );
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
		ok =  idx_smeId.check(((SDMSEntityVariableGeneric) o).smeId, o);
		out = out + "idx_smeId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSEntityVariableGeneric) o).smeId);
		k.add(((SDMSEntityVariableGeneric) o).name);
		ok =  idx_smeId_Name.check(k, o);
		out = out + "idx_smeId_Name: " + (ok ? "ok" : "missing") + "\n";
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
		idx_smeId.put(env, ((SDMSEntityVariableGeneric) o).smeId, o, ((1 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSEntityVariableGeneric) o).smeId);
		k.add(((SDMSEntityVariableGeneric) o).name);
		idx_smeId_Name.put(env, k, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_smeId.remove(env, ((SDMSEntityVariableGeneric) o).smeId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSEntityVariableGeneric) o).smeId);
		k.add(((SDMSEntityVariableGeneric) o).name);
		idx_smeId_Name.remove(env, k, o);
	}

	public static SDMSEntityVariable getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSEntityVariable) table.get(env, id);
	}

	public static SDMSEntityVariable getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSEntityVariable) table.getForUpdate(env, id);
	}

	public static SDMSEntityVariable getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSEntityVariable) table.get(env, id, version);
	}

	public static SDMSEntityVariable idx_smeId_Name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSEntityVariable)  SDMSEntityVariableTableGeneric.idx_smeId_Name.getUnique(env, key);
	}

	public static SDMSEntityVariable idx_smeId_Name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSEntityVariable)  SDMSEntityVariableTableGeneric.idx_smeId_Name.getUniqueForUpdate(env, key);
	}

	public static SDMSEntityVariable idx_smeId_Name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSEntityVariable)  SDMSEntityVariableTableGeneric.idx_smeId_Name.getUnique(env, key, version);
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
