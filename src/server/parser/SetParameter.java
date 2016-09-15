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

public class SetParameter extends Node
{

	public final static String __version = "@(#) $Id: SetParameter.java,v 2.4.14.1 2013/03/14 10:24:48 ronald Exp $";

	private WithHash parms;
	private Long jobid;
	private String key;
	private String auditComment;

	public SetParameter(WithHash w)
	{
		super();
		cmdtype = Node.JOB_COMMAND;
		parms = w;
		jobid = null;
		auditComment = null;
	}

	public SetParameter(WithHash w, Long j, String k, WithHash ac)
	{
		super();
		cmdtype = Node.USER_COMMAND;
		parms = w;
		jobid = j;
		key = k;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (jobid == null) jobid = sysEnv.cEnv.uid();
		if (key != null) {
			Connect.validateJobConnect(sysEnv, jobid, key, true);
		}

		final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobid);

		final Vector keyList = new Vector (parms.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String name = (String) keyList.get (i);
			final Vector tmp = (Vector) parms.get (name);
			final String value = (String) tmp.get(1);

			sme.setVariableValue(sysEnv, name, value);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03206060017", "Parameter set"));
	}
}

