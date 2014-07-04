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

public class AlterFolder extends Node
{

	public final static String __version = "@(#) $Id: AlterFolder.java,v 2.8.2.1 2013/03/14 10:24:20 ronald Exp $";

	private ObjectURL url;
	private WithHash with;
	private boolean noerr;

	public AlterFolder(ObjectURL u, WithHash w, Boolean ne)
	{
		super();
		url = u;
		with = w;
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		String envName;
		Long envId;
		SDMSFolder f;

		try {
			f = (SDMSFolder) url.resolve(sysEnv);
		} catch (NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311121732", "No folder altered"));
				return;
			}
			throw nfe;
		}
		final Long fId = f.getId (sysEnv);

		if(with.containsKey(ParseStr.S_ENVIRONMENT)) {
			envName = (String) with.get(ParseStr.S_ENVIRONMENT);
			if(envName == null) {
				f.setEnvId(sysEnv, null);
			} else {

				sysEnv.checkFeatureAvailability(SystemEnvironment.S_FOLDER_ENVIRONMENTS);

				envId = SDMSNamedEnvironmentTable.idx_name_getUnique(sysEnv, envName).getId(sysEnv);
				f.setEnvId(sysEnv, envId);
			}
		}

		if (with.containsKey(ParseStr.S_INHERIT)) {
			Long inheritPrivs = (Long) with.get(ParseStr.S_INHERIT);
			if (inheritPrivs == null) inheritPrivs = new Long(0);
			long lpriv = inheritPrivs.longValue();
			if((f.getPrivilegeMask() & lpriv) != lpriv) {
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03202061132", "Incompatible grant"));
			}

			f.setInheritPrivs(sysEnv, inheritPrivs);
		}

		if (with.containsKey (ParseStr.S_PARAMETERS))
			ManipParameters.alter (sysEnv, fId, (WithHash) with.get (ParseStr.S_PARAMETERS));

		if (with.containsKey(ParseStr.S_GROUP) || with.containsKey(ParseStr.S_GROUP_CASCADE)) {
			if(with.containsKey(ParseStr.S_GROUP) && with.containsKey(ParseStr.S_GROUP_CASCADE))
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03402031527",
						"You cannot specify both the group and the group cascade clause"));
			final String gName;
			if (with.containsKey(ParseStr.S_GROUP_CASCADE)) {
				gName = (String) with.get(ParseStr.S_GROUP_CASCADE);
			} else {
				gName = (String) with.get(ParseStr.S_GROUP);
			}
			final Long gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(
					sysEnv, new SDMSKey(gName, new Long(0))).getId(sysEnv);
			ChownChecker.check(sysEnv, gId);
			f.setOwnerId(sysEnv, gId);
			if (with.containsKey(ParseStr.S_GROUP_CASCADE)) {
				setChildOwner (sysEnv,f.getId(sysEnv), gId);
			}
		}

		result.setFeedback(new SDMSMessage(sysEnv, "03209151730", "Folder altered"));
	}

	private void setChildOwner (SystemEnvironment sysEnv,Long pId, Long gId)
		throws SDMSException
	{
		Vector v_c = SDMSFolderTable.idx_parentId.getVector (sysEnv, pId);
		Iterator i_c = v_c.iterator();
		while (i_c.hasNext()) {
			SDMSFolder f = (SDMSFolder)i_c.next();
			try {
				f.setOwnerId(sysEnv, gId);
			} catch (AccessViolationException ave) {

			}
			setChildOwner(sysEnv, f.getId(sysEnv), gId);
		}

		Vector v_se =SDMSSchedulingEntityTable.idx_folderId.getVector(sysEnv, pId);
		Iterator i_se = v_se.iterator();
		while (i_se.hasNext()) {
			SDMSSchedulingEntity se = (SDMSSchedulingEntity)i_se.next();
			try {
				se.setOwnerId(sysEnv, gId);
			} catch (AccessViolationException ave) {

			}
		}
	}
}
