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

import java.lang.*;
import java.util.*;
import java.io.*;
import de.independit.scheduler.server.SystemEnvironment;

class Libjni
{

	public static final String copyright = "Copyright (c) 2002-2014";
	public static final String company   = "independIT Integrative Technologies GmbH";
	public static final String version   = SystemEnvironment.programVersion;

	public static final Boolean alive    = new Boolean(true);
	public static final Boolean dead     = new Boolean(false);

	private static final String unixEnv  = "/bin/sh -c env";
	private static final String winEnv   = "CMD.EXE /C SET";

	private static final String unixNull = "/dev/null";
	private static final String winNull  = "NUL";

	private static final char BOOTTIME_NONE   = 'N';
	private static final char BOOTTIME_SYSTEM = 'S';
	private static final char BOOTTIME_FILE   = 'F';

	public static final int OS_WIN       = 0;
	public static final int OS_LINUX     = 1;
	public static final int OS_UNIX      = 2;
	public static final int OS_MAC       = 3;
	public static final int OS_AIX       = 4;
	public static final int OS_DONTKNOW  = -1;

	private char boottimeHow;
	private String os;
	private int osType;

	public Libjni()
	{
		os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac"))
			osType = OS_MAC;
		else if (os.contains("nix"))
			osType = OS_UNIX;
		else if (os.contains("sunos"))
			osType = OS_UNIX;
		else if (os.contains("nux"))
			osType = OS_LINUX;
		else if (os.contains("aix"))
			osType = OS_AIX;
		else if (os.contains("win"))
			osType = OS_WIN;
		else
			osType = OS_DONTKNOW;
		boottimeHow = BOOTTIME_NONE;
	}

	public static String getVersion()
	{
		return version;
	}

	public static String getCopyright()
	{
		return copyright;
	}

	public static String getCompany()
	{
		return company;
	}

	public int getPid()
	{
		try {
			return Integer.parseInt(ProcessInfo.getPid());
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public String getBoottime(char how)
	{
		return "0";
	}

	public Boolean isAlive(String processId)
	{

		long pid;
		String boottime;
		String starttime;
		char how;
		int s, e;

		s = 0;
		e = 0;
		while (processId.charAt(e) != '@') ++e;
		try {
			pid = Long.parseLong(processId.substring(s, e));
		} catch (NumberFormatException nfe) {
			return null;
		}

		s = e + 1;
		how = processId.charAt(s);

		++s;
		e = s;
		while (processId.charAt(e) != '+') ++e;
		boottime = processId.substring(s, e);

		s = e + 1;
		starttime = processId.substring(s);

		if (!boottime.equals(getBoottime(how))) return dead;
		if (!starttime.equals(getProcessStartTime(pid))) return dead;

		return alive;
	}

	public final String getEnvCmd()
	{
		if (osType == OS_WIN)
			return winEnv;
		else if (osType != OS_DONTKNOW)
			return unixEnv;

		return null;
	}

	public String chdir(String path)
	{
		return null;
	}

	public boolean setBoottimeHow(char how)
	{
		if (boottimeHow == BOOTTIME_NONE ||
		    boottimeHow == BOOTTIME_SYSTEM ||
		    boottimeHow == BOOTTIME_FILE) {
			boottimeHow = how;
			return true;
		}
		return false;
	}

	public char getBoottimeHow()
	{
		return boottimeHow;
	}

	private String linuxProcessStartTime(long pid)
	{
		String pfn = "/proc/" + pid + "/stat";
		File   pfh = new File(pfn);
		String s = null;
		BufferedReader pfr = null;

		if (!pfh.canRead()) return null;

		try {
			pfr = new BufferedReader(new FileReader(pfh));
			s = pfr.readLine();
		} catch (IOException ioe) {
			return null;
		} finally {
			if (pfr != null)
				try {
					pfr.close();
				} catch(Exception e) {  }
		}

		int numSpace = 2;

		int l = s.length();
		int i = l - 1;
		while (i >= 0 && s.charAt(i) != ')') i--;
		if (i < 0) return null;

		++i;
		while( i < l && numSpace < 22) {
			if (s.charAt(i) == ' ') ++numSpace;
			++i;
		}

		int start = i;
		++i;
		while( s.charAt(i) != ' ') ++i;
		String spid = s.substring(start, i);

		return spid;
	}

	private String solarisProcessStartTime(long pid)
	{

		return "0";
	}

	private String windowsProcessStartTime(long pid)
	{

		return "0";
	}

	public String getProcessStartTime(long pid)
	{
		if (osType == OS_LINUX)
			return linuxProcessStartTime(pid);
		else if (osType == OS_UNIX)
			return solarisProcessStartTime(pid);
		else if (osType == OS_WIN)
			return windowsProcessStartTime(pid);

		return null;
	}
}

