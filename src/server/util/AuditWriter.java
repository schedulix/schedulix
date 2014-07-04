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


package de.independit.scheduler.server.util;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class AuditWriter
{

	public final static String __version = "@(#) $Id: AuditWriter.java,v 2.1.8.1 2013/03/14 10:25:28 ronald Exp $";

	private static final SimpleDateFormat sysDateFmt = (SimpleDateFormat) SystemEnvironment.staticSystemDateFormat.clone();

	private static PrintStream audit = null;

	private static void openAuditFile(SystemEnvironment sysEnv)
		throws IOException
	{
		int fileno = 0;
		File base = new File(sysEnv.auditFile);
		String dir = base.getParent();
		String child = base.getName() + ".";
		String files[] = new File(dir).list();
		for (int i = 0; i < files.length; ++i) {
			if (files[i].startsWith(child)) {
				String nr = files[i].substring(child.length());
				try {
					int tmp;
					tmp = Integer.parseInt(nr);
					if (tmp > fileno) fileno = tmp;
				} catch (NumberFormatException nfe) {

				}
			}
		}
		fileno++;
		child = child + fileno;
		File auditFile = new File(dir, child);
		audit = new PrintStream(auditFile);
	}

	private static final String getHeader (SystemEnvironment sysEnv, Long versionId)
		throws SDMSException
	{
		String name = null;
		if (sysEnv.cEnv != null) {
			final Long uid = sysEnv.cEnv.uid();
			if (uid != null) {
				try {
					SDMSUser u = SDMSUserTable.getObject(sysEnv, uid);
					name = u.getName(sysEnv);
				} catch (NotFoundException nfe) {
					name = sysEnv.cEnv.name() + "(" + uid + ")";
				}
			} else
				name = sysEnv.cEnv.name();
		} else
			name = "Unknown";

		String header = "" + versionId + "\t" + name;

		synchronized (sysDateFmt) {
			return header + "\t[" + sysDateFmt.format (new java.util.Date (System.currentTimeMillis())) + "]\t";
		}
	}

	public static void write (SystemEnvironment sysEnv, Long versionId, String stmt)
		throws SDMSException
	{
		try {
			if (audit == null) {
				openAuditFile(sysEnv);
			}
			audit.println(getHeader(sysEnv, versionId) + stmt);
		} catch (IOException ioe) {

			if (audit != null) {
				audit.close();
				audit = null;
			}
		} catch (SDMSException e) {

		}
	}

}

