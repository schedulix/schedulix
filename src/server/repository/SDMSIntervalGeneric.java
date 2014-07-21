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

public class SDMSIntervalGeneric extends SDMSObject
	implements Cloneable
{

	public static final int MINUTE = 0;
	public static final int HOUR = 1;
	public static final int DAY = 2;
	public static final int WEEK = 3;
	public static final int MONTH = 4;
	public static final int YEAR = 5;
	public static final long MINUTE_DUR =      1L*60*1000;
	public static final long HOUR_DUR =     60L*60*1000;
	public static final long DAY_DUR =   1440L*60*1000;
	public static final long WEEK_DUR =  10080L*60*1000;
	public static final long MONTH_DUR =  43200L*60*1000;
	public static final long YEAR_DUR = 525600L*60*1000;
	public static final long MINUTE_DUR_M =      1L;
	public static final long HOUR_DUR_M =     60L;
	public static final long DAY_DUR_M =   1440L;
	public static final long WEEK_DUR_M =  10080L;
	public static final long MONTH_DUR_M =  43200L;
	public static final long YEAR_DUR_M = 525600L;
	public static final long MINUTE_MAX =              1*60*1000L;
	public static final long HOUR_MAX =             60*60*1000L;
	public static final long DAY_MAX =          25*60*60*1000L;
	public static final long WEEK_MAX =  (7*24 + 1)*60*60*1000L;
	public static final long MONTH_MAX = (31*24 + 1)*60*60*1000L;
	public static final long YEAR_MAX =    (366*24)*60*60*1000L;

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_ownerId = 3;
	public final static int nr_startTime = 4;
	public final static int nr_endTime = 5;
	public final static int nr_delay = 6;
	public final static int nr_baseInterval = 7;
	public final static int nr_baseIntervalMultiplier = 8;
	public final static int nr_duration = 9;
	public final static int nr_durationMultiplier = 10;
	public final static int nr_syncTime = 11;
	public final static int nr_isInverse = 12;
	public final static int nr_isMerge = 13;
	public final static int nr_embeddedIntervalId = 14;
	public final static int nr_seId = 15;
	public final static int nr_creatorUId = 16;
	public final static int nr_createTs = 17;
	public final static int nr_changerUId = 18;
	public final static int nr_changeTs = 19;

	public static String tableName = SDMSIntervalTableGeneric.tableName;

	protected String name;
	protected Long ownerId;
	protected Long startTime;
	protected Long endTime;
	protected Long delay;
	protected Integer baseInterval;
	protected Integer baseIntervalMultiplier;
	protected Integer duration;
	protected Integer durationMultiplier;
	protected Long syncTime;
	protected Boolean isInverse;
	protected Boolean isMerge;
	protected Long embeddedIntervalId;
	protected Long seId;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSIntervalGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Long p_ownerId,
	        Long p_startTime,
	        Long p_endTime,
	        Long p_delay,
	        Integer p_baseInterval,
	        Integer p_baseIntervalMultiplier,
	        Integer p_duration,
	        Integer p_durationMultiplier,
	        Long p_syncTime,
	        Boolean p_isInverse,
	        Boolean p_isMerge,
	        Long p_embeddedIntervalId,
	        Long p_seId,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSIntervalTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Interval) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		ownerId = p_ownerId;
		startTime = p_startTime;
		endTime = p_endTime;
		delay = p_delay;
		baseInterval = p_baseInterval;
		baseIntervalMultiplier = p_baseIntervalMultiplier;
		duration = p_duration;
		durationMultiplier = p_durationMultiplier;
		syncTime = p_syncTime;
		isInverse = p_isInverse;
		isMerge = p_isMerge;
		embeddedIntervalId = p_embeddedIntervalId;
		seId = p_seId;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		return (name);
	}

	public	SDMSIntervalGeneric setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Interval) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
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

	public	SDMSIntervalGeneric setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
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

	public Long getStartTime (SystemEnvironment env)
	throws SDMSException
	{
		return (startTime);
	}

	public	SDMSIntervalGeneric setStartTime (SystemEnvironment env, Long p_startTime)
	throws SDMSException
	{
		if(p_startTime != null && p_startTime.equals(startTime)) return this;
		if(p_startTime == null && startTime == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.startTime = p_startTime;
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

	public Long getEndTime (SystemEnvironment env)
	throws SDMSException
	{
		return (endTime);
	}

	public	SDMSIntervalGeneric setEndTime (SystemEnvironment env, Long p_endTime)
	throws SDMSException
	{
		if(p_endTime != null && p_endTime.equals(endTime)) return this;
		if(p_endTime == null && endTime == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.endTime = p_endTime;
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

	public Long getDelay (SystemEnvironment env)
	throws SDMSException
	{
		return (delay);
	}

	public	SDMSIntervalGeneric setDelay (SystemEnvironment env, Long p_delay)
	throws SDMSException
	{
		if(p_delay != null && p_delay.equals(delay)) return this;
		if(p_delay == null && delay == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.delay = p_delay;
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

	public Integer getBaseInterval (SystemEnvironment env)
	throws SDMSException
	{
		return (baseInterval);
	}

	public String getBaseIntervalAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getBaseInterval (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSInterval.MINUTE:
			return "MINUTE";
		case SDMSInterval.HOUR:
			return "HOUR";
		case SDMSInterval.DAY:
			return "DAY";
		case SDMSInterval.WEEK:
			return "WEEK";
		case SDMSInterval.MONTH:
			return "MONTH";
		case SDMSInterval.YEAR:
			return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Interval.baseInterval: $1",
		                          getBaseInterval (env)));
	}

	public	SDMSIntervalGeneric setBaseInterval (SystemEnvironment env, Integer p_baseInterval)
	throws SDMSException
	{
		if(p_baseInterval != null && p_baseInterval.equals(baseInterval)) return this;
		if(p_baseInterval == null && baseInterval == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.baseInterval = p_baseInterval;
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

	public Integer getBaseIntervalMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		return (baseIntervalMultiplier);
	}

	public	SDMSIntervalGeneric setBaseIntervalMultiplier (SystemEnvironment env, Integer p_baseIntervalMultiplier)
	throws SDMSException
	{
		if(p_baseIntervalMultiplier != null && p_baseIntervalMultiplier.equals(baseIntervalMultiplier)) return this;
		if(p_baseIntervalMultiplier == null && baseIntervalMultiplier == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.baseIntervalMultiplier = p_baseIntervalMultiplier;
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

	public Integer getDuration (SystemEnvironment env)
	throws SDMSException
	{
		return (duration);
	}

	public String getDurationAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getDuration (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSInterval.MINUTE:
			return "MINUTE";
		case SDMSInterval.HOUR:
			return "HOUR";
		case SDMSInterval.DAY:
			return "DAY";
		case SDMSInterval.WEEK:
			return "WEEK";
		case SDMSInterval.MONTH:
			return "MONTH";
		case SDMSInterval.YEAR:
			return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Interval.duration: $1",
		                          getDuration (env)));
	}

	public	SDMSIntervalGeneric setDuration (SystemEnvironment env, Integer p_duration)
	throws SDMSException
	{
		if(p_duration != null && p_duration.equals(duration)) return this;
		if(p_duration == null && duration == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.duration = p_duration;
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

	public Integer getDurationMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		return (durationMultiplier);
	}

	public	SDMSIntervalGeneric setDurationMultiplier (SystemEnvironment env, Integer p_durationMultiplier)
	throws SDMSException
	{
		if(p_durationMultiplier != null && p_durationMultiplier.equals(durationMultiplier)) return this;
		if(p_durationMultiplier == null && durationMultiplier == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.durationMultiplier = p_durationMultiplier;
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

	public Long getSyncTime (SystemEnvironment env)
	throws SDMSException
	{
		return (syncTime);
	}

	public	SDMSIntervalGeneric setSyncTime (SystemEnvironment env, Long p_syncTime)
	throws SDMSException
	{
		if(syncTime.equals(p_syncTime)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.syncTime = p_syncTime;
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

	public Boolean getIsInverse (SystemEnvironment env)
	throws SDMSException
	{
		return (isInverse);
	}

	public	SDMSIntervalGeneric setIsInverse (SystemEnvironment env, Boolean p_isInverse)
	throws SDMSException
	{
		if(isInverse.equals(p_isInverse)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.isInverse = p_isInverse;
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

	public Boolean getIsMerge (SystemEnvironment env)
	throws SDMSException
	{
		return (isMerge);
	}

	public	SDMSIntervalGeneric setIsMerge (SystemEnvironment env, Boolean p_isMerge)
	throws SDMSException
	{
		if(isMerge.equals(p_isMerge)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.isMerge = p_isMerge;
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

	public Long getEmbeddedIntervalId (SystemEnvironment env)
	throws SDMSException
	{
		return (embeddedIntervalId);
	}

	public	SDMSIntervalGeneric setEmbeddedIntervalId (SystemEnvironment env, Long p_embeddedIntervalId)
	throws SDMSException
	{
		if(p_embeddedIntervalId != null && p_embeddedIntervalId.equals(embeddedIntervalId)) return this;
		if(p_embeddedIntervalId == null && embeddedIntervalId == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
			o.embeddedIntervalId = p_embeddedIntervalId;
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

	public	SDMSIntervalGeneric setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(p_seId != null && p_seId.equals(seId)) return this;
		if(p_seId == null && seId == null) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
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

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	SDMSIntervalGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
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

	SDMSIntervalGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Interval) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalGeneric) change(env);
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

	public	SDMSIntervalGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSIntervalGeneric) change(env);
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

	SDMSIntervalGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSIntervalGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSIntervalGeneric) change(env);
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

	protected SDMSProxy toProxy()
	{
		return new SDMSInterval(this);
	}

	protected SDMSIntervalGeneric(Long p_id,
	                              String p_name,
	                              Long p_ownerId,
	                              Long p_startTime,
	                              Long p_endTime,
	                              Long p_delay,
	                              Integer p_baseInterval,
	                              Integer p_baseIntervalMultiplier,
	                              Integer p_duration,
	                              Integer p_durationMultiplier,
	                              Long p_syncTime,
	                              Boolean p_isInverse,
	                              Boolean p_isMerge,
	                              Long p_embeddedIntervalId,
	                              Long p_seId,
	                              Long p_creatorUId,
	                              Long p_createTs,
	                              Long p_changerUId,
	                              Long p_changeTs,
	                              long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		ownerId = p_ownerId;
		startTime = p_startTime;
		endTime = p_endTime;
		delay = p_delay;
		baseInterval = p_baseInterval;
		baseIntervalMultiplier = p_baseIntervalMultiplier;
		duration = p_duration;
		durationMultiplier = p_durationMultiplier;
		syncTime = p_syncTime;
		isInverse = p_isInverse;
		isMerge = p_isMerge;
		embeddedIntervalId = p_embeddedIntervalId;
		seId = p_seId;
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
				if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "INSERT INTO INTERVALL (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "START_TIME" + equote +
				        ", " + squote + "END_TIME" + equote +
				        ", " + squote + "DELAY" + equote +
				        ", " + squote + "BASE_INTERVAL" + equote +
				        ", " + squote + "BASE_INTERVAL_MULTIPLIER" + equote +
				        ", " + squote + "DURATION" + equote +
				        ", " + squote + "DURATION_MULTIPLIER" + equote +
				        ", " + squote + "SYNC_TIME" + equote +
				        ", " + squote + "IS_INVERSE" + equote +
				        ", " + squote + "IS_MERGE" + equote +
				        ", " + squote + "EMBEDDED_INT_ID" + equote +
				        ", " + squote + "SE_ID" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "Interval: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setString(2, name);
			pInsert.setLong (3, ownerId.longValue());
			if (startTime == null)
				pInsert.setNull(4, Types.INTEGER);
			else
				pInsert.setLong (4, startTime.longValue());
			if (endTime == null)
				pInsert.setNull(5, Types.INTEGER);
			else
				pInsert.setLong (5, endTime.longValue());
			if (delay == null)
				pInsert.setNull(6, Types.INTEGER);
			else
				pInsert.setLong (6, delay.longValue());
			if (baseInterval == null)
				pInsert.setNull(7, Types.INTEGER);
			else
				pInsert.setInt(7, baseInterval.intValue());
			if (baseIntervalMultiplier == null)
				pInsert.setNull(8, Types.INTEGER);
			else
				pInsert.setInt(8, baseIntervalMultiplier.intValue());
			if (duration == null)
				pInsert.setNull(9, Types.INTEGER);
			else
				pInsert.setInt(9, duration.intValue());
			if (durationMultiplier == null)
				pInsert.setNull(10, Types.INTEGER);
			else
				pInsert.setInt(10, durationMultiplier.intValue());
			pInsert.setLong (11, syncTime.longValue());
			pInsert.setInt (12, isInverse.booleanValue() ? 1 : 0);
			pInsert.setInt (13, isMerge.booleanValue() ? 1 : 0);
			if (embeddedIntervalId == null)
				pInsert.setNull(14, Types.INTEGER);
			else
				pInsert.setLong (14, embeddedIntervalId.longValue());
			if (seId == null)
				pInsert.setNull(15, Types.INTEGER);
			else
				pInsert.setLong (15, seId.longValue());
			pInsert.setLong (16, creatorUId.longValue());
			pInsert.setLong (17, createTs.longValue());
			pInsert.setLong (18, changerUId.longValue());
			pInsert.setLong (19, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "Interval: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM INTERVALL WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "Interval: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "Interval: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "UPDATE INTERVALL SET " +
				        "" + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "OWNER_ID" + equote + " = ? " +
				        ", " + squote + "START_TIME" + equote + " = ? " +
				        ", " + squote + "END_TIME" + equote + " = ? " +
				        ", " + squote + "DELAY" + equote + " = ? " +
				        ", " + squote + "BASE_INTERVAL" + equote + " = ? " +
				        ", " + squote + "BASE_INTERVAL_MULTIPLIER" + equote + " = ? " +
				        ", " + squote + "DURATION" + equote + " = ? " +
				        ", " + squote + "DURATION_MULTIPLIER" + equote + " = ? " +
				        ", " + squote + "SYNC_TIME" + equote + " = ? " +
				        ", " + squote + "IS_INVERSE" + equote + " = ? " +
				        ", " + squote + "IS_MERGE" + equote + " = ? " +
				        ", " + squote + "EMBEDDED_INT_ID" + equote + " = ? " +
				        ", " + squote + "SE_ID" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "Interval: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setString(1, name);
			pUpdate.setLong (2, ownerId.longValue());
			if (startTime == null)
				pUpdate.setNull(3, Types.INTEGER);
			else
				pUpdate.setLong (3, startTime.longValue());
			if (endTime == null)
				pUpdate.setNull(4, Types.INTEGER);
			else
				pUpdate.setLong (4, endTime.longValue());
			if (delay == null)
				pUpdate.setNull(5, Types.INTEGER);
			else
				pUpdate.setLong (5, delay.longValue());
			if (baseInterval == null)
				pUpdate.setNull(6, Types.INTEGER);
			else
				pUpdate.setInt(6, baseInterval.intValue());
			if (baseIntervalMultiplier == null)
				pUpdate.setNull(7, Types.INTEGER);
			else
				pUpdate.setInt(7, baseIntervalMultiplier.intValue());
			if (duration == null)
				pUpdate.setNull(8, Types.INTEGER);
			else
				pUpdate.setInt(8, duration.intValue());
			if (durationMultiplier == null)
				pUpdate.setNull(9, Types.INTEGER);
			else
				pUpdate.setInt(9, durationMultiplier.intValue());
			pUpdate.setLong (10, syncTime.longValue());
			pUpdate.setInt (11, isInverse.booleanValue() ? 1 : 0);
			pUpdate.setInt (12, isMerge.booleanValue() ? 1 : 0);
			if (embeddedIntervalId == null)
				pUpdate.setNull(13, Types.INTEGER);
			else
				pUpdate.setLong (13, embeddedIntervalId.longValue());
			if (seId == null)
				pUpdate.setNull(14, Types.INTEGER);
			else
				pUpdate.setLong (14, seId.longValue());
			pUpdate.setLong (15, creatorUId.longValue());
			pUpdate.setLong (16, createTs.longValue());
			pUpdate.setLong (17, changerUId.longValue());
			pUpdate.setLong (18, changeTs.longValue());
			pUpdate.setLong(19, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "Interval: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkBaseInterval(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSInterval.MINUTE:
		case SDMSInterval.HOUR:
		case SDMSInterval.DAY:
		case SDMSInterval.WEEK:
		case SDMSInterval.MONTH:
		case SDMSInterval.YEAR:
			return true;
		}
		return false;
	}
	static public boolean checkDuration(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSInterval.MINUTE:
		case SDMSInterval.HOUR:
		case SDMSInterval.DAY:
		case SDMSInterval.WEEK:
		case SDMSInterval.MONTH:
		case SDMSInterval.YEAR:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : Interval", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "startTime : " + startTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "endTime : " + endTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "delay : " + delay, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "baseInterval : " + baseInterval, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "baseIntervalMultiplier : " + baseIntervalMultiplier, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "duration : " + duration, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "durationMultiplier : " + durationMultiplier, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "syncTime : " + syncTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isInverse : " + isInverse, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isMerge : " + isMerge, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "embeddedIntervalId : " + embeddedIntervalId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "name                   : " + name + "\n" +
		        indentString + "ownerId                : " + ownerId + "\n" +
		        indentString + "startTime              : " + startTime + "\n" +
		        indentString + "endTime                : " + endTime + "\n" +
		        indentString + "delay                  : " + delay + "\n" +
		        indentString + "baseInterval           : " + baseInterval + "\n" +
		        indentString + "baseIntervalMultiplier : " + baseIntervalMultiplier + "\n" +
		        indentString + "duration               : " + duration + "\n" +
		        indentString + "durationMultiplier     : " + durationMultiplier + "\n" +
		        indentString + "syncTime               : " + syncTime + "\n" +
		        indentString + "isInverse              : " + isInverse + "\n" +
		        indentString + "isMerge                : " + isMerge + "\n" +
		        indentString + "embeddedIntervalId     : " + embeddedIntervalId + "\n" +
		        indentString + "seId                   : " + seId + "\n" +
		        indentString + "creatorUId             : " + creatorUId + "\n" +
		        indentString + "createTs               : " + createTs + "\n" +
		        indentString + "changerUId             : " + changerUId + "\n" +
		        indentString + "changeTs               : " + changeTs + "\n" +
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
