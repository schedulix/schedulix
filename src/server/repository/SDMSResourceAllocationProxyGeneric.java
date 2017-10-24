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

public class SDMSResourceAllocationProxyGeneric extends SDMSProxy
{

	public static final int N = Lockmode.N;
	public static final int X = Lockmode.X;
	public static final int SX = Lockmode.SX;
	public static final int S = Lockmode.S;
	public static final int SC = Lockmode.SC;
	public static final int NOKEEP = SDMSResourceRequirement.NOKEEP;
	public static final int KEEP = SDMSResourceRequirement.KEEP;
	public static final int KEEP_FINAL = SDMSResourceRequirement.KEEP_FINAL;
	public static final int REQUEST = 1;
	public static final int RESERVATION = 2;
	public static final int MASTER_RESERVATION = 3;
	public static final int ALLOCATION = 4;
	public static final int IGNORE = 5;
	public static final int MASTER_REQUEST = 6;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int stickyName_size = 64;

	private static SDMSTable masterTables[] = null;

	protected SDMSResourceAllocationProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSResourceAllocation getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSResourceAllocationTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSResourceAllocation (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSResourceAllocation)p;
	}

	public Long getRId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getRId (env));
	}

	public void setRId (SystemEnvironment env, Long p_rId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setRId (env, p_rId);
		return ;
	}
	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public Long getNrId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getNrId (env));
	}

	public void setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setNrId (env, p_nrId);
		return ;
	}
	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getAmount (env));
	}

	public void setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setAmount (env, p_amount);
		return ;
	}
	public Integer getOrigAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getOrigAmount (env));
	}

	public void setOrigAmount (SystemEnvironment env, Integer p_origAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setOrigAmount (env, p_origAmount);
		return ;
	}
	public Integer getKeepMode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getKeepMode (env));
	}

	public String getKeepModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSResourceAllocationGeneric) object).getKeepModeAsString (env);
	}

	public void setKeepMode (SystemEnvironment env, Integer p_keepMode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setKeepMode (env, p_keepMode);
		return ;
	}
	public Boolean getIsSticky (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getIsSticky (env));
	}

	public void setIsSticky (SystemEnvironment env, Boolean p_isSticky)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setIsSticky (env, p_isSticky);
		return ;
	}
	public String getStickyName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getStickyName (env));
	}

	public void setStickyName (SystemEnvironment env, String p_stickyName)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setStickyName (env, p_stickyName);
		return ;
	}
	public static int getStickyNameMaxLength ()
	{
		return (64);
	}
	public Long getStickyParent (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getStickyParent (env));
	}

	public void setStickyParent (SystemEnvironment env, Long p_stickyParent)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setStickyParent (env, p_stickyParent);
		return ;
	}
	public Integer getAllocationType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getAllocationType (env));
	}

	public String getAllocationTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSResourceAllocationGeneric) object).getAllocationTypeAsString (env);
	}

	public void setAllocationType (SystemEnvironment env, Integer p_allocationType)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setAllocationType (env, p_allocationType);
		return ;
	}
	public Long getRsmpId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getRsmpId (env));
	}

	public void setRsmpId (SystemEnvironment env, Long p_rsmpId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setRsmpId (env, p_rsmpId);
		return ;
	}
	public Integer getLockmode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getLockmode (env));
	}

	public String getLockmodeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSResourceAllocationGeneric) object).getLockmodeAsString (env);
	}

	public void setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setLockmode (env, p_lockmode);
		return ;
	}
	public Integer getRefcount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getRefcount (env));
	}

	public void setRefcount (SystemEnvironment env, Integer p_refcount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setRefcount (env, p_refcount);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSResourceAllocation setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSResourceAllocation)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceAllocationGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSResourceAllocation setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSResourceAllocation)this;
	}
	public SDMSResourceAllocation set_SmeIdRIdStickyName (SystemEnvironment env, Long p_smeId, Long p_rId, String p_stickyName)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).set_SmeIdRIdStickyName (env, p_smeId, p_rId, p_stickyName);
		return (SDMSResourceAllocation)this;
	}

	public SDMSResourceAllocation set_StickyParentRIdStickyName (SystemEnvironment env, Long p_stickyParent, Long p_rId, String p_stickyName)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).set_StickyParentRIdStickyName (env, p_stickyParent, p_rId, p_stickyName);
		return (SDMSResourceAllocation)this;
	}

	public SDMSResourceAllocation set_SmeIdNrId (SystemEnvironment env, Long p_smeId, Long p_nrId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSResourceAllocationGeneric)(object)).set_SmeIdNrId (env, p_smeId, p_nrId);
		return (SDMSResourceAllocation)this;
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
		Long rId = getRId(sysEnv);
		if (!gotIt)
			try {
				s.add(SDMSResourceTable.getObject(sysEnv, rId).getSortKey(sysEnv));
				gotIt = true;
			} catch (NotFoundException nfe) {
			}

		s.add(getSmeId(sysEnv));

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

		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSmeId(env));
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing ResourceAllocation $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.uid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSResourceAllocationGeneric) object).print();
	}
}
