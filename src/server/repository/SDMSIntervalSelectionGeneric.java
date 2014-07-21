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

public class SDMSIntervalSelectionGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_intId = 2;
	public final static int nr_value = 3;
	public final static int nr_periodFrom = 4;
	public final static int nr_periodTo = 5;
	public final static int nr_creatorUId = 6;
	public final static int nr_createTs = 7;
	public final static int nr_changerUId = 8;
	public final static int nr_changeTs = 9;

	public static String tableName = SDMSIntervalSelectionTableGeneric.tableName;

	protected Long intId;
	protected Integer value;
	protected Long periodFrom;
	protected Long periodTo;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

	public SDMSIntervalSelectionGeneric(
	        SystemEnvironment env,
	        Long p_intId,
	        Integer p_value,
	        Long p_periodFrom,
	        Long p_periodTo,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSIntervalSelectionTableGeneric.table);
		intId = p_intId;
		value = p_value;
		periodFrom = p_periodFrom;
		periodTo = p_periodTo;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getIntId (SystemEnvironment env)
	throws SDMSException
	{
		return (intId);
	}

	public	SDMSIntervalSelectionGeneric setIntId (SystemEnvironment env, Long p_intId)
	throws SDMSException
	{
		if(intId.equals(p_intId)) return this;
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalSelection) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalSelectionGeneric) change(env);
			o.intId = p_intId;
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

	public Integer getValue (SystemEnvironment env)
	throws SDMSException
	{
		return (value);
	}

	public	SDMSIntervalSelectionGeneric setValue (SystemEnvironment env, Integer p_value)
	throws SDMSException
	{
		if(p_value != null && p_value.equals(value)) return this;
		if(p_value == null && value == null) return this;
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalSelection) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalSelectionGeneric) change(env);
			o.value = p_value;
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

	public Long getPeriodFrom (SystemEnvironment env)
	throws SDMSException
	{
		return (periodFrom);
	}

	public	SDMSIntervalSelectionGeneric setPeriodFrom (SystemEnvironment env, Long p_periodFrom)
	throws SDMSException
	{
		if(p_periodFrom != null && p_periodFrom.equals(periodFrom)) return this;
		if(p_periodFrom == null && periodFrom == null) return this;
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalSelection) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalSelectionGeneric) change(env);
			o.periodFrom = p_periodFrom;
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

	public Long getPeriodTo (SystemEnvironment env)
	throws SDMSException
	{
		return (periodTo);
	}

	public	SDMSIntervalSelectionGeneric setPeriodTo (SystemEnvironment env, Long p_periodTo)
	throws SDMSException
	{
		if(p_periodTo != null && p_periodTo.equals(periodTo)) return this;
		if(p_periodTo == null && periodTo == null) return this;
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalSelection) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalSelectionGeneric) change(env);
			o.periodTo = p_periodTo;
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

	SDMSIntervalSelectionGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalSelection) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalSelectionGeneric) change(env);
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

	SDMSIntervalSelectionGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(IntervalSelection) Change of system object not allowed")
				);
			}
			o = (SDMSIntervalSelectionGeneric) change(env);
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

	public	SDMSIntervalSelectionGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSIntervalSelectionGeneric) change(env);
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

	SDMSIntervalSelectionGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSIntervalSelectionGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSIntervalSelectionGeneric) change(env);
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

	protected SDMSProxy toProxy()
	{
		return new SDMSIntervalSelection(this);
	}

	protected SDMSIntervalSelectionGeneric(Long p_id,
	                                       Long p_intId,
	                                       Integer p_value,
	                                       Long p_periodFrom,
	                                       Long p_periodTo,
	                                       Long p_creatorUId,
	                                       Long p_createTs,
	                                       Long p_changerUId,
	                                       Long p_changeTs,
	                                       long p_validFrom, long p_validTo)
	{
		id     = p_id;
		intId = p_intId;
		value = p_value;
		periodFrom = p_periodFrom;
		periodTo = p_periodTo;
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
				        "INSERT INTO INTERVAL_SELECTION (" +
				        "ID" +
				        ", " + squote + "INT_ID" + equote +
				        ", " + squote + "VALUE" + equote +
				        ", " + squote + "PERIOD_FROM" + equote +
				        ", " + squote + "PERIOD_TO" + equote +
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
				        ")";
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "IntervalSelection: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setLong (2, intId.longValue());
			if (value == null)
				pInsert.setNull(3, Types.INTEGER);
			else
				pInsert.setInt(3, value.intValue());
			if (periodFrom == null)
				pInsert.setNull(4, Types.INTEGER);
			else
				pInsert.setLong (4, periodFrom.longValue());
			if (periodTo == null)
				pInsert.setNull(5, Types.INTEGER);
			else
				pInsert.setLong (5, periodTo.longValue());
			pInsert.setLong (6, creatorUId.longValue());
			pInsert.setLong (7, createTs.longValue());
			pInsert.setLong (8, changerUId.longValue());
			pInsert.setLong (9, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "IntervalSelection: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM INTERVAL_SELECTION WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "IntervalSelection: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "IntervalSelection: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE INTERVAL_SELECTION SET " +
				        "" + squote + "INT_ID" + equote + " = ? " +
				        ", " + squote + "VALUE" + equote + " = ? " +
				        ", " + squote + "PERIOD_FROM" + equote + " = ? " +
				        ", " + squote + "PERIOD_TO" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "IntervalSelection: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setLong (1, intId.longValue());
			if (value == null)
				pUpdate.setNull(2, Types.INTEGER);
			else
				pUpdate.setInt(2, value.intValue());
			if (periodFrom == null)
				pUpdate.setNull(3, Types.INTEGER);
			else
				pUpdate.setLong (3, periodFrom.longValue());
			if (periodTo == null)
				pUpdate.setNull(4, Types.INTEGER);
			else
				pUpdate.setLong (4, periodTo.longValue());
			pUpdate.setLong (5, creatorUId.longValue());
			pUpdate.setLong (6, createTs.longValue());
			pUpdate.setLong (7, changerUId.longValue());
			pUpdate.setLong (8, changeTs.longValue());
			pUpdate.setLong(9, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "IntervalSelection: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : IntervalSelection", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "intId : " + intId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "value : " + value, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "periodFrom : " + periodFrom, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "periodTo : " + periodTo, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "intId      : " + intId + "\n" +
		        indentString + "value      : " + value + "\n" +
		        indentString + "periodFrom : " + periodFrom + "\n" +
		        indentString + "periodTo   : " + periodTo + "\n" +
		        indentString + "creatorUId : " + creatorUId + "\n" +
		        indentString + "createTs   : " + createTs + "\n" +
		        indentString + "changerUId : " + changerUId + "\n" +
		        indentString + "changeTs   : " + changeTs + "\n" +
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
