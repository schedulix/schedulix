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


#ifndef _INCLUDED_libcommon_h
#define _INCLUDED_libcommon_h

#define PID_SEP		"@"
#define JIF_SEP		"+"

extern const char *last_error;

#define RETURN_FALSE(err)	{ last_error = err; return false; }

extern const char *itoa (const int i);

extern const char *timestamp (void);

extern const char *errText  (const char *const msg, const char *const val);
extern const char *errTextX (const char *const msg, const char *const errt, const int errn);
extern const char *errText  (const char *const msg,                         const int errn);
extern const char *errText  (const char *const msg, const char *const val,  const int errn);

extern void die (void);
extern void die (const char *const msg, const char *const val);
extern void die (const char *const msg,                        const int errn);

extern void verbose_print_started (void);

#endif
