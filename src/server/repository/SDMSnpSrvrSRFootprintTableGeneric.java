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

public class SDMSnpSrvrSRFootprintTableGeneric extends SDMSTable
{

	public final static String __version = "SDMSnpSrvrSRFootprintTableGeneric $Revision: 2.0 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

	public final static String tableName = "NPSRVR_SR_FOOTPRINT";
	public static SDMSnpSrvrSRFootprintTable table  = null;

	public static SDMSIndex idx_sId;

	public SDMSnpSrvrSRFootprintTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {

			throw new FatalException(new SDMSMessage(env, "01110182009", "npSrvrSRFootprint"));
		}
		table = (SDMSnpSrvrSRFootprintTable) this;
		SDMSnpSrvrSRFootprintTableGeneric.table = (SDMSnpSrvrSRFootprintTable) this;
		isVersioned = false;
		idx_sId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned);
	}
	public SDMSnpSrvrSRFootprint create(SystemEnvironment env
	                                    ,Long p_sId
	                                    ,Integer p_sessionId
	                                    ,HashMap p_fp
	                                   )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();

		if(env.tx.mode == SDMSTransaction.READONLY) {

			throw new FatalException(new SDMSMessage(env, "01110182049", "npSrvrSRFootprint"));
		}
		validate(env
		         , p_sId
		         , p_sessionId
		         , p_fp
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSnpSrvrSRFootprintGeneric o = new SDMSnpSrvrSRFootprintGeneric(env
		                , p_sId
		                , p_sessionId
		                , p_fp
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                 );

		SDMSnpSrvrSRFootprint p;
		try {
			env.tx.addToChangeSet(env, o.versions, true);
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSnpSrvrSRFootprint)(o.toProxy());
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSnpSrvrSRFootprint)(o.toProxy());
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSnpSrvrSRFootprint p)
	throws SDMSException
	{
		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_sId
	                        ,Integer p_sessionId
	                        ,HashMap p_fp
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{

	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{

		return null;
	}
	protected void loadTable(SystemEnvironment env)
	throws SQLException, SDMSException
	{

	}

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_sId.put(env, ((SDMSnpSrvrSRFootprintGeneric) o).sId, o);
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_sId.remove(env, ((SDMSnpSrvrSRFootprintGeneric) o).sId, o);
	}

	public static SDMSnpSrvrSRFootprint getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSnpSrvrSRFootprint) table.get(env, id);
	}

	public static SDMSnpSrvrSRFootprint getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSnpSrvrSRFootprint) table.get(env, id, version);
	}

	public static SDMSnpSrvrSRFootprint idx_sId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSnpSrvrSRFootprint) SDMSnpSrvrSRFootprintTableGeneric.idx_sId.getUnique(env, key);
	}

	public static SDMSnpSrvrSRFootprint idx_sId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSnpSrvrSRFootprint) SDMSnpSrvrSRFootprintTableGeneric.idx_sId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
}
