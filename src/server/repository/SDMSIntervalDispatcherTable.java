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

public class SDMSIntervalDispatcherTable extends SDMSIntervalDispatcherTableGeneric
{
	private static DispatchComparator dispatchComparator = new DispatchComparator();

	public SDMSIntervalDispatcherTable(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
	}

	public static Vector idx_intId_getSortedVector(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		Vector result = SDMSIntervalDispatcherTable.idx_intId.getVector(sysEnv, id);
		if (result.size() > 0)
			synchronized (dispatchComparator) {
				dispatchComparator.setEnv(sysEnv);
				Collections.sort(result, dispatchComparator);
			}

		return result;
	}
}

class DispatchComparator implements Comparator
{
	SystemEnvironment sysEnv;

	public DispatchComparator()
	{
	}

	public void setEnv (SystemEnvironment sysEnv)
	{
		this.sysEnv = sysEnv;
	}

	public int compare(Object o1, Object o2)
	{
		if (!(o1 instanceof SDMSIntervalDispatcher))
			throw new IllegalArgumentException();
		if (!(o2 instanceof SDMSIntervalDispatcher))
			throw new IllegalArgumentException();

		SDMSIntervalDispatcher oc1 = (SDMSIntervalDispatcher) o1;
		SDMSIntervalDispatcher oc2 = (SDMSIntervalDispatcher) o2;

		try {
			int seqno1 = oc1.getSeqNo(sysEnv).intValue();
			int seqno2 = oc2.getSeqNo(sysEnv).intValue();

			if (seqno1 > seqno2) return 1;
			if (seqno2 > seqno1) return -1;
		} catch (SDMSException e) {
			throw new IllegalArgumentException();
		}

		return 0;
	}
}
