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

public class CreateExitStatusMapping extends Node
{

	public final static String __version = "@(#) $Id: CreateExitStatusMapping.java,v 2.1.14.1 2013/03/14 10:24:25 ronald Exp $";

	private String name;
	private Vector maps;
	private boolean replace;

	public CreateExitStatusMapping(String n, Vector m, Boolean r)
	{
		super();
		name = n;
		maps = m;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateMappingProfile esmp;
		try {
			esmp = SDMSExitStateMappingProfileTable.table.create (sysEnv, name);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterExitStateMapping aesm = new AlterExitStateMapping(new ObjectURL(SDMSConstants.PS_EXIT_STATUS_MAPPING, name), maps, Boolean.FALSE);
				aesm.setEnv(env);
				aesm.go(sysEnv);
				result = aesm.result;
				return;
			} else {
				throw dke;
			}
		}

		SDMSExitStateDefinition esd = null;

		Long esmpId = esmp.getId(sysEnv);
		int exitCode = 0;

		EsmMap esmMap;

		int idx;
		idx = 0;

		Iterator i = maps.iterator();
		while(i.hasNext()) {
			esmMap = (EsmMap)(i.next());
			if (idx != 0) {
				if (exitCode >= esmMap.from().intValue()) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02110222023",
						"Exit Code $1 out of sequence", esmMap.from()));
				}
				SDMSExitStateMappingTable.table.create (
						sysEnv, esmpId, esd.getId(sysEnv),
				        	Integer.valueOf(exitCode), Integer.valueOf (esmMap.from().intValue() - 1));
			}
			idx = idx + 1;
			exitCode = esmMap.from().intValue();
			esd = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, esmMap.name());
		}
		SDMSExitStateMappingTable.table.create (sysEnv, esmpId, esd.getId(sysEnv),
		                                        Integer.valueOf(exitCode), SDMSConstants.iMAX_VALUE);

		result.setFeedback(new SDMSMessage(sysEnv, "03201161956", "Exit State Mapping created"));

	}
}

