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

public class SDMSSubmittedEntityTable extends SDMSSubmittedEntityTableGeneric
{

	public final static String __version = "@(#) $Id: SDMSSubmittedEntityTable.java,v 2.22.2.2 2013/03/22 14:48:03 ronald Exp $";

	public SDMSSubmittedEntityTable(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
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
	                                  ,Long p_originSmeId
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
	                                  ,Integer p_priority
	                                  ,Integer p_raw_priority
	                                  ,Integer p_nice
	                                  ,Integer p_np_nice
	                                  ,Integer p_minEP
	                                  ,Integer p_agingAmount
	                                  ,Integer p_parentSuspended
	                                  ,Integer p_childSuspended
	                                  ,Integer p_warnCount
	                                  ,Long p_warnLink
	                                  ,Integer p_approvalMode
	                                  ,Integer p_childApprovalMode
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
	                                  ,Integer p_dwEndTs
	                                  ,Integer p_idleTs
	                                  ,Integer p_idleTime
	                                  ,Integer p_susresTs
	                                  ,Integer p_suspendTime
	                                  ,Integer p_syncTime
	                                  ,Integer p_resourceTime
	                                  ,Integer p_jobserverTime
	                                  ,Integer p_restartableTime
	                                  ,Integer p_childWaitTime
	                                  ,Long	 p_opSusresTs
	                                  ,Long	 p_npeId
	                                  ,String  p_timeZone
	                                 )
	throws SDMSException
	{
		SDMSSubmittedEntity sme = super.create(env
		                                       ,p_accessKey
		                                       ,p_masterId
		                                       ,p_submitTag
		                                       ,p_unresolvedHandling
		                                       ,p_seId
		                                       ,p_childTag
		                                       ,p_seVersion
		                                       ,p_ownerId
		                                       ,p_parentId
		                                       ,p_scopeId
		                                       ,p_isStatic
		                                       ,p_isDisabled
		                                       ,p_oldState
		                                       ,p_mergeMode
		                                       ,p_state
		                                       ,p_jobEsdId
		                                       ,p_jobEsdPref
		                                       ,p_jobIsFinal
		                                       ,p_jobIsRestartable
		                                       ,p_finalEsdId
		                                       ,p_exitCode
		                                       ,p_commandline
		                                       ,p_rrCommandline
		                                       ,p_rerunSeq
		                                       ,p_isReplaced
		                                       ,p_isCancelled
		                                       ,p_originSmeId
		                                       ,p_reasonSmeId
		                                       ,p_fireSmeId
		                                       ,p_fireSeId
		                                       ,p_trId
		                                       ,p_trSdIdOld
		                                       ,p_trSdIdNew
		                                       ,p_trSeq
		                                       ,p_workdir
		                                       ,p_logfile
		                                       ,p_errlogfile
		                                       ,p_pid
		                                       ,p_extPid
		                                       ,p_errorMsg
		                                       ,p_killId
		                                       ,p_killExitCode
		                                       ,p_isSuspended
		                                       ,Boolean.FALSE
		                                       ,p_priority
		                                       ,p_raw_priority
		                                       ,p_nice
		                                       ,p_np_nice
		                                       ,p_minEP
		                                       ,p_agingAmount
		                                       ,p_parentSuspended
		                                       ,p_childSuspended
		                                       ,p_warnCount
		                                       ,p_warnLink
		                                       ,p_approvalMode
		                                       ,p_childApprovalMode
		                                       ,p_submitTs
		                                       ,p_resumeTs
		                                       ,p_syncTs
		                                       ,p_resourceTs
		                                       ,p_runnableTs
		                                       ,p_startTs
		                                       ,p_finishTs
		                                       ,p_finalTs
		                                       ,p_cntSubmitted
		                                       ,p_cntDependencyWait
		                                       ,p_cntSynchronizeWait
		                                       ,p_cntResourceWait
		                                       ,p_cntRunnable
		                                       ,p_cntStarting
		                                       ,p_cntStarted
		                                       ,p_cntRunning
		                                       ,p_cntToKill
		                                       ,p_cntKilled
		                                       ,p_cntCancelled
		                                       ,p_cntFinished
		                                       ,p_cntFinal
		                                       ,p_cntBrokenActive
		                                       ,p_cntBrokenFinished
		                                       ,p_cntError
		                                       ,p_cntUnreachable
		                                       ,p_cntRestartable
		                                       ,p_cntWarn
		                                       ,p_cntPending
		                                       ,p_dwEndTs
		                                       ,p_idleTs
		                                       ,p_idleTime
		                                       ,p_susresTs
		                                       ,p_suspendTime
		                                       ,p_syncTime
		                                       ,p_resourceTime
		                                       ,p_jobserverTime
		                                       ,p_restartableTime
		                                       ,p_childWaitTime
		                                       ,p_opSusresTs
		                                       ,p_npeId
		                                       ,p_timeZone
		                                      );

		final Long smeId = sme.getId(env);
		long seVersion = p_seVersion.longValue();
		if (p_masterId.longValue() == 0) {
			env.seVersionList.add(env, seVersion);
			sme.setMasterId(env, smeId);
			p_masterId = smeId;
		}

		int cnt = env.tx.smeCtr.intValue() + 1;
		env.tx.smeCtr = Integer.valueOf(cnt);
		Vector v = SDMSResourceTemplateTable.idx_seId.getVector(env, p_seId, seVersion);
		final java.util.Date dts = new java.util.Date();
		final Long ts = Long.valueOf (dts.getTime());
		for(int i = 0; i < v.size(); i++) {
			final SDMSResourceTemplate rt = (SDMSResourceTemplate) v.get(i);
			final Long nrId = rt.getNrId(env);
			final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(env, nrId);
			final SDMSResource r = SDMSResourceTable.table.create(env, rt.getNrId(env), smeId, p_masterId, p_ownerId, null, null,
			                       null, rt.getRsdId(env), ts, rt.getAmount(env), rt.getRequestableAmount(env),
			                       rt.getAmount(env), rt.getAmount(env), rt.getIsOnline(env), nr.getFactor(env),
			                       null, null, Integer.valueOf(10), SDMSConstants.fZERO, SDMSConstants.fZERO,
			                       SDMSConstants.fZERO, SDMSConstants.fZERO, SDMSConstants.lZERO, SDMSConstants.lZERO);
			Vector tv = SDMSTemplateVariableTable.idx_rtId.getVector(env, rt.getId(env), seVersion);
			for(int j = 0; j < tv.size(); j++) {
				final SDMSTemplateVariable t = (SDMSTemplateVariable) tv.get(j);
				SDMSResourceVariableTable.table.create(env, t.getPdId(env), r.getId(env), t.getValue(env));
			}
		}
		if (p_resumeTs != null) {
			env.tt.addToJobsToResume(env, sme.getId(env));
		}

		return sme;
	}

