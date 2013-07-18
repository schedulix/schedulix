/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class ShowEnvironment extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowEnvironment.java,v 2.13.2.2 2013/06/18 09:49:35 ronald Exp $";

	private ObjectURL url;
	private HashSet expandIds;

	public ShowEnvironment(ObjectURL u, HashSet h)
	{
		super();
		url = u;
		expandIds = h;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSNamedEnvironment ne;
		SDMSEnvironment e;
		SDMSNamedResource nr;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		int i;

		ne = (SDMSNamedEnvironment) url.resolve(sysEnv);
		if(!ne.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402041659", "Insufficient privileges"));
		Long neId = ne.getId(sysEnv);

		desc.add("ID");

		desc.add("NAME");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("RESOURCES");

		desc.add("JOB_DEFINITIONS");

		Vector data = new Vector();

		data.add(neId);
		data.add(ne.getName(sysEnv));
		data.add(getCommentDescription(sysEnv, neId));
		data.add(getCommentInfoType(sysEnv, neId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, ne.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(ne.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, ne.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(ne.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(ne.getPrivileges(sysEnv).toString());

		Vector r_desc = new Vector();

		r_desc.add("ID");

		r_desc.add("NR_NAME");

		r_desc.add("CONDITION");
		r_desc.add("PRIVS");

		SDMSOutputContainer s_container = new SDMSOutputContainer(sysEnv, null, r_desc);

		Vector e_v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, neId);
		for(i = 0; i < e_v.size(); i++) {
			e = (SDMSEnvironment) e_v.get(i);
			nr = SDMSNamedResourceTable.getObject(sysEnv, e.getNrId(sysEnv));
			Vector s_data = new Vector();

			s_data.add(nr.getId(sysEnv));
			s_data.add(nr.pathVector(sysEnv));
			s_data.add(e.getCondition(sysEnv));
			s_data.add(nr.getPrivileges(sysEnv).toString());
			s_container.addData(sysEnv, s_data);
		}

		Collections.sort(s_container.dataset, s_container.getComparator(sysEnv, 1));
		data.add(s_container);

		data.add(jobDefContainer(sysEnv, neId));

		d_container = new SDMSOutputContainer(sysEnv, "Environment", desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(
		        new SDMSMessage(sysEnv, "03201232124", "Environment shown"));
	}

	private	SDMSOutputContainer jobDefContainer (SystemEnvironment sysEnv, Long neId)
	throws SDMSException
	{
		FolderLister fl = new FolderLister(null, expandIds);
		fl.setTitle(null);
		fl.setFormatter(new SeSeFormatter(neId));
		int sc[] = new int[1];
		sc[0] = 1;
		fl.setSortColumns(sc);

		Vector j_v = SDMSSchedulingEntityTable.idx_neId.getVector(sysEnv, neId);
		Vector f_v = SDMSFolderTable.idx_envId.getVector(sysEnv, neId);
		Vector nj_v = new Vector();
		for(int i = 0; i < j_v.size(); i++) {
			SDMSSchedulingEntity sme = (SDMSSchedulingEntity) j_v.get(i);
			if (sme.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				nj_v.add(sme);
		}
		for(int i = 0; i < f_v.size(); i++) {
			SDMSFolder f = (SDMSFolder) f_v.get(i);
			if (f.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				nj_v.add(f);
		}

		fl.setObjectsToList(nj_v);

		SDMSOutputContainer s_container = fl.list(sysEnv);

		return s_container;

	}
}

class SeSeFormatter implements Formatter
{

	public static final String asterisk = "*";
	public static final String empty = "";

	private Long sEnvId;

	public SeSeFormatter(Long sEnvId)
	{
		this.sEnvId = sEnvId;
	}

	public Vector fillHeadInfo()
	{
		Vector j_desc = new Vector();

		j_desc.add("ID");

		j_desc.add("SE_PATH");

		j_desc.add("TYPE");

		j_desc.add("ENV");

		j_desc.add("HAS_CHILDREN");
		j_desc.add("PRIVS");

		return j_desc;
	}

	public Vector fillVector(SystemEnvironment sysEnv, SDMSProxy co, HashSet parentSet)
	throws SDMSException
	{
		Vector v = new Vector();
		if(co instanceof SDMSFolder) {
			fillFVector(sysEnv, (SDMSFolder) co, v, parentSet);
		} else {
			fillSeVector(sysEnv, (SDMSSchedulingEntity) co, v);
		}
		return v;
	}

	private void fillSeVector(SystemEnvironment sysEnv, SDMSSchedulingEntity co, Vector v)
	throws SDMSException
	{
		v.add(co.getId(sysEnv));
		v.add(co.pathVector(sysEnv));
		v.add(co.getTypeAsString(sysEnv));
		v.add(asterisk);
		v.add(Boolean.FALSE);
		v.add(co.getPrivileges(sysEnv).toString());
	}

	private void fillFVector(SystemEnvironment sysEnv, SDMSFolder co, Vector v, HashSet parentSet)
	throws SDMSException
	{
		Long envId;
		Long coId = co.getId(sysEnv);
		v.add(coId);
		v.add(co.pathVector(sysEnv));
		v.add("FOLDER");
		envId = co.getEnvId(sysEnv);
		if (envId != null && envId.equals(sEnvId))
			v.add(asterisk);
		else
			v.add(empty);
		if (parentSet.contains(coId))
			v.add(Boolean.TRUE);
		else
			v.add(Boolean.FALSE);
		v.add(co.getPrivileges(sysEnv).toString());
	}
}
