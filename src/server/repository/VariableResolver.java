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
	protected final static char[] eValidChars;

	static
	{
		final char[] carr = {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'@', '_', '#', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
		};
		final char[] ecarr = {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'@', '_', '#', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'!', '%', '&', '(', ')', '*', '+', ',', '-', '/', ';', '<', '=',
			'>', '?', '|'
		};
		Arrays.sort(carr);
		Arrays.sort(ecarr);
		validChars = carr;
		eValidChars = ecarr;
	}

	public String parseAndSubstitute(SystemEnvironment sysEnv,
						SDMSProxy thisObject,
						String key,
	                                 String value,
						boolean fastAccess,
						String mode,
						boolean triggercontext,
						Stack recursionCheck,
						long version)
		throws SDMSException
	{
		return parseAndSubstitute (sysEnv, thisObject, key, value, fastAccess, mode, triggercontext, recursionCheck, version, null);
	}

	public String parseAndSubstitute(SystemEnvironment sysEnv,
						SDMSProxy thisObject,
						String key,
	                                 String value,
						boolean fastAccess,
						String mode,
						boolean triggercontext,
						Stack recursionCheck,
						long version,
						SDMSScope evalScope)
		throws SDMSException
	{
		try {
			recursionCheck.push(new SDMSKey(thisObject.getId(sysEnv), key));
			StringBuffer result = new StringBuffer();
			final String BICSUITE_PRAGMA_VPFX = "BICSUITE_PRAGMA_VPFX:";
			final String BICSUITE_PRAGMA_PBS = "BICSUITE_PRAGMA_PBS";
			final int pfxOffset = BICSUITE_PRAGMA_VPFX.length();
			final boolean pbs = value.indexOf(BICSUITE_PRAGMA_PBS) != -1;
			final int index = value.indexOf(BICSUITE_PRAGMA_VPFX);
			char prefix = '$';
			if (index != -1) {
				prefix = value.charAt (index + pfxOffset);
				value = value.substring(0, index + pfxOffset) + "\\" + value.substring(index + pfxOffset);
			}
			final char[] str = value.toCharArray();
			boolean escape = false;

			for(int i = 0; i < str.length; ++i) {
				char c = str[i];
				if(escape) {
					if (c != '\\' && c != prefix)
						result.append('\\');
					result.append(c);
					escape = false;
				} else {
					if (c == prefix) {
						int varEnd;
						try {
							varEnd = readVar(sysEnv, thisObject, str, i, fastAccess, mode, triggercontext, result, recursionCheck, version, evalScope, true);
						} catch (NotFoundException nfe) {
							SDMSThread.doTrace(sysEnv.cEnv, "recursionCheck:", SDMSThread.SEVERITY_WARNING);
							for (int rci = 0; rci < recursionCheck.size(); ++rci) {
								SDMSThread.doTrace(sysEnv.cEnv, recursionCheck.elementAt(rci).toString(), SDMSThread.SEVERITY_WARNING);
							}
							throw nfe;
						}
						if (varEnd == i)
							result.append(c);
						else
							i = varEnd;
					} else if (c == '\\') {
						escape = true;
					} else {
						result.append(c);
					}
				}
			}
			if(escape)
				result.append('\\');

			recursionCheck.pop();
			return result.toString();
		} catch (NotFoundException nfe) {
			recursionCheck.pop();
			throw nfe;
		}
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
	                    SDMSScope evalScope,
	                    boolean doSubstitute)
		throws SDMSException
	{
		StringBuffer varbuf = new StringBuffer();
		int i = pos + 1;
		boolean delimited = false;
		boolean caseSensitive = false;
		int maxpos = key.length;
		Long objId = thisObject.getId(sysEnv);
		char searchChars[] = validChars;

		if(key[i] == '{') {
			i++;
			delimited = true;
			if (key[i] == '\'') {
				i++;
				caseSensitive = true;
				searchChars = eValidChars;
			}
		}
		while(i < maxpos && (delimited || Arrays.binarySearch(searchChars, key[i]) >= 0)) {
			if(delimited && ((!caseSensitive && key[i] == '}') || (caseSensitive && key[i] == '\''))) {
				if (caseSensitive) {
					if (i >= maxpos - 1) {
						SDMSThread.doTrace(sysEnv.cEnv, "Error parsing parameter value of " + objId + ", unterminated Parameter reference", SDMSThread.SEVERITY_WARNING);
						if (SystemEnvironment.parameterSyntaxHandling == SystemEnvironment.PSH_ERROR)
							throw new CommonErrorException(new SDMSMessage(sysEnv, "03303071558", "Syntax error: unterminated Parameter reference"));
						return pos;
					}
					if (key[i+1] == '}')
						i++;
					else {
						SDMSThread.doTrace(sysEnv.cEnv, "Error parsing parameter value of " + objId + ", unexpected character '" + key[i+1] + "'", SDMSThread.SEVERITY_WARNING);
						if (SystemEnvironment.parameterSyntaxHandling == SystemEnvironment.PSH_ERROR)
							throw new CommonErrorException(new SDMSMessage(sysEnv, "03303071558", "Syntax error: unexpected character '" + key[i+1] + "', expected a closing curly brace"));
						return pos;
					}
				}
				i++;
				break;
			} else {
				if (delimited && Arrays.binarySearch(searchChars, key[i]) < 0) {
					SDMSThread.doTrace(sysEnv.cEnv, "Error parsing parameter value of " + objId + ", unexpected character '" + key[i] + "'", SDMSThread.SEVERITY_WARNING);
					if (SystemEnvironment.parameterSyntaxHandling == SystemEnvironment.PSH_ERROR)
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03303071558", "Syntax error: invalid character '" + key[i] + "' in parameter name"));
					return pos;
				}
				varbuf.append(key[i]);
			}
			i++;
		}
		i--;

		final String varName;
		if (caseSensitive)	varName = varbuf.toString();
		else			varName = varbuf.toString().toUpperCase();
		SDMSKey k = new SDMSKey(objId, varName);
		if(recursionCheck.search(k) >= 0) {
			String loopStr = "";
			for (int rci = 0; rci < recursionCheck.size(); ++rci) {
				loopStr += recursionCheck.elementAt(rci).toString() + "->";
			}
			loopStr += k.toString();
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03603010059", "Run into a loop while trying to resolve variable $1 ($2)", varName, loopStr));
		}
		boolean resolveSucceeded = true;
		Boolean isDefault = (Boolean) sysEnv.tx.txData.get(SystemEnvironment.S_ISDEFAULT);
		sysEnv.tx.txData.remove(SystemEnvironment.S_ISDEFAULT);
		try {
			String tmp = getInternalVariableValue(sysEnv, thisObject, varName, fastAccess, mode, triggercontext, recursionCheck, version, evalScope, doSubstitute);
			result.append(tmp);
		} catch (NotFoundException e) {
			if (SystemEnvironment.unresolvedParameterHandling == SystemEnvironment.UPH_ERROR)
				throw e;
			else
				resolveSucceeded = false;
		}
		if(isDefault != null)
			sysEnv.tx.txData.put(SystemEnvironment.S_ISDEFAULT, isDefault);
		if (!resolveSucceeded && SystemEnvironment.unresolvedParameterHandling == SystemEnvironment.UPH_ECHO)
			return pos;
		return i;
	}

	protected String getInternalVariableValue(SystemEnvironment sysEnv,
							SDMSProxy thisObject,
							String key,
							boolean fastAccess,
							String mode,
							boolean triggercontext,
							Stack recursionCheck,
	                				long version,
	                				boolean doSubstitute)
		throws SDMSException
	{
		return getInternalVariableValue (sysEnv, thisObject, key, fastAccess, mode, triggercontext, recursionCheck, version, null, doSubstitute);
	}

	abstract protected String getInternalVariableValue(SystemEnvironment sysEnv,
							SDMSProxy thisObject,
							String key,
							boolean fastAccess,
							String mode,
							boolean triggercontext,
							Stack recursionCheck,
							long version,
	                SDMSScope evalScope,
	                boolean doSubstitute)
		throws SDMSException;

	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, boolean fastAccess, String mode, boolean triggercontext, SDMSScope evalScope, boolean doSubstitute)
		throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, fastAccess, mode, triggercontext, -1, evalScope, doSubstitute);
	}

	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, long version, boolean doSubstitute)
		throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, false, ParseStr.S_DEFAULT, false, version, null, doSubstitute);
	}

	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, boolean doSubstitute)
		throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, false, ParseStr.S_DEFAULT, false, -1, null, doSubstitute);
	}
	public String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, SDMSSubmittedEntity sme, boolean doSubstitute)
		throws SDMSException
	{
		return getVariableValue(sysEnv, thisObject, key, false, ParseStr.S_DEFAULT, false, -1, null, sme, doSubstitute);
	}

	abstract protected String getVariableValue(SystemEnvironment sysEnv,
							SDMSProxy thisObject,
							String key,
							boolean fastAccess,
							String mode,
							boolean triggercontext,
							long version,
	                SDMSScope evalScope,
	                boolean doSubstitute)
		throws SDMSException;

	protected String getVariableValue(SystemEnvironment sysEnv,
							SDMSProxy thisObject,
							String key,
							boolean fastAccess,
							String mode,
							boolean triggercontext,
							long version,
							SDMSScope evalScope,
	                                  		SDMSSubmittedEntity sme,
	                                 		boolean doSubstitute)
		throws SDMSException
	{
		return getVariableValue(sysEnv,thisObject,key,fastAccess,mode,triggercontext,version,evalScope, doSubstitute);
	}

	public VariableResolver()
	{
	}
}
