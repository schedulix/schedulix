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
import de.independit.scheduler.server.util.*;

public class AlterResource extends ManipResource
{

	public final static String __version = "@(#) $Id: AlterResource.java,v 2.21.4.4 2013/03/22 14:48:02 ronald Exp $";

	private WithHash with;
	private SDMSProxy pp;

	public AlterResource(ObjectURL ra, WithHash w, Boolean n)
	{
		super(ra, n);
		with = w;
	}

	public AlterResource(SDMSProxy p, WithHash w, Boolean n)
	{
		super();
		with = w;
		pp = p;
	}

	private void collectWith(SystemEnvironment sysEnv)
		throws SDMSException
	{
		status = (String) with.get(ParseStr.S_STATUS);
		requestableAmount = (Integer) with.get(ParseStr.S_REQUESTABLE_AMOUNT);
		amount = (Integer) with.get(ParseStr.S_AMOUNT);
		online = (Boolean) with.get(ParseStr.S_ONLINE);
		groupname = (String) with.get(ParseStr.S_GROUP);
		parms = (WithHash) with.get(ParseStr.S_PARAMETERS);
	}

	private void alterResource(SystemEnvironment sysEnv, SDMSResource r)
		throws SDMSException
	{
		int notify = SchedulingThread.ALTER;
		if(online != null) {
			if (r.getIsOnline(sysEnv).booleanValue() != online.booleanValue()) {
				r.setIsOnline(sysEnv, online);
				notify = SchedulingThread.OFFLINE_ONLINE;
			}
		}

		if(status != null) {
			final SDMSResourceStateDefinition rsd = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, status);
			final Long rsdId = rsd.getId(sysEnv);
			final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv));
			final Long rspId = nr.getRspId(sysEnv);
			if (rspId == null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03604041545", "Resource does not have a profile"));
			}
			if(!SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId)))
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03604041633", "Resource state is not contained within the resource state profile"));
			r.setRsdId(sysEnv, rsdId);
		}

		if(requestableAmount != null) {
			if(requestableAmount.intValue() == -1) requestableAmount = null;
			Integer ora = r.getRequestableAmount(sysEnv);
			if ((ora == null && requestableAmount != null) ||
			    (ora != null && requestableAmount == null) ||
			    (ora != null && requestableAmount != null && ora.intValue() != requestableAmount.intValue())
			   ) {
				r.setRequestableAmount(sysEnv, requestableAmount);
				notify = SchedulingThread.ALTER_REQAMOUNT;
			}
		}

		if(amount != null) {
			if(amount.intValue() == -1) amount = null;
			Integer oam = r.getDefinedAmount(sysEnv);
			if (oam != null && amount != null && oam.intValue() > amount.intValue())
				notify = SchedulingThread.ALTER_REQAMOUNT;
			r.setDefinedAmount(sysEnv, amount);
			if (r.getManagerId(sysEnv) == null)
				r.setAmount(sysEnv, amount);
		}

		if(with.containsKey(ParseStr.S_TOUCH)) {
			final DateTime dt = (DateTime) with.get(ParseStr.S_TOUCH);
			final Long rsdTime;
			if(dt != null) {
				if(dt.year == -1) {
					dt.setMissingFieldsFromNow();
				} else
					dt.fixToMinDate();
				rsdTime = Long.valueOf(dt.toDate().getTime());
			} else {
				rsdTime = Long.valueOf(System.currentTimeMillis());
			}
			r.setRsdTime(sysEnv, rsdTime);
		}

		if(groupname != null) {
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(groupname, SDMSConstants.lZERO)).getId(sysEnv);
			ChownChecker.check(sysEnv, gId, r.getOwnerId(sysEnv));
			r.setOwnerId(sysEnv, gId);
		}

		if(with.containsKey(ParseStr.S_PARAMETERS)) {
			r.alterVariables(sysEnv, parms, Long.MAX_VALUE);
		}

		SystemEnvironment.sched.notifyChange(sysEnv, r, sId, notify);
	}

	private void alterResourceTemplate(SystemEnvironment sysEnv, SDMSResourceTemplate rt)
	throws SDMSException
	{
		if(online != null) {
			rt.setIsOnline(sysEnv, online);
		}

		if(status != null) {
			final SDMSResourceStateDefinition rsd = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, status);
			final Long rsdId = rsd.getId(sysEnv);
			final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, rt.getNrId(sysEnv));
			final Long rspId = nr.getRspId(sysEnv);
			final SDMSResourceStateProfile rsp;
			if (rspId == null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03604041546", "Resource does not have a profile"));
			}
			rsp = SDMSResourceStateProfileTable.getObject(sysEnv, rspId);
			if(!rsdId.equals(rsp.getInitialRsdId(sysEnv)) &&
			    !SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId)))
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03604041634", "Resource state is not contained within the resource state profile"));
			rt.setRsdId(sysEnv, rsdId);
		}

		if(requestableAmount != null) {
			if(requestableAmount.intValue() == -1) requestableAmount = null;
			rt.setRequestableAmount(sysEnv, requestableAmount);
		}

		if(amount != null) {
			if(amount.intValue() == -1) amount = null;
			rt.setAmount(sysEnv, amount);
		}

		if(groupname != null) {
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(groupname, SDMSConstants.lZERO)).getId(sysEnv);
			ChownChecker.check(sysEnv, gId, rt.getOwnerId(sysEnv));
			rt.setOwnerId(sysEnv, gId);
		}

		if(with.containsKey(ParseStr.S_PARAMETERS)) {
			rt.alterVariables(sysEnv, parms, Long.MAX_VALUE);
		}
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResource r;
		SDMSResourceTemplate rt;

		collectWith(sysEnv);
		if (resource != null)
			pp = resource.resolve(sysEnv);

		if (pp instanceof SDMSResource) {
			r = (SDMSResource) pp;
			Long linkId = r.getLinkId(sysEnv);
			while (linkId != null) {
				r = SDMSResourceTable.getObject(sysEnv, linkId);
				linkId = r.getLinkId(sysEnv);
			}
			alterResource(sysEnv, r);
		} else {
			rt = (SDMSResourceTemplate) pp;
			alterResourceTemplate(sysEnv, rt);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03202220027", "Resource altered"));
	}
}
