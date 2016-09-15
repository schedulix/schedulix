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

public class CreateFolder extends Node
{

	public final static String __version = "@(#) $Id: CreateFolder.java,v 2.10.2.2 2013/03/19 10:03:52 ronald Exp $";

	private PathVector path;
	private String name;
	private WithHash with;
	private boolean replace;
	private AlterFolder af;

	public CreateFolder(PathVector p, WithHash w, Boolean r)
	{
		super();
		af = null;
		with = (w == null ? new WithHash() : w);
		replace = r.booleanValue();
		path = p;
		if (p.size() == 1) {
			af = new AlterFolder(new ObjectURL(new Integer(Parser.FOLDER), path), with, Boolean.FALSE);
		} else {
			name = (String) p.remove(p.size() - 1);
		}
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if (af != null) {
			af.setEnv(env);
			af.go(sysEnv);
			result = af.result;
			return;
		}

		Long parentId;
		String envName;
		Long envId = null;
		SDMSFolder f;

		SDMSFolder parent = SDMSFolderTable.getFolder(sysEnv, path);
		parentId = parent.getId(sysEnv);

		if(SDMSSchedulingEntityTable.idx_folderId_name.containsKey(sysEnv, new SDMSKey(parentId, name))) {
			throw new DuplicateKeyException(new SDMSMessage(sysEnv, "03201290935", "Object with name $1 already exists within $2",
					name, SDMSFolderTable.getObject(sysEnv, parentId).pathString(sysEnv)));
		}
		envName = (String) with.get(ParseStr.S_ENVIRONMENT);
		if(envName != null) {
			sysEnv.checkFeatureAvailability(SystemEnvironment.S_FOLDER_ENVIRONMENTS);
			envId = SDMSNamedEnvironmentTable.idx_name_getUnique(sysEnv, envName).getId(sysEnv);
		} else {
			envId = null;
		}

		final Long uId = env.uid();
		final SDMSUser u = SDMSUserTable.getObject(sysEnv, uId);
		final Long gId;
		if(!with.containsKey(ParseStr.S_GROUP)) {
			gId = u.getDefaultGId(sysEnv);
		} else {
			final String gName = (String) with.get(ParseStr.S_GROUP);
			gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			if(!SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(gId, uId)) &&
			   !SDMSMemberTable.idx_gId_uId.containsKey(sysEnv, new SDMSKey(SDMSObject.adminGId, uId))) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03312162226",
						"User $1 does not belong to Group $2", u.getName(sysEnv), gName));
			}
		}

		long lpriv = SDMSPrivilege.NOPRIVS;
		if (with.containsKey(ParseStr.S_INHERIT) && with.get(ParseStr.S_INHERIT) != null)
			lpriv = ((Long) with.get(ParseStr.S_INHERIT)).longValue();
		else if (parent.getOwnerId(sysEnv).equals(gId))
			lpriv = parent.getPrivilegeMask();

		if((parent.getPrivilegeMask() & lpriv) != lpriv) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061110", "Incompatible grant"));
		}
		Long inheritPrivs = new Long(lpriv);

		try {
			f = SDMSFolderTable.table.create(sysEnv, name, gId, envId, parentId, inheritPrivs);
		} catch(DuplicateKeyException dke) {
			if(replace) {
				path.add(name);
				af = new AlterFolder(new ObjectURL(new Integer(Parser.FOLDER), path), with, Boolean.FALSE);
				af.setEnv(env);
				af.go(sysEnv);
				result = af.result;
				return;
			} else {
				throw dke;
			}
		}

		ManipParameters.create (sysEnv, f.getId (sysEnv), (WithHash) with.get (ParseStr.S_PARAMETERS));

		result.setFeedback(new SDMSMessage(sysEnv, "03201280946", "Folder created"));
	}
}
