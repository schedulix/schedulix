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
import de.independit.scheduler.server.timer.TimerDate;
import de.independit.scheduler.server.util.*;

public class AlterInterval
	extends Node
{
	private static final Long ZERO = new Long(0);

	private final ObjectURL obj;
	private final WithHash with;
	private final boolean noerr;

	private Long ivalId;

	public AlterInterval (ObjectURL o, WithHash w, Boolean ne)
	{
		super();
		obj = o;
		with = w;
		noerr = ne.booleanValue();
	}

	private void checkIntegrity(SystemEnvironment sysEnv, SDMSInterval checkIval)
	throws SDMSException
	{
		String msg = null;
		Long checkId = checkIval.getId(sysEnv);
		if (SDMSIntervalDispatcherTable.idx_intId.containsKey (sysEnv, checkId)) {
			if (checkIval.getBaseIntervalMultiplier(sysEnv) != null) {
				msg = "Dispatcher can't have a base";
			} else if (checkIval.getDurationMultiplier(sysEnv) != null) {
				msg = "Dispatcher can't have a duration";
			} else if (checkIval.getEmbeddedIntervalId(sysEnv) != null) {
				msg = "Dispatcher can't have an embedded interval";
			} else if (SDMSIntervalSelectionTable.idx_intId.containsKey (sysEnv, checkId)) {
				msg = "Dispatcher can't have a selection";
			} else if (SDMSIntervalHierarchyTable.idx_parentId.containsKey (sysEnv, checkId)) {
				msg = "Dispatcher can't have a filter";
			}
		}
		if (msg != null)
			throw new CommonErrorException (new SDMSMessage (sysEnv, "03812101356", "Inconsistency in definition: " + msg));
	}

	private void checkStructure (final SystemEnvironment sysEnv, SDMSInterval checkIval, HashSet checkSet)
	throws SDMSException
	{
		Long checkId = checkIval.getId(sysEnv);
		if (checkSet.contains(checkId)) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04209121544", "cyclic references not allowed"));
		}
		checkSet.add (checkId);

		final Long embeddedIntervalId = checkIval.getEmbeddedIntervalId (sysEnv);

		if (embeddedIntervalId != null) {
			SDMSInterval embeddedIval = SDMSIntervalTable.getObject(sysEnv, embeddedIntervalId);
			checkStructure (sysEnv, embeddedIval, checkSet);
		}

		final Vector ihList = SDMSIntervalHierarchyTable.idx_parentId.getVector (sysEnv, checkId);
		final Iterator ihIt = ihList.iterator();
		while (ihIt.hasNext()) {
			final SDMSIntervalHierarchy ih = (SDMSIntervalHierarchy) ihIt.next();
			final Long childId = ih.getChildId (sysEnv);
			SDMSInterval childIval = SDMSIntervalTable.getObject(sysEnv, childId);
			checkStructure (sysEnv, childIval, checkSet);
		}

		final Vector dpList = SDMSIntervalDispatcherTable.idx_intId.getVector (sysEnv, checkId);
		final Iterator dpIt = dpList.iterator();
		while (dpIt.hasNext()) {
			final SDMSIntervalDispatcher dp = (SDMSIntervalDispatcher) dpIt.next();

			if (!dp.getIsActive(sysEnv).booleanValue()) continue;
			Long selIvId = dp.getSelectIntId(sysEnv);
			if (selIvId != null) {
				SDMSInterval selectIval = SDMSIntervalTable.getObject(sysEnv, selIvId);
				checkStructure (sysEnv, selectIval, checkSet);
			}
			Long filterIvId = dp.getFilterIntId(sysEnv);
			if (filterIvId != null) {
				SDMSInterval filterIval = SDMSIntervalTable.getObject(sysEnv, filterIvId);
				checkStructure (sysEnv, filterIval, checkSet);
			}
		}

		checkSet.remove(checkId);
	}

	public void go (SystemEnvironment sysEnv)
		throws SDMSException
	{
		final DateTime dt = new DateTime (new Date());
		boolean secondsIgnore = false;
		boolean duplicateFilterIgnore = false;
		boolean ignoreUpperRange = false;
		String warning = null;

		final SDMSInterval ival = (SDMSInterval) obj.resolve(sysEnv);
		ivalId = obj.objId;

		if (with.containsKey (ParseStr.S_BASE)) {
			final WithHash baseIntervalWith = (WithHash) with.get (ParseStr.S_BASE);
			if (baseIntervalWith == null) {
				ival.setBaseInterval           (sysEnv, null);
				ival.setBaseIntervalMultiplier (sysEnv, null);
			} else {
				final Integer baseInterval           = (Integer) baseIntervalWith.get (ParseStr.S_INTERVAL);
				final Integer baseIntervalMultiplier = IntervalUtil.getMultiplier (sysEnv, baseIntervalWith);

				ival.setBaseInterval           (sysEnv, baseInterval);
				ival.setBaseIntervalMultiplier (sysEnv, baseIntervalMultiplier);
			}
		}

		if (with.containsKey (ParseStr.S_DURATION)) {
			final WithHash durationWith = (WithHash) with.get (ParseStr.S_DURATION);
			if (durationWith == null) {
				ival.setDuration           (sysEnv, null);
				ival.setDurationMultiplier (sysEnv, null);
			} else {
				final Integer duration           = (Integer) durationWith.get (ParseStr.S_INTERVAL);
				final Integer durationMultiplier = IntervalUtil.getMultiplier (sysEnv, durationWith);

				ival.setDuration           (sysEnv, duration);
				ival.setDurationMultiplier (sysEnv, durationMultiplier);
			}
		}

		if (with.containsKey (ParseStr.S_SYNCTIME)) {
			if (IntervalUtil.getDateTime (sysEnv, dt, with, ParseStr.S_SYNCTIME))
				secondsIgnore = true;
			dt.fixToMinDate();
			dt.suppressSeconds();
			ival.setSyncTime (sysEnv, dt.toLong());
		}

		if (with.containsKey (ParseStr.S_STARTTIME)) {
			if (with.get (ParseStr.S_STARTTIME) == null) {
				ival.setStartTime(sysEnv, null);
			} else {
				if (IntervalUtil.getDateTime (sysEnv, dt, with, ParseStr.S_STARTTIME))
					secondsIgnore = true;
				dt.fixToMinDate();
				dt.suppressSeconds();
				ival.setStartTime (sysEnv, dt.toLong());
			}
		}

		if (with.containsKey (ParseStr.S_ENDTIME)) {
			if (with.get (ParseStr.S_ENDTIME) == null) {
				ival.setEndTime(sysEnv, null);
			} else {
				if (IntervalUtil.getDateTime (sysEnv, dt, with, ParseStr.S_ENDTIME))
					secondsIgnore = true;
				dt.fixToMaxDate();
				dt.suppressSeconds();
				ival.setEndTime (sysEnv, dt.toLong());
			}
		}

		final Long startTime = ival.getStartTime (sysEnv);
		final Long endTime = ival.getEndTime (sysEnv);
		if ((startTime != null) && (endTime != null)) {
			final TimerDate startDate = new TimerDate (new DateTime (startTime, false).toDate());
			final TimerDate endDate   = new TimerDate (new DateTime (endTime, false).toDate());

			if (endDate.eq (startDate))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04210011321", ParseStr.S_STARTTIME + " and " + ParseStr.S_ENDTIME + " must be different"));
		}

		if (with.containsKey (ParseStr.S_EMBEDDED)) {
			Long embIvalId = ival.getEmbeddedIntervalId(sysEnv);
			if (embIvalId != null) {
				SDMSInterval embIval = SDMSIntervalTable.getObject(sysEnv, embIvalId);
				Long parentObjId = embIval.getObjId(sysEnv);
				System.out.println("embIval = " + embIvalId + ", parentObjId = " + parentObjId + ", ivalId = " + ivalId);
				if (ivalId.equals(parentObjId)) {
					embIval.delete(sysEnv);
				}
				ival.setEmbeddedIntervalId (sysEnv, null);

			}
			final Object embeddedName = with.get (ParseStr.S_EMBEDDED);
			if (embeddedName != null) {
				if (embeddedName instanceof String) {
					final SDMSInterval embeddedIval = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(IntervalUtil.mapIdName ((String) embeddedName, obj.seId), null));
					final Long embeddedIvalId = embeddedIval.getId (sysEnv);

					ival.setEmbeddedIntervalId (sysEnv, embeddedIvalId);
				} else {
					CreateInterval ci = (CreateInterval) embeddedName;
					ci.setEnv(sysEnv.cEnv);
					ci.go(sysEnv, 1);
					SDMSInterval iv = ci.getIval();
					iv.setObjId(sysEnv, ivalId);
					iv.setObjType(sysEnv, new Integer(SDMSInterval.INTERVAL));
				}
			}
		}

		if ((ival.getEmbeddedIntervalId (sysEnv) != null) && (ival.getDuration (sysEnv) != null))
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04311251855", "intervals with " + ParseStr.S_EMBEDDED + " cannot have " + ParseStr.S_DURATION));

		if (with.containsKey (ParseStr.S_DELAY)) {
			warning = "DELAY not longer supported; option ignored";
		}

		if (with.containsKey (ParseStr.S_INVERSE)) {
			final Boolean isInverse = (Boolean) with.get (ParseStr.S_INVERSE);
			ival.setIsInverse (sysEnv, isInverse);
		}

		if (with.containsKey (ParseStr.S_MERGE)) {
			warning = "MERGE not longer supported; option ignored";
		}

		if (with.containsKey (ParseStr.S_SELECTION)) {
			IntervalUtil.killSelections (sysEnv, ivalId);
			if (with.get (ParseStr.S_SELECTION) != null) {
				IntervalUtil.killDispatcher (sysEnv, ivalId);
			}
			switch (IntervalUtil.createSelections (sysEnv, ivalId, with)) {
				case IntervalUtil.IGNORED_SECONDS:
					secondsIgnore = true;
					break;
				case IntervalUtil.IGNORED_UPPER_RANGE:
					ignoreUpperRange = true;
			}
		}

		if (with.containsKey (ParseStr.S_FILTER)) {
			IntervalUtil.killFilter (sysEnv, ivalId);
			if (with.get (ParseStr.S_FILTER) != null) {
				IntervalUtil.killDispatcher (sysEnv, ivalId);
			}
			duplicateFilterIgnore = IntervalUtil.createFilter (sysEnv, ivalId, with, obj.seId, 0);
		}

		if (with.containsKey (ParseStr.S_DISPATCH)) {
			IntervalUtil.killDispatcher (sysEnv, ivalId);
			if ((with.get (ParseStr.S_FILTER) != null || with.get (ParseStr.S_SELECTION) != null) && with.get (ParseStr.S_DISPATCH) != null) {
				throw new CommonErrorException (new SDMSMessage (sysEnv, "03812111214", "An interval can't be both a filter/selection and a dispatcher"));
			} else {
				if (with.get (ParseStr.S_DISPATCH) != null) {
					IntervalUtil.killSelections (sysEnv, ivalId);
					IntervalUtil.killFilter (sysEnv, ivalId);
					IntervalUtil.createDispatcher (sysEnv, ivalId, with, 0);
				}
			}
		}

		ival.setSeId (sysEnv, obj.seId);

		checkIntegrity (sysEnv, ival);
		checkStructure (sysEnv, ival, new HashSet());

		SystemEnvironment.timer.notifyChange (sysEnv, ival, TimerThread.ALTER);

		if(with.containsKey(ParseStr.S_GROUP)) {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(gName, ZERO)).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
			ival.setOwnerId(sysEnv, gId);
		}

		if (duplicateFilterIgnore)
			result.setFeedback (new SDMSMessage (sysEnv, "04209270127", "Interval created (WARNING: duplicate " + ParseStr.S_FILTER + " will be ignored)"));
		else if (secondsIgnore)
			result.setFeedback (new SDMSMessage (sysEnv, "04209201344", "Interval altered (WARNING: seconds will be ignored)"));
		else if (ignoreUpperRange)
			result.setFeedback (new SDMSMessage (sysEnv, "04209270144", "Interval created (WARNING: upper range(s) will be ignored)"));
		else
			result.setFeedback (new SDMSMessage (sysEnv, "04207241731", "Interval altered" + (warning == null ? "" : ". " + warning)));
	}
}
