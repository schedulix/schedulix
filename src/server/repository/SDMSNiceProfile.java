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
import java.util.*;
import java.lang.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;

public class SDMSNiceProfile extends SDMSNiceProfileProxyGeneric
{

	protected SDMSNiceProfile(SDMSObject p_object)
	{
		super(p_object);
	}

	public void setIsActive(SystemEnvironment sysEnv, Boolean active)
		throws SDMSException
	{
		setIsActive(sysEnv, active, null);
	}

	public void setIsActive(SystemEnvironment sysEnv, Boolean active, Long activeTs)
		throws SDMSException
	{
		boolean oldActive = getIsActive(sysEnv).booleanValue();
		boolean newActive = active.booleanValue();

		if (oldActive == newActive) return;

		super.setIsActive(sysEnv, active);

		if (newActive) {
			if (activeTs == null) {
				Date dts = new Date();
				activeTs = new Long (dts.getTime());
			}
			setActiveTs(sysEnv, activeTs);
			activate(sysEnv, activeTs.longValue());
		} else {
			deactivate(sysEnv);
			setActiveTs(sysEnv, null);
		}
	}

	Vector getSortedEntries(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Vector npe_v = SDMSNiceProfileEntryTable.idx_npId.getVector(sysEnv, getId(sysEnv));
		NpeComparator npec = new NpeComparator(sysEnv);
		Collections.sort(npe_v, npec);
		return npe_v;
	}

	private SDMSNiceProfileEntry matchId (SystemEnvironment sysEnv, Vector npe_v, Long matchId)
		throws SDMSException
	{
		Iterator npe_i = npe_v.iterator();
		while (npe_i.hasNext()) {
			SDMSNiceProfileEntry npe = (SDMSNiceProfileEntry)(npe_i.next());
			if (! npe.getIsActive(sysEnv).booleanValue()) continue;
			if (matchId.equals(npe.getFolderId(sysEnv)))
				return npe;
		}
		return null;
	}

	public SDMSNiceProfileEntry match (SystemEnvironment sysEnv, Vector npe_v, SDMSSubmittedEntity sme)
		throws SDMSException
	{
		SDMSNiceProfileEntry npe = null;

		Long seId = sme.getSeId(sysEnv);

		npe = matchId(sysEnv, npe_v, seId);
		if (npe == null) {
			long seVersion = sme.getSeVersion(sysEnv).longValue();
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
			Long folderId = se.getFolderId(sysEnv);
			while (folderId != null) {
				npe = matchId(sysEnv, npe_v, folderId);
				if (npe != null) break;
				SDMSFolder f = SDMSFolderTable.getObject(sysEnv, folderId, seVersion);
				folderId = f.getParentId(sysEnv);
			}
		}

		return npe;
	}

