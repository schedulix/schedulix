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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;

public class DropSchedule
	extends Node
{
	public static final String __version = "@(#) $Id: DropSchedule.java,v 2.3.4.1 2013/03/14 10:24:31 ronald Exp $";

	private final ObjectURL obj;
	private final boolean noerr;

	public DropSchedule (ObjectURL o, Boolean ne)
	{
		super();
		obj = o;
		noerr = ne.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final SDMSSchedule sce = (SDMSSchedule) obj.resolve(sysEnv);
		final Long sceId = obj.objId;

		if (SDMSScheduledEventTable.idx_sceId.containsKey (sysEnv, sceId))
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04207251740", "Schedule in use by Scheduled Event(s)"));

		if (SDMSScheduleTable.idx_parentId.containsKey (sysEnv, sceId))
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04207251742", "Schedule contains sub-schedule(s)"));

		sce.delete (sysEnv);

		result.setFeedback (new SDMSMessage (sysEnv, "04207251741", "Schedule dropped"));
	}
}
