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

public class GetParameter extends Node
{

	public final static String __version = "@(#) $Id: GetParameter.java,v 2.4.8.2 2013/06/18 09:49:31 ronald Exp $";

	String name;
	String mode;
	Long id;

	public GetParameter(String n, String m)
	{
		super();
		cmdtype = Node.JOB_COMMAND;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		name = n;
		mode = m;
		id = null;
	}

	public GetParameter(String n, String m, Long i)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		name = n;
		mode = m;
		id = i;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Vector v = new Vector();
		String s;

		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, (id == null ? sysEnv.cEnv.uid() : id));
		if(id != null && !sme.checkPrivileges(sysEnv, SDMSPrivilege.MONITOR))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03406012220", "Insufficient privileges"));

		desc.add("VALUE");

		try {
			s = sme.getVariableValue(sysEnv, name, false, mode, true);
		} catch (NotFoundException nfe) {

			throw new NotFoundException(new SDMSMessage(sysEnv, "03209231453", "Couldn't resolve Parameter $1", name));
		}
		v.add(s);
		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03206060008", "Parameter"), desc, v);
		result.setOutputContainer(d_container);
		result.setFeedback(new SDMSMessage(sysEnv, "03206060023", "Got Parameter"));
	}
}

