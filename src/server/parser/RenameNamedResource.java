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

public class RenameNamedResource extends Node
{

	public final static String __version = "@(#) $Id: RenameNamedResource.java,v 2.2.2.1 2013/03/14 10:24:45 ronald Exp $";

	private ObjectURL url;
	private Vector path2;
	private String newName;

	public RenameNamedResource(ObjectURL u, Vector to)
	{
		super();
		url = u;
		path2 = to;
		newName = null;
	}

	public RenameNamedResource(ObjectURL u, String to)
	{
		super();
		url = u;
		path2 = null;
		newName = to;
	}

	private void moveNamedResource(SystemEnvironment sysEnv, SDMSNamedResource src)
		throws SDMSException
	{
		SDMSNamedResource p;
		String name;
		Long npId, opId, myId;

		try {
			p = SDMSNamedResourceTable.getNamedResource(sysEnv, path2);

			name = src.getName(sysEnv);

		} catch(NotFoundException nfe) {
			name = (String) path2.remove(path2.size() -1);
			p = SDMSNamedResourceTable.getNamedResource(sysEnv, path2);

		}
		npId = p.getId(sysEnv);
		opId = src.getParentId(sysEnv);

		if(opId == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03704102156",
						"The category RESOURCE cannot be moved"));
		}

		if(p.getUsage(sysEnv).intValue() != SDMSNamedResource.CATEGORY) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206250011",
						"Resource cannot be contained within another Resource"));
		}

		myId = src.getId(sysEnv);

		if(npId.equals(myId)) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207041900", "A category cannot be moved below itself"));
		}
		Long id;
		while((id = p.getParentId(sysEnv)) != null) {
			if(id.equals(myId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03207041903", "A Folder cannot be moved below itself"));
			}
			p = SDMSNamedResourceTable.getObject(sysEnv, id);
		}

		src.set_ParentIdName(sysEnv, npId, name);

		result.setFeedback(new SDMSMessage(sysEnv, "03112161307", "Named Resource moved"));
	}

	private void renameNamedResource(SystemEnvironment sysEnv, SDMSNamedResource src)
		throws SDMSException
	{
		src.setName(sysEnv, newName);

		result.setFeedback(new SDMSMessage(sysEnv, "03203150039", "Named Resource renamed"));
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSNamedResource src;

		src = (SDMSNamedResource) url.resolve(sysEnv);

		if (newName == null)
			moveNamedResource(sysEnv, src);
		else
			renameNamedResource(sysEnv, src);
	}
}

