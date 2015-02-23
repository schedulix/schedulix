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

public class DBCleanupThread extends SDMSThread
{

	public  SystemEnvironment sysEnv;

	private final static int NR = 99999999;
	private final static int TX_RETRY_TIME = 10 * 1000;
	private final static int IDLE_SLEEP_TIME = 15 * 60 * 1000;
	private final static int MASTERCHUNK = 10000;

	private Vector<MasterEntry> masterList = new Vector<MasterEntry>();

	private PreparedStatement deleteMaster = null;
	private PreparedStatement deleteKillJob = null;
	private PreparedStatement deleteAuditTrail = null;
	private PreparedStatement deleteEntityVariable = null;
	private PreparedStatement deleteDependencyInstance = null;
	private PreparedStatement deleteHierarchyInstance = null;

	private PreparedStatement archiveMaster = null;
	private PreparedStatement archiveKillJob = null;
	private PreparedStatement archiveAuditTrail = null;
	private PreparedStatement archiveEntityVariable = null;
	private PreparedStatement archiveDependencyInstance = null;
	private PreparedStatement archiveHierarchyInstance = null;

	private PreparedStatement loadSmeForMaster = null;
	private PreparedStatement loadMasters = null;

	public DBCleanupThread(Server s)
	{
		super("DBCleanupThread");
		sysEnv = null;
	}

	public int id()
	{
		return -NR;
	}

	private Vector checkColumns(SDMSTable table, Vector columns)
	throws SDMSException
	{
		Vector tableColumns = new Vector(Arrays.asList(table.columnNames()));
		if (columns.size() == 0)
			return tableColumns;
		HashSet h = new HashSet();
		columns.add("ID");
		Iterator i = columns.iterator();
		while (i.hasNext()) {
			Object o = i.next();
			if (h.contains(o)) {
				i.remove();
				continue;
			}
			if (!tableColumns.contains(o)) {
				throw new FatalException(new SDMSMessage(sysEnv, "02411181314",
				                         "Invalid Archive Column " + o + " for Table " + table.tableName()));
			}
			h.add(o);
		}
		return columns;
	}

	private PreparedStatement prepareArchive(SDMSTable table, Vector columns, String selectColumn)
	throws SDMSException, SQLException
	{
		if (columns == null) return null;
		columns = checkColumns(table, columns);
		String columnList = "";
		String sep = "";
		String squote = "";
		String equote = "";
		final String driverName = sysEnv.dbConnection.getMetaData().getDriverName();
		if (driverName.startsWith("MySQL") || driverName.startsWith("mariadb")) {
			squote = "`";
			equote = "`";
		}
		if (driverName.startsWith("Microsoft")) {
			squote = "[";
			equote = "]";
		}
		for (int i = 0; i < columns.size(); i ++) {
			columnList += sep + squote + columns.get(i) + equote;
			sep = ",";
		}
		String stmt = "INSERT INTO ARC_" + table.tableName() + " (" + columnList + ") SELECT " +
		              columnList + " FROM " + table.tableName() +
		              " WHERE " + selectColumn + " = ?";

		return sysEnv.dbConnection.prepareStatement(stmt);
	}

