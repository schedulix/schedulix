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

public class CreateComment extends Node
{

	public final static String __version = "@(#) $Id: CreateComment.java,v 2.2.14.1 2013/03/14 10:24:25 ronald Exp $";

	private int type;
	private ObjectURL obj;
	private WithHash with;

	private boolean replace;

	public CreateComment(ObjectURL t, WithHash w, Boolean r)
	{
		super();
		with = w;
		obj = t;
		replace = r.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Integer infoType;

		if(! (with.containsKey(ParseStr.S_TEXT) ^ with.containsKey(ParseStr.S_URL)))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "04411111628", "Must specify exactly one of " + ParseStr.S_TEXT + " or " + ParseStr.S_URL));
		String text = (String) with.get(ParseStr.S_TEXT);
		if(text == null) {
			text = (String) with.get(ParseStr.S_URL);
			infoType = new Integer(SDMSObjectComment.URL);
		} else {
			infoType = new Integer(SDMSObjectComment.TEXT);
		}

		obj.resolve(sysEnv);

		try {
			SDMSObjectCommentTable.table.create(sysEnv, obj.objId, obj.objType, infoType, new Integer(1), text);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				AlterComment ac = new AlterComment(obj, with, Boolean.FALSE);
				ac.setEnv(env);
				ac.go(sysEnv);
				result = ac.result;
				return;
			} else {
				throw dke;
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv,"03209241404", "Comment created"));
	}
}

