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
import java.util.Vector;
import java.util.regex.Pattern;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;

public class Feil
{
	public static final String STATUS_STARTED         = "STARTED";
	public static final String STATUS_RUNNING         = "RUNNING";
	public static final String STATUS_FINISHED        = "FINISHED";
	public static final String STATUS_BROKEN_ACTIVE   = "BROKEN_ACTIVE";
	public static final String STATUS_BROKEN_FINISHED = "BROKEN_FINISHED";
	public static final String STATUS_ERROR           = "ERROR";
	public static final String STATUS_CHILD_ERROR     = "CHILD_ERROR";

	public static final String ERROR_POSTFIX          = "_ERROR";

	private static final int PREALLOC_SIZE = 16384;
	private static final int CHUNK_SIZE    =  2048;

	private static final Pattern NEWLINE_PATTERN = Pattern.compile (".*[\\n\\r].*", Pattern.DOTALL);

	private static String DEV_NULL = "/dev/null";

	public static final String ID            = "id";
	public static final String STATUS        = "status";
	public static final String COMMAND       = "command";
	public static final String ARGUMENT      = "argument";
	public static final String WORKDIR       = "workdir";
	public static final String USEPATH       = "usepath";
	public static final String VERBOSELOGS   = "verboselogs";
	public static final String LOGFILE       = "logfile";
	public static final String LOGFILEAPPEND = "logfile_append";
	public static final String ERRLOG        = "errlog";
	public static final String ERRLOGAPPEND  = "errlog_append";
	public static final String SAMELOGS      = "samelogs";
	public static final String EXECPID       = "execpid";
	public static final String EXTPID        = "extpid";
	public static final String STATUS_TX     = "status_tx";
	public static final String RETURNCODE    = "returncode";
	public static final String ERROR         = "error";
	public static final String RUN           = "run";
	public static final String INCOMPLETE    = "incomplete";
	public static final String COMPLETE      = "complete";

	private final File filnam;
	private final String jid;

	private RandomAccessFile rfil;

	private String id          = "";
	private String status      = "";
	private String command     = "";
	private Vector args        = new Vector();
	private String workdir     = "";
	private boolean usepath    = false;
	private boolean verboseLogs = false;
	private String logfile     = "";
	private boolean logappend  = false;
	private String errlog      = "";
	private boolean errappend  = false;
	private boolean samelogs   = false;
	private String timestamp   = "";
	private String runningTS   = "";
	private String exec_pid    = "";
	private String ext_pid     = "";
	private String status_tx   = "";
	private String return_code = "";
	private String run         = "";

	private StringBuffer error = new StringBuffer();
	private Charset charset = null;
	private String  charsetName = null;

	private boolean complete   = false;

	public boolean doEmergencyRename = false;

	private void printEncoding(String s)
	{
		int l;
		byte[] buffer = charset.encode(s).array();
		l = buffer.length;
		while (l > 0 && buffer[l-1] == 0) l--;
		System.out.println("charsetName = " + charsetName);
		System.out.println("String (not encoded) ='" + s + "'");
		System.out.print  ("String (    encoded) ='");
		System.out.write(buffer, 0, l);
		System.out.println ("'");
	}

	private void setCharset()
	{
		charsetName = System.getenv("SDMSCHARSET");
		if (charsetName != null) {
			try {
				this.charset = Charset.forName(charsetName);
			} catch (Exception e) {
				this.charset = null;
			}
		}
	}

	public Feil (final File prefix, final String jid)
	{
		setCharset();
		filnam   = new File (prefix.toString() + jid);
		this.jid = jid;
	}

	public Feil (final String taskfileName)
	{
		setCharset();
		filnam   = new File (taskfileName);
		this.jid = "0";
	}

	public File getFilename()
	{
		return filnam;
	}

	public boolean exists()
	{
		return filnam.exists();
	}

	public long length()
		throws IOException
	{
		if (rfil != null)
			return rfil.length();
		Trace.error ("(03210221115) Tried to request filelength on a closed file");
		Trace.error ("Stacktrace :\n" + (new Exception()).getStackTrace());
		return 0L;
	}

	public boolean emergency_rename()
	{
		boolean result = false;
		String oldName = "UNKNOWN";
		String newName = oldName;
		try {
			oldName = filnam.getCanonicalPath();
			newName = oldName + ERROR_POSTFIX;
			final File nf = new File (newName);
			result = filnam.renameTo(nf);
		} catch (IOException ioe) {
			Trace.error("I/O Error occured : " + ioe.toString() + " (" + ioe.getClass().getName() + ")");
		}
		if(!result) {
			Trace.error("Error occured trying to rename " + oldName + " to " + newName);
		}
		return result;
	}

	public final boolean getIncomplete()
	{
		return !complete;
	}
	public final boolean getComplete()
	{
		return complete; 
	}

