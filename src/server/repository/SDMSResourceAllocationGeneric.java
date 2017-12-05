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
	public static final int MASTER_REQUEST = 6;

	public final static int nr_id = 1;
	public final static int nr_rId = 2;
	public final static int nr_smeId = 3;
	public final static int nr_nrId = 4;
	public final static int nr_amount = 5;
	public final static int nr_origAmount = 6;
	public final static int nr_keepMode = 7;
	public final static int nr_isSticky = 8;
	public final static int nr_stickyName = 9;
	public final static int nr_stickyParent = 10;
	public final static int nr_allocationType = 11;
	public final static int nr_rsmpId = 12;
	public final static int nr_lockmode = 13;
	public final static int nr_refcount = 14;
	public final static int nr_creatorUId = 15;
	public final static int nr_createTs = 16;
	public final static int nr_changerUId = 17;
	public final static int nr_changeTs = 18;

	public static String tableName = SDMSResourceAllocationTableGeneric.tableName;

	protected Long rId;
	protected Long smeId;
	protected Long nrId;
	protected Integer amount;
	protected Integer origAmount;
	protected Integer keepMode;
	protected Boolean isSticky;
	protected String stickyName;
	protected Long stickyParent;
	protected Integer allocationType;
	protected Long rsmpId;
	protected Integer lockmode;
	protected Integer refcount;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSResourceAllocationGeneric(
	        SystemEnvironment env,
	        Long p_rId,
	        Long p_smeId,
	        Long p_nrId,
	        Integer p_amount,
	        Integer p_origAmount,
	        Integer p_keepMode,
	        Boolean p_isSticky,
	        String p_stickyName,
	        Long p_stickyParent,
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
		if (p_stickyName != null && p_stickyName.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ResourceAllocation) Length of $1 exceeds maximum length $2", "stickyName", "64")
			);
		}
		stickyName = p_stickyName;
		stickyParent = p_stickyParent;
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

	public	void setRId (SystemEnvironment env, Long p_rId)
	throws SDMSException
	{
		if(rId.equals(p_rId)) return;
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
			o.versions.table.index(env, o, 25);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return;
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
			o.versions.table.index(env, o, 42);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getNrId (SystemEnvironment env)
	throws SDMSException
	{
		return (nrId);
	}

	public	void setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		if(nrId.equals(p_nrId)) return;
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
			o.versions.table.index(env, o, 36);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (amount);
	}

	public	void setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		if(p_amount != null && p_amount.equals(amount)) return;
		if(p_amount == null && amount == null) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.amount = p_amount;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getOrigAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (origAmount);
	}

	public	void setOrigAmount (SystemEnvironment env, Integer p_origAmount)
	throws SDMSException
	{
		if(p_origAmount != null && p_origAmount.equals(origAmount)) return;
		if(p_origAmount == null && origAmount == null) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.origAmount = p_origAmount;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setKeepMode (SystemEnvironment env, Integer p_keepMode)
	throws SDMSException
	{
		if(keepMode.equals(p_keepMode)) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.keepMode = p_keepMode;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsSticky (SystemEnvironment env)
	throws SDMSException
	{
		return (isSticky);
	}

	public	void setIsSticky (SystemEnvironment env, Boolean p_isSticky)
	throws SDMSException
	{
		if(isSticky.equals(p_isSticky)) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.isSticky = p_isSticky;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getStickyName (SystemEnvironment env)
	throws SDMSException
	{
		return (stickyName);
	}

	public	void setStickyName (SystemEnvironment env, String p_stickyName)
	throws SDMSException
	{
		if(p_stickyName != null && p_stickyName.equals(stickyName)) return;
		if(p_stickyName == null && stickyName == null) return;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			if (p_stickyName != null && p_stickyName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(ResourceAllocation) Length of $1 exceeds maximum length $2", "stickyName", "64")
				);
			}
			o.stickyName = p_stickyName;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 24);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getStickyParent (SystemEnvironment env)
	throws SDMSException
	{
		return (stickyParent);
	}

	public	void setStickyParent (SystemEnvironment env, Long p_stickyParent)
	throws SDMSException
	{
		if(p_stickyParent != null && p_stickyParent.equals(stickyParent)) return;
		if(p_stickyParent == null && stickyParent == null) return;
		SDMSResourceAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSResourceAllocationGeneric) change(env);
			o.stickyParent = p_stickyParent;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 16);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
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
			case SDMSResourceAllocation.MASTER_REQUEST:
				return "MASTER_REQUEST";
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

	public	void setAllocationType (SystemEnvironment env, Integer p_allocationType)
	throws SDMSException
	{
		if(allocationType.equals(p_allocationType)) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.allocationType = p_allocationType;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getRsmpId (SystemEnvironment env)
	throws SDMSException
	{
		return (rsmpId);
	}

	public	void setRsmpId (SystemEnvironment env, Long p_rsmpId)
	throws SDMSException
	{
		if(p_rsmpId != null && p_rsmpId.equals(rsmpId)) return;
		if(p_rsmpId == null && rsmpId == null) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.rsmpId = p_rsmpId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		if(p_lockmode != null && p_lockmode.equals(lockmode)) return;
		if(p_lockmode == null && lockmode == null) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.lockmode = p_lockmode;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getRefcount (SystemEnvironment env)
	throws SDMSException
	{
		return (refcount);
	}

	public	void setRefcount (SystemEnvironment env, Integer p_refcount)
	throws SDMSException
	{
		if(refcount.equals(p_refcount)) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.refcount = p_refcount;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return;
		SDMSResourceAllocationGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceAllocation) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSResourceAllocationGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.changerUId = p_changerUId;
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return;
		SDMSResourceAllocationGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceAllocationGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSResourceAllocationGeneric set_SmeIdRIdStickyName (SystemEnvironment env, Long p_smeId, Long p_rId, String p_stickyName)
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
			if (p_stickyName != null && p_stickyName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(ResourceAllocation) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.stickyName = p_stickyName;
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

	public SDMSResourceAllocationGeneric set_StickyParentRIdStickyName (SystemEnvironment env, Long p_stickyParent, Long p_rId, String p_stickyName)
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
			o.stickyParent = p_stickyParent;
			o.rId = p_rId;
			if (p_stickyName != null && p_stickyName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(ResourceAllocation) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.stickyName = p_stickyName;
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
	                                        String p_stickyName,
	                                        Long p_stickyParent,
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
		stickyName = p_stickyName;
		stickyParent = p_stickyParent;
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
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
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
				        ", " + squote + "STICKY_NAME" + equote +
				        ", " + squote + "STICKY_PARENT" + equote +
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
				        ", ?" +
				        ", ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "ResourceAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, rId.longValue());
			myInsert.setLong (3, smeId.longValue());
			myInsert.setLong (4, nrId.longValue());
			if (amount == null)
				myInsert.setNull(5, Types.INTEGER);
			else
				myInsert.setInt(5, amount.intValue());
			if (origAmount == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setInt(6, origAmount.intValue());
			myInsert.setInt(7, keepMode.intValue());
			myInsert.setInt (8, isSticky.booleanValue() ? 1 : 0);
			if (stickyName == null)
				myInsert.setNull(9, Types.VARCHAR);
			else
				myInsert.setString(9, stickyName);
			if (stickyParent == null)
				myInsert.setNull(10, Types.INTEGER);
			else
				myInsert.setLong (10, stickyParent.longValue());
			myInsert.setInt(11, allocationType.intValue());
			if (rsmpId == null)
				myInsert.setNull(12, Types.INTEGER);
			else
				myInsert.setLong (12, rsmpId.longValue());
			if (lockmode == null)
				myInsert.setNull(13, Types.INTEGER);
			else
				myInsert.setInt(13, lockmode.intValue());
			myInsert.setInt(14, refcount.intValue());
			myInsert.setLong (15, creatorUId.longValue());
			myInsert.setLong (16, createTs.longValue());
			myInsert.setLong (17, changerUId.longValue());
			myInsert.setLong (18, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "ResourceAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				stmt =
				        "DELETE FROM RESOURCE_ALLOCATION WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "ResourceAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "ResourceAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE RESOURCE_ALLOCATION SET " +
				        "" + squote + "R_ID" + equote + " = ? " +
				        ", " + squote + "SME_ID" + equote + " = ? " +
				        ", " + squote + "NR_ID" + equote + " = ? " +
				        ", " + squote + "AMOUNT" + equote + " = ? " +
				        ", " + squote + "ORIG_AMOUNT" + equote + " = ? " +
				        ", " + squote + "KEEP_MODE" + equote + " = ? " +
				        ", " + squote + "IS_STICKY" + equote + " = ? " +
				        ", " + squote + "STICKY_NAME" + equote + " = ? " +
				        ", " + squote + "STICKY_PARENT" + equote + " = ? " +
				        ", " + squote + "ALLOCATION_TYPE" + equote + " = ? " +
				        ", " + squote + "RSMP_ID" + equote + " = ? " +
				        ", " + squote + "LOCKMODE" + equote + " = ? " +
				        ", " + squote + "REFCOUNT" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "ResourceAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, rId.longValue());
			myUpdate.setLong (2, smeId.longValue());
			myUpdate.setLong (3, nrId.longValue());
			if (amount == null)
				myUpdate.setNull(4, Types.INTEGER);
			else
				myUpdate.setInt(4, amount.intValue());
			if (origAmount == null)
				myUpdate.setNull(5, Types.INTEGER);
			else
				myUpdate.setInt(5, origAmount.intValue());
			myUpdate.setInt(6, keepMode.intValue());
			myUpdate.setInt (7, isSticky.booleanValue() ? 1 : 0);
			if (stickyName == null)
				myUpdate.setNull(8, Types.VARCHAR);
			else
				myUpdate.setString(8, stickyName);
			if (stickyParent == null)
				myUpdate.setNull(9, Types.INTEGER);
			else
				myUpdate.setLong (9, stickyParent.longValue());
			myUpdate.setInt(10, allocationType.intValue());
			if (rsmpId == null)
				myUpdate.setNull(11, Types.INTEGER);
			else
				myUpdate.setLong (11, rsmpId.longValue());
			if (lockmode == null)
				myUpdate.setNull(12, Types.INTEGER);
			else
				myUpdate.setInt(12, lockmode.intValue());
			myUpdate.setInt(13, refcount.intValue());
			myUpdate.setLong (14, creatorUId.longValue());
			myUpdate.setLong (15, createTs.longValue());
			myUpdate.setLong (16, changerUId.longValue());
			myUpdate.setLong (17, changeTs.longValue());
			myUpdate.setLong(18, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "ResourceAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
			case SDMSResourceAllocation.MASTER_REQUEST:
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
		SDMSThread.doTrace(null, "stickyName : " + stickyName, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "stickyParent : " + stickyParent, SDMSThread.SEVERITY_MESSAGE);
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
		dumpVersions(SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "stickyName     : " + stickyName + "\n" +
		        indentString + "stickyParent   : " + stickyParent + "\n" +
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
