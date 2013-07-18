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

public class SDMSResourceAllocationGeneric extends SDMSObject
	implements Cloneable
{

	public final static String __version = "SDMSResourceAllocationGeneric $Revision: 2.6 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public static final int N = Lockmode.N;
	public static final int X = Lockmode.X;
	public static final int SX = Lockmode.SX;
	public static final int S = Lockmode.S;
	public static final int SC = Lockmode.SC;
	public static final int NOKEEP = SDMSResourceRequirement.NOKEEP;
	public static final int KEEP = SDMSResourceRequirement.KEEP;
	public static final int KEEP_FINAL = SDMSResourceRequirement.KEEP_FINAL;
	public static final int REQUEST = 1;
	public static final int RESERVATION = 2;
	public static final int MASTER_RESERVATION = 3;
	public static final int ALLOCATION = 4;
	public static final int IGNORE = 5;

	public final static int nr_id = 1;
	public final static int nr_rId = 2;
	public final static int nr_smeId = 3;
	public final static int nr_nrId = 4;
	public final static int nr_amount = 5;
	public final static int nr_origAmount = 6;
	public final static int nr_keepMode = 7;
	public final static int nr_isSticky = 8;
	public final static int nr_allocationType = 9;
	public final static int nr_rsmpId = 10;
	public final static int nr_lockmode = 11;
	public final static int nr_refcount = 12;
	public final static int nr_creatorUId = 13;
	public final static int nr_createTs = 14;
	public final static int nr_changerUId = 15;
	public final static int nr_changeTs = 16;

	public static String tableName = SDMSResourceAllocationTableGeneric.tableName;

	protected Long rId;
	protected Long smeId;
	protected Long nrId;
	protected Integer amount;
	protected Integer origAmount;
	protected Integer keepMode;
	protected Boolean isSticky;
	protected Integer allocationType;
	protected Long rsmpId;
	protected Integer lockmode;
	protected Integer refcount;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSResourceAllocationGeneric(
	        SystemEnvironment env,
	        Long p_rId,
	        Long p_smeId,
	        Long p_nrId,
	        Integer p_amount,
	        Integer p_origAmount,
	        Integer p_keepMode,
	        Boolean p_isSticky,
	        Integer p_allocationType,
	        Long p_rsmpId,
	        Integer p_lockmode,
	        Integer p_refcount,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSResourceAllocationTableGeneric.table);
		rId = p_rId;
		smeId = p_smeId;
		nrId = p_nrId;
		amount = p_amount;
		origAmount = p_origAmount;
		keepMode = p_keepMode;
		isSticky = p_isSticky;
		allocationType = p_allocationType;
		rsmpId = p_rsmpId;
		lockmode = p_lockmode;
		refcount = p_refcount;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getRId (SystemEnvironment env)
	throws SDMSException
	{
		return (rId);
	}

	public	SDMSResourceAllocationGeneric setRId (SystemEnvironment env, Long p_rId)
	throws SDMSException
	{
		if(rId.equals(p_rId)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.rId = p_rId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	SDMSResourceAllocationGeneric setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.smeId = p_smeId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getNrId (SystemEnvironment env)
	throws SDMSException
	{
		return (nrId);
	}

	public	SDMSResourceAllocationGeneric setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		if(nrId.equals(p_nrId)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.nrId = p_nrId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (amount);
	}

	public	SDMSResourceAllocationGeneric setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		if(p_amount != null && p_amount.equals(amount)) return this;
		if(p_amount == null && amount == null) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.amount = p_amount;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getOrigAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (origAmount);
	}

	public	SDMSResourceAllocationGeneric setOrigAmount (SystemEnvironment env, Integer p_origAmount)
	throws SDMSException
	{
		if(p_origAmount != null && p_origAmount.equals(origAmount)) return this;
		if(p_origAmount == null && origAmount == null) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.origAmount = p_origAmount;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getKeepMode (SystemEnvironment env)
	throws SDMSException
	{
		return (keepMode);
	}

	public String getKeepModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getKeepMode (env);
		switch (v.intValue()) {
		case SDMSResourceAllocation.NOKEEP:
			return "NOKEEP";
		case SDMSResourceAllocation.KEEP:
			return "KEEP";
		case SDMSResourceAllocation.KEEP_FINAL:
			return "KEEP_FINAL";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ResourceAllocation.keepMode: $1",
		                          getKeepMode (env)));
	}

	public	SDMSResourceAllocationGeneric setKeepMode (SystemEnvironment env, Integer p_keepMode)
	throws SDMSException
	{
		if(keepMode.equals(p_keepMode)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.keepMode = p_keepMode;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Boolean getIsSticky (SystemEnvironment env)
	throws SDMSException
	{
		return (isSticky);
	}

	public	SDMSResourceAllocationGeneric setIsSticky (SystemEnvironment env, Boolean p_isSticky)
	throws SDMSException
	{
		if(isSticky.equals(p_isSticky)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.isSticky = p_isSticky;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getAllocationType (SystemEnvironment env)
	throws SDMSException
	{
		return (allocationType);
	}

	public String getAllocationTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getAllocationType (env);
		switch (v.intValue()) {
		case SDMSResourceAllocation.REQUEST:
			return "REQUEST";
		case SDMSResourceAllocation.RESERVATION:
			return "RESERVATION";
		case SDMSResourceAllocation.MASTER_RESERVATION:
			return "MASTER_RESERVATION";
		case SDMSResourceAllocation.ALLOCATION:
			return "ALLOCATION";
		case SDMSResourceAllocation.IGNORE:
			return "IGNORE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ResourceAllocation.allocationType: $1",
		                          getAllocationType (env)));
	}

	public	SDMSResourceAllocationGeneric setAllocationType (SystemEnvironment env, Integer p_allocationType)
	throws SDMSException
	{
		if(allocationType.equals(p_allocationType)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.allocationType = p_allocationType;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getRsmpId (SystemEnvironment env)
	throws SDMSException
	{
		return (rsmpId);
	}

	public	SDMSResourceAllocationGeneric setRsmpId (SystemEnvironment env, Long p_rsmpId)
	throws SDMSException
	{
		if(p_rsmpId != null && p_rsmpId.equals(rsmpId)) return this;
		if(p_rsmpId == null && rsmpId == null) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.rsmpId = p_rsmpId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getLockmode (SystemEnvironment env)
	throws SDMSException
	{
		return (lockmode);
	}

	public String getLockmodeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getLockmode (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSResourceAllocation.N:
			return "N";
		case SDMSResourceAllocation.X:
			return "X";
		case SDMSResourceAllocation.SX:
			return "SX";
		case SDMSResourceAllocation.S:
			return "S";
		case SDMSResourceAllocation.SC:
			return "SC";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ResourceAllocation.lockmode: $1",
		                          getLockmode (env)));
	}

	public	SDMSResourceAllocationGeneric setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		if(p_lockmode != null && p_lockmode.equals(lockmode)) return this;
		if(p_lockmode == null && lockmode == null) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.lockmode = p_lockmode;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getRefcount (SystemEnvironment env)
	throws SDMSException
	{
		return (refcount);
	}

	public	SDMSResourceAllocationGeneric setRefcount (SystemEnvironment env, Integer p_refcount)
	throws SDMSException
	{
		if(refcount.equals(p_refcount)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.refcount = p_refcount;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	SDMSResourceAllocationGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.creatorUId = p_creatorUId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	SDMSResourceAllocationGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.createTs = p_createTs;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	SDMSResourceAllocationGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSResourceAllocationGeneric) change(env);
			o.changerUId = p_changerUId;
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	SDMSResourceAllocationGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSResourceAllocationGeneric) change(env);
			o.changeTs = p_changeTs;
			o.changerUId = env.cEnv.euid();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSResourceAllocationGeneric set_SmeIdRId (SystemEnvironment env, Long p_smeId, Long p_rId)
	throws SDMSException
	{
		SDMSResourceAllocationGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.smeId = p_smeId;
			o.rId = p_rId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSResourceAllocationGeneric set_SmeIdNrId (SystemEnvironment env, Long p_smeId, Long p_nrId)
	throws SDMSException
	{
		SDMSResourceAllocationGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.smeId = p_smeId;
			o.nrId = p_nrId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy()
	{
		return new SDMSResourceAllocation(this);
	}

	protected SDMSResourceAllocationGeneric(Long p_id,
	                                        Long p_rId,
	                                        Long p_smeId,
	                                        Long p_nrId,
	                                        Integer p_amount,
	                                        Integer p_origAmount,
	                                        Integer p_keepMode,
	                                        Boolean p_isSticky,
	                                        Integer p_allocationType,
	                                        Long p_rsmpId,
	                                        Integer p_lockmode,
	                                        Integer p_refcount,
	                                        Long p_creatorUId,
	                                        Long p_createTs,
	                                        Long p_changerUId,
	                                        Long p_changeTs,
	                                        long p_validFrom, long p_validTo)
	{
		id     = p_id;
		rId = p_rId;
		smeId = p_smeId;
		nrId = p_nrId;
		amount = p_amount;
		origAmount = p_origAmount;
		keepMode = p_keepMode;
		isSticky = p_isSticky;
		allocationType = p_allocationType;
		rsmpId = p_rsmpId;
		lockmode = p_lockmode;
		refcount = p_refcount;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		validFrom = p_validFrom;
		validTo   = p_validTo;
	}

	protected String tableName()
	{
		return tableName;
	}

	protected void insertDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pInsert == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				String squote = "";
				String equote = "";
				if (driverName.startsWith("MySQL")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "INSERT INTO RESOURCE_ALLOCATION (" +
				        "ID" +
				        ", " + squote + "R_ID" + equote +
				        ", " + squote + "SME_ID" + equote +
				        ", " + squote + "NR_ID" + equote +
				        ", " + squote + "AMOUNT" + equote +
				        ", " + squote + "ORIG_AMOUNT" + equote +
				        ", " + squote + "KEEP_MODE" + equote +
				        ", " + squote + "IS_STICKY" + equote +
				        ", " + squote + "ALLOCATION_TYPE" + equote +
				        ", " + squote + "RSMP_ID" + equote +
				        ", " + squote + "LOCKMODE" + equote +
				        ", " + squote + "REFCOUNT" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ") VALUES (?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "ResourceAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, rId.longValue());
			pInsert.setLong (3, smeId.longValue());
			pInsert.setLong (4, nrId.longValue());
			if (amount == null)
				pInsert.setNull(5, Types.INTEGER);
			else
				pInsert.setInt(5, amount.intValue());
			if (origAmount == null)
				pInsert.setNull(6, Types.INTEGER);
			else
				pInsert.setInt(6, origAmount.intValue());
			pInsert.setInt(7, keepMode.intValue());
			pInsert.setInt (8, isSticky.booleanValue() ? 1 : 0);
			pInsert.setInt(9, allocationType.intValue());
			if (rsmpId == null)
				pInsert.setNull(10, Types.INTEGER);
			else
				pInsert.setLong (10, rsmpId.longValue());
			if (lockmode == null)
				pInsert.setNull(11, Types.INTEGER);
			else
				pInsert.setInt(11, lockmode.intValue());
			pInsert.setInt(12, refcount.intValue());
			pInsert.setLong (13, creatorUId.longValue());
			pInsert.setLong (14, createTs.longValue());
			pInsert.setLong (15, changerUId.longValue());
			pInsert.setLong (16, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "ResourceAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM RESOURCE_ALLOCATION WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "ResourceAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "ResourceAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		if(pUpdate == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				String squote = "";
				String equote = "";
				if (driverName.startsWith("MySQL")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "UPDATE RESOURCE_ALLOCATION SET " +
				        "" + squote + "R_ID" + equote + " = ? " +
				        ", " + squote + "SME_ID" + equote + " = ? " +
				        ", " + squote + "NR_ID" + equote + " = ? " +
				        ", " + squote + "AMOUNT" + equote + " = ? " +
				        ", " + squote + "ORIG_AMOUNT" + equote + " = ? " +
				        ", " + squote + "KEEP_MODE" + equote + " = ? " +
				        ", " + squote + "IS_STICKY" + equote + " = ? " +
				        ", " + squote + "ALLOCATION_TYPE" + equote + " = ? " +
				        ", " + squote + "RSMP_ID" + equote + " = ? " +
				        ", " + squote + "LOCKMODE" + equote + " = ? " +
				        ", " + squote + "REFCOUNT" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "ResourceAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, rId.longValue());
			pUpdate.setLong (2, smeId.longValue());
			pUpdate.setLong (3, nrId.longValue());
			if (amount == null)
				pUpdate.setNull(4, Types.INTEGER);
			else
				pUpdate.setInt(4, amount.intValue());
			if (origAmount == null)
				pUpdate.setNull(5, Types.INTEGER);
			else
				pUpdate.setInt(5, origAmount.intValue());
			pUpdate.setInt(6, keepMode.intValue());
			pUpdate.setInt (7, isSticky.booleanValue() ? 1 : 0);
			pUpdate.setInt(8, allocationType.intValue());
			if (rsmpId == null)
				pUpdate.setNull(9, Types.INTEGER);
			else
				pUpdate.setLong (9, rsmpId.longValue());
			if (lockmode == null)
				pUpdate.setNull(10, Types.INTEGER);
			else
				pUpdate.setInt(10, lockmode.intValue());
			pUpdate.setInt(11, refcount.intValue());
			pUpdate.setLong (12, creatorUId.longValue());
			pUpdate.setLong (13, createTs.longValue());
			pUpdate.setLong (14, changerUId.longValue());
			pUpdate.setLong (15, changeTs.longValue());
			pUpdate.setLong(16, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "ResourceAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkKeepMode(Integer p)
	{
		switch (p.intValue()) {
		case SDMSResourceAllocation.NOKEEP:
		case SDMSResourceAllocation.KEEP:
		case SDMSResourceAllocation.KEEP_FINAL:
			return true;
		}
		return false;
	}
	static public boolean checkAllocationType(Integer p)
	{
		switch (p.intValue()) {
		case SDMSResourceAllocation.REQUEST:
		case SDMSResourceAllocation.RESERVATION:
		case SDMSResourceAllocation.MASTER_RESERVATION:
		case SDMSResourceAllocation.ALLOCATION:
		case SDMSResourceAllocation.IGNORE:
			return true;
		}
		return false;
	}
	static public boolean checkLockmode(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSResourceAllocation.N:
		case SDMSResourceAllocation.X:
		case SDMSResourceAllocation.SX:
		case SDMSResourceAllocation.S:
		case SDMSResourceAllocation.SC:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ResourceAllocation", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rId : " + rId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "smeId : " + smeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nrId : " + nrId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "amount : " + amount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "origAmount : " + origAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "keepMode : " + keepMode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSticky : " + isSticky, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "allocationType : " + allocationType, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rsmpId : " + rsmpId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lockmode : " + lockmode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "refcount : " + refcount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validTo : " + validTo, SDMSThread.SEVERITY_MESSAGE);
	}

	public String toString(int indent)
	{
		StringBuffer sb = new StringBuffer(indent + 1);
		for(int i = 0; i < indent; ++i) sb.append(" ");
		String indentString = new String(sb);
		String result =
		        indentString + "id : " + id + "\n" +
		        indentString + "rId            : " + rId + "\n" +
		        indentString + "smeId          : " + smeId + "\n" +
		        indentString + "nrId           : " + nrId + "\n" +
		        indentString + "amount         : " + amount + "\n" +
		        indentString + "origAmount     : " + origAmount + "\n" +
		        indentString + "keepMode       : " + keepMode + "\n" +
		        indentString + "isSticky       : " + isSticky + "\n" +
		        indentString + "allocationType : " + allocationType + "\n" +
		        indentString + "rsmpId         : " + rsmpId + "\n" +
		        indentString + "lockmode       : " + lockmode + "\n" +
		        indentString + "refcount       : " + refcount + "\n" +
		        indentString + "creatorUId     : " + creatorUId + "\n" +
		        indentString + "createTs       : " + createTs + "\n" +
		        indentString + "changerUId     : " + changerUId + "\n" +
		        indentString + "changeTs       : " + changeTs + "\n" +
		        indentString + "validFrom : " + validFrom + "\n" +
		        indentString + "validTo : " + validTo + "\n";
		return result;
	}

	public String toString()
	{
		String result = toString(0);
		return result;
	}
}
