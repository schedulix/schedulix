/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

	public final static String __version = "SDMSSubmittedEntityGeneric $Revision: 2.21 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

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
	public static final boolean SUSPEND = true;
	public static final boolean NOSUSPEND = false;
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
	public final static int nr_mergeMode = 13;
	public final static int nr_state = 14;
	public final static int nr_jobEsdId = 15;
	public final static int nr_jobEsdPref = 16;
	public final static int nr_jobIsFinal = 17;
	public final static int nr_jobIsRestartable = 18;
	public final static int nr_finalEsdId = 19;
	public final static int nr_exitCode = 20;
	public final static int nr_commandline = 21;
	public final static int nr_rrCommandline = 22;
	public final static int nr_rerunSeq = 23;
	public final static int nr_isReplaced = 24;
	public final static int nr_isCancelled = 25;
	public final static int nr_baseSmeId = 26;
	public final static int nr_reasonSmeId = 27;
	public final static int nr_fireSmeId = 28;
	public final static int nr_fireSeId = 29;
	public final static int nr_trId = 30;
	public final static int nr_trSdIdOld = 31;
	public final static int nr_trSdIdNew = 32;
	public final static int nr_trSeq = 33;
	public final static int nr_workdir = 34;
	public final static int nr_logfile = 35;
	public final static int nr_errlogfile = 36;
	public final static int nr_pid = 37;
	public final static int nr_extPid = 38;
	public final static int nr_errorMsg = 39;
	public final static int nr_killId = 40;
	public final static int nr_killExitCode = 41;
	public final static int nr_isSuspended = 42;
	public final static int nr_isSuspendedLocal = 43;
	public final static int nr_priority = 44;
	public final static int nr_nice = 45;
	public final static int nr_minPriority = 46;
	public final static int nr_agingAmount = 47;
	public final static int nr_parentSuspended = 48;
	public final static int nr_childSuspended = 49;
	public final static int nr_warnCount = 50;
	public final static int nr_warnLink = 51;
	public final static int nr_submitTs = 52;
	public final static int nr_resumeTs = 53;
	public final static int nr_syncTs = 54;
	public final static int nr_resourceTs = 55;
	public final static int nr_runnableTs = 56;
	public final static int nr_startTs = 57;
	public final static int nr_finishTs = 58;
	public final static int nr_finalTs = 59;
	public final static int nr_cntSubmitted = 60;
	public final static int nr_cntDependencyWait = 61;
	public final static int nr_cntSynchronizeWait = 62;
	public final static int nr_cntResourceWait = 63;
	public final static int nr_cntRunnable = 64;
	public final static int nr_cntStarting = 65;
	public final static int nr_cntStarted = 66;
	public final static int nr_cntRunning = 67;
	public final static int nr_cntToKill = 68;
	public final static int nr_cntKilled = 69;
	public final static int nr_cntCancelled = 70;
	public final static int nr_cntFinished = 71;
	public final static int nr_cntFinal = 72;
	public final static int nr_cntBrokenActive = 73;
	public final static int nr_cntBrokenFinished = 74;
	public final static int nr_cntError = 75;
	public final static int nr_cntUnreachable = 76;
	public final static int nr_cntRestartable = 77;
	public final static int nr_cntWarn = 78;
	public final static int nr_cntPending = 79;
	public final static int nr_creatorUId = 80;
	public final static int nr_createTs = 81;
	public final static int nr_changerUId = 82;
	public final static int nr_changeTs = 83;

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
	protected Boolean isSuspended;
	protected Boolean isSuspendedLocal;
	protected Integer priority;
	protected Integer nice;
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
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

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
	        Boolean p_isSuspended,
	        Boolean p_isSuspendedLocal,
	        Integer p_priority,
	        Integer p_nice,
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
		nice = p_nice;
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

	public	SDMSSubmittedEntityGeneric setAccessKey (SystemEnvironment env, Long p_accessKey)
	throws SDMSException
	{
		if(accessKey.equals(p_accessKey)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.accessKey = p_accessKey;
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

	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		return (masterId);
	}

	public	SDMSSubmittedEntityGeneric setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		if(masterId.equals(p_masterId)) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getSubmitTag (SystemEnvironment env)
	throws SDMSException
	{
		return (submitTag);
	}

	public	SDMSSubmittedEntityGeneric setSubmitTag (SystemEnvironment env, String p_submitTag)
	throws SDMSException
	{
		if(p_submitTag != null && p_submitTag.equals(submitTag)) return this;
		if(p_submitTag == null && submitTag == null) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
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

	public	SDMSSubmittedEntityGeneric setUnresolvedHandling (SystemEnvironment env, Integer p_unresolvedHandling)
	throws SDMSException
	{
		if(p_unresolvedHandling != null && p_unresolvedHandling.equals(unresolvedHandling)) return this;
		if(p_unresolvedHandling == null && unresolvedHandling == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.unresolvedHandling = p_unresolvedHandling;
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

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	SDMSSubmittedEntityGeneric setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getChildTag (SystemEnvironment env)
	throws SDMSException
	{
		return (childTag);
	}

	public	SDMSSubmittedEntityGeneric setChildTag (SystemEnvironment env, String p_childTag)
	throws SDMSException
	{
		if(p_childTag != null && p_childTag.equals(childTag)) return this;
		if(p_childTag == null && childTag == null) return this;
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

	public	SDMSSubmittedEntityGeneric setSeVersion (SystemEnvironment env, Long p_seVersion)
	throws SDMSException
	{
		if(seVersion.equals(p_seVersion)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (ownerId);
	}

	public	SDMSSubmittedEntityGeneric setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		return (parentId);
	}

	public	SDMSSubmittedEntityGeneric setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		if(p_parentId != null && p_parentId.equals(parentId)) return this;
		if(p_parentId == null && parentId == null) return this;
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

	public	SDMSSubmittedEntityGeneric setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		if(p_scopeId != null && p_scopeId.equals(scopeId)) return this;
		if(p_scopeId == null && scopeId == null) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsStatic (SystemEnvironment env)
	throws SDMSException
	{
		return (isStatic);
	}

	public	SDMSSubmittedEntityGeneric setIsStatic (SystemEnvironment env, Boolean p_isStatic)
	throws SDMSException
	{
		if(isStatic.equals(p_isStatic)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.isStatic = p_isStatic;
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

	public	SDMSSubmittedEntityGeneric setMergeMode (SystemEnvironment env, Integer p_mergeMode)
	throws SDMSException
	{
		if(mergeMode.equals(p_mergeMode)) return this;
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

	public	SDMSSubmittedEntityGeneric setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		if(state.equals(p_state)) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getJobEsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (jobEsdId);
	}

	public	SDMSSubmittedEntityGeneric setJobEsdId (SystemEnvironment env, Long p_jobEsdId)
	throws SDMSException
	{
		if(p_jobEsdId != null && p_jobEsdId.equals(jobEsdId)) return this;
		if(p_jobEsdId == null && jobEsdId == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.jobEsdId = p_jobEsdId;
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

	public Integer getJobEsdPref (SystemEnvironment env)
	throws SDMSException
	{
		return (jobEsdPref);
	}

	public	SDMSSubmittedEntityGeneric setJobEsdPref (SystemEnvironment env, Integer p_jobEsdPref)
	throws SDMSException
	{
		if(p_jobEsdPref != null && p_jobEsdPref.equals(jobEsdPref)) return this;
		if(p_jobEsdPref == null && jobEsdPref == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.jobEsdPref = p_jobEsdPref;
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

	public Boolean getJobIsFinal (SystemEnvironment env)
	throws SDMSException
	{
		return (jobIsFinal);
	}

	public	SDMSSubmittedEntityGeneric setJobIsFinal (SystemEnvironment env, Boolean p_jobIsFinal)
	throws SDMSException
	{
		if(jobIsFinal.equals(p_jobIsFinal)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.jobIsFinal = p_jobIsFinal;
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

	public Boolean getJobIsRestartable (SystemEnvironment env)
	throws SDMSException
	{
		return (jobIsRestartable);
	}

	public	SDMSSubmittedEntityGeneric setJobIsRestartable (SystemEnvironment env, Boolean p_jobIsRestartable)
	throws SDMSException
	{
		if(jobIsRestartable.equals(p_jobIsRestartable)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.jobIsRestartable = p_jobIsRestartable;
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

	public Long getFinalEsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (finalEsdId);
	}

	public	SDMSSubmittedEntityGeneric setFinalEsdId (SystemEnvironment env, Long p_finalEsdId)
	throws SDMSException
	{
		if(p_finalEsdId != null && p_finalEsdId.equals(finalEsdId)) return this;
		if(p_finalEsdId == null && finalEsdId == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.finalEsdId = p_finalEsdId;
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

	public	SDMSSubmittedEntityGeneric setExitCode (SystemEnvironment env, Integer p_exitCode)
	throws SDMSException
	{
		if(p_exitCode != null && p_exitCode.equals(exitCode)) return this;
		if(p_exitCode == null && exitCode == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public	SDMSSubmittedEntityGeneric setCommandline (SystemEnvironment env, String p_commandline)
	throws SDMSException
	{
		if(p_commandline != null && p_commandline.equals(commandline)) return this;
		if(p_commandline == null && commandline == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public String getRrCommandline (SystemEnvironment env)
	throws SDMSException
	{
		return (rrCommandline);
	}

	public	SDMSSubmittedEntityGeneric setRrCommandline (SystemEnvironment env, String p_rrCommandline)
	throws SDMSException
	{
		if(p_rrCommandline != null && p_rrCommandline.equals(rrCommandline)) return this;
		if(p_rrCommandline == null && rrCommandline == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			if (p_rrCommandline != null && p_rrCommandline.length() > 512) {
				p_rrCommandline = p_rrCommandline.substring(0,512);
			}
			o.rrCommandline = p_rrCommandline;
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

	public Integer getRerunSeq (SystemEnvironment env)
	throws SDMSException
	{
		return (rerunSeq);
	}

	public	SDMSSubmittedEntityGeneric setRerunSeq (SystemEnvironment env, Integer p_rerunSeq)
	throws SDMSException
	{
		if(rerunSeq.equals(p_rerunSeq)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.rerunSeq = p_rerunSeq;
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

	public Boolean getIsReplaced (SystemEnvironment env)
	throws SDMSException
	{
		return (isReplaced);
	}

	public	SDMSSubmittedEntityGeneric setIsReplaced (SystemEnvironment env, Boolean p_isReplaced)
	throws SDMSException
	{
		if(isReplaced.equals(p_isReplaced)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.isReplaced = p_isReplaced;
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

	public Boolean getIsCancelled (SystemEnvironment env)
	throws SDMSException
	{
		return (isCancelled);
	}

	public	SDMSSubmittedEntityGeneric setIsCancelled (SystemEnvironment env, Boolean p_isCancelled)
	throws SDMSException
	{
		if(p_isCancelled != null && p_isCancelled.equals(isCancelled)) return this;
		if(p_isCancelled == null && isCancelled == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.isCancelled = p_isCancelled;
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

	public Long getBaseSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (baseSmeId);
	}

	public	SDMSSubmittedEntityGeneric setBaseSmeId (SystemEnvironment env, Long p_baseSmeId)
	throws SDMSException
	{
		if(p_baseSmeId != null && p_baseSmeId.equals(baseSmeId)) return this;
		if(p_baseSmeId == null && baseSmeId == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.baseSmeId = p_baseSmeId;
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

	public Long getReasonSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (reasonSmeId);
	}

	public	SDMSSubmittedEntityGeneric setReasonSmeId (SystemEnvironment env, Long p_reasonSmeId)
	throws SDMSException
	{
		if(p_reasonSmeId != null && p_reasonSmeId.equals(reasonSmeId)) return this;
		if(p_reasonSmeId == null && reasonSmeId == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.reasonSmeId = p_reasonSmeId;
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

	public Long getFireSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (fireSmeId);
	}

	public	SDMSSubmittedEntityGeneric setFireSmeId (SystemEnvironment env, Long p_fireSmeId)
	throws SDMSException
	{
		if(p_fireSmeId != null && p_fireSmeId.equals(fireSmeId)) return this;
		if(p_fireSmeId == null && fireSmeId == null) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getFireSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (fireSeId);
	}

	public	SDMSSubmittedEntityGeneric setFireSeId (SystemEnvironment env, Long p_fireSeId)
	throws SDMSException
	{
		if(p_fireSeId != null && p_fireSeId.equals(fireSeId)) return this;
		if(p_fireSeId == null && fireSeId == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.fireSeId = p_fireSeId;
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

	public Long getTrId (SystemEnvironment env)
	throws SDMSException
	{
		return (trId);
	}

	public	SDMSSubmittedEntityGeneric setTrId (SystemEnvironment env, Long p_trId)
	throws SDMSException
	{
		if(p_trId != null && p_trId.equals(trId)) return this;
		if(p_trId == null && trId == null) return this;
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
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getTrSdIdOld (SystemEnvironment env)
	throws SDMSException
	{
		return (trSdIdOld);
	}

	public	SDMSSubmittedEntityGeneric setTrSdIdOld (SystemEnvironment env, Long p_trSdIdOld)
	throws SDMSException
	{
		if(p_trSdIdOld != null && p_trSdIdOld.equals(trSdIdOld)) return this;
		if(p_trSdIdOld == null && trSdIdOld == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.trSdIdOld = p_trSdIdOld;
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

	public Long getTrSdIdNew (SystemEnvironment env)
	throws SDMSException
	{
		return (trSdIdNew);
	}

	public	SDMSSubmittedEntityGeneric setTrSdIdNew (SystemEnvironment env, Long p_trSdIdNew)
	throws SDMSException
	{
		if(p_trSdIdNew != null && p_trSdIdNew.equals(trSdIdNew)) return this;
		if(p_trSdIdNew == null && trSdIdNew == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.trSdIdNew = p_trSdIdNew;
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

	public Integer getTrSeq (SystemEnvironment env)
	throws SDMSException
	{
		return (trSeq);
	}

	public	SDMSSubmittedEntityGeneric setTrSeq (SystemEnvironment env, Integer p_trSeq)
	throws SDMSException
	{
		if(trSeq.equals(p_trSeq)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.trSeq = p_trSeq;
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

	public String getWorkdir (SystemEnvironment env)
	throws SDMSException
	{
		return (workdir);
	}

	public	SDMSSubmittedEntityGeneric setWorkdir (SystemEnvironment env, String p_workdir)
	throws SDMSException
	{
		if(p_workdir != null && p_workdir.equals(workdir)) return this;
		if(p_workdir == null && workdir == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			if (p_workdir != null && p_workdir.length() > 512) {
				p_workdir = p_workdir.substring(0,512);
			}
			o.workdir = p_workdir;
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

	public	SDMSSubmittedEntityGeneric setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		if(p_logfile != null && p_logfile.equals(logfile)) return this;
		if(p_logfile == null && logfile == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public	SDMSSubmittedEntityGeneric setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		if(p_errlogfile != null && p_errlogfile.equals(errlogfile)) return this;
		if(p_errlogfile == null && errlogfile == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public	SDMSSubmittedEntityGeneric setPid (SystemEnvironment env, String p_pid)
	throws SDMSException
	{
		if(p_pid != null && p_pid.equals(pid)) return this;
		if(p_pid == null && pid == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			if (p_pid != null && p_pid.length() > 32) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "pid", "32")
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

	public	SDMSSubmittedEntityGeneric setExtPid (SystemEnvironment env, String p_extPid)
	throws SDMSException
	{
		if(p_extPid != null && p_extPid.equals(extPid)) return this;
		if(p_extPid == null && extPid == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			if (p_extPid != null && p_extPid.length() > 32) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(SubmittedEntity) Length of $1 exceeds maximum length $2", "extPid", "32")
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

	public	SDMSSubmittedEntityGeneric setErrorMsg (SystemEnvironment env, String p_errorMsg)
	throws SDMSException
	{
		if(p_errorMsg != null && p_errorMsg.equals(errorMsg)) return this;
		if(p_errorMsg == null && errorMsg == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public Long getKillId (SystemEnvironment env)
	throws SDMSException
	{
		return (killId);
	}

	public	SDMSSubmittedEntityGeneric setKillId (SystemEnvironment env, Long p_killId)
	throws SDMSException
	{
		if(p_killId != null && p_killId.equals(killId)) return this;
		if(p_killId == null && killId == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.killId = p_killId;
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

	public Integer getKillExitCode (SystemEnvironment env)
	throws SDMSException
	{
		return (killExitCode);
	}

	public	SDMSSubmittedEntityGeneric setKillExitCode (SystemEnvironment env, Integer p_killExitCode)
	throws SDMSException
	{
		if(p_killExitCode != null && p_killExitCode.equals(killExitCode)) return this;
		if(p_killExitCode == null && killExitCode == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.killExitCode = p_killExitCode;
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

	public Boolean getIsSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (isSuspended);
	}

	public String getIsSuspendedAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsSuspended (env);
		final boolean b = v.booleanValue();
		if (b == SDMSSubmittedEntity.SUSPEND)
			return "SUSPEND";
		if (b == SDMSSubmittedEntity.NOSUSPEND)
			return "NOSUSPEND";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SubmittedEntity.isSuspended: $1",
		                          getIsSuspended (env)));
	}

	public	SDMSSubmittedEntityGeneric setIsSuspended (SystemEnvironment env, Boolean p_isSuspended)
	throws SDMSException
	{
		if(isSuspended.equals(p_isSuspended)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.isSuspended = p_isSuspended;
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

	public Boolean getIsSuspendedLocal (SystemEnvironment env)
	throws SDMSException
	{
		return (isSuspendedLocal);
	}

	public	SDMSSubmittedEntityGeneric setIsSuspendedLocal (SystemEnvironment env, Boolean p_isSuspendedLocal)
	throws SDMSException
	{
		if(p_isSuspendedLocal != null && p_isSuspendedLocal.equals(isSuspendedLocal)) return this;
		if(p_isSuspendedLocal == null && isSuspendedLocal == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.isSuspendedLocal = p_isSuspendedLocal;
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

	public Integer getPriority (SystemEnvironment env)
	throws SDMSException
	{
		return (priority);
	}

	public	SDMSSubmittedEntityGeneric setPriority (SystemEnvironment env, Integer p_priority)
	throws SDMSException
	{
		if(priority.equals(p_priority)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.priority = p_priority;
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

	public Integer getNice (SystemEnvironment env)
	throws SDMSException
	{
		return (nice);
	}

	public	SDMSSubmittedEntityGeneric setNice (SystemEnvironment env, Integer p_nice)
	throws SDMSException
	{
		if(nice.equals(p_nice)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.nice = p_nice;
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

	public Integer getMinPriority (SystemEnvironment env)
	throws SDMSException
	{
		return (minPriority);
	}

	public	SDMSSubmittedEntityGeneric setMinPriority (SystemEnvironment env, Integer p_minPriority)
	throws SDMSException
	{
		if(minPriority.equals(p_minPriority)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.minPriority = p_minPriority;
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

	public Integer getAgingAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (agingAmount);
	}

	public	SDMSSubmittedEntityGeneric setAgingAmount (SystemEnvironment env, Integer p_agingAmount)
	throws SDMSException
	{
		if(agingAmount.equals(p_agingAmount)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.agingAmount = p_agingAmount;
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

	public Integer getParentSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (parentSuspended);
	}

	public	SDMSSubmittedEntityGeneric setParentSuspended (SystemEnvironment env, Integer p_parentSuspended)
	throws SDMSException
	{
		if(parentSuspended.equals(p_parentSuspended)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.parentSuspended = p_parentSuspended;
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

	public Integer getChildSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (childSuspended);
	}

	public	SDMSSubmittedEntityGeneric setChildSuspended (SystemEnvironment env, Integer p_childSuspended)
	throws SDMSException
	{
		if(childSuspended.equals(p_childSuspended)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.childSuspended = p_childSuspended;
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

	public Integer getWarnCount (SystemEnvironment env)
	throws SDMSException
	{
		return (warnCount);
	}

	public	SDMSSubmittedEntityGeneric setWarnCount (SystemEnvironment env, Integer p_warnCount)
	throws SDMSException
	{
		if(warnCount.equals(p_warnCount)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.warnCount = p_warnCount;
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

	public Long getWarnLink (SystemEnvironment env)
	throws SDMSException
	{
		return (warnLink);
	}

	public	SDMSSubmittedEntityGeneric setWarnLink (SystemEnvironment env, Long p_warnLink)
	throws SDMSException
	{
		if(p_warnLink != null && p_warnLink.equals(warnLink)) return this;
		if(p_warnLink == null && warnLink == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.warnLink = p_warnLink;
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

	public Long getSubmitTs (SystemEnvironment env)
	throws SDMSException
	{
		return (submitTs);
	}

	public	SDMSSubmittedEntityGeneric setSubmitTs (SystemEnvironment env, Long p_submitTs)
	throws SDMSException
	{
		if(submitTs.equals(p_submitTs)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.submitTs = p_submitTs;
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

	public Long getResumeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeTs);
	}

	public	SDMSSubmittedEntityGeneric setResumeTs (SystemEnvironment env, Long p_resumeTs)
	throws SDMSException
	{
		if(p_resumeTs != null && p_resumeTs.equals(resumeTs)) return this;
		if(p_resumeTs == null && resumeTs == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.resumeTs = p_resumeTs;
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

	public Long getSyncTs (SystemEnvironment env)
	throws SDMSException
	{
		return (syncTs);
	}

	public	SDMSSubmittedEntityGeneric setSyncTs (SystemEnvironment env, Long p_syncTs)
	throws SDMSException
	{
		if(p_syncTs != null && p_syncTs.equals(syncTs)) return this;
		if(p_syncTs == null && syncTs == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.syncTs = p_syncTs;
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

	public Long getResourceTs (SystemEnvironment env)
	throws SDMSException
	{
		return (resourceTs);
	}

	public	SDMSSubmittedEntityGeneric setResourceTs (SystemEnvironment env, Long p_resourceTs)
	throws SDMSException
	{
		if(p_resourceTs != null && p_resourceTs.equals(resourceTs)) return this;
		if(p_resourceTs == null && resourceTs == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.resourceTs = p_resourceTs;
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

	public	SDMSSubmittedEntityGeneric setRunnableTs (SystemEnvironment env, Long p_runnableTs)
	throws SDMSException
	{
		if(p_runnableTs != null && p_runnableTs.equals(runnableTs)) return this;
		if(p_runnableTs == null && runnableTs == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public	SDMSSubmittedEntityGeneric setStartTs (SystemEnvironment env, Long p_startTs)
	throws SDMSException
	{
		if(p_startTs != null && p_startTs.equals(startTs)) return this;
		if(p_startTs == null && startTs == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public	SDMSSubmittedEntityGeneric setFinishTs (SystemEnvironment env, Long p_finishTs)
	throws SDMSException
	{
		if(p_finishTs != null && p_finishTs.equals(finishTs)) return this;
		if(p_finishTs == null && finishTs == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public Long getFinalTs (SystemEnvironment env)
	throws SDMSException
	{
		return (finalTs);
	}

	public	SDMSSubmittedEntityGeneric setFinalTs (SystemEnvironment env, Long p_finalTs)
	throws SDMSException
	{
		if(p_finalTs != null && p_finalTs.equals(finalTs)) return this;
		if(p_finalTs == null && finalTs == null) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.finalTs = p_finalTs;
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

	public Integer getCntSubmitted (SystemEnvironment env)
	throws SDMSException
	{
		return (cntSubmitted);
	}

	public	SDMSSubmittedEntityGeneric setCntSubmitted (SystemEnvironment env, Integer p_cntSubmitted)
	throws SDMSException
	{
		if(cntSubmitted.equals(p_cntSubmitted)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntSubmitted = p_cntSubmitted;
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

	public Integer getCntDependencyWait (SystemEnvironment env)
	throws SDMSException
	{
		return (cntDependencyWait);
	}

	public	SDMSSubmittedEntityGeneric setCntDependencyWait (SystemEnvironment env, Integer p_cntDependencyWait)
	throws SDMSException
	{
		if(cntDependencyWait.equals(p_cntDependencyWait)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntDependencyWait = p_cntDependencyWait;
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

	public Integer getCntSynchronizeWait (SystemEnvironment env)
	throws SDMSException
	{
		return (cntSynchronizeWait);
	}

	public	SDMSSubmittedEntityGeneric setCntSynchronizeWait (SystemEnvironment env, Integer p_cntSynchronizeWait)
	throws SDMSException
	{
		if(cntSynchronizeWait.equals(p_cntSynchronizeWait)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntSynchronizeWait = p_cntSynchronizeWait;
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

	public Integer getCntResourceWait (SystemEnvironment env)
	throws SDMSException
	{
		return (cntResourceWait);
	}

	public	SDMSSubmittedEntityGeneric setCntResourceWait (SystemEnvironment env, Integer p_cntResourceWait)
	throws SDMSException
	{
		if(cntResourceWait.equals(p_cntResourceWait)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntResourceWait = p_cntResourceWait;
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

	public Integer getCntRunnable (SystemEnvironment env)
	throws SDMSException
	{
		return (cntRunnable);
	}

	public	SDMSSubmittedEntityGeneric setCntRunnable (SystemEnvironment env, Integer p_cntRunnable)
	throws SDMSException
	{
		if(cntRunnable.equals(p_cntRunnable)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntRunnable = p_cntRunnable;
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

	public Integer getCntStarting (SystemEnvironment env)
	throws SDMSException
	{
		return (cntStarting);
	}

	public	SDMSSubmittedEntityGeneric setCntStarting (SystemEnvironment env, Integer p_cntStarting)
	throws SDMSException
	{
		if(cntStarting.equals(p_cntStarting)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntStarting = p_cntStarting;
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

	public Integer getCntStarted (SystemEnvironment env)
	throws SDMSException
	{
		return (cntStarted);
	}

	public	SDMSSubmittedEntityGeneric setCntStarted (SystemEnvironment env, Integer p_cntStarted)
	throws SDMSException
	{
		if(cntStarted.equals(p_cntStarted)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntStarted = p_cntStarted;
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

	public Integer getCntRunning (SystemEnvironment env)
	throws SDMSException
	{
		return (cntRunning);
	}

	public	SDMSSubmittedEntityGeneric setCntRunning (SystemEnvironment env, Integer p_cntRunning)
	throws SDMSException
	{
		if(cntRunning.equals(p_cntRunning)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntRunning = p_cntRunning;
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

	public Integer getCntToKill (SystemEnvironment env)
	throws SDMSException
	{
		return (cntToKill);
	}

	public	SDMSSubmittedEntityGeneric setCntToKill (SystemEnvironment env, Integer p_cntToKill)
	throws SDMSException
	{
		if(cntToKill.equals(p_cntToKill)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntToKill = p_cntToKill;
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

	public Integer getCntKilled (SystemEnvironment env)
	throws SDMSException
	{
		return (cntKilled);
	}

	public	SDMSSubmittedEntityGeneric setCntKilled (SystemEnvironment env, Integer p_cntKilled)
	throws SDMSException
	{
		if(cntKilled.equals(p_cntKilled)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntKilled = p_cntKilled;
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

	public Integer getCntCancelled (SystemEnvironment env)
	throws SDMSException
	{
		return (cntCancelled);
	}

	public	SDMSSubmittedEntityGeneric setCntCancelled (SystemEnvironment env, Integer p_cntCancelled)
	throws SDMSException
	{
		if(cntCancelled.equals(p_cntCancelled)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntCancelled = p_cntCancelled;
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

	public Integer getCntFinished (SystemEnvironment env)
	throws SDMSException
	{
		return (cntFinished);
	}

	public	SDMSSubmittedEntityGeneric setCntFinished (SystemEnvironment env, Integer p_cntFinished)
	throws SDMSException
	{
		if(cntFinished.equals(p_cntFinished)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntFinished = p_cntFinished;
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

	public Integer getCntFinal (SystemEnvironment env)
	throws SDMSException
	{
		return (cntFinal);
	}

	public	SDMSSubmittedEntityGeneric setCntFinal (SystemEnvironment env, Integer p_cntFinal)
	throws SDMSException
	{
		if(cntFinal.equals(p_cntFinal)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntFinal = p_cntFinal;
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

	public Integer getCntBrokenActive (SystemEnvironment env)
	throws SDMSException
	{
		return (cntBrokenActive);
	}

	public	SDMSSubmittedEntityGeneric setCntBrokenActive (SystemEnvironment env, Integer p_cntBrokenActive)
	throws SDMSException
	{
		if(cntBrokenActive.equals(p_cntBrokenActive)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntBrokenActive = p_cntBrokenActive;
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

	public Integer getCntBrokenFinished (SystemEnvironment env)
	throws SDMSException
	{
		return (cntBrokenFinished);
	}

	public	SDMSSubmittedEntityGeneric setCntBrokenFinished (SystemEnvironment env, Integer p_cntBrokenFinished)
	throws SDMSException
	{
		if(cntBrokenFinished.equals(p_cntBrokenFinished)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntBrokenFinished = p_cntBrokenFinished;
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

	public Integer getCntError (SystemEnvironment env)
	throws SDMSException
	{
		return (cntError);
	}

	public	SDMSSubmittedEntityGeneric setCntError (SystemEnvironment env, Integer p_cntError)
	throws SDMSException
	{
		if(cntError.equals(p_cntError)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntError = p_cntError;
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

	public Integer getCntUnreachable (SystemEnvironment env)
	throws SDMSException
	{
		return (cntUnreachable);
	}

	public	SDMSSubmittedEntityGeneric setCntUnreachable (SystemEnvironment env, Integer p_cntUnreachable)
	throws SDMSException
	{
		if(cntUnreachable.equals(p_cntUnreachable)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntUnreachable = p_cntUnreachable;
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

	public Integer getCntRestartable (SystemEnvironment env)
	throws SDMSException
	{
		return (cntRestartable);
	}

	public	SDMSSubmittedEntityGeneric setCntRestartable (SystemEnvironment env, Integer p_cntRestartable)
	throws SDMSException
	{
		if(cntRestartable.equals(p_cntRestartable)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntRestartable = p_cntRestartable;
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

	public Integer getCntWarn (SystemEnvironment env)
	throws SDMSException
	{
		return (cntWarn);
	}

	public	SDMSSubmittedEntityGeneric setCntWarn (SystemEnvironment env, Integer p_cntWarn)
	throws SDMSException
	{
		if(cntWarn.equals(p_cntWarn)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntWarn = p_cntWarn;
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

	public Integer getCntPending (SystemEnvironment env)
	throws SDMSException
	{
		return (cntPending);
	}

	public	SDMSSubmittedEntityGeneric setCntPending (SystemEnvironment env, Integer p_cntPending)
	throws SDMSException
	{
		if(cntPending.equals(p_cntPending)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
			o.cntPending = p_cntPending;
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

	SDMSSubmittedEntityGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	SDMSSubmittedEntityGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntity) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	public	SDMSSubmittedEntityGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSSubmittedEntityGeneric) change(env);
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

	SDMSSubmittedEntityGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSSubmittedEntityGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSSubmittedEntityGeneric) change(env);
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
	                                     Boolean p_isSuspended,
	                                     Boolean p_isSuspendedLocal,
	                                     Integer p_priority,
	                                     Integer p_nice,
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
		nice = p_nice;
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
				        ", " + squote + "NICE" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "SubmittedEntity: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, accessKey.longValue());
			pInsert.setLong (3, masterId.longValue());
			if (submitTag == null)
				pInsert.setNull(4, Types.VARCHAR);
			else
				pInsert.setString(4, submitTag);
			if (unresolvedHandling == null)
				pInsert.setNull(5, Types.INTEGER);
			else
				pInsert.setInt(5, unresolvedHandling.intValue());
			pInsert.setLong (6, seId.longValue());
			if (childTag == null)
				pInsert.setNull(7, Types.VARCHAR);
			else
				pInsert.setString(7, childTag);
			pInsert.setLong (8, seVersion.longValue());
			pInsert.setLong (9, ownerId.longValue());
			if (parentId == null)
				pInsert.setNull(10, Types.INTEGER);
			else
				pInsert.setLong (10, parentId.longValue());
			if (scopeId == null)
				pInsert.setNull(11, Types.INTEGER);
			else
				pInsert.setLong (11, scopeId.longValue());
			pInsert.setInt (12, isStatic.booleanValue() ? 1 : 0);
			pInsert.setInt(13, mergeMode.intValue());
			pInsert.setInt(14, state.intValue());
			if (jobEsdId == null)
				pInsert.setNull(15, Types.INTEGER);
			else
				pInsert.setLong (15, jobEsdId.longValue());
			if (jobEsdPref == null)
				pInsert.setNull(16, Types.INTEGER);
			else
				pInsert.setInt(16, jobEsdPref.intValue());
			pInsert.setInt (17, jobIsFinal.booleanValue() ? 1 : 0);
			pInsert.setInt (18, jobIsRestartable.booleanValue() ? 1 : 0);
			if (finalEsdId == null)
				pInsert.setNull(19, Types.INTEGER);
			else
				pInsert.setLong (19, finalEsdId.longValue());
			if (exitCode == null)
				pInsert.setNull(20, Types.INTEGER);
			else
				pInsert.setInt(20, exitCode.intValue());
			if (commandline == null)
				pInsert.setNull(21, Types.VARCHAR);
			else
				pInsert.setString(21, commandline);
			if (rrCommandline == null)
				pInsert.setNull(22, Types.VARCHAR);
			else
				pInsert.setString(22, rrCommandline);
			pInsert.setInt(23, rerunSeq.intValue());
			pInsert.setInt (24, isReplaced.booleanValue() ? 1 : 0);
			if (isCancelled == null)
				pInsert.setNull(25, Types.INTEGER);
			else
				pInsert.setInt (25, isCancelled.booleanValue() ? 1 : 0);
			if (baseSmeId == null)
				pInsert.setNull(26, Types.INTEGER);
			else
				pInsert.setLong (26, baseSmeId.longValue());
			if (reasonSmeId == null)
				pInsert.setNull(27, Types.INTEGER);
			else
				pInsert.setLong (27, reasonSmeId.longValue());
			if (fireSmeId == null)
				pInsert.setNull(28, Types.INTEGER);
			else
				pInsert.setLong (28, fireSmeId.longValue());
			if (fireSeId == null)
				pInsert.setNull(29, Types.INTEGER);
			else
				pInsert.setLong (29, fireSeId.longValue());
			if (trId == null)
				pInsert.setNull(30, Types.INTEGER);
			else
				pInsert.setLong (30, trId.longValue());
			if (trSdIdOld == null)
				pInsert.setNull(31, Types.INTEGER);
			else
				pInsert.setLong (31, trSdIdOld.longValue());
			if (trSdIdNew == null)
				pInsert.setNull(32, Types.INTEGER);
			else
				pInsert.setLong (32, trSdIdNew.longValue());
			pInsert.setInt(33, trSeq.intValue());
			if (workdir == null)
				pInsert.setNull(34, Types.VARCHAR);
			else
				pInsert.setString(34, workdir);
			if (logfile == null)
				pInsert.setNull(35, Types.VARCHAR);
			else
				pInsert.setString(35, logfile);
			if (errlogfile == null)
				pInsert.setNull(36, Types.VARCHAR);
			else
				pInsert.setString(36, errlogfile);
			if (pid == null)
				pInsert.setNull(37, Types.VARCHAR);
			else
				pInsert.setString(37, pid);
			if (extPid == null)
				pInsert.setNull(38, Types.VARCHAR);
			else
				pInsert.setString(38, extPid);
			if (errorMsg == null)
				pInsert.setNull(39, Types.VARCHAR);
			else
				pInsert.setString(39, errorMsg);
			if (killId == null)
				pInsert.setNull(40, Types.INTEGER);
			else
				pInsert.setLong (40, killId.longValue());
			if (killExitCode == null)
				pInsert.setNull(41, Types.INTEGER);
			else
				pInsert.setInt(41, killExitCode.intValue());
			pInsert.setInt (42, isSuspended.booleanValue() ? 1 : 0);
			if (isSuspendedLocal == null)
				pInsert.setNull(43, Types.INTEGER);
			else
				pInsert.setInt (43, isSuspendedLocal.booleanValue() ? 1 : 0);
			pInsert.setInt(44, priority.intValue());
			pInsert.setInt(45, nice.intValue());
			pInsert.setInt(46, minPriority.intValue());
			pInsert.setInt(47, agingAmount.intValue());
			pInsert.setInt(48, parentSuspended.intValue());
			pInsert.setInt(49, childSuspended.intValue());
			pInsert.setInt(50, warnCount.intValue());
			if (warnLink == null)
				pInsert.setNull(51, Types.INTEGER);
			else
				pInsert.setLong (51, warnLink.longValue());
			pInsert.setLong (52, submitTs.longValue());
			if (resumeTs == null)
				pInsert.setNull(53, Types.INTEGER);
			else
				pInsert.setLong (53, resumeTs.longValue());
			if (syncTs == null)
				pInsert.setNull(54, Types.INTEGER);
			else
				pInsert.setLong (54, syncTs.longValue());
			if (resourceTs == null)
				pInsert.setNull(55, Types.INTEGER);
			else
				pInsert.setLong (55, resourceTs.longValue());
			if (runnableTs == null)
				pInsert.setNull(56, Types.INTEGER);
			else
				pInsert.setLong (56, runnableTs.longValue());
			if (startTs == null)
				pInsert.setNull(57, Types.INTEGER);
			else
				pInsert.setLong (57, startTs.longValue());
			if (finishTs == null)
				pInsert.setNull(58, Types.INTEGER);
			else
				pInsert.setLong (58, finishTs.longValue());
			if (finalTs == null)
				pInsert.setNull(59, Types.INTEGER);
			else
				pInsert.setLong (59, finalTs.longValue());
			pInsert.setInt(60, cntSubmitted.intValue());
			pInsert.setInt(61, cntDependencyWait.intValue());
			pInsert.setInt(62, cntSynchronizeWait.intValue());
			pInsert.setInt(63, cntResourceWait.intValue());
			pInsert.setInt(64, cntRunnable.intValue());
			pInsert.setInt(65, cntStarting.intValue());
			pInsert.setInt(66, cntStarted.intValue());
			pInsert.setInt(67, cntRunning.intValue());
			pInsert.setInt(68, cntToKill.intValue());
			pInsert.setInt(69, cntKilled.intValue());
			pInsert.setInt(70, cntCancelled.intValue());
			pInsert.setInt(71, cntFinished.intValue());
			pInsert.setInt(72, cntFinal.intValue());
			pInsert.setInt(73, cntBrokenActive.intValue());
			pInsert.setInt(74, cntBrokenFinished.intValue());
			pInsert.setInt(75, cntError.intValue());
			pInsert.setInt(76, cntUnreachable.intValue());
			pInsert.setInt(77, cntRestartable.intValue());
			pInsert.setInt(78, cntWarn.intValue());
			pInsert.setInt(79, cntPending.intValue());
			pInsert.setLong (80, creatorUId.longValue());
			pInsert.setLong (81, createTs.longValue());
			pInsert.setLong (82, changerUId.longValue());
			pInsert.setLong (83, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "SubmittedEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM SUBMITTED_ENTITY WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "SubmittedEntity: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "SubmittedEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        ", " + squote + "NICE" + equote + " = ? " +
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
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "SubmittedEntity: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, accessKey.longValue());
			pUpdate.setLong (2, masterId.longValue());
			if (submitTag == null)
				pUpdate.setNull(3, Types.VARCHAR);
			else
				pUpdate.setString(3, submitTag);
			if (unresolvedHandling == null)
				pUpdate.setNull(4, Types.INTEGER);
			else
				pUpdate.setInt(4, unresolvedHandling.intValue());
			pUpdate.setLong (5, seId.longValue());
			if (childTag == null)
				pUpdate.setNull(6, Types.VARCHAR);
			else
				pUpdate.setString(6, childTag);
			pUpdate.setLong (7, seVersion.longValue());
			pUpdate.setLong (8, ownerId.longValue());
			if (parentId == null)
				pUpdate.setNull(9, Types.INTEGER);
			else
				pUpdate.setLong (9, parentId.longValue());
			if (scopeId == null)
				pUpdate.setNull(10, Types.INTEGER);
			else
				pUpdate.setLong (10, scopeId.longValue());
			pUpdate.setInt (11, isStatic.booleanValue() ? 1 : 0);
			pUpdate.setInt(12, mergeMode.intValue());
			pUpdate.setInt(13, state.intValue());
			if (jobEsdId == null)
				pUpdate.setNull(14, Types.INTEGER);
			else
				pUpdate.setLong (14, jobEsdId.longValue());
			if (jobEsdPref == null)
				pUpdate.setNull(15, Types.INTEGER);
			else
				pUpdate.setInt(15, jobEsdPref.intValue());
			pUpdate.setInt (16, jobIsFinal.booleanValue() ? 1 : 0);
			pUpdate.setInt (17, jobIsRestartable.booleanValue() ? 1 : 0);
			if (finalEsdId == null)
				pUpdate.setNull(18, Types.INTEGER);
			else
				pUpdate.setLong (18, finalEsdId.longValue());
			if (exitCode == null)
				pUpdate.setNull(19, Types.INTEGER);
			else
				pUpdate.setInt(19, exitCode.intValue());
			if (commandline == null)
				pUpdate.setNull(20, Types.VARCHAR);
			else
				pUpdate.setString(20, commandline);
			if (rrCommandline == null)
				pUpdate.setNull(21, Types.VARCHAR);
			else
				pUpdate.setString(21, rrCommandline);
			pUpdate.setInt(22, rerunSeq.intValue());
			pUpdate.setInt (23, isReplaced.booleanValue() ? 1 : 0);
			if (isCancelled == null)
				pUpdate.setNull(24, Types.INTEGER);
			else
				pUpdate.setInt (24, isCancelled.booleanValue() ? 1 : 0);
			if (baseSmeId == null)
				pUpdate.setNull(25, Types.INTEGER);
			else
				pUpdate.setLong (25, baseSmeId.longValue());
			if (reasonSmeId == null)
				pUpdate.setNull(26, Types.INTEGER);
			else
				pUpdate.setLong (26, reasonSmeId.longValue());
			if (fireSmeId == null)
				pUpdate.setNull(27, Types.INTEGER);
			else
				pUpdate.setLong (27, fireSmeId.longValue());
			if (fireSeId == null)
				pUpdate.setNull(28, Types.INTEGER);
			else
				pUpdate.setLong (28, fireSeId.longValue());
			if (trId == null)
				pUpdate.setNull(29, Types.INTEGER);
			else
				pUpdate.setLong (29, trId.longValue());
			if (trSdIdOld == null)
				pUpdate.setNull(30, Types.INTEGER);
			else
				pUpdate.setLong (30, trSdIdOld.longValue());
			if (trSdIdNew == null)
				pUpdate.setNull(31, Types.INTEGER);
			else
				pUpdate.setLong (31, trSdIdNew.longValue());
			pUpdate.setInt(32, trSeq.intValue());
			if (workdir == null)
				pUpdate.setNull(33, Types.VARCHAR);
			else
				pUpdate.setString(33, workdir);
			if (logfile == null)
				pUpdate.setNull(34, Types.VARCHAR);
			else
				pUpdate.setString(34, logfile);
			if (errlogfile == null)
				pUpdate.setNull(35, Types.VARCHAR);
			else
				pUpdate.setString(35, errlogfile);
			if (pid == null)
				pUpdate.setNull(36, Types.VARCHAR);
			else
				pUpdate.setString(36, pid);
			if (extPid == null)
				pUpdate.setNull(37, Types.VARCHAR);
			else
				pUpdate.setString(37, extPid);
			if (errorMsg == null)
				pUpdate.setNull(38, Types.VARCHAR);
			else
				pUpdate.setString(38, errorMsg);
			if (killId == null)
				pUpdate.setNull(39, Types.INTEGER);
			else
				pUpdate.setLong (39, killId.longValue());
			if (killExitCode == null)
				pUpdate.setNull(40, Types.INTEGER);
			else
				pUpdate.setInt(40, killExitCode.intValue());
			pUpdate.setInt (41, isSuspended.booleanValue() ? 1 : 0);
			if (isSuspendedLocal == null)
				pUpdate.setNull(42, Types.INTEGER);
			else
				pUpdate.setInt (42, isSuspendedLocal.booleanValue() ? 1 : 0);
			pUpdate.setInt(43, priority.intValue());
			pUpdate.setInt(44, nice.intValue());
			pUpdate.setInt(45, minPriority.intValue());
			pUpdate.setInt(46, agingAmount.intValue());
			pUpdate.setInt(47, parentSuspended.intValue());
			pUpdate.setInt(48, childSuspended.intValue());
			pUpdate.setInt(49, warnCount.intValue());
			if (warnLink == null)
				pUpdate.setNull(50, Types.INTEGER);
			else
				pUpdate.setLong (50, warnLink.longValue());
			pUpdate.setLong (51, submitTs.longValue());
			if (resumeTs == null)
				pUpdate.setNull(52, Types.INTEGER);
			else
				pUpdate.setLong (52, resumeTs.longValue());
			if (syncTs == null)
				pUpdate.setNull(53, Types.INTEGER);
			else
				pUpdate.setLong (53, syncTs.longValue());
			if (resourceTs == null)
				pUpdate.setNull(54, Types.INTEGER);
			else
				pUpdate.setLong (54, resourceTs.longValue());
			if (runnableTs == null)
				pUpdate.setNull(55, Types.INTEGER);
			else
				pUpdate.setLong (55, runnableTs.longValue());
			if (startTs == null)
				pUpdate.setNull(56, Types.INTEGER);
			else
				pUpdate.setLong (56, startTs.longValue());
			if (finishTs == null)
				pUpdate.setNull(57, Types.INTEGER);
			else
				pUpdate.setLong (57, finishTs.longValue());
			if (finalTs == null)
				pUpdate.setNull(58, Types.INTEGER);
			else
				pUpdate.setLong (58, finalTs.longValue());
			pUpdate.setInt(59, cntSubmitted.intValue());
			pUpdate.setInt(60, cntDependencyWait.intValue());
			pUpdate.setInt(61, cntSynchronizeWait.intValue());
			pUpdate.setInt(62, cntResourceWait.intValue());
			pUpdate.setInt(63, cntRunnable.intValue());
			pUpdate.setInt(64, cntStarting.intValue());
			pUpdate.setInt(65, cntStarted.intValue());
			pUpdate.setInt(66, cntRunning.intValue());
			pUpdate.setInt(67, cntToKill.intValue());
			pUpdate.setInt(68, cntKilled.intValue());
			pUpdate.setInt(69, cntCancelled.intValue());
			pUpdate.setInt(70, cntFinished.intValue());
			pUpdate.setInt(71, cntFinal.intValue());
			pUpdate.setInt(72, cntBrokenActive.intValue());
			pUpdate.setInt(73, cntBrokenFinished.intValue());
			pUpdate.setInt(74, cntError.intValue());
			pUpdate.setInt(75, cntUnreachable.intValue());
			pUpdate.setInt(76, cntRestartable.intValue());
			pUpdate.setInt(77, cntWarn.intValue());
			pUpdate.setInt(78, cntPending.intValue());
			pUpdate.setLong (79, creatorUId.longValue());
			pUpdate.setLong (80, createTs.longValue());
			pUpdate.setLong (81, changerUId.longValue());
			pUpdate.setLong (82, changeTs.longValue());
			pUpdate.setLong(83, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "SubmittedEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
	static public boolean checkIsSuspended(Boolean p)
	{
		if(p.booleanValue() == SDMSSubmittedEntity.SUSPEND) return true;
		if(p.booleanValue() == SDMSSubmittedEntity.NOSUSPEND) return true;
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
		SDMSThread.doTrace(null, "nice : " + nice, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "nice               : " + nice + "\n" +
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
