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

public class DropFolder extends Node
{

	public final static String __version = "@(#) $Id: DropFolder.java,v 2.7.14.1 2013/03/14 10:24:29 ronald Exp $";

	private ObjectURL url;
	private boolean cascade;
	private boolean force;
	private boolean noerr;

	public DropFolder(ObjectURL u, Boolean c, Boolean f, Boolean ne)
	{
		super();
		url = u;
		cascade = c.booleanValue();
		force = f.booleanValue();
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long parentId ;
		SDMSFolder f;

		try {
			f = (SDMSFolder) url.resolve(sysEnv);
			parentId = f.getParentId(sysEnv);
		} catch(NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311121723", "No folder dropped"));
				return;
			}
			throw nfe;
		}

		if(parentId == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03704102211",
				"Folder SYSTEM cannot be dropped"));
		}

		final Long fId = f.getId(sysEnv);

		if(cascade || force) {
			f.deleteCascadeFirstPass(sysEnv, null);
			HashSet parameterLinks = new HashSet();
			f.deleteCascadeSecondPass(sysEnv, parameterLinks, force, null);
			if(!parameterLinks.isEmpty()) {
				Iterator i = parameterLinks.iterator();
				SDMSParameterDefinition pd = SDMSParameterDefinitionTable.getObject(sysEnv, (Long) i.next());
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, pd.getSeId(sysEnv));
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03402090156", "Parameter Reference from $1 ($2) still exists",
							se.pathString(sysEnv),
							pd.getName(sysEnv)));
			}
		}
		f.delete(sysEnv);
		result.setFeedback(new SDMSMessage(sysEnv, "03204112230", "Folder dropped"));
	}
}
