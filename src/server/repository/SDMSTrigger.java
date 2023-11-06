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
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.parser.triggerexpr.*;

public class SDMSTrigger extends SDMSTriggerProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSTrigger.java,v 2.42.2.4 2013/03/22 14:01:55 dieter Exp $";

	private final static HashMap mapper = new HashMap();

	static
	{
		mapper.put(new Integer(Parser.IMMEDIATE_LOCAL),		new Integer(SDMSTrigger.IMMEDIATE_LOCAL));
		mapper.put(new Integer(Parser.IMMEDIATE_MERGE),		new Integer(SDMSTrigger.IMMEDIATE_MERGE));
		mapper.put(new Integer(Parser.BEFORE_FINAL),		new Integer(SDMSTrigger.BEFORE_FINAL));
		mapper.put(new Integer(Parser.AFTER_FINAL),		new Integer(SDMSTrigger.AFTER_FINAL));
		mapper.put(new Integer(Parser.FINISH_CHILD),		new Integer(SDMSTrigger.FINISH_CHILD));
		mapper.put(new Integer(Parser.UNTIL_FINISHED),		new Integer(SDMSTrigger.UNTIL_FINISHED));
		mapper.put(new Integer(Parser.UNTIL_FINAL),		new Integer(SDMSTrigger.UNTIL_FINAL));
		mapper.put(new Integer(Parser.WARNING),			new Integer(SDMSTrigger.WARNING));

		mapper.put(new Integer(Parser.CREATE),			new Integer(SDMSTrigger.CREATE));
		mapper.put(new Integer(Parser.CHANGE),			new Integer(SDMSTrigger.CHANGE));
		mapper.put(new Integer(Parser.DELETE),			new Integer(SDMSTrigger.DELETE));
	}

	protected SDMSTrigger(SDMSObject p_object)
	{
		super(p_object);
	}

	public static Integer map(Integer p)
	{
		return (Integer) mapper.get(p);
	}

	public void delete(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		Vector act_ts = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, id);
		for(int i = 0; i < act_ts.size(); i++) {
			SDMSTriggerState ts = (SDMSTriggerState) act_ts.get(i);
			ts.delete(sysEnv);
		}
		Vector tpv = SDMSTriggerParameterTable.idx_triggerId.getVector(sysEnv, id);
		for (int i = 0; i < tpv.size(); ++i) {
			SDMSTriggerParameter tp = (SDMSTriggerParameter) tpv.get(i);
			tp.delete(sysEnv);
		}

		super.delete(sysEnv);
	}

	public boolean checkCondition(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, SDMSTriggerQueue tq)
		throws SDMSException
	{
		return checkCondition(sysEnv, null, sme, tq);
	}

	public boolean checkCondition(SystemEnvironment sysEnv, SDMSResource r)
		throws SDMSException
	{
		return checkCondition(sysEnv, r, null, null);
	}

	private boolean checkCondition(SystemEnvironment sysEnv, SDMSResource r, SDMSSubmittedEntity sme, SDMSTriggerQueue tq)
		throws SDMSException
	{
		final String cond = getCondition(sysEnv);

		if(cond == null) return true;

		final BoolExpr be = new BoolExpr(cond);

		return be.checkCondition(sysEnv, r, sme, sme, this, tq, null);
	}

	public void checkConditionSyntax(SystemEnvironment sysEnv)
		throws SDMSException
	{
		String cond = getCondition(sysEnv);
		if(cond == null) return;
		final BoolExpr be = new BoolExpr(cond);

		be.checkConditionSyntax(sysEnv);
	}

	public void checkParameterExpressionSyntax(SystemEnvironment sysEnv, String expr)
	throws SDMSException
	{
		if (expr == null) return;
		final BoolExpr be = new BoolExpr(expr);
		be.checkConditionSyntax(sysEnv);
	}

	public String evalExpression(SystemEnvironment sysEnv, String expression, SDMSResource r, SDMSSubmittedEntity sme, SDMSTriggerQueue tq)
	throws SDMSException
	{
		if (expression == null || expression.equals("")) return "";
		BoolExpr be = new BoolExpr(expression);

		Object rc = be.evalExpression(sysEnv, r, sme, sme, this, tq, null);
		return rc.toString();
	}

	public boolean trigger(SystemEnvironment sysEnv, Long esdId, Long reasonSmeId, SDMSTriggerQueue tq, SDMSSubmittedEntity thisSme)
		throws SDMSException
	{
		if (!getIsActive(sysEnv).booleanValue()) return false;
		if (thisSme.getIsDisabled(sysEnv).booleanValue()) return false;

		SDMSSubmittedEntity sme;
		long seVersion = thisSme.getSeVersion(sysEnv).longValue();
		boolean fired = false;
		Long fireSeId = thisSme.getSeId(sysEnv);
		Long id = thisSme.getId(sysEnv);
		int trigger_type = getType(sysEnv).intValue();
		Long trId = getId(sysEnv);
		long now = System.currentTimeMillis();
		int action = getAction(sysEnv).intValue();

		HashSet ths = (HashSet)sysEnv.tx.txData.get(SystemEnvironment.S_TRIGGER_HASHSET);
		if (ths == null) {
			ths = new HashSet();
			sysEnv.tx.txData.put(SystemEnvironment.S_TRIGGER_HASHSET, ths);
		}
		boolean isMasterTrigger = getIsMaster(sysEnv).booleanValue();

		Long warnLink = null;
		Vector v_trs = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, trId, seVersion);
		if (v_trs.size() > 0) {
			Iterator i_trs = v_trs.iterator();
			boolean found = false;
			while (i_trs.hasNext()) {
				SDMSTriggerState trs = (SDMSTriggerState)i_trs.next();
				if (trs.getToStateId(sysEnv).equals(esdId)) {
					found = true;
					break;
				}
			}
			if (!found) return fired;
		}
		if (trigger_type == SDMSTrigger.FINISH_CHILD) {
			SDMSKey thskey = new SDMSKey(reasonSmeId, id, trId);
			if (!ths.add(thskey)) {
				return fired;
			}
		}

		if (trigger_type == SDMSTrigger.UNTIL_FINISHED || trigger_type == SDMSTrigger.UNTIL_FINAL) {
			if(tq == null) {
				tq = SDMSTriggerQueueTable.idx_smeId_trId_getUnique(sysEnv, new SDMSKey(id, trId));
			}
			long checkAmount = getCheckAmount(sysEnv).longValue();
			int checkBase = getCheckBase(sysEnv).intValue();
			switch(checkBase) {
				case SDMSInterval.MINUTE:
					checkAmount *= SDMSInterval.MINUTE_DUR;
					break;
				case SDMSInterval.HOUR:
					checkAmount *= SDMSInterval.HOUR_DUR;
					break;
				case SDMSInterval.DAY:
					checkAmount *= SDMSInterval.DAY_DUR;
					break;
				case SDMSInterval.WEEK:
					checkAmount *= SDMSInterval.WEEK_DUR;
					break;
			}

			tq.setNextTriggerTime(sysEnv, new Long(now + checkAmount));
		}
		int maxTrSeq = 0;
		Vector v_f_sme;
		if (trigger_type != SDMSTrigger.AFTER_FINAL) {
			if (trigger_type == SDMSTrigger.UNTIL_FINISHED || trigger_type == SDMSTrigger.UNTIL_FINAL) {
				maxTrSeq = tq.getTimesTriggered(sysEnv).intValue();
			} else {
				if (action != RERUN) {
					v_f_sme = SDMSSubmittedEntityTable.idx_fireSmeId_trId.getVector(sysEnv,
					                new SDMSKey(id, trId));
					maxTrSeq = v_f_sme.size();
				} else {
					maxTrSeq = thisSme.getRerunSeq(sysEnv);
				}
			}
		} else {
			if (!isMasterTrigger) {

				SDMSSubmittedEntity tmpSme = thisSme;
				Long replSmeId = tmpSme.getFireSmeId(sysEnv);
				Long replTrId = tmpSme.getTrId(sysEnv);
				while (replSmeId != null && trId.equals(replTrId)) {
					maxTrSeq++;
					tmpSme = SDMSSubmittedEntityTable.getObject(sysEnv, replSmeId);
					replSmeId = tmpSme.getFireSmeId(sysEnv);
					replTrId = tmpSme.getTrId(sysEnv);
				}
			} else {
				maxTrSeq = 0;
			}
		}

		int maxRetry = getMaxRetry(sysEnv).intValue();
		if (action != RERUN || !getIsSuspend(sysEnv).booleanValue()) {
			if (maxRetry == 0)
				maxRetry = SystemEnvironment.triggerSoftLimit;
			if (maxRetry > SystemEnvironment.triggerHardLimit)
				maxRetry = SystemEnvironment.triggerHardLimit;
		}

		if (maxTrSeq >= maxRetry && maxRetry != 0) {
			Long limitState = getLimitState(sysEnv);
			if (limitState != null) {
				Long seId = thisSme.getSeId(sysEnv);
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
				Long espId = se.getEspId(sysEnv);
				SDMSExitState es;
				try {
					es = SDMSExitStateTable.idx_espId_esdId_getUnique(sysEnv, new SDMSKey(espId, limitState), seVersion);
				} catch (NotFoundException nfe) {
					SDMSExitStateDefinition esd = SDMSExitStateDefinitionTable.getObject(sysEnv, limitState);
					String msg = "The limit state " + esd.getName(sysEnv) + " of trigger " + this.getName(sysEnv) + " isn't contained in the job's exit state profile";
					HashSet trBrokenEsdIdSet = (HashSet) sysEnv.tx.txData.get(SystemEnvironment.S_TRIGGER_BROKENESDID);
					if (trBrokenEsdIdSet == null) {
						trBrokenEsdIdSet = new HashSet();
						sysEnv.tx.txData.put(SystemEnvironment.S_TRIGGER_BROKENESDID, trBrokenEsdIdSet);
					}
					trBrokenEsdIdSet.add(thisSme.getId(sysEnv));
					thisSme.setToError(sysEnv, msg);
					SDMSThread.doTrace(sysEnv.cEnv, "Definition error in job definition " + se.pathString(sysEnv) + ": " + msg, SDMSThread.SEVERITY_WARNING);
					return fired;
				}
				thisSme.changeState(sysEnv, limitState, es, null, null, null, false);
			}
			return fired;
		}

		boolean conditionOK = false;
		try {
			conditionOK = checkCondition(sysEnv, thisSme, tq);
		} catch(CommonErrorException cee) {
			java.util.Date dts = new java.util.Date();
			Long ts = new Long (dts.getTime());
			boolean doTrigger = true;
		}

		if(tq != null) tq.setTimesChecked(sysEnv, new Integer(tq.getTimesChecked(sysEnv).intValue() + 1));
		if(conditionOK) {

			Integer trSeq = new Integer(maxTrSeq + 1);
			if (action == SUBMIT) {
				sme = triggerSubmit(sysEnv, thisSme, trSeq, trigger_type);
				if(sme == null) return fired;
				fired = true;

				Long baseSmeId = (Long)(sysEnv.tx.txData.get(SystemEnvironment.S_BASE_SME_ID));
				if (baseSmeId == null)
					baseSmeId = reasonSmeId;
				sme.setBaseSmeId(sysEnv, baseSmeId);
				sme.setReasonSmeId(sysEnv, reasonSmeId);
				sme.setFireSmeId(sysEnv, id);
				sme.setFireSeId(sysEnv, fireSeId);
				sme.setTrId(sysEnv, trId);
				sme.setTrSdIdNew(sysEnv, esdId);
				sme.setTrSeq(sysEnv, trSeq);
				sme.setWarnLink(sysEnv, warnLink);
				if(tq != null) tq.setTimesTriggered(sysEnv, trSeq);
				Long resumeTs = null;
				if (getIsSuspend(sysEnv).booleanValue()) {
					Long submitTs = sme.getSubmitTs(sysEnv);
					resumeTs = SubmitJob.evalResumeObj(sysEnv, getResumeAt(sysEnv), getResumeIn(sysEnv), getResumeBase(sysEnv),
					                                   submitTs, true, sme.getEffectiveTimeZone(sysEnv));
					sme.setResumeTs(sysEnv, resumeTs);
				}
			} else {
				thisSme.rerun(sysEnv);
				Long resumeTs = null;
				if (getIsSuspend(sysEnv).booleanValue()) {
					thisSme.suspend(sysEnv, true, false);
					Long finishTs = thisSme.getFinishTs(sysEnv);
					resumeTs = SubmitJob.evalResumeObj(sysEnv, getResumeAt(sysEnv), getResumeIn(sysEnv), getResumeBase(sysEnv),
					                                   finishTs, true, thisSme.getEffectiveTimeZone(sysEnv));
					thisSme.setResumeTs(sysEnv, resumeTs);
				}
			}
		}

		return fired;
	}

	public SDMSSubmittedEntity triggerSubmit(SystemEnvironment sysEnv, SDMSSubmittedEntity thisSme, Integer trSeq, int trigger_type)
		throws SDMSException
	{
		java.util.Date dts = new java.util.Date();
		Long ts = new Long (dts.getTime());
		Long trId = getId(sysEnv);

		SDMSSubmittedEntity sme;
		SDMSSubmittedEntity psme = null;
		SDMSSchedulingEntity se;
		Long submitSeId = getSeId(sysEnv);
		Long fireSeId = thisSme.getSeId(sysEnv);
		long seVersion = thisSme.getSeVersion(sysEnv).longValue();
		boolean isMasterTrigger = getIsMaster(sysEnv).booleanValue();
		String childTag = null;

		HashSet mths = (HashSet)sysEnv.tx.txData.get(SystemEnvironment.S_MASTERTRIGGER_HASHSET);
		if (mths == null) {
			mths = new HashSet();
			sysEnv.tx.txData.put(SystemEnvironment.S_MASTERTRIGGER_HASHSET, mths);
		}

		if(isMasterTrigger) {
			try {
				se = SDMSSchedulingEntityTable.getObject(sysEnv, submitSeId);
			} catch (NotFoundException nfe) {
				return null;
			}
		} else {
			se = SDMSSchedulingEntityTable.getObject(sysEnv, submitSeId, seVersion);

			boolean isAft = false;
			isAft = true;
			if (trigger_type == SDMSTrigger.AFTER_FINAL) {
				Long parentId = thisSme.getParentId(sysEnv);
				if (parentId == null) {
					return null;
				}

				psme = SDMSSubmittedEntityTable.getObject(sysEnv, thisSme.getParentId(sysEnv));
			} else {
				psme = thisSme;
			}

			childTag = thisSme.getId(sysEnv).toString() + '.' + getId(sysEnv).toString() + ':' + trSeq.toString();
		}

		sysEnv.tx.beginSubTransaction(sysEnv);
		try {
			if (!mths.add(trId)) {
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03305141915",
					"Cannot fire Trigger $1 recursively in same Transaction",
					getName(sysEnv)));
			}
			Vector params = new Vector();
			Vector tpdv = SDMSTriggerParameterTable.idx_triggerId.getVector(sysEnv, trId, thisSme.getSeVersion(sysEnv));
			Iterator tpdi = tpdv.iterator();
			while (tpdi.hasNext()) {
				SDMSTriggerParameter tdp = (SDMSTriggerParameter) tpdi.next();
				String expression = tdp.getExpression(sysEnv);
				String result = this.evalExpression(sysEnv, expression, null, thisSme, null);
				WithItem p = new WithItem(tdp.getName(sysEnv), result);
				params.add(p);
			}

			Boolean suspend = getIsSuspend(sysEnv);
			Integer doSuspend;
			if (suspend.booleanValue() == false) doSuspend = null;
			else				     doSuspend = new Integer(SDMSSubmittedEntity.SUSPEND);
			if(isMasterTrigger) {
				final SDMSSchedulingEntity thisSe = SDMSSchedulingEntityTable.getObject(sysEnv, thisSme.getSeId(sysEnv), seVersion);
				final SDMSSubmittedEntity masterSme = SDMSSubmittedEntityTable.getObject(sysEnv, thisSme.getMasterId(sysEnv));
				sme = se.submitMaster(sysEnv,
				                      params,
					doSuspend,
					null,
					getSubmitOwnerId(sysEnv),
					new Integer(0),
				                      "Triggered by " + thisSe.pathString(sysEnv, seVersion) + "(" + getName(sysEnv) + "), Job " + thisSme.getId(sysEnv).toString(),
				                      masterSme.getTimeZone(sysEnv));
			} else {
				Long replaceId = null;
				if (submitSeId.equals(fireSeId)) {
					replaceId = thisSme.getId(sysEnv);
				}
				boolean forceChildDef;
				if (trigger_type == SDMSTrigger.AFTER_FINAL) forceChildDef = true;
				else forceChildDef = false;
				sme = psme.submitChild(sysEnv,
					params,
					doSuspend,
					null,
					submitSeId,
					childTag,
					replaceId,
					null,
					forceChildDef
				);
			}
			sme.setBaseSmeId(sysEnv, (Long)(sysEnv.tx.txData.get(SystemEnvironment.S_BASE_SME_ID)));
		} catch (NonRecoverableException nre) {
			sysEnv.tx.rollbackSubTransaction(sysEnv);

			if(!nre.errNumber().equals("03305141915")) {
				if(isMasterTrigger) {
					se.createErrorMaster (
							sysEnv,
							getSubmitOwnerId(sysEnv),
							"Triggered by " + thisSme.getId(sysEnv).toString() + "(" + getName(sysEnv) + ")",
							nre.toString()
					);
				}
			}

			sme =  null;
		}
		if (sme != null) {
			sysEnv.tx.commitSubTransaction(sysEnv);
		}

		mths.remove(trId);

		return sme;
	}

	public void trigger(SystemEnvironment sysEnv, SDMSResource r, Long oldRsdId, Long newRsdId, SDMSSubmittedEntity causeSme)
		throws SDMSException
	{
		if (!getIsActive(sysEnv).booleanValue()) return;

		SDMSSubmittedEntity sme;

		Long causeSmeId = null;
		Long causeSeId = null;
		if(causeSme != null) {
			causeSmeId = causeSme.getId(sysEnv);
			causeSeId = causeSme.getSeId(sysEnv);
		}

		Vector vTriggerStates = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, getId(sysEnv));

		boolean fire = false;
		if(vTriggerStates.size() == 0) fire = true;
		for(int j = 0; j < vTriggerStates.size(); j++) {
			SDMSTriggerState ts = (SDMSTriggerState) vTriggerStates.get(j);
			Long tsFromState = ts.getFromStateId(sysEnv);
			Long tsToState = ts.getToStateId(sysEnv);
			if(tsFromState == null || tsFromState.equals(oldRsdId)) {
				if(tsToState == null || tsToState.equals(newRsdId)) {
					fire = true;
					break;
				}
			}
		}

		if(fire) {
			try {
				fire = checkCondition(sysEnv, r);
			} catch (CommonErrorException cee) {
				fire = false;
			}
		}
		if(fire) {
			sme = null;
			sysEnv.tx.beginSubTransaction(sysEnv);
			try {
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, getSeId(sysEnv));
				sme = se.submitMaster(sysEnv,
					null,
					new Integer(getIsSuspend(sysEnv).booleanValue() ? SDMSSubmittedEntity.SUSPEND : SDMSSubmittedEntity.NOSUSPEND),
					null,
					getSubmitOwnerId(sysEnv),
					new Integer(0),
				                      "Triggered by Resource " + r.getId(sysEnv).toString() + "(" + getName(sysEnv) + ")",
				                      null );

			} catch (NonRecoverableException nre) {
				sysEnv.tx.rollbackSubTransaction(sysEnv);
			}
			if (sme != null) {
				sysEnv.tx.commitSubTransaction(sysEnv);
				sme.setBaseSmeId(sysEnv, causeSmeId);
				sme.setReasonSmeId(sysEnv, causeSmeId);
				sme.setFireSmeId(sysEnv, causeSmeId);
				sme.setFireSeId(sysEnv, causeSeId);
				sme.setTrId(sysEnv, getId(sysEnv));
				sme.setTrSdIdOld(sysEnv, oldRsdId);
				sme.setTrSdIdNew(sysEnv, newRsdId);
				sme.setTrSeq(sysEnv, new Integer(0));
				Long resumeTs = null;
				if (getIsSuspend(sysEnv).booleanValue()) {
					Long submitTs = sme.getSubmitTs(sysEnv);
					resumeTs = SubmitJob.evalResumeObj(sysEnv, getResumeAt(sysEnv), getResumeIn(sysEnv), getResumeBase(sysEnv),
					                                   submitTs, true, sme.getEffectiveTimeZone(sysEnv));
					sme.setResumeTs(sysEnv, resumeTs);
				}
			}
		}
	}

	private void updateTriggeredSme(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		sme.setBaseSmeId(sysEnv, null);
		sme.setReasonSmeId(sysEnv, null);
		sme.setFireSmeId(sysEnv, null);
		sme.setFireSeId(sysEnv, null);
		sme.setTrId(sysEnv, getId(sysEnv));
		sme.setTrSdIdOld(sysEnv, null);
		sme.setTrSdIdNew(sysEnv, null);
		sme.setTrSeq(sysEnv, new Integer(0));
		Long resumeTs = null;
		if (getIsSuspend(sysEnv).booleanValue()) {
			Long submitTs = sme.getSubmitTs(sysEnv);
			resumeTs = SubmitJob.evalResumeObj(sysEnv, getResumeAt(sysEnv), getResumeIn(sysEnv), getResumeBase(sysEnv),
			                                   submitTs, true, sme.getEffectiveTimeZone(sysEnv));
			sme.setResumeTs(sysEnv, resumeTs);
		}

	}

	public String getURLName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSProxy p = null;
		int type = getObjectType(sysEnv).intValue();
		switch (type) {
			case SDMSTrigger.JOB_DEFINITION:
				p = SDMSSchedulingEntityTable.getObject(sysEnv, getFireId(sysEnv));
				break;
			case SDMSTrigger.NAMED_RESOURCE:
				p = SDMSNamedResourceTable.getObject(sysEnv, getFireId(sysEnv));
				break;
			case SDMSTrigger.RESOURCE:
				p = SDMSResourceTable.getObject(sysEnv, getFireId(sysEnv));
				break;
			default:
				throw new FatalException (new SDMSMessage(sysEnv, "0330603241133",
					"Unknown Objecttype in Trigger $1", getName(sysEnv)));
		}
		return getName(sysEnv) + " on " + p.getURL(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return "trigger " + getURLName(sysEnv);
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		Vector groups;
		if (checkGroups == null) groups = new Vector();
		else groups = checkGroups;

		long p = 0;
		p = checkPrivs;
		int objectType = getObjectType(env).intValue();
		SDMSTable t;
		boolean found = false;

		switch (objectType) {
			case JOB_DEFINITION:
				boolean isInverse = getIsInverse(env).booleanValue();
				t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
				try {
					SDMSProxy o = t.get(env, (isInverse ? getSeId(env) : getFireId(env)));
					long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
					if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
						sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
					}
					p = p & sp;
					found = true;
				} catch (NotFoundException nfe) {
					p = 0;
				}
				break;
			case RESOURCE:
				t = SystemEnvironment.repository.getTable(env, SDMSResourceTable.tableName);
				try {
					SDMSProxy o = t.get(env, getFireId(env));
					long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
					if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
						sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
					}
					p = p & sp;
					found = true;
				} catch (NotFoundException nfe) {
					p = 0;
				}
				break;
			case NAMED_RESOURCE:
				t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
				try {
					SDMSProxy o = t.get(env, getFireId(env));
					long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
					if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
						sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
					}
					p = p & sp;
					found = true;
				} catch (NotFoundException nfe) {
					p = 0;
				}
				break;
		}
		if (!found) {
			if(env.tx.mode == SDMSTransaction.READONLY) {
				throw new CommonErrorException (new SDMSMessage(env, "03809050935", "Orphan Trigger found, as ADMIN use 'drop trigger $1' to solve this problem", getId(env).toString()));
			} else {
				if (env.cEnv.gid().contains(SDMSObject.adminGId)) {
					p = checkPrivs;
				}
			}
		}
		return p;
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups, long version)
	throws SDMSException
	{
		Vector groups;
		if (checkGroups == null) groups = new Vector();
		else groups = checkGroups;

		long p = 0;
		int objectType = getObjectType(env).intValue();
		p = checkPrivs;
		SDMSTable t;

		switch (objectType) {
			case JOB_DEFINITION:
				boolean isInverse = getIsInverse(env).booleanValue();
				t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
				try {

					SDMSProxy o = t.get(env, (isInverse ? getSeId(env) : getFireId(env)), version);
					long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
					if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
						sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
					}
					p = p & sp;
				} catch (NotFoundException nfe) {
					p = 0;
				}
				break;
			case RESOURCE:
				t = SystemEnvironment.repository.getTable(env, SDMSResourceTable.tableName);
				try {

					SDMSProxy o = t.get(env, getFireId(env), version);
					long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
					if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
						sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
					}
					p = p & sp;
				} catch (NotFoundException nfe) {
					p = 0;
				}
				break;
			case NAMED_RESOURCE:
				t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
				try {

					SDMSProxy o = t.get(env, getFireId(env), version);
					long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
					if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
						sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
					}
					p = p & sp;
				} catch (NotFoundException nfe) {
					p = 0;
				}
				break;
		}
		return p;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;
		int objectType = getObjectType(env).intValue();

		switch (objectType) {
			case JOB_DEFINITION:
				boolean isInverse = getIsInverse(env).booleanValue();
				t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
				try {
					SDMSProxy p = t.get(env, (isInverse ? getSeId(env) : getFireId(env)));
					p.touch(env);
				} catch (NotFoundException nfe) {
				}
				break;
			case RESOURCE:
				t = SystemEnvironment.repository.getTable(env, SDMSResourceTable.tableName);
				try {
					SDMSProxy p = t.get(env, getFireId(env));
					p.touch(env);
				} catch (NotFoundException nfe) {
				}
				break;
			case NAMED_RESOURCE:
				t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
				try {
					SDMSProxy p = t.get(env, getFireId(env));
					p.touch(env);
				} catch (NotFoundException nfe) {
				}
		}
	}
}
