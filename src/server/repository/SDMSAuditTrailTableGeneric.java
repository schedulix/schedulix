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

public class SDMSAuditTrailTableGeneric extends SDMSTable
{

	public final static String tableName = "AUDIT_TRAIL";
	public static SDMSAuditTrailTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "USER_ID"
		, "TS"
		, "TXID"
		, "ACTION"
		, "OBJECT_TYPE"
		, "OBJECT_ID"
		, "ORIGIN_ID"
		, "IS_SET_WARNING"
		, "ACTION_INFO"
		, "ACTION_COMMENT"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_userId;
	public static SDMSIndex idx_objectId;
	public static SDMSIndex idx_originId;

	public SDMSAuditTrailTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "AuditTrail"));
		}
		table = (SDMSAuditTrailTable) this;
		SDMSAuditTrailTableGeneric.table = (SDMSAuditTrailTable) this;
		isVersioned = false;
		idx_userId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "userId");
		idx_objectId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "objectId");
		idx_originId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "originId");
	}
	public SDMSAuditTrail create(SystemEnvironment env
	                             ,Long p_userId
	                             ,Long p_ts
	                             ,Long p_txId
	                             ,Integer p_action
	                             ,Integer p_objectType
	                             ,Long p_objectId
	                             ,Long p_originId
	                             ,Boolean p_isSetWarning
	                             ,String p_actionInfo
	                             ,String p_actionComment
	                            )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "AuditTrail"));
		}
		validate(env
		         , p_userId
		         , p_ts
		         , p_txId
		         , p_action
		         , p_objectType
		         , p_objectId
		         , p_originId
		         , p_isSetWarning
		         , p_actionInfo
		         , p_actionComment
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSAuditTrailGeneric o = new SDMSAuditTrailGeneric(env
		                , p_userId
		                , p_ts
		                , p_txId
		                , p_action
		                , p_objectType
		                , p_objectId
		                , p_originId
		                , p_isSetWarning
		                , p_actionInfo
		                , p_actionComment
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                   );

		SDMSAuditTrail p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSAuditTrail)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSAuditTrail)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSAuditTrail p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_userId
	                        ,Long p_ts
	                        ,Long p_txId
	                        ,Integer p_action
	                        ,Integer p_objectType
	                        ,Long p_objectId
	                        ,Long p_originId
	                        ,Boolean p_isSetWarning
	                        ,String p_actionInfo
	                        ,String p_actionComment
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSAuditTrailGeneric.checkAction(p_action))
			throw new FatalException(new SDMSMessage(env, "01110182023", "AuditTrail: $1 $2", "action", p_action));
		if (!SDMSAuditTrailGeneric.checkObjectType(p_objectType))
			throw new FatalException(new SDMSMessage(env, "01110182023", "AuditTrail: $1 $2", "objectType", p_objectType));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long userId;
		Long ts;
		Long txId;
		Integer action;
		Integer objectType;
		Long objectId;
		Long originId;
		Boolean isSetWarning;
		String actionInfo;
		String actionComment;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			userId = new Long (r.getLong(2));
			ts = new Long (r.getLong(3));
			txId = new Long (r.getLong(4));
			action = new Integer (r.getInt(5));
			objectType = new Integer (r.getInt(6));
			objectId = new Long (r.getLong(7));
			originId = new Long (r.getLong(8));
			isSetWarning = new Boolean ((r.getInt(9) == 0 ? false : true));
			actionInfo = r.getString(10);
			if (r.wasNull()) actionInfo = null;
			actionComment = r.getString(11);
			if (r.wasNull()) actionComment = null;
			creatorUId = new Long (r.getLong(12));
			createTs = new Long (r.getLong(13));
			changerUId = new Long (r.getLong(14));
			changeTs = new Long (r.getLong(15));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "AuditTrail: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSAuditTrailGeneric(id,
		                                 userId,
		                                 ts,
		                                 txId,
		                                 action,
		                                 objectType,
		                                 objectId,
		                                 originId,
		                                 isSetWarning,
		                                 actionInfo,
		                                 actionComment,
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
		                                   ", " + squote + "USER_ID" + equote +
		                                   ", " + squote + "TS" + equote +
		                                   ", " + squote + "TXID" + equote +
		                                   ", " + squote + "ACTION" + equote +
		                                   ", " + squote + "OBJECT_TYPE" + equote +
		                                   ", " + squote + "OBJECT_ID" + equote +
		                                   ", " + squote + "ORIGIN_ID" + equote +
		                                   ", " + squote + "IS_SET_WARNING" + equote +
		                                   ", " + squote + "ACTION_INFO" + equote +
		                                   ", " + squote + "ACTION_COMMENT" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote + ", " +
		                                   "       SME2LOAD " +
		                                   " WHERE " + squote + tableName() + equote + ".OBJECT_ID = SME2LOAD.ID"
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
		ok =  idx_userId.check(((SDMSAuditTrailGeneric) o).userId, o);
		out = out + "idx_userId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_objectId.check(((SDMSAuditTrailGeneric) o).objectId, o);
		out = out + "idx_objectId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_originId.check(((SDMSAuditTrailGeneric) o).originId, o);
		out = out + "idx_originId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_userId.put(env, ((SDMSAuditTrailGeneric) o).userId, o, ((1 & indexMember) != 0));
		idx_objectId.put(env, ((SDMSAuditTrailGeneric) o).objectId, o, ((2 & indexMember) != 0));
		idx_originId.put(env, ((SDMSAuditTrailGeneric) o).originId, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_userId.remove(env, ((SDMSAuditTrailGeneric) o).userId, o);
		idx_objectId.remove(env, ((SDMSAuditTrailGeneric) o).objectId, o);
		idx_originId.remove(env, ((SDMSAuditTrailGeneric) o).originId, o);
	}

	public static SDMSAuditTrail getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSAuditTrail) table.get(env, id);
	}

	public static SDMSAuditTrail getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSAuditTrail) table.getForUpdate(env, id);
	}

	public static SDMSAuditTrail getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSAuditTrail) table.get(env, id, version);
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
