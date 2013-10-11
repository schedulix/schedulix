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


#include <locale.h>

#include "libjobserver.h"

static const char *const BIC_LOCALE     = "BIC_LOCALE";
static const char *const DEFAULT_LOCALE = "en_GB";

static char bt_how;

static void init_runenv (void)
{
	ignore_all_signals();

#ifdef SOLARIS
	const char *lc = getenv (BIC_LOCALE);
	if (! lc)
		lc = DEFAULT_LOCALE;
	if (! setlocale (LC_TIME, lc))
		die ("(04303271531) Cannot set locale %s", lc, errno);
#endif
}

static void eval_args (const int argc, const char *const *const argv)
{
	if ((argc == 2) && ! strcmp (argv [1], "--version")) {
		printf ("Jobserver (executor) %s\n", get_progvers());
		printf ("%s %s\n", get_copyright(), get_company());
		printf ("All rights reserved\n");
		exit (ERR_EXIT);
	}

	if (argc != 3)
		die ("(04301271516) Invalid number of arguments");

	bt_how     = argv [1] [0];
	job.filnam = argv [2];
}

static char *const next_jobfile_entry (char *buf, char* *const key, char* *const value)
{
	*key   = NULL;
	*value = NULL;

	buf = strchr (buf, TIMESTAMP_LEADOUT);
	if (! buf)
		return NULL;

	do ++buf;
	while (*buf == ' ');
	*key = buf;

	size_t len_key = strcspn (buf, "=\n\r");

	if (buf [len_key] != '=')
		buf += len_key;
	else {
		buf += len_key;
		*buf = '\0';
		++buf;
		*value = buf;

		char *const qu = strchr (*key, '\'');	//	locate '\'' inside key
		if (! qu)
			buf += strcspn (buf, "\n\r");
		else {
			*qu = '\0';
			const int len = atoi (qu + 1);
			buf += len;
		}
	}

	*buf = '\0';
	return buf + 1;
}

static void scan_jobfile_data (void)
{
	FILEHANDLE fil = open_jobfile();
	char *buf;
	const bool ok = read_jobfile (fil, &buf);
	close_jobfile (fil);
	if (! ok)
		die (last_error);

	int argc = 0;
	char **argv = NULL;

	char *key, *value;
	while ((buf = next_jobfile_entry (buf, &key, &value)) != NULL)
		if      (! strcasecmp (key, FEIL_USEPATH))       job.usepath     = true;
		else if (! strcasecmp (key, FEIL_VERBOSELOGS))   job.verboselogs = true;
		else if (! strcasecmp (key, FEIL_LOGFILEAPPEND)) job.logfileapp  = true;
		else if (! strcasecmp (key, FEIL_ERRLOGAPPEND))  job.errlogapp   = true;
		else if (! strcasecmp (key, FEIL_SAMELOGS))      job.samelogs    = true;

		else if (! strcasecmp (key, FEIL_COMMAND)) job.command = value;
		else if (! strcasecmp (key, FEIL_WORKDIR)) job.workdir = value;
		else if (! strcasecmp (key, FEIL_LOGFILE)) job.logfile = value;
		else if (! strcasecmp (key, FEIL_ERRLOG))  job.errlog  = value;

		else if (! strcasecmp (key, FEIL_ARGUMENT)) {
			++argc;
			argv = (char **) realloc ((void *) argv, argc * sizeof (char *));
			argv [argc - 1] = value;
		}

	if (! job.command)
		giveup (FEIL_STATUS_ERROR, "(04301271534) Missing command in %s", job.filnam);
	if (! job.workdir)
		giveup (FEIL_STATUS_ERROR, "(04301271535) Missing workdir in %s", job.filnam);
	if (! (job.logfile && *job.logfile))
		job.logfile = DEV_NULL;
	if (! (job.errlog && *job.errlog))
		job.errlog = DEV_NULL;

	job.args = (char **) realloc ((void *) job.args, (argc + 2) * sizeof (char *));
	if (! job.args)
		giveup (FEIL_STATUS_ERROR, "(04301271536) Out of memory");
	job.args [0] = (char *) malloc (strlen (job.command) + sizeof ('\0'));
	if (! job.args [0])
		giveup (FEIL_STATUS_ERROR, "(04301271537) Out of memory");
	strcpy (job.args [0], job.command);
	for (int i = 0; i < argc; ++i)
		job.args [i + 1] = argv [i];
	job.args [argc + 1] = NULL;
}

static const char *scan_jobfile_status (void)
{
	const char *status = "";

	FILEHANDLE fil = open_jobfile();
	char *buf;
	if (read_jobfile (fil, &buf)) {
		char *key, *value;
		while ((buf = next_jobfile_entry (buf, &key, &value)) != NULL)
			if (! strcasecmp (key, FEIL_STATUS))
				status = value;
	}

	close_jobfile (fil);

	return status;
}

static void redirect_stdx()
{
#ifdef WINDOOF
#define APPENDFLAG "a+"
#define WRITEFLAG  "w"
#else
#define APPENDFLAG "a"
#define WRITEFLAG  "w"
#endif
	if (! freopen (job.logfile, job.logfileapp ? APPENDFLAG : WRITEFLAG, stdout))
		giveup (FEIL_STATUS_ERROR, "(04301271539) Cannot open log %s", job.logfile, errno);
#ifdef WINDOOF

	if (job.logfileapp) fseek(stdout, 0, SEEK_END);
#endif

	if (job.samelogs) {
		if (dup2 (fileno (stdout), fileno (stderr)) == -1)
			giveup (FEIL_STATUS_ERROR, "(04301271540) Cannot dup2() stdout to stderr", errno);
	} else {
		if (! freopen (job.errlog, job.errlogapp ? APPENDFLAG : WRITEFLAG, stderr))
			giveup (FEIL_STATUS_ERROR, "(04301271541) Cannot open errlog %s", job.errlog, errno);
#ifdef WINDOOF
		if (job.errlogapp) fseek(stderr, 0, SEEK_END);
#endif
	}
}

int main (const int argc, const char *const argv[])
{
	eval_args (argc, argv);
	init_runenv();
	scan_jobfile_data();

	if (! set_boottime_how (bt_how))
		giveup (FEIL_STATUS_ERROR, "(04307111809) %s", last_error);

	append_jobfile (FEIL_EXECPID, get_unique_pid (getpid()));

	if (chdir (job.workdir) == -1)
		giveup (FEIL_STATUS_ERROR, "(04301271538) Cannot chdir() to %s", job.workdir, errno);

	redirect_stdx();

	int rc;
	if (! execute_child (&rc))
		giveup (FEIL_STATUS_ERROR, last_error);

	const bool child_error = ! strcasecmp (scan_jobfile_status(), FEIL_STATUS_CHILD_ERROR);
	if (! child_error) {
		append_jobfile (FEIL_RETURNCODE, rc);
		append_jobfile (FEIL_STATUS,     FEIL_STATUS_FINISHED);
	}

	if (job.verboselogs)
		verbose_print_finished (child_error, rc);

	return 0;
}
