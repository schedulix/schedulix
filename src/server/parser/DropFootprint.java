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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class DropFootprint extends Node
{

	public final static String __version = "@(#) $Id: DropFootprint.java,v 2.2.14.1 2013/03/14 10:24:29 ronald Exp $";

	private ObjectURL url;
	private boolean noerr;

	public DropFootprint(ObjectURL u, Boolean ne)
	{
		super();
		url = u;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSFootprint fp;
		try {
			fp = (SDMSFootprint) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130013", "No Footprint dropped"));
				return;
			}
			throw nfe;
		}
		Long fpId = fp.getId(sysEnv);

		Vector v = SDMSSchedulingEntityTable.idx_fpId.getVector(sysEnv, fpId);
		if(v.size() > 0) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity) v.get(0);
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02205190930",
			                               "Footprint in use by Scheduling Entity $1", se.pathString(sysEnv)));
		}

		v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, fpId);
		for(int i=0; i< v.size(); i++) {
			SDMSResourceRequirement rr = (SDMSResourceRequirement) v.get(i);
			rr.delete(sysEnv);
		}
		fp.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "02205190934", "Footprint dropped"));
	}
}

