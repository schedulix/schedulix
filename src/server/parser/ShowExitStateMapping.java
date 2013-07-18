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

public class ShowExitStateMapping extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowExitStateMapping.java,v 2.5.8.2 2013/06/18 09:49:35 ronald Exp $";

	private final ObjectURL url;

	public ShowExitStateMapping (ObjectURL u)
	{
		super();
		url = u;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final SDMSExitStateMappingProfile esmp;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		esmp = (SDMSExitStateMappingProfile) url.resolve(sysEnv);
		if(!esmp.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402041712", "Insufficient privileges"));
		Long esmpId = esmp.getId(sysEnv);

		desc.add("ID");

		desc.add("NAME");

		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("RANGES");

		Vector data = new Vector();
		data.add(esmpId);
		data.add(esmp.getName(sysEnv));
		data.add(getCommentDescription(sysEnv, esmpId));
		data.add(getCommentInfoType(sysEnv, esmpId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, esmp.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(esmp.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, esmp.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(esmp.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(esmp.getPrivileges(sysEnv).toString());

		Vector r_desc = new Vector();

		r_desc.add("ECR_START");

		r_desc.add("ECR_END");

		r_desc.add("ESD_NAME");

		SDMSOutputContainer r_container = new SDMSOutputContainer(sysEnv, null, r_desc);

		Vector esm_v = SDMSExitStateMappingTable.idx_esmpId.getVector(sysEnv, esmpId);

		SDMSExitStateMapping esm;
		SDMSExitStateDefinition esd;

		Vector r_data;
		Iterator i = esm_v.iterator();
		while (i.hasNext()) {
			esm = (SDMSExitStateMapping)(i.next());
			r_data = new Vector();
			r_data.add(esm.getEcrStart(sysEnv));
			r_data.add(esm.getEcrEnd(sysEnv));
			esd = SDMSExitStateDefinitionTable.getObject(sysEnv, esm.getEsdId(sysEnv));
			r_data.add(esd.getName(sysEnv));
			r_container.addData(sysEnv, r_data);
		}

		Collections.sort(r_container.dataset, r_container.getComparator(sysEnv, 0));

		data.add(r_container);
		d_container = new SDMSOutputContainer(sysEnv,
		                                      new SDMSMessage(sysEnv, "03201292005", "Exit State Mapping"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "02111082035", "Exit State Mapping shown"));
	}
}

