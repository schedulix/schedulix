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

	public static Integer convert(Token t1)
	{
		switch (t1.token.intValue()) {
			case Parser.RERUN:
				return new Integer(RERUN);
			case Parser.CANCEL:
				return new Integer(CANCEL);
			case Parser.SUSPEND:
				return new Integer(SUSPEND);
			case Parser.RESUME:
				return new Integer(RESUME);
			case Parser.KILL:
				return new Integer(KILL);
			case Parser.COMMENT:
				return new Integer(COMMENT_JOB);
			case Parser.SUBMIT:
				return new Integer(SUBMITTED);
			case Parser.RESTARTABLE:
				return new Integer(JOB_RESTARTABLE);
			case Parser.RENICE:
				return new Integer(RENICE);
			case Parser.TIMEOUT:
				return new Integer(TIMEOUT);
			case Parser.UNREACHABLE:
				return new Integer(JOB_UNREACHABLE);
			case Parser.IGNORE_DEPENDENCY:
				return new Integer(IGNORE_DEPENDENCY);
		}
		return null;
	}

	public static Integer convert(Token t1, Token t2)
	{
		switch (t1.token.intValue()) {
			case Parser.RERUN:
				return new Integer(RERUN_RECURSIVE);
			case Parser.SET:
				switch (t2.token.intValue()) {
					case Parser.STATUS:
						return new Integer(SET_STATE);
					case Parser.EXIT_STATUS:
						return new Integer(SET_EXIT_STATE);
					case Parser.WARNING:
						return new Integer(SET_WARNING);
					case Parser.RESOURCE_STATUS:
						return new Integer(SET_RESOURCE_STATE);
					case Parser.PARAMETERS:
						return new Integer(SET_PARAMETERS);
				}
				return null;
			case Parser.IGNORE:
				switch (t2.token.intValue()) {
					case Parser.RESOURCE:
						return new Integer(IGNORE_RESOURCE);
					case Parser.NAMED_RESOURCE:
						return new Integer(IGNORE_NAMED_RESOURCE);
				}
				return null;
			case Parser.TRIGGER:
				switch (t2.token.intValue()) {
					case Parser.FAILURE:
						return new Integer(TRIGGER_FAILED);
					case Parser.SUBMIT:
						return new Integer(TRIGGER_SUBMIT);
				}
				return null;
			case Parser.CHANGE:
				return new Integer(CHANGE_PRIORITY);
			case Parser.SUBMIT:
				return new Integer(SUBMIT_SUSPENDED);
			case Parser.CLEAR:
				return new Integer(CLEAR_WARNING);
			case Parser.IGNORE_DEPENDENCY:
				return new Integer(IGNORE_DEP_RECURSIVE);
		}
		return null;
	}

	public static Integer convert(Token t1, Token t2, Token t3)
	{
		switch (t1.token.intValue()) {
			case Parser.SET:
				return new Integer(SET_RESOURCE_STATE);
			case Parser.JOB:
				return new Integer(JOB_IN_ERROR);
		}
		return null;
	}

	protected SDMSAuditTrail(SDMSObject p_object)
	{
		super(p_object);
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
