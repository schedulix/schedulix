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

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.util.*;

public class DumpExpandItem implements Comparable
{

	public final static String __version = "@(#) $Id: DumpExpandItem.java,v 2.1.14.2 2013/03/14 12:46:47 dieter Exp $";

	public String name;
	public Integer type;
	public String alias;
	public PathVector ruleList;

	public DumpExpandItem (String p_name, String p_alias, PathVector p_ruleList)
	{
		name  = p_name;
		alias = p_alias;
		ruleList = p_ruleList;
	}

	public DumpExpandItem (DumpExpandItem src)
	{
		name  = new String(src.name);
		alias = (src.alias == null ? null : new String(src.alias));
		ruleList = new PathVector(src.ruleList);
	}

	public String toString()
	{
		return name + (alias == null ? " " : " (" + alias + ") ") + '=' + ruleList.toString();
	}

	public int compareTo(Object o)
	{
		if (o == null) throw new NullPointerException();
		if (!(o instanceof DumpExpandItem)) return -1;
		DumpExpandItem po = (DumpExpandItem) o;
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
