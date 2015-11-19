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

public class SDMSFolderTableGeneric extends SDMSTable
{

	public final static String tableName = "FOLDER";
	public static SDMSFolderTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "OWNER_ID"
		, "ENV_ID"
		, "PARENT_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
		, "INHERIT_PRIVS"
	};
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_envId;
	public static SDMSIndex idx_parentId;
	public static SDMSIndex idx_parentId_name;

	public SDMSFolderTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "Folder"));
		}
		table = (SDMSFolderTable) this;
		SDMSFolderTableGeneric.table = (SDMSFolderTable) this;
		isVersioned = true;
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_envId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "envId");
		idx_parentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId");
		idx_parentId_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "parentId_name");
	}
	public SDMSFolder create(SystemEnvironment env
	                         ,String p_name
	                         ,Long p_ownerId
	                         ,Long p_envId
	                         ,Long p_parentId
	                         ,Long p_inheritPrivs
	                        )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "Folder"));
		}
		validate(env
		         , p_name
		         , p_ownerId
		         , p_envId
		         , p_parentId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		         , p_inheritPrivs
		        );

		env.tx.beginSubTransaction(env);
		SDMSFolderGeneric o = new SDMSFolderGeneric(env
		                , p_name
		                , p_ownerId
		                , p_envId
		                , p_parentId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                , p_inheritPrivs
		                                           );

		SDMSFolder p;
		try {

			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSFolder)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSFolder)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSFolder p)
	throws SDMSException
	{

		final Long parentId = p.getParentId(env);
		final SDMSFolder parent = SDMSFolderTable.getObject(env, parentId);
		if(!parent.checkPrivileges(env, SDMSPrivilege.CREATE_CONTENT))
			return false;
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_ownerId
	                        ,Long p_envId
	                        ,Long p_parentId
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                        ,Long p_inheritPrivs
	                       )
	throws SDMSException
	{

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		String name;
		Long ownerId;
		Long envId;
		Long parentId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		Long inheritPrivs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			ownerId = new Long (r.getLong(3));
			envId = new Long (r.getLong(4));
			if (r.wasNull()) envId = null;
			parentId = new Long (r.getLong(5));
			if (r.wasNull()) parentId = null;
			creatorUId = new Long (r.getLong(6));
			createTs = new Long (r.getLong(7));
			changerUId = new Long (r.getLong(8));
			changeTs = new Long (r.getLong(9));
			inheritPrivs = new Long (r.getLong(10));
			validFrom = r.getLong(11);
			validTo = r.getLong(12);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "Folder: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSFolderGeneric(id,
		                             name,
		                             ownerId,
		                             envId,
		                             parentId,
		                             creatorUId,
		                             createTs,
		                             changerUId,
		                             changeTs,
		                             inheritPrivs,
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
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "OWNER_ID" + equote +
		                                   ", " + squote + "ENV_ID" + equote +
		                                   ", " + squote + "PARENT_ID" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", " + squote + "INHERIT_PRIVS" + equote +
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

	public String checkIndex(SDMSObject o)
	throws SDMSException
	{
		String out = "";
		boolean ok;
		ok =  idx_ownerId.check(((SDMSFolderGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_envId.check(((SDMSFolderGeneric) o).envId, o);
		out = out + "idx_envId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_parentId.check(((SDMSFolderGeneric) o).parentId, o);
		out = out + "idx_parentId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSFolderGeneric) o).parentId);
		k.add(((SDMSFolderGeneric) o).name);
		ok =  idx_parentId_name.check(k, o);
		out = out + "idx_parentId_name: " + (ok ? "ok" : "missing") + "\n";
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
		idx_ownerId.put(env, ((SDMSFolderGeneric) o).ownerId, o, ((1 & indexMember) != 0));
		idx_envId.put(env, ((SDMSFolderGeneric) o).envId, o, ((2 & indexMember) != 0));
		idx_parentId.put(env, ((SDMSFolderGeneric) o).parentId, o, ((4 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSFolderGeneric) o).parentId);
		k.add(((SDMSFolderGeneric) o).name);
		idx_parentId_name.put(env, k, o, ((8 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_ownerId.remove(env, ((SDMSFolderGeneric) o).ownerId, o);
		idx_envId.remove(env, ((SDMSFolderGeneric) o).envId, o);
		idx_parentId.remove(env, ((SDMSFolderGeneric) o).parentId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSFolderGeneric) o).parentId);
		k.add(((SDMSFolderGeneric) o).name);
		idx_parentId_name.remove(env, k, o);
	}

	public static SDMSFolder getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSFolder) table.get(env, id);
	}

	public static SDMSFolder getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSFolder) table.getForUpdate(env, id);
	}

	public static SDMSFolder getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSFolder) table.get(env, id, version);
	}

	public static SDMSFolder idx_parentId_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSFolder)  SDMSFolderTableGeneric.idx_parentId_name.getUnique(env, key);
	}

	public static SDMSFolder idx_parentId_name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSFolder)  SDMSFolderTableGeneric.idx_parentId_name.getUniqueForUpdate(env, key);
	}

	public static SDMSFolder idx_parentId_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSFolder)  SDMSFolderTableGeneric.idx_parentId_name.getUnique(env, key, version);
	}

	public static Long pathToId(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		return getFolder(sysEnv, path).getId(sysEnv);
	}

	public static Long pathToIdForUpdate(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		return getFolderForUpdate(sysEnv, path).getId(sysEnv);
	}

	public static SDMSFolder getFolder(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSFolder f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSFolder) (SDMSFolderTable.idx_parentId_name.getUnique(sysEnv, new SDMSKey(parentId, s)));
			parentId = f.getId(sysEnv);
		}
		return f;
	}

	public static SDMSFolder getFolderForUpdate(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSFolder f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSFolder) (SDMSFolderTable.idx_parentId_name.getUniqueForUpdate(sysEnv, new SDMSKey(parentId, s)));
			parentId = f.getId(sysEnv);
		}
		return f;
	}

	public static Long pathToId(SystemEnvironment sysEnv, Vector path, long version)
	throws SDMSException
	{
		return getFolder(sysEnv, path, version).getId(sysEnv);
	}

	public static SDMSFolder getFolder(SystemEnvironment sysEnv, Vector path, long version)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSFolder f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSFolder) (SDMSFolderTable.idx_parentId_name.getUnique(sysEnv, new SDMSKey(parentId, s), version));
			parentId = f.getId(sysEnv);
		}
		return f;
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
