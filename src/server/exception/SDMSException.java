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

import de.independit.scheduler.server.util.*;

public class SDMSException extends Exception
{

	public final static String __version = "@(#) $Id: SDMSException.java,v 2.2.18.1 2013/03/14 10:24:17 ronald Exp $";
	public static boolean debug = false;

	protected SDMSMessage m;

	public SDMSException()
	{
		super();
		if(debug)
			SDMSThread.doTrace(null, toString(), getStackTrace(), SDMSThread.SEVERITY_DEBUG);
		if (checkTrace()) printStackTrace();
	}

	public SDMSException(String msg)
	{
		super(msg);
		if(debug)
			SDMSThread.doTrace(null, msg, getStackTrace(), SDMSThread.SEVERITY_DEBUG);
		if (checkTrace()) printStackTrace();
	}

	public SDMSException(SDMSMessage msg)
	{
		m = msg;
		if(debug)
			SDMSThread.doTrace(null, msg.toString(), getStackTrace(), SDMSThread.SEVERITY_DEBUG);
		if (checkTrace()) printStackTrace();
	}

	private boolean checkTrace()
	{
		long exceptionNumber = getExceptionNumber();
		try {
			SDMSThread t = (SDMSThread) Thread.currentThread();
			if ((t.exceptionsToTrace & exceptionNumber) != 0) return true;

		} catch (ClassCastException cce) {

		}
		return false;
	}

	public static void toggleDebug()
	{
		debug = ! debug;
	}

	public static void debugOn()
	{
		debug = true;
	}

	public static void debugOff()
	{
		debug = false;
	}

	public String errNumber()
	{
		if(m != null)
			return m.errNumber();
		return "-1";
	}

	public String toString()
	{
		if(m != null)
			return m.toString();
		return super.toString();
	}

	public SDMSMessage toSDMSMessage()
	{
		return m;
	}

	public void setMessage(SDMSMessage m)
	{
		this.m = m;
	}

	public int getExceptionNumber() { return 2048; }
}

