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

public class SsRFormatter implements Formatter
{

	public static final String __version = "@(#) $Id: SsRFormatter.java,v 2.20.2.4 2013/06/18 09:49:39 ronald Exp $";

	HashMap namedResources;
	Long folderId;

	public SsRFormatter(HashMap nrMap)
	{
		namedResources = nrMap;
		folderId = null;
	}

	public SsRFormatter(Long id)
	{
		folderId = id;
		namedResources = null;
	}

	public Vector fillHeadInfo()
	{
		Vector desc = new Vector();

		desc.add("ID");

		desc.add("NR_ID");

		desc.add("NAME");

		desc.add("USAGE");

		desc.add("NR_PRIVS");

		desc.add("TAG");
		desc.add("OWNER");

		desc.add("LINK_ID");

		desc.add("LINK_SCOPE");

		desc.add("STATE");

		desc.add("REQUESTABLE_AMOUNT");

		desc.add("AMOUNT");

		desc.add("FREE_AMOUNT");

		desc.add("TOTAL_FREE_AMOUNT");

		desc.add("IS_ONLINE");

		desc.add("FACTOR");

		desc.add("TIMESTAMP");

		desc.add("SCOPE");

		desc.add("MANAGER_ID");

		desc.add("MANAGER_NAME");

		desc.add("MANAGER_SCOPENAME");

		desc.add("HAS_CHILDREN");

		desc.add("POOL_CHILD");

		desc.add("TRACE_INTERVAL");

		desc.add("TRACE_BASE");

		desc.add("TRACE_BASE_MULTIPLIER");

		desc.add("TD0_AVG");

		desc.add("TD1_AVG");

		desc.add("TD2_AVG");

		desc.add("LW_AVG");

		desc.add("LAST_WRITE");

		desc.add("PRIVS");

		return desc;
	}

	public Vector fillVector(SystemEnvironment sysEnv, SDMSProxy co, HashSet parentSet)
		throws SDMSException
	{
		Vector v = new Vector();
		Long scopeId;
		SDMSNamedResource nr;
		Long nrId;
		boolean pool_child = false;

		try {

			nr = (SDMSNamedResource) co;
			nrId = nr.getId(sysEnv);
			if (namedResources != null)
				scopeId = (Long) namedResources.get(nrId);
			else
				scopeId = folderId;
			pool_child = false;
		} catch ( ClassCastException cce) {

			SDMSResource r = (SDMSResource) co;
			nrId = r.getNrId(sysEnv);
			scopeId = r.getScopeId(sysEnv);
			nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
		}
		try {
			SDMSResource r = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, new SDMSKey(nrId, scopeId));
			v.add(r.getId(sysEnv));
			v.add(nrId);
			v.add(nr.pathVector(sysEnv));
			v.add(nr.getUsageAsString(sysEnv));
			v.add(nr.getPrivileges(sysEnv).toString());
			Long linkId = r.getLinkId(sysEnv);
			Long myLinkId = linkId;
			Long ownerId = r.getOwnerId(sysEnv);
			while (linkId != null) {
				r = SDMSResourceTable.getObject(sysEnv, linkId);
				linkId = r.getLinkId(sysEnv);
			}
			v.add(r.getTag(sysEnv));
			v.add(SDMSGroupTable.getObject(sysEnv, ownerId).getName(sysEnv));
			v.add(myLinkId);
			if (myLinkId != null) {
				SDMSResource lr = SDMSResourceTable.getObject(sysEnv, myLinkId);
				SDMSScope s = SDMSScopeTable.getObject(sysEnv, lr.getScopeId(sysEnv));
				v.add(s.pathVector(sysEnv));
			} else {
				v.add(null);
			}
			if(r.getRsdId(sysEnv) != null) {
				v.add(SDMSResourceStateDefinitionTable.getObject(sysEnv, r.getRsdId(sysEnv)).getName(sysEnv));
			} else {
				v.add(null);
			}
			Integer someAmount;
			someAmount = r.getRequestableAmount(sysEnv);
			v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			someAmount = r.getAmount(sysEnv);
			v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			someAmount = r.getFreeAmount(sysEnv);
			v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			v.add(r.getIsOnline(sysEnv));
			v.add(r.getFactor(sysEnv));
			Long ts = r.getRsdTime(sysEnv);

			Date d = new Date();
			if(ts != null &&
			   nr.getUsage(sysEnv).intValue() == SDMSNamedResource.SYNCHRONIZING &&
			   nr.getRspId(sysEnv) != null) {
				d.setTime(ts.longValue());
				v.add(sysEnv.systemDateFormat.format(d));
			} else v.add(null);

			String scope;
			try {
				scope = SDMSScopeTable.getObject(sysEnv, scopeId).pathString(sysEnv);
			} catch (NotFoundException snfe1) {
				try {
					scope = SDMSFolderTable.getObject(sysEnv, scopeId).pathString(sysEnv);
				} catch (NotFoundException snfe2) {
					scope = scopeId.toString();
				}
			}
			v.add(scope);
			Long managerId = r.getManagerId(sysEnv);
			v.add(managerId);
			v.add(null);
			v.add(null);
			v.add(Boolean.FALSE);
			v.add(new Boolean(pool_child));

			v.add(r.getTraceInterval(sysEnv));
			v.add(r.getTraceBase(sysEnv));
			v.add(r.getTraceBaseMultiplier(sysEnv));
			v.add(r.getTd0Avg(sysEnv));
			v.add(r.getTd1Avg(sysEnv));
			v.add(r.getTd2Avg(sysEnv));
			v.add(r.getLwAvg(sysEnv));
			ts = r.getLastWrite(sysEnv);
			if (ts.longValue() == 0) v.add(null);
			else {
				d.setTime(ts.longValue());
				v.add(sysEnv.systemDateFormat.format(d));
			}

			v.add(r.getPrivileges(sysEnv).toString());
		} catch (NotFoundException nfe) {

			v.add(nr.getId(sysEnv));
			v.add(nr.getId(sysEnv));
			v.add(nr.pathVector(sysEnv));
			v.add(nr.getUsageAsString(sysEnv));
			v.add(nr.getPrivileges(sysEnv).toString());
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(Boolean.TRUE);
			v.add(Boolean.FALSE);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add("");
		}
		return v;
	}
}

