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

public class DropInterval
	extends Node
{
	public static final String __version = "@(#) $Id: DropInterval.java,v 2.3.4.1 2013/03/14 10:24:30 ronald Exp $";

	private final ObjectURL obj;
	private final boolean noerr;

	public DropInterval (ObjectURL o, Boolean ne)
	{
		super();

		obj = o;
		noerr = ne.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		try {
			final SDMSInterval ival = (SDMSInterval) obj.resolve(sysEnv);
			final Long ivalId = obj.objId;

			if (SDMSIntervalTable.idx_embeddedIntervalId.containsKey (sysEnv, ivalId))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04209111937", "Interval in use by Interval(s)"));

			if (SDMSScheduleTable.idx_intId.containsKey (sysEnv, ivalId))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04207191907", "Interval in use by Schedule(s)"));

			if (SDMSIntervalHierarchyTable.idx_childId.containsKey (sysEnv, ivalId)) {
				Vector ihv = SDMSIntervalHierarchyTable.idx_childId.getVector(sysEnv, ivalId);
				SDMSIntervalHierarchy ih;
				for (int i = 0; i < ihv.size(); ++i) {
					ih = (SDMSIntervalHierarchy) ihv.get(i);
					Long parentId = ih.getParentId(sysEnv);
					try {
						SDMSIntervalTable.getObject(sysEnv, parentId);
						throw new CommonErrorException (new SDMSMessage (sysEnv, "04209112049", "Interval in use by Interval(s)"));
					} catch(NotFoundException nfe) {
						ih.delete(sysEnv);
					}
				}
			}

			if (SDMSSchedulingHierarchyTable.idx_intId.containsKey (sysEnv, ivalId))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04811061006", "Interval in use by SchedulingHierarchy(s)"));
			if (SDMSIntervalDispatcherTable.idx_selectIntId.containsKey (sysEnv, ivalId))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "03910070933", "Interval in use as select interval by Dispatcher(s)"));
			if (SDMSIntervalDispatcherTable.idx_filterIntId.containsKey (sysEnv, ivalId))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "03910070934", "Interval in use as filter by Dispatcher(s)"));

			IntervalUtil.killFilter (sysEnv, ivalId);
			IntervalUtil.killSelections (sysEnv, ivalId);
			IntervalUtil.killDispatcher (sysEnv, ivalId);

			ival.delete (sysEnv);
		} catch (NotFoundException nfe) {
			if (!noerr)
				throw nfe;
		}

		result.setFeedback (new SDMSMessage (sysEnv, "04207181908", "Interval dropped"));
	}
}
