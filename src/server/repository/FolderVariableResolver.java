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

public class FolderVariableResolver extends VariableResolver
{

	public static final String __version = "@(#) $Id: FolderVariableResolver.java,v 2.4.8.1 2013/03/14 10:25:17 ronald Exp $";

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
		SDMSThread.doTrace(sysEnv.cEnv, "get valiable value : " + key, SDMSThread.SEVERITY_DEBUG);
		final String retval = getInternalVariableValue(sysEnv, (SDMSFolder) thisObject, key, fastAccess, mode, triggercontext, new Stack(), version, null);

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
						SDMSScope evalScope)
		throws SDMSException
	{
		String retval;
		SDMSFolder thisFolder = (SDMSFolder) thisObject;
		try {
			SDMSParameterDefinition pd;
			if(version < 0)
				pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(thisFolder.getId(sysEnv), key));
			else
				pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(thisFolder.getId(sysEnv), key), version);
			retval = pd.getDefaultValue(sysEnv).substring(1);
			return parseAndSubstitute(sysEnv, thisFolder, retval, fastAccess, mode, triggercontext, recursionCheck, version);
		} catch(NotFoundException nfe) {
			Long parentId = thisFolder.getParentId(sysEnv);
			if(parentId != null) {
				SDMSFolder pf;
				if(version > 0)
					pf = SDMSFolderTable.getObject(sysEnv, parentId, version);
				else
					pf = SDMSFolderTable.getObject(sysEnv, parentId);
				return getInternalVariableValue(sysEnv, pf, key, fastAccess, mode, triggercontext, recursionCheck, version);
			} else {
				throw new NotFoundException(new SDMSMessage(sysEnv, "03209231452", "Couldn't resolve the variable $1", key));
			}
		}
	}

	public FolderVariableResolver()
	{

	}
}
