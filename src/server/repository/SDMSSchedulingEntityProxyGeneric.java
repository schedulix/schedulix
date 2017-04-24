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

public class SDMSSchedulingEntityProxyGeneric extends SDMSProxy
{

	public static final int JOB = 1;
	public static final int BATCH = 2;
	public static final int MILESTONE = 3;
	public static final int AND = 1;
	public static final int OR = 2;
	public static final boolean SUSPEND = true;
	public static final boolean NOSUSPEND = false;
	public static final boolean MASTER = true;
	public static final boolean NOMASTER = false;
	public static final boolean SAME_NODE = true;
	public static final boolean NOSAME_NODE = false;
	public static final boolean GANG = true;
	public static final boolean NOGANG = false;
	public static final boolean TRUNC = true;
	public static final boolean NOTRUNC = false;
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.CREATE_PARENT_CONTENT|SDMSPrivilege.SUBMIT|SDMSPrivilege.MONITOR|SDMSPrivilege.OPERATE|SDMSPrivilege.RESOURCE|SDMSPrivilege.GRANT;

	static final public int name_size = 64;
	static final public int runProgram_size = 512;
	static final public int rerunProgram_size = 512;
	static final public int killProgram_size = 512;
	static final public int workdir_size = 512;
	static final public int logfile_size = 512;
	static final public int errlogfile_size = 512;
	static final public int getExpectedRuntime_size = 32;
	static final public int resumeAt_size = 20;

	protected SDMSSchedulingEntityProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSSchedulingEntity getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSSchedulingEntityTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSSchedulingEntity (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSSchedulingEntity)p;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setName (env, p_name);
		return ;
	}
	public static int getNameMaxLength ()
	{
		return (64);
	}
	public Long getFolderId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getFolderId (env));
	}

	public void setFolderId (SystemEnvironment env, Long p_folderId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setFolderId (env, p_folderId);
		return ;
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getType (env));
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getTypeAsString (env);
	}

	public void setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setType (env, p_type);
		return ;
	}
	public String getRunProgram (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getRunProgram (env));
	}

	public void setRunProgram (SystemEnvironment env, String p_runProgram)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setRunProgram (env, p_runProgram);
		return ;
	}
	public static int getRunProgramMaxLength ()
	{
		return (512);
	}
	public String getRerunProgram (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getRerunProgram (env));
	}

	public void setRerunProgram (SystemEnvironment env, String p_rerunProgram)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setRerunProgram (env, p_rerunProgram);
		return ;
	}
	public static int getRerunProgramMaxLength ()
	{
		return (512);
	}
	public String getKillProgram (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getKillProgram (env));
	}

	public void setKillProgram (SystemEnvironment env, String p_killProgram)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setKillProgram (env, p_killProgram);
		return ;
	}
	public static int getKillProgramMaxLength ()
	{
		return (512);
	}
	public String getWorkdir (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getWorkdir (env));
	}

	public void setWorkdir (SystemEnvironment env, String p_workdir)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setWorkdir (env, p_workdir);
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
		return (((SDMSSchedulingEntityGeneric)(object)).getLogfile (env));
	}

	public void setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setLogfile (env, p_logfile);
		return ;
	}
	public static int getLogfileMaxLength ()
	{
		return (512);
	}
	public Boolean getTruncLog (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getTruncLog (env));
	}

	public String getTruncLogAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getTruncLogAsString (env);
	}

	public void setTruncLog (SystemEnvironment env, Boolean p_truncLog)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setTruncLog (env, p_truncLog);
		return ;
	}
	public String getErrlogfile (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getErrlogfile (env));
	}

	public void setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setErrlogfile (env, p_errlogfile);
		return ;
	}
	public static int getErrlogfileMaxLength ()
	{
		return (512);
	}
	public Boolean getTruncErrlog (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getTruncErrlog (env));
	}

	public String getTruncErrlogAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getTruncErrlogAsString (env);
	}

	public void setTruncErrlog (SystemEnvironment env, Boolean p_truncErrlog)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setTruncErrlog (env, p_truncErrlog);
		return ;
	}
	public Integer getExpectedRuntime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getExpectedRuntime (env));
	}

	public void setExpectedRuntime (SystemEnvironment env, Integer p_expectedRuntime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setExpectedRuntime (env, p_expectedRuntime);
		return ;
	}
	public Integer getExpectedFinaltime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getExpectedFinaltime (env));
	}

	public void setExpectedFinaltime (SystemEnvironment env, Integer p_expectedFinaltime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setExpectedFinaltime (env, p_expectedFinaltime);
		return ;
	}
	public String getGetExpectedRuntime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getGetExpectedRuntime (env));
	}

	public void setGetExpectedRuntime (SystemEnvironment env, String p_getExpectedRuntime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setGetExpectedRuntime (env, p_getExpectedRuntime);
		return ;
	}
	public static int getGetExpectedRuntimeMaxLength ()
	{
		return (32);
	}
	public Integer getPriority (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getPriority (env));
	}

	public void setPriority (SystemEnvironment env, Integer p_priority)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setPriority (env, p_priority);
		return ;
	}
	public Integer getMinPriority (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getMinPriority (env));
	}

	public void setMinPriority (SystemEnvironment env, Integer p_minPriority)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setMinPriority (env, p_minPriority);
		return ;
	}
	public Integer getAgingAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getAgingAmount (env));
	}

	public void setAgingAmount (SystemEnvironment env, Integer p_agingAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setAgingAmount (env, p_agingAmount);
		return ;
	}
	public Integer getAgingBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getAgingBase (env));
	}

	public String getAgingBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getAgingBaseAsString (env);
	}

	public void setAgingBase (SystemEnvironment env, Integer p_agingBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setAgingBase (env, p_agingBase);
		return ;
	}
	public Boolean getSubmitSuspended (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getSubmitSuspended (env));
	}

	public String getSubmitSuspendedAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getSubmitSuspendedAsString (env);
	}

	public void setSubmitSuspended (SystemEnvironment env, Boolean p_submitSuspended)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setSubmitSuspended (env, p_submitSuspended);
		return ;
	}
	public String getResumeAt (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getResumeAt (env));
	}

	public void setResumeAt (SystemEnvironment env, String p_resumeAt)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setResumeAt (env, p_resumeAt);
		return ;
	}
	public static int getResumeAtMaxLength ()
	{
		return (20);
	}
	public Integer getResumeIn (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getResumeIn (env));
	}

	public void setResumeIn (SystemEnvironment env, Integer p_resumeIn)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setResumeIn (env, p_resumeIn);
		return ;
	}
	public Integer getResumeBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getResumeBase (env));
	}

	public String getResumeBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getResumeBaseAsString (env);
	}

	public void setResumeBase (SystemEnvironment env, Integer p_resumeBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setResumeBase (env, p_resumeBase);
		return ;
	}
	public Boolean getMasterSubmittable (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getMasterSubmittable (env));
	}

	public String getMasterSubmittableAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getMasterSubmittableAsString (env);
	}

	public void setMasterSubmittable (SystemEnvironment env, Boolean p_masterSubmittable)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setMasterSubmittable (env, p_masterSubmittable);
		return ;
	}
	public Integer getTimeoutAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getTimeoutAmount (env));
	}

	public void setTimeoutAmount (SystemEnvironment env, Integer p_timeoutAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setTimeoutAmount (env, p_timeoutAmount);
		return ;
	}
	public Integer getTimeoutBase (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getTimeoutBase (env));
	}

	public String getTimeoutBaseAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getTimeoutBaseAsString (env);
	}

	public void setTimeoutBase (SystemEnvironment env, Integer p_timeoutBase)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setTimeoutBase (env, p_timeoutBase);
		return ;
	}
	public Long getTimeoutStateId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getTimeoutStateId (env));
	}

	public void setTimeoutStateId (SystemEnvironment env, Long p_timeoutStateId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setTimeoutStateId (env, p_timeoutStateId);
		return ;
	}
	public Boolean getSameNode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getSameNode (env));
	}

	public String getSameNodeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getSameNodeAsString (env);
	}

	public void setSameNode (SystemEnvironment env, Boolean p_sameNode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setSameNode (env, p_sameNode);
		return ;
	}
	public Boolean getGangSchedule (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getGangSchedule (env));
	}

	public String getGangScheduleAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getGangScheduleAsString (env);
	}

	public void setGangSchedule (SystemEnvironment env, Boolean p_gangSchedule)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setGangSchedule (env, p_gangSchedule);
		return ;
	}
	public Integer getDependencyOperation (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getDependencyOperation (env));
	}

	public String getDependencyOperationAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSchedulingEntityGeneric) object).getDependencyOperationAsString (env);
	}

	public void setDependencyOperation (SystemEnvironment env, Integer p_dependencyOperation)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setDependencyOperation (env, p_dependencyOperation);
		return ;
	}
	public Long getEsmpId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getEsmpId (env));
	}

	public void setEsmpId (SystemEnvironment env, Long p_esmpId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setEsmpId (env, p_esmpId);
		return ;
	}
	public Long getEspId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getEspId (env));
	}

	public void setEspId (SystemEnvironment env, Long p_espId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setEspId (env, p_espId);
		return ;
	}
	public Long getQaId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getQaId (env));
	}

	public void setQaId (SystemEnvironment env, Long p_qaId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setQaId (env, p_qaId);
		return ;
	}
	public Long getNeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getNeId (env));
	}

	public void setNeId (SystemEnvironment env, Long p_neId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setNeId (env, p_neId);
		return ;
	}
	public Long getFpId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getFpId (env));
	}

	public void setFpId (SystemEnvironment env, Long p_fpId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setFpId (env, p_fpId);
		return ;
	}
	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getInheritPrivs (env));
	}

	public void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT|SDMSPrivilege.GRANT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setInheritPrivs (env, p_inheritPrivs);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSSchedulingEntity setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSchedulingEntityGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSSchedulingEntity)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSchedulingEntityGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSchedulingEntityGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSSchedulingEntity setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSchedulingEntityGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSSchedulingEntity)this;
	}
	public SDMSSchedulingEntity set_FolderIdName (SystemEnvironment env, Long p_folderId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSchedulingEntityGeneric)(object)).set_FolderIdName (env, p_folderId, p_name);
		return (SDMSSchedulingEntity)this;
	}

	public SDMSSchedulingEntity set_FolderIdMasterSubmittable (SystemEnvironment env, Long p_folderId, Boolean p_masterSubmittable)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSchedulingEntityGeneric)(object)).set_FolderIdMasterSubmittable (env, p_folderId, p_masterSubmittable);
		return (SDMSSchedulingEntity)this;
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
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Scheduling Entity $1", ((SDMSSchedulingEntity) this).pathString(sysEnv));
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
		((SDMSSchedulingEntityGeneric) object).print();
	}
}
