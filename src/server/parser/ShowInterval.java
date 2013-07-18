/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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
import java.text.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.timer.*;

public class ShowInterval
	extends ShowCommented
{
	public static final String __version = "@(#) $Id: ShowInterval.java,v 2.10.2.2 2013/06/18 09:49:36 ronald Exp $";

	private static final TimeZone localTimeZone = TimeZone.getDefault();

	private static final SimpleDateFormat df = new SimpleDateFormat ("yyyy'-'MM'-'dd'T'HH':'mm", SystemEnvironment.systemLocale);

	private static final String empty = "";

	private final String name;
	private final WithHash with;

	private final TimerDate edgePlusOne = new TimerDate();

	private SDMSInterval ival;
	private Long ivalId;

	private boolean secondsIgnore;

	private double seconds = 0.0;

	private static final String toString (final TimerDate date, final TimeZone tz)
	{
		synchronized (df) {
			df.setTimeZone (tz);
			return df.format (date) + " " + tz.getID();
		}
	}

	public ShowInterval (String n, WithHash w)
	{
		super();
		name = n;
		with = w;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		ival = SDMSIntervalTable.idx_name_getUnique (sysEnv, name);
		if(!ival.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411717", "Insufficient privileges"));
		ivalId = ival.getId (sysEnv);

		final long beginMillis = System.currentTimeMillis();

		final long endMillis = System.currentTimeMillis();

		seconds = ((float) (endMillis - beginMillis)) / 1000.0;

		Vector desc = new Vector();

		desc.add ("ID");
		desc.add ("NAME");
		desc.add ("OWNER");

		desc.add ("STARTTIME");

		desc.add ("ENDTIME");

		desc.add ("BASE");

		desc.add ("DURATION");

		desc.add ("SYNCTIME");

		desc.add ("INVERSE");

		desc.add ("EMBEDDED");

		desc.add ("SELECTION");

		desc.add ("FILTER");
		desc.add ("CREATOR");
		desc.add ("CREATE_TIME");
		desc.add ("CHANGER");
		desc.add ("CHANGE_TIME");
		desc.add ("PRIVS");
		desc.add ("COMMENT");
		desc.add ("COMMENTTYPE");

		final Vector data = new Vector();
		final Long ivalId = ival.getId (sysEnv);

		data.add (ivalId);

		data.add (ival.getName (sysEnv));

		final Long ownerId = ival.getOwnerId (sysEnv);
		final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
		data.add (g.getName (sysEnv));

		final Long startTime = ival.getStartTime (sysEnv);
		if (startTime == null)
			data.add (empty);
		else
			data.add (new DateTime (startTime, false).toString(null));

		final Long endTime = ival.getEndTime (sysEnv);
		if (endTime == null)
			data.add (empty);
		else
			data.add (new DateTime (endTime, false).toString(null));

		final TimerUnit base = new TimerUnit (ival.getBaseIntervalMultiplier (sysEnv), ival.getBaseInterval (sysEnv));
		if (base.isINF())
			data.add (empty);
		else
			data.add (base.asString());

		final TimerUnit duration = new TimerUnit (ival.getDurationMultiplier (sysEnv), ival.getDuration (sysEnv));
		if (duration.isINF())
			data.add (empty);
		else
			data.add (duration.asString());

		data.add (new DateTime (ival.getSyncTime (sysEnv), false).toString(null));

		data.add (ival.getIsInverse (sysEnv));

		final Long embeddedIntervalId = ival.getEmbeddedIntervalId (sysEnv);
		if (embeddedIntervalId == null)
			data.add (empty);
		else {
			final SDMSInterval embeddedInterval = SDMSIntervalTable.getObject (sysEnv, embeddedIntervalId);
			data.add (embeddedInterval.getName (sysEnv));
		}

		data.add (getSelectionList (sysEnv));

		data.add (getFilterList (sysEnv));

		secondsIgnore = false;

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, ival.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(ival.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, ival.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(ival.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(ival.getPrivileges(sysEnv).toString());

		data.add(getCommentDescription(sysEnv, ivalId));
		data.add(getCommentInfoType(sysEnv, ivalId));

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "Interval", desc, data);
		result.setOutputContainer (table);

		if (secondsIgnore)
			result.setFeedback (new SDMSMessage (sysEnv, "04305012013", "Interval shown (WARNING: seconds will be ignored)"));
		else
			result.setFeedback (new SDMSMessage (sysEnv, "04207192249", "Interval shown"));
	}

	private SDMSOutputContainer getSelectionList (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Vector desc = new Vector();

		desc.add ("ID");

		desc.add ("VALUE");

		desc.add ("PERIOD_FROM");

		desc.add ("PERIOD_TO");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Interval Selections", desc);

		final Vector selList = SDMSIntervalSelectionTable.idx_intId.getVector (sysEnv, ivalId);

		final Iterator selIt = selList.iterator();
		while (selIt.hasNext()) {
			final SDMSIntervalSelection sel = (SDMSIntervalSelection) selIt.next();

			final Vector row = new Vector();

			row.add (sel.getId (sysEnv));

			final Integer value = sel.getValue (sysEnv);
			if (value == null)
				row.add (empty);
			else
				row.add (value);

			final Long periodFrom = sel.getPeriodFrom (sysEnv);
			if (periodFrom == null)
				row.add (empty);
			else
				row.add (new DateTime (periodFrom, false).toString());

			final Long periodTo = sel.getPeriodTo (sysEnv);
			if (periodTo == null)
				row.add (empty);
			else
				row.add (new DateTime (periodTo, false).toString());

			table.addData (sysEnv, row);
		}

		Collections.sort (table.dataset, table.getComparator (sysEnv, 0));

		return table;
	}

	private SDMSOutputContainer getFilterList (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Vector desc = new Vector();

		desc.add ("ID");

		desc.add ("CHILD");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Interval Filters", desc);

		final Vector filtList = SDMSIntervalHierarchyTable.idx_parentId.getVector (sysEnv, ivalId);

		final Iterator filtIt = filtList.iterator();
		while (filtIt.hasNext()) {
			final SDMSIntervalHierarchy filt = (SDMSIntervalHierarchy) filtIt.next();

			final Vector row = new Vector();

			row.add (filt.getId (sysEnv));

			final Long childId = filt.getChildId (sysEnv);
			final SDMSInterval childIval = SDMSIntervalTable.getObject (sysEnv, childId);
			row.add (childIval.getName (sysEnv));

			table.addData (sysEnv, row);
		}

		Collections.sort (table.dataset, table.getComparator (sysEnv, 1));

		return table;
	}

}
