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

package de.independit.scheduler.server.exception;

import java.lang.*;
import java.io.*;
import java.util.*;
import de.independit.scheduler.server.*;

public class SDMSMessage
{

	public final static String __version = "@(#) $Id: SDMSMessage.java,v 2.2.18.1 2013/03/14 10:24:17 ronald Exp $";

	private String errId;
	private String msg;
	private Object o[];
	private SystemEnvironment env;
	private static boolean translationEnabled = true;

	private static HashMap trans1 = null;

	private static HashMap trans2 = null;

	private void initTranslationTables(SystemEnvironment p_env)
	{
		trans1 = new HashMap();
		trans2 = new HashMap();

		try {
			InputStream is = SDMSMessage.class.getResourceAsStream("/server/messages/MessageTable1.dat");

			Properties prop = new Properties();
			if(is != null) {
				prop.load(is);
			}
		} catch (IOException ioe) {

			translationEnabled = false;
		}
	}

	public SDMSMessage(SystemEnvironment p_env, String p_errId, String p_msg, Object p[])
	{
		env = p_env;
		errId = p_errId;
		msg = p_msg;
		o = p;
		if(trans1 == null) {
			initTranslationTables(p_env);
		}
	}

	public SDMSMessage(SystemEnvironment p_env, String p_errId, String p_msg)
	{
		this(p_env, p_errId, p_msg, (Object[]) null);
	}

	public SDMSMessage(SystemEnvironment p_env, String p_errId, String p_msg, Object p1)
	{
		this(p_env, p_errId, p_msg, new Object[] {p1});
	}

	public SDMSMessage(SystemEnvironment p_env, String p_errId, String p_msg, Object p1, Object p2)
	{
		this(p_env, p_errId, p_msg, new Object[] {p1, p2});
	}

	public SDMSMessage(SystemEnvironment p_env, String p_errId, String p_msg, Object p1, Object p2, Object p3)
	{
		this(p_env, p_errId, p_msg, new Object[] {p1, p2, p3});
	}

	private String subst(String p_msg)
	{
		if(p_msg == null) return null;
		int lgth = p_msg.length();
		StringBuffer r = new StringBuffer(lgth + (o == null ? 0 : o.length*16));

		boolean ind[] = new boolean[o == null ? 0 : o.length];

		int i, j;

		i = 0;
		j = 0;

		Arrays.fill(ind, false);

		for(; i<lgth; i++) {
			if(p_msg.charAt(i) != '$') continue;

			if(i+1 == lgth) break;
			if(!Character.isDigit(p_msg.charAt(i+1))) continue;
			r.append(p_msg.substring(j,i));

			i++;
			j = i;
			while((i < lgth) && Character.isDigit(p_msg.charAt(i))) i++;

			int pnr = Integer.valueOf(p_msg.substring(j,i)).intValue();
			if(pnr > o.length || pnr <= 0) {
				r.append(SystemEnvironment.noneString);
			} else {
				r.append(o[pnr-1] != null ? o[pnr-1].toString() : SystemEnvironment.nullString);
				ind[pnr-1] = true;
			}
			j = i;

		}
		if(j<lgth)
			r.append(p_msg.substring(j,i));

		boolean first = true;
		for(i=0; i<ind.length; i++) {
			if(!ind[i]) {
				if(first) {
					first = false;
					r.append(" [");
				}
				r.append(o[i].toString());
			}
		}
		if(!first) {
			r.append("]");
		}

		return new String(r);
	}

	public String toString()
	{

		if(!translationEnabled) {
			return (msg == null ? errId : subst(msg));
		}

		return (msg == null ? errId : subst(msg));
	}

	public String errNumber()
	{
		return errId;
	}

	public String getMessage()
	{
		return msg;
	}

	public void setMessage(String s)
	{
		msg = s;
	}
}

