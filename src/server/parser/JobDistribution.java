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
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.cmdline.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.jobserver.RepoIface;
import de.independit.scheduler.jobserver.Config;

public abstract class JobDistribution extends Node
{

	public static final String __version = "@(#) $Id: JobDistribution.java,v 2.15.4.1 2013/03/14 10:24:34 ronald Exp $";

	public JobDistribution()
	{
		super();
		cmdtype = Node.SERVER_COMMAND;
	}

	public abstract void go(SystemEnvironment sysEnv)
		throws SDMSException;

	boolean startJob(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSScope s, Vector desc, Vector data)
		throws SDMSException
	{
		boolean rc;
		Long smeId = sme.getId(sysEnv);
		Long sId = s.getId(sysEnv);
		long actVersion;
		boolean rerun = false;
		SDMSSchedulingEntity se = null;
		SDMSRunnableQueue rq = null;
		String tmpStr;

		data.add(RepoIface.CMD_STARTJOB);

		desc.add(RepoIface.STARTJOB_ID);
		desc.add(RepoIface.STARTJOB_DIR);
		desc.add(RepoIface.STARTJOB_LOG);
		desc.add(RepoIface.STARTJOB_LOGAPP);
		desc.add(RepoIface.STARTJOB_ERR);
		desc.add(RepoIface.STARTJOB_ERRAPP);
		desc.add(RepoIface.STARTJOB_CMD);
		desc.add(RepoIface.STARTJOB_ARGS);
		desc.add(RepoIface.STARTJOB_ENV);
		desc.add(RepoIface.STARTJOB_RUN);
		desc.add(RepoIface.STARTJOB_JOBENV);

		actVersion = sme.getSeVersion(sysEnv).longValue();

		se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), actVersion);
		data.add(smeId);

		sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.STARTING));

		Vector args;
		String runProgram;

		if(sme.getRerunSeq(sysEnv).intValue() > 0) {
			rerun = true;
			runProgram = se.getRerunProgram(sysEnv);
			if(runProgram == null || runProgram.length() == 0) {
				runProgram = se.getRunProgram(sysEnv);
			}
		} else {
			runProgram = se.getRunProgram(sysEnv);
			if(runProgram == null) {
				sysEnv.tx.rollbackSubTransaction(sysEnv);
				sme.setToError(sysEnv, "Missing run program");
				delFromQueue(sysEnv, smeId);
				return false;
			}
		}

		sme.setScopeId(sysEnv, sId);

		StringReader sr;

		String workdir = se.getWorkdir(sysEnv);
		if(workdir != null) workdir = workdir.trim();
		String logfile = se.getLogfile(sysEnv);
		if(logfile != null) logfile = logfile.trim();
		String errlogfile = se.getErrlogfile(sysEnv);
		if(errlogfile != null) errlogfile = errlogfile.trim();
		Boolean tmpb;

		try {
			if(workdir != null) {
				sr = new StringReader(workdir);
				args = cmdlineScan(sysEnv, sr, sme, sme, smeId, "Working Directory", "Working Directory Name is empty");
				workdir = (String) args.get(0);
			} else {
				Long cfgScopeId = sId;
				while(true) {
					try {
						SDMSScopeConfig sc = (SDMSScopeConfig)
							SDMSScopeConfigTable.idx_scopeId_key.getUnique(sysEnv, new SDMSKey(cfgScopeId, Config.DEFAULT_WORKDIR));
						workdir = sc.getValue(sysEnv).substring(1);
						break;
					} catch (NotFoundException nfe) {
						SDMSScope cfgS = SDMSScopeTable.getObject(sysEnv, cfgScopeId);
						cfgScopeId = cfgS.getParentId(sysEnv);
						if (cfgScopeId == null) {
							sysEnv.tx.rollbackSubTransaction(sysEnv);
							sme.setToError(sysEnv, "Default workdir not defined");
							delFromQueue(sysEnv, smeId);
							return false;
						}
					}
				}
			}
			data.add(workdir);
			sme.setWorkdir(sysEnv, workdir);

			if(logfile != null) {
				sr = new StringReader(logfile);
				args = cmdlineScan(sysEnv, sr, sme, sme, smeId, "Logfile", "Logfile Name is empty");
				logfile = (String) args.get(0);
			}
			data.add(logfile);
			tmpStr = logfile;
			sme.setLogfile(sysEnv, logfile);
			tmpb = se.getTruncLog(sysEnv);
			if(rerun)	data.add(new Boolean(! SDMSSchedulingEntity.NOTRUNC));
			else		data.add(tmpb == null ? new Boolean(! SDMSSchedulingEntity.NOTRUNC) : new Boolean(! tmpb.booleanValue()));

			if(errlogfile != null) {
				sr = new StringReader(errlogfile);
				args = cmdlineScan(sysEnv, sr, sme, sme, smeId, "Error Logfile", "Error Logfile Name is empty");
				errlogfile = (String) args.get(0);
			}
			data.add(errlogfile);
			tmpStr = errlogfile;
			sme.setErrlogfile(sysEnv, errlogfile);
			tmpb = se.getTruncErrlog(sysEnv);
			if(rerun)	data.add(new Boolean(! SDMSSchedulingEntity.NOTRUNC));
			else		data.add(tmpb == null ? new Boolean(! SDMSSchedulingEntity.NOTRUNC) : new Boolean(! tmpb.booleanValue()));
		} catch (CommonErrorException cce) {
			return false;
		}

		sr = new StringReader(runProgram);
		try {
			args = cmdlineScan(sysEnv, sr, sme, sme, smeId, "run/rerun Program", "Run Program is missing");
		} catch (CommonErrorException cce) {
			return false;
		}

		String cmd = (String) args.get(0);
		data.add(cmd);
		args.remove(0);
		data.add(args);

		String cmdLine = renderCmdLine(cmd, args);
		if(rerun) {
			sme.setRrCommandline(sysEnv, cmdLine);
		} else {
			sme.setCommandline(sysEnv, cmdLine);
		}

		fillEnvironment(sysEnv, sme, data);

		Vector jsv = SDMSRunnableQueueTable.idx_smeId.getVectorForUpdate(sysEnv, smeId);
		for(int i = 0; i < jsv.size(); i++) {
			rq = (SDMSRunnableQueue) jsv.get(i);
			if(rq.getScopeId(sysEnv).equals(sId)) {
				rq.setState(sysEnv, new Integer(SDMSSubmittedEntity.STARTING));
				continue;
			}
			rq.delete(sysEnv);
		}
		data.add(sme.getRerunSeq(sysEnv));

		Vector jobenv = new Vector();
		Vector jpv = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, se.getId(sysEnv));
		Iterator jpi = jpv.iterator();
		while(jpi.hasNext()) {
			SDMSParameterDefinition pd = (SDMSParameterDefinition)jpi.next();
			String exportName = pd.getExportName(sysEnv);
			if (exportName != null) {
				jobenv.add(exportName);
				jobenv.add(sme.getVariableValue(sysEnv, pd.getName(sysEnv), false, ParseStr.S_DEFAULT));
			}
		}
		data.add(jobenv);

		try {
			int exitcode = Integer.parseInt(cmd);
			sme.finishJob(sysEnv, exitcode, null, null );
			rc = false;
		} catch (NumberFormatException nfe) {
			rc = true;
		}

		sysEnv.tx.commitSubTransaction(sysEnv);

		return rc;
	}

	boolean	startKillJob(SystemEnvironment sysEnv, SDMSKillJob kj, SDMSScope s, Vector desc, Vector data)
		throws SDMSException
	{
		Long kjId;
		Long sId = s.getId(sysEnv);
		long actVersion;
		SDMSSchedulingEntity se = null;
		SDMSSubmittedEntity sme = null;
		SDMSRunnableQueue rq = null;
		String tmpStr;

		data.add(RepoIface.CMD_STARTJOB);

		desc.add(RepoIface.STARTJOB_ID);
		desc.add(RepoIface.STARTJOB_DIR);
		desc.add(RepoIface.STARTJOB_LOG);
		desc.add(RepoIface.STARTJOB_LOGAPP);
		desc.add(RepoIface.STARTJOB_ERR);
		desc.add(RepoIface.STARTJOB_ERRAPP);
		desc.add(RepoIface.STARTJOB_CMD);
		desc.add(RepoIface.STARTJOB_ARGS);
		desc.add(RepoIface.STARTJOB_ENV);
		desc.add(RepoIface.STARTJOB_RUN);
		desc.add(RepoIface.STARTJOB_JOBENV);

		kjId = kj.getId(sysEnv);

		actVersion = kj.getSeVersion(sysEnv).longValue();

		se = SDMSSchedulingEntityTable.getObject(sysEnv, kj.getSeId(sysEnv), actVersion);
		sme = SDMSSubmittedEntityTable.getObject(sysEnv, kj.getSmeId(sysEnv));
		data.add(kjId);

		Vector args;
		String runProgram = kj.getCommandline(sysEnv);

		if(runProgram == null) {
			sysEnv.tx.rollbackSubTransaction(sysEnv);
			kj.setErrorMsg(sysEnv, "Missing run program");
			delFromQueue(sysEnv, kjId);
			return false;
		}

		StringReader sr;

		String workdir = se.getWorkdir(sysEnv);
		if(workdir != null) workdir = workdir.trim();
		String logfile = kj.getLogfile(sysEnv);
		if(logfile != null) logfile = logfile.trim();
		String errlogfile = kj.getErrlogfile(sysEnv);
		if(errlogfile != null) errlogfile = errlogfile.trim();
		if(workdir != null) {
			sr = new StringReader(workdir);
			try {
				args = cmdlineScan(sysEnv, sr, sme, kj, kjId, "workdir", "Working Directory Name is empty");
			} catch (CommonErrorException cce) {
				return false;
			}
			workdir = (String) args.get(0);
		}
		data.add(workdir);

		logfile = kj.getLogfile(sysEnv);
		data.add(logfile);
		data.add(new Boolean(! SDMSSchedulingEntity.NOTRUNC));

		errlogfile = kj.getErrlogfile(sysEnv);
		data.add(errlogfile);
		data.add(new Boolean(! SDMSSchedulingEntity.NOTRUNC));

		sr = new StringReader(runProgram);
		try {
			args = cmdlineScan(sysEnv, sr, sme, kj, kjId, "run/rerun Program", "Run Program missing");
		} catch (CommonErrorException cce) {
			return false;
		}

		String cmd = (String) args.get(0);
		data.add(cmd);
		args.remove(0);
		data.add(args);

		String cmdLine = renderCmdLine(cmd, args);
		kj.setCommandline(sysEnv, cmdLine);

		fillEnvironment(sysEnv, sme, data);

		data.add(new Integer(0));

		kj.setState(sysEnv, new Integer(SDMSSubmittedEntity.STARTING));
		kj.setScopeId(sysEnv, sId);

		Vector jobenv = new Vector();
		Vector jpv = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, se.getId(sysEnv));
		Iterator jpi = jpv.iterator();
		while(jpi.hasNext()) {
			SDMSParameterDefinition pd = (SDMSParameterDefinition)jpi.next();
			String exportName = pd.getExportName(sysEnv);
			if (exportName != null) {
				jobenv.add(exportName);
				jobenv.add(sme.getVariableValue(sysEnv, pd.getName(sysEnv), false, ParseStr.S_DEFAULT));
			}
		}

		data.add(jobenv);
		Vector jsv = SDMSRunnableQueueTable.idx_smeId.getVector(sysEnv, kjId);
		for(int i = 0; i < jsv.size(); i++) {
			rq = (SDMSRunnableQueue) jsv.get(i);
			if(rq.getScopeId(sysEnv).equals(sId)) {
				rq.setState(sysEnv, new Integer(SDMSSubmittedEntity.STARTING));
				continue;
			}
			rq.delete(sysEnv);
		}

		sysEnv.tx.commitSubTransaction(sysEnv);
		return true;
	}

	private Vector cmdlineScan(SystemEnvironment sysEnv, StringReader sr, SDMSSubmittedEntity sme, SDMSProxy job, Long id, String parseObject, String nullMessage)
		throws SDMSException
	{
		Vector args = null;
		SDMSMessage msg = null;
		boolean err = true;

		try {
			CmdLineScanner cmds = new CmdLineScanner(sr);
			CmdLineParser cmdp = new CmdLineParser();
			cmdp.set(sysEnv, sme);
			args = (Vector) cmdp.yyparse(cmds);
			if (args == null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03808071039", "String is empty"));
			}
			err = false;
		} catch (IOException ioe) {
			msg = new SDMSMessage(sysEnv, "03207100213", "I/O Error parsing $1", parseObject);
		} catch (NotFoundException nfe) {
			msg = new SDMSMessage(sysEnv, "03209161709", "Error resolving Parameter: $1", nfe);
		} catch (CommonErrorException cce) {
			msg = new SDMSMessage(sysEnv, "03302291400", "Error parsing $1: $2", parseObject, cce);
		} catch (de.independit.scheduler.server.parser.cmdline.CmdLineParser.yyException yye) {
			msg = new SDMSMessage(sysEnv, "03207100214", "Parse error while parsing $1", parseObject);
		} catch (Error e) {
			msg = new SDMSMessage(sysEnv, "03310101726", "Error Exception parsing $1", parseObject);
		} catch (Exception e) {
			msg = new SDMSMessage(sysEnv, "03808071015", "Exception parsing $1", parseObject);
		}
		if(args == null && nullMessage != null && msg == null) {
			msg = new SDMSMessage(sysEnv, "03207100212", nullMessage);
		}
		if(err) {
			setToError(sysEnv, job, id, msg);
			throw new CommonErrorException(msg);
		}
		return args;
	}

	void setToError(SystemEnvironment sysEnv, SDMSProxy job, Long id, SDMSMessage errMsg)
		throws SDMSException
	{
		if(sysEnv.tx.subTxId > 0)
			sysEnv.tx.rollbackSubTransaction(sysEnv);
		if(job instanceof SDMSSubmittedEntity) {
			((SDMSSubmittedEntity) job).setToError(sysEnv, errMsg.toString());
		} else {
			((SDMSKillJob) job).setToError(sysEnv, errMsg.toString());
		}
		delFromQueue(sysEnv, id);
	}

	String renderCmdLine(String cmd, Vector args)
	{
		StringBuffer sb = new StringBuffer();

		sb.append(cmd);
		for(int i = 0; i < args.size(); i++) {
			sb.append(" \"");
			sb.append((String) args.get(i));
			sb.append("\"");
		}
		return new String(sb);
	}

	void delFromQueue(SystemEnvironment sysEnv, Long smeId)
		throws SDMSException
	{
		SDMSRunnableQueue rq;
		Vector v = SDMSRunnableQueueTable.idx_smeId.getVector(sysEnv, smeId);
		for(int i = 0; i < v.size(); i++) {
			rq = (SDMSRunnableQueue) v.get(i);
			rq.delete(sysEnv);
		}
	}

	void fillEnvironment(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, Vector data)
		throws SDMSException
	{
		Vector env = new Vector();

		for(int i=0; i < SystemEnvironment.exportVariables.size(); i++) {
			env.add(SystemEnvironment.exportVariables.get(i));
			try {
				env.add(sme.getVariableValue(sysEnv, (String) SystemEnvironment.exportVariables.get(i), false, ParseStr.S_DEFAULT));
			} catch(NotFoundException cee) {
				env.add("");
			}
		}
		data.add(env);
	}

	void fillEnvironment(SystemEnvironment sysEnv, SDMSKillJob kj, Vector data)
		throws SDMSException
	{
		Vector env = new Vector();
		for(int i=0; i < SystemEnvironment.exportVariables.size(); i++) {
			env.add(SystemEnvironment.exportVariables.get(i));
			try {
				env.add(kj.getVariableValue(sysEnv, (String) SystemEnvironment.exportVariables.get(i)));
			} catch(NotFoundException cee) {
				env.add("");
			}
		}
		data.add(env);
	}
}

