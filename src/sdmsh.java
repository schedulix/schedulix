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

import java.lang.*;
import java.util.*;
import java.io.*;

import de.independit.scheduler.shell.*;
import de.independit.scheduler.SDMSApp.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.exception.*;

public class sdmsh extends App
{
	public static final String __version = "@(#) $Id: sdmsh.java,v 2.16.4.1 2013/03/14 10:24:02 ronald Exp $";

	String host = "localhost";
	String user = null;
	String pass = null;
	int port = 2506;
	int timeout = -1;
	MiniParser mp = null;
	MiniScanner scanner = null;

	public static final String MYSILENT = "MYSILENT";
	public static final String MYTIMEOUT = App.CONNTIMEOUT;

	public void addOptions()
	{

		addOption("S" , null     , null     , MYSILENT, null, null     , false , "[No] (error) messages are printed (has preference over -s)");
		addOption(null, "timeout", "Timeout", MYTIMEOUT , null, "timeout", false , "Number of seconds after which the server will remove an idle session. 0 means no timeout.");
	}

	private static String getValue (final String prompt, final boolean visible)
	{
		try {
			if (!visible) {
				Console c = System.console();
				if (c != null) {
					System.out.print (prompt);
					String pw = new String(c.readPassword());
					return pw;
				} else {
					InputStreamReader isr = new InputStreamReader (System.in);
					BufferedReader br = new BufferedReader (isr);
					System.out.print (prompt);
					return br.readLine();
				}
			} else {
				InputStreamReader isr = new InputStreamReader (System.in);
				BufferedReader br = new BufferedReader (isr);
				System.out.print (prompt);
				return br.readLine();
			}
		} catch (final IOException e) {
			System.err.println ("FATAL ERROR while reading from stdin");
			System.exit (1);
		}

		return null;
	}

	public String getName()
	{
		return "sdmsh";
	}
	public boolean canRetry()
	{
		return false;
	}
	public String getUsageArguments()
	{
		return "user password host port";
	}

	public boolean specificParse()
	{

		if (options.isSet(MYSILENT)) {
			Option myo = options.getOption(MYSILENT);
			options.getOption(App.SILENT).set(myo);
			silent = myo.getBValue();
		}

		Option o;
		if (options.isSet(App.USER)) {
			user = options.getValue(App.USER);
			if (options.isSet(App.PASS)) pass = options.getValue(App.PASS);
		} else if (options.isSet(App.JID)) {
			user = options.getValue(App.JID);
			if (options.isSet(App.KEY)) pass = options.getValue(App.KEY);
		}
		if (options.isSet(App.HOST)) host = options.getValue(App.HOST);

		try {
			if (options.isSet(App.PORT)) port = Integer.parseInt(options.getValue(App.PORT));
		} catch (Exception e) {
			if (!silent) System.err.println("port be an integer !");
			return false;
		}
		try {
			if (options.isSet(MYTIMEOUT)) timeout = Integer.parseInt(options.getValue(MYTIMEOUT));
		} catch (Exception e) {
			if (!silent) System.err.println("timeout be an integer !");
			return false;
		}

		for (int i = 0; i < options.rest.size(); ++i) {

			switch (i) {
				case 0:
					user = (String) options.rest.get(i);
					o = options.getOption(App.USER);
					o.set(user, false);
					break;
				case 1:
					pass = (String) options.rest.get(i);
					o = options.getOption(App.PASS);
					o.set(pass, false);
					break;
				case 2:
					host = (String) options.rest.get(i);
					o = options.getOption(App.HOST);
					o.set(host, false);
					break;
				case 3:
					try {
						port = Integer.parseInt ((String) options.rest.get(i));
					} catch (Exception e) {
						if (!silent) System.err.println("port be an integer !");
						return false;
					}
					o = options.getOption(App.PORT);
					o.set((String) options.rest.get(i), false);
					break;
			}
		}

		if (!options.isSet(App.HELP) && !options.getOption(App.HELP).getBValue() && !(options.isSet(App.AUTH) && options.getValue(App.AUTH).equals("WINSSO"))) {
			if (user == null) {
				user = getValue ("USERNAME: ", true);
				o = options.getOption(App.USER);
				o.set(user, false);
			}
			if (pass == null) {
				pass = getValue ("PASSWORD: ", false);
				o = options.getOption(App.PASS);
				o.set(pass, false);
			}
		}

		if (System.console() == null && !options.isSet(App.VERBOSE)) {
			if (!silent) {
				o = options.getOption(App.VERBOSE);
				o.set(null, true);
				verbose = true;
			}
		}

		if (user != null)
			App.userName = user;
		return true;
	}

	public boolean validateOptions()
	{
		return true;
	}

	public void render(SDMSOutput o)
		throws SDMSException
	{
		((SDMSLineRenderer)(mp.getOutputRenderer())).setPrompt("[" + App.userName + "@" + host + ":" + port + "] SDMS> ");
		if (mp != null)	mp.render(o);
	}

	public boolean setupApp()
	{
		scanner = new MiniScanner (new InputStreamReader(System.in));
		mp = new MiniParser();
		SDMSLineRenderer renderer = new SDMSLineRenderer("[" + App.userName + "@" + host + ":" + port + "] SDMS> ");
		mp.setOutputRenderer (renderer);
		mp.setEnvInfo(host, port, user);
		mp.setPrintCmd(verbose);
		renderer.setSilent(silent);
		renderer.setVerbose(verbose);
		return true;
	}

	public int go()
		throws RetryException
	{
		try {
			mp.setServerConnection (serverConnection);
			try {
				mp.yyparse (scanner);
			} catch (final MiniParser.yyException ye) {
				if (!silent)
					System.out.println ("Oops : " + ye + " at line " + scanner.yyline());
			}
		} catch (final SDMSException e) {
			if (!silent)
				System.err.println("***ERROR*** " + e.getMessage());
			return 1;
		} catch (final IOException e) {
			if (!silent)
				System.err.println ("***ERROR*** " + e.getMessage());
			return 1;
		} catch (final Throwable t) {
			return 1;
		}
		return 0;
	}

	public static void main (String[] argv)
	{
		System.exit(new sdmsh().run(argv));
	}

}
