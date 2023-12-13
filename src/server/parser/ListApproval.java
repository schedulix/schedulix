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
import java.text.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class ListApproval extends Node
{
	private static final String EMPTY = "";
	HashSet idList;

	public ListApproval()
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		idList = null;
	}

	public ListApproval(HashSet idList)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		this.idList = idList;
	}

	private String getAdditionalInfo(SystemEnvironment sysEnv, SDMSSystemMessage msg, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		String retval = null;
		Long seVersion = sme.getSeVersion(sysEnv);

		switch (msg.getOperation(sysEnv).intValue()) {
			case SDMSSystemMessage.CANCEL:
				if (msg.getAdditionalBool(sysEnv).booleanValue())
					retval = "Cancel with Kill";
				else
					retval = "Cancel";
				break;
			case SDMSSystemMessage.RERUN:
				boolean recursive = msg.getAdditionalBool(sysEnv).booleanValue();
				retval = (recursive ? "Recursive" : EMPTY);
				long now = now = System.currentTimeMillis();
				Long suspendFlag = msg.getAdditionalLong(sysEnv);
				Long resumeVal = msg.getSecondLong(sysEnv);
				if (suspendFlag != null) {
					boolean isLocal, isAdmin;
					isLocal = ((suspendFlag.longValue() & 0x02l) != 0);
					isAdmin = ((suspendFlag.longValue() & 0x04l) != 0);
					Long resumeTs = null;
					if (resumeVal != null) {
						if (resumeVal.longValue() < 0) {
							resumeTs = Long.valueOf(now - resumeVal.longValue());
						} else if (resumeVal.longValue() == 0) {
							resumeTs = null;
						} else {
							resumeTs = resumeVal;
						}
					}
					if ((suspendFlag.longValue() & 1l) == 1) {
						if (resumeTs == null || resumeTs.longValue() > now) {
							retval = retval + " Suspend";
							if (resumeTs != 0) {
								retval = retval + ", Resume";
								if (resumeVal.longValue() < 0) {
									retval = retval + " in " + (resumeVal.longValue() / -60000) + " minutes";
								} else {
									Date d = new Date();
									d.setTime(resumeVal);
									SimpleDateFormat sd = (SimpleDateFormat) sysEnv.systemDateFormat.clone();
									sd.setTimeZone(sme.getEffectiveTimeZone(sysEnv));
									retval = retval + " at " + sd.format(d);
								}
							}
						}
					} else {
						retval = retval + " Resume";
					}
				}
				break;
			case SDMSSystemMessage.SET_STATE:
				Long esdId = msg.getAdditionalLong(sysEnv);
				Long actEsdId = sme.getJobEsdId(sysEnv);
				SDMSExitStateDefinition esd = SDMSExitStateDefinitionTable.getObject(sysEnv, esdId, seVersion);
				SDMSExitStateDefinition actEsd = SDMSExitStateDefinitionTable.getObject(sysEnv, actEsdId, seVersion);
				retval = "Current: " + actEsd.getName(sysEnv) + ", new: " + esd.getName(sysEnv) + " " + (msg.getAdditionalBool(sysEnv).booleanValue() ? "[force]" : "");
				break;
			case SDMSSystemMessage.IGN_DEPENDENCY:
				Long diId = msg.getAdditionalLong(sysEnv);
				SDMSDependencyInstance di = SDMSDependencyInstanceTable.getObject(sysEnv, diId);
				final SDMSSubmittedEntity reqSme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getRequiredId(sysEnv));
				Long reqId = di.getRequiredSeId(sysEnv);
				if (reqId == null) {
					reqId = reqSme.getSeId(sysEnv);
				}
				SDMSSchedulingEntity dse = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), seVersion);
				SDMSSchedulingEntity rse = SDMSSchedulingEntityTable.getObject(sysEnv, reqId, seVersion);
				retval = rse.pathString(sysEnv) + " (" + reqSme.getId(sysEnv) + ") -> " + dse.pathString(sysEnv);
				break;
			case SDMSSystemMessage.IGN_RESOURCE:
				Long rId = msg.getAdditionalLong(sysEnv);
				SDMSResource r = SDMSResourceTable.getObject(sysEnv, rId);
				SDMSNamedResource nr = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv));
				String containerPath = null;
				Long scopeId = r.getScopeId(sysEnv);

				try {
					SDMSScope s = SDMSScopeTable.getObject(sysEnv, scopeId);
					containerPath = s.pathString(sysEnv);
				} catch (NotFoundException nfe) {
					try {
						SDMSFolder f = SDMSFolderTable.getObject(sysEnv, scopeId);
						containerPath = f.pathString(sysEnv);
					} catch (NotFoundException nfe2) {
						sme = SDMSSubmittedEntityTable.getObject(sysEnv, scopeId);
						containerPath = sme.getSubmitPathString(sysEnv, true);
					}
				}
				retval = nr.pathString(sysEnv) + " in " + containerPath;
				break;
			case SDMSSystemMessage.CLONE:
				Boolean shouldSuspend = msg.getAdditionalBool(sysEnv);
				retval = (shouldSuspend.booleanValue() ? "SUSPEND" : "");
				break;
			case SDMSSystemMessage.MODIFY_PARAMETER:
				String name = msg.getComment(sysEnv);
				Long evId = msg.getAdditionalLong(sysEnv);
				SDMSEntityVariable ev = SDMSEntityVariableTable.getObject(sysEnv, evId);
				String newVal = ev.getValue(sysEnv);
				String nl = " ";
				if (newVal.contains("\n"))
					nl = "\n";
				if (msg.getIsMandatory(sysEnv).booleanValue()) {
					String oldValue;
					try {
						oldValue = sme.getVariableValue(sysEnv, name, true, ParseStr.S_DEFAULT, false, null, false);
					} catch (SDMSException e) {
						oldValue = "UNKNOWN";
					}
					if (nl.equals(" ") && oldValue.contains("\n"))
						nl = "\n";
					retval = name + " will be changed." + nl + "NEW VALUE =" + nl + "'" + newVal + "',\nOLD VALUE =" + nl + "'" + oldValue + "'";
				} else {
					retval = name + " has been changed." + nl + "NEW VALUE =" + nl + "'" + newVal + "'";
				}
				break;
			case SDMSSystemMessage.KILL:
				Boolean recFlag = msg.getAdditionalBool(sysEnv);
				retval = "KILL" + (recFlag.booleanValue() ? " RECURSIVE" : "");
				break;
			case SDMSSystemMessage.SET_JOB_STATE:
				int status = msg.getAdditionalLong(sysEnv).intValue();
				Long exitCode = msg.getSecondLong(sysEnv);
				retval = "Old state : " + sme.getStateAsString(sysEnv) + ", new state : " + SDMSSubmittedEntity.convertStateToString(status) + ", exit code = " + exitCode;
				break;
			case SDMSSystemMessage.ENABLE:
			case SDMSSystemMessage.DISABLE:
				Boolean isDisable = msg.getAdditionalBool(sysEnv);
				retval = (isDisable.booleanValue() ? "Disable" : "Enable");
				break;
			default:
				retval = "Unknown Operation";
				break;

		}

		return retval;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSSystemMessage o;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("SME_ID");
		desc.add("NAME");
		desc.add("TYPE");
		desc.add("MASTER_ID");
		desc.add("MASTER_NAME");
		desc.add("MASTER_TYPE");
		desc.add("OPERATION");
		desc.add("MODE");
		desc.add("REQUESTING_USER");
		desc.add("REQUEST_TS");
		desc.add("REQUEST_MSG");
		desc.add("ADDITIONAL_INFORMATION");

		d_container = new SDMSOutputContainer(sysEnv, "List of approval requests", desc);

		Iterator i = SDMSSystemMessageTable.table.iterator(sysEnv);
		while(i.hasNext()) {
			Vector v = new Vector();
			o = (SDMSSystemMessage)(i.next());
			if (!o.getMsgType(sysEnv).equals(SDMSSystemMessage.APPROVAL))
				continue;

			Long smeId = o.getSmeId(sysEnv);
			SDMSSubmittedEntity sme;
			try {
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
				if (! sme.getPrivileges(sysEnv).can(SDMSPrivilege.APPROVE))
					continue;
			} catch (NotFoundException nfe) {
				continue;
			}
			SDMSUser reqUser = SDMSUserTable.getObject(sysEnv, o.getRequestUId(sysEnv));
			if (reqUser.getId(sysEnv).equals(sysEnv.cEnv.uid()) && !sysEnv.cEnv.gid().contains(SDMSObject.adminGId))
				continue;

			if (idList != null && !idList.contains(smeId))
				continue;

			v.add(o.getId(sysEnv));
			v.add(smeId);
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
			v.add(se.pathString(sysEnv));
			v.add(se.getTypeAsString(sysEnv));
			v.add(sme.getMasterId(sysEnv));
			if (sme.getMasterId(sysEnv) != smeId) {
				SDMSSubmittedEntity msme = SDMSSubmittedEntityTable.getObject(sysEnv, sme.getMasterId(sysEnv));
				SDMSSchedulingEntity mse = SDMSSchedulingEntityTable.getObject(sysEnv, msme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
				v.add(mse.pathString(sysEnv));
				v.add(mse.getTypeAsString(sysEnv));
			} else {
				v.add(se.pathString(sysEnv));
				v.add(se.getTypeAsString(sysEnv));
			}
			v.add(o.getOperationAsString(sysEnv));
			if (o.getIsMandatory(sysEnv).booleanValue())
				v.add("APPROVAL");
			else
				v.add("REVIEW");
			SDMSUser user = SDMSUserTable.getObject(sysEnv, o.getRequestUId(sysEnv));
			v.add(reqUser.getName(sysEnv));
			Long requestTs = o.getRequestTs(sysEnv);
			Date d = new Date();
			d.setTime(requestTs);
			v.add(sysEnv.systemDateFormat.format(d));
			v.add(o.getRequestMsg(sysEnv));
			v.add(getAdditionalInfo(sysEnv, o, sme));

			d_container.addData(sysEnv, v);
		}

		Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, 1));

		result.setOutputContainer(d_container);

		result.setFeedback(
		        new SDMSMessage(sysEnv, "03106151242", "$1 item(s) to approve found",
		                        Integer.valueOf(d_container.lines)));
	}
}
