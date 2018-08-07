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

import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;

public class AlterSchedule
	extends Node
{
	public static final String __version = "@(#) $Id: AlterSchedule.java,v 2.10.2.1 2013/03/14 10:24:22 ronald Exp $";

	private final ObjectURL obj;
	private final WithHash with;
	private Boolean active;
	private final boolean noerr;

	public AlterSchedule (ObjectURL o, WithHash i, Boolean ne)
	{
		super();
		obj = o;
		with = (i == null ? new WithHash() : i);
		noerr = ne.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		obj.resolve(sysEnv);
		final Long seId = obj.seId;
		final SDMSSchedule sce;

		final Vector mappedPath = obj.path;
		final String mappedName = (String) mappedPath.remove (mappedPath.size() - 1);
		if (mappedPath.size() == 0) {
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				throw new CommonErrorException(new SDMSMessage (sysEnv, "03709291504", "Insufficient privileges"));
			}
			sce = SDMSScheduleTable.getObject(sysEnv, SDMSObject.rootScId);
		} else {

			final Long parentId = SDMSScheduleTable.pathToId (sysEnv, mappedPath);
			final SDMSKey parentKey = new SDMSKey (parentId, mappedName);

			try {
				sce = SDMSScheduleTable.idx_parentId_name_getUnique (sysEnv, parentKey);
			} catch (NotFoundException nfe) {
				if(noerr) {
					result.setFeedback (new SDMSMessage (sysEnv, "03311130102", "No Schedule altered"));
					return;
				}
				throw nfe;
			}
		}

		if(!with.containsKey(ParseStr.S_ACTIVE)) active = null;
		else	active = (Boolean) with.get(ParseStr.S_ACTIVE);

		final String intervalName = (String) with.get(ParseStr.S_INTERVAL);
		if (intervalName != null) {
			final SDMSInterval ival = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(IntervalUtil.mapIdName (intervalName, seId), null));
			sce.setIntId (sysEnv, ival.getId (sysEnv));
		} else {
			if(with.containsKey(ParseStr.S_INTERVAL))
				sce.setIntId (sysEnv, null);
		}

		if(active != null) {
			final boolean isActive = sce.getIsActive (sysEnv).booleanValue();
			if (active.booleanValue() != isActive)
				sce.setIsActive (sysEnv, active);
		}

		if(with.containsKey(ParseStr.S_GROUP)) {
			final String gName = (String) with.get (ParseStr.S_GROUP);
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
			                         sysEnv, new SDMSKey (gName, new Long(0))).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
			sce.setOwnerId(sysEnv, gId);
		}

		if (with.containsKey(ParseStr.S_INHERIT)) {
			Long inheritPrivs = (Long) with.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
			long lpriv = inheritPrivs.longValue();
			if((sce.getPrivilegeMask() & lpriv) != lpriv) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061132", "Incompatible grant"));
			}
			sce.setInheritPrivs(sysEnv, inheritPrivs);
		}

		if (with.containsKey(ParseStr.S_TIME)) {
			String tmpTz = (String) with.get(ParseStr.S_TIME);
			TimeZone tz = TimeZone.getTimeZone(tmpTz);
			if (!tmpTz.equals(tz.getID())) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03207031502", "Time zone " + tmpTz + " unknown"));
			}
			sce.setTimeZone(sysEnv, tmpTz);
		}

		SystemEnvironment.timer.notifyChange (sysEnv, sce, TimerThread.ALTER);

		result.setFeedback (new SDMSMessage (sysEnv, "04207260120", "Schedule altered"));
	}
}
