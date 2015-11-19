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

import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.exception.SDMSException;

public class TimerUnit
{
	public static final String __version = "@(#) $Id: TimerUnit.java,v 2.1.2.1 2013/03/14 10:25:28 ronald Exp $";

	public static final int YEAR   = 5;
	public static final int MONTH  = 4;
	public static final int DAY    = 2;
	public static final int HOUR   = 1;
	public static final int MINUTE = 0;
	public static final int WEEK   = 3;

	public static final int[][] STANDARD_LENGTH = {

		{ 1                                      },
		{ 60,        1                           },
		{ 60*24,     24,     1                   },
		{ 60*24*7,   24*7,   7,   1              },
		{ 60*24*30,  24*30,  30,  5,   1         },
		{ 60*24*365, 24*365, 365, 52,  12,   1   }
	};

	private boolean isINF;
	private int mult;
	private int unit;

	public final boolean isINF()
	{
		return isINF;
	}

	public final int mult()
	{
		if (isINF) throw new IllegalStateException ("(04304041915) TimerUnit is INF");
		return mult;
	}
	public final int unit()
	{
		if (isINF) throw new IllegalStateException ("(04304041916) TimerUnit is INF");
		return unit;
	}

	public TimerUnit (final Integer mult, final Integer unit)
	{
		set (mult, unit);
	}

	public TimerUnit (final int mult, final int unit)
	{
		set (mult, unit);
	}

	public TimerUnit (final TimerUnit other)
	{
		set (other);
	}

	public final void set (final Integer mult, final Integer unit)
	{
		isINF = (mult == null) || (unit == null);

		if (isINF) {
			this.mult = 0;
			this.unit = -1;
		} else
			set (mult.intValue(), unit.intValue());
	}

	public final void set (final int mult, final int unit)
	{
		if ((unit != YEAR) && (unit != MONTH) && (unit != DAY) && (unit != HOUR) && (unit != MINUTE) && (unit != WEEK))
			throw new IllegalArgumentException ("(04304041917) invalid unit: " + unit);

		isINF     = false;
		this.mult = mult;
		this.unit = unit;
	}

	public final void set (final TimerUnit other)
	{
		isINF = other.isINF;
		mult  = other.mult;
		unit  = other.unit;
	}

	public final int compareTo (final TimerUnit otherUnit)
	{
		if (isINF)
			return otherUnit.isINF ? 0 : 1;
		else if (otherUnit.isINF)
			return -1;

		if (unit == otherUnit.unit)
			return mult - otherUnit.mult;

		final long myMinutes    = mult           * STANDARD_LENGTH [unit]           [TimerUnit.MINUTE];
		final long otherMinutes = otherUnit.mult * STANDARD_LENGTH [otherUnit.unit] [TimerUnit.MINUTE];
		final long diffMinutes = myMinutes - otherMinutes;

		return diffMinutes < 0 ? -1 : (diffMinutes > 0 ? 1 : 0);
	}

	public final String toString()
	{
		if (isINF)
			return "INF";

		switch (unit) {
		case YEAR:
			return mult + "Y";
		case MONTH:
			return mult + "M";
		case DAY:
			return mult + "D";
		case HOUR:
			return mult + "h";
		case MINUTE:
			return mult + "m";
		case WEEK:
			return mult + "W";
		}

		return mult + "?(" + unit + ")";
	}

	public final String asString()
	{
		if (isINF)
			return "INF";

		final String suffix = mult == 1 ? "" : "S";

		switch (unit) {
		case YEAR:
			return mult + " YEAR"   + suffix;
		case MONTH:
			return mult + " MONTH"  + suffix;
		case DAY:
			return mult + " DAY"    + suffix;
		case HOUR:
			return mult + " HOUR"   + suffix;
		case MINUTE:
			return mult + " MINUTE" + suffix;
		case WEEK:
			return mult + " WEEK"   + suffix;
		}

		return mult + " ???(" + unit + ")";
	}
}
