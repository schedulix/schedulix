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
	public static final boolean ENABLED = false;
	public static final boolean DISABLED = true;
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
	public final static int nr_isDisabled = 6;
	public final static int nr_priority = 7;
	public final static int nr_suspend = 8;
	public final static int nr_resumeAt = 9;
	public final static int nr_resumeIn = 10;
	public final static int nr_resumeBase = 11;
	public final static int nr_mergeMode = 12;
	public final static int nr_estpId = 13;
	public final static int nr_creatorUId = 14;
	public final static int nr_createTs = 15;
	public final static int nr_changerUId = 16;
	public final static int nr_changeTs = 17;

	public static String tableName = SDMSSchedulingHierarchyTableGeneric.tableName;

	protected Long seParentId;
	protected Long seChildId;
	protected String aliasName;
	protected Boolean isStatic;
	protected Boolean isDisabled;
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

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSSchedulingHierarchyGeneric(
	        SystemEnvironment env,
	        Long p_seParentId,
	        Long p_seChildId,
	        String p_aliasName,
	        Boolean p_isStatic,
	        Boolean p_isDisabled,
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
		isDisabled = p_isDisabled;
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

	public	void setSeParentId (SystemEnvironment env, Long p_seParentId)
	throws SDMSException
	{
		if(p_seParentId != null && p_seParentId.equals(seParentId)) return;
		if(p_seParentId == null && seParentId == null) return;
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
			o.versions.table.index(env, o, 25);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getSeChildId (SystemEnvironment env)
	throws SDMSException
	{
		return (seChildId);
	}

	public	void setSeChildId (SystemEnvironment env, Long p_seChildId)
	throws SDMSException
	{
		if(p_seChildId != null && p_seChildId.equals(seChildId)) return;
		if(p_seChildId == null && seChildId == null) return;
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
			o.versions.table.index(env, o, 10);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getAliasName (SystemEnvironment env)
	throws SDMSException
	{
		return (aliasName);
	}

	public	void setAliasName (SystemEnvironment env, String p_aliasName)
	throws SDMSException
	{
		if(p_aliasName != null && p_aliasName.equals(aliasName)) return;
		if(p_aliasName == null && aliasName == null) return;
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
			o.versions.table.index(env, o, 16);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
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

	public	void setIsStatic (SystemEnvironment env, Boolean p_isStatic)
	throws SDMSException
	{
		if(isStatic.equals(p_isStatic)) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.isStatic = p_isStatic;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsDisabled (SystemEnvironment env)
	throws SDMSException
	{
		return (isDisabled);
	}

	public String getIsDisabledAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Boolean v = getIsDisabled (env);
		final boolean b = v.booleanValue();
		if (b == SDMSSchedulingHierarchy.ENABLED)
			return "ENABLED";
		if (b == SDMSSchedulingHierarchy.DISABLED)
			return "DISABLED";
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown SchedulingHierarchy.isDisabled: $1",
		                          getIsDisabled (env)));
	}

	public	void setIsDisabled (SystemEnvironment env, Boolean p_isDisabled)
	throws SDMSException
	{
		if(isDisabled.equals(p_isDisabled)) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.isDisabled = p_isDisabled;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getPriority (SystemEnvironment env)
	throws SDMSException
	{
		return (priority);
	}

	public	void setPriority (SystemEnvironment env, Integer p_priority)
	throws SDMSException
	{
		if(priority.equals(p_priority)) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.priority = p_priority;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setSuspend (SystemEnvironment env, Integer p_suspend)
	throws SDMSException
	{
		if(suspend.equals(p_suspend)) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.suspend = p_suspend;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeAt);
	}

	public	void setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		if(p_resumeAt != null && p_resumeAt.equals(resumeAt)) return;
		if(p_resumeAt == null && resumeAt == null) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		if (p_resumeAt != null && p_resumeAt.length() > 20) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SchedulingHierarchy) Length of $1 exceeds maximum length $2", "resumeAt", "20")
			);
		}
		o.resumeAt = p_resumeAt;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		return (resumeIn);
	}

	public	void setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		if(p_resumeIn != null && p_resumeIn.equals(resumeIn)) return;
		if(p_resumeIn == null && resumeIn == null) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.resumeIn = p_resumeIn;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		if(p_resumeBase != null && p_resumeBase.equals(resumeBase)) return;
		if(p_resumeBase == null && resumeBase == null) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.resumeBase = p_resumeBase;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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

	public	void setMergeMode (SystemEnvironment env, Integer p_mergeMode)
	throws SDMSException
	{
		if(mergeMode.equals(p_mergeMode)) return;
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.mergeMode = p_mergeMode;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getEstpId (SystemEnvironment env)
	throws SDMSException
	{
		return (estpId);
	}

	public	void setEstpId (SystemEnvironment env, Long p_estpId)
	throws SDMSException
	{
		if(p_estpId != null && p_estpId.equals(estpId)) return;
		if(p_estpId == null && estpId == null) return;
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
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
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
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
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
		SDMSSchedulingHierarchyGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SchedulingHierarchy) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
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
		SDMSSchedulingHierarchyGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
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
		SDMSSchedulingHierarchyGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSchedulingHierarchyGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
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
	                Boolean p_isDisabled,
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
		isDisabled = p_isDisabled;
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
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "INSERT INTO SCHEDULING_HIERARCHY (" +
				        "ID" +
				        ", " + squote + "SE_PARENT_ID" + equote +
				        ", " + squote + "SE_CHILD_ID" + equote +
				        ", " + squote + "ALIAS_NAME" + equote +
				        ", " + squote + "IS_STATIC" + equote +
				        ", " + squote + "IS_DISABLED" + equote +
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
				        ", ?" +
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "SchedulingHierarchy: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			if (seParentId == null)
				myInsert.setNull(2, Types.INTEGER);
			else
				myInsert.setLong (2, seParentId.longValue());
			if (seChildId == null)
				myInsert.setNull(3, Types.INTEGER);
			else
				myInsert.setLong (3, seChildId.longValue());
			if (aliasName == null)
				myInsert.setNull(4, Types.VARCHAR);
			else
				myInsert.setString(4, aliasName);
			myInsert.setInt (5, isStatic.booleanValue() ? 1 : 0);
			myInsert.setInt (6, isDisabled.booleanValue() ? 1 : 0);
			myInsert.setInt(7, priority.intValue());
			myInsert.setInt(8, suspend.intValue());
			if (resumeAt == null)
				myInsert.setNull(9, Types.VARCHAR);
			else
				myInsert.setString(9, resumeAt);
			if (resumeIn == null)
				myInsert.setNull(10, Types.INTEGER);
			else
				myInsert.setInt(10, resumeIn.intValue());
			if (resumeBase == null)
				myInsert.setNull(11, Types.INTEGER);
			else
				myInsert.setInt(11, resumeBase.intValue());
			myInsert.setInt(12, mergeMode.intValue());
			if (estpId == null)
				myInsert.setNull(13, Types.INTEGER);
			else
				myInsert.setLong (13, estpId.longValue());
			myInsert.setLong (14, creatorUId.longValue());
			myInsert.setLong (15, createTs.longValue());
			myInsert.setLong (16, changerUId.longValue());
			myInsert.setLong (17, changeTs.longValue());
			myInsert.setLong(18, env.tx.versionId);
			myInsert.setLong(19, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "SchedulingHierarchy: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				stmt =
				        "UPDATE SCHEDULING_HIERARCHY " +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				// Can't prepare statement
				throw new FatalException(new SDMSMessage(env, "01110181955", "SchedulingHierarchy : $1\n$2", stmt, sqle.toString()));
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
			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "SchedulingHierarchy: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkIsStatic(Boolean p)
	{
		if(p.booleanValue() == SDMSSchedulingHierarchy.STATIC) return true;
		if(p.booleanValue() == SDMSSchedulingHierarchy.DYNAMIC) return true;
		return false;
	}
	static public boolean checkIsDisabled(Boolean p)
	{
		if(p.booleanValue() == SDMSSchedulingHierarchy.ENABLED) return true;
		if(p.booleanValue() == SDMSSchedulingHierarchy.DISABLED) return true;
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
		SDMSThread.doTrace(null, "isDisabled : " + isDisabled, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "isDisabled : " + isDisabled + "\n" +
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