	private Vector getMasters(SystemEnvironment sysEnv, long activeTs)
		throws SDMSException
	{
		final long ts = activeTs;
		Vector m_v = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, null,
			new SDMSFilter() {
				public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
					int state = ((SDMSSubmittedEntity)obj).getState(sysEnv).intValue();
					if (state == SDMSSubmittedEntity.FINAL || state == SDMSSubmittedEntity.CANCELLED)
						return false;
					Long npeId = ((SDMSSubmittedEntity)obj).getNpeId(sysEnv);
					if (npeId != null) {
						SDMSNiceProfileEntry npe = SDMSNiceProfileEntryTable.getObject(sysEnv, npeId);
						SDMSNiceProfile np = SDMSNiceProfileTable.getObject(sysEnv, npe.getNpId(sysEnv));
						if (np.getActiveTs(sysEnv).longValue() > ts)
							return false;
					}
					return true;
				}
			}
		);
		return m_v;
	}

	private void activate(SystemEnvironment sysEnv, long activeTs)
		throws SDMSException
	{
		String comment = "Renice caused by activation of Nice Profile " + getName(sysEnv);

		Vector npe_v = getSortedEntries(sysEnv);

		Vector m_v = getMasters(sysEnv,activeTs);

		Iterator m_i = m_v.iterator();
		while (m_i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity)(m_i.next());
			SDMSNiceProfileEntry npe = match(sysEnv, npe_v, sme);
			if (npe == null) continue;

			int nice = sme.getNpNice(sysEnv).intValue();
			int oldNice = nice;
			Long oldNpeId = sme.getNpeId(sysEnv);
			if (oldNpeId != null) {
				nice -= SDMSNiceProfileEntryTable.getObject(sysEnv, oldNpeId).getRenice(sysEnv).intValue();
			}
			nice += npe.getRenice(sysEnv).intValue();
			if (nice != oldNice) {
				sme.renice(sysEnv, null, new Integer(nice), comment);
			}
			sme.setNpeId(sysEnv, npe.getId(sysEnv));
			int npeIsSuspended = npe.getIsSuspended(sysEnv).intValue();
			int smeIsSuspended = sme.getIsSuspended(sysEnv).intValue();
			Long opSusresTs = sme.getOpSusresTs(sysEnv);
			long srts = 0;
			if (opSusresTs != null)
				srts = opSusresTs.longValue();
			Date dts = new Date();
			Long ts = new Long (dts.getTime());
			if (npeIsSuspended == SDMSNiceProfileEntry.NOSUSPEND) {
				if (smeIsSuspended != SDMSSubmittedEntity.NOSUSPEND && srts >= 0) {
					SDMSAuditTrailTable.table.create(sysEnv, sysEnv.cEnv.uid(), ts, new Integer(SDMSAuditTrail.RESUME),
						new Integer(SDMSAuditTrail.JOB), sme.getId(sysEnv), sme.getId(sysEnv), Boolean.FALSE, null,
						"Resume caused by activation of Nice Profile " + getName(sysEnv));

					sme.resume(sysEnv, true, false);
				}
			} else {
				if (smeIsSuspended != npeIsSuspended) {
					boolean admin = false;
					String stradm = "";
					if (npeIsSuspended == SDMSNiceProfileEntry.ADMINSUSPEND) {
						admin = true;
						stradm = "Admin ";
					}
					SDMSAuditTrailTable.table.create(sysEnv, sysEnv.cEnv.uid(), ts, new Integer(SDMSAuditTrail.SUSPEND),
						new Integer(SDMSAuditTrail.JOB), sme.getId(sysEnv), sme.getId(sysEnv), Boolean.FALSE, null,
						stradm + "Suspend caused by activation of Nice Profile " + getName(sysEnv));

					sme.suspend(sysEnv, false, admin, false);
				}
			}
		}
	}

	private Vector getNicedMasters(SystemEnvironment sysEnv, Vector npe_v)
		throws SDMSException
	{
		NicedMastersFilter nmf = new NicedMastersFilter(sysEnv, npe_v);
		Vector m_v = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, null, nmf);
		return m_v;
	}

	public static Vector getActiveNiceProfiles(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Iterator np_i = SDMSNiceProfileTable.table.iterator(sysEnv,
			new SDMSFilter() {
				public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
					if (! ((SDMSNiceProfile)obj).getIsActive(sysEnv).booleanValue()) return false;
					return true;
				}
			},
			false
		);
		Vector v = new Vector();
		while (np_i.hasNext()) {
			SDMSNiceProfile np = (SDMSNiceProfile)(np_i.next());
			long ats = np.getActiveTs(sysEnv);
			int i = 0;
			while (i < v.size()) {
				SDMSNiceProfile vnp = (SDMSNiceProfile)(v.elementAt(i));
				long vats = vnp.getActiveTs(sysEnv);
				if (vats < ats) break;
				i ++;
			}
			v.insertElementAt(np, i);
		}
		return v;
	}

	private Vector getPreviousActiveNiceProfiles(SystemEnvironment sysEnv)
		throws SDMSException
	{
		final long ts = getActiveTs(sysEnv).longValue();

		Iterator np_i = SDMSNiceProfileTable.table.iterator(sysEnv,
			new SDMSFilter() {
				public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException {
					if (! ((SDMSNiceProfile)obj).getIsActive(sysEnv).booleanValue()) return false;
					long ats = ((SDMSNiceProfile)obj).getActiveTs(sysEnv).longValue();
					if (ats >= ts) return false;
					return true;
				}
			},
			false
		);
		Vector v = new Vector();
		while (np_i.hasNext()) {
			SDMSNiceProfile np = (SDMSNiceProfile)(np_i.next());
			long ats = np.getActiveTs(sysEnv);
			int i = 0;
			while (i < v.size()) {
				SDMSNiceProfile vnp = (SDMSNiceProfile)(v.elementAt(i));
				long vats = vnp.getActiveTs(sysEnv);
				if (vats < ats) break;
				i ++;
			}
			v.insertElementAt(np, i);
		}
		return v;
	}

	private void deactivate(SystemEnvironment sysEnv)
		throws SDMSException
	{
		String comment = "Renice caused by deactivation of Nice Profile " + getName(sysEnv);

		Vector npe_v = getSortedEntries(sysEnv);

		Vector m_v = getNicedMasters(sysEnv, npe_v);

		Vector panp_v = getPreviousActiveNiceProfiles(sysEnv);

		HashMap npe_map = new HashMap();
		Iterator m_i = m_v.iterator();
		while (m_i.hasNext()) {
			SDMSSubmittedEntity sme = (SDMSSubmittedEntity)(m_i.next());
			SDMSNiceProfileEntry myNpe = SDMSNiceProfileEntryTable.getObject(sysEnv, sme.getNpeId(sysEnv));
			SDMSNiceProfileEntry npe = null;
			Iterator p_i = panp_v.iterator();
			while (p_i.hasNext()) {
				SDMSNiceProfile np = (SDMSNiceProfile)(p_i.next());
				Long npId = np.getId(sysEnv);
				Vector v = null;
				if (npe_map.containsKey(npId))
					v = (Vector)(npe_map.get(npId));
				else {
					v = np.getSortedEntries(sysEnv);
					npe_map.put(npId, v);
				}
				npe = match(sysEnv, v, sme);
				if (npe != null) break;
			}
			int nice = sme.getNpNice(sysEnv).intValue();
			int oldNice = nice;
			nice -= myNpe.getRenice(sysEnv).intValue();
			int npeIsSuspended = SDMSNiceProfileEntry.NOSUSPEND;
			long oldNpActivteTs = 0;
			if (npe != null) {
				nice += npe.getRenice(sysEnv).intValue();
				sme.setNpeId(sysEnv, npe.getId(sysEnv));
				npeIsSuspended = npe.getIsSuspended(sysEnv).intValue();
				SDMSNiceProfile np = SDMSNiceProfileTable.getObject(sysEnv, npe.getNpId(sysEnv));
				oldNpActivteTs = np.getActiveTs(sysEnv).longValue();
			} else
				sme.setNpeId(sysEnv, null);

			if (nice != oldNice) {
				sme.renice(sysEnv, null, new Integer(nice), comment);
			}
			int smeIsSuspended = sme.getIsSuspended(sysEnv).intValue();
			Long opSusresTs = sme.getOpSusresTs(sysEnv);
			long srts = 0;
			if (opSusresTs != null)
				srts = opSusresTs.longValue();
			Date dts = new Date();
			Long ts = new Long (dts.getTime());
			if (srts > oldNpActivteTs)
				npeIsSuspended = SDMSNiceProfileEntry.NOSUSPEND;
			if (npeIsSuspended == SDMSNiceProfileEntry.NOSUSPEND) {
				if (smeIsSuspended != SDMSSubmittedEntity.NOSUSPEND && srts >= 0) {
					SDMSAuditTrailTable.table.create(sysEnv, sysEnv.cEnv.uid(), ts, new Integer(SDMSAuditTrail.RESUME),
						new Integer(SDMSAuditTrail.JOB), sme.getId(sysEnv), sme.getId(sysEnv), Boolean.FALSE, null,
						"Resume caused by deactivation of Nice Profile " + getName(sysEnv));

					sme.resume(sysEnv, true, false);
				}
			} else {

				if (smeIsSuspended != npeIsSuspended && srts < oldNpActivteTs) {
					boolean admin = false;
					String stradm = "";
					if (npeIsSuspended == SDMSNiceProfileEntry.ADMINSUSPEND) {
						admin = true;
						stradm = "Admin ";
					}
					SDMSAuditTrailTable.table.create(sysEnv, sysEnv.cEnv.uid(), ts, new Integer(SDMSAuditTrail.SUSPEND),
						new Integer(SDMSAuditTrail.JOB), sme.getId(sysEnv), sme.getId(sysEnv), Boolean.FALSE, null,
						stradm + "Suspend caused by deactivation of Nice Profile " + getName(sysEnv));

					sme.suspend(sysEnv, false, admin, false);
				}
			}
		}
	}
}

