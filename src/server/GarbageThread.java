/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

	public GarbageThread(SystemEnvironment env, SyncFifo f)
	throws SDMSException
	{
		super(name);
		NR = 1234322;
		initThread(env, f, NR, name, SystemEnvironment.gcWakeupInterval*60*1000);

		preserveTime = SystemEnvironment.preserveTime;
	}

	protected Node getNode(int m)
	{
		if(m == INITIALIZE)	return new DoGarbage(DoGarbage.INITIALIZE);
		else			return new DoGarbage();
	}

	public void collect(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long smeId;
		int state;
		final long now = System.currentTimeMillis();
		long finaltime;

		doTrace(cEnv, "Start Garbage Collect (now = " + now + " preserveTime = " + preserveTime + ")", SEVERITY_INFO);

		SDMSFilter filter = new SDMSFilter () {
			public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
				SDMSSubmittedEntity sme = (SDMSSubmittedEntity)obj;
				if (sme.getId(sysEnv).equals(sme.getMasterId(sysEnv))) {
					int state = sme.getState(sysEnv).intValue();
					if (state == SDMSSubmittedEntity.FINAL || state == SDMSSubmittedEntity.CANCELLED) {
						long finaltime = sme.getFinalTs(sysEnv).longValue();
						if (now - finaltime > preserveTime)
							return true;
					}
				}
				return false;
			}
		};

		Iterator i = SDMSSubmittedEntityTable.table.iterator(sysEnv, filter);
		while(i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity) i.next();
			sme.releaseMaster(sysEnv);
		}

		long purgeLow = sysEnv.roTxList.first(sysEnv);

		long first = sysEnv.seVersionList.first(sysEnv);
		if (first > purgeLow)
			first = purgeLow;
		sysEnv.vPurgeSet.purge(sysEnv, first);

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
