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

public class SDMSSerialRenderer extends SDMSOutputRenderer
{

	public final static String __version = "@(#) $Id: SDMSSerialRenderer.java,v 2.1.20.1 2013/03/14 10:24:19 ronald Exp $";

	String errorCode;
	String errorMessage;

	public SDMSSerialRenderer ()
	{
		super();
	}

	public void render(SystemEnvironment env, SDMSOutput p_output) throws FatalException
	{
		try {
			render(env.cEnv.ostream(), p_output);
		} catch (FatalException fe) {
			throw new FatalException (new SDMSMessage (env, errorCode, errorMessage));
		}
	}

	public void render(PrintStream ostream, SDMSOutput p_output) throws FatalException
	{
		try {

			if (p_output.container != null) {
				cleanContainer(p_output.container);
			}

			if (p_output.feedback != null) {
				p_output.feedback = p_output.feedback.toString();
			}

			ObjectOutputStream objectOutputStream = new ObjectOutputStream (ostream);
			objectOutputStream.writeObject(p_output);

			objectOutputStream.flush();
		} catch (Exception e) {
			errorCode = "02111032117";
			errorMessage = "Error writing object to ObjectOutputStream, " + e.toString();
			throw new FatalException ("Error writing object to ObjectOutputStream");
		}
	}

	private void cleanContainer(SDMSOutputContainer p_container)
	{
		if (p_container.title != null) {
			p_container.title = p_container.title.toString();
		}

		if (p_container.columns == 0) {

			Vector v = (Vector)(p_container.dataset.elementAt(0));
			Iterator i = v.iterator();
			while (i.hasNext()) {
				Object o = i.next();
				if ( o != null) {
					if (o instanceof SDMSOutputContainer) {
						cleanContainer((SDMSOutputContainer)o);
					}
				}
			}
		}
	}
}
