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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;

public class LockingSystemSynchronized
{

	private static HashMap<SDMSThread, HashMap<Object, ObjectLock>> threads = new HashMap<SDMSThread, HashMap<Object, ObjectLock>> ();

	private static HashMap<Object, ObjectLock> objectLocks = new HashMap<Object, ObjectLock> ();

	private static HashMap<SDMSThread, ObjectLock> waits = new HashMap<SDMSThread, ObjectLock> ();

	public static synchronized boolean isWait(SDMSThread t)
	{
		return (waits.get(t) != null);

	}

	public static synchronized String waitInfo(SDMSThread t)
	{
		ObjectLock lock = waits.get(t);
		if (lock == null) return "";
		return lock.objectToShortString();
	}

	protected static synchronized Vector<ObjectLock> release()
	{
		SDMSThread t = (SDMSThread)Thread.currentThread();
		if (t.lockThread != null) t = t.lockThread;
		HashMap<Object, ObjectLock> locksHeld = threads.get(t);
		if (locksHeld == null)
			return null;
		Iterator<ObjectLock> i = locksHeld.values().iterator();
		Vector<ObjectLock> locksToNotify = null;

		String out = null;
		String sep = null;
		if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_RELEASE)) != 0) {
			out = Thread.currentThread().getName() + ":Release:";
			sep = "";
		}

		while (i.hasNext()) {
			ObjectLock lock = i.next();

			if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_RELEASE)) != 0) {
				out = out + sep + lock.objectToShortString();
				sep = ", ";
			}

			Vector<ObjectLock> locksToNotifyForLock = releaseLock(lock);
			if (locksToNotifyForLock != null) {
				if (locksToNotify == null)
					locksToNotify = new Vector<ObjectLock> ();
				locksToNotify.addAll(locksToNotifyForLock);
			}

			lock.syncLock.freeObjectLock();
		}

		if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_RELEASE)) != 0) {
			System.out.println(out);
			out = Thread.currentThread().getName() + ": Notify:";
			sep = "";
			if (locksToNotify == null)
				System.out.println(out + "none");
			else {
				i = locksToNotify.iterator();
				while (i.hasNext()) {
					ObjectLock lock = i.next();
					out = out + sep + lock.objectToShortString();
					sep = ", ";
				}
				System.out.println(out);
			}
		}

		threads.put(t, null);

		return locksToNotify;
	}

	protected static synchronized Vector<ObjectLock> release(Object object)
	{
		SDMSThread t = (SDMSThread)Thread.currentThread();
		if (t.lockThread != null) t = t.lockThread;

		ObjectLock lock = getLockForThread(t, object);
		Vector<ObjectLock> locksToNotify = null;
		if (lock != null) {
			locksToNotify = releaseLock(lock);
			HashMap threadLocks = threads.get(t);
			threadLocks.remove(object);
			if (threadLocks.isEmpty())
				threads.put(t, null);

			lock.syncLock.freeObjectLock();

		}
		return locksToNotify;
	}

	protected static synchronized void resetWait(SDMSThread thread)
	{
		waits.put(thread, null);
	}

	public static ObjectLock getLockForThread (SDMSThread thread, Object object)
	{
		HashMap<Object, ObjectLock> threadLocks = threads.get(thread);
		if (threadLocks == null)
			return null;
		return threadLocks.get(object);
	}

	private static void registerLockForThread (SDMSThread thread, Object object, ObjectLock lock)
	{
		HashMap<Object, ObjectLock> threadLocks = threads.get(thread);
		if (threadLocks == null) {
			threadLocks = new HashMap<Object, ObjectLock> ();
			threads.put(thread, threadLocks);
		}
		threadLocks.put(object, lock);
	}

	protected static synchronized ObjectLock getLock(Object object, int mode)
	throws DeadlockException
	{

		if (object == null) throw new RuntimeException();

		SDMSThread t = (SDMSThread)Thread.currentThread();
		if (t.lockThread != null) t = t.lockThread;

		ObjectLock lock = getLockForThread(t, object);
		boolean escalateDeadlock = false;

		if (lock != null) {

			{
				ObjectLock q = lock;
				while (q != null) {
					if (q.wait && !q.notify) {
						System.out.println("Assertion failed for Thread " + Thread.currentThread().getName());
						System.out.println("Trying to lock ( mode = " + mode + " ) Object:" + lock.objectToShortString());
						System.out.println("Locks held on object " + lock.objectToShortString());
						ObjectLock ol = objectLocks.get(object);
						while (ol != null) {
							System.out.println("    Thread[" + ol.thread.getName() + "] mode = " +
							                   ol.mode + " wait = " + ol.wait);
							ol = ol.next;
						}

						throw new RuntimeException();
					}
					q = q.prev;
				}
			}

			if (mode == ObjectLock.EXCLUSIVE && lock.mode == ObjectLock.SHARED) {
				if ((LockingSystem.debug & LockingSystem.DEBUG_ALL) != 0)
					System.out.println(Thread.currentThread().getName() +
					                   ":Lock Escalation on Object[" +  ObjectLock.objectToShortString(object) + "]");

				lock.mode = ObjectLock.EXCLUSIVE;

				lock.escalated = true;

				ObjectLock p = lock;
				while (true) {
					if (p.next == null)

						break;
					if (p.next.mode == ObjectLock.EXCLUSIVE) {
						if (p.next.escalated) {
							if ((LockingSystem.debug & LockingSystem.DEBUG_ALL) != 0)
								System.out.println(Thread.currentThread().getName() +
								                   ":Escalate Deadlock Detected on Object[" +  ObjectLock.objectToShortString(object) + "]");

							escalateDeadlock = true;
						}

						break;
					} else

						if (p.next.wait && !p.next.notify) throw new RuntimeException();

					p = p.next;
				}

				if (lock != p) {

					if (lock.prev != null)
						lock.prev.next = lock.next;
					else {
						objectLocks.put(object, lock.next);
					}
					lock.next.prev = lock.prev;

					lock.prev = p;
					lock.next = p.next;

					p.next = lock;
					if (lock.next != null)

						lock.next.prev = lock;
				}
				if (lock.prev != null) {

					lock.wait = true;
				}
			}

		} else {

			lock = ObjectLock.getObjectLock(t, object, mode);
			registerLockForThread(t, object, lock);

			ObjectLock locks = objectLocks.get(object);

			if (locks == null) {

				objectLocks.put(object, lock);
			} else {

				if (mode == ObjectLock.EXCLUSIVE) {
					lock.wait = true;
				}
				ObjectLock p = locks;
				while (true) {
					if (p.mode == ObjectLock.EXCLUSIVE) {
						lock.wait = true;
					}
					if (p.next != null)
						p = p.next;
					else {
						p.next = lock;
						lock.prev = p;
						break;
					}
				}
			}
		}

		if (lock.wait) {
			if (lock.object == null) throw new RuntimeException();
			waits.put(t, lock);
		}

		ObjectLock firstLock = objectLocks.get(lock.object);

		if (firstLock.wait && !firstLock.notify) {
			if (firstLock == lock)
				System.out.println("Error while locking (firstlock == lock)" + lock.toString());
			else
				System.out.println("Error while locking  (firstlock != lock)" + firstLock.toString());
			dump();
			throw new RuntimeException();
		}

		if ((LockingSystem.debug & LockingSystem.DEBUG_ALL) != 0)
			System.out.println(Thread.currentThread().getName() +
			                   ":getLock() returned " +  lock.toString());

		if (escalateDeadlock)
			throw new DeadlockException();

		return lock;
	}

	private static synchronized Vector<ObjectLock> releaseLock(ObjectLock lock)
	{

		Vector<ObjectLock> locksToNotify = null;
		if (lock.next != null &&
		    lock.next.wait &&
		    (lock.prev == null ||
		     (lock.prev.mode == ObjectLock.SHARED &&
		      !lock.prev.wait &&
		      lock.next.mode == ObjectLock.SHARED
		     )
		    )
		   ) {
			ObjectLock p = lock.next;

			while (p != null) {
				if (p == lock.next || p.mode != ObjectLock.EXCLUSIVE) {

					if (p.wait) {
						if (locksToNotify == null)
							locksToNotify = new Vector<ObjectLock>();
						p.notify = true;
						p.wait = false;
						locksToNotify.add(p);
					}
				}
				if (p.mode == ObjectLock.EXCLUSIVE)

					break;
				p = p.next;
			}
		}

		if (lock.prev == null) {

			if (lock.object == null) throw new RuntimeException();
			objectLocks.put(lock.object, lock.next);
		} else

			lock.prev.next = lock.next;

		if (lock.next != null)

			lock.next.prev = lock.prev;

		ObjectLock firstLock = objectLocks.get(lock.object);
		if (firstLock != null)

			if (firstLock.wait) {
				if (locksToNotify == null || !locksToNotify.contains(firstLock)) {
					System.out.println("Error in release (firstlock is waiting)" + lock.toString());
					dump();
					throw new RuntimeException();
				}
			}

		return locksToNotify;
	}

	public static ObjectLock getObjectLocks(Object object)
	{
		return objectLocks.get(object);
	}

	protected static synchronized void deadlockDetection(SDMSThread thread, HashSet<SDMSThread> waiters)
	throws DeadlockException, NotMyDeadlockException
	{
		ObjectLock lock = waits.get(thread);
		if (lock == null)
			return;

		if (!lock.wait)
			return;
		if (waiters.contains(thread)) {
			SDMSThread t = (SDMSThread)Thread.currentThread();
			if (t.lockThread != null) t = t.lockThread;
			if (thread == t)
				throw new DeadlockException();
			else
				throw new NotMyDeadlockException();
		}
		waiters.add(thread);

		deadlockDetection(lock.object, waiters);
	}

	private static synchronized void deadlockDetection(Object object, HashSet<SDMSThread> waiters)
	throws DeadlockException, NotMyDeadlockException
	{
		ObjectLock lock = objectLocks.get(object);

		while (lock != null && !lock.wait) {
			try {
				deadlockDetection(lock.thread, waiters);
			} catch (DeadlockException de) {
				if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_DEADLOCK_DETECTION)) != 0)
					System.out.println(lock.objectToShortString() + " " + lock.dumpLockList ());
				throw de;
			} catch (NotMyDeadlockException nmde) {
				if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_DEADLOCK_DETECTION)) != 0)
					System.out.println(lock.objectToShortString() + " " + lock.dumpLockList ());
				throw nmde;
			}
			lock = lock.next;
		}
	}

	public synchronized static void checkAndDump()
	{
		boolean dump = true;
		Iterator<SDMSThread> iw = waits.keySet().iterator();
		while (iw.hasNext()) {
			SDMSThread thread = iw.next();
			ObjectLock lock = waits.get(thread);
			if (lock == null) {
				dump = false;
				break;
			}
		}
		if (dump) dump();
	}

	public synchronized static StringBuilder strDump()
	{
		StringBuilder b = new StringBuilder();
		StringBuilder tmp = new StringBuilder();

		b.append("= LockingSystem.dump() ==========================================================");
		b.append("\n");
		b.append("------- Locks per thread --------------------------------------------------------");
		b.append("\n");
		Iterator<SDMSThread> it = threads.keySet().iterator();
		while (it.hasNext()) {
			SDMSThread thread = it.next();
			HashMap<Object, ObjectLock> locks = threads.get(thread);
			int numlocks = 0;
			if (locks == null) {
				tmp.append("    No Locks held");
				tmp.append("\n");
			} else {
				Iterator<ObjectLock> io =locks.values().iterator();
				while (io.hasNext()) {
					ObjectLock lock = io.next();
					String os;
					if (lock.object instanceof SDMSObject)
						os = ((SDMSObject)lock.object).toShortString();
					else if (lock.object instanceof SDMSVersions)
						os = ((SDMSVersions)lock.object).toShortString();
					else if (lock.object instanceof SDMSIndexBucket)
						os = ((SDMSIndexBucket)lock.object).toShortString();
					else if (lock.object instanceof SDMSIndexMap)
						os = ((SDMSIndexMap)lock.object).toShortString();
					else if (lock.object != null)
						os = lock.object.toString();
					else
						os = "Oops! lock.object == null!";

					tmp.append("    Object[" + os + "] mode = " + lock.mode +
					           ", wait = " + lock.wait +
					           ", waiting = " + lock.waiting +
					           ", escalated=" + lock.escalated +
					           ", notify=" + lock.notify +
					           ", release=" + lock.release);
					tmp.append("\n");
					numlocks++;
				}
			}
			b.append("Locks (" + numlocks + ") held by thread " + thread.getName());
			b.append("\n");
			b.append(tmp);
			tmp.setLength(0);
		}
		b.append("------- Locks per object --------------------------------------------------------");
		b.append("\n");
		Iterator<Object> io = objectLocks.keySet().iterator();
		while (io.hasNext()) {
			Object object = io.next();
			ObjectLock lock = objectLocks.get(object);
			if (lock == null)
				continue;
			String os;
			if (lock.object instanceof SDMSObject)
				os = ((SDMSObject)lock.object).toShortString();
			else if (lock.object instanceof SDMSVersions)
				os = ((SDMSVersions)lock.object).toShortString();
			else if (lock.object instanceof SDMSIndexBucket)
				os = ((SDMSIndexBucket)lock.object).toShortString();
			else if (lock.object instanceof SDMSIndexMap)
				os = ((SDMSIndexMap)lock.object).toShortString();
			else if (lock.object != null)
				os = lock.object.toString();
			else
				os = "Oops! lock.object == null!";
			b.append("Locks held on object " + os);
			b.append("\n");
			while (lock != null) {

				b.append("    Thread[" + lock.thread.getName() + "] id = " + lock.id + ", mode = " + lock.mode +
				         ", wait = " + lock.wait +
				         ", waiting = " + lock.waiting +
				         ", escalated=" + lock.escalated +
				         ", notify=" + lock.notify +
				         ", release=" + lock.release);
				b.append("\n");
				lock = lock.next;
			}
		}
		b.append("------- Lock waits --------------------------------------------------------------");
		b.append("\n");
		Iterator<SDMSThread> iw = waits.keySet().iterator();
		while (iw.hasNext()) {
			SDMSThread thread = iw.next();
			String out = "Thread " + thread.getName() + " waits on ";
			ObjectLock lock = waits.get(thread);
			if (lock != null)
				b.append(out + lock.toString() + "\n" + lock.dumpLockList ());
			else
				b.append(out + "nothing");
			b.append("\n");
		}
		b.append("=================================================================================");
		b.append("\n");

		return b;
	}

	public static void dump ()
	{
		System.out.println(strDump());
	}
}
