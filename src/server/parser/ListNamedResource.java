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

public class ListNamedResource extends Node
	implements Formatter
{

	public final static String __version = "@(#) $Id: ListNamedResource.java,v 2.11.2.2 2013/06/18 09:49:33 ronald Exp $";

	Vector path;
	WithHash with;
	HashSet expandIds;

	public ListNamedResource()
	{
		super();
		path = null;
		expandIds = new HashSet();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListNamedResource(Vector p, WithHash w)
	{
		super();
		path = p;
		with = w;

		if (with.containsKey(ParseStr.S_EXPAND)) {
			expandIds = (HashSet) with.get(ParseStr.S_EXPAND);
		} else	expandIds = new HashSet();

		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public Vector fillHeadInfo()
	{
		Vector desc = new Vector();
		desc.add("ID");
		desc.add("NAME");
		desc.add("OWNER");
		desc.add("USAGE");
		desc.add("RESOURCE_STATE_PROFILE");
		desc.add("FACTOR");
		desc.add("SUBCATEGORIES");
		desc.add("RESOURCES");
		desc.add("PRIVS");
		desc.add("IDPATH");
		return desc;
	}

	public Vector fillVector(SystemEnvironment sysEnv, SDMSProxy co, HashSet parentSet)
		throws SDMSException
	{
		Vector v = new Vector();
		fillVector(sysEnv, (SDMSNamedResource) co, v);
		return v;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSNamedResource nr;
		if (path == null)
			nr = SDMSNamedResourceTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(null, "RESOURCE"));
		else {
			nr = SDMSNamedResourceTable.getNamedResource(sysEnv, path);
		}
		Long rootId = nr.getId(sysEnv);
		if (expandIds != null) {
			if (! expandIds.contains(rootId)) {
				expandIds.add (rootId);
			}
		}

		SDMSOutputContainer d_container = null;
		NamedResourceLister nrl = new NamedResourceLister(path, expandIds);
		nrl.setTitle(new SDMSMessage(sysEnv, "03207191657", "List of Named Resources"));
		nrl.setFormatter(this);

		if(with != null && with.containsKey(ParseStr.S_FILTER)) {
			nrl.objFilter.initialize_filter(sysEnv, (Vector) with.get(ParseStr.S_FILTER), 0, true);
			nrl.useFilter = true;
		}
		d_container = nrl.list(sysEnv);
		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage (sysEnv, "02111101415",
				"$1 Named Resource(s) found", new Integer(d_container.lines)));
	}

	private void fillVector(SystemEnvironment sysEnv, SDMSNamedResource nr, Vector v)
		throws SDMSException
	{
		int subcat, res;

		v.add(nr.getId(sysEnv));
		v.add(nr.pathVector(sysEnv));

		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, nr.getOwnerId(sysEnv));
		v.add(g.getName(sysEnv));

		v.add(nr.getUsageAsString(sysEnv));
		Long rspId = nr.getRspId(sysEnv);
		if(rspId != null) {
			SDMSResourceStateProfile rsp = SDMSResourceStateProfileTable.getObject(sysEnv, rspId);
			v.add(rsp.getName(sysEnv));
		} else {
			v.add(null);
		}
		v.add(nr.getFactor(sysEnv));
		subcat = 0;
		res = 0;
		Vector iv = SDMSNamedResourceTable.idx_parentId.getVector(sysEnv, nr.getId(sysEnv));
		for(int i=0; i<iv.size(); i++) {
			SDMSNamedResource tnr = (SDMSNamedResource) iv.get(i);
			if(tnr.getUsage(sysEnv).intValue() == SDMSNamedResource.CATEGORY)	subcat++;
			else									res++;
		}
		v.add(new Integer(subcat));
		v.add(new Integer(res));
		v.add(nr.getPrivileges(sysEnv).toString());
		v.add(nr.idPathVector(sysEnv));
	}
}

