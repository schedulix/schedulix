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

public class SDMSExitStateTableGeneric extends SDMSTable
{

	public final static String tableName = "EXIT_STATE";
	public static SDMSExitStateTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "PREFERENCE"
		, "IS_FINAL"
		, "IS_RESTARTABLE"
		, "IS_UNREACHABLE"
		, "IS_DISABLED"
		, "IS_BROKEN"
		, "IS_BATCH_DEFAULT"
		, "IS_DEPENDENCY_DEFAULT"
		, "ESP_ID"
		, "ESD_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_espId;
	public static SDMSIndex idx_esdId;
	public static SDMSIndex idx_espId_esdId;

	public SDMSExitStateTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "ExitState"));
		}
		table = (SDMSExitStateTable) this;
		SDMSExitStateTableGeneric.table = (SDMSExitStateTable) this;
		isVersioned = true;
		idx_espId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "espId");
		idx_esdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "esdId");
		idx_espId_esdId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "espId_esdId");
	}
	public SDMSExitState create(SystemEnvironment env
	                            ,Integer p_preference
	                            ,Boolean p_isFinal
	                            ,Boolean p_isRestartable
	                            ,Boolean p_isUnreachable
	                            ,Boolean p_isDisabled
	                            ,Boolean p_isBroken
	                            ,Boolean p_isBatchDefault
	                            ,Boolean p_isDependencyDefault
	                            ,Long p_espId
	                            ,Long p_esdId
	                           )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "ExitState"));
		}
		validate(env
		         , p_preference
		         , p_isFinal
		         , p_isRestartable
		         , p_isUnreachable
		         , p_isDisabled
		         , p_isBroken
		         , p_isBatchDefault
		         , p_isDependencyDefault
		         , p_espId
		         , p_esdId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSExitStateGeneric o = new SDMSExitStateGeneric(env
		                , p_preference
		                , p_isFinal
		                , p_isRestartable
		                , p_isUnreachable
		                , p_isDisabled
		                , p_isBroken
		                , p_isBatchDefault
		                , p_isDependencyDefault
		                , p_espId
		                , p_esdId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                 );

		SDMSExitState p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSExitState)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSExitState)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSExitState p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Integer p_preference
	                        ,Boolean p_isFinal
	                        ,Boolean p_isRestartable
	                        ,Boolean p_isUnreachable
	                        ,Boolean p_isDisabled
	                        ,Boolean p_isBroken
	                        ,Boolean p_isBatchDefault
	                        ,Boolean p_isDependencyDefault
	                        ,Long p_espId
	                        ,Long p_esdId
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
		Integer preference;
		Boolean isFinal;
		Boolean isRestartable;
		Boolean isUnreachable;
		Boolean isDisabled;
		Boolean isBroken;
		Boolean isBatchDefault;
		Boolean isDependencyDefault;
		Long espId;
		Long esdId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			preference = new Integer (r.getInt(2));
			isFinal = new Boolean ((r.getInt(3) == 0 ? false : true));
			isRestartable = new Boolean ((r.getInt(4) == 0 ? false : true));
			isUnreachable = new Boolean ((r.getInt(5) == 0 ? false : true));
			isDisabled = new Boolean ((r.getInt(6) == 0 ? false : true));
			isBroken = new Boolean ((r.getInt(7) == 0 ? false : true));
			isBatchDefault = new Boolean ((r.getInt(8) == 0 ? false : true));
			isDependencyDefault = new Boolean ((r.getInt(9) == 0 ? false : true));
			espId = new Long (r.getLong(10));
			esdId = new Long (r.getLong(11));
			creatorUId = new Long (r.getLong(12));
			createTs = new Long (r.getLong(13));
			changerUId = new Long (r.getLong(14));
			changeTs = new Long (r.getLong(15));
			validFrom = r.getLong(16);
			validTo = r.getLong(17);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "ExitState: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSExitStateGeneric(id,
		                                preference,
		                                isFinal,
		                                isRestartable,
		                                isUnreachable,
		                                isDisabled,
		                                isBroken,
		                                isBatchDefault,
		                                isDependencyDefault,
		                                espId,
		                                esdId,
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
		                                   ", " + squote + "PREFERENCE" + equote +
		                                   ", " + squote + "IS_FINAL" + equote +
		                                   ", " + squote + "IS_RESTARTABLE" + equote +
		                                   ", " + squote + "IS_UNREACHABLE" + equote +
		                                   ", " + squote + "IS_DISABLED" + equote +
		                                   ", " + squote + "IS_BROKEN" + equote +
		                                   ", " + squote + "IS_BATCH_DEFAULT" + equote +
		                                   ", " + squote + "IS_DEPENDENCY_DEFAULT" + equote +
		                                   ", " + squote + "ESP_ID" + equote +
		                                   ", " + squote + "ESD_ID" + equote +
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
		ok =  idx_espId.check(((SDMSExitStateGeneric) o).espId, o);
		out = out + "idx_espId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_esdId.check(((SDMSExitStateGeneric) o).esdId, o);
		out = out + "idx_esdId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateGeneric) o).espId);
		k.add(((SDMSExitStateGeneric) o).esdId);
		ok =  idx_espId_esdId.check(k, o);
		out = out + "idx_espId_esdId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_espId.put(env, ((SDMSExitStateGeneric) o).espId, o, ((1 & indexMember) != 0));
		idx_esdId.put(env, ((SDMSExitStateGeneric) o).esdId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateGeneric) o).espId);
		k.add(((SDMSExitStateGeneric) o).esdId);
		idx_espId_esdId.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_espId.remove(env, ((SDMSExitStateGeneric) o).espId, o);
		idx_esdId.remove(env, ((SDMSExitStateGeneric) o).esdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateGeneric) o).espId);
		k.add(((SDMSExitStateGeneric) o).esdId);
		idx_espId_esdId.remove(env, k, o);
	}

	public static SDMSExitState getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSExitState) table.get(env, id);
	}

	public static SDMSExitState getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSExitState) table.getForUpdate(env, id);
	}

	public static SDMSExitState getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSExitState) table.get(env, id, version);
	}

	public static SDMSExitState idx_espId_esdId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSExitState)  SDMSExitStateTableGeneric.idx_espId_esdId.getUnique(env, key);
	}

	public static SDMSExitState idx_espId_esdId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSExitState)  SDMSExitStateTableGeneric.idx_espId_esdId.getUniqueForUpdate(env, key);
	}

	public static SDMSExitState idx_espId_esdId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSExitState)  SDMSExitStateTableGeneric.idx_espId_esdId.getUnique(env, key, version);
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
