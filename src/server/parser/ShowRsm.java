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

public class ShowRsm extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowRsm.java,v 2.4.8.2 2013/06/18 09:49:37 ronald Exp $";

	String name;

	public ShowRsm(String n)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		name = n;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateMappingProfile rsmp;
		SDMSOutputContainer d_container = null;
		Long rsmpId;
		Long rsdId;
		Vector desc = new Vector();

		rsmp = SDMSResourceStateMappingProfileTable.idx_name_getUnique(sysEnv, name);
		if(!rsmp.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411721", "Insufficient privileges"));
		rsmpId = rsmp.getId(sysEnv);

		desc.add("ID");

		desc.add("NAME");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("MAPPINGS");

		Vector data = new Vector();
		data.add(rsmpId);
		data.add(rsmp.getName(sysEnv));
		data.add(getCommentContainer(sysEnv, rsmpId));
		data.add(getCommentInfoType(sysEnv, rsmpId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, rsmp.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(rsmp.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, rsmp.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(rsmp.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(rsmp.getPrivileges(sysEnv).toString());

		Vector r_desc = new Vector();

		r_desc.add("ESD_NAME");

		r_desc.add("RSD_FROM");

		r_desc.add("RSD_TO");

		SDMSOutputContainer r_container = new SDMSOutputContainer(sysEnv, null, r_desc);

		Vector rsm_v = SDMSResourceStateMappingTable.idx_rsmpId.getVector(sysEnv, rsmpId);

		SDMSResourceStateMapping rsm;
		SDMSExitStateDefinition esd;
		SDMSResourceStateDefinition rsd;

		Vector r_data;
		for (int i = 0; i < rsm_v.size(); i++) {
			rsm = (SDMSResourceStateMapping)(rsm_v.get(i));
			r_data = new Vector();
			esd = SDMSExitStateDefinitionTable.getObject(sysEnv, rsm.getEsdId(sysEnv));
			r_data.add(esd.getName(sysEnv));
			rsdId = rsm.getFromRsdId(sysEnv);
			if(rsdId == null) {
				r_data.add("ANY");
			} else {
				rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, rsdId);
				r_data.add(rsd.getName(sysEnv));
			}
			rsdId = rsm.getToRsdId(sysEnv);
			rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, rsdId);
			r_data.add(rsd.getName(sysEnv));
			r_container.addData(sysEnv, r_data);
		}

		Collections.sort(r_container.dataset, r_container.getComparator(sysEnv, 0, 1));

		data.add(r_container);
		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage(sysEnv, "03204021426", "Resource State Mapping"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03204021427", "Resource State Mapping shown"));
	}

}

