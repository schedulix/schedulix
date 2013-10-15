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

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

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

	public	SDMSKillJobGeneric setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return this;
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
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getSeVersion (SystemEnvironment env)
	throws SDMSException
	{
		return (seVersion);
	}

	public	SDMSKillJobGeneric setSeVersion (SystemEnvironment env, Long p_seVersion)
	throws SDMSException
	{
		if(seVersion.equals(p_seVersion)) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.seVersion = p_seVersion;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	SDMSKillJobGeneric setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return this;
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
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		return (scopeId);
	}

	public	SDMSKillJobGeneric setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		if(scopeId.equals(p_scopeId)) return this;
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
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
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

	public	SDMSKillJobGeneric setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		if(state.equals(p_state)) return this;
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
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getExitCode (SystemEnvironment env)
	throws SDMSException
	{
		return (exitCode);
	}

	public	SDMSKillJobGeneric setExitCode (SystemEnvironment env, Integer p_exitCode)
	throws SDMSException
	{
		if(p_exitCode != null && p_exitCode.equals(exitCode)) return this;
		if(p_exitCode == null && exitCode == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.exitCode = p_exitCode;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getCommandline (SystemEnvironment env)
	throws SDMSException
	{
		return (commandline);
	}

	public	SDMSKillJobGeneric setCommandline (SystemEnvironment env, String p_commandline)
	throws SDMSException
	{
		if(p_commandline != null && p_commandline.equals(commandline)) return this;
		if(p_commandline == null && commandline == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			if (p_commandline != null && p_commandline.length() > 512) {
				p_commandline = p_commandline.substring(0,512);
			}
			o.commandline = p_commandline;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getLogfile (SystemEnvironment env)
	throws SDMSException
	{
		return (logfile);
	}

	public	SDMSKillJobGeneric setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		if(p_logfile != null && p_logfile.equals(logfile)) return this;
		if(p_logfile == null && logfile == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			if (p_logfile != null && p_logfile.length() > 512) {
				p_logfile = p_logfile.substring(0,512);
			}
			o.logfile = p_logfile;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getErrlogfile (SystemEnvironment env)
	throws SDMSException
	{
		return (errlogfile);
	}

	public	SDMSKillJobGeneric setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		if(p_errlogfile != null && p_errlogfile.equals(errlogfile)) return this;
		if(p_errlogfile == null && errlogfile == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			if (p_errlogfile != null && p_errlogfile.length() > 512) {
				p_errlogfile = p_errlogfile.substring(0,512);
			}
			o.errlogfile = p_errlogfile;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getPid (SystemEnvironment env)
	throws SDMSException
	{
		return (pid);
	}

	public	SDMSKillJobGeneric setPid (SystemEnvironment env, String p_pid)
	throws SDMSException
	{
		if(p_pid != null && p_pid.equals(pid)) return this;
		if(p_pid == null && pid == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			if (p_pid != null && p_pid.length() > 32) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(KillJob) Length of $1 exceeds maximum length $2", "pid", "32")
				);
			}
			o.pid = p_pid;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getExtPid (SystemEnvironment env)
	throws SDMSException
	{
		return (extPid);
	}

	public	SDMSKillJobGeneric setExtPid (SystemEnvironment env, String p_extPid)
	throws SDMSException
	{
		if(p_extPid != null && p_extPid.equals(extPid)) return this;
		if(p_extPid == null && extPid == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			if (p_extPid != null && p_extPid.length() > 32) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(KillJob) Length of $1 exceeds maximum length $2", "extPid", "32")
				);
			}
			o.extPid = p_extPid;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getErrorMsg (SystemEnvironment env)
	throws SDMSException
	{
		return (errorMsg);
	}

	public	SDMSKillJobGeneric setErrorMsg (SystemEnvironment env, String p_errorMsg)
	throws SDMSException
	{
		if(p_errorMsg != null && p_errorMsg.equals(errorMsg)) return this;
		if(p_errorMsg == null && errorMsg == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			if (p_errorMsg != null && p_errorMsg.length() > 256) {
				p_errorMsg = p_errorMsg.substring(0,256);
			}
			o.errorMsg = p_errorMsg;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getRunnableTs (SystemEnvironment env)
	throws SDMSException
	{
		return (runnableTs);
	}

	public	SDMSKillJobGeneric setRunnableTs (SystemEnvironment env, Long p_runnableTs)
	throws SDMSException
	{
		if(p_runnableTs != null && p_runnableTs.equals(runnableTs)) return this;
		if(p_runnableTs == null && runnableTs == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.runnableTs = p_runnableTs;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getStartTs (SystemEnvironment env)
	throws SDMSException
	{
		return (startTs);
	}

	public	SDMSKillJobGeneric setStartTs (SystemEnvironment env, Long p_startTs)
	throws SDMSException
	{
		if(p_startTs != null && p_startTs.equals(startTs)) return this;
		if(p_startTs == null && startTs == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.startTs = p_startTs;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getFinishTs (SystemEnvironment env)
	throws SDMSException
	{
		return (finishTs);
	}

	public	SDMSKillJobGeneric setFinishTs (SystemEnvironment env, Long p_finishTs)
	throws SDMSException
	{
		if(p_finishTs != null && p_finishTs.equals(finishTs)) return this;
		if(p_finishTs == null && finishTs == null) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.finishTs = p_finishTs;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	SDMSKillJobGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.creatorUId = p_creatorUId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	SDMSKillJobGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(KillJob) Change of system object not allowed")
				);
			}
			o = (SDMSKillJobGeneric) change(env);
			o.createTs = p_createTs;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	SDMSKillJobGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSKillJobGeneric) change(env);
			o.changerUId = p_changerUId;
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	SDMSKillJobGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSKillJobGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSKillJobGeneric) change(env);
			o.changeTs = p_changeTs;
			o.changerUId = env.cEnv.euid();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy()
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
		if(pInsert == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
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
				stmt =
				        "INSERT INTO KILL_JOB (" +
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
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "KillJob: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, seId.longValue());
			pInsert.setLong (3, seVersion.longValue());
			pInsert.setLong (4, smeId.longValue());
			pInsert.setLong (5, scopeId.longValue());
			pInsert.setInt(6, state.intValue());
			if (exitCode == null)
				pInsert.setNull(7, Types.INTEGER);
			else
				pInsert.setInt(7, exitCode.intValue());
			if (commandline == null)
				pInsert.setNull(8, Types.VARCHAR);
			else
				pInsert.setString(8, commandline);
			if (logfile == null)
				pInsert.setNull(9, Types.VARCHAR);
			else
				pInsert.setString(9, logfile);
			if (errlogfile == null)
				pInsert.setNull(10, Types.VARCHAR);
			else
				pInsert.setString(10, errlogfile);
			if (pid == null)
				pInsert.setNull(11, Types.VARCHAR);
			else
				pInsert.setString(11, pid);
			if (extPid == null)
				pInsert.setNull(12, Types.VARCHAR);
			else
				pInsert.setString(12, extPid);
			if (errorMsg == null)
				pInsert.setNull(13, Types.VARCHAR);
			else
				pInsert.setString(13, errorMsg);
			if (runnableTs == null)
				pInsert.setNull(14, Types.INTEGER);
			else
				pInsert.setLong (14, runnableTs.longValue());
			if (startTs == null)
				pInsert.setNull(15, Types.INTEGER);
			else
				pInsert.setLong (15, startTs.longValue());
			if (finishTs == null)
				pInsert.setNull(16, Types.INTEGER);
			else
				pInsert.setLong (16, finishTs.longValue());
			pInsert.setLong (17, creatorUId.longValue());
			pInsert.setLong (18, createTs.longValue());
			pInsert.setLong (19, changerUId.longValue());
			pInsert.setLong (20, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "KillJob: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM KILL_JOB WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "KillJob: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "KillJob: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		if(pUpdate == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
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
				stmt =
				        "UPDATE KILL_JOB SET " +
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
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "KillJob: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, seId.longValue());
			pUpdate.setLong (2, seVersion.longValue());
			pUpdate.setLong (3, smeId.longValue());
			pUpdate.setLong (4, scopeId.longValue());
			pUpdate.setInt(5, state.intValue());
			if (exitCode == null)
				pUpdate.setNull(6, Types.INTEGER);
			else
				pUpdate.setInt(6, exitCode.intValue());
			if (commandline == null)
				pUpdate.setNull(7, Types.VARCHAR);
			else
				pUpdate.setString(7, commandline);
			if (logfile == null)
				pUpdate.setNull(8, Types.VARCHAR);
			else
				pUpdate.setString(8, logfile);
			if (errlogfile == null)
				pUpdate.setNull(9, Types.VARCHAR);
			else
				pUpdate.setString(9, errlogfile);
			if (pid == null)
				pUpdate.setNull(10, Types.VARCHAR);
			else
				pUpdate.setString(10, pid);
			if (extPid == null)
				pUpdate.setNull(11, Types.VARCHAR);
			else
				pUpdate.setString(11, extPid);
			if (errorMsg == null)
				pUpdate.setNull(12, Types.VARCHAR);
			else
				pUpdate.setString(12, errorMsg);
			if (runnableTs == null)
				pUpdate.setNull(13, Types.INTEGER);
			else
				pUpdate.setLong (13, runnableTs.longValue());
			if (startTs == null)
				pUpdate.setNull(14, Types.INTEGER);
			else
				pUpdate.setLong (14, startTs.longValue());
			if (finishTs == null)
				pUpdate.setNull(15, Types.INTEGER);
			else
				pUpdate.setLong (15, finishTs.longValue());
			pUpdate.setLong (16, creatorUId.longValue());
			pUpdate.setLong (17, createTs.longValue());
			pUpdate.setLong (18, changerUId.longValue());
			pUpdate.setLong (19, changeTs.longValue());
			pUpdate.setLong(20, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "KillJob: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
