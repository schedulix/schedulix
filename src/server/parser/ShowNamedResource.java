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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ShowNamedResource extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowNamedResource.java,v 2.21.2.3 2013/06/18 09:49:36 ronald Exp $";

	private ObjectURL url;
	HashSet expandIds;

	public ShowNamedResource(ObjectURL u)
	{
		super();
		url = u;
		expandIds = null;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ShowNamedResource(ObjectURL u, HashSet h)
	{
		super();
		url = u;
		expandIds = h;
		txMode = SDMSTransaction.READONLY;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSNamedResource nr;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Long rspId;

		nr = (SDMSNamedResource) url.resolve(sysEnv);
		if(!nr.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411718", "Insufficient privileges"));
		Long nrId = nr.getId(sysEnv);

		desc.add("ID");

		desc.add("NAME");

		desc.add("OWNER");

		desc.add("USAGE");

		desc.add("INHERIT_PRIVS");

		desc.add("RESOURCE_STATE_PROFILE");

		desc.add("FACTOR");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("RESOURCES");

		desc.add("PARAMETERS");

		desc.add("JOB_DEFINITIONS");

		Vector data = new Vector();

		data.add(nrId);
		data.add(nr.pathVector(sysEnv));

		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, nr.getOwnerId(sysEnv));
		data.add(g.getName(sysEnv));

		data.add(nr.getUsageAsString(sysEnv));
		data.add(new SDMSPrivilege(sysEnv, nr.getInheritPrivs(sysEnv).longValue()).toString());

		rspId = nr.getRspId(sysEnv);

		if(rspId != null) {
			data.add(SDMSResourceStateProfileTable.getObject(sysEnv, rspId).getName(sysEnv));
		} else {
			data.add(null);
		}
		data.add(nr.getFactor(sysEnv));
		data.add(getCommentDescription(sysEnv, nrId));
		data.add(getCommentInfoType(sysEnv, nrId));

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, nr.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(nr.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, nr.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(nr.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(nr.getPrivileges(sysEnv).toString());

		add_resources(sysEnv, nrId, data);

		add_parameters (sysEnv, nrId, data);

		add_job_definitions(sysEnv, nr, nrId, data);

		d_container = new SDMSOutputContainer(sysEnv, "Named Resource", desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03112182117", "Named Resource shown"));
	}

	private void add_resources(SystemEnvironment sysEnv, Long nrId, Vector v)
	throws SDMSException
	{
		final Vector desc = new Vector();
		SDMSScope s;
		SDMSFolder f;
		SDMSSubmittedEntity sme;
		SDMSGroup g;
		Vector data;
		Vector rv;
		Vector plv;
		SDMSResource r;
		SDMSResourceTemplate rt;

		desc.add("ID");

		desc.add("SCOPE");

		desc.add("TYPE");
		desc.add("OWNER");

		desc.add("STATE");

		desc.add("REQUESTABLE_AMOUNT");

		desc.add("AMOUNT");

		desc.add("FREE_AMOUNT");

		desc.add("IS_ONLINE");
		desc.add("PRIVS");

		final SDMSOutputContainer r_container = new SDMSOutputContainer(sysEnv, null, desc);
		rv = SDMSResourceTable.idx_nrId.getVector(sysEnv, nrId);
		for(int i = 0; i < rv.size(); ++i) {
			r = (SDMSResource) rv.get(i);
			data = new Vector();

			data.add(r.getId(sysEnv));

			try {
				s = SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv));
				data.add(s.pathVector(sysEnv));
				data.add(s.getTypeAsString(sysEnv));
			} catch (final NotFoundException nfe) {
				try {
					f = SDMSFolderTable.getObject(sysEnv, r.getScopeId(sysEnv));
					data.add(f.pathVector(sysEnv));
					data.add("FOLDER");
				} catch (final NotFoundException nfe2) {
					sme = SDMSSubmittedEntityTable.getObject(sysEnv, r.getScopeId(sysEnv));
					SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv).longValue());
					PathVector pv = se.pathVector(sysEnv);
					pv.add("[" + sme.getId(sysEnv) + "]");
					data.add(pv);
					data.add("JOB");
				}
			}

			g = SDMSGroupTable.getObject(sysEnv, r.getOwnerId(sysEnv));
			data.add(g.getName(sysEnv));

			if(r.getRsdId(sysEnv) != null) {
				data.add(SDMSResourceStateDefinitionTable.getObject(sysEnv, r.getRsdId(sysEnv)).getName(sysEnv));
			} else {
				data.add(null);
			}
			Integer someAmount;
			someAmount = r.getRequestableAmount(sysEnv);
			data.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			someAmount = r.getAmount(sysEnv);
			data.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			someAmount = r.getFreeAmount(sysEnv);
			data.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			data.add(r.getIsOnline(sysEnv));
			data.add(r.getPrivileges(sysEnv).toString());

			r_container.addData(sysEnv, data);
		}
		rv = SDMSResourceTemplateTable.idx_nrId.getVector(sysEnv, nrId);
		for(int i = 0; i < rv.size(); ++i) {
			rt = (SDMSResourceTemplate) rv.get(i);
			data = new Vector();

			data.add(rt.getId(sysEnv));

			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, rt.getSeId(sysEnv));
			data.add(se.pathVector(sysEnv));
			data.add("JOB");

			g = SDMSGroupTable.getObject(sysEnv, rt.getOwnerId(sysEnv));
			data.add(g.getName(sysEnv));

			if(rt.getRsdId(sysEnv) != null) {
				data.add(SDMSResourceStateDefinitionTable.getObject(sysEnv, rt.getRsdId(sysEnv)).getName(sysEnv));
			} else {
				data.add(null);
			}
			Integer someAmount;
			someAmount = rt.getRequestableAmount(sysEnv);
			data.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			someAmount = rt.getAmount(sysEnv);
			data.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			data.add(someAmount == null ? (Object)"INFINITE" : (Object)someAmount);
			data.add(rt.getIsOnline(sysEnv));
			data.add(rt.getPrivileges(sysEnv).toString());

			r_container.addData(sysEnv, data);
		}
		Collections.sort(r_container.dataset, r_container.getComparator(sysEnv, 2, 1));

		v.add(r_container);
	}

	private void add_parameters(SystemEnvironment sysEnv, Long nrId, Vector v)
	throws SDMSException
	{
		final Vector c_desc = new Vector();

		c_desc.add("ID");

		c_desc.add ("NAME");

		c_desc.add ("TYPE");

		c_desc.add ("DEFAULT_VALUE");
		c_desc.add ("COMMENT");
		c_desc.add ("COMMENTTYPE");

		final SDMSOutputContainer c_container = new SDMSOutputContainer (sysEnv, null, c_desc);

		SDMSNamedResource actNr;
		actNr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
		final Vector pd_v = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, nrId);

		for (int i = 0; i < pd_v.size(); ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) pd_v.get (i);
			render_parameter(sysEnv, actNr, c_container, pd);
		}

		Collections.sort (c_container.dataset, c_container.getComparator (sysEnv, 1));

		v.add(c_container);
	}

	private void render_parameter(SystemEnvironment sysEnv, SDMSNamedResource nr, SDMSOutputContainer c, SDMSParameterDefinition pd)
	throws SDMSException
	{
		final String name = pd.getName (sysEnv);
		final String value = pd.getDefaultValue(sysEnv);
		final Long pdId = pd.getId (sysEnv);

		final Vector c_data = new Vector();

		c_data.add (pdId);
		c_data.add (name);
		c_data.add (pd.getTypeAsString (sysEnv));
		if(value != null)
			c_data.add(value.substring(1));
		else
			c_data.add(null);

		try {
			final SDMSObjectComment oc = SDMSObjectCommentTable.idx_objectId_getUnique(sysEnv, pdId);
			c_data.add (oc.getDescription(sysEnv));
			c_data.add (oc.getInfoTypeAsString(sysEnv));
		} catch(NotFoundException nfe) {
			c_data.add (null);
			c_data.add (null);
		}

		c.addData(sysEnv, c_data);
	}

	public Vector fillHeadInfo()
	{
		Vector c_desc = new Vector();

		c_desc.add("ID");

		c_desc.add("NAME");

		c_desc.add("AMOUNT");

		c_desc.add("KEEP_MODE");

		c_desc.add("IS_STICKY");

		c_desc.add("RESOURCE_STATE_MAPPING");

		c_desc.add("EXPIRED_AMOUNT");

		c_desc.add("EXPIRED_BASE");

		c_desc.add("LOCKMODE");

		c_desc.add("STATES");

		c_desc.add("CONDITION");
		c_desc.add("PRIVS");

		return c_desc;
	}

	private void add_job_definitions(SystemEnvironment sysEnv, SDMSNamedResource nr, Long nrId, Vector v)
	throws SDMSException
	{

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, fillHeadInfo());

		switch(nr.getUsage(sysEnv).intValue()) {
		case SDMSNamedResource.STATIC:
			searchAndRenderStatic(sysEnv, nrId, c_container);
			break;
		case SDMSNamedResource.SYSTEM:
			searchAndRenderSystem(sysEnv, nrId, c_container);
			break;
		case SDMSNamedResource.SYNCHRONIZING:
			searchAndRenderSync(sysEnv, nrId, c_container);
			break;
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 1));

		v.add(c_container);

	}

	private void searchAndRenderStatic(SystemEnvironment sysEnv, Long nrId, SDMSOutputContainer c_container)
	throws SDMSException
	{
		SDMSResourceRequirement rr;
		SDMSEnvironment e;
		SDMSSchedulingEntity se;
		HashSet seset = new HashSet();

		Vector v = SDMSResourceRequirementTable.idx_nrId.getVector(sysEnv, nrId);
		for(int i = 0; i < v.size(); i++) {
			rr = (SDMSResourceRequirement) v.get(i);
			se = SDMSSchedulingEntityTable.getObject(sysEnv, rr.getSeId(sysEnv));
			Vector c_data = new Vector();
			render_rr(sysEnv, c_data, rr, se);
			seset.add(se.getId(sysEnv));

			c_container.addData(sysEnv, c_data);
		}

		Vector ev = SDMSEnvironmentTable.idx_nrId.getVector(sysEnv, nrId);
		for(int i = 0; i < ev.size(); i++) {
			e = (SDMSEnvironment) ev.get(i);
			v = SDMSSchedulingEntityTable.idx_neId.getVector(sysEnv, e.getNeId(sysEnv));
			for(int j = 0; j < v.size(); j++) {
				se = (SDMSSchedulingEntity) v.get(j);
				if(!seset.contains(se.getId(sysEnv))) {
					Vector c_data = new Vector();
					render_env(sysEnv, c_data, se, e);
					seset.add(se.getId(sysEnv));

					c_container.addData(sysEnv, c_data);
				}
			}
		}
	}

	private void searchAndRenderSystem(SystemEnvironment sysEnv, Long nrId, SDMSOutputContainer c_container)
	throws SDMSException
	{
		SDMSResourceRequirement rr;
		SDMSFootprint fp;
		SDMSSchedulingEntity se;
		HashSet seset = new HashSet();
		Vector fpv = new Vector();

		Vector v = SDMSResourceRequirementTable.idx_nrId.getVector(sysEnv, nrId);
		for(int i = 0; i < v.size(); i++) {
			rr = (SDMSResourceRequirement) v.get(i);
			try {
				se = SDMSSchedulingEntityTable.getObject(sysEnv, rr.getSeId(sysEnv));
			} catch(NotFoundException nfe) {

				fpv.add(rr);
				continue;
			}
			Vector c_data = new Vector();
			render_rr(sysEnv, c_data, rr, se);
			seset.add(se.getId(sysEnv));

			c_container.addData(sysEnv, c_data);
		}

		for(int i = 0; i < fpv.size(); i++) {
			rr = (SDMSResourceRequirement) fpv.get(i);
			fp = SDMSFootprintTable.getObject(sysEnv, rr.getSeId(sysEnv));
			v = SDMSSchedulingEntityTable.idx_fpId.getVector(sysEnv, fp.getId(sysEnv));
			for(int j = 0; j < v.size(); j++) {
				se = (SDMSSchedulingEntity) v.get(j);
				if(!seset.contains(se.getId(sysEnv))) {
					Vector c_data = new Vector();
					render_rr(sysEnv, c_data, rr, se);
					seset.add(se.getId(sysEnv));

					c_container.addData(sysEnv, c_data);
				}
			}
		}
	}

	private void searchAndRenderSync(SystemEnvironment sysEnv, Long nrId, SDMSOutputContainer c_container)
	throws SDMSException
	{
		SDMSResourceRequirement rr;
		SDMSSchedulingEntity se;

		Vector v = SDMSResourceRequirementTable.idx_nrId.getVector(sysEnv, nrId);
		for(int i = 0; i < v.size(); i++) {
			rr = (SDMSResourceRequirement) v.get(i);
			se = SDMSSchedulingEntityTable.getObject(sysEnv, rr.getSeId(sysEnv));
			Vector c_data = new Vector();
			render_rr(sysEnv, c_data, rr, se);

			c_container.addData(sysEnv, c_data);
		}
	}

	private void render_rr(SystemEnvironment sysEnv, Vector c_data, SDMSResourceRequirement rr, SDMSSchedulingEntity se)
	throws SDMSException
	{
		SDMSResourceStateMappingProfile rsmp;

		c_data.add(se.getId(sysEnv));
		c_data.add(se.pathVector(sysEnv));
		c_data.add(rr.getAmount(sysEnv));
		c_data.add(rr.getKeepModeAsString(sysEnv));
		c_data.add(rr.getIsSticky(sysEnv));
		Long rsmpId = rr.getRsmpId(sysEnv);
		if(rsmpId != null) {
			rsmp = SDMSResourceStateMappingProfileTable.getObject(sysEnv, rsmpId);
			c_data.add(rsmp.getName(sysEnv));
		} else {
			c_data.add(null);
		}
		c_data.add(rr.getExpiredAmount(sysEnv));
		c_data.add(rr.getExpiredBaseAsString(sysEnv));
		Integer lm = rr.getLockmode(sysEnv);
		if(lm != null) {
			c_data.add((new Lockmode(sysEnv, lm.intValue())).str(sysEnv));
		} else {
			c_data.add(null);
		}
		Vector rrs_v = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, rr.getId(sysEnv));
		String sep = "";
		StringBuffer states = new StringBuffer();
		for(int j = 0; j < rrs_v.size(); j++) {
			SDMSResourceReqStates rrs = (SDMSResourceReqStates) rrs_v.get(j);
			String rsdn = SDMSResourceStateDefinitionTable.getObject(sysEnv, rrs.getRsdId(sysEnv)).getName(sysEnv);
			states.append(sep);
			states.append(rsdn);
			sep = ",";
		}
		c_data.add(new String(states));
		c_data.add(rr.getCondition(sysEnv));
		c_data.add(se.getPrivileges(sysEnv).toString());
	}

	private void render_env(SystemEnvironment sysEnv, Vector c_data, SDMSSchedulingEntity se, SDMSEnvironment e)
	throws SDMSException
	{
		c_data.add(se.getId(sysEnv));
		c_data.add(se.pathVector(sysEnv));
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(e.getCondition(sysEnv));
		c_data.add(se.getPrivileges(sysEnv).toString());
	}

}

