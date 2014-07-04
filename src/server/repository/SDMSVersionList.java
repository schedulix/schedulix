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
import java.net.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class SDMSVersionList
{

	public final static String __version = "@(#) $Id: SDMSVersionList.java,v 2.1.2.1 2013/03/14 10:25:26 ronald Exp $";

	protected Vector versionList;

	public SDMSVersionList()
	{
		versionList = new Vector();
	}

	public int size()
	{
		return versionList.size();
	}

	public synchronized long first(SystemEnvironment env)
		throws SDMSException
	{
		if(versionList.size() == 0) return SDMSTransaction.getRoVersion(env);
		return ((Long) versionList.get(0)).longValue();
	}

	public synchronized void add(long v)
	{
		int i;

		for(i = versionList.size() - 1; i >= 0; --i) {
			if(((Long) versionList.get(i)).longValue() <= v) break;
		}
		versionList.insertElementAt(new Long(v), i+1);
	}

	public synchronized int remove( long v)
	{
		int i;

		for(i = 0; i < versionList.size(); ++i) {
			if(((Long) versionList.get(i)).longValue() == v) {
				versionList.removeElementAt(i);
				break;
			}
		}
		if (i < versionList.size()) {
			return i;
		} else {
			return -1;
		}
	}
}

