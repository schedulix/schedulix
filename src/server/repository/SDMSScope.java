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
import java.lang.*;
import java.util.*;
import java.sql.*;
import java.net.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.jobserver.Config;

public class SDMSScope extends SDMSScopeProxyGeneric
	implements SDMSOwnedObject
{

	public final static String __version = "@(#) $Id: SDMSScope.java,v 2.27.2.4 2013/03/19 17:16:52 ronald Exp $";
	private final static VariableResolver SVR = new ScopeVariableResolver();

	private final static Long lzero = new Long(0);
	private final static Integer zero = new Integer(0);
	private final static Float fzero = new Float(0);

	protected SDMSScope(SDMSObject p_object)
	{
		super(p_object);
	}

	public SDMSScope copy(SystemEnvironment sysEnv, Long targetScopeId, String name)
		throws SDMSException
	{
		final HashMap rMap = new HashMap();
		final HashMap pMap = new HashMap();
		final HashMap prMap = new HashMap();

		final SDMSScope s = internalCopy(sysEnv, targetScopeId, name, rMap, pMap);
		return s;
	}

	private SDMSScope internalCopy(SystemEnvironment sysEnv, Long targetScopeId, String name, HashMap rMap, HashMap pMap)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		SDMSUser u = SDMSUserTable.getObject(sysEnv, sysEnv.cEnv.uid());
		Long defaultGId = u.getDefaultGId(sysEnv);

		if(!checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(accessViolationMessage(sysEnv, "03402291216"));

		Integer type = getType(sysEnv);
		SDMSScope f;
		if (type.equals(new Integer(SDMSScope.SCOPE))) {
			f = SDMSScopeTable.table.create(sysEnv,
							name,
							defaultGId,
							targetScopeId,
							getType(sysEnv),
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							new Integer(0),
							null,
							null,
							null,
							null,
							getInheritPrivs(sysEnv)
			);
		} else {
			f = SDMSScopeTable.table.create(sysEnv,
							name,
							defaultGId,
							targetScopeId,
							getType(sysEnv),
							Boolean.FALSE,
							Boolean.FALSE,
							Boolean.FALSE,
							getIsEnabled(sysEnv),
							Boolean.FALSE,
							new Integer(SDMSScope.NOMINAL),
							getPasswd(sysEnv),
							getSalt(sysEnv),
							getMethod(sysEnv),
							null,
							getNode(sysEnv),
							null,
							null,
							getInheritPrivs(sysEnv)
			);
		}

		Long newId = f.getId(sysEnv);
		Vector v_ss = SDMSScopeTable.idx_parentId.getVector(sysEnv, id);
		Iterator i_ss = v_ss.iterator();
		while (i_ss.hasNext()) {
			SDMSScope ss = (SDMSScope)i_ss.next();
			ss.internalCopy(sysEnv, newId, ss.getName(sysEnv), rMap, pMap);
		}
		Vector v_r = SDMSResourceTable.idx_scopeId.getVector(sysEnv, id);
		Iterator i_r = v_r.iterator();
		while (i_r.hasNext()) {
			SDMSResource r = (SDMSResource)i_r.next();
			SDMSResource newR = SDMSResourceTable.table.create(sysEnv,
						r.getNrId(sysEnv),
						newId,
						null,
						defaultGId,
						null,
						null,
						null,
						r.getRsdId(sysEnv),
						null,
						r.getDefinedAmount(sysEnv),
						r.getRequestableAmount(sysEnv),
						r.getDefinedAmount(sysEnv),
						r.getDefinedAmount(sysEnv),
						r.getIsOnline(sysEnv),
						r.getFactor(sysEnv),
						r.getTraceInterval(sysEnv),
						r.getTraceBase(sysEnv),
						r.getTraceBaseMultiplier(sysEnv),
						fzero,
						fzero,
						fzero,
						fzero,
						lzero,
						lzero
			);
			rMap.put(r.getId(sysEnv), newR.getId(sysEnv));
		}

		ScopeConfig.copy(sysEnv, id, newId);
		ScopeParameter.copy(sysEnv, id, newId);
		if (type.equals(new Integer(SDMSScope.SERVER))) {
			SystemEnvironment.sched.notifyChange(sysEnv, f, SchedulingThread.CREATE);
		}
		return f;
	}

	private UserConnection getConnection(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSThread[]    list;
		ThreadGroup tg = SystemEnvironment.utg;
		list = new SDMSThread[tg.activeCount()];
		int nt = tg.enumerate(list);
		UserConnection uc;

		for(int i=0; i<nt; i++) {
			if(list[i] instanceof ListenThread) continue;
			if(sysEnv.cEnv.getMe().equals(list[i])) continue;
			uc = (UserConnection) list[i];
			if(uc.iAmAlive()) {
				ConnectionEnvironment env = uc.getEnv();
				if(env == null) continue;
				if(env.uid() == null) continue;
				if(env.uid().equals(getId(sysEnv))) {
					return uc;
				}
			}
		}
		return null;
	}

	public boolean isConnected(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(getType(sysEnv).intValue() == SDMSScope.SERVER) {
			if(getConnection(sysEnv) != null) return true;
			return false;
		}
		return false;
	}

	public int getConnectionId(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(getType(sysEnv).intValue() == SDMSScope.SERVER) {
			UserConnection uc;
			uc = getConnection(sysEnv);
			if(uc != null) return uc.id();
			return -1;
		}
		return -1;
	}

	public long getIdle(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(getType(sysEnv).intValue() == SDMSScope.SERVER) {
			UserConnection uc = getConnection(sysEnv);
			if(uc != null) {
				ConnectionEnvironment env = uc.getEnv();
				return env.idle();
			} else {
				Long lastActive = getLastActive(sysEnv);
				if (lastActive == null) return sysEnv.cEnv.last()/1000;
				return (sysEnv.cEnv.last() - lastActive.longValue() + 500)/1000;
			}
		}
		return 0;
	}

	public void setIsRegistered(SystemEnvironment sysEnv, Boolean state)
		throws SDMSException
	{
		if(state.equals(getIsRegistered(sysEnv))) return ;
		super.setIsRegistered(sysEnv, state);
		SystemEnvironment.sched.notifyChange(sysEnv, this, (state.booleanValue() ? SchedulingThread.REGISTER : SchedulingThread.DEREGISTER));
		return ;
	}

	public void setOwnerId(SystemEnvironment sysEnv, Long ownerId)
		throws SDMSException
	{
		if(ownerId.equals(getOwnerId(sysEnv))) return ;
		super.setOwnerId(sysEnv, ownerId);
		SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.ALTER);
		return ;
	}

	public void delete(SystemEnvironment sysEnv)
		throws SDMSException
	{
		delete(sysEnv, false);
	}

	public void delete(SystemEnvironment sysEnv, boolean cascade)
		throws SDMSException
	{
		myDelete(sysEnv, cascade);
	}

	public void myDelete(SystemEnvironment sysEnv, boolean cascade)
		throws SDMSException
	{
		Vector cv;
		final Long id = getId(sysEnv);

		if(cascade) {
			Vector v = SDMSScopeTable.idx_parentId.getVector(sysEnv, id);
			for(int i=0; i < v.size(); i++) {
				((SDMSScope) v.get(i)).myDelete(sysEnv, cascade);
			}
			cv = SDMSResourceTable.idx_scopeId.getVector(sysEnv, id);
			for(int j = 0; j < cv.size(); j++) {
				SDMSResource r = ((SDMSResource) cv.get(j));
				r.delete(sysEnv);
			}
		}

		cv = SDMSResourceTable.idx_scopeId.getVector(sysEnv, id);
		if(cv.size() != 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03603181720", "Scope not empty (contains resources)"));
		}
		cv = SDMSScopeTable.idx_parentId.getVector(sysEnv, id);
		if(cv.size() != 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03201301041", "Scope not empty (contains subscopes)"));
		}
		cv = SDMSSubmittedEntityTable.idx_scopeId.getVector(sysEnv, id);
		if(cv.size() != 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202231053", "Scope $1 contains Jobs", pathString(sysEnv)));
		}
		if(isConnected(sysEnv))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03209162133", "Server still connected"));

		ScopeConfig.kill (sysEnv, this);
		ScopeParameter.kill (sysEnv, getId (sysEnv));

		if(getType(sysEnv).intValue() == SDMSScope.SERVER) {
			cv = SDMSUserEquivTable.idx_altUId.getVector(sysEnv, id);
			for (int i = 0; i < cv.size(); ++i) {
				SDMSUserEquiv ue = (SDMSUserEquiv) cv.get(i);
				ue.delete(sysEnv);
			}
			SystemEnvironment.sched.notifyChange(sysEnv, this, SchedulingThread.DELETE);
		}

		super.delete(sysEnv);
	}

	public String getVariableValue (final SystemEnvironment sysEnv, final String key)
		throws SDMSException
	{
		return SVR.getVariableValue (sysEnv, this, key, -1);
	}

	public String getVariableValue (final SystemEnvironment sysEnv, final String key, final long version)
		throws SDMSException
	{
		return SVR.getVariableValue (sysEnv, this, key, version);
	}

	public long addImplicitPrivs(long priv)
	{
		priv = super.addImplicitPrivs(priv);
		if ((priv & SDMSPrivilege.EDIT) != 0)
			priv = priv | SDMSPrivilege.RESOURCE;
		return priv;
	}

	public boolean canExecute(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		SDMSPrivilege p = null;
		HashSet hg = new HashSet();
		Long smeOwner = sme.getOwnerId(sysEnv);
		hg.add(smeOwner);
		hg.add(SDMSObject.publicGId);

		sysEnv.cEnv.pushGid(sysEnv, hg);
		try {
			p = new SDMSPrivilege(sysEnv, getPrivileges(sysEnv, SDMSPrivilege.EXECUTE, true, null));
		} catch (Throwable t) {
			sysEnv.cEnv.popGid(sysEnv);
			throw t;
		}
		sysEnv.cEnv.popGid(sysEnv);

		return p.can(SDMSPrivilege.EXECUTE);
	}

	public String getURLName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return pathString(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (getType(sysEnv).intValue() == SDMSScope.SCOPE)
			return "scope " + getURLName(sysEnv);
		else
			return "jobserver " + getURLName(sysEnv);
	}

	public String getSubtypeName(SystemEnvironment env)
		throws SDMSException
	{
		return getTypeAsString(env);
	}

	public void notify(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (this.getType(sysEnv).intValue() == SDMSScope.SERVER) {
			String sport = ScopeConfig.getItem(sysEnv, this, Config.NOTIFY_PORT);
			if (sport == null) return;
			UserConnection uc = getConnection(sysEnv);
			InetAddress addr;
			if (uc == null) {
				String host = ScopeConfig.getItem(sysEnv, this, Config.HTTP_HOST);
				if (host == null || host.equals("")) return;
				try {
					addr = InetAddress.getByName(host);
				} catch(Exception e) {
					return;
				}
			} else {
				ConnectionEnvironment cEnv = uc.getEnv();
				addr = cEnv.getAddress();
			}
			if (addr == null) return;

			int port;
			try {
				port = Integer.parseInt(sport);
			} catch (NumberFormatException nfe) {
				return;
			}

			byte[] sbuf = getId(sysEnv).toString().getBytes();

			try {
				DatagramPacket sd = new DatagramPacket(sbuf, 0, sbuf.length, addr, port);
				if (SystemEnvironment.notifySocket == null)
					SystemEnvironment.notifySocket = new DatagramSocket();
				SystemEnvironment.notifySocket.send(sd);
			} catch (Exception e) {
				System.out.println("Something went wrong : " + e.toString());
			}
		} else {
			Vector v = SDMSScopeTable.idx_parentId.getVector(sysEnv, this.getId(sysEnv));
			SDMSScope s;
			for (int i = 0; i < v.size(); i++) {
				s = (SDMSScope) v.get(i);
				s.notify(sysEnv);
			}
		}
	}

	public long getPrivileges(SystemEnvironment sysEnv, long checkPrivs, boolean fastFail, Vector checkGroups)
		throws SDMSException
	{
		long p = super.getPrivileges(sysEnv, checkPrivs, fastFail, checkGroups);
		if(sysEnv.cEnv.isUser())
			if (getParentId(sysEnv) == null)
				p = p | SDMSPrivilege.VIEW;
		return p & checkPrivs;
	}
}
