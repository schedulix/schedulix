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

public class SDMSResourceRequirementGeneric extends SDMSObject
	implements Cloneable
{

	public static final int N = Lockmode.N;
	public static final int X = Lockmode.X;
	public static final int SX = Lockmode.SX;
	public static final int S = Lockmode.S;
	public static final int SC = Lockmode.SC;
	public static final int NOKEEP = 0;
	public static final int KEEP = 1;
	public static final int KEEP_FINAL = 2;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;

	public final static int nr_id = 1;
	public final static int nr_nrId = 2;
	public final static int nr_seId = 3;
	public final static int nr_amount = 4;
	public final static int nr_keepMode = 5;
	public final static int nr_isSticky = 6;
	public final static int nr_stickyName = 7;
	public final static int nr_stickyParent = 8;
	public final static int nr_rsmpId = 9;
	public final static int nr_expiredAmount = 10;
	public final static int nr_expiredBase = 11;
	public final static int nr_ignoreOnRerun = 12;
	public final static int nr_lockmode = 13;
	public final static int nr_condition = 14;
	public final static int nr_creatorUId = 15;
	public final static int nr_createTs = 16;
	public final static int nr_changerUId = 17;
	public final static int nr_changeTs = 18;

	public static String tableName = SDMSResourceRequirementTableGeneric.tableName;

	protected Long nrId;
	protected Long seId;
	protected Integer amount;
	protected Integer keepMode;
	protected Boolean isSticky;
	protected String stickyName;
	protected Long stickyParent;
	protected Long rsmpId;
	protected Integer expiredAmount;
	protected Integer expiredBase;
	protected Boolean ignoreOnRerun;
	protected Integer lockmode;
	protected String condition;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSResourceRequirementGeneric(
	        SystemEnvironment env,
	        Long p_nrId,
	        Long p_seId,
	        Integer p_amount,
	        Integer p_keepMode,
	        Boolean p_isSticky,
	        String p_stickyName,
	        Long p_stickyParent,
	        Long p_rsmpId,
	        Integer p_expiredAmount,
	        Integer p_expiredBase,
	        Boolean p_ignoreOnRerun,
	        Integer p_lockmode,
	        String p_condition,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSResourceRequirementTableGeneric.table);
		nrId = p_nrId;
		seId = p_seId;
		amount = p_amount;
		keepMode = p_keepMode;
		isSticky = p_isSticky;
		if (p_stickyName != null && p_stickyName.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ResourceRequirement) Length of $1 exceeds maximum length $2", "stickyName", "64")
			);
		}
		stickyName = p_stickyName;
		stickyParent = p_stickyParent;
		rsmpId = p_rsmpId;
		expiredAmount = p_expiredAmount;
		expiredBase = p_expiredBase;
		ignoreOnRerun = p_ignoreOnRerun;
		lockmode = p_lockmode;
		if (p_condition != null && p_condition.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ResourceRequirement) Length of $1 exceeds maximum length $2", "condition", "1024")
			);
		}
		condition = p_condition;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
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
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
			o.nrId = p_nrId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 9);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
			o.seId = p_seId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 10);
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
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.amount = p_amount;
		o.changerUId = env.cEnv.uid();
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
			case SDMSResourceRequirement.NOKEEP:
				return "NOKEEP";
			case SDMSResourceRequirement.KEEP:
				return "KEEP";
			case SDMSResourceRequirement.KEEP_FINAL:
				return "KEEP_FINAL";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ResourceRequirement.keepMode: $1",
		                          getKeepMode (env)));
	}

	public	void setKeepMode (SystemEnvironment env, Integer p_keepMode)
	throws SDMSException
	{
		if(keepMode.equals(p_keepMode)) return;
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.keepMode = p_keepMode;
		o.changerUId = env.cEnv.uid();
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
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.isSticky = p_isSticky;
		o.changerUId = env.cEnv.uid();
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
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		if (p_stickyName != null && p_stickyName.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(ResourceRequirement) Length of $1 exceeds maximum length $2", "stickyName", "64")
			);
		}
		o.stickyName = p_stickyName;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
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
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.stickyParent = p_stickyParent;
		o.changerUId = env.cEnv.uid();
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
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
			o.rsmpId = p_rsmpId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getExpiredAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (expiredAmount);
	}

	public	void setExpiredAmount (SystemEnvironment env, Integer p_expiredAmount)
	throws SDMSException
	{
		if(p_expiredAmount != null && p_expiredAmount.equals(expiredAmount)) return;
		if(p_expiredAmount == null && expiredAmount == null) return;
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.expiredAmount = p_expiredAmount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getExpiredBase (SystemEnvironment env)
	throws SDMSException
	{
		return (expiredBase);
	}

	public String getExpiredBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getExpiredBase (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
			case SDMSResourceRequirement.MINUTE:
				return "MINUTE";
			case SDMSResourceRequirement.HOUR:
				return "HOUR";
			case SDMSResourceRequirement.DAY:
				return "DAY";
			case SDMSResourceRequirement.WEEK:
				return "WEEK";
			case SDMSResourceRequirement.MONTH:
				return "MONTH";
			case SDMSResourceRequirement.YEAR:
				return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ResourceRequirement.expiredBase: $1",
		                          getExpiredBase (env)));
	}

	public	void setExpiredBase (SystemEnvironment env, Integer p_expiredBase)
	throws SDMSException
	{
		if(p_expiredBase != null && p_expiredBase.equals(expiredBase)) return;
		if(p_expiredBase == null && expiredBase == null) return;
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.expiredBase = p_expiredBase;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIgnoreOnRerun (SystemEnvironment env)
	throws SDMSException
	{
		return (ignoreOnRerun);
	}

	public	void setIgnoreOnRerun (SystemEnvironment env, Boolean p_ignoreOnRerun)
	throws SDMSException
	{
		if(ignoreOnRerun.equals(p_ignoreOnRerun)) return;
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.ignoreOnRerun = p_ignoreOnRerun;
		o.changerUId = env.cEnv.uid();
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
			case SDMSResourceRequirement.N:
				return "N";
			case SDMSResourceRequirement.X:
				return "X";
			case SDMSResourceRequirement.SX:
				return "SX";
			case SDMSResourceRequirement.S:
				return "S";
			case SDMSResourceRequirement.SC:
				return "SC";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ResourceRequirement.lockmode: $1",
		                          getLockmode (env)));
	}

	public	void setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		if(p_lockmode != null && p_lockmode.equals(lockmode)) return;
		if(p_lockmode == null && lockmode == null) return;
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.lockmode = p_lockmode;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		return (condition);
	}

	public	void setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		if(p_condition != null && p_condition.equals(condition)) return;
		if(p_condition == null && condition == null) return;
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		if (p_condition != null && p_condition.length() > 1024) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(ResourceRequirement) Length of $1 exceeds maximum length $2", "condition", "1024")
			);
		}
		o.condition = p_condition;
		o.changerUId = env.cEnv.uid();
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
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.uid();
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
		SDMSResourceRequirementGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.uid();
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
		SDMSResourceRequirementGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
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
		SDMSResourceRequirementGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSResourceRequirementGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSResourceRequirementGeneric set_SeIdNrId (SystemEnvironment env, Long p_seId, Long p_nrId)
	throws SDMSException
	{
		SDMSResourceRequirementGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
			o.seId = p_seId;
			o.nrId = p_nrId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return SDMSResourceRequirement.getProxy(sysEnv, this);
	}

	protected SDMSResourceRequirementGeneric(Long p_id,
	                Long p_nrId,
	                Long p_seId,
	                Integer p_amount,
	                Integer p_keepMode,
	                Boolean p_isSticky,
	                String p_stickyName,
	                Long p_stickyParent,
	                Long p_rsmpId,
	                Integer p_expiredAmount,
	                Integer p_expiredBase,
	                Boolean p_ignoreOnRerun,
	                Integer p_lockmode,
	                String p_condition,
	                Long p_creatorUId,
	                Long p_createTs,
	                Long p_changerUId,
	                Long p_changeTs,
	                long p_validFrom, long p_validTo)
	{
		id     = p_id;
		nrId = p_nrId;
		seId = p_seId;
		amount = p_amount;
		keepMode = p_keepMode;
		isSticky = p_isSticky;
		stickyName = p_stickyName;
		stickyParent = p_stickyParent;
		rsmpId = p_rsmpId;
		expiredAmount = p_expiredAmount;
		expiredBase = p_expiredBase;
		ignoreOnRerun = p_ignoreOnRerun;
		lockmode = p_lockmode;
		condition = p_condition;
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
				        "INSERT INTO " + squote + "RESOURCE_REQUIREMENT" + equote + " (" +
				        "ID" +
				        ", " + squote + "NR_ID" + equote +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "AMOUNT" + equote +
				        ", " + squote + "KEEP_MODE" + equote +
				        ", " + squote + "IS_STICKY" + equote +
				        ", " + squote + "STICKY_NAME" + equote +
				        ", " + squote + "STICKY_PARENT" + equote +
				        ", " + squote + "RSMP_ID" + equote +
				        ", " + squote + "EXPIRED_AMOUNT" + equote +
				        ", " + squote + "EXPIRED_BASE" + equote +
				        ", " + squote + "IGNORE_ON_RERUN" + equote +
				        ", " + squote + "LOCKMODE" + equote +
				        ", " + squote + "CONDITION" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ", VALID_FROM, VALID_TO" +
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
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "ResourceRequirement: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, nrId.longValue());
			myInsert.setLong (3, seId.longValue());
			if (amount == null)
				myInsert.setNull(4, Types.INTEGER);
			else
				myInsert.setInt(4, amount.intValue());
			myInsert.setInt(5, keepMode.intValue());
			myInsert.setInt (6, isSticky.booleanValue() ? 1 : 0);
			if (stickyName == null)
				myInsert.setNull(7, Types.VARCHAR);
			else
				myInsert.setString(7, stickyName);
			if (stickyParent == null)
				myInsert.setNull(8, Types.INTEGER);
			else
				myInsert.setLong (8, stickyParent.longValue());
			if (rsmpId == null)
				myInsert.setNull(9, Types.INTEGER);
			else
				myInsert.setLong (9, rsmpId.longValue());
			if (expiredAmount == null)
				myInsert.setNull(10, Types.INTEGER);
			else
				myInsert.setInt(10, expiredAmount.intValue());
			if (expiredBase == null)
				myInsert.setNull(11, Types.INTEGER);
			else
				myInsert.setInt(11, expiredBase.intValue());
			myInsert.setInt (12, ignoreOnRerun.booleanValue() ? 1 : 0);
			if (lockmode == null)
				myInsert.setNull(13, Types.INTEGER);
			else
				myInsert.setInt(13, lockmode.intValue());
			if (condition == null)
				myInsert.setNull(14, Types.VARCHAR);
			else
				myInsert.setString(14, condition);
			myInsert.setLong (15, creatorUId.longValue());
			myInsert.setLong (16, createTs.longValue());
			myInsert.setLong (17, changerUId.longValue());
			myInsert.setLong (18, changeTs.longValue());
			myInsert.setLong(19, env.tx.versionId);
			myInsert.setLong(20, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "ResourceRequirement: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		closeDBObject(env);
		insertDBObject(env);
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		closeDBObject(env);
	}

	private void closeDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				final boolean postgres = driverName.startsWith("PostgreSQL");
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE " + squote + "RESOURCE_REQUIREMENT" + equote +
				        " SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181955", "ResourceRequirement : $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong(1, env.tx.versionId);
			myUpdate.setLong(2, changeTs.longValue());
			myUpdate.setLong(3, changerUId.longValue());
			myUpdate.setLong(4, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "ResourceRequirement: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkKeepMode(Integer p)
	{
		switch (p.intValue()) {
			case SDMSResourceRequirement.NOKEEP:
			case SDMSResourceRequirement.KEEP:
			case SDMSResourceRequirement.KEEP_FINAL:
				return true;
		}
		return false;
	}
	static public boolean checkExpiredBase(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
			case SDMSResourceRequirement.MINUTE:
			case SDMSResourceRequirement.HOUR:
			case SDMSResourceRequirement.DAY:
			case SDMSResourceRequirement.WEEK:
			case SDMSResourceRequirement.MONTH:
			case SDMSResourceRequirement.YEAR:
				return true;
		}
		return false;
	}
	static public boolean checkLockmode(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
			case SDMSResourceRequirement.N:
			case SDMSResourceRequirement.X:
			case SDMSResourceRequirement.SX:
			case SDMSResourceRequirement.S:
			case SDMSResourceRequirement.SC:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ResourceRequirement", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nrId : " + nrId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "amount : " + amount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "keepMode : " + keepMode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSticky : " + isSticky, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "stickyName : " + stickyName, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "stickyParent : " + stickyParent, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rsmpId : " + rsmpId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "expiredAmount : " + expiredAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "expiredBase : " + expiredBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ignoreOnRerun : " + ignoreOnRerun, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lockmode : " + lockmode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "condition : " + condition, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "nrId          : " + nrId + "\n" +
		        indentString + "seId          : " + seId + "\n" +
		        indentString + "amount        : " + amount + "\n" +
		        indentString + "keepMode      : " + keepMode + "\n" +
		        indentString + "isSticky      : " + isSticky + "\n" +
		        indentString + "stickyName    : " + stickyName + "\n" +
		        indentString + "stickyParent  : " + stickyParent + "\n" +
		        indentString + "rsmpId        : " + rsmpId + "\n" +
		        indentString + "expiredAmount : " + expiredAmount + "\n" +
		        indentString + "expiredBase   : " + expiredBase + "\n" +
		        indentString + "ignoreOnRerun : " + ignoreOnRerun + "\n" +
		        indentString + "lockmode      : " + lockmode + "\n" +
		        indentString + "condition     : " + condition + "\n" +
		        indentString + "creatorUId    : " + creatorUId + "\n" +
		        indentString + "createTs      : " + createTs + "\n" +
		        indentString + "changerUId    : " + changerUId + "\n" +
		        indentString + "changeTs      : " + changeTs + "\n" +
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
