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

public class ScopeVariableResolver extends VariableResolver
{

	public static final String __version = "@(#) $Id: ScopeVariableResolver.java,v 2.3.14.1 2013/03/14 10:25:27 ronald Exp $";

	protected String getVariableValue(SystemEnvironment sysEnv,
	                                  SDMSProxy thisObject,
	                                  String key,
	                                  boolean fastAccess,
	                                  String mode,
	                                  boolean triggercontext,
	                                  long version,
	                                  SDMSScope evalScope,
	                                  boolean doSubstitute)
	throws SDMSException
	{
		final String retval = getInternalVariableValue(sysEnv, (SDMSScope) thisObject, key, fastAccess, mode, triggercontext, new Stack(), version, null, doSubstitute);

		return retval;
	}

	protected String getInternalVariableValue(SystemEnvironment sysEnv,
	                SDMSProxy thisObject,
	                String key,
	                boolean fastAccess,
	                String mode,
	                boolean triggercontext,
	                Stack recursionCheck,
	                long version,
	                SDMSScope evalScope,
	                boolean doSubstitute)
	throws SDMSException
	{
		String retval;
		SDMSScope thisScope = (SDMSScope) thisObject;
		try {
			SDMSParameterDefinition pd;
			if(version > 0)
				pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique (sysEnv, new SDMSKey (thisScope.getId (sysEnv), key), version);
			else
				pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique (sysEnv, new SDMSKey (thisScope.getId (sysEnv), key));
			retval = pd.getDefaultValue (sysEnv).substring (1);
			if (doSubstitute)
				return parseAndSubstitute(sysEnv, thisScope, key, retval, fastAccess, mode, triggercontext, recursionCheck, version);
			else
				return retval;
		} catch (final NotFoundException nfe) {
			final Long parentId = thisScope.getParentId (sysEnv);
			if (parentId == null)
				throw new NotFoundException (new SDMSMessage (sysEnv, "04306251847", "Cannot resolve the variable $1", key));

			SDMSScope ps;
			if(version > 0)
				ps = SDMSScopeTable.getObject (sysEnv, parentId, version);
			else
				ps = SDMSScopeTable.getObject (sysEnv, parentId);
			return getInternalVariableValue(sysEnv, ps, key, fastAccess, mode, triggercontext, recursionCheck, version, doSubstitute);
		}
	}

	public ScopeVariableResolver()
	{

	}
}
