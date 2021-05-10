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
	protected String salt;
	protected Integer method;
	protected String txtPasswd;
	protected Boolean enable;
	protected Long defaultGId;
	protected Vector grouplist;
	protected Vector addlist;
	protected Vector dellist;
	protected WithHash with;
	private boolean withEvaluated = false;
	protected Long publicGId;
	protected Integer connect_type;
	protected Vector userEquiv = null;

	protected final static Long ZERO = new Long(0L);
	protected final static int MD5LENGTH = 35;

	public ManipUser(ObjectURL u, WithHash w)
	{
		super();
		with = w;
		url = u;
		user = null;
		grouplist = new Vector();
		addlist = new Vector();
		dellist = new Vector();
		method = new Integer(SDMSUser.SHA256);
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
		method = new Integer(SDMSUser.SHA256);
	}

	public abstract void go(SystemEnvironment sysEnv)
		throws SDMSException;

	public static String generateSalt()
	{
		StringBuffer salt = new StringBuffer();
		Random r = new Random(System.currentTimeMillis());

		for (int i = 0; i < SDMSUser.SALT_LENGTH; ++i) {
			char c = (char) (r.nextInt(96) + 32);
			salt.append(c);
		}

		return salt.toString();
	}

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
			if (method.intValue() == SDMSUser.MD5)
				passwd = CheckSum.mkstr(CheckSum.md5((txtPasswd + salt).getBytes()), true);
			else
				passwd = CheckSum.mkstr(CheckSum.sha256((txtPasswd + salt).getBytes()), false);
			with.remove(ParseStr.S_PASSWORD);
		}
		if (with.containsKey (ParseStr.S_RAWPASSWORD)) {
			if (passwd == null) {
				Vector v = (Vector) with.get (ParseStr.S_RAWPASSWORD);
				passwd = (String) v.get(0);
				salt = (String) v.get(1);

				if (passwd.length() == MD5LENGTH)
					method = new Integer(SDMSUser.MD5);
			} else
				throw new CommonErrorException (new SDMSMessage (sysEnv, "04312181625", "Both " + ParseStr.S_PASSWORD + " and " + ParseStr.S_RAWPASSWORD + " are not allowed"));
			with.remove(ParseStr.S_RAWPASSWORD);
		}

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

		if(defaultGId != null) grouplist.add(defaultGId);
		if(with.containsKey(ParseStr.S_GROUPLIST)) {
			Vector v = (Vector) with.get(ParseStr.S_GROUPLIST);
			Long gId;
			for(int i = 0; i < v.size(); i++) {
				gId = SDMSGroupTable.idx_name_deleteVersion_getUnique(sysEnv, new SDMSKey((String) v.get(i), new Long(0))).getId(sysEnv);
				if(!gId.equals(defaultGId))
					grouplist.add(gId);
			}
		}

		if (with.containsKey(ParseStr.S_CONNECT)) {
			connect_type = (Integer) with.get(ParseStr.S_CONNECT);
		} else {
			connect_type = new Integer(SDMSUser.PLAIN);
		}

		if (with.containsKey(ParseStr.S_EQUIVALENT)) {
			userEquiv = (Vector) with.get(ParseStr.S_EQUIVALENT);
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

	public void delParameters(SystemEnvironment sysEnv, SDMSUser u, Vector arg, boolean ignoreNotFound)
	throws SDMSException
	{
		int i;
		Long uId = u.getId(sysEnv);

		try {
			for(i = 0; i < arg.size(); i++) {
				String n = (String) arg.get(i);
				SDMSUserParameter up = SDMSUserParameterTable.idx_uId_Name_getUnique(sysEnv, new SDMSKey(uId, n));
				up.delete(sysEnv);
			}
		} catch(NotFoundException nfe) {
			if(!ignoreNotFound) throw nfe;
		}
	}

	public void createParameters(SystemEnvironment sysEnv, WithHash wh, SDMSUser u)
	throws SDMSException
	{

		Vector v = SDMSUserParameterTable.idx_uId.getVector(sysEnv, u.getId(sysEnv));
		for (int i = 0; i < v.size(); ++i) {
			SDMSUserParameter up = (SDMSUserParameter) v.get(i);
			up.delete(sysEnv);
		}

		addOrAlterParameters(sysEnv, wh, u, true, false);
	}

	public void addOrAlterParameters(SystemEnvironment sysEnv, WithHash wh, SDMSUser u, boolean isAdd, boolean processError)
	throws SDMSException
	{
		String lpn;
		Long uId = u.getId(sysEnv);

		if (wh == null) return;
		Set s = wh.keySet();
		Iterator i = s.iterator();
		while(i.hasNext()) {
			String pn = (String) i.next();
			String pv = (String) wh.get(pn);
			if(isAdd) {
				try {
					SDMSUserParameterTable.table.create(sysEnv, u.getId(sysEnv), pn, pv);
					System.out.println("Createing parameter " + pn + " with value '" + pv + "'");
				} catch(DuplicateKeyException dke) {
					if(processError) {
						SDMSUserParameter up =
						        SDMSUserParameterTable.idx_uId_Name_getUnique(sysEnv, new SDMSKey(uId, pn));
						up.setValue(sysEnv, pv);
					} else {
						throw dke;
					}
				}
			} else {
				try {
					SDMSUserParameter up =
					        SDMSUserParameterTable.idx_uId_Name_getUnique(sysEnv, new SDMSKey(uId, pn));
					up.setValue(sysEnv, pv);
				} catch (NotFoundException nfe) {
					if(processError) return;
					throw nfe;
				}
			}
		}
	}

}

