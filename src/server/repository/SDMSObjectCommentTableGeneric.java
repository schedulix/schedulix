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

	public final static String[] columnNames = {
		"ID"
		, "OBJECT_ID"
		, "OBJECT_TYPE"
		, "INFO_TYPE"
		, "SEQUENCE_NUMBER"
		, "TAG"
		, "DESCRIPTION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
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
		idx_objectId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "objectId");
		idx_objectType = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "objectType");
	}
	public SDMSObjectComment create(SystemEnvironment env
	                                ,Long p_objectId
	                                ,Integer p_objectType
	                                ,Integer p_infoType
	                                ,Integer p_sequenceNumber
	                                ,String p_tag
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
		         , p_tag
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
		                , p_tag
		                , p_description
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                         );

		SDMSObjectComment p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSObjectComment)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSObjectComment)(o.toProxy(env));
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
	                        ,String p_tag
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
		String tag;
		String description;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			objectId = Long.valueOf (r.getLong(2));
			objectType = Integer.valueOf (r.getInt(3));
			infoType = Integer.valueOf (r.getInt(4));
			sequenceNumber = Integer.valueOf (r.getInt(5));
			tag = r.getString(6);
			if (r.wasNull()) tag = null;
			description = r.getString(7);
			creatorUId = Long.valueOf (r.getLong(8));
			createTs = Long.valueOf (r.getLong(9));
			changerUId = Long.valueOf (r.getLong(10));
			changeTs = Long.valueOf (r.getLong(11));
			validFrom = r.getLong(12);
			validTo = r.getLong(13);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "ObjectComment: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSObjectCommentGeneric(id,
		                                    objectId,
		                                    objectType,
		                                    infoType,
		                                    sequenceNumber,
		                                    tag,
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

		final boolean postgres = SystemEnvironment.isPostgreSQL;
		String squote = SystemEnvironment.SQUOTE;
		String equote = SystemEnvironment.EQUOTE;
		Statement stmt = env.dbConnection.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   squote + tableName() + equote + ".ID" +
		                                   ", " + squote + "OBJECT_ID" + equote +
		                                   ", " + squote + "OBJECT_TYPE" + equote +
		                                   ", " + squote + "INFO_TYPE" + equote +
		                                   ", " + squote + "SEQUENCE_NUMBER" + equote +
		                                   ", " + squote + "TAG" + equote +
		                                   ", " + squote + "DESCRIPTION" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", VALID_FROM, VALID_TO " +
		                                   " FROM " + squote + tableName() + equote +
		                                   " WHERE VALID_TO >= " + (postgres ?
		                                                   "CAST (\'" + env.lowestActiveVersion + "\' AS DECIMAL)" :
		                                                   "" + env.lowestActiveVersion) +
		                                   ""						  );
		while(rset.next()) {
			try {
				if(loadObject(env, rset)) ++loaded;
				++read;
			} catch (Exception e) {
				SDMSThread.doTrace(null, "Exception caught while loading table " + tableName() + ", ID = " + Long.valueOf (rset.getLong(1)), SDMSThread.SEVERITY_ERROR);
				throw(e);
			}
		}
		stmt.close();
		SDMSThread.doTrace(null, "Read " + read + ", Loaded " + loaded + " rows for " + tableName(), SDMSThread.SEVERITY_INFO);
	}

	public String checkIndex(SDMSObject o)
	throws SDMSException
	{
		String out = "";
		boolean ok;
		ok =  idx_objectId.check(((SDMSObjectCommentGeneric) o).objectId, o);
		out = out + "idx_objectId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_objectType.check(((SDMSObjectCommentGeneric) o).objectType, o);
		out = out + "idx_objectType: " + (ok ? "ok" : "missing") + "\n";
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
		idx_objectId.put(env, ((SDMSObjectCommentGeneric) o).objectId, o, ((1 & indexMember) != 0));
		idx_objectType.put(env, ((SDMSObjectCommentGeneric) o).objectType, o, ((2 & indexMember) != 0));
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

	public static SDMSObjectComment getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSObjectComment) table.getForUpdate(env, id);
	}

	public static SDMSObjectComment getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSObjectComment) table.get(env, id, version);
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
