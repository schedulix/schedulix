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
import de.independit.scheduler.server.output.*;

public class ShowCriticalPath extends Node
{
	Long jobId;
	int delta = 0;
	boolean isGlobal = false;

	HashMap<Long, String> masterNameMap = new HashMap<Long, String> ();
	HashMap<Long, String> masterTypeMap = new HashMap<Long, String> ();
	HashMap<Long, String> tagsMap = new HashMap<Long, String> ();

	public ShowCriticalPath (Long jid, WithHash with)
	{
		this.jobId = jid;
		txMode = SDMSTransaction.READONLY;
		auditFlag = false;
		if (with != null) {
			if (with.containsKey(ParseStr.S_GLOBAL)) {
				isGlobal = true;
			}
			if (with.containsKey(ParseStr.S_DELTA)) {
				delta = ((Integer) with.get(ParseStr.S_DELTA)).intValue();
			} else {
				delta = 0;
			}
		}
		if (delta <= 0)
			delta = 0;
		else
			delta *= 1000;
	}

	SDMSSubmittedEntity getLastFinalJobId (SystemEnvironment sysEnv, Long smeId)
	throws SDMSException
	{
		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, smeId);
		Long latestFinalTs = new Long(0);
		if (sme.getState(sysEnv) == SDMSSubmittedEntity.FINAL) {
			Vector cld_v = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, smeId);
			Iterator cld_i = cld_v.iterator();
			while (cld_i.hasNext()) {
				SDMSHierarchyInstance hi = (SDMSHierarchyInstance)cld_i.next();
				SDMSSubmittedEntity childSme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));

				if (childSme.getState(sysEnv) != SDMSSubmittedEntity.FINAL) {
					continue;
				}

				long finalTs = (childSme.getFinalTs(sysEnv).longValue() / 1000) * 1000;
				if (finalTs > latestFinalTs) {
					sme = childSme;
					latestFinalTs = finalTs;
				}
			}
			if (latestFinalTs > 0) {
				return getLastFinalJobId(sysEnv, sme.getId(sysEnv));
			}
		}
		return sme;
	}

	private static String formatTs (SystemEnvironment sysEnv, Long ts)
	{
		GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
		TimeZone gmtTz = SystemEnvironment.systemTimeZone;
		Date d = new Date();
		d.setTime(ts.longValue());
		return sysEnv.systemDateFormat.format(d);
	}

	private String collectTags(SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		Long smeId = sme.getId(sysEnv);
		String tags = tagsMap.get(smeId);
		if (tags == null) {
			tags = "";
			String childTag = sme.getChildTag(sysEnv);
			if (childTag != null) {
				tags = childTag;
			}
			String parentTags = "";
			Long parentSmeId = sme.getParentId(sysEnv);
			if (parentSmeId != null) {
				SDMSSubmittedEntity parentSme = SDMSSubmittedEntityTable.getObject(sysEnv, parentSmeId);
				parentTags = collectTags(sysEnv, parentSme);
			}
			if (!tags.equals("") && !parentTags.equals("")) {
				tags = tags + ",";
			}
			tags = tags + parentTags;
			tagsMap.put(smeId, tags);
		}
		return tags;
	}

	String formatDuration (Long duration)
	{
		if (duration == null) {
			return "0";
		} else {
			return new Long(Math.floorDiv(duration, 1000)).toString();
		}
	}

	private Vector createRow(SystemEnvironment sysEnv, PathNode node)
	throws SDMSException
	{
		Long readyTs;
		Integer seType = node.se.getType(sysEnv);
		Integer smeState = node.sme.getState(sysEnv);
		String outputState = node.sme.getStateAsString(sysEnv);
		Integer rerunSeq = node.sme.getRerunSeq(sysEnv);
		if (seType == SDMSSchedulingEntity.JOB) {
			readyTs = node.sme.getSyncTs(sysEnv);
			if (rerunSeq > 0) {
				Vector v = SDMSSubmittedEntityStatsTable.idx_smeId.getVector(sysEnv, node.sme.getId(sysEnv));
				for (int i = 0; i < v.size(); i++) {
					final SDMSSubmittedEntityStats s = (SDMSSubmittedEntityStats) v.get(i);
					if (s.getRerunSeq(sysEnv) == 0) {
						readyTs = s.getSyncTs(sysEnv);
						break;
					}
				}
			}
		} else {
			readyTs = node.sme.getFinishTs(sysEnv);
			if (smeState == SDMSSubmittedEntity.FINISHED) {
				outputState = "ACTIVE";
			}
		}
		Vector v = new Vector();

		v.add(node.rowType == PathNode.PATH ? "PATH" : "INFO");
		v.add(formatTs(sysEnv, readyTs));
		v.add(node.sme.getId(sysEnv));
		v.add(outputState);
		if (node.di != null && node.di.getIgnore(sysEnv) != SDMSDependencyInstance.NO) {
			v.add(formatTs(sysEnv, node.di.getIgnoreTs(sysEnv)));
		} else {
			v.add("");
		}
		v.add(node.se.pathString(sysEnv, node.sme.getSeVersion(sysEnv)));
		String tags = collectTags(sysEnv, node.sme);
		if (node.sme.getChildTag(sysEnv) != null) {
			tags = "!" + tags;
		}
		v.add(tags);
		v.add(node.se.getTypeAsString(sysEnv));
		if (rerunSeq > 0) {
			v.add(rerunSeq);
		} else {
			v.add("");
		}
		Long startTs = null;
		if (seType == SDMSSchedulingEntity.JOB) {
			startTs = node.sme.getStartTs(sysEnv);
		} else {
			startTs = node.sme.getFinishTs(sysEnv);
		}
		if (startTs != null) {
			v.add(formatTs(sysEnv, startTs));
		} else {
			v.add("");
		}
		Long finishTs = node.sme.getFinishTs(sysEnv);
		if (finishTs != null && seType == SDMSSchedulingEntity.JOB) {
			v.add(formatTs(sysEnv, finishTs));
		} else {
			v.add("");
		}
		Long finalTs = node.sme.getFinalTs(sysEnv);
		if (finalTs != null ) {
			v.add(formatTs(sysEnv, finalTs));
		} else {
			v.add("");
		}
		Long runTime = null;
		if (seType == SDMSSchedulingEntity.JOB && finishTs != null && startTs != null) {
			runTime = finishTs - startTs;
			v.add(formatDuration(runTime));
		} else {
			v.add("");
		}
		if (seType == SDMSSchedulingEntity.JOB && finalTs != null && runTime != null) {
			v.add(formatDuration(finalTs - readyTs - runTime));
		} else {
			v.add("");
		}
		if (finalTs != null) {
			Long totalTime = finalTs - readyTs;
			v.add(formatDuration(totalTime));
		} else {
			v.add("");
		}
		Long suspendTime = new Long (node.sme.evaluateTime(sysEnv, node.sme.getSuspendTime(sysEnv), node.sme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_SUSPEND));
		v.add(suspendTime);
		if (node.contTs != null ) {
			v.add(formatTs(sysEnv, node.contTs));
		} else {
			v.add("");
		}
		if (node.contType == PathNode.ALL_FINAL) {
			v.add ("ALL_FINAL");
		} else if (node.contType == PathNode.JOB_FINAL) {
			v.add ("JOB_FINAL");
		} else {
			v.add ("IGNORE");
		}

		Long masterId = node.sme.getMasterId(sysEnv);
		v.add(masterId);
		String masterName = masterNameMap.get(masterId);
		String masterType = masterTypeMap.get(masterId);
		if (masterName == null) {
			try {
				SDMSSubmittedEntity  masterSme = SDMSSubmittedEntityTable.getObject(sysEnv, masterId);
				SDMSSchedulingEntity masterSe  = SDMSSchedulingEntityTable.getObject(sysEnv, masterSme.getSeId(sysEnv), masterSme.getSeVersion(sysEnv));
				masterName = masterSe.pathString(sysEnv, masterSme.getSeVersion(sysEnv));
				masterType = masterSe.getTypeAsString(sysEnv);
				masterNameMap.put(masterId, masterName);
				masterTypeMap.put(masterId, masterType);
			} catch (NotFoundException nfe) {
				masterName = "Master already purged";
				masterType = "";
			}
		}
		v.add(masterName);
		v.add(masterType);
		v.add(String.join(",", node.pathMembers));

		return v;
	}

	private void addPathNodes (SystemEnvironment sysEnv, Vector<PathNode> pathNodes, HashSet<PathNode> rowsSeen, PathNode node, String indent)
	throws SDMSException
	{
		if (!rowsSeen.contains(node)) {
			rowsSeen.add(node);

			pathNodes.add(node);
			Iterator edges_i = node.prevPathNodes.iterator();
			while(edges_i.hasNext()) {
				PathNode edge = (PathNode)edges_i.next();
				addPathNodes (sysEnv, pathNodes, rowsSeen, edge, indent + "    ");
			}
		}
	}

	public void go(SystemEnvironment sysEnv)
	throws SDMSException
	{
		HashMap<Long, PathNode> evaluated = new HashMap <Long, PathNode>();
		PathNodeFactory pnf = new PathNodeFactory();

		SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, jobId);

		PathNode rootNode = pnf.getOrCreatePathNode (sysEnv, PathNode.ROOT, null, null, null );

		if (sme.getState(sysEnv) == SDMSSubmittedEntity.FINAL) {
			Vector<PathNode> criticalPathLeafs = rootNode.getLatestLeafPathNodes (sysEnv, sme, delta, isGlobal);
			rootNode.addPrevPathNodes(sysEnv, criticalPathLeafs);
			Iterator<PathNode> i_cpl = criticalPathLeafs.iterator();
			int size = criticalPathLeafs.size();
			while (i_cpl.hasNext()) {
				PathNode pathNode = i_cpl.next();
				pathNode.evaluate (sysEnv, delta, isGlobal, evaluated, rootNode);
				pathNode.pathMembers.set(0, pathNode.pathMembers.elementAt(0) + "[END]");
			}
		}

		Vector<PathNode> pathNodes = new Vector<PathNode>();
		Iterator<PathNode> i_startNodes = rootNode.prevPathNodes.iterator();
		HashSet<PathNode> rowsSeen = new HashSet<PathNode>();
		while (i_startNodes.hasNext()) {
			PathNode startNode = i_startNodes.next();
			addPathNodes (sysEnv, pathNodes, rowsSeen, startNode, "");
		}

		int rownum = 0;
		Iterator<PathNode> i_pn = pathNodes.iterator();
		while (i_pn.hasNext()) {
			PathNode pn = i_pn.next();
			rownum++;
			pn.rownum = rownum;
		}
		Collections.sort (pathNodes, new criticalPathComperator(sysEnv) );

		SDMSOutputContainer d_container = null;
		Vector desc = new Vector();

		desc.add("ROW_TYPE");
		desc.add("READY_TS");
		desc.add("SME_ID");
		desc.add("STATE");
		desc.add("IGNORE_TS");
		desc.add("SE_NAME");
		desc.add("TAGS");
		desc.add("TYPE");
		desc.add("RERUNS");
		desc.add("START_TS");
		desc.add("FINISH_TS");
		desc.add("FINAL_TS");
		desc.add("RUNTIME");
		desc.add("EXTRA_TIME");
		desc.add("TOTAL_TIME");
		desc.add("SUSPEND_TIME");
		desc.add("CONT_TS");
		desc.add("CONT_TYPE");
		desc.add("MASTER_ID");
		desc.add("MASTER_NAME");
		desc.add("MASTER_TYPE");
		desc.add("PATH_MEMBERSHIP");

		d_container = new SDMSOutputContainer(sysEnv, new SDMSMessage(sysEnv, "03405101107", "Critical Path"), desc);
		i_pn = pathNodes.iterator();
		while (i_pn.hasNext()) {
			d_container.addData(sysEnv, createRow(sysEnv, i_pn.next()));
		}

		result.setOutputContainer(d_container);

		result.setFeedback(new SDMSMessage(sysEnv, "03405101108", "Critical Path shown"));
	}
}

