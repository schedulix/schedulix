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

public class SDMSMemberTableGeneric extends SDMSTable
{

	public final static String tableName = "MEMBER";
	public static SDMSMemberTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "G_ID"
		, "U_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_gId;
	public static SDMSIndex idx_uId;
	public static SDMSIndex idx_gId_uId;

	public SDMSMemberTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "Member"));
		}
		table = (SDMSMemberTable) this;
		SDMSMemberTableGeneric.table = (SDMSMemberTable) this;
		isVersioned = false;
		idx_gId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "gId");
		idx_uId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "uId");
		idx_gId_uId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "gId_uId");
	}
	public SDMSMember create(SystemEnvironment env
	                         ,Long p_gId
	                         ,Long p_uId
	                        )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "Member"));
		}
		validate(env
		         , p_gId
		         , p_uId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSMemberGeneric o = new SDMSMemberGeneric(env
		                , p_gId
		                , p_uId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                           );

		SDMSMember p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSMember)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSMember)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSMember p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_gId
	                        ,Long p_uId
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
		Long gId;
		Long uId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			gId = new Long (r.getLong(2));
			uId = new Long (r.getLong(3));
			creatorUId = new Long (r.getLong(4));
			createTs = new Long (r.getLong(5));
			changerUId = new Long (r.getLong(6));
			changeTs = new Long (r.getLong(7));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "Member: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSMemberGeneric(id,
		                             gId,
		                             uId,
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
		                                   ", " + squote + "G_ID" + equote +
		                                   ", " + squote + "U_ID" + equote +
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
		ok =  idx_gId.check(((SDMSMemberGeneric) o).gId, o);
		out = out + "idx_gId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_uId.check(((SDMSMemberGeneric) o).uId, o);
		out = out + "idx_uId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSMemberGeneric) o).gId);
		k.add(((SDMSMemberGeneric) o).uId);
		ok =  idx_gId_uId.check(k, o);
		out = out + "idx_gId_uId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_gId.put(env, ((SDMSMemberGeneric) o).gId, o, ((1 & indexMember) != 0));
		idx_uId.put(env, ((SDMSMemberGeneric) o).uId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSMemberGeneric) o).gId);
		k.add(((SDMSMemberGeneric) o).uId);
		idx_gId_uId.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_gId.remove(env, ((SDMSMemberGeneric) o).gId, o);
		idx_uId.remove(env, ((SDMSMemberGeneric) o).uId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSMemberGeneric) o).gId);
		k.add(((SDMSMemberGeneric) o).uId);
		idx_gId_uId.remove(env, k, o);
	}

	public static SDMSMember getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSMember) table.get(env, id);
	}

	public static SDMSMember getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSMember) table.get(env, id, version);
	}

	public static SDMSMember idx_gId_uId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSMember)  SDMSMemberTableGeneric.idx_gId_uId.getUnique(env, key);
	}

	public static SDMSMember idx_gId_uId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSMember)  SDMSMemberTableGeneric.idx_gId_uId.getUnique(env, key, version);
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
