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
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class TestConfig
{
	private static String inifile;
	private static boolean adminMode	= false;
	private static boolean protectMode	= false;
	public static String programLevel = null;
	public static String buildDate = null;
	public static String buildHash = null;

	public static final String PROGRAMLEVEL = "Level";
	public static final String BUILDHASH = "Build";
	public static final String BUILDDATE = "Build-Date";

	// Startup Options
	private static final String ADMIN_OPTION = "-admin";
	private static final String PROTECT_OPTION = "-protected";

	private static SystemEnvironment env;


	private static void say_hello(String argv[])
	{
		final String serverStart = "**************************************************************************";
		final String prog        = "** BICsuite Configuration Test";
		final String version     = "** Version " + SystemEnvironment.programVersion;
		final String copyright2  = "** Copyright (C) 2015 independIT Integrative Technologies GmbH";

		SDMSThread.doTrace(null, serverStart, SDMSThread.SEVERITY_INFO);
		try {
			URL jarURL = new TestConfig().getClass().getResource("TestConfig.class");
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
			// now set the program level
			programLevel=a.getValue(PROGRAMLEVEL);
			buildDate = a.getValue(BUILDDATE);
			buildHash = a.getValue(BUILDHASH);

		} catch (Exception e) {
			// we just ignore this (for now)
			SDMSThread.doTrace(null, e.toString(), SDMSThread.SEVERITY_INFO);
		}

		SDMSThread.doTrace(null, serverStart, SDMSThread.SEVERITY_INFO);
		SDMSThread.doTrace(null, prog, SDMSThread.SEVERITY_INFO);
		SDMSThread.doTrace(null, version, SDMSThread.SEVERITY_INFO);
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
				adminMode = true;	// protect implies admin
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

		/* Test jdbc connection */
		readConfig(inifile, programLevel);
	}

	public static void readConfig(String inifile, String programLevel)
	{
		Properties props = new Properties();	// ini-File Contents
		InputStream ini;

		// We search classpath first
		ini = TestConfig.class.getResourceAsStream(inifile);
		// If we don't find it, we try the name as a "full qualified filename"
		try {
			if(ini == null)
				ini = new FileInputStream(inifile);
			props.load(ini);
		} catch(FileNotFoundException fnf) {
			SDMSThread.doTrace(null, "Properties File not found : " + fnf, SDMSThread.SEVERITY_FATAL);
		} catch(IOException ioe) {
			SDMSThread.doTrace(null, "Error loading Properties file: " + ioe, SDMSThread.SEVERITY_FATAL);
		}
		for (Enumeration e = props.propertyNames() ; e.hasMoreElements() ;) {
			String k = (String) e.nextElement();
			if(k.equals(SystemEnvironment.S_DBPASSWD))		continue;	// for security reasons we skip the password
			if(k.equals(SystemEnvironment.S_SYSPASSWD))		continue;	// for security reasons we skip the password
			if(k.equals(SystemEnvironment.S_KEYSTOREPASSWORD))	continue;	// for security reasons we skip the password
			if(k.equals(SystemEnvironment.S_TRUSTSTOREPASSWORD))	continue;	// for security reasons we skip the password
			SDMSThread.doTrace(null, k + "=" + props.getProperty(k), SDMSThread.SEVERITY_INFO);
		}
		      
		env = new SystemEnvironment(props, programLevel, buildDate, buildHash, null);
		Server.setIniFile(inifile);

		try {
			Connection c = Server.connectToDB(env);
			try {
				String driverName = c.getMetaData().getDriverName();
				SDMSThread.doTrace(null, "JDBC Driver Name =" + driverName, SDMSThread.SEVERITY_INFO);
			} catch (SQLException sqle) {
				SDMSThread.doTrace(null, "Cannot determine JDBC Driver Name", SDMSThread.SEVERITY_WARNING);
			}
			try {
				c.close();
			} catch (Exception e) {
				// ignore
			}
		} catch (SDMSException e) {
			System.out.println("Connection failed ... :-(");
			System.out.println(e.toString());
		}
	}

	private TestConfig()
	{
		// don't instantiate!
	}
}
