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
package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.jobserver.Config;

public class ShowSubmitted extends Node
{

	private static final int REQUIRED   = 1;
	private static final int DEPENDENTS = 2;

	private final static String AM_APPROVE = "APPROVE";
	private final static String AM_REVIEW = "REVIEW";
	private final static String AM_NO = "NO";

	final Long jobId;
	long actVersion;
	Vector path;
	String tag;
	final WithHash with;
	static final int[] auditSortCols = {2, 3, 0};

	public ShowSubmitted(Long id)
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		jobId = id;
		tag = null;
		with = null;
	}

	public ShowSubmitted(String t)
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		jobId = null;
		tag = t;
		with = null;
	}

	public ShowSubmitted(Long id, WithHash w)
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		jobId = id;
		tag = null;
		with = w;
	}

	public ShowSubmitted(String t, WithHash w)
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		jobId = null;
		tag = t;
		with = w;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;

		SDMSSubmittedEntity sme;
		if(jobId != null) {
			sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobId);
		} else {
			Vector v = SDMSSubmittedEntityTable.idx_submitTag.getVector(sysEnv, tag);
			if(v.size() > 0)
				sme = (SDMSSubmittedEntity) v.get(0);
			else
				throw new NotFoundException(new SDMSMessage(sysEnv, "03406032050", "Job with submittag $1 not found", tag));
		}

		if(!sme.checkPrivileges(sysEnv, SDMSPrivilege.MONITOR))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411726", "Insufficient privileges"));
		actVersion = sme.getSeVersion(sysEnv).longValue();

		d_container = showSchedulingEntity(sysEnv, sme);

		result.setOutputContainer(d_container);

		result.setFeedback( new SDMSMessage(sysEnv, "03205081158", "Job shown"));

	}

	private void setApprovalValue(Vector data, int approve, int allFlags, int approvalFlag)
	{
		if ((approve & allFlags) != 0) {
			if ((approve & approvalFlag) != 0)
				data.add(AM_APPROVE);
			else
				data.add(AM_REVIEW);
		} else
			data.add(AM_NO);

	}

	private SDMSOutputContainer showSchedulingEntity(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		SDMSOutputContainer d_container;
		Vector desc = new Vector();
		Vector data = new Vector();

		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		String strType = se.getTypeAsString(sysEnv);

		desc.add("ID");
		desc.add("SE_NAME");
		desc.add("SE_OWNER");
		desc.add("SE_TYPE");
		desc.add("SE_RUN_PROGRAM");
		desc.add("SE_RERUN_PROGRAM");
		desc.add("SE_KILL_PROGRAM");
		desc.add("SE_WORKDIR");
		desc.add("SE_LOGFILE");
		desc.add("SE_TRUNC_LOG");
		desc.add("SE_ERRLOGFILE");
		desc.add("SE_TRUNC_ERRLOG");
		desc.add("SE_EXPECTED_RUNTIME");
		desc.add("SE_PRIORITY");
		desc.add("SE_SUBMIT_SUSPENDED");
		desc.add("SE_MASTER_SUBMITTABLE");
		desc.add("SE_DEPENDENCY_MODE");
		desc.add("SE_ESP_NAME");
		desc.add("SE_ESM_NAME");
		desc.add("SE_ENV_NAME");
		desc.add("SE_FP_NAME");

		desc.add("MASTER_ID");
		desc.add("TIME_ZONE");
		desc.add("CHILD_TAG");
		desc.add("SE_VERSION");
		desc.add("OWNER");
		desc.add("PARENT_ID");
		desc.add("SCOPE_ID");
		desc.add("HTTPHOST");
		desc.add("HTTPPORT");
		desc.add("IS_STATIC");
		desc.add("MERGE_MODE");
		desc.add("STATE");
		desc.add("IS_DISABLED");
		desc.add("IS_PARENT_DISABLED");
		desc.add("IS_CANCELLED");
		desc.add("JOB_ESD_ID");
		desc.add("JOB_ESD_PREF");
		desc.add("JOB_IS_FINAL");
		desc.add("JOB_IS_RESTARTABLE");
		desc.add("FINAL_ESD_ID");
		desc.add("EXIT_CODE");
		desc.add("COMMANDLINE");
		desc.add("RR_COMMANDLINE");
		desc.add("WORKDIR");
		desc.add("LOGFILE");
		desc.add("ERRLOGFILE");
		desc.add("PID");
		desc.add("EXT_PID");
		desc.add("ERROR_MSG");
		desc.add("KILL_ID");
		desc.add("KILL_EXIT_CODE");
		desc.add("IS_SUSPENDED");
		desc.add("IS_SUSPENDED_LOCAL");
		desc.add("PRIORITY");
		desc.add("RAW_PRIORITY");
		desc.add("NICEVALUE");
		desc.add("NP_NICEVALUE");
		desc.add("MIN_PRIORITY");
		desc.add("AGING_AMOUNT");
		desc.add("AGING_BASE");
		desc.add("DYNAMIC_PRIORITY");
		desc.add("PARENT_SUSPENDED");
		desc.add("CANCEL_APPROVAL");
		desc.add("RERUN_APPROVAL");
		desc.add("ENABLE_APPROVAL");
		desc.add("SET_STATE_APPROVAL");
		desc.add("IGN_DEPENDENCY_APPROVAL");
		desc.add("IGN_RESOURCE_APPROVAL");
		desc.add("CLONE_APPROVAL");
		desc.add("SUSPEND_APPROVAL");
		desc.add("CLEAR_WARN_APPROVAL");
		desc.add("PRIORITY_APPROVAL");
		desc.add("EDIT_PARAMETER_APPROVAL");
		desc.add("KILL_APPROVAL");
		desc.add("SET_JOB_STATE_APPROVAL");
		desc.add("SUBMIT_TS");
		desc.add("RESUME_TS");
		desc.add("SYNC_TS");
		desc.add("RESOURCE_TS");
		desc.add("RUNNABLE_TS");
		desc.add("START_TS");
		desc.add("FINISH_TS");
		desc.add("FINAL_TS");
		desc.add("CNT_SUBMITTED");
		desc.add("CNT_DEPENDENCY_WAIT");
		desc.add("CNT_SYNCHRONIZE_WAIT");
		desc.add("CNT_RESOURCE_WAIT");
		desc.add("CNT_RUNNABLE");
		desc.add("CNT_STARTING");
		desc.add("CNT_STARTED");
		desc.add("CNT_RUNNING");
		desc.add("CNT_TO_KILL");
		desc.add("CNT_KILLED");
		desc.add("CNT_CANCELLED");
		desc.add("CNT_FINISHED");
		desc.add("CNT_FINAL");
		desc.add("CNT_BROKEN_ACTIVE");
		desc.add("CNT_BROKEN_FINISHED");
		desc.add("CNT_ERROR");
		desc.add("CNT_RESTARTABLE");
		desc.add("CNT_UNREACHABLE");
		desc.add("CNT_WARN");
		desc.add("WARN_COUNT");

		desc.add("IDLE_TIME");
		desc.add("DEPENDENCY_WAIT_TIME");
		desc.add("SUSPEND_TIME");
		desc.add("SYNC_TIME");
		desc.add("RESOURCE_TIME");
		desc.add("JOBSERVER_TIME");
		desc.add("RESTARTABLE_TIME");
		desc.add("CHILD_WAIT_TIME");
		desc.add("PROCESS_TIME");
		desc.add("ACTIVE_TIME");
		desc.add("IDLE_PCT");

		desc.add("CHILDREN");
		desc.add("PARENTS");
		desc.add("PARAMETER");
		desc.add("REQUIRED_JOBS");
		desc.add("DEPENDENT_JOBS");
		desc.add("REQUIRED_RESOURCES");
		desc.add("SUBMIT_PATH");
		desc.add("IS_REPLACED");
		desc.add("TIMEOUT_AMOUNT");
		desc.add("TIMEOUT_BASE");
		desc.add("TIMEOUT_STATE");
		desc.add("RERUN_SEQ");
		desc.add("AUDIT_TRAIL");
		desc.add("CHILD_SUSPENDED");
		desc.add("CNT_PENDING");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");
		desc.add("SE_PRIVS");
		desc.add("SUBMITTAG");
		desc.add("APPROVAL_PENDING");
		desc.add("UNRESOLVED_HANDLING");
		desc.add("DEFINED_RESOURCES");
		if (strType.equals("JOB"))
			desc.add("RUNS");

		Long smeId = sme.getId(sysEnv);
		Long espId;
		SDMSExitStateProfile esp;
		Long esmpId;
		SDMSExitStateMappingProfile esmp;
		Long neId;
		SDMSNamedEnvironment ne;
		Long fpId;
		SDMSFootprint fp;
		SDMSGroup group;
		Long scopeId;
		SDMSScope scope;
		Long esdId;
		SDMSExitStateDefinition esd;
		Date d = new Date();
		Long ts;

		data.add(smeId);
		data.add(se.pathVector(sysEnv, actVersion));
		group = SDMSGroupTable.getObject(sysEnv, se.getOwnerId(sysEnv));
		data.add(group.getName(sysEnv));
		data.add(strType);
		data.add(se.getRunProgram(sysEnv));
		data.add(se.getRerunProgram(sysEnv));
		data.add(se.getKillProgram(sysEnv));
		data.add(se.getWorkdir(sysEnv));
		data.add(se.getLogfile(sysEnv));
		data.add(se.getTruncLog(sysEnv));
		data.add(se.getErrlogfile(sysEnv));
		data.add(se.getTruncErrlog(sysEnv));
		data.add(se.getExpectedRuntime(sysEnv));
		data.add(se.getPriority(sysEnv));
		data.add(se.getSubmitSuspended(sysEnv));
		data.add(se.getMasterSubmittable(sysEnv));
		data.add(se.getDependencyOperationAsString(sysEnv));
		espId = se.getEspId(sysEnv);
		esp = SDMSExitStateProfileTable.getObject(sysEnv, espId, actVersion);
		data.add(esp.getName(sysEnv));
		esmpId = se.getEsmpId(sysEnv);
		if (esmpId != null) {
			esmp = SDMSExitStateMappingProfileTable.getObject(sysEnv, esmpId, actVersion);
			data.add(esmp.getName(sysEnv));
		} else {
			data.add(SystemEnvironment.defaultString);
		}

		neId = se.getNeId(sysEnv);
		if (neId != null) {
			ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId, actVersion);
			data.add(ne.getName(sysEnv));
		} else {
			data.add(SystemEnvironment.nullString);
		}

		fpId = se.getFpId(sysEnv);
		if (fpId != null) {
			fp = SDMSFootprintTable.getObject(sysEnv, fpId, actVersion);
			data.add(fp.getName(sysEnv));
		} else {
			data.add(SystemEnvironment.nullString);
		}

		data.add(sme.getMasterId(sysEnv));
		data.add(sme.getEffectiveTimeZoneName(sysEnv));
		data.add(sme.getChildTag(sysEnv));
		data.add(new Long(actVersion));
		group = SDMSGroupTable.getObject(sysEnv, sme.getOwnerId(sysEnv));
		data.add(group.getName(sysEnv));
		data.add(sme.getParentId(sysEnv));
		scopeId = sme.getScopeId(sysEnv);
		if(scopeId != null) {
			try {
				scope = SDMSScopeTable.getObject(sysEnv, scopeId);
				data.add(scope.pathVector(sysEnv));
				data.add(ScopeConfig.getItem(sysEnv, scope, Config.HTTP_HOST));
				data.add(ScopeConfig.getItem(sysEnv, scope, Config.HTTP_PORT));
			} catch (NotFoundException nfe) {
				data.add("Scope deleted");
				data.add(null);
				data.add(null);
			}
		} else {
			data.add(null);
			data.add(null);
			data.add(null);
		}
		data.add(sme.getIsStatic(sysEnv));
		data.add(sme.getMergeModeAsString(sysEnv));
		data.add(sme.getStateAsString(sysEnv));
		Boolean isCancelled = sme.getIsCancelled(sysEnv);
		if (isCancelled == null)
			if (sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.CANCELLED)
				isCancelled = Boolean.TRUE;
			else
				isCancelled = Boolean.FALSE;
		data.add(sme.getIsDisabled(sysEnv));
		data.add(sme.getIsParentDisabled(sysEnv));
		data.add(isCancelled);
		esdId = sme.getJobEsdId(sysEnv);
		if(esdId != null) {
			esd = SDMSExitStateDefinitionTable.getObject(sysEnv, esdId, actVersion);
			data.add(esd.getName(sysEnv));
		} else {
			data.add(null);
		}
		data.add(sme.getJobEsdPref(sysEnv));
		data.add(sme.getJobIsFinal(sysEnv));
		data.add(sme.getJobIsRestartable(sysEnv));
		esdId = sme.getFinalEsdId(sysEnv);
		if(esdId != null) {
			esd = SDMSExitStateDefinitionTable.getObject(sysEnv, esdId, actVersion);
			data.add(esd.getName(sysEnv));
		} else {
			data.add(null);
		}
		data.add(sme.getExitCode(sysEnv));
		data.add(sme.getCommandline(sysEnv));
		data.add(sme.getRrCommandline(sysEnv));
		data.add(sme.getWorkdir(sysEnv));
		data.add(sme.getLogfile(sysEnv));
		data.add(sme.getErrlogfile(sysEnv));
		data.add(sme.getPid(sysEnv));
		data.add(sme.getExtPid(sysEnv));
		data.add(sme.getErrorMsg(sysEnv));
		data.add(sme.getKillId(sysEnv));
		data.add(sme.getKillExitCode(sysEnv));
		data.add(sme.getIsSuspendedAsString(sysEnv));
		Boolean isSuspendedLocal = sme.getIsSuspendedLocal(sysEnv);
		if (isSuspendedLocal == null) isSuspendedLocal = Boolean.FALSE;
		data.add(isSuspendedLocal);
		data.add(sme.getPriority(sysEnv));
		data.add(sme.getRawPriority(sysEnv));
		data.add(sme.getNice(sysEnv));
		data.add(sme.getNpNice(sysEnv));
		data.add(sme.getMinPriority(sysEnv));
		data.add(sme.getAgingAmount(sysEnv));
		data.add("MINUTES");
		if(se.getType(sysEnv).intValue() == SDMSSchedulingEntity.JOB) {
			data.add(new Integer(SystemEnvironment.sched.getDynPriority(sysEnv, sme)));
		} else {
			data.add(null);
		}
		data.add(sme.getParentSuspended(sysEnv));

		int approve = sme.getApprovalMode(sysEnv).intValue();
		setApprovalValue(data, approve, SDMSSubmittedEntity.CANCEL_BITS, SDMSSubmittedEntity.CANCEL_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.RERUN_BITS, SDMSSubmittedEntity.RERUN_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.ENABLE_BITS, SDMSSubmittedEntity.ENABLE_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.SET_STATE_BITS, SDMSSubmittedEntity.SET_STATE_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.IGN_DEP_BITS, SDMSSubmittedEntity.IGN_DEP_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.IGN_RSS_BITS, SDMSSubmittedEntity.IGN_RSS_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.CLONE_BITS, SDMSSubmittedEntity.CLONE_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.SUSPEND_BITS, SDMSSubmittedEntity.SUSPEND_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.CLR_WARN_BITS, SDMSSubmittedEntity.CLR_WARN_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.PRIORITY_BITS, SDMSSubmittedEntity.PRIORITY_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.EDIT_PARM_BITS, SDMSSubmittedEntity.EDIT_PARM_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.KILL_BITS, SDMSSubmittedEntity.KILL_APPROVAL);
		setApprovalValue(data, approve, SDMSSubmittedEntity.SET_JOB_STATE_BITS, SDMSSubmittedEntity.SET_JOB_STATE_APPROVAL);

		Long submitTs = sme.getSubmitTs(sysEnv);
		if(submitTs != null) {
			d.setTime(submitTs.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);
		ts = sme.getResumeTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);
		ts = sme.getSyncTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);
		ts = sme.getResourceTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);
		ts = sme.getRunnableTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);
		ts = sme.getStartTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);
		ts = sme.getFinishTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);
		Long finalTs = sme.getFinalTs(sysEnv);
		if(finalTs != null) {
			d.setTime(finalTs.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else data.add(null);

		data.add(sme.getCntSubmitted(sysEnv));
		data.add(sme.getCntDependencyWait(sysEnv));
		data.add(sme.getCntSynchronizeWait(sysEnv));
		data.add(sme.getCntResourceWait(sysEnv));
		data.add(sme.getCntRunnable(sysEnv));
		data.add(sme.getCntStarting(sysEnv));
		data.add(sme.getCntStarted(sysEnv));
		data.add(sme.getCntRunning(sysEnv));
		data.add(sme.getCntToKill(sysEnv));
		data.add(sme.getCntKilled(sysEnv));
		data.add(sme.getCntCancelled(sysEnv));
		data.add(sme.getCntFinished(sysEnv));
		data.add(sme.getCntFinal(sysEnv));
		data.add(sme.getCntBrokenActive(sysEnv));
		data.add(sme.getCntBrokenFinished(sysEnv));
		data.add(sme.getCntError(sysEnv));
		data.add(sme.getCntRestartable(sysEnv));
		data.add(sme.getCntUnreachable(sysEnv));
		data.add(sme.getCntWarn(sysEnv));
		data.add(sme.getWarnCount(sysEnv));

		Integer idleTime = sme.evaluateTime(sysEnv, sme.getIdleTime(sysEnv), sme.getIdleTs(sysEnv), -1);
		data.add(idleTime.toString());
		Integer dwTime = sme.evaluateTime(sysEnv, sme.getDependencyWaitTime(sysEnv), sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_DEPENDENCY_WAIT);
		data.add(dwTime.toString());
		Integer suspendTime = sme.evaluateTime(sysEnv, sme.getSuspendTime(sysEnv), sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_SUSPEND);
		data.add(suspendTime.toString());
		Integer syncTime = sme.evaluateTime(sysEnv, sme.getSyncTime(sysEnv), sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_SYNCHRONIZE);
		data.add(syncTime.toString());
		Integer resourceTime = sme.evaluateTime(sysEnv, sme.getResourceTime(sysEnv), sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_RESOURCE);
		data.add(resourceTime.toString());
		Integer jobserverTime = sme.evaluateTime(sysEnv, sme.getJobserverTime(sysEnv), sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_JOBSERVER);
		data.add(jobserverTime.toString());
		Integer restartableTime = sme.evaluateTime(sysEnv, sme.getRestartableTime(sysEnv), sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_RESTARTABLE);
		data.add(restartableTime.toString());
		Integer childWaitTime = sme.evaluateTime(sysEnv, sme.getChildWaitTime(sysEnv), sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_CHILD_WAIT);
		data.add(childWaitTime.toString());

		int endTs;
		if (finalTs != null)
			endTs = (int)((finalTs.longValue() - submitTs.longValue()) / 1000);
		else
			endTs = (int)((sysEnv.cEnv.last() - submitTs.longValue()) / 1000);
		int processTime = endTs - dwTime.intValue();
		data.add(new Integer (processTime));
		data.add(new Integer(processTime - idleTime.intValue()));
		if (processTime == 0)
			data.add("");
		else
			data.add(new Integer(idleTime.intValue() * 100 / processTime));

		data.add(childContainer(sysEnv, smeId));
		data.add(parentContainer(sysEnv, smeId));
		data.add(parameterContainer(sysEnv, sme, se));
		data.add(requiredJobContainer(sysEnv, sme));
		data.add(dependentJobContainer(sysEnv, sme));
		data.add(resourceContainer(sysEnv, sme));
		data.add(sme.getSubmitPathString(sysEnv));
		data.add(sme.getIsReplaced(sysEnv));
		data.add(se.getTimeoutAmount(sysEnv));
		data.add(se.getTimeoutBaseAsString(sysEnv));
		esdId = se.getTimeoutStateId(sysEnv);
		if(esdId != null) {
			data.add(SDMSExitStateDefinitionTable.getObject(sysEnv, esdId, actVersion).getName(sysEnv));
		} else {
			data.add(null);
		}
		data.add(sme.getRerunSeq(sysEnv));
		data.add(auditContainer(sysEnv, sme));
		data.add(sme.getChildSuspended(sysEnv));
		data.add(sme.getCntPending(sysEnv));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, sme.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(sme.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			try {
				data.add(SDMSUserTable.getObject(sysEnv, sme.getChangerUId(sysEnv)).getName(sysEnv));
			} catch (NotFoundException nfe) {
				data.add(SDMSScopeTable.getObject(sysEnv, sme.getChangerUId(sysEnv)).pathString(sysEnv));
			}
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(sme.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(sme.getPrivileges(sysEnv).toString());
		data.add(se.getPrivileges(sysEnv, actVersion).toString());
		data.add(sme.getSubmitTag(sysEnv));
		data.add(SDMSSystemMessageTable.idx_smeId.containsKey(sysEnv, smeId));
		Integer unresolvedHandling = sme.getUnresolvedHandling(sysEnv);
		if(unresolvedHandling == null)
			data.add(null);
		else if(unresolvedHandling.intValue() == SDMSDependencyDefinition.SUSPEND)
			data.add("SUSPEND");
		else if(unresolvedHandling.intValue() == SDMSDependencyDefinition.DEFER)
			data.add("DEFERED");
		else
			data.add("IGNORE");

		data.add(definedResources(sysEnv, smeId));
		if (strType.equals("JOB"))
			data.add(runs(sysEnv, sme));

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage (sysEnv, "03205081202", "Job"), desc, data);

		return d_container;
	}

	private	SDMSOutputContainer childContainer (SystemEnvironment sysEnv, Long parentId)
		throws SDMSException
	{
		Vector hi_v = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, parentId);
		return familyContainer(sysEnv, hi_v);
	}

	private	SDMSOutputContainer parentContainer (SystemEnvironment sysEnv, Long childId)
		throws SDMSException
	{
		Vector hi_v = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, childId);
		return familyContainer(sysEnv, hi_v);
	}

	private	SDMSOutputContainer familyContainer (SystemEnvironment sysEnv, Vector hi_v)
		throws SDMSException
	{
		Vector c_desc = new Vector();
		c_desc.add("CHILDID");
		c_desc.add("CHILDPRIVS");
		c_desc.add("CHILDSENAME");
		c_desc.add("CHILDSETYPE");
		c_desc.add("CHILDSEPRIVS");
		c_desc.add("PARENTID");
		c_desc.add("PARENTPRIVS");
		c_desc.add("PARENTSENAME");
		c_desc.add("PARENTSETYPE");
		c_desc.add("PARENTSEPRIVS");
		c_desc.add("IS_STATIC");
		c_desc.add("PRIORITY");
		c_desc.add("SUSPEND");
		c_desc.add("MERGE_MODE");
		c_desc.add("EST_NAME");
		c_desc.add("IGNORED_DEPENDENCIES");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		SDMSHierarchyInstance hi;
		SDMSSchedulingHierarchy sh;
		SDMSSchedulingEntity cse;
		SDMSSubmittedEntity csme;
		Long estpId;
		SDMSExitStateTranslationProfile estp;

		Vector c_data;
		Iterator i = hi_v.iterator();
		while (i.hasNext()) {
			hi = (SDMSHierarchyInstance)(i.next());
			sh = SDMSSchedulingHierarchyTable.getObject(sysEnv, hi.getShId(sysEnv), actVersion);
			c_data = new Vector();
			c_data.add(hi.getChildId(sysEnv));
			csme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
			c_data.add(csme.getPrivileges(sysEnv).toString());

			cse = SDMSSchedulingEntityTable.getObject(sysEnv, csme.getSeId(sysEnv), actVersion);

			c_data.add(cse.pathVector(sysEnv, actVersion));
			c_data.add(cse.getTypeAsString(sysEnv));
			c_data.add(cse.getPrivileges(sysEnv, actVersion).toString());
			c_data.add(hi.getParentId(sysEnv));
			csme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
			c_data.add(csme.getPrivileges(sysEnv).toString());

			cse = SDMSSchedulingEntityTable.getObject(sysEnv, csme.getSeId(sysEnv), actVersion);

			c_data.add(cse.pathVector(sysEnv, actVersion));
			c_data.add(cse.getTypeAsString(sysEnv));
			c_data.add(cse.getPrivileges(sysEnv, actVersion).toString());
			c_data.add(sh.getIsStatic(sysEnv));
			c_data.add(sh.getPriority(sysEnv));
			c_data.add(sh.getSuspendAsString(sysEnv));
			c_data.add(sh.getMergeModeAsString(sysEnv));
			estpId = sh.getEstpId(sysEnv);
			if (estpId != null) {
				estp = SDMSExitStateTranslationProfileTable.getObject(sysEnv, estpId, actVersion);
				c_data.add(estp.getName(sysEnv));
			} else {
				c_data.add(null);
			}

			Vector ids_v = SDMSIgnoredDependencyTable.idx_shId.getVector(sysEnv, sh.getId(sysEnv), actVersion);
			String sep = "";
			StringBuffer deps = new StringBuffer();
			for(int j = 0; j < ids_v.size(); j++) {
				SDMSIgnoredDependency ids = (SDMSIgnoredDependency) ids_v.get(j);
				deps.append(sep);
				deps.append(ids.getDdName(sysEnv));
				sep = ",";
			}
			c_data.add(new String(deps));

			c_container.addData(sysEnv, c_data);
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 1));

		return c_container;
	}

	private	SDMSOutputContainer parameterContainer (SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se)
		throws SDMSException
	{
		SDMSParameterDefinition pd;
		SDMSEntityVariable ev;
		HashSet names = new HashSet();

		Vector c_desc = new Vector();
		c_desc.add("ID");
		c_desc.add("NAME");
		c_desc.add("TYPE");
		c_desc.add("VALUE");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		Vector pd_v = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, se.getId(sysEnv), actVersion);

		Vector c_data;
		for(int i = 0; i < pd_v.size(); i++) {
			pd = (SDMSParameterDefinition)(pd_v.get(i));
			c_data = new Vector();
			c_data.add(pd.getId(sysEnv));
			c_data.add(pd.getName(sysEnv));
			names.add(pd.getName(sysEnv));
			c_data.add(pd.getTypeAsString(sysEnv));
			try {
				c_data.add(sme.getVariableValue(sysEnv, pd.getName(sysEnv), false, ParseStr.S_DEFAULT));
			} catch(NotFoundException nfe) {
				c_data.add(null);
			} catch(CommonErrorException cee) {
				c_data.add("ERROR: " + cee.toString());
			}

			c_container.addData(sysEnv, c_data);
		}
		pd_v = SDMSEntityVariableTable.idx_smeId.getVector(sysEnv, sme.getId(sysEnv));
		for(int i = 0; i < pd_v.size(); i++) {
			ev = (SDMSEntityVariable) pd_v.get(i);
			if(!names.contains(ev.getName(sysEnv))) {
				c_data = new Vector();
				c_data.add(ev.getId(sysEnv));
				c_data.add(ev.getName(sysEnv));
				c_data.add("LOCAL");
				c_data.add(ev.getValue(sysEnv).substring(1));

				c_container.addData(sysEnv, c_data);
			}
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 1));

		return c_container;
	}

	private	SDMSOutputContainer requiredJobContainer (SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Long smeId = sme.getId(sysEnv);
		Vector di_v = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, smeId);
		SDMSOutputContainer c_container = dependentContainer(sysEnv, di_v, REQUIRED);
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 3, 7));
		return c_container;
	}

	private	SDMSOutputContainer dependentJobContainer (SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Long smeId = sme.getId(sysEnv);
		Vector di_v = SDMSDependencyInstanceTable.idx_requiredId.getVector(sysEnv, smeId);
		Vector v_di_out = new Vector();
		Iterator i_di = di_v.iterator();
		while (i_di.hasNext()) {
			SDMSDependencyInstance di = (SDMSDependencyInstance)i_di.next();
			if (di.getDependentId(sysEnv).equals(di.getDependentIdOrig(sysEnv))) {
				v_di_out.add(di);
			}
		}
		SDMSOutputContainer c_container = dependentContainer(sysEnv, v_di_out, DEPENDENTS);
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 3, 2));

		return c_container;
	}

	private	SDMSOutputContainer dependentContainer (SystemEnvironment sysEnv, Vector di_v, int mode)
		throws SDMSException
	{
		SDMSDependencyInstance di;
		SDMSDependencyDefinition dd;

		Vector c_desc = new Vector();
		c_desc.add("ID");
		c_desc.add("DEPENDENT_ID");
		c_desc.add("DEPENDENT_PATH");
		c_desc.add("DEPENDENT_PRIVS");
		c_desc.add("DEPENDENT_ID_ORIG");
		c_desc.add("DEPENDENT_PATH_ORIG");
		c_desc.add("DEPENDENT_PRIVS_ORIG");
		c_desc.add("DEPENDENCY_OPERATION");
		c_desc.add("REQUIRED_ID");
		c_desc.add("REQUIRED_PATH");
		c_desc.add("REQUIRED_PRIVS");
		c_desc.add("STATE");

		c_desc.add("DD_ID");
		c_desc.add("DD_NAME");
		c_desc.add("DD_DEPENDENTNAME");
		c_desc.add("DD_DEPENDENTTYPE");
		c_desc.add("DD_DEPENDENTPRIVS");
		c_desc.add("DD_REQUIREDNAME");
		c_desc.add("DD_REQUIREDTYPE");
		c_desc.add("DD_REQUIREDPRIVS");
		c_desc.add("DD_UNRESOLVED_HANDLING");
		c_desc.add("DD_STATE_SELECTION");
		c_desc.add("DD_MODE");
		c_desc.add("DD_STATES");
		c_desc.add("JOB_STATE");
		c_desc.add("IS_SUSPENDED");
		c_desc.add("PARENT_SUSPENDED");
		c_desc.add("CNT_SUBMITTED");
		c_desc.add("CNT_DEPENDENCY_WAIT");
		c_desc.add("CNT_SYNCHRONIZE_WAIT");
		c_desc.add("CNT_RESOURCE_WAIT");
		c_desc.add("CNT_RUNNABLE");
		c_desc.add("CNT_STARTING");
		c_desc.add("CNT_STARTED");
		c_desc.add("CNT_RUNNING");
		c_desc.add("CNT_TO_KILL");
		c_desc.add("CNT_KILLED");
		c_desc.add("CNT_CANCELLED");
		c_desc.add("CNT_FINISHED");
		c_desc.add("CNT_FINAL");
		c_desc.add("CNT_BROKEN_ACTIVE");
		c_desc.add("CNT_BROKEN_FINISHED");
		c_desc.add("CNT_ERROR");
		c_desc.add("CNT_RESTARTABLE");
		c_desc.add("CNT_UNREACHABLE");
		c_desc.add("JOB_IS_FINAL");
		c_desc.add("CHILD_TAG");
		c_desc.add("FINAL_STATE");
		c_desc.add("CHILDREN");
		c_desc.add("IGNORE");
		c_desc.add("CHILD_SUSPENDED");
		c_desc.add("CNT_PENDING");
		c_desc.add("DD_CONDITION");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		Vector c_data;
		SDMSSubmittedEntity sme;
		SDMSSchedulingEntity se;

		for (int i = 0; i < di_v.size(); i++) {
			di = (SDMSDependencyInstance)(di_v.get(i));
			dd = SDMSDependencyDefinitionTable.getObject(sysEnv, di.getDdId(sysEnv), actVersion);

			c_data = new Vector();

			c_data.add(di.getId(sysEnv));
			c_data.add(di.getDependentId(sysEnv));

			sme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getDependentId(sysEnv));
			c_data.add(sme.getSubmitPathString(sysEnv));
			c_data.add(sme.getPrivileges(sysEnv).toString());

			c_data.add(di.getDependentIdOrig(sysEnv));

			sme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getDependentIdOrig(sysEnv));
			c_data.add(sme.getSubmitPathString(sysEnv));
			c_data.add(sme.getPrivileges(sysEnv).toString());

			c_data.add(di.getDependencyOperationAsString(sysEnv));
			c_data.add(di.getRequiredId(sysEnv));

			if (mode == REQUIRED)
				try {
					sme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getRequiredId(sysEnv));
				} catch(NotFoundException nfe) {
					sme = null;
				}
			else
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getDependentId(sysEnv));

			if (sme == null) {
				SDMSSchedulingEntity se_def = SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeRequiredId(sysEnv), actVersion);
				c_data.add(se_def.pathVector(sysEnv, actVersion));
				c_data.add(null);
			} else {
				c_data.add(sme.getSubmitPathString(sysEnv));
				c_data.add(sme.getPrivileges(sysEnv).toString());
			}

			c_data.add(di.getStateAsString(sysEnv));

			c_data.add(dd.getId(sysEnv));
			c_data.add(dd.getName(sysEnv));

			se = SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeDependentId(sysEnv), actVersion);
			c_data.add(se.pathVector(sysEnv, actVersion));
			c_data.add(se.getTypeAsString(sysEnv));
			c_data.add(se.getPrivileges(sysEnv, actVersion).toString());
			se = SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeRequiredId(sysEnv), actVersion);
			c_data.add(se.pathVector(sysEnv, actVersion));
			c_data.add(se.getTypeAsString(sysEnv));
			c_data.add(se.getPrivileges(sysEnv, actVersion).toString());
			c_data.add(dd.getUnresolvedHandlingAsString(sysEnv));
			c_data.add(dd.getStateSelectionAsString(sysEnv));
			c_data.add(dd.getModeAsString(sysEnv));

			Vector dds_v = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, dd.getId(sysEnv), actVersion);
			String sep = "";
			StringBuffer states = new StringBuffer();
			for(int j = 0; j < dds_v.size(); j++) {
				SDMSDependencyState dds = (SDMSDependencyState) dds_v.get(j);
				String esdn = SDMSExitStateDefinitionTable.getObject(sysEnv, dds.getEsdId(sysEnv), actVersion).getName(sysEnv);
				states.append(sep);
				states.append(esdn);
				String condition = dds.getCondition(sysEnv);
				if(condition != null) {
					states.append("*");
				}
				sep = ",";
			}
			c_data.add(new String(states));

			if (sme != null) {
				c_data.add(sme.getStateAsString(sysEnv));
				c_data.add(sme.getIsSuspendedAsString(sysEnv));
				c_data.add(sme.getParentSuspended(sysEnv));

				c_data.add(sme.getCntSubmitted(sysEnv));
				c_data.add(sme.getCntDependencyWait(sysEnv));
				c_data.add(sme.getCntSynchronizeWait(sysEnv));
				c_data.add(sme.getCntResourceWait(sysEnv));
				c_data.add(sme.getCntRunnable(sysEnv));
				c_data.add(sme.getCntStarting(sysEnv));
				c_data.add(sme.getCntStarted(sysEnv));
				c_data.add(sme.getCntRunning(sysEnv));
				c_data.add(sme.getCntToKill(sysEnv));
				c_data.add(sme.getCntKilled(sysEnv));
				c_data.add(sme.getCntCancelled(sysEnv));
				c_data.add(sme.getCntFinished(sysEnv));
				c_data.add(sme.getCntFinal(sysEnv));
				c_data.add(sme.getCntBrokenActive(sysEnv));
				c_data.add(sme.getCntBrokenFinished(sysEnv));
				c_data.add(sme.getCntError(sysEnv));
				c_data.add(sme.getCntRestartable(sysEnv));
				c_data.add(sme.getCntUnreachable(sysEnv));
				c_data.add(sme.getJobIsFinal(sysEnv));
				c_data.add(sme.getChildTag(sysEnv));

				if (sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.FINAL ||
				    (dd.getMode(sysEnv).intValue() == SDMSDependencyDefinition.JOB_FINAL && sme.getJobIsFinal(sysEnv).booleanValue())
				   ) {
					Long finalEsdId;
					if (dd.getMode(sysEnv).intValue() == SDMSDependencyDefinition.JOB_FINAL) {
						finalEsdId = sme.getJobEsdId(sysEnv);
					} else {
						finalEsdId = sme.getFinalEsdId(sysEnv);
					}
					if (finalEsdId != null) {
						SDMSExitStateDefinition esd = SDMSExitStateDefinitionTable.getObject(sysEnv, finalEsdId);
						c_data.add(esd.getName(sysEnv));
					} else {
						c_data.add(null);
					}
				} else {
					c_data.add(null);
				}

				Vector c = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, sme.getId(sysEnv));
				c_data.add(new Integer(c.size()));

				c_data.add(di.getIgnoreAsString(sysEnv));
				c_data.add(sme.getChildSuspended(sysEnv));
				c_data.add(sme.getCntPending(sysEnv));
				c_data.add(dd.getCondition(sysEnv));
			} else {
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);

				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
				c_data.add(di.getIgnoreAsString(sysEnv));
				c_data.add(null);
				c_data.add(null);
				c_data.add(dd.getCondition(sysEnv));
			}

			c_container.addData(sysEnv, c_data);
		}

		return c_container;
	}

	private	SDMSOutputContainer resourceContainer (SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		HashMap jobFp;
		HashMap fpFolder;
		HashMap fpLocal;
		try {
			Vector footprints = SchedulingThread.getJobFootprint(sysEnv, sme);
			jobFp = (HashMap) footprints.get(SchedulingThread.FP_SCOPE);
			fpFolder = (HashMap) footprints.get(SchedulingThread.FP_FOLDER);
			fpLocal = (HashMap) footprints.get(SchedulingThread.FP_LOCAL);
		} catch (NotFoundException nfe) {
			jobFp = new HashMap();
			fpFolder = new HashMap();
			fpLocal = new HashMap();
		}
		ResourceScopeLister sl = new ResourceScopeLister(null, null);
		sl.setTitle(null);
		sl.setFormatter(new SsResourceScopeFormatter(sysEnv, sme));
		int sc[] = new int[1];
		sc[0] = 1;
		sl.setSortColumns(sc);
		Vector resourceList = new Vector();
		int size;
		Long smeId = sme.getId(sysEnv);
		Iterator ri;
		HashSet resources;

		int state = sme.getState(sysEnv).intValue();
		boolean checkSRCondition = false;
		switch(state) {
			case SDMSSubmittedEntity.DEPENDENCY_WAIT:
			case SDMSSubmittedEntity.SYNCHRONIZE_WAIT:
			case SDMSSubmittedEntity.RESOURCE_WAIT:
			case SDMSSubmittedEntity.RUNNABLE:
				checkSRCondition = true;

			case SDMSSubmittedEntity.SUBMITTED:
				resources = new HashSet();
				Vector sv = SDMSScopeTable.idx_type.getVector(sysEnv, new Integer(SDMSScope.SERVER));
				size = sv.size();
				for(int i = 0; i < size; i++) {
					SDMSScope s = (SDMSScope) sv.get(i);
					SDMSnpSrvrSRFootprint npsfp = (SDMSnpSrvrSRFootprint) SDMSnpSrvrSRFootprintTable.idx_sId.getUnique(sysEnv, s.getId(sysEnv));
					HashMap sfp = npsfp.getFp(sysEnv);
					if(SchedulingThread.fits(sysEnv, sfp, jobFp, sme, checkSRCondition, (checkSRCondition ? s : null))) {
						ri = jobFp.keySet().iterator();
						boolean resourcesAdded = false;
						while(ri.hasNext()) {
							Long tmpNrId = (Long) ri.next();
							Long tmpRId = (Long) sfp.get(tmpNrId);
							Vector rav = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(smeId, tmpNrId));
							for (int j = 0; j < rav.size(); ++j) {
								SDMSResourceAllocation tmpRa = (SDMSResourceAllocation) rav.get(j);
								if (tmpRa.getRId(sysEnv).equals(tmpRId)) {
									resources.add(tmpRId);
									resourcesAdded = true;
									break;
								}
							}
						}
						if (resourcesAdded)
							resources.add(s.getId(sysEnv));
					}
				}
				ri = fpFolder.values().iterator();
				while(ri.hasNext()) {
					Vector rv = (Vector) ri.next();
					SDMSResource r = (SDMSResource) rv.get(1);
					resources.add(r.getId(sysEnv));
				}
				ri = fpLocal.values().iterator();
				while(ri.hasNext()) {
					Vector rv = (Vector) ri.next();
					SDMSResource r = (SDMSResource) rv.get(1);
					resources.add(r.getId(sysEnv));
				}
				ri = resources.iterator();
				while(ri.hasNext()) resourceList.add(ri.next());
				break;
			case SDMSSubmittedEntity.STARTING:
			case SDMSSubmittedEntity.STARTED:
			case SDMSSubmittedEntity.RUNNING:
			case SDMSSubmittedEntity.TO_KILL:
			case SDMSSubmittedEntity.KILLED:
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				resources = new HashSet();
				SDMSnpSrvrSRFootprint npsfp = (SDMSnpSrvrSRFootprint) SDMSnpSrvrSRFootprintTable.idx_sId.getUnique(sysEnv, sme.getScopeId(sysEnv));
				HashMap sfp = npsfp.getFp(sysEnv);
				resources.add(sme.getScopeId(sysEnv));
				ri = jobFp.keySet().iterator();
				while(ri.hasNext()) {
					Long L = (Long) ri.next();
					resources.add((Long) sfp.get(L));
				}
				ri = fpFolder.values().iterator();
				while(ri.hasNext()) {
					Vector rv = (Vector) ri.next();
					SDMSResource r = (SDMSResource) rv.get(1);
					resources.add(r.getId(sysEnv));
				}
				ri = fpLocal.values().iterator();
				while(ri.hasNext()) {
					Vector rv = (Vector) ri.next();
					SDMSResource r = (SDMSResource) rv.get(1);
					resources.add(r.getId(sysEnv));
				}
				ri = resources.iterator();
				while(ri.hasNext()) resourceList.add(ri.next());
				break;
			case SDMSSubmittedEntity.CANCELLED:
			case SDMSSubmittedEntity.FINISHED:
			case SDMSSubmittedEntity.FINAL:
			case SDMSSubmittedEntity.BROKEN_FINISHED:
			case SDMSSubmittedEntity.UNREACHABLE:
			case SDMSSubmittedEntity.ERROR:
				Vector rav = SDMSResourceAllocationTable.idx_smeId.getVector(sysEnv, smeId);
				size = rav.size();
				for(int i = 0; i < size; i++) {
					resourceList.add(((SDMSResourceAllocation) rav.get(i)).getRId(sysEnv));
				}
				rav = SDMSResourceAllocationTable.idx_smeId.getVector(sysEnv, new Long(- smeId.longValue()));
				size = rav.size();
				for(int i = 0; i < size; i++) {
					resourceList.add(((SDMSResourceAllocation) rav.get(i)).getRId(sysEnv));
				}
				break;
		}

		sl.setObjectsToList(resourceList);

		SDMSOutputContainer c_container = sl.list(sysEnv, false);

		return c_container;
	}

	private	SDMSOutputContainer definedResources (SystemEnvironment sysEnv, Long smeId)
		throws SDMSException
	{
		Vector c_desc = new Vector();
		c_desc.add("ID");
		c_desc.add("RESOURCE_NAME");
		c_desc.add("RESOURCE_USAGE");
		c_desc.add("RESOURCE_OWNER");
		c_desc.add("RESOURCE_PRIVS");
		c_desc.add("RESOURCE_STATE");
		c_desc.add("RESOURCE_TIMESTAMP");
		c_desc.add("REQUESTABLE_AMOUNT");
		c_desc.add("TOTAL_AMOUNT");
		c_desc.add("FREE_AMOUNT");
		c_desc.add("ONLINE");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		Vector c_data;
		Vector v = SDMSResourceTable.idx_scopeId.getVector(sysEnv, smeId);
		for (int i = 0; i < v.size(); i++) {
			final SDMSResource r = (SDMSResource) v.get(i);
			final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv));

			c_data = new Vector();
			c_data.add(r.getId(sysEnv));
			c_data.add(nr.pathVector(sysEnv));
			c_data.add(nr.getUsageAsString(sysEnv));
			c_data.add(SDMSGroupTable.getObject(sysEnv, r.getOwnerId(sysEnv)).getName(sysEnv));
			c_data.add(r.getPrivileges(sysEnv).toString());

			final Long rsdId = r.getRsdId(sysEnv);
			if(rsdId != null) {
				c_data.add(SDMSResourceStateDefinitionTable.getObject(sysEnv, rsdId).getName(sysEnv));
			} else	c_data.add(null);

			final Long ots = r.getRsdTime(sysEnv);
			if(ots != null &&
			   nr.getUsage(sysEnv).intValue() == SDMSNamedResource.SYNCHRONIZING &&
			   nr.getRspId(sysEnv) != null) {
				long ts = ots.longValue();
				Date dts = new Date();
				dts.setTime(ts);
				c_data.add(sysEnv.systemDateFormat.format(dts));
			} else c_data.add(null);

			c_data.add(r.getRequestableAmount(sysEnv));
			c_data.add(r.getAmount(sysEnv));
			c_data.add(r.getFreeAmount(sysEnv));
			c_data.add(r.getIsOnline(sysEnv));

			c_container.addData(sysEnv, c_data);
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 1));

		return c_container;
	}

	private	SDMSOutputContainer runs (SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Vector c_desc = new Vector();
		c_desc.add("RERUN_SEQ");
		c_desc.add("SCOPE_ID");
		c_desc.add("HTTPHOST");
		c_desc.add("HTTPPORT");
		c_desc.add("JOB_ESD_ID");
		c_desc.add("EXIT_CODE");
		c_desc.add("COMMANDLINE");
		c_desc.add("WORKDIR");
		c_desc.add("LOGFILE");
		c_desc.add("ERRLOGFILE");
		c_desc.add("EXT_PID");
		c_desc.add("SYNC_TS");
		c_desc.add("RESOURCE_TS");
		c_desc.add("RUNNABLE_TS");
		c_desc.add("START_TS");
		c_desc.add("FINISH_TS");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		Long scopeId;
		SDMSScope scope;
		Long esdId;
		SDMSExitStateDefinition esd;
		Date d = new Date();
		Long ts;
		int rrSeq;

		Vector c_data;

		c_data = new Vector();
		rrSeq = sme.getRerunSeq(sysEnv);
		c_data.add(new Integer(rrSeq));
		scopeId = sme.getScopeId(sysEnv);
		if(scopeId != null) {
			try {
				scope = SDMSScopeTable.getObject(sysEnv, scopeId);
				c_data.add(scope.pathVector(sysEnv));
				c_data.add(ScopeConfig.getItem(sysEnv, scope, Config.HTTP_HOST));
				c_data.add(ScopeConfig.getItem(sysEnv, scope, Config.HTTP_PORT));
			} catch (NotFoundException nfe) {
				c_data.add("Scope deleted");
				c_data.add(null);
				c_data.add(null);
			}
		} else {
			c_data.add(null);
			c_data.add(null);
			c_data.add(null);
		}
		esdId = sme.getJobEsdId(sysEnv);
		if(esdId != null) {
			esd = SDMSExitStateDefinitionTable.getObject(sysEnv, esdId, actVersion);
			c_data.add(esd.getName(sysEnv));
		} else {
			c_data.add(null);
		}
		c_data.add(sme.getExitCode(sysEnv));
		if (rrSeq == 0)
			c_data.add(sme.getCommandline(sysEnv));
		else
			c_data.add(sme.getRrCommandline(sysEnv));

		c_data.add(sme.getWorkdir(sysEnv));
		c_data.add(sme.getLogfile(sysEnv));
		c_data.add(sme.getErrlogfile(sysEnv));
		c_data.add(sme.getExtPid(sysEnv));
		ts = sme.getSyncTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			c_data.add(sysEnv.systemDateFormat.format(d));
		} else c_data.add(null);
		ts = sme.getResourceTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			c_data.add(sysEnv.systemDateFormat.format(d));
		} else c_data.add(null);
		ts = sme.getRunnableTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			c_data.add(sysEnv.systemDateFormat.format(d));
		} else c_data.add(null);
		ts = sme.getStartTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			c_data.add(sysEnv.systemDateFormat.format(d));
		} else c_data.add(null);
		ts = sme.getFinishTs(sysEnv);
		if(ts != null) {
			d.setTime(ts.longValue());
			c_data.add(sysEnv.systemDateFormat.format(d));
		} else c_data.add(null);

		c_container.addData(sysEnv, c_data);

		Vector v = SDMSSubmittedEntityStatsTable.idx_smeId.getVector(sysEnv, sme.getId(sysEnv));
		for (int i = 0; i < v.size(); i++) {
			final SDMSSubmittedEntityStats s = (SDMSSubmittedEntityStats) v.get(i);

			c_data = new Vector();
			rrSeq = s.getRerunSeq(sysEnv);
			c_data.add(new Integer(rrSeq));
			scopeId = s.getScopeId(sysEnv);
			if(scopeId != null) {
				try {
					scope = SDMSScopeTable.getObject(sysEnv, scopeId);
					c_data.add(scope.pathVector(sysEnv));
					c_data.add(ScopeConfig.getItem(sysEnv, scope, Config.HTTP_HOST));
					c_data.add(ScopeConfig.getItem(sysEnv, scope, Config.HTTP_PORT));
				} catch (NotFoundException nfe) {
					c_data.add("Scope deleted");
					c_data.add(null);
					c_data.add(null);
				}
			} else {
				c_data.add(null);
				c_data.add(null);
				c_data.add(null);
			}
			esdId = s.getJobEsdId(sysEnv);
			if(esdId != null) {
				esd = SDMSExitStateDefinitionTable.getObject(sysEnv, esdId, actVersion);
				c_data.add(esd.getName(sysEnv));
			} else {
				c_data.add(null);
			}
			c_data.add(s.getExitCode(sysEnv));
			c_data.add(s.getCommandline(sysEnv));
			c_data.add(s.getWorkdir(sysEnv));
			c_data.add(s.getLogfile(sysEnv));
			c_data.add(s.getErrlogfile(sysEnv));
			c_data.add(s.getExtPid(sysEnv));
			ts = s.getSyncTs(sysEnv);
			if(ts != null) {
				d.setTime(ts.longValue());
				c_data.add(sysEnv.systemDateFormat.format(d));
			} else c_data.add(null);
			ts = s.getResourceTs(sysEnv);
			if(ts != null) {
				d.setTime(ts.longValue());
				c_data.add(sysEnv.systemDateFormat.format(d));
			} else c_data.add(null);
			ts = s.getRunnableTs(sysEnv);
			if(ts != null) {
				d.setTime(ts.longValue());
				c_data.add(sysEnv.systemDateFormat.format(d));
			} else c_data.add(null);
			ts = s.getStartTs(sysEnv);
			if(ts != null) {
				d.setTime(ts.longValue());
				c_data.add(sysEnv.systemDateFormat.format(d));
			} else c_data.add(null);
			ts = s.getFinishTs(sysEnv);
			if(ts != null) {
				d.setTime(ts.longValue());
				c_data.add(sysEnv.systemDateFormat.format(d));
			} else c_data.add(null);

			c_container.addData(sysEnv, c_data);
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 0));

		return c_container;
	}

	private void auditContainerRenderSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSOutputContainer c_container, Vector filter, Boolean recursive)
		throws SDMSException
	{
	}

	private	SDMSOutputContainer auditContainer (SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Date d = new Date();
		Vector c_desc = new Vector();
		c_desc.add("ID");
		c_desc.add("USERNAME");
		c_desc.add("TIME");
		c_desc.add("TXID");
		c_desc.add("ACTION");
		c_desc.add("ORIGINID");
		c_desc.add("JOBID");
		c_desc.add("JOBNAME");
		c_desc.add("COMMENT");
		c_desc.add("INFO");

		Vector filter = null;
		Boolean recursive = null;
		if (with != null) {
			filter = (Vector) with.get(ParseStr.S_FILTER);
			recursive = (Boolean) with.get(ParseStr.S_RECURSIVE);
		}
		if (recursive == null) recursive = Boolean.FALSE;

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		auditContainerRenderSme(sysEnv, sme, c_container, filter, recursive);

		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, auditSortCols));

		for(int i = 0; i < c_container.dataset.size(); i++) {
			Vector v = (Vector) c_container.dataset.get(i);
			d.setTime(((Long) v.get(2)).longValue());
			v.set(2, sysEnv.systemDateFormat.format(d));
			c_container.setWidth(sysEnv, 2, sysEnv.systemDateFormat.format(d).toString().length());
		}

		return c_container;
	}
}

