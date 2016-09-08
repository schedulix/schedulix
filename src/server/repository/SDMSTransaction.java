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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.util.*;

public class SDMSTransaction
{
	public final static String __version = "@(#) $Id: SDMSTransaction.java,v 2.12.2.1 2013/03/14 10:25:26 ronald Exp $";

	SDMSThread thread = null;

	public static final int READONLY  = 0x01;
	public static final int READWRITE = 0x02;

	protected static final int UNDEFINED = -1;

	private static TxCounter nextId = null;

	private static final Object commitLock = new Object();

	public int subTxId = 0;
	Stack subTxCheckPoints;
	public long subTxCheckPoint = 0;

	public HashMap txData = new HashMap();
	public Integer smeCtr = new Integer(0);
	private Stack ctrStack = new Stack();
	private Stack clStack = new Stack();
	public Stack lockStack = new Stack();
	public HashMap privCache = new HashMap();
	public Vector resourceRequestList = null;

	public    long    txId;
	public    int     mode;
	public    long    versionId;
	public final long startTime;
	public long endTime = 0;

	private   HashSet touchList;
	public    HashSet subTxLocks;
	public long[] commitingTx;

	public HashMap rscCache = null;
	public HashMap envJSMap = null;

	public boolean traceSubTx = false;

	public SDMSTransaction(SystemEnvironment env, int m, Long version)
		throws SDMSException
	{
		if (nextId == null) {
			synchronized(commitLock) {
				if (nextId == null)
					nextId = new TxCounter(env);
			}
		}
		mode = m;
		txId = nextId.next(env, m, false);

		touchList = null;
		subTxLocks = null;
		if(m == READONLY) {
			versionId = (version == null ? txId : version.longValue());
			env.roTxList.add(env, versionId);
			if (SystemEnvironment.maxWriter > 1)
				commitingTx = nextId.getCommitingTx();
		} else
			versionId = UNDEFINED;
		startTime = System.currentTimeMillis();
		subTxCheckPoints = new Stack();
		thread = env.thread;
	}

	public static long drawVersion(SystemEnvironment env) throws SDMSException
	{
		return nextId.next(env, READWRITE, false);
	}

	public static long getRoVersion(SystemEnvironment env) throws SDMSException
	{
		return nextId.next(env, READONLY, false);
	}

