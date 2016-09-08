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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;

public class SyncLock
{

	final ObjectLock lock;

	protected SyncLock(ObjectLock lock)
	{
		this.lock = lock;
	}

	protected void doWait(SystemEnvironment sysEnv)
		throws DeadlockException, InterruptedException
	{

		if (lock.wait) {
			if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
				System.out.println(Thread.currentThread().getName() + ":doWait() on " + lock.objectToShortString());

			lock.waiting = true;

			if (LockingSystem.DEADLOCK_TIMEOUT_MS == 0) {
				boolean tryAgain = false;
				try {
					LockingSystem.deadlockDetection(sysEnv, lock.thread);
				} catch (DeadlockException de) {
					if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_DEADLOCK_DETECTION)) != 0)
						System.out.println(Thread.currentThread().getName() + ":doWait() Deadlock[1] on " + toString());
					throw de;
				} catch (NotMyDeadlockException nmde) {

					tryAgain = true;
				}
				if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
					System.out.println(lock.dumpLockList());
				while (lock.wait) {
					synchronized (this) {
						wait(10);
					}
					if (tryAgain) {
						tryAgain = false;
						try {
							LockingSystem.deadlockDetection(sysEnv, lock.thread);
						} catch (DeadlockException de) {
							if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_DEADLOCK_DETECTION)) != 0)
								System.out.println(Thread.currentThread().getName() + ":doWait() Deadlock[2] on " + toString());
							throw de;
						} catch (NotMyDeadlockException nmde) {
							tryAgain = true;
						}
					}
				}
			} else {
				boolean deadlockDetect = true;
				long timeout = LockingSystem.DEADLOCK_TIMEOUT_MS;
				while (lock.wait) {
					if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
						System.out.println(lock.dumpLockList());
					synchronized (this) {
						if (deadlockDetect = true) timeout = LockingSystem.DEADLOCK_TIMEOUT_MS;
						else timeout = 5;
						wait(timeout);
					}
					if (lock.wait && deadlockDetect) {
						deadlockDetect = false;
						if ((LockingSystem.debug & LockingSystem.DEBUG_ALL) != 0)
							LockingSystemSynchronized.dump();
						try {
							LockingSystem.deadlockDetection(sysEnv, lock.thread);
						} catch (DeadlockException de) {
							if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_DEADLOCK_DETECTION)) != 0)
								System.out.println(Thread.currentThread().getName() + ":doWait() Deadlock[3] on " + toString());
							throw de;
						} catch (NotMyDeadlockException nmde) {

							deadlockDetect = true;
						}
					}
				}
			}
			lock.waiting = false;
			if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
				System.out.println(Thread.currentThread().getName() + ":doWait() after wait()");
		}
		LockingSystemSynchronized.resetWait(lock.thread);
	}

	protected synchronized void doNotify(SystemEnvironment sysEnv)
	{

		if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
			System.out.println(Thread.currentThread().getName() +
				":Entering doNotify() Thread " + lock.thread.getName() + toString());

		if (lock.notify) {

			if (lock.waiting) {
				if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
					System.out.println(Thread.currentThread().getName() +
						":doNotify() on " + toString() + "]");
				notify();
				if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
					System.out.println(Thread.currentThread().getName() +
						":Leaving doNotify() after notify()");
			} else {

				if ((LockingSystem.debug & (LockingSystem.DEBUG_ALL | LockingSystem.DEBUG_WAIT_AND_NOTIFY)) != 0)
					System.out.println(Thread.currentThread().getName() +
						":Leaving doNotify() on waiting = false");
			}
			lock.notify = false;

		}
	}

	protected synchronized void freeObjectLock(SystemEnvironment sysEnv)
	{
		ObjectLock.freeObjectLock(sysEnv, lock);
	}

	public String toString()
	{
		return lock.toString();
	}
}
