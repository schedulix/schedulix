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

public class SDMSSmeCounterGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_jahr = 2;
	public final static int nr_monat = 3;
	public final static int nr_tag = 4;
	public final static int nr_anzahl = 5;
	public final static int nr_checksum = 6;
	public final static int nr_creatorUId = 7;
	public final static int nr_createTs = 8;
	public final static int nr_changerUId = 9;
	public final static int nr_changeTs = 10;

	public static String tableName = SDMSSmeCounterTableGeneric.tableName;

	protected Integer jahr;
	protected Integer monat;
	protected Integer tag;
	protected Integer anzahl;
	protected Long checksum;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSSmeCounterGeneric(
	        SystemEnvironment env,
	        Integer p_jahr,
	        Integer p_monat,
	        Integer p_tag,
	        Integer p_anzahl,
	        Long p_checksum,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSSmeCounterTableGeneric.table);
		jahr = p_jahr;
		monat = p_monat;
		tag = p_tag;
		anzahl = p_anzahl;
		checksum = p_checksum;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Integer getJahr (SystemEnvironment env)
	throws SDMSException
	{
		return (jahr);
	}

	public	void setJahr (SystemEnvironment env, Integer p_jahr)
	throws SDMSException
	{
		if(jahr.equals(p_jahr)) return;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
			o.jahr = p_jahr;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getMonat (SystemEnvironment env)
	throws SDMSException
	{
		return (monat);
	}

	public	void setMonat (SystemEnvironment env, Integer p_monat)
	throws SDMSException
	{
		if(monat.equals(p_monat)) return;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
			o.monat = p_monat;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getTag (SystemEnvironment env)
	throws SDMSException
	{
		return (tag);
	}

	public	void setTag (SystemEnvironment env, Integer p_tag)
	throws SDMSException
	{
		if(tag.equals(p_tag)) return;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
			o.tag = p_tag;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getAnzahl (SystemEnvironment env)
	throws SDMSException
	{
		return (anzahl);
	}

	public	void setAnzahl (SystemEnvironment env, Integer p_anzahl)
	throws SDMSException
	{
		if(anzahl.equals(p_anzahl)) return;
		SDMSSmeCounterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSmeCounterGeneric) change(env);
		o.anzahl = p_anzahl;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getChecksum (SystemEnvironment env)
	throws SDMSException
	{
		return (checksum);
	}

	public	void setChecksum (SystemEnvironment env, Long p_checksum)
	throws SDMSException
	{
		if(checksum.equals(p_checksum)) return;
		SDMSSmeCounterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSmeCounterGeneric) change(env);
		o.checksum = p_checksum;
		o.changerUId = env.cEnv.uid();
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
		SDMSSmeCounterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSmeCounterGeneric) change(env);
		o.creatorUId = p_creatorUId;
		o.changerUId = env.cEnv.uid();
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
		SDMSSmeCounterGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSmeCounterGeneric) change(env);
		o.createTs = p_createTs;
		o.changerUId = env.cEnv.uid();
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
		SDMSSmeCounterGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSmeCounterGeneric) change(env);
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
		SDMSSmeCounterGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSSmeCounterGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSSmeCounterGeneric set_JahrMonatTag (SystemEnvironment env, Integer p_jahr, Integer p_monat, Integer p_tag)
	throws SDMSException
	{
		SDMSSmeCounterGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
			o.jahr = p_jahr;
			o.monat = p_monat;
			o.tag = p_tag;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return o;
	}

	protected SDMSProxy toProxy(SystemEnvironment sysEnv)
	{
		return SDMSSmeCounter.getProxy(sysEnv, this);
	}

	protected SDMSSmeCounterGeneric(Long p_id,
	                                Integer p_jahr,
	                                Integer p_monat,
	                                Integer p_tag,
	                                Integer p_anzahl,
	                                Long p_checksum,
	                                Long p_creatorUId,
	                                Long p_createTs,
	                                Long p_changerUId,
	                                Long p_changeTs,
	                                long p_validFrom, long p_validTo)
	{
		id     = p_id;
		jahr = p_jahr;
		monat = p_monat;
		tag = p_tag;
		anzahl = p_anzahl;
		checksum = p_checksum;
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
				        "INSERT INTO " + squote + "SME_COUNTER" + equote + " (" +
				        "ID" +
				        ", " + squote + "JAHR" + equote +
				        ", " + squote + "MONAT" + equote +
				        ", " + squote + "TAG" + equote +
				        ", " + squote + "ANZAHL" + equote +
				        ", " + squote + "CHECKSUM" + equote +
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
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "SmeCounter: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setInt(2, jahr.intValue());
			myInsert.setInt(3, monat.intValue());
			myInsert.setInt(4, tag.intValue());
			myInsert.setInt(5, anzahl.intValue());
			myInsert.setLong (6, checksum.longValue());
			myInsert.setLong (7, creatorUId.longValue());
			myInsert.setLong (8, createTs.longValue());
			myInsert.setLong (9, changerUId.longValue());
			myInsert.setLong (10, changeTs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "SmeCounter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "DELETE FROM " + squote + "SME_COUNTER" + equote + " WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "SmeCounter: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "SmeCounter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myUpdate;
		if(pUpdate[env.dbConnectionNr] == null) {
			try {
				String squote = SystemEnvironment.SQUOTE;
				String equote = SystemEnvironment.EQUOTE;
				stmt =
				        "UPDATE " + squote + "SME_COUNTER" + equote + " SET " +
				        "" + squote + "JAHR" + equote + " = ? " +
				        ", " + squote + "MONAT" + equote + " = ? " +
				        ", " + squote + "TAG" + equote + " = ? " +
				        ", " + squote + "ANZAHL" + equote + " = ? " +
				        ", " + squote + "CHECKSUM" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "SmeCounter: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setInt(1, jahr.intValue());
			myUpdate.setInt(2, monat.intValue());
			myUpdate.setInt(3, tag.intValue());
			myUpdate.setInt(4, anzahl.intValue());
			myUpdate.setLong (5, checksum.longValue());
			myUpdate.setLong (6, creatorUId.longValue());
			myUpdate.setLong (7, createTs.longValue());
			myUpdate.setLong (8, changerUId.longValue());
			myUpdate.setLong (9, changeTs.longValue());
			myUpdate.setLong(10, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "SmeCounter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : SmeCounter", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "jahr : " + jahr, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "monat : " + monat, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "tag : " + tag, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "anzahl : " + anzahl, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "checksum : " + checksum, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "jahr       : " + jahr + "\n" +
		        indentString + "monat      : " + monat + "\n" +
		        indentString + "tag        : " + tag + "\n" +
		        indentString + "anzahl     : " + anzahl + "\n" +
		        indentString + "checksum   : " + checksum + "\n" +
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
