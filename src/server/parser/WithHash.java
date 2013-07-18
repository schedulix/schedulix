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

import de.independit.scheduler.server.exception.*;

public class WithHash extends HashMap
{

	public final static String __version = "@(#) $Id: WithHash.java,v 2.1.14.1 2013/03/14 10:24:54 ronald Exp $";

	public void addItem (WithItem wi)
	throws SDMSEscape
	{
		boolean alreadyContained = containsKey (wi.key);

		if (! alreadyContained)
			alreadyContained = put (wi.key, wi.value) != null;

		if (alreadyContained)
			throw new SDMSEscape("didn't expect a second occurrence of " + wi.key);
	}

	public String toString()
	{
		String s = "";
		String c = "";
		final Iterator i = entrySet().iterator();
		while (i.hasNext()) {
			final Map.Entry m = (Map.Entry) i.next();
			s = s + c + m.getKey().toString() + " = " + m.getValue().toString();
			c = ", ";
		}
		return s;
	}
}

