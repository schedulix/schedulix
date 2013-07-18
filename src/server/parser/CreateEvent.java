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

public class CreateEvent
	extends Node
{
	public static final String __version = "@(#) $Id: CreateEvent.java,v 2.6.4.1 2013/03/14 10:24:25 ronald Exp $";

	private final ObjectURL obj;
	private final WithHash with;
	private final boolean replace;

	private SubmitJob submit;

	public CreateEvent (ObjectURL o, WithHash w, Boolean r)
	{
		super();
		obj = o;
		with = w;
		replace = r.booleanValue();
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		try {
			obj.resolve(sysEnv);
		} catch (final NotFoundException nfe) {

		}

		if(!with.containsKey(ParseStr.S_ACTION)) {
			throw new CommonErrorException (new SDMSMessage(sysEnv, "03312161111", "No action specified"));
		}
		submit = (SubmitJob) with.get(ParseStr.S_ACTION);

		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.get (sysEnv, submit.path, submit.name);

		if(!se.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT))
			throw new AccessViolationException(
			        new SDMSMessage(sysEnv, "03402131255", "Submit privilege on $1 missing", se.pathString(sysEnv))
			);
		final Long seId = se.getId (sysEnv);

		final Long uId = env.uid();
		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		final Long gId;
		if(!with.containsKey(ParseStr.S_GROUP)) {
			gId = u.getDefaultGId(sysEnv);
		} else {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
			              sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
		}
		se.checkSubmitForGroup(sysEnv, gId);

		SDMSEvent evt;
		try {
			evt = SDMSEventTable.table.create (sysEnv, obj.mappedName, gId, seId);
		} catch (final DuplicateKeyException dke) {
			if (replace) {
				final AlterEvent ae = new AlterEvent (obj, with, Boolean.FALSE);
				ae.setEnv (env);
				ae.go (sysEnv);
				result = ae.result;
				return;
			}
			throw dke;
		}

		final Long evtId = evt.getId (sysEnv);

		EventParameter.create (sysEnv, evtId, submit);

		result.setFeedback (new SDMSMessage (sysEnv, "04203071739", "Event created"));
	}
}
