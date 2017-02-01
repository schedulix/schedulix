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

public abstract class ShowCommented extends Node
{

	private Vector ocv = null;
	private Long lastId = null;

	public ShowCommented()
	{
		super();
		ocv = null;
		lastId = null;
	}

	private void getObjectComment(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		if (lastId != null && id.equals(lastId)) return;
		ocv = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, id);
		lastId = id;
		return;
	}

	protected SDMSOutputContainer getCommentContainer(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		SDMSOutputContainer result;
		SDMSObjectComment oc;
		Vector desc = new Vector();
		Vector data;

		desc.add("ID");
		desc.add("SEQNO");
		desc.add("TAG");
		desc.add("DESCRIPTION");

		result = new SDMSOutputContainer(sysEnv, null, desc);

		getObjectComment(sysEnv, id);

		for (int i = 0; i < ocv.size(); ++i) {
			oc = (SDMSObjectComment) ocv.get(i);
			data = new Vector();
			data.add(oc.getId(sysEnv));
			data.add(oc.getSequenceNumber(sysEnv));
			data.add(oc.getTag(sysEnv));
			data.add(oc.getDescription(sysEnv));
			result.addData(sysEnv, data);
		}

		Collections.sort(result.dataset, result.getComparator(sysEnv, 1));

		return result;
	}

	protected String getCommentInfoType(SystemEnvironment sysEnv, Long id)
		throws SDMSException
	{
		String infoType = null;
		getObjectComment(sysEnv, id);
		if(ocv != null && ocv.size() > 0)
			infoType = ((SDMSObjectComment)(ocv.elementAt(0))).getInfoTypeAsString(sysEnv);
		return infoType;
	}

	public abstract void go(SystemEnvironment sysEnv) throws SDMSException;
}

