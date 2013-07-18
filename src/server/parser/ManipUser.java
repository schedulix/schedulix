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
import de.independit.scheduler.server.util.*;

public abstract class ManipUser extends Node
{

	public final static String __version = "@(#) $Id";

	protected String user;
	protected ObjectURL url;
	protected String passwd;
	protected String txtPasswd;
	protected Boolean enable;
	protected Long defaultGId;
	protected Vector grouplist;
	protected Vector addlist;
	protected Vector dellist;
	protected WithHash with;
	private boolean withEvaluated = false;
	protected Long publicGId;

	protected final static Long ZERO = new Long(0L);

	public ManipUser(ObjectURL u, WithHash w)
	{
		super();
		with = w;
		url = u;
		user = null;
		grouplist = new Vector();
		addlist = new Vector();
		dellist = new Vector();
	}

	public ManipUser(String u, WithHash w)
	{
		super();
		with = w;
		user = u;
		url = null;
		grouplist = new Vector();
		addlist = new Vector();
		dellist = new Vector();
	}

	public abstract void go(SystemEnvironment sysEnv)
	throws SDMSException;

	protected void evaluate_with(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSGroup g;

		g = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey(SDMSGroup.PUBLIC, new Long(0)));
		publicGId = g.getId(sysEnv);

		if(withEvaluated) return;

		passwd = null;
		if (with.containsKey (ParseStr.S_PASSWORD)) {
			txtPasswd = (String) with.get(ParseStr.S_PASSWORD);
			passwd = CheckSum.mkstr(CheckSum.md5(txtPasswd.getBytes()));
		}
		if (with.containsKey (ParseStr.S_RAWPASSWORD))
			if (passwd == null)
				passwd = (String) with.get (ParseStr.S_RAWPASSWORD);
			else
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04312181625", "Both " + ParseStr.S_PASSWORD + " and " + ParseStr.S_RAWPASSWORD + " are not allowed"));

		if(!with.containsKey(ParseStr.S_ENABLE))
			enable = Boolean.TRUE;
		else
			enable = (Boolean) with.get(ParseStr.S_ENABLE);

		if(with.containsKey(ParseStr.S_DEFAULTGROUP)) {
			g = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) with.get(ParseStr.S_DEFAULTGROUP), new Long(0)));
			defaultGId = g.getId(sysEnv);
		} else {
			defaultGId = null;
		}

		if(with.containsKey(ParseStr.S_GROUPLIST)) {
			if(defaultGId != null) grouplist.add(defaultGId);
			Vector v = (Vector) with.get(ParseStr.S_GROUPLIST);
			Long gId;
			for(int i = 0; i < v.size(); i++) {
				gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) v.get(i), new Long(0))).getId(sysEnv);
				if(!gId.equals(defaultGId))
					grouplist.add(gId);
			}
		}

		if(with.containsKey(ParseStr.S_ADDGROUP)) {
			Vector v = (Vector) with.get(ParseStr.S_ADDGROUP);
			Long gId;
			for(int i = 0; i < v.size(); i++) {
				gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) v.get(i), new Long(0))).getId(sysEnv);
				addlist.add(gId);
			}
		}

		if(with.containsKey(ParseStr.S_DELGROUP)) {
			Vector v = (Vector) with.get(ParseStr.S_DELGROUP);
			Long gId;
			for(int i = 0; i < v.size(); i++) {
				gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) v.get(i), new Long(0))).getId(sysEnv);
				dellist.add(gId);
			}
		}

		withEvaluated = true;
	}
}

