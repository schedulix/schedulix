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
import java.text.*;
import java.net.*;
import java.sql.*;

import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.timer.TimerUnit;

public class SystemEnvironment implements Cloneable
{
	public String setStateThread = null;

	public static final String S_OPEN         = "OPEN";
	public static final String S_BASIC        = "BASIC";
	public static final String S_PROFESSIONAL = "PROFESSIONAL";
	public static final String S_ENTERPRISE   = "ENTERPRISE";

	public static final String programVersion = "2.7";
	public static String programLevel = null;

	public static final long SYSTEM_OBJECTS_BOUNDARY = 1000;

	public static final int NORMAL   = 0;
	public static final int ADMIN    = 1;
	public static final int SHUTDOWN = 2;

	public static final int UPH_ERROR = 0;
	public static final int UPH_BLANK = 1;
	public static final int UPH_ECHO  = 2;

	public static final int PSH_ERROR = 0;
	public static final int PSH_IGNORE = 1;

	private static final String NONE = "NONE";
	private static final String ALL = "ALL";

	private static final String S_LANGLEVEL261 = "2.6.1";
	public static final int LANGLEVEL261 = 0;

	private static final String S_LANGLEVEL27  = "2.7";
	public static final int LANGLEVEL27 = 1;

	private static final String LANGLEVELS[] = {
		S_LANGLEVEL261,
		S_LANGLEVEL27
	};

	private static final int DEFAULT_LANGLEVEL = LANGLEVELS.length - 1;

	public static String SQUOTE = null;
	public static String EQUOTE = null;
	public static boolean isPostgreSQL = false;

	public static SDMSRepository repository;
	public static Properties props;
	public static long startTime;
	public static long cntRwTx = 0;
	public static long cntDl = 0;
	public static long cntWl = 0;
	public static Server server;
	public static SchedulingThread sched;
	public static GarbageThread garb;
	public static TriggerThread tt;
	public static RenewTicketThread ticketThread;
	public static DBCleanupThread dbCleanupThread;
	public static TimerThread timer;
	public static ThreadGroup utg;
	public static ThreadGroup wg;
	public static NotifierThread notifier;

	public Connection dbConnection;
	public int dbConnectionNr;
	public SDMSTransaction tx;
	public ConnectionEnvironment cEnv;
	public SDMSROTxList roTxList;
	public SDMSSeVersionList seVersionList;
	public SDMSPurgeSet nvPurgeSet;
	public SDMSPurgeSet vPurgeSet;
	public static DatagramSocket notifySocket;

	public static String runMode;
	public static int traceLevel;
	public static String parameterHandling;
	public static int unresolvedParameterHandling;
	public static int parameterSyntaxHandling;
	public static TimerUnit timerHorizon;
	public static TimerUnit timerRecalc;
	public static String hostname;
	public static int port;
	public static int sslport;
	public static int service_port;
	public static int txRetryCount;
	public static long preserveTime;
	public static long maxPreserveTime;
	public static long dbPreserveTime;
	public static boolean archive;
	public static int minHistoryCount;
	public static int maxHistoryCount;
	public static int maxWorker;
	public static int maxWriter;
	public static int maxConnects;
	public static String exportVariablesString;
	public static String userExportVariablesString;
	public static String jdbcDriver;
	public static String dbUrl;
	public static String dbUser;
	public static String dbPasswd;
	public static String sysPasswd;
	public static int scheduleWakeupInterval;
	public static int notifyDelay;
	public static int priorityDelay;
	public static int priorityLowerBound;
	public static int timerWakeupInterval;
	public static int timerSuspendLimit;
	public static int timerTimeout;
	public static int gcWakeupInterval;
	public static int ttWakeupInterval;
	public static int triggerSoftLimit;
	public static int triggerHardLimit;
	public static int dbLoaders;
	public static int sessionTimeout;
	public static String compatLevel;
	public static boolean singleServer;
	public static int maxNumCalEntries;
	public static int defCalHorizon;
	public static boolean fatalIsError;
	public static String selectGroup;
	public static String auditFile;

	public static boolean strict_variables;
	public static boolean warn_variables;
	public static Vector exportVariables;
	public static Vector smeColumns;
	public static Vector atColumns;
	public static Vector diColumns;
	public static Vector evColumns;
	public static Vector hiColumns;
	public static Vector kjColumns;

	public static int dumpLangLevel;

	public static String keystore;
	public static String keystorepassword;
	public static String truststore;
	public static String truststorepassword;
	public static boolean clientAuthentication;

	private static boolean got_properties = false;

