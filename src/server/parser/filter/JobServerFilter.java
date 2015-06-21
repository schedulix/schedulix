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

import de.independit.scheduler.locking.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class JobServerFilter extends Filter
{
	Vector jobsrvrList;
	HashSet scopeIds = null;
	SystemEnvironment env;

	public JobServerFilter(SystemEnvironment sysEnv, Vector v)
	{
		super();
		jobsrvrList = v;
		env = sysEnv;
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
		throws SDMSException
	{
		try {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity) p;
			if(scopeIds == null) {
				fillScopeIds(sysEnv);
			}
			if(scopeIds.contains(sme.getScopeId(sysEnv))) return true;
		} catch (SDMSException e) {
		}
		return false;
	}

	private void fillScopeIds(SystemEnvironment sysEnv)
	{
		if(scopeIds == null) {
			scopeIds = new HashSet();
			for(int i = 0; i < jobsrvrList.size(); i++) {
				Vector v = (Vector) jobsrvrList.get(i);
				try {
					scopeIds.add(SDMSScopeTable.pathToId(sysEnv, v));
				} catch (SDMSException sdmse) {
					if (sysEnv.tx.mode != SDMSTransaction.READONLY)
						throw new RuntimeException();
				}
			}
		}
	}

	public boolean equals(Object o)
	{
		if (o == this) return true;
		if (!(o instanceof JobServerFilter)) return false;
		JobServerFilter f;
		f = (JobServerFilter) o;
		if (scopeIds == null)	fillScopeIds(env);
		if (f.scopeIds == null)	f.fillScopeIds(env);
		if (scopeIds.size() != f.scopeIds.size()) return false;
		Iterator i = scopeIds.iterator();
		while (i.hasNext())
			if (!f.scopeIds.contains(i.next())) return false;

		return true;
	}
}

