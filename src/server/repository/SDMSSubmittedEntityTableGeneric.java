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

public class SDMSSubmittedEntityTableGeneric extends SDMSTable
{

	public final static String tableName = "SUBMITTED_ENTITY";
	public static SDMSSubmittedEntityTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "ACCESS_KEY"
		, "MASTER_ID"
		, "SUBMIT_TAG"
		, "UNRESOLVED_HANDLING"
		, "SE_ID"
		, "CHILD_TAG"
		, "SE_VERSION"
		, "OWNER_ID"
		, "PARENT_ID"
		, "SCOPE_ID"
		, "IS_STATIC"
		, "IS_DISABLED"
		, "OLD_STATE"
		, "MERGE_MODE"
		, "STATE"
		, "JOB_ESD_ID"
		, "JOB_ESD_PREF"
		, "JOB_IS_FINAL"
		, "JOB_IS_RESTARTABLE"
		, "FINAL_ESD_ID"
		, "EXIT_CODE"
		, "COMMANDLINE"
		, "RR_COMMANDLINE"
		, "RERUN_SEQ"
		, "IS_REPLACED"
		, "IS_CANCELLED"
		, "BASE_SME_ID"
		, "REASON_SME_ID"
		, "FIRE_SME_ID"
		, "FIRE_SE_ID"
		, "TR_ID"
		, "TR_SD_ID_OLD"
		, "TR_SD_ID_NEW"
		, "TR_SEQ"
		, "WORKDIR"
		, "LOGFILE"
		, "ERRLOGFILE"
		, "PID"
		, "EXTPID"
		, "ERROR_MSG"
		, "KILL_ID"
		, "KILL_EXIT_CODE"
		, "IS_SUSPENDED"
		, "IS_SUSPENDED_LOCAL"
		, "PRIORITY"
		, "RAW_PRIORITY"
		, "NICE"
		, "NP_NICE"
		, "MIN_PRIORITY"
		, "AGING_AMOUNT"
		, "PARENT_SUSPENDED"
		, "CHILD_SUSPENDED"
		, "WARN_COUNT"
		, "WARN_LINK"
		, "SUBMIT_TS"
		, "RESUME_TS"
		, "SYNC_TS"
		, "RESOURCE_TS"
		, "RUNNABLE_TS"
		, "START_TS"
		, "FINSH_TS"
		, "FINAL_TS"
		, "CNT_SUBMITTED"
		, "CNT_DEPENDENCY_WAIT"
		, "CNT_SYNCHRONIZE_WAIT"
		, "CNT_RESOURCE_WAIT"
		, "CNT_RUNNABLE"
		, "CNT_STARTING"
		, "CNT_STARTED"
		, "CNT_RUNNING"
		, "CNT_TO_KILL"
		, "CNT_KILLED"
		, "CNT_CANCELLED"
		, "CNT_FINISHED"
		, "CNT_FINAL"
		, "CNT_BROKEN_ACTIVE"
		, "CNT_BROKEN_FINISHED"
		, "CNT_ERROR"
		, "CNT_UNREACHABLE"
		, "CNT_RESTARTABLE"
		, "CNT_WARN"
		, "CNT_PENDING"
		, "IDLE_TS"
		, "IDLE_TIME"
		, "STATISTIC_TS"
		, "DEPENDENCY_WAIT_TIME"
		, "SUSPEND_TIME"
		, "SYNC_TIME"
		, "RESOURCE_TIME"
		, "JOBSERVER_TIME"
		, "RESTARTABLE_TIME"
		, "CHILD_WAIT_TIME"
		, "OP_SUSRES_TS"
		, "NPE_ID"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_masterId;
	public static SDMSIndex idx_submitTag;
	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_parentId;
	public static SDMSIndex idx_scopeId;
	public static SDMSIndex idx_state;
	public static SDMSIndex idx_masterId_seId_mergeMode;
	public static SDMSIndex idx_masterId_seId;
	public static SDMSIndex idx_fireSmeId_trId;
	public static SDMSIndex idx_masterId_parentId_seId_childTag;
	public static SDMSIndex idx_parentId_seId;
	public static SDMSIndex idx_parentId_trId;

	public SDMSSubmittedEntityTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "SubmittedEntity"));
		}
		table = (SDMSSubmittedEntityTable) this;
		SDMSSubmittedEntityTableGeneric.table = (SDMSSubmittedEntityTable) this;
		isVersioned = false;
		idx_masterId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "masterId");
		idx_submitTag = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "submitTag");
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seId");
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_parentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId");
		idx_scopeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "scopeId");
		idx_state = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "state");
		idx_masterId_seId_mergeMode = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "masterId_seId_mergeMode");
		idx_masterId_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "masterId_seId");
		idx_fireSmeId_trId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "fireSmeId_trId");
		idx_masterId_parentId_seId_childTag = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "masterId_parentId_seId_childTag");
		idx_parentId_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId_seId");
		idx_parentId_trId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "parentId_trId");
	}
	public SDMSSubmittedEntity create(SystemEnvironment env
	                                  ,Long p_accessKey
	                                  ,Long p_masterId
	                                  ,String p_submitTag
	                                  ,Integer p_unresolvedHandling
	                                  ,Long p_seId
	                                  ,String p_childTag
	                                  ,Long p_seVersion
	                                  ,Long p_ownerId
	                                  ,Long p_parentId
	                                  ,Long p_scopeId
	                                  ,Boolean p_isStatic
	                                  ,Boolean p_isDisabled
	                                  ,Integer p_oldState
	                                  ,Integer p_mergeMode
	                                  ,Integer p_state
	                                  ,Long p_jobEsdId
	                                  ,Integer p_jobEsdPref
	                                  ,Boolean p_jobIsFinal
	                                  ,Boolean p_jobIsRestartable
	                                  ,Long p_finalEsdId
	                                  ,Integer p_exitCode
	                                  ,String p_commandline
	                                  ,String p_rrCommandline
	                                  ,Integer p_rerunSeq
	                                  ,Boolean p_isReplaced
	                                  ,Boolean p_isCancelled
	                                  ,Long p_baseSmeId
	                                  ,Long p_reasonSmeId
	                                  ,Long p_fireSmeId
	                                  ,Long p_fireSeId
	                                  ,Long p_trId
	                                  ,Long p_trSdIdOld
	                                  ,Long p_trSdIdNew
	                                  ,Integer p_trSeq
	                                  ,String p_workdir
	                                  ,String p_logfile
	                                  ,String p_errlogfile
	                                  ,String p_pid
	                                  ,String p_extPid
	                                  ,String p_errorMsg
	                                  ,Long p_killId
	                                  ,Integer p_killExitCode
	                                  ,Integer p_isSuspended
	                                  ,Boolean p_isSuspendedLocal
	                                  ,Integer p_priority
	                                  ,Integer p_rawPriority
	                                  ,Integer p_nice
	                                  ,Integer p_npNice
	                                  ,Integer p_minPriority
	                                  ,Integer p_agingAmount
	                                  ,Integer p_parentSuspended
	                                  ,Integer p_childSuspended
	                                  ,Integer p_warnCount
	                                  ,Long p_warnLink
	                                  ,Long p_submitTs
	                                  ,Long p_resumeTs
	                                  ,Long p_syncTs
	                                  ,Long p_resourceTs
	                                  ,Long p_runnableTs
	                                  ,Long p_startTs
	                                  ,Long p_finishTs
	                                  ,Long p_finalTs
	                                  ,Integer p_cntSubmitted
	                                  ,Integer p_cntDependencyWait
	                                  ,Integer p_cntSynchronizeWait
	                                  ,Integer p_cntResourceWait
	                                  ,Integer p_cntRunnable
	                                  ,Integer p_cntStarting
	                                  ,Integer p_cntStarted
	                                  ,Integer p_cntRunning
	                                  ,Integer p_cntToKill
	                                  ,Integer p_cntKilled
	                                  ,Integer p_cntCancelled
	                                  ,Integer p_cntFinished
	                                  ,Integer p_cntFinal
	                                  ,Integer p_cntBrokenActive
	                                  ,Integer p_cntBrokenFinished
	                                  ,Integer p_cntError
	                                  ,Integer p_cntUnreachable
	                                  ,Integer p_cntRestartable
	                                  ,Integer p_cntWarn
	                                  ,Integer p_cntPending
	                                  ,Integer p_idleTs
	                                  ,Integer p_idleTime
	                                  ,Integer p_statisticTs
	                                  ,Integer p_dependencyWaitTime
	                                  ,Integer p_suspendTime
	                                  ,Integer p_syncTime
	                                  ,Integer p_resourceTime
	                                  ,Integer p_jobserverTime
	                                  ,Integer p_restartableTime
	                                  ,Integer p_childWaitTime
	                                  ,Long p_opSusresTs
	                                  ,Long p_npeId
	                                 )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "SubmittedEntity"));
		}
		validate(env
		         , p_accessKey
		         , p_masterId
		         , p_submitTag
		         , p_unresolvedHandling
		         , p_seId
		         , p_childTag
		         , p_seVersion
		         , p_ownerId
		         , p_parentId
		         , p_scopeId
		         , p_isStatic
		         , p_isDisabled
		         , p_oldState
		         , p_mergeMode
		         , p_state
		         , p_jobEsdId
		         , p_jobEsdPref
		         , p_jobIsFinal
		         , p_jobIsRestartable
		         , p_finalEsdId
		         , p_exitCode
		         , p_commandline
		         , p_rrCommandline
		         , p_rerunSeq
		         , p_isReplaced
		         , p_isCancelled
		         , p_baseSmeId
		         , p_reasonSmeId
		         , p_fireSmeId
		         , p_fireSeId
		         , p_trId
		         , p_trSdIdOld
		         , p_trSdIdNew
		         , p_trSeq
		         , p_workdir
		         , p_logfile
		         , p_errlogfile
		         , p_pid
		         , p_extPid
		         , p_errorMsg
		         , p_killId
		         , p_killExitCode
		         , p_isSuspended
		         , p_isSuspendedLocal
		         , p_priority
		         , p_rawPriority
		         , p_nice
		         , p_npNice
		         , p_minPriority
		         , p_agingAmount
		         , p_parentSuspended
		         , p_childSuspended
		         , p_warnCount
		         , p_warnLink
		         , p_submitTs
		         , p_resumeTs
		         , p_syncTs
		         , p_resourceTs
		         , p_runnableTs
		         , p_startTs
		         , p_finishTs
		         , p_finalTs
		         , p_cntSubmitted
		         , p_cntDependencyWait
		         , p_cntSynchronizeWait
		         , p_cntResourceWait
		         , p_cntRunnable
		         , p_cntStarting
		         , p_cntStarted
		         , p_cntRunning
		         , p_cntToKill
		         , p_cntKilled
		         , p_cntCancelled
		         , p_cntFinished
		         , p_cntFinal
		         , p_cntBrokenActive
		         , p_cntBrokenFinished
		         , p_cntError
		         , p_cntUnreachable
		         , p_cntRestartable
		         , p_cntWarn
		         , p_cntPending
		         , p_idleTs
		         , p_idleTime
		         , p_statisticTs
		         , p_dependencyWaitTime
		         , p_suspendTime
		         , p_syncTime
		         , p_resourceTime
		         , p_jobserverTime
		         , p_restartableTime
		         , p_childWaitTime
		         , p_opSusresTs
		         , p_npeId
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSSubmittedEntityGeneric o = new SDMSSubmittedEntityGeneric(env
		                , p_accessKey
		                , p_masterId
		                , p_submitTag
		                , p_unresolvedHandling
		                , p_seId
		                , p_childTag
		                , p_seVersion
		                , p_ownerId
		                , p_parentId
		                , p_scopeId
		                , p_isStatic
		                , p_isDisabled
		                , p_oldState
		                , p_mergeMode
		                , p_state
		                , p_jobEsdId
		                , p_jobEsdPref
		                , p_jobIsFinal
		                , p_jobIsRestartable
		                , p_finalEsdId
		                , p_exitCode
		                , p_commandline
		                , p_rrCommandline
		                , p_rerunSeq
		                , p_isReplaced
		                , p_isCancelled
		                , p_baseSmeId
		                , p_reasonSmeId
		                , p_fireSmeId
		                , p_fireSeId
		                , p_trId
		                , p_trSdIdOld
		                , p_trSdIdNew
		                , p_trSeq
		                , p_workdir
		                , p_logfile
		                , p_errlogfile
		                , p_pid
		                , p_extPid
		                , p_errorMsg
		                , p_killId
		                , p_killExitCode
		                , p_isSuspended
		                , p_isSuspendedLocal
		                , p_priority
		                , p_rawPriority
		                , p_nice
		                , p_npNice
		                , p_minPriority
		                , p_agingAmount
		                , p_parentSuspended
		                , p_childSuspended
		                , p_warnCount
		                , p_warnLink
		                , p_submitTs
		                , p_resumeTs
		                , p_syncTs
		                , p_resourceTs
		                , p_runnableTs
		                , p_startTs
		                , p_finishTs
		                , p_finalTs
		                , p_cntSubmitted
		                , p_cntDependencyWait
		                , p_cntSynchronizeWait
		                , p_cntResourceWait
		                , p_cntRunnable
		                , p_cntStarting
		                , p_cntStarted
		                , p_cntRunning
		                , p_cntToKill
		                , p_cntKilled
		                , p_cntCancelled
		                , p_cntFinished
		                , p_cntFinal
		                , p_cntBrokenActive
		                , p_cntBrokenFinished
		                , p_cntError
		                , p_cntUnreachable
		                , p_cntRestartable
		                , p_cntWarn
		                , p_cntPending
		                , p_idleTs
		                , p_idleTime
		                , p_statisticTs
		                , p_dependencyWaitTime
		                , p_suspendTime
		                , p_syncTime
		                , p_resourceTime
		                , p_jobserverTime
		                , p_restartableTime
		                , p_childWaitTime
		                , p_opSusresTs
		                , p_npeId
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                             );

		SDMSSubmittedEntity p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSubmittedEntity)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSubmittedEntity)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSSubmittedEntity p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_accessKey
	                        ,Long p_masterId
	                        ,String p_submitTag
	                        ,Integer p_unresolvedHandling
	                        ,Long p_seId
	                        ,String p_childTag
	                        ,Long p_seVersion
	                        ,Long p_ownerId
	                        ,Long p_parentId
	                        ,Long p_scopeId
	                        ,Boolean p_isStatic
	                        ,Boolean p_isDisabled
	                        ,Integer p_oldState
	                        ,Integer p_mergeMode
	                        ,Integer p_state
	                        ,Long p_jobEsdId
	                        ,Integer p_jobEsdPref
	                        ,Boolean p_jobIsFinal
	                        ,Boolean p_jobIsRestartable
	                        ,Long p_finalEsdId
	                        ,Integer p_exitCode
	                        ,String p_commandline
	                        ,String p_rrCommandline
	                        ,Integer p_rerunSeq
	                        ,Boolean p_isReplaced
	                        ,Boolean p_isCancelled
	                        ,Long p_baseSmeId
	                        ,Long p_reasonSmeId
	                        ,Long p_fireSmeId
	                        ,Long p_fireSeId
	                        ,Long p_trId
	                        ,Long p_trSdIdOld
	                        ,Long p_trSdIdNew
	                        ,Integer p_trSeq
	                        ,String p_workdir
	                        ,String p_logfile
	                        ,String p_errlogfile
	                        ,String p_pid
	                        ,String p_extPid
	                        ,String p_errorMsg
	                        ,Long p_killId
	                        ,Integer p_killExitCode
	                        ,Integer p_isSuspended
	                        ,Boolean p_isSuspendedLocal
	                        ,Integer p_priority
	                        ,Integer p_rawPriority
	                        ,Integer p_nice
	                        ,Integer p_npNice
	                        ,Integer p_minPriority
	                        ,Integer p_agingAmount
	                        ,Integer p_parentSuspended
	                        ,Integer p_childSuspended
	                        ,Integer p_warnCount
	                        ,Long p_warnLink
	                        ,Long p_submitTs
	                        ,Long p_resumeTs
	                        ,Long p_syncTs
	                        ,Long p_resourceTs
	                        ,Long p_runnableTs
	                        ,Long p_startTs
	                        ,Long p_finishTs
	                        ,Long p_finalTs
	                        ,Integer p_cntSubmitted
	                        ,Integer p_cntDependencyWait
	                        ,Integer p_cntSynchronizeWait
	                        ,Integer p_cntResourceWait
	                        ,Integer p_cntRunnable
	                        ,Integer p_cntStarting
	                        ,Integer p_cntStarted
	                        ,Integer p_cntRunning
	                        ,Integer p_cntToKill
	                        ,Integer p_cntKilled
	                        ,Integer p_cntCancelled
	                        ,Integer p_cntFinished
	                        ,Integer p_cntFinal
	                        ,Integer p_cntBrokenActive
	                        ,Integer p_cntBrokenFinished
	                        ,Integer p_cntError
	                        ,Integer p_cntUnreachable
	                        ,Integer p_cntRestartable
	                        ,Integer p_cntWarn
	                        ,Integer p_cntPending
	                        ,Integer p_idleTs
	                        ,Integer p_idleTime
	                        ,Integer p_statisticTs
	                        ,Integer p_dependencyWaitTime
	                        ,Integer p_suspendTime
	                        ,Integer p_syncTime
	                        ,Integer p_resourceTime
	                        ,Integer p_jobserverTime
	                        ,Integer p_restartableTime
	                        ,Integer p_childWaitTime
	                        ,Long p_opSusresTs
	                        ,Long p_npeId
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSSubmittedEntityGeneric.checkUnresolvedHandling(p_unresolvedHandling))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SubmittedEntity: $1 $2", "unresolvedHandling", p_unresolvedHandling));
		if (!SDMSSubmittedEntityGeneric.checkMergeMode(p_mergeMode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SubmittedEntity: $1 $2", "mergeMode", p_mergeMode));
		if (!SDMSSubmittedEntityGeneric.checkState(p_state))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SubmittedEntity: $1 $2", "state", p_state));
		if (!SDMSSubmittedEntityGeneric.checkIsSuspended(p_isSuspended))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SubmittedEntity: $1 $2", "isSuspended", p_isSuspended));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long accessKey;
		Long masterId;
		String submitTag;
		Integer unresolvedHandling;
		Long seId;
		String childTag;
		Long seVersion;
		Long ownerId;
		Long parentId;
		Long scopeId;
		Boolean isStatic;
		Boolean isDisabled;
		Integer oldState;
		Integer mergeMode;
		Integer state;
		Long jobEsdId;
		Integer jobEsdPref;
		Boolean jobIsFinal;
		Boolean jobIsRestartable;
		Long finalEsdId;
		Integer exitCode;
		String commandline;
		String rrCommandline;
		Integer rerunSeq;
		Boolean isReplaced;
		Boolean isCancelled;
		Long baseSmeId;
		Long reasonSmeId;
		Long fireSmeId;
		Long fireSeId;
		Long trId;
		Long trSdIdOld;
		Long trSdIdNew;
		Integer trSeq;
		String workdir;
		String logfile;
		String errlogfile;
		String pid;
		String extPid;
		String errorMsg;
		Long killId;
		Integer killExitCode;
		Integer isSuspended;
		Boolean isSuspendedLocal;
		Integer priority;
		Integer rawPriority;
		Integer nice;
		Integer npNice;
		Integer minPriority;
		Integer agingAmount;
		Integer parentSuspended;
		Integer childSuspended;
		Integer warnCount;
		Long warnLink;
		Long submitTs;
		Long resumeTs;
		Long syncTs;
		Long resourceTs;
		Long runnableTs;
		Long startTs;
		Long finishTs;
		Long finalTs;
		Integer cntSubmitted;
		Integer cntDependencyWait;
		Integer cntSynchronizeWait;
		Integer cntResourceWait;
		Integer cntRunnable;
		Integer cntStarting;
		Integer cntStarted;
		Integer cntRunning;
		Integer cntToKill;
		Integer cntKilled;
		Integer cntCancelled;
		Integer cntFinished;
		Integer cntFinal;
		Integer cntBrokenActive;
		Integer cntBrokenFinished;
		Integer cntError;
		Integer cntUnreachable;
		Integer cntRestartable;
		Integer cntWarn;
		Integer cntPending;
		Integer idleTs;
		Integer idleTime;
		Integer statisticTs;
		Integer dependencyWaitTime;
		Integer suspendTime;
		Integer syncTime;
		Integer resourceTime;
		Integer jobserverTime;
		Integer restartableTime;
		Integer childWaitTime;
		Long opSusresTs;
		Long npeId;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			accessKey = new Long (r.getLong(2));
			masterId = new Long (r.getLong(3));
			submitTag = r.getString(4);
			if (r.wasNull()) submitTag = null;
			unresolvedHandling = new Integer (r.getInt(5));
			if (r.wasNull()) unresolvedHandling = null;
			seId = new Long (r.getLong(6));
			childTag = r.getString(7);
			if (r.wasNull()) childTag = null;
			seVersion = new Long (r.getLong(8));
			ownerId = new Long (r.getLong(9));
			parentId = new Long (r.getLong(10));
			if (r.wasNull()) parentId = null;
			scopeId = new Long (r.getLong(11));
			if (r.wasNull()) scopeId = null;
			isStatic = new Boolean ((r.getInt(12) == 0 ? false : true));
			isDisabled = new Boolean ((r.getInt(13) == 0 ? false : true));
			oldState = new Integer (r.getInt(14));
			if (r.wasNull()) oldState = null;
			mergeMode = new Integer (r.getInt(15));
			state = new Integer (r.getInt(16));
			jobEsdId = new Long (r.getLong(17));
			if (r.wasNull()) jobEsdId = null;
			jobEsdPref = new Integer (r.getInt(18));
			if (r.wasNull()) jobEsdPref = null;
			jobIsFinal = new Boolean ((r.getInt(19) == 0 ? false : true));
			jobIsRestartable = new Boolean ((r.getInt(20) == 0 ? false : true));
			finalEsdId = new Long (r.getLong(21));
			if (r.wasNull()) finalEsdId = null;
			exitCode = new Integer (r.getInt(22));
			if (r.wasNull()) exitCode = null;
			commandline = r.getString(23);
			if (r.wasNull()) commandline = null;
			rrCommandline = r.getString(24);
			if (r.wasNull()) rrCommandline = null;
			rerunSeq = new Integer (r.getInt(25));
			isReplaced = new Boolean ((r.getInt(26) == 0 ? false : true));
			isCancelled = new Boolean ((r.getInt(27) == 0 ? false : true));
			if (r.wasNull()) isCancelled = null;
			baseSmeId = new Long (r.getLong(28));
			if (r.wasNull()) baseSmeId = null;
			reasonSmeId = new Long (r.getLong(29));
			if (r.wasNull()) reasonSmeId = null;
			fireSmeId = new Long (r.getLong(30));
			if (r.wasNull()) fireSmeId = null;
			fireSeId = new Long (r.getLong(31));
			if (r.wasNull()) fireSeId = null;
			trId = new Long (r.getLong(32));
			if (r.wasNull()) trId = null;
			trSdIdOld = new Long (r.getLong(33));
			if (r.wasNull()) trSdIdOld = null;
			trSdIdNew = new Long (r.getLong(34));
			if (r.wasNull()) trSdIdNew = null;
			trSeq = new Integer (r.getInt(35));
			workdir = r.getString(36);
			if (r.wasNull()) workdir = null;
			logfile = r.getString(37);
			if (r.wasNull()) logfile = null;
			errlogfile = r.getString(38);
			if (r.wasNull()) errlogfile = null;
			pid = r.getString(39);
			if (r.wasNull()) pid = null;
			extPid = r.getString(40);
			if (r.wasNull()) extPid = null;
			errorMsg = r.getString(41);
			if (r.wasNull()) errorMsg = null;
			killId = new Long (r.getLong(42));
			if (r.wasNull()) killId = null;
			killExitCode = new Integer (r.getInt(43));
			if (r.wasNull()) killExitCode = null;
			isSuspended = new Integer (r.getInt(44));
			isSuspendedLocal = new Boolean ((r.getInt(45) == 0 ? false : true));
			if (r.wasNull()) isSuspendedLocal = null;
			priority = new Integer (r.getInt(46));
			rawPriority = new Integer (r.getInt(47));
			nice = new Integer (r.getInt(48));
			npNice = new Integer (r.getInt(49));
			minPriority = new Integer (r.getInt(50));
			agingAmount = new Integer (r.getInt(51));
			parentSuspended = new Integer (r.getInt(52));
			childSuspended = new Integer (r.getInt(53));
			warnCount = new Integer (r.getInt(54));
			warnLink = new Long (r.getLong(55));
			if (r.wasNull()) warnLink = null;
			submitTs = new Long (r.getLong(56));
			resumeTs = new Long (r.getLong(57));
			if (r.wasNull()) resumeTs = null;
			syncTs = new Long (r.getLong(58));
			if (r.wasNull()) syncTs = null;
			resourceTs = new Long (r.getLong(59));
			if (r.wasNull()) resourceTs = null;
			runnableTs = new Long (r.getLong(60));
			if (r.wasNull()) runnableTs = null;
			startTs = new Long (r.getLong(61));
			if (r.wasNull()) startTs = null;
			finishTs = new Long (r.getLong(62));
			if (r.wasNull()) finishTs = null;
			finalTs = new Long (r.getLong(63));
			if (r.wasNull()) finalTs = null;
			cntSubmitted = new Integer (r.getInt(64));
			cntDependencyWait = new Integer (r.getInt(65));
			cntSynchronizeWait = new Integer (r.getInt(66));
			cntResourceWait = new Integer (r.getInt(67));
			cntRunnable = new Integer (r.getInt(68));
			cntStarting = new Integer (r.getInt(69));
			cntStarted = new Integer (r.getInt(70));
			cntRunning = new Integer (r.getInt(71));
			cntToKill = new Integer (r.getInt(72));
			cntKilled = new Integer (r.getInt(73));
			cntCancelled = new Integer (r.getInt(74));
			cntFinished = new Integer (r.getInt(75));
			cntFinal = new Integer (r.getInt(76));
			cntBrokenActive = new Integer (r.getInt(77));
			cntBrokenFinished = new Integer (r.getInt(78));
			cntError = new Integer (r.getInt(79));
			cntUnreachable = new Integer (r.getInt(80));
			cntRestartable = new Integer (r.getInt(81));
			cntWarn = new Integer (r.getInt(82));
			cntPending = new Integer (r.getInt(83));
			idleTs = new Integer (r.getInt(84));
			if (r.wasNull()) idleTs = null;
			idleTime = new Integer (r.getInt(85));
			if (r.wasNull()) idleTime = null;
			statisticTs = new Integer (r.getInt(86));
			if (r.wasNull()) statisticTs = null;
			dependencyWaitTime = new Integer (r.getInt(87));
			if (r.wasNull()) dependencyWaitTime = null;
			suspendTime = new Integer (r.getInt(88));
			if (r.wasNull()) suspendTime = null;
			syncTime = new Integer (r.getInt(89));
			if (r.wasNull()) syncTime = null;
			resourceTime = new Integer (r.getInt(90));
			if (r.wasNull()) resourceTime = null;
			jobserverTime = new Integer (r.getInt(91));
			if (r.wasNull()) jobserverTime = null;
			restartableTime = new Integer (r.getInt(92));
			if (r.wasNull()) restartableTime = null;
			childWaitTime = new Integer (r.getInt(93));
			if (r.wasNull()) childWaitTime = null;
			opSusresTs = new Long (r.getLong(94));
			if (r.wasNull()) opSusresTs = null;
			npeId = new Long (r.getLong(95));
			if (r.wasNull()) npeId = null;
			creatorUId = new Long (r.getLong(96));
			createTs = new Long (r.getLong(97));
			changerUId = new Long (r.getLong(98));
			changeTs = new Long (r.getLong(99));
			validFrom = 0;
			validTo = Long.MAX_VALUE;
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "SubmittedEntity: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSSubmittedEntityGeneric(id,
		                                      accessKey,
		                                      masterId,
		                                      submitTag,
		                                      unresolvedHandling,
		                                      seId,
		                                      childTag,
		                                      seVersion,
		                                      ownerId,
		                                      parentId,
		                                      scopeId,
		                                      isStatic,
		                                      isDisabled,
		                                      oldState,
		                                      mergeMode,
		                                      state,
		                                      jobEsdId,
		                                      jobEsdPref,
		                                      jobIsFinal,
		                                      jobIsRestartable,
		                                      finalEsdId,
		                                      exitCode,
		                                      commandline,
		                                      rrCommandline,
		                                      rerunSeq,
		                                      isReplaced,
		                                      isCancelled,
		                                      baseSmeId,
		                                      reasonSmeId,
		                                      fireSmeId,
		                                      fireSeId,
		                                      trId,
		                                      trSdIdOld,
		                                      trSdIdNew,
		                                      trSeq,
		                                      workdir,
		                                      logfile,
		                                      errlogfile,
		                                      pid,
		                                      extPid,
		                                      errorMsg,
		                                      killId,
		                                      killExitCode,
		                                      isSuspended,
		                                      isSuspendedLocal,
		                                      priority,
		                                      rawPriority,
		                                      nice,
		                                      npNice,
		                                      minPriority,
		                                      agingAmount,
		                                      parentSuspended,
		                                      childSuspended,
		                                      warnCount,
		                                      warnLink,
		                                      submitTs,
		                                      resumeTs,
		                                      syncTs,
		                                      resourceTs,
		                                      runnableTs,
		                                      startTs,
		                                      finishTs,
		                                      finalTs,
		                                      cntSubmitted,
		                                      cntDependencyWait,
		                                      cntSynchronizeWait,
		                                      cntResourceWait,
		                                      cntRunnable,
		                                      cntStarting,
		                                      cntStarted,
		                                      cntRunning,
		                                      cntToKill,
		                                      cntKilled,
		                                      cntCancelled,
		                                      cntFinished,
		                                      cntFinal,
		                                      cntBrokenActive,
		                                      cntBrokenFinished,
		                                      cntError,
		                                      cntUnreachable,
		                                      cntRestartable,
		                                      cntWarn,
		                                      cntPending,
		                                      idleTs,
		                                      idleTime,
		                                      statisticTs,
		                                      dependencyWaitTime,
		                                      suspendTime,
		                                      syncTime,
		                                      resourceTime,
		                                      jobserverTime,
		                                      restartableTime,
		                                      childWaitTime,
		                                      opSusresTs,
		                                      npeId,
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
		                                   tableName() + ".ID" +
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
		                                   "  FROM " + tableName() + ", " +
		                                   "       SME2LOAD " +
		                                   " WHERE " + tableName() + ".ID = SME2LOAD.ID"
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
		ok =  idx_masterId.check(((SDMSSubmittedEntityGeneric) o).masterId, o);
		out = out + "idx_masterId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_submitTag.check(((SDMSSubmittedEntityGeneric) o).submitTag, o);
		out = out + "idx_submitTag: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_seId.check(((SDMSSubmittedEntityGeneric) o).seId, o);
		out = out + "idx_seId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_ownerId.check(((SDMSSubmittedEntityGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_parentId.check(((SDMSSubmittedEntityGeneric) o).parentId, o);
		out = out + "idx_parentId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_scopeId.check(((SDMSSubmittedEntityGeneric) o).scopeId, o);
		out = out + "idx_scopeId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_state.check(((SDMSSubmittedEntityGeneric) o).state, o);
		out = out + "idx_state: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).mergeMode);
		ok =  idx_masterId_seId_mergeMode.check(k, o);
		out = out + "idx_masterId_seId_mergeMode: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		ok =  idx_masterId_seId.check(k, o);
		out = out + "idx_masterId_seId: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).fireSmeId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		ok =  idx_fireSmeId_trId.check(k, o);
		out = out + "idx_fireSmeId_trId: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).childTag);
		ok =  idx_masterId_parentId_seId_childTag.check(k, o);
		out = out + "idx_masterId_parentId_seId_childTag: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		ok =  idx_parentId_seId.check(k, o);
		out = out + "idx_parentId_seId: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		ok =  idx_parentId_trId.check(k, o);
		out = out + "idx_parentId_trId: " + (ok ? "ok" : "missing") + "\n";
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
		idx_masterId.put(env, ((SDMSSubmittedEntityGeneric) o).masterId, o, ((1 & indexMember) != 0));
		idx_submitTag.put(env, ((SDMSSubmittedEntityGeneric) o).submitTag, o, ((2 & indexMember) != 0));
		idx_seId.put(env, ((SDMSSubmittedEntityGeneric) o).seId, o, ((4 & indexMember) != 0));
		idx_ownerId.put(env, ((SDMSSubmittedEntityGeneric) o).ownerId, o, ((8 & indexMember) != 0));
		idx_parentId.put(env, ((SDMSSubmittedEntityGeneric) o).parentId, o, ((16 & indexMember) != 0));
		idx_scopeId.put(env, ((SDMSSubmittedEntityGeneric) o).scopeId, o, ((32 & indexMember) != 0));
		idx_state.put(env, ((SDMSSubmittedEntityGeneric) o).state, o, ((64 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).mergeMode);
		idx_masterId_seId_mergeMode.put(env, k, o, ((128 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		idx_masterId_seId.put(env, k, o, ((256 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).fireSmeId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		idx_fireSmeId_trId.put(env, k, o, ((512 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).childTag);
		idx_masterId_parentId_seId_childTag.put(env, k, o, ((1024 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		idx_parentId_seId.put(env, k, o, ((2048 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		idx_parentId_trId.put(env, k, o, ((4096 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_masterId.remove(env, ((SDMSSubmittedEntityGeneric) o).masterId, o);
		idx_submitTag.remove(env, ((SDMSSubmittedEntityGeneric) o).submitTag, o);
		idx_seId.remove(env, ((SDMSSubmittedEntityGeneric) o).seId, o);
		idx_ownerId.remove(env, ((SDMSSubmittedEntityGeneric) o).ownerId, o);
		idx_parentId.remove(env, ((SDMSSubmittedEntityGeneric) o).parentId, o);
		idx_scopeId.remove(env, ((SDMSSubmittedEntityGeneric) o).scopeId, o);
		idx_state.remove(env, ((SDMSSubmittedEntityGeneric) o).state, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).mergeMode);
		idx_masterId_seId_mergeMode.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		idx_masterId_seId.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).fireSmeId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		idx_fireSmeId_trId.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).childTag);
		idx_masterId_parentId_seId_childTag.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		idx_parentId_seId.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		idx_parentId_trId.remove(env, k, o);
	}

	public static SDMSSubmittedEntity getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSubmittedEntity) table.get(env, id);
	}

	public static SDMSSubmittedEntity getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSubmittedEntity) table.getForUpdate(env, id);
	}

	public static SDMSSubmittedEntity getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSSubmittedEntity) table.get(env, id, version);
	}

	public static SDMSSubmittedEntity idx_masterId_parentId_seId_childTag_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSubmittedEntity)  SDMSSubmittedEntityTableGeneric.idx_masterId_parentId_seId_childTag.getUnique(env, key);
	}

	public static SDMSSubmittedEntity idx_masterId_parentId_seId_childTag_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSubmittedEntity)  SDMSSubmittedEntityTableGeneric.idx_masterId_parentId_seId_childTag.getUniqueForUpdate(env, key);
	}

	public static SDMSSubmittedEntity idx_masterId_parentId_seId_childTag_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSSubmittedEntity)  SDMSSubmittedEntityTableGeneric.idx_masterId_parentId_seId_childTag.getUnique(env, key, version);
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
