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
#include <limits.h>
#include <signal.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <sys/types.h>
#ifdef WINDOWS
#include <windows.h>
#endif

#ifndef WINDOWS
#define NULLDEVICE   "/dev/null"
#define SIGNUM_MIN SIGHUP
#define SIGNUM_MAX SIGSYS
#else
#define NULLDEVICE   "NUL"
#endif

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

#define JOBEXECUTORLOG  "JOBEXECUTORLOG"

#define false (1 == 0)
#define true  (0 == 0)

#ifdef WINDOWS
#define sleep(a)           Sleep((a) * 1000)
#define Fread(a,b,c,d,e)   (ReadFile((d), (a), (b) * (c), (&e), NULL), e)
#define Fwrite(a,b,c,d,e)  (WriteFile((d), (a), (b) * (c), (&e), NULL), e)
#define Fseek(a,b,c)       SetFilePointer((a), (b), NULL, (c))
#define Fflush(a)          FlushFileBuffers((a))
#define Fclose(a)          CloseHandle(a)
#else
#define FILE_BEGIN         SEEK_SET
#define FILE_CURRENT	   SEEK_CUR
#define FILE_END           SEEK_END
#define Fread(a,b,c,d,e)   fread((a), (b), (c), (d))
#define Fwrite(a,b,c,d,e)  fwrite((a), (b), (c), (d))
#define Fseek(a, b, c)     fseek((a), (b), (c))
#define Fflush(a)          fflush((a))
#define Fclose(a)          fclose(a)
#endif

#define STATUS_OK      0
#define SEVERITY_WARNING 1
#define SEVERITY_FATAL   3

typedef struct _callstatus {
	int severity;
	int msg;
	int syserror;
	char *msg2;
} callstatus;

