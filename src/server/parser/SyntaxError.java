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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class SyntaxError extends Node
{

	public final static String __version = "@(#) $Id: SyntaxError.java,v 2.1.8.1 2013/03/14 10:24:53 ronald Exp $";

	private String message;
	private String exp_message;

	public SyntaxError(String p_message)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		message = p_message;
		exp_message = "$1\nSyntax Error: expected $2";
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public SyntaxError(String p_exp_message, String p_message)
	{
		super();
		cmdtype = Node.ANY_COMMAND;
		message = p_message;
		exp_message = p_exp_message;
		txMode = SDMSTransaction.READONLY;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSMessage m = new SDMSMessage (sysEnv, "01111281413", exp_message, env.actstmt, message);
		SDMSThread.doTrace(env, m.toString(), SDMSThread.SEVERITY_WARNING);
		throw new CommonErrorException (m);
	}
}

