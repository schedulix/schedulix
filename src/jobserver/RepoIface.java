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
import java.util.*;
import javax.net.ssl.*;

import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.ScopeConfig;
import de.independit.scheduler.server.dump.Dump;

public class RepoIface
{
	public static final int NOP             = 0;
	public static final int START_JOB       = 1;
	public static final int SHUTDOWN_SERVER = 2;
	public static final int DISPOSE_JOB     = 3;

	public static boolean FATAL    = true;
	public static boolean NONFATAL = false;

	public static final String JS_ALREADY_CONNECTED = "Server already connected";

	public static final String COMMAND             = "COMMAND";
	public static final String CMD_NOP             = "NOP";
	public static final String CMD_SHUTDOWN        = "SHUTDOWN";
	public static final String CMD_STARTJOB        = "STARTJOB";
	public static final String     STARTJOB_ID     = "ID";
	public static final String     STARTJOB_RUN    = "RUN";
	public static final String     STARTJOB_CMD    = "CMD";
	public static final String     STARTJOB_ARGS   = "ARGS";
	public static final String     STARTJOB_DIR    = "DIR";
	public static final String     STARTJOB_LOG    = "LOG";
	public static final String     STARTJOB_LOGAPP = "LOGAPP";
	public static final String     STARTJOB_ERR    = "ERR";
	public static final String     STARTJOB_ERRAPP = "ERRAPP";
	public static final String     STARTJOB_ENV    = "ENV";
	public static final String CMD_ALTER           = "ALTER";
	public static final String     ALTER_CONFIG    = "CONFIG";
	public static final String CMD_DISPOSE         = "DISPOSE";

	public static final int SO_TIMEOUT = 240000;

	private static final String[] REQUIRED_CONFIG     = {Config.REPO_USER, Config.REPO_PASS, Config.DEFAULT_WORKDIR, Config.JOB_EXECUTOR, Config.JOB_FILE_PREFIX};
	private static final String[] REPO_HOST_REPO_PORT = {Config.REPO_HOST, Config.REPO_PORT};

	private String currentHost;
	public final synchronized String getHost()
	{
		return currentHost;
	}
	private int    currentPort;
	public final synchronized int getPort()
	{
		return currentPort;
	}

	private final String mypid = Utils.getMyPid();

	private final Config cfg;

	private Socket repoSock = null;
	private BufferedOutputStream repoOut = null;
	private ObjectInputStream repoInp = null;

	private boolean request_reconnect = false;

	private Descr jobData;

	private boolean isConnected = false;

	public final boolean isConnected()
	{
		return isConnected;
	}

	private final SDMSOutput sdmsExec (final String cmd)
	{
		while (true) {
			if (!isConnected)
				openConnection();
			Trace.debug ("> " + cmd);

			TimeoutThread timeoutThread = new TimeoutThread(5*60);
			try {

				timeoutThread.setExecuting(true);
				timeoutThread.start();

				repoOut.write (cmd.getBytes());
				repoOut.write ('\n');
				repoOut.flush();
				repoInp = new ObjectInputStream (repoSock.getInputStream());

				final SDMSOutput retval = (SDMSOutput) repoInp.readObject();

				Trace.debug ("< " + Trace.dump (retval));

				timeoutThread.setExecuting(false);

				timeoutThread.interrupt();

				return retval;
			} catch (final IOException ioe) {
				timeoutThread.setExecuting(false);
				timeoutThread.interrupt();
				Trace.error ("(04504092058) Error executing command (" + cmd + ")" + ": " + ioe.getMessage()  + " (" + ioe.getClass().getName() + ")");
				closeConnection();
				openConnection();
			} catch (final ClassNotFoundException cnfe) {
				Utils.abortProgram ("(04301271453) Cannot execute command (" + cmd + ")" + ": " + cnfe.getMessage() + " (" + cnfe.getClass().getName() + ")");
			}
		}
	}

