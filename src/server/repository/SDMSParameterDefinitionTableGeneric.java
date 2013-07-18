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

public class SDMSParameterDefinitionTableGeneric extends SDMSTable
{

	public final static String __version = "SDMSParameterDefinitionTableGeneric $Revision: 2.11 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "PARAMETER_DEFINITION";
	public static SDMSParameterDefinitionTable table  = null;

	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_linkPdId;
	public static SDMSIndex idx_seId_Name;

	public SDMSParameterDefinitionTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ParameterDefinition"));
		}
		table = (SDMSParameterDefinitionTable) this;
		SDMSParameterDefinitionTableGeneric.table = (SDMSParameterDefinitionTable) this;
		isVersioned = true;
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_linkPdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_seId_Name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSParameterDefinition create(SystemEnvironment env
	                                      ,Long p_seId
	                                      ,String p_name
	                                      ,Integer p_type
	                                      ,Integer p_aggFunction
	                                      ,String p_defaultValue
	                                      ,Boolean p_isLocal
	                                      ,Long p_linkPdId
	                                     )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ParameterDefinition"));
		}
		validate(env
		         , p_seId
		         , p_name
		         , p_type
		         , p_aggFunction
		         , p_defaultValue
		         , p_isLocal
		         , p_linkPdId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSParameterDefinitionGeneric o = new SDMSParameterDefinitionGeneric(env
		                , p_seId
		                , p_name
		                , p_type
		                , p_aggFunction
		                , p_defaultValue
		                , p_isLocal
		                , p_linkPdId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                     );

		SDMSParameterDefinition p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSParameterDefinition)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSParameterDefinition)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSParameterDefinition p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_seId
	                        ,String p_name
	                        ,Integer p_type
	                        ,Integer p_aggFunction
	                        ,String p_defaultValue
	                        ,Boolean p_isLocal
	                        ,Long p_linkPdId
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSParameterDefinitionGeneric.checkType(p_type))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ParameterDefinition: $1 $2", "type", p_type));
		if (!SDMSParameterDefinitionGeneric.checkAggFunction(p_aggFunction))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ParameterDefinition: $1 $2", "aggFunction", p_aggFunction));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long seId;
		String name;
		Integer type;
		Integer aggFunction;
		String defaultValue;
		Boolean isLocal;
		Long linkPdId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			seId = new Long (r.getLong(2));
			name = r.getString(3);
			type = new Integer (r.getInt(4));
			aggFunction = new Integer (r.getInt(5));
			defaultValue = r.getString(6);
			if (r.wasNull()) defaultValue = null;
			isLocal = new Boolean ((r.getInt(7) == 0 ? false : true));
			linkPdId = new Long (r.getLong(8));
			if (r.wasNull()) linkPdId = null;
			creatorUId = new Long (r.getLong(9));
			createTs = new Long (r.getLong(10));
			changerUId = new Long (r.getLong(11));
			changeTs = new Long (r.getLong(12));
			validFrom = r.getLong(13);
			validTo = r.getLong(14);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ParameterDefinition: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSParameterDefinitionGeneric(id,
		                seId,
		                name,
		                type,
		                aggFunction,
		                defaultValue,
		                isLocal,
		                linkPdId,
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
		                                   ", " + squote + "SE_ID" + equote +
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "TYPE" + equote +
		                                   ", " + squote + "AGG_FUNCTION" + equote +
		                                   ", " + squote + "DEFAULTVALUE" + equote +
		                                   ", " + squote + "IS_LOCAL" + equote +
		                                   ", " + squote + "LINK_PD_ID" + equote +
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

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_seId.put(env, ((SDMSParameterDefinitionGeneric) o).seId, o);
		idx_linkPdId.put(env, ((SDMSParameterDefinitionGeneric) o).linkPdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSParameterDefinitionGeneric) o).seId);
		k.add(((SDMSParameterDefinitionGeneric) o).name);
		idx_seId_Name.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_seId.remove(env, ((SDMSParameterDefinitionGeneric) o).seId, o);
		idx_linkPdId.remove(env, ((SDMSParameterDefinitionGeneric) o).linkPdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSParameterDefinitionGeneric) o).seId);
		k.add(((SDMSParameterDefinitionGeneric) o).name);
		idx_seId_Name.remove(env, k, o);
	}

	public static SDMSParameterDefinition getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSParameterDefinition) table.get(env, id);
	}

	public static SDMSParameterDefinition getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSParameterDefinition) table.get(env, id, version);
	}

	public static SDMSParameterDefinition idx_seId_Name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSParameterDefinition)  SDMSParameterDefinitionTableGeneric.idx_seId_Name.getUnique(env, key);
	}

	public static SDMSParameterDefinition idx_seId_Name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSParameterDefinition)  SDMSParameterDefinitionTableGeneric.idx_seId_Name.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
