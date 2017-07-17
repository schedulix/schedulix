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

public class SDMSScopeProxyGeneric extends SDMSProxy
{

	public static final int SCOPE = 1;
	public static final int SERVER = 2;
	public static final int MD5 = SDMSUser.MD5;
	public static final int SHA256 = SDMSUser.SHA256;
	public static final int NOMINAL = 1;
	public static final int NONFATAL = 2;
	public static final int FATAL = 3;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.CREATE_CONTENT|SDMSPrivilege.CREATE_PARENT_CONTENT|SDMSPrivilege.RESOURCE|SDMSPrivilege.GRANT|SDMSPrivilege.EXECUTE;

	static final public int name_size = 64;
	static final public int passwd_size = 64;
	static final public int salt_size = 64;
	static final public int pid_size = 32;
	static final public int node_size = 32;
	static final public int errmsg_size = 256;

	protected SDMSScopeProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSScope getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSScopeTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSScope (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSScope)p;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setName (env, p_name);
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
		return (((SDMSScopeGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getParentId (env));
	}

	public void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setParentId (env, p_parentId);
		return ;
	}
	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getType (env));
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScopeGeneric) object).getTypeAsString (env);
	}

	public void setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setType (env, p_type);
		return ;
	}
	public Boolean getIsTerminate (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getIsTerminate (env));
	}

	public void setIsTerminate (SystemEnvironment env, Boolean p_isTerminate)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setIsTerminate (env, p_isTerminate);
		return ;
	}
	public Boolean getHasAlteredConfig (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getHasAlteredConfig (env));
	}

	public void setHasAlteredConfig (SystemEnvironment env, Boolean p_hasAlteredConfig)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setHasAlteredConfig (env, p_hasAlteredConfig);
		return ;
	}
	public Boolean getIsSuspended (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getIsSuspended (env));
	}

	public void setIsSuspended (SystemEnvironment env, Boolean p_isSuspended)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setIsSuspended (env, p_isSuspended);
		return ;
	}
	public Boolean getIsEnabled (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getIsEnabled (env));
	}

	public void setIsEnabled (SystemEnvironment env, Boolean p_isEnabled)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setIsEnabled (env, p_isEnabled);
		return ;
	}
	public Boolean getIsRegistered (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getIsRegistered (env));
	}

	public void setIsRegistered (SystemEnvironment env, Boolean p_isRegistered)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setIsRegistered (env, p_isRegistered);
		return ;
	}
	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getState (env));
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScopeGeneric) object).getStateAsString (env);
	}

	public void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setState (env, p_state);
		return ;
	}
	public String getPasswd (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getPasswd (env));
	}

	public void setPasswd (SystemEnvironment env, String p_passwd)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setPasswd (env, p_passwd);
		return ;
	}
	public static int getPasswdMaxLength ()
	{
		return (64);
	}
	public String getSalt (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getSalt (env));
	}

	public void setSalt (SystemEnvironment env, String p_salt)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setSalt (env, p_salt);
		return ;
	}
	public static int getSaltMaxLength ()
	{
		return (64);
	}
	public Integer getMethod (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getMethod (env));
	}

	public String getMethodAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSScopeGeneric) object).getMethodAsString (env);
	}

	public void setMethod (SystemEnvironment env, Integer p_method)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setMethod (env, p_method);
		return ;
	}
	public String getPid (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getPid (env));
	}

	public void setPid (SystemEnvironment env, String p_pid)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setPid (env, p_pid);
		return ;
	}
	public static int getPidMaxLength ()
	{
		return (32);
	}
	public String getNode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getNode (env));
	}

	public void setNode (SystemEnvironment env, String p_node)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setNode (env, p_node);
		return ;
	}
	public static int getNodeMaxLength ()
	{
		return (32);
	}
	public String getErrmsg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getErrmsg (env));
	}

	public void setErrmsg (SystemEnvironment env, String p_errmsg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setErrmsg (env, p_errmsg);
		return ;
	}
	public static int getErrmsgMaxLength ()
	{
		return (256);
	}
	public Long getLastActive (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getLastActive (env));
	}

	public void setLastActive (SystemEnvironment env, Long p_lastActive)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setLastActive (env, p_lastActive);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSScope setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSScopeGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSScope)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSScope setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSScopeGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSScope)this;
	}
	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSScopeGeneric)(object)).getInheritPrivs (env));
	}

	public void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT|SDMSPrivilege.GRANT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSScopeGeneric)(object)).setInheritPrivs (env, p_inheritPrivs);
		return ;
	}
	public SDMSScope set_ParentIdName (SystemEnvironment env, Long p_parentId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSScopeGeneric)(object)).set_ParentIdName (env, p_parentId, p_name);
		return (SDMSScope)this;
	}

	public SDMSProxy getParent(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long parentId = getParentId(sysEnv);
		if (parentId == null) return null;
		return SDMSScopeTable.getObject(sysEnv, parentId);
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
		SDMSScope f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getName(sysEnv));
		while(id != null) {
			f = SDMSScopeTable.getObject(sysEnv, id);
			path.add(0, f.getName(sysEnv));
			id = f.getParentId(sysEnv);
		}
		return path;
	}

	public PathVector idPathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		PathVector path;
		SDMSScope f;
		Long id = getParentId(sysEnv);

		path = new PathVector();
		path.add(getId(sysEnv).toString());
		while(id != null) {
			f = SDMSScopeTable.getObject(sysEnv, id);
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

		tmp = SDMSScopeTable.idx_parentId.getVector(env, id);
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
			if(groups.contains(SDMSObject.adminGId)) {
				if (env.cEnv.isJob()) {
					env.cEnv.popGid(env);
				}
				return checkPrivs;
			}
			if(groups.contains(getOwnerId(env))) {
				p = checkPrivs & (~SDMSPrivilege.CREATE_PARENT_CONTENT);
				if (p == checkPrivs) {
					if (env.cEnv.isJob()) {
						env.cEnv.popGid(env);
					}
					return p;
				}
			}
			if (env.cEnv.isJob()) {
				env.cEnv.popGid(env);
			}
		} else {
			if((env.cEnv.isJobServer()))
				p = checkPrivs;
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Scope $1", pathString(sysEnv));
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
		((SDMSScopeGeneric) object).print();
	}
}
