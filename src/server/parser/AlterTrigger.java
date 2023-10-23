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
	protected String name;
	protected WithHash with;
	protected WithItem fireObj;
	protected boolean noerr;

	protected Long submitOwnerId;
	protected Long fireId;
	protected Long checkFireId;
	protected int fireType;

	protected SDMSSchedulingEntity fireSe = null;
	protected SDMSNamedResource fireNr = null;
	protected SDMSResource fireR = null;
	protected SDMSNamedResource fireNR = null;

	private ObjectURL url;

	public AlterTrigger(ObjectURL u, WithHash w, Boolean ne)
	{
		super();
		name = null;
		with = w;
		fireObj = null;
		url = u;
		isInverse = (Boolean) u.triggerInverse.value;
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
		isInverse = (Boolean) with.get(ParseStr.S_INVERSE);
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

		if (isInverse.booleanValue()) {
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

		Long checkSeId = null;
		folderpath = (Vector) with.get(ParseStr.S_SUBMIT);
		if((folderpath != null) && (iaction == SDMSTrigger.SUBMIT)) {
			String n = (String) folderpath.remove(folderpath.size() -1);
			se = SDMSSchedulingEntityTable.get(sysEnv, folderpath, n);
			if (isInverse.booleanValue()) {
				fireId = se.getId(sysEnv);
				checkFireId = fireId;
				seId = t.getSeId(sysEnv);
			} else {
				if(!se.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
					throw new AccessViolationException(
						new SDMSMessage(sysEnv, "03402131605", "Submit privilege on $1 missing", se.pathString(sysEnv))
					);

				seId = se.getId(sysEnv);
				fireId = t.getFireId(sysEnv);
				checkSeId = seId;
			}
		} else {
			if ((folderpath == null) && with.containsKey(ParseStr.S_RERUN)) {
				seId = t.getFireId(sysEnv);
				fireId = seId;
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

				if(fireSe.getType(sysEnv).intValue() == SDMSSchedulingEntity.MILESTONE)
					if(itt != SDMSTrigger.AFTER_FINAL) {
						if(!checkIsMaster.booleanValue())
							throw new CommonErrorException(
							        new SDMSMessage(sysEnv, "03209201516",
							                        "Triggertype must be AFTER FINAL for non-master triggers on milestones"));
					}

				if(itt == SDMSTrigger.UNTIL_FINISHED || itt == SDMSTrigger.UNTIL_FINAL) {
					if(!with.containsKey(ParseStr.S_CHECK)) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03508030807", "Asynchroneous triggers must define a check interval"));
					}
				}

				if (itt != SDMSTrigger.IMMEDIATE_LOCAL && iaction == SDMSTrigger.RERUN)
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111248", "Only immediate local triggers can be used for automatic restarts"));
			}
		} else {
			itt = t.getType(sysEnv).intValue();
		}

		if (objType == SDMSTrigger.JOB_DEFINITION) {
			isWarnOnLimit = (Boolean) with.get(ParseStr.S_WARN);
			if (with.containsKey(ParseStr.S_LIMIT)) {
				String sLimitState = (String) with.get(ParseStr.S_LIMIT);
				if (sLimitState == null)
					limitState = null;
				else {
					try {
						SDMSExitStateDefinition lsEsd = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, sLimitState);
						limitState = lsEsd.getId(sysEnv);
					} catch (NotFoundException nfe) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03509181338", "Specified exit state " + sLimitState + " not found"));
					}
					Long espId = fireSe.getEspId(sysEnv);
					try {
						SDMSExitState es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, limitState));
					} catch (NotFoundException nfe) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03310101439", "Specified exit state " + sLimitState + " not found in exit state profile"));
					}
				}
			}
		} else
			isWarnOnLimit = null;

		condition = (String) with.get(ParseStr.S_CONDITION);
		if (condition == null)
			condition = t.getCondition(sysEnv);

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
		} else if(objType != SDMSTrigger.OBJECT_MONITOR) {
			state = null;
			rscstate = (Vector) with.get(ParseStr.S_RSCSTATUS);
			if(with.containsKey(ParseStr.S_STATUS) && with.get(ParseStr.S_STATUS) != null) {
				throw new CommonErrorException(
				        new SDMSMessage(sysEnv, "03206202332", "Only resource states are allowed for resource triggers"));
			}
		}

		if(with.containsKey(ParseStr.S_CHECK)) {
			check = (WithHash) with.get(ParseStr.S_CHECK);
			if(condition == null)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03508030809",
				                               "Asynchroneous triggers are only valid in combination with a condition"));
			if(itt != SDMSTrigger.UNTIL_FINISHED && itt != SDMSTrigger.UNTIL_FINAL)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03508030810",
				                               "Check periods are only valid for asynchroneous triggers"));
			checkAmount = (Integer) check.get(ParseStr.S_MULT);
			checkBase = (Integer) check.get(ParseStr.S_INTERVAL);
		} else	{
			checkAmount = null;
			checkBase = null;
		}

		SDMSSchedulingEntity mainSe = null;

		if (checkIsMaster.booleanValue()) {
			if(!with.containsKey(ParseStr.S_GROUP) && checkSubmitOwnerId == null)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02402180659", "Group clause is mandatory for master triggers"));
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
				case SDMSTrigger.NAMED_RESOURCE:
					fireNR = SDMSNamedResourceTable.getObject(sysEnv, fireId);
					break;
			}
			return;
		}

		if(fireObj.key.equals(ParseStr.S_JOB)) {
			objpath = (Vector) fireObj.value;
			objname = (String) objpath.remove(objpath.size() - 1);
			fireSe = SDMSSchedulingEntityTable.get(sysEnv, objpath, objname);
			fireId = fireSe.getId(sysEnv);
		} else if(fireObj.key.equals(ParseStr.S_RESOURCE)) {
			objpath = (Vector) fireObj.value;
			SDMSThread.doTrace(sysEnv.cEnv, "Objectpath : " + objpath.toString(), SDMSThread.SEVERITY_DEBUG);
			final Object tmp = objpath.remove(objpath.size() - 1 );
			SDMSThread.doTrace(sysEnv.cEnv, "Object in doubt : " + tmp.toString(), SDMSThread.SEVERITY_DEBUG);
			resourcepath = (Vector) tmp;
			Long scopeId;
			try {
				scopeId = SDMSScopeTable.pathToId(sysEnv, objpath);
			} catch (NotFoundException nfe) {
				scopeId = SDMSFolderTable.pathToId(sysEnv, objpath);
			}
			Long nrId = SDMSNamedResourceTable.getNamedResource(sysEnv, resourcepath).getId(sysEnv);
			fireR = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, new SDMSKey(nrId, scopeId));
			fireId = fireR.getId(sysEnv);
		} else if(fireObj.key.equals(ParseStr.S_NAMED_RESOURCE)) {
			objpath = (Vector) fireObj.value;
			fireNR = SDMSNamedResourceTable.getNamedResource(sysEnv, objpath);
			fireId = fireNr.getId(sysEnv);
		}
	}

	private void diffParameters(SystemEnvironment sysEnv, SDMSTrigger t)
	throws SDMSException
	{
		Long tId = t.getId(sysEnv);
		Vector pv = SDMSTriggerParameterTable.idx_triggerId.getVector(sysEnv, tId);
		WithHash parmHash = (WithHash) with.get(ParseStr.S_PARAMETERS);
		if (parmHash == null) parmHash = new WithHash();
		for (int i = 0; i < pv.size(); ++i) {
			SDMSTriggerParameter tp = (SDMSTriggerParameter) pv.get(i);
			String name = tp.getName(sysEnv);
			if (parmHash.containsKey(name)) {
				String expression = tp.getExpression(sysEnv);
				String phExpression = (String) parmHash.get(name);
				if (!expression.equals(phExpression)) {
					t.checkParameterExpressionSyntax(sysEnv, phExpression);
					tp.setExpression(sysEnv, phExpression);
				}
				parmHash.remove(name);
			} else {
				tp.delete(sysEnv);
			}
		}

		Iterator phi = parmHash.keySet().iterator();
		while (phi.hasNext()) {
			String name = (String) phi.next();
			String expression = (String) parmHash.get(name);
			t.checkParameterExpressionSyntax(sysEnv, expression);
			SDMSTriggerParameterTable.table.create(sysEnv, name, expression, tId);
		}
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSTrigger t = null;
		Long tId;

		try {
			if (url == null) {
				getFireId(sysEnv);
				Vector v;
				if (isInverse.booleanValue())
					v = SDMSTriggerTable.idx_seId_name.getVector(sysEnv, new SDMSKey(fireId, name));
				else
					v = SDMSTriggerTable.idx_fireId_name.getVector(sysEnv, new SDMSKey(fireId, name));
				if (v.size() == 0) throw new NotFoundException();
				int i;
				for (i = 0; i < v.size(); ++i) {
					t = (SDMSTrigger) v.get(i);
					if (t.getIsInverse(sysEnv).equals(isInverse)) break;
				}
				if (i == v.size()) throw new NotFoundException();
			} else {
				t = (SDMSTrigger) url.resolve(sysEnv);
				if (t == null)
					throw new NotFoundException(new SDMSMessage(sysEnv, "03906060937", "Couldn't resolve the trigger"));
				fireId = t.getFireId(sysEnv);
				fireType = t.getObjectType(sysEnv).intValue();

				switch(fireType) {
					case SDMSTrigger.JOB_DEFINITION:
						fireSe = SDMSSchedulingEntityTable.getObject(sysEnv, fireId);
						break;
					case SDMSTrigger.RESOURCE:
						fireR = SDMSResourceTable.getObject(sysEnv, fireId);
						break;
					case SDMSTrigger.NAMED_RESOURCE:
						fireNR = SDMSNamedResourceTable.getObject(sysEnv, fireId);
						break;
				}

			}
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130107", "No Trigger altered"));
				return;
			}
			throw nfe;
		}
		tId = t.getId(sysEnv);
		fireId = t.getFireId(sysEnv);
		seId = t.getSeId(sysEnv);

		checkWith(sysEnv, t);
		if (isInverse.booleanValue()) {
			if(fireId != null)
				t.setFireId(sysEnv, fireId);
		} else {
			if(seId != null)
				t.setSeId(sysEnv, seId);
		}
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
		if (with.containsKey(ParseStr.S_LIMIT)) t.setLimitState(sysEnv, limitState);
		if(maxRetry != null)		t.setMaxRetry(sysEnv, maxRetry);
		if(with.containsKey(ParseStr.S_CONDITION))		t.setCondition(sysEnv, condition);
		t.checkConditionSyntax(sysEnv);
		if(with.containsKey(ParseStr.S_CHECK)) {
			t.setCheckAmount(sysEnv, checkAmount);
			t.setCheckBase(sysEnv, checkBase);
		}

		checkUniqueness(sysEnv, name, fireId, seId, (isInverse == null ? t.getIsInverse(sysEnv) : isInverse));
		if(isInverse != null) t.setIsInverse(sysEnv, isInverse);

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
		} else if (t.getObjectType(sysEnv).intValue() == SDMSTrigger.RESOURCE || t.getObjectType(sysEnv).intValue() == SDMSTrigger.NAMED_RESOURCE) {
			if(rscstate != null || (rscstate == null && with.containsKey(ParseStr.S_STATUS))) {
				Vector v = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, tId);
				for(int i = 0; i < v.size(); i++) {
					SDMSTriggerState ts = (SDMSTriggerState) v.get(i);
					ts.delete(sysEnv);
				}
			}
			if(rscstate != null) {
				for(int i = 0; i < rscstate.size(); i++) {
					WithItem w = (WithItem) rscstate.get(i);
					Long frsdId;
					Long trsdId;
					if(w.key != null) {
						frsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, w.key).getId(sysEnv);
					} else	frsdId = null;
					if(w.value != null) {
						trsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, w.value).getId(sysEnv);
					} else  trsdId = null;

					SDMSTriggerStateTable.table.create(sysEnv, tId, frsdId, trsdId);
				}
			}
		}

		if (with.containsKey(ParseStr.S_PARAMETERS)) diffParameters(sysEnv, t);

		result.setFeedback(new SDMSMessage(sysEnv, "03206191441", "Trigger altered"));
	}
}

