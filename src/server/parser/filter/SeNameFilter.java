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


package de.independit.scheduler.server.parser.filter;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class SeNameFilter extends Filter
{

	public final static String __version = "@(#) $Id: SeNameFilter.java,v 2.6.2.1 2013/03/14 10:25:16 ronald Exp $";

	String seName;
	Pattern pattern;

	public SeNameFilter(SystemEnvironment sysEnv, String s)
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
			if(c == '_') {
				sbt.append('.');
				continue;
			}
			if(c == '%') {
				sbt.append(".*");
				continue;
			}
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

		seName = new String(sbt);

		try {
			pattern = Pattern.compile(seName);
		} catch (PatternSyntaxException pse) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03310071126", "Error in regular expression"));
		}
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
	throws SDMSException
	{
		SDMSSubmittedEntity sme;
		SDMSSchedulingEntity se;
		SDMSFolder f;
		long version;
		String pathString = null;
		SeNameFilterCache seNameFilterCache;
		if (sysEnv.tx.txData.containsKey(SystemEnvironment.S_SE_NAMEFILTER_CACHE)) {
			seNameFilterCache = (SeNameFilterCache)(sysEnv.tx.txData.get(SystemEnvironment.S_SE_NAMEFILTER_CACHE));

			if (! seNameFilterCache.id.equals(p.getId(sysEnv))) {
				seNameFilterCache.id = p.getId(sysEnv);
				seNameFilterCache.name = null;
			} else
				pathString = seNameFilterCache.name;
		} else {
			seNameFilterCache = new SeNameFilterCache();
			seNameFilterCache.id = p.getId(sysEnv);
			sysEnv.tx.txData.put(SystemEnvironment.S_SE_NAMEFILTER_CACHE, seNameFilterCache);
		}
		try {
			if (pathString == null) {
				pathString = "";
				if (p instanceof SDMSSubmittedEntity) {
					sme = (SDMSSubmittedEntity) p;
					version = sme.getSeVersion(sysEnv).longValue();
					se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), version);
					pathString = se.pathString(sysEnv, version);
				} else if (p instanceof SDMSSchedulingEntity) {
					se = (SDMSSchedulingEntity) p;
					version = sysEnv.tx.versionId;
					pathString = se.pathString(sysEnv, version);
				} else if (p instanceof SDMSFolder) {
					f = (SDMSFolder) p;
					version = sysEnv.tx.versionId;
					pathString = f.pathString(sysEnv, version);
				} else if (p instanceof SDMSCalendar) {
					SDMSScheduledEvent scev = SDMSScheduledEventTable.getObject(sysEnv, ((SDMSCalendar)p).getScevId(sysEnv));
					SDMSEvent ev = SDMSEventTable.getObject(sysEnv, scev.getEvtId(sysEnv));
					se = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
					version = sysEnv.tx.versionId;
					pathString = se.pathString(sysEnv, version);
				} else if (p instanceof SDMSScheduledEvent) {
					SDMSEvent ev = SDMSEventTable.getObject(sysEnv, ((SDMSScheduledEvent)p).getEvtId(sysEnv));
					se = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
					version = sysEnv.tx.versionId;
					pathString = se.pathString(sysEnv, version);
				}
				seNameFilterCache.name = pathString;
			}
			Matcher m = pattern.matcher(pathString);
			return m.matches();
		} catch (Exception e) { }
		return false;
	}

	class SeNameFilterCache
	{
		protected Long id = null;
		protected String name = null;
	}
}