	private final boolean evaluateConfig (final SDMSOutputContainer cont)
	{
		final HashSet allKeys = new HashSet();
		HashSet dynamic = null;

		boolean good = true;

		cfg.remove(Config.NAME_PATTERN);

		if (cont != null) {
			final Vector desc = cont.desc;
			final Vector data = cont.dataset;

			final int size = desc.size();
			for (int i = 0; i < size; ++i) {
				final String key = ((SDMSOutputLabel) desc.get (i)).name;
				if (ScopeConfig.isInternalEntry (key))
					continue;

				final Object value = ((Vector) data.get (0)).get (i);

				if (key.equals (Config.DYNAMIC))
					dynamic = (HashSet) value;
				else {
					allKeys.add (key);

					try {
						if (key.equals (Config.REPO_PASS))
							continue;

						final Object oldValue = cfg.put (key, value);

						if (Utils.isOneOf (key, REPO_HOST_REPO_PORT)
						    && ! value.toString().equals (oldValue.toString()))
							request_reconnect = true;
					} catch (final IllegalArgumentException iae) {
						notifyError (NONFATAL, "(04305141859) Invalid config entry: " + key + "=" + value + ": " + iae.getMessage());
						good = false;
					}
				}
			}
		}

		for (int i = 0; i < REQUIRED_CONFIG.length; ++i)
			if (! allKeys.contains (REQUIRED_CONFIG [i])) {
				notifyError (NONFATAL, "(04305141920) Missing required config entry: " + REQUIRED_CONFIG [i]);
				good = false;
			}

		if (good) {
			final Vector cfgKeys = new Vector (cfg.keySet());
			final int size = cfgKeys.size();
			for (int i = 0; i < size; ++i) {
				final String key = (String) cfgKeys.get (i);
				if (! (allKeys.contains (key) || Utils.isOneOf (key, REPO_HOST_REPO_PORT)))
					if (! key.equals(Config.NAME_PATTERN))
						cfg.remove (key);
			}
		}

		if (good && (dynamic != null) && ! dynamic.isEmpty()) {
			final Vector dynList = new Vector (dynamic.size());
			final Vector keyList = new Vector (dynamic);
			final int size = keyList.size();
			for (int i = 0; i < size; ++i) {
				final String key = (String) keyList.get (i);
				final String value = (String) Environment.getSystemEnvironment().get (key);

				dynList.add (Dump.quotedString (key) + "=" + Dump.quotedString (value == null ? "" : value));
			}

			final SDMSOutput res = sdmsExec ("alter jobserver with dynamic parameters = (" + Dump.join (", ", dynList) + ");");
			if (res.error != null)
				Utils.abortProgram (this, "(04307160007) Unexpected response: (" + res.error.code + ") " + res.error.message);
		}

		WakeupThread wt = WakeupThread.getInstance(cfg);
		wt.interrupt();

		return good;
	}

