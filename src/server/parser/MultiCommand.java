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

public class MultiCommand extends Node
{

	public final static String __version = "@(#) $Id: MultiCommand.java,v 2.4.2.1 2013/03/14 10:24:41 ronald Exp $";

	private Vector cmdlist;
	private int last = -1;
	private boolean commit;

	public MultiCommand(Vector v, boolean c)
	{
		super();
		cmdlist = v;
		cmdtype = ANY_COMMAND;
		commit = c;
	}

	public String getName()
	{
		String result = "Multicommand (" + last + "/" + cmdlist.size() + ")";
		return result;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		for(last = 0; last<cmdlist.size(); last++) {
			Node n;
			sysEnv.tx.beginSubTransaction(sysEnv);
			n = (Node) cmdlist.get(last);
			while(true) {
				if(env.isUser()) {
					if((n.cmdtype & USER_COMMAND) != 0) break;
				} else if(env.isJobServer()) {
					if((n.cmdtype & SERVER_COMMAND) != 0) break;
				} else {
					if((n.cmdtype & JOB_COMMAND) != 0) break;
				}
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03304101137", "Illegal commandtype within multicommand"));
			}
			n.env = env;
			try {
				n.go(sysEnv);
			} catch (SDMSException e) {
				SDMSMessage m = e.toSDMSMessage();
				m.setMessage("Error in Statement " + (last+1) + " (" + n.getName() + ") : " + m.getMessage());
				e.setMessage(m);
				throw e;
			}
			sysEnv.tx.commitSubTransaction(sysEnv);
		}
		if (commit)
			result.setFeedback(new SDMSMessage(sysEnv, "03204041823", "$1 Command(s) processed", new Integer (cmdlist.size())));
		else {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03704251206", "Abort by user request (Rollback option specified)"));
		}
	}

}

