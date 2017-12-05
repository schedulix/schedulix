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

import java.util.*;
import java.text.DecimalFormat;

import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.exception.SDMSEscape;

public class DateTime
{
	public static final String __version = "@(#) $Id: DateTime.java,v 2.12.2.1 2013/03/14 10:25:29 ronald Exp $";

	public static final boolean IS_DURATION = true;

	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	private boolean isDuration;
	private boolean isFixed = false;
	private TimeZone tz;
	private final GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
	private boolean gcValid = false;
	private boolean secondsSuppressed = false;

	public int year   = -1;
	public int month  = -1;
	public int week   = -1;
	public int day    = -1;
	public int hour   = -1;
	public int minute = -1;
	public int second = -1;

	private static final long SECONDS_SUPPRESSED = 0x0008000000000000L;
	private static final long GMT_VALID          = 0x0004000000000000L;
	private static final long DURATION_VALID     = 0x0002000000000000L;
	private static final long YEAR_VALID         = 0x0001000000000000L;
	private static final long MONTH_VALID        = 0x0000000200000000L;
	private static final long WEEK_VALID         = 0x0000000204000000L;
	private static final long DAY_VALID          = 0x0000000002000000L;
	private static final long HOUR_VALID         = 0x0000000000080000L;
	private static final long MINUTE_VALID       = 0x0000000000002000L;
	private static final long SECOND_VALID       = 0x0000000000000040L;

	private static final long GMT_SHIFT    =  0;
	private static final long YEAR_SHIFT   = 34;
	private static final long MONTH_SHIFT  = 29;
	private static final long WEEK_SHIFT   = 27;
	private static final long DAY_SHIFT    = 20;
	private static final long HOUR_SHIFT   = 14;
	private static final long MINUTE_SHIFT =  7;
	private static final long SECOND_SHIFT =  0;

	private static final long GMT_MASK    = 0x3ffffffffffffL;
	private static final long YEAR_MASK   = 0x3fffL;
	private static final long MONTH_MASK  = 0xfL;
	private static final long WEEK_MASK   = 0x3fL;
	private static final long DAY_MASK    = 0x1fL;
	private static final long HOUR_MASK   = 0x1fL;
	private static final long MINUTE_MASK = 0x3fL;
	private static final long SECOND_MASK = 0x3fL;

	private static final int YEAR   = 1;
	private static final int MONTH  = 2;
	private static final int DAY    = 3;
	private static final int HOUR   = 4;
	private static final int MINUTE = 5;
	private static final int SECOND = 6;

	private static final DateTime MIN_VALUE = new DateTime (new Long (GMT_VALID | (0L << GMT_SHIFT)));

	public static final DateTime ZERO = new DateTime (new Long (DURATION_VALID | MINUTE_VALID));

	private final int parseNumber (final String str, final String what, final int ofs, final int len, final int min, final int max)
		throws SDMSEscape
	{
		if (! str.substring (ofs).matches ("^\\d{" + len + "}.*"))
			return -1;

		final String part = str.substring (ofs, ofs + len);

		try {
			final int value = Integer.parseInt (part, 10);
			if ((value < min) || (value > max))
				throw new SDMSEscape ("(04304091319) " + what + ' ' + value + " is out of range " + min + '-' + max);

			return value;
		} catch (final NumberFormatException e) {
			throw new SDMSEscape ("(04304091320) invalid number: " + part);
		}
	}

	private final boolean parseStr (final String str, final String pattern)
		throws SDMSEscape
	{
		return parseStr(str, pattern, false);
	}

