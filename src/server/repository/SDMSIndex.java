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

package de.independit.scheduler.server.repository;

import java.lang.*;
import java.util.*;
import java.io.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.locking.*;

public class SDMSIndex
{

	public final static String __version = "@(#) $Id: SDMSIndex.java,v 2.7.2.3 2013/06/07 18:48:36 dieter Exp $";

	public static final int ORDINARY = 0;

	public static final int UNIQUE   = 1;

	public static final int IDUNIQUE = 3;

	private SDMSIndexMap hashMap;
	private int type;
	private boolean isVersioned;

	public SDMSTable table;
	public String indexName;

	private static final Comparator objectIdComparator = new ObjectIdComparator();

	public SDMSIndex (SystemEnvironment env, int t, boolean versioned, SDMSTable table, String indexName)
		throws SDMSException
	{
		if(t != ORDINARY &&
		   t != UNIQUE   &&
		   t != IDUNIQUE) {
			throw new FatalException(new SDMSMessage(env,
						"03110181526", "Invalid Indextype $1", new Integer(t)));

		}
		this.table = table;
		this.indexName = indexName;
		hashMap = new SDMSIndexMap(this);
		type = t;
		isVersioned = versioned;
	}

	public void put (SystemEnvironment env, Object key, SDMSObject o)
		throws SDMSException
	{
		put (env, key, o, true);
	}

	public void put (SystemEnvironment env, Object key, SDMSObject o, boolean doLockBucket)
		throws SDMSException
	{
		SDMSIndexBucket v = null;
		synchronized(hashMap) {
			v = (SDMSIndexBucket) hashMap.get(key);
			if(v == null) {
				v = new SDMSIndexBucket(this, key);
				synchronized(v) { v.modCnt ++; }
				hashMap.put(key, v);
			} else
				synchronized(v) { v.modCnt ++; }
		}

		if (env.maxWriter > 1 && doLockBucket) {
			try {
				LockingSystem.lock(env, v, ObjectLock.EXCLUSIVE);
			} catch (Exception e) { synchronized(v) { v.modCnt --; } throw e; }
		}

		boolean newBucket = false;
		synchronized (v) {
			if (v.size() == 0) {
				v.add(o);
				v.modCnt --;
				newBucket = true;
			}
		}
		if (newBucket) {
			if (env.maxWriter > 1)
				LockingSystem.release(env, v);
			return;
		}

		if(doLockBucket && (type&UNIQUE) > 0) {

			SDMSThread t = null;
			if (env.maxWriter > 1 ) {
				t = (SDMSThread)Thread.currentThread();
				if (t.lockThread != null) t = t.lockThread;
			}

			SDMSObject old;
			long checkPoint = env.newLockCp();

			Object va[];
			synchronized(v) {
				va = v.toArray();
			}

			HashSet versionsChecked = new HashSet();
			for (int i = 0; i < va.length; ++i) {
				old = (SDMSObject) va[i];
				SDMSVersions ov = old.versions;

				if (env.maxWriter > 1) {
					LockingSystem.lock(env, ov, ((SDMSThread)Thread.currentThread()).readLock);
				}

				if (!old.isCurrent) {

					if (env.maxWriter > 1) {

						old = ov.getRaw(env, true);
						if (old != null) {

							synchronized(v) {
								if (! v.contains(old))
									old = null;
							}
						}
					}
				}

				if (old != null && old.isCurrent && o.isCurrent) {

					synchronized (v) { v.modCnt --; }
					throw new DuplicateKeyException(new SDMSMessage(env, "03110181528",
						"Duplicate Key $1: Second object exists", key));
				}

				if (env.maxWriter > 1)
					LockingSystem.releaseToCheckPoint(env, ov, checkPoint);

				if (old != null && o.validFrom < old.validTo && old.validFrom < o.validTo) {
					Object[] p = new Object[7];
					p[0] = key;
					p[1] = old.id;
					p[2] = new Long(old.validFrom);
					p[3] = new Long(old.validTo);
					p[4] = o.id;
					p[5] = new Long(o.validFrom);
					p[6] = new Long(o.validTo);
					v.modCnt --;
					throw new FatalException(new SDMSMessage(env, "03110181529",
						"Duplicate Key $1: Overlapping versionrange with same id: o[$2:$3,$4], old[$5:$6,$7]", p));
				}
			}
		}
		synchronized(v) {
			v.add(o);
			v.modCnt --;
		}

		if (env.maxWriter > 1 && doLockBucket)
			LockingSystem.release(env, v);
	}

