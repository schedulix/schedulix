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

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.parser.filter.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.util.*;

public class ListCalendar extends Node
{

	public final static String __version = "@(#) $Id: ListCalendar.java,v 2.14.2.2 2013/06/18 09:49:32 ronald Exp $";

	private WithHash with;
	private DateTime starttime = null;
	private DateTime endtime = null;
	private TimeZone tz = null;
	private ObjectFilter objFilter = new ObjectFilter();

	public ListCalendar(WithHash w)
	{
		super();
		with = w == null ? new WithHash() : w;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	protected void fillDetail(SystemEnvironment sysEnv, SDMSScheduledEvent scev, SDMSEvent evt, SDMSSchedulingEntity se, SDMSOutputContainer d_container)
	throws SDMSException
	{
		Vector calv = SDMSCalendarTable.idx_scevId.getVector(sysEnv, scev.getId(sysEnv));
		Long seId = se.getId(sysEnv);
		String sePath = se.pathString(sysEnv);
		String seType = se.getTypeAsString(sysEnv);
		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, se.getOwnerId(sysEnv));
		String groupName = g.getName(sysEnv);
		String sePrivs = se.getPrivileges(sysEnv).toString();
		SDMSSchedule sce = SDMSScheduleTable.getObject(sysEnv, scev.getSceId(sysEnv));
		String scePath = sce.pathString(sysEnv);
		String evtName = evt.getName(sysEnv);
		Integer expFinalTime = se.getExpectedFinaltime(sysEnv);
		String finalTime = expFinalTime == null ? "" : expFinalTime.toString();
		if (expFinalTime == null) expFinalTime = new Integer(0);
		GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
		TimeZone gmtTz = SystemEnvironment.systemTimeZone;
		String tzId = tz.getID();

		for (int i = 0; i < calv.size(); ++i) {
			SDMSCalendar c = (SDMSCalendar) calv.get(i);
			gc.setTimeZone(gmtTz);
			gc.setTimeInMillis(c.getStarttime(sysEnv) * 1000);
			gc.setTimeZone(tz);
			DateTime cStartTime = new DateTime(gc.getTimeInMillis() / 1000);
			DateTime cmpTime = new DateTime(new Long((gc.getTimeInMillis() + expFinalTime.longValue() * 1000) / 1000));
			if (starttime == null || starttime.compareTo(cmpTime) <= 0) {
				if (endtime == null || endtime.compareTo(cStartTime) >= 0) {
					Vector v = new Vector();
					v.add(c.getId(sysEnv));
					v.add(sePath);
					v.add(seType);
					v.add(seId);
					v.add(groupName);
					v.add(sePrivs);
					v.add(scePath);
					v.add(sce.isReallyActive(sysEnv));
					v.add(evtName);
					v.add(cStartTime.toString(tz, true));
					v.add(finalTime);
					v.add(tzId);
					d_container.addData(sysEnv, v);
				}
			}
		}
	}

	protected void collectCalendar(SystemEnvironment sysEnv, SDMSOutputContainer d_container)
	throws SDMSException
	{
		Vector filter;
		if (with.containsKey(ParseStr.S_FILTER)) {
			filter = objFilter.initialize_filter(sysEnv, (Vector) with.get(ParseStr.S_FILTER), 0 , true );
		} else {
			filter = new Vector();
		}

		Iterator scevi = SDMSScheduledEventTable.table.iterator(sysEnv);
		while (scevi.hasNext()) {
			SDMSScheduledEvent scev = (SDMSScheduledEvent) scevi.next();
			if (!scev.getIsCalendar(sysEnv).booleanValue())
				continue;
			SDMSEvent evt = SDMSEventTable.getObject(sysEnv, scev.getEvtId(sysEnv));
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, evt.getSeId(sysEnv));
			if (objFilter.doFilter(sysEnv, se, filter)) {
				fillDetail(sysEnv, scev, evt, se, d_container);
			}
		}
		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 9, 1));
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSOutputContainer d_container = null;
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

		if (with.containsKey(ParseStr.S_STARTTIME)) {
			starttime = (DateTime) with.get(ParseStr.S_STARTTIME);
		}
		if (with.containsKey(ParseStr.S_ENDTIME)) {
			endtime = (DateTime) with.get(ParseStr.S_ENDTIME);
		}
		if (with.containsKey(ParseStr.S_TIME)) {
			tz = TimeZone.getTimeZone((String) with.get(ParseStr.S_TIME));
		} else {
			tz = TimeZone.getDefault();
		}

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03909181034", "List of Calendars"), desc);
		collectCalendar(sysEnv, d_container);

		result.setOutputContainer(d_container);

		result.setFeedback(
		        new SDMSMessage(sysEnv, "03909181035", "Calendar listed"));
	}
}

