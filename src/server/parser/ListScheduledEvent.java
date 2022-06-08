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

public class ListScheduledEvent
	extends Node
{
	public static final String __version = "@(#) $Id: ListScheduledEvent.java,v 2.11.8.2 2013/06/18 09:49:34 ronald Exp $";

	private static final String empty = "";

	public ListScheduledEvent()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go (SystemEnvironment sysEnv)
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
}
