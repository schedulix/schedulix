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

public class SDMSExitStateMappingTableGeneric extends SDMSTable
{

	public final static String tableName = "EXIT_STATE_MAPPING";
	public static SDMSExitStateMappingTable table  = null;

	public static SDMSIndex idx_esmpId;
	public static SDMSIndex idx_esdId;
	public static SDMSIndex idx_esmpId_esdId;

	public SDMSExitStateMappingTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ExitStateMapping"));
		}
		table = (SDMSExitStateMappingTable) this;
		SDMSExitStateMappingTableGeneric.table = (SDMSExitStateMappingTable) this;
		isVersioned = true;
		idx_esmpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_esdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_esmpId_esdId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
	}
	public SDMSExitStateMapping create(SystemEnvironment env
	                                   ,Long p_esmpId
	                                   ,Long p_esdId
	                                   ,Integer p_ecrStart
	                                   ,Integer p_ecrEnd
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ExitStateMapping"));
		}
		validate(env
		         , p_esmpId
		         , p_esdId
		         , p_ecrStart
		         , p_ecrEnd
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSExitStateMappingGeneric o = new SDMSExitStateMappingGeneric(env
		                , p_esmpId
		                , p_esdId
		                , p_ecrStart
		                , p_ecrEnd
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSExitStateMapping p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSExitStateMapping)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSExitStateMapping)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSExitStateMapping p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_esmpId
	                        ,Long p_esdId
	                        ,Integer p_ecrStart
	                        ,Integer p_ecrEnd
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
		Long esmpId;
		Long esdId;
		Integer ecrStart;
		Integer ecrEnd;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			esmpId = new Long (r.getLong(2));
			esdId = new Long (r.getLong(3));
			ecrStart = new Integer (r.getInt(4));
			if (r.wasNull()) ecrStart = null;
			ecrEnd = new Integer (r.getInt(5));
			if (r.wasNull()) ecrEnd = null;
			creatorUId = new Long (r.getLong(6));
			createTs = new Long (r.getLong(7));
			changerUId = new Long (r.getLong(8));
			changeTs = new Long (r.getLong(9));
			validFrom = r.getLong(10);
			validTo = r.getLong(11);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ExitStateMapping: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSExitStateMappingGeneric(id,
		                                       esmpId,
		                                       esdId,
		                                       ecrStart,
		                                       ecrEnd,
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
		                                   ", " + squote + "ESMP_ID" + equote +
		                                   ", " + squote + "ESD_ID" + equote +
		                                   ", " + squote + "ECR_START" + equote +
		                                   ", " + squote + "ECR_END" + equote +
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
		idx_esmpId.put(env, ((SDMSExitStateMappingGeneric) o).esmpId, o);
		idx_esdId.put(env, ((SDMSExitStateMappingGeneric) o).esdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateMappingGeneric) o).esmpId);
		k.add(((SDMSExitStateMappingGeneric) o).esdId);
		idx_esmpId_esdId.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_esmpId.remove(env, ((SDMSExitStateMappingGeneric) o).esmpId, o);
		idx_esdId.remove(env, ((SDMSExitStateMappingGeneric) o).esdId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSExitStateMappingGeneric) o).esmpId);
		k.add(((SDMSExitStateMappingGeneric) o).esdId);
		idx_esmpId_esdId.remove(env, k, o);
	}

	public static SDMSExitStateMapping getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSExitStateMapping) table.get(env, id);
	}

	public static SDMSExitStateMapping getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSExitStateMapping) table.get(env, id, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
