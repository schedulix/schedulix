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

public abstract class SDMSTable
{
	public static final String __version = "@(#) $Id: SDMSTable.java,v 2.15.2.1 2013/03/14 10:25:26 ronald Exp $";

	protected Map hashMap = null;

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

		hashMap = Collections.synchronizedMap(new HashMap());
		isVersioned = true;
	}

	public Iterator iterator(SystemEnvironment env)
		throws SDMSException
	{

		return iterator(env, null);
	}

	public Iterator iterator(SystemEnvironment env, SDMSFilter f)
		throws SDMSException
	{

		Vector r = new Vector();
		SDMSVersions v;
		SDMSProxy p = null;
		Object va[];
		synchronized(hashMap) {
		va = hashMap.values().toArray();
		}
		for (int i = 0; i < va.length; ++i) {
			v = (SDMSVersions) va[i];
			SDMSObject o = v.getRaw(env);
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
		return hashMap.size();
	}

	public void clearTable(SystemEnvironment env)
		throws SDMSException
	{

		SDMSVersions v;
		SDMSProxy p;
		Iterator i = hashMap.values().iterator();

		while(i.hasNext()) {
			v = (SDMSVersions) i.next();
			try {
				p = (v.get(env)).toProxy();
			} catch (NotFoundException nfe) {

				continue;
			}
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
		versions = (SDMSVersions)hashMap.get(o.id);
		if (versions == null) {
			versions = new SDMSVersions(this, o.id);
			hashMap.put(o.id, versions);
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

	protected abstract void index(SystemEnvironment env, SDMSObject o)
		throws SDMSException;

	protected abstract void unIndex(SystemEnvironment env, SDMSObject o)
		throws SDMSException;

	public synchronized SDMSProxy get (SystemEnvironment env, Long id)
		throws SDMSException
	{
		SDMSProxy p;
		SDMSVersions versions;

		versions = (SDMSVersions)hashMap.get (id);
		if(versions == null) {
			throw new NotFoundException (new SDMSMessage(env, "03110251037", "Key $1 not found (" + this.getClass().getName() + ")", id));
		}
		p = (versions.get(env)).toProxy();
		p.current = true;
		return p;

	}

	public synchronized SDMSProxy get (SystemEnvironment env, Long id, long version)
		throws SDMSException
	{
		SDMSVersions versions;
		versions = (SDMSVersions)(hashMap.get (id));
		if(versions == null) {
			throw new NotFoundException (new SDMSMessage(env, "03110251039", "Key $1 not found", id));
		}
		return (versions.get(env, version)).toProxy();
	}

	public synchronized boolean exists (SystemEnvironment env, Long id)
		throws SDMSException
	{
		SDMSVersions versions;

		versions = (SDMSVersions)hashMap.get (id);
		if(versions == null) {
			return false;
		}
		return true;

	}

	protected synchronized void put(SystemEnvironment env, Long id, SDMSVersions versions)
		throws SDMSException
	{
		hashMap.put(id, versions);
		try {
			index(env, (SDMSObject)(versions.o_v.getLast()));
		} catch(SDMSException e) {
			hashMap.remove(id);
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

	synchronized void release(SystemEnvironment env, SDMSVersions v)
		throws SDMSException
	{
		hashMap.remove(v.id);
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
