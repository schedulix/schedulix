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

public class AlterExitStateMapping extends Node
{

	public final static String __version = "@(#) $Id: AlterExitStateMapping.java,v 2.1.14.1 2013/03/14 10:24:20 ronald Exp $";

	private ObjectURL url;
	private Vector maps;
	private boolean noerr;

	public AlterExitStateMapping(ObjectURL u, Vector m, Boolean ne)
	{
		super();
		url = u;
		maps = m;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSExitStateMappingProfile esmp;
		try {
			esmp = (SDMSExitStateMappingProfile) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122355", "No Exit State Mapping altered"));
				return;
			}
			throw nfe;
		}

		Vector esm_v = SDMSExitStateMappingTable.idx_esmpId.getVector(sysEnv, esmp.getId(sysEnv));
		SDMSExitStateMapping esm;
		Iterator i = esm_v.iterator();
		while (i.hasNext()) {
			esm = (SDMSExitStateMapping)(i.next());
			esm.delete(sysEnv);
		}

		SDMSExitStateDefinition esd = null;

		Long esmpId = esmp.getId(sysEnv);
		int exitCode = 0;

		EsmMap esmMap;

		int idx;
		idx = 0;

		i = maps.iterator();
		while(i.hasNext()) {
			esmMap = (EsmMap)(i.next());
			if (idx != 0) {
				if (exitCode >= esmMap.from().intValue()) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02110260801",
					                                "Exit Code $1 out of sequence", esmMap.from()));
				}
				esm = SDMSExitStateMappingTable.table.create (
				              sysEnv, esmpId, esd.getId(sysEnv),
				              new Integer(exitCode), new Integer (esmMap.from().intValue() - 1));
			}
			idx = idx + 1;
			exitCode = esmMap.from().intValue();
			esd = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, esmMap.name());
		}
		esm = SDMSExitStateMappingTable.table.create (sysEnv,
		                esmpId, esd.getId(sysEnv), new Integer(exitCode), new Integer(Integer.MAX_VALUE));

		SDMSExitStateProfile esp;
		Vector v = SDMSExitStateProfileTable.idx_defaultEsmpId.getVector(sysEnv, esmpId);
		i = v.iterator();
		while (i.hasNext()) {
			esp = (SDMSExitStateProfile)(i.next());
			esp.validateMappingProfile(sysEnv, esmpId);
			try {
				esp.validateMappingProfile(sysEnv, esmpId);
			} catch (CommonErrorException cee) {
				throw new CommonErrorException (new SDMSMessage(sysEnv, "02112202121",
				                                "Default Exit State Mapping not compatible to Exit State Profile $1",
				                                esp.getName(sysEnv)
				                                               ));
			}
		}

		Vector se_v = SDMSSchedulingEntityTable.idx_esmpId.getVector(sysEnv, esmpId);
		SDMSSchedulingEntity se;
		Long se_espId;
		i = se_v.iterator();
		while (i.hasNext()) {
			se = (SDMSSchedulingEntity)i.next();
			se_espId = se.getEspId(sysEnv);
			esp = SDMSExitStateProfileTable.getObject(sysEnv, se_espId);
			try {
				esp.validateMappingProfile(sysEnv, esmpId);
			} catch (CommonErrorException cee) {
				throw new CommonErrorException (new SDMSMessage(sysEnv, "02112202120",
				                                "Exit State Mapping not compatible to Exit State Profile $1 of Job $2",
				                                esp.getName(sysEnv),
				                                se.getName(sysEnv)
				                                               ));
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03204112157", "Exit State Mapping altered"));

	}
}

