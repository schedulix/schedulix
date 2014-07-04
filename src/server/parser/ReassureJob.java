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
import de.independit.scheduler.server.parser.cmdline.*;
import de.independit.scheduler.jobserver.RepoIface;

public class ReassureJob extends JobDistribution
{

	public final static String __version = "@(#) $Id: ReassureJob.java,v 2.2.14.1 2013/03/14 10:24:43 ronald Exp $";

	Long	jobId;

	public ReassureJob(Long id)
	{
		super();
		jobId = id;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSScope s;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Vector data = new Vector();

		desc.add (RepoIface.COMMAND);

		s = SDMSScopeTable.getObject(sysEnv, sysEnv.cEnv.uid());
		if(s.getIsTerminate(sysEnv).booleanValue()) {
			data.add(RepoIface.CMD_SHUTDOWN);
			s.setIsTerminate(sysEnv, Boolean.FALSE);
		} else {
			search_job(sysEnv, s, desc, data);
		}

		d_container = new SDMSOutputContainer(sysEnv, "Jobserver Command", desc, data);

		result.setOutputContainer(d_container);
	}

	private void search_job(SystemEnvironment sysEnv, SDMSScope s, Vector desc, Vector data)
		throws SDMSException
	{

		SDMSSubmittedEntity sme;
		SDMSKillJob kj = null;
		try {
			sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobId);
		} catch (NotFoundException nfe) {

			try {
				kj = SDMSKillJobTable.getObject(sysEnv, jobId);
			} catch(NotFoundException nfekj) {

				data.add(RepoIface.CMD_DISPOSE);
				return;
			}
			process_killjob(sysEnv, s, kj, desc, data);
			return;
		}
		process_job(sysEnv, s, sme, desc, data);
		return;
	}

	private void process_job(SystemEnvironment sysEnv, SDMSScope s, SDMSSubmittedEntity sme, Vector desc, Vector data)
		throws SDMSException
	{

		int status = sme.getState(sysEnv).intValue();
		if(status != SDMSSubmittedEntity.STARTING && status != SDMSSubmittedEntity.STARTED) {

			data.add("DISPOSE");
			return;
		}
		Long sId = sme.getScopeId(sysEnv);
		if(sId == null || !sId.equals(s.getId(sysEnv))) {

			data.add("DISPOSE");
			return;
		}

		sysEnv.tx.beginSubTransaction(sysEnv);
		if(! startJob(sysEnv, sme, s, desc, data)) {

		}
	}

	private void process_killjob(SystemEnvironment sysEnv, SDMSScope s, SDMSKillJob kj, Vector desc, Vector data)
		throws SDMSException
	{

		int status = kj.getState(sysEnv).intValue();
		if(status != SDMSSubmittedEntity.STARTING && status != SDMSSubmittedEntity.STARTED) {

			data.add("DISPOSE");
			return;
		}
		Long sId = kj.getScopeId(sysEnv);
		if(sId == null || !sId.equals(s.getId(sysEnv))) {

			data.add("DISPOSE");
			return;
		}

		sysEnv.tx.beginSubTransaction(sysEnv);
		if(! startKillJob(sysEnv, kj, s, desc, data)) {

		}
	}

}

