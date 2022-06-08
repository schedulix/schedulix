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

public class SDMSParameterDefinition extends SDMSParameterDefinitionProxyGeneric
{

	public final static String __version = "SDMSParameterDefinition $Revision: 2.2.6.1 $ / @(#) $Id: SDMSParameterDefinition.java,v 2.2.6.1 2013/03/14 10:25:21 ronald Exp $";

	protected SDMSParameterDefinition(SDMSObject p_object)
	{
		super(p_object);
	}

	private void deleteExtents(SystemEnvironment env)
	throws SDMSException
	{
		Long id = getId(env);
		Vector v = SDMSVersionedExtentsTable.idx_oId.getVector(env, getId(env));
		for(int i = 0; i < v.size(); i++) {
			SDMSVersionedExtents e = (SDMSVersionedExtents) v.get(i);
			e.delete(env);
		}
	}

	public void delete(SystemEnvironment env)
		throws SDMSException
	{
		HashSet<Long> s = new HashSet<Long>();
		s.add(getSeId(env));
		delete(env, s, false);
	}

	public void delete(SystemEnvironment env, HashSet<Long> seIds, boolean force)
		throws SDMSException
	{
		Long id = getId(env);
		Vector v = SDMSParameterDefinitionTable.idx_linkPdId.getVector(env, id);
		for(int i = 0; i < v.size(); i++) {
			SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(i);
			if (seIds.contains(pd.getSeId(env)))
				continue;
				throw new CommonErrorException(
						new SDMSMessage(env, "03402082327",
							"The parameter $1 is referenced by $2", getName(env), pd.getURL(env)
						)
				);
		}
		if (getIsLong(env).booleanValue())
			deleteExtents(env);
		super.delete(env);
	}

	public String getDefaultValue (SystemEnvironment env)
	throws SDMSException
	{
		String val = super.getDefaultValue (env);
		if (getIsLong(env).booleanValue()) {
			Vector v = SDMSVersionedExtentsTable.idx_oId.getVector(env, getId(env));
			for (int s = 1; s <= v.size(); s ++) {
				for (int i = 0; i < v.size(); i ++) {
					SDMSVersionedExtents e = (SDMSVersionedExtents) v.get(i);
					if (s == e.getSequence(env).intValue()) {
						val = val + e.getExtent(env);
					}
				}
			}
		}
		return (val);
	}

	private void createExtents(SystemEnvironment env, String p_value)
	throws SDMSException
	{
		int e = 1;
		while (p_value.length() > 0) {
			if (p_value.length() > SDMSVersionedExtentsProxyGeneric.getExtentMaxLength()) {
				SDMSVersionedExtentsTable.table.create(env, getId(env), Integer.valueOf(e), p_value.substring(0, SDMSVersionedExtentsProxyGeneric.getExtentMaxLength()));
				p_value = p_value.substring(SDMSVersionedExtentsProxyGeneric.getExtentMaxLength());
			} else {
				SDMSVersionedExtentsTable.table.create(env, getId(env), Integer.valueOf(e), p_value);
				break;
			}
			e ++;
		}
	}

	public void setDefaultValue (SystemEnvironment env, String p_defaultValue)
	throws SDMSException
	{
		String oldValue = getDefaultValue(env);
		if(p_defaultValue != null && p_defaultValue.equals(oldValue)) return;
		if(p_defaultValue == null && oldValue == null) return;
		if (p_defaultValue != null) {
			if (getIsLong(env).booleanValue())
				deleteExtents(env);
			if (p_defaultValue.length() > getDefaultValueMaxLength()) {
				createExtents(env, p_defaultValue.substring(getDefaultValueMaxLength()));
				p_defaultValue = p_defaultValue.substring(0, getDefaultValueMaxLength());
				setIsLong(env, Boolean.TRUE);
			} else
				setIsLong(env, Boolean.FALSE);
		}
		super.setDefaultValue(env, p_defaultValue);
		return ;
	}

	public String getURLName(SystemEnvironment env)
		throws SDMSException
	{
		SDMSProxy p = null;
		final Long seId = getSeId(env);
		try {
			p = SDMSSchedulingEntityTable.getObject(env, seId);
		} catch (NotFoundException nfe1) {
			try {
				p = SDMSScopeTable.getObject(env, seId);
			} catch (NotFoundException nfe2) {
				try {
					p = SDMSNamedResourceTable.getObject(env, seId);
				} catch (NotFoundException nfe3) {
					p = SDMSNamedResourceTable.getObject(env, seId);
				}
			}
		}

		return getName(env) + " of " + p.getURL(env);
	}

	public String getURL(SystemEnvironment env)
		throws SDMSException
	{
		return "parameter " + getURLName(env);
	}

	public	void setLinkPdId (SystemEnvironment env, Long p_linkPdId)
	throws SDMSException
	{
		super.setLinkPdId(env, p_linkPdId);
		checkRefCycle(env);
	}

	protected void checkRefCycle(SystemEnvironment env)
	throws SDMSException
	{
		checkRefCycle(env, new HashSet<Long>());
	}

	protected void checkRefCycle(SystemEnvironment env, HashSet<Long> pdIds)
	throws SDMSException
	{
		Long linkPdId = getLinkPdId(env);
		if (linkPdId == null)
			return;
		pdIds.add(getId(env));
		if (pdIds.contains(linkPdId))
			throw new CommonErrorException(new SDMSMessage(env, "03507140909", "Cyclic Parameter References detected"));

		SDMSParameterDefinition pd = SDMSParameterDefinitionTable.getObject(env, linkPdId);
		pd.checkRefCycle(env, pdIds);
	}
}