class criticalPathComperator
	implements Comparator
{
	SystemEnvironment sysEnv;

	public criticalPathComperator (SystemEnvironment sysEnv)
	throws SDMSException
	{
		this.sysEnv = sysEnv;
	}

	public int compare (Object o1, Object o2)
	{
		PathNode p1 = (PathNode)o1;
		PathNode p2 = (PathNode)o2;

		if (p1.contTs < p2.contTs) {
			return -1;
		}
		if (p1.contTs > p2.contTs) {
			return 1;
		}

		if (p1.pathMembers.elementAt(0).length() > p2.pathMembers.elementAt(0).length() ) {
			return -1;
		}
		if (p1.pathMembers.elementAt(0).length() < p2.pathMembers.elementAt(0).length() ) {
			return 1;
		}

		int cmp = p1.pathMembers.elementAt(0).compareTo(p2.pathMembers.elementAt(0));
		if (cmp != 0) {
			return -cmp;
		}

		if (p1.rownum < p2.rownum) {
			return 1;
		}
		if (p1.rownum > p2.rownum) {
			return -1;
		}

		return 0;
	}
}

class PathNodeHashKey
{
	Long smeId;
	Long contTs;

	public PathNodeHashKey (Long smeId, Long contTs)
	{
		this.smeId = smeId;
		this.contTs = contTs;
	}

