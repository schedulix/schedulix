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

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.parser.expandClasses.*;
import de.independit.scheduler.server.repository.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.output.*;
import de.independit.scheduler.server.util.PathVector;

public class ExpandParser
{
	private static final HashMap validTypes    = new HashMap();
	private static final HashMap validOps      = new HashMap();
	private static final HashMap expandClasses = new HashMap();

	private static final int OPERATOR  = 0;
	private static final int OPERAND   = 1;
	private static final int RESULT    = 2;
	private static final int CLASSNAME = 3;

	public static final int T_ALL             =  99;
	public static final int T_ENVIRONMENT     = 100;
	public static final int T_ESD             = 101;
	public static final int T_ESM             = 102;
	public static final int T_ESP             = 103;
	public static final int T_EST             = 104;
	public static final int T_EVENT           = 105;
	public static final int T_FOLDER          = 106;
	public static final int T_FOOTPRINT       = 107;
	public static final int T_GRANT           = 108;
	public static final int T_GROUP           = 109;
	public static final int T_INTERVAL        = 110;
	public static final int T_JOB_DEFINITION  = 111;
	public static final int T_NAMED_RESOURCE  = 112;
	public static final int T_NICE_PROFILE    = 113;
	public static final int T_OBJECT_MONITOR  = 114;
	public static final int T_POOL            = 115;
	public static final int T_RESOURCE        = 116;
	public static final int T_RESOURCE_TMPL   = 117;
	public static final int T_RSD             = 118;
	public static final int T_RSM             = 119;
	public static final int T_RSP             = 120;
	public static final int T_SCHEDULE        = 121;
	public static final int T_SCHEDULED_EVENT = 122;
	public static final int T_SCOPE           = 123;
	public static final int T_TRIGGER         = 124;
	public static final int T_USER            = 125;
	public static final int T_WATCH_TYPE      = 126;

	public static final int T_COMMENT         = 200;
	public static final int T_DISTRIBUTION    = 201;
	public static final int T_NONE            = 202;

	public static final int O_CHILDREN        = 1000;
	public static final int O_COMMENT         = 1001;
	public static final int O_DEPENDENT       = 1002;
	public static final int O_DISTRIBUTION    = 1003;
	public static final int O_EMBEDDED        = 1004;
	public static final int O_ENVIRONMENT     = 1005;
	public static final int O_ESD             = 1006;
	public static final int O_ESM             = 1007;
	public static final int O_ESP             = 1008;
	public static final int O_EST             = 1009;
	public static final int O_EVENT           = 1010;
	public static final int O_FOLDER          = 1011;
	public static final int O_FOOTPRINT       = 1012;
	public static final int O_GRANT           = 1013;
	public static final int O_GROUP           = 1014;
	public static final int O_INTERVAL        = 1015;
	public static final int O_JOB_DEFINITION  = 1016;
	public static final int O_NAMED_RESOURCE  = 1017;
	public static final int O_OBJECT_MONITOR  = 1018;
	public static final int O_OWNER           = 1019;
	public static final int O_PARENTS         = 1020;
	public static final int O_POOL            = 1021;
	public static final int O_REQUIRED        = 1022;
	public static final int O_RESOURCE        = 1023;
	public static final int O_RESOURCE_TMPL   = 1024;
	public static final int O_RSD             = 1025;
	public static final int O_RSM             = 1026;
	public static final int O_RSP             = 1027;
	public static final int O_SCHEDULE        = 1028;
	public static final int O_SCHEDULED_EVENT = 1029;
	public static final int O_SCOPE           = 1030;
	public static final int O_STOP            = 1031;
	public static final int O_TRIGGER         = 1032;
	public static final int O_USER            = 1033;
	public static final int O_WATCH_TYPE      = 1034;

	static final int O_CONTENT         = 2000;
	static final int O_TIME_SCHEDULES  = 2001;

	final static String ST_ALL		= "ALL";
	final static String ST_COMMENT		= "COMMENT";
	final static String ST_DISTRIBUTION     = "DISTRIBUTION";
	final static String ST_ENVIRONMENT	= "ENVIRONMENT";
	final static String ST_ESD		= "ESD";
	final static String ST_ESM		= "ESM";
	final static String ST_ESP		= "ESP";
	final static String ST_EST		= "EST";
	final static String ST_EVENT		= "EVENT";
	final static String ST_FOLDER		= "FOLDER";
	final static String ST_FOOTPRINT	= "FOOTPRINT";
	final static String ST_GRANT		= "GRANT";
	final static String ST_GROUP		= "GROUP";
	final static String ST_INTERVAL		= "INTERVAL";
	final static String ST_JOB_DEFINITION	= "JOB_DEFINITION";
	final static String ST_NAMED_RESOURCE	= "NAMED_RESOURCE";
	final static String ST_NICE_PROFILE	= "NICE_PROFILE";
	final static String ST_OBJECT_MONITOR	= "OBJECT_MONITOR";
	final static String ST_POOL		= "POOL";
	final static String ST_RESOURCE		= "RESOURCE";
	final static String ST_RESOURCE_TMPL	= "RESOURCE_TEMPLATE";
	final static String ST_RSD		= "RSD";
	final static String ST_RSM		= "RSM";
	final static String ST_RSP		= "RSP";
	final static String ST_SCHEDULE		= "SCHEDULE";
	final static String ST_SCHEDULED_EVENT	= "SCHEDULED_EVENT";
	final static String ST_SCOPE		= "SCOPE";
	final static String ST_TRIGGER		= "TRIGGER";
	final static String ST_USER		= "USER";
	final static String ST_WATCH_TYPE	= "WATCH_TYPE";

