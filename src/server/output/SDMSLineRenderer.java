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

public class SDMSLineRenderer extends SDMSOutputRenderer
{

	public final static String __version = "@(#) $Id: SDMSLineRenderer.java,v 2.6.2.1 2013/03/14 10:24:17 ronald Exp $";

	String prompt = null;

	public SDMSLineRenderer ()
	{
		super();
	}

	public SDMSLineRenderer (String p)
	{
		super();
		prompt = p;
	}

	public void setPrompt(String p)
	{
		prompt = p;
	}

	public String getPrompt()
	{
		return prompt;
	}

	public void render(SystemEnvironment env, SDMSOutput p_output) throws FatalException
	{
		render(env.cEnv.ostream(), p_output);
	}

	public void render(PrintStream ostream, SDMSOutput p_output) throws FatalException
	{
		if (p_output.container != null) {
			renderContainer(ostream, p_output.container);
		}
		if ((p_output.feedback != null) && !silent) {
			renderFeedback(ostream, p_output.feedback);
		}
		if ((p_output.error != null) && !silent) {
			renderError(ostream, p_output.error);
		}
		if (!silent)
			renderPrompt(ostream);
	}

	private void renderContainer(PrintStream ostream, SDMSOutputContainer p_container)
	{
		if ((p_container.title != null) && !silent) {
			writeln (ostream, "");
			writeln (ostream, p_container.title.toString());
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
		final int s = p_container.desc.size();
		final int labelwidth = p_container.labelwidth;

		Vector v = (Vector)(p_container.dataset.elementAt(0));

		writeln (ostream, "");
		for (i = 0; i < s; i++) {
			write (ostream, pad (((SDMSOutputLabel)(p_container.desc.elementAt(i))).name, -labelwidth, " ") + " : ");
			if(v.elementAt(i) == null) {
				writeln (ostream, "<null>");
				continue;
			}
			if (!(v.elementAt(i) instanceof SDMSOutputContainer)) {
				writeln (ostream, (v.elementAt(i)).toString());
			} else {
				writeln(ostream, "");
				renderTable (ostream, (SDMSOutputContainer)(v.elementAt(i)));
			}
		}
	}

	private void renderTable(PrintStream ostream, SDMSOutputContainer p_container)
	{
		int i;
		final int s = p_container.desc.size();

		writeln (ostream, "");
		for (i = 0; i < s; i++) {
			if (i > 0) write(ostream, " ");
			write (ostream, pad (((SDMSOutputLabel)(p_container.desc.elementAt(i))).name,
					     ((SDMSOutputLabel)(p_container.desc.elementAt(i))).length, " "));
		}
		writeln(ostream, "");
		for (i = 0; i < s; i++) {
			if (i > 0) write(ostream, " ");
			write (ostream, pad ("", ((SDMSOutputLabel)(p_container.desc.elementAt(i))).length, "-"));
		}
		writeln(ostream, "");
		Iterator it = p_container.dataset.iterator();
		while(it.hasNext()) {
			Vector v = (Vector)(it.next());
			for (i = 0; i < s; i++) {
				if (i > 0) write(ostream, " ");
				write (ostream, renderCell(v.elementAt(i), ((SDMSOutputLabel)(p_container.desc.elementAt(i))).length));
			}
			writeln(ostream, "");
		}
		writeln(ostream, "");
	}

	private String renderCell(Object o, int len)
	{
		if(o != null) {
			try {
				if((Number) o != null)
					len = -len;
			} catch (ClassCastException cce) {

			}
			return pad (o.toString(), len, " ");
		}
		return pad("<null>", len, " ");
	}

	private String pad (String p_str, int length, String p_pad)
	{
		String str = p_str;
		int len = str.length();
		int abslen = length < 0 ? -length : length;
		int s;
		for (s = 0; s < abslen - len; s ++) {
			if (length > 0) {
				str = str.concat (p_pad);
			} else {
				str = p_pad.concat (str);
			}
		}
		return str;
	}

	private void renderFeedback(PrintStream ostream, Object p_feedback)
	{
		writeln (ostream, "");
		writeln (ostream, p_feedback.toString());
	}

	private	void renderError(PrintStream ostream, SDMSOutputError p_error)
	{
		writeln (ostream, "");
		writeln (ostream, "ERROR:" + p_error.code + ", " + p_error.message);
	}

	private	void renderPrompt(PrintStream ostream)
	{
		writeln (ostream, "");
		write (ostream, prompt == null ? "SDMS> " : prompt);
	}
}
