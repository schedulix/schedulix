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

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

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

	public	void setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		if(nrId.equals(p_nrId)) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 257);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		return (scopeId);
	}

	public	void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		if(p_scopeId != null && p_scopeId.equals(scopeId)) return;
		if(p_scopeId == null && scopeId == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 258);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		return (masterId);
	}

	public	void setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		if(p_masterId != null && p_masterId.equals(masterId)) return;
		if(p_masterId == null && masterId == null) return;
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

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (ownerId);
	}

	public	void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 8);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getLinkId (SystemEnvironment env)
	throws SDMSException
	{
		return (linkId);
	}

	public	void setLinkId (SystemEnvironment env, Long p_linkId)
	throws SDMSException
	{
		if(p_linkId != null && p_linkId.equals(linkId)) return;
		if(p_linkId == null && linkId == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 16);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getManagerId (SystemEnvironment env)
	throws SDMSException
	{
		return (managerId);
	}

	public	void setManagerId (SystemEnvironment env, Long p_managerId)
	throws SDMSException
	{
		if(p_managerId != null && p_managerId.equals(managerId)) return;
		if(p_managerId == null && managerId == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 32);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getTag (SystemEnvironment env)
	throws SDMSException
	{
		return (tag);
	}

	public	void setTag (SystemEnvironment env, String p_tag)
	throws SDMSException
	{
		if(p_tag != null && p_tag.equals(tag)) return;
		if(p_tag == null && tag == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 64);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getRsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (rsdId);
	}

	public	void setRsdId (SystemEnvironment env, Long p_rsdId)
	throws SDMSException
	{
		if(p_rsdId != null && p_rsdId.equals(rsdId)) return;
		if(p_rsdId == null && rsdId == null) return;
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
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 128);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getRsdTime (SystemEnvironment env)
	throws SDMSException
	{
		return (rsdTime);
	}

	public	void setRsdTime (SystemEnvironment env, Long p_rsdTime)
	throws SDMSException
	{
		if(p_rsdTime != null && p_rsdTime.equals(rsdTime)) return;
		if(p_rsdTime == null && rsdTime == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.rsdTime = p_rsdTime;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getDefinedAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (definedAmount);
	}

	public	void setDefinedAmount (SystemEnvironment env, Integer p_definedAmount)
	throws SDMSException
	{
		if(p_definedAmount != null && p_definedAmount.equals(definedAmount)) return;
		if(p_definedAmount == null && definedAmount == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.definedAmount = p_definedAmount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getRequestableAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (requestableAmount);
	}

	public	void setRequestableAmount (SystemEnvironment env, Integer p_requestableAmount)
	throws SDMSException
	{
		if(p_requestableAmount != null && p_requestableAmount.equals(requestableAmount)) return;
		if(p_requestableAmount == null && requestableAmount == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.requestableAmount = p_requestableAmount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
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
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.amount = p_amount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getFreeAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (freeAmount);
	}

	public	void setFreeAmount (SystemEnvironment env, Integer p_freeAmount)
	throws SDMSException
	{
		if(p_freeAmount != null && p_freeAmount.equals(freeAmount)) return;
		if(p_freeAmount == null && freeAmount == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.freeAmount = p_freeAmount;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsOnline (SystemEnvironment env)
	throws SDMSException
	{
		return (isOnline);
	}

	public	void setIsOnline (SystemEnvironment env, Boolean p_isOnline)
	throws SDMSException
	{
		if(p_isOnline != null && p_isOnline.equals(isOnline)) return;
		if(p_isOnline == null && isOnline == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.isOnline = p_isOnline;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Float getFactor (SystemEnvironment env)
	throws SDMSException
	{
		return (factor);
	}

	public	void setFactor (SystemEnvironment env, Float p_factor)
	throws SDMSException
	{
		if(p_factor != null && p_factor.equals(factor)) return;
		if(p_factor == null && factor == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.factor = p_factor;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getTraceInterval (SystemEnvironment env)
	throws SDMSException
	{
		return (traceInterval);
	}

	public	void setTraceInterval (SystemEnvironment env, Integer p_traceInterval)
	throws SDMSException
	{
		if(p_traceInterval != null && p_traceInterval.equals(traceInterval)) return;
		if(p_traceInterval == null && traceInterval == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.traceInterval = p_traceInterval;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getTraceBase (SystemEnvironment env)
	throws SDMSException
	{
		return (traceBase);
	}

	public	void setTraceBase (SystemEnvironment env, Integer p_traceBase)
	throws SDMSException
	{
		if(p_traceBase != null && p_traceBase.equals(traceBase)) return;
		if(p_traceBase == null && traceBase == null) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.traceBase = p_traceBase;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getTraceBaseMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		return (traceBaseMultiplier);
	}

	public	void setTraceBaseMultiplier (SystemEnvironment env, Integer p_traceBaseMultiplier)
	throws SDMSException
	{
		if(traceBaseMultiplier.equals(p_traceBaseMultiplier)) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.traceBaseMultiplier = p_traceBaseMultiplier;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Float getTd0Avg (SystemEnvironment env)
	throws SDMSException
	{
		return (td0Avg);
	}

	public	void setTd0Avg (SystemEnvironment env, Float p_td0Avg)
	throws SDMSException
	{
		if(td0Avg.equals(p_td0Avg)) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.td0Avg = p_td0Avg;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Float getTd1Avg (SystemEnvironment env)
	throws SDMSException
	{
		return (td1Avg);
	}

	public	void setTd1Avg (SystemEnvironment env, Float p_td1Avg)
	throws SDMSException
	{
		if(td1Avg.equals(p_td1Avg)) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.td1Avg = p_td1Avg;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Float getTd2Avg (SystemEnvironment env)
	throws SDMSException
	{
		return (td2Avg);
	}

	public	void setTd2Avg (SystemEnvironment env, Float p_td2Avg)
	throws SDMSException
	{
		if(td2Avg.equals(p_td2Avg)) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.td2Avg = p_td2Avg;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Float getLwAvg (SystemEnvironment env)
	throws SDMSException
	{
		return (lwAvg);
	}

	public	void setLwAvg (SystemEnvironment env, Float p_lwAvg)
	throws SDMSException
	{
		if(lwAvg.equals(p_lwAvg)) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.lwAvg = p_lwAvg;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getLastEval (SystemEnvironment env)
	throws SDMSException
	{
		return (lastEval);
	}

	public	void setLastEval (SystemEnvironment env, Long p_lastEval)
	throws SDMSException
	{
		if(lastEval.equals(p_lastEval)) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.lastEval = p_lastEval;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getLastWrite (SystemEnvironment env)
	throws SDMSException
	{
		return (lastWrite);
	}

	public	void setLastWrite (SystemEnvironment env, Long p_lastWrite)
	throws SDMSException
	{
		if(lastWrite.equals(p_lastWrite)) return;
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.lastWrite = p_lastWrite;
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
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
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
		SDMSResourceGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Resource) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
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
		SDMSResourceGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
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
		SDMSResourceGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSResourceGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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
		return SDMSResource.getProxy(sysEnv, this);
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
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
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
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "Resource: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, nrId.longValue());
			if (scopeId == null)
				myInsert.setNull(3, Types.INTEGER);
			else
				myInsert.setLong (3, scopeId.longValue());
			if (masterId == null)
				myInsert.setNull(4, Types.INTEGER);
			else
				myInsert.setLong (4, masterId.longValue());
			myInsert.setLong (5, ownerId.longValue());
			if (linkId == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setLong (6, linkId.longValue());
			if (managerId == null)
				myInsert.setNull(7, Types.INTEGER);
			else
				myInsert.setLong (7, managerId.longValue());
			if (tag == null)
				myInsert.setNull(8, Types.VARCHAR);
			else
				myInsert.setString(8, tag);
			if (rsdId == null)
				myInsert.setNull(9, Types.INTEGER);
			else
				myInsert.setLong (9, rsdId.longValue());
			if (rsdTime == null)
				myInsert.setNull(10, Types.INTEGER);
			else
				myInsert.setLong (10, rsdTime.longValue());
			if (definedAmount == null)
				myInsert.setNull(11, Types.INTEGER);
			else
				myInsert.setInt(11, definedAmount.intValue());
			if (requestableAmount == null)
				myInsert.setNull(12, Types.INTEGER);
			else
				myInsert.setInt(12, requestableAmount.intValue());
			if (amount == null)
				myInsert.setNull(13, Types.INTEGER);
			else
				myInsert.setInt(13, amount.intValue());
			if (freeAmount == null)
				myInsert.setNull(14, Types.INTEGER);
			else
				myInsert.setInt(14, freeAmount.intValue());
			if (isOnline == null)
				myInsert.setNull(15, Types.INTEGER);
			else
				myInsert.setInt (15, isOnline.booleanValue() ? 1 : 0);
			if (factor == null)
				myInsert.setNull(16, Types.FLOAT);
			else
				myInsert.setFloat(16, factor.floatValue());
			if (traceInterval == null)
				myInsert.setNull(17, Types.INTEGER);
			else
				myInsert.setInt(17, traceInterval.intValue());
			if (traceBase == null)
				myInsert.setNull(18, Types.INTEGER);
			else
				myInsert.setInt(18, traceBase.intValue());
			myInsert.setInt(19, traceBaseMultiplier.intValue());
			myInsert.setFloat(20, td0Avg.floatValue());
			myInsert.setFloat(21, td1Avg.floatValue());
			myInsert.setFloat(22, td2Avg.floatValue());
			myInsert.setFloat(23, lwAvg.floatValue());
			myInsert.setLong (24, lastEval.longValue());
			myInsert.setLong (25, lastWrite.longValue());
			myInsert.setLong (26, creatorUId.longValue());
			myInsert.setLong (27, createTs.longValue());
			myInsert.setLong (28, changerUId.longValue());
			myInsert.setLong (29, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "Resource: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "DELETE FROM RESSOURCE WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "Resource: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "Resource: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "Resource: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, nrId.longValue());
			if (scopeId == null)
				myUpdate.setNull(2, Types.INTEGER);
			else
				myUpdate.setLong (2, scopeId.longValue());
			if (masterId == null)
				myUpdate.setNull(3, Types.INTEGER);
			else
				myUpdate.setLong (3, masterId.longValue());
			myUpdate.setLong (4, ownerId.longValue());
			if (linkId == null)
				myUpdate.setNull(5, Types.INTEGER);
			else
				myUpdate.setLong (5, linkId.longValue());
			if (managerId == null)
				myUpdate.setNull(6, Types.INTEGER);
			else
				myUpdate.setLong (6, managerId.longValue());
			if (tag == null)
				myUpdate.setNull(7, Types.VARCHAR);
			else
				myUpdate.setString(7, tag);
			if (rsdId == null)
				myUpdate.setNull(8, Types.INTEGER);
			else
				myUpdate.setLong (8, rsdId.longValue());
			if (rsdTime == null)
				myUpdate.setNull(9, Types.INTEGER);
			else
				myUpdate.setLong (9, rsdTime.longValue());
			if (definedAmount == null)
				myUpdate.setNull(10, Types.INTEGER);
			else
				myUpdate.setInt(10, definedAmount.intValue());
			if (requestableAmount == null)
				myUpdate.setNull(11, Types.INTEGER);
			else
				myUpdate.setInt(11, requestableAmount.intValue());
			if (amount == null)
				myUpdate.setNull(12, Types.INTEGER);
			else
				myUpdate.setInt(12, amount.intValue());
			if (freeAmount == null)
				myUpdate.setNull(13, Types.INTEGER);
			else
				myUpdate.setInt(13, freeAmount.intValue());
			if (isOnline == null)
				myUpdate.setNull(14, Types.INTEGER);
			else
				myUpdate.setInt (14, isOnline.booleanValue() ? 1 : 0);
			if (factor == null)
				myUpdate.setNull(15, Types.FLOAT);
			else
				myUpdate.setFloat(15, factor.floatValue());
			if (traceInterval == null)
				myUpdate.setNull(16, Types.INTEGER);
			else
				myUpdate.setInt(16, traceInterval.intValue());
			if (traceBase == null)
				myUpdate.setNull(17, Types.INTEGER);
			else
				myUpdate.setInt(17, traceBase.intValue());
			myUpdate.setInt(18, traceBaseMultiplier.intValue());
			myUpdate.setFloat(19, td0Avg.floatValue());
			myUpdate.setFloat(20, td1Avg.floatValue());
			myUpdate.setFloat(21, td2Avg.floatValue());
			myUpdate.setFloat(22, lwAvg.floatValue());
			myUpdate.setLong (23, lastEval.longValue());
			myUpdate.setLong (24, lastWrite.longValue());
			myUpdate.setLong (25, creatorUId.longValue());
			myUpdate.setLong (26, createTs.longValue());
			myUpdate.setLong (27, changerUId.longValue());
			myUpdate.setLong (28, changeTs.longValue());
			myUpdate.setLong(29, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "Resource: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
