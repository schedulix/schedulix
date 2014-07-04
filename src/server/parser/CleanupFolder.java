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

import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class CleanupFolder
	extends Node
{
	public static final String __version = "@(#) $Id: CleanupFolder.java,v 2.5.8.1 2013/03/14 10:24:24 ronald Exp $";

	private final Vector cl;
	private Vector kl;
	final boolean force;

	public CleanupFolder (final Vector cl, final WithHash w)
	{
		super();

		this.cl = cl;

		if (w == null) {
			kl = new Vector();
			force = false;
		} else {
			force = w.containsKey (ParseStr.S_FORCE) && ((Boolean) w.get (ParseStr.S_FORCE)).booleanValue();

			if (! w.containsKey (ParseStr.S_KEEP) || (kl = (Vector) w.get (ParseStr.S_KEEP)) == null)
				kl = new Vector();
		}
	}

	public void go (final SystemEnvironment sysEnv)
		throws SDMSException
	{

		Vector folders = new Vector();
		Iterator it = cl.iterator();
		while (it.hasNext()) {
			ObjectURL url = (ObjectURL) it.next();
			SDMSProxy p = url.resolve(sysEnv);
			if (p instanceof SDMSFolder) {
				SDMSFolder f = (SDMSFolder)url.resolve(sysEnv);
				folders.add(f);
			} else {
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03911161555", "The folder specified isn't a folder"));

			}
		}

		HashSet keeplist = new HashSet();
		it = kl.iterator();
		while (it.hasNext()) {
			ObjectURL url = (ObjectURL) it.next();
			url.resolve(sysEnv);
			keeplist.add(url.objId);
		}

		it = folders.iterator();
		while (it.hasNext()) {
			SDMSFolder f = (SDMSFolder) it.next();
			f.deleteCascadeFirstPass(sysEnv, keeplist);
		}

		HashSet parameterLinks = new HashSet();

		it = folders.iterator();
		while (it.hasNext()) {
			SDMSFolder f = (SDMSFolder) it.next();
			f.deleteCascadeSecondPass(sysEnv, parameterLinks, force, keeplist);
			f.dropResources(sysEnv, keeplist);
		}

		if(!parameterLinks.isEmpty()) {
			Iterator i = parameterLinks.iterator();
			SDMSParameterDefinition pd = SDMSParameterDefinitionTable.getObject(sysEnv, (Long) i.next());
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, pd.getSeId(sysEnv));
			throw new CommonErrorException (new SDMSMessage(sysEnv, "02707191115", "Parameter Reference from $1 ($2) still exists",
						se.pathString(sysEnv),
						pd.getName(sysEnv)));
		}
		result.setFeedback(new SDMSMessage(sysEnv, "02707191116", "Cleanup completed"));
	}
}
