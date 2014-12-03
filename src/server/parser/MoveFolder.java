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

public class MoveFolder extends Node
{
	private ObjectURL url;
	private Vector name2;
	private String newName;

	public MoveFolder(ObjectURL u, Vector to)
	{
		super();
		url = u;
		name2 = to;
		newName = null;
	}

	public MoveFolder(ObjectURL u, String to)
	{
		super();
		url = u;
		name2 = null;
		newName = to;
	}

	private void moveFolder(SystemEnvironment sysEnv, SDMSFolder f)
		throws SDMSException
	{
		SDMSFolder p;
		String name;
		Long npId, opId, myId;

		try {
			p = SDMSFolderTable.getFolder(sysEnv, name2);

			name = f.getName(sysEnv);

		} catch(NotFoundException nfe) {
			name = (String) name2.remove(name2.size() - 1);
			p = SDMSFolderTable.getFolder(sysEnv, name2);

		}

		npId = p.getId(sysEnv);

		opId = f.getParentId(sysEnv);
		if(opId == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03704102217", "The Folder SYSTEM cannot be moved"));
		}

		myId = f.getId(sysEnv);

		if(SDMSSchedulingEntityTable.idx_folderId_name.containsKey(sysEnv, new SDMSKey(npId, name))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03201290943", "Object with name $1 already exists within $2",
				name, f.pathString(sysEnv)));
		}

		if(npId.equals(myId)) {

			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201291020", "A Folder cannot be moved below itself"));
		}
		Long id;
		while((id = p.getParentId(sysEnv)) != null) {
			if(id.equals(myId)) {

				throw new CommonErrorException(new SDMSMessage(sysEnv, "03201291021", "A Folder cannot be moved below itself"));
			}
			p = SDMSFolderTable.getObject(sysEnv, id);
		}

		f.set_ParentIdName(sysEnv, npId, name);

		result.setFeedback(new SDMSMessage(sysEnv, "03201290948", "Folder moved"));
	}

	private void renameFolder(SystemEnvironment sysEnv, SDMSFolder f)
		throws SDMSException
	{
		Long opId;

		opId = f.getParentId(sysEnv);
		if(opId == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03112161102", "The Folder SYSTEM cannot be renamed"));
		}

		if(SDMSSchedulingEntityTable.idx_folderId_name.containsKey(sysEnv, new SDMSKey(opId, newName))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03112161101", "Object with name $1 already exists",
				newName));
		}
		if(SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(opId, newName))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03112161103", "Object with name $1 already exists",
				newName));
		}

		f.setName(sysEnv, newName);

		result.setFeedback(new SDMSMessage(sysEnv, "03112161104", "Folder renamed"));
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSFolder f;

		SDMSProxy prox = url.resolve(sysEnv);
		if (prox instanceof SDMSFolder)
			f = (SDMSFolder) url.resolve(sysEnv);
		else {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03111090754", "The object $1 is not a folder", url.toString()));
		}

		if (newName == null)
			moveFolder(sysEnv, f);
		else
			renameFolder(sysEnv, f);

	}
}

