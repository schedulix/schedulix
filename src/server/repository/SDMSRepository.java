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

import java.lang.*;
import java.util.*;
import java.sql.*;
import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;

public class SDMSRepository
{

	public final static String __version = "@(#) $Id: SDMSRepository.java,v 2.16.4.5 2013/03/22 14:48:03 ronald Exp $";

	private HashMap tables;
	private Iterator tableIterator = null;
	private SDMSException loaderException = null;
	private Integer lockObject = new Integer(0);
	private long lowestActiveVersion = Long.MAX_VALUE;

	public SDMSRepository(SystemEnvironment env) throws SDMSException
	{
		SystemEnvironment.repository = this;

		env.dbConnection = Server.connectToDB(env);

		fillSme2Load(env);
		env.lowestActiveVersion = lowestActiveVersion;

		initMap(env);
		loadTables(env);

		try {
			env.dbConnection.close();
		} catch(SQLException sqle) {
			throw new FatalException(new SDMSMessage(env, "03110181535",
			                         "SQL Error : $1", sqle.getMessage()));

		}
	}

	private void fillSme2Load(SystemEnvironment env)
	throws SDMSException
	{
		long lowestActiveDate = getLowestActiveDate(env);
		long historyDate = getHistoryDate(env);
		int masterCtr = 0;
		long oldSeId = 0;

		long smeId;
		long seId;
		int  state;
		long seVersion;
		long finalTs;
		boolean hit;

		try {
			final String driverName = env.dbConnection.getMetaData().getDriverName();
			final boolean postgres = driverName.startsWith("PostgreSQL");
			String squote = "";
			String equote = "";
			if (driverName.startsWith("MySQL")) {
				squote = "`";
				equote = "`";
			}
			if (driverName.startsWith("Microsoft")) {
				squote = "[";
				equote = "]";
			}

			Statement cleanup = env.dbConnection.createStatement();
			cleanup.executeUpdate("DELETE FROM SME2LOAD");
			env.dbConnection.commit();

			PreparedStatement insertStmt = env.dbConnection.prepareStatement("INSERT INTO SME2LOAD VALUES ( ? )");

			Statement stmt = env.dbConnection.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT " + "ID" +
			                                   ", " + squote + "SE_ID" + equote +
			                                   ", " + squote + "SE_VERSION" + equote +
			                                   ", " + squote + "STATE" + equote +
			                                   ", " + squote + "FINAL_TS" + equote +
			                                   ", " + squote + "SUBMIT_TS" + equote +
			                                   "  FROM SUBMITTED_ENTITY" +
			                                   " WHERE ID = MASTER_ID" +
			                                   "   AND (" + squote + "STATE" + squote + " NOT IN (" + SDMSSubmittedEntity.CANCELLED + "," + SDMSSubmittedEntity.FINAL + ") OR" +
			                                   "       FINAL_TS >= " + (postgres ?
			                                                   "	   CAST (\'" + lowestActiveDate + "\' AS DECIMAL)" :
			                                                   "	   " + lowestActiveDate) + ")" +
			                                   " ORDER BY SE_ID, SUBMIT_TS DESC");
			int insctr = 0;
			while(rset.next()) {
				hit = false;
				smeId = rset.getLong(1);
				seId = rset.getLong(2);
				seVersion = rset.getLong(3);
				state = rset.getInt(4);
				finalTs = rset.getLong(5);

				if (seId != oldSeId) {
					masterCtr = 0;
					oldSeId = seId;
				}
				masterCtr++;

				if (state != SDMSSubmittedEntity.CANCELLED && state != SDMSSubmittedEntity.FINAL)
					hit = true;
				else if (masterCtr <= env.minHistoryCount)
					hit = true;
				else if (masterCtr <= env.maxHistoryCount || env.maxHistoryCount == 0) {
					if (finalTs >= historyDate) hit = true;
				}
				if (hit) {
					if (seVersion < lowestActiveVersion)
						lowestActiveVersion = seVersion;
					// now write sme2load table with masterId
					insertStmt.setLong(1, smeId);
					insertStmt.addBatch();
					insctr++;
					if (insctr == 1000) {	// write 1000 rows "at once"
						insertStmt.executeBatch();
						insertStmt.clearBatch();
						insctr = 0;
					}
				}
			}
			if (insctr != 0)			// if there are any remaining rows, write them
				insertStmt.executeBatch();
			stmt.close();
			insertStmt.close();
			env.dbConnection.commit();

			Statement fill = env.dbConnection.createStatement();
			fill.executeUpdate("INSERT INTO SME2LOAD " +
			                   "SELECT S.ID FROM SUBMITTED_ENTITY S, SME2LOAD M " +
			                   " WHERE M.ID = S.MASTER_ID " +
			                   "   AND S.ID != S.MASTER_ID");
			env.dbConnection.commit();
		} catch(SQLException sqle) {
			throw new FatalException(new SDMSMessage(env, "03401131304",
			                         "SQL Error : $1", sqle.getMessage()));
		}
	}

	private long getLowestActiveDate(SystemEnvironment env)
	{
		java.util.Date d = new java.util.Date();

		long pTime = (SystemEnvironment.minHistoryCount == 0 ? SystemEnvironment.preserveTime : SystemEnvironment.maxPreserveTime);

		return d.getTime() - pTime;
	}

	private long getHistoryDate(SystemEnvironment env)
	{
		java.util.Date d = new java.util.Date();

		return d.getTime() - SystemEnvironment.preserveTime;
	}

	private void initMap(SystemEnvironment env)  throws SDMSException
	{
		tables = new HashMap();

		// For each table
		// C
		tables.put(SDMSCalendarTableGeneric.tableName,                    new SDMSCalendarTable(env));
		// D
		tables.put(SDMSDependencyDefinitionTableGeneric.tableName,        new SDMSDependencyDefinitionTable(env));
		tables.put(SDMSDependencyInstanceTableGeneric.tableName,          new SDMSDependencyInstanceTable(env));
		tables.put(SDMSDependencyStateTableGeneric.tableName,             new SDMSDependencyStateTable(env));
		// E
		tables.put(SDMSEntityVariableTableGeneric.tableName,              new SDMSEntityVariableTable(env));
		tables.put(SDMSEnvironmentTableGeneric.tableName,                 new SDMSEnvironmentTable(env));
		tables.put(SDMSEventParameterTableGeneric.tableName,              new SDMSEventParameterTable(env));
		tables.put(SDMSEventTableGeneric.tableName,                       new SDMSEventTable(env));
		tables.put(SDMSExitStateDefinitionTableGeneric.tableName,         new SDMSExitStateDefinitionTable(env));
		tables.put(SDMSExitStateGeneric.tableName,                        new SDMSExitStateTable(env));
		tables.put(SDMSExitStateMappingGeneric.tableName,                 new SDMSExitStateMappingTable(env));
		tables.put(SDMSExitStateMappingProfileGeneric.tableName,          new SDMSExitStateMappingProfileTable(env));
		tables.put(SDMSExitStateProfileGeneric.tableName,                 new SDMSExitStateProfileTable(env));
		// F
		tables.put(SDMSFolderTableGeneric.tableName,                      new SDMSFolderTable(env));
		tables.put(SDMSFootprintTableGeneric.tableName,                   new SDMSFootprintTable(env));
		// G
		tables.put(SDMSGrantTableGeneric.tableName,                       new SDMSGrantTable(env));
		tables.put(SDMSGroupTableGeneric.tableName,                       new SDMSGroupTable(env));
		// H
		tables.put(SDMSHierarchyInstanceTableGeneric.tableName,           new SDMSHierarchyInstanceTable(env));
		// I
		tables.put(SDMSIgnoredDependencyTableGeneric.tableName,           new SDMSIgnoredDependencyTable(env));
		tables.put(SDMSIntervalHierarchyTableGeneric.tableName,           new SDMSIntervalHierarchyTable(env));
		tables.put(SDMSIntervalSelectionTableGeneric.tableName,           new SDMSIntervalSelectionTable(env));
		tables.put(SDMSIntervalTableGeneric.tableName,                    new SDMSIntervalTable(env));
		// K
		tables.put(SDMSKillJobTableGeneric.tableName,                     new SDMSKillJobTable(env));
		// N
		tables.put(SDMSNamedEnvironmentTableGeneric.tableName,            new SDMSNamedEnvironmentTable(env));
		tables.put(SDMSNamedResourceTableGeneric.tableName,               new SDMSNamedResourceTable(env));
		// M
		tables.put(SDMSMasterAllocationTableGeneric.tableName,            new SDMSMasterAllocationTable(env));
		tables.put(SDMSMemberTableGeneric.tableName,                      new SDMSMemberTable(env));
		// O
		tables.put(SDMSObjectCommentTableGeneric.tableName,               new SDMSObjectCommentTable(env));
		// P
		tables.put(SDMSParameterDefinitionTableGeneric.tableName,         new SDMSParameterDefinitionTable(env));
		tables.put(SDMSPersistentValueTableGeneric.tableName,             new SDMSPersistentValueTable(env));
		// R
		tables.put(SDMSResourceAllocationTableGeneric.tableName,          new SDMSResourceAllocationTable(env));
		tables.put(SDMSResourceReqStatesTableGeneric.tableName,           new SDMSResourceReqStatesTable(env));
		tables.put(SDMSResourceRequirementTableGeneric.tableName,         new SDMSResourceRequirementTable(env));
		tables.put(SDMSResourceStateDefinitionTableGeneric.tableName,     new SDMSResourceStateDefinitionTable(env));
		tables.put(SDMSResourceStateMappingTableGeneric.tableName,        new SDMSResourceStateMappingTable(env));
		tables.put(SDMSResourceStateMappingProfileTableGeneric.tableName, new SDMSResourceStateMappingProfileTable(env));
		tables.put(SDMSResourceStateProfileTableGeneric.tableName,        new SDMSResourceStateProfileTable(env));
		tables.put(SDMSResourceStateTableGeneric.tableName,               new SDMSResourceStateTable(env));
		tables.put(SDMSResourceVariableTableGeneric.tableName,            new SDMSResourceVariableTable(env));
		tables.put(SDMSResourceTableGeneric.tableName,                    new SDMSResourceTable(env));
		tables.put(SDMSRunnableQueueTableGeneric.tableName,               new SDMSRunnableQueueTable(env));
		// S
		tables.put(SDMSScheduledEventTableGeneric.tableName,              new SDMSScheduledEventTable(env));
		tables.put(SDMSScheduleTableGeneric.tableName,                    new SDMSScheduleTable(env));
		tables.put(SDMSSchedulingEntityTableGeneric.tableName,            new SDMSSchedulingEntityTable(env));
		tables.put(SDMSSchedulingHierarchyTableGeneric.tableName,         new SDMSSchedulingHierarchyTable(env));
		tables.put(SDMSScopeTableGeneric.tableName,                       new SDMSScopeTable(env));
		tables.put(SDMSScopeConfigTableGeneric.tableName,                 new SDMSScopeConfigTable(env));
		tables.put(SDMSScopeConfigEnvMappingTableGeneric.tableName,       new SDMSScopeConfigEnvMappingTable(env));
		tables.put(SDMSSmeCounterTableGeneric.tableName,                  new SDMSSmeCounterTable(env));
		tables.put(SDMSSubmittedEntityTableGeneric.tableName,             new SDMSSubmittedEntityTable(env));
		// T
		tables.put(SDMSTriggerTableGeneric.tableName,                     new SDMSTriggerTable(env));
		tables.put(SDMSTriggerQueueTableGeneric.tableName,                new SDMSTriggerQueueTable(env));
		tables.put(SDMSTriggerStateTableGeneric.tableName,                new SDMSTriggerStateTable(env));
		// U
		tables.put(SDMSUserTableGeneric.tableName,                        new SDMSUserTable(env));

		// non persistent tables
		tables.put(SDMSnpJobFootprintTableGeneric.tableName,              new SDMSnpJobFootprintTable(env));
		tables.put(SDMSnpSrvrSRFootprintTableGeneric.tableName,           new SDMSnpSrvrSRFootprintTable(env));
	}

	private void loadTables(SystemEnvironment env) throws SDMSException
	{
		tableIterator = tables.values().iterator();

		TableLoader tl[] = new TableLoader[SystemEnvironment.dbLoaders];

		for(int i = 0; i < SystemEnvironment.dbLoaders; i++) {
			tl[i] = new TableLoader(i, env);
		}

		for(int i = 0; i < SystemEnvironment.dbLoaders; i++) {
			tl[i].start();
		}

		for(int i = 0; i < SystemEnvironment.dbLoaders; i++) {
			try {
				tl[i].join();
			} catch (InterruptedException e) {
				i--;
				continue;
			}
			tl[i] = null;
		}
		if(loaderException != null) throw loaderException;
	}

	protected SDMSTable getNextTable()
	{
		synchronized(lockObject) {
			if(tableIterator.hasNext())
				return (SDMSTable) tableIterator.next();
		}
		return null;
	}

	protected void notify(SDMSException e)
	{
		synchronized(lockObject) {
			loaderException = e;
		}
	}

	public Iterator getTableIterator(SystemEnvironment sysEnv)
	throws SDMSException
	{
		return tables.values().iterator();
	}

	public SDMSTable getTable(SystemEnvironment env, String n)
	throws SDMSException
	{
		SDMSTable retVal = (SDMSTable) tables.get(n);
		if(retVal == null) {
			throw new FatalException(new SDMSMessage(env, "03110231115", "Table $1 not found", n));
		}
		return retVal;
	}

	public SDMSTable userGetTable(SystemEnvironment env, String n)
	throws SDMSException
	{
		SDMSTable retVal = (SDMSTable) tables.get(n);
		if(retVal == null) {
			throw new CommonErrorException(new SDMSMessage(env, "03202011519", "Table $1 not found", n));
		}
		return retVal;
	}

}

