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

public class DropEnvironment extends Node
{

	public final static String __version = "@(#) $Id: DropEnvironment.java,v 2.2.14.1 2013/03/14 10:24:28 ronald Exp $";

	private ObjectURL url;
	private boolean noerr;

	public DropEnvironment(ObjectURL u, Boolean ne)
	{
		super();
		url = u;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSNamedEnvironment ne;
		try {
			ne = (SDMSNamedEnvironment) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122349", "No Environment dropped"));
				return;
			}
			throw nfe;
		}
		Long neId = ne.getId(sysEnv);
		Vector v;

		v = SDMSSchedulingEntityTable.idx_neId.getVector(sysEnv, neId);
		if(v.size() > 0) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity) v.get(0);
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201222321",
			                               "Environment in use by Scheduling Entity $1", se.pathString(sysEnv)));
		}

		v = SDMSFolderTable.idx_envId.getVector(sysEnv, neId);
		if(v.size() > 0) {
			SDMSFolder f = (SDMSFolder) v.get(0);
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03309030925",
			                               "Environment in use by Folder $1", f.pathString(sysEnv)));
		}

		v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, neId);
		for(int i=0; i< v.size(); i++) {
			SDMSEnvironment e = (SDMSEnvironment) v.get(i);
			e.delete(sysEnv);
		}
		ne.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03201230008", "Environment dropped"));
	}
}

