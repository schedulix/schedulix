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

package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.timer.*;

public class SDMSInterval extends SDMSIntervalProxyGeneric
	implements SDMSOwnedObject
{

	public final static String __version = "SDMSInterval $Revision: 2.37.2.1 $ / @(#) $Id: SDMSInterval.java,v 2.37.2.1 2013/03/14 10:25:19 ronald Exp $";

	public static final int UNINITIALIZED = -1;
	public static final int N_A = 0;

	public static final boolean FILTER = true;
	public static final boolean DRIVER = false;

	private Vector filter = null;

	private Vector selectedBlocksPos = null;
	private Vector selectedBlocksNeg = null;
	private Vector selectedRanges = null;
	private boolean isInverse = false;
	private boolean rangeSelected = false;
	private boolean posSelected = false;
	private boolean negSelected = false;
	private int fifoLength = 1;
	private int selBlPos;
	private int selBlNeg;

	private int baseMultiplier = UNINITIALIZED;
	private int durationMultiplier = 1;
	private int gcBaseInterval = 0;
	private int gcDurationInterval = 0;
	private long maxBaseLength = 0;
	private boolean isInfinite = false;
	private SDMSInterval embeddedInterval = null;

	private long startTime = 0;
	private long endTime = Long.MAX_VALUE;

	private BlockState blockState = new BlockState();

	GregorianCalendar prevCeilGc = null;
	GregorianCalendar ceilGc = null;
	GregorianCalendar floorGc = null;
	GregorianCalendar nextFloorGc = null;

	protected SDMSInterval(SDMSObject p_object)
	{
		super(p_object);

		selectedBlocksPos = null;
		selectedBlocksNeg = null;
		selectedRanges = null;
		isInverse = false;
		rangeSelected = false;
		posSelected = false;
		negSelected = false;
		fifoLength = 1;
		selBlPos = 0;
		selBlNeg = 0;

		baseMultiplier = UNINITIALIZED;
		durationMultiplier = 1;
		gcBaseInterval = 0;
		gcDurationInterval = 0;
		maxBaseLength = 0;
		isInfinite = false;
		embeddedInterval = null;

		startTime = 0;
		endTime = Long.MAX_VALUE;

		blockState = new BlockState();

		prevCeilGc = null;
		ceilGc = null;
		floorGc = null;
		nextFloorGc = null;
	}

	private void initialize(SystemEnvironment sysEnv, TimeZone tz)
		throws SDMSException
	{
		Vector v = SDMSIntervalHierarchyTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		filter = new Vector();
		for (int i = 0; i < v.size(); ++i) {
			SDMSIntervalHierarchy ih = (SDMSIntervalHierarchy) v.get(i);
			filter.add(SDMSIntervalTable.getObject(sysEnv, ih.getChildId(sysEnv)));
		}
		initSelection(sysEnv);
		initBaseAndDuration(sysEnv);
		initLimits(sysEnv, tz);
		initEmbeddedInterval(sysEnv);
	}

	public String getURLName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long seId = getSeId(sysEnv);
		String se = null;
		if(seId != null)
			se = SDMSSchedulingEntityTable.getObject(sysEnv, seId).pathString(sysEnv);

		return getName(sysEnv) + (se == null ? "" : " (" + se + ")");
	}

	public String getURL(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return "interval " + getURLName(sysEnv);
	}

	private long getHorizon(SystemEnvironment sysEnv, TimeZone tz)
		throws SDMSException
	{
		int gcUnit = Calendar.YEAR;
		switch (SystemEnvironment.timerHorizon.unit()) {
			case YEAR:
				gcUnit = Calendar.YEAR;
				break;
			case MONTH:
				gcUnit = Calendar.MONTH;
				break;
			case WEEK:
				gcUnit = Calendar.WEEK_OF_YEAR;
				break;
			case DAY:
				gcUnit = Calendar.DAY_OF_MONTH;
				break;
			case HOUR:
				gcUnit = Calendar.HOUR_OF_DAY;
				break;
			case MINUTE:
				gcUnit = Calendar.MINUTE;
				break;
		}
		GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
		gc.setTimeInMillis(System.currentTimeMillis());
		gc.setTimeZone(tz);
		gc.add(gcUnit, SystemEnvironment.timerHorizon.mult());

		return gc.getTimeInMillis();
	}

	public Long getNextTriggerDate(SystemEnvironment sysEnv, Long minDate, long horizon, TimeZone tz)
		throws SDMSException
	{
		if (minDate == null) return null;
		long lMinDate = minDate.longValue();
		if (startTime > lMinDate) {
			lMinDate = startTime;
		}
		if (horizon == 0)
			horizon = getHorizon(sysEnv, tz);
		if (filter == null) {
			initialize(sysEnv, tz);
		}
		if (!seek(sysEnv, lMinDate, horizon, tz, DRIVER, "")) return null;
		if (blockState.blockStart < lMinDate) {
			if (blockState.blockEnd == Long.MAX_VALUE) return null;
			if (!seek(sysEnv, blockState.blockEnd + 1, horizon, tz, DRIVER, "")) return null;
		}

		return new Long(blockState.blockStart);
	}

	private boolean advanceBlock(SystemEnvironment sysEnv, long horizon, TimeZone tz)
		throws SDMSException
	{
		if (Thread.currentThread().isInterrupted()) {
			throw new CommonErrorException (new SDMSMessage (sysEnv, "02205091100", "Thread Interrupted"));
		}
		if (blockState.blockEnd == Long.MAX_VALUE) return false;
		if (isInfinite) {
			if (blockState.baseEnd <= blockState.blockEnd) return false;
			long minDate = 0;
			minDate = blockState.blockEnd + 1;

			if (getNextRange(sysEnv, minDate, tz) && blockState.blockStart < endTime && blockState.blockStart < horizon) {
				return true;
			}
			return false;
		} else {
			GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
			gc.setTimeZone(tz);

			if (embeddedInterval != null) {
				if (embeddedInterval.getNextTriggerDate(sysEnv, blockState.blockEnd + 1, blockState.baseEnd > horizon ? blockState.baseEnd : horizon, tz) == null) return false;
				blockState.blockStart = embeddedInterval.blockState.blockStart;
				blockState.blockEnd = embeddedInterval.blockState.blockEnd;
				if (blockState.blockStart > blockState.baseEnd) {
					blockState.baseStart = sync(sysEnv, blockState.blockStart, tz);
					gc.setTimeInMillis(blockState.baseStart);
					gc.add(gcBaseInterval, baseMultiplier);
					blockState.baseEnd = gc.getTimeInMillis() - 1;
					blockState.blockIdx = 0;
				}
			} else {
				blockState.blockStart = blockState.blockEnd + 1;
				gc.setTimeInMillis(blockState.blockStart);
				gc.add(gcDurationInterval, durationMultiplier);
				blockState.blockEnd = gc.getTimeInMillis() - 1;
				if (blockState.blockStart > blockState.baseEnd) {
					blockState.baseStart = blockState.baseEnd + 1;
					gc.setTimeInMillis(blockState.baseStart);
					gc.add(gcBaseInterval, baseMultiplier);
					blockState.baseEnd = gc.getTimeInMillis() - 1;
					blockState.blockIdx = 0;
				}
			}

			if (blockState.blockEnd > blockState.baseEnd) {
				if (embeddedInterval != null || gcDurationInterval != Calendar.WEEK_OF_YEAR) {
					blockState.blockEnd = blockState.baseEnd;
				} else {
					while (blockState.blockEnd > blockState.baseEnd) {
						gc.add(gcDurationInterval, -1);
						blockState.blockEnd = gc.getTimeInMillis() - 1;
					}
					gc.setTimeInMillis(blockState.baseEnd + 1);

					if ((gc.get(Calendar.DAY_OF_WEEK) - gc.getFirstDayOfWeek() + 7) % 7 + 1 > gc.getMinimalDaysInFirstWeek()) {
						gc.setTimeInMillis(blockState.blockEnd + 1);
						gc.add(gcDurationInterval, 1);
						blockState.blockEnd = gc.getTimeInMillis() - 1;
					} else {
						if (blockState.blockStart == blockState.blockEnd + 1) {
							blockState.blockStart = blockState.blockEnd + 1;
							gc.setTimeInMillis(blockState.blockStart);
							gc.add(gcDurationInterval, durationMultiplier);
							blockState.blockEnd = gc.getTimeInMillis() - 1;

							blockState.baseStart = blockState.baseEnd + 1;
							gc.setTimeInMillis(blockState.baseStart);
							gc.add(gcBaseInterval, baseMultiplier);
							blockState.baseEnd = gc.getTimeInMillis() - 1;
							blockState.blockIdx = 0;
						}
					}
				}
			}

			blockState.blockIdx++;

			if (blockState.blockStart >= endTime) return false;

			return true;
		}
	}

	private boolean seekBlock(SystemEnvironment sysEnv, long minDate, long horizon, TimeZone tz)
		throws SDMSException
	{
		GregorianCalendar gc;

		if (startTime > minDate) {
			minDate = startTime;
		}

		if (minDate < blockState.baseStart || minDate > blockState.baseEnd || blockState.baseStart == UNINITIALIZED) {
			blockState.clear();
			blockState.baseStart = sync(sysEnv, minDate, tz);
		}
		gc = SystemEnvironment.newGregorianCalendar();
		gc.setTimeZone(tz);
		gc.setTimeInMillis(blockState.baseStart);

		if (blockState.baseEnd == UNINITIALIZED) {
			if (isInfinite) blockState.baseEnd = endTime;
			else {
				gc.add(gcBaseInterval, baseMultiplier);
				blockState.baseEnd = gc.getTimeInMillis() - 1;
				gc.setTimeInMillis(blockState.baseStart);
			}
		}

		if (!isInfinite) {
			if (embeddedInterval == null && gcDurationInterval == Calendar.WEEK_OF_YEAR) {
				boolean backwardCorrectionNeeded = false;
				if ((gc.get(Calendar.DAY_OF_WEEK) - gc.getFirstDayOfWeek() + 7) % 7 + 1 <= gc.getMinimalDaysInFirstWeek()) {
					backwardCorrectionNeeded = true;
				}
				determineBoundary(sysEnv, gc, blockState.baseStart, durationMultiplier, gcDurationInterval);
				if (backwardCorrectionNeeded) {
					while (gc.getTimeInMillis() > blockState.baseStart) gc.add(gcDurationInterval, -1);
				} else {
					while (gc.getTimeInMillis() < blockState.baseStart) gc.add(gcDurationInterval, 1);
				}
			}
			if (embeddedInterval == null) {
				blockState.blockStart = gc.getTimeInMillis();
				gc.add(gcDurationInterval, durationMultiplier);
				blockState.blockEnd = gc.getTimeInMillis() - 1;
			} else {
				if (embeddedInterval.getNextTriggerDate(sysEnv, blockState.baseStart, blockState.baseEnd > horizon ? blockState.baseEnd : horizon, tz) == null) return false;
				blockState.blockStart = embeddedInterval.blockState.blockStart;
				blockState.blockEnd = embeddedInterval.blockState.blockEnd;
				if (blockState.blockStart > blockState.baseEnd) {
					blockState.baseStart = sync(sysEnv, blockState.blockStart, tz);
					gc.setTimeInMillis(blockState.baseStart);
					gc.add(gcBaseInterval, baseMultiplier);
					blockState.baseEnd = gc.getTimeInMillis() - 1;
				}
			}
			blockState.blockIdx = 1;
		} else {
			blockState.blockStart = blockState.baseStart;
			blockState.blockEnd = getStartingPoint(sysEnv, minDate, tz);
			if (!advanceBlock(sysEnv, horizon, tz)) return false;
		}

		while (blockState.blockEnd <= minDate && blockState.blockStart < endTime && blockState.blockStart < horizon) {
			if (!advanceBlock(sysEnv, horizon, tz)) return false;
		}
		if (blockState.blockStart >= endTime) return false;

		return true;
	}

	private boolean seek(SystemEnvironment sysEnv, long minDate, long horizon, TimeZone tz, boolean isFilter, String indent)
		throws SDMSException
	{
		if (filter == null) {
			initialize(sysEnv, tz);
		}

		while(true) {
			long minNext = Long.MAX_VALUE;
			if (!seekLocal(sysEnv, minDate, horizon, tz)) {
				return false;
			}
			if (filter.size() == 0) return true;

			long checkDate = isFilter ? (minDate < blockState.blockStart ? blockState.blockStart : minDate) : blockState.blockStart;
			for (int i = 0; i < filter.size(); ++i) {
				long next;
				SDMSInterval f = (SDMSInterval) filter.get(i);
				next = f.filter(sysEnv, checkDate, horizon, tz, indent + "\t");
				if (next != Long.MAX_VALUE) {
					if (next <= checkDate) {
						if (isFilter) blockState.blockStart = checkDate;
						return true;
					}
					if (minNext > next) minNext = next;
				} else {
				}
			}
			if (minNext == Long.MAX_VALUE || (!isFilter && (blockState.blockEnd == Long.MAX_VALUE))) return false;
			if (isFilter)
				minDate = minNext;
			else
				minDate = blockState.blockEnd >= minNext ? blockState.blockEnd + 1 : minNext;
		}
	}

	private long filter(SystemEnvironment sysEnv, long checkDate, long horizon, TimeZone tz, String indent)
		throws SDMSException
	{
		if(!seek(sysEnv, checkDate, horizon, tz, FILTER, indent)) return Long.MAX_VALUE;
		if (checkDate < startTime && blockState.blockStart < startTime) return startTime;
		return blockState.blockStart;
	}

	private boolean checkSelection (SystemEnvironment sysEnv, int blockIdx, int negIdx, long ts, TimeZone tz)
		throws SDMSException
	{
		if (!(posSelected || negSelected || rangeSelected)) return true;

		boolean positiveCheck = (posSelected && indexCheckPos(sysEnv, blockIdx)) ||
					(negSelected && indexCheckNeg(sysEnv, negIdx))   ||
					(rangeSelected && rangeCheck(sysEnv, ts, tz));

		return positiveCheck ^ isInverse;
	}

	private boolean indexCheckPos (SystemEnvironment sysEnv, int seqNo)
		throws SDMSException
	{
		if (!posSelected)
			return true;
		if (seqNo <= 0) return false;

		for (; selBlPos < selectedBlocksPos.size(); selBlPos ++) {
			int sel = ((Integer)selectedBlocksPos.get(selBlPos)).intValue();
			if (sel == seqNo)	return true;
			if (sel > seqNo)	return false;
		}

		return false;
	}

	private boolean indexCheckNeg (SystemEnvironment sysEnv, int seqNo)
		throws SDMSException
	{
		if (!negSelected)
			return true;
		if (seqNo >= 0) return false;

		for (; selBlNeg < selectedBlocksNeg.size(); selBlNeg ++) {
			int sel = ((Integer)selectedBlocksNeg.get(selBlNeg)).intValue();
			if (sel == seqNo)	return true;
			if (sel > seqNo)	return false;
		}

		return false;
	}

	private boolean rangeCheck(SystemEnvironment sysEnv, long date, TimeZone tz)
		throws SDMSException
	{
		if (!rangeSelected)
			return true;

		for (int i = 0; i < selectedRanges.size(); i ++) {
			DateTime a[] = (DateTime[])(selectedRanges.get(i));
			long floor = floor(a[0], date, tz);
			long ceil = ceil(a[1], floor, tz);

			if (floor > ceil)
				continue;

			if (floor(a[0], ceil, tz) != floor)
				continue;

			if (floor <= date && date <= ceil) {
				return true;
			}
		}
		return false;
	}

	private boolean getNextRange(SystemEnvironment sysEnv, long date, TimeZone tz)
		throws SDMSException
	{
		if (!rangeSelected) {
			if (date == blockState.blockStart) {
				blockState.blockEnd = blockState.baseEnd;
				return true;
			}
			return false;
		}
		if (!isInverse) {
			BlockState nextBlock = getNextPositiveRange(sysEnv, date, tz);
			if (nextBlock == null) return false;
			blockState.copyFrom(nextBlock);
			return true;
		} else {
			BlockState prevBlock = new BlockState(blockState);

			BlockState nextBlock = getNextPositiveRange(sysEnv, prevBlock.blockEnd + 1, tz);
			if (nextBlock == null) {
				if (prevBlock.blockEnd + 1 == date) {
					blockState.blockStart = date;
					blockState.blockEnd = endTime;
					return true;
				}
				return false;
			}

			while (nextBlock.blockStart < date || prevBlock.blockEnd + 1 < date || prevBlock.blockEnd + 1 == nextBlock.blockStart) {
				prevBlock = nextBlock;
				nextBlock = getNextPositiveRange(sysEnv, prevBlock.blockEnd, tz);
				if (nextBlock == null) {
					if (prevBlock.blockEnd + 1 == date) {
						blockState.blockStart = date;
						blockState.blockEnd = endTime;
						return true;
					}
					return false;
				}
			}

			blockState.blockStart = prevBlock.blockEnd + 1;
			blockState.blockEnd = nextBlock.blockStart - 1;
			return true;
		}
	}

	private long getStartingPoint(SystemEnvironment sysEnv, long date, TimeZone tz)
		throws SDMSException
	{
		long maxFloor = startTime;
		long maxCeil = -1;

		for (int i = 0; i < selectedRanges.size(); i ++) {
			DateTime a[] = (DateTime[])(selectedRanges.get(i));
			long floor = floor(a[0], date, tz);
			long ceil = ceil(a[1], floor, tz);
			if (floor > ceil) floor = Long.MAX_VALUE;
			floor = floor(a[0], ceil, tz);

			if (floor <= date && date <= ceil) {
				if (maxFloor < floor) maxFloor = floor;

				continue;
			}

			if (floor > date) {
				ceil = prevCeil(a[1], a[0], ceil, tz);
			}

			if (ceil > maxCeil && ceil < date)
				maxCeil = ceil;
		}

		return maxFloor >= maxCeil ? maxFloor - 1 : maxCeil;
	}

	private BlockState getNextPositiveRange(SystemEnvironment sysEnv, long date, TimeZone tz)
		throws SDMSException
	{
		long firstFloor = Long.MAX_VALUE;
		long secondFloor = Long.MAX_VALUE;
		long firstCeil = 0;
		long maxCeil = 0;
		BlockState result = new BlockState(blockState);

		for (int i = 0; i < selectedRanges.size(); i ++) {
			DateTime a[] = (DateTime[])(selectedRanges.get(i));
			long floor = floor(a[0], date, tz);
			long ceil = ceil(a[1], floor, tz);
			if (floor > ceil) continue;
			floor = floor(a[0], ceil, tz);

			if (floor < date) {
				if (date <= ceil && maxCeil < ceil) maxCeil = ceil;
				floor = nextFloor(a[0], floor, tz);
				ceil = ceil(a[1], floor, tz);
				if (floor > ceil) floor = Long.MAX_VALUE;
				else
					floor = floor(a[0], ceil, tz);
			}

			if (floor >= date) {
				if (firstFloor > floor) {
					secondFloor = firstFloor;
					firstFloor = floor;
					firstCeil = 0;
				} else {
					if (secondFloor > floor && floor != firstFloor) secondFloor = floor;
				}
			}
			if (floor == firstFloor && firstCeil < ceil)
				firstCeil = ceil;

		}
		if (firstFloor == Long.MAX_VALUE) return null;
		result.blockStart = firstFloor;
		result.blockEnd = secondFloor - 1;
		if (maxCeil < firstCeil) maxCeil = firstCeil;
		if (maxCeil < result.blockEnd) result.blockEnd = maxCeil;
		return result;
	}

	private boolean seekLocal(SystemEnvironment sysEnv, long minDate, long horizon, TimeZone tz)
		throws SDMSException
	{
		long currentBaseStart = 0;
		long currentBaseEnd = Long.MAX_VALUE;

		if (!seekBlock(sysEnv, minDate, horizon, tz)) return false;

		Vector fifo = new Vector();
		int startSeqNo = UNINITIALIZED;

		while (true) {
			if (currentBaseStart < blockState.baseStart) {
				int fifoSize = fifo.size();
				for (int i = 0; i < fifoSize; ++i) {
					BlockState bs = (BlockState) fifo.get(i);
					long t = bs.blockStart;
					if (checkSelection(sysEnv, startSeqNo + i, i - fifoSize, t, tz)) {
						blockState.copyFrom(bs);
						return true;
					}
				}

				resetPositions();
				fifo.clear();
				startSeqNo = blockState.blockIdx;
				currentBaseStart = blockState.baseStart;
				currentBaseEnd = blockState.baseEnd;
			}

			fifo.addElement(blockState.clone());
			if (fifo.size() > fifoLength) {
				BlockState bs = (BlockState) fifo.remove(0);
				long s = bs.blockStart;
				if (s > horizon) return false;
				if (checkSelection(sysEnv, startSeqNo, 0 , s, tz)) {
					blockState.copyFrom(bs);
					return true;
				}
				startSeqNo++;
			}
			if(!advanceBlock(sysEnv, horizon, tz) || blockState.blockStart > horizon) {
				int fifoSize = fifo.size();
				for (int i = 0; i < fifoSize; ++i) {
					BlockState bs = (BlockState) fifo.get(i);
					long t = bs.blockStart;
					if (checkSelection(sysEnv, startSeqNo + i, i - fifoSize, t, tz)) {
						blockState.copyFrom(bs);
						return true;
					}
				}
				return false;
			}
		}
	}

	private long sync(SystemEnvironment sysEnv, long minDate, TimeZone tz)
		throws SDMSException
	{
		GregorianCalendar gc = null;
		if (! isInfinite) {
			Long syncTime = this.getSyncTime (sysEnv);

			gc = localGcFromGMT(sysEnv, syncTime, tz);

			determineBoundary(sysEnv, gc, minDate, baseMultiplier, gcBaseInterval);
			return gc.getTimeInMillis();
		} else {
			return startTime;
		}
	}

	private void truncToUnit(SystemEnvironment sysEnv, GregorianCalendar gc, int gcInterval)
		throws SDMSException
	{
		switch (gcInterval) {
			case Calendar.YEAR:
				gc.set (Calendar.MONTH, Calendar.JANUARY);
			case Calendar.MONTH:
				gc.set (Calendar.DAY_OF_MONTH, 1);
			case Calendar.DAY_OF_MONTH:
				gc.set (Calendar.HOUR_OF_DAY, 0);
			case Calendar.HOUR_OF_DAY:
				gc.set (Calendar.MINUTE, 0);
			case Calendar.MINUTE:
				break;

			case Calendar.WEEK_OF_YEAR:
				gc.set (Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				gc.set (Calendar.HOUR_OF_DAY, 0);
				gc.set (Calendar.MINUTE, 0);
				break;
		}

		gc.set (Calendar.SECOND, 0);
		gc.set (Calendar.MILLISECOND, 0);

		gc.getTimeInMillis();
	}

	private void determineBoundary(SystemEnvironment sysEnv, GregorianCalendar startDate, long targetDate, int multiplier, int gcInterval)
		throws SDMSException
	{
		truncToUnit(sysEnv, startDate, gcInterval);

		long sd = startDate.getTimeInMillis();
		long diff = targetDate - sd;

		int num = (int) (diff / maxBaseLength);
		num = num / multiplier;

		if (num != 0)
			startDate.add(gcInterval, num * multiplier);

		sd = startDate.getTimeInMillis();
		while (sd < targetDate) {
			startDate.add(gcInterval, multiplier);
			sd = startDate.getTimeInMillis();
		}

		while (sd > targetDate) {
			startDate.add(gcInterval, -multiplier);
			sd = startDate.getTimeInMillis();
		}
	}

	private void resetPositions()
	{
		selBlPos = 0;
		selBlNeg = 0;
	}

	private void initSelection(SystemEnvironment sysEnv)
		throws SDMSException
	{
		resetPositions();
		if (selectedBlocksPos != null) return;
		isInverse = getIsInverse(sysEnv).booleanValue();
		Vector v = SDMSIntervalSelectionTable.idx_intId.getVector(sysEnv, getId(sysEnv));
		selectedBlocksPos = new Vector();
		selectedBlocksNeg = new Vector();
		selectedRanges = new Vector();

		for (int i = 0; i < v.size(); ++i) {
			SDMSIntervalSelection isel = (SDMSIntervalSelection) v.get(i);
			Integer idx = isel.getValue(sysEnv);
			if (idx != null) {
				int nextValue = idx.intValue();
				Vector idxv;
				if (nextValue < 0) {
					idxv = selectedBlocksNeg;
				} else {
					idxv = selectedBlocksPos;
				}

				int j = 0;
				while (j < idxv.size() && ((Integer) idxv.get(j)).intValue() < nextValue) {
					++j;
				}
				if (j == idxv.size() || ((Integer) idxv.get(j)).intValue() != nextValue)
					idxv.add(j, idx);
			} else {
				DateTime period[] = new DateTime[2];
				period[0] = new DateTime(isel.getPeriodFrom(sysEnv), false);
				Long tmpTo = isel.getPeriodTo(sysEnv);
				period[1] = (tmpTo == null ? period[0] : new DateTime(tmpTo, false));

				selectedRanges.add(period);
			}
		}
		if (selectedBlocksNeg.size() > 0) {
			fifoLength = Math.abs(((Integer) selectedBlocksNeg.elementAt(0)).intValue());
			negSelected = true;
		}
		if (selectedBlocksPos.size() > 0) {
			posSelected = true;
		}
		if (selectedRanges.size() > 0) {
			rangeSelected = true;
		}
	}

	private void initBaseAndDuration(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(baseMultiplier != UNINITIALIZED) return;

		Integer baseIv = getBaseInterval(sysEnv);
		if (baseIv != null) {
			int bi = baseIv.intValue();
			Integer baseMult = getBaseIntervalMultiplier(sysEnv);
			baseMultiplier = baseMult.intValue();

			switch (bi) {
				case YEAR:
					gcBaseInterval = Calendar.YEAR;
					break;
				case MONTH:
					gcBaseInterval = Calendar.MONTH;
					break;
				case WEEK:
					gcBaseInterval = Calendar.WEEK_OF_YEAR;
					break;
				case DAY:
					gcBaseInterval = Calendar.DAY_OF_MONTH;
					break;
				case HOUR:
					gcBaseInterval = Calendar.HOUR_OF_DAY;
					break;
				case MINUTE:
					gcBaseInterval = Calendar.MINUTE;
					break;
			}
		} else {
			baseMultiplier = 0;
		}

		Integer durationIv = getDuration(sysEnv);
		if (durationIv != null) {
			int di = durationIv.intValue();
			Integer durationMult = getDurationMultiplier(sysEnv);
			durationMultiplier = durationMult.intValue();

			switch (di) {
				case YEAR:
					gcDurationInterval = Calendar.YEAR;
					break;
				case MONTH:
					gcDurationInterval = Calendar.MONTH;
					break;
				case WEEK:
					gcDurationInterval = Calendar.WEEK_OF_YEAR;
					break;
				case DAY:
					gcDurationInterval = Calendar.DAY_OF_MONTH;
					break;
				case HOUR:
					gcDurationInterval = Calendar.HOUR_OF_DAY;
					break;
				case MINUTE:
					gcDurationInterval = Calendar.MINUTE;
					break;
			}
		} else {
			durationMultiplier = 0;
		}

		if (durationMultiplier == 0 && baseMultiplier == 0)
			isInfinite = true;
		else {
			if (durationMultiplier == 0) {
				durationMultiplier = baseMultiplier;
				gcDurationInterval = gcBaseInterval;
			}
			if (baseMultiplier == 0) {
				baseMultiplier = durationMultiplier;
				gcBaseInterval = gcDurationInterval;
			}
			switch (gcBaseInterval) {
				case Calendar.YEAR:
					maxBaseLength = YEAR_MAX;
					break;
				case Calendar.MONTH:
					maxBaseLength = MONTH_MAX;
					break;
				case Calendar.WEEK_OF_YEAR:
					maxBaseLength = WEEK_MAX;
					break;
				case Calendar.DAY_OF_MONTH:
					maxBaseLength = DAY_MAX;
					break;
				case Calendar.HOUR_OF_DAY:
					maxBaseLength = HOUR_MAX;
					break;
				case Calendar.MINUTE:
					maxBaseLength = MINUTE_MAX;
					break;
			}
		}
	}

	private void initLimits(SystemEnvironment sysEnv, TimeZone tz)
		throws SDMSException
	{
		Long st = this.getStartTime (sysEnv);
		if (st != null) {
			startTime = localGcFromGMT(sysEnv, st, tz).getTimeInMillis();
		}
		Long et = this.getEndTime (sysEnv);
		if (et != null) {
			endTime = localGcFromGMT(sysEnv, et, tz).getTimeInMillis() - 1;
		}
	}

	private void initEmbeddedInterval(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long embeddedIntervalId = getEmbeddedIntervalId(sysEnv);
		if (embeddedIntervalId != null) {
			embeddedInterval = SDMSIntervalTable.getObject(sysEnv, embeddedIntervalId);
		}
	}

	private GregorianCalendar localGcFromGMT(SystemEnvironment sysEnv, Long time, TimeZone tz)
		throws SDMSException
	{
		long t = new DateTime (time, false).getTimeInMillis();

		GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
		gc.setTimeInMillis(t);

		int year   = gc.get (Calendar.YEAR);
		int month  = gc.get (Calendar.MONTH);
		int day    = gc.get (Calendar.DAY_OF_MONTH);
		int hour   = gc.get (Calendar.HOUR_OF_DAY);
		int minute = gc.get (Calendar.MINUTE);

		gc.setTimeZone(tz);
		gc.clear();
		gc.set(year, month, day, hour, minute);

		gc.getTimeInMillis();

		return gc;
	}

	private long floor (final DateTime limit, long date, TimeZone tz)
	{
		if (date == Long.MAX_VALUE) return date;
		if (floorGc == null) {
			floorGc = SystemEnvironment.newGregorianCalendar();
		}
		GregorianCalendar gc = floorGc;
		gc.setTimeZone(tz);
		gc.setTimeInMillis(date);
		gc.set (Calendar.SECOND, 0);
		gc.set (Calendar.MILLISECOND, 0);

		if (limit.year != -1)
			gc.set (Calendar.YEAR, limit.year );

		if (limit.week != -1) {

			gc.set (Calendar.WEEK_OF_YEAR, limit.week);
			gc.set (Calendar.DAY_OF_WEEK,  Calendar.MONDAY);
			gc.set (Calendar.HOUR_OF_DAY,  0);
			gc.set (Calendar.MINUTE,       0);
		} else {

			if (limit.minute == -1) {
				gc.set (Calendar.MINUTE, 0);
				if (limit.hour == -1) {
					gc.set (Calendar.HOUR_OF_DAY, 0);
					if (limit.day == -1) {
						gc.set (Calendar.DAY_OF_MONTH, 1);
						if (limit.month == -1)
							gc.set (Calendar.MONTH, Calendar.JANUARY);
					}
				}
			}

			if (limit.month != -1) {
				if (limit.day != -1)
					gc.set(Calendar.DAY_OF_MONTH, 1);
				gc.set (Calendar.MONTH, limit.month - 1);
			}
			if (limit.day != -1) {
				if (limit.day < 29) {
					gc.set (Calendar.DAY_OF_MONTH, limit.day);
				} else {
					gc.set (Calendar.DAY_OF_MONTH, 1);
					gc.getTimeInMillis();
					while (gc.getActualMaximum(Calendar.DAY_OF_MONTH) < limit.day) {
						if (limit.month == -1)
							gc.add(Calendar.MONTH, -1);
						else
							gc.add(Calendar.YEAR, -1);
						gc.getTimeInMillis();
					}
					gc.set (Calendar.DAY_OF_MONTH, limit.day);
				}
			}
			if (limit.hour != -1)
				gc.set (Calendar.HOUR_OF_DAY, limit.hour);
			if (limit.minute != -1)
				gc.set (Calendar.MINUTE, limit.minute);
		}

		long retval = gc.getTimeInMillis();
		if (retval > date && limit.year == -1) {
			int unit = Calendar.YEAR;
			if (limit.week == -1) {
				if (limit.month == -1) {
					unit = Calendar.MONTH;
					if (limit.day == -1) {
						unit = Calendar.DAY_OF_MONTH;
						if (limit.hour == -1) {
							unit = Calendar.HOUR_OF_DAY;
						}
					}
				}
			}
			while (retval > date) {
				if (limit.week == -1 && limit.day >= 29) {
					gc.set(Calendar.DAY_OF_MONTH, 1);
					do {
						gc.add(unit, -1);
						gc.getTimeInMillis();
					} while (gc.getActualMaximum(Calendar.DAY_OF_MONTH) < limit.day);
					gc.set(Calendar.DAY_OF_MONTH, limit.day);
				} else {
					if (limit.week < 53)
						gc.add(unit, -1);
					else {
						gc.set(Calendar.WEEK_OF_YEAR, 1);
						do {
							gc.add(unit, -1);
							gc.getTimeInMillis();
						} while (gc.getActualMaximum(Calendar.WEEK_OF_YEAR) < limit.week);
						gc.set(Calendar.WEEK_OF_YEAR, limit.week);
					}
				}
				retval = gc.getTimeInMillis();
			}
		}

		return retval;
	}

	private long nextFloor (final DateTime limit, long floor, TimeZone tz)
	{
		if (floor == Long.MAX_VALUE) return floor;
		if (limit.year != -1) return Long.MAX_VALUE;
		if (nextFloorGc == null) {
			nextFloorGc = SystemEnvironment.newGregorianCalendar();
		}
		GregorianCalendar gc = nextFloorGc;
		gc.setTimeZone(tz);
		long myFloor = floor(limit, floor, tz);
		gc.setTimeInMillis(myFloor);

		int gcInterval = Calendar.YEAR;
		boolean watchOut = true;
		int unit = 1;

		if (limit.week == -1) {
			if (limit.month == -1) {
				gcInterval = Calendar.MONTH;
				if (limit.day == -1) {
					gcInterval = Calendar.HOUR_OF_DAY;
					watchOut = false;
					if (limit.hour == -1) {
						gcInterval = Calendar.HOUR_OF_DAY;
					} else
						unit = 25;
				}
			}
			if (limit.day < 29) watchOut = false;
		} else if (limit.week < 53) watchOut = false;

		if (watchOut) {
			if (limit.day >= 29) {
				gc.set(Calendar.DAY_OF_MONTH, 1);
				do {
					gc.add(gcInterval, unit);
					gc.getTimeInMillis();
				} while (gc.getActualMaximum(Calendar.DAY_OF_MONTH) < limit.day);
				gc.set(Calendar.DAY_OF_MONTH, limit.day);
			} else {
				gc.set(Calendar.WEEK_OF_YEAR, unit);
				do {
					gc.add(gcInterval, 1);
					gc.getTimeInMillis();
				} while (gc.getActualMaximum(Calendar.WEEK_OF_YEAR) < limit.week);
				gc.set(Calendar.WEEK_OF_YEAR, limit.week);
			}
		} else
			gc.add(gcInterval, unit);

		long retval = gc.getTimeInMillis();
		retval = floor(limit, retval, tz);

		return retval;
	}

	private long ceil (final DateTime limit, long date, TimeZone tz)
	{
		if (date == Long.MAX_VALUE) return date;
		if (ceilGc == null) {
			ceilGc = SystemEnvironment.newGregorianCalendar();
		}
		GregorianCalendar gc = ceilGc;
		gc.setTimeZone(tz);
		gc.setTimeInMillis(date);
		gc.set (Calendar.SECOND, 59);
		gc.set (Calendar.MILLISECOND, 999);

		if (limit.year != -1)
			gc.set (Calendar.YEAR, limit.year );

		if (limit.week != -1) {

			gc.set (Calendar.WEEK_OF_YEAR, limit.week);
			gc.set (Calendar.DAY_OF_WEEK,  Calendar.SUNDAY);
			gc.set (Calendar.HOUR_OF_DAY,  23);
			gc.set (Calendar.MINUTE,       59);
		} else {

			if (limit.minute == -1) {
				gc.set (Calendar.MINUTE, 59);
				if (limit.hour == -1) {
					gc.set (Calendar.HOUR_OF_DAY, 23);
					if (limit.day == -1) {
						if (limit.month == -1)
							gc.set (Calendar.MONTH, Calendar.DECEMBER);
						else
							gc.set (Calendar.MONTH, limit.month - 1);
						gc.set (Calendar.DAY_OF_MONTH, gc.getActualMaximum (Calendar.DAY_OF_MONTH));
					}
				}
			}

			if (limit.month != -1) {
				gc.set (Calendar.DAY_OF_MONTH, 1);
				if (limit.day == -1) {
					gc.getTimeInMillis();
					gc.set (Calendar.MONTH, limit.month - 1);
					gc.set (Calendar.DAY_OF_MONTH, gc.getActualMaximum (Calendar.DAY_OF_MONTH));
				} else {
					gc.set (Calendar.MONTH, limit.month - 1);
				}
			}
			if (limit.day != -1) {
				if (limit.day < 29) {
					gc.set (Calendar.DAY_OF_MONTH, limit.day);
				} else {
					gc.set (Calendar.DAY_OF_MONTH, 1);
					gc.getTimeInMillis();
					while (gc.getActualMaximum(Calendar.DAY_OF_MONTH) < limit.day) {
						if (limit.month == -1)
							gc.add(Calendar.MONTH, 1);
						else
							gc.add(Calendar.YEAR, 1);
						gc.getTimeInMillis();
					}
					gc.set (Calendar.DAY_OF_MONTH, limit.day);
				}
			}
			if (limit.hour != -1)
				gc.set (Calendar.HOUR_OF_DAY, limit.hour);
			if (limit.minute != -1)
				gc.set (Calendar.MINUTE, limit.minute);
		}

		long retval = gc.getTimeInMillis();
		if (retval < date && limit.year == -1) {
			int unit = Calendar.YEAR;
			if (limit.week == -1) {
				if (limit.month == -1) {
					unit = Calendar.MONTH;
					if (limit.day == -1) {
						unit = Calendar.DAY_OF_MONTH;
						if (limit.hour == -1) {
							unit = Calendar.HOUR_OF_DAY;
						}
					}
				}
			}
			while (retval < date) {
				if (limit.week == -1 && limit.day >= 29) {
					gc.set(Calendar.DAY_OF_MONTH, 1);
					do {
						gc.add(unit, 1);
						gc.getTimeInMillis();
					} while (gc.getActualMaximum(Calendar.DAY_OF_MONTH) < limit.day);
					gc.set(Calendar.DAY_OF_MONTH, limit.day);
				} else {
					if (limit.week < 53)
						gc.add(unit, 1);
					else {
						gc.set(Calendar.WEEK_OF_YEAR, 1);
						do {
							gc.add(unit, 1);
							gc.getTimeInMillis();
						} while (gc.getActualMaximum(Calendar.WEEK_OF_YEAR) < limit.week);
						gc.set(Calendar.WEEK_OF_YEAR, limit.week);
					}
				}
				retval = gc.getTimeInMillis();
			}
		}

		return retval;
	}

	private long prevCeil (final DateTime floorLimit, final DateTime limit, long ceil, TimeZone tz)
	{
		if (limit.year != -1) return 0;
		if (ceil == Long.MAX_VALUE) return ceil;
		if (prevCeilGc == null) {
			prevCeilGc = SystemEnvironment.newGregorianCalendar();
		}
		GregorianCalendar gc = prevCeilGc;
		gc.setTimeZone(tz);
		long myCeil = ceil(limit, ceil, tz);
		gc.setTimeInMillis(myCeil);

		int gcInterval = Calendar.YEAR;
		boolean watchOut = true;

		if (limit.week == -1) {
			if (limit.month == -1) {
				gcInterval = Calendar.MONTH;
				if (limit.day == -1) {
					gcInterval = Calendar.DAY_OF_MONTH;
					watchOut = false;
					if (limit.hour == -1) {
						gcInterval = Calendar.HOUR_OF_DAY;
					}
				}
			}
			if (limit.day < 29) watchOut = false;
		} else if (limit.week <= 52) watchOut = false;

		if (watchOut) {
			if (limit.day >= 29) {
				gc.set(Calendar.DAY_OF_MONTH, 1);
				do {
					gc.add(gcInterval, -1);
					gc.getTimeInMillis();
				} while (gc.getActualMaximum(Calendar.DAY_OF_MONTH) < limit.day);
				gc.set(Calendar.DAY_OF_MONTH, limit.day);
			} else {
				gc.set(Calendar.WEEK_OF_YEAR, 1);
				do {
					gc.add(gcInterval, -1);
					gc.getTimeInMillis();
				} while (gc.getActualMaximum(Calendar.WEEK_OF_YEAR) < limit.week);
				gc.set(Calendar.WEEK_OF_YEAR, limit.week);
			}
		} else
			gc.add(gcInterval, -1);

		long retval = gc.getTimeInMillis();

		long floor = floor(floorLimit, retval, tz);
		if (floor > retval) return 0L;

		retval = ceil(limit, floor, tz);
		return retval;
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		long p, seP;
		Long seId;
		SDMSSchedulingEntity se;
		Vector myGroups;

		p = SDMSPrivilege.NOPRIVS;
		seId = getSeId(sysEnv);
		if (seId == null) {
			p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
			return p & checkPrivs;
		}

		if (checkGroups == null) {
			myGroups = new Vector();
			if(sysEnv.cEnv.isUser()) {
				myGroups.addAll(sysEnv.cEnv.gid());
			}
		} else
			myGroups = checkGroups;

		se = SDMSSchedulingEntityTable.getObject(sysEnv, seId);
		seP = se.getPrivileges(sysEnv, SDMSPrivilege.VIEW|SDMSPrivilege.SUBMIT, false, myGroups);
		if ((seP & SDMSPrivilege.SUBMIT) == SDMSPrivilege.SUBMIT) {
			Long submitGId = getOwnerId(sysEnv);
			if (myGroups.contains(submitGId) || myGroups.contains(SDMSObject.adminGId)) {
				p = checkPrivs;
			} else {
				p = SDMSPrivilege.VIEW;
			}
		} else if ((seP & SDMSPrivilege.VIEW) == SDMSPrivilege.VIEW) {
			p |= SDMSPrivilege.VIEW;
		}
		p = addImplicitPrivs(p) & checkPrivs;
		return p;
	}
}

