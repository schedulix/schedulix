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

public class SDMSSystemMessageTableGeneric extends SDMSTable
{

	public final static String tableName = "SYSTEM_MESSAGE";
	public static SDMSSystemMessageTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "MSG_TYPE"
		, "SME_ID"
		, "MASTER_ID"
		, "OPERATION"
		, "IS_MANDATORY"
		, "REQUEST_U_ID"
		, "REQUEST_TS"
		, "REQUEST_MSG"
		, "ADDITIONAL_LONG"
		, "ADDITIONAL_BOOL"
		, "SECOND_LONG"
		, "COMMENT"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_masterId;

	public SDMSSystemMessageTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "SystemMessage"));
		}
		table = (SDMSSystemMessageTable) this;
		SDMSSystemMessageTableGeneric.table = (SDMSSystemMessageTable) this;
		isVersioned = false;
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId");
		idx_masterId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "masterId");
	}
	public SDMSSystemMessage create(SystemEnvironment env
	                                ,Integer p_msgType
	                                ,Long p_smeId
	                                ,Long p_masterId
	                                ,Integer p_operation
	                                ,Boolean p_isMandatory
	                                ,Long p_requestUId
	                                ,Long p_requestTs
	                                ,String p_requestMsg
	                                ,Long p_additionalLong
	                                ,Boolean p_additionalBool
	                                ,Long p_secondLong
	                                ,String p_comment
	                               )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "SystemMessage"));
		}
		validate(env
		         , p_msgType
		         , p_smeId
		         , p_masterId
		         , p_operation
		         , p_isMandatory
		         , p_requestUId
		         , p_requestTs
		         , p_requestMsg
		         , p_additionalLong
		         , p_additionalBool
		         , p_secondLong
		         , p_comment
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSSystemMessageGeneric o = new SDMSSystemMessageGeneric(env
		                , p_msgType
		                , p_smeId
		                , p_masterId
		                , p_operation
		                , p_isMandatory
		                , p_requestUId
		                , p_requestTs
		                , p_requestMsg
		                , p_additionalLong
		                , p_additionalBool
		                , p_secondLong
		                , p_comment
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                         );

		SDMSSystemMessage p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSystemMessage)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSystemMessage)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSSystemMessage p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Integer p_msgType
	                        ,Long p_smeId
	                        ,Long p_masterId
	                        ,Integer p_operation
	                        ,Boolean p_isMandatory
	                        ,Long p_requestUId
	                        ,Long p_requestTs
	                        ,String p_requestMsg
	                        ,Long p_additionalLong
	                        ,Boolean p_additionalBool
	                        ,Long p_secondLong
	                        ,String p_comment
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSSystemMessageGeneric.checkMsgType(p_msgType))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SystemMessage: $1 $2", "msgType", p_msgType));
		if (!SDMSSystemMessageGeneric.checkOperation(p_operation))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SystemMessage: $1 $2", "operation", p_operation));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Integer msgType;
		Long smeId;
		Long masterId;
		Integer operation;
		Boolean isMandatory;
		Long requestUId;
		Long requestTs;
		String requestMsg;
		Long additionalLong;
		Boolean additionalBool;
		Long secondLong;
		String comment;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			msgType = Integer.valueOf (r.getInt(2));
			smeId = Long.valueOf (r.getLong(3));
			masterId = Long.valueOf (r.getLong(4));
			operation = Integer.valueOf (r.getInt(5));
			isMandatory = Boolean.valueOf ((r.getInt(6) == 0 ? false : true));
			requestUId = Long.valueOf (r.getLong(7));
			requestTs = Long.valueOf (r.getLong(8));
			requestMsg = r.getString(9);
			if (r.wasNull()) requestMsg = null;
			additionalLong = Long.valueOf (r.getLong(10));
			if (r.wasNull()) additionalLong = null;
			additionalBool = Boolean.valueOf ((r.getInt(11) == 0 ? false : true));
			if (r.wasNull()) additionalBool = null;
			secondLong = Long.valueOf (r.getLong(12));
			if (r.wasNull()) secondLong = null;
			comment = r.getString(13);
			if (r.wasNull()) comment = null;
			creatorUId = Long.valueOf (r.getLong(14));
			createTs = Long.valueOf (r.getLong(15));
			changerUId = Long.valueOf (r.getLong(16));
			changeTs = Long.valueOf (r.getLong(17));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "SystemMessage: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSSystemMessageGeneric(id,
		                                    msgType,
		                                    smeId,
		                                    masterId,
		                                    operation,
		                                    isMandatory,
		                                    requestUId,
		                                    requestTs,
		                                    requestMsg,
		                                    additionalLong,
		                                    additionalBool,
		                                    secondLong,
		                                    comment,
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
		                                   ", " + squote + "MSG_TYPE" + equote +
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "MASTER_ID" + equote +
		                                   ", " + squote + "OPERATION" + equote +
		                                   ", " + squote + "IS_MANDATORY" + equote +
		                                   ", " + squote + "REQUEST_U_ID" + equote +
		                                   ", " + squote + "REQUEST_TS" + equote +
		                                   ", " + squote + "REQUEST_MSG" + equote +
		                                   ", " + squote + "ADDITIONAL_LONG" + equote +
		                                   ", " + squote + "ADDITIONAL_BOOL" + equote +
		                                   ", " + squote + "SECOND_LONG" + equote +
		                                   ", " + squote + "COMMENT" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote +
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
		ok =  idx_smeId.check(((SDMSSystemMessageGeneric) o).smeId, o);
		out = out + "idx_smeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_masterId.check(((SDMSSystemMessageGeneric) o).masterId, o);
		out = out + "idx_masterId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_smeId.put(env, ((SDMSSystemMessageGeneric) o).smeId, o, ((1 & indexMember) != 0));
		idx_masterId.put(env, ((SDMSSystemMessageGeneric) o).masterId, o, ((2 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_smeId.remove(env, ((SDMSSystemMessageGeneric) o).smeId, o);
		idx_masterId.remove(env, ((SDMSSystemMessageGeneric) o).masterId, o);
	}

	public static SDMSSystemMessage getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSystemMessage) table.get(env, id);
	}

	public static SDMSSystemMessage getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSystemMessage) table.getForUpdate(env, id);
	}

	public static SDMSSystemMessage getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSSystemMessage) table.get(env, id, version);
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
