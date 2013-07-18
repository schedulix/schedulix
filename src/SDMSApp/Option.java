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


package de.independit.scheduler.SDMSApp;

public class Option
{
	public static final String __version = "@(#) $Id: Option.java,v 1.3.6.1 2013/03/14 10:24:02 ronald Exp $";

	String  shortopt;
	String  longopt;
	String	iniopt;
	String  key;
	String  value;
	String  defaultvalue;
	String  valuestring;
	boolean isBoolean;
	boolean bvalue;
	boolean mandatory;
	boolean set;
	String  doc;

	public Option (String shortopt, String longopt, String iniopt, String key, String defaultvalue, String valuestring, boolean mandatory, String doc)
	{
		this.shortopt      = shortopt;
		this.longopt       = longopt;
		this.iniopt        = iniopt;
		this.key           = key;
		this.value         = null;
		this.isBoolean     = (valuestring == null);
		this.bvalue        = false;
		this.set           = false;
		this.defaultvalue  = defaultvalue;
		this.valuestring   = valuestring;
		this.mandatory     = mandatory;
		this.doc           = doc;
	}

	public boolean getIsBoolean()
	{
		return isBoolean;
	}

	public boolean getBValue()
	{
		return bvalue;
	}

	public String getValue()
	{
		return value;
	}

	public void set (String value, boolean bvalue)
	{
		this.value = value;
		this.bvalue = bvalue;
		this.set = true;
	}

	public void set (Option o)
	{
		value = o.value;
		bvalue = o.bvalue;
		set = true;
	}

	public String toString()
	{
		return  "shortopt: " + shortopt +
		        ", longopt: " + longopt +
		        ", iniopt: " + iniopt +
		        ", key: " + key +
		        ", value: " + value +
		        ", isBoolean: " + isBoolean +
		        ", bvalue: " + bvalue +
		        ", set: " + set +
		        ", defaultvalue: " + defaultvalue +
		        ", valuestring: " + valuestring +
		        ", mandatory: " + mandatory +
		        ", doc: " + doc;
	}
	public String toShortString()
	{
		return  "shortopt: " + shortopt +
		        ",\tlongopt: " + longopt +
		        ",\tiniopt: " + iniopt +
		        ",\tkey: " + key +
		        ",\tvalue: " + value +
		        ",\tbvalue: " + bvalue +
		        ",\tset: " + set;
	}
}
