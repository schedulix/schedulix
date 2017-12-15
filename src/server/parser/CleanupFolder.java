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

		HashSet<Long> seIds = new HashSet<Long> ();
		it = folders.iterator();
		while (it.hasNext()) {
			SDMSFolder f = (SDMSFolder) it.next();
			f.collectSeIds(sysEnv, seIds, keeplist);
		}

		SDMSSchedulingEntity.delete(sysEnv, seIds, force);

		it = keeplist.iterator();
		while (it.hasNext()) {
			Long seId = (Long) it.next();
			Vector tv = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
			for (int i = 0; i < tv.size(); ++i) {
				SDMSTrigger t = (SDMSTrigger) tv.get(i);
				if (t.getIsInverse(sysEnv).booleanValue())
					continue;
				if (!keeplist.contains(t.getId(sysEnv)))
					t.delete(sysEnv);
			}
			tv = SDMSTriggerTable.idx_seId.getVector(sysEnv, seId);
			for (int i = 0; i < tv.size(); ++i) {
				SDMSTrigger t = (SDMSTrigger) tv.get(i);
				if (!t.getIsInverse(sysEnv).booleanValue())
					continue;
				if (!keeplist.contains(t.getId(sysEnv)))
					t.delete(sysEnv);
			}
			Vector rtv = SDMSResourceTemplateTable.idx_seId.getVector(sysEnv, seId);
			for (int i = 0; i < rtv.size(); ++i) {
				SDMSResourceTemplate rt = (SDMSResourceTemplate) rtv.get(i);
				if (!keeplist.contains(rt.getId(sysEnv)))
					rt.delete(sysEnv);
			}
			Vector rv = SDMSResourceTable.idx_scopeId.getVector(sysEnv, seId);
			for (int i = 0; i < rv.size(); ++i) {
				SDMSResource r = (SDMSResource) rv.get(i);
				if (!keeplist.contains(r.getId(sysEnv)))
					r.delete(sysEnv);
			}
		}

		it = folders.iterator();
		while (it.hasNext()) {
			SDMSFolder f = (SDMSFolder) it.next();
			f.deleteCascade(sysEnv, keeplist);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "02707191116", "Cleanup completed"));
	}
}
