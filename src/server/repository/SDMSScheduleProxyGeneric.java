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

public class SDMSScheduleProxyGeneric extends SDMSProxy
{

	public final static String __version = "SDMSScheduleProxyGeneric $Revision: 2.6 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.CREATE_CONTENT|SDMSPrivilege.CREATE_PARENT_CONTENT|SDMSPrivilege.GRANT;

	static final public int name_size = 64;
	static final public int timeZone_size = 32;

	protected SDMSScheduleProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setName (env, p_name);
		return ;
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getIntId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getIntId (env));
	}

	public void setIntId (SystemEnvironment env, Long p_intId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setIntId (env, p_intId);
		return ;
	}
	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getParentId (env));
	}

	public void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setParentId (env, p_parentId);
		return ;
	}
	public String getTimeZone (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getTimeZone (env));
	}

	public void setTimeZone (SystemEnvironment env, String p_timeZone)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setTimeZone (env, p_timeZone);
		return ;
	}
	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public Boolean getIsActive (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getIsActive (env));
	}

	public String getIsActiveAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScheduleGeneric) object).getIsActiveAsString (env);
	}

	public void setIsActive (SystemEnvironment env, Boolean p_isActive)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setIsActive (env, p_isActive);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSSchedule setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSScheduleGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSSchedule)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSSchedule setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSScheduleGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSSchedule)this;
	}
	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScheduleGeneric)(object)).getInheritPrivs (env));
	}

	public void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT|SDMSPrivilege.GRANT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScheduleGeneric)(object)).setInheritPrivs (env, p_inheritPrivs);
		return ;
	}
	public SDMSSchedule set_ParentIdName (SystemEnvironment env, Long p_parentId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSScheduleGeneric)(object)).set_ParentIdName (env, p_parentId, p_name);
		return (SDMSSchedule)this;
	}

	public SDMSProxy getParent(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long parentId = getParentId(sysEnv);
		if (parentId == null) return null;
		return SDMSScheduleTable.getObject(sysEnv, parentId);
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
		SDMSSchedule f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getName(sysEnv));
		while(id != null) {
			f = SDMSScheduleTable.getObject(sysEnv, id);
			path.add(0, f.getName(sysEnv));
			id = f.getParentId(sysEnv);
		}
		return path;
	}

	public PathVector idPathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		PathVector path;
		SDMSSchedule f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getId(sysEnv).toString());
		while(id != null) {
			f = SDMSScheduleTable.getObject(sysEnv, id);
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

		tmp = SDMSScheduleTable.idx_parentId.getVector(env, id);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Schedule $1", pathString(sysEnv));
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
		((SDMSScheduleGeneric) object).print();
	}
}
