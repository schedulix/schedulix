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

public class CreateSchedule
	extends Node
{
	public static final String __version = "@(#) $Id: CreateSchedule.java,v 2.12.2.1 2013/03/14 10:24:27 ronald Exp $";

	private final ObjectURL obj;
	private final WithHash with;
	private Boolean active;
	private final boolean replace;

	public CreateSchedule (ObjectURL o, WithHash i, Boolean r)
	{
		super();
		obj = o;
		with = (i == null ? new WithHash() : i);
		replace = r.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		try {
			obj.resolve(sysEnv);
		} catch (final NotFoundException nfe) {

		}
		final Long seId = obj.seId;

		final Vector mappedPath = new Vector (obj.path);
		final String mappedName = (String) mappedPath.remove (mappedPath.size() - 1);

		final SDMSSchedule parent = SDMSScheduleTable.getSchedule (sysEnv, mappedPath);
		final Long parentId = parent.getId(sysEnv);

		if (SDMSScheduleTable.idx_parentId_name.containsKey (sysEnv, new SDMSKey (parentId, mappedName))) {
			if (replace) {
				final AlterSchedule as = new AlterSchedule (obj, with, Boolean.FALSE);
				as.setEnv (env);
				as.go (sysEnv);
				result = as.result;
				return;
			}

			throw new DuplicateKeyException (new SDMSMessage (sysEnv,
			                                 "04207251651",
			                                 "Object with name $1 already exists within $2",
			                                 mappedName,
			                                 SDMSScheduleTable.getObject (sysEnv, parentId).pathString (sysEnv)));
		}

		final Long ivalId;
		final String intervalName = (String) with.get(ParseStr.S_INTERVAL);
		if (intervalName == null)
			ivalId = null;
		else {
			final SDMSInterval ival = SDMSIntervalTable.idx_name_getUnique (sysEnv, IntervalUtil.mapIdName (intervalName, seId));
			ivalId = ival.getId (sysEnv);
		}

		final Long uId = env.uid();
		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		final Long gId;
		if(!with.containsKey(ParseStr.S_GROUP)) {
			gId = u.getDefaultGId(sysEnv);
		} else {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
			              sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(gId, uId)) &&
			    !SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, uId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03401151027",
				                               "User $1 does not belong to Group $2", u.getName(sysEnv), gName));
			}
		}

		Long inheritPrivs;
		if (with.containsKey(ParseStr.S_INHERIT)) {
			inheritPrivs = (Long) with.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
		} else
			inheritPrivs = null;

		long lpriv = (inheritPrivs == null ? parent.getPrivilegeMask() : inheritPrivs.longValue());
		if((parent.getPrivilegeMask() & lpriv) != lpriv) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061327", "Incompatible grant"));
		}

		inheritPrivs = new Long(lpriv);

		if(!with.containsKey(ParseStr.S_ACTIVE)) active = Boolean.TRUE;
		else	active = (Boolean) with.get(ParseStr.S_ACTIVE);

		String tz;
		String warning = "";
		if (with.containsKey(ParseStr.S_TIME)) {
			tz = (String) with.get(ParseStr.S_TIME);
			TimeZone tmp = TimeZone.getTimeZone(tz);
			if (!tz.equals(tmp.getID())) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03207031503", "Time Zone " + tz + " unknown"));
			}
		} else {
			TimeZone tmp = TimeZone.getDefault();
			tz = tmp.getID();
		}

		SDMSScheduleTable.table.create (sysEnv, mappedName, gId, ivalId, parentId, tz, seId, active, inheritPrivs);

		result.setFeedback (new SDMSMessage (sysEnv, "04207251652", "Schedule created"));
	}
}
