/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
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


#ifndef _INCLUDED_libjobserver_h
#define _INCLUDED_libjobserver_h

#include "de_independit_scheduler_jobserver_Utils.h"
#include "libosdepend.h"
#include "feil.h"

const int ERR_EXIT = -1;

#define TIMESTAMP_LEADIN	(char) de_independit_scheduler_jobserver_Utils_TIMESTAMP_LEADIN
#define TIMESTAMP_LEADOUT	(char) de_independit_scheduler_jobserver_Utils_TIMESTAMP_LEADOUT
#define BOOTTIME_NONE		(char) de_independit_scheduler_jobserver_Utils_BOOTTIME_NONE
#define BOOTTIME_SYSTEM		(char) de_independit_scheduler_jobserver_Utils_BOOTTIME_SYSTEM
#define BOOTTIME_FILE		(char) de_independit_scheduler_jobserver_Utils_BOOTTIME_FILE
#define BOOTTIME_DEFAULT	(char) de_independit_scheduler_jobserver_Utils_BOOTTIME_DEFAULT

LIBEXPORT const char *const get_progvers (void);

LIBEXPORT const char *const get_copyright (void);

LIBEXPORT const char *const get_company (void);

LIBEXPORT void die (const char *const msg);
LIBEXPORT void die (const char *const msg, const char *const val, const int errn);

LIBEXPORT void giveup (const char *const status, const char *const msg);
LIBEXPORT void giveup (const char *const status, const char *const msg, const char *const val);
LIBEXPORT void giveup (const char *const status, const char *const msg,                        const int errn);
LIBEXPORT void giveup (const char *const status, const char *const msg, const char *const val, const int errn);

LIBEXPORT FILEHANDLE open_jobfile (void);

LIBEXPORT void close_jobfile (FILEHANDLE fil);

LIBEXPORT bool read_jobfile (FILEHANDLE fil, char **const str);

LIBEXPORT void append_jobfile (const char *const id, const char *const val);
LIBEXPORT void append_jobfile (const char *const id, const int val);

LIBEXPORT void verbose_print_finished (const bool child_error, const int rc);

LIBEXPORT bool set_boottime_how (const char how);

LIBEXPORT const char *get_unique_pid (const PID_T pid);

LIBEXPORT void ignore_all_signals (void);

LIBEXPORT bool execute_child (int *const rc);

#endif
