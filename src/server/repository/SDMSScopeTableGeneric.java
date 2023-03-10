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

public class SDMSScopeTableGeneric extends SDMSTable
{

	public final static String tableName = "SCOPE";
	public static SDMSScopeTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "OWNER_ID"
		, "PARENT_ID"
		, "TYPE"
		, "IS_TERMINATE"
		, "HAS_ALTEREDCONFIG"
		, "IS_SUSPENDED"
		, "IS_ENABLED"
		, "IS_REGISTERED"
		, "STATE"
		, "PASSWD"
		, "SALT"
		, "METHOD"
		, "PID"
		, "NODE"
		, "ERRMSG"
		, "LAST_ACTIVE"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
		, "INHERIT_PRIVS"
	};
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_parentId;
	public static SDMSIndex idx_type;
	public static SDMSIndex idx_node;
	public static SDMSIndex idx_parentId_name;

	public SDMSScopeTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "Scope"));
		}
		table = (SDMSScopeTable) this;
		SDMSScopeTableGeneric.table = (SDMSScopeTable) this;
		isVersioned = false;
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_parentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId");
		idx_type = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "type");
		idx_node = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "node");
		idx_parentId_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "parentId_name");
	}
	public SDMSScope create(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_ownerId
	                        ,Long p_parentId
	                        ,Integer p_type
	                        ,Boolean p_isTerminate
	                        ,Boolean p_hasAlteredConfig
	                        ,Boolean p_isSuspended
	                        ,Boolean p_isEnabled
	                        ,Boolean p_isRegistered
	                        ,Integer p_state
	                        ,String p_passwd
	                        ,String p_salt
	                        ,Integer p_method
	                        ,String p_pid
	                        ,String p_node
	                        ,String p_errmsg
	                        ,Long p_lastActive
	                        ,Long p_inheritPrivs
	                       )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "Scope"));
		}
		validate(env
		         , p_name
		         , p_ownerId
		         , p_parentId
		         , p_type
		         , p_isTerminate
		         , p_hasAlteredConfig
		         , p_isSuspended
		         , p_isEnabled
		         , p_isRegistered
		         , p_state
		         , p_passwd
		         , p_salt
		         , p_method
		         , p_pid
		         , p_node
		         , p_errmsg
		         , p_lastActive
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		         , p_inheritPrivs
		        );

		env.tx.beginSubTransaction(env);
		SDMSScopeGeneric o = new SDMSScopeGeneric(env
		                , p_name
		                , p_ownerId
		                , p_parentId
		                , p_type
		                , p_isTerminate
		                , p_hasAlteredConfig
		                , p_isSuspended
		                , p_isEnabled
		                , p_isRegistered
		                , p_state
		                , p_passwd
		                , p_salt
		                , p_method
		                , p_pid
		                , p_node
		                , p_errmsg
		                , p_lastActive
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                , p_inheritPrivs
		                                         );

		SDMSScope p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSScope)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSScope)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSScope p)
	throws SDMSException
	{
		final Long parentId = p.getParentId(env);
		final SDMSScope parent = SDMSScopeTable.getObject(env, parentId);
		if(!parent.checkPrivileges(env, SDMSPrivilege.CREATE_CONTENT))
			return false;
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_ownerId
	                        ,Long p_parentId
	                        ,Integer p_type
	                        ,Boolean p_isTerminate
	                        ,Boolean p_hasAlteredConfig
	                        ,Boolean p_isSuspended
	                        ,Boolean p_isEnabled
	                        ,Boolean p_isRegistered
	                        ,Integer p_state
	                        ,String p_passwd
	                        ,String p_salt
	                        ,Integer p_method
	                        ,String p_pid
	                        ,String p_node
	                        ,String p_errmsg
	                        ,Long p_lastActive
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                        ,Long p_inheritPrivs
	                       )
	throws SDMSException
	{
		if (!SDMSScopeGeneric.checkType(p_type))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Scope: $1 $2", "type", p_type));
		if (!SDMSScopeGeneric.checkState(p_state))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Scope: $1 $2", "state", p_state));
		if (!SDMSScopeGeneric.checkMethod(p_method))
			throw new FatalException(new SDMSMessage(env, "01110182023", "Scope: $1 $2", "method", p_method));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		String name;
		Long ownerId;
		Long parentId;
		Integer type;
		Boolean isTerminate;
		Boolean hasAlteredConfig;
		Boolean isSuspended;
		Boolean isEnabled;
		Boolean isRegistered;
		Integer state;
		String passwd;
		String salt;
		Integer method;
		String pid;
		String node;
		String errmsg;
		Long lastActive;
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
			parentId = new Long (r.getLong(4));
			if (r.wasNull()) parentId = null;
			type = new Integer (r.getInt(5));
			isTerminate = new Boolean ((r.getInt(6) == 0 ? false : true));
			if (r.wasNull()) isTerminate = null;
			hasAlteredConfig = new Boolean ((r.getInt(7) == 0 ? false : true));
			if (r.wasNull()) hasAlteredConfig = null;
			isSuspended = new Boolean ((r.getInt(8) == 0 ? false : true));
			if (r.wasNull()) isSuspended = null;
			isEnabled = new Boolean ((r.getInt(9) == 0 ? false : true));
			if (r.wasNull()) isEnabled = null;
			isRegistered = new Boolean ((r.getInt(10) == 0 ? false : true));
			if (r.wasNull()) isRegistered = null;
			state = new Integer (r.getInt(11));
			if (r.wasNull()) state = null;
			passwd = r.getString(12);
			if (r.wasNull()) passwd = null;
			salt = r.getString(13);
			if (r.wasNull()) salt = null;
			method = new Integer (r.getInt(14));
			pid = r.getString(15);
			if (r.wasNull()) pid = null;
			node = r.getString(16);
			if (r.wasNull()) node = null;
			errmsg = r.getString(17);
			if (r.wasNull()) errmsg = null;
			lastActive = new Long (r.getLong(18));
			if (r.wasNull()) lastActive = null;
			creatorUId = new Long (r.getLong(19));
			createTs = new Long (r.getLong(20));
			changerUId = new Long (r.getLong(21));
			changeTs = new Long (r.getLong(22));
			inheritPrivs = new Long (r.getLong(23));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "Scope: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSScopeGeneric(id,
		                            name,
		                            ownerId,
		                            parentId,
		                            type,
		                            isTerminate,
		                            hasAlteredConfig,
		                            isSuspended,
		                            isEnabled,
		                            isRegistered,
		                            state,
		                            passwd,
		                            salt,
		                            method,
		                            pid,
		                            node,
		                            errmsg,
		                            lastActive,
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
		                                   squote + tableName() + equote + ".ID" +
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "OWNER_ID" + equote +
		                                   ", " + squote + "PARENT_ID" + equote +
		                                   ", " + squote + "TYPE" + equote +
		                                   ", " + squote + "IS_TERMINATE" + equote +
		                                   ", " + squote + "HAS_ALTEREDCONFIG" + equote +
		                                   ", " + squote + "IS_SUSPENDED" + equote +
		                                   ", " + squote + "IS_ENABLED" + equote +
		                                   ", " + squote + "IS_REGISTERED" + equote +
		                                   ", " + squote + "STATE" + equote +
		                                   ", " + squote + "PASSWD" + equote +
		                                   ", " + squote + "SALT" + equote +
		                                   ", " + squote + "METHOD" + equote +
		                                   ", " + squote + "PID" + equote +
		                                   ", " + squote + "NODE" + equote +
		                                   ", " + squote + "ERRMSG" + equote +
		                                   ", " + squote + "LAST_ACTIVE" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", " + squote + "INHERIT_PRIVS" + equote +
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
		ok =  idx_ownerId.check(((SDMSScopeGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_parentId.check(((SDMSScopeGeneric) o).parentId, o);
		out = out + "idx_parentId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_type.check(((SDMSScopeGeneric) o).type, o);
		out = out + "idx_type: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_node.check(((SDMSScopeGeneric) o).node, o);
		out = out + "idx_node: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScopeGeneric) o).parentId);
		k.add(((SDMSScopeGeneric) o).name);
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
		idx_ownerId.put(env, ((SDMSScopeGeneric) o).ownerId, o, ((1 & indexMember) != 0));
		idx_parentId.put(env, ((SDMSScopeGeneric) o).parentId, o, ((2 & indexMember) != 0));
		idx_type.put(env, ((SDMSScopeGeneric) o).type, o, ((4 & indexMember) != 0));
		idx_node.put(env, ((SDMSScopeGeneric) o).node, o, ((8 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScopeGeneric) o).parentId);
		k.add(((SDMSScopeGeneric) o).name);
		idx_parentId_name.put(env, k, o, ((16 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_ownerId.remove(env, ((SDMSScopeGeneric) o).ownerId, o);
		idx_parentId.remove(env, ((SDMSScopeGeneric) o).parentId, o);
		idx_type.remove(env, ((SDMSScopeGeneric) o).type, o);
		idx_node.remove(env, ((SDMSScopeGeneric) o).node, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSScopeGeneric) o).parentId);
		k.add(((SDMSScopeGeneric) o).name);
		idx_parentId_name.remove(env, k, o);
	}

	public static SDMSScope getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSScope) table.get(env, id);
	}

	public static SDMSScope getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSScope) table.getForUpdate(env, id);
	}

	public static SDMSScope getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSScope) table.get(env, id, version);
	}

	public static SDMSScope idx_parentId_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSScope)  SDMSScopeTableGeneric.idx_parentId_name.getUnique(env, key);
	}

	public static SDMSScope idx_parentId_name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSScope)  SDMSScopeTableGeneric.idx_parentId_name.getUniqueForUpdate(env, key);
	}

	public static SDMSScope idx_parentId_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSScope)  SDMSScopeTableGeneric.idx_parentId_name.getUnique(env, key, version);
	}

	public static Long pathToId(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		return getScope(sysEnv, path).getId(sysEnv);
	}

	public static Long pathToIdForUpdate(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		return getScopeForUpdate(sysEnv, path).getId(sysEnv);
	}

	public static SDMSScope getScope(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSScope f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSScope) (SDMSScopeTable.idx_parentId_name.getUnique(sysEnv, new SDMSKey(parentId, s)));
			parentId = f.getId(sysEnv);
		}
		return f;
	}

	public static SDMSScope getScopeForUpdate(SystemEnvironment sysEnv, Vector path)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSScope f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSScope) (SDMSScopeTable.idx_parentId_name.getUniqueForUpdate(sysEnv, new SDMSKey(parentId, s)));
			parentId = f.getId(sysEnv);
		}
		return f;
	}

	public static Long pathToId(SystemEnvironment sysEnv, Vector path, long version)
	throws SDMSException
	{
		return getScope(sysEnv, path, version).getId(sysEnv);
	}

	public static SDMSScope getScope(SystemEnvironment sysEnv, Vector path, long version)
	throws SDMSException
	{
		Long   parentId = null;
		SDMSScope f = null;
		String s;
		int i;

		for(i=0; i<path.size(); ++i) {
			s = (String) path.get(i);
			f = (SDMSScope) (SDMSScopeTable.idx_parentId_name.getUnique(sysEnv, new SDMSKey(parentId, s), version));
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
