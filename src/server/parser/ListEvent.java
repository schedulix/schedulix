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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ListEvent
	extends Node
{
	public static final String __version = "@(#) $Id: ListEvent.java,v 2.4.8.2 2013/06/18 09:49:32 ronald Exp $";

	public ListEvent()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector desc = new Vector();

		desc.add ("ID");
		desc.add ("NAME");
		desc.add ("OWNER");

		desc.add ("SCHEDULING_ENTITY");
		desc.add ("PRIVS");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Events", desc);

		final Iterator evtIter = SDMSEventTable.table.iterator (sysEnv);
		while (evtIter.hasNext()) {
			final SDMSEvent evt = (SDMSEvent) evtIter.next();

			final Vector row = new Vector();

			row.add (evt.getId (sysEnv));

			row.add (evt.getName (sysEnv));

			final Long ownerId = evt.getOwnerId (sysEnv);
			final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
			row.add (g.getName (sysEnv));

			final Long seId = evt.getSeId (sysEnv);
			if (seId == null)
				row.add (null);
			else {
				final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId);
				row.add (se.pathString (sysEnv));
			}

			row.add (evt.getPrivileges(sysEnv).toString());

			table.addData (sysEnv, row);
		}

		Collections.sort (table.dataset, table.getComparator (sysEnv, 1));

		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04203072329", "$1 Event(s) found", new Integer (table.lines)));
	}
}
