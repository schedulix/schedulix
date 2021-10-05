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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ShowFolder extends ShowCommented
{

	ObjectURL url;

	public ShowFolder(ObjectURL u)
	{
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		url = u;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSOutputContainer d_container = null;

		SDMSProxy p = url.resolve(sysEnv);

		if(url.objType.intValue() == SDMSObjectComment.FOLDER) {
			d_container = showFolder(sysEnv, (SDMSFolder) p);
		} else {
			d_container = showSchedulingEntity(sysEnv, (SDMSSchedulingEntity) p);
		}

		result.setOutputContainer(d_container);

		result.setFeedback( new SDMSMessage(sysEnv, "03201292028", "Folder shown"));

	}

	private SDMSOutputContainer showFolder(SystemEnvironment sysEnv, SDMSFolder f)
		throws SDMSException
	{

		Long id;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		if(!f.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411709", "Insufficient privileges"));

		desc.add("ID");
		desc.add("NAME");
		desc.add("OWNER");
		desc.add("TYPE");
		desc.add("ENVIRONMENT");
		desc.add("INHERIT_PRIVS");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");
		desc.add("PARAMETERS");
		desc.add("DEFINED_RESOURCES");

		id = f.getId(sysEnv);
		Vector v = new Vector();
		v.add(id);
		v.add(f.pathVector(sysEnv));
		SDMSGroup g = SDMSGroupTable.getObject(sysEnv, f.getOwnerId(sysEnv));
		v.add(g.getName(sysEnv));
		v.add("FOLDER");
		Long envId = f.getEnvId(sysEnv);
		if(envId != null) {
			v.add(SDMSNamedEnvironmentTable.getObject(sysEnv, envId).getName(sysEnv));
		} else {
			v.add(null);
		}
		v.add(new SDMSPrivilege(sysEnv, f.getInheritPrivs(sysEnv).longValue()).toString());
		v.add(getCommentContainer(sysEnv, id));
		v.add(getCommentInfoType(sysEnv, id));

		final Date d = new Date();
		try {
			v.add(SDMSUserTable.getObject(sysEnv, f.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			v.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(f.getCreateTs(sysEnv).longValue());
		v.add(sysEnv.systemDateFormat.format(d));
		try {
			v.add(SDMSUserTable.getObject(sysEnv, f.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			v.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(f.getChangeTs(sysEnv).longValue());
		v.add(sysEnv.systemDateFormat.format(d));
		v.add(f.getPrivileges(sysEnv).toString());

		v.add(ManipParameters.get (sysEnv, f.getId (sysEnv)));
		add_resources(sysEnv, f, v);

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03112201803", "Folder"), desc, v);

		return d_container;
	}

	private SDMSOutputContainer showSchedulingEntity(SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		SDMSOutputContainer d_container;
		Vector desc = new Vector();
		Vector data = new Vector();

		desc.add("ID");
		desc.add("NAME");
		desc.add("OWNER");
		desc.add("TYPE");
		desc.add("INHERIT_PRIVS");
		desc.add("RUN_PROGRAM");
		desc.add("RERUN_PROGRAM");
		desc.add("KILL_PROGRAM");
		desc.add("WORKDIR");
		desc.add("LOGFILE");
		desc.add("TRUNC_LOG");
		desc.add("ERRLOGFILE");
		desc.add("TRUNC_ERRLOG");
		desc.add("EXPECTED_RUNTIME");
		desc.add("EXPECTED_FINALTIME");
		desc.add("PRIORITY");
		desc.add("MIN_PRIORITY");
		desc.add("AGING_AMOUNT");
		desc.add("AGING_BASE");
		desc.add("SUBMIT_SUSPENDED");
		desc.add("RESUME_AT");
		desc.add("RESUME_IN");
		desc.add("RESUME_BASE");
		desc.add("MASTER_SUBMITTABLE");
		desc.add("TIMEOUT_AMOUNT");
		desc.add("TIMEOUT_BASE");
		desc.add("TIMEOUT_STATE");
		desc.add("DEPENDENCY_MODE");
		desc.add("ESP_NAME");
		desc.add("ESM_NAME");
		desc.add("ENV_NAME");

		desc.add("CANCEL_LEAD_FLAG");
		desc.add("CANCEL_APPROVAL");
		desc.add("RERUN_LEAD_FLAG");
		desc.add("RERUN_APPROVAL");
		desc.add("ENABLE_LEAD_FLAG");
		desc.add("ENABLE_APPROVAL");
		desc.add("SET_STATE_LEAD_FLAG");
		desc.add("SET_STATE_APPROVAL");
		desc.add("IGN_DEPENDENCY_LEAD_FLAG");
		desc.add("IGN_DEPENDENCY_APPROVAL");
		desc.add("IGN_RESOURCE_LEAD_FLAG");
		desc.add("IGN_RESOURCE_APPROVAL");
		desc.add("CLONE_LEAD_FLAG");
		desc.add("CLONE_APPROVAL");
		desc.add("SUSPEND_LEAD_FLAG");
		desc.add("SUSPEND_APPROVAL");
		desc.add("CLEAR_WARN_LEAD_FLAG");
		desc.add("CLEAR_WARN_APPROVAL");
		desc.add("PRIORITY_LEAD_FLAG");
		desc.add("PRIORITY_APPROVAL");
		desc.add("EDIT_PARAMETER_LEAD_FLAG");
		desc.add("EDIT_PARAMETER_APPROVAL");
		desc.add("KILL_LEAD_FLAG");
		desc.add("KILL_APPROVAL");
		desc.add("SET_JOB_STATE_LEAD_FLAG");
		desc.add("SET_JOB_STATE_APPROVAL");

		desc.add("FP_NAME");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("PRIVS");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("CHILDREN");
		desc.add("PARENTS");
		desc.add("PARAMETER");
		desc.add("REFERENCES");
		desc.add("REQUIRED_JOBS");
		desc.add("DEPENDENT_JOBS");
		desc.add("REQUIRED_RESOURCES");
		desc.add("DEFINED_RESOURCES");

		if(!se.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "034020411708", "Insufficient privileges"));
		Long seId = se.getId(sysEnv);

		Long esmpId;
		SDMSExitStateProfile esp;
		Long espId;
		SDMSExitStateMappingProfile esmp;
		Long neId;
		SDMSNamedEnvironment ne;
		Long fpId;
		SDMSFootprint fp;
		SDMSGroup group;

		data.add(seId);
		data.add(se.pathVector(sysEnv));
		group = SDMSGroupTable.getObject(sysEnv, se.getOwnerId(sysEnv));
		data.add(group.getName(sysEnv));
		data.add(se.getTypeAsString(sysEnv));
		data.add(new SDMSPrivilege(sysEnv, se.getInheritPrivs(sysEnv).longValue()).toString());
		data.add(se.getRunProgram(sysEnv));
		data.add(se.getRerunProgram(sysEnv));
		data.add(se.getKillProgram(sysEnv));
		data.add(se.getWorkdir(sysEnv));
		data.add(se.getLogfile(sysEnv));
		data.add(se.getTruncLog(sysEnv));
		data.add(se.getErrlogfile(sysEnv));
		data.add(se.getTruncErrlog(sysEnv));
		data.add(se.getExpectedRuntime(sysEnv));
		data.add(se.getExpectedFinaltime(sysEnv));
		data.add(se.getPriority(sysEnv));
		data.add(se.getMinPriority(sysEnv));
		data.add(se.getAgingAmount(sysEnv));
		data.add(se.getAgingBaseAsString(sysEnv));
		data.add(se.getSubmitSuspended(sysEnv));
		data.add(se.getResumeAt(sysEnv));
		data.add(se.getResumeIn(sysEnv));
		data.add(se.getResumeBaseAsString(sysEnv));
		data.add(se.getMasterSubmittable(sysEnv));
		data.add(se.getTimeoutAmount(sysEnv));
		data.add(se.getTimeoutBaseAsString(sysEnv));
		Long esdId = se.getTimeoutStateId(sysEnv);
		if(esdId != null) {
			data.add(SDMSExitStateDefinitionTable.getObject(sysEnv, esdId).getName(sysEnv));
		} else {
			data.add(null);
		}
		data.add(se.getDependencyOperationAsString(sysEnv));
		espId = se.getEspId(sysEnv);
		esp = SDMSExitStateProfileTable.getObject(sysEnv, espId);
		data.add(esp.getName(sysEnv));
		esmpId = se.getEsmpId(sysEnv);
		if (esmpId != null) {
			esmp = SDMSExitStateMappingProfileTable.getObject(sysEnv, esmpId);
			data.add(esmp.getName(sysEnv));
		} else {
			data.add(SystemEnvironment.defaultString);
		}

		neId = se.getNeId(sysEnv);
		if (neId != null) {
			ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId);
			data.add(ne.getName(sysEnv));
		} else {
			data.add(SystemEnvironment.nullString);
		}

		data.add(se.getCancelLeadFlag(sysEnv));
		data.add(se.getCancelApprovalAsString(sysEnv));
		data.add(se.getRerunLeadFlag(sysEnv));
		data.add(se.getRerunApprovalAsString(sysEnv));
		data.add(se.getEnableLeadFlag(sysEnv));
		data.add(se.getEnableApprovalAsString(sysEnv));
		data.add(se.getSetStateLeadFlag(sysEnv));
		data.add(se.getSetStateApprovalAsString(sysEnv));
		data.add(se.getIgnDepLeadFlag(sysEnv));
		data.add(se.getIgnDepApprovalAsString(sysEnv));
		data.add(se.getIgnRssLeadFlag(sysEnv));
		data.add(se.getIgnRssApprovalAsString(sysEnv));
		data.add(se.getCloneLeadFlag(sysEnv));
		data.add(se.getCloneApprovalAsString(sysEnv));
		data.add(se.getSuspendLeadFlag(sysEnv));
		data.add(se.getSuspendApprovalAsString(sysEnv));
		data.add(se.getClrWarnLeadFlag(sysEnv));
		data.add(se.getClrWarnApprovalAsString(sysEnv));
		data.add(se.getPriorityLeadFlag(sysEnv));
		data.add(se.getPriorityApprovalAsString(sysEnv));
		data.add(se.getEditParmLeadFlag(sysEnv));
		data.add(se.getEditParmApprovalAsString(sysEnv));
		data.add(se.getKillLeadFlag(sysEnv));
		data.add(se.getKillApprovalAsString(sysEnv));
		data.add(se.getSetJobStateLeadFlag(sysEnv));
		data.add(se.getSetJobStateApprovalAsString(sysEnv));

		fpId = se.getFpId(sysEnv);
		if (fpId != null) {
			fp = SDMSFootprintTable.getObject(sysEnv, fpId);
			data.add(fp.getName(sysEnv));
		} else {
			data.add(SystemEnvironment.nullString);
		}
		data.add(getCommentContainer(sysEnv, seId));
		data.add(getCommentInfoType(sysEnv, seId));
		data.add(se.getPrivileges(sysEnv).toString());

		final Date d = new Date();
		try {
			data.add(SDMSUserTable.getObject(sysEnv, se.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(se.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, se.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(se.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));

		data.add(childContainer(sysEnv, se));
		data.add(parentContainer(sysEnv, se));
		data.add(ManipParameters.get (sysEnv, se.getId (sysEnv)));
		data.add(ManipParameters.getReferences (sysEnv, se.getId (sysEnv)));
		data.add(dependentJobContainer(sysEnv, se));
		data.add(requiredJobContainer(sysEnv, se));
		data.add(requirementsContainer(sysEnv, se));
		add_resources(sysEnv, se, data);

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage (sysEnv, "02112141349", "Job Definition"), desc, data);

		return d_container;
	}

	private	SDMSOutputContainer childContainer (SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		Vector sh_v = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, se.getId(sysEnv));
		return familyContainer(sysEnv, sh_v);
	}

	private	SDMSOutputContainer parentContainer (SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		Vector sh_v = SDMSSchedulingHierarchyTable.idx_seChildId.getVector(sysEnv, se.getId(sysEnv));
		return familyContainer(sysEnv, sh_v);
	}

	private	SDMSOutputContainer familyContainer (SystemEnvironment sysEnv, Vector sh_v)
		throws SDMSException
	{
		Vector c_desc = new Vector();
		c_desc.add("ID");
		c_desc.add("CHILD_ID");
		c_desc.add("CHILDNAME");
		c_desc.add("CHILDTYPE");
		c_desc.add("CHILDPRIVS");
		c_desc.add("PARENT_ID");
		c_desc.add("PARENTNAME");
		c_desc.add("PARENTTYPE");
		c_desc.add("PARENTPRIVS");
		c_desc.add("ALIAS_NAME");
		c_desc.add("IS_STATIC");
		c_desc.add("IS_DISABLED");
		c_desc.add("INT_NAME");
		c_desc.add("ENABLE_CONDITION");
		c_desc.add("ENABLE_MODE");
		c_desc.add("PRIORITY");
		c_desc.add("SUSPEND");
		c_desc.add("RESUME_AT");
		c_desc.add("RESUME_IN");
		c_desc.add("RESUME_BASE");
		c_desc.add("MERGE_MODE");
		c_desc.add("EST_NAME");
		c_desc.add("IGNORED_DEPENDENCIES");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		SDMSSchedulingHierarchy sh;
		SDMSSchedulingEntity cse;
		Long estpId;
		SDMSExitStateTranslationProfile estp;
		SDMSInterval iv;
		Long intId;

		Vector c_data;
		Iterator i = sh_v.iterator();
		while (i.hasNext()) {
			sh = (SDMSSchedulingHierarchy)(i.next());
			c_data = new Vector();
			c_data.add(sh.getId(sysEnv));

			cse = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeChildId(sysEnv));
			c_data.add(sh.getSeChildId(sysEnv));
			c_data.add(cse.pathVector(sysEnv));
			c_data.add(cse.getTypeAsString(sysEnv));
			c_data.add(cse.getPrivileges(sysEnv).toString());
			cse = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeParentId(sysEnv));
			c_data.add(sh.getSeParentId(sysEnv));
			c_data.add(cse.pathVector(sysEnv));
			c_data.add(cse.getTypeAsString(sysEnv));
			c_data.add(cse.getPrivileges(sysEnv).toString());
			c_data.add(sh.getAliasName(sysEnv));
			c_data.add(sh.getIsStatic(sysEnv));
			c_data.add(sh.getIsDisabled(sysEnv));
			intId = sh.getIntId(sysEnv);
			if (intId != null) {
				iv = SDMSIntervalTable.getObject(sysEnv, intId);
				c_data.add(iv.getName(sysEnv));
			} else {
				c_data.add(null);
			}
			c_data.add(sh.getEnableCondition(sysEnv));
			c_data.add(sh.getEnableModeAsString(sysEnv));
			c_data.add(sh.getPriority(sysEnv));
			c_data.add(sh.getSuspendAsString(sysEnv));
			c_data.add(sh.getResumeAt(sysEnv));
			c_data.add(sh.getResumeIn(sysEnv));
			c_data.add(sh.getResumeBaseAsString(sysEnv));
			c_data.add(sh.getMergeModeAsString(sysEnv));
			estpId = sh.getEstpId(sysEnv);
			if (estpId != null) {
				estp = SDMSExitStateTranslationProfileTable.getObject(sysEnv, estpId);
				c_data.add(estp.getName(sysEnv));
			} else {
				c_data.add(null);
			}
			Vector ids_v = SDMSIgnoredDependencyTable.idx_shId.getVector(sysEnv, sh.getId(sysEnv));
			String sep = "";
			StringBuffer deps = new StringBuffer();
			for(int j = 0; j < ids_v.size(); j++) {
				SDMSIgnoredDependency ids = (SDMSIgnoredDependency) ids_v.get(j);
				deps.append(sep);
				deps.append(ids.getDdName(sysEnv));
				sep = ",";
			}
			c_data.add(new String(deps));

			c_container.addData(sysEnv, c_data);
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 1));

		return c_container;
	}

	private	SDMSOutputContainer dependentJobContainer (SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		Vector dd_v = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, se.getId(sysEnv));
		return dependentContainer(sysEnv, dd_v);
	}

	private	SDMSOutputContainer requiredJobContainer (SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		Vector dd_v = SDMSDependencyDefinitionTable.idx_seRequiredId.getVector(sysEnv, se.getId(sysEnv));
		return dependentContainer(sysEnv, dd_v);
	}

	private	SDMSOutputContainer dependentContainer (SystemEnvironment sysEnv, Vector dd_v)
		throws SDMSException
	{
		SDMSDependencyDefinition dd;

		Vector c_desc = new Vector();
		c_desc.add("ID");
		c_desc.add("NAME");
		c_desc.add("DEPENDENT_ID");
		c_desc.add("DEPENDENTNAME");
		c_desc.add("DEPENDENTTYPE");
		c_desc.add("DEPENDENTPRIVS");
		c_desc.add("REQUIRED_ID");
		c_desc.add("REQUIREDNAME");
		c_desc.add("REQUIREDTYPE");
		c_desc.add("REQUIREDPRIVS");
		c_desc.add("UNRESOLVED_HANDLING");
		c_desc.add("MODE");
		c_desc.add("STATE_SELECTION");
		c_desc.add("CONDITION");
		c_desc.add("STATES");

		c_desc.add("RESOLVE_MODE");
		c_desc.add("EXPIRED_AMOUNT");
		c_desc.add("EXPIRED_BASE");
		c_desc.add("SELECT_CONDITION");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		SDMSSchedulingEntity se;

		Vector c_data;
		for (int i = 0; i < dd_v.size(); i++) {
			dd = (SDMSDependencyDefinition)(dd_v.get(i));
			c_data = new Vector();
			c_data.add(dd.getId(sysEnv));
			c_data.add(dd.getName(sysEnv));
			se = SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeDependentId(sysEnv));
			c_data.add(dd.getSeDependentId(sysEnv));
			c_data.add(se.pathVector(sysEnv));
			c_data.add(se.getTypeAsString(sysEnv));
			c_data.add(se.getPrivileges(sysEnv).toString());
			se = SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeRequiredId(sysEnv));
			c_data.add(dd.getSeRequiredId(sysEnv));
			c_data.add(se.pathVector(sysEnv));
			c_data.add(se.getTypeAsString(sysEnv));
			c_data.add(se.getPrivileges(sysEnv).toString());
			c_data.add(dd.getUnresolvedHandlingAsString(sysEnv));
			c_data.add(dd.getModeAsString(sysEnv));
			c_data.add(dd.getStateSelectionAsString(sysEnv));
			c_data.add(dd.getCondition(sysEnv));
			Vector dds_v = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, dd.getId(sysEnv));
			String sep = "";
			StringBuffer states = new StringBuffer();
			for(int j = 0; j < dds_v.size(); j++) {
				final SDMSDependencyState dds = (SDMSDependencyState) dds_v.get(j);
				final String esdn = SDMSExitStateDefinitionTable.getObject(sysEnv, dds.getEsdId(sysEnv)).getName(sysEnv);
				states.append(sep);
				states.append(esdn);
				final String condition = dds.getCondition(sysEnv);
				if(condition != null) {
					states.append(":");
					states.append(condition);
				}
				sep = ",";
			}
			c_data.add(new String(states));

			c_data.add(dd.getResolveModeAsString(sysEnv));
			c_data.add(dd.getExpiredAmount(sysEnv));
			c_data.add(dd.getExpiredBaseAsString(sysEnv));
			c_data.add(dd.getSelectCondition(sysEnv));

			c_container.addData(sysEnv, c_data);
		}
		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 1));

		return c_container;
	}

	private	SDMSOutputContainer requirementsContainer (SystemEnvironment sysEnv, SDMSSchedulingEntity se)
		throws SDMSException
	{
		SDMSResourceRequirement rr;
		SDMSEnvironment e;
		HashSet nrset = new HashSet();

		Vector c_desc = new Vector();
		c_desc.add("ID");
		c_desc.add("RESOURCE_ID");
		c_desc.add("RESOURCE_NAME");
		c_desc.add("RESOURCE_USAGE");
		c_desc.add("RESOURCE_PRIVS");
		c_desc.add("AMOUNT");
		c_desc.add("KEEP_MODE");
		c_desc.add("IS_STICKY");
		c_desc.add("STICKY_NAME");
		c_desc.add("STICKY_PARENT");
		c_desc.add("RESOURCE_STATE_MAPPING");
		c_desc.add("EXPIRED_AMOUNT");
		c_desc.add("EXPIRED_BASE");
		c_desc.add("IGNORE_ON_RERUN");
		c_desc.add("LOCKMODE");
		c_desc.add("STATES");
		c_desc.add("DEFINITION");
		c_desc.add("ORIGIN");
		c_desc.add("CONDITION");

		SDMSOutputContainer c_container = new SDMSOutputContainer(sysEnv, null, c_desc);

		Long neId = se.getNeId(sysEnv);
		Vector env_v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, neId);
		SDMSNamedEnvironment ne;
		String neName = null;
		if(neId != null) {
			ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId);
			neName = ne.getName(sysEnv);
		}
		Long fpId = se.getFpId(sysEnv);
		Vector fprr_v = null;
		String fpName = null;
		if(fpId != null) {
			fprr_v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, fpId);
			final SDMSFootprint fp = SDMSFootprintTable.getObject(sysEnv, fpId);
			fpName = fp.getName(sysEnv);
		} else  {
			fprr_v = new Vector();
		}
		Vector rr_v = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, se.getId(sysEnv));

		Vector c_data;
		for(int i = 0; i < rr_v.size(); i++) {
			rr = (SDMSResourceRequirement)(rr_v.get(i));
			c_data = new Vector();
			render_rr(sysEnv, c_data, rr, "REQUIREMENT", null);
			nrset.add(rr.getNrId(sysEnv));

			c_container.addData(sysEnv, c_data);
		}
		for(int i = 0; i < fprr_v.size(); i++) {
			rr = (SDMSResourceRequirement)(fprr_v.get(i));
			if(!nrset.contains(rr.getNrId(sysEnv))) {
				c_data = new Vector();
				render_rr(sysEnv, c_data, rr, "FOOTPRINT", fpName);
				nrset.add(rr.getNrId(sysEnv));

				c_container.addData(sysEnv, c_data);
			}
		}
		for(int i = 0; i < env_v.size(); i++) {
			e = (SDMSEnvironment)(env_v.get(i));
			if(!nrset.contains(e.getNrId(sysEnv))) {
				c_data = new Vector();
				render_env(sysEnv, c_data, e, neName);
				nrset.add(e.getNrId(sysEnv));

				c_container.addData(sysEnv, c_data);
			}
		}
		Long folderId = se.getFolderId(sysEnv);
		while (folderId != null) {
			SDMSFolder f = SDMSFolderTable.getObject(sysEnv, folderId);
			neId = f.getEnvId(sysEnv);
			if(neId != null) {
				String fName = f.pathString(sysEnv);
				env_v = SDMSEnvironmentTable.idx_neId.getVector(sysEnv, neId);
				for(int i = 0; i < env_v.size(); i++) {
					ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId);
					neName = ne.getName(sysEnv);
					e = (SDMSEnvironment)(env_v.get(i));
					if(!nrset.contains(e.getNrId(sysEnv))) {
						c_data = new Vector();
						render_fenv(sysEnv, c_data, e, neName + "/" + fName);
						nrset.add(e.getNrId(sysEnv));

						c_container.addData(sysEnv, c_data);
					}
				}
			}
			folderId = f.getParentId(sysEnv);
		}

		Collections.sort(c_container.dataset, c_container.getComparator(sysEnv, 2));

		return c_container;
	}

	private void render_rr(SystemEnvironment sysEnv, Vector c_data, SDMSResourceRequirement rr, String source, String sourceName)
		throws SDMSException
	{
		SDMSResourceStateMappingProfile rsmp;

		c_data.add(rr.getId(sysEnv));
		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, rr.getNrId(sysEnv));
		c_data.add(rr.getNrId(sysEnv));
		c_data.add(nr.pathVector(sysEnv));
		c_data.add(nr.getUsageAsString(sysEnv));
		c_data.add(nr.getPrivileges(sysEnv).toString());
		c_data.add(rr.getAmount(sysEnv));
		c_data.add(rr.getKeepModeAsString(sysEnv));
		c_data.add(rr.getIsSticky(sysEnv));
		c_data.add(rr.getStickyName(sysEnv));
		Long spId = rr.getStickyParent(sysEnv);
		if (spId == null)
			c_data.add(null);
		else {
			SDMSSchedulingEntity spse = SDMSSchedulingEntityTable.getObject(sysEnv, spId);
			c_data.add(spse.pathString(sysEnv));
		}
		Long rsmpId = rr.getRsmpId(sysEnv);
		if(rsmpId != null) {
			rsmp = SDMSResourceStateMappingProfileTable.getObject(sysEnv, rsmpId);
			c_data.add(rsmp.getName(sysEnv));
		} else {
			c_data.add(null);
		}
		c_data.add(rr.getExpiredAmount(sysEnv));
		c_data.add(rr.getExpiredBaseAsString(sysEnv));
		c_data.add(rr.getIgnoreOnRerun(sysEnv));
		Integer lm = rr.getLockmode(sysEnv);
		if(lm != null) {
			c_data.add((new Lockmode(sysEnv, lm.intValue())).str(sysEnv));
		} else {
			c_data.add(null);
		}
		Vector rrs_v = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, rr.getId(sysEnv));
		String sep = "";
		StringBuffer states = new StringBuffer();
		for(int j = 0; j < rrs_v.size(); j++) {
			SDMSResourceReqStates rrs = (SDMSResourceReqStates) rrs_v.get(j);
			String rsdn = SDMSResourceStateDefinitionTable.getObject(sysEnv, rrs.getRsdId(sysEnv)).getName(sysEnv);
			states.append(sep);
			states.append(rsdn);
			sep = ",";
		}
		c_data.add(new String(states));
		c_data.add(source);
		c_data.add(sourceName);
		c_data.add(rr.getCondition(sysEnv));
	}

	private void render_env(SystemEnvironment sysEnv, Vector c_data, SDMSEnvironment e, String name)
		throws SDMSException
	{
		render_env_common(sysEnv, c_data, e, "ENVIRONMENT", name);
	}

	private void render_fenv(SystemEnvironment sysEnv, Vector c_data, SDMSEnvironment e, String name)
		throws SDMSException
	{
		render_env_common(sysEnv, c_data, e, "FOLDER", name);
	}

	private void render_env_common(SystemEnvironment sysEnv, Vector c_data, SDMSEnvironment e, String source, String name)
		throws SDMSException
	{
		c_data.add(e.getId(sysEnv));
		SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, e.getNrId(sysEnv));
		c_data.add(e.getNrId(sysEnv));
		c_data.add(nr.pathVector(sysEnv));
		c_data.add(nr.getUsageAsString(sysEnv));
		c_data.add(nr.getPrivileges(sysEnv).toString());
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(null);
		c_data.add(source);
		c_data.add(name);
		c_data.add(e.getCondition(sysEnv));
	}

	private void add_resources(SystemEnvironment sysEnv, SDMSProxy p, Vector v)
		throws SDMSException
	{
		Long cId = p.getId(sysEnv);

		NamedResourceLister nrl = new NamedResourceLister();
		nrl.setTitle(null);
		nrl.setFormatter(new SsRFormatter(cId));
		int sc[] = new int[1];
		sc[0] = 1;
		nrl.setSortColumns(sc);
		Vector nrv = new Vector();
		Vector rv;

		if(p instanceof SDMSFolder) {
			rv = SDMSResourceTable.idx_scopeId.getVector(sysEnv, cId);
			for(int i = 0; i < rv.size(); ++i) {
				SDMSResource r = (SDMSResource) rv.get(i);
				nrv.add(r.getNrId(sysEnv));
			}
		}
		if(!(p instanceof SDMSFolder)) {
			rv = SDMSResourceTemplateTable.idx_seId.getVector(sysEnv, cId);
			for(int i = 0; i < rv.size(); ++i) {
				SDMSResourceTemplate rt = (SDMSResourceTemplate) rv.get(i);
				nrv.add(rt.getNrId(sysEnv));
			}
		}
		nrl.setObjectsToList(nrv);
		nrl.setExpandIds(null);
		SDMSOutputContainer r_container = nrl.list(sysEnv);

		Collections.sort(r_container.dataset, r_container.getComparator(sysEnv, 2));

		v.add(r_container);
	}
}

