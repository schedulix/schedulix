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
import de.independit.scheduler.locking.*;

public abstract class SDMSObject implements Cloneable, Comparable
{
	public static final String __version = "@(#) $Id: SDMSObject.java,v 2.9.2.1 2013/03/14 10:25:20 ronald Exp $";

	protected        long         validFrom;

	protected        long         validTo;

	public        SDMSVersions versions;

	protected        Long         id;

	protected	 int          subTxId;

	protected        boolean      isDeleted = false;

	protected        boolean      memOnly = false;

	protected        boolean      isCurrent = false;

	private static ObjectCounter nextId = null;

	public final static Long adminGId  = new Long(81);
	public final static Long publicGId = new Long(80);

	public final static Long dummyShId  = new Long(30);

	public final static Long systemFId = new Long(40);

	protected SDMSObject()
	{
	}

	protected SDMSObject(SystemEnvironment env, SDMSTable table)
		throws SDMSException
	{
		if (nextId == null) {
			nextId = new ObjectCounter(env);
		}
		id = new Long(nextId.next(env));
		validFrom = -1;
		validTo = -1;
		versions = new SDMSVersions(table, id);
		if (env.maxWriter > 1)
			LockingSystem.lock(versions, ObjectLock.EXCLUSIVE);
		versions.tx = env.tx;
		subTxId = env.tx.subTxId;
		versions.o_v = new LinkedList();
		versions.o_v.add(this);
		versions.table = table;
		isCurrent = true;
	}

	protected SDMSObject change(SystemEnvironment env)
		throws SDMSException
	{
		if (versions.tx != null) {
			if (env.tx != versions.tx) {
				throw new FatalException (new SDMSMessage(env,
					"03110181611", "Trying to change object locked by other transaction"));
			}
			if (versions.o_v == null) {
				throw new FatalException (new SDMSMessage(env,
					"03110181612", "Already touched versions without vector o_v"));
			}

			if (versions.o_v.size() != 0) {

				if (subTxId == env.tx.subTxId) {
					throw new FatalException (new SDMSMessage(env,
						"03111021044", "No two changes within the same subtx permitted"));
				}
			}
		} else {

			versions.o_v = new LinkedList();
			versions.tx = env.tx;
			env.tx.addToChangeSet(env, versions, false);
		}

		SDMSObject o;
		try {

			o = (SDMSObject)clone();
			o.subTxId = env.tx.subTxId;

			isCurrent = false;
			o.isCurrent = true;
		} catch (CloneNotSupportedException exception) {
			throw new FatalException (new SDMSMessage(env,
				"03110181614", "Trying to clone uncloneable Object"));
		}
		o.validFrom = -1;
		o.validTo = -1;
		versions.o_v.add(o);
		env.tx.addToTouchSet(env, versions, false);
		return o;
	}

	public void delete(SystemEnvironment env)
		throws SDMSException
	{
		deleteObject(env, false);
	}

	public void memDelete(SystemEnvironment env)
		throws SDMSException
	{
		deleteObject(env, true);
	}

