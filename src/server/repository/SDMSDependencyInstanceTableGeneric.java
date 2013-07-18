/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class SDMSDependencyInstanceTableGeneric extends SDMSTable
{

	public final static String __version = "SDMSDependencyInstanceTableGeneric $Revision: 2.4 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "DEPENDENCY_INSTANCE";
	public static SDMSDependencyInstanceTable table  = null;

	public static SDMSIndex idx_ddId;
	public static SDMSIndex idx_dependentId;
	public static SDMSIndex idx_requiredId;
	public static SDMSIndex idx_diIdOrig;
	public static SDMSIndex idx_ddId_dependentId_RequiredId;
	public static SDMSIndex idx_dependentId_RequiredId_state;

	public SDMSDependencyInstanceTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "DependencyInstance"));
		}
		table = (SDMSDependencyInstanceTable) this;
		SDMSDependencyInstanceTableGeneric.table = (SDMSDependencyInstanceTable) this;
		isVersioned = false;
		idx_ddId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_dependentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_requiredId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_diIdOrig = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_ddId_dependentId_RequiredId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
		idx_dependentId_RequiredId_state = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
	}
	public SDMSDependencyInstance create(SystemEnvironment env
	                                     ,Long p_ddId
	                                     ,Long p_dependentId
	                                     ,Long p_dependentIdOrig
	                                     ,Integer p_dependencyOperation
	                                     ,Long p_requiredId
	                                     ,Integer p_state
	                                     ,Integer p_ignore
	                                     ,Long p_diIdOrig
	                                     ,Long p_seVersion
	                                    )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "DependencyInstance"));
		}
		validate(env
		         , p_ddId
		         , p_dependentId
		         , p_dependentIdOrig
		         , p_dependencyOperation
		         , p_requiredId
		         , p_state
		         , p_ignore
		         , p_diIdOrig
		         , p_seVersion
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSDependencyInstanceGeneric o = new SDMSDependencyInstanceGeneric(env
		                , p_ddId
		                , p_dependentId
		                , p_dependentIdOrig
		                , p_dependencyOperation
		                , p_requiredId
		                , p_state
		                , p_ignore
		                , p_diIdOrig
		                , p_seVersion
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                   );

		SDMSDependencyInstance p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSDependencyInstance)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSDependencyInstance)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSDependencyInstance p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_ddId
	                        ,Long p_dependentId
	                        ,Long p_dependentIdOrig
	                        ,Integer p_dependencyOperation
	                        ,Long p_requiredId
	                        ,Integer p_state
	                        ,Integer p_ignore
	                        ,Long p_diIdOrig
	                        ,Long p_seVersion
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSDependencyInstanceGeneric.checkDependencyOperation(p_dependencyOperation))

			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyInstance: $1 $2", "dependencyOperation", p_dependencyOperation));
		if (!SDMSDependencyInstanceGeneric.checkState(p_state))

			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyInstance: $1 $2", "state", p_state));
		if (!SDMSDependencyInstanceGeneric.checkIgnore(p_ignore))

			throw new FatalException(new SDMSMessage(env, "01110182023", "DependencyInstance: $1 $2", "ignore", p_ignore));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long ddId;
		Long dependentId;
		Long dependentIdOrig;
		Integer dependencyOperation;
		Long requiredId;
		Integer state;
		Integer ignore;
		Long diIdOrig;
		Long seVersion;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			ddId = new Long (r.getLong(2));
			dependentId = new Long (r.getLong(3));
			dependentIdOrig = new Long (r.getLong(4));
			dependencyOperation = new Integer (r.getInt(5));
			requiredId = new Long (r.getLong(6));
			state = new Integer (r.getInt(7));
			ignore = new Integer (r.getInt(8));
			diIdOrig = new Long (r.getLong(9));
			seVersion = new Long (r.getLong(10));
			creatorUId = new Long (r.getLong(11));
			createTs = new Long (r.getLong(12));
			changerUId = new Long (r.getLong(13));
			changeTs = new Long (r.getLong(14));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "DependencyInstance: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSDependencyInstanceGeneric(id,
		                ddId,
		                dependentId,
		                dependentIdOrig,
		                dependencyOperation,
		                requiredId,
		                state,
		                ignore,
		                diIdOrig,
		                seVersion,
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
		                                   ", " + squote + "DD_ID" + equote +
		                                   ", " + squote + "DEPENDENT_ID" + equote +
		                                   ", " + squote + "DEPENDENT_ID_ORIG" + equote +
		                                   ", " + squote + "DEPENDENCY_OPERATION" + equote +
		                                   ", " + squote + "REQUIRED_ID" + equote +
		                                   ", " + squote + "STATE" + equote +
		                                   ", " + squote + "IGNORE" + equote +
		                                   ", " + squote + "DI_ID_ORIG" + equote +
		                                   ", " + squote + "SE_VERSION" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   "  FROM " + tableName() + ", " +
		                                   "       MASTER_STATE " +
		                                   " WHERE DEPENDENT_ID = MS_C_ID" +
		                                   "   AND (MS_M_STATE NOT IN (" + SDMSSubmittedEntity.CANCELLED + "," + SDMSSubmittedEntity.FINAL + ") OR" +
		                                   "       MS_M_FINAL_Ts >= " + (postgres ?
		                                                   "           CAST (\'" + env.lowestActiveDate + "\' AS DECIMAL)" :
		                                                   "           " + env.lowestActiveDate) + ")" +
		                                   ""		);
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
		idx_ddId.put(env, ((SDMSDependencyInstanceGeneric) o).ddId, o);
		idx_dependentId.put(env, ((SDMSDependencyInstanceGeneric) o).dependentId, o);
		idx_requiredId.put(env, ((SDMSDependencyInstanceGeneric) o).requiredId, o);
		idx_diIdOrig.put(env, ((SDMSDependencyInstanceGeneric) o).diIdOrig, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyInstanceGeneric) o).ddId);
		k.add(((SDMSDependencyInstanceGeneric) o).dependentId);
		k.add(((SDMSDependencyInstanceGeneric) o).requiredId);
		idx_ddId_dependentId_RequiredId.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSDependencyInstanceGeneric) o).dependentId);
		k.add(((SDMSDependencyInstanceGeneric) o).requiredId);
		k.add(((SDMSDependencyInstanceGeneric) o).state);
		idx_dependentId_RequiredId_state.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_ddId.remove(env, ((SDMSDependencyInstanceGeneric) o).ddId, o);
		idx_dependentId.remove(env, ((SDMSDependencyInstanceGeneric) o).dependentId, o);
		idx_requiredId.remove(env, ((SDMSDependencyInstanceGeneric) o).requiredId, o);
		idx_diIdOrig.remove(env, ((SDMSDependencyInstanceGeneric) o).diIdOrig, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSDependencyInstanceGeneric) o).ddId);
		k.add(((SDMSDependencyInstanceGeneric) o).dependentId);
		k.add(((SDMSDependencyInstanceGeneric) o).requiredId);
		idx_ddId_dependentId_RequiredId.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSDependencyInstanceGeneric) o).dependentId);
		k.add(((SDMSDependencyInstanceGeneric) o).requiredId);
		k.add(((SDMSDependencyInstanceGeneric) o).state);
		idx_dependentId_RequiredId_state.remove(env, k, o);
	}

	public static SDMSDependencyInstance getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSDependencyInstance) table.get(env, id);
	}

	public static SDMSDependencyInstance getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSDependencyInstance) table.get(env, id, version);
	}

	public static SDMSDependencyInstance idx_ddId_dependentId_RequiredId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSDependencyInstance)  SDMSDependencyInstanceTableGeneric.idx_ddId_dependentId_RequiredId.getUnique(env, key);
	}

	public static SDMSDependencyInstance idx_ddId_dependentId_RequiredId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSDependencyInstance)  SDMSDependencyInstanceTableGeneric.idx_ddId_dependentId_RequiredId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
