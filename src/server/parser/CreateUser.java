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

public class CreateUser extends ManipUser
{

	public final static String __version = "@(#) $Id: CreateUser.java,v 2.1.6.1 2013/03/14 10:24:28 ronald Exp $";

	private boolean replace;

	public CreateUser(String u, WithHash w, Boolean r)
	{
		super(u, w);
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSUser u;
		boolean suActive = false;
		salt = generateSalt();
		evaluate_with(sysEnv);

		if(defaultGId == null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03401261241", "default group must be specified"));
		if(passwd == null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03312101400", "Either " + ParseStr.S_PASSWORD + " or " + ParseStr.S_RAWPASSWORD + " must be specified"));

		HashSet ug = sysEnv.cEnv.gid();
		try {
			if (!ug.contains(SDMSObject.adminGId)) {
				boolean canCreate = true;
				SDMSPrivilege p = new SDMSPrivilege();
				for (int i = 0; i < grouplist.size(); i++) {
					Long gId = (Long) grouplist.get(i);
					if (!ug.contains(gId)) {
						canCreate = false;
						break;
					}
				}
				Iterator it = ug.iterator();
				while (it.hasNext()) {
					Long gId = (Long) it.next();
					try {
						SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(SDMSConstants.lZERO, gId));
						p.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
					} catch (NotFoundException nfe) {
					}
				}
				if (canCreate && p.can(SDMSPrivilege.MANAGE_USER)) {
					HashSet hg = new HashSet();
					hg.add(SDMSObject.adminGId);
					sysEnv.cEnv.pushGid(sysEnv, hg);
					suActive = true;
				}
			}

			Vector v = SDMSUserTable.idx_name.getVector(sysEnv, user);
			Iterator i1 = v.iterator();
			while (i1.hasNext()) {
				SDMSUser u1 = (SDMSUser)i1.next();
				if (!u1.getDeleteVersion(sysEnv).equals(SDMSConstants.lZERO)) {
					u1.setDeleteVersion(sysEnv, SDMSConstants.lZERO);
					try {
						SDMSMemberTable.table.create(sysEnv, publicGId, u1.getId(sysEnv));
					} catch (DuplicateKeyException dke) {
					} catch (SDMSException e) {
						if (suActive) {
							sysEnv.cEnv.popGid(sysEnv);
							suActive = false;
						}
						throw e;
					}
					replace = true;
					break;
				}
			}

			try {
				u = SDMSUserTable.table.create( sysEnv, user, passwd, salt, method, enable, defaultGId, connect_type, SDMSConstants.lZERO);
			} catch (DuplicateKeyException dke) {
				if(replace) {
					try {
						AlterUser au = new AlterUser(user, with, Boolean.FALSE);
						au.setEnv(env);
						au.go(sysEnv);
						result = au.result;
						if (suActive) {
							sysEnv.cEnv.popGid(sysEnv);
							suActive = false;
						}
						return;
					} catch (SDMSException e) {
						if (suActive) {
							sysEnv.cEnv.popGid(sysEnv);
							suActive = false;
						}
						throw e;
					}
				} else {
					if (suActive) {
						sysEnv.cEnv.popGid(sysEnv);
						suActive = false;
					}
					throw dke;
				}
			}
			Long uId = u.getId(sysEnv);
			SDMSMemberTable.table.create(sysEnv, publicGId, uId);

			for(int i = 0; i < grouplist.size(); i++) {
				Long gId = (Long) grouplist.get(i);
				if(gId != publicGId)
					SDMSMemberTable.table.create(sysEnv, gId, uId);
			}

			if (suActive) {
				sysEnv.cEnv.popGid(sysEnv);
				suActive = false;
			}

			if (with.containsKey(ParseStr.S_PARAMETERS)) {
				WithHash w = (WithHash) with.get(ParseStr.S_PARAMETERS);
				if (w != null)
					createParameters(sysEnv, w, u);
			}
		} catch (Throwable t) {
			if (suActive) {
				sysEnv.cEnv.popGid(sysEnv);
				suActive = false;
			}
			throw t;
		}
		if (ug.contains(SDMSObject.adminGId)) {
			if (userEquiv != null) {
				for (int i = 0; i < userEquiv.size(); ++i) {
					Object o = (Object) userEquiv.get(i);
					if (o instanceof String) {
						try {
							SDMSUser eu = SDMSUserTable.idx_name_getUnique(sysEnv, (String) o);
							if (!eu.getDeleteVersion(sysEnv).equals(SDMSConstants.lZERO)) {
								throw new NotFoundException(new SDMSMessage(sysEnv, "03707311526", "User " + o.toString() + " not found"));
							}
							SDMSUserEquivTable.table.create(sysEnv, u.getId(sysEnv), SDMSConstants.UE_USER, eu.getId(sysEnv));
						} catch (NotFoundException nfe) {
							throw nfe;
						}
					} else {
						if (o instanceof PathVector) {
							try {
								SDMSScope s = SDMSScopeTable.getScope(sysEnv, (PathVector) o);
								if (s.getType(sysEnv).intValue() != SDMSScope.SERVER) {
									throw new NotFoundException(new SDMSMessage(sysEnv, "03707311527", "No job server " + o.toString() + " found"));
								}
								SDMSUserEquivTable.table.create(sysEnv, u.getId(sysEnv), SDMSConstants.UE_SERVER, s.getId(sysEnv));
							} catch (NotFoundException nfe) {

							}
						}
					}
				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03201291636", "User created"));
	}

}

