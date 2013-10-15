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

public class CreateJobDefinition extends ManipJobDefinition
{

	private boolean replace;

	public CreateJobDefinition(Vector p, String n, WithHash w, Boolean r)
	{
		super(p, n, w, Boolean.FALSE);
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		evaluateWith(sysEnv);
		if(otype == null) {

			type = SDMSSchedulingEntity.JOB;
		} else {
			type = otype.intValue();
		}
		if(type == SDMSSchedulingEntity.JOB) {
			checkJob(sysEnv);
		} else {
			if(type == SDMSSchedulingEntity.BATCH) {
				checkBatch(sysEnv);
			} else {

				sysEnv.checkFeatureAvailability(SystemEnvironment.S_MILESTONES);
			}
		}
		if (!submitSuspended.booleanValue()) {
			if (resumeAt != null || resumeIn != null) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03108161106", "Resume option requires a suspend option"));
			}
		}

		if(SDMSFolderTable.idx_parentId_name.containsKey(sysEnv, new SDMSKey(folderId, name))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03201290918", "Object with name $1 already exists within $2",
			                                name, SDMSFolderTable.getObject(sysEnv, folderId).pathString(sysEnv)));
		}
		SDMSSchedulingEntity se;
		try {
			se = SDMSSchedulingEntityTable.table.create(sysEnv,
			                name,
			                folderId,
			                gId,
			                new Integer(type),
			                runProgram,
			                rerunProgram,
			                killProgram,
			                workdir,
			                logfile,
			                truncLog,
			                errlogfile,
			                truncErrlog,
			                expectedRuntime,
			                expectedFinaltime,
			                getExpectedRuntime,
			                priority,
			                minPriority,
			                agingAmount,
			                agingBase,
			                submitSuspended,
			                resumeAt,
			                resumeIn,
			                resumeBase,
			                masterSubmittable,
			                to_mult,
			                to_interval,
			                to_esdId,
			                sameNode,
			                gangSchedule,
			                dependencyOperation,
			                esmpId,
			                espId,
			                null,
			                neId,
			                fpId,
			                inheritPrivs
			                                           );
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterJobDefinition ajd = new AlterJobDefinition(path, name, withs, Boolean.FALSE);
				ajd.setEnv(env);
				ajd.go(sysEnv);
				result = ajd.result;
				return;
			} else {
				throw dke;
			}
		}

		long lpriv = inheritPrivs.longValue();
		if (!withs.containsKey(ParseStr.S_INHERIT))
			lpriv = se.getPrivilegeMask();
		if((se.getPrivilegeMask() & lpriv) != lpriv) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061440", "Incompatible grant"));
		}

		se.setInheritPrivs(sysEnv, new Long(lpriv));

		if(dependencydeflist != null) {
			Iterator i = dependencydeflist.iterator();
			WithHash wh;
			while(i.hasNext()) {
				wh = (WithHash) i.next();
				super.addOrAlterRequirement(sysEnv, wh, se, true, false);
			}
		}

		if(childdeflist != null) {
			Iterator i = childdeflist.iterator();
			WithHash wh;
			while(i.hasNext()) {
				wh = (WithHash) i.next();
				super.addOrAlterChild(sysEnv, wh, se, true, false);
			}
		}

		if(resourcedeflist != null) {
			Iterator i = resourcedeflist.iterator();
			WithHash wh;
			while(i.hasNext()) {
				wh = (WithHash) i.next();
				super.addOrAlterResource(sysEnv, wh, se, true, false);
			}
		}

		if(parameters != null) {
			super.addOrAlterParameters(sysEnv, parameters, se, true, false);
		}

		if(priowarn)
			result.setFeedback(new SDMSMessage(sysEnv, "03303251625","Job Definition created, WARNING: priority adjusted"));
		else
			result.setFeedback(new SDMSMessage(sysEnv, "02201170715","Job Definition created"));
	}

}

