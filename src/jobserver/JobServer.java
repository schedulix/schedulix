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

	public static String env_filnam = null;
	public static String session_info = "";

	public static void main (String argv[])
	{
		if (argv.length == 0) {
			System.err.println ("(04402181122) Missing parameter configfile");
			System.err.flush();
			System.exit (1);
		}
		if (argv[0].equals ("--version")) {
			System.err.println (Server.getVersionInfo());
			System.exit (-1);
		}
		String config_filnam = argv[0];
		int ppos = 1;
		if (argv.length > ppos) {
			if (! argv[ppos].equals("-e")) {
				session_info = argv[ppos];
				ppos++;
			}
		}
		if (argv.length > ppos) {
			if (argv[1].equals("-e")) {
				if (argv.length > ppos + 1) {
					env_filnam = argv [ppos + 1];
					ppos += 2;
				} else {
					System.err.println ("(04402181123) Missing parameter environmentfile");
					System.err.flush();
					System.exit (1);
				}
			}
		}

		Server server = null;
		try {
			server = new Server (config_filnam);
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
