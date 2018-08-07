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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class RenameInterval
	extends Node
{
	public static final String __version = "@(#) $Id: RenameInterval.java,v 2.0.20.1 2013/03/14 10:24:44 ronald Exp $";

	private final String name1;
	private final String name2;

	public RenameInterval (String from, String to)
	{
		super();
		name1 = from;
		name2 = to;
	}

	public void go (SystemEnvironment sysEnv)
	throws SDMSException
	{
		final SDMSInterval ival = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(name1, null));
		ival.setName (sysEnv, name2);

		result.setFeedback (new SDMSMessage (sysEnv, "04207191924", "Interval renamed"));
	}
}
