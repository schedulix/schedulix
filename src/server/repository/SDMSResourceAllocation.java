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

public class SDMSResourceAllocation extends SDMSResourceAllocationProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSResourceAllocation.java,v 2.3.14.1 2013/03/14 10:25:23 ronald Exp $";

	protected SDMSResourceAllocation(SDMSObject p_object)
	{
		super(p_object);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Long rId = getRId(sysEnv);
		final Long smeId = getSmeId(sysEnv);
		int allocType = getAllocationType(sysEnv).intValue();
		if(allocType == ALLOCATION || allocType == RESERVATION || allocType == MASTER_RESERVATION) {
			Integer raAmount = getAmount(sysEnv);
			int iraAmount = raAmount.intValue();
			if (iraAmount != 0) {
				if (smeId.longValue() > 0) {

					final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, getSmeId(sysEnv));
					final Long masterId = new Long(- sme.getMasterId(sysEnv).longValue());
					try {
						final SDMSResourceAllocation mra = SDMSResourceAllocationTable.idx_smeId_rId_getUnique(sysEnv, new SDMSKey(masterId, rId));
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
				try {
					SDMSResource r = SDMSResourceTable.getObject(sysEnv, rId);
					r.releaseAmount(sysEnv, iraAmount);
				} catch(NotFoundException nfe) {

				}
			}
		}
		super.delete(sysEnv);
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
		SDMSResource r = SDMSResourceTable.getObject(sysEnv, getRId(sysEnv));

		if(allocType == REQUEST) {
			if(p_allocType == ALLOCATION || p_allocType == RESERVATION) {
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
		return;
	}

	public void setAmount (SystemEnvironment sysEnv, Integer p_amount)
	throws SDMSException
	{
		int oAmount = getAmount(sysEnv).intValue();
		int nAmount = p_amount.intValue();
		int dAmount;
		if (oAmount == nAmount) return;

		if (oAmount >= 0 || nAmount >= 0) {

			SDMSResource r = SDMSResourceTable.getObject(sysEnv, getRId(sysEnv));
			Integer fAmount = r.getFreeAmount(sysEnv);
			if (fAmount!= null) {
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
}
