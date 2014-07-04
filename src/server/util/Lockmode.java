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

package de.independit.scheduler.server.util;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class Lockmode
{

	public final static String __version = "@(#) $Id: Lockmode.java,v 2.2.18.1 2013/03/14 10:25:29 ronald Exp $";

	private int mode;

	public static final String Nolock      = "N";
	public static final String Shared      = "S";
	public static final String SharedExcl  = "SX";
	public static final String SharedComp  = "SC";
	public static final String Exclusive   = "X";

	public static final int X   = 0x00;
	public static final int SX  = 0x02;
	public static final int S   = 0x04;
	public static final int SC  = 0x06;
	public static final int N   = 0xFF;

	public Lockmode() { mode = N; }
	public Lockmode(SystemEnvironment env, int m) throws CommonErrorException
	{
		if (m != X    &&
		    m != SX   &&
		    m != S    &&
		    m != SC   &&
		    m != N) {
			throw new CommonErrorException(new SDMSMessage(env, "03110181507", "Invalid Lockmode $1", new Integer(m)));
		}
		mode = m;
	}
	public Lockmode(int m)
	{
		mode = N;
		if(m == X || m == SX || m == S || m == SC || m == N) mode = m;
	}
	public int getLockmode() { return mode; }
	public int setLockmode(int m)
	{
		int rc = mode;
		if(m == X || m == SX || m == S || m == SC || m == N) mode = m;
		return rc;
	}
	public int setLockmode(Lockmode l)
	{
		int rc = mode;
		mode = l.mode;
		return rc;
	}
	public int setLockmode(SystemEnvironment env, int m) throws CommonErrorException
	{
		int rc = mode;
		if(m == X || m == SX || m == S || m == SC || m == N) mode = m;
		else {
			throw new CommonErrorException(new SDMSMessage(env, "03308061033", "Invalid Lockmode $1", new Integer(m)));
		}
		return rc;
	}
	public String str(SystemEnvironment env) throws FatalException
	{
		switch(mode) {
			case X:		return Exclusive;
			case SX:	return SharedExcl;
			case S:		return Shared;
			case SC:	return SharedComp;
			case N:		return Nolock;
			default:	throw new FatalException(
						new SDMSMessage(env, "03110181508", "Invalid internal State $1", new Integer(mode)));
		}
	}
	public static boolean isCompatible(Lockmode l1, Lockmode l2)
	{
		return ((l1.mode|0x80)&(l2.mode|0x40))>0 ? true : false;
	}
	public static boolean isCompatible(Integer l1, Lockmode l2)
	{
		return ((l1.intValue()|0x80)&(l2.mode|0x40))>0 ? true : false;
	}
	public static boolean isCompatible(Lockmode l1, Integer l2)
	{
		return ((l1.mode|0x80)&(l2.intValue()|0x40))>0 ? true : false;
	}
	public static boolean isCompatible(Integer l1, Integer l2)
	{
		return ((l1.intValue()|0x80)&(l2.intValue()|0x40))>0 ? true : false;
	}
}

