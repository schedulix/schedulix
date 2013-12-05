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


#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <time.h>
#include <errno.h>

#define NULLDEVICE   "/dev/null"

#define ARGS_NOTREAD  -1
#define ARGS_RUN       0
#define ARGS_VERSION   1
#define ARGS_HELP      2

#define BOOTTIME_SYSTEM 'S'
#define BOOTTIME_FILE   'F'
#define BOOTTIME_NONE   'N'

#define TIMESTAMP_LEADIN  '['
#define TIMESTAMP_LEADOUT ']'

#define RETRY_OPEN_TIME 1
#define RETRY_LOCK_TIME 1

#define false (1 == 0)
#define true  (0 == 0)

#define STATUS_OK      0
#define SEVERITY_WARNING 1
#define SEVERITY_ERROR   2
#define SEVERITY_FATAL   3

typedef struct _callstatus {
	int severity;
	int msg;
	int syserror;
	char *msg2;
} callstatus;

char *message[] = {
#define NO_ERROR         0
	"No error",
#define INVALID_TASKFILE 1
	"Invalid taskfile (doesn't exist, isn't readable or writable)",
#define INVALID_FD       2
	"Invalid file descriptor (open() succeeded, fd invalid)",
#define TFWRITE_FAILED   3
	"Write to taskfile failed",
#define TFCLOSE_FAILED   4
	"Close of taskfile failed",
#define TF_EMPTY         5
	"Task file empty",
#define TFREAD_ERROR     6
	"Error on read (%d)",
#define TFSYNTAX_ERROR   7
	"Syntax error in taskfile (%s)",
#define OUT_OF_MEM       8
	"memory allocation failed",
#define WRONG_ARGS       9
	"Wrong number or type of arguments",
#define TF_INCOMPLETE   10
	"Taskfile seems to be incomplete",
#define FORK_FAILED     11
	"Couldn't create process; fork() failed",
#define LOGOPEN_FAILED  12
	"Couldn't open logfile (%d)",
#define CHDIR_FAILED    13
	"Couldn't chdir to working directory (%d)",
#define ERROPEN_FAILED  14
	"Couldn't open error logfile (%d)",
#define DUP_FAILED      15
	"Couldn't redirect stderr to stdout (%d)",
#define EXEC_FAILED     16
	"Couldn't execute command (%d)",
#define SEEK_FAILED     17
	"Couldn't seek to logical end of file (%d)",
#define WRITE_FAILED    18
	"Couldn't write into taskfile (%d)",
#define TFMISSING_VALUE 19
	"Mandatory value missing in taskfile (key %s)",
#define INVALID_BOOTTIME 20
	"Invalid boottime (too long)"
};

char *ARG_VERSION1 = "--version";
char *ARG_VERSION2 = "-v";
char *ARG_HELP1    = "--help";
char *ARG_HELP2    = "-h";

int argsType = ARGS_NOTREAD;

unsigned char boottimeHow = 'N';
char *taskfileName;
char *boottime;

time_t myStartTime;

#define TF_BUFSIZE 16384
#define MAXLENGTH   8192
char taskfileBuf[TF_BUFSIZE];

char *COMMAND       = "command";
char *ARGUMENT      = "argument";
char *WORKDIR       = "workdir";
char *USEPATH       = "usepath";
char *VERBOSELOGS   = "verboselogs";
char *LOGFILE       = "logfile";
char *LOGFILEAPPEND = "logfile_append";
char *ERRLOG        = "errlog";
char *ERRLOGAPPEND  = "errlog_append";
char *SAMELOGS      = "samelogs";
char *EXECPID       = "execpid";
char *EXTPID        = "extpid";
char *RETURNCODE    = "returncode";
char *ERROR         = "error";
char *STATUS        = "status";
char *STATUS_TX     = "status_tx";
char *INCOMPLETE    = "incomplete";
char *COMPLETE      = "complete";

