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

public class ShowUser extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowUser.java,v 2.9.4.2 2013/06/18 09:49:38 ronald Exp $";

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
	private final static String NP  = "nice profile";
	private final static String WT  = "watch type";
	private final static String SEL = "select";

	private String name;

	public ShowUser (String n)
	{
		super();
		name = n;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSUser u;
		SDMSOutputContainer d_container = null;
		SDMSOutputContainer c_container = null;
		Vector desc = new Vector();
		Vector c_desc = new Vector();
		Long sip;
		Date d = new Date();

		if(name == null) {
			u = SDMSUserTable.getObject(sysEnv, env.uid());
		} else {
			u = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(name, new Long(0)));
		}
		if (u.getDeleteVersion(sysEnv) > 0)
			throw new NotFoundException("User " + (name == null ? "" : name + " ") + " not found");
		if(!u.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411728", "Insufficient privileges"));
		Long uId = u.getId(sysEnv);

		desc.add("ID");
		desc.add("NAME");
		desc.add("IS_ENABLED");
		desc.add("DEFAULT_GROUP");
		desc.add("CONNECTION_TYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");
		desc.add("MANAGE_PRIVS");
		desc.add("GROUPS");
		desc.add("EQUIVALENT_USERS");
		desc.add("PARAMETERS");
		desc.add("COMMENTTYPE");
		desc.add("COMMENT");
		c_desc.add("TAG");
		c_desc.add("COMMENT");

		Vector data = new Vector();

		data.add(uId);
		data.add(u.getName(sysEnv));

		data.add(u.getIsEnabled(sysEnv));
		data.add(SDMSGroupTable.getObject(sysEnv, u.getDefaultGId(sysEnv)).getName(sysEnv));
		data.add(u.getConnectionTypeAsString(sysEnv));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, u.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(u.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, u.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(u.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(u.getPrivileges(sysEnv).toString());
		data.add(getManageList(sysEnv, uId));
		data.add(getGroupList(sysEnv, uId));
		data.add(getEquivUserList(sysEnv, uId));
		data.add(getParms(sysEnv, uId));

		data.add(getCommentInfoType(sysEnv, uId));
		data.add(getCommentContainer(sysEnv, uId));

		d_container = new SDMSOutputContainer(sysEnv, "User", desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03112141435", "User shown"));
	}

	private SDMSOutputContainer getGroupList(SystemEnvironment sysEnv, Long uId)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;

		Vector desc = new Vector(2);
		desc.add("ID");
		desc.add("NAME");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv, "GROUPS", desc);

		Vector v = SDMSMemberTable.idx_uId.getVector(sysEnv, uId);
		for(int i = 0; i < v.size(); i++) {
			SDMSMember m = (SDMSMember) v.get(i);
			SDMSGroup g = SDMSGroupTable.getObject(sysEnv, m.getGId(sysEnv));
			Vector data = new Vector();
			data.add(g.getId(sysEnv));
			data.add(g.getName(sysEnv));
			data.add(g.getPrivileges(sysEnv).toString());

			d_container.addData(sysEnv, data);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		return d_container;
	}

	private SDMSOutputContainer getEquivUserList(SystemEnvironment sysEnv, Long uId)
	throws SDMSException
	{
		SDMSOutputContainer d_container = null;

		Vector desc = new Vector(2);
		desc.add("TYPE");
		desc.add("EQUIVALENT_USER");

		d_container = new SDMSOutputContainer(sysEnv, "EQUIVALENT_USERS", desc);

		Vector v = SDMSUserEquivTable.idx_uId.getVector(sysEnv, uId);
		SDMSUser eu;
		SDMSScope es;
		SDMSUserEquiv equiv;
		for(int i = 0; i < v.size(); i++) {
			equiv = (SDMSUserEquiv) v.get(i);
			Vector data = new Vector();
			data.add(equiv.getAltTypeAsString(sysEnv));
			if (equiv.getAltType(sysEnv).intValue() == SDMSUserEquiv.USER) {
				eu = SDMSUserTable.getObject(sysEnv, equiv.getAltUId(sysEnv));
				data.add(eu.getName(sysEnv));
			} else {
				es = SDMSScopeTable.getObject(sysEnv, equiv.getAltUId(sysEnv));
				data.add(es.pathString(sysEnv));
			}

			d_container.addData(sysEnv, data);
		}

		return d_container;
	}

	private SDMSOutputContainer getManageList(SystemEnvironment sysEnv, Long uId)
		throws SDMSException
	{
		SDMSOutputContainer dc = null;

		Vector desc = new Vector(2);
		desc.add("PRIVS");

		dc = new SDMSOutputContainer(sysEnv, "MANAGE_PRIVS", desc);

		SDMSPrivilege p = new SDMSPrivilege();
		Vector v = SDMSMemberTable.idx_uId.getVector(sysEnv, uId);
		for(int i = 0; i < v.size(); i++) {
			SDMSMember m = (SDMSMember) v.get(i);
			try {
				SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(ZERO, m.getGId(sysEnv)));
				p.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
			} catch (NotFoundException nfe) {
			}
		}
		long pr = p.toLong().longValue();
		if ((SDMSPrivilege.MANAGE_ALL & pr) != SDMSPrivilege.NOPRIVS) {
			v = new Vector();
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
			if ((SDMSPrivilege.MANAGE_FP & pr) ==  SDMSPrivilege.MANAGE_FP)	 {
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
			if ((SDMSPrivilege.MANAGE_NP & pr) ==  SDMSPrivilege.MANAGE_NP) {
				v.add(NP);
				dc.addData(sysEnv, v);
				v = new Vector();
			}
			if ((SDMSPrivilege.MANAGE_WT & pr) ==  SDMSPrivilege.MANAGE_WT) {
				v.add(WT);
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

	public static final SDMSOutputContainer getParms (final SystemEnvironment sysEnv, final Long id)
	throws SDMSException
	{
		final Vector c_desc = new Vector();
		c_desc.add ("ID");
		c_desc.add ("NAME");
		c_desc.add ("VALUE");

		final SDMSOutputContainer c_container = new SDMSOutputContainer (sysEnv, null, c_desc);

		final Vector up_v = SDMSUserParameterTable.idx_uId.getVector (sysEnv, id);

		for (int i = 0; i < up_v.size(); ++i) {
			final SDMSUserParameter up = (SDMSUserParameter) up_v.get (i);

			final Vector c_data = new Vector();
			final Long upId = up.getId (sysEnv);
			c_data.add (upId);
			c_data.add (up.getName (sysEnv));
			c_data.add (up.getValue (sysEnv));

			c_container.addData (sysEnv, c_data);
		}

		Collections.sort (c_container.dataset, c_container.getComparator (sysEnv, 1));

		return c_container;
	}

}

