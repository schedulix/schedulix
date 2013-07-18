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

public class ListFolder extends Node
	implements Formatter
{

	public final static String __version = "@(#) $Id: ListFolder.java,v 2.18.2.2 2013/06/18 09:49:32 ronald Exp $";

	Vector path;
	HashSet expandIds;
	HashMap mseCache = new HashMap();
	WithHash with;

	FolderLister fl;

	public ListFolder(Vector p)
	{
		super();
		path = p;
		expandIds = new HashSet();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListFolder(Vector p, HashSet e)
	{
		super();
		path = p;
		expandIds = e;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListFolder(Vector p, WithHash w)
	{
		super();
		path = p;
		if(w.containsKey(ParseStr.S_EXPAND))
			expandIds = (HashSet) w.get(ParseStr.S_EXPAND);
		else
			expandIds = new HashSet();
		with = w;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public Vector fillHeadInfo()
	{
		Vector desc = new Vector();

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

		desc.add("EXPECTED_FINALTIME");

		desc.add("GET_EXPECTED_RUNTIME");

		desc.add("PRIORITY");

		desc.add("MIN_PRIORITY");

		desc.add("AGING_AMOUNT");

		desc.add("AGING_BASE");

		desc.add("SUBMIT_SUSPENDED");

		desc.add("MASTER_SUBMITTABLE");

		desc.add("SAME_NODE");

		desc.add("GANG_SCHEDULE");

		desc.add("DEPENDENCY_MODE");

		desc.add("ESP_NAME");

		desc.add("ESM_NAME");

		desc.add("ENV_NAME");

		desc.add("FP_NAME");

		desc.add("SUBFOLDERS");

		desc.add("ENTITIES");

		desc.add("HAS_MSE");
		desc.add("PRIVS");

		desc.add("IDPATH");

		desc.add("HIT");

		return desc;
	}

	public Vector fillVector(SystemEnvironment sysEnv, SDMSProxy co, HashSet parentSet)
	throws SDMSException
	{
		Vector v = new Vector();
		if(co instanceof SDMSFolder) {
			fillFVector(sysEnv, (SDMSFolder) co, v);
		} else {
			fillSeVector(sysEnv, (SDMSSchedulingEntity) co, v);
		}
		return v;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSFolder f;
		SDMSSchedulingEntity se;
		fl = new FolderLister(path, expandIds);
		fl.setTitle(new SDMSMessage (sysEnv, "03201292009", "List of Folders"));
		fl.setFormatter(this);

		if(with != null && with.containsKey(ParseStr.S_FILTER)) {
			Vector filter = fl.objFilter.initialize_filter(sysEnv, (Vector) with.get(ParseStr.S_FILTER), 0, true);
			fl.useFilter = true;
		}

		SDMSOutputContainer d_container = fl.list(sysEnv);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03201292008",
		                                   "$1 Object(s) found", new Integer(d_container.lines)));
	}

	private Boolean checkMse(SystemEnvironment sysEnv, SDMSFolder f, HashMap cache)
	throws SDMSException
	{

		Long id = f.getId(sysEnv);
		Boolean hasMse = (Boolean)cache.get(id);
		if (hasMse != null) {

			return hasMse;
		}

		Vector myKey = new Vector();
		myKey.add(id);
		myKey.add(Boolean.TRUE);

		Vector msv = SDMSSchedulingEntityTable.idx_folderId_masterSubmittable.getVector(sysEnv, myKey);
		Iterator i = msv.iterator();
		while (i.hasNext()) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity) i.next();
			if (!se.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
				i.remove();
		}
		if (msv.size() > 0) {

			cache.put(id, Boolean.TRUE);
			return Boolean.TRUE;
		} else {

			Vector v_sf = SDMSFolderTable.idx_parentId.getVector(sysEnv, f.getId(sysEnv));
			Iterator i_sf = v_sf.iterator();
			while (i_sf.hasNext()) {
				SDMSFolder sf = (SDMSFolder)i_sf.next();
				hasMse = checkMse(sysEnv, sf, cache);
				if (hasMse.booleanValue() == true) {
					cache.put(id, Boolean.TRUE);
					return Boolean.TRUE;
				}
			}
			cache.put(id, Boolean.FALSE);
			return Boolean.FALSE;
		}
	}

	private void add_empties(Vector v)
	{
		String empty = "";
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
		v.add(empty);
	}

	private void fillFVector(SystemEnvironment sysEnv, SDMSFolder f, Vector v)
	throws SDMSException
	{
		Long fId = f.getId(sysEnv);
		v.add(fId);
		v.add(f.pathVector(sysEnv));
		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, f.getOwnerId(sysEnv));
		v.add(g.getName(sysEnv));
		v.add("FOLDER");
		add_empties(v);
		Long envId = f.getEnvId(sysEnv);
		if(envId != null) {
			v.add(SDMSNamedEnvironmentTable.getObject(sysEnv, envId).getName(sysEnv));
		} else {
			v.add(null);
		}
		v.add(null);
		Vector v1 = SDMSFolderTable.idx_parentId.getVector(sysEnv, fId);
		Vector v2 = SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, fId);
		v.add(new Integer(v1.size()));
		v.add(new Integer(v2.size()));
		v.add(checkMse(sysEnv, f, mseCache));
		v.add(f.getPrivileges(sysEnv).toString());
		v.add(f.idPathVector(sysEnv));

		if (!fl.useFilter)
			v.add(' ');
		else

			if (fl.objectsToList.contains(f)) {
				if (f.getId(sysEnv).equals(SDMSObject.systemFId)) {
					if (fl.checkValid(sysEnv, f))
						v.add('Y');
					else
						v.add('N');
				} else {
					v.add('Y');
				}
			} else {
				v.add('N');
			}
	}

	private void fillSeVector(SystemEnvironment sysEnv, SDMSSchedulingEntity se, Vector v)
	throws SDMSException
	{
		Long esmpId;
		Long espId;
		Long neId;
		SDMSNamedEnvironment ne;
		SDMSExitStateProfile esp;
		SDMSExitStateMappingProfile esmp;
		Long fpId;
		SDMSFootprint fp;
		Long seId = se.getId(sysEnv);

		v.add(seId);
		v.add(se.pathVector(sysEnv));
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
		v.add(se.getExpectedFinaltime(sysEnv));
		v.add(se.getGetExpectedRuntime(sysEnv));
		v.add(se.getPriority(sysEnv));
		v.add(se.getMinPriority(sysEnv));
		v.add(se.getAgingAmount(sysEnv));
		v.add(se.getAgingBaseAsString(sysEnv));
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
		v.add(new Integer(0));

		Vector v2 = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, seId);
		Integer children = new Integer (v2.size());
		v.add(children);

		v.add("");
		v.add(se.getPrivileges(sysEnv).toString());
		v.add(se.idPathVector(sysEnv));

		if (!fl.useFilter)
			v.add(' ');
		else

			if (fl.objectsToList.contains(se))
				v.add('Y');
			else
				v.add('N');
	}
}

