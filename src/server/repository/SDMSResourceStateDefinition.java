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

public class SDMSResourceStateDefinition extends SDMSResourceStateDefinitionProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSResourceStateDefinition.java,v 2.3.6.1 2013/03/14 10:25:23 ronald Exp $";

	protected SDMSResourceStateDefinition(SDMSObject p_object)
	{
		super(p_object);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{

		Long rsdId = getId(sysEnv);
		Vector v = SDMSResourceStateTable.idx_rsdId.getVector(sysEnv, rsdId);
		if (v.size() > 0) {
			SDMSResourceState rs = (SDMSResourceState)v.get(0);
			SDMSResourceStateProfile rsp = SDMSResourceStateProfileTable.getObject(sysEnv, rs.getRspId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201100921",
			                               "Resource State Definition used by Resource State Profile $1", rsp.getName(sysEnv)));
		}

		v = SDMSResourceStateMappingTable.idx_fromRsdId.getVector(sysEnv, rsdId);
		if(v.size() > 0) {
			SDMSResourceStateMapping rsm = (SDMSResourceStateMapping) v.get(0);
			SDMSResourceStateMappingProfile rsmp = SDMSResourceStateMappingProfileTable.getObject(sysEnv, rsm.getRsmpId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03210082211",
			                               "Resource State Definition used by Resource State Mapping $1", rsmp.getName(sysEnv)));
		}
		v = SDMSResourceStateMappingTable.idx_toRsdId.getVector(sysEnv, rsdId);
		if(v.size() > 0) {
			SDMSResourceStateMapping rsm = (SDMSResourceStateMapping) v.get(0);
			SDMSResourceStateMappingProfile rsmp = SDMSResourceStateMappingProfileTable.getObject(sysEnv, rsm.getRsmpId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03210082212",
			                               "Resource State Definition used by Resource State Mapping $1", rsmp.getName(sysEnv)));
		}

		super.delete(sysEnv);
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getName(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "resource state definition " + getURLName(sysEnv);
	}
}
