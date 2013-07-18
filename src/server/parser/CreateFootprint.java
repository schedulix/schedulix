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

	public final static String __version = "@(#) $Id: CreateFootprint.java,v 2.3.14.1 2013/03/14 10:24:26 ronald Exp $";

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
		Integer lockmode;
		String mapname;
		Long rsmpId;
		Integer keepMode;
		Boolean isSticky;
		WithHash expired;
		Integer exp_mult;
		Integer exp_interval;
		Vector states;

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

		lockmode = (Integer) with.get(ParseStr.S_LOCKMODE);
		if(lockmode == null) lockmode = new Integer(SDMSResourceRequirement.N);

		mapname = (String) with.get(ParseStr.S_MAP_STATUS);
		if(mapname == null)  rsmpId = null;
		else rsmpId = new Long(1);

		isSticky = (Boolean) with.get(ParseStr.S_STICKY);
		if(isSticky == null) isSticky = Boolean.FALSE;

		exp_interval = null;
		expired = (WithHash) with.get(ParseStr.S_EXPIRED);
		if(expired == null)	exp_mult = null;
		else 			exp_mult = new Integer(1);

		states = (Vector) with.get(ParseStr.S_STATUS);
		if(states != null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207161112", "Resource states are not a valid option for system resources"));
		}

		rr = SDMSResourceRequirementTable.table.create(sysEnv,
		                nrId, fId, amount, keepMode, isSticky,
		                rsmpId, exp_mult, exp_interval, lockmode, null );
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

