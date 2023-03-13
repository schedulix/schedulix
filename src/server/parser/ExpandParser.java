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
	public static final int O_DISPATCH        = 1003;
	public static final int O_DISTRIBUTION    = 1004;
	public static final int O_EMBEDDED        = 1005;
	public static final int O_ENVIRONMENT     = 1006;
	public static final int O_ESD             = 1007;
	public static final int O_ESM             = 1008;
	public static final int O_ESP             = 1009;
	public static final int O_EST             = 1010;
	public static final int O_EVENT           = 1011;
	public static final int O_FOLDER          = 1012;
	public static final int O_FOOTPRINT       = 1013;
	public static final int O_GRANT           = 1014;
	public static final int O_GROUP           = 1015;
	public static final int O_INTERVAL        = 1016;
	public static final int O_JOB_DEFINITION  = 1017;
	public static final int O_NAMED_RESOURCE  = 1018;
	public static final int O_OBJECT_MONITOR  = 1019;
	public static final int O_OWNER           = 1020;
	public static final int O_PARENTS         = 1021;
	public static final int O_POOL            = 1022;
	public static final int O_REQUIRED        = 1023;
	public static final int O_RESOURCE        = 1024;
	public static final int O_RESOURCE_TMPL   = 1025;
	public static final int O_RSD             = 1026;
	public static final int O_RSM             = 1027;
	public static final int O_RSP             = 1028;
	public static final int O_SCHEDULE        = 1029;
	public static final int O_SCHEDULED_EVENT = 1030;
	public static final int O_SCOPE           = 1031;
	public static final int O_STOP            = 1032;
	public static final int O_TRIGGER         = 1033;
	public static final int O_USER            = 1034;
	public static final int O_WATCH_TYPE      = 1035;

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
	final static String SO_DISPATCH		= "DISPATCH";
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
	final static int C_INT_DISPATCH		=  8;
	final static int C_PLD_DISTRIBUTION     =  9;
	final static int C_INTINT_EMBEDDED      = 10;
	final static int C_FENV_ENVIRONMENT     = 11;
	final static int C_SEENV_ENVIRONMENT    = 12;
	final static int C_NRENV_ENVIRONMENT    = 13;
	final static int C_ESMESD_ESD           = 14;
	final static int C_ESPESD_ESD           = 15;
	final static int C_RSMESD_ESD           = 16;
	final static int C_ESPESM_ESM           = 17;
	final static int C_SEESM_ESM            = 18;
	final static int C_SEESP_ESP            = 19;
	final static int C_SEEST_EST            = 20;
	final static int C_SEEVT_EVENT          = 21;
	final static int C_SEVEVT_EVENT         = 22;
	final static int C_SEF_FOLDER           = 23;
	final static int C_RF_FOLDER            = 24;
	final static int C_SEFP_FOOTPRINT       = 25;
	final static int C_ALLGR_GRANT          = 26;
	final static int C_GRG_GROUP            = 27;
	final static int C_SEINT_INT		= 28;
	final static int C_SCINT_INTERVAL       = 29;
	final static int C_FSE_JOB_DEFINITION   = 30;
	final static int C_OT_JOB_DEFINITION	= 31;
	final static int C_TRSE_JOB_DEFINITION  = 32;
	final static int C_ENVNR_NAMED_RESOURCE = 33;
	final static int C_FNR_NAMED_RESOURCE   = 34;
	final static int C_FPNR_NAMED_RESOURCE  = 35;
	final static int C_SENR_NAMED_RESOURCE  = 36;
	final static int C_PLNR_NAMED_RESOURCE  = 37;
	final static int C_RNR_NAMED_RESOURCE   = 38;
	final static int C_RTNR_NAMED_RESOURCE  = 39;
	final static int C_WT_OBJECT_MONITOR	= 40;
	final static int C_ALLG_OWNER           = 41;
	final static int C_FF_PARENTS           = 42;
	final static int C_INTINT_PARENTS       = 43;
	final static int C_SESE_PARENTS         = 44;
	final static int C_NRNR_PARENTS         = 45;
	final static int C_SCSC_PARENTS         = 46;
	final static int C_SS_PARENTS           = 47;
	final static int C_NRPL_POOL            = 48;
	final static int C_PLPL_POOL            = 49;
	final static int C_SPL_POOL             = 50;
	final static int C_SESE_REQUIRED        = 51;
	final static int C_FR_RESOURCE          = 52;
	final static int C_NRR_RESOURCE         = 53;
	final static int C_PLR_RESOURCE         = 54;
	final static int C_SR_RESOURCE          = 55;
	final static int C_SERT_RESOURCE_TMPL   = 56;
	final static int C_RRSD_RSD             = 57;
	final static int C_RTRSD_RSD            = 58;
	final static int C_RSMRSD_RSD           = 59;
	final static int C_RSPRSD_RSD           = 60;
	final static int C_SERSM_RSM            = 61;
	final static int C_NRRSP_RSP            = 62;
	final static int C_SEVSC_SCHEDULE       = 63;
	final static int C_EVSEV_SCHEDULED_EVENT= 64;
	final static int C_PLS_SCOPE            = 65;
	final static int C_RS_SCOPE             = 66;
	final static int C_ALLNONE_STOP         = 67;
	final static int C_SETR_TRIGGER         = 68;
	final static int C_NRTR_TRIGGER         = 69;
	final static int C_OT_TRIGGER		= 70;
	final static int C_RTR_TRIGGER          = 71;
	final static int C_GU_USER              = 72;
	final static int C_OT_WATCH_TYPE	= 73;

	static
	{
		validTypes.put(ST_ALL,             Integer.valueOf(T_ALL));
		validTypes.put(ST_DISTRIBUTION,    Integer.valueOf(T_DISTRIBUTION));
		validTypes.put(ST_ENVIRONMENT,     Integer.valueOf(T_ENVIRONMENT));
		validTypes.put(ST_ESD,             Integer.valueOf(T_ESD));
		validTypes.put(ST_ESM,             Integer.valueOf(T_ESM));
		validTypes.put(ST_ESP,             Integer.valueOf(T_ESP));
		validTypes.put(ST_EST,             Integer.valueOf(T_EST));
		validTypes.put(ST_EVENT,           Integer.valueOf(T_EVENT));
		validTypes.put(ST_FOLDER,          Integer.valueOf(T_FOLDER));
		validTypes.put(ST_FOOTPRINT,       Integer.valueOf(T_FOOTPRINT));
		validTypes.put(ST_GRANT,           Integer.valueOf(T_GRANT));
		validTypes.put(ST_GROUP,           Integer.valueOf(T_GROUP));
		validTypes.put(ST_INTERVAL,        Integer.valueOf(T_INTERVAL));
		validTypes.put(ST_JOB_DEFINITION,  Integer.valueOf(T_JOB_DEFINITION));
		validTypes.put(ST_NAMED_RESOURCE,  Integer.valueOf(T_NAMED_RESOURCE));
		validTypes.put(ST_NICE_PROFILE,    Integer.valueOf(T_NICE_PROFILE));
		validTypes.put(ST_OBJECT_MONITOR,  Integer.valueOf(T_OBJECT_MONITOR));
		validTypes.put(ST_POOL,            Integer.valueOf(T_POOL));
		validTypes.put(ST_RESOURCE,        Integer.valueOf(T_RESOURCE));
		validTypes.put(ST_RESOURCE_TMPL,   Integer.valueOf(T_RESOURCE_TMPL));
		validTypes.put(ST_RSD,             Integer.valueOf(T_RSD));
		validTypes.put(ST_RSM,             Integer.valueOf(T_RSM));
		validTypes.put(ST_RSP,             Integer.valueOf(T_RSP));
		validTypes.put(ST_SCHEDULE,        Integer.valueOf(T_SCHEDULE));
		validTypes.put(ST_SCHEDULED_EVENT, Integer.valueOf(T_SCHEDULED_EVENT));
		validTypes.put(ST_SCOPE,           Integer.valueOf(T_SCOPE));
		validTypes.put(ST_TRIGGER,         Integer.valueOf(T_TRIGGER));
		validTypes.put(ST_USER,            Integer.valueOf(T_USER));
		validTypes.put(ST_WATCH_TYPE,      Integer.valueOf(T_WATCH_TYPE));

		validOps.put(SO_CHILDREN,          Integer.valueOf(O_CHILDREN));
		validOps.put(SO_COMMENT,           Integer.valueOf(O_COMMENT));
		validOps.put(SO_DEPENDENT,         Integer.valueOf(O_DEPENDENT));
		validOps.put(SO_DISPATCH,          Integer.valueOf(O_DISPATCH));
		validOps.put(SO_DISTRIBUTION,      Integer.valueOf(O_DISTRIBUTION));
		validOps.put(SO_EMBEDDED,          Integer.valueOf(O_EMBEDDED));
		validOps.put(SO_ENVIRONMENT,       Integer.valueOf(O_ENVIRONMENT));
		validOps.put(SO_ESD,               Integer.valueOf(O_ESD));
		validOps.put(SO_ESM,               Integer.valueOf(O_ESM));
		validOps.put(SO_ESP,               Integer.valueOf(O_ESP));
		validOps.put(SO_EST,               Integer.valueOf(O_EST));
		validOps.put(SO_EVENT,             Integer.valueOf(O_EVENT));
		validOps.put(SO_FOLDER,            Integer.valueOf(O_FOLDER));
		validOps.put(SO_FOOTPRINT,         Integer.valueOf(O_FOOTPRINT));
		validOps.put(SO_GRANT,             Integer.valueOf(O_GRANT));
		validOps.put(SO_GROUP,             Integer.valueOf(O_GROUP));
		validOps.put(SO_INTERVAL,          Integer.valueOf(O_INTERVAL));
		validOps.put(SO_JOB_DEFINITION,    Integer.valueOf(O_JOB_DEFINITION));
		validOps.put(SO_NAMED_RESOURCE,    Integer.valueOf(O_NAMED_RESOURCE));
		validOps.put(SO_OBJECT_MONITOR,    Integer.valueOf(O_OBJECT_MONITOR));
		validOps.put(SO_OWNER,             Integer.valueOf(O_OWNER));
		validOps.put(SO_PARENTS,           Integer.valueOf(O_PARENTS));
		validOps.put(SO_POOL,              Integer.valueOf(O_POOL));
		validOps.put(SO_REQUIRED,          Integer.valueOf(O_REQUIRED));
		validOps.put(SO_RESOURCE,          Integer.valueOf(O_RESOURCE));
		validOps.put(SO_RESOURCE_TMPL,     Integer.valueOf(O_RESOURCE_TMPL));
		validOps.put(SO_RSD,               Integer.valueOf(O_RSD));
		validOps.put(SO_RSM,               Integer.valueOf(O_RSM));
		validOps.put(SO_RSP,               Integer.valueOf(O_RSP));
		validOps.put(SO_SCHEDULE,          Integer.valueOf(O_SCHEDULE));
		validOps.put(SO_SCHEDULED_EVENT,   Integer.valueOf(O_SCHEDULED_EVENT));
		validOps.put(SO_SCOPE,             Integer.valueOf(O_SCOPE));
		validOps.put(SO_STOP,              Integer.valueOf(O_STOP));
		validOps.put(SO_TRIGGER,           Integer.valueOf(O_TRIGGER));
		validOps.put(SO_USER,              Integer.valueOf(O_USER));
		validOps.put(SO_WATCH_TYPE,        Integer.valueOf(O_WATCH_TYPE));

		validOps.put(SO_CONTENT,           Integer.valueOf(O_CONTENT));
		validOps.put(SO_TIME_SCHEDULES,    Integer.valueOf(O_TIME_SCHEDULES));

		expandClasses.put(Integer.valueOf(C_FF_CHILDREN),		new FolderChildren());
		expandClasses.put(Integer.valueOf(C_INTINT_CHILDREN),		new IntervalChildren());
		expandClasses.put(Integer.valueOf(C_SESE_CHILDREN),		new SEChildren());
		expandClasses.put(Integer.valueOf(C_NRNR_CHILDREN),		new NRChildren());
		expandClasses.put(Integer.valueOf(C_SCSC_CHILDREN),		new SCChildren());
		expandClasses.put(Integer.valueOf(C_SS_CHILDREN),		new ScopeChildren());
		expandClasses.put(Integer.valueOf(C_ALLCMT_COMMENT),		new AllComment());
		expandClasses.put(Integer.valueOf(C_SESE_DEPENDENT),		new SEDependent());
		expandClasses.put(Integer.valueOf(C_PLD_DISTRIBUTION),		new PLDistribution());
		expandClasses.put(Integer.valueOf(C_INT_DISPATCH),		new IntervalDispatch());
		expandClasses.put(Integer.valueOf(C_INTINT_EMBEDDED),		new IntervalEmbedded());
		expandClasses.put(Integer.valueOf(C_FENV_ENVIRONMENT),		new FolderEnvironment());
		expandClasses.put(Integer.valueOf(C_SEENV_ENVIRONMENT),		new SEEnvironment());
		expandClasses.put(Integer.valueOf(C_NRENV_ENVIRONMENT),		new NREnvironment());
		expandClasses.put(Integer.valueOf(C_ESMESD_ESD),		new ESMExitStateDefinition());
		expandClasses.put(Integer.valueOf(C_ESPESD_ESD),		new ESPExitStateDefinition());
		expandClasses.put(Integer.valueOf(C_RSMESD_ESD),		new RSMExitStateDefinition());
		expandClasses.put(Integer.valueOf(C_ESPESM_ESM),		new ESPExitStateMapping());
		expandClasses.put(Integer.valueOf(C_SEESM_ESM),			new SEExitStateMapping());
		expandClasses.put(Integer.valueOf(C_SEESP_ESP),			new SEExitStateProfile());
		expandClasses.put(Integer.valueOf(C_SEEST_EST),			new SEExitStateTranslation());
		expandClasses.put(Integer.valueOf(C_SEEVT_EVENT),		new SEEvent());
		expandClasses.put(Integer.valueOf(C_SEVEVT_EVENT),		new SEVEvent());
		expandClasses.put(Integer.valueOf(C_SEF_FOLDER),		new SEFolder());
		expandClasses.put(Integer.valueOf(C_SEINT_INT),			new SEEnableInt());
		expandClasses.put(Integer.valueOf(C_RF_FOLDER),			new RFolder());
		expandClasses.put(Integer.valueOf(C_SEFP_FOOTPRINT),		new SEFootprint());
		expandClasses.put(Integer.valueOf(C_ALLGR_GRANT),		new AllGrant());
		expandClasses.put(Integer.valueOf(C_GRG_GROUP),			new GrantGroup());
		expandClasses.put(Integer.valueOf(C_SCINT_INTERVAL),		new SCInterval());
		expandClasses.put(Integer.valueOf(C_FSE_JOB_DEFINITION),	new FolderSE());
		expandClasses.put(Integer.valueOf(C_TRSE_JOB_DEFINITION),	new TriggerSE());
		expandClasses.put(Integer.valueOf(C_ENVNR_NAMED_RESOURCE),	new ENVNamedResource());
		expandClasses.put(Integer.valueOf(C_FNR_NAMED_RESOURCE),	new FNamedResource());
		expandClasses.put(Integer.valueOf(C_FPNR_NAMED_RESOURCE),	new FPNamedResource());
		expandClasses.put(Integer.valueOf(C_SENR_NAMED_RESOURCE),	new SENamedResource());
		expandClasses.put(Integer.valueOf(C_PLNR_NAMED_RESOURCE),	new PLNamedResource());
		expandClasses.put(Integer.valueOf(C_RNR_NAMED_RESOURCE),	new RNamedResource());
		expandClasses.put(Integer.valueOf(C_RTNR_NAMED_RESOURCE),	new RTNamedResource());
		expandClasses.put(Integer.valueOf(C_ALLG_OWNER),		new AllOwner());
		expandClasses.put(Integer.valueOf(C_FF_PARENTS),		new FolderParents());
		expandClasses.put(Integer.valueOf(C_INTINT_PARENTS),		new IntervalParents());
		expandClasses.put(Integer.valueOf(C_SESE_PARENTS),		new SEParents());
		expandClasses.put(Integer.valueOf(C_NRNR_PARENTS),		new NRParents());
		expandClasses.put(Integer.valueOf(C_SCSC_PARENTS),		new SCParents());
		expandClasses.put(Integer.valueOf(C_SS_PARENTS),		new ScopeParents());
		expandClasses.put(Integer.valueOf(C_NRPL_POOL),			new NRPool());
		expandClasses.put(Integer.valueOf(C_PLPL_POOL),			new PLPool());
		expandClasses.put(Integer.valueOf(C_SPL_POOL),			new SPool());
		expandClasses.put(Integer.valueOf(C_SESE_REQUIRED),		new SERequired());
		expandClasses.put(Integer.valueOf(C_FR_RESOURCE),		new FResource());
		expandClasses.put(Integer.valueOf(C_NRR_RESOURCE),		new NRResource());
		expandClasses.put(Integer.valueOf(C_PLR_RESOURCE),		new PLResource());
		expandClasses.put(Integer.valueOf(C_SR_RESOURCE),		new SResource());
		expandClasses.put(Integer.valueOf(C_SERT_RESOURCE_TMPL),	new SEResourceTemplate());
		expandClasses.put(Integer.valueOf(C_RRSD_RSD),			new RResourceStateDefinition());
		expandClasses.put(Integer.valueOf(C_RTRSD_RSD),			new RTResourceStateDefinition());
		expandClasses.put(Integer.valueOf(C_RSMRSD_RSD),		new RSMResourceStateDefinition());
		expandClasses.put(Integer.valueOf(C_RSPRSD_RSD),		new RSPResourceStateDefinition());
		expandClasses.put(Integer.valueOf(C_SERSM_RSM),			new SEResourceStateMapping());
		expandClasses.put(Integer.valueOf(C_NRRSP_RSP),			new NRResourceStateProfile());
		expandClasses.put(Integer.valueOf(C_SEVSC_SCHEDULE),		new SEVSchedule());
		expandClasses.put(Integer.valueOf(C_EVSEV_SCHEDULED_EVENT),	new EVScheduledEvent());
		expandClasses.put(Integer.valueOf(C_PLS_SCOPE),			new PLScope());
		expandClasses.put(Integer.valueOf(C_RS_SCOPE),			new RScope());
		expandClasses.put(Integer.valueOf(C_ALLNONE_STOP),		null);
		expandClasses.put(Integer.valueOf(C_SETR_TRIGGER),		new SETrigger());
		expandClasses.put(Integer.valueOf(C_NRTR_TRIGGER),		new NRTrigger());
		expandClasses.put(Integer.valueOf(C_RTR_TRIGGER),		new RTrigger());
		expandClasses.put(Integer.valueOf(C_GU_USER),			new GUser());
		expandClasses.put(Integer.valueOf(C_OT_WATCH_TYPE),		new OMWatchType());
		expandClasses.put(Integer.valueOf(C_WT_OBJECT_MONITOR),		new WTObjectMonitor());
		expandClasses.put(Integer.valueOf(C_OT_JOB_DEFINITION),		new OMWatcher());
		expandClasses.put(Integer.valueOf(C_OT_TRIGGER),		new OMTrigger());
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
		{O_DISPATCH,		T_INTERVAL,		T_INTERVAL},
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
		{O_INTERVAL,		T_JOB_DEFINITION,	T_INTERVAL},
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
		new DumpExpandItem(ST_INTERVAL,        CA_TS,	new PathVector().addThis(new DumpRule(SO_DISPATCH,        CA_TS))),
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
				dr.operator = (Expander) expandClasses.get(Integer.valueOf(idx));
			}
		}
	}
}

