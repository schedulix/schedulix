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
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.exception.*;

public class SDMSNamedResource extends SDMSNamedResourceProxyGeneric
	implements SDMSOwnedObject
{

	public final static String __version = "@(#) $Id: SDMSNamedResource.java,v 2.16.2.7 2013/03/22 14:48:03 ronald Exp $";

	protected SDMSNamedResource(SDMSObject p_object)
	{
		super(p_object);
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector rv;
		Long nrId = getId(sysEnv);

		rv = SDMSNamedResourceTable.idx_parentId.getVector(sysEnv, nrId);
		if(rv.size() != 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03207041711",
			                               "Cannot drop, category is not empty"));
		}
		rv = SDMSResourceTable.idx_nrId.getVector(sysEnv, nrId);
		if(rv.size() > 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03206242216",
			                               "Cannot drop, depending resources still exist"));
		}
		rv = SDMSEnvironmentTable.idx_nrId.getVector(sysEnv, nrId);
		if(rv.size() > 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03301050057",
			                               "Cannot drop, depending Environments still exist"));
		}
		rv = SDMSResourceRequirementTable.idx_nrId.getVector(sysEnv, nrId);
		if(rv.size() > 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03301050157",
			                               "Cannot drop, depending Resource Requirements still exist"));
		}

		super.delete(sysEnv);
	}

	public SDMSNamedResource copy(SystemEnvironment sysEnv, Long parentId, String name)
	throws SDMSException
	{
		return copy(sysEnv, parentId, name, null);
	}

	private SDMSNamedResource copy(SystemEnvironment sysEnv, Long parentId, String name, Long pgId)
	throws SDMSException
	{

		Long id = getId(sysEnv);

		Long gId;
		if(pgId == null)
			gId = SDMSUserTable.getObject(sysEnv, sysEnv.cEnv.uid()).getDefaultGId(sysEnv);
		else
			gId = pgId;

		if(!checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(accessViolationMessage(sysEnv, "03402291219"));

		SDMSNamedResource nr;
		nr = SDMSNamedResourceTable.table.create(sysEnv, name, gId, parentId, getUsage(sysEnv), getRspId(sysEnv), getFactor(sysEnv), getInheritPrivs(sysEnv));
		ManipParameters.copy(sysEnv, id, nr.getId(sysEnv));
		Vector v = SDMSNamedResourceTable.idx_parentId.getVector(sysEnv, id);
		for(int i = 0; i < v.size(); i++) {
			SDMSNamedResource cnr = (SDMSNamedResource) v.get(i);
			cnr.copy(sysEnv, nr.getId(sysEnv), cnr.getName(sysEnv), gId);
		}
		return nr;
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
		if(sysEnv.cEnv.isJob() || sysEnv.cEnv.isJobServer())
			p = p | SDMSPrivilege.RESOURCE;
		if(sysEnv.cEnv.isUser())
			if (getParentId(sysEnv) == null)
				p = p | SDMSPrivilege.VIEW;
		return p & checkPrivs;
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return pathString(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "named resource " + getURLName(sysEnv);
	}

	public String getSubtypeName(SystemEnvironment env)
	throws SDMSException
	{
		return getUsageAsString(env);
	}

	public final void createParameters (final SystemEnvironment sysEnv, final WithHash parms, boolean allTypes)
	throws SDMSException
	{
		if (parms == null)
			return;

		final Long id = getId(sysEnv);
		final Vector keyList = new Vector (parms.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String name = (String) keyList.get (i);
			final Vector v = (Vector) parms.get(name);

			Integer t = (Integer) v.get(0);
			final String value = (String) v.get (1);

			if(!allTypes) {
				if(t.intValue() == SDMSParameterDefinition.PARAMETER)
					throw new CommonErrorException(
					        new SDMSMessage(sysEnv, "03409281200", "Parameters are not allowed for this type of named resource")
					);
			}

			if ((t.intValue() == SDMSParameterDefinition.CONSTANT) && (value == null))
				throw new CommonErrorException(
				        new SDMSMessage(sysEnv, "03409281158", "A constant or local constant must have a value")
				);

			final String sic = (value == null ? value : '=' + value);

			SDMSParameterDefinitionTable.table.create (sysEnv, id, name, t, new Integer(SDMSParameterDefinition.NONE), sic, Boolean.FALSE, null);
		}
	}

	public final void alterParameters (final SystemEnvironment sysEnv, final WithHash parms, boolean allTypes)
	throws SDMSException
	{
		SDMSParameterDefinition pd;
		String oldnm;
		final Long id = getId(sysEnv);
		int idx;
		final Integer aggFunction = new Integer(SDMSParameterDefinition.NONE);
		final Long linkPdId = null;

		Vector act_parms = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, id);

		if(parms != null) {
			if(getUsage(sysEnv).intValue() == CATEGORY && parms.size() != 0) {
				throw new CommonErrorException(
				        new SDMSMessage(sysEnv, "03409281330", "Parameters not allowed for categories")
				);
			}
			Set s = parms.keySet();
			Iterator i = s.iterator();
			while(i.hasNext()) {
				String pn = (String) i.next();
				Vector v = (Vector) parms.get(pn);

				Integer t = (Integer) v.get(0);
				String pv = (String) v.get (1);

				if(!allTypes && t.intValue() == SDMSParameterDefinition.PARAMETER)
					throw new CommonErrorException(
					        new SDMSMessage(sysEnv, "03409281218", "Parameter are not allowed for this type of named resource")
					);

				String pdef = (pv == null ? null : "=" + pv);
				if(t.intValue() == SDMSParameterDefinition.CONSTANT && pdef == null)
					throw new CommonErrorException(
					        new SDMSMessage(sysEnv, "03409281221", "Constants must have a value")
					);

				for(idx = 0; idx < act_parms.size(); idx++) {
					pd = (SDMSParameterDefinition) act_parms.get(idx);
					oldnm = pd.getName(sysEnv);
					if(oldnm.equals(pn)) {
						act_parms.removeElementAt(idx);
						idx = -1;

						pd.setType(sysEnv, t);
						pd.setDefaultValue(sysEnv, pdef);
						break;
					}
				}
				if(idx >= act_parms.size()) {

					SDMSParameterDefinitionTable.table.create(sysEnv, id, pn, t, aggFunction, pdef, Boolean.FALSE, linkPdId);
				}
			}
		}

		killParameters(sysEnv, act_parms);
	}

	public final void killParameters (final SystemEnvironment sysEnv)
	throws SDMSException
	{
		final Vector list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, getId(sysEnv));
		killParameters(sysEnv, list);
	}

	private void killParameters (final SystemEnvironment sysEnv, final Vector list)
	throws SDMSException
	{
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);
			checkReferences(sysEnv, pd.getId(sysEnv));

			final Vector rv = SDMSResourceVariableTable.idx_pdId.getVector (sysEnv, pd.getId(sysEnv));
			for(int j = 0; j < rv.size(); j++) {
				SDMSResourceVariable resv = (SDMSResourceVariable) rv.get(j);
				resv.delete(sysEnv);
			}
			pd.delete (sysEnv);
		}
	}

	private final void checkReferences (final SystemEnvironment sysEnv, final Long pdId)
	throws SDMSException
	{
		if(SDMSParameterDefinitionTable.idx_linkPdId.containsKey(sysEnv, pdId))
			throw new CommonErrorException(
			        new SDMSMessage(sysEnv, "03409281349","You cannot delete a referenced parameter")
			);
	}

}
