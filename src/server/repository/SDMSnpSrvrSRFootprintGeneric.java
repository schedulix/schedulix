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

public class SDMSnpSrvrSRFootprintGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_sId = 2;
	public final static int nr_sessionId = 3;
	public final static int nr_fp = 4;
	public final static int nr_creatorUId = 5;
	public final static int nr_createTs = 6;
	public final static int nr_changerUId = 7;
	public final static int nr_changeTs = 8;

	public static String tableName = SDMSnpSrvrSRFootprintTableGeneric.tableName;

	protected Long sId;
	protected Integer sessionId;
	protected HashMap fp;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;
	public SDMSnpSrvrSRFootprintGeneric(
	        SystemEnvironment env,
	        Long p_sId,
	        Integer p_sessionId,
	        HashMap p_fp,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSnpSrvrSRFootprintTableGeneric.table);
		sId = p_sId;
		sessionId = p_sessionId;
		fp = p_fp;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
	}

	public Long getSId (SystemEnvironment env)
	throws SDMSException
	{
		return (sId);
	}

	public	void setSId (SystemEnvironment env, Long p_sId)
	throws SDMSException
	{
		if(sId.equals(p_sId)) return;
		SDMSnpSrvrSRFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(npSrvrSRFootprint) Change of system object not allowed")
				);
			}
			o = (SDMSnpSrvrSRFootprintGeneric) change(env);
			o.sId = p_sId;
			o.changerUId = env.cEnv.euid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 1);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getSessionId (SystemEnvironment env)
	throws SDMSException
	{
		return (sessionId);
	}

	public	void setSessionId (SystemEnvironment env, Integer p_sessionId)
	throws SDMSException
	{
		if(p_sessionId != null && p_sessionId.equals(sessionId)) return;
		if(p_sessionId == null && sessionId == null) return;
		SDMSnpSrvrSRFootprintGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(npSrvrSRFootprint) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSnpSrvrSRFootprintGeneric) change(env);
		o.sessionId = p_sessionId;
		o.changerUId = env.cEnv.euid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public HashMap getFp (SystemEnvironment env)
	throws SDMSException
	{
		return (fp);
	}

	public	void setFp (SystemEnvironment env, HashMap p_fp)
	throws SDMSException
	{
		if(fp.equals(p_fp)) return;
		SDMSnpSrvrSRFootprintGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(npSrvrSRFootprint) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSnpSrvrSRFootprintGeneric) change(env);
		o.fp = p_fp;
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
		SDMSnpSrvrSRFootprintGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(npSrvrSRFootprint) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSnpSrvrSRFootprintGeneric) change(env);
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
		SDMSnpSrvrSRFootprintGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(npSrvrSRFootprint) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSnpSrvrSRFootprintGeneric) change(env);
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
		SDMSnpSrvrSRFootprintGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSnpSrvrSRFootprintGeneric) change(env);
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
		SDMSnpSrvrSRFootprintGeneric o = this;
		if (o.versions.o_v == null || o.subTxId != env.tx.subTxId) o = (SDMSnpSrvrSRFootprintGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.euid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	protected SDMSProxy toProxy()
	{
		return new SDMSnpSrvrSRFootprint(this);
	}

	protected SDMSnpSrvrSRFootprintGeneric(Long p_id,
	                                       Long p_sId,
	                                       Integer p_sessionId,
	                                       HashMap p_fp,
	                                       Long p_creatorUId,
	                                       Long p_createTs,
	                                       Long p_changerUId,
	                                       Long p_changeTs,
	                                       long p_validFrom, long p_validTo)
	{
		id     = p_id;
		sId = p_sId;
		sessionId = p_sessionId;
		fp = p_fp;
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

	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{

	}

	protected void updateDBObject(SystemEnvironment env, SDMSObject old)
	throws SDMSException
	{

	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : npSrvrSRFootprint", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "sId : " + sId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "sessionId : " + sessionId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fp : " + fp, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "sId        : " + sId + "\n" +
		        indentString + "sessionId  : " + sessionId + "\n" +
		        indentString + "fp         : " + fp + "\n" +
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
