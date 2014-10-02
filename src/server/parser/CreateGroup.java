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

public class CreateGroup extends ManipGroup
{

	public final static String __version = "@(#) $Id: CreateGroup.java,v 2.2.6.1 2013/03/14 10:24:26 ronald Exp $";

	private boolean replace;
	private String group;

	public CreateGroup(String g, WithHash w, Boolean r)
	{
		super(null, w);
		group = g;
		with = w;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSGroup g;
		Long newGroupId = null;
		boolean manageGroup = false;
		boolean revive = false;
		boolean setgid = false;
		boolean addedMe = false;
		Long myUId = sysEnv.cEnv.uid();

		evaluate_with(sysEnv);
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
			if (p.can(SDMSPrivilege.MANAGE_GROUP)) {
				manageGroup = true;
			}
		}

		Vector v = SDMSGroupTable.idx_name.getVector(sysEnv, group);
		Iterator i1 = v.iterator();
		try {
			if (manageGroup) {
				HashSet hg = new HashSet();
				hg.add(SDMSObject.adminGId);
				sysEnv.cEnv.pushGid(sysEnv, hg);
				setgid = true;
			}
			while (i1.hasNext()) {
				SDMSGroup g1 = (SDMSGroup)i1.next();
				if (!g1.getDeleteVersion(sysEnv).equals(new Long(0))) {
					replace = true;
					g1.setDeleteVersion(sysEnv, new Long(0));
					newGroupId = g1.getId(sysEnv);
					SDMSMemberTable.table.create(sysEnv, newGroupId, myUId);
					addedMe = true;
					break;
				}
			}
			if (setgid) {
				sysEnv.cEnv.popGid(sysEnv);
				setgid = false;
			}
		} catch (Throwable t) {
			if (setgid) {
				sysEnv.cEnv.popGid(sysEnv);
				setgid = false;
			}
			throw t;
		}

		try {
			try {
				if (manageGroup) {
					HashSet hg = new HashSet();
					hg.add(SDMSObject.adminGId);
					sysEnv.cEnv.pushGid(sysEnv, hg);
					setgid = true;
				}

				g = SDMSGroupTable.table.create( sysEnv, group, new Long(0));
			} catch (DuplicateKeyException dke) {
				if (setgid) {
					sysEnv.cEnv.popGid(sysEnv);
					setgid = false;
				}
				if(replace) {
					try {
						AlterGroup ag = new AlterGroup(new ObjectURL(new Integer(Parser.GROUP), group), with, Boolean.FALSE);
						ag.setEnv(env);
						ag.go(sysEnv);
						result = ag.result;

						if (addedMe) sysEnv.cEnv.addGid(newGroupId);
					} catch (SDMSException e) {
						throw e;
					}
					return;
				} else {
					throw dke;
				}
			}

			newGroupId = g.getId(sysEnv);
			for(int i = 0; i < userlist.size(); i++) {
				Long uid = (Long) userlist.get(i);
				if (uid.equals(myUId)) addedMe = true;
				SDMSMemberTable.table.create(sysEnv, newGroupId, uid);
			}

			if (manageGroup && !addedMe) {
				SDMSMemberTable.table.create(sysEnv, newGroupId, myUId);
				addedMe = true;
			}

			if (setgid) {
				sysEnv.cEnv.popGid(sysEnv);
				setgid = false;
			}
		} catch (Throwable t) {
			if (setgid) {
				sysEnv.cEnv.popGid(sysEnv);
				setgid = false;
			}
			throw t;
		}

		if (addedMe) sysEnv.cEnv.addGid(newGroupId);

		result.setFeedback(new SDMSMessage(sysEnv, "03312091429", "Group created"));
	}
}