	private final boolean connect (final String host, final int port, final boolean use_ssl)
	{
		currentHost = host;
		currentPort = port;

		final String repoUser = (String) cfg.get (Config.REPO_USER);
		final String repoPass = (String) cfg.get (Config.REPO_PASS);

		Trace.debug ("Trying " + repoUser + "/" + "********" + "@" + currentHost + ":" + currentPort + (use_ssl ? " (ssl) " : "") + "...");

		request_reconnect = false;

		try {
			repoSock = new Socket(InetAddress.getByName(currentHost), currentPort);
		} catch (final UnknownHostException uhe) {
			Trace.error ("(04301271454) " + currentHost + ": Host unknown");
			isConnected = false;
			return false;
		} catch (final IOException ioe) {
			Trace.error ("(04301271455) " + currentHost + ":" + currentPort + ": " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			isConnected = false;
			return false;
		}

		try {
			repoSock.setSoTimeout(SO_TIMEOUT);
		} catch (SocketException se) {
			Trace.error ("(03407161423) Cannot set Timeout on socket: " + se.getMessage() + " (" + se.getClass().getName() + ")");
			try {
				repoSock.close();
			} catch (IOException ioe ) {
				// ignore
			}
			isConnected = false;
			return false;
		}

		try {
			repoOut = new BufferedOutputStream (repoSock.getOutputStream());
		} catch (final IOException ioe) {
			Trace.error ("(04301271456) Cannot establish socket output stream: " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			isConnected = false;
			return false;
		}
		isConnected = true;

		SDMSOutput res;

		res = sdmsExec ("connect jobserver " + repoUser + " identified by '" + Utils.quoted (repoPass) + "' with protocol = serial, session = '" + JobServer.session_info + "';");
		if (res.error != null) {
			if (res.error.message.equals (JS_ALREADY_CONNECTED))
				Utils.abortProgram ("(04301271457) Cannot connect: (" + res.error.code + ") " + res.error.message);

			Trace.error ("(04301271458) Cannot connect: (" + res.error.code + ") " + res.error.message);
			isConnected = false;
			return false;
		}

		res = sdmsExec ("register with pid = '" + Utils.quoted (mypid) + "';");
		if (res.error != null) {
			Trace.error ("(04301271459) Cannot register: (" + res.error.code + ") " + res.error.message);
			isConnected = false;
			return false;
		}

		return evaluateConfig (res.container);
	}

	private final void openConnection()
	{
		boolean failed = false;

		while (true) {

			Boolean useSSL;
			useSSL = Boolean.FALSE;
			if (connect (((String) cfg.get (Config.REPO_HOST)),
				     ((Long) cfg.get (Config.REPO_PORT)).intValue(),
				     useSSL.booleanValue()
				    )
			   )
				if (! request_reconnect)
					break;

			if (! request_reconnect)
				failed = true;

			closeConnection();
			if (! request_reconnect)
				Utils.sleep (((Long) cfg.get (Config.RECONNECT_DELAY)).longValue());
		}

		if (failed)
			Trace.info ("(04305151959) Connected!");
	}

	private final void closeConnection()
	{
		if (repoOut != null) {
			try {
				repoOut.close();
			}

			catch (final IOException ioe) {
				Trace.error ("(04504092249) Error closing output stream: " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			}

			repoOut = null;
		}
		if (repoInp != null) {
			try {
				repoInp.close();
			} catch (final IOException ioe) {
				Trace.error ("(03004261219) Error closing input stream: " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			}
			repoInp = null;
		}

		if (repoSock != null) {
			try {
				repoSock.close();
			}

			catch (final IOException ioe) {
				Trace.error ("(04301271500) Error closing socket: " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			}

			repoSock = null;
		}
		isConnected = false;
	}

	public RepoIface (final Config cfg)
	{
		this.cfg = cfg;

		openConnection();
	}

	private static final Object getByName (final SDMSOutputContainer cont, final String colnam)
	{
		final int index = cont.indexForName (null, colnam);
		final Vector vec = (Vector) cont.dataset.get (0);
		final Object res = vec.get (index);
		return res;
	}

	private static final boolean isSameFile (final File file1, final File file2)
	{
		if ((file1 == null) || (file2 == null))
			return false;

		try {
			return file1.getCanonicalFile().equals (file2.getCanonicalFile());
		}

		catch (final IOException ioe) {
			return file1.getAbsoluteFile().equals (file2.getAbsoluteFile());
		}
	}

	private final Descr collectJobData (final SDMSOutput res)
	{
		final String job_cmd = (String) getByName (res.container, STARTJOB_CMD);
		if (job_cmd == null || job_cmd.equals(""))
			Utils.abortProgram (this, "(03310171401) Invalid run command");

		final File job_log  = getFile (res, STARTJOB_LOG);
		final File job_elog = getFile (res, STARTJOB_ERR);

		final File dir = getFile (res, STARTJOB_DIR);
		final File job_dir = dir != null ? dir : (File) cfg.get (Config.DEFAULT_WORKDIR);

		Object o;
		Vector v;

		o = getByName (res.container, STARTJOB_ID);
		if (o == null)
			Utils.abortProgram (this, "(04301271501) No job id received");
		final long job_id = ((Long) o).longValue();

		o = getByName (res.container, STARTJOB_RUN);
		final int job_run = o == null ? 0 : ((Integer) o).intValue();

		v = (Vector) getByName (res.container, STARTJOB_ARGS);
		final String[] job_args = new String [v.size()];
		v.toArray (job_args);

		boolean job_logapp = false;
		if (job_log != null) {
			o = getByName (res.container, STARTJOB_LOGAPP);
			if (o != null)
				job_logapp = ((Boolean) o).booleanValue();
			else
				job_logapp = false;
		}

		boolean job_elogapp = false;
		if (job_elog != null) {
			o = getByName (res.container, STARTJOB_ERRAPP);
			if (o != null)
				job_elogapp = ((Boolean) o).booleanValue();
			else
				job_elogapp = false;
		}

		final boolean job_same = isSameFile (job_log, job_elog);

		Environment job_env = null;
		v = (Vector) getByName (res.container, STARTJOB_ENV);
		if (v != null) {
			if ((v.size() % 2) != 0)
				Utils.abortProgram (this, "(04301271502) Invalid environment received: " + v);
			job_env = new Environment (v);
		}

		return new Descr (job_id, job_run, job_cmd, job_args, job_dir, job_log, job_logapp, job_elog, job_elogapp, job_same, job_env);
	}

	private static final File getFile (final SDMSOutput res, final String which)
	{
		final Object value = getByName (res.container, which);
		return value == null ? null : new File ((String) value);
	}

	public final synchronized int getNextCmd()
	{
		while (true) {
			final SDMSOutput res = sdmsExec ("get next job;");
			final boolean onlineServer = ((Boolean) cfg.get (Config.ONLINE_SERVER)).booleanValue();
			if (res.error != null) {
				if (!onlineServer)
					closeConnection();
				return NOP;
			}

			final String cmdstr = (String) getByName (res.container, COMMAND);

			if (cmdstr.equals (CMD_NOP)) {
				if (!onlineServer)
					closeConnection();
				return NOP;
			}

			if (cmdstr.equals (CMD_STARTJOB)) {
				jobData = collectJobData (res);
				return START_JOB;
			}

			if (cmdstr.equals (CMD_SHUTDOWN))
				return SHUTDOWN_SERVER;

			if (cmdstr.equals (CMD_ALTER)) {
				evaluateConfig ((SDMSOutputContainer) getByName (res.container, ALTER_CONFIG));
				if (request_reconnect) {
					closeConnection();
					openConnection();
				}
				continue;
			}

			notifyError (NONFATAL, "(04301271503) Unknown/unexpected command received: " + cmdstr);
		}
	}

	public final synchronized Descr getJobData()
	{
		return jobData;
	}

	public final synchronized int reassureJob (final String jid)
	{
		final SDMSOutput res = sdmsExec ("reassure " + jid + ";");
		if (res.error != null)
			Utils.abortProgram (this, "(04301271504) Error received: (" + res.error.code + ") " + res.error.message);
		final String cmdstr = (String) getByName (res.container, COMMAND);

		int cmd = NOP;
		if      (cmdstr.equals (CMD_STARTJOB)) {
			jobData = collectJobData (res);
			cmd = START_JOB;
		} else if (cmdstr.equals (CMD_DISPOSE))  cmd = DISPOSE_JOB;
		else if (cmdstr.equals (CMD_SHUTDOWN)) cmd = SHUTDOWN_SERVER;
		else
			Utils.abortProgram (this, "(04301271505) Unknown/unexpected command received: " + cmdstr);

		return cmd;
	}

	public final synchronized void notifyError (final boolean fatal, final String msg)
	{
		if (fatal)
			Trace.fatal (msg);
		else
			Trace.error (msg);

		final SDMSOutput res = sdmsExec ("alter jobserver with " + (fatal ? "" : "non_") + "fatal error_text = '" + Utils.quoted (msg) + "';");
		if (res.error != null && ! fatal)
			Utils.abortProgram (this, "(04301271506) Unexpected response: (" + res.error.code + ") " + res.error.message);
	}

	public final synchronized void reportState (final Feil feil)
	{
		final StringBuffer cmd = new StringBuffer();
		SDMSOutput res;

		String status = feil.getStatus();
		if (status.equals (Feil.STATUS_CHILD_ERROR))
			status = Feil.STATUS_ERROR;

		final String jobId     = feil.getId();
		final String jobRun    = feil.getRun();
		final String execPid   = feil.getExecPid();
		final String extPid    = feil.getExtPid();
		final String runningTS = feil.getRunningTimestamp();

		if (! (status.equals (Feil.STATUS_STARTED) || status.equals (Feil.STATUS_RUNNING)
		       || feil.getStatus_Tx().equals (Feil.STATUS_RUNNING) || feil.getStatus_Tx().equals (Feil.STATUS_BROKEN_ACTIVE))) {
			cmd.append ("alter job ");
			cmd.append (jobId);
			cmd.append (" with status = ");
			cmd.append (Feil.STATUS_RUNNING.toLowerCase());

			if (! jobRun.equals ("")) {
				cmd.append (", run = ");
				cmd.append (jobRun);
			}

			if (! execPid.equals ("")) {
				cmd.append (", exec_pid = '");
				cmd.append (Utils.quoted (execPid));
				cmd.append ("'");
			}

			if (! extPid.equals ("")) {
				cmd.append (", ext_pid = '");
				cmd.append (Utils.quoted (extPid));
				cmd.append ("'");
			}

			cmd.append (", timestamp = '");
			cmd.append (Utils.quoted (runningTS.equals ("") ? feil.getStatusTimestamp() : runningTS));
			cmd.append ("';");

			res = sdmsExec (cmd.toString());
			if (res.error != null)
				Utils.abortProgram (this, "(04307141941) Unexpected response: (" + res.error.code + ") " + res.error.message);

			cmd.setLength (0);
		}

		cmd.append ("alter job ");
		cmd.append (jobId);
		cmd.append (" with status = ");
		cmd.append (status.toLowerCase());

		if (! jobRun.equals ("")) {
			cmd.append (", run = ");
			cmd.append (jobRun);
		}

		if (status.equals (Feil.STATUS_RUNNING)) {
			if (! execPid.equals ("")) {
				cmd.append (", exec_pid = '");
				cmd.append (Utils.quoted (execPid));
				cmd.append ("'");
			}

			if (! extPid.equals ("")) {
				cmd.append (", ext_pid = '");
				cmd.append (Utils.quoted (extPid));
				cmd.append ("'");
			}
		}

		else if (status.equals (Feil.STATUS_FINISHED)) {
			if (! feil.getReturnCode().equals ("")) {
				cmd.append (", exit_code = ");
				cmd.append (feil.getReturnCode());
			}
		}

		else if (status.equals (Feil.STATUS_ERROR))
			if (! feil.getError().equals ("")) {
				cmd.append (", error_text = '");
				cmd.append (Utils.quoted (feil.getError()));
				cmd.append ("'");
			}

		cmd.append (", timestamp = '");
		cmd.append (Utils.quoted (feil.getStatusTimestamp()));
		cmd.append ("';");

		res = sdmsExec (cmd.toString());
		if (res.error != null) {
			if (res.error.code.equals("03110251037")) {

				Trace.error("Server responded with a not found error for job " + jobId);
				Trace.error("The issued command was:");
				Trace.error(cmd.toString());
				if(!feil.emergency_rename()) {

				}
			} else {
				Utils.abortProgram (this, "(04301271507) Unexpected response: (" + res.error.code + ") " + res.error.message);
			}
		}
	}
}

class TimeoutThread extends Thread
{

	volatile boolean executing;
	long timeout;

	TimeoutThread(long timeout)
	{
		this.timeout = timeout * 1000;
		executing = false;
	}

	public void setExecuting(boolean executing)
	{
		this.executing = executing;
	}

	public void run()
	{
		long startTime = System.currentTimeMillis();
		long timeoutLeft = timeout;

		while (executing) {
			try {
				sleep(timeoutLeft);
			} catch (InterruptedException ie) {

				long now = System.currentTimeMillis();
				if (executing && (now - startTime < timeout)) {
					timeoutLeft = timeout - (now - startTime);
					continue;
				}
			}

			if (executing) {
				Utils.abortProgram ("(04407171144) Timeout on Command execution");
			}

		}
	}
}
