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
package de.independit.scheduler.server.locking;

import java.util.*;
import java.io.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.util.*;

public class ObjectLock
{

	private static final boolean reUseLocks = true;

	public static final int SHARED = 0;
	public static final int EXCLUSIVE = 1;

	public static final String S_SHARED = "S";
	public static final String S_EXCLUSIVE = "X";

	public static final String S_WAIT = "W";
	public static final String S_NOWAIT = "N";

	public boolean wait;
	protected boolean waiting;
	protected boolean escalated;

	protected boolean notify;

	private static ObjectLock unusedLocks = null;

	protected int id;
	public Object object = null;
	protected SDMSThread thread = null;
	protected SyncLock syncLock;

	public int mode = 0;
	protected ObjectLock next = null;
	protected ObjectLock prev = null;
	public static long lockHWM = 0;
	public static long lockUsed = 0;
	public static long lockRequest = 0;
	public static long lockDiscarded = 0;
	public static long lockHWMdelta = 0;

	private static int lastId = 0;

	public long createCp;

	public String stackTrace = null;
	public String freeStackTrace = null;

	protected ObjectLock(SDMSThread thread, Object object, int mode, long createCp)
	{
		id = lastId;
		lastId = lastId + 1;
		syncLock = new SyncLock(this);
		initialize(thread, object, mode, createCp);
	}

	protected static synchronized void freeObjectLock(SystemEnvironment sysEnv, ObjectLock objectLock)
	{

		if (objectLock.thread == null || !sysEnv.thread.equals(objectLock.thread)) {
			System.out.println("ObjectLock:freeObjectLock: Oops, trying to free a lock which doesnt belong to our thread !");
			if (objectLock.freeStackTrace != null) {
				System.out.println("Lock has been freed at:\n" + objectLock.freeStackTrace);
				System.out.println("Current Stack Trace:");
				System.out.println(getStackTrace());
			}
			return;
		}

		lockUsed--;
		if (objectLock.notify) {
			lockDiscarded++;
			lockHWMdelta++;
			return;
		}

		objectLock.object = null;
		objectLock.thread = null;

		objectLock.prev = null;
		if (reUseLocks) {
			objectLock.next = unusedLocks;
			unusedLocks = objectLock;
			if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_FREE)) != 0)
				objectLock.freeStackTrace = getStackTrace();
		} else {
			objectLock.next = null;
		}
	}

	protected static synchronized ObjectLock getObjectLock(SDMSThread thread, Object object, int mode, long createCp)
	{
		ObjectLock lock = null;
		if (unusedLocks == null || !reUseLocks) {
			lock = new ObjectLock(thread, object, mode, createCp);
			if (lockHWMdelta > 0)
				lockHWMdelta--;
			else
				lockHWM++;
		} else {
			lock = unusedLocks;
			unusedLocks = lock.next;
			lock.initialize(thread, object, mode, createCp);
		}
		lockUsed++;
		lockRequest++;
		return lock;
	}

	private void initialize(SDMSThread thread, Object object, int mode, long createCp)
	{
		if (object == null) throw new RuntimeException();
		this.object = object;
		this.thread = thread;
		this.mode = mode;
		this.next = null;
		this.prev = null;
		this.createCp = createCp;
		wait = false;
		waiting = false;
		escalated = false;

		notify = false;

		if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_STACK_TRACES)) != 0)
			this.stackTrace = getStackTrace();
		if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_FREE)) != 0)
			this.freeStackTrace = null;
	}

	public boolean releaseAllowed(SystemEnvironment sysEnv)
	{
		boolean ok = true;
		if (object instanceof SDMSVersions) {
			if (mode == EXCLUSIVE && ((SDMSVersions)object).tx == sysEnv.tx && ((SDMSVersions)object).o_v != null && ((SDMSVersions)object).o_v.size() > 0)
				ok = false;
		}
		return ok;
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
					"}";
			else
				out = out + sep + "[Thread " + threadName +
					", " + lock.modeToString() +

					", wait=" + lock.wait +
					", waiting=" + lock.waiting +
					", escalated=" + lock.escalated +
					", notify=" + lock.notify +
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

	static public String getStackTrace()
	{
		Exception e = new Exception();
		StringWriter stackTrace = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTrace));
		return stackTrace.toString().intern();
	}

}
