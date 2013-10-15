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

	public SDMSRepository(SystemEnvironment env) throws SDMSException
	{
		SystemEnvironment.repository = this;

		env.dbConnection = Server.connectToDB(env);

		tables = new HashMap(60);

		env.lowestActiveDate = getLowestActiveDate(env);
		env.lowestActiveVersion = getLowestActiveVersion(env);
		initMap(env);
		loadTables(env);

		try {
			env.dbConnection.close();
		} catch(SQLException sqle) {
			throw new FatalException(new SDMSMessage(env, "03110181535",
			                         "SQL Error : $1", sqle.getMessage()));

		}
	}

	private long getLowestActiveDate(SystemEnvironment env)
	throws SDMSException
	{
		java.util.Date d = new java.util.Date();
		long now = d.getTime();

		long preserveTime = SystemEnvironment.preserveTime;

		return now - preserveTime;
	}

	private long getLowestActiveVersion(SystemEnvironment env)
	throws SDMSException
	{
		PreparedStatement stmt1;
		boolean postgres = false;

		try {

			if(env.dbConnection.getMetaData().getDriverName().startsWith("PostgreSQL")) {
				postgres = true;
			}
		} catch (SQLException sqle) {
			throw new FatalException(new SDMSMessage(env, "03202202228", "Error collecting Driver Information"));
		}

		String s1 = "SELECT MIN(m.SE_VERSION) " +
		            "  FROM SUBMITTED_ENTITY m " +
		            " WHERE (m.STATE NOT IN ( ?, ? ) " +
		            (postgres ? "    OR  (m.FINAL_TS >= CAST (? AS DECIMAL) " :
		             "    OR  (m.FINAL_TS >= ? ") +
		            "   AND m.STATE IN ( ?, ? )))";
		try {
			stmt1 = env.dbConnection.prepareStatement(s1);

			stmt1.clearParameters();

			stmt1.setInt(1, SDMSSubmittedEntity.FINAL);
			stmt1.setInt(2, SDMSSubmittedEntity.CANCELLED);

			if(postgres) {
				stmt1.setString(3, "" + env.lowestActiveDate);
			} else {
				stmt1.setLong(3, env.lowestActiveDate);
			}

			stmt1.setInt(4, SDMSSubmittedEntity.FINAL);
			stmt1.setInt(5, SDMSSubmittedEntity.CANCELLED);

			ResultSet rs = stmt1.executeQuery();
			rs.next();
			long version = rs.getLong(1);
			if (rs.wasNull())
				version = Long.MAX_VALUE;

			env.dbConnection.commit();

			return version;
		} catch (SQLException sqle) {
			throw new FatalException(new SDMSMessage(env, "03202011808", "SQLError $1 in select", sqle.getMessage()));
		}
	}

	private void initMap(SystemEnvironment env)  throws SDMSException
	{

		tables.put(SDMSCalendarTableGeneric.tableName,                    new SDMSCalendarTable(env));

		tables.put(SDMSDependencyDefinitionTableGeneric.tableName,        new SDMSDependencyDefinitionTable(env));
		tables.put(SDMSDependencyInstanceTableGeneric.tableName,          new SDMSDependencyInstanceTable(env));
		tables.put(SDMSDependencyStateTableGeneric.tableName,             new SDMSDependencyStateTable(env));

		tables.put(SDMSEntityVariableTableGeneric.tableName,              new SDMSEntityVariableTable(env));
		tables.put(SDMSEnvironmentTableGeneric.tableName,                 new SDMSEnvironmentTable(env));
		tables.put(SDMSEventParameterTableGeneric.tableName,              new SDMSEventParameterTable(env));
		tables.put(SDMSEventTableGeneric.tableName,                       new SDMSEventTable(env));
		tables.put(SDMSExitStateDefinitionTableGeneric.tableName,         new SDMSExitStateDefinitionTable(env));
		tables.put(SDMSExitStateGeneric.tableName,                        new SDMSExitStateTable(env));
		tables.put(SDMSExitStateMappingGeneric.tableName,                 new SDMSExitStateMappingTable(env));
		tables.put(SDMSExitStateMappingProfileGeneric.tableName,          new SDMSExitStateMappingProfileTable(env));
		tables.put(SDMSExitStateProfileGeneric.tableName,                 new SDMSExitStateProfileTable(env));

		tables.put(SDMSFolderTableGeneric.tableName,                      new SDMSFolderTable(env));
		tables.put(SDMSFootprintTableGeneric.tableName,                   new SDMSFootprintTable(env));

		tables.put(SDMSGrantTableGeneric.tableName,                       new SDMSGrantTable(env));
		tables.put(SDMSGroupTableGeneric.tableName,                       new SDMSGroupTable(env));

		tables.put(SDMSHierarchyInstanceTableGeneric.tableName,           new SDMSHierarchyInstanceTable(env));

		tables.put(SDMSIgnoredDependencyTableGeneric.tableName,           new SDMSIgnoredDependencyTable(env));
		tables.put(SDMSIntervalHierarchyTableGeneric.tableName,           new SDMSIntervalHierarchyTable(env));
		tables.put(SDMSIntervalSelectionTableGeneric.tableName,           new SDMSIntervalSelectionTable(env));
		tables.put(SDMSIntervalTableGeneric.tableName,                    new SDMSIntervalTable(env));

		tables.put(SDMSKillJobTableGeneric.tableName,                     new SDMSKillJobTable(env));

		tables.put(SDMSNamedEnvironmentTableGeneric.tableName,            new SDMSNamedEnvironmentTable(env));
		tables.put(SDMSNamedResourceTableGeneric.tableName,               new SDMSNamedResourceTable(env));

		tables.put(SDMSMasterAllocationTableGeneric.tableName,            new SDMSMasterAllocationTable(env));
		tables.put(SDMSMemberTableGeneric.tableName,                      new SDMSMemberTable(env));

		tables.put(SDMSObjectCommentTableGeneric.tableName,               new SDMSObjectCommentTable(env));

		tables.put(SDMSParameterDefinitionTableGeneric.tableName,         new SDMSParameterDefinitionTable(env));
		tables.put(SDMSPersistentValueTableGeneric.tableName,             new SDMSPersistentValueTable(env));

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

		tables.put(SDMSScheduledEventTableGeneric.tableName,              new SDMSScheduledEventTable(env));
		tables.put(SDMSScheduleTableGeneric.tableName,                    new SDMSScheduleTable(env));
		tables.put(SDMSSchedulingEntityTableGeneric.tableName,            new SDMSSchedulingEntityTable(env));
		tables.put(SDMSSchedulingHierarchyTableGeneric.tableName,         new SDMSSchedulingHierarchyTable(env));
		tables.put(SDMSScopeTableGeneric.tableName,                       new SDMSScopeTable(env));
		tables.put(SDMSScopeConfigTableGeneric.tableName,                 new SDMSScopeConfigTable(env));
		tables.put(SDMSScopeConfigEnvMappingTableGeneric.tableName,       new SDMSScopeConfigEnvMappingTable(env));
		tables.put(SDMSSmeCounterTableGeneric.tableName,                  new SDMSSmeCounterTable(env));
		tables.put(SDMSSubmittedEntityTableGeneric.tableName,             new SDMSSubmittedEntityTable(env));

		tables.put(SDMSTriggerTableGeneric.tableName,                     new SDMSTriggerTable(env));
		tables.put(SDMSTriggerQueueTableGeneric.tableName,                new SDMSTriggerQueueTable(env));
		tables.put(SDMSTriggerStateTableGeneric.tableName,                new SDMSTriggerStateTable(env));

		tables.put(SDMSUserTableGeneric.tableName,                        new SDMSUserTable(env));

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

