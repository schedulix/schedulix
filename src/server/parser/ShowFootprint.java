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

public class ShowFootprint extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowFootprint.java,v 2.9.2.2 2013/06/18 09:49:36 ronald Exp $";

	private final ObjectURL url;
	private final HashSet expandIds;

	public ShowFootprint(ObjectURL u, HashSet h)
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		expandIds = h;
		url = u;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		SDMSFootprint fp;
		Vector desc = new Vector();

		fp = (SDMSFootprint) url.resolve(sysEnv);
		if(!fp.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411716", "Insufficient privileges"));
		Long fpId = fp.getId(sysEnv);

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

		data.add(fpId);
		data.add(fp.getName(sysEnv));
		data.add(getCommentDescription(sysEnv, fpId));
		data.add(getCommentInfoType(sysEnv, fpId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, fp.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(fp.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, fp.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(fp.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(fp.getPrivileges(sysEnv).toString());
		data.add(requirementsContainer(sysEnv, fpId));
		data.add(jobDefContainer(sysEnv, fpId));

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03112201804", "Footprint"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback( new SDMSMessage(sysEnv, "03204092347", "Footprint shown"));

	}

	private	SDMSOutputContainer requirementsContainer (SystemEnvironment sysEnv, Long fpId)
	throws SDMSException
	{
		SDMSResourceRequirement rr;

		Vector c_desc = new Vector();

		c_desc.add("ID");

		c_desc.add("RESOURCE_NAME");

		c_desc.add("AMOUNT");

		c_desc.add("KEEP_MODE");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		Vector rr_v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, fpId);

		Vector c_data;
		for(int i = 0; i < rr_v.size(); i++) {
			rr = (SDMSResourceRequirement)(rr_v.get(i));
			c_data = new Vector();
			c_data.add(rr.getId(sysEnv));
			SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, rr.getNrId(sysEnv));
			c_data.add(nr.pathVector(sysEnv));
			c_data.add(rr.getAmount(sysEnv));
			c_data.add(rr.getKeepModeAsString(sysEnv));

			c_container.addData(sysEnv, c_data);
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 1));

		return c_container;
	}

	private	SDMSOutputContainer jobDefContainer (SystemEnvironment sysEnv, Long fpId)
	throws SDMSException
	{
		FolderLister fl = new FolderLister(null, expandIds);
		fl.setTitle(null);
		fl.setFormatter(new SfSeFormatter());
		int sc[] = new int[1];
		sc[0] = 1;
		fl.setSortColumns(sc);

		Vector j_v = SDMSSchedulingEntityTable.idx_fpId.getVector(sysEnv, fpId);

		fl.setObjectsToList(j_v);

		SDMSOutputContainer s_container = fl.list(sysEnv);

		return s_container;

	}
}

class SfSeFormatter implements Formatter
{

	public SfSeFormatter()
	{
	}

	public Vector fillHeadInfo()
	{
		Vector j_desc = new Vector();

		j_desc.add("ID");

		j_desc.add("SE_PATH");

		j_desc.add("TYPE");

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
		v.add(Boolean.TRUE);
		v.add(co.getPrivileges(sysEnv).toString());
	}

	private void fillFVector(SystemEnvironment sysEnv, SDMSFolder co, Vector v, HashSet parentSet)
	throws SDMSException
	{
		Long coId = co.getId(sysEnv);
		v.add(coId);
		v.add(co.pathVector(sysEnv));
		v.add("FOLDER");
		if (parentSet.contains(coId))
			v.add(Boolean.TRUE);
		else
			v.add(Boolean.FALSE);
		v.add(co.getPrivileges(sysEnv).toString());
	}
}
