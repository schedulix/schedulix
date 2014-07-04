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

package de.independit.scheduler.server;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.sql.*;
import java.math.*;

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.parser.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public abstract class InternalSession extends SDMSThread
{

	public static final String __version = "@(#) $Id: InternalSession.java,v 2.5.2.1 2013/03/14 10:24:09 ronald Exp $";

	protected ConnectionEnvironment	cEnv;
	protected int wakeupInterval;
	protected int NR;

	static final int NORMAL = 0;
	static final int INITIALIZE = 1;

	public InternalSession(String s)
	{
		super(s);
	}

	protected void initThread(SystemEnvironment env, SyncFifo f, int N, String name, int w)
	{
		wakeupInterval = w;

		cEnv = new ConnectionEnvironment(NR, name, false, null, f, null, null);
		cEnv.setMe(this);
		try {
			env.dbConnection = Server.connectToDB(env);
			cEnv.tx = new SDMSTransaction(env, SDMSTransaction.READONLY, null);
			env.tx = cEnv.tx;
			SDMSUser u = SDMSUserTable.idx_name_deleteVersion_getUnique(env, new SDMSKey(SDMSUser.INTERNAL, new Long(0)));
			Long uId = u.getId(env);
			try {
				cEnv.tx.commit(env);
			} catch (SQLException sqle) {
				doTrace(cEnv, "Error on initializing Thread : " + sqle.toString(), SEVERITY_FATAL);
			}
			cEnv.setGid(env, new Vector());
			cEnv.gid().add(SDMSObject.adminGId);
			cEnv.setUid(uId);
			cEnv.setUser();
		} catch (SDMSException e) {
			doTrace(cEnv, "Cannot find INTERNAL User : " + e.toString(), SEVERITY_FATAL);
		}
		try {
			env.dbConnection.close();
		} catch(Throwable sqle) {
			doTrace(cEnv, "Error on closing DB-Connection : " + sqle.toString(), SEVERITY_FATAL);
		}
	}

	protected boolean post(Node n)
		throws FatalException
	{
		n.setEnv(cEnv);
		cEnv.cmdQueue().post(n);
		cEnv.lock().do_wait();

		if(n.result.error != null) {
			doTrace(cEnv, "Error in internal Statement: " + n.result.error.code + " " + n.result.error.message, SEVERITY_ERROR);
		}

		if(n.result.error != null) return false;
		return true;
	}

	public int id()
	{
		return -NR;
	}

	protected abstract Node getNode(int m);

	public void SDMSrun()
	{
		try {
			try {
				post(getNode(INITIALIZE));
			} catch (SDMSException e) {

			}
			while(run) {
				try {
					sleep(wakeupInterval);
				} catch (InterruptedException ie) {

				}
				try {
					post(getNode(NORMAL));
				} catch (SDMSException e) {

				}
			}
		} catch(Error e) {
			doTrace(cEnv, e.toString(), e.getStackTrace(), SEVERITY_FATAL);
		}
		return;
	}

}
