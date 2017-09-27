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

package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSDependencyInstance extends SDMSDependencyInstanceProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSDependencyInstance.java,v 2.11.2.4 2013/05/14 19:21:10 ronald Exp $";

	protected SDMSDependencyInstance(SDMSObject p_object)
	{
		super(p_object);
	}

	private int checkCondition(SystemEnvironment sysEnv, String condition, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		try {
			if(condition != null) {
				final BoolExpr be = new BoolExpr(condition);
				if(be.checkCondition(sysEnv, null, sme, null, null, null))
					return FULFILLED;
				else
					return FAILED;
			} else	return FULFILLED;
		} catch (CommonErrorException cee) {
			sme.setToError(sysEnv, cee.toString());
			return BROKEN;
		}
	}

	public int check(SystemEnvironment sysEnv, HashMap checkCache)
		throws SDMSException
	{
		if (getState(sysEnv).intValue() == SDMSDependencyInstance.DEFERRED)
			return SDMSDependencyInstance.DEFERRED;

		Long idOrig = getDiIdOrig(sysEnv);
		if (checkCache != null && checkCache.containsKey(idOrig)) {
			Integer checkResult = (Integer)(checkCache.get(idOrig));
			setState(sysEnv, checkResult);
			return checkResult.intValue();
		}
		if (!(idOrig.equals(getId(sysEnv)))) {
			SDMSDependencyInstance diOrig = SDMSDependencyInstanceTable.getObject(sysEnv, idOrig);
			int checkResult = diOrig.check(sysEnv, checkCache);
			setState(sysEnv, new Integer(checkResult));
			return checkResult;
		}

		long actVersion = getSeVersion(sysEnv).longValue();

		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, getRequiredId(sysEnv));
		SDMSSubmittedEntity dsme = SDMSSubmittedEntityTable.getObject(sysEnv, getDependentId(sysEnv));
		boolean jobIsFinal = sme.getJobIsFinal(sysEnv).booleanValue();
		int state = sme.getState(sysEnv).intValue();
		Long esdId = null;
		switch (state) {
			case SDMSSubmittedEntity.FINAL:
				esdId = sme.getFinalEsdId(sysEnv);
				break;
			case SDMSSubmittedEntity.FINISHED:
				esdId = sme.getJobEsdId(sysEnv);
				break;
		}
		if ((esdId == null) && (state != SDMSSubmittedEntity.CANCELLED)) {
			return SDMSDependencyInstance.OPEN;
		}

		Long ddId = getDdId(sysEnv);
		SDMSDependencyDefinition dd = SDMSDependencyDefinitionTable.getObject(sysEnv, ddId, actVersion);

		int diState = SDMSDependencyInstance.OPEN;
		switch (state) {
			case SDMSSubmittedEntity.UNREACHABLE:
				break;
			case SDMSSubmittedEntity.CANCELLED:
				diState = SDMSDependencyInstance.CANCELLED;
				break;
			default:
				int mode = dd.getMode(sysEnv).intValue();
				if ((state == SDMSSubmittedEntity.FINISHED && jobIsFinal && mode == SDMSDependencyDefinition.JOB_FINAL) || state == SDMSSubmittedEntity.FINAL) {
					diState = FULFILLED;
					diState = checkCondition(sysEnv, dd.getCondition(sysEnv), dsme);
					if(diState == SDMSDependencyInstance.FULFILLED) {
						int stateSelection = dd.getStateSelection(sysEnv).intValue();
						if (stateSelection == SDMSDependencyDefinition.FINAL) {
							Vector v_ds = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, ddId, actVersion);
							if (v_ds.size() != 0) {
								diState = SDMSDependencyInstance.FAILED;
								Iterator i_ds = v_ds.iterator();
								while (i_ds.hasNext()) {
									SDMSDependencyState ds = (SDMSDependencyState)i_ds.next();
									if (ds.getEsdId(sysEnv).equals(esdId)) {
										diState = FULFILLED;
										diState = checkCondition(sysEnv, ds.getCondition(sysEnv), dsme);
										break;
									}
								}
							} else {
							}
						} else {
							Long seId = sme.getSeId(sysEnv);
							Long espId = SDMSSchedulingEntityTable.getObject(sysEnv, seId, actVersion).getEspId(sysEnv);
							Vector v_es = SDMSExitStateTable.idx_espId.getVector(sysEnv, espId, actVersion);
							diState = SDMSDependencyInstance.FAILED;
							Iterator i_es = v_es.iterator();
							while (i_es.hasNext()) {
								SDMSExitState es = (SDMSExitState)i_es.next();
								if (es.getEsdId(sysEnv).equals(esdId)) {
									if (!(es.getIsFinal(sysEnv).booleanValue())) break;
									if (stateSelection == SDMSDependencyDefinition.ALL_REACHABLE) {
										if (!(es.getIsUnreachable(sysEnv).booleanValue()))
											diState = SDMSDependencyInstance.FULFILLED;
									} else if (stateSelection == SDMSDependencyDefinition.DEFAULT) {
										if (es.getIsDependencyDefault(sysEnv).booleanValue())
											diState = SDMSDependencyInstance.FULFILLED;
									} else if (stateSelection == SDMSDependencyDefinition.UNREACHABLE) {
										if (es.getIsUnreachable(sysEnv).booleanValue())
											diState = SDMSDependencyInstance.FULFILLED;
									}
									break;
								}
							}

						}
					}
				}
				break;
		}
		if (diState != SDMSDependencyInstance.OPEN || dsme.getState(sysEnv).intValue() == SDMSSubmittedEntity.UNREACHABLE) {
			setState(sysEnv, new Integer(diState));
		}

		if (checkCache != null)
			checkCache.put(getId(sysEnv), new Integer(diState));

		return diState;
	}

	public void setIgnore(SystemEnvironment sysEnv, int mode, Long originId, String comment)
		throws SDMSException
	{
		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, getDependentId(sysEnv));

		java.util.Date dts = new java.util.Date();
		Long ts = new Long (dts.getTime());

		if(mode == SDMSDependencyInstance.RECURSIVE) {
			setIgnore(sysEnv, new Integer(mode));
			Long diIdOrig = getDiIdOrig(sysEnv);
			Vector vdi = SDMSDependencyInstanceTable.idx_diIdOrig.getVector(sysEnv, diIdOrig);
			for(int j = 0; j < vdi.size(); j++) {
				SDMSDependencyInstance di = (SDMSDependencyInstance) vdi.get(j);
				SDMSSubmittedEntity dsme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getDependentId(sysEnv));
				if(SDMSHierarchyInstanceTable.idx_parentId_childId.containsKey(sysEnv,
								new SDMSKey(sme.getId(sysEnv), dsme.getId(sysEnv)))) {
					di.setIgnore(sysEnv, SDMSDependencyInstance.RECURSIVE, originId, comment);
				}
			}
		} else {
			int ign = getIgnore(sysEnv).intValue();
			if(ign != SDMSDependencyInstance.RECURSIVE)
				setIgnore(sysEnv, new Integer(mode));
		}
		sme.checkDependencies(sysEnv);
	}
}