char *message[] = {
#define MSG_NO_ERROR     0
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
	"Invalid boottime (too long)",
#define TFUNLOCK_FAILED 21
	"Couldn't release taskfile lock",
#define CHLDWAIT_FAILED	22
	"Wait for child process failed",
#define SET_SIGNAL_FAILED 23
	"Set signal handler failed"
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
char tfWriteBuf[TF_BUFSIZE];

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
char *S_ERROR       = "error";
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

#ifndef WINDOWS
int buflen;
#define HANDLE FILE*
#else
DWORD buflen;
#endif
int bufpos;
int filepos;
HANDLE taskfile;
HANDLE myLog = NULL;

char *getUsage();
char *getVersion();
void initFields();
int checkArgs(callstatus *status, int argc, char *argv[]);
HANDLE openTaskfile(callstatus *status);
void closeTaskfile(callstatus *status, HANDLE taskfile);
void advance(callstatus *status);
void readTimestamp(callstatus *status);
void readWhiteSpace(callstatus *status);
void readKey(callstatus *status, char *key);
void readLength(callstatus *status, char *lgth);
void readValue(callstatus *status, int lgth, char *value);
void processTaskfile(callstatus *status);
void evaluateTaskfile(callstatus *status);
void appendTaskfile(callstatus *status, char *key, char *value, int alreadyOpen);
void redirect(callstatus *status);
void openLog(callstatus *status);
char *getUniquePid(callstatus *status, pid_t pid);
void run(callstatus *status);
void printJobFields();
void default_all_signals(callstatus *status);
void ignore_all_signals(callstatus *status);
#ifndef WINDOWS
void set_all_signals(struct sigaction aktschn, callstatus *status);
#else
void set_all_signals (void (__cdecl *aktschn) (int), callstatus *status);
#endif
char *Strdup(callstatus *status, char *src);

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

#ifndef WINDOWS
void set_all_signals (struct sigaction aktschn, callstatus *status)
{
	int rc;
	int signum;

	for (signum = SIGNUM_MIN; signum <= SIGNUM_MAX; ++signum) {
		if ((signum != SIGKILL) && (signum != SIGSTOP) && (signum != SIGCHLD)) {
			rc = sigaction (signum, &aktschn, NULL);
			if (rc) {
				status->severity = SEVERITY_WARNING;
				status->msg = SET_SIGNAL_FAILED;
			}
		}
	}
}

void ignore_all_signals (callstatus *status)
{
	struct sigaction aktschn;

#ifdef NETBSD
	sigemptyset (&aktschn.sa_mask);

	aktschn.sa_flags   = 0;
#endif
	aktschn.sa_handler = SIG_IGN;

	set_all_signals (aktschn, status);
}

void default_all_signals (callstatus *status)
{
	struct sigaction aktschn;
#ifdef NETBSD
	sigemptyset (&aktschn.sa_mask);

	aktschn.sa_flags   = 0;
#endif
	aktschn.sa_handler = SIG_DFL;

	set_all_signals (aktschn, status);
}
#else
void set_all_signals (void (__cdecl *aktschn) (int), callstatus *status)
{
	if (signal (SIGABRT, aktschn) == SIG_ERR) {
		status->severity = SEVERITY_WARNING;
		status->msg = SET_SIGNAL_FAILED;
	}
	if (signal (SIGFPE, aktschn) == SIG_ERR) {
		status->severity = SEVERITY_WARNING;
		status->msg = SET_SIGNAL_FAILED;
	}
	if (signal (SIGILL, aktschn) == SIG_ERR) {
		status->severity = SEVERITY_WARNING;
		status->msg = SET_SIGNAL_FAILED;
	}
	if (signal (SIGINT, aktschn) == SIG_ERR) {
		status->severity = SEVERITY_WARNING;
		status->msg = SET_SIGNAL_FAILED;
	}
	if (signal (SIGSEGV, aktschn) == SIG_ERR) {
		status->severity = SEVERITY_WARNING;
		status->msg = SET_SIGNAL_FAILED;
	}
	if (signal (SIGTERM, aktschn) == SIG_ERR) {
		status->severity = SEVERITY_WARNING;
		status->msg = SET_SIGNAL_FAILED;
	}
}

void ignore_all_signals (callstatus *status)
{
	set_all_signals (SIG_IGN, status);
}

void default_all_signals (callstatus *status)
{
	set_all_signals (SIG_DFL, status);
}
#endif

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
	status->msg = MSG_NO_ERROR;

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

HANDLE openTaskfile(callstatus *status)
{
	HANDLE taskfile = NULL;
	int retry_cnt = 0;
#ifndef WINDOWS
	struct flock lock;
	int tffd;
#endif

	filepos = -1;
	bufpos = 0;
	buflen = 0;

	while (1) {
#ifndef WINDOWS
		tffd = open(taskfileName, O_RDWR|O_SYNC|O_RSYNC);
		if (tffd < 0) {
			exit(1);
		}
		taskfile = fdopen(tffd, "r+");

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
#else

		taskfile = CreateFile (
		                   taskfileName,
		                   GENERIC_READ | GENERIC_WRITE,
		                   0,
		                   NULL,
		                   OPEN_EXISTING,
		                   FILE_ATTRIBUTE_NORMAL,
		                   NULL);
		if (taskfile != INVALID_HANDLE_VALUE)
			break;

		const DWORD errn = GetLastError();
		if (errn != ERROR_TOO_MANY_OPEN_FILES) {
			status->severity = SEVERITY_FATAL;
			status->msg = INVALID_TASKFILE;
			return NULL;
		}

#endif

		sleep(RETRY_OPEN_TIME + retry_cnt);
		retry_cnt++;
	}

#ifndef WINDOWS
	retry_cnt = 0;
	tffd = fileno(taskfile);
	lock.l_type = F_WRLCK;
	lock.l_whence = FILE_BEGIN;
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
#else

#endif

	return taskfile;
}

void closeTaskfile(callstatus *status, HANDLE taskfile)
{
	if (taskfile != NULL) {
		if (Fflush(taskfile) != 0) {
			status->severity = SEVERITY_FATAL;
			status->msg = TFWRITE_FAILED;
			return;
		}
		if (Fclose(taskfile) != 0) {
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
	if (bufpos == -1) {
		return;
	}

	if (filepos == -1 || bufpos == buflen) {
		if (filepos == -1)
			Fseek(taskfile, 0, FILE_BEGIN);
		else
			Fseek(taskfile, filepos, FILE_BEGIN);

		if ((buflen = Fread(taskfileBuf, sizeof(char), TF_BUFSIZE, taskfile, buflen)) == 0) {
#ifndef WINDOWS
			if (feof(taskfile)) {
#endif

				if (filepos == -1) {
					status->severity = SEVERITY_FATAL;
					status->msg = TF_EMPTY;
					return;
				}
				bufpos = -1;
				return;
#ifndef WINDOWS
			} else {

				status->severity = SEVERITY_FATAL;
				status->msg = TFREAD_ERROR;
				status->syserror = ferror(taskfile);
				return;
			}
#endif
		}

		bufpos = 0;
		if (filepos == -1)
			filepos = buflen;
		else
			filepos += buflen;
	} else {
		bufpos++;
	}

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
	else if (!strcmp(key, S_ERROR))		error = Strdup(status, value);
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

void appendTaskfile(callstatus *status, char *key, char *value, int alreadyOpen)
{
	int numBytes;
	int seekpos;
#ifdef WINDOWS
	DWORD bytesWritten;
#else
	int bytesWritten;
#endif

	if (alreadyOpen == 0) {
		taskfile = openTaskfile(status);
		if (status->severity != STATUS_OK) return;
	} else {
		// reset file position

		Fseek(taskfile, 0, FILE_BEGIN);
		filepos = -1;
		bufpos = 0;
		buflen = 0;
	}

	advance(status);
	if (status->severity != STATUS_OK) return;

	while (bufpos >= 0 && taskfileBuf[bufpos] != '\0') {
		advance(status);
		if (status->severity != STATUS_OK) return;
	}

	seekpos = filepos - buflen + bufpos;
	if (Fseek(taskfile, seekpos, FILE_BEGIN) < 0) {
		status->severity = SEVERITY_FATAL;
		status->msg = SEEK_FAILED;
		status->syserror = errno;
		return;
	}

	if ((numBytes = snprintf(tfWriteBuf, TF_BUFSIZE, "%s %s=%s\n", getTimestamp(time(NULL)), key, value)) >= TF_BUFSIZE) {
		status->severity = SEVERITY_FATAL;
		status->msg = WRITE_FAILED;
		status->syserror = errno;
		return;
	}

	tfWriteBuf[numBytes] = '\0';

	bytesWritten = Fwrite(tfWriteBuf, numBytes, 1, taskfile, bytesWritten) * numBytes;
	if (bytesWritten != numBytes) {
		status->severity = SEVERITY_FATAL;
		status->msg = WRITE_FAILED;
		status->syserror = errno;
		return;
	}

	if (alreadyOpen == 0) {
		closeTaskfile(status, taskfile);
	} else {
		Fflush(taskfile);
	}

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

void openLog(callstatus *status)
{
	char *pl;
	char privateLog[PATH_MAX + 1];
	int myLogFd;
	mode_t mode = S_IRUSR | S_IWUSR;

	fclose(stdin);

	pl = getenv(JOBEXECUTORLOG);
	if (pl == NULL) strncpy(privateLog, NULLDEVICE, PATH_MAX);
	else snprintf(privateLog, PATH_MAX, "%s.%d", pl, getpid());

#ifndef WINDOWS
	myLogFd = open(privateLog, O_WRONLY|O_SYNC|O_CREAT, mode);
	if (myLogFd < 0) {
		myLog = NULL;
		return;
	}

	if ((myLog = fdopen(myLogFd, "w")) == NULL) {
		myLog = fopen(NULLDEVICE, "w");
	}
#else
	myLog = CreateFile (
	                privateLog,
	                GENERIC_WRITE,
	                FILE_SHARE_READ,
	                NULL,
	                CREATE_ALWAYS,
	                FILE_ATTRIBUTE_NORMAL,
	                NULL);
	if (myLog == INVALID_HANDLE_VALUE) {
		myLog = CreateFile(
		                NULLDEVICE,
		                GENERIC_WRITE,
		                FILE_SHARE_READ|FILE_SHARE_WRITE,
		                NULL,
		                OPEN_EXISTING,
		                FILE_ATTRIBUTE_NORMAL,
		                NULL);
		if (myLog == INVALID_HANDLE_VALUE)
			myLog = NULL;
	}

#endif

	return;
}

void closeLog(callstatus *status)
{
	if (myLog != NULL)
		fclose(myLog);
	return;
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
#ifndef WINDOWS
	int   exitcode;
#else
	DWORD exitcode;
#endif
	char  *execpid;
	char  buf[20];
	int exitstatus;

	execpid = getUniquePid(status, getpid());
	if (status->severity != STATUS_OK) return;

	taskfile = openTaskfile(status);
	if (status->severity != STATUS_OK) return;

	appendTaskfile(status, EXECPID, execpid, 1);
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

#ifndef WINDOWS

	cpid = fork();

	if (cpid < 0) {

		status->severity = SEVERITY_FATAL;
		status->msg = FORK_FAILED;
		status->syserror = errno;
		return;
	}
	if (cpid > 0) {

		closeTaskfile(status, taskfile);

		exitcode = -1;
		while (exitcode == -1) {
			exitstatus = 0;
			waitpid(cpid, &exitstatus, 0);
			if (WIFEXITED (exitstatus))
				exitcode = WEXITSTATUS (exitstatus);
			else if (WIFSIGNALED (exitstatus))
				exitcode = 128 + WTERMSIG (exitstatus);
		}

		if (verboselogs) {
			fprintf(stdout, "------- %s End (%d) --------\n", getTimestamp(time(NULL)), exitcode);
		}

		snprintf(buf, 20, "%d", exitcode);
		taskfile = openTaskfile(status);
		if (status->severity != STATUS_OK) return;
		appendTaskfile(status, STATUS, STATUS_FINISHED, 1);
		if (status->severity != STATUS_OK) return;
		appendTaskfile(status, RETURNCODE, buf, 1);
		if (status->severity != STATUS_OK) return;
		closeTaskfile(status, taskfile);
		return;
	}

	default_all_signals (status);
	if (status->severity != STATUS_OK) {
		if (myLog != NULL) fprintf(myLog, "%s\n", message[status->msg]);
		status->severity = STATUS_OK;
		status->msg = MSG_NO_ERROR;
	}
	myStartTime = time(NULL);
	extpid = getUniquePid(status, getpid());
	if (status->severity != STATUS_OK) return;

	appendTaskfile(status, EXTPID, extpid, 1);
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, STATUS, STATUS_RUNNING, 1);
	if (status->severity != STATUS_OK) return;

	closeTaskfile(status, taskfile);

	if (setsid() == -1) {

	}

	if (verboselogs) {
		fprintf(stdout, "------- %s Start --------\n", getTimestamp(time(NULL)));
		Fflush(stdout);
	}

	if (usepath) {
		execvp(command, argv);
	} else {
		execv(command, argv);
	}

	status->severity = SEVERITY_FATAL;
	status->msg = EXEC_FAILED;
	status->syserror = errno;
#else

	size_t size = strlen(argv[0]) + sizeof ('\0');
	for (i = 1; argv[i]; ++i)
		size += strlen (" ") + strlen (argv[i]);

	char *const cmdline = (char *) malloc (size);
	if (! cmdline) {
		status->severity = SEVERITY_FATAL;
		status->msg = OUT_OF_MEM;
		return;
	}

	int ofs = sprintf (cmdline, "%s", argv[0]);
	for (i = 1; argv[i]; ++i)
		ofs += sprintf (cmdline + ofs, " %s", argv[i]);

	char *module = usepath ? NULL : command;
	if (module && (*module == '"')) {
		char *unquoted = Strdup(status, module + 1);
		if (status->severity != STATUS_OK) {
			return;
		}
		unquoted[strlen(unquoted) - 1] = '\0';
		module = unquoted;
	}

	STARTUPINFO si;
	ZeroMemory (&si, sizeof (si));
	si.cb = sizeof (si);

	PROCESS_INFORMATION pi;

	if (! CreateProcess (
	            module,
	            cmdline,
	            NULL,
	            NULL,
	            TRUE,
	            0,
	            NULL,
	            NULL,
	            &si,
	            &pi)) {
		status->severity = SEVERITY_FATAL;
		status->msg = EXEC_FAILED;
		return;
	}

	extpid = getUniquePid(status, pi.dwProcessId);
	if (status->severity != STATUS_OK) return;

	appendTaskfile(status, EXTPID, extpid, 1);
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, STATUS, STATUS_RUNNING, 1);
	if (status->severity != STATUS_OK) return;
	closeTaskfile(status, taskfile);

	if (WaitForSingleObject (pi.hProcess, INFINITE) == WAIT_FAILED) {
		status->severity = SEVERITY_FATAL;
		status->msg = CHLDWAIT_FAILED;
		return;
	}
	if (! GetExitCodeProcess (pi.hProcess, &exitcode)) {
		status->severity = SEVERITY_FATAL;
		status->msg = CHLDWAIT_FAILED;
		return;
	}

	CloseHandle (pi.hProcess);

#endif
	return;
}

int main(int argc, char *argv[])
{
	int argsType;
	callstatus status;

	myStartTime = time(NULL);

	initFields();
	status.severity = STATUS_OK;
	status.msg = MSG_NO_ERROR;

	openLog(&status);

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
		ignore_all_signals (&status);
		if (status.severity != STATUS_OK) {
			if (myLog != NULL) fprintf(myLog, "%s\n", message[status.msg]);
			status.severity = STATUS_OK;
			status.msg = MSG_NO_ERROR;
		}
		run(&status);
		if (status.severity != STATUS_OK) {

			if (myLog != NULL) fprintf(myLog, "%s\n", message[status.msg]);
		}
		closeLog(&status);
		break;
	default:

		fprintf(stderr, "Argument evaluation returned an unexpected value : %d\n", argsType);
		fprintf(stderr, "%s", getUsage());
		exit(1);
	}

	return status.severity;
}
