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

public class SDMSSchedulingHierarchyGeneric extends SDMSObject
	implements Cloneable
{

	public static final int CHILDSUSPEND = 1;
	public static final int NOSUSPEND = 2;
	public static final int SUSPEND = 3;
	public static final int MERGE_LOCAL = 1;
	public static final int MERGE_GLOBAL = 2;
	public static final int NOMERGE = 3;
	public static final int FAILURE = 4;
	public static final boolean STATIC = true;
	public static final boolean DYNAMIC = false;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;

	public final static int nr_id = 1;
	public final static int nr_seParentId = 2;
	public final static int nr_seChildId = 3;
	public final static int nr_aliasName = 4;
	public final static int nr_isStatic = 5;
	public final static int nr_priority = 6;
	public final static int nr_suspend = 7;
	public final static int nr_resumeAt = 8;
	public final static int nr_resumeIn = 9;
	public final static int nr_resumeBase = 10;
	public final static int nr_mergeMode = 11;
	public final static int nr_estpId = 12;
	public final static int nr_creatorUId = 13;
	public final static int nr_createTs = 14;
	public final static int nr_changerUId = 15;
	public final static int nr_changeTs = 16;

	public static String tableName = SDMSSchedulingHierarchyTableGeneric.tableName;

	protected Long seParentId;
	protected Long seChildId;
	protected String aliasName;
	protected Boolean isStatic;
	protected Integer priority;
	protected Integer suspend;
	protected String resumeAt;
	protected Integer resumeIn;
	protected Integer resumeBase;
	protected Integer mergeMode;
	protected Long estpId;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSSchedulingHierarchyGeneric(
	        SystemEnvironment env,
	        Long p_seParentId,
	        Long p_seChildId,
	        String p_aliasName,
	        Boolean p_isStatic,
	        Integer p_priority,
	        Integer p_suspend,
	        String p_resumeAt,
	        Integer p_resumeIn,
	        Integer p_resumeBase,
	        Integer p_mergeMode,
	        Long p_estpId,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSSchedulingHierarchyTableGeneric.table);
		seParentId = p_seParentId;
		seChildId = p_seChildId;
		if (p_aliasName != null && p_aliasName.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingHierarchy) Length of $1 exceeds maximum length $2", "aliasName", "64")
			);
		}
		aliasName = p_aliasName;
		isStatic = p_isStatic;
		priority = p_priority;
		suspend = p_suspend;
		if (p_resumeAt != null && p_resumeAt.length() > 20) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SchedulingHierarchy) Length of $1 exceeds maximum length $2", "resumeAt", "20")
			);
		}
		resumeAt = p_resumeAt;
		resumeIn = p_resumeIn;
		resumeBase = p_resumeBase;
		mergeMode = p_mergeMode;
		estpId = p_estpId;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getSeParentId (SystemEnvironment env)
	throws SDMSException
	{
		return (seParentId);
	}

	public	SDMSSchedulingHierarchyGeneric setSeParentId (SystemEnvironment env, Long p_seParentId)
	throws SDMSException
	{
		if(p_seParentId != null && p_seParentId.equals(seParentId)) return this;
		if(p_seParentId == null && seParentId == null) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.seParentId = p_seParentId;
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

	public Long getSeChildId (SystemEnvironment env)
	throws SDMSException
	{
		return (seChildId);
	}

	public	SDMSSchedulingHierarchyGeneric setSeChildId (SystemEnvironment env, Long p_seChildId)
	throws SDMSException
	{
		if(p_seChildId != null && p_seChildId.equals(seChildId)) return this;
		if(p_seChildId == null && seChildId == null) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.seChildId = p_seChildId;
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

	public String getAliasName (SystemEnvironment env)
	throws SDMSException
	{
		return (aliasName);
	}

	public	SDMSSchedulingHierarchyGeneric setAliasName (SystemEnvironment env, String p_aliasName)
	throws SDMSException
	{
		if(p_aliasName != null && p_aliasName.equals(aliasName)) return this;
		if(p_aliasName == null && aliasName == null) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			if (p_aliasName != null && p_aliasName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(SchedulingHierarchy) Length of $1 exceeds maximum length $2", "aliasName", "64")
				);
			}
			o.aliasName = p_aliasName;
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

	public Boolean getIsStatic (SystemEnvironment env)
	throws SDMSException
	{
		return (isStatic);
	}

	public String getIsStaticAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsStatic (env);
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingHierarchy.STATIC)
			return "STATIC";
		if (b == SDMSSchedulingHierarchy.DYNAMIC)
			return "DYNAMIC";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingHierarchy.isStatic: $1",
		                          getIsStatic (env)));
	}

	public	SDMSSchedulingHierarchyGeneric setIsStatic (SystemEnvironment env, Boolean p_isStatic)
	throws SDMSException
	{
		if(isStatic.equals(p_isStatic)) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.isStatic = p_isStatic;
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

	public Integer getPriority (SystemEnvironment env)
	throws SDMSException
	{
		return (priority);
	}

	public	SDMSSchedulingHierarchyGeneric setPriority (SystemEnvironment env, Integer p_priority)
	throws SDMSException
	{
		if(priority.equals(p_priority)) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.priority = p_priority;
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

	public Integer getSuspend (SystemEnvironment env)
	throws SDMSException
	{
		return (suspend);
	}

	public String getSuspendAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getSuspend (env);
		switch (v.intValue()) {
		case SDMSSchedulingHierarchy.CHILDSUSPEND:
			return "CHILDSUSPEND";
		case SDMSSchedulingHierarchy.NOSUSPEND:
			return "NOSUSPEND";
		case SDMSSchedulingHierarchy.SUSPEND:
			return "SUSPEND";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingHierarchy.suspend: $1",
		                          getSuspend (env)));
	}

	public	SDMSSchedulingHierarchyGeneric setSuspend (SystemEnvironment env, Integer p_suspend)
	throws SDMSException
	{
		if(suspend.equals(p_suspend)) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.suspend = p_suspend;
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

	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeAt);
	}

	public	SDMSSchedulingHierarchyGeneric setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		if(p_resumeAt != null && p_resumeAt.equals(resumeAt)) return this;
		if(p_resumeAt == null && resumeAt == null) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			if (p_resumeAt != null && p_resumeAt.length() > 20) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(SchedulingHierarchy) Length of $1 exceeds maximum length $2", "resumeAt", "20")
				);
			}
			o.resumeAt = p_resumeAt;
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

	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeIn);
	}

	public	SDMSSchedulingHierarchyGeneric setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		if(p_resumeIn != null && p_resumeIn.equals(resumeIn)) return this;
		if(p_resumeIn == null && resumeIn == null) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.resumeIn = p_resumeIn;
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

	public Integer getResumeBase (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeBase);
	}

	public String getResumeBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getResumeBase (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSSchedulingHierarchy.MINUTE:
			return "MINUTE";
		case SDMSSchedulingHierarchy.HOUR:
			return "HOUR";
		case SDMSSchedulingHierarchy.DAY:
			return "DAY";
		case SDMSSchedulingHierarchy.WEEK:
			return "WEEK";
		case SDMSSchedulingHierarchy.MONTH:
			return "MONTH";
		case SDMSSchedulingHierarchy.YEAR:
			return "YEAR";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingHierarchy.resumeBase: $1",
		                          getResumeBase (env)));
	}

	public	SDMSSchedulingHierarchyGeneric setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		if(p_resumeBase != null && p_resumeBase.equals(resumeBase)) return this;
		if(p_resumeBase == null && resumeBase == null) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.resumeBase = p_resumeBase;
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

	public Integer getMergeMode (SystemEnvironment env)
	throws SDMSException
	{
		return (mergeMode);
	}

	public String getMergeModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getMergeMode (env);
		switch (v.intValue()) {
		case SDMSSchedulingHierarchy.MERGE_LOCAL:
			return "MERGE_LOCAL";
		case SDMSSchedulingHierarchy.MERGE_GLOBAL:
			return "MERGE_GLOBAL";
		case SDMSSchedulingHierarchy.NOMERGE:
			return "NOMERGE";
		case SDMSSchedulingHierarchy.FAILURE:
			return "FAILURE";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingHierarchy.mergeMode: $1",
		                          getMergeMode (env)));
	}

	public	SDMSSchedulingHierarchyGeneric setMergeMode (SystemEnvironment env, Integer p_mergeMode)
	throws SDMSException
	{
		if(mergeMode.equals(p_mergeMode)) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.mergeMode = p_mergeMode;
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

	public Long getEstpId (SystemEnvironment env)
	throws SDMSException
	{
		return (estpId);
	}

	public	SDMSSchedulingHierarchyGeneric setEstpId (SystemEnvironment env, Long p_estpId)
	throws SDMSException
	{
		if(p_estpId != null && p_estpId.equals(estpId)) return this;
		if(p_estpId == null && estpId == null) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.estpId = p_estpId;
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

	SDMSSchedulingHierarchyGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
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

	SDMSSchedulingHierarchyGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
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

	public	SDMSSchedulingHierarchyGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSSchedulingHierarchyGeneric) change(env);
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

	SDMSSchedulingHierarchyGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSSchedulingHierarchyGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSSchedulingHierarchyGeneric) change(env);
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

	public SDMSSchedulingHierarchyGeneric set_SeParentIdSeChildId (SystemEnvironment env, Long p_seParentId, Long p_seChildId)
	throws SDMSException
	{
		SDMSSchedulingHierarchyGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.seParentId = p_seParentId;
			o.seChildId = p_seChildId;
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

	public SDMSSchedulingHierarchyGeneric set_SeParentIdAliasName (SystemEnvironment env, Long p_seParentId, String p_aliasName)
	throws SDMSException
	{
		SDMSSchedulingHierarchyGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SchedulingHierarchy) Change of system object not allowed")
				);
			}
			o = (SDMSSchedulingHierarchyGeneric) change(env);
			o.seParentId = p_seParentId;
			if (p_aliasName != null && p_aliasName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(SchedulingHierarchy) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.aliasName = p_aliasName;
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
		return new SDMSSchedulingHierarchy(this);
	}

	protected SDMSSchedulingHierarchyGeneric(Long p_id,
	                Long p_seParentId,
	                Long p_seChildId,
	                String p_aliasName,
	                Boolean p_isStatic,
	                Integer p_priority,
	                Integer p_suspend,
	                String p_resumeAt,
	                Integer p_resumeIn,
	                Integer p_resumeBase,
	                Integer p_mergeMode,
	                Long p_estpId,
	                Long p_creatorUId,
	                Long p_createTs,
	                Long p_changerUId,
	                Long p_changeTs,
	                long p_validFrom, long p_validTo)
	{
		id     = p_id;
		seParentId = p_seParentId;
		seChildId = p_seChildId;
		aliasName = p_aliasName;
		isStatic = p_isStatic;
		priority = p_priority;
		suspend = p_suspend;
		resumeAt = p_resumeAt;
		resumeIn = p_resumeIn;
		resumeBase = p_resumeBase;
		mergeMode = p_mergeMode;
		estpId = p_estpId;
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
				        "INSERT INTO SCHEDULING_HIERARCHY (" +
				        "ID" +
				        ", " + squote + "SE_PARENT_ID" + equote +
				        ", " + squote + "SE_CHILD_ID" + equote +
				        ", " + squote + "ALIAS_NAME" + equote +
				        ", " + squote + "IS_STATIC" + equote +
				        ", " + squote + "PRIORITY" + equote +
				        ", " + squote + "SUSPEND" + equote +
				        ", " + squote + "RESUME_AT" + equote +
				        ", " + squote + "RESUME_IN" + equote +
				        ", " + squote + "RESUME_BASE" + equote +
				        ", " + squote + "MERGE_MODE" + equote +
				        ", " + squote + "ESTP_ID" + equote +
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
				        ", ?, ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "SchedulingHierarchy: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			if (seParentId == null)
				pInsert.setNull(2, Types.INTEGER);
			else
				pInsert.setLong (2, seParentId.longValue());
			if (seChildId == null)
				pInsert.setNull(3, Types.INTEGER);
			else
				pInsert.setLong (3, seChildId.longValue());
			if (aliasName == null)
				pInsert.setNull(4, Types.VARCHAR);
			else
				pInsert.setString(4, aliasName);
			pInsert.setInt (5, isStatic.booleanValue() ? 1 : 0);
			pInsert.setInt(6, priority.intValue());
			pInsert.setInt(7, suspend.intValue());
			if (resumeAt == null)
				pInsert.setNull(8, Types.VARCHAR);
			else
				pInsert.setString(8, resumeAt);
			if (resumeIn == null)
				pInsert.setNull(9, Types.INTEGER);
			else
				pInsert.setInt(9, resumeIn.intValue());
			if (resumeBase == null)
				pInsert.setNull(10, Types.INTEGER);
			else
				pInsert.setInt(10, resumeBase.intValue());
			pInsert.setInt(11, mergeMode.intValue());
			if (estpId == null)
				pInsert.setNull(12, Types.INTEGER);
			else
				pInsert.setLong (12, estpId.longValue());
			pInsert.setLong (13, creatorUId.longValue());
			pInsert.setLong (14, createTs.longValue());
			pInsert.setLong (15, changerUId.longValue());
			pInsert.setLong (16, changeTs.longValue());
			pInsert.setLong(17, env.tx.versionId);
			pInsert.setLong(18, Long.MAX_VALUE);
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "SchedulingHierarchy: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE SCHEDULING_HIERARCHY " +
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
				throw new FatalException(new SDMSMessage(env, "01110181955", "SchedulingHierarchy : $1\n$2", stmt, sqle.toString()));
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

			throw new FatalException(new SDMSMessage(env, "01110181956", "SchedulingHierarchy: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkIsStatic(Boolean p)
	{
		if(p.booleanValue() == SDMSSchedulingHierarchy.STATIC) return true;
		if(p.booleanValue() == SDMSSchedulingHierarchy.DYNAMIC) return true;
		return false;
	}
	static public boolean checkSuspend(Integer p)
	{
		switch (p.intValue()) {
		case SDMSSchedulingHierarchy.CHILDSUSPEND:
		case SDMSSchedulingHierarchy.NOSUSPEND:
		case SDMSSchedulingHierarchy.SUSPEND:
			return true;
		}
		return false;
	}
	static public boolean checkResumeBase(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSSchedulingHierarchy.MINUTE:
		case SDMSSchedulingHierarchy.HOUR:
		case SDMSSchedulingHierarchy.DAY:
		case SDMSSchedulingHierarchy.WEEK:
		case SDMSSchedulingHierarchy.MONTH:
		case SDMSSchedulingHierarchy.YEAR:
			return true;
		}
		return false;
	}
	static public boolean checkMergeMode(Integer p)
	{
		switch (p.intValue()) {
		case SDMSSchedulingHierarchy.MERGE_LOCAL:
		case SDMSSchedulingHierarchy.MERGE_GLOBAL:
		case SDMSSchedulingHierarchy.NOMERGE:
		case SDMSSchedulingHierarchy.FAILURE:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : SchedulingHierarchy", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seParentId : " + seParentId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seChildId : " + seChildId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "aliasName : " + aliasName, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isStatic : " + isStatic, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "priority : " + priority, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "suspend : " + suspend, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeAt : " + resumeAt, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeIn : " + resumeIn, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resumeBase : " + resumeBase, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "mergeMode : " + mergeMode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "estpId : " + estpId, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "seParentId : " + seParentId + "\n" +
		        indentString + "seChildId  : " + seChildId + "\n" +
		        indentString + "aliasName  : " + aliasName + "\n" +
		        indentString + "isStatic   : " + isStatic + "\n" +
		        indentString + "priority   : " + priority + "\n" +
		        indentString + "suspend    : " + suspend + "\n" +
		        indentString + "resumeAt   : " + resumeAt + "\n" +
		        indentString + "resumeIn   : " + resumeIn + "\n" +
		        indentString + "resumeBase : " + resumeBase + "\n" +
		        indentString + "mergeMode  : " + mergeMode + "\n" +
		        indentString + "estpId     : " + estpId + "\n" +
		        indentString + "creatorUId : " + creatorUId + "\n" +
		        indentString + "createTs   : " + createTs + "\n" +
		        indentString + "changerUId : " + changerUId + "\n" +
		        indentString + "changeTs   : " + changeTs + "\n" +
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