	final static String SO_CHILDREN		= "CHILDREN";
	final static String SO_COMMENT		= "COMMENT";
	final static String SO_DEPENDENT	= "DEPENDENT";
	final static String SO_DISTRIBUTION	= "DISTRIBUTION";
	final static String SO_EMBEDDED		= "EMBEDDED";
	final static String SO_ENVIRONMENT	= "ENVIRONMENT";
	final static String SO_ESD		= "ESD";
	final static String SO_ESM		= "ESM";
	final static String SO_ESP		= "ESP";
	final static String SO_EST		= "EST";
	final static String SO_EVENT		= "EVENT";
	final static String SO_FOLDER		= "FOLDER";
	final static String SO_FOOTPRINT	= "FOOTPRINT";
	final static String SO_GRANT		= "GRANT";
	final static String SO_GROUP		= "GROUP";
	final static String SO_INTERVAL		= "INTERVAL";
	final static String SO_JOB_DEFINITION	= "JOB_DEFINITION";
	final static String SO_NAMED_RESOURCE	= "NAMED_RESOURCE";
	final static String SO_OBJECT_MONITOR	= "OBJECT_MONITOR";
	final static String SO_OWNER		= "OWNER";
	final static String SO_PARENTS		= "PARENTS";
	final static String SO_POOL		= "POOL";
	final static String SO_REQUIRED		= "REQUIRED";
	final static String SO_RESOURCE		= "RESOURCE";
	final static String SO_RESOURCE_TMPL	= "RESOURCE_TEMPLATE";
	final static String SO_RSD		= "RSD";
	final static String SO_RSM		= "RSM";
	final static String SO_RSP		= "RSP";
	final static String SO_SCHEDULE		= "SCHEDULE";
	final static String SO_SCHEDULED_EVENT	= "SCHEDULED_EVENT";
	final static String SO_SCOPE		= "SCOPE";
	final static String SO_STOP		= "STOP";
	final static String SO_TRIGGER		= "TRIGGER";
	final static String SO_USER		= "USER";
	final static String SO_WATCH_TYPE	= "WATCH_TYPE";

	final static String SO_CONTENT		= "CONTENT";
	final static String SO_TIME_SCHEDULES	= "TIME_SCHEDULES";

	final static int C_FF_CHILDREN          =  0;
	final static int C_INTINT_CHILDREN      =  1;
	final static int C_SESE_CHILDREN        =  2;
	final static int C_NRNR_CHILDREN        =  3;
	final static int C_SCSC_CHILDREN        =  4;
	final static int C_SS_CHILDREN          =  5;
	final static int C_ALLCMT_COMMENT       =  6;
	final static int C_SESE_DEPENDENT       =  7;
	final static int C_PLD_DISTRIBUTION     =  8;
	final static int C_INTINT_EMBEDDED      =  9;
	final static int C_FENV_ENVIRONMENT     = 10;
	final static int C_SEENV_ENVIRONMENT    = 11;
	final static int C_NRENV_ENVIRONMENT    = 12;
	final static int C_ESMESD_ESD           = 13;
	final static int C_ESPESD_ESD           = 14;
	final static int C_RSMESD_ESD           = 15;
	final static int C_ESPESM_ESM           = 16;
	final static int C_SEESM_ESM            = 17;
	final static int C_SEESP_ESP            = 18;
	final static int C_SEEST_EST            = 19;
	final static int C_SEEVT_EVENT          = 20;
	final static int C_SEVEVT_EVENT         = 21;
	final static int C_SEF_FOLDER           = 22;
	final static int C_RF_FOLDER            = 23;
	final static int C_SEFP_FOOTPRINT       = 24;
	final static int C_ALLGR_GRANT          = 25;
	final static int C_GRG_GROUP            = 26;
	final static int C_SCINT_INTERVAL       = 27;
	final static int C_FSE_JOB_DEFINITION   = 28;
	final static int C_OT_JOB_DEFINITION	= 29;
	final static int C_TRSE_JOB_DEFINITION  = 30;
	final static int C_ENVNR_NAMED_RESOURCE = 31;
	final static int C_FNR_NAMED_RESOURCE   = 32;
	final static int C_FPNR_NAMED_RESOURCE  = 33;
	final static int C_SENR_NAMED_RESOURCE  = 34;
	final static int C_PLNR_NAMED_RESOURCE  = 35;
	final static int C_RNR_NAMED_RESOURCE   = 36;
	final static int C_RTNR_NAMED_RESOURCE  = 37;
	final static int C_WT_OBJECT_MONITOR	= 38;
	final static int C_ALLG_OWNER           = 39;
	final static int C_FF_PARENTS           = 40;
	final static int C_INTINT_PARENTS       = 41;
	final static int C_SESE_PARENTS         = 42;
	final static int C_NRNR_PARENTS         = 43;
	final static int C_SCSC_PARENTS         = 44;
	final static int C_SS_PARENTS           = 45;
	final static int C_NRPL_POOL            = 46;
	final static int C_PLPL_POOL            = 47;
	final static int C_SPL_POOL             = 48;
	final static int C_SESE_REQUIRED        = 49;
	final static int C_FR_RESOURCE          = 50;
	final static int C_NRR_RESOURCE         = 51;
	final static int C_PLR_RESOURCE         = 52;
	final static int C_SR_RESOURCE          = 53;
	final static int C_SERT_RESOURCE_TMPL   = 54;
	final static int C_RRSD_RSD             = 55;
	final static int C_RTRSD_RSD            = 56;
	final static int C_RSMRSD_RSD           = 57;
	final static int C_RSPRSD_RSD           = 58;
	final static int C_SERSM_RSM            = 59;
	final static int C_NRRSP_RSP            = 60;
	final static int C_SEVSC_SCHEDULE       = 61;
	final static int C_EVSEV_SCHEDULED_EVENT= 62;
	final static int C_PLS_SCOPE            = 63;
	final static int C_RS_SCOPE             = 64;
	final static int C_ALLNONE_STOP         = 65;
	final static int C_SETR_TRIGGER         = 66;
	final static int C_NRTR_TRIGGER         = 67;
	final static int C_OT_TRIGGER		= 68;
	final static int C_RTR_TRIGGER          = 69;
	final static int C_GU_USER              = 70;
	final static int C_OT_WATCH_TYPE	= 71;

