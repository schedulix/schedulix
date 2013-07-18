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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSParameterDefinition extends SDMSParameterDefinitionProxyGeneric
{

	public final static String __version = "SDMSParameterDefinition $Revision: 2.2.6.1 $ / @(#) $Id: SDMSParameterDefinition.java,v 2.2.6.1 2013/03/14 10:25:21 ronald Exp $";

	protected SDMSParameterDefinition(SDMSObject p_object)
	{
		super(p_object);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		delete(sysEnv, false, null);
	}

	public void delete(SystemEnvironment sysEnv, boolean force, HashSet links)
	throws SDMSException
	{
		Long id = getId(sysEnv);
		Vector v = SDMSParameterDefinitionTable.idx_linkPdId.getVector(sysEnv, id);
		if(v.size() != 0) {
			if(!force && links == null) {
				SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(0);
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, pd.getSeId(sysEnv));
				throw new CommonErrorException(
				        new SDMSMessage(sysEnv, "03402082327",
				                        "The parameter $1 is referenced by $2", getName(sysEnv), pd.getURL(sysEnv)
				                       )
				);
			} else {
				if(force) {
					for(int i = 0; i < v.size(); i++) {
						SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(i);
						pd.delete(sysEnv, force, links);
					}
				} else {
					for(int i = 0; i < v.size(); i++) {
						SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(i);
						links.add(pd.getId(sysEnv));
					}

				}
			}
		}
		if(links != null) links.remove(id);
		super.delete(sysEnv);
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSProxy p = null;
		final Long seId = getSeId(sysEnv);
		try {
			p = SDMSSchedulingEntityTable.getObject(sysEnv, seId);
		} catch (NotFoundException nfe1) {
			try {
				p = SDMSScopeTable.getObject(sysEnv, seId);
			} catch (NotFoundException nfe2) {
				try {
					p = SDMSNamedResourceTable.getObject(sysEnv, seId);
				} catch (NotFoundException nfe3) {
					p = SDMSNamedResourceTable.getObject(sysEnv, seId);
				}
			}
		}

		return getName(sysEnv) + " of " + p.getURL(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "parameter " + getURLName(sysEnv);
	}
}

