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

public class SDMSUserExtentsTableGeneric extends SDMSTable
{

	public final static String tableName = "USER_EXTENTS";
	public static SDMSUserExtentsTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "UP_ID"
		, "SEQUENCE"
		, "EXTENT"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_upId;

	public SDMSUserExtentsTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "UserExtents"));
		}
		table = (SDMSUserExtentsTable) this;
		SDMSUserExtentsTableGeneric.table = (SDMSUserExtentsTable) this;
		isVersioned = false;
		idx_upId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "upId");
	}
	public SDMSUserExtents create(SystemEnvironment env
	                              ,Long p_upId
	                              ,Integer p_sequence
	                              ,String p_extent
	                             )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "UserExtents"));
		}
		validate(env
		         , p_upId
		         , p_sequence
		         , p_extent
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSUserExtentsGeneric o = new SDMSUserExtentsGeneric(env
		                , p_upId
		                , p_sequence
		                , p_extent
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                     );

		SDMSUserExtents p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSUserExtents)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSUserExtents)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSUserExtents p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_upId
	                        ,Integer p_sequence
	                        ,String p_extent
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
		Long upId;
		Integer sequence;
		String extent;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			upId = Long.valueOf (r.getLong(2));
			sequence = Integer.valueOf (r.getInt(3));
			extent = r.getString(4);
			creatorUId = Long.valueOf (r.getLong(5));
			createTs = Long.valueOf (r.getLong(6));
			changerUId = Long.valueOf (r.getLong(7));
			changeTs = Long.valueOf (r.getLong(8));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "UserExtents: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSUserExtentsGeneric(id,
		                                  upId,
		                                  sequence,
		                                  extent,
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
		                                   ", " + squote + "UP_ID" + equote +
		                                   ", " + squote + "SEQUENCE" + equote +
		                                   ", " + squote + "EXTENT" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote +
		                                   ""
					);
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
		ok =  idx_upId.check(((SDMSUserExtentsGeneric) o).upId, o);
		out = out + "idx_upId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_upId.put(env, ((SDMSUserExtentsGeneric) o).upId, o, ((1 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_upId.remove(env, ((SDMSUserExtentsGeneric) o).upId, o);
	}

	public static SDMSUserExtents getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSUserExtents) table.get(env, id);
	}

	public static SDMSUserExtents getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSUserExtents) table.getForUpdate(env, id);
	}

	public static SDMSUserExtents getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSUserExtents) table.get(env, id, version);
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
