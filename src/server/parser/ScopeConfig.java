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
import de.independit.scheduler.server.util.SDMSThread;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.output.SDMSOutputContainer;
import de.independit.scheduler.server.dump.Dump;
import de.independit.scheduler.jobserver.Config;

public class ScopeConfig
{
	public static final String __version = "@(#) $Id: ScopeConfig.java,v 2.9.8.1 2013/03/14 10:24:47 ronald Exp $";

	public static final String PREFIX_SCOPEID = ".";
	public static final String PREFIX_ANCEST_VALUE = ";";
	public static final String PREFIX_ANCEST_SCOPE = ":";

	private static final HashSet ADMIN_GID = new HashSet();

	static {
		ADMIN_GID.add (SDMSObject.adminGId);
	}

	public static final boolean isInternalEntry (final String key)
	{
		return key.startsWith     (PREFIX_SCOPEID)
			|| key.startsWith (PREFIX_ANCEST_VALUE)
			|| key.startsWith (PREFIX_ANCEST_SCOPE);
	}

	public static final void create (final SystemEnvironment sysEnv, final Long sId, final WithHash cfg)
		throws SDMSException
	{
		if (cfg == null)
			return;

		final Vector keyList = new Vector (cfg.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String key = (String) keyList.get (i);
			final Object value = cfg.get (key);

			if (value == null)
				continue;

			try {
				if (key.equals (Config.ENV_MAPPING)) {
					createEnvMappings (sysEnv, sId, (WithHash) value);
					continue;
				}

				createConfig (sysEnv, sId, key, (String) value);
			} catch (final ClassCastException e) {
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04307151709", "invalid " + ParseStr.S_CONFIG + ": " + key));
			}
		}
	}

	private static final void createConfig (final SystemEnvironment sysEnv, final Long sId, final String key, final String value)
		throws SDMSException
	{

		final String sic = '=' + value;

		SDMSScopeConfigTable.table.create (sysEnv, key, sic, sId);
	}

	private static final void createEnvMappings (final SystemEnvironment sysEnv, final Long sId, final WithHash mappings)
		throws SDMSException
	{
		final Vector keyList = new Vector (mappings.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String key = (String) keyList.get (i);

			final String value = '=' + (String) mappings.get (key);

			SDMSScopeConfigEnvMappingTable.table.create (sysEnv, key, value, sId);
		}
	}

	public static final void kill (final SystemEnvironment sysEnv, final SDMSScope s)
		throws SDMSException
	{
		final Long sId = s.getId (sysEnv);

		killConfig      (sysEnv, sId);
		killEnvMappings (sysEnv, sId);

		markAltered (sysEnv, s);
	}