	public final String getId()
	{
		return id;
	}
	public final String getStatus()
	{
		return status;
	}
	public final String getStatusTimestamp()
	{
		return timestamp;
	}
	public final String getExecPid()
	{
		return exec_pid;
	}
	public final String getExtPid()
	{
		return ext_pid;
	}
	public final String getStatus_Tx()
	{
		return status_tx;
	}
	public final String getReturnCode()
	{
		return return_code;
	}
	public final String getError()
	{
		return error.toString();
	}
	public final String getRunningTimestamp()
	{
		return runningTS;
	}
	public final String getRun()
	{
		return run;
	}

	public final String getCommand()
	{
		return command;
	}
	public final Vector getArgs()
	{
		return args;
	}
	public final String getWorkdir()
	{
		return workdir;
	}
	public final boolean getUsepath()
	{
		return usepath;
	}
	public final boolean getVerboseLogs()
	{
		return verboseLogs;
	}
	public final String getLogfile()
	{
		return logfile;
	}
	public final boolean getLogappend()
	{
		return logappend;
	}
	public final String getErrlog()
	{
		return errlog;
	}
	public final boolean getErrappend()
	{
		return errappend;
	}
	public final boolean getSamelogs()
	{
		return samelogs;
	}

	private final byte[] read()
	{
		try {
			byte[] data = new byte [CHUNK_SIZE];
			rfil.seek (0);
			rfil.read (data);

			if (data [CHUNK_SIZE - 1] != 0) {
				data = new byte [(int) rfil.length()];
				rfil.seek (0);
				rfil.read (data);
			}

			return data;
		}

		catch (final IOException ioe) {
			Trace.error("I/O Exception while reading file " + filnam.toString() + " : " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			return new byte [0];
		}
	}

	private final String println (final String tag)
		throws IOException
	{
		int l;
		final String now = Utils.timestampNow();
		if (charset != null) {
			byte[] buffer = charset.encode(now + " " + tag + "\n").array();
			l = buffer.length;
			while (l > 0 && buffer[l-1] == 0) l--;
			rfil.write(buffer, 0, l);
		} else {
			rfil.writeBytes(now + " " + tag + "\n");
		}
		return now.substring (1, now.indexOf (Utils.TIMESTAMP_LEADOUT));
	}

	private final String println (final String id, final String val)
		throws IOException
	{
		if (val != null) {
			final StringBuffer tag = new StringBuffer (id);
			if (NEWLINE_PATTERN.matcher (val).matches()) {
				tag.append ("'");
				if (charset != null) {
					byte[] buffer = charset.encode(val).array();
					int l = buffer.length;
					while (l > 0 && buffer[l-1] == 0) l--;
					tag.append(l);

				} else
				tag.append (val.length());
			}
			tag.append ("=");
			tag.append (val);

			return println (tag.toString());
		}

		return "";
	}

	public final String append (final String id, final String val)
		throws IOException
	{
		final byte[] data = read();

		int ofs = 0;
		while ((ofs < data.length) && (data [ofs] != 0))
			++ofs;

		rfil.seek (ofs);

		return println (id, val);
	}

	public final void create (final Descr jd, final boolean use_path, final boolean verbose_logs)
		throws IOException
	{
		boolean success = true;

		rfil = new RandomAccessFile (filnam, "rws");
		rfil.setLength (0);

		success &= filnam.setReadable(false, false);  
		success &= filnam.setWritable(false, false); 
		success &= filnam.setExecutable(false, false);
 
		success &= filnam.setReadable(true, true);
		success &= filnam.setWritable(true, true);

		if (!success) {
			Trace.warning("Failed to set file permissions on taskfile");
		}

		try {
			id     = jd.id;
			run    = jd.run;
			status = STATUS_STARTED;

			println (INCOMPLETE);

			println (ID, jd.id);

			println (RUN, jd.run);

			timestamp = println (STATUS, status);

			println (COMMAND, jd.cmd);

			for (int i = 0; i < jd.args.length; ++i)
				println (ARGUMENT, jd.args [i]);

			println (WORKDIR, jd.dir.toString());

			if (use_path)
				println (USEPATH);

			if (verbose_logs)
				println (VERBOSELOGS);

			if (jd.log != null) {
				println (LOGFILE, jd.log.toString());
				if (jd.logapp)
					println (LOGFILEAPPEND);
			}

			if (jd.elog != null) {
				println (ERRLOG, jd.elog.toString());
				if (jd.elogapp)
					println (ERRLOGAPPEND);
			}

			if (jd.samelog)
				println (SAMELOGS);

			println (COMPLETE);

			rfil.write (new byte [Math.max (0, PREALLOC_SIZE - (int) length())]);

			rfil.getFD().sync();
		} catch (SyncFailedException sfe) {
			Trace.error ("(03210150807) Sync() failed on jobfile " + getFilename() + ": " + sfe.getMessage());
			throw sfe;
		} catch (IOException ioe) {
			Trace.error("I/O Exception while creating file " + filnam.getCanonicalPath() + " : " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
			throw ioe;
		}

		finally {
			close();
		}
	}

	public final void remove()
	{
		close();

		if (! filnam.delete())
			Trace.error ("(04504090126) Error deleting " + filnam);
	}

	public final void open()
		throws IOException
	{
		rfil = new RandomAccessFile (filnam, "rws");

		try {
			final FileChannel fchnl = rfil.getChannel();
			fchnl.lock();
		} catch (OverlappingFileLockException ofle) {
			Trace.warning("(03210221131) Overlapping File Lock Exception on File " + filnam + " ignored");
		}
	}

	public final void close()
	{
		try {
			if (rfil != null && rfil.getChannel().isOpen())
				rfil.close();
			else {
				if (rfil != null)
					Trace.error ("(03210221111) Duplicate close on file " + filnam);
			}
			rfil = null;
		}

		catch (final IOException ioe) {
			Trace.error ("(04504090121) Error closing " + filnam + ": " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
		}
	}

	public final void scan()
	{
		error.setLength (0);

		complete    = false;

		final String data = new String (read());

		int size = data.length();
		while ((size > 0) && (data.charAt (size - 1) == 0))
			--size;

		int pos = 0;
		while (pos < size) {
			String key   = null;
			String value = null;

			pos = data.indexOf (Utils.TIMESTAMP_LEADOUT, pos);
			if (pos == -1)
				break;

			final String ts = data.substring (data.lastIndexOf (Utils.TIMESTAMP_LEADIN, pos) + 1, pos);

			do ++pos;
			while ((pos < size) && (data.charAt (pos) == ' '));
			if (pos >= size)
				break;
			int key_start = pos;

			do ++pos;
			while ((pos < size) && ("=\n\r".indexOf (data.charAt (pos)) == -1));
			if (pos >= size)
				break;
			key = data.substring (key_start, pos);

			if (data.charAt (pos) != '=')
				value = null;
			else {
				++pos;
				final int value_start = pos;

				final int qu = key.indexOf ('\'');
				if (qu == -1)
					do ++pos;
					while ((pos < size) && ("\n\r".indexOf (data.charAt (pos)) == -1));
				else {
					try {
						final int value_len = Integer.parseInt (key.substring (qu + 1));
						key = key.substring (0, qu);
						pos += value_len;
					}

					catch (final NumberFormatException nfe) {
						do ++pos;
						while ((pos < size) && ("\n\r".indexOf (data.charAt (pos)) == -1));
					}
				}

				value = data.substring (value_start, pos);
			}

			++pos;

			if      (key.equals (ID))            id          = value;
			else if (key.equals (RUN))           run         = value;
			else if (key.equals (EXECPID))       exec_pid    = value;
			else if (key.equals (EXTPID))        ext_pid     = value;
			else if (key.equals (STATUS_TX))     status_tx   = value;
			else if (key.equals (RETURNCODE))    return_code = value;

			else if (key.equals (INCOMPLETE))    complete = false;
			else if (key.equals (COMPLETE))      complete = true;
			else if (key.equals (COMMAND))       command = value;
			else if (key.equals (ARGUMENT))      args.add(value);
			else if (key.equals (WORKDIR))       workdir = value;
			else if (key.equals (USEPATH))       usepath = true;
			else if (key.equals (VERBOSELOGS))   verboseLogs = true;
			else if (key.equals (LOGFILE))       logfile = value;
			else if (key.equals (ERRLOG))        errlog = value;
			else if (key.equals (ERRLOGAPPEND))  errappend = true;
			else if (key.equals (LOGFILEAPPEND)) logappend = true;
			else if (key.equals (SAMELOGS))      samelogs = true;
			else if (key.equals (STATUS)) {
				status = value;
				timestamp = ts;

				if (status.equals (STATUS_RUNNING))
					runningTS = timestamp;
			}

			else if (key.equals (ERROR)) {
				if (error.length() != 0)
					error.append ("\n");
				error.append (value);
			}
		}

		if (id.equals (""))
			id = jid;

		if (logfile.equals("")) {
			logfile = DEV_NULL;
		}
		if (errlog.equals("")) {
			logfile = DEV_NULL;
		}
	}

	public final void setStatus (final String new_status)
		throws IOException
	{
		status = new_status;
		timestamp = append (STATUS, new_status);
		if (new_status.equals (STATUS_RUNNING))
			runningTS = timestamp;
	}

	public final void setStatus_Tx (final String new_status)
		throws IOException
	{
		status_tx = new_status;
		append (STATUS_TX, new_status);
	}

	public final void setError (final String new_error)
		throws IOException
	{
		error.setLength (0);
		error.append (new_error);
		append (ERROR, new_error);
	}
}
