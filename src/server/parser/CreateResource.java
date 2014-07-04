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
import de.independit.scheduler.server.output.*;

public class CreateResource extends ManipResource
{

	public final static String __version = "@(#) $Id: CreateResource.java,v 2.14.4.5 2013/03/26 11:59:17 ronald Exp $";

	static final Float fzero = new Float(0);

	private WithHash with;
	private boolean replace;
	private WithHash parms;

	public CreateResource(Vector rp, Vector p, WithHash w, Boolean r)
	{
		super();
		resourcepath = rp;
		path = p;
		with = w;
		rId = null;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{

		SDMSNamedResource nr;
		SDMSResource r;
		SDMSScope s;
		SDMSFolder f;
		SDMSSchedulingEntity se;
		SDMSResourceStateDefinition rsd = null;
		SDMSGroup g;
		SDMSUser u;
		Long rsdId = null;
		Long nrId;
		Long sId = null;
		Long gId;
		boolean scopeResource = true;

		status = (String) with.get(ParseStr.S_STATUS);
		requestableAmount = (Integer) with.get(ParseStr.S_REQUESTABLE_AMOUNT);
		amount = (Integer) with.get(ParseStr.S_AMOUNT);
		online = (Boolean) with.get(ParseStr.S_ONLINE);
		if(online == null) online = Boolean.FALSE;
		groupname = (String) with.get(ParseStr.S_GROUP);
		parms = (WithHash) with.get(ParseStr.S_PARAMETERS);

		if(groupname != null) {
			g = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(groupname, new Long(0)));
			gId = g.getId(sysEnv);
		} else {
			u = SDMSUserTable.getObject(sysEnv, sysEnv.cEnv.uid());
			gId = u.getDefaultGId(sysEnv);
		}
		HashSet groups = sysEnv.cEnv.gid();
		if(!groups.contains(gId) && !groups.contains(SDMSObject.adminGId))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402260151",
				"You cannot put a resource into a group you do not belong to"));

		nr = SDMSNamedResourceTable.getNamedResource(sysEnv, resourcepath);
		nrId = nr.getId(sysEnv);

		try {
			s = SDMSScopeTable.getScope(sysEnv, path);
			sId = s.getId(sysEnv);
		} catch (NotFoundException nfe) {
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_FOLDER_RESOURCES);
		}

		if (baseMultiplier == null) baseMultiplier = new Integer(10 * 60);

		if(replace) {
			SDMSKey k = new SDMSKey(nrId, sId);
			if(SDMSResourceTable.idx_nrId_scopeId.containsKey(sysEnv, k)) {
				SDMSProxy p = null;
				try {
					p = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, k);
				} catch (NotFoundException nfe) {
					p = SDMSResourceTemplateTable.idx_nrId_seId_getUnique(sysEnv, k);
				}
				AlterResource ar = new AlterResource(p, with, Boolean.FALSE);
				ar.setEnv(env);
				ar.go(sysEnv);
				result = ar.result;
				return;
			}
		}

		if(status != null) {
			rsd = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, status);
			rsdId = rsd.getId(sysEnv);
		}

		if ((requestableAmount == null) && (amount == null)) {

			requestableAmount = new Integer(-1);
		}
		if(requestableAmount == null) {

			requestableAmount = amount;
		}
		if(amount == null) {

			amount = requestableAmount;
		}
		rsdId = check_resource(sysEnv, nr, rsdId, requestableAmount, amount, scopeResource);

		Date dts = new Date();
		Long ts = new Long (dts.getTime());

		if (requestableAmount.intValue() == -1) {

			requestableAmount = null;
		}
		if (amount.intValue() == -1) {

			amount = null;
		}
		r = SDMSResourceTable.table.create(sysEnv, nrId, sId, null,  gId, null, null, null, rsdId, ts, amount, requestableAmount,
						amount, amount, online, factor, traceInterval, traceBase, baseMultiplier,
						fzero, fzero, fzero, fzero, new Long(0), new Long(0));
		r.createVariables(sysEnv, parms);

		SystemEnvironment.sched.notifyChange(sysEnv, r, sId, SchedulingThread.CREATE);
		result.setFeedback(new SDMSMessage(sysEnv, "03202211126", "Resource created"));
	}

	private Long check_resource(SystemEnvironment sysEnv, SDMSNamedResource nr, Long rsdId, Integer requestableAmount, Integer amount, boolean scopeResource)
		throws SDMSException
	{
		SDMSResourceStateProfile rsp;
		Long rspId;

		rspId = nr.getRspId(sysEnv);
		if(rspId == null) {
			if(rsdId != null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211132", "Named Resource $1 is stateless", resourcepath));
			}
		} else {
			rsp = SDMSResourceStateProfileTable.getObject(sysEnv, rspId);
			if(rsdId == null) {

				if(rsp.getInitialRsdId(sysEnv) == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211217", "Resource must have an initial state"));
				} else {
					rsdId = rsp.getInitialRsdId(sysEnv);
				}
			} else {

				if(!SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId))) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211221", "Resource state is not defined in the profile $1",
						rsp.getName(sysEnv)));
				}
			}
		}

		if(nr.getUsage(sysEnv).intValue() == SDMSNamedResource.CATEGORY) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206250052", "A resource category cannot be allocated"));
		}
		if(nr.getUsage(sysEnv).intValue() == SDMSNamedResource.STATIC) {
			if(amount != null && amount.intValue() != -1) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211140", "A static resource cannot have an amount other than infinite"));
			}
			if(requestableAmount != null && requestableAmount.intValue() != -1) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02402030932", "A static resource cannot have an requestable amount other than infinite"));
			}
			if(!scopeResource) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03410051436", "A static resource can only be created within a scope or jobserver"));
			}
		}
		return rsdId;
	}
}

