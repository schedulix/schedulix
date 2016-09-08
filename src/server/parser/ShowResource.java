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

public class ShowResource extends ShowCommented
{

	private Vector resourcepath;
	private Vector path;
	private Long rId;
	private ObjectURL resource;
	private final static Integer ALLOCATION         = new Integer(1);
	private final static Integer MASTER_RESERVATION = new Integer(2);
	private final static Integer RESERVATION        = new Integer(3);
	private final static Integer AVAILABLE          = new Integer(4);
	private final static Integer BLOCKED            = new Integer(5);
	private final static Integer MASTER_REQUEST     = new Integer(6);
	private final static Integer REQUESTED          = new Integer(7);
	private final static Integer IGNORED            = new Integer(8);
	private final static int[] sortcols = { 11, 13, 1 };

	public ShowResource(ObjectURL ra)
	{
		super();
		resource = ra;
	}

	private Vector fill_desc(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector desc = new Vector();
		desc.add("ID");
		desc.add("NAME");
		desc.add("SCOPENAME");
		desc.add("OWNER");
		desc.add("LINK_ID");
		desc.add("LINK_SCOPE");
		desc.add("BASE_ID");
		desc.add("BASE_SCOPE");
		desc.add("MANAGER_ID");
		desc.add("MANAGER_NAME");
		desc.add("MANAGER_SCOPENAME");
		desc.add("USAGE");
		desc.add("RESOURCE_STATE_PROFILE");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("TAG");
		desc.add("STATE");
		desc.add("TIMESTAMP");
		desc.add("REQUESTABLE_AMOUNT");
		desc.add("DEFINED_AMOUNT");
		desc.add("AMOUNT");
		desc.add("FREE_AMOUNT");
		desc.add("IS_ONLINE");
		desc.add("FACTOR");
		desc.add("TRACE_INTERVAL");
		desc.add("TRACE_BASE");
		desc.add("TRACE_BASE_MULTIPLIER");
		desc.add("TD0_AVG");
		desc.add("TD1_AVG");
		desc.add("TD2_AVG");
		desc.add("LW_AVG");
		desc.add("LAST_WRITE");

		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");
		desc.add("ALLOCATIONS");
		desc.add("PARAMETERS");
		return desc;
	}

	private Long getId(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getId(sysEnv) : r.getId(sysEnv);
	}

	private Long getOwnerId(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getOwnerId(sysEnv) : r.getOwnerId(sysEnv);
	}

	private String getTag(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getTag(sysEnv);
	}

	private Long getRsdId(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getRsdId(sysEnv) : r.getRsdId(sysEnv);
	}

	private Long getRsdTime(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getRsdTime(sysEnv);
	}

	private Integer getRequestableAmount(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getRequestableAmount(sysEnv) : r.getRequestableAmount(sysEnv);
	}

	private Integer getDefinedAmount(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getAmount(sysEnv) : r.getDefinedAmount(sysEnv);
	}

	private Integer getAmount(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getAmount(sysEnv) : r.getAmount(sysEnv);
	}

	private Integer getFreeAmount(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getFreeAmount(sysEnv);
	}

	private Boolean getIsOnline(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getIsOnline(sysEnv) : r.getIsOnline(sysEnv);
	}

	private Float getFactor(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getFactor(sysEnv);
	}

	private Integer getTraceInterval(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getTraceInterval(sysEnv);
	}

	private Integer getTraceBase(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getTraceBase(sysEnv);
	}

	private Integer getTraceBaseMultiplier(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getTraceBaseMultiplier(sysEnv);
	}

	private Float getTd0Avg(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getTd0Avg(sysEnv);
	}

	private Float getTd1Avg(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getTd1Avg(sysEnv);
	}

	private Float getTd2Avg(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getTd2Avg(sysEnv);
	}

	private Float getLwAvg(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getLwAvg(sysEnv);
	}

	private Long getLastWrite(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? null : r.getLastWrite(sysEnv);
	}

	private Long getCreatorUId(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getCreatorUId(sysEnv) : r.getCreatorUId(sysEnv);
	}

	private Long getCreateTs(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getCreateTs(sysEnv) : r.getCreateTs(sysEnv);
	}

