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

public class SDMSMasterAllocationGeneric extends SDMSObject
	implements Cloneable
{

	public static final int N = Lockmode.N;
	public static final int X = Lockmode.X;
	public static final int SX = Lockmode.SX;
	public static final int S = Lockmode.S;
	public static final int SC = Lockmode.SC;

	public final static int nr_id = 1;
	public final static int nr_raId = 2;
	public final static int nr_smeId = 3;
	public final static int nr_amount = 4;
	public final static int nr_stickyName = 5;
	public final static int nr_stickyParent = 6;
	public final static int nr_lockmode = 7;
	public final static int nr_creatorUId = 8;
	public final static int nr_createTs = 9;
	public final static int nr_changerUId = 10;
	public final static int nr_changeTs = 11;

	public static String tableName = SDMSMasterAllocationTableGeneric.tableName;

	protected Long raId;
	protected Long smeId;
	protected Integer amount;
	protected String stickyName;
	protected Long stickyParent;
	protected Integer lockmode;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSMasterAllocationGeneric(
	        SystemEnvironment env,
	        Long p_raId,
	        Long p_smeId,
	        Integer p_amount,
	        String p_stickyName,
	        Long p_stickyParent,
	        Integer p_lockmode,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSMasterAllocationTableGeneric.table);
		raId = p_raId;
		smeId = p_smeId;
		amount = p_amount;
		if (p_stickyName != null && p_stickyName.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(MasterAllocation) Length of $1 exceeds maximum length $2", "stickyName", "64")
			);
		}
		stickyName = p_stickyName;
		stickyParent = p_stickyParent;
		lockmode = p_lockmode;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getRaId (SystemEnvironment env)
	throws SDMSException
	{
		return (raId);
	}

	public	SDMSMasterAllocationGeneric setRaId (SystemEnvironment env, Long p_raId)
	throws SDMSException
	{
		if(raId.equals(p_raId)) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.raId = p_raId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	SDMSMasterAllocationGeneric setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.smeId = p_smeId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (amount);
	}

	public	SDMSMasterAllocationGeneric setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		if(p_amount != null && p_amount.equals(amount)) return this;
		if(p_amount == null && amount == null) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.amount = p_amount;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public String getStickyName (SystemEnvironment env)
	throws SDMSException
	{
		return (stickyName);
	}

	public	SDMSMasterAllocationGeneric setStickyName (SystemEnvironment env, String p_stickyName)
	throws SDMSException
	{
		if(p_stickyName != null && p_stickyName.equals(stickyName)) return this;
		if(p_stickyName == null && stickyName == null) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			if (p_stickyName != null && p_stickyName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(MasterAllocation) Length of $1 exceeds maximum length $2", "stickyName", "64")
				);
			}
			o.stickyName = p_stickyName;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getStickyParent (SystemEnvironment env)
	throws SDMSException
	{
		return (stickyParent);
	}

	public	SDMSMasterAllocationGeneric setStickyParent (SystemEnvironment env, Long p_stickyParent)
	throws SDMSException
	{
		if(p_stickyParent != null && p_stickyParent.equals(stickyParent)) return this;
		if(p_stickyParent == null && stickyParent == null) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.stickyParent = p_stickyParent;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Integer getLockmode (SystemEnvironment env)
	throws SDMSException
	{
		return (lockmode);
	}

	public String getLockmodeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getLockmode (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
		case SDMSMasterAllocation.N:
			return "N";
		case SDMSMasterAllocation.X:
			return "X";
		case SDMSMasterAllocation.SX:
			return "SX";
		case SDMSMasterAllocation.S:
			return "S";
		case SDMSMasterAllocation.SC:
			return "SC";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown MasterAllocation.lockmode: $1",
		                          getLockmode (env)));
	}

	public	SDMSMasterAllocationGeneric setLockmode (SystemEnvironment env, Integer p_lockmode)
	throws SDMSException
	{
		if(p_lockmode != null && p_lockmode.equals(lockmode)) return this;
		if(p_lockmode == null && lockmode == null) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.lockmode = p_lockmode;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	SDMSMasterAllocationGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.creatorUId = p_creatorUId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	SDMSMasterAllocationGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.createTs = p_createTs;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	SDMSMasterAllocationGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSMasterAllocationGeneric) change(env);
			o.changerUId = p_changerUId;
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	SDMSMasterAllocationGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSMasterAllocationGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSMasterAllocationGeneric) change(env);
			o.changeTs = p_changeTs;
			o.changerUId = env.cEnv.euid();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	public SDMSMasterAllocationGeneric set_RaIdStickyParentStickyName (SystemEnvironment env, Long p_raId, Long p_stickyParent, String p_stickyName)
	throws SDMSException
	{
		SDMSMasterAllocationGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(MasterAllocation) Change of system object not allowed")
				);
			}
			o = (SDMSMasterAllocationGeneric) change(env);
			o.raId = p_raId;
			o.stickyParent = p_stickyParent;
			if (p_stickyName != null && p_stickyName.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290025",
				                        "(MasterAllocation) Length of $1 exceeds maximum length $2", "changeTs", "64")
				);
			}
			o.stickyName = p_stickyName;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy()
	{
		return new SDMSMasterAllocation(this);
	}

	protected SDMSMasterAllocationGeneric(Long p_id,
	                                      Long p_raId,
	                                      Long p_smeId,
	                                      Integer p_amount,
	                                      String p_stickyName,
	                                      Long p_stickyParent,
	                                      Integer p_lockmode,
	                                      Long p_creatorUId,
	                                      Long p_createTs,
	                                      Long p_changerUId,
	                                      Long p_changeTs,
	                                      long p_validFrom, long p_validTo)
	{
		id     = p_id;
		raId = p_raId;
		smeId = p_smeId;
		amount = p_amount;
		stickyName = p_stickyName;
		stickyParent = p_stickyParent;
		lockmode = p_lockmode;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		validFrom = p_validFrom;
		validTo   = p_validTo;
	}

	protected String tableName()
	{
		return tableName;
	}

	protected void insertDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pInsert == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				String squote = "";
				String equote = "";
				if (driverName.startsWith("MySQL")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "INSERT INTO MASTER_ALLOCATION (" +
				        "ID" +
				        ", " + squote + "RA_ID" + equote +
				        ", " + squote + "SME_ID" + equote +
				        ", " + squote + "AMOUNT" + equote +
				        ", " + squote + "STICKY_NAME" + equote +
				        ", " + squote + "STICKY_PARENT" + equote +
				        ", " + squote + "LOCKMODE" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ") VALUES (?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ", ?" +
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "MasterAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, raId.longValue());
			pInsert.setLong (3, smeId.longValue());
			if (amount == null)
				pInsert.setNull(4, Types.INTEGER);
			else
				pInsert.setInt(4, amount.intValue());
			if (stickyName == null)
				pInsert.setNull(5, Types.VARCHAR);
			else
				pInsert.setString(5, stickyName);
			if (stickyParent == null)
				pInsert.setNull(6, Types.INTEGER);
			else
				pInsert.setLong (6, stickyParent.longValue());
			if (lockmode == null)
				pInsert.setNull(7, Types.INTEGER);
			else
				pInsert.setInt(7, lockmode.intValue());
			pInsert.setLong (8, creatorUId.longValue());
			pInsert.setLong (9, createTs.longValue());
			pInsert.setLong (10, changerUId.longValue());
			pInsert.setLong (11, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "MasterAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM MASTER_ALLOCATION WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "MasterAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "MasterAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		if(pUpdate == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				String squote = "";
				String equote = "";
				if (driverName.startsWith("MySQL")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "UPDATE MASTER_ALLOCATION SET " +
				        "" + squote + "RA_ID" + equote + " = ? " +
				        ", " + squote + "SME_ID" + equote + " = ? " +
				        ", " + squote + "AMOUNT" + equote + " = ? " +
				        ", " + squote + "STICKY_NAME" + equote + " = ? " +
				        ", " + squote + "STICKY_PARENT" + equote + " = ? " +
				        ", " + squote + "LOCKMODE" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "MasterAllocation: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, raId.longValue());
			pUpdate.setLong (2, smeId.longValue());
			if (amount == null)
				pUpdate.setNull(3, Types.INTEGER);
			else
				pUpdate.setInt(3, amount.intValue());
			if (stickyName == null)
				pUpdate.setNull(4, Types.VARCHAR);
			else
				pUpdate.setString(4, stickyName);
			if (stickyParent == null)
				pUpdate.setNull(5, Types.INTEGER);
			else
				pUpdate.setLong (5, stickyParent.longValue());
			if (lockmode == null)
				pUpdate.setNull(6, Types.INTEGER);
			else
				pUpdate.setInt(6, lockmode.intValue());
			pUpdate.setLong (7, creatorUId.longValue());
			pUpdate.setLong (8, createTs.longValue());
			pUpdate.setLong (9, changerUId.longValue());
			pUpdate.setLong (10, changeTs.longValue());
			pUpdate.setLong(11, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "MasterAllocation: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkLockmode(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
		case SDMSMasterAllocation.N:
		case SDMSMasterAllocation.X:
		case SDMSMasterAllocation.SX:
		case SDMSMasterAllocation.S:
		case SDMSMasterAllocation.SC:
			return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : MasterAllocation", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "raId : " + raId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "smeId : " + smeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "amount : " + amount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "stickyName : " + stickyName, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "stickyParent : " + stickyParent, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lockmode : " + lockmode, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validTo : " + validTo, SDMSThread.SEVERITY_MESSAGE);
	}

	public String toString(int indent)
	{
		StringBuffer sb = new StringBuffer(indent + 1);
		for(int i = 0; i < indent; ++i) sb.append(" ");
		String indentString = new String(sb);
		String result =
		        indentString + "id : " + id + "\n" +
		        indentString + "raId         : " + raId + "\n" +
		        indentString + "smeId        : " + smeId + "\n" +
		        indentString + "amount       : " + amount + "\n" +
		        indentString + "stickyName   : " + stickyName + "\n" +
		        indentString + "stickyParent : " + stickyParent + "\n" +
		        indentString + "lockmode     : " + lockmode + "\n" +
		        indentString + "creatorUId   : " + creatorUId + "\n" +
		        indentString + "createTs     : " + createTs + "\n" +
		        indentString + "changerUId   : " + changerUId + "\n" +
		        indentString + "changeTs     : " + changeTs + "\n" +
		        indentString + "validFrom : " + validFrom + "\n" +
		        indentString + "validTo : " + validTo + "\n";
		return result;
	}

	public String toString()
	{
		String result = toString(0);
		return result;
	}
}
