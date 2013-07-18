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
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class DropJobDefinition extends Node
{

	public final static String __version = "@(#) $Id: DropJobDefinition.java,v 2.7.4.2 2013/03/19 17:16:46 ronald Exp $";

	Vector path;
	String name;
	Boolean force;
	boolean ignoreNotFound;

	public DropJobDefinition(Vector p, String n, Boolean f, boolean ignNF)
	{
		super();
		name = n;
		path = p;
		force = f;
		ignoreNotFound = ignNF;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSFolder f = SDMSFolderTable.getFolder(sysEnv, path);
		SDMSSchedulingEntity se = null;
		try {
			se = SDMSSchedulingEntityTable.idx_folderId_name_getUnique(sysEnv, new SDMSKey(f.getId(sysEnv), name));
		} catch(NotFoundException nfe) {
			if(ignoreNotFound) {
				result.setFeedback(new SDMSMessage(sysEnv, "03301291253", "Job Definition dropped"));
				return;
			} else {
				throw nfe;
			}
		}
		Long seId = se.getId(sysEnv);

		if(!force.booleanValue()) {
			if(SDMSDependencyDefinitionTable.idx_seRequiredId.containsKey(sysEnv, seId)) {
				SDMSDependencyDefinition dd =
				        (SDMSDependencyDefinition) SDMSDependencyDefinitionTable.idx_seRequiredId.getVector(sysEnv, seId).get(0);
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03112202113",
				                               "Dependent jobs ($1) exist, specify force to delete anyway",
				                               SDMSSchedulingEntityTable.getObject(sysEnv, dd.getSeDependentId(sysEnv)).pathString(sysEnv)));
			}
			if(SDMSSchedulingHierarchyTable.idx_seChildId.containsKey(sysEnv, seId)) {
				SDMSSchedulingHierarchy sh =
				        (SDMSSchedulingHierarchy) SDMSSchedulingHierarchyTable.idx_seChildId.getVector(sysEnv, seId).get(0);
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03207042302",
				                               "Job is used as a child of $1, specify force to delete anyway",
				                               SDMSSchedulingEntityTable.getObject(sysEnv, sh.getSeParentId(sysEnv))));
			}
			if(SDMSTriggerTable.idx_seId.containsKey(sysEnv, seId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03301221500",
				                               "Job is used as a trigger job, specify force to delete anyway"));
			}
			if(SDMSTriggerTable.idx_mainSeId.containsKey(sysEnv, seId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03109120829",
				                               "Job is used as a trigger job (mainSe), specify force to delete anyway"));
			}
			if(SDMSTriggerTable.idx_parentSeId.containsKey(sysEnv, seId)) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03109120830",
				                               "Job is used as a trigger job (parentSe), specify force to delete anyway"));
			}
		}

		se.delete(sysEnv);

		result.setFeedback(new SDMSMessage(sysEnv, "03204112232", "Job Definition dropped"));
	}

}

