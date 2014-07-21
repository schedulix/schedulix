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

public class SDMSObjectCommentTableGeneric extends SDMSTable
{

	public final static String tableName = "OBJECT_COMMENT";
	public static SDMSObjectCommentTable table  = null;

	public static SDMSIndex idx_objectId;
	public static SDMSIndex idx_objectType;

	public SDMSObjectCommentTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "ObjectComment"));
		}
		table = (SDMSObjectCommentTable) this;
		SDMSObjectCommentTableGeneric.table = (SDMSObjectCommentTable) this;
		isVersioned = true;
		idx_objectId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
		idx_objectType = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
	}
	public SDMSObjectComment create(SystemEnvironment env
	                                ,Long p_objectId
	                                ,Integer p_objectType
	                                ,Integer p_infoType
	                                ,Integer p_sequenceNumber
	                                ,String p_description
	                               )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "ObjectComment"));
		}
		validate(env
		         , p_objectId
		         , p_objectType
		         , p_infoType
		         , p_sequenceNumber
		         , p_description
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSObjectCommentGeneric o = new SDMSObjectCommentGeneric(env
		                , p_objectId
		                , p_objectType
		                , p_infoType
		                , p_sequenceNumber
		                , p_description
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                         );

		SDMSObjectComment p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSObjectComment)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSObjectComment)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSObjectComment p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_objectId
	                        ,Integer p_objectType
	                        ,Integer p_infoType
	                        ,Integer p_sequenceNumber
	                        ,String p_description
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSObjectCommentGeneric.checkObjectType(p_objectType))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ObjectComment: $1 $2", "objectType", p_objectType));
		if (!SDMSObjectCommentGeneric.checkInfoType(p_infoType))

			throw new FatalException(new SDMSMessage(env, "01110182023", "ObjectComment: $1 $2", "infoType", p_infoType));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long objectId;
		Integer objectType;
		Integer infoType;
		Integer sequenceNumber;
		String description;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			objectId = new Long (r.getLong(2));
			objectType = new Integer (r.getInt(3));
			infoType = new Integer (r.getInt(4));
			sequenceNumber = new Integer (r.getInt(5));
			description = r.getString(6);
			creatorUId = new Long (r.getLong(7));
			createTs = new Long (r.getLong(8));
			changerUId = new Long (r.getLong(9));
			changeTs = new Long (r.getLong(10));
			validFrom = r.getLong(11);
			validTo = r.getLong(12);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "ObjectComment: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSObjectCommentGeneric(id,
		                                    objectId,
		                                    objectType,
		                                    infoType,
		                                    sequenceNumber,
		                                    description,
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
		if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
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
		                                   ", " + squote + "OBJECT_ID" + equote +
		                                   ", " + squote + "OBJECT_TYPE" + equote +
		                                   ", " + squote + "INFO_TYPE" + equote +
		                                   ", " + squote + "SEQUENCE_NUMBER" + equote +
		                                   ", " + squote + "DESCRIPTION" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
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

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_objectId.put(env, ((SDMSObjectCommentGeneric) o).objectId, o);
		idx_objectType.put(env, ((SDMSObjectCommentGeneric) o).objectType, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_objectId.remove(env, ((SDMSObjectCommentGeneric) o).objectId, o);
		idx_objectType.remove(env, ((SDMSObjectCommentGeneric) o).objectType, o);
	}

	public static SDMSObjectComment getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSObjectComment) table.get(env, id);
	}

	public static SDMSObjectComment getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSObjectComment) table.get(env, id, version);
	}

	public static SDMSObjectComment idx_objectId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSObjectComment) SDMSObjectCommentTableGeneric.idx_objectId.getUnique(env, key);
	}

	public static SDMSObjectComment idx_objectId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSObjectComment) SDMSObjectCommentTableGeneric.idx_objectId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
