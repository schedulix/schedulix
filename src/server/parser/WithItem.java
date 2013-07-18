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

public class WithItem implements Comparable
{

	public final static String __version = "@(#) $Id: WithItem.java,v 2.1.14.1 2013/03/14 10:24:54 ronald Exp $";

	public Comparable key;
	public Object value;

	public WithItem (Comparable p_key, Object p_value)
	{
		key   = p_key;
		value = p_value;
	}

	public String toString()
	{
		return key.toString() + '=' + value;
	}

	public int compareTo(Object o)
	{
		if (o == null) throw new NullPointerException();
		if (!(o instanceof WithItem)) return -1;
		WithItem po = (WithItem) o;
		return key.compareTo(po.key);
	}
}
