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

public class ResourceScopeLister extends HierarchyLister
{

	public final static String __version = "@(#) $Id: ResourceScopeLister.java,v 2.2.18.1 2013/03/14 10:24:46 ronald Exp $";

	public ResourceScopeLister()
	{
		super();
	}

	public ResourceScopeLister(Vector p, HashSet h)
	{
		super(p, h);
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
		try {
			return SDMSScopeTable.getObject(sysEnv, id);
		} catch (NotFoundException nfe) {
			try {
				return SDMSResourceTable.getObject(sysEnv, id);
			} catch (NotFoundException nfe2) {
				return SDMSScopeTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(null, "GLOBAL"));
			}
		}
	}

	public Vector getChildren(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		Vector v = SDMSScopeTable.idx_parentId.getVector(sysEnv, id);
		Vector w = SDMSResourceTable.idx_scopeId.getVector(sysEnv, id);
		v.addAll(w);
		return v;
	}

	public Vector getParents(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		Vector v = new Vector();
		Long parentId;
		SDMSScope s;
		try {
			s = SDMSScopeTable.getObject(sysEnv, id);
			parentId = s.getParentId(sysEnv);
		} catch (NotFoundException nfe) {
			SDMSResource r = SDMSResourceTable.getObject(sysEnv, id);
			parentId = r.getScopeId(sysEnv);
		}
		while(parentId != null) {
			try {
				s = SDMSScopeTable.getObject(sysEnv, parentId);
			} catch (NotFoundException nfe) {
				s = SDMSScopeTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(null, "GLOBAL"));
			}
			v.addElement(s);
			parentId = s.getParentId(sysEnv);
		}
		return v;
	}

	public boolean isLeaf(SystemEnvironment sysEnv, SDMSProxy o)
		throws SDMSException
	{
		if(o instanceof SDMSResource) return true;
		if(((SDMSScope) o).getType(sysEnv).intValue() == SDMSScope.SCOPE) return false;
		return true;
	}

}