class BlockState implements Cloneable
{
	public long baseStart;
	public long baseEnd;

	public long blockStart;
	public long blockEnd;
	public int blockIdx;

	public BlockState()
	{
		clear();
	}

	public BlockState(BlockState s)
	{
		baseStart = s.baseStart;
		baseEnd = s.baseEnd;
		blockStart = s.blockStart;
		blockEnd = s.blockEnd;
		blockIdx = s.blockIdx;
	}

	public void copyFrom(BlockState s)
	{
		baseStart = s.baseStart;
		baseEnd = s.baseEnd;
		blockStart = s.blockStart;
		blockEnd = s.blockEnd;
		blockIdx = s.blockIdx;
	}

	public void clear()
	{
		baseStart = SDMSInterval.UNINITIALIZED;
		baseEnd = SDMSInterval.UNINITIALIZED;
		blockStart = SDMSInterval.UNINITIALIZED;
		blockEnd = SDMSInterval.UNINITIALIZED;
		blockIdx = SDMSInterval.N_A;
	}

	public Object clone()
	{
		return new BlockState(this);
	}

	public String toString()
	{
		return toString("");
	}

	public String toString(String indent)
	{
		return  indent + "blockState [\n" +
			indent + "\tbaseStart : " + new TimerDate((int)(baseStart / 60000)).toString() + "\n" +
			indent + "\tbaseEnd   : " + new TimerDate((int)(baseEnd / 60000)).toString() + "\n" +
			indent + "\tblockStart: " + new TimerDate((int)(blockStart / 60000)).toString() + "\n" +
			indent + "\tblockEnd  : " + new TimerDate((int)(blockEnd / 60000)).toString() + "\n" +
			indent + "\tblockIdx  : " + blockIdx + "\n" +
			indent + "]";
	}
}
