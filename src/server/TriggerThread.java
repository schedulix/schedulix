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

import de.independit.scheduler.locking.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class TriggerThread extends InternalSession
{

	public static final String __version = "@(#) $Id: TriggerThread.java,v 2.11.6.3 2013/09/11 11:50:39 ronald Exp $";

	public final static String name = "TriggerThread";

	private long nextTime;
	private int maxWakeupInterval;
	private LockableHashSet jobsToResume;
	private boolean firstTime;

	public TriggerThread(SystemEnvironment env, SyncFifo f)
		throws SDMSException
	{
		super(name);
		NR = 1234323;
		maxWakeupInterval = SystemEnvironment.ttWakeupInterval*1000;
		initThread(env, f, NR, name, maxWakeupInterval);
		jobsToResume = new LockableHashSet();
		firstTime = true;
	}

	protected Node getNode(int m)
	{
		if(m == INITIALIZE)	return new DoCheckTrigger(DoCheckTrigger.INITIALIZE);
		return new DoCheckTrigger();
	}

	public void checkTrigger(SystemEnvironment sysEnv)
		throws SDMSException
	{
		long now = System.currentTimeMillis();
		nextTime = Long.MAX_VALUE;
		Iterator i;
		int ctr = 0;
		doTrace(cEnv, "Start Resuming Jobs", SEVERITY_MESSAGE);

		LockingSystem.lock(jobsToResume, ObjectLock.EXCLUSIVE);
		if (firstTime) {

			i = SDMSSubmittedEntityTable.table.iterator(sysEnv,
			new SDMSFilter() {
				public boolean isValid(SystemEnvironment sysEnv, SDMSProxy p)
				throws SDMSException {
					SDMSSubmittedEntity sme = (SDMSSubmittedEntity) p;
					if (sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.NOSUSPEND) return false;
					if (sme.getResumeTs(sysEnv) == null) return false;
					return true;
				}
			}
			                                           );
			while (i.hasNext()) {
				SDMSSubmittedEntity sme = (SDMSSubmittedEntity) i.next();
				if (sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.NOSUSPEND) continue;
				Long resumeTs = sme.getResumeTs(sysEnv);
				if (resumeTs != null) {
					jobsToResume.add(sme.getId(sysEnv));
				}
			}
			firstTime = false;
		}
		Vector<Long> jobsResumed = new Vector<Long>();
		try {
			i = jobsToResume.iterator();
			while (i.hasNext()) {
				Long smeId = (Long) i.next();
				SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
				if (sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.NOSUSPEND) continue;
				Long resumeTs = sme.getResumeTs(sysEnv);
				if (resumeTs != null) {

					long rts = resumeTs.longValue();
					if (rts <= now) {
						i.remove();
						jobsResumed.add(smeId);
						sme.resume(sysEnv, false);
						ctr++;
					} else {
						if (rts < nextTime) nextTime = rts;
					}
				}
			}
		} catch (SerializationException s) {
			jobsToResume.addAll(jobsResumed);
			throw s;
		}
		LockingSystem.release(jobsToResume);
		doTrace(cEnv, "End Resuming Jobs (" + ctr + " jobs resumed)", SEVERITY_MESSAGE);
		now = System.currentTimeMillis();
		if (now + maxWakeupInterval > nextTime) {
			super.wakeupInterval = Math.max((int) (nextTime - now), 0);
		} else {
			super.wakeupInterval = maxWakeupInterval;
		}
	}

	public void removeFromJobsToResume(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		LockingSystem.lock(jobsToResume, ObjectLock.EXCLUSIVE);
		jobsToResume.remove(id);
		LockingSystem.release(jobsToResume);
	}

	public void addToJobsToResume(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		LockingSystem.lock(jobsToResume, ObjectLock.EXCLUSIVE);
		jobsToResume.add(id);
		LockingSystem.release(jobsToResume);
	}

}

class DoCheckTrigger extends Node
{

	static final int SCHEDULE = 0;
	static final int INITIALIZE = 1;

	int action;

	public DoCheckTrigger()
	{
		super();
		action = SCHEDULE;
		auditFlag = false;
	}

	public DoCheckTrigger(int a)
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
				SystemEnvironment.tt.checkTrigger(sysEnv);
				break;
			case INITIALIZE:

				break;
		}
	}

}
