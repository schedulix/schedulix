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

import de.independit.scheduler.server.repository.SDMSSubmittedEntity;

public class Environment
	extends HashMap
{
	public static final String __version = "@(#) $Id: Environment.java,v 2.3.14.1 2013/03/14 10:24:06 ronald Exp $";

	public static final Environment systemEnvironment = new Environment();

	private static final class CollectorThread
		extends Thread
	{
		private Process p = null;
		private final Runtime rt = Runtime.getRuntime();
		private final String cmd;

		CollectorThread (final String cmd)
		{
			super();

			this.cmd = cmd;
		}

		public void run()
		{
			try {
				p = rt.exec (cmd);
			}

			catch (final IOException ioe) {
				Utils.abortProgram ("(04301271447) Cannot retrieve system environment: Error launching \"" + cmd + "\": " + ioe.getMessage());
			}
		}

		void waitForRunning()
		{
			while (p == null)
				Utils.sleep (100);
		}

		BufferedReader getInputStream()
		{
			return new BufferedReader (new InputStreamReader (p.getInputStream()));
		}

		int exitValue()
		{
			return p.exitValue();
		}
	}

	private Environment()
	{
		super();

		final String cmd = Utils.getEnvCmd();

		final CollectorThread coll = new CollectorThread (cmd);
		coll.start();
		coll.waitForRunning();

		try {
			final BufferedReader inp = coll.getInputStream();

			boolean running = true;
			int rc = 0;
			do {
				while (inp.ready())
					parseLine (inp.readLine());

				try {
					rc = coll.exitValue();
					running = false;
				}

				catch (final IllegalThreadStateException _) {
					running = true;
				}

				if (running)
					Utils.sleep (100);
			} while (running);

			if (rc != 0)
				Utils.abortProgram ("(04301271448) Cannot retrieve system environment: \"" + cmd + "\" returned " + rc);

			while (inp.ready())
				parseLine (inp.readLine());

			inp.close();
		}

		catch (final IOException ioe) {
			Utils.abortProgram ("(04301271450) Cannot retrieve system environment: " + ioe.getMessage());
		}
	}

	private final void parseLine (final String line)
	{
		if (line == null)
			return;

		final int equ = line.indexOf ('=');
		if (equ == -1)
			Utils.abortProgram ("(04301271449) Missing '=' in environment: " + line);

		if (equ < (line.length() - 1)) {
			int p = 0;
			while ((p < equ) && (line.charAt (p) <= ' '))
				++p;

			final String key = line.substring (p, equ);
			final String val = line.substring (equ + 1);

			put (key, val);
		}
	}

	public Environment (final Vector settings)
	{
		super();

		for (int i = 0; i < settings.size(); i += 2)
			put (settings.get (i),
			     settings.get (i + 1));
	}

	public Environment (final HashMap settings)
	{
		super (settings);
	}

	public Environment (final File file)
	throws IOException
	{
		super();

		final BufferedReader inp = new BufferedReader (new FileReader (file));
		while (inp.ready())
			parseLine (inp.readLine());
		inp.close();
	}

	public boolean containsKey (Object key)
	{
		if(Config.isWindows())
			return super.containsKey (((String) key).toUpperCase());
		else
			return super.containsKey (key);
	}

	public Object get (Object key)
	{
		if(Config.isWindows())
			return super.get (((String) key).toUpperCase());
		else
			return super.get (key);
	}

	public Object put (Object key, Object value)
	{
		if(Config.isWindows())
			return super.put (((String) key).toUpperCase(), value);
		else
			return super.put (key, value);
	}

	public void putAll (Map m)
	{
		final Vector keys = new Vector (m.keySet());
		final int size = keys.size();
		for (int i = 0; i < size; ++i) {
			final Object key = keys.get (i);
			put (key, m.get (key));
		}
	}

	public Object remove (Object key)
	{
		if(Config.isWindows())
			return super.remove (((String) key).toUpperCase());
		else
			return super.remove (key);
	}

	public final Environment merge (final Environment env, final RepoIface ri, final Config cfg)
	{
		final Environment result = new Environment (this);

		final HashMap mapping = (HashMap) cfg.get (Config.ENV_MAPPING);

		if ((env != null) && (mapping != null) && (! mapping.isEmpty())) {
			final Vector keyList = new Vector (mapping.keySet());
			final int size = keyList.size();
			for (int i = 0; i < size; ++i) {
				final String exp = (String) keyList.get (i);
				final String key = (String) mapping.get (exp);

				String val = null;
				if (key.equals (SDMSSubmittedEntity.S_SDMSHOST))
					val = ri.getHost();
				else if (key.equals (SDMSSubmittedEntity.S_SDMSPORT))
					val = String.valueOf (ri.getPort());
				else
					val = (String) env.get (key);

				result.put (exp, val != null ? val : "");
			}
		}

		return result;
	}

	public final String[] toArray()
	{
		final int size = size();
		final String[] result = new String [size];
		final Vector keyList = new Vector (keySet());

		for (int i = 0; i < size; ++i) {
			final String key = (String) keyList.get (i);
			final String val = (String) get (key);

			result [i] = key + '=' + val;
		}

		return result;
	}
}
