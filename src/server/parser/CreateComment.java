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
		String tag = null;
		String text = null;

		if(! (with.containsKey(ParseStr.S_TEXT) ^ with.containsKey(ParseStr.S_URL)))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "04411111628", "Must specify exactly one of " + ParseStr.S_TEXT + " or " + ParseStr.S_URL));

		obj.resolve(sysEnv);

		if (with.containsKey(ParseStr.S_URL)) {
			infoType = SDMSConstants.OC_URL;
			text = (String) with.get(ParseStr.S_URL);
			tag = null;
		} else {
			infoType = SDMSConstants.OC_TEXT;
		}

		Vector v = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, obj.objId);
		if (v.size() != 0) {
			if(replace) {
				AlterComment ac = new AlterComment(obj, with, Boolean.FALSE);
				ac.setEnv(env);
				ac.go(sysEnv);
				result = ac.result;
				return;
			} else {
				throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03510061519", "Comment already exists"));
			}
		}

		if (infoType.intValue() == SDMSObjectComment.URL)
			SDMSObjectCommentTable.table.create(sysEnv, obj.objId, obj.objType, infoType, SDMSConstants.iONE, tag, text);
		else {
			v = (Vector) with.get(ParseStr.S_TEXT);
			for (int i = 0; i < v.size(); ++i) {
				Vector tt = (Vector) v.get(i);
				tag = (String) tt.get(0);
				text = (String) tt.get(1);
				SDMSObjectCommentTable.table.create(sysEnv, obj.objId, obj.objType, infoType, Integer.valueOf(i+1), tag, text);
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv,"03209241404", "Comment created"));
	}
}