	@Override
	public int hashCode()
	{
		int hash = Objects.hash(smeId, contTs);
		return hash;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return (this.smeId.longValue()  == ((PathNodeHashKey)o).smeId.longValue() &&
		        this.contTs.longValue() == ((PathNodeHashKey)o).contTs.longValue());
	}

	public String toString()
	{
		return "smeId:" + smeId + ", contTs:"+ contTs;
	}
}

class PathNodeFactory
{

	private HashMap<PathNodeHashKey, PathNode> createdPathNodes;

	public PathNodeFactory()
	{
		createdPathNodes = new HashMap<PathNodeHashKey, PathNode>();
	}

	public PathNode getOrCreatePathNode (SystemEnvironment sysEnv, int rowType, SDMSSubmittedEntity sme, SDMSDependencyInstance di, Long contTs)
	throws SDMSException
	{
		PathNode pathNode = null;

		if (rowType == PathNode.ROOT) {
			pathNode = new PathNode (sysEnv, rowType, sme, di, this);
			return pathNode;
		}
		Long smeId = sme.getId(sysEnv);
		PathNodeHashKey hashKey = new PathNodeHashKey(smeId, contTs);
		pathNode = createdPathNodes.get(hashKey);
		if (pathNode == null) {
			pathNode =  new PathNode (sysEnv, rowType, sme, di, this);
			pathNode.contTs = contTs;
			createdPathNodes.put(hashKey, pathNode);
		} else {
		}
		return pathNode;
	}
}

