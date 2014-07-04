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

public class SDMSResourceGeneric extends SDMSObject
	implements Cloneable
{

	public static final int REASON_AVAILABLE = 0;
	public static final int REASON_LOCKMODE = 1;
	public static final int REASON_AMOUNT = 2;
	public static final int REASON_STATE = 4;
	public static final int REASON_EXPIRE = 8;
	public static final int REASON_OFFLINE = 16;

	public final static int nr_id = 1;
	public final static int nr_nrId = 2;
	public final static int nr_scopeId = 3;
	public final static int nr_masterId = 4;
	public final static int nr_ownerId = 5;
	public final static int nr_linkId = 6;
	public final static int nr_managerId = 7;
	public final static int nr_tag = 8;
	public final static int nr_rsdId = 9;
	public final static int nr_rsdTime = 10;
	public final static int nr_definedAmount = 11;
	public final static int nr_requestableAmount = 12;
	public final static int nr_amount = 13;
	public final static int nr_freeAmount = 14;
	public final static int nr_isOnline = 15;
	public final static int nr_factor = 16;
	public final static int nr_traceInterval = 17;
	public final static int nr_traceBase = 18;
	public final static int nr_traceBaseMultiplier = 19;
	public final static int nr_td0Avg = 20;
	public final static int nr_td1Avg = 21;
	public final static int nr_td2Avg = 22;
	public final static int nr_lwAvg = 23;
	public final static int nr_lastEval = 24;
	public final static int nr_lastWrite = 25;
	public final static int nr_creatorUId = 26;
	public final static int nr_createTs = 27;
	public final static int nr_changerUId = 28;
	public final static int nr_changeTs = 29;

	public static String tableName = SDMSResourceTableGeneric.tableName;

	protected Long nrId;
	protected Long scopeId;
	protected Long masterId;
	protected Long ownerId;
	protected Long linkId;
	protected Long managerId;
	protected String tag;
	protected Long rsdId;
	protected Long rsdTime;
	protected Integer definedAmount;
	protected Integer requestableAmount;
	protected Integer amount;
	protected Integer freeAmount;
	protected Boolean isOnline;
	protected Float factor;
	protected Integer traceInterval;
	protected Integer traceBase;
	protected Integer traceBaseMultiplier;
	protected Float td0Avg;
	protected Float td1Avg;
	protected Float td2Avg;
	protected Float lwAvg;
	protected Long lastEval;
	protected Long lastWrite;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSResourceGeneric(
	        SystemEnvironment env,
	        Long p_nrId,
	        Long p_scopeId,
	        Long p_masterId,
	        Long p_ownerId,
	        Long p_linkId,
	        Long p_managerId,
	        String p_tag,
	        Long p_rsdId,
	        Long p_rsdTime,
	        Integer p_definedAmount,
	        Integer p_requestableAmount,
	        Integer p_amount,
	        Integer p_freeAmount,
	        Boolean p_isOnline,
	        Float p_factor,
	        Integer p_traceInterval,
	        Integer p_traceBase,
	        Integer p_traceBaseMultiplier,
	        Float p_td0Avg,
	        Float p_td1Avg,
	        Float p_td2Avg,
	        Float p_lwAvg,
	        Long p_lastEval,
	        Long p_lastWrite,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSResourceTableGeneric.table);
		nrId = p_nrId;
		scopeId = p_scopeId;
		masterId = p_masterId;
		ownerId = p_ownerId;
		linkId = p_linkId;
		managerId = p_managerId;
		if (p_tag != null && p_tag.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Resource) Length of $1 exceeds maximum length $2", "tag", "64")
			);
		}
		tag = p_tag;
		rsdId = p_rsdId;
		rsdTime = p_rsdTime;
		definedAmount = p_definedAmount;
		requestableAmount = p_requestableAmount;
		amount = p_amount;
		freeAmount = p_freeAmount;
		isOnline = p_isOnline;
		factor = p_factor;
		traceInterval = p_traceInterval;
		traceBase = p_traceBase;
		traceBaseMultiplier = p_traceBaseMultiplier;
		td0Avg = p_td0Avg;
		td1Avg = p_td1Avg;
		td2Avg = p_td2Avg;
		lwAvg = p_lwAvg;
		lastEval = p_lastEval;
		lastWrite = p_lastWrite;
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

	public	SDMSResourceGeneric setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		if(nrId.equals(p_nrId)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
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

	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		return (scopeId);
	}

	public	SDMSResourceGeneric setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		if(p_scopeId != null && p_scopeId.equals(scopeId)) return this;
		if(p_scopeId == null && scopeId == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.scopeId = p_scopeId;
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

	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		return (masterId);
	}

	public	SDMSResourceGeneric setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		if(p_masterId != null && p_masterId.equals(masterId)) return this;
		if(p_masterId == null && masterId == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.masterId = p_masterId;
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

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (ownerId);
	}

	public	SDMSResourceGeneric setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.ownerId = p_ownerId;
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

	public Long getLinkId (SystemEnvironment env)
	throws SDMSException
	{
		return (linkId);
	}

	public	SDMSResourceGeneric setLinkId (SystemEnvironment env, Long p_linkId)
	throws SDMSException
	{
		if(p_linkId != null && p_linkId.equals(linkId)) return this;
		if(p_linkId == null && linkId == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.linkId = p_linkId;
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

	public Long getManagerId (SystemEnvironment env)
	throws SDMSException
	{
		return (managerId);
	}

	public	SDMSResourceGeneric setManagerId (SystemEnvironment env, Long p_managerId)
	throws SDMSException
	{
		if(p_managerId != null && p_managerId.equals(managerId)) return this;
		if(p_managerId == null && managerId == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.managerId = p_managerId;
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

	public String getTag (SystemEnvironment env)
	throws SDMSException
	{
		return (tag);
	}

	public	SDMSResourceGeneric setTag (SystemEnvironment env, String p_tag)
	throws SDMSException
	{
		if(p_tag != null && p_tag.equals(tag)) return this;
		if(p_tag == null && tag == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			if (p_tag != null && p_tag.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Resource) Length of $1 exceeds maximum length $2", "tag", "64")
				);
			}
			o.tag = p_tag;
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

	public Long getRsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (rsdId);
	}

	public	SDMSResourceGeneric setRsdId (SystemEnvironment env, Long p_rsdId)
	throws SDMSException
	{
		if(p_rsdId != null && p_rsdId.equals(rsdId)) return this;
		if(p_rsdId == null && rsdId == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.rsdId = p_rsdId;
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

	public Long getRsdTime (SystemEnvironment env)
	throws SDMSException
	{
		return (rsdTime);
	}

	public	SDMSResourceGeneric setRsdTime (SystemEnvironment env, Long p_rsdTime)
	throws SDMSException
	{
		if(p_rsdTime != null && p_rsdTime.equals(rsdTime)) return this;
		if(p_rsdTime == null && rsdTime == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.rsdTime = p_rsdTime;
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

	public Integer getDefinedAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (definedAmount);
	}

	public	SDMSResourceGeneric setDefinedAmount (SystemEnvironment env, Integer p_definedAmount)
	throws SDMSException
	{
		if(p_definedAmount != null && p_definedAmount.equals(definedAmount)) return this;
		if(p_definedAmount == null && definedAmount == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.definedAmount = p_definedAmount;
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

	public Integer getRequestableAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (requestableAmount);
	}

	public	SDMSResourceGeneric setRequestableAmount (SystemEnvironment env, Integer p_requestableAmount)
	throws SDMSException
	{
		if(p_requestableAmount != null && p_requestableAmount.equals(requestableAmount)) return this;
		if(p_requestableAmount == null && requestableAmount == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.requestableAmount = p_requestableAmount;
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

	public	SDMSResourceGeneric setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		if(p_amount != null && p_amount.equals(amount)) return this;
		if(p_amount == null && amount == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
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

	public Integer getFreeAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (freeAmount);
	}

	public	SDMSResourceGeneric setFreeAmount (SystemEnvironment env, Integer p_freeAmount)
	throws SDMSException
	{
		if(p_freeAmount != null && p_freeAmount.equals(freeAmount)) return this;
		if(p_freeAmount == null && freeAmount == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.freeAmount = p_freeAmount;
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

	public Boolean getIsOnline (SystemEnvironment env)
	throws SDMSException
	{
		return (isOnline);
	}

	public	SDMSResourceGeneric setIsOnline (SystemEnvironment env, Boolean p_isOnline)
	throws SDMSException
	{
		if(p_isOnline != null && p_isOnline.equals(isOnline)) return this;
		if(p_isOnline == null && isOnline == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.isOnline = p_isOnline;
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

	public Float getFactor (SystemEnvironment env)
	throws SDMSException
	{
		return (factor);
	}

	public	SDMSResourceGeneric setFactor (SystemEnvironment env, Float p_factor)
	throws SDMSException
	{
		if(p_factor != null && p_factor.equals(factor)) return this;
		if(p_factor == null && factor == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.factor = p_factor;
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

	public Integer getTraceInterval (SystemEnvironment env)
	throws SDMSException
	{
		return (traceInterval);
	}

	public	SDMSResourceGeneric setTraceInterval (SystemEnvironment env, Integer p_traceInterval)
	throws SDMSException
	{
		if(p_traceInterval != null && p_traceInterval.equals(traceInterval)) return this;
		if(p_traceInterval == null && traceInterval == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.traceInterval = p_traceInterval;
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

	public Integer getTraceBase (SystemEnvironment env)
	throws SDMSException
	{
		return (traceBase);
	}

	public	SDMSResourceGeneric setTraceBase (SystemEnvironment env, Integer p_traceBase)
	throws SDMSException
	{
		if(p_traceBase != null && p_traceBase.equals(traceBase)) return this;
		if(p_traceBase == null && traceBase == null) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.traceBase = p_traceBase;
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

	public Integer getTraceBaseMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		return (traceBaseMultiplier);
	}

	public	SDMSResourceGeneric setTraceBaseMultiplier (SystemEnvironment env, Integer p_traceBaseMultiplier)
	throws SDMSException
	{
		if(traceBaseMultiplier.equals(p_traceBaseMultiplier)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.traceBaseMultiplier = p_traceBaseMultiplier;
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

	public Float getTd0Avg (SystemEnvironment env)
	throws SDMSException
	{
		return (td0Avg);
	}

	public	SDMSResourceGeneric setTd0Avg (SystemEnvironment env, Float p_td0Avg)
	throws SDMSException
	{
		if(td0Avg.equals(p_td0Avg)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.td0Avg = p_td0Avg;
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

	public Float getTd1Avg (SystemEnvironment env)
	throws SDMSException
	{
		return (td1Avg);
	}

	public	SDMSResourceGeneric setTd1Avg (SystemEnvironment env, Float p_td1Avg)
	throws SDMSException
	{
		if(td1Avg.equals(p_td1Avg)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.td1Avg = p_td1Avg;
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

	public Float getTd2Avg (SystemEnvironment env)
	throws SDMSException
	{
		return (td2Avg);
	}

	public	SDMSResourceGeneric setTd2Avg (SystemEnvironment env, Float p_td2Avg)
	throws SDMSException
	{
		if(td2Avg.equals(p_td2Avg)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.td2Avg = p_td2Avg;
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

	public Float getLwAvg (SystemEnvironment env)
	throws SDMSException
	{
		return (lwAvg);
	}

	public	SDMSResourceGeneric setLwAvg (SystemEnvironment env, Float p_lwAvg)
	throws SDMSException
	{
		if(lwAvg.equals(p_lwAvg)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.lwAvg = p_lwAvg;
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

	public Long getLastEval (SystemEnvironment env)
	throws SDMSException
	{
		return (lastEval);
	}

	public	SDMSResourceGeneric setLastEval (SystemEnvironment env, Long p_lastEval)
	throws SDMSException
	{
		if(lastEval.equals(p_lastEval)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.lastEval = p_lastEval;
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

	public Long getLastWrite (SystemEnvironment env)
	throws SDMSException
	{
		return (lastWrite);
	}

	public	SDMSResourceGeneric setLastWrite (SystemEnvironment env, Long p_lastWrite)
	throws SDMSException
	{
		if(lastWrite.equals(p_lastWrite)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.lastWrite = p_lastWrite;
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

	SDMSResourceGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
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

	SDMSResourceGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
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

	public	SDMSResourceGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSResourceGeneric) change(env);
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

	SDMSResourceGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSResourceGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSResourceGeneric) change(env);
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

	public SDMSResourceGeneric set_NrIdScopeId (SystemEnvironment env, Long p_nrId, Long p_scopeId)
	throws SDMSException
	{
		SDMSResourceGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(Resource) Change of system object not allowed")
				);
			}
			o = (SDMSResourceGeneric) change(env);
			o.nrId = p_nrId;
			o.scopeId = p_scopeId;
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
		return new SDMSResource(this);
	}

	protected SDMSResourceGeneric(Long p_id,
	                              Long p_nrId,
	                              Long p_scopeId,
	                              Long p_masterId,
	                              Long p_ownerId,
	                              Long p_linkId,
	                              Long p_managerId,
	                              String p_tag,
	                              Long p_rsdId,
	                              Long p_rsdTime,
	                              Integer p_definedAmount,
	                              Integer p_requestableAmount,
	                              Integer p_amount,
	                              Integer p_freeAmount,
	                              Boolean p_isOnline,
	                              Float p_factor,
	                              Integer p_traceInterval,
	                              Integer p_traceBase,
	                              Integer p_traceBaseMultiplier,
	                              Float p_td0Avg,
	                              Float p_td1Avg,
	                              Float p_td2Avg,
	                              Float p_lwAvg,
	                              Long p_lastEval,
	                              Long p_lastWrite,
	                              Long p_creatorUId,
	                              Long p_createTs,
	                              Long p_changerUId,
	                              Long p_changeTs,
	                              long p_validFrom, long p_validTo)
	{
		id     = p_id;
		nrId = p_nrId;
		scopeId = p_scopeId;
		masterId = p_masterId;
		ownerId = p_ownerId;
		linkId = p_linkId;
		managerId = p_managerId;
		tag = p_tag;
		rsdId = p_rsdId;
		rsdTime = p_rsdTime;
		definedAmount = p_definedAmount;
		requestableAmount = p_requestableAmount;
		amount = p_amount;
		freeAmount = p_freeAmount;
		isOnline = p_isOnline;
		factor = p_factor;
		traceInterval = p_traceInterval;
		traceBase = p_traceBase;
		traceBaseMultiplier = p_traceBaseMultiplier;
		td0Avg = p_td0Avg;
		td1Avg = p_td1Avg;
		td2Avg = p_td2Avg;
		lwAvg = p_lwAvg;
		lastEval = p_lastEval;
		lastWrite = p_lastWrite;
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
				        "INSERT INTO RESSOURCE (" +
				        "ID" +
				        ", " + squote + "NR_ID" + equote +
				        ", " + squote + "SCOPE_ID" + equote +
				        ", " + squote + "MASTER_ID" + equote +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "LINK_ID" + equote +
				        ", " + squote + "MANAGER_ID" + equote +
				        ", " + squote + "TAG" + equote +
				        ", " + squote + "RSD_ID" + equote +
				        ", " + squote + "RSD_TIME" + equote +
				        ", " + squote + "DEFINED_AMOUNT" + equote +
				        ", " + squote + "REQUESTABLE_AMOUNT" + equote +
				        ", " + squote + "AMOUNT" + equote +
				        ", " + squote + "FREE_AMOUNT" + equote +
				        ", " + squote + "IS_ONLINE" + equote +
				        ", " + squote + "FACTOR" + equote +
				        ", " + squote + "TRACE_INTERVAL" + equote +
				        ", " + squote + "TRACE_BASE" + equote +
				        ", " + squote + "TRACE_BASE_MULTIPLIER" + equote +
				        ", " + squote + "TD0_AVG" + equote +
				        ", " + squote + "TD1_AVG" + equote +
				        ", " + squote + "TD2_AVG" + equote +
				        ", " + squote + "LW_AVG" + equote +
				        ", " + squote + "LAST_EVAL" + equote +
				        ", " + squote + "LAST_WRITE" + equote +
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

				throw new FatalException(new SDMSMessage(env, "01110181952", "Resource: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, nrId.longValue());
			if (scopeId == null)
				pInsert.setNull(3, Types.INTEGER);
			else
				pInsert.setLong (3, scopeId.longValue());
			if (masterId == null)
				pInsert.setNull(4, Types.INTEGER);
			else
				pInsert.setLong (4, masterId.longValue());
			pInsert.setLong (5, ownerId.longValue());
			if (linkId == null)
				pInsert.setNull(6, Types.INTEGER);
			else
				pInsert.setLong (6, linkId.longValue());
			if (managerId == null)
				pInsert.setNull(7, Types.INTEGER);
			else
				pInsert.setLong (7, managerId.longValue());
			if (tag == null)
				pInsert.setNull(8, Types.VARCHAR);
			else
				pInsert.setString(8, tag);
			if (rsdId == null)
				pInsert.setNull(9, Types.INTEGER);
			else
				pInsert.setLong (9, rsdId.longValue());
			if (rsdTime == null)
				pInsert.setNull(10, Types.INTEGER);
			else
				pInsert.setLong (10, rsdTime.longValue());
			if (definedAmount == null)
				pInsert.setNull(11, Types.INTEGER);
			else
				pInsert.setInt(11, definedAmount.intValue());
			if (requestableAmount == null)
				pInsert.setNull(12, Types.INTEGER);
			else
				pInsert.setInt(12, requestableAmount.intValue());
			if (amount == null)
				pInsert.setNull(13, Types.INTEGER);
			else
				pInsert.setInt(13, amount.intValue());
			if (freeAmount == null)
				pInsert.setNull(14, Types.INTEGER);
			else
				pInsert.setInt(14, freeAmount.intValue());
			if (isOnline == null)
				pInsert.setNull(15, Types.INTEGER);
			else
				pInsert.setInt (15, isOnline.booleanValue() ? 1 : 0);
			if (factor == null)
				pInsert.setNull(16, Types.FLOAT);
			else
				pInsert.setFloat(16, factor.floatValue());
			if (traceInterval == null)
				pInsert.setNull(17, Types.INTEGER);
			else
				pInsert.setInt(17, traceInterval.intValue());
			if (traceBase == null)
				pInsert.setNull(18, Types.INTEGER);
			else
				pInsert.setInt(18, traceBase.intValue());
			pInsert.setInt(19, traceBaseMultiplier.intValue());
			pInsert.setFloat(20, td0Avg.floatValue());
			pInsert.setFloat(21, td1Avg.floatValue());
			pInsert.setFloat(22, td2Avg.floatValue());
			pInsert.setFloat(23, lwAvg.floatValue());
			pInsert.setLong (24, lastEval.longValue());
			pInsert.setLong (25, lastWrite.longValue());
			pInsert.setLong (26, creatorUId.longValue());
			pInsert.setLong (27, createTs.longValue());
			pInsert.setLong (28, changerUId.longValue());
			pInsert.setLong (29, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "Resource: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM RESSOURCE WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "Resource: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "Resource: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE RESSOURCE SET " +
				        "" + squote + "NR_ID" + equote + " = ? " +
				        ", " + squote + "SCOPE_ID" + equote + " = ? " +
				        ", " + squote + "MASTER_ID" + equote + " = ? " +
				        ", " + squote + "OWNER_ID" + equote + " = ? " +
				        ", " + squote + "LINK_ID" + equote + " = ? " +
				        ", " + squote + "MANAGER_ID" + equote + " = ? " +
				        ", " + squote + "TAG" + equote + " = ? " +
				        ", " + squote + "RSD_ID" + equote + " = ? " +
				        ", " + squote + "RSD_TIME" + equote + " = ? " +
				        ", " + squote + "DEFINED_AMOUNT" + equote + " = ? " +
				        ", " + squote + "REQUESTABLE_AMOUNT" + equote + " = ? " +
				        ", " + squote + "AMOUNT" + equote + " = ? " +
				        ", " + squote + "FREE_AMOUNT" + equote + " = ? " +
				        ", " + squote + "IS_ONLINE" + equote + " = ? " +
				        ", " + squote + "FACTOR" + equote + " = ? " +
				        ", " + squote + "TRACE_INTERVAL" + equote + " = ? " +
				        ", " + squote + "TRACE_BASE" + equote + " = ? " +
				        ", " + squote + "TRACE_BASE_MULTIPLIER" + equote + " = ? " +
				        ", " + squote + "TD0_AVG" + equote + " = ? " +
				        ", " + squote + "TD1_AVG" + equote + " = ? " +
				        ", " + squote + "TD2_AVG" + equote + " = ? " +
				        ", " + squote + "LW_AVG" + equote + " = ? " +
				        ", " + squote + "LAST_EVAL" + equote + " = ? " +
				        ", " + squote + "LAST_WRITE" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "Resource: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, nrId.longValue());
			if (scopeId == null)
				pUpdate.setNull(2, Types.INTEGER);
			else
				pUpdate.setLong (2, scopeId.longValue());
			if (masterId == null)
				pUpdate.setNull(3, Types.INTEGER);
			else
				pUpdate.setLong (3, masterId.longValue());
			pUpdate.setLong (4, ownerId.longValue());
			if (linkId == null)
				pUpdate.setNull(5, Types.INTEGER);
			else
				pUpdate.setLong (5, linkId.longValue());
			if (managerId == null)
				pUpdate.setNull(6, Types.INTEGER);
			else
				pUpdate.setLong (6, managerId.longValue());
			if (tag == null)
				pUpdate.setNull(7, Types.VARCHAR);
			else
				pUpdate.setString(7, tag);
			if (rsdId == null)
				pUpdate.setNull(8, Types.INTEGER);
			else
				pUpdate.setLong (8, rsdId.longValue());
			if (rsdTime == null)
				pUpdate.setNull(9, Types.INTEGER);
			else
				pUpdate.setLong (9, rsdTime.longValue());
			if (definedAmount == null)
				pUpdate.setNull(10, Types.INTEGER);
			else
				pUpdate.setInt(10, definedAmount.intValue());
			if (requestableAmount == null)
				pUpdate.setNull(11, Types.INTEGER);
			else
				pUpdate.setInt(11, requestableAmount.intValue());
			if (amount == null)
				pUpdate.setNull(12, Types.INTEGER);
			else
				pUpdate.setInt(12, amount.intValue());
			if (freeAmount == null)
				pUpdate.setNull(13, Types.INTEGER);
			else
				pUpdate.setInt(13, freeAmount.intValue());
			if (isOnline == null)
				pUpdate.setNull(14, Types.INTEGER);
			else
				pUpdate.setInt (14, isOnline.booleanValue() ? 1 : 0);
			if (factor == null)
				pUpdate.setNull(15, Types.FLOAT);
			else
				pUpdate.setFloat(15, factor.floatValue());
			if (traceInterval == null)
				pUpdate.setNull(16, Types.INTEGER);
			else
				pUpdate.setInt(16, traceInterval.intValue());
			if (traceBase == null)
				pUpdate.setNull(17, Types.INTEGER);
			else
				pUpdate.setInt(17, traceBase.intValue());
			pUpdate.setInt(18, traceBaseMultiplier.intValue());
			pUpdate.setFloat(19, td0Avg.floatValue());
			pUpdate.setFloat(20, td1Avg.floatValue());
			pUpdate.setFloat(21, td2Avg.floatValue());
			pUpdate.setFloat(22, lwAvg.floatValue());
			pUpdate.setLong (23, lastEval.longValue());
			pUpdate.setLong (24, lastWrite.longValue());
			pUpdate.setLong (25, creatorUId.longValue());
			pUpdate.setLong (26, createTs.longValue());
			pUpdate.setLong (27, changerUId.longValue());
			pUpdate.setLong (28, changeTs.longValue());
			pUpdate.setLong(29, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "Resource: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : Resource", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nrId : " + nrId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "scopeId : " + scopeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "masterId : " + masterId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "linkId : " + linkId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "managerId : " + managerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "tag : " + tag, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rsdId : " + rsdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rsdTime : " + rsdTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "definedAmount : " + definedAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "requestableAmount : " + requestableAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "amount : " + amount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "freeAmount : " + freeAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isOnline : " + isOnline, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "factor : " + factor, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "traceInterval : " + traceInterval, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "traceBase : " + traceBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "traceBaseMultiplier : " + traceBaseMultiplier, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "td0Avg : " + td0Avg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "td1Avg : " + td1Avg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "td2Avg : " + td2Avg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lwAvg : " + lwAvg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lastEval : " + lastEval, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lastWrite : " + lastWrite, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "nrId                : " + nrId + "\n" +
		        indentString + "scopeId             : " + scopeId + "\n" +
		        indentString + "masterId            : " + masterId + "\n" +
		        indentString + "ownerId             : " + ownerId + "\n" +
		        indentString + "linkId              : " + linkId + "\n" +
		        indentString + "managerId           : " + managerId + "\n" +
		        indentString + "tag                 : " + tag + "\n" +
		        indentString + "rsdId               : " + rsdId + "\n" +
		        indentString + "rsdTime             : " + rsdTime + "\n" +
		        indentString + "definedAmount       : " + definedAmount + "\n" +
		        indentString + "requestableAmount   : " + requestableAmount + "\n" +
		        indentString + "amount              : " + amount + "\n" +
		        indentString + "freeAmount          : " + freeAmount + "\n" +
		        indentString + "isOnline            : " + isOnline + "\n" +
		        indentString + "factor              : " + factor + "\n" +
		        indentString + "traceInterval       : " + traceInterval + "\n" +
		        indentString + "traceBase           : " + traceBase + "\n" +
		        indentString + "traceBaseMultiplier : " + traceBaseMultiplier + "\n" +
		        indentString + "td0Avg              : " + td0Avg + "\n" +
		        indentString + "td1Avg              : " + td1Avg + "\n" +
		        indentString + "td2Avg              : " + td2Avg + "\n" +
		        indentString + "lwAvg               : " + lwAvg + "\n" +
		        indentString + "lastEval            : " + lastEval + "\n" +
		        indentString + "lastWrite           : " + lastWrite + "\n" +
		        indentString + "creatorUId          : " + creatorUId + "\n" +
		        indentString + "createTs            : " + createTs + "\n" +
		        indentString + "changerUId          : " + changerUId + "\n" +
		        indentString + "changeTs            : " + changeTs + "\n" +
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
