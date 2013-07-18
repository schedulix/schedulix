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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class AlterEvent
	extends Node
{
	public static final String __version = "@(#) $Id: AlterEvent.java,v 2.5.4.1 2013/03/14 10:24:20 ronald Exp $";

	private final ObjectURL obj;
	private final WithHash with;
	private final boolean noerr;

	public AlterEvent (ObjectURL o, WithHash w, Boolean ne)
	{
		super();
		obj = o;
		with = w;
		noerr = ne.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final SDMSEvent evt = (SDMSEvent) obj.resolve(sysEnv);
		final Long evtId = obj.objId;

		if(with.containsKey(ParseStr.S_ACTION)) {
			final SubmitJob submit = (SubmitJob) with.get(ParseStr.S_ACTION);

			final Iterator sIt = submit.with.keySet().iterator();
			while (sIt.hasNext()) {
				final String key = (String) sIt.next();
				if (! key.equals (ParseStr.S_PARAMETERS))
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04207101802", key + " not allowed here"));
			}

			final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.get (sysEnv, submit.path, submit.name);
			if(!se.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
				throw new AccessViolationException(
				        new SDMSMessage(sysEnv, "03402131254", "Execute privilege on $1 missing", se.pathString(sysEnv))
				);
			final Long seId = se.getId (sysEnv);

			evt.setSeId (sysEnv, seId);

			EventParameter.kill   (sysEnv, evtId);
			EventParameter.create (sysEnv, evtId, submit);

			SystemEnvironment.timer.notifyChange (sysEnv, evt, TimerThread.ALTER);
		}

		if(with.containsKey(ParseStr.S_GROUP)) {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
			                         sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
			evt.setOwnerId(sysEnv, gId);
		}

		result.setFeedback (new SDMSMessage (sysEnv, "03201212234", "Event altered"));
	}
}
