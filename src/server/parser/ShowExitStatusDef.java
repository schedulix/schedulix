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

public class ShowExitStatusDef extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowExitStatusDef.java,v 2.4.8.2 2013/06/18 09:49:36 ronald Exp $";

	private ObjectURL url;

	public ShowExitStatusDef(ObjectURL u)
	{
		super();
		url = u;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateDefinition esd;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		esd = (SDMSExitStateDefinition) url.resolve(sysEnv);
		if(!esd.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411715", "Insufficient privileges"));
		Long esdId = esd.getId(sysEnv);

		desc.add("ID");

		desc.add("NAME");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		Vector data = new Vector();

		data.add(esdId);
		data.add(esd.getName(sysEnv));
		data.add(getCommentContainer(sysEnv, esdId));
		data.add(getCommentInfoType(sysEnv, esdId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, esd.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(esd.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, esd.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(esd.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(esd.getPrivileges(sysEnv).toString());

		d_container = new SDMSOutputContainer(sysEnv, "Exit State Definition", desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "03111280029", "Exit State Definition shown"));
	}
}

