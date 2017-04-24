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

public class SDMSParameterDefinitionProxyGeneric extends SDMSProxy
{

	public static final int REFERENCE = 10;
	public static final int CHILDREFERENCE = 20;
	public static final int CONSTANT = 30;
	public static final int RESULT = 40;
	public static final int PARAMETER = 50;
	public static final int EXPRESSION = 60;
	public static final int IMPORT = 70;
	public static final int DYNAMIC = 80;
	public static final int DYNAMICVALUE = 81;
	public static final int LOCAL_CONSTANT = 90;
	public static final int RESOURCEREFERENCE = 91;
	public static final int NONE = 0;
	public static final int AVG = 61;
	public static final int COUNT = 62;
	public static final int MIN = 63;
	public static final int MAX = 64;
	public static final int SUM = 65;
	public final static long privilegeMask = SDMSPrivilege.EDIT|SDMSPrivilege.CREATE|SDMSPrivilege.VIEW|SDMSPrivilege.DROP;

	static final public int name_size = 64;
	static final public int defaultValue_size = 256;
	static final public int exportName_size = 64;

	private static SDMSTable masterTables[] = null;

	protected SDMSParameterDefinitionProxyGeneric(SDMSObject p_object)
	{
		super(p_object);
	}

	protected static SDMSParameterDefinition getProxy (SystemEnvironment sysEnv, SDMSObject p_object)
	{
		int i = SDMSParameterDefinitionTable.table.tableIndex;
		SDMSProxy p = SDMSRepository.getProxy(i);
		if (p == null)
			p = new SDMSParameterDefinition (p_object);
		else {
			p.initProxy(p_object);
		}
		sysEnv.tx.addUsedProxy(i, p);
		return (SDMSParameterDefinition)p;
	}

