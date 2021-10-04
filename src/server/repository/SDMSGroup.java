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

public class SDMSGroup extends SDMSGroupProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSGroup.java,v 2.6.2.1 2013/03/14 10:25:19 ronald Exp $";

	protected SDMSGroup(SDMSObject p_object)
	{
		super(p_object);
	}

	public void setDeleteVersion(SystemEnvironment sysEnv, Long deleteVersion)
	throws SDMSException
	{
		Vector gv = SDMSGrantTable.idx_gId.getVector(sysEnv, this.getId(sysEnv));
		for (int i = 0; i < gv.size(); ++i) {
			SDMSGrant g = (SDMSGrant) gv.get(i);
			g.delete(sysEnv);
		}
		super.setDeleteVersion(sysEnv, deleteVersion);
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getName(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "group " + getURLName(sysEnv);
	}
}

