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

package de.independit.scheduler.SDMSApp;

import java.util.*;
import java.io.*;

public class Options
{
	public static final String __version = "@(#) $Id: Options.java,v 1.13.4.1 2013/03/14 10:24:03 ronald Exp $";

	final static int OPT_WIDTH = 36;
	int DOC_WIDTH = 43;

	private Vector options;
	public  Vector rest;

	public Options ()
	{
		this.options = new Vector();
		this.rest    = new Vector();
		String cols = System.getenv("COLUMNS");
		if (cols != null) {
			try {
				int c = Integer.parseInt(cols);
				if (c < 80) c = 80;
				DOC_WIDTH = c - 1 - OPT_WIDTH;
			} catch (NumberFormatException nfe) {

			}
		}
	}

	public void add (String shortopt, String longopt, String iniopt, String key, String defaultvalue, String valuestring, boolean mandatory, String doc)
	{
		boolean found = false;
		Option option = new Option (shortopt, longopt, iniopt, key, defaultvalue, valuestring, mandatory, doc);
		for (int i = 0; i < options.size(); i ++) {
			if (((Option)options.elementAt(i)).key.equals(key)) {
				options.setElementAt(option, i);
				found = true;
			}
		}
		if (!found) options.add(option);
	}

	public StringBuffer formatDoc(String doc)
	{
		final StringBuffer out = new StringBuffer();
		final StringBuffer d = new StringBuffer(doc);
		while (d.length() > DOC_WIDTH) {
			final int b = d.lastIndexOf(" ", DOC_WIDTH);
			out.append(d.substring(0, b));
			out.append("\n                                   ");
			d.delete(0, b);
		}
		out.append(d);
		return out;
	}

	public StringBuffer formatIniEntry(Option o)
	{
		StringBuffer b = new StringBuffer();
		b.append("  ");
		b.append(o.iniopt);
		b.append(" = ");
		if (o.valuestring != null) {
			b.append(o.valuestring);
		} else {
			b.append("<true|false>");
		}
		do b.append(" ");
		while (b.length() < OPT_WIDTH);
		b.append(formatDoc(o.doc));
		b.append("\n");
		return b;
	}

	public String list()
	{
		final StringBuffer out = new StringBuffer();
		final StringBuffer iniAlso = new StringBuffer();
		final StringBuffer iniOnly = new StringBuffer();

		final int size = options.size();
		for (int i = 0; i < size; ++i) {
			final Option o = (Option) options.get(i);
			if (o.longopt == null && o.shortopt == null) {
				iniOnly.append(formatIniEntry(o));
				continue;
			}

			final StringBuffer opt = new StringBuffer (o.mandatory ? "  " : "[ ");
			String offSwitch = "";
			if (o.isBoolean) offSwitch = "[no]";
			if (o.longopt != null) {
				opt.append("--");
				opt.append(offSwitch);
				opt.append(o.longopt);
			}
			if (o.shortopt != null) {
				if (o.longopt != null)
					opt.append("/");
				opt.append("-");
				opt.append(offSwitch);
				opt.append(o.shortopt);
			}

			if (o.valuestring != null) {
				opt.append(" ");
				opt.append(o.valuestring);
			}

			opt.append (o.mandatory ? "  " : " ]");
			while (opt.length() < OPT_WIDTH) opt.append(" ");
			out.append(opt);

			out.append(formatDoc(o.doc));

			if (o.iniopt != null) {
				out.append(" (*)");
				iniAlso.append(formatIniEntry(o));
			}

			out.append("\n");
		}

		if (iniAlso.length() + iniOnly.length() > 0) {
			if (iniAlso.length() > 0) {
				out.append("\nThe options marked with (*) can also be specified in a configuration\n");
				out.append("file. The following (case sensitive) entries are recognized:\n");
				out.append(iniAlso);
			}
			if (iniOnly.length() > 0) {
				out.append("\nThe following configuration entries are only valid in configuration\nfiles:\n");
				out.append(iniOnly);
			}
			out.append("\n");
			out.append("The configuration files are evaluated in the following order:\n");
			out.append("$BICSUITECONFIG/sdmshrc, $HOME/.sdmshrc and the file specified\n");
			out.append("on the commandline.\n");
			out.append("The first two files need not exist. If a file is specified, it must exist\n\n");
			out.append("Options specified on the commandline take precedence over the same options\n");
			out.append("specified in configuration files.\n");
			out.append("Options marked mandatory must not occur on the command line, if they are\n");
			out.append("specified in a configuration file.\n");
		}
		return out.toString();
	}

