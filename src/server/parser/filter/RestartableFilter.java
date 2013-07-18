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

public class RestartableFilter extends Filter
{

	public final static String __version = "@(#) $Id: RestartableFilter.java,v 2.3.18.1 2013/03/14 10:25:15 ronald Exp $";

	Integer stateType;

	public RestartableFilter(SystemEnvironment sysEnv, Integer type)
	{
		super();
		stateType = type;
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
	throws SDMSException
	{
		try {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity) p;
			int s = sme.getState(sysEnv).intValue();
			if(stateType.intValue() == SDMSExitState.RESTARTABLE) {
				if(sme.getJobIsRestartable(sysEnv).booleanValue()) return true;
				if(s == SDMSSubmittedEntity.BROKEN_ACTIVE ||
				    s == SDMSSubmittedEntity.BROKEN_FINISHED ||
				    s == SDMSSubmittedEntity.ERROR) return true;
			}
			if(s != SDMSSubmittedEntity.FINAL &&
			    s != SDMSSubmittedEntity.FINISHED) return false;
			if(stateType.intValue() == SDMSExitState.FINAL) {
				if(!sme.getJobIsFinal(sysEnv).booleanValue()) return false;
				return true;
			}
			if(stateType.intValue() == SDMSExitState.PENDING) {
				if(sme.getJobIsFinal(sysEnv).booleanValue()) return false;
				if(sme.getJobIsRestartable(sysEnv).booleanValue()) return false;
				return true;
			}
		} catch (Exception e) { }
		return false;
	}
}

