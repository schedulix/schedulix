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

public class SDMSSubmittedEntityStatsTableGeneric extends SDMSTable
{

	public final static String tableName = "SUBMITTED_ENTITY_STATS";
	public static SDMSSubmittedEntityStatsTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "SME_ID"
		, "RERUN_SEQ"
		, "SCOPE_ID"
		, "JOB_ESD_ID"
		, "EXIT_CODE"
		, "COMMANDLINE"
		, "WORKDIR"
		, "LOGFILE"
		, "ERRLOGFILE"
		, "EXTPID"
		, "SYNC_TS"
		, "RESOURCE_TS"
		, "RUNNABLE_TS"
		, "START_TS"
		, "FINISH_TS"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_scopeId;
	public static SDMSIndex idx_smeId_rerunSeq;

	public SDMSSubmittedEntityStatsTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "SubmittedEntityStats"));
		}
		table = (SDMSSubmittedEntityStatsTable) this;
		SDMSSubmittedEntityStatsTableGeneric.table = (SDMSSubmittedEntityStatsTable) this;
		isVersioned = false;
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "smeId");
		idx_scopeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "scopeId");
		idx_smeId_rerunSeq = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "smeId_rerunSeq");
	}
	public SDMSSubmittedEntityStats create(SystemEnvironment env
	                                       ,Long p_smeId
	                                       ,Integer p_rerunSeq
	                                       ,Long p_scopeId
	                                       ,Long p_jobEsdId
	                                       ,Integer p_exitCode
	                                       ,String p_commandline
	                                       ,String p_workdir
	                                       ,String p_logfile
	                                       ,String p_errlogfile
	                                       ,String p_extPid
	                                       ,Long p_syncTs
	                                       ,Long p_resourceTs
	                                       ,Long p_runnableTs
	                                       ,Long p_startTs
	                                       ,Long p_finishTs
	                                      )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "SubmittedEntityStats"));
		}
		validate(env
		         , p_smeId
		         , p_rerunSeq
		         , p_scopeId
		         , p_jobEsdId
		         , p_exitCode
		         , p_commandline
		         , p_workdir
		         , p_logfile
		         , p_errlogfile
		         , p_extPid
		         , p_syncTs
		         , p_resourceTs
		         , p_runnableTs
		         , p_startTs
		         , p_finishTs
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSSubmittedEntityStatsGeneric o = new SDMSSubmittedEntityStatsGeneric(env
		                , p_smeId
		                , p_rerunSeq
		                , p_scopeId
		                , p_jobEsdId
		                , p_exitCode
		                , p_commandline
		                , p_workdir
		                , p_logfile
		                , p_errlogfile
		                , p_extPid
		                , p_syncTs
		                , p_resourceTs
		                , p_runnableTs
		                , p_startTs
		                , p_finishTs
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                       );

		SDMSSubmittedEntityStats p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSubmittedEntityStats)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSubmittedEntityStats)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSSubmittedEntityStats p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_smeId
	                        ,Integer p_rerunSeq
	                        ,Long p_scopeId
	                        ,Long p_jobEsdId
	                        ,Integer p_exitCode
	                        ,String p_commandline
	                        ,String p_workdir
	                        ,String p_logfile
	                        ,String p_errlogfile
	                        ,String p_extPid
	                        ,Long p_syncTs
	                        ,Long p_resourceTs
	                        ,Long p_runnableTs
	                        ,Long p_startTs
	                        ,Long p_finishTs
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
		Long smeId;
		Integer rerunSeq;
		Long scopeId;
		Long jobEsdId;
		Integer exitCode;
		String commandline;
		String workdir;
		String logfile;
		String errlogfile;
		String extPid;
		Long syncTs;
		Long resourceTs;
		Long runnableTs;
		Long startTs;
		Long finishTs;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			smeId = Long.valueOf (r.getLong(2));
			rerunSeq = Integer.valueOf (r.getInt(3));
			scopeId = Long.valueOf (r.getLong(4));
			if (r.wasNull()) scopeId = null;
			jobEsdId = Long.valueOf (r.getLong(5));
			if (r.wasNull()) jobEsdId = null;
			exitCode = Integer.valueOf (r.getInt(6));
			if (r.wasNull()) exitCode = null;
			commandline = r.getString(7);
			if (r.wasNull()) commandline = null;
			workdir = r.getString(8);
			if (r.wasNull()) workdir = null;
			logfile = r.getString(9);
			if (r.wasNull()) logfile = null;
			errlogfile = r.getString(10);
			if (r.wasNull()) errlogfile = null;
			extPid = r.getString(11);
			if (r.wasNull()) extPid = null;
			syncTs = Long.valueOf (r.getLong(12));
			if (r.wasNull()) syncTs = null;
			resourceTs = Long.valueOf (r.getLong(13));
			if (r.wasNull()) resourceTs = null;
			runnableTs = Long.valueOf (r.getLong(14));
			if (r.wasNull()) runnableTs = null;
			startTs = Long.valueOf (r.getLong(15));
			if (r.wasNull()) startTs = null;
			finishTs = Long.valueOf (r.getLong(16));
			if (r.wasNull()) finishTs = null;
			creatorUId = Long.valueOf (r.getLong(17));
			createTs = Long.valueOf (r.getLong(18));
			changerUId = Long.valueOf (r.getLong(19));
			changeTs = Long.valueOf (r.getLong(20));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "SubmittedEntityStats: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSSubmittedEntityStatsGeneric(id,
		                smeId,
		                rerunSeq,
		                scopeId,
		                jobEsdId,
		                exitCode,
		                commandline,
		                workdir,
		                logfile,
		                errlogfile,
		                extPid,
		                syncTs,
		                resourceTs,
		                runnableTs,
		                startTs,
		                finishTs,
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
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "RERUN_SEQ" + equote +
		                                   ", " + squote + "SCOPE_ID" + equote +
		                                   ", " + squote + "JOB_ESD_ID" + equote +
		                                   ", " + squote + "EXIT_CODE" + equote +
		                                   ", " + squote + "COMMANDLINE" + equote +
		                                   ", " + squote + "WORKDIR" + equote +
		                                   ", " + squote + "LOGFILE" + equote +
		                                   ", " + squote + "ERRLOGFILE" + equote +
		                                   ", " + squote + "EXTPID" + equote +
		                                   ", " + squote + "SYNC_TS" + equote +
		                                   ", " + squote + "RESOURCE_TS" + equote +
		                                   ", " + squote + "RUNNABLE_TS" + equote +
		                                   ", " + squote + "START_TS" + equote +
		                                   ", " + squote + "FINISH_TS" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   " FROM " + squote + tableName() + equote + ", " +
		                                   "       SME2LOAD " +
		                                   " WHERE " + squote + tableName() + equote + ".SME_ID = SME2LOAD.ID"
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
		ok =  idx_smeId.check(((SDMSSubmittedEntityStatsGeneric) o).smeId, o);
		out = out + "idx_smeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_scopeId.check(((SDMSSubmittedEntityStatsGeneric) o).scopeId, o);
		out = out + "idx_scopeId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityStatsGeneric) o).smeId);
		k.add(((SDMSSubmittedEntityStatsGeneric) o).rerunSeq);
		ok =  idx_smeId_rerunSeq.check(k, o);
		out = out + "idx_smeId_rerunSeq: " + (ok ? "ok" : "missing") + "\n";
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
		idx_smeId.put(env, ((SDMSSubmittedEntityStatsGeneric) o).smeId, o, ((1 & indexMember) != 0));
		idx_scopeId.put(env, ((SDMSSubmittedEntityStatsGeneric) o).scopeId, o, ((2 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityStatsGeneric) o).smeId);
		k.add(((SDMSSubmittedEntityStatsGeneric) o).rerunSeq);
		idx_smeId_rerunSeq.put(env, k, o, ((4 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_smeId.remove(env, ((SDMSSubmittedEntityStatsGeneric) o).smeId, o);
		idx_scopeId.remove(env, ((SDMSSubmittedEntityStatsGeneric) o).scopeId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityStatsGeneric) o).smeId);
		k.add(((SDMSSubmittedEntityStatsGeneric) o).rerunSeq);
		idx_smeId_rerunSeq.remove(env, k, o);
	}

	public static SDMSSubmittedEntityStats getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSubmittedEntityStats) table.get(env, id);
	}

	public static SDMSSubmittedEntityStats getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSubmittedEntityStats) table.getForUpdate(env, id);
	}

	public static SDMSSubmittedEntityStats getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSSubmittedEntityStats) table.get(env, id, version);
	}

	public static SDMSSubmittedEntityStats idx_smeId_rerunSeq_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSubmittedEntityStats)  SDMSSubmittedEntityStatsTableGeneric.idx_smeId_rerunSeq.getUnique(env, key);
	}

	public static SDMSSubmittedEntityStats idx_smeId_rerunSeq_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSubmittedEntityStats)  SDMSSubmittedEntityStatsTableGeneric.idx_smeId_rerunSeq.getUniqueForUpdate(env, key);
	}

	public static SDMSSubmittedEntityStats idx_smeId_rerunSeq_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSSubmittedEntityStats)  SDMSSubmittedEntityStatsTableGeneric.idx_smeId_rerunSeq.getUnique(env, key, version);
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
