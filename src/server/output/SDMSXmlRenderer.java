/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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

public class SDMSXmlRenderer extends SDMSOutputRenderer
{

	public final static String __version = "@(#) $Id: SDMSXmlRenderer.java,v 2.1.20.1 2013/03/14 10:24:19 ronald Exp $";

	public SDMSXmlRenderer ()
	{
		super();
	}

	public void render(SystemEnvironment env, SDMSOutput p_output) throws FatalException
	{
		render(env.cEnv.ostream(), p_output);
	}

	public void render(PrintStream ostream, SDMSOutput p_output) throws FatalException
	{
		writeln(ostream,"<OUTPUT>");
		if (p_output.container != null) {
			writeln(ostream,"<DATA>");
			renderContainer(ostream, p_output.container);
			writeln(ostream,"</DATA>");
		}
		if (p_output.feedback != null) {
			renderFeedback(ostream, p_output.feedback);
		}
		if (p_output.error != null) {
			renderError(ostream, p_output.error);
		}
		writeln(ostream,"</OUTPUT>");
		renderPrompt(ostream);
	}

	private void renderContainer(PrintStream ostream, SDMSOutputContainer p_container)
	{
		if (p_container.title != null) {
			writeln (ostream, "<TITLE>" +  p_container.title.toString() + "</TITLE>");
		}
		if (p_container.columns == 0) {
			renderRecord(ostream, p_container);
		} else {
			renderTable(ostream, p_container);
		}
	}

	private void renderRecord(PrintStream ostream, SDMSOutputContainer p_container)
	{
		int i;
		int s = p_container.desc.size();
		String name;

		Vector v = (Vector)(p_container.dataset.elementAt(0));

		writeln(ostream, "<RECORD>");
		for (i = 0; i < s; i++) {
			name = ((SDMSOutputLabel)(p_container.desc.elementAt(i))).name.toUpperCase();
			if(v.elementAt(i) == null) {
				write(ostream, "<" + name + " null=true>");
			} else {
				write(ostream, "<" + name  + ">");
				if (!(v.elementAt(i) instanceof SDMSOutputContainer)) {
					write(ostream, (v.elementAt(i)).toString());
				} else {
					writeln(ostream, "");

					renderTable (ostream, (SDMSOutputContainer)(v.elementAt(i)));
				}
			}
			write(ostream, "</" + name  + ">");
		}
		writeln(ostream, "</RECORD>");
	}

	private void renderTable(PrintStream ostream, SDMSOutputContainer p_container)
	{
		int i;
		int s = p_container.desc.size();
		String name;

		writeln(ostream, "<TABLE>");
		Iterator it = p_container.dataset.iterator();
		while(it.hasNext()) {
			writeln(ostream, "<RECORD>");
			Vector v = (Vector)(it.next());
			for (i = 0; i < s; i++) {
				name = ((SDMSOutputLabel)(p_container.desc.elementAt(i))).name.toUpperCase();
				write(ostream, "<" + name);
				if(v.elementAt(i) == null) {
					write(ostream, " null=true");
				}
				write(ostream, ">");
				if(v.elementAt(i) != null) {

					write(ostream, (v.elementAt(i)).toString());
				}
				writeln(ostream, "</" + name + ">");
			}
			writeln(ostream, "</RECORD>");
		}

		writeln(ostream, "</TABLE>");
	}

	private void renderFeedback(PrintStream ostream, Object p_feedback)
	{
		writeln (ostream, "<FEEDBACK>" +  p_feedback.toString() + "</FEEDBACK>");
	}

	private	void renderError(PrintStream ostream, SDMSOutputError p_error)
	{
		writeln (ostream, "<ERROR>");
		writeln (ostream, "<ERRORCODE>" + p_error.code + "</ERRORCODE>");
		writeln (ostream, "<ERRORMESSAGE>" + p_error.message + "</ERRORMESSAGE>");
		writeln (ostream, "</ERROR>");
	}

	private	void renderPrompt(PrintStream ostream)
	{
	}
}
