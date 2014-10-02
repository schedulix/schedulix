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

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSTransaction
{

	public final static String __version = "@(#) $Id: SDMSTransaction.java,v 2.12.2.1 2013/03/14 10:25:26 ronald Exp $";

	public static final int READONLY  = 0x01;
	public static final int READWRITE = 0x02;

	protected static final int UNDEFINED = -1;

	private static TxCounter nextId = null;

	public int subTxId = 0;

	public HashMap txData = new HashMap();
	public Integer smeCtr = new Integer(0);
	private Stack ctrStack = new Stack();
	private Stack clStack = new Stack();
	public HashMap privCache = new HashMap();

	public    long    txId;
	public    int     mode;
	public    long    versionId;
	public final long startTime;
	public long endTime;
	private   ChangeList changeList;
	private   HashSet touchList;

	public SDMSTransaction(SystemEnvironment env, int m, Long version)
		throws SDMSException
	{
		if (nextId == null) {
			nextId = new TxCounter(env);
		}
		mode = m;
		txId = nextId.next(env, m);
		changeList = null;
		touchList = null;
		if(m == READONLY) {
			versionId = (version == null ? txId : version.longValue());
			env.roTxList.add(env, versionId);
		} else {
			versionId = UNDEFINED;
		}
		startTime = System.currentTimeMillis();
	}

	public static long drawVersion(SystemEnvironment env) throws SDMSException
	{
		return nextId.next(env, READWRITE);
	}
	public static long getRoVersion(SystemEnvironment env) throws SDMSException
	{
		return nextId.next(env, READONLY);
	}

	public long txId()
	{
		return txId;
	}
	public int  mode()
	{
		return mode;
	}
	public long versionId(SystemEnvironment env)	throws SDMSException
	{
		if (versionId == UNDEFINED) {
			throw new FatalException(new SDMSMessage(env,
				"03110181540", "VersionId not defined"));
		}
		return versionId;
	}

	public void commit(SystemEnvironment env)
		throws SQLException, SDMSException
	{
		if(versionId == UNDEFINED) versionId = nextId.next(env, READWRITE);
		commitOrRollback(env, true);
	}

	public void rollback(SystemEnvironment env)
		throws SQLException, SDMSException
	{
		commitOrRollback(env, false);
	}

	public void setContextVersionId(SystemEnvironment env, Long version)
		throws SDMSException
	{

		if (versionId == UNDEFINED || mode == READWRITE) {
			throw new FatalException(new SDMSMessage(env,
				"03212191505", "VersionId cannot be set within a writing transaction"));
		} else {

			synchronized(env.roTxList) {
				env.roTxList.remove(env, versionId);
				env.roTxList.add(env, version);
				versionId = version;
			}
		}
	}

	public void commitOrRollback(SystemEnvironment env, boolean isCommit)
		throws SQLException, SDMSException
	{
		if (isCommit) {

			if(subTxId != 0) {
				throw new FatalException (new SDMSMessage (env, "02110301918",
					"Unclosed subtransaction in transaction commit or rollback"));
			} else {
				if(env.inExecution) {
					SDMSThread.doTrace(null, "\n" +
					"*********************************************************************\n" +
					"*********************************************************************\n" +
					"***                                                               ***\n" +
					"*** W A T C H   O U T   ! ! ! ! !                                 ***\n" +
					"***                                                               ***\n" +
					"*** We are committing within a go() method !                      ***\n" +
					"*** This might compromise our database                            ***\n" +
					"***                                                               ***\n" +
					"*********************************************************************\n" +
					"*********************************************************************\n" ,
					SDMSThread.SEVERITY_ERROR
					);
				}
			}
		} else {

			while(subTxId > 0) {
				rollbackSubTransaction(env);
			}
		}

		if (mode == READONLY || changeList == null) {

			env.roTxList.remove(env, versionId);
			if(mode != READONLY) nextId.releaseVersion(env);
			endTime = System.currentTimeMillis();
			return;
		}

		if(isCommit && smeCtr.intValue() > 0)
			throw new FatalException(new SDMSMessage(env, "03406061057",
					"Error in SME Counter, tried to submit $1 unregistered entities", smeCtr));

		int i;
		int maxElms = changeList.size();
		SDMSChangeListElement ce;

		if (isCommit) {
			SystemEnvironment.ticketThread.renewTicket(env);

			for(i = 0; i < maxElms; i++) {
				ce = changeList.get(i);
				ce.versions.flush(env, ce.isNew);
			}
		} else {
			env.dbConnection.rollback();
		}
		for(i = 0; i < maxElms; i++) {

			ce = changeList.get(i);

			ce.versions.commitOrRollback(env, versionId, ce.isNew, isCommit);

			ce.versions.unLock(env);
		}
		changeList = null;
		if (isCommit) {
			env.dbConnection.commit();
		}
		nextId.releaseVersion(env);
		endTime = System.currentTimeMillis();
	}

	public void beginSubTransaction(SystemEnvironment env)
	{
		subTxId ++;
		ctrStack.push(smeCtr);
		clStack.push(touchList);
		touchList = null;
	}

	public void commitSubTransaction(SystemEnvironment env)
		throws SDMSException
	{
		commitOrRollbackSubTransaction(env, true);
		ctrStack.pop();
		HashSet oldList = touchList;
		touchList = (HashSet) clStack.pop();

		if (oldList != null) {
			if (touchList == null) touchList = new HashSet();
			touchList.addAll(oldList);
		}
	}

	public void rollbackSubTransaction(SystemEnvironment env)
		throws SDMSException
	{
		commitOrRollbackSubTransaction(env, false);
		smeCtr = (Integer) ctrStack.pop();
		touchList = (HashSet) clStack.pop();
	}

	private void commitOrRollbackSubTransaction(SystemEnvironment env, boolean isCommit)
		throws SDMSException
	{

		SDMSChangeListElement ce;

		subTxId --;
		if (subTxId < 0) {
			throw new FatalException(new SDMSMessage (env,
						"02110261755", "sub transaction underflow"));
		}
		if (mode == READONLY || touchList == null) {
			return;
		}
		int s;
		Iterator i = touchList.iterator();
		while(i.hasNext()) {
			ce = (SDMSChangeListElement) i.next();

			s = ce.versions.o_v.size();
			if (s == 0) {

				continue;
			}
			SDMSObject o = (SDMSObject)(ce.versions.o_v.getLast());

			if (o.subTxId != subTxId + 1) {
				continue;
			}
			if (isCommit) {

				o.subTxId = subTxId;
				if (s > 1 && o.subTxId == subTxId) {

					o = (SDMSObject)(ce.versions.o_v.get(s - 2));
					if (o.subTxId == subTxId) {
						ce.versions.o_v.remove(s - 2);
						o.versions.table.unIndex(env, o);
					}
				}
			} else {

				o.versions.table.unIndex(env, o);

				o.isCurrent = false;

				ce.versions.o_v.remove(s - 1);

				if (s > 1) {

					o = (SDMSObject)(ce.versions.o_v.getLast());
					o.isCurrent = true;
				} else {
					if(! ce.isNew) {

						o = (SDMSObject)(ce.versions.versions.lastElement());
						if (o != null && o.validTo == Long.MAX_VALUE) {
							o.isCurrent = true;
						}
					}
				}
			}

		}
	}

	protected void addToTouchSet(SystemEnvironment env, SDMSVersions versions, boolean isNew)
		throws SDMSException
	{
		SDMSChangeListElement changeListElement = new SDMSChangeListElement(env, versions, isNew);
		if (touchList == null) {
			touchList = new HashSet();
		}
		touchList.add(changeListElement);
	}

	protected void addToChangeSet(SystemEnvironment env, SDMSVersions versions, boolean isNew)
		throws SDMSException
	{
		SDMSChangeListElement changeListElement = new SDMSChangeListElement(env, versions, isNew);
		if (changeList == null) {
			changeList = new ChangeList();
		}
		changeList.addElement(changeListElement);
	}

	public String toString()
	{
		String rc = new String (
			"-- Start Transaction Data --\n" +
			"  subTxId   : " + subTxId + "\n" +
			"  txId      : " + txId + "\n" +
			"  mode      : " + (mode == READONLY ? "READONLY" : "READWRITE") + "\n" +
			"  versionId : " + (versionId == UNDEFINED ? "UNDEFINED" : "" + versionId) + "\n" +
			"  Changes   : " + changeList.size() + "\n" +
			"-- End Transaction Data --\n"
		);
		return rc;
	}
}

