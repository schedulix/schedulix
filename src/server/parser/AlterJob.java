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
	private static final int DA_ACTION = DA|ET|SU;
	private static final int RC_ACTION = RC|ET|SU;
	private static final int CL_ACTION = CL|ET|SU;

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
		adminSuspend = Boolean.FALSE;
		localSuspend = Boolean.FALSE;
		System.err.println("ADMINSUSPEND = FALSE");
		if (o != null) {
			if (o instanceof Boolean) {
				suspend = (Boolean) o;
				System.err.println("ADMINSUSPEND = FALSE -- simple suspend or resume");
			} else {
				suspend = Boolean.TRUE;

				int suspendType = ((Integer) o).intValue();

				if (suspendType == LOCALADMINSUSPEND) {
					adminSuspend = Boolean.TRUE;
					localSuspend = Boolean.TRUE;
					System.err.println("ADMINSUSPEND = TRUE -- localadminsuspend");
				} else if (suspendType == LOCALSUSPEND) {
					localSuspend = Boolean.TRUE;
				} else {
					adminSuspend = Boolean.TRUE;
					System.err.println("ADMINSUSPEND = TRUE -- adminsuspend or resume");
				}
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
					if (!suspend.booleanValue()) {
						adminSuspend = Boolean.TRUE;
						System.err.println("ADMINSUSPEND = TRUE -- admin mode per default at resume");
					}
				} else if (adminSuspend != null && adminSuspend.booleanValue()) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03408071555", "Insufficient privileges for admin suspend"));
				}
				if (adminSuspend == null) {
					adminSuspend = Boolean.FALSE;
					System.err.println("ADMINSUSPEND = FALSE -- normal user");
				}
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

	private void checkPendingApproval(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, int operation, String opName)
	throws SDMSException
	{
		Long smeId = sme.getId(sysEnv);
		Vector appv = SDMSSystemMessageTable.idx_smeId.getVector(sysEnv, smeId);
		for (int i = 0; i < appv.size(); ++i) {
			SDMSSystemMessage msg = (SDMSSystemMessage) appv.get(i);
			if (!msg.getIsMandatory(sysEnv).booleanValue())
				continue;
			int msgOp = msg.getOperation(sysEnv).intValue();
			if (msgOp == operation) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03309251616", "Approval request for " + opName + " already pending"));
			}
			if (msgOp == SDMSSystemMessage.CANCEL && operation == SDMSSystemMessage.KILL)
				if (msg.getAdditionalBool(sysEnv).booleanValue())
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03309261342", "Approval request for " + opName + " already pending (cancel with kill)"));
		}
		return;
	}

	private Long checkChildApprovals(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, int operationBits)
	throws SDMSException
	{
		int baseApprovalBits;
		int approvalBits;
		boolean isApproval;
		int childApprovalMode = sme.getChildApprovalMode(sysEnv).intValue();
		if ((childApprovalMode & operationBits) == 0) return null;
		Long smeId = sme.getId(sysEnv);

		Vector childv = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, smeId);
		Vector<SDMSSubmittedEntity> interestingChildren = new Vector<SDMSSubmittedEntity>();
		Iterator it = childv.iterator();
		while (it.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance) it.next();
			Long childSmeId = hi.getChildId(sysEnv);
			SDMSSubmittedEntity childSme = SDMSSubmittedEntityTable.getObject(sysEnv, childSmeId);
			if ((operationBits & SDMSSubmittedEntity.CLONE_BITS) == 0 && childSme.getJobIsFinal(sysEnv).booleanValue())
				continue;

			baseApprovalBits = childSme.getApprovalMode(sysEnv).intValue();
			approvalBits = baseApprovalBits & operationBits;
			if (approvalBits != 0) {
				return childSmeId;
			}
			childApprovalMode = childSme.getChildApprovalMode(sysEnv);
			if ((childApprovalMode & operationBits) != 0) {
				interestingChildren.add(childSme);
			}
		}
		for (int i = 0; i < interestingChildren.size(); ++i) {
			SDMSSubmittedEntity childSme = interestingChildren.get(i);

			Long tmp = checkChildApprovals(sysEnv, childSme, operationBits);
			if (tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	private void alterByOperator(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long actVersion)
		throws SDMSException
	{
		int baseApprovalBits = sme.getApprovalMode(sysEnv).intValue();
		int childApprovalMode = sme.getChildApprovalMode(sysEnv).intValue();
		SDMSPrivilege priv = sme.getPrivileges(sysEnv);
		int operationBits = 0;

		Long autoResumeVal = null;
		Long suspendFlag = null;
		if (resumeObj != null) {
			if (resumeObj instanceof WithHash) {
				WithHash wh = (WithHash) resumeObj;
				autoResumeVal = Long.valueOf(- SubmitJob.getResumeInValue(sysEnv, (Integer) wh.get(ParseStr.S_MULT), (Integer) wh.get(ParseStr.S_INTERVAL)));
			} else {
				autoResumeVal = SubmitJob.evalResumeObj(sysEnv, (String) resumeObj, null, null, null, true, sme.getEffectiveTimeZone(sysEnv));
			}
			if (autoResumeVal.longValue() == -1l)
				autoResumeVal = Long.valueOf(0l);
		}
		if(suspend != null) {
			if (priv.can(SDMSPrivilege.SUSPEND)) {
				if (!suspend.booleanValue() && sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.ADMINSUSPEND && !adminSuspend.booleanValue()) {
					System.err.println("ADMINSUSPEND = " + adminSuspend);
					System.err.println("SUSPEND = " + suspend);
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03408080758", "Insufficient privileges for admin resume"));
				}
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191535", "Insufficient privileges for suspend"));
			}
			if (suspend.booleanValue())	suspendFlag = Long.valueOf(0x01l);
			else				suspendFlag = Long.valueOf(0x00l);
			if (localSuspend != null && localSuspend.booleanValue()) suspendFlag |= 0x02l;
			if (adminSuspend != null && adminSuspend.booleanValue()) suspendFlag |= 0x04l;
		}

		if(status != null) {
			String oldState = sme.getStateAsString(sysEnv);
			String newState = SDMSSubmittedEntity.convertStateToString(status);
			Long lExitCode = (exitCode != null ? Long.valueOf(exitCode.intValue()) : null);

			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191534", "Insufficient privileges for set state"));
			}
			int approvalBits = baseApprovalBits & SDMSSubmittedEntity.SET_JOB_STATE_BITS;
			boolean isApproval = ((approvalBits & SDMSSubmittedEntity.SET_JOB_STATE_APPROVAL) == SDMSSubmittedEntity.SET_JOB_STATE_APPROVAL);
			if (approvalBits != 0) {
				checkPendingApproval(sysEnv, sme, SDMSSystemMessage.SET_JOB_STATE, "Set Job State");
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.SET_JOB_STATE,
				                    isApproval, sysEnv.cEnv.uid(), comment, Long.valueOf(status.intValue()), null, lExitCode,
				                    "SET JOB STATE: old=" + oldState + ", new=" + newState + (lExitCode == null ? "" : ", Exit Code=" + lExitCode));
			}
			if (!isApproval) {
				changeState(sysEnv, sme, true, status);
			}
		}
		if(exitState != null) {
			if (!priv.can(SDMSPrivilege.SET_STATE)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191533", "Insufficient privileges for set state"));
			}
			int approvalBits = baseApprovalBits & SDMSSubmittedEntity.SET_STATE_BITS;
			boolean isApproval = ((approvalBits & SDMSSubmittedEntity.SET_STATE_APPROVAL) == SDMSSubmittedEntity.SET_STATE_APPROVAL);
			Long esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, exitState, actVersion).getId(sysEnv);
			Long oldEsdId = sme.getJobEsdId(sysEnv);
			String oldEsd;
			if (oldEsdId != null)
				oldEsd = SDMSExitStateDefinitionTable.getObject(sysEnv, oldEsdId).getName(sysEnv);
			else
				oldEsd = "<NOT SET>";
			if (approvalBits != 0) {
				checkPendingApproval(sysEnv, sme, SDMSSystemMessage.SET_STATE, "Set Exit State");
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.SET_STATE,
				                    isApproval, sysEnv.cEnv.uid(), comment, esdId, exitStateForce, null,
				                    "SET EXIT STATE; old=" + oldEsd + ", new=" + exitState);
			}
			if (!isApproval) {
				setExitState(sysEnv, sme, actVersion, esdId, exitStateForce);
			}
		}

		if(cancel != null) {
			if (!priv.can(SDMSPrivilege.CANCEL)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191551", "Insufficient privileges for cancel"));
			}
			boolean cancelWithKill = false;
			if(kill != null) {
				if (!priv.can(SDMSPrivilege.KILL)) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191549", "Insufficient privileges for kill"));
				}
				cancelWithKill = true;
				killRecursive = true;
				kill = null;
			}

			int approvalBits = baseApprovalBits & SDMSSubmittedEntity.CANCEL_BITS;
			boolean isApproval = ((approvalBits & SDMSSubmittedEntity.CANCEL_APPROVAL) == SDMSSubmittedEntity.CANCEL_APPROVAL);
			boolean isReview = ((approvalBits & SDMSSubmittedEntity.CANCEL_REVIEW) == SDMSSubmittedEntity.CANCEL_REVIEW);
			int killApprovalBits = baseApprovalBits & SDMSSubmittedEntity.KILL_BITS;
			if (cancelWithKill) {
				approvalBits |= killApprovalBits;
				isApproval = isApproval || ((approvalBits & SDMSSubmittedEntity.KILL_APPROVAL) == SDMSSubmittedEntity.KILL_APPROVAL);
				isReview = isReview || ((approvalBits & SDMSSubmittedEntity.KILL_REVIEW) == SDMSSubmittedEntity.KILL_REVIEW);
			}
			boolean childApproval = (childApprovalMode & SDMSSubmittedEntity.CANCEL_APPROVAL) == SDMSSubmittedEntity.CANCEL_APPROVAL ||
			                        (cancelWithKill && (childApprovalMode & SDMSSubmittedEntity.KILL_APPROVAL) == SDMSSubmittedEntity.KILL_APPROVAL);
			boolean childReview = (childApprovalMode & SDMSSubmittedEntity.CANCEL_REVIEW) == SDMSSubmittedEntity.CANCEL_REVIEW ||
			                      (cancelWithKill && (childApprovalMode & SDMSSubmittedEntity.KILL_REVIEW) == SDMSSubmittedEntity.KILL_REVIEW);
			if (childApproval) {
				operationBits = SDMSSubmittedEntity.CANCEL_APPROVAL;
				if (cancelWithKill)
					operationBits |= SDMSSubmittedEntity.KILL_APPROVAL;
			} else {
				if (childReview) {
					operationBits = SDMSSubmittedEntity.CANCEL_REVIEW;
					if (cancelWithKill)
						operationBits |= SDMSSubmittedEntity.KILL_REVIEW;
				}
			}
			Long childSmeId = null;
			if (!isApproval) {
				if (childApproval || (!isReview && childReview))
					childSmeId = checkChildApprovals(sysEnv, sme, operationBits);
				if (childSmeId == null) {
					childApproval = false;
					childReview = false;
				}
			}

			if (isApproval || isReview || childApproval || childReview) {
				checkPendingApproval(sysEnv, sme, SDMSSystemMessage.CANCEL, "Cancel");
				String infomsg = "CANCEL" + (cancelWithKill ? " with KILL" : "");
				if (!(isApproval || (isReview && !childApproval)))
					infomsg = infomsg + " (" + (childApproval ? "approval" : "review") + " requested by a child job, such as " + childSmeId + ")";
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.CANCEL,
				                    isApproval || childApproval, sysEnv.cEnv.uid(), comment, null, cancelWithKill, null, infomsg);
			}
			if (!isApproval && !childApproval) {
				if (cancelWithKill)
					performKill(sysEnv, sme);
				performCancel(sysEnv, sme);
			}
		}

		if(kill != null) {
			if (!priv.can(SDMSPrivilege.KILL)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191549", "Insufficient privileges for kill"));
			}
			if(kill.booleanValue()) {
				boolean childApproval = false;
				boolean childReview = false;
				int approvalBits = baseApprovalBits & SDMSSubmittedEntity.KILL_BITS;
				boolean isApproval = ((approvalBits & SDMSSubmittedEntity.KILL_APPROVAL) == SDMSSubmittedEntity.KILL_APPROVAL);
				boolean isReview = ((approvalBits & SDMSSubmittedEntity.KILL_REVIEW) == SDMSSubmittedEntity.KILL_REVIEW);
				if(!killRecursive && SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getKillProgram(sysEnv) == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "032070111145", "couldn't kill, no kill program defined"));
				}
				if (killRecursive) {
					childApproval = ((childApprovalMode & SDMSSubmittedEntity.KILL_APPROVAL) == SDMSSubmittedEntity.KILL_APPROVAL);
					childReview = ((childApprovalMode & SDMSSubmittedEntity.KILL_REVIEW) == SDMSSubmittedEntity.KILL_REVIEW);
					if (childApproval) {
						operationBits = SDMSSubmittedEntity.KILL_APPROVAL;
					} else if (childReview) {
						operationBits = SDMSSubmittedEntity.KILL_REVIEW;
					}
				}
				Long childSmeId = null;
				if (!isApproval) {
					if (childApproval || (!isReview && childReview))
						childSmeId = checkChildApprovals(sysEnv, sme, operationBits);
					if (childSmeId == null) {
						childApproval = false;
						childReview = false;
					}
				}

				if (isApproval || isReview || childApproval || childReview) {
					checkPendingApproval(sysEnv, sme, SDMSSystemMessage.KILL, "Kill");
					String infomsg = "KILL" + (killRecursive ? " RECURSIVE" : "");
					if (!(isApproval || (isReview && !childApproval)))
						infomsg = infomsg + " (" + (childApproval ? "approval" : "review") + " requested by a child job, such as " + childSmeId + ")";
					createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.KILL,
					                    isApproval || childApproval, sysEnv.cEnv.uid(), comment, null, killRecursive, null, infomsg);
				}
				if (!isApproval && !childApproval) {
					performKill(sysEnv, sme);
				}
			}
		}

		if(disable != null) {
			if (!priv.can(SDMSPrivilege.ENABLE)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191552", "Insufficient privileges for enable/disable"));
			}
			boolean childApproval = false;
			boolean childReview = false;
			int approvalBits = baseApprovalBits & SDMSSubmittedEntity.ENABLE_BITS;
			boolean isApproval = ((approvalBits & SDMSSubmittedEntity.ENABLE_APPROVAL) == SDMSSubmittedEntity.ENABLE_APPROVAL);
			boolean isReview = ((approvalBits & SDMSSubmittedEntity.ENABLE_REVIEW) == SDMSSubmittedEntity.ENABLE_REVIEW);
			childApproval = ((childApprovalMode & SDMSSubmittedEntity.ENABLE_APPROVAL) == SDMSSubmittedEntity.ENABLE_APPROVAL);
			childReview = ((childApprovalMode & SDMSSubmittedEntity.ENABLE_REVIEW) == SDMSSubmittedEntity.ENABLE_REVIEW);
			if (childApproval) {
				operationBits = SDMSSubmittedEntity.ENABLE_APPROVAL;
			} else if (childReview) {
				operationBits = SDMSSubmittedEntity.ENABLE_REVIEW;
			}
			Long childSmeId = null;
			if (!isApproval) {
				if (childApproval || (!isReview && childReview))
					childSmeId = checkChildApprovals(sysEnv, sme, operationBits);
				if (childSmeId == null) {
					childApproval = false;
					childReview = false;
				}
			}
			if (isApproval || isReview || childApproval || childReview) {
				checkPendingApproval(sysEnv, sme, SDMSSystemMessage.ENABLE, "Enable");
				String infomsg = (disable ? "DISABLE" : "ENABLE") + (suspendFlag == null ? ", NOSUSPEND" : ((suspendFlag&1) == 1 ? ", SUSPEND" : ", RESUME"));
				if (localSuspend) infomsg = infomsg + " LOCAL";
				if (adminSuspend) infomsg = infomsg + " ADMIN";
				if (autoResumeVal != null) {
					if ((suspendFlag&1) == 1)
						infomsg = infomsg + ", RESUME";
					if (autoResumeVal > 0)
						infomsg = infomsg + " AT " + (String) resumeObj;
					else
						infomsg = infomsg + " IN " + (autoResumeVal.longValue() / -60000) + " minute(s)";
				}
				if (!(isApproval || (isReview && !childApproval)))
					infomsg = infomsg + " (" + (childApproval ? "approval" : "review") + " requested by a child job, such as " + childSmeId + ")";
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.ENABLE,
				                    isApproval || childApproval, sysEnv.cEnv.uid(), comment, suspendFlag, disable, autoResumeVal, infomsg);
			}
			if (!isApproval && !childApproval) {
				performDisable(sysEnv, sme, disable);
			} else {
				suspend = null;
				resumeObj = null;
			}
		}

		if(priority != null || nicevalue != null || renice != null) {
			if (!priv.can(SDMSPrivilege.PRIORITY)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191556", "Insufficient privileges for set priority"));
			}
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
			performPriority(sysEnv, sme, isRenice, priority);
		}

		if (clone != null) {
			if (!priv.can(SDMSPrivilege.CLONE)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191559", "Insufficient privileges for clone"));
			}
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
			}

			boolean childApproval = false;
			boolean childReview = false;
			int approvalBits = baseApprovalBits & SDMSSubmittedEntity.CLONE_BITS;
			boolean isApproval = ((approvalBits & SDMSSubmittedEntity.CLONE_APPROVAL) == SDMSSubmittedEntity.CLONE_APPROVAL);
			boolean isReview = ((approvalBits & SDMSSubmittedEntity.CLONE_REVIEW) == SDMSSubmittedEntity.CLONE_REVIEW);
			childApproval = ((childApprovalMode & SDMSSubmittedEntity.CLONE_APPROVAL) == SDMSSubmittedEntity.CLONE_APPROVAL);
			childReview = ((childApprovalMode & SDMSSubmittedEntity.CLONE_REVIEW) == SDMSSubmittedEntity.CLONE_REVIEW);
			if (childApproval) {
				operationBits = SDMSSubmittedEntity.CLONE_APPROVAL;
			} else if (childReview) {
				operationBits = SDMSSubmittedEntity.CLONE_REVIEW;
			}
			Long childSmeId = null;
			if (!isApproval) {
				if (childApproval || (!isReview && childReview))
					childSmeId = checkChildApprovals(sysEnv, sme, operationBits);
				if (childSmeId == null) {
					childApproval = false;
					childReview = false;
				}
			}
			if (isApproval || isReview || childApproval || childReview) {
				checkPendingApproval(sysEnv, sme, SDMSSystemMessage.CLONE, "Clone");
				if (clone.booleanValue() && suspendFlag == null) {
					suspendFlag &= 0x01l;
				}
				String infomsg = "CLONE " + (suspendFlag == null ? ", NOSUSPEND" : ((suspendFlag&1) == 1 ? ", SUSPEND" : ", RESUME"));
				if (localSuspend) infomsg = infomsg + " LOCAL";
				if (adminSuspend) infomsg = infomsg + " ADMIN";
				if (autoResumeVal != null) {
					if ((suspendFlag&1) == 1)
						infomsg = infomsg + ", RESUME";
					if (autoResumeVal > 0)
						infomsg = infomsg + " AT " + (String) resumeObj;
					else
						infomsg = infomsg + " IN " + (autoResumeVal.longValue() / -60000) + " minute(s)";
				}
				if (!(isApproval || (isReview && !childApproval)))
					infomsg = infomsg + " (" + (childApproval ? "approval" : "review") + " requested by a child job, such as " + childSmeId + ")";
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.CLONE,
				                    isApproval || childApproval, sysEnv.cEnv.uid(), comment, suspendFlag, null, autoResumeVal, infomsg);
			}
			if (!isApproval && !childApproval) {
				SDMSSubmittedEntity childSme = performClone(sysEnv, sme);
				if (!clone.booleanValue()) {
					childSme.resume(sysEnv, true);
				}
			} else {
				suspend = null;
				resumeObj = null;
			}
		}

		if(rerun != null) {
			if (!priv.can(SDMSPrivilege.RERUN)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191539", "Insufficient privileges for rerun"));
			}
			boolean childApproval = false;
			boolean childReview = false;
			int approvalBits = baseApprovalBits & SDMSSubmittedEntity.RERUN_BITS;
			boolean isApproval = ((approvalBits & SDMSSubmittedEntity.RERUN_APPROVAL) == SDMSSubmittedEntity.RERUN_APPROVAL);
			boolean isReview = ((approvalBits & SDMSSubmittedEntity.RERUN_REVIEW) == SDMSSubmittedEntity.RERUN_REVIEW);
			if (rerun) {
				childApproval = ((childApprovalMode & SDMSSubmittedEntity.RERUN_APPROVAL) == SDMSSubmittedEntity.RERUN_APPROVAL);
				childReview = ((childApprovalMode & SDMSSubmittedEntity.RERUN_REVIEW) == SDMSSubmittedEntity.RERUN_REVIEW);
			}
			if (childApproval) {
				operationBits = SDMSSubmittedEntity.RERUN_APPROVAL;
			} else if (childReview) {
				operationBits = SDMSSubmittedEntity.RERUN_REVIEW;
			}
			Long childSmeId = null;
			if (!isApproval) {
				if (childApproval || (!isReview && childReview))
					childSmeId = checkChildApprovals(sysEnv, sme, operationBits);
				if (childSmeId == null) {
					childApproval = false;
					childReview = false;
				}
			}
			if (isApproval || isReview || childApproval || childReview) {
				checkPendingApproval(sysEnv, sme, SDMSSystemMessage.RERUN, "Rerun");
				String infomsg = "RERUN " + (rerun ? "RECURSIVE" : "") + (suspendFlag == null ? ", NOSUSPEND" : ((suspendFlag&1) == 1 ? ", SUSPEND" : ", RESUME"));
				if (localSuspend) infomsg = infomsg + " LOCAL";
				if (adminSuspend) infomsg = infomsg + " ADMIN";
				if (autoResumeVal != null) {
					if ((suspendFlag&1) == 1)
						infomsg = infomsg + ", RESUME";
					if (autoResumeVal > 0)
						infomsg = infomsg + " AT " + (String) resumeObj;
					else
						infomsg = infomsg + " IN " + (autoResumeVal.longValue() / -60000) + " minute(s)";
				}
				if (!(isApproval || (isReview && !childApproval)))
					infomsg = infomsg + " (" + (childApproval ? "approval" : "review") + " requested by a child job, such as " + childSmeId + ")";
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.RERUN,
				                    isApproval || childApproval, sysEnv.cEnv.uid(), comment, suspendFlag, rerun, autoResumeVal, infomsg);
			}
			if (!isApproval && !childApproval) {
				performRerun(sysEnv, sme, rerun);
			} else {
				suspend = null;
				resumeObj = null;
			}
		}

		if (noResume) {
			sme.setResumeTs(sysEnv, null);
		}
		if(suspend != null || resumeObj != null) {
			if (priv.can(SDMSPrivilege.SUSPEND)) {
				if (suspend != null && !suspend.booleanValue() && sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.ADMINSUSPEND && !adminSuspend.booleanValue()) {
					System.err.println("ADMINSUSPEND = " + adminSuspend);
					System.err.println("SUSPEND = " + suspend);
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03408080757", "Insufficient privileges for admin resume"));
				}
				evalSuspend(sysEnv, suspendFlag, autoResumeVal, sme);
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03105191535", "Insufficient privileges for suspend"));
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
		boolean childApproval = false;
		boolean childReview = false;
		int childApprovalMode = sme.getChildApprovalMode(sysEnv);
		int approvalBits = sme.getApprovalMode(sysEnv).intValue() & SDMSSubmittedEntity.IGN_DEP_BITS;
		boolean isApproval = ((approvalBits & SDMSSubmittedEntity.IGN_DEP_APPROVAL) == SDMSSubmittedEntity.IGN_DEP_APPROVAL);
		boolean isReview = ((approvalBits & SDMSSubmittedEntity.IGN_DEP_REVIEW) == SDMSSubmittedEntity.IGN_DEP_REVIEW);

		for(int i = 0; i < depsToIgnore.size(); i++) {
			v = (Vector) depsToIgnore.get(i);
			Long diId = (Long) v.get(0);
			Boolean rec = (Boolean) v.get(1);
			childApproval = false;
			childReview = false;
			SDMSDependencyInstance di = SDMSDependencyInstanceTable.getObject(sysEnv, diId);
			Long childSmeId = null;
			int operationBits = 0;
			if (rec) {
				childApproval = ((childApprovalMode & SDMSSubmittedEntity.IGN_DEP_APPROVAL) == SDMSSubmittedEntity.IGN_DEP_APPROVAL);
				childReview = ((childApprovalMode & SDMSSubmittedEntity.IGN_DEP_REVIEW) == SDMSSubmittedEntity.IGN_DEP_REVIEW);
				if (childApproval) {
					operationBits = SDMSSubmittedEntity.IGN_DEP_APPROVAL;
				} else if (childReview) {
					operationBits = SDMSSubmittedEntity.IGN_DEP_REVIEW;
				}
				if (!isApproval) {
					if (childApproval || (!isReview && childReview))
						childSmeId = checkChildApprovals(sysEnv, sme, operationBits);
					if (childSmeId == null) {
						childApproval = false;
						childReview = false;
					}
				}
			}
			if (isApproval || isReview || childApproval || childReview) {
				SDMSSubmittedEntity rsme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getRequiredId(sysEnv));
				SDMSSchedulingEntity rse = SDMSSchedulingEntityTable.getObject(sysEnv, rsme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
				String infomsg = "Ignore Dependency " + (rec ? "RECURSIVE" : "") + " of " + rsme.getId(sysEnv) + " (" + rse.pathString(sysEnv) + ")";
				if (!(isApproval || (isReview && !childApproval)))
					infomsg = infomsg + " (" + (childApproval ? "approval" : "review") + " requested by a child job, such as " + childSmeId + ")";
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.IGN_DEPENDENCY,
				                    isApproval || childApproval, sysEnv.cEnv.uid(), comment, di.getId(sysEnv), rec, null, infomsg);
			}
			if (!isApproval && !childApproval) {
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

			SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, ra.getNrId(sysEnv));
			if(se.checkParameterRI(sysEnv, ra.getNrId(sysEnv))) {
				throw new CommonErrorException(
					new SDMSMessage(sysEnv, "03409291444", "You cannot ignore resource $1, parameter references exist",
						nr.pathString(sysEnv)
					)
				);
			}
			int approvalBits = sme.getApprovalMode(sysEnv).intValue() & SDMSSubmittedEntity.IGN_RSS_BITS;
			boolean isApproval = ((approvalBits & SDMSSubmittedEntity.IGN_RSS_APPROVAL) == SDMSSubmittedEntity.IGN_RSS_APPROVAL);
			if (approvalBits != 0) {
				String info = "Ignore Resource " + rId.toString() + " (" + nr.pathString(sysEnv) + ")";;
				createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.IGN_RESOURCE,
				                    isApproval, sysEnv.cEnv.uid(), comment, rId, null, null, info);
			}
			if (!isApproval) {
				ra.ignore(sysEnv);
			}
		}
		SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.IGNORE_RESOURCE);
	}

}

