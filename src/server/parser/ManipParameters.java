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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.output.SDMSOutputContainer;

public class ManipParameters
{
	public static final String __version = "@(#) $Id: ManipParameters.java,v 2.12.2.2 2013/06/18 09:49:34 ronald Exp $";

	private static final Integer defaultType = new Integer (SDMSParameterDefinition.CONSTANT);
	private static final Integer aggFkt = new Integer (SDMSParameterDefinition.NONE);

	public static final void create (final SystemEnvironment sysEnv, final Long id, final WithHash parms)
		throws SDMSException
	{
		ManipParameters.create (sysEnv, id, parms, false);
	}

	public static final void create (final SystemEnvironment sysEnv, final Long id, final WithHash parms, boolean allTypes)
		throws SDMSException
	{
		if (parms == null)
			return;

		final Vector keyList = new Vector (parms.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String name = (String) keyList.get (i);
			final Vector v = (Vector) parms.get(name);

			Integer t = (Integer) v.get(0);
			final String value = (String) v.get (1);
			final Boolean isLocal = (Boolean) v.get(2);
			final String exportName = (v.size() > 3 ? (String)v.get(3) : null);

			if(!allTypes)
				t = defaultType;

			if (t.equals(defaultType) && (value == null))
				continue;

			final String sic = (value == null ? value : '=' + value);

			SDMSParameterDefinitionTable.table.create (sysEnv, id, name, t, aggFkt, sic, isLocal, null, exportName);
		}
	}

