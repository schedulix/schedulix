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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.filter.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.jobserver.Config;

public class ListSubmitted extends Node
{

	public final static String __version = "@(#) $Id: ListSubmitted.java,v 2.32.2.2 2013/06/18 09:49:34 ronald Exp $";

	public final static int TREE = 0;
	public final static int LIST = 1;
	final static int sortCols[] = { 1, 2, 35 };
	final static int sortColsF[] = { 36, 2 };

	private final static String emptyString = new String("");
	private final ObjectFilter objFilter = new ObjectFilter()
	{

		public boolean checkPrivileges(SystemEnvironment sysEnv, SDMSProxy p) {
			return true;
		}
	};

	WithHash with;
	Vector filterItems;
	HashSet renderedJobs = new HashSet();
	Integer mode = new Integer(ListSubmitted.LIST);
	HashSet expandIds = new HashSet();
	HashSet hitList = new HashSet();
	HashSet pathhits = new HashSet();
	HashMap groupHash = new HashMap();
	HashMap scopeHash = new HashMap();
	HashMap httpHostHash = new HashMap();
	HashMap httpPortHash = new HashMap();
	HashMap esdHash = new HashMap();
	Vector parms = null;
	boolean filtered = false;

	private void initialize(Vector jv, WithHash w)
	{
		objFilter.jobVector = jv;
		with = w;
		if(with != null) {
			mode = (Integer) with.get(ParseStr.S_MODE);
			if(mode == null) mode = new Integer(ListSubmitted.LIST);
			if(with.containsKey(ParseStr.S_EXPAND))
				expandIds = (HashSet) with.get(ParseStr.S_EXPAND);
		}
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListSubmitted()
	{
		super();
		initialize(null, null);
	}

	public ListSubmitted(Vector j)
	{
		super();
		initialize(j, null);
	}

	public ListSubmitted(WithHash w)
	{
		super();
		initialize(null, w);
	}

	public ListSubmitted(Vector j, WithHash w)
	{
		super();
		initialize(j, w);
	}

	protected void getSingleJobs(SystemEnvironment sysEnv, Vector jv, Vector result)
		throws SDMSException
	{
		for(int i = 0; i < jv.size(); i++) {
			Long jobId = (Long) objFilter.jobVector.get(i);
			SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobId);
			result.addElement(sme);
		}
	}

	protected void getMasterJobs(SystemEnvironment sysEnv, Vector jv, Vector result)
		throws SDMSException
	{
		for(int i = 0; i < jv.size(); i++) {
			Vector v = SDMSSubmittedEntityTable.idx_masterId.getVector(sysEnv, jv.get(i));
			result.addAll(v);
		}
	}

