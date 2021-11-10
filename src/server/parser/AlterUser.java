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
package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class AlterUser extends ManipUser
{

	public final static String __version = "@(#) $Id: AlterUser.java,v 2.5.4.1 2013/03/14 10:24:23 ronald Exp $";

	private boolean noerr;
	private boolean suActive;
	private boolean manageUser;
	private SDMSMessage feedbackMsg;

	public AlterUser(ObjectURL u, WithHash w, Boolean n)
	{
		super(u, w);
		noerr = n.booleanValue();
	}

	public AlterUser(String u, WithHash w, Boolean n)
	{
		super(u, w);
		noerr = n.booleanValue();
	}

	private void commonInit(SystemEnvironment sysEnv)
	{
		suActive = false;
		manageUser = false;
		feedbackMsg = new SDMSMessage(sysEnv, "03202062029", "User altered");
	}

	private void alterParameters(SystemEnvironment sysEnv, SDMSUser u)
	throws SDMSException
	{
		if (with.containsKey(ParseStr.S_PARAMETERS)) {
			createParameters(sysEnv, (WithHash) with.get(ParseStr.S_PARAMETERS), u);
			with.remove(ParseStr.S_PARAMETERS);
		}
		if (with.containsKey(ParseStr.S_DELPARM)) {
			delParameters(sysEnv, u, (Vector) with.get(ParseStr.S_DELPARM), false);
			with.remove(ParseStr.S_DELPARM);
		}
		if (with.containsKey(ParseStr.S_XDELPARM)) {
			delParameters(sysEnv, u, (Vector) with.get(ParseStr.S_XDELPARM), true);
			with.remove(ParseStr.S_XDELPARM);
		}
		if (with.containsKey(ParseStr.S_ALTPARM)) {
			addOrAlterParameters(sysEnv, (WithHash) with.get(ParseStr.S_ALTPARM), u, false, false);
			with.remove(ParseStr.S_ALTPARM);
		}
		if (with.containsKey(ParseStr.S_XALTPARM)) {
			addOrAlterParameters(sysEnv, (WithHash) with.get(ParseStr.S_XALTPARM), u, false, true);
			with.remove(ParseStr.S_XALTPARM);
		}
		if (with.containsKey(ParseStr.S_ADDPARM)) {
			addOrAlterParameters(sysEnv, (WithHash) with.get(ParseStr.S_ADDPARM), u, true, false);
			with.remove(ParseStr.S_ADDPARM);
		}
		if (with.containsKey(ParseStr.S_XADDPARM)) {
			addOrAlterParameters(sysEnv, (WithHash) with.get(ParseStr.S_XADDPARM), u, true, true);
			with.remove(ParseStr.S_XADDPARM);
		}
	}

	private void alterByUser(SystemEnvironment sysEnv, SDMSUser u)
	throws SDMSException
	{
		HashSet hg = new HashSet();
		hg.add(SDMSObject.adminGId);
		sysEnv.cEnv.pushGid(sysEnv, hg);
		try {
			if (passwd != null) {
				u.setPasswd(sysEnv, passwd);
				u.setSalt(sysEnv, salt);
				u.setMethod(sysEnv, method);
			}
			if (with.containsKey(ParseStr.S_DEFAULTGROUP)) {
				u.setDefaultGId(sysEnv, defaultGId);
				with.remove(ParseStr.S_DEFAULTGROUP);
			}
			alterParameters(sysEnv, u);
		} catch (Throwable t) {
			sysEnv.cEnv.popGid(sysEnv);
			throw t;
		}
		sysEnv.cEnv.popGid(sysEnv);
		if (with.size() != 0) {
			throw new AccessViolationException (new SDMSMessage(sysEnv, "03011101420", "Insufficient privileges"));
		}
		result.setFeedback(feedbackMsg);
	}

	private void setSystemPwd(SystemEnvironment sysEnv)
	throws SDMSException
	{
		if (sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			Properties props = new Properties();
			InputStream ini;
			String iniFile = sysEnv.server.getIniFile();
			ini = Server.class.getResourceAsStream(iniFile);
			try {
				if(ini == null)
					ini = new FileInputStream(iniFile);
				props.load(ini);
			} catch(FileNotFoundException fnf) {
				SDMSThread.doTrace(null, "Properties File not found : " + fnf, SDMSThread.SEVERITY_ERROR);
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03501211409",
				                                "Change of SYSTEM password failed: Properties File not found"));
			} catch(IOException ioe) {
				SDMSThread.doTrace(null, "Error loading Properties file: " + ioe, SDMSThread.SEVERITY_ERROR);
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03501211410",
				                                "Change of SYSTEM password failed: Error loading Properties file"));
			}
			String confPwd = props.getProperty(SystemEnvironment.S_SYSPASSWD);
			if (!txtPasswd.equals(confPwd))
				throw new CommonErrorException (new SDMSMessage(sysEnv, "03501211411",
				                                "Change of SYSTEM password failed: Password in configuration file " + iniFile + " must be changed first"));
			SystemEnvironment.sysPasswd = txtPasswd;
			feedbackMsg = new SDMSMessage(sysEnv, "03102151013", "SYSTEM Password changed");
		} else {
			throw new AccessViolationException (new SDMSMessage(sysEnv, "03102151020", "Insufficient privileges accessing User SYSTEM"));
		}
	}

	private void alterGroups(SystemEnvironment sysEnv, Long uId)
	throws SDMSException
	{
		if(with.containsKey(ParseStr.S_GROUPLIST)) {
			Vector oldgroups = SDMSMemberTable.idx_uId.getVector(sysEnv, uId);
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				boolean canAlter = true;
				SDMSPrivilege p = new SDMSPrivilege();
				for (int i = 0; i < grouplist.size(); i++) {
					Long gId = (Long) grouplist.get(i);
					if (!sysEnv.cEnv.gid().contains(gId)) {
						canAlter = false;
						for(int j = 0; j < oldgroups.size(); j++) {
							SDMSMember m = (SDMSMember) oldgroups.get(j);
							Long mGId = m.getGId(sysEnv);
							if(gId.equals(mGId)) {
								canAlter = true;
							}
						}
						if (!canAlter) {
							break;
						}
					}
				}
				if (canAlter && manageUser) {
					HashSet hg = new HashSet();
					hg.add(SDMSObject.adminGId);
					sysEnv.cEnv.pushGid(sysEnv, hg);
					suActive = true;
				}
			}

			Long gId;
			if(!grouplist.contains(defaultGId) && !publicGId.equals(defaultGId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03401271115", "You cannot remove the default group"));
			}

			for(int i = 0; i < oldgroups.size(); i++) {
				SDMSMember m = (SDMSMember) oldgroups.get(i);
				Long mGId = m.getGId(sysEnv);
				if(!grouplist.contains(mGId) && !mGId.equals(publicGId) && !mGId.equals(defaultGId)) {
					m.delete(sysEnv);
				} else {
					grouplist.remove(mGId);
				}
			}

			for(int i = 0; i < grouplist.size(); i++) {
				gId = (Long) grouplist.get(i);
				SDMSThread.doTrace(sysEnv.cEnv, "uId = " + uId + ", gId = " + gId + ", defaultGId = " + defaultGId + ", publicGId = " + publicGId, SDMSThread.SEVERITY_DEBUG);
				if(!gId.equals(defaultGId) && !gId.equals(publicGId))
					SDMSMemberTable.table.create(sysEnv, gId, uId);
			}
			try {
				SDMSMemberTable.table.create(sysEnv, defaultGId, uId);
			} catch (DuplicateKeyException dke) {
			}
			if (suActive) {
				sysEnv.cEnv.popGid(sysEnv);
				suActive = false;
			}
		}

		if(with.containsKey(ParseStr.S_ADDGROUP)) {
			if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
				boolean canAlter = true;
				SDMSPrivilege p = new SDMSPrivilege();
				for (int i = 0; i < addlist.size(); i++) {
					Long gId = (Long) addlist.get(i);
					if (!sysEnv.cEnv.gid().contains(gId)) {
						canAlter = false;
						break;
					}
				}
				if (canAlter && manageUser) {
					HashSet hg = new HashSet();
					hg.add(SDMSObject.adminGId);
					sysEnv.cEnv.pushGid(sysEnv, hg);
					suActive = true;
				}
			}

			Long gId;
			for(int i = 0; i < addlist.size(); i++) {
				gId = (Long) addlist.get(i);
				if(!gId.equals(defaultGId)) {
					try {
						SDMSMemberTable.table.create(sysEnv, gId, uId);
					} catch (DuplicateKeyException dke) {
					}
				}
			}
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);
		}

		if(with.containsKey(ParseStr.S_DELGROUP)) {
			if (manageUser) {
				HashSet hg = new HashSet();
				hg.add(SDMSObject.adminGId);
				sysEnv.cEnv.pushGid(sysEnv, hg);
				suActive = true;
			}
			SDMSMember m;
			Long gId;
			for(int i = 0; i < dellist.size(); i++) {
				gId = (Long) dellist.get(i);
				if(!gId.equals(defaultGId)) {
					if(gId.equals(publicGId)) continue;
					try {
						m = SDMSMemberTable.idx_gId_uId_getUnique(sysEnv, new SDMSKey(gId, uId));
						m.delete(sysEnv);
					} catch (NotFoundException nfe) {
					}
				} else {
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03312102203",
					                               "You cannot remove the default group"));
				}
			}
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);
		}
	}

	private void alterEquivalences(SystemEnvironment sysEnv, Long uId)
	throws SDMSException
	{
		Vector w = SDMSUserEquivTable.idx_uId.getVector(sysEnv, uId);
		SDMSUserEquiv ue;
		SDMSUser tmpu;
		SDMSScope tmps;
		Object lookUpValue;
		if (userEquiv == null) {
			for (int i = 0; i < w.size(); ++i) {
				ue = (SDMSUserEquiv) w.get(i);
				ue.delete(sysEnv);
			}
		} else {
			for (int i = 0; i < w.size(); ++i) {
				ue = (SDMSUserEquiv) w.get(i);
				Long eId = ue.getAltUId(sysEnv);
				try {
					tmpu = SDMSUserTable.getObject(sysEnv, eId);
					lookUpValue = tmpu.getName(sysEnv);
				} catch (NotFoundException nfe) {
					tmps = SDMSScopeTable.getObject(sysEnv, eId);
					lookUpValue = tmps.pathVector(sysEnv);
				}
				if (userEquiv.contains(lookUpValue)) {
					userEquiv.remove(lookUpValue);
				} else {
					ue.delete(sysEnv);
				}
			}
			for (int i = 0; i < userEquiv.size(); ++i) {
				Object o = (Object) userEquiv.get(i);
				if (o instanceof String) {
					try {
						SDMSUser eu = SDMSUserTable.idx_name_getUnique(sysEnv, (String) o);
						if (!eu.getDeleteVersion(sysEnv).equals(new Long(0))) {
							throw new NotFoundException(new SDMSMessage(sysEnv, "03707311522", "User " + o.toString() + " not found"));
						}
						SDMSUserEquivTable.table.create(sysEnv, uId, new Integer(SDMSUserEquiv.USER), eu.getId(sysEnv));
					} catch (NotFoundException nfe) {
						throw nfe;
					}
				} else {
					if (o instanceof PathVector) {
						try {
							SDMSScope s = SDMSScopeTable.getScope(sysEnv, (PathVector) o);
							if (s.getType(sysEnv).intValue() != SDMSScope.SERVER) {
								throw new NotFoundException(new SDMSMessage(sysEnv, "03707311523", "No job server " + o.toString() + " found"));
							}
							SDMSUserEquivTable.table.create(sysEnv, uId, new Integer(SDMSUserEquiv.SERVER), s.getId(sysEnv));
						} catch (NotFoundException nfe) {
							throw nfe;
						}
					}
				}
			}
		}
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSUser u;
		Long uId;

		commonInit(sysEnv);
		salt = generateSalt();

		evaluate_with(sysEnv);

		try {
			if (url == null)
				u = SDMSUserTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(user, new Long(0)));
			else
				u = (SDMSUser) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311121715", "No user altered"));
				return;
			}
			throw nfe;
		}
		uId = u.getId(sysEnv);

		if(defaultGId == null)
			defaultGId = u.getDefaultGId(sysEnv);

		if (!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			SDMSPrivilege p = new SDMSPrivilege();
			Iterator i = sysEnv.cEnv.gid().iterator();
			while (i.hasNext()) {
				Long gId = (Long) i.next();
				try {
					SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(ZERO, gId));
					p.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
				} catch (NotFoundException nfe) {
				}
			}
			if (p.can(SDMSPrivilege.MANAGE_USER)) {
				manageUser = true;
			}
		} else {
			manageUser = true;
		}

		if(sysEnv.cEnv.uid().equals(uId) && uId != 0) {
			alterByUser(sysEnv, u);
			if (!manageUser)
				return;
		} else {
			if(passwd != null && uId != 0) {
				u.setPasswd(sysEnv, passwd);
				u.setSalt(sysEnv, salt);
				u.setMethod(sysEnv, method);
			} else {
				if (passwd != null) {
					setSystemPwd(sysEnv);
				}
			}
			if(with.containsKey(ParseStr.S_DEFAULTGROUP))
				u.setDefaultGId(sysEnv, defaultGId);
			alterParameters(sysEnv, u);
		}

		if(with.containsKey(ParseStr.S_ENABLE)) {
			u.setIsEnabled(sysEnv, enable);
		}

		try {
			alterGroups(sysEnv, uId);
		} catch (Throwable t) {
			if (suActive)
				sysEnv.cEnv.popGid(sysEnv);
			throw t;
		}

		if (with.containsKey(ParseStr.S_CONNECT)) {
			u.setConnectionType(sysEnv, (Integer) with.get(ParseStr.S_CONNECT));
		}

		if (sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			if (with.containsKey(ParseStr.S_EQUIVALENT)) {
				alterEquivalences(sysEnv, uId);
			}
		}

		if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(defaultGId, uId)))
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03312130121", "a user must belong to his default group"));

		result.setFeedback(feedbackMsg);
	}
}