	private final boolean parseStr (final String str, final String pattern, boolean ignoreTz)
		throws SDMSEscape
	{
		tz     = ignoreTz ? GMT : TimeZone.getDefault();
		year   = -1;
		month  = -1;
		week   = -1;
		day    = -1;
		hour   = -1;
		minute = -1;
		second = -1;

		int i = 0;
		while (i < pattern.length()) {
			final char p = pattern.charAt (i);
			switch (p) {
			case 'Y':
				year = parseNumber (str, "year", i, 4, 0, 9999);
				if (year == -1)
					return false;
				i += 4;
				break;

			case 'M':
				month = parseNumber (str, "month", i, 2, 1, 12);
				if (month == -1)
					return false;
				i += 2;
				break;

			case 'D':
				day = parseNumber (str, "day of month", i, 2, 1, 31);
				if (day == -1)
					return false;
				i += 2;
				break;

			case 'w':
				week = parseNumber (str, "week of year", i, 2, 1, 53);
				if (week == -1)
					return false;
				i += 2;
				break;

			case 'h':
				hour = parseNumber (str, "hour", i, 2, 0, 23);
				if (hour == -1)
					return false;
				i += 2;
				break;

			case 'm':
				minute = parseNumber (str, "minute", i, 2, 0, 59);
				if (minute == -1)
					return false;
				i += 2;
				break;

			case 's':
				second = parseNumber (str, "second", i, 2, 0, 59);
				if (second == -1)
					return false;
				i += 2;
				break;

			case 'z':
				if (str.charAt (i) != ' ')
					return false;
				++i;

				final String tzID = str.substring (i);
				if (!ignoreTz) {
					tz = TimeZone.getTimeZone (tzID);
					if (! tz.getID().equals (tzID))
						throw new SDMSEscape ("(04304091448) unknown timezone: \"" + tzID + '"');
				}
				i += tzID.length();
				break;

			default: // separators, tags, etc.
				if (str.length() <= i) return false;
				if (Character.toUpperCase (str.charAt (i)) != Character.toUpperCase (p))
					return false;
				++i;
			}
		}

		if (i != str.length())
			return false;

		if (day != -1) {
			final int maxdays;
			switch (month) {
			case -1:
				maxdays = 31;
				break;

			case 2:
				if (year == -1)
					maxdays = 29;
				else {
					gc.setTimeZone (tz);
					maxdays = gc.isLeapYear (year) ? 29 : 28;
				}
				break;

			case 4:
			case 6:
			case 9:
			case 11:
				maxdays = 30;
				break;

			default:
				maxdays = 31;
			}

			if ((day > maxdays))
				throw new SDMSEscape ("(04304091331) day of month " + day + " is out of range 1-" + maxdays);
		}

		return true;
	}

	//------------------------------------------------------------------------------------------
	// Constructs a newly allocated DateTime object so that it represents the date specified by str.
	// If isDuration is true, the resulting DateTime is a duration (that has nothing to do with timezone's and must not be >= MIN_VALUE)

	public DateTime (final String str, final boolean isDuration)
		throws SDMSEscape
	{
		this.isDuration = isDuration;
		interpretStr(str, isDuration, false);
	}

	public DateTime (final String str, final boolean isDuration, final boolean ignoreTz)
		throws SDMSEscape
	{
		this.isDuration = isDuration;
		interpretStr(str, isDuration, ignoreTz);
	}

