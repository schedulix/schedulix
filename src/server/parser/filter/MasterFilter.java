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

public class MasterFilter extends Filter
{

	public MasterFilter(SystemEnvironment sysEnv, Boolean dummy)
	{
		super();
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
		throws SDMSException
	{
		if (p instanceof SDMSSubmittedEntity) {
			try {
				SDMSSubmittedEntity sme = (SDMSSubmittedEntity) p;
				if(sme.getMasterId(sysEnv).equals(sme.getId(sysEnv))) return true;
			} catch (Exception e) { }
		} else if (p instanceof SDMSScheduledEvent) {
			return true;
		} else if (p instanceof SDMSCalendar) {
			return true;
		}
		return false;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof MasterFilter)) return false;
		return true;
	}
}

