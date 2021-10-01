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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;

public class ResourceTreeLister extends TreeLister
{
	public static final String __version = "@(#) $Id: ResourceTreeLister.java,v 1.5.2.2 2013/03/19 17:16:49 ronald Exp $";

	SystemEnvironment systemEnvironment;
	SsRFormatter formatter;
	HashMap      namedResources;

	protected void initLister(SystemEnvironment sysEnv, Long sId)
		throws SDMSException
	{
		systemEnvironment = sysEnv;
		Long scopeId = sId;
		namedResources = new HashMap();
		formatter = new SsRFormatter(namedResources);

		while (scopeId != null) {
			Vector rv = SDMSResourceTable.idx_scopeId.getVector(sysEnv, scopeId);
			for(int i = 0; i < rv.size(); ++i) {
				final SDMSResource r = (SDMSResource) rv.get(i);
				enrollCategories(sysEnv, r.getNrId(sysEnv), scopeId);
			}
			final SDMSScope s = SDMSScopeTable.getObject(sysEnv, scopeId);
			scopeId = s.getParentId(sysEnv);
		}
	}

	protected void enrollCategories(SystemEnvironment sysEnv, Long nrId, Long scopeId)
		throws SDMSException
	{
		if (!namedResources.containsKey(nrId)) {
			namedResources.put(nrId, scopeId);
			Long parentId = SDMSNamedResourceTable.getObject(sysEnv,nrId).getParentId(sysEnv);
			if (parentId != null) {
				enrollCategories(sysEnv, parentId, scopeId);
			}
		}
	}

	public ResourceTreeLister(SystemEnvironment sysEnv, SDMSMessage t, Long scopeId)
		throws SDMSException
	{
		super(t);
		initLister(sysEnv, scopeId);
	}

	public Vector format(SystemEnvironment sysEnv, SDMSProxy o) throws SDMSException
	{
		return formatter.fillVector(sysEnv, o, null);
	}

	public Vector getDesc()
	{
		return formatter.fillHeadInfo();
	}

	public Vector getChildren(SystemEnvironment sysEnv, SDMSProxy obj)
		throws SDMSException
	{
		SDMSProxy o = obj;
		Long id = o.getId(sysEnv);
		Vector children = new Vector();

		if (namedResources.containsKey(id)) {
			SDMSNamedResource nr = (SDMSNamedResource)o;
			Long scopeId = (Long) namedResources.get(id);
			switch (nr.getUsage(sysEnv).intValue()) {
				case SDMSNamedResource.CATEGORY:
					Vector v = SDMSNamedResourceTable.idx_parentId.getVector(sysEnv, id);
					Iterator i = v.iterator();
					while (i.hasNext()) {
						SDMSNamedResource cnr = (SDMSNamedResource) i.next();
						Long cnrId = cnr.getId(sysEnv);
						if (namedResources.containsKey(cnrId)) {
							children.add(cnr);
						}
					}
					return children;
				default:
					return children;
			}
		}
		return children;
	}

	public int compare(Object o1, Object o2)
	{
		Vector v1;
		Vector v2;
		try {
			v1  = getPath(systemEnvironment, (SDMSProxy)o1);
			v2  = getPath(systemEnvironment, (SDMSProxy)o2);
		} catch (SerializationException e) {
			throw new RuntimeException();

		} catch (SDMSException se) {
			return 0;
		}
		int l1 = v1.size();
		int l2 = v2.size();
		int n = l1 < l2 ? l1 : l2;
		int r;
		for (int i = 0; i < n; i ++) {
			r = ((String)v1.elementAt(i)).compareTo((String)v2.elementAt(i));
			if (r != 0) return r;
		}
		if (l1 == l2) {
			Vector v1s;
			Vector v2s;
			try {
				v1s = getScopePath(systemEnvironment, (SDMSProxy)o1);
				v2s = getScopePath(systemEnvironment, (SDMSProxy)o2);
			} catch (SerializationException e) {
				throw new RuntimeException();

			} catch (SDMSException se) {
				return 0;
			}
			l1 = v1.size();
			l2 = v2.size();
			n = l1 < l2 ? l1 : l2;
			for (int i = 0; i < n; i ++) {
				r = ((String)v1.elementAt(i)).compareTo((String)v2.elementAt(i));
				if (r != 0) return r;
			}
		}
		return l1 < l2 ? -1 : 1;
	}

	protected Vector getPath(SystemEnvironment sysEnv, SDMSProxy o)
		throws SDMSException
	{
		try {
			return ((SDMSNamedResource)o).pathVector(sysEnv);
		} catch (ClassCastException cce) {
			Long nrId;
			nrId = ((SDMSResource)o).getNrId(sysEnv);
			return SDMSNamedResourceTable.getObject(sysEnv, nrId).pathVector(sysEnv);
		}
	}

	protected Vector getScopePath(SystemEnvironment sysEnv, SDMSProxy o)
		throws SDMSException
	{
		try {
			SDMSNamedResource dummy = (SDMSNamedResource)o;
			return new Vector();
		} catch (ClassCastException cce) {
			SDMSScope s;
			SDMSResource r = (SDMSResource)o;
			s = SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv));
			return s.pathVector(sysEnv);
		}
	}
}

