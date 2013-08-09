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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class DropNamedResource extends Node
{

	public final static String __version = "@(#) $Id: DropNamedResource.java,v 2.7.14.2 2013/03/19 17:16:46 ronald Exp $";

	private ObjectURL url;
	private boolean cascade;
	private boolean noerr;

	public DropNamedResource(ObjectURL u, Boolean c, Boolean n)
	{
		super();
		url = u;
		cascade = c.booleanValue();
		noerr = n.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSNamedResource nr;

		try {
			nr = (SDMSNamedResource) url.resolve(sysEnv);
		} catch(NotFoundException nfe) {
			if(noerr) {
				result.setFeedback(new SDMSMessage(sysEnv, "03311122305", "No Named Resource dropped"));
				return;
			}
			throw nfe;
		}
		dropNamedResource(sysEnv, nr);

		result.setFeedback(new SDMSMessage(sysEnv, "03204112233", "Named Resource dropped"));
	}

	private void dropNamedResource(SystemEnvironment sysEnv, SDMSNamedResource nr)
	throws SDMSException
	{
		Vector rv;
		final Long nrId = nr.getId(sysEnv);
		if(cascade) {
			rv = SDMSNamedResourceTable.idx_parentId.getVector(sysEnv, nrId);
			for(int i = 0; i < rv.size(); i++) {
				dropNamedResource(sysEnv, (SDMSNamedResource) rv.get(i));
			}
			rv = SDMSResourceTable.idx_nrId.getVector(sysEnv, nrId);
			for(int i = 0; i < rv.size(); i++) {
				SDMSResource r = (SDMSResource) rv.get(i);
				r.delete(sysEnv);
			}
		}

		nr.killParameters (sysEnv);

		nr.delete(sysEnv);
	}

}

