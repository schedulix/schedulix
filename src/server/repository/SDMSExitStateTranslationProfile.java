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
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSExitStateTranslationProfile extends SDMSExitStateTranslationProfileProxyGeneric
{

	public final static String __version = "@(#) $Id: SDMSExitStateTranslationProfile.java,v 2.3.2.1 2013/03/14 10:25:19 ronald Exp $";

	protected SDMSExitStateTranslationProfile(SDMSObject p_object)
	{
		super(p_object);
	}

	public Long translate (SystemEnvironment sysEnv, Long p_esdId, boolean assume_identity)
	throws SDMSException
	{
		if(sysEnv.tx.mode == SDMSTransaction.READWRITE) {
			Long estpId = this.getId(sysEnv);
			try {
				SDMSExitStateTranslation est = SDMSExitStateTranslationTable.idx_estpId_fromEsdId_getUnique(
				                                       sysEnv, new SDMSKey (estpId, p_esdId));
				return est.getToEsdId(sysEnv);
			} catch (NotFoundException nfe) {
				if (assume_identity)
					return p_esdId;
				else
					return null;
			}
		}
		return translate (sysEnv, p_esdId, sysEnv.tx.versionId, assume_identity);
	}

	public Long translate (SystemEnvironment sysEnv, Long p_esdId)
	throws SDMSException
	{
		return translate (sysEnv, p_esdId, true);
	}

	public Long translate (SystemEnvironment sysEnv, Long p_esdId, long version)
	throws SDMSException
	{
		return translate (sysEnv, p_esdId, version, true);
	}

	public Long translate (SystemEnvironment sysEnv, Long p_esdId, long version, boolean assume_identity)
	throws SDMSException
	{
		Long estpId = this.getId(sysEnv);
		try {
			SDMSExitStateTranslation est = SDMSExitStateTranslationTable.idx_estpId_fromEsdId_getUnique(sysEnv, new SDMSKey (estpId, p_esdId), version);
			return est.getToEsdId(sysEnv);
		} catch (NotFoundException nfe) {
			if (assume_identity)
				return p_esdId;
			else
				return null;
		}
	}

	public String getURLName(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return getName(sysEnv);
	}

	public String getURL(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return "exit state translation " + getURLName(sysEnv);
	}
}
