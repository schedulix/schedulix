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

public class DropRsp extends Node
{

	public final static String __version = "@(#) $Id: DropRsp.java,v 2.1.14.1 2013/03/14 10:24:31 ronald Exp $";

	private String name;
	private boolean noerr;

	public DropRsp(String n, Boolean ne)
	{
		super();
		name = n;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateProfile rsp;

		try {
			rsp = SDMSResourceStateProfileTable.idx_name_getUnique(sysEnv, name);
		} catch(NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122338", "No Resource State Profile dropped"));
				return;
			}
			throw nfe;
		}

		Long rspId = rsp.getId(sysEnv);

		Vector v = SDMSNamedResourceTable.idx_rspId.getVector(sysEnv, rspId);

		if(v.size()>0) {

			throw new CommonErrorException (new SDMSMessage (sysEnv, "03201212212",
					"Resource State Profile is profile for Named Resource $1",
					((SDMSNamedResource)(v.elementAt(0))).getName(sysEnv) ));
		}

		v = SDMSResourceStateTable.idx_rspId.getVector(sysEnv, rspId);
		for(int i = 0; i < v.size(); i++) {
			SDMSResourceState rs = (SDMSResourceState) v.get(i);
			rs.delete(sysEnv);
		}

		rsp.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03201230009", "Resource State Profile dropped"));
	}
}