	public boolean remove(SystemEnvironment env, Object key, SDMSObject o)
		throws SDMSException
	{
		boolean rc = false;
		synchronized(hashMap) {
			SDMSIndexBucket v = (SDMSIndexBucket) hashMap.get(key);
			if(v == null) return false;

			synchronized(v) {
				rc = v.remove(o);
				if(v.size() == 0 && v.modCnt == 0) {
					hashMap.remove(key);
				}
			}
		}

		return rc;
	}

	public boolean check(Object key, SDMSObject o)
	{
		SDMSIndexBucket v = (SDMSIndexBucket) hashMap.get(key);
		if(v == null) return false;

		boolean rc;
		synchronized(v) {
			return v.contains(o);
		}
	}

	private SDMSIndexBucket getOrCreateBucket(SystemEnvironment env, Object key, int lockMode)
		throws SerializationException
	{
		SDMSIndexBucket v;
		synchronized(hashMap) {
			v = (SDMSIndexBucket) hashMap.get(key);
			if (env.maxWriter > 1 && env.tx.mode == SDMSTransaction.READWRITE) {
				if(v == null) {
					v = new SDMSIndexBucket(this, key);
					hashMap.put(key, v);
				}
			}
		}
		if (v != null && env.maxWriter > 1 && env.tx.mode == SDMSTransaction.READWRITE)
			LockingSystem.lock(env, v, lockMode);
		return v;
	}

