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

public class DropJobDefinition extends Node
{

	public final static String __version = "@(#) $Id: DropJobDefinition.java,v 2.7.4.2 2013/03/19 17:16:46 ronald Exp $";

	Vector path;
	String name;
	Boolean force;
	boolean ignoreNotFound;

	public DropJobDefinition(Vector p, String n, Boolean f, boolean ignNF)
	{
		super();
		name = n;
		path = p;
		force = f;
		ignoreNotFound = ignNF;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSFolder f = SDMSFolderTable.getFolder(sysEnv, path);
		SDMSSchedulingEntity se = null;
		try {
			se = SDMSSchedulingEntityTable.idx_folderId_name_getUnique(sysEnv, new SDMSKey(f.getId(sysEnv), name));
		} catch(NotFoundException nfe) {
			if(ignoreNotFound) {
				result.setFeedback(new SDMSMessage(sysEnv, "03301291253", "Job Definition dropped"));
				return;
			} else {
				throw nfe;
			}
		}
		se.delete(sysEnv, force.booleanValue());

		result.setFeedback(new SDMSMessage(sysEnv, "03204112232", "Job Definition dropped"));
	}

}