class PathNode
{

	public static final int ROOT = 0;
	public static final int PATH = 1;
	public static final int INFO = 2;

	public static final int ALL_FINAL = 0;
	public static final int JOB_FINAL = 1;
	public static final int IGNORE    = 2;

	public int rowType;
	public SDMSSubmittedEntity sme;
	public SDMSSchedulingEntity se;
	public Vector<PathNode> prevPathNodes;
	public SDMSDependencyInstance di;
	public Vector<String> pathMembers;
	public PathNodeFactory pnf;
	public int rownum;
	public Long contTs;
	public Integer contType;

	static HashMap<Long, PathNode> createdPathNodes = new HashMap<Long, PathNode>();

	public PathNode (SystemEnvironment sysEnv, int rowType, SDMSSubmittedEntity sme, SDMSDependencyInstance di, PathNodeFactory pnf)
	throws SDMSException
	{
		this.pnf = pnf;
		this.rowType = rowType;
		this.sme = sme;
		prevPathNodes = new Vector<PathNode>();
		pathMembers = new Vector<String>();
		if (rowType != ROOT) {
			this.se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
		} else {
			this.se = null;
			this.pathMembers.add("1");
		}
		this.di = di;
		this.contTs = null;
		this.contType = null;
	}

	public void addPrevPathNodes(SystemEnvironment sysEnv, Vector<PathNode> prevPathNodes)
	throws SDMSException
	{

		if (prevPathNodes.size() == 1) {
			addPrevPathNode(sysEnv, prevPathNodes.get(0));
			return;
		}
		if (this.rowType == PathNode.ROOT) {
			int pathNum = 0;
			for (int i = 0; i < prevPathNodes.size(); ++i) {
				PathNode curNode = prevPathNodes.get(i);
				this.prevPathNodes.add(curNode);
				pathNum++;
				curNode.pathMembers.add("" + pathNum);
			}
		} else {
			String base = this.pathMembers.get(0);
			for (int i = 0; i < prevPathNodes.size(); ++i) {
				PathNode curNode = prevPathNodes.get(i);
				this.prevPathNodes.add(curNode);
				curNode.pathMembers.add(base + "." + (i + 1));
			}
		}
	}

	public void addPrevPathNode(SystemEnvironment sysEnv, PathNode prevPathNode)
	throws SDMSException
	{

		this.prevPathNodes.add(prevPathNode);
		prevPathNode.pathMembers.add(this.pathMembers.get(0));
	}

