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


package de.independit.scheduler.server;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.sql.*;

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.locking.*;

public class WorkerThread extends SDMSThread
{
	private static String workerLock = "workerLock";

	public final static String __version = "@(#) $Id: WorkerThread.java,v 2.18.2.2 2013/03/15 13:06:48 ronald Exp $";

	private final static String idle = "IDLE";

	private SyncFifo	cmdQueue;
	private int		nr;
	public SystemEnvironment env;
	private int		retryCount;
	private String		state;
	private java.util.Date	state_ts;
	private Node		actNode = null;
	private boolean		protoCommit = false;

	public boolean		commiting;

	public WorkerThread(SystemEnvironment sysEnv, ThreadGroup t, SyncFifo f, int i)
		throws SDMSException
	{
		super(t, "Worker" + Integer.toString(i));
		if (i < SystemEnvironment.maxWriter) protoCommit = true;
		cmdQueue = f;
		nr = i;
		try {
			env = (SystemEnvironment) sysEnv.clone();
		} catch(CloneNotSupportedException cnse) {
			throw new FatalException(new SDMSMessage(sysEnv, "03110181513",
							"Cannot Clone SystemEnvironment"));
		}
		env.dbConnection = Server.connectToDB(env);
		env.dbConnectionNr = nr;
		retryCount = SystemEnvironment.txRetryCount;
		state = idle;
		state_ts = new java.util.Date();
	}

	public int id()
	{
		return nr;
	}

	public String getWorkerState()
	{
		if (actNode != null)
			state = actNode.getName();
		return state;
	}

	public String getWorkerStateTS(SystemEnvironment sysEnv)
	{
		return sysEnv.systemDateFormat.format(state_ts);
	}

