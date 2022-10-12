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

package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.*;

public class SDMSResource extends SDMSResourceProxyGeneric
	implements SDMSSysResource, SDMSOwnedObject
{

	public final static String __version = "@(#) $Id: SDMSResource.java,v 2.50.2.5 2013/03/25 10:19:14 dieter Exp $";

	private final static VariableResolver RVR = new ResourceVariableResolver();
	private static final Object protectTraceInsert = new Object();
	private static PreparedStatement traceInsert = null;

	protected SDMSResource(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		String s;
		final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, getNrId(sysEnv));

		s = nr.getName(sysEnv);

		return s;
	}

	public SDMSResource getBase(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResource r = this;
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			do {
				r = SDMSResourceTable.getObject(sysEnv, linkId);
				linkId = r.getLinkId(sysEnv);
			} while (linkId != null);
		}
		return r;
	}

	public Integer getUsage(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, getNrId(sysEnv));
		return nr.getUsage(sysEnv);
	}

	public String getTag (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getTag(sysEnv) : getBase(sysEnv).getTag(sysEnv));
	}

	public Long getRsdId (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getRsdId(sysEnv) : getBase(sysEnv).getRsdId(sysEnv));
	}

	public Long getRsdTime (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getRsdTime(sysEnv) : getBase(sysEnv).getRsdTime(sysEnv));
	}

	public void setRsdTime (SystemEnvironment sysEnv, Long p_rsdTime)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId == null) {
			super.setRsdTime(sysEnv, p_rsdTime);
		} else {
			getBase(sysEnv).setRsdTime(sysEnv, p_rsdTime);
		}
	}
	public Integer getDefinedAmount (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getDefinedAmount(sysEnv) : getBase(sysEnv).getDefinedAmount(sysEnv));
	}

	public void setDefinedAmount (SystemEnvironment sysEnv, Integer p_definedAmount)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId == null) {
			super.setDefinedAmount(sysEnv, p_definedAmount);
		} else {
			getBase(sysEnv).setDefinedAmount(sysEnv, p_definedAmount);
		}
	}
	public Integer getRequestableAmount (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getRequestableAmount(sysEnv) : getBase(sysEnv).getRequestableAmount(sysEnv));
	}

	public Integer getAmount (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getAmount(sysEnv) : getBase(sysEnv).getAmount(sysEnv));
	}

	public Integer getFreeAmount (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getFreeAmount(sysEnv) : getBase(sysEnv).getFreeAmount(sysEnv));
	}

	public Boolean getIsOnline (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getIsOnline(sysEnv) : getBase(sysEnv).getIsOnline(sysEnv));
	}

	public void setIsOnline (SystemEnvironment sysEnv, Boolean p_isOnline)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId == null) {
			super.setIsOnline(sysEnv, p_isOnline);
		} else {
			getBase(sysEnv).setIsOnline(sysEnv, p_isOnline);
		}
	}

	public Long getLastEval (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getLastEval(sysEnv) : getBase(sysEnv).getLastEval(sysEnv));
	}

	public void setLastEval (SystemEnvironment sysEnv, Long p_lastEval)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId == null) {
			super.setLastEval(sysEnv, p_lastEval);
		} else {
			getBase(sysEnv).setLastEval(sysEnv, p_lastEval);
		}
	}
	public Long getLastWrite (SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		return (linkId == null ? super.getLastWrite(sysEnv) : getBase(sysEnv).getLastWrite(sysEnv));
	}

	public void setLastWrite (SystemEnvironment sysEnv, Long p_lastWrite)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId == null) {
			super.setLastWrite(sysEnv, p_lastWrite);
		} else {
			getBase(sysEnv).setLastWrite(sysEnv, p_lastWrite);
		}
	}

	public Integer getTotalFreeAmount(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return getFreeAmount(sysEnv);
	}

	public int checkAllocate(SystemEnvironment sysEnv, SDMSResourceRequirement rr, SDMSSubmittedEntity sme, SDMSResourceAllocation ra)
		throws SDMSException
	{
		SDMSResource r = this.getBase(sysEnv);
		return r.checkAllocate(sysEnv, rr, sme, ra, 0, new Lockmode());
	}

	public int checkAllocate(SystemEnvironment sysEnv, SDMSResourceRequirement rr, SDMSSubmittedEntity sme, SDMSResourceAllocation ra, int waitAmount, Lockmode waitLock)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).checkAllocate(sysEnv, rr, sme, ra, waitAmount, waitLock);

		int rc = REASON_AVAILABLE;

		if(!getIsOnline(sysEnv).booleanValue())			return REASON_OFFLINE;

		Long stickyParent;
		if (ra != null) stickyParent = ra.getStickyParent(sysEnv);
		else		stickyParent = null;

		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, getNrId(sysEnv));
		int usage = nr.getUsage(sysEnv).intValue();
		if(usage == SDMSNamedResource.STATIC) return REASON_AVAILABLE;

		if(usage == SDMSNamedResource.SYNCHRONIZING) {
			if(!syncCheckState(sysEnv, rr))					return REASON_STATE;
			if(!syncCheckExpired(sysEnv, rr, nr, sme))			return REASON_EXPIRE;
			if(!checkAmount(sysEnv, rr, stickyParent, waitAmount))		rc |= REASON_AMOUNT;
			if(!syncCheckLockmode(sysEnv, rr, stickyParent, waitLock))	rc |= REASON_LOCKMODE;
		} else {
			float factor = 1;
			final int rrAmount = rr.getAmount(sysEnv).intValue();
			final int cAmount = (int) Math.ceil(rrAmount * factor);
			if(!checkAmount(sysEnv, cAmount, rrAmount, waitAmount)) rc |= REASON_AMOUNT;
		}
		return rc;
	}

	public boolean checkAmount(SystemEnvironment sysEnv, SDMSResourceRequirement rr, Long stickyParent, int waitAmount)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).checkAmount(sysEnv, rr, stickyParent, waitAmount);

		final Integer rrAmount = rr.getAmount(sysEnv);
		final Integer rAmount = getFreeAmount(sysEnv);
		final Integer rqAmount = getRequestableAmount(sysEnv);
		int totalAmount;
		if (rrAmount == null || rrAmount.intValue() == 0) {
			return true;
		}
		final int irrAmount = rrAmount.intValue();
		if(rqAmount != null && rqAmount.intValue() < irrAmount) {
			return false;
		}
		if (rAmount == null) {
			return true;
		}
		final int dAmount = rAmount.intValue() - waitAmount;
		float factor = 1;
		final int crrAmount = (int) Math.ceil(irrAmount * factor);
		if(crrAmount > dAmount && rr.getIsSticky(sysEnv).booleanValue()) {
			try {
				Long nStickyParent = Long.valueOf(-stickyParent.longValue());
				SDMSResourceAllocation mra = (SDMSResourceAllocation)
					SDMSResourceAllocationTable.idx_smeId_rId_stickyName.getUnique(
						sysEnv, new SDMSKey(nStickyParent, getId(sysEnv), rr.getStickyName(sysEnv)));

				if (mra.getAllocationType(sysEnv).intValue() == SDMSResourceAllocation.MASTER_REQUEST) return false;

				totalAmount = Math.max(mra.getAmount(sysEnv).intValue(), 0) + Math.max (dAmount, 0);
				if(totalAmount >= crrAmount) {
					return true;
				}
				return false;
			} catch(NotFoundException nfe) {
				return false;
			}
		}

		return true;
	}

	public boolean checkAmount(SystemEnvironment sysEnv, int amount, int uncorrectedAmount, int waitAmount)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).checkAmount(sysEnv, amount, uncorrectedAmount, waitAmount);

		Integer rAmount = getFreeAmount(sysEnv);
		Integer rqAmount = getRequestableAmount(sysEnv);
		if(rqAmount != null && rqAmount.intValue() < uncorrectedAmount) {
			return false;
		}
		if(rAmount == null) {
			return true;
		}
		int irAmount = rAmount.intValue();
		if (
			(amount > irAmount - waitAmount)
		)
			return false;
		return true;
	}

	public boolean syncCheckState(SystemEnvironment sysEnv, SDMSResourceRequirement rr)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).syncCheckState(sysEnv, rr);

		Long rsdId = getRsdId(sysEnv);
		Vector rsdv = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, rr.getId(sysEnv));
		for(int j = 0; j < rsdv.size(); j++) {
			SDMSResourceReqStates rrs = (SDMSResourceReqStates) rsdv.get(j);
			if(rrs.getRsdId(sysEnv).equals(rsdId)) return true;
		}
		if(rsdv.size() == 0) {
			return true;
		}
		return false;
	}

	public boolean syncCheckLockmode(SystemEnvironment sysEnv, SDMSResourceRequirement rr, Long stickyParent, Lockmode waitLockmode)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).syncCheckLockmode(sysEnv, rr, stickyParent, waitLockmode);

		String rrStickyName = rr.getStickyName(sysEnv);

		Vector rav = SDMSResourceAllocationTable.idx_rId.getVector(sysEnv, getId(sysEnv));
		Integer lock = rr.getLockmode(sysEnv);
		boolean isMasterReservation = false;

		if(lock == null) lock = SDMSConstants.RR_N;
		for(int j = 0; j < rav.size(); j++) {
			SDMSResourceAllocation ra = (SDMSResourceAllocation) rav.get(j);
			int allocType = ra.getAllocationType(sysEnv).intValue();
			if(allocType == SDMSResourceAllocation.REQUEST) continue;
			if(allocType == SDMSResourceAllocation.MASTER_REQUEST) continue;
			if(allocType == SDMSResourceAllocation.IGNORE) continue;
			if(stickyParent != null && ra.getSmeId(sysEnv).longValue() == - stickyParent.longValue()) {
				String raStickyName = ra.getStickyName(sysEnv);
				if ((rrStickyName == null && raStickyName == null) ||
				    (rrStickyName != null && rrStickyName.equals(raStickyName))) {
					isMasterReservation = true;
					continue;
				}
			}
			if(! Lockmode.isCompatible(lock, ra.getLockmode(sysEnv))) {
				return false;
			}
		}

		if(! Lockmode.isCompatible(waitLockmode, lock) && !isMasterReservation) {
			return false;
		}
		return true;
	}

	public boolean syncCheckLockmode(SystemEnvironment sysEnv, int lockmode, Lockmode waitLockmode)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).syncCheckLockmode(sysEnv, lockmode, waitLockmode);

		Vector rav = SDMSResourceAllocationTable.idx_rId.getVector(sysEnv, getId(sysEnv));
		Integer lock = Integer.valueOf(lockmode);
		if(! Lockmode.isCompatible(waitLockmode, lock)) {
			return false;
		}
		for(int j = 0; j < rav.size(); j++) {
			SDMSResourceAllocation ra = (SDMSResourceAllocation) rav.get(j);
			int allocType = ra.getAllocationType(sysEnv).intValue();
			if(allocType == SDMSResourceAllocation.REQUEST) continue;
			if(allocType == SDMSResourceAllocation.MASTER_REQUEST) continue;
			if(allocType == SDMSResourceAllocation.IGNORE) continue;
			if(! Lockmode.isCompatible(lock, ra.getLockmode(sysEnv))) {
				return false;
			}
		}
		return true;
	}

	public boolean syncCheckExpired(SystemEnvironment sysEnv, SDMSResourceRequirement rr, SDMSNamedResource nr, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).syncCheckExpired(sysEnv, rr, nr, sme);

		Integer expBase = rr.getExpiredBase(sysEnv);
		if(expBase != null) {
			if(nr.getRspId(sysEnv) != null) {
				boolean ignoreOnRerun = rr.getIgnoreOnRerun(sysEnv).booleanValue();
				if (ignoreOnRerun && (sme.getRerunSeq(sysEnv).intValue() > 0)) {
				} else {
					long rsdTime = getRsdTime(sysEnv).longValue();
					long expTime = rr.getExpiredAmount(sysEnv).longValue();
					switch(expBase.intValue()) {
						case SDMSInterval.MINUTE:
							expTime *= SDMSInterval.MINUTE_DUR;
							break;
						case SDMSInterval.HOUR:
							expTime *= SDMSInterval.HOUR_DUR;
							break;
						case SDMSInterval.DAY:
							expTime *= SDMSInterval.DAY_DUR;
							break;
						case SDMSInterval.WEEK:
							expTime *= SDMSInterval.WEEK_DUR;
							break;
						case SDMSInterval.MONTH:
							expTime *= SDMSInterval.MONTH_DUR;
							break;
						case SDMSInterval.YEAR:
							expTime *= SDMSInterval.YEAR_DUR;
							break;
					}
					long ts;
					long dts = (new java.util.Date()).getTime();
					if(expTime == 0) {
						ts = sme.getSyncTs(sysEnv).longValue() - rsdTime;
					} else {
						if(expTime > 0)
							ts = dts - expTime - rsdTime;
						else
							ts = rsdTime - expTime - dts;
					}
					if(ts > 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void setAmount(SystemEnvironment sysEnv, Integer amount)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).setAmount(sysEnv, amount);
			return;
		}

		Integer oAmount = getAmount(sysEnv);
		if (oAmount == null && amount == null) return;
		if (oAmount != null && oAmount.equals(amount)) return;

		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, getNrId(sysEnv));
		if(nr.getUsage(sysEnv).intValue() == SDMSNamedResource.STATIC) {
			if(amount != null)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211141", "A static resource cannot have an amount other than infinite"));
		}
		super.setAmount(sysEnv, amount);

		if(amount != null) {
			final int a = amount.intValue();
			Integer fAmount;
			if (oAmount == null) {
				Vector v = SDMSResourceAllocationTable.idx_rId.getVector(sysEnv, getId(sysEnv));
				int size = v.size();
				int aAmount = 0;
				for (int i = 0; i < size; i ++) {
					SDMSResourceAllocation ra = (SDMSResourceAllocation)v.get(i);
					Integer raAmount = ra.getAmount(sysEnv);
					if ( raAmount != null && raAmount.intValue() > 0) {
						aAmount += raAmount.intValue();
					}
				}
				fAmount = Integer.valueOf (a - aAmount);
			} else {
				fAmount = getFreeAmount(sysEnv);
				fAmount = Integer.valueOf(fAmount.intValue() + a - oAmount.intValue());
			}
			setFreeAmount(sysEnv, fAmount, oAmount);
		} else {
			setFreeAmount(sysEnv, null, oAmount);
		}

		SystemEnvironment.sched.notifyChange(sysEnv, this, this.getScopeId(sysEnv), SchedulingThread.ALTER);
	}

	public void setRequestableAmount(SystemEnvironment sysEnv, Integer amount)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).setRequestableAmount(sysEnv, amount);
			return;
		}

		Integer oAmount = getRequestableAmount(sysEnv);
		if (oAmount == null && amount == null) return;
		if (oAmount != null && oAmount.equals (amount)) return;
		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, getNrId(sysEnv));
		if(nr.getUsage(sysEnv).intValue() == SDMSNamedResource.STATIC) {
			if(amount != null)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211142",
							"A static resource cannot have a requestable amount other than infinite"));
		}

		SystemEnvironment.sched.notifyChange(sysEnv, this, this.getScopeId(sysEnv), SchedulingThread.ALTER_REQAMOUNT);
		super.setRequestableAmount(sysEnv, amount);
	}

	public void setRsdId(SystemEnvironment sysEnv, Long rsdId)
		throws SDMSException
	{
		this.getBase(sysEnv).setRsdId(sysEnv, rsdId, null);
		return;
	}

	public void setRsdId(SystemEnvironment sysEnv, Long rsdId, SDMSSubmittedEntity causeSme)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).setRsdId(sysEnv, rsdId, causeSme);
			return;
		}

		Long nrId = getNrId(sysEnv);

		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
		Long rspId = nr.getRspId(sysEnv);
		if(rspId == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211133", "Named Resource $1 is stateless", nr.pathVector(sysEnv).toString()));
		} else {
			if(!SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId))) {
				SDMSResourceStateProfile rsp = SDMSResourceStateProfileTable.getObject(sysEnv, rspId);
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202211222", "Resource state is not defined in the profile $1",
							rsp.getName(sysEnv)));
			}
		}

		Vector v = SDMSTriggerTable.idx_fireId.getVector(sysEnv, getId(sysEnv));
		v.addAll(SDMSTriggerTable.idx_fireId.getVector(sysEnv, nrId));
		for(int i = 0; i < v.size(); i++) {
			Long oldRsdId = getRsdId(sysEnv);
			SDMSTrigger t = (SDMSTrigger) v.get(i);
			t.trigger(sysEnv, this, oldRsdId, rsdId, causeSme);
		}
		super.setRsdId(sysEnv, rsdId);
	}

	private int collectChildren(SystemEnvironment sysEnv, Vector sv, SDMSScope parent, Long nrId)
	throws SDMSException
	{
		int count = 0;
		Vector cv = SDMSScopeTable.idx_parentId.getVector(sysEnv, parent.getId(sysEnv));
		for (int i = 0; i < cv.size(); ++i) {
			SDMSScope cs = (SDMSScope) cv.get(i);
			if (SDMSResourceTable.idx_nrId_scopeId.containsKey(sysEnv, new SDMSKey(nrId, cs.getId(sysEnv)))) {
				continue;
			}
			int tmpcount = collectChildren(sysEnv, sv, cs, nrId);
			if (tmpcount == 0) {
				sv.add(cs);
				count++;
			} else
				count += tmpcount;
		}

		return count;
	}

	private boolean isStaticAndJobsActive(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long nrId;
		SDMSScope s;

		if (getUsage(sysEnv).intValue() != SDMSNamedResource.STATIC)
			return false;

		nrId = getNrId(sysEnv);
		s = SDMSScopeTable.getObject(sysEnv, getScopeId(sysEnv));

		Vector sv = new Vector();
		if (collectChildren(sysEnv, sv, s, nrId) == 0) {
			sv.add(s);
		}

		for (int i = 0; i < sv.size(); ++i) {
			SDMSScope ls = (SDMSScope) sv.get(i);
			if (s.hasActiveJobs(sysEnv))
				return true;

		}

		return false;
	}

	public void delete(SystemEnvironment sysEnv)
		throws SDMSException
	{
		delete(sysEnv, false);
	}

	public void delete(SystemEnvironment sysEnv, boolean force)
		throws SDMSException
	{
		Long sId = getScopeId(sysEnv);
		Long rId = getId(sysEnv);
		if (getManagerId(sysEnv) != null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03603151436", "Resource is member in an active pool"));
		Vector v = SDMSResourceAllocationTable.idx_rId.getVector(sysEnv, rId);
		int size = v.size();
		if(size > 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206242252", "Resource is in use"));
		}
		if (isStaticAndJobsActive(sysEnv))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202101013", "Resource is in use"));

		Vector lr = SDMSResourceTable.idx_linkId.getVector(sysEnv, rId);
		for (int i = 0; i < lr.size(); ++i) {
			SDMSResource r  = (SDMSResource) lr.get(i);
			r.delete(sysEnv, force);
		}

		Vector lt = SDMSTriggerTable.idx_fireId.getVector(sysEnv, rId);
		for (int i = 0; i < lt.size(); ++i) {
			SDMSTrigger t  = (SDMSTrigger) lt.get(i);
			t.delete(sysEnv);
		}

		killVariables(sysEnv);
		super.delete(sysEnv);
		try {
			SDMSSubmittedEntity s = SDMSSubmittedEntityTable.getObject(sysEnv, sId);

		} catch (NotFoundException nfe) {
			SystemEnvironment.sched.notifyChange(sysEnv, (SDMSResource) null, sId, SchedulingThread.DELETE);
		}
	}

	public Vector getAllocations(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector result;
		Long id = getId(sysEnv);

		result = SDMSResourceAllocationTable.idx_rId.getVector(sysEnv, id);
		Vector links = SDMSResourceTable.idx_linkId.getVector(sysEnv, id);
		for (int i = 0; i < links.size(); ++i) {
			SDMSResource r = (SDMSResource) links.get(i);
			result.addAll(r.getAllocations(sysEnv));
		}

		return result;
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);

		Long nrId;
		Long sId;
		long ptmp;

		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
		if ((p & checkPrivs) == checkPrivs) return checkPrivs;

		if(sysEnv.cEnv.isUser() && ((checkPrivs & SDMSPrivilege.CREATE) == SDMSPrivilege.CREATE) && ((p & SDMSPrivilege.CREATE) == SDMSPrivilege.NOPRIVS)) {
			nrId = getNrId(sysEnv);
			final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
			ptmp = nr.getPrivileges(sysEnv, SDMSPrivilege.RESOURCE, fastFail, checkGroups);
			sId = getScopeId(sysEnv);

			SDMSProxy prox;
			try {
				prox = SDMSScopeTable.getObject(sysEnv, sId);
			} catch(NotFoundException nfe) {
				try {
					prox = SDMSFolderTable.getObject(sysEnv, sId);
				} catch (NotFoundException nfe2) {
					prox = SDMSSubmittedEntityTable.getObject(sysEnv, sId);
				}
			}
			ptmp = ptmp & prox.getPrivileges(sysEnv, SDMSPrivilege.RESOURCE, fastFail, checkGroups);
			if((ptmp & SDMSPrivilege.RESOURCE) == SDMSPrivilege.RESOURCE) p = p | SDMSPrivilege.CREATE;
		} else {
			if (sysEnv.cEnv.isJob() || sysEnv.cEnv.isJobServer()) {
				return checkPrivs;
			}
		}
		return p & checkPrivs;
	}

	public void releaseAmount(SystemEnvironment sysEnv, int raAmount)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).releaseAmount(sysEnv, raAmount);
			return;
		}

		Integer rAmount = getFreeAmount(sysEnv);
		if (rAmount != null) {
			rAmount = Integer.valueOf(rAmount.intValue() + raAmount);
			setFreeAmount(sysEnv, rAmount, getAmount(sysEnv));
		}
	}

	public void setFreeAmount(SystemEnvironment sysEnv, Integer newFree)
		throws SDMSException
	{
		this.getBase(sysEnv).setFreeAmount(sysEnv, newFree, getAmount(sysEnv));
		return;
	}

	public void setFreeAmount(SystemEnvironment sysEnv, Integer newFree, Integer oldAmount)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).setFreeAmount(sysEnv, newFree, oldAmount);
			return;
		}
		super.setFreeAmount(sysEnv, newFree);
	}

	public String getURLName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, getNrId(sysEnv));
		String ps;
		final Long scopeId = getScopeId(sysEnv);
		try {
			final SDMSScope s = SDMSScopeTable.getObject(sysEnv, scopeId);
			ps = s.pathString(sysEnv);
		} catch (NotFoundException nfe) {
			try {
				final SDMSFolder f = SDMSFolderTable.getObject(sysEnv, scopeId);
				ps = f.pathString(sysEnv);
			} catch (NotFoundException nfe2) {
				return getId(sysEnv).toString();
			}
		}

		return nr.pathString(sysEnv) + " in " + ps;
	}

	public String getURL(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return "resource " + getURLName(sysEnv);
	}

	protected final SDMSParameterDefinition getParameterDefinition(final SystemEnvironment sysEnv, final String name)
		throws SDMSException
	{
		SDMSParameterDefinition pd = null;

		final Long nrId = getNrId(sysEnv);
		try {
			pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(nrId, name));
		} catch(NotFoundException nfe) {
			throw new CommonErrorException(
				new SDMSMessage(sysEnv, "03409111747", "Parameter $1 not defined for this resource", name));
		}

		return pd;
	}

	public final void createVariables (final SystemEnvironment sysEnv, final WithHash parms)
		throws SDMSException
	{
		if (parms == null)
			return;

		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).createVariables (sysEnv, parms);
			return;
		}

		Long id = getId(sysEnv);
		SDMSParameterDefinition pd;

		final Vector keyList = new Vector (parms.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String name = (String) keyList.get (i);
			final Vector tmp = (Vector) parms.get(name);
			final String value = (String) tmp.get(1);

			pd = getParameterDefinition(sysEnv, name);
			int type = pd.getType(sysEnv).intValue();
			if(type == SDMSParameterDefinition.CONSTANT)
				throw new CommonErrorException(
					new SDMSMessage(sysEnv, "03409111750", "Parameter $1 is defined as a $2 for this resource", name, pd.getTypeAsString(sysEnv)));

			if(value == null) {
				continue;
			}
			final String sic = (value == null ? value : '=' + value);

			SDMSResourceVariableTable.table.create (sysEnv, pd.getId(sysEnv), id, sic);
		}
	}

	public final void killVariables (final SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).killVariables (sysEnv);
			return;
		}

		final Long id = getId(sysEnv);
		final Vector list = SDMSResourceVariableTable.idx_rId.getVector (sysEnv, id);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSResourceVariable rv = (SDMSResourceVariable) list.get (i);
			rv.delete (sysEnv);
		}
	}

	public final void copyVariables (final SystemEnvironment sysEnv, final Long newId)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).copyVariables (sysEnv, newId);
			return;
		}

		final Long oldId = getId(sysEnv);
		final Vector list = SDMSResourceVariableTable.idx_rId.getVector (sysEnv, oldId);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSResourceVariable rv = (SDMSResourceVariable) list.get (i);
			SDMSResourceVariableTable.table.create (sysEnv, rv.getPdId(sysEnv), newId, rv.getValue (sysEnv));
		}
	}

	public final void alterVariables (final SystemEnvironment sysEnv, final WithHash parms, long version)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null) {
			this.getBase(sysEnv).alterVariables (sysEnv, parms, version);
			return;
		}

		SDMSResourceVariable rv;
		SDMSParameterDefinition pd;
		Long pdId;
		int idx;
		String pdef;
		final Long id = getId(sysEnv);

		Vector act_parms = SDMSResourceVariableTable.idx_rId.getVector(sysEnv, id);

		if(parms != null) {
			Set s = parms.keySet();
			Iterator i = s.iterator();
			while(i.hasNext()) {
				final String pn = (String) i.next();
				final Vector tmp = (Vector) parms.get(pn);
				final String pv = (String) tmp.get(1);

				pd = getParameterDefinition(sysEnv, pn);
				if(pd.getType(sysEnv).intValue() == SDMSParameterDefinition.CONSTANT)
					throw new CommonErrorException(
						new SDMSMessage(sysEnv, "03409131150",
							"Parameter $1 is defined as a $2 for this resource", pn, pd.getTypeAsString(sysEnv)));

				pdId = pd.getId(sysEnv);

				pdef = null;
				if(pv != null) {
					pdef = "=" + pv;
				}

				for(idx = 0; idx < act_parms.size(); idx++) {
					rv = (SDMSResourceVariable) act_parms.get(idx);
					if(pdId.equals(rv.getPdId(sysEnv))) {
						act_parms.removeElementAt(idx);
						idx = -1;
						if(pdef == null) {
							rv.delete(sysEnv);
						} else {
							rv.setValue(sysEnv, pdef);
						}
						break;
					}
				}
				if(idx >= act_parms.size()) {
					if(pdef != null)
						SDMSResourceVariableTable.table.create(sysEnv, pdId, id, pdef);
				}
			}
		}
	}

	public final SDMSOutputContainer getVariables (final SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long linkId = getLinkId(sysEnv);
		if (linkId != null)
			return this.getBase(sysEnv).getVariables (sysEnv);

		final Vector c_desc = new Vector();
		final HashSet names = new HashSet();
		final Long id = getId(sysEnv);

		c_desc.add("ID");
		c_desc.add ("NAME");
		c_desc.add ("TYPE");
		c_desc.add ("VALUE");
		c_desc.add ("IS_DEFAULT");
		c_desc.add ("COMMENT");
		c_desc.add ("COMMENTTYPE");

		final SDMSOutputContainer c_container = new SDMSOutputContainer (sysEnv, null, c_desc);

		final Vector rv_v = SDMSResourceVariableTable.idx_rId.getVector (sysEnv, id);
		final Long nrId = getNrId(sysEnv);

		for (int i = 0; i < rv_v.size(); ++i) {
			final SDMSResourceVariable rv = (SDMSResourceVariable) rv_v.get (i);

			final Vector c_data;
			final String name;
			final SDMSParameterDefinition pd;
			final Long pdId;
			try {
				pd = SDMSParameterDefinitionTable.getObject(sysEnv, rv.getPdId(sysEnv));
			} catch(NotFoundException nfe) {
				continue;
			}
			name = pd.getName (sysEnv);
			c_data = new Vector();
			c_data.add (rv.getId (sysEnv));
			c_data.add (name);
			c_data.add (pd.getTypeAsString(sysEnv));
			pdId = pd.getId (sysEnv);

			final String value = rv.getValue (sysEnv);
			c_data.add (value == null ? null : value.substring (1));
			c_data.add (Boolean.FALSE);

			Vector ocv = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, pdId);
			if (ocv.size() != 0) {
				StringBuffer sb = new StringBuffer();
				String tag;
				String infoType = null;
				for (int j = 0; j < ocv.size(); ++j) {
					SDMSObjectComment oc = (SDMSObjectComment) ocv.get(j);
					tag = oc.getTag(sysEnv);
					if (tag != null) {
						if (j != 0)
							sb.append("\n");
						sb.append(tag);
						sb.append("\n\n");
					}
					sb.append (oc.getDescription(sysEnv));
					sb.append ("\n");
					if (j == 0)
						infoType = oc.getInfoTypeAsString(sysEnv);
				}
				c_data.add (sb.toString());
				c_data.add (infoType);
			} else {
				c_data.add (null);
				c_data.add (null);
			}
			names.add(name);

			c_container.addData(sysEnv, c_data);
		}

		final Vector v = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, nrId);
		for(int i = 0; i < v.size(); i++) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(i);
			final String name = pd.getName(sysEnv);
			final Vector c_data;
			final Long pdId = pd.getId (sysEnv);
			if(!names.contains(name)) {
				c_data = new Vector();
				c_data.add(pd.getId(sysEnv));
				c_data.add(name);
				c_data.add(pd.getTypeAsString(sysEnv));
				final String value = pd.getDefaultValue(sysEnv);
				c_data.add((value == null ? null : value.substring(1)));
				c_data.add(Boolean.TRUE);

				Vector ocv = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, pdId);
				if (ocv.size() != 0) {
					StringBuffer sb = new StringBuffer();
					String tag;
					String infoType = null;
					for (int j = 0; j < ocv.size(); ++j) {
						SDMSObjectComment oc = (SDMSObjectComment) ocv.get(j);
						tag = oc.getTag(sysEnv);
						if (tag != null) {
							if (j != 0)
								sb.append("\n");
							sb.append(tag);
							sb.append("\n\n");
						}
						sb.append (oc.getDescription(sysEnv));
						sb.append ("\n");
						if (j == 0)
							infoType = oc.getInfoTypeAsString(sysEnv);
					}
					c_data.add (sb.toString());
					c_data.add (infoType);
				} else {
					c_data.add (null);
					c_data.add (null);
				}
				c_container.addData(sysEnv, c_data);
			}
		}

		Collections.sort (c_container.dataset, c_container.getComparator (sysEnv, 1));

		return c_container;
	}

	public String getVariableValue(SystemEnvironment sysEnv, String name, boolean doSubstitute)
		throws SDMSException
	{
		return RVR.getVariableValue(sysEnv, this.getBase(sysEnv), name, null, doSubstitute);
	}

	public String getVariableValue(SystemEnvironment sysEnv, String name, SDMSSubmittedEntity sme, boolean doSubstitute)
		throws SDMSException
	{
		return RVR.getVariableValue(sysEnv, this.getBase(sysEnv), name, sme, doSubstitute);
	}

	public void setVariableValue(SystemEnvironment sysEnv, String name, Long smeId, String value)
		throws SDMSException
	{
		SDMSParameterDefinition pd;
		SDMSResourceAllocation ra;
		SDMSResourceVariable rv;
		final Long rId = getId(sysEnv);
		final Long pdId;

		final Vector rav = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(smeId, getNrId(sysEnv)));
		if (rav.size() == 0)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03409221436", "Resource not allocated"));
		ra = (SDMSResourceAllocation) rav.get(0);
		if(ra.getAllocationType(sysEnv).intValue() != SDMSResourceAllocation.ALLOCATION) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03409221438", "Resource not allocated"));
		}
		if(ra.getLockmode(sysEnv).intValue() != Lockmode.X) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03409221440", "Resource not exclusively locked"));
		}

		pd = getParameterDefinition(sysEnv, name);
		if(pd.getType(sysEnv).intValue() != SDMSParameterDefinition.PARAMETER) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "0309221536", "Parameter $1 is non mutable", name));
		}
		pdId = pd.getId(sysEnv);

		try {
			rv = SDMSResourceVariableTable.idx_pdId_rId_getUnique(sysEnv, new SDMSKey(pdId, rId));
			rv.setValue(sysEnv, "=" + value);
		} catch (NotFoundException nfe) {
			SDMSResourceVariableTable.table.create(sysEnv, pd.getId(sysEnv), rId, "=" + value);
		}
	}
}