	public boolean containsKeyForUpdate(SystemEnvironment env, Object key)
		throws SerializationException, SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return containsKey(env, key);
	}

	public boolean containsKey(SystemEnvironment env, Object key)
		throws SerializationException, SDMSException
	{
		int readLock = env.thread.readLock;
		env.thread.readLock = ObjectLock.SHARED;

		SDMSIndexBucket v;

		v = getOrCreateBucket(env, key, readLock);
		if (v == null || v.size() == 0) return false;

		if(env.tx.mode == SDMSTransaction.READWRITE) {

			SDMSObject o;
			long checkPoint = env.newLockCp();
			Object va[];

			synchronized(v) {
				va = v.toArray();
			}

			HashSet versionsChecked = new HashSet();
			for (int i = 0; i < va.length; ++i) {
				o = (SDMSObject) va[i];
				SDMSVersions ov = o.versions;

				if (versionsChecked.contains(ov))
					continue;

				versionsChecked.add(ov);

				if (env.maxWriter > 1) {
					LockingSystem.lock(env, ov, readLock);
				}

				if (!o.isCurrent) {

					o = ov.getRaw(env, true);
					if (o != null) {

						synchronized(v) {
							if (! v.contains(o))
								o = null;
						}
					}
				}

				if (o == null)  {
					if (env.maxWriter > 1)
						LockingSystem.releaseToCheckPoint(env, ov, checkPoint);
					continue;
				}

				return true;
			}
			return false;
		} else {
			return containsKey(env, key, env.tx.versionId);
		}

	}

	public boolean containsKey(SystemEnvironment env, Object key, long version)
	{
		SDMSIndexBucket v;
		synchronized(hashMap) {
			v = (SDMSIndexBucket) hashMap.get(key);
		}
		if(v == null)
			return false;
		synchronized(v) {
			Iterator i = v.iterator();
			SDMSObject o;
			while (i.hasNext()) {
				o = (SDMSObject) i.next();
				if(o.validFrom <= version && version < o.validTo)
					return true;
			}
		}
		return false;
	}

	public Vector getVector(SystemEnvironment env, Object key)
		throws SDMSException
	{
		return getVector(env, key, null, 0);
	}
	public Vector getVectorForUpdate(SystemEnvironment env, Object key)
		throws SerializationException, SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return getVector(env, key, null, 0);
	}

	public Vector getVector(SystemEnvironment env, Object key, SDMSFilter filter)
		throws SDMSException
	{
		return getVector(env, key, filter, 0);
	}
	public Vector getVectorForUpdate(SystemEnvironment env, Object key, SDMSFilter filter)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return getVector(env, key, filter, 0);
	}

	public Vector getVector(SystemEnvironment env, Object key, int limit)
		throws SDMSException
	{
		return getVector(env, key, null, limit);
	}
	public Vector getVectorForUpdate(SystemEnvironment env, Object key, int limit)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return getVector(env, key, null, limit);
	}

	public Vector getVectorForUpdate(SystemEnvironment env, Object key, SDMSFilter filter, int limit)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return getVector(env, key, filter, limit);
	}
	public Vector getVector(SystemEnvironment env, Object key, SDMSFilter filter, int limit)
		throws SDMSException
	{
		int readLock = env.thread.readLock;
		env.thread.readLock = ObjectLock.SHARED;

		long checkPoint = env.newLockCp();
		Vector r = new Vector();
		int count = 0;

		if(env.tx.mode == SDMSTransaction.READWRITE) {

			Object va[];
			SDMSIndexBucket v;

			v = getOrCreateBucket(env, key, readLock);
			if (v == null || v.size() == 0) return r;
			synchronized(v) {
				va = v.toArray();
			}

			if (env.maxWriter > 1) {
				Arrays.sort(va, objectIdComparator);
			}
			ObjectLock versionsLock = null;
			SDMSProxy p = null;
			SDMSObject o;
			HashSet versionsChecked = new HashSet();
			for (int i = 0; i < va.length; ++i) {
				o = (SDMSObject) va[i];
				SDMSVersions ov = o.versions;

				if (versionsChecked.contains(ov))
					continue;

				versionsChecked.add(ov);

				if (env.maxWriter > 1) {
					LockingSystem.lock(env, ov, readLock);
				}

				if (!o.isCurrent) {

					o = ov.getRaw(env, true);
					if (o != null) {

						synchronized(v) {
							if (! v.contains(o))
								o = null;
						}
					}
				}

				if (o != null)  {
					if (p == null) {
						p = o.toProxy();
						p.current = true;
					} else
						p.object = o;

					if (filter == null || filter.isValid(env, p)) {
						r.add(p);
						p = null;
						count ++;
						if (limit > 0 && count >= limit)
							break;
					} else
						o = null;
				}

				if (o == null) {
					if (env.maxWriter > 1)
						LockingSystem.releaseToCheckPoint(env, ov, checkPoint);
				}
			}

			if (env.maxWriter > 1)
				LockingSystem.release(env, v);
			return r;
		} else {

			return getVector(env, key, env.tx.versionId, filter);
		}
	}

	public Vector getSortedVectorForUpdate(SystemEnvironment env, Object key)
		throws SDMSException
	{
		Vector v = getVectorForUpdate(env, key);
		Collections.sort(v);
		return v;
	}
	public Vector getSortedVector(SystemEnvironment env, Object key)
		throws SDMSException
	{
		Vector v = getVector(env, key);
		Collections.sort(v);
		return v;
	}

	public Vector getVector(SystemEnvironment env, Object key, long version)
		throws SDMSException
	{
		return getVector(env, key, version, null);
	}

	private Vector getVector(SystemEnvironment env, Object key, long version, SDMSFilter filter)
		throws SDMSException
	{
		Vector r = new Vector();

		Object va[];
		SDMSIndexBucket v;
		synchronized(hashMap) {
			v = (SDMSIndexBucket) hashMap.get(key);
		}
		if (v != null) {
			synchronized(v) {
				va = v.toArray();
			}
		} else {
			return r;
		}

		SDMSObject o;
		SDMSProxy p = null;
		for (int i = 0; i < va.length; ++i) {
			o = (SDMSObject) va[i];
			if(o.validFrom <= version && version < o.validTo) {
				if (p == null) {
					p = o.toProxy();
				} else
					p.object = o;
				if (filter == null || filter.isValid(env, p)) {
					r.add(p);
					p = null;
				}
			}
		}
		return r;
	}

	public Vector getSortedVector(SystemEnvironment env, Object key, long version)
		throws SDMSException
	{
		Vector v = getVector(env, key, version);
		Collections.sort(v);
		return v;
	}

	public SDMSProxy getUniqueForUpdate(SystemEnvironment env, Object key)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return getUnique(env, key);
	}
	public SDMSProxy getUnique(SystemEnvironment env, Object key)
		throws SDMSException
	{
		int readLock = env.thread.readLock;
		env.thread.readLock = ObjectLock.SHARED;

		long checkPoint = env.newLockCp();

		if((type&UNIQUE) == 0)
			throw new FatalException(new SDMSMessage(env, "03110181530",
				"Attempt to retrieve unique value from nonunique index"));

		if(env.tx.mode == SDMSTransaction.READWRITE) {

			SDMSIndexBucket v;

			v = getOrCreateBucket(env, key, readLock);
			if (v == null || v.size() == 0) throw new NotFoundException(new SDMSMessage(env, "03110181532", "$1 not found", key));

			SDMSProxy p;
			SDMSObject o;
			ObjectLock versionsLock = null;
			Object va[];
			synchronized(v) {
				va = v.toArray();
			}

			for (int i = 0; i < va.length; ++i) {
				o = (SDMSObject) va[i];
				SDMSVersions ov = o.versions;

				if (env.maxWriter > 1) {
					LockingSystem.lock(env, ov, readLock);
				}

				if (!o.isCurrent) {
					if (env.maxWriter > 1) {

						o = ov.getRaw(env, true);
						if (o != null) {

							synchronized(v) {
								if (! v.contains(o))
									o = null;
							}
						}
					} else
						o = null;
				}

				if (o == null)  {
					if (env.maxWriter > 1)
						LockingSystem.releaseToCheckPoint(env, ov, checkPoint);
					continue;
				}

				p = o.toProxy();
				p.current = true;
				return p;
			}
			throw new NotFoundException(new SDMSMessage(env, "03110181533", "$1 not found", key));
		}

		return getUnique(env, key, env.tx.versionId);
	}

	public SDMSProxy getUnique(SystemEnvironment env, Object key, long version)
		throws SDMSException
	{
		if((type&UNIQUE) == 0)
			throw new FatalException(new SDMSMessage(env, "03110181531",
				"Attempt to retrieve unique value from nonunique index"));

		SDMSIndexBucket v;
		synchronized(hashMap) {
			v = (SDMSIndexBucket) hashMap.get(key);
		}
		if (v == null) {
			throw new NotFoundException(new SDMSMessage(env, "03201292040", "$1 not found", key));
		}

		synchronized(v) {
			Iterator i = v.iterator();
			SDMSObject o;
			while (i.hasNext()) {
				o = (SDMSObject) i.next();
				if(o.validFrom <= version && version < o.validTo)
					return o.toProxy();
			}
		}
		throw new NotFoundException(new SDMSMessage(env, "03201292041", "$1 not found", key));
	}

	public void dumpIndex(SystemEnvironment env)
	{
		Set keys;
		synchronized(hashMap) {
			keys = hashMap.keySet();
		}
		String[] s;
		SDMSObject o;
		int k;
		Iterator i,j;
		String msg = "";
		synchronized(keys) {
			i = keys.iterator();
			s = new String[keys.size()];
			k = 0;
			while(i.hasNext()) {
				Object key = i.next();
				SDMSIndexBucket v;
				synchronized(hashMap) {
					v = (SDMSIndexBucket) hashMap.get(key);
				}
				s[k] = key.toString() + ": ";
				msg = msg + "$" + (k+1) + "\n";
				synchronized(v) {
					j = v.iterator();
					while(j.hasNext()) {
						o = (SDMSObject) j.next();
						s[k] = s[k] + o.id.toString() + "[" + o.validFrom + "," + o.validTo + "] ";
					}
				}
				k++;
			}
		}
		SDMSThread.doTrace(env.cEnv, "----------- Index Dump ------------", s, SDMSThread.SEVERITY_DEBUG);
	}
}

class ObjectIdComparator implements Comparator
{
	public int compare(Object o1, Object o2)
		throws ClassCastException
	{
		long id1 = ((SDMSObject)o1).id;
		long id2 = ((SDMSObject)o2).id;
		if (id1 < id2)
			return -1;
		else if (id1 > id2)
			return 1;
		else
			return 0;
	}
	public boolean equals(Object o1, Object o2)
		throws ClassCastException
	{
		long id1 = ((SDMSObject)o1).id;
		long id2 = ((SDMSObject)o2).id;
		if (id1 == id2)
			return true;
		else
			return false;
	}
}
