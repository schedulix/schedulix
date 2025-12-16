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

public class SDMSUserParameter extends SDMSUserParameterProxyGeneric
{

	protected SDMSUserParameter(SDMSObject p_object)
	{
		super(p_object);
	}

	private void deleteExtents(SystemEnvironment env)
	throws SDMSException
	{
		Long id = getId(env);
		Vector v = SDMSUserExtentsTable.idx_upId.getVector(env, id);
		for(int i = 0; i < v.size(); i++) {
			SDMSUserExtents e = (SDMSUserExtents) v.get(i);
			e.delete(env);
		}
	}

	public void delete(SystemEnvironment env)
	throws SDMSException
	{
		if (getIsLong(env).booleanValue())
			deleteExtents(env);
		super.delete(env);
	}

	public String getValue (SystemEnvironment env)
	throws SDMSException
	{
		String val = super.getValue (env);
		if (getIsLong(env).booleanValue()) {
			Vector v = SDMSUserExtentsTable.idx_upId.getVector(env, getId(env));
			for (int s = 1; s <= v.size(); s ++) {
				for (int i = 0; i < v.size(); i ++) {
					SDMSUserExtents e = (SDMSUserExtents) v.get(i);
					if (s == e.getSequence(env).intValue()) {
						val = val + e.getExtent(env);
						break;
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
			if (p_value.length() > SDMSUserExtentsProxyGeneric.getExtentMaxLength()) {
				SDMSUserExtentsTable.table.create(env, getId(env), Integer.valueOf(e), p_value.substring(0, SDMSUserExtentsProxyGeneric.getExtentMaxLength()));
				p_value = p_value.substring(SDMSUserExtentsProxyGeneric.getExtentMaxLength());
			} else {
				SDMSUserExtentsTable.table.create(env, getId(env), Integer.valueOf(e), p_value);
				break;
			}
			e ++;
		}
	}

	public void setValue (SystemEnvironment env, String p_value)
	throws SDMSException
	{
		String oldValue = getValue(env);
		if(p_value != null && p_value.equals(oldValue)) return;
		if(p_value == null && oldValue == null) return;
		if (p_value != null) {
			if (getIsLong(env).booleanValue())
				deleteExtents(env);
			if (p_value.length() > getValueMaxLength()) {
				createExtents(env, p_value.substring(getValueMaxLength()));
				p_value = p_value.substring(0, getValueMaxLength());
				setIsLong(env, Boolean.TRUE);
			} else
				setIsLong(env, Boolean.FALSE);
		}
		super.setValue(env, p_value);
		return ;
	}

}
