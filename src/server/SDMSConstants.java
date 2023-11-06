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

package de.independit.scheduler.server;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.parser.*;

public final class SDMSConstants
{

	public final static Long    lZERO = Long.valueOf(0L);
	public final static Integer iZERO = Integer.valueOf(0);
	public final static Short   sZERO = Short.valueOf((short) 0);
	public final static Float   fZERO = Float.valueOf(0f);
	public final static Double  dZERO = Double.valueOf(0);

	public final static Long    lONE = Long.valueOf(1L);
	public final static Integer iONE = Integer.valueOf(1);
	public final static Short   sONE = Short.valueOf((short) 1);
	public final static Float   fONE = Float.valueOf(1f);
	public final static Double  dONE = Double.valueOf(1.0);

	public final static Long    lMINUS_ONE = Long.valueOf(-1L);
	public final static Integer iMINUS_ONE = Integer.valueOf(-1);
	public final static Short   sMINUS_ONE = Short.valueOf((short) -1);
	public final static Float   fMINUS_ONE = Float.valueOf(-1f);
	public final static Double  dMINUS_ONE = Double.valueOf(-1.0);

	public final static Integer iMAX_VALUE = Integer.valueOf(Integer.MAX_VALUE);
	public final static Integer iMIN_VALUE = Integer.valueOf(Integer.MIN_VALUE);

	public static final Integer AT_APPROVE			= Integer.valueOf(SDMSAuditTrail.APPROVE);
	public static final Integer AT_APPROVAL_REQUEST		= Integer.valueOf(SDMSAuditTrail.APPROVAL_REQUEST);
	public static final Integer AT_CANCEL			= Integer.valueOf(SDMSAuditTrail.CANCEL);
	public static final Integer AT_CHANGE_PRIORITY		= Integer.valueOf(SDMSAuditTrail.CHANGE_PRIORITY);
	public static final Integer AT_CLEAR_WARNING		= Integer.valueOf(SDMSAuditTrail.CLEAR_WARNING);
	public static final Integer AT_CLONE			= Integer.valueOf(SDMSAuditTrail.CLONE);
	public static final Integer AT_COMMENT_JOB		= Integer.valueOf(SDMSAuditTrail.COMMENT_JOB);
	public static final Integer AT_DISABLE			= Integer.valueOf(SDMSAuditTrail.DISABLE);
	public static final Integer AT_ENABLE			= Integer.valueOf(SDMSAuditTrail.ENABLE);
	public static final Integer AT_JOB			= Integer.valueOf(SDMSAuditTrail.JOB);
	public static final Integer AT_JOB_IN_ERROR		= Integer.valueOf(SDMSAuditTrail.JOB_IN_ERROR);
	public static final Integer AT_JOB_RESTARTABLE		= Integer.valueOf(SDMSAuditTrail.JOB_RESTARTABLE);
	public static final Integer AT_JOB_UNREACHABLE		= Integer.valueOf(SDMSAuditTrail.JOB_UNREACHABLE);
	public static final Integer AT_IGNORE_DEPENDENCY	= Integer.valueOf(SDMSAuditTrail.IGNORE_DEPENDENCY);
	public static final Integer AT_IGNORE_DEP_RECURSIVE	= Integer.valueOf(SDMSAuditTrail.IGNORE_DEP_RECURSIVE);
	public static final Integer AT_IGNORE_NAMED_RESOURCE	= Integer.valueOf(SDMSAuditTrail.IGNORE_NAMED_RESOURCE);
	public static final Integer AT_IGNORE_RESOURCE		= Integer.valueOf(SDMSAuditTrail.IGNORE_RESOURCE);
	public static final Integer AT_KILL			= Integer.valueOf(SDMSAuditTrail.KILL);
	public static final Integer AT_KILL_RECURSIVE		= Integer.valueOf(SDMSAuditTrail.KILL_RECURSIVE);
	public static final Integer AT_REJECT			= Integer.valueOf(SDMSAuditTrail.REJECT);
	public static final Integer AT_RENICE			= Integer.valueOf(SDMSAuditTrail.RENICE);
	public static final Integer AT_RERUN			= Integer.valueOf(SDMSAuditTrail.RERUN);
	public static final Integer AT_RERUN_RECURSIVE		= Integer.valueOf(SDMSAuditTrail.RERUN_RECURSIVE);
	public static final Integer AT_RESUME			= Integer.valueOf(SDMSAuditTrail.RESUME);
	public static final Integer AT_REVIEW_REQUEST		= Integer.valueOf(SDMSAuditTrail.REVIEW_REQUEST);
	public static final Integer AT_SET_EXIT_STATE		= Integer.valueOf(SDMSAuditTrail.SET_EXIT_STATE);
	public static final Integer AT_SET_PARAMETERS		= Integer.valueOf(SDMSAuditTrail.SET_PARAMETERS);
	public static final Integer AT_SET_RESOURCE_STATE	= Integer.valueOf(SDMSAuditTrail.SET_RESOURCE_STATE);
	public static final Integer AT_SET_STATE		= Integer.valueOf(SDMSAuditTrail.SET_STATE);
	public static final Integer AT_SET_WARNING		= Integer.valueOf(SDMSAuditTrail.SET_WARNING);
	public static final Integer AT_SUBMIT_SUSPENDED		= Integer.valueOf(SDMSAuditTrail.SUBMIT_SUSPENDED);
	public static final Integer AT_SUBMITTED		= Integer.valueOf(SDMSAuditTrail.SUBMITTED);
	public static final Integer AT_SUSPEND			= Integer.valueOf(SDMSAuditTrail.SUSPEND);
	public static final Integer AT_TIMEOUT			= Integer.valueOf(SDMSAuditTrail.TIMEOUT);
	public static final Integer AT_TRIGGER_FAILED		= Integer.valueOf(SDMSAuditTrail.TRIGGER_FAILED);
	public static final Integer AT_TRIGGER_SUBMIT		= Integer.valueOf(SDMSAuditTrail.TRIGGER_SUBMIT);
	public static final String  AT_EVAUDITPREFIX		= "AUDIT.";

