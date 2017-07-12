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
import java.lang.*;
import java.util.*;
import java.text.*;

import de.independit.scheduler.server.*;
import de.independit.scheduler.server.util.*;
import de.independit.scheduler.server.exception.*;
import de.independit.scheduler.server.parser.*;

public class SmeVariableResolver extends VariableResolver
{

	public static final String __version = "@(#) $Id: SmeVariableResolver.java,v 2.14.4.2 2013/03/20 11:27:16 ronald Exp $";

	public final static String S_JOBID	= SDMSSubmittedEntity.S_JOBID;
	public final static String S_SEID	= SDMSSubmittedEntity.S_SEID;
	public final static String S_MASTERID	= SDMSSubmittedEntity.S_MASTERID;
	public final static String S_KEY	= SDMSSubmittedEntity.S_KEY;
	public final static String S_PID	= SDMSSubmittedEntity.S_PID;
	public final static String S_LOGFILE	= SDMSSubmittedEntity.S_LOGFILE;
	public final static String S_ERRORLOG	= SDMSSubmittedEntity.S_ERRORLOG;
	public final static String S_WORKDIR	= SDMSSubmittedEntity.S_WORKDIR;
	public final static String S_SDMSHOST	= SDMSSubmittedEntity.S_SDMSHOST;
	public final static String S_SDMSPORT	= SDMSSubmittedEntity.S_SDMSPORT;
	public final static String S_JOBNAME	= SDMSSubmittedEntity.S_JOBNAME;
	public final static String S_JOBTAG	= SDMSSubmittedEntity.S_JOBTAG;
	public final static String S_TRNAME	= SDMSSubmittedEntity.S_TRNAME;
	public final static String S_TRTYPE	= SDMSSubmittedEntity.S_TRTYPE;
	public final static String S_TRBASE	= SDMSSubmittedEntity.S_TRBASE;
	public final static String S_TRBASEID	= SDMSSubmittedEntity.S_TRBASEID;
	public final static String S_TRBASEJOBID= SDMSSubmittedEntity.S_TRBASEJOBID;
	public final static String S_TRORIGIN	= SDMSSubmittedEntity.S_TRORIGIN;
	public final static String S_TRORIGINID	= SDMSSubmittedEntity.S_TRORIGINID;
	public final static String S_TRORIGINJOBID = SDMSSubmittedEntity.S_TRORIGINJOBID;
	public final static String S_TRREASON	= SDMSSubmittedEntity.S_TRREASON;
	public final static String S_TRREASONID	= SDMSSubmittedEntity.S_TRREASONID;
	public final static String S_TRREASONJOBID = SDMSSubmittedEntity.S_TRREASONJOBID;
	public final static String S_TRSEQ	= SDMSSubmittedEntity.S_TRSEQ;
	public final static String S_TROSTATE	= SDMSSubmittedEntity.S_TROSTATE;
	public final static String S_TRNSTATE	= SDMSSubmittedEntity.S_TRNSTATE;
	public final static String S_TRWARNING	= SDMSSubmittedEntity.S_TRWARNING;
	public final static String S_SUBMITTS	= SDMSSubmittedEntity.S_SUBMITTS;
	public final static String S_STARTTS	= SDMSSubmittedEntity.S_STARTTS;
	public final static String S_EXPRUNTIME	= SDMSSubmittedEntity.S_EXPRUNTIME;
	public final static String S_EXPFINALTIME = SDMSSubmittedEntity.S_EXPFINALTIME;
	public final static String S_JOBSTATE	= SDMSSubmittedEntity.S_JOBSTATE;
	public final static String S_MERGEDSTATE = SDMSSubmittedEntity.S_MERGEDSTATE;
	public final static String S_PARENTID	= SDMSSubmittedEntity.S_PARENTID;
	public final static String S_STATE	= SDMSSubmittedEntity.S_STATE;
	public final static String S_ISRESTARTABLE = SDMSSubmittedEntity.S_ISRESTARTABLE;
	public final static String S_SYNCTS	= SDMSSubmittedEntity.S_SYNCTS;
	public final static String S_RESOURCETS	= SDMSSubmittedEntity.S_RESOURCETS;
	public final static String S_RUNNABLETS	= SDMSSubmittedEntity.S_RUNNABLETS;
	public final static String S_FINISHTS	= SDMSSubmittedEntity.S_FINISHTS;
	public final static String S_SYSDATE	= SDMSSubmittedEntity.S_SYSDATE;
	public final static String S_WARNING	= SDMSSubmittedEntity.S_WARNING;
	public final static String S_RERUNSEQ	= SDMSSubmittedEntity.S_RERUNSEQ;
	public final static String S_SCOPENAME	= SDMSSubmittedEntity.S_SCOPENAME;

	public final static String S_IDLE_TIME	= SDMSSubmittedEntity.S_IDLE_TIME;
	public final static String S_DEPENDENCY_WAIT_TIME	= SDMSSubmittedEntity.S_DEPENDENCY_WAIT_TIME;
	public final static String S_SUSPEND_TIME	= SDMSSubmittedEntity.S_SUSPEND_TIME;
	public final static String S_SYNC_TIME	= SDMSSubmittedEntity.S_SYNC_TIME;
	public final static String S_RESOURCE_TIME	= SDMSSubmittedEntity.S_RESOURCE_TIME;
	public final static String S_JOBSERVER_TIME	= SDMSSubmittedEntity.S_JOBSERVER_TIME;
	public final static String S_RESTARTABLE_TIME	= SDMSSubmittedEntity.S_RESTARTABLE_TIME;
	public final static String S_CHILD_WAIT_TIME	= SDMSSubmittedEntity.S_CHILD_WAIT_TIME;
	public final static String S_PROCESS_TIME	= SDMSSubmittedEntity.S_PROCESS_TIME;
	public final static String S_ACTIVE_TIME	= SDMSSubmittedEntity.S_ACTIVE_TIME;
	public final static String S_IDLE_PCT		= SDMSSubmittedEntity.S_IDLE_PCT;

