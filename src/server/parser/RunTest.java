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
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class RunTest extends Node
{

	public final static String __version = "@(#) $Id: RunTest.java,v 2.4.2.1 2013/03/14 10:24:46 ronald Exp $";

	private int testid;
	private String  str;
	private String  str2;

	public RunTest(Integer id)
	{
		super();
		testid = id.intValue();
		str = null;
		str2 = null;
	}

	public RunTest(Integer id, String s)
	{
		super();
		testid = id.intValue();
		str = s;
		str2 = null;
	}

	public RunTest(Integer id, String s, boolean ro)
	{
		super();
		testid = id.intValue();
		str = s;
		str2 = null;
		if(ro) txMode = SDMSTransaction.READONLY;
	}

	public RunTest(Integer id, String s1, String s2, boolean ro)
	{
		super();
		testid = id.intValue();
		str = s1;
		str2 = s2;
		if(ro) txMode = SDMSTransaction.READONLY;
	}

	public void getLock()
	{
		SystemEnvironment.getSharedLock();
	}

	public void releaseLock()
	{
		SystemEnvironment.releaseSharedLock();
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(!SystemEnvironment.runMode.toUpperCase().equals("TEST")) {
			return;
		}

		switch(testid) {
		case 1: do_test1(sysEnv);
			break;
		case 2: do_test2(sysEnv);
			break;
		case 3: do_test3(sysEnv);
			break;
		case 4: do_test4(sysEnv);
			break;
		case 5: do_test5(sysEnv);
			break;
		case 6: do_test6(sysEnv);
			break;
		case 7: do_test7(sysEnv);
			break;
		case 8: do_test8(sysEnv);
			break;
		case 9: do_test9(sysEnv);
			break;
		default:

		}

	}

	private void do_test1(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long id = new Long(str);
		System.out.println("start release of "+id);
		SDMSSubmittedEntityTable.getObject(sysEnv, id).releaseMaster(sysEnv);
	}

	private void do_test2(SystemEnvironment sysEnv)
		throws SDMSException
	{
		try {
			Thread.sleep(300000);
		} catch(InterruptedException ie) {

		}
	}

	private void do_test3(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSRunnableQueueTable t = (SDMSRunnableQueueTable) SystemEnvironment.repository.getTable(sysEnv, SDMSRunnableQueueTableGeneric.tableName);
		t.dump(sysEnv);
	}

	private void do_test4(SystemEnvironment sysEnv)
		throws SDMSException
	{
		if(str == null) {
			SDMSUserTable.idx_name.dumpIndex(sysEnv);
		} else if(str.equals("user")) {
			SDMSUserTable.idx_name.dumpIndex(sysEnv);
		} else if(str.equals("named_resource")) {
			SDMSNamedResourceTable.idx_name.dumpIndex(sysEnv);
		} else if(str.equals("exit_state_definition")) {
			SDMSExitStateDefinitionTable.idx_name.dumpIndex(sysEnv);
		} else
			SDMSUserTable.idx_name.dumpIndex(sysEnv);
	}

	private void do_test5(SystemEnvironment sysEnv)
		throws SDMSException
	{
	}

	private void do_test6(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SystemEnvironment.sched.notifyChange (sysEnv, (SDMSScope) null, SchedulingThread.REGISTER);
	}

	private void do_test7(SystemEnvironment sysEnv)
		throws SDMSException
	{

		Long x = null;
		if(x.intValue() == 0) {
			SDMSThread.doTrace(env, "x = " + x, SDMSThread.SEVERITY_INFO);
		}
	}

	private void do_test8(SystemEnvironment sysEnv)
		throws SDMSException
	{
		throw new RuntimeException("Absichtlicher Crash");
	}

	private void do_test9(SystemEnvironment sysEnv)
		throws SDMSException
	{
		long low = Long.MAX_VALUE;
		long totlow = Long.MAX_VALUE;

		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Vector data = new Vector();

		HashMap stat;
		Vector tdata;
		Vector tdesc;

		desc.add("sysEnv.roTxList.first(sysEnv)");
		data.add(sysEnv.roTxList.first(sysEnv));

		desc.add("sysEnv.seVersionList.first(sysEnv)");
		data.add(sysEnv.seVersionList.first(sysEnv));

		desc.add("VERSIONED_PURGE_SET");

		tdesc = new Vector();
		tdata = new Vector();

		stat = sysEnv.vPurgeSet.stat();

		tdesc.add("SIZE");
		tdata.add(stat.get(SDMSPurgeSet.STAT_SIZE));

		data.add(new SDMSOutputContainer(sysEnv, null, tdesc, tdata));

		desc.add("NONVERSIONED_PURGE_SET");

		tdesc = new Vector();
		tdata = new Vector();

		stat = sysEnv.nvPurgeSet.stat();

		tdesc.add("SIZE");
		tdata.add(stat.get(SDMSPurgeSet.STAT_SIZE));

		data.add(new SDMSOutputContainer(sysEnv, null, tdesc, tdata));

		desc.add("TABLES");

		Vector t_desc = new Vector();
		t_desc.add("TABLE_NAME");
		t_desc.add("COUNT_IDS");
		t_desc.add("COUNT_VERSIONS");
		t_desc.add("LOW_VERSION");
		t_desc.add("MAX_VERSIONS");

		SDMSOutputContainer t_container = new SDMSOutputContainer(sysEnv, null, t_desc);

		long lowVersion = Long.MAX_VALUE;
		long countIds = 0;
		long maxVersions = 0;
		long countVersions = 0;

		Iterator it = sysEnv.repository.getTableIterator(sysEnv);
		while (it.hasNext()) {
			SDMSTable table = (SDMSTable)(it.next());
			Vector t_data = new Vector();
			t_data.add(table.tableName());
			stat = table.stat(sysEnv);
			long tIdCount = ((Long)(stat.get(SDMSTable.STAT_ID_COUNT))).longValue();
			t_data.add(new Long(tIdCount));
			countIds += tIdCount;
			long tVersionsCount = ((Long)(stat.get(SDMSTable.STAT_VERSION_COUNT))).longValue();
			t_data.add(new Long(tVersionsCount));
			countVersions += tVersionsCount;
			long tLowVersion = ((Long)(stat.get(SDMSTable.STAT_LOW_VERSION))).longValue();
			if (tLowVersion == Long.MAX_VALUE)
				t_data.add("Long.MAX_VALUE");
			else
				t_data.add(new Long(tLowVersion));
			if (tLowVersion < lowVersion) lowVersion = tLowVersion;
			long tMaxVersions = ((Long)(stat.get(SDMSTable.STAT_MAX_VERSIONS))).longValue();
			t_data.add(new Long(tMaxVersions));
			if (tMaxVersions > maxVersions) maxVersions = tMaxVersions;

			t_container.addData(sysEnv, t_data);
		}

		Collections.sort(t_container.dataset, t_container.getComparator(sysEnv, 0));

		data.add(t_container);

		d_container = new SDMSOutputContainer(sysEnv,
			new SDMSMessage(sysEnv, "03212191002", "Memory Data Statistics"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03212191003", "Memory Data Statistics shown"));
	}

}

