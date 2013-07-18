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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class SubmitJob extends Node
{

	public final static String __version = "@(#) $Id: SubmitJob.java,v 2.17.4.2 2013/06/18 09:49:39 ronald Exp $";

	Vector path;
	String name;
	WithHash with;

	public SubmitJob(Vector p, WithHash w)
	{
		super();
		cmdtype = Node.USER_COMMAND | Node.JOB_COMMAND;
		path = p;
		name = name = (String) p.remove(p.size() -1);;
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

	public static Long evalResumeObj(SystemEnvironment sysEnv, Object resumeObj, Long refTime, boolean adjust)
	throws SDMSException
	{
		Long resumeTs = null;

		if (resumeObj == null) return resumeTs;
		if (resumeObj instanceof WithHash) {
			WithHash wh = (WithHash) resumeObj;
			return evalResumeObj(sysEnv, null, (Integer) wh.get(ParseStr.S_MULT), (Integer) wh.get(ParseStr.S_INTERVAL), refTime, adjust);
		}
		return evalResumeObj(sysEnv, (String) resumeObj, null, null, refTime, adjust);
	}

	public static Long evalResumeObj(SystemEnvironment sysEnv, String resumeAt, Integer resumeIn, Integer resumeBase, Long refTime, boolean adjust)
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
			resumeTs = new Long (now + no_msecs);
		} else {
			if (resumeAt != null) {
				DateTime dt = new DateTime(resumeAt);
				if(dt.year == -1) {
					dt.setMissingFieldsFromReference(new Date(now), adjust);
				} else {
					dt.fixToMinDate();
				}
				Date d = dt.toDate();
				resumeTs = d.getTime();
			}
		}

		if (resumeTs.longValue() <= now) return new Long(-1);

		return resumeTs;
	}

	public Long master_submit(SystemEnvironment sysEnv, String submitTag)
	throws SDMSException
	{
		SDMSSchedulingEntity se = (SDMSSchedulingEntity)SDMSSchedulingEntityTable.get(sysEnv, path, name);
		Boolean suspend = (Boolean) with.get(ParseStr.S_SUSPEND);
		Object resumeObj = with.get(ParseStr.S_RESUME);
		Vector params = (Vector) with.get(ParseStr.S_PARAMETERS);
		Integer niceValue = (Integer) with.get(ParseStr.S_NICEVALUE);
		Integer unresolvedHandling = (Integer) with.get(ParseStr.S_UNRESOLVED);
		String childTag = (String) with.get(ParseStr.S_CHILDTAG);
		Long resumeTs = null;

		if(unresolvedHandling != null) {
			if(unresolvedHandling.intValue() == SDMSDependencyDefinition.DEFER) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03407082029", "DEFER is not a valid option"));
			}
			if(unresolvedHandling.intValue() == SDMSDependencyDefinition.ERROR) {
				unresolvedHandling = null;
			}
		}

		final Long uId = env.uid();
		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		final Long gId;
		if(!with.containsKey(ParseStr.S_GROUP)) {
			gId = u.getDefaultGId(sysEnv);
		} else {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
			              sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
		}
		if(!se.checkPrivileges(sysEnv, SDMSPrivilege.SUBMIT)) {
			throw new AccessViolationException(
			        new SDMSMessage(sysEnv, "03312181437", "Insufficient privileges for submitting $1", se.pathString(sysEnv))
			);
		}
		se.checkSubmitForGroup(sysEnv, gId);

		if (suspend != null && suspend.booleanValue()) {
			resumeTs = evalResumeObj(sysEnv, resumeObj, null, true );

			if (resumeTs != null && resumeTs.longValue() == -1l) {
				suspend = new Boolean(false);
				resumeTs = null;
			}
		}

		final SDMSSubmittedEntity sme = se.submitMaster (sysEnv, params, suspend, resumeTs, gId, niceValue,
		                                "manually submitted", submitTag, childTag, unresolvedHandling);
		return sme.getId(sysEnv);
	}

	public Long child_submit(SystemEnvironment sysEnv, String submitTag)
	throws SDMSException
	{
		Boolean suspend = (Boolean) with.get(ParseStr.S_SUSPEND);
		Object resumeObj = with.get(ParseStr.S_RESUME);
		Vector params = (Vector) with.get(ParseStr.S_PARAMETERS);
		String childTag = (String) with.get(ParseStr.S_CHILDTAG);
		Integer unresolvedHandling = (Integer) with.get(ParseStr.S_UNRESOLVED);
		Long resumeTs = null;

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
		resumeTs = evalResumeObj(sysEnv, resumeObj, null, true);

		final SDMSSubmittedEntity smec = sme.submitChild(sysEnv, params, suspend, resumeTs, se.getId(sysEnv), childTag, null, submitTag);
		return smec.getId(sysEnv);
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

		Long id;
		if(sysEnv.cEnv.isUser()) {
			Boolean checkOnly = (Boolean) with.get(ParseStr.S_CHECK_ONLY);
			boolean co = (checkOnly == null ? false : checkOnly.booleanValue());

			if(path == null)
				throw new CommonErrorException( new SDMSMessage(sysEnv, "03212060119", "You cannot submit by alias as a user"));
			if(co) sysEnv.tx.beginSubTransaction(sysEnv);
			id = master_submit(sysEnv, submitTag);
			if(co) {
				sysEnv.tx.rollbackSubTransaction(sysEnv);
				result.setFeedback(new SDMSMessage(sysEnv, "03304141142","Job submit checked successfully"));
				return;
			}
		} else {
			id = child_submit(sysEnv, submitTag);
		}

		data.add(id);
		d_container = new SDMSOutputContainer(sysEnv, "Submit", desc, data);
		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "02201170716","Job submitted"));
	}

}
