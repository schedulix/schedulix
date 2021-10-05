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
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.*;

public class SDMSResourceTemplate extends SDMSResourceTemplateProxyGeneric
	implements SDMSOwnedObject
{

	public final static String __version = "@(#) $Id: SDMSResourceTemplate.java,v 2.11.2.3 2013/03/22 14:48:03 ronald Exp $";

	protected SDMSResourceTemplate(SDMSObject p_object)
	{
		super(p_object);
	}
	public String getName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		String s;
		final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, getNrId(sysEnv));

		s = nr.getName(sysEnv);

		return s;
	}

	public void setRequestableAmount(SystemEnvironment sysEnv, Integer amount)
	throws SDMSException
	{
		super.setRequestableAmount(sysEnv, amount);
		return ;
	}

	public void setRsdId(SystemEnvironment sysEnv, Long rsdId)
	throws SDMSException
	{
		setRsdId(sysEnv, rsdId, null);
		return ;
	}

	public void setRsdId(SystemEnvironment sysEnv, Long rsdId, SDMSSubmittedEntity causeSme)
	throws SDMSException
	{
		SDMSSubmittedEntity sme = null;
		Long nrId = getNrId(sysEnv);

		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
		Long rspId = nr.getRspId(sysEnv);
		if(rspId == null) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03409301352", "Named Resource $1 is stateless", nr.pathVector(sysEnv).toString()));
		} else {
			if(!SDMSResourceStateTable.idx_rsdId_rspId.containsKey(sysEnv, new SDMSKey(rsdId, rspId))) {
				SDMSResourceStateProfile rsp = SDMSResourceStateProfileTable.getObject(sysEnv, rspId);
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03409301353", "Resource state is not defined in the profile $1",
				                               rsp.getName(sysEnv)));
			}
		}

		return ;
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		killVariables(sysEnv);
		super.delete(sysEnv);
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		Long nrId;
		Long seId;
		long ptmp;

		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
		if(sysEnv.cEnv.isUser()) {
			nrId = getNrId(sysEnv);
			final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
			ptmp = nr.getPrivileges(sysEnv, SDMSPrivilege.RESOURCE, fastFail, checkGroups);
			seId = getSeId(sysEnv);

			SDMSSchedulingEntity se;
			se = SDMSSchedulingEntityTable.getObject(sysEnv, seId);
			ptmp = ptmp & se.getPrivileges(sysEnv, SDMSPrivilege.RESOURCE, fastFail, checkGroups);
			if((ptmp & SDMSPrivilege.RESOURCE) == SDMSPrivilege.RESOURCE)
				p = p | SDMSPrivilege.CREATE;
		}
		return p & checkPrivs;
	}

	private static Boolean nonlocal = Boolean.FALSE;

	private final SDMSParameterDefinition getParameterDefinition(final SystemEnvironment sysEnv, final String name)
	throws SDMSException
	{
		SDMSParameterDefinition pd = null;

		final Long nrId = getNrId(sysEnv);
		pd = null;
		try {
			pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(nrId, name));
		} catch(NotFoundException nfe) {
			throw new CommonErrorException(
			        new SDMSMessage(sysEnv, "03409111748", "Parameter $1 not defined for this resource", name));
		}

		return pd;
	}

	public final void createVariables (final SystemEnvironment sysEnv, final WithHash parms)
	throws SDMSException
	{
		if (parms == null)
			return;

		Long id = getId(sysEnv);
		SDMSParameterDefinition pd;

		final Vector keyList = new Vector (parms.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String name = (String) keyList.get (i);
			final Vector tmp = (Vector) parms.get(name);
			final String value = (String) tmp.get(1);

			pd = getParameterDefinition(sysEnv, name);
			int type = pd.getType(sysEnv).intValue();
			if(type == SDMSParameterDefinition.CONSTANT)
				throw new CommonErrorException(
				        new SDMSMessage(sysEnv, "03409111751", "Parameter $1 is defined as a $2 for this resource", name, pd.getTypeAsString(sysEnv)));
			if(value == null) continue;

			final String sic = (value == null ? value : '=' + (String) value);

			SDMSTemplateVariableTable.table.create (sysEnv, pd.getId(sysEnv), id, sic);
		}
	}

	public final void killVariables (final SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Long id = getId(sysEnv);
		final Vector list = SDMSResourceVariableTable.idx_rId.getVector (sysEnv, id);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSTemplateVariable rv = (SDMSTemplateVariable) list.get (i);
			rv.delete (sysEnv);
		}
	}

	public final void copyVariables (final SystemEnvironment sysEnv, final Long newId)
	throws SDMSException
	{
		final Long oldId = getId(sysEnv);
		final Vector list = SDMSTemplateVariableTable.idx_rtId.getVector (sysEnv, oldId);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSTemplateVariable rv = (SDMSTemplateVariable) list.get (i);
			SDMSTemplateVariableTable.table.create (sysEnv, rv.getPdId(sysEnv), newId, rv.getValue (sysEnv));
		}
	}

	public final void alterVariables (final SystemEnvironment sysEnv, final WithHash parms, long version)
	throws SDMSException
	{
		SDMSTemplateVariable rv;
		SDMSParameterDefinition pd;
		Long pdId;
		int idx;
		String pdef;
		final Long id = getId(sysEnv);

		Vector act_parms = SDMSTemplateVariableTable.idx_rtId.getVector(sysEnv, id);
		final Long nrId = getNrId(sysEnv);

		if(parms != null) {
			Set s = parms.keySet();
			Iterator i = s.iterator();
			while(i.hasNext()) {
				final String pn = (String) i.next();
				final Vector tmp = (Vector) parms.get(pn);
				final String pv = (String) tmp.get(1);

				pd = getParameterDefinition(sysEnv, pn);
				if(pd.getType(sysEnv).intValue() == SDMSParameterDefinition.CONSTANT)
					throw new CommonErrorException(
					        new SDMSMessage(sysEnv, "03409131151",
					                        "Parameter $1 is defined as a $2 for this resource", pn, pd.getTypeAsString(sysEnv)));

				pdId = pd.getId(sysEnv);

				pdef = null;
				if(pv != null) {
					pdef = "=" + pv;
				}

				for(idx = 0; idx < act_parms.size(); idx++) {
					rv = (SDMSTemplateVariable) act_parms.get(idx);
					if(pdId.equals(rv.getPdId(sysEnv))) {
						act_parms.removeElementAt(idx);
						idx = -1;
						if(pdef == null) {
							rv.delete(sysEnv);
						} else {
							rv.setValue(sysEnv, pdef);
						}
						break;
					}
				}
				if(idx >= act_parms.size()) {
					if(pdef != null)
						SDMSTemplateVariableTable.table.create(sysEnv, pdId, id, pdef);
				}
			}
		}
	}

	public final SDMSOutputContainer getVariables (final SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Vector c_desc = new Vector();
		final HashSet names = new HashSet();
		final Long id = getId(sysEnv);

		c_desc.add("ID");
		c_desc.add ("NAME");
		c_desc.add ("TYPE");
		c_desc.add ("VALUE");
		c_desc.add ("IS_DEFAULT");

		final SDMSOutputContainer c_container = new SDMSOutputContainer (sysEnv, null, c_desc);

		final Vector rv_v = SDMSTemplateVariableTable.idx_rtId.getVector (sysEnv, id);

		for (int i = 0; i < rv_v.size(); ++i) {
			final SDMSTemplateVariable rv = (SDMSTemplateVariable) rv_v.get (i);

			final Vector c_data;
			final String name;
			try {
				final SDMSParameterDefinition pd = SDMSParameterDefinitionTable.getObject(sysEnv, rv.getPdId(sysEnv));
				name = pd.getName (sysEnv);
				c_data = new Vector();
				c_data.add (rv.getId (sysEnv));
				c_data.add (name);
				c_data.add (pd.getTypeAsString(sysEnv));
			} catch(NotFoundException nfe) {
				continue;
			}

			final String value = rv.getValue (sysEnv);
			c_data.add (value == null ? null : value.substring (1));
			c_data.add (Boolean.FALSE);
			names.add(name);

			c_container.addData(sysEnv, c_data);
		}

		final Vector v = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, getNrId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(i);
			final String name = pd.getName(sysEnv);
			final Vector c_data;
			if(!names.contains(name)) {
				c_data = new Vector();
				c_data.add(pd.getId(sysEnv));
				c_data.add(name);
				c_data.add(pd.getTypeAsString(sysEnv));
				final String value = pd.getDefaultValue(sysEnv);
				c_data.add((value == null ? null : value.substring(1)));
				c_data.add(Boolean.TRUE);
				c_container.addData(sysEnv, c_data);
			}
		}

		Collections.sort (c_container.dataset, c_container.getComparator (sysEnv, 1));

		return c_container;
	}
}
