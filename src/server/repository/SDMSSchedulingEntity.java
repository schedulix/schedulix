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
import java.lang.*;
import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.*;

public class SDMSSchedulingEntity extends SDMSSchedulingEntityProxyGeneric
	implements SDMSOwnedObject
{

	private final static VariableResolver SEVR = new SeVariableResolver();
	private static final Integer zero = new Integer(0);
	private static final Long lzero = new Long(0);

	protected SDMSSchedulingEntity(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long getParentId(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getFolderId(sysEnv);
	}

	public SDMSProxy getParent(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long parentId = getParentId(sysEnv);
		if (parentId == null) return null;
		return SDMSFolderTable.getObject(sysEnv, parentId);
	}

	public String pathString(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSFolder f = SDMSFolderTable.getObject(sysEnv, getFolderId(sysEnv));
		return f.pathString(sysEnv) + "." + getName(sysEnv);
	}

	public String pathString(SystemEnvironment sysEnv, long version)
	throws SDMSException
	{
		SDMSFolder f = SDMSFolderTable.getObject(sysEnv, getFolderId(sysEnv),version);
		return f.pathString(sysEnv,version) + "." + getName(sysEnv);
	}

	public PathVector pathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSFolder f = SDMSFolderTable.getObject(sysEnv, getFolderId(sysEnv));
		PathVector p = f.pathVector(sysEnv);
		p.add(getName(sysEnv));
		return p;
	}

	public PathVector idPathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSFolder f = SDMSFolderTable.getObject(sysEnv, getFolderId(sysEnv));
		PathVector p = f.idPathVector(sysEnv);
		p.add(getId(sysEnv));
		return p;
	}

	public PathVector pathVector(SystemEnvironment sysEnv, long version)
	throws SDMSException
	{
		SDMSFolder f = SDMSFolderTable.getObject(sysEnv, getFolderId(sysEnv), version);
		PathVector p = f.pathVector(sysEnv, version);
		p.add(getName(sysEnv));
		return p;
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return pathString(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "job definition " + getURLName(sysEnv);
	}

	public String getSubtypeName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getTypeAsString(sysEnv);
	}

	public boolean checkParameterRI(final SystemEnvironment sysEnv, final Long nrId)
	throws SDMSException
	{
		final Long seId = getId(sysEnv);
		final Vector v = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, seId);
		for(int i = 0; i < v.size(); i++) {
			SDMSParameterDefinition pd = (SDMSParameterDefinition) v.get(i);
			if(pd.getType(sysEnv).intValue() != SDMSParameterDefinition.RESOURCEREFERENCE) continue;
			pd = SDMSParameterDefinitionTable.getObject(sysEnv, pd.getLinkPdId(sysEnv));
			if(pd.getSeId(sysEnv).equals(nrId))
				return true;
		}
		return false;
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key)
	throws SDMSException
	{
		return SEVR.getVariableValue(sysEnv, this, key, -1);
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key, long version)
	throws SDMSException
	{
		return SEVR.getVariableValue(sysEnv, this, key, version);
	}

	public SDMSSubmittedEntity submitMaster (SystemEnvironment sysEnv, Vector params, Integer suspended, Long resumeTs, Long ownerId,
	                Integer niceValue, String auditEintrag)
	throws SDMSException
	{
		return submitMaster (sysEnv,params,suspended,resumeTs,ownerId,niceValue,auditEintrag,null,null);
	}

	public SDMSSubmittedEntity submitMaster (SystemEnvironment sysEnv, Vector params, Integer suspended, Long resumeTs, Long ownerId,
	                Integer niceValue, String auditEintrag, String submitTag, Integer unresolvedHandling)
	throws SDMSException
	{
		return submitMaster (sysEnv, params, suspended, resumeTs, ownerId, niceValue, auditEintrag, submitTag, null, unresolvedHandling);
	}

	public SDMSSubmittedEntity submitMaster (SystemEnvironment sysEnv, Vector params, Integer suspended, Long resumeTs, Long ownerId,
	                Integer niceValue, String auditEintrag, String submitTag, String childTag, Integer unresolvedHandling)
	throws SDMSException
	{
		Long seId = getId(sysEnv);
		Long submitTs;
		Date ts = new Date();
		submitTs = ts.getTime();

		if (getMasterSubmittable(sysEnv).booleanValue() == SDMSSchedulingEntity.NOMASTER) {
			throw new CommonErrorException (
			        new SDMSMessage(sysEnv, "02201041040",
			                        "$1 $2 cannot be submitted as master", getTypeAsString(sysEnv), getName(sysEnv))
			);
		}
		Long espId = getEspId(sysEnv);
		SDMSExitStateProfile esp = SDMSExitStateProfileTable.getObject (sysEnv, espId);
		if (esp.getIsValid(sysEnv).booleanValue() == false) {
			esp.checkProfile(sysEnv);
		}
		if (suspended == null) {
			suspended = new Integer(getSubmitSuspended(sysEnv) ? SDMSSubmittedEntity.SUSPEND : SDMSSubmittedEntity.NOSUSPEND);
			if (suspended.intValue() == SDMSSubmittedEntity.SUSPEND) {
				resumeTs = SubmitJob.evalResumeObj(sysEnv, getResumeAt(sysEnv), getResumeIn(sysEnv), getResumeBase(sysEnv), submitTs, true );
			}
		}
		Long opSusresTs = null;
		if (suspended.intValue() != SDMSSubmittedEntity.NOSUSPEND)
			opSusresTs = new Long(-submitTs.longValue());

		long seVersion = SDMSTransaction.drawVersion(sysEnv);

		Integer prio = getPriority(sysEnv);
		Integer rawPrio = zero;
		Integer nice = (niceValue == null ? new Integer(0) : niceValue);
		switch(getType(sysEnv).intValue() ) {
			case JOB:
				if(prio == null) {
					prio = new Integer(SchedulingThread.DEFAULT_PRIORITY + nice.intValue());
				} else {
					if(prio.intValue() >= SystemEnvironment.priorityLowerBound) {
						prio = new Integer(prio.intValue() + nice.intValue());
						if(prio.intValue() < SystemEnvironment.priorityLowerBound) {
							prio = new Integer(SystemEnvironment.priorityLowerBound);
						}
					}
					rawPrio = new Integer(prio.intValue() * 100);
				}
				break;
			case BATCH:
				if(prio != null) {
					nice = new Integer(prio.intValue() + nice.intValue());
					prio = new Integer(SchedulingThread.DEFAULT_PRIORITY);
				}
				break;
			case MILESTONE:
				prio = new Integer(SchedulingThread.DEFAULT_PRIORITY);
				if (nice == null)
					nice = new Integer(0);
				break;
		}
		Integer minEP = null;
		minEP = new Integer(SystemEnvironment.priorityLowerBound);
		Integer agingAmount = null;
		Integer agingBase = null;
		agingAmount = new Integer(SystemEnvironment.priorityDelay);
		agingBase = new Integer(SDMSInterval.MINUTE);
		switch(agingBase.intValue()) {
			case SDMSInterval.MINUTE:
				break;
			case SDMSInterval.HOUR:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.HOUR_DUR_M));
				break;
			case SDMSInterval.DAY:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.DAY_DUR_M));
				break;
			case SDMSInterval.WEEK:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.WEEK_DUR_M));
				break;
			case SDMSInterval.MONTH:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.MONTH_DUR_M));
				break;
			case SDMSInterval.YEAR:
				agingAmount = new Integer((int) (agingAmount.intValue() * SDMSInterval.YEAR_DUR_M));
				break;
		}
		agingBase = new Integer(SDMSInterval.MINUTE);
		final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.table.create(sysEnv,
		                                sysEnv.randomLong(),
		                                new Long(0),
		                                submitTag,
		                                unresolvedHandling,
		                                seId,
		                                childTag,
		                                new Long(seVersion),
		                                ownerId,
		                                null,
		                                null,
		                                Boolean.TRUE,
		                                Boolean.FALSE,
						null,
		                                new Integer(SDMSSchedulingHierarchy.FAILURE),
		                                new Integer(SDMSSubmittedEntity.SUBMITTED),
		                                null,
		                                null,
		                                Boolean.FALSE,
		                                Boolean.FALSE,
		                                null,
		                                null,
		                                null,
		                                null,
		                                zero,
		                                Boolean.FALSE,
		                                Boolean.FALSE,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                zero,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                suspended,
		                                prio,
						rawPrio,
		                                nice,
						zero,
		                                minEP,
		                                agingAmount,
		                                zero,
		                                zero,
		                                zero,
		                                null,
		                                submitTs,
		                                resumeTs,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                null,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                zero,
		                                null,
						zero,
						zero,
						zero,
						zero,
		                                zero,
						zero,
						zero,
						zero,
						zero,
		                                opSusresTs,
						null
		);

		Long smeId = sme.getId(sysEnv);
		if (params != null) {
			Iterator i = params.iterator();
			while (i.hasNext()) {
				WithItem wi = (WithItem)i.next();
				sme.setVariableValue(sysEnv, (String) wi.key, (String) wi.value);
			}
		}

		sme.submitChilds(sysEnv, suspended.intValue() == SDMSSubmittedEntity.NOSUSPEND ? 0 : 1, ownerId, null, nice.intValue() * 100);

		sme.resolveDependencies(sysEnv, true );

		Vector trv = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
		for(int i = 0; i < trv.size(); i++) {
			SDMSTrigger t = (SDMSTrigger) trv.get(i);
			int trType = t.getType(sysEnv).intValue();
			if(trType == SDMSTrigger.UNTIL_FINISHED || trType == SDMSTrigger.UNTIL_FINAL) {
				SDMSTriggerQueueTable.table.create(sysEnv, sme.getId(sysEnv), t.getId(sysEnv), lzero, zero, zero);
			}
		}

		sme.checkDependencies(sysEnv);

		Long lTs = new Long (ts.getTime());
		Long internalId;
		if(sysEnv.cEnv.isUser())
			internalId = sysEnv.cEnv.uid();
		else
			internalId = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(SDMSUser.INTERNAL, new Long(0))).getId(sysEnv);

		SystemEnvironment.sched.notifyChange(sysEnv, sme, SchedulingThread.SUBMIT);

		return sme;
	}

	public SDMSSubmittedEntity createErrorMaster (SystemEnvironment sysEnv, Long ownerId, String auditEintrag, String errorMsg)
	throws SDMSException
	{
		Long seId = getId(sysEnv);

		Date ts = new Date();
		long seVersion = SDMSTransaction.drawVersion(sysEnv);

		Integer prio = new Integer(SchedulingThread.DEFAULT_PRIORITY);
		Integer nice = new Integer(0);

		Long espId = getEspId(sysEnv);
		SDMSExitState es;
		Vector v = SDMSExitStateTable.idx_espId.getVector (sysEnv, espId);

		Long esdId = null;
		for(int i = 0; i < v.size(); i++) {
			es = (SDMSExitState) v.get(i);
			if(es.getIsBroken(sysEnv).booleanValue()) {
				esdId = es.getEsdId(sysEnv);
				break;
			}
		}
		SDMSSubmittedEntity sme = ((SDMSSubmittedEntityTable) SDMSSubmittedEntityTable.table).createErrorMaster(sysEnv,
		                          seId,
		                          new Long(seVersion),
		                          ownerId,
		                          esdId,
		                          esdId,
		                          errorMsg,
		                          prio,
		                          nice,
		                          new Long (ts.getTime())
		                                                                                                       );

		Long smeId = sme.getId(sysEnv);
		sme.setMasterId(sysEnv, smeId);

		Long lTs = new Long (ts.getTime());
		Long internalId;
		if(sysEnv.cEnv.isUser())
			internalId = sysEnv.cEnv.uid();
		else
			internalId = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(SDMSUser.INTERNAL, new Long(0))).getId(sysEnv);

		sme.trigger(sysEnv, SDMSTrigger.IMMEDIATE_LOCAL);
		sme.trigger(sysEnv, SDMSTrigger.IMMEDIATE_MERGE);
		sme.trigger(sysEnv, SDMSTrigger.FINISH_CHILD);

		return sme;
	}

	public SDMSSchedulingEntity copy(SystemEnvironment sysEnv, Long targetFolderId, String name, HashMap relocationTable)
	throws SDMSException
	{
		Long id = getId(sysEnv);
		SDMSUser u = SDMSUserTable.getObject(sysEnv, sysEnv.cEnv.uid());

		final Long neId = getNeId(sysEnv);
		if(neId != null) {
			final SDMSNamedEnvironment ne = SDMSNamedEnvironmentTable.getObject(sysEnv, neId);
			if(!ne.checkPrivileges(sysEnv, SDMSPrivilege.USE))
				throw new AccessViolationException(ne.accessViolationMessage(sysEnv, "03402291147"));
		}
		final Long fpId = getFpId(sysEnv);
		if(fpId != null) {
			final SDMSFootprint fp = SDMSFootprintTable.getObject(sysEnv, fpId);
			if(!fp.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(fp.accessViolationMessage(sysEnv, "03402291151"));
		}

		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.table.create(sysEnv,
		                          name,
		                          targetFolderId,
		                          u.getDefaultGId(sysEnv),
		                          getType(sysEnv),
		                          getRunProgram(sysEnv),
		                          getRerunProgram(sysEnv),
		                          getKillProgram(sysEnv),
		                          getWorkdir(sysEnv),
		                          getLogfile(sysEnv),
		                          getTruncLog(sysEnv),
		                          getErrlogfile(sysEnv),
		                          getTruncErrlog(sysEnv),
		                          getExpectedRuntime(sysEnv),
		                          getExpectedFinaltime(sysEnv),
		                          getGetExpectedRuntime(sysEnv),
		                          getPriority(sysEnv),
		                          getMinPriority(sysEnv),
		                          getAgingAmount(sysEnv),
		                          getAgingBase(sysEnv),
		                          getSubmitSuspended(sysEnv),
		                          getResumeAt(sysEnv),
		                          getResumeIn(sysEnv),
		                          getResumeBase(sysEnv),
		                          getMasterSubmittable(sysEnv),
		                          getTimeoutAmount(sysEnv),
		                          getTimeoutBase(sysEnv),
		                          getTimeoutStateId(sysEnv),
		                          getSameNode(sysEnv),
		                          getGangSchedule(sysEnv),
		                          getDependencyOperation(sysEnv),
		                          getEsmpId(sysEnv),
		                          getEspId(sysEnv),
		                          getQaId(sysEnv),
		                          neId,
		                          fpId,
		                          getInheritPrivs(sysEnv)
		                                                                );
		Long seId = se.getId(sysEnv);
		Vector v_rr = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, id);
		Iterator i_rr = v_rr.iterator();
		while (i_rr.hasNext()) {
			SDMSResourceRequirement rr_o = (SDMSResourceRequirement)i_rr.next();
			final Long nrId = rr_o.getNrId(sysEnv);
			final SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, nrId);
			if(!nr.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(nr.accessViolationMessage(sysEnv, "03402291155"));
			SDMSResourceRequirement rr_n = SDMSResourceRequirementTable.table.create(sysEnv,
			                               nrId,
			                               seId,
			                               rr_o.getAmount(sysEnv),
			                               rr_o.getKeepMode(sysEnv),
			                               rr_o.getIsSticky(sysEnv),
			                               rr_o.getStickyName(sysEnv),
			                               rr_o.getStickyParent(sysEnv),
			                               rr_o.getRsmpId(sysEnv),
			                               rr_o.getExpiredAmount(sysEnv),
			                               rr_o.getExpiredBase(sysEnv),
			                               rr_o.getIgnoreOnRerun(sysEnv),
			                               rr_o.getLockmode(sysEnv),
			                               rr_o.getCondition(sysEnv)
			                                                                        );
			Vector v_rrs = SDMSResourceReqStatesTable.idx_rrId.getVector(sysEnv, rr_o.getId(sysEnv));
			Iterator i_rrs = v_rrs.iterator();
			while (i_rrs.hasNext()) {
				SDMSResourceReqStates rrs = (SDMSResourceReqStates)i_rrs.next();
				SDMSResourceReqStatesTable.table.create(sysEnv,
				                                        rr_n.getId(sysEnv),
				                                        rrs.getRsdId(sysEnv)
				                                       );
			}

		}
		Vector v_pd = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, id);
		Iterator i_pd = v_pd.iterator();
		while (i_pd.hasNext()) {
			SDMSParameterDefinition pd = (SDMSParameterDefinition)i_pd.next();
			SDMSParameterDefinition npd = SDMSParameterDefinitionTable.table.create(sysEnv,
			                              seId,
			                              pd.getName(sysEnv),
			                              pd.getType(sysEnv),
			                              pd.getAggFunction(sysEnv),
			                              pd.getDefaultValue(sysEnv),
			                              pd.getIsLocal(sysEnv),
			                              pd.getLinkPdId(sysEnv),
			                              pd.getExportName(sysEnv)
			                                                                       );
			if(relocationTable != null) {
				relocationTable.put(pd.getId(sysEnv), npd.getId(sysEnv));
			}
		}
		Vector v_dd = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, id);
		Iterator i_dd = v_dd.iterator();
		while (i_dd.hasNext()) {
			SDMSDependencyDefinition dd_o = (SDMSDependencyDefinition)i_dd.next();
			SDMSDependencyDefinition dd_n = SDMSDependencyDefinitionTable.table.create(sysEnv,
			                                seId,
			                                dd_o.getSeRequiredId(sysEnv),
			                                dd_o.getName(sysEnv),
			                                dd_o.getUnresolvedHandling(sysEnv),
			                                dd_o.getMode(sysEnv),
			                                dd_o.getStateSelection(sysEnv),
			                                dd_o.getCondition(sysEnv),
			                                dd_o.getResolveMode(sysEnv),
			                                dd_o.getExpiredAmount(sysEnv),
			                                dd_o.getExpiredBase(sysEnv),
			                                dd_o.getSelectCondition(sysEnv)
			                                                                          );
			Vector v_ds = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, dd_o.getId(sysEnv));
			Iterator i_ds = v_ds.iterator();
			while (i_ds.hasNext()) {
				SDMSDependencyState ds = (SDMSDependencyState)i_ds.next();
				SDMSDependencyStateTable.table.create(sysEnv,
				                                      dd_n.getId(sysEnv),
				                                      ds.getEsdId(sysEnv),
				                                      ds.getCondition(sysEnv)
				                                     );
			}

		}
		Vector v_sh = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, id);
		Iterator i_sh = v_sh.iterator();
		while (i_sh.hasNext()) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)i_sh.next();
			final SDMSSchedulingEntity c = SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeChildId(sysEnv));
			if(!c.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(c.accessViolationMessage(sysEnv, "03402292318"));
			SDMSSchedulingHierarchyTable.table.create(sysEnv,
			                seId,
			                sh.getSeChildId(sysEnv),
			                sh.getAliasName(sysEnv),
			                sh.getIsStatic(sysEnv),
			                sh.getIsDisabled(sysEnv),
			                sh.getPriority(sysEnv),
			                sh.getSuspend(sysEnv),
			                sh.getResumeAt(sysEnv),
			                sh.getResumeIn(sysEnv),
			                sh.getResumeBase(sysEnv),
			                sh.getMergeMode(sysEnv),
			                sh.getEstpId(sysEnv)
			                                         );
		}
		boolean testInverse = false;
		Vector v_tr = SDMSTriggerTable.idx_fireId.getVector(sysEnv, id);
		do {
			Iterator i_tr = v_tr.iterator();
			while (i_tr.hasNext()) {
				SDMSTrigger tr_o = (SDMSTrigger)i_tr.next();
				Boolean isInverse = tr_o.getIsInverse(sysEnv);
				if (isInverse.booleanValue() == testInverse) {
					SDMSTrigger tr_n = SDMSTriggerTable.table.create(sysEnv,
					                   tr_o.getName(sysEnv),
					                   testInverse ? tr_o.getFireId(sysEnv) : seId,
					                   tr_o.getObjectType(sysEnv),
					                   testInverse ? seId : tr_o.getSeId(sysEnv),
					                   tr_o.getMainSeId(sysEnv),
					                   tr_o.getParentSeId(sysEnv),
					                   tr_o.getIsActive(sysEnv),
					                   tr_o.getIsInverse(sysEnv),
					                   tr_o.getAction(sysEnv),
					                   tr_o.getType(sysEnv),
					                   tr_o.getIsMaster(sysEnv),
					                   tr_o.getIsSuspend(sysEnv),
					                   tr_o.getIsCreate(sysEnv),
					                   tr_o.getIsChange(sysEnv),
					                   tr_o.getIsDelete(sysEnv),
					                   tr_o.getIsGroup(sysEnv),
					                   tr_o.getResumeAt(sysEnv),
					                   tr_o.getResumeIn(sysEnv),
					                   tr_o.getResumeBase(sysEnv),
					                   tr_o.getIsWarnOnLimit(sysEnv),
					                   tr_o.getLimitState(sysEnv),
					                   tr_o.getMaxRetry(sysEnv),
					                   tr_o.getSubmitOwnerId(sysEnv),
					                   tr_o.getCondition(sysEnv),
					                   tr_o.getCheckAmount(sysEnv),
					                   tr_o.getCheckBase(sysEnv)
					                                                );
					Long tr_nId = tr_n.getId(sysEnv);
					Vector v_trs = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, tr_o.getId(sysEnv));
					Iterator i_trs = v_trs.iterator();
					while (i_trs.hasNext()) {
						SDMSTriggerState trs = (SDMSTriggerState)i_trs.next();
						SDMSTriggerStateTable.table.create(sysEnv,
						                                   tr_nId,
						                                   trs.getFromStateId(sysEnv),
						                                   trs.getToStateId(sysEnv)
						                                  );
					}
					Vector v_trp = SDMSTriggerParameterTable.idx_triggerId.getVector(sysEnv, tr_o.getId(sysEnv));
					Iterator i_trp = v_trp.iterator();
					while (i_trp.hasNext()) {
						SDMSTriggerParameter trp = (SDMSTriggerParameter) i_trp.next();
						SDMSTriggerParameterTable.table.create(sysEnv,
						                                       trp.getName(sysEnv),
						                                       trp.getExpression(sysEnv),
						                                       tr_nId
						                                      );
					}
				}
			}
			if (!testInverse)
				v_tr = SDMSTriggerTable.idx_seId.getVector(sysEnv, id);
			testInverse = !testInverse;
		} while(testInverse);
		Vector v_rt = SDMSResourceTemplateTable.idx_seId.getVector(sysEnv, id);
		Iterator i_rt = v_rt.iterator();
		while(i_rt.hasNext()) {
			SDMSResourceTemplate rt_o = (SDMSResourceTemplate) i_rt.next();
			SDMSResourceTemplate rt_n = SDMSResourceTemplateTable.table.create(sysEnv,
			                            rt_o.getNrId(sysEnv),
			                            seId,
			                            rt_o.getOwnerId(sysEnv),
			                            rt_o.getRsdId(sysEnv),
			                            rt_o.getRequestableAmount(sysEnv),
			                            rt_o.getAmount(sysEnv),
			                            rt_o.getIsOnline(sysEnv)
			                                                                  );
			rt_o.copyVariables(sysEnv, rt_n.getId(sysEnv));
		}

		Vector ocv = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, id);
		for (int ii = 0; ii < ocv.size(); ++ii) {
			SDMSObjectComment oc = (SDMSObjectComment) ocv.get(ii);
			SDMSObjectCommentTable.table.create(sysEnv,
			                                    seId,
			                                    oc.getObjectType(sysEnv),
			                                    oc.getInfoType(sysEnv),
			                                    oc.getSequenceNumber(sysEnv),
			                                    oc.getTag(sysEnv),
			                                    oc.getDescription(sysEnv)
			                                   );
		}
		return se;
	}

	public void relocateDetails (SystemEnvironment sysEnv, HashMap relocationTable)
	throws SDMSException
	{
		Long id = getId(sysEnv);
		Vector v_dd = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, id);
		Iterator i_dd = v_dd.iterator();
		while (i_dd.hasNext()) {
			SDMSDependencyDefinition dd = (SDMSDependencyDefinition)i_dd.next();
			Long requiredId = (Long)relocationTable.get(dd.getSeRequiredId(sysEnv));
			if (requiredId != null) {
				dd.setSeRequiredId(sysEnv, requiredId);
			}
		}
		Vector v_sh = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, id);
		Iterator i_sh = v_sh.iterator();
		while (i_sh.hasNext()) {
			SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy)i_sh.next();
			Long childId = (Long)relocationTable.get(sh.getSeChildId(sysEnv));
			if (childId != null) {
				sh.setSeChildId(sysEnv, childId);
			}
		}
		Vector v_pd = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, id);
		Iterator i_pd = v_pd.iterator();
		while (i_pd.hasNext()) {
			SDMSParameterDefinition pd = (SDMSParameterDefinition)i_pd.next();
			Long linkId = (Long)relocationTable.get(pd.getLinkPdId(sysEnv));
			if (linkId != null) {
				pd.setLinkPdId(sysEnv, linkId);
			}
		}
		Vector v_tr = SDMSTriggerTable.idx_fireId.getVector(sysEnv, id);
		Iterator i_tr = v_tr.iterator();
		while (i_tr.hasNext()) {
			SDMSTrigger tr = (SDMSTrigger)i_tr.next();
			Long seId = (Long)relocationTable.get(tr.getSeId(sysEnv));
			if (seId != null) {
				tr.setSeId(sysEnv, seId);
			}
		}
		v_tr = SDMSTriggerTable.idx_seId.getVector(sysEnv, id);
		i_tr = v_tr.iterator();
		while (i_tr.hasNext()) {
			SDMSTrigger tr = (SDMSTrigger)i_tr.next();
			Long fireId = (Long)relocationTable.get(tr.getFireId(sysEnv));
			if (fireId != null) {
				tr.setFireId(sysEnv, fireId);
			}
		}
		Vector v_rr = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, id);
		Iterator i_rr = v_rr.iterator();
		while (i_rr.hasNext()) {
			SDMSResourceRequirement rr = (SDMSResourceRequirement)i_rr.next();
			Long stickyParent = rr.getStickyParent(sysEnv);
			if (stickyParent != null) {
				stickyParent = (Long)relocationTable.get(stickyParent);
				if (stickyParent != null) {
					rr.setStickyParent(sysEnv, stickyParent);
				}
			}
		}
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
		if(sysEnv.cEnv.isUser() && p != checkPrivs) {
			long inheritPrivs = getInheritPrivs(sysEnv).longValue();
			if ((checkPrivs & SDMSPrivilege.CREATE_PARENT_CONTENT) != 0) inheritPrivs = inheritPrivs | SDMSPrivilege.CREATE_PARENT_CONTENT;
			long missingPrivs = checkPrivs & (~p) & inheritPrivs;
			if ((p | missingPrivs) != checkPrivs && fastFail) return 0L;

			Long parentId = getFolderId(sysEnv);
			if(parentId != null && missingPrivs != 0) {
				SDMSFolder po = SDMSFolderTable.getObject(sysEnv, parentId);
				long parentPrivs = ((missingPrivs & SDMSPrivilege.CREATE_PARENT_CONTENT) != 0) ?
				                   (missingPrivs & (~SDMSPrivilege.CREATE_PARENT_CONTENT)) | SDMSPrivilege.CREATE_CONTENT : missingPrivs;
				parentPrivs = po.getPrivileges(sysEnv, parentPrivs, fastFail, checkGroups);
				if ((parentPrivs & SDMSPrivilege.CREATE_CONTENT) != 0)
					parentPrivs = parentPrivs | SDMSPrivilege.CREATE_PARENT_CONTENT;
				p = p | parentPrivs & inheritPrivs;
			}
			p = p & checkPrivs;
		}
		return p;
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups, long version)
	throws SDMSException
	{
		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups, version);
		if(sysEnv.cEnv.isUser() && p != checkPrivs) {
			long inheritPrivs = 0;
			try {
				SDMSSchedulingEntity act = SDMSSchedulingEntityTable.getObject(sysEnv, getId(sysEnv));
				inheritPrivs = act.getInheritPrivs(sysEnv).longValue();
			} catch (NotFoundException nfe) {

			}
			if ((checkPrivs & SDMSPrivilege.CREATE_PARENT_CONTENT) != 0) inheritPrivs = inheritPrivs | SDMSPrivilege.CREATE_PARENT_CONTENT;
			long missingPrivs = checkPrivs & (~p) & inheritPrivs;
			if ((p | missingPrivs) != checkPrivs && fastFail) return 0L;

			Long parentId = getFolderId(sysEnv);
			if(parentId != null && missingPrivs != 0) {
				SDMSFolder po = SDMSFolderTable.getObject(sysEnv, parentId, version);
				long parentPrivs = ((missingPrivs & SDMSPrivilege.CREATE_PARENT_CONTENT) != 0) ?
				                   (missingPrivs & (~SDMSPrivilege.CREATE_PARENT_CONTENT)) | SDMSPrivilege.CREATE_CONTENT : missingPrivs;
				parentPrivs = po.getPrivileges(sysEnv, parentPrivs, fastFail, checkGroups, version);
				if ((parentPrivs & SDMSPrivilege.CREATE_CONTENT) != 0)
					parentPrivs = parentPrivs | SDMSPrivilege.CREATE_PARENT_CONTENT;
				p = p | parentPrivs & inheritPrivs;
			}
			if((p & SDMSPrivilege.SUBMIT) == SDMSPrivilege.SUBMIT) p = p | SDMSPrivilege.VIEW;
			if((p & SDMSPrivilege.OPERATE) == SDMSPrivilege.OPERATE) p = p | SDMSPrivilege.MONITOR;
			p = p & checkPrivs;
		}
		return p;
	}

	public void checkSubmitForGroup (SystemEnvironment sysEnv, Long gId)
	throws SDMSException
	{
		final Long uId = sysEnv.cEnv.uid();
		if (sysEnv.cEnv.isUser()) {
			if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, uId))) {
				if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(gId, uId))) {
					final SDMSUser  u = SDMSUserTable.getObject(sysEnv, uId);
					final SDMSGroup g = SDMSGroupTable.getObject(sysEnv, gId);
					throw new CommonErrorException(new SDMSMessage(sysEnv, "02402180823",
					                               "User $1 does not belong to Group $2", u.getName(sysEnv), g.getName(sysEnv)));
				}
				final Long oId = getOwnerId(sysEnv);
				if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(oId, uId))) {
					Vector checkGroups = new Vector();
					checkGroups.add(gId);
					SDMSPrivilege grp = getPrivilegesForGroups(sysEnv, checkGroups);
					if (!grp.can(SDMSPrivilege.SUBMIT)) {
						final SDMSUser  u = SDMSUserTable.getObject(sysEnv, uId);
						final SDMSGroup g = SDMSGroupTable.getObject(sysEnv, gId);
						throw new CommonErrorException(
						        new SDMSMessage(sysEnv, "02402180938", "User $1 not allowed to submit $2 for group $3",
						                        u.getName(sysEnv), pathString(sysEnv), g.getName(sysEnv)));
					}
				}
			}
		} else {
			Vector checkGroups = new Vector();
			checkGroups.add(gId);
			checkGroups.add(SDMSObject.publicGId);
			sysEnv.cEnv.setUser();
			try {
				SDMSPrivilege grp = getPrivilegesForGroups(sysEnv, checkGroups);
				if (!grp.can(SDMSPrivilege.SUBMIT)) {
					final SDMSGroup g = SDMSGroupTable.getObject(sysEnv, gId);
					throw new CommonErrorException(
					        new SDMSMessage(sysEnv, "03801291413", "Job $1 not allowed to submit $2 from group $3",
					                        uId, pathString(sysEnv), g.getName(sysEnv)));
				}
			} catch (SDMSException e) {
				throw e;
			} finally {
				sysEnv.cEnv.setJob();
			}
		}
	}

	public void removeFromTimeScheduling(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long seId = getId(sysEnv);

		Vector ev = SDMSEventTable.idx_seId.getVector(sysEnv, seId);
		for(int i = 0; i < ev.size(); i++) {
			SDMSEvent e = (SDMSEvent) ev.get(i);
			Vector scev = SDMSScheduledEventTable.idx_evtId.getVector(sysEnv, e.getId(sysEnv));
			for(int j = 0; j < scev.size(); j++) {
				SDMSScheduledEvent sce = (SDMSScheduledEvent) scev.get(j);
				sce.delete(sysEnv);
			}
			e.delete(sysEnv);
		}
		Vector sv = null;
		boolean done = false;
		while (!done) {
			done = true;
			sv = SDMSScheduleTable.idx_seId.getVector(sysEnv, seId);
			for(int i = 0; i < sv.size(); i++) {
				SDMSSchedule s = (SDMSSchedule) sv.get(i);
				Long sId = s.getId(sysEnv);
				if (SDMSScheduledEventTable.idx_sceId.containsKey (sysEnv, sId)) continue;
				if (SDMSScheduleTable.idx_parentId.containsKey (sysEnv, sId)) continue;
				s.delete(sysEnv);
				done = false;
			}
		}
		if (sv != null) {
			for(int i = 0; i < sv.size(); i++) {
				SDMSSchedule s = (SDMSSchedule) sv.get(i);
				s.setSeId(sysEnv, null);
			}
		}
		Vector iv = null;
		done = false;
		while (!done) {
			done = true;
			iv = SDMSIntervalTable.idx_seId.getVector(sysEnv, seId);
			for(int j = 0; j < iv.size(); j++) {
				SDMSInterval i = (SDMSInterval) iv.get(j);
				Long iId = i.getId(sysEnv);
				if (SDMSIntervalTable.idx_embeddedIntervalId.containsKey (sysEnv, iId)) continue;
				if (SDMSScheduleTable.idx_intId.containsKey (sysEnv, iId)) {
					i.setSeId(sysEnv, null);
					continue;
				}
				if (SDMSIntervalHierarchyTable.idx_childId.containsKey (sysEnv, iId)) continue;
				IntervalUtil.killFilter (sysEnv, iId);
				IntervalUtil.killSelections (sysEnv, iId);
				i.delete(sysEnv);
				done = false;
			}
		}
		if (iv != null) {
			for(int j = 0; j < iv.size(); j++) {
				SDMSInterval i = (SDMSInterval) iv.get(j);
				i.setSeId(sysEnv, null);
			}
		}
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		delete (sysEnv, false);
	}

	private void doDelete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		super.delete (sysEnv);
	}

	public void delete(SystemEnvironment sysEnv, boolean force)
	throws SDMSException
	{
		HashSet<Long> seIds = new HashSet<Long>();
		seIds.add(getId(sysEnv));
		delete (sysEnv, seIds, force);
	}

	public static void delete(SystemEnvironment sysEnv, HashSet<Long> seIds, boolean force)
	throws SDMSException
	{
		Iterator<Long> i_se = seIds.iterator();
		while (i_se.hasNext()) {

			Long seId = i_se.next();
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId);

			final Vector ddrv = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, seId);
			for(int i = 0; i < ddrv.size(); i++) {
				((SDMSDependencyDefinition) ddrv.get(i)).delete(sysEnv);
			}

			Vector dddv = SDMSDependencyDefinitionTable.idx_seRequiredId.getVector(sysEnv, seId);
			for(int i = 0; i < dddv.size(); i++) {
				SDMSDependencyDefinition dd = (SDMSDependencyDefinition) dddv.get(i);
				if (seIds.contains(dd.getSeDependentId(sysEnv)))
					continue;
				if (!force)
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03112202113",
					                               "$1 is required by $2, specify force to delete anyway",
					                               se.pathString(sysEnv),
					                               SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeDependentId(sysEnv)).pathString(sysEnv)));
				dd.delete(sysEnv);
			}

			final Vector shcv = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, seId);
			for(int i = 0; i < shcv.size(); i++) {
				final SDMSSchedulingHierarchy shc = (SDMSSchedulingHierarchy) shcv.get(i);
				shc.delete(sysEnv);
			}

			final Vector shpv = SDMSSchedulingHierarchyTable.idx_seChildId.getVector(sysEnv, seId);
			for(int i = 0; i < shpv.size(); i++) {
				SDMSSchedulingHierarchy sh = (SDMSSchedulingHierarchy) shpv.get(i);
				if (seIds.contains(sh.getSeParentId(sysEnv)))
					continue;
				if (!force)
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03207042302",
					                               "$1 is used as a child of $2, specify force to delete anyway",
					                               se.pathString(sysEnv),
					                               SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeParentId(sysEnv)).pathString(sysEnv)));
				sh.delete(sysEnv);
			}

			final Vector rrv = SDMSResourceRequirementTable.idx_seId.getVector(sysEnv, seId);
			for(int i = 0; i < rrv.size(); i++) {
				((SDMSResourceRequirement) rrv.get(i)).delete(sysEnv);
			}

			final Vector pdv = SDMSParameterDefinitionTable.idx_seId.getVector(sysEnv, seId);
			for(int i = 0; i < pdv.size(); i++) {
				final SDMSParameterDefinition pd = (SDMSParameterDefinition) pdv.get(i);
				pd.delete(sysEnv, seIds, force);
			}

			final Vector ftv = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
			for(int i = 0; i < ftv.size(); i++) {
				final SDMSTrigger t = (SDMSTrigger) ftv.get(i);
				if (t.getIsInverse(sysEnv).booleanValue()) {
					if (seIds.contains(t.getSeId(sysEnv)))
						continue;
					if (!force)
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03207042303",
						                               "$1 is used as triggering job of $2, specify force to delete anyway",
						                               se.pathString(sysEnv),
						                               SDMSSchedulingEntityTable.getObject(sysEnv, t.getSeId(sysEnv)).pathString(sysEnv)));
				}
				t.delete(sysEnv);
			}

			final Vector tv = SDMSTriggerTable.idx_seId.getVector(sysEnv, seId);
			for(int i = 0; i < tv.size(); i++) {
				final SDMSTrigger t = (SDMSTrigger) tv.get(i);
				if (!t.getIsInverse(sysEnv).booleanValue()) {
					if (seIds.contains(t.getFireId(sysEnv)))
						continue;
					if (!force)
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03207042304",
						                               "$1 triggered by $2, specify force to delete anyway",
						                               se.pathString(sysEnv),
						                               SDMSSchedulingEntityTable.getObject(sysEnv, t.getFireId(sysEnv)).pathString(sysEnv)));
				}
				t.delete(sysEnv);
			}

			final Vector mtv = SDMSTriggerTable.idx_mainSeId.getVector(sysEnv, seId);
			for(int i = 0; i < mtv.size(); i++) {
				final SDMSTrigger t = (SDMSTrigger) mtv.get(i);
				t.delete(sysEnv);
			}

			final Vector ptv = SDMSTriggerTable.idx_parentSeId.getVector(sysEnv, seId);
			for(int i = 0; i < ptv.size(); i++) {
				final SDMSTrigger t = (SDMSTrigger) ptv.get(i);
				t.delete(sysEnv);
			}

			final Vector rtv = SDMSResourceTemplateTable.idx_seId.getVector(sysEnv, seId);
			for(int i = 0; i < rtv.size(); i++) {
				final SDMSResourceTemplate rt = (SDMSResourceTemplate) rtv.get(i);
				rt.delete(sysEnv);
			}

			se.removeFromTimeScheduling(sysEnv);

			se.doDelete(sysEnv);
		}
	}
}
