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

public class ObjectURL
{

	public final static String __version = "@(#) $Id: ObjectURL.java,v 2.15.4.2 2013/03/19 17:16:48 ronald Exp $";

	public Long objId = null;
	public Integer objType = null;
	public Integer parserType = null;
	public String name = null;
	public PathVector path = null;
	public ObjectURL master = null;
	public Long seId;
	public Long sceId;
	public Long evtId;
	public String mappedName = null;
	public WithItem seSpec;
	public boolean wildcard = false;
	public WithItem triggerInverse;

	private final static HashMap typeFromURL = new HashMap();
	static
	{
		typeFromURL.put(new Integer(Parser.DISTRIBUTION),		new Integer(SDMSObjectComment.DISTRIBUTION));
		typeFromURL.put(new Integer(Parser.ENVIRONMENT),		new Integer(SDMSObjectComment.ENVIRONMENT));
		typeFromURL.put(new Integer(Parser.EVENT),			new Integer(SDMSObjectComment.EVENT));
		typeFromURL.put(new Integer(Parser.EXIT_STATUS_DEFINITION),	new Integer(SDMSObjectComment.EXIT_STATE_DEFINITION));
		typeFromURL.put(new Integer(Parser.EXIT_STATUS_PROFILE),	new Integer(SDMSObjectComment.EXIT_STATE_PROFILE));
		typeFromURL.put(new Integer(Parser.EXIT_STATUS_MAPPING),	new Integer(SDMSObjectComment.EXIT_STATE_MAPPING));
		typeFromURL.put(new Integer(Parser.EXIT_STATUS_TRANSLATION),	new Integer(SDMSObjectComment.EXIT_STATE_TRANSLATION));
		typeFromURL.put(new Integer(Parser.FOLDER),			new Integer(SDMSObjectComment.FOLDER));
		typeFromURL.put(new Integer(Parser.FOOTPRINT),			new Integer(SDMSObjectComment.FOOTPRINT));
		typeFromURL.put(new Integer(Parser.GROUP),			new Integer(SDMSObjectComment.GROUP));
		typeFromURL.put(new Integer(Parser.INTERVAL),			new Integer(SDMSObjectComment.INTERVAL));
		typeFromURL.put(new Integer(Parser.JOB_DEFINITION),		new Integer(SDMSObjectComment.JOB_DEFINITION));
		typeFromURL.put(new Integer(Parser.JOB_SERVER),			new Integer(SDMSObjectComment.SCOPE));
		typeFromURL.put(new Integer(Parser.JOB),			new Integer(SDMSObjectComment.JOB));
		typeFromURL.put(new Integer(Parser.NAMED_RESOURCE),		new Integer(SDMSObjectComment.NAMED_RESOURCE));
		typeFromURL.put(new Integer(Parser.NICE_PROFILE),		new Integer(SDMSObjectComment.NICE_PROFILE));
		typeFromURL.put(new Integer(Parser.OBJECT),			new Integer(SDMSObjectComment.OBJECT_MONITOR));
		typeFromURL.put(new Integer(Parser.PARAMETERS),			new Integer(SDMSObjectComment.PARAMETER));
		typeFromURL.put(new Integer(Parser.POOL),			new Integer(SDMSObjectComment.POOL));
		typeFromURL.put(new Integer(Parser.RESOURCE),			new Integer(SDMSObjectComment.RESOURCE));
		typeFromURL.put(new Integer(Parser.RESOURCE_STATUS_DEFINITION),	new Integer(SDMSObjectComment.RESOURCE_STATE_DEFINITION));
		typeFromURL.put(new Integer(Parser.RESOURCE_STATUS_PROFILE),	new Integer(SDMSObjectComment.RESOURCE_STATE_PROFILE));
		typeFromURL.put(new Integer(Parser.RESOURCE_STATUS_MAPPING),	new Integer(SDMSObjectComment.RESOURCE_STATE_MAPPING));
		typeFromURL.put(new Integer(Parser.RESOURCE_TEMPLATE),		new Integer(SDMSObjectComment.RESOURCE_TEMPLATE));
		typeFromURL.put(new Integer(Parser.SCHEDULE),			new Integer(SDMSObjectComment.SCHEDULE));
		typeFromURL.put(new Integer(Parser.SCHEDULED_EVENT),		new Integer(SDMSObjectComment.SCHEDULED_EVENT));
		typeFromURL.put(new Integer(Parser.SCOPE),			new Integer(SDMSObjectComment.SCOPE));
		typeFromURL.put(new Integer(Parser.TRIGGER),			new Integer(SDMSObjectComment.TRIGGER));
		typeFromURL.put(new Integer(Parser.USER),			new Integer(SDMSObjectComment.USER));
		typeFromURL.put(new Integer(Parser.WATCH),			new Integer(SDMSObjectComment.WATCH_TYPE));
		typeFromURL.put(new Integer(Parser.COMMENT),			new Integer(SDMSObjectComment.COMMENT));
		typeFromURL.put(new Integer(Parser.GRANT),			new Integer(SDMSObjectComment.GRANT));
	}

