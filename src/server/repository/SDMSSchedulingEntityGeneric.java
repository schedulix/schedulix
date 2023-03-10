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

public class SDMSSchedulingEntityGeneric extends SDMSObject
	implements Cloneable
{

	public static final int JOB = 1;
	public static final int BATCH = 2;
	public static final int MILESTONE = 3;
	public static final int AND = 1;
	public static final int OR = 2;
	public static final boolean SUSPEND = true;
	public static final boolean NOSUSPEND = false;
	public static final boolean MASTER = true;
	public static final boolean NOMASTER = false;
	public static final boolean SAME_NODE = true;
	public static final boolean NOSAME_NODE = false;
	public static final boolean GANG = true;
	public static final boolean NOGANG = false;
	public static final boolean TRUNC = true;
	public static final boolean NOTRUNC = false;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_folderId = 3;
	public final static int nr_ownerId = 4;
	public final static int nr_type = 5;
	public final static int nr_runProgram = 6;
	public final static int nr_rerunProgram = 7;
	public final static int nr_killProgram = 8;
	public final static int nr_workdir = 9;
	public final static int nr_logfile = 10;
	public final static int nr_truncLog = 11;
	public final static int nr_errlogfile = 12;
	public final static int nr_truncErrlog = 13;
	public final static int nr_expectedRuntime = 14;
	public final static int nr_expectedFinaltime = 15;
	public final static int nr_getExpectedRuntime = 16;
	public final static int nr_priority = 17;
	public final static int nr_minPriority = 18;
	public final static int nr_agingAmount = 19;
	public final static int nr_agingBase = 20;
	public final static int nr_submitSuspended = 21;
	public final static int nr_resumeAt = 22;
	public final static int nr_resumeIn = 23;
	public final static int nr_resumeBase = 24;
	public final static int nr_masterSubmittable = 25;
	public final static int nr_timeoutAmount = 26;
	public final static int nr_timeoutBase = 27;
	public final static int nr_timeoutStateId = 28;
	public final static int nr_sameNode = 29;
	public final static int nr_gangSchedule = 30;
	public final static int nr_dependencyOperation = 31;
	public final static int nr_esmpId = 32;
	public final static int nr_espId = 33;
	public final static int nr_qaId = 34;
	public final static int nr_neId = 35;
	public final static int nr_fpId = 36;
	public final static int nr_inheritPrivs = 37;
	public final static int nr_creatorUId = 38;
	public final static int nr_createTs = 39;
	public final static int nr_changerUId = 40;
	public final static int nr_changeTs = 41;

	public static String tableName = SDMSSchedulingEntityTableGeneric.tableName;

	protected String name;
	protected Long folderId;
	protected Long ownerId;
	protected Integer type;
	protected String runProgram;
	protected String rerunProgram;
	protected String killProgram;
	protected String workdir;
	protected String logfile;
	protected Boolean truncLog;
	protected String errlogfile;
	protected Boolean truncErrlog;
	protected Integer expectedRuntime;
	protected Integer expectedFinaltime;
	protected String getExpectedRuntime;
	protected Integer priority;
	protected Integer minPriority;
	protected Integer agingAmount;
	protected Integer agingBase;
	protected Boolean submitSuspended;
	protected String resumeAt;
	protected Integer resumeIn;
	protected Integer resumeBase;
	protected Boolean masterSubmittable;
	protected Integer timeoutAmount;
	protected Integer timeoutBase;
	protected Long timeoutStateId;
	protected Boolean sameNode;
	protected Boolean gangSchedule;
	protected Integer dependencyOperation;
	protected Long esmpId;
	protected Long espId;
	protected Long qaId;
	protected Long neId;
	protected Long fpId;
	protected Long inheritPrivs;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSSchedulingEntityGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Long p_folderId,
	        Long p_ownerId,
	        Integer p_type,
	        String p_runProgram,
	        String p_rerunProgram,
	        String p_killProgram,
	        String p_workdir,
	        String p_logfile,
	        Boolean p_truncLog,
	        String p_errlogfile,
	        Boolean p_truncErrlog,
	        Integer p_expectedRuntime,
	        Integer p_expectedFinaltime,
	        String p_getExpectedRuntime,
	        Integer p_priority,
	        Integer p_minPriority,
	        Integer p_agingAmount,
	        Integer p_agingBase,
	        Boolean p_submitSuspended,
	        String p_resumeAt,
	        Integer p_resumeIn,
	        Integer p_resumeBase,
	        Boolean p_masterSubmittable,
	        Integer p_timeoutAmount,
	        Integer p_timeoutBase,
	        Long p_timeoutStateId,
	        Boolean p_sameNode,
	        Boolean p_gangSchedule,
	        Integer p_dependencyOperation,
	        Long p_esmpId,
	        Long p_espId,
	        Long p_qaId,
	        Long p_neId,
	        Long p_fpId,
	        Long p_inheritPrivs,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSSchedulingEntityTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		folderId = p_folderId;
		ownerId = p_ownerId;
		type = p_type;
		if (p_runProgram != null && p_runProgram.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "runProgram", "512")
			);
		}
		runProgram = p_runProgram;
		if (p_rerunProgram != null && p_rerunProgram.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "rerunProgram", "512")
			);
		}
		rerunProgram = p_rerunProgram;
		if (p_killProgram != null && p_killProgram.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "killProgram", "512")
			);
		}
		killProgram = p_killProgram;
		if (p_workdir != null && p_workdir.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "workdir", "512")
			);
		}
		workdir = p_workdir;
		if (p_logfile != null && p_logfile.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "logfile", "512")
			);
		}
		logfile = p_logfile;
		truncLog = p_truncLog;
		if (p_errlogfile != null && p_errlogfile.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "errlogfile", "512")
			);
		}
		errlogfile = p_errlogfile;
		truncErrlog = p_truncErrlog;
		expectedRuntime = p_expectedRuntime;
		expectedFinaltime = p_expectedFinaltime;
		if (p_getExpectedRuntime != null && p_getExpectedRuntime.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "getExpectedRuntime", "32")
			);
		}
		getExpectedRuntime = p_getExpectedRuntime;
		priority = p_priority;
		minPriority = p_minPriority;
		agingAmount = p_agingAmount;
		agingBase = p_agingBase;
		submitSuspended = p_submitSuspended;
		if (p_resumeAt != null && p_resumeAt.length() > 20) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "resumeAt", "20")
			);
		}
		resumeAt = p_resumeAt;
		resumeIn = p_resumeIn;
		resumeBase = p_resumeBase;
		masterSubmittable = p_masterSubmittable;
		timeoutAmount = p_timeoutAmount;
		timeoutBase = p_timeoutBase;
		timeoutStateId = p_timeoutStateId;
		sameNode = p_sameNode;
		gangSchedule = p_gangSchedule;
		dependencyOperation = p_dependencyOperation;
		esmpId = p_esmpId;
		espId = p_espId;
		qaId = p_qaId;
		neId = p_neId;
		fpId = p_fpId;
		inheritPrivs = p_inheritPrivs;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		return (name);
	}

	public	void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 64);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getFolderId (SystemEnvironment env)
	throws SDMSException
	{
		return (folderId);
	}

	public	void setFolderId (SystemEnvironment env, Long p_folderId)
	throws SDMSException
	{
		if(folderId.equals(p_folderId)) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.folderId = p_folderId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 193);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (ownerId);
	}

	public	void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.ownerId = p_ownerId;
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

	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		return (type);
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getType (env);
		switch (v.intValue()) {
			case SDMSSchedulingEntity.JOB:
				return "JOB";
			case SDMSSchedulingEntity.BATCH:
				return "BATCH";
			case SDMSSchedulingEntity.MILESTONE:
				return "MILESTONE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.type: $1",
		                          getType (env)));
	}

	public	void setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		if(type.equals(p_type)) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.type = p_type;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getRunProgram (SystemEnvironment env)
	throws SDMSException
	{
		return (runProgram);
	}

	public	void setRunProgram (SystemEnvironment env, String p_runProgram)
	throws SDMSException
	{
		if(p_runProgram != null && p_runProgram.equals(runProgram)) return;
		if(p_runProgram == null && runProgram == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_runProgram != null && p_runProgram.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "runProgram", "512")
			);
		}
		o.runProgram = p_runProgram;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getRerunProgram (SystemEnvironment env)
	throws SDMSException
	{
		return (rerunProgram);
	}

	public	void setRerunProgram (SystemEnvironment env, String p_rerunProgram)
	throws SDMSException
	{
		if(p_rerunProgram != null && p_rerunProgram.equals(rerunProgram)) return;
		if(p_rerunProgram == null && rerunProgram == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_rerunProgram != null && p_rerunProgram.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "rerunProgram", "512")
			);
		}
		o.rerunProgram = p_rerunProgram;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getKillProgram (SystemEnvironment env)
	throws SDMSException
	{
		return (killProgram);
	}

	public	void setKillProgram (SystemEnvironment env, String p_killProgram)
	throws SDMSException
	{
		if(p_killProgram != null && p_killProgram.equals(killProgram)) return;
		if(p_killProgram == null && killProgram == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_killProgram != null && p_killProgram.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "killProgram", "512")
			);
		}
		o.killProgram = p_killProgram;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getWorkdir (SystemEnvironment env)
	throws SDMSException
	{
		return (workdir);
	}

	public	void setWorkdir (SystemEnvironment env, String p_workdir)
	throws SDMSException
	{
		if(p_workdir != null && p_workdir.equals(workdir)) return;
		if(p_workdir == null && workdir == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_workdir != null && p_workdir.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "workdir", "512")
			);
		}
		o.workdir = p_workdir;
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
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_logfile != null && p_logfile.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "logfile", "512")
			);
		}
		o.logfile = p_logfile;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getTruncLog (SystemEnvironment env)
	throws SDMSException
	{
		return (truncLog);
	}

	public String getTruncLogAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getTruncLog (env);
		if (v == null)
			return null;
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingEntity.NOTRUNC)
			return "NOTRUNC";
		if (b == SDMSSchedulingEntity.TRUNC)
			return "TRUNC";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.truncLog: $1",
		                          getTruncLog (env)));
	}

	public	void setTruncLog (SystemEnvironment env, Boolean p_truncLog)
	throws SDMSException
	{
		if(p_truncLog != null && p_truncLog.equals(truncLog)) return;
		if(p_truncLog == null && truncLog == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.truncLog = p_truncLog;
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
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_errlogfile != null && p_errlogfile.length() > 512) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "errlogfile", "512")
			);
		}
		o.errlogfile = p_errlogfile;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getTruncErrlog (SystemEnvironment env)
	throws SDMSException
	{
		return (truncErrlog);
	}

	public String getTruncErrlogAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getTruncErrlog (env);
		if (v == null)
			return null;
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingEntity.NOTRUNC)
			return "NOTRUNC";
		if (b == SDMSSchedulingEntity.TRUNC)
			return "TRUNC";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.truncErrlog: $1",
		                          getTruncErrlog (env)));
	}

	public	void setTruncErrlog (SystemEnvironment env, Boolean p_truncErrlog)
	throws SDMSException
	{
		if(p_truncErrlog != null && p_truncErrlog.equals(truncErrlog)) return;
		if(p_truncErrlog == null && truncErrlog == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.truncErrlog = p_truncErrlog;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getExpectedRuntime (SystemEnvironment env)
	throws SDMSException
	{
		return (expectedRuntime);
	}

	public	void setExpectedRuntime (SystemEnvironment env, Integer p_expectedRuntime)
	throws SDMSException
	{
		if(p_expectedRuntime != null && p_expectedRuntime.equals(expectedRuntime)) return;
		if(p_expectedRuntime == null && expectedRuntime == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.expectedRuntime = p_expectedRuntime;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getExpectedFinaltime (SystemEnvironment env)
	throws SDMSException
	{
		return (expectedFinaltime);
	}

	public	void setExpectedFinaltime (SystemEnvironment env, Integer p_expectedFinaltime)
	throws SDMSException
	{
		if(p_expectedFinaltime != null && p_expectedFinaltime.equals(expectedFinaltime)) return;
		if(p_expectedFinaltime == null && expectedFinaltime == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.expectedFinaltime = p_expectedFinaltime;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getGetExpectedRuntime (SystemEnvironment env)
	throws SDMSException
	{
		return (getExpectedRuntime);
	}

	public	void setGetExpectedRuntime (SystemEnvironment env, String p_getExpectedRuntime)
	throws SDMSException
	{
		if(p_getExpectedRuntime != null && p_getExpectedRuntime.equals(getExpectedRuntime)) return;
		if(p_getExpectedRuntime == null && getExpectedRuntime == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_getExpectedRuntime != null && p_getExpectedRuntime.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "getExpectedRuntime", "32")
			);
		}
		o.getExpectedRuntime = p_getExpectedRuntime;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getPriority (SystemEnvironment env)
	throws SDMSException
	{
		return (priority);
	}

	public	void setPriority (SystemEnvironment env, Integer p_priority)
	throws SDMSException
	{
		if(priority.equals(p_priority)) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.priority = p_priority;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getMinPriority (SystemEnvironment env)
	throws SDMSException
	{
		return (minPriority);
	}

	public	void setMinPriority (SystemEnvironment env, Integer p_minPriority)
	throws SDMSException
	{
		if(p_minPriority != null && p_minPriority.equals(minPriority)) return;
		if(p_minPriority == null && minPriority == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.minPriority = p_minPriority;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getAgingAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (agingAmount);
	}

	public	void setAgingAmount (SystemEnvironment env, Integer p_agingAmount)
	throws SDMSException
	{
		if(p_agingAmount != null && p_agingAmount.equals(agingAmount)) return;
		if(p_agingAmount == null && agingAmount == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.agingAmount = p_agingAmount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getAgingBase (SystemEnvironment env)
	throws SDMSException
	{
		return (agingBase);
	}

	public String getAgingBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getAgingBase (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
			case SDMSSchedulingEntity.MINUTE:
				return "MINUTE";
			case SDMSSchedulingEntity.HOUR:
				return "HOUR";
			case SDMSSchedulingEntity.DAY:
				return "DAY";
			case SDMSSchedulingEntity.WEEK:
				return "WEEK";
			case SDMSSchedulingEntity.MONTH:
				return "MONTH";
			case SDMSSchedulingEntity.YEAR:
				return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.agingBase: $1",
		                          getAgingBase (env)));
	}

	public	void setAgingBase (SystemEnvironment env, Integer p_agingBase)
	throws SDMSException
	{
		if(p_agingBase != null && p_agingBase.equals(agingBase)) return;
		if(p_agingBase == null && agingBase == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.agingBase = p_agingBase;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getSubmitSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (submitSuspended);
	}

	public String getSubmitSuspendedAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getSubmitSuspended (env);
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingEntity.SUSPEND)
			return "SUSPEND";
		if (b == SDMSSchedulingEntity.NOSUSPEND)
			return "NOSUSPEND";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.submitSuspended: $1",
		                          getSubmitSuspended (env)));
	}

	public	void setSubmitSuspended (SystemEnvironment env, Boolean p_submitSuspended)
	throws SDMSException
	{
		if(submitSuspended.equals(p_submitSuspended)) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.submitSuspended = p_submitSuspended;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeAt);
	}

	public	void setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		if(p_resumeAt != null && p_resumeAt.equals(resumeAt)) return;
		if(p_resumeAt == null && resumeAt == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		if (p_resumeAt != null && p_resumeAt.length() > 20) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "resumeAt", "20")
			);
		}
		o.resumeAt = p_resumeAt;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeIn);
	}

	public	void setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		if(p_resumeIn != null && p_resumeIn.equals(resumeIn)) return;
		if(p_resumeIn == null && resumeIn == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.resumeIn = p_resumeIn;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getResumeBase (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeBase);
	}

	public String getResumeBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getResumeBase (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
			case SDMSSchedulingEntity.MINUTE:
				return "MINUTE";
			case SDMSSchedulingEntity.HOUR:
				return "HOUR";
			case SDMSSchedulingEntity.DAY:
				return "DAY";
			case SDMSSchedulingEntity.WEEK:
				return "WEEK";
			case SDMSSchedulingEntity.MONTH:
				return "MONTH";
			case SDMSSchedulingEntity.YEAR:
				return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.resumeBase: $1",
		                          getResumeBase (env)));
	}

	public	void setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		if(p_resumeBase != null && p_resumeBase.equals(resumeBase)) return;
		if(p_resumeBase == null && resumeBase == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.resumeBase = p_resumeBase;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getMasterSubmittable (SystemEnvironment env)
	throws SDMSException
	{
		return (masterSubmittable);
	}

	public String getMasterSubmittableAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getMasterSubmittable (env);
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingEntity.MASTER)
			return "MASTER";
		if (b == SDMSSchedulingEntity.NOMASTER)
			return "NOMASTER";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.masterSubmittable: $1",
		                          getMasterSubmittable (env)));
	}

	public	void setMasterSubmittable (SystemEnvironment env, Boolean p_masterSubmittable)
	throws SDMSException
	{
		if(masterSubmittable.equals(p_masterSubmittable)) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.masterSubmittable = p_masterSubmittable;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 128);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getTimeoutAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (timeoutAmount);
	}

	public	void setTimeoutAmount (SystemEnvironment env, Integer p_timeoutAmount)
	throws SDMSException
	{
		if(p_timeoutAmount != null && p_timeoutAmount.equals(timeoutAmount)) return;
		if(p_timeoutAmount == null && timeoutAmount == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.timeoutAmount = p_timeoutAmount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getTimeoutBase (SystemEnvironment env)
	throws SDMSException
	{
		return (timeoutBase);
	}

	public String getTimeoutBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getTimeoutBase (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
			case SDMSSchedulingEntity.MINUTE:
				return "MINUTE";
			case SDMSSchedulingEntity.HOUR:
				return "HOUR";
			case SDMSSchedulingEntity.DAY:
				return "DAY";
			case SDMSSchedulingEntity.WEEK:
				return "WEEK";
			case SDMSSchedulingEntity.MONTH:
				return "MONTH";
			case SDMSSchedulingEntity.YEAR:
				return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.timeoutBase: $1",
		                          getTimeoutBase (env)));
	}

	public	void setTimeoutBase (SystemEnvironment env, Integer p_timeoutBase)
	throws SDMSException
	{
		if(p_timeoutBase != null && p_timeoutBase.equals(timeoutBase)) return;
		if(p_timeoutBase == null && timeoutBase == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.timeoutBase = p_timeoutBase;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getTimeoutStateId (SystemEnvironment env)
	throws SDMSException
	{
		return (timeoutStateId);
	}

	public	void setTimeoutStateId (SystemEnvironment env, Long p_timeoutStateId)
	throws SDMSException
	{
		if(p_timeoutStateId != null && p_timeoutStateId.equals(timeoutStateId)) return;
		if(p_timeoutStateId == null && timeoutStateId == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.timeoutStateId = p_timeoutStateId;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getSameNode (SystemEnvironment env)
	throws SDMSException
	{
		return (sameNode);
	}

	public String getSameNodeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getSameNode (env);
		if (v == null)
			return null;
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingEntity.SAME_NODE)
			return "SAME_NODE";
		if (b == SDMSSchedulingEntity.NOSAME_NODE)
			return "NOSAME_NODE";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.sameNode: $1",
		                          getSameNode (env)));
	}

	public	void setSameNode (SystemEnvironment env, Boolean p_sameNode)
	throws SDMSException
	{
		if(p_sameNode != null && p_sameNode.equals(sameNode)) return;
		if(p_sameNode == null && sameNode == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.sameNode = p_sameNode;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getGangSchedule (SystemEnvironment env)
	throws SDMSException
	{
		return (gangSchedule);
	}

	public String getGangScheduleAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getGangSchedule (env);
		if (v == null)
			return null;
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingEntity.GANG)
			return "GANG";
		if (b == SDMSSchedulingEntity.NOGANG)
			return "NOGANG";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.gangSchedule: $1",
		                          getGangSchedule (env)));
	}

	public	void setGangSchedule (SystemEnvironment env, Boolean p_gangSchedule)
	throws SDMSException
	{
		if(p_gangSchedule != null && p_gangSchedule.equals(gangSchedule)) return;
		if(p_gangSchedule == null && gangSchedule == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.gangSchedule = p_gangSchedule;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getDependencyOperation (SystemEnvironment env)
	throws SDMSException
	{
		return (dependencyOperation);
	}

	public String getDependencyOperationAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getDependencyOperation (env);
		switch (v.intValue()) {
			case SDMSSchedulingEntity.AND:
				return "AND";
			case SDMSSchedulingEntity.OR:
				return "OR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingEntity.dependencyOperation: $1",
		                          getDependencyOperation (env)));
	}

	public	void setDependencyOperation (SystemEnvironment env, Integer p_dependencyOperation)
	throws SDMSException
	{
		if(dependencyOperation.equals(p_dependencyOperation)) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.dependencyOperation = p_dependencyOperation;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getEsmpId (SystemEnvironment env)
	throws SDMSException
	{
		return (esmpId);
	}

	public	void setEsmpId (SystemEnvironment env, Long p_esmpId)
	throws SDMSException
	{
		if(p_esmpId != null && p_esmpId.equals(esmpId)) return;
		if(p_esmpId == null && esmpId == null) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.esmpId = p_esmpId;
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

	public Long getEspId (SystemEnvironment env)
	throws SDMSException
	{
		return (espId);
	}

	public	void setEspId (SystemEnvironment env, Long p_espId)
	throws SDMSException
	{
		if(p_espId != null && p_espId.equals(espId)) return;
		if(p_espId == null && espId == null) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.espId = p_espId;
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

	public Long getQaId (SystemEnvironment env)
	throws SDMSException
	{
		return (qaId);
	}

	public	void setQaId (SystemEnvironment env, Long p_qaId)
	throws SDMSException
	{
		if(p_qaId != null && p_qaId.equals(qaId)) return;
		if(p_qaId == null && qaId == null) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.qaId = p_qaId;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getNeId (SystemEnvironment env)
	throws SDMSException
	{
		return (neId);
	}

	public	void setNeId (SystemEnvironment env, Long p_neId)
	throws SDMSException
	{
		if(p_neId != null && p_neId.equals(neId)) return;
		if(p_neId == null && neId == null) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.neId = p_neId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 16);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getFpId (SystemEnvironment env)
	throws SDMSException
	{
		return (fpId);
	}

	public	void setFpId (SystemEnvironment env, Long p_fpId)
	throws SDMSException
	{
		if(p_fpId != null && p_fpId.equals(fpId)) return;
		if(p_fpId == null && fpId == null) return;
		SDMSSchedulingEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.fpId = p_fpId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 32);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		return (inheritPrivs);
	}

	public	void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		if(inheritPrivs.equals(p_inheritPrivs)) return;
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.inheritPrivs = p_inheritPrivs;
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
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
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
		SDMSSchedulingEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
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
		SDMSSchedulingEntityGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
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
		SDMSSchedulingEntityGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingEntityGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSSchedulingEntityGeneric set_FolderIdName (SystemEnvironment env, Long p_folderId, String p_name)
	throws SDMSException
	{
		SDMSSchedulingEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.folderId = p_folderId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290026",
				                        "(SchedulingEntity) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSSchedulingEntityGeneric set_FolderIdMasterSubmittable (SystemEnvironment env, Long p_folderId, Boolean p_masterSubmittable)
	throws SDMSException
	{
		SDMSSchedulingEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SchedulingEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingEntityGeneric) change(env);
			o.folderId = p_folderId;
			o.masterSubmittable = p_masterSubmittable;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return SDMSSchedulingEntity.getProxy(sysEnv, this);
	}

	protected SDMSSchedulingEntityGeneric(Long p_id,
	                                      String p_name,
	                                      Long p_folderId,
	                                      Long p_ownerId,
	                                      Integer p_type,
	                                      String p_runProgram,
	                                      String p_rerunProgram,
	                                      String p_killProgram,
	                                      String p_workdir,
	                                      String p_logfile,
	                                      Boolean p_truncLog,
	                                      String p_errlogfile,
	                                      Boolean p_truncErrlog,
	                                      Integer p_expectedRuntime,
	                                      Integer p_expectedFinaltime,
	                                      String p_getExpectedRuntime,
	                                      Integer p_priority,
	                                      Integer p_minPriority,
	                                      Integer p_agingAmount,
	                                      Integer p_agingBase,
	                                      Boolean p_submitSuspended,
	                                      String p_resumeAt,
	                                      Integer p_resumeIn,
	                                      Integer p_resumeBase,
	                                      Boolean p_masterSubmittable,
	                                      Integer p_timeoutAmount,
	                                      Integer p_timeoutBase,
	                                      Long p_timeoutStateId,
	                                      Boolean p_sameNode,
	                                      Boolean p_gangSchedule,
	                                      Integer p_dependencyOperation,
	                                      Long p_esmpId,
	                                      Long p_espId,
	                                      Long p_qaId,
	                                      Long p_neId,
	                                      Long p_fpId,
	                                      Long p_inheritPrivs,
	                                      Long p_creatorUId,
	                                      Long p_createTs,
	                                      Long p_changerUId,
	                                      Long p_changeTs,
	                                      long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		folderId = p_folderId;
		ownerId = p_ownerId;
		type = p_type;
		runProgram = p_runProgram;
		rerunProgram = p_rerunProgram;
		killProgram = p_killProgram;
		workdir = p_workdir;
		logfile = p_logfile;
		truncLog = p_truncLog;
		errlogfile = p_errlogfile;
		truncErrlog = p_truncErrlog;
		expectedRuntime = p_expectedRuntime;
		expectedFinaltime = p_expectedFinaltime;
		getExpectedRuntime = p_getExpectedRuntime;
		priority = p_priority;
		minPriority = p_minPriority;
		agingAmount = p_agingAmount;
		agingBase = p_agingBase;
		submitSuspended = p_submitSuspended;
		resumeAt = p_resumeAt;
		resumeIn = p_resumeIn;
		resumeBase = p_resumeBase;
		masterSubmittable = p_masterSubmittable;
		timeoutAmount = p_timeoutAmount;
		timeoutBase = p_timeoutBase;
		timeoutStateId = p_timeoutStateId;
		sameNode = p_sameNode;
		gangSchedule = p_gangSchedule;
		dependencyOperation = p_dependencyOperation;
		esmpId = p_esmpId;
		espId = p_espId;
		qaId = p_qaId;
		neId = p_neId;
		fpId = p_fpId;
		inheritPrivs = p_inheritPrivs;
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
				        "INSERT INTO " + squote + "SCHEDULING_ENTITY" + equote + " (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "FOLDER_ID" + equote +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "TYPE" + equote +
				        ", " + squote + "RUN_PROGRAM" + equote +
				        ", " + squote + "RERUN_PROGRAM" + equote +
				        ", " + squote + "KILL_PROGRAM" + equote +
				        ", " + squote + "WORKDIR" + equote +
				        ", " + squote + "LOGFILE" + equote +
				        ", " + squote + "TRUNC_LOG" + equote +
				        ", " + squote + "ERRLOGFILE" + equote +
				        ", " + squote + "TRUNC_ERRLOG" + equote +
				        ", " + squote + "EXPECTED_RUNTIME" + equote +
				        ", " + squote + "EXPECTED_FINALTIME" + equote +
				        ", " + squote + "GET_EXPECTED_RUNTIME" + equote +
				        ", " + squote + "PRIORITY" + equote +
				        ", " + squote + "MIN_PRIORITY" + equote +
				        ", " + squote + "AGING_AMOUNT" + equote +
				        ", " + squote + "AGING_BASE" + equote +
				        ", " + squote + "SUBMIT_SUSPENDED" + equote +
				        ", " + squote + "RESUME_AT" + equote +
				        ", " + squote + "RESUME_IN" + equote +
				        ", " + squote + "RESUME_BASE" + equote +
				        ", " + squote + "MASTER_SUBMITTABLE" + equote +
				        ", " + squote + "TIMEOUT_AMOUNT" + equote +
				        ", " + squote + "TIMEOUT_BASE" + equote +
				        ", " + squote + "TIMEOUT_STATE_ID" + equote +
				        ", " + squote + "SAME_NODE" + equote +
				        ", " + squote + "GANG_SCHEDULE" + equote +
				        ", " + squote + "DEPENDENCY_OPERATION" + equote +
				        ", " + squote + "ESMP_ID" + equote +
				        ", " + squote + "ESP_ID" + equote +
				        ", " + squote + "QA_ID" + equote +
				        ", " + squote + "NE_ID" + equote +
				        ", " + squote + "FP_ID" + equote +
				        ", " + squote + "INHERIT_PRIVS" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ", VALID_FROM, VALID_TO" +
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
				        ", ?" +
				        ", ?" +
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "SchedulingEntity: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setString(2, name);
			myInsert.setLong (3, folderId.longValue());
			myInsert.setLong (4, ownerId.longValue());
			myInsert.setInt(5, type.intValue());
			if (runProgram == null)
				myInsert.setNull(6, Types.VARCHAR);
			else
				myInsert.setString(6, runProgram);
			if (rerunProgram == null)
				myInsert.setNull(7, Types.VARCHAR);
			else
				myInsert.setString(7, rerunProgram);
			if (killProgram == null)
				myInsert.setNull(8, Types.VARCHAR);
			else
				myInsert.setString(8, killProgram);
			if (workdir == null)
				myInsert.setNull(9, Types.VARCHAR);
			else
				myInsert.setString(9, workdir);
			if (logfile == null)
				myInsert.setNull(10, Types.VARCHAR);
			else
				myInsert.setString(10, logfile);
			if (truncLog == null)
				myInsert.setNull(11, Types.INTEGER);
			else
				myInsert.setInt (11, truncLog.booleanValue() ? 1 : 0);
			if (errlogfile == null)
				myInsert.setNull(12, Types.VARCHAR);
			else
				myInsert.setString(12, errlogfile);
			if (truncErrlog == null)
				myInsert.setNull(13, Types.INTEGER);
			else
				myInsert.setInt (13, truncErrlog.booleanValue() ? 1 : 0);
			if (expectedRuntime == null)
				myInsert.setNull(14, Types.INTEGER);
			else
				myInsert.setInt(14, expectedRuntime.intValue());
			if (expectedFinaltime == null)
				myInsert.setNull(15, Types.INTEGER);
			else
				myInsert.setInt(15, expectedFinaltime.intValue());
			if (getExpectedRuntime == null)
				myInsert.setNull(16, Types.VARCHAR);
			else
				myInsert.setString(16, getExpectedRuntime);
			myInsert.setInt(17, priority.intValue());
			if (minPriority == null)
				myInsert.setNull(18, Types.INTEGER);
			else
				myInsert.setInt(18, minPriority.intValue());
			if (agingAmount == null)
				myInsert.setNull(19, Types.INTEGER);
			else
				myInsert.setInt(19, agingAmount.intValue());
			if (agingBase == null)
				myInsert.setNull(20, Types.INTEGER);
			else
				myInsert.setInt(20, agingBase.intValue());
			myInsert.setInt (21, submitSuspended.booleanValue() ? 1 : 0);
			if (resumeAt == null)
				myInsert.setNull(22, Types.VARCHAR);
			else
				myInsert.setString(22, resumeAt);
			if (resumeIn == null)
				myInsert.setNull(23, Types.INTEGER);
			else
				myInsert.setInt(23, resumeIn.intValue());
			if (resumeBase == null)
				myInsert.setNull(24, Types.INTEGER);
			else
				myInsert.setInt(24, resumeBase.intValue());
			myInsert.setInt (25, masterSubmittable.booleanValue() ? 1 : 0);
			if (timeoutAmount == null)
				myInsert.setNull(26, Types.INTEGER);
			else
				myInsert.setInt(26, timeoutAmount.intValue());
			if (timeoutBase == null)
				myInsert.setNull(27, Types.INTEGER);
			else
				myInsert.setInt(27, timeoutBase.intValue());
			if (timeoutStateId == null)
				myInsert.setNull(28, Types.INTEGER);
			else
				myInsert.setLong (28, timeoutStateId.longValue());
			if (sameNode == null)
				myInsert.setNull(29, Types.INTEGER);
			else
				myInsert.setInt (29, sameNode.booleanValue() ? 1 : 0);
			if (gangSchedule == null)
				myInsert.setNull(30, Types.INTEGER);
			else
				myInsert.setInt (30, gangSchedule.booleanValue() ? 1 : 0);
			myInsert.setInt(31, dependencyOperation.intValue());
			if (esmpId == null)
				myInsert.setNull(32, Types.INTEGER);
			else
				myInsert.setLong (32, esmpId.longValue());
			if (espId == null)
				myInsert.setNull(33, Types.INTEGER);
			else
				myInsert.setLong (33, espId.longValue());
			if (qaId == null)
				myInsert.setNull(34, Types.INTEGER);
			else
				myInsert.setLong (34, qaId.longValue());
			if (neId == null)
				myInsert.setNull(35, Types.INTEGER);
			else
				myInsert.setLong (35, neId.longValue());
			if (fpId == null)
				myInsert.setNull(36, Types.INTEGER);
			else
				myInsert.setLong (36, fpId.longValue());
			myInsert.setLong (37, inheritPrivs.longValue());
			myInsert.setLong (38, creatorUId.longValue());
			myInsert.setLong (39, createTs.longValue());
			myInsert.setLong (40, changerUId.longValue());
			myInsert.setLong (41, changeTs.longValue());
			myInsert.setLong(42, env.tx.versionId);
			myInsert.setLong(43, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "SchedulingEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		closeDBObject(env);
		insertDBObject(env);
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		closeDBObject(env);
	}

	private void closeDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				final boolean postgres = driverName.startsWith("PostgreSQL");
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE " + squote + "SCHEDULING_ENTITY" + equote +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181955", "SchedulingEntity : $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong(1, env.tx.versionId);
			myUpdate.setLong(2, changeTs.longValue());
			myUpdate.setLong(3, changerUId.longValue());
			myUpdate.setLong(4, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "SchedulingEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkType(Integer p)
	{
		switch (p.intValue()) {
			case SDMSSchedulingEntity.JOB:
			case SDMSSchedulingEntity.BATCH:
			case SDMSSchedulingEntity.MILESTONE:
				return true;
		}
		return false;
	}
	static public boolean checkTruncLog(Boolean p)
	{
		if(p == null) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.NOTRUNC) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.TRUNC) return true;
		return false;
	}
	static public boolean checkTruncErrlog(Boolean p)
	{
		if(p == null) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.NOTRUNC) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.TRUNC) return true;
		return false;
	}
	static public boolean checkAgingBase(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
			case SDMSSchedulingEntity.MINUTE:
			case SDMSSchedulingEntity.HOUR:
			case SDMSSchedulingEntity.DAY:
			case SDMSSchedulingEntity.WEEK:
			case SDMSSchedulingEntity.MONTH:
			case SDMSSchedulingEntity.YEAR:
				return true;
		}
		return false;
	}
	static public boolean checkSubmitSuspended(Boolean p)
	{
		if(p.booleanValue() == SDMSSchedulingEntity.SUSPEND) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.NOSUSPEND) return true;
		return false;
	}
	static public boolean checkResumeBase(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
			case SDMSSchedulingEntity.MINUTE:
			case SDMSSchedulingEntity.HOUR:
			case SDMSSchedulingEntity.DAY:
			case SDMSSchedulingEntity.WEEK:
			case SDMSSchedulingEntity.MONTH:
			case SDMSSchedulingEntity.YEAR:
				return true;
		}
		return false;
	}
	static public boolean checkMasterSubmittable(Boolean p)
	{
		if(p.booleanValue() == SDMSSchedulingEntity.MASTER) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.NOMASTER) return true;
		return false;
	}
	static public boolean checkTimeoutBase(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
			case SDMSSchedulingEntity.MINUTE:
			case SDMSSchedulingEntity.HOUR:
			case SDMSSchedulingEntity.DAY:
			case SDMSSchedulingEntity.WEEK:
			case SDMSSchedulingEntity.MONTH:
			case SDMSSchedulingEntity.YEAR:
				return true;
		}
		return false;
	}
	static public boolean checkSameNode(Boolean p)
	{
		if(p == null) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.SAME_NODE) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.NOSAME_NODE) return true;
		return false;
	}
	static public boolean checkGangSchedule(Boolean p)
	{
		if(p == null) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.GANG) return true;
		if(p.booleanValue() == SDMSSchedulingEntity.NOGANG) return true;
		return false;
	}
	static public boolean checkDependencyOperation(Integer p)
	{
		switch (p.intValue()) {
			case SDMSSchedulingEntity.AND:
			case SDMSSchedulingEntity.OR:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : SchedulingEntity", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "folderId : " + folderId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "type : " + type, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "runProgram : " + runProgram, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rerunProgram : " + rerunProgram, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "killProgram : " + killProgram, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "workdir : " + workdir, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "logfile : " + logfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "truncLog : " + truncLog, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errlogfile : " + errlogfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "truncErrlog : " + truncErrlog, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "expectedRuntime : " + expectedRuntime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "expectedFinaltime : " + expectedFinaltime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "getExpectedRuntime : " + getExpectedRuntime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "priority : " + priority, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "minPriority : " + minPriority, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "agingAmount : " + agingAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "agingBase : " + agingBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "submitSuspended : " + submitSuspended, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeAt : " + resumeAt, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeIn : " + resumeIn, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeBase : " + resumeBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "masterSubmittable : " + masterSubmittable, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "timeoutAmount : " + timeoutAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "timeoutBase : " + timeoutBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "timeoutStateId : " + timeoutStateId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "sameNode : " + sameNode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "gangSchedule : " + gangSchedule, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "dependencyOperation : " + dependencyOperation, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "esmpId : " + esmpId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "espId : " + espId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "qaId : " + qaId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "neId : " + neId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fpId : " + fpId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "inheritPrivs : " + inheritPrivs, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "name                : " + name + "\n" +
		        indentString + "folderId            : " + folderId + "\n" +
		        indentString + "ownerId             : " + ownerId + "\n" +
		        indentString + "type                : " + type + "\n" +
		        indentString + "runProgram          : " + runProgram + "\n" +
		        indentString + "rerunProgram        : " + rerunProgram + "\n" +
		        indentString + "killProgram         : " + killProgram + "\n" +
		        indentString + "workdir             : " + workdir + "\n" +
		        indentString + "logfile             : " + logfile + "\n" +
		        indentString + "truncLog            : " + truncLog + "\n" +
		        indentString + "errlogfile          : " + errlogfile + "\n" +
		        indentString + "truncErrlog         : " + truncErrlog + "\n" +
		        indentString + "expectedRuntime     : " + expectedRuntime + "\n" +
		        indentString + "expectedFinaltime   : " + expectedFinaltime + "\n" +
		        indentString + "getExpectedRuntime  : " + getExpectedRuntime + "\n" +
		        indentString + "priority            : " + priority + "\n" +
		        indentString + "minPriority         : " + minPriority + "\n" +
		        indentString + "agingAmount         : " + agingAmount + "\n" +
		        indentString + "agingBase           : " + agingBase + "\n" +
		        indentString + "submitSuspended     : " + submitSuspended + "\n" +
		        indentString + "resumeAt            : " + resumeAt + "\n" +
		        indentString + "resumeIn            : " + resumeIn + "\n" +
		        indentString + "resumeBase          : " + resumeBase + "\n" +
		        indentString + "masterSubmittable   : " + masterSubmittable + "\n" +
		        indentString + "timeoutAmount       : " + timeoutAmount + "\n" +
		        indentString + "timeoutBase         : " + timeoutBase + "\n" +
		        indentString + "timeoutStateId      : " + timeoutStateId + "\n" +
		        indentString + "sameNode            : " + sameNode + "\n" +
		        indentString + "gangSchedule        : " + gangSchedule + "\n" +
		        indentString + "dependencyOperation : " + dependencyOperation + "\n" +
		        indentString + "esmpId              : " + esmpId + "\n" +
		        indentString + "espId               : " + espId + "\n" +
		        indentString + "qaId                : " + qaId + "\n" +
		        indentString + "neId                : " + neId + "\n" +
		        indentString + "fpId                : " + fpId + "\n" +
		        indentString + "inheritPrivs        : " + inheritPrivs + "\n" +
		        indentString + "creatorUId          : " + creatorUId + "\n" +
		        indentString + "createTs            : " + createTs + "\n" +
		        indentString + "changerUId          : " + changerUId + "\n" +
		        indentString + "changeTs            : " + changeTs + "\n" +
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