	static {
		validTypes.put(ST_ALL,             new Integer(T_ALL));
		validTypes.put(ST_DISTRIBUTION,    new Integer(T_DISTRIBUTION));
		validTypes.put(ST_ENVIRONMENT,     new Integer(T_ENVIRONMENT));
		validTypes.put(ST_ESD,             new Integer(T_ESD));
		validTypes.put(ST_ESM,             new Integer(T_ESM));
		validTypes.put(ST_ESP,             new Integer(T_ESP));
		validTypes.put(ST_EST,             new Integer(T_EST));
		validTypes.put(ST_EVENT,           new Integer(T_EVENT));
		validTypes.put(ST_FOLDER,          new Integer(T_FOLDER));
		validTypes.put(ST_FOOTPRINT,       new Integer(T_FOOTPRINT));
		validTypes.put(ST_GRANT,           new Integer(T_GRANT));
		validTypes.put(ST_GROUP,           new Integer(T_GROUP));
		validTypes.put(ST_INTERVAL,        new Integer(T_INTERVAL));
		validTypes.put(ST_JOB_DEFINITION,  new Integer(T_JOB_DEFINITION));
		validTypes.put(ST_NAMED_RESOURCE,  new Integer(T_NAMED_RESOURCE));
		validTypes.put(ST_NICE_PROFILE,    new Integer(T_NICE_PROFILE));
		validTypes.put(ST_OBJECT_MONITOR,  new Integer(T_OBJECT_MONITOR));
		validTypes.put(ST_POOL,            new Integer(T_POOL));
		validTypes.put(ST_RESOURCE,        new Integer(T_RESOURCE));
		validTypes.put(ST_RESOURCE_TMPL,   new Integer(T_RESOURCE_TMPL));
		validTypes.put(ST_RSD,             new Integer(T_RSD));
		validTypes.put(ST_RSM,             new Integer(T_RSM));
		validTypes.put(ST_RSP,             new Integer(T_RSP));
		validTypes.put(ST_SCHEDULE,        new Integer(T_SCHEDULE));
		validTypes.put(ST_SCHEDULED_EVENT, new Integer(T_SCHEDULED_EVENT));
		validTypes.put(ST_SCOPE,           new Integer(T_SCOPE));
		validTypes.put(ST_TRIGGER,         new Integer(T_TRIGGER));
		validTypes.put(ST_USER,            new Integer(T_USER));
		validTypes.put(ST_WATCH_TYPE,      new Integer(T_WATCH_TYPE));

		validOps.put(SO_CHILDREN,          new Integer(O_CHILDREN));
		validOps.put(SO_COMMENT,           new Integer(O_COMMENT));
		validOps.put(SO_DEPENDENT,         new Integer(O_DEPENDENT));
		validOps.put(SO_DISTRIBUTION,      new Integer(O_DISTRIBUTION));
		validOps.put(SO_EMBEDDED,          new Integer(O_EMBEDDED));
		validOps.put(SO_ENVIRONMENT,       new Integer(O_ENVIRONMENT));
		validOps.put(SO_ESD,               new Integer(O_ESD));
		validOps.put(SO_ESM,               new Integer(O_ESM));
		validOps.put(SO_ESP,               new Integer(O_ESP));
		validOps.put(SO_EST,               new Integer(O_EST));
		validOps.put(SO_EVENT,             new Integer(O_EVENT));
		validOps.put(SO_FOLDER,            new Integer(O_FOLDER));
		validOps.put(SO_FOOTPRINT,         new Integer(O_FOOTPRINT));
		validOps.put(SO_GRANT,             new Integer(O_GRANT));
		validOps.put(SO_GROUP,             new Integer(O_GROUP));
		validOps.put(SO_INTERVAL,          new Integer(O_INTERVAL));
		validOps.put(SO_JOB_DEFINITION,    new Integer(O_JOB_DEFINITION));
		validOps.put(SO_NAMED_RESOURCE,    new Integer(O_NAMED_RESOURCE));
		validOps.put(SO_OBJECT_MONITOR,    new Integer(O_OBJECT_MONITOR));
		validOps.put(SO_OWNER,             new Integer(O_OWNER));
		validOps.put(SO_PARENTS,           new Integer(O_PARENTS));
		validOps.put(SO_POOL,              new Integer(O_POOL));
		validOps.put(SO_REQUIRED,          new Integer(O_REQUIRED));
		validOps.put(SO_RESOURCE,          new Integer(O_RESOURCE));
		validOps.put(SO_RESOURCE_TMPL,     new Integer(O_RESOURCE_TMPL));
		validOps.put(SO_RSD,               new Integer(O_RSD));
		validOps.put(SO_RSM,               new Integer(O_RSM));
		validOps.put(SO_RSP,               new Integer(O_RSP));
		validOps.put(SO_SCHEDULE,          new Integer(O_SCHEDULE));
		validOps.put(SO_SCHEDULED_EVENT,   new Integer(O_SCHEDULED_EVENT));
		validOps.put(SO_SCOPE,             new Integer(O_SCOPE));
		validOps.put(SO_STOP,              new Integer(O_STOP));
		validOps.put(SO_TRIGGER,           new Integer(O_TRIGGER));
		validOps.put(SO_USER,              new Integer(O_USER));
		validOps.put(SO_WATCH_TYPE,        new Integer(O_WATCH_TYPE));

		validOps.put(SO_CONTENT,           new Integer(O_CONTENT));
		validOps.put(SO_TIME_SCHEDULES,    new Integer(O_TIME_SCHEDULES));

		expandClasses.put(new Integer(C_FF_CHILDREN),		new FolderChildren());
		expandClasses.put(new Integer(C_INTINT_CHILDREN),	new IntervalChildren());
		expandClasses.put(new Integer(C_SESE_CHILDREN),		new SEChildren());
		expandClasses.put(new Integer(C_NRNR_CHILDREN),		new NRChildren());
		expandClasses.put(new Integer(C_SCSC_CHILDREN),		new SCChildren());
		expandClasses.put(new Integer(C_SS_CHILDREN),		new ScopeChildren());
		expandClasses.put(new Integer(C_ALLCMT_COMMENT),	new AllComment());
		expandClasses.put(new Integer(C_SESE_DEPENDENT),	new SEDependent());
		expandClasses.put(new Integer(C_PLD_DISTRIBUTION),	new PLDistribution());
		expandClasses.put(new Integer(C_INTINT_EMBEDDED),	new IntervalEmbedded());
		expandClasses.put(new Integer(C_FENV_ENVIRONMENT),	new FolderEnvironment());
		expandClasses.put(new Integer(C_SEENV_ENVIRONMENT),	new SEEnvironment());
		expandClasses.put(new Integer(C_NRENV_ENVIRONMENT),	new NREnvironment());
		expandClasses.put(new Integer(C_ESMESD_ESD),		new ESMExitStateDefinition());
		expandClasses.put(new Integer(C_ESPESD_ESD),		new ESPExitStateDefinition());
		expandClasses.put(new Integer(C_RSMESD_ESD),		new RSMExitStateDefinition());
		expandClasses.put(new Integer(C_ESPESM_ESM),		new ESPExitStateMapping());
		expandClasses.put(new Integer(C_SEESM_ESM),		new SEExitStateMapping());
		expandClasses.put(new Integer(C_SEESP_ESP),		new SEExitStateProfile());
		expandClasses.put(new Integer(C_SEEST_EST),		new SEExitStateTranslation());
		expandClasses.put(new Integer(C_SEEVT_EVENT),		new SEEvent());
		expandClasses.put(new Integer(C_SEVEVT_EVENT),		new SEVEvent());
		expandClasses.put(new Integer(C_SEF_FOLDER),		new SEFolder());
		expandClasses.put(new Integer(C_RF_FOLDER),		new RFolder());
		expandClasses.put(new Integer(C_SEFP_FOOTPRINT),	new SEFootprint());
		expandClasses.put(new Integer(C_ALLGR_GRANT),		new AllGrant());
		expandClasses.put(new Integer(C_GRG_GROUP),		new GrantGroup());
		expandClasses.put(new Integer(C_SCINT_INTERVAL),	new SCInterval());
		expandClasses.put(new Integer(C_FSE_JOB_DEFINITION),	new FolderSE());
		expandClasses.put(new Integer(C_TRSE_JOB_DEFINITION),	new TriggerSE());
		expandClasses.put(new Integer(C_ENVNR_NAMED_RESOURCE),	new ENVNamedResource());
		expandClasses.put(new Integer(C_FNR_NAMED_RESOURCE),	new FNamedResource());
		expandClasses.put(new Integer(C_FPNR_NAMED_RESOURCE),	new FPNamedResource());
		expandClasses.put(new Integer(C_SENR_NAMED_RESOURCE),	new SENamedResource());
		expandClasses.put(new Integer(C_PLNR_NAMED_RESOURCE),	new PLNamedResource());
		expandClasses.put(new Integer(C_RNR_NAMED_RESOURCE),	new RNamedResource());
		expandClasses.put(new Integer(C_RTNR_NAMED_RESOURCE),	new RTNamedResource());
		expandClasses.put(new Integer(C_ALLG_OWNER),		new AllOwner());
		expandClasses.put(new Integer(C_FF_PARENTS),		new FolderParents());
		expandClasses.put(new Integer(C_INTINT_PARENTS),	new IntervalParents());
		expandClasses.put(new Integer(C_SESE_PARENTS),		new SEParents());
		expandClasses.put(new Integer(C_NRNR_PARENTS),		new NRParents());
		expandClasses.put(new Integer(C_SCSC_PARENTS),		new SCParents());
		expandClasses.put(new Integer(C_SS_PARENTS),		new ScopeParents());
		expandClasses.put(new Integer(C_NRPL_POOL),		new NRPool());
		expandClasses.put(new Integer(C_PLPL_POOL),		new PLPool());
		expandClasses.put(new Integer(C_SPL_POOL),		new SPool());
		expandClasses.put(new Integer(C_SESE_REQUIRED),		new SERequired());
		expandClasses.put(new Integer(C_FR_RESOURCE),		new FResource());
		expandClasses.put(new Integer(C_NRR_RESOURCE),		new NRResource());
		expandClasses.put(new Integer(C_PLR_RESOURCE),		new PLResource());
		expandClasses.put(new Integer(C_SR_RESOURCE),		new SResource());
		expandClasses.put(new Integer(C_SERT_RESOURCE_TMPL),	new SEResourceTemplate());
		expandClasses.put(new Integer(C_RRSD_RSD),		new RResourceStateDefinition());
		expandClasses.put(new Integer(C_RTRSD_RSD),		new RTResourceStateDefinition());
		expandClasses.put(new Integer(C_RSMRSD_RSD),		new RSMResourceStateDefinition());
		expandClasses.put(new Integer(C_RSPRSD_RSD),		new RSPResourceStateDefinition());
		expandClasses.put(new Integer(C_SERSM_RSM),		new SEResourceStateMapping());
		expandClasses.put(new Integer(C_NRRSP_RSP),		new NRResourceStateProfile());
		expandClasses.put(new Integer(C_SEVSC_SCHEDULE),	new SEVSchedule());
		expandClasses.put(new Integer(C_EVSEV_SCHEDULED_EVENT),	new EVScheduledEvent());
		expandClasses.put(new Integer(C_PLS_SCOPE),		new PLScope());
		expandClasses.put(new Integer(C_RS_SCOPE),		new RScope());
		expandClasses.put(new Integer(C_ALLNONE_STOP),		null);
		expandClasses.put(new Integer(C_SETR_TRIGGER),		new SETrigger());
		expandClasses.put(new Integer(C_NRTR_TRIGGER),		new NRTrigger());
		expandClasses.put(new Integer(C_RTR_TRIGGER),		new RTrigger());
		expandClasses.put(new Integer(C_GU_USER),		new GUser());
		expandClasses.put(new Integer(C_OT_WATCH_TYPE),		new OMWatchType());
		expandClasses.put(new Integer(C_WT_OBJECT_MONITOR),	new WTObjectMonitor());
		expandClasses.put(new Integer(C_OT_JOB_DEFINITION),	new OMWatcher());
		expandClasses.put(new Integer(C_OT_TRIGGER),		new OMTrigger());
	}

