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

public class StopServer extends Node
{

	public final static String __version = "@(#) $Id: StopServer.java,v 2.2.6.1 2013/03/14 10:24:53 ronald Exp $";

	public StopServer()
	{
		super();
	}

	public StopServer(ConnectionEnvironment ce, boolean b)
	{
		super();
		if(!ce.gid().contains(SDMSObject.adminGId))
			return;

		if(b) System.exit(0);
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		env.tx.mode = SDMSTransaction.READONLY;
		if(!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			Iterator i = sysEnv.cEnv.gid().iterator();
			SDMSPrivilege p = new SDMSPrivilege();
			while (i.hasNext()) {
				Long gId = (Long) i.next();
				try {
					SDMSGrant g = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(SDMSConstants.lZERO, gId));
					p.addPriv(sysEnv, g.getPrivs(sysEnv).longValue());
				} catch (NotFoundException nfe) {

				}
			}
			if (!p.can(SDMSPrivilege.MANAGE_SYS))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03203272214", "Insufficient Privileges"));
		}
		SystemEnvironment.server.shutdown();
		result.setFeedback(new SDMSMessage(sysEnv, "03201212332", "Server shut down"));
	}
}

