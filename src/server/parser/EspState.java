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

public class EspState
{

	public final static String __version = "@(#) $Id: EspState.java,v 2.2.2.1 2013/03/14 10:24:32 ronald Exp $";

	public String name;
	public Integer type;
	public Boolean unreachable;
	public Boolean broken;
	public Boolean batchDefault;
	public Boolean depDefault;

	public EspState(String n, Integer t, WithHash w)
	{
		name = n;
		type = t;
		unreachable = w.containsKey(ParseStr.S_UNREACHABLE) ? Boolean.TRUE : Boolean.FALSE;
		broken =  w.containsKey(ParseStr.S_BROKEN) ? Boolean.TRUE : Boolean.FALSE;
		batchDefault =  w.containsKey(ParseStr.S_BATCH) ? Boolean.TRUE : Boolean.FALSE;
		depDefault =  w.containsKey(ParseStr.S_DEPENDENCY) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String toString()
	{
		return "EspState: " + name + " type " + type + " unreachable " + unreachable + " broken " + broken +
		       " batch default " + batchDefault + " dependency default " + depDefault;
	}
}

