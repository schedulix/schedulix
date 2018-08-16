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
package de.independit.scheduler.server;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.sql.*;
import java.math.*;

import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.util.*;

public class SchedulingThread extends InternalSession
{

	public final static String name = "SchedulingThread";

	private boolean needSched;
	private boolean needReSched;
	private long priorityDelay;
	private prioComparator pc;
	private long timeoutWakeup;
	private long lastSchedule;
	private Locklist publl = null;
	private Vector<Long> resourceRequestList = null;
	private final Object resourceRequestLock = new Object();
	private final Integer lock = new Integer(0);
	private Vector<Long> actualRequestList;

	public static final int CREATE	= 1;
	public static final int ALTER	 = 2;
	public static final int DELETE	= 3;
	public static final int REGISTER        = 4;
	public static final int DEREGISTER      = 5;
	public static final int SUSPEND         = 6;
	public static final int RESUME	= 7;
	public static final int SHUTDOWN        = 8;
	public static final int FINISH	= 9;
	public static final int STATECHANGE     = 10;
	public static final int COPY	  = 11;
	public static final int IGNORE_RESOURCE = 12;
	public static final int MOVE		= 13;
	public static final int PRIORITY	= 14;
	public static final int ALTER_REQAMOUNT	= 15;
	public static final int OFFLINE_ONLINE  = 16;
	public static final int RERUN	 = 17;
	public static final int SUBMIT	= 18;

	public static final int MAX_PRIORITY	= 0;
	public static final int DEFAULT_PRIORITY = 50;
	public static final int MIN_PRIORITY	= 100;

	public final static int FP_SCOPE	= 0;
	public final static int FP_FOLDER	= 1;
	public final static int FP_LOCAL	= 2;

	private final static Integer ONE = new Integer(1);

	public long envhit = 0;
	public long envmiss = 0;

	private long timer = 0;
	private	java.util.Date dts = new java.util.Date();

	public SchedulingThread(SystemEnvironment env, SyncFifo f)
		throws SDMSException
	{
		super(name);
		NR = 1234321;

		initThread(env, f, NR, name, SystemEnvironment.scheduleWakeupInterval*1000);

		priorityDelay = SystemEnvironment.priorityDelay;
		priorityDelay *= 60000;
		needSched = true;
		needReSched = true;

		if(pc == null)
			pc = new prioComparator(env, priorityDelay);
		timeoutWakeup = Long.MAX_VALUE;
		lastSchedule = 0;
	}

	protected Node getNode(int m)
	{
		if(m == INITIALIZE)	return new DoSchedule(DoSchedule.INITIALIZE);
		return new DoSchedule();
	}

	public int getDynPriority(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		prioComparator myPc = new prioComparator(sysEnv, priorityDelay);
		myPc.setNow();
		return myPc.dynPrio(sme);
	}

	public boolean isBlocked(SystemEnvironment sysEnv, Long smeId, Long rId)
		throws SDMSException
	{
		Reservator r;
		synchronized(lock) {
			if(publl == null) return false;
			r = publl.get(rId, smeId);
		}
		if(r == null) return false;
		return (r.seq > 1 ? true : false);
	}

	public void addToRequestList(SystemEnvironment sysEnv, Long smeId)
	{
		synchronized (resourceRequestLock) {
			if (sysEnv.tx.resourceRequestList == null)
				sysEnv.tx.resourceRequestList = new Vector<Long>();
			sysEnv.tx.resourceRequestList.add(smeId);
		}
	}

	public void publishRequestList(SystemEnvironment sysEnv)
	{
		if (sysEnv.tx.resourceRequestList == null) return;
		addToRequestList(sysEnv.tx.resourceRequestList);
	}

	private void addToRequestList(Vector v)
	{
		synchronized (resourceRequestLock) {
			if (resourceRequestList == null)
				resourceRequestList = new Vector<Long>();
			resourceRequestList.addAll(v);
		}
	}

	private Vector getRequestList()
	{
		Vector retval;
		synchronized (resourceRequestLock) {
			retval = resourceRequestList;
			resourceRequestList = null;
		}
		return (retval == null ? new Vector<Long>() : retval);
	}

