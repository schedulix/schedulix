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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;

public class ParameterFilter extends Filter
{

	public final static String __version = "@(#) $Id: ParameterFilter.java,v 2.6.2.1 2013/03/14 10:25:15 ronald Exp $";

	String name;
	Comparable value;
	String cmpop;
	Comparer c;
	Caster cst;

	public ParameterFilter(SystemEnvironment sysEnv, WithHash w)
	throws SDMSException
	{
		super();
		name = (String) w.get(ParseStr.S_NAME);
		value = (Comparable) w.get(ParseStr.S_VALUE);
		cmpop = (String) w.get(ParseStr.S_CMPOP);

		cst = null;

		if(cmpop.equals("=="))				{
			c = new EQComparer(sysEnv, value);
		} else if(cmpop.equals("!="))			{
			c = new NQComparer(sysEnv, value);
		} else if(cmpop.equals("<>"))			{
			c = new NQComparer(sysEnv, value);
		} else if(cmpop.equals(">"))			{
			c = new GTComparer(sysEnv, value);
		} else if(cmpop.equals(">="))			{
			c = new GEComparer(sysEnv, value);
		} else if(cmpop.equals("<"))			{
			c = new LTComparer(sysEnv, value);
		} else if(cmpop.equals("<="))			{
			c = new LEComparer(sysEnv, value);
		} else if(cmpop.equals("=~"))			{
			c = new LikeComparer(sysEnv, value.toString());
			cst = new StringCaster();
		} else if(cmpop.equals(ParseStr.S_LIKE))		{
			c = new LikeComparer(sysEnv, value.toString());
			cst = new StringCaster();
		} else if(cmpop.equals("!~"))			{
			c = new NotLikeComparer(sysEnv, value.toString());
			cst = new StringCaster();
		} else if(cmpop.equals(ParseStr.S_NOTLIKE))	{
			c = new NotLikeComparer(sysEnv, value.toString());
			cst = new StringCaster();
		} else throw new CommonErrorException(new SDMSMessage(sysEnv, "03511031050", "Unknown comparison operator: " + cmpop));

		if(cst == null) {
			if(value instanceof String)	{
				cst = new StringCaster();
			}
			if(value instanceof Integer)	{
				cst = new IntegerCaster();
			}
			if(value instanceof Double)	{
				cst = new DoubleCaster();
			}
		}
	}

	public boolean valid(SystemEnvironment sysEnv, SDMSProxy p)
	throws SDMSException
	{
		String parmVal = null;
		ParameterFilterCache parameterFilterCache;
		if (sysEnv.tx.txData.containsKey(SystemEnvironment.S_PARAMETERFILTER_CACHE)) {
			parameterFilterCache = (ParameterFilterCache)(sysEnv.tx.txData.get(SystemEnvironment.S_PARAMETERFILTER_CACHE));

			if (! parameterFilterCache.id.equals(p.getId(sysEnv))) {
				parameterFilterCache.id = p.getId(sysEnv);
				parameterFilterCache.parameters.clear();
			} else
				parmVal = (String)(parameterFilterCache.parameters.get(name));
		} else {
			parameterFilterCache = new ParameterFilterCache();
			parameterFilterCache.id = p.getId(sysEnv);
			sysEnv.tx.txData.put(SystemEnvironment.S_PARAMETERFILTER_CACHE, parameterFilterCache);
		}
		try {
			if (parmVal == null) {
				if (p instanceof SDMSSubmittedEntity) {
					SDMSSubmittedEntity sme = (SDMSSubmittedEntity) p;
					try {
						parmVal = sme.getVariableValue(sysEnv, name, true, ParseStr.S_DEFAULT);
					} catch(NotFoundException cee) {
						parmVal = null;
					}
				} else if (p instanceof SDMSSchedulingEntity) {
					SDMSSchedulingEntity se = (SDMSSchedulingEntity) p;
					try {
						parmVal = se.getVariableValue(sysEnv, name);
					} catch (NotFoundException cee) {
						parmVal = null;
					}
				} else if (p instanceof SDMSCalendar) {
					try {
						SDMSScheduledEvent scev = SDMSScheduledEventTable.getObject(sysEnv, ((SDMSCalendar)p).getScevId(sysEnv));
						SDMSEvent ev = SDMSEventTable.getObject(sysEnv, scev.getEvtId(sysEnv));
						SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
						parmVal = se.getVariableValue(sysEnv, name);
					} catch (NotFoundException cee) {
						parmVal = null;
					}
				} else if (p instanceof SDMSScheduledEvent) {
					try {
						SDMSEvent ev = SDMSEventTable.getObject(sysEnv, ((SDMSScheduledEvent)p).getEvtId(sysEnv));
						SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, ev.getSeId(sysEnv));
						parmVal = se.getVariableValue(sysEnv, name);
					} catch (NotFoundException cee) {
						parmVal = null;
					}
				}
				if (parmVal != null)
					parameterFilterCache.parameters.put(name, parmVal);
			}
			if (parmVal != null)
				return c.cmp(cst.cast(parmVal));
		} catch (Exception e) { }
		return false;
	}

	class ParameterFilterCache
	{
		protected Long id = null;
		protected HashMap parameters = new HashMap();
	}
}

abstract class Caster
{

	abstract Comparable cast(String v);
}

class StringCaster extends Caster
{

	Comparable cast(String v)
	{
		return v;
	}
}

class IntegerCaster extends Caster
{

	Comparable cast(String v)
	{
		try {
			int i = Integer.parseInt(v);
			return new Integer(i);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
}

class DoubleCaster extends Caster
{

	Comparable cast(String v)
	{
		try {
			double x = Double.parseDouble(v);
			return new Double(x);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
}

abstract class Comparer
{

	Comparable wert;

	Comparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		wert = w;
	}

	abstract boolean cmp(Comparable val);

}

class LTComparer extends Comparer
{

	LTComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(val.compareTo(wert) < 0) return true;
		return false;
	}
}

class LEComparer extends Comparer
{

	LEComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(val.compareTo(wert) <= 0) return true;
		return false;
	}
}

class GTComparer extends Comparer
{

	GTComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(val.compareTo(wert) > 0) return true;
		return false;
	}
}

class GEComparer extends Comparer
{

	GEComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(val.compareTo(wert) >= 0) return true;
		return false;
	}
}

class EQComparer extends Comparer
{

	EQComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(val.compareTo(wert) == 0) return true;
		return false;
	}
}

class NQComparer extends Comparer
{

	NQComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(val.compareTo(wert) != 0) return true;
		return false;
	}
}

class LikeComparer extends Comparer
{

	Pattern p;

	LikeComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
		if(w == null) p = null;
		try {
			p = Pattern.compile(w.toString());
		} catch (PatternSyntaxException pse) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03511030959", "Error in regular expression"));
		}
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(p == null) return false;

		try {
			Matcher m;
			m = p.matcher((String) val);
			return m.matches();
		} catch(Exception e) { }
		return false;
	}
}

class NotLikeComparer extends Comparer
{

	Pattern p;

	NotLikeComparer(SystemEnvironment sysEnv, Comparable w)
	throws SDMSException
	{
		super(sysEnv, w);
		if(w == null) p = null;
		try {
			p = Pattern.compile(w.toString());
		} catch (PatternSyntaxException pse) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03511031051", "Error in regular expression"));
		}
	}

	boolean cmp(Comparable val)
	{
		if(val == null) return false;
		if(p == null) return false;

		try {
			Matcher m;
			m = p.matcher((String) val);
			return !m.matches();
		} catch(Exception e) { }
		return false;
	}
}

