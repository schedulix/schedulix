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

public class ShowExitStateTranslation extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowExitStateTranslation.java,v 2.6.8.4 2013/06/18 09:49:35 ronald Exp $";

	private ObjectURL url;

	public ShowExitStateTranslation (ObjectURL u)
	{
		url = u;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXIT_STATE_TRANSLATION);
		SDMSExitStateTranslationProfile estp;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		estp = (SDMSExitStateTranslationProfile) url.resolve(sysEnv);
		if(!estp.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411714", "Insufficient privileges"));
		Long estpId = estp.getId(sysEnv);

		desc.add("ID");
		desc.add("NAME");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");
		desc.add("TRANSLATION");

		Vector data = new Vector();
		data.add(estpId);
		data.add(estp.getName(sysEnv));
		data.add(getCommentContainer(sysEnv, estpId));
		data.add(getCommentInfoType(sysEnv, estpId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, estp.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(estp.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, estp.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(estp.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(estp.getPrivileges(sysEnv).toString());

		Vector r_desc = new Vector();
		r_desc.add("FROM_ESD_NAME");
		r_desc.add("TO_ESD_NAME");

		SDMSOutputContainer s_container = new SDMSOutputContainer(sysEnv, null, r_desc);

		Vector est_v = SDMSExitStateTranslationTable.idx_estpId.getVector(sysEnv, estpId);

		SDMSExitStateTranslation est;

		Vector s_data;
		Iterator i = est_v.iterator();
		while (i.hasNext()) {
			est = (SDMSExitStateTranslation)(i.next());
			s_data = new Vector();
			SDMSExitStateDefinition esd;
			esd = SDMSExitStateDefinitionTable.getObject(sysEnv, est.getFromEsdId(sysEnv));
			s_data.add(esd.getName(sysEnv));
			esd = SDMSExitStateDefinitionTable.getObject(sysEnv, est.getToEsdId(sysEnv));
			s_data.add(esd.getName(sysEnv));
			s_container.addData(sysEnv, s_data);
		}

		Collections.sort(s_container.dataset, s_container.getComparator(sysEnv, 0));

		data.add(s_container);
		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage(sysEnv, "02111282031", "Exit State Translation"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "02111282032", "Exit State Translation shown"));
	}
}