class TxCounter
{

	private static final int QUANTUM = 1000;
	private static long nextId = 0;
	private static long lastId;
	private static long lastRoId;

	public TxCounter(SystemEnvironment env)
		throws SDMSException
	{
		lastId = getNextQuantum(env);
		nextId = lastId - QUANTUM + 1;
		lastRoId = nextId;
	}

	private synchronized long next(SystemEnvironment env)
		throws SDMSException
	{
		if(nextId == lastId) {
			lastId = getNextQuantum(env);
			nextId = lastId - QUANTUM;
		}
		return ++nextId;
	}

	public synchronized long next(SystemEnvironment env, int m)
		throws SDMSException
	{
		if(m == SDMSTransaction.READONLY)	return lastRoId;
		else					return next(env);
	}

	public synchronized void releaseVersion(SystemEnvironment env)
		throws SDMSException
	{
		lastRoId = next(env);
	}

	private synchronized long getNextQuantum(SystemEnvironment env)
		throws SDMSException
	{
		long v;

		try {
			Statement stmt = env.dbConnection.createStatement();

			ResultSet rset = stmt.executeQuery("SELECT LASTID" +
							   " FROM VERSIONCOUNTER");
			if(rset.next()) {
				v = rset.getLong(1);
			} else {
				throw new FatalException(new SDMSMessage(env,
						"03110181541", "Counter Value Missing"));
			}
			if(rset.next()) {
				throw new FatalException(new SDMSMessage(env,
						"03110181542", "Duplicate Counter Value"));
			}
			rset.close();

			v = v + QUANTUM;

			stmt.executeUpdate("UPDATE VERSIONCOUNTER " +
					   "SET LASTID = LASTID + " + QUANTUM);

			stmt.close();
			env.dbConnection.commit();
		} catch (SQLException sqle) {
			throw new FatalException(new SDMSMessage(env, "03110181543",
						"SQLerror on updating the VersionCounter:\n$1",
						"SQL Error : " + sqle.getMessage()));
		}

		return v;
	}
}

class ChangeList
{

	final static public int INITIAL_CAPACITY = 25;
	private SDMSChangeListElement elm[];
	private int size = 0;

	public ChangeList()
	{
		elm = new SDMSChangeListElement[INITIAL_CAPACITY+1];
	}

	public int size()
	{
		return size;
	}

	public SDMSChangeListElement get(int pos)
	{
		if(pos >= size || pos < 0)
			throw new ArrayIndexOutOfBoundsException();
		return elm[pos];
	}

	public void addElement(SDMSChangeListElement e)
	{
		elm[size] = e;
		size++;
		ensureCapacity(size);
	}

	private void ensureCapacity(int s)
	{
		int old = elm.length;
		if(s >= old) {
			SDMSChangeListElement oldElm[] = elm;
			elm = new SDMSChangeListElement[s*2];
			System.arraycopy(oldElm, 0, elm, 0, size);
		}
	}
}

