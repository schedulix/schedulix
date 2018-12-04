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
import de.independit.scheduler.server.parser.triggerexpr.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.cmdline.*;

public abstract class ManipJobDefinition extends Node
{

	Vector path;
	WithHash withs;
	String name;
	Long folderId;
	Integer otype;
	int type;
	String runProgram;
	String rerunProgram;
	String killProgram;
	String workdir;
	String logfile;
	Boolean truncLog;
	String errlogfile;
	Boolean truncErrlog;
	Integer expectedRuntime;
	Integer expectedFinaltime;
	String getExpectedRuntime;
	Integer priority;
	Integer minPriority;
	WithHash aging;
	Integer agingAmount = null;
	Integer agingBase = null;
	Boolean submitSuspended;
	String resumeAt = null;
	Integer resumeIn = null;
	Integer resumeBase = null;
	Boolean masterSubmittable;
	Boolean sameNode;
	Boolean gangSchedule;
	Integer dependencyOperation;
	Long esmpId;
	Long espId;
	Long qaId;
	Long neId;
	Long fpId;
	WithHash timeout;
	Integer to_mult = null;
	Integer to_interval = null;
	String  to_state = null;
	Long to_esdId;
	String gName;
	Long gId;
	Long inheritPrivs;

	Vector childdeflist;
	Vector dependencydeflist;
	Vector resourcedeflist;
	WithHash parameters;

	SDMSExitStateProfile esp;
	boolean priowarn = false;
	boolean noerr;
	HashSet resourceList = null;

	public ManipJobDefinition(Vector p, String n, WithHash w, Boolean ne)
	{
		super();
		name = n;
		path = p;
		withs = w;
		noerr = ne.booleanValue();
	}

	protected void evaluateWith(SystemEnvironment sysEnv)
		throws SDMSException
	{
		String s;
		WithHash w;

		folderId = SDMSFolderTable.pathToId(sysEnv, path);

		otype = ((Integer) withs.get(ParseStr.S_TYPE));

		runProgram = (String) withs.get(ParseStr.S_RUN_PROGRAM);

		rerunProgram = (String) withs.get(ParseStr.S_RERUN_PROGRAM);

		killProgram = (String) withs.get(ParseStr.S_KILL_PROGRAM);

		workdir = (String) withs.get(ParseStr.S_WORKDIR);

		w = (WithHash) withs.get(ParseStr.S_LOGFILE);
		if(w != null) {
			logfile = (String) w.get(ParseStr.S_FILENAME);
			truncLog = (Boolean) w.get(ParseStr.S_TRUNC);
		}

		w = (WithHash) withs.get(ParseStr.S_ERRLOG);
		if(w != null) {
			errlogfile = (String) w.get(ParseStr.S_FILENAME);
			truncErrlog = (Boolean) w.get(ParseStr.S_TRUNC);
		}

		expectedRuntime = (Integer) withs.get(ParseStr.S_RUNTIME);

		expectedFinaltime = (Integer) withs.get(ParseStr.S_FINAL);

		priority = (Integer) withs.get(ParseStr.S_PRIORITY);

		minPriority = (Integer) withs.get(ParseStr.S_MPRIORITY);

		aging = (WithHash) withs.get(ParseStr.S_AGING);

		if (minPriority != null || aging != null)
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_JOB_LEVEL_AGING_CONTROL);

		submitSuspended = (Boolean) withs.get(ParseStr.S_SUSPEND);

		Object resumeObj = withs.get(ParseStr.S_RESUME);
		if (resumeObj != null) {
			if (resumeObj instanceof String) {
				resumeAt = (String) resumeObj;
			} else {
				WithHash wh = (WithHash) resumeObj;
				resumeIn = (Integer) wh.get(ParseStr.S_MULT);
				resumeBase = (Integer) wh.get(ParseStr.S_INTERVAL);
			}
		}

		masterSubmittable = (Boolean) withs.get(ParseStr.S_MASTER);

		sameNode = null;

		gangSchedule = null;

		dependencyOperation = (Integer) withs.get(ParseStr.S_DEPENDENCY_MODE);

		s = (String) withs.get(ParseStr.S_MAPPING);
		if(s != null) {
			esmpId = SDMSExitStateMappingProfileTable.idx_name_getUnique(sysEnv, s).getId(sysEnv);
		} else {
			esmpId = null;
		}

		s = (String) withs.get(ParseStr.S_PROFILE);
		if(s != null) {
			esp = SDMSExitStateProfileTable.idx_name_getUnique(sysEnv, s);
			espId = esp.getId(sysEnv);
		} else {
			esp =  null;
			espId = null;
		}

		childdeflist = (Vector) withs.get(ParseStr.S_CHILDREN);

		dependencydeflist = (Vector) withs.get(ParseStr.S_REQUIRED);

		resourcedeflist = (Vector) withs.get(ParseStr.S_RESOURCE);

		timeout = (WithHash) withs.get(ParseStr.S_TIMEOUT);

		parameters = (WithHash) withs.get(ParseStr.S_PARAMETERS);

		s = (String) withs.get(ParseStr.S_ENVIRONMENT);
		if(s != null) {
			SDMSNamedEnvironment ne = SDMSNamedEnvironmentTable.idx_name_getUnique(sysEnv, s);
			neId = ne.getId(sysEnv);
		}

