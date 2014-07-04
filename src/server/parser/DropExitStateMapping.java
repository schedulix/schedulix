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

public class DropExitStateMapping extends Node
{

	public final static String __version = "@(#) $Id: DropExitStateMapping.java,v 2.2.14.1 2013/03/14 10:24:29 ronald Exp $";

	private final ObjectURL url;
	private boolean noerr;

	public DropExitStateMapping(ObjectURL u, Boolean ne)
	{
		super();
		url = u;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateMappingProfile esmp;
		try {
			esmp = (SDMSExitStateMappingProfile) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122353", "No Exit State Mapping dropped"));
				return;
			}
			throw nfe;
		}
		Long esmpId = esmp.getId(sysEnv);

		Vector v = SDMSExitStateProfileTable.idx_defaultEsmpId.getVector (sysEnv, esmpId);
		if (v.size() > 0) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02111082000",
				"Exit State Mapping is default in Exit State Profile $1",
				((SDMSExitStateProfile)(v.elementAt(0))).getName(sysEnv) ));

		}

		Vector se_v = SDMSSchedulingEntityTable.idx_esmpId.getVector(sysEnv, esmpId);
		if (se_v.size() != 0) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02112171836",
				"Exit State Mapping in use by $1",
				((SDMSSchedulingEntity)(se_v.elementAt(0))).pathString(sysEnv)
				));

		}

		Vector esm_v = SDMSExitStateMappingTable.idx_esmpId.getVector(sysEnv, esmpId);
		SDMSExitStateMapping esm;
		Iterator i = esm_v.iterator();
		while (i.hasNext()) {
			esm = (SDMSExitStateMapping)(i.next());
			esm.delete(sysEnv);
		}
		esmp.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03201212211", "Exit State Mapping dropped"));
	}
}

