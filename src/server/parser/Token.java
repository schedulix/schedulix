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


package de.independit.scheduler.server.parser;

import java.io.*;
import java.util.*;
import java.lang.*;

public class Token
{

	public final static String __version = "@(#) $Id: Token.java,v 2.2.14.1 2013/03/14 10:24:53 ronald Exp $";

	public String textValue;
	public Integer token;
	public Object value;

	public Token(Integer t, String n, Object v)
	{
		textValue = n;
		token = t;
		value = v;
	}

	public Token(int t, String n, Object v)
	{
		textValue = n;
		token = Integer.valueOf(t);
		value = v;
	}

	public Token(int t, String n)
	{
		textValue = n;
		token = Integer.valueOf(t);
		value = n.toUpperCase();
	}

	public String toString()
	{
		return new String ("textValue : " + textValue + ", token : " + token + ", Object : " + value);
	}
}

