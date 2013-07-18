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

public class AlterRsm extends Node
{

	public final static String __version = "@(#) $Id: AlterRsm.java,v 2.1.14.1 2013/03/14 10:24:22 ronald Exp $";

	private String name;
	private Vector map;
	private boolean noerr;

	public AlterRsm(String n, Vector v, Boolean ne)
	{
		super();
		name = n;
		map = v;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSResourceStateMappingProfile rsmp;
		SDMSResourceStateMapping rsm;
		Long rsmpId;

		try {
			rsmp = SDMSResourceStateMappingProfileTable.idx_name_getUnique(sysEnv, name);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122345", "No Resource State Mapping altered"));
				return;
			}
			throw nfe;
		}
		rsmpId = rsmp.getId(sysEnv);

		Vector v = SDMSResourceStateMappingTable.idx_rsmpId.getVector(sysEnv, rsmpId);
		for(int i = 0; i < v.size(); i++) {
			rsm = (SDMSResourceStateMapping) v.get(i);
			rsm.delete(sysEnv);
		}

		for(int i = 0; i < map.size(); i++) {
			Vector m = (Vector) map.get(i);
			String esdName = (String) m.get(0);
			String rsdfName = (String) m.get(1);
			String rsdtName = (String) m.get(2);

			Long esdId  = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, esdName).getId(sysEnv);
			Long rsdfId = (rsdfName == null ? null : SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, rsdfName).getId(sysEnv));
			Long rsdtId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, rsdtName).getId(sysEnv);
			SDMSResourceStateMappingTable.table.create(sysEnv, rsmpId, esdId, rsdfId, rsdtId);
		}

		v = SDMSResourceRequirementTable.idx_rsmpId.getVector(sysEnv, rsmpId);
		for(int i = 0; i < v.size(); i++) {
			((SDMSResourceRequirement)v.get(i)).check(sysEnv);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03204021155", "Resource State Mapping altered"));
	}
}

