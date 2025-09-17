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
package de.independit.scheduler.server.util;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.parser.triggerexpr.*;

public class BoolExpr
{
	public static final String __version = "@(#) $Id: BoolExpr.java,v 2.4.8.1 2013/03/14 10:25:28 ronald Exp $";
	private static final int BOOLEAN = 1;
	private static final int ANYTYPE = 0;

	final String condition;
	final StringReader sr;
	ExprScanner exprs;
	ExprParser  exprp;

	public BoolExpr(String c)
	{
		condition = c;
		if(condition != null)
			sr = new StringReader(condition);
		else
			sr = null;
		exprs = null;
		exprp = null;
	}

	private void initParser(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSSubmittedEntity requiredSme, SDMSResource r, SDMSTrigger t, SDMSTriggerQueue tq, SDMSScope s, boolean checkOnly)
	throws SDMSException, IOException
	{
		if (sr == null) {
			exprs = null;
			exprp = null;
			return;
		}
		if(exprs == null) {
			exprs = new ExprScanner(sr);
			exprp = new ExprParser();
		} else {
			sr.reset();
			exprs = new ExprScanner(sr);
		}
		exprp.set(sysEnv, sme, requiredSme, r, t, tq, s);
		exprp.checkOnly = checkOnly;
	}

	public void checkConditionSyntax(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSMessage msg = null;
		if(condition == null) return;

		try {
			initParser(sysEnv, null, null, null, null, null, null, true);
			exprp.yyparse(exprs);
		} catch (Exception e) {
			msg = new SDMSMessage(sysEnv, "03602151611", "Syntax Error ($1) while parsing '$2'", e.toString(), condition);
		} catch (Error e) {
			msg = new SDMSMessage(sysEnv, "03602151612", "Unexpected Error ($1) while parsing '$2'", e.toString(), condition);
		}
		if(msg != null) throw new CommonErrorException(msg);
	}

	public boolean checkCondition(SystemEnvironment sysEnv, SDMSResource r, SDMSSubmittedEntity sme, SDMSSubmittedEntity requiredSme, SDMSTrigger t, SDMSTriggerQueue tq, SDMSScope s)
	throws SDMSException
	{
		Boolean retval;

		if(condition == null) return true;

		retval = (Boolean) evaluate(sysEnv, r, sme, requiredSme, t, tq, s, BOOLEAN);

		return retval.booleanValue();
	}

	public Object evalExpression(SystemEnvironment sysEnv, SDMSResource r, SDMSSubmittedEntity sme, SDMSSubmittedEntity requiredSme, SDMSTrigger t, SDMSTriggerQueue tq, SDMSScope s)
	throws SDMSException
	{
		if (condition == null) return null;
		return evaluate(sysEnv, r, sme, requiredSme, t, tq, s, ANYTYPE);
	}

	private Object evaluate(SystemEnvironment sysEnv, SDMSResource r, SDMSSubmittedEntity sme, SDMSSubmittedEntity requiredSme, SDMSTrigger t, SDMSTriggerQueue tq, SDMSScope s, int returnType)
	throws SDMSException
	{
		Object rc = null;
		SDMSMessage msg = null;

		if(condition == null) return null;

		try {
			long now = System.currentTimeMillis();
			initParser(sysEnv, sme, requiredSme, r, t, tq, s, false);
			if (exprp == null) return null;
			if (returnType == BOOLEAN)
				rc = exprp.parseBoolExpr(exprs);
			else
				rc = exprp.parseObjExpr(exprs);
			now = System.currentTimeMillis() - now;
		} catch (IOException ioe) {
			msg = new SDMSMessage(sysEnv, "03506171435", "I/O Error parsing '$1'", condition);
		} catch (NotFoundException nfe) {
			msg = new SDMSMessage(sysEnv, "03506171436", "Error resolving Parameter: $1", nfe);
		} catch (CommonErrorException cce) {
			msg = new SDMSMessage(sysEnv, "03506171437", "Error parsing '$1': $2", condition, cce);
		} catch (de.independit.scheduler.server.parser.triggerexpr.ExprParser.yyException yye) {
			msg = new SDMSMessage(sysEnv, "03506171438", "Parse error while parsing '$1'", condition);
		} catch (Exception e) {
			msg = new SDMSMessage(sysEnv, "03506171439", "Exception ($1) occurred while calculating '$2'", e.toString(), condition);
		} catch (Error e) {
			msg = new SDMSMessage(sysEnv, "03506171440", "Error Exception parsing '$1'", condition);
		}
		if(msg != null) {
			SDMSThread.doTrace(sysEnv.cEnv, msg.toString(), SDMSThread.SEVERITY_WARNING);
			throw new CommonErrorException(msg);
		}
		return rc;
	}

}