	final static int opDesc[][] = {
		{O_CHILDREN,		T_FOLDER,		T_FOLDER},
		{O_CHILDREN,		T_INTERVAL,		T_INTERVAL},
		{O_CHILDREN,		T_JOB_DEFINITION,	T_JOB_DEFINITION},
		{O_CHILDREN,		T_NAMED_RESOURCE,	T_NAMED_RESOURCE},
		{O_CHILDREN,		T_SCHEDULE,		T_SCHEDULE},
		{O_CHILDREN,		T_SCOPE,		T_SCOPE},
		{O_COMMENT,		T_ALL,			T_COMMENT},
		{O_DEPENDENT,		T_JOB_DEFINITION,	T_JOB_DEFINITION},
		{O_DISTRIBUTION,	T_POOL,			T_DISTRIBUTION},
		{O_EMBEDDED,		T_INTERVAL,		T_INTERVAL},
		{O_ENVIRONMENT,		T_FOLDER,		T_ENVIRONMENT},
		{O_ENVIRONMENT,		T_JOB_DEFINITION,	T_ENVIRONMENT},
		{O_ENVIRONMENT,		T_NAMED_RESOURCE,	T_ENVIRONMENT},
		{O_ESD,			T_ESM,			T_ESD},
		{O_ESD,			T_ESP,			T_ESD},
		{O_ESD,			T_RSM,			T_ESD},
		{O_ESM,			T_ESP,			T_ESM},
		{O_ESM,			T_JOB_DEFINITION,	T_ESM},
		{O_ESP,			T_JOB_DEFINITION,	T_ESP},
		{O_EST,			T_JOB_DEFINITION,	T_EST},
		{O_EVENT,		T_JOB_DEFINITION,	T_EVENT},
		{O_EVENT,		T_SCHEDULED_EVENT,	T_EVENT},
		{O_FOLDER,		T_JOB_DEFINITION,	T_FOLDER},
		{O_FOLDER,		T_RESOURCE,		T_FOLDER},
		{O_FOOTPRINT,		T_JOB_DEFINITION,	T_FOOTPRINT},
		{O_GRANT,		T_ALL,			T_GRANT},
		{O_GROUP,		T_GRANT,		T_GROUP},
		{O_INTERVAL,		T_SCHEDULE,		T_INTERVAL},
		{O_JOB_DEFINITION,	T_FOLDER,		T_JOB_DEFINITION},
		{O_JOB_DEFINITION,	T_OBJECT_MONITOR,	T_JOB_DEFINITION},
		{O_JOB_DEFINITION,	T_TRIGGER,		T_JOB_DEFINITION},
		{O_NAMED_RESOURCE,	T_ENVIRONMENT,		T_NAMED_RESOURCE},
		{O_NAMED_RESOURCE,	T_FOLDER,		T_NAMED_RESOURCE},
		{O_NAMED_RESOURCE,	T_FOOTPRINT,		T_NAMED_RESOURCE},
		{O_NAMED_RESOURCE,	T_JOB_DEFINITION,	T_NAMED_RESOURCE},
		{O_NAMED_RESOURCE,	T_POOL,			T_NAMED_RESOURCE},
		{O_NAMED_RESOURCE,	T_RESOURCE,		T_NAMED_RESOURCE},
		{O_NAMED_RESOURCE,	T_RESOURCE_TMPL,	T_NAMED_RESOURCE},
		{O_OBJECT_MONITOR,	T_WATCH_TYPE,		T_OBJECT_MONITOR},
		{O_OWNER,		T_ALL,			T_GROUP},
		{O_PARENTS,		T_FOLDER,		T_FOLDER},
		{O_PARENTS,		T_INTERVAL,		T_INTERVAL},
		{O_PARENTS,		T_JOB_DEFINITION,	T_JOB_DEFINITION},
		{O_PARENTS,		T_NAMED_RESOURCE,	T_NAMED_RESOURCE},
		{O_PARENTS,		T_SCHEDULE,		T_SCHEDULE},
		{O_PARENTS,		T_SCOPE,		T_SCOPE},
		{O_POOL,		T_NAMED_RESOURCE,	T_POOL},
		{O_POOL,		T_POOL,			T_POOL},
		{O_POOL,		T_SCOPE,		T_POOL},
		{O_REQUIRED,		T_JOB_DEFINITION,	T_JOB_DEFINITION},
		{O_RESOURCE,		T_FOLDER,		T_RESOURCE},
		{O_RESOURCE,		T_NAMED_RESOURCE,	T_RESOURCE},
		{O_RESOURCE,		T_POOL,			T_RESOURCE},
		{O_RESOURCE,		T_SCOPE,		T_RESOURCE},
		{O_RESOURCE_TMPL,	T_JOB_DEFINITION,	T_RESOURCE_TMPL},
		{O_RSD,			T_RESOURCE,		T_RSD},
		{O_RSD,			T_RESOURCE_TMPL,	T_RSD},
		{O_RSD,			T_RSM,			T_RSD},
		{O_RSD,			T_RSP,			T_RSD},
		{O_RSM,			T_JOB_DEFINITION,	T_RSM},
		{O_RSP,			T_NAMED_RESOURCE,	T_RSP},
		{O_SCHEDULE,		T_SCHEDULED_EVENT,	T_SCHEDULE},
		{O_SCHEDULED_EVENT,	T_EVENT,		T_SCHEDULED_EVENT},
		{O_SCOPE,		T_POOL,			T_SCOPE},
		{O_SCOPE,		T_RESOURCE,		T_SCOPE},
		{O_STOP,		T_ALL,			T_NONE},
		{O_TRIGGER,		T_JOB_DEFINITION,	T_TRIGGER},
		{O_TRIGGER,		T_NAMED_RESOURCE,	T_TRIGGER},
		{O_TRIGGER,		T_OBJECT_MONITOR,	T_TRIGGER},
		{O_TRIGGER,		T_RESOURCE,		T_TRIGGER},
		{O_USER,		T_GROUP,		T_USER},
		{O_WATCH_TYPE,		T_OBJECT_MONITOR,	T_WATCH_TYPE},

		{O_CONTENT,		T_FOLDER,		T_ALL},
		{O_CONTENT,		T_SCOPE,		T_ALL},
		{O_TIME_SCHEDULES,	T_JOB_DEFINITION,	T_ALL},

		{Integer.MAX_VALUE,	Integer.MAX_VALUE,	T_NONE}
	};

