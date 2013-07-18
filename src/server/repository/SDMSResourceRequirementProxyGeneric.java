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

public class SDMSResourceRequirementProxyGeneric extends SDMSProxy
{

	public final static String __version = "SDMSResourceRequirementProxyGeneric $Revision: 2.5 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public static final int N = Lockmode.N;
	public static final int X = Lockmode.X;
	public static final int SX = Lockmode.SX;
	public static final int S = Lockmode.S;
	public static final int SC = Lockmode.SC;
	public static final int NOKEEP = 0;
	public static final int KEEP = 1;
	public static final int KEEP_FINAL = 2;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int condition_size = 1024;

	private static SDMSTable masterTables[] = null;

	protected SDMSResourceRequirementProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getNrId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getNrId (env));
	}

	public void setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setNrId (env, p_nrId);
		return ;
	}
	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getAmount (env));
	}

	public void setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setAmount (env, p_amount);
		return ;
	}
	public Integer getKeepMode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getKeepMode (env));
	}

	public String getKeepModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSResourceRequirementGeneric) object).getKeepModeAsString (env);
	}

	public void setKeepMode (SystemEnvironment env, Integer p_keepMode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setKeepMode (env, p_keepMode);
		return ;
	}
	public Boolean getIsSticky (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getIsSticky (env));
	}

	public void setIsSticky (SystemEnvironment env, Boolean p_isSticky)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setIsSticky (env, p_isSticky);
		return ;
	}
	public Long getRsmpId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getRsmpId (env));
	}

	public void setRsmpId (SystemEnvironment env, Long p_rsmpId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setRsmpId (env, p_rsmpId);
		return ;
	}
	public Integer getExpiredAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getExpiredAmount (env));
	}

	public void setExpiredAmount (SystemEnvironment env, Integer p_expiredAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setExpiredAmount (env, p_expiredAmount);
		return ;
	}
	public Integer getExpiredBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getExpiredBase (env));
	}

	public String getExpiredBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSResourceRequirementGeneric) object).getExpiredBaseAsString (env);
	}

	public void setExpiredBase (SystemEnvironment env, Integer p_expiredBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setExpiredBase (env, p_expiredBase);
		return ;
	}
	public Integer getLockmode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getLockmode (env));
	}

	public String getLockmodeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSResourceRequirementGeneric) object).getLockmodeAsString (env);
	}

	public void setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setLockmode (env, p_lockmode);
		return ;
	}
	public String getCondition (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getCondition (env));
	}

	public void setCondition (SystemEnvironment env, String p_condition)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setCondition (env, p_condition);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSResourceRequirement setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSResourceRequirement)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSResourceRequirementGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSResourceRequirement setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSResourceRequirement)this;
	}
	public SDMSResourceRequirement set_SeIdNrId (SystemEnvironment env, Long p_seId, Long p_nrId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSResourceRequirementGeneric)(object)).set_SeIdNrId (env, p_seId, p_nrId);
		return (SDMSResourceRequirement)this;
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
		p = checkPrivs;
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSeId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFootprintTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSeId(env));
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

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
		p = checkPrivs;
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {

			SDMSProxy o = t.get(env, getSeId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFootprintTable.tableName);
		try {

			SDMSProxy o = t.get(env, getSeId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, fastFail, checkGroups);
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing ResourceRequirement $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
		t = SystemEnvironment.repository.getTable(env, SDMSFootprintTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {

		}
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.euid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSResourceRequirementGeneric) object).print();
	}
}
