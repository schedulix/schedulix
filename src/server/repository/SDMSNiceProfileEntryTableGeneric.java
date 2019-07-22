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

public class SDMSNiceProfileEntryTableGeneric extends SDMSTable
{

	public final static String tableName = "NICE_PROFILE_ENTRY";
	public static SDMSNiceProfileEntryTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NP_ID"
		, "PREFERENCE"
		, "FOLDER_ID"
		, "IS_SUSPENDED"
		, "RENICE"
		, "IS_ACTIVE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_npId;
	public static SDMSIndex idx_folderId;

	public SDMSNiceProfileEntryTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "NiceProfileEntry"));
		}
		table = (SDMSNiceProfileEntryTable) this;
		SDMSNiceProfileEntryTableGeneric.table = (SDMSNiceProfileEntryTable) this;
		isVersioned = false;
		idx_npId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "npId");
		idx_folderId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "folderId");
	}
	public SDMSNiceProfileEntry create(SystemEnvironment env
	                                   ,Long p_npId
	                                   ,Integer p_preference
	                                   ,Long p_folderId
	                                   ,Integer p_isSuspended
	                                   ,Integer p_renice
	                                   ,Boolean p_isActive
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "NiceProfileEntry"));
		}
		validate(env
		         , p_npId
		         , p_preference
		         , p_folderId
		         , p_isSuspended
		         , p_renice
		         , p_isActive
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSNiceProfileEntryGeneric o = new SDMSNiceProfileEntryGeneric(env
		                , p_npId
		                , p_preference
		                , p_folderId
		                , p_isSuspended
		                , p_renice
		                , p_isActive
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSNiceProfileEntry p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSNiceProfileEntry)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSNiceProfileEntry)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSNiceProfileEntry p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_npId
	                        ,Integer p_preference
	                        ,Long p_folderId
	                        ,Integer p_isSuspended
	                        ,Integer p_renice
	                        ,Boolean p_isActive
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSNiceProfileEntryGeneric.checkIsSuspended(p_isSuspended))
			throw new FatalException(new SDMSMessage(env, "01110182023", "NiceProfileEntry: $1 $2", "isSuspended", p_isSuspended));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long npId;
		Integer preference;
		Long folderId;
		Integer isSuspended;
		Integer renice;
		Boolean isActive;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			npId = new Long (r.getLong(2));
			preference = new Integer (r.getInt(3));
			folderId = new Long (r.getLong(4));
			if (r.wasNull()) folderId = null;
			isSuspended = new Integer (r.getInt(5));
			renice = new Integer (r.getInt(6));
			isActive = new Boolean ((r.getInt(7) == 0 ? false : true));
			creatorUId = new Long (r.getLong(8));
			createTs = new Long (r.getLong(9));
			changerUId = new Long (r.getLong(10));
			changeTs = new Long (r.getLong(11));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "NiceProfileEntry: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSNiceProfileEntryGeneric(id,
		                                       npId,
		                                       preference,
		                                       folderId,
		                                       isSuspended,
		                                       renice,
		                                       isActive,
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
		                                   ", " + squote + "NP_ID" + equote +
		                                   ", " + squote + "PREFERENCE" + equote +
		                                   ", " + squote + "FOLDER_ID" + equote +
		                                   ", " + squote + "IS_SUSPENDED" + equote +
		                                   ", " + squote + "RENICE" + equote +
		                                   ", " + squote + "IS_ACTIVE" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote +
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
		ok =  idx_npId.check(((SDMSNiceProfileEntryGeneric) o).npId, o);
		out = out + "idx_npId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_folderId.check(((SDMSNiceProfileEntryGeneric) o).folderId, o);
		out = out + "idx_folderId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
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
		idx_npId.put(env, ((SDMSNiceProfileEntryGeneric) o).npId, o, ((1 & indexMember) != 0));
		idx_folderId.put(env, ((SDMSNiceProfileEntryGeneric) o).folderId, o, ((2 & indexMember) != 0));
		SDMSKey k;
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_npId.remove(env, ((SDMSNiceProfileEntryGeneric) o).npId, o);
		idx_folderId.remove(env, ((SDMSNiceProfileEntryGeneric) o).folderId, o);
		SDMSKey k;
	}

	public static SDMSNiceProfileEntry getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSNiceProfileEntry) table.get(env, id);
	}

	public static SDMSNiceProfileEntry getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSNiceProfileEntry) table.getForUpdate(env, id);
	}

	public static SDMSNiceProfileEntry getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSNiceProfileEntry) table.get(env, id, version);
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
