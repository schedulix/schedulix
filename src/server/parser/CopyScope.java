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

public class CopyScope extends Node
{

	public final static String __version = "@(#) $Id: CopyScope.java,v 2.4.2.1 2013/03/14 10:24:24 ronald Exp $";

	private Vector name2;
	private ObjectURL url;
	private String newName;

	public CopyScope(ObjectURL u, Vector to)
	{
		super();
		url = u;
		name2 = to;
		newName = null;
	}

	public CopyScope(ObjectURL u, String to)
	{
		super();
		url = u;
		name2 = null;
		newName = to;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSScope f, p;
		String name;
		Long npId, opId, myId;

		f = (SDMSScope) url.resolve(sysEnv);
		opId = f.getParentId(sysEnv);
		if (opId == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03112191230", "The scope GLOBAL cannot be copied"));
		}

		if (newName == null) {
			try {
				p = SDMSScopeTable.getScope(sysEnv, name2);

				name = f.getName(sysEnv);

			} catch(NotFoundException nfe) {
				name = (String) name2.remove(name2.size() -1);
				p = SDMSScopeTable.getScope(sysEnv, name2);

			}
		} else {
			p = SDMSScopeTable.getObject(sysEnv, opId);
			name = newName;
		}

		if(p.getType(sysEnv).intValue() == SDMSScope.SERVER) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02204171911", "A scope cannot be copied below a Server"));
		}
		npId = p.getId(sysEnv);
		if (SDMSScopeTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(npId, name))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03112191233", "A scope with name $1 already exists within $2", name, p.pathString(sysEnv)));
		}
		myId = f.getId(sysEnv);

		if(npId.equals(myId)) {

			throw new CommonErrorException(new SDMSMessage(sysEnv, "02204171912", "A Scope cannot be copied below itself"));
		}
		Long id;
		while((id = p.getParentId(sysEnv)) != null) {
			if(id.equals(myId)) {

				throw new CommonErrorException(new SDMSMessage(sysEnv, "02204171913", "A Scope cannot be copied below itself"));
			}
			p = SDMSScopeTable.getObject(sysEnv, id);
		}

		f.copy(sysEnv, npId, name);

		SystemEnvironment.sched.notifyChange(sysEnv, (SDMSScope) null, SchedulingThread.COPY);

		result.setFeedback(new SDMSMessage(sysEnv, "03202201950", "Scope copied"));
	}
}

