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

public class SDMSnpJobFootprintProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.ALL;

	protected SDMSnpJobFootprintProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public HashMap getFpScope (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getFpScope (env));
	}

	public void setFpScope (SystemEnvironment env, HashMap p_fpScope)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setFpScope (env, p_fpScope);
		return ;
	}
	public HashMap getFpFolder (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getFpFolder (env));
	}

	public void setFpFolder (SystemEnvironment env, HashMap p_fpFolder)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setFpFolder (env, p_fpFolder);
		return ;
	}
	public HashMap getFpLocal (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getFpLocal (env));
	}

	public void setFpLocal (SystemEnvironment env, HashMap p_fpLocal)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setFpLocal (env, p_fpLocal);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSnpJobFootprint setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSnpJobFootprint)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSnpJobFootprintGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSnpJobFootprint setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSnpJobFootprintGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSnpJobFootprint)this;
	}

	public SDMSKey getSortKey(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSKey s = new SDMSKey();
		s.add(getId(sysEnv));
		return s;
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
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing npJobFootprint $1", getId(sysEnv));
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
		((SDMSnpJobFootprintGeneric) object).print();
	}
}
