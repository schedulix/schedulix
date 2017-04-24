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

public class SDMSEntityVariableTable extends SDMSEntityVariableTableGeneric
{

	public SDMSEntityVariableTable(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
	}

	public SDMSEntityVariable create(SystemEnvironment env
	                                 ,Long p_smeId
	                                 ,String p_name
	                                 ,String p_value
	                                 ,Boolean p_isLocal
	                                 ,Long p_evLink
	                                )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		SDMSEntityVariable ev = super.create(env, p_smeId, p_name, null, p_isLocal, p_evLink, false);
		ev.setValue(env, p_value);
		return ev;
	}
}