class TableLoader extends SDMSThread
{

	private int id = 0;
	private SystemEnvironment sysEnv = null;

	public TableLoader(int i, SystemEnvironment env)
	{
		super();
		id = i;
		try {
			sysEnv = (SystemEnvironment) env.clone();
		} catch (CloneNotSupportedException cnse) {
			SystemEnvironment.repository.notify(new FatalException(new SDMSMessage(sysEnv, "03305091630", "Error Cloning System Environment")));
		}
		try {
			sysEnv.dbConnection = Server.connectToDB(env);
		} catch (SDMSException e) {
			SystemEnvironment.repository.notify(e);
		}
	}

	public int id()
	{
		return id;
	}

	public void SDMSrun()
	{
		SDMSTable t;

		doTrace(null, "TableLoader " + id + " started", SEVERITY_INFO);
		try {
			try {
				while((t = SystemEnvironment.repository.getNextTable()) != null) {
					t.loadTable(sysEnv);
					sysEnv.dbConnection.commit();
				}
			} catch (SQLException sqle) {
				throw new FatalException(new SDMSMessage(sysEnv, "03110181536",
				                         "SQL Error : " + sqle.getMessage()));
			}
		} catch (SDMSException e) {
			SystemEnvironment.repository.notify(e);
		} finally {
			try {
				sysEnv.dbConnection.close();
			} catch (SQLException sqle) {
				SystemEnvironment.repository.notify(new FatalException(new SDMSMessage(sysEnv, "03305091627",
				                                    "SQL Error : " + sqle.getMessage())));
			}
		}
	}

}