	private void interpretStr(final String str, final boolean isDuration, final boolean ignoreTz)
		throws SDMSEscape
	{
		boolean good =
			parseStr    (str, "YYYY", ignoreTz)
			|| parseStr (str, "YYYYMM", ignoreTz)		|| parseStr (str, "YYYY-MM", ignoreTz)
			|| parseStr (str, "YYYYMMDD", ignoreTz)		|| parseStr (str, "YYYY-MM-DD", ignoreTz)
			|| parseStr (str, "YYYYMMDDThh", ignoreTz)	|| parseStr (str, "YYYY-MM-DDThh", ignoreTz)
			|| parseStr (str, "YYYYMMDDThhmm", ignoreTz)	|| parseStr (str, "YYYY-MM-DDThh:mm", ignoreTz)
			|| parseStr (str, "YYYYMMDDThhmmss", ignoreTz)	|| parseStr (str, "YYYY-MM-DDThh:mm:ss", ignoreTz)

			|| parseStr (str, "-MM", ignoreTz)
			|| parseStr (str, "-MMDD", ignoreTz)		|| parseStr (str, "-MM-DD", ignoreTz)
			|| parseStr (str, "-MMDDThh", ignoreTz)		|| parseStr (str, "-MM-DDThh", ignoreTz)
			|| parseStr (str, "-MMDDThhmm", ignoreTz)	|| parseStr (str, "-MM-DDThh:mm", ignoreTz)
			|| parseStr (str, "-MMDDThhmmss", ignoreTz)	|| parseStr (str, "-MM-DDThh:mm:ss", ignoreTz)

			|| parseStr (str, "--DD", ignoreTz)
			|| parseStr (str, "--DDThh", ignoreTz)
			|| parseStr (str, "--DDThhmm", ignoreTz)	|| parseStr (str, "--DDThh:mm", ignoreTz)
			|| parseStr (str, "--DDThhmmss", ignoreTz)	|| parseStr (str, "--DDThh:mm:ss", ignoreTz)

			|| parseStr (str, "Thh", ignoreTz)
			|| parseStr (str, "Thhmm", ignoreTz)		|| parseStr (str, "Thh:mm", ignoreTz)
			|| parseStr (str, "Thhmmss", ignoreTz)		|| parseStr (str, "Thh:mm:ss", ignoreTz)

			|| parseStr (str, "T-mm", ignoreTz)		|| parseStr (str, "T-mm", ignoreTz)
			|| parseStr (str, "T-mmss", ignoreTz)		|| parseStr (str, "T-mm:ss", ignoreTz)

			|| parseStr (str, "T--ss", ignoreTz)		|| parseStr (str, "T--ss", ignoreTz)

			|| parseStr (str, "YYYYWww", ignoreTz)
			|| parseStr (str, "Www", ignoreTz);

		if (! good) {
			good =
				parseStr    (str, "YYYYz", ignoreTz)
				|| parseStr (str, "YYYYMMz", ignoreTz)		|| parseStr (str, "YYYY-MMz", ignoreTz)
				|| parseStr (str, "YYYYMMDDz", ignoreTz)	|| parseStr (str, "YYYY-MM-DDz", ignoreTz)
				|| parseStr (str, "YYYYMMDDThhz", ignoreTz)	|| parseStr (str, "YYYY-MM-DDThhz", ignoreTz)
				|| parseStr (str, "YYYYMMDDThhmmz", ignoreTz)	|| parseStr (str, "YYYY-MM-DDThh:mmz", ignoreTz)
				|| parseStr (str, "YYYYMMDDThhmmssz", ignoreTz)	|| parseStr (str, "YYYY-MM-DDThh:mm:ssz", ignoreTz)

				|| parseStr (str, "YYYYWwwz", ignoreTz);

			if (good && isDuration)
				throw new SDMSEscape ("(04304091425) durations cannot have a timezone: \"" + str + '"');
		}

		if (! good)
			throw new SDMSEscape ("(04304091318) invalid format: \"" + str + '"');

		isFixed = isDuration || (year == -1);

		if (! (isDuration || (year == -1) || (compareTo (MIN_VALUE) >= 0)))
			throw new SDMSEscape ("(04304091321) Date is too early: " + this + " (must be at least " + MIN_VALUE + ")");
	}

	//------------------------------------------------------------------------------------------
	// Constructs a newly allocated DateTime object so that it represents the specified date (which is not a duration)
	//------------------------------------------------------------------------------------------

	public DateTime (final String str)
		throws SDMSEscape
	{
		this (str, ! IS_DURATION);
	}

	//-------------------------------------------------------------------------
	// Fills the public fields (year, ..., second) from the current value in gc
	//-------------------------------------------------------------------------

	private final void setFieldsFromGC()
	{
		gc.setTimeZone (tz);

		year   = gc.get (Calendar.YEAR);
		month  = gc.get (Calendar.MONTH) + 1;
		week   = -1;
		day    = gc.get (Calendar.DAY_OF_MONTH);
		hour   = gc.get (Calendar.HOUR_OF_DAY);
		minute = gc.get (Calendar.MINUTE);
		second = gc.get (Calendar.SECOND);
	}

	//-------------------------------------------------------------------------
	// Fills the public fields (year, ..., second) from the current value in gc
	//-------------------------------------------------------------------------

	public final void setMissingFieldsFromNow()
	{
		setMissingFieldsFromNow(false);
	}

	public final void setMissingFieldsFromNow(boolean adjust)
	{
		long now = System.currentTimeMillis();
		java.util.Date d = new java.util.Date(now);

		setMissingFieldsFromReference(d, adjust);
	}

