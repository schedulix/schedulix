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

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.parser.*;

public class ConnectionEnvironment
{

	public final static String __version = "@(#) $Id: ConnectionEnvironment.java,v 2.12.2.1 2013/03/14 10:24:08 ronald Exp $";

	public static final int IDLE      = 0;
	public static final int QUEUED    = 1;
	public static final int ACTIVE    = 2;
	public static final int COMMITTING = 3;
	public static final int CONNECTED = 4;
        public static final int PARSING = 5;
        public static final int RENDERING = 6;

	public static final String stateNames[] = {
		"IDLE", "QUEUED", "ACTIVE", "COMMITTING", "CONNECTED", "PARSING", "RENDERING"
	};

	protected final int id;
	protected final String name;
	protected long start;
	protected long last;
	protected Date dStart;
	protected PrintStream ostream;
	protected Long uid;
	protected HashSet gid;
	protected SyncFifo cmdQueue;
	protected SyncFifo roCmdQueue;
	protected SDMSThread me;
	protected ThreadLock lock;
	protected boolean jobServer;
	protected boolean job;
	protected boolean user;
	public  SDMSTransaction tx;
	protected SDMSOutputRenderer renderer;
	protected boolean trace;
	protected int tracelevel;
	protected final int port;
	protected int state;
	public  String actstmt;
	public  String firstToken;
	protected InetAddress userNode;
	protected String info;

	private Stack groupStack;
	public SDMSThread worker;

	protected Long prev_uid;
	protected HashSet prev_gid;
	protected boolean prev_trace;
	protected int prev_tracelevel;

	public ConnectionEnvironment(int c, String n, boolean svrtrc, PrintStream o, SyncFifo f, SyncFifo rof, int portno, InetAddress uNode)
	{
		id = c;
		name = n;
		ostream = o;
		start = System.currentTimeMillis();
		last = start;
		dStart = new Date(start);
		cmdQueue = f;
		roCmdQueue = rof;
		lock = new ThreadLock();
		renderer = new SDMSLineRenderer();
		trace = svrtrc;
		port = portno;
		actstmt = null;
		firstToken = null;
		info = null;
		state = CONNECTED;
		groupStack = new Stack();
		userNode = uNode;
		tracelevel = SDMSThread.SEVERITY_INFO;
		worker = null;
		prev_uid = null;
	}

	public ConnectionEnvironment(int c, boolean svrtrc, PrintStream o, SyncFifo f, SyncFifo rof, int portno, InetAddress uNode)
	{
		this (c, null, svrtrc, o, f, rof, portno, uNode);
	}

	public ConnectionEnvironment(int c, String n, boolean svrtrc, PrintStream o, SyncFifo f, SyncFifo rof, InetAddress uNode)
	{
		this (c, n, svrtrc, o, f, rof, 0, uNode);
	}

	public int id()
	{
		return id;
	}
	public String name()
	{
		return name != null ? name : new Integer (id).toString();
	}
	public long start()
	{
		return start;
	}
	public long last()
	{
		return last;
	}
	public long idle()
	{
		return ((System.currentTimeMillis() - last + 500)/1000);
	}
	public Date dStart()
	{
		return dStart;
	}
	public PrintStream ostream()
	{
		return ostream;
	}

	public Long uid()
	{
		return uid;
	}
	public HashSet gid()
	{
		return gid;
	}
	public void setUid(Long id)
	{
		uid = id;
	}
	public int timeout()
	{
		return ((UserConnection) me).getTimeout();
	}

	public String getInfo()
	{
		return info;
	}
	public void setInfo(String inf)
	{
		info = inf;
	}

	public String ip()
	{
		if(userNode != null)
			return userNode.getHostAddress();
		return null;
	}
	public InetAddress getAddress()
	{
		return userNode;
	}

	public void setGid(SystemEnvironment sysEnv, Vector v)
		throws SDMSException
	{
		if(v.size() == 0) {
			gid = new HashSet();
			return;
		}
		gid = new HashSet(v.size()*2);
		for(int i = 0; i < v.size(); i++) {
			gid.add(((SDMSMember) v.get(i)).getGId(sysEnv));
		}
	}
	public void addGid(Long groupId)
	{
		if (gid == null) gid = new HashSet();
		gid.add(groupId);
	}
	public void delGid(Long groupId)
	{
		if (gid == null) return;
		gid.remove(groupId);
	}