	private void processRequestList(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector<Long> v = getRequestList();
		actualRequestList = v;
		try {
			for (int i = 0; i < v.size(); ++i) {
				SDMSSubmittedEntity sme;
				Long smeId = v.get(i);
				Integer oldState;
				int os;
				try {
					sme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, smeId);
					oldState = sme.getOldState(sysEnv);
					if (oldState == null) {
						continue;
					}
					os = oldState.intValue();
				} catch (NotFoundException nfe) {
					continue;
				}
				int state = sme.getState(sysEnv).intValue();
				if (state == SDMSSubmittedEntity.DEPENDENCY_WAIT) {
					requestSyncSme(sysEnv, sme, oldState.intValue());

					if (sme.getState(sysEnv).intValue() != SDMSSubmittedEntity.ERROR) {
						if (os == SDMSSubmittedEntity.SUBMITTED ||
						    os == SDMSSubmittedEntity.DEPENDENCY_WAIT ||
						    os == SDMSSubmittedEntity.ERROR ||
						    os == SDMSSubmittedEntity.UNREACHABLE)
							sme.checkDependencies(sysEnv);
						else
							sme.setState(sysEnv, SDMSSubmittedEntity.SYNCHRONIZE_WAIT);
					}
				} else {
					if (state == SDMSSubmittedEntity.SYNCHRONIZE_WAIT) {
						reevaluateJSAssignment(sysEnv, sme);
						requestSysSme(sysEnv, sme);
					} else {
					}
				}
				needSched = true;
			}
		} catch (SDMSException e) {
			throw e;
		}
	}

	protected void scheduleProtected(SystemEnvironment sysEnv)
		throws SDMSException
	{
		try {
			schedule(sysEnv);
		} catch (Throwable e) {

			if (e instanceof SerializationException) {
				if (actualRequestList != null) {
					addToRequestList(actualRequestList);
				}
				throw e;
			} else {
				StringWriter stackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTrace));
				doTrace(sysEnv.cEnv, "Schedule threw an exception; server will abort " + e.toString() + ':' + e.getMessage() + "\n" + stackTrace.toString(), SEVERITY_FATAL);
				System.exit(1);
			}
		}
		actualRequestList = null;
	}

	private void schedule(SystemEnvironment sysEnv)
		throws SDMSException
	{
		dts = new java.util.Date();
		timer = dts.getTime();

		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		processRequestList(sysEnv);

		if(needReSched) {
			doTrace(cEnv, "==============> Start Resource Rescheduling <=================\nStartTime = 0", SEVERITY_MESSAGE);
			SDMSnpJobFootprintTable.table.clearTableUnlocked(sysEnv);
			reschedule(sysEnv);
			doTrace(cEnv, "==============> End Resource Rescheduling   <=================\nEndTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
			needSched = true;
		}
		if(!needSched) {
			long ts = dts.getTime() - timeoutWakeup;
			if((ts < 0) && (timer < lastSchedule + 10000 )) {
				return;
			}
		}
		lastSchedule = timer;

		Locklist resourceChain = new Locklist();

		doTrace(cEnv, "---------------> Start Synchronize Resource Scheduling <-------------------\nStartTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
		needSched = false;
		syncSchedule(sysEnv, resourceChain);
		doTrace(cEnv, "---------------> Start System Resource Scheduling <-------------------\nStartTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
		resourceSchedule(sysEnv, resourceChain);
		synchronized(lock) {
			publl = resourceChain;
		}

		doTrace(cEnv, "---------------> Start Cleanup LifeTables   <-------------------\nStartTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
		long purgeLow = sysEnv.roTxList.first(sysEnv);
		doTrace(cEnv, "purgeLow = " + purgeLow, SEVERITY_MESSAGE);
		doTrace(cEnv, "purgeSetSize = " + sysEnv.nvPurgeSet.size(), SEVERITY_MESSAGE);

		sysEnv.nvPurgeSet.purge(sysEnv, purgeLow);

		doTrace(cEnv, "---------------> End Resource Scheduling   <-------------------\nEndTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
	}

	public boolean getNextJobSchedule(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(needReSched)
			return false;

		if (sysEnv.maxWriter > 1) {
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);
			if(needReSched)
				return false;
		}

		HashSet myGroups = new HashSet();
		myGroups.add(SDMSObject.adminGId);
		sysEnv.cEnv.pushGid(sysEnv, myGroups);
		sysEnv.cEnv.setUser();
		try {
			scheduleProtected(sysEnv);
		} finally {
			sysEnv.cEnv.popGid(sysEnv);
			sysEnv.cEnv.setJobServer();
		}
		return true;
	}

	public boolean getPoolSchedule(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(needReSched)
			return false;

		if (sysEnv.maxWriter > 1) {
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);
			if(needReSched)
				return false;
		}

		scheduleProtected(sysEnv);

		return true;
	}

	private void reschedule(SystemEnvironment sysEnv)
		throws SDMSException
	{
		pc.setNow();
		needReSched = false;

		Vector sv = SDMSScopeTable.idx_type.getVectorForUpdate(sysEnv, new Integer(SDMSScope.SERVER));

		Vector rjv = SDMSSubmittedEntityTable.idx_state.getVectorForUpdate(sysEnv, new Integer(SDMSSubmittedEntity.RUNNABLE), null, Integer.MAX_VALUE);
		doTrace(cEnv, "Number of Runnable Jobs found: " + rjv.size(), SEVERITY_MESSAGE);

		doTrace(cEnv, "==============> Rescheduling Runnables <=================\nStartTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
		rescheduleVector(sysEnv, rjv, sv, SDMSSubmittedEntity.RUNNABLE);

		doTrace(cEnv, "==============> Rescheduling Resource Wait <=================\nStartTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
		Vector smev = SDMSSubmittedEntityTable.idx_state.getVectorForUpdate(sysEnv, new Integer(SDMSSubmittedEntity.RESOURCE_WAIT), null, Integer.MAX_VALUE);
		doTrace(cEnv, "Number of Jobs in Resource Wait found: " + smev.size(), SEVERITY_MESSAGE);

		rescheduleVector(sysEnv, smev, sv, SDMSSubmittedEntity.RESOURCE_WAIT);

		doTrace(cEnv, "==============> Rescheduling Synchronize Wait <=================\nStartTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
		smev = SDMSSubmittedEntityTable.idx_state.getVectorForUpdate(sysEnv, new Integer(SDMSSubmittedEntity.SYNCHRONIZE_WAIT), null, Integer.MAX_VALUE);
		doTrace(cEnv, "Number of Jobs in Synchronize Wait found: " + smev.size(), SEVERITY_MESSAGE);

		rescheduleVector(sysEnv, smev, sv, SDMSSubmittedEntity.SYNCHRONIZE_WAIT);

		doTrace(cEnv, "==============> Rescheduling Dependency Wait <=================\nStartTime = " + (dts.getTime() - timer), SEVERITY_MESSAGE);
		smev = SDMSSubmittedEntityTable.idx_state.getVector(sysEnv, new Integer(SDMSSubmittedEntity.DEPENDENCY_WAIT), null, Integer.MAX_VALUE);
		doTrace(cEnv, "Number of Jobs in Dependency Wait found: " + smev.size(), SEVERITY_MESSAGE);

		rescheduleVector(sysEnv, smev, sv, SDMSSubmittedEntity.DEPENDENCY_WAIT);
	}

	private void rescheduleVector(SystemEnvironment sysEnv, Vector smev, Vector sv, int maxState)
		throws SDMSException
	{
		SDMSSubmittedEntity sme;
		SDMSSchedulingEntity se;
		SDMSResourceAllocation ra;
		Long smeId;
		boolean suspended;
		Locklist ll = new Locklist();
		long actVersion;

		Collections.sort(smev, pc);

		for(int i = 0; i < smev.size(); i++) {
			sme = (SDMSSubmittedEntity) smev.get(i);
			actVersion = sme.getSeVersion(sysEnv).longValue();
			se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
			if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) continue;
			smeId = sme.getId(sysEnv);

			if(sme.getIsSuspended(sysEnv).intValue() != SDMSSubmittedEntity.NOSUSPEND || sme.getParentSuspended(sysEnv).intValue() > 0)
				suspended = true;
			else
				suspended = false;

			Vector v = SDMSRunnableQueueTable.idx_smeId.getVectorForUpdate(sysEnv, smeId);
			for(int j = 0; j < v.size(); j++) {
				((SDMSRunnableQueue) v.get(j)).delete(sysEnv);
			}
			v = SDMSResourceAllocationTable.idx_smeId.getVectorForUpdate(sysEnv, smeId);
			for(int j = 0; j < v.size(); j++) {
				ra = (SDMSResourceAllocation) v.get(j);
				int allocType = ra.getAllocationType(sysEnv).intValue();
				if(allocType != SDMSResourceAllocation.ALLOCATION &&
				   allocType != SDMSResourceAllocation.IGNORE &&
				   !ra.getIsSticky(sysEnv).booleanValue()) {
					ra.delete(sysEnv, false, true);
				}
			}

			requestSyncSme(sysEnv, sme, SDMSSubmittedEntity.DEPENDENCY_WAIT);

			if(sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.ERROR)
				continue;

			reevaluateJSAssignment(sysEnv, sme);

			if(sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.ERROR)
				continue;

			if(maxState == SDMSSubmittedEntity.RUNNABLE ||
			   maxState == SDMSSubmittedEntity.RESOURCE_WAIT ||
			   maxState == SDMSSubmittedEntity.SYNCHRONIZE_WAIT) {
				requestSysSme(sysEnv, sme);
			}

			if((maxState == SDMSSubmittedEntity.RUNNABLE ||
			    maxState == SDMSSubmittedEntity.RESOURCE_WAIT) ||
			   (maxState == SDMSSubmittedEntity.SYNCHRONIZE_WAIT && !suspended))
				syncScheduleSme(sysEnv, sme, ll);

			if(maxState == SDMSSubmittedEntity.RUNNABLE ||
			   (sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.RESOURCE_WAIT && !suspended))
				resourceScheduleSme(sysEnv, sme, ll);

		}
	}

	public void syncSchedule(SystemEnvironment sysEnv, Locklist resourceChain)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		SDMSSubmittedEntity sme;
		int i;

		Vector smev = SDMSSubmittedEntityTable.idx_state.getVectorForUpdate(sysEnv, new Integer(SDMSSubmittedEntity.SYNCHRONIZE_WAIT), null, Integer.MAX_VALUE);
		Vector sv = SDMSScopeTable.idx_type.getVector(sysEnv, new Integer(SDMSScope.SERVER));
		doTrace(cEnv, "Number of Job Server : " + sv.size(), SEVERITY_DEBUG);
		doTrace(cEnv, "Number of Jobs in SYNCHRONIZE_WAIT : " + smev.size(), SEVERITY_DEBUG);
		if(sv.size() == 0) {
			return;
		}

		timeoutWakeup = Long.MAX_VALUE;
		pc.setNow();
		Collections.sort(smev, pc);

		for(i = 0; i < smev.size(); ++i) {
			sme = (SDMSSubmittedEntity) smev.get(i);
			if(sme.getIsSuspended(sysEnv).intValue() != SDMSSubmittedEntity.NOSUSPEND ||
			   sme.getParentSuspended(sysEnv).intValue() > 0			  ||
			   sme.getOldState(sysEnv) != null)
				continue;
			syncScheduleSme(sysEnv, sme, resourceChain);
		}
	}

	public void requestSyncSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, int oldState)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		long actVersion = sme.getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) return;

		Vector sv = getServerList(sysEnv, sme, se, actVersion);

		requestResourceSme(sysEnv, sme, se, sv, SDMSNamedResource.SYNCHRONIZING, actVersion, oldState);
		sme.setOldState(sysEnv, null);
	}

	public void requestSysSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		long actVersion = sme.getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) return;

		Vector sv = findRelevantJobserver (sysEnv, sme);

		requestResourceSme(sysEnv, sme, se, sv, SDMSNamedResource.SYSTEM, actVersion, SDMSSubmittedEntity.SYNCHRONIZE_WAIT);
		sme.setOldState(sysEnv, null);
	}

	private Vector getServerList(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se, long actVersion)
		throws SDMSException
	{
		Vector cacheEntry;
		Vector result = null;
		Long validFrom;
		Long validTo;

		if (sysEnv.tx.envJSMap == null)
			sysEnv.tx.envJSMap = new HashMap();
		HashMap envJSMap = sysEnv.tx.envJSMap;

		Long envId = se.getNeId(sysEnv);

		cacheEntry = (Vector) envJSMap.get(envId);
		if (cacheEntry != null) {
			for (int i = 0; i < cacheEntry.size(); ++i) {
				Vector v = (Vector) cacheEntry.get(i);
				validFrom = (Long) v.get(0);
				validTo = (Long) v.get(1);
				if (validFrom.longValue() < actVersion && validTo.longValue() >= actVersion) {
					result = (Vector) v.get(2);
					++envhit;
					break;
				}
			}
		}
		if (cacheEntry == null || result == null) {
			++envmiss;
			Vector envv = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, envId, actVersion);
			SDMSNamedEnvironment ne = SDMSNamedEnvironmentTable.getObject(sysEnv, envId, actVersion);
			validFrom = new Long(ne.getValidFrom(sysEnv));
			validTo = new Long(ne.getValidTo(sysEnv));
			result = SDMSScopeTable.idx_type.getVectorForUpdate(sysEnv, new Integer(SDMSScope.SERVER));
			Iterator i = result.iterator();
			while (i.hasNext()) {
				SDMSScope s = (SDMSScope) i.next();
				if (!s.getIsRegistered(sysEnv).booleanValue()) {
					i.remove();
					continue;
				}

				Long sId = s.getId(sysEnv);
				SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUniqueForUpdate(sysEnv, sId);
				HashMap sfp = npsfp.getFp(sysEnv);
				for (int j = 0; j < envv.size(); ++j) {
					SDMSEnvironment env = (SDMSEnvironment) envv.get(j);
					Long nrId = env.getNrId(sysEnv);
					if(!sfp.containsKey(nrId)) {
						i.remove();
						break;
					}
					SDMSResource r = SDMSResourceTable.getObjectForUpdate(sysEnv, (Long) sfp.get(nrId));
					if(!r.getIsOnline(sysEnv).booleanValue()) {
						i.remove();
						break;
					}
				}
			}
			Vector v = new Vector();
			v.add(validFrom);
			v.add(validTo);
			v.add(result);
			if (cacheEntry == null) cacheEntry = new Vector();
			cacheEntry.add(v);
			envJSMap.put(envId, cacheEntry);
		}
		return result;
	}

	public static void allocateAndReleaseResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSScope s)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, sysEnv.sched, ObjectLock.EXCLUSIVE);

		Long rId;
		Long nrId;
		Long srId;
		SDMSResource r, sr;
		SDMSResourceAllocation ra;

		SDMSnpJobFootprint npjfp;
		try {
			npjfp = SDMSnpJobFootprintTable.idx_smeId_getUnique(sysEnv, sme.getId(sysEnv));
		} catch (NotFoundException nfe) {
			getJobFootprint(sysEnv, sme);
			npjfp = SDMSnpJobFootprintTable.idx_smeId_getUnique(sysEnv, sme.getId(sysEnv));
		}

		HashMap fpLocal = npjfp.getFpLocal(sysEnv);
		HashMap fpFolder = npjfp.getFpFolder(sysEnv);

		SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
		HashMap sfp = npsfp.getFp(sysEnv);

		Vector v = SDMSResourceAllocationTable.idx_smeId.getVectorForUpdate(sysEnv, sme.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			ra = (SDMSResourceAllocation) v.get(i);
			rId = ra.getRId(sysEnv);
			r = SDMSResourceTable.getObjectForUpdate(sysEnv, rId);
			nrId = r.getNrId(sysEnv);
			if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.RESERVATION) {
				if(fpFolder.containsKey(nrId) || fpLocal.containsKey(nrId)) {
					ra.setAllocationType(sysEnv, new Integer(SDMSResourceAllocation.ALLOCATION));
				} else {
					srId = (Long) sfp.get(nrId);
					sr = SDMSResourceTable.getObjectForUpdate(sysEnv, srId);
					if(sr.getId(sysEnv).equals(rId)) {
						ra.setAllocationType(sysEnv, new Integer(SDMSResourceAllocation.ALLOCATION));
					}
				}
				if (ra.getIsSticky(sysEnv).booleanValue()) {
				}
			}
			if (ra.getAllocationType(sysEnv).intValue() != SDMSResourceAllocation.ALLOCATION) ra.delete(sysEnv, true, true);
		}

		SystemEnvironment.sched.needSched = true;
	}

	private void  requestResourceSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se, Vector sv, int type, long actVersion, int oldState)
		throws SDMSException
	{

		SDMSScope s;
		HashMap masterMap = new HashMap();
		Long smeId = sme.getId(sysEnv);
		Integer smeState = sme.getState(sysEnv);
		boolean fitsSomewhere = false;
		HashMap smefp = (HashMap) getJobFootprint(sysEnv, sme).get(FP_SCOPE);

		if(sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.ERROR)
			return;

		Vector jsv = new Vector();
		Vector v;

		if(checkKeptResources(sysEnv, smeId, jsv, sv))	v = jsv;
		else						v = sv;

		jsv = new Vector();
		if (checkStickyResources(sysEnv, smeId, smefp, jsv, v)) v = jsv;

		try {
			requestLocalResourceSme(sysEnv, sme, type, masterMap, oldState);
			requestFolderResourceSme(sysEnv, sme, type, masterMap, oldState);
		} catch (SDMSEscape e) {
			sme.setToError(sysEnv, "Sticky Resource resolution conflict (resource to allocate not visible)");
			Long tMasterId = sme.getMasterId(sysEnv);
			SDMSSubmittedEntity tMsme = SDMSSubmittedEntityTable.getObject(sysEnv, tMasterId);
			tMsme.suspend(sysEnv, false, false);
		}

		for(int j = 0; j < v.size(); ++j) {
			s = (SDMSScope) v.get(j);
			if (!s.getIsRegistered(sysEnv).booleanValue()) continue;
			Long sId = s.getId(sysEnv);

			if(!s.canExecute(sysEnv, sme))
				continue;

			SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
			HashMap sfp = npsfp.getFp(sysEnv);

			sysEnv.tx.beginSubTransaction(sysEnv);
			try {
				if(fits(sysEnv, sfp, smefp, sme, false, null)) {
					requestResources(sysEnv, sme, se, actVersion, sfp, type, smefp, masterMap, oldState);

					try {
						SDMSRunnableQueue rq = SDMSRunnableQueueTable.idx_smeId_scopeId_getUniqueForUpdate(sysEnv, new SDMSKey(smeId, sId));
						rq.setState(sysEnv, smeState);
					} catch (NotFoundException nfe) {
						SDMSRunnableQueueTable.table.create(sysEnv, smeId, sId, smeState);
					}
					fitsSomewhere = true;
				}
			} catch (SDMSEscape e) {
				sysEnv.tx.rollbackSubTransaction(sysEnv);
				continue;
			} catch (Exception e) {
				doTrace(cEnv, ": Job " + smeId + " run into an Exception during Resource Scheduling : " + e.toString(), SEVERITY_WARNING);
				sysEnv.tx.rollbackSubTransaction(sysEnv);
				throw e;
			}
			sysEnv.tx.commitSubTransaction(sysEnv);
		}

		if (type == SDMSNamedResource.SYNCHRONIZING) {
			Iterator it = masterMap.values().iterator();
			while (it.hasNext()) {
				Vector  rabv = (Vector) it.next();
				for (int vi = 0; vi < rabv.size(); ++vi) {
					Vector rab = (Vector) rabv.get(vi);
					if (((Boolean) rab.get(1)).booleanValue())
						continue;
					((SDMSResourceAllocation) rab.get(0)).cleanupStickyGroup(sysEnv);
				}
			}
		}

		if(!fitsSomewhere) {
			sme.setToError(sysEnv, "Job cannot run in any scope because of resource shortage");
			for (int j = 0; j < v.size(); ++j) {
				s = (SDMSScope) v.get(j);
				Long sId = s.getId(sysEnv);
				if (!s.getIsRegistered(sysEnv).booleanValue()) {
					continue;
				}
				if(!s.canExecute(sysEnv, sme)) {
					continue;
				}
				SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
				HashMap sfp = npsfp.getFp(sysEnv);
				verboseFits(sysEnv, sfp, smefp, sme, false, null);
			}
		}
	}

	public void reevaluateJSAssignment(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		SDMSScope s;
		Long smeId = sme.getId(sysEnv);
		boolean fitsSomewhere = false;
		HashMap smefp = (HashMap) getJobFootprint(sysEnv, sme).get(FP_SCOPE);

		doTrace(cEnv, ": Job " + sme.getId(sysEnv) + " is re-evaluated", SEVERITY_DEBUG);
		long actVersion = sme.getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) return;

		final Vector rqv = SDMSRunnableQueueTable.idx_smeId.getVectorForUpdate(sysEnv, smeId);
		final Vector sv = new Vector();
		for(int j = 0; j < rqv.size(); j++) {
			SDMSRunnableQueue rq = (SDMSRunnableQueue) rqv.get(j);
			try {
				s = SDMSScopeTable.getObject(sysEnv, rq.getScopeId(sysEnv));
				sv.add(s);
				doTrace(cEnv, ": added scope id " + s.getId(sysEnv), SEVERITY_DEBUG);
			} catch (NotFoundException nfe) {
				rq.delete(sysEnv);
			}
		}
		doTrace(cEnv, ": we've found " + sv.size() + " potential servers", SEVERITY_DEBUG);

		for(int j = 0; j < sv.size(); ++j) {
			s = (SDMSScope) sv.get(j);
			Long sId = s.getId(sysEnv);
			doTrace(cEnv, ": testing server " + sId, SEVERITY_DEBUG);

			SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
			HashMap sfp = npsfp.getFp(sysEnv);

			if(s.getIsRegistered(sysEnv).booleanValue() && fits(sysEnv, sfp, smefp, sme, true, s)) {
				doTrace(cEnv, ": seems to fit *****************", SEVERITY_DEBUG);
				fitsSomewhere = true;
			} else {
				doTrace(cEnv, ": doesn't seem to fit -+-+-+-+-+-+-+-+-", SEVERITY_DEBUG);
				doTrace(cEnv, ": deleting [" + s.getId(sysEnv) + ", " + smeId + "]", SEVERITY_DEBUG);
				try {
					SDMSRunnableQueue rq = SDMSRunnableQueueTable.idx_smeId_scopeId_getUnique(sysEnv, new SDMSKey(smeId, sId));
					rq.delete(sysEnv);
				} catch (NotFoundException nfe) {
				}

				Iterator i = smefp.keySet().iterator();
				while(i.hasNext()) {
					Long L = (Long) i.next();
					SDMSResource r = SDMSResourceTable.getObject(sysEnv, (Long) sfp.get(L));
					Long rId = r.getId(sysEnv);
					final Vector rav = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(smeId, r.getNrId(sysEnv)));
					for (int k = 0; k < rav.size(); ++k) {
						SDMSResourceAllocation ra = (SDMSResourceAllocation) rav.get(k);
						if (ra.getRId(sysEnv).equals(rId)) {
							ra.delete(sysEnv, true, false);
							break;
						}
					}
				}
			}
		}
		if(!fitsSomewhere) {
			sme.setToError(sysEnv, "Job cannot run in any scope because of resource shortage");
		}
	}

	private void requestFolderResourceSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, int type, HashMap masterMap, int oldState)
		throws SDMSException
	{
		SDMSnpJobFootprint jfp;
		Long smeId = sme.getId(sysEnv);

		try {
			jfp = SDMSnpJobFootprintTable.idx_smeId_getUnique(sysEnv, smeId);
			HashMap fpFolder = jfp.getFpFolder(sysEnv);
			requestLocalFolderResources(sysEnv, sme, smeId, fpFolder, type, masterMap, oldState);
		} catch(NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03501142150", "No footprint found for job $1", smeId));
		}
	}

	private void requestLocalResourceSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, int type, HashMap masterMap, int oldState)
		throws SDMSException
	{
		SDMSnpJobFootprint jfp;
		Long smeId = sme.getId(sysEnv);

		try {
			jfp = SDMSnpJobFootprintTable.idx_smeId_getUniqueForUpdate(sysEnv, smeId);
			HashMap fpLocal = jfp.getFpLocal(sysEnv);
			requestLocalFolderResources(sysEnv, sme, smeId, fpLocal, type, masterMap, oldState);
		} catch(NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03501142151", "No footprint found for job $1", smeId));
		}
	}

	private void requestLocalFolderResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Long smeId, HashMap fp, int type, HashMap masterMap, int oldState)
		throws SDMSException
	{
		Iterator i = fp.values().iterator();
		SDMSResourceRequirement rr;
		SDMSResource r;
		SDMSNamedResource nr;
		Long nrId;

		while (i.hasNext()) {
			Vector v = (Vector) i.next();
			rr = (SDMSResourceRequirement) v.get(0);
			r  = (SDMSResource) v.get(1);
			nrId = rr.getNrId(sysEnv);

			nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
			if(nr.getUsage(sysEnv).intValue() != type) continue;
			if (rr.getIsSticky(sysEnv).booleanValue() && oldState != SDMSSubmittedEntity.SUBMITTED) continue;
			createRequest(sysEnv, smeId, rr, r, nrId, type, masterMap);
		}
	}

	private void requestResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se,
					long actVersion, HashMap sfp, int type, HashMap smefp, HashMap masterMap, int oldState)
		throws SDMSException
	{
		SDMSProxy proxy;
		SDMSNamedResource nr;
		SDMSResource r;
		SDMSResourceRequirement rr;
		Long nrId, smeId;
		Iterator i = null;

		i = smefp.values().iterator();
		smeId = sme.getId(sysEnv);

		while(i.hasNext()) {
			proxy = (SDMSProxy) i.next();
			if(!(proxy instanceof SDMSResourceRequirement)) continue;
			rr = (SDMSResourceRequirement) proxy;
			if (rr.getIsSticky(sysEnv).booleanValue()) {
				if (oldState != SDMSSubmittedEntity.SUBMITTED) continue;
			}
			nrId = rr.getNrId(sysEnv);
			nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
			if(nr.getUsage(sysEnv).intValue() != type) continue;

			r = SDMSResourceTable.getObject(sysEnv, (Long) sfp.get(nrId));
			createRequest(sysEnv, smeId, rr, r, nrId, type, masterMap);
		}
	}

	private boolean isVisible(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Long folderId)
	throws SDMSException
	{
		Long seVersion = sme.getSeVersion(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), seVersion);
		Long parentFolderId = se.getFolderId(sysEnv);
		SDMSFolder f;
		while (parentFolderId != null) {
			if (parentFolderId.equals(folderId)) return true;
			f = SDMSFolderTable.getObject(sysEnv, parentFolderId, seVersion);
			parentFolderId = f.getParentId(sysEnv);
		}
		return false;
	}

	private void createRequest(SystemEnvironment sysEnv, Long smeId, SDMSResourceRequirement rr, SDMSResource r, Long nrId, int type, HashMap masterMap)
		throws SDMSException
	{
		Integer lock;
		Long rsmpId;
		Long rId;
		float factor = 1;
		Long stickyParentId = null;

		if(rr == null) return;
		rId = r.getId(sysEnv);

		try {
			SDMSResourceAllocation ra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUniqueForUpdate(
				sysEnv, new SDMSKey(smeId, rId, rr.getStickyName(sysEnv)));
			ra.setRefcount(sysEnv, new Integer(ra.getRefcount(sysEnv).intValue() + 1));
			return;
		} catch (NotFoundException nfe) {
		}
		if(SDMSResourceAllocationTable.idx_smeId_rId_stickyName.containsKey(sysEnv, new SDMSKey(smeId, nrId, rr.getStickyName(sysEnv)))) return;

		if(type == SDMSNamedResource.SYNCHRONIZING) {
			lock = rr.getLockmode(sysEnv);
			if(lock == null) lock = new Integer(Lockmode.N);
			rsmpId = rr.getRsmpId(sysEnv);
			Long stickyParentSeId = rr.getStickyParent(sysEnv);
			if (rr.getIsSticky(sysEnv).booleanValue()) {
				SDMSSubmittedEntity psme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
				stickyParentId = psme.getMasterId(sysEnv);
				if (stickyParentSeId != null) {

					Long pId = psme.getParentId(sysEnv);
					while (pId != null && !stickyParentSeId.equals(psme.getSeId(sysEnv))) {
						psme = SDMSSubmittedEntityTable.getObject(sysEnv, pId);
						pId = psme.getParentId(sysEnv);
					}

					if (pId != null || stickyParentSeId.equals(psme.getSeId(sysEnv))) stickyParentId = psme.getId(sysEnv);
				} else {

					Long sId = r.getScopeId(sysEnv);
					if (!SDMSScopeTable.table.exists(sysEnv, sId)) {

						if (SDMSFolderTable.table.exists(sysEnv, sId)) {
							Long stickyParentCandidate = smeId;
							SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
							Long parentId = sme.getParentId(sysEnv);
							while (parentId != null) {

								sme = SDMSSubmittedEntityTable.getObject(sysEnv, parentId);

								if (isVisible(sysEnv, sme, sId))
									stickyParentCandidate = parentId;
								parentId = sme.getParentId(sysEnv);
							}
							stickyParentId = stickyParentCandidate;
						} else {

							stickyParentId = sId;
						}

					}
				}
			}
		} else {
			lock = new Integer(Lockmode.N);
			rsmpId = null;
		}

		Integer reqAmount = new Integer((int) Math.ceil(rr.getAmount(sysEnv).intValue() * factor));

		if (rr.getIsSticky(sysEnv).booleanValue()) {
			String stickyName = rr.getStickyName(sysEnv);
			Long nStickyParentId = new Long(- stickyParentId.longValue());
			SDMSKey masterKey = new SDMSKey(nStickyParentId, stickyName, nrId);
			Vector ravok = (Vector) masterMap.get(masterKey);
			if (ravok == null) {
				Vector rav = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(nStickyParentId, nrId));
				ravok = new Vector();
				for (int i = 0; i < rav.size(); ++i) {
					SDMSResourceAllocation ra = (SDMSResourceAllocation) rav.get(i);
					String raName = ra.getStickyName(sysEnv);
					if ((raName == null && stickyName == null) || (raName != null && raName.equals(stickyName))) {
						Vector entry = new Vector();
						entry.add(ra);
						entry.add(Boolean.FALSE);
						ravok.add(entry);
					}
				}
				masterMap.put(masterKey, ravok);
			}
			if (ravok.size() != 0) {
				boolean raOK = false;
				for (int i = 0; i < ravok.size(); ++i) {
					Vector entry = (Vector) ravok.get(i);
					SDMSResourceAllocation ra = (SDMSResourceAllocation) entry.get(0);
					if (ra.getRId(sysEnv).equals(rId)) {
						raOK = true;
						entry.set(1, Boolean.TRUE);
						int raReqAmount = ra.getOrigAmount(sysEnv).intValue();
						int raLockMode = ra.getLockmode(sysEnv).intValue();

						if (raReqAmount < reqAmount.intValue()) {
							if (ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.MASTER_RESERVATION) {
								SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
								SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv,
												sme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
								throw new CommonErrorException(new SDMSMessage(sysEnv, "03405261410",
									"Invalid amount escalation for already reserved sticky resource $1, job definition $2",
										rId, se.pathString(sysEnv)));
							}
							ra.setOrigAmount(sysEnv, reqAmount);
							ra.setAmount(sysEnv, reqAmount);
						}

						raLockMode &= lock.intValue();
						if (raLockMode != ra.getLockmode(sysEnv).intValue()) {
							if (ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.MASTER_RESERVATION) {
								SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
								SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv,
												sme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
								throw new CommonErrorException(new SDMSMessage(sysEnv, "03405261415",
									"Invalid lock escalation for already reserved sticky resource $1, job definition $2",
										rId, se.pathString(sysEnv)));
							}
							ra.setLockmode(sysEnv, new Integer(raLockMode));
						}

						int refCount = ra.getRefcount(sysEnv).intValue();
						ra.setRefcount(sysEnv, new Integer(refCount + 1));
						break;
					}
				}
				if (!raOK) {
					throw new SDMSEscape();
				}
			} else {
				try {
					SDMSResourceAllocationTable.table.create(sysEnv,
							rId, nStickyParentId, nrId,
							reqAmount,
							reqAmount,
							rr.getKeepMode(sysEnv),
							rr.getIsSticky(sysEnv),
							rr.getStickyName(sysEnv),
							stickyParentId,
							new Integer(SDMSResourceAllocation.MASTER_REQUEST),
							null,
							lock,
							ONE);
				} catch (DuplicateKeyException dke) {
				}
			}
		}

		SDMSResourceAllocationTable.table.create(sysEnv,
							rId, smeId, nrId,
							reqAmount,
							reqAmount,
							rr.getKeepMode(sysEnv),
							rr.getIsSticky(sysEnv),
							rr.getStickyName(sysEnv),
							stickyParentId,
							new Integer(SDMSResourceAllocation.REQUEST),
							rsmpId,
							lock,
							ONE);
	}

	private Vector findRelevantJobserver (SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Vector sv = new Vector();
		Vector rqv = SDMSRunnableQueueTable.idx_smeId.getVectorForUpdate(sysEnv, sme.getId(sysEnv));
		for(int j = 0; j < rqv.size(); j++) {
			SDMSRunnableQueue rq = (SDMSRunnableQueue) rqv.get(j);
			SDMSScope s = SDMSScopeTable.getObject(sysEnv, rq.getScopeId(sysEnv));
			sv.add(s);
		}
		return sv;
	}

	private void  syncScheduleSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Locklist resourceChain)
		throws SDMSException
	{
		Vector rqv;
		SDMSScope s;
		Long sId;
		Long smeId = sme.getId(sysEnv);
		boolean resourcesReserved = false;
		Vector smefpv = getJobFootprint(sysEnv, sme);
		HashMap smefp = (HashMap) smefpv.get(FP_SCOPE);

		Vector jsv = new Vector();
		Vector v;

		long actVersion = sme.getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);

		v = findRelevantJobserver(sysEnv, sme);

		final HashMap fpFolder = (HashMap) smefpv.get(FP_FOLDER);
		final HashMap fpLocal = (HashMap) smefpv.get(FP_LOCAL);
		resourcesReserved = reserveFp(sysEnv, sme, se, resourceChain, fpFolder, SDMSNamedResource.SYNCHRONIZING) &&
					reserveFp(sysEnv, sme, se, resourceChain, fpLocal, SDMSNamedResource.SYNCHRONIZING);
		if(!resourcesReserved) {
			checkTimeout(sysEnv, sme, se, actVersion);
			return;
		}

		resourcesReserved = false;

		for(int j = 0; j < v.size(); ++j) {
			s = (SDMSScope) v.get(j);
			sId = s.getId(sysEnv);

			SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, sId);
			HashMap sfp = npsfp.getFp(sysEnv);

			if(!checkStaticResources(sysEnv, sfp, smefp)) continue;

			Iterator rrvi = smefp.values().iterator();

			if(reserveSyncResources(sysEnv, sme, se, actVersion, sfp, resourceChain, rrvi)) {
				resourcesReserved = true;
				SDMSRunnableQueue rq = SDMSRunnableQueueTable.idx_smeId_scopeId_getUniqueForUpdate(sysEnv, new SDMSKey(smeId, sId));
				rq.setState(sysEnv, new Integer(SDMSSubmittedEntity.RESOURCE_WAIT));
			}
		}

		if(resourcesReserved) {
			Vector rv = SDMSResourceAllocationTable.idx_smeId.getVectorForUpdate(sysEnv, smeId);
			for(int i = 0; i < rv.size(); i++) {
				SDMSResourceAllocation ra = (SDMSResourceAllocation) rv.get(i);
				if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.REQUEST) {
					SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, ra.getNrId(sysEnv));
					if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.SYNCHRONIZING)
						continue;
					ra.delete(sysEnv, true, true);
				}
			}
			rqv = SDMSRunnableQueueTable.idx_smeId.getVector(sysEnv, smeId);
			for(int i = 0; i < rqv.size(); i++) {
				SDMSRunnableQueue rq = (SDMSRunnableQueue) rqv.get(i);
				if(rq.getState(sysEnv).intValue() != SDMSSubmittedEntity.RESOURCE_WAIT) {
					rq.delete(sysEnv);
				}
			}
			sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.RESOURCE_WAIT));
		} else {
			checkTimeout(sysEnv, sme, se, actVersion);
		}
	}

	private boolean checkKeptResources(SystemEnvironment sysEnv, Long smeId, Vector jsv, Vector sv)
		throws SDMSException
	{
		SDMSScope s;

		Vector rav = SDMSResourceAllocationTable.idx_smeId.getVectorForUpdate(sysEnv, smeId);
		boolean jsv_used = false;
		for(int j = 0; j < rav.size(); j++) {
			SDMSResourceAllocation ra = (SDMSResourceAllocation) rav.get(j);
			int raAllocationType = ra.getAllocationType(sysEnv).intValue();
			if(raAllocationType == SDMSResourceAllocation.ALLOCATION) {
				if(!jsv_used) {
					for(int i = 0; i < sv.size(); i++) {
						s = (SDMSScope) sv.get(i);
						SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
						HashMap sfp = npsfp.getFp(sysEnv);
						if(sfp.containsValue(ra.getRId(sysEnv))) {
							jsv.addElement(s);
							jsv_used = true;
						}
					}
				} else {
					Iterator jsi = jsv.iterator();
					while(jsi.hasNext()) {
						s = (SDMSScope) jsi.next();
						SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
						HashMap sfp = npsfp.getFp(sysEnv);
						if(!sfp.containsValue(ra.getRId(sysEnv))) {
							jsi.remove();
						}
					}
				}
			}
		}
		return jsv_used;
	}

	private boolean checkStickyResources(SystemEnvironment sysEnv, Long smeId, HashMap smefp, Vector jsv, Vector v)
		throws SDMSException
	{
		Vector rav = SDMSResourceAllocationTable.idx_smeId.getVector(sysEnv, smeId);
		SDMSResourceAllocation ra;

		Iterator ravi = rav.iterator();
		while (ravi.hasNext()) {
			ra = (SDMSResourceAllocation) ravi.next();
			if (!ra.getIsSticky(sysEnv).booleanValue()) {
				ravi.remove();
				continue;
			}
			if (!smefp.containsKey(ra.getNrId(sysEnv))) {
				ravi.remove();
				continue;
			}
		}
		if (rav.size() == 0) return false;

		for (int i = 0; i < v.size(); ++i) {
			SDMSScope s = (SDMSScope) v.get(i);
			SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
			HashMap sfp = npsfp.getFp(sysEnv);
			ravi = rav.iterator();
			while (ravi.hasNext()) {
				ra = (SDMSResourceAllocation) ravi.next();
				if(sfp.containsValue(ra.getRId(sysEnv))) {
					jsv.addElement(s);
				}
			}
		}

		return true;
	}

	private void checkTimeout(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se, long actVersion)
		throws SDMSException
	{
		Integer toBase = se.getTimeoutBase(sysEnv);

		if(toBase != null) {
			long toTime = se.getTimeoutAmount(sysEnv).longValue();
			switch(toBase.intValue()) {
				case SDMSInterval.MINUTE:
					toTime *= SDMSInterval.MINUTE_DUR;
					break;
				case SDMSInterval.HOUR:
					toTime *= SDMSInterval.HOUR_DUR;
					break;
				case SDMSInterval.DAY:
					toTime *= SDMSInterval.DAY_DUR;
					break;
				case SDMSInterval.WEEK:
					toTime *= SDMSInterval.WEEK_DUR;
					break;
				case SDMSInterval.MONTH:
					toTime *= SDMSInterval.MONTH_DUR;
					break;
				case SDMSInterval.YEAR:
					toTime *= SDMSInterval.YEAR_DUR;
					break;
			}
			java.util.Date ldts = new java.util.Date();
			long ts = ldts.getTime();
			long nextTimeout = sme.getSyncTs(sysEnv).longValue() + toTime;
			if(nextTimeout < ts) {
				doTrace(cEnv, ": Job " + sme.getId(sysEnv) + " run into timeout", SEVERITY_MESSAGE);
				Long esdId, espId;
				esdId = se.getTimeoutStateId(sysEnv);
				espId = se.getEspId(sysEnv);
				SDMSExitState es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, esdId), actVersion);

				sme.changeState(sysEnv, esdId, es, sme.getExitCode(sysEnv), "Timeout", null );
			} else {
				if(nextTimeout < timeoutWakeup) timeoutWakeup = nextTimeout;
			}
		}
	}

	public void resourceSchedule(SystemEnvironment sysEnv, Locklist resourceChain)
		throws SDMSException
	{
		SDMSSubmittedEntity sme;
		Vector sv;

		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		sv = SDMSSubmittedEntityTable.idx_state.getVectorForUpdate(sysEnv, new Integer(SDMSSubmittedEntity.RESOURCE_WAIT), null, Integer.MAX_VALUE);
		doTrace(cEnv, "Number of Jobs in RESOURCE_WAIT : " + sv.size(), SEVERITY_DEBUG);

		pc.setNow();
		Collections.sort(sv, pc);

		for(int i = 0; i < sv.size(); ++i) {
			sme = (SDMSSubmittedEntity) sv.get(i);
			if(sme.getIsSuspended(sysEnv).intValue() != SDMSSubmittedEntity.NOSUSPEND || sme.getParentSuspended(sysEnv).intValue() > 0)
				continue;
			resourceScheduleSme(sysEnv, sme, resourceChain);
		}
	}

	private void resourceScheduleSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Locklist resourceChain)
		throws SDMSException
	{
		SDMSRunnableQueue rq;
		boolean resourcesReserved = false;
		Long smeId = sme.getId(sysEnv);
		long actVersion = sme.getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);

		final Vector fpv = getJobFootprint(sysEnv, sme);
		final HashMap fp = (HashMap) fpv.get(FP_SCOPE);
		final HashMap fpFolder = (HashMap) fpv.get(FP_FOLDER);
		final HashMap fpLocal = (HashMap) fpv.get(FP_LOCAL);
		resourcesReserved = reserveFp(sysEnv, sme, se, resourceChain, fpLocal, SDMSNamedResource.SYSTEM);
		if(resourcesReserved)
			resourcesReserved = reserveFp(sysEnv, sme, se, resourceChain, fpFolder, SDMSNamedResource.SYSTEM);
		if(!resourcesReserved) {
			checkTimeout(sysEnv, sme, se, actVersion);
			return;
		}
		resourcesReserved = false;

		Vector v = SDMSRunnableQueueTable.idx_smeId.getVectorForUpdate(sysEnv, smeId);
		if(v.size() == 0) {
			doTrace(cEnv, ": Job " +  sme.getId(sysEnv) + " cannot run in any scope", SEVERITY_WARNING);
			return;
		}

		SDMSScope s = null;
		for(int j = 0; j < v.size(); ++j) {
			rq = (SDMSRunnableQueue) v.get(j);
			s = SDMSScopeTable.getObject(sysEnv, rq.getScopeId(sysEnv));

			Iterator it = fp.values().iterator();
			HashMap sfp = (SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv))).getFp(sysEnv);
			if(reserveSysResources(sysEnv, sme, sfp, resourceChain, it)) {
				resourcesReserved = true;
				rq.setState(sysEnv, new Integer(SDMSSubmittedEntity.RUNNABLE));
				allocateAndReleaseResources(sysEnv, sme, s);
				break;
			}
		}
		if(resourcesReserved) {
			doTrace(cEnv, ": Job " + smeId + " added to Runnable Queue " + s.getId(sysEnv), SEVERITY_DEBUG);
			Vector rv = SDMSResourceAllocationTable.idx_smeId.getVector(sysEnv, smeId);
			for(int i = 0; i < rv.size(); i++) {
				SDMSResourceAllocation ra = (SDMSResourceAllocation) rv.get(i);
				if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.REQUEST) {
					SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, ra.getNrId(sysEnv));
					if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.SYSTEM)
						continue;
					ra.delete(sysEnv, true, true);
				}
			}
			Vector rqv = SDMSRunnableQueueTable.idx_smeId.getVector(sysEnv, smeId);
			for(int i = 0; i < rqv.size(); i++) {
				rq = (SDMSRunnableQueue) rqv.get(i);
				if(rq.getState(sysEnv).intValue() != SDMSSubmittedEntity.RUNNABLE) {
					rq.delete(sysEnv);
				}
			}
			sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.RUNNABLE));
			sysEnv.notifier.addJobServerToNotify(s.getId(sysEnv));
		} else {
			checkTimeout(sysEnv, sme, se, actVersion);
		}
	}

	public static boolean fits(SystemEnvironment sysEnv, HashMap scopeFp, HashMap smeFp, SDMSSubmittedEntity sme, boolean checkCondition, SDMSScope evalScope)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1 && sysEnv.tx.mode == SDMSTransaction.READWRITE)
			LockingSystem.lock(sysEnv, sysEnv.sched, ObjectLock.EXCLUSIVE);

		Iterator i = smeFp.keySet().iterator();
		while(i.hasNext()) {
			Long L = (Long) i.next();
			if(!scopeFp.containsKey(L)) {
				return false;
			}
			SDMSResource r = SDMSResourceTable.getObject(sysEnv, (Long) scopeFp.get(L));
			Integer sAmount = r.getRequestableAmount(sysEnv);
			SDMSResourceRequirement rr;
			SDMSEnvironment e;
			SDMSProxy p = (SDMSProxy) smeFp.get(L);
			if (p instanceof SDMSResourceRequirement) {
				rr = (SDMSResourceRequirement) p;
				e = null;
			} else {
				e = (SDMSEnvironment) p;
				rr = null;
			}
			if(checkCondition) {
				String condition = (rr == null ? e.getCondition(sysEnv) : rr.getCondition(sysEnv));
				if (condition != null) {
					final BoolExpr be = new BoolExpr(condition);
					try {
						if (! be.checkCondition(sysEnv, r, sme, null, null, evalScope)) {
							return false;
						}
					} catch (CommonErrorException cee) {
						if (sysEnv.tx.mode == SDMSTransaction.READWRITE) {
							SDMSNamedResource nr;
							if (rr != null) {
								nr = SDMSNamedResourceTable.getObject(sysEnv, rr.getNrId(sysEnv));
							} else {
								nr = SDMSNamedResourceTable.getObject(sysEnv, e.getNrId(sysEnv));
							}
							String msg = cee.toString() + " evaluating the condition for resource " + nr.pathString(sysEnv);
						}
						return false;
					}
				}
			}

			Integer jAmount;
			if(rr == null)	jAmount = new Integer(0);
			else		jAmount = rr.getAmount(sysEnv);
			if(sAmount == null)
				continue;
			if(jAmount.compareTo(sAmount) > 0) {
				return false;
			}
		}
		return true;
	}

	private static boolean verboseFits(SystemEnvironment sysEnv, HashMap scopeFp, HashMap smeFp, SDMSSubmittedEntity sme, boolean checkCondition, SDMSScope evalScope)
		throws SDMSException
	{
		Iterator i = smeFp.keySet().iterator();
		while(i.hasNext()) {
			Long L = (Long) i.next();
			if(!scopeFp.containsKey(L))
				return false;
			SDMSResource r = SDMSResourceTable.getObject(sysEnv, (Long) scopeFp.get(L));
			Integer sAmount = r.getRequestableAmount(sysEnv);
			SDMSResourceRequirement rr;
			SDMSEnvironment e;
			SDMSProxy p = (SDMSProxy) smeFp.get(L);
			if (p instanceof SDMSResourceRequirement) {
				rr = (SDMSResourceRequirement) p;
				e = null;
			} else {
				e = (SDMSEnvironment) p;
				rr = null;
			}
			if(checkCondition) {
				String condition = (rr == null ? e.getCondition(sysEnv) : rr.getCondition(sysEnv));
				if (condition != null) {
					final BoolExpr be = new BoolExpr(condition);
					try {
						if (! be.checkCondition(sysEnv, r, sme, null, null, evalScope)) return false;
					} catch (CommonErrorException cee) {
						SDMSNamedResource nr;
						if (rr != null) {
							nr = SDMSNamedResourceTable.getObject(sysEnv, rr.getNrId(sysEnv));
						} else {
							nr = SDMSNamedResourceTable.getObject(sysEnv, e.getNrId(sysEnv));
						}
						String msg = cee.toString() + " evaluating the condition for resource " + nr.pathString(sysEnv);
						return false;
					}
				}
			}

			Integer jAmount;
			if(rr == null)	jAmount = new Integer(0);
			else		jAmount = rr.getAmount(sysEnv);
			if(sAmount == null)
				continue;
			if(jAmount.compareTo(sAmount) > 0)
				return false;
		}
		return true;
	}

	private boolean checkStaticResources(SystemEnvironment sysEnv, HashMap scopeFp, HashMap smeFp)
		throws SDMSException
	{
		Iterator i = smeFp.keySet().iterator();
		while(i.hasNext()) {
			Long L = (Long) i.next();
			if(!scopeFp.containsKey(L))
				return false;
			SDMSResource r = SDMSResourceTable.getObjectForUpdate(sysEnv, (Long) scopeFp.get(L));
			SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv));
			if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.STATIC) continue;
			if(!r.getIsOnline(sysEnv).booleanValue()) return false;
		}
		return true;
	}

	private boolean reserveSyncResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se, long actVersion, HashMap sfp, Locklist resourceChain, Iterator i)
		throws SDMSException
	{
		SDMSResourceRequirement rr;
		SDMSProxy proxy;
		SDMSResource r;

		Long smeId = sme.getId(sysEnv);
		Long nrId;
		Long rId;
		Long stickyParent;
		Long nStickyParent;
		String rrStickyName;
		SDMSResourceAllocation ra = null;
		SDMSResourceAllocation mra = null;
		boolean isSticky;
		boolean allocSucceeded = true;
		int waitAmount;
		Lockmode waitLock;
		Reservator rsrv = null;

		sysEnv.tx.beginSubTransaction(sysEnv);
		try {
			Vector srv = new Vector();
			while(i.hasNext()) {
				proxy = (SDMSProxy) i.next();
				if (!(proxy instanceof SDMSResourceRequirement)) continue;
				rr = (SDMSResourceRequirement) proxy;
				nrId = rr.getNrId(sysEnv);
				SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
				if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.SYNCHRONIZING) continue;

				r = SDMSResourceTable.getObject(sysEnv, (Long) sfp.get(nrId));
				rId = r.getId(sysEnv);

				try {
					ra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv, new SDMSKey(smeId, rId, rr.getStickyName(sysEnv)));
				} catch (NotFoundException nfe) {
					if(SDMSResourceAllocationTable.idx_smeId_rId_stickyName.containsKey(sysEnv, new SDMSKey(smeId, nrId, rr.getStickyName(sysEnv))))
						continue;
					doTrace(cEnv, ": Job " +  smeId + " needs a resource " + nrId + "/" + rId +
								" which is neither requested/reserved/allocated nor ignored", SEVERITY_ERROR);
					continue;
				}

				int allocType = ra.getAllocationType(sysEnv).intValue();
				if(allocType == SDMSResourceAllocation.IGNORE) continue;
				if(allocType == SDMSResourceAllocation.RESERVATION) continue;
				if(allocType == SDMSResourceAllocation.ALLOCATION) continue;
				if(SDMSResourceAllocationTable.idx_smeId_rId_stickyName.containsKey(sysEnv, new SDMSKey(smeId, nrId, rr.getStickyName(sysEnv))))
					continue;

				if(resourceChain != null) {
					rsrv = resourceChain.get(rId);
				}
				if(rsrv == null) rsrv = new Reservator(rId, smeId);
				else		 rsrv = new Reservator(rId, smeId, rsrv.amount, rsrv.lock.getLockmode());

				isSticky = rr.getIsSticky(sysEnv).booleanValue();
				if(isSticky) {
					stickyParent = ra.getStickyParent(sysEnv);
					nStickyParent = new Long(- stickyParent.longValue());
					rrStickyName = rr.getStickyName(sysEnv);

					try {
						mra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv,
												new SDMSKey(nStickyParent, rId, rrStickyName));
					} catch (NotFoundException nfe) {
						mra = createUpgradeMasterRequest(sysEnv, sme, rr, r, actVersion);
					}

					MasterReservationInfo mri = checkMasterReservation(sysEnv, sme, rr, stickyParent, r, rsrv);
					if(mri.mustAllocate && (resourceChain != null)) {
						resourceChain.set(new Reservator(rId, nStickyParent, mri.amount, mri.lockmode));
						srv.add(mra);
					}
					if(!mri.canAllocate) {
						if(resourceChain != null) {
							allocSucceeded = false;
							continue;
						}
						throw new SDMSEscape();
					}
					if(mri.mustAllocate) {
						mra.setAllocationType(sysEnv, new Integer(SDMSResourceAllocation.MASTER_RESERVATION));
					}
				}

				waitAmount = rsrv.amount;
				waitLock = rsrv.lock;
				int reason = r.checkAllocate(sysEnv, rr, sme, ra, waitAmount, waitLock);
				if(resourceChain != null)
					resourceChain.set(new Reservator(rId, smeId, rr.getAmount(sysEnv).intValue(), rr.getLockmode(sysEnv).intValue()));
				if(reason != SDMSResource.REASON_AVAILABLE) {
					if(resourceChain == null) throw new SDMSEscape();
					if((reason & (SDMSResource.REASON_STATE|SDMSResource.REASON_EXPIRE|SDMSResource.REASON_OFFLINE)) != 0) {
						resourceChain.removeSme(smeId);
						throw new SDMSEscape();
					}
					allocSucceeded = false;
					continue;
				}

				ra.setAllocationType(sysEnv, new Integer(SDMSResourceAllocation.RESERVATION));

				if(isSticky) {
					int mAmount = mra.getAmount(sysEnv).intValue();
					int raAmount = ra.getAmount(sysEnv).intValue();

					mra.setAmount(sysEnv, new Integer(mAmount - raAmount));
				}
			}
			if(!allocSucceeded) throw new SDMSEscape();
			if(resourceChain != null) {
				resourceChain.removeSme(smeId);
				for(int j = 0; j < srv.size(); j++) {
					mra = (SDMSResourceAllocation) srv.get(j);
					resourceChain.remove(mra.getRId(sysEnv), mra.getSmeId(sysEnv));
				}
			}
		} catch(SDMSEscape e) {
			sysEnv.tx.rollbackSubTransaction(sysEnv);
			return false;
		}
		sysEnv.tx.commitSubTransaction(sysEnv);

		return true;
	}

	private boolean reserveFp(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se, Locklist resourceChain, HashMap lf_fp, int type)
		throws SDMSException
	{
		HashMap fp = new HashMap();
		Vector rrv = new Vector();

		Iterator i = lf_fp.values().iterator();
		while(i.hasNext()) {
			Vector v = (Vector) i.next();
			SDMSResource r = (SDMSResource) v.get(1);
			fp.put(r.getNrId(sysEnv), r.getId(sysEnv));
			rrv.add(v.get(0));
		}

		if(rrv.size() == 0) return true;

		if(type == SDMSNamedResource.SYSTEM)
			return reserveSysResources(sysEnv, sme, fp, resourceChain, rrv.iterator());

		return reserveSyncResources(sysEnv, sme, se, sme.getSeVersion(sysEnv).longValue(), fp, resourceChain, rrv.iterator());
	}

	private SDMSResourceAllocation createUpgradeMasterRequest(SystemEnvironment sysEnv, SDMSSubmittedEntity sme,
			SDMSResourceRequirement rr, SDMSResource r, long actVersion)
		throws SDMSException
	{
		SDMSSubmittedEntity tsme;
		SDMSResourceRequirement trr;

		final Long nrId = rr.getNrId(sysEnv);
		final Long rId = r.getId(sysEnv);
		Long seId = null;
		SDMSResourceAllocation ra;
		float factor = 1;

		Vector v;
		int lockmode = Lockmode.N;
		int amount = 0;
		int refcount = 0;

		v = SDMSSubmittedEntityTable.idx_masterId.getVectorForUpdate(sysEnv, sme.getMasterId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			tsme = (SDMSSubmittedEntity) v.get(i);
			int state = tsme.getState(sysEnv).intValue();
			if(state != SDMSSubmittedEntity.SUBMITTED	 &&
			   state != SDMSSubmittedEntity.DEPENDENCY_WAIT     &&
			   state != SDMSSubmittedEntity.SYNCHRONIZE_WAIT) continue;

			if(tsme.getJobIsFinal(sysEnv).booleanValue())	continue;

			seId = tsme.getSeId(sysEnv);
			try {
				trr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(seId, nrId), actVersion);
			} catch (NotFoundException nfe) {
				continue;
			}
			if(! trr.getIsSticky(sysEnv).booleanValue())
				continue;

			try {
				ra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv, new SDMSKey(tsme.getId(sysEnv), rId, null));
				if(ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.IGNORE) continue;
			} catch (NotFoundException nfe) {  }

			refcount++;

			lockmode &= trr.getLockmode(sysEnv).intValue();

			int tmp = trr.getAmount(sysEnv).intValue();
			if(tmp > amount) amount = tmp;
		}

		ra = SDMSResourceAllocationTable.table.create(sysEnv,
						rId, new Long(- sme.getMasterId(sysEnv)), nrId,
						new Integer(amount),
						new Integer(amount),
						rr.getKeepMode(sysEnv),
						Boolean.TRUE,
						null,
						sme.getMasterId(sysEnv),
						new Integer(SDMSResourceAllocation.MASTER_REQUEST),
						null,
						new Integer(lockmode),
						new Integer(refcount));

		return ra;
	}

	public MasterReservationInfo checkMasterReservation(SystemEnvironment sysEnv, SDMSSubmittedEntity sme,
			SDMSResourceRequirement rr, Long stickyParent, SDMSResource r)
			throws SDMSException
	{
		return checkMasterReservation(sysEnv, sme, rr, stickyParent, r, new Reservator(r.getId(sysEnv), sme.getId(sysEnv)));
	}

	public MasterReservationInfo checkMasterReservation(SystemEnvironment sysEnv, SDMSSubmittedEntity sme,
			SDMSResourceRequirement rr, Long stickyParent, SDMSResource r, Reservator rsrv)
			throws SDMSException
	{
		if (sysEnv.maxWriter > 1 && sysEnv.tx.mode == SDMSTransaction.READWRITE)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		SDMSSubmittedEntity tsme;
		SDMSResourceRequirement trr;

		final Long nrId = rr.getNrId(sysEnv);
		final Long rId = r.getId(sysEnv);
		final MasterReservationInfo mri = new MasterReservationInfo();
		SDMSResourceAllocation ra = null;
		float factor = 1;
		String rrStickyName = rr.getStickyName(sysEnv);
		Long nStickyParent = new Long(- stickyParent.longValue());

		mri.stickyName = rrStickyName;
		mri.stickyParent = stickyParent;

		Vector v;
		int lockmode = Lockmode.N;
		int amount = 0;

		try {
			ra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv, new SDMSKey(nStickyParent, rId, rrStickyName));
			mri.amount = ra.getAmount(sysEnv).intValue();
			mri.lockmode = ra.getLockmode(sysEnv).intValue();
		} catch (NotFoundException nfe) {
			return mri;
		}

		if (ra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.MASTER_RESERVATION) {
			mri.mustAllocate = false;
			mri.canAllocate = true;
			return mri;
		}

		int cAmount = (int) Math.ceil(mri.amount * factor);
		if(!r.checkAmount(sysEnv, cAmount, mri.amount, rsrv.amount)) {
			mri.mustAllocate = false;
			mri.canAllocate = false;
			return mri;
		}

		if(!r.syncCheckLockmode(sysEnv, mri.lockmode, rsrv.lock)) {
			mri.mustAllocate = false;
			mri.canAllocate = false;
			return mri;
		}

		mri.mustAllocate = true;
		mri.canAllocate = true;
		mri.amount = cAmount;
		mri.lockmode = lockmode;

		return mri;
	}

	private boolean reserveSysResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, HashMap sfp, Locklist resourceChain, Iterator it)
		throws SDMSException
	{
		SDMSResourceRequirement rr;
		SDMSProxy proxy;
		Long smeId = sme.getId(sysEnv);
		Long rId;
		Long nrId;
		Reservator rsrv = null;
		boolean allocSucceeded = true;

		sysEnv.tx.beginSubTransaction(sysEnv);
		try {
			while(it.hasNext()) {
				proxy = (SDMSProxy) it.next();
				if (!(proxy instanceof SDMSResourceRequirement)) continue;
				rr = (SDMSResourceRequirement) proxy;
				nrId = rr.getNrId(sysEnv);
				SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
				if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.SYSTEM) continue;

				SDMSResource r = SDMSResourceTable.getObjectForUpdate(sysEnv, (Long) sfp.get(nrId));
				rId =  r.getId(sysEnv);

				SDMSResourceAllocation ra = null;
				try {
					ra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUniqueForUpdate(sysEnv, new SDMSKey(smeId, rId, null));
				} catch (NotFoundException nfe) {
					if(SDMSResourceAllocationTable.idx_smeId_rId_stickyName.containsKey(sysEnv, new SDMSKey(smeId, nrId, null))) continue;
					doTrace(cEnv, ": Job " +  smeId + " needs a resource " + nrId + "/" + rId +
								" which is neither requested/reserved/allocated nor ignored", SEVERITY_ERROR);
					continue;
				}

				int allocType = ra.getAllocationType(sysEnv).intValue();
				if(allocType == SDMSResourceAllocation.IGNORE) continue;
				if(allocType == SDMSResourceAllocation.RESERVATION) continue;
				if(allocType == SDMSResourceAllocation.ALLOCATION) continue;

				if(resourceChain != null) {
					rsrv = resourceChain.get(rId);
				}
				if(rsrv == null) rsrv = new Reservator(rId, smeId);
				int waitAmount = rsrv.amount;
				Lockmode waitLock = rsrv.lock;
				int reason = r.checkAllocate(sysEnv, rr, sme, ra, waitAmount, waitLock);
				if(reason != SDMSResource.REASON_AVAILABLE) {
					if(resourceChain != null && reason != SDMSResource.REASON_OFFLINE) {
						resourceChain.set(new Reservator(rId, smeId, rr.getAmount(sysEnv).intValue(), Lockmode.N));
						allocSucceeded = false;
						continue;
					}
					throw new SDMSEscape();
				}

				ra.setAllocationType(sysEnv, new Integer(SDMSResourceAllocation.RESERVATION));
			}
			if(!allocSucceeded) throw new SDMSEscape();
			if(resourceChain != null) {
				resourceChain.removeSme(smeId);
			}
		} catch(SDMSEscape e) {
			sysEnv.tx.rollbackSubTransaction(sysEnv);
			return false;
		}
		sysEnv.tx.commitSubTransaction(sysEnv);

		return true;
	}

	private void merge(HashMap target, HashMap source)
	{
		Long L;

		Iterator i = source.keySet().iterator();
		while(i.hasNext()) {
			L = (Long) i.next();
			if(!target.containsKey(L))
				target.put(L, source.get(L));
		}
	}

	public HashMap getScopeFootprint(SystemEnvironment sysEnv, SDMSScope s)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1 && sysEnv.tx.mode == SDMSTransaction.READWRITE)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		SDMSResource r;
		SDMSScope ps;
		HashMap fp = new HashMap();
		HashMap tfp;
		Long sId = s.getId(sysEnv);
		Long psId;

		Vector v = SDMSResourceTable.idx_scopeId.getVector(sysEnv, sId);
		for(int i = 0; i < v.size(); i++) {
			r = (SDMSResource) v.get(i);
			fp.put(r.getNrId(sysEnv), r.getId(sysEnv));
		}

		psId = s.getParentId(sysEnv);
		if(psId != null) {
			ps = SDMSScopeTable.getObject(sysEnv, psId);
			tfp = getScopeFootprint(sysEnv, ps);
			merge(fp,tfp);
		}
		return fp;
	}

	public static Vector getJobFootprint(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1 && sysEnv.tx.mode == SDMSTransaction.READWRITE)
			LockingSystem.lock(sysEnv, sysEnv.sched, ObjectLock.EXCLUSIVE);

		SDMSnpJobFootprint jfp;
		SDMSSchedulingEntity se;
		SDMSEnvironment e;
		SDMSResourceRequirement rr;
		HashMap fp;
		Long smeId = sme.getId(sysEnv);

		try {
			jfp = SDMSnpJobFootprintTable.idx_smeId_getUniqueForUpdate(sysEnv, smeId);
			Vector result = new Vector();
			result.add(jfp.getFpScope(sysEnv)) ;
			result.add(jfp.getFpFolder(sysEnv)) ;
			result.add(jfp.getFpLocal(sysEnv)) ;
			return result;
		} catch(NotFoundException nfe) {

		}

		fp = new HashMap();
		Long seId = sme.getSeId(sysEnv);
		long version = sme.getSeVersion(sysEnv).longValue();

		se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, version);

		Vector v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, se.getNeId(sysEnv), version);

		for(int i = 0; i < v.size(); i++) {
			e = (SDMSEnvironment) v.get(i);
			fp.put(e.getNrId(sysEnv), e);
		}

		Long parentId = se.getFolderId(sysEnv);
		do {
			SDMSFolder f = SDMSFolderTable.getObject(sysEnv, parentId, version);
			Long id = f.getEnvId(sysEnv);
			if(id != null) {
				v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, id, version);

				for(int i = 0; i < v.size(); i++) {
					e = (SDMSEnvironment) v.get(i);
					fp.put(e.getNrId(sysEnv), e);
				}
			}
			parentId = f.getParentId(sysEnv);
		} while(parentId != null);

		v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, se.getFpId(sysEnv), version);

		for(int i = 0; i < v.size(); i++) {
			rr = (SDMSResourceRequirement) v.get(i);
			fp.put(rr.getNrId(sysEnv), rr);
		}

		v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, seId, version);

		for(int i = 0; i < v.size(); i++) {
			rr = (SDMSResourceRequirement) v.get(i);
			fp.put(rr.getNrId(sysEnv), rr);
		}

		return SystemEnvironment.sched.splitSmeFootprint(sysEnv, sme, se, fp, smeId);
	}

	private Vector splitSmeFootprint(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSchedulingEntity se, HashMap fp, Long smeId)
		throws SDMSException
	{
		SDMSResourceRequirement rr;
		SDMSNamedResource nr;
		SDMSResource r;

		SDMSResource bestFit;
		Long bestFitSmeId;
		Long bestFitFId;

		Long nrId;

		HashMap fpFolder = new HashMap();
		HashMap fpLocal = new HashMap();
		HashMap fpScope = new HashMap();

		Vector result = new Vector();
		SDMSKey k = null;
		Vector kv = null;

		long actVersion = sme.getSeVersion(sysEnv).longValue();

		if (sysEnv.tx.rscCache == null)
			sysEnv.tx.rscCache = new HashMap();
		HashMap myRscCache = sysEnv.tx.rscCache;

		Iterator fpi = fp.keySet().iterator();
		while(fpi.hasNext()) {
			bestFit = null;
			bestFitSmeId = null;
			bestFitFId = null;

			nrId = (Long) fpi.next();
			SDMSProxy proxy = (SDMSProxy) fp.get(nrId);
			if (proxy instanceof SDMSResourceRequirement) {
				rr = (SDMSResourceRequirement) proxy;
			} else {
				rr = null;
			}
			if(rr == null) {
				proxy.fix();
				fpScope.put(nrId, proxy);
				continue;
			}
			nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
			if(nr.getUsage(sysEnv).intValue() == SDMSNamedResource.STATIC) {
				rr.fix();
				fpScope.put(nrId, rr);
				continue;
			}

			SDMSSchedulingEntity myse = se;
			SDMSSubmittedEntity mysme = sme;
			boolean hit;
			while(true) {
				hit = false;
				Long fId = myse.getFolderId(sysEnv);
				long myActVersion = mysme.getSeVersion(sysEnv).longValue();
				kv = new Vector();
				while(fId != null) {
					k = new SDMSKey(nrId, fId);
					if (myRscCache.containsKey(k)) {
						doTrace(cEnv, "Cache hit for folder " + k, SEVERITY_DEBUG);
						hit = true;
						Vector e = (Vector) myRscCache.get(k);
						if (e == null) {
							bestFitFId = null;
						} else {
							bestFit = (SDMSResource) e.get(0);
							bestFitFId = (Long) e.get(1);
						}
						break;
					} else {
						doTrace(cEnv, "Cache miss for folder " + k, SEVERITY_DEBUG);
						try {
							r = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, k);
							bestFit = r;
							bestFitFId = fId;
							Vector e = new Vector();
							r.fix();
							e.add(r);
							e.add(fId);
							myRscCache.put(k, e);
							for (int kvi = 0; kvi < kv.size(); ++kvi) {
								myRscCache.put(kv.get(kvi), e);
							}
							break;
						} catch (NotFoundException nfe) {
							fId = SDMSFolderTable.getObject(sysEnv, fId, myActVersion).getParentId(sysEnv);
							kv.add(k);
						}
					}
				}
				if(bestFitFId != null) break;
				doTrace(cEnv, "No folder Resource found for " + k, SEVERITY_DEBUG);
				if (!hit) {
					for (int kvi = 0; kvi < kv.size(); ++kvi) {
						myRscCache.put(kv.get(kvi), null);
					}
				}
				Long pSmeId = mysme.getParentId(sysEnv);
				if(pSmeId == null) break;
				mysme = SDMSSubmittedEntityTable.getObject(sysEnv, pSmeId);
				myse = SDMSSchedulingEntityTable.getObject(sysEnv, mysme.getSeId(sysEnv), myActVersion);
			}

			Long pSmeId = smeId;
			kv = new Vector();
			hit = false;
			while(pSmeId != null) {
				k = new SDMSKey(nrId, pSmeId);
				if (myRscCache.containsKey(k)) {
					doTrace(cEnv, "Cache hit for sme " + k, SEVERITY_DEBUG);
					hit = true;
					Vector e = (Vector) myRscCache.get(k);
					if (e == null) {
						bestFitSmeId = null;
					} else {
						bestFit = (SDMSResource) e.get(0);
						bestFitSmeId = (Long) e.get(1);
					}
					break;
				} else {
					try {
						doTrace(cEnv, "Cache miss for sme " + k, SEVERITY_DEBUG);
						r = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, k);
						bestFit = r;
						bestFitSmeId = pSmeId;
						Vector e = new Vector();
						r.fix();
						e.add(r);
						e.add(pSmeId);
						myRscCache.put(k, e);
						for (int kvi = 0; kvi < kv.size(); ++kvi) {
							myRscCache.put(kv.get(kvi), e);
						}
						break;
					} catch (NotFoundException nfe) {
						pSmeId = SDMSSubmittedEntityTable.getObject(sysEnv, pSmeId).getParentId(sysEnv);
						kv.add(k);
					}
				}
			}
			if(bestFitSmeId == null && !hit) {
				doTrace(cEnv, "No sme Resource found for " + k, SEVERITY_DEBUG);
				for (int kvi = 0; kvi < kv.size(); ++kvi) {
					myRscCache.put(kv.get(kvi), null);
				}
			}
			if(bestFitSmeId != null || bestFitFId != null) {
				Integer requestableAmount = bestFit.getRequestableAmount(sysEnv);
				Integer requestedAmount = rr.getAmount(sysEnv);
				if(requestableAmount != null) {
					if(requestableAmount.compareTo(requestedAmount) < 0 && sysEnv.tx.mode == SDMSTransaction.READWRITE) {
						sme.setToError(sysEnv, "Job cannot run because of resource shortage on resource " + nr.pathString(sysEnv));
					}
				}
				Vector v = new Vector();
				rr.fix();
				v.add(rr);
				bestFit.fix();
				v.add(bestFit);
				if(bestFitSmeId != null) {
					fpLocal.put(nrId, v);
				} else {
					fpFolder.put(nrId, v);
				}
			} else {
				rr.fix();
				fpScope.put(nrId, rr);
			}
		}

		if(sysEnv.tx.mode == SDMSTransaction.READWRITE) {

			SDMSnpJobFootprintTable.table.create(sysEnv, smeId, fpScope, fpFolder, fpLocal);
		}

		result.add(fpScope) ;
		result.add(fpFolder) ;
		result.add(fpLocal) ;
		return result;
	}

	void recalc_sfp(SystemEnvironment sysEnv, Long scopeId, SDMSScope s)
		throws SDMSException
	{
		doTrace(cEnv, "Calculating footprint for scope " + s.pathString(sysEnv), SEVERITY_DEBUG);
		if(s.getType(sysEnv).intValue() == SDMSScope.SERVER) {
			HashMap fp = getScopeFootprint(sysEnv, s);
			doTrace(cEnv, "footprint = " + fp.toString(), SEVERITY_DEBUG);
			(SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, scopeId)).setFp(sysEnv, fp);
		} else {
			Vector v = SDMSScopeTable.idx_parentId.getVector(sysEnv, scopeId);
			for(int i = 0; i < v.size(); i++) {
				s = (SDMSScope) v.get(i);
				recalc_sfp(sysEnv, s.getId(sysEnv), s);
			}
		}
	}

	void destroyEnvironment(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSnpSrvrSRFootprintTable.table.clearTableUnlocked(sysEnv);
	}

	void buildEnvironment(SystemEnvironment sysEnv, boolean jsOnly)
		throws SDMSException
	{
		SDMSScope s;
		Vector v;

		v = SDMSScopeTable.idx_type.getVector(sysEnv, new Integer(SDMSScope.SERVER));
		for(int j = 0; j < v.size(); j++) {
			s = (SDMSScope) v.get(j);
			SDMSnpSrvrSRFootprintTable.table.create(sysEnv, s.getId(sysEnv), null, getScopeFootprint(sysEnv, s));
		}

		if (!jsOnly) {
			Vector rl = new Vector();
			SDMSSubmittedEntity sme;
			SDMSSchedulingEntity se;
			v = SDMSSubmittedEntityTable.idx_state.getVector(sysEnv, new Integer(SDMSSubmittedEntity.DEPENDENCY_WAIT));
			for (int i = 0; i < v.size(); ++i) {
				sme = (SDMSSubmittedEntity) v.get(i);
				se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
				if (se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) continue;
				if (sme.getOldState(sysEnv) != null)
					rl.add(sme.getId(sysEnv));
			}
			v = SDMSSubmittedEntityTable.idx_state.getVector(sysEnv, new Integer(SDMSSubmittedEntity.SYNCHRONIZE_WAIT));
			for (int i = 0; i < v.size(); ++i) {
				sme = (SDMSSubmittedEntity) v.get(i);
				se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
				if (se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) continue;
				if (sme.getOldState(sysEnv) != null)
					rl.add(sme.getId(sysEnv));
			}
			addToRequestList(rl);
		}
		needSched = true;
	}

	public void requestSchedule()
	{
		needSched = true;
		this.wakeUp();
	}

	public void notifyChange(SystemEnvironment sysEnv, SDMSResource r, Long scopeId, int change)
		throws SDMSException
	{
		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		if (scopeId != null) {
			SDMSScope s = null;
			try {
				s = SDMSScopeTable.getObject(sysEnv, scopeId);
			} catch (NotFoundException nfe) {

			}
			if (s != null)
				recalc_sfp(sysEnv, scopeId, s);
		}
		switch(change) {
			case CREATE:
				needReSched = true;
				break;
			case ALTER:
				break;
			case ALTER_REQAMOUNT:
				needReSched = true;
				break;
			case OFFLINE_ONLINE:
				needReSched = true;
				break;
			case DELETE:
				needReSched = true;
				break;
			default:
				throw new FatalException(new SDMSMessage(sysEnv, "03202252140", "Unknown change code $1", new Integer(change)));
		}
		needSched = true;
	}

	public void notifyChange(SystemEnvironment sysEnv, SDMSNamedResource nr, int change)
		throws SDMSException
	{
		switch(change) {
			case CREATE:
				break;
			case ALTER:
				break;
			case DELETE:
				break;
			default:
				throw new FatalException(new SDMSMessage(sysEnv, "03203060018", "Unknown change code $1", new Integer(change)));
		}
		needSched = true;
	}

	public void notifyChange(SystemEnvironment sysEnv, SDMSScope s, int change)
		throws SDMSException
	{
		switch(change) {
			case CREATE:
				if (sysEnv.maxWriter > 1)
					LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);
				SDMSnpSrvrSRFootprintTable.table.create(sysEnv, s.getId(sysEnv), null, getScopeFootprint(sysEnv, s));
				break;
			case ALTER:
				break;
			case DELETE:
				if (sysEnv.maxWriter > 1)
					LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);
				SDMSnpSrvrSRFootprint f = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
				f.delete(sysEnv);
				needReSched = true;
				break;
			case REGISTER:
			case DEREGISTER:
				needReSched = true;
				break;
			case SUSPEND:
				break;
			case RESUME:
				break;
			case SHUTDOWN:
				needSched = true;
				break;
			case MOVE:
			case COPY:
				if (sysEnv.maxWriter > 1)
					LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);
				destroyEnvironment(sysEnv);
				buildEnvironment(sysEnv, true);
				needSched = true;
				break;
			default:
				throw new FatalException(new SDMSMessage(sysEnv, "03202252142", "Unknown change code $1", new Integer(change)));
		}
		needSched = true;
	}

	public void notifyChange(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, int change)
		throws SDMSException
	{
		int size;
		Vector v;

		switch(change) {
			case SUSPEND:
				break;
			case STATECHANGE:
				int s = sme.getState(sysEnv).intValue();
				switch(s) {
					case SDMSSubmittedEntity.FINISHED:
					case SDMSSubmittedEntity.FINAL:
					case SDMSSubmittedEntity.ERROR:
					case SDMSSubmittedEntity.CANCELLED:
					case SDMSSubmittedEntity.BROKEN_ACTIVE:
					case SDMSSubmittedEntity.BROKEN_FINISHED:
						v = SDMSRunnableQueueTable.idx_smeId.getVector(sysEnv, sme.getId(sysEnv));
						size = v.size();
						for (int i = 0; i < size; i ++) {
							SDMSRunnableQueue rq = (SDMSRunnableQueue)v.get(i);
							rq.delete(sysEnv);
						}
						break;
				}
				needSched = true;
				break;
			case FINISH:
			case PRIORITY:
			case RERUN:
			case IGNORE_RESOURCE:
			case SUBMIT:
			case RESUME:
				needSched = true;
				break;
			default:
				throw new FatalException(new SDMSMessage(sysEnv, "03202252317", "Unknown change code $1", new Integer(change)));
		}
	}

	public void requestReschedule()
	{
		needReSched = true;
	}

}