	public final void setMissingFieldsFromReference(java.util.Date refDate, boolean adjust)
	{
		// adjust == true means that the calculated time
		//                will be in the future, related to refDate, but only
		//		  if the most significant values are missing
		//                i.e. '--3T17:00' will be corrected
		//                     '1970' will not

		gc.setTimeZone (tz);
		GregorianCalendar refGC = new GregorianCalendar(tz);
		GregorianCalendar newGC = new GregorianCalendar(tz);
		refGC.setTime(refDate);
		newGC.setTime(refDate);
		DateTime tmp = new DateTime(0L);
		tmp.set(this);

		int levelToAdjust = 0;
		if (adjust) {
			if (year == -1) {
				levelToAdjust = YEAR;
				if (month == -1) {
					levelToAdjust = MONTH;
					if (day == -1) {
						levelToAdjust = DAY;
						if (hour == -1) {
							levelToAdjust = HOUR;
							if (minute == -1)
								levelToAdjust = MINUTE;
						}
					}
				}
			}
			switch (levelToAdjust) {
				case YEAR:
					newGC.add(Calendar.YEAR, 1);
					break;
				case MONTH:
					newGC.add(Calendar.MONTH, 1);
					break;
				case DAY:
					newGC.add(Calendar.DAY_OF_MONTH, 1);
					break;
				case HOUR:
					newGC.add(Calendar.HOUR_OF_DAY, 1);
					break;
				case MINUTE:
					newGC.add(Calendar.MINUTE, 1);
					break;
			}
		}

		if (year == -1) {
			year   = refGC.get(Calendar.YEAR);
			tmp.year = newGC.get(Calendar.YEAR);
		} else {
			if (month == -1) month = 1;
		}
		if (month == -1) {
			month  = refGC.get(Calendar.MONTH) + 1;
			tmp.month = newGC.get(Calendar.MONTH) + 1;
		} else {
			if (day == -1) day = 1;
		}
		if (day == -1) {
			day    = refGC.get(Calendar.DAY_OF_MONTH);
			tmp.day = newGC.get(Calendar.DAY_OF_MONTH);
		} else {
			if (hour == -1) hour = 0;
		}
		if (hour == -1)	{
			hour = refGC.get(Calendar.HOUR_OF_DAY);
			tmp.hour = newGC.get(Calendar.HOUR_OF_DAY);
		} else {
			if (minute == -1) minute = 0;
		}
		if (minute == -1) {
			minute = refGC.get(Calendar.MINUTE);
			tmp.minute = newGC.get(Calendar.MINUTE);
		} else {
			if (second == -1) second = 0;
		}
		if (second == -1) {
			second = refGC.get(Calendar.SECOND);
			tmp.second = newGC.get(Calendar.SECOND);
		}
		week = -1;
		tmp.week = -1;

		loadGC();
		tmp.loadGC();

		if (refDate.compareTo(toDate()) > 0) {
			// if refDate larger than the calculated date
			// we use the adjusted calculated date
			// if adjust == false then newGC == refGC
			set(tmp);
		}
	}

	//------------------------------------------------------------------------------------------
	// Constructs a newly allocated DateTime object so that it represents the specified date (which is not a duration)
	//------------------------------------------------------------------------------------------

	public DateTime (final Date date)
	{
		isDuration = false;

		gc.setTime (date);
		tz = SystemEnvironment.systemTimeZone;

		gcValid = true;
		isFixed = true;

		if (gcValid)
			setFieldsFromGC();
	}

	//------------------------------------------------------------------------------------------
	// Constructs a newly allocated DateTime object so that it represents the specified DateTime
	//------------------------------------------------------------------------------------------

	public DateTime (final DateTime other)
	{
		set (other);
	}

	//------------------------------------------------------------------------------------------
	// Constructs a newly allocated DateTime object so that it represents the date specified by the long value
	//------------------------------------------------------------------------------------------
	public DateTime (final long l)
	{
		setValues(l, true);
	}

	public DateTime (final Long lng)
	{
		setValues(lng.longValue(), true);
	}

	public DateTime (final Long lng, boolean defaultTimeZone)
	{
		setValues(lng.longValue(), defaultTimeZone);
	}

