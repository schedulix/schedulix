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

public class CreateFootprint extends Node
{

	private String name;
	private Vector resources;
	private boolean replace;

	public CreateFootprint(String n, Vector r, Boolean rep)
	{
		super();
		name = n;
		resources = r;
		replace = rep.booleanValue();
	}

	private void createResourceRequirement(SystemEnvironment sysEnv, WithHash with, SDMSFootprint f)
		throws SDMSException
	{
		Vector name;
		Long   nrId;
		Long   fId = f.getId(sysEnv);
		Integer amount;
		Integer keepMode;

		Long rsmpId = null;
		Integer lockmode = new Integer(SDMSResourceRequirement.N);
		Boolean isSticky = Boolean.FALSE;
		String stickyName = null;
		Long stickyParent = null;
		Integer exp_mult = null;
		Integer exp_interval = null;
		Boolean ignoreOnRerun = Boolean.FALSE;
		String condition = null;

		SDMSResourceRequirement rr;
		SDMSNamedResource nr;

		name = (Vector) with.get(ParseStr.S_NAME);
		if(name == null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03203140031", "No resourcename specified"));
		nr = SDMSNamedResourceTable.getNamedResource(sysEnv, name);
		if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.SYSTEM) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207161116", "Only system resources are allowed in footprints"));
		}
		nrId = nr.getId(sysEnv);

		amount = (Integer) with.get(ParseStr.S_AMOUNT);
		if(amount == null) amount = new Integer(0);

		keepMode = (Integer) with.get(ParseStr.S_KEEP);
		if(keepMode == null) keepMode = new Integer(SDMSResourceRequirement.NOKEEP);

		rr = SDMSResourceRequirementTable.table.create(sysEnv,
					nrId, fId, amount, keepMode, isSticky, stickyName, stickyParent,
					rsmpId, exp_mult, exp_interval, ignoreOnRerun, lockmode, condition);
		rr.check(sysEnv);

	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSFootprint f;

		try {
			f = SDMSFootprintTable.table.create(sysEnv, name);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterFootprint af = new AlterFootprint(new ObjectURL(new Integer(Parser.FOOTPRINT), name), resources, Boolean.FALSE);
				af.setEnv(env);
				af.go(sysEnv);
				result = af.result;
				return;
			} else {
				throw dke;
			}
		}

		for(int i = 0; i < resources.size(); i++) {
			WithHash w = (WithHash) resources.get(i);
			createResourceRequirement(sysEnv, w, f);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03203142353", "Footprint created"));
	}
}

