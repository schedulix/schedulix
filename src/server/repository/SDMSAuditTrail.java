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
				return Integer.valueOf(RERUN);
			case Parser.CANCEL:
				return Integer.valueOf(CANCEL);
			case Parser.SUSPEND:
				return Integer.valueOf(SUSPEND);
			case Parser.RESUME:
				return Integer.valueOf(RESUME);
			case Parser.KILL:
				return Integer.valueOf(KILL);
			case Parser.COMMENT:
				return Integer.valueOf(COMMENT_JOB);
			case Parser.SUBMIT:
				return Integer.valueOf(SUBMITTED);
			case Parser.RESTARTABLE:
				return Integer.valueOf(JOB_RESTARTABLE);
			case Parser.RENICE:
				return Integer.valueOf(RENICE);
			case Parser.TIMEOUT:
				return Integer.valueOf(TIMEOUT);
			case Parser.UNREACHABLE:
				return Integer.valueOf(JOB_UNREACHABLE);
			case Parser.IGNORE_DEPENDENCY:
				return Integer.valueOf(IGNORE_DEPENDENCY);
			case Parser.DISABLE:
				return Integer.valueOf(DISABLE);
			case Parser.ENABLE:
				return Integer.valueOf(ENABLE);
		}
		return null;
	}

	public static Integer convert(Token t1, Token t2)
	{
		switch (t1.token.intValue()) {
			case Parser.RERUN:
				return Integer.valueOf(RERUN_RECURSIVE);
			case Parser.SET:
				switch (t2.token.intValue()) {
					case Parser.STATUS:
						return Integer.valueOf(SET_STATE);
					case Parser.EXIT_STATUS:
						return Integer.valueOf(SET_EXIT_STATE);
					case Parser.WARNING:
						return Integer.valueOf(SET_WARNING);
					case Parser.RESOURCE_STATUS:
						return Integer.valueOf(SET_RESOURCE_STATE);
					case Parser.PARAMETERS:
						return Integer.valueOf(SET_PARAMETERS);
				}
				return null;
			case Parser.IGNORE:
				switch (t2.token.intValue()) {
					case Parser.RESOURCE:
						return Integer.valueOf(IGNORE_RESOURCE);
					case Parser.NAMED_RESOURCE:
						return Integer.valueOf(IGNORE_NAMED_RESOURCE);
				}
				return null;
			case Parser.TRIGGER:
				switch (t2.token.intValue()) {
					case Parser.FAILURE:
						return Integer.valueOf(TRIGGER_FAILED);
					case Parser.SUBMIT:
						return Integer.valueOf(TRIGGER_SUBMIT);
				}
				return null;
			case Parser.CHANGE:
				return Integer.valueOf(CHANGE_PRIORITY);
			case Parser.SUBMIT:
				return Integer.valueOf(SUBMIT_SUSPENDED);
			case Parser.CLEAR:
				return Integer.valueOf(CLEAR_WARNING);
			case Parser.IGNORE_DEPENDENCY:
				return Integer.valueOf(IGNORE_DEP_RECURSIVE);
		}
		return null;
	}

	public static Integer convert(Token t1, Token t2, Token t3)
	{
		switch (t1.token.intValue()) {
			case Parser.SET:
				return Integer.valueOf(SET_RESOURCE_STATE);
			case Parser.JOB:
				return Integer.valueOf(JOB_IN_ERROR);
		}
		return null;
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups)
		throws SDMSException
	{
		long p = super.getPrivileges(env, checkPrivs | SDMSPrivilege.OPERATE, fastFail, checkGroups);
		if ((p & SDMSPrivilege.OPERATE) != 0) p = p | SDMSPrivilege.CREATE;
		p = p & checkPrivs;
		return p;
	}

}
