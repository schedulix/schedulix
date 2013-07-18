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
import de.independit.scheduler.server.util.*;

public abstract class ManipGroup extends Node
{

	public final static String __version = "@(#) $Id";

	protected final static Long ZERO = new Long(0L);

	protected ObjectURL url;
	protected Vector userlist;
	protected Vector addlist;
	protected Vector dellist;
	protected WithHash with;
	private boolean withEvaluated = false;

	public ManipGroup(ObjectURL u, WithHash w)
	{
		super();
		with = w;
		url = u;
		userlist = new Vector();
		addlist = new Vector();
		dellist = new Vector();
	}

	public abstract void go(SystemEnvironment sysEnv)
	throws SDMSException;

	protected void evaluate_with(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long zero = new Long(0);
		if(withEvaluated) return;

		if(with.containsKey(ParseStr.S_USERLIST)) {
			Vector v = (Vector) with.get(ParseStr.S_USERLIST);
			Long uId;
			for(int i = 0; i < v.size(); i++) {
				uId = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) v.get(i), zero)).getId(sysEnv);
				userlist.add(uId);
			}
		}

		if(with.containsKey(ParseStr.S_ADDUSER)) {
			Vector v = (Vector) with.get(ParseStr.S_ADDUSER);
			Long uId;
			for(int i = 0; i < v.size(); i++) {
				uId = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) v.get(i), zero)).getId(sysEnv);
				addlist.add(uId);
			}
		}

		if(with.containsKey(ParseStr.S_DELUSER)) {
			Vector v = (Vector) with.get(ParseStr.S_DELUSER);
			Long uId;
			for(int i = 0; i < v.size(); i++) {
				uId = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) v.get(i), zero)).getId(sysEnv);
				dellist.add(uId);
			}
		}

		withEvaluated = true;
	}
}

