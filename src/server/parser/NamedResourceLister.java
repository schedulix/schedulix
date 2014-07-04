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

public class NamedResourceLister extends HierarchyLister
{

	public final static String __version = "@(#) $Id: NamedResourceLister.java,v 2.2.8.1 2013/03/14 10:24:42 ronald Exp $";

	public NamedResourceLister()
	{
		super();
	}

	public NamedResourceLister(Vector p, HashSet h)
	{
		super(p, h);
	}

	public void setDefaultStartpoint(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSNamedResource nr = SDMSNamedResourceTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(null, "RESOURCE"));
		startpoint = nr.pathVector(sysEnv);
	}

	public SDMSProxy getStartObject(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return SDMSNamedResourceTable.getNamedResource(sysEnv, startpoint);
	}

	public SDMSProxy getObject(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		return SDMSNamedResourceTable.getObject(sysEnv, id);
	}

	public Vector getChildren(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		Vector v = SDMSNamedResourceTable.idx_parentId.getVector(sysEnv, id);
		return v;
	}

	public Vector getParents(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		Vector v = new Vector();
		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, id);
		Long parentId = nr.getParentId(sysEnv);
		while(parentId != null) {
			nr = SDMSNamedResourceTable.getObject(sysEnv, parentId);
			v.addElement(nr);
			parentId = nr.getParentId(sysEnv);
		}
		return v;
	}

	public boolean isLeaf(SystemEnvironment sysEnv, SDMSProxy o)
		throws SDMSException
	{
		if(((SDMSNamedResource) o).getUsage(sysEnv).intValue() == SDMSNamedResource.CATEGORY) return false;
		return true;
	}

}

