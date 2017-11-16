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

public class SDMSResourceProxyGeneric extends SDMSProxy
{

	public static final int REASON_AVAILABLE = 0;
	public static final int REASON_LOCKMODE = 1;
	public static final int REASON_AMOUNT = 2;
	public static final int REASON_STATE = 4;
	public static final int REASON_EXPIRE = 8;
	public static final int REASON_OFFLINE = 16;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.RESOURCE|SDMSPrivilege.GRANT;

	static final public int tag_size = 64;

	protected SDMSResourceProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getNrId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getNrId (env));
	}

	public void setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setNrId (env, p_nrId);
		return ;
	}
	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getScopeId (env));
	}

	public void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setScopeId (env, p_scopeId);
		return ;
	}
	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getMasterId (env));
	}

	public void setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setMasterId (env, p_masterId);
		return ;
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getLinkId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getLinkId (env));
	}

	public void setLinkId (SystemEnvironment env, Long p_linkId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setLinkId (env, p_linkId);
		return ;
	}
	public Long getManagerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getManagerId (env));
	}

	public void setManagerId (SystemEnvironment env, Long p_managerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setManagerId (env, p_managerId);
		return ;
	}
	public String getTag (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getTag (env));
	}

	public void setTag (SystemEnvironment env, String p_tag)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setTag (env, p_tag);
		return ;
	}
	public Long getRsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getRsdId (env));
	}

	public void setRsdId (SystemEnvironment env, Long p_rsdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setRsdId (env, p_rsdId);
		return ;
	}
	public Long getRsdTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getRsdTime (env));
	}

	public void setRsdTime (SystemEnvironment env, Long p_rsdTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setRsdTime (env, p_rsdTime);
		return ;
	}
	public Integer getDefinedAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getDefinedAmount (env));
	}

	public void setDefinedAmount (SystemEnvironment env, Integer p_definedAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setDefinedAmount (env, p_definedAmount);
		return ;
	}
	public Integer getRequestableAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getRequestableAmount (env));
	}

	public void setRequestableAmount (SystemEnvironment env, Integer p_requestableAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setRequestableAmount (env, p_requestableAmount);
		return ;
	}
	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getAmount (env));
	}

	public void setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setAmount (env, p_amount);
		return ;
	}
	public Integer getFreeAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getFreeAmount (env));
	}

	public void setFreeAmount (SystemEnvironment env, Integer p_freeAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setFreeAmount (env, p_freeAmount);
		return ;
	}
	public Boolean getIsOnline (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getIsOnline (env));
	}

	public void setIsOnline (SystemEnvironment env, Boolean p_isOnline)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setIsOnline (env, p_isOnline);
		return ;
	}
	public Float getFactor (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getFactor (env));
	}

	public void setFactor (SystemEnvironment env, Float p_factor)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setFactor (env, p_factor);
		return ;
	}
	public Integer getTraceInterval (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getTraceInterval (env));
	}

	public void setTraceInterval (SystemEnvironment env, Integer p_traceInterval)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setTraceInterval (env, p_traceInterval);
		return ;
	}
	public Integer getTraceBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getTraceBase (env));
	}

	public void setTraceBase (SystemEnvironment env, Integer p_traceBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setTraceBase (env, p_traceBase);
		return ;
	}
	public Integer getTraceBaseMultiplier (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getTraceBaseMultiplier (env));
	}

	public void setTraceBaseMultiplier (SystemEnvironment env, Integer p_traceBaseMultiplier)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setTraceBaseMultiplier (env, p_traceBaseMultiplier);
		return ;
	}
	public Float getTd0Avg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getTd0Avg (env));
	}

	public void setTd0Avg (SystemEnvironment env, Float p_td0Avg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setTd0Avg (env, p_td0Avg);
		return ;
	}
	public Float getTd1Avg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getTd1Avg (env));
	}

	public void setTd1Avg (SystemEnvironment env, Float p_td1Avg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setTd1Avg (env, p_td1Avg);
		return ;
	}
	public Float getTd2Avg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getTd2Avg (env));
	}

	public void setTd2Avg (SystemEnvironment env, Float p_td2Avg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setTd2Avg (env, p_td2Avg);
		return ;
	}
	public Float getLwAvg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getLwAvg (env));
	}

	public void setLwAvg (SystemEnvironment env, Float p_lwAvg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setLwAvg (env, p_lwAvg);
		return ;
	}
	public Long getLastEval (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getLastEval (env));
	}

	public void setLastEval (SystemEnvironment env, Long p_lastEval)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setLastEval (env, p_lastEval);
		return ;
	}
	public Long getLastWrite (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getLastWrite (env));
	}

	public void setLastWrite (SystemEnvironment env, Long p_lastWrite)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setLastWrite (env, p_lastWrite);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSResource setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSResourceGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSResource)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSResourceGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSResource setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSResourceGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSResource)this;
	}
	public SDMSResource set_NrIdScopeId (SystemEnvironment env, Long p_nrId, Long p_scopeId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSResourceGeneric)(object)).set_NrIdScopeId (env, p_nrId, p_scopeId);
		return (SDMSResource)this;
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
		Long nrId = getNrId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSNamedResourceTable.getObject(sysEnv, nrId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		gotIt = false;
		Long scopeId = getScopeId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSScopeTable.getObject(sysEnv, scopeId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}
		if (!gotIt)
			try {
				s.add(SDMSFolderTable.getObject(sysEnv, scopeId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		gotIt = false;
		Long masterId = getMasterId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSSubmittedEntityTable.getObject(sysEnv, masterId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		sysEnv.tx.sortKeyMap.put(myId, s);
		return s;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Resource $1", getId(sysEnv));
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
		((SDMSResourceGeneric) object).print();
	}
}
