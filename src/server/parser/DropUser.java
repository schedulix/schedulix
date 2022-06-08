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

public class DropUser extends Node
{

	public final static String __version = "@(#) $Id: DropUser.java,v 2.5.4.1 2013/03/14 10:24:31 ronald Exp $";

	private ObjectURL url;
	private boolean noerr;

	public DropUser(ObjectURL u, Boolean n)
	{
		super();
		url = u;
		noerr = n.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSUser u;

		try {
			u = (SDMSUser) url.resolve(sysEnv);
		} catch(NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311121718", "No user dropped"));
				return;
			}
			throw nfe;
		}

		Long uid = u.getId(sysEnv);
		boolean suActive = false;

		try {
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				SDMSPrivilege p = new SDMSPrivilege();
				Iterator i = sysEnv.cEnv.gid().iterator();
				while(i.hasNext()) {
					Long gId = (Long) i.next();
					try {
						SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(SDMSConstants.lZERO, gId));
						p.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
					} catch (NotFoundException nfe) {
					}
				}
				if (p.can(SDMSPrivilege.MANAGE_USER)) {
					HashSet hg = new HashSet();
					hg.add(SDMSObject.adminGId);
					sysEnv.cEnv.pushGid(sysEnv, hg);
					suActive = true;
				} else {
					throw new AccessViolationException(new SDMSMessage(sysEnv, "03408281223", "Insufficient Privileges"));
				}
			}

			Vector v = SDMSMemberTable.idx_uId.getVector(sysEnv, uid);
			for(int i = 0; i < v.size(); i++) {
				SDMSMember m = (SDMSMember) v.get(i);
				m.delete(sysEnv);
			}
			v = SDMSUserEquivTable.idx_uId.getVector(sysEnv, uid);
			for (int i = 0; i < v.size(); ++i) {
				SDMSUserEquiv ue = (SDMSUserEquiv) v.get(i);
				ue.delete(sysEnv);
			}
			v = SDMSUserEquivTable.idx_altUId.getVector(sysEnv, uid);
			for (int i = 0; i < v.size(); ++i) {
				SDMSUserEquiv ue = (SDMSUserEquiv) v.get(i);
				ue.delete(sysEnv);
			}
			u.setIsEnabled(sysEnv, Boolean.FALSE);
			u.setDeleteVersion(sysEnv, Long.valueOf(sysEnv.tx.txId));
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);

		} catch (Throwable t) {
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);
			throw t;
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03301272337", "User dropped"));
	}
}

