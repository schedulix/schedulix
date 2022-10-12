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
package de.independit.scheduler.server.timer;

import java.util.*;
import java.text.SimpleDateFormat;

import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.exception.SDMSException;
import de.independit.scheduler.server.util.DateTime;

public class TimerDate
	extends Date
{
	public static final String __version = "@(#) $Id: TimerDate.java,v 2.4.2.1 2013/03/14 10:25:28 ronald Exp $";

	public static final int NaD = Integer.MIN_VALUE;

	private static final GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();

	private boolean isNaD;

	public final boolean isNaD() { return isNaD; }

	static {
		gc.setTimeZone (TimeZone.getDefault());
	}

	public TimerDate()
	{
		isNaD = true;
	}

	public TimerDate (final int minutes)
	{
		super (toMillis (minutes));
		isNaD = minutes == NaD;
	}

	public TimerDate (final long minutes)
	{
		super (minutes * 60 * 1000);
		isNaD = minutes == NaD;
	}

	public TimerDate (final Date date)
	{
		super (date.getTime());
		isNaD = false;
	}

	public TimerDate (final TimerDate date)
	{
		super (date.getTime());
		set (date);
	}

	public static final long toMillis (final int minutes)
	{
		return ((long) minutes) * (60 * 1000);
	}

	public static final int fromMillis (final long millis)
	{
		return (int) (millis / (60 * 1000));
	}

	public final boolean eq (final TimerDate when) { return (isNaD || when.isNaD) ? false :   equals (when); }
	public final boolean ne (final TimerDate when) { return (isNaD || when.isNaD) ? true  : ! equals (when); }

	public final boolean gt (final TimerDate when) { ensureNoNaD (when); return   after  (when); }
	public final boolean ge (final TimerDate when) { ensureNoNaD (when); return ! before (when); }
	public final boolean lt (final TimerDate when) { ensureNoNaD (when); return   before (when); }
	public final boolean le (final TimerDate when) { ensureNoNaD (when); return ! after  (when); }

	private final void ensureNoNaD (final TimerDate when)
	{
		if (isNaD)
			throw new IllegalStateException ("(04304041900) TimerDate is NaD");
		if (when.isNaD)
			throw new IllegalArgumentException ("(04304041901) TimerDate is NaD");
	}

	public final TimerDate set (final int minutes)
	{
		isNaD = minutes == NaD;
		if (! isNaD)
			setTime (toMillis (minutes));
		return this;
	}

	public final TimerDate set (final TimerDate other)
	{
		isNaD = other.isNaD;
		if (! isNaD)
			setTime (other.getTime());
		return this;
	}

	public final int toMinutes()
	{
		return isNaD ? NaD : fromMillis (getTime());
	}

	public final int plus (final int minutes)
	{
		return isNaD ? NaD : (toMinutes() + minutes);
	}

	public final int plus (final TimerUnit incr)
	{
		return plus(1, incr);
	}

	public final int plus (final int mult, final TimerUnit incr)
	{
		if (incr.isINF())
			throw new IllegalArgumentException ("(04304041904) TimerUnit is INF");

		if (isNaD)
			return toMinutes();

		synchronized (gc) {
			gc.setTime (this);

			switch (incr.unit()) {
			case TimerUnit.YEAR:   gc.add (Calendar.YEAR,         mult * incr.mult()); break;
			case TimerUnit.MONTH:  gc.add (Calendar.MONTH,        mult * incr.mult()); break;
			case TimerUnit.WEEK:   gc.add (Calendar.WEEK_OF_YEAR, mult * incr.mult()); break;
			case TimerUnit.DAY:    gc.add (Calendar.DAY_OF_MONTH, mult * incr.mult()); break;
			case TimerUnit.HOUR:   gc.add (Calendar.HOUR_OF_DAY,  mult * incr.mult()); break;
			case TimerUnit.MINUTE: gc.add (Calendar.MINUTE,       mult * incr.mult()); break;
			default:
				throw new IllegalArgumentException ("(04304041905) unexpected TimerUnit: " + incr);
			}

			return fromMillis (gc.getTimeInMillis());
		}
	}

	public final String toString()
	{
		return toString (SystemEnvironment.systemTimeZone);
	}

	private static final SimpleDateFormat df = new SimpleDateFormat ("dd'.'MM'.'yyyy'-'HH':'mm", SystemEnvironment.systemLocale);

	public final String toString (final TimeZone tz)
	{
		if (isNaD)
			return "NaD";

		synchronized (df) {
			df.setTimeZone (tz);

			return df.format (this) + " " + tz.getID();
		}
	}
}