	public static final String S_ARCHIVE               = "Archive";
	public static final String S_ARCSMECOLS            = "ArchiveSMEColumns";
	public static final String S_ARCATCOLS             = "ArchiveAuditColumns";
	public static final String S_ARCDICOLS             = "ArchiveDependencyColumns";
	public static final String S_ARCEVCOLS             = "ArchiveVariableColumns";
	public static final String S_ARCHICOLS             = "ArchiveHierarchyColumns";
	public static final String S_ARCKJCOLS             = "ArchiveKillJobColumns";
	public static final String S_AUDITFILE             = "AuditFile";
	public static final String S_CALHORIZON            = "CalendarHorizon";
	public static final String S_CALENTRIES            = "CalendarEntries";
	public static final String S_DBLOADER              = "DbLoaders";
	public static final String S_DBPASSWD              = "DbPasswd";
	public static final String S_DBURL                 = "DbUrl";
	public static final String S_DBUSER                = "DbUser";
	public static final String S_DMPLANGLEVEL          = "DumpLangLevel";
	public static final String S_EXPORTVARIABLES       = "ExportVariables";
	public static final String S_GCWAKEUP              = "GCWakeup";
	public static final String S_HISTORY               = "History";
	public static final String S_DBHISTORY             = "DbHistory";
	public static final String S_HISTORYLIMIT          = "HistoryLimit";
	public static final String S_MINHISTORYCOUNT       = "MinHistoryCount";
	public static final String S_MAXHISTORYCOUNT       = "MaxHistoryCount";
	public static final String S_NOTIFYDELAY           = "NotifyDelay";
	public static final String S_HOSTNAME              = "Hostname";
	public static final String S_JDBCDRIVER            = "JdbcDriver";
	public static final String S_LEVEL                 = "CompatibilityLevel";
	public static final String S_PARAMETERHANDLING     = "ParameterHandling";
	public static final String S_PARAMETERSYNTAXHANDLING = "ParameterSyntaxHandling";
	public static final String S_PORT                  = "Port";
	public static final String S_PRIORITYDELAY         = "PriorityDelay";
	public static final String S_PRIORITYLB            = "PriorityLowerBound";
	public static final String S_RUNMODE               = "RunMode";
	public static final String S_SCHEDULEWAKEUP        = "ScheduleWakeup";
	public static final String S_SELECTGROUP           = "SelectGroup";
	public static final String S_SERVICEPORT           = "ServicePort";
	public static final String S_SESSIONTIMEOUT        = "SessionTimeout";
	public static final String S_SINGLESERVER          = "SingleServer";
	public static final String S_SSLPORT               = "SSLPort";
	public static final String S_SYSPASSWD             = "SysPasswd";
	public static final String S_TIMERHORIZON          = "TimerHorizon";
	public static final String S_TIMERRECALC           = "TimerRecalc";
	public static final String S_TIMERSUSPENDLIMIT     = "TimerSuspendLimit";
	public static final String S_TIMERTIMEOUT          = "TimerTimeout";
	public static final String S_TIMERWAKEUP           = "TimerWakeup";
	public static final String S_TRACELEVEL            = "TraceLevel";
	public static final String S_TRIGGERSOFTLIMIT      = "TriggerSoftLimit";
	public static final String S_TRIGGERHARDLIMIT      = "TriggerHardLimit";
	public static final String S_TTWAKEUP              = "TTWakeup";
	public static final String S_TXRETRYCOUNT          = "TxRetryCount";
	public static final String S_USERTHREADS           = "UserThreads";
	public static final String S_USEREXPORTVARIABLES   = "UserExportVariables";
	public static final String S_UNRESPARMHANDLING     = "UnresolvedParameterHandling";
	public static final String S_WORKERTHREADS         = "WorkerThreads";
	public static final String S_WRITERTHREADS         = "WriterThreads";
	public static final String S_KEYSTORE              = "KeyStore";
	public static final String S_KEYSTOREPASSWORD      = "KeyStorePassword";
	public static final String S_KEYSTOREPROVIDER      = "KeyStoreProvider";
	public static final String S_KEYSTORETYPE          = "KeyStoreType";
	public static final String S_TRUSTSTORE            = "TrustStore";
	public static final String S_TRUSTSTOREPASSWORD    = "TrustStorePassword";
	public static final String S_TRUSTSTOREPROVIDER    = "TrustStoreProvider";
	public static final String S_TRUSTSTORETYPE        = "TrustStoreType";
	public static final String S_CLIENTAUTHENTICATION  = "ClientAuthentication";

	public static final String J_KEYSTORE                     = "javax.net.ssl.keyStore";
	public static final String J_KEYSTOREPASSWORD             = "javax.net.ssl.keyStorePassword";
	public static final String J_KEYSTOREPROVIDER             = "javax.net.ssl.keyStoreProvider";
	public static final String J_KEYSTORETYPE                 = "javax.net.ssl.keyStoreType";
	public static final String J_TRUSTSTORE                   = "javax.net.ssl.trustStore";
	public static final String J_TRUSTSTOREPASSWORD           = "javax.net.ssl.trustStorePassword";
	public static final String J_TRUSTSTOREPROVIDER           = "javax.net.ssl.trustStoreProvider";
	public static final String J_TRUSTSTORETYPE               = "javax.net.ssl.trustStoreType";

	public static final String S_BASE_SME_ID           = "BASE_SME_ID";
	public static final String S_TRIGGER_HASHSET       = "TRIGGER_HASH_SET";
	public static final String S_CANCEL_HASHSET        = "CANCEL_HASH_SET";
	public static final String S_MASTERTRIGGER_HASHSET = "MASTERTRIGGER_HASHSET";
	public static final String S_ISDEFAULT             = "ISDEFAULT";
	public static final String S_RESOURCE_TRACE        = "RESOURCETRACE";
	public static final String S_SE_NAMEFILTER_CACHE   = "SE_NAMEFILTER_CACHE";
	public static final String S_PARAMETERFILTER_CACHE = "PARAMETERFILTER_CACHE";

	public static final String nullString = "<null>";
	public static final String defaultString = "<default>";
	public static final String noneString = "<none>";
	public static final TimeZone systemTimeZone = TimeZone.getTimeZone ("GMT");
	public static final Locale systemLocale = new Locale("EN", "GB");
	public static final SimpleDateFormat staticJSCommDateFormat = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss Z", systemLocale);
	public static final SimpleDateFormat staticOldJSCommDateFormat = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss z", systemLocale);
	public static final SimpleDateFormat staticSystemDateFormat = new SimpleDateFormat ("dd MMM yyyy HH:mm:ss z", systemLocale);
	public SimpleDateFormat systemDateFormat = (SimpleDateFormat) staticSystemDateFormat.clone();
	public SimpleDateFormat jsCommDateFormat = (SimpleDateFormat) staticJSCommDateFormat.clone();
	public SimpleDateFormat oldJsCommDateFormat = (SimpleDateFormat) staticOldJSCommDateFormat.clone();