	public ObjectURL(Integer t)
	{
		objId = null;
		objType = (Integer) typeFromURL.get(t);
		parserType = t;
	}

	public ObjectURL(Integer t, Long id)
	{
		objId = id;
		objType = (Integer) typeFromURL.get(t);
		parserType = t;
	}

	public ObjectURL(Integer t, String n)
	{
		name = n;
		objType = (Integer) typeFromURL.get(t);
		parserType = t;
	}

	public ObjectURL(Integer t, String n, WithItem s)
		throws SDMSException
	{
		name = n;
		objType = (Integer) typeFromURL.get(t);
		seSpec = s;
		parserType = t;
	}

	public ObjectURL(Integer t, PathVector p)
	{
		path = p;
		objType = (Integer) typeFromURL.get(t);
		if (path.get(path.size() - 1) == null) {
			wildcard = true;
			path.remove(path.size() - 1);
		}
		parserType = t;
	}

	public ObjectURL(Integer t, PathVector p, WithItem s)
		throws SDMSException
	{
		path = p;
		objType = (Integer) typeFromURL.get(t);
		seSpec = s;
		if (path.get(path.size() - 1) == null) {
			wildcard = true;
			path.remove(path.size() - 1);
		}
		parserType = t;
	}

	public ObjectURL(Integer t, PathVector p, ObjectURL o)
	{
		objType = (Integer) typeFromURL.get(t);
		master = o;
		path = p;
		if (path.get(path.size() - 1) == null) {
			wildcard = true;
			path.remove(path.size() - 1);
		}
		parserType = t;
	}

	public ObjectURL(Integer t, String s, ObjectURL o)
	{
		objType = (Integer) typeFromURL.get(t);
		master = o;
		name = s;
		parserType = t;
	}

	public ObjectURL(Integer t, String s, ObjectURL o, WithItem inverse)
	{
		objType = (Integer) typeFromURL.get(t);
		master = o;
		name = s;
		parserType = t;
		triggerInverse = inverse;
	}

	public SDMSProxy resolve(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(objType == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03512131431", "Cannot resolve an Object when the type is unknown"));
		}
		if(objId != null && name == null && path == null) {
			return resolveById(sysEnv);
		} else if(name != null || path != null) {
			return resolveByName(sysEnv);
		} else
			return null;
	}

	private SDMSProxy resolveById(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSProxy p = null;
		switch (objType.intValue()) {
			case SDMSObjectComment.ENVIRONMENT:		p = SDMSNamedEnvironmentTable.getObject(sysEnv, objId);		break;
			case SDMSObjectComment.EVENT:			p = SDMSEventTable.getObject(sysEnv, objId);			break;
			case SDMSObjectComment.EXIT_STATE_DEFINITION:	p = SDMSExitStateDefinitionTable.getObject(sysEnv, objId);	break;
			case SDMSObjectComment.EXIT_STATE_PROFILE:	p = SDMSExitStateProfileTable.getObject(sysEnv, objId);		break;
			case SDMSObjectComment.EXIT_STATE_MAPPING:	p = SDMSExitStateMappingProfileTable.getObject(sysEnv, objId);	break;
			case SDMSObjectComment.EXIT_STATE_TRANSLATION:	p = SDMSExitStateTranslationProfileTable.getObject(sysEnv, objId); break;
			case SDMSObjectComment.FOLDER:
			case SDMSObjectComment.JOB_DEFINITION:
				try {
					p = SDMSFolderTable.getObject(sysEnv, objId);
					objType = new Integer(SDMSObjectComment.FOLDER);
				} catch (NotFoundException nfe) {
					p = SDMSSchedulingEntityTable.getObject(sysEnv, objId);
					objType = new Integer(SDMSObjectComment.JOB_DEFINITION);
				}
				break;
			case SDMSObjectComment.FOOTPRINT:		p = SDMSFootprintTable.getObject(sysEnv, objId);		break;
			case SDMSObjectComment.GROUP:			p = SDMSGroupTable.getObject(sysEnv, objId);			break;
			case SDMSObjectComment.INTERVAL:		p = SDMSIntervalTable.getObject(sysEnv, objId);			break;
			case SDMSObjectComment.JOB:			p = SDMSSubmittedEntityTable.getObject(sysEnv, objId);		break;
			case SDMSObjectComment.NAMED_RESOURCE:		p = SDMSNamedResourceTable.getObject(sysEnv, objId);		break;
			case SDMSObjectComment.PARAMETER:		p = SDMSParameterDefinitionTable.getObject(sysEnv, objId);	break;
			case SDMSObjectComment.RESOURCE:		p = SDMSResourceTable.getObject(sysEnv, objId);			break;
			case SDMSObjectComment.RESOURCE_STATE_DEFINITION: p = SDMSResourceStateDefinitionTable.getObject(sysEnv, objId); break;
			case SDMSObjectComment.RESOURCE_STATE_PROFILE:	p = SDMSResourceStateProfileTable.getObject(sysEnv, objId);	break;
			case SDMSObjectComment.RESOURCE_STATE_MAPPING:	p = SDMSResourceStateMappingProfileTable.getObject(sysEnv, objId); break;
			case SDMSObjectComment.SCHEDULE:		p = SDMSScheduleTable.getObject(sysEnv, objId);			break;
			case SDMSObjectComment.SCHEDULED_EVENT:		p = SDMSScheduledEventTable.getObject(sysEnv, objId);		break;
			case SDMSObjectComment.SCOPE:
				p = SDMSScopeTable.getObject(sysEnv, objId);
				if (((SDMSScope) p).getType(sysEnv).intValue() == SDMSScope.SERVER)
					parserType = new Integer(Parser.JOB_SERVER);
				break;
			case SDMSObjectComment.TRIGGER:			p = SDMSTriggerTable.getObject(sysEnv, objId);			break;
			case SDMSObjectComment.USER:			p = SDMSUserTable.getObject(sysEnv, objId);			break;
			default: break;
		}
		return p;
	}