	private void setValues(final long l, boolean defaultTimeZone)
	{
		isDuration = (l & DURATION_VALID) == DURATION_VALID;

		if (defaultTimeZone)
			tz = TimeZone.getDefault();
		else
			tz = GMT;

		gcValid = (l & GMT_VALID) == GMT_VALID;
		if (gcValid) {
			gc.setTimeInMillis ((l >> GMT_SHIFT) & GMT_MASK);
			setFieldsFromGC();
		} else {
			year   = ((l & YEAR_VALID)   != YEAR_VALID)   ? -1 : (int) ((l >> YEAR_SHIFT)   & YEAR_MASK);
			day    = ((l & DAY_VALID)    != DAY_VALID)    ? -1 : (int) ((l >> DAY_SHIFT)    & DAY_MASK);
			hour   = ((l & HOUR_VALID)   != HOUR_VALID)   ? -1 : (int) ((l >> HOUR_SHIFT)   & HOUR_MASK);
			minute = ((l & MINUTE_VALID) != MINUTE_VALID) ? -1 : (int) ((l >> MINUTE_SHIFT) & MINUTE_MASK);
			second = ((l & SECOND_VALID) != SECOND_VALID) ? -1 : (int) ((l >> SECOND_SHIFT) & SECOND_MASK);

			if ((l & WEEK_VALID) == WEEK_VALID) {
				week  = (int) ((l >> WEEK_SHIFT) & WEEK_MASK);
				month = -1;
			} else {
				week  = -1;
				month = ((l & MONTH_VALID) != MONTH_VALID) ? -1 : (int) ((l >> MONTH_SHIFT) & MONTH_MASK);
			}
		}

		secondsSuppressed = (l & SECONDS_SUPPRESSED) == SECONDS_SUPPRESSED;

		isFixed = true;
	}

	//------------------------------------------------------------------------------------------
	// Sets this DateTime object so that it represents the specified DateTime
	//------------------------------------------------------------------------------------------

	public final void set (final DateTime other)
	{
		isDuration = other.isDuration;
		isFixed    = other.isFixed;

		gcValid    = other.gcValid;
		if (gcValid)
			gc.setTime (other.gc.getTime());
		tz = other.tz;
		secondsSuppressed = other.secondsSuppressed;

		year   = other.year;
		month  = other.month;
		week   = other.week;
		day    = other.day;
		hour   = other.hour;
		minute = other.minute;
		second = other.second;
	}

	//------------------------------------------------------------------------------------------
	// Loads gc with the current values
	//------------------------------------------------------------------------------------------

	public final void loadGC()
	{
		gc.setTimeZone (tz);

		gc.clear();

		gc.set (Calendar.YEAR, year);
		if (week != -1) {
			gc.set (Calendar.WEEK_OF_YEAR, week);
			gc.set (Calendar.HOUR_OF_DAY,  0);
			gc.set (Calendar.MINUTE,       0);
			gc.set (Calendar.SECOND,       0);
		} else {
			if (month  != -1) gc.set (Calendar.MONTH,        month - 1);
			if (day    != -1) gc.set (Calendar.DAY_OF_MONTH, day);
			if (hour   != -1) gc.set (Calendar.HOUR_OF_DAY,  hour);
			if (minute != -1) gc.set (Calendar.MINUTE,       minute);
			if (second != -1) gc.set (Calendar.SECOND,       second);
		}
		gc.set (Calendar.MILLISECOND, 0);

		gcValid = true;
	}

	//------------------------------------------------------------------------------------------
	// Adjusts this DateTime so that it represents the earliest possible date
	//------------------------------------------------------------------------------------------

	public final void fixToMinDate()
	{
		if (isDuration)
			throw new IllegalStateException ("(04304091942) Durations cannot be fixed");
		if (isFixed) {
			if (year == -1)
				return;
			throw new IllegalStateException ("(04304092046) DateTime is already fixed");
		}

		loadGC();

		isFixed = true;
	}

	//------------------------------------------------------------------------------------------
	// Adjusts this DateTime so that it represents the latest possible date
	//------------------------------------------------------------------------------------------

	public final void fixToMaxDate()
	{
		if (isDuration)
			throw new IllegalStateException ("(04304092053) Durations cannot be fixed");
		if (isFixed) {
			if (year == -1)
				return;
			throw new IllegalStateException ("(04304092054) DateTime is already fixed");
		}

		loadGC();

		if (second == -1) {
			if (! secondsSuppressed)
				gc.set (Calendar.SECOND, gc.getActualMaximum (Calendar.SECOND));

			if (minute == -1) {
				gc.set (Calendar.MINUTE, gc.getActualMaximum (Calendar.MINUTE));

				if (hour == -1) {
					gc.set (Calendar.HOUR_OF_DAY, gc.getActualMaximum (Calendar.HOUR_OF_DAY));

					if (week != -1)
						gc.add (Calendar.DAY_OF_MONTH, 6);
					else if (day == -1) {
						gc.set (Calendar.DAY_OF_MONTH, gc.getActualMaximum (Calendar.DAY_OF_MONTH));

						if (month == -1)
							gc.set (Calendar.MONTH, gc.getActualMaximum (Calendar.MONTH));
					}
				}
			}
		}

		isFixed = true;
	}

