/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

import java.lang.*;
import java.util.*;
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public abstract class SDMSProxy implements Comparable
{
	public static final String __version = "@(#) $Id: SDMSProxy.java,v 2.11.2.2 2013/03/16 11:47:20 dieter Exp $";
	public static final Long ZERO = new Long(0);

	protected SDMSObject  object;

	protected boolean     lockedExclusive;

	protected boolean     current;

	protected SDMSProxy(SDMSObject p_object)
	{
		object = p_object;
		current = false;
		lockedExclusive = false;
	}

	protected void checkRead (SystemEnvironment env)
	throws SDMSException
	{
		if (current) {

			if (!object.isCurrent) {
				object = object.versions.get (env);
			}

			if (object.isDeleted) {
				throw new FatalException (new SDMSMessage (env, "02110292004",
				                          "Accessing a previously deleted object"));
			}
		}
	}

	protected void checkWrite (SystemEnvironment env)
	throws SDMSException
	{
		if(env.tx.mode == SDMSTransaction.READONLY) {
			throw new FatalException(new SDMSMessage(env,
			                         "03110182335", "Illegal write access in Readonly Transaction"));
		}
		if (!current) {
			throw new FatalException (new SDMSMessage (env, "02110292014",
			                          "Trying to change object via readonly object reference"));
		}

		if (!lockedExclusive) {
			object.versions.lockExclusive(env);
			lockedExclusive = true;
		}

		checkRead(env);
	}

	public void delete(SystemEnvironment env)
	throws SDMSException
	{
		objectDelete(env, false);
	}

	public void memDelete(SystemEnvironment env)
	throws SDMSException
	{
		objectDelete(env, true);
	}

	private void objectDelete(SystemEnvironment env, boolean memOnly)
	throws SDMSException
	{

		if (!lockedExclusive) {
			object.versions.lockExclusive(env);
			lockedExclusive = true;
		}

		checkRead(env);
		if(!checkPrivileges(env, SDMSPrivilege.DROP)) {
			throw new AccessViolationException(accessViolationMessage(env, "03312191407"));
		}
		if (memOnly)
			object.memDelete(env);
		else
			object.delete (env);
	}

	public String toString()
	{
		return "SDMSProxy (object = " + object.toString() +
		       ",current = " + current +
		       ", lockedExclusive = " + lockedExclusive + ")";
	}

	public long getValidFrom(SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return object.getValidFrom(env);
	}

	public long getValidTo(SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return object.getValidTo(env);
	}

	public Long getId(SystemEnvironment env)
	throws SDMSException
	{
		checkRead(env);
		return object.getId(env);
	}

	protected abstract void touch(SystemEnvironment env)
	throws SDMSException;

	synchronized void release(SystemEnvironment env)
	throws SDMSException
	{
		object.memDelete(env);
	}

	public Vector getContent(SystemEnvironment env)
	throws SDMSException
	{

		return null;
	}

	public abstract long getPrivilegeMask();

	public abstract boolean checkPrivileges(SystemEnvironment env, long p)
	throws SDMSException;

	public abstract SDMSPrivilege getPrivileges(SystemEnvironment env)
	throws SDMSException;

	public abstract SDMSPrivilege getPrivilegesForGroups(SystemEnvironment env, Vector groups)
	throws SDMSException;

	public abstract long getPrivileges(SystemEnvironment env, long checkPrivs, boolean fastFail, Vector checkGroups)
	throws SDMSException;

	public abstract SDMSMessage accessViolationMessage(SystemEnvironment sysEnv, String errno)
	throws SDMSException;

	public void dumpVersions()
	{
		object.dumpVersions();
	}

	public String getURL(SystemEnvironment env)
	throws SDMSException
	{
		return "getURL is not supported for this object";
	}

	public String getURLName(SystemEnvironment env)
	throws SDMSException
	{
		return "getURLName is not supported for this object";
	}

	public String getSubtypeName(SystemEnvironment env)
	throws SDMSException
	{
		return "";
	}

	public int compareTo(Object o)
	{
		SDMSProxy p = (SDMSProxy) o;
		return object.compareTo(p.object);
	}

	public Long getInheritPrivs(SystemEnvironment env)
	throws SDMSException
	{
		return ZERO;
	}

	public long addImplicitPrivs(long priv)
	{
		if (priv != 0)
			priv = priv | SDMSPrivilege.VIEW;
		if ((priv & SDMSPrivilege.OPERATE) != 0)
			priv = priv | SDMSPrivilege.MONITOR;
		return priv;
	}

	public Long getParentId(SystemEnvironment env)
	throws SDMSException
	{
		return null;
	}

	public SDMSProxy getParent(SystemEnvironment env)
	throws SDMSException
	{
		return null;
	}

	public PathVector pathVector(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return null;
	}

	public Long getOwnerId(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return null;
	}

}

