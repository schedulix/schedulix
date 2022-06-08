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

public class SDMSExtentsTableGeneric extends SDMSTable
{

	public final static String tableName = "EXTENTS";
	public static SDMSExtentsTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "O_ID"
		, "SME_ID"
		, "SEQUENCE"
		, "EXTENT"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_oId;
	public static SDMSIndex idx_smeId;

	public SDMSExtentsTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "Extents"));
		}
		table = (SDMSExtentsTable) this;
		SDMSExtentsTableGeneric.table = (SDMSExtentsTable) this;
		isVersioned = false;
		idx_oId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "oId");
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId");
	}
	public SDMSExtents create(SystemEnvironment env
	                          ,Long p_oId
	                          ,Long p_smeId
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
			throw new FatalException(new SDMSMessage(env, "01110182049", "Extents"));
		}
		validate(env
		         , p_oId
		         , p_smeId
		         , p_sequence
		         , p_extent
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSExtentsGeneric o = new SDMSExtentsGeneric(env
		                , p_oId
		                , p_smeId
		                , p_sequence
		                , p_extent
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                             );

		SDMSExtents p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSExtents)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSExtents)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSExtents p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_oId
	                        ,Long p_smeId
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
		Long oId;
		Long smeId;
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
			oId = Long.valueOf (r.getLong(2));
			smeId = Long.valueOf (r.getLong(3));
			sequence = Integer.valueOf (r.getInt(4));
			extent = r.getString(5);
			creatorUId = Long.valueOf (r.getLong(6));
			createTs = Long.valueOf (r.getLong(7));
			changerUId = Long.valueOf (r.getLong(8));
			changeTs = Long.valueOf (r.getLong(9));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "Extents: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSExtentsGeneric(id,
		                              oId,
		                              smeId,
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
		                                   ", " + squote + "O_ID" + equote +
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "SEQUENCE" + equote +
		                                   ", " + squote + "EXTENT" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote + ", " +
		                                   "       SME2LOAD " +
		                                   " WHERE " + squote + tableName() + equote + ".SME_ID = SME2LOAD.ID"
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
		ok =  idx_oId.check(((SDMSExtentsGeneric) o).oId, o);
		out = out + "idx_oId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_smeId.check(((SDMSExtentsGeneric) o).smeId, o);
		out = out + "idx_smeId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_oId.put(env, ((SDMSExtentsGeneric) o).oId, o, ((1 & indexMember) != 0));
		idx_smeId.put(env, ((SDMSExtentsGeneric) o).smeId, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_oId.remove(env, ((SDMSExtentsGeneric) o).oId, o);
		idx_smeId.remove(env, ((SDMSExtentsGeneric) o).smeId, o);
	}

	public static SDMSExtents getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSExtents) table.get(env, id);
	}

	public static SDMSExtents getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSExtents) table.getForUpdate(env, id);
	}

	public static SDMSExtents getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSExtents) table.get(env, id, version);
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
