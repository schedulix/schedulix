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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.jobserver.Config;

public class Connect extends Node
{

	public final static String __version = "@(#) $Id: Connect.java,v 2.20.2.1 2013/03/14 10:24:24 ronald Exp $";

	public static final String JS_ALREADY_CONNECTED = "Server already connected";
	private final static Long zero = new Long(0);

	private String user;
	private String jsName;
	private String passwd;
	private String txtPasswd;
	private boolean isJobServer;
	private boolean isJob;
	private boolean isUser;
	private Vector path;
	private Long jobid;
	private WithHash withs;
	private final Node cmd;

	public Connect(String u, String p, WithHash wh)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		user = u;
		passwd = CheckSum.mkstr(CheckSum.md5(p.getBytes()));
		txtPasswd = p;
		isUser = true;
		isJobServer = false;
		jsName = null;
		isJob = false;
		jobid = null;
		path = null;
		withs = wh;
		cmd = (Node) withs.get(ParseStr.S_COMMAND);
		if (cmd == null) {
			txMode = SDMSTransaction.READONLY;
			auditFlag = false;
		} else {
			txMode = cmd.txMode;
			auditFlag = cmd.auditFlag;
		}
	}

	public Connect(Vector pth, String js, String p, WithHash wh)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		user = null;
		passwd = CheckSum.mkstr(CheckSum.md5(p.getBytes()));
		isUser = false;
		isJobServer = true;
		jsName = js;
		isJob = false;
		jobid = null;
		path = pth;
		withs = wh;
		cmd = (Node) withs.get(ParseStr.S_COMMAND);
		if (cmd == null) auditFlag = false;
		else		auditFlag = cmd.auditFlag;
	}

	public Connect(Long i, String p, WithHash wh)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		user = null;
		passwd = p;
		isUser = false;
		isJobServer = false;
		jsName = null;
		isJob = true;
		jobid = i;
		path = null;
		withs = wh;
		cmd = (Node) withs.get(ParseStr.S_COMMAND);
		if (cmd == null) {
			txMode = SDMSTransaction.READONLY;
			auditFlag = false;
		} else {
			txMode = cmd.txMode;
			auditFlag = cmd.auditFlag;
		}
	}

	private void connect_user(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSUser u;
		Long uId;

		try {

			u = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(user, zero));
			if (!u.getIsEnabled(sysEnv).booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv,
				                               "02110192355", "User disabled"));
			}
			if (user.toUpperCase().equals("SYSTEM")) {
				if(!txtPasswd.equals(SystemEnvironment.sysPasswd)) {
					throw new CommonErrorException(new SDMSMessage(sysEnv,
					                               "02110192352", "Invalid username or password"));
				}
			} else {
				if (!u.getPasswd(sysEnv).equals(passwd)) {
					throw new CommonErrorException(new SDMSMessage(sysEnv,
					                               "02110192352", "Invalid username or password"));
				}
			}
			if(sysEnv.getConnectState() != SystemEnvironment.NORMAL) {
				if(u.getId(sysEnv).intValue() != 0)

					throw new CommonErrorException(new SDMSMessage(sysEnv, "03202081739", "Login restricted"));
			}
		} catch (NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02110192350", "Invalid username or password"));
		}
		uId = u.getId(sysEnv);
		sysEnv.cEnv.setUid(uId);
		sysEnv.cEnv.setUser();
		sysEnv.cEnv.setGid(sysEnv, SDMSMemberTable.idx_uId.getVector(sysEnv, uId));
	}

	private void connect_jobserver(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSScope s;
		Long pId;
		int timeout;

		try {

			pId = SDMSScopeTable.pathToId(sysEnv, path);

			s = SDMSScopeTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(pId, jsName));
			if(s.getType(sysEnv).intValue() != SDMSScope.SERVER) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202041546",
				                               "Invalid jobservername or password"));
			}
			if(!s.getIsEnabled(sysEnv).booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv,
				                               "03202041508", "JobServer disabled"));
			}
			if(!s.getPasswd(sysEnv).equals(passwd)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv,
				                               "03202041511", "Invalid jobservername or password"));
			}
		} catch (NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "03202041510", "Invalid jobservername or password"));
		}

		SDMSnpSrvrSRFootprint sf = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, s.getId(sysEnv));
		if(s.isConnected(sysEnv)) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03204102020", JS_ALREADY_CONNECTED));
		} else {
			sf.setSessionId(sysEnv, new Integer(env.id()));
		}

		sysEnv.cEnv.setUid(s.getId(sysEnv));
		sysEnv.cEnv.setJobServer();
		try {
			timeout = Integer.parseInt(ScopeConfig.getItem(sysEnv, s, Config.NOP_DELAY));
			sysEnv.cEnv.getMe().setTimeout(timeout * 3);
		} catch (NumberFormatException nfe) {

			sysEnv.cEnv.getMe().setTimeout(300);
		}
	}

	public static Long validateJobConnect(SystemEnvironment sysEnv, Long jobId, String key, boolean adminAccess)
	throws SDMSException
	{
		Long accessKey;
		SDMSSubmittedEntity sme;

		try {
			try {
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobId);
			} catch (NotFoundException nfe) {
				SDMSKillJob kj = SDMSKillJobTable.getObject(sysEnv, jobId);
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, kj.getSmeId(sysEnv));
			}
			try {
				accessKey = new Long(Long.parseLong(key));
			} catch (NumberFormatException nfe) {
				throw new CommonErrorException(new SDMSMessage(sysEnv,
				                               "03206031607", "Invalid username or password"));
			}
			if (!sme.getAccessKey(sysEnv).equals(accessKey)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv,
				                               "02110192353", "Invalid username or password"));
			}
		} catch (NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02110192351", "Invalid username or password"));
		}

		if (!adminAccess) {

			int state = sme.getState(sysEnv).intValue();
			if (state == SDMSSubmittedEntity.CANCELLED || state == SDMSSubmittedEntity.FINAL)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03703141511",
				                               "Invalid username or password"));
		}

		return sme.getId(sysEnv);
	}

	private void connect_job(SystemEnvironment sysEnv)
	throws SDMSException
	{
		sysEnv.cEnv.setUid(validateJobConnect(sysEnv, jobid, passwd, false));
		sysEnv.cEnv.setJob();
	}

	public Node getNode()
	{
		return cmd;
	}

	public String getName()
	{
		String s = this.getClass().getName();
		if (cmd != null)
			s = cmd.getClass().getName();
		return s.substring(s.lastIndexOf('.')+1);
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Vector data = new Vector();
		if (isUser) {

			connect_user(sysEnv);
		} else {
			if(sysEnv.getConnectState() != SystemEnvironment.NORMAL) {

				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202081740", "Login restricted"));
			}
			if(isJobServer) {

				connect_jobserver(sysEnv);
			} else {
				if(isJob) {

					connect_job(sysEnv);
				} else {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03406282207", "Wrong usertype"));
				}
			}
		}

		if(withs.containsKey(ParseStr.S_PROTOCOL)) {
			sysEnv.cEnv.setRenderer(((Token) withs.get(ParseStr.S_PROTOCOL)).token);
		}
		if (withs.containsKey(ParseStr.S_SESSION)) {
			sysEnv.cEnv.setInfo((String) withs.get(ParseStr.S_SESSION));
		} else {
			sysEnv.cEnv.setInfo(null);
		}

		if(withs.containsKey(ParseStr.S_TRACE_LEVEL)) {
			Object tmptrc = withs.get(ParseStr.S_TRACE_LEVEL);
			if (tmptrc instanceof Boolean) {
				final boolean trc = ((Boolean) tmptrc).booleanValue();
				if(trc) sysEnv.cEnv.trace_on();
				else	sysEnv.cEnv.trace_off();
			} else {
				sysEnv.cEnv.setTraceLevel(((Integer) tmptrc).intValue());
			}
		}
		if(withs.containsKey(ParseStr.S_TIMEOUT)) {

			sysEnv.cEnv.getMe().setTimeout(((Integer) withs.get(ParseStr.S_TIMEOUT)).intValue());
		}

		if (cmd != null) {

			sysEnv.tx.beginSubTransaction(sysEnv);
			while(true) {
				if(env.isUser()) {
					if((cmd.cmdtype & USER_COMMAND) != 0) break;
				} else if(env.isJobServer()) {
					if((cmd.cmdtype & SERVER_COMMAND) != 0) break;
				} else {
					if((cmd.cmdtype & JOB_COMMAND) != 0) break;
				}
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03603041709", "Illegal commandtype within connect command"));
			}
			if (cmd.contextVersion != null)
				sysEnv.tx.setContextVersionId(sysEnv, cmd.contextVersion);
			cmd.env = env;
			cmd.go(sysEnv);
			sysEnv.tx.commitSubTransaction(sysEnv);
			result = cmd.result;
		} else {
			desc.add("CONNECT_TIME");
			data.add(sysEnv.systemDateFormat.format(new Date(System.currentTimeMillis())));
			d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03205141302", "Connect"), desc, data);
			result.setOutputContainer(d_container);
			result.setFeedback(new SDMSMessage(sysEnv, "02110192358", "Connected"));
		}
	}

}

