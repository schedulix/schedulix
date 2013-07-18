/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class MoveSchedule
	extends Node
{
	public static final String __version = "@(#) $Id: MoveSchedule.java,v 2.1.2.1 2013/03/14 10:24:41 ronald Exp $";

	private final Vector fromPath;
	private final String fromName;
	private final Vector target;
	private final String newName;

	public MoveSchedule (Vector p, String n, Vector t)
	{
		super();
		fromPath = p;
		fromName = n;
		target = t;
		newName = null;
	}

	public MoveSchedule (Vector p, String n, String t)
	{
		super();
		fromPath = p;
		fromName = n;
		target = null;
		newName = t;
	}

	private void moveSchedule(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSSchedule parentSce;
		String newName;
		try {
			parentSce = SDMSScheduleTable.getSchedule (sysEnv, target);
			newName = fromName;
		} catch (final NotFoundException e) {
			newName = (String) target.remove (target.size() - 1);
			parentSce = SDMSScheduleTable.getSchedule (sysEnv, target);
		}

		final Long newParentId = parentSce.getId (sysEnv);

		final Long oldParentId = SDMSScheduleTable.getSchedule (sysEnv, fromPath).getId (sysEnv);
		final SDMSKey oldParentKey = new SDMSKey (oldParentId, fromName);
		final SDMSSchedule sce = SDMSScheduleTable.idx_parentId_name_getUnique (sysEnv, oldParentKey);
		final Long sceId = sce.getId (sysEnv);

		if (newParentId.equals (sceId))
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04207252320", "A Schedule cannot be moved below itself"));
		Long id;
		while ((id = parentSce.getParentId (sysEnv)) != null) {
			if (id.equals (sceId))
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04207252324", "A Schedule cannot be moved below itself"));
			parentSce = SDMSScheduleTable.getObject (sysEnv, id);
		}

		sce.set_ParentIdName (sysEnv, newParentId, newName);

		result.setFeedback (new SDMSMessage (sysEnv, "04207251911", "Schedule moved"));
	}

	private void renameSchedule(SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Long oldParentId = SDMSScheduleTable.getSchedule (sysEnv, fromPath).getId (sysEnv);
		final SDMSKey oldParentKey = new SDMSKey (oldParentId, fromName);
		final SDMSSchedule sce = SDMSScheduleTable.idx_parentId_name_getUnique (sysEnv, oldParentKey);

		sce.setName (sysEnv, newName);

		result.setFeedback (new SDMSMessage (sysEnv, "03112161244", "Schedule renamed"));
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		if (newName == null)
			moveSchedule(sysEnv);
		else
			renameSchedule(sysEnv);
	}
}