		s = (String) withs.get(ParseStr.S_FOOTPRINT);
		if(s != null) {
			fpId = SDMSFootprintTable.idx_name_getUnique(sysEnv, s).getId(sysEnv);
		} else {
			fpId = null;
		}

		gName = (String) withs.get(ParseStr.S_GROUP);
		if(gName != null) {
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
		} else {
			final SDMSUser u = SDMSUserTable.getObject(sysEnv, env.uid());
			gId = u.getDefaultGId(sysEnv);
		}

		if (withs.containsKey(ParseStr.S_INHERIT)) {
			inheritPrivs = (Long) withs.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
		} else
			inheritPrivs = new Long(0);
		long lpriv = inheritPrivs.longValue();
		inheritPrivs = new Long(lpriv);
	}

	protected void checkJob(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(runProgram == null || runProgram.equals("")) {
			throw new CommonErrorException(
				new SDMSMessage(sysEnv, "02112141002", "Job needs a Run Program"));
		}
		if(logfile == null) truncLog = null;
		if(errlogfile == null)  truncErrlog = null;
		if(priority != null) {
			if(priority.intValue() > SchedulingThread.MIN_PRIORITY) {
				priority = new Integer(SchedulingThread.MIN_PRIORITY);
				priowarn = true;
			}
			if(priority.intValue() < SchedulingThread.MAX_PRIORITY) {
				priority = new Integer(SchedulingThread.MAX_PRIORITY);
				priowarn = true;
			}
			if(priority.intValue() < SystemEnvironment.priorityLowerBound && !sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				priority = new Integer(SystemEnvironment.priorityLowerBound);
				priowarn = true;
			}
		} else  priority = new Integer(SchedulingThread.DEFAULT_PRIORITY);
		if(minPriority != null) {
			if(minPriority.intValue() > SchedulingThread.MIN_PRIORITY) {
				minPriority = new Integer(SchedulingThread.MIN_PRIORITY);
				priowarn = true;
			}
			if(minPriority.intValue() < SchedulingThread.MAX_PRIORITY) {
				minPriority = new Integer(SchedulingThread.MAX_PRIORITY);
				priowarn = true;
			}
			if(minPriority.intValue() < SystemEnvironment.priorityLowerBound && !sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				priority = new Integer(SystemEnvironment.priorityLowerBound);
				priowarn = true;
			}
		}
		if(aging != null) {
			agingAmount = (Integer) aging.get(ParseStr.S_MULT);
			agingBase   = (Integer) aging.get(ParseStr.S_INTERVAL);
		}
		if(submitSuspended == null) {
			submitSuspended = Boolean.FALSE;
		}

		if(masterSubmittable == null) masterSubmittable = Boolean.FALSE;
		if(dependencyOperation == null) dependencyOperation = new Integer (SDMSSchedulingEntity.AND);
		if(esp == null) {
			throw new CommonErrorException(
				new SDMSMessage(sysEnv, "02112140955",
						 "Missing Exit State Profile"
				)
			);
		}
		if(esmpId == null) {
			if(esp.getDefaultEsmpId(sysEnv) == null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02112140957", "Exit State Profile doesn't define a Default Exit State Mapping"));
			}
		} else {
			esp.validateMappingProfile(sysEnv, esmpId);
		}

		if(neId == null) {
			throw new CommonErrorException(
				new SDMSMessage(sysEnv, "03203062019", "Environment missing")
			);
		}

		SDMSNamedEnvironment ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId);
		Vector gv = new Vector();
		gv.add(SDMSObject.publicGId);
		gv.add(gId);
		if(ne.getPrivileges(sysEnv, SDMSPrivilege.USE, false, gv) != SDMSPrivilege.USE) {
			String gName = SDMSGroupTable.getObject(sysEnv,gId).getName(sysEnv);
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402131040", "Insufficient privileges of group $1 for environment $2", gName, ne.getName(sysEnv)));
		}

		getExpectedRuntime = null;

		if(timeout != null) {
			to_mult = (Integer) timeout.get(ParseStr.S_MULT);
			if(to_mult == null) to_mult = new Integer(1);
			to_interval = (Integer) timeout.get(ParseStr.S_INTERVAL);
			to_state = (String) timeout.get(ParseStr.S_STATUS);
			to_esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, to_state).getId(sysEnv);

			if(to_esdId != null && !SDMSExitStateTable.idx_espId_esdId.containsKey(sysEnv, new SDMSKey(espId, to_esdId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03311031749",
					"Timeout state $1 is not contained in profile $2", to_state, esp.getName(sysEnv)));
			}
		}
		if(sameNode == null) sameNode = Boolean.FALSE;
		if(gangSchedule == null) gangSchedule = Boolean.FALSE;

		StringReader sr = new StringReader(runProgram);
		CmdLineScanner cmds = new CmdLineScanner(sr);
		CmdLineParser cmdp = new CmdLineParser();
		cmdp.set(sysEnv, null);
		cmdp.setCheckOnly();
		SDMSMessage msg = null;
		try {
			cmdp.yyparse(cmds);
		} catch (CmdLineParser.yyException yye) {
			msg = new SDMSMessage(sysEnv, "03402291355", "Error parsing run command (missing (double) quotes?)");
		} catch (IOException ioe) {
			msg = new SDMSMessage(sysEnv, "03402291401", "I/O Error parsing run command ($1)", ioe);
		} catch (NonRecoverableException cce) {
			msg = new SDMSMessage(sysEnv, "03402291421", "Error parsing run command (missing quotes?)");
		}
		if(msg != null)
			throw new CommonErrorException(msg);

		if(rerunProgram != null) {
			sr = new StringReader(rerunProgram);
			cmds = new CmdLineScanner(sr);
			cmdp = new CmdLineParser();
			cmdp.set(sysEnv, null);
			cmdp.setCheckOnly();
			try {
				cmdp.yyparse(cmds);
			} catch (CmdLineParser.yyException yye) {
				msg = new SDMSMessage(sysEnv, "03402291507", "Error parsing rerun command (missing (double) quotes?)");
			} catch (IOException ioe) {
				msg = new SDMSMessage(sysEnv, "03402291508", "I/O Error parsing rerun command ($1)", ioe);
			} catch (NonRecoverableException cce) {
				msg = new SDMSMessage(sysEnv, "03402291509", "Error parsing rerun command (missing quotes?)");
			}
			if(msg != null)
				throw new CommonErrorException(msg);
		}
	}

	protected void checkBatch(SystemEnvironment sysEnv)
		throws SDMSException
	{
		runProgram = null;
		rerunProgram = null;
		killProgram = null;
		workdir = null;
		logfile = null;
		errlogfile = null;
		truncLog = null;
		truncErrlog = null;
		if(masterSubmittable == null) masterSubmittable = Boolean.FALSE;
		if(dependencyOperation == null) dependencyOperation = new Integer (SDMSSchedulingEntity.AND);
		if(priority != null) {
			if(priority.intValue() > SchedulingThread.MIN_PRIORITY) {
				priority = new Integer(SchedulingThread.MIN_PRIORITY);
				priowarn = true;
			}
			if(priority.intValue() < -SchedulingThread.MIN_PRIORITY) {
				priority = new Integer(-SchedulingThread.MIN_PRIORITY);
				priowarn = true;
			}
		} else  priority = new Integer(0);
		minPriority = null;
		if(submitSuspended == null) submitSuspended = Boolean.FALSE;
		if(esp == null) {
			throw new CommonErrorException( new SDMSMessage(sysEnv, "02112140956", "Missing Exit State Profile"));
		}
		esmpId = null;

		neId = null;
		fpId = null;
		getExpectedRuntime = null;
		resourcedeflist = null;
		sameNode = null;
		gangSchedule = null;
		to_mult = null;
		to_interval = null;
		to_state = null;
		to_esdId = null;
	}

	protected void checkMilestone(SystemEnvironment sysEnv)
	throws SDMSException
	{
		checkBatch(sysEnv);
		masterSubmittable = Boolean.FALSE;
		minPriority = null;
		resourcedeflist = null;
		parameters = null;
	}

	protected String checkChildDependencies (SystemEnvironment sysEnv, Long checkId, Long childId)
		throws SDMSException
	{
		SDMSKey k = new SDMSKey(checkId, childId);
		if (SDMSDependencyDefinitionTable.idx_DependentId_RequiredId.containsKey(sysEnv, k)) {
			return childId.toString();
		}
		Vector sh_v = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, childId);
		Iterator i = sh_v.iterator();
		while (i.hasNext()) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)i.next();
			String s = checkChildDependencies(sysEnv, checkId, sh.getSeChildId(sysEnv));
			if (s != null) {
				return sh.getSeParentId(sysEnv) + "->" + s;
			}
		}
		return null;
	}

	protected void checkTranslation(SystemEnvironment sysEnv, SDMSSchedulingEntity seChild, SDMSSchedulingEntity seParent, SDMSExitStateTranslationProfile estp)
	throws SDMSException
	{
		Vector es_v = SDMSExitStateTable.idx_espId.getVector(sysEnv, seChild.getEspId(sysEnv));
		Iterator ies = es_v.iterator();
		Long espId = seParent.getEspId(sysEnv);
		while (ies.hasNext()) {
			SDMSExitState esChild = (SDMSExitState)ies.next();
			Long esdIdChild = esChild.getEsdId(sysEnv);
			Long esdIdParent = esdIdChild;
			if (estp != null) {
				esdIdParent = estp.translate(sysEnv, esdIdChild, false);
				if (esdIdParent == null)
					continue;
			} else {
				continue;
			}
			SDMSKey k = new SDMSKey (espId, esdIdParent);
			if (!SDMSExitStateTable.idx_espId_esdId.containsKey(sysEnv, k)) {
				Object[] p = new Object[3];
				p[0] = SDMSExitStateDefinitionTable.getObject(sysEnv, esdIdChild).getName(sysEnv);
				p[1] = seChild.pathString(sysEnv);
				p[2] = (estp == null ? "NONE" : estp.getName(sysEnv));
				throw new CommonErrorException(new SDMSMessage (sysEnv, "02112201828",
				                               "Profile doesn't contain translated child state $1 of $2 Translation $3", p));
			} else {
				SDMSExitState esParent = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, k);
				if (esParent.getIsFinal(sysEnv).equals(Boolean.FALSE)) {
					if (esChild.getIsFinal(sysEnv).equals(Boolean.TRUE)) {
						Object[] p = new Object[5];
						p[0] = SDMSExitStateDefinitionTable.getObject(sysEnv, esdIdChild).getName(sysEnv);
						p[1] = seChild.pathString(sysEnv);
						p[2] = SDMSExitStateDefinitionTable.getObject(sysEnv, esdIdParent).getName(sysEnv);
						p[3] = seParent.pathString(sysEnv);
						p[4] = (estp == null ? "NONE" : estp.getName(sysEnv));
						throw new CommonErrorException(new SDMSMessage (sysEnv, "02205061803",
						                               "Invalid translation from final child state $1 of $2 to non final state $3 of $4, Translation = [$5]", p));
					}
				}
			}
		}
	}

	public void addOrAlterChild(SystemEnvironment sysEnv, WithHash wh, SDMSSchedulingEntity seParent, boolean isAdd, boolean processError)
		throws SDMSException
	{
		SDMSSchedulingHierarchy sh;

		WithHash cwh;
		String cName;
		Vector cPath;
		Long childId;
		Boolean isStatic;
		Boolean isDisabled = Boolean.FALSE;
		Integer prio;
		Integer suspend;
		String shResumeAt;
		Integer shResumeIn;
		Integer shResumeBase;
		String estpName;
		String intName;
		Integer mergeMode;
		Long estpId;
		Long intId;
		Vector depNames;
		String aliasName;
		int parentType;

		Long parentId = seParent.getId(sysEnv);

		cwh = (WithHash) wh.get(ParseStr.S_FULLNAME);
		if(cwh == null) {
			throw new CommonErrorException(
				new SDMSMessage(sysEnv, "03112141754",
					"Missing Child Name"
					)
				);
		}
		cName = (String) cwh.get(ParseStr.S_NAME);
		cPath = (Vector) cwh.get(ParseStr.S_PATH);

		parentType = seParent.getType(sysEnv).intValue();
		isStatic = (Boolean) wh.get(ParseStr.S_STATIC);
		if(isStatic == null) {
			if(parentType == SDMSSchedulingEntity.BATCH)
				isStatic = Boolean.TRUE;
			else
				isStatic = Boolean.FALSE;
		}

		if(wh.containsKey(ParseStr.S_ENABLE))
			isDisabled = new Boolean(!((Boolean) wh.get(ParseStr.S_ENABLE)).booleanValue());

		if(parentType != SDMSSchedulingEntity.BATCH && parentType != SDMSSchedulingEntity.JOB && isStatic.equals(Boolean.TRUE)) {
			throw new CommonErrorException(
				new SDMSMessage(sysEnv, "03406071422", "Only batches and Jobs can have static children"));
		}

		prio = (Integer) wh.get(ParseStr.S_PRIORITY);
		if(prio != null) {
			if(prio.intValue() > SchedulingThread.MIN_PRIORITY)
				prio = new Integer(SchedulingThread.MIN_PRIORITY);
			if(prio.intValue() < - SchedulingThread.MIN_PRIORITY)
				prio = new Integer(- SchedulingThread.MAX_PRIORITY);
		} else prio = new Integer(0);

		suspend = (Integer) wh.get(ParseStr.S_SUSPEND);
		if(suspend == null) suspend = new Integer(SDMSSchedulingHierarchy.CHILDSUSPEND);
		shResumeAt = null;
		shResumeIn = null;
		shResumeBase = null;
		if (suspend.intValue() == SDMSSchedulingHierarchy.SUSPEND) {
			Object resumeObj = wh.get(ParseStr.S_RESUME);
			if (resumeObj != null) {
				if (resumeObj instanceof String) {
					shResumeAt = (String) resumeObj;
				} else {
					WithHash rowh = (WithHash) resumeObj;
					shResumeIn = (Integer) rowh.get(ParseStr.S_MULT);
					shResumeBase = (Integer) rowh.get(ParseStr.S_INTERVAL);
				}
			}
		}

		mergeMode = (Integer) wh.get(ParseStr.S_MERGE_MODE);
		if(mergeMode == null) mergeMode = new Integer(SDMSSchedulingHierarchy.NOMERGE);

		estpName = (String) wh.get(ParseStr.S_TRANSLATION);
		intName = (String) wh.get(ParseStr.S_INTERVAL);
		depNames = (Vector) wh.get(ParseStr.S_IGNORE);

		aliasName = (String) wh.get(ParseStr.S_ALIAS);

		SDMSSchedulingEntity seChild = SDMSSchedulingEntityTable.get(sysEnv, cPath, cName);
		if(!seChild.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
			throw new AccessViolationException(
					new SDMSMessage(sysEnv, "03402131121", "Execute privilege missing for $1", seChild.pathString(sysEnv))
				);
		childId = seChild.getId(sysEnv);

		estpId = null;
		if(estpName != null) {
			SDMSExitStateTranslationProfile estp = null;
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXIT_STATE_TRANSLATION);
			estp = SDMSExitStateTranslationProfileTable.idx_name_getUnique(sysEnv, estpName);
			estpId = estp.getId(sysEnv);

			checkTranslation(sysEnv, seChild, seParent, estp);
		}
		intId = null;
		if(intName != null) {
			SDMSInterval iv = null;
			try {
				iv = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(intName, 0));
			} catch(NotFoundException nfe) {
				iv = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(intName, null));
			}
			intId = iv.getId(sysEnv);
		}

		if (parentId.equals(childId)) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02112201726", "Cannot have itself as child"));
		}

		String s = checkChildDependencies (sysEnv, parentId, childId);
		if (s != null) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02112202010",
				"Dependency from children not allowed [$1]", parentId.toString() + "->" + s
				));
		}

		if(isAdd) {
			try {
				sh = SDMSSchedulingHierarchyTable.table.create(sysEnv, parentId, childId, aliasName, isStatic, isDisabled, prio,
				                suspend, shResumeAt, shResumeIn, shResumeBase, mergeMode, estpId, intId);
			} catch (DuplicateKeyException dke) {
				if(processError) {
					sh = SDMSSchedulingHierarchyTable.idx_parentId_childId_getUnique(sysEnv, new SDMSKey(parentId, childId));
					sh.setAliasName(sysEnv, aliasName);
					sh.setIsStatic(sysEnv, isStatic);
					sh.setIsDisabled(sysEnv, isDisabled);
					sh.setPriority(sysEnv, prio);
					sh.setSuspend(sysEnv, suspend);
					sh.setResumeAt(sysEnv, shResumeAt);
					sh.setResumeIn(sysEnv, shResumeIn);
					sh.setResumeBase(sysEnv, shResumeBase);
					sh.setMergeMode(sysEnv, mergeMode);
					sh.setEstpId(sysEnv, estpId);
					sh.setIntId(sysEnv, intId);

					Vector v = SDMSIgnoredDependencyTable.idx_shId.getVector(sysEnv, sh.getId(sysEnv));
					for(int i = 0; i < v.size(); i++) ((SDMSIgnoredDependency) v.get(i)).delete(sysEnv);
				} else {
					throw dke;
				}
			}
		} else {
			try {
				sh = SDMSSchedulingHierarchyTable.idx_parentId_childId_getUnique(sysEnv, new SDMSKey(parentId, childId));
				sh.setAliasName(sysEnv, aliasName);
				sh.setIsStatic(sysEnv, isStatic);
				sh.setIsDisabled(sysEnv, isDisabled);
				sh.setPriority(sysEnv, prio);
				sh.setSuspend(sysEnv, suspend);
				sh.setResumeAt(sysEnv, shResumeAt);
				sh.setResumeIn(sysEnv, shResumeIn);
				sh.setResumeBase(sysEnv, shResumeBase);
				sh.setMergeMode(sysEnv, mergeMode);
				sh.setEstpId(sysEnv, estpId);
				sh.setIntId(sysEnv, intId);

				Vector v = SDMSIgnoredDependencyTable.idx_shId.getVector(sysEnv, sh.getId(sysEnv));
				for(int i = 0; i < v.size(); i++) ((SDMSIgnoredDependency) v.get(i)).delete(sysEnv);
			} catch(NotFoundException nfe) {
				if(processError) return;
				throw nfe;
			}
		}

		if(aliasName != null) {
			Vector av = SDMSSchedulingHierarchyTable.idx_parentId_aliasName.getVector(sysEnv, new SDMSKey(parentId, aliasName));
			if(av.size() > 1) {
				throw new CommonErrorException (new SDMSMessage (sysEnv, "03212051503", "Duplicate Aliasname $1", aliasName));
			}
		}

		if(depNames != null) {
			Long shId = sh.getId(sysEnv);
			for(int i = 0; i < depNames.size(); i++) {
				String dn = (String) depNames.get(i);
				SDMSIgnoredDependencyTable.table.create(sysEnv, shId, dn);
			}
		}

	}

	private void buildResourceList(final SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		resourceList = new HashSet();
		Vector v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, se.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			final SDMSResourceRequirement rr = (SDMSResourceRequirement) v.get(i);
			resourceList.add(rr.getNrId(sysEnv));
		}
		final Long fpId = se.getFpId(sysEnv);
		if(fpId != null) {
			v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, fpId);
			for(int i = 0; i < v.size(); i++) {
				final SDMSResourceRequirement rr = (SDMSResourceRequirement) v.get(i);
				resourceList.add(rr.getNrId(sysEnv));
			}
		}
		final Long neId = se.getNeId(sysEnv);
		v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, neId);
		for(int i = 0; i < v.size(); i++) {
			final SDMSEnvironment e = (SDMSEnvironment) v.get(i);
			resourceList.add(e.getNrId(sysEnv));
		}
	}

	protected boolean checkResourceRequirement(final SystemEnvironment sysEnv, Long nrId, SDMSSchedulingEntity se)
		throws SDMSException
	{
		if(resourceList == null) {
			buildResourceList(sysEnv, se);
		}
		if(resourceList.contains(nrId))
			return true;
		return false;
	}

	public void addOrAlterParameters(SystemEnvironment sysEnv, WithHash wh, SDMSSchedulingEntity se, boolean isAdd, boolean processError)
		throws SDMSException
	{
		String lpn;

		Set s = wh.keySet();
		Iterator i = s.iterator();
		while(i.hasNext()) {
			String pn = (String) i.next();
			Vector pv = (Vector) wh.get(pn);
			WithItem pt = (WithItem) pv.get(0);
			String pdef = (String) pv.get(1);
			Boolean isLocal = (Boolean) pv.get(2);
			String exportName = (String) pv.get(3);
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
							new SDMSMessage(sysEnv, "03603061409", "A local parameter cannot be referenced")
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
							new SDMSMessage(sysEnv, "03409281727", "Resource $2 for parameter $1 not required", pn, lnrv)
						);
					linkPdId = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(lnrId, lpn)).getId(sysEnv);
					break;
			}
			if(isAdd) {
				try {
					SDMSParameterDefinitionTable.table.create(sysEnv, se.getId(sysEnv), pn, type, aggFunction, pdef, isLocal, linkPdId, exportName);
				} catch(DuplicateKeyException dke) {
					if(processError) {
						SDMSParameterDefinition pd =
							SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(se.getId(sysEnv), pn));
						pd.setType(sysEnv, type);
						pd.setAggFunction(sysEnv, aggFunction);
						pd.setDefaultValue(sysEnv, pdef);
						pd.setIsLocal(sysEnv, isLocal);
						pd.setLinkPdId(sysEnv, linkPdId);
						pd.setExportName(sysEnv, exportName);
					} else {
						throw dke;
					}
				}
			} else {
				try {
					SDMSParameterDefinition pd =
						SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(se.getId(sysEnv), pn));
					pd.setType(sysEnv, type);
					pd.setAggFunction(sysEnv, aggFunction);
					pd.setDefaultValue(sysEnv, pdef);
					pd.setIsLocal(sysEnv, isLocal);
					pd.setLinkPdId(sysEnv, linkPdId);
					pd.setExportName(sysEnv, exportName);
				} catch (NotFoundException nfe) {
					if(processError) return;
					throw nfe;
				}
			}
		}
	}

	public void addOrAlterResource(SystemEnvironment sysEnv, WithHash with, SDMSSchedulingEntity se, boolean isAdd, boolean processError)
		throws SDMSException
	{
		Vector name;
		Long   seId = se.getId(sysEnv);
		Integer amount;
		Integer lockmode;
		String rsmpname;
		Long rsmpId;
		Integer keepMode;
		WithHash sticky;
		String stickyName;
		Long stickyParent;
		Boolean isSticky;
		WithHash expired;
		Integer exp_mult;
		Integer exp_interval;
		Boolean ignoreOnRerun;
		Vector  states;
		String  condition;
		SDMSNamedResource nr;
		SDMSResourceRequirement rr;
		SDMSResourceReqStates rrs;

		name = (Vector) with.get(ParseStr.S_NAME);
		if(name == null)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03203140032", "No resourcename specified"));
		amount = (Integer) with.get(ParseStr.S_AMOUNT);
		lockmode = (Integer) with.get(ParseStr.S_LOCKMODE);
		rsmpname = (String) with.get(ParseStr.S_MAP_STATUS);
		keepMode = (Integer) with.get(ParseStr.S_KEEP);
		sticky = (WithHash) with.get(ParseStr.S_STICKY);
		expired = (WithHash) with.get(ParseStr.S_EXPIRED);
		condition = (String) with.get(ParseStr.S_CONDITION);
		if (condition != null)
			condition = canonizeCondition (sysEnv, condition);

		nr = SDMSNamedResourceTable.getNamedResource(sysEnv, name);
		Long nrId = nr.getId(sysEnv);

		if(amount == null) amount = new Integer(0);
		if(lockmode == null) lockmode = new Integer(SDMSResourceRequirement.N);
		if(rsmpname != null)	rsmpId = SDMSResourceStateMappingProfileTable.idx_name_getUnique(sysEnv, rsmpname).getId(sysEnv);
		else 			rsmpId = null;
		if(keepMode == null) keepMode = new Integer(SDMSResourceRequirement.NOKEEP);
		if(sticky != null) {

			isSticky = Boolean.TRUE;
			stickyName = (String) sticky.get(ParseStr.S_NAME);
			PathVector spv = (PathVector) sticky.get(ParseStr.S_JOB_DEFINITION);
			if (spv != null) {
				String pName = (String) spv.remove(spv.size() -1);
				try {
					SDMSSchedulingEntity spse = SDMSSchedulingEntityTable.get(sysEnv, spv, pName);
					if(!spse.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
						throw new AccessViolationException(new SDMSMessage(sysEnv, "03309241442", "Insufficient privileges"));
					stickyParent = spse.getId(sysEnv);
				} catch(NotFoundException nfe) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03309250931", "The specified sticky parent isn't a job definition"));
				}
			} else
				stickyParent = null;
		} else {
			isSticky = Boolean.FALSE;
			stickyName = null;
			stickyParent = null;
		}

		if(expired != null) {
			exp_mult = (Integer) expired.get(ParseStr.S_MULT);
			if(exp_mult == null) exp_mult = new Integer(1);
			exp_interval = (Integer) expired.get(ParseStr.S_INTERVAL);
			ignoreOnRerun = (Boolean) expired.get(ParseStr.S_IGNORE);
		} else {
			exp_mult = null;
			exp_interval = null;
			ignoreOnRerun = Boolean.FALSE;
		}

		if(isAdd) {
			try {
				rr = SDMSResourceRequirementTable.table.create(sysEnv,
								nrId, seId, amount, keepMode, isSticky, stickyName, stickyParent,
								rsmpId, exp_mult, exp_interval, ignoreOnRerun, lockmode, condition);
			} catch (DuplicateKeyException dke) {
				if(processError) {
					rr = changeResourceRequirement(sysEnv, seId, nrId, amount, keepMode, isSticky, stickyName, stickyParent, rsmpId,
									exp_mult, exp_interval, ignoreOnRerun, lockmode, condition);
				} else {
					throw dke;
				}
			}
		} else {
			try {
				rr = changeResourceRequirement(sysEnv, seId, nrId, amount, keepMode, isSticky, stickyName, stickyParent, rsmpId, exp_mult, exp_interval, ignoreOnRerun, lockmode, condition);
			} catch (NotFoundException nfe) {
				if(processError) return;
				throw nfe;
			}
		}
		states = (Vector) with.get(ParseStr.S_STATUS);
		Vector stateIds = new Vector();
		if(states != null) {
			Long rrId = rr.getId(sysEnv);

			for(int i=0; i < states.size(); i++) {
				String rsdn = (String) states.get(i);
				Long rsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, rsdn).getId(sysEnv);
				stateIds.add(rsdId);
				if (!SDMSResourceReqStatesTable.idx_rr_rsd_pk.containsKey(sysEnv, new SDMSKey(rrId, rsdId)))
					SDMSResourceReqStatesTable.table.create(sysEnv, rrId, rsdId);
			}
		}

		Vector rrstates = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, rr.getId(sysEnv));
		Long rrstateRsdId;

		for (int i = 0; i < rrstates.size(); i++) {
			rrstateRsdId = ((SDMSResourceReqStates)rrstates.get(i)).getRsdId(sysEnv);
			if (!stateIds.contains(rrstateRsdId))
				((SDMSResourceReqStates) rrstates.get(i)).delete(sysEnv);
		}

		rr.check(sysEnv);
	}

	private SDMSResourceRequirement changeResourceRequirement(SystemEnvironment sysEnv, Long seId, Long nrId, Integer amount, Integer keepMode,
			Boolean isSticky, String stickyName, Long stickyParent, Long rsmpId, Integer
	                exp_mult, Integer exp_interval, Boolean ignoreOnRerun, Integer lockmode, String condition)
		throws SDMSException
	{
		SDMSResourceRequirement rr;
		rr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(seId, nrId));
		rr.setAmount(sysEnv, amount);
		rr.setKeepMode(sysEnv, keepMode);
		rr.setIsSticky(sysEnv, isSticky);
		rr.setStickyName(sysEnv, stickyName);
		rr.setStickyParent(sysEnv, stickyParent);
		rr.setRsmpId(sysEnv, rsmpId);
		rr.setExpiredAmount(sysEnv, exp_mult);
		rr.setExpiredBase(sysEnv, exp_interval);
		rr.setIgnoreOnRerun(sysEnv, ignoreOnRerun);
		rr.setLockmode(sysEnv, lockmode);
		rr.setCondition(sysEnv, condition);

		return rr;
	}

	public String canonizeCondition(SystemEnvironment sysEnv, String condition)
		throws SDMSException
	{
		if(condition == null) return null;
		final String newCondition = condition.trim();
		if(newCondition.equals("")) return null;

		final BoolExpr be = new BoolExpr(condition);
		be.checkConditionSyntax(sysEnv);

		return newCondition;
	}

	public void addOrAlterRequirement(SystemEnvironment sysEnv, WithHash wh, SDMSSchedulingEntity seDependent, boolean isAdd, boolean processError)
		throws SDMSException
	{
		SDMSDependencyDefinition dd;
		final WithHash rwh;
		final String rName;
		final Vector rPath;
		final Vector rStateNames;
		final String statesMacro;
		final String rdName;
		Integer unresolved;
		Integer mode;
		Integer stateSelection;
		String condition = null;

		Integer resolveMode = SDMSDependencyDefinition.INTERNAL;
		WithHash expired;
		String selectCondition = null;

		rwh = (WithHash) wh.get(ParseStr.S_FULLNAME);
		if(rwh == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "0220117156", "Missing Required Name"));
		}
		rName = (String) rwh.get(ParseStr.S_NAME);
		rPath = (Vector) rwh.get(ParseStr.S_PATH);
		rdName = (String) wh.get(ParseStr.S_DEPENDENCY);
		condition = canonizeCondition(sysEnv, (String) wh.get(ParseStr.S_CONDITION));

		if (condition != null) sysEnv.checkFeatureAvailability(SystemEnvironment.S_CONDITIONAL_DEPENDENCIES);

		unresolved = (Integer) wh.get(ParseStr.S_UNRESOLVED);
		if (unresolved == null) {
			unresolved = new Integer(SDMSDependencyDefinition.ERROR);
		}

		Object o = wh.get(ParseStr.S_STATUS);
		Vector sns = new Vector();
		String sn;
		stateSelection = null;
		if (o == null || o instanceof Vector) {
			rStateNames = (Vector) o;

			if (rStateNames != null) {
				Iterator i = rStateNames.iterator();
				while (i.hasNext()) {
					sns.add ((String) ((WithItem) i.next()).key);
				}
			}
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

		SDMSSchedulingEntity rSe = SDMSSchedulingEntityTable.get(sysEnv, rPath, rName);
		Long rId = rSe.getId(sysEnv);

		mode = (Integer) wh.get(ParseStr.S_MODE);
		if(mode == null) mode = new Integer(SDMSDependencyDefinition.ALL_FINAL);

		resolveMode = (Integer) wh.get(ParseStr.S_RESOLVE);
		if (resolveMode == null) resolveMode = SDMSDependencyDefinition.INTERNAL;

		Integer expiredAmount = null;
		Integer expiredBase = null;
		expired = (WithHash) wh.get(ParseStr.S_EXPIRED);
		if (expired != null) {
			expiredAmount = (Integer) expired.get(ParseStr.S_MULT);
			if(expiredAmount == null) expiredAmount = new Integer(1);
			expiredBase = (Integer) expired.get(ParseStr.S_INTERVAL);
		}

		selectCondition = canonizeCondition(sysEnv, (String) wh.get(ParseStr.S_SELECT_CONDITION));

		SDMSDependencyState ds;
		if(isAdd) {
			try {
				dd = SDMSDependencyDefinitionTable.table.create(sysEnv,
					seDependent.getId(sysEnv),
					rId,
					rdName,
					unresolved,
					mode,
					stateSelection,
				                condition,
				                resolveMode,
				                expiredAmount,
				                expiredBase,
				                selectCondition
				);
			} catch (DuplicateKeyException dke) {
				if(processError) {
					dd = SDMSDependencyDefinitionTable.idx_DependentId_RequiredId_getUnique(sysEnv,
							new SDMSKey(seDependent.getId(sysEnv), rId));
					dd.setName(sysEnv, rdName);
					dd.setUnresolvedHandling(sysEnv, unresolved);
					dd.setMode(sysEnv, mode);
					dd.setStateSelection(sysEnv, stateSelection);
					dd.setCondition(sysEnv, condition);
					dd.setResolveMode(sysEnv, resolveMode);
					dd.setExpiredAmount(sysEnv, expiredAmount);
					dd.setExpiredBase(sysEnv, expiredBase);
					dd.setSelectCondition(sysEnv, selectCondition);

					Vector v = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, dd.getId(sysEnv));
					for(int i = 0; i < v.size(); i++) {
						ds = (SDMSDependencyState) v.get(i);
						sn = SDMSExitStateDefinitionTable.getObject(sysEnv, ds.getEsdId(sysEnv)).getName(sysEnv);
						if (sns.contains(sn)) continue;
						ds.delete(sysEnv);
					}
				} else {
					throw dke;
				}
			}

		} else {
			try {
				dd = SDMSDependencyDefinitionTable.idx_DependentId_RequiredId_getUnique(sysEnv,
						new SDMSKey(seDependent.getId(sysEnv), rId));
				dd.setName(sysEnv, rdName);
				dd.setUnresolvedHandling(sysEnv, unresolved);
				dd.setMode(sysEnv, mode);
				dd.setStateSelection(sysEnv, stateSelection);
				dd.setCondition(sysEnv, condition);
				dd.setResolveMode(sysEnv, resolveMode);
				dd.setExpiredAmount(sysEnv, expiredAmount);
				dd.setExpiredBase(sysEnv, expiredBase);
				dd.setSelectCondition(sysEnv, selectCondition);

				Vector v = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, dd.getId(sysEnv));
				for(int i = 0; i < v.size(); i++) {
					ds = (SDMSDependencyState) v.get(i);
					sn = SDMSExitStateDefinitionTable.getObject(sysEnv, ds.getEsdId(sysEnv)).getName(sysEnv);
					if (sns.contains(sn)) continue;
					ds.delete(sysEnv);
				}
			} catch (NotFoundException nfe) {
				if(processError) return;
				throw nfe;
			}
		}

		if (rStateNames != null) {
			Long ddId = dd.getId(sysEnv);
			Long rEspId = rSe.getEspId(sysEnv);
			Iterator i = rStateNames.iterator();
			while (i.hasNext()) {
				final WithItem w = (WithItem) i.next();
				final String esdName = (String) w.key;
				condition = canonizeCondition(sysEnv, (String) w.value);
				SDMSExitStateDefinition esd = null;
				try {
					esd = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, esdName);
				} catch (NotFoundException nfe) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "02201171210",
						"Exit State $1 does not exist",	esdName));
				}
				SDMSExitState es = null;
				Long esdId = esd.getId(sysEnv);
				try {
					es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(rEspId, esdId));
				} catch (NotFoundException nfe) {
					SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, rEspId);
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03201292036",
						"Exit State $1 not in Exit State Profile $2 of $3",
						esdName, esp.getName(sysEnv), rSe.pathString(sysEnv)));
				}
				if (!es.getIsFinal(sysEnv).booleanValue()) {
					SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, rEspId);
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03201292037",
						"Exit State $1 in Exit State Profile $2 of $3 must be a final state",
						esdName, esp.getName(sysEnv), rSe.pathString(sysEnv)));
				}
				if (!(SDMSDependencyStateTable.idx_ddId_esdId.containsKey(sysEnv, new SDMSKey (ddId, esdId))))
					SDMSDependencyStateTable.table.create(sysEnv, ddId, esdId, condition);
				else {
					ds = SDMSDependencyStateTable.idx_ddId_esdId_getUnique(sysEnv, new SDMSKey (ddId, esdId));
					ds.setCondition(sysEnv, condition);
				}
			}
		}
	}

	protected void checkParameterRI(SystemEnvironment sysEnv, Long seId)
		throws SDMSException
	{
		final Vector v = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, seId);
		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId);
		for(int i = 0; i < v.size(); i++) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(i);
			if(pd.getType(sysEnv).intValue() == SDMSParameterDefinition.RESOURCEREFERENCE) {
				final SDMSParameterDefinition lpd = SDMSParameterDefinitionTable.getObject(sysEnv, pd.getLinkPdId(sysEnv));
				Long nrId = lpd.getSeId(sysEnv);
				if(!checkResourceRequirement(sysEnv, nrId, se))
					throw new CommonErrorException(
						new SDMSMessage(sysEnv, "03409291229", "Resource $2 for parameter $1 not required",
							pd.getName(sysEnv),
							SDMSNamedResourceTable.getObject(sysEnv, nrId).pathString(sysEnv))
					);
			}
		}
	}

	abstract public void go(SystemEnvironment sysEnv)
		throws SDMSException;

}

