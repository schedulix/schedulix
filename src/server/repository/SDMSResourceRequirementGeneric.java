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

	public final static String __version = "SDMSResourceRequirementGeneric $Revision: 2.5 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

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
	public final static int nr_rsmpId = 7;
	public final static int nr_expiredAmount = 8;
	public final static int nr_expiredBase = 9;
	public final static int nr_lockmode = 10;
	public final static int nr_condition = 11;
	public final static int nr_creatorUId = 12;
	public final static int nr_createTs = 13;
	public final static int nr_changerUId = 14;
	public final static int nr_changeTs = 15;

	public static String tableName = SDMSResourceRequirementTableGeneric.tableName;

	protected Long nrId;
	protected Long seId;
	protected Integer amount;
	protected Integer keepMode;
	protected Boolean isSticky;
	protected Long rsmpId;
	protected Integer expiredAmount;
	protected Integer expiredBase;
	protected Integer lockmode;
	protected String condition;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSResourceRequirementGeneric(
	        SystemEnvironment env,
	        Long p_nrId,
	        Long p_seId,
	        Integer p_amount,
	        Integer p_keepMode,
	        Boolean p_isSticky,
	        Long p_rsmpId,
	        Integer p_expiredAmount,
	        Integer p_expiredBase,
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
		rsmpId = p_rsmpId;
		expiredAmount = p_expiredAmount;
		expiredBase = p_expiredBase;
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

	public	SDMSResourceRequirementGeneric setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		if(nrId.equals(p_nrId)) return this;
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

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	SDMSResourceRequirementGeneric setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return this;
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

	public	SDMSResourceRequirementGeneric setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		if(p_amount != null && p_amount.equals(amount)) return this;
		if(p_amount == null && amount == null) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
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

	public	SDMSResourceRequirementGeneric setKeepMode (SystemEnvironment env, Integer p_keepMode)
	throws SDMSException
	{
		if(keepMode.equals(p_keepMode)) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
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

	public	SDMSResourceRequirementGeneric setIsSticky (SystemEnvironment env, Boolean p_isSticky)
	throws SDMSException
	{
		if(isSticky.equals(p_isSticky)) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
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

	public Long getRsmpId (SystemEnvironment env)
	throws SDMSException
	{
		return (rsmpId);
	}

	public	SDMSResourceRequirementGeneric setRsmpId (SystemEnvironment env, Long p_rsmpId)
	throws SDMSException
	{
		if(p_rsmpId != null && p_rsmpId.equals(rsmpId)) return this;
		if(p_rsmpId == null && rsmpId == null) return this;
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

	public Integer getExpiredAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (expiredAmount);
	}

	public	SDMSResourceRequirementGeneric setExpiredAmount (SystemEnvironment env, Integer p_expiredAmount)
	throws SDMSException
	{
		if(p_expiredAmount != null && p_expiredAmount.equals(expiredAmount)) return this;
		if(p_expiredAmount == null && expiredAmount == null) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
			o.expiredAmount = p_expiredAmount;
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

	public	SDMSResourceRequirementGeneric setExpiredBase (SystemEnvironment env, Integer p_expiredBase)
	throws SDMSException
	{
		if(p_expiredBase != null && p_expiredBase.equals(expiredBase)) return this;
		if(p_expiredBase == null && expiredBase == null) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
			o.expiredBase = p_expiredBase;
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

	public	SDMSResourceRequirementGeneric setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		if(p_lockmode != null && p_lockmode.equals(lockmode)) return this;
		if(p_lockmode == null && lockmode == null) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
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

	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		return (condition);
	}

	public	SDMSResourceRequirementGeneric setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		if(p_condition != null && p_condition.equals(condition)) return this;
		if(p_condition == null && condition == null) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
			if (p_condition != null && p_condition.length() > 1024) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(ResourceRequirement) Length of $1 exceeds maximum length $2", "condition", "1024")
				);
			}
			o.condition = p_condition;
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

	SDMSResourceRequirementGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
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

	SDMSResourceRequirementGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceRequirement) Change of system object not allowed")
				);
			}
			o = (SDMSResourceRequirementGeneric) change(env);
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

	public	SDMSResourceRequirementGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSResourceRequirementGeneric) change(env);
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

	SDMSResourceRequirementGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSResourceRequirementGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSResourceRequirementGeneric) change(env);
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
		return new SDMSResourceRequirement(this);
	}

	protected SDMSResourceRequirementGeneric(Long p_id,
	                Long p_nrId,
	                Long p_seId,
	                Integer p_amount,
	                Integer p_keepMode,
	                Boolean p_isSticky,
	                Long p_rsmpId,
	                Integer p_expiredAmount,
	                Integer p_expiredBase,
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
		rsmpId = p_rsmpId;
		expiredAmount = p_expiredAmount;
		expiredBase = p_expiredBase;
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
				        "INSERT INTO RESOURCE_REQUIREMENT (" +
				        "ID" +
				        ", " + squote + "NR_ID" + equote +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "AMOUNT" + equote +
				        ", " + squote + "KEEP_MODE" + equote +
				        ", " + squote + "IS_STICKY" + equote +
				        ", " + squote + "RSMP_ID" + equote +
				        ", " + squote + "EXPIRED_AMOUNT" + equote +
				        ", " + squote + "EXPIRED_BASE" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "ResourceRequirement: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, nrId.longValue());
			pInsert.setLong (3, seId.longValue());
			if (amount == null)
				pInsert.setNull(4, Types.INTEGER);
			else
				pInsert.setInt(4, amount.intValue());
			pInsert.setInt(5, keepMode.intValue());
			pInsert.setInt (6, isSticky.booleanValue() ? 1 : 0);
			if (rsmpId == null)
				pInsert.setNull(7, Types.INTEGER);
			else
				pInsert.setLong (7, rsmpId.longValue());
			if (expiredAmount == null)
				pInsert.setNull(8, Types.INTEGER);
			else
				pInsert.setInt(8, expiredAmount.intValue());
			if (expiredBase == null)
				pInsert.setNull(9, Types.INTEGER);
			else
				pInsert.setInt(9, expiredBase.intValue());
			if (lockmode == null)
				pInsert.setNull(10, Types.INTEGER);
			else
				pInsert.setInt(10, lockmode.intValue());
			if (condition == null)
				pInsert.setNull(11, Types.VARCHAR);
			else
				pInsert.setString(11, condition);
			pInsert.setLong (12, creatorUId.longValue());
			pInsert.setLong (13, createTs.longValue());
			pInsert.setLong (14, changerUId.longValue());
			pInsert.setLong (15, changeTs.longValue());
			pInsert.setLong(16, env.tx.versionId);
			pInsert.setLong(17, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "ResourceRequirement: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
		if(pUpdate == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				final boolean postgres = driverName.startsWith("PostgreSQL");
				stmt =
				        "UPDATE RESOURCE_REQUIREMENT " +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				// Can't prepare statement
				throw new FatalException(new SDMSMessage(env, "01110181955", "ResourceRequirement : $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong(1, env.tx.versionId);
			pUpdate.setLong(2, changeTs.longValue());
			pUpdate.setLong(3, changerUId.longValue());
			pUpdate.setLong(4, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181956", "ResourceRequirement: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
		SDMSThread.doTrace(null, "rsmpId : " + rsmpId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "expiredAmount : " + expiredAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "expiredBase : " + expiredBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lockmode : " + lockmode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "condition : " + condition, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "nrId          : " + nrId + "\n" +
		        indentString + "seId          : " + seId + "\n" +
		        indentString + "amount        : " + amount + "\n" +
		        indentString + "keepMode      : " + keepMode + "\n" +
		        indentString + "isSticky      : " + isSticky + "\n" +
		        indentString + "rsmpId        : " + rsmpId + "\n" +
		        indentString + "expiredAmount : " + expiredAmount + "\n" +
		        indentString + "expiredBase   : " + expiredBase + "\n" +
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
