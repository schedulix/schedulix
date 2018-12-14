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

	private Long ivalId;
	private SDMSInterval ival;

	private CreateInterval embeddedInterval;

	private boolean secondsIgnore = false;

	public CreateInterval (ObjectURL o, WithHash w, Boolean r)
	{
		super();
		obj = o;
		with = w;
		replace = r.booleanValue();
		embeddedInterval = null;
	}

	public CreateInterval (String n, WithHash w )
	{
		super();
		obj = new ObjectURL(Parser.INTERVAL, n);
		with = w;
		replace = true;
		embeddedInterval = null;
	}

	public Long getIvalId()
	{
		return ivalId;
	}
	public SDMSInterval getIval()
	{
		return ival;
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();

		result.append(obj.toString());
		result.append("\n");
		result.append(with.toString());

		return result.toString();
	}

	public void go (SystemEnvironment sysEnv)
		throws SDMSException
	{
		go (sysEnv, 0);
	}

	protected void go (SystemEnvironment sysEnv, int recursionLevel)
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
				Object embeddedRef = with.get (ParseStr.S_EMBEDDED);
				if (embeddedRef != null) {
					if (embeddedRef instanceof String) {
						final String embeddedName = (String) embeddedRef;
						if (embeddedName != null) {
							if (embeddedName.equals (obj.name))
								throw new CommonErrorException (new SDMSMessage (sysEnv, "04207191734", "interval cannot embed itself"));

							final SDMSInterval embeddedIval = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey (IntervalUtil.mapIdName (embeddedName, obj.seId), null));
							embeddedIntervalId = embeddedIval.getId (sysEnv);

							if (duration != null)
								throw new CommonErrorException (new SDMSMessage (sysEnv, "04311251854", "intervals with " + ParseStr.S_EMBEDDED + " cannot have " + ParseStr.S_DURATION));
						}
					} else {
						embeddedInterval = (CreateInterval) embeddedRef;
					}
				}
			}

			if (with.containsKey (ParseStr.S_DELAY)) {
				warning = "DELAY is not longer supported";
				with.remove (ParseStr.S_DELAY);
			}

			if (with.containsKey (ParseStr.S_INVERSE))
				isInverse = (Boolean) with.get (ParseStr.S_INVERSE);

			if (with.containsKey (ParseStr.S_MERGE)) {
				warning = "MERGE is not longer supported";
				with.remove(ParseStr.S_MERGE);
			}

			if (with.containsKey(ParseStr.S_DISPATCH) && with.get(ParseStr.S_DISPATCH) != null) {
				int maxcnt = 1;
				if (with.containsKey(ParseStr.S_STARTTIME)) maxcnt++;
				if (with.containsKey(ParseStr.S_ENDTIME)) maxcnt++;
				if (with.containsKey(ParseStr.S_GROUP)) maxcnt++;

				if (with.containsKey(ParseStr.S_FILTER)) {
					Object o = with.get(ParseStr.S_FILTER);
					if (o == null) maxcnt++;
				}
				if (with.containsKey(ParseStr.S_EMBEDDED)) {
					Object o = with.get(ParseStr.S_EMBEDDED);
					if (o == null) maxcnt++;
				}
				if (with.containsKey(ParseStr.S_SELECTION)) {
					Object o = with.get(ParseStr.S_SELECTION);
					if (o == null) maxcnt++;
				}
				if (with.containsKey(ParseStr.S_BASE)) {
					Object o = with.get(ParseStr.S_BASE);
					if (o == null) maxcnt++;
				}
				if (with.containsKey(ParseStr.S_DURATION)) {
					Object o = with.get(ParseStr.S_DURATION);
					if (o == null) maxcnt++;
				}
				if (with.size() > maxcnt) {
					StringBuffer sb = new StringBuffer();
					Iterator it = with.keySet().iterator();
					String sep = "";
					while (it.hasNext()) {
						sb.append(sep);
						sb.append(it.next().toString());
						sep = ",";
					}
					throw new CommonErrorException (new SDMSMessage (sysEnv, "03808211116",
					                                "Dispatch interval definitions are not allowed to specify other attributes than STARTTIME, ENDTIME and GROUP; found " + sb.toString()));
				}
			}

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

		try {
			Long tmp_objId = null;
			if (recursionLevel != 0)
				tmp_objId = new Long(recursionLevel);
			ival = SDMSIntervalTable.table.create (sysEnv,
			                                       obj.mappedName, gId, startTime, endTime, delay, baseInterval, baseIntervalMultiplier,
			                                       duration, durationMultiplier, syncTime, isInverse, isMerge, embeddedIntervalId, obj.seId,
			                                       tmp_objId,  null);
			ivalId = ival.getId (sysEnv);
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

		if (embeddedInterval != null) {
			embeddedInterval.setEnv(env);
			embeddedInterval.go(sysEnv, recursionLevel + 1);
			ival.setEmbeddedIntervalId(sysEnv, embeddedInterval.getIvalId());
			SDMSInterval embIval = embeddedInterval.getIval();
			embIval.setObjId(sysEnv, ivalId);
			embIval.setObjType(sysEnv, new Integer(SDMSInterval.INTERVAL));
		}

		if (with != null) {

			if (with.containsKey (ParseStr.S_SELECTION)) {
				switch (IntervalUtil.createSelections (sysEnv, ivalId, with)) {
				case IntervalUtil.IGNORED_SECONDS:
					secondsIgnore = true;
					break;
				case IntervalUtil.IGNORED_UPPER_RANGE:
					ignoreUpperRange = true;
						break;
				}
			}

			if (with.containsKey (ParseStr.S_FILTER)) {
				duplicateFilterIgnore = IntervalUtil.createFilter (sysEnv, ivalId, with, obj.seId, recursionLevel + 1);
				}

			if (with.containsKey (ParseStr.S_DISPATCH)) {
				IntervalUtil.createDispatcher (sysEnv, ivalId, with, recursionLevel + 1);
			}
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