	//------------------------------------------------------------------------------------------
	// Removes this DateTime's seconds and returns whether it was necessary (ie. the seconds were set before)
	//------------------------------------------------------------------------------------------

	public final boolean suppressSeconds()
	{
		if (secondsSuppressed)
			return false;

		boolean needed = false;

		if (gcValid) {
			needed = (gc.isSet (Calendar.SECOND)) && (gc.get (Calendar.SECOND) != 0);
			if (needed)
				gc.set (Calendar.SECOND, 0);
		}

		if (second != -1) {
			needed = true;
			second = -1;
		}

		secondsSuppressed = true;

		return needed;
	}

	//------------------------------------------------------------------------------------------
	// Compares two DateTimes for ordering.
	//------------------------------------------------------------------------------------------

	private static final void fillGC (final DateTime dt)
	{
		dt.gc.setTimeZone (dt.tz);
		dt.gc.clear();
		if (dt.year != -1) dt.gc.set (Calendar.YEAR, dt.year);
		if (dt.week != -1) {
			dt.gc.set (Calendar.WEEK_OF_YEAR, dt.week);
			dt.gc.set (Calendar.HOUR_OF_DAY,  0);
			dt.gc.set (Calendar.MINUTE,       0);
			dt.gc.set (Calendar.SECOND,       0);
		} else {
			if (dt.month  != -1) dt.gc.set (Calendar.MONTH,        dt.month - 1);
			if (dt.day    != -1) dt.gc.set (Calendar.DAY_OF_MONTH, dt.day);
			if (dt.hour   != -1) dt.gc.set (Calendar.HOUR_OF_DAY,  dt.hour);
			if (dt.minute != -1) dt.gc.set (Calendar.MINUTE,       dt.minute);
			if (dt.second != -1) dt.gc.set (Calendar.SECOND,       dt.second);
		}
		dt.gc.set (Calendar.MILLISECOND, 0);
	}

	public final long compareTo (final DateTime other)
	{
		if (! gcValid)
			fillGC (this);
		if (! other.gcValid)
			fillGC (other);

		return gc.getTimeInMillis() - other.gc.getTimeInMillis();
	}

	//------------------------------------------------------------------------------------------
	// Returns a newly allocated Long object that represents this DateTime's value.
	//------------------------------------------------------------------------------------------

	public final Long toLong()
	{
		if (! isFixed)
			throw new IllegalStateException ("(04304091916) DateTime is not fixed");

		long result = 0;

		if (gcValid) {
			result |= GMT_VALID;
			result |= gc.getTimeInMillis();
		} else {
			if (isDuration)
				result |= DURATION_VALID;

			if (year != -1)
				result |= YEAR_VALID | ((long) year << YEAR_SHIFT);
			if (day != -1)
				result |= DAY_VALID | ((long) day << DAY_SHIFT);
			if (hour != -1)
				result |= HOUR_VALID | ((long) hour << HOUR_SHIFT);
			if (minute != -1)
				result |= MINUTE_VALID | ((long) minute << MINUTE_SHIFT);
			if (second != -1)
				result |= SECOND_VALID | ((long) second << SECOND_SHIFT);

			if (month != -1)
				result |= MONTH_VALID | ((long) month << MONTH_SHIFT);
			else if (week  != -1)
				result |= WEEK_VALID | ((long) week << WEEK_SHIFT);
		}

		if (secondsSuppressed)
			result |= SECONDS_SUPPRESSED;

		return new Long (result);
	}

	//------------------------------------------------------------------------------------------
	// Returns a newly allocated Date object that represents this DateTime's value (which must be fixed).
	//------------------------------------------------------------------------------------------