	private void prepareConnection()
	throws SDMSException
	{
		while (true) {
			try {
				sysEnv.dbConnection = Server.connectToDB(sysEnv);
			} catch(SDMSException e) {
				sysEnv = null;
				doTrace(null, "Error: couldn't get database connection", SEVERITY_ERROR);
				throw e;
			}
			try {

				sysEnv.dbConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			} catch (SQLException sqle) {

			}
			try {
				String query = "SELECT ID, FINAL_TS FROM SUBMITTED_ENTITY WHERE ID = MASTER_ID AND STATE IN (" +
				               SDMSSubmittedEntity.CANCELLED + ", " + SDMSSubmittedEntity.FINAL + ") AND FINAL_TS  < ? ORDER BY FINAL_TS";
				loadMasters              = sysEnv.dbConnection.prepareStatement(query);
				loadSmeForMaster         = sysEnv.dbConnection.prepareStatement("SELECT ID FROM SUBMITTED_ENTITY WHERE MASTER_ID = ?");
				deleteMaster             = sysEnv.dbConnection.prepareStatement("DELETE FROM SUBMITTED_ENTITY WHERE MASTER_ID = ?");
				deleteKillJob            = sysEnv.dbConnection.prepareStatement("DELETE FROM KILL_JOB WHERE SME_ID = ?");
				deleteAuditTrail         = sysEnv.dbConnection.prepareStatement("DELETE FROM AUDIT_TRAIL WHERE OBJECT_ID = ?");
				deleteEntityVariable     = sysEnv.dbConnection.prepareStatement("DELETE FROM ENTITY_VARIABLE WHERE SME_ID = ?");
				deleteDependencyInstance = sysEnv.dbConnection.prepareStatement("DELETE FROM DEPENDENCY_INSTANCE WHERE DEPENDENT_ID = ?");
				deleteHierarchyInstance  = sysEnv.dbConnection.prepareStatement("DELETE FROM HIERARCHY_INSTANCE WHERE CHILD_ID = ?");
				if (sysEnv.archive) {
					archiveMaster = prepareArchive(SDMSSubmittedEntityTable.table, sysEnv.smeColumns, "MASTER_ID");
					archiveKillJob = prepareArchive(SDMSKillJobTable.table, sysEnv.kjColumns, "SME_ID");
					archiveAuditTrail = prepareArchive(SDMSAuditTrailTable.table, sysEnv.atColumns, "OBJECT_ID");
					archiveEntityVariable = prepareArchive(SDMSEntityVariableTable.table, sysEnv.evColumns, "SME_ID");
					archiveDependencyInstance = prepareArchive(SDMSDependencyInstanceTable.table, sysEnv.diColumns, "DEPENDENT_ID");
					archiveHierarchyInstance = prepareArchive(SDMSHierarchyInstanceTable.table, sysEnv.hiColumns, "CHILD_ID");
				}
			} catch (SQLRecoverableException sqlre) {
				try {

					doTrace(null, "Recoverable Error Preparing Delete/Archive Statements" + sqlre.getMessage(), SEVERITY_WARNING);
					try {
						sysEnv.dbConnection.close();
					} catch(SQLException sqle2) {

						doTrace(null, "Error while closing connection: " + sqle2.getMessage(), SEVERITY_ERROR);
					}
					sleep(TX_RETRY_TIME);
				} catch (InterruptedException ie) {

				}
				continue;
			} catch (SQLException sqle) {
				sysEnv = null;
				doTrace(null, "Error Preparing Delete/Archive Statements" + sqle.getMessage(), SEVERITY_ERROR);

				throw new FatalException(new SDMSMessage(sysEnv, "03411171211", "Error Preparing Delete/Archive Statements"));
			}
			break;
		}
	}

	public void initDBCleanupThread(SystemEnvironment env)
	throws SDMSException
	{
		try {
			sysEnv = (SystemEnvironment) env.clone();
		} catch(CloneNotSupportedException cnse) {
			throw new FatalException(new SDMSMessage(sysEnv, "03411170950",
			                         "Cannot Clone SystemEnvironment"));
		}
		prepareConnection();

		return;
	}

	private void loadMasters()
	throws SDMSException
	{

		try {
			if (sysEnv.dbConnection.isClosed())
				prepareConnection();
		} catch (SQLException sqle) {

			try {
				sysEnv.dbConnection.close();
			} catch (SQLException sqle1) {  }
			prepareConnection();
		}

		while (true) {
			try {
				loadMasters.clearParameters();
				long maxFinalTs = System.currentTimeMillis() - sysEnv.dbPreserveTime + 24 * 60 * 60 * 1000;
				loadMasters.setLong(1, maxFinalTs);
				loadMasters.setMaxRows(MASTERCHUNK);
				ResultSet rs = loadMasters.executeQuery();
				while(rs.next()) {
					MasterEntry e = new MasterEntry();
					e.finalTs = rs.getLong(2);
					e.id = rs.getLong(1);
					masterList.add(e);

				}
				sysEnv.dbConnection.commit();
			} catch (SQLRecoverableException sqlre) {

				doTrace(null, "Recoverable Error Preparing Delete/Archive Statements" + sqlre.getMessage(), SEVERITY_WARNING);
				try {
					sysEnv.dbConnection.close();
					try {
						sleep(TX_RETRY_TIME);
					} catch (InterruptedException ie) {

					}
				} catch(SQLException sqle2) {

					doTrace(null, "Error while closing connection: " + sqle2.getMessage(), SEVERITY_ERROR);
				}

				prepareConnection();
				continue;
			} catch (SQLException sqle) {
				doTrace(null, "Error loading masters:" + sqle.getMessage(), SEVERITY_ERROR);

				try {
					sysEnv.dbConnection.close();
				} catch (SQLException sqle4) {

				}
				masterList.clear();
			}
			doTrace(null, "Found " + masterList.size() + " masters for archive/cleanup", SEVERITY_MESSAGE);
			break;
		}

	}

	private Vector<Long> loadMaster(long id)
	throws SDMSException
	{
		Vector<Long> sme_v = new Vector<Long>();
		try {
			loadSmeForMaster.clearParameters();
			loadSmeForMaster.setLong(1, id);
			ResultSet rs = loadSmeForMaster.executeQuery();
			while(rs.next()) {
				sme_v.add(new Long(rs.getLong(1)));
			}
		} catch (SQLException sqle) {
			try {
				sysEnv.dbConnection.rollback();
			} catch (SQLException sqle1) {
				// do nothing; at least we tried
			}
			doTrace(null, "Error loading master:" + sqle.getMessage(), SEVERITY_ERROR);
			return null;

		}
		return sme_v;
	}