	static final String CA_FC  = "%fc%";
	static final String CA_SC  = "%sc%";
	static final String CA_TS  = "%ts%";
	static final String CA_TSX = "%tsx%";

	static final DumpExpandItem[] compoundRules = {

		new DumpExpandItem(ST_FOLDER, null,		new PathVector().addThis(new DumpRule(SO_CHILDREN,       CA_FC))),
		new DumpExpandItem(ST_FOLDER, null,		new PathVector().addThis(new DumpRule(SO_JOB_DEFINITION, CA_FC))),
		new DumpExpandItem(ST_FOLDER, null,		new PathVector().addThis(new DumpRule(SO_COMMENT,        CA_FC))),
		new DumpExpandItem(ST_FOLDER, null,		new PathVector().addThis(new DumpRule(SO_RESOURCE,       CA_FC))),
		new DumpExpandItem(ST_FOLDER, CA_FC,		new PathVector().addThis(new DumpRule(SO_CHILDREN,       CA_FC))),
		new DumpExpandItem(ST_FOLDER, CA_FC,		new PathVector().addThis(new DumpRule(SO_JOB_DEFINITION, CA_FC))),
		new DumpExpandItem(ST_ALL, CA_FC,		new PathVector().addThis(new DumpRule(SO_COMMENT,        CA_FC))),
		new DumpExpandItem(ST_FOLDER, CA_FC, 		new PathVector().addThis(new DumpRule(SO_RESOURCE,       CA_FC))),
		new DumpExpandItem(ST_JOB_DEFINITION, CA_FC,	new PathVector().addThis(new DumpRule(SO_TRIGGER,        CA_FC))),
		new DumpExpandItem(ST_JOB_DEFINITION, CA_FC,	new PathVector().addThis(new DumpRule(SO_RESOURCE_TMPL,  CA_FC))),
		null,

		new DumpExpandItem(ST_SCOPE, null,		new PathVector().addThis(new DumpRule(SO_CHILDREN,       CA_SC))),
		new DumpExpandItem(ST_SCOPE, null,		new PathVector().addThis(new DumpRule(SO_RESOURCE,       CA_SC))),
		new DumpExpandItem(ST_SCOPE, null,		new PathVector().addThis(new DumpRule(SO_COMMENT,        CA_SC))),
		new DumpExpandItem(ST_SCOPE, null,		new PathVector().addThis(new DumpRule(SO_POOL,           CA_SC))),
		new DumpExpandItem(ST_SCOPE, CA_SC,		new PathVector().addThis(new DumpRule(SO_CHILDREN,       CA_SC))),
		new DumpExpandItem(ST_SCOPE, CA_SC,		new PathVector().addThis(new DumpRule(SO_RESOURCE,       CA_SC))),
		new DumpExpandItem(ST_SCOPE, CA_SC, 		new PathVector().addThis(new DumpRule(SO_POOL,           CA_SC))),
		new DumpExpandItem(ST_POOL, CA_SC,		new PathVector().addThis(new DumpRule(SO_DISTRIBUTION,   CA_SC))),
		new DumpExpandItem(ST_ALL, CA_SC,		new PathVector().addThis(new DumpRule(SO_COMMENT,        CA_SC))),
		null,

		new DumpExpandItem(ST_JOB_DEFINITION,  null,	new PathVector().addThis(new DumpRule(SO_EVENT,           CA_TS))),
		new DumpExpandItem(ST_EVENT,           CA_TS,	new PathVector().addThis(new DumpRule(SO_SCHEDULED_EVENT, CA_TS))),
		new DumpExpandItem(ST_ALL,             CA_TS,	new PathVector().addThis(new DumpRule(SO_COMMENT,         CA_TS))),
		new DumpExpandItem(ST_SCHEDULED_EVENT, CA_TS,	new PathVector().addThis(new DumpRule(SO_SCHEDULE,        CA_TS))),
		new DumpExpandItem(ST_SCHEDULE,        CA_TS,	new PathVector().addThis(new DumpRule(SO_CHILDREN,        CA_TS))),
		new DumpExpandItem(ST_SCHEDULE,        CA_TS,	new PathVector().addThis(new DumpRule(SO_INTERVAL,        CA_TS))),
		new DumpExpandItem(ST_SCHEDULE,        CA_TS,	new PathVector().addThis(new DumpRule(SO_PARENTS,         CA_TSX))),
		new DumpExpandItem(ST_SCHEDULE,        CA_TSX,	new PathVector().addThis(new DumpRule(SO_PARENTS,         CA_TSX))),
		new DumpExpandItem(ST_SCHEDULE,        CA_TSX,	new PathVector().addThis(new DumpRule(SO_INTERVAL,        CA_TS))),
		new DumpExpandItem(ST_INTERVAL,        CA_TS,	new PathVector().addThis(new DumpRule(SO_CHILDREN,        CA_TS))),
		new DumpExpandItem(ST_INTERVAL,        CA_TS,	new PathVector().addThis(new DumpRule(SO_EMBEDDED,        CA_TS))),
		new DumpExpandItem(ST_INTERVAL,        CA_TS,	new PathVector().addThis(new DumpRule(SO_PARENTS,         CA_TSX))),
		new DumpExpandItem(ST_INTERVAL,        CA_TSX,	new PathVector().addThis(new DumpRule(SO_PARENTS,         CA_TSX))),
		new DumpExpandItem(ST_ALL,             CA_TSX,	new PathVector().addThis(new DumpRule(SO_COMMENT,         CA_TS))),
		null
	};

