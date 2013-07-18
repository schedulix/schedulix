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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSDependencyDefinitionTable extends SDMSDependencyDefinitionTableGeneric
{

	public final static String __version = "@(#) $Id: SDMSDependencyDefinitionTable.java,v 2.2.2.1 2013/03/14 10:25:18 ronald Exp $";

	public SDMSDependencyDefinitionTable(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
	}

	public SDMSDependencyDefinition create(SystemEnvironment env
	                                       ,Long p_seDependentId
	                                       ,Long p_seRequiredId
	                                       ,String p_name
	                                       ,Integer p_unresolvedHandling
	                                       ,Integer p_mode
	                                       ,Integer p_state_selection
	                                       ,String p_condition
	                                      )
	throws SDMSException
	{
		if(p_mode == null) p_mode = new Integer(SDMSDependencyDefinition.ALL_FINAL);
		if(p_mode.intValue() == SDMSDependencyDefinition.JOB_FINAL) {
			SDMSSchedulingEntity rSe = SDMSSchedulingEntityTable.getObject(env, p_seRequiredId);
			if(rSe.getType(env).intValue() != SDMSSchedulingEntity.JOB) {
				throw new CommonErrorException(new SDMSMessage(env, "03210141844",
				                               "Cannot create a JOB_FINAL dependency to $1 $2",
				                               rSe.getTypeAsString(env), rSe.pathVector(env)));
			}
		}
		return super.create(env, p_seDependentId, p_seRequiredId, p_name, p_unresolvedHandling, p_mode, p_state_selection, p_condition);
	}
}
