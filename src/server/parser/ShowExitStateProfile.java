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

public class ShowExitStateProfile extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowExitStateProfile.java,v 2.11.2.2 2013/06/18 09:49:35 ronald Exp $";

	private ObjectURL url;

	public ShowExitStateProfile (ObjectURL u)
	{
		url = u;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateProfile esp;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		esp = (SDMSExitStateProfile) url.resolve(sysEnv);
		if(!esp.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411713", "Insufficient privileges"));
		Long espId = esp.getId(sysEnv);

		desc.add("ID");
		desc.add("NAME");
		desc.add("DEFAULT_ESM_NAME");
		desc.add("IS_VALID");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");
		desc.add("STATES");

		Long defaultEsmpId;
		SDMSExitStateMappingProfile esmp;

		Vector data = new Vector();
		data.add(espId);
		data.add(esp.getName(sysEnv));
		defaultEsmpId = esp.getDefaultEsmpId(sysEnv);
		if (defaultEsmpId != null) {
			esmp = SDMSExitStateMappingProfileTable.getObject(sysEnv, defaultEsmpId);
			data.add(esmp.getName(sysEnv));
		} else {
			data.add(SystemEnvironment.nullString);
		}
		data.add(esp.getIsValid(sysEnv));
		data.add(getCommentContainer(sysEnv, espId));
		data.add(getCommentInfoType(sysEnv, espId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, esp.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(esp.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, esp.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(esp.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(esp.getPrivileges(sysEnv).toString());

		Vector r_desc = new Vector();
		r_desc.add("ID");
		r_desc.add("PREFERENCE");
		r_desc.add("TYPE");
		r_desc.add("ESD_NAME");
		r_desc.add("IS_UNREACHABLE");
		r_desc.add("IS_DISABLED");
		r_desc.add("IS_BROKEN");
		r_desc.add("IS_BATCH_DEFAULT");
		r_desc.add("IS_DEPENDENCY_DEFAULT");

		SDMSOutputContainer s_container = new SDMSOutputContainer(sysEnv, null, r_desc);

		Vector es_v = SDMSExitStateTable.idx_espId.getVector(sysEnv, espId);

		SDMSExitState es;

		Vector s_data;
		Iterator i = es_v.iterator();
		while (i.hasNext()) {
			es = (SDMSExitState)(i.next());
			s_data = new Vector();
			s_data.add(es.getId(sysEnv));
			s_data.add(es.getPreference(sysEnv));
			if (es.getIsFinal(sysEnv).booleanValue()) {
				s_data.add("FINAL");
			} else {
				if (es.getIsRestartable(sysEnv).booleanValue()) {
					s_data.add("RESTARTABLE");
				} else {
					s_data.add("PENDING");
				}
			}
			SDMSExitStateDefinition esd = (SDMSExitStateDefinitionTable.getObject(sysEnv, es.getEsdId(sysEnv)));
			s_data.add(esd.getName(sysEnv));
			s_data.add(es.getIsUnreachable(sysEnv));
			s_data.add(es.getIsDisabled(sysEnv));
			s_data.add(es.getIsBroken(sysEnv));
			s_data.add(es.getIsBatchDefault(sysEnv));
			s_data.add(es.getIsDependencyDefault(sysEnv));
			s_container.addData(sysEnv, s_data);
		}

		Collections.sort(s_container.dataset, s_container.getComparator(sysEnv, 1));

		data.add(s_container);

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage(sysEnv, "02111082039", "Exit State Profile"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "02111082040", "Exit State Profile shown"));
	}
}

