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

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.util.*;

public class SubmitJob extends Node
{

	public final static String __version = "@(#) $Id: SubmitJob.java,v 2.17.4.2 2013/06/18 09:49:39 ronald Exp $";

	Long se_id = null;
	Vector path = null;
	String name = null;
	WithHash with;

	public SubmitJob(Vector p, WithHash w)
	{
		super();
		cmdtype = Node.USER_COMMAND | Node.JOB_COMMAND;
		path = p;
		name = (String) p.remove(p.size() -1);
		with = w;
	}

	public SubmitJob(Long id, WithHash w)
	{
		super();
		cmdtype = Node.USER_COMMAND | Node.JOB_COMMAND;
		se_id = id;
		with = w;
	}

	public SubmitJob(String n, WithHash w)
	{
		super();
		cmdtype = Node.JOB_COMMAND;
		path = null;
		name = n;
		with = w;
	}

	public static Long evalResumeObj(SystemEnvironment sysEnv, Object resumeObj, Long refTime, boolean adjust, TimeZone tz)
		throws SDMSException
	{
		Long resumeTs = null;

		if (resumeObj == null) return resumeTs;
		if (resumeObj instanceof WithHash) {
			WithHash wh = (WithHash) resumeObj;
			return evalResumeObj(sysEnv, null, (Integer) wh.get(ParseStr.S_MULT), (Integer) wh.get(ParseStr.S_INTERVAL), refTime, adjust, tz);
		}
		return evalResumeObj(sysEnv, (String) resumeObj, null, null, refTime, adjust, tz);
	}

	public static long getResumeInValue(SystemEnvironment sysEnv, Integer resumeIn, Integer resumeBase)
		throws SDMSException
	{
		if (resumeIn == null) return 0;
		if (resumeBase == null) return 0;
		int m = resumeIn.intValue();
		int i = resumeBase.intValue();
		long no_msecs = m * 60000;
		switch (i) {
			case SDMSInterval.MINUTE:
				break;
			case SDMSInterval.HOUR:
				no_msecs *= 60;
				break;
			case SDMSInterval.DAY:
				no_msecs *= 24 * 60;
				break;
			case SDMSInterval.WEEK:
				no_msecs *= 7 * 24 * 60;
				break;
			case SDMSInterval.MONTH:
				no_msecs *= 30 * 24 * 60;
				break;
			case SDMSInterval.YEAR:
				no_msecs *= 365 * 24 * 60;
				break;
		}
		return no_msecs;
	}

	public static Long evalResumeObj(SystemEnvironment sysEnv, String resumeAt, Integer resumeIn, Integer resumeBase, Long refTime, boolean adjust, TimeZone tz)
	throws SDMSException
	{
		Long resumeTs = null;
		long now;
		if (refTime != null)	now = refTime.longValue();
		else 			now = System.currentTimeMillis();

		if (resumeAt == null && resumeIn == null) return resumeTs;
		if (resumeIn != null) {
			if (resumeBase == null) {
			}
			resumeTs = Long.valueOf (now + getResumeInValue(sysEnv, resumeIn, resumeBase));
		} else {
			if (resumeAt != null) {
				DateTime dt = new DateTime(resumeAt, tz);
				if(dt.year == -1) {
					dt.setMissingFieldsFromReference(new Date(now), adjust);
				} else {
					dt.fixToMinDate();
				}
				Date d = dt.toDate();
				resumeTs = d.getTime();
			}
		}

		if (resumeTs.longValue() <= now) return SDMSConstants.lMINUS_ONE;

		return resumeTs;
	}

