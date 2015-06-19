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
package de.independit.scheduler.locking;

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.locking.*;
import de.independit.scheduler.server.repository.*;

public class ObjectLock
{

	public static final int SHARED = 0;
	public static final int EXCLUSIVE = 1;

	public static final String S_SHARED = "S";
	public static final String S_EXCLUSIVE = "X";

	public static final String S_WAIT = "W";
	public static final String S_NOWAIT = "N";

	public boolean wait;
	public boolean waiting;
	public boolean escalated;
	public boolean notify;
	public boolean release;

	private static ObjectLock unusedLocks = null;

	protected int id;
	protected Object object = null;
	protected SDMSThread thread = null;
	protected SyncLock syncLock;

	protected int mode = 0;
	protected ObjectLock next = null;
	protected ObjectLock prev = null;

	public void setWait(boolean wait)
	{
		this.wait = wait;
		if (wait) {
			System.out.println("ObjectLock.setWait(true) for ObjectLock[" + id + "] in Thread " + Thread.currentThread().getName());
			Thread.currentThread().dumpStack();
		}
	}

	private static int lastId = 0;

	protected ObjectLock(SDMSThread thread, Object object, int mode)
	{
		id = lastId;
		lastId = lastId + 1;
		syncLock = new SyncLock(this);
		initialize(thread, object, mode);
	}

	protected static synchronized void freeObjectLock(ObjectLock objectLock)
	{

		objectLock.object = null;
		objectLock.thread = null;

		objectLock.next = unusedLocks;
		objectLock.prev = null;

		unusedLocks = objectLock;
	}

	protected static synchronized ObjectLock getObjectLock(SDMSThread thread, Object object, int mode)
	{
		ObjectLock lock = null;
		if (unusedLocks == null) {
			lock = new ObjectLock(thread, object, mode);
		} else {
			lock = unusedLocks;
			unusedLocks = lock.next;
			lock.initialize(thread, object, mode);
		}
		return lock;
	}

	private void initialize(SDMSThread thread, Object object, int mode)
	{
		this.object = object;
		this.thread = thread;
		this.mode = mode;
		this.next = null;
		this.prev = null;

		wait = false;
		waiting = false;
		escalated = false;
		notify = false;
		release = false;
	}

	public String modeToString()
	{
		if (mode == SHARED)
			return S_SHARED;
		else
			return S_EXCLUSIVE;
	}

	public String toString()
	{
		if (object != null) {

			return "ObjectLock[" + id + "] on " + objectToShortString() + "[" +
			       "mode=" + modeToString() +
			       ", wait=" + wait +
			       ", waiting=" + waiting +
			       ", escalated=" + escalated +
			       ", notify=" + notify +
			       ", release=" + release +
			       "]";
		} else
			return "ObjectLock[" + id + "] uninitialized";
	}

	public String dumpLockList ()
	{
		ObjectLock lock = this;
		while (lock.prev != null) {
			lock = lock.prev;
		}
		String out = "";
		String sep = "";
		while (lock != null) {
			String threadName = "Oops, lock.thread == null!";
			if (lock.thread != null) threadName = lock.thread.getName();
			if (lock == this)
				out = out + sep + "{Thread " + threadName +
				      ", " + lock.modeToString() +

				      ", wait=" + lock.wait +
				      ", waiting=" + lock.waiting +
				      ", escalated=" + lock.escalated +
				      ", notify=" + lock.notify +
				      ", release=" + lock.release +
				      "}";
			else
				out = out + sep + "[Thread " + threadName +
				      ", " + lock.modeToString() +

				      ", wait=" + lock.wait +
				      ", waiting=" + lock.waiting +
				      ", escalated=" + lock.escalated +
				      ", notify=" + lock.notify +
				      ", release=" + lock.release +
				      "]";
			sep = " -> ";
			lock = lock.next;
		}
		return out;
	}

	public static String objectToShortString(Object object)
	{
		String os;
		if (object instanceof SDMSObject)
			os = ((SDMSObject)object).toShortString();
		else if (object instanceof SDMSVersions)
			os = ((SDMSVersions)object).toShortString();
		else if (object instanceof SDMSIndexBucket)
			os = ((SDMSIndexBucket)object).toShortString();
		else if (object instanceof SDMSIndexMap)
			os = ((SDMSIndexMap)object).toShortString();
		else if (object != null)
			os = object.toString();
		else
			os = "Oops! lock.object == null!";
		return os;
	}

	public String objectToShortString()
	{
		String os;
		if (object instanceof SDMSObject)
			os = ((SDMSObject)object).toShortString();
		else if (object instanceof SDMSVersions)
			os = ((SDMSVersions)object).toShortString();
		else if (object instanceof SDMSIndexBucket)
			os = ((SDMSIndexBucket)object).toShortString();
		else if (object instanceof SDMSIndexMap)
			os = ((SDMSIndexMap)object).toShortString();
		else if (object != null)
			os = object.toString();
		else
			os = "Oops! lock.object == null!";
		return os;
	}
}
