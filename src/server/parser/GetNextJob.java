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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.cmdline.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.jobserver.RepoIface;

public class GetNextJob extends JobDistribution
{

	public final static String __version = "@(#) $Id: GetNextJob.java,v 2.18.6.2 2013/06/18 09:49:31 ronald Exp $";

	public GetNextJob()
	{
		super();
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSScope s;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Vector data = new Vector();

		desc.add (RepoIface.COMMAND);

		s = SDMSScopeTable.getObject(sysEnv, sysEnv.cEnv.uid());

		if(s.getIsTerminate(sysEnv).booleanValue()) {
			data.add(RepoIface.CMD_SHUTDOWN);
			s = SDMSScopeTable.getObjectForUpdate(sysEnv, sysEnv.cEnv.uid());
			s.setIsTerminate(sysEnv, Boolean.FALSE);
		} else {
			if (s.getHasAlteredConfig (sysEnv).booleanValue()) {
				data.add (RepoIface.CMD_ALTER);

				desc.add (RepoIface.ALTER_CONFIG);
				data.add (ScopeConfig.get (sysEnv, s, false));

				s = SDMSScopeTable.getObjectForUpdate(sysEnv, sysEnv.cEnv.uid());
				s.setHasAlteredConfig (sysEnv, Boolean.FALSE);
			} else {
				if(s.getIsSuspended(sysEnv).booleanValue() || !s.getIsRegistered(sysEnv).booleanValue()) {
					data.add(RepoIface.CMD_NOP);
				} else {

					while(!search_next_job(sysEnv, s, desc, data)) {
						desc = new Vector();
						data = new Vector();
						desc.add(RepoIface.COMMAND);
					}
				}
			}
		}

		SystemEnvironment.notifier.removeFromPingList(s.getId(sysEnv));
		long l = sysEnv.cEnv.last();
		Long lastActive = s.getLastActive(sysEnv);
		if (lastActive == null || l > lastActive.longValue()) {
			s = SDMSScopeTable.getObjectForUpdate(sysEnv, sysEnv.cEnv.uid());
			s.setLastActive(sysEnv, new Long(l));
		}

		d_container = new SDMSOutputContainer(sysEnv, "Jobserver Command", desc, data);

		result.setOutputContainer(d_container);
	}

	private boolean search_next_job(SystemEnvironment sysEnv, SDMSScope s, Vector desc, Vector data)
		throws SDMSException
	{
		Long sId = s.getId(sysEnv);
		SDMSRunnableQueue rq = null;
		int rqState;
		boolean rc;

		sysEnv.tx.beginSubTransaction(sysEnv);
		Vector v;
		SDMSSubmittedEntity sme = null;
		SDMSKillJob kj = null;

		Long smeId = null;

		Long jId;
		long now = new Date().getTime();
		v = SDMSRunnableQueueTable.idx_scopeId_state.getVectorForUpdate(sysEnv, new SDMSKey(sId, new Integer(SDMSSubmittedEntity.STARTING)));
		Iterator i_sj = v.iterator();
		while (i_sj.hasNext()) {
			rq = (SDMSRunnableQueue) i_sj.next();
			rqState = rq.getState(sysEnv).intValue();
			smeId = rq.getSmeId(sysEnv);
			try {
				sme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, smeId);
			} catch (NotFoundException nfe) {
				kj = SDMSKillJobTable.getObjectForUpdate(sysEnv, smeId);
			}
			Long startingTs;
			synchronized (SystemEnvironment.jidsStarting) {
				startingTs = SystemEnvironment.jidsStarting.get(smeId);
			}
			if (startingTs != null) {
				long sTs = startingTs.longValue();
				if (sTs + SystemEnvironment.startingResendDelay > now)
					continue;
			}
			synchronized (SystemEnvironment.jidsStarting) {
				SystemEnvironment.jidsStarting.put(smeId, new Long(now));
			}
			break;
		}
		if (sme == null  && kj == null) {
			SDMSFilter filter = new SDMSFilter() {
				public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
					Long smeId = ((SDMSRunnableQueue)obj).getSmeId(sysEnv);
					try {
						SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, smeId);
						if(sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.NOSUSPEND && sme.getParentSuspended(sysEnv).intValue() == 0)
							return true;
						else
							return false;
					} catch (NotFoundException nfe) {
						return true;
					}
				}
			};
			v = SDMSRunnableQueueTable.idx_scopeId_state.getVectorForUpdate(sysEnv, new SDMSKey(sId, new Integer(SDMSSubmittedEntity.RUNNABLE)), filter);
			if(v.size() == 0) {
				if (sysEnv.maxWriter == 1) {
					sysEnv.tx.commitSubTransaction(sysEnv);
					SystemEnvironment.sched.getNextJobSchedule(sysEnv);
					sysEnv.tx.beginSubTransaction(sysEnv);
					v = SDMSRunnableQueueTable.idx_scopeId_state.getVector(sysEnv, new SDMSKey(sId, new Integer(SDMSSubmittedEntity.RUNNABLE)), filter);
				} else {
					SystemEnvironment.sched.requestSchedule();
				}
			}
			if (v.size() > 0) {
				Long minRunnableTs = Long.MAX_VALUE;
				int candidates = 0;
				for(int i = 0; i < v.size(); i++) {
					rq = (SDMSRunnableQueue) v.get(i);
					rqState = rq.getState(sysEnv).intValue();
					try {
						smeId = rq.getSmeId(sysEnv);

						SDMSSubmittedEntity tmpsme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, smeId);
						if (tmpsme.getIsDisabled(sysEnv).booleanValue()) {
							tmpsme.finishDisabledOrBatch(sysEnv);
							SDMSThread.doTrace(env, "(DISABLED BUG): Disabled Job " +  tmpsme.getId(sysEnv) + " finished in getNextJob()", SDMSThread.SEVERITY_WARNING);
							continue;
						}
						if (tmpsme.getRunnableTs(sysEnv) > minRunnableTs) continue;
						candidates++;
						sme = tmpsme;
						minRunnableTs = sme.getRunnableTs(sysEnv);
						continue;
					} catch (NotFoundException nfe) {
						kj = SDMSKillJobTable.getObject(sysEnv, smeId);
						if (kj.getRunnableTs(sysEnv) > minRunnableTs) continue;
						candidates++;
						minRunnableTs = kj.getRunnableTs(sysEnv);
					}
				}
			}
		}
		if(sme == null) {
			if(kj != null) {
				try {
					rc = startKillJob(sysEnv, kj, s, desc, data);
				} catch (SerializationException e) {
					throw e;
				} catch (SDMSException e) {
					SDMSThread.doTrace(env, "Exception from startKillJob : " + e.toString(), e.getStackTrace(), SDMSThread.SEVERITY_ERROR);
					setToError(sysEnv, kj, smeId, e.toSDMSMessage());
					rc = false;
				}
			} else {
				data.add(RepoIface.CMD_NOP);
				sysEnv.tx.commitSubTransaction(sysEnv);
				rc = true;
			}
		} else {
			try {
				rc = startJob(sysEnv, sme, s, desc, data);
			} catch (SerializationException e) {
				throw e;
			} catch (SDMSException e) {
				SDMSThread.doTrace(env, "Exception from startJob : " + e.toString(), e.getStackTrace(), SDMSThread.SEVERITY_ERROR);
				setToError(sysEnv, sme, smeId, e.toSDMSMessage());
				rc = false;
			}
		}
		return rc;
	}
}

