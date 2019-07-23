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
import de.independit.scheduler.server.output.*;

public class DropResourceStatus extends Node
{

	public final static String __version = "@(#) $Id: DropResourceStatus.java,v 2.0.20.1 2013/03/14 10:24:31 ronald Exp $";

	private String name;
	private boolean noerr;

	public DropResourceStatus(String n, Boolean ne)
	{
		super();
		name = n;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateDefinition rsd;

		try {
			rsd = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, name);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122323", "No Resource State Definition dropped"));
				return;
			}
			throw nfe;
		}

		rsd.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03201100113", "Resource State Definition dropped"));
	}
}

