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

public class ListExitStateTranslation extends Node
{

	public final static String __version = "@(#) $Id: ListExitStateTranslation.java,v 2.3.8.3 2013/03/20 06:42:59 ronald Exp $";

	public ListExitStateTranslation()
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_EXIT_STATE_TRANSLATION);
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("NAME");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage (sysEnv, "02111281855", "List of Exit State Translations"), desc);

		Vector estp_v = new Vector();
		Iterator i = SDMSExitStateTranslationProfileTable.table.iterator(sysEnv);
		while(i.hasNext()) {
			estp_v.add(i.next());
		}

		SDMSExitStateTranslationProfile estp;

		i = estp_v.iterator();

		Vector v;
		while(i.hasNext()) {
			estp = (SDMSExitStateTranslationProfile)(i.next());

			v = new Vector();
			v.add(estp.getId(sysEnv));
			v.add(estp.getName(sysEnv));
			v.add(estp.getPrivileges(sysEnv).toString());
			d_container.addData(sysEnv, v);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "02111281857",
		                        "$1 Exit State Translations(s) found", Integer.valueOf(d_container.lines)));
	}
}

