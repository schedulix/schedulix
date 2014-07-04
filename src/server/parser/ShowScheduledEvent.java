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

import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.timer.TimerUnit;

public class ShowScheduledEvent
	extends ShowCommented
{
	public static final String __version = "@(#) $Id: ShowScheduledEvent.java,v 2.15.2.2 2013/06/18 09:49:37 ronald Exp $";

	private final Vector path;
	private final String name;

	private static final String empty = "";
	private GregorianCalendar gc;
	private TimeZone gmtTz;
	private TimeZone tz;

	public ShowScheduledEvent (Vector p, String n)
	{
		super();
		path = p;
		name = n;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public SDMSOutputContainer renderCalendar(SystemEnvironment sysEnv, Long scevId, SDMSSchedule sce)
		throws SDMSException
	{
		Vector desc = new Vector();

		desc.add ("ID");
		desc.add ("STARTTIME");

		SDMSOutputContainer ct_container = new SDMSOutputContainer(sysEnv, "CALENDAR_TABLE", desc);

		Vector v = SDMSCalendarTable.idx_scevId.getVector(sysEnv, scevId);
		Comparator cmp = SDMSCalendar.getComparator(sysEnv);
		Collections.sort(v,cmp);
		for (int i = 0; i < v.size(); ++i) {
			SDMSCalendar c = (SDMSCalendar) v.get(i);
			Vector data = new Vector();
			data.add (c.getId(sysEnv));
			Long startTime = c.getStarttime(sysEnv);
			gc.setTimeZone(gmtTz);
			gc.setTimeInMillis(startTime.longValue() * 1000);
			gc.setTimeZone(tz);
			data.add (new DateTime(gc.getTimeInMillis() / 1000).toString(tz, true));

			ct_container.addData(sysEnv, data);
		}

		return ct_container;
	}

	public void go (SystemEnvironment sysEnv)
		throws SDMSException
	{
		final Long sceId = SDMSScheduleTable.pathToId (sysEnv, path);
		final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, sceId);

		gc = SystemEnvironment.newGregorianCalendar();
		gmtTz = SystemEnvironment.systemTimeZone;
		tz = TimeZone.getTimeZone(sce.getTimeZone(sysEnv));

		final SDMSEvent evt = SDMSEventTable.idx_name_getUnique (sysEnv, name);
		final Long evtId = evt.getId (sysEnv);
		final SDMSKey scevKey = new SDMSKey (sceId, evtId);
		final SDMSScheduledEvent scev = SDMSScheduledEventTable.idx_sceId_evtId_getUnique (sysEnv, scevKey);
		if(!scev.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411724", "Insufficient privileges"));

		Vector desc = new Vector();

		desc.add ("ID");
		desc.add ("OWNER");

		desc.add ("SCHEDULE");

		desc.add ("EVENT");

		desc.add ("ACTIVE");

		desc.add ("EFF_ACTIVE");

		desc.add ("BROKEN");

		desc.add ("ERROR_CODE");

		desc.add ("ERROR_MSG");

		desc.add ("LAST_START");

		desc.add ("NEXT_START");

		desc.add ("NEXT_CALC");
		desc.add ("CREATOR");
		desc.add ("CREATE_TIME");
		desc.add ("CHANGER");
		desc.add ("CHANGE_TIME");
		desc.add ("PRIVS");

		desc.add ("BACKLOG_HANDLING");

		desc.add ("SUSPEND_LIMIT");

		desc.add ("EFFECTIVE_SUSPEND_LIMIT");

		desc.add ("CALENDAR");

		desc.add ("CALENDAR_HORIZON");

		desc.add ("EFFECTIVE_CALENDAR_HORIZON");
		desc.add ("COMMENT");
		desc.add ("COMMENTTYPE");

		desc.add ("CALENDAR_TABLE");

		final Vector data = new Vector();
		final Long scevId = scev.getId (sysEnv);

		data.add (scevId);

		final Long ownerId = scev.getOwnerId (sysEnv);
		final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
		data.add (g.getName (sysEnv));

		data.add (sce.pathVector (sysEnv));

		data.add (evt.getName (sysEnv));

		data.add (scev.getIsActive (sysEnv));

		data.add (new Boolean (scev.isReallyActive (sysEnv)));

		data.add (scev.getIsBroken (sysEnv));

		final String errorCode = scev.getErrorCode (sysEnv);
		if (errorCode == null)
			data.add (empty);
		else
			data.add (errorCode);

		final String errorMsg = scev.getErrorMsg (sysEnv);
		if (errorMsg == null)
			data.add (empty);
		else
			data.add (errorMsg);

		final Long lastStartTime = scev.getLastStartTime (sysEnv);
		if (lastStartTime == null)
			data.add (empty);
		else {
			gc.setTimeZone(gmtTz);
			gc.setTimeInMillis(lastStartTime.longValue() * 1000);
			gc.setTimeZone(tz);
			data.add (new DateTime(gc.getTimeInMillis() / 1000).toString(tz));
		}

		final Long nextActivityTime = scev.getNextActivityTime (sysEnv);
		if (nextActivityTime == null) {
			data.add (empty);
			data.add (empty);
		} else {
			final boolean nextActivityIsTrigger = scev.getNextActivityIsTrigger (sysEnv).booleanValue();

			if (nextActivityIsTrigger) {
				gc.setTimeZone(gmtTz);
				gc.setTimeInMillis(nextActivityTime.longValue() * 1000);
				gc.setTimeZone(tz);
				data.add (new DateTime(gc.getTimeInMillis() / 1000).toString(tz));
				data.add (empty);
			} else {
				data.add (empty);
				gc.setTimeZone(gmtTz);
				gc.setTimeInMillis(nextActivityTime.longValue() * 1000);
				gc.setTimeZone(tz);
				data.add (new DateTime(gc.getTimeInMillis() / 1000).toString(tz));
			}
		}
		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, scev.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(scev.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, scev.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(scev.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(scev.getPrivileges(sysEnv).toString());

		data.add (scev.getBacklogHandlingAsString (sysEnv));

		final TimerUnit suspendLimit = new TimerUnit (scev.getSuspendLimitMultiplier (sysEnv), scev.getSuspendLimit (sysEnv));
		if (suspendLimit.isINF())
			data.add (empty);
		else
			data.add (suspendLimit.asString());
		data.add (scev.getEffectiveSuspendLimit(sysEnv).asString());

		Boolean isCalendar = scev.getIsCalendar(sysEnv);
		data.add(scev.getIsCalendarAsString(sysEnv));
		if (isCalendar.booleanValue()) {
			Integer horizon = scev.getCalendarHorizon(sysEnv);
			data.add(horizon == null ? empty : horizon);
			data.add(scev.getEffectiveCalendarHorizon(sysEnv));
		} else {
			data.add(empty);
			data.add(empty);
		}
		data.add(getCommentDescription(sysEnv, scevId));
		data.add(getCommentInfoType(sysEnv, scevId));
		data.add(renderCalendar(sysEnv, scevId, sce));

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "Scheduled Event", desc, data);
		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04207262108", "Scheduled Event shown"));

	}
}
