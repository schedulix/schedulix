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


package de.independit.scheduler.server.output;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSOutputContainer implements Serializable
{

	public final static String __version = "@(#) $Id: SDMSOutputContainer.java,v 2.2.2.1 2013/03/14 10:24:17 ronald Exp $";

	public Object	title;
	public Vector	desc;
	public Vector	dataset;
	public int	lines;
	public int	columns;
	public int	labelwidth;

	public SDMSOutputContainer (SystemEnvironment env, Object p_title, Vector p_desc)
	throws FatalException
	{
		commonInit (env, p_title, p_desc);
		columns = desc.size();
	}

	public SDMSOutputContainer (SystemEnvironment env, Object p_title, Vector p_desc, Vector p_data)
	throws FatalException
	{
		commonInit (env, p_title, p_desc);
		columns = 0;
		addData(env, p_data);
	}

	private void commonInit (SystemEnvironment env, Object p_title, Vector p_desc)
	throws FatalException
	{
		SDMSOutputLabel lbl;

		title = p_title;
		if (p_desc == null || p_desc.size() == 0) {
			throw new FatalException (new SDMSMessage(env, "03110181621",
			                          "Cannot create SDMSOutputContainer without or empty description"));
		}
		desc = new Vector();
		labelwidth = 0;
		Iterator i = p_desc.iterator();
		while(i.hasNext()) {
			lbl = new SDMSOutputLabel( (String) i.next() );
			desc.add(lbl);
			if (lbl.length > labelwidth) {
				labelwidth = lbl.length;
			}
		}
		dataset    = new Vector();
		lines      = 0;
	}

	public void addData (SystemEnvironment env, Vector p_data) throws FatalException
	{
		if (columns == 0 && lines != 0) {
			throw new FatalException (new SDMSMessage(env, "03110181622",
			                          "Cannot add data to record type SDMSOutputContainer"));
		}
		if (columns != 0 && p_data.size() != columns) {
			throw new FatalException (new SDMSMessage(env, "03110181623",
			                          "Number of data items does not match description"));
		}
		int idx = 0;
		Iterator i = p_data.iterator();
		while(i.hasNext()) {
			Object o = i.next();
			if(o == null) {

				if (((SDMSOutputLabel)(desc.elementAt(idx))).length < 6) {
					((SDMSOutputLabel)(desc.elementAt(idx))).length = 6;
				}
			} else {
				if (!(o instanceof SDMSOutputContainer)) {
					String str = o.toString();
					int len = str.length();
					if (((SDMSOutputLabel)(desc.elementAt(idx))).length < len) {
						((SDMSOutputLabel)(desc.elementAt(idx))).length = len;
					}
				}
			}
			idx += 1;
		}
		dataset.add(p_data);
		lines = lines + 1;
	}

	public void setWidth(SystemEnvironment env, int colno, int len)
	throws FatalException
	{
		if (columns != 0 && colno >= columns) {
			throw new FatalException (new SDMSMessage(env, "0312121108",
			                          "Columnnumber exceeds the number of defined columns"));
		}
		((SDMSOutputLabel)(desc.elementAt(colno))).length = len;
	}

	public int indexForName(SystemEnvironment env, String p_name)
	{
		SDMSOutputLabel label;
		Iterator i = desc.iterator();
		int idx = 0;
		while (i.hasNext()) {
			label = (SDMSOutputLabel)i.next();
			if (label.name.equals(p_name)) {
				return idx;
			}
			idx ++;
		}

		throw new RuntimeException("Name " + p_name + " not found in decriptor");
	}

	public Comparator getComparator(SystemEnvironment env, int c1)
	throws SDMSException
	{
		int a[] = new int[1];

		a[0] = c1;
		if(c1 >= desc.size() || -c1 >= desc.size()) {
			throw new FatalException(new SDMSMessage(env, "03117121557",
			                         "The number of the sortcolumn ($1) exceeds the number of columns ($2)",
			                         new Integer(c1), new Integer(desc.size())));
		}
		return new occomp(env, a);
	}

	public Comparator getComparator(SystemEnvironment env, int c1, int c2)
	throws SDMSException
	{
		int a[] = new int[2];

		a[0] = c1;
		if(c1 >= desc.size() || -c1 >= desc.size()) {
			throw new FatalException(new SDMSMessage(env, "03201292045",
			                         "The number of the sortcolumn ($1) exceeds the number of columns ($2)",
			                         new Integer(c1), new Integer(desc.size())));
		}
		a[1] = c2;
		if(c2 >= desc.size() || -c2 >= desc.size()) {
			throw new FatalException(new SDMSMessage(env, "03117121559",
			                         "The number of the sortcolumn ($1) exceeds the number of columns ($2)",
			                         new Integer(c2), new Integer(desc.size())));
		}
		return new occomp(env, a);
	}

	public Comparator getComparator(SystemEnvironment env, int c[])
	throws SDMSException
	{
		int i;

		for(i = 0; i < c.length; i++) {
			if(c[i] >= desc.size() || -c[i] >= desc.size()) {
				throw new FatalException(new SDMSMessage(env, "03117121601",
				                         "The number of the sortcolumn ($1) exceeds the number of columns ($2)",
				                         new Integer(c[i]), new Integer(desc.size())));
			}
		}
		return new occomp(env, c);
	}

}

class occomp implements Comparator
{

	SystemEnvironment env;
	int collist[];

	public occomp(SystemEnvironment e, int c[])
	{
		env = e;
		collist = c;
	}

	public int compare(Object o1, Object o2)
	{
		int i, rc, dir;
		Vector v1, v2;

		try {
			v1 = (Vector) o1;
			v2 = (Vector) o2;
		} catch (ClassCastException cce) {
			throw new RuntimeException("Classes do not match: o1 = " + o1.toString() + ", o2 = " + o2.toString());
		}

		for(i = 0; i < collist.length; i++) {
			int c = collist[i];
			if(c < 0) {
				c = -c;
				dir = -1;
			} else dir = 1;

			if(c>v1.size()) {
				throw new RuntimeException("Sortcolumn outside of array bounds: c = " + c + ", array = " + v1.size());
			}
			Object ob1 = v1.get(c);
			Object ob2 = v2.get(c);

			if(ob1 == null) {
				if(ob2 == null) continue;
				return dir;
			}
			if(ob2 == null) return -dir;

			if(ob1 instanceof Boolean) {
				if(((Boolean)ob1).booleanValue()) {
					if(((Boolean)ob2).booleanValue()) continue;
					return dir;
				}
				if(((Boolean)ob2).booleanValue()) return -dir;
				continue;
			}

			rc = ((Comparable)ob1).compareTo(ob2);
			if(rc == 0) continue;
			return rc*dir;
		}

		return 0;
	}

	public boolean equals(Object obj)
	{
		return (obj == this);
	}
}
