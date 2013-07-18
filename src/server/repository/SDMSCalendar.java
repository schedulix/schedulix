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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSCalendar extends SDMSCalendarProxyGeneric
{

	public final static String __version = "SDMSCalendar $Revision: 2.1.8.1 $ / @(#) $Id: SDMSCalendar.java,v 2.1.8.1 2013/03/14 10:25:17 ronald Exp $";

	protected SDMSCalendar(SDMSObject p_object)
	{
		super(p_object);
	}

	public static Comparator getComparator(SystemEnvironment sysEnv)
	{
		return new CalendarComparator(sysEnv);
	}
}

class CalendarComparator implements Comparator
{
	SystemEnvironment sysEnv;

	public CalendarComparator(SystemEnvironment env)
	{
		sysEnv = env;
	}

	public int compare(Object o1, Object o2)
	throws ClassCastException
	{
		if (! (o1 instanceof SDMSCalendar))
			throw new ClassCastException("Wrong argument type for o1");
		if (! (o2 instanceof SDMSCalendar))
			throw new ClassCastException("Wrong argument type for o2");
		SDMSCalendar cal1 = (SDMSCalendar) o1;
		SDMSCalendar cal2 = (SDMSCalendar) o2;

		Long st1, st2;
		try {
			if (cal1.getId(sysEnv).longValue() == cal2.getId(sysEnv).longValue()) return 0;
			st1 = cal1.getStarttime(sysEnv);
			st2 = cal2.getStarttime(sysEnv);
		} catch (SDMSException e) {

			throw new ClassCastException(e.toString());
		}
		return st1.compareTo(st2);
	}

	public boolean equals(Object o1, Object o2)
	throws ClassCastException
	{
		if (! (o1 instanceof SDMSCalendar))
			throw new ClassCastException("Wrong argument type for o1");
		if (! (o2 instanceof SDMSCalendar))
			throw new ClassCastException("Wrong argument type for o2");
		SDMSCalendar cal1 = (SDMSCalendar) o1;
		SDMSCalendar cal2 = (SDMSCalendar) o2;

		Long st1, st2;
		try {
			if (cal1.getId(sysEnv).longValue() == cal2.getId(sysEnv).longValue()) return true;
			st1 = cal1.getStarttime(sysEnv);
			st2 = cal2.getStarttime(sysEnv);
		} catch (SDMSException e) {

			throw new ClassCastException(e.toString());
		}
		return st1.equals(st2);
	}
}
