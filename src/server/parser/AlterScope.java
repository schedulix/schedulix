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

import java.util.Vector;

import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.util.*;

public class AlterScope
	extends Node
{
	public static final String __version = "@(#) $Id: AlterScope.java,v 2.7.2.1 2013/03/14 10:24:23 ronald Exp $";

	private final WithHash with;
	private final boolean noerr;
	private final ObjectURL url;
	private boolean dynamic = false;
	private boolean fatal;
	private String  errmsg;

	public AlterScope (final ObjectURL u, final WithHash with, Boolean n)
	{
		super();
		url = u;
		this.with = with == null ? new WithHash() : with;
		errmsg = null;
		noerr = n.booleanValue();
	}

	public AlterScope(boolean f, String emsg, final Boolean ne)
	{
		super();
		cmdtype = Node.SERVER_COMMAND;
		url = null;
		with = new WithHash();
		fatal = f;
		errmsg = emsg;
		noerr = ne.booleanValue();
	}

	public AlterScope (final WithHash with, final Boolean ne)
	{
		super();
		cmdtype = Node.SERVER_COMMAND;
		url = null;
		this.with = with;
		errmsg = null;
		dynamic = true;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(url == null) {
			jobServerAlter(sysEnv);
		} else {
			userAlter(sysEnv);
		}
	}

	public void userAlter (final SystemEnvironment sysEnv)
		throws SDMSException
	{
		final SDMSScope s;
		try {
			s = (SDMSScope) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback (new SDMSMessage (sysEnv, "03311121736", "No scope altered"));
				return;
			}
			throw nfe;
		}
		if (url.parserType.intValue() == Parser.SCOPE)
			userAlterScope(sysEnv, s);
		else
			userAlterJobServer(sysEnv, s);
	}

	public void userAlterScope (final SystemEnvironment sysEnv, SDMSScope s)
		throws SDMSException
	{
		final Long sId = s.getId(sysEnv);

		if (with.containsKey (ParseStr.S_CONFIG))
			ScopeConfig.alter (sysEnv, s, (WithHash) with.get (ParseStr.S_CONFIG));
		if (with.containsKey (ParseStr.S_PARAMETERS))
			ScopeParameter.alter (sysEnv, sId, (WithHash) with.get (ParseStr.S_PARAMETERS));

		if (with.containsKey (ParseStr.S_GROUP) || with.containsKey (ParseStr.S_GROUP_CASCADE)) {
			if (with.containsKey (ParseStr.S_GROUP) && with.containsKey (ParseStr.S_GROUP_CASCADE)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03402041341", "It is forbidden to specify the group clause twice"));
			}
			String gName;
			if (with.containsKey (ParseStr.S_GROUP)) {
				gName = (String) with.get(ParseStr.S_GROUP);
			} else {
				gName = (String) with.get(ParseStr.S_GROUP_CASCADE);
			}
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
			s.setOwnerId(sysEnv, gId);
			if(with.containsKey (ParseStr.S_GROUP_CASCADE)) {
				changeChildGroup(sysEnv, sId, gId);
			}
		}

		if (with.containsKey(ParseStr.S_INHERIT)) {
			Long inheritPrivs = (Long) with.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
			long lpriv = inheritPrivs.longValue();
			if((s.getPrivilegeMask() & lpriv) != lpriv) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061325", "Incompatible grant"));
			}

			s.setInheritPrivs(sysEnv, inheritPrivs);
		}

		result.setFeedback (new SDMSMessage (sysEnv, "04305181612", "Scope altered"));
	}

	private void userAlterJobServer(SystemEnvironment sysEnv, SDMSScope s)
		throws SDMSException
	{
		final Long sId = s.getId(sysEnv);
		String salt = null;
		Integer method = new Integer(SDMSScope.SHA256);

		s.notify(sysEnv);

		if (with.containsKey (ParseStr.S_NODE))
			s.setNode(sysEnv, (String) with.get (ParseStr.S_NODE));

		String passwd = null;
		if (with.containsKey (ParseStr.S_PASSWORD)) {
			salt = ManipUser.generateSalt();
			if (method.intValue() == SDMSScope.MD5)
				passwd = CheckSum.mkstr (CheckSum.md5 ((((String) with.get (ParseStr.S_PASSWORD)) + salt).getBytes()), true);
			else
				passwd = CheckSum.mkstr (CheckSum.sha256 ((((String) with.get (ParseStr.S_PASSWORD)) + salt).getBytes()), false);
		}
		if (with.containsKey (ParseStr.S_RAWPASSWORD)) {
			if (passwd == null) {
				Vector v = (Vector) with.get (ParseStr.S_RAWPASSWORD);
				passwd = (String) v.get(0);
				salt = (String) v.get(1);

				if (passwd.length() == ManipUser.MD5LENGTH)
					method = new Integer(SDMSScope.MD5);
			} else
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04312151811", "Both " + ParseStr.S_PASSWORD + " and " + ParseStr.S_RAWPASSWORD + " are not allowed"));
		}
		if(passwd != null) {
			s.setPasswd(sysEnv, passwd);
			s.setSalt(sysEnv, salt);
			s.setMethod(sysEnv, method);
		}

		if (with.containsKey (ParseStr.S_ERROR_TEXT)) {
			s.setErrmsg(sysEnv, (String) with.get(ParseStr.S_ERROR_TEXT));
		}

		if (with.containsKey (ParseStr.S_ENABLE)) {
			final Boolean enable = (Boolean) with.get (ParseStr.S_ENABLE);

			if(!enable.booleanValue()) {
				Vector v = SDMSSubmittedEntityTable.idx_scopeId.getVector(sysEnv, sId);
				for(int i = 0; i < v.size(); i++) {
					SDMSSubmittedEntity sme = (SDMSSubmittedEntity) v.get(i);
					int state = sme.getState(sysEnv).intValue();
					if (state == SDMSSubmittedEntity.STARTING ||
					    state == SDMSSubmittedEntity.STARTED  ||
					    state == SDMSSubmittedEntity.RUNNING  ||
					    state == SDMSSubmittedEntity.TO_KILL  ||
					    state == SDMSSubmittedEntity.KILLED) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03311031035",
								"A scope cannot be disabled while jobs are still running"));
					}
				}
				if(s.isConnected(sysEnv)) {
					SystemEnvironment.server.killUser(s.getConnectionId(sysEnv));
				}
				s.setIsRegistered(sysEnv, Boolean.FALSE);
			}
			s.setIsEnabled(sysEnv, enable);
		}
		if (with.containsKey (ParseStr.S_CONFIG))
			ScopeConfig.alter (sysEnv, s, (WithHash) with.get (ParseStr.S_CONFIG));
		if (with.containsKey (ParseStr.S_PARAMETERS))
			ScopeParameter.alter (sysEnv, sId, (WithHash) with.get (ParseStr.S_PARAMETERS));
		if (with.containsKey (ParseStr.S_GROUP)) {
			final String gName = (String) with.get (ParseStr.S_GROUP);
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
			s.setOwnerId(sysEnv, gId);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03201301103", "Job Server altered"));
	}

	private void changeChildGroup(SystemEnvironment sysEnv, Long parentId, Long groupId)
		throws SDMSException
	{
		Vector cv = SDMSScopeTable.idx_parentId.getVector(sysEnv, parentId);
		for(int i = 0; i < cv.size(); i++) {
			SDMSScope s = (SDMSScope) cv.get(i);
			try {
				s.setOwnerId(sysEnv, groupId);
			} catch (AccessViolationException ave) {

			}
			changeChildGroup(sysEnv, s.getId(sysEnv), groupId);
		}
	}

	private void jobServerAlter(SystemEnvironment sysEnv)
		throws SDMSException
	{
		final SDMSScope s = SDMSScopeTable.getObjectForUpdate(sysEnv, sysEnv.cEnv.uid());

		if (dynamic) {
			ScopeParameter.setDynamicValues (sysEnv, s, with);
			result.setFeedback (new SDMSMessage (sysEnv, "04307152322", "Job Server altered"));
			return;
		}

		if(fatal) {

			s.setState(sysEnv, new Integer(SDMSScope.FATAL));
		} else {

			s.setState(sysEnv, new Integer (SDMSScope.NONFATAL));

		}
		SDMSThread.doTrace(env, errmsg, SDMSThread.SEVERITY_ERROR);
		s.setErrmsg(sysEnv, errmsg);
		result.setFeedback(new SDMSMessage(sysEnv, "03202052011", "Job Server altered"));
	}

}
