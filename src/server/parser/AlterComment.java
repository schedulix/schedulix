/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class AlterComment extends Node
{

	public final static String __version = "@(#) $Id: AlterComment.java,v 2.4.12.1 2013/03/14 10:24:19 ronald Exp $";

	private int type;
	private ObjectURL obj;
	private WithHash with;
	private boolean noerr;

	public AlterComment(ObjectURL t, WithHash w, Boolean n)
	{
		super();
		obj = t;
		with = w;
		noerr = n.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Integer infoType;

		String text = (String) with.get(ParseStr.S_TEXT);
		if(text == null) {
			text = (String) with.get(ParseStr.S_URL);
			infoType = new Integer(SDMSObjectComment.URL);
		} else {
			infoType = new Integer(SDMSObjectComment.TEXT);
		}

		if (text == null || text.equals("")) {

			throw new CommonErrorException(new SDMSMessage(sysEnv, "03808040831", "Comment cannot be empty. Use the drop command to delete comments"));
		}

		obj.resolve(sysEnv);

		SDMSObjectComment oc;
		try {
			oc = SDMSObjectCommentTable.idx_objectId_getUnique(sysEnv, obj.objId);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv,"03311130114", "No Comment altered"));
				return;
			}
			throw nfe;
		}
		oc.setDescription(sysEnv, text);
		oc.setInfoType(sysEnv, infoType);

		result.setFeedback(new SDMSMessage(sysEnv,"03209241403", "Comment altered"));
	}
}

