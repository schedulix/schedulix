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

import java.io.*;
import java.net.*;

import de.independit.scheduler.server.SDMSConstants;

public class WakeupThread
	extends Thread
{
	public static final String __version = "@(#) $Id";

	private static WakeupThread wecker = null;
	private int port = 0;
	private byte[] buf = new byte[4096];
	private final Config cfg;
	private boolean run = true;

	private WakeupThread (final Config cfg)
	{
		setDaemon (true);
		this.cfg = cfg;
	}

	public static WakeupThread getInstance(final Config cfg)
	{
		if (wecker == null) wecker = new WakeupThread(cfg);
		return wecker;
	}

	public final void run()
	{
		final Runtime rt = Runtime.getRuntime();

RUNLOOP:	while (run) {
			Long p = (Long) cfg.get (Config.NOTIFY_PORT);
			if (p != null)
				port = p.intValue();
			else
				port = 0;
			while (port == 0) {
				try {
					sleep(5000);
					if(!run) break RUNLOOP;
				} catch (Exception e) {
				}
				p = (Long) cfg.get (Config.NOTIFY_PORT);
				if (p != null)
					port = p.intValue();
				else
					port = 0;
			}

			DatagramSocket s = null;
			try {
				s = new DatagramSocket(port);
				s.setSoTimeout(5000);
				while(true) {
					DatagramPacket d = new DatagramPacket(buf, buf.length);
					try {
						s.receive(d);
						String msg = new String(d.getData(), 0, d.getLength());
						try {
							Server.notified = true;
							Notifier.interrupt(SDMSConstants.lZERO);
						} catch(NumberFormatException nfe) {
							Trace.error("[WakeupThread] got a message I don't understand : >" + msg + "< (length : " + msg.length() + ")");
							Trace.error("[WakeupThread] offending IP : " + d.getAddress().toString() + ", Port : " + d.getPort());
						}
					} catch(java.net.SocketTimeoutException ste) {
						/* do nothing */
					}
					p = (Long) cfg.get (Config.NOTIFY_PORT);
					if (p == null || p.intValue() != port) {
						try {
							s.close();
						} catch (Exception e) {
							/* do nothing */
						}
						break;
					}
				}
			} catch (Exception e) {
				Trace.error("[WakeupThread] Something went wrong : " + e.toString());
				try {
					if (run)
						sleep(5000);
				} catch (Exception eStrich) {
					/* do nothing */
				}
			}
			try {
				if (s != null)
					s.close();
			} catch (Exception e) {
				/* do nothing */
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

