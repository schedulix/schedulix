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
		String tag, octag;
		String text = null, octext;

		Vector texts = (Vector) with.get(ParseStr.S_TEXT);
		if(texts == null) {
			text = (String) with.get(ParseStr.S_URL);
			infoType = SDMSConstants.OC_URL;
		} else {
			infoType = SDMSConstants.OC_TEXT;
		}

		if (texts == null && (text == null || text.equals(""))) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03808040831", "Comment cannot be empty. Use the drop command to delete comments"));
		}

		obj.resolve(sysEnv);

		Vector ocv;
		SDMSObjectComment oc;
		ocv = SDMSObjectCommentTable.idx_objectId_getSortedVector(sysEnv, obj.objId);
		if (ocv.size() == 0) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv,"03311130114", "No Comment altered"));
				return;
			}
			throw new NotFoundException(new SDMSMessage(sysEnv, "03510130956", "Comment not found for object $1", obj.toString()));
		}

		oc = (SDMSObjectComment) ocv.get(0);
		if (oc.getInfoType(sysEnv).intValue() == SDMSObjectComment.URL) {
			if (infoType.intValue() == SDMSObjectComment.URL) {
				oc.setDescription(sysEnv, text);
			} else {
				oc.delete(sysEnv);
				for (int i = 0; i < texts.size(); ++i) {
					Vector entry = (Vector) texts.get(i);
					tag = (String) entry.get(0);
					text = (String) entry.get(1);
					SDMSObjectCommentTable.table.create(sysEnv, obj.objId, obj.objType, infoType, Integer.valueOf(i+1), tag, text);
				}
			}
		} else {
			if (infoType.intValue() == SDMSObjectComment.URL) {
				for (int i = 0; i < ocv.size(); ++i) {
					oc = (SDMSObjectComment) ocv.get(i);
					oc.delete(sysEnv);
				}
				SDMSObjectCommentTable.table.create(sysEnv, obj.objId, obj.objType, infoType, SDMSConstants.iONE, null, text);
			} else {
				int i;
				for (i = 0; i < ocv.size() && i < texts.size(); ++i) {
					oc = (SDMSObjectComment) ocv.get(i);
					Vector entry = (Vector) texts.get(i);
					tag = (String) entry.get(0);
					text = (String) entry.get(1);
					octag = oc.getTag(sysEnv);
					octext = oc.getDescription(sysEnv);

					if ((tag == null && octag != null) || (octag == null && tag != null))
						oc.setTag(sysEnv, tag);
					if (tag != null && octag != null && !tag.equals(octag))
						oc.setTag(sysEnv, tag);

					if (!text.equals(octext))
						oc.setDescription(sysEnv, text);
				}
				for (; i < ocv.size(); ++i) {
					oc = (SDMSObjectComment) ocv.get(i);
					oc.delete(sysEnv);
				}
				for (; i < texts.size(); ++i) {
					Vector entry = (Vector) texts.get(i);
					tag = (String) entry.get(0);
					text = (String) entry.get(1);
					SDMSObjectCommentTable.table.create(sysEnv, obj.objId, obj.objType, infoType, Integer.valueOf(i+1), tag, text);
				}
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv,"03209241403", "Comment altered"));
	}
}

