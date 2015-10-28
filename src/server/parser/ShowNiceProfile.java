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
import de.independit.scheduler.server.output.*;

public class ShowNiceProfile extends ShowCommented
{
	private ObjectURL url;

	public ShowNiceProfile (ObjectURL u)
	{
		url = u;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSNiceProfile np;
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Date d = new Date();

		np = (SDMSNiceProfile) url.resolve(sysEnv);
		if(!np.checkPrivileges(sysEnv, SDMSPrivilege.VIEW))
			throw new AccessViolationException(new SDMSMessage(sysEnv, "03408110918", "Insufficient privileges"));
		Long npId = np.getId(sysEnv);

		desc.add("ID");
		desc.add("NAME");

		desc.add("IS_ACTIVE");

		desc.add("ACTIVE_TS");

		desc.add("COMMENT");
		desc.add("COMMENTTYPE");
		desc.add("CREATOR");
		desc.add("CREATE_TIME");
		desc.add("CHANGER");
		desc.add("CHANGE_TIME");
		desc.add("PRIVS");

		desc.add("ENTRIES");

		Vector data = new Vector();
		data.add(npId);
		data.add(np.getName(sysEnv));
		data.add(np.getIsActive(sysEnv));
		Long activeTs = np.getActiveTs(sysEnv);
		if (activeTs != null) {
			d.setTime(activeTs.longValue());
			data.add(sysEnv.systemDateFormat.format(d));
		} else {
			data.add(null);
		}
		data.add(getCommentContainer(sysEnv, npId));
		data.add(getCommentInfoType(sysEnv, npId));

		try {
			data.add(SDMSUserTable.getObject(sysEnv, np.getCreatorUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(np.getCreateTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		try {
			data.add(SDMSUserTable.getObject(sysEnv, np.getChangerUId(sysEnv)).getName(sysEnv));
		} catch (NotFoundException nfe) {
			data.add(SDMSUserTable.DROPPED_NAME);
		}
		d.setTime(np.getChangeTs(sysEnv).longValue());
		data.add(sysEnv.systemDateFormat.format(d));
		data.add(np.getPrivileges(sysEnv).toString());

		Vector r_desc = new Vector();

		r_desc.add("ID");

		r_desc.add("PREFERENCE");

		r_desc.add("FOLDER_ID");

		r_desc.add("FOLDER_NAME");

		r_desc.add("FOLDER_TYPE");

		r_desc.add("ACTIVE");

		r_desc.add("RENICE");

		r_desc.add("IS_SUSPENDED");

		SDMSOutputContainer s_container = new SDMSOutputContainer(sysEnv, null, r_desc);

		Vector npe_v = SDMSNiceProfileEntryTable.idx_npId.getVector(sysEnv, npId);

		SDMSNiceProfileEntry npe;
		Long folderId;
		SDMSFolder f;
		SDMSSchedulingEntity se;

		Vector s_data;
		Iterator i = npe_v.iterator();
		while (i.hasNext()) {
			npe = (SDMSNiceProfileEntry)(i.next());
			s_data = new Vector();
			s_data.add(npe.getId(sysEnv));
			s_data.add(npe.getPreference(sysEnv));
			folderId = npe.getFolderId(sysEnv);
			s_data.add(folderId);
			if (folderId != null) {
				try {
					f = SDMSFolderTable.getObject(sysEnv, folderId);
					s_data.add(f.pathString(sysEnv));
					s_data.add("FOLDER");
				} catch (NotFoundException nfe) {
					se = SDMSSchedulingEntityTable.getObject(sysEnv, folderId);
					s_data.add(se.pathString(sysEnv));
					s_data.add(se.getTypeAsString(sysEnv));
				}
			} else {
				s_data.add(null);
				s_data.add(null);
			}
			s_data.add(npe.getIsActive(sysEnv));
			s_data.add(npe.getRenice(sysEnv));
			s_data.add(npe.getIsSuspendedAsString(sysEnv));

			s_container.addData(sysEnv, s_data);
		}

		Collections.sort(s_container.dataset, s_container.getComparator(sysEnv, 1));

		data.add(s_container);

		d_container = new SDMSOutputContainer(sysEnv,
		                                      new SDMSMessage(sysEnv, "03408141539", "Nice Profile"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03408110922", "Nice Profile shown"));
	}
}

