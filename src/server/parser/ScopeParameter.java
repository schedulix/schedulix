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

import java.util.*;

import de.independit.scheduler.server.SystemEnvironment;
import de.independit.scheduler.server.SDMSConstants;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.output.SDMSOutputContainer;

public class ScopeParameter
{
	public static final String __version = "@(#) $Id: ScopeParameter.java,v 2.4.14.1 2013/03/14 10:24:47 ronald Exp $";

	private static final Integer typeConstant     = SDMSConstants.PD_CONSTANT;
	private static final Integer typeDynamic      = SDMSConstants.PD_DYNAMIC;
	private static final Integer typeDynamicValue = SDMSConstants.PD_DYNAMICVALUE;

	private static final Integer aggFunctionNone = SDMSConstants.PD_NONE;

	private static final void markAltered (final SystemEnvironment sysEnv, final Long sId)
		throws SDMSException
	{
		ScopeConfig.markAltered (sysEnv, SDMSScopeTable.getObject (sysEnv, sId));
	}

	public static final void create (final SystemEnvironment sysEnv, final Long id, final WithHash p)
		throws SDMSException
	{
		if (p == null)
			return;

		final Vector list = new Vector (p.keySet());
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final String name = (String) list.get (i);
			final String value = (String) p.get (name);

			final Integer type;
			final String sic;
			if (value == null) {
				type = typeDynamic;
				sic = null;
			} else {
				type = typeConstant;

				sic = '=' + value;
			}

			SDMSParameterDefinitionTable.table.create (sysEnv,
			                id,
			                name,
			                type,
			                aggFunctionNone,
			                sic,
			                Boolean.FALSE,
			                null,
			                null);

			if (type.intValue() != SDMSParameterDefinition.DYNAMICVALUE)
				killChildDynamicValues (sysEnv, id, name);
		}

		markAltered (sysEnv, id);
	}

	public static final void kill (final SystemEnvironment sysEnv, final Long id)
		throws SDMSException
	{
		final Vector list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, id);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);
			if (pd.getType (sysEnv).intValue() == SDMSParameterDefinition.DYNAMIC)
				killChildDynamicValues (sysEnv, id, pd.getName (sysEnv));
			pd.delete (sysEnv);
		}

		markAltered (sysEnv, id);
	}

	private static final void killChildDynamicValues (final SystemEnvironment sysEnv, final Long id, final String name)
		throws SDMSException
	{
		final Vector childList = SDMSScopeTable.idx_parentId.getVector (sysEnv, id);
		final int childSize = childList.size();
		for (int i = 0; i < childSize; ++i) {
			final SDMSScope childScope = (SDMSScope) childList.get (i);
			final Long childSId = childScope.getId (sysEnv);

			try {
				final SDMSParameterDefinition pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique (sysEnv, new SDMSKey (childSId, name));
				if (pd.getType (sysEnv).intValue() == SDMSParameterDefinition.DYNAMICVALUE)
					pd.delete (sysEnv);
			} catch (final NotFoundException e) {
				killChildDynamicValues (sysEnv, childSId, name);
			}
		}
	}

	public static final void copy (final SystemEnvironment sysEnv, final Long oldId, final Long newId)
		throws SDMSException
	{
		final Vector list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, oldId);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);
			if (pd.getType (sysEnv).intValue() != SDMSParameterDefinition.DYNAMICVALUE)
				SDMSParameterDefinitionTable.table.create (sysEnv,
				                newId,
				                pd.getName (sysEnv),
				                pd.getType (sysEnv),
				                pd.getAggFunction (sysEnv),
				                pd.getDefaultValue (sysEnv),
				                pd.getIsLocal (sysEnv),
				                null,
				                null);
		}
	}

	public static final void alter (final SystemEnvironment sysEnv, final Long id, final WithHash p)
		throws SDMSException
	{
		Vector list;
		int size;

		final WithHash parms = p == null ? new WithHash() : (WithHash) p.clone();

		list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, id);
		size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);
			final String name = pd.getName (sysEnv);

			final String value = (String) parms.get (name);

			final Integer type;
			final String sic;
			if (value == null) {
				type = typeDynamic;
				sic = pd.getDefaultValue (sysEnv);
			} else {
				type = typeConstant;

				sic = '=' + value;
			}

			if (parms.containsKey (name)) {
				pd.setType         (sysEnv, type);
				pd.setAggFunction  (sysEnv, aggFunctionNone);
				pd.setDefaultValue (sysEnv, sic);
				pd.setLinkPdId     (sysEnv, null);

				if (type.intValue() != SDMSParameterDefinition.DYNAMICVALUE)
					killChildDynamicValues (sysEnv, id, name);

				parms.remove (name);
			} else {
				pd.delete (sysEnv);
				killChildDynamicValues (sysEnv, id, name);
			}
		}

		create (sysEnv, id, parms);

		markAltered (sysEnv, id);
	}

	public static final void setDynamicValues (final SystemEnvironment sysEnv, final SDMSScope s, final WithHash p)
		throws SDMSException
	{
		if (s.getType (sysEnv).intValue() != SDMSScope.SERVER)
			throw new CommonErrorException (new SDMSMessage (sysEnv, "04307171658", "Only jobservers can have dynamic values"));

		Vector list;
		int size;

		final WithHash parms = (WithHash) p.clone();
		final Long sId = s.getId (sysEnv);

		list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, sId);
		size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);

			final String name = pd.getName (sysEnv);
			final int type = pd.getType (sysEnv).intValue();

			final String sic = '=' + (String) parms.get (name);

			if (parms.containsKey (name)) {
				if (! ((type == SDMSParameterDefinition.DYNAMIC) || (type == SDMSParameterDefinition.DYNAMICVALUE)))
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04307171902", "Parameter " + name + " is not dynamic"));

				pd.setDefaultValue (sysEnv, sic);
				parms.remove (name);
			} else if (type == SDMSParameterDefinition.DYNAMICVALUE)
				pd.delete (sysEnv);
		}

		list = new Vector (parms.keySet());
		size = list.size();
		for (int i = 0; i < size; ++i) {
			final String key = (String) list.get (i);

			final String sic = '=' + (String) parms.get (key);

			SDMSParameterDefinitionTable.table.create (sysEnv,
			                sId,
			                key,
			                typeDynamicValue,
			                aggFunctionNone,
			                sic,
			                Boolean.FALSE,
			                null,
			                null);
		}
	}

	public static final SDMSOutputContainer get (final SystemEnvironment sysEnv, final Long id)
		throws SDMSException
	{
		return ManipParameters.get (sysEnv, id);
	}

	public static final SDMSOutputContainer getRecursive (final SystemEnvironment sysEnv, final SDMSScope obj)
	throws SDMSException
	{
		return ManipParameters.getRecursive (sysEnv, obj);
	}

	private ScopeParameter()
	{

	}
}
