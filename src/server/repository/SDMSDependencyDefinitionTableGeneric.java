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

public class SDMSDependencyDefinitionTableGeneric extends SDMSTable
{

	public final static String tableName = "DEPENDENCY_DEFINITION";
	public static SDMSDependencyDefinitionTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SE_DEPENDENT_ID"
		, "SE_REQUIRED_ID"
		, "NAME"
		, "UNRESOLVED_HANDLING"
		, "DMODE"
		, "STATE_SELECTION"
		, "CONDITION"
		, "RESOLVE_MODE"
		, "EXPIRED_AMOUNT"
		, "EXPIRED_BASE"
		, "SELECT_CONDITION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_seDependentId;
	public static SDMSIndex idx_seRequiredId;
	public static SDMSIndex idx_name;
	public static SDMSIndex idx_DependentId_RequiredId;

	public SDMSDependencyDefinitionTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "DependencyDefinition"));
		}
		table = (SDMSDependencyDefinitionTable) this;
		SDMSDependencyDefinitionTableGeneric.table = (SDMSDependencyDefinitionTable) this;
		isVersioned = true;
		idx_seDependentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seDependentId");
		idx_seRequiredId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seRequiredId");
		idx_name = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "name");
		idx_DependentId_RequiredId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "DependentId_RequiredId");
	}
	public SDMSDependencyDefinition create(SystemEnvironment env
	                                       ,Long p_seDependentId
	                                       ,Long p_seRequiredId
	                                       ,String p_name
	                                       ,Integer p_unresolvedHandling
	                                       ,Integer p_mode
	                                       ,Integer p_stateSelection
	                                       ,String p_condition
	                                       ,Integer p_resolveMode
	                                       ,Integer p_expiredAmount
	                                       ,Integer p_expiredBase
	                                       ,String p_selectCondition
	                                      )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "DependencyDefinition"));
		}
		validate(env
		         , p_seDependentId
		         , p_seRequiredId
		         , p_name
		         , p_unresolvedHandling
		         , p_mode
		         , p_stateSelection
		         , p_condition
		         , p_resolveMode
		         , p_expiredAmount
		         , p_expiredBase
		         , p_selectCondition
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSDependencyDefinitionGeneric o = new SDMSDependencyDefinitionGeneric(env
		                , p_seDependentId
		                , p_seRequiredId
		                , p_name
		                , p_unresolvedHandling
		                , p_mode
		                , p_stateSelection
		                , p_condition
		                , p_resolveMode
		                , p_expiredAmount
		                , p_expiredBase
		                , p_selectCondition
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                       );

		SDMSDependencyDefinition p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSDependencyDefinition)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSDependencyDefinition)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSDependencyDefinition p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_seDependentId
	                        ,Long p_seRequiredId
	                        ,String p_name
	                        ,Integer p_unresolvedHandling
	                        ,Integer p_mode
	                        ,Integer p_stateSelection
	                        ,String p_condition
	                        ,Integer p_resolveMode
	                        ,Integer p_expiredAmount
	                        ,Integer p_expiredBase
	                        ,String p_selectCondition
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSDependencyDefinitionGeneric.checkUnresolvedHandling(p_unresolvedHandling))
			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyDefinition: $1 $2", "unresolvedHandling", p_unresolvedHandling));
		if (!SDMSDependencyDefinitionGeneric.checkMode(p_mode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyDefinition: $1 $2", "mode", p_mode));
		if (!SDMSDependencyDefinitionGeneric.checkStateSelection(p_stateSelection))
			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyDefinition: $1 $2", "stateSelection", p_stateSelection));
		if (!SDMSDependencyDefinitionGeneric.checkResolveMode(p_resolveMode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyDefinition: $1 $2", "resolveMode", p_resolveMode));
		if (!SDMSDependencyDefinitionGeneric.checkExpiredBase(p_expiredBase))
			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyDefinition: $1 $2", "expiredBase", p_expiredBase));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long seDependentId;
		Long seRequiredId;
		String name;
		Integer unresolvedHandling;
		Integer mode;
		Integer stateSelection;
		String condition;
		Integer resolveMode;
		Integer expiredAmount;
		Integer expiredBase;
		String selectCondition;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			seDependentId = Long.valueOf (r.getLong(2));
			seRequiredId = Long.valueOf (r.getLong(3));
			name = r.getString(4);
			if (r.wasNull()) name = null;
			unresolvedHandling = Integer.valueOf (r.getInt(5));
			mode = Integer.valueOf (r.getInt(6));
			stateSelection = Integer.valueOf (r.getInt(7));
			condition = r.getString(8);
			if (r.wasNull()) condition = null;
			resolveMode = Integer.valueOf (r.getInt(9));
			expiredAmount = Integer.valueOf (r.getInt(10));
			if (r.wasNull()) expiredAmount = null;
			expiredBase = Integer.valueOf (r.getInt(11));
			if (r.wasNull()) expiredBase = null;
			selectCondition = r.getString(12);
			if (r.wasNull()) selectCondition = null;
			creatorUId = Long.valueOf (r.getLong(13));
			createTs = Long.valueOf (r.getLong(14));
			changerUId = Long.valueOf (r.getLong(15));
			changeTs = Long.valueOf (r.getLong(16));
			validFrom = r.getLong(17);
			validTo = r.getLong(18);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "DependencyDefinition: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSDependencyDefinitionGeneric(id,
		                seDependentId,
		                seRequiredId,
		                name,
		                unresolvedHandling,
		                mode,
		                stateSelection,
		                condition,
		                resolveMode,
		                expiredAmount,
		                expiredBase,
		                selectCondition,
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
		                                   ", " + squote + "SE_DEPENDENT_ID" + equote +
		                                   ", " + squote + "SE_REQUIRED_ID" + equote +
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "UNRESOLVED_HANDLING" + equote +
		                                   ", " + squote + "DMODE" + equote +
		                                   ", " + squote + "STATE_SELECTION" + equote +
		                                   ", " + squote + "CONDITION" + equote +
		                                   ", " + squote + "RESOLVE_MODE" + equote +
		                                   ", " + squote + "EXPIRED_AMOUNT" + equote +
		                                   ", " + squote + "EXPIRED_BASE" + equote +
		                                   ", " + squote + "SELECT_CONDITION" + equote +
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
		ok =  idx_seDependentId.check(((SDMSDependencyDefinitionGeneric) o).seDependentId, o);
		out = out + "idx_seDependentId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_seRequiredId.check(((SDMSDependencyDefinitionGeneric) o).seRequiredId, o);
		out = out + "idx_seRequiredId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_name.check(((SDMSDependencyDefinitionGeneric) o).name, o);
		out = out + "idx_name: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyDefinitionGeneric) o).seDependentId);
		k.add(((SDMSDependencyDefinitionGeneric) o).seRequiredId);
		ok =  idx_DependentId_RequiredId.check(k, o);
		out = out + "idx_DependentId_RequiredId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_seDependentId.put(env, ((SDMSDependencyDefinitionGeneric) o).seDependentId, o, ((1 & indexMember) != 0));
		idx_seRequiredId.put(env, ((SDMSDependencyDefinitionGeneric) o).seRequiredId, o, ((2 & indexMember) != 0));
		idx_name.put(env, ((SDMSDependencyDefinitionGeneric) o).name, o, ((4 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyDefinitionGeneric) o).seDependentId);
		k.add(((SDMSDependencyDefinitionGeneric) o).seRequiredId);
		idx_DependentId_RequiredId.put(env, k, o, ((8 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_seDependentId.remove(env, ((SDMSDependencyDefinitionGeneric) o).seDependentId, o);
		idx_seRequiredId.remove(env, ((SDMSDependencyDefinitionGeneric) o).seRequiredId, o);
		idx_name.remove(env, ((SDMSDependencyDefinitionGeneric) o).name, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyDefinitionGeneric) o).seDependentId);
		k.add(((SDMSDependencyDefinitionGeneric) o).seRequiredId);
		idx_DependentId_RequiredId.remove(env, k, o);
	}

	public static SDMSDependencyDefinition getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSDependencyDefinition) table.get(env, id);
	}

	public static SDMSDependencyDefinition getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSDependencyDefinition) table.getForUpdate(env, id);
	}

	public static SDMSDependencyDefinition getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSDependencyDefinition) table.get(env, id, version);
	}

	public static SDMSDependencyDefinition idx_DependentId_RequiredId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSDependencyDefinition)  SDMSDependencyDefinitionTableGeneric.idx_DependentId_RequiredId.getUnique(env, key);
	}

	public static SDMSDependencyDefinition idx_DependentId_RequiredId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSDependencyDefinition)  SDMSDependencyDefinitionTableGeneric.idx_DependentId_RequiredId.getUniqueForUpdate(env, key);
	}

	public static SDMSDependencyDefinition idx_DependentId_RequiredId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSDependencyDefinition)  SDMSDependencyDefinitionTableGeneric.idx_DependentId_RequiredId.getUnique(env, key, version);
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
