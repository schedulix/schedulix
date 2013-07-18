/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class ScopeLister extends HierarchyLister
{

	public final static String __version = "@(#) $Id: ScopeLister.java,v 2.2.14.1 2013/03/14 10:24:47 ronald Exp $";

	public ScopeLister()
	{
		super();
	}

	public ScopeLister(ObjectURL u, HashSet h)
	{
		super(u, h);
	}

	public void setDefaultStartpoint(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSScope s = SDMSScopeTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(null, "GLOBAL"));
		startpoint = s.pathVector(sysEnv);
	}

	public SDMSProxy getStartObject(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return SDMSScopeTable.getScope(sysEnv, startpoint);
	}

	public SDMSProxy getObject(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		return SDMSScopeTable.getObject(sysEnv, id);
	}

	public Vector getChildren(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		Vector v = SDMSScopeTable.idx_parentId.getVector(sysEnv, id);
		return v;
	}

	public Vector getParents(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		Vector v = new Vector();
		SDMSScope s = SDMSScopeTable.getObject(sysEnv, id);
		Long parentId = s.getParentId(sysEnv);
		while(parentId != null) {
			s = SDMSScopeTable.getObject(sysEnv, parentId);
			v.addElement(s);
			parentId = s.getParentId(sysEnv);
		}
		return v;
	}

	public boolean isLeaf(SystemEnvironment sysEnv, SDMSProxy o)
	throws SDMSException
	{
		if(((SDMSScope) o).getType(sysEnv).intValue() == SDMSScope.SCOPE) return false;
		return true;
	}

}

