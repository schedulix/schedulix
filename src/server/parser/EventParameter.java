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

public class EventParameter
{
	public static final String __version = "@(#) $Id: EventParameter.java,v 2.1.18.1 2013/03/14 10:24:33 ronald Exp $";

	public static void kill (SystemEnvironment sysEnv, final Long evtId)
	throws SDMSException
	{
		final Vector epList = SDMSEventParameterTable.idx_evtId.getVector (sysEnv, evtId);
		final Iterator epIt = epList.iterator();
		while (epIt.hasNext()) {
			final SDMSEventParameter ep = (SDMSEventParameter) epIt.next();
			ep.delete (sysEnv);
		}
	}

	public static void create (SystemEnvironment sysEnv, final Long evtId, final SubmitJob submit)
	throws SDMSException
	{
		final Vector parmList = (Vector) submit.with.get (ParseStr.S_PARAMETERS);
		if (parmList == null)
			return;

		final Iterator parmIt = parmList.iterator();
		while (parmIt.hasNext()) {
			final WithItem parm = (WithItem) parmIt.next();

			final String sic = '=' + parm.value.toString();

			SDMSEventParameterTable.table.create (sysEnv, (String) parm.key, sic, evtId);
		}
	}

	private EventParameter()
	{

	}
}
