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

public class SDMSSubmittedEntityProxyGeneric extends SDMSProxy
{

	public static final int SUBMITTED = 0;
	public static final int DEPENDENCY_WAIT = 1;
	public static final int SYNCHRONIZE_WAIT = 2;
	public static final int RESOURCE_WAIT = 3;
	public static final int RUNNABLE = 4;
	public static final int STARTING = 5;
	public static final int STARTED = 6;
	public static final int RUNNING = 7;
	public static final int TO_KILL = 8;
	public static final int KILLED = 9;
	public static final int CANCELLED = 10;
	public static final int FINISHED = 11;
	public static final int FINAL = 12;
	public static final int BROKEN_ACTIVE = 13;
	public static final int BROKEN_FINISHED = 14;
	public static final int ERROR = 15;
	public static final int UNREACHABLE = 16;
	public static final int SUSPEND = 1;
	public static final int NOSUSPEND = 0;
	public static final int ADMINSUSPEND = 2;
	public static final int UH_IGNORE = SDMSDependencyDefinition.IGNORE;
	public static final int UH_ERROR = SDMSDependencyDefinition.ERROR;
	public static final int UH_SUSPEND = SDMSDependencyDefinition.SUSPEND;
	public static final int MERGE_LOCAL = SDMSSchedulingHierarchy.MERGE_LOCAL;
	public static final int MERGE_GLOBAL = SDMSSchedulingHierarchy.MERGE_GLOBAL;
	public static final int NOMERGE = SDMSSchedulingHierarchy.NOMERGE;
	public static final int FAILURE = SDMSSchedulingHierarchy.FAILURE;
	public static final char PID_SEP = '@';
	public static final int MINUTE = SDMSInterval.MINUTE;
	public static final int HOUR = SDMSInterval.HOUR;
	public static final int DAY = SDMSInterval.DAY;
	public static final int WEEK = SDMSInterval.WEEK;
	public static final int MONTH = SDMSInterval.MONTH;
	public static final int YEAR = SDMSInterval.YEAR;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP|SDMSPrivilege.MONITOR|SDMSPrivilege.OPERATE|SDMSPrivilege.GRANT|SDMSPrivilege.RESOURCE;

	static final public int submitTag_size = 32;
	static final public int childTag_size = 70;
	static final public int commandline_size = 512;
	static final public int rrCommandline_size = 512;
	static final public int workdir_size = 512;
	static final public int logfile_size = 512;
	static final public int errlogfile_size = 512;
	static final public int pid_size = 32;
	static final public int extPid_size = 32;
	static final public int errorMsg_size = 256;

	protected SDMSSubmittedEntityProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSSubmittedEntity getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSSubmittedEntityTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSSubmittedEntity (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSSubmittedEntity)p;
	}

	public Long getAccessKey (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getAccessKey (env));
	}

	public void setAccessKey (SystemEnvironment env, Long p_accessKey)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setAccessKey (env, p_accessKey);
		return ;
	}
	public Long getMasterId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getMasterId (env));
	}

	public void setMasterId (SystemEnvironment env, Long p_masterId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setMasterId (env, p_masterId);
		return ;
	}
	public String getSubmitTag (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getSubmitTag (env));
	}

	public void setSubmitTag (SystemEnvironment env, String p_submitTag)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setSubmitTag (env, p_submitTag);
		return ;
	}
	public static int getSubmitTagMaxLength ()
	{
		return (32);
	}
	public Integer getUnresolvedHandling (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getUnresolvedHandling (env));
	}

	public String getUnresolvedHandlingAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSubmittedEntityGeneric) object).getUnresolvedHandlingAsString (env);
	}

	public void setUnresolvedHandling (SystemEnvironment env, Integer p_unresolvedHandling)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setUnresolvedHandling (env, p_unresolvedHandling);
		return ;
	}
	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public String getChildTag (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getChildTag (env));
	}

	public void setChildTag (SystemEnvironment env, String p_childTag)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setChildTag (env, p_childTag);
		return ;
	}
	public static int getChildTagMaxLength ()
	{
		return (70);
	}
	public Long getSeVersion (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getSeVersion (env));
	}

	public void setSeVersion (SystemEnvironment env, Long p_seVersion)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setSeVersion (env, p_seVersion);
		return ;
	}
	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getOwnerId (env));
	}

	public void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setOwnerId (env, p_ownerId);
		return ;
	}
	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getParentId (env));
	}

	public void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setParentId (env, p_parentId);
		return ;
	}
	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getScopeId (env));
	}

	public void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setScopeId (env, p_scopeId);
		return ;
	}
	public Boolean getIsStatic (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIsStatic (env));
	}

	public void setIsStatic (SystemEnvironment env, Boolean p_isStatic)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIsStatic (env, p_isStatic);
		return ;
	}
	public Boolean getIsDisabled (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIsDisabled (env));
	}

	public void setIsDisabled (SystemEnvironment env, Boolean p_isDisabled)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIsDisabled (env, p_isDisabled);
		return ;
	}
	public Integer getOldState (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getOldState (env));
	}

	public void setOldState (SystemEnvironment env, Integer p_oldState)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setOldState (env, p_oldState);
		return ;
	}
	public Integer getMergeMode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getMergeMode (env));
	}

	public String getMergeModeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSubmittedEntityGeneric) object).getMergeModeAsString (env);
	}

	public void setMergeMode (SystemEnvironment env, Integer p_mergeMode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setMergeMode (env, p_mergeMode);
		return ;
	}
	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getState (env));
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSubmittedEntityGeneric) object).getStateAsString (env);
	}

	public void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setState (env, p_state);
		return ;
	}
	public Long getJobEsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getJobEsdId (env));
	}

	public void setJobEsdId (SystemEnvironment env, Long p_jobEsdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setJobEsdId (env, p_jobEsdId);
		return ;
	}
	public Integer getJobEsdPref (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getJobEsdPref (env));
	}

	public void setJobEsdPref (SystemEnvironment env, Integer p_jobEsdPref)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setJobEsdPref (env, p_jobEsdPref);
		return ;
	}
	public Boolean getJobIsFinal (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getJobIsFinal (env));
	}

	public void setJobIsFinal (SystemEnvironment env, Boolean p_jobIsFinal)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setJobIsFinal (env, p_jobIsFinal);
		return ;
	}
	public Boolean getJobIsRestartable (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getJobIsRestartable (env));
	}

	public void setJobIsRestartable (SystemEnvironment env, Boolean p_jobIsRestartable)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setJobIsRestartable (env, p_jobIsRestartable);
		return ;
	}
	public Long getFinalEsdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getFinalEsdId (env));
	}

	public void setFinalEsdId (SystemEnvironment env, Long p_finalEsdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setFinalEsdId (env, p_finalEsdId);
		return ;
	}
	public Integer getExitCode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getExitCode (env));
	}

	public void setExitCode (SystemEnvironment env, Integer p_exitCode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setExitCode (env, p_exitCode);
		return ;
	}
	public String getCommandline (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCommandline (env));
	}

	public void setCommandline (SystemEnvironment env, String p_commandline)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCommandline (env, p_commandline);
		return ;
	}
	public static int getCommandlineMaxLength ()
	{
		return (512);
	}
	public String getRrCommandline (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getRrCommandline (env));
	}

	public void setRrCommandline (SystemEnvironment env, String p_rrCommandline)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setRrCommandline (env, p_rrCommandline);
		return ;
	}
	public static int getRrCommandlineMaxLength ()
	{
		return (512);
	}
	public Integer getRerunSeq (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getRerunSeq (env));
	}

	public void setRerunSeq (SystemEnvironment env, Integer p_rerunSeq)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setRerunSeq (env, p_rerunSeq);
		return ;
	}
	public Boolean getIsReplaced (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIsReplaced (env));
	}

	public void setIsReplaced (SystemEnvironment env, Boolean p_isReplaced)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIsReplaced (env, p_isReplaced);
		return ;
	}
	public Boolean getIsCancelled (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIsCancelled (env));
	}

	public void setIsCancelled (SystemEnvironment env, Boolean p_isCancelled)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIsCancelled (env, p_isCancelled);
		return ;
	}
	public Long getBaseSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getBaseSmeId (env));
	}

	public void setBaseSmeId (SystemEnvironment env, Long p_baseSmeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setBaseSmeId (env, p_baseSmeId);
		return ;
	}
	public Long getReasonSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getReasonSmeId (env));
	}

	public void setReasonSmeId (SystemEnvironment env, Long p_reasonSmeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setReasonSmeId (env, p_reasonSmeId);
		return ;
	}
	public Long getFireSmeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getFireSmeId (env));
	}

	public void setFireSmeId (SystemEnvironment env, Long p_fireSmeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setFireSmeId (env, p_fireSmeId);
		return ;
	}
	public Long getFireSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getFireSeId (env));
	}

	public void setFireSeId (SystemEnvironment env, Long p_fireSeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setFireSeId (env, p_fireSeId);
		return ;
	}
	public Long getTrId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getTrId (env));
	}

	public void setTrId (SystemEnvironment env, Long p_trId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setTrId (env, p_trId);
		return ;
	}
	public Long getTrSdIdOld (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getTrSdIdOld (env));
	}

	public void setTrSdIdOld (SystemEnvironment env, Long p_trSdIdOld)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setTrSdIdOld (env, p_trSdIdOld);
		return ;
	}
	public Long getTrSdIdNew (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getTrSdIdNew (env));
	}

	public void setTrSdIdNew (SystemEnvironment env, Long p_trSdIdNew)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setTrSdIdNew (env, p_trSdIdNew);
		return ;
	}
	public Integer getTrSeq (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getTrSeq (env));
	}

	public void setTrSeq (SystemEnvironment env, Integer p_trSeq)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setTrSeq (env, p_trSeq);
		return ;
	}
	public String getWorkdir (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getWorkdir (env));
	}

	public void setWorkdir (SystemEnvironment env, String p_workdir)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setWorkdir (env, p_workdir);
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
		return (((SDMSSubmittedEntityGeneric)(object)).getLogfile (env));
	}

	public void setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setLogfile (env, p_logfile);
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
		return (((SDMSSubmittedEntityGeneric)(object)).getErrlogfile (env));
	}

	public void setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setErrlogfile (env, p_errlogfile);
		return ;
	}
	public static int getErrlogfileMaxLength ()
	{
		return (512);
	}
	public String getPid (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getPid (env));
	}

	public void setPid (SystemEnvironment env, String p_pid)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setPid (env, p_pid);
		return ;
	}
	public static int getPidMaxLength ()
	{
		return (32);
	}
	public String getExtPid (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getExtPid (env));
	}

	public void setExtPid (SystemEnvironment env, String p_extPid)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setExtPid (env, p_extPid);
		return ;
	}
	public static int getExtPidMaxLength ()
	{
		return (32);
	}
	public String getErrorMsg (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getErrorMsg (env));
	}

	public void setErrorMsg (SystemEnvironment env, String p_errorMsg)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setErrorMsg (env, p_errorMsg);
		return ;
	}
	public static int getErrorMsgMaxLength ()
	{
		return (256);
	}
	public Long getKillId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getKillId (env));
	}

	public void setKillId (SystemEnvironment env, Long p_killId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setKillId (env, p_killId);
		return ;
	}
	public Integer getKillExitCode (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getKillExitCode (env));
	}

	public void setKillExitCode (SystemEnvironment env, Integer p_killExitCode)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setKillExitCode (env, p_killExitCode);
		return ;
	}
	public Integer getIsSuspended (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIsSuspended (env));
	}

	public String getIsSuspendedAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSSubmittedEntityGeneric) object).getIsSuspendedAsString (env);
	}

	public void setIsSuspended (SystemEnvironment env, Integer p_isSuspended)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIsSuspended (env, p_isSuspended);
		return ;
	}
	public Boolean getIsSuspendedLocal (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIsSuspendedLocal (env));
	}

	public void setIsSuspendedLocal (SystemEnvironment env, Boolean p_isSuspendedLocal)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIsSuspendedLocal (env, p_isSuspendedLocal);
		return ;
	}
	public Integer getPriority (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getPriority (env));
	}

	public void setPriority (SystemEnvironment env, Integer p_priority)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setPriority (env, p_priority);
		return ;
	}
	public Integer getRawPriority (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getRawPriority (env));
	}

	public void setRawPriority (SystemEnvironment env, Integer p_rawPriority)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setRawPriority (env, p_rawPriority);
		return ;
	}
	public Integer getNice (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getNice (env));
	}

	public void setNice (SystemEnvironment env, Integer p_nice)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setNice (env, p_nice);
		return ;
	}
	public Integer getNpNice (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getNpNice (env));
	}

	public void setNpNice (SystemEnvironment env, Integer p_npNice)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setNpNice (env, p_npNice);
		return ;
	}
	public Integer getMinPriority (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getMinPriority (env));
	}

	public void setMinPriority (SystemEnvironment env, Integer p_minPriority)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setMinPriority (env, p_minPriority);
		return ;
	}
	public Integer getAgingAmount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getAgingAmount (env));
	}

	public void setAgingAmount (SystemEnvironment env, Integer p_agingAmount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setAgingAmount (env, p_agingAmount);
		return ;
	}
	public Integer getParentSuspended (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getParentSuspended (env));
	}

	public void setParentSuspended (SystemEnvironment env, Integer p_parentSuspended)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setParentSuspended (env, p_parentSuspended);
		return ;
	}
	public Integer getChildSuspended (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getChildSuspended (env));
	}

	public void setChildSuspended (SystemEnvironment env, Integer p_childSuspended)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setChildSuspended (env, p_childSuspended);
		return ;
	}
	public Integer getWarnCount (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getWarnCount (env));
	}

	public void setWarnCount (SystemEnvironment env, Integer p_warnCount)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setWarnCount (env, p_warnCount);
		return ;
	}
	public Long getWarnLink (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getWarnLink (env));
	}

	public void setWarnLink (SystemEnvironment env, Long p_warnLink)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setWarnLink (env, p_warnLink);
		return ;
	}
	public Long getSubmitTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getSubmitTs (env));
	}

	public void setSubmitTs (SystemEnvironment env, Long p_submitTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setSubmitTs (env, p_submitTs);
		return ;
	}
	public Long getResumeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getResumeTs (env));
	}

	public void setResumeTs (SystemEnvironment env, Long p_resumeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setResumeTs (env, p_resumeTs);
		return ;
	}
	public Long getSyncTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getSyncTs (env));
	}

	public void setSyncTs (SystemEnvironment env, Long p_syncTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setSyncTs (env, p_syncTs);
		return ;
	}
	public Long getResourceTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getResourceTs (env));
	}

	public void setResourceTs (SystemEnvironment env, Long p_resourceTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setResourceTs (env, p_resourceTs);
		return ;
	}
	public Long getRunnableTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getRunnableTs (env));
	}

	public void setRunnableTs (SystemEnvironment env, Long p_runnableTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setRunnableTs (env, p_runnableTs);
		return ;
	}
	public Long getStartTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getStartTs (env));
	}

	public void setStartTs (SystemEnvironment env, Long p_startTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setStartTs (env, p_startTs);
		return ;
	}
	public Long getFinishTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getFinishTs (env));
	}

	public void setFinishTs (SystemEnvironment env, Long p_finishTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setFinishTs (env, p_finishTs);
		return ;
	}
	public Long getFinalTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getFinalTs (env));
	}

	public void setFinalTs (SystemEnvironment env, Long p_finalTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setFinalTs (env, p_finalTs);
		return ;
	}
	public Integer getCntSubmitted (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntSubmitted (env));
	}

	public void setCntSubmitted (SystemEnvironment env, Integer p_cntSubmitted)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntSubmitted (env, p_cntSubmitted);
		return ;
	}
	public Integer getCntDependencyWait (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntDependencyWait (env));
	}

	public void setCntDependencyWait (SystemEnvironment env, Integer p_cntDependencyWait)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntDependencyWait (env, p_cntDependencyWait);
		return ;
	}
	public Integer getCntSynchronizeWait (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntSynchronizeWait (env));
	}

	public void setCntSynchronizeWait (SystemEnvironment env, Integer p_cntSynchronizeWait)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntSynchronizeWait (env, p_cntSynchronizeWait);
		return ;
	}
	public Integer getCntResourceWait (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntResourceWait (env));
	}

	public void setCntResourceWait (SystemEnvironment env, Integer p_cntResourceWait)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntResourceWait (env, p_cntResourceWait);
		return ;
	}
	public Integer getCntRunnable (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntRunnable (env));
	}

	public void setCntRunnable (SystemEnvironment env, Integer p_cntRunnable)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntRunnable (env, p_cntRunnable);
		return ;
	}
	public Integer getCntStarting (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntStarting (env));
	}

	public void setCntStarting (SystemEnvironment env, Integer p_cntStarting)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntStarting (env, p_cntStarting);
		return ;
	}
	public Integer getCntStarted (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntStarted (env));
	}

	public void setCntStarted (SystemEnvironment env, Integer p_cntStarted)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntStarted (env, p_cntStarted);
		return ;
	}
	public Integer getCntRunning (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntRunning (env));
	}

	public void setCntRunning (SystemEnvironment env, Integer p_cntRunning)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntRunning (env, p_cntRunning);
		return ;
	}
	public Integer getCntToKill (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntToKill (env));
	}

	public void setCntToKill (SystemEnvironment env, Integer p_cntToKill)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntToKill (env, p_cntToKill);
		return ;
	}
	public Integer getCntKilled (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntKilled (env));
	}

	public void setCntKilled (SystemEnvironment env, Integer p_cntKilled)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntKilled (env, p_cntKilled);
		return ;
	}
	public Integer getCntCancelled (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntCancelled (env));
	}

	public void setCntCancelled (SystemEnvironment env, Integer p_cntCancelled)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntCancelled (env, p_cntCancelled);
		return ;
	}
	public Integer getCntFinished (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntFinished (env));
	}

	public void setCntFinished (SystemEnvironment env, Integer p_cntFinished)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntFinished (env, p_cntFinished);
		return ;
	}
	public Integer getCntFinal (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntFinal (env));
	}

	public void setCntFinal (SystemEnvironment env, Integer p_cntFinal)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntFinal (env, p_cntFinal);
		return ;
	}
	public Integer getCntBrokenActive (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntBrokenActive (env));
	}

	public void setCntBrokenActive (SystemEnvironment env, Integer p_cntBrokenActive)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntBrokenActive (env, p_cntBrokenActive);
		return ;
	}
	public Integer getCntBrokenFinished (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntBrokenFinished (env));
	}

	public void setCntBrokenFinished (SystemEnvironment env, Integer p_cntBrokenFinished)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntBrokenFinished (env, p_cntBrokenFinished);
		return ;
	}
	public Integer getCntError (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntError (env));
	}

	public void setCntError (SystemEnvironment env, Integer p_cntError)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntError (env, p_cntError);
		return ;
	}
	public Integer getCntUnreachable (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntUnreachable (env));
	}

	public void setCntUnreachable (SystemEnvironment env, Integer p_cntUnreachable)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntUnreachable (env, p_cntUnreachable);
		return ;
	}
	public Integer getCntRestartable (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntRestartable (env));
	}

	public void setCntRestartable (SystemEnvironment env, Integer p_cntRestartable)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntRestartable (env, p_cntRestartable);
		return ;
	}
	public Integer getCntWarn (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntWarn (env));
	}

	public void setCntWarn (SystemEnvironment env, Integer p_cntWarn)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntWarn (env, p_cntWarn);
		return ;
	}
	public Integer getCntPending (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCntPending (env));
	}

	public void setCntPending (SystemEnvironment env, Integer p_cntPending)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCntPending (env, p_cntPending);
		return ;
	}
	public Integer getIdleTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIdleTs (env));
	}

	public void setIdleTs (SystemEnvironment env, Integer p_idleTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIdleTs (env, p_idleTs);
		return ;
	}
	public Integer getIdleTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getIdleTime (env));
	}

	public void setIdleTime (SystemEnvironment env, Integer p_idleTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setIdleTime (env, p_idleTime);
		return ;
	}
	public Integer getStatisticTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getStatisticTs (env));
	}

	public void setStatisticTs (SystemEnvironment env, Integer p_statisticTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setStatisticTs (env, p_statisticTs);
		return ;
	}
	public Integer getDependencyWaitTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getDependencyWaitTime (env));
	}

	public void setDependencyWaitTime (SystemEnvironment env, Integer p_dependencyWaitTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setDependencyWaitTime (env, p_dependencyWaitTime);
		return ;
	}
	public Integer getSuspendTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getSuspendTime (env));
	}

	public void setSuspendTime (SystemEnvironment env, Integer p_suspendTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setSuspendTime (env, p_suspendTime);
		return ;
	}
	public Integer getSyncTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getSyncTime (env));
	}

	public void setSyncTime (SystemEnvironment env, Integer p_syncTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setSyncTime (env, p_syncTime);
		return ;
	}
	public Integer getResourceTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getResourceTime (env));
	}

	public void setResourceTime (SystemEnvironment env, Integer p_resourceTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setResourceTime (env, p_resourceTime);
		return ;
	}
	public Integer getJobserverTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getJobserverTime (env));
	}

	public void setJobserverTime (SystemEnvironment env, Integer p_jobserverTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setJobserverTime (env, p_jobserverTime);
		return ;
	}
	public Integer getRestartableTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getRestartableTime (env));
	}

	public void setRestartableTime (SystemEnvironment env, Integer p_restartableTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setRestartableTime (env, p_restartableTime);
		return ;
	}
	public Integer getChildWaitTime (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getChildWaitTime (env));
	}

	public void setChildWaitTime (SystemEnvironment env, Integer p_childWaitTime)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setChildWaitTime (env, p_childWaitTime);
		return ;
	}
	public Long getOpSusresTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getOpSusresTs (env));
	}

	public void setOpSusresTs (SystemEnvironment env, Long p_opSusresTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setOpSusresTs (env, p_opSusresTs);
		return ;
	}
	public Long getNpeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getNpeId (env));
	}

	public void setNpeId (SystemEnvironment env, Long p_npeId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setNpeId (env, p_npeId);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSSubmittedEntity setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSubmittedEntityGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSSubmittedEntity)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSSubmittedEntityGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		((SDMSSubmittedEntityGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSSubmittedEntity setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		((SDMSSubmittedEntityGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSSubmittedEntity)this;
	}
	public SDMSSubmittedEntity set_MasterIdSeIdMergeMode (SystemEnvironment env, Long p_masterId, Long p_seId, Integer p_mergeMode)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSubmittedEntityGeneric)(object)).set_MasterIdSeIdMergeMode (env, p_masterId, p_seId, p_mergeMode);
		return (SDMSSubmittedEntity)this;
	}

	public SDMSSubmittedEntity set_MasterIdSeId (SystemEnvironment env, Long p_masterId, Long p_seId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSubmittedEntityGeneric)(object)).set_MasterIdSeId (env, p_masterId, p_seId);
		return (SDMSSubmittedEntity)this;
	}

	public SDMSSubmittedEntity set_FireSmeIdTrId (SystemEnvironment env, Long p_fireSmeId, Long p_trId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSubmittedEntityGeneric)(object)).set_FireSmeIdTrId (env, p_fireSmeId, p_trId);
		return (SDMSSubmittedEntity)this;
	}

	public SDMSSubmittedEntity set_MasterIdParentIdSeIdChildTag (SystemEnvironment env, Long p_masterId, Long p_parentId, Long p_seId, String p_childTag)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSubmittedEntityGeneric)(object)).set_MasterIdParentIdSeIdChildTag (env, p_masterId, p_parentId, p_seId, p_childTag);
		return (SDMSSubmittedEntity)this;
	}

	public SDMSSubmittedEntity set_ParentIdSeId (SystemEnvironment env, Long p_parentId, Long p_seId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSubmittedEntityGeneric)(object)).set_ParentIdSeId (env, p_parentId, p_seId);
		return (SDMSSubmittedEntity)this;
	}

	public SDMSSubmittedEntity set_ParentIdTrId (SystemEnvironment env, Long p_parentId, Long p_trId)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		((SDMSSubmittedEntityGeneric)(object)).set_ParentIdTrId (env, p_parentId, p_trId);
		return (SDMSSubmittedEntity)this;
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
		} else {
			if((env.cEnv.isJobServer()) || (env.cEnv.isJob()))
				p = checkPrivs;
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing Submitted Entity $1", getId(sysEnv));
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
		((SDMSSubmittedEntityGeneric) object).print();
	}
}
