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

public class SDMSPerlRenderer extends SDMSScriptRenderer
{

	public final static String __version = "@(#) $Id: SDMSPerlRenderer.java,v 2.1.20.1 2013/03/14 10:24:18 ronald Exp $";

	public SDMSPerlRenderer ()
	{
		super();
	}

	public void render(SystemEnvironment env, SDMSOutput p_output) throws FatalException
	{
		render(env.cEnv.ostream(), p_output);
	}

	public void render(PrintStream ostream, SDMSOutput p_output) throws FatalException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		boolean first = true;
		if (p_output.container != null) {
			sb.append("'DATA' => \n");
			renderContainer(sb, p_output.container);
			first = false;
		}
		if (p_output.feedback != null) {
			if (!first) {
				sb.append( ",");
			}
			first = false;
			renderFeedback(sb, p_output.feedback);
		}
		if (p_output.error != null) {
			if (!first) {
				sb.append( ",");
			}
			renderError(sb, p_output.error);
		}
		sb.append("}\n");

		ostream.print(sb);

	}

	private void renderContainer(StringBuffer sb, SDMSOutputContainer p_container)
	{
		sb.append("{\n");
		if (p_container.title != null) {
			sb.append("'TITLE' => '" +  maskQuotes(p_container.title.toString()) + "',\n");
		}
		sb.append("'DESC' => [\n");
		boolean first = true;
		Iterator i = p_container.desc.iterator();
		while (i.hasNext()) {
			if (!first) {
				sb.append(",\n");
			}
			first = false;
			SDMSOutputLabel l = (SDMSOutputLabel)i.next();
			sb.append("'" + maskQuotes(l.name.toUpperCase()) + "'\n");
		}
		sb.append("],\n");
		if (p_container.columns == 0) {
			sb.append("'RECORD' => ");
			renderRecord(sb, p_container);
		} else {
			sb.append("'TABLE' => ");
			renderTable(sb, p_container);
		}
		sb.append("}\n");
	}

	private void renderRecord(StringBuffer sb, SDMSOutputContainer p_container)
	{
		int i;
		int s = p_container.desc.size();
		String name;
		boolean first = true;

		Vector v = (Vector)(p_container.dataset.elementAt(0));

		sb.append("{\n");
		for (i = 0; i < s; i++) {
			name = ((SDMSOutputLabel)(p_container.desc.elementAt(i))).name.toUpperCase();
			if(v.elementAt(i) != null) {
				if (!first) {
					sb.append(",\n");
				}
				first = false;
				sb.append("'" + maskQuotes(name)  + "' => ");
				if (!(v.elementAt(i) instanceof SDMSOutputContainer)) {
					sb.append("'" + maskQuotes((v.elementAt(i)).toString()) + "'");
				} else {
					sb.append("\n");
					renderContainer (sb, (SDMSOutputContainer)(v.elementAt(i)));
				}
			}
		}
		sb.append("}\n");
	}

	private void renderTable(StringBuffer sb, SDMSOutputContainer p_container)
	{
		int i;
		int s = p_container.desc.size();
		String name;
		boolean first = true;

		sb.append( "[\n");
		Iterator it = p_container.dataset.iterator();
		while(it.hasNext()) {
			if (!first) {
				sb.append(",\n");
			}
			first = false;
			sb.append( "{\n");
			Vector v = (Vector)(it.next());
			boolean rfirst = true;
			for (i = 0; i < s; i++) {
				if(v.elementAt(i) == null) {
					continue;
				}
				if (!rfirst) {
					sb.append(",\n");
				}
				rfirst = false;
				name = ((SDMSOutputLabel)(p_container.desc.elementAt(i))).name.toUpperCase();
				sb.append("'" + maskQuotes(name) + "' => '");
				sb.append( maskQuotes((v.elementAt(i)).toString()));
				sb.append("'");
			}
			sb.append("}\n");
		}
		sb.append("]\n");
	}

	private void renderFeedback(StringBuffer sb, Object p_feedback)
	{
		sb.append("'FEEDBACK' => '" +  maskQuotes(p_feedback.toString()) + "'\n");
	}

	private	void renderError(StringBuffer sb, SDMSOutputError p_error)
	{
		sb.append("'ERROR' => {\n");
		sb.append("'ERRORCODE' => '" + maskQuotes(p_error.code.toString()) + "', \n");
		sb.append("'ERRORMESSAGE' => '");
		sb.append(maskQuotes(p_error.message.toString()));
		sb.append("'\n");
		sb.append("}\n");
	}

	private	void renderPrompt(StringBuffer sb)
	{
	}

}
