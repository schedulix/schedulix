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

import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.output.SDMSOutputContainer;

public class ChownChecker
{
	public static final String __version = "@(#) $Id: ChownChecker.java,v 2.2.6.1 2013/03/14 10:24:24 ronald Exp $";

	public static void check(SystemEnvironment sysEnv, Long gIdTo)
	throws SDMSException
	{
		final Long uId = sysEnv.cEnv.uid();
		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		final SDMSGroup g = SDMSGroupTable.getObject(sysEnv, gIdTo);
		if (g.getDeleteVersion(sysEnv).longValue() != 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03603220847",
			                               "Group $1 is deleted", g.getName(sysEnv)));
		}

		if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(gIdTo, uId)) &&
		    !SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, uId))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03312162236",
			                               "User $1 does not belong to Group $2", u.getName(sysEnv), g.getName(sysEnv)));
		}
	}

	private ChownChecker()
	{

	}
}
