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

public class SDMSResourceAllocationTable extends SDMSResourceAllocationTableGeneric
{

	public SDMSResourceAllocationTable(SystemEnvironment env)
		throws SDMSException
	{
		super(env);
	}
	public SDMSResourceAllocation create(SystemEnvironment env
						,Long p_rId
						,Long p_smeId
						,Long p_nrId
						,Integer p_amount
						,Integer p_origAmount
						,Integer p_keepMode
						,Boolean p_isSticky
						,String p_stickyName
						,Long p_stickyParent
						,Integer p_allocationType
						,Long p_rsmpId
						,Integer p_lockmode
						,Integer p_refcount
		)
		throws SDMSException
	{
		int p_allocType = p_allocationType.intValue();
		if(p_allocType != SDMSResourceAllocation.REQUEST &&
		   p_allocType != SDMSResourceAllocation.MASTER_REQUEST &&
		   p_allocType != SDMSResourceAllocation.IGNORE) {
			SDMSResource r;
			r = SDMSResourceTable.getObject(env, p_rId);
			Integer rAmount = r.getFreeAmount(env);
			if (rAmount != null ) {
				rAmount = Integer.valueOf(rAmount.intValue() - Math.max (p_amount.intValue(), 0));
				r.setFreeAmount(env, rAmount);
			}
		}
		return super.create( env
					, p_rId
					, p_smeId
					, p_nrId
					, p_amount
					, p_origAmount
					, p_keepMode
					, p_isSticky
					, p_stickyName
					, p_stickyParent
					, p_allocationType
					, p_rsmpId
					, p_lockmode
					, p_refcount
		);
	}
}