	public SDMSSubmittedEntity createErrorMaster(SystemEnvironment env
	                ,Long p_seId
	                ,Long p_seVersion
	                ,Long p_ownerId
	                ,Long p_jobEsdId
	                ,Long p_finalEsdId
	                ,String p_errorMsg
	                ,Integer p_priority
	                ,Integer p_nice
	                ,Long p_submitTs
	                                            )
	throws SDMSException
	{
		SDMSSubmittedEntity sme = super.create(env
		                                       ,env.randomLong()
		                                       ,SDMSConstants.lZERO
		                                       ,null
		                                       ,SDMSConstants.DD_ERROR
		                                       ,p_seId
		                                       ,null
		                                       ,p_seVersion
		                                       ,p_ownerId
		                                       ,null
		                                       ,null
		                                       ,Boolean.TRUE
		                                       ,Boolean.FALSE
		                                       ,null
		                                       ,SDMSConstants.SH_FAILURE
		                                       ,SDMSConstants.SME_ERROR
		                                       ,p_jobEsdId
		                                       ,SDMSConstants.iZERO
		                                       ,Boolean.FALSE
		                                       ,Boolean.FALSE
		                                       ,p_finalEsdId
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,SDMSConstants.iZERO
		                                       ,Boolean.FALSE
		                                       ,Boolean.FALSE
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,SDMSConstants.iZERO
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,p_errorMsg
		                                       ,null
		                                       ,null
		                                       ,SDMSConstants.SME_NOSUSPEND
		                                       ,Boolean.FALSE
		                                       ,p_priority
		                                       ,p_priority
		                                       ,p_nice
		                                       ,SDMSConstants.iZERO
		                                       ,SDMSConstants.iZERO
		                                       ,SDMSConstants.iZERO
		                                       ,SDMSConstants.iZERO
		                                       ,SDMSConstants.iZERO
		                                       ,SDMSConstants.iZERO
		                                       ,null
		                                       ,SDMSConstants.iZERO
		                                       ,SDMSConstants.iZERO
		                                       ,p_submitTs
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       ,null
		                                       , SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO,
		                                       SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO,
		                                       SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO,
		                                       SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO, SDMSConstants.iZERO,
		                                       SDMSConstants.iZERO
		                                       ,null,null,SDMSConstants.iZERO,null,SDMSConstants.iZERO
		                                       ,SDMSConstants.iZERO,SDMSConstants.iZERO,SDMSConstants.iZERO,SDMSConstants.iZERO,SDMSConstants.iZERO
		                                       , null, null
		                                       , null
		                                      );

		final Long smeId = sme.getId(env);
		long seVersion = p_seVersion.longValue();
		env.seVersionList.add(env, seVersion);
		sme.setMasterId(env, smeId);

		int cnt = env.tx.smeCtr.intValue() + 1;
		env.tx.smeCtr = Integer.valueOf(cnt);

		return sme;
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		SDMSSubmittedEntityGeneric smeg = (SDMSSubmittedEntityGeneric)(super.rowToObject(env, r));
		if (smeg.getId(env).longValue() == smeg.getMasterId(env).longValue()) {
			env.seVersionList.add(env, smeg.getSeVersion(env).longValue());
		}

		return smeg;
	}
}
