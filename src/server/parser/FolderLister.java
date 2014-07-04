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
import de.independit.scheduler.server.parser.filter.*;

public class FolderLister extends HierarchyLister
{

	public final static String __version = "@(#) $Id: FolderLister.java,v 2.3.8.1 2013/03/14 10:24:33 ronald Exp $";

	public FolderLister()
	{
		super();
	}

	public FolderLister(Vector p, HashSet h)
	{
		super(p, h);
	}

	public void setDefaultStartpoint(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSFolder nr = SDMSFolderTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(null, "SYSTEM"));
		startpoint = nr.pathVector(sysEnv);
	}

	public SDMSProxy getStartObject(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return SDMSFolderTable.getFolder(sysEnv, startpoint);
	}

	public SDMSProxy getObject(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		try {
			return SDMSFolderTable.getObject(sysEnv, id);
		} catch (NotFoundException nfe) {
			return SDMSSchedulingEntityTable.getObject(sysEnv, id);
		}
	}

	public Vector getChildren(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		Vector v = SDMSFolderTable.idx_parentId.getVector(sysEnv, id);
		Vector w = SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, id);
		v.addAll(w);
		return v;
	}

	public Vector getParents(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		Vector v = new Vector();
		Long parentId;
		try {
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, id);
			parentId = se.getFolderId(sysEnv);
		} catch (NotFoundException nfe) {
			SDMSFolder f = SDMSFolderTable.getObject(sysEnv, id);
			parentId = f.getParentId(sysEnv);
		}
		while(parentId != null) {
			SDMSFolder f = SDMSFolderTable.getObject(sysEnv, parentId);
			v.addElement(f);
			parentId = f.getParentId(sysEnv);
		}
		return v;
	}

	public boolean isLeaf(SystemEnvironment sysEnv, SDMSProxy o)
		throws SDMSException
	{
		if(o instanceof SDMSFolder) return false;
		return true;
	}

}

