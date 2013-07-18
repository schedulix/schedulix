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
import de.independit.scheduler.server.output.*;

public class RegisterServer extends Node
{

	public final static String __version = "@(#) $Id: RegisterServer.java,v 2.3.14.1 2013/03/14 10:24:43 ronald Exp $";

	private Vector path;
	private String name;
	private String pid;
	private Boolean suspended;

	public RegisterServer(Vector p, String n, String id, Boolean s)
	{
		super();
		cmdtype = Node.SERVER_COMMAND|Node.USER_COMMAND;
		path = p;
		name = n;
		pid = id;
		suspended = s;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSScope s;
		if(name == null) {

			s = registerByServer(sysEnv);
		} else {

			if(pid != null) {
				s = registerByOperator(sysEnv);
			} else {
				s = deregisterByOperator(sysEnv);
			}
		}
	}

	private SDMSScope registerByOperator(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long parentId;

		parentId = SDMSScopeTable.pathToId(sysEnv, path);
		SDMSScope s = SDMSScopeTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(parentId, name));
		if(s.getType(sysEnv).intValue() != SDMSScope.SERVER) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201291821", "A Scope cannot be registered"));
		}
		s.setIsRegistered(sysEnv, Boolean.TRUE);
		s.setState(sysEnv, new Integer(SDMSScope.NOMINAL));
		s.setErrmsg(sysEnv, null);
		s.setPid(sysEnv, pid);
		if(suspended != null) {
			s.setIsSuspended(sysEnv, suspended);
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03201292822", "Server registered"));

		return s;
	}

	private SDMSScope deregisterByOperator(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Long parentId;

		parentId = SDMSScopeTable.pathToId(sysEnv, path);
		SDMSScope s = SDMSScopeTable.idx_parentId_name_getUnique(sysEnv, new SDMSKey(parentId, name));
		if(s.getType(sysEnv).intValue() != SDMSScope.SERVER) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201291822", "A Scope cannot be deregistered"));
		}
		s.setIsRegistered(sysEnv, Boolean.FALSE);
		if(suspended != null) {
			s.setIsSuspended(sysEnv, suspended);
		}

		final Vector jobv = SDMSSubmittedEntityTable.idx_scopeId.getVector(sysEnv, s.getId(sysEnv));
		for (int i = 0; i < jobv.size(); ++i) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity) jobv.get(i);
			int state = sme.getState(sysEnv).intValue();
			int newState = SDMSSubmittedEntity.BROKEN_FINISHED;
			switch (state) {
			case SDMSSubmittedEntity.STARTED:
			case SDMSSubmittedEntity.RUNNING:
			case SDMSSubmittedEntity.TO_KILL:
			case SDMSSubmittedEntity.KILLED:
			case SDMSSubmittedEntity.BROKEN_ACTIVE:
				sme.releaseResources(sysEnv, newState);

				sme.setErrorMsg(sysEnv, "Jobserver deregistered");
				sme.setState(sysEnv, new Integer(newState));
				break;
			case SDMSSubmittedEntity.STARTING:
				sme.setState(sysEnv, new Integer(SDMSSubmittedEntity.RUNNABLE));
				break;
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03201292823", "Server deregistered"));

		return s;
	}

	private SDMSScope registerByServer(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSScope s = SDMSScopeTable.getObject(sysEnv, sysEnv.cEnv.uid());
		s.setIsRegistered(sysEnv, Boolean.TRUE);
		s.setState(sysEnv, new Integer(SDMSScope.NOMINAL));
		s.setErrmsg(sysEnv, null);
		s.setPid(sysEnv, pid);
		s.setHasAlteredConfig (sysEnv, Boolean.FALSE);

		result.setOutputContainer (ScopeConfig.get (sysEnv, s));

		result.setFeedback(new SDMSMessage(sysEnv, "03201292824", "Server registered"));

		return s;
	}
}
