/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class AlterNamedResource extends Node
{

	public final static String __version = "@(#) $Id: AlterNamedResource.java,v 2.18.2.3 2013/03/19 10:03:51 ronald Exp $";

	private ObjectURL url;
	private Integer usage;
	private String rspName;
	private String gName;
	private Long gId;
	private Float factor = null;
	private WithHash with;
	private boolean noerr;
	private WithHash parms = null;
	private boolean allTypes = false;

	public AlterNamedResource(ObjectURL u, WithHash w, Boolean n)
	{
		super();
		url = u;
		with = w;
		noerr = n.booleanValue();
	}

	private void evaluateWith(SystemEnvironment sysEnv)
	throws SDMSException
	{
		usage = (Integer) with.get(ParseStr.S_USAGE);

		rspName = (String) with.get(ParseStr.S_STATUS_PROFILE);

		if(with.containsKey(ParseStr.S_GROUP) && with.containsKey(ParseStr.S_GROUP_CASCADE)) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03402041406", "It is not allowed to specify the group clause twice"));
		}
		gName = (String) with.get(ParseStr.S_GROUP);
		if(gName == null) gName = (String) with.get(ParseStr.S_GROUP_CASCADE);
		if(gName != null) {
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
		} else {
			gId = null;
		}

		parms = (WithHash) with.get (ParseStr.S_PARAMETERS);
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSNamedResource nr;
		Long rspId;

		evaluateWith(sysEnv);
		try {
			nr = (SDMSNamedResource) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122309", "No Named Resource altered"));
				return;
			}
			throw nfe;
		}

		if(usage != null) {
			if(!usage.equals(nr.getUsage(sysEnv)))
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03402041237", "You cannot change the resource usage"));
		} else {
			usage = nr.getUsage(sysEnv);
		}
		if(nr.getUsage(sysEnv).intValue() == SDMSNamedResource.SYNCHRONIZING)
			allTypes = true;

		SDMSResourceStateProfile rsp = null;
		if(rspName != null) {
			Long rsdId;

			rsp = SDMSResourceStateProfileTable.idx_name_getUnique(sysEnv, rspName);

			rspId = rsp.getId(sysEnv);

			if (nr.getRspId(sysEnv) == null) {

				Long initialRsdId = rsp.getInitialRsdId(sysEnv);
				Vector v = SDMSResourceTable.idx_nrId.getVector(sysEnv, nr.getId(sysEnv));
				for(int j = 0; j < v.size(); j++) {
					SDMSResource r = ((SDMSResource) v.get(j));
					r.setRsdId(sysEnv, initialRsdId);
				}
			} else {

				Vector v = SDMSResourceTable.idx_nrId.getVector(sysEnv, nr.getId(sysEnv));
				for(int j = 0; j < v.size(); j++) {
					SDMSResource r = ((SDMSResource) v.get(j));
					rsdId = r.getRsdId(sysEnv);
					if(rsdId.equals(rsp.getInitialRsdId(sysEnv))) continue;
					if(!SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId))) {
						String path;
						try {
							SDMSScope fs = SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv));
							path = fs.pathString(sysEnv);
						} catch (NotFoundException nfe) {
							SDMSFolder ff = SDMSFolderTable.getObject(sysEnv, r.getScopeId(sysEnv));
							path = ff.pathString(sysEnv);
						}
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03709071000",
						                               "Profile does not contain state $1 of resource $2->$3",
						                               SDMSResourceStateDefinitionTable.getObject(sysEnv, r.getRsdId(sysEnv)).getName(sysEnv),
						                               path,
						                               SDMSNamedResourceTable.getObject(sysEnv, ((SDMSResource) v.get(j)).getNrId(sysEnv)).pathString(sysEnv)));
					}
				}
			}
		} else {
			rspId = null;

			if (nr.getRspId(sysEnv) != null) {

				Vector v = SDMSResourceTable.idx_nrId.getVector(sysEnv, nr.getId(sysEnv));
				for(int j = 0; j < v.size(); j++) {
					SDMSResource r = ((SDMSResource) v.get(j));

					Vector a = SDMSResourceAllocationTable.idx_rId.getVector(sysEnv, r.getId(sysEnv));
					if (a.size() > 0) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03709071020",
						                               "Resource $1->$2 currently allocated, cannet set resource state to null",
						                               SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv)),
						                               nr.pathVector(sysEnv)));
					}
					r.setRsdId(sysEnv, null);
				}
			}
		}

		if (with.containsKey(ParseStr.S_STATUS_PROFILE))
			nr.setRspId(sysEnv, rspId);

		Vector v = SDMSResourceRequirementTable.idx_nrId.getVector(sysEnv, nr.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			((SDMSResourceRequirement)v.get(i)).check(sysEnv);
		}

		if(gId != null) {
			ChownChecker.check(sysEnv, gId);
			nr.setOwnerId(sysEnv, gId);
			if(with.containsKey(ParseStr.S_GROUP_CASCADE)) {
				changeChildGroup(sysEnv, nr.getId(sysEnv), gId);
			}
		}

		if (with.containsKey(ParseStr.S_INHERIT)) {
			Long inheritPrivs = (Long) with.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
			long lpriv = inheritPrivs.longValue();
			if((nr.getPrivilegeMask() & lpriv) != lpriv) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061132", "Incompatible grant"));
			}

			nr.setInheritPrivs(sysEnv, inheritPrivs);
		}

		int u = usage.intValue();

		if(factor != null && !(factor.equals(new Float(1.0))))
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_RESOURCE_TRACING);

		if (with.containsKey (ParseStr.S_PARAMETERS))
			nr.alterParameters (sysEnv, parms, allTypes);

		result.setFeedback(new SDMSMessage(sysEnv, "03201212235", "Named Resource altered"));
	}

	private void changeChildGroup(SystemEnvironment sysEnv, Long parentId, Long groupId)
	throws SDMSException
	{
		Vector cv = SDMSNamedResourceTable.idx_parentId.getVector(sysEnv, parentId);
		for(int i = 0; i < cv.size(); i++) {
			SDMSNamedResource nr = (SDMSNamedResource) cv.get(i);
			try {
				nr.setOwnerId(sysEnv, groupId);
			} catch (AccessViolationException ave) {

			}
			changeChildGroup(sysEnv, nr.getId(sysEnv), groupId);
		}
	}

}