	public static final String S_GRANTS                   = "GRANTS";
	public static final String S_CONDITIONAL_DEPENDENCIES = "CONDITIONAL_DEPENDENCIES";
	public static final String S_MILESTONES               = "MILESTONES";
	public static final String S_EXTENDED_TRIGGERS        = "EXTENDED_TRIGGERS";
	public static final String S_ASYNC_TRIGGERS           = "ASYNC_TRIGGERS";
	public static final String S_FOLDER_ENVIRONMENTS      = "FOLDER_ENVIRONMENTS";
	public static final String S_FOLDER_RESOURCES         = "FOLDER_RESOURCES";
	public static final String S_SE_RESOURCES             = "SE_RESOURCES";
	public static final String S_JOB_LEVEL_AGING_CONTROL  = "JOB_LEVEL_AGING_CONTROL";
	public static final String S_RESOURCE_TRIGGER         = "RESOURCE_TRIGGER";
	public static final String S_OBJECTMONITOR_TRIGGER    = "OBJECTMONITOR_TRIGGER";
	public static final String S_WRITABLE_RESOURCE_PARAMS = "WRITABLE_RESOURCE_PARAMS";
	public static final String S_DUMP_COMMAND             = "DUMP_COMMAND";
	public static final String S_EXTENDED_DUMP_COMMAND    = "EXTENDED_DUMP_COMMAND";
	public static final String S_RESOURCE_POOLS           = "RESOURCE_POOLS";
	public static final String S_RESOURCE_TRACING         = "RESOURCE_TRACING";
	public static final String S_EXIT_STATE_TRANSLATION   = "EXIT_STATE_TRANSLATION";
	public static final String S_POOL		      = "POOL";
	public static final String S_OBJECT_MONITOR	      = "OBJECT_MONITOR";
	public static final String S_NICE_PROFILE	      = "NICE_PROFILE";
	private HashMap featureLevels;

	public final static HashMap<Long, Long> jidsStarting = new HashMap<Long, Long>();
	public final static int startingResendDelay = 5000;

	public SDMSThread thread;
	private long lockCp;

	public long getLockCp ()
	{
		return lockCp;
	}

	public void initLockCp ()
	{
		lockCp = 0;
	}

	public long newLockCp ()
	{
		long r = lockCp;
		lockCp ++;
		return r;
	}

	public boolean inExecution = false;

	static
	{
		staticSystemDateFormat.setTimeZone (systemTimeZone);
		try {
			notifySocket = new DatagramSocket();
		} catch (Exception e) {
			notifySocket = null;
		}
	}

	public long lowestActiveVersion;
	public long lowestActiveDate;

	private MutableInteger  connectState;
	private static boolean  protectMode = false;

	private static Random random = new Random();
	private static ExecuteLock executeLock = new ExecuteLock();

	public SystemEnvironment(Properties p, String programLevel)
	{
		if (SystemEnvironment.programLevel == null)
			SystemEnvironment.programLevel = programLevel;
		props = p;
		if(! got_properties) setProperties();
		roTxList = new SDMSROTxList();
		seVersionList = new SDMSSeVersionList();
		nvPurgeSet = new SDMSPurgeSet();
		vPurgeSet = new SDMSPurgeSet();
		connectState = new MutableInteger(NORMAL);
		startTime = System.currentTimeMillis();
		featureLevels = new HashMap();
		featureLevels.put(S_GRANTS,                   new Feature(S_PROFESSIONAL, "The GRANT/REVOKE Commands"));
		featureLevels.put(S_CONDITIONAL_DEPENDENCIES, new Feature(S_ENTERPRISE,   "Conditional Dependencies"));
		featureLevels.put(S_MILESTONES,               new Feature(S_PROFESSIONAL, "Scheduling Entity Type MILESTONE"));
		featureLevels.put(S_EXTENDED_TRIGGERS,        new Feature(S_PROFESSIONAL, "Using Trigger Types other than IMMEDIATE_..., FINISH_CHILD or BEFORE_FINAL"));
		featureLevels.put(S_ASYNC_TRIGGERS,           new Feature(S_ENTERPRISE,   "Asynchronous Trigger Types"));
		featureLevels.put(S_FOLDER_ENVIRONMENTS,      new Feature(S_PROFESSIONAL, "Use of Folder Environments"));
		featureLevels.put(S_FOLDER_RESOURCES,         new Feature(S_PROFESSIONAL, "Use of Folder Resources"));
		featureLevels.put(S_SE_RESOURCES,             new Feature(S_PROFESSIONAL, "Use of Job or Batch Resources"));
		featureLevels.put(S_JOB_LEVEL_AGING_CONTROL,  new Feature(S_PROFESSIONAL, "Job Level Priority Aging Control"));
		featureLevels.put(S_RESOURCE_TRIGGER,         new Feature(S_PROFESSIONAL, "Use of Resource Triggers"));
		featureLevels.put(S_OBJECTMONITOR_TRIGGER,    new Feature(S_PROFESSIONAL, "Use of Object Monitor Triggers"));
		featureLevels.put(S_WRITABLE_RESOURCE_PARAMS, new Feature(S_PROFESSIONAL, "Writing to Resource Parameters"));
		featureLevels.put(S_DUMP_COMMAND,             new Feature(S_PROFESSIONAL, "The Dump Command"));
		featureLevels.put(S_EXTENDED_DUMP_COMMAND,    new Feature(S_PROFESSIONAL, "Use of Extended Dump Command Options DEPLOY, MAPPING, HEADER and CLEANUP"));
		featureLevels.put(S_RESOURCE_POOLS,           new Feature(S_ENTERPRISE,   "Use of Resource Pools"));
		featureLevels.put(S_RESOURCE_TRACING,         new Feature(S_ENTERPRISE,   "Resource Tracing"));
		featureLevels.put(S_EXIT_STATE_TRANSLATION,   new Feature(S_PROFESSIONAL, "Use of Exit State Translations"));
		featureLevels.put(S_POOL,		      new Feature(S_ENTERPRISE,   "Use of Pools and Distributions"));
		featureLevels.put(S_OBJECT_MONITOR,	      new Feature(S_PROFESSIONAL, "Use of Object Monitors"));
		featureLevels.put(S_NICE_PROFILE,	      new Feature(S_ENTERPRISE,   "Use of Nice Profiles"));
	}

	private void setProperties()
	{
		getSimpleValues();
		getPropsTraceLevel();
		getArchive();
		getArchiveCols();
		getParameterHandling();
		getUnresolvedParameterHandling();
		getParameterSyntaxHandling();
		getTimerHorizon();
		getTimerRecalc();
		getPort();
		getServicePort();
		getTxRetryCount();
		getHistory();
		getDbHistory();
		getHistoryLimit();
		getMinHistoryCount();
		getMaxHistoryCount();
		getNotifyDelay();
		getWorkerThreads();
		getWriterThreads();
		getUserThreads();
		getExportVariables();
		getScheduleWakeup();
		getGCWakeup();
		getTTWakeup();
		getPriorityDelay();
		getPriorityLB();
		getSessionTimeout();
		getSysPasswd();
		getTimerWakeup();
		getTimerSuspendLimit();
		getTimerTimeout();
		getDbLoader();
		getCompatLevel();
		getSingleServer();
		getMaxNumCalEntries();
		getCalHorizon();

		getTriggerSoftLimit();
		getTriggerHardLimit();

		if (checkCompatLevel(S_ENTERPRISE)) {
			getAuditFile();
		}

		getSSLproperties();

		if ((port == 0) && (service_port == 0) && (sslport == 0)) {
			port = 2506;
		}

		getDumpLangLevel();

		got_properties = true;
	}

