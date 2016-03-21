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

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class SSLListenThread extends ListenThread
{

	public final static String __version = "@(#) $Id: SSLListenThread.java,v 2.2.6.1 2013/03/14 10:24:09 ronald Exp $";

	private String[] prots = null;

	public SSLListenThread(ThreadGroup t, int p, int mc, SyncFifo f, SyncFifo rof, int type)
	{
		super(t, p, mc, f, rof, type);
	}

	ServerSocket getServerSocket(int port)
	throws IOException
	{
		SSLServerSocketFactory sslserversocketfactory =
		        (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket sslserversocket =
		        (SSLServerSocket) sslserversocketfactory.createServerSocket(port);

		sslserversocket.setNeedClientAuth(SystemEnvironment.clientAuthentication);

		prots = sslserversocket.getSupportedProtocols();

		return sslserversocket;
	}

	protected Socket accept()
	throws InterruptedIOException, IOException
	{
		return (SSLSocket) serv.accept();
	}

	public String[] getProtocols()
	{
		return prots;
	}

}