	public Long master_submit(SystemEnvironment sysEnv, String submitTag, String timeZone)
		throws SDMSException
	{
		SDMSSchedulingEntity se;
		if (se_id == null) {
			se = (SDMSSchedulingEntity)SDMSSchedulingEntityTable.get(sysEnv, path, name);
		} else {
			se = SDMSSchedulingEntityTable.getObject(sysEnv, se_id);
		}
		Boolean suspend = (Boolean) with.get(ParseStr.S_SUSPEND);
		Object resumeObj = with.get(ParseStr.S_RESUME);
		Vector params = (Vector) with.get(ParseStr.S_PARAMETERS);
		Integer niceValue = (Integer) with.get(ParseStr.S_NICEVALUE);
		Integer unresolvedHandling = (Integer) with.get(ParseStr.S_UNRESOLVED);
		String childTag = (String) with.get(ParseStr.S_CHILDTAG);
		Long resumeTs = null;
		Boolean enable = (Boolean) with.get(ParseStr.S_ENABLE);
		if (enable != null)
			if (!enable.booleanValue())
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03007181310", "A disabled Master Submit is not allowed"));

		if(unresolvedHandling != null) {
			if(unresolvedHandling.intValue() == SDMSDependencyDefinition.DEFER) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03407082029", "DEFER is not a valid option"));
			}
			if(unresolvedHandling.intValue() == SDMSDependencyDefinition.ERROR) {
				unresolvedHandling = null;
			}
		}

