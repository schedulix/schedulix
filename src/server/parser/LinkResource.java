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
import de.independit.scheduler.server.output.*;

public class LinkResource extends ManipResource
{

	public final static String __version = "@(#) $Id: LinkResource.java,v 2.4.4.1 2013/03/14 10:24:35 ronald Exp $";

	static final Float fzero = new Float(0);

	private ObjectURL scope;
	private boolean force;

	public LinkResource(ObjectURL ra, ObjectURL s, Boolean f)
	{
		super(ra, false);
		scope = s;
		force = f.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{

		SDMSResource r = (SDMSResource) resource.resolve(sysEnv);
		SDMSScope s = (SDMSScope) scope.resolve(sysEnv);
		SDMSGroup g;
		SDMSUser u;
		Long nrId, sId, gId;
		boolean replaced = false;

		online = Boolean.TRUE;
		gId = r.getOwnerId(sysEnv);
		HashSet groups = sysEnv.cEnv.gid();
		if(!groups.contains(gId) && !groups.contains(SDMSObject.adminGId))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402260151",
			                                   "You cannot create a resource with a group you do not belong to"));

		tag = null;
		traceInterval = null;
		traceBase = null;
		baseMultiplier = new Integer(10 * 60);

		nrId = r.getNrId(sysEnv);
		sId = s.getId(sysEnv);

		check_link(sysEnv, r, sId);

		Long linkTo = r.getId(sysEnv);

		try {
			r = SDMSResourceTable.table.create(sysEnv, nrId, sId, null, gId, linkTo, null, null, null, null, null,
			                                   null, null, null, null, null, null, null, baseMultiplier,
			                                   fzero, fzero, fzero, fzero, new Long(0), new Long(0));
		} catch (DuplicateKeyException dke) {
			if (!force) throw dke;
			replaced = true;
			r = (SDMSResource) SDMSResourceTable.idx_nrId_scopeId.getUnique(sysEnv, new SDMSKey(nrId, sId));
			Vector v = r.getAllocations(sysEnv);
			if (v.size() == 0) {
				if (r.getLinkId(sysEnv) != null) {

					r.setLinkId(sysEnv, linkTo);
				} else {

					r.delete(sysEnv);
					r = SDMSResourceTable.table.create(sysEnv, nrId, sId, null, gId, linkTo, null, null, null, null, null,
					                                   null, null, null, null, null, null, null, baseMultiplier,
					                                   fzero, fzero, fzero, fzero, new Long(0), new Long(0));
				}
			}
		}

		SystemEnvironment.sched.notifyChange(sysEnv, r, sId, SchedulingThread.CREATE);

		if (replaced)
			result.setFeedback(new SDMSMessage(sysEnv, "03202211126", "Resource replaced"));
		else
			result.setFeedback(new SDMSMessage(sysEnv, "03202211126", "Resource created"));
	}

	private void check_link(SystemEnvironment sysEnv, SDMSResource r, Long sId)
	throws SDMSException
	{
		Long scopeId = r.getScopeId(sysEnv);
		SDMSScope s;
		try {
			s = SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv));
		} catch (NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03107151544",
			                               "The resource linked to must reside within a scope"));
		}
		if (sId.equals(s.getId(sysEnv))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03107151545",
			                               "The resource linked to must reside within a different scope"));
		}

		Long linkId = r.getLinkId(sysEnv);
		while (linkId != null) {
			SDMSResource lr = SDMSResourceTable.getObject(sysEnv, linkId);
			Long lsId = lr.getScopeId(sysEnv);
			if (lsId.equals(sId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108021409",
				                               "The resource link cycle detected"));
			}
			linkId = lr.getLinkId(sysEnv);
		}
	}
}

