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


package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class CreateEnvironment extends Node
{

	public final static String __version = "@(#) $Id: CreateEnvironment.java,v 2.5.14.1 2013/03/14 10:24:25 ronald Exp $";

	private String name;
	private Vector resourceList;
	private boolean replace;

	public CreateEnvironment(String n, Vector r, Boolean rep)
	{
		super();
		name = n;
		resourceList = (r == null ? new Vector() : r);
		replace = rep.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{

		SDMSNamedEnvironment ne;
		try {
			ne = SDMSNamedEnvironmentTable.table.create(sysEnv, name);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterEnvironment ae = new AlterEnvironment(new ObjectURL(new Integer(Parser.ENVIRONMENT), name), resourceList, Boolean.FALSE);
				ae.setEnv(env);
				ae.go(sysEnv);
				result = ae.result;
				return;
			} else {
				throw dke;
			}
		}

		Long neId = ne.getId(sysEnv);

		for(int i = 0; i< resourceList.size(); i++) {
			WithItem w = (WithItem) resourceList.get(i);
			SDMSNamedResource nr = SDMSNamedResourceTable.getNamedResource(sysEnv, (Vector) w.key);
			String condition = (String) w.value;
			if (condition != null && condition.trim().equals("")) condition = null;

			if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.STATIC) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03201222223",
				                               "Resource $1 must be STATIC", nr.getName(sysEnv)));
			}
			SDMSEnvironmentTable.table.create(sysEnv, neId, nr.getId(sysEnv), condition);
		}
		result.setFeedback(new SDMSMessage(sysEnv, "03201222251", "Environment created"));
	}
}

