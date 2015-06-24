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

public class AlterTrigger extends ManipTrigger
{

	public final static String __version = "@(#) $Id: AlterTrigger.java,v 2.29.2.4 2013/03/22 14:32:15 dieter Exp $";

	protected String name;
	protected WithHash with;
	protected WithItem fireObj;
	protected boolean noerr;

	protected Long submitOwnerId;
	protected Long fireId;
	protected int fireType;

	protected SDMSSchedulingEntity fireSe = null;
	protected SDMSNamedResource fireNr = null;
	protected SDMSResource fireR = null;

	private ObjectURL url;

	public AlterTrigger(ObjectURL u, WithHash w, Boolean ne)
	{
		super();
		name = null;
		with = w;
		fireObj = null;
		url = u;
		noerr = ne.booleanValue();
	}

	public AlterTrigger(String n, Long fId, int ot, WithHash w, Boolean ne)
	{
		super();
		name = n;
		with = w;
		fireObj = null;
		fireId = fId;
		fireType = ot;
		url = null;
		noerr = ne.booleanValue();
	}

	private void checkWith(SystemEnvironment sysEnv, SDMSTrigger t)
		throws SDMSException
	{
		int objType = t.getObjectType(sysEnv).intValue();
		SDMSSchedulingEntity se = null;

		if (with.containsKey(ParseStr.S_RERUN)) {
			if (with.containsKey(ParseStr.S_SUBMIT)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108190858", "Submit and Rerun cannot be specified both"));
			}
			action = new Integer(SDMSTrigger.RERUN);
			iaction = SDMSTrigger.RERUN;
			if (objType != SDMSTrigger.JOB_DEFINITION)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111304", "Rerun triggers are valid for jobs only"));
		} else {
			action = t.getAction(sysEnv);
			iaction = action.intValue();
		}

		Long checkSeId;
		folderpath = (Vector) with.get(ParseStr.S_SUBMIT);
		if((folderpath != null) && (iaction == SDMSTrigger.SUBMIT)) {
			String n = (String) folderpath.remove(folderpath.size() -1);
			se = SDMSSchedulingEntityTable.get(sysEnv, folderpath, n);
			if(!se.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
				throw new AccessViolationException(
					new SDMSMessage(sysEnv, "03402131605", "Submit privilege on $1 missing", se.pathString(sysEnv))
				);
			seId = se.getId(sysEnv);
			checkSeId = seId;
		} else {
			if ((folderpath == null) && with.containsKey(ParseStr.S_RERUN)) {
				seId = t.getFireId(sysEnv);
				checkSeId = seId;
			} else {
				seId = null;
				checkSeId = t.getSeId(sysEnv);
			}
		}

		if (with.containsKey(ParseStr.S_ACTIVE)) {
			active = (Boolean) with.get(ParseStr.S_ACTIVE);
		} else {
			active = t.getIsActive(sysEnv);
		}

		if (iaction == SDMSTrigger.RERUN) {
			if (SDMSSchedulingEntityTable.getObject(sysEnv, checkSeId).getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111251", "Rerun triggers are valid for jobs only"));
		}

		isSuspend = (Boolean) with.get(ParseStr.S_SUSPEND);
		if (isSuspend == null) isSuspend = t.getIsSuspend(sysEnv);
		resumeObj = with.get(ParseStr.S_RESUME);
		if (!isSuspend.booleanValue() && (resumeObj != null))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111218", "Resume without suspend clause doesn't make sense"));
		if (!isSuspend.booleanValue()) {
			resumeAt = null;
			resumeIn = null;
			resumeBase = null;
		} else
			analyzeResumeObj(sysEnv);

		isMaster = (Boolean) with.get(ParseStr.S_MASTER);
		Boolean checkIsMaster = isMaster;
		if(isMaster != null) {
			if(objType != SDMSTrigger.JOB_DEFINITION && objType != SDMSTrigger.OBJECT_MONITOR) {
				if(!isMaster.booleanValue()) {
					throw new CommonErrorException(
						new SDMSMessage(sysEnv, "03206202334", "Only master submits allowed for resource triggers"));
				}
			}
			if (iaction == SDMSTrigger.RERUN && isMaster.booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108091455", "Master option not valid for rerun triggers"));
			}
		} else
			checkIsMaster = t.getIsMaster(sysEnv);

		isInverse = (Boolean) with.get(ParseStr.S_INVERSE);
		if (isInverse != null && isInverse.booleanValue()) {
			if (!checkIsMaster.booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "036231301", "Master option mandatory for inverse triggers"));
			}
			if (iaction == SDMSTrigger.RERUN) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "036231302", "Inverse option not valid for rerun triggers"));
			}
			if (objType != SDMSTrigger.JOB_DEFINITION) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "036231303", "Inverse option only valid for job triggers"));
			}
		}

		final String submitOwnerName = (String)with.get(ParseStr.S_GROUP);
		Long checkSubmitOwnerId;
		if (submitOwnerName != null) {
			if (iaction == SDMSTrigger.RERUN) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111234", "Group clause is not allowed for rerun triggers"));
			}
			if (!checkIsMaster.booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02402180855", "Group clause is not allowed for child triggers"));
			}
			submitOwnerId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey(submitOwnerName, new Long(0))).getId(sysEnv);
			checkSubmitOwnerId = submitOwnerId;
		} else {
			submitOwnerId = null;
			checkSubmitOwnerId = t.getSubmitOwnerId(sysEnv);
		}

		triggertype = (Integer) with.get(ParseStr.S_TRIGGERTYPE);
		int itt = 0;
		if(triggertype != null) {
			itt = triggertype.intValue();
			if(objType != SDMSTrigger.JOB_DEFINITION) {

				if (objType == SDMSTrigger.OBJECT_MONITOR) {
					sysEnv.checkFeatureAvailability(SystemEnvironment.S_OBJECTMONITOR_TRIGGER);
				} else {
					sysEnv.checkFeatureAvailability(SystemEnvironment.S_RESOURCE_TRIGGER);
				}
				if(itt != SDMSTrigger.IMMEDIATE_LOCAL) {
					throw new CommonErrorException(
						new SDMSMessage(sysEnv, "03206200913", "Triggertype must be Immediate for resource and object monitor triggers"));
				}

			} else {

				if (itt == SDMSTrigger.UNTIL_FINISHED ||
				    itt == SDMSTrigger.UNTIL_FINAL
				   ) {
					sysEnv.checkFeatureAvailability(SystemEnvironment.S_ASYNC_TRIGGERS);
				}
				if (!(itt == SDMSTrigger.BEFORE_FINAL ||
				      itt == SDMSTrigger.IMMEDIATE_LOCAL ||
				      itt == SDMSTrigger.IMMEDIATE_MERGE
				   )) {
					sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXTENDED_TRIGGERS);
				}

				if(itt == SDMSTrigger.UNTIL_FINISHED || itt == SDMSTrigger.UNTIL_FINAL) {
					if(!with.containsKey(ParseStr.S_CHECK)) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03508030807", "Asynchroneous triggers must define a check interval"));
					}
				}

				if (itt != SDMSTrigger.IMMEDIATE_LOCAL && iaction == SDMSTrigger.RERUN)
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111248", "Only immediate local triggers can be used for automatic restarts"));
			}
		}

		if (condition != null && !(condition.equals("")))
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXTENDED_TRIGGERS);

		if (objType == SDMSTrigger.JOB_DEFINITION)
			isWarnOnLimit = (Boolean) with.get(ParseStr.S_WARN);
		else
			isWarnOnLimit = null;

		condition = (String) with.get(ParseStr.S_CONDITION);

		maxRetry = (Integer) with.get(ParseStr.S_SUBMITCOUNT);
		if(maxRetry != null) {
			if(objType != SDMSTrigger.JOB_DEFINITION) {
				throw new CommonErrorException(
					new SDMSMessage(sysEnv, "03206202335", "Retry Count doesn't make sense for resource triggers"));
			}
		}

		if(objType == SDMSTrigger.JOB_DEFINITION) {
			rscstate = null;
			state = (Vector) with.get(ParseStr.S_STATUS);
			if(with.containsKey(ParseStr.S_RSCSTATUS)) {
				throw new CommonErrorException(
					new SDMSMessage(sysEnv, "03206200920", "Only job states are allowed for job triggers"));
			}
		}

		SDMSSchedulingEntity mainSe = null;

		if (checkIsMaster.booleanValue()) {

			if (isMaster != null || seId != null) {
				if (se == null)
					se = SDMSSchedulingEntityTable.getObject(sysEnv, checkSeId);
				if((mainSe == null && !se.getMasterSubmittable(sysEnv).booleanValue()) ||
				   (mainSe != null && !mainSe.getMasterSubmittable(sysEnv).booleanValue()))
					throw new CommonErrorException(new SDMSMessage(sysEnv, "02402180838",
							"Master trigger defined for non master submittable job"));
			}

			if (isMaster != null || submitOwnerId != null || seId != null) {

				if (se == null)
					se = SDMSSchedulingEntityTable.getObject(sysEnv, checkSeId);
				se.checkSubmitForGroup(sysEnv, checkSubmitOwnerId);
			}
		}

	}

	private void getFireId(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector objpath;
		String objname;
		Vector resourcepath;

		if(fireObj == null) {
			switch(fireType) {
				case SDMSTrigger.JOB_DEFINITION:
					fireSe = SDMSSchedulingEntityTable.getObject(sysEnv, fireId);
					break;
				case SDMSTrigger.RESOURCE:
					fireR = SDMSResourceTable.getObject(sysEnv, fireId);
					break;
			}
			return;
		}

		if(fireObj.key.equals(ParseStr.S_JOB)) {
			objpath = (Vector) fireObj.value;
			objname = (String) objpath.remove(objpath.size() - 1);
			fireSe = SDMSSchedulingEntityTable.get(sysEnv, objpath, objname);
			fireId = fireSe.getId(sysEnv);
		}
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSTrigger t;
		Long tId;

		try {
			if (url == null) {
				getFireId(sysEnv);
				t = SDMSTriggerTable.idx_fireId_name_getUnique(sysEnv, new SDMSKey(fireId, name));
			} else {
				t = (SDMSTrigger) url.resolve(sysEnv);
				fireId = t.getFireId(sysEnv);
				fireType = t.getObjectType(sysEnv).intValue();
				getFireId(sysEnv);
			}
		} catch ( NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130107", "No Trigger altered"));
				return;
			}
			throw nfe;
		}
		tId = t.getId(sysEnv);

		checkWith(sysEnv, t);

		if(seId != null)		t.setSeId(sysEnv, seId);
		if(triggertype != null)	{
			t.setType(sysEnv, triggertype);
		}
		t.setAction(sysEnv, action);
		t.setIsActive(sysEnv, active);
		if(isMaster != null)		t.setIsMaster(sysEnv, isMaster);
		if(isSuspend != null)		t.setIsSuspend(sysEnv, isSuspend);
		t.setResumeAt(sysEnv, resumeAt);
		t.setResumeIn(sysEnv, resumeIn);
		t.setResumeBase(sysEnv, resumeBase);
		if(isWarnOnLimit != null)	t.setIsWarnOnLimit(sysEnv, isWarnOnLimit);
		if(maxRetry != null)		t.setMaxRetry(sysEnv, maxRetry);
		if(with.containsKey(ParseStr.S_CONDITION))		t.setCondition(sysEnv, condition);

		checkUniqueness(sysEnv, fireId);

		if(isMaster == null) isMaster = t.getIsMaster(sysEnv);
		if(isMaster.booleanValue())
			if(submitOwnerId != null)
				t.setSubmitOwnerId(sysEnv, submitOwnerId);
			else
				t.setSubmitOwnerId(sysEnv, null);

		if(t.getObjectType(sysEnv).intValue() == SDMSTrigger.JOB_DEFINITION) {
			if(state != null || (state == null && with.containsKey(ParseStr.S_STATUS))) {

				Vector v = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, tId);
				for(int i = 0; i < v.size(); i++) {
					SDMSTriggerState ts = (SDMSTriggerState) v.get(i);
					ts.delete(sysEnv);
				}
			}
			if(state != null) {

				for(int i = 0; i < state.size(); i++) {
					String s = (String) state.get(i);
					Long esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, s).getId(sysEnv);

					SDMSTriggerStateTable.table.create(sysEnv, tId, null, esdId);
				}
			}

			if (t.getType(sysEnv).intValue() != SDMSTrigger.AFTER_FINAL)
				SDMSSchedulingHierarchyTable.checkHierarchyCycles(sysEnv, t.getFireId(sysEnv));
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03206191441", "Trigger altered"));
	}
}

