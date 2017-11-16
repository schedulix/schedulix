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

package de.independit.scheduler.server;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.math.*;

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class InitRepositoryThread extends InternalSession
{

	public final static String name = "InitRepositoryThread";

	public InitRepositoryThread(SystemEnvironment env, SyncFifo f)
	throws SDMSException
	{
		super(name);
		NR = 1234326;
		initThread(env, f, NR, name, 1	);
		run = false;
	}

	protected Node getNode(int m)
	{
		return new DoCheckRepository(DoCheckRepository.INITIALIZE);
	}

}

class DoCheckRepository extends Node
{

	static final int SCHEDULE = 0;
	static final int INITIALIZE = 1;

	int action;

	public DoCheckRepository()
	{
		super();
		action = SCHEDULE;
	}

	public DoCheckRepository(int a)
	{
		super();
		action = a;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		switch(action) {
			case SCHEDULE:
				break;
			case INITIALIZE:
				RepositoryChecker.checkRepository(sysEnv);
				break;
		}
	}

}
