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

public class MoveScope extends Node
{

	public final static String __version = "@(#) $Id: MoveScope.java,v 2.3.2.1 2013/03/14 10:24:41 ronald Exp $";

	private Vector path1 = null;
	private String name1 = null;
	private Vector name2;
	private ObjectURL url;
	private String newName;

	public MoveScope(ObjectURL u, Vector to)
	{
		super();
		url = u;
		name2 = to;
		newName = null;
	}

	public MoveScope(ObjectURL u, String to)
	{
		super();
		url = u;
		name2 = null;
		newName = to;
	}

	private void moveScope(SystemEnvironment sysEnv, SDMSScope f)
	throws SDMSException
	{
		SDMSScope p;
		String name;
		Long npId, opId, myId;

		myId = f.getId(sysEnv);
		opId = f.getParentId(sysEnv);
		try {
			p = SDMSScopeTable.getScope(sysEnv, name2);

			name = f.getName(sysEnv);

		} catch(NotFoundException nfe) {
			name = (String) name2.remove(name2.size() -1);
			p = SDMSScopeTable.getScope(sysEnv, name2);

		}

		npId = p.getId(sysEnv);
		if(p.getType(sysEnv).intValue() == SDMSScope.SERVER) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201291039", "A scope cannot be moved below a Server"));
		}

		if(npId.equals(myId)) {

			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201291038", "A Scope cannot be moved below itself"));
		}
		Long id;
		while((id = p.getParentId(sysEnv)) != null) {
			if(id.equals(myId)) {

				throw new CommonErrorException(new SDMSMessage(sysEnv, "03201291037", "A Scope cannot be moved below itself"));
			}
			p = SDMSScopeTable.getObject(sysEnv, id);
		}

		f.set_ParentIdName(sysEnv, npId, name);

		result.setFeedback(new SDMSMessage(sysEnv, "03201291036", "Scope moved"));
	}

	private void renameScope(SystemEnvironment sysEnv, SDMSScope f)
	throws SDMSException
	{
		f.setName(sysEnv, newName);

		result.setFeedback(new SDMSMessage(sysEnv, "03112161152", "Scope renamed"));
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSScope f, p;
		String name;
		Long npId, opId, myId;

		f = (SDMSScope) url.resolve(sysEnv);

		if (newName == null)
			moveScope(sysEnv, f);
		else
			renameScope(sysEnv, f);

		ScopeConfig.markAltered (sysEnv, f);

		SystemEnvironment.sched.notifyChange(sysEnv, f, SchedulingThread.MOVE);
	}
}

