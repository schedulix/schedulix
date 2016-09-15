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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.ManipParameters;

public class SDMSFolder extends SDMSFolderProxyGeneric
	implements SDMSOwnedObject
{

	public final static String __version = "@(#) $Id: SDMSFolder.java,v 2.16.2.5 2013/03/19 17:16:51 ronald Exp $";

	private final static VariableResolver FVR = new FolderVariableResolver();

	private final static Long lzero = new Long(0);
	private final static Integer zero = new Integer(0);
	private final static Float fzero = new Float(0);

	protected SDMSFolder(SDMSObject p_object)
	{
		super(p_object);
	}

	public SDMSFolder copy(SystemEnvironment sysEnv, Long targetFolderId, String name)
		throws SDMSException
	{
		if(!checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(accessViolationMessage(sysEnv, "03402291137"));
		HashMap relocationTable = new HashMap();
		SDMSFolder f = copy(sysEnv, targetFolderId, name, relocationTable);
		f.relocateEntityDetails(sysEnv, relocationTable);
		return f;
	}

	public SDMSFolder copy(SystemEnvironment sysEnv, Long targetFolderId, String name, HashMap relocationTable)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		SDMSUser u = SDMSUserTable.getObject(sysEnv, sysEnv.cEnv.uid());
		SDMSFolder f = SDMSFolderTable.table.create(sysEnv,
				name,
				u.getDefaultGId(sysEnv),
				getEnvId(sysEnv),
				targetFolderId,
				getInheritPrivs(sysEnv)
		);
		Long newId = f.getId(sysEnv);
		ManipParameters.copy(sysEnv, id, newId);
		Vector v_sf = SDMSFolderTable.idx_parentId.getVector(sysEnv, id);
		Iterator i_sf = v_sf.iterator();
		while (i_sf.hasNext()) {
			SDMSFolder sf = (SDMSFolder)i_sf.next();
			if(!sf.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(accessViolationMessage(sysEnv, "03402291134"));
			sf.copy(sysEnv, newId, sf.getName(sysEnv), relocationTable);
		}
		Vector v_se = SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, id);
		Iterator i_se = v_se.iterator();
		while (i_se.hasNext()) {
			SDMSSchedulingEntity se_o = (SDMSSchedulingEntity)i_se.next();
			if(!se_o.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
				throw new AccessViolationException(accessViolationMessage(sysEnv, "03402291135"));
			SDMSSchedulingEntity se_n = se_o.copy(sysEnv, newId, se_o.getName(sysEnv), relocationTable);
			relocationTable.put(se_o.getId(sysEnv), se_n.getId(sysEnv));
		}
		Vector v_r = SDMSResourceTable.idx_scopeId.getVector(sysEnv, id);
		Iterator i_r = v_r.iterator();
		while(i_r.hasNext()) {
			SDMSResource r_o = (SDMSResource) i_r.next();
			SDMSResource r_n = SDMSResourceTable.table.create(sysEnv,
			                   r_o.getNrId(sysEnv),
			                   newId,
			                   r_o.getMasterId(sysEnv),
			                   r_o.getOwnerId(sysEnv),
			                   r_o.getLinkId(sysEnv),
			                   null,
			                   r_o.getTag(sysEnv),
			                   r_o.getRsdId(sysEnv),
			                   r_o.getRsdTime(sysEnv),
			                   r_o.getDefinedAmount(sysEnv),
			                   r_o.getRequestableAmount(sysEnv),
			                   r_o.getDefinedAmount(sysEnv),
			                   r_o.getDefinedAmount(sysEnv),
			                   r_o.getIsOnline(sysEnv),
			                   r_o.getFactor(sysEnv),
			                   r_o.getTraceInterval(sysEnv),
			                   r_o.getTraceBase(sysEnv),
			                   r_o.getTraceBaseMultiplier(sysEnv),
			                   fzero,
			                   fzero,
			                   fzero,
			                   fzero,
			                   lzero,
			                   lzero
			                                                 );
			r_o.copyVariables(sysEnv, r_n.getId(sysEnv));
		}
		Vector ocv = SDMSObjectCommentTable.idx_objectId.getVector(sysEnv, id);
		for (int ii = 0; ii < ocv.size(); ++ii) {
			SDMSObjectComment oc = (SDMSObjectComment) ocv.get(ii);
			SDMSObjectCommentTable.table.create(sysEnv,
			                                    newId,
			                                    oc.getObjectType(sysEnv),
			                                    oc.getInfoType(sysEnv),
			                                    oc.getSequenceNumber(sysEnv),
			                                    oc.getTag(sysEnv),
			                                    oc.getDescription(sysEnv)
			                                   );
		}
		return f;
	}

	public void relocateEntityDetails(SystemEnvironment sysEnv, HashMap relocationTable)
		throws SDMSException
	{
		Long id = getId(sysEnv);
		Vector v_sf = SDMSFolderTable.idx_parentId.getVector(sysEnv, id);
		Iterator i_sf = v_sf.iterator();
		while (i_sf.hasNext()) {
			SDMSFolder sf = (SDMSFolder)i_sf.next();
			sf.relocateEntityDetails(sysEnv, relocationTable);
		}
		Vector v_se = SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, id);
		Iterator i_se = v_se.iterator();
		while (i_se.hasNext()) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity)i_se.next();
			se.relocateDetails(sysEnv, relocationTable);
		}
	}

	public void collectSeIds (SystemEnvironment sysEnv, HashSet<Long> seIds, HashSet<Long> keeplist)
	throws SDMSException
	{
		Long id = getId(sysEnv);
		Vector v_se = SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, id);
		Iterator i_se = v_se.iterator();
		while (i_se.hasNext()) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity)i_se.next();
			Long seId = se.getId(sysEnv);
			if (keeplist != null && keeplist.contains(seId)) {
				continue;
			}
			seIds.add(se.getId(sysEnv));
		}
		Vector v_sf = SDMSFolderTable.idx_parentId.getVector(sysEnv, id);
		Iterator i_sf = v_sf.iterator();
		while (i_sf.hasNext()) {
			SDMSFolder sf = (SDMSFolder)i_sf.next();
			sf.collectSeIds(sysEnv, seIds, keeplist);
		}
	}

	public void delete(SystemEnvironment sysEnv)
		throws SDMSException
	{
		boolean dummy = delete (sysEnv, null);
	}

	public void deleteCascade(SystemEnvironment sysEnv, HashSet keeplist)
	throws SDMSException
	{
		Vector v_sf = SDMSFolderTable.idx_parentId.getVector(sysEnv, getId(sysEnv));
		Iterator i_sf = v_sf.iterator();
		while (i_sf.hasNext()) {
			SDMSFolder sf = (SDMSFolder)i_sf.next();
			sf.deleteCascade(sysEnv, keeplist);
		}
		boolean dummy = delete(sysEnv, keeplist);
	}

	public boolean  delete(SystemEnvironment sysEnv, HashSet keeplist)
		throws SDMSException
	{
		final Long fId = getId(sysEnv);

		boolean dropped_all_resources = dropResources(sysEnv, keeplist);

		if (dropped_all_resources && (keeplist == null || !(keeplist.contains(fId)))) {
			if(SDMSFolderTable.idx_parentId.containsKey(sysEnv, fId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03112191517",
				                               "Folder $1 not empty", pathString(sysEnv)));
			}
			if(SDMSSchedulingEntityTable.idx_folderId.containsKey(sysEnv, fId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03112191519",
				                               "Folder $1 not empty", pathString(sysEnv)));
			}
			ManipParameters.kill (sysEnv, fId);
			SDMSNiceProfileEntry npe;
			SDMSNiceProfile np;
			Vector npev = SDMSNiceProfileEntryTable.idx_folderId.getVector(sysEnv, fId);
			for (int i = 0; i < npev.size(); ++i) {
				npe = (SDMSNiceProfileEntry) npev.get(i);
				if (npe.getIsActive(sysEnv).booleanValue()) {
					np = SDMSNiceProfileTable.getObject(sysEnv, npe.getNpId(sysEnv));
					if (!np.getIsActive(sysEnv).booleanValue())
						npe.delete(sysEnv);
					else
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03408211524",
						                               "Folder $1 is addressed by active Nice Profile $2", pathString(sysEnv), np.getName(sysEnv)));
				} else {
					npe.delete(sysEnv);
				}
			}
			super.delete(sysEnv);
		}
		return dropped_all_resources;
	}

	public void dropResources (SystemEnvironment sysEnv)
		throws SDMSException
	{
		boolean dummy = dropResources (sysEnv, null);
	}

	public boolean dropResources (SystemEnvironment sysEnv, HashSet keeplist)
		throws SDMSException
	{
		boolean dropped_all_resources = true;
		final Long fId = getId(sysEnv);

		final Vector rv = SDMSResourceTable.idx_scopeId.getVector(sysEnv, fId);
		for (int i = 0; i < rv.size(); i++) {
			final SDMSResource r = (SDMSResource) rv.get(i);
			if (keeplist != null && keeplist.contains(r.getId(sysEnv))) {
				dropped_all_resources = false;
			} else {
				r.delete(sysEnv);
			}
		}
		return dropped_all_resources;
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key)
		throws SDMSException
	{
		return FVR.getVariableValue(sysEnv, this, key, -1);
	}

	public String getVariableValue(SystemEnvironment sysEnv, String key, long version)
		throws SDMSException
	{
		return FVR.getVariableValue(sysEnv, this, key, version);
	}

	public String getURLName(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return pathString(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
		throws SDMSException
	{
		return "folder " + getURLName(sysEnv);
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
