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
import de.independit.scheduler.server.timer.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.timer.TimerUnit;

public class ListScheduledEvent
	extends Node
{
	public static final String __version = "@(#) $Id: ListScheduledEvent.java,v 2.11.8.2 2013/06/18 09:49:34 ronald Exp $";

	private static final String empty = "";
	private DateTime starttime = null;
	private DateTime endtime = null;
	private TimeZone tz = null;
	private ObjectFilter objFilter = new ObjectFilter();
	private WithHash with;
	private Vector filter;

	public ListScheduledEvent()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		with = null;
	}

	public ListScheduledEvent(WithHash w)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		with = w;
	}

	public void go (SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (with == null)
			plaingo(sysEnv);
		else
			scheduledGo(sysEnv);
	}

	public void plaingo (SystemEnvironment sysEnv)
	throws SDMSException
	{
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
		desc.add ("PRIVS");

		desc.add ("BACKLOG_HANDLING");

		desc.add ("SUSPEND_LIMIT");

		desc.add ("EFFECTIVE_SUSPEND_LIMIT");

		desc.add ("CALENDAR");

		desc.add ("CALENDAR_HORIZON");

		desc.add ("EFFECTIVE_CALENDAR_HORIZON");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Scheduled Events", desc);

		final Iterator scevIt = SDMSScheduledEventTable.table.iterator (sysEnv);
		while (scevIt.hasNext()) {
			final SDMSScheduledEvent scev = (SDMSScheduledEvent) scevIt.next();

			final Vector row = new Vector();

			row.add (scev.getId (sysEnv));
			final Long ownerId = scev.getOwnerId (sysEnv);
			final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
			row.add (g.getName (sysEnv));

			final Long sceId = scev.getSceId (sysEnv);
			final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, sceId);
			row.add (sce.pathVector (sysEnv));

			final Long evtId = scev.getEvtId (sysEnv);
			final SDMSEvent evt = SDMSEventTable.getObject (sysEnv, evtId);
			row.add (evt.getName (sysEnv));

			row.add (scev.getIsActive (sysEnv));

			row.add (Boolean.valueOf (scev.isReallyActive (sysEnv)));

			row.add (scev.getIsBroken (sysEnv));

			final String errorCode = scev.getErrorCode (sysEnv);
			if (errorCode == null)
				row.add (empty);
			else
				row.add (errorCode);

			final String errorMsg = scev.getErrorMsg (sysEnv);
			if (errorMsg == null)
				row.add (empty);
			else
				row.add (errorMsg);

			final Long lastStartTime = scev.getLastStartTime (sysEnv);
			if (lastStartTime == null)
				row.add (empty);
			else
				row.add (new DateTime (lastStartTime).toString());

			final Long nextActivityTime = scev.getNextActivityTime (sysEnv);
			if (nextActivityTime == null) {
				row.add (empty);
				row.add (empty);
			} else {
				final boolean nextActivityIsTrigger = scev.getNextActivityIsTrigger (sysEnv).booleanValue();

				if (nextActivityIsTrigger) {
					row.add (new DateTime (nextActivityTime).toString());
					row.add (empty);
				} else {
					row.add (empty);
					row.add (new DateTime (nextActivityTime).toString());
				}
			}
			row.add (scev.getPrivileges(sysEnv).toString());

			row.add (scev.getBacklogHandlingAsString (sysEnv));

			final TimerUnit suspendLimit = new TimerUnit (scev.getSuspendLimitMultiplier (sysEnv), scev.getSuspendLimit (sysEnv));
			if (suspendLimit.isINF())
				row.add (empty);
			else
				row.add (suspendLimit.asString());
			row.add (scev.getEffectiveSuspendLimit(sysEnv).asString());

			Boolean isCalendar = scev.getIsCalendar(sysEnv);
			row.add (scev.getIsCalendarAsString(sysEnv));
			if (isCalendar.booleanValue()) {
				Integer horizon = scev.getCalendarHorizon(sysEnv);
				row.add (horizon == null ? empty : horizon);
				row.add (scev.getEffectiveCalendarHorizon(sysEnv));
			} else {
				row.add (empty);
				row.add (empty);
			}

			table.addData (sysEnv, row);
		}

		Collections.sort (table.dataset, table.getComparator (sysEnv, 2, 3));

		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04207261919", "$1 Scheduled Event(s) found", Integer.valueOf (table.lines)));
	}

	public void scheduledGo (SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector desc = new Vector();
		desc.add("ID");
		desc.add("SE_NAME");
		desc.add("SE_TYPE");
		desc.add("SE_ID");
		desc.add("SE_OWNER");
		desc.add("SE_PRIVS");
		desc.add("SCE_NAME");
		desc.add("SCE_ACTIVE");
		desc.add("EVT_NAME");
		desc.add("STARTTIME");
		desc.add("EXPECTED_FINAL_TIME");
		desc.add("TIME_ZONE");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of scheduled submits", desc);

		if (with.containsKey(ParseStr.S_STARTTIME)) {
			starttime = (DateTime) with.get(ParseStr.S_STARTTIME);
			starttime.suppressSeconds();
			starttime.fixToMinDate();
		} else {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03209051439", "Syntax Error: Start time is missing"));
		}
		if (with.containsKey(ParseStr.S_ENDTIME)) {
			endtime = (DateTime) with.get(ParseStr.S_ENDTIME);
			starttime.suppressSeconds();
			endtime.fixToMinDate();
		} else {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03209051440", "Syntax Error: End time is missing"));
		}
		if (with.containsKey(ParseStr.S_TIME)) {
			tz = TimeZone.getTimeZone((String) with.get(ParseStr.S_TIME));
		} else {
			tz = TimeZone.getDefault();
		}
		if (with.containsKey(ParseStr.S_FILTER)) {
			filter = objFilter.initialize_filter(sysEnv, (Vector) with.get(ParseStr.S_FILTER), 0, true );
		} else {
			filter = new Vector();
		}

		TimerDate finalDate = new TimerDate(endtime.getTimeInMillis()/(60*1000));
		System.out.println("TimerDate finalDate = " + finalDate.toString());

		final Iterator scevIt = SDMSScheduledEventTable.table.iterator (sysEnv);
		while (scevIt.hasNext()) {
			final SDMSScheduledEvent scev = (SDMSScheduledEvent) scevIt.next();
			final Long scevId = scev.getId(sysEnv);

			final Long sceId = scev.getSceId (sysEnv);
			final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, sceId);
			final PathVector sceName = sce.pathVector(sysEnv);
			final Boolean sceActive = sce.getIsActive(sysEnv);
			final TimeZone sceTz = TimeZone.getTimeZone(sce.getTimeZone(sysEnv));

			final Long evtId = scev.getEvtId (sysEnv);
			final SDMSEvent evt = SDMSEventTable.getObject (sysEnv, evtId);
			final String evtName = evt.getName(sysEnv);

			final Long seId = evt.getSeId(sysEnv);
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId);
			if (!objFilter.doFilter(sysEnv, se, filter)) {
				continue;
			}
			final PathVector seName = se.pathVector(sysEnv);
			final String seType = se.getTypeAsString(sysEnv);
			final Long seOwnerId = se.getOwnerId (sysEnv);
			final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, seOwnerId);
			final String groupName = g.getName(sysEnv);
			final String sePrivs = se.getPrivileges(sysEnv).toString();
			Integer expFinalTime = se.getExpectedFinaltime(sysEnv);
			String finalTime = expFinalTime == null ? "" : expFinalTime.toString();
			GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
			TimeZone sysTz = SystemEnvironment.systemTimeZone;
			String tzId = sceTz.getID();

			TimerDate trigDate = new TimerDate (starttime.getTimeInMillis()/(60*1000));
			TimerDate baseDate = new TimerDate(trigDate);
			int nrEntries = 0;
			int maxEntries = sysEnv.maxNumCalEntries;
			do {
				final Vector row = new Vector();
				trigDate = sce.getNextTriggerDate (sysEnv, baseDate);
				if (trigDate == null) break;
				if (trigDate.isNaD()) break;
				gc.setTimeZone(sysTz);
				gc.setTimeInMillis(trigDate.getTime());
				gc.setTimeZone(sceTz);
				TimerDate tmpDate = new TimerDate(gc.getTimeInMillis()/(60*1000));

				if (trigDate.lt(finalDate)) {
					row.add (scevId);
					row.add (seName);
					row.add (seType);
					row.add (seId);
					row.add (groupName);
					row.add (sePrivs);
					row.add (sceName);
					row.add (sceActive);
					row.add (evtName);
					row.add (tmpDate.toString(sceTz));
					row.add (finalTime);
					row.add (tzId);
					++nrEntries;
					table.addData (sysEnv, row);
				}
				baseDate.set (trigDate.plus (1));
			} while(baseDate.le(finalDate) && (nrEntries < maxEntries));
		}
		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04207261919", "$1 Scheduled Event(s) found", Integer.valueOf (table.lines)));
	}
}
