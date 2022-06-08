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
package de.independit.scheduler.server.repository;

import java.lang.*;
import java.util.*;
import java.sql.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class RepositoryChecker
{
	private static boolean checked = false;

	public static void checkRepository(SystemEnvironment env) throws SDMSException
	{
		if (checked) return;

		checkRunnableQueue(env);
		checkResourceAllocation(env);

		checked = true;
	}

	private static boolean smeDead(SystemEnvironment env, Long smeId) throws SDMSException
	{
		boolean dead = false;
		if (smeId.longValue() < 0) smeId = Long.valueOf(- smeId.longValue());
		try {
			SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(env, smeId);
			Integer state = sme.getState(env);
			if (state == SDMSSubmittedEntity.FINAL || state == SDMSSubmittedEntity.CANCELLED) {
				dead = true;
			} else {
				smeId = sme.getMasterId(env);
				sme = SDMSSubmittedEntityTable.getObject(env, smeId);
				state = sme.getState(env);
				if (state == SDMSSubmittedEntity.FINAL || state == SDMSSubmittedEntity.CANCELLED) {
					dead = true;
				}
			}
		} catch (NotFoundException nfe) {
			dead = true;
		}
		return dead;
	}

	private static void checkRunnableQueue(SystemEnvironment env) throws SDMSException
	{
		Iterator i = SDMSRunnableQueueTable.table.iterator(env);
		while (i.hasNext()) {
			SDMSRunnableQueue rq = (SDMSRunnableQueue)(i.next());
			if (smeDead(env, rq.getSmeId(env))) {
				rq.delete(env);
			}
		}
	}

	private static void checkResourceAllocation(SystemEnvironment env) throws SDMSException
	{
		Iterator i = SDMSResourceAllocationTable.table.iterator(env);
		while (i.hasNext()) {
			SDMSResourceAllocation ra = (SDMSResourceAllocation)(i.next());
			if (smeDead(env, ra.getSmeId(env))) {
				ra.delete(env);
			}
		}
	}
}

