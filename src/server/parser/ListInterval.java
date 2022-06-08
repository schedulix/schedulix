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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.timer.*;

public class ListInterval
	extends Node
{
	public static final String __version = "@(#) $Id: ListInterval.java,v 2.8.2.2 2013/06/18 09:49:33 ronald Exp $";

	private static final String empty = "";
	private boolean all;

	public ListInterval(boolean all)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		this.all = all;
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long oId = null;
		SDMSInterval ival = null;

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
		desc.add ("OBJ_TYPE");
		desc.add ("OBJ_ID");
		desc.add ("PRIVS");
		desc.add ("SE_ID");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Intervals", desc);

		final Iterator ivalIter = SDMSIntervalTable.table.iterator (sysEnv);
		while (ivalIter.hasNext()) {
			ival = (SDMSInterval) ivalIter.next();
			oId = ival.getObjId(sysEnv);
			if (!all) {
				if (oId != null) continue;
			}

			final Vector row = new Vector();

			row.add (ival.getId (sysEnv));

			row.add (ival.getName (sysEnv));

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
				final SDMSInterval embeddedInterval = SDMSIntervalTable.getObject (sysEnv, embeddedIntervalId);
				row.add (embeddedInterval.getName (sysEnv));
			}

			row.add (ival.getObjTypeAsString(sysEnv));
			row.add (oId);

			row.add (ival.getPrivileges(sysEnv).toString());
			row.add (ival.getSeId(sysEnv));

			table.addData (sysEnv, row);
		}

		Collections.sort (table.dataset, table.getComparator (sysEnv, 1));

		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04207192158", "$1 Interval(s) found", Integer.valueOf (table.lines)));
	}
}
