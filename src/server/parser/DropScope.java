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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class DropScope extends Node
{

	public final static String __version = "@(#) $Id: DropScope.java,v 2.5.14.1 2013/03/14 10:24:31 ronald Exp $";

	private Vector path;
	private String name;
	private ObjectURL url;
	private boolean cascade;
	private boolean noerr;

	public DropScope(ObjectURL u, Boolean c, Boolean ne)
	{
		super();
		url = u;
		cascade = c.booleanValue();
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSScope s;
		Long parentId;
		Long sId;

		try {
			s = (SDMSScope) url.resolve(sysEnv);
		} catch(NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311121741", "No scope dropped"));
				return;
			}
			throw nfe;
		}

		s.delete(sysEnv, cascade);

		result.setFeedback(new SDMSMessage(sysEnv, "03201301042", "Scope dropped"));
	}

}

