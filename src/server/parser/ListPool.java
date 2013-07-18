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


package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class ListPool extends Node
{

	public final static String __version = "@(#) $Id: ListPool.java,v 2.6.14.5 2013/06/18 09:49:33 ronald Exp $";

	public ListPool()
	{
		super();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector desc = new Vector();
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_POOL);

		desc.add("ID");

		desc.add("NAME");

		desc.add("SCOPENAME");

		desc.add("OWNER");

		desc.add("MANAGER_ID");

		desc.add("MANAGER_NAME");

		desc.add("MANAGER_SCOPENAME");

		desc.add("DEFINED_AMOUNT");

		desc.add("AMOUNT");

		desc.add("FREE_AMOUNT");

		desc.add("EVALUATION_CYCLE");

		desc.add("NEXT_EVALUATION_TIME");

		desc.add("CREATOR");

		desc.add("CREATE_TIME");

		desc.add("CHANGER");

		desc.add("CHANGE_TIME");

		desc.add("PRIVS");

		SDMSOutputContainer s_container = new SDMSOutputContainer(sysEnv, null, desc);

		result.setOutputContainer(s_container);

		result.setFeedback(new SDMSMessage(sysEnv,"03603151712", "$1 Pool(s) found", new Integer(s_container.lines)));
	}

}

