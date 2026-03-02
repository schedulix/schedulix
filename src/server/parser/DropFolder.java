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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;

public class DropFolder extends Node
{

	public final static String __version = "@(#) $Id: DropFolder.java,v 2.7.14.1 2013/03/14 10:24:29 ronald Exp $";

	private ObjectURL url;
	private Vector urlVector;
	private boolean cascade;
	private boolean force;
	private boolean noerr;

	public DropFolder(Vector v, Boolean c, Boolean f, Boolean ne)
	{
		super();
		urlVector = v;
		cascade = c.booleanValue();
		force = f.booleanValue();
		noerr = ne.booleanValue();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long parentId ;
		SDMSFolder f;

		HashSet<Long> seIds = new HashSet<Long> ();

		for (int i = 0; i < urlVector.size(); ++i) {
			url = (ObjectURL) urlVector.get(i);
			if (url.objType.equals(SDMSObjectComment.FOLDER)) {
				if(cascade || force) {
					try {
						f = (SDMSFolder) url.resolve(sysEnv);
						parentId = f.getParentId(sysEnv);
					} catch(NotFoundException nfe) {
						if(noerr) {
							continue;
						}
						throw nfe;
					}

					if(parentId == null) {
						throw new CommonErrorException(new SDMSMessage(sysEnv, "03704102211",
						                               "Folder SYSTEM cannot be dropped"));
					}
					f.collectSeIds(sysEnv, seIds, null);
				}
			} else {
				SDMSSchedulingEntity se;
				try {
					se = (SDMSSchedulingEntity)url.resolve(sysEnv);
					seIds.add(se.getId(sysEnv));
				} catch (NotFoundException nfe) {
					if (!noerr) throw nfe;
				}
			}
		}
		SDMSSchedulingEntity.delete(sysEnv, seIds, force);

		for (int i = 0; i < urlVector.size(); ++i) {
			url = (ObjectURL) urlVector.get(i);
			if (url.objType.equals(SDMSObjectComment.FOLDER)) {
				try {
					f = (SDMSFolder) url.resolve(sysEnv);
					parentId = f.getParentId(sysEnv);
				} catch(NotFoundException nfe) {
					if(noerr) {
						continue;
					}
					throw nfe;
				}

				final Long fId = f.getId(sysEnv);
				if(cascade || force)
					f.deleteCascade(sysEnv, null);
				else
					f.delete(sysEnv);
			}
		}
		result.setFeedback(new SDMSMessage(sysEnv, "03204112230", "Folder(s) dropped"));
	}
}