	static final int compoundIndex[][] = {
		{ O_CONTENT,		T_FOLDER,		0 },
		{ O_CONTENT,		T_SCOPE,		11 },
		{ O_TIME_SCHEDULES,	T_JOB_DEFINITION,	21 }
	};

	static String checkExpandRules(Vector rules)
	{
		if(rules == null) return null;
		final Iterator i = rules.iterator();
		while (i.hasNext()) {
			final DumpExpandItem dei = (DumpExpandItem) i.next();
			final String name = dei.name;
			final Integer type = (Integer) validTypes.get(name);
			if (type == null) return "Invalid dumptype : " + name;
			else {
				final String s = checkExpandOperators(type, dei.ruleList);
				if (s != null) return "Syntax error in rule " + s + " for type " + name;
			}
		}
		return null;
	}

	static String checkExpandOperators(Integer type, PathVector rules)
	{
		String empty = "";
		String dot = ".";
		for (int i = 0; i < rules.size(); ++i) {
			DumpRule rule = (DumpRule) rules.get(i);
			Integer numOp = (Integer) validOps.get(rule.name);
			if (numOp == null)
				return rule.toString() + " (invalid operator)";

			int resultType = type.intValue();
			resultType = findValue(numOp.intValue(), resultType, opDesc);
			if (resultType == -1)
				return rule.toString() + " (operator doesn't accept input)";
		}
		return null;
	}

