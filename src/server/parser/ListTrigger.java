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

public class ListTrigger extends Node
{

	public final static String __version = "@(#) $Id: ListTrigger.java,v 2.22.4.3 2013/06/18 09:49:34 ronald Exp $";

	private String name;
	private Vector path;
	private WithItem fireObj;
	private boolean reverse;
	private int maxCount;

	public ListTrigger(Vector p, boolean reverse, Integer maxCount)
	{
		super();
		fireObj = null;
		if(p != null) {
			name = (String) p.remove(p.size() - 1);
			path = p;
		} else {
			name = null;
			path = null;
		}
		txMode = SDMSTransaction.READONLY;
		this.reverse = reverse;
		auditFlag = false;
		this.maxCount = maxCount.intValue();
	}

	public ListTrigger(WithItem o, Integer maxCount)
	{
		super();
		name = null;
		path = null;
		fireObj = o;
		txMode = SDMSTransaction.READONLY;
		this.maxCount = maxCount.intValue();
	}

	private Long getFireId(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long fireId = null;
		SDMSNamedResource nr;
		Vector objpath;
		String objname;
		Vector resourcepath;

		if(fireObj.key.equals(ParseStr.S_JOB)) {
			objpath = (Vector) fireObj.value;
			objname = (String) objpath.remove(objpath.size() - 1);
			SDMSSchedulingEntity se = (SDMSSchedulingEntityTable.get(sysEnv, objpath, objname));
			if(!se.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03407230019", "Insufficient privileges"));
			fireId = se.getId(sysEnv);
		} else if(fireObj.key.equals(ParseStr.S_RESOURCE)) {
			objpath = (Vector) fireObj.value;
			resourcepath = (Vector) objpath.remove(objpath.size() - 1);
			Long scopeId;
			try {
				scopeId = SDMSScopeTable.pathToId(sysEnv, objpath);
			} catch (NotFoundException nfe) {
				scopeId = SDMSFolderTable.pathToId(sysEnv, objpath);
			}
			nr = SDMSNamedResourceTable.getNamedResource(sysEnv, resourcepath);
			Long nrId = nr.getId(sysEnv);
			SDMSResource r = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, new SDMSKey(nrId, scopeId));
			if(!r.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03407230021", "Insufficient privileges"));
			fireId = r.getId(sysEnv);
		} else if(fireObj.key.equals(ParseStr.S_NAMED_RESOURCE)) {
			resourcepath = (Vector) fireObj.value;
			nr = SDMSNamedResourceTable.getNamedResource(sysEnv, resourcepath);
			if(!nr.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03407230018", "Insufficient privileges"));
			fireId = nr.getId(sysEnv);
		}
		return fireId;
	}

	private String quote(String s)
	{
		String result;
		result = s.replace("\\", "\\\\");
		result = result.replace("'", "\\'");

		return result;
	}

	private String renderParameters(SystemEnvironment sysEnv, SDMSTrigger t)
	throws SDMSException
	{
		StringBuffer result = new StringBuffer();
		SDMSTriggerParameter tp;
		String name;
		String expression;
		Long id = t.getId(sysEnv);
		String sep = "";

		Vector v = SDMSTriggerParameterTable.idx_triggerId.getSortedVector(sysEnv, id);
		for (int i = 0; i < v.size(); ++i) {
			tp = (SDMSTriggerParameter) v.get(i);
			name = tp.getName(sysEnv);
			expression = tp.getExpression(sysEnv);
			result.append(sep);
			result.append("'");
			result.append(name);
			result.append("'='");
			result.append(quote(expression));
			result.append("'");
			sep = ",";
		}

		return result.toString();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		SDMSTrigger t;
		Vector desc;
		Vector data;
		Vector v;
		Iterator i;

		if(name != null) {
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.get(sysEnv, path, name);
			if(!se.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03402131651", "Insufficient privileges"));

			if (reverse) {
				v = SDMSTriggerTable.idx_seId.getVector(sysEnv, se.getId(sysEnv));
				i = v.iterator();
			} else {
				v = SDMSTriggerTable.idx_fireId.getVector(sysEnv, se.getId(sysEnv));
				i = v.iterator();
			}
		} else {
			if(fireObj != null) {
				Long fireId = getFireId(sysEnv);
				v = SDMSTriggerTable.idx_fireId.getVector(sysEnv, fireId);
				i = v.iterator();
			} else {
				i = SDMSTriggerTable.table.iterator(sysEnv);
			}
		}

		desc = new Vector();
		desc.add("ID");
		desc.add("NAME");
		desc.add("OBJECT_TYPE");
		desc.add("OBJECT_SUBTYPE");
		desc.add("OBJECT_NAME");
		desc.add("ACTIVE");
		desc.add("ACTION");
		desc.add("STATES");
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
		desc.add("LIMIT_STATE");
		desc.add("CONDITION");
		desc.add("CHECK_AMOUNT");
		desc.add("CHECK_BASE");
		desc.add("PARAMETERS");
		desc.add("PRIVS");
		desc.add("TAG");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03206200023", "List of Triggers"), desc);

		int ctr = 0;
		boolean gotRerun = false;
		while(i.hasNext()) {
			t = (SDMSTrigger) i.next();
			if (t.getAction(sysEnv).intValue() == SDMSTrigger.RERUN) {
				gotRerun = true;
			} else {
				if ((maxCount != 0) && (ctr >= maxCount))
					if (gotRerun)
						break;
					else
						continue;
			}
			ctr++;
			SDMSSchedulingEntity fire_se = null;
			SDMSNamedResource fire_nr = null;
			SDMSResource fire_r = null;
			SDMSResource r = null;
			SDMSNamedResource nr = null;

			Long fireId = t.getFireId(sysEnv);
			int objType = t.getObjectType(sysEnv).intValue();
			switch(objType) {
				case SDMSTrigger.JOB_DEFINITION:
					fire_se = SDMSSchedulingEntityTable.getObject(sysEnv, fireId);
					if(!fire_se.checkPrivileges(sysEnv, SDMSPrivilege.VIEW)) continue;
					break;
				case SDMSTrigger.NAMED_RESOURCE:
					fire_nr = SDMSNamedResourceTable.getObject(sysEnv, fireId);
					if(!fire_nr.checkPrivileges(sysEnv, SDMSPrivilege.VIEW)) continue;
					break;
				case SDMSTrigger.RESOURCE:
					fire_r = SDMSResourceTable.getObject(sysEnv, fireId);
					if(!fire_r.checkPrivileges(sysEnv, SDMSPrivilege.VIEW)) continue;
					break;
			}
			data = new Vector();
			data.add(t.getId(sysEnv));
			data.add(t.getName(sysEnv));
			data.add(t.getObjectTypeAsString(sysEnv));
			switch(objType) {
				case SDMSTrigger.JOB_DEFINITION:
					data.add(fire_se.getTypeAsString(sysEnv));
					break;
				case SDMSTrigger.NAMED_RESOURCE:
					data.add(fire_nr.getUsageAsString(sysEnv));
					break;
				case SDMSTrigger.RESOURCE:
					r = SDMSResourceTable.getObject(sysEnv, fireId);
					nr = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv));
					data.add(nr.getUsageAsString(sysEnv));
					break;
			}
			switch(objType) {
				case SDMSTrigger.JOB_DEFINITION:
					data.add(fire_se.pathVector(sysEnv));
					break;
				case SDMSTrigger.NAMED_RESOURCE:
					data.add(fire_nr.pathVector(sysEnv));
					break;
				case SDMSTrigger.RESOURCE:
					data.add(fire_r.getURLName(sysEnv));
					break;
			}

			data.add(t.getIsActive(sysEnv));
			data.add(t.getActionAsString(sysEnv));

			Vector v_ts = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, t.getId(sysEnv));
			Iterator i_ts = v_ts.iterator();
			String sep = "";
			StringBuffer states = new StringBuffer();
			while(i_ts.hasNext()) {
				SDMSTriggerState ts = (SDMSTriggerState) i_ts.next();
				String s_ts = "";
				if (objType == SDMSTrigger.RESOURCE || objType == SDMSTrigger.NAMED_RESOURCE) {
					Long fromStateId = ts.getFromStateId(sysEnv);
					if (fromStateId != null) {
						s_ts = s_ts + SDMSResourceStateDefinitionTable.getObject(sysEnv, fromStateId).getName(sysEnv);
					}
					s_ts = s_ts + ":";
					Long toStateId = ts.getToStateId(sysEnv);
					if (toStateId != null) {
						s_ts = s_ts + SDMSResourceStateDefinitionTable.getObject(sysEnv, toStateId).getName(sysEnv);
					}
				} else {
					Long toStateId = ts.getToStateId(sysEnv);
					if (toStateId != null) {
						s_ts = s_ts + SDMSExitStateDefinitionTable.getObject(sysEnv, toStateId).getName(sysEnv);
					}
				}
				states.append(sep);
				states.append(s_ts);
				sep = ",";
			}
			data.add(new String(states));

			SDMSSchedulingEntity subm_se = SDMSSchedulingEntityTable.getObject(sysEnv, t.getSeId(sysEnv));
			data.add(subm_se.getTypeAsString(sysEnv));
			data.add(subm_se.pathVector(sysEnv));
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
				data.add(null);
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
			Long limitState = t.getLimitState(sysEnv);
			if (limitState == null) {
				data.add(null);
			} else {
				SDMSExitStateDefinition lsEsd = SDMSExitStateDefinitionTable.getObject(sysEnv, limitState);
				data.add(lsEsd.getName(sysEnv));
			}
			data.add(t.getCondition(sysEnv));
			data.add(t.getCheckAmount(sysEnv));
			data.add(t.getCheckBaseAsString(sysEnv));
			data.add(renderParameters(sysEnv, t));
			data.add(t.getPrivileges(sysEnv).toString());
			try {
				SDMSObjectComment oc = SDMSObjectCommentTable.idx_objectId_getFirst(sysEnv, t.getId(sysEnv));
				data.add(oc.getTag(sysEnv));
				data.add(oc.getDescription(sysEnv));
				data.add(oc.getInfoTypeAsString(sysEnv));
			} catch (NotFoundException ne) {
				data.add(null);
				data.add(null);
				data.add(null);
			}
			d_container.addData(sysEnv, data);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 2, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03206191305", "$1 Trigger(s) found", new Integer(d_container.lines)));
	}
}

