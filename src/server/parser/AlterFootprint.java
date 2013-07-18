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

public class AlterFootprint extends Node
{

	public final static String __version = "@(#) $Id: AlterFootprint.java,v 2.4.14.1 2013/03/14 10:24:20 ronald Exp $";

	private ObjectURL url;
	private Vector resources;
	private Boolean add;
	private boolean noerr;

	public AlterFootprint(ObjectURL u, Vector r, Boolean ne)
	{
		super();
		url = u;
		resources = r;
		add = null;
		noerr = ne.booleanValue();
	}

	public AlterFootprint(ObjectURL u, Vector r, boolean a, Boolean ne)
	{
		super();
		url = u;
		resources = r;
		add = new Boolean(a);
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
				result.setFeedback(new SDMSMessage(sysEnv, "03311130021", "Footprint altered"));
				return;
			}
			throw nfe;
		}

		if(add != null && !add.booleanValue()) {
			deleteResourceRequirements(sysEnv, fp);
		} else {
			if(add == null) {
				Vector v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, fp.getId(sysEnv));
				for(int i = 0; i < v.size(); i++) {
					((SDMSResourceRequirement) v.get(i)).delete(sysEnv);
				}
			}
			for(int i = 0; i < resources.size(); i++) {
				WithHash w = (WithHash) resources.get(i);
				createResourceRequirement(sysEnv, w, fp);
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03207161123", "Footprint altered"));
	}

	private void deleteResourceRequirements(SystemEnvironment sysEnv, SDMSFootprint f)
	throws SDMSException
	{
		Long fpId = f.getId(sysEnv);

		for(int i = 0; i < resources.size(); i++) {
			Vector rname = (Vector) resources.get(i);
			Long nrId = SDMSNamedResourceTable.pathToId(sysEnv, rname);
			SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(fpId, nrId)).delete(sysEnv);

			Vector v = SDMSSchedulingEntityTable.idx_fpId.getVector(sysEnv, fpId);
			for(int j = 0; i < v.size(); j++) {
				final SDMSSchedulingEntity se = (SDMSSchedulingEntity) v.get(j);
				if(se.checkParameterRI(sysEnv, nrId)) {

					SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
					throw new CommonErrorException(
					        new SDMSMessage(sysEnv, "0340911291427", "A parameter of Resource $1 is referenced by $2",
					                        nr.pathString(sysEnv), se.pathString(sysEnv))
					);
				}
			}

		}
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
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03203140030", "No resourcename specified"));
		nr = SDMSNamedResourceTable.getNamedResource(sysEnv, name);
		if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.SYSTEM) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207161115", "Only system resources are allowed in footprints"));
		}
		nrId = nr.getId(sysEnv);

		amount = (Integer) with.get(ParseStr.S_AMOUNT);
		if(amount == null) amount = new Integer(0);

		keepMode = (Integer) with.get(ParseStr.S_KEEP);
		if(keepMode == null) keepMode = new Integer(SDMSResourceRequirement.NOKEEP);

		lockmode = (Integer) with.get(ParseStr.S_LOCKMODE);
		if(lockmode == null) lockmode = new Integer(SDMSResourceRequirement.N);

		mapname = (String) with.get(ParseStr.S_MAP_STATUS);
		if(mapname == null)	rsmpId = null;
		else			rsmpId = new Long(1);

		isSticky = (Boolean) with.get(ParseStr.S_STICKY);
		if(isSticky == null) isSticky = Boolean.FALSE;

		exp_interval = null;
		expired = (WithHash) with.get(ParseStr.S_EXPIRED);
		if(expired == null)	exp_mult = null;
		else			exp_mult = new Integer(1);

		states = (Vector) with.get(ParseStr.S_STATUS);
		if(states != null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207161111", "Resource states are not a valid option for system resources"));
		}

		rr = SDMSResourceRequirementTable.table.create(sysEnv,
		                nrId, fId, amount, keepMode, isSticky,
		                rsmpId, exp_mult, exp_interval, lockmode, null );

		rr.check(sysEnv);
	}

}