	public final static int I_JOBID		= 1;
	public final static int I_MASTERID	= 2;
	public final static int I_KEY		= 3;
	public final static int I_PID		= 4;
	public final static int I_LOGFILE	= 5;
	public final static int I_ERRORLOG	= 6;
	public final static int I_SDMSHOST	= 7;
	public final static int I_SDMSPORT	= 8;
	public final static int I_JOBNAME	= 9;
	public final static int I_JOBTAG	= 10;
	public final static int I_TRNAME	= 11;
	public final static int I_TRTYPE	= 12;
	public final static int I_TRORIGIN	= 13;
	public final static int I_TRORIGINID	= 14;
	public final static int I_TRORIGINJOBID	= 15;
	public final static int I_TRREASON	= 16;
	public final static int I_TRREASONID	= 17;
	public final static int I_TRREASONJOBID	= 19;
	public final static int I_TRBASE	= 20;
	public final static int I_TRBASEID	= 21;
	public final static int I_TRBASEJOBID	= 22;
	public final static int I_TRSEQ		= 23;
	public final static int I_TROSTATE	= 24;
	public final static int I_TRNSTATE	= 25;
	public final static int I_SUBMITTS	= 26;
	public final static int I_STARTTS	= 27;
	public final static int I_EXPRUNTIME	= 28;
	public final static int I_JOBSTATE	= 29;
	public final static int I_MERGEDSTATE	= 30;
	public final static int I_PARENTID	= 31;
	public final static int I_STATE		= 32;
	public final static int I_ISRESTARTABLE	= 33;
	public final static int I_SYNCTS	= 34;
	public final static int I_RESOURCETS	= 35;
	public final static int I_RUNNABLETS	= 36;
	public final static int I_FINISHTS	= 37;
	public final static int I_SYSDATE	= 38;
	public final static int I_SEID		= 39;
	public final static int I_WORKDIR	= 40;
	public final static int I_WARNING	= 41;
	public final static int I_TRWARNING	= 42;
	public final static int I_RERUNSEQ	= 43;
	public final static int I_SCOPENAME	= 44;
	public final static int I_EXPFINALTIME	= 45;
	public final static int I_IDLE_TIME	= 46;
	public final static int I_DEPENDENCY_WAIT_TIME	= 47;
	public final static int I_SUSPEND_TIME	= 48;
	public final static int I_SYNC_TIME	= 49;
	public final static int I_RESOURCE_TIME	= 50;
	public final static int I_JOBSERVER_TIME	= 51;
	public final static int I_RESTARTABLE_TIME	= 52;
	public final static int I_CHILD_WAIT_TIME	= 53;
	public final static int I_PROCESS_TIME		= 54;
	public final static int I_ACTIVE_TIME		= 55;
	public final static int I_IDLE_PCT		= 56;

	private final static HashMap specialNames = new HashMap();

	static {
		specialNames.put(S_JOBID,	new Integer(I_JOBID));
		specialNames.put(S_SEID,	new Integer(I_SEID));
		specialNames.put(S_MASTERID,	new Integer(I_MASTERID));
		specialNames.put(S_KEY,		new Integer(I_KEY));
		specialNames.put(S_PID,		new Integer(I_PID));
		specialNames.put(S_LOGFILE,	new Integer(I_LOGFILE));
		specialNames.put(S_ERRORLOG,	new Integer(I_ERRORLOG));
		specialNames.put(S_WORKDIR,	new Integer(I_WORKDIR));
		specialNames.put(S_SDMSHOST,	new Integer(I_SDMSHOST));
		specialNames.put(S_SDMSPORT,	new Integer(I_SDMSPORT));
		specialNames.put(S_JOBNAME,	new Integer(I_JOBNAME));
		specialNames.put(S_JOBTAG,	new Integer(I_JOBTAG));
		specialNames.put(S_TRNAME,	new Integer(I_TRNAME));
		specialNames.put(S_TRTYPE,	new Integer(I_TRTYPE));
		specialNames.put(S_TRORIGIN,	new Integer(I_TRORIGIN));
		specialNames.put(S_TRORIGINID,	new Integer(I_TRORIGINID));
		specialNames.put(S_TRORIGINJOBID, new Integer(I_TRORIGINJOBID));
		specialNames.put(S_TRREASON,	new Integer(I_TRREASON));
		specialNames.put(S_TRREASONID,	new Integer(I_TRREASONID));
		specialNames.put(S_TRREASONJOBID, new Integer(I_TRREASONJOBID));
		specialNames.put(S_TRBASE,	new Integer(I_TRBASE));
		specialNames.put(S_TRBASEID,	new Integer(I_TRBASEID));
		specialNames.put(S_TRBASEJOBID,	new Integer(I_TRBASEJOBID));
		specialNames.put(S_TRSEQ,	new Integer(I_TRSEQ));
		specialNames.put(S_TROSTATE,	new Integer(I_TROSTATE));
		specialNames.put(S_TRNSTATE,	new Integer(I_TRNSTATE));
		specialNames.put(S_TRWARNING,	new Integer(I_TRWARNING));
		specialNames.put(S_SUBMITTS,	new Integer(I_SUBMITTS));
		specialNames.put(S_STARTTS,	new Integer(I_STARTTS));
		specialNames.put(S_EXPRUNTIME,	new Integer(I_EXPRUNTIME));
		specialNames.put(S_EXPFINALTIME, new Integer(I_EXPFINALTIME));
		specialNames.put(S_JOBSTATE,	new Integer(I_JOBSTATE));
		specialNames.put(S_MERGEDSTATE,	new Integer(I_MERGEDSTATE));
		specialNames.put(S_PARENTID,	new Integer(I_PARENTID));
		specialNames.put(S_STATE,	new Integer(I_STATE));
		specialNames.put(S_ISRESTARTABLE, new Integer(I_ISRESTARTABLE));
		specialNames.put(S_SYNCTS,	new Integer(I_SYNCTS));
		specialNames.put(S_RESOURCETS,	new Integer(I_RESOURCETS));
		specialNames.put(S_RUNNABLETS,	new Integer(I_RUNNABLETS));
		specialNames.put(S_FINISHTS,	new Integer(I_FINISHTS));
		specialNames.put(S_SYSDATE,	new Integer(I_SYSDATE));
		specialNames.put(S_WARNING,	new Integer(I_WARNING));
		specialNames.put(S_RERUNSEQ,	new Integer(I_RERUNSEQ));
		specialNames.put(S_SCOPENAME,	new Integer(I_SCOPENAME));
		specialNames.put(S_IDLE_TIME,	new Integer(I_IDLE_TIME));
		specialNames.put(S_SUSPEND_TIME,	new Integer(I_SUSPEND_TIME));
		specialNames.put(S_DEPENDENCY_WAIT_TIME,	new Integer(I_DEPENDENCY_WAIT_TIME));
		specialNames.put(S_SYNC_TIME,	new Integer(I_SYNC_TIME));
		specialNames.put(S_RESOURCE_TIME,	new Integer(I_RESOURCE_TIME));
		specialNames.put(S_JOBSERVER_TIME,	new Integer(I_JOBSERVER_TIME));
		specialNames.put(S_RESTARTABLE_TIME,	new Integer(I_RESTARTABLE_TIME));
		specialNames.put(S_CHILD_WAIT_TIME,	new Integer(I_CHILD_WAIT_TIME));
		specialNames.put(S_PROCESS_TIME,	new Integer(I_PROCESS_TIME));
		specialNames.put(S_ACTIVE_TIME,	new Integer(I_ACTIVE_TIME));
		specialNames.put(S_IDLE_PCT,	new Integer(I_IDLE_PCT));
	}

