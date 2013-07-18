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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.jobserver.Config;

public class ShowScope extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowScope.java,v 2.13.2.2 2013/06/18 09:49:38 ronald Exp $";

	ObjectURL url;
	SDMSScope s;
	Long sId = null;
	HashSet resourceExpandList;

	public ShowScope(ObjectURL u, HashSet rel)
	{
		super();
		url = u;

		resourceExpandList = rel;

		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	private void fillHeader(SystemEnvironment sysEnv, Vector desc)
	throws SDMSException
	{

		desc.add("ID");
		desc.add("NAME");
		desc.add("OWNER");

		desc.add("TYPE");

		desc.add("INHERIT_PRIVS");

		desc.add("IS_TERMINATE");

		desc.add("IS_SUSPENDED");

		desc.add("IS_ENABLED");

		desc.add("IS_REGISTERED");

		desc.add("IS_CONNECTED");

		desc.add("HAS_ALTERED_CONFIG");

		desc.add("STATE");

		desc.add("PID");

		desc.add("NODE");

		desc.add("IDLE");

		desc.add("ERRMSG");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("RESOURCES");

		desc.add("CONFIG");

		desc.add("CONFIG_ENVMAPPING");

		desc.add("PARAMETERS");

	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{

		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Date d = new Date();

		fillHeader(sysEnv, desc);

		s = (SDMSScope) url.resolve(sysEnv);
		if(!s.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411725", "Insufficient privileges"));

		sId = s.getId(sysEnv);
		Vector v = new Vector();
		v.add(sId);
		v.add(s.pathVector(sysEnv));
		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, s.getOwnerId(sysEnv));
		v.add(g.getName(sysEnv));
		v.add(s.getTypeAsString(sysEnv));
		v.add(new SDMSPrivilege(sysEnv, s.getInheritPrivs(sysEnv).longValue()).toString());
		v.add(s.getIsTerminate(sysEnv));
		v.add(s.getIsSuspended(sysEnv));
		v.add(s.getIsEnabled(sysEnv));
		v.add(s.getIsRegistered(sysEnv));
		v.add(new Boolean(s.isConnected(sysEnv)));
		v.add(s.getHasAlteredConfig(sysEnv));
		v.add(s.getState(sysEnv));
		v.add(s.getPid(sysEnv));
		v.add(s.getNode(sysEnv));
		v.add(new Long(s.getIdle(sysEnv)));
		v.add(s.getErrmsg(sysEnv));
		v.add(getCommentDescription(sysEnv, sId));
		v.add(getCommentInfoType(sysEnv, sId));
		try {
			v.add(SDMSUserTable.getObject(sysEnv, s.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			v.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(s.getCreateTs(sysEnv).longValue());
		v.add(sysEnv.systemDateFormat.format(d));
		try {
			try {
				v.add(SDMSUserTable.getObject(sysEnv, s.getChangerUId(sysEnv)).getName(sysEnv));
			} catch(NotFoundException nfe) {
				v.add(SDMSScopeTable.getObject(sysEnv, s.getChangerUId(sysEnv)).pathString(sysEnv));
			}
		} catch (NotFoundException nfe) {
			v.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(s.getChangeTs(sysEnv).longValue());
		v.add(sysEnv.systemDateFormat.format(d));
		v.add(s.getPrivileges(sysEnv).toString());
		add_resources(sysEnv, s, v);
		collectConfig (sysEnv, v);
		v.add (ScopeParameter.get (sysEnv, sId));

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv,
		                                      "03201291430", "Scope"), desc, v);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03201291503", (s.getType (sysEnv).intValue() == SDMSScope.SERVER ? "Job Server" : "Scope") + " shown"));
	}

	private void add_resources(SystemEnvironment sysEnv, SDMSScope s, Vector v)
	throws SDMSException
	{

		ResourceTreeLister rtl = new ResourceTreeLister(sysEnv, null, s.getId(sysEnv));

		Vector roots = new Vector();
		roots.add(SDMSNamedResourceTable.idx_parentId_name.getUnique(sysEnv, new SDMSKey (null, "RESOURCE")));

		v.add(rtl.list(sysEnv, roots, resourceExpandList));
	}

	private void fillConfigHeaders(SystemEnvironment sysEnv, Vector cfgDesc, Vector envDesc)
	throws SDMSException
	{

		cfgDesc.add ("KEY");

		cfgDesc.add ("VALUE");

		cfgDesc.add ("LOCAL");

		cfgDesc.add ("ANCESTOR_SCOPE");

		cfgDesc.add ("ANCESTOR_VALUE");

		envDesc.add ("KEY");

		envDesc.add ("VALUE");

		envDesc.add ("LOCAL");

		envDesc.add ("ANCESTOR_SCOPE");

		envDesc.add ("ANCESTOR_VALUE");

	}

	private void collectConfig (SystemEnvironment sysEnv, final Vector v)
	throws SDMSException
	{
		final Vector cfgDesc = new Vector();
		final Vector envDesc = new Vector();

		fillConfigHeaders(sysEnv, cfgDesc, envDesc);

		final SDMSOutputContainer cfgTable = new SDMSOutputContainer (sysEnv, "List of Effective Scope Config", cfgDesc);

		final SDMSOutputContainer envTable = new SDMSOutputContainer (sysEnv, "List of Effective Scope Environment Mapping Config", envDesc);

		final SDMSOutputContainer cont = ScopeConfig.get (sysEnv, s);
		final Vector data = (Vector) cont.dataset.get (0);
		final HashSet keysIssued = new HashSet();

		if (cont != null) {
			final int size = cont.desc.size();
			for (int i = 0; i < size; ++i) {
				final String key = ((SDMSOutputLabel) cont.desc.get (i)).name;
				if (ScopeConfig.isInternalEntry (key))
					continue;
				if (key.startsWith(Config.NAME_PATTERN))
					keysIssued.add(Config.NAME_PATTERN);
				else
					keysIssued.add(key);

				final Object value = data.get (i);

				if (key.equals (Config.ENV_MAPPING))
					collectEnvMapping (sysEnv, envTable, (HashMap) value);

				else if (key.equals (Config.DYNAMIC))
					;

				else {
					Long scopeId = null;
					Long ancestId = null;
					String ancestVal = "";

					final String scopeKey  = ScopeConfig.PREFIX_SCOPEID      + key;
					final String ancestKey = ScopeConfig.PREFIX_ANCEST_SCOPE + key;
					final String valueKey  = ScopeConfig.PREFIX_ANCEST_VALUE + key;

					for (int j = 0; j < size; ++j) {
						final String name = ((SDMSOutputLabel) cont.desc.get (j)).name;

						if      (name.equals (scopeKey))  scopeId   = (Long) data.get (j);
						else if (name.equals (ancestKey)) ancestId  = (Long) data.get (j);
						else if (name.equals (valueKey))  ancestVal = data.get (j).toString();
					}

					if (scopeId != null) {
						final boolean isLocal = scopeId == sId;

						final Object cfgValue = key.equals (Config.REPO_PASS) ? "***" : value;

						final Vector row = new Vector();
						row.add (key);
						row.add (cfgValue);
						row.add (new Boolean (isLocal));
						row.add (getScopePath (sysEnv, isLocal ? ancestId : scopeId));
						row.add (isLocal ? ancestVal : cfgValue);

						cfgTable.addData (sysEnv, row);
					}
				}
			}
		}
		for (int i = 0; i < Config.ALL_VALUES.length; ++i) {
			String key = Config.ALL_VALUES[i];
			if (!keysIssued.contains(key)) {

				Object value = null;
				for (int j = 0; j < Config.defaultKeys.length; ++j) {
					if (key.equals(Config.defaultKeys[j])) {
						value = Config.defaultValues[j];
						break;
					}
				}
				final Vector row = new Vector();
				row.add (key);
				row.add (value == null ? "" : value);
				row.add (Boolean.FALSE);
				row.add ("DEFAULT");
				row.add (value == null ? "" : value);

				cfgTable.addData (sysEnv, row);
			}
		}

		Collections.sort (cfgTable.dataset, cfgTable.getComparator (sysEnv, 0));
		v.add (cfgTable);

		Collections.sort (envTable.dataset, envTable.getComparator (sysEnv, 0));
		v.add (envTable);
	}

	private String getScopePath (final SystemEnvironment sysEnv, final Long id)
	throws SDMSException
	{
		if (id == null)
			return "";

		final SDMSScope s = SDMSScopeTable.getObject (sysEnv, id);
		return s.pathVector (sysEnv).toString();
	}

	private final void collectEnvMapping (final SystemEnvironment sysEnv, final SDMSOutputContainer envTable, final HashMap envMap)
	throws SDMSException
	{
		final Vector envKeys = new Vector (envMap.keySet());
		final int envSize = envKeys.size();
		for (int j = 0; j < envSize; ++j) {
			final String envKey = (String) envKeys.get (j);
			if (ScopeConfig.isInternalEntry (envKey))
				continue;

			final String envValue = (String) envMap.get (envKey);

			final Long scopeId = (Long) envMap.get (ScopeConfig.PREFIX_SCOPEID + envKey);
			final boolean isLocal = scopeId == sId;

			final Long ancestId = (Long) envMap.get (ScopeConfig.PREFIX_ANCEST_SCOPE + envKey);

			final String ancestValKey = ScopeConfig.PREFIX_ANCEST_VALUE + envKey;
			final String ancestVal = envMap.containsKey (ancestValKey) ? (String) envMap.get (ScopeConfig.PREFIX_ANCEST_VALUE + envKey) : "";

			final Vector row = new Vector();
			row.add (envKey);
			row.add (envValue);
			row.add (new Boolean (isLocal));
			row.add (getScopePath (sysEnv, isLocal ? ancestId : scopeId));
			row.add (isLocal ? ancestVal : envValue);

			envTable.addData (sysEnv, row);
		}
	}
}

