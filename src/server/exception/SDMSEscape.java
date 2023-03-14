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

public class SDMSEscape extends RecoverableException
{

	public final static String __version = "@(#) $Id: SDMSEscape.java,v 2.0.20.1 2013/03/14 10:24:17 ronald Exp $";

	public SDMSEscape()
	{
		super();
	}

	public SDMSEscape(String msg)
	{
		super(msg);
	}

	public SDMSEscape(SDMSMessage msg)
	{
		super(msg);
	}

	public int getExceptionNumber()
	{
		return 1024;
	}
}

