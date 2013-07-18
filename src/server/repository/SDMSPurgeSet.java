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
import java.net.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class SDMSPurgeSet
{

	public final static String __version = "@(#) $Id: SDMSPurgeSet.java,v 2.3.2.1 2013/03/14 10:25:22 ronald Exp $";

	public final static String STAT_SIZE = "SIZE";

	public HashSet s;

	public SDMSPurgeSet()
	{
		s = new HashSet();
	}

	public synchronized void add(SystemEnvironment sysEnv, SDMSVersions v)
	throws SDMSException
	{
		s.add(v);
	}

	public synchronized void purge(SystemEnvironment sysEnv, long purgeLow)
	throws SDMSException
	{
		Iterator hi = s.iterator();
		while(hi.hasNext()) {
			((SDMSVersions) hi.next()).purge(sysEnv, purgeLow, hi);
		}
	}

	public synchronized int size()
	{
		return s.size();
	}

	public synchronized HashMap stat()
	{
		HashMap result = new HashMap();

		result.put(STAT_SIZE, new Long(s.size()));

		return result;
	}
}

