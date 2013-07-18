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

public class CreateRsm extends Node
{

	public final static String __version = "@(#) $Id: CreateRsm.java,v 2.1.18.1 2013/03/14 10:24:27 ronald Exp $";

	private String name;
	private Vector map;
	private boolean replace;

	public CreateRsm(String n, Vector v, Boolean r)
	{
		super();
		name = n;
		map = v;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSResourceStateMappingProfile rsmp;
		Long rsmpId;

		try {
			rsmp = SDMSResourceStateMappingProfileTable.table.create(sysEnv, name);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterRsm arsm = new AlterRsm(name, map, Boolean.FALSE);
				arsm.setEnv(env);
				arsm.go(sysEnv);
				result = arsm.result;
				return;
			} else {
				throw dke;
			}
		}

		rsmpId = rsmp.getId(sysEnv);
		for(int i=0; i < map.size(); i++) {
			Vector m = (Vector) map.get(i);
			String esdName = (String) m.get(0);
			String rsdfName = (String) m.get(1);
			String rsdtName = (String) m.get(2);

			Long esdId  = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, esdName).getId(sysEnv);
			Long rsdfId = (rsdfName == null ? null : SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, rsdfName).getId(sysEnv));
			Long rsdtId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, rsdtName).getId(sysEnv);
			SDMSResourceStateMappingTable.table.create(sysEnv, rsmpId, esdId, rsdfId, rsdtId);
		}
		result.setFeedback(new SDMSMessage(sysEnv, "03203301646", "Resource State Mapping created"));
	}
}

