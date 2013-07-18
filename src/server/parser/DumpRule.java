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

public class DumpRule implements Comparable
{

	public final static String __version = "@(#) $Id: DumpRule.java,v 2.1.14.2 2013/03/14 10:24:32 ronald Exp $";

	public String name;
	public Integer numOp;
	public String alias;

	public DumpRule (String p_name, String p_alias)
	{
		name  = p_name;
		alias = p_alias;
	}

	public DumpRule (DumpRule r)
	{
		name  = new String(r.name);
		alias = new String(r.alias);
	}

	public String toString()
	{
		return name + (alias == null ? " " : " (" + alias + ") ");
	}

	public int compareTo(Object o)
	{
		if (o == null) throw new NullPointerException();
		if (!(o instanceof DumpRule)) return -1;
		DumpRule po = (DumpRule) o;
		int result = name.compareTo(po.name);
		if (result == 0) {
			if (alias == null && po.alias == null) result = 0;
			else {
				if (alias != null) {
					if (po.alias != null) result = alias.compareTo(po.alias);
					else result = 1;
				} else return -1;
			}
		}
		return result;
	}
}
