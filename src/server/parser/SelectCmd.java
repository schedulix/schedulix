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
import java.sql.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class SelectCmd extends Node
{

	private String selectCmd;
	private WithHash with;
	private Vector sv;
	private int clist[];
	private int ctype[];
	private int cquote[];
	private int cl_size;

	public static final int CATEGORYTYPE = Parser.CATEGORY;
	public static final int FOLDERTYPE = Parser.FOLDER;
	public static final int JOBTYPE = Parser.JOB;
	public static final int RESOURCETYPE = Parser.RESOURCE;
	public static final int SCOPETYPE = Parser.SCOPE;
	public static final int SCHEDULETYPE = Parser.SCHEDULE;

	public SelectCmd(String s)
	{
		super();
		selectCmd = s;
		with = null;
		cl_size = 0;
		sv = null;
		txMode = SDMSTransaction.READONLY;
	}

	public SelectCmd(String s, WithHash w)
	{
		super();
		selectCmd = s;
		with = w;
		if (w != null) {
			cl_size = w.size();
			sv = (Vector) w.get(ParseStr.S_SORT);
			if(sv != null) cl_size--;
		} else {
			cl_size = 0;
			sv = null;
		}
		txMode = SDMSTransaction.READONLY;
	}

	private Vector collist(ResultSetMetaData mdset)
		throws SQLException
	{
		Vector desc = new Vector();

		int j = 0;
		for(int i = 1; i <= mdset.getColumnCount(); i++) {
			String cn = mdset.getColumnName(i).toUpperCase();
			if(cl_size > 0 && with.get(cn) != null) {
				Integer t = (Integer) with.get(cn);
				clist[j] = i;
				ctype[j] = Math.abs(t.intValue());
				cquote[j] = (t > 0 ? 1 : -1);
				j++;
			}
			desc.addElement(cn);
		}
		cl_size = j;
		sort();

		return desc;
	}

	private void sort()
	{
		int i, j;

		for(i = 0; i < cl_size - 1; i++) {
			for(j = i+1; j < cl_size; j++) {
				if(clist[i] > clist[j]) {
					int tmp = clist[i];
					clist[i] = clist[j];
					clist[j] = tmp;

					tmp = ctype[i];
					ctype[i] = ctype[j];
					ctype[j] = tmp;
				}
			}
		}
	}

	private Long objectToId(SystemEnvironment sysEnv, Object o)
		throws SDMSException
	{
		Long sId;
		if(o instanceof Integer) {
			sId = new Long (((Integer) o).intValue());
		} else if(o instanceof Long) {
			sId = (Long) o;
		} else if(o instanceof java.math.BigDecimal) {
			sId = new Long (((java.math.BigDecimal) o).intValue());
		} else {
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03204250147",
				"Type Error, Column is not an Id but a $1", o.getClass().getName()));
		}
		return sId;
	}

	private PathVector convert_folder(SystemEnvironment sysEnv, Object o)
	throws SDMSException
	{
		SDMSFolder f;
		Long id = objectToId(sysEnv, o);
		try {
			f = SDMSFolderTable.getObject(sysEnv, id);
		} catch (NotFoundException nfe) {
			return convert_job_raw(sysEnv, id);
		}
		return f.pathVector(sysEnv);
	}

	private PathVector convert_folder_raw(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		SDMSFolder f;
		f = SDMSFolderTable.getObject(sysEnv, id);
		return f.pathVector(sysEnv);
	}

	private PathVector convert_scope(SystemEnvironment sysEnv, Object o)
	throws SDMSException
	{
		SDMSScope s;
		s = SDMSScopeTable.getObject(sysEnv, objectToId(sysEnv, o));
		return s.pathVector(sysEnv);
	}

	private PathVector convert_job(SystemEnvironment sysEnv, Object o)
	throws SDMSException
	{
		SDMSSchedulingEntity se;
		Long id = objectToId(sysEnv, o);
		try {
			se = SDMSSchedulingEntityTable.getObject(sysEnv, id);
		} catch (NotFoundException nfe) {
			return convert_folder_raw(sysEnv, id);
		}
		return se.pathVector(sysEnv);
	}

	private PathVector convert_job_raw(SystemEnvironment sysEnv, Long id)
	throws SDMSException
	{
		SDMSSchedulingEntity se;
		se = SDMSSchedulingEntityTable.getObject(sysEnv, id);
		return se.pathVector(sysEnv);
	}

	private PathVector convert_category(SystemEnvironment sysEnv, Object o)
	throws SDMSException
	{
		SDMSNamedResource nr;
		nr = SDMSNamedResourceTable.getObject(sysEnv, objectToId(sysEnv, o));
		return nr.pathVector(sysEnv);
	}

	private PathVector convert_schedule(SystemEnvironment sysEnv, Object o)
	throws SDMSException
	{
		SDMSSchedule sc;
		sc = SDMSScheduleTable.getObject(sysEnv, objectToId(sysEnv, o));
		return sc.pathVector(sysEnv);
	}

	private String convert(SystemEnvironment sysEnv, Object o, int idx)
	throws SDMSException
	{
		PathVector pv = null;
		int quoted = cquote[idx];

		if(o == null) return null;
		try {
			switch(ctype[idx]) {
				case CATEGORYTYPE:
					pv = convert_category(sysEnv, o);
					break;
				case FOLDERTYPE:
					pv = convert_folder(sysEnv, o);
					break;
				case JOBTYPE:
					pv = convert_job(sysEnv, o);
					break;
				case RESOURCETYPE:
					pv = convert_category(sysEnv, o);
					break;
				case SCHEDULETYPE:
					pv = convert_schedule(sysEnv, o);
					break;
				case SCOPETYPE:
					pv = convert_scope(sysEnv, o);
					break;
			}
		} catch (NotFoundException nfe) {
			nfe.printStackTrace();
			pv = null;
		}

		if (pv != null) {
			if (quoted < 0) {
				return pv.toQuotedString(null);
			} else {
				return pv.toString();
			}
		}
		return null;
	}

	public void go(SystemEnvironment sysEnv)
		throws SDMSException
	{
		Long sgId = null;
		Long ZERO = new Long(0);

		if(!sysEnv.cEnv.gid().contains(SDMSObject.adminGId)) {
			SDMSPrivilege p = new SDMSPrivilege();
			Vector v = SDMSMemberTable.idx_uId.getVector(sysEnv, sysEnv.cEnv.uid());
			for(int i = 0; i < v.size(); i++) {
				SDMSMember m = (SDMSMember) v.get(i);
				try {
					SDMSGrant gr = SDMSGrantTable.idx_objectId_gId_getUnique(sysEnv, new SDMSKey(ZERO, m.getGId(sysEnv)));
					p.addPriv(sysEnv, gr.getPrivs(sysEnv).longValue());
				} catch (NotFoundException nfe) {
				}
			}
			try {
				if (sysEnv.selectGroup != null) {
					SDMSGroup sg = SDMSGroupTable.idx_name_getUnique(sysEnv, sysEnv.selectGroup);
					sgId = sg.getId(sysEnv);
				}
			} catch (NotFoundException nfe) {
			}
			if (!(p.can(SDMSPrivilege.MANAGE_SEL) || (sgId != null && sysEnv.cEnv.gid().contains(sgId))))
				throw new AccessViolationException(new SDMSMessage(sysEnv, "03003081235", "Insufficient Privileges"));
		}

		int read = 0;
		SDMSOutputContainer d_container = null;

		if(cl_size > 0) {
			clist = new int[cl_size];
			ctype = new int[cl_size];
			cquote = new int[cl_size];
		}

		try {
			Statement stmt = sysEnv.dbConnection.createStatement();
			ResultSet rset = stmt.executeQuery(selectCmd);
			ResultSetMetaData mdset = rset.getMetaData();
			Vector desc = collist(mdset);
			d_container = new SDMSOutputContainer(sysEnv, "Selected Values", desc);
			while(rset.next()) {
				Vector data = new Vector();
				int j = 0;
				for(int i = 1; i <= desc.size(); i++) {
					Object o = rset.getObject(i);
					if(cl_size > 0 && j < cl_size && i == clist[j]) {
						o = convert(sysEnv, o, j);
						j++;
					}
					data.addElement((rset.wasNull() ? null : o));
				}
				d_container.addData(sysEnv, data);
				read++;
			}
			stmt.close();
			sysEnv.dbConnection.commit();
		} catch (SQLException sqle) {
			try {
				sysEnv.dbConnection.rollback();
			} catch (SQLException sqle2) {
				throw new RecoverableException(new SDMSMessage(sysEnv, "03310281524", "Connection lost"));
			}
			throw new CommonErrorException(new SDMSMessage(sysEnv, "03204170024", "SQL Error : $1", sqle.toString()));
		}

		if(sv != null && sv.size() > 0) {
			int sca[] = new int[sv.size()];
			for(int i = 0; i < sv.size(); i++) {
				sca[i] = ((Integer) sv.get(i)).intValue();
				if (sca[i] >= d_container.columns)
					throw new CommonErrorException(new SDMSMessage(sysEnv, "03003081227",
								"The sort column specified ($1) exceeds the number of columns in the output", new Integer(sca[i])));
			}
			Collections.sort(d_container.dataset, d_container.getComparator(sysEnv, sca));
		}

		result.setOutputContainer(d_container);
		result.setFeedback(new SDMSMessage(sysEnv, "03204112153", "$1 Row(s) selected", new Integer(read)));
	}
}

