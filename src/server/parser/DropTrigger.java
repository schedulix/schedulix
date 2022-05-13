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

public class DropTrigger extends Node
{

	public final static String __version = "@(#) $Id: DropTrigger.java,v 2.5.4.1 2013/03/14 10:24:31 ronald Exp $";

	private String name;
	private WithItem fireObj;
	private boolean noerr;
	private ObjectURL url;

	public DropTrigger(ObjectURL u, Boolean ne)
	{
		super();
		url = u;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSTrigger t;

		t = (SDMSTrigger) url.resolve(sysEnv);
		if (t == null) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130110", "Trigger dropped"));
				return;
			} else {
				throw new NotFoundException(new SDMSMessage(sysEnv, "03112061110", "Trigger " + url.name + " not found"));
			}
		}

		t.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03206191503", "Trigger dropped"));
	}
}

