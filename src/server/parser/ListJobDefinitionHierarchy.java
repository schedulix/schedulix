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

public class ListJobDefinitionHierarchy extends Node
{

	Vector path;
	boolean expand;
	HashSet expandIds;
	HashMap stateStrings;

	public ListJobDefinitionHierarchy(Vector p)
	{
		super();
		path = p;
		expand = false;
		expandIds = null;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListJobDefinitionHierarchy(Vector p, HashSet e)
	{
		super();
		path = p;
		expand = true;
		expandIds = e;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{

		SDMSSchedulingEntity se;
		Long id;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		SDMSKey key;
		String	states;

		stateStrings = new HashMap ();

		desc.add("ID");
		desc.add("NAME");
		desc.add("OWNER");

		desc.add("TYPE");

		desc.add("RUN_PROGRAM");

		desc.add("RERUN_PROGRAM");

		desc.add("KILL_PROGRAM");

		desc.add("WORKDIR");

		desc.add("LOGFILE");

		desc.add("TRUNC_LOG");

		desc.add("ERRLOGFILE");

		desc.add("TRUNC_ERRLOG");

		desc.add("EXPECTED_RUNTIME");

		desc.add("GET_EXPECTED_RUNTIME");

		desc.add("PRIORITY");

		desc.add("SUBMIT_SUSPENDED");

		desc.add("MASTER_SUBMITTABLE");

		desc.add("SAME_NODE");

		desc.add("GANG_SCHEDULE");

		desc.add("DEPENDENCY_MODE");

		desc.add("ESP_NAME");

		desc.add("ESM_NAME");

		desc.add("ENV_NAME");

		desc.add("FP_NAME");

		desc.add("CHILDREN");

		desc.add("SH_ID");

		desc.add("IS_STATIC");

		desc.add("SH_PRIORITY");

		desc.add("SH_SUSPEND");

		desc.add("SH_ALIAS_NAME");

		desc.add("MERGE_MODE");

		desc.add("EST_NAME");

		desc.add("IGNORED_DEPENDENCIES");

		desc.add("HIERARCHY_PATH");

		desc.add("STATES");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv,
		                                      "03201292007", "Job Definition Hierarchy"), desc);

		String name = (String) path.remove(path.size() -1);

		se = SDMSSchedulingEntityTable.get(sysEnv, path, name);
		if(!se.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402051434", "Insufficient privileges"));

		id = se.getId(sysEnv);
		Vector v = new Vector();
		String sePath = se.pathString(sysEnv);

		v.add(id);
		v.add(sePath);

		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, se.getOwnerId(sysEnv));
		v.add(g.getName(sysEnv));

		v.add(se.getTypeAsString(sysEnv));
		v.add(se.getRunProgram(sysEnv));
		v.add(se.getRerunProgram(sysEnv));
		v.add(se.getKillProgram(sysEnv));
		v.add(se.getWorkdir(sysEnv));
		v.add(se.getLogfile(sysEnv));
		v.add(se.getTruncLog(sysEnv));
		v.add(se.getErrlogfile(sysEnv));
		v.add(se.getTruncErrlog(sysEnv));
		v.add(se.getExpectedRuntime(sysEnv));
		v.add(se.getGetExpectedRuntime(sysEnv));
		v.add(se.getPriority(sysEnv));
		v.add(se.getSubmitSuspended(sysEnv));
		v.add(se.getMasterSubmittable(sysEnv));
		v.add(se.getSameNode(sysEnv));
		v.add(se.getGangSchedule(sysEnv));
		v.add(se.getDependencyOperationAsString(sysEnv));

		Long espId = se.getEspId(sysEnv);
		SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, espId);
		v.add(esp.getName(sysEnv));

