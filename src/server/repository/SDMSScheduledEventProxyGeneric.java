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

public class SDMSScheduledEventProxyGeneric extends SDMSProxy
{

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
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.GRANT;

	static final public int errorCode_size = 32;
	static final public int errorMsg_size = 256;

	protected SDMSScheduledEventProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSScheduledEvent getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSScheduledEventTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSScheduledEvent (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSScheduledEvent)p;
	}

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getSceId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getSceId (env));
	}

	public void setSceId (SystemEnvironment env, Long p_sceId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setSceId (env, p_sceId);
		return ;
	}
	public Long getEvtId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getEvtId (env));
	}

	public void setEvtId (SystemEnvironment env, Long p_evtId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setEvtId (env, p_evtId);
		return ;
	}
	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getIsActive (env));
	}

	public String getIsActiveAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScheduledEventGeneric) object).getIsActiveAsString (env);
	}

	public void setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setIsActive (env, p_isActive);
		return ;
	}
	public Boolean getIsBroken (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getIsBroken (env));
	}

	public String getIsBrokenAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScheduledEventGeneric) object).getIsBrokenAsString (env);
	}

	public void setIsBroken (SystemEnvironment env, Boolean p_isBroken)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setIsBroken (env, p_isBroken);
		return ;
	}
	public String getErrorCode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getErrorCode (env));
	}

	public void setErrorCode (SystemEnvironment env, String p_errorCode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setErrorCode (env, p_errorCode);
		return ;
	}
	public String getErrorMsg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getErrorMsg (env));
	}

	public void setErrorMsg (SystemEnvironment env, String p_errorMsg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setErrorMsg (env, p_errorMsg);
		return ;
	}
	public Long getLastStartTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getLastStartTime (env));
	}

	public void setLastStartTime (SystemEnvironment env, Long p_lastStartTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setLastStartTime (env, p_lastStartTime);
		return ;
	}
	public Long getNextActivityTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getNextActivityTime (env));
	}

	public void setNextActivityTime (SystemEnvironment env, Long p_nextActivityTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setNextActivityTime (env, p_nextActivityTime);
		return ;
	}
	public Boolean getNextActivityIsTrigger (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getNextActivityIsTrigger (env));
	}

	public void setNextActivityIsTrigger (SystemEnvironment env, Boolean p_nextActivityIsTrigger)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setNextActivityIsTrigger (env, p_nextActivityIsTrigger);
		return ;
	}
	public Integer getBacklogHandling (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getBacklogHandling (env));
	}

	public String getBacklogHandlingAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScheduledEventGeneric) object).getBacklogHandlingAsString (env);
	}

	public void setBacklogHandling (SystemEnvironment env, Integer p_backlogHandling)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setBacklogHandling (env, p_backlogHandling);
		return ;
	}
	public Integer getSuspendLimit (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getSuspendLimit (env));
	}

	public String getSuspendLimitAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScheduledEventGeneric) object).getSuspendLimitAsString (env);
	}

	public void setSuspendLimit (SystemEnvironment env, Integer p_suspendLimit)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setSuspendLimit (env, p_suspendLimit);
		return ;
	}
	public Integer getSuspendLimitMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getSuspendLimitMultiplier (env));
	}

	public void setSuspendLimitMultiplier (SystemEnvironment env, Integer p_suspendLimitMultiplier)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setSuspendLimitMultiplier (env, p_suspendLimitMultiplier);
		return ;
	}
	public Boolean getIsCalendar (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getIsCalendar (env));
	}

	public String getIsCalendarAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScheduledEventGeneric) object).getIsCalendarAsString (env);
	}

	public void setIsCalendar (SystemEnvironment env, Boolean p_isCalendar)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setIsCalendar (env, p_isCalendar);
		return ;
	}
	public Integer getCalendarHorizon (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getCalendarHorizon (env));
	}

	public void setCalendarHorizon (SystemEnvironment env, Integer p_calendarHorizon)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setCalendarHorizon (env, p_calendarHorizon);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSScheduledEvent setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSScheduledEventGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSScheduledEvent)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduledEventGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduledEventGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSScheduledEvent setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSScheduledEventGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSScheduledEvent)this;
	}
	public SDMSScheduledEvent set_SceIdEvtId (SystemEnvironment env, Long p_sceId, Long p_evtId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSScheduledEventGeneric)(object)).set_SceIdEvtId (env, p_sceId, p_evtId);
		return (SDMSScheduledEvent)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Scheduled Event $1", getId(sysEnv));
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
		((SDMSScheduledEventGeneric) object).print();
	}
}