	public static final void kill (final SystemEnvironment sysEnv, final Long id)
		throws SDMSException
	{
		final Vector list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, id);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);
			pd.delete (sysEnv);
		}
	}

	public static final void copy (final SystemEnvironment sysEnv, final Long oldId, final Long newId)
		throws SDMSException
	{
		final Vector list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, oldId);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);
			SDMSParameterDefinitionTable.table.create (sysEnv, newId,
								pd.getName (sysEnv),
								pd.getType (sysEnv),
								pd.getAggFunction (sysEnv),
								pd.getDefaultValue (sysEnv),
								pd.getIsLocal (sysEnv),
			                null,
			                pd.getExportName(sysEnv));
		}
	}

	public static final void alter (final SystemEnvironment sysEnv, final Long id, final WithHash parms)
		throws SDMSException
	{
		ManipParameters.alter (sysEnv, id, parms, false);
	}

	public static final void alter (final SystemEnvironment sysEnv, final Long id, final WithHash parms, boolean allTypes)
		throws SDMSException
	{
		SDMSParameterDefinition pd;
		String oldnm;
		int idx;

		Vector act_parms = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, id);

		if(parms != null) {
			Set s = parms.keySet();
			Iterator i = s.iterator();
			while(i.hasNext()) {
				String pn = (String) i.next();
				Vector v = (Vector) parms.get(pn);

				Integer t = (Integer) v.get(0);
				String pv = (String) v.get (1);
				Boolean isLocal = (Boolean) v.get (2);
				String exportName = (v.size() > 3 ? (String)v.get(3) : null);

				if(!allTypes)
					t = defaultType;

				Integer aggFunction = new Integer(SDMSParameterDefinition.NONE);
				Long linkPdId = null;
				String pdef = (pv == null ? null : "=" + pv);
				if(t.equals(defaultType) && pdef == null)
					continue;

				for(idx = 0; idx < act_parms.size(); idx++) {
					pd = (SDMSParameterDefinition) act_parms.get(idx);
					oldnm = pd.getName(sysEnv);
					if(oldnm.equals(pn)) {
						act_parms.removeElementAt(idx);
						idx = -1;

						pd.setType(sysEnv, t);
						pd.setAggFunction(sysEnv, aggFunction);
						pd.setDefaultValue(sysEnv, pdef);
						pd.setIsLocal(sysEnv, isLocal);
						pd.setLinkPdId(sysEnv, linkPdId);
						pd.setExportName(sysEnv, exportName);
						break;
					}
				}
				if(idx >= act_parms.size()) {

					SDMSParameterDefinitionTable.table.create(sysEnv, id, pn, t, aggFunction, pdef, isLocal, linkPdId, exportName);
				}
			}
		}

		for(idx = 0; idx < act_parms.size(); idx++) {
			pd = (SDMSParameterDefinition) act_parms.get(idx);
			pd.delete(sysEnv);
		}
	}

	public static final SDMSOutputContainer get (final SystemEnvironment sysEnv, final Long id)
		throws SDMSException
	{
		final Vector c_desc = new Vector();

		c_desc.add ("ID");

		c_desc.add ("NAME");

		c_desc.add ("EXPORT_NAME");

		c_desc.add ("TYPE");

		c_desc.add ("IS_LOCAL");

		c_desc.add ("EXPRESSION");

		c_desc.add ("DEFAULT_VALUE");

		c_desc.add ("REFERENCE_TYPE");

		c_desc.add ("REFERENCE_PATH");

		c_desc.add ("REFERENCE_PRIVS");

		c_desc.add ("REFERENCE_PARAMETER");
		c_desc.add ("COMMENT");
		c_desc.add ("COMMENTTYPE");

		final SDMSOutputContainer c_container = new SDMSOutputContainer (sysEnv, null, c_desc);

		final Vector pd_v = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, id);

		for (int i = 0; i < pd_v.size(); ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) pd_v.get (i);

			final Vector c_data = new Vector();
			final Long pdId = pd.getId (sysEnv);
			c_data.add (pdId);
			c_data.add (pd.getName (sysEnv));
			c_data.add (pd.getExportName (sysEnv));

			int ptype = pd.getType (sysEnv).intValue();
			c_data.add (pd.getTypeAsString (sysEnv));

			c_data.add (pd.getIsLocal (sysEnv));

			final Integer pfn = pd.getAggFunction (sysEnv);
			if (pfn.intValue() == SDMSParameterDefinition.NONE) {
				c_data.add (pd.getAggFunctionAsString (sysEnv));

				if (ptype == SDMSParameterDefinition.REFERENCE		||
				    ptype == SDMSParameterDefinition.CHILDREFERENCE	||
				    ptype == SDMSParameterDefinition.RESOURCEREFERENCE)
					c_data.add (null);
				else {
					final String defval = pd.getDefaultValue (sysEnv);
					c_data.add (defval == null ? null : defval.substring (1));
				}
			} else {
				c_data.add (pd.getAggFunctionAsString (sysEnv) + "(" + pd.getDefaultValue (sysEnv).substring (1) + ")");
				c_data.add (null);
			}

			final Long linkPdId = pd.getLinkPdId (sysEnv);
			if (linkPdId != null) {
				final SDMSParameterDefinition lpd = SDMSParameterDefinitionTable.getObject (sysEnv, linkPdId);
				if(ptype == SDMSParameterDefinition.RESOURCEREFERENCE) {
					final SDMSNamedResource nr = SDMSNamedResourceTable.getObject (sysEnv, lpd.getSeId (sysEnv));
					c_data.add (nr.getUsageAsString (sysEnv));
					c_data.add (nr.pathVector (sysEnv));
					c_data.add (nr.getPrivileges(sysEnv).toString());
				} else {
					final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, lpd.getSeId (sysEnv));
					c_data.add (se.getTypeAsString (sysEnv));
					c_data.add (se.pathVector (sysEnv));
					c_data.add (se.getPrivileges(sysEnv).toString());
				}
				c_data.add (lpd.getName (sysEnv));
			} else {
				c_data.add (null);
				c_data.add (null);
				c_data.add (null);
				c_data.add (null);
			}

			try {
				final SDMSObjectComment oc = SDMSObjectCommentTable.idx_objectId_getUnique(sysEnv, pdId);
				c_data.add (oc.getDescription(sysEnv));
				c_data.add (oc.getInfoTypeAsString(sysEnv));
			} catch(NotFoundException nfe) {
				c_data.add (null);
				c_data.add (null);
			}

			c_container.addData (sysEnv, c_data);
		}

		Collections.sort (c_container.dataset, c_container.getComparator (sysEnv, 1));

		return c_container;
	}

	public static final SDMSOutputContainer getReferences (final SystemEnvironment sysEnv, final Long id)
		throws SDMSException
	{
		final Vector c_desc = new Vector();

		c_desc.add ("ID");

		c_desc.add ("NAME");

		c_desc.add ("TYPE");

		c_desc.add ("IS_LOCAL");

		c_desc.add ("REFERENCE_TYPE");

		c_desc.add ("REFERENCE_PATH");

		c_desc.add ("REFERENCE_PRIVS");

		c_desc.add ("REFERENCE_PARAMETER");
		c_desc.add ("COMMENT");
		c_desc.add ("COMMENTTYPE");

		final SDMSOutputContainer c_container = new SDMSOutputContainer (sysEnv, null, c_desc);

		Vector pd_v = new Vector();
		final Vector pdr_v = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, id);
		for (int i = 0; i < pdr_v.size(); ++i) {
			final SDMSParameterDefinition pdr = (SDMSParameterDefinition) pdr_v.get (i);
			pd_v.addAll(SDMSParameterDefinitionTable.idx_linkPdId.getVector (sysEnv, pdr.getId(sysEnv)));
		}

		for (int i = 0; i < pd_v.size(); ++i) {
			final SDMSParameterDefinition pd = (SDMSParameterDefinition) pd_v.get (i);

			final Vector c_data = new Vector();
			final Long pdId = pd.getId (sysEnv);
			c_data.add (pdId);
			c_data.add (pd.getName (sysEnv));

			int ptype = pd.getType (sysEnv).intValue();
			c_data.add (pd.getTypeAsString (sysEnv));

			c_data.add (pd.getIsLocal (sysEnv));

			final Long linkPdId = pd.getLinkPdId (sysEnv);
			final SDMSParameterDefinition lpd = SDMSParameterDefinitionTable.getObject (sysEnv, linkPdId);
			final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, pd.getSeId (sysEnv));
			c_data.add (se.getTypeAsString (sysEnv));
			c_data.add (se.pathVector (sysEnv));
			c_data.add (se.getPrivileges(sysEnv).toString());
			c_data.add (lpd.getName (sysEnv));

			try {
				final SDMSObjectComment oc = SDMSObjectCommentTable.idx_objectId_getUnique(sysEnv, pdId);
				c_data.add (oc.getDescription(sysEnv));
				c_data.add (oc.getInfoTypeAsString(sysEnv));
			} catch(NotFoundException nfe) {
				c_data.add (null);
				c_data.add (null);
			}

			c_container.addData (sysEnv, c_data);
		}

		Collections.sort (c_container.dataset, c_container.getComparator (sysEnv, 3, 4));

		return c_container;
	}

	private ManipParameters()
	{

	}
}
