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

import java.util.Iterator;
import java.util.Vector;
import java.util.HashSet;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class LockingSystem
{

	public static int DEBUG_ALL = 1;
	public static int DEBUG_WAIT_AND_NOTIFY = 2;
	public static int DEBUG_RELEASE = 4;
	public static int DEBUG_DEADLOCK_DETECTION = 8;
	public static int DEBUG_SPECIAL = 16;
	public static int DEBUG_STACK_TRACES = 32;
	public static int DEBUG_FREE = 64;

	public static int debug = 0;

	public static long DEADLOCK_TIMEOUT_MS = 0;

	private static Object deadlockDetectionLock = new Object();
	private static SDMSThread deadlockDetectionThread = null;
	private static int deadlockDetectionWaitCount = 0;

	public static void lock(SystemEnvironment sysEnv, Object object, int mode)
		throws DeadlockException, InterruptedLockException
	{
		SDMSThread lt = sysEnv.thread;
		if (lt.lastSerializationException != null) {
			System.out.println("Unhandled SerializationException " + lt.lastSerializationException.toString() + " in Thread " + Thread.currentThread().getName());
			System.out.println("StackTrace:");
			new Exception().printStackTrace();
			System.out.println("StackTrace of unhandled Exception:");
			lt.lastSerializationException.printStackTrace();
			System.exit(0);
		}
		if ((LockingSystem.debug & LockingSystem.DEBUG_ALL) != 0)
			System.out.println(Thread.currentThread().getName() +
				":LockingSystem.lock("  + ObjectLock.objectToShortString(object) + ", mode = "+ mode + ")");
		ObjectLock lock = LockingSystemSynchronized.getLock(sysEnv, object, mode);
		if (lock.wait)
			try {
				lock.syncLock.doWait(sysEnv);
				if (lock.wait) {
					System.out.println("doWait() returned with ObjectLockWait Flag still set in Thread "+ Thread.currentThread().getName());
					new Exception().printStackTrace();
					System.exit(0);
				}
			} catch (DeadlockException de) {
				lt.lastSerializationException = de;
				LockingSystemSynchronized.resetWait(lt);
				throw de;
			} catch (InterruptedException ie) {
				InterruptedLockException ile = new InterruptedLockException();
				lt.lastSerializationException = ile;
				LockingSystemSynchronized.resetWait(lt);
				throw new InterruptedLockException();
			}
	}

	protected static void deadlockDetection(SystemEnvironment sysEnv, SDMSThread thread)
		throws DeadlockException, NotMyDeadlockException
	{
		startDeadlockDetection(thread);
		if ((debug & (DEBUG_ALL | DEBUG_DEADLOCK_DETECTION)) != 0)
			System.out.println(Thread.currentThread().getName() + ":DeadlockDetection started");

		try {
			LockingSystemSynchronized.deadlockDetection(sysEnv, thread, new HashSet<SDMSThread> ());
		} catch(NotMyDeadlockException nmde) {
			endDeadlockDetection();
			throw nmde;
		} catch(DeadlockException mde) {
			String stackTrace = ObjectLock.getStackTrace();
			DeadlockException.countAndTraceDeadlock(stackTrace);
			throw mde;
		}

		if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_DEADLOCK_DETECTION)) != 0)
			System.out.println(Thread.currentThread().getName() + ":DeadlockDetection finished");
		endDeadlockDetection();
	}

	private static void startDeadlockDetection(SDMSThread thread)
	{
		if ((debug & (DEBUG_ALL | DEBUG_DEADLOCK_DETECTION)) != 0)
			System.out.println(Thread.currentThread().getName() + ":startDeadlockDetection check for running deadlock detection");
		synchronized (deadlockDetectionLock) {
			if (deadlockDetectionThread != null) {
				if ((debug & (DEBUG_ALL | DEBUG_DEADLOCK_DETECTION)) != 0)
					System.out.println(Thread.currentThread().getName() + ":startDeadlockDetection start wait for running deadlock detection");
				try {
					deadlockDetectionWaitCount ++;
					deadlockDetectionLock.wait();
					if ((debug & (DEBUG_ALL | DEBUG_DEADLOCK_DETECTION)) != 0)
						System.out.println(Thread.currentThread().getName() + ":startDeadlockDetection end wait for running deadlock detection");
				} catch (InterruptedException ie) {  }
				finally {
					deadlockDetectionWaitCount --;
				}
			}
			deadlockDetectionThread = thread;
		}
	}

	private static void endDeadlockDetection()
	{
		synchronized (deadlockDetectionLock) {
			deadlockDetectionThread = null;
			if (deadlockDetectionWaitCount > 0) {
				if ((debug & (DEBUG_ALL | DEBUG_DEADLOCK_DETECTION)) != 0)
					System.out.println(Thread.currentThread().getName() + ":endDeadlockDetection notify waiting deadlock detection");
				deadlockDetectionLock.notify();
			} else if ((debug & (DEBUG_ALL | DEBUG_DEADLOCK_DETECTION)) != 0)
				System.out.println(Thread.currentThread().getName() + ":endDeadlockDetection no waiting deadlock detection");
		}
	}

	public static void release(SystemEnvironment sysEnv)
		throws FatalException
	{
		sysEnv.thread.lastSerializationException = null;

		notifyLocks(sysEnv, LockingSystemSynchronized.release(sysEnv));

		if (deadlockDetectionThread == sysEnv.thread) {
			if ((debug & (DEBUG_ALL | DEBUG_DEADLOCK_DETECTION)) != 0)
				System.out.println(Thread.currentThread().getName() + ":release closing deadlock detection");
			endDeadlockDetection();
		}
	}

	public static void releaseSubTxLocks(SystemEnvironment sysEnv, long checkPoint)
		throws FatalException
	{
		notifyLocks(sysEnv, LockingSystemSynchronized.releaseSubTxLocks(sysEnv, checkPoint));
	}

	public static void release(SystemEnvironment sysEnv, Object object)
		throws FatalException
	{
		notifyLocks(sysEnv, LockingSystemSynchronized.release(sysEnv, object));
	}

	public static void releaseToCheckPoint(SystemEnvironment sysEnv, Object object, long checkPoint)
		throws FatalException
	{
		notifyLocks(sysEnv, LockingSystemSynchronized.releaseToCheckPoint(sysEnv, object, checkPoint));
	}

	private static void notifyLocks(SystemEnvironment sysEnv, Vector<ObjectLock> locks)
	{
		if (locks == null) return;
		Iterator<ObjectLock> i = locks.iterator();
		while (i.hasNext()) {
			ObjectLock lock = i.next();
			lock.syncLock.doNotify(sysEnv);
		}
	}
}
