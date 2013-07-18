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

public class AlterUser extends ManipUser
{

	public final static String __version = "@(#) $Id: AlterUser.java,v 2.5.4.1 2013/03/14 10:24:23 ronald Exp $";

	private boolean noerr;

	public AlterUser(ObjectURL u, WithHash w, Boolean n)
	{
		super(u, w);
		noerr = n.booleanValue();
	}

	public AlterUser(String u, WithHash w, Boolean n)
	{
		super(u, w);
		noerr = n.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSUser u;
		boolean suActive = false;
		boolean manageUser = false;
		Long uId;
		SDMSMessage feedbackMsg = new SDMSMessage(sysEnv, "03202062029", "User altered");

		evaluate_with(sysEnv);

		try {
			if (url == null)
				u = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(user, new Long(0)));
			else
				u = (SDMSUser) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311121715", "No user altered"));
				return;
			}
			throw nfe;
		}
		uId = u.getId(sysEnv);

		if(defaultGId == null)
			defaultGId = u.getDefaultGId(sysEnv);

		if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			SDMSPrivilege p = new SDMSPrivilege();
			Iterator i = sysEnv.cEnv.gid().iterator();
			while (i.hasNext()) {
				Long gId = (Long) i.next();
				try {
					SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(ZERO , gId));
					p.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
				} catch (NotFoundException nfe) {

				}
			}
			if (p.can(SDMSPrivilege.MANAGE_USER)) {
				manageUser = true;
			}
		}

		if(sysEnv.cEnv.uid().equals(uId) && uId != 0) {
			if(passwd != null || with.containsKey(ParseStr.S_DEFAULTGROUP)) {
				HashSet hg = new HashSet();
				hg.add(SDMSObject.adminGId);
				sysEnv.cEnv.pushGid(sysEnv, hg);
				if(passwd != null)
					u.setPasswd(sysEnv, passwd);
				if(with.containsKey(ParseStr.S_DEFAULTGROUP))
					u.setDefaultGId(sysEnv, defaultGId);
				sysEnv.cEnv.popGid(sysEnv);
			}
		} else {
			if(passwd != null && uId != 0)
				u.setPasswd(sysEnv, passwd);
			else {
				if (passwd != null) {

					if (sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
						SystemEnvironment.sysPasswd = txtPasswd;
						feedbackMsg = new SDMSMessage(sysEnv, "03102151013", "SYSTEM Password changed. Don't forget to update server.conf");
					} else {
						throw new AccessViolationException (new SDMSMessage(sysEnv, "03102151020", "Insufficient privileges accessing User SYSTEM"));
					}
				}
			}
			if(with.containsKey(ParseStr.S_DEFAULTGROUP))
				u.setDefaultGId(sysEnv, defaultGId);
		}

		if(with.containsKey(ParseStr.S_ENABLE)) {
			u.setIsEnabled(sysEnv, enable);
		}

		if(with.containsKey(ParseStr.S_GROUPLIST)) {

			Vector oldgroups = SDMSMemberTable.idx_uId.getVector(sysEnv, uId);
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				boolean canAlter = true;
				SDMSPrivilege p = new SDMSPrivilege();
				for (int i = 0; i < grouplist.size(); i++) {
					Long gId = (Long) grouplist.get(i);
					if (!sysEnv.cEnv.gid().contains(gId)) {
						canAlter = false;
						for(int j = 0; j < oldgroups.size(); j++) {
							SDMSMember m = (SDMSMember) oldgroups.get(j);
							Long mGId = m.getGId(sysEnv);
							if(gId.equals(mGId)) {

								canAlter = true;
							}
						}
						if (!canAlter) {
							break;
						}
					}
				}
				if (canAlter && manageUser) {
					HashSet hg = new HashSet();
					hg.add(SDMSObject.adminGId);
					sysEnv.cEnv.pushGid(sysEnv, hg);
					suActive = true;
				}
			}

			Long gId;
			if(!grouplist.contains(defaultGId) && !publicGId.equals(defaultGId))
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03401271115", "You cannot remove the default group"));

			for(int i = 0; i < oldgroups.size(); i++) {
				SDMSMember m = (SDMSMember) oldgroups.get(i);
				Long mGId = m.getGId(sysEnv);
				if(!grouplist.contains(mGId) && !mGId.equals(publicGId) && !mGId.equals(defaultGId)) {
					m.delete(sysEnv);
				} else {
					grouplist.remove(mGId);
				}
			}
			for(int i = 0; i < grouplist.size(); i++) {
				gId = (Long) grouplist.get(i);
				SDMSThread.doTrace(sysEnv.cEnv, "uId = " + uId + ", gId = " + gId + ", defaultGId = " + defaultGId + ", publicGId = " + publicGId, SDMSThread.SEVERITY_DEBUG);
				if(!gId.equals(defaultGId) && !gId.equals(publicGId))
					SDMSMemberTable.table.create(sysEnv, gId, uId);
			}
			try {
				SDMSMemberTable.table.create(sysEnv, defaultGId, uId);
			} catch (DuplicateKeyException dke) {

			}
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);
		}

		if(with.containsKey(ParseStr.S_ADDGROUP)) {

			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				boolean canAlter = true;
				SDMSPrivilege p = new SDMSPrivilege();
				for (int i = 0; i < addlist.size(); i++) {
					Long gId = (Long) addlist.get(i);
					if (!sysEnv.cEnv.gid().contains(gId)) {
						canAlter = false;
						break;
					}
				}
				if (canAlter && manageUser) {
					HashSet hg = new HashSet();
					hg.add(SDMSObject.adminGId);
					sysEnv.cEnv.pushGid(sysEnv, hg);
					suActive = true;
				}
			}

			Long gId;
			for(int i = 0; i < addlist.size(); i++) {
				gId = (Long) addlist.get(i);
				if(!gId.equals(defaultGId)) {
					try {
						SDMSMemberTable.table.create(sysEnv, gId, uId);
					} catch (DuplicateKeyException dke) {

					}
				}
			}
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);
		}

		if(with.containsKey(ParseStr.S_DELGROUP)) {
			if (manageUser) {
				HashSet hg = new HashSet();
				hg.add(SDMSObject.adminGId);
				sysEnv.cEnv.pushGid(sysEnv, hg);
				suActive = true;
			}
			SDMSMember m;
			Long gId;
			for(int i = 0; i < dellist.size(); i++) {
				gId = (Long) dellist.get(i);
				if(!gId.equals(defaultGId)) {
					if(gId.equals(publicGId)) continue;
					try {
						m = SDMSMemberTable.idx_gId_uId_getUnique(sysEnv, new SDMSKey(gId, uId));
						m.delete(sysEnv);
					} catch (NotFoundException nfe) {

					}
				} else {
					if (suActive)
						sysEnv.cEnv.popGid(sysEnv);
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03312102203",
					                               "You cannot remove the default group"));
				}
			}
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);
		}
		if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(defaultGId, uId)))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03312130121", "a user must belong to his default group"));

		result.setFeedback(feedbackMsg);
	}
}

