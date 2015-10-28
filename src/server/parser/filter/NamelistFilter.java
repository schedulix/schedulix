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
import de.independit.scheduler.server.util.*;

public class NamelistFilter extends Filter
{
	Vector paths;
	HashSet pathStrings = null;

	public NamelistFilter(SystemEnvironment sysEnv, Vector v)
	{
		super();
		paths = v;
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
		throws SDMSException
	{
		try {
			if (pathStrings == null) {
				fillPathstrings();
			}
			Long seId = null;
			SDMSSchedulingEntity se;
			long version = sysEnv.tx.versionId;
			if (p instanceof SDMSSubmittedEntity) {
				SDMSSubmittedEntity sme = (SDMSSubmittedEntity) p;
				version = sme.getSeVersion(sysEnv).longValue();
				seId = sme.getSeId(sysEnv);
			} else if (p instanceof SDMSCalendar) {
				SDMSScheduledEvent scev = SDMSScheduledEventTable.getObject(sysEnv, ((SDMSCalendar)p).getScevId(sysEnv));
				SDMSEvent ev = SDMSEventTable.getObject(sysEnv, scev.getEvtId(sysEnv));
				seId = ev.getSeId(sysEnv);
			} else if (p instanceof SDMSScheduledEvent) {
				SDMSEvent ev = SDMSEventTable.getObject(sysEnv, ((SDMSScheduledEvent)p).getEvtId(sysEnv));
				seId = ev.getSeId(sysEnv);
			}
			se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, version);
			if (pathStrings.contains(se.pathString(sysEnv, version)))
				return true;
		} catch (Exception e) { }
		return false;
	}

	private void fillPathstrings()
	{
		if (pathStrings == null) {
			pathStrings = new HashSet();
			for(int i = 0; i < paths.size(); i++)
				pathStrings.add(((PathVector)paths.get(i)).toString());
		}
	}

	public boolean equals(Object o)
	{
		if (o == this) return true;
		if (!(o instanceof NamelistFilter)) return false;
		NamelistFilter f;
		f = (NamelistFilter) o;
		if (paths.size() != f.paths.size()) return false;
		if (pathStrings == null) fillPathstrings();
		if (f.pathStrings == null) f.fillPathstrings();
		Iterator i = pathStrings.iterator();
		while (i.hasNext())
			if (!f.pathStrings.contains(i.next())) return false;
		return true;
	}
}