char *STATUS_RUNNING     = "RUNNING";
char *STATUS_FINISHED    = "FINISHED";
char *STATUS_ERROR       = "ERROR";
char *STATUS_CHILD_ERROR = "CHILD_ERROR";

char *command;
char **argument;
int num_args;
int argsize;
char *workdir;
int  usepath;
int  verboselogs;
char *logfile;
int  logfileappend;
char *errlog;
int  errlogappend;
int  samelogs;
char *execpid;
char *extpid;
char *returncode;
char *error;
char *jstatus;
char *jstatus_tx;
int  complete;

FILE *taskfile;
int buflen;
int bufpos;
int filepos;

char *getUsage();
char *getVersion();
void initFields();
int checkArgs(callstatus *status, int argc, char *argv[]);
FILE *openTaskfile(callstatus *status);
void closeTaskfile(callstatus *status, FILE *taskfile);
void advance(callstatus *status);
void readTimestamp(callstatus *status);
void readWhiteSpace(callstatus *status);
void readKey(callstatus *status, char *key);
void readLength(callstatus *status, char *lgth);
void readValue(callstatus *status, int lgth, char *value);
void processTaskfile(callstatus *status);
void evaluateTaskfile(callstatus *status);
void appendTaskfile(callstatus *status, char *key, char *value);
void redirect(callstatus *status);
char *getUniquePid(callstatus *status, pid_t pid);
void run(callstatus *status);
void printJobFields();

char *getUsage()
{

	return  "Usage:\n" \
	        "Jexecutor [--version|-v] [--help|-h] [<boottime_how> <taskfileName> [boottime]]\n" \
	        "\n" \
	        "Exactly one of the optional argument sets must be specified\n" \
	        "i.e. either the version request, or this help request or some specification on what to do\n";
}

char *getVersion()
{

	return  "Jobserver (executor) 2.6\n" \
	        "Copyright (C) 2013 independIT Integrative Technologies GmbH\n" \
	        "All rights reserved\n";
}

void initFields()
{
	command = NULL;
	argument = NULL;
	num_args = 0;
	argsize = 0;
	workdir = NULL;
	usepath = false;
	verboselogs = false;
	logfile = NULL;
	logfileappend = false;
	errlog = NULL;
	errlogappend = false;
	samelogs = false;
	execpid = NULL;
	extpid = NULL;
	returncode = NULL;
	error = NULL;
	jstatus = NULL;
	jstatus_tx = NULL;
	complete = false;

	taskfile = NULL;
	buflen = 0;
	bufpos = 0;
	filepos = -1;
}

char *Strdup(callstatus *status, char *src)
{
	char *trg;

	if (*src == '\0') {
		status->severity = SEVERITY_FATAL;
		status->msg = TFMISSING_VALUE;
		return;
	}
	trg = strdup(src);
	if (trg == NULL) {
		status->severity = SEVERITY_FATAL;
		status->msg = OUT_OF_MEM;
	}
	return trg;
}

void printJobFields()
{
	int i;

	fprintf(stdout, "command       = %s\n", command != NULL ? command : "NULL");
	fprintf(stdout, "arguments:\n");
	if (argument == NULL)
		fprintf(stdout, "\tNULL\n");
	else {
		for (i = 0; i < num_args; ++i)
			fprintf(stdout, "\t%2.2d: %s\n", i, argument[i]);
	}
	fprintf(stdout, "workdir       = %s\n", workdir != NULL ? workdir : "NULL");
	fprintf(stdout, "usepath       = %s\n", usepath == false ? "false" : "true");
	fprintf(stdout, "verboselogs   = %s\n", verboselogs == false ? "false" : "true");
	fprintf(stdout, "logfile       = %s\n", logfile != NULL ? logfile : "NULL");
	fprintf(stdout, "logfileappend = %s\n", logfileappend == false ? "false" : "true");
	fprintf(stdout, "errlog        = %s\n", errlog != NULL ? errlog : "NULL");
	fprintf(stdout, "errlogappend  = %s\n", errlogappend == false ? "false" : "true");
	fprintf(stdout, "samelogs      = %s\n", samelogs == false ? "false" : "true");
	fprintf(stdout, "execpid       = %s\n", execpid != NULL ? execpid : "NULL");
	fprintf(stdout, "extpid        = %s\n", extpid != NULL ? extpid : "NULL");
	fprintf(stdout, "returncode    = %s\n", returncode != NULL ? returncode : "NULL");
	fprintf(stdout, "error         = %s\n", error != NULL ? error : "NULL");
	fprintf(stdout, "jstatus       = %s\n", jstatus != NULL ? jstatus : "NULL");
	fprintf(stdout, "jstatus_tx    = %s\n", jstatus_tx != NULL ? jstatus : "NULL");
	fprintf(stdout, "complete      = %s\n", complete == false ? "false" : "true");
}

