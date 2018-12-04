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

public class SDMSScopeGeneric extends SDMSObject
	implements Cloneable
{

	public static final int SCOPE = 1;
	public static final int SERVER = 2;
	public static final int MD5 = SDMSUser.MD5;
	public static final int SHA256 = SDMSUser.SHA256;
	public static final int NOMINAL = 1;
	public static final int NONFATAL = 2;
	public static final int FATAL = 3;

	public final static int nr_id = 1;
	public final static int nr_name = 2;
	public final static int nr_ownerId = 3;
	public final static int nr_parentId = 4;
	public final static int nr_type = 5;
	public final static int nr_isTerminate = 6;
	public final static int nr_hasAlteredConfig = 7;
	public final static int nr_isSuspended = 8;
	public final static int nr_isEnabled = 9;
	public final static int nr_isRegistered = 10;
	public final static int nr_state = 11;
	public final static int nr_passwd = 12;
	public final static int nr_salt = 13;
	public final static int nr_method = 14;
	public final static int nr_pid = 15;
	public final static int nr_node = 16;
	public final static int nr_errmsg = 17;
	public final static int nr_lastActive = 18;
	public final static int nr_creatorUId = 19;
	public final static int nr_createTs = 20;
	public final static int nr_changerUId = 21;
	public final static int nr_changeTs = 22;
	public final static int nr_inheritPrivs = 23;

	public static String tableName = SDMSScopeTableGeneric.tableName;

	protected String name;
	protected Long ownerId;
	protected Long parentId;
	protected Integer type;
	protected Boolean isTerminate;
	protected Boolean hasAlteredConfig;
	protected Boolean isSuspended;
	protected Boolean isEnabled;
	protected Boolean isRegistered;
	protected Integer state;
	protected String passwd;
	protected String salt;
	protected Integer method;
	protected String pid;
	protected String node;
	protected String errmsg;
	protected Long lastActive;
	protected Long creatorUId;
	protected Long createTs;
	protected Long changerUId;
	protected Long changeTs;
	protected Long inheritPrivs;

	private static PreparedStatement pUpdate[] = new PreparedStatement[128];
	private static PreparedStatement pDelete[] = new PreparedStatement[128];
	private static PreparedStatement pInsert[] = new PreparedStatement[128];

	public SDMSScopeGeneric(
	        SystemEnvironment env,
	        String p_name,
	        Long p_ownerId,
	        Long p_parentId,
	        Integer p_type,
	        Boolean p_isTerminate,
	        Boolean p_hasAlteredConfig,
	        Boolean p_isSuspended,
	        Boolean p_isEnabled,
	        Boolean p_isRegistered,
	        Integer p_state,
	        String p_passwd,
	        String p_salt,
	        Integer p_method,
	        String p_pid,
	        String p_node,
	        String p_errmsg,
	        Long p_lastActive,
	        Long p_creatorUId,
	        Long p_createTs,
	        Long p_changerUId,
	        Long p_changeTs,
	        Long p_inheritPrivs
	)
	throws SDMSException
	{
		super(env, SDMSScopeTableGeneric.table);
		if (p_name != null && p_name.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Scope) Length of $1 exceeds maximum length $2", "name", "64")
			);
		}
		name = p_name;
		ownerId = p_ownerId;
		parentId = p_parentId;
		type = p_type;
		isTerminate = p_isTerminate;
		hasAlteredConfig = p_hasAlteredConfig;
		isSuspended = p_isSuspended;
		isEnabled = p_isEnabled;
		isRegistered = p_isRegistered;
		state = p_state;
		if (p_passwd != null && p_passwd.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Scope) Length of $1 exceeds maximum length $2", "passwd", "64")
			);
		}
		passwd = p_passwd;
		if (p_salt != null && p_salt.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Scope) Length of $1 exceeds maximum length $2", "salt", "64")
			);
		}
		salt = p_salt;
		method = p_method;
		if (p_pid != null && p_pid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Scope) Length of $1 exceeds maximum length $2", "pid", "32")
			);
		}
		pid = p_pid;
		if (p_node != null && p_node.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141528",
			                        "(Scope) Length of $1 exceeds maximum length $2", "node", "64")
			);
		}
		node = p_node;
		if (p_errmsg != null && p_errmsg.length() > 256) {
			p_errmsg = p_errmsg.substring(0,256);
		}
		errmsg = p_errmsg;
		lastActive = p_lastActive;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		inheritPrivs = p_inheritPrivs;
	}

	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		return (name);
	}

	public	void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		if(name.equals(p_name)) return;
		SDMSScopeGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
				);
			}
			o = (SDMSScopeGeneric) change(env);
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Scope) Length of $1 exceeds maximum length $2", "name", "64")
				);
			}
			o.name = p_name;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 16);
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
		SDMSScopeGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			o = (SDMSScopeGeneric) change(env);
			o.ownerId = p_ownerId;
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

	public Long getParentId (SystemEnvironment env)
	throws SDMSException
	{
		return (parentId);
	}

	public	void setParentId (SystemEnvironment env, Long p_parentId)
	throws SDMSException
	{
		if(p_parentId != null && p_parentId.equals(parentId)) return;
		if(p_parentId == null && parentId == null) return;
		SDMSScopeGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
				);
			}
			o = (SDMSScopeGeneric) change(env);
			o.parentId = p_parentId;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 18);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		return (type);
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getType (env);
		switch (v.intValue()) {
			case SDMSScope.SCOPE:
				return "SCOPE";
			case SDMSScope.SERVER:
				return "SERVER";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Scope.type: $1",
		                          getType (env)));
	}

	public	void setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		if(type.equals(p_type)) return;
		SDMSScopeGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
				);
			}
			o = (SDMSScopeGeneric) change(env);
			o.type = p_type;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 4);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public Boolean getIsTerminate (SystemEnvironment env)
	throws SDMSException
	{
		return (isTerminate);
	}

	public	void setIsTerminate (SystemEnvironment env, Boolean p_isTerminate)
	throws SDMSException
	{
		if(p_isTerminate != null && p_isTerminate.equals(isTerminate)) return;
		if(p_isTerminate == null && isTerminate == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.isTerminate = p_isTerminate;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getHasAlteredConfig (SystemEnvironment env)
	throws SDMSException
	{
		return (hasAlteredConfig);
	}

	public	void setHasAlteredConfig (SystemEnvironment env, Boolean p_hasAlteredConfig)
	throws SDMSException
	{
		if(p_hasAlteredConfig != null && p_hasAlteredConfig.equals(hasAlteredConfig)) return;
		if(p_hasAlteredConfig == null && hasAlteredConfig == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.hasAlteredConfig = p_hasAlteredConfig;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsSuspended (SystemEnvironment env)
	throws SDMSException
	{
		return (isSuspended);
	}

	public	void setIsSuspended (SystemEnvironment env, Boolean p_isSuspended)
	throws SDMSException
	{
		if(p_isSuspended != null && p_isSuspended.equals(isSuspended)) return;
		if(p_isSuspended == null && isSuspended == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.isSuspended = p_isSuspended;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsEnabled (SystemEnvironment env)
	throws SDMSException
	{
		return (isEnabled);
	}

	public	void setIsEnabled (SystemEnvironment env, Boolean p_isEnabled)
	throws SDMSException
	{
		if(p_isEnabled != null && p_isEnabled.equals(isEnabled)) return;
		if(p_isEnabled == null && isEnabled == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.isEnabled = p_isEnabled;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Boolean getIsRegistered (SystemEnvironment env)
	throws SDMSException
	{
		return (isRegistered);
	}

	public	void setIsRegistered (SystemEnvironment env, Boolean p_isRegistered)
	throws SDMSException
	{
		if(p_isRegistered != null && p_isRegistered.equals(isRegistered)) return;
		if(p_isRegistered == null && isRegistered == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.isRegistered = p_isRegistered;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getState (SystemEnvironment env)
	throws SDMSException
	{
		return (state);
	}

	public String getStateAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getState (env);
		if (v == null)
			return null;
		switch (v.intValue()) {
			case SDMSScope.NOMINAL:
				return "NOMINAL";
			case SDMSScope.NONFATAL:
				return "NONFATAL";
			case SDMSScope.FATAL:
				return "FATAL";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Scope.state: $1",
		                          getState (env)));
	}

	public	void setState (SystemEnvironment env, Integer p_state)
	throws SDMSException
	{
		if(p_state != null && p_state.equals(state)) return;
		if(p_state == null && state == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.state = p_state;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getPasswd (SystemEnvironment env)
	throws SDMSException
	{
		return (passwd);
	}

	public	void setPasswd (SystemEnvironment env, String p_passwd)
	throws SDMSException
	{
		if(p_passwd != null && p_passwd.equals(passwd)) return;
		if(p_passwd == null && passwd == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		if (p_passwd != null && p_passwd.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(Scope) Length of $1 exceeds maximum length $2", "passwd", "64")
			);
		}
		o.passwd = p_passwd;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getSalt (SystemEnvironment env)
	throws SDMSException
	{
		return (salt);
	}

	public	void setSalt (SystemEnvironment env, String p_salt)
	throws SDMSException
	{
		if(p_salt != null && p_salt.equals(salt)) return;
		if(p_salt == null && salt == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		if (p_salt != null && p_salt.length() > 64) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(Scope) Length of $1 exceeds maximum length $2", "salt", "64")
			);
		}
		o.salt = p_salt;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Integer getMethod (SystemEnvironment env)
	throws SDMSException
	{
		return (method);
	}

	public String getMethodAsString (SystemEnvironment env)
	throws SDMSException
	{
		final Integer v = getMethod (env);
		switch (v.intValue()) {
			case SDMSScope.MD5:
				return "MD5";
			case SDMSScope.SHA256:
				return "SHA256";
		}
		throw new FatalException (new SDMSMessage (env,
		                          "01205252242",
		                          "Unknown Scope.method: $1",
		                          getMethod (env)));
	}

	public	void setMethod (SystemEnvironment env, Integer p_method)
	throws SDMSException
	{
		if(method.equals(p_method)) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.method = p_method;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getPid (SystemEnvironment env)
	throws SDMSException
	{
		return (pid);
	}

	public	void setPid (SystemEnvironment env, String p_pid)
	throws SDMSException
	{
		if(p_pid != null && p_pid.equals(pid)) return;
		if(p_pid == null && pid == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		if (p_pid != null && p_pid.length() > 32) {
			throw new CommonErrorException (
			        new SDMSMessage(env, "01112141510",
			                        "(Scope) Length of $1 exceeds maximum length $2", "pid", "32")
			);
		}
		o.pid = p_pid;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public String getNode (SystemEnvironment env)
	throws SDMSException
	{
		return (node);
	}

	public	void setNode (SystemEnvironment env, String p_node)
	throws SDMSException
	{
		if(p_node != null && p_node.equals(node)) return;
		if(p_node == null && node == null) return;
		SDMSScopeGeneric o;
		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
				);
			}
			o = (SDMSScopeGeneric) change(env);
			if (p_node != null && p_node.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01112141510",
				                        "(Scope) Length of $1 exceeds maximum length $2", "node", "64")
				);
			}
			o.node = p_node;
			o.changerUId = env.cEnv.uid();
			o.changeTs = env.txTime();
			o.versions.table.index(env, o, 8);
			env.tx.commitSubTransaction(env);
		} catch (SDMSException e) {
			env.tx.rollbackSubTransaction(env);
			throw e;
		}
		return;
	}

	public String getErrmsg (SystemEnvironment env)
	throws SDMSException
	{
		return (errmsg);
	}

	public	void setErrmsg (SystemEnvironment env, String p_errmsg)
	throws SDMSException
	{
		if(p_errmsg != null && p_errmsg.equals(errmsg)) return;
		if(p_errmsg == null && errmsg == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		if (p_errmsg != null && p_errmsg.length() > 256) {
			p_errmsg = p_errmsg.substring(0,256);
		}
		o.errmsg = p_errmsg;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getLastActive (SystemEnvironment env)
	throws SDMSException
	{
		return (lastActive);
	}

	public	void setLastActive (SystemEnvironment env, Long p_lastActive)
	throws SDMSException
	{
		if(p_lastActive != null && p_lastActive.equals(lastActive)) return;
		if(p_lastActive == null && lastActive == null) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.lastActive = p_lastActive;
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
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
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
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
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
		SDMSScopeGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
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
		SDMSScopeGeneric o = this;
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.changeTs = p_changeTs;
		o.changerUId = env.cEnv.uid();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public Long getInheritPrivs (SystemEnvironment env)
	throws SDMSException
	{
		return (inheritPrivs);
	}

	public	void setInheritPrivs (SystemEnvironment env, Long p_inheritPrivs)
	throws SDMSException
	{
		if(inheritPrivs.equals(p_inheritPrivs)) return;
		SDMSScopeGeneric o = this;
		if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
			throw new CommonErrorException(
			        new SDMSMessage (env, "02112141636", "(Scope) Change of system object not allowed")
			);
		}
		if (o.versions.o_v == null || o.versions.o_v.size() == 0 || o.subTxId != env.tx.subTxId) o = (SDMSScopeGeneric) change(env);
		o.inheritPrivs = p_inheritPrivs;
		o.changerUId = env.cEnv.uid();
		o.changeTs = env.txTime();
		if (o != this) o.versions.table.index(env, o, 0);
		return;
	}

	public SDMSScopeGeneric set_ParentIdName (SystemEnvironment env, Long p_parentId, String p_name)
	throws SDMSException
	{
		SDMSScopeGeneric o;

		env.tx.beginSubTransaction(env);
		try {
			if (versions.id.longValue() < SystemEnvironment.SYSTEM_OBJECTS_BOUNDARY) {
				throw new CommonErrorException(
				        new SDMSMessage (env, "02112141637", "(Scope) Change of system object not allowed")
				);
			}
			o = (SDMSScopeGeneric) change(env);
			o.parentId = p_parentId;
			if (p_name != null && p_name.length() > 64) {
				throw new CommonErrorException (
				        new SDMSMessage(env, "01201290026",
				                        "(Scope) Length of $1 exceeds maximum length $2", "inheritPrivs", "64")
				);
			}
			o.name = p_name;
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
		return SDMSScope.getProxy(sysEnv, this);
	}

	protected SDMSScopeGeneric(Long p_id,
	                           String p_name,
	                           Long p_ownerId,
	                           Long p_parentId,
	                           Integer p_type,
	                           Boolean p_isTerminate,
	                           Boolean p_hasAlteredConfig,
	                           Boolean p_isSuspended,
	                           Boolean p_isEnabled,
	                           Boolean p_isRegistered,
	                           Integer p_state,
	                           String p_passwd,
	                           String p_salt,
	                           Integer p_method,
	                           String p_pid,
	                           String p_node,
	                           String p_errmsg,
	                           Long p_lastActive,
	                           Long p_creatorUId,
	                           Long p_createTs,
	                           Long p_changerUId,
	                           Long p_changeTs,
	                           Long p_inheritPrivs,
	                           long p_validFrom, long p_validTo)
	{
		id     = p_id;
		name = p_name;
		ownerId = p_ownerId;
		parentId = p_parentId;
		type = p_type;
		isTerminate = p_isTerminate;
		hasAlteredConfig = p_hasAlteredConfig;
		isSuspended = p_isSuspended;
		isEnabled = p_isEnabled;
		isRegistered = p_isRegistered;
		state = p_state;
		passwd = p_passwd;
		salt = p_salt;
		method = p_method;
		pid = p_pid;
		node = p_node;
		errmsg = p_errmsg;
		lastActive = p_lastActive;
		creatorUId = p_creatorUId;
		createTs = p_createTs;
		changerUId = p_changerUId;
		changeTs = p_changeTs;
		inheritPrivs = p_inheritPrivs;
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
				        "INSERT INTO SCOPE (" +
				        "ID" +
				        ", " + squote + "NAME" + equote +
				        ", " + squote + "OWNER_ID" + equote +
				        ", " + squote + "PARENT_ID" + equote +
				        ", " + squote + "TYPE" + equote +
				        ", " + squote + "IS_TERMINATE" + equote +
				        ", " + squote + "HAS_ALTEREDCONFIG" + equote +
				        ", " + squote + "IS_SUSPENDED" + equote +
				        ", " + squote + "IS_ENABLED" + equote +
				        ", " + squote + "IS_REGISTERED" + equote +
				        ", " + squote + "STATE" + equote +
				        ", " + squote + "PASSWD" + equote +
				        ", " + squote + "SALT" + equote +
				        ", " + squote + "METHOD" + equote +
				        ", " + squote + "PID" + equote +
				        ", " + squote + "NODE" + equote +
				        ", " + squote + "ERRMSG" + equote +
				        ", " + squote + "LAST_ACTIVE" + equote +
				        ", " + squote + "CREATOR_U_ID" + equote +
				        ", " + squote + "CREATE_TS" + equote +
				        ", " + squote + "CHANGER_U_ID" + equote +
				        ", " + squote + "CHANGE_TS" + equote +
				        ", " + squote + "INHERIT_PRIVS" + equote +
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
				        ")";
				pInsert[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110181952", "Scope: $1\n$2", stmt, sqle.toString()));
			}
		}
		myInsert = pInsert[env.dbConnectionNr];
		try {
			myInsert.clearParameters();
			myInsert.setLong(1, id.longValue());
			myInsert.setString(2, name);
			myInsert.setLong (3, ownerId.longValue());
			if (parentId == null)
				myInsert.setNull(4, Types.INTEGER);
			else
				myInsert.setLong (4, parentId.longValue());
			myInsert.setInt(5, type.intValue());
			if (isTerminate == null)
				myInsert.setNull(6, Types.INTEGER);
			else
				myInsert.setInt (6, isTerminate.booleanValue() ? 1 : 0);
			if (hasAlteredConfig == null)
				myInsert.setNull(7, Types.INTEGER);
			else
				myInsert.setInt (7, hasAlteredConfig.booleanValue() ? 1 : 0);
			if (isSuspended == null)
				myInsert.setNull(8, Types.INTEGER);
			else
				myInsert.setInt (8, isSuspended.booleanValue() ? 1 : 0);
			if (isEnabled == null)
				myInsert.setNull(9, Types.INTEGER);
			else
				myInsert.setInt (9, isEnabled.booleanValue() ? 1 : 0);
			if (isRegistered == null)
				myInsert.setNull(10, Types.INTEGER);
			else
				myInsert.setInt (10, isRegistered.booleanValue() ? 1 : 0);
			if (state == null)
				myInsert.setNull(11, Types.INTEGER);
			else
				myInsert.setInt(11, state.intValue());
			if (passwd == null)
				myInsert.setNull(12, Types.VARCHAR);
			else
				myInsert.setString(12, passwd);
			if (salt == null)
				myInsert.setNull(13, Types.VARCHAR);
			else
				myInsert.setString(13, salt);
			myInsert.setInt(14, method.intValue());
			if (pid == null)
				myInsert.setNull(15, Types.VARCHAR);
			else
				myInsert.setString(15, pid);
			if (node == null)
				myInsert.setNull(16, Types.VARCHAR);
			else
				myInsert.setString(16, node);
			if (errmsg == null)
				myInsert.setNull(17, Types.VARCHAR);
			else
				myInsert.setString(17, errmsg);
			if (lastActive == null)
				myInsert.setNull(18, Types.INTEGER);
			else
				myInsert.setLong (18, lastActive.longValue());
			myInsert.setLong (19, creatorUId.longValue());
			myInsert.setLong (20, createTs.longValue());
			myInsert.setLong (21, changerUId.longValue());
			myInsert.setLong (22, changeTs.longValue());
			myInsert.setLong (23, inheritPrivs.longValue());
			myInsert.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110181954", "Scope: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	protected void deleteDBObject(SystemEnvironment env)
	throws SDMSException
	{
		String stmt = "";
		PreparedStatement myDelete;
		if(pDelete[env.dbConnectionNr] == null) {
			try {
				stmt =
				        "DELETE FROM SCOPE WHERE ID = ?";
				pDelete[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182001", "Scope: $1\n$2", stmt, sqle.toString()));
			}
		}
		myDelete = pDelete[env.dbConnectionNr];
		try {
			myDelete.clearParameters();
			myDelete.setLong(1, id.longValue());
			myDelete.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182002", "Scope: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
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
				        "UPDATE SCOPE SET " +
				        "" + squote + "NAME" + equote + " = ? " +
				        ", " + squote + "OWNER_ID" + equote + " = ? " +
				        ", " + squote + "PARENT_ID" + equote + " = ? " +
				        ", " + squote + "TYPE" + equote + " = ? " +
				        ", " + squote + "IS_TERMINATE" + equote + " = ? " +
				        ", " + squote + "HAS_ALTEREDCONFIG" + equote + " = ? " +
				        ", " + squote + "IS_SUSPENDED" + equote + " = ? " +
				        ", " + squote + "IS_ENABLED" + equote + " = ? " +
				        ", " + squote + "IS_REGISTERED" + equote + " = ? " +
				        ", " + squote + "STATE" + equote + " = ? " +
				        ", " + squote + "PASSWD" + equote + " = ? " +
				        ", " + squote + "SALT" + equote + " = ? " +
				        ", " + squote + "METHOD" + equote + " = ? " +
				        ", " + squote + "PID" + equote + " = ? " +
				        ", " + squote + "NODE" + equote + " = ? " +
				        ", " + squote + "ERRMSG" + equote + " = ? " +
				        ", " + squote + "LAST_ACTIVE" + equote + " = ? " +
				        ", " + squote + "CREATOR_U_ID" + equote + " = ? " +
				        ", " + squote + "CREATE_TS" + equote + " = ? " +
				        ", " + squote + "CHANGER_U_ID" + equote + " = ? " +
				        ", " + squote + "CHANGE_TS" + equote + " = ? " +
				        ", " + squote + "INHERIT_PRIVS" + equote + " = ? " +
				        "WHERE ID = ?";
				pUpdate[env.dbConnectionNr] = env.dbConnection.prepareStatement(stmt);
			} catch(SQLException sqle) {
				throw new FatalException(new SDMSMessage(env, "01110182005", "Scope: $1\n$2", stmt, sqle.toString()));
			}
		}
		myUpdate = pUpdate[env.dbConnectionNr];
		try {
			myUpdate.clearParameters();
			myUpdate.setString(1, name);
			myUpdate.setLong (2, ownerId.longValue());
			if (parentId == null)
				myUpdate.setNull(3, Types.INTEGER);
			else
				myUpdate.setLong (3, parentId.longValue());
			myUpdate.setInt(4, type.intValue());
			if (isTerminate == null)
				myUpdate.setNull(5, Types.INTEGER);
			else
				myUpdate.setInt (5, isTerminate.booleanValue() ? 1 : 0);
			if (hasAlteredConfig == null)
				myUpdate.setNull(6, Types.INTEGER);
			else
				myUpdate.setInt (6, hasAlteredConfig.booleanValue() ? 1 : 0);
			if (isSuspended == null)
				myUpdate.setNull(7, Types.INTEGER);
			else
				myUpdate.setInt (7, isSuspended.booleanValue() ? 1 : 0);
			if (isEnabled == null)
				myUpdate.setNull(8, Types.INTEGER);
			else
				myUpdate.setInt (8, isEnabled.booleanValue() ? 1 : 0);
			if (isRegistered == null)
				myUpdate.setNull(9, Types.INTEGER);
			else
				myUpdate.setInt (9, isRegistered.booleanValue() ? 1 : 0);
			if (state == null)
				myUpdate.setNull(10, Types.INTEGER);
			else
				myUpdate.setInt(10, state.intValue());
			if (passwd == null)
				myUpdate.setNull(11, Types.VARCHAR);
			else
				myUpdate.setString(11, passwd);
			if (salt == null)
				myUpdate.setNull(12, Types.VARCHAR);
			else
				myUpdate.setString(12, salt);
			myUpdate.setInt(13, method.intValue());
			if (pid == null)
				myUpdate.setNull(14, Types.VARCHAR);
			else
				myUpdate.setString(14, pid);
			if (node == null)
				myUpdate.setNull(15, Types.VARCHAR);
			else
				myUpdate.setString(15, node);
			if (errmsg == null)
				myUpdate.setNull(16, Types.VARCHAR);
			else
				myUpdate.setString(16, errmsg);
			if (lastActive == null)
				myUpdate.setNull(17, Types.INTEGER);
			else
				myUpdate.setLong (17, lastActive.longValue());
			myUpdate.setLong (18, creatorUId.longValue());
			myUpdate.setLong (19, createTs.longValue());
			myUpdate.setLong (20, changerUId.longValue());
			myUpdate.setLong (21, changeTs.longValue());
			myUpdate.setLong (22, inheritPrivs.longValue());
			myUpdate.setLong(23, id.longValue());
			myUpdate.executeUpdate();
		} catch(SQLException sqle) {
			throw new SDMSSQLException(new SDMSMessage(env, "01110182006", "Scope: $1 $2", new Integer(sqle.getErrorCode()), sqle.getMessage()));
		}
	}

	static public boolean checkType(Integer p)
	{
		switch (p.intValue()) {
			case SDMSScope.SCOPE:
			case SDMSScope.SERVER:
				return true;
		}
		return false;
	}
	static public boolean checkState(Integer p)
	{
		if(p == null) return true;
		switch (p.intValue()) {
			case SDMSScope.NOMINAL:
			case SDMSScope.NONFATAL:
			case SDMSScope.FATAL:
				return true;
		}
		return false;
	}
	static public boolean checkMethod(Integer p)
	{
		switch (p.intValue()) {
			case SDMSScope.MD5:
			case SDMSScope.SHA256:
				return true;
		}
		return false;
	}

	public void print()
	{
		SDMSThread.doTrace(null, "Type : Scope", SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "id : " + id, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "name : " + name, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "ownerId : " + ownerId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "parentId : " + parentId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "type : " + type, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isTerminate : " + isTerminate, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "hasAlteredConfig : " + hasAlteredConfig, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isSuspended : " + isSuspended, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isEnabled : " + isEnabled, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "isRegistered : " + isRegistered, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "state : " + state, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "passwd : " + passwd, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "salt : " + salt, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "method : " + method, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "pid : " + pid, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "node : " + node, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "errmsg : " + errmsg, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "lastActive : " + lastActive, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "creatorUId : " + creatorUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "createTs : " + createTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changerUId : " + changerUId, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "changeTs : " + changeTs, SDMSThread.SEVERITY_MESSAGE);
		SDMSThread.doTrace(null, "inheritPrivs : " + inheritPrivs, SDMSThread.SEVERITY_MESSAGE);
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
		        indentString + "name             : " + name + "\n" +
		        indentString + "ownerId          : " + ownerId + "\n" +
		        indentString + "parentId         : " + parentId + "\n" +
		        indentString + "type             : " + type + "\n" +
		        indentString + "isTerminate      : " + isTerminate + "\n" +
		        indentString + "hasAlteredConfig : " + hasAlteredConfig + "\n" +
		        indentString + "isSuspended      : " + isSuspended + "\n" +
		        indentString + "isEnabled        : " + isEnabled + "\n" +
		        indentString + "isRegistered     : " + isRegistered + "\n" +
		        indentString + "state            : " + state + "\n" +
		        indentString + "passwd           : " + passwd + "\n" +
		        indentString + "salt             : " + salt + "\n" +
		        indentString + "method           : " + method + "\n" +
		        indentString + "pid              : " + pid + "\n" +
		        indentString + "node             : " + node + "\n" +
		        indentString + "errmsg           : " + errmsg + "\n" +
		        indentString + "lastActive       : " + lastActive + "\n" +
		        indentString + "creatorUId       : " + creatorUId + "\n" +
		        indentString + "createTs         : " + createTs + "\n" +
		        indentString + "changerUId       : " + changerUId + "\n" +
		        indentString + "changeTs         : " + changeTs + "\n" +
		        indentString + "inheritPrivs     : " + inheritPrivs + "\n" +
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
