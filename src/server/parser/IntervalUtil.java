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
package de.independit.scheduler.server.parser;

import java.util.*;
import java.util.regex.Pattern;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.timer.TimerDate;
import de.independit.scheduler.server.util.*;

public class IntervalUtil
{
	public static final String __version = "@(#) $Id: IntervalUtil.java,v 2.4.2.1 2013/03/14 10:24:34 ronald Exp $";

	public static final int IGNORED_NOTHING     = 0;
	public static final int IGNORED_UPPER_RANGE = 1;
	public static final int IGNORED_SECONDS     = 2;

	private static final Pattern ID_NAME        = Pattern.compile ("^S\\d+_.+$");
	private static final Pattern ID_QUOTED_NAME = Pattern.compile ("^'S\\d+_.+'$");

	private static final Pattern ID_PATH = Pattern.compile ("^S\\d+$");

	private static final Pattern ID_REPLACE = Pattern.compile ("\\d+");

	private static final Integer ivalType = SDMSConstants.IV_INTERVAL;
	private static final Integer dispType = SDMSConstants.IV_INTERVAL_DISPATCHER;

	public static final boolean matchesIdName (final String name)
	{
		return ID_NAME.matcher (name).matches();
	}

	public static final boolean matchesQuotedIdName (final String name)
	{
		return ID_QUOTED_NAME.matcher (name).matches();
	}

	public static final boolean matchesIdPath (final Vector path)
	{
		if (path.size() <= 1)
			return false;

		final String item = (String) path.get (1);
		return ID_PATH.matcher (item).matches();
	}

	static final Long getCheckedSeId (final SystemEnvironment sysEnv, final WithItem seSpec)
		throws SDMSException
	{
		if (seSpec == null)
			return null;

		final Long seId;
		if (seSpec.key == null)
			seId = (Long) seSpec.value;
		else {
			final Vector path = (Vector) seSpec.key;
			final String name = (String) seSpec.value;

			final Long folderId = SDMSFolderTable.pathToId (sysEnv, path);
			final SDMSKey key = new SDMSKey (folderId, name);
			final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.idx_folderId_name_getUnique (sysEnv, key);
			seId = se.getId (sysEnv);
		}

		if (seId != 0) {
			try {
				SDMSSchedulingEntityTable.getObject (sysEnv, seId);
			}

			catch (final NotFoundException nfe) {
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04410300037", "No Batch or Job with id $1 exists", seId));
			}
		}

		return seId;
	}

	static final String mapIdName (final String name, final Long seId)
	{
		return ((seId != null) && matchesIdName (name)) ? ID_REPLACE.matcher (name).replaceFirst (seId.toString()) : name;
	}

	static final String mapIdPath (final String name, final Long seId)
	{
		return ((seId != null) && ID_PATH.matcher (name).matches()) ? ID_REPLACE.matcher (name).replaceFirst (seId.toString()) : name;
	}

	static final Vector mapIdPath (final Vector path, final Long seId)
	{
		if ((seId == null) || ! matchesIdPath (path))
			return path;

		final String item = (String) path.get (1);
		final String mappedItem = ID_REPLACE.matcher (item).replaceFirst (seId.toString());

		if (mappedItem.equals (item))
			return path;

		final PathVector mappedPath = new PathVector (path);
		mappedPath.set (1, mappedItem);

		return mappedPath;
	}

	public static final Integer getMultiplier (final SystemEnvironment sysEnv, final WithHash with)
		throws SDMSException
	{
		final Integer multiplier = (Integer) with.get (ParseStr.S_MULT);

		if (multiplier == null)
			return SDMSConstants.iONE;

		if (multiplier.intValue() <= 0)
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04207191659", "multiplier must be greater than 0"));

		return multiplier;
	}

	public static final boolean getDateTime (final SystemEnvironment sysEnv, final DateTime datetime, final WithHash with, final String which)
		throws SDMSException
	{
		boolean secondsIgnore = false;

		final DateTime dt = (DateTime) with.get (which);

		if (dt.year == -1)
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04210011547", which + " must contain a year"));

		if (dt.suppressSeconds())
			secondsIgnore = true;

		datetime.set (dt);

		return secondsIgnore;
	}

	public static final void killDispatcher (final SystemEnvironment sysEnv, final Long ivalId)
	throws SDMSException
	{
		final Vector drList = SDMSIntervalDispatcherTable.idx_intId.getVector (sysEnv, ivalId);
		final Iterator drIt = drList.iterator();
		while (drIt.hasNext()) {
			final SDMSIntervalDispatcher dr = (SDMSIntervalDispatcher) drIt.next();
			dr.delete (sysEnv);
		}
	}

