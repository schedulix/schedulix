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
#ifndef WINDOWS
#include <sys/wait.h>
#include <ctype.h>
#endif
#include <sys/types.h>
#ifdef WINDOWS
#include <windows.h>
#endif

#ifndef OPEN_MAX
#define OPEN_MAX 256
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
#define Hprintf            hprintf
#define Strerror           winStrerror
#else
#define FILE_BEGIN         SEEK_SET
#define FILE_CURRENT	   SEEK_CUR
#define FILE_END           SEEK_END
#define Fread(a,b,c,d,e)   fread((a), (b), (c), (d))
#define Fwrite(a,b,c,d,e)  fwrite((a), (b), (c), (d))
#define Fseek(a, b, c)     fseek((a), (b), (c))
#define Hprintf            fprintf
#define Strerror           strerror
#endif

/*
Warnings can be reported to the taskfile
Errors must be reported to the taskfile since further processing isn't possible
Fatals lead to a hard exit
*/
#define STATUS_OK      0
#define SEVERITY_WARNING 1
#define SEVERITY_FATAL   3

typedef struct _callstatus {
	int severity;	/* mandatory field, the severity of the error */
	int msg;	/* mandatory field, contains error code (see below) */
	int syserror;	/* optional field, contains errno if applicable */
	char *msg2;	/* optional field, contains additional information */
} callstatus;

typedef struct _errmsg_t {
	const char *msg;
#define T_NONE   0
#define T_INT    1
#define T_STRING 2
#define T_BOTH   3
	int  argType;
} errmsg_t;

errmsg_t message[] = {
#define MSG_NO_ERROR     0
	{ "No error", T_NONE },
#define INVALID_TASKFILE 1
	{ "Invalid taskfile (doesn't exist, isn't readable or writable (%d / %s))", T_BOTH },
#define INVALID_FD       2
	{ "Invalid file descriptor (open() succeeded, fd invalid)", T_NONE },
#define TFWRITE_FAILED   3
	{ "Write to taskfile failed", T_NONE },
#define TFCLOSE_FAILED   4
	{ "Close of taskfile failed (%d / %s)", T_BOTH },
#define TF_EMPTY         5
	{ "Task file empty", T_NONE },
#define TFREAD_ERROR     6
	{ "Error on read (%d / %s)", T_BOTH },
#define TFSYNTAX_ERROR   7
	{ "Syntax error in taskfile (%s)", T_STRING },
#define OUT_OF_MEM       8
	{ "memory allocation failed", T_NONE },
#define WRONG_ARGS       9
	{ "Wrong number or type of arguments", T_NONE },
#define TF_INCOMPLETE   10
	{ "Taskfile seems to be incomplete", T_NONE },
#define FORK_FAILED     11
	{ "Couldn't create process; fork() failed", T_NONE },
#define LOGOPEN_FAILED  12
	{ "Couldn't open logfile (%d / %s)", T_BOTH },
#define CHDIR_FAILED    13
	{ "Couldn't chdir to working directory (%d / %s)", T_BOTH },
#define ERROPEN_FAILED  14
	{ "Couldn't open error logfile (%d / %s)", T_BOTH },
#define DUP_FAILED      15
	{ "Couldn't redirect stderr to stdout (%d / %s)", T_BOTH },
#define EXEC_FAILED     16
	{ "Couldn't execute command (%d / %s)", T_BOTH },
#define SEEK_FAILED     17
	{ "Couldn't seek to logical end of file (%d / %s)", T_BOTH },
#define WRITE_FAILED    18
	{ "Couldn't write into taskfile (%d / %s)", T_BOTH },
#define TFMISSING_VALUE 19
	{ "Mandatory value missing in taskfile (key %s)", T_STRING },
#define INVALID_BOOTTIME 20
	{ "Invalid boottime (too long)", T_NONE },
#define TFUNLOCK_FAILED 21
	{ "Couldn't release taskfile lock", T_NONE },
#define CHLDWAIT_FAILED	22
	{ "Wait for child process failed", T_NONE },
#define SET_SIGNAL_FAILED 23
	{ "Set signal handler failed", T_NONE }
};

const char *ARG_VERSION1 = "--version";
const char *ARG_VERSION2 = "-v";
const char *ARG_HELP1    = "--help";
const char *ARG_HELP2    = "-h";

/* some protocol fields */
unsigned char boottimeHow = 'N';

const char *COMMAND       = "command";
const char *ARGUMENT      = "argument";
const char *WORKDIR       = "workdir";
const char *USEPATH       = "usepath";
const char *VERBOSELOGS   = "verboselogs";
const char *LOGFILE       = "logfile";
const char *LOGFILEAPPEND = "logfile_append";
const char *ERRLOG        = "errlog";
const char *ERRLOGAPPEND  = "errlog_append";
const char *SAMELOGS      = "samelogs";
const char *EXECPID       = "execpid";
const char *EXTPID        = "extpid";
const char *RETURNCODE    = "returncode";
const char *S_ERROR       = "error";
const char *STATUS        = "status";
const char *STATUS_TX     = "status_tx";
const char *INCOMPLETE    = "incomplete";
const char *COMPLETE      = "complete";

const char *STATUS_RUNNING     = "RUNNING";
const char *STATUS_FINISHED    = "FINISHED";
const char *STATUS_ERROR       = "ERROR";
const char *STATUS_CHILD_ERROR = "CHILD_ERROR";

#define TF_BUFSIZE 16384
#define MAXLENGTH   8192
struct _global {
	char *boottime;
	time_t myStartTime;

/* Job describing fields */
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

/* some globals for parsing the taskfile
   since we're single threaded, a global state is ok
*/
#ifndef WINDOWS
	int buflen;
	#define HANDLE FILE*
#else
	DWORD buflen;
#endif
	int bufpos;
	int filepos;
	char *taskfileName;
	HANDLE taskfile;

	char taskfileBuf[TF_BUFSIZE];
	char tfWriteBuf[TF_BUFSIZE];

#ifndef WINDOWS
	char *errorTaskfilePath;
	char *errorTaskfileName;
	int etflinked;
#endif
} global;
HANDLE myLog = NULL;

