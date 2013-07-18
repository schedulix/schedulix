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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class AlterJobDefinition extends ManipJobDefinition
{

	public final static String __version = "@(#) $Id: AlterJobDefinition.java,v 2.30.2.3 2013/03/22 14:32:15 dieter Exp $";

	public AlterJobDefinition(Vector p, String n, WithHash w, Boolean ne)
	{
		super(p, n, w, ne);
	}

	protected void checkDependents(SystemEnvironment sysEnv, Long seId)
	throws SDMSException
	{
		Vector ddv = SDMSDependencyDefinitionTable.idx_seRequiredId.getVector(sysEnv, seId);
		for(int i = 0; i < ddv.size(); i++) {
			SDMSDependencyDefinition dd = (SDMSDependencyDefinition) ddv.get(i);
			if(dd.getMode(sysEnv).intValue() == SDMSDependencyDefinition.JOB_FINAL) {
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeDependentId(sysEnv));
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03210141809",
				                               "JOB_FINAL Dependent $1 prohibits conversion from Job to Batch or Milestone",
				                               se.pathVector(sysEnv)));
			}
		}
	}

	private void diffDependencies(SystemEnvironment sysEnv, Long seId, Vector dependencydeflist)
	throws SDMSException
	{
		int idx;
		SDMSDependencyDefinition dd;

		WithHash rwh;
		String rName;
		Vector rPath;
		Vector rStateNames;
		String rdName;
		Integer unresolved;
		Integer mode;
		Integer stateSelection;
		String condition;

		Vector act_deps = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, seId);

		if(dependencydeflist != null) {
			Iterator i = dependencydeflist.iterator();
			while(i.hasNext()) {
				WithHash wh = (WithHash) i.next();

				rwh = (WithHash) wh.get(ParseStr.S_FULLNAME);
				if(rwh == null) {
					throw new CommonErrorException( new SDMSMessage(sysEnv, "03204302254", "Missing Required Name"));
				}
				rName = (String) rwh.get(ParseStr.S_NAME);
				rPath = (Vector) rwh.get(ParseStr.S_PATH);
				rdName = (String) wh.get(ParseStr.S_DEPENDENCY);
				unresolved = (Integer) wh.get(ParseStr.S_UNRESOLVED);
				Object o = wh.get(ParseStr.S_STATUS);
				stateSelection = null;
				if (o == null || o instanceof Vector) {
					rStateNames = (Vector) o;
					stateSelection = new Integer(SDMSDependencyDefinition.FINAL);
				} else {
					rStateNames = null;
					String stateMacro = (String) o;
					if (stateMacro.equals(ParseStr.S_DEFAULT)) {
						stateSelection = new Integer(SDMSDependencyDefinition.DEFAULT);
					} else if (stateMacro.equals(ParseStr.S_UNREACHABLE)) {
						stateSelection = new Integer(SDMSDependencyDefinition.UNREACHABLE);
					} else if (stateMacro.equals(ParseStr.S_REACHABLE)) {
						stateSelection = new Integer(SDMSDependencyDefinition.ALL_REACHABLE);
					}
				}
				mode = (Integer) wh.get(ParseStr.S_MODE);
				condition = canonizeCondition(sysEnv, (String) wh.get(ParseStr.S_CONDITION));
				if (condition != null) sysEnv.checkFeatureAvailability(SystemEnvironment.S_CONDITIONAL_DEPENDENCIES);

				SDMSSchedulingEntity rSe = SDMSSchedulingEntityTable.get(sysEnv, rPath, rName);
				Long rId = rSe.getId(sysEnv);

				dd = null;
				for(idx = 0; idx < act_deps.size(); idx++) {
					dd = (SDMSDependencyDefinition) act_deps.get(idx);
					Long ddReqId = dd.getSeRequiredId(sysEnv);
					if(ddReqId.equals(rId)) {
						act_deps.removeElementAt(idx);
						idx = -1;

						dd.setName(sysEnv, rdName);
						if(wh.containsKey(ParseStr.S_UNRESOLVED))	dd.setUnresolvedHandling(sysEnv, unresolved);
						if(wh.containsKey(ParseStr.S_MODE))		dd.setMode(sysEnv, mode);
						if(wh.containsKey(ParseStr.S_STATUS))		dd.setStateSelection(sysEnv, stateSelection);
						if(wh.containsKey(ParseStr.S_CONDITION))	dd.setCondition(sysEnv, condition);
						break;
					}
				}
				if(idx >= act_deps.size()) {
					if(unresolved == null) unresolved = new Integer(SDMSDependencyDefinition.ERROR);
					dd = SDMSDependencyDefinitionTable.table.create(sysEnv, seId, rId, rdName, unresolved, mode, stateSelection, condition);
				}

				if(wh.containsKey(ParseStr.S_STATUS)) {
					diffDependencyState(sysEnv, dd, rStateNames, rSe);
				} else {

					Vector v_ds = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv,dd.getId(sysEnv));
					Iterator i_ds = v_ds.iterator();
					while (i_ds.hasNext()) {
						SDMSDependencyState ds = (SDMSDependencyState)i_ds.next();
						ds.delete(sysEnv);
					}
				}
			}
		}
		for(idx = 0; idx < act_deps.size(); idx++) {
			dd = (SDMSDependencyDefinition) act_deps.get(idx);
			dd.delete(sysEnv);
		}
	}

	private void diffDependencyState(SystemEnvironment sysEnv, SDMSDependencyDefinition dd, Vector rStateNames, SDMSSchedulingEntity se)
	throws SDMSException
	{
		int idx;
		Long ddId = dd.getId(sysEnv);
		SDMSDependencyState ds;
		SDMSExitStateDefinition esd;
		Long rEspId = se.getEspId(sysEnv);

		Vector act_ds = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, ddId);

		if(rStateNames != null) {
			for(int i = 0; i < rStateNames.size(); i++) {
				WithItem w = (WithItem) rStateNames.get(i);
				String dn = (String) w.key;
				String condition = canonizeCondition(sysEnv, (String) w.value);
				if (condition != null) sysEnv.checkFeatureAvailability(SystemEnvironment.S_CONDITIONAL_DEPENDENCIES);

				for(idx = 0; idx < act_ds.size(); idx++) {
					ds = (SDMSDependencyState) act_ds.get(idx);
					esd = SDMSExitStateDefinitionTable.getObject(sysEnv, ds.getEsdId(sysEnv));
					if(dn.equals(esd.getName(sysEnv))) {
						act_ds.removeElementAt(idx);
						ds.setCondition(sysEnv, condition);
						idx = -1;
						break;
					}
				}
				if(idx >= act_ds.size()) {
					esd = null;
					try {
						esd = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, dn);
					} catch (NotFoundException nfe) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03205010115",
						                               "Exit State $1 does not exist",	dn));
					}
					SDMSExitState es = null;
					Long esdId = esd.getId(sysEnv);
					try {
						es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(rEspId, esdId));
					} catch (NotFoundException nfe) {
						SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, rEspId);
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03205010116",
						                               "Exit State $1 not in Exit State Profile $2 of $3",
						                               dn, esp.getName(sysEnv), se.pathString(sysEnv)));
					}
					if (!es.getIsFinal(sysEnv).booleanValue()) {
						SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, rEspId);
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03205010117",
						                               "Exit State $1 in Exit State Profile $2 of $3 must be a final state",
						                               dn, esp.getName(sysEnv), se.pathString(sysEnv)));
					}
					SDMSDependencyStateTable.table.create(sysEnv, ddId, esdId, condition);
				}
			}
		}
		for(idx = 0; idx < act_ds.size(); idx++) {
			ds = (SDMSDependencyState) act_ds.get(idx);
			ds.delete(sysEnv);
		}
	}

	private void diffChildren(SystemEnvironment sysEnv, SDMSSchedulingEntity se, Vector childdeflist)
	throws SDMSException
	{
		SDMSSchedulingHierarchy sh;
		int idx;
		WithHash cwh;
		String cName;
		Vector cPath;
		Long newChildId;
		Boolean isStatic;
		Integer prio;
		Integer suspend;
		String shResumeAt;
		Integer shResumeIn;
		Integer shResumeBase;
		String estpName;
		Integer mergeMode;
		Vector depNames;
		String aliasName;
		Long estpId;

		Long parentId = se.getId(sysEnv);

		Vector act_childs = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, parentId);

		if(childdeflist != null) {
			Iterator i = childdeflist.iterator();
			while(i.hasNext()) {

				WithHash wh = (WithHash) i.next();

				cwh = (WithHash) wh.get(ParseStr.S_FULLNAME);
				if(cwh == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03112141753", "Missing Child Name"));
				}
				cName = (String) cwh.get(ParseStr.S_NAME);
				cPath = (Vector) cwh.get(ParseStr.S_PATH);

				SDMSSchedulingEntity seChild = SDMSSchedulingEntityTable.get(sysEnv, cPath, cName);
				newChildId = seChild.getId(sysEnv);

				isStatic = (Boolean) wh.get(ParseStr.S_STATIC);
				if(isStatic != null && isStatic.equals(Boolean.TRUE)) {
					if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.BATCH && se.getType(sysEnv).intValue() != SDMSSchedulingEntity.JOB)
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03406071443", "Only batches and jobs can have static children"));
				}

				prio = (Integer) wh.get(ParseStr.S_PRIORITY);
				suspend = (Integer) wh.get(ParseStr.S_SUSPEND);
				shResumeAt = null;
				shResumeIn = null;
				shResumeBase = null;

				mergeMode = (Integer) wh.get(ParseStr.S_MERGE_MODE);
				estpName = (String) wh.get(ParseStr.S_TRANSLATION);
				depNames = (Vector) wh.get(ParseStr.S_IGNORE);
				aliasName = (String) wh.get(ParseStr.S_ALIAS);
				estpId = null;

				sh = null;

				for(idx = 0; idx < act_childs.size(); idx++) {
					sh = (SDMSSchedulingHierarchy) act_childs.get(idx);
					if(newChildId.equals(sh.getSeChildId(sysEnv))) {
						act_childs.removeElementAt(idx);
						idx = -1;

						if(wh.containsKey(ParseStr.S_ALIAS))		sh.setAliasName(sysEnv, aliasName);
						if(wh.containsKey(ParseStr.S_STATIC))		sh.setIsStatic(sysEnv, isStatic);
						if(wh.containsKey(ParseStr.S_PRIORITY))		sh.setPriority(sysEnv, prio);
						if(wh.containsKey(ParseStr.S_SUSPEND))		sh.setSuspend(sysEnv, suspend);
						suspend = sh.getSuspend(sysEnv);
						if (suspend.intValue() == SDMSSchedulingHierarchy.SUSPEND) {
							Object resumeObj = wh.get(ParseStr.S_RESUME);
							if (resumeObj != null) {
								if (resumeObj instanceof String) shResumeAt = (String) resumeObj;
								else {
									WithHash rowh = (WithHash) resumeObj;
									shResumeIn = (Integer) rowh.get(ParseStr.S_MULT);
									shResumeBase = (Integer) rowh.get(ParseStr.S_INTERVAL);
								}
							}
						}
						if(wh.containsKey(ParseStr.S_RESUME)) {
							sh.setResumeAt(sysEnv, shResumeAt);
							sh.setResumeIn(sysEnv, shResumeIn);
							sh.setResumeBase(sysEnv, shResumeBase);
						}
						if(wh.containsKey(ParseStr.S_MERGE_MODE))	sh.setMergeMode(sysEnv, mergeMode);
						break;
					}
				}
				if(idx >= act_childs.size()) {

					if(isStatic == null) {
						if(se.getType(sysEnv).intValue() != SDMSSchedulingEntity.BATCH)
							isStatic = Boolean.FALSE;
						else	isStatic = Boolean.TRUE;
					}
					if(suspend == null) suspend = new Integer(SDMSSchedulingHierarchy.CHILDSUSPEND);
					if (suspend.intValue() == SDMSSchedulingHierarchy.SUSPEND) {
						Object resumeObj = wh.get(ParseStr.S_RESUME);
						if (resumeObj != null) {
							if (resumeObj instanceof String) shResumeAt = (String) resumeObj;
							else {
								WithHash rowh = (WithHash) resumeObj;
								shResumeIn = (Integer) rowh.get(ParseStr.S_MULT);
								shResumeBase = (Integer) rowh.get(ParseStr.S_INTERVAL);
							}
						}
					}
					if(mergeMode == null) mergeMode = new Integer(SDMSSchedulingHierarchy.NOMERGE);
					if(prio != null) {
						if(prio.intValue() > SchedulingThread.MIN_PRIORITY)
							prio = new Integer(SchedulingThread.MIN_PRIORITY);
						if(prio.intValue() < - SchedulingThread.MIN_PRIORITY)
							prio = new Integer(- SchedulingThread.MAX_PRIORITY);
					} else prio = new Integer(0);

					estpId = null;
					if (parentId.equals(newChildId)) {
						throw new CommonErrorException (new SDMSMessage(sysEnv, "03204292208",
						                                "A job or batch cannot have itself as child"));
					}

					sh = SDMSSchedulingHierarchyTable.table.create(sysEnv, parentId, newChildId, aliasName, isStatic, prio,
					                suspend, shResumeAt, shResumeIn, shResumeBase, mergeMode, estpId);
				}

				if(aliasName != null) {
					Vector av = SDMSSchedulingHierarchyTable.idx_parentId_aliasName.getVector(sysEnv, new SDMSKey(parentId, aliasName));
					if(av.size() > 1) {
						throw new CommonErrorException (new SDMSMessage (sysEnv, "03212051509", "Duplicate Aliasname $1", aliasName));
					}
				}

				if(wh.containsKey(ParseStr.S_IGNORE))
					diffIgnoredDependencies(sysEnv, sh, depNames);
			}
		}

		for(idx = 0; idx < act_childs.size(); idx++) {
			sh = (SDMSSchedulingHierarchy) act_childs.get(idx);
			sh.delete(sysEnv);
		}
	}

	private void diffIgnoredDependencies(SystemEnvironment sysEnv, SDMSSchedulingHierarchy sh, Vector depNames)
	throws SDMSException
	{
		int idx;
		SDMSIgnoredDependency id;
		String ddName;
		Long shId = sh.getId(sysEnv);

		Vector act_id = SDMSIgnoredDependencyTable.idx_shId.getVector(sysEnv, shId);

		if(depNames != null) {
			for(int i = 0; i < depNames.size(); i++) {
				String dn = (String) depNames.get(i);

				for(idx = 0; idx < act_id.size(); idx++) {
					id = (SDMSIgnoredDependency) act_id.get(idx);
					ddName = id.getDdName(sysEnv);
					if(dn.equals(ddName)) {
						act_id.removeElementAt(idx);
						idx = -1;
						break;
					}
				}
				if(idx >= act_id.size()) {
					SDMSIgnoredDependencyTable.table.create(sysEnv, shId, dn);
				}
			}
		}
		for(idx = 0; idx < act_id.size(); idx++) {
			id = (SDMSIgnoredDependency) act_id.get(idx);
			id.delete(sysEnv);
		}
	}

	private void diffParameters(SystemEnvironment sysEnv, Long seId, WithHash parameters)
	throws SDMSException
	{
		SDMSParameterDefinition pd;
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId);
		String oldnm;
		int idx;
		String lpn;

		Vector act_parms = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, seId);

		if(parameters != null) {
			Set s = parameters.keySet();
			Iterator i = s.iterator();
			while(i.hasNext()) {
				String pn = (String) i.next();
				Vector pv = (Vector) parameters.get(pn);
				WithItem pt = (WithItem) pv.get(0);
				String pdef = (String) pv.get(1);
				Boolean isLocal = (Boolean) pv.get(2);
				Integer type = (pt == null ? new Integer(SDMSParameterDefinition.PARAMETER) : (Integer) pt.key);
				Integer aggFunction = new Integer(SDMSParameterDefinition.NONE);
				Long linkPdId = null;
				switch(type.intValue()) {
				case SDMSParameterDefinition.PARAMETER:
				case SDMSParameterDefinition.RESULT:
				case SDMSParameterDefinition.IMPORT:
					if(pdef != null) pdef = "=" + pdef;
					break;
				case SDMSParameterDefinition.EXPRESSION:
					Vector pev = (Vector) pt.value;
					aggFunction = (Integer) pev.get(0);
					pdef = "=" + (String) pev.get(1);
					break;
				case SDMSParameterDefinition.CONSTANT:
					pdef = "=" + (String) pt.value;
					break;
				case SDMSParameterDefinition.REFERENCE:
				case SDMSParameterDefinition.CHILDREFERENCE:
					if(pdef != null) pdef = "=" + pdef;
					PathVector lsev = (PathVector) pt.value;
					lpn = (String) lsev.remove(lsev.size() - 1);
					Long lseId = SDMSSchedulingEntityTable.get(sysEnv, lsev, null).getId(sysEnv);
					final SDMSParameterDefinition lpd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(lseId, lpn));
					if(!lpd.getIsLocal(sysEnv).booleanValue()) {
						linkPdId = lpd.getId(sysEnv);
					} else {
						throw new CommonErrorException(
						        new SDMSMessage(sysEnv, "03603061310", "Local parameters cannot be referenced")
						);
					}
					break;
				case SDMSParameterDefinition.RESOURCEREFERENCE:
					if(pdef != null) pdef = "=" + pdef;
					PathVector lnrv = (PathVector) pt.value;
					lpn = (String) lnrv.remove(lnrv.size() - 1);
					Long lnrId = SDMSNamedResourceTable.getNamedResource(sysEnv, lnrv).getId(sysEnv);

					if(!checkResourceRequirement(sysEnv, lnrId, se))
						throw new CommonErrorException(
						        new SDMSMessage(sysEnv, "03409290926", "Resource $2 for parameter $1 not required", pn, lnrv)
						);

					linkPdId = (SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(lnrId, lpn))).getId(sysEnv);
					break;
				}

				for(idx = 0; idx < act_parms.size(); idx++) {
					pd = (SDMSParameterDefinition) act_parms.get(idx);
					oldnm = pd.getName(sysEnv);
					if(oldnm.equals(pn)) {
						act_parms.removeElementAt(idx);
						idx = -1;

						pd.setType(sysEnv, type);
						pd.setAggFunction(sysEnv, aggFunction);
						pd.setDefaultValue(sysEnv, pdef);
						pd.setIsLocal(sysEnv, isLocal);
						pd.setLinkPdId(sysEnv, linkPdId);
						break;
					}
				}
				if(idx >= act_parms.size()) {

					SDMSParameterDefinitionTable.table.create(sysEnv, seId, pn, type, aggFunction, pdef, isLocal, linkPdId);
				}
			}
		}

		for(idx = 0; idx < act_parms.size(); idx++) {
			pd = (SDMSParameterDefinition) act_parms.get(idx);

			if(SDMSParameterDefinitionTable.idx_linkPdId.containsKey(sysEnv, pd.getId(sysEnv))) {
				SDMSParameterDefinition rpd;
				SDMSSchedulingEntity rpdse;
				Vector rpdv = SDMSParameterDefinitionTable.idx_linkPdId.getVector(sysEnv, pd.getId(sysEnv));
				rpd = (SDMSParameterDefinition) rpdv.get(0);
				rpdse = SDMSSchedulingEntityTable.getObject(sysEnv, rpd.getSeId(sysEnv));
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03208291546", "Parameter $1 is referenced by $2($3)",
				                               pd.getName(sysEnv), rpdse.pathString(sysEnv), rpd.getName(sysEnv)));
			}
			pd.delete(sysEnv);
		}

	}

	private void diffResources(SystemEnvironment sysEnv, Long seId, Vector resourcedeflist)
	throws SDMSException
	{
		int idx;
		Vector newnm;
		Long   nrId;
		Integer amount;
		Integer lockmode;
		String rsmpname;
		Long rsmpId;
		Integer keepMode;
		Boolean isSticky;
		WithHash expired;
		Integer exp_mult;
		Integer exp_interval;
		Vector  states;
		String  condition;

		SDMSResourceRequirement rr;

		Vector act_resrc = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, seId);

		if(resourcedeflist != null) {
			Iterator i = resourcedeflist.iterator();
			while(i.hasNext()) {
				WithHash wh = (WithHash) i.next();

				newnm = (Vector) wh.get(ParseStr.S_NAME);
				if(newnm == null)
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03204301100", "No resourcename specified"));
				Long newNrId = SDMSNamedResourceTable.pathToId(sysEnv, newnm);

				amount = (Integer) wh.get(ParseStr.S_AMOUNT);
				lockmode = (Integer) wh.get(ParseStr.S_LOCKMODE);
				rsmpname = (String) wh.get(ParseStr.S_MAP_STATUS);
				keepMode = (Integer) wh.get(ParseStr.S_KEEP);
				isSticky = (Boolean) wh.get(ParseStr.S_STICKY);
				expired = (WithHash) wh.get(ParseStr.S_EXPIRED);
				states = (Vector) wh.get(ParseStr.S_STATUS);
				condition = (String) wh.get(ParseStr.S_CONDITION);
				if (condition != null) {
					sysEnv.checkFeatureAvailability(SystemEnvironment.S_CONDITIONAL_RESOURCES);
					condition = canonizeCondition(sysEnv, condition);
				}

				rr = null;
				for(idx = 0; idx < act_resrc.size(); idx++) {
					rr = (SDMSResourceRequirement) act_resrc.get(idx);
					nrId = rr.getNrId(sysEnv);

					if(nrId.equals(newNrId)) {
						act_resrc.removeElementAt(idx);
						idx = -1;

						if(wh.containsKey(ParseStr.S_AMOUNT))	rr.setAmount(sysEnv, amount);
						if(wh.containsKey(ParseStr.S_LOCKMODE))	rr.setLockmode(sysEnv, lockmode);
						if(wh.containsKey(ParseStr.S_MAP_STATUS)) {
							if(rsmpname != null) {
								rsmpId = SDMSResourceStateMappingProfileTable.idx_name_getUnique(sysEnv, rsmpname).getId(sysEnv);
							} else {
								rsmpId = null;
							}
							rr.setRsmpId(sysEnv, rsmpId);
						}
						if(wh.containsKey(ParseStr.S_KEEP))	rr.setKeepMode(sysEnv, keepMode);
						if(wh.containsKey(ParseStr.S_STICKY))	rr.setIsSticky(sysEnv, isSticky);
						if(wh.containsKey(ParseStr.S_EXPIRED)) {
							if(expired != null) {
								exp_mult = (Integer) expired.get(ParseStr.S_MULT);
								if(exp_mult == null) exp_mult = new Integer(1);
								exp_interval = (Integer) expired.get(ParseStr.S_INTERVAL);
							} else {
								exp_mult = null;
								exp_interval = null;
							}
							rr.setExpiredAmount(sysEnv, exp_mult);
							rr.setExpiredBase(sysEnv, exp_interval);
						}
						if(wh.containsKey(ParseStr.S_CONDITION)) rr.setCondition(sysEnv, condition);
						break;
					}
				}
				if(idx >= act_resrc.size()) {

					if(amount == null)	amount = new Integer(0);
					if(lockmode == null)	lockmode = new Integer(SDMSResourceRequirement.N);
					if(rsmpname == null) {
						rsmpId = null;
					} else {
						rsmpId = SDMSResourceStateMappingProfileTable.idx_name_getUnique(sysEnv, rsmpname).getId(sysEnv);
					}
					if(keepMode == null)	keepMode = new Integer(SDMSResourceRequirement.NOKEEP);
					if(isSticky == null)	isSticky = Boolean.FALSE;
					if(expired == null) {
						exp_mult = null;
						exp_interval = null;
					} else {
						exp_mult = (Integer) expired.get(ParseStr.S_MULT);
						if(exp_mult == null) exp_mult = new Integer(1);
						exp_interval = (Integer) expired.get(ParseStr.S_INTERVAL);
					}
					rr = SDMSResourceRequirementTable.table.create(sysEnv,
					                newNrId, seId, amount, keepMode, isSticky,
					                rsmpId, exp_mult, exp_interval, lockmode, condition);
				}

				if(wh.containsKey(ParseStr.S_STATUS))
					diffRequiredStates(sysEnv, rr, states);

				rr.check(sysEnv);
			}
		}

		for(idx = 0; idx < act_resrc.size(); idx++) {
			rr = (SDMSResourceRequirement) act_resrc.get(idx);
			rr.delete(sysEnv);
		}

	}

	private void checkNonAfterFinalTrigger(SystemEnvironment sysEnv, Long seId)
	throws SDMSException
	{
		Vector v = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
		for(int i = 0; i < v.size(); i++) {
			SDMSTrigger t = (SDMSTrigger) v.get(i);

			if(t.getType(sysEnv).intValue() != SDMSTrigger.AFTER_FINAL && t.getAction(sysEnv).intValue() != SDMSTrigger.RERUN)
				if(!t.getIsMaster(sysEnv).booleanValue())
					throw new CommonErrorException(
					        new SDMSMessage(sysEnv, "03209201623", "Cannot change type to milestone, other than AFTER FINAL trigger exist"));
		}
	}

	private void diffRequiredStates(SystemEnvironment sysEnv, SDMSResourceRequirement rr, Vector states)
	throws SDMSException
	{
		int idx;
		SDMSResourceReqStates rrs;
		SDMSResourceStateDefinition rsd;
		Long rrId = rr.getId(sysEnv);

		Vector act_rrs = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, rrId);

		if(states != null) {
			for(int i = 0; i < states.size(); i++) {
				String dn = (String) states.get(i);

				for(idx = 0; idx < act_rrs.size(); idx++) {
					rrs = (SDMSResourceReqStates) act_rrs.get(idx);
					rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, rrs.getRsdId(sysEnv));
					if(dn.equals(rsd.getName(sysEnv))) {
						act_rrs.removeElementAt(idx);
						idx = -1;
						break;
					}
				}
				if(idx >= act_rrs.size()) {
					rsd = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, dn);
					Long rsdId = rsd.getId(sysEnv);
					SDMSResourceReqStatesTable.table.create(sysEnv, rrId, rsdId);
				}
			}
		}
		for(idx = 0; idx < act_rrs.size(); idx++) {
			rrs = (SDMSResourceReqStates) act_rrs.get(idx);
			rrs.delete(sysEnv);
		}
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSSchedulingEntity se;
		Long seId;
		boolean espChanged = false;

		evaluateWith(sysEnv);
		try {
			se = SDMSSchedulingEntityTable.idx_folderId_name_getUnique(sysEnv, new SDMSKey(folderId, name));
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130031","No Job Definition altered"));
				return;
			}
			throw nfe;
		}
		seId = se.getId(sysEnv);

		Integer oldType = se.getType(sysEnv);
		if(otype == null)		otype = oldType;

		if(!withs.containsKey(ParseStr.S_RUN_PROGRAM))		runProgram = se.getRunProgram(sysEnv);
		if(!withs.containsKey(ParseStr.S_RERUN_PROGRAM))	rerunProgram = se.getRerunProgram(sysEnv);
		if(!withs.containsKey(ParseStr.S_KILL_PROGRAM))		killProgram = se.getKillProgram(sysEnv);
		if(!withs.containsKey(ParseStr.S_WORKDIR))		workdir = se.getWorkdir(sysEnv);
		if(!withs.containsKey(ParseStr.S_LOGFILE))	{
			logfile = se.getLogfile(sysEnv);
			truncLog = se.getTruncLog(sysEnv);
			if (logfile != null && truncLog == null)
				truncLog = Boolean.FALSE;
		}
		if(!withs.containsKey(ParseStr.S_ERRLOG))	{
			errlogfile = se.getErrlogfile(sysEnv);
			truncErrlog = se.getTruncErrlog(sysEnv);
			if (errlogfile != null && truncErrlog == null)
				truncErrlog = Boolean.FALSE;
		}
		if(!withs.containsKey(ParseStr.S_RUNTIME))	{
			expectedRuntime = se.getExpectedRuntime(sysEnv);
		}
		if(!withs.containsKey(ParseStr.S_FINAL))	{
			expectedFinaltime = se.getExpectedFinaltime(sysEnv);
		}
		if(!withs.containsKey(ParseStr.S_PRIORITY))		priority = se.getPriority(sysEnv);
		if(!withs.containsKey(ParseStr.S_MPRIORITY)) {
			minPriority = se.getMinPriority(sysEnv);
		} else if (withs.get(ParseStr.S_MPRIORITY) != null)
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_JOB_LEVEL_AGING_CONTROL);
		if(!withs.containsKey(ParseStr.S_AGING)) {
			agingAmount = se.getAgingAmount(sysEnv);
			agingBase = se.getAgingBase(sysEnv);
		} else if (withs.get(ParseStr.S_AGING) != null)
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_JOB_LEVEL_AGING_CONTROL);
		if(!withs.containsKey(ParseStr.S_SUSPEND))		submitSuspended = se.getSubmitSuspended(sysEnv);
		if(!submitSuspended.booleanValue()) {
			if (resumeAt != null || resumeIn != null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108161050",
				                               "Resume option requires a suspend option"));
			}
		}
		if(!withs.containsKey(ParseStr.S_MASTER))		masterSubmittable = se.getMasterSubmittable(sysEnv);
		gangSchedule = se.getGangSchedule(sysEnv);
		if(!withs.containsKey(ParseStr.S_DEPENDENCY_MODE))	dependencyOperation = se.getDependencyOperation(sysEnv);
		if(!withs.containsKey(ParseStr.S_MAPPING))		esmpId = se.getEsmpId(sysEnv);
		if(!withs.containsKey(ParseStr.S_PROFILE))	{
			espId = se.getEspId(sysEnv);
			esp = SDMSExitStateProfileTable.getObject(sysEnv, espId);
		}
		if(!withs.containsKey(ParseStr.S_ENVIRONMENT))		neId = se.getNeId(sysEnv);
		if(!withs.containsKey(ParseStr.S_FOOTPRINT))		fpId = se.getFpId(sysEnv);
		if(!withs.containsKey(ParseStr.S_TIMEOUT)) {
			to_mult = se.getTimeoutAmount(sysEnv);
			to_interval = se.getTimeoutBase(sysEnv);
			to_esdId = se.getTimeoutStateId(sysEnv);

			if(to_esdId != null && !SDMSExitStateTable.idx_espId_esdId.containsKey(sysEnv, new SDMSKey(espId, to_esdId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03311041003",
				                               "Timeoutstate $1 is not defined by the exit state profile $2",
				                               SDMSExitStateDefinitionTable.getObject(sysEnv, to_esdId).getName(sysEnv),
				                               esp.getName(sysEnv)));
			}
		}
		if(!withs.containsKey(ParseStr.S_GROUP)) 		gId = se.getOwnerId(sysEnv);

		type = otype.intValue();
		switch(type) {
		case SDMSSchedulingEntity.JOB:
			checkJob(sysEnv);
			break;
		case SDMSSchedulingEntity.BATCH:
			checkBatch(sysEnv);
			break;
		case SDMSSchedulingEntity.MILESTONE:
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_MILESTONES);
		default:
			throw new FatalException(new SDMSMessage(sysEnv, "03204291141", "Unknown Job Definition Type $1", otype));
		}

		if(oldType.intValue() == SDMSSchedulingEntity.JOB &&
		    otype.intValue()   != SDMSSchedulingEntity.JOB)	{

			checkDependents(sysEnv, seId);

			Vector tv = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
			Iterator i = tv.iterator();
			while (i.hasNext()) {
				SDMSTrigger t = (SDMSTrigger) i.next();
				if (t.getAction(sysEnv).intValue() == SDMSTrigger.RERUN)
					t.delete(sysEnv);
			}
		}

		if (withs.containsKey(ParseStr.S_INHERIT)) {
			Long inheritPrivs = (Long) withs.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
			long lpriv = inheritPrivs.longValue();
			if((se.getPrivilegeMask() & lpriv) != lpriv) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061132", "Incompatible grant"));
			}

		} else
			inheritPrivs = se.getInheritPrivs(sysEnv);

		se.setType(sysEnv, otype);
		se.setRunProgram(sysEnv, runProgram);
		se.setRerunProgram(sysEnv, rerunProgram);
		se.setKillProgram(sysEnv, killProgram);
		se.setWorkdir(sysEnv, workdir);
		se.setLogfile(sysEnv, logfile);
		se.setTruncLog(sysEnv, truncLog);
		se.setErrlogfile(sysEnv, errlogfile);
		se.setTruncErrlog(sysEnv, truncErrlog);
		se.setExpectedRuntime(sysEnv, expectedRuntime);
		se.setExpectedFinaltime(sysEnv, expectedFinaltime);
		se.setGetExpectedRuntime(sysEnv, getExpectedRuntime);
		se.setPriority(sysEnv, priority);
		se.setMinPriority(sysEnv, minPriority);
		se.setAgingAmount(sysEnv, agingAmount);
		se.setAgingBase(sysEnv, agingBase);
		se.setSubmitSuspended(sysEnv, submitSuspended);
		se.setResumeAt(sysEnv, resumeAt);
		se.setResumeIn(sysEnv, resumeIn);
		se.setResumeBase(sysEnv, resumeBase);
		se.setTimeoutAmount(sysEnv, to_mult);
		se.setTimeoutBase(sysEnv, to_interval);
		se.setTimeoutStateId(sysEnv, to_esdId);
		se.setSameNode(sysEnv, sameNode);
		se.setGangSchedule(sysEnv, gangSchedule);
		se.setDependencyOperation(sysEnv, dependencyOperation);
		se.setEsmpId(sysEnv, esmpId);
		se.setOwnerId(sysEnv, gId);
		se.setInheritPrivs(sysEnv, inheritPrivs);

		Boolean oldMasterSubmittable = se.getMasterSubmittable(sysEnv);
		if (oldMasterSubmittable.booleanValue() && !masterSubmittable.booleanValue()) {
			if (SDMSEventTable.idx_seId.containsKey(sysEnv, seId))
				throw new CommonErrorException(new SDMSMessage(sysEnv,
				                               "02402181250", "Cannot change master submittable to false, because time scheduling events are defined on $1",
				                               se.pathString(sysEnv)));

			Vector tv = SDMSTriggerTable.idx_seId.getVector(sysEnv, seId);
			for(int tvi = 0; tvi < tv.size(); ++tvi) {
				final SDMSTrigger tt = (SDMSTrigger) tv.get(tvi);
				if(tt.getIsMaster(sysEnv).booleanValue()) {
					if (tt.getObjectType(sysEnv).intValue() == SDMSTrigger.OBJECT_MONITOR) {
						if (tt.getMainSeId(sysEnv) != null) continue;
					}
					throw new CommonErrorException(new SDMSMessage(sysEnv,
					                               "02402181251",
					                               "Cannot change master submittable to false, because master triggers are still defined using $1",
					                               se.pathString(sysEnv)));
				}
			}

			tv = SDMSTriggerTable.idx_mainSeId.getVector(sysEnv, seId);
			for(int tvi = 0; tvi < tv.size(); ++tvi) {
				final SDMSTrigger tt = (SDMSTrigger) tv.get(tvi);
				if(tt.getIsMaster(sysEnv).booleanValue()) {
					throw new CommonErrorException(new SDMSMessage(sysEnv,
					                               "03209191209",
					                               "Cannot change master submittable to false, because master triggers are still defined using $1",
					                               se.pathString(sysEnv)));
				}
			}
		}
		se.setMasterSubmittable(sysEnv, masterSubmittable);

		if (! espId.equals(se.getEspId(sysEnv))) {
			se.setEspId(sysEnv, espId);
			espChanged = true;
		}

		se.setQaId(sysEnv, null);
		se.setNeId(sysEnv, neId);
		se.setFpId(sysEnv, fpId);

		if(withs.containsKey(ParseStr.S_REQUIRED))
			diffDependencies(sysEnv, seId, dependencydeflist);

		if(withs.containsKey(ParseStr.S_CHILDREN))
			diffChildren(sysEnv, se, childdeflist);

		if(withs.containsKey(ParseStr.S_RESOURCE) ||
		    (oldType.intValue() == SDMSSchedulingEntity.JOB && otype.intValue() != SDMSSchedulingEntity.JOB)
		  )
			diffResources(sysEnv, seId, resourcedeflist);

		if(withs.containsKey(ParseStr.S_PARAMETERS))
			diffParameters(sysEnv, seId, parameters);

		checkParameterRI(sysEnv, seId);

		if(priowarn)
			result.setFeedback(new SDMSMessage(sysEnv, "03303251626","Job Definition altered, WARNING: priority adjusted"));
		else
			result.setFeedback(new SDMSMessage(sysEnv, "03204041251","Job Definition altered"));
	}

}

