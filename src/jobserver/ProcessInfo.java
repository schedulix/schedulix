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

import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.text.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.*;
import com.sun.jna.Pointer;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class ProcessInfo
{
	static final long BOOTTIME_JITTER  = 90;
	static final long STARTTIME_JITTER = 10;

	public static final char BOOTTIME_NONE   = 'N';
	public static final char BOOTTIME_SYSTEM = 'S';
	public static final char BOOTTIME_FILE   = 'F';

	public static final char BOOTTIME_DEFAULT =  BOOTTIME_NONE;

	private static char boottimeHow = BOOTTIME_DEFAULT;

	private static final String VAR_RUN_LASTBOOT  = "/var/run/lastboot";

	private static HashMap<String,Long> bootTimes = new HashMap<String,Long>();

	public static String getPid(Process p)
	{
		long pid = 0;

		if(p.getClass().getName().equals("java.lang.UNIXProcess")) {

			try {
				Field f = p.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				pid = f.getInt(p);
			} catch (Exception e) {
				throw new RuntimeException("(02310251000) Cannot get pid of child Process : " + e.toString());
			}
		} else if (System.getProperty("os.name").toLowerCase().contains("win")) {
			try {
				Field f = p.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				long h = f.getLong(p);
				Kernel32 kernel = Kernel32.INSTANCE;
				W32API.HANDLE handle = new W32API.HANDLE();
				handle.setPointer(Pointer.createConstant(h));
				pid = kernel.GetProcessId(handle);
			} catch (Exception e) {
				throw new RuntimeException("(02310251001) Cannot get pid of child Process : " + e.toString());
			}
		}

		return "" + pid;
	}

	public static String getPid()
	{
		final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

		final String jvmName = runtimeBean.getName();
		String pid = jvmName.split("@")[0];

		return pid;
	}

	private static String getBootTimeFile()
	{
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("sunos")) {
			File f = new File(VAR_RUN_LASTBOOT);
			return new Long(f.lastModified()).toString();
		}
		return "0";
	}

	private static String getBootTimeSystem()
	{
		try {
			long bootTime = -1;
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				Process p = Runtime.getRuntime().exec("net stats srv");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				String pattern = getPattern();
				int patternLength = pattern.length();
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				while ((line = in.readLine()) != null) {
					int lineLength = line.length();
					if (lineLength >= patternLength) {
						line = line.substring(lineLength - patternLength);
						try {
							bootTime = format.parse(line).getTime() / 1000;
						} catch (ParseException pe) {
							continue;
						}
						break;
					}
				}
			} else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("sunos")) {
				Process p = Runtime.getRuntime().exec("uptime");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = in.readLine();
				if (line != null) {
					Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
					Matcher matcher = parse.matcher(line);
					if (matcher.find()) {
						String _days = matcher.group(2);
						String _hours = matcher.group(3);
						String _minutes = matcher.group(4);
						int days = _days != null ? Integer.parseInt(_days) : 0;
						int hours = _hours != null ? Integer.parseInt(_hours) : 0;
						int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
						bootTime = System.currentTimeMillis() / 1000 - ((minutes * 60) + (hours * 60 * 60) + (days * 60 * 60 * 24));
					}
				}
			}
			return new Long(bootTime).toString();
		} catch (Exception e) {
			throw new RuntimeException("(02310251147) Cannot get boot time : " + e.toString());
		}
	}

	private static String getPattern()
	{
		final Date currentDate = new Date();
		final DateFormat dateInstance  = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
		final String format = dateInstance.format(currentDate);
		if (dateInstance instanceof SimpleDateFormat) {
			return ((SimpleDateFormat) dateInstance).toPattern();
		}
		return "unknown";
	}

	public static long getStartTime(String strPid)
	{
		long result = 0;
		String os = System.getProperty("os.name").toLowerCase();
		if(   os.contains("sunos")
		   || os.contains("nux")
		  ) {
			try {
				BasicFileAttributes attr = Files.readAttributes(Paths.get("/proc/" + strPid), BasicFileAttributes.class);
				result = attr.creationTime().to(TimeUnit.SECONDS);
			} catch (Exception e) {
				return 0;
			}
		}
		return result;
	}

	synchronized public static HashMap<String,Long> getStartTimes(Config cfg, HashMap<String,Long> startTimes)
	{
		HashMap<String,Long> result = startTimes;
		if (result == null)
			result = new HashMap<String,Long>();
		else
			result.clear();

		final File job_file_prefix = (File) cfg.get (Config.JOB_FILE_PREFIX);
		final File tmp_file = new File(job_file_prefix.getParent() + "/starttimes." + cfg.get(Config.REPO_USER));
		String tmpfilename = null;
		try {
			tmpfilename = tmp_file.getCanonicalPath();
		} catch (Exception e) {}

		String os = System.getProperty("os.name").toLowerCase();
		if(   os.contains("mac")
		   || os.contains("nix")
		   || os.contains("nux")
		   || os.contains("aix")
		   || os.contains("hp-ux")
		  ) {
			try {
				ProcessBuilder pb = new ProcessBuilder("ps", "-e", "-o", "pid=", "-o", "etime=");
				Map<String, String> psEnv = pb.environment();
				if (os.contains("hp-ux"))
					psEnv.put("UNIX95", "1");
				pb.redirectOutput(new File(tmpfilename));
				pb.redirectErrorStream(true);
				Process p = pb.start();
				p.getOutputStream().close();
				try {
					p.waitFor();
				} catch (InterruptedException ie) {  }
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tmpfilename)));
				String line;
				long now = System.currentTimeMillis() / 1000;
				while ((line = in.readLine()) != null) {
					line = line.trim();
					String parts[] = line.split("  *");
					String strPid = parts[0];
					String strDate;
					if (parts.length < 2) 
						strDate = "00:00:00";
					else
						strDate = parts[1];
					String etimeParts[] = strDate.split("[-:]");
					long etime = 0;
					int idx = 0;
					try {
						if (etimeParts.length > 3) {
							etime += Integer.parseInt(etimeParts[idx]) * 86400;
							idx++;
						}
						if (etimeParts.length > 2) {
							etime += Integer.parseInt(etimeParts[idx]) * 3600;
							idx++;
						}
						etime += Integer.parseInt(etimeParts[idx]) * 60;
						idx++;
						etime += Integer.parseInt(etimeParts[idx]);
					} catch (NumberFormatException nfe) {
						etime = 0;
					}
					Long startTime = new Long(now - etime);
					result.put(strPid,startTime);
				}
				in.close();
			} catch (IOException e) {
				throw new RuntimeException("(02310251043) Exception in getStartTimes() : " + e.toString());
			}
		} else if (os.contains("win")) {
			try {
				final String BICSUITEHOME = System.getenv("BICSUITEHOME");
				if (BICSUITEHOME == null)
					throw new Exception("ERROR: BICSUITEHOME must be set, but isn't");
				ProcessBuilder pb = new ProcessBuilder(BICSUITEHOME + "\\bin\\winps.exe");

				pb.redirectOutput(new File(tmpfilename));
				pb.redirectErrorStream(true);
				Process p = pb.start();
				p.getOutputStream().close();
				try {
					p.waitFor();
				} catch (InterruptedException ie) {  }
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tmpfilename), "UTF-8"));
				String line;
				String pattern = "yyyyMMddHHmmss";
				int patternLength = pattern.length();
				SimpleDateFormat format = new SimpleDateFormat(pattern + " z");
				while ((line = in.readLine()) != null) {
					line = line.trim();
					if (line.length() < patternLength) continue;
					String strDate = line.substring(0, patternLength);
					String strPid  = line.substring(patternLength);
					int blankIdx = strPid.indexOf(" ");
					if (blankIdx < 0) continue;
					strPid = strPid.substring(blankIdx).trim();

					if (strPid.equals("")) continue;
					try {
						Long startTime = new Long (format.parse(strDate + " UTC").getTime() / 1000);
						result.put(strPid,startTime);
					} catch (ParseException pe) {
						continue;
					}
				}
				in.close();
			} catch (Exception e) {
				throw new RuntimeException("(02310251044) Process start times : " + e.toString());
			}
		} else {
			Trace.error("Unknown Operating System : " + os);
		}
		return result;
	}

	public static boolean isAlive (String processId, HashMap<String,Long> startTimes)
	{
		long pid;
		String strPid;
		String boottime;
		String starttime;
		char how;
		int s, e;

		s = 0;
		e = 0;
		while (processId.charAt(e) != '@') ++e;
		try {
			strPid = processId.substring(s, e);
			pid = Long.parseLong(strPid);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("(023102812133) Invalid processId " + processId + " : " + nfe.toString());
		}

		s = e + 1;
		how = processId.charAt(s);

		++s;
		e = s;
		while (processId.charAt(e) != '+') ++e;
		boottime = processId.substring(s, e);

		s = e + 1;
		starttime = processId.substring(s);

		long startTimeJob = 0;
		if (!(startTimes.containsKey(strPid))) {
			startTimeJob = getStartTime(strPid);
			if (startTimeJob == 0) {
				return false;
			}
		} else {
			startTimeJob = startTimes.get(strPid).longValue();
		}
		long startTimePid = Long.parseLong(starttime);

		if (Math.abs(startTimeJob - startTimePid) > STARTTIME_JITTER) {
			System.err.println("startTimeJob = " + new Long(startTimeJob).toString() + " startTimePid = " + starttime);
			String os = System.getProperty("os.name").toLowerCase();
			if (!os.contains("hp-ux"))
			return false;
		}

		return true;
	}

	public static String getBoottime(char how)
	{
		if (! bootTimes.containsKey("" + how)) {
			String bootTime = "0";
			switch (how) {
				case 'S':
					bootTime = getBootTimeSystem();
					break;
				case 'F':
					bootTime = getBootTimeFile();
					break;
			}
			bootTimes.put("" + how, new Long(bootTime));
		}
		return bootTimes.get("" + how).toString();

	}

	public static boolean setBoottimeHow(char how)
	{
		if (boottimeHow == BOOTTIME_NONE ||
		    boottimeHow == BOOTTIME_SYSTEM ||
		    boottimeHow == BOOTTIME_FILE) {
			boottimeHow = how;
			return true;
		}
		return false;
	}

	public static char getBoottimeHow()
	{
		return BOOTTIME_NONE;
	}

}
