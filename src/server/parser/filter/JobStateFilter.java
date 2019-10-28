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
import de.independit.scheduler.server.exception.*;

public class JobStateFilter extends Filter
{
	HashSet jobStates;

	public JobStateFilter(SystemEnvironment sysEnv, HashSet v)
	{
		super();
		jobStates = v;
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
		throws SDMSException
	{
		SDMSSubmittedEntity sme = null;
		try {
			sme = (SDMSSubmittedEntity) p;
			if(jobStates.contains(sme.getState(sysEnv))) return true;
		} catch (Exception e) { }
		if (sme != null && jobStates.contains(SDMSSubmittedEntity.SUSPENDED)) {
			if (!sme.getIsSuspended(sysEnv).equals(SDMSSubmittedEntity.NOSUSPEND))
				return true;
			if (sme.getChildSuspended(sysEnv).intValue() > 0)
				return true;
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if (o == this) return true;
		if (!(o instanceof JobStateFilter)) return false;
		JobStateFilter f;
		f = (JobStateFilter) o;
		if (jobStates.size() != f.jobStates.size()) return false;
		Iterator i = jobStates.iterator();
		while (i.hasNext())
			if (!f.jobStates.contains(i.next())) return false;
		return true;
	}
}

