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

package de.independit.scheduler.jobserver;

import java.util.*;
import java.io.*;
import java.net.*;

public class HttpThread
	extends Thread
{
	private static HttpThread httpThread = null;
	private int port = 0;
	private final Config cfg;
	private boolean run = true;
	private boolean needFooter = false;
	private boolean gotFooter = false;
	private int reqNr = 0;

	private static final String CLGTH = "Content-Length:";
	private static final String POST  = "POST";
	private static final String GET   = "GET";
	private static final String FNAME = "FNAME";
	private static final String LIMIT = "LIMIT";

	private static final int defaultLimit    = 100000;
	private static final int sleepInterval   =   5000;
	private static final int timeoutInterval =   5000;

	private static final String DIRECTORY_UP_PATTERN = ".*[/\\\\]\\.\\.[/\\\\].*";

	private HttpThread (final Config cfg)
	{
		setDaemon (true);
		this.cfg = cfg;
	}

	public static HttpThread getInstance(final Config cfg)
	{
		if (httpThread == null) httpThread = new HttpThread(cfg);
		return httpThread;
	}

	private String extrPostMsg(int msgLength, BufferedReader inpStream)
	{
		final char cbuf[] = new char[msgLength];
		try {
			inpStream.read(cbuf, 0, msgLength);
		} catch (java.io.IOException ioe) {
			return "";
		}
		return new String(cbuf, 0, msgLength);
	}

	private boolean authenticate(HashMap qry)
	{
		return true;
	}

	private String getFile(RandomAccessFile f, long limit)
		throws IOException
	{
		StringBuffer sb = new StringBuffer();
		if (f == null) return null;
		long fl = f.length();
		if (fl > limit) {
			sb.append("<SPAN style=\"color:#ff0000;\">\n");
			sb.append("The length of the requested file exceeds the limit of " + limit + " Bytes !<BR>\n");
			sb.append("Only the last " + limit + " Bytes of the file are returned.<BR>\n");
			sb.append("</SPAN>\n");
			sb.append("<HR>\n");
			f.seek(fl - limit);
			fl = limit;
		}
		byte buf[] = new byte[(int)fl];
		f.read(buf);
		f.close();
		sb.append("<PRE>\n");
		sb.append(new String(buf));
		sb.append("\n</PRE>\n");
		return sb.toString();
	}

	private void executeQuery(String query, PrintWriter out)
		throws java.io.UnsupportedEncodingException
	{
		HashMap qry = new HashMap();
		String[] ql = query.split("\\&");
		for (int i = 0; i < ql.length; i++) {
			String fl[] = ql[i].split("=");
			String value;
			if (fl.length > 1) {
				value = URLDecoder.decode(fl[1],"UTF-8");
				qry.put(fl[0],value);
			}
		}

		if(!authenticate(qry)) {
			out.println("ERROR: insufficient privileges");
		} else {
			RandomAccessFile f = null;

			if (qry.containsKey(FNAME)) {
				String fname = (String) qry.get(FNAME);
				try {
					f = new RandomAccessFile(fname,"r");
				} catch (Exception fe) {
					try {
						f.close();
					} catch (Exception e) {
					}
					printPreamble(out, 404, "Not Found");
					out.println("ERROR: couldn't open the requested file");
					printFooter(out);
					out.flush();
					return;
				}

				Vector patterns = (Vector) cfg.get(Config.NAME_PATTERN);
				boolean accessGranted = false;
				if (patterns != null && !fname.matches(DIRECTORY_UP_PATTERN)) {
					for (int j = 0; j < patterns.size(); ++j) {
						String pattern = (String) patterns.get(j);
						if (fname.matches(pattern)) {
							accessGranted = true;
							break;
						}
					}
				}
				if (!accessGranted) {
					printPreamble(out, 403, "Forbidden");
					out.println("ERROR: The requested filename doesn't match any of the configured patterns");
					Trace.error("[HttpThread] ERROR: Illegal file request : " + fname);
					printFooter(out);
					out.flush();
					return;
				} else {
					long limit;

					if (qry.containsKey(LIMIT)) {
						limit = Long.parseLong((String)(qry.get(LIMIT)));
					} else {
						limit = defaultLimit;
					}

					String s;
					try {
						s = getFile(f, limit);
						if (s == null) {
							printPreamble(out, 500, "Internal Server Error");
							Trace.error("[HttpThread] ERROR: Internal error: getFile returned null");
						} else {
							printPreamble(out, 200, "OK", fname);
							out.print(s);
						}
					} catch (IOException ioe) {
						printPreamble(out, 500, "Internal Server Error");
						Trace.error("[HttpThread] ERROR: IOEception : " + ioe.toString());
					}
				}
			} else {
				printPreamble(out, 400, "Bad Request");
			}
		}
		printFooter(out);

		out.flush();
	}

	private void printPreamble(PrintWriter out, int status, String message)
	{
		printPreamble(out, status, message, null);
	}

	private void printPreamble(PrintWriter out, int status, String message, String title)
	{
		out.println("HTTP/1.0 " + status + " " + message);
		out.println("Content-Type: text/html");
		out.println("Server: BICsuiteJobserver");
		out.println("");
		if (status == 200 || status == 404) {
			out.println("<!DOCTYPE HTML>");
			out.println("<html>");
			String encoding = (String) (cfg.get (Config.HTTP_LOGENCODING));
			if (encoding == null)
				encoding = "utf-8";
			if (title != null) {
				out.println("<head>");
				out.println("<meta charset=\"" + encoding + "\">");
				out.println("<title>" + title + "</title>");
				out.println("</head>");
			}
			out.println("<body>");
		}
		needFooter = true;
	}

	private void printFooter(PrintWriter out)
	{
		out.println("</body>");
		out.println("</html>");
		gotFooter = true;
	}

	public final void run()
	{
		final Runtime rt = Runtime.getRuntime();
		Socket remote = null;

RUNLOOP:	while (run) {
			Long p = (Long) cfg.get (Config.HTTP_PORT);
			if (p != null)
				port = p.intValue();
			else
				port = 0;
			while (port == 0) {
				try {
					sleep(sleepInterval);
					if(!run) break RUNLOOP;
				} catch (Exception e) {
				}
				p = (Long) cfg.get (Config.HTTP_PORT);
				if (p != null)
					port = p.intValue();
				else
					port = 0;
			}

			ServerSocket s = null;
			try {
				while(true) {
					if (s == null) {
						s = new ServerSocket(port);
						s.setSoTimeout(timeoutInterval);
					}
					PrintWriter out = null;
					try {
						remote = s.accept();
						reqNr++;
						String remoteAddress = remote.getInetAddress().getHostAddress();
						remote.setSoTimeout(timeoutInterval);
						BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
						out = new PrintWriter(remote.getOutputStream());
						String str = null;
						int cl = 0;
						String query = "";
						do {
							str = in.readLine();
							Trace.message("[HttpThread] Got Request " + reqNr + " from " + remoteAddress + " : " + str);
							if (str == null)
								break;
							String[] spl = str.split(" ");

							if (spl[0].equals(CLGTH))	cl = Integer.parseInt(spl[1]);
							else if (spl[0].equals(POST))	query = extrPostMsg(cl, in);
							else if (spl[0].equals(GET))
								if (spl.length > 1) {
									String tmp[] = spl[1].split("\\?");
									if (tmp.length > 1)
										query = spl[1].split("\\?")[1];
									else {
										query = "FNAME=" + spl[1];
									}
								} else {
									Trace.error("[HttpThread] Request " + reqNr + " Invalid : " + str);
								}
						} while (!str.equals(""));
						executeQuery(query, out);
						Trace.message("[HttpThread] Request " + reqNr + " from " + remoteAddress + " processed");
					} catch(java.net.SocketTimeoutException ste) {
						Trace.error("[HttpThread] Request " + reqNr + " received SocketTimeoutException");
					} catch(java.lang.Throwable t) {
						port = 0;
						Trace.error("[HttpThread] Request " + reqNr + " caught Exception (300): " + t.toString());
						StackTraceElement trace[] = t.getStackTrace();
						for (int i = 0; i < trace.length; ++i) {
							Trace.error("[HttpThread] " + trace[i].toString());
						}
					} finally {
						try {
							if (remote != null) {
								if (needFooter && !gotFooter && (out != null)) printFooter(out);
								remote.close();
								Trace.message("[HttpThread] Request " + reqNr + " connection closed");
								remote = null;
							}
						} catch (Exception e) {
							Trace.error("[HttpThread] Request " + reqNr + " caught Exception (314) : " + e.toString());
							StackTraceElement trace[] = e.getStackTrace();
							for (int i = 0; i < trace.length; ++i) {
								Trace.error("[HttpThread] " + trace[i].toString());
							}
						}

						p = (Long) cfg.get (Config.HTTP_PORT);
						if (p == null || p.intValue() != port) {
							try {
								s.close();
								s = null;
							} catch (Exception e) {
							}
							break;
						}
					}
				}
			} catch (Exception e) {
				Trace.error("[HttpThread] Something went wrong : " + e.toString());
				try {
					if (run)
						sleep(sleepInterval);
					if (s != null) {
						s.close();
						s = null;
					}
				} catch (Exception eStrich) {
				}
			}
			try {
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
			}
		}
	}

	public void setPort(int p)
	{
		port = p;
	}

	public void setRun(boolean r)
	{
		run = r;
	}
}

