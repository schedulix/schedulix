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


package de.independit.scheduler.server;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.sql.*;
import java.math.*;

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ShutdownThread extends SDMSThread
{

	public final static String __version = "@(#) $Id: ShutdownThread.java,v 2.6.6.1 2013/03/14 10:24:10 ronald Exp $";

	private SystemEnvironment sysEnv;
	private final static int NR = 999999999;
	private boolean postgres = false;

	public ShutdownThread(SystemEnvironment env, Server s)
	{
		super("Shutdown");
		sysEnv = env;
	}

	public int id()
	{
		return -NR;
	}

	public void SDMSrun()
	{
		if(run) {
			doTrace(null, "***********************************************", SEVERITY_INFO);
			doTrace(null, "**                                           **", SEVERITY_INFO);
			doTrace(null, "** U N G R A C E F U L   S H U T D O W N ! ! **", SEVERITY_INFO);
			doTrace(null, "**                                           **", SEVERITY_INFO);
			doTrace(null, "***********************************************", SEVERITY_INFO);
		}
		try {
			sysEnv.dbConnection = Server.connectToDB(sysEnv);
		} catch(SDMSException e) {
			doTrace(null, "Error while unlocking repository (couldn't get database connection)", SEVERITY_ERROR);
			return;
		}
		try {

			if(sysEnv.dbConnection.getMetaData().getDriverName().startsWith("PostgreSQL")) {
				postgres = true;
			}
		} catch (SQLException sqle) {
			doTrace(null, "Error collecting Driver Information", SEVERITY_ERROR);
		}
		doTrace(null, "Release repository lock from " + SystemEnvironment.startTime, SEVERITY_INFO);
		String s1 = "DELETE FROM REPOSITORY_LOCK " +
			    (postgres ? "WHERE TS = CAST (? AS DECIMAL)" :
			     " WHERE TS = ?");
		try {
			PreparedStatement pDelete = sysEnv.dbConnection.prepareStatement(s1);
			pDelete.clearParameters();
			if(postgres) {
				pDelete.setString(1, "" + SystemEnvironment.startTime);
			} else {
				pDelete.setLong(1, SystemEnvironment.startTime);
			}
			pDelete.executeUpdate();
			sysEnv.dbConnection.commit();
			sysEnv.dbConnection.close();
		} catch (SQLException sqle) {
			doTrace(null, "Error while unlocking repository", SEVERITY_ERROR);
		}

		doTrace (null, "Shutdown completed", SEVERITY_INFO);

		return;
	}

}

