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

public class SDMSnpJobFootprintGeneric extends SDMSObject
	implements Cloneable
{

	public final static int nr_id = 1;
	public final static int nr_smeId = 2;
	public final static int nr_fpScope = 3;
	public final static int nr_fpFolder = 4;
	public final static int nr_fpLocal = 5;
	public final static int nr_creatorUId = 6;
	public final static int nr_createTs = 7;
	public final static int nr_changerUId = 8;
	public final static int nr_changeTs = 9;

	public static String tableName = SDMSnpJobFootprintTableGeneric.tableName;

	protected Long smeId;
	protected HashMap fpScope;
	protected HashMap fpFolder;
	protected HashMap fpLocal;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;
	public SDMSnpJobFootprintGeneric(
	        SystemEnvironment env,
	        Long p_smeId,
	        HashMap p_fpScope,
	        HashMap p_fpFolder,
	        HashMap p_fpLocal,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs
	)
	throws SDMSException
	{
		super(env, SDMSnpJobFootprintTableGeneric.table);
		smeId = p_smeId;
		fpScope = p_fpScope;
		fpFolder = p_fpFolder;
		fpLocal = p_fpLocal;
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

	public	SDMSnpJobFootprintGeneric setSmeId (SystemEnvironment env, Long p_smeId)
	throws SDMSException
	{
		if(smeId.equals(p_smeId)) return this;
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(npJobFootprint) Change of system object not allowed")
				);
			}
			o = (SDMSnpJobFootprintGeneric) change(env);
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

	public HashMap getFpScope (SystemEnvironment env)
	throws SDMSException
	{
		return (fpScope);
	}

	public	SDMSnpJobFootprintGeneric setFpScope (SystemEnvironment env, HashMap p_fpScope)
	throws SDMSException
	{
		if(fpScope.equals(p_fpScope)) return this;
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(npJobFootprint) Change of system object not allowed")
				);
			}
			o = (SDMSnpJobFootprintGeneric) change(env);
			o.fpScope = p_fpScope;
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

	public HashMap getFpFolder (SystemEnvironment env)
	throws SDMSException
	{
		return (fpFolder);
	}

	public	SDMSnpJobFootprintGeneric setFpFolder (SystemEnvironment env, HashMap p_fpFolder)
	throws SDMSException
	{
		if(fpFolder.equals(p_fpFolder)) return this;
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(npJobFootprint) Change of system object not allowed")
				);
			}
			o = (SDMSnpJobFootprintGeneric) change(env);
			o.fpFolder = p_fpFolder;
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

	public HashMap getFpLocal (SystemEnvironment env)
	throws SDMSException
	{
		return (fpLocal);
	}

	public	SDMSnpJobFootprintGeneric setFpLocal (SystemEnvironment env, HashMap p_fpLocal)
	throws SDMSException
	{
		if(fpLocal.equals(p_fpLocal)) return this;
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(npJobFootprint) Change of system object not allowed")
				);
			}
			o = (SDMSnpJobFootprintGeneric) change(env);
			o.fpLocal = p_fpLocal;
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

	SDMSnpJobFootprintGeneric setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		if(creatorUId.equals(p_creatorUId)) return this;
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(npJobFootprint) Change of system object not allowed")
				);
			}
			o = (SDMSnpJobFootprintGeneric) change(env);
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

	SDMSnpJobFootprintGeneric setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		if(createTs.equals(p_createTs)) return this;
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(npJobFootprint) Change of system object not allowed")
				);
			}
			o = (SDMSnpJobFootprintGeneric) change(env);
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

	public	SDMSnpJobFootprintGeneric setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSnpJobFootprintGeneric) change(env);
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

	SDMSnpJobFootprintGeneric setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		if(changeTs.equals(p_changeTs)) return this;
		SDMSnpJobFootprintGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSnpJobFootprintGeneric) change(env);
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
		return new SDMSnpJobFootprint(this);
	}

	protected SDMSnpJobFootprintGeneric(Long p_id,
	                                    Long p_smeId,
	                                    HashMap p_fpScope,
	                                    HashMap p_fpFolder,
	                                    HashMap p_fpLocal,
	                                    Long p_creatorUId,
	                                    Long p_createTs,
	                                    Long p_changerUId,
	                                    Long p_changeTs,
	                                    long p_validFrom, long p_validTo)
	{
		id     = p_id;
		smeId = p_smeId;
		fpScope = p_fpScope;
		fpFolder = p_fpFolder;
		fpLocal = p_fpLocal;
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
		SDMSThread.doTrace(null, "Type : npJobFootprint", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "smeId : " + smeId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fpScope : " + fpScope, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fpFolder : " + fpFolder, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "fpLocal : " + fpLocal, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "smeId      : " + smeId + "\n" +
		        indentString + "fpScope    : " + fpScope + "\n" +
		        indentString + "fpFolder   : " + fpFolder + "\n" +
		        indentString + "fpLocal    : " + fpLocal + "\n" +
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
