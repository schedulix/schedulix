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

import java.util.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.exception.*;

public class SDMSPrivilege
{

	public final static String __version = "@(#) $Id: SDMSPrivilege.java,v 2.8.4.1 2013/03/14 10:25:22 ronald Exp $";

	public final static String S_CREATE_CONTENT	= "C";
	public final static String S_DROP		= "D";
	public final static String S_EDIT		= "E";
	public final static String S_GRANT		= "G";
	public final static String S_CREATE		= "K";
	public final static String S_MONITOR		= "M";
	public final static String S_OPERATE		= "O";
	public final static String S_CREATE_PARENT_CONTENT = "P";
	public final static String S_RESOURCE		= "R";
	public final static String S_SUBMIT		= "S";
	public final static String S_USE		= "U";
	public final static String S_VIEW		= "V";
	public final static String S_EXECUTE		= "X";

	public final static long CREATE			= 0x0000000000000001L;
	public final static long CREATE_CONTENT		= 0x0000000000000002L;
	public final static long CREATE_PARENT_CONTENT	= 0x0000000000000004L;
	public final static long RESOURCE		= 0x0000000000000008L;
	public final static long DROP			= 0x0000000000000010L;
	public final static long EDIT			= 0x0000000000000100L;
	public final static long MONITOR		= 0x0000000000001000L;
	public final static long OPERATE		= 0x0000000000010000L;
	public final static long SUBMIT			= 0x0000000000100000L;
	public final static long USE			= 0x0000000001000000L;
	public final static long VIEW			= 0x0000000010000000L;
	public final static long EXECUTE		= 0x0000000100000000L;
	public final static long GRANT			= 0x0000000200000000L;

	public final static long MANAGE_USER		= 0x0000000400000000L;
	public final static long MANAGE_GROUP		= 0x0000000800000000L;
	public final static long MANAGE_ESD		= 0x0000001000000000L;
	public final static long MANAGE_ESP		= 0x0000002000000000L;
	public final static long MANAGE_ESM		= 0x0000004000000000L;
	public final static long MANAGE_EST		= 0x0000008000000000L;
	public final static long MANAGE_RSD		= 0x0000010000000000L;
	public final static long MANAGE_RSP		= 0x0000020000000000L;
	public final static long MANAGE_RSM		= 0x0000040000000000L;
	public final static long MANAGE_FP		= 0x0000080000000000L;
	public final static long MANAGE_ENV		= 0x0000100000000000L;
	public final static long MANAGE_SYS		= 0x0000200000000000L;
	public final static long MANAGE_SEL		= 0x0000400000000000L;
	public final static long MANAGE_NP		= 0x0000800000000000L;

	private final static long VALID_BITS		= CREATE|CREATE_CONTENT|CREATE_PARENT_CONTENT|DROP|
							  EDIT|MONITOR|OPERATE|SUBMIT|USE|VIEW|GRANT|RESOURCE|
							  EXECUTE|MANAGE_USER|MANAGE_GROUP|MANAGE_ESD|MANAGE_ESP|
							  MANAGE_ESM|MANAGE_EST|MANAGE_RSD|MANAGE_RSP|MANAGE_RSM|
							  MANAGE_FP|MANAGE_ENV|MANAGE_SYS|MANAGE_SEL|MANAGE_NP;

	private final static long INVALID_BITS		= ~VALID_BITS;

	public final static long ALL			= CREATE|CREATE_CONTENT|DROP|RESOURCE|EXECUTE|
							  EDIT|MONITOR|OPERATE|SUBMIT|USE|VIEW|GRANT;

	public final static long MANAGE_ALL		= MANAGE_USER|MANAGE_GROUP|MANAGE_ESD|MANAGE_ESP|
							  MANAGE_ESM|MANAGE_EST|MANAGE_RSD|MANAGE_RSP|MANAGE_RSM|
							  MANAGE_FP|MANAGE_ENV|MANAGE_SYS|MANAGE_SEL|MANAGE_NP;

