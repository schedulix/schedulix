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

public class ShowTrigger extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowTrigger.java,v 2.15.4.3 2013/06/18 09:49:38 ronald Exp $";

	private String name;
	private ObjectURL url;

	public ShowTrigger(ObjectURL u)
	{
		super();
		url = u;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		SDMSOutputContainer r_container = null;
		SDMSTrigger t;
		SDMSTriggerState ts;
		Vector vts;
		Vector desc;
		Vector data;
		Vector rdesc;
		Vector rdata;
		Long fireId;
		Date d_create = new Date();
		Date d_change = new Date();

		t = (SDMSTrigger) url.resolve(sysEnv);

		if(!t.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411727", "Insufficient privileges"));
		Long tId = t.getId(sysEnv);
		vts = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, tId);

		desc = new Vector();

		desc.add("ID");
		desc.add("NAME");

		desc.add("OBJECTTYPE");

		desc.add("OBJECTNAME");

		desc.add("ACTIVE");

		desc.add("ACTION");

		desc.add("SUBMIT_TYPE");

		desc.add("SUBMIT_NAME");

		desc.add("SUBMIT_SE_OWNER");

		desc.add("SUBMIT_PRIVS");

		desc.add("MAIN_TYPE");

		desc.add("MAIN_NAME");

		desc.add("MAIN_SE_OWNER");

		desc.add("MAIN_PRIVS");

		desc.add("PARENT_TYPE");

		desc.add("PARENT_NAME");

		desc.add("PARENT_SE_OWNER");

		desc.add("PARENT_PRIVS");

		desc.add("TRIGGER_TYPE");

		desc.add("MASTER");

		desc.add("IS_INVERSE");

		desc.add("SUBMIT_OWNER");

		desc.add("IS_CREATE");

		desc.add("IS_CHANGE");

		desc.add("IS_DELETE");

		desc.add("IS_GROUP");

		desc.add("MAX_RETRY");

		desc.add("SUSPEND");

		desc.add("RESUME_AT");

		desc.add("RESUME_IN");

		desc.add("RESUME_BASE");

		desc.add("WARN");

		desc.add("CONDITION");

		desc.add("CHECK_AMOUNT");

		desc.add("CHECK_BASE");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");

		desc.add("STATES");

		data = new Vector();
		data.add(tId);
		data.add(t.getName(sysEnv));
		int objType = t.getObjectType(sysEnv).intValue();
		data.add(t.getObjectTypeAsString(sysEnv));
		switch(objType) {
			case SDMSTrigger.JOB_DEFINITION:
				data.add(SDMSSchedulingEntityTable.getObject(sysEnv, t.getFireId(sysEnv)).pathString(sysEnv));
				break;
			case SDMSTrigger.NAMED_RESOURCE:
				data.add(SDMSNamedResourceTable.getObject(sysEnv, t.getFireId(sysEnv)).pathString(sysEnv));
				break;
			case SDMSTrigger.RESOURCE:
				SDMSResource r = SDMSResourceTable.getObject(sysEnv, t.getFireId(sysEnv));
				SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv));
				SDMSScope s = SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv));
				data.add(nr.pathString(sysEnv) + " in " + s.pathString(sysEnv));
				break;
		}
		data.add(t.getIsActive(sysEnv));
		data.add(t.getActionAsString(sysEnv));
		SDMSSchedulingEntity subm_se = SDMSSchedulingEntityTable.getObject(sysEnv, t.getSeId(sysEnv));
		data.add(subm_se.getTypeAsString(sysEnv));
		data.add(subm_se.pathString(sysEnv));
		Long submitSeOwnerId = subm_se.getOwnerId (sysEnv);
		SDMSGroup g = SDMSGroupTable.getObject (sysEnv, submitSeOwnerId);
		data.add (g.getName (sysEnv));
		data.add(subm_se.getPrivileges(sysEnv).toString());

		Long mainSeId = t.getMainSeId(sysEnv);
		if (mainSeId == null) {
			data.add(null);
			data.add(null);
			data.add(null);
			data.add(null);
		} else {
			subm_se = SDMSSchedulingEntityTable.getObject(sysEnv, mainSeId);
			data.add(subm_se.getTypeAsString(sysEnv));
			data.add(subm_se.pathVector(sysEnv));
			submitSeOwnerId = subm_se.getOwnerId (sysEnv);
			g = SDMSGroupTable.getObject (sysEnv, submitSeOwnerId);
			data.add (g.getName (sysEnv));
			data.add(subm_se.getPrivileges(sysEnv).toString());
		}
		Long parentSeId = t.getParentSeId(sysEnv);
		if (parentSeId == null) {
			data.add(null);
			data.add(null);
			data.add(null);
			data.add(null);
		} else {
			subm_se = SDMSSchedulingEntityTable.getObject(sysEnv, parentSeId);
			data.add(subm_se.getTypeAsString(sysEnv));
			data.add(subm_se.pathVector(sysEnv));
			submitSeOwnerId = subm_se.getOwnerId (sysEnv);
			g = SDMSGroupTable.getObject (sysEnv, submitSeOwnerId);
			data.add (g.getName (sysEnv));
			data.add(subm_se.getPrivileges(sysEnv).toString());
		}

		data.add(t.getTypeAsString(sysEnv));
		data.add(t.getIsMaster(sysEnv));
		data.add(t.getIsInverse(sysEnv));

		if (t.getIsMaster(sysEnv).booleanValue()) {
			final Long submitOwnerId = t.getSubmitOwnerId (sysEnv);
			g = SDMSGroupTable.getObject (sysEnv, submitOwnerId);
			data.add (g.getName (sysEnv));
		} else
			data.add (null);

		data.add(t.getIsCreate(sysEnv));
		data.add(t.getIsChange(sysEnv));
		data.add(t.getIsDelete(sysEnv));
		data.add(t.getIsGroup(sysEnv));
		data.add(t.getMaxRetry(sysEnv));
		data.add(t.getIsSuspend(sysEnv));
		data.add(t.getResumeAt(sysEnv));
		data.add(t.getResumeIn(sysEnv));
		data.add(t.getResumeBaseAsString(sysEnv));
		data.add(t.getIsWarnOnLimit(sysEnv));
		data.add(t.getCondition(sysEnv));
		data.add(t.getCheckAmount(sysEnv));
		data.add(t.getCheckBaseAsString(sysEnv));
		data.add(getCommentDescription(sysEnv, tId));
		data.add(getCommentInfoType(sysEnv, tId));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, t.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d_create.setTime(t.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d_create));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, t.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d_change.setTime(t.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d_change));

		rdesc = new Vector();
		rdesc.add("ID");

		rdesc.add("FROM_STATE");

		rdesc.add("TO_STATE");

		r_container = new SDMSOutputContainer(sysEnv, null, rdesc);

		for(int i = 0; i < vts.size(); i++) {
			ts = (SDMSTriggerState) vts.get(i);
			rdata = new Vector();
			rdata.add(ts.getId(sysEnv));
			Long fromStateId = ts.getFromStateId(sysEnv);
			Long toStateId = ts.getToStateId(sysEnv);
			switch(objType) {
				case SDMSTrigger.JOB_DEFINITION:
					if(fromStateId != null) rdata.add(SDMSExitStateDefinitionTable.getObject(sysEnv,fromStateId).getName(sysEnv));
					else			rdata.add(null);
					if(toStateId != null)	rdata.add(SDMSExitStateDefinitionTable.getObject(sysEnv,toStateId).getName(sysEnv));
					else			rdata.add(null);
					break;
				case SDMSTrigger.NAMED_RESOURCE:
				case SDMSTrigger.RESOURCE:
					if(fromStateId != null) rdata.add(SDMSResourceStateDefinitionTable.getObject(sysEnv,fromStateId).getName(sysEnv));
					else			rdata.add(null);
					if(toStateId != null)	rdata.add(SDMSResourceStateDefinitionTable.getObject(sysEnv,toStateId).getName(sysEnv));
					else			rdata.add(null);
					break;
			}
			r_container.addData(sysEnv, rdata);
		}

		Collections.sort(r_container.dataset, r_container.getComparator(sysEnv, 1, 2));

		data.add(r_container);

		d_container = new SDMSOutputContainer(sysEnv,  new SDMSMessage(sysEnv, "03206200010", "Trigger"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "032061911509", "Trigger shown"));
	}
}