	public static final Integer DD_ALL_FINAL		= Integer.valueOf(SDMSDependencyDefinition.ALL_FINAL);
	public static final Integer DD_ALL_REACHABLE		= Integer.valueOf(SDMSDependencyDefinition.ALL_REACHABLE);
	public static final Integer DD_DEFAULT			= Integer.valueOf(SDMSDependencyDefinition.DEFAULT);
	public static final Integer DD_ERROR			= Integer.valueOf(SDMSDependencyDefinition.ERROR);
	public static final Integer DD_FINAL			= Integer.valueOf(SDMSDependencyDefinition.FINAL);
	public static final Integer DD_UNREACHABLE		= Integer.valueOf(SDMSDependencyDefinition.UNREACHABLE);

	public static final Integer DI_NO			= Integer.valueOf(SDMSDependencyInstance.NO);
	public static final Integer DI_OPEN			= Integer.valueOf(SDMSDependencyInstance.OPEN);
	public static final Integer DI_RECURSIVE		= Integer.valueOf(SDMSDependencyInstance.RECURSIVE);

	public static final Integer ES_FINAL			= Integer.valueOf(SDMSExitState.FINAL);
	public static final Integer ES_RESTARTABLE		= Integer.valueOf(SDMSExitState.RESTARTABLE);

	public static final Integer IV_MINUTE			= Integer.valueOf(SDMSInterval.MINUTE);
	public static final Integer IV_INTERVAL			= Integer.valueOf(SDMSInterval.INTERVAL);
	public static final Integer IV_INTERVAL_DISPATCHER	= Integer.valueOf(SDMSInterval.INTERVAL_DISPATCHER);

	public static final Integer KJ_ERROR			= Integer.valueOf(SDMSKillJob.ERROR);

