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

public class SDMSResourceAllocation extends SDMSResourceAllocationProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSResourceAllocation.java,v 2.3.14.1 2013/03/14 10:25:23 ronald Exp $";
	static final Integer ONE = new Integer(1);

	protected SDMSResourceAllocation(SDMSObject p_object)
	{
		super(p_object);
	}

	public void delete(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSThread.doTrace(sysEnv.cEnv, "Deleting RA without stickyCleanup ------------------------------------", SDMSThread.SEVERITY_ERROR);
		delete(sysEnv, false, false);
	}

	public void delete(SystemEnvironment sysEnv, boolean stickyCleanup, boolean deleteAll)
		throws SDMSException
	{
		int refcount = getRefcount(sysEnv).intValue();
		if (refcount > 1 && !deleteAll) {
			setRefcount(sysEnv, new Integer(refcount -1));
			return;
		}

		final Long rId = getRId(sysEnv);
		final Long smeId = getSmeId(sysEnv);
		int allocType = getAllocationType(sysEnv).intValue();
		SDMSResourceAllocation masterra = null;

		if (getIsSticky(sysEnv).booleanValue()) {
			if ( smeId.longValue() > 0) {
				try {
					masterra = SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(sysEnv,
						new SDMSKey(new Long(-getStickyParent(sysEnv).longValue()),getRId(sysEnv), getStickyName(sysEnv)));

				} catch(NotFoundException nfe) {
					SDMSThread.doTrace(sysEnv.cEnv, ": No master reservation found for sticky allocation", SDMSThread.SEVERITY_ERROR);
				}
			}
		}

		if (allocType == ALLOCATION || allocType == RESERVATION || allocType == MASTER_RESERVATION) {
			Integer raAmount = getAmount(sysEnv);
			int iraAmount = raAmount.intValue();
			if (iraAmount != 0) {
				if (smeId.longValue() > 0) {
					if (getIsSticky(sysEnv).booleanValue()) {
						final Long nParentId = new Long(-getStickyParent(sysEnv).longValue());
						try {
							final SDMSResourceAllocation mra =
								SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(
									sysEnv, new SDMSKey(nParentId, rId, getStickyName(sysEnv)));
							int mraAmount = mra.getAmount(sysEnv).intValue();
							int diff = mra.getOrigAmount(sysEnv).intValue() - mraAmount;
							if (diff > 0) {
								if (diff >= iraAmount) {
									mra.setMyAmount(sysEnv, new Integer(mraAmount + iraAmount));
									iraAmount = 0;
								} else {
									iraAmount = iraAmount - diff;
									mra.setMyAmount(sysEnv, new Integer(mraAmount + diff));
								}
							}
						} catch (NotFoundException nfe) {
						}
					}
				}
				try {
					SDMSResource r = SDMSResourceTable.getObjectForUpdate(sysEnv, rId);
					r.releaseAmount(sysEnv, iraAmount);
				} catch(NotFoundException nfe) {
				}
			}
		}
		super.delete(sysEnv);

		if (masterra != null) {
			if (stickyCleanup) {
					masterra.cleanupStickyGroup(sysEnv);
			}
			masterra.delete(sysEnv, stickyCleanup, false);
		}
	}

	public void cleanupStickyGroup(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long rId = getRId(sysEnv);
		SDMSResource r = SDMSResourceTable.getObject(sysEnv, rId);
		Long nrId = r.getNrId(sysEnv);
		String stickyName = getStickyName(sysEnv);

		Vector rargv = SDMSResourceAllocationTable.idx_stickyParent_rId_stickyName.getVector(sysEnv,
							new SDMSKey(getStickyParent(sysEnv), getRId(sysEnv), stickyName));
		for (int i = 0; i < rargv.size(); ++i) {
			SDMSResourceAllocation ra = (SDMSResourceAllocation) rargv.get(i);
			Long raId;
			try {
				raId = ra.getId(sysEnv);
			} catch (NotFoundException e) {
				continue;
			}
			if (getId(sysEnv).equals(raId))
				continue;
			Long smeId = ra.getSmeId(sysEnv);
			Vector smerqv = SDMSRunnableQueueTable.idx_smeId.getVector(sysEnv, smeId);
			for (int j = 0; j < smerqv.size(); ++j) {
				SDMSRunnableQueue rq = (SDMSRunnableQueue) smerqv.get(j);
				Long scopeId = rq.getScopeId(sysEnv);
				SDMSnpSrvrSRFootprint npsfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, scopeId);
				HashMap sfp = npsfp.getFp(sysEnv);
				if (rId.equals(sfp.get(nrId))) {
					Iterator nrit = sfp.keySet().iterator();
					while (nrit.hasNext()) {
						Long sfpNRId = (Long) nrit.next();
						Long sfpRId = (Long) sfp.get(sfpNRId);
						Vector riv = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(smeId, sfpNRId));
						for (int k = 0; k < riv.size(); ++k) {
							SDMSResourceAllocation ra2d = (SDMSResourceAllocation) riv.get(k);
							if (sfpRId.equals(ra2d.getRId(sysEnv))) {
								ra2d.delete(sysEnv, true, false);
							}
						}
					}
					rq.delete(sysEnv);
				}
			}
			smerqv = SDMSRunnableQueueTable.idx_smeId.getVector(sysEnv, smeId);
			if (smerqv.size() == 0) {
				SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
				sme.setToError(sysEnv, "Job cannot run in any scope because all scopes were eliminated due to sticky requests");
			}
		}
	}

	public SDMSResourceAllocation ignore (SystemEnvironment sysEnv)
		throws SDMSException
	{
		setAllocationType(sysEnv, new Integer(IGNORE));
		return this;
	}

	public void setAllocationType (SystemEnvironment sysEnv, Integer p_allocationType)
		throws SDMSException
	{
		int allocType = getAllocationType(sysEnv).intValue();
		int p_allocType = p_allocationType.intValue();
		SDMSResource r;
		try {
			r = SDMSResourceTable.getObject(sysEnv, getRId(sysEnv));
		} catch (NotFoundException nfe) {
			SDMSThread.doTrace(sysEnv.cEnv, "Cannot set allocation type of a Named Resource IGNORE Resource Allocation", SDMSThread.SEVERITY_ERROR);
			return;
		}

		if(allocType == REQUEST || allocType == MASTER_REQUEST) {
			if(p_allocType == ALLOCATION ||
			   p_allocType == RESERVATION ||
			   p_allocType == MASTER_RESERVATION) {
				Integer rAmount = r.getFreeAmount(sysEnv);
				if (rAmount != null ) {
					rAmount = new Integer(rAmount.intValue() - Math.max (getAmount(sysEnv).intValue(), 0));
					r.setFreeAmount(sysEnv, rAmount);
				}
			}
		}
		super.setAllocationType(sysEnv, p_allocationType);
		if(p_allocType == IGNORE) {
			if(allocType == ALLOCATION || allocType == RESERVATION) {
				Integer raAmount = getAmount(sysEnv);
				if (raAmount.intValue() > 0) {
					Integer rAmount = r.getFreeAmount(sysEnv);
					if (rAmount != null) {
						rAmount = new Integer(rAmount.intValue() + raAmount.intValue());
						r.setFreeAmount(sysEnv, rAmount);
					}
				}
			}

			if(getIsSticky(sysEnv).booleanValue()) {
				try {
					SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, getSmeId(sysEnv));
					SDMSThread.doTrace(null, "Trying to release Masterreservation", SDMSThread.SEVERITY_DEBUG);
					sme.releaseStickyResource(sysEnv, this, r, sme.getState(sysEnv).intValue());
				} catch (NotFoundException nfe) {

				}
			}
		}
		if (allocType == ALLOCATION || allocType == RESERVATION) {
			if (p_allocType == REQUEST) {
				Integer raAmount = getAmount(sysEnv);
				int iraAmount = raAmount.intValue();
				if (iraAmount != 0) {
					Long rId = getRId(sysEnv);
					if (getIsSticky(sysEnv).booleanValue()) {
						final Long nParentId = new Long(-getStickyParent(sysEnv).longValue());
						try {
							final SDMSResourceAllocation mra =
								SDMSResourceAllocationTable.idx_smeId_rId_stickyName_getUnique(
									sysEnv, new SDMSKey(nParentId, rId, getStickyName(sysEnv)));
							int mraAmount = mra.getAmount(sysEnv).intValue();
							int diff = mra.getOrigAmount(sysEnv).intValue() - mraAmount;
							if (diff > 0) {
								if (diff >= iraAmount) {
									mra.setMyAmount(sysEnv, new Integer(mraAmount + iraAmount));
									iraAmount = 0;
								} else {
									iraAmount = iraAmount - diff;
									mra.setMyAmount(sysEnv, new Integer(mraAmount + diff));
								}
							}
						} catch (NotFoundException nfe) {
						}
					}
					r.releaseAmount(sysEnv, iraAmount);
				}
			}
		}
		if (p_allocType == ALLOCATION)
			setRefcount(sysEnv, ONE);
		return;
	}

	public void setAmount (SystemEnvironment sysEnv, Integer p_amount)
		throws SDMSException
	{
		int allocationType = getAllocationType(sysEnv);
		if (allocationType == REQUEST || allocationType == MASTER_REQUEST) {
			super.setAmount(sysEnv, new Integer(Math.max(p_amount, 0)));
			return;
		}

		int oAmount = getAmount(sysEnv).intValue();
		int nAmount = p_amount.intValue();
		int dAmount;
		if (oAmount == nAmount) return;

		if (oAmount >= 0 || nAmount >= 0) {

			SDMSResource r = SDMSResourceTable.getObject(sysEnv, getRId(sysEnv));
			Integer fAmount = r.getFreeAmount(sysEnv);
			if (fAmount != null) {
				int rAmount = fAmount.intValue();
				dAmount = nAmount - oAmount;
				if (dAmount < 0 && nAmount < 0) {
					dAmount -= nAmount;
				}
				if (dAmount > 0 && oAmount < 0) {
					dAmount += oAmount;
				}

				r.setFreeAmount(sysEnv, new Integer(rAmount - dAmount));
			}
		}

		super.setAmount(sysEnv, new Integer(Math.max(nAmount, 0)));
	}

	protected void setMyAmount (SystemEnvironment sysEnv, Integer p_amount)
		throws SDMSException
	{
		super.setAmount(sysEnv, p_amount);
	}

	public void setRefcount(SystemEnvironment sysEnv, Integer p_count)
		throws SDMSException
	{
		super.setRefcount(sysEnv, p_count);
	}
}
