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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSSchedulingHierarchyTable extends SDMSSchedulingHierarchyTableGeneric
{

	public final static String __version = "SDMSSchedulingHierarchyTable $Revision: 1.4.4.1 $ / @(#) $Id: SDMSSchedulingHierarchyTable.java,v 1.4.4.1 2013/03/14 10:25:24 ronald Exp $";

	public SDMSSchedulingHierarchyTable(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
	}

	public SDMSSchedulingHierarchy create(SystemEnvironment env
	                                      ,Long p_seParentId
	                                      ,Long p_seChildId
	                                      ,String p_aliasName
	                                      ,Boolean p_isStatic
	                                      ,Integer p_priority
	                                      ,Integer p_suspend
	                                      ,String p_resumeAt
	                                      ,Integer p_resumeIn
	                                      ,Integer p_resumeBase
	                                      ,Integer p_mergeMode
	                                      ,Long p_estpId
	                                     )
	throws SDMSException
	{
		SDMSSchedulingHierarchy p = super.create(
		                                    env ,p_seParentId, p_seChildId, p_aliasName,
		                                    p_isStatic, p_priority, p_suspend,
		                                    p_resumeAt, p_resumeIn, p_resumeBase,
		                                    p_mergeMode, p_estpId
		                            );

		checkHierarchyCycles(env, p_seParentId);

		return p;
	}

	public static void checkHierarchyCycles(SystemEnvironment sysEnv, Long seId)
	throws SDMSException
	{
		checkHierarchyCycles(sysEnv, seId, new Vector());
	}

	private static void checkHierarchyCycles(SystemEnvironment sysEnv, Long seId, Vector path)
	throws SDMSException
	{

		int len;
		len = path.size();
		for (int idx = 0; idx < len; idx ++) {
			Long p_seId = (Long)path.get(idx);
			if (p_seId.equals(seId)) {

				String cycleString = p_seId.toString();
				idx ++;

				while (idx < len) {
					p_seId = (Long)path.get(idx);
					cycleString = cycleString + "->" + p_seId.toString();
					idx ++;
				}

				cycleString = cycleString + "->" + seId.toString();

				throw new CommonErrorException(
				        new SDMSMessage(sysEnv, "02209190845", "Hierarchy cycle detected (Child->Parent:$1)",
				                        cycleString));
			}
		}

		path.add(seId);

		Vector v_seParents = SDMSSchedulingHierarchyTable.idx_seChildId.getVector(sysEnv, seId);

		len = v_seParents.size();
		Set<Long> parentsSet = new HashSet();
		for (int idx = 0; idx < len; ++idx) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)v_seParents.get(idx);
			parentsSet.add(sh.getSeParentId(sysEnv));
		}

		Vector v_triggers = SDMSTriggerTable.idx_seId.getVector(sysEnv, seId);

		for (int idx = 0; idx < v_triggers.size(); ++idx) {
			SDMSTrigger t = (SDMSTrigger) v_triggers.get(idx);
			if (t.getIsMaster(sysEnv).booleanValue()) continue;
			if (t.getType(sysEnv).intValue() == SDMSTrigger.AFTER_FINAL) continue;
			if (t.getAction(sysEnv).intValue() == SDMSTrigger.RERUN) continue;
			parentsSet.add(t.getFireId(sysEnv));
		}
		Iterator<Long> i = parentsSet.iterator();
		while (i.hasNext()) {
			checkHierarchyCycles(sysEnv, i.next(), (Vector)path.clone());
		}
	}

}
