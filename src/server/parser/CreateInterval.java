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
import de.independit.scheduler.server.util.DateTime;
import de.independit.scheduler.server.timer.TimerDate;

public class CreateInterval
	extends Node
{
	public static final String __version = "@(#) $Id: CreateInterval.java,v 2.10.2.1 2013/03/14 10:24:26 ronald Exp $";

	private final ObjectURL obj;
	private final WithHash with;
	private final boolean replace;

	private boolean secondsIgnore = false;

	public CreateInterval (ObjectURL o, WithHash w, Boolean r)
	{
		super();
		obj = o;
		with = w;
		replace = r.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long startTime = null;
		Long endTime = null;
		Long delay = null;
		Integer baseInterval = null;
		Integer baseIntervalMultiplier = null;
		Integer duration = null;
		Integer durationMultiplier = null;
		Boolean isInverse = Boolean.FALSE;
		Boolean isMerge = Boolean.FALSE;
		Long embeddedIntervalId = null;
		boolean duplicateFilterIgnore = false;
		boolean ignoreUpperRange = false;
		String warning = null;

		secondsIgnore = false;

		final DateTime dt = new DateTime (new Date());
		dt.suppressSeconds();
		Long syncTime = dt.toLong();

		try {
			obj.resolve(sysEnv);
		} catch (final NotFoundException nfe) {

		}

		if (with != null) {
			if (with.containsKey (ParseStr.S_BASE)) {
				final WithHash wh = (WithHash) with.get (ParseStr.S_BASE);
				if (wh != null) {
					baseInterval = (Integer) wh.get (ParseStr.S_INTERVAL);
					baseIntervalMultiplier = IntervalUtil.getMultiplier (sysEnv, wh);
				}
			}

			if (with.containsKey (ParseStr.S_DURATION)) {
				final WithHash wh = (WithHash) with.get (ParseStr.S_DURATION);
				if (wh != null) {
					duration = (Integer) wh.get (ParseStr.S_INTERVAL);
					durationMultiplier = IntervalUtil.getMultiplier (sysEnv, wh);
				}
			}

			if (with.containsKey (ParseStr.S_SYNCTIME)) {
				if (IntervalUtil.getDateTime (sysEnv, dt, with, ParseStr.S_SYNCTIME))
					secondsIgnore = true;
				dt.fixToMinDate();
				dt.suppressSeconds();
				syncTime = dt.toLong();
			}

			if (with.containsKey (ParseStr.S_STARTTIME) && with.get (ParseStr.S_STARTTIME) != null) {
				if (IntervalUtil.getDateTime (sysEnv, dt, with, ParseStr.S_STARTTIME))
					secondsIgnore = true;
				dt.fixToMinDate();
				dt.suppressSeconds();
				startTime = dt.toLong();
			}

			if (with.containsKey (ParseStr.S_ENDTIME) && with.get (ParseStr.S_ENDTIME) != null) {
				if (IntervalUtil.getDateTime (sysEnv, dt, with, ParseStr.S_ENDTIME))
					secondsIgnore = true;
				dt.fixToMaxDate();
				dt.suppressSeconds();
				endTime = dt.toLong();
			}

			if ((startTime != null) && (endTime != null)) {
				final TimerDate startDate = new TimerDate (new DateTime (startTime, false).toDate());
				final TimerDate endDate   = new TimerDate (new DateTime (endTime, false).toDate());

				if (endDate.eq (startDate))
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04210242337", ParseStr.S_STARTTIME + " and " + ParseStr.S_ENDTIME + " must be different"));
			}

			if (with.containsKey (ParseStr.S_EMBEDDED)) {
				final String embeddedName = (String) with.get (ParseStr.S_EMBEDDED);
				if (embeddedName != null) {
					if (embeddedName.equals (obj.name))
						throw new CommonErrorException (new SDMSMessage (sysEnv, "04207191734", "interval cannot embed itself"));

					final SDMSInterval embeddedIval = SDMSIntervalTable.idx_name_getUnique (sysEnv, IntervalUtil.mapIdName (embeddedName, obj.seId));
					embeddedIntervalId = embeddedIval.getId (sysEnv);

					if (duration != null)
						throw new CommonErrorException (new SDMSMessage (sysEnv, "04311251854", "intervals with " + ParseStr.S_EMBEDDED + " cannot have " + ParseStr.S_DURATION));
				}
			}

			if (with.containsKey (ParseStr.S_DELAY)) {
				warning = "DELAY is not longer supported";
			}

			if (with.containsKey (ParseStr.S_INVERSE))
				isInverse = (Boolean) with.get (ParseStr.S_INVERSE);

			if (with.containsKey (ParseStr.S_MERGE))
				warning = "MERGE is not longer supported";
		}

		final Long uId = env.uid();
		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		final Long gId;
		if(with == null || !with.containsKey(ParseStr.S_GROUP)) {
			gId = u.getDefaultGId(sysEnv);
		} else {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(gId, uId)) &&
			   !SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, uId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03312161745",
						"User $1 does not belong to Group $2", u.getName(sysEnv), gName));
			}
		}

		SDMSInterval ival;
		try {
			ival = SDMSIntervalTable.table.create (sysEnv,
							       obj.mappedName, gId, startTime, endTime, delay, baseInterval, baseIntervalMultiplier,
							       duration, durationMultiplier, syncTime, isInverse, isMerge, embeddedIntervalId, obj.seId);
		} catch (final DuplicateKeyException dke) {
			if (replace) {
				final AlterInterval ai = new AlterInterval (obj, with, Boolean.FALSE);
				ai.setEnv (env);
				ai.go (sysEnv);
				result = ai.result;
				return;
			}
			throw dke;
		}

		if (with != null) {
			final Long ivalId = ival.getId (sysEnv);

			if (with.containsKey (ParseStr.S_SELECTION))
				switch (IntervalUtil.createSelections (sysEnv, ivalId, with)) {
				case IntervalUtil.IGNORED_SECONDS:
					secondsIgnore = true;
					break;
				case IntervalUtil.IGNORED_UPPER_RANGE:
					ignoreUpperRange = true;
				}

			if (with.containsKey (ParseStr.S_FILTER))
				duplicateFilterIgnore = IntervalUtil.createFilter (sysEnv, ivalId, obj.seSpec, with);
		}

		if (duplicateFilterIgnore)
			result.setFeedback (new SDMSMessage (sysEnv, "04209270107", "Interval created (WARNING: duplicate " + ParseStr.S_FILTER + " will be ignored)"));
		else if (secondsIgnore)
			result.setFeedback (new SDMSMessage (sysEnv, "04209201340", "Interval created (WARNING: seconds will be ignored)"));
		else if (ignoreUpperRange)
			result.setFeedback (new SDMSMessage (sysEnv, "04209270143", "Interval created (WARNING: upper range(s) will be ignored)"));
		else
			result.setFeedback (new SDMSMessage (sysEnv, "04107191714", "Interval created" + (warning == null ? "" : ". " + warning)));
	}
}
