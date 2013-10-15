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

public class SDMSMasterAllocationTable extends SDMSMasterAllocationTableGeneric
{

	public SDMSMasterAllocationTable(SystemEnvironment sysEnv)
	throws SDMSException
	{
		super(sysEnv);
	}

	public SDMSMasterAllocation create(SystemEnvironment sysEnv
	                                   ,Long p_raId
	                                   ,Long p_smeId
	                                   ,Integer p_amount
	                                   ,String p_stickyName
	                                   ,Long p_stickyParent
	                                   ,Integer p_lockmode
	                                  )
	throws SDMSException
	{
		SDMSMasterAllocation ma = super.create(sysEnv, p_raId, p_smeId, p_amount, p_stickyName, p_stickyParent, p_lockmode);
		final SDMSResourceAllocation mra = SDMSResourceAllocationTable.getObject(sysEnv, p_raId);

		Integer mraLockmode = mra.getLockmode(sysEnv);
		if (mraLockmode == null) {
			mra.setLockmode(sysEnv, p_lockmode);
		} else {
			mra.setLockmode(sysEnv, new Integer(mraLockmode.intValue() & p_lockmode.intValue()));
		}

		Integer mraAmount = mra.getAmount(sysEnv);
		mra.setAmount(sysEnv, new Integer(mraAmount.intValue() + p_amount.intValue()));

		mraAmount = mra.getOrigAmount(sysEnv);
		mra.setOrigAmount(sysEnv, new Integer(mraAmount.intValue() + p_amount.intValue()));

		return ma;
	}
}
