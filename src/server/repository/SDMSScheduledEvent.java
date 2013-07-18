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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.timer.*;
import de.independit.scheduler.server.exception.*;

public class SDMSScheduledEvent extends SDMSScheduledEventProxyGeneric
	implements SDMSOwnedObject
{

	public final static String __version = "SDMSScheduledEvent $Revision: 2.12.4.1 $ / @(#) $Id: SDMSScheduledEvent.java,v 2.12.4.1 2013/03/14 10:25:24 ronald Exp $";

	protected SDMSScheduledEvent(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Long sceId = getSceId (sysEnv);
		final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, sceId);
		final String sceName = sce.getName (sysEnv);

		final Long evtId = getEvtId (sysEnv);
		final SDMSEvent evt = SDMSEventTable.getObject (sysEnv, evtId);
		final String evtName = evt.getName (sysEnv);

		return sceName + '.' + evtName;
	}

	public final boolean isReallyActive (final SystemEnvironment sysEnv)
	throws SDMSException
	{
		boolean reallyActive = getIsActive (sysEnv).booleanValue();

		if (reallyActive) {
			final Long sceId = getSceId (sysEnv);
			final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, sceId);
			reallyActive = sce.isReallyActive (sysEnv);
		}

		return reallyActive;
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final SDMSSchedule sce = SDMSScheduleTable.getObject(sysEnv, getSceId(sysEnv));
		final Long seId = sce.getSeId(sysEnv);
		String se = null;
		if(seId != null)
			se = SDMSSchedulingEntityTable.getObject(sysEnv, seId).pathString(sysEnv);

		return getName(sysEnv) + (se == null ? "" : " (" + se + ")");
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "scheduled event " + getURLName(sysEnv);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		clearCalendar(sysEnv);
		super.delete(sysEnv);
	}

	public void clearCalendar(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Long scevId = getId(sysEnv);
		Vector v = SDMSCalendarTable.idx_scevId.getVector(sysEnv, scevId);
		for (int i = 0; i < v.size(); ++i) {
			SDMSCalendar c = (SDMSCalendar) v.get(i);
			c.delete(sysEnv);
		}
	}

	private void deleteCalendarItemsOutOfWindow(SystemEnvironment sysEnv, Vector calVec, TimerDate nextTime, TimerDate lastTime)
	throws SDMSException
	{
		Iterator i = calVec.iterator();
		while (i.hasNext()) {
			SDMSCalendar c = (SDMSCalendar) i.next();
			Long cDate = c.getStarttime(sysEnv);
			DateTime dtcDate = new DateTime(cDate);
			TimerDate tcDate = new TimerDate(dtcDate.toDate());
			if (!tcDate.gt(nextTime) || tcDate.gt(lastTime)) {
				i.remove();
				c.delete(sysEnv);
			}
		}
	}

	public Integer getEffectiveCalendarHorizon(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Integer horizon = getCalendarHorizon(sysEnv);
		if (horizon == null) {
			return new Integer(SystemEnvironment.defCalHorizon);
		}
		return horizon;
	}

	public TimerUnit getEffectiveSuspendLimit(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final TimerUnit suspendLimit = new TimerUnit (getSuspendLimitMultiplier (sysEnv), getSuspendLimit (sysEnv));
		if (suspendLimit.isINF())
			return new TimerUnit(SystemEnvironment.timerSuspendLimit, TimerUnit.MINUTE);
		else
			return suspendLimit;
	}

	public void updateCalendar(SystemEnvironment sysEnv, Long nextTime, SDMSSchedule sce)
	throws SDMSException
	{
		TimerDate next = nextTime == null ? null : new TimerDate(new DateTime(nextTime).toDate());
		updateCalendar(sysEnv, next, sce);
	}

	public void updateCalendar(SystemEnvironment sysEnv, TimerDate nextTime, SDMSSchedule sce)
	throws SDMSException
	{
		if (!getIsCalendar(sysEnv).booleanValue()) return;
		if (!getIsActive(sysEnv).booleanValue()) return;

		TimerDate next = new TimerDate();
		if (nextTime == null) {
			final GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
			gc.setTimeInMillis (System.currentTimeMillis());
			gc.set (Calendar.SECOND, 0);
			gc.set (Calendar.MILLISECOND, 0);
			next.set(TimerDate.fromMillis(gc.getTimeInMillis()));
		} else {
			next = nextTime;
		}
		final Long scevId = getId(sysEnv);
		final Integer horizon = getEffectiveCalendarHorizon(sysEnv);
		TimerDate last = new TimerDate(next.plus(horizon.intValue() * 24 * 60));

		Vector v = SDMSCalendarTable.idx_scevId.getVector(sysEnv, scevId);

		deleteCalendarItemsOutOfWindow(sysEnv, v, next, last);
		Comparator c = SDMSCalendar.getComparator(sysEnv);
		Collections.sort(v,c);

		TimerDate finalDate = new TimerDate (last);
		if (v.size() > 0)
			last = new TimerDate(new DateTime(((SDMSCalendar) v.get(v.size() - 1)).getStarttime(sysEnv)).toDate());
		else
			last = next;
		TimerDate trigDate = new TimerDate (last);
		TimerDate baseDate = new TimerDate(trigDate);

		int nrEntries = v.size();
		int maxEntries = sysEnv.maxNumCalEntries;
		while (baseDate.le(finalDate) && nrEntries < maxEntries) {
			trigDate = sce.getNextTriggerDate (sysEnv, baseDate);
			if (trigDate == null) break;
			if (trigDate.isNaD()) break;
			if (trigDate.lt(finalDate)) {

				try {
					SDMSCalendarTable.table.create (sysEnv, scevId, TimerThread.dateToDateTimeLong(trigDate));
				} catch (DuplicateKeyException dke) {

				}
				++nrEntries;
			}
			baseDate.set (trigDate.plus (1));
		}
	}
}
