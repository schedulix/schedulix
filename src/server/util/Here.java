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

public class Here
{
	public static String at ()
	{
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		String where = ste.getClassName() + "->" + ste.getMethodName() + "() at " + ste.getLineNumber() + " : ";
		return where;
	}

	public static String atc ()
	{
		StackTraceElement st[] = Thread.currentThread().getStackTrace();
		StackTraceElement sth = st[3];
		StackTraceElement ste = null;
		StackTraceElement stec = null;
		if (st.length > 3) {
			ste = st[4];
			if (st.length > 4)
				stec = st[5];
		}

		String where = (stec != null ? stec.getMethodName() + "(" + stec.getLineNumber() + ")" : "null" ) +
		               (ste != null ? " -> " + ste.getMethodName() + "(" + ste.getLineNumber() + ")" : "null") + " -> " +
		               sth.getMethodName() + "(" + sth.getLineNumber() + ") => ";
		return where;
	}
}

