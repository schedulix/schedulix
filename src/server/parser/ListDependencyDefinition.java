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

public class ListDependencyDefinition extends Node
{

	public final static String __version = "@(#) $Id: ListDependencyDefinition.java,v 2.9.2.2 2013/06/18 09:49:32 ronald Exp $";

	Vector path;

	public ListDependencyDefinition (Vector p)
	{
		super();
		path = p;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{

		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");

		desc.add("SE_DEPENDENT_PATH");

		desc.add("DEPENDENT_NAME");

		desc.add("SE_REQUIRED_PATH");

		desc.add("REQUIRED_NAME");
		desc.add("NAME");

		desc.add("UNRESOLVED_HANDLING");

		desc.add("MODE");

		desc.add("STATE_SELECTION");

		desc.add("ALL_FINALS");

		desc.add("CONDITION");

		desc.add("STATES");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv,
			"02204291436", "Dependency Definitions"), desc);

		String name = (String) path.remove(path.size() -1);

		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.get(sysEnv, path, name);

		HashMap resolveMap = new HashMap();
		fill_resolveMap (sysEnv, se, new Vector(), "", resolveMap, false);

		resolveChildren (sysEnv, se, new Vector(), "", resolveMap, d_container, false);

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "02204291509", "$1 Dependency Definition(s) found",
					new Integer(d_container.lines)));

	}

	private void fill_resolveMap(SystemEnvironment sysEnv,
			SDMSSchedulingEntity se, Vector p_v_Path, String p_s_Path, HashMap resolveMap, boolean disabled)
		throws SDMSException
	{
		String s_Path;
		Vector v_Path;

		Long id = se.getId(sysEnv);
		String path = se.pathString(sysEnv);

		if (p_s_Path != "") {
			s_Path = p_s_Path + ":" + path;
		} else {
			s_Path = path;
		}
		v_Path = new Vector(p_v_Path);
		v_Path.add(path);

		if (v_Path.size() == 1) {
			Vector rootVector = new Vector();
			rootVector.add(path);
			resolveMap.put(path, rootVector);
		}
		int i;
		String ps = "";
		String sep = "";
		for (i = 0; i < v_Path.size() - 1; i ++) {
			Vector v;
			ps = ps + sep + v_Path.get(i);
			sep = ":";
			String p = ps + ":" + path;
			if (resolveMap.containsKey(p)) {
				v = (Vector) resolveMap.get (p);
				v.add(s_Path);
				resolveMap.put (p, v);
			} else {
				v = new Vector();
				v.add(s_Path);
				resolveMap.put (p, v);
			}
		}

		if (disabled)
			return;

		Vector v = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, id);
		Iterator iv = v.iterator();
		while (iv.hasNext()) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)iv.next();
			SDMSSchedulingEntity cse = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeChildId(sysEnv));
			fill_resolveMap (sysEnv, cse, v_Path, s_Path, resolveMap, sh.getIsDisabled(sysEnv).booleanValue());
		}
	}

	private Vector lookupRequired(HashMap resolveMap, Vector p_v_Path, String path)
	{
		int i, j;
		int s = p_v_Path.size();
		for (i = s; i > 0; i --) {
			String k = (String)(p_v_Path.get(0));
			for (j = 1; j < i; j ++) {
				k = k + ":" + (String)(p_v_Path.get(j));
			}
			k = k + ":" + path;
			Vector v = (Vector)(resolveMap.get(k));
			if ( v != null) {
				return v;
			}

			v = (Vector)(resolveMap.get(path));
			if ( v != null) {
				return v;
			}
		}
		return new Vector();
	}

	private String getStatesString(SystemEnvironment sysEnv, SDMSDependencyDefinition dd)
		throws SDMSException
	{
		StringBuffer s = new StringBuffer();
		Vector v_ds = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, dd.getId(sysEnv));
		Iterator i_ds = v_ds.iterator();
		String sep = "";
		while (i_ds.hasNext()) {
			SDMSDependencyState ds = (SDMSDependencyState)i_ds.next();
			SDMSExitStateDefinition esd = SDMSExitStateDefinitionTable.getObject(sysEnv, ds.getEsdId(sysEnv));
			s.append(sep);
			s.append(esd.getName(sysEnv));
			final String condition = ds.getCondition(sysEnv);
			if(condition != null) {
				s.append("*");
			}
			sep = ",";
		}
		return s.toString();
	}

	private void resolveChildren(SystemEnvironment sysEnv,
			SDMSSchedulingEntity se, Vector p_v_Path, String p_s_Path, HashMap resolveMap,
			SDMSOutputContainer d_container, boolean disabled)
		throws SDMSException
	{
		String s_Path;
		Vector v_Path;

		String path = se.pathString(sysEnv);

		if (p_s_Path != "") {
			s_Path = p_s_Path + ":" + path;
		} else {
			s_Path = path;
		}
		v_Path = new Vector(p_v_Path);
		v_Path.add(path);

		Long id = se.getId(sysEnv);
		Vector v_dd = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, id);
		Iterator i = v_dd.iterator();
		while (i.hasNext()) {
			SDMSDependencyDefinition dd = (SDMSDependencyDefinition)i.next();
			String statesString = getStatesString (sysEnv, dd);
			Long dd_req_id = dd.getSeRequiredId(sysEnv);
			SDMSSchedulingEntity rse = SDMSSchedulingEntityTable.getObject(sysEnv, dd_req_id);
			String rpath = rse.pathString(sysEnv);

			Vector vr = lookupRequired (resolveMap, p_v_Path, rpath);

			for(int j = 0; j < vr.size(); j++) {
				Vector v = new Vector();
				v.add(dd.getId(sysEnv));
				v.add(s_Path);
				v.add(path);
				v.add(vr.get(j));
				v.add(rpath);
				v.add(dd.getName(sysEnv));
				v.add(dd.getUnresolvedHandlingAsString(sysEnv));
				v.add(dd.getModeAsString(sysEnv));
				v.add(dd.getStateSelectionAsString(sysEnv));

				if (SDMSDependencyStateTable.idx_ddId.containsKey(sysEnv, dd.getId(sysEnv))) {
					v.add(Boolean.FALSE);
				} else {
					v.add(Boolean.TRUE);
				}
				v.add(dd.getCondition(sysEnv));
				v.add(statesString);
				d_container.addData(sysEnv, v);
			}
			if(vr.size() == 0) {
				Vector v = new Vector();
				v.add(dd.getId(sysEnv));
				v.add(s_Path);
				v.add(path);
				v.add(null);
				v.add(rpath);
				v.add(dd.getName(sysEnv));
				v.add(dd.getUnresolvedHandlingAsString(sysEnv));
				v.add(dd.getModeAsString(sysEnv));
				v.add(dd.getStateSelectionAsString(sysEnv));

				if (SDMSDependencyStateTable.idx_ddId.containsKey(sysEnv, dd.getId(sysEnv))) {
					v.add(Boolean.FALSE);
				} else {
					v.add(Boolean.TRUE);
				}
				v.add(dd.getCondition(sysEnv));
				v.add(statesString);
				d_container.addData(sysEnv, v);
			}
		}

		if (disabled)
			return;

		Vector v_sh = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, id);
		i = v_sh.iterator();
		while (i.hasNext()) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)i.next();
			SDMSSchedulingEntity cse = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeChildId(sysEnv));
			resolveChildren (sysEnv, cse, v_Path, s_Path, resolveMap, d_container, sh.getIsDisabled(sysEnv).booleanValue());
		}
	}
}