	private void getSimpleValues()
	{
		runMode = props.getProperty(S_RUNMODE, "Production").toUpperCase();
		props.setProperty(S_RUNMODE, runMode);
		if (runMode.equals("TEST"))
			fatalIsError = false;
		else
			fatalIsError = true;

		hostname = props.getProperty(S_HOSTNAME, "localhost");
		props.setProperty(S_HOSTNAME, hostname);

		jdbcDriver = props.getProperty(S_JDBCDRIVER);
		dbUrl = props.getProperty(S_DBURL);
		dbUser = props.getProperty(S_DBUSER, "");
		props.setProperty(S_DBUSER, dbUser);

		dbPasswd = props.getProperty(S_DBPASSWD, "");
		props.remove(S_DBPASSWD);
		selectGroup = props.getProperty(S_SELECTGROUP);
		if (selectGroup != null) {
			selectGroup = selectGroup.toUpperCase();
		}
	}

	private void getCompatLevel()
	{
		compatLevel = props.getProperty(S_LEVEL, programLevel);
		if (programLevel.equals(S_OPEN))
			compatLevel = S_BASIC;
		if (!compatLevel.equals(programLevel)) {
			if (programLevel.equals(S_PROFESSIONAL) && !compatLevel.equals(S_BASIC)) {
				compatLevel = S_PROFESSIONAL;
			}
			if (programLevel.equals(S_BASIC))
				compatLevel = programLevel;
		}
		props.setProperty(S_LEVEL, "" + compatLevel);
	}

	private void getSSLproperties()
	{
		getSSLPort();
		if (sslport != 0) {
			keystore = props.getProperty(S_KEYSTORE);
			if (keystore == null) {
				keystore = System.getProperty(J_KEYSTORE);
			}
			if (keystore != null) {
				System.setProperty(J_KEYSTORE, keystore);
				props.setProperty(S_KEYSTORE, keystore);
			} else {
				sslport = 0;
				return;
			}

			keystorepassword = props.getProperty(S_KEYSTOREPASSWORD);
			if (keystorepassword == null) {
				keystorepassword = System.getProperty(J_KEYSTOREPASSWORD);
			}
			if (keystorepassword != null) {
				System.setProperty(J_KEYSTOREPASSWORD, keystorepassword);
				props.setProperty(S_KEYSTOREPASSWORD, keystorepassword);
			}

			truststore = props.getProperty(S_TRUSTSTORE);
			if (truststore == null) {
				truststore = System.getProperty(J_TRUSTSTORE);
			}
			if (truststore != null) {
				System.setProperty(J_TRUSTSTORE, truststore);
				props.setProperty(S_TRUSTSTORE, truststore);
			}

			truststorepassword = props.getProperty(S_TRUSTSTOREPASSWORD);
			if (truststorepassword == null) {
				truststorepassword = System.getProperty(J_TRUSTSTOREPASSWORD);
			}
			if (truststorepassword != null) {
				System.setProperty(J_TRUSTSTOREPASSWORD, truststorepassword);
				props.setProperty(S_TRUSTSTOREPASSWORD, truststorepassword);
			}

			clientAuthentication = Boolean.parseBoolean(props.getProperty(S_CLIENTAUTHENTICATION, "false").trim());
		}
	}

	public boolean checkCompatLevel(String requiredLevel)
	{
		if (compatLevel.equals(requiredLevel)) return true;
		if (compatLevel.equals(S_ENTERPRISE)) return true;
		if (compatLevel.equals(S_PROFESSIONAL) && requiredLevel.equals(S_BASIC)) return true;
		return false;
	}

	public void checkFeatureAvailability(String feature)
		throws SDMSException
	{
		Feature f = (Feature)featureLevels.get(feature);
		if (f == null) {
			throw new CommonErrorException(new SDMSMessage(this, "02803040840", "Availability Check on unknown Feature $1", feature));
		}
		if (!checkCompatLevel(f.requiredLevel)) {
			throw new CommonErrorException(new SDMSMessage(this, "02803040845", "$1 requires Version $2 or higher", f.featureText, f.requiredLevel));
		}
	}

	public boolean checkFeature (String feature)
		throws SDMSException
	{
		Feature f = (Feature)featureLevels.get(feature);
		if (f == null) {
			throw new CommonErrorException(new SDMSMessage(this, "02803040840", "Availability Check on unknown Feature $1", feature));
		}
		if (checkCompatLevel(f.requiredLevel)) {
			return true;
		}
		return false;
	}

	private void getAuditFile()
	{
		auditFile = props.getProperty(S_AUDITFILE);
	}

	private void getArchive()
	{
		String s_archive = props.getProperty(S_ARCHIVE, "false");
		archive = Boolean.parseBoolean(s_archive.trim());
		props.setProperty(S_ARCHIVE, archive ? "true" : "false" );
	}

	private void getPropsTraceLevel()
	{
		String s_traceLevel = props.getProperty(S_TRACELEVEL, "1");
		traceLevel = checkIntProperty(s_traceLevel, S_TRACELEVEL, 0, 1, 3, "Invalid trace level : ");
		props.setProperty(S_TRACELEVEL, "" + traceLevel);
	}

	private void getNotifyDelay()
	{
		String s_notifyDelay = props.getProperty(S_NOTIFYDELAY, "1");
		notifyDelay = checkIntProperty(s_notifyDelay, S_NOTIFYDELAY, 1, 1, 5, "Invalid notify delay: ");
		props.setProperty(S_NOTIFYDELAY, "" + notifyDelay);
	}

