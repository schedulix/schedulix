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


package de.independit.scheduler.server.parser.filter;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class NrNameFilter extends Filter
{
	String nrName;
	Pattern p;

	public NrNameFilter(SystemEnvironment sysEnv, String s)
		throws SDMSException
	{
		super();

		int i,j;
		char c, ct;

		StringBuffer sbt, sbf;
		sbf = new StringBuffer(s);
		sbt = new StringBuffer();
		for(i = 0; i < sbf.length(); i++) {
			c = sbf.charAt(i);
			if(c == '_') { sbt.append('.'); continue; }
			if(c == '%') { sbt.append(".*"); continue; }
			if(c == '\\') {
				j = i+1;
				if(j < sbf.length()) {
					ct = sbf.charAt(j);
					if(ct == '_' || ct == '%') {
						i=j;
						c = ct;
					}
				}
			}
			sbt.append(c);
		}

		nrName = new String(sbt);

		try {
			p = Pattern.compile(nrName);
		} catch (PatternSyntaxException pse) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03402051228", "Error in regular expression"));
		}
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy prox)
		throws SDMSException
	{
		try {
			SDMSNamedResource nr = (SDMSNamedResource) prox;
			Matcher m = p.matcher(nr.pathString(sysEnv));
			return m.matches();
		} catch (Exception e) { }
		return false;
	}

	public boolean equals(Object o)
	{
		if (o == this) return true;
		if (!(o instanceof NrNameFilter)) return false;
		NrNameFilter f;
		f = (NrNameFilter) o;
		return nrName.equals(f.nrName);
	}
}