		final Long uId = env.uid();
		Long gId;
		String auditMsg;
		if (env.isUser()) {
			final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
			if(!with.containsKey(ParseStr.S_GROUP)) {
				gId = u.getDefaultGId(sysEnv);
			} else {
				final String gName = (String) with.get(ParseStr.S_GROUP);
				gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
				              sysEnv, new SDMSKey(gName, SDMSConstants.lZERO)).getId(sysEnv);
			}
			auditMsg = "manually submitted";
		} else {
			SDMSSubmittedEntity submittingSme = SDMSSubmittedEntityTable.getObject(sysEnv, uId);
			gId = submittingSme.getOwnerId(sysEnv);
			auditMsg = "submitted by job " + uId;
		}

		boolean job2user = false;
		try {
			if (env.isJob()) {
				job2user = true;
				env.setUser();
			}
			if(!se.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT)) {
				throw new AccessViolationException(
					new SDMSMessage(sysEnv, "03312181437", "Insufficient privileges for submitting $1", se.pathString(sysEnv))
				);
			}
		} catch (SDMSException e) {
			throw e;
		} finally {
			if (job2user) {
				env.setJob();
			}
		}
		se.checkSubmitForGroup(sysEnv, gId);

		if (suspend != null && suspend.booleanValue()) {
			resumeTs = evalResumeObj(sysEnv, resumeObj, null, true, TimeZone.getTimeZone(timeZone));

			if (resumeTs != null && resumeTs.longValue() == -1l) {
				suspend = Boolean.FALSE;
				resumeTs = null;
			}
		}

		final SDMSSubmittedEntity sme = se.submitMaster (sysEnv, params, suspend == null ? null : Integer.valueOf(suspend ? SDMSSubmittedEntity.SUSPEND : SDMSSubmittedEntity.NOSUSPEND),
								resumeTs, gId, niceValue,
								auditMsg, submitTag, childTag, unresolvedHandling, timeZone);
		return sme.getId(sysEnv);
	}

	public Long child_submit(SystemEnvironment sysEnv, String submitTag)
		throws SDMSException
	{
		Integer suspended = (Integer) with.get(ParseStr.S_SUSPEND);
		Object resumeObj = with.get(ParseStr.S_RESUME);
		Vector params = (Vector) with.get(ParseStr.S_PARAMETERS);
		String childTag = (String) with.get(ParseStr.S_CHILDTAG);
		Integer unresolvedHandling = (Integer) with.get(ParseStr.S_UNRESOLVED);
		Long resumeTs = null;
		Boolean enable = (Boolean) with.get(ParseStr.S_ENABLE);
		if (enable == null)
			enable = Boolean.TRUE;

		if(unresolvedHandling != null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03407082033", "UNRESOLVED only allowed for master submit"));
		}
		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, sysEnv.cEnv.uid());
		long version = sme.getSeVersion(sysEnv).longValue();
		SDMSSchedulingEntity se = null;
		if(path != null)
			se = SDMSSchedulingEntityTable.get(sysEnv, path, name, version);
		else {
			Vector v = SDMSSchedulingHierarchyTable.idx_parentId_aliasName.getVector(sysEnv, new SDMSKey(sme.getSeId(sysEnv), name), version);
			if(v.size() == 0) {
				throw new CommonErrorException( new SDMSMessage(sysEnv, "03212060125", "alias not found"));
			}
			se = SDMSSchedulingEntityTable.getObject(sysEnv, ((SDMSSchedulingHierarchy) v.get(0)).getSeChildId(sysEnv), version);
		}
		resumeTs = evalResumeObj(sysEnv, resumeObj, null, true, sme.getEffectiveTimeZone(sysEnv));

		Integer state = sme.getState(sysEnv);
		if (state == SDMSSubmittedEntity.STARTING ||
		    state == SDMSSubmittedEntity.STARTED ||
		    state == SDMSSubmittedEntity.RUNNING ||
		    state == SDMSSubmittedEntity.TO_KILL ||
		    state == SDMSSubmittedEntity.KILLED ||
		    state == SDMSSubmittedEntity.BROKEN_ACTIVE ||
		    sme.getIsSuspended(sysEnv).intValue() != SDMSSubmittedEntity.NOSUSPEND) {
			if (sme.getIsCancelled(sysEnv).booleanValue())
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03703020852", "Child submit rejected because job is cancelling"));
			final SDMSSubmittedEntity smec = sme.submitChild(sysEnv, params, suspended, resumeTs, se.getId(sysEnv), childTag, null, submitTag, enable.booleanValue());
			return smec.getId(sysEnv);
		} else
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03703020853", "Child submit only allowed while job is active"));
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");

		Vector data = new Vector();

		String submitTag = (String) with.get(ParseStr.S_SUBMITTAG);
		if (submitTag != null) {
			if (SDMSSubmittedEntityTable.idx_submitTag.containsKey(sysEnv, submitTag)) {
				throw new CommonErrorException( new SDMSMessage(sysEnv, "03406031553",
									"Job with submit tag $1 already submitted", submitTag));
			}
		}

		String tz;
		if (with.containsKey(ParseStr.S_TIME)) {
			tz = (String) with.get(ParseStr.S_TIME);
			TimeZone tmp = TimeZone.getTimeZone(tz);
			if (!tz.equals(tmp.getID())) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03207031503", "Time Zone " + tz + " unknown"));
			}
		} else {
			TimeZone tmp = TimeZone.getDefault();
			tz = tmp.getID();
		}

		Long id;
		if(sysEnv.cEnv.isUser()) {
			Boolean checkOnly = (Boolean) with.get(ParseStr.S_CHECK_ONLY);
			boolean co = (checkOnly == null ? false : checkOnly.booleanValue());

			if(path == null && se_id == null)
				throw new CommonErrorException( new SDMSMessage(sysEnv, "03212060119", "You cannot submit by alias as a user"));
			if(co) sysEnv.tx.beginSubTransaction(sysEnv);
			id = master_submit(sysEnv, submitTag, tz);
			if(co) {
				sysEnv.tx.rollbackSubTransaction(sysEnv);
				result.setFeedback(new SDMSMessage(sysEnv, "03304141142","Job submit checked successfully"));
				return;
			}
		} else {
			if (with.containsKey(ParseStr.S_MASTER)) {
				if (path == null && se_id == null) {
					throw new CommonErrorException( new SDMSMessage(sysEnv, "03801291249", "A master submit by alias is not supported"));
				}
				id = master_submit(sysEnv, submitTag, tz);
			} else {
				id = child_submit(sysEnv, submitTag);
			}
		}

		data.add(id);
		d_container = new SDMSOutputContainer(sysEnv, "Submit", desc, data);
		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "02201170716","Job submitted"));
	}

}