/* prototypes */
const char *getUsage();
const char *getVersion();
void initFields();
int checkArgs(callstatus *status, int argc, char *argv[]);
HANDLE openTaskfile(callstatus *status);
void closeTaskfile(callstatus *status, HANDLE taskfile);
#ifndef WINDOWS
void createErrorTaskfileName(char *tfn);
void createTfLink();
#endif
void advance(callstatus *status);
void readTimestamp(callstatus *status);
void readWhiteSpace(callstatus *status);
void readKey(callstatus *status, char *key);
void readLength(callstatus *status, char *lgth);
void readValue(callstatus *status, int lgth, char *value);
char *renderError(callstatus *status);
void processTaskfile(callstatus *status);
void evaluateTaskfile(callstatus *status);
void appendTaskfile(callstatus *status, const char *key, char *value, int alreadyOpen);
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
char *winStrerror(DWORD errorno);
DWORD hprintf(HANDLE wrc, char *format, ...);
#endif
char *Strdup(callstatus *status, char *src);

const char *getUsage()
{
	/* TODO: Generate this from jobserver.jexecutor.Jexecutor.java (?)*/
	return  "Usage:\n" \
		"jobexecutor [--version|-v] [--help|-h] [<boottime_how> <taskfileName> [boottime]]\n" \
		"\n" \
		"Exactly one of the optional argument sets must be specified\n" \
		"i.e. either the version request, or this help request or some specification on what to do\n";
}

const char *getVersion()
{
	/* TODO: Generate this from server.SystemEnvironment.java or jobserver.jexecutor.Jexecutor.java (?)*/
	/* First line must match with line expected from java jobserver agent !!! */
	return  "Jobserver (executor) 2.8\n" \
		"Copyright (C) 2013 independIT Integrative Technologies GmbH\n" \
		"All rights reserved\n";
}

void initFields()
{
	global.command = NULL;
	global.argument = NULL;
	global.num_args = 0;
	global.argsize = 0;
	global.workdir = NULL;
	global.usepath = false;
	global.verboselogs = false;
	global.logfile = NULL;
	global.logfileappend = false;
	global.errlog = NULL;
	global.errlogappend = false;
	global.samelogs = false;
	global.execpid = NULL;
	global.extpid = NULL;
	global.returncode = NULL;
	global.error = NULL;
	global.jstatus = NULL;
	global.jstatus_tx = NULL;
	global.complete = false;

	global.taskfile = NULL;
	global.buflen = 0;
	global.bufpos = 0;
	global.filepos = -1;

#ifndef WINDOWS
	global.etflinked = false;
	global.errorTaskfilePath = NULL;
	global.errorTaskfileName = NULL;
#endif
}

#ifndef WINDOWS
void createErrorTaskfileName(char *tfn)
{
	static const char *errorDir = "errorTaskfiles";
	global.errorTaskfileName = NULL;
	global.errorTaskfilePath = NULL;
	char *dirname;
	char *filename;
	int len;

	dirname = strdup(tfn);
	if (dirname == NULL) return;	/* no mem; no safety net */
	len = (int) strlen(dirname);
	while (len > 0 && dirname[len - 1] != '/') {
		len--;
		dirname[len] = '\0';
	}
	if (len == 0) return;		/* no path; Name should be full qualified though */
	filename = tfn + len;

	len += (int) strlen(errorDir);
	global.errorTaskfilePath = (char *) malloc(len + 1);
	if (global.errorTaskfilePath == NULL) {
		free(dirname);
		return;
	}

	global.errorTaskfilePath[0] = '\0';
	strcat(global.errorTaskfilePath, dirname);
	strcat(global.errorTaskfilePath, errorDir);
	free(dirname);

	global.errorTaskfileName = (char *) malloc(len + 1 + strlen(filename) + 1);
	if (global.errorTaskfileName == NULL) {
		free(global.errorTaskfilePath);
		global.errorTaskfilePath = NULL;
		return;
	}
	strcat(global.errorTaskfileName, global.errorTaskfilePath);
	strcat(global.errorTaskfileName, "/");
	strcat(global.errorTaskfileName, filename);
}

void createTfLink()
{
	/* if we already had an error, we already have a link			*/
	/* if we couldn't determine the errorTaskfilePath, we don't try to link */
	if (global.etflinked || global.errorTaskfilePath == NULL)
		return;

	/* create the errorTaskfilePath Directory first.			*/
	/* if the return value == 0 or errno == EEXIST we can (try to) continue	*/
	if (mkdir(global.errorTaskfilePath, 0700) != 0) {
		if (errno != EEXIST) return;
	}

	if (link(global.taskfileName, global.errorTaskfileName) != 0) {
		/* no real means for error processing here (yet?)		*/
		/* but the next time (if there is one) we might succeed		*/
		/* But is possible we linked the taskfile in a previous run     */
		/* That taskfile has to be removed manually			*/
		if (errno != EEXIST)
			return;
	}
	global.etflinked = true;
}
#endif

char *renderError(callstatus *status)
{
	char *msg;
	int len;
	errmsg_t *e;
	char *syserr = NULL;

	e = &message[status->msg];
	len = (int) strlen(e->msg) + 1; /* \0 */
	switch (e->argType) {
		case T_NONE:
			break;
		case T_STRING:
			if (status->msg2 == NULL)
				status->msg2 = (char *) "";
			else
				len += (int) strlen(status->msg2);
			break;
		case T_INT:
			len += 10;	/* max length of a 4 byte int */
			break;
		case T_BOTH:
			len += 10;
			syserr = strdup(Strerror(status->syserror));	/* we need a copy, could get overwritten; accepted memory leak */
			if (syserr == NULL)
				syserr = (char *) "(unable to retrieve system error message)";
			len += (int) strlen(syserr);
			break;
	}
	msg = (char *) malloc(len);

	if (msg != NULL) {
		memset(msg, 0, len);
		switch (e->argType) {
			case T_NONE:
				snprintf(msg, len, e->msg);
				break;
			case T_STRING:
				snprintf(msg, len, e->msg, status->msg2);
				break;
			case T_INT:
				snprintf(msg, len, e->msg, status->syserror);
				break;
			case T_BOTH:
				snprintf(msg, len, e->msg, status->syserror, syserr);
				break;
		}
	} else
		msg = (char *) "unable to render error message";
	
	return msg;	/* we generously accept this memory leak. The program will terminate soon anyway */
}

#ifdef WINDOWS
char *winStrerror(DWORD errorno)
{
	LPTSTR result = NULL;
	DWORD retSize;

	retSize=FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER|
				FORMAT_MESSAGE_FROM_SYSTEM|
				FORMAT_MESSAGE_ARGUMENT_ARRAY,
				NULL,
				errorno,
				LANG_NEUTRAL,
				(LPTSTR) &result,
				0,
				NULL );
	if (!retSize || result == NULL) {
		return (char *) "(Failed to render error message)";
	}
	result[strlen(result)-2]='\0'; //remove cr and newline character

	return (char *) result;
}

