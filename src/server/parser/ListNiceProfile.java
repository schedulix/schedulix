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

public class ListNiceProfile extends Node
{

	public ListNiceProfile()
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Date d = new Date();
		Long activeTs;
		int seq = 0;

		desc.add("ID");
		desc.add("NAME");

		desc.add("IS_ACTIVE");

		desc.add("ACTIVE_TS");

		desc.add("ACTIVE_SEQ");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv,
		                                      new SDMSMessage (sysEnv, "03408110804", "List of Nice Profiles"), desc);

		Iterator i = SDMSNiceProfileTable.table.iterator(sysEnv);
		SDMSNiceProfile np;
		Vector<SDMSNiceProfile> npv = new Vector<SDMSNiceProfile>();

		while(i.hasNext()) {
			np = (SDMSNiceProfile)(i.next());
			npv.add(np);
		}

		Collections.sort(npv, new NpComparator(sysEnv));

		Vector v;
		i = npv.iterator();
		while(i.hasNext()) {
			np = (SDMSNiceProfile)(i.next());
			v = new Vector();
			v.add(np.getId(sysEnv));
			v.add(np.getName(sysEnv));
			v.add(np.getIsActive(sysEnv));
			activeTs = np.getActiveTs(sysEnv);
			if (activeTs != null) {
				d.setTime(activeTs.longValue());
				v.add(sysEnv.systemDateFormat.format(d));
				seq++;
				v.add(new Integer(seq));
			} else {
				v.add(null);
				v.add(null);
			}
			v.add(np.getPrivileges(sysEnv).toString());
			d_container.addData(sysEnv, v);
		}

		Collections.sort(d_container.dataset , d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03408110806", "$1 Nice Profile(s) found", new Integer(d_container.lines)));
	}
}

class NpComparator implements Comparator<SDMSNiceProfile>
{

	SystemEnvironment sysEnv;

	public NpComparator(SystemEnvironment env)
	{
		sysEnv = env;
	}

	public int compare(SDMSNiceProfile np1, SDMSNiceProfile np2)
	{
		Long a1, a2;
		try {
			a1 = np1.getActiveTs(sysEnv);
			a2 = np2.getActiveTs(sysEnv);
		} catch(SDMSException e) {
			throw new IllegalArgumentException(e.toString());
		}

		if (a1 == null && a2 == null) return 0;
		if (a1 != null && a2 == null) return -1;
		if (a1 == null && a2 != null) return 1;

		if (a1.longValue() == a2.longValue()) return 0;
		if (a1.longValue() < a2.longValue()) return 1;
		return -1;
	}

}