	public void pushGid(SystemEnvironment sysEnv, HashSet g)
		throws SDMSException
	{
		groupStack.push(gid);
		gid = g;
	}

	public void popGid(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(groupStack.isEmpty())
			throw new FatalException(new SDMSMessage(sysEnv, "03401301115", "Groupstack is empty. Unbalanced change group"));
		gid = (HashSet) groupStack.pop();
	}

	public void emptyGid(SystemEnvironment sysEnv)
		throws SDMSException
	{
		while(!groupStack.isEmpty()) {
			SDMSThread.doTrace(this, "Unbalanced push(gid) : \n\t" + toString(), SDMSThread.SEVERITY_WARNING);
			gid = (HashSet) groupStack.pop();
		}
	}

	public int port()
	{
		return port;
	}

	public SyncFifo cmdQueue()
	{
		return cmdQueue;
	}
	public SyncFifo roCmdQueue()
	{
		return roCmdQueue;
	}

	public void setMe(SDMSThread t)
	{
		me = t;
	}
	public SDMSThread getMe()
	{
		return me;
	}
	public ThreadLock lock()
	{
		return lock;
	}
	public boolean isJobServer()
	{
		return jobServer;
	}
	public boolean isJob()
	{
		return job;
	}
	public boolean isUser()
	{
		return user;
	}
	public void setJobServer()
	{
		jobServer = true;
		job       = false;
		user = false;
	}
	public void setJob()
	{
		job       = true;
		jobServer = false;
		user = false;
	}
	public void setUser()
	{
		user      = true;
		jobServer = false;
		job  = false;
	}
	public void trace_on()
	{
		trace = true;
	}
	public void trace_off()
	{
		trace = false;
	}
	public void setTrace(boolean t)
	{
		trace = t;
	}
	public boolean trace()
	{
		return trace;
	}
	public void setTraceLevel(int t)
	{
		tracelevel = t;
	}
	public int getTraceLevel()
	{
		return tracelevel;
	}
	public void setLast()
	{
		last = System.currentTimeMillis();
	}
	public SDMSOutputRenderer renderer()
	{
		return renderer;
	}
	public void setRenderer(SDMSOutputRenderer type)
	{
		renderer = type;
	}

	public void setRenderer(int type)
	{
		renderer = null;
		switch (type) {
			case Parser.XML:
				renderer = new SDMSXmlRenderer();
				break;
			case Parser.LINE:
				renderer = new SDMSLineRenderer();
				break;
			case Parser.PERL:
				renderer = new SDMSPerlRenderer();
				break;
			case Parser.PYTHON:
				renderer = new SDMSPythonRenderer();
				break;
			case Parser.JSON:
				renderer = new SDMSJsonRenderer();
				break;
			case Parser.SERIAL:
				renderer = new SDMSSerialRenderer();
				break;
			case Parser.TIME:
				renderer = new SDMSTimeRenderer();
				break;
		}
	}

	public long versionId(SystemEnvironment env)	throws SDMSException
	{
		return tx.versionId(env);
	}

	public long txId()
	{
		return tx.txId;
	}
	public void proto_input(String s)
	{
		((UserConnection) me).scanner.proto_input(s);
	}

	public void setState(int s)
	{
		if(s == IDLE || s == QUEUED || s == ACTIVE || s == COMMITTING || s == CONNECTED || s == PARSING || s == RENDERING)
			state = s;
	}

	public String getState()
	{
		return stateNames[state];
	}

	public String toString()
	{
		String s = "Id : " + id + ", Name : " + name + ", Start : " + start + ", Last : " + last +
			   ", Uid : " + uid + ", Type : " + (user ? "User" : job ? "Job" : "Jobserver") +
			   ", Trace : " + trace + ", Port : " + port + ", State : " + stateNames[state];
		return s;
	}

	public boolean setConnectedUser(SystemEnvironment sysEnv, Long uid, Vector groups)
	throws SDMSException
	{
		prev_uid = this.uid;
		prev_gid = this.gid;
		prev_trace = this.trace;
		prev_tracelevel = this.tracelevel;

		setUid(uid);
		setGid(sysEnv, groups);
		return true;
	}

	public boolean resetConnectedUser()
	{
		if (prev_uid != null) {
			uid = prev_uid;
			gid = prev_gid;
			trace = prev_trace;
			tracelevel = prev_tracelevel;

			prev_uid = null;
			prev_gid = null;
			prev_trace = false;
			prev_tracelevel = 0;
			return true;
		}
		return false;
	}
}

