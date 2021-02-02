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

public class SDMSUserParameterProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int name_size = 64;
	static final public int value_size = 256;

	private static SDMSTable masterTables[] = null;

	protected SDMSUserParameterProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSUserParameter getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSUserParameterTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSUserParameter (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSUserParameter)p;
	}

	public Long getUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getUId (env));
	}

	public void setUId (SystemEnvironment env, Long p_uId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setUId (env, p_uId);
		return ;
	}
	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setName (env, p_name);
		return ;
	}
	public static int getNameMaxLength ()
	{
		return (64);
	}
	public String getValue (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getValue (env));
	}

	public void setValue (SystemEnvironment env, String p_value)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setValue (env, p_value);
		return ;
	}
	public static int getValueMaxLength ()
	{
		return (256);
	}
	public Boolean getIsLong (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getIsLong (env));
	}

	public void setIsLong (SystemEnvironment env, Boolean p_isLong)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setIsLong (env, p_isLong);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSUserParameter setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSUserParameter)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSUserParameterGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSUserParameter setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSUserParameter)this;
	}
	public SDMSUserParameter set_UIdName (SystemEnvironment env, Long p_uId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSUserParameterGeneric)(object)).set_UIdName (env, p_uId, p_name);
		return (SDMSUserParameter)this;
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

		gotIt = false;
		Long uId = getUId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSUserTable.getObject(sysEnv, uId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		s.add(getName(sysEnv));

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

		t = SystemEnvironment.repository.getTable(env, SDMSUserTable.tableName);
		try {
			SDMSProxy o = t.get(env, getUId(env));
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing User Parameter $1", getName(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSUserTable.tableName);
		try {
			SDMSProxy p = t.get(env, getUId(env));
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
		((SDMSUserParameterGeneric) object).print();
	}
}
