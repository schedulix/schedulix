/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class AlterGroup extends ManipGroup
{

	public final static String __version = "@(#) $Id: AlterGroup.java,v 2.2.6.1 2013/03/14 10:24:20 ronald Exp $";

	private boolean noerr;

	public AlterGroup(ObjectURL u, WithHash w, Boolean n)
	{
		super(u, w);
		noerr = n.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSGroup g;
		Long gId;
		Long myUId = sysEnv.cEnv.uid();

		evaluate_with(sysEnv);

		try {
			g = (SDMSGroup) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03312130027", "No group altered"));
				return;
			}
			throw nfe;
		}
		gId = g.getId(sysEnv);

		if(g.getName(sysEnv).equals(SDMSGroup.PUBLIC))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03401271652", "you cannot remove a user from public"));

		if(with.containsKey(ParseStr.S_USERLIST)) {
			Vector oldusers = SDMSMemberTable.idx_gId.getVector(sysEnv, gId);
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId) && !userlist.contains(myUId)) {

				throw new CommonErrorException(new SDMSMessage(sysEnv, "03104151448",
				                               "You cannot remove yourself from a group"));
			}
			for(int i = 0; i < oldusers.size(); i++) {
				SDMSMember m = (SDMSMember) oldusers.get(i);
				Long uId = m.getUId(sysEnv);
				SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
				if(gId.equals(u.getDefaultGId(sysEnv))) {
					if(!userlist.contains(u.getId(sysEnv))) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03312130039",
						                               "you cannot remove a user from his default group"));
					}
				}
				if(userlist.contains(uId))
					userlist.remove(uId);
				else
					m.delete(sysEnv);
			}
			for(int i = 0; i < userlist.size(); i++) {
				Long uId = (Long) userlist.get(i);
				SDMSMemberTable.table.create(sysEnv, gId, uId);
			}
		}

		if(with.containsKey(ParseStr.S_ADDUSER)) {
			Long uId;
			for(int i = 0; i < addlist.size(); i++) {
				uId = (Long) addlist.get(i);
				try {
					SDMSMemberTable.table.create(sysEnv, gId, uId);
				} catch (DuplicateKeyException dke) {

				}
			}
		}

		if(with.containsKey(ParseStr.S_DELUSER)) {
			SDMSMember m;
			Long uId;
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId) && dellist.contains(myUId)) {

				throw new CommonErrorException(new SDMSMessage(sysEnv, "03104151447",
				                               "You cannot remove yourself from a group"));
			}
			for(int i = 0; i < dellist.size(); i++) {
				uId = (Long) dellist.get(i);
				Long defaultGId = SDMSUserTable.getObject(sysEnv, uId).getDefaultGId(sysEnv);
				if(!gId.equals(defaultGId)) {
					try {
						m = SDMSMemberTable.idx_gId_uId_getUnique(sysEnv, new SDMSKey(gId, uId));
						m.delete(sysEnv);
					} catch (NotFoundException nfe) {

					}
				} else {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03312102202",
					                               "You cannot remove the default group"));
				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03312091431", "Group altered"));
	}
}

