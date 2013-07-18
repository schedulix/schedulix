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

public class DropRsm extends Node
{

	public final static String __version = "@(#) $Id: DropRsm.java,v 2.1.14.1 2013/03/14 10:24:31 ronald Exp $";

	private String name;
	private boolean noerr;

	public DropRsm(String n, Boolean ne)
	{
		super();
		name = n;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSResourceStateMappingProfile rsmp;
		try {
			rsmp = SDMSResourceStateMappingProfileTable.idx_name_getUnique(sysEnv, name);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122342", "No Resource State Mapping dropped"));
				return;
			}
			throw nfe;
		}
		Long rsmpId = rsmp.getId(sysEnv);

		if(SDMSResourceRequirementTable.idx_rsmpId.containsKey(sysEnv, rsmpId)) {
			SDMSResourceRequirement rr = (SDMSResourceRequirement)SDMSResourceRequirementTable.idx_rsmpId.getVector(sysEnv, rsmpId).get(0);
			String t,n,nr;
			Long rrSeId = rr.getSeId(sysEnv);
			nr = SDMSNamedResourceTable.getObject(sysEnv, rr.getNrId(sysEnv)).pathString(sysEnv);
			try {
				n = SDMSSchedulingEntityTable.getObject(sysEnv, rrSeId).pathString(sysEnv);
				t = "job";
			} catch (NotFoundException nfe) {
				n = SDMSFootprintTable.getObject(sysEnv, rrSeId).getName(sysEnv);
				t = "footprint";
			}
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03311041044", "Resource state mapping is in use by $1 $2 ($3)",
			                               t, n, nr));
		}

		Vector v = SDMSResourceStateMappingTable.idx_rsmpId.getVector(sysEnv, rsmpId);
		for(int i = 0; i < v.size(); i++) {
			SDMSResourceStateMapping rsm = (SDMSResourceStateMapping) v.get(i);
			rsm.delete(sysEnv);
		}
		rsmp.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03203301647", "Resource State Mapping dropped"));
	}
}

