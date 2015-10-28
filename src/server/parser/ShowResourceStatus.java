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

public class ShowResourceStatus extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowResourceStatus.java,v 2.2.8.1 2013/03/14 10:24:50 ronald Exp $";

	String name;

	public ShowResourceStatus(String n)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		name = n;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateDefinition rsd;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		rsd = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, name);
		if(!rsd.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411720", "Insufficient privileges"));
		Long rsdId = rsd.getId(sysEnv);

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

		data.add(rsdId);
		data.add(rsd.getName(sysEnv));
		data.add(getCommentContainer(sysEnv, rsdId));
		data.add(getCommentInfoType(sysEnv, rsdId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, rsd.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(rsd.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, rsd.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(rsd.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(rsd.getPrivileges(sysEnv).toString());

		d_container = new SDMSOutputContainer(sysEnv, "Resource State Definition", desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "03201100044", "Resource State Definition shown"));

	}

}

