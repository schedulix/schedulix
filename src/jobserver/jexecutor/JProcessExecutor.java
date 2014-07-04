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


package de.independit.scheduler.jobserver.jexecutor;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.nio.channels.*;
import de.independit.scheduler.jobserver.*;
import de.independit.scheduler.jobserver.Utils.*;

public class JProcessExecutor extends Jexecutor
{

	private boolean isComplete;
	private boolean usePath;

	private String workdir;
	private String logfile;
	private boolean samelogs;
	private boolean logappend;
	private boolean errappend;
	private String errlog;

	private String command;
	private Vector arguments;
	private PrintStream userLog;

	private long myStartTime = 0;
	private long userStartTime = 0;

	public JProcessExecutor()
	{
		super();
	}

	@Override
	public String getMyStarttime()
	{
		return "" + myStartTime;
	}

	@Override
	public String getUserStarttime()
	{
		return "" + userStartTime;
	}

	@Override
	public String getPid()
	{
		return ProcessInfo.getPid();
	}

	@Override
	public String getPid(Process p)
	{
		return ProcessInfo.getPid(p);
	}

	private String checkPath(String cmd, String wd, boolean usePath)
	{
		File cmdFile = new File(cmd);

		if (cmdFile.isAbsolute() && cmdFile.canExecute()) return cmd;
		if (cmdFile.isAbsolute()) return null;

		cmdFile = new File(wd + File.separator + cmd);
		if (cmdFile.canExecute()) return cmdFile.toString();
		if (!usePath) return null;

		String path = System.getenv("PATH");
		String dirs[] = path.split(File.pathSeparator);
		for (int i = 0; i < dirs.length; ++i) {
			cmdFile = new File(dirs[i] + File.separator + cmd);
			if (cmdFile.canExecute()) return cmdFile.toString();
		}
		return null;
	}

	private void redirectOutput(ProcessBuilder pb)
	{
		userLog = null;

		File fLog = new File(logfile);
		try {

			if (!fLog.isAbsolute()) {
				fLog = new File(workdir + File.separator + logfile);
			}
			FileOutputStream fosLog = new FileOutputStream(fLog, logappend);

			if (!logappend) {
				fosLog.close();
				fosLog = new FileOutputStream(fLog, true);
			}

			userLog = new PrintStream(fosLog, true);

			pb.redirectOutput(ProcessBuilder.Redirect.appendTo(fLog));
		} catch (IOException ioe) {
			throw new RuntimeException ("Cannot open logfile " + fLog + " : " + ioe.toString());
		}

		File fErr = new File(errlog);
		try {

			if (!fErr.isAbsolute()) {
				fErr = new File(workdir + File.separator + errlog);
			}
			if (!samelogs) {
				if (!errappend) {
					FileOutputStream errLog = new FileOutputStream(fErr, errappend);
					errLog.close();
				}
				pb.redirectError(ProcessBuilder.Redirect.appendTo(fErr));
			}
		} catch (IOException ioe) {
			throw new RuntimeException ("Cannot open errlogfile " + fErr + " : " + ioe.toString());
		}

		pb.redirectErrorStream(samelogs);

	}

	private Process startUserProcess(ProcessBuilder pb)
	{
		pb.directory(new File(workdir));

		Process process = null;
		try {
			if (taskfile.getVerboseLogs())
				userLog.println((new Date()).toString() + " started");
			process = pb.start();
			userStartTime = System.currentTimeMillis() / 1000;
		} catch (IOException ioe) {

			try {
				taskfile.open();
				taskfile.append(Feil.STATUS_CHILD_ERROR, "(02310220819) Cannot execute " + command + " : " + ioe.toString());
				taskfile.close();
			} catch (IOException ioe2) {
				throw new RuntimeException ("(02310220940) Cannot write taskfile : " + ioe2.toString());
			}
			return null;
		}

		try {
			taskfile.open();
			taskfile.append(Feil.EXECPID, getProcessId(process));
			taskfile.append(Feil.EXTPID,  getProcessId());
			taskfile.append(Feil.STATUS,  Feil.STATUS_RUNNING);
			taskfile.close();
		} catch (IOException ioe) {
			throw new RuntimeException ("(02310220941) Cannot write taskfile : " + ioe.toString());
		}

		return process;
	}

	private void getTaskfileInfo()
	{

		if (argsType != ARGS_RUN)
			throw new RuntimeException("Programm arguments not evaluated");

		boolean isComplete = taskfile.getComplete();
		if (!isComplete)
			throw new RuntimeException("Taskfile " + taskfile.toString() + " seems to be incomplete");

		usePath = taskfile.getUsepath();

		workdir = taskfile.getWorkdir();
		logfile = taskfile.getLogfile();
		samelogs = taskfile.getSamelogs();
		logappend = taskfile.getLogappend();
		errappend = taskfile.getErrappend();
		errlog = (samelogs ? logfile : taskfile.getErrlog());

		command = taskfile.getCommand();
		arguments = taskfile.getArgs();
	}

	@Override
	public void run ()
	{
		myStartTime = System.currentTimeMillis() / 1000;

		getTaskfileInfo();

		String cmd[] = new String[arguments.size() + 1];
		cmd[0] = checkPath(command, workdir, usePath);
		if (cmd[0] == null) {
			try {
				taskfile.open();
				taskfile.append(Feil.STATUS_CHILD_ERROR, "(02310221008) Cannot execute " + command);
				taskfile.close();
			} catch (IOException ioe2) {
				throw new RuntimeException ("(02310220940) Cannot write taskfile : " + ioe2.toString());
			}
			return;
		}
		for (int i = 0; i < arguments.size(); ++i) {
			cmd[i+1] = (String) arguments.get(i);
		}

		ProcessBuilder pb = new ProcessBuilder(cmd);

		if (env != null) {
			Map<String, String> pbEnv = pb.environment();
			pbEnv.putAll(env);
		}

		redirectOutput(pb);

		Process process = startUserProcess(pb);

		if (process != null) {
			while (true) {
				try {
					process.waitFor();
					break;
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
			if (taskfile.getVerboseLogs())
				userLog.println((new Date()).toString() + " finished (" + process.exitValue() + ")");

			try {
				taskfile.open();
				taskfile.append(Feil.RETURNCODE, new Integer(process.exitValue()).toString());
				taskfile.append(Feil.STATUS,     Feil.STATUS_FINISHED);
			} catch (IOException ioe) {
				throw new RuntimeException ("(02310220942) Cannot write taskfile : " + ioe.toString());
			}
		}

		userLog.close();

	}

	public static void main(String[] args)
	{
		int argState = ARGS_NOTREAD;

		try {
			JProcessExecutor je = new JProcessExecutor();
			try {
				argState = je.checkArgs(args);
			} catch (IllegalArgumentException iae) {
				System.out.println(getUsage());
				System.exit(1);
			}
			switch(argState) {
				case ARGS_RUN:
					je.run();
					break;
				case ARGS_HELP:
					System.out.println(getUsage());
					break;
				case ARGS_VERSION:
					System.out.println(getVersion());
					break;
				default:
					System.err.println("Invalid internal state! argState contains : " + argState);
					System.exit(1);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		System.exit(0);
	}
}
