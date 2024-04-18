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

public class SDMSSchedulingEntityTableGeneric extends SDMSTable
{

	public final static String tableName = "SCHEDULING_ENTITY";
	public static SDMSSchedulingEntityTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NAME"
		, "FOLDER_ID"
		, "OWNER_ID"
		, "TYPE"
		, "RUN_PROGRAM"
		, "RERUN_PROGRAM"
		, "KILL_PROGRAM"
		, "WORKDIR"
		, "LOGFILE"
		, "TRUNC_LOG"
		, "ERRLOGFILE"
		, "TRUNC_ERRLOG"
		, "EXPECTED_RUNTIME"
		, "EXPECTED_FINALTIME"
		, "GET_EXPECTED_RUNTIME"
		, "PRIORITY"
		, "MIN_PRIORITY"
		, "AGING_AMOUNT"
		, "AGING_BASE"
		, "SUBMIT_SUSPENDED"
		, "RESUME_AT"
		, "RESUME_IN"
		, "RESUME_BASE"
		, "MASTER_SUBMITTABLE"
		, "TIMEOUT_AMOUNT"
		, "TIMEOUT_BASE"
		, "TIMEOUT_STATE_ID"
		, "SAME_NODE"
		, "GANG_SCHEDULE"
		, "DEPENDENCY_OPERATION"
		, "ESMP_ID"
		, "ESP_ID"
		, "QA_ID"
		, "NE_ID"
		, "FP_ID"
		, "CANCEL_LEAD_FLAG"
		, "CANCEL_APPROVAL"
		, "RERUN_LEAD_FLAG"
		, "RERUN_APPROVAL"
		, "ENABLE_LEAD_FLAG"
		, "ENABLE_APPROVAL"
		, "SET_STATE_LEAD_FLAG"
		, "SET_STATE_APPROVAL"
		, "IGN_DEP_LEAD_FLAG"
		, "IGN_DEP_APPROVAL"
		, "IGN_RSS_LEAD_FLAG"
		, "IGN_RSS_APPROVAL"
		, "CLONE_LEAD_FLAG"
		, "CLONE_APPROVAL"
		, "EDIT_PARM_LEAD_FLAG"
		, "EDIT_PARM_APPROVAL"
		, "KILL_LEAD_FLAG"
		, "KILL_APPROVAL"
		, "SET_JOB_STATE_LEAD_FLAG"
		, "SET_JOB_STATE_APPROVAL"
		, "INHERIT_PRIVS"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_folderId;
	public static SDMSIndex idx_ownerId;
	public static SDMSIndex idx_esmpId;
	public static SDMSIndex idx_espId;
	public static SDMSIndex idx_neId;
	public static SDMSIndex idx_fpId;
	public static SDMSIndex idx_folderId_name;
	public static SDMSIndex idx_folderId_masterSubmittable;

	public SDMSSchedulingEntityTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "SchedulingEntity"));
		}
		table = (SDMSSchedulingEntityTable) this;
		SDMSSchedulingEntityTableGeneric.table = (SDMSSchedulingEntityTable) this;
		isVersioned = true;
		idx_folderId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "folderId");
		idx_ownerId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "ownerId");
		idx_esmpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "esmpId");
		idx_espId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "espId");
		idx_neId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "neId");
		idx_fpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "fpId");
		idx_folderId_name = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "folderId_name");
		idx_folderId_masterSubmittable = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "folderId_masterSubmittable");
	}
	public SDMSSchedulingEntity create(SystemEnvironment env
	                                   ,String p_name
	                                   ,Long p_folderId
	                                   ,Long p_ownerId
	                                   ,Integer p_type
	                                   ,String p_runProgram
	                                   ,String p_rerunProgram
	                                   ,String p_killProgram
	                                   ,String p_workdir
	                                   ,String p_logfile
	                                   ,Boolean p_truncLog
	                                   ,String p_errlogfile
	                                   ,Boolean p_truncErrlog
	                                   ,Integer p_expectedRuntime
	                                   ,Integer p_expectedFinaltime
	                                   ,String p_getExpectedRuntime
	                                   ,Integer p_priority
	                                   ,Integer p_minPriority
	                                   ,Integer p_agingAmount
	                                   ,Integer p_agingBase
	                                   ,Boolean p_submitSuspended
	                                   ,String p_resumeAt
	                                   ,Integer p_resumeIn
	                                   ,Integer p_resumeBase
	                                   ,Boolean p_masterSubmittable
	                                   ,Integer p_timeoutAmount
	                                   ,Integer p_timeoutBase
	                                   ,Long p_timeoutStateId
	                                   ,Boolean p_sameNode
	                                   ,Boolean p_gangSchedule
	                                   ,Integer p_dependencyOperation
	                                   ,Long p_esmpId
	                                   ,Long p_espId
	                                   ,Long p_qaId
	                                   ,Long p_neId
	                                   ,Long p_fpId
	                                   ,Boolean p_cancelLeadFlag
	                                   ,Integer p_cancelApproval
	                                   ,Boolean p_rerunLeadFlag
	                                   ,Integer p_rerunApproval
	                                   ,Boolean p_enableLeadFlag
	                                   ,Integer p_enableApproval
	                                   ,Boolean p_setStateLeadFlag
	                                   ,Integer p_setStateApproval
	                                   ,Boolean p_ignDepLeadFlag
	                                   ,Integer p_ignDepApproval
	                                   ,Boolean p_ignRssLeadFlag
	                                   ,Integer p_ignRssApproval
	                                   ,Boolean p_cloneLeadFlag
	                                   ,Integer p_cloneApproval
	                                   ,Boolean p_editParmLeadFlag
	                                   ,Integer p_editParmApproval
	                                   ,Boolean p_killLeadFlag
	                                   ,Integer p_killApproval
	                                   ,Boolean p_setJobStateLeadFlag
	                                   ,Integer p_setJobStateApproval
	                                   ,Long p_inheritPrivs
	                                  )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "SchedulingEntity"));
		}
		validate(env
		         , p_name
		         , p_folderId
		         , p_ownerId
		         , p_type
		         , p_runProgram
		         , p_rerunProgram
		         , p_killProgram
		         , p_workdir
		         , p_logfile
		         , p_truncLog
		         , p_errlogfile
		         , p_truncErrlog
		         , p_expectedRuntime
		         , p_expectedFinaltime
		         , p_getExpectedRuntime
		         , p_priority
		         , p_minPriority
		         , p_agingAmount
		         , p_agingBase
		         , p_submitSuspended
		         , p_resumeAt
		         , p_resumeIn
		         , p_resumeBase
		         , p_masterSubmittable
		         , p_timeoutAmount
		         , p_timeoutBase
		         , p_timeoutStateId
		         , p_sameNode
		         , p_gangSchedule
		         , p_dependencyOperation
		         , p_esmpId
		         , p_espId
		         , p_qaId
		         , p_neId
		         , p_fpId
		         , p_cancelLeadFlag
		         , p_cancelApproval
		         , p_rerunLeadFlag
		         , p_rerunApproval
		         , p_enableLeadFlag
		         , p_enableApproval
		         , p_setStateLeadFlag
		         , p_setStateApproval
		         , p_ignDepLeadFlag
		         , p_ignDepApproval
		         , p_ignRssLeadFlag
		         , p_ignRssApproval
		         , p_cloneLeadFlag
		         , p_cloneApproval
		         , p_editParmLeadFlag
		         , p_editParmApproval
		         , p_killLeadFlag
		         , p_killApproval
		         , p_setJobStateLeadFlag
		         , p_setJobStateApproval
		         , p_inheritPrivs
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSSchedulingEntityGeneric o = new SDMSSchedulingEntityGeneric(env
		                , p_name
		                , p_folderId
		                , p_ownerId
		                , p_type
		                , p_runProgram
		                , p_rerunProgram
		                , p_killProgram
		                , p_workdir
		                , p_logfile
		                , p_truncLog
		                , p_errlogfile
		                , p_truncErrlog
		                , p_expectedRuntime
		                , p_expectedFinaltime
		                , p_getExpectedRuntime
		                , p_priority
		                , p_minPriority
		                , p_agingAmount
		                , p_agingBase
		                , p_submitSuspended
		                , p_resumeAt
		                , p_resumeIn
		                , p_resumeBase
		                , p_masterSubmittable
		                , p_timeoutAmount
		                , p_timeoutBase
		                , p_timeoutStateId
		                , p_sameNode
		                , p_gangSchedule
		                , p_dependencyOperation
		                , p_esmpId
		                , p_espId
		                , p_qaId
		                , p_neId
		                , p_fpId
		                , p_cancelLeadFlag
		                , p_cancelApproval
		                , p_rerunLeadFlag
		                , p_rerunApproval
		                , p_enableLeadFlag
		                , p_enableApproval
		                , p_setStateLeadFlag
		                , p_setStateApproval
		                , p_ignDepLeadFlag
		                , p_ignDepApproval
		                , p_ignRssLeadFlag
		                , p_ignRssApproval
		                , p_cloneLeadFlag
		                , p_cloneApproval
		                , p_editParmLeadFlag
		                , p_editParmApproval
		                , p_killLeadFlag
		                , p_killApproval
		                , p_setJobStateLeadFlag
		                , p_setJobStateApproval
		                , p_inheritPrivs
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                               );

		SDMSSchedulingEntity p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSSchedulingEntity)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSSchedulingEntity)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSSchedulingEntity p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,String p_name
	                        ,Long p_folderId
	                        ,Long p_ownerId
	                        ,Integer p_type
	                        ,String p_runProgram
	                        ,String p_rerunProgram
	                        ,String p_killProgram
	                        ,String p_workdir
	                        ,String p_logfile
	                        ,Boolean p_truncLog
	                        ,String p_errlogfile
	                        ,Boolean p_truncErrlog
	                        ,Integer p_expectedRuntime
	                        ,Integer p_expectedFinaltime
	                        ,String p_getExpectedRuntime
	                        ,Integer p_priority
	                        ,Integer p_minPriority
	                        ,Integer p_agingAmount
	                        ,Integer p_agingBase
	                        ,Boolean p_submitSuspended
	                        ,String p_resumeAt
	                        ,Integer p_resumeIn
	                        ,Integer p_resumeBase
	                        ,Boolean p_masterSubmittable
	                        ,Integer p_timeoutAmount
	                        ,Integer p_timeoutBase
	                        ,Long p_timeoutStateId
	                        ,Boolean p_sameNode
	                        ,Boolean p_gangSchedule
	                        ,Integer p_dependencyOperation
	                        ,Long p_esmpId
	                        ,Long p_espId
	                        ,Long p_qaId
	                        ,Long p_neId
	                        ,Long p_fpId
	                        ,Boolean p_cancelLeadFlag
	                        ,Integer p_cancelApproval
	                        ,Boolean p_rerunLeadFlag
	                        ,Integer p_rerunApproval
	                        ,Boolean p_enableLeadFlag
	                        ,Integer p_enableApproval
	                        ,Boolean p_setStateLeadFlag
	                        ,Integer p_setStateApproval
	                        ,Boolean p_ignDepLeadFlag
	                        ,Integer p_ignDepApproval
	                        ,Boolean p_ignRssLeadFlag
	                        ,Integer p_ignRssApproval
	                        ,Boolean p_cloneLeadFlag
	                        ,Integer p_cloneApproval
	                        ,Boolean p_editParmLeadFlag
	                        ,Integer p_editParmApproval
	                        ,Boolean p_killLeadFlag
	                        ,Integer p_killApproval
	                        ,Boolean p_setJobStateLeadFlag
	                        ,Integer p_setJobStateApproval
	                        ,Long p_inheritPrivs
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSSchedulingEntityGeneric.checkType(p_type))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "type", p_type));
		if (!SDMSSchedulingEntityGeneric.checkTruncLog(p_truncLog))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "truncLog", p_truncLog));
		if (!SDMSSchedulingEntityGeneric.checkTruncErrlog(p_truncErrlog))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "truncErrlog", p_truncErrlog));
		if (!SDMSSchedulingEntityGeneric.checkAgingBase(p_agingBase))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "agingBase", p_agingBase));
		if (!SDMSSchedulingEntityGeneric.checkSubmitSuspended(p_submitSuspended))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "submitSuspended", p_submitSuspended));
		if (!SDMSSchedulingEntityGeneric.checkResumeBase(p_resumeBase))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "resumeBase", p_resumeBase));
		if (!SDMSSchedulingEntityGeneric.checkMasterSubmittable(p_masterSubmittable))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "masterSubmittable", p_masterSubmittable));
		if (!SDMSSchedulingEntityGeneric.checkTimeoutBase(p_timeoutBase))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "timeoutBase", p_timeoutBase));
		if (!SDMSSchedulingEntityGeneric.checkSameNode(p_sameNode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "sameNode", p_sameNode));
		if (!SDMSSchedulingEntityGeneric.checkGangSchedule(p_gangSchedule))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "gangSchedule", p_gangSchedule));
		if (!SDMSSchedulingEntityGeneric.checkDependencyOperation(p_dependencyOperation))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "dependencyOperation", p_dependencyOperation));
		if (!SDMSSchedulingEntityGeneric.checkCancelApproval(p_cancelApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "cancelApproval", p_cancelApproval));
		if (!SDMSSchedulingEntityGeneric.checkRerunApproval(p_rerunApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "rerunApproval", p_rerunApproval));
		if (!SDMSSchedulingEntityGeneric.checkEnableApproval(p_enableApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "enableApproval", p_enableApproval));
		if (!SDMSSchedulingEntityGeneric.checkSetStateApproval(p_setStateApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "setStateApproval", p_setStateApproval));
		if (!SDMSSchedulingEntityGeneric.checkIgnDepApproval(p_ignDepApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "ignDepApproval", p_ignDepApproval));
		if (!SDMSSchedulingEntityGeneric.checkIgnRssApproval(p_ignRssApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "ignRssApproval", p_ignRssApproval));
		if (!SDMSSchedulingEntityGeneric.checkCloneApproval(p_cloneApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "cloneApproval", p_cloneApproval));
		if (!SDMSSchedulingEntityGeneric.checkEditParmApproval(p_editParmApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "editParmApproval", p_editParmApproval));
		if (!SDMSSchedulingEntityGeneric.checkKillApproval(p_killApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "killApproval", p_killApproval));
		if (!SDMSSchedulingEntityGeneric.checkSetJobStateApproval(p_setJobStateApproval))
			throw new FatalException(new SDMSMessage(env, "01110182023", "SchedulingEntity: $1 $2", "setJobStateApproval", p_setJobStateApproval));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		String name;
		Long folderId;
		Long ownerId;
		Integer type;
		String runProgram;
		String rerunProgram;
		String killProgram;
		String workdir;
		String logfile;
		Boolean truncLog;
		String errlogfile;
		Boolean truncErrlog;
		Integer expectedRuntime;
		Integer expectedFinaltime;
		String getExpectedRuntime;
		Integer priority;
		Integer minPriority;
		Integer agingAmount;
		Integer agingBase;
		Boolean submitSuspended;
		String resumeAt;
		Integer resumeIn;
		Integer resumeBase;
		Boolean masterSubmittable;
		Integer timeoutAmount;
		Integer timeoutBase;
		Long timeoutStateId;
		Boolean sameNode;
		Boolean gangSchedule;
		Integer dependencyOperation;
		Long esmpId;
		Long espId;
		Long qaId;
		Long neId;
		Long fpId;
		Boolean cancelLeadFlag;
		Integer cancelApproval;
		Boolean rerunLeadFlag;
		Integer rerunApproval;
		Boolean enableLeadFlag;
		Integer enableApproval;
		Boolean setStateLeadFlag;
		Integer setStateApproval;
		Boolean ignDepLeadFlag;
		Integer ignDepApproval;
		Boolean ignRssLeadFlag;
		Integer ignRssApproval;
		Boolean cloneLeadFlag;
		Integer cloneApproval;
		Boolean editParmLeadFlag;
		Integer editParmApproval;
		Boolean killLeadFlag;
		Integer killApproval;
		Boolean setJobStateLeadFlag;
		Integer setJobStateApproval;
		Long inheritPrivs;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = Long.valueOf (r.getLong(1));
			name = r.getString(2);
			folderId = Long.valueOf (r.getLong(3));
			ownerId = Long.valueOf (r.getLong(4));
			type = Integer.valueOf (r.getInt(5));
			runProgram = r.getString(6);
			if (r.wasNull()) runProgram = null;
			rerunProgram = r.getString(7);
			if (r.wasNull()) rerunProgram = null;
			killProgram = r.getString(8);
			if (r.wasNull()) killProgram = null;
			workdir = r.getString(9);
			if (r.wasNull()) workdir = null;
			logfile = r.getString(10);
			if (r.wasNull()) logfile = null;
			truncLog = Boolean.valueOf ((r.getInt(11) == 0 ? false : true));
			if (r.wasNull()) truncLog = null;
			errlogfile = r.getString(12);
			if (r.wasNull()) errlogfile = null;
			truncErrlog = Boolean.valueOf ((r.getInt(13) == 0 ? false : true));
			if (r.wasNull()) truncErrlog = null;
			expectedRuntime = Integer.valueOf (r.getInt(14));
			if (r.wasNull()) expectedRuntime = null;
			expectedFinaltime = Integer.valueOf (r.getInt(15));
			if (r.wasNull()) expectedFinaltime = null;
			getExpectedRuntime = r.getString(16);
			if (r.wasNull()) getExpectedRuntime = null;
			priority = Integer.valueOf (r.getInt(17));
			minPriority = Integer.valueOf (r.getInt(18));
			if (r.wasNull()) minPriority = null;
			agingAmount = Integer.valueOf (r.getInt(19));
			if (r.wasNull()) agingAmount = null;
			agingBase = Integer.valueOf (r.getInt(20));
			if (r.wasNull()) agingBase = null;
			submitSuspended = Boolean.valueOf ((r.getInt(21) == 0 ? false : true));
			resumeAt = r.getString(22);
			if (r.wasNull()) resumeAt = null;
			resumeIn = Integer.valueOf (r.getInt(23));
			if (r.wasNull()) resumeIn = null;
			resumeBase = Integer.valueOf (r.getInt(24));
			if (r.wasNull()) resumeBase = null;
			masterSubmittable = Boolean.valueOf ((r.getInt(25) == 0 ? false : true));
			timeoutAmount = Integer.valueOf (r.getInt(26));
			if (r.wasNull()) timeoutAmount = null;
			timeoutBase = Integer.valueOf (r.getInt(27));
			if (r.wasNull()) timeoutBase = null;
			timeoutStateId = Long.valueOf (r.getLong(28));
			if (r.wasNull()) timeoutStateId = null;
			sameNode = Boolean.valueOf ((r.getInt(29) == 0 ? false : true));
			if (r.wasNull()) sameNode = null;
			gangSchedule = Boolean.valueOf ((r.getInt(30) == 0 ? false : true));
			if (r.wasNull()) gangSchedule = null;
			dependencyOperation = Integer.valueOf (r.getInt(31));
			esmpId = Long.valueOf (r.getLong(32));
			if (r.wasNull()) esmpId = null;
			espId = Long.valueOf (r.getLong(33));
			if (r.wasNull()) espId = null;
			qaId = Long.valueOf (r.getLong(34));
			if (r.wasNull()) qaId = null;
			neId = Long.valueOf (r.getLong(35));
			if (r.wasNull()) neId = null;
			fpId = Long.valueOf (r.getLong(36));
			if (r.wasNull()) fpId = null;
			cancelLeadFlag = Boolean.valueOf ((r.getInt(37) == 0 ? false : true));
			cancelApproval = Integer.valueOf (r.getInt(38));
			rerunLeadFlag = Boolean.valueOf ((r.getInt(39) == 0 ? false : true));
			rerunApproval = Integer.valueOf (r.getInt(40));
			enableLeadFlag = Boolean.valueOf ((r.getInt(41) == 0 ? false : true));
			enableApproval = Integer.valueOf (r.getInt(42));
			setStateLeadFlag = Boolean.valueOf ((r.getInt(43) == 0 ? false : true));
			setStateApproval = Integer.valueOf (r.getInt(44));
			ignDepLeadFlag = Boolean.valueOf ((r.getInt(45) == 0 ? false : true));
			ignDepApproval = Integer.valueOf (r.getInt(46));
			ignRssLeadFlag = Boolean.valueOf ((r.getInt(47) == 0 ? false : true));
			ignRssApproval = Integer.valueOf (r.getInt(48));
			cloneLeadFlag = Boolean.valueOf ((r.getInt(49) == 0 ? false : true));
			cloneApproval = Integer.valueOf (r.getInt(50));
			editParmLeadFlag = Boolean.valueOf ((r.getInt(51) == 0 ? false : true));
			editParmApproval = Integer.valueOf (r.getInt(52));
			killLeadFlag = Boolean.valueOf ((r.getInt(53) == 0 ? false : true));
			killApproval = Integer.valueOf (r.getInt(54));
			setJobStateLeadFlag = Boolean.valueOf ((r.getInt(55) == 0 ? false : true));
			setJobStateApproval = Integer.valueOf (r.getInt(56));
			inheritPrivs = Long.valueOf (r.getLong(57));
			creatorUId = Long.valueOf (r.getLong(58));
			createTs = Long.valueOf (r.getLong(59));
			changerUId = Long.valueOf (r.getLong(60));
			changeTs = Long.valueOf (r.getLong(61));
			validFrom = r.getLong(62);
			validTo = r.getLong(63);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "SchedulingEntity: $1 $2", Integer.valueOf(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSSchedulingEntityGeneric(id,
		                                       name,
		                                       folderId,
		                                       ownerId,
		                                       type,
		                                       runProgram,
		                                       rerunProgram,
		                                       killProgram,
		                                       workdir,
		                                       logfile,
		                                       truncLog,
		                                       errlogfile,
		                                       truncErrlog,
		                                       expectedRuntime,
		                                       expectedFinaltime,
		                                       getExpectedRuntime,
		                                       priority,
		                                       minPriority,
		                                       agingAmount,
		                                       agingBase,
		                                       submitSuspended,
		                                       resumeAt,
		                                       resumeIn,
		                                       resumeBase,
		                                       masterSubmittable,
		                                       timeoutAmount,
		                                       timeoutBase,
		                                       timeoutStateId,
		                                       sameNode,
		                                       gangSchedule,
		                                       dependencyOperation,
		                                       esmpId,
		                                       espId,
		                                       qaId,
		                                       neId,
		                                       fpId,
		                                       cancelLeadFlag,
		                                       cancelApproval,
		                                       rerunLeadFlag,
		                                       rerunApproval,
		                                       enableLeadFlag,
		                                       enableApproval,
		                                       setStateLeadFlag,
		                                       setStateApproval,
		                                       ignDepLeadFlag,
		                                       ignDepApproval,
		                                       ignRssLeadFlag,
		                                       ignRssApproval,
		                                       cloneLeadFlag,
		                                       cloneApproval,
		                                       editParmLeadFlag,
		                                       editParmApproval,
		                                       killLeadFlag,
		                                       killApproval,
		                                       setJobStateLeadFlag,
		                                       setJobStateApproval,
		                                       inheritPrivs,
		                                       creatorUId,
		                                       createTs,
		                                       changerUId,
		                                       changeTs,
		                                       validFrom, validTo);
	}

	protected void loadTable(SystemEnvironment env)
	throws SQLException, SDMSException
	{
		int read = 0;
		int loaded = 0;

		final boolean postgres = SystemEnvironment.isPostgreSQL;
		String squote = SystemEnvironment.SQUOTE;
		String equote = SystemEnvironment.EQUOTE;
		Statement stmt = env.dbConnection.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   squote + tableName() + equote + ".ID" +
		                                   ", " + squote + "NAME" + equote +
		                                   ", " + squote + "FOLDER_ID" + equote +
		                                   ", " + squote + "OWNER_ID" + equote +
		                                   ", " + squote + "TYPE" + equote +
		                                   ", " + squote + "RUN_PROGRAM" + equote +
		                                   ", " + squote + "RERUN_PROGRAM" + equote +
		                                   ", " + squote + "KILL_PROGRAM" + equote +
		                                   ", " + squote + "WORKDIR" + equote +
		                                   ", " + squote + "LOGFILE" + equote +
		                                   ", " + squote + "TRUNC_LOG" + equote +
		                                   ", " + squote + "ERRLOGFILE" + equote +
		                                   ", " + squote + "TRUNC_ERRLOG" + equote +
		                                   ", " + squote + "EXPECTED_RUNTIME" + equote +
		                                   ", " + squote + "EXPECTED_FINALTIME" + equote +
		                                   ", " + squote + "GET_EXPECTED_RUNTIME" + equote +
		                                   ", " + squote + "PRIORITY" + equote +
		                                   ", " + squote + "MIN_PRIORITY" + equote +
		                                   ", " + squote + "AGING_AMOUNT" + equote +
		                                   ", " + squote + "AGING_BASE" + equote +
		                                   ", " + squote + "SUBMIT_SUSPENDED" + equote +
		                                   ", " + squote + "RESUME_AT" + equote +
		                                   ", " + squote + "RESUME_IN" + equote +
		                                   ", " + squote + "RESUME_BASE" + equote +
		                                   ", " + squote + "MASTER_SUBMITTABLE" + equote +
		                                   ", " + squote + "TIMEOUT_AMOUNT" + equote +
		                                   ", " + squote + "TIMEOUT_BASE" + equote +
		                                   ", " + squote + "TIMEOUT_STATE_ID" + equote +
		                                   ", " + squote + "SAME_NODE" + equote +
		                                   ", " + squote + "GANG_SCHEDULE" + equote +
		                                   ", " + squote + "DEPENDENCY_OPERATION" + equote +
		                                   ", " + squote + "ESMP_ID" + equote +
		                                   ", " + squote + "ESP_ID" + equote +
		                                   ", " + squote + "QA_ID" + equote +
		                                   ", " + squote + "NE_ID" + equote +
		                                   ", " + squote + "FP_ID" + equote +
		                                   ", " + squote + "CANCEL_LEAD_FLAG" + equote +
		                                   ", " + squote + "CANCEL_APPROVAL" + equote +
		                                   ", " + squote + "RERUN_LEAD_FLAG" + equote +
		                                   ", " + squote + "RERUN_APPROVAL" + equote +
		                                   ", " + squote + "ENABLE_LEAD_FLAG" + equote +
		                                   ", " + squote + "ENABLE_APPROVAL" + equote +
		                                   ", " + squote + "SET_STATE_LEAD_FLAG" + equote +
		                                   ", " + squote + "SET_STATE_APPROVAL" + equote +
		                                   ", " + squote + "IGN_DEP_LEAD_FLAG" + equote +
		                                   ", " + squote + "IGN_DEP_APPROVAL" + equote +
		                                   ", " + squote + "IGN_RSS_LEAD_FLAG" + equote +
		                                   ", " + squote + "IGN_RSS_APPROVAL" + equote +
		                                   ", " + squote + "CLONE_LEAD_FLAG" + equote +
		                                   ", " + squote + "CLONE_APPROVAL" + equote +
		                                   ", " + squote + "EDIT_PARM_LEAD_FLAG" + equote +
		                                   ", " + squote + "EDIT_PARM_APPROVAL" + equote +
		                                   ", " + squote + "KILL_LEAD_FLAG" + equote +
		                                   ", " + squote + "KILL_APPROVAL" + equote +
		                                   ", " + squote + "SET_JOB_STATE_LEAD_FLAG" + equote +
		                                   ", " + squote + "SET_JOB_STATE_APPROVAL" + equote +
		                                   ", " + squote + "INHERIT_PRIVS" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", VALID_FROM, VALID_TO " +
		                                   " FROM " + squote + tableName() + equote +
		                                   " WHERE VALID_TO >= " + (postgres ?
		                                                   "CAST (\'" + env.lowestActiveVersion + "\' AS DECIMAL)" :
		                                                   "" + env.lowestActiveVersion) +
		                                   ""						  );
		while(rset.next()) {
			try {
				if(loadObject(env, rset)) ++loaded;
				++read;
			} catch (Exception e) {
				SDMSThread.doTrace(null, "Exception caught while loading table " + tableName() + ", ID = " + Long.valueOf (rset.getLong(1)), SDMSThread.SEVERITY_ERROR);
				throw(e);
			}
		}
		stmt.close();
		SDMSThread.doTrace(null, "Read " + read + ", Loaded " + loaded + " rows for " + tableName(), SDMSThread.SEVERITY_INFO);
	}

	public String checkIndex(SDMSObject o)
	throws SDMSException
	{
		String out = "";
		boolean ok;
		ok =  idx_folderId.check(((SDMSSchedulingEntityGeneric) o).folderId, o);
		out = out + "idx_folderId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_ownerId.check(((SDMSSchedulingEntityGeneric) o).ownerId, o);
		out = out + "idx_ownerId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_esmpId.check(((SDMSSchedulingEntityGeneric) o).esmpId, o);
		out = out + "idx_esmpId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_espId.check(((SDMSSchedulingEntityGeneric) o).espId, o);
		out = out + "idx_espId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_neId.check(((SDMSSchedulingEntityGeneric) o).neId, o);
		out = out + "idx_neId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_fpId.check(((SDMSSchedulingEntityGeneric) o).fpId, o);
		out = out + "idx_fpId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSchedulingEntityGeneric) o).folderId);
		k.add(((SDMSSchedulingEntityGeneric) o).name);
		ok =  idx_folderId_name.check(k, o);
		out = out + "idx_folderId_name: " + (ok ? "ok" : "missing") + "\n";
		k = new SDMSKey();
		k.add(((SDMSSchedulingEntityGeneric) o).folderId);
		k.add(((SDMSSchedulingEntityGeneric) o).masterSubmittable);
		ok =  idx_folderId_masterSubmittable.check(k, o);
		out = out + "idx_folderId_masterSubmittable: " + (ok ? "ok" : "missing") + "\n";
		return out;
	}

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		index(env, o, -1);
	}

	protected void index(SystemEnvironment env, SDMSObject o, long indexMember)
	throws SDMSException
	{
		idx_folderId.put(env, ((SDMSSchedulingEntityGeneric) o).folderId, o, ((1 & indexMember) != 0));
		idx_ownerId.put(env, ((SDMSSchedulingEntityGeneric) o).ownerId, o, ((2 & indexMember) != 0));
		idx_esmpId.put(env, ((SDMSSchedulingEntityGeneric) o).esmpId, o, ((4 & indexMember) != 0));
		idx_espId.put(env, ((SDMSSchedulingEntityGeneric) o).espId, o, ((8 & indexMember) != 0));
		idx_neId.put(env, ((SDMSSchedulingEntityGeneric) o).neId, o, ((16 & indexMember) != 0));
		idx_fpId.put(env, ((SDMSSchedulingEntityGeneric) o).fpId, o, ((32 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSchedulingEntityGeneric) o).folderId);
		k.add(((SDMSSchedulingEntityGeneric) o).name);
		idx_folderId_name.put(env, k, o, ((64 & indexMember) != 0));
		k = new SDMSKey();
		k.add(((SDMSSchedulingEntityGeneric) o).folderId);
		k.add(((SDMSSchedulingEntityGeneric) o).masterSubmittable);
		idx_folderId_masterSubmittable.put(env, k, o, ((128 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_folderId.remove(env, ((SDMSSchedulingEntityGeneric) o).folderId, o);
		idx_ownerId.remove(env, ((SDMSSchedulingEntityGeneric) o).ownerId, o);
		idx_esmpId.remove(env, ((SDMSSchedulingEntityGeneric) o).esmpId, o);
		idx_espId.remove(env, ((SDMSSchedulingEntityGeneric) o).espId, o);
		idx_neId.remove(env, ((SDMSSchedulingEntityGeneric) o).neId, o);
		idx_fpId.remove(env, ((SDMSSchedulingEntityGeneric) o).fpId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSSchedulingEntityGeneric) o).folderId);
		k.add(((SDMSSchedulingEntityGeneric) o).name);
		idx_folderId_name.remove(env, k, o);
		k = new SDMSKey();
		k.add(((SDMSSchedulingEntityGeneric) o).folderId);
		k.add(((SDMSSchedulingEntityGeneric) o).masterSubmittable);
		idx_folderId_masterSubmittable.remove(env, k, o);
	}

	public static SDMSSchedulingEntity getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSchedulingEntity) table.get(env, id);
	}

	public static SDMSSchedulingEntity getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSSchedulingEntity) table.getForUpdate(env, id);
	}

	public static SDMSSchedulingEntity getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSSchedulingEntity) table.get(env, id, version);
	}

	public static SDMSSchedulingEntity idx_folderId_name_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSchedulingEntity)  SDMSSchedulingEntityTableGeneric.idx_folderId_name.getUnique(env, key);
	}

	public static SDMSSchedulingEntity idx_folderId_name_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSSchedulingEntity)  SDMSSchedulingEntityTableGeneric.idx_folderId_name.getUniqueForUpdate(env, key);
	}

	public static SDMSSchedulingEntity idx_folderId_name_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSSchedulingEntity)  SDMSSchedulingEntityTableGeneric.idx_folderId_name.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
	public String[] columnNames()
	{
		return columnNames;
	}
}
