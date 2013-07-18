/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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
import java.lang.*;
import java.util.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSExitStateMappingProfile extends SDMSExitStateMappingProfileProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSExitStateMappingProfile.java,v 2.2.6.1 2013/03/14 10:25:18 ronald Exp $";

	protected SDMSExitStateMappingProfile(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long map(SystemEnvironment sysEnv, int exitCode, long version)
	throws SDMSException
	{

		Long esdId = null;
		Long esmpId = getId(sysEnv);
		Vector v_esm = SDMSExitStateMappingTable.idx_esmpId.getVector(sysEnv, esmpId, version);
		Iterator i = v_esm.iterator();
		while (i.hasNext()) {
			SDMSExitStateMapping esm = (SDMSExitStateMapping)i.next();
			if (esm.getEcrStart(sysEnv).intValue() <= exitCode && esm.getEcrEnd(sysEnv).intValue() >= exitCode) {
				esdId = esm.getEsdId(sysEnv);
				break;
			}
		}
		if (esdId == null) {

			throw new FatalException(new SDMSMessage(sysEnv, "02201111620",
			                         "Invalid Exit State Mapping Profile $1 does not map exit code $2",
			                         esmpId, new Integer(exitCode)));
		}
		return esdId;
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getName(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "exit state mapping " + getURLName(sysEnv);
	}
}
