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

import java.util.*;
import java.util.concurrent.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.timer.*;
import de.independit.scheduler.server.util.*;

public class SDMSSchedule
	extends SDMSScheduleProxyGeneric
	implements ThreadFactory
{
	public static final String __version = "@(#) $Id: SDMSSchedule.java,v 2.11.2.3 2013/03/16 11:47:21 dieter Exp $";

	SDMSThread thread = null;

	private SDMSInterval interval = null;
	private TimeZone tz = null;

	protected SDMSSchedule (SDMSObject o)
	{
		super (o);
	}

	protected void initProxy(SDMSObject p_object)
	{
		super.initProxy(p_object);

		thread = null;
		interval = null;
		tz = null;
	}

	public final TimerDate getNextTriggerDate (final SystemEnvironment sysEnv, final TimerDate minDate)
		throws SDMSException
	{
		if (interval == null) {
			final Long intervalId = getIntId (sysEnv);
			if (intervalId != null) {
				interval = SDMSIntervalTable.getObject(sysEnv, intervalId);
				String tmpTz = getTimeZone(sysEnv);
				tz = TimeZone.getTimeZone(tmpTz);
			}
		}
		if (interval == null)
			return new TimerDate();

		TimerDate nextTriggerDate;

		ExecutorService executor = Executors.newFixedThreadPool(1, this);

		final Future<TimerDate> future = executor.submit(new Callable<TimerDate>() {
			public TimerDate call() {
				try {

					long md = minDate.getTime();
					GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
					gc.setTimeInMillis(md);
					gc.setTimeZone(tz);
					md = gc.getTimeInMillis();

					Long next = interval.getNextTriggerDate(sysEnv, Long.valueOf(md), 0, tz, false);

					TimerDate result = new TimerDate();
					if (next != null) {
						md = next.longValue();
						gc.setTimeInMillis(md);
						gc.setTimeZone(SystemEnvironment.systemTimeZone);
						result = new TimerDate((int)(gc.getTimeInMillis() / (60 * 1000)));
					}

					return result;
				} catch (Exception e) {
					e.printStackTrace();
					SDMSMessage message = new SDMSMessage (sysEnv, "02205080756", "Exception ($1)", e.toString());
					SDMSThread.doTrace(sysEnv.cEnv, message.toString(), SDMSThread.SEVERITY_ERROR);
					return new TimerDate();
				}
			}
		});

		try {
			nextTriggerDate = future.get(SystemEnvironment.timerTimeout, TimeUnit.SECONDS);
			executor.shutdown();
		} catch (TimeoutException te) {
			executor.shutdown();
			SDMSMessage message = new SDMSMessage (sysEnv, "02205080950", "Timeout Exception during calculation of next execution");
			String header = SDMSThread.getHeader(sysEnv.cEnv, SDMSThread.SEVERITY_WARNING);
			System.err.println(header + message.toString());
			StackTraceElement[] ste = thread.getStackTrace();
			System.err.println(header + "****************** Start Stacktrace *********************");
			for(int i = 0; i < ste.length; i++) {
				System.err.println(header + ste[i].toString());
			}
			System.err.println(header + "****************** End Stacktrace   *********************");

			future.cancel(true);
			throw new CommonErrorException (message);
		} catch (ExecutionException ee) {
			executor.shutdown();
			throw (SDMSException)(ee.getCause());
		} catch (Exception e) {
			executor.shutdown();
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02205080653", "Unexpected Exception ($1) during calculation of next execution", e.toString()));
		}

		return nextTriggerDate;
	}

	public Thread newThread(Runnable r)
	{
		final int threadId = 1000 + ((SDMSThread)Thread.currentThread()).id();
		final String threadName = "getNextTriggerDate from " + ((SDMSThread)Thread.currentThread()).getName();
		thread = new SDMSThread(r) {
			public int id() {
				return threadId;
			}
			public void SDMSrun() {}
		};
		thread.lockThread = (SDMSThread)Thread.currentThread();
		thread.setName(threadName);
		return thread;
	}

	public final boolean isReallyActive (final SystemEnvironment sysEnv)
		throws SDMSException
	{
		boolean reallyActive = getIsActive (sysEnv).booleanValue();

		if (reallyActive) {
			Long parentId = getParentId (sysEnv);
			while (parentId != null) {
				final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, parentId);

				reallyActive = sce.getIsActive (sysEnv).booleanValue();
				if (! reallyActive)
					break;

				parentId = sce.getParentId (sysEnv);
			}
		}

		return reallyActive;
	}

	public String getURLName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long seId = getSeId(sysEnv);
		String se = null;
		if(seId != null)
			se = SDMSSchedulingEntityTable.getObject(sysEnv, seId).pathString(sysEnv);

		return pathString(sysEnv) + (se == null ? "" : " (" + se + ")");
	}

	public String getURL(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return "schedule " + getURLName(sysEnv);
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
		throws SDMSException
	{
		long p = SDMSPrivilege.NOPRIVS;
		long seP;
		Long seId;
		SDMSSchedulingEntity se;
		Vector myGroups;

		seId = getSeId(sysEnv);
		if (seId == null) {
			p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
			if(sysEnv.cEnv.isUser())
				if (getParentId(sysEnv) == null)
					p = p | SDMSPrivilege.VIEW | SDMSPrivilege.CREATE_CONTENT;

			return p & checkPrivs;
		}

		if (checkGroups == null) {
			myGroups = new Vector();
			if(sysEnv.cEnv.isUser()) {
				myGroups.addAll(sysEnv.cEnv.gid());
			}
		} else
			myGroups = checkGroups;

		p = SDMSPrivilege.NOPRIVS;
		se = SDMSSchedulingEntityTable.getObject(sysEnv, seId);
		seP = se.getPrivileges(sysEnv, SDMSPrivilege.VIEW|SDMSPrivilege.SUBMIT, false, myGroups);
		if ((seP & SDMSPrivilege.SUBMIT) == SDMSPrivilege.SUBMIT) {
			Long submitGId = getOwnerId(sysEnv);
			if (myGroups.contains(submitGId) || myGroups.contains(SDMSObject.adminGId)) {
				p = checkPrivs;
			} else {
				p = SDMSPrivilege.VIEW;
			}
		} else if ((seP & SDMSPrivilege.VIEW) == SDMSPrivilege.VIEW) {
			p |= SDMSPrivilege.VIEW;
		}
		p = addImplicitPrivs(p) & checkPrivs;

		return p & checkPrivs;
	}
}
