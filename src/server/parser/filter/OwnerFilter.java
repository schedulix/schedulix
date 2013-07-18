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

public class OwnerFilter extends Filter
{

	public final static String __version = "@(#) $Id: OwnerFilter.java,v 2.2.6.1 2013/03/14 10:25:15 ronald Exp $";

	HashSet owners = null;
	Vector names;

	public OwnerFilter(SystemEnvironment sysEnv, Vector v)
	{
		super();
		names = v;
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
	throws SDMSException
	{
		try {
			if(owners == null) {
				owners = new HashSet();
				for(int i = 0; i < names.size(); i++) {
					try {
						Long gid = ((SDMSGroup) SDMSGroupTable.idx_name.getUnique(sysEnv, names.get(i))).getId(sysEnv);
						owners.add(gid);
					} catch (NotFoundException nfe) {

					}
				}
			}
			SDMSOwnedObject oo;

			if (p instanceof SDMSCalendar) {
				SDMSScheduledEvent scev = SDMSScheduledEventTable.getObject(sysEnv, ((SDMSCalendar)p).getScevId(sysEnv));
				SDMSEvent ev = SDMSEventTable.getObject(sysEnv, scev.getEvtId(sysEnv));
				oo = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
			} else if (p instanceof SDMSScheduledEvent) {
				SDMSEvent ev = SDMSEventTable.getObject(sysEnv, ((SDMSScheduledEvent)p).getEvtId(sysEnv));
				oo = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
			} else {
				oo = (SDMSOwnedObject) p;
			}

			if(owners.contains(oo.getOwnerId(sysEnv))) return true;
		} catch (Exception e) { }
		return false;
	}
}