void addArgument(callstatus *status, char *value)
{
#define ARGCHUNK 32
	int i;

	if (argument == NULL) {
		argument = (char **) malloc(ARGCHUNK * sizeof(char *));
		if (argument == NULL) {
			status->severity = SEVERITY_FATAL;
			status->msg = OUT_OF_MEM;
			return;
		}
		argsize = ARGCHUNK;
		for (i = num_args; i < argsize; ++i) argument[i] = NULL;
	} else {
		if (num_args == argsize) {
			argument = (char **) realloc(argument, (argsize + ARGCHUNK) * sizeof(char*));
			if (argument == NULL) {
				status->severity = SEVERITY_FATAL;
				status->msg = OUT_OF_MEM;
				return;
			}
			argsize += ARGCHUNK;

			for (i = num_args; i < argsize; ++i) argument[i] = NULL;
		}
	}
	argument[num_args] = Strdup(status, value);
	if (status->severity == STATUS_OK)
		num_args++;

	return;
}

int checkArgs(callstatus *status, int argc, char *argv[])
{
	status->severity = STATUS_OK;
	status->msg = NO_ERROR;

	if (argc <= 4 && argc > 1) {
		if (!strcmp(argv[1], ARG_VERSION1) || !strcmp(argv[1], ARG_VERSION2)) {
			argsType = ARGS_VERSION;
			return ARGS_VERSION;
		}
		if (!strcmp(argv[1], ARG_HELP1) || !strcmp(argv[1], ARG_HELP2)) {
			argsType = ARGS_HELP;
			return ARGS_HELP;
		}
		if (argc < 3) {
			status->severity = SEVERITY_FATAL;
			status->msg = WRONG_ARGS;
			return ARGS_NOTREAD;
		}

		boottimeHow = toupper((unsigned char) argv[1][0]);
		if (!(boottimeHow == BOOTTIME_SYSTEM ||
		      boottimeHow == BOOTTIME_FILE   ||
		      boottimeHow == BOOTTIME_NONE)) {
			status->severity = SEVERITY_FATAL;
			status->msg = WRONG_ARGS;
			return ARGS_NOTREAD;
		}

		taskfileName = argv[2];

		if (argc == 4) {
			boottime = argv[3];
			if (strlen(boottime) > 19) {
				status->severity = SEVERITY_FATAL;
				status->msg = INVALID_BOOTTIME;
				return ARGS_NOTREAD;
			}
		} else
			boottime = NULL;

		processTaskfile(status);
		if (status->severity != STATUS_OK) {
			return ARGS_NOTREAD;
		}

		if (!complete) {
			status->severity = SEVERITY_FATAL;
			status->msg = TF_INCOMPLETE;
			return ARGS_NOTREAD;
		}

		return ARGS_RUN;
	}
	status->severity = SEVERITY_FATAL;
	status->msg = WRONG_ARGS;
	return ARGS_NOTREAD;
}

