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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.util.*;

public class CreateScope extends Node
{

	public final static String __version = "@(#) $Id: CreateScope.java,v 2.8.2.1 2013/03/14 10:24:28 ronald Exp $";

	private boolean replace;
	private final WithHash with;
	private ObjectURL url;

	public CreateScope(ObjectURL u, final WithHash with, Boolean r)
	{
		super();
		url = u;
		this.with = with == null ? new WithHash() : with;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long parentId;
		String passwd = null;
		String node = null;
		String salt = null;
		Integer method = new Integer(SDMSScope.SHA256);
		Boolean isEnabled = null;
		Vector path;
		String name;
		final Long uId = env.uid();
		final Long gId;
		Boolean isTerminate = null;
		Boolean hasAlteredConfig = null;
		Boolean isSuspended = null;
		Boolean isRegistered = null;
		Integer state = null;
		Integer type = new Integer(SDMSScope.SCOPE);
		boolean isScope;
		final SDMSScope s;

		if (url.parserType.intValue() == Parser.SCOPE)	isScope = true;
		else {
			isScope = false;
			isTerminate = Boolean.FALSE;
			hasAlteredConfig = Boolean.FALSE;
			isSuspended = Boolean.FALSE;
			isRegistered = Boolean.FALSE;
			isEnabled = Boolean.TRUE;
			state = new Integer(SDMSScope.NOMINAL);
			type = new Integer(SDMSScope.SERVER);
			node = (String) with.get (ParseStr.S_NODE);
			if (with.containsKey (ParseStr.S_PASSWORD)) {
				salt = ManipUser.generateSalt();
				if (method.intValue() == SDMSScope.MD5)
					passwd = CheckSum.mkstr (CheckSum.md5 ((((String) with.get (ParseStr.S_PASSWORD)) + salt).getBytes()), true);
				else
					passwd = CheckSum.mkstr (CheckSum.sha256 ((((String) with.get (ParseStr.S_PASSWORD)) + salt).getBytes()), false);
			}
			if (with.containsKey (ParseStr.S_RAWPASSWORD))
				if (passwd == null) {
					Vector v = (Vector) with.get (ParseStr.S_RAWPASSWORD);
					passwd = (String) v.get(0);
					salt = (String) v.get(1);

					if (passwd.length() == ManipUser.MD5LENGTH)
						method = new Integer(SDMSScope.MD5);
				} else
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04312151753",
						"Both " + ParseStr.S_PASSWORD + " and " + ParseStr.S_RAWPASSWORD + " are not allowed"));
			if (passwd == null)
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04312151754",
					"Either " + ParseStr.S_PASSWORD + " or " + ParseStr.S_RAWPASSWORD + " must be specified"));

			if (with.containsKey (ParseStr.S_ENABLE))
				isEnabled = (Boolean) with.get (ParseStr.S_ENABLE);
		}

		path = (Vector) url.path.clone();
		name = (String) path.remove(path.size() - 1);

		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		if(!with.containsKey(ParseStr.S_GROUP)) {
			gId = u.getDefaultGId(sysEnv);
		} else {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey (gName, new Long(0))).getId(sysEnv);
			if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(gId, uId)) &&
			   !SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, uId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03312161746",
						"User $1 does not belong to Group $2", u.getName(sysEnv), gName));
			}
		}

		SDMSScope parent;
		if (path.size() > 0) {
			parent = SDMSScopeTable.getScope(sysEnv, path);
			parentId = parent.getId(sysEnv);
		} else
			throw new DuplicateKeyException();

		Long inheritPrivs;
		if (with.containsKey(ParseStr.S_INHERIT)) {
			inheritPrivs = (Long) with.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
		} else
			inheritPrivs = null;

		long lpriv = (inheritPrivs == null ? parent.getPrivilegeMask() : inheritPrivs.longValue());
		if((parent.getPrivilegeMask() & lpriv) != lpriv) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061323", "Incompatible grant"));
		}

		inheritPrivs = new Long(lpriv);

		try {
			s = SDMSScopeTable.table.create(sysEnv, name, gId, parentId, type, isTerminate,
							hasAlteredConfig, isSuspended, isEnabled,
							isRegistered, state, passwd, salt, method, null ,
							node, null , null , inheritPrivs);

			final Long sId = s.getId (sysEnv);

			if (with.containsKey (ParseStr.S_CONFIG))
				ScopeConfig.create (sysEnv, sId, (WithHash) with.get (ParseStr.S_CONFIG));
			if (with.containsKey (ParseStr.S_PARAMETERS))
				ScopeParameter.create (sysEnv, sId, (WithHash) with.get (ParseStr.S_PARAMETERS));
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterScope as = new AlterScope(url, with, Boolean.FALSE);
				as.setEnv(env);
				as.go(sysEnv);
				result = as.result;
				return;
			}
			throw dke;
		}

		if (!isScope)
			SystemEnvironment.sched.notifyChange(sysEnv, s, SchedulingThread.CREATE);

		result.setFeedback(new SDMSMessage(sysEnv, "03201280947", (isScope ? "Scope created" : "Jobserver created")));
	}
}
