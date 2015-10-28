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
import de.independit.scheduler.server.exception.*;

public class SDMSObjectCommentTable extends SDMSObjectCommentTableGeneric
{

	public final static String __version = "@(#) $Id: SDMSObjectCommentTable.java,v 2.0.20.1 2013/03/14 10:25:20 ronald Exp $";
	private static CommentComparator commentComparator = new CommentComparator();

	public SDMSObjectCommentTable(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
	}

	public static int dropComment(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		Vector ocv = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, id);
		for (int i = 0; i < ocv.size(); ++i) {
			SDMSObjectComment oc = (SDMSObjectComment) ocv.get(i);
			oc.delete(sysEnv);
		}

		return ocv.size();
	}

	public static SDMSObjectComment idx_objectId_getFirst(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		SDMSObjectComment firstoc = null;
		int lowestSeqNo = Integer.MAX_VALUE;

		Vector ocv = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, id);
		for (int i = 0; i < ocv.size(); ++i) {
			SDMSObjectComment oc = (SDMSObjectComment) ocv.get(i);
			int mySeqNo = oc.getSequenceNumber(sysEnv).intValue();
			if (mySeqNo < lowestSeqNo) {
				firstoc = oc;
				lowestSeqNo = mySeqNo;
			}
		}
		if (firstoc == null)
			throw new NotFoundException(new SDMSMessage(sysEnv, "03510121305", "No comment found for id $1", id));

		return firstoc;
	}

	public static Vector idx_objectId_getSortedVector(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		Vector result = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, id);
		if (result.size() > 0)
			synchronized (commentComparator) {
				commentComparator.setEnv(sysEnv);
				Collections.sort(result, commentComparator);
			}

		return result;
	}
}

class CommentComparator implements Comparator
{
	SystemEnvironment sysEnv;

	public CommentComparator()
	{

	}

	public void setEnv (SystemEnvironment sysEnv)
	{
		this.sysEnv = sysEnv;
	}

	public int compare(Object o1, Object o2)
	{
		if (!(o1 instanceof SDMSObjectComment))
			throw new IllegalArgumentException();
		if (!(o2 instanceof SDMSObjectComment))
			throw new IllegalArgumentException();

		SDMSObjectComment oc1 = (SDMSObjectComment) o1;
		SDMSObjectComment oc2 = (SDMSObjectComment) o2;

		try {
			int seqno1 = oc1.getSequenceNumber(sysEnv).intValue();
			int seqno2 = oc2.getSequenceNumber(sysEnv).intValue();

			if (seqno1 > seqno2) return 1;
			if (seqno2 > seqno1) return -1;

			String tag1 = oc1.getTag(sysEnv);
			String tag2 = oc2.getTag(sysEnv);

			if (tag1 == null && tag2 != null) return 1;
			if (tag2 == null && tag1 != null) return -1;
			if (tag1 != null && tag2 != null) {
				if (! tag1.equals(tag2)) {
					return tag1.compareTo(tag2);
				}
			}
		} catch (SDMSException e) {
			throw new IllegalArgumentException();
		}

		return 0;
	}
}
