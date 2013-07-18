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

public class DropExitStatProf extends Node
{

	public final static String __version = "@(#) $Id: DropExitStatProf.java,v 2.3.4.1 2013/03/14 10:24:29 ronald Exp $";

	private ObjectURL url;
	private boolean noerr;

	public DropExitStatProf(ObjectURL u, Boolean ne)
	{
		super();
		url = u;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSExitStateProfile esp;
		try {
			esp = (SDMSExitStateProfile) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130011", "No Exit State Profile dropped"));
				return;
			}
			throw nfe;
		}

		Long espId = esp.getId(sysEnv);

		Vector se_v = SDMSSchedulingEntityTable.idx_espId.getVector(sysEnv, espId);
		if (se_v.size() != 0) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02112171835",
			                                "Exit State Profile in use by $1",
			                                ((SDMSSchedulingEntity)(se_v.elementAt(0))).pathString(sysEnv)
			                                                ));
		}

		Vector es_v = SDMSExitStateTable.idx_espId.getVector(sysEnv, espId);
		SDMSExitState es;
		Iterator i = es_v.iterator();
		while (i.hasNext()) {
			es = (SDMSExitState)(i.next());
			es.delete(sysEnv);
		}
		esp.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03204112214", "Exit State Profile dropped"));
	}
}

