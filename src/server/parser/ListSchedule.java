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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ListSchedule
	extends Node
{
	public static final String __version = "@(#) $Id: ListSchedule.java,v 2.6.2.2 2013/06/18 09:49:33 ronald Exp $";

	private final Vector path;
	private final HashSet expandIds;

	public ListSchedule (Vector p)
	{
		super();

		path = p;
		expandIds = null;

		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListSchedule (Vector p, HashSet i)
	{
		super();

		path = p;
		expandIds = i;

		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	private final void expand_subs (final SystemEnvironment sysEnv, final Long sceId, final SDMSOutputContainer table)
	throws SDMSException
	{
		final Vector subList = SDMSScheduleTable.idx_parentId.getVector (sysEnv, sceId);
		final Iterator subIt = subList.iterator();
		while (subIt.hasNext()) {
			final SDMSSchedule sub = (SDMSSchedule) subIt.next();
			final Long subId = sub.getId (sysEnv);

			final Vector row = new Vector();

			row.add (subId);
			row.add (sub.pathVector (sysEnv));

			final Long ownerId = sub.getOwnerId (sysEnv);
			final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
			row.add (g.getName (sysEnv));

			row.add (ScheduleUtil.getIvalName (sysEnv, sub));
			row.add (sub.getTimeZone (sysEnv));
			row.add (sub.getIsActive (sysEnv));
			row.add (new Boolean (sub.isReallyActive (sysEnv)));
			row.add (sub.getPrivileges(sysEnv).toString());

			table.addData (sysEnv, row);

			if ((expandIds == null || expandIds.contains (subId)) && sub.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				expand_subs (sysEnv, subId, table);
		}
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector desc = new Vector();

		desc.add ("ID");
		desc.add ("NAME");
		desc.add ("OWNER");

		desc.add ("INTERVAL");

		desc.add ("TIME_ZONE");

		desc.add ("ACTIVE");

		desc.add ("EFF_ACTIVE");
		desc.add ("PRIVS");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Schedules", desc);

		SDMSSchedule sce;
		if (path != null)
			sce = SDMSScheduleTable.getSchedule (sysEnv, path);
		else {
			final SDMSKey key = new SDMSKey (null, "ROOT");
			sce = SDMSScheduleTable.idx_parentId_name_getUnique (sysEnv, key);
		}
		if(!sce.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402131453", "Insufficient privileges"));

		final Long sceId = sce.getId (sysEnv);

		final Vector row = new Vector();

		row.add (sceId);
		row.add (sce.pathVector (sysEnv));

		final Long ownerId = sce.getOwnerId (sysEnv);
		final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
		row.add (g.getName (sysEnv));

		row.add (ScheduleUtil.getIvalName (sysEnv, sce));
		row.add (sce.getTimeZone (sysEnv));
		row.add (sce.getIsActive (sysEnv));

		row.add (new Boolean (sce.isReallyActive (sysEnv)));

		row.add (sce.getPrivileges(sysEnv).toString());

		table.addData (sysEnv, row);

		if (expandIds == null || expandIds.size() > 0)
			expand_subs (sysEnv, sceId, table);

		Collections.sort (table.dataset, table.getComparator (sysEnv, 1));

		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04207252340", "$1 Schedule(s) found", new Integer (table.lines)));
	}
}