	static int findValue(int operator, int operand, int[][] searchTable)
	{

		int lb = 0;
		int ub = searchTable.length - 1;
		int idx;

		if (operand == T_NONE) return 0;
		while (lb <= ub) {
			idx = (lb + ub) / 2;
			if (searchTable[idx][OPERATOR] > operator)
				ub = idx - 1;
			else if (searchTable[idx][OPERATOR] < operator)
				lb = idx + 1;
			else {
				if (searchTable[idx][OPERAND] == T_ALL)
					return idx;
				if (searchTable[idx][OPERAND] > operand)
					ub = idx - 1;
				else if (searchTable[idx][OPERAND] < operand)
					lb = idx + 1;
				else
					return idx;
			}
		}

		return -1;
	}

	static Vector expandCompoundRules(Vector rules)
	{
		PathVector rulesToAdd = new PathVector("\n\t");
		for (int i = 0; i < rules.size(); ++i) {
			DumpExpandItem dei = (DumpExpandItem) rules.get(i);
			String deiAlias = dei.alias;
			Integer type = (Integer) validTypes.get(dei.name);
			if (type == null) return null;
			for (Iterator j = dei.ruleList.iterator(); j.hasNext();) {
				DumpRule rule = (DumpRule) j.next();
				String alias = rule.alias;
				Integer numOp = (Integer) validOps.get(rule.name);
				if (numOp == null) return null;
				int idx = findValue(numOp.intValue(), type.intValue(), compoundIndex);
				if (idx != -1) {
					idx = compoundIndex[idx][RESULT];

					for (int k = idx; compoundRules[k] != null; ++k) {
						DumpExpandItem crk = new DumpExpandItem(compoundRules[k]);
						if (crk.alias == null) crk.alias = deiAlias;

						rulesToAdd.addElement(crk);
						if (alias != null) {
							DumpExpandItem tmp = new DumpExpandItem(crk);
							DumpRule dr = new DumpRule((DumpRule) tmp.ruleList.get(0));
							dr.alias = alias;
							tmp.ruleList.clear();
							tmp.ruleList.addElement(dr);
							rulesToAdd.addElement(tmp);

						}
					}
					j.remove();
				}
			}
		}

		rules.addAll(rulesToAdd);
		return rules;
	}

