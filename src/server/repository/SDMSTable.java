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
import java.sql.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;

public abstract class SDMSTable
{
	public static final String __version = "@(#) $Id: SDMSTable.java,v 2.15.2.1 2013/03/14 10:25:26 ronald Exp $";

	private HashMap hashMap = null;
	protected boolean hashMapTrace = false;

	protected void hashMapRemove(Object id)
	{
		if (hashMapTrace)
			System.out.println (tableName() + ":hashMapRemove(" + id + ")");
		synchronized(hashMap) {
			hashMap.remove(id);
		}
	}

	protected boolean isVersioned;

	public final static String STAT_ID_COUNT      = "ID_COUNT";
	public final static String STAT_VERSION_COUNT = "VERSION_COUNT";
	public final static String STAT_LOW_VERSION   = "LOW_VERSION";
	public final static String STAT_MAX_VERSIONS  = "MAX_VERSIONS";

	protected SDMSTable(SystemEnvironment env)
		throws SDMSException
	{
		if(hashMap != null) {
			throw new FatalException (new SDMSMessage(env,
					"03110251129", "Tried to initialize table twice"));
		}
		hashMap = new HashMap();
		isVersioned = true;
	}

	public Iterator iterator (SystemEnvironment env)
		throws SDMSException
	{

		return iterator (env, null, false );
	}
	public Iterator iteratorForUpdate (SystemEnvironment env)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;

