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

public class SDMSSubmittedEntityStatsProxyGeneric extends SDMSProxy
{

	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int commandline_size = 512;
	static final public int workdir_size = 512;
	static final public int logfile_size = 512;
	static final public int errlogfile_size = 512;
	static final public int extPid_size = 32;

	private static SDMSTable masterTables[] = null;

	protected SDMSSubmittedEntityStatsProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSSubmittedEntityStats getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSSubmittedEntityStatsTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSSubmittedEntityStats (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSSubmittedEntityStats)p;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public Integer getRerunSeq (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getRerunSeq (env));
	}

	public void setRerunSeq (SystemEnvironment env, Integer p_rerunSeq)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setRerunSeq (env, p_rerunSeq);
		return ;
	}
	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getScopeId (env));
	}

	public void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setScopeId (env, p_scopeId);
		return ;
	}
	public Long getJobEsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getJobEsdId (env));
	}

	public void setJobEsdId (SystemEnvironment env, Long p_jobEsdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setJobEsdId (env, p_jobEsdId);
		return ;
	}
	public Integer getExitCode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getExitCode (env));
	}

	public void setExitCode (SystemEnvironment env, Integer p_exitCode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setExitCode (env, p_exitCode);
		return ;
	}
	public String getCommandline (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getCommandline (env));
	}

	public void setCommandline (SystemEnvironment env, String p_commandline)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setCommandline (env, p_commandline);
		return ;
	}
	public static int getCommandlineMaxLength ()
	{
		return (512);
	}
	public String getWorkdir (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getWorkdir (env));
	}

	public void setWorkdir (SystemEnvironment env, String p_workdir)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setWorkdir (env, p_workdir);
		return ;
	}
	public static int getWorkdirMaxLength ()
	{
		return (512);
	}
	public String getLogfile (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getLogfile (env));
	}

	public void setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setLogfile (env, p_logfile);
		return ;
	}
	public static int getLogfileMaxLength ()
	{
		return (512);
	}
	public String getErrlogfile (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getErrlogfile (env));
	}

	public void setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setErrlogfile (env, p_errlogfile);
		return ;
	}
	public static int getErrlogfileMaxLength ()
	{
		return (512);
	}
	public String getExtPid (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getExtPid (env));
	}

	public void setExtPid (SystemEnvironment env, String p_extPid)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setExtPid (env, p_extPid);
		return ;
	}
	public static int getExtPidMaxLength ()
	{
		return (32);
	}
	public Long getSyncTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getSyncTs (env));
	}

	public void setSyncTs (SystemEnvironment env, Long p_syncTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setSyncTs (env, p_syncTs);
		return ;
	}
	public Long getResourceTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getResourceTs (env));
	}

	public void setResourceTs (SystemEnvironment env, Long p_resourceTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setResourceTs (env, p_resourceTs);
		return ;
	}
	public Long getRunnableTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getRunnableTs (env));
	}

	public void setRunnableTs (SystemEnvironment env, Long p_runnableTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setRunnableTs (env, p_runnableTs);
		return ;
	}
	public Long getStartTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getStartTs (env));
	}

	public void setStartTs (SystemEnvironment env, Long p_startTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setStartTs (env, p_startTs);
		return ;
	}
	public Long getFinishTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getFinishTs (env));
	}

	public void setFinishTs (SystemEnvironment env, Long p_finishTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setFinishTs (env, p_finishTs);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSSubmittedEntityStats setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSSubmittedEntityStats)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityStatsGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSSubmittedEntityStats setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSSubmittedEntityStats)this;
	}
	public SDMSSubmittedEntityStats set_SmeIdRerunSeq (SystemEnvironment env, Long p_smeId, Integer p_rerunSeq)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSSubmittedEntityStatsGeneric)(object)).set_SmeIdRerunSeq (env, p_smeId, p_rerunSeq);
		return (SDMSSubmittedEntityStats)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Submitted Entity Statistics $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSubmittedEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSmeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.uid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSSubmittedEntityStatsGeneric) object).print();
	}
}
