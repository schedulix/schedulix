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

public abstract class HierarchyLister
{

	public static final String __version = "@(#) $Id: HierarchyLister.java,v 2.11.2.3 2013/08/24 10:30:27 dieter Exp $";

	Vector startpoint;
	HashSet expandIds;
	Vector objectsToList;
	SDMSMessage title;
	Formatter formatter;
	int sortColumns[];
	Vector filter;
	public ObjectFilter objFilter = new ObjectFilter();

	public boolean useFilter = false;

	protected void initLister(Vector s, HashSet e, Vector o, SDMSMessage t, Formatter f, Vector flt)
	{
		startpoint = s;
		expandIds = e;
		objectsToList = o;
		title = t;
		sortColumns = new int[2];
		sortColumns[0] = 1;
		sortColumns[1] = 2;
		formatter = f;
		filter = flt;
	}

	public HierarchyLister()			{ initLister(null, null, null, null, null, new Vector()); }
	public HierarchyLister(Vector s, HashSet e)	{ initLister(s, e, null, null, null, new Vector()); }
	public HierarchyLister(ObjectURL u, HashSet e)	{ initLister(u.path, e, null, null, null, new Vector()); }
	public HierarchyLister(Vector s, HashSet e, Formatter f)
	{
		initLister(s, e, null, null, f, new Vector());
	}
	public HierarchyLister(Vector s, HashSet e, Formatter f, Vector flt)
	{
		initLister(s, e, null, null, f, flt);
	}

	public void setObjectsToList(Vector h)		{ objectsToList = h; }
	public void setStartpoint(Vector v)		{ startpoint = v; }
	public void setExpandIds(HashSet h)		{ expandIds = h; }
	public void setTitle(SDMSMessage s)		{ title = s; }
	public void setFormatter(Formatter f)		{ formatter = f; }
	public void setSortColumns(int c[])		{ sortColumns = c; }
	public void setFilter(Vector flt)		{ filter = flt; }

	public abstract void setDefaultStartpoint(SystemEnvironment sysEnv)	throws SDMSException;
	public abstract SDMSProxy getStartObject(SystemEnvironment sysEnv)	throws SDMSException;
	public abstract SDMSProxy getObject(SystemEnvironment sysEnv, Long id)	throws SDMSException;
	public abstract Vector getChildren(SystemEnvironment sysEnv, Long id)	throws SDMSException;
	public abstract Vector getParents(SystemEnvironment sysEnv, Long id)	throws SDMSException;
	public abstract boolean isLeaf(SystemEnvironment sysEnv, SDMSProxy o)	throws SDMSException;

	public SDMSOutputContainer list(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return list(sysEnv, true);
	}

	public SDMSOutputContainer list(SystemEnvironment sysEnv, boolean privCheck)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		SDMSProxy startObject;
		if(formatter == null) {
			throw new FatalException(new SDMSMessage(sysEnv, "03207221510", "No formatter specified"));
		}

