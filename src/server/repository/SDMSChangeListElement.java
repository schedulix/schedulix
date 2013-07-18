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

import java.lang.*;
import java.util.*;
import java.io.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.util.*;

class SDMSChangeListElement
{

	public static final String __version = "@(#) $Id: SDMSChangeListElement.java,v 2.2.8.1 2013/03/14 10:25:17 ronald Exp $";

	public SDMSVersions versions;
	public boolean isNew;

	public SDMSChangeListElement (SystemEnvironment env, SDMSVersions v, boolean p_isNew)
	{
		versions = v;
		isNew = p_isNew;
	}

	public int hashCode()
	{
		return versions.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof SDMSChangeListElement) {
			return versions.equals(((SDMSChangeListElement) obj).versions);
		} else return false;
	}

	public void dump()
	{
		SDMSThread.doTrace(null, toString(), SDMSThread.SEVERITY_DEBUG);
	}

	public String toString()
	{
		String rc = new String (
		        "------ Start Dump ChangeListElement -------\n" +
		        versions.toString(4) + "\n" +
		        "isNew : " + isNew + "\n" +
		        "------ End Dump ChangeListElement -------\n"
		);
		return rc;
	}
}
