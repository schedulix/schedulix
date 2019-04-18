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

	public static final int UNINITIALIZED = -1;
	public static final int N_A = 0;

	public static final boolean FILTER = true;
	public static final boolean DRIVER = false;

	protected static final String HT = "    ";
	private static boolean debug = false;

	private Vector filter = null;
	private SDMSInterval embedFilter = null;

	private GntdCache gntdCache = null;
	private GntdCache fltrCache = null;

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

	private Vector<DispatchRule> dispatchRules = null;

	private long startTime = 0;
	private long endTime = Long.MAX_VALUE;

	private BlockState blockState = new BlockState();

	GregorianCalendar prevCeilGc = null;
	GregorianCalendar ceilGc = null;
	GregorianCalendar floorGc = null;
	GregorianCalendar nextFloorGc = null;

	private void debugmsg(SystemEnvironment sysEnv, int indent, String msg)
	throws SDMSException
	{
		if (!debug) return;
		for (int i = 0; i < indent; ++i)
			System.out.print(HT);
		System.out.println(this.getName(sysEnv) + "/" + this.getId(sysEnv) + " : " + Here.atc() + msg);
	}

	protected SDMSInterval(SDMSObject p_object)
	{
		super(p_object);
	}

	protected void initProxy(SDMSObject p_object)
	{
		super.initProxy(p_object);

		filter = null;
		embedFilter = null;
		gntdCache = null;
		fltrCache = null;

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

		dispatchRules = null;

		startTime = 0;
		endTime = Long.MAX_VALUE;

		blockState = new BlockState();

		prevCeilGc = null;
		ceilGc = null;
		floorGc = null;
		nextFloorGc = null;
	}

	private void initialize(SystemEnvironment sysEnv, TimeZone tz, int indent)
		throws SDMSException
	{
		gntdCache = new GntdCache();
		fltrCache = new GntdCache();
		Vector v = SDMSIntervalHierarchyTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		filter = new Vector();
		for (int i = 0; i < v.size(); ++i) {
			SDMSIntervalHierarchy ih = (SDMSIntervalHierarchy) v.get(i);
			filter.add(SDMSIntervalTable.getObject(sysEnv, ih.getChildId(sysEnv)));
		}
		initSelection(sysEnv, indent + 1);
		initBaseAndDuration(sysEnv, indent + 1);
		initLimits(sysEnv, tz);
		initEmbeddedInterval(sysEnv, tz, indent + 1);
		initDispatcher(sysEnv);
	}

	private void restoreState(gntd val)
	{
		blockState.copyFrom(val.bs);
		selBlPos = val.selBlPos;
		selBlNeg = val.selBlNeg;
		prevCeilGc = (val.prevCeilGc == null ? null : (GregorianCalendar) (val.prevCeilGc.clone()));
		ceilGc = (val.ceilGc == null ? null : (GregorianCalendar) (val.ceilGc.clone()));
		floorGc = (val.floorGc == null ? null : (GregorianCalendar) (val.floorGc.clone()));
		nextFloorGc = (val.nextFloorGc == null ? null : (GregorianCalendar) (val.nextFloorGc.clone()));
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

	public long getHorizon(SystemEnvironment sysEnv, TimeZone tz)
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

	public Long getNextTriggerDate(SystemEnvironment sysEnv, Long minDate, long horizon, TimeZone tz, boolean mode)
	throws SDMSException
	{
		return getNextTriggerDate(sysEnv, minDate, horizon, tz, mode, 0);
	}

	public Long getNextTriggerDate(SystemEnvironment sysEnv, Long minDate, long horizon, TimeZone tz, boolean mode, int indent)
	throws SDMSException
	{
		if (filter == null) {
			initialize(sysEnv, tz, indent + 1);
		}
		gntd result = gntdCache.get(minDate, horizon, tz);
		if (result != null) {
			restoreState(result);
			return result.retVal;
		}

		if (minDate == null) return null;

		long lMinDate = minDate.longValue();
		if (startTime > lMinDate) {
			lMinDate = startTime;
		}
		if (horizon == 0)
			horizon = getHorizon(sysEnv, tz);
		if (dispatchRules != null) {
			Long dispTD = getNextDispatchTriggerDate(sysEnv, lMinDate, horizon, tz, mode, indent + 1);
			gntdCache.add(new gntd(minDate, horizon, tz, blockState, dispTD, selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, mode));
			return dispTD;
		}
		if (!seek(sysEnv, lMinDate, horizon, tz, mode, indent + 1)) {
			gntdCache.add(new gntd(minDate, horizon, tz, blockState, null, selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, mode));
			return null;
		}
		if (blockState.blockStart < lMinDate && (mode == DRIVER)) {
			if (blockState.blockEnd == Long.MAX_VALUE) {
				gntdCache.add(new gntd(minDate, horizon, tz, blockState, null, selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, mode));
				return null;
			}
			if (!seek(sysEnv, blockState.blockEnd + 1, horizon, tz, mode, indent + 1)) {
				gntdCache.add(new gntd(minDate, horizon, tz, blockState, null, selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, mode));
				return null;
			}
		}

		gntdCache.add(new gntd(minDate, horizon, tz, blockState, new Long(blockState.blockStart), selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, mode));
		return new Long(blockState.blockStart);
	}

	private Long getNextDispatchTriggerDate(SystemEnvironment sysEnv, long minDate, long horizon, TimeZone tz, boolean mode, int indent)
	throws SDMSException
	{
		int drSize = dispatchRules.size();

		if (minDate < startTime) minDate = startTime;

		Long ntd[] = new Long[drSize];
		long nextTriggerDate = Long.MAX_VALUE;
		int best = Integer.MAX_VALUE;

		for (int i = 0; i < drSize; ++i) {
			DispatchRule dr = dispatchRules.get(i);
			Long tmp;
			if (dr.isActive) {
				if (dr.fltInterval != null) {
					tmp = dr.fltInterval.getNextTriggerDate(sysEnv, minDate, horizon, tz, mode, indent + 1);
				} else {
					tmp = new Long(minDate);
				}
				if (best == Integer.MAX_VALUE || (tmp != null && tmp < ntd[best])) {
					best = i;
				}
			} else {
				tmp = null;
			}
			ntd[i] = (tmp == null ? Long.MAX_VALUE : (tmp < minDate ? minDate : tmp));

		}
		while (true) {
			if (ntd[best] > horizon) {
				return null;
			}
			for (int i = 0; i <= best ; ++i) {
				DispatchRule dr = dispatchRules.get(i);
				Long tmp;
				if (dr.selInterval != null) {
					tmp = dr.selInterval.filter(sysEnv, ntd[best].longValue(), horizon, tz, indent + 1);
				} else {
					tmp = ntd[best];
				}
				if (tmp <= ntd[best]) {
					if (i == best) {
						SDMSInterval bestFlt = dispatchRules.get(best).fltInterval;
						if (bestFlt != null) {
							blockState.copyFrom(bestFlt.blockState);
							for (int j = 0; j < drSize; ++j) {
								if (ntd[j] > ntd[best] && ntd[j] < blockState.blockEnd) {
									blockState.blockEnd = ntd[j] - 1;
								}
							}
						} else {  }
						return ntd[best];
					} else {
						DispatchRule warBest = dispatchRules.get(best);
						Long bestTmp = 0L;
						if (warBest.fltInterval != null) {
							bestTmp = warBest.fltInterval.getNextTriggerDate(sysEnv, ntd[best] + 1, horizon, tz, mode, indent + 1);
							ntd[best] = (bestTmp == null ? Long.MAX_VALUE : bestTmp);
						} else {  }
						for (int j = 0; j < drSize; ++j) {
							if (ntd[j] < bestTmp) {
								best = j;
								bestTmp = ntd[j];
							}
						}
					}
					break;
				} else {
					if (i == best) {
						if (dr.fltInterval != null) {
							tmp = dr.fltInterval.getNextTriggerDate(sysEnv, dr.fltInterval.blockState.blockEnd + 1, horizon, tz, mode, indent + 1);
							ntd[best] = (tmp == null ? Long.MAX_VALUE : tmp);
						} else {
						}
						for (int j = 0; j < drSize; ++j) {
							if (ntd[j] < tmp) {
								best = j;
								tmp = ntd[j];
							}
						}
					}
				}
			}
		}
	}

	private boolean advanceBlock(SystemEnvironment sysEnv, long horizon, TimeZone tz, boolean mode, int indent)
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

			if (getNextRange(sysEnv, minDate, tz, indent + 1) && blockState.blockStart < endTime && blockState.blockStart < horizon) {
				return true;
			}
			return false;
		} else {
			GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
			gc.setTimeZone(tz);

			long tmp = Long.MAX_VALUE;
			if (embeddedInterval != null) {
				long checkDate = blockState.blockEnd;
				if (checkDate < Long.MAX_VALUE) checkDate = checkDate + 1;
				Long tmp2 = null;
				if (mode == FILTER) {
					tmp = embeddedInterval.filter(sysEnv, checkDate, blockState.baseEnd > horizon ? blockState.baseEnd : horizon, tz, indent + 1);
					if (tmp > checkDate) {
						blockState.blockStart = tmp;
						tmp2 = embeddedInterval.getNextTriggerDate(sysEnv, blockState.blockStart, blockState.baseEnd > horizon ? blockState.baseEnd : horizon, tz, DRIVER, indent + 1);
						if (tmp2 == null) {
							blockState.blockEnd = blockState.baseEnd;
						} else {
							blockState.blockEnd = embeddedInterval.blockState.blockEnd;
						}
					} else {
						blockState.blockStart = checkDate;
						blockState.blockEnd = embeddedInterval.blockState.blockEnd;
					}
					tmp2 = blockState.blockEnd;
				} else {
					if (embeddedInterval.getNextTriggerDate(sysEnv, checkDate, blockState.baseEnd > horizon ? blockState.baseEnd : horizon, tz, DRIVER, indent + 1) == null) {
						return false;
					}
				}

				if (mode == DRIVER) {
					blockState.blockStart = embeddedInterval.blockState.blockStart;
					blockState.blockEnd = embeddedInterval.blockState.blockEnd;
				}
				if (blockState.blockStart > blockState.baseEnd) {
					blockState.baseStart = sync(sysEnv, blockState.blockStart, tz);
					gc.setTimeInMillis(blockState.baseStart);
					gc.add(gcBaseInterval, baseMultiplier);
					blockState.baseEnd = gc.getTimeInMillis() - 1;
					blockState.blockIdx = 0;
					if (blockState.blockEnd < blockState.blockStart) {
						blockState.blockEnd = tmp2;
					}
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

			if (blockState.blockStart >= endTime) {
				return false;
			}

			return true;
		}
	}

	private boolean seekBlock(SystemEnvironment sysEnv, long minDate, long horizon, TimeZone tz, boolean mode, int indent)
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
				if (mode == FILTER) {
					long tmp = embeddedInterval.filter(sysEnv, blockState.baseStart, blockState.baseEnd > horizon ? blockState.baseEnd : horizon, tz, indent + 1);
				} else {
					if (embeddedInterval.getNextTriggerDate(sysEnv, blockState.baseStart, blockState.baseEnd > horizon ? blockState.baseEnd : horizon, tz, DRIVER, indent + 1) == null) {
						return false;
					}
				}
				blockState.blockStart = embeddedInterval.blockState.blockStart;
				blockState.blockEnd = embeddedInterval.blockState.blockEnd;
				if (blockState.blockStart > blockState.baseEnd) {
					blockState.baseStart = sync(sysEnv, blockState.blockStart, tz);
					gc.setTimeInMillis(blockState.baseStart);
					gc.add(gcBaseInterval, baseMultiplier);
					blockState.baseEnd = gc.getTimeInMillis() - 1;
				}
				if (blockState.blockEnd > blockState.baseEnd) {
					blockState.blockEnd = blockState.baseEnd;
				}
			}
			blockState.blockIdx = 1;
		} else {
			blockState.blockStart = blockState.baseStart;
			blockState.blockEnd = getStartingPoint(sysEnv, minDate, tz, indent + 1);
			if (!advanceBlock(sysEnv, horizon, tz, mode, indent + 1)) {
				return false;
			}
		}

		while (blockState.blockEnd <= minDate && blockState.blockStart < endTime && blockState.blockStart < horizon) {
			if (!advanceBlock(sysEnv, horizon, tz, mode, indent + 1)) return false;
		}
		if (blockState.blockStart >= endTime) return false;

		return true;
	}

	private boolean seek(SystemEnvironment sysEnv, long minDate, long horizon, TimeZone tz, boolean mode, int indent)
		throws SDMSException
	{
		if (filter == null) {
			initialize(sysEnv, tz, indent + 1);
		}

		final int filterSize = filter.size();
		while(true) {
			long minNext = Long.MAX_VALUE;
			if (!seekLocal(sysEnv, minDate, horizon, tz, mode, indent + 1)) {
				return false;
			}
			if ((filterSize == 0) && (embeddedInterval == null)) {
				return true;
			}

			long checkDate = (mode == FILTER) ? (minDate < blockState.blockStart ? blockState.blockStart : minDate) : blockState.blockStart;
			long next;

			long maxBlockEnd = blockState.blockEnd;
			if (embeddedInterval != null) {
				next = embeddedInterval.filter(sysEnv, checkDate, horizon, tz, indent + 1);
				if (maxBlockEnd > embeddedInterval.blockState.blockEnd)
					maxBlockEnd = embeddedInterval.blockState.blockEnd;
			} else {
				next = checkDate;
			}
			if (next <= checkDate && next != Long.MAX_VALUE) {
				if (filterSize == 0) {
					if (mode == FILTER) {
						blockState.blockStart = checkDate;
						blockState.blockEnd = maxBlockEnd;
					}
					return true;
				}
				boolean ok = false;
				for (int i = 0; i < filterSize; ++i) {
					SDMSInterval f = (SDMSInterval) filter.get(i);
					next = f.filter(sysEnv, checkDate, horizon, tz, indent + 1);
					if (next != Long.MAX_VALUE) {
						if (next <= checkDate) {
							if (mode == FILTER) {
								blockState.blockStart = checkDate;
								blockState.blockEnd = f.blockState.blockEnd;
							}
							if (blockState.blockEnd > maxBlockEnd) {
								blockState.blockEnd = maxBlockEnd;
							}
							maxBlockEnd = blockState.blockEnd;
							ok = true;
						}
						if (minNext > next) minNext = next;
					}
				}
				if (ok) {
					return true;
				}
			} else {
				if (minNext > next) minNext = next;
			}
			if (minNext == Long.MAX_VALUE || ((mode == DRIVER) && (blockState.blockEnd == Long.MAX_VALUE))) {
				return false;
			}
			if (mode == FILTER)
				minDate = minNext;
			else
				minDate = blockState.blockEnd >= minNext ? blockState.blockEnd + 1 : minNext;
		}
	}

	public long filter(SystemEnvironment sysEnv, long checkDate, long horizon, TimeZone tz, int indent)
		throws SDMSException
	{

		if (filter == null) {
			initialize(sysEnv, tz, indent + 1);
		}
		Long lCheckDate = new Long(checkDate);

		gntd result = fltrCache.get(lCheckDate, horizon, tz);
		if (result != null) {
			restoreState(result);
			return result.retVal.longValue();
		}

		if (dispatchRules != null) {
			long tmp = dispatchFilter(sysEnv, checkDate, horizon, tz, indent + 1);
			fltrCache.add(new gntd(lCheckDate, horizon, tz, blockState, new Long(tmp), selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, FILTER));
			return tmp;
		}
		if(!seek(sysEnv, checkDate, horizon, tz, FILTER, indent + 1)) {
			fltrCache.add(new gntd(lCheckDate, horizon, tz, blockState, new Long(Long.MAX_VALUE), selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, FILTER));
			return Long.MAX_VALUE;
		}
		if (checkDate < startTime && blockState.blockStart < startTime) {
			fltrCache.add(new gntd(lCheckDate, horizon, tz, blockState, new Long(startTime), selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, FILTER));
			return startTime;
		}
		fltrCache.add(new gntd(lCheckDate, horizon, tz, blockState, new Long(blockState.blockStart), selBlPos, selBlNeg, prevCeilGc, ceilGc, floorGc, nextFloorGc, FILTER));
		return blockState.blockStart;
	}

	private long dispatchFilter(SystemEnvironment sysEnv, long checkDate, long horizon, TimeZone tz, int indent)
	throws SDMSException
	{
		long blockStart;
		long selBlockStart;
		BlockState selBs[] = new BlockState[dispatchRules.size()];

		if (checkDate < startTime) {
			return startTime;
		}
		for (int i = 0; i < dispatchRules.size(); ++i) {
			DispatchRule dr = dispatchRules.get(i);
			if (dr.selInterval == null) {
				selBlockStart = 0;
				selBs[i] = new BlockState();
				selBs[i].baseStart = 0;
				selBs[i].baseEnd = Long.MAX_VALUE;
				selBs[i].blockStart = 0;
				selBs[i].blockEnd = Long.MAX_VALUE;
			} else {
				selBlockStart = dr.selInterval.filter(sysEnv, checkDate, horizon, tz, indent + 1);
				selBs[i] = new BlockState(dr.selInterval.blockState);
			}
			if (selBlockStart <= checkDate) {
				if (dr.isActive) {
					blockStart = dr.fltInterval.filter(sysEnv, checkDate, horizon, tz, indent + 1);
					blockState.copyFrom(dr.fltInterval.blockState);
					if (blockStart <= checkDate) {
						long blockEnd = blockState.blockEnd;
						if (selBs[i].blockEnd < blockEnd)
							blockEnd = selBs[i].blockEnd;
						for (int j = i - 1; j >= 0; --j) {
							if (selBs[j].blockStart < blockEnd && selBs[j].blockStart > blockStart)
								blockEnd = selBs[j].blockStart;
						}
						blockState.blockEnd = blockEnd;
						return blockStart;
					}
				} else {
					blockStart = Long.MAX_VALUE;
					blockState.copyFrom(dr.selInterval.blockState);
				}
				long nextBlockStart = dr.selInterval.blockState.blockEnd;
				if (blockStart < nextBlockStart) {
					nextBlockStart = blockStart;
				}
				for (int j = i - 1; j >= 0; j --) {
					if (selBs[j].blockStart < nextBlockStart) {
						nextBlockStart = selBs[j].blockStart;
					}
				}
				return nextBlockStart;
			}
		}
		return Long.MAX_VALUE;
	}

	private boolean checkSelection (SystemEnvironment sysEnv, int blockIdx, int negIdx, long ts, TimeZone tz, int indent)
		throws SDMSException
	{
		if (!(posSelected || negSelected || rangeSelected)) {
			return true;
		}

		boolean positiveCheck = (posSelected && indexCheckPos(sysEnv, blockIdx, indent + 1)) ||
		                        (negSelected && indexCheckNeg(sysEnv, negIdx, indent + 1))   ||
		                        (rangeSelected && rangeCheck(sysEnv, ts, tz, indent + 1));

		return positiveCheck ^ isInverse;
	}

	private boolean indexCheckPos (SystemEnvironment sysEnv, int seqNo, int indent)
		throws SDMSException
	{
		if (!posSelected) {
			return true;
		}
		if (seqNo <= 0) {
			return false;
		}

		for (; selBlPos < selectedBlocksPos.size(); selBlPos ++) {
			int sel = ((Integer)selectedBlocksPos.get(selBlPos)).intValue();
			if (sel == seqNo) {
				return true;
			}
			if (sel > seqNo) {
				return false;
			}
		}

		return false;
	}

	private boolean indexCheckNeg (SystemEnvironment sysEnv, int seqNo, int indent)
		throws SDMSException
	{
		if (!negSelected) {
			return true;
		}
		if (seqNo >= 0) {
			return false;
		}

		for (; selBlNeg < selectedBlocksNeg.size(); selBlNeg ++) {
			int sel = ((Integer)selectedBlocksNeg.get(selBlNeg)).intValue();
			if (sel == seqNo) {
				return true;
			}
			if (sel > seqNo) {
				return false;
			}
		}

		return false;
	}

	private boolean rangeCheck(SystemEnvironment sysEnv, long date, TimeZone tz, int indent)
		throws SDMSException
	{
		if (!rangeSelected) {
			return true;
		}

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

	private boolean getNextRange(SystemEnvironment sysEnv, long date, TimeZone tz, int indent)
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
			if (nextBlock == null) {
				return false;
			}
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

	private long getStartingPoint(SystemEnvironment sysEnv, long date, TimeZone tz, int indent)
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

	private boolean seekLocal(SystemEnvironment sysEnv, long minDate, long horizon, TimeZone tz, boolean mode, int indent)
		throws SDMSException
	{
		long currentBaseStart = 0;
		long currentBaseEnd = Long.MAX_VALUE;

		if (!seekBlock(sysEnv, minDate, horizon, tz, mode, indent + 1)) {
			return false;
		}

		Vector fifo = new Vector();
		int startSeqNo = UNINITIALIZED;

		while (true) {
			if (currentBaseStart < blockState.baseStart) {
				int fifoSize = fifo.size();
				for (int i = 0; i < fifoSize; ++i) {
					BlockState bs = (BlockState) fifo.get(i);
					long t = bs.blockStart;
					if (checkSelection(sysEnv, startSeqNo + i, i - fifoSize, t, tz, indent + 1)) {
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
				if (checkSelection(sysEnv, startSeqNo, 0, s, tz, indent + 1)) {
					blockState.copyFrom(bs);
					return true;
				}
				startSeqNo++;
			}
			if(!advanceBlock(sysEnv, horizon, tz, mode, indent + 1) || blockState.blockStart > horizon) {
				int fifoSize = fifo.size();
				for (int i = 0; i < fifoSize; ++i) {
					BlockState bs = (BlockState) fifo.get(i);
					long t = bs.blockStart;
					if (checkSelection(sysEnv, startSeqNo + i, i - fifoSize, t, tz, indent + 1)) {
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
			if (sd < 0) {
				sd = Long.MAX_VALUE;
				startDate.setTimeInMillis(sd);
				return;
			}
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

	private void initSelection(SystemEnvironment sysEnv, int indent)
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

	private void initBaseAndDuration(SystemEnvironment sysEnv, int indent)
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

	private void initEmbeddedInterval(SystemEnvironment sysEnv, TimeZone tz, int indent)
		throws SDMSException
	{
		Long embeddedIntervalId = getEmbeddedIntervalId(sysEnv);

		if (embeddedIntervalId != null) {
			SDMSInterval emb = SDMSIntervalTable.getObject(sysEnv, embeddedIntervalId);
			embeddedInterval = emb;
			embedFilter = null;
		}
	}

	private void initDispatcher(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector drv = SDMSIntervalDispatcherTable.idx_intId.getSortedVector(sysEnv, getId(sysEnv));
		if (drv.size() != 0) {
			dispatchRules = new Vector<DispatchRule>();
			for (int i = 0; i < drv.size(); ++i) {
				DispatchRule dr = new DispatchRule(sysEnv, (SDMSIntervalDispatcher) drv.get(i));
				if (dr.isEnabled)
					dispatchRules.add(dr);
				if (dr.selInterval == null)
					break;
			}
		} else {
			dispatchRules = null;
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

	public void delete (SystemEnvironment sysEnv)
	throws SDMSException
	{
		deleteDependingObjects(sysEnv);
		super.delete(sysEnv);
	}

	public void deleteDependingObjects(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long id = getId(sysEnv);
		Vector ivv = SDMSIntervalTable.idx_objId.getVector(sysEnv, id);
		Iterator i = ivv.iterator();
		while (i.hasNext()) {
			SDMSInterval iv = (SDMSInterval) i.next();
			if (id.equals(iv.getId(sysEnv))) continue;
			try {
				iv.delete(sysEnv);
			} catch (NotFoundException nfe) {
			}
		}
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
		if (seId == null || seId == 0) {
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
		return toString(0);
	}

	public String toString(int iindent)
	{
		String indent = "";
		for (int i = 0; i < iindent; ++i)
			indent = indent + SDMSInterval.HT;
		return  indent + "blockState [\n" +
			indent + "\tbaseStart : " + new TimerDate((int)(baseStart / 60000)).toString() + "\n" +
			indent + "\tbaseEnd   : " + new TimerDate((int)(baseEnd / 60000)).toString() + "\n" +
			indent + "\tblockStart: " + new TimerDate((int)(blockStart / 60000)).toString() + "\n" +
			indent + "\tblockEnd  : " + new TimerDate((int)(blockEnd / 60000)).toString() + "\n" +
			indent + "\tblockIdx  : " + blockIdx + "\n" +
			indent + "]";
	}

	public boolean equals(BlockState other)
	{
		return	((baseStart == other.baseStart)	&&
		         (baseEnd == other.baseEnd)	&&
		         (blockStart == other.blockStart) &&
		         (blockEnd == other.blockEnd)	&&
		         (blockIdx == other.blockIdx));
	}
}

class DispatchRule
{

	public SDMSInterval selInterval;
	public SDMSInterval fltInterval;
	public String drName;
	public String selName;
	public String fltName;
	public boolean isActive;
	public boolean isEnabled;
	public int seqNo;

	public DispatchRule ()
	{
		selInterval = null;
		fltInterval = null;
		drName = null;
		selName = null;
		fltName = null;
		isActive = false;
		isEnabled = false;
	}

	public DispatchRule (SystemEnvironment sysEnv, SDMSIntervalDispatcher dr)
	throws SDMSException
	{
		Long selIntervalId = dr.getSelectIntId(sysEnv);
		if (selIntervalId == null) {
			selInterval = null;
			selName = null;
		} else {
			selInterval = SDMSIntervalTable.getObject(sysEnv, selIntervalId);
			selName = selInterval.getName(sysEnv);
		}

		Long fltIntervalId = dr.getFilterIntId(sysEnv);
		if (fltIntervalId == null) {
			fltInterval = null;
			fltName = null;
		} else {
			fltInterval = SDMSIntervalTable.getObject(sysEnv, fltIntervalId);
			fltName = fltInterval.getName(sysEnv);
		}

		isActive = dr.getIsActive(sysEnv).booleanValue();
		isEnabled = dr.getIsEnabled(sysEnv).booleanValue();

		seqNo = dr.getSeqNo(sysEnv).intValue();
		drName = dr.getName(sysEnv);
	}

	public String toString()
	{
		return	"DispatchRule " + drName +
		        ": seqNo = " + seqNo +
		        ", sel = " + selName +
		        ", flt = " + fltName +
		        ", isActive = " + isActive +
		        ", isEnabled = " + isEnabled;
	}
}

class gntd
{
	Long minDate;
	long horizon;
	TimeZone tz;
	BlockState bs;
	Long retVal;
	int selBlPos;
	int selBlNeg;
	GregorianCalendar prevCeilGc;
	GregorianCalendar ceilGc;
	GregorianCalendar floorGc;
	GregorianCalendar nextFloorGc;
	boolean mode;

	public gntd(Long p_minDate, long p_horizon, TimeZone p_tz, BlockState p_bs, Long p_retVal, int p_selBlPos, int p_selBlNeg,
	            GregorianCalendar p_prevCeilGc, GregorianCalendar p_ceilGc, GregorianCalendar p_floorGc, GregorianCalendar p_nextFloorGc, boolean mode)
	{
		minDate = p_minDate;
		horizon = p_horizon;
		tz = p_tz;
		bs = (p_bs == null ? null : new BlockState(p_bs));
		retVal = p_retVal;
		selBlPos = p_selBlPos;
		selBlNeg = p_selBlNeg;
		prevCeilGc = (p_prevCeilGc == null ? null :(GregorianCalendar) (p_prevCeilGc.clone()));
		ceilGc = (p_ceilGc == null ? null : (GregorianCalendar) (p_ceilGc.clone()));
		floorGc = (p_floorGc == null ? null : (GregorianCalendar) (p_floorGc.clone()));
		nextFloorGc = (p_nextFloorGc == null ? null : (GregorianCalendar) (p_nextFloorGc.clone()));
		mode = mode;
	}

	public boolean equals(gntd other)
	{
		return tz.equals(other.tz) && (horizon == other.horizon) && minDate.equals(other.minDate);
	}
}

class CacheKey
{
	Long minDate;
	long horizon;
	TimeZone tz;

	public CacheKey(Long p_minDate, long p_horizon, TimeZone p_tz)
	{
		minDate = p_minDate;
		horizon = p_horizon;
		tz = p_tz;
	}
}

class GntdCache
{
	private static final int ROUND_ROBIN = 0;
	private static final int HASH = 1;
	private static final int LRU = 2;
	private static final int NOCACHE = 99;

	private static final int cacheStrategy = NOCACHE;

	private static final int maxEntries = 40;
	Vector<gntd> cache = null;

	HashMap<CacheKey, gntd> hashCache = null;

	public GntdCache()
	{
		if (cacheStrategy == ROUND_ROBIN || cacheStrategy == LRU)
			cache = new Vector<gntd>();
		if (cacheStrategy == HASH)
			hashCache = new HashMap<CacheKey, gntd>();
	}

	public gntd get(Long p_minDate, long p_horizon, TimeZone p_tz)
	{
		gntd result = null;

		if (cacheStrategy == NOCACHE)
			return null;
		if (cacheStrategy == ROUND_ROBIN) {
			gntd tstObj = new gntd(p_minDate, p_horizon, p_tz, null, null, 0, 0, null, null, null, null, false);
			Iterator<gntd> i = cache.iterator();
			while (i.hasNext()) {
				gntd cacheObj = i.next();
				if (cacheObj.equals(tstObj)) {
					result = cacheObj;
					break;
				}
			}
		}
		if (cacheStrategy == LRU) {
			gntd tstObj = new gntd(p_minDate, p_horizon, p_tz, null, null, 0, 0, null, null, null, null, false);
			for (int i = cache.size() - 1; i >= 0; --i) {
				gntd cacheObj = cache.get(i);
				if (cacheObj.equals(tstObj)) {
					cache.remove(i);
					cache.add(cacheObj);
					result = cacheObj;
					break;
				}
			}
		}
		if (cacheStrategy == HASH) {
			CacheKey key = new CacheKey (p_minDate, p_horizon, p_tz);
			result = hashCache.get(key);
		}

		return result;
	}

	private gntd privateGet(gntd key)
	{
		Iterator<gntd> i = cache.iterator();
		while (i.hasNext()) {
			gntd cacheObj = i.next();
			if (cacheObj.equals(key)) return cacheObj;
		}
		return null;
	}

	public void add(gntd result)
	{
		gntd old = null;
		CacheKey key = null;

		if (cacheStrategy == NOCACHE)
			return;
		if (cacheStrategy == ROUND_ROBIN || cacheStrategy == LRU) {
			old = privateGet(result);
		}
		if (cacheStrategy == HASH) {
			key = new CacheKey(result.minDate, result.horizon, result.tz);
			old = hashCache.get(key);
		}
		if (old != null) {
			if ((result.retVal != null && !result.retVal.equals(old.retVal)) ||
			    (result.retVal == null && old.retVal != null) ||
			    (!result.bs.equals(old.bs)) ||
			    (result.selBlPos != old.selBlPos) ||
			    (result.selBlNeg != old.selBlNeg) ||
			    (result.prevCeilGc != null && !result.prevCeilGc.equals(old.prevCeilGc)) ||
			    (result.ceilGc != null && !result.ceilGc.equals(old.ceilGc)) ||
			    (result.floorGc != null && !result.floorGc.equals(old.floorGc)) ||
			    (result.nextFloorGc != null && !result.nextFloorGc.equals(old.nextFloorGc)) ||
			    (result.prevCeilGc == null && old.prevCeilGc != null) ||
			    (result.ceilGc == null && old.ceilGc != null) ||
			    (result.floorGc == null && old.floorGc != null) ||
			    (result.nextFloorGc == null && old.nextFloorGc != null) ||
			    (result.mode != old.mode)
			   ) {
				System.out.println ("Cache Diskrepanz");
			}
			return;
		}

		if (cacheStrategy == ROUND_ROBIN || cacheStrategy == LRU) {
			while (cache.size() >= maxEntries)
				cache.removeElementAt(0);
			cache.add(result);
		}
		if (cacheStrategy == HASH) {
			hashCache.put(key, result);
		}
	}
}

