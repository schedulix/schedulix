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

public class SDMSNamedResourceProxyGeneric extends SDMSProxy
{

	public static final int STATIC = 1;
	public static final int SYSTEM = 2;
	public static final int POOL = 3;
	public static final int SYNCHRONIZING = 4;
	public static final int CATEGORY = 8;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.CREATE_CONTENT|SDMSPrivilege.CREATE_PARENT_CONTENT|SDMSPrivilege.RESOURCE|SDMSPrivilege.GRANT;

	static final public int name_size = 64;

	protected SDMSNamedResourceProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setName (env, p_name);
		return ;
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getParentId (env));
	}

	public void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setParentId (env, p_parentId);
		return ;
	}
	public Integer getUsage (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getUsage (env));
	}

	public String getUsageAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSNamedResourceGeneric) object).getUsageAsString (env);
	}

	public void setUsage (SystemEnvironment env, Integer p_usage)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setUsage (env, p_usage);
		return ;
	}
	public Long getRspId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getRspId (env));
	}

	public void setRspId (SystemEnvironment env, Long p_rspId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setRspId (env, p_rspId);
		return ;
	}
	public Float getFactor (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getFactor (env));
	}

	public void setFactor (SystemEnvironment env, Float p_factor)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setFactor (env, p_factor);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSNamedResource setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSNamedResourceGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSNamedResource)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSNamedResource setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSNamedResourceGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSNamedResource)this;
	}
	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSNamedResourceGeneric)(object)).getInheritPrivs (env));
	}

	public void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT|SDMSPrivilege.GRANT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSNamedResourceGeneric)(object)).setInheritPrivs (env, p_inheritPrivs);
		return ;
	}
	public SDMSNamedResource set_ParentIdName (SystemEnvironment env, Long p_parentId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSNamedResourceGeneric)(object)).set_ParentIdName (env, p_parentId, p_name);
		return (SDMSNamedResource)this;
	}

	public SDMSProxy getParent(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long parentId = getParentId(sysEnv);
		if (parentId == null) return null;
		return SDMSNamedResourceTable.getObject(sysEnv, parentId);
	}

	public String pathString(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return pathVector(sysEnv).toString();
	}

	public PathVector pathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		PathVector path;
		SDMSNamedResource f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getName(sysEnv));
		while(id != null) {
			f = SDMSNamedResourceTable.getObject(sysEnv, id);
			path.add(0, f.getName(sysEnv));
			id = f.getParentId(sysEnv);
		}
		return path;
	}

	public PathVector idPathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		PathVector path;
		SDMSNamedResource f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getId(sysEnv).toString());
		while(id != null) {
			f = SDMSNamedResourceTable.getObject(sysEnv, id);
			path.add(0, f.getId(sysEnv).toString());
			id = f.getParentId(sysEnv);
		}
		return path;
	}

	public Vector getContent(SystemEnvironment env)
	throws SDMSException
	{
		final Vector result = new Vector();
		final Long id = getId(env);
		Vector tmp;

		tmp = SDMSNamedResourceTable.idx_parentId.getVector(env, id);
		result.addAll(tmp);
		return result;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Named Resource $1", pathString(sysEnv));
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
		((SDMSNamedResourceGeneric) object).print();
	}
}
