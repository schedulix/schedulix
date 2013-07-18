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

import de.independit.scheduler.server.exception.CommonErrorException;

public class JobServer
{
	public static final String __version = "@(#) $Id: JobServer.java,v 2.7.2.1 2013/03/14 10:24:06 ronald Exp $";

	public static void main (String argv[])
	{
		String config_filnam = "";
		String information = "";
		if (argv.length > 0) {
			config_filnam = argv [0];
			if (argv.length > 1)
				information = argv [1];
			if (argv.length > 2) {
				System.err.print ("***WARNING*** (04301271451) Ignoring superfluous arguments:");
				for (int i = 1; i < argv.length; ++i)
					System.err.print (" " + argv [i]);
				System.err.println();
				System.err.flush();
			}
		} else {
			System.out.println ("Enter name of configuration file:");
			System.out.flush();
			try {
				config_filnam = new BufferedReader (new InputStreamReader (System.in)).readLine();
			}

			catch (final IOException ioe) {
				System.out.println ("(04301271452) Oops: " + ioe.getMessage() + " (" + ioe.getClass().getName() + ")");
				System.exit (1);
			}
		}

		if (config_filnam.equals ("--version")) {
			System.err.println (Server.getVersionInfo());
			System.exit (-1);
		}

		if (config_filnam.startsWith ("-")) {
			System.err.println ("***ERROR*** (04506161445) Unknown/invalid argument: " + config_filnam);
			System.exit (-1);
		}

		Server server = null;
		try {
			server = new Server (config_filnam, information);
		}

		catch (final CommonErrorException cee) {
			Utils.abortProgram (cee.getMessage());
		}
		server.runServer ();
	}

	private JobServer()
	{

	}
}
