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

import java.lang.*;
import java.util.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSSchedulingEntityTable extends SDMSSchedulingEntityTableGeneric
{

	public final static String __version = "@(#) $Id: SDMSSchedulingEntityTable.java,v 2.1.18.1 2013/03/14 10:25:24 ronald Exp $";

	public SDMSSchedulingEntityTable(SystemEnvironment env)
		throws SDMSException
	{
		super(env);
	}

	public static SDMSSchedulingEntity get(SystemEnvironment sysEnv, Vector path, String name)
		throws SDMSException
	{
		Vector myPath = new Vector(path);
		if (name == null) {

			name = (String)(myPath.remove(path.size()-1));
		}
		if (myPath.size() > 0) {
			SDMSFolder f = SDMSFolderTable.getFolder(sysEnv, myPath);
			Long fId = f.getId(sysEnv);
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.idx_folderId_name_getUnique(sysEnv, new SDMSKey (fId, name));
			return se;
		} else {
			throw new CommonErrorException(
					new SDMSMessage(sysEnv, "02206271151", "Invalid Name [Missing Folderpath]"));
		}
	}

	public static SDMSSchedulingEntity get(SystemEnvironment sysEnv, Vector path, String name, long version)
		throws SDMSException
	{
		Vector myPath = new Vector(path);
		if (name == null) {

			name = (String)(myPath.remove(path.size()-1));
		}
		if (path.size() > 0) {
			SDMSFolder f = SDMSFolderTable.getFolder(sysEnv, myPath, version);
			Long fId = f.getId(sysEnv);
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.idx_folderId_name_getUnique(sysEnv, new SDMSKey (fId, name), version);
			return se;
		} else {
			throw new CommonErrorException(
					new SDMSMessage(sysEnv, "02206271152", "Invalid Name [Missing Folderpath]"));
		}
	}

	protected boolean checkCreatePrivs(SystemEnvironment sysEnv, SDMSSchedulingEntity p)
		throws SDMSException
	{
		final SDMSFolder f = SDMSFolderTable.getObject(sysEnv, p.getFolderId(sysEnv));
		if(f.checkPrivileges(sysEnv, SDMSPrivilege.CREATE_CONTENT))
			return true;
		return false;
	}

}
