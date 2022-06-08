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

import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ShowSchedule
	extends ShowCommented
{
	public static final String __version = "@(#) $Id: ShowSchedule.java,v 2.7.2.2 2013/06/18 09:49:37 ronald Exp $";

	private final Vector path;

	public ShowSchedule (Vector p)
	{
		super();
		path = p;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Long sceId = SDMSScheduleTable.pathToId (sysEnv, path);
		final SDMSSchedule sce = SDMSScheduleTable.getObject (sysEnv, sceId);
		if(!sce.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411723", "Insufficient privileges"));

		Vector desc = new Vector();

		desc.add ("ID");
		desc.add ("NAME");
		desc.add ("OWNER");

		desc.add("INHERIT_PRIVS");

		desc.add ("INTERVAL");

		desc.add ("TIME_ZONE");

		desc.add ("ACTIVE");

		desc.add ("EFF_ACTIVE");
		desc.add ("CREATOR");
		desc.add ("CREATE_TIME");
		desc.add ("CHANGER");
		desc.add ("CHANGE_TIME");
		desc.add ("PRIVS");
		desc.add ("COMMENT");
		desc.add ("COMMENTTYPE");

		final Vector data = new Vector();
		final Long sciId = sce.getId (sysEnv);

		data.add (sceId);
		data.add (sce.pathVector (sysEnv));

		final Long ownerId = sce.getOwnerId (sysEnv);
		final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
		data.add (g.getName (sysEnv));

		data.add(new SDMSPrivilege(sysEnv, sce.getInheritPrivs(sysEnv).longValue()).toString());

		data.add (ScheduleUtil.getIvalName (sysEnv, sce));

		data.add (sce.getTimeZone (sysEnv));

		data.add (sce.getIsActive (sysEnv));

		data.add (Boolean.valueOf (sce.isReallyActive (sysEnv)));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, sce.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(sce.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, sce.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(sce.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(sce.getPrivileges(sysEnv).toString());

		data.add(getCommentContainer(sysEnv, sceId));
		data.add(getCommentInfoType(sysEnv, sceId));

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "Schedule", desc, data);
		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04207260049", "Schedule shown"));
	}
}
