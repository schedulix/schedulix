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

public class ShowGroup extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowGroup.java,v 2.9.4.2 2013/06/18 09:49:36 ronald Exp $";

	private final static Long ZERO = new Long(0L);
	private final static String USR = "user";
	private final static String GRP = "group";
	private final static String ESD = "exit state definition";
	private final static String ESP = "exit state profile";
	private final static String ESM = "exit state mapping";
	private final static String EST = "exit state translation";
	private final static String RSD = "resource state definition";
	private final static String RSP = "resource state profile";
	private final static String RSM = "resource state mapping";
	private final static String FPR = "footprint";
	private final static String ENV = "environment";
	private final static String SYS = "system";
	private final static String SEL = "select";

	private ObjectURL url;

	public ShowGroup (ObjectURL u)
	{
		super();
		url = u;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSGroup g;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		g = (SDMSGroup) url.resolve(sysEnv);
		if(!g.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411717", "Insufficient privileges"));
		Long gId = g.getId(sysEnv);

		desc.add("ID");

		desc.add("NAME");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("MANAGE_PRIVS");

		desc.add("USERS");

		Vector data = new Vector();

		data.add(gId);
		data.add(g.getName(sysEnv));

		data.add(getCommentDescription(sysEnv, gId));
		data.add(getCommentInfoType(sysEnv, gId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, g.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(g.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, g.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(g.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(g.getPrivileges(sysEnv).toString());
		data.add(getManageList(sysEnv, gId));
		data.add(getUserList(sysEnv, gId));

		d_container = new SDMSOutputContainer(sysEnv, "Group", desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03312091452", "Group shown"));
	}

	private SDMSOutputContainer getUserList(SystemEnvironment sysEnv, Long gId)
	throws SDMSException
	{
		SDMSOutputContainer d_container = null;

		Vector desc = new Vector();

		desc.add("ID");

		desc.add("UID");
		desc.add("NAME");

		desc.add("IS_ENABLED");

		desc.add("DEFAULT_GROUP");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv, "Users", desc);

		Vector v = SDMSMemberTable.idx_gId.getVector(sysEnv, gId);
		for(int i = 0; i < v.size(); i++) {
			SDMSMember m = (SDMSMember) v.get(i);
			SDMSUser u = SDMSUserTable.getObject(sysEnv, m.getUId(sysEnv));
			if (u.getName(sysEnv).equals(SDMSUser.NOBODY)) continue;
			Vector data = new Vector();

			data.add(m.getId(sysEnv));
			data.add(u.getId(sysEnv));
			data.add(u.getName(sysEnv));
			data.add(u.getIsEnabled(sysEnv));
			data.add(SDMSGroupTable.getObject(sysEnv, u.getDefaultGId(sysEnv)).getName(sysEnv));
			data.add(u.getPrivileges(sysEnv).toString());

			d_container.addData(sysEnv, data);
		}
		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		return d_container;
	}

	private SDMSOutputContainer getManageList(SystemEnvironment sysEnv, Long gId)
	throws SDMSException
	{
		SDMSOutputContainer dc = null;

		Vector desc = new Vector(2);

		desc.add("PRIVS");

		dc = new SDMSOutputContainer(sysEnv, "MANAGE_PRIVS", desc);

		SDMSPrivilege p = new SDMSPrivilege();
		try {
			SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(ZERO , gId));
			p.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
		} catch (NotFoundException nfe) {

		}
		long pr = p.toLong().longValue();
		if ((SDMSPrivilege.MANAGE_ALL & pr) != SDMSPrivilege.NOPRIVS) {
			Vector v = new Vector();
			if ((SDMSPrivilege.MANAGE_USER & pr) ==  SDMSPrivilege.MANAGE_USER) {
				v.add(USR);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_GROUP & pr) ==  SDMSPrivilege.MANAGE_GROUP) {
				v.add(GRP);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_ESD & pr) ==  SDMSPrivilege.MANAGE_ESD) {
				v.add(ESD);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_ESP & pr) ==  SDMSPrivilege.MANAGE_ESP) {
				v.add(ESP);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_ESM & pr) ==  SDMSPrivilege.MANAGE_ESM) {
				v.add(ESM);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_EST & pr) ==  SDMSPrivilege.MANAGE_EST) {
				v.add(EST);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_RSD & pr) ==  SDMSPrivilege.MANAGE_RSD) {
				v.add(RSD);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_RSP & pr) ==  SDMSPrivilege.MANAGE_RSP) {
				v.add(RSP);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_RSM & pr) ==  SDMSPrivilege.MANAGE_RSM) {
				v.add(RSM);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_FP & pr) ==  SDMSPrivilege.MANAGE_FP)  {
				v.add(FPR);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_ENV & pr) ==  SDMSPrivilege.MANAGE_ENV) {
				v.add(ENV);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_SYS & pr) ==  SDMSPrivilege.MANAGE_SYS) {
				v.add(SYS);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_SEL & pr) ==  SDMSPrivilege.MANAGE_SEL) {
				v.add(SEL);
				dc.addData(sysEnv, v);
			}
		}

		Collections.sort(dc.dataset, dc.getComparator(sysEnv, 0));

		return dc;
	}
}

