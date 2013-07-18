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

public class SDMSDependencyDefinition extends SDMSDependencyDefinitionProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSDependencyDefinition.java,v 2.1.14.1 2013/03/14 10:25:18 ronald Exp $";

	protected SDMSDependencyDefinition(SDMSObject p_object)
	{
		super(p_object);
	}

	public void setMode (SystemEnvironment env, Integer p_mode)
	throws SDMSException
	{
		if(p_mode.intValue() == SDMSDependencyDefinition.JOB_FINAL) {
			SDMSSchedulingEntity rSe = SDMSSchedulingEntityTable.getObject(env, getSeRequiredId(env));
			if(rSe.getType(env).intValue() != SDMSSchedulingEntity.JOB) {
				throw new CommonErrorException(new SDMSMessage(env, "03210141927",
				                               "Cannot create a JOB_FINAL dependency to $1 $2",
				                               rSe.getTypeAsString(env), rSe.pathVector(env)));
			}
		}
		super.setMode(env, p_mode);
		return ;
	}

	public void delete(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Vector act_ds = SDMSDependencyStateTable.idx_ddId.getVector(sysEnv, getId(sysEnv));
		for(int i = 0; i < act_ds.size(); i++) {
			SDMSDependencyState ds = (SDMSDependencyState) act_ds.get(i);
			ds.delete(sysEnv);
		}

		super.delete(sysEnv);
	}
}
