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

public class ListGrant extends Node
{

	public final static String __version = "@(#) $Id: ListGrant.java,v 2.14.2.3 2013/06/18 09:49:33 ronald Exp $";

	private final ObjectURL url;
	private final String groupName;
	private SDMSGroup group;
	private SDMSProxy obj;

	private final boolean objectGrants;

	public ListGrant(ObjectURL w)
	{
		super();
		objectGrants = true;
		url = w;
		groupName = null;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListGrant(String g)
	{
		super();
		objectGrants = false;
		url = null;
		groupName = g;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	private void go_grantsForObject(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSGroup g;
		SDMSGrant gr;
		SDMSPrivilege p = new SDMSPrivilege();
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("GROUP");
		desc.add("PRIVS");
		desc.add("INHERITED_PRIVS");
		desc.add("EFFECTIVE_PRIVS");
		desc.add("ORIGIN");
		desc.add("OWNER");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03302111221", "List of Grants"), desc);

		long inheritPrivs = 0;
		SDMSProxy parent = obj.getParent(sysEnv);
		if (parent != null) {
			inheritPrivs = obj.getInheritPrivs(sysEnv).longValue();
			Vector v = new Vector();

			v.add(obj.getId(sysEnv));
			v.add("");

			p.setPriv(sysEnv, inheritPrivs);
			v.add(p.toString());

			v.add("");
			v.add("");

			v.add(null);

			v.add(null);

			d_container.addData(sysEnv, v);
		}

		HashSet groups = new HashSet();
		long remainingInheritPrivs = obj.getPrivilegeMask();
		SDMSProxy grantObj = obj;
		while (grantObj != null) {

			Long ownerId = grantObj.getOwnerId(sysEnv);
			if (ownerId != null && !ownerId.equals(SDMSObject.adminGId))
				groups.add(ownerId);

			Vector gv = SDMSGrantTable.idx_objectId.getVector(sysEnv, grantObj.getId(sysEnv));
			for(int i = 0; i < gv.size(); i++) {
				SDMSGrant grant = (SDMSGrant) gv.get(i);
				long privs = grant.getPrivs(sysEnv).longValue();
				if ((privs & remainingInheritPrivs) != 0) {
					groups.add(grant.getGId(sysEnv));
				}
			}

			remainingInheritPrivs = remainingInheritPrivs & inheritPrivs;
			if (remainingInheritPrivs == 0)
				grantObj = null;
			else {
				grantObj = grantObj.getParent(sysEnv);
				if (grantObj != null)
					inheritPrivs = grantObj.getInheritPrivs(sysEnv).longValue();
			}
		}

		Iterator gi = groups.iterator();
		while (gi.hasNext()) {
			Long gId = (Long)gi.next();
			inheritPrivs = obj.getInheritPrivs(sysEnv).longValue();
			remainingInheritPrivs = obj.getPrivilegeMask();
			grantObj = obj;
			SDMSProxy parentObj = null;
			long parentEffectivePrivs = -1;
			while (grantObj != null) {
				long privs;
				Long grId = null;
				Long ownerId = grantObj.getOwnerId(sysEnv);
				Vector gv = new Vector();
				gv.add(gId);
				long effectivePrivs = 0;
				if (parentEffectivePrivs == -1)
					effectivePrivs = grantObj.getPrivilegesForGroups(sysEnv, gv).toLong().longValue();
				else
					effectivePrivs = parentEffectivePrivs;
				parentObj = grantObj.getParent(sysEnv);
				if (parentObj != null)
					parentEffectivePrivs = parentObj.getPrivilegesForGroups(sysEnv, gv).toLong().longValue();
				else
					parentEffectivePrivs = 0;
				try {
					SDMSGrant grant = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(grantObj.getId(sysEnv),gId));
					grId = grant.getId(sysEnv);
					privs = grant.getPrivs(sysEnv).longValue();
				} catch (NotFoundException nfe) {
					grId = grantObj.getId(sysEnv);
					privs = 0;
				}
				if (grantObj != obj) {
					privs = privs & remainingInheritPrivs;
				}
				if ((effectivePrivs & remainingInheritPrivs) != 0 &&
				    (grantObj == obj || (ownerId != null && ownerId.equals(gId)) || privs != 0)
				   ) {
					Vector v = new Vector();

					v.add(grId);

					SDMSGroup grp = SDMSGroupTable.getObject(sysEnv, gId);
					v.add(grp.getName(sysEnv));

					if (ownerId != null && ownerId.equals(gId) && grantObj != obj)
						p.setPriv(sysEnv, remainingInheritPrivs);
					else {
						p.setPriv(sysEnv, privs);
					}
					v.add(p.toString());

					if (ownerId != null && ownerId.equals(gId)) {
						v.add("");
					} else {
						p.setPriv(sysEnv, parentEffectivePrivs & remainingInheritPrivs & effectivePrivs);
						v.add(p.toString());
					}

					p.setPriv(sysEnv, effectivePrivs);
					v.add(p.toString());

					String origin = null;
					if (grantObj != obj) {
						PathVector path = grantObj.pathVector(sysEnv);
						if (path != null)
							origin = path.toString();
					}
					v.add(origin);

					String owner = null;
					if (ownerId != null) {
						grp = SDMSGroupTable.getObject(sysEnv, ownerId);
						owner = grp.getName(sysEnv);
					}
					v.add(owner);

					d_container.addData(sysEnv, v);
				}
				if (ownerId != null && ownerId.equals(gId))
					remainingInheritPrivs = 0;
				else
					remainingInheritPrivs = remainingInheritPrivs & inheritPrivs & (~privs) & effectivePrivs;
				if (remainingInheritPrivs == 0)
					grantObj = null;
				else {
					grantObj = parentObj;
					if (grantObj != null)
						inheritPrivs = grantObj.getInheritPrivs(sysEnv).longValue();
				}
			}
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1, 5));

		result.setOutputContainer(d_container);

		result.setFeedback(
		        new SDMSMessage(sysEnv, "03402110100", "$1 Grant(s) found", Integer.valueOf(d_container.lines)));
	}