	private void getTimerHorizon()
	{
		final String s_timerHorizon = props.getProperty (S_TIMERHORIZON, "5");
		final int horizon = checkIntProperty(s_timerHorizon, S_TIMERHORIZON, 1, 5, 0, "Invalid TimerThread horizon : ");
		timerHorizon = new TimerUnit (horizon, TimerUnit.YEAR);
		props.setProperty(S_TIMERHORIZON, "" + horizon);
	}

	private void getTimerRecalc()
	{
		final String s_timerRecalc = props.getProperty (S_TIMERRECALC, "5");
		final int recalc = checkIntProperty(s_timerRecalc, S_TIMERRECALC, 1, 5, 0, "Invalid TimerThread recalc : ");
		timerRecalc = new TimerUnit (recalc, TimerUnit.DAY);
		props.setProperty(S_TIMERRECALC, "" + recalc);
	}

	private void getPort()
	{
		String s_port = props.getProperty(S_PORT, "0");
		port = checkIntProperty(s_port, S_PORT, 0, 2506, 0, "Invalid port number : ");
		props.setProperty(S_PORT, "" + port);
	}

	private void getSSLPort()
	{
		String s_port = props.getProperty(S_SSLPORT, "0");
		sslport = checkIntProperty(s_port, S_SSLPORT, 0, 2507, 0, "Invalid port number : ");
		props.setProperty(S_SSLPORT, "" + sslport);
	}

	private void getServicePort()
	{
		String s_port = props.getProperty(S_SERVICEPORT, "0");
		service_port = checkIntProperty(s_port, S_SERVICEPORT, 0, 2505, 0, "Invalid port number : ");
		props.setProperty(S_SERVICEPORT, "" + service_port);
	}

	private void getSessionTimeout()
	{
		String s_timeout = props.getProperty(S_SESSIONTIMEOUT, "60");
		sessionTimeout = checkIntProperty(s_timeout, S_SESSIONTIMEOUT, 0, 60, 0, "Invalid timeout value: ");
		props.setProperty(S_SESSIONTIMEOUT, "" + sessionTimeout);
	}

	private void getTxRetryCount()
	{
		String s_txRetryCount = props.getProperty(S_TXRETRYCOUNT, "1");
		txRetryCount = checkIntProperty(s_txRetryCount, S_TXRETRYCOUNT, 1, 1, 0, "Invalid retry count : ");
		props.setProperty(S_TXRETRYCOUNT, "" + txRetryCount);
	}

	private void getHistory()
	{
		String s_preserveTime = props.getProperty(S_HISTORY, "14400");
		int minTime = 60;
		if (runMode.equals("TEST")) minTime = 5;
		preserveTime = checkIntProperty(s_preserveTime, S_HISTORY, minTime, 14400, 0, "Invalid History : ");
		props.setProperty(S_HISTORY, "" + preserveTime);
		preserveTime *= 1000 * 60;
	}
	private void getDbHistory()
	{
		String s_dbPreserveTime = props.getProperty(S_DBHISTORY, "0");
		dbPreserveTime = checkIntProperty(s_dbPreserveTime, S_DBHISTORY, 0, 0, 0, "Invalid DbHistory : ");
		props.setProperty(S_DBHISTORY, "" + dbPreserveTime);
		dbPreserveTime *= 1000 * 60;
	}

	private void getHistoryLimit()
	{
		String s_preserveTime = props.getProperty(S_HISTORYLIMIT, "14400");
		int minTime = (int) preserveTime / (60 * 1000);
		if (runMode.equals("TEST")) minTime = 5;
		maxPreserveTime = checkIntProperty(s_preserveTime, S_HISTORYLIMIT, minTime, 14400, 0, "Invalid HistoryLimit : ");
		props.setProperty(S_HISTORYLIMIT, "" + maxPreserveTime);
		maxPreserveTime *= 1000 * 60;
	}

	private void getMinHistoryCount()
	{
		String s_historyCount = props.getProperty(S_MINHISTORYCOUNT, "0");
		minHistoryCount = checkIntProperty(s_historyCount, S_MINHISTORYCOUNT, 0, 0, 0, "Invalid MinHistoryCount : ");
		props.setProperty(S_MINHISTORYCOUNT, "" + minHistoryCount);
	}

	private void getMaxHistoryCount()
	{
		String s_historyCount = props.getProperty(S_MAXHISTORYCOUNT, "0");
		if (s_historyCount.equals("0"))
			maxHistoryCount = 0;
		else
			maxHistoryCount = checkIntProperty(s_historyCount, S_MAXHISTORYCOUNT, minHistoryCount, 0, 0, "Invalid MaxHistoryCount : ");
		props.setProperty(S_MAXHISTORYCOUNT, "" + maxHistoryCount);
	}

	private void getCalHorizon()
	{
		String s_calHorizon = props.getProperty(S_CALHORIZON, "62");
		defCalHorizon = checkIntProperty(s_calHorizon, S_CALHORIZON, 0, 62, 0, "Invalid Calendar Horizon : ");
		props.setProperty(S_CALHORIZON, "" + defCalHorizon);
	}

	private void getMaxNumCalEntries()
	{
		String s_maxNumCalEntries = props.getProperty(S_CALENTRIES, "300");
		maxNumCalEntries = checkIntProperty(s_maxNumCalEntries, S_CALENTRIES, 0, 300, 0, "Invalid Number of Calendar Entries : ");
		props.setProperty(S_CALENTRIES, "" + maxNumCalEntries);
	}

	private void getWorkerThreads()
	{
		String s_maxWorker = props.getProperty(S_WORKERTHREADS, "2");
		maxWorker = checkIntProperty(s_maxWorker, S_WORKERTHREADS, 0, 2, 0, "Invalid number of RO Worker : ");
		props.setProperty(S_WORKERTHREADS, "" + maxWorker);
	}

	private void getWriterThreads()
	{
		String s_maxWriter = props.getProperty(S_WRITERTHREADS, "1");
		maxWriter = checkIntProperty(s_maxWriter, S_WRITERTHREADS, 1, 1, 128, "Invalid number of RW Worker : ");
		props.setProperty(S_WRITERTHREADS, "" + maxWriter);
	}