	public final static long MANAGE_PRIVS[] = 	{ MANAGE_USER, MANAGE_GROUP, MANAGE_ESD, MANAGE_ESP,
							  MANAGE_ESM, MANAGE_EST, MANAGE_RSD, MANAGE_RSP, MANAGE_RSM,
							  MANAGE_FP, MANAGE_ENV, MANAGE_SYS, MANAGE_SEL, MANAGE_NP
							};

	public final static long NOPRIVS		= 0x0000000000000000L;

	public final static Long SYSPRIVOBJID		= new Long(0);

	private long priv;

	private final static HashMap mapper = new HashMap();

	static {
		mapper.put(new Integer(Parser.VIEW),		new Long(SDMSPrivilege.VIEW));
		mapper.put(new Integer(Parser.USE),		new Long(SDMSPrivilege.USE));
		mapper.put(new Integer(Parser.EDIT),		new Long(SDMSPrivilege.EDIT));
		mapper.put(new Integer(Parser.DROP),		new Long(SDMSPrivilege.DROP));
		mapper.put(new Integer(Parser.SUBMIT),		new Long(SDMSPrivilege.SUBMIT));
		mapper.put(new Integer(Parser.CREATE),		new Long(SDMSPrivilege.CREATE_CONTENT));
		mapper.put(new Integer(Parser.MONITOR),		new Long(SDMSPrivilege.MONITOR));
		mapper.put(new Integer(Parser.OPERATE),		new Long(SDMSPrivilege.OPERATE));
		mapper.put(new Integer(Parser.RESOURCE),	new Long(SDMSPrivilege.RESOURCE));
		mapper.put(new Integer(Parser.EXECUTE),		new Long(SDMSPrivilege.EXECUTE));
	}

	public SDMSPrivilege()
	{
		priv = NOPRIVS;
	}

	public SDMSPrivilege(SystemEnvironment sysEnv, long p)
		throws SDMSException
	{
		if((p & INVALID_BITS) != 0) {

			throw new FatalException(new SDMSMessage(sysEnv, "03708061541", "Trying to use invalid privileges"));
		}
		priv = p;
	}

	public static Long map(Integer p)
	{
		return (Long) mapper.get(p);
	}

	public void addPriv(SystemEnvironment sysEnv, long p)
		throws SDMSException
	{
		if((p & INVALID_BITS) != 0)	{

			throw new FatalException(new SDMSMessage(sysEnv, "03402101453", "Trying to use invalid privileges"));
		}
		priv |= p;
	}

	public void addPriv(SystemEnvironment sysEnv, SDMSPrivilege p)
		throws SDMSException
	{
		priv |= p.priv;
	}

	public void delPriv(SystemEnvironment sysEnv, long p)
		throws SDMSException
	{
		if((p & INVALID_BITS) != 0) {

			throw new FatalException(new SDMSMessage(sysEnv, "03402101459", "Trying to use invalid privileges"));
		}
		priv &= ~p;
	}

	public void setPriv(SystemEnvironment sysEnv, long p)
		throws SDMSException
	{
		if((p & INVALID_BITS) != 0) {

			throw new FatalException(new SDMSMessage(sysEnv, "03402111239", "Trying to use invalid privileges"));
		}
		priv = p;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if((priv&CREATE)	== CREATE)			sb.append(S_CREATE);
		if((priv&CREATE_CONTENT)== CREATE_CONTENT)		sb.append(S_CREATE_CONTENT);
		if((priv&CREATE_PARENT_CONTENT)==CREATE_PARENT_CONTENT)	sb.append(S_CREATE_PARENT_CONTENT);
		if((priv&DROP)		== DROP)			sb.append(S_DROP);
		if((priv&EDIT)		== EDIT)			sb.append(S_EDIT);
		if((priv&MONITOR)	== MONITOR)			sb.append(S_MONITOR);
		if((priv&OPERATE)	== OPERATE)			sb.append(S_OPERATE);
		if((priv&SUBMIT)	== SUBMIT)			sb.append(S_SUBMIT);
		if((priv&USE)		== USE)				sb.append(S_USE);
		if((priv&VIEW)		== VIEW)			sb.append(S_VIEW);
		if((priv&GRANT)		== GRANT)			sb.append(S_GRANT);
		if((priv&RESOURCE)	== RESOURCE)			sb.append(S_RESOURCE);
		if((priv&EXECUTE)	== EXECUTE)			sb.append(S_EXECUTE);

		return new String(sb);
	}

