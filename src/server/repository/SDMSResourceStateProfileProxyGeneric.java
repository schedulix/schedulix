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

public class SDMSResourceStateProfileProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.GRANT;

	static final public int name_size = 64;

	protected SDMSResourceStateProfileProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceStateProfileGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceStateProfileGeneric)(object)).setName (env, p_name);
		return ;
	}
	public Long getInitialRsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceStateProfileGeneric)(object)).getInitialRsdId (env));
	}

	public void setInitialRsdId (SystemEnvironment env, Long p_initialRsdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceStateProfileGeneric)(object)).setInitialRsdId (env, p_initialRsdId);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceStateProfileGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceStateProfileGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceStateProfileGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceStateProfileGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceStateProfileGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceStateProfileGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSResourceStateProfile setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSResourceStateProfileGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSResourceStateProfile)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceStateProfileGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceStateProfileGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSResourceStateProfile setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSResourceStateProfileGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSResourceStateProfile)this;
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
		try {
			if(env.cEnv.isUser() || env.cEnv.isJob()) {
				if (env.cEnv.isJob()) {
					HashSet hg = new HashSet();
					SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(env, env.cEnv.uid());
					Long smeOwner = sme.getOwnerId(env);
					hg.add(smeOwner);
					hg.add(SDMSObject.publicGId);
					env.cEnv.pushGid(env, hg);
				}
				if(checkGroups == null)
					groups.addAll(env.cEnv.gid());
				if(groups.contains(SDMSObject.adminGId)) p = checkPrivs;
				else {
					p = p | SDMSPrivilege.VIEW & checkPrivs;
				}
			} else p = checkPrivs;
		} catch(Throwable t) {
			if (env.cEnv.isJob()) {
				env.cEnv.popGid(env);
			}
			throw t;
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Resource State Profile $1", getName(sysEnv));
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
		((SDMSResourceStateProfileGeneric) object).print();
	}
}
