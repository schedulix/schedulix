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

public abstract class Node
{

	public static final String __version = "@(#) $Id: Node.java,v 2.2.8.1 2013/03/14 10:24:42 ronald Exp $";

	public final static int USER_COMMAND = 1;
	public final static int SERVER_COMMAND = 2;
	public final static int JOB_COMMAND = 4;
	public final static int ANY_COMMAND = 7;

	protected ConnectionEnvironment env;
	public int txMode;
	public SDMSOutput result;
	public int cmdtype;
	public Long contextVersion;
	public boolean auditFlag;

	public Node()
	{
		txMode = SDMSTransaction.READWRITE;
		auditFlag = true;
		cmdtype = USER_COMMAND;
		result = new SDMSOutput();
		contextVersion = null;
	}

	public String getName()
	{
		String s = this.getClass().getName();
		return s.substring(s.lastIndexOf('.')+1);
	}

	public void getLock()
	{
		SystemEnvironment.getSharedLock();
	}

	public void releaseLock()
	{
		SystemEnvironment.releaseSharedLock();
	}

	public abstract void go(SystemEnvironment sysEnv)
		throws SDMSException;

	public void setContextVersion(Long l)
	{
		contextVersion = l;
	}

	public void setEnv(ConnectionEnvironment e)
	{
		env = e;
	}

	public ConnectionEnvironment getEnv()
	{
		return env;
	}

	public void render(PrintStream o)
		throws FatalException
	{
		env.renderer().render(o, result);
	}
}
