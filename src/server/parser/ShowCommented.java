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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public abstract class ShowCommented extends Node
{

	public static final String __version = "@(#) $Id: ShowCommented.java,v 2.1.14.1 2013/03/14 10:24:48 ronald Exp $";

	private SDMSObjectComment oc;
	private boolean oc_exists;
	private Long lastId;

	public ShowCommented()
	{
		super();
		oc = null;
		oc_exists = true;
		lastId = null;
	}

	private void getObjectComment(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		if(oc != null && id.equals(lastId)) return;
		if(oc == null && id.equals(lastId) && !oc_exists) return;
		try {
			oc = SDMSObjectCommentTable.idx_objectId_getUnique(sysEnv, id);
		} catch(NotFoundException nfe) {
			oc_exists = false;
			oc = null;
		}
		lastId = id;
		return;
	}

	protected String getCommentDescription(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		String text = null;
		getObjectComment(sysEnv, id);
		if(oc != null)
			text = oc.getDescription(sysEnv);
		return text;
	}

	protected String getCommentInfoType(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		String infoType = null;
		getObjectComment(sysEnv, id);
		if(oc != null)
			infoType = oc.getInfoTypeAsString(sysEnv);
		return infoType;
	}

	public abstract void go(SystemEnvironment sysEnv) throws SDMSException;
}

