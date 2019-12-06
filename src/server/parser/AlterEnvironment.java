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

public class AlterEnvironment extends Node
{

	public final static String __version = "@(#) $Id: AlterEnvironment.java,v 2.7.14.1 2013/03/14 10:24:19 ronald Exp $";

	private ObjectURL url;
	private Vector resourceList;
	private Boolean add;
	private boolean noerr;

	public AlterEnvironment(ObjectURL u, boolean a, Vector r, Boolean ne)
	{
		super();
		url = u;
		add = new Boolean(a);
		resourceList = r;
		noerr = ne.booleanValue();
	}

	public AlterEnvironment(ObjectURL u, Vector r, Boolean ne)
	{
		url = u;
		resourceList = r;
		add = null;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSNamedEnvironment ne;
		try {
			ne = (SDMSNamedEnvironment) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122352", "No Environment altered"));
				return;
			}
			throw nfe;
		}
		Long neId = ne.getId(sysEnv);
		SDMSEnvironment e;
		SDMSNamedResource nr;

		if(add == null) {
			if (resourceList != null) {
				Vector v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, neId);
				for(int i = 0; i < v.size(); i++) {
					e = (SDMSEnvironment) v.get(i);
					e.delete(sysEnv);
				}
				for(int i = 0; i < resourceList.size(); i++) {
					WithItem w = (WithItem) resourceList.get(i);
					String condition = (String) w.value;
					if (condition != null && condition.trim().equals("")) condition = null;

					nr = SDMSNamedResourceTable.getNamedResource(sysEnv, (Vector) w.key);
					SDMSEnvironmentTable.table.create(sysEnv, neId, nr.getId(sysEnv), condition);
				}
			}
		} else {
			if(add.booleanValue()) {
				for(int i = 0; i < resourceList.size(); i++) {
					WithItem w = (WithItem) resourceList.get(i);
					String condition = (String) w.value;
					if (condition != null && condition.trim().equals("")) condition = null;
					nr = SDMSNamedResourceTable.getNamedResource(sysEnv, (Vector) w.key);
					SDMSEnvironmentTable.table.create(sysEnv, neId, nr.getId(sysEnv), condition);
				}
			} else {
				for(int i = 0; i < resourceList.size(); i++) {
					nr = SDMSNamedResourceTable.getNamedResource(sysEnv, (Vector) resourceList.get(i));
					Long nrId = nr.getId(sysEnv);
					e = SDMSEnvironmentTable.idx_neId_nrId_getUnique(sysEnv, new SDMSKey(neId, nrId));

					Vector v = SDMSSchedulingEntityTable.idx_neId.getVector(sysEnv, neId);
					for(int j = 0; i < v.size(); j++) {
						final SDMSSchedulingEntity se = (SDMSSchedulingEntity) v.get(j);
						if(se.checkParameterRI(sysEnv, nrId)) {
							throw new CommonErrorException(
								new SDMSMessage(sysEnv, "0340911291423", "A parameter of Resource $1 is referenced by $2",
									nr.pathString(sysEnv), se.pathString(sysEnv))
							);
						}
					}
					e.delete(sysEnv);
				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03201230216", "Environment altered"));
	}

}

