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

public class SDMSResourceRequirementTableGeneric extends SDMSTable
{

	public final static String tableName = "RESOURCE_REQUIREMENT";
	public static SDMSResourceRequirementTable table  = null;

	public final static String[] columnNames = {
		"ID"
		, "NR_ID"
		, "SE_ID"
		, "AMOUNT"
		, "KEEP_MODE"
		, "IS_STICKY"
		, "STICKY_NAME"
		, "STICKY_PARENT"
		, "RSMP_ID"
		, "EXPIRED_AMOUNT"
		, "EXPIRED_BASE"
		, "IGNORE_ON_RERUN"
		, "LOCKMODE"
		, "CONDITION"
		, "CREATOR_U_ID"
		, "CREATE_TS"
		, "CHANGER_U_ID"
		, "CHANGE_TS"
	};
	public static SDMSIndex idx_nrId;
	public static SDMSIndex idx_seId;
	public static SDMSIndex idx_rsmpId;
	public static SDMSIndex idx_seId_nrId;

	public SDMSResourceRequirementTableGeneric(SystemEnvironment env)
	throws SDMSException
	{
		super(env);
		if (table != null) {
			throw new FatalException(new SDMSMessage(env, "01110182009", "ResourceRequirement"));
		}
		table = (SDMSResourceRequirementTable) this;
		SDMSResourceRequirementTableGeneric.table = (SDMSResourceRequirementTable) this;
		isVersioned = true;
		idx_nrId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "nrId");
		idx_seId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "seId");
		idx_rsmpId = new SDMSIndex(env, SDMSIndex.ORDINARY, isVersioned, table, "rsmpId");
		idx_seId_nrId = new SDMSIndex(env, SDMSIndex.UNIQUE, isVersioned, table, "seId_nrId");
	}
	public SDMSResourceRequirement create(SystemEnvironment env
	                                      ,Long p_nrId
	                                      ,Long p_seId
	                                      ,Integer p_amount
	                                      ,Integer p_keepMode
	                                      ,Boolean p_isSticky
	                                      ,String p_stickyName
	                                      ,Long p_stickyParent
	                                      ,Long p_rsmpId
	                                      ,Integer p_expiredAmount
	                                      ,Integer p_expiredBase
	                                      ,Boolean p_ignoreOnRerun
	                                      ,Integer p_lockmode
	                                      ,String p_condition
	                                     )
	throws SDMSException
	{
		Long p_creatorUId = env.cEnv.uid();
		Long p_createTs = env.txTime();
		Long p_changerUId = env.cEnv.uid();
		Long p_changeTs = env.txTime();
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env, "01110182049", "ResourceRequirement"));
		}
		validate(env
		         , p_nrId
		         , p_seId
		         , p_amount
		         , p_keepMode
		         , p_isSticky
		         , p_stickyName
		         , p_stickyParent
		         , p_rsmpId
		         , p_expiredAmount
		         , p_expiredBase
		         , p_ignoreOnRerun
		         , p_lockmode
		         , p_condition
		         , p_creatorUId
		         , p_createTs
		         , p_changerUId
		         , p_changeTs
		        );

		env.tx.beginSubTransaction(env);
		SDMSResourceRequirementGeneric o = new SDMSResourceRequirementGeneric(env
		                , p_nrId
		                , p_seId
		                , p_amount
		                , p_keepMode
		                , p_isSticky
		                , p_stickyName
		                , p_stickyParent
		                , p_rsmpId
		                , p_expiredAmount
		                , p_expiredBase
		                , p_ignoreOnRerun
		                , p_lockmode
		                , p_condition
		                , p_creatorUId
		                , p_createTs
		                , p_changerUId
		                , p_changeTs
		                                                                     );

		SDMSResourceRequirement p;
		try {
			env.tx.addToTouchSet(env, o.versions, true);
			table.put(env, o.id, o.versions);
			env.tx.commitSubTransaction(env);
			p = (SDMSResourceRequirement)(o.toProxy(env));
			p.current = true;
		} catch(SDMSException e) {
			p = (SDMSResourceRequirement)(o.toProxy(env));
			p.current = true;
			env.tx.rollbackSubTransaction(env);
			throw e;
		}

		if(!checkCreatePrivs(env, p))
			throw new AccessViolationException(p.accessViolationMessage(env, "01402270738"));

		p.touchMaster(env);
		return p;
	}

	protected boolean checkCreatePrivs(SystemEnvironment env, SDMSResourceRequirement p)
	throws SDMSException
	{
		if(!p.checkPrivileges(env, SDMSPrivilege.CREATE))
			return false;

		return true;
	}

	protected void validate(SystemEnvironment env
	                        ,Long p_nrId
	                        ,Long p_seId
	                        ,Integer p_amount
	                        ,Integer p_keepMode
	                        ,Boolean p_isSticky
	                        ,String p_stickyName
	                        ,Long p_stickyParent
	                        ,Long p_rsmpId
	                        ,Integer p_expiredAmount
	                        ,Integer p_expiredBase
	                        ,Boolean p_ignoreOnRerun
	                        ,Integer p_lockmode
	                        ,String p_condition
	                        ,Long p_creatorUId
	                        ,Long p_createTs
	                        ,Long p_changerUId
	                        ,Long p_changeTs
	                       )
	throws SDMSException
	{
		if (!SDMSResourceRequirementGeneric.checkKeepMode(p_keepMode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "ResourceRequirement: $1 $2", "keepMode", p_keepMode));
		if (!SDMSResourceRequirementGeneric.checkExpiredBase(p_expiredBase))
			throw new FatalException(new SDMSMessage(env, "01110182023", "ResourceRequirement: $1 $2", "expiredBase", p_expiredBase));
		if (!SDMSResourceRequirementGeneric.checkLockmode(p_lockmode))
			throw new FatalException(new SDMSMessage(env, "01110182023", "ResourceRequirement: $1 $2", "lockmode", p_lockmode));
	}

	protected SDMSObject rowToObject(SystemEnvironment env, ResultSet r)
	throws SDMSException
	{
		Long id;
		Long nrId;
		Long seId;
		Integer amount;
		Integer keepMode;
		Boolean isSticky;
		String stickyName;
		Long stickyParent;
		Long rsmpId;
		Integer expiredAmount;
		Integer expiredBase;
		Boolean ignoreOnRerun;
		Integer lockmode;
		String condition;
		Long creatorUId;
		Long createTs;
		Long changerUId;
		Long changeTs;
		long validFrom;
		long validTo;

		try {
			id     = new Long (r.getLong(1));
			nrId = new Long (r.getLong(2));
			seId = new Long (r.getLong(3));
			amount = new Integer (r.getInt(4));
			if (r.wasNull()) amount = null;
			keepMode = new Integer (r.getInt(5));
			isSticky = new Boolean ((r.getInt(6) == 0 ? false : true));
			stickyName = r.getString(7);
			if (r.wasNull()) stickyName = null;
			stickyParent = new Long (r.getLong(8));
			if (r.wasNull()) stickyParent = null;
			rsmpId = new Long (r.getLong(9));
			if (r.wasNull()) rsmpId = null;
			expiredAmount = new Integer (r.getInt(10));
			if (r.wasNull()) expiredAmount = null;
			expiredBase = new Integer (r.getInt(11));
			if (r.wasNull()) expiredBase = null;
			ignoreOnRerun = new Boolean ((r.getInt(12) == 0 ? false : true));
			lockmode = new Integer (r.getInt(13));
			if (r.wasNull()) lockmode = null;
			condition = r.getString(14);
			if (r.wasNull()) condition = null;
			creatorUId = new Long (r.getLong(15));
			createTs = new Long (r.getLong(16));
			changerUId = new Long (r.getLong(17));
			changeTs = new Long (r.getLong(18));
			validFrom = r.getLong(19);
			validTo = r.getLong(20);
		} catch(SQLException sqle) {
			SDMSThread.doTrace(null, "SQL Error : " + sqle.getMessage(), SDMSThread.SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(env, "01110182045", "ResourceRequirement: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
		if(validTo < env.lowestActiveVersion) return null;
		return new SDMSResourceRequirementGeneric(id,
		                nrId,
		                seId,
		                amount,
		                keepMode,
		                isSticky,
		                stickyName,
		                stickyParent,
		                rsmpId,
		                expiredAmount,
		                expiredBase,
		                ignoreOnRerun,
		                lockmode,
		                condition,
		                creatorUId,
		                createTs,
		                changerUId,
		                changeTs,
		                validFrom, validTo);
	}

	protected void loadTable(SystemEnvironment env)
	throws SQLException, SDMSException
	{
		int read = 0;
		int loaded = 0;

		final boolean postgres = SystemEnvironment.isPostgreSQL;
		String squote = SystemEnvironment.SQUOTE;
		String equote = SystemEnvironment.EQUOTE;
		Statement stmt = env.dbConnection.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT " +
		                                   squote + tableName() + equote + ".ID" +
		                                   ", " + squote + "NR_ID" + equote +
		                                   ", " + squote + "SE_ID" + equote +
		                                   ", " + squote + "AMOUNT" + equote +
		                                   ", " + squote + "KEEP_MODE" + equote +
		                                   ", " + squote + "IS_STICKY" + equote +
		                                   ", " + squote + "STICKY_NAME" + equote +
		                                   ", " + squote + "STICKY_PARENT" + equote +
		                                   ", " + squote + "RSMP_ID" + equote +
		                                   ", " + squote + "EXPIRED_AMOUNT" + equote +
		                                   ", " + squote + "EXPIRED_BASE" + equote +
		                                   ", " + squote + "IGNORE_ON_RERUN" + equote +
		                                   ", " + squote + "LOCKMODE" + equote +
		                                   ", " + squote + "CONDITION" + equote +
		                                   ", " + squote + "CREATOR_U_ID" + equote +
		                                   ", " + squote + "CREATE_TS" + equote +
		                                   ", " + squote + "CHANGER_U_ID" + equote +
		                                   ", " + squote + "CHANGE_TS" + equote +
		                                   ", VALID_FROM, VALID_TO " +
		                                   " FROM " + squote + tableName() + equote +
		                                   " WHERE VALID_TO >= " + (postgres ?
		                                                   "CAST (\'" + env.lowestActiveVersion + "\' AS DECIMAL)" :
		                                                   "" + env.lowestActiveVersion) +
		                                   ""						  );
		while(rset.next()) {
			if(loadObject(env, rset)) ++loaded;
			++read;
		}
		stmt.close();
		SDMSThread.doTrace(null, "Read " + read + ", Loaded " + loaded + " rows for " + tableName(), SDMSThread.SEVERITY_INFO);
	}

	public String checkIndex(SDMSObject o)
	throws SDMSException
	{
		String out = "";
		boolean ok;
		ok =  idx_nrId.check(((SDMSResourceRequirementGeneric) o).nrId, o);
		out = out + "idx_nrId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_seId.check(((SDMSResourceRequirementGeneric) o).seId, o);
		out = out + "idx_seId: " + (ok ? "ok" : "missing") + "\n";
		ok =  idx_rsmpId.check(((SDMSResourceRequirementGeneric) o).rsmpId, o);
		out = out + "idx_rsmpId: " + (ok ? "ok" : "missing") + "\n";
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceRequirementGeneric) o).seId);
		k.add(((SDMSResourceRequirementGeneric) o).nrId);
		ok =  idx_seId_nrId.check(k, o);
		out = out + "idx_seId_nrId: " + (ok ? "ok" : "missing") + "\n";
		return out;
	}

	protected void index(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		index(env, o, -1);
	}

	protected void index(SystemEnvironment env, SDMSObject o, long indexMember)
	throws SDMSException
	{
		idx_nrId.put(env, ((SDMSResourceRequirementGeneric) o).nrId, o, ((1 & indexMember) != 0));
		idx_seId.put(env, ((SDMSResourceRequirementGeneric) o).seId, o, ((2 & indexMember) != 0));
		idx_rsmpId.put(env, ((SDMSResourceRequirementGeneric) o).rsmpId, o, ((4 & indexMember) != 0));
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceRequirementGeneric) o).seId);
		k.add(((SDMSResourceRequirementGeneric) o).nrId);
		idx_seId_nrId.put(env, k, o, ((8 & indexMember) != 0));
	}

	protected  void unIndex(SystemEnvironment env, SDMSObject o)
	throws SDMSException
	{
		idx_nrId.remove(env, ((SDMSResourceRequirementGeneric) o).nrId, o);
		idx_seId.remove(env, ((SDMSResourceRequirementGeneric) o).seId, o);
		idx_rsmpId.remove(env, ((SDMSResourceRequirementGeneric) o).rsmpId, o);
		SDMSKey k;
		k = new SDMSKey();
		k.add(((SDMSResourceRequirementGeneric) o).seId);
		k.add(((SDMSResourceRequirementGeneric) o).nrId);
		idx_seId_nrId.remove(env, k, o);
	}

	public static SDMSResourceRequirement getObject(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceRequirement) table.get(env, id);
	}

	public static SDMSResourceRequirement getObjectForUpdate(SystemEnvironment env, Long id)
	throws SDMSException
	{
		return (SDMSResourceRequirement) table.getForUpdate(env, id);
	}

	public static SDMSResourceRequirement getObject(SystemEnvironment env, Long id, long version)
	throws SDMSException
	{
		return (SDMSResourceRequirement) table.get(env, id, version);
	}

	public static SDMSResourceRequirement idx_seId_nrId_getUnique(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceRequirement)  SDMSResourceRequirementTableGeneric.idx_seId_nrId.getUnique(env, key);
	}

	public static SDMSResourceRequirement idx_seId_nrId_getUniqueForUpdate(SystemEnvironment env, Object key)
	throws SDMSException
	{
		return (SDMSResourceRequirement)  SDMSResourceRequirementTableGeneric.idx_seId_nrId.getUniqueForUpdate(env, key);
	}

	public static SDMSResourceRequirement idx_seId_nrId_getUnique(SystemEnvironment env, Object key, long version)
	throws SDMSException
	{
		return (SDMSResourceRequirement)  SDMSResourceRequirementTableGeneric.idx_seId_nrId.getUnique(env, key, version);
	}

	public String tableName()
	{
		return tableName;
	}
	public String[] columnNames()
	{
		return columnNames;
	}
}