	public static final Integer OC_COMMENT			= Integer.valueOf(SDMSObjectComment.COMMENT);
	public static final Integer OC_DISTRIBUTION		= Integer.valueOf(SDMSObjectComment.DISTRIBUTION);
	public static final Integer OC_ENVIRONMENT		= Integer.valueOf(SDMSObjectComment.ENVIRONMENT);
	public static final Integer OC_EVENT			= Integer.valueOf(SDMSObjectComment.EVENT);
	public static final Integer OC_EXIT_STATE_DEFINITION	= Integer.valueOf(SDMSObjectComment.EXIT_STATE_DEFINITION);
	public static final Integer OC_EXIT_STATE_MAPPING	= Integer.valueOf(SDMSObjectComment.EXIT_STATE_MAPPING);
	public static final Integer OC_EXIT_STATE_PROFILE	= Integer.valueOf(SDMSObjectComment.EXIT_STATE_PROFILE);
	public static final Integer OC_EXIT_STATE_TRANSLATION	= Integer.valueOf(SDMSObjectComment.EXIT_STATE_TRANSLATION);
	public static final Integer OC_FOLDER			= Integer.valueOf(SDMSObjectComment.FOLDER);
	public static final Integer OC_FOOTPRINT		= Integer.valueOf(SDMSObjectComment.FOOTPRINT);
	public static final Integer OC_GRANT			= Integer.valueOf(SDMSObjectComment.GRANT);
	public static final Integer OC_GROUP			= Integer.valueOf(SDMSObjectComment.GROUP);
	public static final Integer OC_INTERVAL			= Integer.valueOf(SDMSObjectComment.INTERVAL);
	public static final Integer OC_JOB_DEFINITION		= Integer.valueOf(SDMSObjectComment.JOB_DEFINITION);
	public static final Integer OC_NAMED_RESOURCE		= Integer.valueOf(SDMSObjectComment.NAMED_RESOURCE);
	public static final Integer OC_NICE_PROFILE		= Integer.valueOf(SDMSObjectComment.NICE_PROFILE);
	public static final Integer OC_OBJECT_MONITOR		= Integer.valueOf(SDMSObjectComment.OBJECT_MONITOR);
	public static final Integer OC_POOL			= Integer.valueOf(SDMSObjectComment.POOL);
	public static final Integer OC_RESOURCE			= Integer.valueOf(SDMSObjectComment.RESOURCE);
	public static final Integer OC_RESOURCE_STATE_DEFINITION= Integer.valueOf(SDMSObjectComment.RESOURCE_STATE_DEFINITION);
	public static final Integer OC_RESOURCE_STATE_MAPPING	= Integer.valueOf(SDMSObjectComment.RESOURCE_STATE_MAPPING);
	public static final Integer OC_RESOURCE_STATE_PROFILE	= Integer.valueOf(SDMSObjectComment.RESOURCE_STATE_PROFILE);
	public static final Integer OC_RESOURCE_TEMPLATE	= Integer.valueOf(SDMSObjectComment.RESOURCE_TEMPLATE);
	public static final Integer OC_SCHEDULE			= Integer.valueOf(SDMSObjectComment.SCHEDULE);
	public static final Integer OC_SCHEDULED_EVENT		= Integer.valueOf(SDMSObjectComment.SCHEDULED_EVENT);
	public static final Integer OC_SCOPE			= Integer.valueOf(SDMSObjectComment.SCOPE);
	public static final Integer OC_TEXT			= Integer.valueOf(SDMSObjectComment.TEXT);
	public static final Integer OC_TRIGGER			= Integer.valueOf(SDMSObjectComment.TRIGGER);
	public static final Integer OC_URL			= Integer.valueOf(SDMSObjectComment.URL);
	public static final Integer OC_USER			= Integer.valueOf(SDMSObjectComment.USER);
	public static final Integer OC_WATCH_TYPE		= Integer.valueOf(SDMSObjectComment.WATCH_TYPE);

	public static final Integer OM_NONE			= iZERO;
	public static final Integer OM_CHANGE			= iZERO;
	public static final Integer OM_CREATE			= iZERO;

	public static final Integer    PD_CONSTANT		= Integer.valueOf(SDMSParameterDefinition.CONSTANT);
	public static final Integer    PD_DYNAMIC		= Integer.valueOf(SDMSParameterDefinition.DYNAMIC);
	public static final Integer    PD_DYNAMICVALUE		= Integer.valueOf(SDMSParameterDefinition.DYNAMICVALUE);
	public static final Integer    PD_NONE			= Integer.valueOf(SDMSParameterDefinition.NONE);
	public static final Integer    PD_PARAMETER		= Integer.valueOf(SDMSParameterDefinition.PARAMETER);

