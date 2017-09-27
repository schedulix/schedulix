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
	protected final static Long zero = new Long(0);

	protected String user;
	private String jsName;
	private String passwd;
	protected String txtPasswd;
	private boolean isJobServer;
	private boolean isJob;
	private boolean isUser;
	private Vector path;
	private Long jobid;
	private WithHash withs;
	private final Vector cmd;
	private Node actual_cmd = null;

	public Connect(String u, String p, WithHash wh)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		user = u;
		txtPasswd = p;
		isUser = true;
		isJobServer = false;
		jsName = null;
		isJob = false;
		jobid = null;
		path = null;
		withs = wh;
		cmd = (Vector) withs.get(ParseStr.S_COMMAND);
		auditFlag = false;
		if (cmd == null && SystemEnvironment.auth == null) {
			txMode = SDMSTransaction.READONLY;
		} else {
			txMode = SDMSTransaction.READONLY;
			if (SystemEnvironment.auth == null) {
				for (int i = 0; i < cmd.size(); ++i) {
					Node n = (Node) cmd.get(i);
					if (n.txMode == SDMSTransaction.READWRITE) {
						txMode = SDMSTransaction.READWRITE;
						auditFlag = n.auditFlag;
						break;
					}
				}
			} else {
				txMode = SDMSTransaction.READWRITE;
			}
		}
	}

	protected Connect(WithHash wh)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		isUser = true;
		isJobServer = false;
		jsName = null;
		isJob = false;
		jobid = null;
		path = null;
		withs = wh;
		cmd = (Vector) withs.get(ParseStr.S_COMMAND);
		auditFlag = false;
		if (cmd == null && SystemEnvironment.auth == null) {
			txMode = SDMSTransaction.READONLY;
		} else {
			txMode = SDMSTransaction.READONLY;
			if (SystemEnvironment.auth == null) {
				for (int i = 0; i < cmd.size(); ++i) {
					Node n = (Node) cmd.get(i);
					if (n.txMode == SDMSTransaction.READWRITE) {
						txMode = SDMSTransaction.READWRITE;
						auditFlag = n.auditFlag;
						break;
					}
				}
			} else {
				txMode = SDMSTransaction.READWRITE;
			}
		}
	}

	public Connect(Vector pth, String js, String p, WithHash wh)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		user = null;
		isUser = false;
		isJobServer = true;
		txtPasswd = p;
		jsName = js;
		isJob = false;
		jobid = null;
		path = pth;
		withs = wh;
		cmd = (Vector) withs.get(ParseStr.S_COMMAND);
		if (cmd == null) auditFlag = false;
		else {
			auditFlag = ((Node) cmd.get(0)).auditFlag;
		}
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
		cmd = (Vector) withs.get(ParseStr.S_COMMAND);
		if (cmd == null) {
			txMode = SDMSTransaction.READONLY;
			auditFlag = false;
		} else {
			txMode = SDMSTransaction.READONLY;
			auditFlag = false;
			for (int k = 0; k < cmd.size(); ++k) {
				Node n = (Node) cmd.get(k);
				if (n.txMode == SDMSTransaction.READWRITE) {
					txMode = SDMSTransaction.READWRITE;
					auditFlag = n.auditFlag;
					break;
				}
			}
		}
	}

	private void writeCredentials(SystemEnvironment sysEnv, SDMSUser u)
	throws SDMSException
	{
		String pwdHash;
		String storedHash;
		String salt;
		int method;

		storedHash = u.getPasswd(sysEnv);
		salt = u.getSalt(sysEnv);
		method = u.getMethod(sysEnv).intValue();
		if (method == SDMSUser.MD5)
			pwdHash = CheckSum.mkstr(CheckSum.md5((txtPasswd + (salt == null ? "" : salt)).getBytes()), true);
		else
			pwdHash = CheckSum.mkstr(CheckSum.sha256((txtPasswd + (salt == null ? "" : salt)).getBytes()), false);
		if (pwdHash.equals(storedHash))
			return;

		salt = ManipUser.generateSalt();
		pwdHash = CheckSum.mkstr(CheckSum.sha256((txtPasswd + salt).getBytes()), false);
		method = SDMSUser.SHA256;

		u.setSalt(sysEnv, salt);
		u.setMethod(sysEnv, new Integer(method));
		u.setPasswd(sysEnv, pwdHash);
	}

	private void connect_internal_user(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSUser u;
		Long uId;
		String salt;
		int method;

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
				salt = u.getSalt(sysEnv);
				method = u.getMethod(sysEnv).intValue();
				if (method == SDMSUser.MD5)
					passwd = CheckSum.mkstr(CheckSum.md5((txtPasswd + (salt == null ? "" : salt)).getBytes()), true);
				else
					passwd = CheckSum.mkstr(CheckSum.sha256((txtPasswd + (salt == null ? "" : salt)).getBytes()), false);
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

		int connectType = u.getConnectionType(sysEnv).intValue();
		boolean connectOK;
		if (connectType > SDMSUser.PLAIN)
			if (sysEnv.cEnv.getIsSSLConnection())
				if (connectType > SDMSUser.SSL)
					if (sysEnv.cEnv.getIsClientAuthenticated())
						connectOK = true;
					else
						connectOK = false;
				else
					connectOK = true;
			else
				connectOK = false;
		else
			connectOK = true;

		if (!connectOK)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03707271340", "(Authencicated) SSL Connection required"));

		uId = u.getId(sysEnv);
		sysEnv.cEnv.setUid(uId);
		sysEnv.cEnv.setUser();
		sysEnv.cEnv.setGid(sysEnv, SDMSMemberTable.idx_uId.getVector(sysEnv, uId));
	}

	protected void initUser(SystemEnvironment sysEnv, String[] groups, boolean syncCredentials, boolean createUser, boolean createGroups)
	throws SDMSException
	{
		SDMSUser u;
		Long uId;
		Integer method = new Integer(SDMSUser.SHA256);
		boolean suActive = false;
		Vector members = null;
		boolean freshMeat = false;
		sysEnv.cEnv.setUid(SDMSObject.internalUId);
		HashSet hg = new HashSet();
		hg.add(SDMSObject.adminGId);
		sysEnv.cEnv.pushGid(sysEnv, hg);
		suActive = true;

		try {
			try {
				u = SDMSUserTable.idx_name_getUnique(sysEnv, user);
				if (u.getDeleteVersion(sysEnv).intValue() != 0) {
					if (!createUser)
						throw new CommonErrorException(new SDMSMessage(sysEnv, "02709251500", "User " + user + " does not exist"));
					u.setDeleteVersion(sysEnv, zero);
					u.setIsEnabled(sysEnv, Boolean.TRUE);
					try {
						SDMSMemberTable.table.create(sysEnv, SDMSObject.publicGId, u.getId(sysEnv));
					} catch (DuplicateKeyException dke) {
					}
				}
			} catch (NotFoundException e) {
				if (createUser)
					u = null;
				else
					throw new CommonErrorException(new SDMSMessage(sysEnv, "02709251459", "User " + user + " does not exist"));
			}

			if (u == null) {
				String passwd = "Internal Authentication Disabled";
				Boolean enable = Boolean.TRUE;
				u = SDMSUserTable.table.create(sysEnv, user, passwd, passwd , method, enable, SDMSObject.publicGId, new Integer(SDMSUser.PLAIN), zero);
				SDMSMemberTable.table.create(sysEnv, SDMSObject.publicGId, u.getId(sysEnv));
				freshMeat = true;
			} else {
				if (!u.getIsEnabled(sysEnv).booleanValue()) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "02709251502", "User " + user + " is disabled"));
				}
			}

			uId = u.getId(sysEnv);
			members = SDMSMemberTable.idx_uId.getVector(sysEnv, uId);
			if (groups == null) {
			} else {
				HashSet extGroups = new HashSet();
				extGroups.add(SDMSObject.publicGId);
				SDMSGroup g;
				SDMSMember m;
				for (int i = 0; i < groups.length; ++i) {
					try {
						Vector tmp = SDMSGroupTable.idx_name.getVector(sysEnv, groups[i]);
						g = (SDMSGroup) tmp.get(0);
						if (i == 0 && freshMeat) {
							u.setDefaultGId(sysEnv, g.getId(sysEnv));
						}
						try {
							SDMSMemberTable.table.create(sysEnv, g.getId(sysEnv), uId);
						} catch (DuplicateKeyException dke) {
						}
					} catch (NotFoundException nfe) {
						if (!createGroups)
							throw new CommonErrorException(new SDMSMessage(sysEnv, "02709251501", "Group " + groups[i] + " does not exist"));
						g = SDMSGroupTable.table.create(sysEnv, groups[i], zero);
						m = SDMSMemberTable.table.create(sysEnv, g.getId(sysEnv), uId);
						members.add(m);
					}
					extGroups.add(g.getId(sysEnv));
				}
				Iterator it = members.iterator();
				while (it.hasNext()) {
					m = (SDMSMember) it.next();
					Long gId = m.getGId(sysEnv);
					if (!extGroups.contains(gId)) {
						it.remove();
						m.delete(sysEnv);
					}
				}
			}

			if (syncCredentials && SystemEnvironment.auth.syncCredentials(user)) {
				writeCredentials(sysEnv, u);
			}

			sysEnv.cEnv.popGid(sysEnv);
			suActive = false;
		} catch (Throwable t) {
			if (suActive) {
				sysEnv.cEnv.popGid(sysEnv);
				suActive = false;
			}
			throw t;
		}

		uId = u.getId(sysEnv);
		sysEnv.cEnv.setUid(uId);
		sysEnv.cEnv.setUser();
		sysEnv.cEnv.setGid(sysEnv, members);
	}

	private void connect_external_user(SystemEnvironment sysEnv)
	throws SDMSException
	{
		String[] groups = SystemEnvironment.auth.getGroupNames(user);
		int checkResult;

		checkResult = SystemEnvironment.auth.checkCredentials(user, txtPasswd);
		if (checkResult == Authenticator.SUCCESS) {
			initUser(sysEnv, groups, true, true, true);
		} else {
			if (checkResult == Authenticator.ABORT && SystemEnvironment.auth.checkInternally(user)) {
				connect_internal_user(sysEnv);
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02110192352", "Invalid username or password"));
			}
		}
	}

	private void connect_user(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSUser u;
		Long uId;
		String salt;
		int method;

		if (SystemEnvironment.auth == null || user.toUpperCase().equals("SYSTEM") || !SystemEnvironment.auth.checkExternally(user)) {
			connect_internal_user(sysEnv);
		} else {
			connect_external_user(sysEnv);
		}
	}

	private void connect_jobserver(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSScope s;
		Long pId;
		int timeout;
		String salt;
		int method;

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
			salt = s.getSalt(sysEnv);
			method = s.getMethod(sysEnv).intValue();
			if (method == SDMSScope.MD5)
				passwd = CheckSum.mkstr(CheckSum.md5((txtPasswd + (salt == null ? "" : salt)).getBytes()), true);
			else
				passwd = CheckSum.mkstr(CheckSum.sha256((txtPasswd + (salt == null ? "" : salt)).getBytes()), false);
			if(!s.getPasswd(sysEnv).equals(passwd)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv,
						"03202041511", "Invalid jobservername or password"));
			}
		} catch (NotFoundException nfe) {
			throw new CommonErrorException(new SDMSMessage(sysEnv,
					"03202041510", "Invalid jobservername or password"));
		}

		SDMSnpSrvrSRFootprint sf = SDMSnpSrvrSRFootprintTable.idx_sId_getUniqueForUpdate(sysEnv, s.getId(sysEnv));
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
		return actual_cmd;
	}

	public String getName()
	{
		String s = this.getClass().getName();
		if (cmd != null)
			s = cmd.getClass().getName();
		return s.substring(s.lastIndexOf('.')+1);
	}

	protected void evaluateWith(SystemEnvironment sysEnv)
	throws SDMSException
	{
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

		evaluateWith(sysEnv);

		if (cmd != null) {
			int stmtnr = 0;
			sysEnv.tx.beginSubTransaction(sysEnv);
			try {
				for (int i = 0; i < cmd.size(); ++i) {
					stmtnr++;
					Node n = (Node) cmd.get(i);
			sysEnv.tx.beginSubTransaction(sysEnv);
			while(true) {
				if(env.isUser()) {
					if((n.cmdtype & USER_COMMAND) != 0) break;
				} else if(env.isJobServer()) {
					if((n.cmdtype & SERVER_COMMAND) != 0) break;
				} else {
					if((n.cmdtype & JOB_COMMAND) != 0) break;
				}
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03603041709", "Illegal commandtype within connect command"));
			}
			if (n.contextVersion != null)
				sysEnv.tx.setContextVersionId(sysEnv, n.contextVersion);
			n.env = env;
			n.go(sysEnv);
					sysEnv.tx.commitSubTransaction(sysEnv);
					result = n.result;
				}
			} catch (SDMSException e) {
				sysEnv.tx.rollbackSubTransaction(sysEnv);
				throw e;
			}
			sysEnv.tx.commitSubTransaction(sysEnv);
		} else {
			desc.add("CONNECT_TIME");
			data.add(sysEnv.systemDateFormat.format(new Date(System.currentTimeMillis())));
			d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03205141302", "Connect"), desc, data);
			result.setOutputContainer(d_container);
			result.setFeedback(new SDMSMessage(sysEnv, "02110192358", "Connected"));
		}
	}

}

