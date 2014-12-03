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
	private ObjectURL url;
	private Vector name2;
	private String newName;

	public MoveJobDefinition(ObjectURL u, Vector to)
	{
		super();
		url = u;
		name2 = to;
		newName = null;
	}

	public MoveJobDefinition(ObjectURL u, String to)
	{
		super();
		url = u;
		name2 = null;
		newName = to;
	}

	private void moveJobDefinition(SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		SDMSFolder p;
		Long folderId;
		String name;
		Long npId, opId, myId;

		try {
			p = SDMSFolderTable.getFolder(sysEnv, name2);

			name = se.getName(sysEnv);

		} catch(NotFoundException nfe) {

			name = (String) name2.remove(name2.size() - 1);
			p = SDMSFolderTable.getFolder(sysEnv, name2);

		}
		npId = p.getId(sysEnv);
		opId = se.getFolderId(sysEnv);

		if(SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(npId, name))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "02204160909", "Object with name $1 already exists within $2",
				name, p.pathString(sysEnv)));
		}

		if(SDMSSchedulingEntityTable.idx_folderId_name.containsKey(sysEnv, new SDMSKey(npId, name))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03410311345", "Object with name $1 already exists within $2",
			                                name, p.pathString(sysEnv)));
		}

		se.set_FolderIdName (sysEnv, npId, name);

		result.setFeedback(new SDMSMessage(sysEnv, "03202201037", "Job Definition moved"));
	}

	private void renameJobDefinition(SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		Long opId;

		opId = se.getFolderId(sysEnv);

		if(SDMSSchedulingEntityTable.idx_folderId_name.containsKey(sysEnv, new SDMSKey(opId, newName))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03112161101", "Object with name $1 already exists",
				newName));
		}
		if(SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(opId, newName))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03112161103", "Object with name $1 already exists",
			                                newName));
		}

		se.setName (sysEnv, newName);

		result.setFeedback(new SDMSMessage(sysEnv, "03202201037", "Job Definition renamed"));
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSSchedulingEntity se;

		SDMSProxy prox = url.resolve(sysEnv);
		if (prox instanceof SDMSSchedulingEntity)
			se = (SDMSSchedulingEntity) url.resolve(sysEnv);
		else {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03410311333", "The object $1 is not a scheduling entity", url.toString()));
		}

		if (newName == null)
			moveJobDefinition(sysEnv, se);
		else
			renameJobDefinition(sysEnv, se);

	}
}

