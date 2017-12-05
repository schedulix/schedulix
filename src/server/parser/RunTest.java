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
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.locking.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.util.*;

public class RunTest extends Node
{

	private int testid;
	private Long objectId;
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

	public RunTest(Integer id, Long objectId)
	{
		super();
		testid = id.intValue();
		str = null;
		str2 = null;
		this.objectId = objectId;
	}

	public RunTest(Integer id, String s, Long objectId)
	{
		super();
		testid = id.intValue();
		str = s;
		str2 = null;
		this.objectId = objectId;
		txMode = SDMSTransaction.READWRITE;
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
		case 1:
			do_test1(sysEnv);
			break;
		case 2:
			do_test2(sysEnv);
			break;
		case 3:
			do_test3(sysEnv);
			break;
		case 4:
			do_test4(sysEnv);
			break;
		case 5:
			do_test5(sysEnv);
			break;
		case 6:
			do_test6(sysEnv);
			break;
		case 7:
			do_test7(sysEnv);
			break;
		case 8:
			do_test8(sysEnv);
			break;
		case 9:
			do_test9(sysEnv);
			break;
		case 10:
			do_test10(sysEnv);
			break;
		case 11:
			do_test11(sysEnv);
			break;
		case 12:
			do_test12(sysEnv);
			break;
		case 13:
			do_test13(sysEnv);
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

	private void do_test10(SystemEnvironment sysEnv)
		throws SDMSException
	{
		SDMSTable t = sysEnv.repository.getTableByName(str);
		if (t == null) {
			result.setFeedback(new SDMSMessage(sysEnv, "03212191003", "Table " + str + " not found!"));
			return;
		}
		SDMSVersions v = t.getVersions(objectId);
		if (v == null) {
			boolean exist = t.contains(objectId);
			result.setFeedback(new SDMSMessage(sysEnv, "03212191003", "Object with id " + objectId + "(contains = " + exist + ") not found!"));
			return;
		}
		System.out.println(v.toString());

		result.setFeedback(new SDMSMessage(sysEnv, "03212191003", "Versions dumped to stdout"));
	}

	private void do_test11(SystemEnvironment sysEnv)
	throws SDMSException
	{
		LockingSystemSynchronized.dump();
	}

	private void do_test12(SystemEnvironment sysEnv)
	throws SDMSException
	{
		Map.Entry<String,Long>[] m = de.independit.scheduler.server.exception.DeadlockException.getStackTraces(10);
		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();
		Vector data = new Vector();

		desc.add("Stack Traces");

		Vector t_desc = new Vector();
		t_desc.add("Stack Trace");
		t_desc.add("Count");
		SDMSOutputContainer t_container = new SDMSOutputContainer(sysEnv, null, t_desc);

		Vector t_data;

		for (int i = 0; i < m.length; ++i) {
			if (m[i] == null) continue;
			t_data = new Vector();
			t_data.add(m[i].getKey());
			t_data.add(m[i].getValue());
			t_container.addData(sysEnv, t_data);
		}
		data.add(t_container);

		d_container = new SDMSOutputContainer(sysEnv,
		                                      new SDMSMessage(sysEnv, "03212191004", "Deadlock Stack Traces"), desc, data);

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03212191005", "Deadlock Stack Traces shown"));
	}

	private boolean repairSmeCounts(SystemEnvironment sysEnv, SDMSSubmittedEntity sme, HashSet hiProcessed)
	throws SDMSException
	{
		boolean changedSomething = false;

		int fixSubmitted = 0;
		int fixDependencyWait = 0;
		int fixSynchronizeWait = 0;
		int fixResourceWait = 0;
		int fixRunnable = 0;
		int fixStarting = 0;
		int fixStarted = 0;
		int fixRunning = 0;
		int fixToKill = 0;
		int fixKilled = 0;
		int fixCancelled = 0;
		int fixFinished = 0;
		int fixFinal = 0;
		int fixBrokenActive = 0;
		int fixBrokenFinished = 0;
		int fixError = 0;
		int fixUnreachable = 0;
		int fixRestartable = 0;
		int fixWarn = 0;
		int fixChildSuspended = 0;
		int fixPending = 0;

		int cntSubmitted = sme.getCntSubmitted(sysEnv).intValue();
		int cntDependencyWait = sme.getCntDependencyWait(sysEnv).intValue();
		int cntSynchronizeWait = sme.getCntSynchronizeWait(sysEnv).intValue();
		int cntResourceWait = sme.getCntResourceWait(sysEnv).intValue();
		int cntRunnable = sme.getCntRunnable(sysEnv).intValue();
		int cntStarting = sme.getCntStarting(sysEnv).intValue();
		int cntStarted = sme.getCntStarted(sysEnv).intValue();
		int cntRunning = sme.getCntRunning(sysEnv).intValue();
		int cntToKill = sme.getCntToKill(sysEnv).intValue();
		int cntKilled = sme.getCntKilled(sysEnv).intValue();
		int cntCancelled = sme.getCntCancelled(sysEnv).intValue();
		int cntFinished = sme.getCntFinished(sysEnv).intValue();
		int cntFinal = sme.getCntFinal(sysEnv).intValue();
		int cntBrokenActive = sme.getCntBrokenActive(sysEnv).intValue();
		int cntBrokenFinished = sme.getCntBrokenFinished(sysEnv).intValue();
		int cntError = sme.getCntError(sysEnv).intValue();
		int cntUnreachable = sme.getCntUnreachable(sysEnv).intValue();
		int cntRestartable = sme.getCntRestartable(sysEnv).intValue();

		Vector cv = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, sme.getId(sysEnv));
		SDMSHierarchyInstance hi;
		for (int i = 0; i < cv.size(); ++i) {
			hi = (SDMSHierarchyInstance) cv.get(i);
			Long hiId = hi.getId(sysEnv);
			if (hiProcessed.contains(hiId))
				continue;
			else
				hiProcessed.add(hiId);
			SDMSSubmittedEntity csme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
			changedSomething = changedSomething || repairSmeCounts(sysEnv, csme, hiProcessed);

			fixSubmitted += csme.getCntSubmitted(sysEnv).intValue();
			fixDependencyWait += csme.getCntDependencyWait(sysEnv).intValue();
			fixSynchronizeWait += csme.getCntSynchronizeWait(sysEnv).intValue();
			fixResourceWait += csme.getCntResourceWait(sysEnv).intValue();
			fixRunnable += csme.getCntRunnable(sysEnv).intValue();
			fixStarting += csme.getCntStarting(sysEnv).intValue();
			fixStarted += csme.getCntStarted(sysEnv).intValue();
			fixRunning += csme.getCntRunning(sysEnv).intValue();
			fixToKill += csme.getCntToKill(sysEnv).intValue();
			fixKilled += csme.getCntKilled(sysEnv).intValue();
			fixCancelled += csme.getCntCancelled(sysEnv).intValue();
			fixFinished += csme.getCntFinished(sysEnv).intValue();
			fixFinal += csme.getCntFinal(sysEnv).intValue();
			fixBrokenActive += csme.getCntBrokenActive(sysEnv).intValue();
			fixBrokenFinished += csme.getCntBrokenFinished(sysEnv).intValue();
			fixError += csme.getCntError(sysEnv).intValue();
			fixUnreachable += csme.getCntUnreachable(sysEnv).intValue();
			fixRestartable += csme.getCntRestartable(sysEnv).intValue();

			int cstate = csme.getState(sysEnv);
			switch (cstate) {
				case SDMSSubmittedEntity.SUBMITTED:
					fixSubmitted += 1;
					break;
				case SDMSSubmittedEntity.DEPENDENCY_WAIT:
					fixDependencyWait += 1;
					break;
				case SDMSSubmittedEntity.SYNCHRONIZE_WAIT:
					fixSynchronizeWait += 1;
					break;
				case SDMSSubmittedEntity.RESOURCE_WAIT:
					fixResourceWait += 1;
					break;
				case SDMSSubmittedEntity.RUNNABLE:
					fixRunnable += 1;
					break;
				case SDMSSubmittedEntity.STARTING:
					fixStarting += 1;
					break;
				case SDMSSubmittedEntity.STARTED:
					fixStarted += 1;
					break;
				case SDMSSubmittedEntity.RUNNING:
					fixRunning += 1;
					break;
				case SDMSSubmittedEntity.TO_KILL:
					fixToKill += 1;
					break;
				case SDMSSubmittedEntity.KILLED:
					fixKilled += 1;
					break;
				case SDMSSubmittedEntity.CANCELLED:
					fixCancelled += 1;
					break;
				case SDMSSubmittedEntity.FINISHED:
					fixFinished += 1;
					break;
				case SDMSSubmittedEntity.FINAL:
					fixFinal += 1;
					break;
				case SDMSSubmittedEntity.BROKEN_ACTIVE:
					fixBrokenActive += 1;
					break;
				case SDMSSubmittedEntity.BROKEN_FINISHED:
					fixBrokenFinished += 1;
					break;
				case SDMSSubmittedEntity.ERROR:
					fixError += 1;
					break;
				case SDMSSubmittedEntity.UNREACHABLE:
					fixUnreachable += 1;
					break;
			}
			if (csme.getJobIsRestartable(sysEnv).booleanValue()) fixRestartable += 1;
		}

		if (fixSubmitted != cntSubmitted) {
			sme.setCntSubmitted(sysEnv, fixSubmitted);
			changedSomething = true;
		}
		if (fixDependencyWait != cntDependencyWait) {
			sme.setCntDependencyWait(sysEnv, fixDependencyWait);
			changedSomething = true;
		}
		if (fixSynchronizeWait != cntSynchronizeWait) {
			sme.setCntSynchronizeWait(sysEnv, fixSynchronizeWait);
			changedSomething = true;
		}
		if (fixResourceWait != cntResourceWait) {
			sme.setCntResourceWait(sysEnv, fixResourceWait);
			changedSomething = true;
		}
		if (fixRunnable != cntRunnable) {
			sme.setCntRunnable(sysEnv, fixRunnable);
			changedSomething = true;
		}
		if (fixStarting != cntStarting) {
			sme.setCntStarting(sysEnv, fixStarting);
			changedSomething = true;
		}
		if (fixStarted != cntStarted) {
			sme.setCntStarted(sysEnv, fixStarted);
			changedSomething = true;
		}
		if (fixRunning != cntRunning) {
			sme.setCntRunning(sysEnv, fixRunning);
			changedSomething = true;
		}
		if (fixToKill != cntToKill) {
			sme.setCntToKill(sysEnv, fixToKill);
			changedSomething = true;
		}
		if (fixKilled != cntKilled) {
			sme.setCntKilled(sysEnv, fixKilled);
			changedSomething = true;
		}
		if (fixCancelled != cntCancelled) {
			sme.setCntCancelled(sysEnv, fixCancelled);
			changedSomething = true;
		}
		if (fixFinished != cntFinished) {
			sme.setCntFinished(sysEnv, fixFinished);
			changedSomething = true;
		}
		if (fixFinal != cntFinal) {
			sme.setCntFinal(sysEnv, fixFinal);
			changedSomething = true;
		}
		if (fixBrokenActive != cntBrokenActive) {
			sme.setCntBrokenActive(sysEnv, fixBrokenActive);
			changedSomething = true;
		}
		if (fixBrokenFinished != cntBrokenFinished) {
			sme.setCntBrokenFinished(sysEnv, fixBrokenFinished);
			changedSomething = true;
		}
		if (fixError != cntError) {
			sme.setCntError(sysEnv, fixError);
			changedSomething = true;
		}
		if (fixUnreachable != cntUnreachable) {
			sme.setCntUnreachable(sysEnv, fixUnreachable);
			changedSomething = true;
		}
		if (fixRestartable != cntRestartable) {
			sme.setCntRestartable(sysEnv, fixRestartable);
			changedSomething = true;
		}

		sme.setCntWarn(sysEnv, 0);

		if (changedSomething) {
			System.out.println("------------------------------------------------------------");
			System.out.println("Setting following values for sme " + sme.getId(sysEnv) + ":");
			System.out.println("fixSubmitted = " + fixSubmitted + " (old = " + cntSubmitted + ")");
			System.out.println("fixDependencyWait = " + fixDependencyWait + " (old = " + cntDependencyWait + ")");
			System.out.println("fixSynchronizeWait = " + fixSynchronizeWait + " (old = " + cntSynchronizeWait + ")");
			System.out.println("fixResourceWait = " + fixResourceWait + " (old = " + cntResourceWait + ")");
			System.out.println("fixRunnable = " + fixRunnable + " (old = " + cntRunnable + ")");
			System.out.println("fixStarting = " + fixStarting + " (old = " + cntStarting + ")");
			System.out.println("fixStarted = " + fixStarted + " (old = " + cntStarted + ")");
			System.out.println("fixRunning = " + fixRunning + " (old = " + cntRunning + ")");
			System.out.println("fixToKill = " + fixToKill + " (old = " + cntToKill + ")");
			System.out.println("fixKilled = " + fixKilled + " (old = " + cntKilled + ")");
			System.out.println("fixCancelled = " + fixCancelled + " (old = " + cntCancelled + ")");
			System.out.println("fixFinished = " + fixFinished + " (old = " + cntFinished + ")");
			System.out.println("fixFinal = " + fixFinal + " (old = " + cntFinal + ")");
			System.out.println("fixBrokenActive = " + fixBrokenActive + " (old = " + cntBrokenActive + ")");
			System.out.println("fixBrokenFinished = " + fixBrokenFinished + " (old = " + cntBrokenFinished + ")");
			System.out.println("fixError = " + fixError + " (old = " + cntError + ")");
			System.out.println("fixUnreachable = " + fixUnreachable + " (old = " + cntUnreachable + ")");
			System.out.println("fixRestartable = " + fixRestartable + " (old = " + cntRestartable + ")");
		}

		return changedSomething;

	}

	private void tryFinalize(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		Vector cv = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, sme.getId(sysEnv));
		SDMSHierarchyInstance hi;
		for (int i = 0; i < cv.size(); ++i) {
			hi = (SDMSHierarchyInstance) cv.get(i);
			SDMSSubmittedEntity csme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
			tryFinalize(sysEnv, csme);
		}
		if (sme.getIsCancelled(sysEnv).booleanValue() && sme.getState(sysEnv).intValue() == SDMSSubmittedEntity.FINISHED)
			sme.doDeferredCancel(sysEnv);
	}

	private void do_test13(SystemEnvironment sysEnv)
	throws SDMSException
	{
		SDMSSubmittedEntity sme;
		sme = SDMSSubmittedEntityTable.getObject(sysEnv, objectId);
		Long seVersion = sme.getSeVersion(sysEnv);
		HashSet hiProcessed = new HashSet();

		if (repairSmeCounts(sysEnv, sme, hiProcessed)) {
			tryFinalize(sysEnv, sme);
			result.setFeedback(new SDMSMessage(sysEnv, "03711230834", "SME " + objectId + " fixed"));
		} else {
			result.setFeedback(new SDMSMessage(sysEnv, "03711230834", "SME " + objectId + " OK"));
		}
	}
}