	public static final Long    PR_CANCEL			= Long.valueOf(SDMSPrivilege.CANCEL);
	public static final Long    PR_CLEAR_WARNING		= Long.valueOf(SDMSPrivilege.CLEAR_WARNING);
	public static final Long    PR_CLONE			= Long.valueOf(SDMSPrivilege.CLONE);
	public static final Long    PR_ENABLE			= Long.valueOf(SDMSPrivilege.ENABLE);
	public static final Long    PR_IGN_DEPENDENCY		= Long.valueOf(SDMSPrivilege.IGN_DEPENDENCY);
	public static final Long    PR_IGN_RESOURCE		= Long.valueOf(SDMSPrivilege.IGN_RESOURCE);
	public static final Long    PR_KILL			= Long.valueOf(SDMSPrivilege.KILL);
	public static final Long    PR_MODIFY_PARAMETER		= Long.valueOf(SDMSPrivilege.MODIFY_PARAMETER);
	public static final Long    PR_NOPRIVS			= Long.valueOf(SDMSPrivilege.NOPRIVS);
	public static final Long    PR_PRIORITY			= Long.valueOf(SDMSPrivilege.PRIORITY);
	public static final Long    PR_RERUN			= Long.valueOf(SDMSPrivilege.RERUN);
	public static final Long    PR_SET_JOB_STATE		= Long.valueOf(SDMSPrivilege.SET_JOB_STATE);
	public static final Long    PR_SET_STATE		= Long.valueOf(SDMSPrivilege.SET_STATE);
	public static final Long    PR_SUSPEND			= Long.valueOf(SDMSPrivilege.SUSPEND);

	public static final Integer RA_ALLOCATION		= Integer.valueOf(SDMSResourceAllocation.ALLOCATION);
	public static final Integer RA_IGNORE			= Integer.valueOf(SDMSResourceAllocation.IGNORE);
	public static final Integer RA_MASTER_REQUEST		= Integer.valueOf(SDMSResourceAllocation.MASTER_REQUEST);
	public static final Integer RA_MASTER_RESERVATION	= Integer.valueOf(SDMSResourceAllocation.MASTER_RESERVATION);
	public static final Integer RA_REQUEST			= Integer.valueOf(SDMSResourceAllocation.REQUEST);
	public static final Integer RA_RESERVATION		= Integer.valueOf(SDMSResourceAllocation.RESERVATION);

	public static final Integer RR_N			= Integer.valueOf(SDMSResourceRequirement.N);
	public static final Integer RR_NOKEEP			= Integer.valueOf(SDMSResourceRequirement.NOKEEP);

	public static final Integer S_FATAL			= Integer.valueOf(SDMSScope.FATAL);
	public static final Integer S_MD5			= Integer.valueOf(SDMSScope.MD5);
	public static final Integer S_NOMINAL			= Integer.valueOf(SDMSScope.NOMINAL);
	public static final Integer S_NONFATAL			= Integer.valueOf(SDMSScope.NONFATAL);
	public static final Integer S_SCOPE			= Integer.valueOf(SDMSScope.SCOPE);
	public static final Integer S_SERVER			= Integer.valueOf(SDMSScope.SERVER);
	public static final Integer S_SHA256			= Integer.valueOf(SDMSScope.SHA256);

