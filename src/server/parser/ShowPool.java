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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class ShowPool extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowPool.java,v 2.17.4.5 2013/06/18 09:49:37 ronald Exp $";

	private final ObjectURL url;

	public ShowPool(ObjectURL u)
	{
		super();
		url = u;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_POOL);

		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");

		desc.add("NAME");

		desc.add("SCOPENAME");

		desc.add("TAG");

		desc.add("OWNER");

		desc.add("MANAGER_ID");

		desc.add("MANAGER_NAME");

		desc.add("MANAGER_SCOPENAME");

		desc.add("DEFINED_AMOUNT");

		desc.add("AMOUNT");

		desc.add("FREE_AMOUNT");

		desc.add("TOTAL_FREE_AMOUNT");

		desc.add("CHILD_ALLOCATED");

		desc.add("EVALUATION_CYCLE");

		desc.add("NEXT_EVALUATION_TIME");

		desc.add("ACTIVE_DISTRIBUTION");

		desc.add("TRACE_INTERVAL");

		desc.add("TRACE_BASE");

		desc.add("TRACE_BASE_MULTIPLIER");

		desc.add("TD0_AVG");

		desc.add("TD1_AVG");

		desc.add("TD2_AVG");

		desc.add("LW_AVG");

		desc.add("LAST_WRITE");

		desc.add("COMMENT");

		desc.add("COMMENTTYPE");

		desc.add("CREATOR");

		desc.add("CREATE_TIME");

		desc.add("CHANGER");

		desc.add("CHANGE_TIME");

		desc.add("PRIVS");

		desc.add("RESOURCES");

		desc.add("DISTRIBUTION_NAMES");

		desc.add("DISTRIBUTIONS");

		Vector data = new Vector();
		d_container = new SDMSOutputContainer(sysEnv,
		                                      new SDMSMessage(sysEnv, "03603161317", "Profile"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03603161318", "Pool shown"));
	}

}

