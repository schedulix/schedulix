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

public class AlterRsp extends Node
{

	public final static String __version = "@(#) $Id: AlterRsp.java,v 2.2.4.1 2013/03/14 10:24:22 ronald Exp $";

	private String name;
	private WithHash with;
	private boolean noerr;

	public AlterRsp(String n, WithHash w, Boolean ne)
	{
		super();
		name = n;
		with = w;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{

		SDMSResourceStateProfile rsp;
		try {
			rsp = SDMSResourceStateProfileTable.idx_name_getUnique (sysEnv, name);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122340", "No Resource State Profile altered"));
				return;
			}
			throw nfe;
		}

		Long rspId = rsp.getId(sysEnv);
		Long rsdId;
		Long initialRsdId = null;
		Vector v;

		Vector states = (Vector) with.get(ParseStr.S_STATUS);
		if(states == null) {

		} else {
			Iterator i;
			SDMSResourceState rs;
			String rsn;

			v = SDMSResourceStateTable.idx_rspId.getVector(sysEnv, rspId);
			i = v.iterator();
			while(i.hasNext()) {
				rs = (SDMSResourceState) i.next();
				rs.delete(sysEnv);
			}

			i = states.iterator();
			while (i.hasNext()) {
				rsn = (String)i.next();
				rsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, rsn).getId(sysEnv);

				try {
					SDMSResourceStateTable.table.create (sysEnv, rsdId, rspId);
				} catch (DuplicateKeyException dke) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03110120920",
					                               "State $1 specified more than once", rsn));
				}
			}
		}

		String initState = (String) with.get(ParseStr.S_INITIAL_STATUS);
		if (initState != null) {
			initialRsdId = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, initState).getId(sysEnv);

			rsp.setInitialRsdId (sysEnv, initialRsdId);
		}

		v = SDMSNamedResourceTable.idx_rspId.getVector(sysEnv, rspId);
		for(int i = 0; i < v.size(); i++) {
			Long nrId = ((SDMSNamedResource) v.get(i)).getId(sysEnv);
			Vector w = SDMSResourceTable.idx_nrId.getVector(sysEnv, nrId);
			for(int j = 0; j < w.size(); j++) {
				SDMSResource r = ((SDMSResource) w.get(j));
				rsdId = r.getRsdId(sysEnv);
				if(rsdId.equals(initialRsdId)) continue;
				if(!SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId))) {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03209181659",
					                               "Profile does not contain state $1 of resource $2->$3",
					                               SDMSResourceStateDefinitionTable.getObject(sysEnv, r.getRsdId(sysEnv)).getName(sysEnv),
					                               SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv)),
					                               ((SDMSNamedResource) v.get(i)).pathVector(sysEnv)));

				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03203311351", "Resource State Profile altered"));
	}
}

