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

public class SDMSUserTableGeneric extends SDMSTable
{

	public final static String tableName = "USERS";
	public static SDMSUserTable table  = null;

	public static SDMSIndex idx_name;
	public static SDMSIndex idx_name_deleteVersion;

	public SDMSUserTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "User"));
		}
		table = (SDMSUserTable) this;
		SDMSUserTableGeneric.table = (SDMSUserTable) this;
		isVersioned = false;
		idx_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
		idx_name_deleteVersion = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSUser create(SystemEnvironment env
	                       ,String p_name
	                       ,String p_passwd
	                       ,String p_salt
	                       ,Integer p_method
	                       ,Boolean p_isEnabled
	                       ,Long p_defaultGId
	                       ,Long p_deleteVersion
	                      )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "User"));
		}
		validate(env
		         , p_name
		         , p_passwd
		         , p_salt
		         , p_method
		         , p_isEnabled
		         , p_defaultGId
		         , p_deleteVersion
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSUserGeneric o = new SDMSUserGeneric(env
		                                        , p_name
		                                        , p_passwd
		                                        , p_salt
		                                        , p_method
		                                        , p_isEnabled
		                                        , p_defaultGId
		                                        , p_deleteVersion
		                                        , p_creatorUId
		                                        , p_createTs
		                                        , p_changerUId
		                                        , p_changeTs
		                                       );

		SDMSUser p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSUser)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSUser)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSUser p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,String p_passwd
	                        ,String p_salt
	                        ,Integer p_method
	                        ,Boolean p_isEnabled
	                        ,Long p_defaultGId
	                        ,Long p_deleteVersion
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSUserGeneric.checkMethod(p_method))

			throw new FatalException(new SDMSMessage(env, "01110182023", "User: $1 $2", "method", p_method));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		String name;
		String passwd;
		String salt;
		Integer method;
		Boolean isEnabled;
		Long defaultGId;
		Long deleteVersion;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			name = r.getString(2);
			passwd = r.getString(3);
			salt = r.getString(4);
			if (r.wasNull()) salt = null;
			method = new Integer (r.getInt(5));
			isEnabled = new Boolean ((r.getInt(6) == 0 ? false : true));
			defaultGId = new Long (r.getLong(7));
			deleteVersion = new Long (r.getLong(8));
			creatorUId = new Long (r.getLong(9));
			createTs = new Long (r.getLong(10));
			changerUId = new Long (r.getLong(11));
			changeTs = new Long (r.getLong(12));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "User: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSUserGeneric(id,
		                           name,
		                           passwd,
		                           salt,
		                           method,
		                           isEnabled,
		                           defaultGId,
		                           deleteVersion,
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
		                                   tableName() + ".ID" +
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "PASSWD" + equote +
		                                   ", " + squote + "SALT" + equote +
		                                   ", " + squote + "METHOD" + equote +
		                                   ", " + squote + "IS_ENABLED" + equote +
		                                   ", " + squote + "DEFAULT_G_ID" + equote +
		                                   ", " + squote + "DELETE_VERSION" + equote +
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

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_name.put(env, ((SDMSUserGeneric) o).name, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSUserGeneric) o).name);
		k.add(((SDMSUserGeneric) o).deleteVersion);
		idx_name_deleteVersion.put(env, k, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_name.remove(env, ((SDMSUserGeneric) o).name, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSUserGeneric) o).name);
		k.add(((SDMSUserGeneric) o).deleteVersion);
		idx_name_deleteVersion.remove(env, k, o);
	}

	public static SDMSUser getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSUser) table.get(env, id);
	}

	public static SDMSUser getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSUser) table.get(env, id, version);
	}

	public static SDMSUser idx_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSUser) SDMSUserTableGeneric.idx_name.getUnique(env, key);
	}

	public static SDMSUser idx_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSUser) SDMSUserTableGeneric.idx_name.getUnique(env, key, version);
	}

	public static SDMSUser idx_name_deleteVersion_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSUser)  SDMSUserTableGeneric.idx_name_deleteVersion.getUnique(env, key);
	}

	public static SDMSUser idx_name_deleteVersion_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSUser)  SDMSUserTableGeneric.idx_name_deleteVersion.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
