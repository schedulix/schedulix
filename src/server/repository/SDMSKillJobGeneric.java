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

public class SDMSKillJobGeneric extends SDMSObject
	implements Cloneable
{

	public static final int RUNNABLE = SDMSSubmittedEntity.RUNNABLE;
	public static final int STARTING = SDMSSubmittedEntity.STARTING;
	public static final int STARTED = SDMSSubmittedEntity.STARTED;
	public static final int RUNNING = SDMSSubmittedEntity.RUNNING;
	public static final int FINISHED = SDMSSubmittedEntity.FINISHED;
	public static final int BROKEN_ACTIVE = SDMSSubmittedEntity.BROKEN_ACTIVE;
	public static final int BROKEN_FINISHED = SDMSSubmittedEntity.BROKEN_FINISHED;
	public static final int ERROR = SDMSSubmittedEntity.ERROR;

	public final static int nr_id = 1;
	public final static int nr_seId = 2;
	public final static int nr_seVersion = 3;
	public final static int nr_smeId = 4;
	public final static int nr_scopeId = 5;
	public final static int nr_state = 6;
	public final static int nr_exitCode = 7;
	public final static int nr_commandline = 8;
	public final static int nr_logfile = 9;
	public final static int nr_errlogfile = 10;
	public final static int nr_pid = 11;
	public final static int nr_extPid = 12;
	public final static int nr_errorMsg = 13;
	public final static int nr_runnableTs = 14;
	public final static int nr_startTs = 15;
	public final static int nr_finishTs = 16;
	public final static int nr_creatorUId = 17;
	public final static int nr_createTs = 18;
	public final static int nr_changerUId = 19;
	public final static int nr_changeTs = 20;

	public static String tableName = SDMSKillJobTableGeneric.tableName;

	protected Long seId;
	protected Long seVersion;
	protected Long smeId;
	protected Long scopeId;
	protected Integer state;
	protected Integer exitCode;
	protected String commandline;
	protected String logfile;
	protected String errlogfile;
	protected String pid;
	protected String extPid;
	protected String errorMsg;
	protected Long runnableTs;
	protected Long startTs;
	protected Long finishTs;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSKillJobGeneric(
	        SystemEnvironment env,
	        Long p_seId,
	        Long p_seVersion,
	        Long p_smeId,
	        Long p_scopeId,
	        Integer p_state,
	        Integer p_exitCode,
	        String p_commandline,
	        String p_logfile,
	        String p_errlogfile,
	        String p_pid,
	        String p_extPid,
	        String p_errorMsg,
	        Long p_runnableTs,
	        Long p_startTs,
	        Long p_finishTs,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSKillJobTableGeneric.table);
		seId = p_seId;
		seVersion = p_seVersion;
		smeId = p_smeId;
		scopeId = p_scopeId;
		state = p_state;
		exitCode = p_exitCode;
		if (p_commandline != null && p_commandline.length() > 512) {
			p_commandline = p_commandline.substring(0,512);
		}
		commandline = p_commandline;
		if (p_logfile != null && p_logfile.length() > 512) {
			p_logfile = p_logfile.substring(0,512);
		}
		logfile = p_logfile;
		if (p_errlogfile != null && p_errlogfile.length() > 512) {
			p_errlogfile = p_errlogfile.substring(0,512);
		}
		errlogfile = p_errlogfile;
		if (p_pid != null && p_pid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(KillJob) Length of $1 exceeds maximum length $2", "pid", "32")
			);
		}
		pid = p_pid;
		if (p_extPid != null && p_extPid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(KillJob) Length of $1 exceeds maximum length $2", "extPid", "32")
			);
		}
		extPid = p_extPid;
		if (p_errorMsg != null && p_errorMsg.length() > 256) {
			p_errorMsg = p_errorMsg.substring(0,256);
		}
		errorMsg = p_errorMsg;
		runnableTs = p_runnableTs;
		startTs = p_startTs;
		finishTs = p_finishTs;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.seId = p_seId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getSeVersion (SystemEnvironment env)
	throws SDMSException
	{
		return (seVersion);
	}

	public	void setSeVersion (SystemEnvironment env, Long p_seVersion)
	throws SDMSException
	{
		if(seVersion.equals(p_seVersion)) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.seVersion = p_seVersion;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.smeId = p_smeId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 2);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		return (scopeId);
	}

	public	void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		if(scopeId.equals(p_scopeId)) return;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.scopeId = p_scopeId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		return (state);
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getState (env);
		switch (v.intValue()) {
			case SDMSKillJob.RUNNABLE:
				return "RUNNABLE";
			case SDMSKillJob.STARTING:
				return "STARTING";
			case SDMSKillJob.STARTED:
				return "STARTED";
			case SDMSKillJob.RUNNING:
				return "RUNNING";
			case SDMSKillJob.FINISHED:
				return "FINISHED";
			case SDMSKillJob.BROKEN_ACTIVE:
				return "BROKEN_ACTIVE";
			case SDMSKillJob.BROKEN_FINISHED:
				return "BROKEN_FINISHED";
			case SDMSKillJob.ERROR:
				return "ERROR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown KillJob.state: $1",
		                          getState (env)));
	}

	public	void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		if(state.equals(p_state)) return;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.state = p_state;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 8);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getExitCode (SystemEnvironment env)
	throws SDMSException
	{
		return (exitCode);
	}

	public	void setExitCode (SystemEnvironment env, Integer p_exitCode)
	throws SDMSException
	{
		if(p_exitCode != null && p_exitCode.equals(exitCode)) return;
		if(p_exitCode == null && exitCode == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.exitCode = p_exitCode;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getCommandline (SystemEnvironment env)
	throws SDMSException
	{
		return (commandline);
	}

	public	void setCommandline (SystemEnvironment env, String p_commandline)
	throws SDMSException
	{
		if(p_commandline != null && p_commandline.equals(commandline)) return;
		if(p_commandline == null && commandline == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		if (p_commandline != null && p_commandline.length() > 512) {
			p_commandline = p_commandline.substring(0,512);
		}
		o.commandline = p_commandline;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getLogfile (SystemEnvironment env)
	throws SDMSException
	{
		return (logfile);
	}

	public	void setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		if(p_logfile != null && p_logfile.equals(logfile)) return;
		if(p_logfile == null && logfile == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		if (p_logfile != null && p_logfile.length() > 512) {
			p_logfile = p_logfile.substring(0,512);
		}
		o.logfile = p_logfile;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getErrlogfile (SystemEnvironment env)
	throws SDMSException
	{
		return (errlogfile);
	}

	public	void setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		if(p_errlogfile != null && p_errlogfile.equals(errlogfile)) return;
		if(p_errlogfile == null && errlogfile == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		if (p_errlogfile != null && p_errlogfile.length() > 512) {
			p_errlogfile = p_errlogfile.substring(0,512);
		}
		o.errlogfile = p_errlogfile;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getPid (SystemEnvironment env)
	throws SDMSException
	{
		return (pid);
	}

	public	void setPid (SystemEnvironment env, String p_pid)
	throws SDMSException
	{
		if(p_pid != null && p_pid.equals(pid)) return;
		if(p_pid == null && pid == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		if (p_pid != null && p_pid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(KillJob) Length of $1 exceeds maximum length $2", "pid", "32")
			);
		}
		o.pid = p_pid;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getExtPid (SystemEnvironment env)
	throws SDMSException
	{
		return (extPid);
	}

	public	void setExtPid (SystemEnvironment env, String p_extPid)
	throws SDMSException
	{
		if(p_extPid != null && p_extPid.equals(extPid)) return;
		if(p_extPid == null && extPid == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		if (p_extPid != null && p_extPid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(KillJob) Length of $1 exceeds maximum length $2", "extPid", "32")
			);
		}
		o.extPid = p_extPid;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getErrorMsg (SystemEnvironment env)
	throws SDMSException
	{
		return (errorMsg);
	}

	public	void setErrorMsg (SystemEnvironment env, String p_errorMsg)
	throws SDMSException
	{
		if(p_errorMsg != null && p_errorMsg.equals(errorMsg)) return;
		if(p_errorMsg == null && errorMsg == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		if (p_errorMsg != null && p_errorMsg.length() > 256) {
			p_errorMsg = p_errorMsg.substring(0,256);
		}
		o.errorMsg = p_errorMsg;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getRunnableTs (SystemEnvironment env)
	throws SDMSException
	{
		return (runnableTs);
	}

	public	void setRunnableTs (SystemEnvironment env, Long p_runnableTs)
	throws SDMSException
	{
		if(p_runnableTs != null && p_runnableTs.equals(runnableTs)) return;
		if(p_runnableTs == null && runnableTs == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.runnableTs = p_runnableTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getStartTs (SystemEnvironment env)
	throws SDMSException
	{
		return (startTs);
	}

	public	void setStartTs (SystemEnvironment env, Long p_startTs)
	throws SDMSException
	{
		if(p_startTs != null && p_startTs.equals(startTs)) return;
		if(p_startTs == null && startTs == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.startTs = p_startTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getFinishTs (SystemEnvironment env)
	throws SDMSException
	{
		return (finishTs);
	}

	public	void setFinishTs (SystemEnvironment env, Long p_finishTs)
	throws SDMSException
	{
		if(p_finishTs != null && p_finishTs.equals(finishTs)) return;
		if(p_finishTs == null && finishTs == null) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.finishTs = p_finishTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return;
		SDMSKillJobGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSKillJobGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.changerUId = p_changerUId;
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return;
		SDMSKillJobGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSKillJobGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return new SDMSKillJob(this);
	}

	protected SDMSKillJobGeneric(Long p_id,
	                             Long p_seId,
	                             Long p_seVersion,
	                             Long p_smeId,
	                             Long p_scopeId,
	                             Integer p_state,
	                             Integer p_exitCode,
	                             String p_commandline,
	                             String p_logfile,
	                             String p_errlogfile,
	                             String p_pid,
	                             String p_extPid,
	                             String p_errorMsg,
	                             Long p_runnableTs,
	                             Long p_startTs,
	                             Long p_finishTs,
	                             Long p_creatorUId,
	                             Long p_createTs,
	                             Long p_changerUId,
	                             Long p_changeTs,
	                             long p_validFrom, long p_validTo)
	{
		id     = p_id;
		seId = p_seId;
		seVersion = p_seVersion;
		smeId = p_smeId;
		scopeId = p_scopeId;
		state = p_state;
		exitCode = p_exitCode;
		commandline = p_commandline;
		logfile = p_logfile;
		errlogfile = p_errlogfile;
		pid = p_pid;
		extPid = p_extPid;
		errorMsg = p_errorMsg;
		runnableTs = p_runnableTs;
		startTs = p_startTs;
		finishTs = p_finishTs;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		validFrom = p_validFrom;
		validTo   = p_validTo;
	}

	protected String tableName()
	{
		return tableName;
	}

	protected void insertDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "INSERT INTO " + squote + "KILL_JOB" + equote + " (" +
				        "ID" +
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
				        ") VALUES (?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "KillJob: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, seId.longValue());
			myInsert.setLong (3, seVersion.longValue());
			myInsert.setLong (4, smeId.longValue());
			myInsert.setLong (5, scopeId.longValue());
			myInsert.setInt(6, state.intValue());
			if (exitCode == null)
				myInsert.setNull(7, Types.INTEGER);
			else
				myInsert.setInt(7, exitCode.intValue());
			if (commandline == null)
				myInsert.setNull(8, Types.VARCHAR);
			else
				myInsert.setString(8, commandline);
			if (logfile == null)
				myInsert.setNull(9, Types.VARCHAR);
			else
				myInsert.setString(9, logfile);
			if (errlogfile == null)
				myInsert.setNull(10, Types.VARCHAR);
			else
				myInsert.setString(10, errlogfile);
			if (pid == null)
				myInsert.setNull(11, Types.VARCHAR);
			else
				myInsert.setString(11, pid);
			if (extPid == null)
				myInsert.setNull(12, Types.VARCHAR);
			else
				myInsert.setString(12, extPid);
			if (errorMsg == null)
				myInsert.setNull(13, Types.VARCHAR);
			else
				myInsert.setString(13, errorMsg);
			if (runnableTs == null)
				myInsert.setNull(14, Types.INTEGER);
			else
				myInsert.setLong (14, runnableTs.longValue());
			if (startTs == null)
				myInsert.setNull(15, Types.INTEGER);
			else
				myInsert.setLong (15, startTs.longValue());
			if (finishTs == null)
				myInsert.setNull(16, Types.INTEGER);
			else
				myInsert.setLong (16, finishTs.longValue());
			myInsert.setLong (17, creatorUId.longValue());
			myInsert.setLong (18, createTs.longValue());
			myInsert.setLong (19, changerUId.longValue());
			myInsert.setLong (20, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "KillJob: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "DELETE FROM " + squote + "KILL_JOB" + equote + " WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "KillJob: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "KillJob: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE " + squote + "KILL_JOB" + equote + " SET " +
				        "" + squote + "SE_ID" + equote + " = ? " +
				        ", " + squote + "SE_VERSION" + equote + " = ? " +
				        ", " + squote + "SME_ID" + equote + " = ? " +
				        ", " + squote + "SCOPE_ID" + equote + " = ? " +
				        ", " + squote + "STATE" + equote + " = ? " +
				        ", " + squote + "EXIT_CODE" + equote + " = ? " +
				        ", " + squote + "COMMANDLINE" + equote + " = ? " +
				        ", " + squote + "LOGFILE" + equote + " = ? " +
				        ", " + squote + "ERRLOGFILE" + equote + " = ? " +
				        ", " + squote + "PID" + equote + " = ? " +
				        ", " + squote + "EXTPID" + equote + " = ? " +
				        ", " + squote + "ERROR_MSG" + equote + " = ? " +
				        ", " + squote + "RUNNABLE_TS" + equote + " = ? " +
				        ", " + squote + "START_TS" + equote + " = ? " +
				        ", " + squote + "FINSH_TS" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "KillJob: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, seId.longValue());
			myUpdate.setLong (2, seVersion.longValue());
			myUpdate.setLong (3, smeId.longValue());
			myUpdate.setLong (4, scopeId.longValue());
			myUpdate.setInt(5, state.intValue());
			if (exitCode == null)
				myUpdate.setNull(6, Types.INTEGER);
			else
				myUpdate.setInt(6, exitCode.intValue());
			if (commandline == null)
				myUpdate.setNull(7, Types.VARCHAR);
			else
				myUpdate.setString(7, commandline);
			if (logfile == null)
				myUpdate.setNull(8, Types.VARCHAR);
			else
				myUpdate.setString(8, logfile);
			if (errlogfile == null)
				myUpdate.setNull(9, Types.VARCHAR);
			else
				myUpdate.setString(9, errlogfile);
			if (pid == null)
				myUpdate.setNull(10, Types.VARCHAR);
			else
				myUpdate.setString(10, pid);
			if (extPid == null)
				myUpdate.setNull(11, Types.VARCHAR);
			else
				myUpdate.setString(11, extPid);
			if (errorMsg == null)
				myUpdate.setNull(12, Types.VARCHAR);
			else
				myUpdate.setString(12, errorMsg);
			if (runnableTs == null)
				myUpdate.setNull(13, Types.INTEGER);
			else
				myUpdate.setLong (13, runnableTs.longValue());
			if (startTs == null)
				myUpdate.setNull(14, Types.INTEGER);
			else
				myUpdate.setLong (14, startTs.longValue());
			if (finishTs == null)
				myUpdate.setNull(15, Types.INTEGER);
			else
				myUpdate.setLong (15, finishTs.longValue());
			myUpdate.setLong (16, creatorUId.longValue());
			myUpdate.setLong (17, createTs.longValue());
			myUpdate.setLong (18, changerUId.longValue());
			myUpdate.setLong (19, changeTs.longValue());
			myUpdate.setLong(20, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "KillJob: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkState(Integer p)
	{
		switch (p.intValue()) {
			case SDMSKillJob.RUNNABLE:
			case SDMSKillJob.STARTING:
			case SDMSKillJob.STARTED:
			case SDMSKillJob.RUNNING:
			case SDMSKillJob.FINISHED:
			case SDMSKillJob.BROKEN_ACTIVE:
			case SDMSKillJob.BROKEN_FINISHED:
			case SDMSKillJob.ERROR:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : KillJob", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seVersion : " + seVersion, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "smeId : " + smeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "scopeId : " + scopeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "state : " + state, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "exitCode : " + exitCode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "commandline : " + commandline, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "logfile : " + logfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errlogfile : " + errlogfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "pid : " + pid, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "extPid : " + extPid, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errorMsg : " + errorMsg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "runnableTs : " + runnableTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "startTs : " + startTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "finishTs : " + finishTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validTo : " + validTo, SDMSThread.SEVERITY_MESSAGE);
		dumpVersions(SDMSThread.SEVERITY_MESSAGE);
	}

	public String toString(int indent)
	{
		StringBuffer sb = new StringBuffer(indent + 1);
		for(int i = 0; i < indent; ++i) sb.append(" ");
		String indentString = new String(sb);
		String result =
		        indentString + "id : " + id + "\n" +
		        indentString + "seId        : " + seId + "\n" +
		        indentString + "seVersion   : " + seVersion + "\n" +
		        indentString + "smeId       : " + smeId + "\n" +
		        indentString + "scopeId     : " + scopeId + "\n" +
		        indentString + "state       : " + state + "\n" +
		        indentString + "exitCode    : " + exitCode + "\n" +
		        indentString + "commandline : " + commandline + "\n" +
		        indentString + "logfile     : " + logfile + "\n" +
		        indentString + "errlogfile  : " + errlogfile + "\n" +
		        indentString + "pid         : " + pid + "\n" +
		        indentString + "extPid      : " + extPid + "\n" +
		        indentString + "errorMsg    : " + errorMsg + "\n" +
		        indentString + "runnableTs  : " + runnableTs + "\n" +
		        indentString + "startTs     : " + startTs + "\n" +
		        indentString + "finishTs    : " + finishTs + "\n" +
		        indentString + "creatorUId  : " + creatorUId + "\n" +
		        indentString + "createTs    : " + createTs + "\n" +
		        indentString + "changerUId  : " + changerUId + "\n" +
		        indentString + "changeTs    : " + changeTs + "\n" +
		        indentString + "validFrom : " + validFrom + "\n" +
		        indentString + "validTo : " + validTo + "\n";
		return result;
	}

	public String toString()
	{
		String result = toString(0);
		return result;
	}
}
