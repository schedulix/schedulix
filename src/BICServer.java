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


package de.independit.scheduler;

import java.io.*;
import java.lang.*;
import java.util.*;

import java.util.jar.*;
import java.net.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;

public class BICServer
{

	public final static String __version = "@(#) $Id: BICServer.java,v 2.7.6.1 2013/03/14 10:24:01 ronald Exp $";

	private static String inifile;
	private static boolean adminMode	= false;
	private static boolean protectMode	= false;
	private static Server s;
	public static String programLevel = null;

	private static final String ADMIN_OPTION = "-admin";
	private static final String PROTECT_OPTION = "-protected";

	private static void say_hello(String argv[])
	{
		final String serverStart = "**************************************************************************";
		final String prog        = "** BICsuite!step";
		final String version     = "** Version " + SystemEnvironment.programVersion;
		final String copyright1  = "** Copyright (C) 2000-2002 topIT Informationstechnologie GmbH";
		final String copyright2  = "** Copyright (C) 2003-2011 independIT Integrative Technologies GmbH";

		SDMSThread.doTrace(null, serverStart, SDMSThread.SEVERITY_INFO);
		try {
			URL jarURL = new BICServer().getClass().getResource("BICServer.class");
			JarURLConnection jurlConn = (JarURLConnection)jarURL.openConnection();
			Manifest manifest = jurlConn.getManifest();

			Map entries = manifest.getEntries();

			java.util.jar.Attributes a = manifest.getMainAttributes();
			Iterator it = a.keySet().iterator();
			while (it.hasNext()) {
				java.util.jar.Attributes.Name attrName = (java.util.jar.Attributes.Name)it.next();
				String attrValue = a.getValue(attrName);
				SDMSThread.doTrace(null, attrName + " : " + attrValue, SDMSThread.SEVERITY_INFO);
			}

			programLevel=a.getValue("Level");

		} catch (Exception e) {

			SDMSThread.doTrace(null, e.toString(), SDMSThread.SEVERITY_INFO);
		}

		SDMSThread.doTrace(null, serverStart, SDMSThread.SEVERITY_INFO);
		SDMSThread.doTrace(null, prog, SDMSThread.SEVERITY_INFO);
		SDMSThread.doTrace(null, version, SDMSThread.SEVERITY_INFO);
		SDMSThread.doTrace(null, copyright1, SDMSThread.SEVERITY_INFO);
		SDMSThread.doTrace(null, copyright2, SDMSThread.SEVERITY_INFO);
		SDMSThread.doTrace(null, serverStart, SDMSThread.SEVERITY_INFO);

		for (int i = 0; i < argv.length; ++i) {
			if (i == 0)
				SDMSThread.doTrace(null, "Commandline Start Options:", SDMSThread.SEVERITY_INFO);
			SDMSThread.doTrace(null, "\t" + argv[i], SDMSThread.SEVERITY_INFO);
		}

	}

	private static void scan_args(String argv[])
	{
		int i;
		boolean gotAdmin = false;
		boolean gotProtect = false;
		boolean gotIni = false;

		for(i = 0; i < argv.length; i++) {
			if(argv[i].toLowerCase().equals(ADMIN_OPTION)) {
				SDMSThread.doTrace(null, "Server will start in admin mode, logins disabled" , SDMSThread.SEVERITY_INFO);
				if(gotAdmin) {
					SDMSThread.doTrace(null, "Duplicate option: " + ADMIN_OPTION, SDMSThread.SEVERITY_INFO);
				}
				adminMode = true;
				gotAdmin = true;
			} else if(argv[i].toLowerCase().equals(PROTECT_OPTION)) {
				SDMSThread.doTrace(null, "Server will start in protected mode, logins and internal threads disabled" ,
				                   SDMSThread.SEVERITY_INFO);
				if(gotProtect) {
					SDMSThread.doTrace(null, "Duplicate option: " + PROTECT_OPTION, SDMSThread.SEVERITY_INFO);
				}
				protectMode = true;
				adminMode = true;
				gotProtect = true;
			} else {
				if(gotIni) {
					SDMSThread.doTrace(null, "Duplicate specification of configurationfile, effective : " + inifile, SDMSThread.SEVERITY_INFO);
				} else {
					inifile = argv[i];
					gotIni = true;
				}
			}
		}

		if (!gotIni)
			SDMSThread.doTrace(null, "Missing specification of configurationfile", SDMSThread.SEVERITY_FATAL);
	}

	public static void main(String argv[])
	{
		say_hello(argv);
		scan_args(argv);

		s = new Server(inifile, adminMode, protectMode, programLevel);
		s.serverMain();
	}

	private BICServer()
	{

	}
}
