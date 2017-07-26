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
import javax.net.ssl.*;
import java.security.cert.*;

import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class UserConnection extends SDMSThread
{

	public final static String __version = "@(#) $Id: UserConnection.java,v 2.6.2.1 2013/03/14 10:24:10 ronald Exp $";

	private final static Object initLock = new Object();

	private ConnectionEnvironment env;
	public  de.independit.scheduler.server.parser.Scanner scanner;
	private SDMSParser parser;
	private PrintStream ostream;
	private InputStream istream;
	private Socket sock;
	private int timeout;
	private static int i = 1000;
	private boolean isSSLConnection;
	private boolean isClientAuthenticated;

	public UserConnection(boolean svrtrc, ThreadGroup g, Socket s, SyncFifo f, SyncFifo rof, int port)
	{
		super(g, Integer.toString(++i));
		sock = s;
		if (s instanceof SSLSocket) {
			SSLSocket ssls = (SSLSocket) s;
			isSSLConnection = true;
			MyListener ml = new MyListener();
			ssls.addHandshakeCompletedListener(ml);
			try {
				while (ml.isFinished())
					try {
						Thread.sleep(50);
					} catch (InterruptedException ie) { }
				SSLSession sess = ssls.getSession();
				sess.getPeerCertificates();
				isClientAuthenticated = true;
			} catch(SSLPeerUnverifiedException pue) {
				isClientAuthenticated = false;
			}
		} else {
			isSSLConnection = false;
			isClientAuthenticated = false;
		}
		try {
			synchronized(initLock) {
				ostream = new PrintStream(sock.getOutputStream(), true);
				istream = sock.getInputStream();
			}
		} catch(UnsupportedEncodingException uee) {
			doTrace(null, "Oops -> " + uee, SEVERITY_ERROR);
		} catch(IOException ioe) {
			doTrace(null, "Oops -> " + ioe, SEVERITY_ERROR);
		}
		timeout = SystemEnvironment.sessionTimeout;
		setTimeout(timeout);

		env = new ConnectionEnvironment(i, svrtrc, ostream, f, rof, port, sock.getInetAddress(), isSSLConnection, isClientAuthenticated);
		env.setMe(this);

		doTrace(env, "UserConnection initialized", SEVERITY_MESSAGE);
		ostream.flush();
	}

	public boolean iAmAlive()
	{
		if(!super.isAlive()) return false;
		if(!sock.isConnected()) {
			do_stop();
			return false;
		}
		return true;
	}

	public boolean getIsClientAuthenticated()
	{
		return isClientAuthenticated;
	}

	public boolean isSSLConnection()
	{
		return isSSLConnection;
	}

	public ConnectionEnvironment getEnv()
	{
		return env;
	}

	public int id()
	{
		return env.id();
	}

	public void do_stop()
	{
		run = false;
		try {
			istream.close();
		} catch(IOException ioe) {
		}
		interrupt();
	}

	public void setTimeout(int newTimeout)
	{
		if(newTimeout < 0) return;
		synchronized(sock) {
			try {
				sock.setSoTimeout(newTimeout * 1000);
			} catch (java.net.SocketException se) {
				return;
			}
			timeout = newTimeout;
		}
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void SDMSrun()
	{
		doTrace(env, "UserConnection started", SEVERITY_MESSAGE);
		synchronized(initLock) {
			scanner = new de.independit.scheduler.server.parser.Scanner(new BufferedReader(new InputStreamReader(istream)));
			parser = new SDMSParser(ostream, env);
		}

		scanner.setEnv(env);

		try {
			parser.yyparse(scanner);
		} catch (java.net.SocketTimeoutException ste) {
			if (run) {
				String username = null;
				if(env.isUser()) {
					username = "user " + env.uid();
				} else if(env.isJobServer()) {
					username = "jobserver " +  env.uid();
				} else {
					username = "job " +  env.uid();
				}
				doTrace(env, "Connection (" + username + ") timed out", SEVERITY_WARNING);
			}
		} catch (SDMSException se) {
			if(run) {
				doTrace(env, "Oops -> "+se+" at line "+scanner.yyline(), se.getStackTrace(), SEVERITY_ERROR);
			}
		} catch (Parser.yyException ye) {
			if(run) {
				doTrace(env, "Oops -> "+ye+" at line "+scanner.yyline(), ye.getStackTrace(), SEVERITY_ERROR);
			}
		} catch (Exception e) {
			if(run) {
				doTrace(env, "Oops -> " + e, e.getStackTrace(), SEVERITY_ERROR);
			}
		} catch (Error e) {
			doTrace(env, "Oops -> " + e, e.getStackTrace(), SEVERITY_FATAL);
		}

		try {
			sock.close();
		} catch (IOException ioe) {
			if(run) doTrace(env, "Oops -> Socket close() problem", SEVERITY_ERROR);
		}
		doTrace(env, "UserConnection terminated", SEVERITY_MESSAGE);
	}

}

class MyListener implements HandshakeCompletedListener
{
	private boolean finished;

	public MyListener()
	{
		finished = false;
	}

	public void handshakeCompleted(javax.net.ssl.HandshakeCompletedEvent event)
	{
		SSLSession session = event.getSession();
		SDMSThread.doTrace(null, "\n================================================================", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "Handshake Completed with peer " + session.getPeerHost(), SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "	 cipher: " + session.getCipherSuite(), SDMSThread.SEVERITY_MESSAGE);
		Certificate[] certs = null;
		try {
			certs = session.getPeerCertificates();
		} catch (SSLPeerUnverifiedException puv) {
			certs = null;
		}
		if (certs != null) {
			SDMSThread.doTrace(null, "peer certificates:", SDMSThread.SEVERITY_MESSAGE);
			for (int z=0; z<certs.length; z++)
				SDMSThread.doTrace(null, "	certs["+z+"]: " + certs[z], SDMSThread.SEVERITY_MESSAGE);
		} else {
			SDMSThread.doTrace(null, "No peer certificates presented", SDMSThread.SEVERITY_MESSAGE);
		}
		SDMSThread.doTrace(null, "================================================================", SDMSThread.SEVERITY_MESSAGE);
		finished = true;
	}

	public boolean isFinished()
	{
		return finished;
	}
}
