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

public class SDMSResourceTemplateProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.RESOURCE|SDMSPrivilege.GRANT;

	protected SDMSResourceTemplateProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSResourceTemplate getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSResourceTemplateTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSResourceTemplate (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSResourceTemplate)p;
	}

	public Long getNrId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getNrId (env));
	}

	public void setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setNrId (env, p_nrId);
		return ;
	}
	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getRsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getRsdId (env));
	}

	public void setRsdId (SystemEnvironment env, Long p_rsdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setRsdId (env, p_rsdId);
		return ;
	}
	public Integer getRequestableAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getRequestableAmount (env));
	}

	public void setRequestableAmount (SystemEnvironment env, Integer p_RequestableAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setRequestableAmount (env, p_RequestableAmount);
		return ;
	}
	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getAmount (env));
	}

	public void setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setAmount (env, p_amount);
		return ;
	}
	public Boolean getIsOnline (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getIsOnline (env));
	}

	public void setIsOnline (SystemEnvironment env, Boolean p_isOnline)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setIsOnline (env, p_isOnline);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSResourceTemplate setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSResourceTemplateGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSResourceTemplate)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceTemplateGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceTemplateGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSResourceTemplate setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSResourceTemplateGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSResourceTemplate)this;
	}
	public SDMSResourceTemplate set_NrIdSeId (SystemEnvironment env, Long p_nrId, Long p_seId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSResourceTemplateGeneric)(object)).set_NrIdSeId (env, p_nrId, p_seId);
		return (SDMSResourceTemplate)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing ResourceTemplate $1", getId(sysEnv));
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
		((SDMSResourceTemplateGeneric) object).print();
	}
}
