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

public class SDMSSmeCounterProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.ALL;

	protected SDMSSmeCounterProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSSmeCounter getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSSmeCounterTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSSmeCounter (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSSmeCounter)p;
	}

	public Integer getJahr (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getJahr (env));
	}

	public void setJahr (SystemEnvironment env, Integer p_jahr)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setJahr (env, p_jahr);
		return ;
	}
	public Integer getMonat (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getMonat (env));
	}

	public void setMonat (SystemEnvironment env, Integer p_monat)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setMonat (env, p_monat);
		return ;
	}
	public Integer getTag (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getTag (env));
	}

	public void setTag (SystemEnvironment env, Integer p_tag)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setTag (env, p_tag);
		return ;
	}
	public Integer getAnzahl (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getAnzahl (env));
	}

	public void setAnzahl (SystemEnvironment env, Integer p_anzahl)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setAnzahl (env, p_anzahl);
		return ;
	}
	public Long getChecksum (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getChecksum (env));
	}

	public void setChecksum (SystemEnvironment env, Long p_checksum)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setChecksum (env, p_checksum);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSSmeCounter setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSSmeCounter)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSmeCounterGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSSmeCounter setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSmeCounterGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSSmeCounter)this;
	}
	public SDMSSmeCounter set_JahrMonatTag (SystemEnvironment env, Integer p_jahr, Integer p_monat, Integer p_tag)
	throws SDMSException
	{
		checkRead(env);
		((SDMSSmeCounterGeneric)(object)).set_JahrMonatTag (env, p_jahr, p_monat, p_tag);
		return (SDMSSmeCounter)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing SmeCounter $1", getId(sysEnv));
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
		((SDMSSmeCounterGeneric) object).print();
	}
}
