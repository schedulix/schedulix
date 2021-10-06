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

public class SDMSSystemMessageProxyGeneric extends SDMSProxy
{

	public static final int CANCEL = 1;
	public static final int RERUN = 2;
	public static final int ENABLE = 3;
	public static final int SET_STATE = 4;
	public static final int IGN_DEPENDENCY = 5;
	public static final int IGN_RESOURCE = 6;
	public static final int CLONE = 7;
	public static final int SUSPEND = 8;
	public static final int CLEAR_WARNING = 9;
	public static final int SET_WARNING = 29;
	public static final int PRIORITY = 10;
	public static final int MODIFY_PARAMETER = 11;
	public static final int KILL = 12;
	public static final int SET_JOB_STATE = 13;
	public static final int DISABLE = 23;
	public static final int RESUME = 28;
	public static final int RENICE = 30;
	public static final int NICEVALUE = 50;
	public static final int APPROVAL = 1;
	public final static long privilegeMask = SDMSPrivilege.ALL;

	static final public int requestMsg_size = 512;
	static final public int comment_size = 1024;

	private static SDMSTable masterTables[] = null;

	protected SDMSSystemMessageProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSSystemMessage getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSSystemMessageTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSSystemMessage (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSSystemMessage)p;
	}

	public Integer getMsgType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getMsgType (env));
	}

	public String getMsgTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSystemMessageGeneric) object).getMsgTypeAsString (env);
	}

	public void setMsgType (SystemEnvironment env, Integer p_msgType)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setMsgType (env, p_msgType);
		return ;
	}
	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getMasterId (env));
	}

	public void setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setMasterId (env, p_masterId);
		return ;
	}
	public Integer getOperation (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getOperation (env));
	}

	public String getOperationAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSystemMessageGeneric) object).getOperationAsString (env);
	}

	public void setOperation (SystemEnvironment env, Integer p_operation)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setOperation (env, p_operation);
		return ;
	}
	public Boolean getIsMandatory (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getIsMandatory (env));
	}

	public void setIsMandatory (SystemEnvironment env, Boolean p_isMandatory)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setIsMandatory (env, p_isMandatory);
		return ;
	}
	public Long getRequestUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getRequestUId (env));
	}

	public void setRequestUId (SystemEnvironment env, Long p_requestUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setRequestUId (env, p_requestUId);
		return ;
	}
	public Long getRequestTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getRequestTs (env));
	}

	public void setRequestTs (SystemEnvironment env, Long p_requestTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setRequestTs (env, p_requestTs);
		return ;
	}
	public String getRequestMsg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getRequestMsg (env));
	}

	public void setRequestMsg (SystemEnvironment env, String p_requestMsg)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setRequestMsg (env, p_requestMsg);
		return ;
	}
	public static int getRequestMsgMaxLength ()
	{
		return (512);
	}
	public Long getAdditionalLong (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getAdditionalLong (env));
	}

	public void setAdditionalLong (SystemEnvironment env, Long p_additionalLong)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setAdditionalLong (env, p_additionalLong);
		return ;
	}
	public Boolean getAdditionalBool (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getAdditionalBool (env));
	}

	public void setAdditionalBool (SystemEnvironment env, Boolean p_additionalBool)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setAdditionalBool (env, p_additionalBool);
		return ;
	}
	public Long getSecondLong (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getSecondLong (env));
	}

	public void setSecondLong (SystemEnvironment env, Long p_secondLong)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setSecondLong (env, p_secondLong);
		return ;
	}
	public String getComment (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getComment (env));
	}

	public void setComment (SystemEnvironment env, String p_comment)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setComment (env, p_comment);
		return ;
	}
	public static int getCommentMaxLength ()
	{
		return (1024);
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSSystemMessage setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSSystemMessage)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSystemMessageGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSSystemMessage setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSystemMessageGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSSystemMessage)this;
	}

	public SDMSKey getSortKey(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSKey s = null;
		Long myId = getId(sysEnv);
		if (sysEnv.tx.sortKeyMap == null)
			sysEnv.tx.sortKeyMap = new HashMap();
		else
			s = (SDMSKey) sysEnv.tx.sortKeyMap.get(myId);
		if (s != null) return s;
		boolean gotIt = false;
		s = new SDMSKey();

		s.add(getRequestTs(sysEnv));

		sysEnv.tx.sortKeyMap.put(myId, s);
		return s;
	}

	public void delete (SystemEnvironment env)
	throws SDMSException
	{
		touchMaster(env);
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
		p = checkPrivs;
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSmeId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing SystemMessage $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSmeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.uid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSSystemMessageGeneric) object).print();
	}
}
