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

public class SDMSSubmittedEntityStatsGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_smeId = 2;
	public final static int nr_rerunSeq = 3;
	public final static int nr_scopeId = 4;
	public final static int nr_jobEsdId = 5;
	public final static int nr_exitCode = 6;
	public final static int nr_commandline = 7;
	public final static int nr_workdir = 8;
	public final static int nr_logfile = 9;
	public final static int nr_errlogfile = 10;
	public final static int nr_extPid = 11;
	public final static int nr_syncTs = 12;
	public final static int nr_resourceTs = 13;
	public final static int nr_runnableTs = 14;
	public final static int nr_startTs = 15;
	public final static int nr_finishTs = 16;
	public final static int nr_creatorUId = 17;
	public final static int nr_createTs = 18;
	public final static int nr_changerUId = 19;
	public final static int nr_changeTs = 20;

	public static String tableName = SDMSSubmittedEntityStatsTableGeneric.tableName;

	protected Long smeId;
	protected Integer rerunSeq;
	protected Long scopeId;
	protected Long jobEsdId;
	protected Integer exitCode;
	protected String commandline;
	protected String workdir;
	protected String logfile;
	protected String errlogfile;
	protected String extPid;
	protected Long syncTs;
	protected Long resourceTs;
	protected Long runnableTs;
	protected Long startTs;
	protected Long finishTs;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSSubmittedEntityStatsGeneric(
	        SystemEnvironment env,
	        Long p_smeId,
	        Integer p_rerunSeq,
	        Long p_scopeId,
	        Long p_jobEsdId,
	        Integer p_exitCode,
	        String p_commandline,
	        String p_workdir,
	        String p_logfile,
	        String p_errlogfile,
	        String p_extPid,
	        Long p_syncTs,
	        Long p_resourceTs,
	        Long p_runnableTs,
	        Long p_startTs,
	        Long p_finishTs,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSSubmittedEntityStatsTableGeneric.table);
		smeId = p_smeId;
		rerunSeq = p_rerunSeq;
		scopeId = p_scopeId;
		jobEsdId = p_jobEsdId;
		exitCode = p_exitCode;
		if (p_commandline != null && p_commandline.length() > 512) {
			p_commandline = p_commandline.substring(0,512);
		}
		commandline = p_commandline;
		if (p_workdir != null && p_workdir.length() > 512) {
			p_workdir = p_workdir.substring(0,512);
		}
		workdir = p_workdir;
		if (p_logfile != null && p_logfile.length() > 512) {
			p_logfile = p_logfile.substring(0,512);
		}
		logfile = p_logfile;
		if (p_errlogfile != null && p_errlogfile.length() > 512) {
			p_errlogfile = p_errlogfile.substring(0,512);
		}
		errlogfile = p_errlogfile;
		if (p_extPid != null && p_extPid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(SubmittedEntityStats) Length of $1 exceeds maximum length $2", "extPid", "32")
			);
		}
		extPid = p_extPid;
		syncTs = p_syncTs;
		resourceTs = p_resourceTs;
		runnableTs = p_runnableTs;
		startTs = p_startTs;
		finishTs = p_finishTs;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	void setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return;
		SDMSSubmittedEntityStatsGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityStatsGeneric) change(env);
			o.smeId = p_smeId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 5);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getRerunSeq (SystemEnvironment env)
	throws SDMSException
	{
		return (rerunSeq);
	}

	public	void setRerunSeq (SystemEnvironment env, Integer p_rerunSeq)
	throws SDMSException
	{
		if(rerunSeq.equals(p_rerunSeq)) return;
		SDMSSubmittedEntityStatsGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityStatsGeneric) change(env);
			o.rerunSeq = p_rerunSeq;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getScopeId (SystemEnvironment env)
	throws SDMSException
	{
		return (scopeId);
	}

	public	void setScopeId (SystemEnvironment env, Long p_scopeId)
	throws SDMSException
	{
		if(p_scopeId != null && p_scopeId.equals(scopeId)) return;
		if(p_scopeId == null && scopeId == null) return;
		SDMSSubmittedEntityStatsGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityStatsGeneric) change(env);
			o.scopeId = p_scopeId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 2);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getJobEsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (jobEsdId);
	}

	public	void setJobEsdId (SystemEnvironment env, Long p_jobEsdId)
	throws SDMSException
	{
		if(p_jobEsdId != null && p_jobEsdId.equals(jobEsdId)) return;
		if(p_jobEsdId == null && jobEsdId == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.jobEsdId = p_jobEsdId;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getExitCode (SystemEnvironment env)
	throws SDMSException
	{
		return (exitCode);
	}

	public	void setExitCode (SystemEnvironment env, Integer p_exitCode)
	throws SDMSException
	{
		if(p_exitCode != null && p_exitCode.equals(exitCode)) return;
		if(p_exitCode == null && exitCode == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.exitCode = p_exitCode;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getCommandline (SystemEnvironment env)
	throws SDMSException
	{
		return (commandline);
	}

	public	void setCommandline (SystemEnvironment env, String p_commandline)
	throws SDMSException
	{
		if(p_commandline != null && p_commandline.equals(commandline)) return;
		if(p_commandline == null && commandline == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		if (p_commandline != null && p_commandline.length() > 512) {
			p_commandline = p_commandline.substring(0,512);
		}
		o.commandline = p_commandline;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getWorkdir (SystemEnvironment env)
	throws SDMSException
	{
		return (workdir);
	}

	public	void setWorkdir (SystemEnvironment env, String p_workdir)
	throws SDMSException
	{
		if(p_workdir != null && p_workdir.equals(workdir)) return;
		if(p_workdir == null && workdir == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		if (p_workdir != null && p_workdir.length() > 512) {
			p_workdir = p_workdir.substring(0,512);
		}
		o.workdir = p_workdir;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getLogfile (SystemEnvironment env)
	throws SDMSException
	{
		return (logfile);
	}

	public	void setLogfile (SystemEnvironment env, String p_logfile)
	throws SDMSException
	{
		if(p_logfile != null && p_logfile.equals(logfile)) return;
		if(p_logfile == null && logfile == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		if (p_logfile != null && p_logfile.length() > 512) {
			p_logfile = p_logfile.substring(0,512);
		}
		o.logfile = p_logfile;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getErrlogfile (SystemEnvironment env)
	throws SDMSException
	{
		return (errlogfile);
	}

	public	void setErrlogfile (SystemEnvironment env, String p_errlogfile)
	throws SDMSException
	{
		if(p_errlogfile != null && p_errlogfile.equals(errlogfile)) return;
		if(p_errlogfile == null && errlogfile == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		if (p_errlogfile != null && p_errlogfile.length() > 512) {
			p_errlogfile = p_errlogfile.substring(0,512);
		}
		o.errlogfile = p_errlogfile;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getExtPid (SystemEnvironment env)
	throws SDMSException
	{
		return (extPid);
	}

	public	void setExtPid (SystemEnvironment env, String p_extPid)
	throws SDMSException
	{
		if(p_extPid != null && p_extPid.equals(extPid)) return;
		if(p_extPid == null && extPid == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		if (p_extPid != null && p_extPid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(SubmittedEntityStats) Length of $1 exceeds maximum length $2", "extPid", "32")
			);
		}
		o.extPid = p_extPid;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getSyncTs (SystemEnvironment env)
	throws SDMSException
	{
		return (syncTs);
	}

	public	void setSyncTs (SystemEnvironment env, Long p_syncTs)
	throws SDMSException
	{
		if(p_syncTs != null && p_syncTs.equals(syncTs)) return;
		if(p_syncTs == null && syncTs == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.syncTs = p_syncTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getResourceTs (SystemEnvironment env)
	throws SDMSException
	{
		return (resourceTs);
	}

	public	void setResourceTs (SystemEnvironment env, Long p_resourceTs)
	throws SDMSException
	{
		if(p_resourceTs != null && p_resourceTs.equals(resourceTs)) return;
		if(p_resourceTs == null && resourceTs == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.resourceTs = p_resourceTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getRunnableTs (SystemEnvironment env)
	throws SDMSException
	{
		return (runnableTs);
	}

	public	void setRunnableTs (SystemEnvironment env, Long p_runnableTs)
	throws SDMSException
	{
		if(p_runnableTs != null && p_runnableTs.equals(runnableTs)) return;
		if(p_runnableTs == null && runnableTs == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.runnableTs = p_runnableTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getStartTs (SystemEnvironment env)
	throws SDMSException
	{
		return (startTs);
	}

	public	void setStartTs (SystemEnvironment env, Long p_startTs)
	throws SDMSException
	{
		if(p_startTs != null && p_startTs.equals(startTs)) return;
		if(p_startTs == null && startTs == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.startTs = p_startTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getFinishTs (SystemEnvironment env)
	throws SDMSException
	{
		return (finishTs);
	}

	public	void setFinishTs (SystemEnvironment env, Long p_finishTs)
	throws SDMSException
	{
		if(p_finishTs != null && p_finishTs.equals(finishTs)) return;
		if(p_finishTs == null && finishTs == null) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.finishTs = p_finishTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SubmittedEntityStats) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSSubmittedEntityStatsGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.changerUId = p_changerUId;
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return;
		SDMSSubmittedEntityStatsGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSubmittedEntityStatsGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSSubmittedEntityStatsGeneric set_SmeIdRerunSeq (SystemEnvironment env, Long p_smeId, Integer p_rerunSeq)
	throws SDMSException
	{
		SDMSSubmittedEntityStatsGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SubmittedEntityStats) Change of system object not allowed")
				);
			}
			o = (SDMSSubmittedEntityStatsGeneric) change(env);
			o.smeId = p_smeId;
			o.rerunSeq = p_rerunSeq;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return SDMSSubmittedEntityStats.getProxy(sysEnv, this);
	}

	protected SDMSSubmittedEntityStatsGeneric(Long p_id,
	                Long p_smeId,
	                Integer p_rerunSeq,
	                Long p_scopeId,
	                Long p_jobEsdId,
	                Integer p_exitCode,
	                String p_commandline,
	                String p_workdir,
	                String p_logfile,
	                String p_errlogfile,
	                String p_extPid,
	                Long p_syncTs,
	                Long p_resourceTs,
	                Long p_runnableTs,
	                Long p_startTs,
	                Long p_finishTs,
	                Long p_creatorUId,
	                Long p_createTs,
	                Long p_changerUId,
	                Long p_changeTs,
	                long p_validFrom, long p_validTo)
	{
		id     = p_id;
		smeId = p_smeId;
		rerunSeq = p_rerunSeq;
		scopeId = p_scopeId;
		jobEsdId = p_jobEsdId;
		exitCode = p_exitCode;
		commandline = p_commandline;
		workdir = p_workdir;
		logfile = p_logfile;
		errlogfile = p_errlogfile;
		extPid = p_extPid;
		syncTs = p_syncTs;
		resourceTs = p_resourceTs;
		runnableTs = p_runnableTs;
		startTs = p_startTs;
		finishTs = p_finishTs;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		validFrom = p_validFrom;
		validTo   = p_validTo;
	}

	protected String tableName()
	{
		return tableName;
	}

	protected void insertDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "INSERT INTO " + squote + "SUBMITTED_ENTITY_STATS" + equote + " (" +
				        "ID" +
				        ", " + squote + "SME_ID" + equote +
				        ", " + squote + "RERUN_SEQ" + equote +
				        ", " + squote + "SCOPE_ID" + equote +
				        ", " + squote + "JOB_ESD_ID" + equote +
				        ", " + squote + "EXIT_CODE" + equote +
				        ", " + squote + "COMMANDLINE" + equote +
				        ", " + squote + "WORKDIR" + equote +
				        ", " + squote + "LOGFILE" + equote +
				        ", " + squote + "ERRLOGFILE" + equote +
				        ", " + squote + "EXTPID" + equote +
				        ", " + squote + "SYNC_TS" + equote +
				        ", " + squote + "RESOURCE_TS" + equote +
				        ", " + squote + "RUNNABLE_TS" + equote +
				        ", " + squote + "START_TS" + equote +
				        ", " + squote + "FINISH_TS" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ") VALUES (?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "SubmittedEntityStats: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, smeId.longValue());
			myInsert.setInt(3, rerunSeq.intValue());
			if (scopeId == null)
				myInsert.setNull(4, Types.INTEGER);
			else
				myInsert.setLong (4, scopeId.longValue());
			if (jobEsdId == null)
				myInsert.setNull(5, Types.INTEGER);
			else
				myInsert.setLong (5, jobEsdId.longValue());
			if (exitCode == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setInt(6, exitCode.intValue());
			if (commandline == null)
				myInsert.setNull(7, Types.VARCHAR);
			else
				myInsert.setString(7, commandline);
			if (workdir == null)
				myInsert.setNull(8, Types.VARCHAR);
			else
				myInsert.setString(8, workdir);
			if (logfile == null)
				myInsert.setNull(9, Types.VARCHAR);
			else
				myInsert.setString(9, logfile);
			if (errlogfile == null)
				myInsert.setNull(10, Types.VARCHAR);
			else
				myInsert.setString(10, errlogfile);
			if (extPid == null)
				myInsert.setNull(11, Types.VARCHAR);
			else
				myInsert.setString(11, extPid);
			if (syncTs == null)
				myInsert.setNull(12, Types.INTEGER);
			else
				myInsert.setLong (12, syncTs.longValue());
			if (resourceTs == null)
				myInsert.setNull(13, Types.INTEGER);
			else
				myInsert.setLong (13, resourceTs.longValue());
			if (runnableTs == null)
				myInsert.setNull(14, Types.INTEGER);
			else
				myInsert.setLong (14, runnableTs.longValue());
			if (startTs == null)
				myInsert.setNull(15, Types.INTEGER);
			else
				myInsert.setLong (15, startTs.longValue());
			if (finishTs == null)
				myInsert.setNull(16, Types.INTEGER);
			else
				myInsert.setLong (16, finishTs.longValue());
			myInsert.setLong (17, creatorUId.longValue());
			myInsert.setLong (18, createTs.longValue());
			myInsert.setLong (19, changerUId.longValue());
			myInsert.setLong (20, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "SubmittedEntityStats: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "DELETE FROM " + squote + "SUBMITTED_ENTITY_STATS" + equote + " WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "SubmittedEntityStats: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "SubmittedEntityStats: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE " + squote + "SUBMITTED_ENTITY_STATS" + equote + " SET " +
				        "" + squote + "SME_ID" + equote + " = ? " +
				        ", " + squote + "RERUN_SEQ" + equote + " = ? " +
				        ", " + squote + "SCOPE_ID" + equote + " = ? " +
				        ", " + squote + "JOB_ESD_ID" + equote + " = ? " +
				        ", " + squote + "EXIT_CODE" + equote + " = ? " +
				        ", " + squote + "COMMANDLINE" + equote + " = ? " +
				        ", " + squote + "WORKDIR" + equote + " = ? " +
				        ", " + squote + "LOGFILE" + equote + " = ? " +
				        ", " + squote + "ERRLOGFILE" + equote + " = ? " +
				        ", " + squote + "EXTPID" + equote + " = ? " +
				        ", " + squote + "SYNC_TS" + equote + " = ? " +
				        ", " + squote + "RESOURCE_TS" + equote + " = ? " +
				        ", " + squote + "RUNNABLE_TS" + equote + " = ? " +
				        ", " + squote + "START_TS" + equote + " = ? " +
				        ", " + squote + "FINISH_TS" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "SubmittedEntityStats: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong (1, smeId.longValue());
			myUpdate.setInt(2, rerunSeq.intValue());
			if (scopeId == null)
				myUpdate.setNull(3, Types.INTEGER);
			else
				myUpdate.setLong (3, scopeId.longValue());
			if (jobEsdId == null)
				myUpdate.setNull(4, Types.INTEGER);
			else
				myUpdate.setLong (4, jobEsdId.longValue());
			if (exitCode == null)
				myUpdate.setNull(5, Types.INTEGER);
			else
				myUpdate.setInt(5, exitCode.intValue());
			if (commandline == null)
				myUpdate.setNull(6, Types.VARCHAR);
			else
				myUpdate.setString(6, commandline);
			if (workdir == null)
				myUpdate.setNull(7, Types.VARCHAR);
			else
				myUpdate.setString(7, workdir);
			if (logfile == null)
				myUpdate.setNull(8, Types.VARCHAR);
			else
				myUpdate.setString(8, logfile);
			if (errlogfile == null)
				myUpdate.setNull(9, Types.VARCHAR);
			else
				myUpdate.setString(9, errlogfile);
			if (extPid == null)
				myUpdate.setNull(10, Types.VARCHAR);
			else
				myUpdate.setString(10, extPid);
			if (syncTs == null)
				myUpdate.setNull(11, Types.INTEGER);
			else
				myUpdate.setLong (11, syncTs.longValue());
			if (resourceTs == null)
				myUpdate.setNull(12, Types.INTEGER);
			else
				myUpdate.setLong (12, resourceTs.longValue());
			if (runnableTs == null)
				myUpdate.setNull(13, Types.INTEGER);
			else
				myUpdate.setLong (13, runnableTs.longValue());
			if (startTs == null)
				myUpdate.setNull(14, Types.INTEGER);
			else
				myUpdate.setLong (14, startTs.longValue());
			if (finishTs == null)
				myUpdate.setNull(15, Types.INTEGER);
			else
				myUpdate.setLong (15, finishTs.longValue());
			myUpdate.setLong (16, creatorUId.longValue());
			myUpdate.setLong (17, createTs.longValue());
			myUpdate.setLong (18, changerUId.longValue());
			myUpdate.setLong (19, changeTs.longValue());
			myUpdate.setLong(20, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "SubmittedEntityStats: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : SubmittedEntityStats", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "smeId : " + smeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rerunSeq : " + rerunSeq, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "scopeId : " + scopeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "jobEsdId : " + jobEsdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "exitCode : " + exitCode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "commandline : " + commandline, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "workdir : " + workdir, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "logfile : " + logfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errlogfile : " + errlogfile, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "extPid : " + extPid, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "syncTs : " + syncTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "resourceTs : " + resourceTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "runnableTs : " + runnableTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "startTs : " + startTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "finishTs : " + finishTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validTo : " + validTo, SDMSThread.SEVERITY_MESSAGE);
		dumpVersions(SDMSThread.SEVERITY_MESSAGE);
	}

	public String toString(int indent)
	{
		StringBuffer sb = new StringBuffer(indent + 1);
		for(int i = 0; i < indent; ++i) sb.append(" ");
		String indentString = new String(sb);
		String result =
		        indentString + "id : " + id + "\n" +
		        indentString + "smeId       : " + smeId + "\n" +
		        indentString + "rerunSeq    : " + rerunSeq + "\n" +
		        indentString + "scopeId     : " + scopeId + "\n" +
		        indentString + "jobEsdId    : " + jobEsdId + "\n" +
		        indentString + "exitCode    : " + exitCode + "\n" +
		        indentString + "commandline : " + commandline + "\n" +
		        indentString + "workdir     : " + workdir + "\n" +
		        indentString + "logfile     : " + logfile + "\n" +
		        indentString + "errlogfile  : " + errlogfile + "\n" +
		        indentString + "extPid      : " + extPid + "\n" +
		        indentString + "syncTs      : " + syncTs + "\n" +
		        indentString + "resourceTs  : " + resourceTs + "\n" +
		        indentString + "runnableTs  : " + runnableTs + "\n" +
		        indentString + "startTs     : " + startTs + "\n" +
		        indentString + "finishTs    : " + finishTs + "\n" +
		        indentString + "creatorUId  : " + creatorUId + "\n" +
		        indentString + "createTs    : " + createTs + "\n" +
		        indentString + "changerUId  : " + changerUId + "\n" +
		        indentString + "changeTs    : " + changeTs + "\n" +
		        indentString + "validFrom : " + validFrom + "\n" +
		        indentString + "validTo : " + validTo + "\n";
		return result;
	}

	public String toString()
	{
		String result = toString(0);
		return result;
	}
}
