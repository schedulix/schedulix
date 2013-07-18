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


package de.independit.scheduler.shell;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import javax.net.ssl.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.SDMSApp.*;

public class SDMSServerConnection
{

	public final static String __version = "@(#) $Id: SDMSServerConnection.java,v 2.8.2.2 2013/03/15 12:16:53 ronald Exp $";

	String host;
	int port = 2506;
	String user;
	String passwd;
	Socket svrConnection = null;
	ObjectInputStream in = null;
	PrintStream out = null;
	boolean isJob = false;
	boolean debug = false;
	boolean use_ssl = false;
	int timeout = -1;
	String info = null;

	public SDMSServerConnection(String h, int p, String u, String pwd)
	{
		host = h;
		port = p;
		user = u;
		try {
			Long.parseLong(u);
			isJob = true;
		} catch(NumberFormatException nfe) {

		}
		passwd = pwd;
	}

	public SDMSServerConnection(String h, int p, String u, String pwd, int timeout)
	{
		host = h;
		port = p;
		user = u;
		try {
			Long.parseLong(u);
			isJob = true;
		} catch(NumberFormatException nfe) {

		}
		passwd = pwd;
		this.timeout = timeout;
	}

	public SDMSServerConnection(String h, int p, String u, String pwd, int timeout, boolean ssl)
	{
		host = h;
		port = p;
		user = u;
		use_ssl = ssl;
		try {
			Long.parseLong(u);
			isJob = true;
		} catch(NumberFormatException nfe) {

		}
		passwd = pwd;
		this.timeout = timeout;
	}

	public SDMSServerConnection(Options options)
	{
		host = options.getValue(App.HOST);
		try {
			port = Integer.parseInt(options.getValue(App.PORT));
			timeout = options.isSet(App.CONNTIMEOUT) ? Integer.parseInt(options.getValue(App.CONNTIMEOUT)) : -1;
		} catch (NumberFormatException nfe) {

			timeout = -1;
		}
		user = options.getValue(App.USER);
		passwd = options.getValue(App.PASS);
		use_ssl =
		        false;
		info = options.isSet(App.INFO) ? options.getValue(App.INFO) : null;
		if (info != null) {
			info = info.replace("\\","\\\\");
			info = info.replace("'","\\'");
		}
	}

	public SDMSOutput connect() throws IOException
	{
		if (use_ssl) {
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(InetAddress.getByName(host), port);
			svrConnection = sslsocket;
		} else {
			svrConnection = new Socket(InetAddress.getByName(host), port);
		}

		InputStream is = new DataInputStream(svrConnection.getInputStream());
		checkSuperfluous(is);

		out = new PrintStream(svrConnection.getOutputStream(), true);

		return execute("connect " + (isJob ? "job " : "") + user + " identified by '" + passwd +
		               "' with protocol = SERIAL" +
		               (timeout != -1 ? ", timeout = " + timeout : "") +
		               (info != null ? ", session = '" + info + "'" : "" ) + ";");
	}

	public SDMSOutput execute(String cmd)
	{
		SDMSOutput input = null;

		String tCmd = cmd.trim();

		if(debug)
			System.out.println(tCmd);

		try {

			out.print(tCmd);
			if(!(tCmd.endsWith(";"))) {
				out.print(";");
			}
			out.println("");
			if(out.checkError()) {
				input = new SDMSOutput();
				input.setError(new SDMSOutputError("Desktop-0004", "Error on communication channel to Server"));
				return input;
			}
			in = new ObjectInputStream (svrConnection.getInputStream());
			input = (SDMSOutput)in.readObject();

			InputStream is = new DataInputStream(svrConnection.getInputStream());
			checkSuperfluous(is);

		} catch (EOFException eof) {
			input = new SDMSOutput();
			input.setError(new SDMSOutputError("Desktop-0002", "Connection closed by Server"));
		} catch (SocketException se) {
			input = new SDMSOutput();
			input.setError(new SDMSOutputError("Desktop-0003", "Connection closed by Server"));
		} catch (Exception e) {

			input = new SDMSOutput();
			input.setError(new SDMSOutputError("Desktop-0001", "Server Error executing command"));
		}

		if(debug) {
			try {
				SDMSOutputRenderer renderer = new SDMSLineRenderer();
				renderer.render(System.out, input);
			} catch (Exception e) {
				System.err.println (e.toString());
				System.exit (1);
			}
		}

		return input;
	}

	public void finish() throws IOException
	{
		out.println("disconnect;");
		closeall();
	}

	private void closeall() throws IOException
	{
		in.close();
		out.close();
		svrConnection.close();
	}

	private void checkSuperfluous(InputStream is)
	throws IOException
	{
		if (is.available() > 0) {
			byte[] bytes = new byte[5000];
			int howMany = is.read(bytes);
			System.err.println ("got " + howMany + " additional bytes!");
			System.exit(1);
		}
	}

	public String getUser()
	{
		return user;
	}
	public String getHost()
	{
		return host;
	}
	public int getPort()
	{
		return port;
	}
}

