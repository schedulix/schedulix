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

public class DropComment extends Node
{

	public final static String __version = "@(#) $Id: DropComment.java,v 2.2.14.1 2013/03/14 10:24:28 ronald Exp $";

	private int type;
	private ObjectURL obj;
	private boolean noerr;

	public DropComment(ObjectURL t, Boolean n)
	{
		super();
		obj = t;
		noerr = n.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long objId = null;

		obj.resolve(sysEnv);

		if (0 == SDMSObjectCommentTable.dropComment(sysEnv, obj.objId)) {
			if (noerr) {
				result.setFeedback(new SDMSMessage(sysEnv,"03311130113", "No Comment dropped"));
				return;
			} else
				throw new NotFoundException(new SDMSMessage(sysEnv, "03510121546", "No comment found for object $1", obj.toString()));
		}

		result.setFeedback(new SDMSMessage(sysEnv,"03209240935", "Comment dropped"));
	}
}

