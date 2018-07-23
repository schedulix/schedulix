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

public class AlterJob extends Node
{

	public final static String __version = "@(#) $Id: AlterJob.java,v 2.35.2.4 2013/07/19 06:45:20 dieter Exp $";

	public static final int LOCALSUSPEND      = 100;
	public static final int LOCALADMINSUSPEND = 200;

	private Long jobId		= null;
	private WithHash with		= null;
	private String execPid		= null;
	private String extPid		= null;
	private Integer status		= null;
	private String exitState	= null;
	private Boolean exitStateForce	= null;
	private String ts		= null;
	private Integer exitCode	= null;
	private String errText		= null;
	private Boolean suspend		= null;
	private Boolean adminSuspend	= null;
	private Boolean localSuspend	= null;
	private Object resumeObj	= null;
	private String runProgram	= null;
	private String rerunProgram	= null;
	private Boolean rerun		= null;
	private Integer rerunSeq	= null;
	private Boolean kill		= null;
	private Boolean cancel		= null;
	private Long tsLong		= null;
	private Vector depsToIgnore	= null;
	private Integer priority	= null;
	private Integer nicevalue	= null;
	private Integer renice		= null;
	private String comment		= null;
	private Vector resToIgnore	= null;
	private Vector nrsToIgnore	= null;
	private Boolean clearWarning	= null;
	private String warning		= null;

	private static final int EP = 0x000001;
	private static final int EX = 0x000002;
	private static final int ST = 0x000004;
	private static final int TS = 0x000008;
	private static final int EC = 0x000010;
	private static final int RP = 0x000020;
	private static final int RR = 0x000040;
	private static final int SU = 0x000080;
	private static final int RE = 0x000100;
	private static final int ID = 0x000200;
	private static final int ES = 0x000400;
	private static final int KI = 0x000800;
	private static final int CN = 0x001000;
	private static final int RC = 0x002000;
	private static final int ET = 0x004000;
	private static final int PR = 0x008000;
	private static final int NV = 0x010000;
	private static final int RN = 0x020000;
	private static final int IR = 0x040000;
	private static final int IN = 0x040000;
	private static final int RU = 0x080000;
	private static final int CW = 0x100000;
	private static final int SW = 0x200000;
	private static final int RS = 0x400000;

	private static final int JS_ACTION = EP|EX|ST|TS|EC|ET|RU;
	private static final int OP_ACTION = RP|RR|SU|RE|ID|ET|PR|NV|IR|IN|CW|SW|RS;
	private static final int ES_ACTION = ES|ET|RU;
	private static final int KI_ACTION = KI|ET;
	private static final int CN_ACTION = CN|ET;
	private static final int RC_ACTION = RC|ET;

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
		   ((v & ~KI_ACTION) == 0) ||
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
			tsLong = new Long(gc.getTimeInMillis());
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
		cancel = (Boolean) with.get(ParseStr.S_CANCEL);
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

	private void setSomeFields(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Integer status)
		throws SDMSException
	{
		sme.setState(sysEnv, status);
		if(with.containsKey(ParseStr.S_ERROR_TEXT))	sme.setErrorMsg(sysEnv, errText);
		if(with.containsKey(ParseStr.S_EXEC_PID))	sme.setPid(sysEnv, execPid);
		if(with.containsKey(ParseStr.S_EXT_PID))	sme.setExtPid(sysEnv, extPid);
	}

	private void changeState(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, boolean force)
		throws SDMSException
	{
		switch(status.intValue()) {
			case SDMSSubmittedEntity.STARTED:
				setSomeFields(sysEnv, sme, status);
				if(tsLong != null)
					sme.setStartTs(sysEnv, tsLong);
				delFromQueue(sysEnv, sme);
				break;
			case SDMSSubmittedEntity.RUNNING:
				int oldState = sme.getState(sysEnv);
				setSomeFields(sysEnv, sme, status);
				if (oldState != SDMSSubmittedEntity.STARTED && tsLong != null)
					sme.setStartTs(sysEnv, tsLong);
				delFromQueue(sysEnv, sme);
				break;
			case SDMSSubmittedEntity.FINISHED:
				if(exitCode == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv,
							"03212102041", "you cannot finish a job without an exit code"));
				}
				sme.finishJob(sysEnv, exitCode, errText, tsLong);

