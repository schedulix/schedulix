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

import java.util.Vector;

import de.independit.scheduler.server.output.*;

public final class Trace
{
	public static final String __version = "@(#) $Id: Trace.java,v 2.2.16.1 2013/03/14 10:24:07 ronald Exp $";

	public static final int FATAL   = -2;
	public static final int ERROR   = -1;
	public static final int INFO    =  0;
	public static final int WARNING =  1;
	public static final int MESSAGE =  2;
	public static final int DEBUG   =  3;

	public static final int DEFAULT = WARNING;

	private static int level = DEFAULT;

	public static final void setLevel (final int newLevel)
	{
		level = newLevel;
	}

	private static final String levelString (final int level)
	{
		switch (level) {
		case FATAL:
			return "FATAL  ";
		case ERROR:
			return "ERROR  ";
		case INFO:
			return "INFO   ";
		case WARNING:
			return "WARNING";
		case DEBUG:
			return "DEBUG  ";
		case MESSAGE:
			return "MESSAGE";
		}

		return "UNKNOWN";
	}

	private static final void trace (final String msg, final int msg_level)
	{
		if (msg_level <= level) {
			final String ts = Utils.timestampNow();
			System.err.println (levelString (msg_level) + " [Jobserver]\t" + ts.substring (1, ts.length() - 1) + " " + msg);
		}
	}

	public static final void fatal   (final String msg)
	{
		trace (msg, FATAL);
	}
	public static final void error   (final String msg)
	{
		trace (msg, ERROR);
	}
	public static final void info    (final String msg)
	{
		trace (msg, INFO);
	}
	public static final void warning (final String msg)
	{
		trace (msg, WARNING);
	}
	public static final void debug   (final String msg)
	{
		trace (msg, DEBUG);
	}
	public static final void message (final String msg)
	{
		trace (msg, MESSAGE);
	}

	private static final String dump_field (final Object obj)
	{
		if (obj == null)
			return "<null>";

		if (obj instanceof String)
			return "\"" + obj + "\"";

		if (obj instanceof SDMSOutputContainer)
			return dump_table ((SDMSOutputContainer) obj);

		if (obj instanceof Vector) {
			final Vector v = (Vector) (obj);
			final StringBuffer s = new StringBuffer ("[");
			for (int i = 0; i < v.size(); ++i) {
				if (i != 0)
					s.append (", ");
				s.append (dump_field (v.get (i)));
			}
			s.append ("]");
			return s.toString();
		}

		return obj.toString();
	}

	private static final String dump_record (final SDMSOutputContainer c)
	{
		final Vector v = (Vector) (c.dataset.get (0));

		final StringBuffer s = new StringBuffer ("record=[");
		for (int i = 0; i < c.desc.size(); ++i) {
			if (i != 0)
				s.append (", ");
			s.append (((SDMSOutputLabel) c.desc.get (i)).name);
			s.append ("=");
			s.append (dump_field (i < v.size() ? v.get (i) : null));
		}

		s.append ("]");
		return s.toString();
	}

	private static final String dump_table (final SDMSOutputContainer c)
	{
		final int size = c.dataset.size();
		int count = 0;

		final StringBuffer s = new StringBuffer ("table=[");
		for (int i = 0; i < size; ++i) {
			if (count != 0)
				s.append (", ");

			s.append ("#");
			s.append (count++);
			s.append ("=[");
			final Vector v = (Vector) (c.dataset.get (i));
			for (int j = 0; j < c.desc.size(); ++j) {
				if (j != 0)
					s.append (", ");
				s.append (((SDMSOutputLabel) c.desc.get (j)).name);
				s.append ("=");
				s.append (dump_field (v.get (j)));
			}
			s.append ("]");
		}

		s.append ("]");
		return s.toString();
	}

	public static final String dump (final SDMSOutput o)
	{
		final StringBuffer s = new StringBuffer();
		if (o.container != null) {
			s.append ("container=[");

			if (o.container.title != null) {
				s.append ("title=");
				s.append (dump_field (o.container.title));
				s.append (", ");
			}

			if (o.container.columns == 0)
				s.append (dump_record (o.container));
			else
				s.append (dump_table (o.container));

			s.append ("]");
		}

		if (o.feedback != null) {
			if (s.length() != 0)
				s.append (", ");
			s.append ("feedback=");
			s.append (dump_field (o.feedback));
		}

		if (o.error != null) {
			if (s.length() != 0)
				s.append (", ");
			s.append ("error=[code=");
			s.append (dump_field (o.error.code));
			s.append (", message=");
			s.append (dump_field (o.error.message));
			s.append ("]");
		}

		return s.toString();
	}

	private Trace()
	{

	}
}