		return iterator (env, null, false );
	}

	public Iterator iterator (SystemEnvironment env, boolean unlocked)
		throws SDMSException
	{

		return iterator (env, null, unlocked);
	}
	public Iterator iteratorForUpdate (SystemEnvironment env, boolean unlocked)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;

		return iterator (env, null, unlocked);
	}

	public Iterator iterator (SystemEnvironment env, SDMSFilter f)
		throws SDMSException
	{

		return iterator (env, f, false);
	}
	public Iterator iteratorForUpdate (SystemEnvironment env, SDMSFilter f)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;

		return iterator (env, f, false);
	}
	public Iterator iteratorForUpdate (SystemEnvironment env, SDMSFilter f, boolean unlocked)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return iterator(env, f, unlocked);
	}
	public Iterator iterator (SystemEnvironment env, SDMSFilter f, boolean unlocked)
		throws SDMSException
	{
		int readLock = env.thread.readLock;
		env.thread.readLock = ObjectLock.SHARED;

		Vector r = new Vector();
		SDMSVersions v;
		SDMSProxy p = null;
		Object va[];
		synchronized(hashMap) {
			va = hashMap.values().toArray();
		}
		for (int i = 0; i < va.length; ++i) {
			v = (SDMSVersions) va[i];
			if (env.tx.mode == SDMSTransaction.READWRITE && env.maxWriter > 1 && !unlocked)
				LockingSystem.lock(env, v, readLock);
			SDMSObject o = v.getRaw(env, unlocked);
			if (o == null) continue;
			if (p == null) {
				p = o.toProxy();
				p.current = true;
			} else
				p.object = o;
			if (f == null || f.isValid(env, p)) {
				r.add(p);
				p = null;
			}
		}
		return r.iterator();
	}

	public int rawSize()
	{
		synchronized(hashMap) {
			return hashMap.size();
		}
	}

	public void clearTableUnlocked(SystemEnvironment env)
		throws SDMSException
	{
		SDMSVersions v;
		Iterator i = iterator(env, true );

		while(i.hasNext()) {
			SDMSProxy p = (SDMSProxy) i.next();
			p.delete(env);
		}
	}

	protected abstract void loadTable(SystemEnvironment env)
		throws SQLException, SDMSException;

	protected abstract SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
		throws SDMSException;

	protected boolean loadObject(SystemEnvironment env, ResultSet r)
		throws SDMSException
	{
		SDMSVersions versions;
		SDMSObject   o;

		o = rowToObject (env, r);
		if(o == null) return false;
		synchronized(hashMap) {
			versions = (SDMSVersions)hashMap.get(o.id);
		}
		if (versions == null) {
			versions = new SDMSVersions(this, o.id);
			synchronized(hashMap) {
				hashMap.put(o.id, versions);
			}
		}
		o.versions = versions;
		versions.add(env, o);
		try {
			index(env, o);
		} catch (DuplicateKeyException e) {
			throw new FatalException (new SDMSMessage(env,
					"03110181537", "Duplicate id during load Object"));
		}
		if (getIsVersioned()) {
			long s = versions.versions.size();
			if( s > 1 ||
			   (s == 1 && ((SDMSObject)(versions.versions.lastElement())).validTo != Long.MAX_VALUE)
			  ) {
				env.vPurgeSet.add(env, versions);
			}
		}
		return true;
	}

	protected void remove(SystemEnvironment env, Long id)
		throws SDMSException
	{
		throw new FatalException(new SDMSMessage(env,
				"02110271229", "cannot remove id from an SDMSTables hashMap"));

	}

	public abstract String tableName();

	public abstract String[] columnNames();

	public abstract String checkIndex(SDMSObject o)
		throws SDMSException;
	protected abstract void index(SystemEnvironment env, SDMSObject o)
		throws SDMSException;
	protected abstract void index(SystemEnvironment env, SDMSObject o, long indexMember)
		throws SDMSException;

	protected abstract void unIndex(SystemEnvironment env, SDMSObject o)
		throws SDMSException;

	public SDMSProxy get (SystemEnvironment env, Long id)
		throws SDMSException
	{
		int readLock = env.thread.readLock;
		env.thread.readLock = ObjectLock.SHARED;

		SDMSProxy p;
		SDMSVersions versions;

		synchronized (hashMap) {
			versions = (SDMSVersions)hashMap.get (id);
		}
		if(versions == null) {
			throw new NotFoundException (new SDMSMessage(env, "03110251037", "Key $1 not found (" + this.getClass().getName() + ")", id));
		}
		if (env.tx.mode == SDMSTransaction.READWRITE && env.maxWriter > 1)
			LockingSystem.lock(env, versions, readLock);
		p = (versions.get(env)).toProxy();
		p.current = true;
		return p;

	}
	public SDMSProxy getForUpdate (SystemEnvironment env, Long id)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return get(env, id);
	}

	public SDMSVersions getVersions(Long id)
	{
		synchronized (hashMap) {
			return (SDMSVersions)hashMap.get (id);
		}
	}

	public boolean contains(Long id)
	{
		synchronized (hashMap) {
			return hashMap.containsKey (id);
		}
	}

	public SDMSProxy get (SystemEnvironment env, Long id, long version)
		throws SDMSException
	{
		SDMSVersions versions;
		synchronized (hashMap) {
			versions = (SDMSVersions)(hashMap.get (id));
		}
		if(versions == null) {
			throw new NotFoundException (new SDMSMessage(env, "03110251039", "Key $1 not found", id));
		}
		return (versions.get(env, version)).toProxy();
	}

	public boolean exists (SystemEnvironment env, Long id)
		throws SDMSException
	{
		int readLock = env.thread.readLock;
		env.thread.readLock = ObjectLock.SHARED;

		SDMSVersions versions;

		synchronized (hashMap) {
			versions = (SDMSVersions)hashMap.get (id);
		}
		if(versions == null) {
			return false;
		}
		if (env.tx.mode == SDMSTransaction.READWRITE && env.maxWriter > 1)
			LockingSystem.lock(env, versions, readLock);
		if (versions.getRaw(env, false) == null) return false;
		return true;

	}
	public boolean existsForUpdate (SystemEnvironment env, Long id)
		throws SDMSException
	{
		env.thread.readLock = ObjectLock.EXCLUSIVE;
		return exists(env, id);
	}

	protected void put(SystemEnvironment env, Long id, SDMSVersions versions)
		throws SDMSException
	{
		synchronized (hashMap) {
			if (hashMapTrace)
				System.out.println (tableName() + ":hashMap.put(" + id + ")");
			hashMap.put(id, versions);
		}
		try {
			index(env, (SDMSObject)(versions.o_v.getLast()));
		} catch(SDMSException e) {
			synchronized (hashMap) {
				hashMapRemove(id);
			}
			throw e;
		}
	}

	public void dump(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSVersions v;

		synchronized(hashMap) {
			Iterator i = hashMap.values().iterator();
			while(i.hasNext()) {
				v = (SDMSVersions) i.next();
				v.dump();
			}
		}
	}

	public boolean getIsVersioned()
	{
		return isVersioned;
	}

	public HashMap stat(SystemEnvironment sysEnv)
		throws SDMSException
	{
		HashMap result = new HashMap();

		long lowVersion = Long.MAX_VALUE;
		long countIds = 0;
		long maxVersions = 0;
		long countVersions = 0;

		synchronized(hashMap) {
			Iterator i = hashMap.values().iterator();
			while(i.hasNext()) {
				countIds++;
				SDMSVersions v = (SDMSVersions) i.next();
				HashMap vStat = v.stat(sysEnv);
				long vVersionsCount = ((Long)(vStat.get(SDMSVersions.STAT_VERSION_COUNT))).longValue();
				countVersions += vVersionsCount;
				if (vVersionsCount > maxVersions) maxVersions = vVersionsCount;
				long vLowVersion = ((Long)(vStat.get(SDMSVersions.STAT_LOW_VERSION))).longValue();
				if (vLowVersion < lowVersion) lowVersion = vLowVersion;
			}
		}
		result.put(STAT_ID_COUNT, new Long(countIds));
		result.put(STAT_VERSION_COUNT, new Long(countVersions));
		result.put(STAT_LOW_VERSION, new Long(lowVersion));
		result.put(STAT_MAX_VERSIONS, new Long(maxVersions));

		return result;
	}
}
