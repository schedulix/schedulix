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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class CreateNamedResource extends Node
{

	public final static String __version = "@(#) $Id: CreateNamedResource.java,v 2.15.2.3 2013/03/19 10:03:52 ronald Exp $";

	private PathVector path;
	private Integer usage;
	private String rspName;
	private boolean replace;
	private String gName;
	private Long gId;
	private Float factor = null;
	private Long inheritPrivs;
	private WithHash with;
	private WithHash parms = null;
	private boolean allTypes = false;

	public CreateNamedResource(PathVector p, WithHash w, Boolean rep)
	{
		super();
		path = p;
		with = w;
		replace = rep.booleanValue();
	}

	private void evaluateWith(SystemEnvironment sysEnv)
	throws SDMSException
	{
		usage = (Integer) with.get(ParseStr.S_USAGE);
		if(usage == null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03402041231", "You must specify the resource usage"));
		if(usage.intValue() == SDMSNamedResource.SYNCHRONIZING) allTypes = true;

		rspName = (String) with.get(ParseStr.S_STATUS_PROFILE);

		if(with.containsKey(ParseStr.S_GROUP) && with.containsKey(ParseStr.S_GROUP_CASCADE)) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03111091015", "It is not allowed to specify the group clause twice"));
		}
		gName = (String) with.get(ParseStr.S_GROUP);
		if(gName == null) gName = (String) with.get(ParseStr.S_GROUP_CASCADE);

		if(gName != null) {
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey (gName, new Long(0))).getId(sysEnv);
		} else {
			gId = SDMSUserTable.getObject(sysEnv, sysEnv.cEnv.uid()).getDefaultGId(sysEnv);
		}
		parms = (WithHash) with.get(ParseStr.S_PARAMETERS);
		final int iu = usage.intValue();
		if(iu == SDMSNamedResource.CATEGORY || iu == SDMSNamedResource.POOL) {
			if(parms != null && parms.size() > 0) {
				throw new CommonErrorException(
				        new SDMSMessage(sysEnv, "03409281206", "Parameters are not allowed for categories")
				);
			}
		}
		int u = usage.intValue();
		if (u == SDMSNamedResource.STATIC || u == SDMSNamedResource.CATEGORY || u == SDMSNamedResource.STATIC)
			factor = null;
		else if (factor == null)
			factor = new Float(1.0);

		if (with.containsKey(ParseStr.S_INHERIT)) {
			inheritPrivs = (Long) with.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
		} else
			inheritPrivs = null;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long rsp_id;
		Long parentId;
		String name;

		evaluateWith(sysEnv);
		final int iu = usage.intValue();

		if(iu == SDMSNamedResource.POOL)
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_RESOURCE_POOLS);
		if(factor != null && !(factor.equals(new Float(1.0))))
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_RESOURCE_TRACING);

		if(rspName != null) {
			SDMSResourceStateProfile rsp = SDMSResourceStateProfileTable.idx_name_getUnique(sysEnv, rspName);
			rsp_id = rsp.getId(sysEnv);
		} else {
			rsp_id = null;
		}
		name = (String) path.remove(path.size() - 1);
		if (path.size() == 0) {

			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061335", "The category RESOURCE cannot be created"));
		}

		SDMSNamedResource n = SDMSNamedResourceTable.getNamedResource(sysEnv, path);
		parentId = n.getId(sysEnv);

		long lpriv = (inheritPrivs == null ? n.getPrivilegeMask() : inheritPrivs.longValue());
		if((n.getPrivilegeMask() & lpriv) != lpriv) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061331", "Incompatible grant"));
		}

		inheritPrivs = new Long(lpriv);

		try {
			n = SDMSNamedResourceTable.table.create(sysEnv, name, gId, parentId, usage, rsp_id, factor, inheritPrivs);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				path.add(name);
				AlterNamedResource anr = new AlterNamedResource(new ObjectURL(new Integer(Parser.NAMED_RESOURCE), path), with, Boolean.FALSE);
				anr.setEnv(env);
				anr.go(sysEnv);
				result = anr.result;
				return;
			} else {
				throw dke;
			}
		}

		if(iu != SDMSNamedResource.CATEGORY && iu != SDMSNamedResource.POOL)
			n.createParameters(sysEnv, parms, allTypes);

		SystemEnvironment.sched.notifyChange(sysEnv, n, SchedulingThread.CREATE);
		result.setFeedback(new SDMSMessage(sysEnv,"03201212129", "Named Resource created"));
	}

}