FILE *openTaskfile(callstatus *status)
{
	FILE *taskfile = NULL;
	int retry_cnt = 0;
	struct flock lock;
	int tffd;

	while (1) {
		taskfile = fopen(taskfileName, "r+");

		if (taskfile != NULL) break;

		if (errno == ENOENT ||
		    errno == EACCES ||
		    errno == EISDIR ||
		    errno == ELOOP
		   ) {
			status->severity = SEVERITY_FATAL;
			status->msg = INVALID_TASKFILE;
			return NULL;
		}

		sleep(RETRY_OPEN_TIME + retry_cnt);
		retry_cnt++;
	}

	retry_cnt = 0;
	tffd = fileno(taskfile);
	lock.l_type = F_WRLCK;
	lock.l_whence = SEEK_SET;
	lock.l_start = 0;
	lock.l_len = 0;
	while (1) {
		if (fcntl(tffd, F_SETLKW, &lock) == 0) break;

		if (errno == EBADF) {
			status->severity = SEVERITY_FATAL;
			status->msg = INVALID_FD;
			return NULL;
		}

		sleep(RETRY_LOCK_TIME + retry_cnt);
		retry_cnt++;
	}

	return taskfile;
}

void closeTaskfile(callstatus *status, FILE *taskfile)
{
	if (taskfile != NULL) {
		if (fsync(fileno(taskfile)) != 0) {
			status->severity = SEVERITY_FATAL;
			status->msg = TFWRITE_FAILED;
			return;
		}
		if (fclose(taskfile) != 0) {
			status->severity = SEVERITY_WARNING;
			status->msg = TFCLOSE_FAILED;
		}
	}

	filepos = -1;
	bufpos = 0;
	buflen = 0;

	return;
}

void advance(callstatus *status)
{
	if (bufpos == -1) return;

	if (filepos == -1 || bufpos == buflen) {

		if ((buflen = fread(taskfileBuf, sizeof(char), TF_BUFSIZE, taskfile)) == 0) {
			if (feof(taskfile)) {

				if (filepos == -1) {
					status->severity = SEVERITY_FATAL;
					status->msg = TF_EMPTY;
					return;
				}
				bufpos = -1;
				return;
			} else {

				status->severity = SEVERITY_FATAL;
				status->msg = TFREAD_ERROR;
				status->syserror = ferror(taskfile);
				return;
			}
		}

		bufpos = 0;
		if (filepos == -1)
			filepos = buflen;
		else
			filepos += buflen;
	} else
		bufpos++;

	return;
}

void readTimestamp(callstatus *status)
{

	while (bufpos >= 0 && taskfileBuf[bufpos] != TIMESTAMP_LEADOUT) {
		advance(status);
		if (status->severity != STATUS_OK) return;
	}

	advance(status);
	if (status->severity != STATUS_OK) return;

	return;
}

void readWhiteSpace(callstatus *status)
{
	while (bufpos >= 0 && isblank(taskfileBuf[bufpos])) {
		advance(status);
		if (status->severity != STATUS_OK) return;
	}

	return;
}

void readKey(callstatus *status, char *key)
{
	int i = 0;

	while (bufpos >= 0 && i < MAXLENGTH && !isspace(taskfileBuf[bufpos]) && taskfileBuf[bufpos] != '\'' && taskfileBuf[bufpos] != '=') {
		*(key + i) = tolower(taskfileBuf[bufpos]);
		advance(status);
		if (status->severity != STATUS_OK) return;
		i++;
	}

	return;
}

void readLength(callstatus *status, char *lgth)
{
	int i = 0;

	while (bufpos >= 0 && i < MAXLENGTH && isdigit(taskfileBuf[bufpos])) {
		*(lgth + i) = taskfileBuf[bufpos];
		advance(status);
		if (status->severity != STATUS_OK) return;
		i++;
	}

	return;
}

void readValue(callstatus *status, int lgth, char *value)
{
	int i = 0;

	while (bufpos >= 0 && i < MAXLENGTH && (i < lgth || (lgth < 0 && taskfileBuf[bufpos] != '\n'))) {
		*(value + i) = taskfileBuf[bufpos];
		advance(status);
		if (status->severity != STATUS_OK) return;
		i++;
	}

	return;
}