class SsResourceScopeFormatter implements Formatter
{

	private static final String ALLOCATED = "ALLOCATED";
	private static final String RESERVED  = "RESERVED";
	private static final String IGNORE    = "IGNORE";
	private static final String BLOCKED   = "BLOCKED";
	private static final String AVAILABLE = "AVAILABLE";

	SDMSSubmittedEntity sme;
	SDMSSchedulingEntity se;
	Long seId;
	Long smeId;
	long actVersion;

	public SsResourceScopeFormatter(SystemEnvironment sysEnv, SDMSSubmittedEntity e)
		throws SDMSException
	{
		sme = e;
		initialize(sysEnv);
	}

	private void initialize(SystemEnvironment sysEnv)
		throws SDMSException
	{
		actVersion = sme.getSeVersion(sysEnv).longValue();
		se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		seId = se.getId(sysEnv);
		smeId = sme.getId(sysEnv);
	}

	public Vector fillHeadInfo()
	{
		Vector c_desc = new Vector();
		c_desc.add("SCOPE_ID");
		c_desc.add("SCOPE_NAME");
		c_desc.add("SCOPE_TYPE");
		c_desc.add("SCOPE_PRIVS");
		c_desc.add("RESOURCE_ID");
		c_desc.add("RESOURCE_NAME");
		c_desc.add("RESOURCE_USAGE");
		c_desc.add("RESOURCE_OWNER");
		c_desc.add("RESOURCE_PRIVS");
		c_desc.add("RESOURCE_STATE");
		c_desc.add("RESOURCE_TIMESTAMP");
		c_desc.add("REQUESTABLE_AMOUNT");
		c_desc.add("TOTAL_AMOUNT");
		c_desc.add("FREE_AMOUNT");
		c_desc.add("REQUESTED_AMOUNT");
		c_desc.add("REQUESTED_LOCKMODE");
		c_desc.add("REQUESTED_STATES");
		c_desc.add("RESERVED_AMOUNT");
		c_desc.add("ALLOCATED_AMOUNT");
		c_desc.add("ALLOCATED_LOCKMODE");
		c_desc.add("IGNORE");
		c_desc.add("STICKY");
		c_desc.add("STICKY_NAME");
		c_desc.add("STICKY_PARENT");
		c_desc.add("STICKY_PARENT_TYPE");
		c_desc.add("ONLINE");
		c_desc.add("ALLOCATE_STATE");
		c_desc.add("EXPIRE");
		c_desc.add("EXPIRE_SIGN");
		c_desc.add("IGNORE_ON_RERUN");
		c_desc.add("DEFINITION");

		return c_desc;
	}

