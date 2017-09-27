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
import java.security.*;
import javax.xml.bind.DatatypeConverter;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.SDMSApp.*;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Win32Exception;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

public class SDMSServerConnection
{
	String host;
	int port = 2506;
	String auth = App.BASIC;
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
	KeyManagerFactory kmf;
	SSLContext sc = null;

	public SDMSServerConnection(String h, int p, String u, String pwd, String auth)
	{
		this.auth = auth;
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

	public SDMSServerConnection(String h, int p, String u, String pwd, String auth, int timeout)
	{
		this.auth = auth;
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

	public SDMSServerConnection(String h, int p, String u, String pwd, String auth, int timeout, boolean ssl)
	{
		this.auth = auth;
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
		        options.isSet(App.TLS) ? options.getOption(App.TLS).getBValue() :
		        false;
		info = options.isSet(App.INFO) ? options.getValue(App.INFO) : null;
		if (info != null) {
			info = info.replace("\\\\","\\\\");
			info = info.replace("'","\\'");
		}
	}

	public SDMSOutput connect(Options options)
	throws IOException
	{
		if (use_ssl) {
			KeyStore ks = null;
			String ksName;
			char[] ksPass = null;

			if (options.isSet(App.KEYSTORE) && options.isSet(App.KEYSTOREPW)) {
				ksName = options.getValue(App.KEYSTORE);
				System.setProperty("javax.net.ssl.keyStore", ksName);
				System.setProperty("javax.net.ssl.keyStorePassword", options.getValue(App.KEYSTOREPW));
				ksPass = options.getValue(App.KEYSTOREPW).toCharArray();
				try {
					ks = KeyStore.getInstance("JKS");
					ks.load(new FileInputStream(ksName), ksPass);
				} catch (Exception e) {
					System.out.println(e.toString());
					System.exit(1);
				}
			}
			if (options.isSet(App.TRUSTSTORE) && options.isSet(App.TRUSTSTOREPW)) {
				System.setProperty("javax.net.ssl.trustStore", options.getValue(App.TRUSTSTORE));
				System.setProperty("javax.net.ssl.trustStorePassword", options.getValue(App.TRUSTSTOREPW));
			}
			try {
				kmf = KeyManagerFactory.getInstance("SunX509");
				if (ks == null)
					kmf.init(null, null);
				else
					kmf.init(ks, ksPass);

				sc = SSLContext.getInstance("SSL");
				sc.init(kmf.getKeyManagers(), null, null);
			} catch (Exception e) {
				System.out.println(e.toString());
				System.exit(1);
			}

			SSLSocketFactory ssf = sc.getSocketFactory();
			SSLSocket sslsocket = (SSLSocket) ssf.createSocket(InetAddress.getByName(host), port);
			sslsocket.startHandshake();
			svrConnection = sslsocket;
		} else {
			svrConnection = new Socket();
			svrConnection.setPerformancePreferences(0, 1, 0);
			svrConnection.setTcpNoDelay(true);
			svrConnection.connect(new InetSocketAddress(InetAddress.getByName(host), port));
		}

		InputStream is = new DataInputStream(svrConnection.getInputStream());
		checkSuperfluous(is);

		out = new PrintStream(svrConnection.getOutputStream(), true);

		String userSQuote = "'";
		String userEQuote = "'";
		if (isJob) {
			auth = App.BASIC;
			userSQuote = "job ";
			userEQuote = "";
		}

		SDMSOutput result;
		String strToken = null;
		byte[] byteToken;
		IWindowsSecurityContext clientContext = null;
		if (options.isSet(App.AUTH))
			auth = options.getValue(App.AUTH);
		switch (auth) {
			case App.BASIC:
				return execute("connect " + userSQuote + user + userEQuote + " identified by '" + passwd +
			"' with protocol = SERIAL" +
			(timeout != -1 ? ", timeout = " + timeout : "") +
			(info != null ? ", session = '" + info + "'" : "" ) + ";");
			case App.WINSSO:
				String spn = null;
				if (options.isSet(App.SPN))
					spn = options.getValue(App.SPN);
				try {
					clientContext = WindowsSecurityContextImpl.getCurrent( "Negotiate", spn );
					byteToken = clientContext.getToken();
					strToken = DatatypeConverter.printBase64Binary(byteToken);
				} catch (Throwable e) {
					clientContext.dispose();
					result = new SDMSOutput();
					result.setError(new SDMSOutputError("Desktop-0006", "Exception getting clientToken:" + e.toString() + " !"));
					return result;
				}

				String cmd = "connect with" +
				             " token = '" + strToken + "'" +
				             ", protocol = SERIAL" +
				             ", method = '" + auth + "'" +
				             (timeout != -1 ? ", timeout = " + timeout : "") +
				             (info != null ? ", session = '" + info + "'" : "" ) + ";";

				result = execute(cmd);
				if (result.error != null) {
					clientContext.dispose();
					return result;
				}

				int idxToken = result.container.indexForName (null, "TOKEN");
				Vector v_data = (Vector)(result.container.dataset.get(0));
				strToken = (String)(v_data.get(idxToken));
				byteToken = DatatypeConverter.parseBase64Binary(strToken);
				try {
					SecBufferDesc continueToken = new SecBufferDesc(Sspi.SECBUFFER_TOKEN, byteToken);
					clientContext.initialize(clientContext.getHandle(), continueToken, spn);
					byteToken = clientContext.getToken();
					strToken = DatatypeConverter.printBase64Binary(byteToken);
				} catch (Throwable e) {
					result = new SDMSOutput();
					result.setError(new SDMSOutputError("Desktop-0006", "Exception getting clientToken:" + e.toString() + " !"));
					return result;
				} finally {
					clientContext.dispose();
				}

				cmd = "continue connect with token = '" + strToken + "';";
				SDMSOutput result2 = execute(cmd);
				if (result2.error != null) {
					return result;
				}

				cmd = "show user;";
				result = execute(cmd);
				if (result.error != null) {
					return result;
				}

				int idxName = result.container.indexForName (null, "NAME");
				v_data = (Vector)(result.container.dataset.get(0));
				App.userName = (String)(v_data.get(idxName));
				if (options.isSet(App.USER)) {
					String userName  = options.getValue(App.USER);
					if (!userName.equals(App.userName)) {
						App.userName = userName;
						cmd = "alter session set user = '" + userName + "';";
						result = execute(cmd);
						if (result.error != null) {
							return result;
						}
					}
				}

				return result2;
			default:
				result = new SDMSOutput();
				result.setError(new SDMSOutputError("Desktop-0005", "Invalid auth option '" + auth + "'!"));
				return result;
		}
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
		out.close();
		in.close();
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

