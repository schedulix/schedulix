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

public class RenewTicketThread extends SDMSThread
{

	public final static String __version = "@(#) $Id: RenewTicketThread.java,v 2.10.2.1 2013/03/14 10:24:09 ronald Exp $";

	public  SystemEnvironment pSysEnv;
	private final static int NR = 888888888;
	private final static int TICKETINTERVAL = 60 * 1000;
	private final static int TICKET_TOO_OLD = 3;
	private boolean postgres = false;
	private String updateString;
	private String selectString;
	private String insertString;
	private String deleteString;
	private String getTicketString;
	private String lockString;
	private String brokenPostgresSelect;
	private PreparedStatement pUpdate;
	private PreparedStatement pSelect;
	private PreparedStatement pGetTicket;
	private PreparedStatement pInsert;
	private PreparedStatement pDelete;
	private PreparedStatement pLock;
	private PreparedStatement pBPSelect;

	public RenewTicketThread(Server s)
	{
		super("TicketThread");
		pSysEnv = null;
	}

	public int id()
	{
		return -NR;
	}

	public void initRenewTicketThread(SystemEnvironment env)
		throws SDMSException
	{
		try {
			pSysEnv = (SystemEnvironment) env.clone();
		} catch(CloneNotSupportedException cnse) {
			throw new FatalException(new SDMSMessage(pSysEnv, "03302061654",
							"Cannot Clone SystemEnvironment"));
		}
		try {
			pSysEnv.dbConnection = Server.connectToDB(pSysEnv);
		} catch(SDMSException e) {
			doTrace(null, "Error while unlocking repository (couldn't get database connection)", SEVERITY_ERROR);
			throw e;
		}
		try {

			if(pSysEnv.dbConnection.getMetaData().getDriverName().startsWith("PostgreSQL")) {
				postgres = true;
			}
		} catch (SQLException sqle) {
			doTrace(null, "Error collecting Driver Information", SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(pSysEnv, "03302061655",
							"Error collecting Driver Information"));
		}

		updateString = "UPDATE REPOSITORY_LOCK " +
			(postgres ? "SET TICKET = CAST (? AS DECIMAL) WHERE TS = CAST (? AS DECIMAL)" :
			" SET TICKET = ? WHERE TS = ?");
		selectString = "SELECT TICKET FROM REPOSITORY_LOCK " +
			(postgres ? "WHERE TS = CAST (? AS DECIMAL)" :
			"WHERE TS = ?");
		getTicketString = "SELECT TS, TICKET FROM REPOSITORY_LOCK";
		insertString = "INSERT INTO REPOSITORY_LOCK ( LOCKID, TS, TICKET ) VALUES ( 1 , ?, ?)";
		deleteString = "DELETE FROM REPOSITORY_LOCK";
		lockString = "UPDATE REPOSITORY_LOCK SET TS = TS+0";
		brokenPostgresSelect = "SELECT COUNT(*) FROM REPOSITORY_LOCK WHERE TS = CAST (? AS DECIMAL)";

		return;
	}

	public synchronized boolean checkTicket(SystemEnvironment sysEnv)
		throws SDMSException
	{
		long ticket = 0;

		try {
			pSelect = sysEnv.dbConnection.prepareStatement(selectString);
			pSelect.clearParameters();
			if(postgres) {
				pSelect.setString(1, "" + SystemEnvironment.startTime);
			} else {
				pSelect.setLong(1, SystemEnvironment.startTime);
			}
			ResultSet rset = pSelect.executeQuery();
			while(rset.next()) {
				ticket = rset.getLong(1);
			}
			pSelect.close();
		} catch (SQLException sqle) {
			doTrace(null, "Error while getting Ticket: " + sqle.getMessage(), SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(sysEnv, "03302071708",
							"Error while getting Ticket"));
		}

		if(ticket != 0) return true;
		return false;
	}

