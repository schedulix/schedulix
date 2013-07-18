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

public class CopyFolder extends Node
{

	public final static String __version = "@(#) $Id: CopyFolder.java,v 2.7.2.1 2013/03/14 10:24:24 ronald Exp $";

	private ObjectURL url;
	private Vector urlVector;
	private Vector name2;
	private String newName;

	public CopyFolder(Vector v, Vector to)
	{
		super();
		urlVector = v;
		name2 = to;
		newName = null;
	}

	public CopyFolder(Vector v, String to)
	{
		super();
		urlVector = v;
		name2 = null;
		newName = to;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSFolder f, p, parent;
		SDMSSchedulingEntity se_o;
		String name;
		boolean keepName = false;
		Long npId, opId, myId;

		if (newName == null) {
			try {
				p = SDMSFolderTable.getFolder(sysEnv, name2);

				name = null;
				keepName = true;

			} catch(NotFoundException nfe) {
				if (urlVector.size() > 1) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03203131151", "It isn't possible to copy multiple sources to one target"));
				}
				name = (String) name2.remove(name2.size() -1);
				p = SDMSFolderTable.getFolder(sysEnv, name2);

			}
		} else {
			if (urlVector.size() > 1) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03203131143", "A partially specified target allows only one folder to be copied"));
			}
			url = (ObjectURL) urlVector.get(0);
			f = (SDMSFolder) url.resolve(sysEnv);
			opId = f.getParentId(sysEnv);
			p = SDMSFolderTable.getObject(sysEnv, opId);
			name = newName;
		}

		npId = p.getId(sysEnv);
		parent = p;
		HashMap relocationTable = new HashMap();
		Vector newFolders = new Vector();

		for (int i = 0; i < urlVector.size(); ++i) {
			url = (ObjectURL) urlVector.get(i);
			if (url.objType.equals(SDMSObjectComment.FOLDER)) {
				f = (SDMSFolder) url.resolve(sysEnv);
				if(!f.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
					throw new AccessViolationException(f.accessViolationMessage(sysEnv, "03203131453"));
				opId = f.getParentId(sysEnv);

				if (keepName)
					name = f.getName(sysEnv);

				if(opId == null) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03704102216", "The Folder SYSTEM cannot be copied"));
				}

				myId = f.getId(sysEnv);

				if(SDMSSchedulingEntityTable.idx_folderId_name.containsKey(sysEnv, new SDMSKey(npId, name))) {
					throw new DuplicateKeyException(new SDMSMessage(sysEnv, "02204160938", "Object with name $1 already exists within $2",
					                                name, parent.pathString(sysEnv)));
				}

				if (SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(npId, name))) {
					throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03112191224", "Object with name $1 already exists within $2",
					                                name, parent.pathString(sysEnv)));
				}

				if(npId.equals(myId)) {

					throw new CommonErrorException(new SDMSMessage(sysEnv, "02204160936", "A Folder cannot be copied below itself"));
				}
				Long id;
				p = parent;
				while((id = p.getParentId(sysEnv)) != null) {
					if(id.equals(myId)) {

						throw new CommonErrorException(new SDMSMessage(sysEnv, "02204160937", "A Folder cannot be copied below itself"));
					}
					p = SDMSFolderTable.getObject(sysEnv, id);
				}

				SDMSFolder newF = f.copy(sysEnv, npId, name, relocationTable);
				newFolders.addElement(newF);
			} else {
				se_o = (SDMSSchedulingEntity) url.resolve(sysEnv);
				if(!se_o.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
					throw new AccessViolationException(se_o.accessViolationMessage(sysEnv, "03203131454"));
				if (keepName)
					name = se_o.getName(sysEnv);
				if(SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(npId, name))) {
					throw new DuplicateKeyException(new SDMSMessage(sysEnv, "02204160925", "Object with name $1 already exists within $2",
					                                name, parent.pathString(sysEnv)));
				}
				SDMSSchedulingEntity newSe = se_o.copy(sysEnv, npId, name, relocationTable);
				relocationTable.put(se_o.getId(sysEnv), newSe.getId(sysEnv));
				newFolders.addElement(newSe);
			}
		}
		for (int i = 0; i < newFolders.size(); ++i) {
			SDMSProxy prox = (SDMSProxy) newFolders.get(i);
			if (prox instanceof SDMSFolder) {
				f = (SDMSFolder) prox;
				f.relocateEntityDetails(sysEnv, relocationTable);
			} else {
				se_o = (SDMSSchedulingEntity) prox;
				se_o.relocateDetails(sysEnv, relocationTable);
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03202200945", "Folder(s) copied"));
	}
}

