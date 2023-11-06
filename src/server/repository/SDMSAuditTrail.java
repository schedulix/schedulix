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
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.exception.*;

public class SDMSAuditTrail extends SDMSAuditTrailProxyGeneric
{

	public final static String __version = "SDMSAuditTrail $Revision: 2.4.2.2 $ / @(#) $Id: SDMSAuditTrail.java,v 2.4.2.2 2013/03/16 11:47:20 dieter Exp $";

	protected SDMSAuditTrail(SDMSObject p_object)
	{
		super(p_object);
	}

	public static Integer convert(Token t1)
	{
		switch (t1.token.intValue()) {
			case Parser.RERUN:
				return SDMSConstants.AT_RERUN;
			case Parser.CANCEL:
				return SDMSConstants.AT_CANCEL;
			case Parser.SUSPEND:
				return SDMSConstants.AT_SUSPEND;
			case Parser.RESUME:
				return SDMSConstants.AT_RESUME;
			case Parser.KILL:
				return SDMSConstants.AT_KILL;
			case Parser.COMMENT:
				return SDMSConstants.AT_COMMENT_JOB;
			case Parser.SUBMIT:
				return SDMSConstants.AT_SUBMITTED;
			case Parser.RESTARTABLE:
				return SDMSConstants.AT_JOB_RESTARTABLE;
			case Parser.RENICE:
				return SDMSConstants.AT_RENICE;
			case Parser.TIMEOUT:
				return SDMSConstants.AT_TIMEOUT;
			case Parser.UNREACHABLE:
				return SDMSConstants.AT_JOB_UNREACHABLE;
			case Parser.IGNORE_DEPENDENCY:
				return SDMSConstants.AT_IGNORE_DEPENDENCY;
			case Parser.DISABLE:
				return SDMSConstants.AT_DISABLE;
			case Parser.ENABLE:
				return SDMSConstants.AT_ENABLE;
			case Parser.CLONE:
				return SDMSConstants.AT_CLONE;
			case Parser.APPROVE:
				return SDMSConstants.AT_APPROVE;
			case Parser.REJECT:
				return SDMSConstants.AT_REJECT;
		}
		return null;
	}

	public static Integer convert(Token t1, Token t2)
	{
		switch (t1.token.intValue()) {
			case Parser.APPROVAL:
				return SDMSConstants.AT_APPROVAL_REQUEST;
			case Parser.REVIEW:
				return SDMSConstants.AT_REVIEW_REQUEST;
			case Parser.RERUN:
				return SDMSConstants.AT_RERUN_RECURSIVE;
			case Parser.CLONE:
				return SDMSConstants.AT_CLONE;
			case Parser.SET:
				switch (t2.token.intValue()) {
					case Parser.STATUS:
						return SDMSConstants.AT_SET_STATE;
					case Parser.EXIT_STATUS:
						return SDMSConstants.AT_SET_EXIT_STATE;
					case Parser.WARNING:
						return SDMSConstants.AT_SET_WARNING;
					case Parser.RESOURCE_STATUS:
						return SDMSConstants.AT_SET_RESOURCE_STATE;
					case Parser.PARAMETERS:
						return SDMSConstants.AT_SET_PARAMETERS;
				}
				return null;
			case Parser.IGNORE:
				switch (t2.token.intValue()) {
					case Parser.RESOURCE:
						return SDMSConstants.AT_IGNORE_RESOURCE;
					case Parser.NAMED_RESOURCE:
						return SDMSConstants.AT_IGNORE_NAMED_RESOURCE;
				}
				return null;
			case Parser.TRIGGER:
				switch (t2.token.intValue()) {
					case Parser.FAILURE:
						return SDMSConstants.AT_TRIGGER_FAILED;
					case Parser.SUBMIT:
						return SDMSConstants.AT_TRIGGER_SUBMIT;
				}
				return null;
			case Parser.CHANGE:
				return SDMSConstants.AT_CHANGE_PRIORITY;
			case Parser.SUBMIT:
				return SDMSConstants.AT_SUBMIT_SUSPENDED;
			case Parser.CLEAR:
				return SDMSConstants.AT_CLEAR_WARNING;
			case Parser.IGNORE_DEPENDENCY:
				return SDMSConstants.AT_IGNORE_DEP_RECURSIVE;
		}
		return null;
	}

	public static Integer convert(Token t1, Token t2, Token t3)
	{
		switch (t1.token.intValue()) {
			case Parser.CLONE:
				return SDMSConstants.AT_CLONE;
			case Parser.SET:
				return SDMSConstants.AT_SET_RESOURCE_STATE;
			case Parser.JOB:
				return SDMSConstants.AT_JOB_IN_ERROR;
		}
		return null;
	}

	public String getActionInfo (SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long infoId = getInfoId(sysEnv);
		if (infoId != null) {
			SDMSEntityVariable ev = SDMSEntityVariableTable.getObject(sysEnv, infoId);
			return ev.getValue(sysEnv);
		} else {
			return super.getActionInfo(sysEnv);
		}
	}

	public void setActionInfo (SystemEnvironment sysEnv, String p_actionInfo)
	throws SDMSException
	{
		Long smeId = getObjectId(sysEnv);
		Long infoId = getInfoId(sysEnv);
		SDMSEntityVariable ev = null;
		if (infoId != null) {
			ev = SDMSEntityVariableTable.getObject(sysEnv, infoId);
			ev.delete(sysEnv);
			ev = null;
			setInfoId(sysEnv, null);
		}
		if (p_actionInfo == null) {
			super.setActionInfo(sysEnv, p_actionInfo);
			return;
		}
		if (p_actionInfo.length() > 1024) {
			super.setActionInfo(sysEnv, null);
			String name = SDMSConstants.AT_EVAUDITPREFIX + getId(sysEnv).toString();
			ev = SDMSEntityVariableTable.table.create(sysEnv, smeId, name, p_actionInfo, Boolean.TRUE, null);
			setInfoId(sysEnv, ev.getId(sysEnv));
		} else {
			super.setActionInfo(sysEnv, p_actionInfo);
		}
		return;
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups)
		throws SDMSException
	{
		long p = super.getPrivileges(env, checkPrivs | SDMSPrivilege.OPERATE, fastFail, checkGroups);
		if ((p&(SDMSPrivilege.OPERATE|SDMSPrivilege.OPERATE_PRIVS)) != 0) p = p | SDMSPrivilege.CREATE;
		p = p & checkPrivs;
		return p;
	}

}
