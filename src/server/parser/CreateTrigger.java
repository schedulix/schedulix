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

public class CreateTrigger extends ManipTrigger
{

	public final static String __version = "@(#) $Id: CreateTrigger.java,v 2.30.2.2 2013/03/15 15:00:56 ronald Exp $";

	protected String name;
	protected WithItem objType;
	protected WithHash with;

	protected Integer objectType;
	protected Vector objpath;
	protected Vector resourcepath;

	protected boolean replace;

	protected Long fireId = null;

	protected int oType;

	public CreateTrigger(String n, WithItem o, WithHash w, Boolean r)
	{
		super();
		name = n;
		objType = o;
		with = w;
		replace = r.booleanValue();
	}

	private void checkJobWith(SystemEnvironment sysEnv)
		throws SDMSException
	{
		oType = SDMSTrigger.JOB_DEFINITION;
		objpath = (Vector) objType.value;
		objectType = new Integer(oType);
		SDMSSchedulingEntity fireJob = SDMSSchedulingEntityTable.get(sysEnv, objpath, null);
		fireId = fireJob.getId(sysEnv);
		SDMSSchedulingEntity triggerJob;
		SDMSSchedulingEntity mainJob = null;
		SDMSSchedulingEntity parentJob = null;

		if (with.containsKey(ParseStr.S_ACTIVE)) {
			active = (Boolean) with.get(ParseStr.S_ACTIVE);
		} else {
			active = Boolean.TRUE;
		}

		mainSeId = null;
		parentSeId = null;
		if (with.containsKey(ParseStr.S_MAIN) && with.get(ParseStr.S_MAIN) != null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03109081515", "Main Scheduling Entity option is only valid for Object Monitor Triggers"));
		if (with.containsKey(ParseStr.S_PARENT) && with.get(ParseStr.S_PARENT) != null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03109081536", "Parent Scheduling Entity option is only valid for Object Monitor Triggers"));

		if (with.containsKey(ParseStr.S_RERUN)) {
			if (with.containsKey(ParseStr.S_SUBMIT)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108190857", "Submit and Rerun cannot be specified both"));
			}
			action = new Integer(SDMSTrigger.RERUN);
			iaction = SDMSTrigger.RERUN;
		} else {
			action = new Integer(SDMSTrigger.SUBMIT);
			iaction = SDMSTrigger.SUBMIT;
		}

		folderpath = (Vector) with.get(ParseStr.S_SUBMIT);
		if((folderpath != null) && (iaction == SDMSTrigger.SUBMIT)) {
			triggerJob = SDMSSchedulingEntityTable.get(sysEnv, folderpath, null);
			if(!triggerJob.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
				throw new AccessViolationException(
					new SDMSMessage(sysEnv, "03402131550", "Submit privilege on $1 missing", triggerJob.pathString(sysEnv))
				);
			seId = triggerJob.getId(sysEnv);
			action = new Integer(SDMSTrigger.SUBMIT);
			iaction = SDMSTrigger.SUBMIT;
		} else	{
			if ((folderpath == null) && (iaction == SDMSTrigger.RERUN)) {
				seId = fireId;
				triggerJob = fireJob;
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03206210034", "Either Submit or Rerun is mandatory"));
			}
		}

		isInverse = (Boolean) with.get(ParseStr.S_INVERSE);
		if (isInverse == null) isInverse = Boolean.FALSE;
		if (isInverse.booleanValue()) {
			SDMSSchedulingEntity tmpSe = fireJob;
			fireJob = triggerJob;
			triggerJob = tmpSe;

			Long tmp = fireId;
			fireId = seId;
			seId = tmp;
		}

		if (with.containsKey(ParseStr.S_MAIN)) {
			Vector mainFolderPath = (Vector) with.get(ParseStr.S_MAIN);
			if (mainFolderPath != null)
				mainJob = SDMSSchedulingEntityTable.get(sysEnv, mainFolderPath, null);
		}

		if (with.containsKey(ParseStr.S_PARENT)) {
			Vector parentFolderPath = (Vector) with.get(ParseStr.S_PARENT);
			if (parentFolderPath != null)
				parentJob = SDMSSchedulingEntityTable.get(sysEnv, parentFolderPath, null);
		}

		if (iaction == SDMSTrigger.RERUN) {
			if (fireJob.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111239", "Rerun triggers are valid for jobs only"));
		}

		isMaster = (Boolean) with.get(ParseStr.S_MASTER);
		if(isMaster == null) {
			isMaster = Boolean.FALSE;
		}

		triggertype = (Integer) with.get(ParseStr.S_TRIGGERTYPE);
		if(triggertype != null) {
			int tt = triggertype.intValue();

			if(fireJob.getType(sysEnv).intValue() == SDMSSchedulingEntity.MILESTONE) {
				if(tt != SDMSTrigger.AFTER_FINAL) {
					if(!isMaster.booleanValue())
						throw new CommonErrorException(
							new SDMSMessage(sysEnv, "03209201514", "Triggertype must be after final for non-master triggers on milestones"));
				}
			}
			if(tt == SDMSTrigger.UNTIL_FINISHED || tt == SDMSTrigger.UNTIL_FINAL) {
				if(!with.containsKey(ParseStr.S_CHECK)) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03407281037", "Asynchroneous triggers must define a check interval"));
				}
			}
			if (action.intValue() == SDMSTrigger.RERUN) {
				if (tt != SDMSTrigger.IMMEDIATE_LOCAL) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03108091435", "Triggertype must be immediate local in case of rerun triggers"));
				}
			}
		} else {
			if (action.intValue() == SDMSTrigger.RERUN) {
				triggertype = new Integer(SDMSTrigger.IMMEDIATE_LOCAL);
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03206211424", "Triggertype must be specified"));
			}
		}

		if(isMaster.booleanValue()) {
			if (iaction == SDMSTrigger.RERUN) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108091437", "Master option not valid for rerun triggers"));
			}
			if((mainJob == null && !triggerJob.getMasterSubmittable(sysEnv).booleanValue()) ||
			   (mainJob != null && !mainJob.getMasterSubmittable(sysEnv).booleanValue()))
				throw new CommonErrorException(
						new SDMSMessage(sysEnv, "03301280208", "Master trigger defined for non master submittable job"));
			if(!with.containsKey(ParseStr.S_GROUP))
				throw new CommonErrorException(
						new SDMSMessage(sysEnv, "02402180658", "Group clause is mandatory for master triggers"));
			final String gName = (String) with.get(ParseStr.S_GROUP);
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			if (mainJob != null) mainJob.checkSubmitForGroup(sysEnv, gId);
			else triggerJob.checkSubmitForGroup(sysEnv, gId);
		} else {
			if(with.containsKey(ParseStr.S_GROUP))
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02402180929", "Group clause is not allowed for child or rerun triggers"));
		}

		isSuspend = (Boolean) with.get(ParseStr.S_SUSPEND);
		if(isSuspend == null) isSuspend = Boolean.FALSE;

		if (isInverse.booleanValue()) {
			if (!isMaster.booleanValue())
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03506231011", "Master option mandatory in case of inverse Triggers"));
			if (iaction == SDMSTrigger.RERUN)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03506231304", "Inverse option invalid in case of rerun Triggers"));
		}

		resumeObj = with.get(ParseStr.S_RESUME);
		if (!isSuspend.booleanValue() && (resumeObj != null))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03108091752", "Resume clause without suspend clause doesn't make sense"));
		analyzeResumeObj(sysEnv);

		isWarnOnLimit = (Boolean) with.get(ParseStr.S_WARN);
		if(isWarnOnLimit == null) isWarnOnLimit = Boolean.FALSE;

		limitState = null;
		String sLimitState = (String) with.get(ParseStr.S_LIMIT);
		if (sLimitState != null) {
			try {
				SDMSExitStateDefinition lsEsd = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, sLimitState);
				limitState = lsEsd.getId(sysEnv);
			} catch (NotFoundException nfe) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03509181230", "Specified exit state " + sLimitState + " not found"));
			}
			Long espId = fireJob.getEspId(sysEnv);
			try {
				SDMSExitState es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, limitState));
			} catch (NotFoundException nfe) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03310101437", "Specified exit state " + sLimitState + " not found in exit state profile"));
			}
		}

		maxRetry = (Integer) with.get(ParseStr.S_SUBMITCOUNT);
		if(maxRetry == null) {
			maxRetry = new Integer(1);
		}

		rscstate = null;
		state = (Vector) with.get(ParseStr.S_STATUS);
		if(with.containsKey(ParseStr.S_RSCSTATUS)) {
			throw new CommonErrorException( new SDMSMessage(sysEnv, "03206210046", "Only job states are allowed for job triggers"));
		}

		if(with.containsKey(ParseStr.S_CONDITION)) {
			condition = (String) with.get(ParseStr.S_CONDITION);
		} else	condition = null;

		if(with.containsKey(ParseStr.S_CHECK)) {
			check = (WithHash) with.get(ParseStr.S_CHECK);
			if(condition == null)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03407271556",
							"Asynchroneous triggers are only valid in combination with a condition"));
			if(triggertype.intValue() != SDMSTrigger.UNTIL_FINISHED && triggertype.intValue() != SDMSTrigger.UNTIL_FINAL)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03407271557",
							"Check periods are only valid for asynchroneous triggers"));
			checkAmount = (Integer) check.get(ParseStr.S_MULT);
			checkBase = (Integer) check.get(ParseStr.S_INTERVAL);
		} else	{
			checkAmount = null;
			checkBase = null;
		}
	}

	private void checkResourceCommonWith(SystemEnvironment sysEnv, SDMSNamedResource nr)
	throws SDMSException
	{
		SDMSSchedulingEntity triggerJob;
		if(nr.getUsage(sysEnv).intValue() != SDMSNamedResource.SYNCHRONIZING) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206250058", "Trigger can only be defined for synchronizing resources"));
		}

		objectType = new Integer(oType);

		if (with.containsKey(ParseStr.S_ACTIVE)) {
			active = (Boolean) with.get(ParseStr.S_ACTIVE);
		} else {
			active = Boolean.TRUE;
		}

		mainSeId = null;
		parentSeId = null;
		if (with.containsKey(ParseStr.S_MAIN) && with.get(ParseStr.S_MAIN) != null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03109081537", "Main Scheduling Entity option is only valid for Object Monitor Triggers"));
		if (with.containsKey(ParseStr.S_PARENT) && with.get(ParseStr.S_PARENT) != null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03109081538", "Parent Scheduling Entity option is only valid for Object Monitor Triggers"));

		action = new Integer(SDMSTrigger.SUBMIT);
		iaction = SDMSTrigger.SUBMIT;

		folderpath = (Vector) with.get(ParseStr.S_SUBMIT);
		if(folderpath != null) {
			triggerJob = SDMSSchedulingEntityTable.get(sysEnv, folderpath, null);
			if(!triggerJob.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
				throw new AccessViolationException(
				        new SDMSMessage(sysEnv, "03402131546", "Submit privilege on $1 missing", triggerJob.pathString(sysEnv))
				);
			seId = triggerJob.getId(sysEnv);
		} else	{
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206210035", "Submit is mandatory"));
		}

		triggertype = (Integer) with.get(ParseStr.S_TRIGGERTYPE);
		if(triggertype != null) {
			if(triggertype.intValue() != SDMSTrigger.IMMEDIATE_LOCAL) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03206210043", "Triggertype must be Immediate local for resource triggers"));
			}
		} else {
			triggertype = new Integer(SDMSTrigger.IMMEDIATE_LOCAL);
		}
		isMaster = (Boolean) with.get(ParseStr.S_MASTER);
		if(isMaster != null) {
			if(! isMaster.booleanValue()) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03206210044", "Only master submits allowed for resource triggers"));
			}
		} else {
			isMaster = Boolean.TRUE;
		}
		if(!triggerJob.getMasterSubmittable(sysEnv).booleanValue())
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03104261631", "Master trigger defined for non master submittable job"));
		if(!with.containsKey(ParseStr.S_GROUP))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03602082122", "Group clause is mandatory for resource triggers"));

		isSuspend = (Boolean) with.get(ParseStr.S_SUSPEND);
		if(isSuspend == null) isSuspend = Boolean.FALSE;

		resumeObj = with.get(ParseStr.S_RESUME);
		if (!isSuspend.booleanValue() && (resumeObj != null))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03108091752", "Resume clause without suspend clause doesn't make sense"));
		analyzeResumeObj(sysEnv);

		maxRetry = (Integer) with.get(ParseStr.S_SUBMITCOUNT);
		if(maxRetry != null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206210045", "Retry Count not allowed for resource triggers"));
		} else {
			maxRetry = new Integer(0);
			isWarnOnLimit = Boolean.FALSE;
		}

		state = null;
		rscstate = (Vector) with.get(ParseStr.S_RSCSTATUS);
		if(with.containsKey(ParseStr.S_STATUS) && with.get(ParseStr.S_STATUS) != null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206210047", "Only resource states are allowed for resource triggers"));
		}

		if(with.containsKey(ParseStr.S_CONDITION)) {
			condition = (String) with.get(ParseStr.S_CONDITION);
		} else condition = null;

		if(with.containsKey(ParseStr.S_CHECK)) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03407271555", "Asynchroneous triggers are only valid for jobs"));
		} else	check = null;

		isInverse = (Boolean) with.get(ParseStr.S_INVERSE);
		if (isInverse != null && isInverse.booleanValue())
			throw new CommonErrorException(new SDMSMessage(sysEnv, "036231306", "Inverse option is invalid for resource triggers"));
	}

	private void checkResourceWith(SystemEnvironment sysEnv)
	throws SDMSException
	{
		oType = SDMSTrigger.RESOURCE;
		objpath = (Vector) objType.value;
		resourcepath = (Vector) objpath.remove(objpath.size() - 1);
		Long scopeId;
		try {
			scopeId = SDMSScopeTable.pathToId(sysEnv, objpath);
		} catch (NotFoundException nfe) {
			scopeId = SDMSFolderTable.pathToId(sysEnv, objpath);
		}
		SDMSNamedResource nr = SDMSNamedResourceTable.getNamedResource(sysEnv, resourcepath);
		Long nrId = nr.getId(sysEnv);
		fireId = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, new SDMSKey(nrId, scopeId)).getId(sysEnv);

		checkResourceCommonWith(sysEnv, nr);
	}

	private void checkNamedResourceWith(SystemEnvironment sysEnv)
	throws SDMSException
	{
		oType = SDMSTrigger.NAMED_RESOURCE;
		objpath = (Vector) objType.value;
		resourcepath = objpath;
		SDMSNamedResource nr = SDMSNamedResourceTable.getNamedResource(sysEnv, resourcepath);
		Long nrId = nr.getId(sysEnv);
		fireId = nrId;

		checkResourceCommonWith(sysEnv, nr);
	}

	private void checkWith(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(objType.key.equals(ParseStr.S_JOB)) {
			checkJobWith(sysEnv);
		} else {
			if(objType.key.equals(ParseStr.S_OBJECT)) {
				sysEnv.checkFeatureAvailability(SystemEnvironment.S_OBJECTMONITOR_TRIGGER);
			} else {
				sysEnv.checkFeatureAvailability(SystemEnvironment.S_RESOURCE_TRIGGER);
				if (objType.key.equals(ParseStr.S_RESOURCE)) {
					checkResourceWith(sysEnv);
				} else {
					checkNamedResourceWith(sysEnv);
				}
			}
		}
		if (triggertype != null) {
			int tt = triggertype.intValue();
			if (tt == SDMSTrigger.UNTIL_FINISHED ||
			    tt == SDMSTrigger.UNTIL_FINAL
			   ) {
				sysEnv.checkFeatureAvailability(SystemEnvironment.S_ASYNC_TRIGGERS);
			}
			if (!(tt == SDMSTrigger.BEFORE_FINAL ||
			      tt == SDMSTrigger.IMMEDIATE_LOCAL ||
			      tt == SDMSTrigger.IMMEDIATE_MERGE
			   )) {
				sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXTENDED_TRIGGERS);
			}
		}
		if (condition != null && !(condition.equals(""))) sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXTENDED_TRIGGERS);

	}

	private void checkAndCreateParameters(SystemEnvironment sysEnv, SDMSTrigger t)
	throws SDMSException
	{
		WithHash parmHash = (WithHash) with.get(ParseStr.S_PARAMETERS);
		if (parmHash == null) return;

		Iterator i = parmHash.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			String value = (String) parmHash.get(key);
			t.checkParameterExpressionSyntax(sysEnv, value);
			try {
				SDMSTriggerParameterTable.table.create(sysEnv, key, value, t.getId(sysEnv));
			} catch (DuplicateKeyException dke) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03801311320", "Ambiguous parameter definition for parameter $1", key));
			}
		}
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSTrigger t;
		Long tId;

		checkWith(sysEnv);

		Long gId = null;

		if(isMaster.booleanValue()) {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			if (gName == null) {
			} else {
				gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			}
		}

		try {
			t = SDMSTriggerTable.table.create(sysEnv, name, fireId, objectType, seId, mainSeId, parentSeId, active, isInverse, action,
							triggertype, isMaster, isSuspend, isCreate, isChange, isDelete, isGroup,
							resumeAt, resumeIn, resumeBase, isWarnOnLimit, limitState, maxRetry, gId, condition,
							checkAmount, checkBase);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterTrigger at = new AlterTrigger(name, fireId, objectType.intValue(), with, Boolean.FALSE);
				at.setEnv(env);
				at.go(sysEnv);
				result = at.result;
				return;
			} else {
				throw dke;
			}
		}
		t.checkConditionSyntax(sysEnv);

		if (with.containsKey(ParseStr.S_PARAMETERS)) {
			checkAndCreateParameters(sysEnv, t);
		}

		if (isInverse.booleanValue())
			checkUniqueness(sysEnv, name, fireId, seId, isInverse);
		else
			checkUniqueness(sysEnv, name, fireId, seId, isInverse);

		tId = t.getId(sysEnv);

		if(t.getObjectType(sysEnv).intValue() == SDMSTrigger.JOB_DEFINITION) {
			if(state != null) {
				for(int i = 0; i < state.size(); i++) {
					String s = (String) state.get(i);
					Long esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, s).getId(sysEnv);

					SDMSTriggerStateTable.table.create(sysEnv, tId, null, esdId);
				}
			}

			if ((triggertype.intValue() != SDMSTrigger.AFTER_FINAL) && (iaction != SDMSTrigger.RERUN))
				SDMSSchedulingHierarchyTable.checkHierarchyCycles(sysEnv, fireId);
		} else {
			if (t.getObjectType(sysEnv).intValue() != SDMSTrigger.OBJECT_MONITOR) {
				if(rscstate != null) {
					for(int i = 0; i < rscstate.size(); i++) {
						WithItem w = (WithItem) rscstate.get(i);
						Long frsdId;
						if(w.key != null) {
							frsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, w.key).getId(sysEnv);
						} else	frsdId = null;
						Long trsdId;
						if(w.value != null) {
							trsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, w.value).getId(sysEnv);
						} else  trsdId = null;

						SDMSTriggerStateTable.table.create(sysEnv, tId, frsdId, trsdId);
					}
				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03206191306", "Trigger created"));
	}
}

