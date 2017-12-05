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

public abstract class TreeLister implements Comparator
{

	public static final String __version = "@(#) $Id: TreeLister.java,v 1.1.14.1 2013/03/14 10:24:54 ronald Exp $";

	SDMSMessage title;

	protected void initLister(SDMSMessage t)
		throws SDMSException
	{
		title = t;
	}

	public TreeLister(SDMSMessage t)
		throws SDMSException
	{
		initLister(t);
	}

	public abstract Vector format(SystemEnvironment sysEnv, SDMSProxy o) throws SDMSException;
	public abstract Vector getDesc();
	public abstract Vector getChildren(SystemEnvironment sysEnv, SDMSProxy o) throws SDMSException;
	public int compare(Object o1, Object o2) { return 0; }

	public SDMSOutputContainer list(SystemEnvironment sysEnv, Vector rootObjects, HashSet expandList)
		throws SDMSException
	{
		Vector objs = (Vector)rootObjects.clone();
		Collections.sort(objs, this);
		SDMSOutputContainer container = new SDMSOutputContainer(sysEnv, title, getDesc());
		Iterator i = objs.iterator();
		while (i.hasNext()) {
			SDMSProxy o = (SDMSProxy)i.next();
			render(sysEnv, container, o, expandList);
		}
		return container;
	}

	protected void render(SystemEnvironment sysEnv, SDMSOutputContainer container, SDMSProxy o, HashSet expandList)
		throws SDMSException
	{
		container.addData(sysEnv, format(sysEnv, o));
		Long id = o.getId(sysEnv);
		if (expandList == null || expandList.contains(id)) {
			Vector children = getChildren(sysEnv, o);
			Collections.sort(children, this);
			Iterator i = children.iterator();
			while (i.hasNext()) {
				SDMSProxy c = (SDMSProxy)i.next();
				render(sysEnv, container, c, expandList);
			}
		}
	}
}

