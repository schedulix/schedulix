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
		idx_masterId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_submitTag = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_parentId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_scopeId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_state = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_masterId_seId_mergeMode = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_masterId_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_fireSmeId_trId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_masterId_parentId_seId_childTag = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
		idx_parentId_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
		idx_parentId_trId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned);
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
	                                  ,Boolean p_isSuspended
	                                  ,Boolean p_isSuspendedLocal
	                                  ,Integer p_priority
	                                  ,Integer p_nice
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
		         , p_nice
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
		                , p_nice
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
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                             );

		SDMSSubmittedEntity p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSubmittedEntity)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSubmittedEntity)(o.toProxy());
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
	                        ,Boolean p_isSuspended
	                        ,Boolean p_isSuspendedLocal
	                        ,Integer p_priority
	                        ,Integer p_nice
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
		Boolean isSuspended;
		Boolean isSuspendedLocal;
		Integer priority;
		Integer nice;
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
			mergeMode = new Integer (r.getInt(13));
			state = new Integer (r.getInt(14));
			jobEsdId = new Long (r.getLong(15));
			if (r.wasNull()) jobEsdId = null;
			jobEsdPref = new Integer (r.getInt(16));
			if (r.wasNull()) jobEsdPref = null;
			jobIsFinal = new Boolean ((r.getInt(17) == 0 ? false : true));
			jobIsRestartable = new Boolean ((r.getInt(18) == 0 ? false : true));
			finalEsdId = new Long (r.getLong(19));
			if (r.wasNull()) finalEsdId = null;
			exitCode = new Integer (r.getInt(20));
			if (r.wasNull()) exitCode = null;
			commandline = r.getString(21);
			if (r.wasNull()) commandline = null;
			rrCommandline = r.getString(22);
			if (r.wasNull()) rrCommandline = null;
			rerunSeq = new Integer (r.getInt(23));
			isReplaced = new Boolean ((r.getInt(24) == 0 ? false : true));
			isCancelled = new Boolean ((r.getInt(25) == 0 ? false : true));
			if (r.wasNull()) isCancelled = null;
			baseSmeId = new Long (r.getLong(26));
			if (r.wasNull()) baseSmeId = null;
			reasonSmeId = new Long (r.getLong(27));
			if (r.wasNull()) reasonSmeId = null;
			fireSmeId = new Long (r.getLong(28));
			if (r.wasNull()) fireSmeId = null;
			fireSeId = new Long (r.getLong(29));
			if (r.wasNull()) fireSeId = null;
			trId = new Long (r.getLong(30));
			if (r.wasNull()) trId = null;
			trSdIdOld = new Long (r.getLong(31));
			if (r.wasNull()) trSdIdOld = null;
			trSdIdNew = new Long (r.getLong(32));
			if (r.wasNull()) trSdIdNew = null;
			trSeq = new Integer (r.getInt(33));
			workdir = r.getString(34);
			if (r.wasNull()) workdir = null;
			logfile = r.getString(35);
			if (r.wasNull()) logfile = null;
			errlogfile = r.getString(36);
			if (r.wasNull()) errlogfile = null;
			pid = r.getString(37);
			if (r.wasNull()) pid = null;
			extPid = r.getString(38);
			if (r.wasNull()) extPid = null;
			errorMsg = r.getString(39);
			if (r.wasNull()) errorMsg = null;
			killId = new Long (r.getLong(40));
			if (r.wasNull()) killId = null;
			killExitCode = new Integer (r.getInt(41));
			if (r.wasNull()) killExitCode = null;
			isSuspended = new Boolean ((r.getInt(42) == 0 ? false : true));
			isSuspendedLocal = new Boolean ((r.getInt(43) == 0 ? false : true));
			if (r.wasNull()) isSuspendedLocal = null;
			priority = new Integer (r.getInt(44));
			nice = new Integer (r.getInt(45));
			minPriority = new Integer (r.getInt(46));
			agingAmount = new Integer (r.getInt(47));
			parentSuspended = new Integer (r.getInt(48));
			childSuspended = new Integer (r.getInt(49));
			warnCount = new Integer (r.getInt(50));
			warnLink = new Long (r.getLong(51));
			if (r.wasNull()) warnLink = null;
			submitTs = new Long (r.getLong(52));
			resumeTs = new Long (r.getLong(53));
			if (r.wasNull()) resumeTs = null;
			syncTs = new Long (r.getLong(54));
			if (r.wasNull()) syncTs = null;
			resourceTs = new Long (r.getLong(55));
			if (r.wasNull()) resourceTs = null;
			runnableTs = new Long (r.getLong(56));
			if (r.wasNull()) runnableTs = null;
			startTs = new Long (r.getLong(57));
			if (r.wasNull()) startTs = null;
			finishTs = new Long (r.getLong(58));
			if (r.wasNull()) finishTs = null;
			finalTs = new Long (r.getLong(59));
			if (r.wasNull()) finalTs = null;
			cntSubmitted = new Integer (r.getInt(60));
			cntDependencyWait = new Integer (r.getInt(61));
			cntSynchronizeWait = new Integer (r.getInt(62));
			cntResourceWait = new Integer (r.getInt(63));
			cntRunnable = new Integer (r.getInt(64));
			cntStarting = new Integer (r.getInt(65));
			cntStarted = new Integer (r.getInt(66));
			cntRunning = new Integer (r.getInt(67));
			cntToKill = new Integer (r.getInt(68));
			cntKilled = new Integer (r.getInt(69));
			cntCancelled = new Integer (r.getInt(70));
			cntFinished = new Integer (r.getInt(71));
			cntFinal = new Integer (r.getInt(72));
			cntBrokenActive = new Integer (r.getInt(73));
			cntBrokenFinished = new Integer (r.getInt(74));
			cntError = new Integer (r.getInt(75));
			cntUnreachable = new Integer (r.getInt(76));
			cntRestartable = new Integer (r.getInt(77));
			cntWarn = new Integer (r.getInt(78));
			cntPending = new Integer (r.getInt(79));
			creatorUId = new Long (r.getLong(80));
			createTs = new Long (r.getLong(81));
			changerUId = new Long (r.getLong(82));
			changeTs = new Long (r.getLong(83));
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
		                                      nice,
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

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_masterId.put(env, ((SDMSSubmittedEntityGeneric) o).masterId, o);
		idx_submitTag.put(env, ((SDMSSubmittedEntityGeneric) o).submitTag, o);
		idx_seId.put(env, ((SDMSSubmittedEntityGeneric) o).seId, o);
		idx_ownerId.put(env, ((SDMSSubmittedEntityGeneric) o).ownerId, o);
		idx_parentId.put(env, ((SDMSSubmittedEntityGeneric) o).parentId, o);
		idx_scopeId.put(env, ((SDMSSubmittedEntityGeneric) o).scopeId, o);
		idx_state.put(env, ((SDMSSubmittedEntityGeneric) o).state, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).mergeMode);
		idx_masterId_seId_mergeMode.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		idx_masterId_seId.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).fireSmeId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		idx_fireSmeId_trId.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).masterId);
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		k.add(((SDMSSubmittedEntityGeneric) o).childTag);
		idx_masterId_parentId_seId_childTag.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).seId);
		idx_parentId_seId.put(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSubmittedEntityGeneric) o).parentId);
		k.add(((SDMSSubmittedEntityGeneric) o).trId);
		idx_parentId_trId.put(env, k, o);
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

	public static SDMSSubmittedEntity idx_masterId_parentId_seId_childTag_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSSubmittedEntity)  SDMSSubmittedEntityTableGeneric.idx_masterId_parentId_seId_childTag.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