	private void getUserThreads()
	{
		String s_maxConnects = props.getProperty(S_USERTHREADS, "10");
		maxConnects = checkIntProperty(s_maxConnects, S_USERTHREADS, 3, 10, 0, "Invalid number of Connections : ");
		props.setProperty(S_USERTHREADS, "" + maxConnects);
	}

	private void getScheduleWakeup()
	{
		String s_scheduleWakeupInterval = props.getProperty(S_SCHEDULEWAKEUP, "30");
		scheduleWakeupInterval = checkIntProperty(s_scheduleWakeupInterval, S_SCHEDULEWAKEUP, 1, 30, 0,
								"Invalid wakeup interval for scheduler : ");
		props.setProperty(S_SCHEDULEWAKEUP, "" + scheduleWakeupInterval);
	}

	private void getGCWakeup()
	{
		String s_GCWakeupInterval = props.getProperty(S_GCWAKEUP, "240");
		gcWakeupInterval = checkIntProperty(s_GCWakeupInterval, S_GCWAKEUP, 1, 240, 0,
								"Invalid wakeup interval for garbage collector : ");
		props.setProperty(S_GCWAKEUP, "" + gcWakeupInterval);
	}

	private void getTTWakeup()
	{
		String s_TTWakeupInterval = props.getProperty(S_TTWAKEUP, "30");
		ttWakeupInterval = checkIntProperty(s_TTWakeupInterval, S_TTWAKEUP, 10, 30, 0,
								"Invalid wakeup interval for trigger thread : ");
		props.setProperty(S_TTWAKEUP, "" + ttWakeupInterval);
	}

	private void getPriorityDelay()
	{
		String s_priorityDelay = props.getProperty(S_PRIORITYDELAY, "30");
		priorityDelay = checkIntProperty(s_priorityDelay, S_PRIORITYDELAY, 1, 30, 0, "Invalid priority Delay : ");
		props.setProperty(S_PRIORITYDELAY, "" + priorityDelay);
	}

	private void getPriorityLB()
	{
		String s_priorityLB = props.getProperty(S_PRIORITYLB, "10");
		priorityLowerBound = checkIntProperty(s_priorityLB, S_PRIORITYLB, 0, 10, 20, "Invalid priority Lower Bound : ");
		props.setProperty(S_PRIORITYLB, "" + priorityLowerBound);
	}

	private void getSingleServer()
	{
		String s_singleServer = props.getProperty(S_SINGLESERVER, "false");
		singleServer = Boolean.parseBoolean(s_singleServer.trim());
		props.setProperty(S_SINGLESERVER, singleServer ? "true" : "false" );
	}

	private void getSysPasswd()
	{
		sysPasswd = props.getProperty(S_SYSPASSWD, "G0H0ME");
		props.remove(S_SYSPASSWD);
	}

	private void getTimerWakeup()
	{
		String s_timerWakeupInterval = props.getProperty (S_TIMERWAKEUP, "30");
		timerWakeupInterval = checkIntProperty(s_timerWakeupInterval, S_TIMERWAKEUP, 10, 30, 0, "Invalid timer wakeup: ");
		props.setProperty(S_TIMERWAKEUP, "" + timerWakeupInterval);
		timerWakeupInterval *= 1000;
	}

	private void getTimerSuspendLimit()
	{
		String s_timerSuspendLimit = props.getProperty (S_TIMERSUSPENDLIMIT, "15");
		timerSuspendLimit = checkIntProperty(s_timerSuspendLimit, S_TIMERSUSPENDLIMIT, 1, 15, 0, "Invalid timer suspend: ");
		props.setProperty(S_TIMERSUSPENDLIMIT, "" + timerSuspendLimit);
	}

	private void getTimerTimeout()
	{
		String s_timerTimeout = props.getProperty (S_TIMERTIMEOUT, "10");
		timerTimeout = checkIntProperty(s_timerTimeout, S_TIMERTIMEOUT, 1, 10, 0, "Invalid timer timeout: ");
		props.setProperty(S_TIMERTIMEOUT, "" + timerTimeout);
	}

	private void getTriggerSoftLimit()
	{
		String s_triggerSoftLimit = props.getProperty (S_TRIGGERSOFTLIMIT, "50");
		triggerSoftLimit = checkIntProperty(s_triggerSoftLimit, S_TRIGGERSOFTLIMIT, 1, 50, 0, "Invalid trigger soft limit: ");
		props.setProperty(S_TRIGGERSOFTLIMIT, "" + triggerSoftLimit);
	}

	private void getTriggerHardLimit()
	{
		String s_triggerHardLimit = props.getProperty (S_TRIGGERHARDLIMIT, "100");
		triggerHardLimit = checkIntProperty(s_triggerHardLimit, S_TRIGGERHARDLIMIT, triggerSoftLimit, 100, 0,
								"Invalid trigger hard limit: ");
		props.setProperty(S_TRIGGERHARDLIMIT, "" + triggerHardLimit);
	}

	private void getDbLoader()
	{
		int numProc = Runtime.getRuntime().availableProcessors();
		int numLoad = Math.min(5, numProc);
		String s_dbLoaders = props.getProperty (S_DBLOADER, "" + numLoad);
		dbLoaders = checkIntProperty(s_dbLoaders, S_DBLOADER, 1, numLoad, 0, "Invalid number of database loaders : ");
		props.setProperty(S_DBLOADER, "" + dbLoaders);
	}

	private void getParameterHandling()
	{
		String pb = props.getProperty(S_PARAMETERHANDLING, "LIBERAL").toUpperCase();
		if(pb.startsWith("S")) {
			strict_variables = true;
			warn_variables = false;
			parameterHandling = "STRICT";
		} else if(pb.startsWith("W")) {
			strict_variables = false;
			warn_variables = true;
			parameterHandling = "WARN";
		} else {
			strict_variables = false;
			warn_variables = false;
			parameterHandling = "LIBERAL";
		}
		props.setProperty(S_PARAMETERHANDLING, parameterHandling);
	}

