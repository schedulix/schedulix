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

public class AlterExitStatProf extends Node
{

	public final static String __version = "@(#) $Id: AlterExitStatProf.java,v 2.8.2.1 2013/03/14 10:24:20 ronald Exp $";

	private ObjectURL url;
	private WithHash items;
	private boolean noerr;

	public AlterExitStatProf(ObjectURL u, WithHash w, Boolean ne)
	{
		super();
		url = u;
		items = w;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateProfile esp;
		try {
			esp = (SDMSExitStateProfile) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130018", "No Exit State Profile altered"));
				return;
			}
			throw nfe;
		}
		Long espId = esp.getId(sysEnv);

		if (items.containsKey(ParseStr.S_STATUS)) {
			removeExitStates(sysEnv, espId, SDMSExitStateTable.idx_espId.getVector(sysEnv, espId));
			insertExitStates(sysEnv, espId, (Vector)items.get(ParseStr.S_STATUS));
			checkForFinalState(sysEnv, espId);
		}

		Long d_esmpId = null;
		if (items.containsKey(ParseStr.S_DEFAULT_MAPPING)) {
			String profile = (String)items.get(ParseStr.S_DEFAULT_MAPPING);
			if (profile != null) {
				d_esmpId = SDMSExitStateMappingProfileTable.idx_name_getUnique(sysEnv, profile).getId(sysEnv);
			}
			esp.setDefaultEsmpId(sysEnv, d_esmpId);
		} else {
			d_esmpId = esp.getDefaultEsmpId(sysEnv);
		}

		if (items.containsKey(ParseStr.S_FORCE) && ((Boolean)(items.get(ParseStr.S_FORCE))).booleanValue() == true) {
			esp.setIsValid(sysEnv, Boolean.FALSE);
		} else {
			esp.checkProfile(sysEnv);
		}
		result.setFeedback(new SDMSMessage(sysEnv, "03204112156", "Exit State Profile altered"));
	}

	private void removeExitStates(
		SystemEnvironment sysEnv,
		Long espId,
		Vector states
	)
		throws SDMSException
	{
		Iterator i = states.iterator();
		SDMSExitState es;
		while (i.hasNext()) {
			es = (SDMSExitState)(i.next());
			es.delete(sysEnv);
		}
	}

	private void insertExitStates(
		SystemEnvironment sysEnv,
		Long espId,
		Vector espStates
	)
		throws SDMSException
	{
		int idx = 1;
		Integer pref;
		Boolean isFinal;
		Boolean isRestartable;
		SDMSExitStateDefinition esd;
		Long esdId;
		boolean had_unreachable = false;
		boolean had_disabled = false;
		boolean had_broken = false;
		boolean had_batchDefault = false;

		Iterator i = espStates.iterator();
		EspState espState;
		while (i.hasNext()) {
			espState = (EspState)i.next();
			if (espState.type.equals(SDMSConstants.ES_FINAL))
				isFinal = Boolean.TRUE;
			else isFinal = Boolean.FALSE;
			if (espState.type.equals(SDMSConstants.ES_RESTARTABLE))
				isRestartable = Boolean.TRUE;
			else isRestartable = Boolean.FALSE;

			Boolean isUnreachable = espState.unreachable;
			Boolean isDisabled = espState.disabled;
			Boolean isBroken = espState.broken;
			Boolean isBatchDefault = espState.batchDefault;
			Boolean isDependencyDefault = espState.depDefault;

			if (isUnreachable.booleanValue()) {
				if (had_unreachable) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02205311457",
						"Only one state can be marked as unreachable state"));
				}
				had_unreachable = true;
				if (isFinal.booleanValue() != true) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02204301736",
						"The unreachable state must be defined FINAL"));
				}
			}
			if (isDisabled.booleanValue()) {
				if (had_disabled) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02011110832",
					                                "Only one state can be marked as disabled state"));
				}
				had_disabled = true;
				if (isFinal.booleanValue() != true) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "02011110833",
					                                "The disabled state must be defined FINAL"));
				}
			}
			if (isBroken.booleanValue()) {
				if (had_broken) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03504300807",
						"Only one state can be marked as broken state"));
				}
				had_broken = true;
			}
			if (isBatchDefault.booleanValue()) {
				if (had_batchDefault) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03202141012",
						"Only one state can be marked as default exit stae for batches and milestones"));
				}
				had_batchDefault = true;
				if (isFinal.booleanValue() != true) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03202141013",
						"The default exit state for batches and milestones must be defined FINAL"));
				}
			}
			if (isDependencyDefault.booleanValue()) {
				if (isFinal.booleanValue() != true) {
					throw new CommonErrorException (new SDMSMessage(sysEnv, "03202141014",
						"A default exit state for dependencies must be defined FINAL"));
				}
			}
			esd = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, espState.name);
			esdId = esd.getId(sysEnv);
			pref = Integer.valueOf (idx);
			idx ++;
			try {
				SDMSExitStateTable.table.create (sysEnv, pref, isFinal, isRestartable, isUnreachable, isDisabled, isBroken, isBatchDefault, isDependencyDefault, espId, esdId);
			} catch (DuplicateKeyException dke) {
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03110120950",
						"Exit state definition $1 specified more than once", espState.name));
			}
		}
	}

	private void checkForFinalState(SystemEnvironment sysEnv, Long espId)
		throws SDMSException
	{
		Vector v = SDMSExitStateTable.idx_espId.getVector(sysEnv, espId);
		boolean gotFinalState = false;
		for (int i = 0; i < v.size(); ++i) {
			SDMSExitState es = (SDMSExitState) v.get(i);
			if (es.getIsFinal(sysEnv).booleanValue()) {
				gotFinalState = true;
				break;
			}
		}
		if (!gotFinalState) {
			throw new CommonErrorException (new SDMSMessage(sysEnv, "03202241103",
					"An exit state profile requires at least one final state"));
		}
	}
}
