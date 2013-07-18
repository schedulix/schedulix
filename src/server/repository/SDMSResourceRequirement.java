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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSResourceRequirement extends SDMSResourceRequirementProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSResourceRequirement.java,v 2.2.14.1 2013/03/14 10:25:23 ronald Exp $";

	protected SDMSResourceRequirement(SDMSObject p_object)
	{
		super(p_object);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{

		Vector act_rrs = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, getId(sysEnv));
		for(int i = 0; i < act_rrs.size(); i++) {
			SDMSResourceReqStates rrs = (SDMSResourceReqStates) act_rrs.get(i);
			rrs.delete(sysEnv);
		}

		super.delete(sysEnv);
	}

	public void check(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long nrId = getNrId(sysEnv);
		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
		int usage = nr.getUsage(sysEnv).intValue();
		int lockmode = getLockmode(sysEnv).intValue();
		String condition = getCondition(sysEnv);
		Long rsmpId = getRsmpId(sysEnv);

		if(usage == SDMSNamedResource.STATIC) {
			if(getAmount(sysEnv).intValue() != 0) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304102050",
				                               "Amount option invalid for static Resources ($1)", nr.pathString(sysEnv)));
			}
			if(getKeepMode(sysEnv).intValue() != NOKEEP) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304102045",
				                               "Keep option invalid for static Resources ($1)", nr.pathString(sysEnv)));
			}
		}

		if(usage != SDMSNamedResource.STATIC) {
			if(condition != null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03710301113",
				                               "Condition option invalid for non static Resources ($1)", nr.pathString(sysEnv)));
			}
		}

		if(usage != SDMSNamedResource.SYNCHRONIZING) {
			if(lockmode != SDMSResourceRequirement.N) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304101727",
				                               "Lockmode invalid for non synchronizing Resources ($1)", nr.pathString(sysEnv)));
			}
			if(rsmpId != null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304110943",
				                               "Resource Mapping invalid for non synchronizing Resources ($1)", nr.pathString(sysEnv)));
			}
			if(getIsSticky(sysEnv).booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304101735",
				                               "Sticky invalid for non synchronizing Resources ($1)", nr.pathString(sysEnv)));
			}
			if(getExpiredAmount(sysEnv) != null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304101737",
				                               "Expired invalid for non synchronizing Resources ($1)", nr.pathString(sysEnv)));
			}
			Vector v = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, getId(sysEnv));
			if(v.size() != 0) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304110942",
				                               "Requesting States is invalid for non synchronizing Resources ($1)", nr.pathString(sysEnv)));
			}
			return;
		}

		Long rspId = nr.getRspId(sysEnv);
		Vector v = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, getId(sysEnv));
		if (rspId == null) {
			if (v.size() > 0) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03709071145",
				                               "Requesting States is invalid for Resources ($1) without Resource State Profile", nr.pathString(sysEnv)));
			}
		} else {
			Long initalRsdId = SDMSResourceStateProfileTable.getObject(sysEnv, rspId).getInitialRsdId(sysEnv);
			for (int i = 0; i < v.size(); i ++) {
				Long rsdId = ((SDMSResourceReqStates)v.get(i)).getRsdId(sysEnv);
				if (!(SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId)) ||
				      rsdId.equals(initalRsdId)
				     )) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03709071155",
					                               "Requested State $1 not valid for Resource $2", rsdId, nr.pathString(sysEnv)));
				}
			}
		}

		if(rsmpId != null) {
			if(lockmode != SDMSResourceRequirement.X) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304101619",
				                               "For setting a resource state, an exclusive lock is required ($1)", nr.pathString(sysEnv)));
			}

			if(rspId == null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304110944",
				                               "Resource $1 doesn't have a state profile", nr.pathString(sysEnv)));
			}
			checkMapping(sysEnv, rspId, rsmpId, nr);
		}
	}

	private void checkMapping(SystemEnvironment sysEnv, Long rspId, Long rsmpId, SDMSNamedResource nr)
	throws SDMSException
	{
		Vector v = SDMSResourceStateMappingTable.idx_rsmpId.getVector(sysEnv, rsmpId);
		for(int i = 0; i < v.size(); i++) {
			SDMSResourceStateMapping rsm = (SDMSResourceStateMapping) v.get(i);
			Long toRsdId = rsm.getToRsdId(sysEnv);
			Long fromRsdId = rsm.getFromRsdId(sysEnv);
			Long initialRsdId = SDMSResourceStateProfileTable.getObject(sysEnv, rspId).getInitialRsdId(sysEnv);

			if (fromRsdId != null &&
			    !(SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(fromRsdId, rspId)) ||
			      fromRsdId.equals(initialRsdId)
			     )) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03709071205",
				                               "Resource State Profile $1 of $2 doesn't contain 'from' State $3",
				                               SDMSResourceStateProfileTable.getObject(sysEnv, rspId).getName(sysEnv),
				                               nr.pathString(sysEnv),
				                               SDMSResourceStateDefinitionTable.getObject(sysEnv, toRsdId).getName(sysEnv)));
			}

			if (!SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(toRsdId, rspId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304111022",
				                               "Resource State Profile $1 of $2 doesn't contain 'to' State $3",
				                               SDMSResourceStateProfileTable.getObject(sysEnv, rspId).getName(sysEnv),
				                               nr.pathString(sysEnv),
				                               SDMSResourceStateDefinitionTable.getObject(sysEnv, toRsdId).getName(sysEnv)));
			}
		}
	}
}

