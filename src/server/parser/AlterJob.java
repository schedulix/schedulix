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
package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.timer.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class AlterJob extends ManipJob
{

	public final static String __version = "@(#) $Id: AlterJob.java,v 2.35.2.4 2013/07/19 06:45:20 dieter Exp $";

	public static final int LOCALSUSPEND      = 100;
	public static final int LOCALADMINSUSPEND = 200;

	private static final int EP = 0x0000001;
	private static final int EX = 0x0000002;
	private static final int ST = 0x0000004;
	private static final int TS = 0x0000008;
	private static final int EC = 0x0000010;
	private static final int RP = 0x0000020;
	private static final int RR = 0x0000040;
	private static final int SU = 0x0000080;
	private static final int RE = 0x0000100;
	private static final int ID = 0x0000200;
	private static final int ES = 0x0000400;
	private static final int KI = 0x0000800;
	private static final int CN = 0x0001000;
	private static final int RC = 0x0002000;
	private static final int ET = 0x0004000;
	private static final int PR = 0x0008000;
	private static final int NV = 0x0010000;
	private static final int RN = 0x0020000;
	private static final int IR = 0x0040000;
	private static final int IN = 0x0040000;
	private static final int RU = 0x0080000;
	private static final int CW = 0x0100000;
	private static final int SW = 0x0200000;
	private static final int RS = 0x0400000;
	private static final int DA = 0x0800000;
	private static final int CL = 0x1000000;

	private static final int JS_ACTION = EP|EX|ST|TS|EC|ET|RU;
	private static final int OP_ACTION = RP|RR|SU|RE|ID|ET|PR|NV|IR|IN|CW|SW|RS|DA;
	private static final int ES_ACTION = ES|ET|RU;
	private static final int KI_ACTION = KI|ET;
	private static final int CN_ACTION = KI|CN|ET;
	private static final int DA_ACTION = DA|ET;
	private static final int RC_ACTION = RC|ET;
	private static final int CL_ACTION = CL|ET;

	public AlterJob(Long id, WithHash w)
	{
		super();
		cmdtype |= Node.SERVER_COMMAND;
		jobId = id;
		with = w;
	}

	public AlterJob(WithHash w)
	{
		super();
		cmdtype = Node.JOB_COMMAND;
		jobId = null;
		with = w;
	}

	private boolean checkConsistency(SystemEnvironment sysEnv)
		throws SDMSException
	{
		int v = 0;
		if(clone          != null) v += CL;
		if(execPid        != null) v += EP;
		if(extPid         != null) v += EX;
		if(status         != null) v += ST;
		if(ts             != null) v += TS;
		if(exitCode       != null) v += EC;
		if(runProgram     != null) v += RP;
		if(rerunProgram   != null) v += RR;
		if(suspend        != null) v += SU;
		if(resumeObj      != null) v += RS;
		if(rerunSeq       != null) v += RU;
		if(rerun          != null)
			if(rerun.booleanValue()) v += RC;
			else			 v += RE;
		if(depsToIgnore   != null) v += ID;
		if(exitState      != null) v += ES;
		if(kill           != null) v += KI;
		if(cancel         != null) v += CN;
		if(disable        != null) v += DA;
		if(priority       != null) v += PR;
		if(nicevalue      != null) v += NV;
		if(renice         != null) v += RN;
		if(resToIgnore    != null) v += IR;
		if(nrsToIgnore    != null) v += IN;
		if(errText        != null) v += ET;
		if(clearWarning   != null) v += CW;
		if(warning        != null) v += SW;

		if(((v & ~JS_ACTION) == 0) ||
		    ((v & ~OP_ACTION) == 0) ||
		    ((v & ~ES_ACTION) == 0) ||
		    ((v & ~DA_ACTION) == 0) ||
		    ((v & ~CL_ACTION) == 0) ||
		    ((v & ~CN_ACTION) == 0) ||
		    ((v & ~RC_ACTION) == 0)) {
			if(nicevalue != null && renice != null) return false;
			if(clearWarning != null && warning != null) return false;
		} else {
			return false;
		}
		return true;
	}

	private void collectWith(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector tmp;
		clone = (Boolean) with.get(ParseStr.S_CLONE);
		execPid = (String) with.get(ParseStr.S_EXEC_PID);
		extPid = (String) with.get(ParseStr.S_EXT_PID);
		status = (Integer) with.get(ParseStr.S_STATUS);
		tmp = (Vector) with.get(ParseStr.S_EXIT_STATUS);
		if(tmp != null) {
			exitState = (String) tmp.get(0);
			exitStateForce = (Boolean) tmp.get(1);
		}

		ts = (String) with.get(ParseStr.S_TIMESTAMP);
		if(ts != null) {
			ParsePosition pp = new ParsePosition(0);
			Date d = sysEnv.jsCommDateFormat.parse (ts, pp);
			if(pp.getErrorIndex() != -1) {
				pp = new ParsePosition(0);
				d = sysEnv.oldJsCommDateFormat.parse (ts, pp);
				if(pp.getErrorIndex() != -1) {
					pp = new ParsePosition(0);
					d = sysEnv.systemDateFormat.parse (ts, pp);
					if (pp.getErrorIndex() != -1) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03205061602", "Error in date format: $1\nPosition: $2",
								ts, pp.toString()));
					}
				}
			}
			final GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
			gc.setTime(d);
			tsLong = Long.valueOf(gc.getTimeInMillis());
		}

		exitCode = (Integer) with.get(ParseStr.S_EXIT_CODE);
		errText = (String) with.get(ParseStr.S_ERROR_TEXT);
		runProgram = (String) with.get(ParseStr.S_RUN_PROGRAM);
		rerunProgram = (String) with.get(ParseStr.S_RERUN_PROGRAM);
		Object o = with.get(ParseStr.S_SUSPEND);
		if (o != null) {
			adminSuspend = Boolean.FALSE;
			localSuspend = Boolean.FALSE;
			if (o instanceof Boolean) {
				suspend = (Boolean) o;
			} else {
				suspend = Boolean.TRUE;

				int suspendType = ((Integer) o).intValue();

				if (suspendType == LOCALADMINSUSPEND) {
					adminSuspend = Boolean.TRUE;
					localSuspend = Boolean.TRUE;
				} else if (suspendType == LOCALSUSPEND) {
					localSuspend = Boolean.TRUE;
				} else
					adminSuspend = Boolean.TRUE;
			}
		}
		resumeObj = with.get(ParseStr.S_RESUME);
		if (resumeObj == null && with.containsKey(ParseStr.S_RESUME)) {
			noResume = true;
		}
		if(suspend == null && resumeObj != null) {

			if (resumeObj instanceof Boolean) {
				suspend = (Boolean) resumeObj;
				resumeObj = null;
			}
		}
		if (suspend != null) {
			if(sysEnv.cEnv.isUser()) {
				if(sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
					if (!suspend.booleanValue())
						adminSuspend = Boolean.TRUE;
				} else if (adminSuspend != null && adminSuspend.booleanValue()) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03408071555", "Insufficient privileges for admin suspend"));
				}
				if (adminSuspend == null) adminSuspend = Boolean.FALSE;
			}

		}
		rerun = (Boolean) with.get(ParseStr.S_RERUN);
		rerunSeq = (Integer) with.get(ParseStr.S_RUN);
		kill = (Boolean) with.get(ParseStr.S_KILL);
		if (kill == Boolean.FALSE) {
			killRecursive = true;
			kill = Boolean.TRUE;
		} else {
			killRecursive = false;
		}
		cancel = (Boolean) with.get(ParseStr.S_CANCEL);
		disable = (Boolean) with.get(ParseStr.S_DISABLE);
		depsToIgnore = (Vector) with.get(ParseStr.S_IGNORE_DEPENDENCY);
		priority = (Integer) with.get(ParseStr.S_PRIORITY);
		nicevalue = (Integer) with.get(ParseStr.S_NICEVALUE);
		renice = (Integer) with.get(ParseStr.S_RENICE);
		resToIgnore = (Vector) with.get(ParseStr.S_IGNORE_RESOURCE);
		nrsToIgnore = (Vector) with.get(ParseStr.S_IGNORE_NAMED_RESOURCE);
		clearWarning = (Boolean) with.get(ParseStr.S_CLEAR_WARN);
		warning = (String) with.get(ParseStr.S_WARNING);

		if(!checkConsistency(sysEnv)) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207091715", "Illegal combination of With Options"));
		}
		comment = (String) with.get(ParseStr.S_COMMENT);
	}

	private void alterByJob(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{

		long actVersion = sme.getSeVersion(sysEnv).longValue();

		if(exitState != null) {
			Long esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, exitState, actVersion).getId(sysEnv);
			setExitState(sysEnv, sme, actVersion, esdId, exitStateForce);
		} else {
			if(with.containsKey(ParseStr.S_ERROR_TEXT)) {
				sme.setErrorMsg(sysEnv, errText);
			}
		}
		if(rerun != null) {
			if(rerun.booleanValue()) {
				sme.rerunRecursive(sysEnv, jobId, comment, true);
			} else {
				sme.rerun(sysEnv);
			}
		}
		if(cancel != null) {
			if(cancel.booleanValue()) {
				sme.cancel(sysEnv);
			}
		}
		if (kill != null) {
			if(kill.booleanValue()) {
				if(!killRecursive && SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getKillProgram(sysEnv) == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "032070111144", "couldn't kill, no kill program defined"));
				}
				sme.kill(sysEnv, killRecursive);
			}
		}
		return;
	}

	private void alterByJobserver(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long actVersion)
		throws SDMSException
	{
		auditFlag = false;
		Long sId = sme.getScopeId(sysEnv);
		SDMSScope s = SDMSScopeTable.getObjectForUpdate(sysEnv, sId);
		int actstate = sme.getState(sysEnv).intValue();
		if (actstate != SDMSSubmittedEntity.STARTING &&
		    actstate != SDMSSubmittedEntity.STARTED  &&
		    actstate != SDMSSubmittedEntity.RUNNING  &&
		    actstate != SDMSSubmittedEntity.TO_KILL  &&
		    actstate != SDMSSubmittedEntity.KILLED   &&
		    actstate != SDMSSubmittedEntity.BROKEN_ACTIVE) {
			result.setFeedback(new SDMSMessage(sysEnv, "03205141709", "Job altered"));
			return;
		}
		if(sysEnv.cEnv.uid().equals(sId)) {
			if(status != null) {
				changeState(sysEnv, sme, false, status);
			}
			s.setLastActive(sysEnv, Long.valueOf(sysEnv.cEnv.last()));
		} else {
			result.setFeedback(new SDMSMessage(sysEnv, "03205141710", "Job altered"));
		}
		if (!s.getIsEnabled(sysEnv).booleanValue() && !s.hasActiveJobs(sysEnv)) {
			SDMSThread.doTrace(sysEnv.cEnv, "Trying to kill connection " + sysEnv.cEnv.getMe().id() + " of Jobserver " + s.pathString(sysEnv), SDMSThread.SEVERITY_MESSAGE);
			if(s.isConnected(sysEnv)) {
				SystemEnvironment.server.killUser(sysEnv.cEnv.getMe().id());
			}
			s.setIsRegistered(sysEnv, Boolean.FALSE);
		}

	}

	private void alterByOperator(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long actVersion)
		throws SDMSException
	{
		int baseApprovalBits = sme.getApprovalMode(sysEnv).intValue();
		SDMSPrivilege priv = sme.getPrivileges(sysEnv);
		if(status != null) {
			String oldState = sme.getStateAsString(sysEnv);
			if (priv.can(SDMSPrivilege.SET_JOB_STATE)) {
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.SET_JOB_STATE_BITS;
				if (approvalBits != 0) {
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.SET_JOB_STATE,
					                    ((approvalBits & SDMSSubmittedEntity.SET_JOB_STATE_APPROVAL) == SDMSSubmittedEntity.SET_JOB_STATE_APPROVAL),
					                    sysEnv.cEnv.uid(), comment, Long.valueOf(status.intValue()), null, (exitCode != null ? Long.valueOf(exitCode.intValue()) : null), null);
				}
				if ((approvalBits & SDMSSubmittedEntity.SET_JOB_STATE_APPROVAL) == 0) {
					changeState(sysEnv, sme, true, status);
				}
			} else
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191534", "Insufficient privileges for set state"));
		}
		if(exitState != null) {
			if (priv.can(SDMSPrivilege.SET_STATE)) {
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.SET_STATE_BITS;
				Long esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, exitState, actVersion).getId(sysEnv);
				if (approvalBits != 0) {
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.SET_STATE,
					                    ((approvalBits & SDMSSubmittedEntity.SET_STATE_APPROVAL) == SDMSSubmittedEntity.SET_STATE_APPROVAL),
					                    sysEnv.cEnv.uid(), comment, esdId, exitStateForce, null, null);
				}
				if ((approvalBits & SDMSSubmittedEntity.SET_STATE_APPROVAL) == 0) {
					setExitState(sysEnv, sme, actVersion, esdId, exitStateForce);
				}
			} else
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191533", "Insufficient privileges for set state"));
		}
		if (noResume) {
			sme.setResumeTs(sysEnv, null);
		}
		Long resumeTs = null;
		if(resumeObj != null) {
			resumeTs = SubmitJob.evalResumeObj(sysEnv, resumeObj, null, true, sme.getEffectiveTimeZone(sysEnv));

			if (resumeTs == null || resumeTs.longValue() == -1l) {
				if (sme.getIsSuspended(sysEnv).intValue() != SDMSSubmittedEntity.NOSUSPEND)
					suspend = Boolean.FALSE;
				else {
					suspend = null;
					resumeObj = null;
				}
			}
		}
		if(suspend != null) {
			if (priv.can(SDMSPrivilege.SUSPEND)) {
				if (!suspend.booleanValue() && sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.ADMINSUSPEND && !adminSuspend.booleanValue())
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03408080757", "Insufficient privileges for admin resume"));
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.SUSPEND_BITS;
				if (approvalBits != 0) {
					int suspendType = 0;
					if (localSuspend != null && localSuspend.booleanValue()) suspendType += 1;
					if (adminSuspend.booleanValue()) suspendType += 2;
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.SUSPEND,
					                    ((approvalBits & SDMSSubmittedEntity.SUSPEND_APPROVAL) == SDMSSubmittedEntity.SUSPEND_APPROVAL),
					                    sysEnv.cEnv.uid(), comment, Long.valueOf (suspendType), suspend, resumeTs, null);
				}
				if ((approvalBits & SDMSSubmittedEntity.SUSPEND_APPROVAL) == 0) {
					performSuspend(sysEnv, sme, suspend, (localSuspend == null ? false : localSuspend.booleanValue()), adminSuspend.booleanValue(), resumeTs);
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191535", "Insufficient privileges for suspend"));
			}
		}
		if(resumeObj != null && suspend == null) {
			if (priv.can(SDMSPrivilege.SUSPEND)) {
				Date d = new Date(resumeTs);
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.SUSPEND_BITS;
				if (approvalBits != 0) {
					int suspendType = 0;
					if (localSuspend != null && localSuspend.booleanValue()) suspendType += 1;
					if (adminSuspend != null && adminSuspend.booleanValue()) suspendType += 2;
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.SUSPEND,
					                    ((approvalBits & SDMSSubmittedEntity.SUSPEND_APPROVAL) == SDMSSubmittedEntity.SUSPEND_APPROVAL),
					                    sysEnv.cEnv.uid(), comment, Long.valueOf (suspendType), suspend, resumeTs, null);
				}
				if ((approvalBits & SDMSSubmittedEntity.SUSPEND_APPROVAL) == 0) {
					sme.setResumeTs(sysEnv, resumeTs);
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03106151535", "Insufficient privileges for resume"));
			}
		}
		if(rerun != null) {
			if (priv.can(SDMSPrivilege.RERUN)) {
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.RERUN_BITS;
				if (approvalBits != 0) {
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.RERUN,
					                    ((approvalBits & SDMSSubmittedEntity.RERUN_APPROVAL) == SDMSSubmittedEntity.RERUN_APPROVAL),
					                    sysEnv.cEnv.uid(), comment, null, rerun, null, null);
				}
				if ((approvalBits & SDMSSubmittedEntity.RERUN_APPROVAL) == 0) {
					performRerun(sysEnv, sme, rerun);
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191539", "Insufficient privileges for rerun"));
			}
		}
		if(kill != null) {
			if (priv.can(SDMSPrivilege.KILL)) {
				if(kill.booleanValue()) {
					if(!killRecursive && SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getKillProgram(sysEnv) == null) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "032070111145", "couldn't kill, no kill program defined"));
					}
					int approvalBits = baseApprovalBits & SDMSSubmittedEntity.KILL_BITS;
					if (approvalBits != 0) {
						createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.KILL,
						                    ((approvalBits & SDMSSubmittedEntity.KILL_APPROVAL) == SDMSSubmittedEntity.KILL_APPROVAL),
						                    sysEnv.cEnv.uid(), comment, null, kill, null, null);
					}
					if ((approvalBits & SDMSSubmittedEntity.KILL_APPROVAL) == 0) {
						performKill(sysEnv, sme);
					}
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191549", "Insufficient privileges for kill"));
			}
		}
		if(cancel != null) {
			if (priv.can(SDMSPrivilege.CANCEL)) {
				if(cancel.booleanValue()) {
					int approvalBits = baseApprovalBits & SDMSSubmittedEntity.CANCEL_BITS;
					if (approvalBits != 0) {
						createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.CANCEL,
						                    ((approvalBits & SDMSSubmittedEntity.CANCEL_APPROVAL) == SDMSSubmittedEntity.CANCEL_APPROVAL),
						                    sysEnv.cEnv.uid(), comment, null, cancel, null, null);
					}
					if ((approvalBits & SDMSSubmittedEntity.CANCEL_APPROVAL) == 0) {
						performCancel(sysEnv, sme);
					}
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191551", "Insufficient privileges for cancel"));
			}
		}
		if(disable != null) {
			if (priv.can(SDMSPrivilege.ENABLE)) {
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.ENABLE_BITS;
				if (approvalBits != 0) {
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.ENABLE,
					                    ((approvalBits & SDMSSubmittedEntity.ENABLE_APPROVAL) == SDMSSubmittedEntity.ENABLE_APPROVAL),
					                    sysEnv.cEnv.uid(), comment, null, disable, null, null);
				}
				if ((approvalBits & SDMSSubmittedEntity.ENABLE_APPROVAL) == 0) {
					performDisable(sysEnv, sme, disable);
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191552", "Insufficient privileges for enable/disable"));
			}
		}
		if(depsToIgnore != null) {
			if (priv.can(SDMSPrivilege.IGN_DEPENDENCY)) {
				ignoreDeps(sysEnv, sme);
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191553", "Insufficient privileges for ignore dependency"));
			}
		}
		if(resToIgnore != null) {
			if (priv.can(SDMSPrivilege.IGN_RESOURCE)) {
				ignoreResources(sysEnv, sme);
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191555", "Insufficient privileges for ignore resource"));
			}
		}
		if(priority != null || nicevalue != null || renice != null) {
			if (priv.can(SDMSPrivilege.PRIORITY)) {
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.PRIORITY_BITS;
				Boolean isRenice = Boolean.FALSE;
				if (priority != null) {
					if(SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03211211229", "Cannot change the priority of a batch or milestone"));
					}
					if(priority.intValue() < SystemEnvironment.priorityLowerBound && !sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
						priority = Integer.valueOf(SystemEnvironment.priorityLowerBound);

					}
					isRenice = Boolean.FALSE;

				}
				if (nicevalue != null) {
					isRenice = Boolean.TRUE;
					priority = nicevalue;
				}
				if(renice != null) {
					int nv = renice.intValue() + sme.getNice(sysEnv).intValue();
					isRenice = Boolean.TRUE;
					priority = Integer.valueOf(nv);
				}
				if (approvalBits != 0) {
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.PRIORITY,
					                    ((approvalBits & SDMSSubmittedEntity.PRIORITY_APPROVAL) == SDMSSubmittedEntity.PRIORITY_APPROVAL),
					                    sysEnv.cEnv.uid(), comment, Long.valueOf(priority), isRenice, null, null);
				}
				if ((approvalBits & SDMSSubmittedEntity.PRIORITY_APPROVAL) == 0) {
					performPriority(sysEnv, sme, isRenice, priority);
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191556", "Insufficient privileges for set priority"));
			}
		}
		if (clone != null) {
			if (priv.can(SDMSPrivilege.CLONE)) {
				if(comment == null)
					comment = "";
				if (sme.getIsReplaced(sysEnv).booleanValue()) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03910311420", "Cannot clone an already replaced job or batch"));
				}
				if (sme.getState(sysEnv) != SDMSSubmittedEntity.FINAL) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03910311421", "Cannot clone a job or batch that is stil active"));
				}
				Long parentId = sme.getParentId(sysEnv);
				if (parentId == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03910311422", "Cannot clone a master job or batch"));
				} else {
					int approvalBits = baseApprovalBits & SDMSSubmittedEntity.CLONE_BITS;
					if (approvalBits != 0) {
						createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.CLONE,
						                    ((approvalBits & SDMSSubmittedEntity.CLONE_APPROVAL) == SDMSSubmittedEntity.CLONE_APPROVAL),
						                    sysEnv.cEnv.uid(), comment, null, clone, null, null);
					}
					if ((approvalBits & SDMSSubmittedEntity.CLONE_APPROVAL) == 0) {
						performClone(sysEnv, sme, clone);
					}
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191559", "Insufficient privileges for clone"));
			}
		}

		if(with.containsKey(ParseStr.S_ERROR_TEXT))	sme.setErrorMsg(sysEnv, errText);

	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSSubmittedEntity sme = null;
		boolean ignore = false;

		collectWith(sysEnv);

		if(sysEnv.cEnv.isJob()) {
			jobId = sysEnv.cEnv.uid();
			sme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, jobId);
		} else {
			try {
				sme = SDMSSubmittedEntityTable.getObjectForUpdate(sysEnv, jobId);
			} catch (NotFoundException nfe) {}
		}

		if (rerunSeq != null) {
			if(sme != null) {
				int rs = rerunSeq.intValue();
				int sme_rs = sme.getRerunSeq(sysEnv).intValue();
				if(rs < sme_rs) {
					if (sysEnv.cEnv.isUser()) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03406031444", "Run sequence number expired"));
					} else {
						ignore = true;
					}
				} else if(rs > sme_rs) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03406031442", "Run sequence number out of sync"));
				}
			} else {
			}
		}

		if(!ignore) {
			if(sysEnv.cEnv.isJob()) {
				alterByJob(sysEnv, sme);
			} else {
				if (sme == null) {
					go_killjob(sysEnv);
					return;
				}
				long actVersion = sme.getSeVersion(sysEnv).longValue();

				if(sysEnv.cEnv.isJobServer()) {
					alterByJobserver(sysEnv, sme, actVersion);
				} else {
					alterByOperator(sysEnv, sme, actVersion);
				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03204112159", "Job altered"));
	}

	private void changeKillJobState(SystemEnvironment sysEnv, SDMSKillJob kj, boolean force)
		throws SDMSException
	{
		int s;

		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, kj.getSmeId(sysEnv));
		s = sme.getState(sysEnv).intValue();

		kj.setState(sysEnv, status);
		delFromQueue(sysEnv, kj);
		if(with.containsKey(ParseStr.S_ERROR_TEXT))	kj.setErrorMsg(sysEnv, errText);
		if(with.containsKey(ParseStr.S_EXEC_PID))	kj.setPid(sysEnv, execPid);
		if(with.containsKey(ParseStr.S_EXT_PID))	kj.setExtPid(sysEnv, extPid);

		switch(status.intValue()) {
			case SDMSSubmittedEntity.STARTED:
				if(tsLong != null) 			kj.setStartTs(sysEnv, tsLong);
				break;
			case SDMSSubmittedEntity.RUNNING:
				if(tsLong != null) 			kj.setStartTs(sysEnv, tsLong);
				break;
			case SDMSSubmittedEntity.FINISHED:
				if(tsLong != null) 			kj.setFinishTs(sysEnv, tsLong);
				if(with.containsKey(ParseStr.S_EXIT_CODE)) {
					kj.setExitCode(sysEnv, exitCode);
					sme.setKillExitCode(sysEnv, exitCode);
				}
				if(s == SDMSSubmittedEntity.TO_KILL) {
					sme.setState(sysEnv, SDMSConstants.SME_KILLED);
				}
				break;
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				break;
			case SDMSSubmittedEntity.BROKEN_FINISHED:
				if(s == SDMSSubmittedEntity.TO_KILL) {
					sme.setState(sysEnv, SDMSConstants.SME_KILLED);
				}
				break;
			case SDMSSubmittedEntity.ERROR:
				if(s == SDMSSubmittedEntity.TO_KILL) {
					sme.setState(sysEnv, SDMSConstants.SME_RUNNING);
				}
				break;
			default:
				if(force) {
					kj.setState(sysEnv, status);
				}
				break;
		}
	}

	private void go_killjob(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSKillJob kj = null;

		kj = SDMSKillJobTable.getObject(sysEnv, jobId);

		if(sysEnv.cEnv.isJobServer()) {
			if(!sysEnv.cEnv.uid().equals(kj.getScopeId(sysEnv))) {
				result.setFeedback(new SDMSMessage(sysEnv, "03207091826", "Job altered"));
				return;
			}
			if(status != null) {
				changeKillJobState(sysEnv, kj, false);
			}
		} else {

			if(status != null) {
				changeKillJobState(sysEnv, kj, true);
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03207091825", "Job altered"));
	}

	private void ignoreDeps(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Vector v;

		for(int i = 0; i < depsToIgnore.size(); i++) {
			v = (Vector) depsToIgnore.get(i);
			Long diId = (Long) v.get(0);
			Boolean rec = (Boolean) v.get(1);
			SDMSDependencyInstance di = SDMSDependencyInstanceTable.getObject(sysEnv, diId);
			int approvalBits = sme.getApprovalMode(sysEnv).intValue() & SDMSSubmittedEntity.IGN_DEP_BITS;
			if (approvalBits != 0) {
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.IGN_DEPENDENCY,
				                    ((approvalBits & SDMSSubmittedEntity.IGN_DEP_APPROVAL) == SDMSSubmittedEntity.IGN_DEP_APPROVAL),
				                    sysEnv.cEnv.uid(), comment, di.getId(sysEnv), rec, null, null);
			}
			if ((approvalBits & SDMSSubmittedEntity.IGN_DEP_APPROVAL) == 0) {
				di.setIgnore(sysEnv, (rec.booleanValue()? SDMSDependencyInstance.RECURSIVE : SDMSDependencyInstance.YES),
				             jobId, comment);
			}
		}
	}

	private void ignoreResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Long rId;
		SDMSResourceAllocation ra = null;
		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv).longValue());
		Long smeId = sme.getId(sysEnv);

		for(int i = 0; i < resToIgnore.size(); i++) {
			boolean raFound = false;
			rId = (Long) resToIgnore.get(i);
			try {
				SDMSResource r = SDMSResourceTable.getObject(sysEnv, rId);
				Vector rav = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(smeId, r.getNrId(sysEnv)));
				for (int j = 0; j < rav.size(); ++j) {
					ra = (SDMSResourceAllocation) rav.get(j);
					if (ra.getRId(sysEnv).equals(rId)) {
						raFound = true;
						break;
					}
				}
			} catch(NotFoundException nfe) {
				continue;
			}
			if (!raFound) continue;

			if(se.checkParameterRI(sysEnv, ra.getNrId(sysEnv))) {
				SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, ra.getNrId(sysEnv));
				throw new CommonErrorException(
					new SDMSMessage(sysEnv, "03409291444", "You cannot ignore resource $1, parameter references exist",
						nr.pathString(sysEnv)
					)
				);
			}
			int approvalBits = sme.getApprovalMode(sysEnv).intValue() & SDMSSubmittedEntity.IGN_RSS_BITS;
			if (approvalBits != 0) {
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.IGN_RESOURCE,
				                    ((approvalBits & SDMSSubmittedEntity.IGN_RSS_APPROVAL) == SDMSSubmittedEntity.IGN_RSS_APPROVAL),
				                    sysEnv.cEnv.uid(), comment, rId, null, null, null);
			}
			if ((approvalBits & SDMSSubmittedEntity.IGN_RSS_APPROVAL) == 0) {
				ra.ignore(sysEnv);
			}
		}
		SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.IGNORE_RESOURCE);
	}

}

