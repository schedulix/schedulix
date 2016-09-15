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

public class SDMSKillJobProxyGeneric extends SDMSProxy
{

	public static final int RUNNABLE = SDMSSubmittedEntity.RUNNABLE;
	public static final int STARTING = SDMSSubmittedEntity.STARTING;
	public static final int STARTED = SDMSSubmittedEntity.STARTED;
	public static final int RUNNING = SDMSSubmittedEntity.RUNNING;
	public static final int FINISHED = SDMSSubmittedEntity.FINISHED;
	public static final int BROKEN_ACTIVE = SDMSSubmittedEntity.BROKEN_ACTIVE;
	public static final int BROKEN_FINISHED = SDMSSubmittedEntity.BROKEN_FINISHED;
	public static final int ERROR = SDMSSubmittedEntity.ERROR;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int commandline_size = 512;
	static final public int logfile_size = 512;
	static final public int errlogfile_size = 512;
	static final public int pid_size = 32;
	static final public int extPid_size = 32;
	static final public int errorMsg_size = 256;

	private static SDMSTable masterTables[] = null;

	protected SDMSKillJobProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSKillJob getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSKillJobTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSKillJob (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSKillJob)p;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public Long getSeVersion (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getSeVersion (env));
	}

	public void setSeVersion (SystemEnvironment env, Long p_seVersion)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setSeVersion (env, p_seVersion);
		return ;
	}
	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getSmeId (env));
	}

	public void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setSmeId (env, p_smeId);
		return ;
	}
	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getScopeId (env));
	}

	public void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setScopeId (env, p_scopeId);
		return ;
	}
	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getState (env));
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSKillJobGeneric) object).getStateAsString (env);
	}

	public void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setState (env, p_state);
		return ;
	}
	public Integer getExitCode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getExitCode (env));
	}

	public void setExitCode (SystemEnvironment env, Integer p_exitCode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setExitCode (env, p_exitCode);
		return ;
	}
	public String getCommandline (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getCommandline (env));
	}

	public void setCommandline (SystemEnvironment env, String p_commandline)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setCommandline (env, p_commandline);
		return ;
	}
	public String getLogfile (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getLogfile (env));
	}

	public void setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setLogfile (env, p_logfile);
		return ;
	}
	public String getErrlogfile (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getErrlogfile (env));
	}

	public void setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setErrlogfile (env, p_errlogfile);
		return ;
	}
	public String getPid (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getPid (env));
	}

	public void setPid (SystemEnvironment env, String p_pid)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setPid (env, p_pid);
		return ;
	}
	public String getExtPid (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getExtPid (env));
	}

	public void setExtPid (SystemEnvironment env, String p_extPid)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setExtPid (env, p_extPid);
		return ;
	}
	public String getErrorMsg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getErrorMsg (env));
	}

	public void setErrorMsg (SystemEnvironment env, String p_errorMsg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setErrorMsg (env, p_errorMsg);
		return ;
	}
	public Long getRunnableTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getRunnableTs (env));
	}

	public void setRunnableTs (SystemEnvironment env, Long p_runnableTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setRunnableTs (env, p_runnableTs);
		return ;
	}
	public Long getStartTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getStartTs (env));
	}

	public void setStartTs (SystemEnvironment env, Long p_startTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setStartTs (env, p_startTs);
		return ;
	}
	public Long getFinishTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getFinishTs (env));
	}

	public void setFinishTs (SystemEnvironment env, Long p_finishTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setFinishTs (env, p_finishTs);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSKillJob setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSKillJob)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSKillJobGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSKillJob setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSKillJobGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSKillJob)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing KillJob $1", getId(sysEnv));
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
		setChangerUIdNoCheck (env, env.cEnv.euid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSKillJobGeneric) object).print();
	}
}
