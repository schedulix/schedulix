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
		}

		try {
			SDMSObjectComment oc = SDMSObjectCommentTable.idx_objectId_getUnique(sysEnv, id);
			SDMSObjectCommentTable.table.create(sysEnv,
					newId,
					oc.getObjectType(sysEnv),
					oc.getInfoType(sysEnv),
					oc.getSequenceNumber(sysEnv),
					oc.getDescription(sysEnv)
			);
		} catch (NotFoundException nfe) {

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

	public void delete(SystemEnvironment sysEnv)
		throws SDMSException
	{
		boolean dummy = delete (sysEnv, null);
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

	public void deleteCascadeFirstPass(SystemEnvironment sysEnv, HashSet keeplist)
		throws SDMSException
	{
		deleteCascadeFirstPass(sysEnv, this, keeplist);
	}

	private void deleteCascadeFirstPass(SystemEnvironment sysEnv, SDMSFolder f, HashSet keeplist)
		throws SDMSException
	{

		Vector v = SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, f.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity) v.get(i);
			Long seId = se.getId(sysEnv);

			if (keeplist != null && keeplist.contains(seId)) {
				continue;
			}

			Vector tv = SDMSTriggerTable.idx_fireId.getVector(sysEnv, seId);
			for(int j = 0; j < tv.size(); j++) {
				SDMSTrigger t = (SDMSTrigger) tv.get(j);

				Vector tsv = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, t.getId(sysEnv));
				for(int k = 0; k < tsv.size(); k++) {
					((SDMSTriggerState) tsv.get(k)).delete(sysEnv);
				}
				t.delete(sysEnv);
			}

			Vector dv = SDMSDependencyDefinitionTable.idx_seDependentId.getVector(sysEnv, seId);
			for(int j = 0; j < dv.size(); j++) {
				SDMSDependencyDefinition dd = (SDMSDependencyDefinition) dv.get(j);
				dd.delete(sysEnv);
			}

			Vector hv = SDMSSchedulingHierarchyTable.idx_seParentId.getVector(sysEnv, seId);
			for(int j = 0; j < hv.size(); j++) {
				((SDMSSchedulingHierarchy) hv.get(j)).delete(sysEnv);
			}
		}

		v = SDMSFolderTable.idx_parentId.getVector(sysEnv, f.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			SDMSFolder tf = (SDMSFolder) v.get(i);
			deleteCascadeFirstPass(sysEnv, tf, keeplist);
		}

	}

	public boolean deleteCascadeSecondPass(SystemEnvironment sysEnv, HashSet parameterLinks, boolean force, HashSet keeplist)
		throws SDMSException
	{
		return deleteCascadeSecondPass(sysEnv, this, parameterLinks, force, keeplist);
	}

	private boolean deleteCascadeSecondPass(SystemEnvironment sysEnv, SDMSFolder f, HashSet parameterLinks, boolean force, HashSet keeplist)
		throws SDMSException
	{
		boolean dropped_all_content = true;

		Vector v = SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, f.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity) v.get(i);
			Long seId = se.getId(sysEnv);

			if (keeplist != null && keeplist.contains(seId)) {
				dropped_all_content = false;
				continue;
			}

			Vector tv = SDMSTriggerTable.idx_seId.getVector(sysEnv, seId);
			if(force) {
				for(int j = 0; j < tv.size(); j++) {
					SDMSTrigger t = (SDMSTrigger) tv.get(j);

					Vector tsv = SDMSTriggerStateTable.idx_triggerId.getVector(sysEnv, t.getId(sysEnv));
					for(int k = 0; k < tsv.size(); k++) {
						((SDMSTriggerState) tsv.get(k)).delete(sysEnv);
					}
					t.delete(sysEnv);
				}
			} else {
				if(tv.size() != 0) {
					SDMSTrigger tt = (SDMSTrigger) tv.get(0);
					String name = null;
					int type = tt.getObjectType(sysEnv).intValue();
					switch(type) {
						case SDMSTrigger.JOB_DEFINITION:
							name = SDMSSchedulingEntityTable.getObject(sysEnv, tt.getFireId(sysEnv)).pathString(sysEnv);
							break;
						case SDMSTrigger.RESOURCE:
							SDMSResource r = SDMSResourceTable.getObject(sysEnv, tt.getFireId(sysEnv));
							name = SDMSNamedResourceTable.getObject(sysEnv, r.getNrId(sysEnv)).pathString(sysEnv) +
							       " in " +
							       SDMSScopeTable.getObject(sysEnv, r.getScopeId(sysEnv)).pathString(sysEnv);
							break;
						case SDMSTrigger.NAMED_RESOURCE:
							name = SDMSNamedResourceTable.getObject(sysEnv, tt.getFireId(sysEnv)).pathString(sysEnv);
							break;
					}
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03207041735",
							"$1 in use by Trigger $2 in $3", se.pathString(sysEnv),
							tt.getName(sysEnv), name));
				}
			}

			Vector dv = SDMSDependencyDefinitionTable.idx_seRequiredId.getVector(sysEnv, seId);
			if(force) {
				for(int j = 0; j < dv.size(); j++) {
					SDMSDependencyDefinition dd = (SDMSDependencyDefinition) dv.get(j);
					dd.delete(sysEnv);
				}
			} else {
				if(dv.size() != 0) {
					SDMSSchedulingEntity tse = SDMSSchedulingEntityTable.getObject(sysEnv, ((SDMSDependencyDefinition) dv.get(0)).getSeDependentId(sysEnv));
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03207041736",
							"$1 is required by job definition $2", se.pathString(sysEnv),
							tse.pathString(sysEnv)));
				}
			}

			Vector hv = SDMSSchedulingHierarchyTable.idx_seChildId.getVector(sysEnv, seId);
			if(force) {
				for(int j = 0; j < hv.size(); j++) {
					((SDMSSchedulingHierarchy) hv.get(j)).delete(sysEnv);
				}
			} else {
				if(hv.size() != 0) {
					SDMSSchedulingEntity tse = SDMSSchedulingEntityTable.getObject(sysEnv,
								((SDMSSchedulingHierarchy) hv.get(0)).getSeParentId(sysEnv));
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03207041737",
							"$1 is child of job definition $2", se.pathString(sysEnv),
							tse.pathString(sysEnv)));
				}
			}

			se.delete(sysEnv, force, parameterLinks);
		}

		v = SDMSFolderTable.idx_parentId.getVector(sysEnv, f.getId(sysEnv));
		for(int i = 0; i < v.size(); i++) {
			SDMSFolder tf = (SDMSFolder) v.get(i);
			if (deleteCascadeSecondPass(sysEnv, tf, parameterLinks, force, keeplist)) {

				if (!(tf.delete(sysEnv, keeplist))) {
					dropped_all_content = false;
				}
			} else {

				tf.dropResources(sysEnv, keeplist);
				dropped_all_content = false;
			}
		}
		return dropped_all_content;
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
