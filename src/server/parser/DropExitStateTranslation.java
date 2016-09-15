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

public class DropExitStateTranslation extends Node
{

	public final static String __version = "@(#) $Id: DropExitStateTranslation.java,v 2.2.14.3 2013/03/20 06:42:58 ronald Exp $";

	private ObjectURL url;
	private boolean noerr;

	public DropExitStateTranslation(ObjectURL u, Boolean ne)
	{
		super();
		url = u;
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
				result.setFeedback(new SDMSMessage(sysEnv, "03311130025", "No Exit State Translation dropped"));
				return;
			}
			throw nfe;
		}
		Long estpId = estp.getId(sysEnv);

		Vector sh_v = SDMSSchedulingHierarchyTable.idx_estpId.getVector(sysEnv, estpId);
		if (sh_v.size() != 0) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)(sh_v.elementAt(0));
			SDMSSchedulingEntity se_p = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeParentId (sysEnv));
			SDMSSchedulingEntity se_c = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeChildId (sysEnv));
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02112171840",
			                                "Exit State Translation in use between parent $1 and child $2",
			                                se_p.pathString(sysEnv),
			                                se_c.pathString(sysEnv)
			                                                ));
		}

		Vector est_v = SDMSExitStateTranslationTable.idx_estpId.getVector(sysEnv, estpId);
		SDMSExitStateTranslation est;
		Iterator i = est_v.iterator();
		while (i.hasNext()) {
			est = (SDMSExitStateTranslation)(i.next());
			est.delete(sysEnv);
		}
		estp.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03204112215", "Exit State Translation dropped"));
	}
}

