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

public class SeVariableResolver extends VariableResolver
{

	public static final String __version = "@(#) $Id: SeVariableResolver.java,v 2.1.8.2 2013/03/22 14:48:04 ronald Exp $";

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
		final String retval = getInternalVariableValue(sysEnv, thisObject, key, fastAccess, mode, triggercontext, new Stack(), version, null, doSubstitute);

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
		SDMSSchedulingEntity thisSE = (SDMSSchedulingEntity) thisObject;
		try {
			SDMSParameterDefinition pd;
			if(version < 0)
				pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(thisSE.getId(sysEnv), key));
			else
				pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(thisSE.getId(sysEnv), key), version);
			retval = pd.getDefaultValue(sysEnv);
			if (retval != null) retval = retval.substring(1);
			else return retval;
			if (doSubstitute)
				return parseAndSubstitute(sysEnv, thisSE, retval, fastAccess, mode, triggercontext, recursionCheck, version);
			else
				return retval;
		} catch(NotFoundException nfe) {
			Long folderId = thisSE.getFolderId(sysEnv);
			if(folderId != null) {
				SDMSFolder pf;
				if(version < 0)
					pf = SDMSFolderTable.getObject(sysEnv, folderId);
				else
					pf = SDMSFolderTable.getObject(sysEnv, folderId, version);
				return pf.getVariableValue(sysEnv, key, version, doSubstitute);
			} else {
				throw new NotFoundException(new SDMSMessage(sysEnv, "03209231452", "Couldn't resolve the variable $1", key));
			}
		} catch (Exception e) {
			SDMSThread.doTrace(sysEnv.cEnv, "Exception occured while resolving parameter " + key + "\nException message:\n" + e.toString(), SDMSThread.SEVERITY_WARNING);
			return null;
		}
	}

	public SeVariableResolver()
	{

	}
}
