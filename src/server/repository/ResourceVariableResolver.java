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
import java.lang.*;
import java.util.*;
import java.text.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.*;

public class ResourceVariableResolver extends VariableResolver
{

	public static final String __version = "@(#) $Id: ResourceVariableResolver.java,v 2.7.4.1 2013/03/14 10:25:17 ronald Exp $";

	public static final String S_STATE              = "STATE";
	public static final String S_AMOUNT             = "AMOUNT";
	public static final String S_FREE_AMOUNT        = "FREE_AMOUNT";
	public static final String S_REQUESTABLE_AMOUNT = "REQUESTABLE_AMOUNT";
	public static final String S_REQUESTED_AMOUNT   = "REQUESTED_AMOUNT";
	public static final String S_TIMESTAMP          = "TIMESTAMP";

	private static final HashSet internalVars;

	static
	{
		internalVars = new HashSet();
		internalVars.add(S_STATE);
		internalVars.add(S_AMOUNT);
		internalVars.add(S_FREE_AMOUNT);
		internalVars.add(S_REQUESTABLE_AMOUNT);
		internalVars.add(S_REQUESTED_AMOUNT);
		internalVars.add(S_TIMESTAMP);
	}

	private SDMSSubmittedEntity sme = null;

	protected String getVariableValue(SystemEnvironment sysEnv,
	                                  SDMSProxy thisObject,
	                                  String key,
	                                  boolean fastAccess,
	                                  String mode,
	                                  boolean triggercontext,
	                                  long version,
	                                  SDMSScope evalScope)
	throws SDMSException
	{
		sme = null;
		final String retval = getInternalVariableValue(sysEnv, (SDMSResource) thisObject, key, fastAccess, mode, triggercontext, new Stack(), version, null);

		return retval;
	}

	protected String getVariableValue(SystemEnvironment sysEnv,
	                                  SDMSProxy thisObject,
	                                  String key,
	                                  boolean fastAccess,
	                                  String mode,
	                                  boolean triggercontext,
	                                  long version,
	                                  SDMSScope evalScope,
	                                  SDMSSubmittedEntity sme)
	throws SDMSException
	{
		this.sme = sme;
		final String retval = getInternalVariableValue(sysEnv, (SDMSResource) thisObject, key, fastAccess, mode, triggercontext, new Stack(), version, null);

		return retval;
	}

	protected String getStandardVariable(SystemEnvironment sysEnv, SDMSResource thisResource, String key)
	throws SDMSException
	{
		String retVal = "";

		if (key.equals(S_STATE)) {
			Long rsdId = thisResource.getRsdId(sysEnv);
			if (rsdId == null) return retVal;
			SDMSResourceStateDefinition rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, thisResource.getRsdId(sysEnv));
			retVal = rsd.getName(sysEnv);
		} else if (key.equals(S_AMOUNT)) {
			Integer amount = thisResource.getAmount(sysEnv);
			if (amount == null) retVal = "";
			else retVal = amount.toString();
		} else if (key.equals(S_FREE_AMOUNT)) {
			Integer amount = thisResource.getFreeAmount(sysEnv);
			if (amount == null) retVal = "";
			else retVal = amount.toString();
		} else if (key.equals(S_REQUESTABLE_AMOUNT)) {
			Integer amount = thisResource.getRequestableAmount(sysEnv);
			if (amount == null) retVal = "";
			else retVal = amount.toString();
		} else if (key.equals(S_REQUESTED_AMOUNT)) {
			Integer amount = null;
			if (sme != null) {
				try {
					SDMSResourceAllocation sa = SDMSResourceAllocationTable.idx_smeId_rId_getUnique(sysEnv, new SDMSKey(sme.getId(sysEnv), thisResource.getId(sysEnv)));
					amount = sa.getAmount(sysEnv);
				} catch (NotFoundException nfe) {

				}
			}
			if (amount == null) retVal = "";
			else retVal = amount.toString();
		} else if (key.equals(S_TIMESTAMP)) {
			Long rsdTime = thisResource.getRsdTime(sysEnv);
			if (rsdTime == null) retVal = "";
			else retVal = "" + rsdTime.longValue()/1000;
		}
		return retVal;
	}

	protected String getInternalVariableValue(SystemEnvironment sysEnv,
	                SDMSProxy thisObject,
	                String key,
	                boolean fastAccess,
	                String mode,
	                boolean triggercontext,
	                Stack recursionCheck,
	                long version,
	                SDMSScope evalScope)
	throws SDMSException
	{
		SDMSResource thisResource = (SDMSResource) thisObject;

		Long linkId = thisResource.getLinkId(sysEnv);
		while (linkId != null) {
			thisResource = SDMSResourceTable.getObject(sysEnv, linkId);
			linkId = thisResource.getLinkId(sysEnv);
		}
		Long rId = thisResource.getId(sysEnv);
		Long pdId;
		String retVal;
		SDMSParameterDefinition pd;
		if (internalVars.contains(key)) return getStandardVariable(sysEnv, thisResource, key);

		pd = thisResource.getParameterDefinition(sysEnv, key);
		pdId = pd.getId(sysEnv);
		String defaultValue = pd.getDefaultValue(sysEnv);
		if(defaultValue != null) defaultValue = defaultValue.substring(1);
		else			 defaultValue = new String("");

		if(pd.getType(sysEnv).intValue() == SDMSParameterDefinition.CONSTANT)
			retVal = defaultValue;
		else {
			SDMSResourceVariable rv;
			try {
				rv = SDMSResourceVariableTable.idx_pdId_rId_getUnique(sysEnv, new SDMSKey(pdId, rId));
				retVal = rv.getValue(sysEnv).substring(1);
			} catch(NotFoundException nfe) {
				retVal = defaultValue;
			}
		}

		return parseAndSubstitute(sysEnv, thisResource, retVal, fastAccess, mode, triggercontext, recursionCheck, version);
	}

	public ResourceVariableResolver()
	{

	}
}
