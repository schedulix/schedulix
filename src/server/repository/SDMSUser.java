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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSUser extends SDMSUserProxyGeneric
{

	public final static String __version = "SDMSUser $Revision: 2.5.2.2 $ / @(#) $Id: SDMSUser.java,v 2.5.2.2 2013/03/16 11:47:22 dieter Exp $";

	protected SDMSUser(SDMSObject p_object)
	{
		super(p_object);
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getName(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "user " + getURLName(sysEnv);
	}

	public void setPasswd (SystemEnvironment sysEnv, String p_passwd)
	throws SDMSException
	{
		boolean canAlter = true;

		HashSet groups = sysEnv.cEnv.gid();
		if (!groups.contains(SDMSObject.adminGId)) {
			Vector gv = SDMSMemberTable.idx_uId.getVector(sysEnv, this.getId(sysEnv));
			for (int i = 0; i < gv.size(); ++i ) {
				SDMSMember gm = (SDMSMember) gv.get(i);
				Long gId = gm.getGId(sysEnv);
				if (!groups.contains(gId)) {
					canAlter = false;
					break;
				}
			}
		}
		if (canAlter) {
			checkWrite(sysEnv);
			if(!checkPrivileges(sysEnv, SDMSPrivilege.EDIT))
				throw new AccessViolationException (accessViolationMessage(sysEnv, "01312181241"));

			((SDMSUserGeneric)(object)).setPasswd (sysEnv, p_passwd);
		} else
			throw new AccessViolationException (accessViolationMessage(sysEnv, "03103071727"));

		return ;
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
		if (getId(sysEnv).equals(sysEnv.cEnv.uid())) {
			p = p | SDMSPrivilege.VIEW;
		}
		return p & checkPrivs;
	}

}
