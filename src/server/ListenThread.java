/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public abstract class ListenThread extends SDMSThread
{

	public final static String __version = "@(#) $Id: ListenThread.java,v 2.4.6.1 2013/03/14 10:24:09 ronald Exp $";

	public final static int LISTENER = 1;
	public final static int SERVICE = 2;

	private int		port;
	private int		maxConnections;
	private int             svrtype;
	private ThreadGroup	uc;
	protected ServerSocket	serv;
	private	UserConnection[] connects;
	private SyncFifo	cmdQueue;
	private SyncFifo	roCmdQueue;
	private boolean trace;

	public ListenThread(ThreadGroup t, int p, int mc, SyncFifo f, SyncFifo rof, int type)
	{
		super(t, type == LISTENER ? "Listener" : "Service" );
		svrtype = type;
		port = p;
		uc = t;
		if(port <= 0) port = 2506;
		maxConnections = mc;
		if(maxConnections <= 0) maxConnections = 1000;
		run = true;
		connects = new UserConnection[maxConnections];
		cmdQueue = f;
		roCmdQueue = rof;
		trace = false;

		de.independit.scheduler.server.parser.Scanner s = new de.independit.scheduler.server.parser.Scanner((java.io.Reader) null);
		if(s == null) {
			throw new RuntimeException("new on Scanner failed");
		}
	}

	public int id()
	{
		return 0;
	}

	public boolean trace()
	{
		return trace;
	}

	public void trace_on()
	{
		int next;
		trace = true;
		for(next = 0; next < maxConnections; ++next) {
			if(connects[next] == null) break;
			if(!connects[next].isAlive()) break;
			connects[next].getEnv().trace_on();
		}
	}

	public void trace_off()
	{
		int next;
		trace = false;
		for(next = 0; next < maxConnections; ++next) {
			if(connects[next] == null) break;
			if(!connects[next].isAlive()) break;
			connects[next].getEnv().trace_off();
		}
	}

	abstract ServerSocket getServerSocket(int port)
	throws IOException;

	private void init()
	{
		try {
			serv = getServerSocket(port);
			serv.setSoTimeout(1000);
		} catch (IOException ioe) {
			doTrace(null, "Oops: ServerSocket open() problem: " + ioe + "\nPort = " + port, SEVERITY_FATAL);
		}
	}

	private void exit()
	{
		try {
			serv.close();
		} catch (IOException ioe) {
			doTrace(null, "Oops: ServerSocket close() problem: " + ioe, SEVERITY_FATAL);
		}
	}

	public void print(PrintStream o)
	{
		o.println(getName());
		o.println("Port : " + port);
		o.println("Max Connections : " + maxConnections);
		o.println("Trace : " + trace);
	}

	protected Socket accept()
	throws InterruptedIOException, IOException
	{
		return serv.accept();
	}

	public void SDMSrun()
	{
		Socket		sock;

		init();

		try {
			run:		while(run) {
				int next;

				do {
					for(next = 0; next < maxConnections; ++next) {
						if(connects[next] == null) break;
						if(!connects[next].isAlive()) break;
					}
					if(next == maxConnections) {

						if(svrtype == LISTENER)
							doTrace(null, "Out of user connects, waiting 1 second", SEVERITY_WARNING);
						try {
							sleep(1000);
						} catch(InterruptedException ie) {

							doTrace(null, "Interrupted " + getName(), SEVERITY_WARNING);
							continue run;
						}
					}
				} while(next >= maxConnections);

				try {
					sock = accept();
					sock.setKeepAlive(true);
					connects[next] = new UserConnection(trace, uc, sock, cmdQueue, roCmdQueue, port);
					connects[next].start();
					sock = null;
				} catch (InterruptedIOException iioe) {
					continue run;
				} catch (IOException ioe) {
					doTrace(null, "Exception : " + ioe, SEVERITY_WARNING);
					break;
				}

				for(next = 0; next < maxConnections; ++next) {
					if(connects[next] == null) continue;
					if(!connects[next].isAlive()) connects[next] = null;
				}
			}
		} catch(Error e) {
			doTrace(null, e.toString(), e.getStackTrace(), SEVERITY_FATAL);
		}
		doTrace(null, (svrtype == LISTENER ? "Listener" : "Service") + " waiting for UserConnections to terminate", SEVERITY_INFO);
		for(int i = 0; i < maxConnections; ++i) {
			if(connects[i] == null) continue;
			if(!connects[i].isAlive()) continue;
			try {
				doTrace(null, "Waiting for " + connects[i].toString(), SEVERITY_INFO);
				connects[i].do_stop();
				connects[i].join();
			} catch(InterruptedException ie) {
				--i;
			}
		}

		exit();

		return;
	}

}

