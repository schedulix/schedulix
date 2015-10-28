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
import de.independit.scheduler.server.output.*;

public class ShowEvent
	extends ShowCommented
{
	public static final String __version = "@(#) $Id: ShowEvent.java,v 2.5.8.2 2013/06/18 09:49:35 ronald Exp $";

	private final String name;

	private SDMSEvent evt;
	private Long evtId;

	public ShowEvent (String n)
	{
		super();
		name = n;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		evt = SDMSEventTable.idx_name_getUnique (sysEnv, name);
		if(!evt.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411707", "Insufficient privileges"));
		evtId = evt.getId (sysEnv);

		Vector desc = new Vector();

		desc.add ("ID");

		desc.add ("NAME");
		desc.add ("OWNER");

		desc.add ("SCHEDULING_ENTITY");
		desc.add ("CREATOR");
		desc.add ("CREATE_TIME");
		desc.add ("CHANGER");
		desc.add ("CHANGE_TIME");

		desc.add ("PARAMETERS");
		desc.add ("PRIVS");
		desc.add ("COMMENT");
		desc.add ("COMMENTTYPE");

		final Vector data = new Vector();

		data.add (evtId);

		data.add (evt.getName (sysEnv));

		final Long ownerId = evt.getOwnerId (sysEnv);
		final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, ownerId);
		data.add (g.getName (sysEnv));

		final Long seId = evt.getSeId (sysEnv);
		if (seId == null)
			data.add (null);
		else {
			final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId);
			data.add (se.pathString (sysEnv));
		}

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, evt.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(evt.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, evt.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(evt.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));

		data.add (parm_list (sysEnv));
		data.add (evt.getPrivileges(sysEnv).toString());

		data.add (getCommentContainer (sysEnv, evtId));
		data.add (getCommentInfoType (sysEnv, evtId));

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "Event", desc, data);
		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04203161558", "Event shown"));
	}

	private SDMSOutputContainer parm_list (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Vector desc = new Vector();

		desc.add ("ID");

		desc.add ("KEY");

		desc.add ("VALUE");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "List of Event Parameters", desc);

		final Vector epList = SDMSEventParameterTable.idx_evtId.getVector (sysEnv, evtId);

		final Iterator epIt = epList.iterator();
		while (epIt.hasNext()) {
			final SDMSEventParameter ep = (SDMSEventParameter) epIt.next();

			final Vector row = new Vector();

			row.add (ep.getId (sysEnv));

			row.add (ep.getKey (sysEnv));

			final String sic = ep.getValue (sysEnv).substring (1);
			row.add (sic);

			table.addData (sysEnv, row);
		}

		Collections.sort (table.dataset, table.getComparator (sysEnv, 1));

		return table;
	}
}
