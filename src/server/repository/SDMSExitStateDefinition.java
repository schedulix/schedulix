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

public class SDMSExitStateDefinition extends SDMSExitStateDefinitionProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSExitStateDefinition.java,v 2.4.6.2 2013/03/19 17:16:51 ronald Exp $";

	protected SDMSExitStateDefinition(SDMSObject p_object)
	{
		super(p_object);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long esdId = getId(sysEnv);

		Vector v = SDMSExitStateMappingTable.idx_esdId.getVector(sysEnv, esdId);
		if (v.size() > 0) {
			SDMSExitStateMapping esm = (SDMSExitStateMapping)v.elementAt(0);
			SDMSExitStateMappingProfile esmp = SDMSExitStateMappingProfileTable.getObject(sysEnv, esm.getEsmpId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02111082356",
			                               "Exit State Definition used by Exit State Mapping $1", esmp.getName(sysEnv)));
		}

		v = SDMSExitStateTable.idx_esdId.getVector(sysEnv, esdId);
		if (v.size() > 0) {
			SDMSExitState es = (SDMSExitState)v.elementAt(0);
			SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject(sysEnv, es.getEspId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02111090001",
			                               "Exit State Definition used by Exit State Profile $1", esp.getName(sysEnv)));
		}
		v = SDMSExitStateTranslationTable.idx_fromEsdId.getVector(sysEnv, esdId);
		if (v.size() > 0) {
			SDMSExitStateTranslation est = (SDMSExitStateTranslation)v.elementAt(0);
			SDMSExitStateTranslationProfile estp = SDMSExitStateTranslationProfileTable.getObject(sysEnv, est.getEstpId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02112202054",
			                               "Exit State Definition is from State in Exit State Translation $1", estp.getName(sysEnv)));
		}
		v = SDMSExitStateTranslationTable.idx_toEsdId.getVector(sysEnv, esdId);
		if (v.size() > 0) {
			SDMSExitStateTranslation est = (SDMSExitStateTranslation)v.elementAt(0);
			SDMSExitStateTranslationProfile estp = SDMSExitStateTranslationProfileTable.getObject(sysEnv, est.getEstpId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02112202055",
			                               "Exit State Definition is to State in Exit State Translation $1", estp.getName(sysEnv)));
		}
		v = SDMSTriggerStateTable.idx_fromStateId.getVector(sysEnv, esdId);
		if (v.size() > 0) {
			SDMSTriggerState ts = (SDMSTriggerState)v.elementAt(0);
			SDMSTrigger t = SDMSTriggerTable.getObject(sysEnv, ts.getTriggerId(sysEnv));
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, t.getFireId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02305130933",
			                               "Exit State Definition is Trigger State in $1 of $2", t.getName(sysEnv), se.pathString(sysEnv)));
		}
		v = SDMSResourceStateMappingTable.idx_esdId.getVector(sysEnv, esdId);
		if (v.size() > 0) {
			SDMSResourceStateMapping rsm = (SDMSResourceStateMapping)v.elementAt(0);
			SDMSResourceStateMappingProfile rsmp = SDMSResourceStateMappingProfileTable.getObject(sysEnv, rsm.getRsmpId(sysEnv));
			throw new CommonErrorException(new SDMSMessage(sysEnv, "039011821630",
			                               "Exit State Definition is in use by resource state mapping $1", rsmp.getName(sysEnv)));
		}

		super.delete(sysEnv);
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getName(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "exit state definition " + getURLName(sysEnv);
	}
}
