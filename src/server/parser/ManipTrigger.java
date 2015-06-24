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

public abstract class ManipTrigger extends Node
{

	public final static String __version = "@(#) $Id: ManipTrigger.java,v 2.7.4.1 2013/03/14 10:24:40 ronald Exp $";

	protected Vector folderpath;
	protected Long seId;
	protected Long mainSeId;
	protected Long parentSeId;
	protected Boolean active;
	protected Integer action;
	protected Boolean isInverse;
	protected int iaction;
	protected Integer triggertype;
	protected Boolean isMaster;
	protected Boolean isSuspend;
	protected Boolean isCreate;
	protected Boolean isChange;
	protected Boolean isDelete;
	protected Boolean isGroup;
	protected Object resumeObj;
	protected String resumeAt = null;
	protected Integer resumeIn = null;
	protected Integer resumeBase = null;
	protected Boolean isWarnOnLimit;
	protected Integer maxRetry;
	protected Vector rscstate;
	protected Vector state;
	protected String condition;
	protected WithHash check;
	protected Integer checkAmount;
	protected Integer checkBase;

	public ManipTrigger()
	{
		super();
	}

	protected void analyzeResumeObj(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (resumeObj == null) return;
		if (resumeObj instanceof WithHash) {
			resumeIn = (Integer) ((WithHash) resumeObj).get(ParseStr.S_MULT);
			resumeBase = (Integer) ((WithHash) resumeObj).get(ParseStr.S_INTERVAL);
		} else {

			resumeAt = (String) resumeObj;
		}
	}

	protected void checkUniqueness(SystemEnvironment sysEnv, Long seId)
		throws SDMSException
	{
		Vector tv = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
		int cnt = 0;

		for (int i = 0; i < tv.size(); ++i) {
			SDMSTrigger t = (SDMSTrigger) tv.get(i);
			if (t.getAction(sysEnv).intValue() == SDMSTrigger.RERUN) ++cnt;
		}

		if (cnt > 1)
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03108111353", "Only one rerun trigger per job allowed"));
	}

	public abstract void go(SystemEnvironment sysEnv)
		throws SDMSException;
}

