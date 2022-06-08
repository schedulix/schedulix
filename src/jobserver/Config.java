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
import java.util.*;

import de.independit.scheduler.server.exception.CommonErrorException;
import de.independit.scheduler.server.parser.ScopeConfig;

public class Config
	extends HashMap
{
	public static final String __version = "@(#) $Id: Config.java,v 2.16.4.1 2013/03/14 10:24:05 ronald Exp $";

	public static final String REPO_HOST       = "REPOHOST";
	public static final String REPO_PORT       = "REPOPORT";
	public static final String REPO_USER       = "REPOUSER";
	public static final String REPO_PASS       = "REPOPASS";

	public static final String RECONNECT_DELAY = "RECONNECTDELAY";
	public static final String DEFAULT_WORKDIR = "DEFAULTWORKDIR";
	public static final String USE_PATH        = "USEPATH";
	public static final String VERBOSE_LOGS    = "VERBOSELOGS";
	public static final String TRACE_LEVEL     = "TRACELEVEL";
	public static final String BOOTTIME        = "BOOTTIME";
	public static final String NOP_DELAY       = "NOPDELAY";
	public static final String JOB_EXECUTOR    = "JOBEXECUTOR";
	public static final String JOB_FILE_PREFIX = "JOBFILEPREFIX";
	public static final String ENV_MAPPING     = "ENV";
	public static final String DYNAMIC         = "DYNAMIC";
	public static final String NOTIFY_PORT     = "NOTIFYPORT";
	public static final String HTTP_PORT       = "HTTPPORT";
	public static final String HTTP_HOST       = "HTTPHOST";
	public static final String HTTP_LOGENCODING= "HTTPLOGENCODING";
	public static final String HTTP_INTERFACE  = "HTTP_INTERFACE";
	public static final String ONLINE_SERVER   = "ONLINE_SERVER";
	public static final String NAME_PATTERN    = "NAME_PATTERN_";
	public static final String KEYSTORE        = "KEYSTORE";
	public static final String TRUSTSTORE      = "TRUSTSTORE";
	public static final String KEYSTOREPW      = "KEYSTOREPASSWORD";
	public static final String TRUSTSTOREPW    = "TRUSTSTOREPASSWORD";
	public static final String USE_SSL         = "USE_SSL";
	public static final String CREATE_WORKDIR  = "CREATE_WORKDIR";
	public static final String CONVERT_NEWLINE = "CONVERT_NEWLINE";
	public static final String STARTTIME_JITTER = "STARTTIME_JITTER";

	public static final Long NOREPLACE         =Long.valueOf(0);
	public static final Long CRLF_TO_LF        =Long.valueOf(1);
	public static final Long LF_TO_CRLF        =Long.valueOf(2);

	private static final String[] LONG_VALUES    = {REPO_PORT, TRACE_LEVEL, RECONNECT_DELAY, NOP_DELAY,
	                                                NOTIFY_PORT, HTTP_PORT, CONVERT_NEWLINE, STARTTIME_JITTER
	                                               };
	private static final String[] BOOLEAN_VALUES = {USE_PATH, VERBOSE_LOGS, ONLINE_SERVER, USE_SSL,
	                                                CREATE_WORKDIR
	                                               };
	private static final String[] FILE_VALUES    = {DEFAULT_WORKDIR, JOB_EXECUTOR, JOB_FILE_PREFIX };
	private static final String[] SECOND_VALUES  = {RECONNECT_DELAY, NOP_DELAY, STARTTIME_JITTER};
	private static final String[] VECTOR_VALUES = {NAME_PATTERN, HTTP_INTERFACE};

	private static final String[] REQUIRED = {REPO_HOST, REPO_PORT, REPO_USER, REPO_PASS};
	private static final String[] WRITE_THROUGH = {REPO_HOST, REPO_PORT, REPO_USER, USE_SSL,
						       KEYSTOREPW, TRUSTSTOREPW,
						       KEYSTORE, TRUSTSTORE
						      };
	private static final String[] DEPRECATED = {RECONNECT_DELAY, NOP_DELAY, DEFAULT_WORKDIR, USE_PATH,
						    VERBOSE_LOGS, NOP_DELAY, JOB_EXECUTOR, JOB_FILE_PREFIX,
						    ENV_MAPPING, NOTIFY_PORT, ONLINE_SERVER, KEYSTORE, TRUSTSTORE,
						    CREATE_WORKDIR
						   };
	public static final String[] ALL_VALUES = {REPO_HOST, REPO_PORT, RECONNECT_DELAY, DEFAULT_WORKDIR,
						   USE_PATH, VERBOSE_LOGS, TRACE_LEVEL, BOOTTIME, NOP_DELAY,
						   JOB_EXECUTOR, JOB_FILE_PREFIX, ENV_MAPPING, DYNAMIC,
	                                           NOTIFY_PORT, HTTP_PORT, HTTP_INTERFACE, HTTP_HOST, HTTP_LOGENCODING,
	                                           ONLINE_SERVER, NAME_PATTERN, KEYSTORE, TRUSTSTORE, KEYSTOREPW,
	                                           TRUSTSTOREPW, USE_SSL, CREATE_WORKDIR, CONVERT_NEWLINE, STARTTIME_JITTER
	                                          };

	private final File startupWorkdir;

	private final File cfgFileName;

	private StreamTokenizer scanner;
	private LineNumberReader scannerFile;
	private boolean scanningConfigFile;

	private static Boolean isWindows = null;

	public static final String[] defaultKeys   = {	USE_PATH,
							VERBOSE_LOGS,
							RECONNECT_DELAY,
							NOP_DELAY,
							TRACE_LEVEL,
							BOOTTIME,
							ONLINE_SERVER,
							CREATE_WORKDIR,
	                                                HTTP_LOGENCODING,
	                                                CONVERT_NEWLINE,
	                                                STARTTIME_JITTER
						};
	public static final Object[] defaultValues = {	Boolean.FALSE,
							Boolean.FALSE,
	                                                Long.valueOf (30),
	                                                Long.valueOf (5),
	                                                Integer.valueOf (Trace.DEFAULT),
							"NONE",
							Boolean.TRUE,
							Boolean.FALSE,
	                                                "utf-8",
	                                                Long.valueOf(0),
	                                                Long.valueOf(5)
						};

	public static boolean isWindows()
	{
		if (isWindows == null) {
			String os = System.getProperty("os.name");
			if (os.toUpperCase().indexOf("WINDOWS") == -1)
				isWindows = Boolean.FALSE;
			else
				isWindows = Boolean.TRUE;
		}
		return isWindows.booleanValue();
	}

	private final void ensureDefaults()
	{
		for (int i = 0; i < defaultKeys.length; ++i)
			if (! containsKey (defaultKeys [i]))
				put (defaultKeys [i], defaultValues [i]);
	}

	private final void initScanner()
		throws IOException
	{
		scannerFile = new LineNumberReader (new InputStreamReader (new FileInputStream (cfgFileName)));
		scanner = new StreamTokenizer (scannerFile);

		scanner.commentChar ('#');
		scanner.wordChars ('_', '_');
		scanner.slashStarComments (true);
		scanner.slashSlashComments (true);
		scanner.eolIsSignificant (true);
	}

	private final String configFileError (final String msg)
	{
		if (scannerFile != null)
			return cfgFileName + ", line " + (scannerFile.getLineNumber() + 1) + ": " + msg;

		return msg;
	}

	private final Object scanValue (final String what)
		throws IOException, CommonErrorException
	{
		scanner.nextToken();
		if ((scanner.ttype == '=') || (scanner.ttype == ':'))
			scanner.nextToken();

		switch (scanner.ttype) {
		case StreamTokenizer.TT_WORD:
		case '"':
			return scanner.sval;

		case StreamTokenizer.TT_NUMBER:
				return Long.valueOf ((long) scanner.nval);

		case StreamTokenizer.TT_EOL:
		case StreamTokenizer.TT_EOF:
			if (what.equals (USE_PATH) || what.equals (VERBOSE_LOGS))
				return Boolean.TRUE;
		}

		throw new CommonErrorException (configFileError ("(04301271412) Missing value of " + what));
	}

	private final void scanFile()
		throws IOException, CommonErrorException
	{
		final HashMap envMapping = new HashMap();

		while (scanner.nextToken() != StreamTokenizer.TT_EOF) {
			if (scanner.ttype == StreamTokenizer.TT_EOL)
				continue;

			if (scanner.ttype != StreamTokenizer.TT_WORD)
				throw new CommonErrorException (configFileError ("(04301271423) Identifier expected"));

			final String key = scanner.sval.toUpperCase();

			if (! (Utils.isOneOf (key, WRITE_THROUGH) || Utils.isOneOf (key, REQUIRED)))
				if (Utils.isOneOf (key, DEPRECATED))
					Trace.warning (configFileError ("(04305082226) Deprecated entry: " + key));
				else if (! Utils.isOneOf (key, new String[] {TRACE_LEVEL, BOOTTIME}))
					Trace.warning (configFileError ("(04305082228) Unknown entry: " + key));

			if (key.equals (ENV_MAPPING)) {
				final String envKey = scanValue (ENV_MAPPING + " key").toString().toUpperCase();
				if (envMapping.containsKey (envKey))
					Trace.warning (configFileError ("(04305072227) Duplicate " + ENV_MAPPING + " entry: " + envKey));

				final String envVal = scanValue (ENV_MAPPING + " value").toString().toUpperCase();

				envMapping.put (envKey, envVal);
				continue;
			}

			if (containsKey (key))
				Trace.warning (configFileError ("(04305082215) Duplicate entry: " + key));

			final Object value = scanValue (key);

			put (key, value);
		}

		put (ENV_MAPPING, envMapping);
	}

	public Config (final String configFileName)
		throws CommonErrorException
	{
		super();

		try {
			cfgFileName = new File (configFileName).getCanonicalFile();

			scanningConfigFile = true;
			initScanner();
			scanFile();
		}

		catch (final IOException ioe) {
			throw new CommonErrorException (configFileError ("(04305082230) Error processing config file: " + ioe.getMessage()));
		}

		catch (final IllegalArgumentException iae) {
			throw new CommonErrorException (configFileError (iae.getMessage()));
		}

		finally {
			scanningConfigFile = false;
			try {
				if (scannerFile != null)
					scannerFile.close();
			}

			catch (final IOException ioe) {
				Trace.error ("(04504100141) Error closing config file: " + ioe.getMessage());
			}
		}

		try {
			startupWorkdir = cfgFileName.getParentFile().getCanonicalFile();
		}

		catch (final IOException ioe) {
			throw new CommonErrorException (configFileError ("(04305181347) Cannot determine startup directory: " + ioe.getMessage()));
		}

		for (int i = 0; i < REQUIRED.length; ++i)
			if (! containsKey (REQUIRED [i]))
				throw new CommonErrorException ("(04305100039) Missing entry: " + REQUIRED [i]);

		ensureDefaults();
	}

	public Object put (Object key, Object value)
	{
		if ((key == null) || (value == null))
			throw new NullPointerException ("(04305091334) Class Config does not permit null keys/values");

		String keyStr = ((String) key).toUpperCase();

		if (Utils.isOneOf (keyStr, LONG_VALUES) && ! (value instanceof Long)) {
			try {
				value =Long.valueOf (value.toString());
			}

			catch (final NumberFormatException e) {
				throw new IllegalArgumentException ("(04305141714) Error converting value: " + e.getMessage());
			}
		}

		else if (Utils.isOneOf (keyStr, BOOLEAN_VALUES) && ! (value instanceof Boolean))
			value = Boolean.valueOf (value.toString());

		else if (Utils.isOneOf (keyStr, FILE_VALUES) && ! (value instanceof File))
			value = new File (value.toString());

		else if (keyStr.equals (ENV_MAPPING)) {
			if (! (value instanceof HashMap))
				throw new IllegalArgumentException ("(04305151810) " + keyStr + " values must be of class java.util.HashMap");

			final HashMap oldMapping = (HashMap) value;
			final HashMap newMapping = new HashMap();

			final Vector keyList = new Vector (oldMapping.keySet());
			final int size = keyList.size();
			for (int i = 0; i < size; ++i) {
				final String oldKey = (String) keyList.get (i);
				if (ScopeConfig.isInternalEntry (oldKey))
					continue;
				final String oldValue = (String) oldMapping.get (oldKey);
				newMapping.put (oldKey.toUpperCase(), oldValue.toUpperCase());
			}

			value = newMapping;
		} else if (keyStr.startsWith (NAME_PATTERN)) {
			Vector npv = (Vector) get (NAME_PATTERN);
			if (npv == null) {
				npv = new Vector();
			}
			npv.add (value);
			value = npv;
			keyStr = NAME_PATTERN;
		}

		if (Utils.isOneOf (keyStr, SECOND_VALUES))
			value =Long.valueOf (1000 * ((Long) value).longValue());

		if (keyStr.equals (TRACE_LEVEL))
			Trace.setLevel (((Long) value).intValue());

		if (keyStr.equals (BOOTTIME)) {
			final String howStr = (String) value;
			String errStr = null;
			if      (howStr.equals ("NONE"))   ProcessInfo.setBoottimeHow (ProcessInfo.BOOTTIME_NONE);
			else if (howStr.equals ("SYSTEM")) ProcessInfo.setBoottimeHow (ProcessInfo.BOOTTIME_SYSTEM);
			else if (howStr.equals ("FILE"))   ProcessInfo.setBoottimeHow (ProcessInfo.BOOTTIME_FILE);
			else                               errStr = "(04307111914) Unknown boottime determination: " + howStr;

			if (errStr != null)
				throw new IllegalArgumentException ("(04307111418) Cannot determine " + value + " boottime: " + errStr);
		}

		if ((! scanningConfigFile) && Utils.isOneOf (keyStr, WRITE_THROUGH)) {
			try {
				updateConfigFile (keyStr, value);
			}

			catch (final IOException ioe) {
				throw new IllegalArgumentException ("(04305091428) Error processing config file: " + ioe.getMessage());
			}
		}

		if (keyStr.equals (DEFAULT_WORKDIR)) {
			final File workdir = (File) value;

			if (! workdir.isAbsolute())
				throw new IllegalArgumentException ("(04301271441) Default working directory must be absolute");

			final String errStr = Utils.chdir (workdir.toString());
			if (errStr != null) {
				throw new IllegalArgumentException ("(04301271442) Cannot chdir() to default working directory " + workdir + ": " + errStr);
			}
		}

		if (keyStr.equals (JOB_EXECUTOR))
			validateJobExecutor ((File) value);

		if (keyStr.equals (JOB_FILE_PREFIX)) {
			if (containsKey (REPO_USER)) {
				final File f = (File) value;
				value = new File (f.toString() + (f.isDirectory() ? File.separator : "") + get (REPO_USER).toString() + "-");
			}

			final File parent = ((File) value).getParentFile();
			if (parent == null)
				throw new IllegalArgumentException ("(04301271438) Job file prefix contains no directory");

			if (! parent.isAbsolute())
				throw new IllegalArgumentException ("(04301271439) Job file prefix must be absolute");
			if (! (parent.isDirectory() && parent.canRead() && parent.canWrite()))
				throw new IllegalArgumentException ("(04301271440) Job file prefix denotes no (accessible) directory");
		}

		final Object result = super.put (keyStr, value);

		if (keyStr.equals (REPO_USER) && containsKey (JOB_FILE_PREFIX)) {
			File prefix = (File) get (JOB_FILE_PREFIX);
			if (result != null) {
				final String oldPrefix = prefix.toString();
				prefix = new File (oldPrefix.substring (0, oldPrefix.length() - ((String) result).length() - 1));
			}
			put (JOB_FILE_PREFIX, prefix);
		}

		return result;
	}

	public Object remove (Object key)
	{
		if (key == null)
			throw new NullPointerException ("(04305181334) Class Config does not permit null keys");

		final String keyStr = ((String) key).toUpperCase();

		if (Utils.isOneOf (keyStr, REQUIRED))
			throw new IllegalArgumentException ("(04504100123) Cannot remove() a required entry: " + keyStr);

		if (keyStr.equals (TRACE_LEVEL))
			Trace.setLevel (Trace.DEFAULT);

		if (Utils.isOneOf (keyStr, WRITE_THROUGH)) {
			try {
				updateConfigFile (keyStr);
			}

			catch (final IOException ioe) {
				throw new IllegalArgumentException ("(04305091429) Error processing config file: " + ioe.getMessage());
			}
		}

		if (keyStr.equals (DEFAULT_WORKDIR)) {
			final String errStr = Utils.chdir (startupWorkdir.toString());
			if (errStr != null)
				throw new IllegalArgumentException ("(04305181339) Cannot chdir() to startup working directory " + startupWorkdir + ": " + errStr);
		}

		final Object result = super.remove (keyStr);

		if (keyStr.equals (REPO_USER) && containsKey (JOB_FILE_PREFIX)) {
			File prefix = (File) get (JOB_FILE_PREFIX);
			if (result != null) {
				final String oldPrefix = prefix.toString();
				prefix = new File (oldPrefix.substring (0, oldPrefix.length() - ((String) result).length() - 1));
			}
			put (JOB_FILE_PREFIX, prefix);
		}

		ensureDefaults();

		return result;
	}

	private final Vector readAllLines()
		throws IOException
	{
		final Vector allLines = new Vector();

		final BufferedReader inFil = new BufferedReader (new InputStreamReader (new FileInputStream (cfgFileName)));
		try {
			while (inFil.ready())
				allLines.add (inFil.readLine());
		}

		finally {
			inFil.close();
		}

		return allLines;
	}

	private final void writeAllLines (final Vector allLines)
		throws IOException
	{
		final RandomAccessFile outFil = new RandomAccessFile (cfgFileName, "rws");
		try {
			outFil.setLength (0);
			final int size = allLines.size();
			for (int i = 0; i < size; ++i)
				outFil.writeBytes (allLines.get (i) + "\n");
		}

		finally {
			outFil.close();
		}
	}

	private final void skipValue()
		throws IOException
	{
		scanner.nextToken();
		if ((scanner.ttype == '=') || (scanner.ttype == ':'))
			scanner.nextToken();
	}

	private final Vector getNoOfAllLines (final String key)
		throws IOException
	{
		final Vector result = new Vector();

		initScanner();

		try {
			while (scanner.nextToken() != StreamTokenizer.TT_EOF)
				if (scanner.ttype == StreamTokenizer.TT_WORD) {
					if (scanner.sval.toUpperCase().equals (key))
						result.add (Integer.valueOf (scannerFile.getLineNumber()));

					skipValue();
					if (key.equals (ENV_MAPPING))
						skipValue();
				}
		}

		finally {
			scannerFile.close();
		}

		return result;
	}

	private final int skipBlanks (final String line, int begin)
	{
		while (true) {
			while (Character.isWhitespace (line.charAt (begin)))
				++begin;

			if (line.substring (begin, begin + 2).equals ("/*"))
				begin = line.indexOf ("*/", begin) + 2;
			else
				break;
		}

		return begin;
	}

	public static final String quoted (final String str)
	{
		final String result = str.replaceAll ("\\\"", "\\\\\"");
		if (result.matches (".*[^0-9].*") || result.length() == 0)
			return "\"" + result + "\"";

		return result;
	}

	private final void updateConfigFile (final String key, final Object newValue)
		throws IOException
	{
		final String placeHolder = key + " = THIS_WILL_BE_REPLACED!!!";

		if (containsKey (key) && get (key).equals (newValue))
			return;

		final Vector allLines = readAllLines();
		final Vector allLineNos = getNoOfAllLines (key);
		final int lineno;
		if (! allLineNos.isEmpty())
			lineno = ((Integer) allLineNos.get (allLineNos.size() - 1)).intValue();
		else {
			Trace.warning ("(04305101139) No line with " + key + " found in " + cfgFileName);
			allLines.add ("");
			allLines.add (placeHolder);
			lineno = allLines.size() - 1;
		}

		final String oldLine = (String) allLines.get (lineno);

		final int length = oldLine.length();

		int begin, commentOpen, commentClose = 0;
		do {
			begin = oldLine.toUpperCase().indexOf (key, commentClose);
			commentOpen = oldLine.lastIndexOf ("/*", begin);
			commentClose = oldLine.indexOf ("*/", commentOpen) + 1;
		} while ((commentOpen != -1) && (commentClose != -1) && (begin >= commentOpen) && (begin <= commentClose));

		begin = skipBlanks (oldLine, begin + key.length());

		if ((oldLine.charAt (begin) == '=') || (oldLine.charAt (begin) == ':'))
			begin = skipBlanks (oldLine, begin + 1);

		int end = begin + 1;
		if (oldLine.charAt (begin) == '"') {
			while ((end < length) && (oldLine.charAt (end) != '"')) {
				if (oldLine.charAt (end) == '\\') {
					++end;
				}
				++end;
			}
		} else {
			while ((end < length) && ! Character.isWhitespace (oldLine.charAt (end))) {
				++end;
			}
		}
		if ((end < length) && (oldLine.charAt (end) == '"'))
			++end;

		String newLine = oldLine.substring (0, begin) + quoted (newValue.toString());
		if (end < length)
			newLine += oldLine.substring (end);
		allLines.set (lineno, newLine);

		writeAllLines (allLines);
	}

	private final void updateConfigFile (final String key)
		throws IOException
	{
		final Vector allLines = readAllLines();

		final Vector lineNos = getNoOfAllLines (key);
		final int size = lineNos.size();
		for (int i = 0; i < size; ++i) {
			final int lineno = ((Integer) lineNos.get (i)).intValue();
			allLines.set (lineno, "// " + allLines.get (lineno));
		}

		writeAllLines (allLines);
	}

	private final void validateJobExecutor (final File executor)
	{
		if (! executor.isAbsolute())
			throw new IllegalArgumentException ("(04301271443) Job executor must be absolute");

		final StringBuffer execOut = new StringBuffer();
		final StringBuffer execErr = new StringBuffer();

		try {
			final Runtime rt = Runtime.getRuntime();
			final Process p = rt.exec (new String[] {executor.toString(), "--version"});
			final BufferedReader stdout = new BufferedReader (new InputStreamReader (p.getInputStream()));
			try {
				final BufferedReader stderr = new BufferedReader (new InputStreamReader (p.getErrorStream()));

				try {
					p.waitFor();

					String line;
					while ((line = stdout.readLine()) != null) {
						if (execOut.length() != 0)
							execOut.append ("\n");
						execOut.append (line);
					}

					while ((line = stderr.readLine()) != null) {
						if (execErr.length() != 0)
							execErr.append ("\n");
						execErr.append (line);
					}

					if (execErr.length() != 0)
						throw new IllegalArgumentException ("(04301271418) Job executor: " + execErr.toString());

					String executorVersion = execOut.toString().toLowerCase();
					executorVersion = executorVersion.substring(0, executorVersion.indexOf("\n"));
					String executorProtocol = executorVersion.substring(executorVersion.lastIndexOf("(") + 1, executorVersion.lastIndexOf(")"));
					String serverVersion = Server.getVersionInfo().toLowerCase();
					System.out.println(serverVersion);
					serverVersion = serverVersion.substring(0, serverVersion.indexOf("\n"));
					String serverProtocol = serverVersion.substring(serverVersion.lastIndexOf("(") + 1, serverVersion.lastIndexOf(")"));

					if (! executorProtocol.equals (serverProtocol))
						throw new IllegalArgumentException ("(04301271419) Invalid/non matching job executor: " + executor + " [ " + serverVersion + " != " + executorVersion + " ]");
				}

				finally {
					stderr.close();
				}
			}

			finally {
				stdout.close();
			}
		}

		catch (final IOException ioe) {
			Trace.error("(04301271416) Cannot launch job executor: " + ioe.getMessage());
			System.exit(1);
		}

		catch (final InterruptedException ie) {
			throw new IllegalArgumentException ("(04301271417) Cannot launch job executor: " + ie.getMessage());
		}
	}
}