	public static final void createDispatcher (final SystemEnvironment sysEnv, final Long ivalId, final WithHash with, int recursionLevel)
	throws SDMSException
	{
		SDMSInterval iv = null;
		final Vector dispatchList = (Vector) with.get (ParseStr.S_DISPATCH);
		if (dispatchList == null) {
			return;
		}

		for (int seqNo = 0; seqNo < dispatchList.size(); ++seqNo) {
			Vector v = (Vector) dispatchList.get(seqNo);
			String name = (String) v.get(0);
			Boolean isActive = (Boolean) v.get(1);
			Vector dispRule = (Vector) v.get(2);
			Object selectIval = dispRule.get(0);
			Object filterIval = dispRule.get(1);
			Boolean isEnabled = (Boolean) dispRule.get(2);
			SDMSIntervalDispatcher intDisp = SDMSIntervalDispatcherTable.table.create(sysEnv, ivalId, Integer.valueOf(seqNo), name, null, null, isEnabled, isActive);
			Long intDispId = intDisp.getId(sysEnv);
			if (selectIval != null) {
				if (selectIval instanceof String) {
					iv = (SDMSInterval) SDMSIntervalTable.idx_name_objId.getUnique(sysEnv, new SDMSKey((String) selectIval, null));
					if ((iv.getPrivileges(sysEnv).toLong() & SDMSPrivilege.VIEW) != SDMSPrivilege.VIEW) {
						throw new CommonErrorException (new SDMSMessage (sysEnv, "03308181538", "Insufficient privileges to use Interval " + selectIval.toString()));
					}
				} else {
					CreateInterval ci = (CreateInterval) selectIval;
					ci.setEnv(sysEnv.cEnv);
					ci.go(sysEnv, recursionLevel);
					iv = ci.getIval();
					iv.setObjId(sysEnv, intDispId);
					iv.setObjType(sysEnv, dispType);
				}
				intDisp.setSelectIntId(sysEnv, iv.getId(sysEnv));
			} else {
			}
			if (filterIval != null) {
				if (filterIval instanceof String) {
					iv = (SDMSInterval) SDMSIntervalTable.idx_name_objId.getUnique(sysEnv, new SDMSKey((String) filterIval, null));
					if ((iv.getPrivileges(sysEnv).toLong() & SDMSPrivilege.VIEW) != SDMSPrivilege.VIEW) {
						throw new CommonErrorException (new SDMSMessage (sysEnv, "03308181539", "Insufficient privileges to use Interval " + selectIval.toString()));
					}
				} else {
					CreateInterval ci = (CreateInterval) filterIval;
					ci.setEnv(sysEnv.cEnv);
					ci.go(sysEnv, recursionLevel);
					iv = ci.getIval();
					iv.setObjId(sysEnv, intDispId);
					iv.setObjType(sysEnv, dispType);
				}
				intDisp.setFilterIntId(sysEnv, iv.getId(sysEnv));
			} else {
			}
		}
	}

	public static final void killFilter (final SystemEnvironment sysEnv, final Long ivalId)
		throws SDMSException
	{
		final Vector ihList = SDMSIntervalHierarchyTable.idx_parentId.getVector (sysEnv, ivalId);
		final Iterator ihIt = ihList.iterator();
		while (ihIt.hasNext()) {
			final SDMSIntervalHierarchy ih = (SDMSIntervalHierarchy) ihIt.next();
			Long filterIvalId = ih.getChildId(sysEnv);
			SDMSInterval filterIval = SDMSIntervalTable.getObject(sysEnv, filterIvalId);
			Long objId = filterIval.getObjId(sysEnv);
			if (ivalId.equals(objId))
				filterIval.delete(sysEnv);
			ih.delete (sysEnv);
		}
	}

	public static final void killEmbedded (final SystemEnvironment sysEnv, final Long ivalId)
	throws SDMSException
	{
		SDMSInterval ival = SDMSIntervalTable.getObject(sysEnv, ivalId);
		Long embIntId = ival.getEmbeddedIntervalId(sysEnv);
		if (embIntId == null)
			return;
		SDMSInterval embIval = SDMSIntervalTable.getObject(sysEnv, embIntId);
		Long objId = embIval.getObjId(sysEnv);
		if (ivalId.equals(objId)) {
			embIval.delete(sysEnv);
		}
	}

	public static final boolean createFilter (final SystemEnvironment sysEnv, final Long ivalId, final WithHash with, int recursionLevel)
	throws SDMSException
	{
		return createFilter (sysEnv, ivalId, with, null, recursionLevel);
	}

