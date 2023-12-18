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
package de.independit.scheduler.server;

import java.util.*;

import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.timer.*;
import de.independit.scheduler.server.util.*;

class TimeSchedule
	extends Node
{
	static final int SCHEDULE = 0;
	static final int INITIALIZE = 1;

	int action;

	public TimeSchedule()
	{
		super();
		action = SCHEDULE;
		auditFlag = false;
	}

	public TimeSchedule (final int a)
	{
		super();
		action = a;
		auditFlag = false;
	}

	public String getName()
	{
		return SystemEnvironment.timer.getInfo();
	}

	public void go (final SystemEnvironment sysEnv)
		throws SDMSException
	{
		switch (action) {
		case SCHEDULE:
			SystemEnvironment.timer.schedule (sysEnv);
			break;

		case INITIALIZE:
			SystemEnvironment.timer.initialize (sysEnv);
			break;
		}
	}
}

public class TimerThread
	extends InternalSession
{
	public static final String __version = "@(#) $Id: TimerThread.java,v 2.29.2.2 2013/03/20 11:27:15 ronald Exp $";
	public static final String name = "TimerThread";

	public static final int CREATE = 1;
	public static final int ALTER  = 2;
	public static final int DROP   = 3;

	private static final GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
	private static final String LAST_SCHEDULE_RUN = "LAST_SCHEDULE_RUN";

	private final TimerDate now = new TimerDate();
	private final TimerDate lastRun = new TimerDate();
	private static final long warnLimitInMilis = 1000;

	private boolean aliveSinceLong = false;

	private Long nowLong;
	private final TimerDate suspendNow = new TimerDate();

	private int current = 0;
	private int total = 0;

	public TimerThread (final SystemEnvironment sysEnv, final SyncFifo f)
	{
		super (name);
		NR = 19630127;
		initThread(sysEnv, f, NR, name, SystemEnvironment.timerWakeupInterval);
	}

	protected Node getNode(int m)
	{
		if(m == INITIALIZE)	return new TimeSchedule(TimeSchedule.INITIALIZE);
		return new TimeSchedule();
	}

	private final void loadNow()
	{
		synchronized (gc) {
			gc.setTimeInMillis (System.currentTimeMillis());
			gc.set (Calendar.SECOND, 0);
			gc.set (Calendar.MILLISECOND, 0);

			now.set (TimerDate.fromMillis (gc.getTimeInMillis()));
		}
	}

	public static final Long dateToDateTimeLong (final TimerDate date)
	{
		return date.isNaD() ? null : new DateTime (date).toLong();
	}

	private final void loadLastRun (final SystemEnvironment sysEnv)
		throws SDMSException
	{
		try {
			final SDMSPersistentValue persVal = SDMSPersistentValueTable.idx_name_getUnique (sysEnv, LAST_SCHEDULE_RUN);
			final Integer lastTime = persVal.getIntValue (sysEnv);
			lastRun.set (lastTime.intValue());
			if (lastTime.intValue() > (SystemEnvironment.startTime / (1000 * 60))) {
				aliveSinceLong = true;
			} else {
				aliveSinceLong = false;
			}
		} catch (final NotFoundException e) {
			SDMSPersistentValueTable.table.create (sysEnv, LAST_SCHEDULE_RUN, Integer.valueOf (now.toMinutes()));
			lastRun.set (now);
			aliveSinceLong = false;
		}
	}

	private final void setLastRunToNow (final SystemEnvironment sysEnv)
		throws SDMSException
	{
		final SDMSPersistentValue persVal = SDMSPersistentValueTable.idx_name_getUniqueForUpdate(sysEnv, LAST_SCHEDULE_RUN);
		persVal.setIntValue (sysEnv, Integer.valueOf (now.toMinutes()));
		lastRun.set (now);
	}

	private final String toString (final Exception e)
	{
		final StackTraceElement[] st = e.getStackTrace();

		String stx = e.toString();
		for (int i = 0; i < st.length; ++i)
			stx += "\n" + st [i];

		return stx;
	}

	private final void doSubmit (final SystemEnvironment sysEnv, final SDMSEvent evt, final Long ownerId, final TimerDate triggerDate, final TimerDate suspendNow, String timeZone)
		throws SDMSException
	{

		final Long evtId = evt.getId (sysEnv);

		final Long seId = evt.getSeId (sysEnv);
		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId);

		final Vector parmList = new Vector();
		final Vector epList = SDMSEventParameterTable.idx_evtId.getVector (sysEnv, evtId);
		final int epSize = epList.size();
		for (int i = 0; i < epSize; ++i) {
			final SDMSEventParameter ep = (SDMSEventParameter) epList.get (i);

			final String key = ep.getKey (sysEnv);
			final String value = ep.getValue (sysEnv).substring (1);

			final WithItem wi = new WithItem (key, value);
			parmList.add (wi);
		}

		final boolean forceSuspend = triggerDate.lt (suspendNow);

		final boolean submitSuspended = se.getSubmitSuspended (sysEnv).booleanValue();
		final Boolean doSuspend = (forceSuspend || submitSuspended) ? Boolean.TRUE : Boolean.FALSE;

		final SDMSSubmittedEntity sme = se.submitMaster (sysEnv, parmList, (doSuspend.booleanValue() ? SDMSConstants.SME_SUSPEND : SDMSConstants.SME_NOSUSPEND),
								null,
								ownerId, null, "Event " + evt.getName (sysEnv), timeZone);

		if (forceSuspend) {

			final Long smeId = sme.getId (sysEnv);

		}

	}

	private final void doSchedule (final SystemEnvironment sysEnv, final SDMSScheduledEvent scev)
		throws SDMSException
	{
		doSchedule(sysEnv, scev, true);
	}

	private final void doSchedule (final SystemEnvironment sysEnv, final SDMSScheduledEvent scev, boolean submit)
		throws SDMSException
	{

		long startTimeInMillis = new Date().getTime();

		final Long sceId = scev.getSceId (sysEnv);
		final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, sceId);

		final Long evtId = scev.getEvtId (sysEnv);
		final SDMSEvent evt = SDMSEventTable.getObject (sysEnv, evtId);

		final Long ownerId = scev.getOwnerId (sysEnv);

		final boolean isActive = scev.isReallyActive (sysEnv);

		final int backlogHandling = scev.getBacklogHandling (sysEnv).intValue();

		final TimerDate baseDate;
		TimerDate trigDate = null;

		final Long nextActivityTime = scev.getNextActivityTime (sysEnv);
		final TimerDate nextActivityDate = nextActivityTime == null ? null : new TimerDate (new DateTime (nextActivityTime).toDate());

		if (! isActive) {
			if ((nextActivityDate != null)
			    && nextActivityDate.gt (now))
				return;

			baseDate = new TimerDate (now.plus (1));
			trigDate = sce.getNextTriggerDate (sysEnv, baseDate);
		}

		else {
			if (nextActivityDate == null)
				if (backlogHandling != SDMSScheduledEvent.NONE && submit)
					baseDate = new TimerDate (lastRun.plus (1));
				else
					baseDate = new TimerDate (now.plus (1));

			else {
				if (nextActivityDate.gt (now))
					return;

				if (scev.getNextActivityIsTrigger (sysEnv).booleanValue())
					if ((backlogHandling == SDMSScheduledEvent.NONE)
					    && nextActivityDate.lt (now) && !aliveSinceLong)
						baseDate = new TimerDate (now);
					else {
						baseDate = nextActivityDate;
						trigDate = nextActivityDate;
					}

				else if (backlogHandling != SDMSScheduledEvent.NONE)
					baseDate = nextActivityDate;
				else
					baseDate = now;
			}

			if (trigDate == null)
				trigDate = sce.getNextTriggerDate (sysEnv, baseDate);

			if (submit) {
				TimerDate lastDate = null;
				while ((! trigDate.isNaD()) && trigDate.lt (now)) {
					if (backlogHandling == SDMSScheduledEvent.ALL)
						submit_and_set (sysEnv, scev, evt, ownerId, trigDate, suspendNow);

					lastDate = trigDate;

					baseDate.set (trigDate.plus (1));
					trigDate = sce.getNextTriggerDate (sysEnv, baseDate);
				}

				if ((backlogHandling == SDMSScheduledEvent.LAST)
				    && (lastDate != null))
					submit_and_set (sysEnv, scev, evt, ownerId, lastDate, suspendNow);
				if (   backlogHandling == SDMSScheduledEvent.NONE
				    && aliveSinceLong
				    && lastDate != null
				    && ! trigDate.isNaD() && !trigDate.eq (now))
					submit_and_set (sysEnv, scev, evt, ownerId, lastDate, suspendNow);

				if ((! trigDate.isNaD()) && trigDate.eq (now)) {
					submit_and_set (sysEnv, scev, evt, ownerId, trigDate, suspendNow);

					baseDate.set (trigDate.plus (1));
					trigDate = sce.getNextTriggerDate (sysEnv, baseDate);
				}
			}
		}

		if (trigDate.isNaD()) {
			scev.setNextActivityTime      (sysEnv, dateToDateTimeLong (new TimerDate (baseDate.plus (SystemEnvironment.timerRecalc))));
			scev.setNextActivityIsTrigger (sysEnv, Boolean.FALSE);
			scev.clearCalendar(sysEnv);
		}

		else {
			scev.setNextActivityTime      (sysEnv, dateToDateTimeLong (trigDate));
			scev.setNextActivityIsTrigger (sysEnv, Boolean.TRUE);
			scev.updateCalendar(sysEnv, new TimerDate(trigDate.plus(-1)), sce);
		}

		long endTimeInMillis = new Date().getTime();
		if (endTimeInMillis - startTimeInMillis > warnLimitInMilis) {
			doTrace(cEnv, "doSchedule on scheduled event  " + scev.getId(sysEnv).toString() + " took " + (endTimeInMillis - startTimeInMillis)  + " milliseconds", SEVERITY_WARNING);
		}
	}

	private final void submit_and_set (final SystemEnvironment sysEnv, final SDMSScheduledEvent scev, final SDMSEvent evt, final Long ownerId, final TimerDate triggerDate,
					   final TimerDate suspendNow)
		throws SDMSException
	{
		boolean setLastStartTime = true;
		try {
			SDMSSchedule sce = SDMSScheduleTable.getObject(sysEnv, scev.getSceId(sysEnv));
			doSubmit (sysEnv, evt, ownerId, triggerDate, suspendNow, sce.getTimeZone(sysEnv));
		} catch (SerializationException e) {
			setLastStartTime = false;
			throw e;
		} finally {
			if (setLastStartTime)
				scev.setLastStartTime (sysEnv, nowLong);
		}
	}

	private final void retire (final SystemEnvironment sysEnv, final SDMSScheduledEvent scev, final SDMSException e)
		throws SDMSException
	{

		final String msg = e.toString();
		doTrace (cEnv, "Retire scheduled event " + scev.getId (sysEnv) + ": " + msg, SEVERITY_INFO);

		scev.setIsActive  (sysEnv, Boolean.FALSE);
		scev.setIsBroken  (sysEnv, Boolean.TRUE);
		scev.setErrorCode (sysEnv, e.errNumber());
		scev.setErrorMsg  (sysEnv, msg);

		scev.setNextActivityTime      (sysEnv, null);
		scev.setNextActivityIsTrigger (sysEnv, null);
		scev.clearCalendar            (sysEnv);

	}

	public final String getInfo()
	{
		return "TimeScheduling (" + current + "/" + total + ")";
	}

	private final void scheduleAll (final SystemEnvironment sysEnv)
		throws SDMSException
	{

		current = 0;
		total = SDMSScheduledEventTable.table.rawSize();
		if (lastRun.lt (now)) {
			nowLong = dateToDateTimeLong (now);

			final Iterator scevIt = SDMSScheduledEventTable.table.iterator (sysEnv, false );
			while (scevIt.hasNext()) {
				current ++;
				final SDMSScheduledEvent scev = (SDMSScheduledEvent) scevIt.next();

				if (! scev.getIsBroken (sysEnv).booleanValue()) {
					suspendNow.set (now.plus (-1, scev.getEffectiveSuspendLimit(sysEnv)));

					try {
						sysEnv.tx.beginSubTransaction (sysEnv);
						doSchedule (sysEnv, scev);
						sysEnv.tx.commitSubTransaction (sysEnv);
					} catch (final SerializationException e) {
						sysEnv.tx.rollbackSubTransaction (sysEnv);
						throw e;
					} catch (final SDMSException e) {
						sysEnv.tx.rollbackSubTransaction (sysEnv);
						createError (sysEnv, scev, e.toString());
						retire (sysEnv, scev, e);
					} catch (final Exception e) {
						sysEnv.tx.rollbackSubTransaction (sysEnv);
						createError (sysEnv, scev, e.toString());
						retire (sysEnv, scev, new CommonErrorException (new SDMSMessage (sysEnv, "04311102118", toString (e))));
					}
				}
			}

			setLastRunToNow (sysEnv);
		}

	}

	private final void createError (final SystemEnvironment sysEnv, final SDMSScheduledEvent scev, final String errorMsg)
		throws SDMSException
	{
		final Long evtId = scev.getEvtId (sysEnv);
		final SDMSEvent evt = SDMSEventTable.getObject (sysEnv, evtId);
		final Long seId = evt.getSeId (sysEnv);
		final Long ownerId = scev.getOwnerId (sysEnv);
		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId);

		se.createErrorMaster (sysEnv, ownerId, "Event " + evt.getName (sysEnv), errorMsg);
	}

	final void initialize (final SystemEnvironment sysEnv)
		throws SDMSException
	{

		loadNow();
		loadLastRun (sysEnv);

	}

	public void schedule (final SystemEnvironment sysEnv)
		throws SDMSException
	{

		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		loadNow();

		doTrace (cEnv, "-----------> Start Time Scheduling <------------", SEVERITY_MESSAGE);
		scheduleAll (sysEnv);
		doTrace (cEnv, "-----------> End Time Scheduling   <------------", SEVERITY_MESSAGE);

	}

	public void notifyChange (final SystemEnvironment sysEnv, final SDMSEvent evt, final int action)
		throws SDMSException
	{

		if (action != ALTER)
			throw new FatalException (new SDMSMessage (sysEnv, "04207262214", "Unexpected action code $1 for Event $2", Integer.valueOf (action), evt.getId (sysEnv)));

	}

	private final HashSet ivalIds = new HashSet();

	public final void notifyChange (final SystemEnvironment sysEnv, final SDMSInterval ival, final int action)
		throws SDMSException
	{

		if (action != ALTER)
			throw new FatalException (new SDMSMessage (sysEnv, "04207262215", "Unexpected action code $1 for Interval $2", Integer.valueOf (action), ival.getId (sysEnv)));
		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		try {
			collectIvals (sysEnv, ival.getId (sysEnv));

			final Iterator ivalIdsIt = ivalIds.iterator();
			while (ivalIdsIt.hasNext()) {
				final Long ivalId = (Long) ivalIdsIt.next();
				final Vector sceList = SDMSScheduleTable.idx_intId.getVector (sysEnv, ivalId);

				final int sceSize = sceList.size();
				for (int i = 0; i < sceSize; ++i) {
					final SDMSSchedule sce = (SDMSSchedule) sceList.get (i);
					notifyChange (sysEnv, sce, ALTER);
				}
			}
		} finally {
			ivalIds.clear();
		}

	}

	private final void collectIvals (final SystemEnvironment sysEnv, final Long ivalId)
		throws SDMSException
	{

		ivalIds.add (ivalId);

		final Vector embList = SDMSIntervalTable.idx_embeddedIntervalId.getVector (sysEnv, ivalId);
		final int embSize = embList.size();
		for (int i = 0; i < embSize; ++i) {
			final SDMSInterval embIval = (SDMSInterval) embList.get (i);
			final Long embId = embIval.getId (sysEnv);
			if (! ivalIds.contains (embId))
				collectIvals (sysEnv, embId);
		}

		final Vector ihList = SDMSIntervalHierarchyTable.idx_childId.getVector (sysEnv, ivalId);
		final int ihSize = ihList.size();
		for (int i = 0; i < ihSize; ++i) {
			final SDMSIntervalHierarchy ih = (SDMSIntervalHierarchy) ihList.get (i);
			final Long parentId = ih.getParentId (sysEnv);
			if (! ivalIds.contains (parentId))
				collectIvals (sysEnv, parentId);
		}

		final Vector dsList = SDMSIntervalDispatcherTable.idx_selectIntId.getVector(sysEnv, ivalId);
		final int dsSize = dsList.size();
		for (int i = 0; i < dsSize; ++i) {
			final SDMSIntervalDispatcher idp = (SDMSIntervalDispatcher) dsList.get (i);
			final Long idpId = idp.getIntId(sysEnv);
			if (! ivalIds.contains (idpId))
				collectIvals (sysEnv, idpId);
		}

		final Vector dfList = SDMSIntervalDispatcherTable.idx_filterIntId.getVector(sysEnv, ivalId);
		final int dfSize = dfList.size();
		for (int i = 0; i < dfSize; ++i) {
			final SDMSIntervalDispatcher idp = (SDMSIntervalDispatcher) dfList.get (i);
			final Long idpId = idp.getIntId(sysEnv);
			if (! ivalIds.contains (idpId))
				collectIvals (sysEnv, idpId);
		}

	}

	public final void notifyChange (final SystemEnvironment sysEnv, final SDMSSchedule sce, final int action)
		throws SDMSException
	{

		final Long sceId = sce.getId (sysEnv);

		if (action != ALTER)
			throw new FatalException (new SDMSMessage (sysEnv, "04207262216", "Unexpected action code $1 for Schedule $2", Integer.valueOf (action), sceId));

		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		final Vector scevList = SDMSScheduledEventTable.idx_sceId.getVector (sysEnv, sceId);
		final int size = scevList.size();
		for (int i = 0; i < size; ++i) {
			final SDMSScheduledEvent scev = (SDMSScheduledEvent) scevList.get (i);
			notifyChange (sysEnv, scev, ALTER);
		}

	}

	public final void notifyChange (final SystemEnvironment sysEnv, final SDMSScheduledEvent scev, final int action)
		throws SDMSException
	{

		if (sysEnv.maxWriter > 1)
			LockingSystem.lock(sysEnv, this, ObjectLock.EXCLUSIVE);

		switch (action) {
			case CREATE:
				if (! scev.getIsBroken (sysEnv).booleanValue())
					try {
						loadNow();
						doSchedule (sysEnv, scev, false);
					} catch (final SDMSException se) {
						throw se;
					} catch (final Exception e) {
						SDMSMessage message = new SDMSMessage (sysEnv, "02205080754", "Unexpected Exception ($1)", toString (e));
						SDMSThread.doTrace(sysEnv.cEnv, message.toString(), SDMSThread.SEVERITY_ERROR);
						throw new CommonErrorException (message);
					}
				break;

			case ALTER:
				scev.setNextActivityTime      (sysEnv, null);
				scev.setNextActivityIsTrigger (sysEnv, null);
				scev.clearCalendar(sysEnv);
				if (! scev.getIsBroken (sysEnv).booleanValue())
					try {
						loadNow();
						doSchedule (sysEnv, scev, false);
					} catch (final SDMSException se) {
						throw se;
					} catch (final Exception e) {
						SDMSMessage message = new SDMSMessage (sysEnv, "02205080755", "Unexpected Exception ($1)", toString (e));
						SDMSThread.doTrace(sysEnv.cEnv, message.toString(), SDMSThread.SEVERITY_ERROR);
						throw new CommonErrorException (message);
					}
				break;

			case DROP:
				break;

			default:
				throw new FatalException (new SDMSMessage (sysEnv, "04207262159", "Unexpected action code $1 for Scheduled Event $2", Integer.valueOf (action), scev.getId (sysEnv)));
		}

	}
}
