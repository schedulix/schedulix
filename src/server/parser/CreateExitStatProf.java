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

public class CreateExitStatProf extends Node
{

	public final static String __version = "@(#) $Id: CreateExitStatProf.java,v 2.8.2.1 2013/03/14 10:24:25 ronald Exp $";

	private String name;
	private WithHash items;
	private boolean replace;

	public CreateExitStatProf(String n, WithHash w, Boolean r)
	{
		super();
		name = n;
		items = w;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		boolean gotFinalState = false;

		SDMSExitStateProfile esp;
		try {
			esp = SDMSExitStateProfileTable.table.create (sysEnv, name, null, Boolean.TRUE);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterExitStatProf aesp = new AlterExitStatProf(new ObjectURL(new Integer(Parser.EXIT_STATUS_PROFILE), name), items, Boolean.FALSE);
				aesp.setEnv(env);
				aesp.go(sysEnv);
				result = aesp.result;
				return;
			} else {
				throw dke;
			}
		}

		Long espId = esp.getId(sysEnv);
		Long esdId;

		Iterator i;
		Vector states = (Vector) items.get(ParseStr.S_STATUS);
		if(states == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03112171825",
				"States missing"));
		}
		i = states.iterator();
		EspState esps;
		int idx = 1;
		Integer pref;
		boolean had_unreachable = false;
		boolean had_broken = false;
		boolean had_batchDefault = false;
		while (i.hasNext()) {
			pref = new Integer (idx);
			idx ++;
			esps = (EspState)i.next();

			Boolean isFinal = Boolean.FALSE;
			Boolean isRestartable = Boolean.FALSE;
			Boolean isBroken = esps.broken;
			Boolean isUnreachable = esps.unreachable;
			Boolean isBatchDefault = esps.batchDefault;
			Boolean isDependencyDefault = esps.depDefault;

			if (esps.type.equals(new Integer(SDMSExitState.FINAL))) {
				isFinal = Boolean.TRUE;
				gotFinalState = true;
			}
			if (esps.type.equals(new Integer(SDMSExitState.RESTARTABLE))) {
				isRestartable = Boolean.TRUE;
			}
			if (isUnreachable.booleanValue()) {
				if (had_unreachable) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02205311455",
						"Only one state can be marked as unreachable state"));
				}
				had_unreachable = true;
				if (isFinal.booleanValue() != true) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02204301739",
						"The unreachable state must be defined FINAL"));
				}
			}
			if (isBroken.booleanValue()) {
				if (had_broken) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03504300759",
						"Only one state can be marked as broken state"));
				}
				had_broken = true;
			}
			if (isBatchDefault.booleanValue()) {
				if (had_batchDefault) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03202141003",
						"Only one state can be marked as default exit stae for batches and milestones"));
				}
				had_batchDefault = true;
				if (isFinal.booleanValue() != true) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03202141008",
						"The default exit state for batches and milestones must be defined FINAL"));
				}
			}
			if (isDependencyDefault.booleanValue()) {
				if (isFinal.booleanValue() != true) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03202141009",
						"A default exit state for dependencies must be defined FINAL"));
				}
			}
			esdId = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, esps.name).getId(sysEnv);

			try {
				SDMSExitStateTable.table.create (sysEnv, pref, isFinal, isRestartable, isUnreachable, isBroken, isBatchDefault, isDependencyDefault, espId, esdId);
			} catch (DuplicateKeyException dke) {
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03110120948",
					"Exit state definition $1 specified more than once", esps.name));
			}
		}

		if (!gotFinalState) {
			throw new CommonErrorException (new SDMSMessage(sysEnv, "03202242058",
				"An exit state profile requires at least one final state"));
		}

		Long d_esmpId = null;
		String profile = (String) items.get(ParseStr.S_DEFAULT_MAPPING);
		if (profile != null) {
			d_esmpId = SDMSExitStateMappingProfileTable.idx_name_getUnique(sysEnv, profile).getId(sysEnv);

			esp.validateMappingProfile(sysEnv, d_esmpId);

			esp.setDefaultEsmpId (sysEnv, d_esmpId);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03204112201", "Exit State Profile created"));
	}
}

