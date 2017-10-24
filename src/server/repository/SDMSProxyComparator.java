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

import java.lang.*;
import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;

public class SDMSProxyComparator implements Comparator
{

	SystemEnvironment sysEnv = null;

	public SDMSProxyComparator(SystemEnvironment sysEnv)
	{
		this.sysEnv = sysEnv;
	}

	public int compare(Object o1, Object o2)
	{
		SDMSProxy p1, p2;
		SDMSKey k1, k2;
		if (o1 instanceof SDMSProxy && o2 instanceof SDMSProxy) {
			p1 = (SDMSProxy) o1;
			p2 = (SDMSProxy) o2;
			try {
				k1 = p1.getSortKey(sysEnv);
				k2 = p2.getSortKey(sysEnv);
			} catch (SDMSException e) {
				return 0;
			}

			return k1.compareTo(k2);
		}
		return 0;
	}

	public boolean equals(Object o)
	{
		return (o instanceof SDMSProxyComparator);
	}
}