		Long esmpId = se.getEsmpId(sysEnv);
		if (esmpId != null) {
			SDMSExitStateMappingProfile esmp = SDMSExitStateMappingProfileTable.getObject(sysEnv, esmpId);
			v.add(esmp.getName(sysEnv));
		} else {
			v.add(SystemEnvironment.defaultString);
		}
		Long neId = se.getNeId(sysEnv);
		if (neId != null) {
			SDMSNamedEnvironment ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId);
			v.add(ne.getName(sysEnv));
		} else {
			v.add(SystemEnvironment.nullString);
		}
		Long fpId = se.getFpId(sysEnv);
		if (fpId != null) {
			SDMSFootprint fp = SDMSFootprintTable.getObject(sysEnv, fpId);
			v.add(fp.getName(sysEnv));
		} else {
			v.add(SystemEnvironment.nullString);
		}

		Vector v1 = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, id);
		v.add(new Integer(v1.size()));

		String empty = "";
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);

		v.add(sePath);

		Integer seType = se.getType(sysEnv);
		key = new SDMSKey (seType, espId, esmpId, null);
		states = (String)stateStrings.get(key);
		if (states == null) {
			states = getStateString(sysEnv, seType.intValue(), espId, esmpId, null);
			stateStrings.put(key, states);
		}
		v.add(states);
		v.add(se.getPrivileges(sysEnv).toString());

		d_container.addData(sysEnv, v);
		add_childs(sysEnv, d_container, v1, sePath);

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 33));

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "02204260958",
		                                   "$1 Object(s) found", new Integer(d_container.lines)));
	}

	private void add_childs(SystemEnvironment sysEnv, SDMSOutputContainer oc, Vector vc, String hPath)
	throws SDMSException
	{
		SDMSExitStateProfile esp;
		SDMSExitStateMappingProfile esmp;

		Long esmpId;
		Long espId;
		Long neId;
		SDMSNamedEnvironment ne;
		Long fpId;
		SDMSFootprint fp;
		SDMSKey key;
		String	states;

		Iterator i_c = vc.iterator();
		while (i_c.hasNext()) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)i_c.next();
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeChildId(sysEnv));
			String sePath = se.pathString(sysEnv);

			Vector v = new Vector();
			Long seId = se.getId(sysEnv);
			Long shId = sh.getId(sysEnv);
			v.add(seId);
			v.add(se.pathString(sysEnv));
			SDMSGroup g = SDMSGroupTable.getObject(sysEnv, se.getOwnerId(sysEnv));
			v.add(g.getName(sysEnv));
			String typeString = se.getTypeAsString(sysEnv);
			v.add(typeString);
			v.add(se.getRunProgram(sysEnv));
			v.add(se.getRerunProgram(sysEnv));
			v.add(se.getKillProgram(sysEnv));
			v.add(se.getWorkdir(sysEnv));
			v.add(se.getLogfile(sysEnv));
			v.add(se.getTruncLog(sysEnv));
			v.add(se.getErrlogfile(sysEnv));
			v.add(se.getTruncErrlog(sysEnv));
			v.add(se.getExpectedRuntime(sysEnv));
			v.add(se.getGetExpectedRuntime(sysEnv));
			v.add(se.getPriority(sysEnv));
			v.add(se.getSubmitSuspended(sysEnv));
			v.add(se.getMasterSubmittable(sysEnv));
			v.add(se.getSameNode(sysEnv));
			v.add(se.getGangSchedule(sysEnv));
			v.add(se.getDependencyOperationAsString(sysEnv));
			espId = se.getEspId(sysEnv);
			esp = SDMSExitStateProfileTable.getObject(sysEnv, espId);
			v.add(esp.getName(sysEnv));
			esmpId = se.getEsmpId(sysEnv);
			if (esmpId != null) {
				esmp = SDMSExitStateMappingProfileTable.getObject(sysEnv, esmpId);
				v.add(esmp.getName(sysEnv));
			} else {
				v.add(SystemEnvironment.defaultString);
			}
			neId = se.getNeId(sysEnv);
			if (neId != null) {
				ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId);
				v.add(ne.getName(sysEnv));
			} else {
				v.add(SystemEnvironment.nullString);
			}
			fpId = se.getFpId(sysEnv);
			if (fpId != null) {
				fp = SDMSFootprintTable.getObject(sysEnv, fpId);
				v.add(fp.getName(sysEnv));
			} else {
				v.add(SystemEnvironment.nullString);
			}
			Vector v1 = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, seId);
			v.add(new Integer(v1.size()));
			v.add(shId);
			v.add(sh.getIsStatic(sysEnv));
			v.add(sh.getPriority(sysEnv));
			v.add(sh.getSuspendAsString(sysEnv));
			v.add(sh.getAliasName(sysEnv));
			v.add(sh.getMergeModeAsString(sysEnv));

			Long estpId = sh.getEstpId(sysEnv);
			v.add(SystemEnvironment.nullString);

			Vector ids_v = SDMSIgnoredDependencyTable.idx_shId.getVector(sysEnv, shId);
			String sep = "";
			StringBuffer deps = new StringBuffer();
			for(int j = 0; j < ids_v.size(); j++) {
				SDMSIgnoredDependency ids = (SDMSIgnoredDependency) ids_v.get(j);
				deps.append(sep);
				deps.append(ids.getDdName(sysEnv));
				sep = ",";
			}
			v.add(new String(deps));

			String shPath = hPath + ':' + sePath;
			v.add(shPath);

			Integer seType = se.getType(sysEnv);
			key = new SDMSKey (seType, espId, esmpId, estpId);
			states = (String)stateStrings.get(key);
			if (states == null) {
				states = getStateString(sysEnv, seType.intValue(), espId, esmpId, estpId);
				stateStrings.put(key, states);
			}

			v.add(states);
			SDMSPrivilege p = se.getPrivileges(sysEnv);
			v.add(p.toString());

			oc.addData(sysEnv, v);

			if(expand && p.can(SDMSPrivilege.VIEW)) {
				if(expandIds == null || expandIds.contains(shId)) {
					add_childs(sysEnv, oc, v1, shPath);
				}
			}
		}
	}

	private String getStateString(SystemEnvironment sysEnv, int p_seType, Long p_espId, Long p_esmpId, Long p_estpId)
	throws SDMSException
	{
		Long esmpId = p_esmpId;
		HashSet mappedExitStates = new HashSet();
		if (p_seType == SDMSSchedulingEntity.JOB) {

			if (esmpId == null) {
				SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, p_espId);
				esmpId = esp.getDefaultEsmpId(sysEnv);
			}

			Vector v_esm = SDMSExitStateMappingTable.idx_esmpId.getVector(sysEnv, esmpId);
			Iterator i_esm = v_esm.iterator();
			while (i_esm.hasNext()) {
				SDMSExitStateMapping esm = (SDMSExitStateMapping)i_esm.next();
				mappedExitStates.add (esm.getEsdId(sysEnv));
			}
		}

		Vector v_es = SDMSExitStateTable.idx_espId.getVector(sysEnv, p_espId);
		Vector v_resultStates = new Vector();
		String unreachableState = null;
		Iterator i_es = v_es.iterator();
		while (i_es.hasNext()) {
			SDMSExitState es = (SDMSExitState)i_es.next();
			SDMSExitStateDefinition esd;
			SDMSExitStateDefinition pesd;
			Long esdId = es.getEsdId(sysEnv);
			Long pesdId;
			String state = null;
			String pstate = null;
			if (es.getIsFinal(sysEnv).booleanValue()) {
				esd = SDMSExitStateDefinitionTable.getObject(sysEnv, esdId);
				state = esd.getName(sysEnv);
				if (!mappedExitStates.contains(esdId)) {
					state = "(" + state + ")";
				}
				Vector v = new Vector();
				v.add(state);
				v.add(es.getPreference(sysEnv));
				v_resultStates.add(v);
				if (es.getIsUnreachable(sysEnv).booleanValue()) {
					unreachableState = state;
				}
			}
		}

		Collections.sort (v_resultStates,
		new Comparator () {
			public int compare (Object o1, Object o2) {
				Vector v1 = (Vector)o1;
				Vector v2 = (Vector)o2;
				int p1 = ((Integer)v1.get(1)).intValue();
				int p2 = ((Integer)v2.get(1)).intValue();
				if (p1 != p2)
					if (p1 < p2)
						return -1;
					else
						return 1;
				else
					return 0;
			}
			public boolean equals (Object o1, Object o2) {
				Vector v1 = (Vector)o1;
				Vector v2 = (Vector)o2;
				int p1 = ((Integer)v1.get(1)).intValue();
				int p2 = ((Integer)v2.get(1)).intValue();
				if (p1 == p2)
					return true;
				else
					return false;
			}
		}
		                 );

		String resultString = "";
		String seperator = "";
		Iterator i_resultStates = v_resultStates.iterator();
		while (i_resultStates.hasNext()) {
			Vector v = (Vector)i_resultStates.next();
			resultString = resultString + seperator + (String)(v.get(0));
			seperator = ",";
		}
		if (unreachableState != null)
			resultString = resultString + ":" + unreachableState;

		return resultString;
	}
}