class DoSchedule extends Node
{

	static final int SCHEDULE = 0;
	static final int INITIALIZE = 1;

	int action;

	public DoSchedule()
	{
		super();
		action = SCHEDULE;
		auditFlag = false;
	}

	public DoSchedule(int a)
	{
		super();
		action = a;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		switch(action) {
			case SCHEDULE:
				SystemEnvironment.sched.scheduleProtected(sysEnv);
				break;
			case INITIALIZE:
				SystemEnvironment.sched.buildEnvironment(sysEnv, false);
		}
	}

}

class prioComparator implements Comparator
{

	SystemEnvironment sysEnv;
	long priorityDelay;
	long now;

	public prioComparator(SystemEnvironment e, long p)
	{
		sysEnv = e;
		priorityDelay = p;
		now = System.currentTimeMillis();
	}

	public void setNow()
	{
		now = System.currentTimeMillis();
	}

	public int dynPrio(SDMSSubmittedEntity sme)
		throws SDMSException
	{
		int p = sme.getPriority(sysEnv).intValue();
		int lb = Math.max(SystemEnvironment.priorityLowerBound, sme.getMinPriority(sysEnv).intValue());

		if (p <= lb) return p;

		long t = now - sme.getSubmitTs(sysEnv).longValue();
		long priorityDelay = sme.getAgingAmount(sysEnv).intValue();

		if(priorityDelay != 0)
			t /= (priorityDelay * 60000);
		else
			t = 0;

		if(t >= p)	p = lb;
		else		p = Math.max(p - (int) t, lb);

		return p;
	}

