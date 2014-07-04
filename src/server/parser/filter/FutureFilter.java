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


package de.independit.scheduler.server.parser.filter;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.util.*;

public class FutureFilter extends Filter
{

	public final static String __version = "@(#) $Id: FutureFilter.java,v 2.1.6.1 2013/03/14 10:25:14 ronald Exp $";

	long numMillis = 0;

	public FutureFilter(SystemEnvironment sysEnv, WithHash interval)
	{
		super();
		Integer mult = (Integer) interval.get("MULT");
		Integer base = (Integer) interval.get("INTERVAL");
		if(mult == null || base == null) {
			numMillis = 0;
			return;
		}
		numMillis = System.currentTimeMillis();
		switch(base.intValue()) {
			case SDMSInterval.MINUTE:
				numMillis += mult.longValue() * 60 * 1000;
				break;
			case SDMSInterval.HOUR:
				numMillis += mult.longValue() * 60 * 60 * 1000;
				break;
			case SDMSInterval.DAY:
				numMillis += mult.longValue() * 24 * 60 * 60 * 1000;
				break;
			case SDMSInterval.WEEK:
				numMillis += mult.longValue() * 7 * 24 * 60 * 60 * 1000;
				break;
			case SDMSInterval.MONTH:
				numMillis += mult.longValue() * 30 * 24 * 60 * 60 * 1000;
				break;
			case SDMSInterval.YEAR:
				numMillis += mult.longValue() * 365 * 24 * 60 * 60 * 1000;
				break;
		}
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
		throws SDMSException
	{
		try {
			if (p instanceof SDMSSubmittedEntity)
				return true;
			Long sts = null;
			if (p instanceof SDMSCalendar) {
				sts = ((SDMSCalendar)p).getStarttime(sysEnv);
			} else if (p instanceof SDMSScheduledEvent) {
				if (!((SDMSScheduledEvent)p).getNextActivityIsTrigger(sysEnv).booleanValue())
					return false;
				sts = ((SDMSScheduledEvent)p).getNextActivityTime(sysEnv);
			}
			DateTime dt = new DateTime(sts);
			sts = dt.toDate().getTime();
			if(sts.longValue() < numMillis) return true;

		} catch (Exception e) { }
		return false;
	}
}
