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

public class SDMSIntervalProxyGeneric extends SDMSProxy
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
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.GRANT;

	static final public int name_size = 64;

	protected SDMSIntervalProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setName (env, p_name);
		return ;
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getStartTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getStartTime (env));
	}

	public void setStartTime (SystemEnvironment env, Long p_startTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setStartTime (env, p_startTime);
		return ;
	}
	public Long getEndTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getEndTime (env));
	}

	public void setEndTime (SystemEnvironment env, Long p_endTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setEndTime (env, p_endTime);
		return ;
	}
	public Long getDelay (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getDelay (env));
	}

	public void setDelay (SystemEnvironment env, Long p_delay)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setDelay (env, p_delay);
		return ;
	}
	public Integer getBaseInterval (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getBaseInterval (env));
	}

	public String getBaseIntervalAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSIntervalGeneric) object).getBaseIntervalAsString (env);
	}

	public void setBaseInterval (SystemEnvironment env, Integer p_baseInterval)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setBaseInterval (env, p_baseInterval);
		return ;
	}
	public Integer getBaseIntervalMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getBaseIntervalMultiplier (env));
	}

	public void setBaseIntervalMultiplier (SystemEnvironment env, Integer p_baseIntervalMultiplier)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setBaseIntervalMultiplier (env, p_baseIntervalMultiplier);
		return ;
	}
	public Integer getDuration (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getDuration (env));
	}

	public String getDurationAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSIntervalGeneric) object).getDurationAsString (env);
	}

	public void setDuration (SystemEnvironment env, Integer p_duration)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setDuration (env, p_duration);
		return ;
	}
	public Integer getDurationMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getDurationMultiplier (env));
	}

	public void setDurationMultiplier (SystemEnvironment env, Integer p_durationMultiplier)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setDurationMultiplier (env, p_durationMultiplier);
		return ;
	}
	public Long getSyncTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getSyncTime (env));
	}

	public void setSyncTime (SystemEnvironment env, Long p_syncTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setSyncTime (env, p_syncTime);
		return ;
	}
	public Boolean getIsInverse (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getIsInverse (env));
	}

	public void setIsInverse (SystemEnvironment env, Boolean p_isInverse)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setIsInverse (env, p_isInverse);
		return ;
	}
	public Boolean getIsMerge (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getIsMerge (env));
	}

	public void setIsMerge (SystemEnvironment env, Boolean p_isMerge)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setIsMerge (env, p_isMerge);
		return ;
	}
	public Long getEmbeddedIntervalId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getEmbeddedIntervalId (env));
	}

	public void setEmbeddedIntervalId (SystemEnvironment env, Long p_embeddedIntervalId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setEmbeddedIntervalId (env, p_embeddedIntervalId);
		return ;
	}
	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSInterval setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSIntervalGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSInterval)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSIntervalGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSIntervalGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSInterval setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSIntervalGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSInterval)this;
	}
	public void delete (SystemEnvironment env)
	throws SDMSException
	{
		SDMSObjectCommentTable.dropComment (env, getId (env));
		Vector gv = SDMSGrantTable.idx_objectId.getVector(env, getId(env));
		for (int i = 0; i < gv.size(); ++i) {
			SDMSGrant g = (SDMSGrant) gv.get(i);
			g.delete(env);
		}
		super.delete(env);
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null) & p) == p;
	}

	public long getPrivilegeMask()
	{
		return privilegeMask;
	}

	public final SDMSPrivilege getPrivileges(SystemEnvironment env)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, null));
	}

	public final SDMSPrivilege getPrivilegesForGroups(SystemEnvironment env, Vector checkGroups)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, checkGroups));
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		Vector groups;
		if (checkGroups == null) groups = new Vector();
		else groups = checkGroups;

		long p = 0;
		if(env.cEnv.isUser()) {
			if(checkGroups == null)
				groups.addAll(env.cEnv.gid());
			if(groups.contains(SDMSObject.adminGId)) {
				return checkPrivs;
			}
			if(groups.contains(getOwnerId(env))) {

				p = checkPrivs & (~SDMSPrivilege.CREATE_PARENT_CONTENT);
				if (p == checkPrivs) {
					return p;
				}
			}
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Interval $1", getName(sysEnv));
		return m;
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.euid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSIntervalGeneric) object).print();
	}
}