	public Long getSeId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getSeId (env));
	}

	public void setSeId (SystemEnvironment env, Long p_seId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setSeId (env, p_seId);
		return ;
	}
	public String getName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getName (env));
	}

	public void setName (SystemEnvironment env, String p_name)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setName (env, p_name);
		return ;
	}
	public static int getNameMaxLength ()
	{
		return (64);
	}
	public Integer getType (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getType (env));
	}

	public String getTypeAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSParameterDefinitionGeneric) object).getTypeAsString (env);
	}

	public void setType (SystemEnvironment env, Integer p_type)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setType (env, p_type);
		return ;
	}
	public Integer getAggFunction (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getAggFunction (env));
	}

	public String getAggFunctionAsString (SystemEnvironment env)
	throws SDMSException
	{
		checkRead (env);
		return ((SDMSParameterDefinitionGeneric) object).getAggFunctionAsString (env);
	}

	public void setAggFunction (SystemEnvironment env, Integer p_aggFunction)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setAggFunction (env, p_aggFunction);
		return ;
	}
	public String getDefaultValue (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getDefaultValue (env));
	}

	public void setDefaultValue (SystemEnvironment env, String p_defaultValue)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setDefaultValue (env, p_defaultValue);
		return ;
	}
	public static int getDefaultValueMaxLength ()
	{
		return (256);
	}
	public Boolean getIsLocal (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getIsLocal (env));
	}

	public void setIsLocal (SystemEnvironment env, Boolean p_isLocal)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setIsLocal (env, p_isLocal);
		return ;
	}
	public Long getLinkPdId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getLinkPdId (env));
	}

	public void setLinkPdId (SystemEnvironment env, Long p_linkPdId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setLinkPdId (env, p_linkPdId);
		return ;
	}
	public String getExportName (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getExportName (env));
	}

	public void setExportName (SystemEnvironment env, String p_exportName)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setExportName (env, p_exportName);
		return ;
	}
	public static int getExportNameMaxLength ()
	{
		return (64);
	}
	public Boolean getIsLong (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getIsLong (env));
	}

	public void setIsLong (SystemEnvironment env, Boolean p_isLong)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setIsLong (env, p_isLong);
		return ;
	}
	public Long getCreatorUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getCreatorUId (env));
	}

	private void setCreatorUId (SystemEnvironment env, Long p_creatorUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setCreatorUId (env, p_creatorUId);
		return ;
	}
	public Long getCreateTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getCreateTs (env));
	}

	private void setCreateTs (SystemEnvironment env, Long p_createTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setCreateTs (env, p_createTs);
		return ;
	}
	public Long getChangerUId (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getChangerUId (env));
	}

	public void setChangerUId (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setChangerUId (env, p_changerUId);
		return ;
	}
	protected SDMSParameterDefinition setChangerUIdNoCheck (SystemEnvironment env, Long p_changerUId)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setChangerUId (env, p_changerUId);
		return (SDMSParameterDefinition)this;
	}
	public Long getChangeTs (SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return (((SDMSParameterDefinitionGeneric)(object)).getChangeTs (env));
	}

	private void setChangeTs (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181241"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setChangeTs (env, p_changeTs);
		return ;
	}
	protected SDMSParameterDefinition setChangeTsNoCheck (SystemEnvironment env, Long p_changeTs)
	throws SDMSException
	{
		checkWrite(env);
		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).setChangeTs (env, p_changeTs);
		return (SDMSParameterDefinition)this;
	}
	public SDMSParameterDefinition set_SeIdName (SystemEnvironment env, Long p_seId, String p_name)
	throws SDMSException
	{
		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.EDIT))
			throw new AccessViolationException (accessViolationMessage(env, "01312181242"));

		touchMaster(env);
		((SDMSParameterDefinitionGeneric)(object)).set_SeIdName (env, p_seId, p_name);
		return (SDMSParameterDefinition)this;
	}

	public void delete (SystemEnvironment env)
	throws SDMSException
	{
		SDMSObjectCommentTable.dropComment (env, getId (env));
		touchMaster(env);
		super.delete(env);
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null) & p) == p;
	}

	public final boolean checkPrivileges(SystemEnvironment env, long p, long version)
	throws SDMSException
	{
		return (getPrivileges(env, p, true, null, version) & p) == p;
	}

	public long getPrivilegeMask()
	{
		return privilegeMask;
	}

	public final SDMSPrivilege getPrivileges(SystemEnvironment env)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, null));
	}

	public final SDMSPrivilege getPrivilegesForGroups(SystemEnvironment env, Vector checkGroups)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, checkGroups));
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException
	{
		Vector groups;
		if (checkGroups == null) groups = new Vector();
		else groups = checkGroups;

		long p = 0;
		p = checkPrivs;
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSeId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSFolderTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSeId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSScopeTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSeId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {
			SDMSProxy o = t.get(env, getSeId(env));
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		return p;
	}

	public final SDMSPrivilege getPrivileges(SystemEnvironment env, long version)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, null, version));
	}

	public final SDMSPrivilege getPrivilegesForGroups(SystemEnvironment env, Vector checkGroups, long version)
	throws SDMSException
	{
		return new SDMSPrivilege(env, getPrivileges(env, privilegeMask, false, checkGroups, version));
	}

	public long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups, long version)
	throws SDMSException
	{
		Vector groups;
		if (checkGroups == null) groups = new Vector();
		else groups = checkGroups;

		long p = 0;
		p = checkPrivs;
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {

			SDMSProxy o = t.get(env, getSeId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSFolderTable.tableName);
		try {

			SDMSProxy o = t.get(env, getSeId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSScopeTable.tableName);
		try {

			SDMSProxy o = t.get(env, getSeId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {

			SDMSProxy o = t.get(env, getSeId(env), version);
			long sp = o.getPrivileges(env, privilegeMask, false, checkGroups);
			if ((sp & SDMSPrivilege.EDIT) == SDMSPrivilege.EDIT) {
				sp |= SDMSPrivilege.CREATE | SDMSPrivilege.DROP | SDMSPrivilege.VIEW;
			}
			p = p & sp;
		} catch (NotFoundException nfe) {
		}
		return p;
	}

	public SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException
	{
		SDMSMessage m;
		m = new SDMSMessage(sysEnv, errno, "Insufficient privileges accessing ParameterDefinition $1", getId(sysEnv));
		return m;
	}

	void touchMaster(SystemEnvironment env)
	throws SDMSException
	{
		SDMSTable t;

		t = SystemEnvironment.repository.getTable(env, SDMSSchedulingEntityTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSFolderTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSScopeTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
		t = SystemEnvironment.repository.getTable(env, SDMSNamedResourceTable.tableName);
		try {
			SDMSProxy p = t.get(env, getSeId(env));
			p.touch(env);
		} catch (NotFoundException nfe) {
		}
	}

	protected void touch(SystemEnvironment env)
	throws SDMSException
	{
		setChangerUIdNoCheck (env, env.cEnv.euid());
		setChangeTsNoCheck (env, env.txTime());
	}

	public void print()
	{
		((SDMSParameterDefinitionGeneric) object).print();
	}
}