	protected String getVariableValue(SystemEnvironment sysEnv, SDMSProxy thisObject, String key, boolean fastAccess, String mode, boolean triggercontext, long version, SDMSScope evalScope)
		throws SDMSException
	{
		sysEnv.tx.txData.remove(SystemEnvironment.S_ISDEFAULT);
		final String retval = getInternalVariableValue(sysEnv, thisObject, key, fastAccess, mode, triggercontext, new Stack(), version, evalScope);

		return retval;
	}

	private String getFolderVariableValue(SystemEnvironment sysEnv, SDMSSubmittedEntity thisSme, String key, SDMSFolder f, long seVersion)
		throws SDMSException
	{
		String retVal = null;

		try {
			retVal = f.getVariableValue(sysEnv, key, seVersion);
		} catch (NotFoundException nfe) {
			Long parentId = thisSme.getParentId(sysEnv);
			if (parentId == null) {
				throw new NotFoundException(new SDMSMessage(sysEnv, "03405032240", "Couldn't resolve the variable $1", key));
			}
			SDMSSubmittedEntity sme = SDMSSubmittedEntityTable.getObject(sysEnv, parentId);
			long nseVersion = sme.getSeVersion(sysEnv).longValue();
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), nseVersion);
			SDMSFolder pf = SDMSFolderTable.getObject(sysEnv, se.getFolderId(sysEnv), nseVersion);
			retVal = getFolderVariableValue(sysEnv, sme, key, pf, nseVersion);
		}

		return retVal;
	}

	protected String getInternalVariableValue(SystemEnvironment sysEnv,
						SDMSProxy thisSmeP,
						String key,
						boolean fastAccess,
						String mode,
						boolean triggercontext,
						Stack recursionCheck,
						long version,
						SDMSScope evalScope)
		throws SDMSException
	{
		SDMSEntityVariable ev = null;
		SDMSParameterDefinition pd = null;
		SDMSSubmittedEntity thisSme = (SDMSSubmittedEntity) thisSmeP;
		boolean warn = SystemEnvironment.warn_variables;
		boolean strict = SystemEnvironment.strict_variables;
		String retVal;

		if(!mode.equals(ParseStr.S_DEFAULT)) {
			if(mode.equals(ParseStr.S_LIBERAL)) {
				warn = false;
				strict = false;
			} else if(mode.equals(ParseStr.S_WARN)) {
				warn = true;
				strict = false;
			} else {
				strict = true;
			}
		}

		if(specialNames.containsKey(key)) return getSpecialValue(sysEnv, thisSme, key, triggercontext, evalScope);
		try {
			ev = SDMSEntityVariableTable.idx_smeId_Name_getUnique(sysEnv, new SDMSKey(thisSme.getId(sysEnv), key));
			Long evLink = ev.getEvLink(sysEnv);
			while(evLink != null) {
				ev = SDMSEntityVariableTable.getObject(sysEnv, evLink);
				evLink = ev.getEvLink(sysEnv);
			}
			retVal = ev.getValue(sysEnv).substring(1);
			return parseAndSubstitute(sysEnv, thisSme, retVal, fastAccess, mode, triggercontext, recursionCheck, version, evalScope);

		} catch (NotFoundException nfe) {
			try {
				long seVersion = thisSme.getSeVersion(sysEnv).longValue();
				pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(thisSme.getSeId(sysEnv), key), seVersion);
				if(pd.getType(sysEnv).intValue() == SDMSParameterDefinition.IMPORT) fastAccess = false;
			} catch (NotFoundException nfe2) {
				pd = null;
				if(fastAccess) return emptyString;
				if(strict) {
					SDMSThread.doTrace(null, "Couldn't find parameter " + key + " for job " + thisSme.getId(sysEnv) +
								 " (no import specified)", SDMSThread.SEVERITY_WARNING);
					throw new NotFoundException(new SDMSMessage(sysEnv, "03304101030", "Couldn't resolve Parameter $1", key));
				}
				if(warn) {
					SDMSThread.doTrace(null, "Couldn't find parameter " + key + " for job " + thisSme.getId(sysEnv) +
								 " (no import specified)", SDMSThread.SEVERITY_WARNING);
				}
			}
		}
		try {
			retVal =  getVariableExtendedValue(sysEnv, thisSme, thisSme, key, new HashSet(), fastAccess, mode, triggercontext, recursionCheck, evalScope);
		} catch(NotFoundException nf) {
			if(fastAccess) return emptyString;
			retVal = null;
		}

		boolean isDefault;
		Boolean tmpIsDefault = (Boolean) sysEnv.tx.txData.get(SystemEnvironment.S_ISDEFAULT);
		if(tmpIsDefault == null)	isDefault = true;
		else				isDefault = tmpIsDefault.booleanValue();

		if(retVal == null || isDefault) {
			long seVersion = thisSme.getSeVersion(sysEnv).longValue();
			Long scopeId = thisSme.getScopeId(sysEnv);
			SDMSScope s = evalScope;
			if(scopeId != null && evalScope == null)
				s = SDMSScopeTable.getObject(sysEnv, scopeId);
			SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, thisSme.getSeId(sysEnv), seVersion);
			SDMSFolder f = SDMSFolderTable.getObject(sysEnv, se.getFolderId(sysEnv), seVersion);
			String nRetVal;

			try {
				try {
					if(s != null)
						nRetVal =  s.getVariableValue(sysEnv, key);
					else
						throw new NotFoundException();
				} catch (NotFoundException nfe1) {
					nRetVal =  getFolderVariableValue(sysEnv, thisSme, key, f, seVersion);
				}
			} catch (NotFoundException nfe) {
				if(isDefault && retVal != null) {
					return retVal;
				}
				throw nfe;
			}
			return nRetVal;
		}
		return retVal;
	}

	private String getVariableValue(SystemEnvironment sysEnv,
					SDMSSubmittedEntity thisSme,
					SDMSSubmittedEntity baseSme,
					String key,
					HashSet visited,
					boolean fastAccess,
					String mode,
					boolean triggercontext,
					Stack recursionCheck,
					SDMSScope evalScope)
		throws SDMSException
	{
		try {
			SDMSEntityVariable ev = SDMSEntityVariableTable.idx_smeId_Name_getUnique(sysEnv, new SDMSKey(thisSme.getId(sysEnv), key));
			if(ev.getIsLocal(sysEnv).booleanValue()) {
				return getVariableExtendedValue(sysEnv, thisSme, baseSme, key, visited, fastAccess, mode, triggercontext, recursionCheck, evalScope);
			}
			Long evLink = ev.getEvLink(sysEnv);
			while(evLink != null) {
				ev = SDMSEntityVariableTable.getObject(sysEnv, evLink);
				evLink = ev.getEvLink(sysEnv);
			}
			sysEnv.tx.txData.put(SystemEnvironment.S_ISDEFAULT, Boolean.FALSE);
			return parseAndSubstitute(sysEnv, thisSme, ev.getValue(sysEnv).substring(1), fastAccess, mode, triggercontext, recursionCheck, -1, evalScope);

		} catch (NotFoundException nfe) {
			String retval =  getVariableExtendedValue(sysEnv, thisSme, baseSme, key, visited, fastAccess, mode, triggercontext, recursionCheck, evalScope);
			return retval;
		}
	}

	private String getSpecialValue(SystemEnvironment sysEnv, SDMSSubmittedEntity thisSme, String key, boolean triggercontext, SDMSScope evalScope)
		throws SDMSException
	{
		long seVersion;
		int varno = ((Integer) specialNames.get(key)).intValue();
		int trObjectType;
		long sysdate;
		String s;
		SDMSSchedulingEntity se;
		SDMSSubmittedEntity sme;
		SDMSTrigger t;
		SDMSResourceStateDefinition rsd;
		SDMSExitStateDefinition esd;
		Long trId;
		Long baseSmeId;
		Long reasonSmeId;
		Long fireSmeId;
		Long trSdIdOld;
		Long trSdIdNew;
		Long parentId;
		Long ts;
		Long jobState;
		Integer trSeq;
		Boolean isRestartable;
		int i;

		switch(varno) {
			case I_JOBID:
				return thisSme.getId(sysEnv).toString();

			case I_SEID:
				return thisSme.getSeId(sysEnv).toString();

			case I_MASTERID:
				return thisSme.getMasterId(sysEnv).toString();

			case I_KEY:
				return thisSme.getAccessKey(sysEnv).toString();

			case I_PID:
				s = thisSme.getExtPid(sysEnv);
				if(s == null) return emptyString;
				i = s.indexOf(SDMSSubmittedEntity.PID_SEP);
				return s.substring(0, i);

			case I_LOGFILE:
				s = thisSme.getLogfile(sysEnv);
				return (s == null ? emptyString : s);

			case I_ERRORLOG:
				s = thisSme.getErrlogfile(sysEnv);
				return (s == null ? emptyString : s);

			case I_WORKDIR:
				s = thisSme.getWorkdir(sysEnv);
				return (s == null ? emptyString : s);

			case I_SDMSHOST:
				return SystemEnvironment.hostname;

			case I_SDMSPORT:
				return emptyString + SystemEnvironment.port;

			case I_JOBNAME:
				seVersion = thisSme.getSeVersion(sysEnv).longValue();
				se = SDMSSchedulingEntityTable.getObject(sysEnv, thisSme.getSeId(sysEnv), seVersion);
				return (se == null ? emptyString : se.pathString(sysEnv, seVersion));

			case I_JOBTAG:
				s = thisSme.getChildTag(sysEnv);
				return (s == null ? emptyString : s);

			case I_TRNAME:
				trId = thisSme.getTrId(sysEnv);
				if (trId == null) return emptyString;
				t = SDMSTriggerTable.getObject(sysEnv, trId, thisSme.getSeVersion(sysEnv).longValue());
				return t.getName(sysEnv);

			case I_TRTYPE:
				trId = thisSme.getTrId(sysEnv);
				if (trId == null) return emptyString;
				t = SDMSTriggerTable.getObject(sysEnv, trId, thisSme.getSeVersion(sysEnv).longValue());
				return t.getObjectTypeAsString(sysEnv);

			case I_TRBASE:
				baseSmeId = thisSme.getBaseSmeId(sysEnv);
				if (baseSmeId == null) return emptyString;
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, baseSmeId);
				seVersion = sme.getSeVersion(sysEnv).longValue();
				se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), seVersion);
				return se.pathString(sysEnv, seVersion);

			case I_TRBASEID:
				baseSmeId = thisSme.getBaseSmeId(sysEnv);
				if (baseSmeId == null) return emptyString;
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, baseSmeId);
				return (sme.getId(sysEnv)).toString();

			case I_TRBASEJOBID:
				baseSmeId = thisSme.getBaseSmeId(sysEnv);
				if (baseSmeId == null) return emptyString;
				return baseSmeId.toString();

			case I_TRREASON:
				reasonSmeId = thisSme.getReasonSmeId(sysEnv);
				if (reasonSmeId == null) return emptyString;
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, reasonSmeId);
				seVersion = sme.getSeVersion(sysEnv).longValue();
				se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), seVersion);
				return se.pathString(sysEnv, seVersion);

			case I_TRREASONID:
				reasonSmeId = thisSme.getReasonSmeId(sysEnv);
				if (reasonSmeId == null) return emptyString;
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, reasonSmeId);
				return (sme.getId(sysEnv)).toString();

			case I_TRREASONJOBID:
				reasonSmeId = thisSme.getReasonSmeId(sysEnv);
				if (reasonSmeId == null) return emptyString;
				return reasonSmeId.toString();

			case I_TRORIGIN:
				fireSmeId = thisSme.getFireSmeId(sysEnv);
				if (fireSmeId == null) return emptyString;
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, fireSmeId);
				seVersion = sme.getSeVersion(sysEnv).longValue();
				se = SDMSSchedulingEntityTable.getObject(sysEnv, sme.getSeId(sysEnv), seVersion);
				return se.pathString(sysEnv, seVersion);

			case I_TRORIGINID:
				fireSmeId = thisSme.getFireSmeId(sysEnv);
				if (fireSmeId == null) return emptyString;
				sme = SDMSSubmittedEntityTable.getObject(sysEnv, fireSmeId);
				return (sme.getId(sysEnv)).toString();

			case I_TRORIGINJOBID:
				fireSmeId = thisSme.getFireSmeId(sysEnv);
				if (fireSmeId == null) return emptyString;
				return fireSmeId.toString();

			case I_TRSEQ:
				trSeq = thisSme.getTrSeq(sysEnv);
				if (trSeq == null) return emptyString;
				return trSeq.toString();

			case I_TROSTATE:
				trId = thisSme.getTrId(sysEnv);
				if (trId == null) return emptyString;
				t = SDMSTriggerTable.getObject(sysEnv, trId, thisSme.getSeVersion(sysEnv).longValue());
				trObjectType = t.getObjectType(sysEnv).intValue();
				if (trObjectType == SDMSTrigger.JOB_DEFINITION) {
					return emptyString;
				}
				trSdIdOld = thisSme.getTrSdIdOld(sysEnv);
				rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, trSdIdOld, thisSme.getSeVersion(sysEnv).longValue());
				return rsd.getName(sysEnv);

			case I_TRNSTATE:
				trId = thisSme.getTrId(sysEnv);
				if (trId == null) return emptyString;
				t = SDMSTriggerTable.getObject(sysEnv, trId, thisSme.getSeVersion(sysEnv).longValue());
				trObjectType = t.getObjectType(sysEnv).intValue();
				if (trObjectType == SDMSTrigger.JOB_DEFINITION) {
					trSdIdNew = thisSme.getTrSdIdNew(sysEnv);
					if (trSdIdNew == null) return emptyString;
					esd = SDMSExitStateDefinitionTable.getObject(sysEnv, trSdIdNew, thisSme.getSeVersion(sysEnv).longValue());
					return esd.getName(sysEnv);
				} else {
					trSdIdNew = thisSme.getTrSdIdNew(sysEnv);
					rsd = SDMSResourceStateDefinitionTable.getObject(sysEnv, trSdIdNew, thisSme.getSeVersion(sysEnv).longValue());
					return rsd.getName(sysEnv);
				}

			case I_TRWARNING:
				return emptyString;

			case I_EXPRUNTIME:
				seVersion = thisSme.getSeVersion(sysEnv).longValue();
				se = SDMSSchedulingEntityTable.getObject(sysEnv, thisSme.getSeId(sysEnv), seVersion);
				Integer expRuntime = se.getExpectedRuntime(sysEnv);
				if(expRuntime != null) return expRuntime.toString();
				return emptyString;

			case I_EXPFINALTIME:
				seVersion = thisSme.getSeVersion(sysEnv).longValue();
				se = SDMSSchedulingEntityTable.getObject(sysEnv, thisSme.getSeId(sysEnv), seVersion);
				Integer expFinaltime = se.getExpectedFinaltime(sysEnv);
				if(expFinaltime != null) return expFinaltime.toString();
				return emptyString;

			case I_JOBSTATE:
				jobState = thisSme.getJobEsdId(sysEnv);
				if(jobState == null) return emptyString;
				esd = SDMSExitStateDefinitionTable.getObject(sysEnv, jobState, thisSme.getSeVersion(sysEnv).longValue());
				return esd.getName(sysEnv);

			case I_MERGEDSTATE:
				jobState = thisSme.getFinalEsdId(sysEnv);
				if(jobState == null) return emptyString;
				esd = SDMSExitStateDefinitionTable.getObject(sysEnv, jobState, thisSme.getSeVersion(sysEnv).longValue());
				return esd.getName(sysEnv);

			case I_PARENTID:
				parentId = thisSme.getParentId(sysEnv);
				if(parentId == null) return emptyString;
				return parentId.toString();

			case I_STATE:
				return thisSme.getStateAsString(sysEnv);

			case I_ISRESTARTABLE:
				isRestartable = thisSme.getJobIsRestartable(sysEnv);
				if(isRestartable.booleanValue()) return "1";
				return "0";

			case I_SUBMITTS:
				ts = thisSme.getSubmitTs(sysEnv);
				if(ts != null) {
					if(triggercontext) return emptyString + ts.longValue()/1000;
					return myFormat.format(new java.util.Date(ts.longValue()));
				}
				return emptyString;

			case I_SYNCTS:
				ts = thisSme.getSyncTs(sysEnv);
				if(ts != null) {
					if(triggercontext) return emptyString + ts.longValue()/1000;
					return myFormat.format(new java.util.Date(ts.longValue()));
				}
				return emptyString;

			case I_RESOURCETS:
				ts = thisSme.getResourceTs(sysEnv);
				if(ts != null) {
					if(triggercontext) return emptyString + ts.longValue()/1000;
					return myFormat.format(new java.util.Date(ts.longValue()));
				}
				return emptyString;

			case I_RUNNABLETS:
				ts = thisSme.getRunnableTs(sysEnv);
				if(ts != null) {
					if(triggercontext) return emptyString + ts.longValue()/1000;
					return myFormat.format(new java.util.Date(ts.longValue()));
				}
				return emptyString;

			case I_STARTTS:
				ts = thisSme.getStartTs(sysEnv);
				if(ts != null) {
					if(triggercontext) return emptyString + ts.longValue()/1000;
					return myFormat.format(new java.util.Date(ts.longValue()));
				}
				return emptyString;

			case I_FINISHTS:
				ts = thisSme.getFinishTs(sysEnv);
				if(ts != null) {
					if(triggercontext) return emptyString + ts.longValue()/1000;
					return myFormat.format(new java.util.Date(ts.longValue()));
				}
				return emptyString;

			case I_SYSDATE:
				sysdate = System.currentTimeMillis();
				if(triggercontext) return emptyString + sysdate/1000;
				return myFormat.format(new java.util.Date(sysdate));

			case I_WARNING:
				return emptyString;

			case I_RERUNSEQ:
				return emptyString + thisSme.getRerunSeq(sysEnv);

			case I_SCOPENAME:
				SDMSScope scope;
				Long scopeId = thisSme.getScopeId(sysEnv);
				if (scopeId != null)
					scope = SDMSScopeTable.getObject(sysEnv, scopeId);
				else {
					scope = evalScope;
				}
				return (scope == null ? emptyString : scope.pathString(sysEnv));

			case I_IDLE_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getIdleTime(sysEnv), thisSme.getIdleTs(sysEnv), -1).toString();

			case I_DEPENDENCY_WAIT_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getDependencyWaitTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_DEPENDENCY_WAIT).toString();

			case I_SUSPEND_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getSuspendTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_SUSPEND).toString();

			case I_SYNC_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getSyncTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_SYNCHRONIZE).toString();

			case I_RESOURCE_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getResourceTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_RESOURCE).toString();

			case I_JOBSERVER_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getJobserverTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_JOBSERVER).toString();

			case I_RESTARTABLE_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getRestartableTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_RESTARTABLE).toString();

			case I_CHILD_WAIT_TIME:
				return thisSme.evaluateTime(sysEnv, thisSme.getChildWaitTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_CHILD_WAIT).toString();

			case I_PROCESS_TIME:
			case I_ACTIVE_TIME:
			case I_IDLE_PCT: {
				Integer dwTime = thisSme.evaluateTime(sysEnv, thisSme.getDependencyWaitTime(sysEnv), thisSme.getStatisticTs(sysEnv), SDMSSubmittedEntity.STAT_DEPENDENCY_WAIT);
				Long finalTs = thisSme.getFinalTs(sysEnv);
				int endTs;
				if (finalTs != null)
					endTs = (int)((finalTs.longValue() - thisSme.getSubmitTs(sysEnv).longValue()) / 1000);
				else
					endTs = (int)((sysEnv.cEnv.last() - thisSme.getSubmitTs(sysEnv).longValue()) / 1000);
				int processTime = endTs - dwTime.intValue();
				switch(varno) {
					case I_PROCESS_TIME:
						return new Integer(processTime).toString();
					case I_ACTIVE_TIME:
					case I_IDLE_PCT:
						Integer idleTime = thisSme.evaluateTime(sysEnv, thisSme.getIdleTime(sysEnv), thisSme.getIdleTs(sysEnv), -1);
						if (varno == I_ACTIVE_TIME)
							return new Integer(processTime  - idleTime.intValue()).toString();
						else if (processTime == 0) return emptyString;
							return new Integer(idleTime.intValue() * 100 / processTime).toString();
				}
			}

		}
		throw new FatalException(new SDMSMessage(sysEnv, "03208090953", "Unknown special Parameter : $1", key));
	}

	private String getVariableExtendedValue(SystemEnvironment sysEnv,
						SDMSSubmittedEntity thisSme,
						SDMSSubmittedEntity baseSme,
						String key,
						HashSet visited,
						boolean fastAccess,
						String mode,
						boolean triggercontext,
						Stack recursionCheck,
						SDMSScope evalScope)
		throws SDMSException
	{
		SDMSSubmittedEntity sme;
		SDMSParameterDefinition pd;
		SDMSParameterDefinition lpd;
		SDMSResourceAllocation ra;
		SDMSResource r;
		Long smeId = thisSme.getId(sysEnv);
		Long seId;
		Long nrId;
		Long parentId;
		String linkName;
		String defVal;
		String s;
		Vector v;

		long seVersion = thisSme.getSeVersion(sysEnv).longValue();
		try {
			pd = SDMSParameterDefinitionTable.idx_seId_Name_getUnique(sysEnv, new SDMSKey(thisSme.getSeId(sysEnv), key), seVersion);
			if(!visited.add(new SDMSKey(thisSme.getId(sysEnv), pd.getId(sysEnv)))) {
				seId = thisSme.getSeId(sysEnv);
				SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion);
				throw new CommonErrorException(new SDMSMessage(sysEnv, "03208292044",
							"Run into a loop while trying to resolve variable $1 of $2", key, se.pathString(sysEnv, seVersion)));
			}
			if (pd.getIsLocal(sysEnv).booleanValue() && !smeId.equals(baseSme.getId(sysEnv))) {
				throw new NotFoundException("local");
			}
		} catch (NotFoundException nfe) {
			if(fastAccess) return emptyString;
			parentId = thisSme.getParentId(sysEnv);
			if (parentId == null) {
				throw new NotFoundException(new SDMSMessage(sysEnv, "03208100013", "Couldn't resolve the variable $1", key));
			}
			sme = SDMSSubmittedEntityTable.getObject(sysEnv, parentId);
			return getVariableValue(sysEnv, sme, baseSme, key, visited, fastAccess, mode, triggercontext, recursionCheck, evalScope);
		}
		int type = pd.getType(sysEnv).intValue();
		defVal =  pd.getDefaultValue(sysEnv);
		switch(type) {
			case SDMSParameterDefinition.PARAMETER:
				parentId = thisSme.getParentId(sysEnv);
				try {
					if (parentId == null) {
						throw new NotFoundException(new SDMSMessage(sysEnv, "03208091742", "Couldn't resolve the mandatory parameter $1", key));
					}
					sme = SDMSSubmittedEntityTable.getObject(sysEnv, parentId);
					return getVariableValue(sysEnv, sme, baseSme, key, visited, fastAccess, mode, triggercontext, recursionCheck, evalScope);
				} catch(NotFoundException nfe) {
					if(defVal != null) {
						return parseAndSubstitute(sysEnv, thisSme, defVal.substring(1), false, mode, triggercontext, recursionCheck, -1);
					}
					throw nfe;
				}
			case SDMSParameterDefinition.IMPORT:
				parentId = thisSme.getParentId(sysEnv);
				try {
					if (parentId == null) {
						throw new NotFoundException(new SDMSMessage(sysEnv, "03304161119", "Couldn't resolve the import parameter $1", key));
					}
					sme = SDMSSubmittedEntityTable.getObject(sysEnv, parentId);
					return getVariableValue(sysEnv, sme, baseSme, key, visited, fastAccess, mode, triggercontext, recursionCheck, evalScope);
				} catch(NotFoundException nfe) {
					if(defVal != null) {
						return parseAndSubstitute(sysEnv, thisSme, defVal.substring(1), false, mode, triggercontext, recursionCheck, -1);
					}
					throw nfe;
				}
			case SDMSParameterDefinition.RESULT:
				if(defVal == null) defVal = "=";
				sysEnv.tx.txData.put(SystemEnvironment.S_ISDEFAULT, Boolean.FALSE);
				return parseAndSubstitute(sysEnv, thisSme, defVal.substring(1), false, mode, triggercontext, recursionCheck, -1);
			case SDMSParameterDefinition.CONSTANT:
				sysEnv.tx.txData.put(SystemEnvironment.S_ISDEFAULT, Boolean.FALSE);
				return parseAndSubstitute(sysEnv, thisSme, defVal.substring(1), false, mode, triggercontext, recursionCheck, -1);
			case SDMSParameterDefinition.REFERENCE:
				lpd = SDMSParameterDefinitionTable.getObject(sysEnv, pd.getLinkPdId(sysEnv), seVersion);
				seId = lpd.getSeId(sysEnv);
				linkName = lpd.getName(sysEnv);
				if(seId.equals(thisSme.getSeId(sysEnv))) sme = thisSme;
				else sme = thisSme.getNearestSubmittedEntity(sysEnv, seId, false, false, true);
				if(sme == null) {
					throw new NotFoundException(new SDMSMessage(sysEnv, "03304161131",
							    "Couldn't resolve reference $1",
							    SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion).pathString(sysEnv)));
				} else return getVariableValue(sysEnv, sme, baseSme, linkName, visited, fastAccess, mode, triggercontext, recursionCheck, evalScope);
			case SDMSParameterDefinition.CHILDREFERENCE:
				lpd = SDMSParameterDefinitionTable.getObject(sysEnv, pd.getLinkPdId(sysEnv), seVersion);
				seId = lpd.getSeId(sysEnv);
				linkName = lpd.getName(sysEnv);
				sme = thisSme.getChildSubmittedEntity(sysEnv, seId, null, thisSme, false);
				if(sme == null) {
					throw new NotFoundException(new SDMSMessage(sysEnv, "03304161134",
							    "Couldn't resolve child reference $1",
							    SDMSSchedulingEntityTable.getObject(sysEnv, seId, seVersion).pathString(sysEnv)));
				} else return getVariableValue(sysEnv, sme, baseSme, linkName, visited, fastAccess, mode, triggercontext, recursionCheck, evalScope);
			case SDMSParameterDefinition.RESOURCEREFERENCE:
				lpd = SDMSParameterDefinitionTable.getObject(sysEnv, pd.getLinkPdId(sysEnv), seVersion);
				nrId = lpd.getSeId(sysEnv);
				linkName = lpd.getName(sysEnv);
				HashMap sfp = null;
				if (evalScope == null) {
					v = SDMSResourceAllocationTable.idx_smeId_nrId.getVector(sysEnv, new SDMSKey(smeId, nrId));
					if(v.size() > 1) {
						throw new NotFoundException(new SDMSMessage(sysEnv, "03409222313",
								    "Couldn't resolve reference $1 unambigiously",
								    SDMSNamedResourceTable.getObject(sysEnv, nrId, seVersion).pathString(sysEnv)));
					} else if (v.size() == 0) {
						Long scopeId = thisSme.getScopeId(sysEnv);
						if (scopeId == null)
							throw new NotFoundException(new SDMSMessage(sysEnv, "03711081546",
									    "Couldn't resolve reference $1 unambigiously",
									    SDMSNamedResourceTable.getObject(sysEnv, nrId, seVersion).pathString(sysEnv)));
						try {
							SDMSnpSrvrSRFootprint serverfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, scopeId);
							sfp = serverfp.getFp(sysEnv);
						} catch (NotFoundException nfe) {
							SDMSScope scope = SDMSScopeTable.getObject(sysEnv, scopeId);
							sfp = SystemEnvironment.sched.getScopeFootprint(sysEnv, scope);
						}
						Long rId = (Long) sfp.get(nrId);
						r = SDMSResourceTable.getObject(sysEnv, rId);
					} else {
						ra = (SDMSResourceAllocation) v.get(0);
						r = SDMSResourceTable.getObject(sysEnv, ra.getRId(sysEnv));
					}
				} else {
					try {
						SDMSnpSrvrSRFootprint serverfp = SDMSnpSrvrSRFootprintTable.idx_sId_getUnique(sysEnv, evalScope.getId(sysEnv));
						sfp = serverfp.getFp(sysEnv);
					} catch (NotFoundException nfe) {
						sfp = SystemEnvironment.sched.getScopeFootprint(sysEnv, evalScope);
					}
					Long rId = (Long) sfp.get(nrId);
					if (rId == null) {
						throw new NotFoundException(new SDMSMessage(sysEnv, "03711091158",
							"Couldn't resolve reference $1 unambigiously",
							SDMSNamedResourceTable.getObject(sysEnv, nrId, seVersion).pathString(sysEnv)));
					}
					r = SDMSResourceTable.getObject(sysEnv, rId);
				}
				return r.getVariableValue(sysEnv, linkName, thisSme);
			case SDMSParameterDefinition.EXPRESSION:
				double tmpsum = 0;
				double tmpmax = Double.MIN_VALUE;
				double tmpmin = Double.MAX_VALUE;
				int tmpcnt = 0;
				double tmpd;
				Vector cv = SDMSSubmittedEntityTable.idx_parentId.getVector(sysEnv, smeId);
				for(int j = 0; j < cv.size(); j++) {
					SDMSSubmittedEntity tsme = (SDMSSubmittedEntity) cv.get(j);
					try {
						Long baseSmeId = baseSme.getId(sysEnv);
						Long tsmeId = tsme.getId(sysEnv);
						String newKey = defVal.substring(1);
						if (baseSmeId != null && baseSmeId.equals(tsmeId) && key.equals(newKey)) {
							seId = baseSme.getSeId(sysEnv);
							long vers =  baseSme.getSeVersion(sysEnv).longValue();
							SDMSSchedulingEntity se = SDMSSchedulingEntityTable.getObject(sysEnv, seId, vers);
							throw new CommonErrorException(new SDMSMessage(sysEnv, "03805140836",
										"Run into a loop while trying to resolve variable $1 of job $2", newKey, se.pathString(sysEnv, vers)));
						} else {
							if (baseSmeId != null && baseSmeId.equals(tsmeId) && pd != null) {
								s = getVariableValue(sysEnv, tsme, baseSme, newKey, visited, true , mode, triggercontext, recursionCheck, evalScope);
							} else {
								s = tsme.getVariableValue(sysEnv, newKey, true, ParseStr.S_LIBERAL, triggercontext, evalScope);
							}
						}
					} catch(NotFoundException nfe) {
						continue;
					}
					try {
						tmpd = Double.parseDouble(s);
						tmpsum += tmpd;
						tmpcnt++;
						if(tmpmax < tmpd) tmpmax = tmpd;
						if(tmpmin > tmpd) tmpmin = tmpd;
					} catch (NumberFormatException nfe) {
					}
				}
				sysEnv.tx.txData.put(SystemEnvironment.S_ISDEFAULT, Boolean.FALSE);
				int f = pd.getAggFunction(sysEnv).intValue();
				switch(f) {
					case SDMSParameterDefinition.AVG:	return (tmpcnt == 0 ? emptyString : new Double(tmpsum/tmpcnt).toString());
					case SDMSParameterDefinition.COUNT:	return new Integer(tmpcnt).toString();
					case SDMSParameterDefinition.MIN:	return new Double(tmpmin).toString();
					case SDMSParameterDefinition.MAX:	return new Double(tmpmax).toString();
					case SDMSParameterDefinition.SUM:	return new Double(tmpsum).toString();
				}
				break;
		}
		throw new FatalException(new SDMSMessage(sysEnv, "03208100006", "Fall through while resolving a parameter $1 of $2",
						key, SDMSSchedulingEntityTable.getObject(sysEnv, thisSme.getSeId(sysEnv),
						thisSme.getSeVersion(sysEnv).longValue()).pathString(sysEnv, thisSme.getSeVersion(sysEnv).longValue())));
	}

	public SmeVariableResolver()
	{
	}
}
