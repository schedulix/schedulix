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

public abstract class ManipJob extends Node
{
	protected String message;
	protected WithHash with		= null;
	protected Long jobId		= null;
	protected Boolean clone		= null;
	protected String execPid	= null;
	protected String extPid		= null;
	protected Integer status	= null;
	protected String exitState	= null;
	protected Boolean exitStateForce= null;
	protected String ts		= null;
	protected Integer exitCode	= null;
	protected String errText	= null;
	protected Boolean suspend	= null;
	protected Boolean adminSuspend	= null;
	protected Boolean localSuspend	= null;
	protected Object resumeObj	= null;
	protected String runProgram	= null;
	protected String rerunProgram	= null;
	protected Boolean rerun		= null;
	protected Integer rerunSeq	= null;
	protected Boolean kill		= null;
	protected Boolean cancel	= null;
	protected Boolean disable	= null;
	protected Long tsLong		= null;
	protected Vector depsToIgnore	= null;
	protected Integer priority	= null;
	protected Integer nicevalue	= null;
	protected Integer renice	= null;
	protected String comment	= null;
	protected Vector resToIgnore	= null;
	protected Vector nrsToIgnore	= null;
	protected Boolean clearWarning	= null;
	protected String warning	= null;

	public ManipJob()
	{
		super();
	}

	protected SDMSSystemMessage createSystemMessage(SystemEnvironment sysEnv, int msgType, Long smeId, Long masterId, int operation, boolean isMandatory, Long uid, String comment, Long additionalLong, Boolean additionalBool, Long secondLong, String opComment)
	throws SDMSException
	{
		return SDMSSystemMessageTable.table.create(sysEnv, new Integer(msgType), smeId, masterId, new Integer(operation), new Boolean(isMandatory), uid, new Long ((new Date()).getTime()), comment, additionalLong, additionalBool, secondLong, opComment);
	}

	protected void setSomeFields(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Integer status)
	throws SDMSException
	{
		sme.setState(sysEnv, status);
		if(with.containsKey(ParseStr.S_ERROR_TEXT))	sme.setErrorMsg(sysEnv, errText);
		if(with.containsKey(ParseStr.S_EXEC_PID))	sme.setPid(sysEnv, execPid);
		if(with.containsKey(ParseStr.S_EXT_PID))	sme.setExtPid(sysEnv, extPid);
	}

	protected void delFromQueue(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		try {
			SDMSRunnableQueue rq = SDMSRunnableQueueTable.idx_smeId_scopeId_getUniqueForUpdate(sysEnv, new SDMSKey(sme.getId(sysEnv), sysEnv.cEnv.uid()));
			rq.delete(sysEnv);
		} catch (NotFoundException nfe) {
		}
	}

	protected void delFromQueue(SystemEnvironment sysEnv, SDMSKillJob kj)
	throws SDMSException
	{
		try {
			SDMSRunnableQueue rq = SDMSRunnableQueueTable.idx_smeId_scopeId_getUnique(sysEnv, new SDMSKey(kj.getId(sysEnv), sysEnv.cEnv.uid()));
			rq.delete(sysEnv);
		} catch (NotFoundException nfe) {
		}
	}

	protected void changeState(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, boolean force, Integer status)
	throws SDMSException
	{
		switch(status.intValue()) {
			case SDMSSubmittedEntity.STARTED:
				setSomeFields(sysEnv, sme, status);
				if(tsLong != null)
					sme.setStartTs(sysEnv, tsLong);
				delFromQueue(sysEnv, sme);
				break;
			case SDMSSubmittedEntity.RUNNING:
				int oldState = sme.getState(sysEnv);
				setSomeFields(sysEnv, sme, status);
				if (oldState != SDMSSubmittedEntity.STARTED && tsLong != null)
					sme.setStartTs(sysEnv, tsLong);
				delFromQueue(sysEnv, sme);
				break;
			case SDMSSubmittedEntity.FINISHED:
				if(exitCode == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv,
					                               "03212102041", "you cannot finish a job without an exit code"));
				}
				sme.finishJob(sysEnv, exitCode, errText, tsLong);

