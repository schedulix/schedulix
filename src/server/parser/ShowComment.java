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
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class ShowComment extends Node
{

	public final static String __version = "@(#) $Id: ShowComment.java,v 2.4.14.2 2013/06/18 09:49:35 ronald Exp $";

	private int type;
	private ObjectURL obj;

	public ShowComment(ObjectURL t)
	{
		super();
		obj = t;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		final SDMSOutputContainer d_container;
		final Vector desc = new Vector();
		final Date d = new Date();

		desc.add("ID");

		desc.add("TAG");

		desc.add("COMMENT");

		desc.add("COMMENTTYPE");

		desc.add("CREATOR");

		desc.add("CREATE_TIME");

		desc.add("CHANGER");

		desc.add("CHANGE_TIME");

		desc.add("PRIVS");

		Long objId = null;

		obj.resolve(sysEnv);

		d_container = new SDMSOutputContainer(sysEnv, "Comment", desc);

		SDMSObjectComment oc;
		Vector ocv = SDMSObjectCommentTable.idx_objectId_getSortedVector(sysEnv, obj.objId);
		for (int i = 0; i < ocv.size(); ++i) {
			Vector data = new Vector();
			oc = (SDMSObjectComment) ocv.get(i);

			data.add(oc.getId(sysEnv));
			data.add(oc.getTag(sysEnv));
			data.add(oc.getDescription(sysEnv));
			data.add(oc.getInfoTypeAsString(sysEnv));

			try {
				data.add(SDMSUserTable.getObject(sysEnv, oc.getCreatorUId(sysEnv)).getName(sysEnv));
			} catch (NotFoundException nfe) {
				data.add(SDMSUserTable.DROPPED_NAME);
			}
			d.setTime(oc.getCreateTs(sysEnv).longValue());
			data.add(sysEnv.systemDateFormat.format(d));
			try {
				data.add(SDMSUserTable.getObject(sysEnv, oc.getChangerUId(sysEnv)).getName(sysEnv));
			} catch (NotFoundException nfe) {
				data.add(SDMSUserTable.DROPPED_NAME);
			}
			d.setTime(oc.getChangeTs(sysEnv).longValue());
			data.add(sysEnv.systemDateFormat.format(d));
			data.add(oc.getPrivileges(sysEnv).toString());

			d_container.addData(sysEnv, data);
		}

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03602161459", "Comment shown"));
	}
}

