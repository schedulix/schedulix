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

public class ShowRsp extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowRsp.java,v 2.4.8.2 2013/06/18 09:49:37 ronald Exp $";

	String name;

	public ShowRsp(String n)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		name = n;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateProfile rsp;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		rsp = SDMSResourceStateProfileTable.idx_name_getUnique(sysEnv, name);
		if(!rsp.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411722", "Insufficient privileges"));
		Long rspId = rsp.getId(sysEnv);

		desc.add("ID");
		desc.add("NAME");

		desc.add("INITIAL_STATE");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("STATES");

		Vector data = new Vector();

		data.add(rspId);
		data.add(rsp.getName(sysEnv));

		Long rsdId = rsp.getInitialRsdId(sysEnv);
		SDMSResourceStateDefinition rsd;
		if (rsdId != null) {
			rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, rsdId);
			data.add(rsd.getName(sysEnv));
		} else {
			data.add(null);
		}
		data.add(getCommentDescription(sysEnv, rspId));
		data.add(getCommentInfoType(sysEnv, rspId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, rsp.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(rsp.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, rsp.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(rsp.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(rsp.getPrivileges(sysEnv).toString());

		Vector r_desc = new Vector();

		r_desc.add("ID");

		r_desc.add("RSD_NAME");
		r_desc.add("PRIVS");

		SDMSOutputContainer s_container = new SDMSOutputContainer(sysEnv, null, r_desc);

		Vector rs_v = SDMSResourceStateTable.idx_rspId.getVector(sysEnv, rspId);

		SDMSResourceState rs;

		Vector s_data;
		Iterator rsi = rs_v.iterator();
		while (rsi.hasNext()) {
			rs = (SDMSResourceState)(rsi.next());
			s_data = new Vector();
			s_data.add(rs.getId(sysEnv));
			rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, rs.getRsdId(sysEnv));
			s_data.add(rsd.getName(sysEnv));
			s_data.add(rsd.getPrivileges(sysEnv).toString());
			s_container.addData(sysEnv, s_data);
		}

		Collections.sort(s_container.dataset, s_container.getComparator(sysEnv, 1));

		data.add(s_container);

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage(sysEnv, "03201101454", "Resource State Profile"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "03201292056", "Resource State Profile shown"));

	}

}