	public static final Integer SE_AND			= Integer.valueOf(SDMSSchedulingEntity.AND);
	public static final Integer SE_APPROVE			= Integer.valueOf(SDMSSchedulingEntity.APPROVE);
	public static final Integer SE_DEFAULT			= Integer.valueOf(SDMSSchedulingEntity.DEFAULT);
	public static final Boolean SE_MASTER			= Boolean.valueOf(SDMSSchedulingEntity.MASTER);
	public static final Integer SE_NO			= Integer.valueOf(SDMSSchedulingEntity.NO);
	public static final Boolean SE_NOMASTER			= Boolean.valueOf(SDMSSchedulingEntity.NOMASTER);
	public static final Boolean SE_NOSUSPEND		= Boolean.valueOf(SDMSSchedulingEntity.NOSUSPEND);
	public static final Boolean SE_NOTRUNC			= Boolean.valueOf(SDMSSchedulingEntity.NOTRUNC);
	public static final Integer SE_PARENT			= Integer.valueOf(SDMSSchedulingEntity.PARENT);
	public static final Integer SE_REVIEW			= Integer.valueOf(SDMSSchedulingEntity.REVIEW);
	public static final Boolean SE_SUSPEND			= Boolean.valueOf(SDMSSchedulingEntity.SUSPEND);
	public static final Boolean SE_TRUNC			= Boolean.valueOf(SDMSSchedulingEntity.TRUNC);

	public static final Integer SEV_LAST			= Integer.valueOf(SDMSScheduledEvent.LAST);

	public static final Integer SH_CHILDSUSPEND		= Integer.valueOf(SDMSSchedulingHierarchy.CHILDSUSPEND);
	public static final Boolean SH_DYNAMIC			= Boolean.valueOf(SDMSSchedulingHierarchy.DYNAMIC);
	public static final Integer SH_FAILURE			= Integer.valueOf(SDMSSchedulingHierarchy.FAILURE);
	public static final Integer SH_NOMERGE			= Integer.valueOf(SDMSSchedulingHierarchy.NOMERGE);
	public static final Boolean SH_STATIC			= Boolean.valueOf(SDMSSchedulingHierarchy.STATIC);

	public static final Integer SME_CANCELLED		= Integer.valueOf(SDMSSubmittedEntity.CANCELLED);
	public static final Integer SME_DEFERRED		= Integer.valueOf(SDMSDependencyInstance.DEFERRED);
	public static final Integer SME_DEPENDENCY_WAIT		= Integer.valueOf(SDMSSubmittedEntity.DEPENDENCY_WAIT);
	public static final Integer SME_ERROR			= Integer.valueOf(SDMSSubmittedEntity.ERROR);
	public static final Integer SME_FAILURE			= Integer.valueOf(SDMSSchedulingHierarchy.FAILURE);
	public static final Integer SME_FINAL			= Integer.valueOf(SDMSSubmittedEntity.FINAL);
	public static final Integer SME_FINISHED		= Integer.valueOf(SDMSSubmittedEntity.FINISHED);
	public static final Integer SME_KILLED			= Integer.valueOf(SDMSSubmittedEntity.KILLED);
	public static final Integer SME_NOSUSPEND		= Integer.valueOf(SDMSSubmittedEntity.NOSUSPEND);
	public static final Integer SME_RESOURCE_WAIT		= Integer.valueOf(SDMSSubmittedEntity.RESOURCE_WAIT);
	public static final Integer SME_RUNNABLE		= Integer.valueOf(SDMSSubmittedEntity.RUNNABLE);
	public static final Integer SME_RUNNING			= Integer.valueOf(SDMSSubmittedEntity.RUNNING);
	public static final Integer SME_STARTING		= Integer.valueOf(SDMSSubmittedEntity.STARTING);
	public static final Integer SME_SUBMITTED		= Integer.valueOf(SDMSSubmittedEntity.SUBMITTED);
	public static final Integer SME_SUSPEND			= Integer.valueOf(SDMSSubmittedEntity.SUSPEND);
	public static final Integer SME_SYNCHRONIZE_WAIT	= Integer.valueOf(SDMSSubmittedEntity.SYNCHRONIZE_WAIT);
	public static final Integer SME_TO_KILL			= Integer.valueOf(SDMSSubmittedEntity.TO_KILL);
	public static final Integer SME_UNREACHABLE		= Integer.valueOf(SDMSSubmittedEntity.UNREACHABLE);

