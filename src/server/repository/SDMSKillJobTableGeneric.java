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

public class SDMSKillJobTableGeneric extends SDMSTable
{

	public final static String tableName = "KILL_JOB";
	public static SDMSKillJobTable table  = null;

	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_smeId;
	public static SDMSIndex idx_scopeId;
	public static SDMSIndex idx_state;

	public SDMSKillJobTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "KillJob"));
		}
		table = (SDMSKillJobTable) this;
		SDMSKillJobTableGeneric.table = (SDMSKillJobTable) this;
		isVersioned = false;
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_smeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_scopeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_state = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
	}
	public SDMSKillJob create(SystemEnvironment env
	                          ,Long p_seId
	                          ,Long p_seVersion
	                          ,Long p_smeId
	                          ,Long p_scopeId
	                          ,Integer p_state
	                          ,Integer p_exitCode
	                          ,String p_commandline
	                          ,String p_logfile
	                          ,String p_errlogfile
	                          ,String p_pid
	                          ,String p_extPid
	                          ,String p_errorMsg
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

			throw new FatalException(new SDMSMessage(env, "01110182049", "KillJob"));
		}
		validate(env
		         , p_seId
		         , p_seVersion
		         , p_smeId
		         , p_scopeId
		         , p_state
		         , p_exitCode
		         , p_commandline
		         , p_logfile
		         , p_errlogfile
		         , p_pid
		         , p_extPid
		         , p_errorMsg
		         , p_runnableTs
		         , p_startTs
		         , p_finishTs
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSKillJobGeneric o = new SDMSKillJobGeneric(env
		                , p_seId
		                , p_seVersion
		                , p_smeId
		                , p_scopeId
		                , p_state
		                , p_exitCode
		                , p_commandline
		                , p_logfile
		                , p_errlogfile
		                , p_pid
		                , p_extPid
		                , p_errorMsg
		                , p_runnableTs
		                , p_startTs
		                , p_finishTs
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                             );

		SDMSKillJob p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSKillJob)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSKillJob)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSKillJob p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_seId
	                        ,Long p_seVersion
	                        ,Long p_smeId
	                        ,Long p_scopeId
	                        ,Integer p_state
	                        ,Integer p_exitCode
	                        ,String p_commandline
	                        ,String p_logfile
	                        ,String p_errlogfile
	                        ,String p_pid
	                        ,String p_extPid
	                        ,String p_errorMsg
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
		if (!SDMSKillJobGeneric.checkState(p_state))

			throw new FatalException(new SDMSMessage(env, "01110182023", "KillJob: $1 $2", "state", p_state));

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long seId;
		Long seVersion;
		Long smeId;
		Long scopeId;
		Integer state;
		Integer exitCode;
		String commandline;
		String logfile;
		String errlogfile;
		String pid;
		String extPid;
		String errorMsg;
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
			id     = new Long (r.getLong(1));
			seId = new Long (r.getLong(2));
			seVersion = new Long (r.getLong(3));
			smeId = new Long (r.getLong(4));
			scopeId = new Long (r.getLong(5));
			state = new Integer (r.getInt(6));
			exitCode = new Integer (r.getInt(7));
			if (r.wasNull()) exitCode = null;
			commandline = r.getString(8);
			if (r.wasNull()) commandline = null;
			logfile = r.getString(9);
			if (r.wasNull()) logfile = null;
			errlogfile = r.getString(10);
			if (r.wasNull()) errlogfile = null;
			pid = r.getString(11);
			if (r.wasNull()) pid = null;
			extPid = r.getString(12);
			if (r.wasNull()) extPid = null;
			errorMsg = r.getString(13);
			if (r.wasNull()) errorMsg = null;
			runnableTs = new Long (r.getLong(14));
			if (r.wasNull()) runnableTs = null;
			startTs = new Long (r.getLong(15));
			if (r.wasNull()) startTs = null;
			finishTs = new Long (r.getLong(16));
			if (r.wasNull()) finishTs = null;
			creatorUId = new Long (r.getLong(17));
			createTs = new Long (r.getLong(18));
			changerUId = new Long (r.getLong(19));
			changeTs = new Long (r.getLong(20));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);

			throw new FatalException(new SDMSMessage(env, "01110182045", "KillJob: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSKillJobGeneric(id,
		                              seId,
		                              seVersion,
		                              smeId,
		                              scopeId,
		                              state,
		                              exitCode,
		                              commandline,
		                              logfile,
		                              errlogfile,
		                              pid,
		                              extPid,
		                              errorMsg,
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
		                                   ", " + squote + "SE_ID" + equote +
		                                   ", " + squote + "SE_VERSION" + equote +
		                                   ", " + squote + "SME_ID" + equote +
		                                   ", " + squote + "SCOPE_ID" + equote +
		                                   ", " + squote + "STATE" + equote +
		                                   ", " + squote + "EXIT_CODE" + equote +
		                                   ", " + squote + "COMMANDLINE" + equote +
		                                   ", " + squote + "LOGFILE" + equote +
		                                   ", " + squote + "ERRLOGFILE" + equote +
		                                   ", " + squote + "PID" + equote +
		                                   ", " + squote + "EXTPID" + equote +
		                                   ", " + squote + "ERROR_MSG" + equote +
		                                   ", " + squote + "RUNNABLE_TS" + equote +
		                                   ", " + squote + "START_TS" + equote +
		                                   ", " + squote + "FINSH_TS" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   "  FROM " + tableName() +
		                                   " WHERE SE_VERSION >= " + (postgres ?
		                                                   "CAST (\'" + env.lowestActiveVersion + "\' as DECIMAL)" :
		                                                   "" + env.lowestActiveVersion) +
		                                   ""		);
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
		idx_seId.put(env, ((SDMSKillJobGeneric) o).seId, o);
		idx_smeId.put(env, ((SDMSKillJobGeneric) o).smeId, o);
		idx_scopeId.put(env, ((SDMSKillJobGeneric) o).scopeId, o);
		idx_state.put(env, ((SDMSKillJobGeneric) o).state, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_seId.remove(env, ((SDMSKillJobGeneric) o).seId, o);
		idx_smeId.remove(env, ((SDMSKillJobGeneric) o).smeId, o);
		idx_scopeId.remove(env, ((SDMSKillJobGeneric) o).scopeId, o);
		idx_state.remove(env, ((SDMSKillJobGeneric) o).state, o);
	}

	public static SDMSKillJob getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSKillJob) table.get(env, id);
	}

	public static SDMSKillJob getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSKillJob) table.get(env, id, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
