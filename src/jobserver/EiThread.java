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
import java.nio.channels.*;
import java.util.*;

public class EiThread
	extends Thread
{
	private static final long DEFAULT_NOP_DELAY = 30000;

	private final RepoIface ri;
	private final Config    cfg;
	private final String    jid;
	private final File workdir;

	private final String[] env;

	public EiThread (final RepoIface ri, final Config cfg, final String jid)
	{
		this (ri, cfg, jid, ri.getJobData().env, null, ri.getJobData().jobenv);
	}

	public EiThread (final RepoIface ri, final Config cfg, final String jid, final Environment env, File workdir, final Environment jobenv)
	{
		this.ri  = ri;
		this.cfg = cfg;
		this.jid = jid;
		this.workdir = workdir;

		final Environment e = Environment.getSystemEnvironment().merge (env, ri, cfg);
		if (jobenv != null) e.putAll(jobenv);
		this.env = e.toArray();

		setPriority (Thread.MIN_PRIORITY);
		setDaemon (true);
	}

	public final void run()
	{
		final Runtime rt = Runtime.getRuntime();
		boolean wd_ok = true;
		int rc = 0;

		final String[] cmdarray;
		final Feil feil = Server.getFeil(cfg, jid);
		synchronized(feil) {
			cmdarray = new String[] {
				cfg.get (Config.JOB_EXECUTOR).toString(),
				String.valueOf (ProcessInfo.getBoottimeHow()),
				feil.getFilename().toString(),
				ProcessInfo.getBoottime(ProcessInfo.getBoottimeHow())
			};
		}

		final long nop_delay = ((Long) cfg.get (Config.NOP_DELAY)).longValue();
		final long breed_delay = Math.min(nop_delay, DEFAULT_NOP_DELAY);

		if (workdir != null && ((Boolean) cfg.get (Config.CREATE_WORKDIR)).booleanValue()) {
			if (!workdir.exists())
				if (!workdir.mkdirs()) {
					ri.notifyError(RepoIface.NONFATAL, "(03110261111) Cannot create working directory " + workdir.toString());
					wd_ok = false;
				}
		}

		if (wd_ok) {
			Process p;
			int trycnt = 0;
			while (true) {
				try {
					p = rt.exec (cmdarray, env);
					break;
				} catch (final IOException ioe) {
					Utils.sleep (1000);
					trycnt ++;
					if (trycnt >= 5) {
						ri.notifyError (RepoIface.NONFATAL, "(04302012116) Cannot launch job executor: " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
						System.exit(1);
					}
				}
			}

			try {

				InputStream i1 = p.getInputStream();
				i1.close();
			} catch (final IOException ioe) {
				Trace.warning("(03210171637) Error closing pipe stdout : " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");

			}
			try {

				InputStream i1 = p.getErrorStream();
				i1.close();
			} catch (final IOException ioe) {
				Trace.warning("(03210171638) Error closing pipe stderr : " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");

			}
			try {

				OutputStream i1 = p.getOutputStream();
				i1.close();
			} catch (final IOException ioe) {
				Trace.warning("(03210171639) Error closing pipe stdin : " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");

			}

			while (true) {
				try {
					rc = p.waitFor();
					break;
				} catch (final InterruptedException ie) {

				}
			}
		}

		IOException ioe = null;
		synchronized(feil) {

			if (Server.feilExists(jid)) {
				try {
					feil.open();
					feil.scan();

					if (rc == 42) {

						Trace.warning("(02402051056)Job executor for job " + jid + " returned error = " + rc + ", double execution ignored");
					} else if (rc != 0) {
						String extPid = feil.getExtPid();
						boolean alive = false;
						if (!(extPid.equals(""))) {
							HashMap<String,Long> startTimes = ProcessInfo.getStartTimes(null);
							alive = ProcessInfo.isAlive (extPid, startTimes);
						}
						if (alive)
							feil.setStatus (Feil.STATUS_BROKEN_ACTIVE);
						else {
							feil.setError ("(04301271445) Job executor returned errno = " + rc);
							feil.setStatus (Feil.STATUS_ERROR);
						}
					} else {
						if (!wd_ok) {
							feil.setError ("(03110261111) Cannot create working directory " + workdir.toString());
							feil.setStatus (Feil.STATUS_ERROR);
						} else {
							if (feil.getStatus().equals (Feil.STATUS_STARTED)) {
								feil.setError ("(04302042027) Error launching job executor");
								feil.setStatus (Feil.STATUS_ERROR);
							}
						}
					}
				} catch (final OverlappingFileLockException ofle) {

				} catch (final IOException e) {
					ioe = e;
				} finally {
					feil.close();
				}
			}
		}

		JobServer.server.addJidToBreed(jid);
		Notifier.interrupt(new Long(0));

		if (ioe != null)
			synchronized(feil) {

				ri.notifyError (RepoIface.NONFATAL, "(04302042025) Cannot operate on jobfile " + feil.getFilename() + ": " +
					ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			}
	}
}
