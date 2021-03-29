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
import de.independit.scheduler.server.output.*;

public class SDMSParser extends Parser
{

	public final static String __version = "@(#) $Id: SDMSParser.java,v 2.7.2.1 2013/03/14 10:24:47 ronald Exp $";

	PrintStream ostream;
	ConnectionEnvironment env;
	SDMSOutput msgBuffer = new SDMSOutput();
	boolean	stmtInError = false;
	String  errmsg;
	String  errexp[];

	public SDMSParser(PrintStream o, ConnectionEnvironment e)
	{
		super();
		ostream = o;
		env = e;
	}

	public ConnectionEnvironment getConnectionEnvironment()
	{
		return env;
	}

	protected boolean post(Node n)
		throws FatalException
	{
		if(stmtInError) {
			stmtInError = false;
			n = createError();
		} else if(n.cmdtype != Node.ANY_COMMAND) {
			if(env.isUser() && (n.cmdtype & Node.USER_COMMAND) == 0) {
				errexp = new String[1];
				errexp[0] = "a user command";
				n = createError();
			} else if(env.isJobServer() && (n.cmdtype & Node.SERVER_COMMAND) == 0) {
				errexp = new String[1];
				errexp[0] = "a jobserver command";
				n = createError();
			} else if(env.isJob() && (n.cmdtype & Node.JOB_COMMAND) == 0) {
				errexp = new String[1];
				errexp[0] = "a job command";
				n = createError();
			}
		}

		n.setEnv(env);
		env.setLast();
		env.setState(ConnectionEnvironment.QUEUED);
		env.firstToken = null;
		if(n.txMode == SDMSTransaction.READWRITE) {
			env.cmdQueue().post(n);
		} else {
			env.roCmdQueue().post(n);
		}
		env.lock().do_wait();
		env.setState(ConnectionEnvironment.RENDERING);
		n.render(ostream);
		env.setLast();
		env.setState(ConnectionEnvironment.IDLE);
		env.actstmt = null;
		if(n.result.error != null) return false;
		return true;
	}

	protected Node createError()
		throws FatalException
	{
		SyntaxError n;

		StringBuffer msg = new StringBuffer("");

		if(errexp != null) {
			for(int i=0; i<errexp.length-1; ++i) {
				msg.append(errexp[i]+",\n");
			}
			msg.append(errexp[errexp.length-1]);
		}
		if(errmsg == null || errmsg.equals(ParseStr.S_SYNTAX_ERROR)) {
			n = new SyntaxError(msg.toString());
		} else {
			n = new SyntaxError(errmsg, msg.toString());
		}

		return n;
	}

	public void yyerror(String message, String[] exp)
	{
		System.out.println(message);
		if (exp != null)
			for(int i = 0; i < exp.length; ++i)
				System.out.println("\t" + exp[i]);
		if(stmtInError) return;
		stmtInError = true;
		env.proto_input("+++++");
		errmsg = message;
		errexp = exp;
	}

	protected String[] yyExpecting (int state)
	{
		String result[] = super.yyExpecting(state);
		if(result.length > 35) {
			result = new String[1];
			result[0] = "valid identifier";
		}
		return result;
	}

}
