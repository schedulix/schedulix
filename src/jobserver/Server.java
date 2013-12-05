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
import java.nio.channels.*;

import de.independit.scheduler.server.exception.CommonErrorException;

public class Server
{
	public static final String __version = "@(#) $Id: Server.java,v 2.19.2.1 2013/03/14 10:24:07 ronald Exp $";

	private static final String[] FINAL_STATES = {Feil.STATUS_FINISHED, Feil.STATUS_BROKEN_FINISHED, Feil.STATUS_ERROR, Feil.STATUS_CHILD_ERROR};

	private final Config cfg;
	private final RepoIface ri;
	private WakeupThread wecker = null;
	private HttpThread httpserver = null;

	private static HashMap<String, Feil> feilMap = null;

	public Server (final String config_filnam, final String information)
	throws CommonErrorException
	{
		cfg = new Config (config_filnam);
		ri  = new RepoIface (cfg, information);
	}

	public static final String getVersionInfo()
	{
		return "Jobserver (server) " + Utils.getVersion() + "\n"
		       + Utils.getCopyright() + " " + Utils.getCompany()  + "\n"
		       + "All rights reserved";
	}

	public static final synchronized Feil getFeil(Config cfg, String jid)
	{
		final Feil f;
		if (feilMap == null) {
			feilMap = new HashMap<String, Feil>();
		}
		if (feilMap.containsKey(jid))
			f = feilMap.get(jid);
		else {
			f = new Feil ((File) cfg.get (Config.JOB_FILE_PREFIX), jid);
			feilMap.put(jid, f);
		}
		return f;
	}

	public static final synchronized void removeFeil(String jid)
	{
		Feil f;
		if (feilMap.containsKey(jid))
			f = feilMap.get(jid);
		else
			return;

		f.remove();
		feilMap.remove(jid);
	}

	public static final synchronized boolean feilExists(String jid)
	{

		return feilMap.containsKey(jid);
	}

	private final String[] getJobFileIds()
	{
		final File job_file_prefix = (File) cfg.get (Config.JOB_FILE_PREFIX);

		final File job_file_dir = job_file_prefix.getParentFile();

		final String fnam_prefix = job_file_prefix.getName();
		final int fnam_prefix_len = fnam_prefix.length();

		final String list[] = job_file_dir.list();
		final int len = list != null ? list.length : 0;

		int num = 0;
		for (int i = 0; i < len; ++i)
			if (list [i].startsWith (fnam_prefix) && !list [i].endsWith (Feil.ERROR_POSTFIX)) {
				++num;
			}

		final String found[] = new String [num];
		int f = 0;
		for (int i = 0; i < len; ++i)
			if (list [i].startsWith (fnam_prefix) && !list [i].endsWith (Feil.ERROR_POSTFIX)) {
				found [f++] = list [i].substring (fnam_prefix_len);
				if (f == num)
					break;
			}

		return found;
	}