	public int compare(Object o1, Object o2)
	{
		SDMSSubmittedEntity sme1;
		SDMSSubmittedEntity sme2;
		int p1, p2;

		sme1 = (SDMSSubmittedEntity) o1;
		sme2 = (SDMSSubmittedEntity) o2;

		try {
			p1 = dynPrio(sme1);
			p2 = dynPrio(sme2);
			if(p1 < p2) return -1;
			if(p1 > p2) return 1;

			int rp1, rp2;
			rp1 = sme1.getRawPriority(sysEnv).intValue();
			rp2 = sme2.getRawPriority(sysEnv).intValue();
			if(rp1 < rp2) return -1;
			if(rp1 > rp2) return 1;

			long l1, l2;
			l1 = sme1.getId(sysEnv).longValue();
			l2 = sme2.getId(sysEnv).longValue();
			if(l1 < l2) return -1;
			if(l1 > l2) return 1;
		} catch (SDMSException e) {
			throw new RuntimeException("Error while comparing : " + e.toString());
		}
		return 0;
	}
}

class Reservator
{
	public Long rId;
	public Long smeId;
	public int amount;
	public Lockmode lock;
	public int seq;

	public Reservator(Long r, Long s)
	{
		rId = r;
		smeId = s;
		amount = 0;
		lock = new Lockmode();
		seq = 0;
	}

