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

public class ShowWt extends ShowCommented
{

	public final static String __version = "@(#) $Id: ShowWt.java,v 2.1.4.4 2013/06/18 09:49:39 ronald Exp $";

	ObjectURL url;

	public ShowWt(ObjectURL url)
	{
		super();
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		this.url = url;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		sysEnv.checkFeatureAvailability(SystemEnvironment.S_OBJECT_MONITOR);
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ID");
		desc.add("NAME");
		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("PARAMETERS");

		Vector data = new Vector();

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage(sysEnv, "02108241021", "Watch Type"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(
			new SDMSMessage(sysEnv, "02108241022", "Watch Type shown"));

	}

}

