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


package de.independit.scheduler.server.util;

import java.lang.*;
import java.util.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class PathVector extends Vector implements Comparable
{

	public final static String __version = "@(#) $Id: PathVector.java,v 2.4.14.1 2013/03/14 10:25:29 ronald Exp $";
	public final static String DEFAULT_SEP = ".";

	private String sep;

	public PathVector()
	{
		super();
		setSep(DEFAULT_SEP);
	}

	public PathVector(Collection c)
	{
		super(c);
		setSep(DEFAULT_SEP);
	}

	public PathVector(int initialCapacity)
	{
		super(initialCapacity);
		setSep(DEFAULT_SEP);
	}

	public PathVector(int initialCapacity, int capacityIncrement)
	{
		super(initialCapacity, capacityIncrement);
		setSep(DEFAULT_SEP);
	}

	public PathVector(String s)
	{
		super();
		setSep(s);
	}

	public void setSep(String s)
	{
		sep = s;
	}

	public int compareTo(Object o)
	{
		PathVector p;
		int t1, t2, s;
		Comparable o1, o2;
		try {
			p = (PathVector) o;
			t1 = size();
			t2 = p.size();
			s = (t1 < t2 ? t1 : t2);

			for(int i = 0; i < s; i++) {
				o1 = (Comparable) get(i);
				o2 = (Comparable) p.get(i);
				t1 = o1.compareTo(o2);
				if(t1 != 0) return t1;
			}
		} catch (ClassCastException cce) {
			throw cce;
		}
		if(p.size() > size()) return -1;
		if(p.size() < size()) return 1;
		return 0;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();

		if (size() == 0) return "";
		for(int i = 0; i < size() - 1; i++) {
			s.append(get(i).toString());
			s.append(sep);
		}
		s.append(get(size() - 1));
		return s.toString();
	}

	public String toQuotedString(HashMap mapping)
	{
		PathVector testv = new PathVector();
		if (mapping != null) {
			testv.addAll(this);
			while (!mapping.containsKey(testv) && testv.size() > 0) {
				testv.removeElementAt(testv.size() - 1);
			}
			if (testv.size() != 0) {
				testv = (PathVector) mapping.get(testv);
			}
		}

		StringBuffer s = new StringBuffer();

		if (size() == 0) return "";
		int sz = size() - 1;
		int tvs = testv.size();
		for(int i = 0; i < sz; i++) {
			if (i != 0) s.append('\'');
			s.append((i < tvs ? testv.get(i).toString() : get(i).toString()));
			if (i != 0) s.append('\'');
			s.append(sep);
		}
		if (sz != 0) s.append('\'');
		s.append((sz < tvs ? testv.get(sz).toString() : get(sz).toString()));
		if (sz != 0) s.append('\'');
		return s.toString();
	}

	public PathVector addThis(Comparable o)
	{
		super.addElement(o);
		return this;
	}
}