	private SDMSProxy resolveById(SystemEnvironment sysEnv, Integer objType, Long objId)
		throws SDMSException
	{
		SDMSProxy p = null;
		try {
			switch (objType.intValue()) {
				case SDMSObjectComment.ENVIRONMENT:
					p = SDMSNamedEnvironmentTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.EVENT:
					p = SDMSEventTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.EXIT_STATE_DEFINITION:
					p = SDMSExitStateDefinitionTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.EXIT_STATE_PROFILE:
					p = SDMSExitStateProfileTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.EXIT_STATE_MAPPING:
					p = SDMSExitStateMappingProfileTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.EXIT_STATE_TRANSLATION:
					p = SDMSExitStateTranslationProfileTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.FOLDER:
				case SDMSObjectComment.JOB_DEFINITION:
					try {
						p = SDMSFolderTable.getObject(sysEnv, objId);
						objType = SDMSConstants.OC_FOLDER;
					} catch (NotFoundException nfe) {
						p = SDMSSchedulingEntityTable.getObject(sysEnv, objId);
						objType = SDMSConstants.OC_JOB_DEFINITION;
					}
					break;
				case SDMSObjectComment.FOOTPRINT:
					p = SDMSFootprintTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.GROUP:
					p = SDMSGroupTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.INTERVAL:
					p = SDMSIntervalTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.JOB:
					p = SDMSSubmittedEntityTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.NAMED_RESOURCE:
					p = SDMSNamedResourceTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.PARAMETER:
					p = SDMSParameterDefinitionTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.RESOURCE:
					p = SDMSResourceTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.RESOURCE_STATE_DEFINITION:
					p = SDMSResourceStateDefinitionTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.RESOURCE_STATE_PROFILE:
					p = SDMSResourceStateProfileTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.RESOURCE_STATE_MAPPING:
					p = SDMSResourceStateMappingProfileTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.SCHEDULE:
					p = SDMSScheduleTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.SCHEDULED_EVENT:
					p = SDMSScheduledEventTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.SCOPE:
					p = SDMSScopeTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.TRIGGER:
					p = SDMSTriggerTable.getObject(sysEnv, objId);
					break;
				case SDMSObjectComment.USER:
					p = SDMSUserTable.getObject(sysEnv, objId);
					break;
				default:
					break;
			}
		} catch (NotFoundException nfe) {
		}
		return p;
	}