	public static final Integer TR_AFTER_FINAL		= Integer.valueOf(SDMSTrigger.AFTER_FINAL);
	public static final Integer TR_BEFORE_FINAL		= Integer.valueOf(SDMSTrigger.BEFORE_FINAL);
	public static final Integer TR_CHANGE			= Integer.valueOf(SDMSTrigger.CHANGE);
	public static final Integer TR_CREATE			= Integer.valueOf(SDMSTrigger.CREATE);
	public static final Integer TR_DELETE			= Integer.valueOf(SDMSTrigger.DELETE);
	public static final Integer TR_FINISH_CHILD		= Integer.valueOf(SDMSTrigger.FINISH_CHILD);
	public static final Integer TR_IMMEDIATE_LOCAL		= Integer.valueOf(SDMSTrigger.IMMEDIATE_LOCAL);
	public static final Integer TR_IMMEDIATE_MERGE		= Integer.valueOf(SDMSTrigger.IMMEDIATE_MERGE);
	public static final Integer TR_RERUN			= Integer.valueOf(SDMSTrigger.RERUN);
	public static final Integer TR_SUBMIT			= Integer.valueOf(SDMSTrigger.SUBMIT);
	public static final Integer TR_UNTIL_FINAL		= Integer.valueOf(SDMSTrigger.UNTIL_FINAL);
	public static final Integer TR_UNTIL_FINISHED		= Integer.valueOf(SDMSTrigger.UNTIL_FINISHED);
	public static final Integer TR_WARNING			= Integer.valueOf(SDMSTrigger.WARNING);

	public static final Integer U_MD5			= Integer.valueOf(SDMSUser.MD5);
	public static final Integer U_PLAIN			= Integer.valueOf(SDMSUser.PLAIN);
	public static final Integer U_SHA256			= Integer.valueOf(SDMSUser.SHA256);

	public static final Integer UE_SERVER			= Integer.valueOf(SDMSUserEquiv.SERVER);
	public static final Integer UE_USER			= Integer.valueOf(SDMSUserEquiv.USER);

	public static final Integer ST_DEFAULT_PRIORITY		= Integer.valueOf(SchedulingThread.DEFAULT_PRIORITY);
	public static final Integer ST_MAX_PRIORITY		= Integer.valueOf(SchedulingThread.MAX_PRIORITY);
	public static final Integer ST_MIN_PRIORITY		= Integer.valueOf(SchedulingThread.MIN_PRIORITY);
	public static final Integer ST_MINUS_MIN_PRIORITY	= Integer.valueOf(- SchedulingThread.MIN_PRIORITY);

	public static final Integer PS_ENVIRONMENT		= Integer.valueOf(Parser.ENVIRONMENT);
	public static final Integer PS_EVENT			= Integer.valueOf(Parser.EVENT);
	public static final Integer PS_EXIT_STATUS_MAPPING	= Integer.valueOf(Parser.EXIT_STATUS_MAPPING);
	public static final Integer PS_EXIT_STATUS_PROFILE	= Integer.valueOf(Parser.EXIT_STATUS_PROFILE);
	public static final Integer PS_EXIT_STATUS_TRANSLATION	= Integer.valueOf(Parser.EXIT_STATUS_TRANSLATION);
	public static final Integer PS_FOLDER			= Integer.valueOf(Parser.FOLDER);
	public static final Integer PS_FOOTPRINT		= Integer.valueOf(Parser.FOOTPRINT);
	public static final Integer PS_GROUP			= Integer.valueOf(Parser.GROUP);
	public static final Integer PS_JOB_DEFINITION		= Integer.valueOf(Parser.JOB_DEFINITION);
	public static final Integer PS_JOB_SERVER		= Integer.valueOf(Parser.JOB_SERVER);
	public static final Integer PS_NAMED_RESOURCE		= Integer.valueOf(Parser.NAMED_RESOURCE);
	public static final Integer PS_NICE_PROFILE		= Integer.valueOf(Parser.NICE_PROFILE);
	public static final Integer PS_OBJECT			= Integer.valueOf(Parser.OBJECT);
	public static final Integer PS_SCHEDULE			= Integer.valueOf(Parser.SCHEDULE);
	public static final Integer PS_WATCH			= Integer.valueOf(Parser.WATCH);

	private SDMSConstants() {  }

}
