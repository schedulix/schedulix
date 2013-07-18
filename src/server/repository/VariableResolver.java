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


package de.independit.scheduler.server.repository;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.*;

public abstract class VariableResolver
{

	public static final String __version = "@(#) $Id: VariableResolver.java,v 2.5.4.1 2013/03/14 10:25:28 ronald Exp $";

	protected final static SimpleDateFormat myFormat = new SimpleDateFormat ("yyyyMMddHHmmss", SystemEnvironment.systemLocale);
	protected final static String emptyString = new String("");
	protected final static char[] validChars;

	static
	{
		final char[] carr = {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'@', '_', '#', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
		};
		Arrays.sort(carr);
		validChars = carr;
	}

	public String parseAndSubstitute(SystemEnvironment sysEnv,
	                                 SDMSProxy thisObject,
	                                 String key,
	                                 boolean fastAccess,
	                                 String mode,
	                                 boolean triggercontext,
	                                 Stack recursionCheck,
	                                 long version)
	throws SDMSException
	{
		return parseAndSubstitute (sysEnv, thisObject, key, fastAccess, mode, triggercontext, recursionCheck, version, null);
	}

	public String parseAndSubstitute(SystemEnvironment sysEnv,
	                                 SDMSProxy thisObject,
	                                 String key,
	                                 boolean fastAccess,
	                                 String mode,
	                                 boolean triggercontext,
	                                 Stack recursionCheck,
	                                 long version,
	                                 SDMSScope evalScope)
	throws SDMSException
	{

		StringBuffer result = new StringBuffer();
		final char[] str = key.toCharArray();

		boolean escape = false;

		for(int i = 0; i < str.length; ++i) {
			char c = str[i];
			if(escape) {
				if(c != '\\' && c != '$') {
					result.append('\\');
				}
				result.append(c);
				escape = false;
			} else {
				if (c == '$') {

					i = readVar(sysEnv, thisObject, str, i, fastAccess, mode, triggercontext, result, recursionCheck, version, evalScope);
				} else if (c == '\\') {
					escape = true;
				} else {
					result.append(c);
				}
			}
		}

		if(escape)
			result.append('\\');
		return result.toString();
	}

	private int readVar(SystemEnvironment sysEnv,
	                    SDMSProxy thisObject,
	                    char[] key,
	                    int pos,
	                    boolean fastAccess,
	                    String mode,
	                    boolean triggercontext,
	                    StringBuffer result,
	                    Stack recursionCheck,
	                    long version,
	                    SDMSScope evalScope)
	throws SDMSException
	{
		StringBuffer var = new StringBuffer();
		int i = pos + 1;
		boolean delimited = false;
		int maxpos = key.length;
		Long objId = thisObject.getId(sysEnv);

		if(key[i] == '{') {
			i++;
			delimited = true;
		}
		while(i < maxpos && (delimited || Arrays.binarySearch(validChars, key[i]) >= 0)) {
			if(key[i] == '}') {
				i++;
				break;
			} else {
				var.append(key[i]);
			}
			i++;
		}
		i--;

		final String varName = var.toString();
		SDMSKey k = new SDMSKey(objId, varName);
		if(recursionCheck.search(k) >= 0) {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03603010059", "Run into a loop while trying to resolve variable $1", varName));
		}
		recursionCheck.push(k);

		Boolean isDefault = (Boolean) sysEnv.tx.txData.get(SystemEnvironment.S_ISDEFAULT);
		sysEnv.tx.txData.remove(SystemEnvironment.S_ISDEFAULT);
		result.append(getInternalVariableValue(sysEnv, thisObject, varName, fastAccess, mode, triggercontext, recursionCheck, version, evalScope));
		if(isDefault != null)
			sysEnv.tx.txData.put(SystemEnvironment.S_ISDEFAULT, isDefault);
		recursionCheck.pop();
		return i;
	}

	protected String getInternalVariableValue(SystemEnvironment sysEnv,
	                SDMSProxy thisObject,
	                String key,
	                boolean fastAccess,
	                String mode,
	                boolean triggercontext,
	                Stack recursionCheck,
	                long version)
	throws SDMSException
	{
		return getInternalVariableValue (sysEnv, thisObject, key, fastAccess, mode, triggercontext, recursionCheck, version, null);
	}

	abstract protected String getInternalVariableValue(SystemEnvironment sysEnv,
	                SDMSProxy thisObject,
	                String key,
	                boolean fastAccess,
	                String mode,
	                boolean triggercontext,
	                Stack recursionCheck,
	                long version,
	                SDMSScope evalScope)
	throws SDMSException;

	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, boolean fastAccess, String mode, boolean triggercontext, SDMSScope evalScope)
	throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, fastAccess, mode, triggercontext, -1, evalScope);
	}

	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, long version)
	throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, false, ParseStr.S_DEFAULT, false, version, null);
	}

	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key)
	throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, false, ParseStr.S_DEFAULT, false, -1, null);
	}
	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, false, ParseStr.S_DEFAULT, false, -1, null, sme);
	}

	abstract protected String getVariableValue(SystemEnvironment sysEnv,
	                SDMSProxy thisObject,
	                String key,
	                boolean fastAccess,
	                String mode,
	                boolean triggercontext,
	                long version,
	                SDMSScope evalScope)
	throws SDMSException;

	protected String getVariableValue(SystemEnvironment sysEnv,
	                                  SDMSProxy thisObject,
	                                  String key,
	                                  boolean fastAccess,
	                                  String mode,
	                                  boolean triggercontext,
	                                  long version,
	                                  SDMSScope evalScope,
	                                  SDMSSubmittedEntity sme)
	throws SDMSException
	{
		return getVariableValue(sysEnv,thisObject,key,fastAccess,mode,triggercontext,version,evalScope);
	}

	public VariableResolver()
	{

	}
}