void processEntry(callstatus *status)
{
	static char key[MAXLENGTH];
	static char vlength[MAXLENGTH];
	int length;
	static char value[MAXLENGTH];
	callstatus mystatus;

	/* reset contents */
	memset(&(key[0]), 0, MAXLENGTH);
	memset(&(vlength[0]), 0, MAXLENGTH);
	memset(&(value[0]), 0, MAXLENGTH);

	readTimestamp(status);
	if (status->severity != STATUS_OK) return;

	readWhiteSpace(status);
	if (status->severity != STATUS_OK) return;

	readKey(status, &(key[0]));
	if (status->severity != STATUS_OK) return;

	readWhiteSpace(status);
	if (status->severity != STATUS_OK) return;

	switch (taskfileBuf[bufpos]) {
	case '\n':
		/* Entry is ready */
		advance(status);	/* skip the newline */
		if (status->severity != STATUS_OK) return;
		/* we accept an EOF instead of a '\n' */
		break;
	case '\'':
		advance(status);
		if (status->severity != STATUS_OK) return;
		if (bufpos < 0) {
			status->severity = SEVERITY_FATAL;
			status->msg = TFSYNTAX_ERROR;
			status->msg2 = "Unexpected EOF, expected a number";
			return;
		}

		readWhiteSpace(status);
		if (status->severity != STATUS_OK) return;

		readLength(status, &(vlength[0]));
		if (status->severity != STATUS_OK) return;
		length = atoi(vlength);

		readWhiteSpace(status);
		if (status->severity != STATUS_OK) return;

		if (taskfileBuf[bufpos] != '=') {
			status->severity = SEVERITY_FATAL;
			status->msg = TFSYNTAX_ERROR;
			status->msg2 = "Unexpected token, expected an equal sign";
			return;
		}
		advance(status);
		if (status->severity != STATUS_OK) return;
		if (bufpos < 1) {
			status->severity = SEVERITY_FATAL;
			status->msg = TFSYNTAX_ERROR;
			status->msg2 = "Unexpected EOF, expected a value";
			return;
		}

		readValue(status, length, &(value[0]));
		if (status->severity != STATUS_OK) return;

		advance(status);
		if (status->severity != STATUS_OK) return;

		break;
	case '=':
		advance(status);
		if (status->severity != STATUS_OK) return;
		if (bufpos < 1) {
			status->severity = SEVERITY_FATAL;
			status->msg = TFSYNTAX_ERROR;
			status->msg2 = "Unexpected EOF, expected a value";
			return;
		}

		readValue(status, -1, &(value[0]));
		if (status->severity != STATUS_OK) return;

		advance(status);
		if (status->severity != STATUS_OK) return;

		break;
	default:

		status->severity = SEVERITY_FATAL;
		status->msg = TFSYNTAX_ERROR;
		status->msg2 = "Unexpected Value, expected an equal sign, a quote or a newline";
		return;
	}

	if (!strcmp(key, INCOMPLETE))		complete = false;
	else if (!strcmp(key, COMMAND))		command = Strdup(status, value);
	else if (!strcmp(key, ARGUMENT))	addArgument(status, value);
	else if (!strcmp(key, WORKDIR))		workdir = Strdup(status, value);
	else if (!strcmp(key, USEPATH))		usepath = true;
	else if (!strcmp(key, VERBOSELOGS))	verboselogs = true;
	else if (!strcmp(key, LOGFILE))		logfile = Strdup(status, value);
	else if (!strcmp(key, LOGFILEAPPEND))	logfileappend = true;
	else if (!strcmp(key, ERRLOG))		errlog = Strdup(status, value);
	else if (!strcmp(key, ERRLOGAPPEND))	errlogappend = true;
	else if (!strcmp(key, SAMELOGS))	samelogs = true;
	else if (!strcmp(key, EXECPID))		execpid = Strdup(status, value);
	else if (!strcmp(key, EXTPID))		extpid = Strdup(status, value);
	else if (!strcmp(key, RETURNCODE))	returncode = Strdup(status, value);
	else if (!strcmp(key, ERROR))		error = Strdup(status, value);
	else if (!strcmp(key, STATUS))		jstatus = Strdup(status, value);
	else if (!strcmp(key, STATUS_TX))	jstatus_tx = Strdup(status, value);
	else if (!strcmp(key, COMPLETE))	complete = true;

	if (status->msg == TFMISSING_VALUE) {

		status->msg2 = Strdup(&mystatus, key);
	}

	return;
}