	public String toString(SystemEnvironment sysEnv)
	throws SDMSException
	{
		String result = "rowType = " + this.rowType + ", sme = " + (this.sme == null ?  "null" : this.sme.getId(sysEnv));
		if (se != null) {
			result += ", se = " + se.pathString(sysEnv);
		} else {
			result += ", se = none";
		}
		if (di != null) {
			result += ", di = " + di.getId(sysEnv);
		} else {
			result += ", di = none";
		}
		result += ", #prevPathNodes =" + prevPathNodes.size();
		result += ", pathMembers =" + String.join(", ", pathMembers);
		return result;
	}

	protected Vector<PathNode> getLeafPathNodesForLatestFinalTs (SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long latestFinalTs, long delta, boolean isGlobal)
	throws SDMSException
	{
		Vector<PathNode> pathNodes = new Vector<PathNode>();

		Vector children = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, sme.getId(sysEnv));
		Iterator children_i = children.iterator();
		while (children_i.hasNext()) {
			SDMSHierarchyInstance hi = (SDMSHierarchyInstance)children_i.next();
			SDMSSubmittedEntity childSme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
			if (childSme.getState(sysEnv) == SDMSSubmittedEntity.CANCELLED) {
				continue;
			}
			Vector<PathNode> qualifyingNodes = getLeafPathNodesForLatestFinalTs (sysEnv, childSme, latestFinalTs, delta, isGlobal);
			if (qualifyingNodes.size() == 0) {
			} else {
				pathNodes.addAll(qualifyingNodes);
			}
		}
		Long finishTs = sme.getFinishTs(sysEnv);
		PathNode tmpPathNode = this.pnf.getOrCreatePathNode (sysEnv, PathNode.PATH, sme, null, finishTs);
		if (tmpPathNode.se.getType(sysEnv) == SDMSSchedulingEntity.JOB || children.size() == 0)  {
			if ((delta > 0 && Math.abs(latestFinalTs - finishTs) <= delta) || (delta == 0 && latestFinalTs <= finishTs)) {

				tmpPathNode.contType = PathNode.ALL_FINAL;
				pathNodes.add(tmpPathNode);
			}
		}
		return pathNodes;
	}

	protected Vector<PathNode> getLatestLeafPathNodes (SystemEnvironment sysEnv, SDMSSubmittedEntity sme, long delta, boolean isGlobal)
	throws SDMSException
	{
		Vector<PathNode> leafPathNodes = null;
		Long latestChildFinalTs = getLatestChildFinalTs (sysEnv, sme);
		if (latestChildFinalTs != null) {
			leafPathNodes = getLeafPathNodesForLatestFinalTs (sysEnv, sme, latestChildFinalTs, delta, isGlobal);
		} else {
			leafPathNodes = new Vector<PathNode>();
			Long contTs = sme.getFinalTs(sysEnv);
			if (contTs == null) {
				contTs = sme.getFinishTs(sysEnv);
			}
			PathNode tmpPathNode = this.pnf.getOrCreatePathNode (sysEnv, PathNode.PATH, sme, null, contTs);

			tmpPathNode.contType = PathNode.ALL_FINAL;
			leafPathNodes.add (tmpPathNode);
		}
		filterTransitiveDependencies(sysEnv, leafPathNodes);
		leafPathNodes.sort(new PathNodeComperator());

		return leafPathNodes;
	}

	protected void filterTransitiveDependencies(SystemEnvironment sysEnv, Vector<PathNode> leafPathNodes)
	throws SDMSException
	{
		HashSet<PathNode> negResult = new HashSet<PathNode>();
		Iterator<PathNode> i_lpn = leafPathNodes.iterator();
		while (i_lpn.hasNext()) {
			PathNode curPN = i_lpn.next();
			Long curSmeId = curPN.sme.getId(sysEnv);
			Vector di_v = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, curSmeId);
			for (int i = 0; i < di_v.size(); ++i) {
				SDMSDependencyInstance di = (SDMSDependencyInstance) di_v.get(i);
				Long reqSmeId = di.getRequiredId(sysEnv);
				Iterator<PathNode> s_lpn = leafPathNodes.iterator();
				while (s_lpn.hasNext()) {
					PathNode srchPN = s_lpn.next();
					if (reqSmeId.equals(srchPN.sme.getId(sysEnv))) {
						negResult.add(srchPN);
					}
					if (reqSmeId.equals(srchPN.sme.getParentId(sysEnv))) {
						negResult.add(srchPN);
					}
				}
			}
		}
		i_lpn = leafPathNodes.iterator();
		while (i_lpn.hasNext()) {
			PathNode curPN = i_lpn.next();
			if (negResult.contains(curPN))
				i_lpn.remove();
		}

	}

	protected Long getLatestChildFinalTs (SystemEnvironment sysEnv, SDMSSubmittedEntity sme)
	throws SDMSException
	{
		if (sme.getState(sysEnv) != SDMSSubmittedEntity.FINAL) {
			return null;
		}
		long latestFinalTs = 0;
		SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv));
		int seType = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), sme.getSeVersion(sysEnv)).getType(sysEnv).intValue();
		if (seType == SDMSSchedulingEntity.JOB) {
			latestFinalTs = sme.getFinishTs(sysEnv).longValue();
		}
		Vector children = SDMSHierarchyInstanceTable.idx_parentId.getVector(sysEnv, sme.getId(sysEnv));
		if (children.size() == 0) {
			if (seType != SDMSSchedulingEntity.JOB) {
				latestFinalTs = (sme.getFinalTs(sysEnv).longValue() / 1000) * 1000;
			}
		} else {
			Iterator children_i = children.iterator();
			while (children_i.hasNext()) {
				SDMSHierarchyInstance hi = (SDMSHierarchyInstance)children_i.next();
				SDMSSubmittedEntity childSme = SDMSSubmittedEntityTable.getObject(sysEnv, hi.getChildId(sysEnv));
				if (childSme.getState(sysEnv) == SDMSSubmittedEntity.CANCELLED) {
					continue;
				}
				Long latestChildFinalTs = getLatestChildFinalTs(sysEnv, childSme);
				if (latestChildFinalTs != null) {
					if (latestChildFinalTs.longValue() > latestFinalTs) {
						latestFinalTs = latestChildFinalTs.longValue();
					}
				}
			}
		}

		return latestFinalTs > 0 ? new Long(latestFinalTs) : null;
	}

	private Vector<PathNode> getRequiredPathNodesForLatestFinalTs (SystemEnvironment sysEnv, HashMap<Long, Vector<SDMSDependencyInstance>> origMap, long latestFinalTs, long delta, boolean isGlobal)
	throws SDMSException
	{
		long seVersion = this.sme.getSeVersion(sysEnv);
		Vector<PathNode> pathNodes = new Vector<PathNode>();
		for (Vector<SDMSDependencyInstance> di_v : origMap.values()) {
			Iterator di_i = di_v.iterator();
			while (di_i.hasNext()) {
				SDMSDependencyInstance di = (SDMSDependencyInstance)di_i.next();
				SDMSSubmittedEntity reqSme = null;
				if (di.getState(sysEnv) != SDMSDependencyInstance.FULFILLED) {
					continue;
				}
				try {
					reqSme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getRequiredId(sysEnv));
				} catch (NotFoundException nfe) {
					continue;
				}
				if (!isGlobal && reqSme.getMasterId(sysEnv) != this.sme.getMasterId(sysEnv)) {
					continue;
				}
				long contTs = 0;
				SDMSDependencyDefinition dd = SDMSDependencyDefinitionTable.getObject(sysEnv, di.getDdId (sysEnv), seVersion);
				int ddMode = dd.getMode (sysEnv).intValue();
				Integer contType = PathNode.ALL_FINAL;

				if (di.getIgnore(sysEnv) != SDMSDependencyInstance.NO) {
					contTs = di.getIgnoreTs(sysEnv);
					contType = new Integer (PathNode.IGNORE);
				} else {
					if (ddMode == SDMSDependencyDefinition.ALL_FINAL) {
						contTs = (reqSme.getFinalTs(sysEnv).longValue() / 1000) * 1000;
						contType = new Integer (PathNode.ALL_FINAL);
					} else {
						contTs = reqSme.getFinishTs(sysEnv).longValue();
						contType = new Integer (PathNode.JOB_FINAL);
					}
				}
				if (reqSme.getMasterId(sysEnv) != this.sme.getMasterId(sysEnv) && contTs <= this.sme.getSubmitTs(sysEnv)) {
					continue;
				}
				if ((delta > 0 && Math.abs(latestFinalTs - contTs) <= delta) || (delta == 0 && latestFinalTs == contTs)) {
					PathNode tmpPathNode = this.pnf.getOrCreatePathNode(sysEnv, PathNode.PATH, reqSme, di, contTs);
					if (tmpPathNode.contType == null || tmpPathNode.contType != PathNode.ALL_FINAL) {

						tmpPathNode.contType = contType;
					}
					pathNodes.add(tmpPathNode);
				}
			}
		}

		return pathNodes;
	}

	private long getLatestRequiredFinalTs (SystemEnvironment sysEnv, HashMap<Long, Vector<SDMSDependencyInstance>> origMap)
	throws SDMSException
	{
		long seVersion = this.sme.getSeVersion(sysEnv);
		long latestFinalTs = 0;
		for (Vector<SDMSDependencyInstance> di_v : origMap.values()) {
			long latestOrigFinalTs = 0;
			long latestIgnoreTs = 0;
			Iterator di_i = di_v.iterator();
			Integer diOperation = SDMSDependencyInstance.AND;
			SDMSDependencyDefinition dd = null;
			while (di_i.hasNext()) {
				SDMSDependencyInstance di = (SDMSDependencyInstance)di_i.next();
				if (di.getState(sysEnv) != SDMSDependencyInstance.FULFILLED) {
					continue;
				}
				if (dd == null) {
					diOperation = di.getDependencyOperation(sysEnv);
					dd = SDMSDependencyDefinitionTable.getObject(sysEnv, di.getDdId (sysEnv), seVersion);
				}
				SDMSSubmittedEntity reqSme = null;
				try {
					reqSme = SDMSSubmittedEntityTable.getObject(sysEnv, di.getRequiredId(sysEnv));
					long reqFinalTs;
					if (dd.getMode (sysEnv) == SDMSDependencyDefinition.JOB_FINAL && reqSme.getJobIsFinal(sysEnv)) {
						if (di.getIgnore(sysEnv) != SDMSDependencyInstance.NO && reqSme.getFinishTs(sysEnv) < di.getIgnoreTs(sysEnv)) {
							continue;
						}
					}
					if (dd.getMode (sysEnv) == SDMSDependencyDefinition.ALL_FINAL && reqSme.getState(sysEnv) == SDMSSubmittedEntity.FINAL) {
						if (di.getIgnore(sysEnv) != SDMSDependencyInstance.NO && reqSme.getFinalTs(sysEnv) < di.getIgnoreTs(sysEnv)) {
							continue;
						}
					}
				} catch (NotFoundException nfe) {
					continue;
				}
				long finalTs = 0;
				if (di.getIgnore (sysEnv) == SDMSDependencyInstance.NO) {
					if (dd.getMode (sysEnv) == SDMSDependencyDefinition.ALL_FINAL) {
						finalTs = (reqSme.getFinalTs(sysEnv).longValue() / 1000) * 1000;
					} else {
						finalTs = reqSme.getFinishTs(sysEnv).longValue();
					}
					if (latestOrigFinalTs == 0) {
						latestOrigFinalTs = finalTs;
					} else {
						if (diOperation == SDMSDependencyInstance.AND) {
							if (finalTs > latestOrigFinalTs) {
								latestOrigFinalTs = finalTs;
							}
						} else {
							if (finalTs < latestOrigFinalTs) {
								latestOrigFinalTs = finalTs;
							}
						}
					}
				} else {
					long ignoreTs = di.getIgnoreTs (sysEnv);
					if (latestIgnoreTs == 0) {
						latestIgnoreTs = ignoreTs;
					} else {
						if (latestIgnoreTs < ignoreTs) {
							latestIgnoreTs = ignoreTs;
						}
					}
				}
			}
			if (diOperation == SDMSDependencyInstance.AND) {
				if (latestIgnoreTs > latestOrigFinalTs) {
					latestOrigFinalTs = latestIgnoreTs;
				}
			} else {
				if (latestOrigFinalTs == 0) {
					latestOrigFinalTs = latestIgnoreTs;
				}
			}

			if (latestOrigFinalTs > latestFinalTs) {
				latestFinalTs = latestOrigFinalTs;
			}
		}

		return latestFinalTs;
	}

	private Vector<PathNode> getLatestRequiredPathNodes (SystemEnvironment sysEnv, HashMap<Long, Vector<SDMSDependencyInstance>> origMap, long delta, boolean isGlobal)
	throws SDMSException
	{
		Long latestRequiredFinalTs = getLatestRequiredFinalTs (sysEnv, origMap);
		if (latestRequiredFinalTs != null) {
			Vector<PathNode> latestRequiredPathNodes = getRequiredPathNodesForLatestFinalTs (sysEnv, origMap, latestRequiredFinalTs, delta, isGlobal);
			latestRequiredPathNodes.sort(new PathNodeComperator());
			return latestRequiredPathNodes;
		} else {
			return new Vector<PathNode>();
		}
	}

	protected void addRequiredPathNodes (SystemEnvironment sysEnv, long delta, boolean isGlobal, HashMap<Long, PathNode> evaluated)
	throws SDMSException
	{
		HashMap<Long, Vector<SDMSDependencyInstance>> origMap = new HashMap<Long, Vector<SDMSDependencyInstance>> ();

		Vector req_v = SDMSDependencyInstanceTable.idx_dependentId.getVector(sysEnv, this.sme.getId(sysEnv));
		Iterator req_i = req_v.iterator();
		while (req_i.hasNext()) {
			SDMSDependencyInstance di = (SDMSDependencyInstance)req_i.next();
			Long dependentIdOrig = di.getDependentIdOrig (sysEnv);
			Vector<SDMSDependencyInstance> di_v = origMap.get(dependentIdOrig);
			if (di_v == null) {
				di_v = new Vector<SDMSDependencyInstance> ();
				origMap.put(dependentIdOrig, di_v);
			}
			di_v.add(di);
		}
		Vector<PathNode> latestRequiredPathNodes = getLatestRequiredPathNodes(sysEnv, origMap, delta, isGlobal);

		this.addPrevPathNodes(sysEnv, latestRequiredPathNodes);

		if (latestRequiredPathNodes.size() == 0) {
			this.pathMembers.set(0, this.pathMembers.elementAt(0) + "[START]");
		} else {
			Iterator<PathNode> i_lrpn = latestRequiredPathNodes.iterator();
			while (i_lrpn.hasNext()) {
				PathNode pathNode = i_lrpn.next();
				pathNode.evaluate (sysEnv, delta, isGlobal, evaluated, this);
			}
		}
	}

	protected void evaluate (SystemEnvironment sysEnv, long delta, boolean isGlobal, HashMap<Long, PathNode> evaluated, PathNode callingPathNode)
	throws SDMSException
	{
		Long thisSmeId = this.sme.getId(sysEnv);
		if (evaluated.containsKey(thisSmeId))
			return;
		evaluated.put(thisSmeId, this);
		Vector<PathNode> criticalPathLeafs = new Vector<PathNode>();

		boolean haveChildren = false;
		Vector<PathNode> childNodes = new Vector<PathNode>();
		if (this.sme.getState (sysEnv) == SDMSSubmittedEntity.FINAL &&
		    this.sme.getCntFinal(sysEnv) > 0 &&
		    (this.di == null || SDMSDependencyDefinitionTable.getObject(sysEnv, this.di.getDdId(sysEnv),sme.getSeVersion(sysEnv)).getMode(sysEnv) == SDMSDependencyDefinition.ALL_FINAL)) {
			criticalPathLeafs = getLatestLeafPathNodes (sysEnv, this.sme, delta, isGlobal);
			this.addPrevPathNodes(sysEnv, criticalPathLeafs);
			Iterator<PathNode> i_cpl = criticalPathLeafs.iterator();
			while (i_cpl.hasNext()) {
				haveChildren = true;
				PathNode pathNode = i_cpl.next();
				PathNode preEvaluatedPathNode = evaluated.get(pathNode.sme.getId(sysEnv));
				if (preEvaluatedPathNode == null) {
					if (!pathNode.sme.getId(sysEnv).equals(this.sme.getId(sysEnv))) {
						pathNode.evaluate (sysEnv, delta, isGlobal, evaluated, this);
					}
				} else {
					if (!pathNode.sme.getId(sysEnv).equals(this.sme.getId(sysEnv))) {
					}
				}
			}
		}
		if (!haveChildren) {
			this.addRequiredPathNodes (sysEnv, delta, isGlobal, evaluated);
		}
	}

	private static String formatTs (SystemEnvironment sysEnv, Long ts)
	{
		GregorianCalendar gc = SystemEnvironment.newGregorianCalendar();
		TimeZone gmtTz = SystemEnvironment.systemTimeZone;
		Date d = new Date();
		d.setTime(ts.longValue());
		return sysEnv.systemDateFormat.format(d);
	}
}

class PathNodeComperator implements Comparator
{
	public SystemEnvironment sysEnv;

	public int compare(Object obj1, Object obj2)
	{
		PathNode o1 = (PathNode)obj1;
		PathNode o2 = (PathNode)obj2;

		if (o1.contTs > o2.contTs)
			return -1;
		else if (o1.contTs < o2.contTs)
			return 1;
		return 0;
	}
}
