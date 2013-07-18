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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class AlterJobDefDependents extends ManipJobDefinition
{

	public final static String __version = "@(#) $Id: AlterJobDefDependents.java,v 2.1.18.1 2013/03/14 10:24:21 ronald Exp $";

	private SDMSSchedulingEntity se;
	private Long seId;

	public AlterJobDefDependents(PathVector p, String n, WithHash w, Boolean ne)
	{
		super(p, n, w, ne);
	}

	private void delChildren(SystemEnvironment sysEnv, Vector arg, boolean ignoreNotFound)
	throws SDMSException
	{
		int i;

		try {
			for(i = 0; i < arg.size(); i++) {
				Vector p = (Vector) arg.get(i);
				SDMSSchedulingEntity c = SDMSSchedulingEntityTable.get(sysEnv, p, null);
				Long cId = c.getId(sysEnv);
				SDMSSchedulingHierarchy sh =
				        SDMSSchedulingHierarchyTable.idx_parentId_childId_getUnique(sysEnv, new SDMSKey(seId, cId));
				sh.delete(sysEnv);
			}
		} catch(NotFoundException nfe) {
			if(!ignoreNotFound) throw nfe;
		}
	}

	private void delParameters(SystemEnvironment sysEnv, Vector arg, boolean ignoreNotFound)
	throws SDMSException
	{
		int i;

		try {
			for(i = 0; i < arg.size(); i++) {
				String n = (String) arg.get(i);
				SDMSParameterDefinition pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(seId, n));
				pd.delete(sysEnv);
			}
		} catch(NotFoundException nfe) {
			if(!ignoreNotFound) throw nfe;
		}
	}

	private void delRequirements(SystemEnvironment sysEnv, Vector arg, boolean ignoreNotFound)
	throws SDMSException
	{
		int i;

		try {
			for(i = 0; i < arg.size(); i++) {
				Vector p = (Vector) arg.get(i);
				SDMSSchedulingEntity r = SDMSSchedulingEntityTable.get(sysEnv, p, null);
				Long rId = r.getId(sysEnv);
				SDMSDependencyDefinition dd =
				        SDMSDependencyDefinitionTable.idx_DependentId_RequiredId_getUnique(sysEnv, new SDMSKey(seId, rId));
				dd.delete(sysEnv);
			}
		} catch(NotFoundException nfe) {
			if(!ignoreNotFound) throw nfe;
		}
	}

	private void delResources(SystemEnvironment sysEnv, Vector arg, boolean ignoreNotFound)
	throws SDMSException
	{
		int i;

		try {
			for(i = 0; i < arg.size(); i++) {
				Vector p = (Vector) arg.get(i);
				SDMSNamedResource nr = SDMSNamedResourceTable.getNamedResource(sysEnv, p);
				Long nrId = nr.getId(sysEnv);
				SDMSResourceRequirement rr = SDMSResourceRequirementTable.idx_seId_nrId_getUnique(sysEnv, new SDMSKey(seId, nrId));
				rr.delete(sysEnv);
			}
		} catch(NotFoundException nfe) {
			if(!ignoreNotFound) throw nfe;
		}
	}

	private void addOrAlterChildren(SystemEnvironment sysEnv, Vector arg, boolean isAdd, boolean ignoreError)
	throws SDMSException
	{
		int i;

		for(i = 0; i < arg.size(); i++) {
			WithHash wh = (WithHash) arg.get(i);
			super.addOrAlterChild(sysEnv, wh, se, isAdd, ignoreError);
		}
	}

	private void addOrAlterParameters(SystemEnvironment sysEnv, WithHash wh, boolean isAdd, boolean ignoreError)
	throws SDMSException
	{
		super.addOrAlterParameters(sysEnv, wh, se, isAdd, ignoreError);
	}

	private void addOrAlterRequirements(SystemEnvironment sysEnv, Vector arg, boolean isAdd, boolean ignoreError)
	throws SDMSException
	{
		int i;

		for(i = 0; i < arg.size(); i++) {
			WithHash wh = (WithHash) arg.get(i);
			super.addOrAlterRequirement(sysEnv, wh, se, isAdd, ignoreError);
		}
	}

	private void addOrAlterResources(SystemEnvironment sysEnv, Vector arg, boolean isAdd, boolean ignoreError)
	throws SDMSException
	{
		int i;

		for(i = 0; i < arg.size(); i++) {
			WithHash wh = (WithHash) arg.get(i);
			super.addOrAlterResource(sysEnv, wh, se, isAdd, ignoreError);
		}
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Object arg;

		try {
			se = SDMSSchedulingEntityTable.get(sysEnv, path, name);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311130034","No Job Definition altered"));
				return;
			}
			throw nfe;
		}
		seId = se.getId(sysEnv);

		if(withs.containsKey(ParseStr.S_DELCHILD))	{
			arg = withs.get(ParseStr.S_DELCHILD);
			delChildren(sysEnv, (Vector) arg, false);
		}
		if(withs.containsKey(ParseStr.S_DELREQ))	{
			arg = withs.get(ParseStr.S_DELREQ);
			delRequirements(sysEnv, (Vector) arg, false);
		}
		if(withs.containsKey(ParseStr.S_DELRES))	{
			arg = withs.get(ParseStr.S_DELRES);
			delResources(sysEnv, (Vector) arg, false);
		}
		if(withs.containsKey(ParseStr.S_DELPARM))	{
			arg = withs.get(ParseStr.S_DELPARM);
			delParameters(sysEnv, (Vector) arg, false);
		}

		if(withs.containsKey(ParseStr.S_XDELCHILD))	{
			arg = withs.get(ParseStr.S_XDELCHILD);
			delChildren(sysEnv, (Vector) arg, true);
		}
		if(withs.containsKey(ParseStr.S_XDELREQ))	{
			arg = withs.get(ParseStr.S_XDELREQ);
			delRequirements(sysEnv, (Vector) arg, true);
		}
		if(withs.containsKey(ParseStr.S_XDELRES))	{
			arg = withs.get(ParseStr.S_XDELRES);
			delResources(sysEnv, (Vector) arg, true);
		}
		if(withs.containsKey(ParseStr.S_XDELPARM))	{
			arg = withs.get(ParseStr.S_XDELPARM);
			delParameters(sysEnv, (Vector) arg, true);
		}

		if(withs.containsKey(ParseStr.S_ALTCHILD))	{
			arg = withs.get(ParseStr.S_ALTCHILD);
			addOrAlterChildren(sysEnv, (Vector) arg, false, false);
		}
		if(withs.containsKey(ParseStr.S_ALTREQ))	{
			arg = withs.get(ParseStr.S_ALTREQ);
			addOrAlterRequirements(sysEnv, (Vector) arg, false, false);
		}
		if(withs.containsKey(ParseStr.S_ALTRES))	{
			arg = withs.get(ParseStr.S_ALTRES);
			addOrAlterResources(sysEnv, (Vector) arg, false, false);
		}
		if(withs.containsKey(ParseStr.S_ALTPARM))	{
			arg = withs.get(ParseStr.S_ALTPARM);
			addOrAlterParameters(sysEnv, (WithHash) arg, false, false);
		}

		if(withs.containsKey(ParseStr.S_XALTCHILD))	{
			arg = withs.get(ParseStr.S_XALTCHILD);
			addOrAlterChildren(sysEnv, (Vector) arg, false, true);
		}
		if(withs.containsKey(ParseStr.S_XALTREQ))	{
			arg = withs.get(ParseStr.S_XALTREQ);
			addOrAlterRequirements(sysEnv, (Vector) arg, false, true);
		}
		if(withs.containsKey(ParseStr.S_XALTRES))	{
			arg = withs.get(ParseStr.S_XALTRES);
			addOrAlterResources(sysEnv, (Vector) arg, false, true);
		}
		if(withs.containsKey(ParseStr.S_XALTPARM))	{
			arg = withs.get(ParseStr.S_XALTPARM);
			addOrAlterParameters(sysEnv, (WithHash) arg, false, true);
		}

		if(withs.containsKey(ParseStr.S_ADDCHILD))	{
			arg = withs.get(ParseStr.S_ADDCHILD);
			addOrAlterChildren(sysEnv, (Vector) arg, true, false);
		}
		if(withs.containsKey(ParseStr.S_ADDREQ))	{
			arg = withs.get(ParseStr.S_ADDREQ);
			addOrAlterRequirements(sysEnv, (Vector) arg, true, false);
		}
		if(withs.containsKey(ParseStr.S_ADDRES))	{
			arg = withs.get(ParseStr.S_ADDRES);
			addOrAlterResources(sysEnv, (Vector) arg, true, false);
		}
		if(withs.containsKey(ParseStr.S_ADDPARM))	{
			arg = withs.get(ParseStr.S_ADDPARM);
			addOrAlterParameters(sysEnv, (WithHash) arg, true, false);
		}

		if(withs.containsKey(ParseStr.S_XADDCHILD))	{
			arg = withs.get(ParseStr.S_XADDCHILD);
			addOrAlterChildren(sysEnv, (Vector) arg, true, true);
		}
		if(withs.containsKey(ParseStr.S_XADDREQ))	{
			arg = withs.get(ParseStr.S_XADDREQ);
			addOrAlterRequirements(sysEnv, (Vector) arg, true, true);
		}
		if(withs.containsKey(ParseStr.S_XADDRES))	{
			arg = withs.get(ParseStr.S_XADDRES);
			addOrAlterResources(sysEnv, (Vector) arg, true, true);
		}
		if(withs.containsKey(ParseStr.S_XADDPARM))	{
			arg = withs.get(ParseStr.S_XADDPARM);
			addOrAlterParameters(sysEnv, (WithHash) arg, true, true);
		}

		checkParameterRI(sysEnv, seId);

		result.setFeedback(new SDMSMessage(sysEnv, "03211051235","Job Definition altered"));
	}

}

