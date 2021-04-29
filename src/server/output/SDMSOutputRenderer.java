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
package de.independit.scheduler.server.output;

import java.lang.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public abstract class SDMSOutputRenderer
{
	public static final String __version = "@(#) $Id: SDMSOutputRenderer.java,v 2.2.4.1 2013/03/14 10:24:18 ronald Exp $";

	protected boolean silent = false;
	protected boolean verbose = true;

	public SDMSOutputRenderer ()
	{
	}

	public void setSilent(boolean silent)
	{
		this.silent = silent;
	}
	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}

	public abstract void render(SystemEnvironment env, SDMSOutput p_output) throws FatalException;
	public abstract void render(PrintStream ostream, SDMSOutput p_output) throws FatalException;

	protected void write(PrintStream ostream, String s)
	{
		ostream.print(s);
	}

	protected void writeln(PrintStream ostream, String s)
	{
		ostream.println(s);
	}

}