	private final boolean doRestart ()
	{
		HashMap<String,Long> startTimes = ProcessInfo.getStartTimes();
		final String jid[] = getJobFileIds();
		for (int i = 0; i < jid.length; ++i) {
			final Feil feil = Server.getFeil(cfg, jid[i]);
			synchronized(feil) {
				try {
					feil.open();
				}

				catch (final IOException ioe) {
					feil.close();
					ri.notifyError (RepoIface.NONFATAL,
					                "(04301271508) Open() failed on jobfile " + feil.getFilename() + ": " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
					continue;
				}

				feil.scan();

				if (feil.getIncomplete() && ! feil.getComplete()) {
					Trace.warning ("Cleaning up incomplete task file " + feil.getFilename());
					Server.removeFeil(jid[i]);
					continue;
				}

				if ((! feil.getStatus().equals (Feil.STATUS_STARTED))
				    || (((feil.getExecPid() != null) && (! feil.getExecPid().equals (""))) && ProcessInfo.isAlive (feil.getExecPid(), startTimes))) {
					feil.close();
					continue;
				}

				switch (ri.reassureJob (feil.getId())) {
				case RepoIface.START_JOB:
					feil.close();
					new EiThread (ri, cfg, jid [i]).start();
					break;

				case RepoIface.DISPOSE_JOB:
					Server.removeFeil(jid[i]);
					break;

				case RepoIface.SHUTDOWN_SERVER:
					feil.close();
					return false;

				default:
					Utils.abortProgram (ri, "(04301271509) Internal error");
				}
			}
		}

		return true;
	}

	private final void breed()
	{
		HashMap<String,Long> startTimes = ProcessInfo.getStartTimes();
		final String jid[] = getJobFileIds();
		for (int i = 0; i < jid.length; ++i) {
			breed(jid[i], startTimes);
		}
	}

	private final void breed(String jid, HashMap<String,Long> startTimes)
	{
		final Feil feil = Server.getFeil(cfg, jid);
		boolean feil_expired = false;
		IOException saveIOE = null;

		synchronized(feil) {
			try {
				feil.open();
			} catch (final IOException ioe) {
				ri.notifyError (RepoIface.NONFATAL, "(03210150800) Cannot open on jobfile " + feil.getFilename() + ": " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
				saveIOE = ioe;
			}
			try {
				if (saveIOE != null) throw saveIOE;

				if (feil.length() == 0) {

					feil_expired = true;
				} else {
					feil.scan();

					if (feil.getStatus().equals (Feil.STATUS_RUNNING)) {
						if (! ProcessInfo.isAlive (feil.getExecPid(), startTimes))
							if (ProcessInfo.isAlive (feil.getExtPid(), startTimes))
								feil.setStatus (Feil.STATUS_BROKEN_ACTIVE);
							else
								feil.setStatus (Feil.STATUS_BROKEN_FINISHED);
					} else if (feil.getStatus().equals (Feil.STATUS_BROKEN_ACTIVE))
						if (! ProcessInfo.isAlive (feil.getExtPid(), startTimes))
							feil.setStatus (Feil.STATUS_BROKEN_FINISHED);

					if (! feil.getStatus().equals (feil.getStatus_Tx())) {
						feil.close();
						ri.reportState (feil);
						feil.open();

						feil.setStatus_Tx (feil.getStatus());
					}

					if (Utils.isOneOf (feil.getStatus_Tx(), FINAL_STATES))
						feil_expired = true;
				}
			} catch (final EOFException ioe) {
				ri.notifyError (RepoIface.NONFATAL, "(03210150802) Unexpected End of File on jobfile " + feil.getFilename());
			}

			catch (final IOException ioe) {
				ri.notifyError (RepoIface.NONFATAL, "(04302041618) Cannot operate on jobfile " + feil.getFilename() + ": " + ioe.getMessage()  + " (" + ioe.getClass().getName() + ")");
			}

			finally {
				if (feil_expired)
					Server.removeFeil(jid);
				else
					feil.close();
			}
		}
	}

	private int errorCount = 0;

	private final void createNewEi (final Descr jd)
	{
		final Feil feil = Server.getFeil(cfg, jd.id);
		synchronized(feil) {
			if (feil.exists())
				Utils.abortProgram (ri, "(04301271511) Job id " + jd.id + " already in process");

			try {
				feil.create (jd, ((Boolean) cfg.get (Config.USE_PATH)).booleanValue(), ((Boolean) cfg.get (Config.VERBOSE_LOGS)).booleanValue());
			}

			catch (final IOException ioe) {
				Server.removeFeil(jd.id);
				ri.notifyError (RepoIface.NONFATAL, "(04301271512) Cannot create job file " + feil.getFilename() + ": " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
				++errorCount;

				Utils.sleep (errorCount * ((Long) cfg.get (Config.NOP_DELAY)).longValue());

				return;
			}
		}
		final EiThread ei = new EiThread (ri, cfg, jd.id, jd.env, jd.dir);
		ei.start();

		errorCount = 0;
	}

	private final void startWakeupThread(final Config cfg)
	{
		wecker = WakeupThread.getInstance(cfg);
		if (! wecker.isAlive())
			wecker.start();
	}

	private final void startHttpThread(final Config cfg)
	{
		httpserver = HttpThread.getInstance(cfg);
		if (! httpserver.isAlive())
			httpserver.start();
	}

	public final void runServer ()
	{
		int status = 0;
		final Long id = new Long(0);
		Thread currentThread = Thread.currentThread();
		startWakeupThread(cfg);
		startHttpThread(cfg);

		final int loop_delay = 5000;

		boolean active = doRestart ();
		long ts = 0;
		while (active) {
			breed();
			long now = new Date().getTime();
			if (now - ts > ((Long) cfg.get (Config.NOP_DELAY)).longValue()) {
				switch (ri.getNextCmd()) {
				case RepoIface.NOP:
					ts = now;
					break;

				case RepoIface.START_JOB:
					createNewEi (ri.getJobData());
					break;

				case RepoIface.SHUTDOWN_SERVER:
					active = false;
					break;
				default:
					Utils.abortProgram (ri, "(04504112210) Unexpected response");
				}
			}
			if (ts > 0) {
				try {
					Notifier.register(id, currentThread);
					Thread.sleep (loop_delay);
					Notifier.unregister(id);
				} catch (Exception e) {
					ts = 0;
				}
			}
		}
	}
}