	public void SDMSrun()
	{
		Node n;
		ConnectionEnvironment cEnv;
		boolean succeeded;
		PrintStream sav;

		env.thread = this;

		while(run) {
			n = (Node) cmdQueue.get();
			if(!run) break;
			if(n == null) {
				continue;
			}
			actNode = n;
			state = null;
			cEnv = n.getEnv();
			n.getLock();
			commiting = false;
			cEnv.setState(ConnectionEnvironment.ACTIVE);
			cEnv.setLast();
			cEnv.worker = this;
			env.cEnv = cEnv;

			try {
				int i = 0;
				succeeded = false;

				sav = cEnv.ostream;
				cEnv.ostream = System.out;
				SystemEnvironment.incrCntRwTx();

				do {

					cEnv.tx = new SDMSTransaction(env, n.txMode, n.contextVersion);
					state_ts.setTime(cEnv.tx.startTime);
					env.tx = cEnv.tx;
					try {
						if(env.maxWriter > 1 && n.txMode == SDMSTransaction.READWRITE) {
							if(i == retryCount - 1) {
								doTrace(cEnv, "SDMSRun() reached max retryCount, running exclusively now", SEVERITY_MESSAGE);
								SystemEnvironment.incrCntWl();
								LockingSystem.lock(env, workerLock, ObjectLock.EXCLUSIVE);
							} else
								LockingSystem.lock(env, workerLock, ObjectLock.SHARED);
						}

						env.inExecution = true;
						env.initLockCp();
						n.go(env);

						if(n.txMode == SDMSTransaction.READWRITE) {
							SDMSSmeCounterTable.updateCounter(env);
						}

						LockingSystem.release(env, workerLock);

						env.inExecution = false;
						cEnv.setState(ConnectionEnvironment.COMMITTING);
						commiting = true;
						if (protoCommit)
							doTrace(cEnv, "Server Execution time for " + n.getClass() + " : " + (System.currentTimeMillis() - cEnv.tx.startTime) +
								" ms -- Start Committing", SEVERITY_MESSAGE);
						cEnv.tx.commit(env);

						env.sched.publishRequestList(env);
						i = retryCount;
						succeeded = true;
						if (n instanceof Connect) {
							Node cmd = ((Connect) n).getNode();
							doTrace(cEnv, "Execution time for " + n.getClass() + (cmd == null ? "" : "/" + cmd.getClass()) + " : " +
								(cEnv.tx.endTime - cEnv.tx.startTime) + " ms", SEVERITY_MESSAGE);
						} else
							doTrace(cEnv, "Execution time for " + n.getClass() + " : " + (cEnv.tx.endTime - cEnv.tx.startTime) + " ms", SEVERITY_MESSAGE);
					} catch (RecoverableException re) {
						SystemEnvironment.incrCntDl();
						String msg = re.toString();
						doTrace(cEnv, "RecoverableException: " + msg + " in Try " + (i + 1) + " of " + retryCount, SEVERITY_MESSAGE);
						if (msg.contains("Connection lost")) {

							env.dbConnection = Server.connectToDB(env);
						}
						i++;
						if(i == retryCount) {

							n.result.setError(new SDMSOutputError(re.errNumber(), re.toString()));
						}
					} catch (SQLException sqle) {

						throw new FatalException(new SDMSMessage(env, "03110181514", "Error at Commit: $1", sqle.toString()));
					} catch (FatalException fe) {
						throw fe;
					} catch (SDMSException se) {
						i = retryCount;

						n.result.setError(new SDMSOutputError(se.errNumber(), se.toString()));
					} catch (StackOverflowError soe) {
						i = retryCount;
						doTrace(null, soe.toString(), soe.getStackTrace(), SEVERITY_ERROR);
						n.result.setError(new SDMSOutputError("03805150911", "Stack Overflow ! Contact your System Administrator !"));
					} catch(Exception fe) {

						if (SystemEnvironment.fatalIsError) {
							i = retryCount;
							doTrace(null, fe.toString(), fe.getStackTrace(), SEVERITY_ERROR);
							n.result.setError(new SDMSOutputError("03909211556", "Internal Error ! Contact your System Administrator !"));
						} else throw fe;
					} catch(OutOfMemoryError ome) {

						throw ome;
					} catch(Error fe) {

						if (SystemEnvironment.fatalIsError) {
							i = retryCount;
							doTrace(null, fe.toString(), fe.getStackTrace(), SEVERITY_ERROR);
							n.result.setError(new SDMSOutputError("03909211653", "Internal Error ! Contact your System Administrator !"));
						} else throw fe;
					} finally {
						if(!succeeded)
							try {
								cEnv.tx.rollback(env);
								cEnv.ostream.flush();
							} catch (DeadlockException de) {

								throw new FatalException(
								        new SDMSMessage(env, "03110181515", "Deadlock at Rollback"));
							} catch (SQLException sqle) {

								throw new FatalException(
								        new SDMSMessage(env, "03110181516", "Rollback failed"));
							}
					}
				} while(i < retryCount);
				if (SystemEnvironment.auditFile != null && n.auditFlag) {

					if (cEnv.actstmt != null)
						AuditWriter.write(env, cEnv.tx.versionId, cEnv.actstmt);
				}

				cEnv.emptyGid(env);

				cEnv.ostream = sav;
				cEnv.lock().do_notify();

			} catch(SDMSException fe) {

				doTrace(null, fe.toString(), fe.getStackTrace(), SEVERITY_FATAL);
			} catch(Exception fe) {

				doTrace(null, fe.toString(), fe.getStackTrace(), SEVERITY_FATAL);
			} catch(Error fe) {

				doTrace(null, fe.toString(), fe.getStackTrace(), SEVERITY_FATAL);
			} finally {
				cEnv.worker = null;
			}
			n.releaseLock();
			actNode = null;
			state = idle;
			state_ts.setTime(System.currentTimeMillis());
		}

		try {
			env.dbConnection.close();
		} catch(Throwable sqle) {

		}
		return;
	}

}

