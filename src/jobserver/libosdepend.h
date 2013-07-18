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


#ifndef _INCLUDED_libosdepend_h
#define _INCLUDED_libosdepend_h

#ifndef WINDOOF
#include <sys/stat.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <wait.h>
#include <signal.h>
#ifdef NETBSD
#include <sys/param.h>
#include <sys/sysctl.h>
#include <sys/proc.h>
#include <sys/types.h>
#endif
#else
#include <io.h>
#include <direct.h>
#include <process.h>
#include <Windows.h>
#endif

#ifndef WINDOOF
#define LIBEXPORT	extern
#define LIBIMPORT

#define DEV_NULL	"/dev/null"
#define GET_ENV		"/bin/sh -c env"

#define FILEHANDLE	FILE *
#define PID_T		pid_t
#else
#define LIBEXPORT	__declspec (dllexport)
#define LIBIMPORT	__declspec (dllimport)

#define DEV_NULL	"NUL"
#define GET_ENV		"CMD.EXE /C SET"

#define FILEHANDLE	HANDLE
#define PID_T		DWORD

#define chdir		_chdir
#define dup2		_dup2
#define sleep(sec)	Sleep ((sec) * 1000)
#define snprintf	_snprintf
#define strcasecmp	_stricmp
#endif

#if defined (LINUX)
#define OS_TYPE "Linux"
#elif defined (SOLARIS)
#define OS_TYPE "Solaris"
#elif defined (NETBSD)
#define OS_TYPE "NetBSD"
#elif defined (WINDOOF)
#define OS_TYPE "Win32"
#endif

#define FILE_REOPEN_DELAY_SECONDS	5

typedef struct {
	const char *filnam;
	const char *command;
	char      **args;
	const char *workdir;
	bool        usepath;
	bool        verboselogs;
	const char *logfile;
	bool        logfileapp;
	const char *errlog;
	bool        errlogapp;
	bool        samelogs;
} JOB_T;

#ifndef WINDOOF
extern JOB_T job;
extern char boottime_how;
extern const char *last_error;
#else
#ifndef DLL
LIBIMPORT JOB_T job;
LIBIMPORT char boottime_how;
LIBIMPORT const char *last_error;
#else
extern LIBEXPORT JOB_T job;
extern LIBEXPORT char boottime_how;
extern LIBEXPORT const char *last_error;
#endif
#endif

extern bool isAlive (const char *const upid, bool *const alive);

#endif
