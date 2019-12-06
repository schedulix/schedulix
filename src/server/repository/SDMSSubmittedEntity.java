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
import java.lang.*;
import java.util.*;
import java.text.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.util.*;

public class SDMSSubmittedEntity extends SDMSSubmittedEntityProxyGeneric
	implements SDMSOwnedObject
{

	protected static final int NOTYET = 0;
	protected static final int FULFILLED = 1;
	protected static final int UNREACHABLE_CANCELLED = 2;
	protected static final int BROKEN = 3;
	protected static final int UNRESOLVED_SUSPEND = 4;

	public static final int STAT_NONE = 0;
	public static final int STAT_DEPENDENCY_WAIT = 1;
	public static final int STAT_SUSPEND = 2;
	public static final int STAT_SYNCHRONIZE = 3;
	public static final int STAT_RESOURCE = 4;
	public static final int STAT_JOBSERVER = 5;
	public static final int STAT_RESTARTABLE = 6;
	public static final int STAT_CHILD_WAIT = 7;

	public static final String S_JOBID		= "JOBID";
	public static final String S_SEID		= "SEID";
	public static final String S_MASTERID		= "MASTERID";
	public static final String S_KEY		= "KEY";
	public static final String S_PID		= "PID";
	public static final String S_LOGFILE		= "LOGFILE";
	public static final String S_ERRORLOG		= "ERRORLOG";
	public static final String S_WORKDIR		= "WORKDIR";
	public static final String S_SDMSHOST		= "SDMSHOST";
	public static final String S_SDMSPORT		= "SDMSPORT";
	public static final String S_JOBNAME		= "JOBNAME";
	public static final String S_JOBTAG		= "JOBTAG";
	public static final String S_TRNAME		= "TRIGGERNAME";
	public static final String S_TRTYPE		= "TRIGGERTYPE";
	public static final String S_TRBASE		= "TRIGGERBASE";
	public static final String S_TRBASEID		= "TRIGGERBASEID";
	public static final String S_TRBASEJOBID	= "TRIGGERBASEJOBID";
	public static final String S_TRORIGIN		= "TRIGGERORIGIN";
	public static final String S_TRORIGINID		= "TRIGGERORIGINID";
	public static final String S_TRORIGINJOBID	= "TRIGGERORIGINJOBID";
	public static final String S_TRREASON		= "TRIGGERREASON";
	public static final String S_TRREASONID		= "TRIGGERREASONID";
	public static final String S_TRREASONJOBID	= "TRIGGERREASONJOBID";
	public static final String S_TRSEQ		= "TRIGGERSEQNO";
	public static final String S_TROSTATE		= "TRIGGEROLDSTATE";
	public static final String S_TRNSTATE		= "TRIGGERNEWSTATE";
	public static final String S_TRWARNING		= "TRIGGERWARNING";
	public static final String S_SUBMITTS		= "SUBMITTIME";
	public static final String S_STARTTS		= "STARTTIME";
	public static final String S_EXPRUNTIME		= "EXPRUNTIME";
	public static final String S_EXPFINALTIME	= "EXPFINALTIME";
	public static final String S_JOBSTATE		= "JOBSTATE";
	public static final String S_MERGEDSTATE	= "MERGEDSTATE";
	public static final String S_PARENTID		= "PARENTID";
	public static final String S_STATE		= "STATE";
	public static final String S_ISRESTARTABLE	= "ISRESTARTABLE";
	public static final String S_SYNCTS		= "SYNCTIME";
	public static final String S_RESOURCETS		= "RESOURCETIME";
	public static final String S_RUNNABLETS		= "RUNNABLETIME";
	public static final String S_FINISHTS		= "FINISHTIME";
	public static final String S_SYSDATE		= "SYSDATE";
	public static final String S_WARNING		= "LAST_WARNING";
	public static final String S_RERUNSEQ		= "RERUNSEQ";
	public static final String S_SCOPENAME		= "SCOPENAME";
	public static final String S_IDLE_TIME		= "IDLE_TIME";
	public static final String S_DEPENDENCY_WAIT_TIME	= "DEPENDENCY_WAIT_TIME";
	public static final String S_SUSPEND_TIME	= "SUSPEND_TIME";
	public static final String S_SYNC_TIME		= "SYNC_TIME";
	public static final String S_RESOURCE_TIME	= "RESOURCE_TIME";
	public static final String S_JOBSERVER_TIME	= "JOBSERVER_TIME";
	public static final String S_RESTARTABLE_TIME	= "RESTARTABLE_TIME";
	public static final String S_CHILD_WAIT_TIME	= "CHILD_WAIT_TIME";
	public static final String S_PROCESS_TIME	= "PROCESS_TIME";
	public static final String S_ACTIVE_TIME	= "ACTIVE_TIME";
	public static final String S_IDLE_PCT		= "IDLE_PCT";
	public static final String S_SUBMITTER		= "SUBMITTER";
	public static final String S_SUBMITGROUP	= "SUBMITGROUP";
	public static final String S_SEOWNER		= "SEOWNER";
	public static final String S_ENVIRONMENT	= "ENVIRONMENT";

	protected static final Integer zero = new Integer(0);
	protected static final Long lzero = new Long(0);

	protected static final VariableResolver SVR = new SmeVariableResolver();

	protected final static HashMap mapper = new HashMap();

	static
	{
		mapper.put(new Integer(Parser.SUBMITTED),		new Integer(SDMSSubmittedEntity.SUBMITTED));
		mapper.put(new Integer(Parser.DEPENDENCY_WAIT),		new Integer(SDMSSubmittedEntity.DEPENDENCY_WAIT));
		mapper.put(new Integer(Parser.SYNCHRONIZE_WAIT),	new Integer(SDMSSubmittedEntity.SYNCHRONIZE_WAIT));
		mapper.put(new Integer(Parser.RESOURCE_WAIT),		new Integer(SDMSSubmittedEntity.RESOURCE_WAIT));
		mapper.put(new Integer(Parser.RUNNABLE),		new Integer(SDMSSubmittedEntity.RUNNABLE));
		mapper.put(new Integer(Parser.STARTING),		new Integer(SDMSSubmittedEntity.STARTING));
		mapper.put(new Integer(Parser.STARTED),			new Integer(SDMSSubmittedEntity.STARTED));
		mapper.put(new Integer(Parser.RUNNING),			new Integer(SDMSSubmittedEntity.RUNNING));
		mapper.put(new Integer(Parser.FINISHED),		new Integer(SDMSSubmittedEntity.FINISHED));
		mapper.put(new Integer(Parser.FINAL),			new Integer(SDMSSubmittedEntity.FINAL));
		mapper.put(new Integer(Parser.BROKEN_ACTIVE),		new Integer(SDMSSubmittedEntity.BROKEN_ACTIVE));
		mapper.put(new Integer(Parser.BROKEN_FINISHED),		new Integer(SDMSSubmittedEntity.BROKEN_FINISHED));
		mapper.put(new Integer(Parser.ERROR_TOKEN),		new Integer(SDMSSubmittedEntity.ERROR));
		mapper.put(new Integer(Parser.TO_KILL),			new Integer(SDMSSubmittedEntity.TO_KILL));
		mapper.put(new Integer(Parser.KILLED),			new Integer(SDMSSubmittedEntity.KILLED));
		mapper.put(new Integer(Parser.CANCELLED),		new Integer(SDMSSubmittedEntity.CANCELLED));
		mapper.put(new Integer(Parser.UNREACHABLE),		new Integer(SDMSSubmittedEntity.UNREACHABLE));
		mapper.put(new Integer(Parser.SUSPENDED),		new Integer(SDMSSubmittedEntity.SUSPENDED));
	}

	protected SDMSSubmittedEntity(SDMSObject p_object)
	{
		super(p_object);
	}

	public static Integer map(Integer p)
	{
		return (Integer) mapper.get(p);
	}

	public String getSubmitPathString(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return getSubmitPathString(sysEnv, false);
	}

	public String getSubmitPathString(SystemEnvironment sysEnv, boolean withChildTag)
		throws SDMSException
	{
		Long seId;
		long actVersion;
		SDMSSchedulingEntity se;
		Long parentId;
		String pathString = "";
		SDMSSubmittedEntity sme = this;

		do {
			seId = sme.getSeId (sysEnv);

			actVersion = sme.getSeVersion(sysEnv).longValue();

			se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, actVersion);

			if (! pathString.equals (""))
				pathString = ":" + pathString;

			if (withChildTag) {
				String childTag = sme.getChildTag(sysEnv);
				if (childTag != null && ! childTag.equals("")) {
					pathString = "[" + childTag + "]" + pathString;
				}
			}
			pathString = se.pathString (sysEnv, actVersion) + pathString;

			parentId = sme.getParentId (sysEnv);

			if (parentId != null) {
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, parentId);
			}
		} while (parentId != null);

		return pathString;

	}

	public void suspend (SystemEnvironment sysEnv, boolean local, boolean admin)
	throws SDMSException
	{
		suspend (sysEnv, local, admin, true);
	}

	public void suspend (SystemEnvironment sysEnv, boolean local, boolean admin, boolean operator)
		throws SDMSException
	{
		int state = getState(sysEnv).intValue();
		if (state == FINAL || state == CANCELLED || getIsCancelled(sysEnv).booleanValue()) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02201121213",
				"Cannot suspend an already final (or cancelled) submitted entity"));
		}
		suspend (sysEnv, true, local, admin, operator);
	}

	public void resume (SystemEnvironment sysEnv, boolean admin)
	throws SDMSException
	{
		resume (sysEnv, admin, true);
	}

	public void resume (SystemEnvironment sysEnv, boolean admin, boolean operator)
		throws SDMSException
	{

		setResumeTs(sysEnv, null);
		suspend (sysEnv, false, false, admin, operator);
		Long esdId = getJobEsdId(sysEnv);
		if (esdId != null) {
			long version = getSeVersion(sysEnv).longValue();
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), version);
			Long espId = se.getEspId(sysEnv);
			SDMSExitState es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, esdId), version);
			if (es.getIsFinal(sysEnv).booleanValue()) {
				setJobIsFinal(sysEnv, Boolean.TRUE);
				checkDependents(sysEnv);
			}
		}
		checkFinal(sysEnv);
	}

	public void rerun (SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (!getJobIsRestartable(sysEnv).booleanValue()) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "03205191052",
				"Submitted entity not rerunable"));
		}
		rerunEntity(sysEnv);
	}

	public boolean rerunEntity (SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (getJobIsRestartable(sysEnv).booleanValue()) {
			int state = getState(sysEnv).intValue();
			if(state != ERROR) {
				Integer rerunSeq = getRerunSeq(sysEnv);
				SDMSSubmittedEntityStatsTable.table.create(sysEnv,
				                getId(sysEnv),
				                rerunSeq,
				                getScopeId(sysEnv),
				                getJobEsdId(sysEnv),
				                getExitCode(sysEnv),
				                getCommandline(sysEnv),
				                getWorkdir(sysEnv),
				                getLogfile(sysEnv),
				                getErrlogfile(sysEnv),
				                getExtPid(sysEnv),
				                getSyncTs(sysEnv),
				                getResourceTs(sysEnv),
				                getRunnableTs(sysEnv),
				                getStartTs(sysEnv),
				                getFinishTs(sysEnv)
				                                          );
				setRerunSeq(sysEnv, new Integer(rerunSeq.intValue() + 1));
			}
			setStartTs(sysEnv, null);
			setFinishTs(sysEnv, null);

			setErrorMsg(sysEnv, null);
			setJobEsdId(sysEnv, null, false);
			setJobEsdPref(sysEnv, null);

			setState(sysEnv, new Integer(DEPENDENCY_WAIT));

			SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.RERUN);
			return true;
		}
		return false;
	}

	public void rerunRecursive (SystemEnvironment sysEnv, Long originId, String comment, boolean topLevel)
		throws SDMSException
	{
		boolean rerun = rerunEntity(sysEnv);
		if (getState(sysEnv).intValue() != FINAL) {
			Vector v_hi = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
			Iterator i_hi = v_hi.iterator();
			while (i_hi.hasNext()) {
				SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i_hi.next();
				SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
				sme.rerunRecursive(sysEnv, originId, comment, false);
			}
		}
	}

	public void kill (SystemEnvironment sysEnv)
		throws SDMSException
	{
		int state = getState(sysEnv).intValue();

		if (state != RUNNING && state != KILLED && state != BROKEN_ACTIVE) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "03207081340",
				"Cannot kill a submitted entity which is not in a running State"));
		}

		Date dts = new Date();
		Long ts = new Long (dts.getTime());
		long actVersion = getSeVersion(sysEnv).longValue();
		String cmdLine = (SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), actVersion)).getKillProgram(sysEnv);
		if(cmdLine == null) return;

		SDMSKillJob kj = SDMSKillJobTable.table.create(sysEnv, getSeId(sysEnv),
								       getSeVersion(sysEnv),
								       getId(sysEnv),
								       getScopeId(sysEnv),
								       new Integer(RUNNABLE),
								       null,
								       cmdLine,
								       getLogfile(sysEnv),
								       getErrlogfile(sysEnv),
								       null,
								       null,
								       null,
								       ts,
								       null,
								       null);

		Long scopeId = getScopeId(sysEnv);
		SDMSScope s = SDMSScopeTable.getObject(sysEnv, scopeId);
		SDMSRunnableQueueTable.table.create(sysEnv, kj.getId(sysEnv), getScopeId(sysEnv), kj.getState(sysEnv));

		setKillId(sysEnv, kj.getId(sysEnv));
		setState(sysEnv, new Integer(TO_KILL));
		s.notify(sysEnv);
	}

	private boolean isActive(SystemEnvironment sysEnv)
		throws SDMSException
	{
		int state = getState(sysEnv).intValue();
		if (state == STARTING ||
		    state == STARTED ||
		    state == RUNNING ||
		    state == TO_KILL ||
		    state == KILLED ||
		    state == BROKEN_ACTIVE ||
		    getCntStarting(sysEnv).intValue() +
		    getCntStarted(sysEnv).intValue() +
		    getCntRunning(sysEnv).intValue() +
		    getCntToKill(sysEnv).intValue() +
		    getCntKilled(sysEnv).intValue() +
		    getCntBrokenActive(sysEnv).intValue() > 0
		   )
			return true;
		else
			return false;
	}

	public void cancel (SystemEnvironment sysEnv)
		throws SDMSException
	{
		int state = getState(sysEnv).intValue();
		if (state == FINAL || state == CANCELLED) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02204260751",
					"Cannot cancel an already final (or cancelled) submitted entity"));
		}
		Long id = getId(sysEnv);
		Vector v_hi = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, id);
		Iterator i = v_hi.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			SDMSSubmittedEntity psme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
			psme.mergeExitStates(sysEnv, false, this );
		}
		do_cancel(sysEnv);
		i = v_hi.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			SDMSSubmittedEntity psme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
			psme.checkFinal(sysEnv);
		}
	}

	private void do_cancel (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long smeId = getId(sysEnv);

		setIsCancelled(sysEnv, Boolean.TRUE);

		boolean active = isActive(sysEnv);

		HashSet chs = (HashSet)sysEnv.tx.txData.get(SystemEnvironment.S_CANCEL_HASHSET);
		if (chs == null) {
			chs = new HashSet();
			sysEnv.tx.txData.put(SystemEnvironment.S_CANCEL_HASHSET, chs);
		}
		chs.add(getId(sysEnv));

		if (!active)
			releaseResources(sysEnv, CANCELLED);

		if (!active)
			setState(sysEnv, new Integer(CANCELLED));
		Vector v_sme = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, smeId);
		Iterator i_sme = v_sme.iterator();
		while (i_sme.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity)i_sme.next();
			if (sme.getState(sysEnv).intValue() != FINAL &&
			    sme.getState(sysEnv).intValue() != CANCELLED)
				sme.cancel(sysEnv);
		}
		setResumeTs(sysEnv, null);
		suspend (sysEnv, false, false, true, true, true);
		removeAsyncTrigger(sysEnv);
		if (!active) {
			deleteLocalResources(sysEnv);
			try {
				SDMSnpJobFootprint jfp = SDMSnpJobFootprintTable.idx_smeId_getUnique(sysEnv, getId(sysEnv));
				jfp.delete(sysEnv);
			} catch (NotFoundException nfe) {

			}
		}
	}

	public void doDeferredCancel (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		Vector v_hi = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, id);
		Iterator i = v_hi.iterator();
		boolean parentCancel = false;
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			SDMSSubmittedEntity psme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
			if (psme.getIsCancelled(sysEnv).booleanValue()) {
				parentCancel = true;
				psme.doDeferredCancel(sysEnv);
			}
		}
		if (!parentCancel) {
			if (getState(sysEnv).intValue() != FINAL &&
			    getState(sysEnv).intValue() != CANCELLED) {
				cancel(sysEnv);
			}
		}
	}

	public void disable(Boolean isDisable, SystemEnvironment sysEnv)
	throws SDMSException
	{
		if (isDisable.booleanValue()) {
			int state = getState(sysEnv).intValue();
			boolean finishedJob = false;
			if (state == FINISHED) {
				long seVersion = getSeVersion(sysEnv).longValue();
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), seVersion);
				int type = se.getType(sysEnv).intValue();
				if (type == SDMSSchedulingEntity.JOB)
					finishedJob = true;
			}
			if (
			        finishedJob ||
			        state != DEPENDENCY_WAIT ||
			        (state == DEPENDENCY_WAIT && getRerunSeq(sysEnv).intValue() > 0) ||
			        (
			                this.getCntRunnable(sysEnv) +
			                this.getCntStarting(sysEnv) +
			                this.getCntStarted(sysEnv) +
			                this.getCntRunning(sysEnv) +
			                this.getCntKilled(sysEnv) +
			                this.getCntFinished(sysEnv) +
			                this.getCntFinal(sysEnv) +
			                this.getCntBrokenActive(sysEnv) +
			                this.getCntBrokenFinished(sysEnv) +
			                this.getCntError(sysEnv) +
			                this.getCntUnreachable(sysEnv) +
			                this.getCntRestartable(sysEnv) +
			                this.getCntPending(sysEnv) +
			                this.getCntToKill(sysEnv) > 0
			        )
			) {
				throw new CommonErrorException (new SDMSMessage (sysEnv, "02809031333",
				                                "Cannot disable a cancelled or already active submitted entity"));
			}
			doDisable(sysEnv, true );
		} else {
			if (!getIsDisabled(sysEnv).booleanValue()) return;
			if (getIsParentDisabled(sysEnv).booleanValue()) {
				throw new CommonErrorException (new SDMSMessage (sysEnv, "03908251744",
				                                "Cannot enable a submitted entity which is not at top level of the disabled subtree"));
			}
			doEnable(sysEnv);
		}
	}

	public Boolean getIsParentDisabled (SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector pv = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, getId(sysEnv));
		for (int i = 0; i < pv.size(); ++i) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance) pv.get(i);
			SDMSSubmittedEntity pSme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
			if (pSme.getIsDisabled(sysEnv).booleanValue() || pSme.getIsParentDisabled(sysEnv).booleanValue()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public Boolean getDirectParentsAreDisabled (SystemEnvironment sysEnv)
	throws SDMSException
	{
		boolean parentsDisabled = true;
		Vector pv = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, getId(sysEnv));
		if (pv.size() == 0) return false;
		for (int i = 0; i < pv.size(); ++i) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance) pv.get(i);
			SDMSSubmittedEntity pSme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
			if (!pSme.getIsDisabled(sysEnv).booleanValue()) {
				parentsDisabled = false;
			}
		}
		return new Boolean(parentsDisabled);
	}

	private void doDisable(SystemEnvironment sysEnv, boolean root)
	throws SDMSException
	{
		SDMSSubmittedEntity sme = this;
		Long smeId = sme.getId(sysEnv);
		if (sme.getIsDisabled(sysEnv).booleanValue()) return;
		if (!root && !sme.getDirectParentsAreDisabled(sysEnv).booleanValue()) return;
		sme.setIsDisabled(sysEnv, Boolean.TRUE);
		sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.DEPENDENCY_WAIT));
		sme.checkDependencies(sysEnv);
		Vector v = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, smeId);
		Iterator i = v.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)(i.next());
			Long childId = hi.getChildId(sysEnv);
			SDMSSubmittedEntity child = SDMSSubmittedEntityTable.getObject(sysEnv, childId);
			child.doDisable(sysEnv, false);
		}
	}

	private void doEnable(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSSubmittedEntity sme = this;
		Long smeId = sme.getId(sysEnv);
		if (!sme.getIsDisabled(sysEnv).booleanValue()) return;
		int state = sme.getState(sysEnv).intValue();

		if (state != SDMSSubmittedEntity.DEPENDENCY_WAIT) {
			if (! (state == SDMSSubmittedEntity.CANCELLED && sme.getIsDisabled(sysEnv).booleanValue()))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "03908251740",
				                                "Cannot enable a submitted entity that is not in DEPENDENCY WAIT state"));
		}
		sme.setIsDisabled(sysEnv, Boolean.FALSE);
		sme.checkDependencies(sysEnv);
		Vector v = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, smeId);
		Iterator i = v.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)(i.next());
			Long childId = hi.getChildId(sysEnv);
			SDMSSubmittedEntity child = SDMSSubmittedEntityTable.getObject(sysEnv, childId);
			child.doEnable(sysEnv);
		}
	}

	private void checkDeferStall(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSSubmittedEntity sme = this;
		Long mSmeId = getMasterId(sysEnv);
		if (mSmeId != getId(sysEnv)) {
			sme = SDMSSubmittedEntityTable.getObject(sysEnv, mSmeId);
		}
		if (
			sme.getState(sysEnv).intValue()			!= CANCELLED	&&
			sme.getIsSuspended(sysEnv).intValue()		== NOSUSPEND	&&
			sme.getChildSuspended(sysEnv).intValue()	<= 0		&&
			sme.getJobIsFinal(sysEnv).booleanValue()	== true		&&
			sme.getCntSubmitted(sysEnv).intValue()		== 0 		&&
			sme.getCntDependencyWait(sysEnv).intValue()	> 0 		&&
			sme.getCntSynchronizeWait(sysEnv).intValue()	== 0		&&
			sme.getCntResourceWait(sysEnv).intValue()	== 0		&&
			sme.getCntRunnable(sysEnv).intValue()		== 0		&&
			sme.getCntStarting(sysEnv).intValue()		== 0		&&
			sme.getCntStarted(sysEnv).intValue()		== 0		&&
			sme.getCntRunning(sysEnv).intValue()		== 0		&&
			sme.getCntToKill(sysEnv).intValue()		== 0 		&&
			sme.getCntKilled(sysEnv).intValue()		== 0 		&&
			sme.getCntRestartable(sysEnv).intValue()	== 0 		&&
			sme.getCntBrokenActive(sysEnv).intValue()	== 0 		&&
			sme.getCntBrokenFinished(sysEnv).intValue()	== 0 		&&
			sme.getCntError(sysEnv).intValue()		== 0 		&&
			sme.getCntUnreachable(sysEnv).intValue()	== 0
		) {
			Vector dwSme_v = SDMSSubmittedEntityTable.idx_masterId.getVector(sysEnv, mSmeId,
				new SDMSFilter() {
					public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
						if (((SDMSSubmittedEntity)obj).getState(sysEnv).intValue() == DEPENDENCY_WAIT)
							return true;
						else
							return false;
					}
				});
			for(int i = 0; i < dwSme_v.size(); i++) {
				SDMSSubmittedEntity dwSme = (SDMSSubmittedEntity) dwSme_v.get(i);
				if (dwSme.getIsSuspended(sysEnv).intValue() != NOSUSPEND) {
					return;
				}
			}
			Vector vTest = new Vector();
			for(int i = 0; i < dwSme_v.size(); i++) {
				SDMSSubmittedEntity dwSme = (SDMSSubmittedEntity) dwSme_v.get(i);
				boolean open = false;
				boolean deferred = false;
				Vector v_di = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, dwSme.getId(sysEnv));
				for(int j = 0; j < v_di.size(); j++) {
					SDMSDependencyInstance di = (SDMSDependencyInstance)v_di.get(j);
					if (di.getState(sysEnv).intValue() == SDMSDependencyInstance.OPEN) {
						open = true;
						break;
					}
				}
				if (open)
					continue;
				for(int j = 0; j < v_di.size(); j++) {
					SDMSDependencyInstance di = (SDMSDependencyInstance)v_di.get(j);
					if (di.getState(sysEnv).intValue() == SDMSDependencyInstance.DEFERRED && di.getRequiredSeId(sysEnv) == null) {
						SDMSDependencyDefinition dd = SDMSDependencyDefinitionTable.getObject(sysEnv, di.getDdId(sysEnv), dwSme.getSeVersion(sysEnv));
						if (dd.getUnresolvedHandling(sysEnv).intValue() != SDMSDependencyDefinition.DEFER_IGNORE) {
							di.setState(sysEnv, SDMSDependencyInstance.FAILED);
						}
						deferred = true;
					}
				}
				if (deferred)
					vTest.add(dwSme);
			}
			for(int i = 0; i < vTest.size(); i++) {
				SDMSSubmittedEntity dwSme = (SDMSSubmittedEntity) vTest.get(i);
				dwSme.testDependencies(sysEnv);
			}
		}

	}

	private boolean canFinalize(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (
			getState(sysEnv).intValue()		!= CANCELLED	&&
			getIsSuspended(sysEnv).intValue()	== NOSUSPEND	&&
			getParentSuspended(sysEnv).intValue()	== 0		&&
			getChildSuspended(sysEnv).intValue()	<= 0		&&
			getJobIsFinal(sysEnv).booleanValue()	== true		&&
			getCntSubmitted(sysEnv).intValue()	== 0 		&&
			getCntDependencyWait(sysEnv).intValue()	== 0 		&&
			getCntSynchronizeWait(sysEnv).intValue()== 0		&&
			getCntResourceWait(sysEnv).intValue()   == 0		&&
			getCntRunnable(sysEnv).intValue()	== 0		&&
			getCntStarting(sysEnv).intValue()	== 0		&&
			getCntStarted(sysEnv).intValue()	== 0		&&
			getCntRunning(sysEnv).intValue()	== 0		&&
			getCntToKill(sysEnv).intValue()		== 0 		&&
			getCntKilled(sysEnv).intValue()		== 0 		&&
			getCntFinished(sysEnv).intValue()	== 0 		&&
			getCntBrokenActive(sysEnv).intValue()	== 0 		&&
			getCntBrokenFinished(sysEnv).intValue()	== 0 		&&
			getCntError(sysEnv).intValue()		== 0 		&&
			getCntUnreachable(sysEnv).intValue()	== 0
		) {
			return true;
		}
		return false;
	}

	private Long getDefaultEsdId(SystemEnvironment sysEnv)
	throws SDMSException
	{
		int pref = 0;
		int pref1 = 0;
		Long esdId = null;
		Long esdId1 = null;
		long actVersion = getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), actVersion);
		Vector v = SDMSExitStateTable.idx_espId.getVector(sysEnv, se.getEspId(sysEnv), actVersion);
		Iterator i = v.iterator();
		while (i.hasNext()) {
			SDMSExitState es = (SDMSExitState)i.next();
			if (es.getIsBatchDefault(sysEnv).booleanValue()) {
				esdId1 = es.getEsdId(sysEnv);
				break;
			}
			if (pref1 < es.getPreference(sysEnv).intValue() && es.getIsFinal(sysEnv).booleanValue() && !(es.getIsUnreachable(sysEnv).booleanValue())) {
				pref1 = es.getPreference(sysEnv).intValue();
				esdId1 = es.getEsdId(sysEnv);
			}
			if (pref < es.getPreference(sysEnv).intValue() && es.getIsFinal(sysEnv).booleanValue()) {
				pref = es.getPreference(sysEnv).intValue();
				esdId = es.getEsdId(sysEnv);
			}
		}
		if (esdId1 != null)
			esdId = esdId1;

		return esdId;
	}

	public void checkFinal (SystemEnvironment sysEnv)
	throws SDMSException
	{
		if (getState(sysEnv).intValue() == CANCELLED || getState(sysEnv).intValue() == FINAL) {
			return;
		}

		boolean cf = canFinalize(sysEnv);

		if (cf) {
			if (getFinalEsdId(sysEnv) == null) {
				setFinalEsdId(sysEnv, getDefaultEsdId(sysEnv));
			}
			trigger (sysEnv, SDMSTrigger.BEFORE_FINAL);
			cf = canFinalize(sysEnv);
		}

		if (cf) {
			trigger (sysEnv, SDMSTrigger.AFTER_FINAL);

			releaseResources(sysEnv, FINAL);

			setState(sysEnv, new Integer(FINAL));
			removeAsyncTrigger(sysEnv);
			deleteLocalResources(sysEnv);
			try {
				SDMSnpJobFootprint jfp = SDMSnpJobFootprintTable.idx_smeId_getUniqueForUpdate(sysEnv, getId(sysEnv));
				jfp.delete(sysEnv);
			} catch (NotFoundException nfe) {

			}
		}
	}

	private void deleteLocalResources(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long masterId = getMasterId(sysEnv);
		Long id = getId(sysEnv);
		if(id.equals(masterId)) {
			final Vector rv = SDMSResourceTable.idx_masterId.getVector(sysEnv, masterId);
			for(int i = 0; i < rv.size(); i++) {
				final SDMSResource r = (SDMSResource) rv.get(i);
				r.delete(sysEnv);
			}
		}
	}

	private void suspend (SystemEnvironment sysEnv, boolean suspend, boolean local, boolean admin, boolean operator)
		throws SDMSException
	{
		suspend (sysEnv, suspend, local, admin, operator, false);
	}
	private void suspend (SystemEnvironment sysEnv, boolean suspend, boolean local, boolean admin, boolean operator, boolean cancelResume)
	throws SDMSException
	{
		int oldSuspended = getIsSuspended(sysEnv).intValue();
		Boolean booleanLocal = getIsSuspendedLocal(sysEnv);

		if (oldSuspended == NOSUSPEND && !suspend)
			return;
		if (oldSuspended == SUSPEND && (suspend & !admin))
			return;
		if (oldSuspended == ADMINSUSPEND && (suspend && admin))
			return;
		if (suspend && local && (oldSuspended != NOSUSPEND) && (booleanLocal == null || !booleanLocal.booleanValue()))
			return;

		if (!cancelResume && admin && (!sysEnv.cEnv.isUser() || !sysEnv.cEnv.gid().contains(SDMSObject.adminGId))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03503051428", "Insufficient privileges for admin suspend/resume"));
		}

		if (oldSuspended == ADMINSUSPEND && !admin) {
			if (!sysEnv.cEnv.isUser() || !sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03408111621", "Insufficient privileges for admin suspend/resume"));
			}
		}

		if (!suspend) {
			if (booleanLocal != null)
				local = booleanLocal.booleanValue();
			else
				local = false;
		} else {
			setIsSuspendedLocal(sysEnv, new Boolean(local));
			setResumeTs(sysEnv, null);
		}

		int newSuspended;
		if (suspend && admin)
			newSuspended = ADMINSUSPEND;
		else if (suspend)
			newSuspended = SUSPEND;
		else
			newSuspended = NOSUSPEND;

		setIsSuspended(sysEnv, new Integer(newSuspended));
		if (!suspend)
			setIsSuspendedLocal(sysEnv, Boolean.FALSE);

		if (!((oldSuspended == SUSPEND && newSuspended == ADMINSUSPEND) ||
		      (oldSuspended == ADMINSUSPEND && newSuspended == SUSPEND))) {
			if (!local)
				addParentSuspendedToChildren (sysEnv, suspend ? 1 : - 1);
			fixCntInParents(sysEnv,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					suspend ? 1 : -1,
					0
				);
			updateStatistics(sysEnv);
		}
		long ts = new Date().getTime();

		int state = getState(sysEnv).intValue();
		if(suspend) {
			if(state == RESOURCE_WAIT || state == RUNNABLE) {
				releaseResources(sysEnv, SYNCHRONIZE_WAIT);
				setState(sysEnv, new Integer(DEPENDENCY_WAIT));
			}
			if (operator)
				setOpSusresTs(sysEnv, new Long(-ts));
		} else {
			if (operator)
				setOpSusresTs(sysEnv, new Long(ts));
			testDependencies(sysEnv);
			checkDeferStall(sysEnv);

			if (state == SYNCHRONIZE_WAIT || state == DEPENDENCY_WAIT)
				if (getScopeId(sysEnv) != null)
					setScopeId(sysEnv, null);
		}

		SystemEnvironment.sched.notifyChange(sysEnv, this, (suspend ? SchedulingThread.SUSPEND : SchedulingThread.RESUME));
	}

	private void addParentSuspendedToChildren (SystemEnvironment sysEnv, int parentSuspended)
		throws SDMSException
	{
		Vector childs = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		Iterator i = childs.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			SDMSSubmittedEntity child_sme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
			Integer childParentSuspended = child_sme.getParentSuspended(sysEnv).intValue();
			int newParentSuspended = parentSuspended + childParentSuspended;
			child_sme.setParentSuspended(sysEnv, new Integer (newParentSuspended));
			if (newParentSuspended == 0 && child_sme.getIsSuspended(sysEnv).intValue() == NOSUSPEND) {
				child_sme.testDependencies(sysEnv);
			}
			if(childParentSuspended == 0 && parentSuspended > 0) {
				int state = child_sme.getState(sysEnv).intValue();
				if(state == RESOURCE_WAIT || state == RUNNABLE) {
					child_sme.releaseResources(sysEnv, SYNCHRONIZE_WAIT);
				}
			}
			child_sme.addParentSuspendedToChildren (sysEnv, parentSuspended);
			if (parentSuspended < 0) {
				Long esdId = child_sme.getJobEsdId(sysEnv);
				if (esdId != null) {
					long version = child_sme.getSeVersion(sysEnv).longValue();
					SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, child_sme.getSeId(sysEnv), version);
					Long espId = se.getEspId(sysEnv);
					SDMSExitState es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, esdId), version);
					if (es.getIsFinal(sysEnv).booleanValue() &&
					    (child_sme.getIsSuspended(sysEnv).intValue() == NOSUSPEND) &&
					    newParentSuspended == 0) {
						child_sme.setJobIsFinal(sysEnv, Boolean.TRUE);
						child_sme.checkDependents(sysEnv);
					}
				}
				child_sme.checkFinal(sysEnv);
			}
		}
	}

	protected void checkParameters (SystemEnvironment sysEnv)
		throws SDMSException
	{
		long seVersion = getSeVersion(sysEnv).longValue();
		Vector params = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, getSeId(sysEnv), seVersion);
		Iterator i = params.iterator();
		while (i.hasNext()) {
			SDMSParameterDefinition pd = (SDMSParameterDefinition)i.next();
			int type = pd.getType(sysEnv).intValue();
			if(type != SDMSParameterDefinition.PARAMETER) continue;
			try {
				SVR.getVariableValue(sysEnv, this, pd.getName(sysEnv), false, ParseStr.S_DEFAULT, false, null);
			} catch (NotFoundException nfe) {
				final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), seVersion);
				throw new NotFoundException (new SDMSMessage(sysEnv, "03606211020", "Couldn't resolve parameter $1 of $2",
							 pd.getName(sysEnv), se.pathString(sysEnv)));
			}
		}
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key, boolean fastAccess, String mode, boolean triggercontext, SDMSScope evalScope)
		throws SDMSException
	{
		return SVR.getVariableValue(sysEnv, this, key, fastAccess, mode, triggercontext, evalScope);
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key, boolean fastAccess, String mode, boolean triggercontext)
		throws SDMSException
	{
		return SVR.getVariableValue(sysEnv, this, key, fastAccess, mode, triggercontext, null);
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key, boolean fastAccess, String mode)
		throws SDMSException
	{
		return SVR.getVariableValue(sysEnv, this, key, fastAccess, mode, false, null);
	}

	public void setVariableValue(SystemEnvironment sysEnv, String key, String value)
		throws SDMSException
	{
		long seVersion = getSeVersion(sysEnv).longValue();
		String val = "=" + value;
		Long smeId = getId(sysEnv);

		try {
			SDMSParameterDefinition pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(getSeId(sysEnv), key), seVersion);
			int type = pd.getType(sysEnv).intValue();
			if(type != SDMSParameterDefinition.PARAMETER &&
			   type != SDMSParameterDefinition.RESULT &&
			   type != SDMSParameterDefinition.RESOURCEREFERENCE) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03208090253", "Attempt to write the readonly variable $1 ($2)",
							key, pd.getTypeAsString(sysEnv)));
			}

			if(type == SDMSParameterDefinition.RESOURCEREFERENCE) {
				sysEnv.checkFeatureAvailability(SystemEnvironment.S_WRITABLE_RESOURCE_PARAMS);

				SDMSParameterDefinition lpd = SDMSParameterDefinitionTable.getObject(sysEnv, pd.getLinkPdId(sysEnv));
				Long nrId = lpd.getSeId(sysEnv);
				Vector v = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(smeId, nrId));
				for(int i = 0; i < v.size(); i++) {
					SDMSResourceAllocation ra = (SDMSResourceAllocation) v.get(i);
					if(ra.getAllocationType(sysEnv).intValue() != SDMSResourceAllocation.ALLOCATION) continue;
					final SDMSResource r = SDMSResourceTable.getObject(sysEnv, ra.getRId(sysEnv));
					r.setVariableValue(sysEnv, lpd.getName(sysEnv), smeId, value);
				}
			} else {

				try {
					SDMSEntityVariable ev = SDMSEntityVariableTable.idx_smeId_Name_getUnique(sysEnv, new SDMSKey(getId(sysEnv), key));
					ev.setEvLink(sysEnv, null);
					ev.setValue(sysEnv, val);
				} catch (NotFoundException nfe) {
					SDMSEntityVariableTable.table.create(sysEnv, getId(sysEnv), key, val, pd.getIsLocal(sysEnv), null);
				}
			}
		} catch (NotFoundException nfe) {
			try {
				SDMSEntityVariable ev = SDMSEntityVariableTable.idx_smeId_Name_getUnique(sysEnv, new SDMSKey(getId(sysEnv), key));
				ev.setValue(sysEnv, val);
			} catch (NotFoundException nfe2) {
				SDMSEntityVariableTable.table.create(sysEnv, getId(sysEnv), key, val, Boolean.TRUE, null);
			}
		}
	}

	protected void resolveDependencies(SystemEnvironment sysEnv, boolean master)
		throws SDMSException
	{
		resolveRequired(sysEnv);
		resolveDependent(sysEnv);

		checkParameters(sysEnv);

		Vector v_sme = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		Iterator i = v_sme.iterator();
		while (i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity)i.next();
			sme.resolveDependencies(sysEnv, master);
		}
	}

	private void resolveRequired(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long id   = getId(sysEnv);
		long seVersion = getSeVersion(sysEnv).longValue();
		Integer state = null;
		Long seId = getSeId(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
		Integer dependencyOperation = se.getDependencyOperation(sysEnv);
		Vector v_se = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, seId, seVersion);
		Iterator i = v_se.iterator();
		while (i.hasNext()) {
			SDMSDependencyDefinition dd = (SDMSDependencyDefinition)i.next();
			int resolveMode = dd.getResolveMode(sysEnv).intValue();
			String ddName = dd.getName(sysEnv);
			Long ddId = dd.getId(sysEnv);
			Long requiredId = dd.getSeRequiredId(sysEnv);
			SDMSSubmittedEntity sme = null;
			Long requiredSeId = requiredId;
			if (resolveMode != SDMSDependencyDefinition.EXTERNAL) {
				sme = getNearestSubmittedEntity (sysEnv, requiredId, false, false, true);
				if (sme != null || resolveMode == SDMSDependencyDefinition.INTERNAL)
					requiredSeId = null;
			}
			if (sme == null && resolveMode != SDMSDependencyDefinition.INTERNAL) {
				sme = getExternalSubmittedEntity (sysEnv, dd);
			}
			if (sme == null) {
				int unresolvedHandling = dd.getUnresolvedHandling(sysEnv).intValue();
				if (resolveMode != SDMSDependencyDefinition.INTERNAL)
					unresolvedHandling = SDMSDependencyDefinition.DEFER;
				if(unresolvedHandling == SDMSDependencyDefinition.ERROR) {
					SDMSSubmittedEntity msme = SDMSSubmittedEntityTable.getObject(sysEnv, getMasterId(sysEnv));
					Integer msmeUnresolvedHandling = msme.getUnresolvedHandling(sysEnv);
					if(msmeUnresolvedHandling != null) unresolvedHandling = msmeUnresolvedHandling.intValue();
				}
				switch (unresolvedHandling) {
					case SDMSDependencyDefinition.IGNORE:
						continue;
					case SDMSDependencyDefinition.ERROR:
						SDMSSchedulingEntity dse = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
						SDMSSchedulingEntity rse = SDMSSchedulingEntityTable.getObject(sysEnv, requiredId, seVersion);
						throw new CommonErrorException (new SDMSMessage(sysEnv, "02201110833",
							"Cannot resolve dependency of $1 from $2 for Version $3",
							dse.pathString(sysEnv, seVersion),
							rse.pathString(sysEnv, seVersion),
							new Long (seVersion)
							));
					case SDMSDependencyDefinition.SUSPEND:
						suspend(sysEnv, false, false);
						Date dts = new Date();
						Long ts = new Long (dts.getTime());
						continue;
					case SDMSDependencyDefinition.DEFER:
					case SDMSDependencyDefinition.DEFER_IGNORE:
						state = new Integer(SDMSDependencyInstance.DEFERRED);
						break;
				}
			} else {
				Long smeId = sme.getId(sysEnv);
				requiredId = smeId;
				state = new Integer(SDMSDependencyInstance.OPEN);
			}
			Integer ignore = new Integer(SDMSDependencyInstance.NO);

			SDMSDependencyInstance di;
			try {
				di = SDMSDependencyInstanceTable.table.create(sysEnv,
						ddId,
						id,
						id,
						dependencyOperation,
						requiredId,
				                requiredSeId,
						state,
						ignore,
						new Long(0),
						new Long(seVersion)
					);

				Long diIdOrig = di.getId(sysEnv);
				di.setDiIdOrig(sysEnv, diIdOrig);

				di.check(sysEnv, null, false);

				createChildDependencyInstances (sysEnv, ddId, ddName, id,
				                                dependencyOperation, requiredId, requiredSeId, di.getState(sysEnv), ignore, diIdOrig, new Long(seVersion));
			} catch (DuplicateKeyException dke) {
			}
		}
	}

	private void resolveDependent(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		long seVersion = getSeVersion(sysEnv).longValue();
		Integer state = null;
		Long seId = getSeId(sysEnv);

		Vector dependencyDefinitionList = new Vector();
		Vector v_se = SDMSDependencyDefinitionTable.idx_seRequiredId.getVector(sysEnv, seId, seVersion);
		Iterator i = v_se.iterator();
		while (i.hasNext()) {
			SDMSDependencyDefinition dd = (SDMSDependencyDefinition)i.next();
			dependencyDefinitionList.add(new DependencyDefinitionListElement(seVersion, dd));
		}
		Vector v_eDi = SDMSDependencyInstanceTable.idx_requiredSeId.getVector(sysEnv, seId);
		Iterator i_eDi = v_eDi.iterator();
		while (i_eDi.hasNext()) {
			SDMSDependencyInstance eDi = (SDMSDependencyInstance)i_eDi.next();
			Long dSmeId = eDi.getDependentId(sysEnv);
			if (!eDi.getDependentIdOrig(sysEnv).equals(dSmeId))
				continue;
			SDMSSubmittedEntity eSme = SDMSSubmittedEntityTable.getObject(sysEnv, dSmeId);
			int eSmeState = eSme.getState(sysEnv);
			if (eSmeState == FINAL || eSmeState == CANCELLED)
				continue;
			Long eSeVersion = eSme.getSeVersion(sysEnv);
			SDMSDependencyDefinition dd = SDMSDependencyDefinitionTable.getObject(sysEnv, eDi.getDdId(sysEnv), eSeVersion);
			int resolveMode = dd.getResolveMode(sysEnv).intValue();
			if (resolveMode == SDMSDependencyDefinition.INTERNAL)
				continue;
			dependencyDefinitionList.add(new DependencyDefinitionListElement(eSeVersion, dd));
		}

		Iterator i_ddl = dependencyDefinitionList.iterator();
		while (i_ddl.hasNext()) {
			DependencyDefinitionListElement ddle = (DependencyDefinitionListElement)i_ddl.next();
			Long ddSeVersion = ddle.seVersion;
			SDMSDependencyDefinition dd = ddle.dd;
			int resolveMode = dd.getResolveMode(sysEnv).intValue();
			String ddName = dd.getName(sysEnv);
			Long ddId = dd.getId(sysEnv);
			Long ddSeDependentId = dd.getSeDependentId(sysEnv);

			Long masterId = getMasterId(sysEnv);

			Vector v_dSme = new Vector();
			if (resolveMode != SDMSDependencyDefinition.EXTERNAL)
				v_dSme.addAll(SDMSSubmittedEntityTable.idx_masterId_seId.getVector(
				                      sysEnv, new SDMSKey(masterId, ddSeDependentId)));
			if (resolveMode != SDMSDependencyDefinition.INTERNAL) {
				Vector v_edSme = SDMSSubmittedEntityTable.idx_seId.getVector(sysEnv, ddSeDependentId);
				Iterator i_edSme = v_edSme.iterator();
				while (i_edSme.hasNext()) {
					SDMSSubmittedEntity edSme = (SDMSSubmittedEntity)i_edSme.next();
					if (edSme.getMasterId(sysEnv).equals(masterId))
						continue;
					if (!edSme.getSeVersion(sysEnv).equals(ddSeVersion))
						continue;
					v_dSme.add(edSme);
				}
			}
			Iterator i_dSme = v_dSme.iterator();
			while (i_dSme.hasNext()) {
				SDMSSubmittedEntity dSme = (SDMSSubmittedEntity)i_dSme.next();
				int dSmeState = dSme.getState(sysEnv).intValue();
				if (dSmeState != SDMSSubmittedEntity.DEPENDENCY_WAIT &&
				    dSmeState != SDMSSubmittedEntity.SUBMITTED &&
				    dSmeState != SDMSSubmittedEntity.UNREACHABLE)
					continue;
				if (dSme.getIsReplaced(sysEnv).booleanValue()) continue;

				Long dSmeId = dSme.getId(sysEnv);
				SDMSSubmittedEntity rSme = null;
				Long dMasterId = dSme.getMasterId(sysEnv);
				if (resolveMode != SDMSDependencyDefinition.EXTERNAL)
				rSme = dSme.getNearestSubmittedEntity (sysEnv, seId,
								       false,
								       false,
								       true
								      );
				if (rSme == null && resolveMode != SDMSDependencyDefinition.INTERNAL)
					rSme = dSme.getExternalSubmittedEntity (sysEnv, dd);
				if (rSme == null)
					if (dMasterId.equals(masterId)) {
						SDMSSchedulingEntity rSe = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
						SDMSSchedulingEntity dSe = SDMSSchedulingEntityTable.getObject(sysEnv, dSme.getSeId(sysEnv), dSme.getSeVersion(sysEnv));
						throw new CommonErrorException(new SDMSMessage(sysEnv, "02201111122",
						                               "Cannot resolve cyclic dependency between dependent $1 and required $2, because required is not allowed to be child of dependent",
						                               dSe.pathString(sysEnv, seVersion),
						                               rSe.pathString(sysEnv, seVersion)
						                                              ));
					} else
						continue;
				Long rSmeId = rSme.getId(sysEnv);
				if (!rSmeId.equals(id)) {
					continue;
				}

				try {
					SDMSDependencyInstance di = SDMSDependencyInstanceTable.idx_ddId_dependentId_RequiredId_getUnique(sysEnv,
					                            new SDMSKey (ddId, dSmeId, seId));
					int diState = di.getState(sysEnv).intValue();
					if (diState == SDMSDependencyInstance.DEFERRED && di.getRequiredSeId(sysEnv) == null) {
						di.delete(sysEnv);
					}
				} catch (NotFoundException nfe) {
				}

				Long dSeId = dSme.getSeId(sysEnv);
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, dSeId, dSme.getSeVersion(sysEnv));
				Integer dependencyOperation = se.getDependencyOperation(sysEnv);
				state = new Integer(SDMSDependencyInstance.OPEN);
				try {
					boolean found = false;
					Vector v_di = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, dSmeId);
					if (resolveMode != SDMSDependencyDefinition.INTERNAL) {
						v_eDi = SDMSDependencyInstanceTable.idx_requiredSeId.getVector(sysEnv, seId);
						v_di.addAll(v_eDi);
					}
					Iterator i_di = v_di.iterator();
					while (i_di.hasNext()) {
						SDMSDependencyInstance tdi = (SDMSDependencyInstance)i_di.next();
						if (!tdi.getDdId(sysEnv).equals(ddId)) {
							continue;
						}
						found = true;
						Long replacedRequiredId = tdi.getRequiredId(sysEnv);
						if (!replacedRequiredId.equals(id)) {

							Vector v_di_r = SDMSDependencyInstanceTable.idx_diIdOrig.getVector(sysEnv,
									tdi.getId(sysEnv));
							Iterator i_di_r = v_di_r.iterator();
							while (i_di_r.hasNext()) {
								SDMSDependencyInstance di_r = (SDMSDependencyInstance)i_di_r.next();
								if (di_r.getState(sysEnv).intValue() == SDMSDependencyInstance.OPEN || di_r.getRequiredSeId(sysEnv) != null) {
									di_r.setRequiredId(sysEnv, id);
									di_r.setState(sysEnv, SDMSDependencyInstance.OPEN);
								} else {
									if (di_r.getIgnore(sysEnv).intValue() == SDMSDependencyInstance.NO) {
										Long depSmeId = di_r.getDependentId(sysEnv);
										SDMSSubmittedEntity dsme = SDMSSubmittedEntityTable.getObject(sysEnv, depSmeId);
										int dState = dsme.getState(sysEnv).intValue();
										if (dState == SDMSSubmittedEntity.DEPENDENCY_WAIT || dsme.getJobIsRestartable(sysEnv)) {
											di_r.setRequiredId(sysEnv, id);
											di_r.setState(sysEnv, SDMSDependencyInstance.OPEN);
										}
									}
								}
							}
						}
					}
					if (!found) {
						Integer ignore = new Integer(SDMSDependencyInstance.NO);
						SDMSDependencyInstance di = SDMSDependencyInstanceTable.table.create(sysEnv,
										ddId,
										dSmeId,
										dSmeId,
										dependencyOperation,
										id,
										null,
										state,
										ignore,
										new Long(0),
										ddSeVersion
							);
						Long diIdOrig = di.getId(sysEnv);
						di.setDiIdOrig(sysEnv, diIdOrig);
						dSme.createChildDependencyInstances (sysEnv, ddId, ddName, dSmeId,
						                                     dependencyOperation, id, null, state, ignore, diIdOrig, ddSeVersion);
					}
				} catch (DuplicateKeyException dke) {
				}
			}
		}
	}

	private void createChildDependencyInstances (SystemEnvironment sysEnv,
			Long ddId, String ddName, Long dependentIdOrig,
			Integer dependencyOperation, Long requiredId, Long requiredSeId, Integer state,
			Integer ignore,	Long diIdOrig, Long seVersion)
		throws SDMSException
	{
		Vector v_sh = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		Iterator i = v_sh.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			if (ddName != null) {
				if (SDMSIgnoredDependencyTable.idx_shId_ddName.containsKey(sysEnv, new SDMSKey (hi.getShId(sysEnv), ddName))) {
					continue;
				}
			}
			Long childId = hi.getChildId(sysEnv);
			SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv,childId);

			int smeState = sme.getState(sysEnv).intValue();
			if (smeState != SUBMITTED &&
			    smeState != DEPENDENCY_WAIT)
				continue;

			Long smeId = sme.getId(sysEnv);

			try {
				SDMSDependencyInstanceTable.table.create(sysEnv,
						ddId,
						smeId,
						dependentIdOrig,
						dependencyOperation,
						requiredId,
				                requiredSeId,
						state,
						ignore,
						diIdOrig,
						seVersion
					);
				sme.createChildDependencyInstances (sysEnv,
						ddId,
						ddName,
						dependentIdOrig,
						dependencyOperation,
						requiredId,
						requiredSeId,
						state,
						ignore,
						diIdOrig,
						seVersion
					);
			} catch (DuplicateKeyException dke) {
			}
		}
	}

	SDMSSubmittedEntity getNearestSubmittedEntity (SystemEnvironment sysEnv,
			Long seId,
			boolean stopAtDynamicParent,
			boolean ignoreNoMerge
		)
		throws SDMSException
	{
		return getNearestSubmittedEntity (sysEnv, seId, stopAtDynamicParent, ignoreNoMerge, false);
	}

	SDMSSubmittedEntity getExternalSubmittedEntity (SystemEnvironment sysEnv,SDMSDependencyDefinition dd)
	throws SDMSException
	{
		Integer expBase = dd.getExpiredBase(sysEnv);
		Integer expAmount = dd.getExpiredAmount(sysEnv);
		int eb = 0;
		long ea = 0;
		if (expAmount != null && expBase != null) {
			ea = expAmount.longValue();
			eb = expBase.intValue();
			switch(eb) {
				case SDMSInterval.MINUTE:
					ea *= SDMSInterval.MINUTE_DUR;
					break;
				case SDMSInterval.HOUR:
					ea *= SDMSInterval.HOUR_DUR;
					break;
				case SDMSInterval.DAY:
					ea *= SDMSInterval.DAY_DUR;
					break;
				case SDMSInterval.WEEK:
					ea *= SDMSInterval.WEEK_DUR;
					break;
				case SDMSInterval.MONTH:
					ea *= SDMSInterval.MONTH_DUR;
					break;
				case SDMSInterval.YEAR:
					ea *= SDMSInterval.YEAR_DUR;
					break;
			}
		}
		long depSubmitTs = getSubmitTs(sysEnv);
		String selCond = dd.getSelectCondition(sysEnv);
		int resolveMode = dd.getResolveMode(sysEnv).intValue();
		Long masterId = this.getMasterId(sysEnv);
		Vector grps = new Vector();
		grps.add(getOwnerId(sysEnv));

		SDMSSubmittedEntity lastSme = null;
		long maxSubmitTs = 0;
		Vector v_sme = SDMSSubmittedEntityTable.idx_seId.getVector(sysEnv, dd.getSeRequiredId(sysEnv));
		Iterator i = v_sme.iterator();
		while (i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity)i.next();
			if (resolveMode == SDMSDependencyDefinition.EXTERNAL && masterId.equals(sme.getMasterId(sysEnv))) {
				continue;
			}
			long reqSubmitTs = sme.getSubmitTs(sysEnv).longValue();
			if (sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.CANCELLED)
				continue;
			if (reqSubmitTs <= maxSubmitTs)
				continue;
			SDMSPrivilege priv = sme.getPrivilegesForGroups(sysEnv,grps);
			if (!priv.can(SDMSPrivilege.MONITOR))
				continue;
			if (expAmount != null && expBase != null) {
				if (reqSubmitTs + ea < depSubmitTs) {
					continue;
				}
			}
			if (selCond != null) {
				final BoolExpr be = new BoolExpr(selCond);
				try {
					if (!be.checkCondition(sysEnv, null, this, sme, null, null, null))
						continue;
				} catch (CommonErrorException ce) {
					continue;
				}
			}
			lastSme = sme;
			maxSubmitTs = reqSubmitTs;
		}
		return lastSme;
	}

	SDMSSubmittedEntity getNearestSubmittedEntity (SystemEnvironment sysEnv,
			Long seId,
			boolean stopAtDynamicParent,
			boolean ignoreNoMerge,
			boolean usePath
		)
		throws SDMSException
	{
		SDMSSubmittedEntity sme = null;
		Long parentId = getParentId(sysEnv);
		Long childIdToIgnore = getId(sysEnv);
		while (parentId != null) {
			SDMSSubmittedEntity parentSme = SDMSSubmittedEntityTable.getObject(sysEnv, parentId);
			if(usePath) {
				if(parentSme.getSeId(sysEnv).equals(seId)) return parentSme;
			}
			sme = parentSme.getChildSubmittedEntity(sysEnv,
								seId,
								childIdToIgnore,
								this,
								ignoreNoMerge);
			if (sme != null) {
				break;
			}
			if (stopAtDynamicParent && parentSme.getIsStatic(sysEnv).booleanValue() == false) {
				break;
			}
			childIdToIgnore = parentId;
			parentId = parentSme.getParentId(sysEnv);
		}
		return sme;
	}

	private boolean isMyChild(SystemEnvironment sysEnv, Long hitId, Long targetId, Long ignoreId)
	throws SDMSException
	{
		Vector parents = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, hitId);
		for (int i = 0; i < parents.size(); ++i) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance) parents.get(i);
			Long parentId = hi.getParentId(sysEnv);
			if (ignoreId != null && parentId.equals(ignoreId))
				continue;
			if (parentId.equals(targetId)) return true;
			if (isMyChild(sysEnv, parentId, targetId, ignoreId)) return true;
		}
		return false;
	}

	public SDMSSubmittedEntity getChildSubmittedEntity (SystemEnvironment sysEnv,
			Long seId,
			Long childIdToIgnore,
			SDMSSubmittedEntity smeOrig,
			boolean ignoreNoMerge
		)
		throws SDMSException
	{
		long seVersion = getSeVersion(sysEnv).longValue();
		SDMSSubmittedEntity sme = null;
		SDMSSubmittedEntity foundSme = null;
		Long smeOrigId = smeOrig.getId(sysEnv);

		Vector potentialHits = SDMSSubmittedEntityTable.idx_masterId_seId.getVector(sysEnv, new SDMSKey(getMasterId(sysEnv), seId));
		Vector v_sme = new Vector();
		for (int i = 0; i < potentialHits.size(); ++i) {
			SDMSSubmittedEntity hit = (SDMSSubmittedEntity) potentialHits.get(i);
			if (hit.getIsReplaced(sysEnv).booleanValue())
				continue;
			if (ignoreNoMerge && hit.getMergeMode(sysEnv).intValue() == SDMSSchedulingHierarchy.NOMERGE) {
				continue;
			}
			if (isMyChild(sysEnv, hit.getId(sysEnv), getId(sysEnv), childIdToIgnore)) {
				if (!v_sme.contains(hit))
					v_sme.add(hit);
			}
		}
		if (v_sme.size() == 0) {
			return null;
		}
		if (v_sme.size() > 1) {

			throw new CommonErrorException(new SDMSMessage(sysEnv, "02201111026",
			                               "Ambigous resolution"));
		}

		return (SDMSSubmittedEntity) v_sme.get(0);
	}

	public void checkDependencies(SystemEnvironment sysEnv)
		throws SDMSException
	{
		testDependencies(sysEnv);
		Vector v_sme = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		Iterator i = v_sme.iterator();
		while (i.hasNext()) {
			SDMSSubmittedEntity childSme = (SDMSSubmittedEntity)i.next();
			childSme.checkDependencies(sysEnv);
		}
	}

	public void finishDisabledOrBatch(SystemEnvironment sysEnv)
	throws SDMSException
	{
		if (getJobEsdId(sysEnv) == null) {
			setJobEsdId(sysEnv, getDefaultEsdId(sysEnv));
		}
		setJobIsFinal(sysEnv, Boolean.TRUE);
		checkDependents(sysEnv);
		setState(sysEnv, new Integer(FINISHED));
		trigger (sysEnv, SDMSTrigger.UNTIL_FINISHED);
		trigger (sysEnv, SDMSTrigger.UNTIL_FINAL);
		mergeExitStates(sysEnv);
		checkFinal(sysEnv);
	}

	protected void testDependencies(SystemEnvironment sysEnv)
		throws SDMSException
	{
		int s = getState(sysEnv).intValue();
		boolean diDeleted = false;

		if (s != SDMSSubmittedEntity.SUBMITTED &&
		    s != SDMSSubmittedEntity.DEPENDENCY_WAIT &&
		    s != SDMSSubmittedEntity.UNREACHABLE) {
			return;
		}
		if (s == SDMSSubmittedEntity.DEPENDENCY_WAIT && getOldState(sysEnv) != null) {
			return;
		}
		long seVersion = getSeVersion(sysEnv).longValue();
		Long id = getId(sysEnv);
		Vector v_di = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, id);
		HashMap hm_origId_di  = new HashMap();
		HashMap hm_origId_do  = new HashMap();
		Iterator i = v_di.iterator();
		HashMap checkCache = new HashMap();
		while (i.hasNext()) {
			SDMSDependencyInstance di = (SDMSDependencyInstance)i.next();
			if (di.getIgnore(sysEnv).intValue() == SDMSDependencyInstance.YES ||
			    di.getIgnore(sysEnv).intValue() == SDMSDependencyInstance.RECURSIVE ) {
				continue;
			}
			if (di.getRequiredSeId(sysEnv) != null) {
				long actVersion = di.getSeVersion(sysEnv).longValue();
				Long ddId = di.getDdId(sysEnv);
				SDMSDependencyDefinition dd = SDMSDependencyDefinitionTable.getObject(sysEnv, ddId, actVersion);
				if (dd.getResolveMode(sysEnv) != SDMSDependencyDefinition.INTERNAL) {
					boolean resolvedInternally = false;
					if (dd.getResolveMode(sysEnv) == SDMSDependencyDefinition.BOTH) {
						Long requiredId = di.getRequiredId(sysEnv);
						if (requiredId != null) {
							try {
								SDMSSubmittedEntity requiredSme = SDMSSubmittedEntityTable.getObject(sysEnv, requiredId);
								if (requiredSme.getMasterId(sysEnv).equals(getMasterId(sysEnv))) {
									resolvedInternally = true;
								}
							} catch (NotFoundException nfe) {
							}
						}
					}
					if (!resolvedInternally) {
						SDMSSubmittedEntity sme = getExternalSubmittedEntity (sysEnv, dd);
						if (sme == null) {
							di.setState(sysEnv, SDMSDependencyInstance.DEFERRED);
							di.setRequiredId(sysEnv, di.getRequiredSeId(sysEnv));
						} else {
							if (!sme.getId(sysEnv).equals(id)) {
								di.setState(sysEnv, SDMSDependencyInstance.OPEN);
								di.setRequiredId(sysEnv, sme.getId(sysEnv));
								di.check(sysEnv, checkCache, false);
							}
						}
					}
				}
			}
			Long dependentIdOrig = di.getDependentIdOrig(sysEnv);
			Vector v_orig_di;
			if (!hm_origId_do.containsKey(dependentIdOrig)) {
				Integer dependencyOperation = di.getDependencyOperation(sysEnv);
				hm_origId_do.put(dependentIdOrig, dependencyOperation);
				v_orig_di = new Vector();
				hm_origId_di.put(dependentIdOrig, v_orig_di);
			} else {
				v_orig_di = (Vector)hm_origId_di.get(dependentIdOrig);
			}
			v_orig_di.add(di);
		}
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), seVersion);
		int result = FULFILLED;
		Set s_do = hm_origId_do.keySet();
		Iterator i_do = s_do.iterator();
		String errorMessage = null;
		boolean openOrDeferred = false;
		Vector unresolvedExternals = new Vector();
		while (i_do.hasNext() && (result == FULFILLED || result == UNREACHABLE_CANCELLED || result == NOTYET)) {
			Long dependentIdOrig = (Long)i_do.next();
			boolean unreachable;
			boolean fulfilled;
			boolean cancelled = false;
			boolean broken = false;
			boolean failedExternal = false;
			int dependencyOperation = ((Integer)(hm_origId_do.get(dependentIdOrig))).intValue();

			if (dependencyOperation == SDMSSchedulingEntity.AND) {
				fulfilled = true;
				unreachable = false;
			} else {
				fulfilled = false;
				unreachable = true;
			}
			Vector v_orig_di = (Vector)hm_origId_di.get(dependentIdOrig);
			i = v_orig_di.iterator();
			while (i.hasNext()) {
				SDMSDependencyInstance di = (SDMSDependencyInstance)i.next();
				int state = di.getState(sysEnv).intValue();
				SDMSDependencyDefinition tmpDD = SDMSDependencyDefinitionTable.getObject(sysEnv, di.getDdId(sysEnv), seVersion);
				int unresolvedHandling = tmpDD.getUnresolvedHandling(sysEnv).intValue();
				if (state == SDMSDependencyInstance.CANCELLED) {
					cancelled = true;
				}
				if (state == SDMSDependencyInstance.BROKEN) {
					errorMessage = "Broken Dependency";
					broken = true;
				}
				if (dependencyOperation == SDMSSchedulingEntity.AND && state != SDMSDependencyInstance.FULFILLED) {
					if (state != SDMSDependencyInstance.DEFERRED)
						fulfilled = false;
					else {
						if (unresolvedHandling != SDMSDependencyDefinition.DEFER_IGNORE) {
							if (di.getRequiredSeId(sysEnv) != null && unresolvedHandling != SDMSDependencyDefinition.DEFER) {
								unresolvedExternals.add(new UnresolvedExternalDependency(unresolvedHandling, di));
							}
							fulfilled = false;
						}

					}
					if (state ==  SDMSDependencyInstance.OPEN) {
						openOrDeferred = true;
					}
					if (state == SDMSDependencyInstance.FAILED || state == SDMSDependencyInstance.BROKEN || state == SDMSDependencyInstance.CANCELLED) {
						if (di.getRequiredSeId(sysEnv) == null) {
							unreachable = true;
							break;
						} else {
							failedExternal = true;
						}
					}
					if (state == SDMSDependencyInstance.DEFERRED) {
						if (unresolvedHandling == SDMSDependencyDefinition.DEFER) {
							openOrDeferred = true;
						}
					}
				}
				if (dependencyOperation == SDMSSchedulingEntity.OR && state == SDMSDependencyInstance.FULFILLED) {
					fulfilled = true;
					unreachable = false;
					break;
				}
				if (dependencyOperation == SDMSSchedulingEntity.OR &&
				    (state == SDMSDependencyInstance.OPEN || state == SDMSDependencyInstance.DEFERRED)) {
					unreachable = false;
				}
			}
			if (!fulfilled) {
				if (unreachable) {
					if (broken)
						result = BROKEN;
					else if (cancelled)
						result = UNREACHABLE_CANCELLED;
					else
						result = UNREACHABLE;
				} else {
					if(result != UNREACHABLE_CANCELLED) {
						if (failedExternal && !openOrDeferred)
							result = UNREACHABLE;
						else
							result = NOTYET;
					}
				}
			}
		}
		if (unresolvedExternals.size() > 0 && !openOrDeferred) {
			Iterator i_ue = unresolvedExternals.iterator();
			while (i_ue.hasNext()) {
				UnresolvedExternalDependency ue = (UnresolvedExternalDependency)i_ue.next();
				if (ue.unresolvedHandling == SDMSDependencyDefinition.ERROR) {
					errorMessage = "Unresolved External Dependency";
					result = BROKEN;
				} else {
					if (ue.unresolvedHandling == SDMSDependencyDefinition.SUSPEND)
						result = UNRESOLVED_SUSPEND;
					if (ue.unresolvedHandling == SDMSDependencyDefinition.SUSPEND || ue.unresolvedHandling == SDMSDependencyDefinition.IGNORE) {
						ue.di.delete(sysEnv);
						diDeleted = true;
					}
				}
			}
		}
		int type = se.getType(sysEnv).intValue();
		switch (result) {
			case NOTYET:
				setState(sysEnv, new Integer(DEPENDENCY_WAIT));
				break;
			case UNRESOLVED_SUSPEND:
				suspend(sysEnv, false, false);
				Date dts = new Date();
				Long ts = new Long (dts.getTime());
				if (openOrDeferred) {
					setState(sysEnv, new Integer(DEPENDENCY_WAIT));
					break;
				}
			case FULFILLED:

				if(getState(sysEnv).intValue() == SUBMITTED)
					setState(sysEnv, new Integer(DEPENDENCY_WAIT));
				if(getState(sysEnv).intValue() == ERROR)
					break;
				switch (type) {
					case SDMSSchedulingEntity.JOB:
						boolean isRR = (getOldState(sysEnv) == null);
						if (isRR) {
							if ((getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.NOSUSPEND &&
							     getParentSuspended(sysEnv).intValue() == 0) ||
							    getRerunSeq(sysEnv).intValue() > 0) {
								setState(sysEnv, new Integer(SYNCHRONIZE_WAIT));
							}
						}
						break;
					case SDMSSchedulingEntity.BATCH:
					case SDMSSchedulingEntity.MILESTONE:

						if ((getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.NOSUSPEND &&
						     getParentSuspended(sysEnv).intValue() == 0) ||
						    getRerunSeq(sysEnv).intValue() > 0) {
							finishDisabledOrBatch(sysEnv);
						}
						break;
				}
				break;
			case UNREACHABLE:
				final Long espId = se.getEspId(sysEnv);
				final SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, espId, seVersion);
				final Long esdIdUR = esp.getUnreachableState(sysEnv, seVersion);

				if (esdIdUR == null) {
					setState(sysEnv, new Integer(UNREACHABLE));
				} else {
					final SDMSExitState es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, esdIdUR), seVersion);
					changeState(sysEnv, esdIdUR, es, null, null, null);
				}
				break;
			case UNREACHABLE_CANCELLED:
				setState(sysEnv, new Integer(UNREACHABLE));
				break;
			case BROKEN:
				setToError(sysEnv, errorMessage);
				break;
		}
		if (diDeleted) {
			this.testDependencies(sysEnv);
		}
	}

	protected void submitChilds (SystemEnvironment sysEnv, int parentSuspended, Long ownerId, Long replaceSmeId, int parentNiceX100, boolean isDisabled)
		throws SDMSException
	{

		long seVersion = getSeVersion(sysEnv).longValue();
		Vector childs = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, getSeId(sysEnv), seVersion);
		Iterator i = childs.iterator();
		while (i.hasNext()) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)i.next();
			if (sh.getIsStatic(sysEnv).booleanValue()) {
				Long seId = sh.getSeChildId(sysEnv);
				Long newReplaceSmeId = null;
				if (replaceSmeId != null) {
					Vector v_rSme = SDMSSubmittedEntityTable.idx_parentId_seId.getVector(sysEnv,
							new SDMSKey(replaceSmeId, seId));
					boolean found = false;
					Iterator i_rSme = v_rSme.iterator();
					while (i_rSme.hasNext()) {
						SDMSSubmittedEntity rSme = (SDMSSubmittedEntity)i_rSme.next();
						if (!rSme.getIsReplaced(sysEnv).booleanValue()) {
							found = true;
							newReplaceSmeId = rSme.getId(sysEnv);
							break;
						}
					}
					if (!found) {
						throw new CommonErrorException (new SDMSMessage(sysEnv, "02305141559",
										"Cannot find child to replace for smeId $1, seId $2", replaceSmeId, seId));
					}
				}
				doSubmitChild(sysEnv, seId, parentSuspended, null,
				              ownerId, sh, seVersion, newReplaceSmeId, null, null, null, true,
				              isDisabled, new Integer(parentNiceX100));
			}
		}
	}

	public TimeZone getEffectiveTimeZone(SystemEnvironment sysEnv)
	throws SDMSException
	{
		String tz = null;
		Long masterId = getMasterId(sysEnv);
		if (getId(sysEnv).equals(masterId))
			tz = getTimeZone(sysEnv);
		else {
			SDMSSubmittedEntity msme = SDMSSubmittedEntityTable.getObject(sysEnv, masterId);
			tz = msme.getTimeZone(sysEnv);
		}
		if (tz == null)
			return TimeZone.getDefault();
		else
			return TimeZone.getTimeZone(tz);
	}

	public String getEffectiveTimeZoneName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getEffectiveTimeZone(sysEnv).getID();
	}

	private boolean evaluateDisable(SystemEnvironment sysEnv, SDMSSchedulingHierarchy sh)
	throws SDMSException
	{
		boolean disable = sh.getIsDisabled(sysEnv);
		long seVersion = getSeVersion(sysEnv).longValue();
		Long intId = sh.getIntId(sysEnv);
		if (!disable && intId != null) {
			SDMSInterval iVal = SDMSIntervalTable.getObject (sysEnv, intId, seVersion);
			TimeZone tz = getEffectiveTimeZone(sysEnv);
			Long submitTs = getSubmitTs(sysEnv);
			Long nextTs = iVal.filter(sysEnv, submitTs, iVal.getHorizon(sysEnv, tz), tz, 0);
			if (nextTs > getSubmitTs(sysEnv)) {
				disable = true;
			}
		}
		return disable;
	}

	private void checkMergeFailure(SystemEnvironment sysEnv, Long masterId, Long seChildId,
				       SDMSSchedulingEntity se, SDMSSchedulingHierarchy sh)
		throws SDMSException
	{
		SDMSKey k = new SDMSKey (masterId, seChildId, new Integer(SDMSSchedulingHierarchy.FAILURE));
		Vector v = SDMSSubmittedEntityTable.idx_masterId_seId_mergeMode.getVector(sysEnv, k);
		if (v.size() != 0) {
			Long failId = ((SDMSSubmittedEntity)(v.elementAt(0))).getId(sysEnv);
			throw new CommonErrorException (
				new SDMSMessage(sysEnv, "02201201334",
					"$1 alread submitted with id $2 and merge mode FAILURE in same Master run",
					se.pathString(sysEnv, getSeVersion(sysEnv).longValue()),
					failId
					));
		}
		int mergeMode = sh.getMergeMode(sysEnv).intValue();
		if (mergeMode == SDMSSchedulingHierarchy.FAILURE) {
			k = new SDMSKey (masterId,seChildId);
			if (SDMSSubmittedEntityTable.idx_masterId_seId.containsKey(sysEnv, k)) {
				throw new CommonErrorException (
					new SDMSMessage(sysEnv, "02201101318",
						"$1 has merge mode FAILURE and cannot be submitted twice in same Master run",
						se.pathString(sysEnv, getSeVersion(sysEnv).longValue())));
			}
		}
	}

	private Boolean determineSuspend(SystemEnvironment sysEnv, SDMSSchedulingEntity se, SDMSSchedulingHierarchy sh)
		throws SDMSException
	{
		int suspend = sh.getSuspend(sysEnv).intValue();
		Boolean suspended = Boolean.FALSE;
		switch (suspend) {
			case SDMSSchedulingHierarchy.CHILDSUSPEND:
				suspended = se.getSubmitSuspended(sysEnv);
				break;
			case SDMSSchedulingHierarchy.NOSUSPEND:
				suspended = Boolean.FALSE;
				break;
			case SDMSSchedulingHierarchy.SUSPEND:
				suspended = Boolean.TRUE;
				break;
		}
		return suspended;
	}

	private Long determineResumeTs(SystemEnvironment sysEnv, SDMSSchedulingEntity se, SDMSSchedulingHierarchy sh, Long defaultResumeTs, Long submitTs)
		throws SDMSException
	{
		Long resumeTs = defaultResumeTs;
		String resumeAt;
		Integer resumeIn;
		Integer resumeBase;

		int suspend = sh.getSuspend(sysEnv).intValue();
		Boolean suspended = Boolean.FALSE;
		switch (suspend) {
			case SDMSSchedulingHierarchy.CHILDSUSPEND:
				resumeTs = SubmitJob.evalResumeObj(sysEnv, se.getResumeAt(sysEnv), se.getResumeIn(sysEnv), se.getResumeBase(sysEnv), submitTs, true, getEffectiveTimeZone(sysEnv));
				break;
			case SDMSSchedulingHierarchy.NOSUSPEND:
				resumeTs = null;
				break;
			case SDMSSchedulingHierarchy.SUSPEND:
				resumeTs = SubmitJob.evalResumeObj(sysEnv, sh.getResumeAt(sysEnv), sh.getResumeIn(sysEnv), sh.getResumeBase(sysEnv), submitTs, true, getEffectiveTimeZone(sysEnv));
				break;
		}
		return resumeTs;
	}

	private void fixMergedCounters(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		int fixSubmitted = sme.getCntSubmitted(sysEnv).intValue();
		int fixDependencyWait = sme.getCntDependencyWait(sysEnv).intValue();
		int fixSynchronizeWait = sme.getCntSynchronizeWait(sysEnv).intValue();
		int fixResourceWait = sme.getCntResourceWait(sysEnv).intValue();
		int fixRunnable = sme.getCntRunnable(sysEnv).intValue();
		int fixStarting = sme.getCntStarting(sysEnv).intValue();
		int fixStarted = sme.getCntStarted(sysEnv).intValue();
		int fixRunning = sme.getCntRunning(sysEnv).intValue();
		int fixToKill = sme.getCntToKill(sysEnv).intValue();
		int fixKilled = sme.getCntKilled(sysEnv).intValue();
		int fixCancelled = sme.getCntCancelled(sysEnv).intValue();
		int fixFinished = sme.getCntFinished(sysEnv).intValue();
		int fixFinal = sme.getCntFinal(sysEnv).intValue();
		int fixBrokenActive = sme.getCntBrokenActive(sysEnv).intValue();
		int fixBrokenFinished = sme.getCntBrokenFinished(sysEnv).intValue();
		int fixError = sme.getCntError(sysEnv).intValue();
		int fixUnreachable = sme.getCntUnreachable(sysEnv).intValue();
		int fixRestartable = sme.getCntRestartable(sysEnv).intValue();
		int fixWarn = sme.getCntWarn(sysEnv).intValue();
		int fixChildSuspended = sme.getChildSuspended(sysEnv).intValue();
		int fixPending = sme.getCntPending(sysEnv).intValue();
		switch(sme.getState(sysEnv).intValue()) {
			case SDMSSubmittedEntity.SUBMITTED:
				fixSubmitted ++;
				break;
			case SDMSSubmittedEntity.DEPENDENCY_WAIT:
				fixDependencyWait ++;
				break;
			case SDMSSubmittedEntity.SYNCHRONIZE_WAIT:
				fixSynchronizeWait ++;
				break;
			case SDMSSubmittedEntity.RESOURCE_WAIT:
				fixResourceWait ++;
				break;
			case SDMSSubmittedEntity.RUNNABLE:
				fixRunnable ++;
				break;
			case SDMSSubmittedEntity.STARTING:
				fixStarting ++;
				break;
			case SDMSSubmittedEntity.STARTED:
				fixStarted ++;
				break;
			case SDMSSubmittedEntity.RUNNING:
				fixRunning ++;
				break;
			case SDMSSubmittedEntity.TO_KILL:
				fixToKill ++;
				break;
			case SDMSSubmittedEntity.KILLED:
				fixKilled ++;
				break;
			case SDMSSubmittedEntity.CANCELLED:
				fixCancelled ++;
				break;
			case SDMSSubmittedEntity.FINISHED:
				fixFinished ++;
				break;
			case SDMSSubmittedEntity.FINAL:
				fixFinal ++;
				break;
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				fixBrokenActive ++;
				break;
			case SDMSSubmittedEntity.BROKEN_FINISHED:
				fixBrokenFinished ++;
				break;
			case SDMSSubmittedEntity.ERROR:
				fixError ++;
				break;
			case SDMSSubmittedEntity.UNREACHABLE:
				fixUnreachable ++;
				break;
		}
		if (sme.getJobIsRestartable(sysEnv).booleanValue() == true) fixRestartable ++;
		if (sme.getIsSuspended(sysEnv).intValue() != NOSUSPEND) fixChildSuspended ++;
		if (sme.isPending(sysEnv)) fixPending ++;
		if (fixSubmitted	!= 0) setCntSubmitted(sysEnv, new Integer(getCntSubmitted(sysEnv).intValue() + fixSubmitted));
		if (fixDependencyWait	!= 0) setCntDependencyWait(sysEnv, new Integer(getCntDependencyWait(sysEnv).intValue() + fixDependencyWait));
		if (fixSynchronizeWait	!= 0) setCntSynchronizeWait(sysEnv, new Integer(getCntSynchronizeWait(sysEnv).intValue() + fixSynchronizeWait));
		if (fixResourceWait	!= 0) setCntResourceWait(sysEnv, new Integer(getCntResourceWait(sysEnv).intValue() + fixResourceWait));
		if (fixRunnable		!= 0) setCntRunnable(sysEnv, new Integer(getCntRunnable(sysEnv).intValue() + fixRunnable));
		if (fixStarting		!= 0) setCntStarting(sysEnv, new Integer(getCntStarting(sysEnv).intValue() + fixStarting));
		if (fixStarted		!= 0) setCntStarted(sysEnv, new Integer(getCntStarted(sysEnv).intValue() + fixStarted));
		if (fixRunning		!= 0) setCntRunning(sysEnv, new Integer(getCntRunning(sysEnv).intValue() + fixRunning));
		if (fixToKill		!= 0) setCntToKill(sysEnv, new Integer(getCntToKill(sysEnv).intValue() + fixToKill));
		if (fixKilled		!= 0) setCntKilled(sysEnv, new Integer(getCntKilled(sysEnv).intValue() + fixKilled));
		if (fixCancelled	!= 0) setCntCancelled(sysEnv, new Integer(getCntCancelled(sysEnv).intValue() + fixCancelled));
		if (fixFinished		!= 0) setCntFinished(sysEnv, new Integer(getCntFinished(sysEnv).intValue() + fixFinished));
		if (fixFinal		!= 0) setCntFinal(sysEnv, new Integer(getCntFinal(sysEnv).intValue() + fixFinal));
		if (fixBrokenActive	!= 0) setCntBrokenActive(sysEnv, new Integer(getCntBrokenActive(sysEnv).intValue() + fixBrokenActive));
		if (fixBrokenFinished	!= 0) setCntBrokenFinished(sysEnv, new Integer(getCntBrokenFinished(sysEnv).intValue() + fixBrokenFinished));
		if (fixError		!= 0) setCntError(sysEnv, new Integer(getCntError(sysEnv).intValue() + fixError));
		if (fixUnreachable	!= 0) setCntUnreachable(sysEnv, new Integer(getCntUnreachable(sysEnv).intValue() + fixUnreachable));
		if (fixRestartable	!= 0) setCntRestartable(sysEnv, new Integer(getCntRestartable(sysEnv).intValue() + fixRestartable));
		if (fixChildSuspended	!= 0) setChildSuspended(sysEnv, new Integer(getChildSuspended(sysEnv).intValue() + fixChildSuspended));
		if (fixPending		!= 0) setCntPending(sysEnv, new Integer(getCntPending(sysEnv).intValue() + fixPending));
		fixCntInParents(sysEnv, fixSubmitted, fixDependencyWait, fixSynchronizeWait, fixResourceWait,
				fixRunnable, fixStarting, fixStarted, fixRunning, fixToKill, fixKilled,
				fixCancelled, fixFinished, fixFinal, fixBrokenActive, fixBrokenFinished,
				fixError, fixUnreachable, fixRestartable, fixWarn, fixChildSuspended, fixPending);
	}

	private void checkValidESP(SystemEnvironment sysEnv, SDMSSchedulingEntity se, long seVersion)
		throws SDMSException
	{
		Long espId = se.getEspId(sysEnv);
		SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, espId, seVersion);
		if (esp.getIsValid(sysEnv).booleanValue() == false) {
			esp.checkProfile(sysEnv);
		}
	}

	private SDMSSubmittedEntity doSubmitChild(SystemEnvironment sysEnv, Long seChildId, int parentSuspended, Long resumeTs, Long ownerId,
	                SDMSSchedulingHierarchy sh, long seVersion, Long replaceSmeId, Integer forceSuspend, String childTag,
	                String submitTag, boolean isStatic, boolean isDisabled, Integer parentNiceX100)
		throws SDMSException
	{
		if (!isDisabled)
			isDisabled = evaluateDisable(sysEnv, sh);

		Long id = getId(sysEnv);
		Long masterId  = getMasterId(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seChildId, seVersion);
		Long seId = se.getId(sysEnv);
		SDMSSubmittedEntity sme = null;
		Long myResumeTs = resumeTs;
		Long submitTs;

		Date ts = new Date();
		submitTs = new Long(ts.getTime());

		int myState = getState(sysEnv).intValue();
		if (myState == CANCELLED || myState == FINAL)
			throw new CommonErrorException (new SDMSMessage(sysEnv, "03703141457",
							"Submitting children of a cancelled or final parent isn't allowed"));

		checkMergeFailure(sysEnv, masterId, seChildId, se, sh);

		Boolean suspended;
		if (forceSuspend != null)
			suspended = (forceSuspend.intValue() != NOSUSPEND);
		else
			suspended = determineSuspend(sysEnv, se, sh);

		if (suspended.booleanValue()) {
			myResumeTs = determineResumeTs(sysEnv, se, sh, resumeTs, submitTs);
		}

		boolean merged = false;
		int mergeMode = sh.getMergeMode(sysEnv).intValue();

		if (mergeMode == SDMSSchedulingHierarchy.MERGE_GLOBAL ||
		    mergeMode == SDMSSchedulingHierarchy.MERGE_LOCAL) {
			boolean stopAtSubmit = false;
			if (mergeMode == SDMSSchedulingHierarchy.MERGE_LOCAL) {
				stopAtSubmit = true;
			}
			sme = getNearestSubmittedEntity (sysEnv, seChildId, stopAtSubmit, true );
			if (sme != null) {
				int state = sme.getState(sysEnv).intValue();
				if (suspended.booleanValue() && (sme.getIsSuspended(sysEnv).intValue() != NOSUSPEND) &&
				    state != FINAL && state != CANCELLED) {
					sme.suspend(sysEnv, false, false);
				}
				merged = true;
				if (parentSuspended > 0) {
					int sme_ps = sme.getParentSuspended(sysEnv).intValue() + parentSuspended;
					sme.setParentSuspended(sysEnv, new Integer(sme_ps));
					sme.addParentSuspendedToChildren (sysEnv, parentSuspended);
				}
			}
		}
		boolean submit = !merged;

		if (submit) {
			checkValidESP(sysEnv, se, seVersion);

			sme = createSme(sysEnv, se, sh, childTag, ownerId, isStatic, isDisabled,
			                (suspended.booleanValue() ? (forceSuspend == null ? new Integer(SUSPEND) : forceSuspend) : new Integer(NOSUSPEND)),
			                parentSuspended, myResumeTs, replaceSmeId, submitTag, submitTs, null );
		}
		SDMSHierarchyInstance hi = SDMSHierarchyInstanceTable.table.create(sysEnv,
					   id,
					   sme.getId(sysEnv),
					   sh.getId(sysEnv),
					   sh.getPriority(sysEnv),
					   null,
					   null,
					   new Long(seVersion)
		);
		if (submit) {
			int msParentSuspended = 0;
			SDMSSubmittedEntity dynamicSme = sme;
			Long dynamicSmeId;
			while (dynamicSme != null && dynamicSme.getIsStatic(sysEnv).booleanValue() == true) {
				dynamicSmeId = dynamicSme.getParentId(sysEnv);
				if (dynamicSmeId != null) {
					dynamicSme = SDMSSubmittedEntityTable.getObject(sysEnv, dynamicSmeId);
				} else {
					dynamicSme = null;
				}
			}
			if (dynamicSme != null) {
				dynamicSmeId = dynamicSme.getParentId(sysEnv);
				dynamicSme = SDMSSubmittedEntityTable.getObject(sysEnv, dynamicSmeId);

				Vector v_sh = SDMSSchedulingHierarchyTable.idx_seChildId.getVector(sysEnv, seId, seVersion);
				Iterator ip = v_sh.iterator();
				while (ip.hasNext()) {
					SDMSSchedulingHierarchy milestoneSh = (SDMSSchedulingHierarchy)ip.next();
					if (milestoneSh.getIsStatic(sysEnv).booleanValue() == SDMSSchedulingHierarchy.STATIC) {
						continue;
					}
					Long msParentSeId = milestoneSh.getSeParentId(sysEnv);
					SDMSSchedulingEntity msSe = SDMSSchedulingEntityTable.getObject(sysEnv, msParentSeId, seVersion);
					if(msSe.getType(sysEnv).intValue() == SDMSSchedulingEntity.MILESTONE) {
						SDMSSubmittedEntity msParentSme = dynamicSme.getNearestSubmittedEntity (sysEnv, msParentSeId, false, false);
						if (msParentSme != null) {
							int state = msParentSme.getState(sysEnv).intValue();
							if (state == FINAL || state == CANCELLED) continue;

							Long msParentSmeId = msParentSme.getId(sysEnv);
							if (! msParentSmeId.equals(id)) {
								SDMSHierarchyInstanceTable.table.create(sysEnv,
								                                        msParentSmeId,
								                                        sme.getId(sysEnv),
								                                        milestoneSh.getId(sysEnv),
								                                        milestoneSh.getPriority(sysEnv),
								                                        null,
								                                        null,
								                                        new Long(seVersion)
								                                       );
								int pSuspended =
								        msParentSme.getParentSuspended(sysEnv).intValue() +
								        (msParentSme.getIsSuspended(sysEnv).intValue() != NOSUSPEND ? 1 : 0);
								msParentSuspended += pSuspended;
								parentNiceX100 = sme.getParentNiceX100(sysEnv);
							}
						}
					}
				}
			}
			sme.setRawPriority(sysEnv, new Integer(parentNiceX100 + sme.getRawPriority(sysEnv).intValue()));

			sme.submitChilds(sysEnv, parentSuspended + (suspended.booleanValue() ? 1 : 0) + msParentSuspended,
			                 ownerId, replaceSmeId, parentNiceX100 + sme.getNice(sysEnv) * 100, isDisabled);

			sme.fixCntInParents(sysEnv,
						1,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						0,
						suspended.booleanValue() ? 1 : 0,
						0
			);

		} else {
			fixMergedCounters(sysEnv, sme);
			parentNiceX100 = sme.getParentNiceX100(sysEnv);
			int newPrio = parentNiceX100 + sme.getNice(sysEnv).intValue() * 100 + sme.getRawPriority(sysEnv).intValue();
			int deltaPrio = newPrio - sme.getRawPriority(sysEnv);
			if (deltaPrio != 0) {
				sme.setRawPriority(sysEnv, new Integer(newPrio));
				sme.fixChildPrioritiesAndNpNice(sysEnv, deltaPrio, null);
			}
		}
		return sme;
	}

	private void inheritDependencies(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long seVersion,
					   SDMSHierarchyInstance hi)
		throws SDMSException
	{
		int smeState = sme.getState(sysEnv).intValue();
		if (smeState != SUBMITTED &&
		    smeState != DEPENDENCY_WAIT)
			return;

		Long pSmeId = hi.getParentId(sysEnv);
		Vector v_di = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, pSmeId);
		Iterator i_di = v_di.iterator();
		while (i_di.hasNext()) {
			SDMSDependencyInstance di = (SDMSDependencyInstance)i_di.next();
			Long ddId = di.getDdId(sysEnv);
			SDMSDependencyDefinition dd  = SDMSDependencyDefinitionTable.getObject(sysEnv, ddId, seVersion);
			String ddName = dd.getName(sysEnv);
			if (ddName != null) {
				if (SDMSIgnoredDependencyTable.idx_shId_ddName.containsKey(sysEnv, new SDMSKey (hi.getShId(sysEnv), ddName))) {
					continue;
				}
			}
			try {
				Integer ignore;
				if (di.getIgnore(sysEnv).intValue() == SDMSDependencyInstance.RECURSIVE) {
					ignore = new Integer(SDMSDependencyInstance.RECURSIVE);
				} else {
					ignore = new Integer(SDMSDependencyInstance.NO);
				}
				Long dependentIdOrig = di.getDependentIdOrig(sysEnv);
				Integer dependencyOperation = di.getDependencyOperation(sysEnv);
				Long requiredId = di.getRequiredId(sysEnv);
				Long requiredSeId = di.getRequiredSeId(sysEnv);
				Integer state = di.getState(sysEnv);
				Long diIdOrig = di.getDiIdOrig(sysEnv);
				SDMSDependencyInstanceTable.table.create(sysEnv,
						ddId,
						sme.getId(sysEnv),
						dependentIdOrig,
						dependencyOperation,
						requiredId,
				                requiredSeId,
						state,
						ignore,
						diIdOrig,
						new Long(seVersion)
				);
				sme.createChildDependencyInstances (sysEnv, ddId, ddName, dependentIdOrig,
				                                    dependencyOperation, requiredId, requiredSeId, state, ignore, diIdOrig, new Long(seVersion));
			} catch (DuplicateKeyException dke) {
			}
		}
	}

	public void finishJob(SystemEnvironment sysEnv, Integer exitCode, String errmsg, Long finishTs)
		throws SDMSException
	{
		long seVersion = getSeVersion(sysEnv).longValue();
		Long seId = getSeId(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
		Long espId = se.getEspId(sysEnv);
		Long esmpId = se.getEsmpId(sysEnv);
		if (esmpId == null) {
			SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject (sysEnv, espId, seVersion);
			esmpId = esp.getDefaultEsmpId(sysEnv);
		}
		SDMSExitStateMappingProfile esmp = SDMSExitStateMappingProfileTable.getObject(sysEnv, esmpId, seVersion);
		Long esdId = esmp.map(sysEnv, exitCode.intValue(), seVersion);
		SDMSExitState es;
		try {
			es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, esdId), seVersion);
		} catch (NotFoundException nfe) {
			throw new FatalException(new SDMSMessage(sysEnv, "02201111630",
				"Invalid Exit State Mapping Profile $1 maps to exit state definition $2 not in exit state profile $3",
				esmpId, esdId, espId));

		}
		changeState(sysEnv, esdId, es, exitCode, errmsg, finishTs);
	}

	private void finishChildTriggers(SystemEnvironment sysEnv, Long esdId)
		throws SDMSException
	{
		HashSet rhs = new HashSet();
		Stack rs = new Stack();
		do_finishChildTriggers(sysEnv, rhs, esdId, rs, true);
	}

	private void do_finishChildTriggers(SystemEnvironment sysEnv, HashSet rhs, Long esdId, Stack rs, boolean is_reason)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		long seVersion = getSeVersion(sysEnv).longValue();
		SDMSKey k = new SDMSKey(id, esdId);

		if (is_reason) {
			if (!rhs.add(k)) return;
			rs.push(k);
		}

		Iterator i_rs = rs.iterator();
		while (i_rs.hasNext()) {
			SDMSKey k_rs = (SDMSKey)i_rs.next();
			trigger (sysEnv, SDMSTrigger.FINISH_CHILD, (Long)k_rs.get(1), (Long)k_rs.get(0), false );
		}

		Vector v_p = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, id);
		Iterator i_p = v_p.iterator();
		while (i_p.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i_p.next();
			SDMSSchedulingHierarchy sh = SDMSSchedulingHierarchyTable.getObject(sysEnv,
									hi.getShId(sysEnv), seVersion);
			Long tresdId = esdId;
			boolean p_is_reason = false;
			if (sh.getEstpId(sysEnv) != null) {
				SDMSExitStateTranslationProfile estp = SDMSExitStateTranslationProfileTable.getObject(sysEnv,
				                                       sh.getEstpId(sysEnv), seVersion);
				tresdId = estp.translate(sysEnv, esdId, seVersion, false);
				if (tresdId != null) {
					p_is_reason = true;
				} else {
					tresdId = esdId;
				}
			}
			SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
			sme.do_finishChildTriggers(sysEnv, rhs, tresdId, rs, p_is_reason);
		}
		if (is_reason) rs.pop();
	}

	public void changeState(SystemEnvironment sysEnv, Long esdId, SDMSExitState es, Integer exitCode, String errmsg, Long finishTs)
		throws SDMSException
	{
		changeState(sysEnv, esdId, es, exitCode, errmsg, finishTs, true);
	}

	public void changeState(SystemEnvironment sysEnv, Long esdId, SDMSExitState es, Integer exitCode, String errmsg, Long finishTs, boolean evalRerunTrigger)
		throws SDMSException
	{
		Long baseSmeId = (Long)sysEnv.tx.txData.get(SystemEnvironment.S_BASE_SME_ID);
		if (baseSmeId == null) {
			baseSmeId = getId(sysEnv);
			sysEnv.tx.txData.put(SystemEnvironment.S_BASE_SME_ID, baseSmeId);
		}
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), getSeVersion(sysEnv).longValue());
		if (se.getType(sysEnv).intValue() == SDMSSchedulingEntity.JOB)
			setJobEsdPref(sysEnv, es.getPreference(sysEnv));

		setJobEsdId(sysEnv, esdId);

		if (es.getIsFinal(sysEnv).booleanValue() && (getIsSuspended(sysEnv).intValue() == NOSUSPEND) && getParentSuspended(sysEnv).intValue() == 0) {
			setJobIsFinal(sysEnv, Boolean.TRUE);
			checkDependents(sysEnv);
		}
		if (es.getIsRestartable(sysEnv).booleanValue()) {
			setJobIsRestartable(sysEnv, Boolean.TRUE);
			Date dts = new Date();
			Long ts = new Long (dts.getTime());

		} else {
			setJobIsRestartable(sysEnv, Boolean.FALSE);
		}
		setState(sysEnv, new Integer (FINISHED));
		if (finishTs != null)
			setFinishTs(sysEnv, finishTs);

		setExitCode(sysEnv, exitCode);
		setErrorMsg(sysEnv, errmsg);

		if (baseSmeId.equals(getId(sysEnv))) {
			finishChildTriggers(sysEnv, esdId);
		}

		trigger (sysEnv, SDMSTrigger.IMMEDIATE_LOCAL, false );
		trigger (sysEnv, SDMSTrigger.UNTIL_FINISHED);
		trigger (sysEnv, SDMSTrigger.UNTIL_FINAL);

		mergeExitStates(sysEnv);

		checkFinal(sysEnv);

		if (getState(sysEnv).intValue() != FINAL)
			releaseResources(sysEnv, getState(sysEnv).intValue());

		if (getJobIsRestartable(sysEnv).booleanValue() &&
		    getState(sysEnv).intValue() != ERROR && getState(sysEnv).intValue() != BROKEN_FINISHED &&
		    evalRerunTrigger &&
		    (getIsSuspended(sysEnv).intValue() == NOSUSPEND)) {
			trigger (sysEnv, SDMSTrigger.IMMEDIATE_LOCAL, true );
		}

		SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.STATECHANGE);
	}

	private void mergeExitStates(SystemEnvironment sysEnv)
		throws SDMSException
	{
		mergeExitStates(sysEnv, true, null);
	}

	private void mergeExitStates(SystemEnvironment sysEnv, boolean doTrigger)
		throws SDMSException
	{
		mergeExitStates(sysEnv, doTrigger, null);
	}
	private void mergeExitStates(SystemEnvironment sysEnv, boolean doTrigger, SDMSSubmittedEntity sme_to_ignore)
		throws SDMSException
	{

		if (getState(sysEnv).intValue() == CANCELLED) return;

		Long id = getId(sysEnv);
		Vector v_hi = SDMSHierarchyInstanceTable.idx_parentId.getVectorForUpdate(sysEnv, id);
		Long jobEsdId = getJobEsdId(sysEnv);
		Long finalEsdId = jobEsdId;
		Long oldFinalEsdId = getFinalEsdId(sysEnv);
		Integer jobEsdPref = getJobEsdPref(sysEnv);
		int pref = (jobEsdPref == null ? -1 : jobEsdPref.intValue());

		Iterator i = v_hi.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			SDMSSubmittedEntity csme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, hi.getChildId(sysEnv));
			if ((sme_to_ignore != null &&
			     sme_to_ignore.getId(sysEnv).equals(csme.getId(sysEnv))) ||
			    csme.getState(sysEnv).intValue() == CANCELLED)
				continue;
			if (hi.getChildEsdId(sysEnv) != null) {
				int hiPref = hi.getChildEsPreference(sysEnv).intValue();
				if (hiPref < pref || pref == -1) {
					pref = hiPref;
					finalEsdId = hi.getChildEsdId(sysEnv);
				}
			}
		}
		if (finalEsdId == null && oldFinalEsdId == null) return;
		if ((finalEsdId != null && oldFinalEsdId == null) ||
		    (finalEsdId == null && oldFinalEsdId != null) ||
		    !finalEsdId.equals(oldFinalEsdId)) {
			setFinalEsdId(sysEnv, finalEsdId, doTrigger);
		}
	}

	private void translateToParent(SystemEnvironment sysEnv, boolean doTrigger)
		throws SDMSException
	{
		long seVersion = getSeVersion(sysEnv).longValue();
		Long id = getId(sysEnv);
		Long finalEsdId = getFinalEsdId(sysEnv);
		Vector v_hi = SDMSHierarchyInstanceTable.idx_childId.getVectorForUpdate(sysEnv, id);
		Iterator i = v_hi.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			Long shId = hi.getShId(sysEnv);
			SDMSSchedulingHierarchy sh = SDMSSchedulingHierarchyTable.getObject(sysEnv, shId, seVersion);
			Long estpId = sh.getEstpId(sysEnv);
			Long trEsdId = finalEsdId;
			boolean translated = false;
			if (estpId != null && finalEsdId != null) {
				SDMSExitStateTranslationProfile estp =
				        SDMSExitStateTranslationProfileTable.getObject(sysEnv, estpId, seVersion);
				trEsdId = estp.translate(sysEnv, finalEsdId, seVersion, false);
				if (trEsdId == null)
					trEsdId = finalEsdId;
				else
					translated = true;
			} else {
				trEsdId = finalEsdId;
			}
			Long childEsdId = hi.getChildEsdId(sysEnv);
			if ((childEsdId == null && trEsdId == null) || (childEsdId != null && childEsdId.equals(trEsdId))) {
				return;
			}
			SDMSSubmittedEntity parentSme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, hi.getParentId(sysEnv));
			if (trEsdId != null) {
				Long seId = parentSme.getSeId(sysEnv);
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
				Long espId = se.getEspId(sysEnv);
				SDMSExitState es;
				Integer es_pref;
				try {
					es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, trEsdId), seVersion);
					es_pref = es.getPreference(sysEnv);
				} catch (NotFoundException nfe) {
					if (translated) {
						throw new FatalException(new SDMSMessage(sysEnv, "02201112130",
									"Invalid Exit State Translation Profile $1 translates to exit state definition $2 not in exit state profile $3",
									estpId, trEsdId, espId));
					} else {
						trEsdId = null;
						es_pref = null;
					}
				}
				hi.setChildEsdId(sysEnv, trEsdId);
				hi.setChildEsPreference(sysEnv, es_pref);
			} else {
				hi.setChildEsPreference(sysEnv, null);
				hi.setChildEsdId(sysEnv, null);
			}
			parentSme.mergeExitStates(sysEnv, doTrigger);
		}
	}

	private boolean checkParentCancelled(SystemEnvironment sysEnv, Long smeId, HashSet chs)
		throws SDMSException
	{
		if (chs == null) return false;
		Vector pv = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, smeId);
		Iterator pvi = pv.iterator();
		while (pvi.hasNext()) {
			SDMSHierarchyInstance sh = (SDMSHierarchyInstance) pvi.next();
			Long parentId = sh.getParentId(sysEnv);
			if (chs.contains(parentId)) {
				return true;
			} else {
				if (checkParentCancelled(sysEnv, parentId, chs))
					return true;
			}
		}
		return false;
	}

	public void setState(SystemEnvironment sysEnv, Integer state)
		throws SDMSException
	{
		long actVersion = getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), actVersion);
		final Long mySmeId = getId(sysEnv);

		int oldState = getState(sysEnv).intValue();
		int newState = state.intValue();

		int fixSubmitted = 0;
		int fixDependencyWait = 0;
		int fixSynchronizeWait = 0;
		int fixResourceWait = 0;
		int fixRunnable = 0;
		int fixStarting = 0;
		int fixStarted = 0;
		int fixRunning = 0;
		int fixToKill = 0;
		int fixKilled = 0;
		int fixCancelled = 0;
		int fixFinished = 0;
		int fixFinal = 0;
		int fixBrokenActive = 0;
		int fixBrokenFinished = 0;
		int fixError = 0;
		int fixUnreachable = 0;

		Date dts = new Date();
		Long ts = new Long (dts.getTime());

		if (newState == STARTING && oldState != STARTING)
			synchronized(SystemEnvironment.jidsStarting) {
				SystemEnvironment.jidsStarting.put(getId(sysEnv), ts);
			}
		if (oldState == STARTING && newState != STARTING)
			synchronized(SystemEnvironment.jidsStarting) {
				SystemEnvironment.jidsStarting.remove(getId(sysEnv));
			}

		if (oldState == newState)
			return;

		switch (oldState) {
			case SUBMITTED:
				fixSubmitted		-= 1;
				break;
			case DEPENDENCY_WAIT:
				fixDependencyWait	-= 1;
				break;
			case SYNCHRONIZE_WAIT:
				fixSynchronizeWait	-= 1;
				break;
			case RESOURCE_WAIT:
				fixResourceWait		-= 1;
				break;
			case RUNNABLE:
				fixRunnable		-= 1;
				break;
			case STARTING:
				fixStarting		-= 1;
				break;
			case STARTED:
				fixStarted		-= 1;
				break;
			case RUNNING:
				fixRunning		-= 1;
				break;
			case TO_KILL:
				fixToKill		-= 1;
				break;
			case KILLED:
				fixKilled		-= 1;
				break;
			case CANCELLED:
				fixCancelled		-= 1;
				break;
			case FINISHED:
				fixFinished		-= 1;
				break;
			case FINAL:
				fixFinal		-= 1;
				break;
			case BROKEN_ACTIVE:
				fixBrokenActive		-= 1;
				break;
			case BROKEN_FINISHED:
				fixBrokenFinished	-= 1;
				break;
			case ERROR:
				fixError		-= 1;
				break;
			case UNREACHABLE:
				fixUnreachable		-= 1;
				break;
		}
		switch (newState) {
			case SUBMITTED:
				fixSubmitted		+= 1;
				break;
			case DEPENDENCY_WAIT:
				fixDependencyWait	+= 1;
				break;
			case SYNCHRONIZE_WAIT:
				fixSynchronizeWait	+= 1;
				break;
			case RESOURCE_WAIT:
				fixResourceWait		+= 1;
				break;
			case RUNNABLE:
				fixRunnable		+= 1;
				break;
			case STARTING:
				fixStarting		+= 1;
				break;
			case STARTED:
				fixStarted		+= 1;
				break;
			case RUNNING:
				fixRunning		+= 1;
				break;
			case TO_KILL:
				fixToKill		+= 1;
				break;
			case KILLED:
				fixKilled		+= 1;
				break;
			case CANCELLED:
				fixCancelled		+= 1;
				break;
			case FINISHED:
				fixFinished		+= 1;
				break;
			case FINAL:
				fixFinal		+= 1;
				break;
			case BROKEN_ACTIVE:
				fixBrokenActive		+= 1;
				break;
			case BROKEN_FINISHED:
				fixBrokenFinished	+= 1;
				break;
			case ERROR:
				fixError		+= 1;
				break;
			case UNREACHABLE:
				fixUnreachable		+= 1;
				break;
		}

		super.setState(sysEnv, state);

		fixCntInParents(sysEnv, fixSubmitted, fixDependencyWait, fixSynchronizeWait, fixResourceWait, fixRunnable,
				fixStarting, fixStarted, fixRunning, fixToKill, fixKilled, fixCancelled, fixFinished, fixFinal,
				fixBrokenActive, fixBrokenFinished, fixError, fixUnreachable,
				0,
				0,
				0,
				0
		);

		if (getIsCancelled(sysEnv).booleanValue() && newState != CANCELLED) {
			doDeferredCancel(sysEnv);
			return;
		}

		if (newState == RESOURCE_WAIT) {
			setResourceTs(sysEnv, ts);
		}
		if (newState == RUNNABLE) {
			setRunnableTs(sysEnv, ts);
		}
		if (newState == STARTING ||
		    newState == STARTED) {
			setStartTs(sysEnv, ts);
		}
		if (newState == CANCELLED) {
			setFinalTs(sysEnv, ts);
			int type = se.getType(sysEnv).intValue();
			if (type != SDMSSchedulingEntity.BATCH && getFinishTs(sysEnv) == null) {
				setFinishTs(sysEnv, ts);
			}
			setJobIsRestartable(sysEnv, Boolean.FALSE);
		}
		if (newState == ERROR ||
		    newState == BROKEN_FINISHED ||
		    newState == FINISHED) {
			setFinishTs(sysEnv, ts);
		}
		if (newState != FINISHED && newState != CANCELLED) {
			if (newState != FINAL) {
				setJobEsdId(sysEnv, null);
				setJobEsdPref(sysEnv, null);
			}
			if (getJobIsRestartable(sysEnv).booleanValue()) {
				setJobIsRestartable(sysEnv, Boolean.FALSE);
			}
		}
		if (newState == ERROR ||
		    newState == BROKEN_FINISHED) {
			final Long espId = se.getEspId(sysEnv);
			final SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, espId);
			final Long brokenEsdId = esp.getBrokenState(sysEnv, actVersion);
			if(brokenEsdId != null) {
				final SDMSExitState es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, brokenEsdId));
				changeState(sysEnv, brokenEsdId, es, null, null, null, true);
				int curState = getState(sysEnv).intValue();
				if (curState == FINISHED) {
					super.setState(sysEnv, state);
					fixCntInParents(sysEnv, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0,
							newState == BROKEN_FINISHED ? 1 : 0, newState == ERROR ? 1 : 0, 0, 0, 0, 0, 0 );
				}
			} else
				setJobIsRestartable(sysEnv, Boolean.TRUE);

		}

		if (newState == FINAL ) {
			setFinalTs(sysEnv, ts);
			setJobIsRestartable(sysEnv, Boolean.FALSE);
		}

		if (newState == UNREACHABLE) {
			HashSet chs = (HashSet)sysEnv.tx.txData.get(SystemEnvironment.S_CANCEL_HASHSET);
			if (!checkParentCancelled(sysEnv, mySmeId, chs)) {
				Date adts = new Date();
				Long ats = new Long (dts.getTime());
			}
		}

		if (newState == DEPENDENCY_WAIT) {
			if (se.getType(sysEnv).intValue() == SDMSSchedulingEntity.JOB) {
				this.setOldState(sysEnv, new Integer(oldState));
				SystemEnvironment.sched.addToRequestList(sysEnv, mySmeId);
			} else
				checkDependencies(sysEnv);
			SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.STATECHANGE);
		}
		if (newState == SYNCHRONIZE_WAIT) {
			this.setOldState(sysEnv, new Integer(oldState));
			SystemEnvironment.sched.addToRequestList(sysEnv, mySmeId);
			setSyncTs(sysEnv, ts);
			SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.STATECHANGE);
		}
		if (newState == FINAL ||
		    newState == CANCELLED ||
		    (newState == FINISHED && getJobIsFinal(sysEnv).booleanValue()) ||
		    newState == UNREACHABLE ||
		    ((newState == DEPENDENCY_WAIT || newState == SYNCHRONIZE_WAIT || newState == FINISHED || newState == FINAL) && (oldState == UNREACHABLE || oldState == ERROR))) {
			checkDependents(sysEnv);
		}
		if (getFinalTs(sysEnv) != null && getFinishTs(sysEnv) == null) setFinishTs(sysEnv, ts);
		if (getFinishTs(sysEnv) != null && getStartTs(sysEnv) == null) setStartTs(sysEnv, ts);
		if (getStartTs(sysEnv) != null && getRunnableTs(sysEnv) == null) setRunnableTs(sysEnv, ts);
		if (getRunnableTs(sysEnv) != null && getResourceTs(sysEnv) == null) setResourceTs(sysEnv, ts);
		if (getResourceTs(sysEnv) != null && getSyncTs(sysEnv) == null) setSyncTs(sysEnv, ts);

		if (newState == FINAL ||
		    newState == CANCELLED ||
		    newState == FINISHED ||
		    newState == ERROR ||
		    newState == BROKEN_FINISHED) {
			SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.STATECHANGE);
			checkDeferStall(sysEnv);
		}
		updateStatistics(sysEnv);
		return;
	}

	private HashSet<Long> collectDependents(SystemEnvironment sysEnv)
	throws SDMSException
	{
		HashSet<Long> result = new HashSet<Long>();
		Long id = getId(sysEnv);
		Vector v_di = SDMSDependencyInstanceTable.idx_requiredId.getVectorForUpdate(sysEnv, id);
		HashMap checkCache = new HashMap();
		Iterator i = v_di.iterator();
		while (i.hasNext()) {
			SDMSDependencyInstance di = (SDMSDependencyInstance)i.next();
			int oldDiState = di.getState(sysEnv).intValue();
			int newDiState = di.check(sysEnv, checkCache);
			if (newDiState != SDMSDependencyInstance.OPEN ||
			    (oldDiState == SDMSDependencyInstance.FAILED && newDiState != SDMSDependencyInstance.FAILED)) {
				result.add(di.getDependentId(sysEnv));
			}
		}

		return result;
	}

	private void checkDependents(SystemEnvironment sysEnv)
	throws SDMSException
	{
		HashSet<Long> smesToTest = collectDependents(sysEnv);
		Iterator<Long> i = smesToTest.iterator();
		while (i.hasNext()) {
			Long dSmeId = i.next();
			SDMSSubmittedEntity dSme = SDMSSubmittedEntityTable.getObject(sysEnv, dSmeId);
			dSme.testDependencies(sysEnv);
		}
	}

	private void fixCntInParents(SystemEnvironment sysEnv,
			int fixSubmitted, int fixDependencyWait, int fixSynchronizeWait, int fixResourceWait,
			int fixRunnable, int fixStarting, int fixStarted, int fixRunning,
			int fixToKill, int fixKilled, int fixCancelled, int fixFinished, int fixFinal,
			int fixBrokenActive, int fixBrokenFinished, int fixError, int fixUnreachable,
			int fixRestartable, int fixWarn, int fixChildSuspended, int fixPending
		)
		throws SDMSException
	{
		Vector v_sh = SDMSHierarchyInstanceTable.idx_childId.getVectorForUpdate(sysEnv, getId(sysEnv));
		Iterator i = v_sh.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			Long parentId = hi.getParentId(sysEnv);

			SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, parentId);

			if (fixSubmitted	!= 0) {
				sme.setCntSubmitted(sysEnv, new Integer(sme.getCntSubmitted(sysEnv).intValue() + fixSubmitted));
			}
			if (fixDependencyWait	!= 0) {
				sme.setCntDependencyWait(sysEnv, new Integer(sme.getCntDependencyWait(sysEnv).intValue() + fixDependencyWait));
			}
			if (fixSynchronizeWait	!= 0) {
				sme.setCntSynchronizeWait(sysEnv, new Integer(sme.getCntSynchronizeWait(sysEnv).intValue() + fixSynchronizeWait));
			}
			if (fixResourceWait	!= 0) {
				sme.setCntResourceWait(sysEnv, new Integer(sme.getCntResourceWait(sysEnv).intValue() + fixResourceWait));
			}
			if (fixRunnable		!= 0) {
				sme.setCntRunnable(sysEnv, new Integer(sme.getCntRunnable(sysEnv).intValue() + fixRunnable));
			}
			if (fixStarting		!= 0) {
				sme.setCntStarting(sysEnv, new Integer(sme.getCntStarting(sysEnv).intValue() + fixStarting));
			}
			if (fixStarted		!= 0) {
				sme.setCntStarted(sysEnv, new Integer(sme.getCntStarted(sysEnv).intValue() + fixStarted));
			}
			if (fixRunning		!= 0) {
				sme.setCntRunning(sysEnv, new Integer(sme.getCntRunning(sysEnv).intValue() + fixRunning));
			}
			if (fixToKill		!= 0) {
				sme.setCntToKill(sysEnv, new Integer(sme.getCntToKill(sysEnv).intValue() + fixToKill));
			}
			if (fixKilled		!= 0) {
				sme.setCntKilled(sysEnv, new Integer(sme.getCntKilled(sysEnv).intValue() + fixKilled));
			}
			if (fixCancelled		!= 0) {
				sme.setCntCancelled(sysEnv, new Integer(sme.getCntCancelled(sysEnv).intValue() + fixCancelled));
			}
			if (fixFinished		!= 0) {
				sme.setCntFinished(sysEnv, new Integer(sme.getCntFinished(sysEnv).intValue() + fixFinished));
			}
			if (fixFinal		!= 0) {
				sme.setCntFinal(sysEnv, new Integer(sme.getCntFinal(sysEnv).intValue() + fixFinal));
			}
			if (fixBrokenActive	!= 0) {
				sme.setCntBrokenActive(sysEnv, new Integer(sme.getCntBrokenActive(sysEnv).intValue() + fixBrokenActive));
			}
			if (fixBrokenFinished	!= 0) {
				sme.setCntBrokenFinished(sysEnv, new Integer(sme.getCntBrokenFinished(sysEnv).intValue() + fixBrokenFinished));
			}
			if (fixError		!= 0) {
				sme.setCntError(sysEnv, new Integer(sme.getCntError(sysEnv).intValue() + fixError));
			}
			if (fixUnreachable	!= 0) {
				sme.setCntUnreachable(sysEnv, new Integer(sme.getCntUnreachable(sysEnv).intValue() + fixUnreachable));
			}
			if (fixRestartable	!= 0) {
				sme.setCntRestartable(sysEnv, new Integer(sme.getCntRestartable(sysEnv).intValue() + fixRestartable));
			}
			if (fixWarn		!= 0) {
				sme.setCntWarn(sysEnv, new Integer(sme.getCntWarn(sysEnv).intValue() + fixWarn));
			}
			if (fixChildSuspended		!= 0) {
				sme.setChildSuspended(sysEnv, new Integer(sme.getChildSuspended(sysEnv).intValue() + fixChildSuspended));
			}
			if (fixPending		!= 0) {
				sme.setCntPending(sysEnv, new Integer(sme.getCntPending(sysEnv).intValue() + fixPending));
			}

			sme.fixCntInParents(sysEnv, fixSubmitted, fixDependencyWait, fixSynchronizeWait, fixResourceWait, fixRunnable,
					    fixStarting, fixStarted, fixRunning, fixToKill, fixKilled, fixCancelled, fixFinished, fixFinal,
					    fixBrokenActive, fixBrokenFinished, fixError, fixUnreachable, fixRestartable, fixWarn, fixChildSuspended, fixPending);

			sme.checkFinal(sysEnv);
		}
		Integer idleTs = getIdleTs(sysEnv);
		boolean idle = false;
		int state = getState(sysEnv).intValue();
		if (state != SDMSSubmittedEntity.SUBMITTED &&
		    state != SDMSSubmittedEntity.DEPENDENCY_WAIT &&
		    state != SDMSSubmittedEntity.STARTED &&
		    state != SDMSSubmittedEntity.RUNNING &&
		    state != SDMSSubmittedEntity.TO_KILL &&
		    state != SDMSSubmittedEntity.KILLED &&
		    state != SDMSSubmittedEntity.BROKEN_ACTIVE &&
		    state != SDMSSubmittedEntity.FINAL &&
		    state != SDMSSubmittedEntity.CANCELLED &&
		    getCntRunning(sysEnv).intValue() == 0 &&
		    getCntStarted(sysEnv).intValue() == 0 &&
		    getCntToKill(sysEnv).intValue() == 0 &&
		    getCntKilled(sysEnv).intValue() == 0 &&
		    getCntBrokenActive(sysEnv).intValue() == 0
		   )
			idle = true;
		if (idle && idleTs == null) {
			setIdleTs(sysEnv, new Integer((int)((sysEnv.cEnv.last() - getSubmitTs(sysEnv).longValue()) / 1000)));
		}
		if (!idle && idleTs != null) {
			Integer idleTime = getIdleTime(sysEnv);
			int iT = 0;
			if (idleTime != null) iT = idleTime.intValue();
			setIdleTime(sysEnv,
			            new Integer(iT + (int)((sysEnv.cEnv.last() - getSubmitTs(sysEnv).longValue()) / 1000) - idleTs.intValue()));
			setIdleTs(sysEnv, null);
		}
	}

	public void setFinalEsdId(SystemEnvironment sysEnv, Long esdId)
		throws SDMSException
	{
		setFinalEsdId(sysEnv, esdId, true);
	}
	public void setFinalEsdId(SystemEnvironment sysEnv, Long esdId, boolean doTrigger)
		throws SDMSException
	{
		SDMSSubmittedEntity ret;
		Long finalEsdId = getFinalEsdId(sysEnv);
		if(esdId == null && finalEsdId == null) return;
		if ((esdId != null && finalEsdId == null) ||
		    (esdId == null && finalEsdId != null) ||
		    !esdId.equals(finalEsdId)) {
			super.setFinalEsdId(sysEnv, esdId);

			if(doTrigger) {
				trigger (sysEnv, SDMSTrigger.IMMEDIATE_MERGE);
			}

			translateToParent(sysEnv, doTrigger);
			return;
		} else {
			return;
		}
	}

	public void setResumeTs (SystemEnvironment sysEnv, Long resumeTs)
	throws SDMSException
	{
		super.setResumeTs(sysEnv, resumeTs);
		if (resumeTs == null)
			sysEnv.tt.removeFromJobsToResume(sysEnv, getId(sysEnv));
		else
			sysEnv.tt.addToJobsToResume(sysEnv, getId(sysEnv));
	}

	public void setJobIsRestartable(SystemEnvironment sysEnv, Boolean flag)
		throws SDMSException
	{
		if (flag.booleanValue() != getJobIsRestartable(sysEnv).booleanValue()) {
			super.setJobIsRestartable(sysEnv, flag);
			fixCntInParents(sysEnv,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					flag.booleanValue() ? 1 : -1,
					0,
					0,
					0
			);
		}
		return;
	}

	public void setJobEsdId(SystemEnvironment sysEnv, Long esdId)
		throws SDMSException
	{
		setJobEsdId(sysEnv, esdId, true);
	}

	boolean isPending(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return isPendingState(sysEnv, getJobEsdId(sysEnv));
	}

	boolean isPendingState(SystemEnvironment sysEnv, Long esdId)
		throws SDMSException
	{
		if (esdId == null) return false;
		long seVersion = getSeVersion(sysEnv).longValue();
		Long seId = getSeId(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
		Long espId = se.getEspId(sysEnv);
		SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, espId, seVersion);
		return esp.isPendingState(sysEnv, esdId, seVersion);
	}

	public void setJobEsdId(SystemEnvironment sysEnv, Long newEsdId, boolean doTrigger)
		throws SDMSException
	{
		Long oldEsdId = getJobEsdId(sysEnv);

		boolean oldIsPending = isPendingState(sysEnv, oldEsdId);
		boolean newIsPending = isPendingState(sysEnv, newEsdId);
		if (oldIsPending != newIsPending)
			fixCntInParents(sysEnv,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					0,
					newIsPending ? 1 : -1
			);
		if (newEsdId == null && oldEsdId == null) return;
		if ((newEsdId != null && oldEsdId == null) ||
		    (newEsdId == null && oldEsdId != null) ||
		    !newEsdId.equals(oldEsdId)) {
			super.setJobEsdId(sysEnv, newEsdId);
			mergeExitStates(sysEnv, doTrigger);
		}
	}

	public SDMSSubmittedEntity submitChild (SystemEnvironment sysEnv,
			Vector params, Integer suspended, Long resumeTs, Long childId, String childTag, Long replaceSmeId, String submitTag)
		throws SDMSException
	{
		return submitChild (sysEnv, params, suspended, resumeTs, childId, childTag, replaceSmeId, submitTag, true);
	}

	public SDMSSubmittedEntity submitChild (SystemEnvironment sysEnv,
			Vector params, Integer suspended, Long resumeTs, Long childId, String childTag, Long replaceSmeId, String submitTag, boolean forceChildDef)
		throws SDMSException
	{
		long seVersion = getSeVersion(sysEnv).longValue();
		Long thisSeId = getSeId(sysEnv);

		SDMSSchedulingHierarchy sh;
		try {
			sh = SDMSSchedulingHierarchyTable.idx_parentId_childId_getUnique(sysEnv, new SDMSKey (thisSeId, childId), seVersion);
		} catch (NotFoundException nfe) {
			if (forceChildDef) {
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, childId, seVersion);
				SDMSSchedulingEntity thisSe = SDMSSchedulingEntityTable.getObject(sysEnv, thisSeId, seVersion);
				throw new CommonErrorException (new SDMSMessage(sysEnv, "02210120844",
					"$1 not a child of $2", se.pathString(sysEnv, seVersion), thisSe.pathString(sysEnv, seVersion)));
			} else
				sh = SDMSSchedulingHierarchyTable.getObject(sysEnv, SDMSObject.dummyShId, seVersion);
		}

		if (replaceSmeId == null) {
			if (sh.getIsStatic(sysEnv).booleanValue() == SDMSSchedulingHierarchy.STATIC) {
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, childId, seVersion);
				SDMSSchedulingEntity thisSe = SDMSSchedulingEntityTable.getObject(sysEnv, thisSeId, seVersion);
				throw new CommonErrorException (new SDMSMessage(sysEnv, "02210120849",
					"$1 must not be a static child of $2",
					se.pathString(sysEnv, seVersion), thisSe.pathString(sysEnv, seVersion)));
			}
		}

		int parentSuspended = getParentSuspended(sysEnv).intValue();
		parentSuspended = parentSuspended + (getIsSuspended(sysEnv).intValue() != NOSUSPEND? 1 : 0);

		Long ownerId = getOwnerId(sysEnv);

		int parentNiceX100 = getParentNiceX100(sysEnv) + getNice(sysEnv).intValue();
		SDMSSubmittedEntity sme = doSubmitChild(sysEnv, childId, parentSuspended, resumeTs, ownerId,
		                                        sh, seVersion, replaceSmeId, suspended, childTag, submitTag, evaluateDisable(sysEnv, sh), false, parentNiceX100);

		if(params != null) {
			Iterator i = params.iterator();
			while (i.hasNext()) {
				WithItem wi = (WithItem)i.next();
				sme.setVariableValue(sysEnv, (String) wi.key, wi.value.toString());
			}
		}

		sme.resolveDependencies(sysEnv, false );

		Vector v_hi = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, sme.getId(sysEnv), seVersion);
		Iterator i = v_hi.iterator();
		while (i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)i.next();
			inheritDependencies(sysEnv, sme, seVersion, hi);
		}

		sme.checkDependencies(sysEnv);

		return sme;
	}

	private void copySmeParameters(SystemEnvironment sysEnv, Long fromSmeId, Long toSmeId)
		throws SDMSException
	{
		Vector v_p = SDMSEntityVariableTable.idx_smeId.getVector(sysEnv, fromSmeId);
		Iterator i_p = v_p.iterator();
		while (i_p.hasNext()) {
			SDMSEntityVariable from_ev = (SDMSEntityVariable)i_p.next();
			if (!from_ev.getIsLocal(sysEnv).booleanValue()) {
				SDMSEntityVariableTable.table.create (sysEnv,
					toSmeId,
					from_ev.getName(sysEnv),
					from_ev.getValue(sysEnv),
					Boolean.FALSE,
					null
				);
			}
		}
	}

	public void releaseResources(SystemEnvironment sysEnv, int newState)
		throws SDMSException
	{
		SDMSResourceAllocation ra;
		SDMSResource r;
		Vector v;
		int state;

		HashSet hg = new HashSet();
		hg.add(SDMSObject.adminGId);
		sysEnv.cEnv.pushGid(sysEnv, hg);

		try {
			v = SDMSResourceAllocationTable.idx_smeId.getVectorForUpdate(sysEnv, this.getId(sysEnv));
			for(int i = 0; i < v.size(); i++) {
				ra = (SDMSResourceAllocation) v.get(i);
				state = newState;
				int allocType = ra.getAllocationType(sysEnv).intValue();
				if(allocType != SDMSResourceAllocation.IGNORE) {
					int keepMode = ra.getKeepMode(sysEnv).intValue();
					if(keepMode != SDMSResourceRequirement.NOKEEP) {
						if(state == FINISHED) {
							if(! getJobIsFinal(sysEnv).booleanValue())	{
								continue;
							}
						}
						if(keepMode == SDMSResourceRequirement.KEEP_FINAL && state != FINAL && state != CANCELLED) {
							continue;
						}
					}
					boolean skip = false;
					r = SDMSResourceTable.getObjectForUpdate(sysEnv, ra.getRId(sysEnv));
					if (ra.getIsSticky(sysEnv).booleanValue()) {
						releaseStickyResource(sysEnv, ra, r, newState);
						if(! getJobIsFinal(sysEnv).booleanValue() && state != CANCELLED)	{
							ra.setAllocationType(sysEnv, SDMSResourceAllocation.REQUEST);
							skip = true;
						}
					}
					if(state == FINISHED || state == FINAL) {
						if((allocType == SDMSResourceAllocation.ALLOCATION) && (ra.getRsmpId(sysEnv) != null) && (ra.getLockmode(sysEnv).intValue() == Lockmode.X)) {
							setResourceState(sysEnv, r, ra.getRsmpId(sysEnv));
						}
					}
					if (skip) continue;
				}
				ra.delete(sysEnv, false, true);
			}

		} catch (Throwable t) {
			sysEnv.cEnv.popGid(sysEnv);
			throw t;
		}
		sysEnv.cEnv.popGid(sysEnv);
	}

	public void releaseStickyResource(SystemEnvironment sysEnv, SDMSResourceAllocation ra, SDMSResource r, int newState)
		throws SDMSException
	{
		final SDMSResourceAllocation mra;
		Vector rav;
		int lockmode = Lockmode.N;
		Long rId = r.getId(sysEnv);
		Long raId = ra.getId(sysEnv);
		Long draId;
		String raStickyName = ra.getStickyName(sysEnv);
		Long raStickyParent = ra.getStickyParent(sysEnv);
		int amount = 0;
		int draAmount;

		try {
			mra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv,
				new SDMSKey(new Long(-raStickyParent.longValue()), rId, raStickyName));
		} catch (NotFoundException nfe) {
			return;
		}
		Long mraId = mra.getId(sysEnv);
		rav = SDMSResourceAllocationTable.idx_stickyParent_rId_stickyName.getVector(sysEnv,
				new SDMSKey(raStickyParent, rId, raStickyName));

		for (int i = 0; i < rav.size(); ++i) {
			SDMSResourceAllocation dra = (SDMSResourceAllocation) rav.get(i);

			draId = dra.getId(sysEnv);
			if (draId.equals(mraId)) continue;
			if (draId.equals(raId) &&
			    (newState == FINAL ||
			     newState == CANCELLED ||
			     (getJobIsFinal(sysEnv) && dra.getKeepMode(sysEnv).intValue() != SDMSResourceRequirement.KEEP_FINAL)))
				continue;

			if(dra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.IGNORE) continue;

			draAmount = dra.getAmount(sysEnv).intValue();
			if (draAmount > amount) amount = draAmount;

			lockmode &= dra.getLockmode(sysEnv).intValue();
		}

		mra.setLockmode(sysEnv, new Integer(lockmode));

		int origAmount = mra.getOrigAmount(sysEnv).intValue();
		if (origAmount > amount) {
			int mraAmount = mra.getAmount(sysEnv).intValue() - (origAmount - amount);

			mra.setOrigAmount(sysEnv, new Integer(amount));
			mra.setAmount(sysEnv, new Integer(mraAmount));
		}
	}

	public void setResourceState(SystemEnvironment sysEnv, SDMSResource r, Long rsmpId)
		throws SDMSException
	{
		final Long ts = new Long ((new Date()).getTime());
		Vector v = SDMSResourceStateMappingTable.idx_rsmpId.getVector(sysEnv, rsmpId);
		for(int i = 0; i < v.size(); i++) {
			SDMSResourceStateMapping rsm = (SDMSResourceStateMapping) v.get(i);
			if(this.getJobEsdId(sysEnv).equals(rsm.getEsdId(sysEnv))) {
				Long fromRsdId = rsm.getFromRsdId(sysEnv);
				if(fromRsdId != null && fromRsdId.equals(r.getRsdId(sysEnv))) {
					try {
						r.setRsdId(sysEnv, rsm.getToRsdId(sysEnv), this);
						r.setRsdTime(sysEnv, ts);
					} catch(CommonErrorException cee) {
					}
					return;
				}
			}
		}
		for(int i = 0; i < v.size(); i++) {
			SDMSResourceStateMapping rsm = (SDMSResourceStateMapping) v.get(i);
			if(this.getJobEsdId(sysEnv).equals(rsm.getEsdId(sysEnv))) {
				Long fromRsdId = rsm.getFromRsdId(sysEnv);
				if(fromRsdId == null) {
					try {
						r.setRsdId(sysEnv, rsm.getToRsdId(sysEnv), this);
						r.setRsdTime(sysEnv, ts);
					} catch(CommonErrorException cee) {
					}
					return;
				}
			}
		}
	}

	public Vector pathStrings(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSHierarchyInstance hi;
		SDMSSubmittedEntity sme;
		Vector parents;
		Vector result = new Vector();
		Long pId;

		String tag = getChildTag(sysEnv);
		Long id = getId(sysEnv);

		parents = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, getId(sysEnv));
		if(parents.size() == 0) {
			Vector p = new Vector();
			p.add(id);
			p.add(tag);
			result.add(p);
		} else {
			SDMSSubmittedEntity psme = SDMSSubmittedEntityTable.getObject(sysEnv, getParentId(sysEnv));
			pId = psme.getId(sysEnv);
			Vector v = psme.pathStrings(sysEnv);
			for(int j = 0; j < v.size(); j++) {
				Vector p = (Vector) v.get(j);
				p.add(id);
				p.add(tag);
				result.add(p);
			}
			for(int i = 0; i < parents.size(); i++) {
				hi = (SDMSHierarchyInstance) parents.get(i);
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
				if(pId.equals(sme.getId(sysEnv))) continue;
				v = sme.pathStrings(sysEnv);
				for(int j = 0; j < v.size(); j++) {
					Vector p = (Vector) v.get(j);
					p.add(id);
					p.add(tag);
					result.add(p);
				}
			}
		}

		return result;
	}

	private void removeAsyncTrigger(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector trv = SDMSTriggerQueueTable.idx_smeId.getVector(sysEnv, getId(sysEnv));
		for(int i = 0; i < trv.size(); i++) {
			SDMSTriggerQueue tq = (SDMSTriggerQueue) trv.get(i);
			tq.delete(sysEnv);
		}
	}

	protected boolean trigger(SystemEnvironment sysEnv, int trigger_type)
		throws SDMSException
	{
		return trigger (sysEnv, trigger_type, false);
	}

	protected boolean trigger(SystemEnvironment sysEnv, int trigger_type, boolean evaluateRerun)
		throws SDMSException
	{
		Long esdId;
		if (trigger_type == SDMSTrigger.IMMEDIATE_LOCAL)
			esdId = getJobEsdId(sysEnv);
		else
			esdId = getFinalEsdId(sysEnv);
		return trigger (sysEnv, trigger_type, esdId, getId(sysEnv), evaluateRerun);
	}

	private boolean trigger(SystemEnvironment sysEnv, int trigger_type, Long esdId, Long reasonSmeId, boolean evaluateRerun)
		throws SDMSException
	{
		if (getIsCancelled(sysEnv).booleanValue())
			return false;

		long seVersion = getSeVersion(sysEnv).longValue();
		boolean fired = false;
		Long fireSeId = getSeId(sysEnv);

		HashSet ths = (HashSet)sysEnv.tx.txData.get(SystemEnvironment.S_TRIGGER_HASHSET);
		if (ths == null) {
			ths = new HashSet();
			sysEnv.tx.txData.put(SystemEnvironment.S_TRIGGER_HASHSET, ths);
		}
		Vector v_tr = SDMSTriggerTable.idx_fireId_type.getVector(sysEnv, new SDMSKey(fireSeId, new Integer(trigger_type)), seVersion);
		Iterator i_tr = v_tr.iterator();
		while (i_tr.hasNext()) {
			SDMSTrigger tr = (SDMSTrigger)i_tr.next();
			int action = tr.getAction(sysEnv).intValue();
			if (evaluateRerun && action == SDMSTrigger.RERUN)
				if(tr.trigger(sysEnv, esdId, reasonSmeId, null, this)) fired = true;
			if (!evaluateRerun && action == SDMSTrigger.SUBMIT)
				if(tr.trigger(sysEnv, esdId, reasonSmeId, null, this)) fired = true;
		}
		return fired;
	}

	private SDMSSubmittedEntity createSme(SystemEnvironment sysEnv, SDMSSchedulingEntity se, SDMSSchedulingHierarchy sh,
	                                      String childTag, Long ownerId, boolean isStatic, boolean isDisabled, Integer suspended, int parentSuspended,
	                                      Long resumeTs, Long replaceSmeId, String submitTag, Long submitTs, String timeZone)
		throws SDMSException
	{
		SDMSSubmittedEntity sme;
		Long seId = se.getId(sysEnv);

		Integer prio = null;
		Integer rawPrio = null;
		Integer nice;
		if(se.getType(sysEnv).intValue() == SDMSSchedulingEntity.BATCH || se.getType(sysEnv).intValue() == SDMSSchedulingEntity.MILESTONE) {
			nice = se.getPriority(sysEnv);
			prio = zero;
			rawPrio = zero;
		} else {
			prio = se.getPriority(sysEnv);
			rawPrio = new Integer (se.getPriority(sysEnv).intValue() * 100);
			nice = zero;
		}
		Integer minEP = se.getMinPriority(sysEnv);
		if(minEP == null) {
			minEP = new Integer(SystemEnvironment.priorityLowerBound);
		}
		Integer agingAmount = se.getAgingAmount(sysEnv);
		Integer agingBase = se.getAgingBase(sysEnv);
		if (agingAmount == null || agingBase == null) {
			agingAmount = new Integer(SystemEnvironment.priorityDelay);
			agingBase = new Integer(SDMSInterval.MINUTE);
		}
		switch(agingBase.intValue()) {
			case SDMSInterval.MINUTE:
				break;
			case SDMSInterval.HOUR:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.HOUR_DUR_M));
				break;
			case SDMSInterval.DAY:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.DAY_DUR_M));
				break;
			case SDMSInterval.WEEK:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.WEEK_DUR_M));
				break;
			case SDMSInterval.MONTH:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.MONTH_DUR_M));
				break;
			case SDMSInterval.YEAR:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.YEAR_DUR_M));
				break;
		}
		agingBase = new Integer(SDMSInterval.MINUTE);

		Long opSusresTs = null;
		if (suspended.intValue() != SDMSSubmittedEntity.NOSUSPEND)
			opSusresTs = new Long(-submitTs.longValue());

		sme = SDMSSubmittedEntityTable.table.create(sysEnv,
				sysEnv.randomLong(),
				getMasterId(sysEnv),
				submitTag,
				null,
				seId,
				childTag,
				getSeVersion(sysEnv),
				ownerId,
				getId(sysEnv),
				null,
				new Boolean(isStatic),
		                new Boolean(isDisabled),
		                null,
				sh.getMergeMode(sysEnv),
				new Integer(SDMSSubmittedEntity.SUBMITTED),
				null,
				null,
				Boolean.FALSE,
				Boolean.FALSE,
				null,
				null,
				null,
				null,
				zero,
				Boolean.FALSE,
				Boolean.FALSE,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				zero,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				suspended,
				prio,
		                rawPrio,
				nice,
				zero,
				minEP,
				agingAmount,
				new Integer(parentSuspended),
				zero,
				zero,
				null,
				submitTs,
				resumeTs,
				null,
				null,
				null,
				null,
				null,
				null,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
				zero,
		                null,
				zero,
				zero,
				zero,
				zero,
		                zero,
				zero,
				zero,
				zero,
				zero,
		                opSusresTs,
		                null,
		                timeZone
		);

		if (replaceSmeId != null) {
			copySmeParameters(sysEnv, replaceSmeId, sme.getId(sysEnv));
			SDMSSubmittedEntity replaceSme = SDMSSubmittedEntityTable.getObject(sysEnv, replaceSmeId);
			replaceSme.setIsReplaced(sysEnv, Boolean.TRUE);
		}

		Vector trv = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
		for(int i = 0; i < trv.size(); i++) {
			SDMSTrigger t = (SDMSTrigger) trv.get(i);
			int trType = t.getType(sysEnv).intValue();
			if(trType == SDMSTrigger.UNTIL_FINISHED || trType == SDMSTrigger.UNTIL_FINAL) {
				SDMSTriggerQueueTable.table.create(sysEnv, sme.getId(sysEnv), t.getId(sysEnv), lzero, zero, zero);
			}
		}
		return sme;
	}

	private void fixChildPrioritiesAndNpNice(SystemEnvironment sysEnv, int prioDelta, Integer np_nicevalue)
	throws SDMSException
	{
		Vector child_v = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		for(int i = 0; i < child_v.size(); i++) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance) child_v.get(i);
			SDMSSubmittedEntity csme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
			int parents = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, csme.getId(sysEnv)).size();
			prioDelta =  prioDelta / parents;
			if (prioDelta != 0)
				csme.setRawPriority(sysEnv, new Integer(csme.getRawPriority(sysEnv).intValue() + prioDelta));
			csme.fixChildPrioritiesAndNpNice(sysEnv, prioDelta, np_nicevalue);
			if (np_nicevalue != null)
				csme.setNpNice(sysEnv, np_nicevalue);
		}
	}

	public int getParentNiceX100(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector v = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, getId(sysEnv));
		int parentNiceTotal = 0;
		for(int i = 0; i < v.size(); i++) {
			SDMSHierarchyInstance h = (SDMSHierarchyInstance) v.get(i);
			Long pId = h.getParentId(sysEnv);
			SDMSSubmittedEntity psme  = SDMSSubmittedEntityTable.getObject(sysEnv, pId);
			parentNiceTotal += (h.getNice(sysEnv).intValue() + psme.getNice(sysEnv).intValue()) * 100 + psme.getParentNiceX100(sysEnv);
		}
		if (v.size() == 0) return 0;
		return parentNiceTotal / v.size();
	}

	public void renice(SystemEnvironment sysEnv, Integer nicevalue, Integer np_nicevalue, String comment)
		throws SDMSException
	{
		Integer nv;
		Integer pr;
		int inv = 0;
		int ipr;

		int state = getState(sysEnv).intValue();
		if(state == CANCELLED || state == FINAL) {
			throw new CommonErrorException (new SDMSMessage(sysEnv, "03303061417",
				"Cannot renice a cancelled or final job"));
		}

		int niceDelta = 0;
		if (nicevalue != null) {
			inv = nicevalue.intValue();
			if(inv < - SchedulingThread.MIN_PRIORITY)
				inv = - SchedulingThread.MIN_PRIORITY;
			if(inv > SchedulingThread.MIN_PRIORITY)
				inv = SchedulingThread.MIN_PRIORITY;
			niceDelta = inv - getNice(sysEnv).intValue();
			setNice(sysEnv, new Integer (inv));
		}
		int npNiceDelta = 0;
		if (np_nicevalue != null) {
			npNiceDelta = np_nicevalue.intValue() - getNpNice(sysEnv).intValue();
			setNpNice(sysEnv, np_nicevalue);
		}

		pr = getRawPriority(sysEnv);
		int rpDelta = (niceDelta + npNiceDelta ) * 100;
		if (pr != null) {
			if (rpDelta != 0) {
				ipr = pr.intValue() + rpDelta;
				setRawPriority(sysEnv, new Integer(ipr));
			}
		}
		fixChildPrioritiesAndNpNice(sysEnv, rpDelta, np_nicevalue);
	}

	public void setPriority(SystemEnvironment sysEnv, Integer priority)
	throws SDMSException
	{
		setRawPriority(sysEnv, new Integer(priority.intValue() * 100), true);
	}

	public void setRawPriority(SystemEnvironment sysEnv, Integer priority)
	throws SDMSException
	{
		setRawPriority(sysEnv, priority, false);
	}

	public void setRawPriority(SystemEnvironment sysEnv, Integer priority, boolean force)
	throws SDMSException
	{
		int irpr = priority.intValue();
		int cpr = getPriority(sysEnv).intValue();
		int crpr = getRawPriority(sysEnv).intValue();

		if (irpr == crpr) return;
		int ipr = irpr / 100;

		if(ipr > SchedulingThread.MIN_PRIORITY)
			ipr = SchedulingThread.MIN_PRIORITY;
		if(ipr < SchedulingThread.MAX_PRIORITY)
			ipr = SchedulingThread.MAX_PRIORITY;

		if(ipr < SystemEnvironment.priorityLowerBound && !force) {
			ipr = SystemEnvironment.priorityLowerBound;
		}

		if (cpr < SystemEnvironment.priorityLowerBound && !force)
			ipr = cpr;

		super.setRawPriority(sysEnv, priority);
		if (ipr != cpr) {
			super.setPriority(sysEnv, new Integer(ipr));
			SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.PRIORITY);
		}
	}

	public synchronized void releaseMaster(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		int state = getState(sysEnv).intValue();

		if(!id.equals(getMasterId(sysEnv))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03311061650",
					"Tried to release a nonmaster submitted entity ($1)", id));
		}
		if(state != CANCELLED && state != FINAL) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03311061638",
					"Tried to release a nonfinal submitted entity ($1)", id));
		}
		sysEnv.seVersionList.remove(getSeVersion(sysEnv).longValue());
		release(sysEnv);
	}

	synchronized void release(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		int i;

		Vector v = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, id);
		for(i = 0; i < v.size(); i++) {
			((SDMSSubmittedEntity) v.get(i)).release(sysEnv);
		}

		v = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, id);
		for(i = 0; i < v.size(); i++) {
			((SDMSHierarchyInstance) v.get(i)).release(sysEnv);
		}

		v = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, id);
		for(i = 0; i < v.size(); i++) {
			((SDMSDependencyInstance) v.get(i)).release(sysEnv);
		}

		v = SDMSDependencyInstanceTable.idx_requiredId.getVector(sysEnv, id);
		for(i = 0; i < v.size(); i++) {
			((SDMSDependencyInstance) v.get(i)).release(sysEnv);
		}

		v = SDMSEntityVariableTable.idx_smeId.getVector(sysEnv, id);
		for(i = 0; i < v.size(); i++) {
			((SDMSEntityVariable) v.get(i)).release(sysEnv);
		}

		v = SDMSKillJobTable.idx_smeId.getVector(sysEnv, id);
		for(i = 0; i < v.size(); i++) {
			((SDMSKillJob) v.get(i)).release(sysEnv);
		}

		v = SDMSSubmittedEntityStatsTable.idx_smeId.getVector(sysEnv, id);
		for(i = 0; i < v.size(); i++) {
			((SDMSSubmittedEntityStats) v.get(i)).release(sysEnv);
		}

		super.release(sysEnv);
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
		throws SDMSException
	{
		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
		if(sysEnv.cEnv.isUser()) {
			Long masterId = getMasterId(sysEnv);
			if(!masterId.equals(getId(sysEnv)) && masterId.longValue() != 0) {
				SDMSSubmittedEntity m = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, masterId);
				return m.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
			}
			if((p & SDMSPrivilege.OPERATE) != SDMSPrivilege.OPERATE) {
				long seVersion = getSeVersion(sysEnv).longValue();
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv), seVersion);
				long q = se.getPrivileges(sysEnv, SDMSPrivilege.OPERATE | SDMSPrivilege.MONITOR, false, checkGroups, seVersion);
				if (q != 0) {
					SDMSGroup g = SDMSGroupTable.getObject(sysEnv, getOwnerId(sysEnv));
					q = q & g.getPrivileges(sysEnv, SDMSPrivilege.OPERATE | SDMSPrivilege.MONITOR, false, checkGroups);
				}
				p = p | q;
			}
		}
		p = addImplicitPrivs(p);
		return p & checkPrivs;
	}

	public long addImplicitPrivs(long priv)
	{
		priv = super.addImplicitPrivs(priv);
		if((priv & SDMSPrivilege.OPERATE) == SDMSPrivilege.OPERATE) priv = priv | SDMSPrivilege.EDIT;
		return priv;
	}

	public void setToError(SystemEnvironment sysEnv, String msg)
		throws SDMSException
	{
		setState(sysEnv, new Integer(ERROR));
		if(msg != null) {
			setErrorMsg(sysEnv, msg);
		}
	}

	public String getURLName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return getId(sysEnv).toString();
	}

	public String getURL(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return "job " + getURLName(sysEnv);
	}

	public void updateStatistics(SystemEnvironment sysEnv)
	throws SDMSException
	{

		int statTs = 0;
		Integer statisticTs = getStatisticTs(sysEnv);
		if (statisticTs != null) {
			statTs = statisticTs.intValue();
		}
		int oldStatSelect = statTs % 10;
		statTs = statTs / 10;

		int newStatSelect = STAT_NONE;
		boolean suspended = getIsSuspended(sysEnv).intValue() != NOSUSPEND || getParentSuspended(sysEnv).intValue() > 0;

		int state = getState(sysEnv).intValue();

		switch (state) {
			case SUBMITTED:
				if (suspended) newStatSelect = STAT_SUSPEND;
				break;
			case UNREACHABLE:
			case DEPENDENCY_WAIT:
				if (suspended) newStatSelect = STAT_SUSPEND;
				else newStatSelect = STAT_DEPENDENCY_WAIT;
				break;

			case SYNCHRONIZE_WAIT:
				if (suspended) newStatSelect = STAT_SUSPEND;
				else newStatSelect = STAT_SYNCHRONIZE;
				break;

			case RESOURCE_WAIT:
				if (suspended) newStatSelect = STAT_SUSPEND;
				else newStatSelect = STAT_RESOURCE;
				break;
			case RUNNABLE:
			case STARTING:
				newStatSelect = STAT_JOBSERVER;
				break;
			case STARTED:
			case RUNNING:
			case TO_KILL:
			case BROKEN_ACTIVE:
			case CANCELLED:
			case FINAL:
			case KILLED:
				newStatSelect = STAT_NONE;
				break;

			case FINISHED:
				if (getJobIsRestartable(sysEnv).booleanValue())
					newStatSelect = STAT_RESTARTABLE;
				else if (suspended) newStatSelect = STAT_SUSPEND;
				else newStatSelect = STAT_CHILD_WAIT;
				break;
			case BROKEN_FINISHED:
			case ERROR:
				newStatSelect = STAT_RESTARTABLE;
				break;
		}

		if (newStatSelect != oldStatSelect) {
			Integer statTime = null;
			switch (oldStatSelect) {
				case STAT_DEPENDENCY_WAIT:
					statTime = getDependencyWaitTime(sysEnv);
					break;
				case STAT_SUSPEND:
					statTime = getSuspendTime(sysEnv);
					break;
				case STAT_SYNCHRONIZE:
					statTime = getSyncTime(sysEnv);
					break;
				case STAT_RESOURCE:
					statTime = getResourceTime(sysEnv);
					break;
				case STAT_JOBSERVER:
					statTime = getJobserverTime(sysEnv);
					break;
				case STAT_RESTARTABLE:
					statTime = getRestartableTime(sysEnv);
					break;
				case STAT_CHILD_WAIT:
					statTime = getChildWaitTime(sysEnv);
					break;
			}
			if (statTime == null)
				statTime = new Integer(0);
			int now = (int)((sysEnv.cEnv.last() - getSubmitTs(sysEnv).longValue()) / 1000);
			if (now < 0) now = 0;

			int delta = now - statTs;

			statTime = new Integer(statTime.intValue() + delta);

			switch (oldStatSelect) {
				case STAT_DEPENDENCY_WAIT:
					setDependencyWaitTime(sysEnv, statTime);
					break;
				case STAT_SUSPEND:
					setSuspendTime(sysEnv, statTime);
					break;
				case STAT_SYNCHRONIZE:
					setSyncTime(sysEnv, statTime);
					break;
				case STAT_RESOURCE:
					setResourceTime(sysEnv, statTime);
					break;
				case STAT_JOBSERVER:
					setJobserverTime(sysEnv, statTime);
					break;
				case STAT_RESTARTABLE:
					setRestartableTime(sysEnv, statTime);
					break;
				case STAT_CHILD_WAIT:
					setChildWaitTime(sysEnv, statTime);
					break;
			}
			setStatisticTs(sysEnv, new Integer(now * 10 + newStatSelect));
		}
	}

	public Integer evaluateTime(SystemEnvironment sysEnv, Integer time, Integer timeStamp, int selector)
	throws SDMSException
	{
		int t = 0;
		if (time != null)
			t = time.intValue();
		if (timeStamp != null) {
			int ts = timeStamp.intValue();
			int tsType = ts % 10;
			if (selector >= 0)
				ts = ts / 10;
			if (selector == -1 || tsType == selector) {
				int now = (int)((sysEnv.cEnv.last() - getSubmitTs(sysEnv).longValue()) / 1000);
				t += now - ts;
			}
		}
		return new Integer(t);
	}
}

class DependencyDefinitionListElement
{
	public long seVersion;
	public SDMSDependencyDefinition dd;

	public DependencyDefinitionListElement (long seVersion, SDMSDependencyDefinition dd)
	{
		this.seVersion = seVersion;
		this.dd = dd;
	}
}

class UnresolvedExternalDependency
{
	public int unresolvedHandling;
	public SDMSDependencyInstance di;

	public UnresolvedExternalDependency (int unresolvedHandling, SDMSDependencyInstance di)
	{
		this.unresolvedHandling = unresolvedHandling;
		this.di = di;
	}
}
