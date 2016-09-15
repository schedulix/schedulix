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

public class AlterExitStateTranslation extends Node
{

	public final static String __version = "@(#) $Id: AlterExitStateTranslation.java,v 2.5.2.4 2013/03/20 06:42:57 ronald Exp $";

	private ObjectURL url;
	private Vector trans;
	private boolean noerr;

	public AlterExitStateTranslation (ObjectURL u, Vector p_trans, Boolean ne)
	{
		super();
		url = u;
		trans = p_trans;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXIT_STATE_TRANSLATION);
		SDMSExitStateTranslationProfile estp;
		try {
			estp = (SDMSExitStateTranslationProfile) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130023", "No Exit State Translation altered"));
				return;
			}
			throw nfe;
		}
		Long estpId = estp.getId(sysEnv);

		Vector est_v = SDMSExitStateTranslationTable.idx_estpId.getVector(sysEnv, estpId);
		SDMSExitStateTranslation est;
		Iterator i = est_v.iterator();
		while (i.hasNext()) {
			est = (SDMSExitStateTranslation)(i.next());
			est.delete(sysEnv);
		}

		Long esdIdFrom;
		Long esdIdTo;
		i = trans.iterator();
		StatusTranslation st;
		while (i.hasNext()) {
			st = (StatusTranslation)i.next();
			esdIdFrom = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, st.sfrom).getId(sysEnv);
			esdIdTo = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, st.sto).getId(sysEnv);
			try {
				SDMSExitStateTranslationTable.table.create (sysEnv, estpId, esdIdFrom, esdIdTo);
			} catch (DuplicateKeyException dke) {
				throw new CommonErrorException(new SDMSMessage (sysEnv, "03110101310",
				                               "Exit State $1 is translated twice", st.sfrom));
			}
		}

		Vector se_v = SDMSSchedulingHierarchyTable.idx_estpId.getVector(sysEnv, estpId);
		i = se_v.iterator();
		SDMSSchedulingHierarchy sh;
		Long seIdChild;
		Long seIdParent;
		Long espIdChild;
		Long espIdParent;
		SDMSSchedulingEntity seChild;
		SDMSSchedulingEntity seParent;
		while (i.hasNext()) {
			sh = (SDMSSchedulingHierarchy)i.next();
			seIdChild  = sh.getSeChildId(sysEnv);
			seChild = SDMSSchedulingEntityTable.getObject(sysEnv, seIdChild);
			espIdChild = seChild.getEspId(sysEnv);
			seIdParent = sh.getSeParentId(sysEnv);
			seParent = SDMSSchedulingEntityTable.getObject(sysEnv, seIdParent);
			espIdParent = seParent.getEspId(sysEnv);
			Vector v_childEsd = SDMSExitStateTable.idx_espId.getVector(sysEnv, espIdChild);
			Iterator icesd = v_childEsd.iterator();
			Long esdIdChild;
			SDMSKey k;
			while (icesd.hasNext()) {
				Long esdIdChildOrig = ((SDMSExitState)(icesd.next())).getEsdId(sysEnv);
				esdIdChild = estp.translate(sysEnv, esdIdChildOrig, false);
				if (esdIdChild == null) continue;
				k = new SDMSKey (espIdParent, esdIdChild);
				if (!SDMSExitStateTable.idx_espId_esdId.containsKey(sysEnv, k)) {
					Object[] p = new Object[5];
					p[0] = SDMSExitStateDefinitionTable.getObject(sysEnv, esdIdChild).getName(sysEnv);
					p[1] = SDMSExitStateProfileTable.getObject(sysEnv, espIdChild).getName(sysEnv);
					p[2] = seChild.pathString(sysEnv);
					p[3] = seParent.pathString(sysEnv);
					p[4] = SDMSExitStateDefinitionTable.getObject(sysEnv, esdIdChildOrig).getName(sysEnv);
					throw new CommonErrorException(new SDMSMessage (sysEnv, "02112172122",
					                               "Parent Profile $2 of $4 does not contain translated State $1 <- $5 of $3", p));
				}
			}
		}
		result.setFeedback(new SDMSMessage(sysEnv, "03204112158", "Exit State Translation altered"));
	}
}
