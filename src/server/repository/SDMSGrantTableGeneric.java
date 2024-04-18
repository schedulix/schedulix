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

public class SDMSGrantTableGeneric extends SDMSTable
{

	public final static String tableName = "GRANTS";
	public static SDMSGrantTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "OBJECT_ID"
		, "G_ID"
		, "OBJECT_TYPE"
		, "PRIVS"
		, "DELETE_VERSION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_objectId;
	public static SDMSIndex idx_gId;
	public static SDMSIndex idx_objectId_gId;

	public SDMSGrantTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "Grant"));
		}
		table = (SDMSGrantTable) this;
		SDMSGrantTableGeneric.table = (SDMSGrantTable) this;
		isVersioned = false;
		idx_objectId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "objectId");
		idx_gId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "gId");
		idx_objectId_gId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "objectId_gId");
	}
	public SDMSGrant create(SystemEnvironment env
	                        ,Long p_objectId
	                        ,Long p_gId
	                        ,Integer p_objectType
	                        ,Long p_privs
	                        ,Long p_deleteVersion
	                       )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "Grant"));
		}
		validate(env
		         , p_objectId
		         , p_gId
		         , p_objectType
		         , p_privs
		         , p_deleteVersion
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSGrantGeneric o = new SDMSGrantGeneric(env
		                , p_objectId
		                , p_gId
		                , p_objectType
		                , p_privs
		                , p_deleteVersion
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                         );

		SDMSGrant p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSGrant)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSGrant)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSGrant p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_objectId
	                        ,Long p_gId
	                        ,Integer p_objectType
	                        ,Long p_privs
	                        ,Long p_deleteVersion
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSGrantGeneric.checkObjectType(p_objectType))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Grant: $1 $2", "objectType", p_objectType));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long objectId;
		Long gId;
		Integer objectType;
		Long privs;
		Long deleteVersion;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			objectId = Long.valueOf (r.getLong(2));
			gId = Long.valueOf (r.getLong(3));
			objectType = Integer.valueOf (r.getInt(4));
			privs = Long.valueOf (r.getLong(5));
			deleteVersion = Long.valueOf (r.getLong(6));
			if (r.wasNull()) deleteVersion = null;
			creatorUId = Long.valueOf (r.getLong(7));
			createTs = Long.valueOf (r.getLong(8));
			changerUId = Long.valueOf (r.getLong(9));
			changeTs = Long.valueOf (r.getLong(10));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "Grant: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSGrantGeneric(id,
		                            objectId,
		                            gId,
		                            objectType,
		                            privs,
		                            deleteVersion,
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
		                                   ", " + squote + "OBJECT_ID" + equote +
		                                   ", " + squote + "G_ID" + equote +
		                                   ", " + squote + "OBJECT_TYPE" + equote +
		                                   ", " + squote + "PRIVS" + equote +
		                                   ", " + squote + "DELETE_VERSION" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote +
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
		ok =  idx_objectId.check(((SDMSGrantGeneric) o).objectId, o);
		out = out + "idx_objectId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_gId.check(((SDMSGrantGeneric) o).gId, o);
		out = out + "idx_gId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGrantGeneric) o).objectId);
		k.add(((SDMSGrantGeneric) o).gId);
		ok =  idx_objectId_gId.check(k, o);
		out = out + "idx_objectId_gId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_objectId.put(env, ((SDMSGrantGeneric) o).objectId, o, ((1 & indexMember) != 0));
		idx_gId.put(env, ((SDMSGrantGeneric) o).gId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGrantGeneric) o).objectId);
		k.add(((SDMSGrantGeneric) o).gId);
		idx_objectId_gId.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_objectId.remove(env, ((SDMSGrantGeneric) o).objectId, o);
		idx_gId.remove(env, ((SDMSGrantGeneric) o).gId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSGrantGeneric) o).objectId);
		k.add(((SDMSGrantGeneric) o).gId);
		idx_objectId_gId.remove(env, k, o);
	}

	public static SDMSGrant getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSGrant) table.get(env, id);
	}

	public static SDMSGrant getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSGrant) table.getForUpdate(env, id);
	}

	public static SDMSGrant getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSGrant) table.get(env, id, version);
	}

	public static SDMSGrant idx_objectId_gId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSGrant)  SDMSGrantTableGeneric.idx_objectId_gId.getUnique(env, key);
	}

	public static SDMSGrant idx_objectId_gId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSGrant)  SDMSGrantTableGeneric.idx_objectId_gId.getUniqueForUpdate(env, key);
	}

	public static SDMSGrant idx_objectId_gId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSGrant)  SDMSGrantTableGeneric.idx_objectId_gId.getUnique(env, key, version);
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