	public final Date toDate()
	{
		if (isDuration)
			throw new IllegalStateException ("(04304151255) Durations cannot be converted to Date");

		if (! isFixed)
			throw new IllegalStateException ("(04304151254) DateTime is not fixed");
		if (! gcValid)
			if (year == -1)
				throw new IllegalStateException ("(04304151303) DateTime must have a year to be converted to Date");
			else
				throw new IllegalStateException ("(04304151305) DateTime in unexpected internal state");

		return gc.getTime();
	}

	//------------------------------------------------------------------------------------------
	// Returns time in millis that represents this DateTime's value (which must be fixed).
	//------------------------------------------------------------------------------------------

	public final long getTimeInMillis()
	{
		if (isDuration)
			throw new IllegalStateException ("(03304151255) Durations cannot be converted to Date");

		if (! isFixed)
			throw new IllegalStateException ("(03304151254) DateTime is not fixed");
		if (! gcValid)
			if (year == -1)
				throw new IllegalStateException ("(03304151303) DateTime must have a year to be converted to Date");
			else
				throw new IllegalStateException ("(03304151305) DateTime in unexpected internal state");

		return gc.getTimeInMillis();
	}

	//------------------------------------------------------------------------------------------
	// Returns a String object representing this DateTime's current value.
	//------------------------------------------------------------------------------------------

	private static final DecimalFormat df = new DecimalFormat();

	private static final String nice (final String pattern, final int value)
	{
		df.applyPattern (pattern);
		return df.format (value);
	}

	public final synchronized String toString()
	{
		return toString(tz);
	}

	public final synchronized String toString(TimeZone myTz)
	{
		return toString(myTz, false);
	}

	public final synchronized String toString(TimeZone myTz, boolean addDST)
	{
		if (gcValid) {
			gc.setTimeZone (myTz == null ? SystemEnvironment.systemTimeZone : myTz);
			String DSTkz = "";
			if (addDST) {
				final GregorianCalendar tmpGc = SystemEnvironment.newGregorianCalendar();
				tmpGc.setTimeZone (myTz == null ? SystemEnvironment.systemTimeZone : myTz);
				tmpGc.setTimeInMillis(gc.getTimeInMillis());
				// if Summer Time, isDST == true
				final boolean isDST = (tmpGc.get(Calendar.DST_OFFSET) != 0);
				// now if isDST && gc.hour == tmpGc.hour + 1 -> A Period
				if (isDST) {
					tmpGc.add(Calendar.HOUR_OF_DAY, 1);
					if (tmpGc.get(Calendar.HOUR_OF_DAY) == gc.get(Calendar.HOUR_OF_DAY))
						DSTkz = "A";
				} else {
					tmpGc.add(Calendar.HOUR_OF_DAY, -1);
					if (tmpGc.get(Calendar.HOUR_OF_DAY) == gc.get(Calendar.HOUR_OF_DAY))
						DSTkz = "B";
				}
			}
			return nice    ("0000",  gc.get (Calendar.YEAR))
				+ nice ("'-'00", gc.get (Calendar.MONTH) + 1)
				+ nice ("'-'00", gc.get (Calendar.DAY_OF_MONTH))
				+ nice ("'T'00", gc.get (Calendar.HOUR_OF_DAY))
				+ nice ("':'00", gc.get (Calendar.MINUTE))
				+ (secondsSuppressed ? "" : nice ("':'00", gc.get (Calendar.SECOND)))
				+ DSTkz
				+ (myTz == null ? "" : " " + myTz.getID());
		} else {
			final String tzID = ((! isDuration) && (year != -1)) ? (myTz == null ? "" : " " + myTz.getID()) : "";

			String result = (year == -1) ? "" : nice ("0000", year);

			if (week != -1)
				return result + nice ("'W'00", week) + tzID;

			if ((month != -1) || (day != -1)) {
				result += "-";

				if (month != -1)
					result += nice ("00", month);

				if (day != -1)
					result += nice ("'-'00", day);
			}

			if ((hour == -1) && (minute == -1) && (second == -1))
				return result + tzID;

			if (hour != -1)
				result += nice ("'T'00", hour);
			else
				result += "T-";

			if (minute != -1)
				result += (hour != -1 ? ":" : "") + nice ("00", minute);

			if ((second != -1) && (! secondsSuppressed))
				result += (minute != -1 ? ":" : "-") + nice ("00", second);

			return result + tzID;
		}
	}
}
