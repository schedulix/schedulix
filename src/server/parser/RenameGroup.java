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

public class RenameGroup extends Node
{

	public final static String __version = "@(#) $Id: RenameGroup.java,v 2.1.14.1 2013/03/14 10:24:44 ronald Exp $";

	private ObjectURL url;
	private String name2;

	public RenameGroup(ObjectURL u, String to)
	{
		super();
		url = u;
		name2 = to;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSGroup g;

		g = (SDMSGroup) url.resolve(sysEnv);
		try {
			g.setName(sysEnv, name2);
		} catch (DuplicateKeyException dke) {
			g = SDMSGroupTable.idx_name_getUnique(sysEnv, name2);
			if(g.getDeleteVersion(sysEnv).longValue() == 0)	throw dke;
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03402191848", "A group with the same name previously existed"));
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03312091436", "Group renamed"));
	}
}