	private void getUnresolvedParameterHandling()
	{
		String pb = props.getProperty(S_UNRESPARMHANDLING, "ERROR").toUpperCase();
		if(pb.equals("BLANK")) {
			unresolvedParameterHandling = UPH_BLANK;
		} else if(pb.equals("ECHO")) {
			unresolvedParameterHandling = UPH_ECHO;
		} else {
			unresolvedParameterHandling = UPH_ERROR;
		}
		props.setProperty(S_UNRESPARMHANDLING, pb);
	}

	private void getParameterSyntaxHandling()
	{
		String pb = props.getProperty(S_PARAMETERSYNTAXHANDLING, "ERROR").toUpperCase();
		if(pb.equals("IGNORE")) {
			parameterSyntaxHandling = PSH_IGNORE;
		} else if(pb.equals("ERROR")) {
			unresolvedParameterHandling = PSH_ERROR;
		}
		props.setProperty(S_PARAMETERSYNTAXHANDLING, pb);
	}

	private Vector convertEntryToVector(String s, String msg)
	{
		final StringReader sr = new StringReader(s);
		final StreamTokenizer st = new StreamTokenizer(sr);
		int tok;

		st.slashStarComments(true);
		st.wordChars('_', '_');
		st.wordChars('@', '@');
		st.wordChars('#', '#');
		Vector vars = new Vector();
		try {
			while((tok = st.nextToken()) != StreamTokenizer.TT_EOF) {
				if(tok ==  StreamTokenizer.TT_WORD) {
					vars.add(st.sval.toUpperCase());
					if (msg != null)
						SDMSThread.doTrace(null, msg + st.sval, SDMSThread.SEVERITY_INFO);
				}
			}
		} catch(java.io.IOException ioe) {
		}
		return vars;
	}

	private void getArchiveCols()
	{
		String s_smeColumns = props.getProperty(S_ARCSMECOLS, ALL);
		String s_atColumns = props.getProperty(S_ARCATCOLS, ALL);
		String s_diColumns = props.getProperty(S_ARCDICOLS, ALL);
		String s_evColumns = props.getProperty(S_ARCEVCOLS, ALL);
		String s_hiColumns = props.getProperty(S_ARCHICOLS, ALL);
		String s_kjColumns = props.getProperty(S_ARCKJCOLS, ALL);

		if (!archive) return;

		if (s_smeColumns.toUpperCase().equals(NONE))
			smeColumns = null;
		else if (s_smeColumns.toUpperCase().equals(ALL))
			smeColumns = new Vector();
		else
			smeColumns = convertEntryToVector(s_smeColumns, null);

		if (s_atColumns.toUpperCase().equals(NONE))
			atColumns = null;
		else if (s_atColumns.toUpperCase().equals(ALL))
			atColumns = new Vector();
		else
			atColumns = convertEntryToVector(s_atColumns, null);

		if (s_diColumns.toUpperCase().equals(NONE))
			diColumns = null;
		else if (s_diColumns.toUpperCase().equals(ALL))
			diColumns = new Vector();
		else
			diColumns = convertEntryToVector(s_diColumns, null);

		if (s_evColumns.toUpperCase().equals(NONE))
			evColumns = null;
		else if (s_evColumns.toUpperCase().equals(ALL))
			evColumns = new Vector();
		else
			evColumns = convertEntryToVector(s_evColumns, null);

		if (s_hiColumns.toUpperCase().equals(NONE))
			hiColumns = null;
		else if (s_hiColumns.toUpperCase().equals(ALL))
			hiColumns = new Vector();
		else
			hiColumns = convertEntryToVector(s_hiColumns, null);

		if (s_kjColumns.toUpperCase().equals(NONE))
			kjColumns = null;
		else if (s_kjColumns.toUpperCase().equals(ALL))
			kjColumns = new Vector();
		else
			kjColumns = convertEntryToVector(s_kjColumns, null);

	}

	private void getExportVariables()
	{
		final String defaultVars =
			SDMSSubmittedEntity.S_ERRORLOG		+ "," +
			SDMSSubmittedEntity.S_EXPFINALTIME	+ "," +
			SDMSSubmittedEntity.S_EXPRUNTIME	+ "," +
			SDMSSubmittedEntity.S_FINISHTS		+ "," +
			SDMSSubmittedEntity.S_ISRESTARTABLE	+ "," +
			SDMSSubmittedEntity.S_JOBID		+ "," +
			SDMSSubmittedEntity.S_JOBNAME		+ "," +
			SDMSSubmittedEntity.S_JOBSTATE		+ "," +
			SDMSSubmittedEntity.S_JOBTAG		+ "," +
			SDMSSubmittedEntity.S_KEY		+ "," +
			SDMSSubmittedEntity.S_LOGFILE		+ "," +
			SDMSSubmittedEntity.S_MASTERID		+ "," +
			SDMSSubmittedEntity.S_MERGEDSTATE	+ "," +
			SDMSSubmittedEntity.S_PARENTID		+ "," +
			SDMSSubmittedEntity.S_PID		+ "," +
			SDMSSubmittedEntity.S_RERUNSEQ		+ "," +
			SDMSSubmittedEntity.S_RESOURCETS	+ "," +
			SDMSSubmittedEntity.S_RUNNABLETS	+ "," +
			SDMSSubmittedEntity.S_SCOPENAME		+ "," +
			SDMSSubmittedEntity.S_SDMSHOST		+ "," +
			SDMSSubmittedEntity.S_SDMSPORT		+ "," +
			SDMSSubmittedEntity.S_SEID		+ "," +
			SDMSSubmittedEntity.S_STARTTS		+ "," +
			SDMSSubmittedEntity.S_STATE		+ "," +
			SDMSSubmittedEntity.S_SUBMITTS		+ "," +
			SDMSSubmittedEntity.S_SYNCTS		+ "," +
			SDMSSubmittedEntity.S_SYSDATE		+ "," +
			SDMSSubmittedEntity.S_TRBASE		+ "," +
			SDMSSubmittedEntity.S_TRBASEID		+ "," +
			SDMSSubmittedEntity.S_TRBASEJOBID	+ "," +
			SDMSSubmittedEntity.S_TRNAME		+ "," +
			SDMSSubmittedEntity.S_TRNSTATE		+ "," +
			SDMSSubmittedEntity.S_TRORIGIN		+ "," +
			SDMSSubmittedEntity.S_TRORIGINID	+ "," +
			SDMSSubmittedEntity.S_TRORIGINJOBID	+ "," +
			SDMSSubmittedEntity.S_TROSTATE		+ "," +
			SDMSSubmittedEntity.S_TRREASON		+ "," +
			SDMSSubmittedEntity.S_TRREASONID	+ "," +
			SDMSSubmittedEntity.S_TRREASONJOBID	+ "," +
			SDMSSubmittedEntity.S_TRSEQ		+ "," +
			SDMSSubmittedEntity.S_TRTYPE		+ "," +
			SDMSSubmittedEntity.S_WARNING		+ "," +
			SDMSSubmittedEntity.S_WORKDIR;

		exportVariablesString = props.getProperty(S_EXPORTVARIABLES, defaultVars);
		userExportVariablesString = props.getProperty(S_USEREXPORTVARIABLES, "");
		if (userExportVariablesString.length() != 0)
			exportVariablesString = exportVariablesString + "," + userExportVariablesString;
		exportVariables = convertEntryToVector(exportVariablesString, "Exporting ");
		StringBuffer ergebnis = new StringBuffer();
		for (int i = 0; i < exportVariables.size(); ++i) {
			ergebnis.append(exportVariables.get(i).toString());
			if (i < exportVariables.size() - 1) ergebnis.append(",");
		}
		props.setProperty(S_EXPORTVARIABLES, ergebnis.toString());
	}

