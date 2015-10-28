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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.*;

public class SDMSKillJob extends SDMSKillJobProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSKillJob.java,v 2.2.16.1 2013/03/14 10:25:20 ronald Exp $";

	public static final String S_JOBID	= "JOBID";
	public static final String S_KILLJOBID	= "KILLJOBID";
	public static final String S_SDMSHOST	= "SDMSHOST";
	public static final String S_SDMSPORT	= "SDMSPORT";

	public static final HashSet specialNames = new HashSet( Arrays.asList(new String[] {
	                        S_JOBID,
	                        S_KILLJOBID,
	                        S_SDMSHOST,
	                        S_SDMSPORT
	                }) );

	protected SDMSKillJob(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key)
	throws SDMSException
	{
		if(specialNames.contains(key)) return getSpecialValue(sysEnv, key);
		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, getSmeId(sysEnv));
		return sme.getVariableValue(sysEnv, key, false, ParseStr.S_DEFAULT);
	}

	private String getSpecialValue(SystemEnvironment sysEnv, String key)
	throws SDMSException
	{
		if(key.equals(S_JOBID)) 	return getId(sysEnv).toString();
		if(key.equals(S_KILLJOBID)) 	return getSmeId(sysEnv).toString();
		if(key.equals(S_SDMSHOST))	return SystemEnvironment.hostname;
		if(key.equals(S_SDMSPORT))	return "" + SystemEnvironment.port;
		throw new FatalException(new SDMSMessage(sysEnv, "03208090952", "Unknown special Parameter : $1", key));
	}

	public void setToError(SystemEnvironment sysEnv, String msg)
	throws SDMSException
	{
		setState(sysEnv, new Integer(ERROR));
		setErrorMsg(sysEnv, msg);
	}

	public void setState(SystemEnvironment sysEnv, Integer state)
	throws SDMSException
	{
		int oldState = getState(sysEnv).intValue();
		int newState = state.intValue();

		Date dts = new Date();
		Long ts = new Long (dts.getTime());

		if (newState == STARTING)
			synchronized(sysEnv.jidsStarting) {
				sysEnv.jidsStarting.put(getId(sysEnv), ts);
			}
		else if (oldState == STARTING)
			synchronized(sysEnv.jidsStarting) {
				sysEnv.jidsStarting.remove(getId(sysEnv));
			}

		super.setState(sysEnv, state);
	}
}
