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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.util.PathVector;

public class DumpCommand
	extends Node
{
	public static final String __version = "@(#) $Id: DumpCommand.java,v 2.21.4.3 2013/06/18 09:49:31 ronald Exp $";

	public DumpCommand (final Vector l, final WithHash w, final String fn)
	{
		super();
	}

	public String syntaxCheck()
	{
		String s = null;
		return s;
	}

	public final void go (final SystemEnvironment sysEnv)
	throws SDMSException
	{

		sysEnv.checkFeatureAvailability(SystemEnvironment.S_DUMP_COMMAND);

	}

}

