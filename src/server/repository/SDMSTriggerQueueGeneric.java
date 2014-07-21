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

public class SDMSTriggerQueueGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_smeId = 2;
	public final static int nr_trId = 3;
	public final static int nr_nextTriggerTime = 4;
	public final static int nr_timesChecked = 5;
	public final static int nr_timesTriggered = 6;
	public final static int nr_creatorUId = 7;
	public final static int nr_createTs = 8;
	public final static int nr_changerUId = 9;
	public final static int nr_changeTs = 10;

	public static String tableName = SDMSTriggerQueueTableGeneric.tableName;

	protected Long smeId;
	protected Long trId;
	protected Long nextTriggerTime;
	protected Integer timesChecked;
	protected Integer timesTriggered;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSTriggerQueueGeneric(
	        SystemEnvironment env,
	        Long p_smeId,
	        Long p_trId,
	        Long p_nextTriggerTime,
	        Integer p_timesChecked,
	        Integer p_timesTriggered,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSTriggerQueueTableGeneric.table);
		smeId = p_smeId;
		trId = p_trId;
		nextTriggerTime = p_nextTriggerTime;
		timesChecked = p_timesChecked;
		timesTriggered = p_timesTriggered;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getSmeId (SystemEnvironment env)
	throws SDMSException
	{
		return (smeId);
	}

	public	SDMSTriggerQueueGeneric setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
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

	public Long getTrId (SystemEnvironment env)
	throws SDMSException
	{
		return (trId);
	}

	public	SDMSTriggerQueueGeneric setTrId (SystemEnvironment env, Long p_trId)
	throws SDMSException
	{
		if(trId.equals(p_trId)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
			o.trId = p_trId;
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

	public Long getNextTriggerTime (SystemEnvironment env)
	throws SDMSException
	{
		return (nextTriggerTime);
	}

	public	SDMSTriggerQueueGeneric setNextTriggerTime (SystemEnvironment env, Long p_nextTriggerTime)
	throws SDMSException
	{
		if(nextTriggerTime.equals(p_nextTriggerTime)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
			o.nextTriggerTime = p_nextTriggerTime;
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

	public Integer getTimesChecked (SystemEnvironment env)
	throws SDMSException
	{
		return (timesChecked);
	}

	public	SDMSTriggerQueueGeneric setTimesChecked (SystemEnvironment env, Integer p_timesChecked)
	throws SDMSException
	{
		if(timesChecked.equals(p_timesChecked)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
			o.timesChecked = p_timesChecked;
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

	public Integer getTimesTriggered (SystemEnvironment env)
	throws SDMSException
	{
		return (timesTriggered);
	}

	public	SDMSTriggerQueueGeneric setTimesTriggered (SystemEnvironment env, Integer p_timesTriggered)
	throws SDMSException
	{
		if(timesTriggered.equals(p_timesTriggered)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
			o.timesTriggered = p_timesTriggered;
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

	SDMSTriggerQueueGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
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

	SDMSTriggerQueueGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
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

	public	SDMSTriggerQueueGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSTriggerQueueGeneric) change(env);
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

	SDMSTriggerQueueGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSTriggerQueueGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSTriggerQueueGeneric) change(env);
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

	public SDMSTriggerQueueGeneric set_SmeIdTrId (SystemEnvironment env, Long p_smeId, Long p_trId)
	throws SDMSException
	{
		SDMSTriggerQueueGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(TriggerQueue) Change of system object not allowed")
				);
			}
			o = (SDMSTriggerQueueGeneric) change(env);
			o.smeId = p_smeId;
			o.trId = p_trId;
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
		return new SDMSTriggerQueue(this);
	}

	protected SDMSTriggerQueueGeneric(Long p_id,
	                                  Long p_smeId,
	                                  Long p_trId,
	                                  Long p_nextTriggerTime,
	                                  Integer p_timesChecked,
	                                  Integer p_timesTriggered,
	                                  Long p_creatorUId,
	                                  Long p_createTs,
	                                  Long p_changerUId,
	                                  Long p_changeTs,
	                                  long p_validFrom, long p_validTo)
	{
		id     = p_id;
		smeId = p_smeId;
		trId = p_trId;
		nextTriggerTime = p_nextTriggerTime;
		timesChecked = p_timesChecked;
		timesTriggered = p_timesTriggered;
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
				if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "INSERT INTO TRIGGER_QUEUE (" +
				        "ID" +
				        ", " + squote + "SME_ID" + equote +
				        ", " + squote + "TR_ID" + equote +
				        ", " + squote + "NEXT_TRIGGER_TIME" + equote +
				        ", " + squote + "TIMES_CHECKED" + equote +
				        ", " + squote + "TIMES_TRIGGERED" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "TriggerQueue: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, smeId.longValue());
			pInsert.setLong (3, trId.longValue());
			pInsert.setLong (4, nextTriggerTime.longValue());
			pInsert.setInt(5, timesChecked.intValue());
			pInsert.setInt(6, timesTriggered.intValue());
			pInsert.setLong (7, creatorUId.longValue());
			pInsert.setLong (8, createTs.longValue());
			pInsert.setLong (9, changerUId.longValue());
			pInsert.setLong (10, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "TriggerQueue: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM TRIGGER_QUEUE WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "TriggerQueue: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "TriggerQueue: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
					squote = "`";
					equote = "`";
				}
				if (driverName.startsWith("Microsoft")) {
					squote = "[";
					equote = "]";
				}
				stmt =
				        "UPDATE TRIGGER_QUEUE SET " +
				        "" + squote + "SME_ID" + equote + " = ? " +
				        ", " + squote + "TR_ID" + equote + " = ? " +
				        ", " + squote + "NEXT_TRIGGER_TIME" + equote + " = ? " +
				        ", " + squote + "TIMES_CHECKED" + equote + " = ? " +
				        ", " + squote + "TIMES_TRIGGERED" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "TriggerQueue: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, smeId.longValue());
			pUpdate.setLong (2, trId.longValue());
			pUpdate.setLong (3, nextTriggerTime.longValue());
			pUpdate.setInt(4, timesChecked.intValue());
			pUpdate.setInt(5, timesTriggered.intValue());
			pUpdate.setLong (6, creatorUId.longValue());
			pUpdate.setLong (7, createTs.longValue());
			pUpdate.setLong (8, changerUId.longValue());
			pUpdate.setLong (9, changeTs.longValue());
			pUpdate.setLong(10, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "TriggerQueue: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : TriggerQueue", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "smeId : " + smeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "trId : " + trId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "nextTriggerTime : " + nextTriggerTime, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "timesChecked : " + timesChecked, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "timesTriggered : " + timesTriggered, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "smeId           : " + smeId + "\n" +
		        indentString + "trId            : " + trId + "\n" +
		        indentString + "nextTriggerTime : " + nextTriggerTime + "\n" +
		        indentString + "timesChecked    : " + timesChecked + "\n" +
		        indentString + "timesTriggered  : " + timesTriggered + "\n" +
		        indentString + "creatorUId      : " + creatorUId + "\n" +
		        indentString + "createTs        : " + createTs + "\n" +
		        indentString + "changerUId      : " + changerUId + "\n" +
		        indentString + "changeTs        : " + changeTs + "\n" +
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
