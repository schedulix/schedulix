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

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class TriggerThread extends InternalSession
{


	public final static String name = "TriggerThread";

	private long nextTime;
	private int maxWakeupInterval;

	public TriggerThread(SystemEnvironment env, SyncFifo f)
	throws SDMSException
	{
		super(name);
		NR = 1234323;
		maxWakeupInterval = SystemEnvironment.ttWakeupInterval*1000;
		initThread(env, f, NR, name, maxWakeupInterval);
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

		i = SDMSSubmittedEntityTable.table.iterator(sysEnv,
		new SDMSFilter() {
			public boolean isValid(SystemEnvironment sysEnv, SDMSProxy p)
			throws SDMSException {
				SDMSSubmittedEntity sme = (SDMSSubmittedEntity) p;
				if (!sme.getIsSuspended(sysEnv).booleanValue()) return false;
				if (sme.getResumeTs(sysEnv) == null) return false;
				return true;
			}
		});
		while (i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity) i.next();
			if (!sme.getIsSuspended(sysEnv).booleanValue()) continue;
			Long resumeTs = sme.getResumeTs(sysEnv);
			if (resumeTs != null) {

				long rts = resumeTs.longValue();
				if (rts <= now) {
					sme.resume(sysEnv);
					SDMSAuditTrailTable.table.create(sysEnv, cEnv.uid(), new Long ((new java.util.Date()).getTime()),
					                                 new Integer(SDMSAuditTrail.RESUME),
					                                 new Integer(SDMSAuditTrail.JOB),
					                                 sme.getId(sysEnv), sme.getId(sysEnv),
					                                 Boolean.FALSE,
					                                 null,
					                                 "Automatic resume");

				} else {
					if (rts < nextTime) nextTime = rts;
				}
			}
		}
		doTrace(cEnv, "End Resuming Jobs (" + ctr + " jobs resumed)", SEVERITY_MESSAGE);
		now = System.currentTimeMillis();
		if (now + maxWakeupInterval > nextTime) {
			super.wakeupInterval = Math.max((int) (nextTime - now), 0);
		} else {
			super.wakeupInterval = maxWakeupInterval;
		}
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
