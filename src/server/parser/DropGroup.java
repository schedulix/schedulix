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

public class DropGroup extends Node
{

	public final static String __version = "@(#) $Id: DropGroup.java,v 2.4.6.1 2013/03/14 10:24:30 ronald Exp $";

	private ObjectURL url;
	private boolean noerr;

	public DropGroup(ObjectURL u, Boolean n)
	{
		super();
		url = u;
		noerr = n.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSGroup g;

		try {
			g = (SDMSGroup) url.resolve(sysEnv);
		} catch(NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03312130041", "No group dropped"));
				return;
			}
			throw nfe;
		}

		Long gId = g.getId(sysEnv);

		Vector v = SDMSMemberTable.idx_gId.getVector(sysEnv, gId);
		SDMSMember me = null;
		for(int i = 0; i < v.size(); i++) {
			SDMSMember m = (SDMSMember) v.get(i);
			Long uid = m.getUId(sysEnv);
			SDMSUser u = SDMSUserTable.getObject(sysEnv, uid);
			if(gId.equals(u.getDefaultGId(sysEnv))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03312130038",
				                               "you cannot remove a user from his default group"));
			}
			if (uid.equals(sysEnv.cEnv.uid())) {

				me = m;
			} else
				m.delete(sysEnv);
		}

		String gName = g.getName(sysEnv);

		if (SDMSEventTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181200", "One or more $1 still owned by group $2",
			                               "Event", gName));

		if (SDMSFolderTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181201", "One or more $1 still owned by group $2",
			                               "Folder", gName));

		if (SDMSIntervalTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181202", "One or more $1 still owned by group $2",
			                               "Interval", gName));

		if (SDMSNamedResourceTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181203", "One or more $1 still owned by group $2",
			                               "NamedResource", gName));

		if (SDMSScheduleTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181204", "One or more $1 still owned by group $2",
			                               "Schedule", gName));

		if (SDMSScheduledEventTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181205", "One or more $1 still owned by group $2",
			                               "ScheduledEvent", gName));

		if (SDMSSchedulingEntityTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181206", "One or more $1 still owned by group $2",
			                               "SchedulingEntity", gName));

		if (SDMSScopeTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181207", "One or more $1 still owned by group $2",
			                               "Scope", gName));

		if (SDMSSubmittedEntityTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181208", "One or more $1 still owned by group $2",
			                               "SubmittedEntity", gName));

		if (SDMSResourceTable.idx_ownerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402270834", "One or more $1 still owned by group $2",
			                               "Resource", gName));

		if (SDMSTriggerTable.idx_submitOwnerId.containsKey(sysEnv, gId))
			throw new CommonErrorException(new SDMSMessage(sysEnv,
			                               "02402181209", "One or more Master Trigger still submits for group $1", gName));

		SDMSObjectCommentTable.dropComment (sysEnv, gId);

		g.setDeleteVersion(sysEnv, new Long(sysEnv.tx.txId));

		if (me != null) {
			me.delete(sysEnv);
			sysEnv.cEnv.delGid(gId);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03312091433", "Group disabled"));
	}
}