void evaluateTaskfile(callstatus *status)
{
	advance(status);
	if (status->severity != STATUS_OK) return;

	while (bufpos >= 0 && taskfileBuf[bufpos] != '\0') {
		processEntry(status);
		if (status->severity != STATUS_OK) return;
	}

	return;
}

void processTaskfile(callstatus *status)
{
	taskfile = openTaskfile(status);
	if (status->severity != STATUS_OK) return;

	evaluateTaskfile(status);
	if (status->severity != STATUS_OK) return;

	closeTaskfile(status, taskfile);
	if (status->severity != STATUS_OK) return;

	return;
}

char *getTimestamp(time_t tim)
{
	static char buf [32];
#ifndef SOLARIS
	const struct tm *timeval = gmtime (&tim);
#ifdef __GNUC__
	snprintf (buf, sizeof (buf), "%c%2.2d-%2.2d-%4.4d %2.2d:%2.2d:%2.2d GMT%c",
	          TIMESTAMP_LEADIN,
	          timeval->tm_mday,
	          timeval->tm_mon + 1,
	          timeval->tm_year + 1900,
	          timeval->tm_hour,
	          timeval->tm_min,
	          timeval->tm_sec,
	          TIMESTAMP_LEADOUT);
#else
	snprintf (buf, sizeof (buf), "%c%02.2d-%02.2d-%04.4d %02.2d:%02.2d:%02.2d GMT%c",
	          TIMESTAMP_LEADIN,
	          timeval->tm_mday,
	          timeval->tm_mon + 1,
	          timeval->tm_year + 1900,
	          timeval->tm_hour,
	          timeval->tm_min,
	          timeval->tm_sec,
	          TIMESTAMP_LEADOUT);
#endif

#else
	buf [0] = TIMESTAMP_LEADIN;
	const size_t len = strftime (buf + 1, sizeof (buf) - 3 * sizeof (char), "%d-%m-%Y %H:%M:%S GMT", gmtime (&tim));
	buf [len + 1] = TIMESTAMP_LEADOUT;
	buf [len + 2] = '\0';
#endif
	return buf;
}

void appendTaskfile(callstatus *status, char *key, char *value)
{
	taskfile = openTaskfile(status);
	if (status->severity != STATUS_OK) return;

	advance(status);
	if (status->severity != STATUS_OK) return;

	while (bufpos >= 0 && taskfileBuf[bufpos] != '\0') {
		advance(status);
		if (status->severity != STATUS_OK) return;
	}

	if (fseek(taskfile, filepos - buflen + bufpos, SEEK_SET) < 0) {
		status->severity = SEVERITY_FATAL;
		status->msg = SEEK_FAILED;
		status->syserror = errno;
		return;
	}

	if (fprintf(taskfile, "%s %s=%s\n", getTimestamp(time(NULL)), key, value) < 0) {
		status->severity = SEVERITY_FATAL;
		status->msg = WRITE_FAILED;
		status->syserror = errno;
		return;
	}

	closeTaskfile(status, taskfile);
	if (status->severity != STATUS_OK) return;

	return;
}

