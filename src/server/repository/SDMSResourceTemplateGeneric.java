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

public class SDMSResourceTemplateGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_nrId = 2;
	public final static int nr_seId = 3;
	public final static int nr_ownerId = 4;
	public final static int nr_rsdId = 5;
	public final static int nr_RequestableAmount = 6;
	public final static int nr_amount = 7;
	public final static int nr_isOnline = 8;
	public final static int nr_creatorUId = 9;
	public final static int nr_createTs = 10;
	public final static int nr_changerUId = 11;
	public final static int nr_changeTs = 12;

	public static String tableName = SDMSResourceTemplateTableGeneric.tableName;

	protected Long nrId;
	protected Long seId;
	protected Long ownerId;
	protected Long rsdId;
	protected Integer RequestableAmount;
	protected Integer amount;
	protected Boolean isOnline;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[50];
	private static PreparedStatement pDelete[] = new PreparedStatement[50];
	private static PreparedStatement pInsert[] = new PreparedStatement[50];

	public SDMSResourceTemplateGeneric(
	        SystemEnvironment env,
	        Long p_nrId,
	        Long p_seId,
	        Long p_ownerId,
	        Long p_rsdId,
	        Integer p_RequestableAmount,
	        Integer p_amount,
	        Boolean p_isOnline,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSResourceTemplateTableGeneric.table);
		nrId = p_nrId;
		seId = p_seId;
		ownerId = p_ownerId;
		rsdId = p_rsdId;
		RequestableAmount = p_RequestableAmount;
		amount = p_amount;
		isOnline = p_isOnline;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getNrId (SystemEnvironment env)
	throws SDMSException
	{
		return (nrId);
	}

	public	void setNrId (SystemEnvironment env, Long p_nrId)
	throws SDMSException
	{
		if(nrId.equals(p_nrId)) return;
		SDMSResourceTemplateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
				);
			}
			o = (SDMSResourceTemplateGeneric) change(env);
			o.nrId = p_nrId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 17);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		return (seId);
	}

	public	void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		if(seId.equals(p_seId)) return;
		SDMSResourceTemplateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
				);
			}
			o = (SDMSResourceTemplateGeneric) change(env);
			o.seId = p_seId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 18);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getOwnerId (SystemEnvironment env)
	throws SDMSException
	{
		return (ownerId);
	}

	public	void setOwnerId (SystemEnvironment env, Long p_ownerId)
	throws SDMSException
	{
		if(ownerId.equals(p_ownerId)) return;
		SDMSResourceTemplateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
				);
			}
			o = (SDMSResourceTemplateGeneric) change(env);
			o.ownerId = p_ownerId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Long getRsdId (SystemEnvironment env)
	throws SDMSException
	{
		return (rsdId);
	}

	public	void setRsdId (SystemEnvironment env, Long p_rsdId)
	throws SDMSException
	{
		if(p_rsdId != null && p_rsdId.equals(rsdId)) return;
		if(p_rsdId == null && rsdId == null) return;
		SDMSResourceTemplateGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
				);
			}
			o = (SDMSResourceTemplateGeneric) change(env);
			o.rsdId = p_rsdId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 8);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getRequestableAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (RequestableAmount);
	}

	public	void setRequestableAmount (SystemEnvironment env, Integer p_RequestableAmount)
	throws SDMSException
	{
		if(p_RequestableAmount != null && p_RequestableAmount.equals(RequestableAmount)) return;
		if(p_RequestableAmount == null && RequestableAmount == null) return;
		SDMSResourceTemplateGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null) o = (SDMSResourceTemplateGeneric) change(env);
		o.RequestableAmount = p_RequestableAmount;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getAmount (SystemEnvironment env)
	throws SDMSException
	{
		return (amount);
	}

	public	void setAmount (SystemEnvironment env, Integer p_amount)
	throws SDMSException
	{
		if(p_amount != null && p_amount.equals(amount)) return;
		if(p_amount == null && amount == null) return;
		SDMSResourceTemplateGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null) o = (SDMSResourceTemplateGeneric) change(env);
		o.amount = p_amount;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsOnline (SystemEnvironment env)
	throws SDMSException
	{
		return (isOnline);
	}

	public	void setIsOnline (SystemEnvironment env, Boolean p_isOnline)
	throws SDMSException
	{
		if(isOnline.equals(p_isOnline)) return;
		SDMSResourceTemplateGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null) o = (SDMSResourceTemplateGeneric) change(env);
		o.isOnline = p_isOnline;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		return (creatorUId);
	}

	void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return;
		SDMSResourceTemplateGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null) o = (SDMSResourceTemplateGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		return (createTs);
	}

	void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return;
		SDMSResourceTemplateGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(ResourceTemplate) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null) o = (SDMSResourceTemplateGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		return (changerUId);
	}

	public	void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSResourceTemplateGeneric o = this;
		if (o.versions.o_v == null) o = (SDMSResourceTemplateGeneric) change(env);
		o.changerUId = p_changerUId;
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		return (changeTs);
	}

	void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return;
		SDMSResourceTemplateGeneric o = this;
		if (o.versions.o_v == null) o = (SDMSResourceTemplateGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSResourceTemplateGeneric set_NrIdSeId (SystemEnvironment env, Long p_nrId, Long p_seId)
	throws SDMSException
	{
		SDMSResourceTemplateGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(ResourceTemplate) Change of system object not allowed")
				);
			}
			o = (SDMSResourceTemplateGeneric) change(env);
			o.nrId = p_nrId;
			o.seId = p_seId;
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
		return new SDMSResourceTemplate(this);
	}

	protected SDMSResourceTemplateGeneric(Long p_id,
	                                      Long p_nrId,
	                                      Long p_seId,
	                                      Long p_ownerId,
	                                      Long p_rsdId,
	                                      Integer p_RequestableAmount,
	                                      Integer p_amount,
	                                      Boolean p_isOnline,
	                                      Long p_creatorUId,
	                                      Long p_createTs,
	                                      Long p_changerUId,
	                                      Long p_changeTs,
	                                      long p_validFrom, long p_validTo)
	{
		id     = p_id;
		nrId = p_nrId;
		seId = p_seId;
		ownerId = p_ownerId;
		rsdId = p_rsdId;
		RequestableAmount = p_RequestableAmount;
		amount = p_amount;
		isOnline = p_isOnline;
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
		PreparedStatement myInsert;
		if(pInsert[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "INSERT INTO RESOURCE_TEMPLATE (" +
				        "ID" +
				        ", " + squote + "NR_ID" + equote +
				        ", " + squote + "SE_ID" + equote +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "RSD_ID" + equote +
				        ", " + squote + "REQUESTABLE_AMOUNT" + equote +
				        ", " + squote + "AMOUNT" + equote +
				        ", " + squote + "IS_ONLINE" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ", VALID_FROM, VALID_TO" +
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
				        ", ?" +
				        ", ?, ?" +
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "ResourceTemplate: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];

		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setLong (2, nrId.longValue());
			myInsert.setLong (3, seId.longValue());
			myInsert.setLong (4, ownerId.longValue());
			if (rsdId == null)
				myInsert.setNull(5, Types.INTEGER);
			else
				myInsert.setLong (5, rsdId.longValue());
			if (RequestableAmount == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setInt(6, RequestableAmount.intValue());
			if (amount == null)
				myInsert.setNull(7, Types.INTEGER);
			else
				myInsert.setInt(7, amount.intValue());
			myInsert.setInt (8, isOnline.booleanValue() ? 1 : 0);
			myInsert.setLong (9, creatorUId.longValue());
			myInsert.setLong (10, createTs.longValue());
			myInsert.setLong (11, changerUId.longValue());
			myInsert.setLong (12, changeTs.longValue());
			myInsert.setLong(13, env.tx.versionId);
			myInsert.setLong(14, Long.MAX_VALUE);
			myInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "ResourceTemplate: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		closeDBObject(env);
		insertDBObject(env);
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		closeDBObject(env);
	}

	private void closeDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				final String driverName = env.dbConnection.getMetaData().getDriverName();
				final boolean postgres = driverName.startsWith("PostgreSQL");
				stmt =
				        "UPDATE RESOURCE_TEMPLATE " +
				        "SET VALID_TO = ?, " +
				        "    CHANGE_TS = ?, " +
				        "    CHANGER_U_ID = ? " +
				        "WHERE ID = ?" +
				        "  AND VALID_TO = " + (postgres ?
				                               "CAST (\'" +  Long.MAX_VALUE + "\' AS DECIMAL)" :
				                               "" + Long.MAX_VALUE);
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				// Can't prepare statement
				throw new FatalException(new SDMSMessage(env, "01110181955", "ResourceTemplate : $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setLong(1, env.tx.versionId);
			myUpdate.setLong(2, changeTs.longValue());
			myUpdate.setLong(3, changerUId.longValue());
			myUpdate.setLong(4, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new SDMSSQLException(new SDMSMessage(env, "01110181956", "ResourceTemplate: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : ResourceTemplate", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nrId : " + nrId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "seId : " + seId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "rsdId : " + rsdId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "RequestableAmount : " + RequestableAmount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "amount : " + amount, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isOnline : " + isOnline, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validFrom : " + validFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "validTo : " + validTo, SDMSThread.SEVERITY_MESSAGE);
		dumpVersions(SDMSThread.SEVERITY_MESSAGE);
	}

	public String toString(int indent)
	{
		StringBuffer sb = new StringBuffer(indent + 1);
		for(int i = 0; i < indent; ++i) sb.append(" ");
		String indentString = new String(sb);
		String result =
		        indentString + "id : " + id + "\n" +
		        indentString + "nrId              : " + nrId + "\n" +
		        indentString + "seId              : " + seId + "\n" +
		        indentString + "ownerId           : " + ownerId + "\n" +
		        indentString + "rsdId             : " + rsdId + "\n" +
		        indentString + "RequestableAmount : " + RequestableAmount + "\n" +
		        indentString + "amount            : " + amount + "\n" +
		        indentString + "isOnline          : " + isOnline + "\n" +
		        indentString + "creatorUId        : " + creatorUId + "\n" +
		        indentString + "createTs          : " + createTs + "\n" +
		        indentString + "changerUId        : " + changerUId + "\n" +
		        indentString + "changeTs          : " + changeTs + "\n" +
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