	private void deleteForSme(PreparedStatement s, long id, String what)
	throws SDMSException, SQLException
	{
		s.clearParameters();
		s.setLong(1, id);
		s.executeUpdate();
	}

	private void archiveForSme(PreparedStatement s, long id, String what)
	throws SDMSException, SQLException
	{
		if (s == null) return;
		s.clearParameters();
		s.setLong(1, id);
		s.executeUpdate();
	}

	private void processSme(long id)
	throws SDMSException, SQLException
	{
		archiveForSme(archiveKillJob, id, "KILL_JOB");
		deleteForSme(deleteKillJob, id, "KILL_JOB");
		archiveForSme(archiveAuditTrail, id, "AUDIT_TRAIL");
		deleteForSme(deleteAuditTrail, id, "AUDIT_TRAIL");
		archiveForSme(archiveEntityVariable, id, "ENTITY_VARIABLE");
		deleteForSme(deleteEntityVariable, id, "ENTITY_VARIABLE");
		archiveForSme(archiveDependencyInstance, id, "DEPENDENCY_INSTANCE");
		deleteForSme(deleteDependencyInstance, id, "DEPENDENCY_INSTANCE");
		archiveForSme(archiveHierarchyInstance, id, "HIERARCHY_INSTANCE");
		deleteForSme(deleteHierarchyInstance, id, "HIERARCHY_INSTANCE");
	}

	private boolean processMaster(long id)
	throws SDMSException
	{
		Vector<Long> sme_v = loadMaster(id);
		if (sme_v == null) {

			return false;
		}
		for (int i = 0; i < sme_v.size(); i ++) {
			try {
				processSme(sme_v.get(i).longValue());
			} catch (SQLException sqle) {
				doTrace(null, "Error processing Smes for Master" + sqle.getMessage(), SEVERITY_ERROR);
				try {
					sysEnv.dbConnection.rollback();
				} catch (SQLException sqle1) {  }
				return false;
			}
		}
		try {
			if (archiveMaster != null) {
				archiveMaster.clearParameters();
				archiveMaster.setLong(1, id);
				archiveMaster.executeUpdate();
			}
			deleteMaster.clearParameters();
			deleteMaster.setLong(1, id);
			deleteMaster.executeUpdate();
			sysEnv.dbConnection.commit();
		} catch (SQLException sqle) {
			doTrace(null, "Error Deleting Children of Master" + id + ": " + sqle.getMessage(), SEVERITY_ERROR);
			try {
				sysEnv.dbConnection.rollback();
			} catch (SQLException sqle1) {  }
			return false;

		}
		return true;
	}

	private boolean processMasters()
	throws SDMSException, InterruptedException
	{
		boolean deleted = false;
		int mastersRemoved = 0;
		while (masterList.size() > 0) {
			if (!run) break;
			MasterEntry master = masterList.get(0);

			if (SDMSSubmittedEntityTable.table.exists(sysEnv, master.id)) {
				masterList.remove(0);
				continue;
			}

			try {
				if (sysEnv.dbConnection.isClosed())
					prepareConnection();
			} catch (SQLException sqle) {
				try {
					sysEnv.dbConnection.close();
				} catch (SQLException sqle1) {  }
				prepareConnection();
			}

			long maxFinalTs = System.currentTimeMillis() - sysEnv.dbPreserveTime;
			if (master.finalTs <= maxFinalTs) {
				if (processMaster(master.id)) {
					masterList.remove(0);
					deleted = true;
					mastersRemoved++;
				} else {
					sleep(TX_RETRY_TIME);
				}
			} else {
				sleep(master.finalTs - maxFinalTs);
			}
		}
		doTrace(null, "Number of masters removed : " + mastersRemoved, SEVERITY_MESSAGE);
		return deleted;
	}

	public void SDMSrun()
	{
		if (sysEnv.dbPreserveTime == 0)
			return;

		if(sysEnv == null)
			doTrace(null, "Uninitialized Thread. Call to initDBCleanupThread() missing?", SEVERITY_FATAL);

		try {
			sleep(IDLE_SLEEP_TIME);
			while(run) {
				loadMasters();
				if (masterList.size() == 0 || !processMasters() )
					sleep(IDLE_SLEEP_TIME);
			}
		} catch(SDMSException e) {
			doTrace(null, "Error occurred : " + e.toString(), SEVERITY_FATAL);
		} catch(InterruptedException ie) {

		}

		try {
			sysEnv.dbConnection.close();
		} catch(SQLException sqle) {
			doTrace(null, "Error while closing connection: " + sqle.getMessage(), SEVERITY_ERROR);
		}

		return;
	}

}

class MasterEntry
{
	public long id;
	public long finalTs;
}