	public Vector fillVector(SystemEnvironment sysEnv, SDMSProxy co, HashSet parentSet)
		throws SDMSException
	{
		if(co instanceof SDMSScope) {
			return fillVector(sysEnv, (SDMSScope) co);
		} else {
			return fillVector(sysEnv, (SDMSResource) co);
		}
	}

	private Vector fillVector(SystemEnvironment sysEnv, SDMSScope s)
		throws SDMSException
	{
		Vector v = new Vector();
		Long scopeId = s.getId(sysEnv);
		PathVector scopeName = s.pathVector(sysEnv);

		v.add(scopeId);
		v.add(scopeName);
		v.add(s.getTypeAsString(sysEnv));
		v.add(s.getPrivileges(sysEnv).toString());
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(Boolean.FALSE);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(new Boolean(s.isConnected(sysEnv)));
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(scopeName);

		return v;
	}

	private Vector fillVector(SystemEnvironment sysEnv, SDMSResource r)
		throws SDMSException
	{
		SDMSResourceAllocation ra;
		Vector v = new Vector();
		Long scopeId = r.getScopeId(sysEnv);
		SDMSScope s = null;
		SDMSFolder f = null;
		SDMSSchedulingEntity dse = null;
		SDMSProxy defProxy;

		try {
			s = SDMSScopeTable.getObject(sysEnv, scopeId);
			defProxy = s;
		} catch(NotFoundException nfe) {
			s = SDMSScopeTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(null, "GLOBAL"));
			try {
				f = SDMSFolderTable.getObject(sysEnv, scopeId);
				defProxy = f;
			} catch(NotFoundException nfe2) {
				final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, scopeId);
				dse = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv).longValue());
				defProxy = dse;
			}
		}
		PathVector scopeName = s.pathVector(sysEnv);
		long ts;
		java.util.Date dts;

		Long nrId = r.getNrId(sysEnv);
		Long rId = r.getId(sysEnv);
		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
		Long rsdId = r.getRsdId(sysEnv);
		SDMSResourceStateDefinition rsd = null;
		SDMSResourceRequirement rr = null;
		if(rsdId != null) {
			rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, rsdId);
		}
		v.add(scopeId);
		v.add(scopeName);
		if(defProxy instanceof SDMSScope)
			v.add(s.getTypeAsString(sysEnv));
		else if (defProxy instanceof SDMSFolder)
			v.add("FOLDER");
		else	v.add(dse.getTypeAsString(sysEnv));
		v.add(s.getPrivileges(sysEnv).toString());
		v.add(rId);
		v.add(nr.pathVector(sysEnv));
		v.add(nr.getUsageAsString(sysEnv));
		v.add(SDMSGroupTable.getObject(sysEnv, r.getOwnerId(sysEnv)).getName(sysEnv));
		v.add(r.getPrivileges(sysEnv).toString());
		v.add(rsd == null ? null : rsd.getName(sysEnv));
		Long ots = r.getRsdTime(sysEnv);
		if(ots != null &&
		   nr.getUsage(sysEnv).intValue() == SDMSNamedResource.SYNCHRONIZING &&
		   nr.getRspId(sysEnv) != null) {
			ts = ots.longValue();
			dts = new Date();
			dts.setTime(ts);
			v.add(sysEnv.systemDateFormat.format(dts));
		} else v.add(null);
		Integer someAmount;
		someAmount = r.getRequestableAmount(sysEnv);
		v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
		someAmount = r.getAmount(sysEnv);
		v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
		someAmount = r.getFreeAmount(sysEnv);
		v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);

		try {
			switch(nr.getUsage(sysEnv).intValue()) {
				case SDMSNamedResource.STATIC:
					rr = null;
					break;
				case SDMSNamedResource.SYSTEM:
					try {
						rr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(seId, nrId), actVersion);
					} catch (NotFoundException nfe) {
						rr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(se.getFpId(sysEnv), nrId), actVersion);
					}
					break;
				case SDMSNamedResource.SYNCHRONIZING:
					rr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(seId, nrId), actVersion);
					break;
			}
		} catch (NotFoundException nfe) {

		}
		if(rr == null) {
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(Boolean.FALSE);
			v.add(Boolean.FALSE);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(r.getIsOnline(sysEnv));
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(scopeName);
		} else {
			v.add(rr.getAmount(sysEnv));
			v.add(rr.getLockmodeAsString(sysEnv));
			Vector rsv = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, rr.getId(sysEnv));
			String rsn = new String();
			String sep = "";
			int size = rsv.size();
			for(int i = 0; i < size; i++) {
				SDMSResourceReqStates rrs = (SDMSResourceReqStates) rsv.get(i);
				rsn = rsn + sep + SDMSResourceStateDefinitionTable.getObject(sysEnv, rrs.getRsdId(sysEnv)).getName(sysEnv);
				sep = ",";
			}
			v.add(rsn);

			try {
				ra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv, new SDMSKey(smeId, rId, rr.getStickyName(sysEnv)));
				if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.ALLOCATION) {
					v.add(new Integer(0));
					v.add(ra.getAmount(sysEnv));
					v.add(ra.getLockmodeAsString(sysEnv));
				} else if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.RESERVATION) {
					v.add(ra.getAmount(sysEnv));
					v.add(new Integer(0));
					v.add(ra.getLockmodeAsString(sysEnv));
				} else {
					v.add(new Integer(0));
					v.add(new Integer(0));
					v.add(null);
				}

			} catch (NotFoundException nfe) {
				try {
					ra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv, new SDMSKey(new Long(- smeId.longValue()), rId, rr.getStickyName(sysEnv)));
					v.add(ra.getAmount(sysEnv));
					v.add(new Integer(0));
					v.add(ra.getLockmodeAsString(sysEnv));
				} catch (NotFoundException nfe2) {
					ra = null;
					v.add(null);
					v.add(null);
					v.add(null);
				}
			}
			v.add(Boolean.FALSE);
			v.add(rr.getIsSticky(sysEnv));
			v.add(rr.getStickyName(sysEnv));
			Long stickyParent;
			SDMSSchedulingEntity spse = null;
			if (ra == null) {
				stickyParent = rr.getStickyParent(sysEnv);
				v.add(stickyParent);
				if (stickyParent != null)
					spse = SDMSSchedulingEntityTable.getObject(sysEnv, stickyParent, actVersion);
			} else {
				stickyParent = ra.getStickyParent(sysEnv);
				v.add(stickyParent);
				if (stickyParent != null) {
					final SDMSSubmittedEntity spsme = SDMSSubmittedEntityTable.getObject(sysEnv, stickyParent);
					spse = SDMSSchedulingEntityTable.getObject(sysEnv, spsme.getSeId(sysEnv), actVersion);
				}
			}
			if (spse != null)
				v.add(spse.getTypeAsString(sysEnv));
			else
				v.add(null);
			v.add(r.getIsOnline(sysEnv));
			if(ra != null) {
				if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.ALLOCATION) {
					v.add(ALLOCATED);
				} else if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.RESERVATION) {
					v.add(RESERVED);
				} else if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.IGNORE) {
					v.add(IGNORE);
				} else if (ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.MASTER_RESERVATION) {
					v.add(ALLOCATED);
				} else {
					if(r.checkAllocate(sysEnv, rr, sme, ra) == SDMSResource.REASON_AVAILABLE) {
						if(SystemEnvironment.sched.isBlocked(sysEnv, smeId, r.getId(sysEnv))) {
							v.add(BLOCKED);
						} else {
							if(ra.getIsSticky(sysEnv).booleanValue()) {
								MasterReservationInfo mri =
									SystemEnvironment.sched.checkMasterReservation(sysEnv, sme, rr, ra.getStickyParent(sysEnv), r);
								if(mri.canAllocate)	v.add(AVAILABLE);
								else			v.add(BLOCKED);
							} else {
								v.add(AVAILABLE);
							}
						}
					} else {
						v.add(BLOCKED);
					}
				}
			} else {
				if(r.checkAllocate(sysEnv, rr, sme, ra) == SDMSResource.REASON_AVAILABLE) {
					v.add(AVAILABLE);
				} else {
					v.add(BLOCKED);
				}
			}
			Integer expBase = rr.getExpiredBase(sysEnv);
			if(expBase != null) {
				if(nr.getRspId(sysEnv) != null) {
					long expTime = rr.getExpiredAmount(sysEnv).longValue();
					switch(expBase.intValue()) {
						case SDMSInterval.MINUTE: expTime *= SDMSInterval.MINUTE_DUR; break;
						case SDMSInterval.HOUR:   expTime *= SDMSInterval.HOUR_DUR;   break;
						case SDMSInterval.DAY:    expTime *= SDMSInterval.DAY_DUR;    break;
						case SDMSInterval.WEEK:   expTime *= SDMSInterval.WEEK_DUR;   break;
						case SDMSInterval.MONTH:  expTime *= SDMSInterval.MONTH_DUR;  break;
						case SDMSInterval.YEAR:   expTime *= SDMSInterval.YEAR_DUR;   break;
					}
					dts = new java.util.Date();
					String expSign;
					if(expTime == 0) {
						ts = sme.getSyncTs(sysEnv).longValue();
						expSign = "+";
					} else {
						if (expTime > 0) {
							ts = dts.getTime() - expTime;
							expSign = "+";
						} else {
							ts = dts.getTime() + expTime;
							expSign = "-";
						}
					}
					dts.setTime(ts);
					v.add(sysEnv.systemDateFormat.format(dts));
					v.add(expSign);
					v.add(rr.getIgnoreOnRerun(sysEnv));
				} else {
					v.add(null);
					v.add(null);
					v.add(null);
				}
			} else {
				v.add(null);
				v.add(null);
				v.add(null);
			}

			if(defProxy instanceof SDMSScope)
				v.add(scopeName);
			else if(defProxy instanceof SDMSFolder)
				v.add(f.pathVector(sysEnv));
			else	v.add(dse.pathVector(sysEnv));
		}

		return v;
	}
}