				int newState = sme.getState(sysEnv);
				if (newState == SDMSSubmittedEntity.FINISHED || newState == SDMSSubmittedEntity.FINAL)
					delFromQueue(sysEnv, sme);
				break;
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				setSomeFields(sysEnv, sme, status);
				break;
			case SDMSSubmittedEntity.BROKEN_FINISHED:
				sme.releaseResources(sysEnv, status.intValue());
				setSomeFields(sysEnv, sme, status);
				break;
			case SDMSSubmittedEntity.ERROR:
				sme.releaseResources(sysEnv, status.intValue());
				sme.setToError(sysEnv, errText);
				break;
			default:
				if(force) {
					sme.setState(sysEnv, status);
				}
				break;
		}
	}

	protected void setExitState(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long actVersion, Long esdId, Boolean force)
	throws SDMSException
	{
		String oldExitState;
		if (force == null) force = Boolean.FALSE;
		exitState = SDMSExitStateDefinitionTable.getObject(sysEnv, esdId, actVersion).getName(sysEnv);
		jobId = sme.getId(sysEnv);

		Long espId = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getEspId(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		SDMSExitState es;
		int state = sme.getState(sysEnv).intValue();

		if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03403091701", "You can only change the exit state of a job, $1 is a $2",
			                               se.pathString(sysEnv), se.getTypeAsString(sysEnv)));
		}

		boolean isSuspended = (sme.getIsSuspended(sysEnv).intValue() != SDMSSubmittedEntity.NOSUSPEND);
		if((state != SDMSSubmittedEntity.FINISHED && state != SDMSSubmittedEntity.BROKEN_FINISHED && state != SDMSSubmittedEntity.ERROR && !isSuspended) ||
		    (state == SDMSSubmittedEntity.ERROR && !sme.getJobIsRestartable(sysEnv).booleanValue()) ||
		    (isSuspended && (state == SDMSSubmittedEntity.STARTING || state == SDMSSubmittedEntity.STARTED ||
		                     state == SDMSSubmittedEntity.RUNNING || state == SDMSSubmittedEntity.TO_KILL ||
		                     state == SDMSSubmittedEntity.KILLED || state == SDMSSubmittedEntity.BROKEN_ACTIVE))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207082043", "you can only set a state for a (broken) finished  or a suspended not active job"));
		}
		if(state != SDMSSubmittedEntity.BROKEN_FINISHED  && state != SDMSSubmittedEntity.ERROR) {
			try {
				Long jobEsdId = sme.getJobEsdId(sysEnv);
				if (jobEsdId != null) {
					es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, jobEsdId), actVersion);
					if(sme.getJobIsFinal(sysEnv).booleanValue()) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03207082044",
						                               "you can only set a state for a job in a nonfinal state"));
					}
					oldExitState = SDMSExitStateDefinitionTable.getObject(sysEnv, sme.getJobEsdId(sysEnv), actVersion).getName(sysEnv);
				} else {
					oldExitState = "N/A";
				}
			} catch(NotFoundException nfe) {
				throw new FatalException(new SDMSMessage(sysEnv, "03207082059", "Actual exit state is not part of the profile"));
			}
		} else {
			oldExitState = "N/A";
		}
		try {
			es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, esdId), actVersion);
		} catch(NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207051918", "Exit State $1 is not part of the profile", exitState));
		}

		if(!force.booleanValue()) {
			Long esmpId = se.getEsmpId(sysEnv);
			if(esmpId == null) {
				SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, espId, actVersion);
				esmpId = esp.getDefaultEsmpId(sysEnv);
				if(esmpId == null) {
					SDMSMessage m = new SDMSMessage(sysEnv, "03403090928",
					                                "Couldn't determine the exit state mapping for job definition $1",
					                                se.pathString(sysEnv));
					SDMSThread.doTrace(sysEnv.cEnv, m.toString(), SDMSThread.SEVERITY_ERROR);
					throw new CommonErrorException(m);
				}
			}
			if(!SDMSExitStateMappingTable.idx_esmpId_esdId.containsKey(sysEnv, new SDMSKey(esmpId, esdId), actVersion)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03403090958",
				                               "A mapping to exit state $1 doesn't exist, use force if you really want this", exitState));
			}
		}

		sme.changeState(sysEnv, esdId, es, sme.getExitCode(sysEnv), errText, null, false);
	}

	void performReject(SystemEnvironment sysEnv, SDMSSystemMessage msg, SDMSSubmittedEntity sme)
	throws SDMSException
	{
	}

	void performClone(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Boolean shouldSuspend)
	throws SDMSException
	{
		SDMSSubmittedEntity psme = SDMSSubmittedEntityTable.getObject(sysEnv, sme.getParentId(sysEnv));
		String childTag = "C_" + sysEnv.tx.txId;
		Long replaceId = sme.getId (sysEnv);
		Long submitSeId = sme.getSeId(sysEnv);
		SDMSSubmittedEntity childSme = psme.submitChild(sysEnv,
		                               null,
		                               new Integer (SDMSSubmittedEntity.SUSPEND),
		                               null,
		                               submitSeId,
		                               childTag,
		                               replaceId,
		                               null,
		                               true
		                                               );
		if (childSme.getIsDisabled(sysEnv).booleanValue()) {
			childSme.disable(sysEnv, Boolean.FALSE);
		} else {
			if (!shouldSuspend.booleanValue()) {
				childSme.resume(sysEnv, true);
			}
		}
	}

	void performCancel(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		sme.cancel(sysEnv);
	}

	void performRerun(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Boolean rerun)
	throws SDMSException
	{
		if(rerun.booleanValue()) {
			sme.rerunRecursive(sysEnv, sme.getId(sysEnv), message, true);
		} else {
			sme.rerun(sysEnv);
		}
	}

	void performDisable(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Boolean disable)
	throws SDMSException
	{
		sme.disable(sysEnv, disable);
	}

	void performSetState(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Long esdId, Boolean force)
	throws SDMSException
	{
		setExitState(sysEnv, sme, sme.getSeVersion(sysEnv), esdId, force);
	}

	void performIgnDep(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Long diId, Boolean recursive)
	throws SDMSException
	{
		SDMSDependencyInstance di = SDMSDependencyInstanceTable.getObject(sysEnv, diId);
		di.setIgnore(sysEnv, (recursive.booleanValue()? SDMSDependencyInstance.RECURSIVE : SDMSDependencyInstance.YES), sme.getId(sysEnv), message);
	}

	void performIgnRss(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Long rId)
	throws SDMSException
	{
		SDMSResource r = SDMSResourceTable.getObject(sysEnv, rId);
		SDMSResourceAllocation ra = null;
		boolean raFound = false;

		Vector rav = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(sme.getId(sysEnv), r.getNrId(sysEnv)));
		for (int j = 0; j < rav.size(); ++j) {
			ra = (SDMSResourceAllocation) rav.get(j);
			if (ra.getRId(sysEnv).equals(rId)) {
				raFound = true;
				break;
			}
		}
		if (raFound) {
			ra.ignore(sysEnv);
		}
	}

	void performSuspend(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Boolean suspend, boolean isLocal, boolean isAdmin, Long resumeTs)
	throws SDMSException
	{
		if (suspend != null) {
			if(suspend.booleanValue()) {
				sme.suspend(sysEnv, isLocal, isAdmin);
				SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.SUSPEND);
			} else {
				if (resumeTs == null) {
					sme.resume(sysEnv, isAdmin);
					SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.RESUME);
				}
			}
		}
		if (resumeTs != null) {
			sme.setResumeTs(sysEnv, resumeTs);
		}
	}

	void performClrWarn(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
	}

	void performSetWarn(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
	}

	void performPriority(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Boolean isRenice, Integer priority)
	throws SDMSException
	{
		if (isRenice) {
			sme.renice(sysEnv, priority.intValue(), null, message);
		} else {
			sme.setPriority(sysEnv, priority.intValue());
		}
	}

	void performModParm(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, String name, String value)
	throws SDMSException
	{
		sme.setVariableValue(sysEnv, name, value);
	}

	void performKill(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		sme.kill(sysEnv);
	}

	void performSetJobState(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		changeState(sysEnv, sme, true, status);
	}

	public abstract void go(SystemEnvironment sysEnv)
	throws SDMSException;
}

