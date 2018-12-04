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

	private static final String ROLE_HEAD		= "HEAD";
	private static final String ROLE_FILTER		= "FILTER";
	private static final String ROLE_EMBED		= "EMBEDDED";
	private static final String ROLE_DISPATCH	= "DISPATCH";
	private static final String ROLE_DISP_SELECT	= "DISPATCH_SELECT";
	private static final String ROLE_DISP_FILTER	= "DISPATCH_FILTER";

	private static final int cmpList[] = {1, 2, 3};

	private final String name;
	private final WithHash with;
	private final Long ownerObject;

	private final TimerDate edgePlusOne = new TimerDate();

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
		ownerObject = null;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ShowInterval (String n, Long oid)
	{
		super();
		name = n;
		with = null;
		ownerObject = oid;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{

		SDMSInterval ival = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(name, ownerObject));
		if(!ival.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411717", "Insufficient privileges"));
		Long ivalId = ival.getId (sysEnv);

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
		desc.add ("DISPATCHER");
		desc.add ("HIERARCHY");
		desc.add ("CREATOR");
		desc.add ("CREATE_TIME");
		desc.add ("CHANGER");
		desc.add ("CHANGE_TIME");
		desc.add ("PRIVS");
		desc.add ("OWNER_OBJ_TYPE");
		desc.add ("OWNER_OBJ_ID");
		desc.add ("COMMENT");
		desc.add ("COMMENTTYPE");

		if (with != null) {
			desc.add ("EDGES");
		}

		final Vector data = new Vector();

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

		data.add (getSelectionList (sysEnv, ivalId));

		data.add (getFilterList (sysEnv, ivalId));

		data.add (getDispatcherList (sysEnv, ivalId));

		data.add (getHierarchyList (sysEnv, ivalId));

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

		data.add(ival.getObjTypeAsString(sysEnv));
		data.add(ival.getObjId(sysEnv));

		data.add(getCommentContainer(sysEnv, ivalId));
		data.add(getCommentInfoType(sysEnv, ivalId));

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "Interval", desc, data);
		result.setOutputContainer (table);

		if (secondsIgnore)
			result.setFeedback (new SDMSMessage (sysEnv, "04305012013", "Interval shown (WARNING: seconds will be ignored)"));
		else
			result.setFeedback (new SDMSMessage (sysEnv, "04207192249", "Interval shown"));
	}

	private SDMSOutputContainer getSelectionList (SystemEnvironment sysEnv, Long ivalId)
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

	private SDMSOutputContainer getFilterList (SystemEnvironment sysEnv, Long ivalId)
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

	private SDMSOutputContainer getDispatcherList (SystemEnvironment sysEnv, Long ivalId)
	throws SDMSException
	{
		final Vector desc = new Vector();
		desc.add ("ID");
		desc.add ("SEQNO");
		desc.add ("NAME");
		desc.add ("SELECT_INTERVAL_ID");
		desc.add ("SELECT_INTERVAL_NAME");
		desc.add ("FILTER_INTERVAL_ID");
		desc.add ("FILTER_INTERVAL_NAME");
		desc.add ("IS_ENABLED");
		desc.add ("IS_ACTIVE");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Dispatch Rules", desc);

		Vector drv = SDMSIntervalDispatcherTable.idx_intId.getVector(sysEnv, ivalId);
		for (int i = 0; i < drv.size(); ++i) {
			SDMSIntervalDispatcher intD = (SDMSIntervalDispatcher) drv.get(i);
			Vector row = new Vector();

			row.add(intD.getId(sysEnv));
			row.add(intD.getSeqNo(sysEnv));
			row.add(intD.getName(sysEnv));
			Long selIntId = intD.getSelectIntId(sysEnv);
			row.add(selIntId);
			if (selIntId != null) {
				SDMSInterval ival = SDMSIntervalTable.getObject(sysEnv, selIntId);
				row.add(ival.getName(sysEnv));
			} else {
				row.add(null);
			}
			Long fltIntId = intD.getFilterIntId(sysEnv);
			row.add(fltIntId);
			if (fltIntId != null) {
				SDMSInterval ival = SDMSIntervalTable.getObject(sysEnv, fltIntId);
				row.add(ival.getName(sysEnv));
			} else {
				row.add(null);
			}
			row.add(intD.getIsEnabled(sysEnv));
			row.add(intD.getIsActive(sysEnv));

			table.addData (sysEnv, row);
		}

		Collections.sort (table.dataset, table.getComparator (sysEnv, 1));

		return table;
	}

	private SDMSOutputContainer getHierarchyList (SystemEnvironment sysEnv, Long ivalId)
	throws SDMSException
	{
		final Vector desc = new Vector();
		desc.add ("ID");
		desc.add ("LEVEL");
		desc.add ("ROLE");
		desc.add ("PARENT");
		desc.add ("NAME");

		desc.add ("SEQNO");
		desc.add ("SELECT_INTERVAL_NAME");
		desc.add ("FILTER_INTERVAL_NAME");
		desc.add ("IS_ENABLED");
		desc.add ("IS_ACTIVE");

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
		desc.add ("DISPATCHER");
		desc.add ("OWNER_OBJ_TYPE");
		desc.add ("OWNER_OBJ_ID");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "Interval Hierarchy", desc);

		collectHierarchy(sysEnv, ivalId, ROLE_HEAD, null, table, 0);

		return table;
	}

	private void collectHierarchy(SystemEnvironment sysEnv, Long ivalId, String role, Long parentId, SDMSOutputContainer table, int level)
	throws SDMSException
	{
		SDMSInterval ival = SDMSIntervalTable.getObject(sysEnv, ivalId);
		SDMSInterval embeddedInterval = null;
		Integer iLevel = new Integer(level);
		Vector row = new Vector();

		row.add (ivalId);
		row.add (iLevel);
		row.add (role);
		row.add (parentId);
		row.add (ival.getName(sysEnv));
		row.add (null);
		row.add (null);
		row.add (null);
		row.add (null);
		row.add (null);
		final Long ownerId = ival.getOwnerId (sysEnv);
		final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
		row.add (g.getName (sysEnv));

		final Long startTime = ival.getStartTime (sysEnv);
		if (startTime == null)
			row.add (empty);
		else
			row.add (new DateTime (startTime, false).toString(null));

		final Long endTime = ival.getEndTime (sysEnv);
		if (endTime == null)
			row.add (empty);
		else
			row.add (new DateTime (endTime, false).toString(null));

		final TimerUnit base = new TimerUnit (ival.getBaseIntervalMultiplier (sysEnv), ival.getBaseInterval (sysEnv));
		if (base.isINF())
			row.add (empty);
		else
			row.add (base.asString());

		final TimerUnit duration = new TimerUnit (ival.getDurationMultiplier (sysEnv), ival.getDuration (sysEnv));
		if (duration.isINF())
			row.add (empty);
		else
			row.add (duration.asString());

		row.add (new DateTime (ival.getSyncTime (sysEnv), false).toString(null));

		row.add (ival.getIsInverse (sysEnv));

		final Long embeddedIntervalId = ival.getEmbeddedIntervalId (sysEnv);
		if (embeddedIntervalId == null)
			row.add (empty);
		else {
			embeddedInterval = SDMSIntervalTable.getObject (sysEnv, embeddedIntervalId);
			row.add (embeddedInterval.getName (sysEnv));
		}

		final StringBuffer selStr = new StringBuffer();
		String sep = "";
		final Vector selList = SDMSIntervalSelectionTable.idx_intId.getVector (sysEnv, ivalId);
		final Iterator selIt = selList.iterator();
		while (selIt.hasNext()) {
			final SDMSIntervalSelection sel = (SDMSIntervalSelection) selIt.next();

			final Integer value = sel.getValue (sysEnv);
			if (value != null) {
				selStr.append (sep);
				selStr.append (value);
			}

			final Long periodFrom = sel.getPeriodFrom (sysEnv);
			if (periodFrom != null) {
				selStr.append (sep);
				selStr.append (new DateTime (periodFrom, false).toString());
			}

			final Long periodTo = sel.getPeriodTo (sysEnv);
			if (periodTo != null) {
				if (periodFrom != null)
					selStr.append (" - ");
				else
					selStr.append (sep);
				selStr.append (new DateTime (periodTo, false).toString());
			}
			sep = ", ";
		}
		row.add (selStr.toString());

		final Vector filtList = SDMSIntervalHierarchyTable.idx_parentId.getVector (sysEnv, ivalId);
		Iterator filtIt = filtList.iterator();
		final StringBuffer filtStr = new StringBuffer();
		sep = empty;
		while (filtIt.hasNext()) {
			final SDMSIntervalHierarchy filt = (SDMSIntervalHierarchy) filtIt.next();

			final Long childId = filt.getChildId (sysEnv);
			final SDMSInterval childIval = SDMSIntervalTable.getObject (sysEnv, childId);
			filtStr.append (sep);
			filtStr.append (childIval.getName (sysEnv));
			sep = ", ";
		}
		row.add (filtStr.toString());

		Vector drv = SDMSIntervalDispatcherTable.idx_intId.getSortedVector(sysEnv, ivalId);
		final StringBuffer dispStr = new StringBuffer();
		sep = empty;
		for (int i = 0; i < drv.size(); ++i) {
			SDMSIntervalDispatcher intD = (SDMSIntervalDispatcher) drv.get(i);
			dispStr.append(sep);
			dispStr.append(intD.getName(sysEnv));
			sep = ", ";
		}
		row.add (dispStr.toString());
		row.add(ival.getObjTypeAsString(sysEnv));
		row.add(ival.getObjId(sysEnv));

		table.addData (sysEnv, row);

		if (embeddedIntervalId != null)
			collectHierarchy(sysEnv, embeddedIntervalId, ROLE_EMBED, ivalId, table, level + 1);
		filtIt = filtList.iterator();
		while (filtIt.hasNext()) {
			final SDMSIntervalHierarchy filt = (SDMSIntervalHierarchy) filtIt.next();
			final Long childId = filt.getChildId (sysEnv);
			collectHierarchy(sysEnv, childId, ROLE_FILTER, ivalId, table, level + 1);
		}
		for (int i = 0; i < drv.size(); ++i) {
			SDMSIntervalDispatcher intD = (SDMSIntervalDispatcher) drv.get(i);
			collectDspHierarchy (sysEnv, intD.getId(sysEnv), ROLE_DISPATCH, ivalId, table, level + 1);
		}
	}

	private void collectDspHierarchy(SystemEnvironment sysEnv, Long drId, String role, Long parentId, SDMSOutputContainer table, int level)
	throws SDMSException
	{
		SDMSIntervalDispatcher dr = SDMSIntervalDispatcherTable.getObject(sysEnv, drId);
		Vector row = new Vector();
		Integer iLevel = new Integer(level);

		row.add (drId);
		row.add (iLevel);
		row.add (role);
		row.add (parentId);
		row.add (dr.getName(sysEnv));
		row.add (dr.getSeqNo(sysEnv));

		Long selIntId = dr.getSelectIntId(sysEnv);
		if (selIntId != null) {
			SDMSInterval ival = SDMSIntervalTable.getObject(sysEnv, selIntId);
			row.add(ival.getName(sysEnv));
		} else {
			row.add(null);
		}
		Long fltIntId = dr.getFilterIntId(sysEnv);
		if (fltIntId != null) {
			SDMSInterval ival = SDMSIntervalTable.getObject(sysEnv, fltIntId);
			row.add(ival.getName(sysEnv));
		} else {
			row.add(null);
		}
		row.add(dr.getIsEnabled(sysEnv));
		row.add(dr.getIsActive(sysEnv));

		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);
		row.add(null);

		table.addData (sysEnv, row);

		if (selIntId != null) {
			collectHierarchy(sysEnv, selIntId, ROLE_DISP_SELECT, drId, table, level + 1);
		}
		if (fltIntId != null) {
			collectHierarchy(sysEnv, fltIntId, ROLE_DISP_FILTER, drId, table, level + 1);
		}
	}

}
