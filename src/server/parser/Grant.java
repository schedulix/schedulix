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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class Grant extends Node
{

	public final static String __version = "@(#) $Id: Grant.java,v 2.20.2.2 2013/03/16 10:35:53 dieter Exp $";

	private boolean isGrant;
	private Vector groupList;
	private long lpriv;
	private ObjectURL url;
	private Boolean isRecursive;
	private Boolean isForce;
	private Boolean childrenOnly;
	private int thisType;

	private static final Integer SYSTEM = Integer.valueOf(SDMSGrant.SYSTEM);

	public Grant(Boolean g, Long p, ObjectURL u, Vector grp, Boolean r, Boolean c)
	{
		super();
		isGrant = g.booleanValue();
		groupList = grp;
		lpriv = p.longValue();
		url = u;
		if(r == null) {
			isRecursive = Boolean.FALSE;
			isForce = Boolean.FALSE;
		} else if(r.booleanValue()) {
			isRecursive = Boolean.TRUE;
			isForce = Boolean.TRUE;
		} else {
			isRecursive = Boolean.FALSE;
			isForce = Boolean.TRUE;
		}
		childrenOnly = c;
	}

	public Grant(Boolean g, Integer t, Vector grp)
	{
		super();

		isGrant = g.booleanValue();
		groupList = grp;
		childrenOnly = Boolean.FALSE;
		isForce = Boolean.FALSE;
		isRecursive = Boolean.FALSE;
		url = null;
		if ((t.intValue() != Parser.SYSTEM) && (t.intValue() != Parser.MANAGE_SELECT)) {
			ObjectURL o = new ObjectURL(t);
			thisType = o.objType.intValue();
		} else	{
			if (t.intValue() == Parser.SYSTEM)
				thisType = SDMSGrant.SYSTEM;
			else
				thisType = SDMSGrant.SELECT;
		}

		lpriv = getManagePriv(thisType);
	}

	private long getManagePriv(int type)
	{
		switch (type) {
			case SDMSGrant.EXIT_STATE_DEFINITION:		return SDMSPrivilege.MANAGE_ESD;
			case SDMSGrant.EXIT_STATE_PROFILE:		return SDMSPrivilege.MANAGE_ESP;
			case SDMSGrant.EXIT_STATE_MAPPING:		return SDMSPrivilege.MANAGE_ESM;
			case SDMSGrant.EXIT_STATE_TRANSLATION:		return SDMSPrivilege.MANAGE_EST;
			case SDMSGrant.RESOURCE_STATE_DEFINITION:	return SDMSPrivilege.MANAGE_RSD;
			case SDMSGrant.RESOURCE_STATE_PROFILE:		return SDMSPrivilege.MANAGE_RSP;
			case SDMSGrant.RESOURCE_STATE_MAPPING:		return SDMSPrivilege.MANAGE_RSM;
			case SDMSGrant.FOOTPRINT:			return SDMSPrivilege.MANAGE_FP;
			case SDMSGrant.USER:				return SDMSPrivilege.MANAGE_USER;
			case SDMSGrant.GROUP:				return SDMSPrivilege.MANAGE_GROUP;
			case SDMSGrant.ENVIRONMENT:			return SDMSPrivilege.MANAGE_ENV;
			case SDMSGrant.SYSTEM:				return SDMSPrivilege.MANAGE_SYS;
			case SDMSGrant.SELECT:				return SDMSPrivilege.MANAGE_SEL;
			case SDMSGrant.NICE_PROFILE:			return SDMSPrivilege.MANAGE_NP;
			case SDMSGrant.WATCH_TYPE:			return SDMSPrivilege.MANAGE_WT;
		}
		return SDMSConstants.PR_NOPRIVS;
	}

	private void checkObjectType(SystemEnvironment sysEnv, int type)
		throws SDMSException
	{
		if(type == SDMSGrant.ENVIRONMENT)	return;
		if(type == SDMSGrant.NAMED_RESOURCE)	return;
		if(type == SDMSGrant.FOLDER)		return;
		if(type == SDMSGrant.JOB_DEFINITION)	return;
		if(type == SDMSGrant.JOB)		return;
		if(type == SDMSGrant.SCOPE)		return;
		if(type == SDMSGrant.EVENT)		return;
		if(type == SDMSGrant.INTERVAL)		return;
		if(type == SDMSGrant.SCHEDULE)		return;
		if(type == SDMSGrant.GROUP)		return;
		if(type == SDMSGrant.RESOURCE)		return;
		if(type == SDMSGrant.OBJECT_MONITOR)	return;
		if(type == SDMSGrant.NICE_PROFILE)	return;
		throw new CommonErrorException(new SDMSMessage(sysEnv, "03601231458", "Objecttype not supported"));
	}

	private int adjustType(SystemEnvironment sysEnv, SDMSProxy p, int type)
		throws SDMSException
	{
		if(type == SDMSGrant.FOLDER) {
			if(p instanceof SDMSSchedulingEntity) return SDMSGrant.JOB_DEFINITION;
		}
		return type;
	}

	private Vector makeGroupList(SystemEnvironment sysEnv)
		throws SDMSException
	{
		final Vector result = new Vector();

		for (int i = 0; i < groupList.size(); i++) {
			final String name = (String) groupList.get(i);
			final SDMSGroup g = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(name, SDMSConstants.lZERO));
			if (isGrant && !g.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				continue;
			final Long gId = g.getId(sysEnv);
			result.addElement(gId);
		}
		if (result.size() == 0) {
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03401171214", "Insufficient privileges"));
		}
		return result;
	}

	private void performGrant(SystemEnvironment sysEnv, SDMSProxy p, int type, long lpriv, Vector gList, boolean recursive, boolean force)
		throws SDMSException
	{
		SDMSGrant g;
		Long gId;
		Long oId;
		Long oldPrivs;
		long newPrivs = lpriv;

		if(!p.checkPrivileges(sysEnv, SDMSPrivilege.GRANT)) {
			if(force) return;
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03402191311", "Insufficient privileges"));
		}
		if((p.getPrivilegeMask() & lpriv) != lpriv) {
			if (force) {
				newPrivs = p.getPrivilegeMask() & lpriv;
			} else {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03402240938", "Incompatible grant"));
			}
		}

		oId = p.getId(sysEnv);

		SDMSPrivilege pr = new SDMSPrivilege();
		pr.addPriv(sysEnv, newPrivs);

		thisType = adjustType(sysEnv, p, type);

		if(thisType == SDMSGrant.SCOPE && pr.can(SDMSPrivilege.EXECUTE))
			SystemEnvironment.sched.notifyChange(sysEnv, (SDMSScope) p, SchedulingThread.ALTER);

		for (int i = 0; i < gList.size(); i++) {
			gId = (Long) gList.get(i);
			try {
				g = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(oId, gId));
			} catch (NotFoundException nfe) {
				g = SDMSGrantTable.table.create(sysEnv, oId, gId, Integer.valueOf(thisType), SDMSConstants.PR_NOPRIVS, null);
			}
			oldPrivs = g.getPrivs(sysEnv);
			if(isGrant) {
				pr.addPriv(sysEnv, oldPrivs.longValue());
			} else {
				pr.addPriv(sysEnv, oldPrivs.longValue());
				pr.delPriv(sysEnv, newPrivs);
			}
			if(pr.isEmpty()) {
				g.delete(sysEnv);
			} else {
				g.setPrivs(sysEnv, pr.toLong());
			}
		}

		if (recursive) {
			final Vector children = p.getContent(sysEnv);
			if(children == null) return;
			for (int i = 0; i < children.size(); ++i) {
				performGrant(sysEnv, (SDMSProxy) children.get(i), thisType, lpriv, gList, recursive, force);
			}
		}
	}

	private void performManageGrant(SystemEnvironment sysEnv, Vector gList)
		throws SDMSException
	{
		SDMSGrant g;
		Long gId;
		Long oId = SDMSPrivilege.SYSPRIVOBJID;

		if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03110051520", "Insufficient privileges"));
		}

		SDMSPrivilege pr = new SDMSPrivilege(sysEnv, lpriv);

		for (int i = 0; i < gList.size(); i++) {
			gId = (Long) gList.get(i);
			try {
				g = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(oId, gId));
			} catch (NotFoundException nfe) {
				g = SDMSGrantTable.table.create(sysEnv, oId, gId, SYSTEM, SDMSConstants.PR_NOPRIVS, null);
			}
			Long oldPrivs = g.getPrivs(sysEnv);
			if(isGrant) {
				pr.addPriv(sysEnv, oldPrivs.longValue());
			} else {
				pr.addPriv(sysEnv, oldPrivs.longValue());
				pr.delPriv(sysEnv, lpriv);
			}
			if(pr.isEmpty()) {
				g.delete(sysEnv);
			} else {
				g.setPrivs(sysEnv, pr.toLong());
			}
		}
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSProxy p;
		final int objectType;
		final boolean recursive = isRecursive.booleanValue();
		final boolean force = isForce.booleanValue();
		final Vector gList = makeGroupList(sysEnv);

		if (url == null) {
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_GRANTS);
			performManageGrant(sysEnv, gList);
		} else {

			p = url.resolve(sysEnv);
			objectType = url.objType.intValue();
			checkObjectType (sysEnv, objectType);

			if (
			    objectType != SDMSGrant.ENVIRONMENT
			) {
				sysEnv.checkFeatureAvailability(SystemEnvironment.S_GRANTS);
			}

			if (childrenOnly.booleanValue()) {
				final Vector children = p.getContent(sysEnv);
				if(children != null) {
					for (int i = 0; i < children.size(); ++i) {
						performGrant(sysEnv, (SDMSProxy) children.get(i), objectType, lpriv, gList, recursive, force);
					}
				}
			} else {
				performGrant(sysEnv, p, objectType, lpriv, gList, recursive, force);
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03402110010", (isGrant ? "Grant" : "Revoke") + " issued"));
	}
}

