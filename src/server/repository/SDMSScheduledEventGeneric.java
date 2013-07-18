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

public class SDMSScheduledEventGeneric extends SDMSObject
	implements Cloneable
{

	public final static String __version = "SDMSScheduledEventGeneric $Revision: 2.11 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	public static final boolean BROKEN = true;
	public static final boolean NOBROKEN = false;
	public static final int NONE = 0;
	public static final int LAST = 1;
	public static final int ALL = 2;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;

	public final static int nr_id = 1;
	public final static int nr_ownerId = 2;
	public final static int nr_sceId = 3;
	public final static int nr_evtId = 4;
	public final static int nr_isActive = 5;
	public final static int nr_isBroken = 6;
	public final static int nr_errorCode = 7;
	public final static int nr_errorMsg = 8;
	public final static int nr_lastStartTime = 9;
	public final static int nr_nextActivityTime = 10;
	public final static int nr_nextActivityIsTrigger = 11;
	public final static int nr_backlogHandling = 12;
	public final static int nr_suspendLimit = 13;
	public final static int nr_suspendLimitMultiplier = 14;
	public final static int nr_isCalendar = 15;
	public final static int nr_calendarHorizon = 16;
	public final static int nr_creatorUId = 17;
	public final static int nr_createTs = 18;
	public final static int nr_changerUId = 19;
	public final static int nr_changeTs = 20;

	public static String tableName = SDMSScheduledEventTableGeneric.tableName;

	protected Long ownerId;
	protected Long sceId;
	protected Long evtId;
	protected Boolean isActive;
	protected Boolean isBroken;
	protected String errorCode;
	protected String errorMsg;
	protected Long lastStartTime;
	protected Long nextActivityTime;
	protected Boolean nextActivityIsTrigger;
	protected Integer backlogHandling;
	protected Integer suspendLimit;
	protected Integer suspendLimitMultiplier;
	protected Boolean isCalendar;
	protected Integer calendarHorizon;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSScheduledEventGeneric(
	        SystemEnvironment env,
	        Long p_ownerId,
	        Long p_sceId,
	        Long p_evtId,
	        Boolean p_isActive,
	        Boolean p_isBroken,
	        String p_errorCode,
	        String p_errorMsg,
	        Long p_lastStartTime,
	        Long p_nextActivityTime,
	        Boolean p_nextActivityIsTrigger,
	        Integer p_backlogHandling,
	        Integer p_suspendLimit,
	        Integer p_suspendLimitMultiplier,
	        Boolean p_isCalendar,
	        Integer p_calendarHorizon,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSScheduledEventTableGeneric.table);
		ownerId = p_ownerId;
		sceId = p_sceId;
		evtId = p_evtId;
		isActive = p_isActive;
		isBroken = p_isBroken;
		if (p_errorCode != null && p_errorCode.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(ScheduledEvent) Length of $1 exceeds maximum length $2", "errorCode", "32")
			);
		}
		errorCode = p_errorCode;
		if (p_errorMsg != null && p_errorMsg.length() > 256) {
			p_errorMsg = p_errorMsg.substring(0,256);
		}
		errorMsg = p_errorMsg;
		lastStartTime = p_lastStartTime;
		nextActivityTime = p_nextActivityTime;
		nextActivityIsTrigger = p_nextActivityIsTrigger;
		backlogHandling = p_backlogHandling;
		suspendLimit = p_suspendLimit;
		suspendLimitMultiplier = p_suspendLimitMultiplier;
		isCalendar = p_isCalendar;
		calendarHorizon = p_calendarHorizon;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (ownerId);
	}

	public	SDMSScheduledEventGeneric setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
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

	public Long getSceId (SystemEnvironment env)
	throws SDMSException
	{
		return (sceId);
	}

	public	SDMSScheduledEventGeneric setSceId (SystemEnvironment env, Long p_sceId)
	throws SDMSException
	{
		if(sceId.equals(p_sceId)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.sceId = p_sceId;
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

	public Long getEvtId (SystemEnvironment env)
	throws SDMSException
	{
		return (evtId);
	}

	public	SDMSScheduledEventGeneric setEvtId (SystemEnvironment env, Long p_evtId)
	throws SDMSException
	{
		if(evtId.equals(p_evtId)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.evtId = p_evtId;
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

	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		return (isActive);
	}

	public String getIsActiveAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsActive (env);
		final boolean b = v.booleanValue();
		if (b == SDMSScheduledEvent.ACTIVE)
			return "ACTIVE";
		if (b == SDMSScheduledEvent.INACTIVE)
			return "INACTIVE";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ScheduledEvent.isActive: $1",
		                          getIsActive (env)));
	}

	public	SDMSScheduledEventGeneric setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		if(isActive.equals(p_isActive)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.isActive = p_isActive;
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

	public Boolean getIsBroken (SystemEnvironment env)
	throws SDMSException
	{
		return (isBroken);
	}

	public String getIsBrokenAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsBroken (env);
		final boolean b = v.booleanValue();
		if (b == SDMSScheduledEvent.BROKEN)
			return "BROKEN";
		if (b == SDMSScheduledEvent.NOBROKEN)
			return "NOBROKEN";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ScheduledEvent.isBroken: $1",
		                          getIsBroken (env)));
	}

	public	SDMSScheduledEventGeneric setIsBroken (SystemEnvironment env, Boolean p_isBroken)
	throws SDMSException
	{
		if(isBroken.equals(p_isBroken)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.isBroken = p_isBroken;
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

	public String getErrorCode (SystemEnvironment env)
	throws SDMSException
	{
		return (errorCode);
	}

	public	SDMSScheduledEventGeneric setErrorCode (SystemEnvironment env, String p_errorCode)
	throws SDMSException
	{
		if(p_errorCode != null && p_errorCode.equals(errorCode)) return this;
		if(p_errorCode == null && errorCode == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			if (p_errorCode != null && p_errorCode.length() > 32) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(ScheduledEvent) Length of $1 exceeds maximum length $2", "errorCode", "32")
				);
			}
			o.errorCode = p_errorCode;
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

	public String getErrorMsg (SystemEnvironment env)
	throws SDMSException
	{
		return (errorMsg);
	}

	public	SDMSScheduledEventGeneric setErrorMsg (SystemEnvironment env, String p_errorMsg)
	throws SDMSException
	{
		if(p_errorMsg != null && p_errorMsg.equals(errorMsg)) return this;
		if(p_errorMsg == null && errorMsg == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			if (p_errorMsg != null && p_errorMsg.length() > 256) {
				p_errorMsg = p_errorMsg.substring(0,256);
			}
			o.errorMsg = p_errorMsg;
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

	public Long getLastStartTime (SystemEnvironment env)
	throws SDMSException
	{
		return (lastStartTime);
	}

	public	SDMSScheduledEventGeneric setLastStartTime (SystemEnvironment env, Long p_lastStartTime)
	throws SDMSException
	{
		if(p_lastStartTime != null && p_lastStartTime.equals(lastStartTime)) return this;
		if(p_lastStartTime == null && lastStartTime == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.lastStartTime = p_lastStartTime;
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

	public Long getNextActivityTime (SystemEnvironment env)
	throws SDMSException
	{
		return (nextActivityTime);
	}

	public	SDMSScheduledEventGeneric setNextActivityTime (SystemEnvironment env, Long p_nextActivityTime)
	throws SDMSException
	{
		if(p_nextActivityTime != null && p_nextActivityTime.equals(nextActivityTime)) return this;
		if(p_nextActivityTime == null && nextActivityTime == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.nextActivityTime = p_nextActivityTime;
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

	public Boolean getNextActivityIsTrigger (SystemEnvironment env)
	throws SDMSException
	{
		return (nextActivityIsTrigger);
	}

	public	SDMSScheduledEventGeneric setNextActivityIsTrigger (SystemEnvironment env, Boolean p_nextActivityIsTrigger)
	throws SDMSException
	{
		if(p_nextActivityIsTrigger != null && p_nextActivityIsTrigger.equals(nextActivityIsTrigger)) return this;
		if(p_nextActivityIsTrigger == null && nextActivityIsTrigger == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.nextActivityIsTrigger = p_nextActivityIsTrigger;
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

	public Integer getBacklogHandling (SystemEnvironment env)
	throws SDMSException
	{
		return (backlogHandling);
	}

	public String getBacklogHandlingAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getBacklogHandling (env);
		switch (v.intValue()) {
		case SDMSScheduledEvent.NONE:
			return "NONE";
		case SDMSScheduledEvent.LAST:
			return "LAST";
		case SDMSScheduledEvent.ALL:
			return "ALL";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ScheduledEvent.backlogHandling: $1",
		                          getBacklogHandling (env)));
	}

	public	SDMSScheduledEventGeneric setBacklogHandling (SystemEnvironment env, Integer p_backlogHandling)
	throws SDMSException
	{
		if(backlogHandling.equals(p_backlogHandling)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.backlogHandling = p_backlogHandling;
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

	public Integer getSuspendLimit (SystemEnvironment env)
	throws SDMSException
	{
		return (suspendLimit);
	}

	public String getSuspendLimitAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getSuspendLimit (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSScheduledEvent.MINUTE:
			return "MINUTE";
		case SDMSScheduledEvent.HOUR:
			return "HOUR";
		case SDMSScheduledEvent.DAY:
			return "DAY";
		case SDMSScheduledEvent.WEEK:
			return "WEEK";
		case SDMSScheduledEvent.MONTH:
			return "MONTH";
		case SDMSScheduledEvent.YEAR:
			return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ScheduledEvent.suspendLimit: $1",
		                          getSuspendLimit (env)));
	}

	public	SDMSScheduledEventGeneric setSuspendLimit (SystemEnvironment env, Integer p_suspendLimit)
	throws SDMSException
	{
		if(p_suspendLimit != null && p_suspendLimit.equals(suspendLimit)) return this;
		if(p_suspendLimit == null && suspendLimit == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.suspendLimit = p_suspendLimit;
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

	public Integer getSuspendLimitMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		return (suspendLimitMultiplier);
	}

	public	SDMSScheduledEventGeneric setSuspendLimitMultiplier (SystemEnvironment env, Integer p_suspendLimitMultiplier)
	throws SDMSException
	{
		if(p_suspendLimitMultiplier != null && p_suspendLimitMultiplier.equals(suspendLimitMultiplier)) return this;
		if(p_suspendLimitMultiplier == null && suspendLimitMultiplier == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.suspendLimitMultiplier = p_suspendLimitMultiplier;
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

	public Boolean getIsCalendar (SystemEnvironment env)
	throws SDMSException
	{
		return (isCalendar);
	}

	public String getIsCalendarAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsCalendar (env);
		final boolean b = v.booleanValue();
		if (b == SDMSScheduledEvent.ACTIVE)
			return "ACTIVE";
		if (b == SDMSScheduledEvent.INACTIVE)
			return "INACTIVE";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown ScheduledEvent.isCalendar: $1",
		                          getIsCalendar (env)));
	}

	public	SDMSScheduledEventGeneric setIsCalendar (SystemEnvironment env, Boolean p_isCalendar)
	throws SDMSException
	{
		if(isCalendar.equals(p_isCalendar)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.isCalendar = p_isCalendar;
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

	public Integer getCalendarHorizon (SystemEnvironment env)
	throws SDMSException
	{
		return (calendarHorizon);
	}

	public	SDMSScheduledEventGeneric setCalendarHorizon (SystemEnvironment env, Integer p_calendarHorizon)
	throws SDMSException
	{
		if(p_calendarHorizon != null && p_calendarHorizon.equals(calendarHorizon)) return this;
		if(p_calendarHorizon == null && calendarHorizon == null) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.calendarHorizon = p_calendarHorizon;
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

	SDMSScheduledEventGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
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

	SDMSScheduledEventGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
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

	public	SDMSScheduledEventGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSScheduledEventGeneric) change(env);
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

	SDMSScheduledEventGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSScheduledEventGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSScheduledEventGeneric) change(env);
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

	public SDMSScheduledEventGeneric set_SceIdEvtId (SystemEnvironment env, Long p_sceId, Long p_evtId)
	throws SDMSException
	{
		SDMSScheduledEventGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ScheduledEvent) Change of system object not allowed")
				);
			}
			o = (SDMSScheduledEventGeneric) change(env);
			o.sceId = p_sceId;
			o.evtId = p_evtId;
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
		return new SDMSScheduledEvent(this);
	}

	protected SDMSScheduledEventGeneric(Long p_id,
	                                    Long p_ownerId,
	                                    Long p_sceId,
	                                    Long p_evtId,
	                                    Boolean p_isActive,
	                                    Boolean p_isBroken,
	                                    String p_errorCode,
	                                    String p_errorMsg,
	                                    Long p_lastStartTime,
	                                    Long p_nextActivityTime,
	                                    Boolean p_nextActivityIsTrigger,
	                                    Integer p_backlogHandling,
	                                    Integer p_suspendLimit,
	                                    Integer p_suspendLimitMultiplier,
	                                    Boolean p_isCalendar,
	                                    Integer p_calendarHorizon,
	                                    Long p_creatorUId,
	                                    Long p_createTs,
	                                    Long p_changerUId,
	                                    Long p_changeTs,
	                                    long p_validFrom, long p_validTo)
	{
		id     = p_id;
		ownerId = p_ownerId;
		sceId = p_sceId;
		evtId = p_evtId;
		isActive = p_isActive;
		isBroken = p_isBroken;
		errorCode = p_errorCode;
		errorMsg = p_errorMsg;
		lastStartTime = p_lastStartTime;
		nextActivityTime = p_nextActivityTime;
		nextActivityIsTrigger = p_nextActivityIsTrigger;
		backlogHandling = p_backlogHandling;
		suspendLimit = p_suspendLimit;
		suspendLimitMultiplier = p_suspendLimitMultiplier;
		isCalendar = p_isCalendar;
		calendarHorizon = p_calendarHorizon;
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
				        "INSERT INTO SCHEDULED_EVENT (" +
				        "ID" +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "SCE_ID" + equote +
				        ", " + squote + "EVT_ID" + equote +
				        ", " + squote + "ACTIVE" + equote +
				        ", " + squote + "BROKEN" + equote +
				        ", " + squote + "ERROR_CODE" + equote +
				        ", " + squote + "ERROR_MSG" + equote +
				        ", " + squote + "LAST_START_TIME" + equote +
				        ", " + squote + "NEXT_START_TIME" + equote +
				        ", " + squote + "NEXT_IS_TRIGGER" + equote +
				        ", " + squote + "BACKLOG_HANDLING" + equote +
				        ", " + squote + "SUSPEND_LIMIT" + equote +
				        ", " + squote + "SUSPEND_LIMIT_MULTIPLIER" + equote +
				        ", " + squote + "IS_CALENDAR" + equote +
				        ", " + squote + "CALENDAR_HORIZON" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "ScheduledEvent: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, ownerId.longValue());
			pInsert.setLong (3, sceId.longValue());
			pInsert.setLong (4, evtId.longValue());
			pInsert.setInt (5, isActive.booleanValue() ? 1 : 0);
			pInsert.setInt (6, isBroken.booleanValue() ? 1 : 0);
			if (errorCode == null)
				pInsert.setNull(7, Types.VARCHAR);
			else
				pInsert.setString(7, errorCode);
			if (errorMsg == null)
				pInsert.setNull(8, Types.VARCHAR);
			else
				pInsert.setString(8, errorMsg);
			if (lastStartTime == null)
				pInsert.setNull(9, Types.INTEGER);
			else
				pInsert.setLong (9, lastStartTime.longValue());
			if (nextActivityTime == null)
				pInsert.setNull(10, Types.INTEGER);
			else
				pInsert.setLong (10, nextActivityTime.longValue());
			if (nextActivityIsTrigger == null)
				pInsert.setNull(11, Types.INTEGER);
			else
				pInsert.setInt (11, nextActivityIsTrigger.booleanValue() ? 1 : 0);
			pInsert.setInt(12, backlogHandling.intValue());
			if (suspendLimit == null)
				pInsert.setNull(13, Types.INTEGER);
			else
				pInsert.setInt(13, suspendLimit.intValue());
			if (suspendLimitMultiplier == null)
				pInsert.setNull(14, Types.INTEGER);
			else
				pInsert.setInt(14, suspendLimitMultiplier.intValue());
			pInsert.setInt (15, isCalendar.booleanValue() ? 1 : 0);
			if (calendarHorizon == null)
				pInsert.setNull(16, Types.INTEGER);
			else
				pInsert.setInt(16, calendarHorizon.intValue());
			pInsert.setLong (17, creatorUId.longValue());
			pInsert.setLong (18, createTs.longValue());
			pInsert.setLong (19, changerUId.longValue());
			pInsert.setLong (20, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "ScheduledEvent: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM SCHEDULED_EVENT WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "ScheduledEvent: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "ScheduledEvent: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE SCHEDULED_EVENT SET " +
				        "" + squote + "OWNER_ID" + equote + " = ? " +
				        ", " + squote + "SCE_ID" + equote + " = ? " +
				        ", " + squote + "EVT_ID" + equote + " = ? " +
				        ", " + squote + "ACTIVE" + equote + " = ? " +
				        ", " + squote + "BROKEN" + equote + " = ? " +
				        ", " + squote + "ERROR_CODE" + equote + " = ? " +
				        ", " + squote + "ERROR_MSG" + equote + " = ? " +
				        ", " + squote + "LAST_START_TIME" + equote + " = ? " +
				        ", " + squote + "NEXT_START_TIME" + equote + " = ? " +
				        ", " + squote + "NEXT_IS_TRIGGER" + equote + " = ? " +
				        ", " + squote + "BACKLOG_HANDLING" + equote + " = ? " +
				        ", " + squote + "SUSPEND_LIMIT" + equote + " = ? " +
				        ", " + squote + "SUSPEND_LIMIT_MULTIPLIER" + equote + " = ? " +
				        ", " + squote + "IS_CALENDAR" + equote + " = ? " +
				        ", " + squote + "CALENDAR_HORIZON" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "ScheduledEvent: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, ownerId.longValue());
			pUpdate.setLong (2, sceId.longValue());
			pUpdate.setLong (3, evtId.longValue());
			pUpdate.setInt (4, isActive.booleanValue() ? 1 : 0);
			pUpdate.setInt (5, isBroken.booleanValue() ? 1 : 0);
			if (errorCode == null)
				pUpdate.setNull(6, Types.VARCHAR);
			else
				pUpdate.setString(6, errorCode);
			if (errorMsg == null)
				pUpdate.setNull(7, Types.VARCHAR);
			else
				pUpdate.setString(7, errorMsg);
			if (lastStartTime == null)
				pUpdate.setNull(8, Types.INTEGER);
			else
				pUpdate.setLong (8, lastStartTime.longValue());
			if (nextActivityTime == null)
				pUpdate.setNull(9, Types.INTEGER);
			else
				pUpdate.setLong (9, nextActivityTime.longValue());
			if (nextActivityIsTrigger == null)
				pUpdate.setNull(10, Types.INTEGER);
			else
				pUpdate.setInt (10, nextActivityIsTrigger.booleanValue() ? 1 : 0);
			pUpdate.setInt(11, backlogHandling.intValue());
			if (suspendLimit == null)
				pUpdate.setNull(12, Types.INTEGER);
			else
				pUpdate.setInt(12, suspendLimit.intValue());
			if (suspendLimitMultiplier == null)
				pUpdate.setNull(13, Types.INTEGER);
			else
				pUpdate.setInt(13, suspendLimitMultiplier.intValue());
			pUpdate.setInt (14, isCalendar.booleanValue() ? 1 : 0);
			if (calendarHorizon == null)
				pUpdate.setNull(15, Types.INTEGER);
			else
				pUpdate.setInt(15, calendarHorizon.intValue());
			pUpdate.setLong (16, creatorUId.longValue());
			pUpdate.setLong (17, createTs.longValue());
			pUpdate.setLong (18, changerUId.longValue());
			pUpdate.setLong (19, changeTs.longValue());
			pUpdate.setLong(20, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "ScheduledEvent: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkIsActive(Boolean p)
	{
		if(p.booleanValue() == SDMSScheduledEvent.ACTIVE) return true;
		if(p.booleanValue() == SDMSScheduledEvent.INACTIVE) return true;
		return false;
	}
	static public boolean checkIsBroken(Boolean p)
	{
		if(p.booleanValue() == SDMSScheduledEvent.BROKEN) return true;
		if(p.booleanValue() == SDMSScheduledEvent.NOBROKEN) return true;
		return false;
	}
	static public boolean checkBacklogHandling(Integer p)
	{
		switch (p.intValue()) {
		case SDMSScheduledEvent.NONE:
		case SDMSScheduledEvent.LAST:
		case SDMSScheduledEvent.ALL:
			return true;
		}
		return false;
	}
	static public boolean checkSuspendLimit(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSScheduledEvent.MINUTE:
		case SDMSScheduledEvent.HOUR:
		case SDMSScheduledEvent.DAY:
		case SDMSScheduledEvent.WEEK:
		case SDMSScheduledEvent.MONTH:
		case SDMSScheduledEvent.YEAR:
			return true;
		}
		return false;
	}
	static public boolean checkIsCalendar(Boolean p)
	{
		if(p.booleanValue() == SDMSScheduledEvent.ACTIVE) return true;
		if(p.booleanValue() == SDMSScheduledEvent.INACTIVE) return true;
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ScheduledEvent", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "sceId : " + sceId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "evtId : " + evtId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isActive : " + isActive, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isBroken : " + isBroken, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errorCode : " + errorCode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errorMsg : " + errorMsg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lastStartTime : " + lastStartTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nextActivityTime : " + nextActivityTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nextActivityIsTrigger : " + nextActivityIsTrigger, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "backlogHandling : " + backlogHandling, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "suspendLimit : " + suspendLimit, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "suspendLimitMultiplier : " + suspendLimitMultiplier, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isCalendar : " + isCalendar, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "calendarHorizon : " + calendarHorizon, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "ownerId                : " + ownerId + "\n" +
		        indentString + "sceId                  : " + sceId + "\n" +
		        indentString + "evtId                  : " + evtId + "\n" +
		        indentString + "isActive               : " + isActive + "\n" +
		        indentString + "isBroken               : " + isBroken + "\n" +
		        indentString + "errorCode              : " + errorCode + "\n" +
		        indentString + "errorMsg               : " + errorMsg + "\n" +
		        indentString + "lastStartTime          : " + lastStartTime + "\n" +
		        indentString + "nextActivityTime       : " + nextActivityTime + "\n" +
		        indentString + "nextActivityIsTrigger  : " + nextActivityIsTrigger + "\n" +
		        indentString + "backlogHandling        : " + backlogHandling + "\n" +
		        indentString + "suspendLimit           : " + suspendLimit + "\n" +
		        indentString + "suspendLimitMultiplier : " + suspendLimitMultiplier + "\n" +
		        indentString + "isCalendar             : " + isCalendar + "\n" +
		        indentString + "calendarHorizon        : " + calendarHorizon + "\n" +
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