	public boolean validate()
	{
		Iterator i = options.iterator();
		while (i.hasNext()) {
			Option o = (Option) i.next();
			if (o.mandatory && !o.set) {
				System.err.println("Missing mandatory " + o.longopt + " option !");
				return false;
			}
			if (o.set && o.valuestring != null && o.value == null) {
				System.err.println("Option " + o.longopt + " requires a value !");
				return false;
			}
			if (!o.set && o.defaultvalue != null) {
				o.set = true;
				o.value = o.defaultvalue;
			}
		}
		return true;
	}

	public Vector getRest() { return rest; }

	public String getValue(String key)
	{
		Iterator i = options.iterator();
		while (i.hasNext()) {
			Option o = (Option) i.next();
			if (o.key.equals(key)) return o.value;
		}
		return null;
	}

	public Option getOption(String key)
	{
		Iterator i = options.iterator();
		while (i.hasNext()) {
			Option o = (Option) i.next();
			if (o.key.equals(key)) return o;
		}
		return null;
	}

	public boolean isSet(String key)
	{
		Iterator i = options.iterator();
		while (i.hasNext()) {
			Option o = (Option) i.next();
			if (o.key.equals(key)) return o.set;
		}
		return false;
	}

	public void evaluateInifile(String inifile, boolean ignoreError, String[] ignoreKeys)
	{
		try {
			InputStream ini = new FileInputStream (inifile);
			Properties props = new Properties();
			props.load (ini);

			Enumeration e = props.propertyNames();
			Option o = null;

MAIN:			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if (ignoreKeys != null)
					for (int i = 0; i < ignoreKeys.length; ++i) {
						if (key.equals(ignoreKeys[i]))
							continue MAIN;
					}
				Iterator oi = options.iterator();
				while (oi.hasNext()) {
					Option o1 = (Option) oi.next();
					if (key.equals(o1.iniopt) && !o1.set) {
						o1.set = true;
						o1.value = props.getProperty(key);
						if (o1.isBoolean) {
							o1.bvalue = Boolean.parseBoolean(props.getProperty(key));
						}
						break;
					}
				}
			}

		} catch (final IOException e) {
			if (!ignoreError) {
				System.err.println ("FATAL ERROR while processing startupfile " + inifile);
				System.exit (1);
			}
		}
	}

	public void parse(String[] argv)
	{
		for (int i = 0; i < argv.length; ++i) {
			boolean found = false;
			String arg = argv[i];
			Iterator oi = options.iterator();
			while (oi.hasNext()) {
				Option o1 = (Option) oi.next();

				if (o1.isBoolean)  {
					if (arg.equals("-" + o1.shortopt) || arg.equals("--" + o1.longopt)) {
						o1.set = true;
						o1.value = "true";
						o1.bvalue = true;
						found = true;
						break;
					}
					if (arg.equals("-no" + o1.shortopt) || arg.equals("--no" + o1.longopt)) {
						o1.set = true;
						o1.value = "false";
						o1.bvalue = false;
						found = true;
						break;
					}
				} else {
					if (arg.equals("-" + o1.shortopt) || arg.equals("--" + o1.longopt)) {
						o1.set = true;
						++i;
						if (i < argv.length) {
							o1.value = argv[i];
						} else {
						}
						found = true;
						break;
					}
				}
			}
			if (!found) rest.add(arg);
		}
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();
		Iterator oi = options.iterator();
		while (oi.hasNext()) {
			Option o = (Option) oi.next();
			result.append(o.toShortString());
			result.append("\n");
		}
		result.append("Rest:\n");
		for (int i = 0; i < rest.size(); ++i) {
			result.append ("\t" + rest.get(i) + "\n");
		}

		return result.toString();
	}
}
