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

public class SetParameter extends ManipJob
{

	public final static String __version = "@(#) $Id: SetParameter.java,v 2.4.14.1 2013/03/14 10:24:48 ronald Exp $";

	private WithHash parms;
	private Long jobid;
	private String key;
	private String auditComment;

	public SetParameter(WithHash w)
	{
		super();
		cmdtype = Node.JOB_COMMAND;
		parms = w;
		jobid = null;
		auditComment = null;
	}

	public SetParameter(WithHash w, Long j, String k, WithHash ac)
	{
		super();
		cmdtype = Node.USER_COMMAND;
		parms = w;
		jobid = j;
		key = k;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (jobid == null) jobid = sysEnv.cEnv.uid();
		if (key != null) {
			Connect.validateJobConnect(sysEnv, jobid, key, true);
		}

		final SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobid);
		SDMSPrivilege privs = sme.getPrivileges(sysEnv);

		if (!privs.can(SDMSPrivilege.MODIFY_PARAMETER))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03105201628", "Insufficient privileges to modify job parameters"));

		final Vector keyList = new Vector (parms.keySet());
		final int size = keyList.size();
		for (int i = 0; i < size; ++i) {
			final String name = (String) keyList.get (i);
			final Vector tmp = (Vector) parms.get (name);
			final String value = (String) tmp.get(1);

			if ((cmdtype == Node.JOB_COMMAND)) {
				sme.setVariableValue(sysEnv, name, value);
			} else {
				String oldValue;
				try {
					oldValue = sme.getVariableValue(sysEnv, name, true, ParseStr.S_DEFAULT, false, null, false);
				} catch (SDMSException e) {
					oldValue = "UNKNOWN";
				}
				String nl = " ";
				if (value.contains("\n") || oldValue.contains("\n"))
					nl = "\n";
				final String auditInfo = "Parameter '" + name + "'," + nl + "NEW VALUE =" + nl + "'" + value + "',\nOLD VALUE =" + nl + "'" + oldValue + "'";
				final int baseApprovalBits = sme.getApprovalMode(sysEnv).intValue();
				final int approvalBits = baseApprovalBits & SDMSSubmittedEntity.EDIT_PARM_BITS;
				boolean isApproval = ((approvalBits & SDMSSubmittedEntity.EDIT_PARM_APPROVAL) == SDMSSubmittedEntity.EDIT_PARM_APPROVAL);
				if (approvalBits != 0) {
					SDMSSystemMessage msg = createSystemMessage(sysEnv, SDMSSystemMessage.APPROVAL, sme.getId(sysEnv), sme.getMasterId(sysEnv), SDMSSystemMessage.MODIFY_PARAMETER,
					                        isApproval, sysEnv.cEnv.uid(), auditComment, SDMSConstants.lZERO, null, null, name, auditInfo);
					Long msgId = msg.getId(sysEnv);
					SDMSEntityVariable ev = SDMSEntityVariableTable.table.create(sysEnv, msgId, name, value, Boolean.TRUE, null);
					msg.setAdditionalLong(sysEnv, ev.getId(sysEnv));
				}
				if (!isApproval) {
					sme.setVariableValue(sysEnv, name, value);
				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03206060017", "Parameter set"));
	}
}