	public String[] asString()
	{
		final Vector v = new Vector();
		if((priv&CREATE)	== CREATE)			v.add("CREATE");
		if((priv&CREATE_CONTENT)== CREATE_CONTENT)		v.add("CREATE CONTENT");
		if((priv&CREATE_PARENT_CONTENT)==CREATE_PARENT_CONTENT)	v.add("CREATE PARENT CONTENT");
		if((priv&DROP)		== DROP)			v.add("DROP");
		if((priv&EDIT)		== EDIT)			v.add("EDIT");
		if((priv&MONITOR)	== MONITOR)			v.add("MONITOR");
		if((priv&OPERATE)	== OPERATE)			v.add("OPERATE");
		if((priv&SUBMIT)	== SUBMIT)			v.add("SUBMIT");
		if((priv&USE)		== USE)				v.add("USE");
		if((priv&VIEW)		== VIEW)			v.add("VIEW");

		if((priv&RESOURCE)	== RESOURCE)			v.add("RESOURCE");
		if((priv&EXECUTE)	== EXECUTE)			v.add("EXECUTE");
		if((priv&MANAGE_USER)	== MANAGE_USER)			v.add("MANAGE USER");
		if((priv&MANAGE_GROUP)	== MANAGE_GROUP)		v.add("MANAGE GROUP");
		if((priv&MANAGE_ESD)	== MANAGE_ESD)			v.add("MANAGE EXIT STATE DEFINITION");
		if((priv&MANAGE_ESP)	== MANAGE_ESP)			v.add("MANAGE EXIT STATE PROFILE");
		if((priv&MANAGE_ESM)	== MANAGE_ESM)			v.add("MANAGE EXIT STATE MAPPING");
		if((priv&MANAGE_EST)	== MANAGE_EST)			v.add("MANAGE EXIT STATE DEFINITION");
		if((priv&MANAGE_RSD)	== MANAGE_RSD)			v.add("MANAGE RESOURCE STATE DEFINITION");
		if((priv&MANAGE_RSP)	== MANAGE_RSP)			v.add("MANAGE RESOURCE STATE PROFILE");
		if((priv&MANAGE_RSM)	== MANAGE_RSM)			v.add("MANAGE RESOURCE STATE MAPPING");
		if((priv&MANAGE_FP)	== MANAGE_FP)			v.add("MANAGE FOOTPRINT");
		if((priv&MANAGE_ENV)	== MANAGE_ENV)			v.add("MANAGE ENVIRONMENT");
		if((priv&MANAGE_SYS)	== MANAGE_SYS)			v.add("MANAGE SYSTEM");
		if((priv&MANAGE_SEL)	== MANAGE_SEL)			v.add("MANAGE SELECT");
		if((priv&MANAGE_NP)	== MANAGE_NP)			v.add("MANAGE NICE PROFILE");

		return (String[]) v.toArray (new String [v.size()]);
	}

	public Long toLong()
	{
		return new Long(priv);
	}

	public void merge(SDMSPrivilege p)
	{
		priv &= p.priv;
	}

	public void merge(long p)
	{
		priv &= p;
	}

	public boolean can(long p)
	{
		return ((priv&p)==p);
	}

	public boolean isEmpty()
	{
		return (priv == NOPRIVS);
	}
}