	public Reservator(Long r, Long s, int a)
	{
		rId = r;
		smeId = s;
		amount = a;
		lock = new Lockmode();
		seq = 0;
	}

	public Reservator(Long r, Long s, int a, Lockmode l)
	{
		rId = r;
		smeId = s;
		amount = a;
		lock = l;
		seq = 0;
	}

	public Reservator(Long r, Long s, int a, int l)
	{
		rId = r;
		smeId = s;
		amount = a;
		lock = new Lockmode(l);
		seq = 0;
	}

	public int addLock(Lockmode lm)
	{
		int l = lm.getLockmode();
		int ol = lock.getLockmode();
		if(l != Lockmode.N)  {
			if(ol == Lockmode.N) lock.setLockmode(l);
			else {
				if(l != ol) lock.setLockmode(Lockmode.X);
			}
		}
		return lock.getLockmode();
	}
}

class Locklist
{
	private HashMap lpr;
	private HashMap lpj;
	static private final Long ZERO = new Long(0);

	public Locklist()
	{
		lpr = new HashMap();
		lpj = new HashMap();
	}

	public Reservator get(Long rId, Long smeId)
	{
		HashMap h = (HashMap) lpr.get(rId);
		if(h == null) return new Reservator(rId, smeId);
		Reservator r = (Reservator) h.get(smeId);
		if(r == null) return new Reservator(rId, smeId);
		return r;
	}

