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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSSmeCounterTable extends SDMSSmeCounterTableGeneric
{

	public final static String __version = "SDMSSmeCounterTable $Revision: 2.3.2.1 $ / @(#) $Id: SDMSSmeCounterTable.java,v 2.3.2.1 2013/03/14 10:25:25 ronald Exp $";

	public SDMSSmeCounterTable(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
	}

	public static void updateCounter(SystemEnvironment env)
	throws SDMSException
	{
		int n = env.tx.smeCtr.intValue();
		if(n == 0) return;

		TimeZone tz = SystemEnvironment.systemTimeZone;
		GregorianCalendar c = new GregorianCalendar(tz);
		Integer day = new Integer(c.get(Calendar.DAY_OF_MONTH));
		Integer month = new Integer(c.get(Calendar.MONTH)+1);
		Integer year = new Integer(c.get(Calendar.YEAR));
		Integer cnt;
		Long chksum;

		SDMSSmeCounter ctr;
		try {
			ctr = SDMSSmeCounterTable.idx_jahr_monat_tag_getUnique(env, new SDMSKey(year, month, day));
		} catch(NotFoundException nfe) {
			ctr = SDMSSmeCounterTable.table.create(env, year, month, day, new Integer(0), new Long(0));
		}

		cnt = ctr.getAnzahl(env);
		cnt = new Integer(cnt.intValue() + n);

		chksum = new Long(CheckSum.fastchksum(year.intValue(), month.intValue(), day.intValue(), cnt.intValue()));

		ctr.setAnzahl(env, cnt);
		ctr.setChecksum(env, chksum);

		env.tx.smeCtr = new Integer(0);
	}

}