	private void getDumpLangLevel()
	{
		String cnfLangLevel = props.getProperty(S_DMPLANGLEVEL, LANGLEVELS[DEFAULT_LANGLEVEL]);
		for (int i = 0; i < LANGLEVELS.length; ++i) {
			if (cnfLangLevel.equals(LANGLEVELS[i]) || cnfLangLevel.equals("v" + LANGLEVELS[i]) || cnfLangLevel.equals("V" + LANGLEVELS[i])) {
				dumpLangLevel = i;
				break;
			}
		}
		props.setProperty(S_DMPLANGLEVEL, LANGLEVELS[dumpLangLevel]);
	}

	private int checkIntProperty(String val, String name, int minval, int def, int maxval, String msg)
	{
		int rc;
		try {
			rc = Integer.parseInt(val.trim());
		} catch(NumberFormatException nfe) {
			rc = def;
			SDMSThread.doTrace(null, "Invalid number format in " + props.getProperty(name), SDMSThread.SEVERITY_WARNING);
		}
		if(rc < minval) {
			SDMSThread.doTrace(null, msg + rc, SDMSThread.SEVERITY_WARNING);
			rc = minval;
		}
		if(maxval > minval && rc > maxval) {
			SDMSThread.doTrace(null, msg + rc, SDMSThread.SEVERITY_WARNING);
			rc = def;
		}
		return rc;
	}

	public static synchronized void incrCntRwTx () { cntRwTx ++; }
	public static synchronized void incrCntDl ()   { cntDl   ++; }
	public static synchronized void incrCntWl ()   { cntWl   ++; }

	public Object clone()
		throws CloneNotSupportedException
	{
		final SystemEnvironment sysEnv = (SystemEnvironment) super.clone();
		sysEnv.systemDateFormat = (SimpleDateFormat) systemDateFormat.clone();
		sysEnv.jsCommDateFormat = (SimpleDateFormat) jsCommDateFormat.clone();
		return sysEnv;
	}

	public void enableConnect()		{ connectState.setValue(connectState.getValue() & 2); }
	public void disableConnect()		{ connectState.setValue(connectState.getValue() | 1); }
	public void setConnectShutdown()	{ connectState.setValue(connectState.getValue() | 2); }
	public int getConnectState()		{ return connectState.getValue(); }
	public static void setProtectMode()	{ protectMode = true; }
	public static void resetProtectMode()	{ protectMode = false; }
	public static boolean getProtectMode()	{ return protectMode; }

	public Long randomLong()		{ return new Long(java.lang.Math.abs(random.nextLong())); }

	public static synchronized int getTraceLevel()	{ return traceLevel; }
	public static synchronized void setTraceLevel(int t) { traceLevel = t; }

	public static void getSharedLock()
	{
		executeLock.getSharedLock();
	}
	public static void releaseSharedLock()
	{
		executeLock.releaseSharedLock();
	}
	public static void getExclusiveLock()
	{
		executeLock.getExclusiveLock();
	}
	public static void releaseExclusiveLock()
	{
		executeLock.releaseExclusiveLock();
	}

	public Long txTime()
	{
		return new Long(tx.startTime);
	}

	public static final GregorianCalendar newGregorianCalendar()
	{
		final GregorianCalendar gc = new GregorianCalendar (systemTimeZone, systemLocale);

		gc.setFirstDayOfWeek (Calendar.MONDAY);
		gc.setMinimalDaysInFirstWeek (4);
		gc.clear();

		return gc;
	}
}

class MutableInteger
{

	int value;

	public MutableInteger(int v) { value = v; }
	public void setValue(int v)  { value = v; }
	public int  getValue()       { return value; }
}

class ExecuteLock
{
	int ctr;
	int xLockRequested;

	public ExecuteLock()
	{
		ctr = 0;
		xLockRequested = 0;
	}

	public synchronized void getSharedLock()
	{
		while(ctr < 0 || xLockRequested != 0) {
			try {
				wait();
			} catch (InterruptedException ie) {  }
		}
		ctr++;
	}
	public synchronized void releaseSharedLock()
	{
		ctr--;
		if(ctr == 0) notify();
	}
	public synchronized void getExclusiveLock()
	{
		xLockRequested++;
		while(ctr != 0) {
			try {
				wait();
			} catch (InterruptedException ie) {  }
		}
		xLockRequested--;
		ctr--;
	}
	public synchronized void releaseExclusiveLock()
	{
		ctr++;
		notify();
	}
	public synchronized int currentLockMode()
	{
		return (ctr < 0 ? SDMSLock.X : SDMSLock.S);
	}
}

class Feature
{
	public String requiredLevel;
	public String featureText;
	public Feature(String level, String text)
	{
		requiredLevel = level;
		featureText = text;
	}
}

