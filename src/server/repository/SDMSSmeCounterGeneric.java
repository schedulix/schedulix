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

	public final static String __version = "SDMSSmeCounterGeneric $Revision: 2.4 $ / @(#) $Id: generate.py,v 2.42.2.7 2013/04/17 12:40:29 ronald Exp $";

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

	private static PreparedStatement pUpdate;
	private static PreparedStatement pDelete;
	private static PreparedStatement pInsert;

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

	public	SDMSSmeCounterGeneric setJahr (SystemEnvironment env, Integer p_jahr)
	throws SDMSException
	{
		if(jahr.equals(p_jahr)) return this;
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

	public Integer getMonat (SystemEnvironment env)
	throws SDMSException
	{
		return (monat);
	}

	public	SDMSSmeCounterGeneric setMonat (SystemEnvironment env, Integer p_monat)
	throws SDMSException
	{
		if(monat.equals(p_monat)) return this;
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

	public Integer getTag (SystemEnvironment env)
	throws SDMSException
	{
		return (tag);
	}

	public	SDMSSmeCounterGeneric setTag (SystemEnvironment env, Integer p_tag)
	throws SDMSException
	{
		if(tag.equals(p_tag)) return this;
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

	public Integer getAnzahl (SystemEnvironment env)
	throws SDMSException
	{
		return (anzahl);
	}

	public	SDMSSmeCounterGeneric setAnzahl (SystemEnvironment env, Integer p_anzahl)
	throws SDMSException
	{
		if(anzahl.equals(p_anzahl)) return this;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
			o.anzahl = p_anzahl;
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

	public Long getChecksum (SystemEnvironment env)
	throws SDMSException
	{
		return (checksum);
	}

	public	SDMSSmeCounterGeneric setChecksum (SystemEnvironment env, Long p_checksum)
	throws SDMSException
	{
		if(checksum.equals(p_checksum)) return this;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
			o.checksum = p_checksum;
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

	SDMSSmeCounterGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
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

	SDMSSmeCounterGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(SmeCounter) Change of system object not allowed")
				);
			}
			o = (SDMSSmeCounterGeneric) change(env);
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

	public	SDMSSmeCounterGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSSmeCounterGeneric) change(env);
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

	SDMSSmeCounterGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSSmeCounterGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSSmeCounterGeneric) change(env);
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
		return new SDMSSmeCounter(this);
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
				        "INSERT INTO SME_COUNTER (" +
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
				pInsert = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110181952", "SmeCounter: $1\n$2", stmt, sqle.toString()));
			}
		}

		try {
			pInsert.clearParameters();
			pInsert.setLong(1, id.longValue());
			pInsert.setInt(2, jahr.intValue());
			pInsert.setInt(3, monat.intValue());
			pInsert.setInt(4, tag.intValue());
			pInsert.setInt(5, anzahl.intValue());
			pInsert.setLong (6, checksum.longValue());
			pInsert.setLong (7, creatorUId.longValue());
			pInsert.setLong (8, createTs.longValue());
			pInsert.setLong (9, changerUId.longValue());
			pInsert.setLong (10, changeTs.longValue());
			pInsert.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110181954", "SmeCounter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		if(pDelete == null) {
			try {
				stmt =
				        "DELETE FROM SME_COUNTER WHERE ID = ?";
				pDelete = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182001", "SmeCounter: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pDelete.clearParameters();
			pDelete.setLong(1, id.longValue());
			pDelete.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182002", "SmeCounter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE SME_COUNTER SET " +
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
				pUpdate = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {

				throw new FatalException(new SDMSMessage(env, "01110182005", "SmeCounter: $1\n$2", stmt, sqle.toString()));
			}
		}
		try {
			pUpdate.clearParameters();
			pUpdate.setInt(1, jahr.intValue());
			pUpdate.setInt(2, monat.intValue());
			pUpdate.setInt(3, tag.intValue());
			pUpdate.setInt(4, anzahl.intValue());
			pUpdate.setLong (5, checksum.longValue());
			pUpdate.setLong (6, creatorUId.longValue());
			pUpdate.setLong (7, createTs.longValue());
			pUpdate.setLong (8, changerUId.longValue());
			pUpdate.setLong (9, changeTs.longValue());
			pUpdate.setLong(10, id.longValue());
			pUpdate.executeUpdate();
		} catch(SQLException sqle) {

			throw new FatalException(new SDMSMessage(env, "01110182006", "SmeCounter: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