	protected void getMastersFirst(SystemEnvironment sysEnv, Vector result, SDMSFilter filter)
		throws SDMSException
	{
		Vector v = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, null, filter);
		result.addAll(v);
	}

	protected Vector creator(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSThread.doTrace(sysEnv.cEnv, "Start creator : " + System.currentTimeMillis(), SDMSThread.SEVERITY_DEBUG);
		Vector result = new Vector();

		SDMSFilter filter = new SDMSFilter () {
			public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
				return objFilter.doFilter(sysEnv, obj);
			}
		};

		if(objFilter.jobVector != null) {
			SDMSThread.doTrace(sysEnv.cEnv, "type creator : getSingleJobs", SDMSThread.SEVERITY_DEBUG);
			getSingleJobs(sysEnv, objFilter.jobVector, result);
		} else if(objFilter.masterVector != null) {
			SDMSThread.doTrace(sysEnv.cEnv, "type creator : getMasterJobs", SDMSThread.SEVERITY_DEBUG);
			getMasterJobs(sysEnv, objFilter.masterVector, result);
		} else if(objFilter.mastersFirst == true) {
			SDMSThread.doTrace(sysEnv.cEnv, "type creator : getMastersFirst", SDMSThread.SEVERITY_DEBUG);
			getMastersFirst(sysEnv, result, filter);
		} else {

			SDMSThread.doTrace(sysEnv.cEnv, "type creator : Scan", SDMSThread.SEVERITY_DEBUG);
			Iterator i = SDMSSubmittedEntityTable.table.iterator(sysEnv, filter);
			filtered = true;

			while(i.hasNext()) {
				result.addElement(i.next());
			}
		}
		if (objFilter.hasFuture) {

			Iterator i;
			if (filtered)
				i = SDMSScheduledEventTable.table.iterator(sysEnv, filter);
			else
				i = SDMSScheduledEventTable.table.iterator(sysEnv);
			while(i.hasNext()) {
				SDMSScheduledEvent scev = (SDMSScheduledEvent)(i.next());

				if (!scev.getIsCalendar(sysEnv).booleanValue()) {

					SDMSSchedule sce = SDMSScheduleTable.getObject(sysEnv, scev.getSceId(sysEnv));
					if (sce.isReallyActive(sysEnv))
						result.addElement(scev);
				}
			}
			if (filtered)
				i = SDMSCalendarTable.table.iterator(sysEnv, filter);
			else
				i = SDMSCalendarTable.table.iterator(sysEnv);
			while(i.hasNext()) {
				SDMSCalendar cal = (SDMSCalendar) i.next();

				SDMSScheduledEvent scev = SDMSScheduledEventTable.getObject(sysEnv, cal.getScevId(sysEnv));
				SDMSSchedule sce = SDMSScheduleTable.getObject(sysEnv, scev.getSceId(sysEnv));
				if (sce.isReallyActive(sysEnv))
					result.addElement(cal);
			}
		}

		SDMSThread.doTrace(sysEnv.cEnv, "End creator : " + System.currentTimeMillis(), SDMSThread.SEVERITY_DEBUG);
		return result;
	}

	protected void renderResultFuture(SystemEnvironment sysEnv, SDMSProxy p, SDMSOutputContainer d_container, String pathhit)
		throws SDMSException
	{
		Long seId;

		String type;
		String owner;
		Integer priority;
		Integer niceValue;
		Integer minPriority;
		Integer agingAmount;
		String agingBase;
		String state;
		Long submitTs;
		String hit = "";
		Boolean is_suspended;
		String privs;
		String workdir;
		String logfile;
		String errlogfile;
		long actVersion;

		SDMSScheduledEvent scev = null;
		SDMSCalendar cal = null;

		if (p instanceof SDMSCalendar) {
			cal = (SDMSCalendar)p;
			scev = SDMSScheduledEventTable.getObject(sysEnv, cal.getScevId(sysEnv));
		} else
			scev = (SDMSScheduledEvent)p;

		SDMSEvent ev = SDMSEventTable.getObject(sysEnv, scev.getEvtId(sysEnv));
		actVersion = sysEnv.tx.versionId;
		Long actVersionObject = new Long(actVersion);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv), actVersion);

		Long tmp;
		Date d = new Date();

		seId = se.getId(sysEnv);

		type = se.getTypeAsString(sysEnv);

		tmp = scev.getOwnerId(sysEnv);
		owner = (String) groupHash.get(tmp);
		if(owner == null) {
			owner = SDMSGroupTable.getObject(sysEnv, tmp).getName(sysEnv);
			groupHash.put(tmp, owner);
		}
		state = "SCHEDULED";
		if (cal != null)
			tmp = cal.getStarttime(sysEnv);
		else {
			tmp = scev.getNextActivityTime(sysEnv);
		}
		if(tmp != null) {
			DateTime dt = new DateTime(tmp);
			submitTs = dt.toDate().getTime();
		} else submitTs = null;

		if (type.equals("JOB")) {
			priority = se.getPriority(sysEnv);
			niceValue = null;
		} else {
			priority = null;
			niceValue =  se.getPriority(sysEnv);
		}

		minPriority = se.getMinPriority(sysEnv);
		agingAmount = se.getAgingAmount(sysEnv);
		agingBase = "MINUTES";

		if(hitList.contains(p.getId(sysEnv)))	hit = "H";

		is_suspended = se.getSubmitSuspended(sysEnv);
		privs = se.getPrivileges(sysEnv).toString();

		workdir = se.getWorkdir(sysEnv);
		logfile = se.getLogfile(sysEnv);
		errlogfile = se.getErrlogfile(sysEnv);

		Vector parameterVector = new Vector();

		if(parms != null) {
			for(int i = 0; i < parms.size(); i++) {
				String w = (String) parms.get(i);
				if(w.equals(SDMSSubmittedEntity.S_KEY)) continue;
				String parmVal;
				try {
					parmVal = se.getVariableValue(sysEnv, w, actVersion);
				} catch(NotFoundException cee) {
					parmVal = emptyString;
				}
				parameterVector.add(parmVal);
			}
		}

		Vector v = new Vector();
		v.add(seId);
		v.add(null);
		v.add(se.pathString(sysEnv, actVersion));
		v.add(type);
		v.add(null);
		v.add(owner);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(state);
		v.add(Boolean.FALSE);
		v.add(Boolean.FALSE);
		v.add(null);
		v.add(null);
		v.add(Boolean.FALSE);
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
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(submitTs);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(priority);
		v.add(null);
		v.add(niceValue);
		v.add(minPriority);
		v.add(agingAmount);
		v.add(agingBase);
		v.add(null);
		v.add(null);
		v.add(hit);
		v.add(pathhit);
		v.add(null);
		v.add(is_suspended);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(null);
		v.add(privs);
		v.add(workdir);
		v.add(logfile);
		v.add(errlogfile);

		if(parameterVector.size() > 0)
			v.addAll(parameterVector);

		d_container.addData(sysEnv, v);
	}

	protected void renderResultJob(SystemEnvironment sysEnv, SDMSSubmittedEntity job, SDMSOutputContainer d_container, String pathhit)
		throws SDMSException
	{
		Vector paths;
		Long jobId;
		Long masterId;
		String type;
		Long parentId;
		String owner;
		String scope;
		String httpHost;
		String httpPort;
		Integer exitCode;
		String pid;
		String extPid;
		Integer priority;
		Integer dynPriority;
		Integer niceValue;
		Integer minPriority;
		Integer agingAmount;
		String agingBase;
		String errorMsg;
		String state;
		String jobExitState;
		String exitState;
		Boolean jobIsFinal;
		Integer cntRestartable;
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
		Integer cntWarn;
		Long submitTs;
		String resumeTs;
		String syncTs;
		String resourceTs;
		String runnableTs;
		String startTs;
		String finishTs;
		String finalTs;
		String hit = "";
		String is_suspended;
		Boolean is_restartable;
		Integer parent_suspended;
		Integer child_suspended;
		String child_tag;
		Boolean isReplaced;
		Boolean isDisabled;
		Boolean isCancelled;
		Integer warnCount;
		Integer pendingCount;
		String privs;
		String workdir;
		String logfile;
		String errlogfile;
		long actVersion;

		Long tmp;
		Date d = new Date();

		jobId = job.getId(sysEnv);

		if(renderedJobs.contains(jobId)) return;

		actVersion = job.getSeVersion(sysEnv).longValue();
		Long actVersionObject = new Long(actVersion);

		masterId = job.getMasterId(sysEnv);
		paths = job.pathStrings(sysEnv);

		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, job.getSeId(sysEnv), actVersion);
		type = se.getTypeAsString(sysEnv);

		parentId = job.getParentId(sysEnv);
		tmp = job.getOwnerId(sysEnv);
		owner = (String) groupHash.get(tmp);
		if(owner == null) {
			owner = SDMSGroupTable.getObject(sysEnv, tmp).getName(sysEnv);
			groupHash.put(tmp, owner);
		}
		tmp = job.getScopeId(sysEnv);
		if(tmp != null) {
			scope = (String) scopeHash.get(tmp);
			if(scope == null) {
				SDMSScope s = SDMSScopeTable.getObject(sysEnv, tmp);
				scope = s.pathString(sysEnv);
				scopeHash.put(tmp, scope);
				httpHost = ScopeConfig.getItem(sysEnv, s, Config.HTTP_HOST);
				httpHostHash.put(tmp, httpHost);
				httpPort = ScopeConfig.getItem(sysEnv, s, Config.HTTP_PORT);
				httpPortHash.put(tmp, httpPort);
			} else {
				httpHost = (String)httpHostHash.get(tmp);
				httpPort = (String)httpPortHash.get(tmp);
			}
		} else {
			scope    = null;
			httpHost = null;
			httpPort = null;
		}
		exitCode = job.getExitCode(sysEnv);
		pid = job.getPid(sysEnv);
		extPid = job.getExtPid(sysEnv);
		state = job.getStateAsString(sysEnv);
		isDisabled = job.getIsDisabled(sysEnv);
		isCancelled = job.getIsCancelled(sysEnv);
		if (isCancelled == null)
			if (job.getState(sysEnv).intValue() == SDMSSubmittedEntity.CANCELLED)
				isCancelled = Boolean.TRUE;
			else
				isCancelled = Boolean.FALSE;
		tmp = job.getJobEsdId(sysEnv);
		if(tmp != null) {
			SDMSKey k = new SDMSKey(tmp, actVersionObject);
			jobExitState = (String) esdHash.get(k);
			if(jobExitState == null) {
				jobExitState  = SDMSExitStateDefinitionTable.getObject(sysEnv, tmp, actVersion).getName(sysEnv);
				esdHash.put(k, jobExitState);
			}
		} else jobExitState = null;
		tmp = job.getFinalEsdId(sysEnv);
		if(tmp != null) {
			SDMSKey k = new SDMSKey(tmp, actVersionObject);
			exitState = (String) esdHash.get(k);
			if(exitState == null) {
				exitState = SDMSExitStateDefinitionTable.getObject(sysEnv, tmp, actVersion).getName(sysEnv);
				esdHash.put(k, exitState);
			}
		} else exitState = null;
		jobIsFinal = job.getJobIsFinal(sysEnv);
		cntRestartable = job.getCntRestartable(sysEnv);
		cntSubmitted = job.getCntSubmitted(sysEnv);
		cntDependencyWait = job.getCntDependencyWait(sysEnv);
		cntSynchronizeWait = job.getCntSynchronizeWait(sysEnv);
		cntResourceWait = job.getCntResourceWait(sysEnv);
		cntRunnable = job.getCntRunnable(sysEnv);
		cntStarting = job.getCntStarting(sysEnv);
		cntStarted = job.getCntStarted(sysEnv);
		cntRunning = job.getCntRunning(sysEnv);
		cntToKill = job.getCntToKill(sysEnv);
		cntKilled = job.getCntKilled(sysEnv);
		cntCancelled = job.getCntCancelled(sysEnv);
		cntFinished = job.getCntFinished(sysEnv);
		cntFinal = job.getCntFinal(sysEnv);
		cntBrokenActive = job.getCntBrokenActive(sysEnv);
		cntBrokenFinished = job.getCntBrokenFinished(sysEnv);
		cntError = job.getCntError(sysEnv);
		cntUnreachable = job.getCntUnreachable(sysEnv);
		cntWarn = job.getCntWarn(sysEnv);
		submitTs = job.getSubmitTs(sysEnv);
		tmp = job.getResumeTs(sysEnv);
		if(tmp != null) {
			d.setTime(tmp.longValue());
			resumeTs = sysEnv.systemDateFormat.format(d);
		} else resumeTs = null;
		tmp = job.getSyncTs(sysEnv);
		if(tmp != null) {
			d.setTime(tmp.longValue());
			syncTs = sysEnv.systemDateFormat.format(d);
		} else syncTs = null;
		tmp = job.getResourceTs(sysEnv);
		if(tmp != null) {
			d.setTime(tmp.longValue());
			resourceTs = sysEnv.systemDateFormat.format(d);
		} else resourceTs = null;
		tmp = job.getRunnableTs(sysEnv);
		if(tmp != null) {
			d.setTime(tmp.longValue());
			runnableTs = sysEnv.systemDateFormat.format(d);
		} else runnableTs = null;
		tmp = job.getStartTs(sysEnv);
		if(tmp != null) {
			d.setTime(tmp.longValue());
			startTs = sysEnv.systemDateFormat.format(d);
		} else startTs = null;
		tmp = job.getFinishTs(sysEnv);
		if(tmp != null) {
			d.setTime(tmp.longValue());
			finishTs = sysEnv.systemDateFormat.format(d);
		} else finishTs = null;
		tmp = job.getFinalTs(sysEnv);
		if(tmp != null) {
			d.setTime(tmp.longValue());
			finalTs = sysEnv.systemDateFormat.format(d);
		} else finalTs = null;
		priority = job.getPriority(sysEnv);
		if(se.getType(sysEnv).intValue() == SDMSSchedulingEntity.JOB) {
			dynPriority = new Integer(SystemEnvironment.sched.getDynPriority(sysEnv, job));
		} else {
			dynPriority = null;
		}
		niceValue = job.getNice(sysEnv);

		minPriority = job.getMinPriority(sysEnv);
		agingAmount = job.getAgingAmount(sysEnv);
		agingBase = "MINUTES";

		errorMsg = job.getErrorMsg(sysEnv);

		Vector c = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, job.getId(sysEnv));
		Integer numChilds = new Integer(c.size());
		if(hitList.contains(jobId))	hit = "H";

		if(pathhit.equals("P")) pathhits.add(jobId);
		String submitPath = "*";

		is_suspended = job.getIsSuspendedAsString(sysEnv);
		is_restartable = job.getJobIsRestartable(sysEnv);
		parent_suspended = job.getParentSuspended(sysEnv);

		child_tag = job.getChildTag(sysEnv);
		isReplaced = job.getIsReplaced(sysEnv);
		warnCount = job.getWarnCount(sysEnv);
		child_suspended = job.getChildSuspended(sysEnv);
		pendingCount = job.getCntPending(sysEnv);
		privs = job.getPrivileges(sysEnv).toString();

		workdir = job.getWorkdir(sysEnv);
		logfile = job.getLogfile(sysEnv);
		errlogfile = job.getErrlogfile(sysEnv);

		Vector parameterVector = new Vector();

		if(parms != null) {
			for(int i = 0; i < parms.size(); i++) {
				String w = (String) parms.get(i);
				if(w.equals(SDMSSubmittedEntity.S_KEY)) continue;
				String parmVal;
				try {
					parmVal = job.getVariableValue(sysEnv, w, true, ParseStr.S_DEFAULT);
				} catch(NotFoundException cee) {
					parmVal = emptyString;
				}
				parameterVector.add(parmVal);
			}
		}

		for(int i = 0; i < paths.size(); i++) {
			Vector v = new Vector();
			v.add(jobId);
			v.add(masterId);
			v.add(paths.get(i));
			v.add(type);
			v.add(parentId);
			v.add(owner);
			v.add(scope);
			v.add(httpHost);
			v.add(httpPort);
			v.add(exitCode);
			v.add(pid);
			v.add(extPid);
			v.add(state);
			v.add(isDisabled);
			v.add(isCancelled);
			v.add(jobExitState);
			v.add(exitState);
			v.add(jobIsFinal);
			v.add(cntRestartable);
			v.add(cntSubmitted);
			v.add(cntDependencyWait);
			v.add(cntSynchronizeWait);
			v.add(cntResourceWait);
			v.add(cntRunnable);
			v.add(cntStarting);
			v.add(cntStarted);
			v.add(cntRunning);
			v.add(cntToKill);
			v.add(cntKilled);
			v.add(cntCancelled);
			v.add(cntFinished);
			v.add(cntFinal);
			v.add(cntBrokenActive);
			v.add(cntBrokenFinished);
			v.add(cntError);
			v.add(cntUnreachable);
			v.add(cntWarn);
			v.add(submitTs);
			v.add(resumeTs);
			v.add(syncTs);
			v.add(resourceTs);
			v.add(runnableTs);
			v.add(startTs);
			v.add(finishTs);
			v.add(finalTs);
			v.add(priority);
			v.add(dynPriority);
			v.add(niceValue);
			v.add(minPriority);
			v.add(agingAmount);
			v.add(agingBase);
			v.add(errorMsg);
			v.add(numChilds);
			v.add(hit);
			v.add(pathhit);
			v.add(submitPath);
			v.add(is_suspended);
			v.add(is_restartable);
			v.add(parent_suspended);
			v.add(child_tag);
			v.add(isReplaced);
			v.add(warnCount);
			v.add(child_suspended);
			v.add(pendingCount);
			v.add(privs);
			v.add(workdir);
			v.add(logfile);
			v.add(errlogfile);

			if(parameterVector.size() > 0)
				v.addAll(parameterVector);
			submitPath = "";

			d_container.addData(sysEnv, v);
		}
		renderedJobs.add(jobId);
	}

	private boolean checkOrphans(SystemEnvironment sysEnv, Vector dataset)
		throws SDMSException
	{
		boolean foundOrphans = false;
		Iterator i = dataset.iterator();
		while(i.hasNext()) {
			Vector v = (Vector) i.next();
			if (((String)v.get(12)).equals("SCHEDULED"))
				continue;
			Long id = (Long) v.get(0);
			if(hitList.contains(id)) continue;
			Vector p = (Vector) v.get(2);

			int j = p.size() - 4;
			if(j > 0) {
				Long pId = (Long) p.get(j);
				if(pathhits.contains(pId)) continue;

				if(! (renderedJobs.contains(pId) && (expandIds == null || expandIds.contains(pId)))) {
					i.remove();
					foundOrphans = true;
				}
			}
		}
		return foundOrphans;
	}

	private void resolvePaths(SystemEnvironment sysEnv, Vector dataset)
		throws SDMSException
	{
		Iterator i = dataset.iterator();
		int len = 0;
		while(i.hasNext()) {
			Vector v = (Vector) i.next();
			if (((String)v.get(12)).equals("SCHEDULED"))
				continue;
			Vector p = (Vector) v.get(2);
			PathVector result = new PathVector(":");
			for(int j = 0; j < p.size(); j += 2) {
				Long id = (Long) p.get(j);
				String tag = (String) p.get(j+1);
				SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, id);
				long seVersion = sme.getSeVersion(sysEnv).longValue();
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), seVersion);
				PathVector name = se.pathVector(sysEnv, seVersion);
				if(tag != null) {
					String s = (String) name.lastElement() + "[" + tag + "]";
					name.set(name.size() - 1, s);
				}
				result.add(name);
			}
			v.set(2, result);
		}
		return;
	}

	private void formatSubmitTimes(SystemEnvironment sysEnv, Vector dataset)
		throws SDMSException
	{
		Date d = new Date();
		Iterator i = dataset.iterator();
		while(i.hasNext()) {
			Vector v = (Vector) i.next();
			Long st = (Long) v.get(37);
			if(st != null) {
				d.setTime(st.longValue());
				v.set(37, sysEnv.systemDateFormat.format(d));
			}
		}
		return;
	}

	private int convertPaths(SystemEnvironment sysEnv, Vector dataset)
		throws SDMSException
	{
		Iterator i = dataset.iterator();
		int len = 0;
		while(i.hasNext()) {
			Vector v = (Vector) i.next();
			String s;
			if (((String)v.get(12)).equals("SCHEDULED")) {
				s = (String)v.get(2);
			} else {
				PathVector p = (PathVector) v.get(2);
				s = p.toString();
				v.set(2, s);
			}
			len = java.lang.Math.max(len, s.length());
		}
		return len;
	}

	protected void renderResult1passDriver(SystemEnvironment sysEnv, SDMSSubmittedEntity job, SDMSOutputContainer d_container)
		throws SDMSException
	{
		Long jobId;

		jobId = job.getId(sysEnv);

		if(renderedJobs.contains(jobId)) return;

		if(mode.intValue() == ListSubmitted.TREE) {
			Vector p = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, jobId);
			for(int i = 0; i < p.size(); i++) {
				SDMSHierarchyInstance hi = (SDMSHierarchyInstance) p.get(i);
				SDMSSubmittedEntity parentJob = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
				renderResult1pass(sysEnv, parentJob, d_container);
			}
		}

	}

	protected void renderResult1pass(SystemEnvironment sysEnv, SDMSSubmittedEntity job, SDMSOutputContainer d_container)
		throws SDMSException
	{
		Long jobId;

		jobId = job.getId(sysEnv);

		if(renderedJobs.contains(jobId)) return;

		if(mode.intValue() == ListSubmitted.TREE) {
			Vector p = SDMSHierarchyInstanceTable.idx_childId.getVector(sysEnv, jobId);
			for(int i = 0; i < p.size(); i++) {
				SDMSHierarchyInstance hi = (SDMSHierarchyInstance) p.get(i);
				SDMSSubmittedEntity parentJob = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getParentId(sysEnv));
				renderResult1pass(sysEnv, parentJob, d_container);
			}
		}

		renderResultJob(sysEnv, job, d_container, "P");
	}

	protected void renderResult3pass(SystemEnvironment sysEnv, SDMSSubmittedEntity job, SDMSOutputContainer d_container, HashSet expandIds)
		throws SDMSException
	{
		Long jobId;

		jobId = job.getId(sysEnv);

		if(expandIds == null || expandIds.contains(jobId)) {
			Vector c = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, job.getId(sysEnv));

			for(int i = 0; i < c.size(); i++) {
				SDMSHierarchyInstance hi = (SDMSHierarchyInstance) c.get(i);
				SDMSSubmittedEntity childJob = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
				renderResult3pass(sysEnv, childJob, d_container, expandIds);
			}
		}

		renderResultJob(sysEnv, job, d_container, "");
	}

	protected void createDescription(SystemEnvironment sysEnv, Vector desc)
		throws SDMSException
	{

		desc.add("ID");

		desc.add("MASTER_ID");

		desc.add("HIERARCHY_PATH");

		desc.add("SE_TYPE");

		desc.add("PARENT_ID");
		desc.add("OWNER");

		desc.add("SCOPE");

		desc.add("HTTPHOST");

		desc.add("HTTPPORT");

		desc.add("EXIT_CODE");

		desc.add("PID");

		desc.add("EXTPID");

		desc.add("STATE");

		desc.add("IS_DISABLED");

		desc.add("IS_CANCELLED");

		desc.add("JOB_ESD");

		desc.add("FINAL_ESD");

		desc.add("JOB_IS_FINAL");

		desc.add("CNT_RESTARTABLE");

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

		desc.add("CNT_UNREACHABLE");

		desc.add("CNT_WARN");

		desc.add("SUBMIT_TS");

		desc.add("RESUME_TS");

		desc.add("SYNC_TS");

		desc.add("RESOURCE_TS");

		desc.add("RUNNABLE_TS");

		desc.add("START_TS");

		desc.add("FINISH_TS");

		desc.add("FINAL_TS");

		desc.add("PRIORITY");

		desc.add("DYNAMIC_PRIORITY");

		desc.add("NICEVALUE");

		desc.add("MIN_PRIORITY");

		desc.add("AGING_AMOUNT");

		desc.add("AGING_BASE");

		desc.add("ERROR_MSG");

		desc.add("CHILDREN");

		desc.add("HIT");

		desc.add("HITPATH");

		desc.add("SUBMITPATH");

		desc.add("IS_SUSPENDED");

		desc.add("IS_RESTARTABLE");

		desc.add("PARENT_SUSPENDED");

		desc.add("CHILDTAG");

		desc.add("IS_REPLACED");

		desc.add("WARN_COUNT");

		desc.add("CHILD_SUSPENDED");

		desc.add("CNT_PENDING");
		desc.add("PRIVS");

		desc.add("WORKDIR");

		desc.add("LOGFILE");

		desc.add("ERRLOGFILE");

		if(with != null) {
			parms = (Vector) with.get(ParseStr.S_PARAMETERS);
			if(parms != null) {
				for(int i = 0; i < parms.size(); i++) {
					String w = (String) parms.get(i);
					if(w.equals(SDMSSubmittedEntity.S_KEY)) continue;
					desc.add("P_" + w);
				}
			}
		} else parms = null;
	}

	public void go(final SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector desc = new Vector();
		SDMSSubmittedEntity job;
		Vector resultVector;

		if(with != null) {
			Vector fi = (Vector) with.get(ParseStr.S_FILTER);
			filterItems = objFilter.initialize_filter(sysEnv, fi, 0, true);
		} else filterItems = new Vector();
		if(filterItems.size() == 0 && objFilter.jobVector == null) {
			Vector v = new Vector();
			v.addElement(new MasterFilter(sysEnv, null));
			objFilter.mastersFirst = true;
			filterItems.addElement(Boolean.TRUE);
			filterItems.addElement(v);
			objFilter.setFilter(filterItems);
		}

		createDescription(sysEnv, desc);
		final SDMSOutputContainer d_container = new SDMSOutputContainer(sysEnv,
				new SDMSMessage(sysEnv, "03201170303", "List of Submitted Entities"), desc);

		resultVector = creator(sysEnv);

		Iterator j = resultVector.iterator();
		while(j.hasNext()) {
			SDMSProxy p = (SDMSProxy)j.next();
			if (p instanceof SDMSSubmittedEntity) {
				job = (SDMSSubmittedEntity) p;
				if(!job.checkPrivileges(sysEnv, SDMSPrivilege.MONITOR)) {
					j.remove();
					continue;
				}
			} else if (p instanceof SDMSScheduledEvent) {
				SDMSEvent ev = SDMSEventTable.getObject(sysEnv, ((SDMSScheduledEvent)p).getEvtId(sysEnv));
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
				SDMSPrivilege q = se.getPrivileges(sysEnv);
				if(!(q.can(SDMSPrivilege.OPERATE) || q.can(SDMSPrivilege.MONITOR))) {
					j.remove();
					continue;
				}
			} else if (p instanceof SDMSCalendar) {
				SDMSScheduledEvent scev = SDMSScheduledEventTable.getObject(sysEnv, ((SDMSCalendar)p).getScevId(sysEnv));
				SDMSEvent ev = SDMSEventTable.getObject(sysEnv, scev.getEvtId(sysEnv));
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
				SDMSPrivilege q = se.getPrivileges(sysEnv);
				if(!(q.can(SDMSPrivilege.OPERATE) || q.can(SDMSPrivilege.MONITOR))) {
					j.remove();
					continue;
				}
			}
			if(! objFilter.doFilter(sysEnv, p)) {
				j.remove();
				continue;
			}
			hitList.add(p.getId(sysEnv));
		}

		j = resultVector.iterator();
		while(j.hasNext()) {
			SDMSProxy p = (SDMSProxy)j.next();
			if (p instanceof SDMSSubmittedEntity) {
				job = (SDMSSubmittedEntity) p;
				renderResult1passDriver(sysEnv, job, d_container);
			}
		}

		j = resultVector.iterator();
		while(j.hasNext()) {
			SDMSProxy p = (SDMSProxy)j.next();
			if (p instanceof SDMSSubmittedEntity) {
				job = (SDMSSubmittedEntity) p;
				renderResultJob(sysEnv, job, d_container, "");
			} else {
				renderResultFuture(sysEnv, p, d_container, "");
			}
		}

		Vector renderCopy = new Vector(renderedJobs);
		j = renderCopy.iterator();
		while(j.hasNext()) {
			Long eId = (Long) j.next();
			SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, eId);
			renderResult3pass(sysEnv, sme, d_container, expandIds);
		}

		boolean didRemove = true;
		while (didRemove) {
			didRemove = checkOrphans(sysEnv, d_container.dataset);
		}

		d_container.lines = d_container.dataset.size();

		resolvePaths(sysEnv, d_container.dataset);

		Collections.sort(d_container.dataset, new Comparator() {
			Comparator c1 = d_container.getComparator(sysEnv, sortCols);
			Comparator c2 = d_container.getComparator(sysEnv, sortColsF);
			public int compare (Object o1, Object o2) {
				Vector v1, v2;
				try {
					v1 = (Vector) o1;
					v2 = (Vector) o2;
				} catch (ClassCastException c) {
					throw new RuntimeException("Classes do not match: o1 = " + o1.toString() + ", o2 = " + o2.toString());
				}

				if ((v1.get(sortCols[0]) == null) && (v2.get(sortCols[0]) == null)) return c2.compare(o1, o2);
				else return c1.compare(o1, o2);
			}
		});

		formatSubmitTimes(sysEnv, d_container.dataset);

		int maxlength = convertPaths(sysEnv, d_container.dataset);
		d_container.setWidth(sysEnv, 2, maxlength);

		result.setOutputContainer(d_container);
		result.setFeedback(new SDMSMessage(sysEnv, "03201170305", "$1 Object(s) found",
							new Integer(d_container.lines)));
	}

}