	public long txId()	{ return txId; }
	public int  mode()	{ return mode; }
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
		if(versionId == UNDEFINED) versionId = nextId.next(env, READWRITE, true);
		try {
			commitOrRollback(env, true);
		} catch(Throwable t) {
			nextId.releaseVersion(env);
			throw t;
		}
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
				env.roTxList.add(env, version);
				env.roTxList.remove(env, versionId);
				versionId = version;
			}
		}
	}

	public void commitOrRollback(SystemEnvironment env, boolean isCommit)
		throws SQLException, SDMSException
	{
		if (traceSubTx)
			SDMSThread.doTrace(null, "Commiting or rolling back Transaction", SDMSThread.SEVERITY_ERROR);
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

		if (mode == READONLY || touchList == null) {
			if(mode != READONLY)
				nextId.releaseVersion(env);
			else {

				env.roTxList.remove(env, versionId);
			}
			endTime = System.currentTimeMillis();
			if (env.maxWriter > 1 && mode == READWRITE)
				LockingSystem.release(env);
			return;
		}

		if(isCommit && smeCtr.intValue() > 0)
			throw new FatalException(new SDMSMessage(env, "03406061057",
					"Error in SME Counter, tried to submit $1 unregistered entities", smeCtr));

		Iterator i;
		SDMSChangeListElement ce;

		int lockmode = ObjectLock.SHARED;
		if (isCommit) {
			boolean again = true;
			while (again) {
				again = false;
				if (env.maxWriter > 1)
					LockingSystem.lock(env, commitLock, lockmode);
				try {
					i = touchList.iterator();

					while(i.hasNext()) {
						ce = (SDMSChangeListElement) i.next();

						ce.versions.flush(env, ce.isNew);
					}
				} catch (SDMSSQLException sqle) {
					if (lockmode == ObjectLock.EXCLUSIVE) {

						throw sqle;
					}
					again = true;
					lockmode = ObjectLock.EXCLUSIVE;

					env.dbConnection.rollback();

					if (env.maxWriter > 1)
						LockingSystem.release(env, commitLock);

					continue;
				}

				SystemEnvironment.ticketThread.renewTicket(env);

				env.dbConnection.commit();

				if (env.maxWriter > 1)
					LockingSystem.release(env, commitLock);
			}
		} else {
			env.dbConnection.rollback();
		}

		i = touchList.iterator();
		while(i.hasNext()) {

			ce = (SDMSChangeListElement) i.next();

			ce.versions.commitOrRollback(env, versionId, ce.isNew, isCommit);

		}

		if (env.maxWriter > 1)
			LockingSystem.release(env);
		nextId.releaseVersion(env);
		endTime = System.currentTimeMillis();
	}

	public void beginSubTransaction(SystemEnvironment env)
	{
		subTxId ++;
		ctrStack.push(smeCtr);
		clStack.push(touchList);
		lockStack.push(subTxLocks);
		subTxLocks = new HashSet();
		touchList = new HashSet();
		if (traceSubTx)
			SDMSThread.doTrace(null, "Starting subtransaction", SDMSThread.SEVERITY_ERROR);
		subTxCheckPoints.push(new Long(subTxCheckPoint));
		subTxCheckPoint = env.newLockCp();
	}

	public void commitSubTransaction(SystemEnvironment env)
		throws SDMSException
	{
		commitOrRollbackSubTransaction(env, true);
		subTxCheckPoint = ((Long)subTxCheckPoints.pop()).longValue();
		ctrStack.pop();

		HashSet oldSubTxLocks = subTxLocks;
		subTxLocks = (HashSet) lockStack.pop();
		if (subTxLocks != null)
			subTxLocks.addAll(oldSubTxLocks);

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
		subTxCheckPoint = ((Long)subTxCheckPoints.pop()).longValue();
		subTxLocks = (HashSet) lockStack.pop();
		smeCtr = (Integer) ctrStack.pop();
		touchList = (HashSet) clStack.pop();
	}

	private void commitOrRollbackSubTransaction(SystemEnvironment env, boolean isCommit)
		throws SDMSException
	{

		SDMSChangeListElement ce;

		if (traceSubTx)
			SDMSThread.doTrace(null, "Terminating subtransaction" + (isCommit ? "(commit)" : "(rollback)"), SDMSThread.SEVERITY_ERROR);
		subTxId --;
		if (subTxId < 0) {
			throw new FatalException(new SDMSMessage (env,
						"02110261755", "sub transaction underflow"));
		}
		if (mode == READONLY || touchList == null) {
			if (mode == READWRITE && !isCommit && env.maxWriter > 1) {
				LockingSystem.releaseSubTxLocks(env, subTxCheckPoint);
			}
			return;
		}

		int s;
		Iterator i = touchList.iterator();
		while(i.hasNext()) {
			ce = (SDMSChangeListElement) i.next();

			if (ce.versions.o_v == null)
				continue;

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
				if (s > 1) {

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
					o.versions.tx = null;
				}
			}

		}

		if (!isCommit && env.maxWriter > 1) {
			LockingSystem.releaseSubTxLocks(env, subTxCheckPoint);
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

	public String toString()
	{
		String rc = new String (
			"-- Start Transaction Data --\n" +
			"  subTxId   : " + subTxId + "\n" +
			"  txId      : " + txId + "\n" +
			"  mode      : " + (mode == READONLY ? "READONLY" : "READWRITE") + "\n" +
			"  versionId : " + (versionId == UNDEFINED ? "UNDEFINED" : "" + versionId) + "\n" +
			"  Changes   : " + (touchList == null ? "0" : touchList.size()) + "\n" +
			"  StartTime : " + startTime + "\n" +
			"  EndTime   : " + endTime + "\n" +
			"  Thread    : " + thread.getName() + "\n" +
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

	private long commitingTx[];
	private int commitingTxCnt = 0;

	public TxCounter(SystemEnvironment env)
		throws SDMSException
	{
		lastId = getNextQuantum(env);
		nextId = lastId - QUANTUM + 1;
		lastRoId = nextId;

		commitingTx = new long[SystemEnvironment.maxWriter];
	}

	private synchronized long next(SystemEnvironment env, boolean isCommitVersion)
		throws SDMSException
	{
		if(nextId == lastId) {
			lastId = getNextQuantum(env);
			nextId = lastId - QUANTUM;
		}
		++nextId;
		if (isCommitVersion && SystemEnvironment.maxWriter > 1) {
			commitingTx[env.dbConnectionNr] = nextId;
			commitingTxCnt ++;
		}
		return nextId;
	}

	public synchronized long next(SystemEnvironment env, int m, boolean isCommitVersion)
		throws SDMSException
	{
		if(m == SDMSTransaction.READONLY)	return lastRoId;
		else					return next(env, isCommitVersion);
	}

	public synchronized void releaseVersion(SystemEnvironment env)
		throws SDMSException
	{
		if (SystemEnvironment.maxWriter > 1 && commitingTx[env.dbConnectionNr] != 0) {
			commitingTx[env.dbConnectionNr] = 0;
			commitingTxCnt--;
		}
		lastRoId = next(env, false);
	}

	public synchronized long[] getCommitingTx()
	{
		long[] result;

		if (commitingTxCnt > 0) {
			result = new long[commitingTxCnt];

			int j = 0;
			for (int i = 0; i < SystemEnvironment.maxWriter; ++i) {
				if (commitingTx[i] != 0) {
					result[j] = commitingTx[i];
					j++;
				}
			}

			Arrays.sort(result);
		} else {
			result = new long[1];
			result[0] = Long.MAX_VALUE;
		}
		return result;
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