	private SDMSProxy resolveByName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSProxy p = null;
		switch (objType.intValue()) {
			case SDMSObjectComment.ENVIRONMENT:		p = getEnvironment(sysEnv);		break;
			case SDMSObjectComment.EVENT:			p = getEvent(sysEnv);			break;
			case SDMSObjectComment.EXIT_STATE_DEFINITION:	p = getExitStateDefinition(sysEnv);	break;
			case SDMSObjectComment.EXIT_STATE_PROFILE:	p = getExitStateProfile(sysEnv);	break;
			case SDMSObjectComment.EXIT_STATE_MAPPING:	p = getExitStateMapping(sysEnv);	break;
			case SDMSObjectComment.EXIT_STATE_TRANSLATION:	p = getExitStateTranslation(sysEnv);	break;
			case SDMSObjectComment.FOLDER:			p = getFolder(sysEnv);			break;
			case SDMSObjectComment.FOOTPRINT:		p = getFootprint(sysEnv);		break;
			case SDMSObjectComment.GROUP:			p = getGroup(sysEnv);			break;
			case SDMSObjectComment.INTERVAL:		p = getInterval(sysEnv);		break;
			case SDMSObjectComment.JOB_DEFINITION:		p = getSchedulingEntity(sysEnv);	break;
			case SDMSObjectComment.NAMED_RESOURCE:		p = getNamedResource(sysEnv);		break;
			case SDMSObjectComment.PARAMETER:		p = getParameterDefinition(sysEnv);	break;
			case SDMSObjectComment.RESOURCE:		p = getResource(sysEnv);		break;
			case SDMSObjectComment.RESOURCE_STATE_DEFINITION: p = getResourceStateDefinition(sysEnv); break;
			case SDMSObjectComment.RESOURCE_STATE_PROFILE:	p = getResourceStateProfile(sysEnv);	break;
			case SDMSObjectComment.RESOURCE_STATE_MAPPING:	p = getResourceStateMapping(sysEnv);	break;
			case SDMSObjectComment.SCHEDULE:		p = getSchedule(sysEnv);		break;
			case SDMSObjectComment.SCHEDULED_EVENT:		p = getScheduledEvent(sysEnv);		break;
			case SDMSObjectComment.SCOPE:
				p = getScope(sysEnv);
				if (((SDMSScope) p).getType(sysEnv).intValue() == SDMSScope.SERVER)
					parserType = new Integer(Parser.JOB_SERVER);
				break;
			case SDMSObjectComment.TRIGGER:			p = getTrigger(sysEnv);			break;
			case SDMSObjectComment.USER:			p = getUser(sysEnv);			break;
			default: break;
		}
		if (p != null) objId = p.getId(sysEnv);
		return p;
	}

	private final String getObjectName(final SystemEnvironment sysEnv, final String name, final WithItem seSpec)
		throws SDMSException
	{
		if (seSpec == null)
			return name;

		seId = IntervalUtil.getCheckedSeId (sysEnv, seSpec);
		return IntervalUtil.mapIdName (name, seId);
	}

	private SDMSNamedEnvironment getEnvironment(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSNamedEnvironment p = null;
		p = SDMSNamedEnvironmentTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSEvent getEvent(SystemEnvironment sysEnv)
		throws SDMSException
	{
		mappedName = getObjectName(sysEnv, name, seSpec);
		SDMSEvent p = null;
		p = SDMSEventTable.idx_name_getUnique(sysEnv, mappedName);
		return p;
	}

	private SDMSExitStateDefinition getExitStateDefinition(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateDefinition p = null;
		p = SDMSExitStateDefinitionTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSExitStateProfile getExitStateProfile(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateProfile p = null;
		p = SDMSExitStateProfileTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSExitStateMappingProfile getExitStateMapping(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateMappingProfile p = null;
		p = SDMSExitStateMappingProfileTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSExitStateTranslationProfile getExitStateTranslation(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSExitStateTranslationProfile p = null;
		p = SDMSExitStateTranslationProfileTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSProxy getFolder(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSProxy p = null;
		try {
			p = SDMSFolderTable.getFolder(sysEnv, path);
		} catch (NotFoundException nfe) {
			objType = new Integer(SDMSObjectComment.JOB_DEFINITION);
			p = getSchedulingEntity(sysEnv);
		}
		return p;
	}

	private SDMSFootprint getFootprint(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSFootprint p = null;
		p = SDMSFootprintTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSGroup getGroup(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSGroup p = null;
		p = SDMSGroupTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSInterval getInterval(SystemEnvironment sysEnv)
		throws SDMSException
	{
		mappedName = getObjectName(sysEnv, name, seSpec);
		SDMSInterval p = null;
		p = SDMSIntervalTable.idx_name_objId_getUnique(sysEnv, new SDMSKey(mappedName, null));
		return p;
	}

	private SDMSSchedulingEntity getSchedulingEntity(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSSchedulingEntity p = null;
		p = SDMSSchedulingEntityTable.get(sysEnv, path, null);
		return p;
	}

	private SDMSNamedResource getNamedResource(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSNamedResource p = null;
		p = SDMSNamedResourceTable.getNamedResource(sysEnv, path);
		return p;
	}

	private SDMSParameterDefinition getParameterDefinition(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSParameterDefinition p = null;
		SDMSProxy m = master.resolve(sysEnv);
		p = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(master.objId, name));
		return p;
	}

	private SDMSProxy getResource(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSProxy p = null;
		final SDMSProxy m = master.resolve(sysEnv);
		final SDMSNamedResource nr = SDMSNamedResourceTable.getNamedResource(sysEnv, path);
		final Long nrId = nr.getId(sysEnv);
		final SDMSKey key = new SDMSKey(nrId, master.objId);
		try {
			p = SDMSResourceTable.idx_nrId_scopeId_getUnique(sysEnv, key);
		} catch (NotFoundException nfe) {
			objType = new Integer(SDMSObjectComment.RESOURCE_TEMPLATE);
			p = SDMSResourceTemplateTable.idx_nrId_seId_getUnique(sysEnv, key);
		}
		return p;
	}

	private SDMSResourceStateDefinition getResourceStateDefinition(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateDefinition p = null;
		p = SDMSResourceStateDefinitionTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSResourceStateProfile getResourceStateProfile(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateProfile p = null;
		p = SDMSResourceStateProfileTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSResourceStateMappingProfile getResourceStateMapping(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSResourceStateMappingProfile p = null;
		p = SDMSResourceStateMappingProfileTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	private SDMSSchedule getSchedule(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (seSpec != null) {
			seId = IntervalUtil.getCheckedSeId (sysEnv, seSpec);

			final String mappedName;
			final PathVector mappedPath;
			if (path.size() > 2) {
				mappedName = (String) path.remove (path.size() - 1);
				mappedPath = (PathVector) IntervalUtil.mapIdPath (path, seId);
			} else {
				mappedName = IntervalUtil.mapIdPath ((String) path.remove (path.size() - 1), seId);
				mappedPath = path;
			}
			path = mappedPath;
			path.add (mappedName);
		}

		return SDMSScheduleTable.getSchedule (sysEnv, path);
	}

	private SDMSScheduledEvent getScheduledEvent(SystemEnvironment sysEnv)
		throws SDMSException
	{
		final String evtName = (String) path.remove (path.size() - 1);

		if (seSpec == null) {
			sceId = SDMSScheduleTable.pathToId (sysEnv, path);

			final SDMSEvent evt = SDMSEventTable.idx_name_getUnique (sysEnv, evtName);
			evtId = evt.getId (sysEnv);

			path.add (evtName);
		} else {
			final ObjectURL evtObj = new ObjectURL (new Integer(Parser.EVENT), evtName, seSpec);
			evtObj.resolveByName (sysEnv);
			evtId = evtObj.objId;

			final ObjectURL sceObj = new ObjectURL (new Integer(Parser.SCHEDULE), path, seSpec);
			sceObj.resolveByName (sysEnv);
			sceId = sceObj.objId;

			path = new PathVector (sceObj.path);
			path.add (evtObj.name);
		}

		final SDMSKey scevKey = new SDMSKey (sceId, evtId);

		return SDMSScheduledEventTable.idx_sceId_evtId_getUnique (sysEnv, scevKey);
	}

	private SDMSScope getScope(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSScope p = null;
		p = SDMSScopeTable.getScope(sysEnv, path);
		return p;
	}

	private SDMSTrigger getTrigger(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSTrigger p = null;
		SDMSProxy m = master.resolve(sysEnv);
		Boolean isInverse = (Boolean) triggerInverse.value;
		Vector v;
		if (isInverse.booleanValue()) {
			v = SDMSTriggerTable.idx_seId_name.getVector(sysEnv, new SDMSKey(master.objId, name));
		} else {
			v = SDMSTriggerTable.idx_fireId_name.getVector(sysEnv, new SDMSKey(master.objId, name));
		}
		for (int i = 0; i < v.size(); ++i) {
			p = (SDMSTrigger) v.get(i);
			if (p.getIsInverse(sysEnv).equals(isInverse)) break;
			p = null;
		}
		return p;
	}

	private SDMSUser getUser(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSUser p = null;
		p = SDMSUserTable.idx_name_getUnique(sysEnv, name);
		return p;
	}

	public String toString()
	{
		String s = null;
		switch (objType.intValue()) {
			case SDMSObjectComment.ENVIRONMENT:
				s = "ENVIRONMENT " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.EVENT:
				s = "EVENT " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.EXIT_STATE_DEFINITION:
				s = "EXIT STATE DEFINITION " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.EXIT_STATE_PROFILE:
				s = "EXIT STATE PROFILE " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.EXIT_STATE_MAPPING:
				s = "EXIT STATE MAPPING " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.EXIT_STATE_TRANSLATION:
				s = "EXIT STATE TRANSLATION " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.FOLDER:
				s = "FOLDER " + (path != null ? path.toString() : objId.toString());
				break;
			case SDMSObjectComment.FOOTPRINT:
				s = "FOOTPRINT " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.GROUP:
				s = "GROUP " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.INTERVAL:
				s = "INTERVAL " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.JOB:
				s = "JOB " + objId.toString();
				break;
			case SDMSObjectComment.JOB_DEFINITION:
				s = "JOB DEFINITION " + (path != null ? path.toString() : objId.toString());
				break;
			case SDMSObjectComment.NAMED_RESOURCE:
				s = "NAMED RESOURCE " + (path != null ? path.toString() : objId.toString());
				break;
			case SDMSObjectComment.PARAMETER:
				s = "PARAMETER " + (name != null ? name + " OF " + master.toString() : objId.toString());
				break;
			case SDMSObjectComment.RESOURCE:
				s = "RESOURCE " + (path != null ? path.toString() + " IN " + master.toString() : objId.toString());
				break;
			case SDMSObjectComment.RESOURCE_STATE_DEFINITION:
				s = "RESOURCE STATE DEFINITION " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.RESOURCE_STATE_PROFILE:
				s = "RESOURCE STATE PROFILE " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.RESOURCE_STATE_MAPPING:
				s = "RESOURCE STATE MAPPING " + (name != null ? name : objId.toString());
				break;
			case SDMSObjectComment.SCHEDULE:
				s = "SCHEDULE " + (path != null ? path.toString() : objId.toString());
				break;
			case SDMSObjectComment.SCHEDULED_EVENT:
				s = "SCHEDULED EVENT " + (path != null ? path.toString() : objId.toString());
				break;
			case SDMSObjectComment.SCOPE:
				s = "SCOPE " + (path != null ? path.toString() : objId.toString());
				break;
			case SDMSObjectComment.TRIGGER:
				s = "TRIGGER " + (name != null ? name + " ON " + master.toString() : objId.toString());
				break;
			case SDMSObjectComment.USER:
				s = "USER " + (name != null ? name : objId.toString());
				break;
			default:
				break;
		}
		return s;
	}

}

