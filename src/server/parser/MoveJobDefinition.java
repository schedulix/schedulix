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
import de.independit.scheduler.server.output.*;

public class MoveJobDefinition extends Node
{

	public final static String __version = "@(#) $Id: MoveJobDefinition.java,v 2.1.2.1 2013/03/14 10:24:41 ronald Exp $";

	private Vector path1;
	private String name1;
	private Vector path2;
	private String newName;

	public MoveJobDefinition(Vector p1, String from, Vector p2)
	{
		super();
		path1 = p1;
		name1 = from;
		path2 = p2;
		newName = null;
	}

	public MoveJobDefinition(Vector p1, String from, String to)
	{
		super();
		path1 = p1;
		name1 = from;
		path2 = null;
		newName = to;
	}

	private void moveJobDefinition(SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		SDMSFolder f;
		Long folderId;
		String name;

		try {
			f = SDMSFolderTable.getFolder(sysEnv, path2);

			name = name1;

		} catch(NotFoundException nfe) {

			name = (String) path2.remove(path2.size() -1);
			f = SDMSFolderTable.getFolder(sysEnv, path2);

		}
		folderId = f.getId(sysEnv);

		if(SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(folderId, name))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "02204160909", "Object with name $1 already exists within $2",
				name, f.pathString(sysEnv)));
		}

		se.set_FolderIdName (sysEnv, folderId, name);

		result.setFeedback(new SDMSMessage(sysEnv, "03202201037", "Job Definition moved"));
	}

	private void renameJobDefinition(SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{

		if(SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(se.getFolderId(sysEnv), newName))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03112161224", "Object with name $1 already exists",
				newName));
		}
		se.setName (sysEnv, newName);

		result.setFeedback(new SDMSMessage(sysEnv, "03202201037", "Job Definition renamed"));
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{

		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.get(sysEnv, path1, name1);

		if (newName == null)
			moveJobDefinition(sysEnv, se);
		else
			renameJobDefinition(sysEnv, se);

	}
}

