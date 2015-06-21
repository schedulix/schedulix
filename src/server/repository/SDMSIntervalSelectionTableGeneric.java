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

public class SDMSIntervalSelectionTableGeneric extends SDMSTable
{

	public final static String tableName = "INTERVAL_SELECTION";
	public static SDMSIntervalSelectionTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "INT_ID"
		, "VALUE"
		, "PERIOD_FROM"
		, "PERIOD_TO"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_intId;

	public SDMSIntervalSelectionTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "IntervalSelection"));
		}
		table = (SDMSIntervalSelectionTable) this;
		SDMSIntervalSelectionTableGeneric.table = (SDMSIntervalSelectionTable) this;
		isVersioned = false;
		idx_intId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "intId");
	}
	public SDMSIntervalSelection create(SystemEnvironment env
	                                    ,Long p_intId
	                                    ,Integer p_value
	                                    ,Long p_periodFrom
	                                    ,Long p_periodTo
	                                   )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "IntervalSelection"));
		}
		validate(env
		         , p_intId
		         , p_value
		         , p_periodFrom
		         , p_periodTo
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSIntervalSelectionGeneric o = new SDMSIntervalSelectionGeneric(env
		                , p_intId
		                , p_value
		                , p_periodFrom
		                , p_periodTo
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                 );

		SDMSIntervalSelection p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSIntervalSelection)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSIntervalSelection)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSIntervalSelection p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_intId
	                        ,Integer p_value
	                        ,Long p_periodFrom
	                        ,Long p_periodTo
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
		Long intId;
		Integer value;
		Long periodFrom;
		Long periodTo;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			intId = new Long (r.getLong(2));
			value = new Integer (r.getInt(3));
			if (r.wasNull()) value = null;
			periodFrom = new Long (r.getLong(4));
			if (r.wasNull()) periodFrom = null;
			periodTo = new Long (r.getLong(5));
			if (r.wasNull()) periodTo = null;
			creatorUId = new Long (r.getLong(6));
			createTs = new Long (r.getLong(7));
			changerUId = new Long (r.getLong(8));
			changeTs = new Long (r.getLong(9));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "IntervalSelection: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSIntervalSelectionGeneric(id,
		                                        intId,
		                                        value,
		                                        periodFrom,
		                                        periodTo,
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
		                                   ", " + squote + "INT_ID" + equote +
		                                   ", " + squote + "VALUE" + equote +
		                                   ", " + squote + "PERIOD_FROM" + equote +
		                                   ", " + squote + "PERIOD_TO" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + tableName() +
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
		ok =  idx_intId.check(((SDMSIntervalSelectionGeneric) o).intId, o);
		out = out + "idx_intId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_intId.put(env, ((SDMSIntervalSelectionGeneric) o).intId, o, ((1 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_intId.remove(env, ((SDMSIntervalSelectionGeneric) o).intId, o);
	}

	public static SDMSIntervalSelection getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSIntervalSelection) table.get(env, id);
	}

	public static SDMSIntervalSelection getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSIntervalSelection) table.get(env, id, version);
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
