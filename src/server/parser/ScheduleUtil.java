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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;

public class ScheduleUtil
{
	public static final String __version = "@(#) $Id: ScheduleUtil.java,v 2.1.18.1 2013/03/14 10:24:47 ronald Exp $";

	public static String getIvalName (final SystemEnvironment sysEnv, final SDMSSchedule sched)
	throws  SDMSException
	{
		final Long ivalId = sched.getIntId (sysEnv);
		if (ivalId == null)
			return new String ("");

		final SDMSInterval ival = SDMSIntervalTable.getObject (sysEnv, ivalId);
		return ival.getName (sysEnv);
	}

	private ScheduleUtil()
	{

	}
}
