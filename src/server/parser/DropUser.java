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

		HashSet hg = new HashSet();
		hg.add(SDMSObject.adminGId);
		sysEnv.cEnv.pushGid(sysEnv, hg);

		Vector v = SDMSMemberTable.idx_uId.getVector(sysEnv, u.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			SDMSMember m = (SDMSMember) v.get(i);
			m.delete(sysEnv);
		}
		sysEnv.cEnv.popGid(sysEnv);

		u.setIsEnabled(sysEnv, Boolean.FALSE);
		u.setDeleteVersion(sysEnv, new Long(sysEnv.tx.txId));
		result.setFeedback(new SDMSMessage(sysEnv, "03301272337", "User dropped"));
	}
}