DWORD hprintf(HANDLE wrc, char *format, ...)
{
#define BUFSIZE 2048
        va_list ap;
        char buf[BUFSIZE];
        DWORD nc;       /* number of chars */
        DWORD nw;       /* number written */

        if (wrc == INVALID_HANDLE_VALUE) return (DWORD) -1;
        va_start(ap, format);
        nc = vsnprintf(buf, BUFSIZE, format, ap);
        va_end(ap);
        if (nc < 0) return nc;
        if (nc == BUFSIZE) {
                /* would have been too long; we simply truncate it since it's only used for error messages */
                buf[BUFSIZE - 1] = '\0';
        }
        if (!WriteFile(wrc, buf, nc, &nw, NULL)) {
                /* process write error */;
        }
        if (nc != nw) {
                /* hm, what to do here ??? */;
        }

        return nw;
}
#endif

char *Strdup(callstatus *status, char *src)
{
	char *trg;

	if (*src == '\0') {
		status->severity = SEVERITY_WARNING;
		status->msg = TFMISSING_VALUE;
		trg = (char *) malloc(1);
		*trg = '\0';
		return trg;
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

#ifdef BSD
	sigemptyset (&aktschn.sa_mask);

	aktschn.sa_flags   = 0;
#endif
	aktschn.sa_handler = SIG_IGN;

	set_all_signals (aktschn, status);
}

void default_all_signals (callstatus *status)
{
	struct sigaction aktschn;
#ifdef BSD
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

	Hprintf(myLog, "command       = %s\n", global.command != NULL ? global.command : "NULL");
	Hprintf(myLog, "arguments:\n");
	if (global.argument == NULL)
		Hprintf(myLog, "\tNULL\n");
	else {
		for (i = 0; i < global.num_args; ++i)
			Hprintf(myLog, "\t%2.2d: %s\n", i, global.argument[i]);
	}
	Hprintf(myLog, "workdir       = %s\n", global.workdir != NULL ? global.workdir : "NULL");
	Hprintf(myLog, "usepath       = %s\n", global.usepath == false ? "false" : "true");
	Hprintf(myLog, "verboselogs   = %s\n", global.verboselogs == false ? "false" : "true");
	Hprintf(myLog, "logfile       = %s\n", global.logfile != NULL ? global.logfile : "NULL");
	Hprintf(myLog, "logfileappend = %s\n", global.logfileappend == false ? "false" : "true");
	Hprintf(myLog, "errlog        = %s\n", global.errlog != NULL ? global.errlog : "NULL");
	Hprintf(myLog, "errlogappend  = %s\n", global.errlogappend == false ? "false" : "true");
	Hprintf(myLog, "samelogs      = %s\n", global.samelogs == false ? "false" : "true");
	Hprintf(myLog, "execpid       = %s\n", global.execpid != NULL ? global.execpid : "NULL");
	Hprintf(myLog, "extpid        = %s\n", global.extpid != NULL ? global.extpid : "NULL");
	Hprintf(myLog, "returncode    = %s\n", global.returncode != NULL ? global.returncode : "NULL");
	Hprintf(myLog, "error         = %s\n", global.error != NULL ? global.error : "NULL");
	Hprintf(myLog, "jstatus       = %s\n", global.jstatus != NULL ? global.jstatus : "NULL");
	Hprintf(myLog, "jstatus_tx    = %s\n", global.jstatus_tx != NULL ? global.jstatus_tx : "NULL");
	Hprintf(myLog, "complete      = %s\n", global.complete == false ? "false" : "true");
}

void addArgument(callstatus *status, char *value)
{
#define ARGCHUNK 32
	int i;

	if (global.argument == NULL) {
		global.argument = (char **) malloc(ARGCHUNK * sizeof(char *));
		if (global.argument == NULL) {
			status->severity = SEVERITY_FATAL;
			status->msg = OUT_OF_MEM;
			return;
		}
		global.argsize = ARGCHUNK;
		for (i = global.num_args; i < global.argsize; ++i) global.argument[i] = NULL;
	} else {
		if (global.num_args == global.argsize) {
			global.argument = (char **) realloc(global.argument, (global.argsize + ARGCHUNK) * sizeof(char*));
			if (global.argument == NULL) {
				status->severity = SEVERITY_FATAL;
				status->msg = OUT_OF_MEM;
				return;
			}
			global.argsize += ARGCHUNK;
			/* make valid pointers; we don't rely on side effects */
			for (i = global.num_args; i < global.argsize; ++i) global.argument[i] = NULL;
		}
	}

	global.argument[global.num_args] = Strdup(status, value);

	if (status->severity != SEVERITY_FATAL)  {
		status->severity = STATUS_OK;
		global.num_args++;
	}

	return;
}

int checkArgs(callstatus *status, int argc, char *argv[])
{
	status->severity = STATUS_OK;
	status->msg = MSG_NO_ERROR;

	if (argc <= 4 && argc > 1) {	/* we have the command + 1, 2 or 3 parameters */
		if (!strcmp(argv[1], ARG_VERSION1) || !strcmp(argv[1], ARG_VERSION2)) {
			return ARGS_VERSION;
		}
		if (!strcmp(argv[1], ARG_HELP1) || !strcmp(argv[1], ARG_HELP2)) {
			return ARGS_HELP;
		}
		if (argc < 3) {
			status->severity = SEVERITY_FATAL;
			status->msg = WRONG_ARGS;
			return ARGS_NOTREAD;
		}

		boottimeHow = (unsigned char) toupper((unsigned char) argv[1][0]);
		if (!(boottimeHow == BOOTTIME_SYSTEM ||
		      boottimeHow == BOOTTIME_FILE   ||
		      boottimeHow == BOOTTIME_NONE)) {
			status->severity = SEVERITY_FATAL;
			status->msg = WRONG_ARGS;
			return ARGS_NOTREAD;
		}

		global.taskfileName = argv[2];
#ifndef WINDOWS
		createErrorTaskfileName(global.taskfileName);
#endif

		if (argc == 4) {
			global.boottime = argv[3];
			if (strlen(global.boottime) > 19) {
				status->severity = SEVERITY_FATAL;
				status->msg = INVALID_BOOTTIME;
				return ARGS_NOTREAD;
			}
		} else
			global.boottime = NULL;

		processTaskfile(status);
		if (status->severity == SEVERITY_FATAL) {
			return ARGS_NOTREAD;
		}

		if (!global.complete) {
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

/*------------------------------------------------------------------------------*/
/* Taskfile Routines                                                            */
/*------------------------------------------------------------------------------*/
HANDLE openTaskfile(callstatus *status)
{
	HANDLE taskfile = NULL;
	int retry_cnt = 0;
#ifndef WINDOWS
	struct flock lock;
	int tffd;					/* File Descriptor of Taskfile, needed for fcntl */
#endif
	/* reset some bookkeeping fields */
	global.filepos = -1;
	global.bufpos = 0;
	global.buflen = 0;

	while (1) {
#ifndef WINDOWS
#ifdef O_RSYNC
		tffd = open(global.taskfileName, O_RDWR|O_SYNC|O_RSYNC);
#else
		tffd = open(global.taskfileName, O_RDWR|O_SYNC);
#endif
		if (tffd < 0) {
			exit(1);
		}
		taskfile = fdopen(tffd, "r+");		/* we might or might not write */
							/* but open for write is required for an exclusive lock */
		if (taskfile != NULL) break;		/* fopen() succeeded */

		/* hard errors, won't change over time */
		if (errno == ENOENT ||			/* File does not exist */
		    errno == EACCES ||			/* Permission denied */
		    errno == EISDIR ||			/* file is a directory */
		    errno == ELOOP			/* link chain too long */
		) {
			status->severity = SEVERITY_FATAL;
			status->msg = INVALID_TASKFILE;
			status->syserror = errno;
			return NULL;
		}
#else
		/*
		 * Excerpt form MSDN Documentation:
		 * As stated previously, if the lpSecurityAttributes parameter is NULL,
		 * the handle returned by CreateFile cannot be inherited by any child
		 * processes your application may create.
		 * This is exactly the behaviour we need (here)
		 */
		taskfile = CreateFile (
				global.taskfileName,
				GENERIC_READ | GENERIC_WRITE,           // access mode
				FILE_SHARE_READ | FILE_SHARE_WRITE,	// share mode; share all access to prevent the open() to fail
				NULL,                                   // security attributes -> not inheritable
				OPEN_EXISTING,                          // how to create
				FILE_ATTRIBUTE_NORMAL,                  // file attributes
				NULL);                                  // handle to template file
		if (taskfile != INVALID_HANDLE_VALUE) {
			// we've openend the file. Now lock it to get private access
			OVERLAPPED regionStart;
			DWORD fsLow = 0xFFFFFFFF, fsHigh = 0xFFFFFFFF;

			regionStart.Offset = 0;
			regionStart.OffsetHigh = 0;
			regionStart.hEvent = (HANDLE)0;


			if (!LockFileEx(taskfile, LOCKFILE_EXCLUSIVE_LOCK, 0, fsLow, fsHigh, &regionStart)) {
				/* shouldn't fail, but block until the lock is granted */ ;
				if (myLog != NULL) Hprintf(myLog, "Couldn't lock file > %s < (%d)\n", global.taskfileName, GetLastError());
			}
			break;
		}

		const DWORD errn = GetLastError();
		if (errn != ERROR_TOO_MANY_OPEN_FILES &&
		    errn != ERROR_SHARING_VIOLATION &&
		    errn != ERROR_LOCK_VIOLATION) {		// Not a soft error?
			status->severity = SEVERITY_FATAL;
			status->msg = INVALID_TASKFILE;
			status->syserror = errn;
			if ((errn == ERROR_FILE_NOT_FOUND) && (retry_cnt < 11)) {
				if (myLog != NULL) Hprintf(myLog, "Couldn't open file > %s <\n", global.taskfileName);
			} else
				return NULL;
		}

#endif

		/* Soft errors might vanish after a while */
		/* we increase the wait time, 1 second at a time, if errors occur persistently */
		sleep(RETRY_OPEN_TIME + retry_cnt);
		retry_cnt++;
	}

	/* now lock the entire file */
	/* For windows this is done implicitly when opening the file via CreateFile */
#ifndef WINDOWS
	retry_cnt = 0;					/* reset retry count */
	tffd = fileno(taskfile);
	lock.l_type = F_WRLCK;
	lock.l_whence = FILE_BEGIN;
	lock.l_start = 0;
	lock.l_len = 0;					/* Specifying  0  for  l_len has the special meaning:
							   lock all bytes starting at the location specified by
							   l_whence and l_start through to the end of file, no
							   matter how large the file grows
							*/
	while (1) {
		if (fcntl(tffd, F_SETLKW, &lock) == 0) break;	/* we wait for the lock */

		if (errno == EBADF) {			/* Bad File Descriptor -> invalid internal state (not our fault) */
			status->severity = SEVERITY_FATAL;
			status->msg = INVALID_FD;
			return NULL;
		}
		/* Soft errors might vanish after a while */
		/* we increase the wait time, 1 second at a time, if errors occur persistently */
		sleep(RETRY_LOCK_TIME + retry_cnt);
		retry_cnt++;
	}
#endif

	return taskfile;
}

void closeTaskfile(callstatus *status, HANDLE taskfile)
{
	if (taskfile != NULL) {
#ifndef WINDOWS
		if (fflush(taskfile) != 0) {		/* releases locks */
#else
		if (FlushFileBuffers(taskfile) == 0) {		/* releases locks */
#endif
			status->severity = SEVERITY_FATAL;
			status->msg = TFWRITE_FAILED;
			return;
		}
#ifndef WINDOWS
		if (fclose(taskfile) != 0) {		/* releases locks */
#else
		/* release lock first; we have flushed already, hence no side effects should occur */
		OVERLAPPED regionStart;
		DWORD fsLow = 0xFFFFFFFF, fsHigh = 0xFFFFFFFF;

		regionStart.Offset = 0;
		regionStart.OffsetHigh = 0;
		regionStart.hEvent = (HANDLE)0;


		if (!UnlockFileEx(taskfile, 0, fsLow, fsHigh, &regionStart)) {
				/* shouldn't fail */ ;
		}
		if (CloseHandle(taskfile) == 0) {
#endif
			status->severity = SEVERITY_WARNING;
			status->msg = TFCLOSE_FAILED;
#ifndef WINDOWS
			status->syserror = errno;
#else
			status->syserror = GetLastError();
#endif
			if (myLog != NULL) Hprintf(myLog, "%s\n", renderError(status));
		}
	}

	/* reset the fileposition describing fields */
	global.filepos = -1;
	global.bufpos = 0;
	global.buflen = 0;

	return;
}

/*
taskfile Syntax:

file: entrylist
entrylist: entry
	| entrylist entry
entry: [timestamp] KEY \n
	| [timestamp] KEY =value \n
	| [timestamp] KEY 'length =value \n

Because of the simplicity of the syntax, we do a recursive descent parser

Spaces are optional. Everything after the equals sign is regarded value.
This is also true for spaces following the equals sign
*/
void advance(callstatus *status)
{
	if (global.bufpos == -1) {
		return;	/* after EOF we only return EOF */
	}

	/* are we at the end of the buffer? */
	if (global.filepos == -1 || global.bufpos == global.buflen) {
		if (global.filepos == -1)
			Fseek(global.taskfile, 0, FILE_BEGIN);
		else
			Fseek(global.taskfile, global.filepos, FILE_BEGIN);

		/* read a chunk of the taskfile into a buffer */
		if ((global.buflen = (int) Fread(global.taskfileBuf, sizeof(char), TF_BUFSIZE, global.taskfile, global.buflen)) == 0) {
#ifndef WINDOWS
			if (feof(global.taskfile)) {
#endif
				/* taskfile empty ? */
				if (global.filepos == -1) {	/* we have EOF and didn't read a byte before */
					status->severity = SEVERITY_FATAL;
					status->msg = TF_EMPTY;
					return;
				}
				global.bufpos = -1;	/* we're at EOF */
				return;
#ifndef WINDOWS
			} else {
				/* error on read */
				status->severity = SEVERITY_FATAL;
				status->msg = TFREAD_ERROR;
				status->syserror = ferror(global.taskfile);
				return;
			}
#endif
		}
		/* this way filepos - buflen + bufpos == position in the file of the current character (taskfileBuf[bufpos]) */
		global.bufpos = 0;
		if (global.filepos == -1)
			global.filepos = global.buflen;
		else
			global.filepos += global.buflen;
	} else {
		global.bufpos++;
	}

	return;
}

void readTimestamp(callstatus *status)
{
	/* since we actually don't need the timestamp, we skip it */
	while (global.bufpos >= 0 && global.taskfileBuf[global.bufpos] != TIMESTAMP_LEADOUT) {
		advance(status);
		if (status->severity != STATUS_OK) return;
	}
	/* we must eat the last character of the timestamp too */
	advance(status);
	if (status->severity != STATUS_OK) return;

	return;
}

void readWhiteSpace(callstatus *status)
{
	while (global.bufpos >= 0 && (global.taskfileBuf[global.bufpos] == ' ' ||
				      global.taskfileBuf[global.bufpos] == '\t' ||
				      global.taskfileBuf[global.bufpos] == '\r' ||
				      global.taskfileBuf[global.bufpos] == 0)) {
		advance(status);
		if (status->severity != STATUS_OK) return;
	}

	return;
}

void readKey(callstatus *status, char *key)
{
	int i = 0;

	while (global.bufpos >= 0 && i < MAXLENGTH &&
		!isspace(global.taskfileBuf[global.bufpos]) &&
		global.taskfileBuf[global.bufpos] != '\'' &&
		global.taskfileBuf[global.bufpos] != '=') {
			*(key + i) = (unsigned char) tolower(global.taskfileBuf[global.bufpos]);
			advance(status);
			if (status->severity != STATUS_OK) return;
			i++;
	}
	return;
}

void readLength(callstatus *status, char *lgth)
{
	int i = 0;

	while (global.bufpos >= 0 && i < MAXLENGTH && isdigit(global.taskfileBuf[global.bufpos])) {
		*(lgth + i) = global.taskfileBuf[global.bufpos];
		advance(status);
		if (status->severity != STATUS_OK) return;
		i++;
	}

	return;
}

void readValue(callstatus *status, int lgth, char *value)
{
	int i = 0;

	while (global.bufpos >= 0 && i < MAXLENGTH && (i < lgth || (lgth < 0 && global.taskfileBuf[global.bufpos] != '\n'))) {
		*(value + i) = global.taskfileBuf[global.bufpos];
		advance(status);
		if (status->severity != STATUS_OK) return;
		i++;
	}
	*(value + i) = '\0';

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

	switch (global.taskfileBuf[global.bufpos]) {
		case '\n':
			/* Entry is ready */
			advance(status);	/* skip the newline */
			if (status->severity != STATUS_OK) return;
			/* we accept an EOF instead of a '\n' */
			break;
		case '\'':
			advance(status);	/* skip the quote */
			if (status->severity != STATUS_OK) return;
			if (global.bufpos < 0) {
				status->severity = SEVERITY_FATAL;
				status->msg = TFSYNTAX_ERROR;
				status->msg2 = (char *) "Unexpected EOF, expected a number";
				return;
			}

			readWhiteSpace(status);
			if (status->severity != STATUS_OK) return;

			readLength(status, &(vlength[0]));
			if (status->severity != STATUS_OK) return;
			length = atoi(vlength);

			readWhiteSpace(status);
			if (status->severity != STATUS_OK) return;

			if (global.taskfileBuf[global.bufpos] != '=') {
				status->severity = SEVERITY_FATAL;
				status->msg = TFSYNTAX_ERROR;
				status->msg2 = (char *) "Unexpected token, expected an equal sign";
				return;
			}
			advance(status);	/* skip the equals sign */
			if (status->severity != STATUS_OK) return;
			if (global.bufpos < 1) {
				status->severity = SEVERITY_FATAL;
				status->msg = TFSYNTAX_ERROR;
				status->msg2 = (char *) "Unexpected EOF, expected a value";
				return;
			}

			readValue(status, length, &(value[0]));
			if (status->severity != STATUS_OK) return;

			advance(status);	/* skip the newline  (if any) */
			if (status->severity != STATUS_OK) return;
			/* we accept an EOF instead of a '\n' */
			break;
		case '=':
			advance(status);	/* skip the equals sign */
			if (status->severity != STATUS_OK) return;
			if (global.bufpos < 1) {
				status->severity = SEVERITY_FATAL;
				status->msg = TFSYNTAX_ERROR;
				status->msg2 = (char *) "Unexpected EOF, expected a value";
				return;
			}

			readValue(status, -1, &(value[0]));	/* -1 means: no length specified; read up to newline */
			if (status->severity != STATUS_OK) return;

			advance(status);	/* skip the newline */
			if (status->severity != STATUS_OK) return;
			/* we accept an EOF instead of a '\n' */
			break;
		default:
			/* Syntax error */
			status->severity = SEVERITY_FATAL;
			status->msg = TFSYNTAX_ERROR;
			status->msg2 = (char *) "Unexpected Value, expected an equal sign, a quote or a newline";
			return;
	}
	/* we now should have a key and an optional value */
	if (!strcmp(key, INCOMPLETE))		global.complete = false;
	else if (!strcmp(key, COMMAND))		global.command = Strdup(status, value);
	else if (!strcmp(key, ARGUMENT))	addArgument(status, value);
	else if (!strcmp(key, WORKDIR))		global.workdir = Strdup(status, value);
	else if (!strcmp(key, USEPATH))		global.usepath = true;
	else if (!strcmp(key, VERBOSELOGS))	global.verboselogs = true;
	else if (!strcmp(key, LOGFILE))		global.logfile = Strdup(status, value);
	else if (!strcmp(key, LOGFILEAPPEND))	global.logfileappend = true;
	else if (!strcmp(key, ERRLOG))		global.errlog = Strdup(status, value);
	else if (!strcmp(key, ERRLOGAPPEND))	global.errlogappend = true;
	else if (!strcmp(key, SAMELOGS))	global.samelogs = true;
	else if (!strcmp(key, EXECPID))		global.execpid = Strdup(status, value);
	else if (!strcmp(key, EXTPID))		global.extpid = Strdup(status, value);
	else if (!strcmp(key, RETURNCODE))	global.returncode = Strdup(status, value);
	else if (!strcmp(key, S_ERROR))		global.error = Strdup(status, value);
	else if (!strcmp(key, STATUS))		global.jstatus = Strdup(status, value);
	else if (!strcmp(key, STATUS_TX))	global.jstatus_tx = Strdup(status, value);
	else if (!strcmp(key, COMPLETE))	global.complete = true;

	if (status->msg == TFMISSING_VALUE) {
		/* add some more information to the occurred error */
		status->msg2 = Strdup(&mystatus, key);
	}

	return;
}

void evaluateTaskfile(callstatus *status)
{
	advance(status);
	if (status->severity != STATUS_OK) return;

	while (global.bufpos >= 0 && global.taskfileBuf[global.bufpos] != '\0') {
		processEntry(status);
		if (status->severity != STATUS_OK) {
#ifndef WINDOWS
			/* since something's wrong with the taskfile, we save a reference in the
			// errorTaskfiles directory
			// No need to check success, if it succeeds it's ok, if not, things don't get worse
			*/
			createTfLink();
#endif
			return;
		}
	}

	return;
}

void processTaskfile(callstatus *status)
{
	global.taskfile = openTaskfile(status);
	if (status->severity != STATUS_OK) return;

	evaluateTaskfile(status);
	if (status->severity != STATUS_OK) return;

	closeTaskfile(status, global.taskfile);
	if (status->severity != STATUS_OK) return;

	return;
}

char *getTimestamp(time_t tim, int local)
{
	static char buf [64];
#if !defined SOLARIS && !defined AIX
  #ifdef WINDOWS
	_tzset();
  #endif

	const struct tm *timeval;
	if (local)
		timeval = localtime (&tim);
	else
		timeval = gmtime(&tim);
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
  #ifdef WINDOWS
		(local ? (timeval->tm_isdst > 0 ? _tzname[1] : _tzname[0]) : "GMT"),
  #else
		timeval->tm_zone,
  #endif
		TIMESTAMP_LEADOUT);
#else
	static const char *gmtformat = "%d-%m-%Y %H:%M:%S GMT";
	static const char *localformat = "%d-%m-%Y %H:%M:%S %Z";
	char *format = (char *) (local ? localformat : gmtformat);
	buf [0] = TIMESTAMP_LEADIN;
	const size_t len = strftime (buf + 1, sizeof (buf) - 3 * sizeof (char), format, local ? localtime (&tim) : gmtime(&tim));
	buf [len + 1] = TIMESTAMP_LEADOUT;
	buf [len + 2] = '\0';
#endif
	return buf;
}

void appendTaskfile(callstatus *status, const char *key, char *value, int alreadyOpen)
{
	int numBytes;
	int seekpos;
#ifdef WINDOWS
	DWORD bytesWritten;
#else
	int bytesWritten;
#endif

	if (alreadyOpen == 0) {
		global.taskfile = openTaskfile(status);
		if (status->severity != STATUS_OK) return;
	} else {
		/* reset file position */
		Fseek(global.taskfile, 0, FILE_BEGIN);
		global.filepos = -1;
		global.bufpos = 0;
		global.buflen = 0;
	}

	advance(status);
	if (status->severity != STATUS_OK) return;

	/* this loop advances until eof or a null byte is read */
	while (global.bufpos >= 0 && global.taskfileBuf[global.bufpos] != '\0') {
		advance(status);
		if (status->severity != STATUS_OK) return;
	}

	seekpos = global.filepos - global.buflen + global.bufpos;
	if (Fseek(global.taskfile, seekpos, FILE_BEGIN) < 0) {
		status->severity = SEVERITY_FATAL;
		status->msg = SEEK_FAILED;
		status->syserror = errno;
		return;
	}

	if ((numBytes = snprintf(global.tfWriteBuf, TF_BUFSIZE, "%s %s=%s\n", getTimestamp(time(NULL), 0), key, value)) >= TF_BUFSIZE) {
		status->severity = SEVERITY_FATAL;
		status->msg = WRITE_FAILED;
		status->syserror = errno;
		return;
	}

	global.tfWriteBuf[numBytes] = '\0';

	bytesWritten = (int) Fwrite(global.tfWriteBuf, 1, numBytes, global.taskfile, bytesWritten);
	if (bytesWritten != numBytes) {
		status->severity = SEVERITY_FATAL;
		status->msg = WRITE_FAILED;
		status->syserror = errno;
		return;
	}

	if (alreadyOpen == 0) {
		closeTaskfile(status, global.taskfile);
	} else {
#ifndef WINDOWS
		fflush(global.taskfile);
#else
		FlushFileBuffers(global.taskfile);
#endif
	}

	return;
}

void redirect(callstatus *status)
{
	if (global.workdir != NULL) {
		if (chdir(global.workdir) < 0) {
			status->severity = SEVERITY_FATAL;
			status->msg = CHDIR_FAILED;
			status->syserror = errno;
			return;
		}
	}
#ifndef WINDOWS
#define APPENDFLAG "a"
#else
#define APPENDFLAG "a+"
#endif

	if (!freopen(global.logfile == NULL ? NULLDEVICE : global.logfile , global.logfileappend ? APPENDFLAG : "w", stdout)) {
		status->severity = SEVERITY_FATAL;
		status->msg = LOGOPEN_FAILED;
		status->syserror = errno;
		return;
	}
#ifdef WINDOWS
        // excerpt from visual C documentation:
        //
        // When a file is opened with the "a" or "a+" access type, all write operations take place
        // at the end of the file. Although the file pointer can be repositioned using fseek or rewind,
        // the file pointer is always moved back to the end of the file before any write operation is
        // carried out. Thus, existing data cannot be overwritten.
        // 
        // since this statement is wrong, we'll have to do the seek manually
        if (global.logfileappend) fseek(stdout, 0, SEEK_END);
#endif
	if (!global.samelogs) {
		if (!freopen(global.errlog == NULL ? NULLDEVICE : global.errlog , global.errlogappend ? APPENDFLAG : "w", stderr)) {
			status->severity = SEVERITY_FATAL;
			status->msg = ERROPEN_FAILED;
			status->syserror = errno;
			return;
		}
#ifdef WINDOWS
        	if (global.errlogappend) fseek(stderr, 0, SEEK_END);
#endif
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

	pl = getenv(JOBEXECUTORLOG);
	if (pl == NULL) {
		myLog = NULL;
		return;
	} else 
		snprintf(privateLog, PATH_MAX, "%s.%d", pl, (int) getpid());

#ifndef WINDOWS
	myLogFd = open(privateLog, O_WRONLY|O_SYNC|O_CREAT, mode);
	if (myLogFd < 0) {
		myLog = NULL;	/* TODO: differentiate between causes */
		return;
	}

	/* myLog will be NULL if this fails */
	myLog = fdopen(myLogFd, "w");
#else
	myLog = CreateFile (
			privateLog,
			GENERIC_WRITE,          // access mode
			FILE_SHARE_READ,	// share mode
			NULL,                   // security attributes
			CREATE_ALWAYS,          // how to create
			FILE_ATTRIBUTE_NORMAL,  // file attributes
			NULL);                  // handle to template file
	if (myLog == INVALID_HANDLE_VALUE) {
		myLog = NULL;
	}

#endif

	return;
}

void closeLog(callstatus *status)
{
	if (myLog != NULL)
#ifndef WINDOWS
		fclose(myLog);
#else
		CloseHandle(myLog);
#endif
	return;
}

char *getUniquePid(callstatus *status, pid_t pid)
{
	static char buf[64];	/* starttime + sep + how + boottime + sep + pid + \0
				//    20        1     1       20       1     20    1
				*/
	static const char *pattern = "%d@%c%s+%ld";
	snprintf(buf, 63, pattern, (int) pid, boottimeHow, (global.boottime == NULL ? "0" : global.boottime), global.myStartTime);
	/* TODO Error handling */

	return buf;
}

void run(callstatus *status)
{
	char **argv;
	int i;
	pid_t cpid;
#ifndef WINDOWS
	int   exitcode;
	int fd, fds;
#else
	DWORD exitcode;
#endif
	char  *execpid;
	char  buf[20];	/* for the exit code, assumed maximally a 64 byte int */
	int exitstatus;

	execpid = getUniquePid(status, getpid());
	if (status->severity != STATUS_OK) return;

	/* We open and lock the taskfile here.
	 * Afterwards, we'll close it in both the parent and
	 * after some more appends, in the child (Unix Version).
	 * Although this seems to decrease concurrency, this is
	 * needed for W95/W98 and some more platforms, where no
	 * modern Java (like 1.6) is available. It turned out that
	 * Java 1.4 sometimes starts multiple processes within _one_
	 * RunTime.exec() call.
	 * Since we lock here, and release first when we wrote the
	 * RUNNING state, this can be used to detect multiple starts
	 * of a jobexecutor (for the same taskfile)
	 */
	global.taskfile = openTaskfile(status);
	if (status->severity != STATUS_OK) return;	/* TODO: better error handling */

	appendTaskfile(status, EXECPID, execpid, 1);
	if (status->severity != STATUS_OK) return;

	/* 1. build command array */
	argv = (char **) malloc((1 + global.num_args + 1) * sizeof(char*));
	if (argv == NULL) {
		status->severity = SEVERITY_FATAL;
		status->msg = OUT_OF_MEM;
		return;
	}
	argv[0] = global.command;
	for (i = 0; i < global.num_args; ++i) {
		argv[i+1] = global.argument[i];
	}
	argv[i + 1] = NULL;

	/* 2. redirect stdout, stderr */

	redirect(status);
	if (status->severity != STATUS_OK) return;
#ifndef WINDOWS

	/* release the lock from the taskfile first */
/*	closeTaskfile(status, global.taskfile); */
	fflush(global.taskfile);

	/* now we fork() and the child will do the rest */
	cpid = fork();

	if (cpid < 0) {
		/* something went wrong */
		status->severity = SEVERITY_FATAL;
		status->msg = FORK_FAILED;
		status->syserror = errno;
		return;
	}
	if (cpid > 0) {
		/* parent -> wait for child */

		/* release the lock from the taskfile first */
		closeTaskfile(status, global.taskfile);

		exitcode = -1;
		while (exitcode == -1) {
			exitstatus = 0;
			waitpid(cpid, &exitstatus, 0);
			if (WIFEXITED (exitstatus))
				exitcode = WEXITSTATUS (exitstatus);
			else if (WIFSIGNALED (exitstatus))
				exitcode = 128 + WTERMSIG (exitstatus);
		}

		if (global.verboselogs) {
			fprintf(stdout, "------- %s End (%d) --------\n", getTimestamp(time(NULL), 1), exitcode);
		}

		/* something might have gone wrong starting the process
		   so we read the taskfile again and ask for an error
		   if an error has been set, we don't add any information,
		   if not, we write the exit code and status
		*/
		processTaskfile(status);
		if (global.error != NULL)
			return;

		snprintf(buf, 20, "%d", exitcode);
		global.taskfile = openTaskfile(status);
		if (status->severity != STATUS_OK) return;
		appendTaskfile(status, RETURNCODE, buf, 1);
		if (status->severity != STATUS_OK) return;
		appendTaskfile(status, STATUS, (char *) STATUS_FINISHED, 1);
		if (status->severity != STATUS_OK) return;
		closeTaskfile(status, global.taskfile);
		return;
	}
	global.myStartTime = time(NULL);	/* we're in the child, so we can overwrite the start time */
	global.taskfile = openTaskfile(status);

	default_all_signals (status);
	if (status->severity != STATUS_OK) {
		if (myLog != NULL) Hprintf(myLog, "%s\n", renderError(status));
		status->severity = STATUS_OK;
		status->msg = MSG_NO_ERROR;
	}
	global.extpid = getUniquePid(status, getpid());
	if (status->severity != STATUS_OK) return;

	appendTaskfile(status, EXTPID, global.extpid, 1);
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, STATUS, (char *) STATUS_RUNNING, 1);
	if (status->severity != STATUS_OK) return;

	closeTaskfile(status, global.taskfile);

	/* since the task file has been closed here, all filehandles other than 
	   the standard filehandles should be closed.
	   It is not guaranteed this is the case though. So we'll close all non standard
	   file handles here
	*/
	if ((fds = getdtablesize()) == -1) fds = OPEN_MAX;
	for (fd = 3; fd < fds; ++fd) {
		close(fd);	// ignore errors; most fd's aren't open
	}

	/* start a new session. This way we will be in a new process group
	   and we (and our children) can be easily killed by a "kill -PID".
	   We also don't have a controlling tty. This is normally that what
	   we want. (and it closes some potential security issues)
	*/
	if (setsid() == -1) {
		/* could not set group, permission thing */
		/* we ignore this for now */
	}

	if (global.verboselogs) {
		fprintf(stdout, "------- %s Start --------\n", getTimestamp(time(NULL), 1));
		fflush(stdout);		/* seems to be necessary */
	}

	/* 3. start user process */
	if (global.usepath) {
		execvp(global.command, argv);
	} else {
		execv(global.command, argv);
	}

	status->severity = SEVERITY_FATAL;
	status->msg = EXEC_FAILED;
	status->syserror = errno;
#else
	if (global.verboselogs) {
		fprintf(stdout, "------- %s Start --------\n", getTimestamp(time(NULL), 1));
		fflush(stdout);		/* seems to be necessary */
	}
        // It seems that Windows only really *appends* to files if something has happend with them before the child is started,
        // even if the file has been reopen()ed with "a"! (I really *LOVE* that too!!!)
        // So by explicitly moving the filepointer to the end of the stream, even Windows can't refuse to *append* child's output...
        else {
                fseek (stdout, 0, SEEK_END);
                fseek (stderr, 0, SEEK_END);
        }

	/* build commandline (windows doesn't know what to do with a correctly parsed command array) */
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

	/* now we need the "module" if windows shouldn't search the path */
	char *module = global.usepath ? NULL : global.command;
	if (module && (*module == '"')) {               // module *must not* have quotes...
		char *unquoted = Strdup(status, module + 1);
		if (status->severity != STATUS_OK) {
			return;
		}
		unquoted[strlen(unquoted) - 1] = '\0';
		module = unquoted;
	}

	/* start the child process */
	STARTUPINFO si;
	ZeroMemory (&si, sizeof (si));
	si.cb = sizeof (si);

	PROCESS_INFORMATION pi;

	if (! CreateProcess (
			module,                         // module name
			cmdline,                        // command line
			NULL,                           // don't inherit process handle
			NULL,                           // don't inherit thread handle
			TRUE,                           // do    inherit open handles
			0,                              // creation flags
			NULL,                           // use parent's environment
			NULL,                           // use parent's current directory
			&si,
			&pi)) {
		status->severity = SEVERITY_FATAL;
		status->msg = EXEC_FAILED;
		status->syserror = GetLastError();
		return;
	}

	global.extpid = getUniquePid(status, pi.dwProcessId);
	if (status->severity != STATUS_OK) return;

	/* The taskfile has been opened already */
	appendTaskfile(status, EXTPID, global.extpid, 1);
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, STATUS, (char *) STATUS_RUNNING, 1);
	if (status->severity != STATUS_OK) return;
	closeTaskfile(status, global.taskfile);

	CloseHandle (pi.hThread);

	/* now wait for the child to terminate */
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

	snprintf(buf, 20, "%d", exitcode);
	global.taskfile = openTaskfile(status);
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, RETURNCODE, buf, 1);
	if (status->severity != STATUS_OK) return;
	appendTaskfile(status, STATUS, (char *) STATUS_FINISHED, 1);
	if (status->severity != STATUS_OK) return;
	closeTaskfile(status, global.taskfile);

	CloseHandle (pi.hProcess);	/* ignore errors */

	if (global.verboselogs) {
		fprintf(stdout, "------- %s End (%d) --------\n", getTimestamp(time(NULL), 1), exitcode);
		fflush(stdout);
	}

#endif
	return;
}


int main(int argc, char *argv[])
{
	callstatus status;
	int argsType = ARGS_NOTREAD;

	global.myStartTime = time(NULL);

	initFields();
	status.severity = STATUS_OK;
	status.msg = MSG_NO_ERROR;

	openLog(&status);

	argsType = checkArgs(&status, argc, argv);
	switch (argsType) {
		case ARGS_NOTREAD:
			/* some error occurred; TODO: be more specific in error message */
			fprintf(stderr, "%s\n", renderError(&status));
			fprintf(stderr, "%s", getUsage());
			exit(1);
		case ARGS_VERSION:
			fprintf(stdout, "%s", getVersion());
			exit(0);
		case ARGS_HELP:
			fprintf(stdout, "%s", getUsage());
			exit(0);
		case ARGS_RUN:
			if (myLog != NULL)
				printJobFields();
			ignore_all_signals (&status);
			if (status.severity != STATUS_OK) {
				if (myLog != NULL) Hprintf(myLog, "%s\n", renderError(&status));
				status.severity = STATUS_OK;
				status.msg = MSG_NO_ERROR;
			}
			run(&status);
			if (status.severity != STATUS_OK) {
				/* TODO write error */
				if (myLog != NULL) Hprintf(myLog, "%s\n", renderError(&status));
			}
			closeLog(&status);
			break;
		default:
			/* cannot happen */
			fprintf(stderr, "Argument evaluation returned an unexpected value : %d\n", argsType);
			fprintf(stderr, "%s", getUsage());
			exit(1);
	}
	if (status.severity == SEVERITY_FATAL) {
		/* we now try to report the error to the scheduling server by writing it into 
		   the taskfile. This might or might not be successful. We hope for the best.
		   All non job related errors (incorrect call of jobexecutor) are handled in
		   the previous switch() statement.
		*/
		callstatus tmp;

		tmp.severity = STATUS_OK;
		tmp.msg = MSG_NO_ERROR;
#ifndef WINDOWS
		global.taskfile = openTaskfile(&tmp);
		if (tmp.severity != STATUS_OK) return status.msg;
#endif
		appendTaskfile(&tmp, S_ERROR, renderError(&status), 1);
		if (tmp.severity != STATUS_OK) return status.msg;
		appendTaskfile(&tmp, STATUS, (char *) STATUS_ERROR, 1);
		if (tmp.severity != STATUS_OK) return status.msg;
		closeTaskfile(&tmp, global.taskfile);
		
		/* return status.msg; */ /* TODO: interpret the exit code of the jobexecutor within jobserver */
	}

	return 0;
}
