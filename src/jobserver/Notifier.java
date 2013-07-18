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


package de.independit.scheduler.jobserver;

import java.util.*;

public class Notifier
{
	public static final String __version = "@(#) $Id: Notifier.java,v 2.4.14.1 2013/03/14 10:24:07 ronald Exp $";

	static public  Notifier notifier   = new Notifier();
	static private HashMap  threadHash = new HashMap();

	private Notifier ()
	{
	}

	synchronized static public void register(Long id, Thread thread)
	{
		threadHash.put(id, thread);
		Trace.debug("registered thread " + id);
	}

	synchronized static public void unregister(Long id)
	{
		threadHash.remove(id);
		Trace.debug("unregistered thread " + id);
	}

	synchronized static public void interrupt(Long id)
	{
		Thread sleepy = (Thread) threadHash.get(id);
		Trace.debug("Interrupting Thread : " + id);
		unregister(id);
		try {
			Trace.debug("Thread found : " + sleepy);
			if (sleepy != null)
				sleepy.interrupt();
		} catch (Exception e) {  }
	}
}
