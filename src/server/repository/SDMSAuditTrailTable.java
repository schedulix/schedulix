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

public class SDMSAuditTrailTable extends SDMSAuditTrailTableGeneric
{

	public final static String __version = "SDMSAuditTrailTable $Revision: 2.8.4.1 $ / @(#) $Id: SDMSAuditTrailTable.java,v 2.8.4.1 2013/03/14 10:25:17 ronald Exp $";

	public SDMSAuditTrailTable(SystemEnvironment env)
		throws SDMSException
	{
		super(env);
	}

	public SDMSAuditTrail create(SystemEnvironment env
		,Long p_userId
		,Long p_ts
		,Integer p_action
		,Integer p_objectType
		,Long p_objectId
		,Long p_originId
		,Boolean p_isSetWarning
		,String p_actionInfo
		,String p_actionComment
	)
		throws SDMSException
	{

		if (env.checkCompatLevel(SystemEnvironment.S_PROFESSIONAL)) {
			return super.create(env ,p_userId ,p_ts , new Long(env.tx.txId), p_action ,p_objectType ,p_objectId ,p_originId ,p_isSetWarning ,p_actionInfo ,p_actionComment);
		} else return null;
	}
}
