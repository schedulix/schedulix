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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.filter.*;
import de.independit.scheduler.server.util.*;

public class ObjectFilter
{

	public final static String __version = "@(#) $Id: ObjectFilter.java,v 2.7.2.1 2013/03/14 10:24:42 ronald Exp $";

	public boolean hasFuture;
	public boolean mastersFirst;
	public Vector masterVector;
	public Vector jobVector;
	private Vector filter = null;

	public ObjectFilter()
	{
		hasFuture = false;
		mastersFirst = false;
		masterVector = null;
		jobVector = null;
	}

	public Vector initialize_filter(SystemEnvironment sysEnv, Vector fi, int level, boolean positive)
		throws SDMSException
	{
		Vector subFilter = new Vector();
		Vector restartableVector = new Vector();

		if(fi == null) return subFilter;
		if(positive)	subFilter.addElement(Boolean.TRUE);
		else		subFilter.addElement(Boolean.FALSE);
		for(int i = 0; i < fi.size(); i ++) {
			Vector fa = (Vector) fi.get(i);
			Vector item = new Vector();
			for(int j = 0; j < fa.size(); j++) {
				WithItem w = (WithItem) fa.get(j);
				if(((String) w.key).equals(ParseStr.S_EXIT_STATUS)) {
					item.addElement(new ExitStateFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_MERGED)) {
					item.addElement(new MergedExitStateFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_JOB_STATUS)) {
					item.addElement(new JobStateFilter(sysEnv, (HashSet) w.value));
				} else if(((String) w.key).equals(ParseStr.S_NAME)) {
					item.addElement(new SeNameFilter(sysEnv, (String) w.value));
				} else if(((String) w.key).equals(ParseStr.S_NODE)) {
					item.addElement(new NodeFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_JOB_SERVER)) {
					item.addElement(new JobServerFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_SUBMITTING)) {
					item.addElement(new SubmitUserFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_OWNER)) {
					item.addElement(new OwnerFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_RESTARTABLE)) {
					RestartableFilter r = new RestartableFilter(sysEnv, (Integer) w.value, mastersFirst);
					item.addElement(r);
					restartableVector.add(r);
				} else if(((String) w.key).equals(ParseStr.S_WARNING)) {
					item.addElement(new WarningFilter(sysEnv, (Boolean) w.value));
				} else if(((String) w.key).equals(ParseStr.S_MASTER)) {
					item.addElement(new MasterFilter(sysEnv, (Boolean) w.value));
					if(level == 0 && fi.size() == 1) {
						mastersFirst = true;
						for (int k = 0; k < restartableVector.size(); ++k) {
							RestartableFilter r = (RestartableFilter) restartableVector.get(k);
							r.mastersFirst = true;
						}
					}
				} else if(((String) w.key).equals(ParseStr.S_MASTER_ID)) {
					item.addElement(new MasterIdFilter(sysEnv, (Vector) w.value));
					if(level == 0 && fi.size() == 1)
						masterVector = (Vector) w.value;
				} else if(((String) w.key).equals(ParseStr.S_FUTURE)) {
					item.addElement(new FutureFilter(sysEnv, (WithHash) w.value));
					hasFuture = true;
				} else if(((String) w.key).equals(ParseStr.S_HISTORY)) {
					final Object val = w.value;
					if (val instanceof java.util.Vector) {
						item.addElement(new HistoryFilter(sysEnv, (WithHash) ((Vector) val).get(0), (WithHash) ((Vector) val).get(1)));
					} else {
						item.addElement(new HistoryFilter(sysEnv, (WithHash) val));
					}
				} else if(((String) w.key).equals(ParseStr.S_NAMELIST)) {
					item.addElement(new NamelistFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_FILTER)) {
					Vector v = null;
					int inc = 1;
					if(level == 0 && fi.size() == 1 && fa.size() == 1)
						inc = 0;
					v = initialize_filter(sysEnv, (Vector) w.value, level + inc, true);
					item.addElement(v);
				} else if(((String) w.key).equals(ParseStr.S_NEGFILTER)) {
					Vector v = null;
					int inc = 1;
					if(level == 0 && fi.size() == 1 && fa.size() == 1)
						inc = 0;
					v = initialize_filter(sysEnv, (Vector) w.value, level + inc, false);
					item.addElement(v);
				} else if(((String) w.key).equals(ParseStr.S_PARAMETERS)) {
					item.addElement(new ParameterFilter(sysEnv, (WithHash) w.value));
				} else if(((String) w.key).equals(ParseStr.S_JOB)) {
					item.addElement(new JobidFilter(sysEnv, (Vector) w.value));
					if(level == 0 && fi.size() == 1) {
						if(jobVector == null) {
							jobVector = (Vector) w.value;
						}
					}
				} else if(((String) w.key).equals(ParseStr.S_USAGE)) {
					item.addElement(new UsageFilter(sysEnv, (Vector) w.value));
				} else if(((String) w.key).equals(ParseStr.S_NRNAME)) {
					item.addElement(new NrNameFilter(sysEnv, (String) w.value));
				} else if(((String) w.key).equals(ParseStr.S_ENABLE)) {
					item.addElement(new SmeDisabledFilter(sysEnv, (Boolean) w.value));
				} else {
				}
			}
			subFilter.addElement(item);
		}
		filter = subFilter;
		return subFilter;
	}

	public void setFilter(Vector filter)
	{
		this.filter = filter;
	}

	public boolean checkPrivileges(SystemEnvironment sysEnv, SDMSProxy p)
		throws SDMSException
	{
		SDMSPrivilege priv = p.getPrivileges(sysEnv);
		if (priv.can(SDMSPrivilege.VIEW) || priv.can(SDMSPrivilege.CREATE_PARENT_CONTENT)) return true;
		return false;
	}

	public boolean doFilter(SystemEnvironment sysEnv, SDMSProxy p)
		throws SDMSException
	{
		return doFilter(sysEnv, p, filter);
	}

	public boolean doFilter(SystemEnvironment sysEnv, SDMSProxy p, Vector subFilter)
		throws SDMSException
	{
		int i, j;
		boolean b;

		if (!checkPrivileges(sysEnv, p)) return false;

		if(subFilter == null || subFilter.size() == 0) return true;
		boolean dir = ((Boolean) subFilter.get(0)).booleanValue();
		for(i = 1; i < subFilter.size(); i++) {
			Vector item = (Vector) subFilter.get(i);
			for(j = 0; j < item.size(); j++) {
				Object o = item.get(j);
				if(o instanceof Vector)
					b = doFilter(sysEnv, p, (Vector) o);
				else
					b = ((Filter) o).valid(sysEnv, p);
				if(!b) break;
			}
			if(j == item.size()) return (dir ? true : false);
		}
		return (dir ? false : true);
	}
}
