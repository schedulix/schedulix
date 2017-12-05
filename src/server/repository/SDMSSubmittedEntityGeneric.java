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

public class SDMSSubmittedEntityGeneric extends SDMSObject
	implements Cloneable
{

	public static final int SUBMITTED = 0;
	public static final int DEPENDENCY_WAIT = 1;
	public static final int SYNCHRONIZE_WAIT = 2;
	public static final int RESOURCE_WAIT = 3;
	public static final int RUNNABLE = 4;
	public static final int STARTING = 5;
	public static final int STARTED = 6;
	public static final int RUNNING = 7;
	public static final int TO_KILL = 8;
	public static final int KILLED = 9;
	public static final int CANCELLED = 10;
	public static final int FINISHED = 11;
	public static final int FINAL = 12;
	public static final int BROKEN_ACTIVE = 13;
	public static final int BROKEN_FINISHED = 14;
	public static final int ERROR = 15;
	public static final int UNREACHABLE = 16;
	public static final int SUSPEND = 1;
	public static final int NOSUSPEND = 0;
	public static final int ADMINSUSPEND = 2;
	public static final int UH_IGNORE = SDMSDependencyDefinition.IGNORE;
	public static final int UH_ERROR = SDMSDependencyDefinition.ERROR;
	public static final int UH_SUSPEND = SDMSDependencyDefinition.SUSPEND;
	public static final int MERGE_LOCAL = SDMSSchedulingHierarchy.MERGE_LOCAL;
	public static final int MERGE_GLOBAL = SDMSSchedulingHierarchy.MERGE_GLOBAL;
	public static final int NOMERGE = SDMSSchedulingHierarchy.NOMERGE;
	public static final int FAILURE = SDMSSchedulingHierarchy.FAILURE;
	public static final char PID_SEP = '@';
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;

	public final static int nr_id = 1;
	public final static int nr_accessKey = 2;
	public final static int nr_masterId = 3;
	public final static int nr_submitTag = 4;
	public final static int nr_unresolvedHandling = 5;
	public final static int nr_seId = 6;
	public final static int nr_childTag = 7;
	public final static int nr_seVersion = 8;
	public final static int nr_ownerId = 9;
	public final static int nr_parentId = 10;
	public final static int nr_scopeId = 11;
	public final static int nr_isStatic = 12;
	public final static int nr_isDisabled = 13;
	public final static int nr_oldState = 14;
	public final static int nr_mergeMode = 15;
	public final static int nr_state = 16;
	public final static int nr_jobEsdId = 17;
	public final static int nr_jobEsdPref = 18;
	public final static int nr_jobIsFinal = 19;
	public final static int nr_jobIsRestartable = 20;
	public final static int nr_finalEsdId = 21;
	public final static int nr_exitCode = 22;
	public final static int nr_commandline = 23;
	public final static int nr_rrCommandline = 24;
	public final static int nr_rerunSeq = 25;
	public final static int nr_isReplaced = 26;
	public final static int nr_isCancelled = 27;
	public final static int nr_baseSmeId = 28;
	public final static int nr_reasonSmeId = 29;
	public final static int nr_fireSmeId = 30;
	public final static int nr_fireSeId = 31;
	public final static int nr_trId = 32;
	public final static int nr_trSdIdOld = 33;
	public final static int nr_trSdIdNew = 34;
	public final static int nr_trSeq = 35;
	public final static int nr_workdir = 36;
	public final static int nr_logfile = 37;
	public final static int nr_errlogfile = 38;
	public final static int nr_pid = 39;
	public final static int nr_extPid = 40;
	public final static int nr_errorMsg = 41;
	public final static int nr_killId = 42;
	public final static int nr_killExitCode = 43;
	public final static int nr_isSuspended = 44;
	public final static int nr_isSuspendedLocal = 45;
	public final static int nr_priority = 46;
	public final static int nr_rawPriority = 47;
	public final static int nr_nice = 48;
	public final static int nr_npNice = 49;
	public final static int nr_minPriority = 50;
	public final static int nr_agingAmount = 51;
	public final static int nr_parentSuspended = 52;
	public final static int nr_childSuspended = 53;
	public final static int nr_warnCount = 54;
	public final static int nr_warnLink = 55;
	public final static int nr_submitTs = 56;
	public final static int nr_resumeTs = 57;
	public final static int nr_syncTs = 58;
	public final static int nr_resourceTs = 59;
	public final static int nr_runnableTs = 60;
	public final static int nr_startTs = 61;
	public final static int nr_finishTs = 62;
	public final static int nr_finalTs = 63;
	public final static int nr_cntSubmitted = 64;
	public final static int nr_cntDependencyWait = 65;
	public final static int nr_cntSynchronizeWait = 66;
	public final static int nr_cntResourceWait = 67;
	public final static int nr_cntRunnable = 68;
	public final static int nr_cntStarting = 69;
	public final static int nr_cntStarted = 70;
	public final static int nr_cntRunning = 71;
	public final static int nr_cntToKill = 72;
	public final static int nr_cntKilled = 73;
	public final static int nr_cntCancelled = 74;
	public final static int nr_cntFinished = 75;
	public final static int nr_cntFinal = 76;
	public final static int nr_cntBrokenActive = 77;
	public final static int nr_cntBrokenFinished = 78;
	public final static int nr_cntError = 79;
	public final static int nr_cntUnreachable = 80;
	public final static int nr_cntRestartable = 81;
	public final static int nr_cntWarn = 82;
	public final static int nr_cntPending = 83;
	public final static int nr_idleTs = 84;
	public final static int nr_idleTime = 85;
	public final static int nr_statisticTs = 86;
	public final static int nr_dependencyWaitTime = 87;
	public final static int nr_suspendTime = 88;
	public final static int nr_syncTime = 89;
	public final static int nr_resourceTime = 90;
	public final static int nr_jobserverTime = 91;
	public final static int nr_restartableTime = 92;
	public final static int nr_childWaitTime = 93;
	public final static int nr_opSusresTs = 94;
	public final static int nr_npeId = 95;
	public final static int nr_creatorUId = 96;
	public final static int nr_createTs = 97;
	public final static int nr_changerUId = 98;
	public final static int nr_changeTs = 99;

	public static String tableName = SDMSSubmittedEntityTableGeneric.tableName;

	protected Long accessKey;
	protected Long masterId;
	protected String submitTag;
	protected Integer unresolvedHandling;
	protected Long seId;
	protected String childTag;
	protected Long seVersion;
	protected Long ownerId;
	protected Long parentId;
	protected Long scopeId;
	protected Boolean isStatic;
	protected Boolean isDisabled;
	protected Integer oldState;
	protected Integer mergeMode;
	protected Integer state;
	protected Long jobEsdId;
	protected Integer jobEsdPref;
	protected Boolean jobIsFinal;
	protected Boolean jobIsRestartable;
	protected Long finalEsdId;
	protected Integer exitCode;
	protected String commandline;
	protected String rrCommandline;
	protected Integer rerunSeq;
	protected Boolean isReplaced;
	protected Boolean isCancelled;
	protected Long baseSmeId;
	protected Long reasonSmeId;
	protected Long fireSmeId;
	protected Long fireSeId;
	protected Long trId;
	protected Long trSdIdOld;
	protected Long trSdIdNew;
	protected Integer trSeq;
	protected String workdir;
	protected String logfile;
	protected String errlogfile;
	protected String pid;
	protected String extPid;
	protected String errorMsg;
	protected Long killId;
	protected Integer killExitCode;
	protected Integer isSuspended;
	protected Boolean isSuspendedLocal;
	protected Integer priority;
	protected Integer rawPriority;
	protected Integer nice;
	protected Integer npNice;
	protected Integer minPriority;
	protected Integer agingAmount;
	protected Integer parentSuspended;
	protected Integer childSuspended;
	protected Integer warnCount;
	protected Long warnLink;
	protected Long submitTs;
	protected Long resumeTs;
	protected Long syncTs;
	protected Long resourceTs;
	protected Long runnableTs;
	protected Long startTs;
	protected Long finishTs;
	protected Long finalTs;
	protected Integer cntSubmitted;
	protected Integer cntDependencyWait;
	protected Integer cntSynchronizeWait;
	protected Integer cntResourceWait;
	protected Integer cntRunnable;
	protected Integer cntStarting;
	protected Integer cntStarted;
	protected Integer cntRunning;
	protected Integer cntToKill;
	protected Integer cntKilled;
	protected Integer cntCancelled;
	protected Integer cntFinished;
	protected Integer cntFinal;
	protected Integer cntBrokenActive;
	protected Integer cntBrokenFinished;
	protected Integer cntError;
	protected Integer cntUnreachable;
	protected Integer cntRestartable;
	protected Integer cntWarn;
	protected Integer cntPending;
	protected Integer idleTs;
	protected Integer idleTime;
	protected Integer statisticTs;
	protected Integer dependencyWaitTime;
	protected Integer suspendTime;
	protected Integer syncTime;
	protected Integer resourceTime;
	protected Integer jobserverTime;
	protected Integer restartableTime;
	protected Integer childWaitTime;
	protected Long opSusresTs;
	protected Long npeId;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSSubmittedEntityGeneric(
	        SystemEnvironment env,
	        Long p_accessKey,
	        Long p_masterId,
	        String p_submitTag,
	        Integer p_unresolvedHandling,
	        Long p_seId,
	        String p_childTag,
	        Long p_seVersion,
	        Long p_ownerId,
	        Long p_parentId,
	        Long p_scopeId,
	        Boolean p_isStatic,
	        Boolean p_isDisabled,
	        Integer p_oldState,
	        Integer p_mergeMode,
	        Integer p_state,
	        Long p_jobEsdId,
	        Integer p_jobEsdPref,
	        Boolean p_jobIsFinal,
	        Boolean p_jobIsRestartable,
	        Long p_finalEsdId,
	        Integer p_exitCode,
	        String p_commandline,
	        String p_rrCommandline,
	        Integer p_rerunSeq,
	        Boolean p_isReplaced,
	        Boolean p_isCancelled,
	        Long p_baseSmeId,
	        Long p_reasonSmeId,
	        Long p_fireSmeId,
	        Long p_fireSeId,
	        Long p_trId,
	        Long p_trSdIdOld,
	        Long p_trSdIdNew,
	        Integer p_trSeq,
	        String p_workdir,
	        String p_logfile,
	        String p_errlogfile,
	        String p_pid,
	        String p_extPid,
	        String p_errorMsg,
	        Long p_killId,
	        Integer p_killExitCode,
	        Integer p_isSuspended,
	        Boolean p_isSuspendedLocal,
	        Integer p_priority,
	        Integer p_rawPriority,
	        Integer p_nice,
	        Integer p_npNice,
	        Integer p_minPriority,
	        Integer p_agingAmount,
	        Integer p_parentSuspended,
	        Integer p_childSuspended,
	        Integer p_warnCount,
	        Long p_warnLink,
	        Long p_submitTs,
	        Long p_resumeTs,
	        Long p_syncTs,
	        Long p_resourceTs,
	        Long p_runnableTs,
	        Long p_startTs,
	        Long p_finishTs,
	        Long p_finalTs,
	        Integer p_cntSubmitted,
	        Integer p_cntDependencyWait,
	        Integer p_cntSynchronizeWait,
	        Integer p_cntResourceWait,
	        Integer p_cntRunnable,
	        Integer p_cntStarting,
	        Integer p_cntStarted,
	        Integer p_cntRunning,
	        Integer p_cntToKill,
	        Integer p_cntKilled,
	        Integer p_cntCancelled,
	        Integer p_cntFinished,
	        Integer p_cntFinal,
	        Integer p_cntBrokenActive,
	        Integer p_cntBrokenFinished,
	        Integer p_cntError,
	        Integer p_cntUnreachable,
	        Integer p_cntRestartable,
	        Integer p_cntWarn,
	        Integer p_cntPending,
	        Integer p_idleTs,
	        Integer p_idleTime,
	        Integer p_statisticTs,
	        Integer p_dependencyWaitTime,
	        Integer p_suspendTime,
	        Integer p_syncTime,
	        Integer p_resourceTime,
	        Integer p_jobserverTime,
	        Integer p_restartableTime,
	        Integer p_childWaitTime,
	        Long p_opSusresTs,
	        Long p_npeId,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSSubmittedEntityTableGeneric.table);
		accessKey = p_accessKey;
		masterId = p_masterId;
		if (p_submitTag != null && p_submitTag.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "submitTag", "32")
			);
		}
		submitTag = p_submitTag;
		unresolvedHandling = p_unresolvedHandling;
		seId = p_seId;
		if (p_childTag != null && p_childTag.length() > 70) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "childTag", "70")
			);
		}
		childTag = p_childTag;
		seVersion = p_seVersion;
		ownerId = p_ownerId;
		parentId = p_parentId;
		scopeId = p_scopeId;
		isStatic = p_isStatic;
		isDisabled = p_isDisabled;
		oldState = p_oldState;
		mergeMode = p_mergeMode;
		state = p_state;
		jobEsdId = p_jobEsdId;
		jobEsdPref = p_jobEsdPref;
		jobIsFinal = p_jobIsFinal;
		jobIsRestartable = p_jobIsRestartable;
		finalEsdId = p_finalEsdId;
		exitCode = p_exitCode;
		if (p_commandline != null && p_commandline.length() > 512) {
			p_commandline = p_commandline.substring(0,512);
		}
		commandline = p_commandline;
		if (p_rrCommandline != null && p_rrCommandline.length() > 512) {
			p_rrCommandline = p_rrCommandline.substring(0,512);
		}
		rrCommandline = p_rrCommandline;
		rerunSeq = p_rerunSeq;
		isReplaced = p_isReplaced;
		isCancelled = p_isCancelled;
		baseSmeId = p_baseSmeId;
		reasonSmeId = p_reasonSmeId;
		fireSmeId = p_fireSmeId;
		fireSeId = p_fireSeId;
		trId = p_trId;
		trSdIdOld = p_trSdIdOld;
		trSdIdNew = p_trSdIdNew;
		trSeq = p_trSeq;
		if (p_workdir != null && p_workdir.length() > 512) {
			p_workdir = p_workdir.substring(0,512);
		}
		workdir = p_workdir;
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
			                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "pid", "32")
			);
		}
		pid = p_pid;
		if (p_extPid != null && p_extPid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "extPid", "32")
			);
		}
		extPid = p_extPid;
		if (p_errorMsg != null && p_errorMsg.length() > 256) {
			p_errorMsg = p_errorMsg.substring(0,256);
		}
		errorMsg = p_errorMsg;
		killId = p_killId;
		killExitCode = p_killExitCode;
		isSuspended = p_isSuspended;
		isSuspendedLocal = p_isSuspendedLocal;
		priority = p_priority;
		rawPriority = p_rawPriority;
		nice = p_nice;
		npNice = p_npNice;
		minPriority = p_minPriority;
		agingAmount = p_agingAmount;
		parentSuspended = p_parentSuspended;
		childSuspended = p_childSuspended;
		warnCount = p_warnCount;
		warnLink = p_warnLink;
		submitTs = p_submitTs;
		resumeTs = p_resumeTs;
		syncTs = p_syncTs;
		resourceTs = p_resourceTs;
		runnableTs = p_runnableTs;
		startTs = p_startTs;
		finishTs = p_finishTs;
		finalTs = p_finalTs;
		cntSubmitted = p_cntSubmitted;
		cntDependencyWait = p_cntDependencyWait;
		cntSynchronizeWait = p_cntSynchronizeWait;
		cntResourceWait = p_cntResourceWait;
		cntRunnable = p_cntRunnable;
		cntStarting = p_cntStarting;
		cntStarted = p_cntStarted;
		cntRunning = p_cntRunning;
		cntToKill = p_cntToKill;
		cntKilled = p_cntKilled;
		cntCancelled = p_cntCancelled;
		cntFinished = p_cntFinished;
		cntFinal = p_cntFinal;
		cntBrokenActive = p_cntBrokenActive;
		cntBrokenFinished = p_cntBrokenFinished;
		cntError = p_cntError;
		cntUnreachable = p_cntUnreachable;
		cntRestartable = p_cntRestartable;
		cntWarn = p_cntWarn;
		cntPending = p_cntPending;
		idleTs = p_idleTs;
		idleTime = p_idleTime;
		statisticTs = p_statisticTs;
		dependencyWaitTime = p_dependencyWaitTime;
		suspendTime = p_suspendTime;
		syncTime = p_syncTime;
		resourceTime = p_resourceTime;
		jobserverTime = p_jobserverTime;
		restartableTime = p_restartableTime;
		childWaitTime = p_childWaitTime;
		opSusresTs = p_opSusresTs;
		npeId = p_npeId;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getAccessKey (SystemEnvironment env)
	throws SDMSException
	{
		return (accessKey);
	}

	public	void setAccessKey (SystemEnvironment env, Long p_accessKey)
	throws SDMSException
	{
		if(accessKey.equals(p_accessKey)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.accessKey = p_accessKey;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		return (masterId);
	}

	public	void setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		if(masterId.equals(p_masterId)) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.masterId = p_masterId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1409);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getSubmitTag (SystemEnvironment env)
	throws SDMSException
	{
		return (submitTag);
	}

	public	void setSubmitTag (SystemEnvironment env, String p_submitTag)
	throws SDMSException
	{
		if(p_submitTag != null && p_submitTag.equals(submitTag)) return;
		if(p_submitTag == null && submitTag == null) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			if (p_submitTag != null && p_submitTag.length() > 32) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "submitTag", "32")
				);
			}
			o.submitTag = p_submitTag;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 2);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getUnresolvedHandling (SystemEnvironment env)
	throws SDMSException
	{
		return (unresolvedHandling);
	}

	public String getUnresolvedHandlingAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getUnresolvedHandling (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
			case SDMSSubmittedEntity.UH_IGNORE:
				return "UH_IGNORE";
			case SDMSSubmittedEntity.UH_SUSPEND:
				return "UH_SUSPEND";
			case SDMSSubmittedEntity.UH_ERROR:
				return "UH_ERROR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SubmittedEntity.unresolvedHandling: $1",
		                          getUnresolvedHandling (env)));
	}

	public	void setUnresolvedHandling (SystemEnvironment env, Integer p_unresolvedHandling)
	throws SDMSException
	{
		if(p_unresolvedHandling != null && p_unresolvedHandling.equals(unresolvedHandling)) return;
		if(p_unresolvedHandling == null && unresolvedHandling == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.unresolvedHandling = p_unresolvedHandling;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.seId = p_seId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 3460);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getChildTag (SystemEnvironment env)
	throws SDMSException
	{
		return (childTag);
	}

	public	void setChildTag (SystemEnvironment env, String p_childTag)
	throws SDMSException
	{
		if(p_childTag != null && p_childTag.equals(childTag)) return;
		if(p_childTag == null && childTag == null) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			if (p_childTag != null && p_childTag.length() > 70) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "childTag", "70")
				);
			}
			o.childTag = p_childTag;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1024);
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.seVersion = p_seVersion;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
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
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.ownerId = p_ownerId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 8);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		return (parentId);
	}

	public	void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		if(p_parentId != null && p_parentId.equals(parentId)) return;
		if(p_parentId == null && parentId == null) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.parentId = p_parentId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 7184);
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
		if(p_scopeId != null && p_scopeId.equals(scopeId)) return;
		if(p_scopeId == null && scopeId == null) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.scopeId = p_scopeId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 32);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Boolean getIsStatic (SystemEnvironment env)
	throws SDMSException
	{
		return (isStatic);
	}

	public	void setIsStatic (SystemEnvironment env, Boolean p_isStatic)
	throws SDMSException
	{
		if(isStatic.equals(p_isStatic)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.isStatic = p_isStatic;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsDisabled (SystemEnvironment env)
	throws SDMSException
	{
		return (isDisabled);
	}

	public	void setIsDisabled (SystemEnvironment env, Boolean p_isDisabled)
	throws SDMSException
	{
		if(isDisabled.equals(p_isDisabled)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.isDisabled = p_isDisabled;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getOldState (SystemEnvironment env)
	throws SDMSException
	{
		return (oldState);
	}

	public	void setOldState (SystemEnvironment env, Integer p_oldState)
	throws SDMSException
	{
		if(p_oldState != null && p_oldState.equals(oldState)) return;
		if(p_oldState == null && oldState == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.oldState = p_oldState;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getMergeMode (SystemEnvironment env)
	throws SDMSException
	{
		return (mergeMode);
	}

	public String getMergeModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getMergeMode (env);
		switch (v.intValue()) {
			case SDMSSubmittedEntity.MERGE_LOCAL:
				return "MERGE_LOCAL";
			case SDMSSubmittedEntity.MERGE_GLOBAL:
				return "MERGE_GLOBAL";
			case SDMSSubmittedEntity.NOMERGE:
				return "NOMERGE";
			case SDMSSubmittedEntity.FAILURE:
				return "FAILURE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SubmittedEntity.mergeMode: $1",
		                          getMergeMode (env)));
	}

	public	void setMergeMode (SystemEnvironment env, Integer p_mergeMode)
	throws SDMSException
	{
		if(mergeMode.equals(p_mergeMode)) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.mergeMode = p_mergeMode;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 128);
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
			case SDMSSubmittedEntity.SUBMITTED:
				return "SUBMITTED";
			case SDMSSubmittedEntity.DEPENDENCY_WAIT:
				return "DEPENDENCY_WAIT";
			case SDMSSubmittedEntity.SYNCHRONIZE_WAIT:
				return "SYNCHRONIZE_WAIT";
			case SDMSSubmittedEntity.RESOURCE_WAIT:
				return "RESOURCE_WAIT";
			case SDMSSubmittedEntity.RUNNABLE:
				return "RUNNABLE";
			case SDMSSubmittedEntity.STARTING:
				return "STARTING";
			case SDMSSubmittedEntity.STARTED:
				return "STARTED";
			case SDMSSubmittedEntity.RUNNING:
				return "RUNNING";
			case SDMSSubmittedEntity.TO_KILL:
				return "TO_KILL";
			case SDMSSubmittedEntity.KILLED:
				return "KILLED";
			case SDMSSubmittedEntity.CANCELLED:
				return "CANCELLED";
			case SDMSSubmittedEntity.FINISHED:
				return "FINISHED";
			case SDMSSubmittedEntity.FINAL:
				return "FINAL";
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				return "BROKEN_ACTIVE";
			case SDMSSubmittedEntity.BROKEN_FINISHED:
				return "BROKEN_FINISHED";
			case SDMSSubmittedEntity.ERROR:
				return "ERROR";
			case SDMSSubmittedEntity.UNREACHABLE:
				return "UNREACHABLE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SubmittedEntity.state: $1",
		                          getState (env)));
	}

	public	void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		if(state.equals(p_state)) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.state = p_state;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 64);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getJobEsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (jobEsdId);
	}

	public	void setJobEsdId (SystemEnvironment env, Long p_jobEsdId)
	throws SDMSException
	{
		if(p_jobEsdId != null && p_jobEsdId.equals(jobEsdId)) return;
		if(p_jobEsdId == null && jobEsdId == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.jobEsdId = p_jobEsdId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getJobEsdPref (SystemEnvironment env)
	throws SDMSException
	{
		return (jobEsdPref);
	}

	public	void setJobEsdPref (SystemEnvironment env, Integer p_jobEsdPref)
	throws SDMSException
	{
		if(p_jobEsdPref != null && p_jobEsdPref.equals(jobEsdPref)) return;
		if(p_jobEsdPref == null && jobEsdPref == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.jobEsdPref = p_jobEsdPref;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getJobIsFinal (SystemEnvironment env)
	throws SDMSException
	{
		return (jobIsFinal);
	}

	public	void setJobIsFinal (SystemEnvironment env, Boolean p_jobIsFinal)
	throws SDMSException
	{
		if(jobIsFinal.equals(p_jobIsFinal)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.jobIsFinal = p_jobIsFinal;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getJobIsRestartable (SystemEnvironment env)
	throws SDMSException
	{
		return (jobIsRestartable);
	}

	public	void setJobIsRestartable (SystemEnvironment env, Boolean p_jobIsRestartable)
	throws SDMSException
	{
		if(jobIsRestartable.equals(p_jobIsRestartable)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.jobIsRestartable = p_jobIsRestartable;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getFinalEsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (finalEsdId);
	}

	public	void setFinalEsdId (SystemEnvironment env, Long p_finalEsdId)
	throws SDMSException
	{
		if(p_finalEsdId != null && p_finalEsdId.equals(finalEsdId)) return;
		if(p_finalEsdId == null && finalEsdId == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.finalEsdId = p_finalEsdId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.exitCode = p_exitCode;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_commandline != null && p_commandline.length() > 512) {
			p_commandline = p_commandline.substring(0,512);
		}
		o.commandline = p_commandline;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getRrCommandline (SystemEnvironment env)
	throws SDMSException
	{
		return (rrCommandline);
	}

	public	void setRrCommandline (SystemEnvironment env, String p_rrCommandline)
	throws SDMSException
	{
		if(p_rrCommandline != null && p_rrCommandline.equals(rrCommandline)) return;
		if(p_rrCommandline == null && rrCommandline == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_rrCommandline != null && p_rrCommandline.length() > 512) {
			p_rrCommandline = p_rrCommandline.substring(0,512);
		}
		o.rrCommandline = p_rrCommandline;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getRerunSeq (SystemEnvironment env)
	throws SDMSException
	{
		return (rerunSeq);
	}

	public	void setRerunSeq (SystemEnvironment env, Integer p_rerunSeq)
	throws SDMSException
	{
		if(rerunSeq.equals(p_rerunSeq)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.rerunSeq = p_rerunSeq;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsReplaced (SystemEnvironment env)
	throws SDMSException
	{
		return (isReplaced);
	}

	public	void setIsReplaced (SystemEnvironment env, Boolean p_isReplaced)
	throws SDMSException
	{
		if(isReplaced.equals(p_isReplaced)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.isReplaced = p_isReplaced;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsCancelled (SystemEnvironment env)
	throws SDMSException
	{
		return (isCancelled);
	}

	public	void setIsCancelled (SystemEnvironment env, Boolean p_isCancelled)
	throws SDMSException
	{
		if(p_isCancelled != null && p_isCancelled.equals(isCancelled)) return;
		if(p_isCancelled == null && isCancelled == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.isCancelled = p_isCancelled;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getBaseSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (baseSmeId);
	}

	public	void setBaseSmeId (SystemEnvironment env, Long p_baseSmeId)
	throws SDMSException
	{
		if(p_baseSmeId != null && p_baseSmeId.equals(baseSmeId)) return;
		if(p_baseSmeId == null && baseSmeId == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.baseSmeId = p_baseSmeId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getReasonSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (reasonSmeId);
	}

	public	void setReasonSmeId (SystemEnvironment env, Long p_reasonSmeId)
	throws SDMSException
	{
		if(p_reasonSmeId != null && p_reasonSmeId.equals(reasonSmeId)) return;
		if(p_reasonSmeId == null && reasonSmeId == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.reasonSmeId = p_reasonSmeId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getFireSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (fireSmeId);
	}

	public	void setFireSmeId (SystemEnvironment env, Long p_fireSmeId)
	throws SDMSException
	{
		if(p_fireSmeId != null && p_fireSmeId.equals(fireSmeId)) return;
		if(p_fireSmeId == null && fireSmeId == null) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.fireSmeId = p_fireSmeId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 512);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getFireSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (fireSeId);
	}

	public	void setFireSeId (SystemEnvironment env, Long p_fireSeId)
	throws SDMSException
	{
		if(p_fireSeId != null && p_fireSeId.equals(fireSeId)) return;
		if(p_fireSeId == null && fireSeId == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.fireSeId = p_fireSeId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getTrId (SystemEnvironment env)
	throws SDMSException
	{
		return (trId);
	}

	public	void setTrId (SystemEnvironment env, Long p_trId)
	throws SDMSException
	{
		if(p_trId != null && p_trId.equals(trId)) return;
		if(p_trId == null && trId == null) return;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.trId = p_trId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4608);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getTrSdIdOld (SystemEnvironment env)
	throws SDMSException
	{
		return (trSdIdOld);
	}

	public	void setTrSdIdOld (SystemEnvironment env, Long p_trSdIdOld)
	throws SDMSException
	{
		if(p_trSdIdOld != null && p_trSdIdOld.equals(trSdIdOld)) return;
		if(p_trSdIdOld == null && trSdIdOld == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.trSdIdOld = p_trSdIdOld;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getTrSdIdNew (SystemEnvironment env)
	throws SDMSException
	{
		return (trSdIdNew);
	}

	public	void setTrSdIdNew (SystemEnvironment env, Long p_trSdIdNew)
	throws SDMSException
	{
		if(p_trSdIdNew != null && p_trSdIdNew.equals(trSdIdNew)) return;
		if(p_trSdIdNew == null && trSdIdNew == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.trSdIdNew = p_trSdIdNew;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getTrSeq (SystemEnvironment env)
	throws SDMSException
	{
		return (trSeq);
	}

	public	void setTrSeq (SystemEnvironment env, Integer p_trSeq)
	throws SDMSException
	{
		if(trSeq.equals(p_trSeq)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.trSeq = p_trSeq;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_workdir != null && p_workdir.length() > 512) {
			p_workdir = p_workdir.substring(0,512);
		}
		o.workdir = p_workdir;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_logfile != null && p_logfile.length() > 512) {
			p_logfile = p_logfile.substring(0,512);
		}
		o.logfile = p_logfile;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_errlogfile != null && p_errlogfile.length() > 512) {
			p_errlogfile = p_errlogfile.substring(0,512);
		}
		o.errlogfile = p_errlogfile;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_pid != null && p_pid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "pid", "32")
			);
		}
		o.pid = p_pid;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_extPid != null && p_extPid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "extPid", "32")
			);
		}
		o.extPid = p_extPid;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		if (p_errorMsg != null && p_errorMsg.length() > 256) {
			p_errorMsg = p_errorMsg.substring(0,256);
		}
		o.errorMsg = p_errorMsg;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getKillId (SystemEnvironment env)
	throws SDMSException
	{
		return (killId);
	}

	public	void setKillId (SystemEnvironment env, Long p_killId)
	throws SDMSException
	{
		if(p_killId != null && p_killId.equals(killId)) return;
		if(p_killId == null && killId == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.killId = p_killId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getKillExitCode (SystemEnvironment env)
	throws SDMSException
	{
		return (killExitCode);
	}

	public	void setKillExitCode (SystemEnvironment env, Integer p_killExitCode)
	throws SDMSException
	{
		if(p_killExitCode != null && p_killExitCode.equals(killExitCode)) return;
		if(p_killExitCode == null && killExitCode == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.killExitCode = p_killExitCode;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getIsSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (isSuspended);
	}

	public String getIsSuspendedAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getIsSuspended (env);
		switch (v.intValue()) {
			case SDMSSubmittedEntity.ADMINSUSPEND:
				return "ADMINSUSPEND";
			case SDMSSubmittedEntity.SUSPEND:
				return "SUSPEND";
			case SDMSSubmittedEntity.NOSUSPEND:
				return "NOSUSPEND";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SubmittedEntity.isSuspended: $1",
		                          getIsSuspended (env)));
	}

	public	void setIsSuspended (SystemEnvironment env, Integer p_isSuspended)
	throws SDMSException
	{
		if(isSuspended.equals(p_isSuspended)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.isSuspended = p_isSuspended;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsSuspendedLocal (SystemEnvironment env)
	throws SDMSException
	{
		return (isSuspendedLocal);
	}

	public	void setIsSuspendedLocal (SystemEnvironment env, Boolean p_isSuspendedLocal)
	throws SDMSException
	{
		if(p_isSuspendedLocal != null && p_isSuspendedLocal.equals(isSuspendedLocal)) return;
		if(p_isSuspendedLocal == null && isSuspendedLocal == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.isSuspendedLocal = p_isSuspendedLocal;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.priority = p_priority;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getRawPriority (SystemEnvironment env)
	throws SDMSException
	{
		return (rawPriority);
	}

	public	void setRawPriority (SystemEnvironment env, Integer p_rawPriority)
	throws SDMSException
	{
		if(rawPriority.equals(p_rawPriority)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.rawPriority = p_rawPriority;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getNice (SystemEnvironment env)
	throws SDMSException
	{
		return (nice);
	}

	public	void setNice (SystemEnvironment env, Integer p_nice)
	throws SDMSException
	{
		if(nice.equals(p_nice)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.nice = p_nice;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getNpNice (SystemEnvironment env)
	throws SDMSException
	{
		return (npNice);
	}

	public	void setNpNice (SystemEnvironment env, Integer p_npNice)
	throws SDMSException
	{
		if(npNice.equals(p_npNice)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.npNice = p_npNice;
		o.changerUId = env.cEnv.euid();
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
		if(minPriority.equals(p_minPriority)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.minPriority = p_minPriority;
		o.changerUId = env.cEnv.euid();
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
		if(agingAmount.equals(p_agingAmount)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.agingAmount = p_agingAmount;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getParentSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (parentSuspended);
	}

	public	void setParentSuspended (SystemEnvironment env, Integer p_parentSuspended)
	throws SDMSException
	{
		if(parentSuspended.equals(p_parentSuspended)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.parentSuspended = p_parentSuspended;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getChildSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (childSuspended);
	}

	public	void setChildSuspended (SystemEnvironment env, Integer p_childSuspended)
	throws SDMSException
	{
		if(childSuspended.equals(p_childSuspended)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.childSuspended = p_childSuspended;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getWarnCount (SystemEnvironment env)
	throws SDMSException
	{
		return (warnCount);
	}

	public	void setWarnCount (SystemEnvironment env, Integer p_warnCount)
	throws SDMSException
	{
		if(warnCount.equals(p_warnCount)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.warnCount = p_warnCount;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getWarnLink (SystemEnvironment env)
	throws SDMSException
	{
		return (warnLink);
	}

	public	void setWarnLink (SystemEnvironment env, Long p_warnLink)
	throws SDMSException
	{
		if(p_warnLink != null && p_warnLink.equals(warnLink)) return;
		if(p_warnLink == null && warnLink == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.warnLink = p_warnLink;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSubmitTs (SystemEnvironment env)
	throws SDMSException
	{
		return (submitTs);
	}

	public	void setSubmitTs (SystemEnvironment env, Long p_submitTs)
	throws SDMSException
	{
		if(submitTs.equals(p_submitTs)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.submitTs = p_submitTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getResumeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeTs);
	}

	public	void setResumeTs (SystemEnvironment env, Long p_resumeTs)
	throws SDMSException
	{
		if(p_resumeTs != null && p_resumeTs.equals(resumeTs)) return;
		if(p_resumeTs == null && resumeTs == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.resumeTs = p_resumeTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSyncTs (SystemEnvironment env)
	throws SDMSException
	{
		return (syncTs);
	}

	public	void setSyncTs (SystemEnvironment env, Long p_syncTs)
	throws SDMSException
	{
		if(p_syncTs != null && p_syncTs.equals(syncTs)) return;
		if(p_syncTs == null && syncTs == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.syncTs = p_syncTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getResourceTs (SystemEnvironment env)
	throws SDMSException
	{
		return (resourceTs);
	}

	public	void setResourceTs (SystemEnvironment env, Long p_resourceTs)
	throws SDMSException
	{
		if(p_resourceTs != null && p_resourceTs.equals(resourceTs)) return;
		if(p_resourceTs == null && resourceTs == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.resourceTs = p_resourceTs;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.runnableTs = p_runnableTs;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.startTs = p_startTs;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.finishTs = p_finishTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getFinalTs (SystemEnvironment env)
	throws SDMSException
	{
		return (finalTs);
	}

	public	void setFinalTs (SystemEnvironment env, Long p_finalTs)
	throws SDMSException
	{
		if(p_finalTs != null && p_finalTs.equals(finalTs)) return;
		if(p_finalTs == null && finalTs == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.finalTs = p_finalTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntSubmitted (SystemEnvironment env)
	throws SDMSException
	{
		return (cntSubmitted);
	}

	public	void setCntSubmitted (SystemEnvironment env, Integer p_cntSubmitted)
	throws SDMSException
	{
		if(cntSubmitted.equals(p_cntSubmitted)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntSubmitted = p_cntSubmitted;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntDependencyWait (SystemEnvironment env)
	throws SDMSException
	{
		return (cntDependencyWait);
	}

	public	void setCntDependencyWait (SystemEnvironment env, Integer p_cntDependencyWait)
	throws SDMSException
	{
		if(cntDependencyWait.equals(p_cntDependencyWait)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntDependencyWait = p_cntDependencyWait;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntSynchronizeWait (SystemEnvironment env)
	throws SDMSException
	{
		return (cntSynchronizeWait);
	}

	public	void setCntSynchronizeWait (SystemEnvironment env, Integer p_cntSynchronizeWait)
	throws SDMSException
	{
		if(cntSynchronizeWait.equals(p_cntSynchronizeWait)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntSynchronizeWait = p_cntSynchronizeWait;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntResourceWait (SystemEnvironment env)
	throws SDMSException
	{
		return (cntResourceWait);
	}

	public	void setCntResourceWait (SystemEnvironment env, Integer p_cntResourceWait)
	throws SDMSException
	{
		if(cntResourceWait.equals(p_cntResourceWait)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntResourceWait = p_cntResourceWait;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntRunnable (SystemEnvironment env)
	throws SDMSException
	{
		return (cntRunnable);
	}

	public	void setCntRunnable (SystemEnvironment env, Integer p_cntRunnable)
	throws SDMSException
	{
		if(cntRunnable.equals(p_cntRunnable)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntRunnable = p_cntRunnable;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntStarting (SystemEnvironment env)
	throws SDMSException
	{
		return (cntStarting);
	}

	public	void setCntStarting (SystemEnvironment env, Integer p_cntStarting)
	throws SDMSException
	{
		if(cntStarting.equals(p_cntStarting)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntStarting = p_cntStarting;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntStarted (SystemEnvironment env)
	throws SDMSException
	{
		return (cntStarted);
	}

	public	void setCntStarted (SystemEnvironment env, Integer p_cntStarted)
	throws SDMSException
	{
		if(cntStarted.equals(p_cntStarted)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntStarted = p_cntStarted;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntRunning (SystemEnvironment env)
	throws SDMSException
	{
		return (cntRunning);
	}

	public	void setCntRunning (SystemEnvironment env, Integer p_cntRunning)
	throws SDMSException
	{
		if(cntRunning.equals(p_cntRunning)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntRunning = p_cntRunning;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntToKill (SystemEnvironment env)
	throws SDMSException
	{
		return (cntToKill);
	}

	public	void setCntToKill (SystemEnvironment env, Integer p_cntToKill)
	throws SDMSException
	{
		if(cntToKill.equals(p_cntToKill)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntToKill = p_cntToKill;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntKilled (SystemEnvironment env)
	throws SDMSException
	{
		return (cntKilled);
	}

	public	void setCntKilled (SystemEnvironment env, Integer p_cntKilled)
	throws SDMSException
	{
		if(cntKilled.equals(p_cntKilled)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntKilled = p_cntKilled;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntCancelled (SystemEnvironment env)
	throws SDMSException
	{
		return (cntCancelled);
	}

	public	void setCntCancelled (SystemEnvironment env, Integer p_cntCancelled)
	throws SDMSException
	{
		if(cntCancelled.equals(p_cntCancelled)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntCancelled = p_cntCancelled;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntFinished (SystemEnvironment env)
	throws SDMSException
	{
		return (cntFinished);
	}

	public	void setCntFinished (SystemEnvironment env, Integer p_cntFinished)
	throws SDMSException
	{
		if(cntFinished.equals(p_cntFinished)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntFinished = p_cntFinished;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntFinal (SystemEnvironment env)
	throws SDMSException
	{
		return (cntFinal);
	}

	public	void setCntFinal (SystemEnvironment env, Integer p_cntFinal)
	throws SDMSException
	{
		if(cntFinal.equals(p_cntFinal)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntFinal = p_cntFinal;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntBrokenActive (SystemEnvironment env)
	throws SDMSException
	{
		return (cntBrokenActive);
	}

	public	void setCntBrokenActive (SystemEnvironment env, Integer p_cntBrokenActive)
	throws SDMSException
	{
		if(cntBrokenActive.equals(p_cntBrokenActive)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntBrokenActive = p_cntBrokenActive;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntBrokenFinished (SystemEnvironment env)
	throws SDMSException
	{
		return (cntBrokenFinished);
	}

	public	void setCntBrokenFinished (SystemEnvironment env, Integer p_cntBrokenFinished)
	throws SDMSException
	{
		if(cntBrokenFinished.equals(p_cntBrokenFinished)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntBrokenFinished = p_cntBrokenFinished;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntError (SystemEnvironment env)
	throws SDMSException
	{
		return (cntError);
	}

	public	void setCntError (SystemEnvironment env, Integer p_cntError)
	throws SDMSException
	{
		if(cntError.equals(p_cntError)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntError = p_cntError;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntUnreachable (SystemEnvironment env)
	throws SDMSException
	{
		return (cntUnreachable);
	}

	public	void setCntUnreachable (SystemEnvironment env, Integer p_cntUnreachable)
	throws SDMSException
	{
		if(cntUnreachable.equals(p_cntUnreachable)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntUnreachable = p_cntUnreachable;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntRestartable (SystemEnvironment env)
	throws SDMSException
	{
		return (cntRestartable);
	}

	public	void setCntRestartable (SystemEnvironment env, Integer p_cntRestartable)
	throws SDMSException
	{
		if(cntRestartable.equals(p_cntRestartable)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntRestartable = p_cntRestartable;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntWarn (SystemEnvironment env)
	throws SDMSException
	{
		return (cntWarn);
	}

	public	void setCntWarn (SystemEnvironment env, Integer p_cntWarn)
	throws SDMSException
	{
		if(cntWarn.equals(p_cntWarn)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntWarn = p_cntWarn;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getCntPending (SystemEnvironment env)
	throws SDMSException
	{
		return (cntPending);
	}

	public	void setCntPending (SystemEnvironment env, Integer p_cntPending)
	throws SDMSException
	{
		if(cntPending.equals(p_cntPending)) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.cntPending = p_cntPending;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getIdleTs (SystemEnvironment env)
	throws SDMSException
	{
		return (idleTs);
	}

	public	void setIdleTs (SystemEnvironment env, Integer p_idleTs)
	throws SDMSException
	{
		if(p_idleTs != null && p_idleTs.equals(idleTs)) return;
		if(p_idleTs == null && idleTs == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.idleTs = p_idleTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getIdleTime (SystemEnvironment env)
	throws SDMSException
	{
		return (idleTime);
	}

	public	void setIdleTime (SystemEnvironment env, Integer p_idleTime)
	throws SDMSException
	{
		if(p_idleTime != null && p_idleTime.equals(idleTime)) return;
		if(p_idleTime == null && idleTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.idleTime = p_idleTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getStatisticTs (SystemEnvironment env)
	throws SDMSException
	{
		return (statisticTs);
	}

	public	void setStatisticTs (SystemEnvironment env, Integer p_statisticTs)
	throws SDMSException
	{
		if(p_statisticTs != null && p_statisticTs.equals(statisticTs)) return;
		if(p_statisticTs == null && statisticTs == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.statisticTs = p_statisticTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getDependencyWaitTime (SystemEnvironment env)
	throws SDMSException
	{
		return (dependencyWaitTime);
	}

	public	void setDependencyWaitTime (SystemEnvironment env, Integer p_dependencyWaitTime)
	throws SDMSException
	{
		if(p_dependencyWaitTime != null && p_dependencyWaitTime.equals(dependencyWaitTime)) return;
		if(p_dependencyWaitTime == null && dependencyWaitTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.dependencyWaitTime = p_dependencyWaitTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getSuspendTime (SystemEnvironment env)
	throws SDMSException
	{
		return (suspendTime);
	}

	public	void setSuspendTime (SystemEnvironment env, Integer p_suspendTime)
	throws SDMSException
	{
		if(p_suspendTime != null && p_suspendTime.equals(suspendTime)) return;
		if(p_suspendTime == null && suspendTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.suspendTime = p_suspendTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getSyncTime (SystemEnvironment env)
	throws SDMSException
	{
		return (syncTime);
	}

	public	void setSyncTime (SystemEnvironment env, Integer p_syncTime)
	throws SDMSException
	{
		if(p_syncTime != null && p_syncTime.equals(syncTime)) return;
		if(p_syncTime == null && syncTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.syncTime = p_syncTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getResourceTime (SystemEnvironment env)
	throws SDMSException
	{
		return (resourceTime);
	}

	public	void setResourceTime (SystemEnvironment env, Integer p_resourceTime)
	throws SDMSException
	{
		if(p_resourceTime != null && p_resourceTime.equals(resourceTime)) return;
		if(p_resourceTime == null && resourceTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.resourceTime = p_resourceTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getJobserverTime (SystemEnvironment env)
	throws SDMSException
	{
		return (jobserverTime);
	}

	public	void setJobserverTime (SystemEnvironment env, Integer p_jobserverTime)
	throws SDMSException
	{
		if(p_jobserverTime != null && p_jobserverTime.equals(jobserverTime)) return;
		if(p_jobserverTime == null && jobserverTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.jobserverTime = p_jobserverTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getRestartableTime (SystemEnvironment env)
	throws SDMSException
	{
		return (restartableTime);
	}

	public	void setRestartableTime (SystemEnvironment env, Integer p_restartableTime)
	throws SDMSException
	{
		if(p_restartableTime != null && p_restartableTime.equals(restartableTime)) return;
		if(p_restartableTime == null && restartableTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.restartableTime = p_restartableTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getChildWaitTime (SystemEnvironment env)
	throws SDMSException
	{
		return (childWaitTime);
	}

	public	void setChildWaitTime (SystemEnvironment env, Integer p_childWaitTime)
	throws SDMSException
	{
		if(p_childWaitTime != null && p_childWaitTime.equals(childWaitTime)) return;
		if(p_childWaitTime == null && childWaitTime == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.childWaitTime = p_childWaitTime;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getOpSusresTs (SystemEnvironment env)
	throws SDMSException
	{
		return (opSusresTs);
	}

	public	void setOpSusresTs (SystemEnvironment env, Long p_opSusresTs)
	throws SDMSException
	{
		if(p_opSusresTs != null && p_opSusresTs.equals(opSusresTs)) return;
		if(p_opSusresTs == null && opSusresTs == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.opSusresTs = p_opSusresTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getNpeId (SystemEnvironment env)
	throws SDMSException
	{
		return (npeId);
	}

	public	void setNpeId (SystemEnvironment env, Long p_npeId)
	throws SDMSException
	{
		if(p_npeId != null && p_npeId.equals(npeId)) return;
		if(p_npeId == null && npeId == null) return;
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.npeId = p_npeId;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.euid();
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
		SDMSSubmittedEntityGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
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
		SDMSSubmittedEntityGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSSubmittedEntityGeneric set_MasterIdSeIdMergeMode (SystemEnvironment env, Long p_masterId, Long p_seId, Integer p_mergeMode)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.masterId = p_masterId;
			o.seId = p_seId;
			o.mergeMode = p_mergeMode;
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

	public SDMSSubmittedEntityGeneric set_MasterIdSeId (SystemEnvironment env, Long p_masterId, Long p_seId)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.masterId = p_masterId;
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

	public SDMSSubmittedEntityGeneric set_FireSmeIdTrId (SystemEnvironment env, Long p_fireSmeId, Long p_trId)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.fireSmeId = p_fireSmeId;
			o.trId = p_trId;
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

	public SDMSSubmittedEntityGeneric set_MasterIdParentIdSeIdChildTag (SystemEnvironment env, Long p_masterId, Long p_parentId, Long p_seId, String p_childTag)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.masterId = p_masterId;
			o.parentId = p_parentId;
			o.seId = p_seId;
			if (p_childTag != null && p_childTag.length() > 70) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "changeTs", "70")
				);
			}
			o.childTag = p_childTag;
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

	public SDMSSubmittedEntityGeneric set_ParentIdSeId (SystemEnvironment env, Long p_parentId, Long p_seId)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.parentId = p_parentId;
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

	public SDMSSubmittedEntityGeneric set_ParentIdTrId (SystemEnvironment env, Long p_parentId, Long p_trId)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.parentId = p_parentId;
			o.trId = p_trId;
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

	protected SDMSProxy toProxy()
	{
		return new SDMSSubmittedEntity(this);
	}

	protected SDMSSubmittedEntityGeneric(Long p_id,
	                                     Long p_accessKey,
	                                     Long p_masterId,
	                                     String p_submitTag,
	                                     Integer p_unresolvedHandling,
	                                     Long p_seId,
	                                     String p_childTag,
	                                     Long p_seVersion,
	                                     Long p_ownerId,
	                                     Long p_parentId,
	                                     Long p_scopeId,
	                                     Boolean p_isStatic,
	                                     Boolean p_isDisabled,
	                                     Integer p_oldState,
	                                     Integer p_mergeMode,
	                                     Integer p_state,
	                                     Long p_jobEsdId,
	                                     Integer p_jobEsdPref,
	                                     Boolean p_jobIsFinal,
	                                     Boolean p_jobIsRestartable,
	                                     Long p_finalEsdId,
	                                     Integer p_exitCode,
	                                     String p_commandline,
	                                     String p_rrCommandline,
	                                     Integer p_rerunSeq,
	                                     Boolean p_isReplaced,
	                                     Boolean p_isCancelled,
	                                     Long p_baseSmeId,
	                                     Long p_reasonSmeId,
	                                     Long p_fireSmeId,
	                                     Long p_fireSeId,
	                                     Long p_trId,
	                                     Long p_trSdIdOld,
	                                     Long p_trSdIdNew,
	                                     Integer p_trSeq,
	                                     String p_workdir,
	                                     String p_logfile,
	                                     String p_errlogfile,
	                                     String p_pid,
	                                     String p_extPid,
	                                     String p_errorMsg,
	                                     Long p_killId,
	                                     Integer p_killExitCode,
	                                     Integer p_isSuspended,
	                                     Boolean p_isSuspendedLocal,
	                                     Integer p_priority,
	                                     Integer p_rawPriority,
	                                     Integer p_nice,
	                                     Integer p_npNice,
	                                     Integer p_minPriority,
	                                     Integer p_agingAmount,
	                                     Integer p_parentSuspended,
	                                     Integer p_childSuspended,
	                                     Integer p_warnCount,
	                                     Long p_warnLink,
	                                     Long p_submitTs,
	                                     Long p_resumeTs,
	                                     Long p_syncTs,
	                                     Long p_resourceTs,
	                                     Long p_runnableTs,
	                                     Long p_startTs,
	                                     Long p_finishTs,
	                                     Long p_finalTs,
	                                     Integer p_cntSubmitted,
	                                     Integer p_cntDependencyWait,
	                                     Integer p_cntSynchronizeWait,
	                                     Integer p_cntResourceWait,
	                                     Integer p_cntRunnable,
	                                     Integer p_cntStarting,
	                                     Integer p_cntStarted,
	                                     Integer p_cntRunning,
	                                     Integer p_cntToKill,
	                                     Integer p_cntKilled,
	                                     Integer p_cntCancelled,
	                                     Integer p_cntFinished,
	                                     Integer p_cntFinal,
	                                     Integer p_cntBrokenActive,
	                                     Integer p_cntBrokenFinished,
	                                     Integer p_cntError,
	                                     Integer p_cntUnreachable,
	                                     Integer p_cntRestartable,
	                                     Integer p_cntWarn,
	                                     Integer p_cntPending,
	                                     Integer p_idleTs,
	                                     Integer p_idleTime,
	                                     Integer p_statisticTs,
	                                     Integer p_dependencyWaitTime,
	                                     Integer p_suspendTime,
	                                     Integer p_syncTime,
	                                     Integer p_resourceTime,
	                                     Integer p_jobserverTime,
	                                     Integer p_restartableTime,
	                                     Integer p_childWaitTime,
	                                     Long p_opSusresTs,
	                                     Long p_npeId,
	                                     Long p_creatorUId,
	                                     Long p_createTs,
	                                     Long p_changerUId,
	                                     Long p_changeTs,
	                                     long p_validFrom, long p_validTo)
	{
		id     = p_id;
		accessKey = p_accessKey;
		masterId = p_masterId;
		submitTag = p_submitTag;
		unresolvedHandling = p_unresolvedHandling;
		seId = p_seId;
		childTag = p_childTag;
		seVersion = p_seVersion;
		ownerId = p_ownerId;
		parentId = p_parentId;
		scopeId = p_scopeId;
		isStatic = p_isStatic;
		isDisabled = p_isDisabled;
		oldState = p_oldState;
		mergeMode = p_mergeMode;
		state = p_state;
		jobEsdId = p_jobEsdId;
		jobEsdPref = p_jobEsdPref;
		jobIsFinal = p_jobIsFinal;
		jobIsRestartable = p_jobIsRestartable;
		finalEsdId = p_finalEsdId;
		exitCode = p_exitCode;
		commandline = p_commandline;
		rrCommandline = p_rrCommandline;
		rerunSeq = p_rerunSeq;
		isReplaced = p_isReplaced;
		isCancelled = p_isCancelled;
		baseSmeId = p_baseSmeId;
		reasonSmeId = p_reasonSmeId;
		fireSmeId = p_fireSmeId;
		fireSeId = p_fireSeId;
		trId = p_trId;
		trSdIdOld = p_trSdIdOld;
		trSdIdNew = p_trSdIdNew;
		trSeq = p_trSeq;
		workdir = p_workdir;
		logfile = p_logfile;
		errlogfile = p_errlogfile;
		pid = p_pid;
		extPid = p_extPid;
		errorMsg = p_errorMsg;
		killId = p_killId;
		killExitCode = p_killExitCode;
		isSuspended = p_isSuspended;
		isSuspendedLocal = p_isSuspendedLocal;
		priority = p_priority;
		rawPriority = p_rawPriority;
		nice = p_nice;
		npNice = p_npNice;
		minPriority = p_minPriority;
		agingAmount = p_agingAmount;
		parentSuspended = p_parentSuspended;
		childSuspended = p_childSuspended;
		warnCount = p_warnCount;
		warnLink = p_warnLink;
		submitTs = p_submitTs;
		resumeTs = p_resumeTs;
		syncTs = p_syncTs;
		resourceTs = p_resourceTs;
		runnableTs = p_runnableTs;
		startTs = p_startTs;
		finishTs = p_finishTs;
		finalTs = p_finalTs;
		cntSubmitted = p_cntSubmitted;
		cntDependencyWait = p_cntDependencyWait;
		cntSynchronizeWait = p_cntSynchronizeWait;
		cntResourceWait = p_cntResourceWait;
		cntRunnable = p_cntRunnable;
		cntStarting = p_cntStarting;
		cntStarted = p_cntStarted;
		cntRunning = p_cntRunning;
		cntToKill = p_cntToKill;
		cntKilled = p_cntKilled;
		cntCancelled = p_cntCancelled;
		cntFinished = p_cntFinished;
		cntFinal = p_cntFinal;
		cntBrokenActive = p_cntBrokenActive;
		cntBrokenFinished = p_cntBrokenFinished;
		cntError = p_cntError;
		cntUnreachable = p_cntUnreachable;
		cntRestartable = p_cntRestartable;
		cntWarn = p_cntWarn;
		cntPending = p_cntPending;
		idleTs = p_idleTs;
		idleTime = p_idleTime;
		statisticTs = p_statisticTs;
		dependencyWaitTime = p_dependencyWaitTime;
		suspendTime = p_suspendTime;
		syncTime = p_syncTime;
		resourceTime = p_resourceTime;
		jobserverTime = p_jobserverTime;
		restartableTime = p_restartableTime;
		childWaitTime = p_childWaitTime;
		opSusresTs = p_opSusresTs;
		npeId = p_npeId;
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
				        "INSERT INTO SUBMITTED_ENTITY (" +
				        "ID" +
				        ", " + squote + "ACCESS_KEY" + equote +
				        ", " + squote + "MASTER_ID" + equote +
				        ", " + squote + "SUBMIT_TAG" + equote +
				        ", " + squote + "UNRESOLVED_HANDLING" + equote +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "CHILD_TAG" + equote +
				        ", " + squote + "SE_VERSION" + equote +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "PARENT_ID" + equote +
				        ", " + squote + "SCOPE_ID" + equote +
				        ", " + squote + "IS_STATIC" + equote +
				        ", " + squote + "IS_DISABLED" + equote +
				        ", " + squote + "OLD_STATE" + equote +
				        ", " + squote + "MERGE_MODE" + equote +
				        ", " + squote + "STATE" + equote +
				        ", " + squote + "JOB_ESD_ID" + equote +
				        ", " + squote + "JOB_ESD_PREF" + equote +
				        ", " + squote + "JOB_IS_FINAL" + equote +
				        ", " + squote + "JOB_IS_RESTARTABLE" + equote +
				        ", " + squote + "FINAL_ESD_ID" + equote +
				        ", " + squote + "EXIT_CODE" + equote +
				        ", " + squote + "COMMANDLINE" + equote +
				        ", " + squote + "RR_COMMANDLINE" + equote +
				        ", " + squote + "RERUN_SEQ" + equote +
				        ", " + squote + "IS_REPLACED" + equote +
				        ", " + squote + "IS_CANCELLED" + equote +
				        ", " + squote + "BASE_SME_ID" + equote +
				        ", " + squote + "REASON_SME_ID" + equote +
				        ", " + squote + "FIRE_SME_ID" + equote +
				        ", " + squote + "FIRE_SE_ID" + equote +
				        ", " + squote + "TR_ID" + equote +
				        ", " + squote + "TR_SD_ID_OLD" + equote +
				        ", " + squote + "TR_SD_ID_NEW" + equote +
				        ", " + squote + "TR_SEQ" + equote +
				        ", " + squote + "WORKDIR" + equote +
				        ", " + squote + "LOGFILE" + equote +
				        ", " + squote + "ERRLOGFILE" + equote +
				        ", " + squote + "PID" + equote +
				        ", " + squote + "EXTPID" + equote +
				        ", " + squote + "ERROR_MSG" + equote +
				        ", " + squote + "KILL_ID" + equote +
				        ", " + squote + "KILL_EXIT_CODE" + equote +
				        ", " + squote + "IS_SUSPENDED" + equote +
				        ", " + squote + "IS_SUSPENDED_LOCAL" + equote +
				        ", " + squote + "PRIORITY" + equote +
				        ", " + squote + "RAW_PRIORITY" + equote +
				        ", " + squote + "NICE" + equote +
				        ", " + squote + "NP_NICE" + equote +
				        ", " + squote + "MIN_PRIORITY" + equote +
				        ", " + squote + "AGING_AMOUNT" + equote +
				        ", " + squote + "PARENT_SUSPENDED" + equote +
				        ", " + squote + "CHILD_SUSPENDED" + equote +
				        ", " + squote + "WARN_COUNT" + equote +
				        ", " + squote + "WARN_LINK" + equote +
				        ", " + squote + "SUBMIT_TS" + equote +
				        ", " + squote + "RESUME_TS" + equote +
				        ", " + squote + "SYNC_TS" + equote +
				        ", " + squote + "RESOURCE_TS" + equote +
				        ", " + squote + "RUNNABLE_TS" + equote +
				        ", " + squote + "START_TS" + equote +
				        ", " + squote + "FINSH_TS" + equote +
				        ", " + squote + "FINAL_TS" + equote +
				        ", " + squote + "CNT_SUBMITTED" + equote +
				        ", " + squote + "CNT_DEPENDENCY_WAIT" + equote +
				        ", " + squote + "CNT_SYNCHRONIZE_WAIT" + equote +
				        ", " + squote + "CNT_RESOURCE_WAIT" + equote +
				        ", " + squote + "CNT_RUNNABLE" + equote +
				        ", " + squote + "CNT_STARTING" + equote +
				        ", " + squote + "CNT_STARTED" + equote +
				        ", " + squote + "CNT_RUNNING" + equote +
				        ", " + squote + "CNT_TO_KILL" + equote +
				        ", " + squote + "CNT_KILLED" + equote +
				        ", " + squote + "CNT_CANCELLED" + equote +
				        ", " + squote + "CNT_FINISHED" + equote +
				        ", " + squote + "CNT_FINAL" + equote +
				        ", " + squote + "CNT_BROKEN_ACTIVE" + equote +
				        ", " + squote + "CNT_BROKEN_FINISHED" + equote +
				        ", " + squote + "CNT_ERROR" + equote +
				        ", " + squote + "CNT_UNREACHABLE" + equote +
				        ", " + squote + "CNT_RESTARTABLE" + equote +
				        ", " + squote + "CNT_WARN" + equote +
				        ", " + squote + "CNT_PENDING" + equote +
				        ", " + squote + "IDLE_TS" + equote +
				        ", " + squote + "IDLE_TIME" + equote +
				        ", " + squote + "STATISTIC_TS" + equote +
				        ", " + squote + "DEPENDENCY_WAIT_TIME" + equote +
				        ", " + squote + "SUSPEND_TIME" + equote +
				        ", " + squote + "SYNC_TIME" + equote +
				        ", " + squote + "RESOURCE_TIME" + equote +
				        ", " + squote + "JOBSERVER_TIME" + equote +
				        ", " + squote + "RESTARTABLE_TIME" + equote +
				        ", " + squote + "CHILD_WAIT_TIME" + equote +
				        ", " + squote + "OP_SUSRES_TS" + equote +
				        ", " + squote + "NPE_ID" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "SubmittedEntity: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, accessKey.longValue());
			myInsert.setLong (3, masterId.longValue());
			if (submitTag == null)
				myInsert.setNull(4, Types.VARCHAR);
			else
				myInsert.setString(4, submitTag);
			if (unresolvedHandling == null)
				myInsert.setNull(5, Types.INTEGER);
			else
				myInsert.setInt(5, unresolvedHandling.intValue());
			myInsert.setLong (6, seId.longValue());
			if (childTag == null)
				myInsert.setNull(7, Types.VARCHAR);
			else
				myInsert.setString(7, childTag);
			myInsert.setLong (8, seVersion.longValue());
			myInsert.setLong (9, ownerId.longValue());
			if (parentId == null)
				myInsert.setNull(10, Types.INTEGER);
			else
				myInsert.setLong (10, parentId.longValue());
			if (scopeId == null)
				myInsert.setNull(11, Types.INTEGER);
			else
				myInsert.setLong (11, scopeId.longValue());
			myInsert.setInt (12, isStatic.booleanValue() ? 1 : 0);
			myInsert.setInt (13, isDisabled.booleanValue() ? 1 : 0);
			if (oldState == null)
				myInsert.setNull(14, Types.INTEGER);
			else
				myInsert.setInt(14, oldState.intValue());
			myInsert.setInt(15, mergeMode.intValue());
			myInsert.setInt(16, state.intValue());
			if (jobEsdId == null)
				myInsert.setNull(17, Types.INTEGER);
			else
				myInsert.setLong (17, jobEsdId.longValue());
			if (jobEsdPref == null)
				myInsert.setNull(18, Types.INTEGER);
			else
				myInsert.setInt(18, jobEsdPref.intValue());
			myInsert.setInt (19, jobIsFinal.booleanValue() ? 1 : 0);
			myInsert.setInt (20, jobIsRestartable.booleanValue() ? 1 : 0);
			if (finalEsdId == null)
				myInsert.setNull(21, Types.INTEGER);
			else
				myInsert.setLong (21, finalEsdId.longValue());
			if (exitCode == null)
				myInsert.setNull(22, Types.INTEGER);
			else
				myInsert.setInt(22, exitCode.intValue());
			if (commandline == null)
				myInsert.setNull(23, Types.VARCHAR);
			else
				myInsert.setString(23, commandline);
			if (rrCommandline == null)
				myInsert.setNull(24, Types.VARCHAR);
			else
				myInsert.setString(24, rrCommandline);
			myInsert.setInt(25, rerunSeq.intValue());
			myInsert.setInt (26, isReplaced.booleanValue() ? 1 : 0);
			if (isCancelled == null)
				myInsert.setNull(27, Types.INTEGER);
			else
				myInsert.setInt (27, isCancelled.booleanValue() ? 1 : 0);
			if (baseSmeId == null)
				myInsert.setNull(28, Types.INTEGER);
			else
				myInsert.setLong (28, baseSmeId.longValue());
			if (reasonSmeId == null)
				myInsert.setNull(29, Types.INTEGER);
			else
				myInsert.setLong (29, reasonSmeId.longValue());
			if (fireSmeId == null)
				myInsert.setNull(30, Types.INTEGER);
			else
				myInsert.setLong (30, fireSmeId.longValue());
			if (fireSeId == null)
				myInsert.setNull(31, Types.INTEGER);
			else
				myInsert.setLong (31, fireSeId.longValue());
			if (trId == null)
				myInsert.setNull(32, Types.INTEGER);
			else
				myInsert.setLong (32, trId.longValue());
			if (trSdIdOld == null)
				myInsert.setNull(33, Types.INTEGER);
			else
				myInsert.setLong (33, trSdIdOld.longValue());
			if (trSdIdNew == null)
				myInsert.setNull(34, Types.INTEGER);
			else
				myInsert.setLong (34, trSdIdNew.longValue());
			myInsert.setInt(35, trSeq.intValue());
			if (workdir == null)
				myInsert.setNull(36, Types.VARCHAR);
			else
				myInsert.setString(36, workdir);
			if (logfile == null)
				myInsert.setNull(37, Types.VARCHAR);
			else
				myInsert.setString(37, logfile);
			if (errlogfile == null)
				myInsert.setNull(38, Types.VARCHAR);
			else
				myInsert.setString(38, errlogfile);
			if (pid == null)
				myInsert.setNull(39, Types.VARCHAR);
			else
				myInsert.setString(39, pid);
			if (extPid == null)
				myInsert.setNull(40, Types.VARCHAR);
			else
				myInsert.setString(40, extPid);
			if (errorMsg == null)
				myInsert.setNull(41, Types.VARCHAR);
			else
				myInsert.setString(41, errorMsg);
			if (killId == null)
				myInsert.setNull(42, Types.INTEGER);
			else
				myInsert.setLong (42, killId.longValue());
			if (killExitCode == null)
				myInsert.setNull(43, Types.INTEGER);
			else
				myInsert.setInt(43, killExitCode.intValue());
			myInsert.setInt(44, isSuspended.intValue());
			if (isSuspendedLocal == null)
				myInsert.setNull(45, Types.INTEGER);
			else
				myInsert.setInt (45, isSuspendedLocal.booleanValue() ? 1 : 0);
			myInsert.setInt(46, priority.intValue());
			myInsert.setInt(47, rawPriority.intValue());
			myInsert.setInt(48, nice.intValue());
			myInsert.setInt(49, npNice.intValue());
			myInsert.setInt(50, minPriority.intValue());
			myInsert.setInt(51, agingAmount.intValue());
			myInsert.setInt(52, parentSuspended.intValue());
			myInsert.setInt(53, childSuspended.intValue());
			myInsert.setInt(54, warnCount.intValue());
			if (warnLink == null)
				myInsert.setNull(55, Types.INTEGER);
			else
				myInsert.setLong (55, warnLink.longValue());
			myInsert.setLong (56, submitTs.longValue());
			if (resumeTs == null)
				myInsert.setNull(57, Types.INTEGER);
			else
				myInsert.setLong (57, resumeTs.longValue());
			if (syncTs == null)
				myInsert.setNull(58, Types.INTEGER);
			else
				myInsert.setLong (58, syncTs.longValue());
			if (resourceTs == null)
				myInsert.setNull(59, Types.INTEGER);
			else
				myInsert.setLong (59, resourceTs.longValue());
			if (runnableTs == null)
				myInsert.setNull(60, Types.INTEGER);
			else
				myInsert.setLong (60, runnableTs.longValue());
			if (startTs == null)
				myInsert.setNull(61, Types.INTEGER);
			else
				myInsert.setLong (61, startTs.longValue());
			if (finishTs == null)
				myInsert.setNull(62, Types.INTEGER);
			else
				myInsert.setLong (62, finishTs.longValue());
			if (finalTs == null)
				myInsert.setNull(63, Types.INTEGER);
			else
				myInsert.setLong (63, finalTs.longValue());
			myInsert.setInt(64, cntSubmitted.intValue());
			myInsert.setInt(65, cntDependencyWait.intValue());
			myInsert.setInt(66, cntSynchronizeWait.intValue());
			myInsert.setInt(67, cntResourceWait.intValue());
			myInsert.setInt(68, cntRunnable.intValue());
			myInsert.setInt(69, cntStarting.intValue());
			myInsert.setInt(70, cntStarted.intValue());
			myInsert.setInt(71, cntRunning.intValue());
			myInsert.setInt(72, cntToKill.intValue());
			myInsert.setInt(73, cntKilled.intValue());
			myInsert.setInt(74, cntCancelled.intValue());
			myInsert.setInt(75, cntFinished.intValue());
			myInsert.setInt(76, cntFinal.intValue());
			myInsert.setInt(77, cntBrokenActive.intValue());
			myInsert.setInt(78, cntBrokenFinished.intValue());
			myInsert.setInt(79, cntError.intValue());
			myInsert.setInt(80, cntUnreachable.intValue());
			myInsert.setInt(81, cntRestartable.intValue());
			myInsert.setInt(82, cntWarn.intValue());
			myInsert.setInt(83, cntPending.intValue());
			if (idleTs == null)
				myInsert.setNull(84, Types.INTEGER);
			else
				myInsert.setInt(84, idleTs.intValue());
			if (idleTime == null)
				myInsert.setNull(85, Types.INTEGER);
			else
				myInsert.setInt(85, idleTime.intValue());
			if (statisticTs == null)
				myInsert.setNull(86, Types.INTEGER);
			else
				myInsert.setInt(86, statisticTs.intValue());
			if (dependencyWaitTime == null)
				myInsert.setNull(87, Types.INTEGER);
			else
				myInsert.setInt(87, dependencyWaitTime.intValue());
			if (suspendTime == null)
				myInsert.setNull(88, Types.INTEGER);
			else
				myInsert.setInt(88, suspendTime.intValue());
			if (syncTime == null)
				myInsert.setNull(89, Types.INTEGER);
			else
				myInsert.setInt(89, syncTime.intValue());
			if (resourceTime == null)
				myInsert.setNull(90, Types.INTEGER);
			else
				myInsert.setInt(90, resourceTime.intValue());
			if (jobserverTime == null)
				myInsert.setNull(91, Types.INTEGER);
			else
				myInsert.setInt(91, jobserverTime.intValue());
			if (restartableTime == null)
				myInsert.setNull(92, Types.INTEGER);
			else
				myInsert.setInt(92, restartableTime.intValue());
			if (childWaitTime == null)
				myInsert.setNull(93, Types.INTEGER);
			else
				myInsert.setInt(93, childWaitTime.intValue());
			if (opSusresTs == null)
				myInsert.setNull(94, Types.INTEGER);
			else
				myInsert.setLong (94, opSusresTs.longValue());
			if (npeId == null)
				myInsert.setNull(95, Types.INTEGER);
			else
				myInsert.setLong (95, npeId.longValue());
			myInsert.setLong (96, creatorUId.longValue());
			myInsert.setLong (97, createTs.longValue());
			myInsert.setLong (98, changerUId.longValue());
			myInsert.setLong (99, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "SubmittedEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				stmt =
				        "DELETE FROM SUBMITTED_ENTITY WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "SubmittedEntity: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "SubmittedEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE SUBMITTED_ENTITY SET " +
				        "" + squote + "ACCESS_KEY" + equote + " = ? " +
				        ", " + squote + "MASTER_ID" + equote + " = ? " +
				        ", " + squote + "SUBMIT_TAG" + equote + " = ? " +
				        ", " + squote + "UNRESOLVED_HANDLING" + equote + " = ? " +
				        ", " + squote + "SE_ID" + equote + " = ? " +
				        ", " + squote + "CHILD_TAG" + equote + " = ? " +
				        ", " + squote + "SE_VERSION" + equote + " = ? " +
				        ", " + squote + "OWNER_ID" + equote + " = ? " +
				        ", " + squote + "PARENT_ID" + equote + " = ? " +
				        ", " + squote + "SCOPE_ID" + equote + " = ? " +
				        ", " + squote + "IS_STATIC" + equote + " = ? " +
				        ", " + squote + "IS_DISABLED" + equote + " = ? " +
				        ", " + squote + "OLD_STATE" + equote + " = ? " +
				        ", " + squote + "MERGE_MODE" + equote + " = ? " +
				        ", " + squote + "STATE" + equote + " = ? " +
				        ", " + squote + "JOB_ESD_ID" + equote + " = ? " +
				        ", " + squote + "JOB_ESD_PREF" + equote + " = ? " +
				        ", " + squote + "JOB_IS_FINAL" + equote + " = ? " +
				        ", " + squote + "JOB_IS_RESTARTABLE" + equote + " = ? " +
				        ", " + squote + "FINAL_ESD_ID" + equote + " = ? " +
				        ", " + squote + "EXIT_CODE" + equote + " = ? " +
				        ", " + squote + "COMMANDLINE" + equote + " = ? " +
				        ", " + squote + "RR_COMMANDLINE" + equote + " = ? " +
				        ", " + squote + "RERUN_SEQ" + equote + " = ? " +
				        ", " + squote + "IS_REPLACED" + equote + " = ? " +
				        ", " + squote + "IS_CANCELLED" + equote + " = ? " +
				        ", " + squote + "BASE_SME_ID" + equote + " = ? " +
				        ", " + squote + "REASON_SME_ID" + equote + " = ? " +
				        ", " + squote + "FIRE_SME_ID" + equote + " = ? " +
				        ", " + squote + "FIRE_SE_ID" + equote + " = ? " +
				        ", " + squote + "TR_ID" + equote + " = ? " +
				        ", " + squote + "TR_SD_ID_OLD" + equote + " = ? " +
				        ", " + squote + "TR_SD_ID_NEW" + equote + " = ? " +
				        ", " + squote + "TR_SEQ" + equote + " = ? " +
				        ", " + squote + "WORKDIR" + equote + " = ? " +
				        ", " + squote + "LOGFILE" + equote + " = ? " +
				        ", " + squote + "ERRLOGFILE" + equote + " = ? " +
				        ", " + squote + "PID" + equote + " = ? " +
				        ", " + squote + "EXTPID" + equote + " = ? " +
				        ", " + squote + "ERROR_MSG" + equote + " = ? " +
				        ", " + squote + "KILL_ID" + equote + " = ? " +
				        ", " + squote + "KILL_EXIT_CODE" + equote + " = ? " +
				        ", " + squote + "IS_SUSPENDED" + equote + " = ? " +
				        ", " + squote + "IS_SUSPENDED_LOCAL" + equote + " = ? " +
				        ", " + squote + "PRIORITY" + equote + " = ? " +
				        ", " + squote + "RAW_PRIORITY" + equote + " = ? " +
				        ", " + squote + "NICE" + equote + " = ? " +
				        ", " + squote + "NP_NICE" + equote + " = ? " +
				        ", " + squote + "MIN_PRIORITY" + equote + " = ? " +
				        ", " + squote + "AGING_AMOUNT" + equote + " = ? " +
				        ", " + squote + "PARENT_SUSPENDED" + equote + " = ? " +
				        ", " + squote + "CHILD_SUSPENDED" + equote + " = ? " +
				        ", " + squote + "WARN_COUNT" + equote + " = ? " +
				        ", " + squote + "WARN_LINK" + equote + " = ? " +
				        ", " + squote + "SUBMIT_TS" + equote + " = ? " +
				        ", " + squote + "RESUME_TS" + equote + " = ? " +
				        ", " + squote + "SYNC_TS" + equote + " = ? " +
				        ", " + squote + "RESOURCE_TS" + equote + " = ? " +
				        ", " + squote + "RUNNABLE_TS" + equote + " = ? " +
				        ", " + squote + "START_TS" + equote + " = ? " +
				        ", " + squote + "FINSH_TS" + equote + " = ? " +
				        ", " + squote + "FINAL_TS" + equote + " = ? " +
				        ", " + squote + "CNT_SUBMITTED" + equote + " = ? " +
				        ", " + squote + "CNT_DEPENDENCY_WAIT" + equote + " = ? " +
				        ", " + squote + "CNT_SYNCHRONIZE_WAIT" + equote + " = ? " +
				        ", " + squote + "CNT_RESOURCE_WAIT" + equote + " = ? " +
				        ", " + squote + "CNT_RUNNABLE" + equote + " = ? " +
				        ", " + squote + "CNT_STARTING" + equote + " = ? " +
				        ", " + squote + "CNT_STARTED" + equote + " = ? " +
				        ", " + squote + "CNT_RUNNING" + equote + " = ? " +
				        ", " + squote + "CNT_TO_KILL" + equote + " = ? " +
				        ", " + squote + "CNT_KILLED" + equote + " = ? " +
				        ", " + squote + "CNT_CANCELLED" + equote + " = ? " +
				        ", " + squote + "CNT_FINISHED" + equote + " = ? " +
				        ", " + squote + "CNT_FINAL" + equote + " = ? " +
				        ", " + squote + "CNT_BROKEN_ACTIVE" + equote + " = ? " +
				        ", " + squote + "CNT_BROKEN_FINISHED" + equote + " = ? " +
				        ", " + squote + "CNT_ERROR" + equote + " = ? " +
				        ", " + squote + "CNT_UNREACHABLE" + equote + " = ? " +
				        ", " + squote + "CNT_RESTARTABLE" + equote + " = ? " +
				        ", " + squote + "CNT_WARN" + equote + " = ? " +
				        ", " + squote + "CNT_PENDING" + equote + " = ? " +
				        ", " + squote + "IDLE_TS" + equote + " = ? " +
				        ", " + squote + "IDLE_TIME" + equote + " = ? " +
				        ", " + squote + "STATISTIC_TS" + equote + " = ? " +
				        ", " + squote + "DEPENDENCY_WAIT_TIME" + equote + " = ? " +
				        ", " + squote + "SUSPEND_TIME" + equote + " = ? " +
				        ", " + squote + "SYNC_TIME" + equote + " = ? " +
				        ", " + squote + "RESOURCE_TIME" + equote + " = ? " +
				        ", " + squote + "JOBSERVER_TIME" + equote + " = ? " +
				        ", " + squote + "RESTARTABLE_TIME" + equote + " = ? " +
				        ", " + squote + "CHILD_WAIT_TIME" + equote + " = ? " +
				        ", " + squote + "OP_SUSRES_TS" + equote + " = ? " +
				        ", " + squote + "NPE_ID" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "SubmittedEntity: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, accessKey.longValue());
			myUpdate.setLong (2, masterId.longValue());
			if (submitTag == null)
				myUpdate.setNull(3, Types.VARCHAR);
			else
				myUpdate.setString(3, submitTag);
			if (unresolvedHandling == null)
				myUpdate.setNull(4, Types.INTEGER);
			else
				myUpdate.setInt(4, unresolvedHandling.intValue());
			myUpdate.setLong (5, seId.longValue());
			if (childTag == null)
				myUpdate.setNull(6, Types.VARCHAR);
			else
				myUpdate.setString(6, childTag);
			myUpdate.setLong (7, seVersion.longValue());
			myUpdate.setLong (8, ownerId.longValue());
			if (parentId == null)
				myUpdate.setNull(9, Types.INTEGER);
			else
				myUpdate.setLong (9, parentId.longValue());
			if (scopeId == null)
				myUpdate.setNull(10, Types.INTEGER);
			else
				myUpdate.setLong (10, scopeId.longValue());
			myUpdate.setInt (11, isStatic.booleanValue() ? 1 : 0);
			myUpdate.setInt (12, isDisabled.booleanValue() ? 1 : 0);
			if (oldState == null)
				myUpdate.setNull(13, Types.INTEGER);
			else
				myUpdate.setInt(13, oldState.intValue());
			myUpdate.setInt(14, mergeMode.intValue());
			myUpdate.setInt(15, state.intValue());
			if (jobEsdId == null)
				myUpdate.setNull(16, Types.INTEGER);
			else
				myUpdate.setLong (16, jobEsdId.longValue());
			if (jobEsdPref == null)
				myUpdate.setNull(17, Types.INTEGER);
			else
				myUpdate.setInt(17, jobEsdPref.intValue());
			myUpdate.setInt (18, jobIsFinal.booleanValue() ? 1 : 0);
			myUpdate.setInt (19, jobIsRestartable.booleanValue() ? 1 : 0);
			if (finalEsdId == null)
				myUpdate.setNull(20, Types.INTEGER);
			else
				myUpdate.setLong (20, finalEsdId.longValue());
			if (exitCode == null)
				myUpdate.setNull(21, Types.INTEGER);
			else
				myUpdate.setInt(21, exitCode.intValue());
			if (commandline == null)
				myUpdate.setNull(22, Types.VARCHAR);
			else
				myUpdate.setString(22, commandline);
			if (rrCommandline == null)
				myUpdate.setNull(23, Types.VARCHAR);
			else
				myUpdate.setString(23, rrCommandline);
			myUpdate.setInt(24, rerunSeq.intValue());
			myUpdate.setInt (25, isReplaced.booleanValue() ? 1 : 0);
			if (isCancelled == null)
				myUpdate.setNull(26, Types.INTEGER);
			else
				myUpdate.setInt (26, isCancelled.booleanValue() ? 1 : 0);
			if (baseSmeId == null)
				myUpdate.setNull(27, Types.INTEGER);
			else
				myUpdate.setLong (27, baseSmeId.longValue());
			if (reasonSmeId == null)
				myUpdate.setNull(28, Types.INTEGER);
			else
				myUpdate.setLong (28, reasonSmeId.longValue());
			if (fireSmeId == null)
				myUpdate.setNull(29, Types.INTEGER);
			else
				myUpdate.setLong (29, fireSmeId.longValue());
			if (fireSeId == null)
				myUpdate.setNull(30, Types.INTEGER);
			else
				myUpdate.setLong (30, fireSeId.longValue());
			if (trId == null)
				myUpdate.setNull(31, Types.INTEGER);
			else
				myUpdate.setLong (31, trId.longValue());
			if (trSdIdOld == null)
				myUpdate.setNull(32, Types.INTEGER);
			else
				myUpdate.setLong (32, trSdIdOld.longValue());
			if (trSdIdNew == null)
				myUpdate.setNull(33, Types.INTEGER);
			else
				myUpdate.setLong (33, trSdIdNew.longValue());
			myUpdate.setInt(34, trSeq.intValue());
			if (workdir == null)
				myUpdate.setNull(35, Types.VARCHAR);
			else
				myUpdate.setString(35, workdir);
			if (logfile == null)
				myUpdate.setNull(36, Types.VARCHAR);
			else
				myUpdate.setString(36, logfile);
			if (errlogfile == null)
				myUpdate.setNull(37, Types.VARCHAR);
			else
				myUpdate.setString(37, errlogfile);
			if (pid == null)
				myUpdate.setNull(38, Types.VARCHAR);
			else
				myUpdate.setString(38, pid);
			if (extPid == null)
				myUpdate.setNull(39, Types.VARCHAR);
			else
				myUpdate.setString(39, extPid);
			if (errorMsg == null)
				myUpdate.setNull(40, Types.VARCHAR);
			else
				myUpdate.setString(40, errorMsg);
			if (killId == null)
				myUpdate.setNull(41, Types.INTEGER);
			else
				myUpdate.setLong (41, killId.longValue());
			if (killExitCode == null)
				myUpdate.setNull(42, Types.INTEGER);
			else
				myUpdate.setInt(42, killExitCode.intValue());
			myUpdate.setInt(43, isSuspended.intValue());
			if (isSuspendedLocal == null)
				myUpdate.setNull(44, Types.INTEGER);
			else
				myUpdate.setInt (44, isSuspendedLocal.booleanValue() ? 1 : 0);
			myUpdate.setInt(45, priority.intValue());
			myUpdate.setInt(46, rawPriority.intValue());
			myUpdate.setInt(47, nice.intValue());
			myUpdate.setInt(48, npNice.intValue());
			myUpdate.setInt(49, minPriority.intValue());
			myUpdate.setInt(50, agingAmount.intValue());
			myUpdate.setInt(51, parentSuspended.intValue());
			myUpdate.setInt(52, childSuspended.intValue());
			myUpdate.setInt(53, warnCount.intValue());
			if (warnLink == null)
				myUpdate.setNull(54, Types.INTEGER);
			else
				myUpdate.setLong (54, warnLink.longValue());
			myUpdate.setLong (55, submitTs.longValue());
			if (resumeTs == null)
				myUpdate.setNull(56, Types.INTEGER);
			else
				myUpdate.setLong (56, resumeTs.longValue());
			if (syncTs == null)
				myUpdate.setNull(57, Types.INTEGER);
			else
				myUpdate.setLong (57, syncTs.longValue());
			if (resourceTs == null)
				myUpdate.setNull(58, Types.INTEGER);
			else
				myUpdate.setLong (58, resourceTs.longValue());
			if (runnableTs == null)
				myUpdate.setNull(59, Types.INTEGER);
			else
				myUpdate.setLong (59, runnableTs.longValue());
			if (startTs == null)
				myUpdate.setNull(60, Types.INTEGER);
			else
				myUpdate.setLong (60, startTs.longValue());
			if (finishTs == null)
				myUpdate.setNull(61, Types.INTEGER);
			else
				myUpdate.setLong (61, finishTs.longValue());
			if (finalTs == null)
				myUpdate.setNull(62, Types.INTEGER);
			else
				myUpdate.setLong (62, finalTs.longValue());
			myUpdate.setInt(63, cntSubmitted.intValue());
			myUpdate.setInt(64, cntDependencyWait.intValue());
			myUpdate.setInt(65, cntSynchronizeWait.intValue());
			myUpdate.setInt(66, cntResourceWait.intValue());
			myUpdate.setInt(67, cntRunnable.intValue());
			myUpdate.setInt(68, cntStarting.intValue());
			myUpdate.setInt(69, cntStarted.intValue());
			myUpdate.setInt(70, cntRunning.intValue());
			myUpdate.setInt(71, cntToKill.intValue());
			myUpdate.setInt(72, cntKilled.intValue());
			myUpdate.setInt(73, cntCancelled.intValue());
			myUpdate.setInt(74, cntFinished.intValue());
			myUpdate.setInt(75, cntFinal.intValue());
			myUpdate.setInt(76, cntBrokenActive.intValue());
			myUpdate.setInt(77, cntBrokenFinished.intValue());
			myUpdate.setInt(78, cntError.intValue());
			myUpdate.setInt(79, cntUnreachable.intValue());
			myUpdate.setInt(80, cntRestartable.intValue());
			myUpdate.setInt(81, cntWarn.intValue());
			myUpdate.setInt(82, cntPending.intValue());
			if (idleTs == null)
				myUpdate.setNull(83, Types.INTEGER);
			else
				myUpdate.setInt(83, idleTs.intValue());
			if (idleTime == null)
				myUpdate.setNull(84, Types.INTEGER);
			else
				myUpdate.setInt(84, idleTime.intValue());
			if (statisticTs == null)
				myUpdate.setNull(85, Types.INTEGER);
			else
				myUpdate.setInt(85, statisticTs.intValue());
			if (dependencyWaitTime == null)
				myUpdate.setNull(86, Types.INTEGER);
			else
				myUpdate.setInt(86, dependencyWaitTime.intValue());
			if (suspendTime == null)
				myUpdate.setNull(87, Types.INTEGER);
			else
				myUpdate.setInt(87, suspendTime.intValue());
			if (syncTime == null)
				myUpdate.setNull(88, Types.INTEGER);
			else
				myUpdate.setInt(88, syncTime.intValue());
			if (resourceTime == null)
				myUpdate.setNull(89, Types.INTEGER);
			else
				myUpdate.setInt(89, resourceTime.intValue());
			if (jobserverTime == null)
				myUpdate.setNull(90, Types.INTEGER);
			else
				myUpdate.setInt(90, jobserverTime.intValue());
			if (restartableTime == null)
				myUpdate.setNull(91, Types.INTEGER);
			else
				myUpdate.setInt(91, restartableTime.intValue());
			if (childWaitTime == null)
				myUpdate.setNull(92, Types.INTEGER);
			else
				myUpdate.setInt(92, childWaitTime.intValue());
			if (opSusresTs == null)
				myUpdate.setNull(93, Types.INTEGER);
			else
				myUpdate.setLong (93, opSusresTs.longValue());
			if (npeId == null)
				myUpdate.setNull(94, Types.INTEGER);
			else
				myUpdate.setLong (94, npeId.longValue());
			myUpdate.setLong (95, creatorUId.longValue());
			myUpdate.setLong (96, createTs.longValue());
			myUpdate.setLong (97, changerUId.longValue());
			myUpdate.setLong (98, changeTs.longValue());
			myUpdate.setLong(99, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "SubmittedEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkUnresolvedHandling(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
			case SDMSSubmittedEntity.UH_IGNORE:
			case SDMSSubmittedEntity.UH_SUSPEND:
			case SDMSSubmittedEntity.UH_ERROR:
				return true;
		}
		return false;
	}
	static public boolean checkMergeMode(Integer p)
	{
		switch (p.intValue()) {
			case SDMSSubmittedEntity.MERGE_LOCAL:
			case SDMSSubmittedEntity.MERGE_GLOBAL:
			case SDMSSubmittedEntity.NOMERGE:
			case SDMSSubmittedEntity.FAILURE:
				return true;
		}
		return false;
	}
	static public boolean checkState(Integer p)
	{
		switch (p.intValue()) {
			case SDMSSubmittedEntity.SUBMITTED:
			case SDMSSubmittedEntity.DEPENDENCY_WAIT:
			case SDMSSubmittedEntity.SYNCHRONIZE_WAIT:
			case SDMSSubmittedEntity.RESOURCE_WAIT:
			case SDMSSubmittedEntity.RUNNABLE:
			case SDMSSubmittedEntity.STARTING:
			case SDMSSubmittedEntity.STARTED:
			case SDMSSubmittedEntity.RUNNING:
			case SDMSSubmittedEntity.TO_KILL:
			case SDMSSubmittedEntity.KILLED:
			case SDMSSubmittedEntity.CANCELLED:
			case SDMSSubmittedEntity.FINISHED:
			case SDMSSubmittedEntity.FINAL:
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
			case SDMSSubmittedEntity.BROKEN_FINISHED:
			case SDMSSubmittedEntity.ERROR:
			case SDMSSubmittedEntity.UNREACHABLE:
				return true;
		}
		return false;
	}
	static public boolean checkIsSuspended(Integer p)
	{
		switch (p.intValue()) {
			case SDMSSubmittedEntity.ADMINSUSPEND:
			case SDMSSubmittedEntity.SUSPEND:
			case SDMSSubmittedEntity.NOSUSPEND:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : SubmittedEntity", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "accessKey : " + accessKey, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "masterId : " + masterId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "submitTag : " + submitTag, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "unresolvedHandling : " + unresolvedHandling, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "childTag : " + childTag, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seVersion : " + seVersion, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "parentId : " + parentId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "scopeId : " + scopeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isStatic : " + isStatic, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isDisabled : " + isDisabled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "oldState : " + oldState, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "mergeMode : " + mergeMode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "state : " + state, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "jobEsdId : " + jobEsdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "jobEsdPref : " + jobEsdPref, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "jobIsFinal : " + jobIsFinal, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "jobIsRestartable : " + jobIsRestartable, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "finalEsdId : " + finalEsdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "exitCode : " + exitCode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "commandline : " + commandline, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rrCommandline : " + rrCommandline, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rerunSeq : " + rerunSeq, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isReplaced : " + isReplaced, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isCancelled : " + isCancelled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "baseSmeId : " + baseSmeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "reasonSmeId : " + reasonSmeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fireSmeId : " + fireSmeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fireSeId : " + fireSeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "trId : " + trId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "trSdIdOld : " + trSdIdOld, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "trSdIdNew : " + trSdIdNew, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "trSeq : " + trSeq, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "workdir : " + workdir, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "logfile : " + logfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errlogfile : " + errlogfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "pid : " + pid, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "extPid : " + extPid, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errorMsg : " + errorMsg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "killId : " + killId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "killExitCode : " + killExitCode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSuspended : " + isSuspended, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSuspendedLocal : " + isSuspendedLocal, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "priority : " + priority, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rawPriority : " + rawPriority, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nice : " + nice, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "npNice : " + npNice, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "minPriority : " + minPriority, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "agingAmount : " + agingAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "parentSuspended : " + parentSuspended, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "childSuspended : " + childSuspended, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "warnCount : " + warnCount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "warnLink : " + warnLink, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "submitTs : " + submitTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeTs : " + resumeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "syncTs : " + syncTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resourceTs : " + resourceTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "runnableTs : " + runnableTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "startTs : " + startTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "finishTs : " + finishTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "finalTs : " + finalTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntSubmitted : " + cntSubmitted, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntDependencyWait : " + cntDependencyWait, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntSynchronizeWait : " + cntSynchronizeWait, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntResourceWait : " + cntResourceWait, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntRunnable : " + cntRunnable, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntStarting : " + cntStarting, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntStarted : " + cntStarted, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntRunning : " + cntRunning, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntToKill : " + cntToKill, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntKilled : " + cntKilled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntCancelled : " + cntCancelled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntFinished : " + cntFinished, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntFinal : " + cntFinal, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntBrokenActive : " + cntBrokenActive, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntBrokenFinished : " + cntBrokenFinished, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntError : " + cntError, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntUnreachable : " + cntUnreachable, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntRestartable : " + cntRestartable, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntWarn : " + cntWarn, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "cntPending : " + cntPending, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "idleTs : " + idleTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "idleTime : " + idleTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "statisticTs : " + statisticTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "dependencyWaitTime : " + dependencyWaitTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "suspendTime : " + suspendTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "syncTime : " + syncTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resourceTime : " + resourceTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "jobserverTime : " + jobserverTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "restartableTime : " + restartableTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "childWaitTime : " + childWaitTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "opSusresTs : " + opSusresTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "npeId : " + npeId, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "accessKey          : " + accessKey + "\n" +
		        indentString + "masterId           : " + masterId + "\n" +
		        indentString + "submitTag          : " + submitTag + "\n" +
		        indentString + "unresolvedHandling : " + unresolvedHandling + "\n" +
		        indentString + "seId               : " + seId + "\n" +
		        indentString + "childTag           : " + childTag + "\n" +
		        indentString + "seVersion          : " + seVersion + "\n" +
		        indentString + "ownerId            : " + ownerId + "\n" +
		        indentString + "parentId           : " + parentId + "\n" +
		        indentString + "scopeId            : " + scopeId + "\n" +
		        indentString + "isStatic           : " + isStatic + "\n" +
		        indentString + "isDisabled         : " + isDisabled + "\n" +
		        indentString + "oldState           : " + oldState + "\n" +
		        indentString + "mergeMode          : " + mergeMode + "\n" +
		        indentString + "state              : " + state + "\n" +
		        indentString + "jobEsdId           : " + jobEsdId + "\n" +
		        indentString + "jobEsdPref         : " + jobEsdPref + "\n" +
		        indentString + "jobIsFinal         : " + jobIsFinal + "\n" +
		        indentString + "jobIsRestartable   : " + jobIsRestartable + "\n" +
		        indentString + "finalEsdId         : " + finalEsdId + "\n" +
		        indentString + "exitCode           : " + exitCode + "\n" +
		        indentString + "commandline        : " + commandline + "\n" +
		        indentString + "rrCommandline      : " + rrCommandline + "\n" +
		        indentString + "rerunSeq           : " + rerunSeq + "\n" +
		        indentString + "isReplaced         : " + isReplaced + "\n" +
		        indentString + "isCancelled        : " + isCancelled + "\n" +
		        indentString + "baseSmeId          : " + baseSmeId + "\n" +
		        indentString + "reasonSmeId        : " + reasonSmeId + "\n" +
		        indentString + "fireSmeId          : " + fireSmeId + "\n" +
		        indentString + "fireSeId           : " + fireSeId + "\n" +
		        indentString + "trId               : " + trId + "\n" +
		        indentString + "trSdIdOld          : " + trSdIdOld + "\n" +
		        indentString + "trSdIdNew          : " + trSdIdNew + "\n" +
		        indentString + "trSeq              : " + trSeq + "\n" +
		        indentString + "workdir            : " + workdir + "\n" +
		        indentString + "logfile            : " + logfile + "\n" +
		        indentString + "errlogfile         : " + errlogfile + "\n" +
		        indentString + "pid                : " + pid + "\n" +
		        indentString + "extPid             : " + extPid + "\n" +
		        indentString + "errorMsg           : " + errorMsg + "\n" +
		        indentString + "killId             : " + killId + "\n" +
		        indentString + "killExitCode       : " + killExitCode + "\n" +
		        indentString + "isSuspended        : " + isSuspended + "\n" +
		        indentString + "isSuspendedLocal   : " + isSuspendedLocal + "\n" +
		        indentString + "priority           : " + priority + "\n" +
		        indentString + "rawPriority        : " + rawPriority + "\n" +
		        indentString + "nice               : " + nice + "\n" +
		        indentString + "npNice             : " + npNice + "\n" +
		        indentString + "minPriority        : " + minPriority + "\n" +
		        indentString + "agingAmount        : " + agingAmount + "\n" +
		        indentString + "parentSuspended    : " + parentSuspended + "\n" +
		        indentString + "childSuspended     : " + childSuspended + "\n" +
		        indentString + "warnCount          : " + warnCount + "\n" +
		        indentString + "warnLink           : " + warnLink + "\n" +
		        indentString + "submitTs           : " + submitTs + "\n" +
		        indentString + "resumeTs           : " + resumeTs + "\n" +
		        indentString + "syncTs             : " + syncTs + "\n" +
		        indentString + "resourceTs         : " + resourceTs + "\n" +
		        indentString + "runnableTs         : " + runnableTs + "\n" +
		        indentString + "startTs            : " + startTs + "\n" +
		        indentString + "finishTs           : " + finishTs + "\n" +
		        indentString + "finalTs            : " + finalTs + "\n" +
		        indentString + "cntSubmitted       : " + cntSubmitted + "\n" +
		        indentString + "cntDependencyWait  : " + cntDependencyWait + "\n" +
		        indentString + "cntSynchronizeWait : " + cntSynchronizeWait + "\n" +
		        indentString + "cntResourceWait    : " + cntResourceWait + "\n" +
		        indentString + "cntRunnable        : " + cntRunnable + "\n" +
		        indentString + "cntStarting        : " + cntStarting + "\n" +
		        indentString + "cntStarted         : " + cntStarted + "\n" +
		        indentString + "cntRunning         : " + cntRunning + "\n" +
		        indentString + "cntToKill          : " + cntToKill + "\n" +
		        indentString + "cntKilled          : " + cntKilled + "\n" +
		        indentString + "cntCancelled       : " + cntCancelled + "\n" +
		        indentString + "cntFinished        : " + cntFinished + "\n" +
		        indentString + "cntFinal           : " + cntFinal + "\n" +
		        indentString + "cntBrokenActive    : " + cntBrokenActive + "\n" +
		        indentString + "cntBrokenFinished  : " + cntBrokenFinished + "\n" +
		        indentString + "cntError           : " + cntError + "\n" +
		        indentString + "cntUnreachable     : " + cntUnreachable + "\n" +
		        indentString + "cntRestartable     : " + cntRestartable + "\n" +
		        indentString + "cntWarn            : " + cntWarn + "\n" +
		        indentString + "cntPending         : " + cntPending + "\n" +
		        indentString + "idleTs             : " + idleTs + "\n" +
		        indentString + "idleTime           : " + idleTime + "\n" +
		        indentString + "statisticTs        : " + statisticTs + "\n" +
		        indentString + "dependencyWaitTime : " + dependencyWaitTime + "\n" +
		        indentString + "suspendTime        : " + suspendTime + "\n" +
		        indentString + "syncTime           : " + syncTime + "\n" +
		        indentString + "resourceTime       : " + resourceTime + "\n" +
		        indentString + "jobserverTime      : " + jobserverTime + "\n" +
		        indentString + "restartableTime    : " + restartableTime + "\n" +
		        indentString + "childWaitTime      : " + childWaitTime + "\n" +
		        indentString + "opSusresTs         : " + opSusresTs + "\n" +
		        indentString + "npeId              : " + npeId + "\n" +
		        indentString + "creatorUId         : " + creatorUId + "\n" +
		        indentString + "createTs           : " + createTs + "\n" +
		        indentString + "changerUId         : " + changerUId + "\n" +
		        indentString + "changeTs           : " + changeTs + "\n" +
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