	public static final boolean createFilter (final SystemEnvironment sysEnv, final Long ivalId, final WithHash with, Long se_id, int recursionLevel)
		throws SDMSException
	{
		final Vector filtList = (Vector) with.get (ParseStr.S_FILTER);
		if (filtList == null)
			return false;

		final HashSet filtSet = new HashSet (filtList.size());
		boolean duplicateFilterIgnore = false;

		final Iterator filtIt = filtList.iterator();
		while (filtIt.hasNext()) {
			Object filtItem = filtIt.next();
			if (filtItem instanceof String) {
				String filtName = (String) filtItem;
				if (se_id != null) {
					filtName =  IntervalUtil.mapIdName (filtName, se_id);
				}
				SDMSInterval filtIval;
				try {
					filtIval  = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(filtName, ivalId));
				} catch (NotFoundException nfe) {
					filtIval  = SDMSIntervalTable.idx_name_objId_getUnique (sysEnv, new SDMSKey(filtName, null));
				}
				final Long filtId = filtIval.getId (sysEnv);

				if (filtId.equals (ivalId))
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04209270132", "interval cannot filter itself"));

				if (filtSet.contains (filtId))
					duplicateFilterIgnore = true;
				else {
					filtSet.add (filtId);

					SDMSIntervalHierarchyTable.table.create (sysEnv, filtId, ivalId);
				}
			} else {
				CreateInterval ci = (CreateInterval) filtItem;
				ci.setEnv(sysEnv.cEnv);
				ci.go(sysEnv, recursionLevel);
				SDMSInterval filterIval = ci.getIval();
				Long filterIvalId = ci.getIvalId();
				filterIval.setObjId(sysEnv, ivalId);
				filterIval.setObjType(sysEnv, ivalType);

				filtSet.add (filterIvalId);

				SDMSIntervalHierarchyTable.table.create (sysEnv, filterIvalId, ivalId);
			}
		}

		return duplicateFilterIgnore;
	}

	public static final void killSelections (final SystemEnvironment sysEnv, final Long ivalId)
		throws SDMSException
	{
		final Vector isList = SDMSIntervalSelectionTable.idx_intId.getVector (sysEnv, ivalId);
		final Iterator isIt = isList.iterator();
		while (isIt.hasNext()) {
			final SDMSIntervalSelection is = (SDMSIntervalSelection) isIt.next();
			is.delete (sysEnv);
		}
	}

	public static final int createSelections (final SystemEnvironment sysEnv, final Long ivalId, final WithHash with)
		throws SDMSException
	{
		final Vector selList = (Vector) with.get (ParseStr.S_SELECTION);
		if (selList == null)
			return IGNORED_NOTHING;

		int rc = IGNORED_NOTHING;
		final Iterator selIt = selList.iterator();
		while (selIt.hasNext()) {
			final WithItem selItem = (WithItem) selIt.next();

			Integer value = null;
			Long periodFrom = null;
			Long periodTo = null;

			if (selItem.key.equals (ParseStr.S_SELECT_NUM)) {
				value = (Integer) selItem.value;

				if (value.intValue() == 0)
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04207262333", "selection must not be 0"));
			} else if (selItem.key.equals (ParseStr.S_SELECT_RANGE)) {
				final WithHash period = (WithHash) selItem.value;

				final DateTime dtFrom = (DateTime) period.get (ParseStr.S_SELECT_FROM);
				if (dtFrom.second != -1)
					rc = Math.max (rc, IGNORED_SECONDS);

				DateTime dtTo = (DateTime) period.get (ParseStr.S_SELECT_TO);
				if (dtTo == null)
					dtTo = new DateTime (dtFrom);
				else if ((   (dtFrom.year   == -1) != (dtTo.year   == -1))
					 || ((dtFrom.month  == -1) != (dtTo.month  == -1))
					 || ((dtFrom.week   == -1) != (dtTo.week   == -1))
					 || ((dtFrom.day    == -1) != (dtTo.day    == -1))
					 || ((dtFrom.hour   == -1) != (dtTo.hour   == -1))
					 || ((dtFrom.minute == -1) != (dtTo.minute == -1))
					 || ((dtFrom.second == -1) != (dtTo.second == -1)))
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04210221611", "both limits must have the same type"));
				if (dtTo.second != -1)
					rc = Math.max (rc, IGNORED_SECONDS);

				dtFrom.suppressSeconds();
				dtFrom.fixToMinDate();

				dtTo.suppressSeconds();
				dtTo.fixToMaxDate();

				final long comp = dtFrom.compareTo (dtTo);
				if ((comp == 0) && (period.get (ParseStr.S_SELECT_TO) != null))
					rc = Math.max (rc, IGNORED_UPPER_RANGE);

				periodFrom = dtFrom.toLong();
				periodTo   = comp == 0 ? null : dtTo.toLong();
			} else
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04207181833", "unknown selection type " + selItem.key));

			SDMSIntervalSelectionTable.table.create (sysEnv, ivalId, value, periodFrom, periodTo);
		}

		return rc;
	}

	private IntervalUtil()
	{
	}
}
