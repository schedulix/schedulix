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

public class CreateRsp extends Node
{

	public final static String __version = "@(#) $Id: CreateRsp.java,v 2.1.4.1 2013/03/14 10:24:27 ronald Exp $";

	private String name;
	private WithHash with;
	private boolean replace;

	public CreateRsp(String n, WithHash w, Boolean r)
	{
		super();
		name = n;
		with = w;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{

		SDMSResourceStateProfile rsp;
		try {
			rsp = SDMSResourceStateProfileTable.table.create (sysEnv, name, null);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterRsp arsp = new AlterRsp(name, with, Boolean.FALSE);
				arsp.setEnv(env);
				arsp.go(sysEnv);
				result = arsp.result;
				return;
			} else {
				throw dke;
			}
		}

		Long rspId = rsp.getId(sysEnv);
		Long rsdId;

		Vector states = (Vector) with.get(ParseStr.S_STATUS);
		if(states == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201102145",
			                               "States missing"));
		}

		String rs;
		Iterator i = states.iterator();
		while (i.hasNext()) {
			rs = (String)i.next();
			rsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, rs).getId(sysEnv);

			try {
				SDMSResourceStateTable.table.create (sysEnv, rsdId, rspId);
			} catch (DuplicateKeyException dke) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03110120918",
				                               "State $1 specified more than once", rs));
			}
		}

		Long d_rsdId = null;
		String initState = (String) with.get(ParseStr.S_INITIAL_STATUS);
		if (initState != null) {
			d_rsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, initState).getId(sysEnv);

			rsp.setInitialRsdId (sysEnv, d_rsdId);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03201222249", "Resource State Profile created"));
	}
}