class NpeComparator implements Comparator
{
	SystemEnvironment sysEnv;

	public  NpeComparator (SystemEnvironment sysEnv)
	{
		this.sysEnv = sysEnv;
	}

	public int compare (Object o1, Object o2)
	{
		SDMSNiceProfileEntry npe1 = (SDMSNiceProfileEntry) o1;
		SDMSNiceProfileEntry npe2 = (SDMSNiceProfileEntry) o2;
		try {
			return npe1.getPreference(sysEnv).compareTo(npe2.getPreference(sysEnv));
		} catch (SDMSException e) {

			return 0;
		}
	}
}

class NicedMastersFilter implements SDMSFilter
{
	private HashSet hs = null;

	public NicedMastersFilter (SystemEnvironment sysEnv, Vector npe_v)  throws SDMSException
	{

		hs = new HashSet();
		Iterator npe_i = npe_v.iterator();
		while (npe_i.hasNext()) {
			SDMSNiceProfileEntry npe = (SDMSNiceProfileEntry)(npe_i.next());
			hs.add(npe.getId(sysEnv));
		}
	}

	public boolean isValid(SystemEnvironment sysEnv, SDMSProxy obj) throws SDMSException
	{
		int state = ((SDMSSubmittedEntity)obj).getState(sysEnv).intValue();
		if (state == SDMSSubmittedEntity.FINAL || state == SDMSSubmittedEntity.CANCELLED || !hs.contains(((SDMSSubmittedEntity)obj).getNpeId(sysEnv)))
			return false;
		return true;
	}
}