	private void go_grantsForGroup(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSGrant gr;
		SDMSPrivilege p = new SDMSPrivilege();
		SDMSProxy prox;

		Vector grv = SDMSGrantTable.idx_gId.getVector(sysEnv, group.getId(sysEnv));

		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("TYPE");
		desc.add("SUBTYPE");
		desc.add("NAME");
		desc.add("PRIVS");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03302111222", "List of Grants"), desc);

		for (int i = 0; i < grv.size(); ++i) {
			gr = (SDMSGrant) grv.get(i);
			prox = resolveById(sysEnv, gr.getObjectType(sysEnv), gr.getObjectId(sysEnv));
			long pr = gr.getPrivs(sysEnv).longValue();
			if (prox == null) {
				for (int j = 0; j < SDMSPrivilege.MANAGE_PRIVS.length; ++j) {
					if ((SDMSPrivilege.MANAGE_PRIVS[j] & pr) != SDMSPrivilege.NOPRIVS) {
						Vector v = new Vector();

						v.add(gr.getId(sysEnv));
						if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_USER) {
							v.add("user");
							p.setPriv(sysEnv, SDMSUser.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_GROUP) {
							v.add("group");
							p.setPriv(sysEnv, SDMSGroup.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_ESD) {
							v.add("exit state definition");
							p.setPriv(sysEnv, SDMSExitStateDefinition.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_ESP) {
							v.add("exit state profile");
							p.setPriv(sysEnv, SDMSExitStateProfile.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_ESM) {
							v.add("exit state mapping");
							p.setPriv(sysEnv, SDMSExitStateMappingProfile.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_EST) {
							v.add("exit state translation");
							p.setPriv(sysEnv, SDMSExitStateTranslationProfile.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_RSD) {
							v.add("resource state definition");
							p.setPriv(sysEnv, SDMSResourceStateDefinition.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_RSP) {
							v.add("resource state profile");
							p.setPriv(sysEnv, SDMSResourceStateProfile.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_RSM) {
							v.add("resource state mapping");
							p.setPriv(sysEnv, SDMSResourceStateMappingProfile.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_FP) {
							v.add("footprint");
							p.setPriv(sysEnv, SDMSFootprint.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_ENV) {
							v.add("environment");
							p.setPriv(sysEnv, SDMSNamedEnvironment.privilegeMask);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_SYS) {
							v.add("system");
							p.setPriv(sysEnv, SDMSPrivilege.NOPRIVS);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_SEL) {
							v.add("select");
							p.setPriv(sysEnv, SDMSPrivilege.NOPRIVS);
						} else if (SDMSPrivilege.MANAGE_PRIVS[j] ==  SDMSPrivilege.MANAGE_WT) {
							v.add("watch type");
							p.setPriv(sysEnv, SDMSPrivilege.NOPRIVS);
						} else {
							v.add("Unknown System Privilege");
							p.setPriv(sysEnv, SDMSPrivilege.NOPRIVS);
						}
						v.add("");
						v.add("");
						v.add(p.toString());

						d_container.addData(sysEnv, v);
					}
				}
			} else {
				Vector v = new Vector();

				v.add(gr.getId(sysEnv));
				v.add(gr.getObjectTypeAsString(sysEnv));
				v.add(prox.getSubtypeName(sysEnv));
				v.add(prox.getURLName(sysEnv));
				p.setPriv(sysEnv, pr);
				v.add(p.toString());

				d_container.addData(sysEnv, v);
			}
		}
		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(
		        new SDMSMessage(sysEnv, "03402110101", "$1 Grant(s) found", Integer.valueOf(d_container.lines)));
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (objectGrants) {
			obj = url.resolve(sysEnv);
			if(!obj.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03402131446", "Insufficient Privileges"));
			go_grantsForObject(sysEnv);
		} else {
			group = SDMSGroupTable.idx_name_getUnique(sysEnv, groupName);
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId) && !sysEnv.cEnv.gid().contains(group.getId(sysEnv))) {
				Iterator i = sysEnv.cEnv.gid().iterator();
				SDMSPrivilege p = new SDMSPrivilege();
				while (i.hasNext()) {
					Long gId = (Long) i.next();
					try {
						SDMSGrant g = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(SDMSConstants.lZERO, gId));
						p.addPriv(sysEnv, g.getPrivs(sysEnv).longValue());
					} catch (NotFoundException nfe) {
					}
				}
				if (!p.can(SDMSPrivilege.MANAGE_GROUP))
					throw new AccessViolationException(new SDMSMessage(sysEnv, "03102241232", "Insufficient privileges"));
			}
			go_grantsForGroup(sysEnv);
		}
	}
}