	private static final void killConfig (final SystemEnvironment sysEnv, final Long sId)
		throws SDMSException
	{
		final Vector list = SDMSScopeConfigTable.idx_sId.getVector (sysEnv, sId);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSScopeConfig sc = (SDMSScopeConfig) list.get (i);
			sc.delete (sysEnv);
		}
	}

	private static final void killEnvMappings (final SystemEnvironment sysEnv, final Long sId)
		throws SDMSException
	{
		final Vector list = SDMSScopeConfigEnvMappingTable.idx_sId.getVector (sysEnv, sId);
		final int size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSScopeConfigEnvMapping sce = (SDMSScopeConfigEnvMapping) list.get (i);
			sce.delete (sysEnv);
		}
	}

	public static final void copy (final SystemEnvironment sysEnv, final Long oldId, final Long newId)
		throws SDMSException
	{
		Vector list;
		int size;

		list = SDMSScopeConfigTable.idx_sId.getVector (sysEnv, oldId);
		size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSScopeConfig sc = (SDMSScopeConfig) list.get (i);
			SDMSScopeConfigTable.table.create (sysEnv, sc.getKey (sysEnv), sc.getValue (sysEnv), newId);
		}

		list = SDMSScopeConfigEnvMappingTable.idx_sId.getVector (sysEnv, oldId);
		size = list.size();
		for (int i = 0; i < size; ++i) {
			final SDMSScopeConfigEnvMapping sce = (SDMSScopeConfigEnvMapping) list.get (i);
			SDMSScopeConfigEnvMappingTable.table.create (sysEnv, sce.getKey (sysEnv), sce.getValue (sysEnv), newId);
		}

		markAltered (sysEnv, SDMSScopeTable.getObject (sysEnv, newId));
	}

	public static final void markAltered (final SystemEnvironment sysEnv, final SDMSScope s)
		throws SDMSException
	{
		final Vector scopeList = SDMSScopeTable.idx_parentId.getVector (sysEnv, s.getId (sysEnv));
		final int size = scopeList.size();

		for (int i = 0; i < size; ++i)
			markAltered (sysEnv, (SDMSScope) scopeList.get (i));

		if (s.getType (sysEnv).intValue() == SDMSScope.SERVER) {
			try {
				sysEnv.cEnv.pushGid (sysEnv, ADMIN_GID);
				s.setHasAlteredConfig (sysEnv, Boolean.TRUE);
				sysEnv.cEnv.popGid (sysEnv);
			} catch (Throwable t) {
				sysEnv.cEnv.popGid (sysEnv);
				throw t;
			}
		}
	}

	public static final void alter (final SystemEnvironment sysEnv, final SDMSScope s, final WithHash cfg)
		throws SDMSException
	{
		if (cfg == null)
			kill (sysEnv, s);
		else {
			final Long sId = s.getId (sysEnv);

			final Vector keyList = new Vector (cfg.keySet());
			final int size = keyList.size();
			for (int i = 0; i < size; ++i) {
				final String key = (String) keyList.get (i);
				final Object value = cfg.get (key);

				try {
					if (key.equals (Config.ENV_MAPPING)) {
						killEnvMappings (sysEnv, sId);
						if (value != null)
							createEnvMappings (sysEnv, sId, (WithHash) value);
						continue;
					}

					if (key.startsWith (Config.NAME_PATTERN) && value != null) {

						try {
							((String) value).matches((String) value);
						} catch (java.util.regex.PatternSyntaxException pse) {
							throw new CommonErrorException (new SDMSMessage (sysEnv, "03910051735", "invalid regular expression : '" +
											(String) value) + "' for rule " + key);
						}
					}

					SDMSScopeConfig sc;
					try {
						sc = (SDMSScopeConfig) SDMSScopeConfigTable.idx_scopeId_key.getUnique (sysEnv, new SDMSKey (sId, key));
					} catch (final NotFoundException e) {
						sc = null;
					}
					String sic = null;
					if (value != null) {

						sic = '=' + (String) value;
					}

					if ((sic == null && sc == null) || (sic != null && sc != null && sic.equals(sc.getValue(sysEnv))))
						continue;

					if (key.equals (Config.JOB_FILE_PREFIX)) {

						final Vector jsv = collectJobserver(sysEnv, s);
						for (int k = 0; k < jsv.size(); ++k) {
							final SDMSScope js = (SDMSScope) jsv.get(k);
							final Long jsId = js.getId(sysEnv);

							final Vector v = SDMSSubmittedEntityTable.idx_scopeId.getVector(sysEnv, jsId);
							for (int j = 0; j < v.size(); ++j) {
								final SDMSSubmittedEntity sme = (SDMSSubmittedEntity) v.get(j);
								final int state = sme.getState(sysEnv).intValue();
								if (state == SDMSSubmittedEntity.STARTING ||
								    state == SDMSSubmittedEntity.STARTED  ||
								    state == SDMSSubmittedEntity.RUNNING  ||
								    state == SDMSSubmittedEntity.BROKEN_ACTIVE ||
								    state == SDMSSubmittedEntity.TO_KILL  ||
								    state == SDMSSubmittedEntity.KILLED)
									throw new CommonErrorException (new SDMSMessage (sysEnv, "03703151451",
										"The job file prefix cannot be altered as long as there are running jobs"));
							}
						}
					}

					if (value != null) {
						if (sc != null)
							sc.setValue (sysEnv, sic);
						else
							SDMSScopeConfigTable.table.create (sysEnv, key, sic, sId);
					} else if (sc != null)
						sc.delete (sysEnv);
				} catch (final ClassCastException e) {
					throw new CommonErrorException (new SDMSMessage (sysEnv, "04307151738", "invalid " + ParseStr.S_CONFIG + ": " + key));
				}
			}
		}

		markAltered (sysEnv, s);
	}

	private static Vector collectJobserver(SystemEnvironment sysEnv, SDMSScope s)
		throws SDMSException
	{
		final Vector v = new Vector();

		if (s.getType(sysEnv).intValue() == SDMSScope.SERVER) {
			v.add(s);
		} else {
			Vector cv = SDMSScopeTable.idx_parentId.getVector(sysEnv, s.getId(sysEnv));
			for (int i = 0; i < cv.size(); ++i) {
				v.addAll(collectJobserver(sysEnv, (SDMSScope) cv.get(i)));
			}
		}

		return v;
	}

	public static final String getItem (final SystemEnvironment sysEnv, final SDMSScope s, final String item)
		throws SDMSException
	{
		if (item == null) return null;

		final HashMap config     = new HashMap();

		collect (sysEnv, s, config, null, null);

		return (String) config.get (item);
	}

	public static final SDMSOutputContainer get (final SystemEnvironment sysEnv, final SDMSScope s)
		throws SDMSException
	{
		final HashMap config     = new HashMap();
		final HashMap envMapping = new HashMap();
		final HashSet dynamic    = new HashSet();

		config.put (Config.REPO_USER, quotedPath (sysEnv, s.getId (sysEnv)));

		final String pass = s.getPasswd (sysEnv);
		if (pass != null)
			config.put (Config.REPO_PASS, pass);

		collect (sysEnv, s, config, envMapping, dynamic);

		final Vector cfgDesc = new Vector();
		final Vector cfgData = new Vector();

		final Vector keyList = new Vector (config.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String key = (String) keyList.get (i);
			cfgDesc.add (key);
			cfgData.add (config.get (key));
		}

		if (! envMapping.isEmpty()) {
			cfgDesc.add (Config.ENV_MAPPING);
			cfgData.add (envMapping);
		}

		if (! dynamic.isEmpty()) {
			cfgDesc.add (Config.DYNAMIC);
			cfgData.add (dynamic);
		}

		return new SDMSOutputContainer (sysEnv, "Jobserver Config", cfgDesc, cfgData);
	}

	private static final String quotedPath (final SystemEnvironment sysEnv, final Long sId)
		throws SDMSException
	{
		final SDMSScope s = SDMSScopeTable.getObject (sysEnv, sId);

		String path = Dump.quotedName (sId, s.getName (sysEnv));

		final Long parentId = s.getParentId (sysEnv);
		if (parentId != null)
			path = quotedPath (sysEnv, parentId) + '.' + path;

		return path;
	}

	private static final void collect (final SystemEnvironment sysEnv, final SDMSScope s, final HashMap config, final HashMap envMapping, final HashSet dynamic)
		throws SDMSException
	{

		final Long parentId = s.getParentId (sysEnv);
		if (parentId != null) {
			final SDMSScope parent = SDMSScopeTable.getObject (sysEnv, parentId);
			collect (sysEnv, parent, config, envMapping, dynamic);
		}

		final Long sId = s.getId (sysEnv);

		Vector list;
		int size;

		if (config != null) {
			list = SDMSScopeConfigTable.idx_sId.getVector (sysEnv, sId);
			size = list.size();
			for (int i = 0; i < size; ++i) {
				final SDMSScopeConfig sc = (SDMSScopeConfig) list.get (i);

				final String key = sc.getKey (sysEnv);

				final String value = sc.getValue (sysEnv).substring (1);

				if (config.containsKey (key)) {
					config.put (PREFIX_ANCEST_VALUE + key, config.get (                 key));
					config.put (PREFIX_ANCEST_SCOPE + key, config.get (PREFIX_SCOPEID + key));
				}

				config.put (                 key, value);
				config.put (PREFIX_SCOPEID + key, sId);
			}
		}

		if (envMapping != null) {
			list = SDMSScopeConfigEnvMappingTable.idx_sId.getVector (sysEnv, sId);
			size = list.size();
			for (int i = 0; i < size; ++i) {
				final SDMSScopeConfigEnvMapping sce = (SDMSScopeConfigEnvMapping) list.get (i);

				final String key = sce.getKey (sysEnv);

				final String value = sce.getValue (sysEnv).substring (1);

				if (envMapping.containsKey (key)) {
					envMapping.put (PREFIX_ANCEST_VALUE + key, envMapping.get (                 key));
					envMapping.put (PREFIX_ANCEST_SCOPE + key, envMapping.get (PREFIX_SCOPEID + key));
				}

				envMapping.put (                 key, value);
				envMapping.put (PREFIX_SCOPEID + key, sId);
			}
		}

		if (dynamic != null) {
			list = SDMSParameterDefinitionTable.idx_seId.getVector (sysEnv, sId);
			size = list.size();
			for (int i = 0; i < size; ++i) {
				final SDMSParameterDefinition pd = (SDMSParameterDefinition) list.get (i);
				final String name = pd.getName (sysEnv);

				switch (pd.getType (sysEnv).intValue()) {
				case SDMSParameterDefinition.DYNAMIC:
					dynamic.add (name);
					break;

				case SDMSParameterDefinition.DYNAMICVALUE:
					break;

				default:
					if (dynamic.contains (name))
						dynamic.remove (name);
				}
			}
		}
	}

	private ScopeConfig()
	{

	}
}
