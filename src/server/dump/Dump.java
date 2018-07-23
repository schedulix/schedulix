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
package de.independit.scheduler.server.dump;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public abstract class Dump
{
	public static final String __version = "@(#) $Id: Dump.java,v 2.14.14.2 2013/03/14 10:24:11 ronald Exp $";

	static final boolean isSystemObject (final Long id)
	{
		final long i = id.longValue();

		return (i >= 0) && (i < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY);
	}

	public static final String quotedString (final String str)
	{
		StringBuffer buf = new StringBuffer (str);

		for (int i = buf.length() - 1; i >= 0; --i)
			switch (buf.charAt (i)) {
			case '\'':
			case '\\':
				buf.insert (i, '\\');
			}

		buf.insert (0, '\'');
		buf.append ('\'');

		return new String (buf);
	}

	public static final String quotedName (final Long id, final String name)
	{
		return isSystemObject (id) ? name.toUpperCase() : quotedString (name);
	}

	public static final String join (final String sep, final Vector vec)
	{
		if (vec == null)
			return null;

		if (vec.isEmpty())
			return "";

		final int size = vec.size();

		String res = vec.get (0).toString();
		for (int i = 1; i < size; ++i)
			res += sep + vec.get (i).toString();

		return res;
	}

}
