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

	public final static String __version = "SDMSAuditTrailTableGeneric $Revision: 2.10 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "AUDIT_TRAIL";
	public static SDMSAuditTrailTable table  = null;

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
		idx_userId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_objectId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_originId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
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
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSAuditTrail)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSAuditTrail)(o.toProxy());
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
		                                   "ID" +
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
		                                   " FROM " + tableName() + ", " +
		                                   "       MASTER_STATE " +
		                                   " WHERE OBJECT_ID = MS_C_ID" +
		                                   "   AND (MS_M_STATE NOT IN (" + SDMSSubmittedEntity.CANCELLED + "," + SDMSSubmittedEntity.FINAL + ") OR" +
		                                   "       MS_M_FINAL_TS >= " + (postgres ?
		                                                   "           CAST (\'" + env.lowestActiveDate + "\' AS DECIMAL)" :
		                                                   "           " + env.lowestActiveDate) + ")" +
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
		idx_userId.put(env, ((SDMSAuditTrailGeneric) o).userId, o);
		idx_objectId.put(env, ((SDMSAuditTrailGeneric) o).objectId, o);
		idx_originId.put(env, ((SDMSAuditTrailGeneric) o).originId, o);
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

	public static SDMSAuditTrail getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSAuditTrail) table.get(env, id, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
