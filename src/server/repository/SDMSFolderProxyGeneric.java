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

public class SDMSFolderProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.CREATE_CONTENT|SDMSPrivilege.CREATE_PARENT_CONTENT|SDMSPrivilege.GRANT|SDMSPrivilege.RESOURCE|SDMSPrivilege.SUBMIT|SDMSPrivilege.MONITOR|SDMSPrivilege.OPERATE|SDMSPrivilege.PRIORITY|SDMSPrivilege.CANCEL|SDMSPrivilege.RERUN|SDMSPrivilege.ENABLE|SDMSPrivilege.CLEAR_WARNING|SDMSPrivilege.SET_STATE|SDMSPrivilege.IGN_DEPENDENCY|SDMSPrivilege.IGN_RESOURCE|SDMSPrivilege.CLONE|SDMSPrivilege.SUSPEND|SDMSPrivilege.MODIFY_PARAMETER|SDMSPrivilege.KILL|SDMSPrivilege.APPROVE;

	static final public int name_size = 64;

	protected SDMSFolderProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSFolder getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSFolderTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSFolder (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSFolder)p;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setName (env, p_name);
		return ;
	}
	public static int getNameMaxLength ()
	{
		return (64);
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getEnvId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getEnvId (env));
	}

	public void setEnvId (SystemEnvironment env, Long p_envId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setEnvId (env, p_envId);
		return ;
	}
	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getParentId (env));
	}

	public void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setParentId (env, p_parentId);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSFolder setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSFolderGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSFolder)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSFolder setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSFolderGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSFolder)this;
	}
	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSFolderGeneric)(object)).getInheritPrivs (env));
	}

	public void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT|SDMSPrivilege.GRANT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSFolderGeneric)(object)).setInheritPrivs (env, p_inheritPrivs);
		return ;
	}
	public SDMSFolder set_ParentIdName (SystemEnvironment env, Long p_parentId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSFolderGeneric)(object)).set_ParentIdName (env, p_parentId, p_name);
		return (SDMSFolder)this;
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

		s.add(pathString(sysEnv));

		sysEnv.tx.sortKeyMap.put(myId, s);
		return s;
	}

	public SDMSProxy getParent(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long parentId = getParentId(sysEnv);
		if (parentId == null) return null;
		return SDMSFolderTable.getObject(sysEnv, parentId);
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
		SDMSFolder f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getName(sysEnv));
		while(id != null) {
			f = SDMSFolderTable.getObject(sysEnv, id);
			path.add(0, f.getName(sysEnv));
			id = f.getParentId(sysEnv);
		}
		return path;
	}

	public String pathString(SystemEnvironment sysEnv, long version)
	throws SDMSException
	{
		return pathVector(sysEnv, version).toString();
	}

	public PathVector pathVector(SystemEnvironment sysEnv, long version)
	throws SDMSException
	{
		PathVector path;
		SDMSFolder f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getName(sysEnv));
		while(id != null) {
			f = SDMSFolderTable.getObject(sysEnv, id, version);
			path.add(0, f.getName(sysEnv));
			id = f.getParentId(sysEnv);
		}
		return path;
	}

	public PathVector idPathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		PathVector path;
		SDMSFolder f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getId(sysEnv).toString());
		while(id != null) {
			f = SDMSFolderTable.getObject(sysEnv, id);
			path.add(0, f.getId(sysEnv).toString());
			id = f.getParentId(sysEnv);
		}
		return path;
	}

	public PathVector idPathVector(SystemEnvironment sysEnv, long version)
	throws SDMSException
	{
		PathVector path;
		SDMSFolder f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getId(sysEnv).toString());
		while(id != null) {
			f = SDMSFolderTable.getObject(sysEnv, id, version);
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

		tmp = SDMSFolderTable.idx_parentId.getVector(env, id);
		result.addAll(tmp);

		tmp = SDMSSchedulingEntityTable.idx_folderId.getVector(env, id);
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
			g.setDeleteVersion(env, env.tx.txId);
		}
		super.delete(env);
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null) & p) == p;
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p, long version)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null, version) & p) == p;
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

	public final SDMSPrivilege getPrivileges(SystemEnvironment env, long version)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, null, version));
	}

	public final SDMSPrivilege getPrivilegesForGroups(SystemEnvironment env, Vector checkGroups, long version)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, checkGroups, version));
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups, long version)
	throws SDMSException
	{
		Vector groups;
		if (checkGroups == null) groups = new Vector();
		else groups = checkGroups;

		long p = 0;
		if(env.cEnv.isUser()) {
			if(checkGroups == null)
				groups.addAll(env.cEnv.gid());
			if(groups.contains(SDMSObject.adminGId))
				return checkPrivs;
			if(groups.contains(getOwnerId(env))) {
				p = checkPrivs & (~SDMSPrivilege.CREATE_PARENT_CONTENT);
				if (p == checkPrivs) return p;
			}
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Folder $1", pathString(sysEnv));
		return m;
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.uid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSFolderGeneric) object).print();
	}
}