	public synchronized void getTicket(SystemEnvironment sysEnv)
		throws SDMSException
	{
		long ts = 0;
		long ticket = 0;
		long oldticket = 0;
		int nrRows;

		try {
			pGetTicket = sysEnv.dbConnection.prepareStatement(getTicketString);
			pInsert = sysEnv.dbConnection.prepareStatement(insertString);
			while(true) {

				try {
					SDMSThread.doTrace(null, "Acquire repository lock for " + SystemEnvironment.startTime, SDMSThread.SEVERITY_INFO);
					pInsert.clearParameters();
					pInsert.setLong(1, SystemEnvironment.startTime);
					pInsert.setLong(2, System.currentTimeMillis());
					pInsert.executeUpdate();
					pInsert.close();
					break;
				} catch (SQLException sqle) {

					try {
						sysEnv.dbConnection.rollback();
					} catch (SQLException sqle2) {  }
				}
				SDMSThread.doTrace(null, "Lock Acquisition for " + SystemEnvironment.startTime + " failed", SDMSThread.SEVERITY_INFO);

				lockTicket(sysEnv);
				nrRows = updateTicket(sysEnv);

				if(nrRows == 0) {

					ResultSet rset = pGetTicket.executeQuery();
					while(rset.next()) {
						ts = rset.getLong(1);
						ticket = rset.getLong(2);
					}
					SDMSThread.doTrace(null, "Ticket values Read: " + ts + ", " + ticket, SDMSThread.SEVERITY_INFO);
					if(ts != 0) {
						if(!SystemEnvironment.singleServer) {

							if(oldticket != 0) {
								SDMSThread.doTrace(null, "old/new Ticket values : " + oldticket + ", " + ticket, SDMSThread.SEVERITY_INFO);

								if(ticket != oldticket) {
									oldticket = 0;
									continue;
								}
							} else {
								sysEnv.dbConnection.commit();
								oldticket = ticket;

								long now = System.currentTimeMillis() + TICKETINTERVAL * TICKET_TOO_OLD;
								while(now > System.currentTimeMillis()) {
									try {
										sleep(TICKETINTERVAL);
									} catch(Exception e) {  }
								}
								continue;
							}
						}

						deleteTicket(sysEnv);
						SDMSThread.doTrace(null, "ticket deleted ..... ", SDMSThread.SEVERITY_INFO);
					}

				} else {

					break;
				}
			}
			sysEnv.dbConnection.commit();
		} catch(SQLException sqle) {
			doTrace(null, "Error while getting Ticket: " + sqle.getMessage(), SEVERITY_ERROR);
			throw new FatalException(new SDMSMessage(sysEnv, "03302111630", "Error while getting Ticket"));
		}
		return;
	}

	private synchronized int lockTicket(SystemEnvironment sysEnv)
		throws SQLException
	{
		int rc;
		pLock = sysEnv.dbConnection.prepareStatement(lockString);
		pLock.clearParameters();
		rc = pLock.executeUpdate();
		pLock.close();
		return rc;
	}

	private synchronized int deleteTicket(SystemEnvironment sysEnv)
		throws SQLException
	{
		int rc;
		pDelete = sysEnv.dbConnection.prepareStatement(deleteString);
		pDelete.clearParameters();
		rc = pDelete.executeUpdate();
		pDelete.close();
		return rc;
	}

	private synchronized int updateTicket(SystemEnvironment sysEnv)
		throws SQLException
	{
		int rc;
		long newTicket = System.currentTimeMillis();
		pUpdate = sysEnv.dbConnection.prepareStatement(updateString);
		pUpdate.clearParameters();
		if(postgres) {
			pUpdate.setString(1, "" + newTicket);
			pUpdate.setString(2, "" + SystemEnvironment.startTime);
		} else {
			pUpdate.setLong(1, newTicket);
			pUpdate.setLong(2, SystemEnvironment.startTime);
		}
		rc = pUpdate.executeUpdate();
		pUpdate.close();

		if(postgres) {
			pBPSelect = sysEnv.dbConnection.prepareStatement(brokenPostgresSelect);
			pBPSelect.setString(1, "" + SystemEnvironment.startTime);
			ResultSet rset = pBPSelect.executeQuery();
			while(rset.next()) {
				rc = rset.getInt(1);
			}
		}
		return rc;
	}

	public synchronized void renewTicket(SystemEnvironment sysEnv)
		throws SDMSException
	{
		int nrRows;

		try {
			nrRows = updateTicket(sysEnv);
			if(nrRows == 0) {
				throw new FatalException(new SDMSMessage(sysEnv, "03302071040",
									"Error while setting Ticket (no rows updated)"));
			}
		} catch (SQLException sqle) {
			throw new FatalException(new SDMSMessage(sysEnv, "03302061656", "Error while setting Ticket"));
		}

		return;
	}

	public void SDMSrun()
	{
		if(pSysEnv == null) doTrace(null, "Uninitialized Thread. Call to initRenewTicketThread() missing", SEVERITY_FATAL);

		try {
			while(run) {
				renewTicket(pSysEnv);
				try {
					pSysEnv.dbConnection.commit();
				} catch (SQLException sqle) {
					throw new FatalException(new SDMSMessage(pSysEnv, "03302061657", "Error while committing transaction"));
				}
				try {
					sleep(TICKETINTERVAL);
				} catch(Exception e) {

				}
			}
		} catch(SDMSException e) {
			doTrace(null, "Error occurred : " + e.toString(), SEVERITY_FATAL);
		}
		try {
			pSysEnv.dbConnection.close();
		} catch(SQLException sqle) {
			doTrace(null, "Error while closing connection: " + sqle.getMessage(), SEVERITY_ERROR);
		}

		return;
	}

}

