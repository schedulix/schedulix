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

public class ListDependencyHierarchy
	extends Node
{
	public static final String __version = "@(#) $Id: ListDependencyHierarchy.java,v 2.11.2.2 2013/06/18 09:49:32 ronald Exp $";

	private final Long    smeId;
	private final HashSet expandIds;

	public ListDependencyHierarchy (Long i)
	{
		super();
		smeId     = i;
		expandIds = null;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public ListDependencyHierarchy (Long i, HashSet e)
	{
		super();
		smeId     = i;
		expandIds = e;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	private Object asTimestamp (SystemEnvironment sysEnv, final Long ts)
	{
		if (ts == null)
			return null;

		final Date d = new Date();
		d.setTime (ts.longValue());

		return sysEnv.systemDateFormat.format(d);
	}

	private Object asOwnerString (final SystemEnvironment sysEnv, final Long o)
		throws SDMSException
	{
		if (o == null)
			return null;

		final SDMSGroup g = SDMSGroupTable.getObject (sysEnv, o);
		if (g == null)
			return null;

		return g.getName (sysEnv);
	}

	private Object asScopeString (final SystemEnvironment sysEnv, final Long s)
		throws SDMSException
	{
		if (s == null)
			return null;

		final SDMSScope scope = SDMSScopeTable.getObject (sysEnv, s);
		if (scope == null)
			return null;

		return scope.pathString (sysEnv);
	}

	private Object asEsdString (final SystemEnvironment sysEnv, final Long e, final long actVersion)
		throws SDMSException
	{
		if (e == null)
			return null;

		final SDMSExitStateDefinition exitState  = SDMSExitStateDefinitionTable.getObject (sysEnv, e, actVersion);
		if (exitState == null)
			return null;

		return exitState.getName (sysEnv);
	}

	private String getSePathString (final SystemEnvironment sysEnv, final Long seId)
		throws SDMSException
	{
		if (seId == null)
			return null;

		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId);

		return se.pathString (sysEnv);
	}

	private String getSmePathString (final SystemEnvironment sysEnv, final Long smeId, long version)
		throws SDMSException
	{
		if (smeId == null)
			return null;

		Long seId;
		long actVersion = version;
		try {
			final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject (sysEnv, smeId);
			seId = sme.getSeId (sysEnv);
			actVersion = sme.getSeVersion(sysEnv).longValue();
		} catch (NotFoundException nfe) {
			seId = smeId;
		}

		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId, actVersion);

		return se.pathString (sysEnv);
	}

	private String getSmePathString (final SystemEnvironment sysEnv, final Long smeId)
		throws SDMSException
	{
		if (smeId == null)
			return null;

		final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject (sysEnv, smeId);
		final Long seId = sme.getSeId (sysEnv);
		final Long actVersion = sme.getSeVersion(sysEnv).longValue();
		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId, actVersion);

		return se.pathString (sysEnv);
	}

	private Long cntRequired (final SystemEnvironment sysEnv, final SDMSSubmittedEntity sme)
		throws SDMSException
	{
		if (sme.getState(sysEnv).intValue() != SDMSSubmittedEntity.DEPENDENCY_WAIT) {
			return new Long(0);
		}
		final Vector deps = SDMSDependencyInstanceTable.idx_dependentId.getVector (sysEnv, sme.getId (sysEnv));

		return new Long (deps == null ? 0 : deps.size());
	}

	private final HashMap depsComp_nameHash = new HashMap();

	private class depsComp
		implements Comparator
	{
		private final SystemEnvironment env;

		public depsComp (final SystemEnvironment e)
		{
			env = e;
		}

		private String getName (final SDMSDependencyInstance di)
		{
			try {
				final Long diId = di.getId (env);

				String name = (String) depsComp_nameHash.get (diId);
				if (name != null)
					return name;

				final Long ReqSmeId = di.getRequiredId (env);

				Long seId = null;
				long version;

				try {
					final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject (env, ReqSmeId);
					version = sme.getSeVersion(env).longValue();
					seId = sme.getSeId (env);
				} catch (NotFoundException nfe) {
					final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject (env, di.getDependentId (env));
					version = sme.getSeVersion(env).longValue();
					seId = ReqSmeId;
				}

				final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (env, seId, version);

				name = se.pathString (env);

				depsComp_nameHash.put (diId, name);

				return name;
			} catch (SDMSException e) {
				return new String ("");
			}
		}

		public int compare (Object o1, Object o2)
		{
			Long id1;
			Long id2;
			try {
				id1 = ((SDMSDependencyInstance) o1).getDependentIdOrig (env);
			} catch (SDMSException e) {
				id1 = null;
			}
			try {
				id2 = ((SDMSDependencyInstance) o2).getDependentIdOrig (env);
			} catch (SDMSException e) {
				id2 = null;
			}
			final int r = id1.compareTo (id2);

			if (r == 0) {
				final String path1 = getName ((SDMSDependencyInstance) o1);
				final String path2 = getName ((SDMSDependencyInstance) o2);
				return path1.compareTo (path2);
			} else
				return r;
		}

		public boolean equals (Object obj)
		{
			return obj == this;
		}
	}

	private final void render_deps (SystemEnvironment sysEnv, Long smeId, String depPath, SDMSOutputContainer table, HashSet visitedHash)
		throws SDMSException
	{
		final String empty = "";
		final Vector deps = SDMSDependencyInstanceTable.idx_dependentId.getVector (sysEnv, smeId);

		Collections.sort (deps, new depsComp (sysEnv));

		for (int i = 0; i < deps.size(); ++i) {

			final SDMSDependencyInstance di = (SDMSDependencyInstance) deps.get (i);
			final Long diDepSmeId = di.getDependentId (sysEnv);
			final Long diReqSmeId = di.getRequiredId (sysEnv);

			SDMSSubmittedEntity sme;
			try {
				sme = SDMSSubmittedEntityTable.getObject (sysEnv, diReqSmeId);
			} catch (NotFoundException nfe) {
				sme = null;
			}

			Long seId;
			long actVersion;
			Long parentId = null;
			final SDMSSubmittedEntity depSme = SDMSSubmittedEntityTable.getObject (sysEnv, diDepSmeId);

			if (sme != null) {
				parentId = sme.getParentId (sysEnv);
				actVersion = sme.getSeVersion(sysEnv).longValue();
				seId = sme.getSeId (sysEnv);
			} else {
				actVersion = depSme.getSeVersion(sysEnv).longValue();
				seId = diReqSmeId;
			}

			final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId, actVersion);

			String path = depPath + ";";
			if (sme != null)
				path = path + sme.getSubmitPathString(sysEnv);
			else
				path = path + se.pathString(sysEnv);

			final Long ddId = di.getDdId (sysEnv);

			final SDMSDependencyDefinition dd = SDMSDependencyDefinitionTable.getObject (sysEnv, ddId, actVersion);
			final Long ddDepSeId = dd.getSeDependentId (sysEnv);
			final Long ddReqSeId = dd.getSeRequiredId (sysEnv);
			final boolean visited = visitedHash.contains(seId);

			final Vector row = new Vector();

			row.add (di.getId (sysEnv));
			row.add (ddId);
			row.add (diDepSmeId);
			row.add (getSmePathString (sysEnv, diDepSmeId));
			row.add (diReqSmeId);
			row.add (getSmePathString (sysEnv, diReqSmeId, actVersion));
			row.add (di.getStateAsString (sysEnv));
			row.add (path);
			row.add (ddDepSeId);
			row.add (getSePathString (sysEnv, ddDepSeId));
			row.add (ddReqSeId);
			row.add (getSePathString (sysEnv, ddReqSeId));
			row.add (dd.getName (sysEnv));
			row.add (dd.getUnresolvedHandlingAsString (sysEnv));
			row.add (dd.getModeAsString (sysEnv));
			row.add (dd.getStateSelectionAsString (sysEnv));
			row.add (depSme.getMasterId (sysEnv));
			row.add (se.getTypeAsString (sysEnv));
			if (sme != null) {
				row.add (parentId);
				row.add (getSmePathString (sysEnv, parentId));
				row.add (asOwnerString (sysEnv, sme.getOwnerId (sysEnv)));
				row.add (asScopeString (sysEnv, sme.getScopeId (sysEnv)));
				row.add (sme.getExitCode (sysEnv));
				row.add (sme.getPid (sysEnv));
				row.add (sme.getExtPid (sysEnv));
				row.add (sme.getStateAsString (sysEnv));
				row.add (asEsdString (sysEnv, sme.getJobEsdId (sysEnv), actVersion));
				row.add (asEsdString (sysEnv, sme.getFinalEsdId (sysEnv), actVersion));
				row.add (sme.getJobIsFinal (sysEnv));
				row.add (visited ? new Long(0) : cntRequired (sysEnv, sme));
				row.add (sme.getCntRestartable (sysEnv));
				row.add (sme.getCntSubmitted (sysEnv));
				row.add (sme.getCntDependencyWait (sysEnv));
				row.add (sme.getCntResourceWait (sysEnv));
				row.add (sme.getCntRunnable (sysEnv));
				row.add (sme.getCntStarting (sysEnv));
				row.add (sme.getCntStarted (sysEnv));
				row.add (sme.getCntRunning (sysEnv));
				row.add (sme.getCntToKill (sysEnv));
				row.add (sme.getCntKilled (sysEnv));
				row.add (sme.getCntCancelled (sysEnv));
				row.add (sme.getCntFinal (sysEnv));
				row.add (sme.getCntBrokenActive (sysEnv));
				row.add (sme.getCntBrokenFinished (sysEnv));
				row.add (sme.getCntError (sysEnv));
				row.add (sme.getCntSynchronizeWait (sysEnv));
				row.add (sme.getCntFinished (sysEnv));
				row.add (asTimestamp (sysEnv, sme.getSubmitTs (sysEnv)));
				row.add (asTimestamp (sysEnv, sme.getSyncTs (sysEnv)));
				row.add (asTimestamp (sysEnv, sme.getResourceTs (sysEnv)));
				row.add (asTimestamp (sysEnv, sme.getRunnableTs (sysEnv)));
				row.add (asTimestamp (sysEnv, sme.getStartTs (sysEnv)));
				row.add (asTimestamp (sysEnv, sme.getFinishTs (sysEnv)));
				row.add (asTimestamp (sysEnv, sme.getFinalTs (sysEnv)));
				row.add (sme.getErrorMsg (sysEnv));
				row.add (di.getDependentIdOrig (sysEnv));
				row.add (di.getDependencyOperationAsString (sysEnv));
				row.add (sme.getChildTag (sysEnv));

				Vector c = SDMSHierarchyInstanceTable.idx_parentId.getVector (sysEnv, sme.getId (sysEnv));
				row.add (new Integer (c.size()));

				c = SDMSDependencyInstanceTable.idx_dependentId.getVector (sysEnv, sme.getId (sysEnv));
				row.add (new Integer (c.size()));

				final Vector dds_v = SDMSDependencyStateTable.idx_ddId.getVector (sysEnv, dd.getId (sysEnv), actVersion);
				String sep = "";
				final StringBuffer states = new StringBuffer();
				for (int j = 0; j < dds_v.size(); ++j) {
					final SDMSDependencyState dds = (SDMSDependencyState) dds_v.get (j);
					final SDMSExitStateDefinition esd = SDMSExitStateDefinitionTable.getObject (sysEnv, dds.getEsdId (sysEnv), actVersion);
					final String esdn = esd.getName (sysEnv);
					states.append (sep);
					states.append (esdn);
					sep = ",";
				}
				row.add (new String (states));

				row.add (sme.getIsSuspendedAsString (sysEnv));
				row.add (sme.getParentSuspended (sysEnv));
				row.add (sme.getCntUnreachable (sysEnv));

				final SDMSSubmittedEntity sme_orig = SDMSSubmittedEntityTable.getObject (sysEnv, di.getDependentIdOrig (sysEnv));
				row.add (sme_orig.getSubmitPathString (sysEnv));

				row.add (di.getIgnoreAsString (sysEnv));
			} else {
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (new Long(0));
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (empty);
				row.add (di.getDependentIdOrig (sysEnv));
				row.add (di.getDependencyOperationAsString (sysEnv));
				row.add (empty);

				row.add (new Integer (0));

				row.add (new Integer (0));

				final Vector dds_v = SDMSDependencyStateTable.idx_ddId.getVector (sysEnv, dd.getId (sysEnv), actVersion);
				String sep = "";
				final StringBuffer states = new StringBuffer();
				for (int j = 0; j < dds_v.size(); ++j) {
					final SDMSDependencyState dds = (SDMSDependencyState) dds_v.get (j);
					final SDMSExitStateDefinition esd = SDMSExitStateDefinitionTable.getObject (sysEnv, dds.getEsdId (sysEnv), actVersion);
					final String esdn = esd.getName (sysEnv);
					states.append (sep);
					states.append (esdn);
					sep = ",";
				}
				row.add (new String (states));

				row.add (empty);
				row.add (empty);
				row.add (empty);

				final SDMSSubmittedEntity sme_orig = SDMSSubmittedEntityTable.getObject (sysEnv, di.getDependentIdOrig (sysEnv));
				row.add (sme_orig.getSubmitPathString (sysEnv));

				row.add (di.getIgnoreAsString (sysEnv));
			}
			row.add(dd.getResolveModeAsString(sysEnv));
			row.add(dd.getExpiredAmount(sysEnv));
			row.add(dd.getExpiredBaseAsString(sysEnv));
			row.add(dd.getSelectCondition(sysEnv));

			table.addData (sysEnv, row);

			if (sme != null) {
				if (expandIds == null || expandIds.contains (diReqSmeId)) {
					if (!visited && sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.DEPENDENCY_WAIT) {
						visitedHash.add(diReqSmeId);
						render_deps (sysEnv, diReqSmeId, path, table, visitedHash);
					}
				}
			}
		}
	}

	public void go (SystemEnvironment sysEnv)
		throws SDMSException
	{
		final String empty = "";

		final Vector desc = new Vector();

		desc.add ("ID");
		desc.add ("DD_ID");
		desc.add ("DEPENDENT_ID");
		desc.add ("DEPENDENT_NAME");
		desc.add ("REQUIRED_ID");
		desc.add ("REQUIRED_NAME");
		desc.add ("DEP_STATE");
		desc.add ("DEPENDENCY_PATH");
		desc.add ("SE_DEPENDENT_ID");
		desc.add ("SE_DEPENDENT_NAME");
		desc.add ("SE_REQUIRED_ID");
		desc.add ("SE_REQUIRED_NAME");
		desc.add ("DD_NAME");
		desc.add ("UNRESOLVED_HANDLING");
		desc.add ("MODE");
		desc.add("STATE_SELECTION");
		desc.add ("MASTER_ID");
		desc.add ("SE_TYPE");
		desc.add ("PARENT_ID");
		desc.add ("PARENT_NAME");
		desc.add ("OWNER");
		desc.add ("SCOPE");
		desc.add ("EXIT_CODE");
		desc.add ("PID");
		desc.add ("EXTPID");
		desc.add ("JOB_STATE");
		desc.add ("JOB_ESD");
		desc.add ("FINAL_ESD");
		desc.add ("JOB_IS_FINAL");
		desc.add ("CNT_REQUIRED");
		desc.add ("CNT_RESTARTABLE");
		desc.add ("CNT_SUBMITTED");
		desc.add ("CNT_DEPENDENCY_WAIT");
		desc.add ("CNT_RESOURCE_WAIT");
		desc.add ("CNT_RUNNABLE");
		desc.add ("CNT_STARTING");
		desc.add ("CNT_STARTED");
		desc.add ("CNT_RUNNING");
		desc.add ("CNT_TO_KILL");
		desc.add ("CNT_KILLED");
		desc.add ("CNT_CANCELLED");
		desc.add ("CNT_FINAL");
		desc.add ("CNT_BROKEN_ACTIVE");
		desc.add ("CNT_BROKEN_FINISHED");
		desc.add ("CNT_ERROR");
		desc.add ("CNT_SYNCHRONIZE_WAIT");
		desc.add ("CNT_FINISHED");
		desc.add ("SUBMIT_TS");
		desc.add ("SYNC_TS");
		desc.add ("RESOURCE_TS");
		desc.add ("RUNNABLE_TS");
		desc.add ("START_TS");
		desc.add ("FINSH_TS");
		desc.add ("FINAL_TS");
		desc.add ("ERROR_MSG");
		desc.add ("DEPENDENT_ID_ORIG");
		desc.add ("DEPENDENCY_OPERATION");
		desc.add ("CHILD_TAG");
		desc.add ("CHILDREN");
		desc.add ("REQUIRED");
		desc.add ("DD_STATES");
		desc.add ("IS_SUSPENDED");
		desc.add ("PARENT_SUSPENDED");
		desc.add ("CNT_UNREACHABLE");
		desc.add ("DEPENDENT_PATH_ORIG");
		desc.add ("IGNORE");

		desc.add("RESOLVE_MODE");
		desc.add("EXPIRED_AMOUNT");
		desc.add("EXPIRED_BASE");
		desc.add("SELECT_CONDITION");

		final SDMSOutputContainer table = new SDMSOutputContainer (sysEnv, "Dependency Hierarchy", desc);

		final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject (sysEnv, smeId);
		final Long parentId = sme.getParentId (sysEnv);

		final long actVersion = sme.getSeVersion(sysEnv).longValue();

		final Long seId = sme.getSeId (sysEnv);

		final SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject (sysEnv, seId, actVersion);

		String sePath = sme.getSubmitPathString (sysEnv);

		HashSet visitedHash = new HashSet();

		final Vector row = new Vector();

		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (sePath);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (empty);
		row.add (sme.getMasterId (sysEnv));
		row.add (se.getTypeAsString (sysEnv));
		row.add (parentId);
		row.add (getSmePathString (sysEnv, parentId));
		row.add (asOwnerString (sysEnv, sme.getOwnerId (sysEnv)));
		row.add (asScopeString (sysEnv, sme.getScopeId (sysEnv)));
		row.add (sme.getExitCode (sysEnv));
		row.add (sme.getPid (sysEnv));
		row.add (sme.getExtPid (sysEnv));
		row.add (sme.getStateAsString (sysEnv));
		row.add (asEsdString (sysEnv, sme.getJobEsdId (sysEnv), actVersion));
		row.add (asEsdString (sysEnv, sme.getFinalEsdId (sysEnv), actVersion));
		row.add (sme.getJobIsFinal (sysEnv));
		row.add (cntRequired (sysEnv, sme));
		row.add (sme.getCntRestartable (sysEnv));
		row.add (sme.getCntSubmitted (sysEnv));
		row.add (sme.getCntDependencyWait (sysEnv));
		row.add (sme.getCntResourceWait (sysEnv));
		row.add (sme.getCntRunnable (sysEnv));
		row.add (sme.getCntStarting (sysEnv));
		row.add (sme.getCntStarted (sysEnv));
		row.add (sme.getCntRunning (sysEnv));
		row.add (sme.getCntToKill (sysEnv));
		row.add (sme.getCntKilled (sysEnv));
		row.add (sme.getCntCancelled (sysEnv));
		row.add (sme.getCntFinal (sysEnv));
		row.add (sme.getCntBrokenActive (sysEnv));
		row.add (sme.getCntBrokenFinished (sysEnv));
		row.add (sme.getCntError (sysEnv));
		row.add (sme.getCntSynchronizeWait (sysEnv));
		row.add (sme.getCntFinished (sysEnv));
		row.add (asTimestamp (sysEnv, sme.getSubmitTs (sysEnv)));
		row.add (asTimestamp (sysEnv, sme.getSyncTs (sysEnv)));
		row.add (asTimestamp (sysEnv, sme.getResourceTs (sysEnv)));
		row.add (asTimestamp (sysEnv, sme.getRunnableTs (sysEnv)));
		row.add (asTimestamp (sysEnv, sme.getStartTs (sysEnv)));
		row.add (asTimestamp (sysEnv, sme.getFinishTs (sysEnv)));
		row.add (asTimestamp (sysEnv, sme.getFinalTs (sysEnv)));
		row.add (sme.getErrorMsg (sysEnv));
		row.add (empty);
		row.add (empty);
		row.add (sme.getChildTag (sysEnv));

		Vector c = SDMSHierarchyInstanceTable.idx_parentId.getVector (sysEnv, sme.getId (sysEnv));
		row.add (new Integer (c.size()));

		c = SDMSDependencyInstanceTable.idx_dependentId.getVector (sysEnv, sme.getId (sysEnv));
		row.add (new Integer (c.size()));
		row.add (empty);
		row.add (sme.getIsSuspendedAsString (sysEnv));
		row.add (sme.getParentSuspended (sysEnv));
		row.add (sme.getCntUnreachable (sysEnv));
		row.add (empty);
		row.add (empty);

		row.add(empty);
		row.add(empty);
		row.add(empty);
		row.add(empty);

		table.addData (sysEnv, row);

		if (expandIds == null || expandIds.size() > 0) {
			visitedHash.add(smeId);
			render_deps (sysEnv, smeId, sePath, table, visitedHash);
		}
		result.setOutputContainer (table);

		result.setFeedback (new SDMSMessage (sysEnv, "04205142204", "$1 Dependencies found", new Integer (table.lines)));
	}
}