	public Reservator get(Long rId)
	{
		return get(rId, ZERO);
	}

	public void set(Reservator r)
	{
		HashMap h = (HashMap) lpr.get(r.rId);
		if(h == null) {
			h = new HashMap();
			lpr.put(r.rId, h);
		}
		h.put(r.smeId, r);
		Reservator rt = (Reservator) h.get(ZERO);
		if(rt == null) {
			rt = new Reservator(r.rId, ZERO);
			h.put(ZERO, rt);
		}
		rt.amount += r.amount;
		rt.addLock(r.lock);
		rt.seq++;
		r.seq = rt.seq;

		h = (HashMap) lpj.get(r.smeId);
		if(h == null) {
			h = new HashMap();
			lpj.put(r.smeId, h);
		}
		h.put(r.rId, r);
	}

	public void removeSme(Long smeId)
	{
		HashMap h = (HashMap) lpj.get(smeId);
		if(h == null) return;
		Iterator i = h.keySet().iterator();
		while(i.hasNext()) {
			Long rId = (Long) i.next();
			HashMap rh = (HashMap) lpr.get(rId);
			rh.remove(smeId);
			rh.remove(ZERO);

			Reservator zr = new Reservator(rId, ZERO);
			Iterator j = rh.values().iterator();
			while(j.hasNext()) {
				Reservator r = (Reservator) j.next();
				zr.amount += r.amount;
				zr.addLock(r.lock);
				zr.seq++;
			}
			rh.put(ZERO, zr);
		}
		lpj.remove(smeId);
	}

	public void remove(Long rId, Long smeId)
	{
		HashMap h = (HashMap) lpj.get(smeId);
		if(h == null) return;
		if(h.remove(rId) == null) return;
		h = (HashMap) lpr.get(rId);
		h.remove(smeId);
		h.remove(ZERO);
		Reservator zr = new Reservator(rId, ZERO);
		Iterator i = h.values().iterator();
		while(i.hasNext()) {
			Reservator r = (Reservator) i.next();
			zr.amount += r.amount;
			zr.addLock(r.lock);
			zr.seq++;
		}
		h.put(ZERO, zr);
	}
}

