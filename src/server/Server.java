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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class Server
{

	public final static String __version = "@(#) $Id: Server.java,v 2.17.2.6 2013/09/11 11:50:39 ronald Exp $";

	private ThreadGroup utg;
	private SSLListenThread ssllt;
	private OrdinaryListenThread ult;
	private OrdinaryListenThread svt;
	private SchedulingThread wst;
	private GarbageThread gst;
	private TimerThread tt;
	private TriggerThread trt;
	private ThreadGroup wg;
	private SyncFifo cmdQueue;
	private SyncFifo roCmdQueue;
	private WorkerThread[] wt;
	private ShutdownThread shutt;
	private RenewTicketThread rtt;
	private DBCleanupThread dbct;
	private NotifierThread notifier;
	private String iniFile;

	private SystemEnvironment env;

	public Server(String inifile, boolean adminMode, boolean protectMode, String programLevel)
	{
		iniFile = inifile;
		Properties props = new Properties();
		InputStream ini;

		ini = Server.class.getResourceAsStream(inifile);
		try {
			if(ini == null)
				ini = new FileInputStream(inifile);
			props.load(ini);
		} catch(FileNotFoundException fnf) {
			SDMSThread.doTrace(null, "Properties File not found : " + fnf, SDMSThread.SEVERITY_FATAL);
		} catch(IOException ioe) {
			SDMSThread.doTrace(null, "Error loading Properties file: " + ioe, SDMSThread.SEVERITY_FATAL);
		}
		for (Enumeration e = props.propertyNames() ; e.hasMoreElements() ;) {
			String k = (String) e.nextElement();
			if(k.equals(SystemEnvironment.S_DBPASSWD))		continue;
			if(k.equals(SystemEnvironment.S_SYSPASSWD))		continue;
			if(k.equals(SystemEnvironment.S_KEYSTOREPASSWORD))	continue;
			if(k.equals(SystemEnvironment.S_TRUSTSTOREPASSWORD))	continue;
			SDMSThread.doTrace(null, k + "=" + props.getProperty(k), SDMSThread.SEVERITY_INFO);
		}

		env = new SystemEnvironment(props, programLevel);
		if(adminMode) env.disableConnect();
		if(protectMode) SystemEnvironment.setProtectMode();
		SystemEnvironment.server = this;
	}

	public String getIniFile()
	{
		return iniFile;
	}

	private void initShutdownThread()
	{
		shutt = new ShutdownThread(env, this);
		Runtime r = Runtime.getRuntime();
		r.addShutdownHook(shutt);
	}

	private void initRenewTicketThread() throws SDMSException
	{
		rtt = new RenewTicketThread(this);
		SystemEnvironment.ticketThread = rtt;
		rtt.initRenewTicketThread(env);
		rtt.getTicket(rtt.pSysEnv);
	}

	private void startRenewTicketThread() throws SDMSException
	{
		SDMSThread.doTrace(null, "Starting Renew Ticket Thread", SDMSThread.SEVERITY_INFO);
		rtt.start();
	}

	private void initDBCleanupThread() throws SDMSException
	{
		dbct = new DBCleanupThread(this);
		SystemEnvironment.dbCleanupThread = dbct;
		dbct.initDBCleanupThread(env);
	}

	private void startDBCleanupThread() throws SDMSException
	{
		SDMSThread.doTrace(null, "Starting Database Cleanup Thread", SDMSThread.SEVERITY_INFO);
		dbct.start();
	}

	private void initNotifierThread() throws SDMSException
	{
		notifier = new NotifierThread(env, roCmdQueue);
		SystemEnvironment.notifier = notifier;
		notifier.initNotifierThread(env);
	}

	private void startNotifierThread() throws SDMSException
	{
		SDMSThread.doTrace(null, "Starting Notifier Thread", SDMSThread.SEVERITY_INFO);
		notifier.start();
	}

	private void createRepository() throws SDMSException
	{
		new SDMSRepository(env);
	}

	private void initWorkers() throws SDMSException
	{
		wg = new ThreadGroup("WorkerThreads");
		cmdQueue = new SyncFifo();
		roCmdQueue = new SyncFifo();
		SystemEnvironment.wg = wg;
	}

	private void startWorkers() throws SDMSException
	{
		int maxWorker;
		SyncFifo q;

		maxWorker = SystemEnvironment.maxWorker + SystemEnvironment.maxWriter;

		SDMSThread.doTrace(null, "Starting " + maxWorker + " Worker Threads", SDMSThread.SEVERITY_INFO);
		wt = new WorkerThread[maxWorker];

		q = cmdQueue;
		for(int i=0; i < maxWorker; ++i) {
			if (i >= SystemEnvironment.maxWriter)
				q = roCmdQueue;
			wt[i] = new WorkerThread(env, wg, q, i);
			wt[i].start();
		}
	}

	private void initScheduling() throws SDMSException
	{
		wst = new SchedulingThread(env, cmdQueue);
		wst.spinDelay = 50;
		SystemEnvironment.sched = wst;
	}

	public void startScheduling() throws SDMSException
	{
		if(SystemEnvironment.getProtectMode()) return;
		if(wst.isAlive()) return;
		SDMSThread.doTrace(null, "Starting Scheduling Thread", SDMSThread.SEVERITY_INFO);
		wst.start();
	}

	private void initGC() throws SDMSException
	{
		gst = new GarbageThread(env, cmdQueue);
		SystemEnvironment.garb = gst;
	}

	private void startGC() throws SDMSException
	{
		SDMSThread.doTrace(null, "Starting Garbage Collection Thread", SDMSThread.SEVERITY_INFO);
		gst.start();
	}

	private void initTT() throws SDMSException
	{
		trt = new TriggerThread(env, cmdQueue);
		SystemEnvironment.tt = trt;
	}

	private void startTT() throws SDMSException
	{
		SDMSThread.doTrace(null, "Starting Trigger Thread", SDMSThread.SEVERITY_INFO);
		trt.start();
	}

	private void initTimeScheduling() throws SDMSException
	{
		tt = new TimerThread(env, cmdQueue);
		SystemEnvironment.timer = tt;
	}

	public void startTimeScheduling() throws SDMSException
	{
		if(SystemEnvironment.getProtectMode()) return;
		if(tt.isAlive()) return;
		SDMSThread.doTrace(null, "Starting Time Scheduling Thread", SDMSThread.SEVERITY_INFO);
		tt.start();
	}

	private void initListener()
	{
		utg = new ThreadGroup(wg, "UserThreads");
		SystemEnvironment.utg = utg;
	}

	private void startListener()
	{

		SDMSThread.doTrace(null, "Starting Listener Thread(s)", SDMSThread.SEVERITY_INFO);
		if (SystemEnvironment.port != 0)  {
			ult = new OrdinaryListenThread(utg, SystemEnvironment.port, SystemEnvironment.maxConnects, cmdQueue, roCmdQueue, ListenThread.LISTENER);

			ult.start();
		} else {
			SDMSThread.doTrace(null, "Standard communication Listener disabled", SDMSThread.SEVERITY_INFO);
			ult = null;
		}

		if (SystemEnvironment.service_port != 0) {
			svt = new OrdinaryListenThread(utg, SystemEnvironment.service_port, 1, cmdQueue, roCmdQueue, ListenThread.SERVICE);

			svt.start();
		} else {
			SDMSThread.doTrace(null, "Service port Listener disabled", SDMSThread.SEVERITY_INFO);
			svt = null;
		}

		if (SystemEnvironment.sslport != 0) {
			ssllt = new SSLListenThread(utg, SystemEnvironment.sslport, SystemEnvironment.maxConnects, cmdQueue, roCmdQueue, ListenThread.LISTENER);

			ssllt.start();
			try {
				Thread.sleep(1000);
			} catch (java.lang.InterruptedException ie) {  }
			String[] prots = ssllt.getProtocols();
			if (prots != null) {
				SDMSThread.doTrace(null, "TLS Listen Thread started. Supported Protocols :", SDMSThread.SEVERITY_INFO);
				for (int i = 0; i < prots.length; ++i)
					SDMSThread.doTrace(null, "\t" + prots[i], SDMSThread.SEVERITY_INFO);
			}
		} else {
			SDMSThread.doTrace(null, "SSL communication Listener disabled", SDMSThread.SEVERITY_INFO);
			ssllt = null;
		}
	}

	public void shutdown()
	{
		if(gst != null) {
			if(gst.isAlive()) {
				gst.do_stop();
				SDMSThread.doTrace(null, "Stopped " + gst.toString(), SDMSThread.SEVERITY_INFO);
			}
		}
		if(ult != null) {
			if(ult.isAlive()) {
				ult.do_stop();
			}
			killAll();
		}
		if(svt != null) {
			if(svt.isAlive()) {
				svt.do_stop();
			}
			killAll();
		}
		if(ssllt != null) {
			if(ssllt.isAlive()) {
				ssllt.do_stop();
			}
			killAll();
		}
		if(wst != null) {
			if(wst.isAlive()) {
				wst.do_stop();
				SDMSThread.doTrace(null, "Stopped " + wst.toString(), SDMSThread.SEVERITY_INFO);
			}
		}
		if(notifier != null) {
			if(notifier.isAlive()) {
				notifier.do_stop();
				SDMSThread.doTrace(null, "Stopped " + notifier.toString(), SDMSThread.SEVERITY_INFO);
			}
		}
		if(tt != null) {
			if(tt.isAlive()) {
				tt.do_stop();
				SDMSThread.doTrace(null, "Stopped " + tt.toString(), SDMSThread.SEVERITY_INFO);
			}
		}
		if(rtt != null) {
			if(rtt.isAlive()) {
				rtt.do_stop();
				SDMSThread.doTrace(null, "Stopped " + rtt.toString(), SDMSThread.SEVERITY_INFO);
			}
		}
		if (dbct != null) {
			if (dbct.isAlive()) {
				dbct.do_stop();
				SDMSThread.doTrace(null, "Stopped " + dbct.toString(), SDMSThread.SEVERITY_INFO);
			}
		}
		if(wt != null) {
			for(int i = 0; i < wt.length; ++i) {
				if(wt[i] != null) {
					if(wt[i].isAlive()) {
						wt[i].do_stop();
						SDMSThread.doTrace(null, "Stopped " + wt[i].toString(), SDMSThread.SEVERITY_INFO);
					}
				}
			}
		}
		shutt.do_stop();
	}

	public void killUser(int cid)
	{
		int numUser = utg.activeCount();
		SDMSThread list[];
		int i;

		list = new SDMSThread[numUser];

		utg.enumerate(list);
		for(i = 0; i< numUser; i++) {
			if(list[i] != null && list[i].id() == cid) {
				list[i].do_stop();
			}
		}
	}

	public void killAll()
	{
		int numUser = utg.activeCount();
		SDMSThread list[];
		int i;

		list = new SDMSThread[numUser];

		utg.enumerate(list);
		for(i = 0; i< numUser; i++) {
			if(list[i] != null && list[i].isAlive()) {
				list[i].do_stop();
				SDMSThread.doTrace(null, "Stopped " + list[i].toString(), SDMSThread.SEVERITY_INFO);
			}
		}
	}

	public static synchronized Connection connectToDB(SystemEnvironment env) throws FatalException
	{
		String jdbcDriver = SystemEnvironment.jdbcDriver;
		String dbUrl = SystemEnvironment.dbUrl;
		String dbUser = SystemEnvironment.dbUser;
		String dbPasswd = SystemEnvironment.dbPasswd;
		Connection c;

		if(jdbcDriver == null)
			throw new FatalException(new SDMSMessage(env,
							"03110181509", "No JDBC Driver Specified"));
		if(dbUrl == null) throw new FatalException(new SDMSMessage(env,
							"03110181510", "No JDBC URL Specified"));

		try {
			Class.forName(jdbcDriver);
		} catch(ClassNotFoundException cnf) {
			throw new FatalException(new SDMSMessage(env,
							"03110181511", "Class $1 not Found", jdbcDriver));
		}
		try {
			c = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
		} catch(SQLException sqle) {
			throw new FatalException(new SDMSMessage(env,
							"03110181512", "Unable to connect to $1, $2", dbUrl, sqle.toString()));
		}
		try {
			c.setAutoCommit(false);
		} catch(SQLException sqle) {
			throw new FatalException(new SDMSMessage(env,
			                         "03202071128", "Cannot set autocommit off ($1)", sqle.toString()));
		}

		if (SystemEnvironment.SQUOTE == null) {
			try {
				final String driverName = c.getMetaData().getDriverName();
				SDMSThread.doTrace(null, "JDBC Driver used : " + driverName, SDMSThread.SEVERITY_INFO);
				if (driverName.startsWith("MySQL") || driverName.startsWith("MariaDB")) {
					SystemEnvironment.SQUOTE = "`";
					SystemEnvironment.EQUOTE = "`";
				} else if (driverName.startsWith("Microsoft")) {
					SystemEnvironment.SQUOTE = "[";
					SystemEnvironment.EQUOTE = "]";
				} else {
					if (driverName.startsWith("PostgreSQL"))
						SystemEnvironment.isPostgreSQL = true;
					SystemEnvironment.SQUOTE = "";
					SystemEnvironment.EQUOTE = "";
				}
			} catch (SQLException sqle) {
				SDMSThread.doTrace(null, "Unknown JDBC Driver used; run into an exception while trying to determine the Driver Name : " + sqle.toString(), SDMSThread.SEVERITY_FATAL);
				SystemEnvironment.SQUOTE = "";
				SystemEnvironment.EQUOTE = "";
			}
		}
		return c;
	}

	public void serverMain()
	{
		try {
			initShutdownThread();
			initRenewTicketThread();
			try {
				startRenewTicketThread();
			} catch(SDMSException fe1) {
				SDMSThread.doTrace(null, (new SDMSMessage(env, "03302061700",
							"Fatal exception while starting TicketThread:\n$1", fe1.toString())).toString(), SDMSThread.SEVERITY_FATAL);
			}
			createRepository();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03202252201",
							"Fatal exception while loading Repository:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			SDMSThread.doTrace(null, "Initializing System Threads", SDMSThread.SEVERITY_INFO);
			initWorkers();
			initListener();
			initScheduling();
			initTimeScheduling();
			initTT();
			initGC();
			initNotifierThread();
			if (env.dbPreserveTime > 0)
				initDBCleanupThread();
			else
				dbct = null;
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03202252202",
							"Fatal exception while initializing System Threads:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			startWorkers();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03202252203",
							"Fatal exception while starting Workerthreads:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			startScheduling();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03202252204",
							"Fatal exception while starting SchedulingThread:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			startTimeScheduling();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03206082124",
							"Fatal exception while starting Time Scheduling:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			startTT();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03407301455",
							"Fatal exception while starting trigger thread:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			startGC();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03311120827",
							"Fatal exception while starting garbage collector:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			startNotifierThread();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03701031503",
			                          "Fatal exception while starting notifier thread:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		try {
			if (dbct != null) startDBCleanupThread();
		} catch(SDMSException fe) {
			SDMSThread.doTrace(null, (new SDMSMessage(env, "03311141139",
			                          "Fatal exception while starting dbCleanupThread:\n$1", fe.toString())).toString(), SDMSThread.SEVERITY_FATAL);
		}
		startListener();
		SDMSMessage m = new SDMSMessage(env, "03110212341", "-- $1 -- $2 -- $3 -- ready --",
							"SDMS", "Server", "Systems");
		SDMSThread.doTrace(null, m.toString(), SDMSThread.SEVERITY_INFO);

		for(int i = 0; i < wt.length; ++i) {
			try {
				wt[i].join();
				SDMSThread.doTrace(null, "Worker " + i + " terminated", SDMSThread.SEVERITY_INFO);
			} catch(InterruptedException ie) {
				--i;
			}
			if(i == 0) {
				shutdown();
			}
		}
		while(true) {
			try {
				SDMSThread.doTrace(null, "Waiting for Listener", SDMSThread.SEVERITY_INFO);
				ult.interrupt();
				ult.join();
				SDMSThread.doTrace(null, "Listener terminated", SDMSThread.SEVERITY_INFO);
				if (svt != null) {
					SDMSThread.doTrace(null, "Waiting for ServiceThread", SDMSThread.SEVERITY_INFO);
					svt.interrupt();
					svt.join();
					SDMSThread.doTrace(null, "ServiceThread terminated", SDMSThread.SEVERITY_INFO);
				}
				if (dbct != null) {
					SDMSThread.doTrace(null, "Waiting for DBCleanup", SDMSThread.SEVERITY_INFO);
					if (dbct.isAlive()) {
						dbct.join();
					}
					SDMSThread.doTrace(null, "DBCleanup Thread terminated", SDMSThread.SEVERITY_INFO);
				}
			} catch(InterruptedException ie) {
				continue;
			}
			break;
		}

		System.exit(0);
	}

}

