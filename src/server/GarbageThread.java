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
import de.independit.scheduler.server.timer.*;

public class GarbageThread extends InternalSession
{

	public static final String __version = "@(#) $Id: GarbageThread.java,v 2.13.2.2 2013/03/19 17:16:44 ronald Exp $";

	public final static String name = "GarbageCollection";
	private final long preserveTime;
	private final long maxPreserveTime;

	public GarbageThread(SystemEnvironment env, SyncFifo f)
		throws SDMSException
	{
		super(name);
		NR = 1234322;
		initThread(env, f, NR, name, SystemEnvironment.gcWakeupInterval*60*1000);

		preserveTime = SystemEnvironment.preserveTime;
		maxPreserveTime = SystemEnvironment.maxPreserveTime;
	}

	protected Node getNode(int m)
	{
		if(m == INITIALIZE)	return new DoGarbage(DoGarbage.INITIALIZE);
		else			return new DoGarbage();
	}

	public void collect(final SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long smeId;
		int state;
		final long now = System.currentTimeMillis();
		long finaltime;

		doTrace(cEnv, "Start Garbage Collect (now = " + now + " preserveTime = " + preserveTime + ")", SEVERITY_INFO);

		Vector v_masters = new Vector();
		SDMSFilter filter = new SDMSFilter () {
			public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
				SDMSSubmittedEntity sme = (SDMSSubmittedEntity)obj;
				if (sme.getId(sysEnv).equals(sme.getMasterId(sysEnv))) {
					int state = sme.getState(sysEnv).intValue();
					if (state == SDMSSubmittedEntity.FINAL || state == SDMSSubmittedEntity.CANCELLED) {
						return true;
					}
				}
				return false;
			}
		};
		Iterator i = SDMSSubmittedEntityTable.table.iterator(sysEnv, filter, true );
		while(i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity) i.next();
			v_masters.add(sme);
		}

		Collections.sort (v_masters,
			new Comparator () {
				public int compare (Object o1, Object o2) {
					try {
						SDMSSubmittedEntity sme_1 = (SDMSSubmittedEntity)o1;
						SDMSSubmittedEntity sme_2 = (SDMSSubmittedEntity)o2;

						long se_id_1 = sme_1.getSeId(sysEnv).longValue();
						long se_id_2 = sme_2.getSeId(sysEnv).longValue();
						if (se_id_1 != se_id_2) {
							if (se_id_1 < se_id_2)
								return -1;
							else
								return 1;
						} else {
							long submit_ts_1 = sme_1.getSubmitTs(sysEnv).longValue();
							long submit_ts_2 = sme_2.getSubmitTs(sysEnv).longValue();
							if (submit_ts_1 != submit_ts_2) {
								if (submit_ts_1 < submit_ts_2)
									return 1;
								else
									return -1;
							}
							return 0;
						}
					} catch (SDMSException e) {
						return 0;
					}
				}
				public boolean equals (Object o1, Object o2) {
					try {
						SDMSSubmittedEntity sme_1 = (SDMSSubmittedEntity)o1;
						SDMSSubmittedEntity sme_2 = (SDMSSubmittedEntity)o2;

						long se_id_1 = sme_1.getSeId(sysEnv).longValue();
						long se_id_2 = sme_2.getSeId(sysEnv).longValue();
						if (se_id_1 != se_id_2) {
							return false;
						} else {
							long submit_ts_1 = sme_1.getSubmitTs(sysEnv).longValue();
							long submit_ts_2 = sme_2.getSubmitTs(sysEnv).longValue();
							if (submit_ts_1 != submit_ts_2) {
								return false;
							}
							return true;
						}
					} catch (SDMSException e) {
						return false;
					}
				}
			}
		);

		int masterCtr = 0;
		long seId = 0;
		long oldSeId = 0;
		i = v_masters.iterator();
		while(i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity) i.next();
			seId = sme.getSeId(sysEnv).longValue();
			if (seId != oldSeId) {
				masterCtr = 0;
				oldSeId = seId;
			}
			masterCtr++;
			finaltime = sme.getFinalTs(sysEnv).longValue();
			if (now - finaltime <= maxPreserveTime) {
				if (masterCtr <= sysEnv.minHistoryCount)
					continue;
				if (now - finaltime <= preserveTime)
					if (sysEnv.maxHistoryCount == 0 || masterCtr <= sysEnv.maxHistoryCount)
						continue;
			}
			sme.releaseMaster(sysEnv);
		}

		long purgeLow = sysEnv.roTxList.first(sysEnv);

		long first = sysEnv.seVersionList.first(sysEnv);
		if (first > purgeLow)
			first = purgeLow;
		sysEnv.vPurgeSet.purge(sysEnv, first);

		i = SDMSSystemMessageTable.table.iterator(sysEnv, null, false );
		while (i.hasNext()) {
			SDMSSystemMessage msg = (SDMSSystemMessage) i.next();
			Integer msgType = msg.getMsgType(sysEnv);
			if (msgType.intValue() != SDMSSystemMessage.APPROVAL)
				continue;
			Long msgSmeId = msg.getSmeId(sysEnv);
			if (!SDMSSubmittedEntityTable.table.exists(sysEnv, msgSmeId)) {
				msg.delete(sysEnv);
			}
		}

		doTrace(cEnv, "End Garbage Collect", SEVERITY_INFO);
	}
}

class DoGarbage extends Node
{

	static final int SCHEDULE = 0;
	static final int INITIALIZE = 1;

	int action;

	public DoGarbage()
	{
		super();
		action = SCHEDULE;
		auditFlag = false;
	}

	public DoGarbage(int a)
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
				SystemEnvironment.garb.collect(sysEnv);
				break;
			case INITIALIZE:

				break;
		}
	}

}