	static Vector mergeRules(Vector rules)
	{
		DumpExpandItem[] ruleArray = new DumpExpandItem[rules.size()];
		PathVector result = new PathVector();

		rules.toArray(ruleArray);
		Arrays.sort(ruleArray);

		int i = 0;
		while(i < ruleArray.length) {
			DumpExpandItem next = ruleArray[i];
			DumpExpandItem newDei = new DumpExpandItem(next.name, next.alias, new PathVector());
			newDei.ruleList.setSep(", ");
			PathVector resultRule = new PathVector();
			do {

				resultRule.addAll(next.ruleList);
				i++;
				if (i == ruleArray.length) break;
				next = ruleArray[i];
			} while (newDei.compareTo(next) == 0);

			DumpRule[] tmp = new DumpRule[resultRule.size()];
			resultRule.toArray(tmp);
			Arrays.sort(tmp);
			DumpRule old = null;
			for (int j = 0; j < tmp.length; j++) {
				DumpRule dr = tmp[j];
				if (old == null || dr.compareTo(old) != 0)
					newDei.ruleList.add(dr);
				old = dr;
			}
			result.add(newDei);

		}
		result.setSep(" \n\t");
		compile(result);
		return result;
	}

	private static void compile(Vector rules)
	{
		for (int i = 0; i < rules.size(); ++i) {
			DumpExpandItem dei = (DumpExpandItem) rules.get(i);

			final String name = dei.name;
			final Integer type = (Integer) validTypes.get(name);
			dei.type = type;
			for (int j = 0; j < dei.ruleList.size(); j++) {
				DumpRule dr = (DumpRule) dei.ruleList.get(j);
				Integer numOp = (Integer) validOps.get(dr.name);
				dr.numOp = numOp;
				int idx = findValue(numOp.intValue(), type.intValue(), opDesc);
				dr.operator = (Expander) expandClasses.get(new Integer(idx));
			}
		}
	}
}

