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

public class SDMSExitStateTranslationTableGeneric extends SDMSTable
{

	public final static String tableName = "EXIT_STATE_TRANSLATION";
	public static SDMSExitStateTranslationTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "ESTP_ID"
		, "FROM_ESD_ID"
		, "TO_ESD_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_estpId;
	public static SDMSIndex idx_fromEsdId;
	public static SDMSIndex idx_toEsdId;
	public static SDMSIndex idx_estpId_fromEsdId;

	public SDMSExitStateTranslationTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "ExitStateTranslation"));
		}
		table = (SDMSExitStateTranslationTable) this;
		SDMSExitStateTranslationTableGeneric.table = (SDMSExitStateTranslationTable) this;
		isVersioned = true;
		idx_estpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "estpId");
		idx_fromEsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "fromEsdId");
		idx_toEsdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "toEsdId");
		idx_estpId_fromEsdId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "estpId_fromEsdId");
	}
	public SDMSExitStateTranslation create(SystemEnvironment env
	                                       ,Long p_estpId
	                                       ,Long p_fromEsdId
	                                       ,Long p_toEsdId
	                                      )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "ExitStateTranslation"));
		}
		validate(env
		         , p_estpId
		         , p_fromEsdId
		         , p_toEsdId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSExitStateTranslationGeneric o = new SDMSExitStateTranslationGeneric(env
		                , p_estpId
		                , p_fromEsdId
		                , p_toEsdId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                       );

		SDMSExitStateTranslation p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSExitStateTranslation)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSExitStateTranslation)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSExitStateTranslation p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_estpId
	                        ,Long p_fromEsdId
	                        ,Long p_toEsdId
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
		Long estpId;
		Long fromEsdId;
		Long toEsdId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			estpId = new Long (r.getLong(2));
			fromEsdId = new Long (r.getLong(3));
			toEsdId = new Long (r.getLong(4));
			creatorUId = new Long (r.getLong(5));
			createTs = new Long (r.getLong(6));
			changerUId = new Long (r.getLong(7));
			changeTs = new Long (r.getLong(8));
			validFrom = r.getLong(9);
			validTo = r.getLong(10);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "ExitStateTranslation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSExitStateTranslationGeneric(id,
		                estpId,
		                fromEsdId,
		                toEsdId,
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
		                                   ", " + squote + "ESTP_ID" + equote +
		                                   ", " + squote + "FROM_ESD_ID" + equote +
		                                   ", " + squote + "TO_ESD_ID" + equote +
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
		ok =  idx_estpId.check(((SDMSExitStateTranslationGeneric) o).estpId, o);
		out = out + "idx_estpId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_fromEsdId.check(((SDMSExitStateTranslationGeneric) o).fromEsdId, o);
		out = out + "idx_fromEsdId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_toEsdId.check(((SDMSExitStateTranslationGeneric) o).toEsdId, o);
		out = out + "idx_toEsdId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateTranslationGeneric) o).estpId);
		k.add(((SDMSExitStateTranslationGeneric) o).fromEsdId);
		ok =  idx_estpId_fromEsdId.check(k, o);
		out = out + "idx_estpId_fromEsdId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_estpId.put(env, ((SDMSExitStateTranslationGeneric) o).estpId, o, ((1 & indexMember) != 0));
		idx_fromEsdId.put(env, ((SDMSExitStateTranslationGeneric) o).fromEsdId, o, ((2 & indexMember) != 0));
		idx_toEsdId.put(env, ((SDMSExitStateTranslationGeneric) o).toEsdId, o, ((4 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateTranslationGeneric) o).estpId);
		k.add(((SDMSExitStateTranslationGeneric) o).fromEsdId);
		idx_estpId_fromEsdId.put(env, k, o, ((8 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_estpId.remove(env, ((SDMSExitStateTranslationGeneric) o).estpId, o);
		idx_fromEsdId.remove(env, ((SDMSExitStateTranslationGeneric) o).fromEsdId, o);
		idx_toEsdId.remove(env, ((SDMSExitStateTranslationGeneric) o).toEsdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateTranslationGeneric) o).estpId);
		k.add(((SDMSExitStateTranslationGeneric) o).fromEsdId);
		idx_estpId_fromEsdId.remove(env, k, o);
	}

	public static SDMSExitStateTranslation getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSExitStateTranslation) table.get(env, id);
	}

	public static SDMSExitStateTranslation getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSExitStateTranslation) table.getForUpdate(env, id);
	}

	public static SDMSExitStateTranslation getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSExitStateTranslation) table.get(env, id, version);
	}

	public static SDMSExitStateTranslation idx_estpId_fromEsdId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSExitStateTranslation)  SDMSExitStateTranslationTableGeneric.idx_estpId_fromEsdId.getUnique(env, key);
	}

	public static SDMSExitStateTranslation idx_estpId_fromEsdId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSExitStateTranslation)  SDMSExitStateTranslationTableGeneric.idx_estpId_fromEsdId.getUniqueForUpdate(env, key);
	}

	public static SDMSExitStateTranslation idx_estpId_fromEsdId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSExitStateTranslation)  SDMSExitStateTranslationTableGeneric.idx_estpId_fromEsdId.getUnique(env, key, version);
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