	private void deleteObject(SystemEnvironment env, boolean memOnly)
		throws SDMSException
	{
		SDMSObject o;

		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
				new SDMSMessage (env, "03201292032", "Change of system object not allowed")
			);
		}
		if (memOnly && versions.table.getIsVersioned()) {
			throw new FatalException (new SDMSMessage(env,
				"03204260808", "Trying to do a memOnly delete on a versioned table"));
		}

		if (versions.tx != null) {
			if (env.tx != versions.tx) {
				throw new FatalException (new SDMSMessage(env,
					"03110181615", "Trying to delete object locked by other transaction"));
			}
			if (versions.o_v == null) {
				throw new FatalException (new SDMSMessage(env,
					"03110261614", "Already touched versions without vector o_v"));
			}
			if (versions.o_v.size() != 0) {

				if (subTxId == env.tx.subTxId) {

					versions.table.unIndex(env, this);
					isDeleted = true;
					isCurrent = false;
					return;
				}
			}
		} else {

			versions.tx = env.tx;
			versions.o_v = new LinkedList();
			env.tx.addToChangeSet(env, versions, false);
		}
		try {

			o = (SDMSObject)clone();
			o.subTxId = env.tx.subTxId;
			o.isDeleted = true;
			o.memOnly = memOnly;

			isCurrent = false;

			o.isCurrent = false;
		} catch (CloneNotSupportedException exception) {
			throw new FatalException (new SDMSMessage(env,
				"02110261714", "Trying to clone uncloneable Object"));
		}
		versions.o_v.add(o);
		env.tx.addToTouchSet(env, versions, false);
	}

	protected abstract SDMSProxy toProxy();

	protected abstract void insertDBObject(SystemEnvironment env)
		throws SDMSException;

	protected abstract void updateDBObject(SystemEnvironment env, SDMSObject old)
		throws SDMSException;

	protected abstract void deleteDBObject(SystemEnvironment env)
		throws SDMSException;

	public long getValidFrom(SystemEnvironment env)
		throws SDMSException
	{
		return validFrom;
	}

	public long getValidTo(SystemEnvironment env)
		throws SDMSException
	{
		return validTo;
	}

	public Long getId(SystemEnvironment env)
		throws SDMSException
	{
		return id;
	}

	public abstract void print();
	public abstract String toString(int indent);

	public String toShortString()
	{
		return "SDMSObject(" + versions.table.tableName() + ", " + id + ")";
	}

	public void dumpVersions()
	{
		dumpVersions(SDMSThread.SEVERITY_DEBUG);
	}

	public void dumpVersions(int severity)
	{
		SDMSThread.doTrace(null, "-- Object is --", severity);
		SDMSThread.doTrace(null, "id : " + id.toString(), severity);
		SDMSThread.doTrace(null, "subTxId : " + subTxId, severity);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, severity);
		SDMSThread.doTrace(null, "validTo : " + validTo, severity);
		SDMSThread.doTrace(null, "subTxId : " + subTxId, severity);
		SDMSThread.doTrace(null, "isDeleted : " + isDeleted, severity);
		SDMSThread.doTrace(null, "memOnly : " + memOnly, severity);
		SDMSThread.doTrace(null, "isCurrent : " + isCurrent, severity);
		versions.dump(severity);
	}

	public int compareTo(Object o)
	{
		SDMSObject p = (SDMSObject) o;
		if (id.longValue() > p.id.longValue()) return 1;
		if (id.longValue() < p.id.longValue()) return -1;
		if (validFrom > p.validFrom) return 1;
		if (validFrom < p.validFrom) return -1;
		return 0;
	}

}

class ObjectCounter
{

	private static final long QUANTUM = 1000L;
	private static long nextId = 0;
	private static long lastId;

	public ObjectCounter(SystemEnvironment env)
		throws SDMSException
	{
		lastId = getNextQuantum(env);
		nextId = lastId - QUANTUM + 1;
	}

	public synchronized long next(SystemEnvironment env)
		throws SDMSException
	{
		if(nextId == lastId)
			lastId = getNextQuantum(env);
		return ++nextId;
	}

	private synchronized long getNextQuantum(SystemEnvironment env)
		throws SDMSException
	{
		long l;

		try {
			Statement stmt = env.dbConnection.createStatement();
			stmt.executeUpdate("UPDATE OBJECTCOUNTER SET LASTID = LASTID + " + QUANTUM);

			ResultSet rset = stmt.executeQuery("SELECT LASTID FROM OBJECTCOUNTER");
			if(rset.next()) {
				l = rset.getLong(1);
			} else {
				throw new FatalException(new SDMSMessage(env,
						"03110181618", "Counter Value Missing"));
			}
			if(rset.next()) {
				throw new FatalException(new SDMSMessage(env,
						"03110181619", "Duplicate Counter Value"));
			}
			rset.close();

			stmt.close();

			env.dbConnection.commit();

		} catch (SQLException sqle) {
			throw new FatalException(new SDMSMessage(env, "03110181620",
								"Error on updating the ObjectCounter:\n$1",
								"SQLError : " + sqle.getMessage()));
		}

		return l;
	}
}

