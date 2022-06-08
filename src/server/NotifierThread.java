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

package de.independit.scheduler.server;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.sql.*;
import java.math.*;

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.timer.*;

public class NotifierThread extends InternalSession
{

	public final static String name = "Notifier";

	private HashMap<Long,Integer> jsToNotify;

	public NotifierThread(SystemEnvironment env, SyncFifo f)
	throws SDMSException
	{
		super(name);
		NR = 1234325;
		initThread(env, f, NR, name, env.notifyDelay * 1000);
	}

	public synchronized void initNotifierThread(SystemEnvironment sysEnv)
	throws SDMSException
	{
		jsToNotify = new HashMap<Long,Integer>();
	}

	protected Node getNode(int m)
	{
		return new DoNotify();
	}

	public synchronized void addJobServerToNotify(Long id)
	{
		Integer cnt = jsToNotify.get(id);
		if (cnt == null) {
			jsToNotify.put(id, SDMSConstants.iZERO);
		}
	}

	public synchronized void removeFromPingList(Long id)
	{
		jsToNotify.remove(id);
	}

	public synchronized Integer getFromPingList(Long id)
	{
		return jsToNotify.get(id);
	}

	private synchronized void incPingList(Long id)
	{
		Integer cnt = jsToNotify.get(id);
		if (cnt != null) {
			jsToNotify.put(id, Integer.valueOf(cnt.intValue() + 1));
		}
	}

	private synchronized Vector getJobServerList()
	{
		Vector v = new Vector();
		v.addAll(jsToNotify.keySet());
		return v;
	}

	protected void doNotify(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector v = getJobServerList();
		if (v == null) return;

		for (int i = 0; i < v.size(); ++i) {
			Long sId = (Long) v.get(i);
			try {
				Integer cnt = getFromPingList(sId);
				if (cnt != null && cnt.intValue() < 5) {
					SDMSScope s = SDMSScopeTable.getObject(sysEnv, sId);
					doTrace(null, "Notifying " + s.pathString(sysEnv) + " (" + s.getId(sysEnv) + ")", SEVERITY_DEBUG);
					s.notify(sysEnv);
					incPingList(sId);
				}
			} catch (NotFoundException nfe) {
				removeFromPingList(sId);
			}
		}
	}
}

class DoNotify extends Node
{
	public DoNotify()
	{
		super();
		auditFlag = false;
		txMode = SDMSTransaction.READONLY;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SystemEnvironment.notifier.doNotify(sysEnv);
	}

}
