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
import de.independit.scheduler.jobserver.Config;

public class ListScope extends Node
	implements Formatter
{

	public final static String __version = "@(#) $Id: ListScope.java,v 2.10.2.2 2013/06/18 09:49:34 ronald Exp $";

	ObjectURL url;
	HashSet expandIds;

	public ListScope(ObjectURL u)
	{
		super();
		url = u;
		expandIds = new HashSet();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListScope(ObjectURL u, HashSet e)
	{
		super();
		url = u;
		expandIds = e;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public Vector fillHeadInfo()
	{
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("NAME");
		desc.add("OWNER");
		desc.add("TYPE");
		desc.add("IS_TERMINATE");
		desc.add("HAS_ALTERED_CONFIG");
		desc.add("IS_SUSPENDED");
		desc.add("IS_ENABLED");
		desc.add("IS_REGISTERED");
		desc.add("IS_CONNECTED");
		desc.add("STATE");
		desc.add("PID");
		desc.add("NODE");
		desc.add("IDLE");
		desc.add("NOPDELAY");
		desc.add("ERRMSG");

		desc.add("SUBSCOPES");
		desc.add("RESOURCES");
		desc.add("PRIVS");
		desc.add("IDPATH");

		return desc;
	}

	public Vector fillVector(SystemEnvironment sysEnv, SDMSProxy co, HashSet parentSet)
		throws SDMSException
	{
		Vector v = new Vector();
		fillVector(sysEnv, (SDMSScope) co, v);
		return v;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (url.path == null) {
			url.path = ((SDMSScope)url.resolve(sysEnv)).pathVector(sysEnv);
		}
		ScopeLister sl = new ScopeLister(url, expandIds);
		sl.setTitle(new SDMSMessage(sysEnv, "03207191656", "List of Scopes"));
		sl.setFormatter(this);

		SDMSOutputContainer d_container = sl.list(sysEnv);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03201281436",
		                                   "$1 Object(s) found", Integer.valueOf(d_container.lines)));
	}

	private void fillVector(SystemEnvironment sysEnv, SDMSScope s, Vector v)
		throws SDMSException
	{
		v.add(s.getId(sysEnv));
		v.add(s.pathVector(sysEnv));
		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, s.getOwnerId(sysEnv));
		v.add(g.getName(sysEnv));
		v.add(s.getTypeAsString(sysEnv));
		v.add(s.getIsTerminate(sysEnv));
		v.add(s.getHasAlteredConfig(sysEnv));
		v.add(s.getIsSuspended(sysEnv));
		v.add(s.getIsEnabled(sysEnv));
		v.add(s.getIsRegistered(sysEnv));
		v.add(Boolean.valueOf(s.isConnected(sysEnv)));
		v.add(s.getStateAsString(sysEnv));
		v.add(s.getPid(sysEnv));
		v.add(s.getNode(sysEnv));
		v.add(Long.valueOf(s.getIdle(sysEnv)));
		v.add(ScopeConfig.getItem(sysEnv, s, Config.NOP_DELAY));
		v.add(s.getErrmsg(sysEnv));
		Vector v1 = SDMSScopeTable.idx_parentId.getVector(sysEnv, s.getId(sysEnv));
		Vector v2 = SDMSResourceTable.idx_scopeId.getVector(sysEnv, s.getId(sysEnv));
		v.add(Integer.valueOf(v1.size()));
		v.add(Integer.valueOf(v2.size()));
		v.add(s.getPrivileges(sysEnv).toString());
		v.add(s.idPathVector(sysEnv));
	}

}

