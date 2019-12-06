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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;

public class AuditWriter
{
	private static final SimpleDateFormat sysDateFmt = (SimpleDateFormat) SystemEnvironment.staticSystemDateFormat.clone();
	private static final SimpleDateFormat fileExtFmt = new SimpleDateFormat ("yyyyMMddHHmmss", SystemEnvironment.systemLocale);

	private static PrintStream audit = null;
	private static int nrLinesWritten = 0;

	private static void openAuditFile(SystemEnvironment sysEnv)
		throws IOException
	{
		int fileno = 0;
		File base = new File(sysEnv.auditFile);
		String dir = base.getParent();
		String child = base.getName() + ".";
		String dt = fileExtFmt.format (new java.util.Date (System.currentTimeMillis()));
		child = child + dt;
		File auditFile = new File(dir, child);
		audit = new PrintStream(auditFile);
		nrLinesWritten = 0;
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

	public static synchronized void write (SystemEnvironment sysEnv, Long versionId, String stmt)
		throws SDMSException
	{
		try {
			if (audit == null) {
				openAuditFile(sysEnv);
			}
			audit.println(getHeader(sysEnv, versionId) + stmt);
			nrLinesWritten++;
			if (nrLinesWritten >= SystemEnvironment.auditEntries) {
				audit.close();
				audit = null;
			}
		} catch (IOException ioe) {
			if (audit != null) {
				audit.close();
				audit = null;
			}
		} catch (SerializationException e) {
			throw e;
		} catch (SDMSException e) {
		}
	}

}

