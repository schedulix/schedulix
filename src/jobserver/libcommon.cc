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


#include <time.h>

#include "libjobserver.h"
#include "libcommon.h"

JOB_T job = {
	NULL,
	NULL,
	NULL,
	NULL,
	false,
	false,
	NULL,
	false,
	NULL,
	false,
	false
};

const char *last_error = NULL;

#define VERSION "v2.5.1/" OS_TYPE

static const char *const copyright = "Copyright (c) 2002-2013";
static const char *const company   = "independIT Integrative Technologies GmbH";

static const int DEATH_DELAY_SECONDS = 2;

extern const char *itoa (const int i)
{
	static char buf [32];
	snprintf (buf, sizeof (buf), "%d", i);
	return buf;
}

static const char *as_gmt (const time_t tim)
{
	static char buf [32];
#ifndef SOLARIS
	const struct tm *timeval = gmtime (&tim);
#ifdef __GNUC__
	snprintf (buf, sizeof (buf), "%c%2.2d-%2.2d-%4.4d %2.2d:%2.2d:%2.2d GMT%c",
#else
	snprintf (buf, sizeof (buf), "%c%02.2d-%02.2d-%04.4d %02.2d:%02.2d:%02.2d GMT%c",
#endif
	          TIMESTAMP_LEADIN,
	          timeval->tm_mday,
	          timeval->tm_mon + 1,
	          timeval->tm_year + 1900,
	          timeval->tm_hour,
	          timeval->tm_min,
	          timeval->tm_sec,
	          TIMESTAMP_LEADOUT);

#else
	buf [0] = TIMESTAMP_LEADIN;
	const size_t len = strftime (buf + 1, sizeof (buf) - 3 * sizeof (char), "%d-%m-%Y %H:%M:%S GMT", gmtime (&tim));
	buf [len + 1] = TIMESTAMP_LEADOUT;
	buf [len + 2] = '\0';
#endif
	return buf;
}

static const char *as_localtime (const time_t tim)
{
	static char buf [256];
#ifndef SOLARIS
#ifdef WINDOOF
	_tzset();
#endif
	const struct tm *timeval = localtime (&tim);
#ifdef __GNUC__
	snprintf (buf, sizeof (buf), "%c%2.2d-%2.2d-%4.4d %2.2d:%2.2d:%2.2d %s%c",
#else
	snprintf (buf, sizeof (buf), "%c%02.2d-%02.2d-%04.4d %02.2d:%02.2d:%02.2d %s%c",
#endif
	          TIMESTAMP_LEADIN,
	          timeval->tm_mday,
	          timeval->tm_mon + 1,
	          timeval->tm_year + 1900,
	          timeval->tm_hour,
	          timeval->tm_min,
	          timeval->tm_sec,
#ifdef WINDOOF
	          _tzname[0],
#else
	          timeval->tm_zone,
#endif
	          TIMESTAMP_LEADOUT);
#else
	buf [0] = TIMESTAMP_LEADIN;
	size_t len = strftime (buf + 1, sizeof (buf) - 3 * sizeof (char), "%d-%m-%Y %H:%M:%S %Z", localtime (&tim));
	buf [len + 1] = TIMESTAMP_LEADOUT;
	buf [len + 2] = '\0';
#endif
	return buf;
}

extern const char *timestamp (void)
{
	return as_gmt (time (NULL));
}

LIBEXPORT const char *const get_progvers (void)
{
	return VERSION " (compiled " __DATE__ ")";
}

LIBEXPORT const char *const get_copyright (void)
{
	return copyright;
}

LIBEXPORT const char *const get_company (void)
{
	return company;
}

extern const char *errText (const char *const msg, const char *const val)
{
	const size_t siz = strlen (msg) - strlen ("%s") + strlen (val) + sizeof ('\0');

	char *const msgtxt = (char *const) malloc (siz);
	if (! msgtxt) {
		fprintf (stderr, "(04401202028) Out of memory\n");
		die();
	}

	snprintf (msgtxt, siz, msg, val);

	return msgtxt;
}

extern const char *errTextX (const char *const msg, const char *const errt, const int errn)
{
	const char *const errnstr = itoa (errn);
	const size_t siz = strlen (msg) + strlen (": (") + strlen (errnstr) + strlen (") %s") + sizeof ('\0');
	char *const msgtxt = (char *const) malloc (siz);
	if (! msgtxt) {
		fprintf (stderr, "(04401202029) Out of memory\n");
		die();
	}
	snprintf (msgtxt, siz, "%s: (%s) %%s", msg, errnstr);

	return errText (msgtxt, errt);
}

extern const char *errText (const char *const msg, const int errn)
{
	return errTextX (msg, strerror (errn), errn);
}

extern const char *errText (const char *const msg, const char *const val, const int errn)
{
	return errText (errText (msg, val), errn);
}

extern void die (void)
{
	sleep (DEATH_DELAY_SECONDS);
	exit (ERR_EXIT);
}

LIBEXPORT void die (const char *const msg)
{
	fprintf (stderr, "%s\n", msg);
	die();
}

extern void die (const char *const msg, const char *const val)
{
	die (errText (msg, val));
}

extern void die (const char *const msg, const int errn)
{
	die (errText (msg, errn));
}

LIBEXPORT void die (const char *const msg, const char *const val, const int errn)
{
	die (errText (msg, val, errn));
}

LIBEXPORT void giveup (const char *const status, const char *const msg)
{
	append_jobfile (FEIL_ERROR,  msg);
	append_jobfile (FEIL_STATUS, status);

	exit (0);
}

LIBEXPORT void giveup (const char *const status, const char *const msg, const char *const val)
{
	giveup (status, errText (msg, val));
}

LIBEXPORT void giveup (const char *const status, const char *const msg, const int errn)
{
	giveup (status, errText (msg, errn));
}

LIBEXPORT void giveup (const char *const status, const char *const msg, const char *const val, const int errn)
{
	giveup (status, errText (msg, val, errn));
}

static void verbose_print (const char *const txt, FILE *const fil, const char *const fil_nam, const bool child_error)
{
	if (fprintf (fil, "%s %s\n", as_localtime (time (NULL)), txt) <= 0) {
		if (child_error)
			exit (0);
		else
			giveup (FEIL_STATUS_ERROR, "(04306270001) Cannot write %s", fil_nam, errno);
	}

	fflush (fil);
}

extern void verbose_print_started (void)
{
	const bool child_error = false;

	verbose_print ("started", stdout, job.logfile, child_error);
	if (! job.samelogs)
		verbose_print ("started", stderr, job.errlog, child_error);
}

LIBEXPORT void verbose_print_finished (const bool child_error, const int rc)
{
	const char *msg;
	if (child_error)
		msg = errText ("finished (%s)", FEIL_STATUS_CHILD_ERROR);
	else
		msg = errText ("finished (%s)", itoa (rc));

	verbose_print (msg, stdout, job.logfile, child_error);
	if (! job.samelogs)
		verbose_print (msg, stderr, job.errlog, child_error);
}
