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
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSLock
{

	public final static String __version = "@(#) $Id: SDMSLock.java,v 2.1.18.1 2013/03/14 10:25:20 ronald Exp $";

	private int mode;

	public static final String Nolock      = "N";
	public static final String Shared      = "S";
	public static final String Exclusive   = "X";
	public static final String Optimistic  = "IX";

	public static final int X   = 0x00;
	public static final int S   = 0x04;
	public static final int O   = 0x05;
	public static final int N   = 0xFF;

	public SDMSLock()
	{
		mode = N;
	}
	protected SDMSLock(int m)
	{
		mode = m;
	}
	public SDMSLock(SystemEnvironment env, int m) throws SDMSException
	{
		if (m != X    &&
		    m != S    &&
		    m != O    &&
		    m != N) {
			throw new FatalException(new SDMSMessage(env, "03110181534",
			                         "Invalid Lockmode $1", Integer.valueOf(m)));
		}
		mode = m;
	}
	public int getLockmode()
	{	
		return mode;
	}
}

