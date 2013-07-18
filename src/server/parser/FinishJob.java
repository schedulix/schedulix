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

public class FinishJob extends Node
{

	public final static String __version = "@(#) $Id: FinishJob.java,v 2.2.4.1 2013/03/14 10:24:33 ronald Exp $";

	Integer exitcode;
	Long	jobId;
	String  errmsg;

	public FinishJob(Long jid, Integer e, String err)
	{
		super();
		jobId = jid;
		exitcode = e;
		errmsg = err;
	}

	public FinishJob(Long jid, Integer e)
	{
		super();
		jobId = jid;
		exitcode = e;
		errmsg = null;
	}

	public FinishJob(Integer e, String err)
	{
		super();
		jobId = env.uid();
		exitcode = e;
		errmsg = err;
	}

	public FinishJob(Integer e)
	{
		super();
		jobId = env.uid();
		exitcode = e;
		errmsg = null;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobId);
		sme.finishJob(sysEnv, exitcode, errmsg, null );

		SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.FINISH);
		result.setFeedback(new SDMSMessage(sysEnv, "02201171808","Job finished"));
	}

}