	private Long getChangerUId(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getChangerUId(sysEnv) : r.getChangerUId(sysEnv);
	}

	private Long getChangeTs(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getChangeTs(sysEnv) : r.getChangeTs(sysEnv);
	}

	private SDMSPrivilege getPrivileges(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		return r == null ? rt.getPrivileges(sysEnv) : r.getPrivileges(sysEnv);
	}

	private SDMSOutputContainer getVariables(SystemEnvironment sysEnv, SDMSResource r, SDMSResourceTemplate rt)
		throws SDMSException
	{
		SDMSOutputContainer vars = null;
		if (r != null)
			vars = r.getVariables(sysEnv);
		return vars;
	}

	private Vector fill_master(SystemEnvironment sysEnv, SDMSProxy prox, SDMSNamedResource nr, String containerPath)
		throws SDMSException
	{
		SDMSResource r = null;
		SDMSResourceTemplate rt = null;
		r = (SDMSResource) prox;

		Vector v = new Vector();
		Long id = getId(sysEnv, r, rt);

		v.add(getId(sysEnv, r, rt));
		v.add(nr.pathString(sysEnv));
		v.add(containerPath);
		v.add(SDMSGroupTable.getObject(sysEnv, getOwnerId(sysEnv, r, rt)).getName(sysEnv));
		if (r != null) {
			Long linkId = r.getLinkId(sysEnv);
			v.add(linkId);
			if (linkId != null) {
				r = SDMSResourceTable.getObject(sysEnv, linkId);
				SDMSScope s = SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv));
				v.add(s.pathString(sysEnv));
				while (linkId != null) {
					r = SDMSResourceTable.getObject(sysEnv, linkId);
					linkId = r.getLinkId(sysEnv);
				}
				v.add(r.getId(sysEnv));
				s = SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv));
				v.add(s.pathString(sysEnv));
			} else {
				v.add(null);
				v.add(null);
				v.add(null);
			}
			final Long managerId = r.getManagerId(sysEnv);
			v.add(managerId);

			v.add(null);
			v.add(null);
		} else {
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
			v.add(null);
		}

		v.add(nr.getUsageAsString(sysEnv));
		Long rspId = nr.getRspId(sysEnv);
		if(rspId != null) {
			v.add(SDMSResourceStateProfileTable.getObject(sysEnv, rspId).getName(sysEnv));
		} else {
			v.add(null);
		}
		v.add(getCommentContainer(sysEnv, id));
		v.add(getCommentInfoType(sysEnv, id));
		v.add(getTag(sysEnv, r, rt));
		Long rsdId = getRsdId(sysEnv, r, rt);
		if(rsdId != null) {
			v.add(SDMSResourceStateDefinitionTable.getObject(sysEnv, rsdId).getName(sysEnv));
		} else {
			v.add(null);
		}
		Long ts = getRsdTime(sysEnv, r, rt);

		final Date d = new Date();
		if(ts != null &&
		   nr.getUsage(sysEnv).intValue() == SDMSNamedResource.SYNCHRONIZING &&
		   nr.getRspId(sysEnv) != null) {
			d.setTime(ts.longValue());
			v.add(sysEnv.systemDateFormat.format(d));
		} else v.add(null);
		Integer someAmount;
		someAmount = getRequestableAmount(sysEnv, r, rt);
		v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
		someAmount = getDefinedAmount(sysEnv, r, rt);
		v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
		someAmount = getAmount(sysEnv, r, rt);
		v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
		someAmount = getFreeAmount(sysEnv, r, rt);
		v.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
		v.add(getIsOnline(sysEnv, r, rt));
		v.add(getFactor(sysEnv, r, rt));
		v.add(getTraceInterval(sysEnv, r, rt));
		v.add(getTraceBase(sysEnv, r, rt));
		v.add(getTraceBaseMultiplier(sysEnv, r, rt));
		v.add(getTd0Avg(sysEnv, r, rt));
		v.add(getTd1Avg(sysEnv, r, rt));
		v.add(getTd2Avg(sysEnv, r, rt));
		v.add(getLwAvg(sysEnv, r, rt));
		ts = getLastWrite(sysEnv, r, rt);
		if (ts == null || ts.longValue() == 0) v.add(null);
		else {
			d.setTime(ts.longValue());
			v.add(sysEnv.systemDateFormat.format(d));
		}
		try {
			v.add(SDMSUserTable.getObject(sysEnv, getCreatorUId(sysEnv, r, rt)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			v.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(getCreateTs(sysEnv, r, rt).longValue());
		v.add(sysEnv.systemDateFormat.format(d));
		try {
			try {
				v.add(SDMSUserTable.getObject(sysEnv, getChangerUId(sysEnv, r, rt)).getName(sysEnv));
			} catch (NotFoundException nfe) {
				v.add(SDMSScopeTable.getObject(sysEnv, getChangerUId(sysEnv, r, rt)).pathString(sysEnv));
			}
		} catch (NotFoundException nfe) {
			v.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(getChangeTs(sysEnv, r, rt).longValue());
		v.add(sysEnv.systemDateFormat.format(d));
		v.add(getPrivileges(sysEnv, r, rt).toString());

		SDMSOutputContainer s_container = new SDMSOutputContainer(sysEnv, null, fill_rdesc(sysEnv));

		if (r != null) {
			Vector rav = r.getAllocations(sysEnv);
			for(int i = 0; i < rav.size(); i++) {
				SDMSResourceAllocation ra = (SDMSResourceAllocation) rav.get(i);
				if (ra.getAllocationType(sysEnv).intValue() != SDMSResourceAllocation.MASTER_REQUEST)
					s_container.addData(sysEnv, fill_detail(sysEnv, ra, nr, r));
			}
		}

		Collections.sort(s_container.dataset, s_container.getComparator(sysEnv, sortcols));
		v.add(s_container);

		v.add(getVariables(sysEnv, r, rt));

		return v;
	}

	private Vector fill_rdesc(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector rdesc = new Vector();
		rdesc.add("ID");
		rdesc.add("JOBID");
		rdesc.add("MASTERID");
		rdesc.add("JOBTYPE");
		rdesc.add("JOBNAME");
		rdesc.add("AMOUNT");
		rdesc.add("KEEP_MODE");
		rdesc.add("IS_STICKY");
		rdesc.add("STICKY_NAME");
		rdesc.add("STICKY_PARENT");
		rdesc.add("STICKY_PARENT_TYPE");
		rdesc.add("LOCKMODE");
		rdesc.add("RSM_NAME");
		rdesc.add("TYPE");
		rdesc.add("TYPESORT");
		rdesc.add("P");
		rdesc.add("EP");
		rdesc.add("PRIVS");

		return rdesc;
	}

	private Vector fill_detail(SystemEnvironment sysEnv, SDMSResourceAllocation ra, SDMSNamedResource nr, SDMSResource r)
		throws SDMSException
	{
		Vector v = new Vector();
		Long seId;
		long smeId;
		Long oSmeId;
		SDMSSubmittedEntity sme;
		long actVersion;
		SDMSSchedulingEntity se;
		SDMSResourceRequirement rr;

		v.add(ra.getId(sysEnv));
		smeId = ra.getSmeId(sysEnv).longValue();
		oSmeId = new Long(smeId < 0 ? -smeId : smeId);
		v.add(oSmeId);
		sme = SDMSSubmittedEntityTable.getObject(sysEnv, oSmeId);
		v.add(sme.getMasterId(sysEnv));
		actVersion = sme.getSeVersion(sysEnv).longValue();
		seId = sme.getSeId(sysEnv);
		se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, actVersion);

		v.add(se.getTypeAsString(sysEnv));

		v.add(sme.getSubmitPathString(sysEnv,true));
		v.add(ra.getAmount(sysEnv));
		v.add(ra.getKeepModeAsString(sysEnv));
		v.add(ra.getIsSticky(sysEnv));
		v.add(ra.getStickyName(sysEnv));
		Long stickyParent = ra.getStickyParent(sysEnv);
		v.add(stickyParent);
		if (stickyParent == null)
			v.add(null);
		else {
			final SDMSSubmittedEntity spsme = SDMSSubmittedEntityTable.getObject(sysEnv, stickyParent);
			final SDMSSchedulingEntity spse = SDMSSchedulingEntityTable.getObject(sysEnv, spsme.getSeId(sysEnv), actVersion);
			v.add(spse.getTypeAsString(sysEnv));
		}
		v.add(ra.getLockmodeAsString(sysEnv));
		Long rsmpId = ra.getRsmpId(sysEnv);
		if (rsmpId != null) {
			SDMSResourceStateMappingProfile rsmp = SDMSResourceStateMappingProfileTable.getObject(sysEnv, ra.getRsmpId(sysEnv), actVersion);
			v.add(rsmp.getName(sysEnv));
		} else v.add(null);
		int allocType = ra.getAllocationType(sysEnv).intValue();
		if (allocType == SDMSResourceAllocation.REQUEST) {

			Long nrId = nr.getId(sysEnv);
			try {
				rr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(seId, nrId), actVersion);
			} catch (NotFoundException nfe) {
				Long fpId = se.getFpId(sysEnv);
				rr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(fpId, nrId), actVersion);
			}
			if(r.checkAllocate(sysEnv, rr, sme, ra) == SDMSResource.REASON_AVAILABLE) {
				if(SystemEnvironment.sched.isBlocked(sysEnv, oSmeId, r.getId(sysEnv))) {
					v.add("BLOCKED");
					v.add(BLOCKED);
				} else {
					if(ra.getIsSticky(sysEnv).booleanValue()) {
						MasterReservationInfo mri = SystemEnvironment.sched.checkMasterReservation(sysEnv, sme, rr, ra.getStickyParent(sysEnv), r);
						if(mri.canAllocate) {
							v.add("AVAILABLE");
							v.add(AVAILABLE);
						} else {
							v.add("BLOCKED");
							v.add(BLOCKED);
						}
					} else {
						v.add("AVAILABLE");
						v.add(AVAILABLE);
					}
				}
			} else {
				v.add("BLOCKED");
				v.add(BLOCKED);
			}
		} else {
			if (allocType == SDMSResourceAllocation.MASTER_REQUEST) {

				v.add(ra.getAllocationTypeAsString(sysEnv));
				v.add(MASTER_REQUEST);
			} else {
				v.add(ra.getAllocationTypeAsString(sysEnv));
				switch(allocType) {
					case SDMSResourceAllocation.ALLOCATION:		v.add(ALLOCATION);		break;
					case SDMSResourceAllocation.MASTER_RESERVATION: v.add(MASTER_RESERVATION);	break;
					case SDMSResourceAllocation.RESERVATION:	v.add(RESERVATION);		break;
					case SDMSResourceAllocation.IGNORE:		v.add(IGNORED);			break;
				}
			}
		}
		v.add(sme.getPriority(sysEnv));
		v.add(new Integer(SystemEnvironment.sched.getDynPriority(sysEnv, sme)));
		v.add(sme.getPrivileges(sysEnv).toString());

		return v;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResource r = null;
		SDMSResourceTemplate rt = null;
		SDMSNamedResource nr;
		SDMSScope s;
		SDMSFolder f;
		String containerPath;
		SDMSSchedulingEntity se;
		SDMSSubmittedEntity sme;
		SDMSOutputContainer d_container = null;

		SDMSProxy p = resource.resolve(sysEnv);
		r = (SDMSResource) p;
		if (!r.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411719", "Insufficient privileges"));
		nr = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv));
		Long scopeId = r.getScopeId(sysEnv);
		try {
			s = SDMSScopeTable.getObject(sysEnv, scopeId);
			containerPath = s.pathString(sysEnv);
		} catch (NotFoundException nfe) {
			try {
				f = SDMSFolderTable.getObject(sysEnv, scopeId);
				containerPath = f.pathString(sysEnv);
			} catch (NotFoundException nfe2) {
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, scopeId);
				containerPath = sme.getSubmitPathString(sysEnv, true);
			}
		}

		d_container = new SDMSOutputContainer(sysEnv, "Resource", fill_desc(sysEnv),
				fill_master(sysEnv, r == null ? (SDMSProxy) rt : (SDMSProxy) r, nr, containerPath));

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03303071132", "Resource shown"));
	}
}
