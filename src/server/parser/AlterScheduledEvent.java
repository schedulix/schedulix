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


package de.independit.scheduler.server.parser;

import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.timer.*;

public class AlterScheduledEvent
	extends Node
{
	public static final String __version = "@(#) $Id: AlterScheduledEvent.java,v 2.9.4.1 2013/03/14 10:24:22 ronald Exp $";

	private final ObjectURL obj;
	private final WithHash with;
	private final Boolean active;
	private final boolean noerr;

	public AlterScheduledEvent (ObjectURL o, WithHash w, Boolean ne)
	{
		super();
		obj = o;
		with = w;
		if(!with.containsKey(ParseStr.S_ACTIVE)) active = null;
		else	active = (Boolean) with.get(ParseStr.S_ACTIVE);
		noerr = ne.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final SDMSScheduledEvent scev = (SDMSScheduledEvent) obj.resolve (sysEnv);
		boolean calUpdNeeded = false;
		String addMsg = null;

		if(active != null) {
			final boolean isActive = scev.getIsActive (sysEnv).booleanValue();
			if (active.booleanValue() != isActive) {
				if (! isActive) {
					scev.setIsBroken (sysEnv, new Boolean (false));
					scev.setErrorCode (sysEnv, null);
					scev.setErrorMsg (sysEnv, null);
				}

				scev.setIsActive (sysEnv, active);

				SystemEnvironment.timer.notifyChange (sysEnv, scev, TimerThread.ALTER);
				if (! active.booleanValue()) scev.clearCalendar(sysEnv);
				else	calUpdNeeded = true;
			}
		}

		if(with.containsKey(ParseStr.S_GROUP)) {
			final String gName = (String) with.get (ParseStr.S_GROUP);
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
			                         sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
			scev.setOwnerId(sysEnv, gId);
		}

		if (with.containsKey (ParseStr.S_BACKLOG_HANDLING))
			scev.setBacklogHandling (sysEnv, (Integer) with.get (ParseStr.S_BACKLOG_HANDLING));

		if (with.containsKey (ParseStr.S_CALENDAR)) {
			Boolean isCalendar = (Boolean) with.get (ParseStr.S_CALENDAR);
			if (!isCalendar.booleanValue()) {
				scev.clearCalendar(sysEnv);
				scev.setCalendarHorizon (sysEnv, null);
			} else {
				if (!scev.getIsCalendar(sysEnv).booleanValue()) {
					calUpdNeeded = true;
					scev.setCalendarHorizon (sysEnv, null);
				}
			}
			scev.setIsCalendar (sysEnv, isCalendar);
		}

		if (with.containsKey (ParseStr.S_HORIZON)) {
			if (scev.getIsCalendar(sysEnv).booleanValue()) {
				Integer calendarHorizon = (Integer) with.get (ParseStr.S_HORIZON);
				calUpdNeeded = true;
				scev.setCalendarHorizon (sysEnv, calendarHorizon);
			} else {
				addMsg = ". Superfluous horizon specification ignored";
			}
		}

		if (with.containsKey (ParseStr.S_SUSPEND_LIMIT)) {
			final WithHash suspendLimitWith = (WithHash) with.get (ParseStr.S_SUSPEND_LIMIT);
			if (suspendLimitWith == null) {
				scev.setSuspendLimit           (sysEnv, null);
				scev.setSuspendLimitMultiplier (sysEnv, null);
			} else {
				final Integer suspendLimit           = (Integer) suspendLimitWith.get (ParseStr.S_INTERVAL);
				final Integer suspendLimitMultiplier = IntervalUtil.getMultiplier (sysEnv, suspendLimitWith);

				scev.setSuspendLimit           (sysEnv, suspendLimit);
				scev.setSuspendLimitMultiplier (sysEnv, suspendLimitMultiplier);
			}
		}

		if (calUpdNeeded) {
			SDMSSchedule sce = SDMSScheduleTable.getObject(sysEnv, scev.getSceId(sysEnv));
			Long nextActivityTime = scev.getNextActivityTime(sysEnv);
			scev.updateCalendar(sysEnv, nextActivityTime, sce);
		}

		result.setFeedback (new SDMSMessage (sysEnv, "04207262225", "Scheduled Event altered" + (addMsg == null ? "" : addMsg)));
	}
}