void redirect(callstatus *status)
{
	if (workdir != NULL) {
		if (chdir(workdir) < 0) {
			status->severity = SEVERITY_FATAL;
			status->msg = CHDIR_FAILED;
			status->syserror = errno;
			return;
		}
	}
	if (freopen(logfile == NULL ? NULLDEVICE : logfile , logfileappend ? "a" : "w", stdout) < 0) {
		status->severity = SEVERITY_FATAL;
		status->msg = LOGOPEN_FAILED;
		status->syserror = errno;
		return;
	}
	if (!samelogs) {
		if (freopen(errlog == NULL ? NULLDEVICE : errlog , errlogappend ? "a" : "w", stderr) < 0) {
			status->severity = SEVERITY_FATAL;
			status->msg = ERROPEN_FAILED;
			status->syserror = errno;
			return;
		}
	} else {
		if (dup2(fileno(stdout), fileno(stderr)) < 0) {
			status->severity = SEVERITY_FATAL;
			status->msg = DUP_FAILED;
			status->syserror = errno;
			return;
		}
	}
}

char *getUniquePid(callstatus *status, pid_t pid)
{
	static char buf[64];

	static char *pattern = "%d@%c%s+%ld";
	snprintf(buf, 63, pattern, (int) pid, boottimeHow, (boottime == NULL ? "0" : boottime), myStartTime);

	return buf;
}

void run(callstatus *status)
{
	char **argv;
	int i;
	pid_t cpid;
	int   exitcode;
	char  *execpid;
	char  buf[20];

	execpid = getUniquePid(status, getpid());
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, EXECPID, execpid);
	if (status->severity != STATUS_OK) return;

	argv = (char **) malloc((1 + num_args + 1) * sizeof(char*));
	if (argv == NULL) {
		status->severity = SEVERITY_FATAL;
		status->msg = OUT_OF_MEM;
		return;
	}
	argv[0] = command;
	for (i = 0; i < num_args; ++i) {
		argv[i+1] = argument[i];
	}
	argv[i + 1] = NULL;

	redirect(status);
	if (status->severity != STATUS_OK) return;

	cpid = fork();

	if (cpid < 0) {

		status->severity = SEVERITY_FATAL;
		status->msg = FORK_FAILED;
		status->syserror = errno;
		return;
	}
	if (cpid > 0) {

		waitpid(cpid, &exitcode, 0);
		if (verboselogs) {
			fprintf(stdout, "------- %s End (%d) --------\n", getTimestamp(time(NULL)), exitcode);
		}
		snprintf(buf, 20, "%d", exitcode);
		appendTaskfile(status, STATUS, STATUS_FINISHED);
		if (status->severity != STATUS_OK) return;
		appendTaskfile(status, RETURNCODE, buf);
		if (status->severity != STATUS_OK) return;
		return;
	}

	myStartTime = time(NULL);
	extpid = getUniquePid(status, getpid());
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, EXTPID, extpid);
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, STATUS, STATUS_RUNNING);
	if (status->severity != STATUS_OK) return;

	if (setsid() == -1) {

	}

	if (verboselogs) {
		fprintf(stdout, "------- %s Start --------\n", getTimestamp(time(NULL)));
		fflush(stdout);
	}

	if (usepath) {
		execvp(command, argv);
	} else {
		execv(command, argv);
	}

	status->severity = SEVERITY_FATAL;
	status->msg = EXEC_FAILED;
	status->syserror = errno;
	return;
}

int main(int argc, char *argv[])
{
	int argsType;
	callstatus status;

	myStartTime = time(NULL);

	initFields();
	status.severity = STATUS_OK;
	status.msg = NO_ERROR;

	argsType = checkArgs(&status, argc, argv);

	switch (argsType) {
	case ARGS_NOTREAD:

		fprintf(stderr, "%s\n", message[status.msg]);
		fprintf(stderr, "%s", getUsage());
		exit(1);
	case ARGS_VERSION:
		fprintf(stdout, "%s", getVersion());
		exit(0);
	case ARGS_HELP:
		fprintf(stdout, "%s", getUsage());
		exit(0);
	case ARGS_RUN:
		run(&status);
		if (status.severity != STATUS_OK) {

			fprintf(stderr, "%s\n", message[status.msg]);
		}
		break;
	default:

		fprintf(stderr, "Argument evaluation returned an unexpected value : %d\n", argsType);
		fprintf(stderr, "%s", getUsage());
		exit(1);
	}

	return status.severity;
}
