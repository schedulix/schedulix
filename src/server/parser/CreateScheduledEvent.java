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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;

public class CreateScheduledEvent
	extends Node
{
	public static final String __version = "@(#) $Id: CreateScheduledEvent.java,v 2.8.4.1 2013/03/14 10:24:27 ronald Exp $";

	private final ObjectURL obj;
	private final Boolean active;
	private final WithHash with;
	private final boolean replace;

	public CreateScheduledEvent (ObjectURL o, WithHash w, Boolean r)
	{
		super();
		obj = o;
		with = w;
		if(!with.containsKey(ParseStr.S_ACTIVE)) active = Boolean.TRUE;
		else	active = (Boolean) with.get(ParseStr.S_ACTIVE);
		replace = r.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
		throws SDMSException
	{
		try {
			obj.resolve(sysEnv);
		} catch (final NotFoundException nfe) {

		}

		final Long uId = env.uid();
		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		final Long gId;
		if(!with.containsKey(ParseStr.S_GROUP)) {
			gId = u.getDefaultGId(sysEnv);
		} else {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
			              sysEnv, new SDMSKey(gName, SDMSConstants.lZERO)).getId(sysEnv);
			if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(gId, uId)) &&
			   !SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, uId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03312162143",
						"User $1 does not belong to Group $2", u.getName(sysEnv), gName));
			}
		}

		final Integer backlogHandling = with.containsKey (ParseStr.S_BACKLOG_HANDLING) ? (Integer) with.get (ParseStr.S_BACKLOG_HANDLING) : SDMSConstants.SEV_LAST;

		Integer suspendLimit = null;
		Integer suspendLimitMultiplier = null;
		if (with.containsKey (ParseStr.S_SUSPEND_LIMIT)) {
			final WithHash wh = (WithHash) with.get (ParseStr.S_SUSPEND_LIMIT);
			if (wh != null) {
				suspendLimit = (Integer) wh.get (ParseStr.S_INTERVAL);
				suspendLimitMultiplier = IntervalUtil.getMultiplier (sysEnv, wh);
			}
		}

		Boolean isCalendar = with.containsKey (ParseStr.S_CALENDAR) ? (Boolean) with.get (ParseStr.S_CALENDAR) : Boolean.FALSE;
		Integer calendarHorizon = (Integer) with.get (ParseStr.S_HORIZON);

		SDMSScheduledEvent scev;
		try {
			scev = SDMSScheduledEventTable.table.create (sysEnv, gId, obj.sceId, obj.evtId, active,
					Boolean.FALSE,
					null,
					null,
					null,
					null,
					null,
					backlogHandling,
					suspendLimit,
					suspendLimitMultiplier,
					isCalendar,
					calendarHorizon);
		} catch (final DuplicateKeyException dke) {
			if (replace) {
				final AlterScheduledEvent ase = new AlterScheduledEvent (obj, with, Boolean.FALSE);
				ase.setEnv (env);
				ase.go (sysEnv);
				result = ase.result;
				return;
			}
			throw dke;
		}

		SystemEnvironment.timer.notifyChange (sysEnv, scev, TimerThread.CREATE);

		result.setFeedback (new SDMSMessage (sysEnv, "04207261913", "Scheduled Event created"));
	}
}