		d_container = new SDMSOutputContainer(sysEnv, title, formatter.fillHeadInfo());
		boolean objectsToListPreset = (objectsToList != null);
		boolean didReject = false;
		if(!objectsToListPreset) {
			if(startpoint == null) setDefaultStartpoint(sysEnv);
			startObject = getStartObject(sysEnv);
			if(!privCheck || !startObject.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03402131330", "Insufficient Privileges"));
			objectsToList = new Vector();
			objectsToList.add(startObject);
			didReject = add_children(sysEnv, d_container, startObject, privCheck);
		}
		if (objectsToListPreset || didReject) {
			listBottomUp(sysEnv, d_container, privCheck);
		} else {
			Iterator i = objectsToList.iterator();
			while(i.hasNext()) {
				SDMSProxy elem = (SDMSProxy)i.next();
				Vector v = formatter.fillVector(sysEnv, elem, null);
				d_container.addData(sysEnv, v);
			}
		}
		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, sortColumns));

		return d_container;
	}

	private void listBottomUp(SystemEnvironment sysEnv, SDMSOutputContainer oc, boolean privCheck)
		throws SDMSException
	{
		Iterator i = objectsToList.iterator();
		Vector objectsToRender = new Vector();
		HashSet parentSet = new HashSet();
		HashSet rendered = new HashSet();
		Long pId;
		int j;

		while(i.hasNext()) {
			Object elem = i.next();
			SDMSProxy o;
			if (elem instanceof Long)
				o = getObject(sysEnv, (Long)elem);
			else
				o = (SDMSProxy)elem;
			Vector v = getParents(sysEnv, o.getId(sysEnv));
			j = -1;
			if (privCheck) {
				for(j = v.size() - 1; j >= 0; j--) {
					SDMSProxy p = (SDMSProxy) v.get(j);
					pId = p.getId(sysEnv);
					if(!p.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
						break;
				}
			}
			if (j >= 0) continue;
			for(j = v.size() - 1; j >= 0; j--) {
				SDMSProxy p = (SDMSProxy) v.get(j);
				pId = p.getId(sysEnv);

				if(!rendered.contains(pId)) {
					objectsToRender.addElement(p);
					parentSet.add(pId);
					rendered.add(pId);
				}
				if((expandIds != null && !expandIds.contains(pId)))
					break;
			}
			if(j < 0) {

				pId = o.getId(sysEnv);
				if(!rendered.contains(pId)) {
					objectsToRender.addElement(o);
					rendered.add(pId);
				}
			}
		}
		i = objectsToRender.iterator();
		while (i.hasNext()) {
			oc.addData(sysEnv, formatter.fillVector(sysEnv, (SDMSProxy) i.next(), parentSet));
		}
	}

	private boolean add_children(SystemEnvironment sysEnv, SDMSOutputContainer oc, SDMSProxy o, boolean privCheck)
		throws SDMSException
	{
		Vector iv;
		int i;
		Long id = o.getId(sysEnv);
		Long coId;
		SDMSProxy co;

		iv = getChildren(sysEnv, id);
		boolean didReject = false;
		for(i=0; i<iv.size(); i++) {
			co = (SDMSProxy) iv.get(i);
			SDMSPrivilege priv = co.getPrivileges(sysEnv);
			if (!privCheck || priv.can(SDMSPrivilege.VIEW) || priv.can(SDMSPrivilege.CREATE_PARENT_CONTENT)) {
				coId = co.getId(sysEnv);
				if (checkValid(sysEnv, co) || checkChildren(sysEnv, co, privCheck))
					objectsToList.add(co);
				else
					didReject = true;
				if((expandIds == null || expandIds.contains(coId)) && (!privCheck || priv.can(SDMSPrivilege.VIEW)))
					if (add_children(sysEnv, oc, co, privCheck))
						didReject = true;
			}
		}
		return didReject;
	}

	private boolean checkChildren(SystemEnvironment sysEnv, SDMSProxy o, boolean privCheck)
		throws SDMSException
	{
		Vector iv;
		int i;
		Long id = o.getId(sysEnv);
		Long coId;
		SDMSProxy co;

		iv = getChildren(sysEnv, id);
		for(i=0; i<iv.size(); i++) {
			co = (SDMSProxy) iv.get(i);
			SDMSPrivilege priv = co.getPrivileges(sysEnv);
			if (!privCheck || priv.can(SDMSPrivilege.VIEW) || priv.can(SDMSPrivilege.CREATE_PARENT_CONTENT)) {
				coId = co.getId(sysEnv);
				if (checkValid(sysEnv, co) || checkChildren(sysEnv, co, privCheck))
					return true;
			}
		}
		return false;
	}

	public boolean checkValid(SystemEnvironment sysEnv, SDMSProxy co)
		throws SDMSException
	{
		boolean valid = true;
		if (useFilter) {
			valid = objFilter.doFilter(sysEnv, co);
		}
		return valid;
	}

}

