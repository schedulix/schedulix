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
import javax.xml.bind.DatatypeConverter;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.jobserver.Config;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;
import com.sun.jna.platform.win32.Win32Exception;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsSecurityContextImpl;

public class SSOConnect extends Connect
{
	private String token;
	private boolean firstTime;

	private final static String ADMIN = "ADMIN";
	private final static String PUBLIC = "PUBLIC";
	private final static String ISDEFAULT = "ISDEFAULT";
	private final static String PROVIDER = "PROVIDER";

	public SSOConnect(WithHash w, boolean firstTime)
	{
		super(w);
		cmdtype = Node.ANY_COMMAND;
		txMode = SDMSTransaction.READWRITE;
		token = (String) w.get(ParseStr.S_TOKEN);
		this.firstTime = firstTime;
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Vector data = new Vector();

		WindowsAuthProviderImpl provider = null;
		IWindowsSecurityContext serverContext = null;
		byte[] byteToken;
		boolean ssoFailed = false;

		if (firstTime) {
			sysEnv.cEnv.SSOInfo = new HashMap();
			try {
				provider = new WindowsAuthProviderImpl();
			} catch (Throwable t) {
				SDMSThread.doTrace(sysEnv.cEnv, t.toString(), SDMSThread.SEVERITY_WARNING);
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02709250926", "Single Sign On not supported in this installation"));
			}
			sysEnv.cEnv.SSOInfo.put(PROVIDER, provider);
		} else {
			provider = (WindowsAuthProviderImpl)(sysEnv.cEnv.SSOInfo.get(PROVIDER));
		}
		try {
			byteToken = DatatypeConverter.parseBase64Binary(token);
			serverContext = provider.acceptSecurityToken("server-connection", byteToken, "Negotiate");
			byteToken = serverContext.getToken();
			if (byteToken != null)
				token = DatatypeConverter.printBase64Binary(byteToken);
			else
				token = "null";

		} catch (Throwable t) {
			SDMSThread.doTrace(sysEnv.cEnv, t.toString(), SDMSThread.SEVERITY_WARNING);
			throw new CommonErrorException(new SDMSMessage(sysEnv, "02709250928", "Single Sign On not supported in this installation"));
		}
		if (firstTime) {
			evaluateWith(sysEnv);
		} else {
			if (serverContext.isContinue())
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02709201816", "SSO negotiation failed"));
			user = serverContext.getIdentity().getFqn();
			int bsIdx = user.indexOf('\\');
			String domain = "";
			if (bsIdx > 0) {
				domain = user.substring(0,bsIdx);
				if (SystemEnvironment.includeDomainNames)
					user = user.replace('\\','_');
				else
					user = user.substring (bsIdx + 1);
			}
			if (SystemEnvironment.nameCase.equals(SystemEnvironment.CASE_UPPER)) {
				user = user.toUpperCase();
			} else if (SystemEnvironment.nameCase.equals(SystemEnvironment.CASE_LOWER)) {
				user = user.toLowerCase();
			}

			String namePrefix = (SystemEnvironment.bicsuitePrefix.length() > 0 ? SystemEnvironment.bicsuitePrefix + "_" : "") +
			                    (SystemEnvironment.serverName.length() > 0 ? SystemEnvironment.serverName + "_" : "");

			IWindowsAccount[] groups = serverContext.getIdentity().getGroups();
			Vector<String> vGroups = new Vector<String>();
			boolean isBicsuiteUser = false;
			String defaultGroup = null;
			boolean isDefaultGroup = false;
			for (IWindowsAccount group : serverContext.getIdentity().getGroups()) {
				String name = group.getFqn();
				int i = name.indexOf('\\');
				String groupDomain = name.substring(0,bsIdx);
				if (i > 0)
					name = name.substring(i + 1);
				if (!name.startsWith(namePrefix))
					continue;
				if (name.endsWith("_" + ISDEFAULT)) {
					isDefaultGroup = true;
					name = name.substring(0,name.length() - ("_" + ISDEFAULT).length());
				}
					isBicsuiteUser = true;
				if (name.equals(namePrefix + "_" + PUBLIC)) {
					continue;
				}
				if (SystemEnvironment.useAdGroups) {
					if (name.equals(namePrefix + "_" + ADMIN))
						name = (ADMIN);
					else {
						name = name.substring(namePrefix.length());
						if (SystemEnvironment.includeDomainNames)
							name = groupDomain + "_" + name;
						if (SystemEnvironment.nameCase.equals(SystemEnvironment.CASE_UPPER)) {
							name = name.toUpperCase();
						} else if (SystemEnvironment.nameCase.equals(SystemEnvironment.CASE_LOWER)) {
							name = name.toLowerCase();
						}
					}
					if (isDefaultGroup)
						defaultGroup = name;
					vGroups.add(name);
				}
			}
			if (!isBicsuiteUser)
				throw new CommonErrorException(new SDMSMessage(sysEnv, "02709251459", "Permission denied"));

			if (SystemEnvironment.useAdGroups)
				initUser(sysEnv, vGroups.toArray(new String[0]), false, SystemEnvironment.autoCreateUsers, SystemEnvironment.autoCreateGroups, defaultGroup);
			else
				initUser(sysEnv, null, false, SystemEnvironment.autoCreateUsers, SystemEnvironment.autoCreateGroups, defaultGroup);
		}

		desc.add("CONNECT_TIME");
		data.add(sysEnv.systemDateFormat.format(new Date(System.currentTimeMillis())));
		if (firstTime) {
			desc.add("TOKEN");
			data.add(token);
		}
		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage (sysEnv, "03205141302", "Connect"), desc, data);
		result.setOutputContainer(d_container);
		result.setFeedback(new SDMSMessage(sysEnv, "03709191436", (firstTime ? "Connection in progress" : "Connected")));
	}
}