				int newState = sme.getState(sysEnv);
				if (newState == SDMSSubmittedEntity.FINISHED || newState == SDMSSubmittedEntity.FINAL)
					delFromQueue(sysEnv, sme);
				break;
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				setSomeFields(sysEnv, sme, status);
				break;
			case SDMSSubmittedEntity.BROKEN_FINISHED:
				sme.releaseResources(sysEnv, status.intValue());
				setSomeFields(sysEnv, sme, status);
				break;
			case SDMSSubmittedEntity.ERROR:
				sme.releaseResources(sysEnv, status.intValue());
				sme.setToError(sysEnv, errText);
				break;
			default:
				if(force) {
					sme.setState(sysEnv, status);
				}
				break;
		}
	}

	private void setExitState(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long actVersion)
		throws SDMSException
	{
		String oldExitState;

		Long esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, exitState, actVersion).getId(sysEnv);
		Long espId = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getEspId(sysEnv);
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		SDMSExitState es;
		int state = sme.getState(sysEnv).intValue();

		if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03403091701", "You can only change the exit state of a job, $1 is a $2",
						se.pathString(sysEnv), se.getTypeAsString(sysEnv)));
		}

		boolean isSuspended = (sme.getIsSuspended(sysEnv).intValue() != SDMSSubmittedEntity.NOSUSPEND);
		if((state != SDMSSubmittedEntity.FINISHED && state != SDMSSubmittedEntity.BROKEN_FINISHED && state != SDMSSubmittedEntity.ERROR && !isSuspended) ||
		   (state == SDMSSubmittedEntity.ERROR && !sme.getJobIsRestartable(sysEnv).booleanValue()) ||
		   (isSuspended && (state == SDMSSubmittedEntity.STARTING || state == SDMSSubmittedEntity.STARTED ||
				    state == SDMSSubmittedEntity.RUNNING || state == SDMSSubmittedEntity.TO_KILL ||
				    state == SDMSSubmittedEntity.KILLED || state == SDMSSubmittedEntity.BROKEN_ACTIVE))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207082043", "you can only set a state for a (broken) finished  or a suspended not active job"));
		}
		if(state != SDMSSubmittedEntity.BROKEN_FINISHED  && state != SDMSSubmittedEntity.ERROR) {
			try {
				Long jobEsdId = sme.getJobEsdId(sysEnv);
				if (jobEsdId != null) {
					es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, jobEsdId), actVersion);
					if(sme.getJobIsFinal(sysEnv).booleanValue()) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03207082044",
										"you can only set a state for a job in a nonfinal state"));
					}
					oldExitState = SDMSExitStateDefinitionTable.getObject(sysEnv, sme.getJobEsdId(sysEnv), actVersion).getName(sysEnv);
				} else {
					oldExitState = "N/A";
				}
			} catch(NotFoundException nfe) {
				throw new FatalException(new SDMSMessage(sysEnv, "03207082059", "Actual exit state is not part of the profile"));
			}
		} else {
			oldExitState = "N/A";
		}
		try {
			es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, esdId), actVersion);
		} catch(NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207051918", "Exit State $1 is not part of the profile", exitState));
		}

		if(!exitStateForce.booleanValue()) {
			Long esmpId = se.getEsmpId(sysEnv);
			if(esmpId == null) {
				SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, espId, actVersion);
				esmpId = esp.getDefaultEsmpId(sysEnv);
				if(esmpId == null) {
					SDMSMessage m = new SDMSMessage(sysEnv, "03403090928",
										"Couldn't determine the exit state mapping for job definition $1",
										se.pathString(sysEnv));
					SDMSThread.doTrace(sysEnv.cEnv, m.toString(), SDMSThread.SEVERITY_ERROR);
					throw new CommonErrorException(m);
				}
			}
			if(!SDMSExitStateMappingTable.idx_esmpId_esdId.containsKey(sysEnv, new SDMSKey(esmpId, esdId), actVersion)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03403090958",
							"A mapping to exit state $1 doesn't exist, use force if you really want this", exitState));
			}
		}

		sme.changeState(sysEnv, esdId, es, sme.getExitCode(sysEnv), errText, null, false);
	}

	private void alterByJob(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{

		long actVersion = sme.getSeVersion(sysEnv).longValue();

		if(exitState != null) {
			setExitState(sysEnv, sme, actVersion);
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
				if(SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getKillProgram(sysEnv) == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "032070111144", "couldn't kill, no kill program defined"));
				}
				sme.kill(sysEnv);
			}
		}
		return;
	}

	private void alterByJobserver(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long actVersion)
		throws SDMSException
	{
		auditFlag = false;
		Long sId = sme.getScopeId(sysEnv);
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
		if(!sysEnv.cEnv.uid().equals(sId)) {
			result.setFeedback(new SDMSMessage(sysEnv, "03205141710", "Job altered"));
			return;
		}
		if(status != null) {
			changeState(sysEnv, sme, false);
		}
		SDMSScope s = SDMSScopeTable.getObjectForUpdate(sysEnv, sId);
		s.setLastActive(sysEnv, new Long(sysEnv.cEnv.last()));
	}

	private void alterByOperator(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long actVersion)
		throws SDMSException
	{
		if(status != null) {
			String oldState = sme.getStateAsString(sysEnv);
			changeState(sysEnv, sme, true);
		}
		if(exitState != null) {
			setExitState(sysEnv, sme, actVersion);
		}
		Long resumeTs = null;
		if(resumeObj != null) {
			resumeTs = SubmitJob.evalResumeObj(sysEnv, resumeObj, null, true);

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
			if(suspend.booleanValue()) {
				sme.suspend(sysEnv, localSuspend.booleanValue(), adminSuspend.booleanValue());
				SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.SUSPEND);
			} else {
				if (sme.getIsSuspended(sysEnv).intValue() == SDMSSubmittedEntity.ADMINSUSPEND && !adminSuspend.booleanValue())
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03408080757", "Insufficient privileges for admin resume"));
				sme.resume(sysEnv, adminSuspend.booleanValue());
				SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.RESUME);
			}
		}
		if(resumeObj != null) {
			Date d = new Date(resumeTs);
			sme.setResumeTs(sysEnv, resumeTs);
		}
		if(rerun != null) {
			if(rerun.booleanValue()) {
				sme.rerunRecursive(sysEnv, jobId, comment, true);
			} else {
				sme.rerun(sysEnv);
			}
		}
		if(kill != null) {
			if(kill.booleanValue()) {
				if(SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getKillProgram(sysEnv) == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "032070111144", "couldn't kill, no kill program defined"));
				}
				sme.kill(sysEnv);
			}
		}
		if(cancel != null) {
			if(cancel.booleanValue()) {
				sme.cancel(sysEnv);
			}
		}
		if(depsToIgnore != null) {
			ignoreDeps(sysEnv, sme);
		}
		if(nrsToIgnore != null) {
			ignoreNamedResources(sysEnv, sme);
			SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.IGNORE_RESOURCE);
		}
		if(resToIgnore != null) {
			ignoreResources(sysEnv, sme);
			SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.IGNORE_RESOURCE);
		}
		if(priority != null) {
			if(SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion).getType(sysEnv).intValue()
				!= SDMSSchedulingEntity.JOB) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03211211229", "Cannot change the priority of a batch or milestone"));
			}

			if(priority.intValue() < SystemEnvironment.priorityLowerBound && !sysEnv.cEnv.gid().contains(SDMSObject.adminGId))
				priority = new Integer(SystemEnvironment.priorityLowerBound);
			sme.setPriority(sysEnv, priority);
		}
		if(nicevalue != null) {
			sme.renice(sysEnv, nicevalue, null, comment);
		}
		if(renice != null) {
			int nv = renice.intValue() + sme.getNice(sysEnv).intValue();
			sme.renice(sysEnv, new Integer(nv), null, comment);
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
					sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.KILLED));
				}
				break;
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				break;
			case SDMSSubmittedEntity.BROKEN_FINISHED:
				if(s == SDMSSubmittedEntity.TO_KILL) {
					sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.KILLED));
				}
				break;
			case SDMSSubmittedEntity.ERROR:
				if(s == SDMSSubmittedEntity.TO_KILL) {
					sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.RUNNING));
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
			di.setIgnore(sysEnv, (rec.booleanValue()? SDMSDependencyInstance.RECURSIVE : SDMSDependencyInstance.YES),
					jobId, comment);
		}
	}

	private void ignoreNamedResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
	}

	private void ignoreResources(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		Long rId;
		SDMSResourceAllocation ra = null;
		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv).longValue());
		Long smeId = sme.getId(sysEnv);
		boolean raFound = false;

		for(int i = 0; i < resToIgnore.size(); i++) {
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
			ra.ignore(sysEnv);
		}
	}

	private void delFromQueue(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		try {
			SDMSRunnableQueue rq = SDMSRunnableQueueTable.idx_smeId_scopeId_getUniqueForUpdate(sysEnv, new SDMSKey(sme.getId(sysEnv), sysEnv.cEnv.uid()));
			rq.delete(sysEnv);
		} catch (NotFoundException nfe) {
		}
	}

	private void delFromQueue(SystemEnvironment sysEnv, SDMSKillJob kj)
		throws SDMSException
	{
		try {
			SDMSRunnableQueue rq = SDMSRunnableQueueTable.idx_smeId_scopeId_getUnique(sysEnv, new SDMSKey(kj.getId(sysEnv), sysEnv.cEnv.uid()));
			rq.delete(sysEnv);
		} catch (NotFoundException nfe) {
		}
	}

}

