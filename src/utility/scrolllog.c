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


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdarg.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#if defined(LINUX) || defined(AIX)
#include <sys/file.h>
#endif
#include <limits.h>
#include <dirent.h>
#include <signal.h>
#include "scrolllog.h"
#include "common.h"

static const char default_output[] = "/dev/null";

static int no_of_segments = 3;
static int no_of_lines = 100000;
static char *logbasename = NULL;
static char *pipename = NULL;
static char *outputfile = (char*) default_output;
static char *workdir = NULL;
static char **cmd = NULL;
static int run_as_daemon = 1;
static int last_logno = 0;
static int first_logno = INT_MAX;
static int verbose = 0;
static volatile int terminate = 0;
static volatile int restart = 1;
static int status = 0;
static FILE *paip;
static int fnout;

static const char *ANALYZER = "SCROLLLOGANALYZER";
static char *analyzer = NULL;

void usage(char *argv[], char *msg)
{
	if(msg != NULL) {
		fprintf(stdout, "Error: %s\n\n", msg);
	}
	fprintf(stdout, "Usage: \n");
	fprintf(stdout, "%s [OPTIONS] name [-e cmdline]\n\n", argv[0]);
	fprintf(stdout, "Options:\n");
	fprintf(stdout, "-s NUMBER\tnumber of segments (default 3)\n");
	fprintf(stdout, "-l NUMBER\tnumber of lines per segment (default 100.000)\n");
	fprintf(stdout, "-n NAME  \tbasename of outputfiles (default same as named pipe)\n");
	fprintf(stdout, "-f\t\tRun in foreground (don't daemonize); log messages are written to stdout too\n");
	fprintf(stdout, "-h\t\tDisplays this message\n");
	fprintf(stdout, "-v\t\tDisplays options\n");
	fprintf(stdout, "-o FILESPEC\tFile for own errormessages (only valid if daemon, default = /dev/null)\n\n");
	fprintf(stdout, "-w WORKDIR\tWorking Directory, default = /\n");
	fprintf(stdout, "name is the name of a named pipe\n");
	fprintf(stdout, "-e cmdline\tstarts the command with stdout and stderr redirected to the named pipe\n");
	fprintf(stdout, "\t\t%s then terminates after the child process has terminated with exit code 0\n", argv[0]);
	fprintf(stdout, "\t\tIf the child process terminates with exit code other than 0, it is restarted\n");
	fprintf(stdout, "\t\tThe -e cmdline _MUST_ be the last option on the commandline. Everything following\n");
	fprintf(stdout, "\t\tthe -e parameter is considered to be part of the commandline\n");
	fprintf(stdout, "\t\tThe childprocess is started with the same environment as the calling process\n");
	fprintf(stdout, "\t\tThe PATH environment variable is used\n");
	fprintf(stdout, "\t\tWith the -D flag environment variables can be set\n");
	exit(1);
}

#ifdef HPUX
char * strndup(char *src, size_t len)
{
	int i;
	char *result = malloc(len + 1);
	if (result == NULL)
		return NULL;

	for (i = 0; i < len && src[i]; ++i)
		result[i] = src[i];
	result[i] = '\0';

	return result;
}
#endif

void exit_handler(int p)
{

	if(childpid)
		kill(childpid, p);
	terminate = 1;
	restart = 0;
	return;
}

void hup_handler(int p)
{

}

int check_basename(char *name)
{
	check_name(name, &logbasename);
	return 0;
}

int check_pipename(char *name)
{
	struct stat buf;

	check_name(name, &pipename);
	if( stat(pipename, &buf)) {
		fprintf(stderr, "Error in stat() of pipe %s\n", pipename);
		return 1;
	}
	if(!S_ISFIFO(buf.st_mode)) {
		fprintf(stderr, "%s seems not to be a named pipe\n", pipename);
		return 1;
	}
	return 0;
}

void getopts(int argc, char *argv[])
{
	int i;
	int got_nos = 0;
	int got_nol = 0;
	int got_bn  = 0;
	int got_pn  = 0;
	int got_of  = 0;
	int got_wd  = 0;
	int got_cmd = 0;
	char *endptr;

	for(i = 1; i < argc; i++) {
		if(!strcmp(argv[i], "-s")) {
			i++;
			if(i >= argc)	usage(argv, (char*) "Expected another parameter (number of segments)");
			if(got_nos)	usage(argv, (char*) "Number of segments specified twice");
			no_of_segments = (int) strtol(argv[i], &endptr, 10);
			if((*endptr != '\0') || (no_of_segments < 0))	usage(argv, (char*) "Invalid numeric value for number of segments");
			got_nos = 1;
		} else if(!strcmp(argv[i], "-l")) {
			i++;
			if(i >= argc)	usage(argv, (char*) "Expected another parameter (number of lines per segment)");
			if(got_nol)	usage(argv, (char*) "Number of lines per segment specified twice");
			no_of_lines = (int) strtol(argv[i], &endptr, 10);
			if((*endptr != '\0') || (no_of_lines < 1))	usage(argv, (char*) "Invalid numeric value for number of lines per segment");
			got_nol = 1;
		} else if(!strcmp(argv[i], "-n")) {
			i++;
			if(i >= argc)	usage(argv, (char*) "Expected another parameter (basename)");
			if(got_bn)	usage(argv, (char*) "Basename of outputfiles specified twice");
			if(check_basename(argv[i]))	usage(argv, (char*) "Invalid basename");
			got_bn = 1;
		} else if(!strcmp(argv[i], "-o")) {
			i++;
			if(i >= argc)	usage(argv, (char*) "Expected another parameter (outputfile)");
			if(got_of)	usage(argv, (char*) "Outputfile specified twice");
			if(check_name(argv[i], &outputfile))	usage(argv, (char*) "Invalid outputfilename");
			got_of = 1;
		} else if(!strcmp(argv[i], "-w")) {
			i++;
			if(i >= argc)	usage(argv, (char*) "Expected another parameter (workdir)");
			if(got_wd)	usage(argv, (char*) "Workdir specified twice");
			if(check_name(argv[i], &workdir))	usage(argv, (char*) "Invalid workdir");
			got_wd = 1;
		} else if(!strcmp(argv[i], "-e")) {
			i++;
			if(i >= argc)	usage(argv, (char*) "Expected another parameter (commandline)");
			if(got_cmd)	usage(argv, (char*) "Command specified twice");
			cmd = argv+i;
			got_cmd = 1;
			i = argc;
		} else if(!strcmp(argv[i], "-h")) {
			usage(argv, NULL);
		} else if(!strcmp(argv[i], "-v")) {
			verbose = 1;
		} else if(!strcmp(argv[i], "-f")) {
			run_as_daemon = 0;
		} else if(!strcmp(argv[i], "-D")) {
			i++;
			if(i >= argc)	{
				usage(argv, (char*) "Expected another parameter (environment variable specification)");
			}
			if(parseAndSetEnvironment(argc, argv, &i))	{
				usage(argv, (char*) "Invalid environment specification");
			}
		} else {
			if(got_pn)	usage(argv, (char*) "Pipename specified twice");
			if(check_pipename(argv[i]))	usage(argv, (char*) "Invalid pipename");
			got_pn = 1;
		}
	}
	if(!got_pn)	usage(argv, (char*) "Pipename missing");
	if(!got_bn)	logbasename = pipename;
	if(no_of_segments < 2)	usage(argv, (char*) "Number of segments should be at least 2");
	analyzer = getenv(ANALYZER);
}

int getlognumber()
{
	char *dirname;
	char basename[NAME_MAX + 1];
	unsigned int i, bnl;
	DIR *dir;
	struct dirent *entry;
	char *errtok;
	int logno;

	dirname = strdup(logbasename);
	if(dirname == NULL) {
		fprintf(stderr, "Error with strdup() of log basename\n");
		return 1;
	}
	for(i = (unsigned int) strlen(dirname) - 1; dirname[i] != '/'; i--)  ;
	if(i + NAME_MAX < strlen(dirname)) {
		fprintf(stderr, "Invalid filename %s (name too long)\n", dirname);
		return 1;
	}
	strncpy(basename, dirname + i + 1, NAME_MAX);
	dirname[i] = '\0';
	bnl = (unsigned int) strlen(basename);

	dir = opendir(dirname);
	if(dir == NULL) {
		fprintf(stderr, "Error reading directory %s : %s\n", dirname, strerror(errno));
		return 1;
	}

	while((entry = readdir(dir)) != NULL) {
		if(!strncmp(basename, entry->d_name, bnl)) {

			logno = (int) strtol(&(entry->d_name[bnl + 1]), &errtok, 10);
			if(*errtok == '\0') {
				if(logno > last_logno) last_logno = logno;
				if(logno < first_logno) first_logno = logno;
			}
		}
	}
	if(first_logno > last_logno) first_logno = last_logno;

	closedir(dir);

	return 0;
}

int parseAndSetEnvironment(int argc, char *argv[], int *idx)
{
	char *arg;
	int argptr = *idx;
	int arglen;
	int i;
	char *key;
	char *value;

	arg = argv[argptr];
	while (argptr < argc && arg[0] != '-') {

		arglen = strlen(arg);
		for (i = 0; i < arglen && arg[i] != '='; i++)
			;
		if (i >= arglen) {
			return 1;
		}
		key = strndup(arg, i);
		if (i+1 >= arglen) {

			value = NULL;
			unsetenv(key);
		} else {
			value = strdup((char *) &arg[i+1]);
			setenv(key, value, 1 );
		}
		free(key);
		if (value != NULL) free(value);
		argptr++;
		if (argptr < argc) arg = argv[argptr];
	}
	*idx = argptr - 1;
	return 0;
}

void print_config()
{
	fprintf(stdout, "Pipe    : %s\n", pipename);
	fprintf(stdout, "LogBase : %s\n", logbasename);
	fprintf(stdout, "Segments: %d\n", no_of_segments);
	fprintf(stdout, "Lines   : %d\n", no_of_lines);
	fprintf(stdout, "Last    : %d\n", last_logno);
	fprintf(stdout, "First   : %d\n", first_logno);
}

int start_cmd()
{
	if((childpid = fork()) > 0) {
		fprintf(stderr, "%d\n", (int) childpid);
		return 0;
	}
	if(childpid < 0) {
		childpid = 0;
		return -1;
	}

	if(redirect(pipename)) {

		exit(0);
	}

	sleep(3);

	reset_sighandling();

	execvp(cmd[0], cmd);
	fprintf(stderr, "Couldn't start %s\nExiting ...", cmd[0]);
	_exit(0);

	return -1;
}

int mylock(int fd)
{
	int rc;

#ifdef SOLARIS
	fshare_t denyread;

	denyread.f_deny = F_RDDNY;
	denyread.f_access = F_RDACC;
	denyread.f_id = fd;

	rc = fcntl(fd, F_SHARE, &denyread);
#endif
#ifdef LINUX
	rc = flock(fd, LOCK_EX|LOCK_NB);
#endif
#if defined(BSD) || defined(AIX) || defined(HPUX)

	struct stat buf;
	static FILE *shadowfile=NULL;
	char *shadowname;

	shadowname = (char *) malloc(strlen(pipename) + 5);
	if(shadowname == NULL) {
		fprintf(stderr, "Error while allocating memory\n");
		return -1;
	}
	strcpy(shadowname,pipename);
	strcat(shadowname,".lck");
	if (stat(shadowname, &buf)<0) {
		if(errno == ENOENT) {
			shadowfile = fopen(shadowname,"a+");
			if(shadowfile == NULL) {
				fprintf(stderr, "Error creating shadowfile : %s\n", strerror(errno));
				return -1;
			}
		} else {
			fprintf(stderr, "Error checking shadowfile : %s\n", strerror(errno));
			return -1;
		}
	} else {
		if (!S_ISREG(buf.st_mode)) {
			fprintf(stderr, "Error shadowfile not regular: %d\n", buf.st_mode);
			return -1;
		} else {
			shadowfile = fopen(shadowname, "r+");
			if(shadowfile == NULL) {
				fprintf(stderr, "Error opening shadowfile : %s\n", strerror(errno));
				return -1;
			}
		}
	}
	fprintf(shadowfile, "%d\n", (int)getpid());

	rc = flock(fileno(shadowfile), LOCK_EX|LOCK_NB);

#endif

	return rc;
}

int open_pipe()
{

	paip = fopen(pipename, "r");
	if(paip == NULL) {
		fprintf(stderr, "Error opening pipe : %s\n", strerror(errno));
		return 7;
	}
	if (mylock(fileno (paip)) == -1) {
		if(errno == EWOULDBLOCK) {
			fprintf(stderr, "Error locking pipe : %s\n", "another scrolllog is running");
		} else {
			fprintf(stderr, "Error locking pipe : %s\n", strerror(errno));
		}
		return 8;
	}
	return 0;
}

int outf_printf(FILE *outf, const char *fmt, ...)
{
	va_list args;
	int rc;
	va_start (args, fmt);
	rc = vfprintf(outf, fmt, args);
	if (run_as_daemon == 0) {
		vfprintf(stdout, fmt, args);
	}
	va_end (args);
	fflush(outf);
	return rc;
}

int outf_puts(FILE *outf, char *s)
{
	int rc;
	rc = fputs(s, outf);
	fflush(outf);
	if (run_as_daemon == 0) {
		fputs(s, stdout);
	}
	return rc;
}

int process()
{
#define BUFSIZE 2048

	FILE *outf;
	char filename[PATH_MAX + 1];
	char analyzeline[PATH_MAX*3 + 1];
	int lineno;
	static char buf[BUFSIZE+1];
	int rc;

	if(open_pipe()) {
		if(childpid)
			kill(childpid, SIGTERM);
		exit(8);
	}
	while(terminate != 2) {

		while(last_logno - first_logno > no_of_segments) {
			snprintf(filename, PATH_MAX, "%s.%d", logbasename, first_logno);
			if (analyzer != NULL) {
				snprintf(analyzeline, PATH_MAX*3, "%s %s", analyzer, filename);
				rc = system(analyzeline);
				if (rc < 0) {
					fprintf(stderr, "Error executing %s : %s\n", analyzeline, strerror(errno));
				}
			}
			unlink(filename);
			first_logno++;
		}

		last_logno++;
		snprintf(filename, PATH_MAX, "%s.%d", logbasename, last_logno);
		outf = fopen(filename, "w");
		if(outf == NULL) {
			fprintf(stderr, "Error opening file %s : %s\n", filename, strerror(errno));
			if(childpid) {
				kill(childpid, SIGTERM);
				sleep(10);
			}
			return 9;
		}
		fnout = fileno(outf);
		lineno = 0;
		while((lineno < no_of_lines) && (terminate != 2)) {
			if(fgets(buf, BUFSIZE, paip) == NULL) {
				fclose(paip);
				fflush(outf);
				if (childpid) {
					outf_printf(outf, "[scrolllog] Waiting for child (%d) to terminate\n", (int) childpid);
					do {
						switch ((int)waitpid((pid_t) -1, &status, 0)) {
						case -1:
							if(errno == ECHILD) {

								outf_printf(outf, "[scrolllog] WARNING, childpid != 0 && ECHILD !\n");

								restart   = 1;
							}
							break;
						default:
							if (WIFEXITED(status)) {
								status = WEXITSTATUS(status);
							} else if (WIFSIGNALED(status)) {
								status = 128 + WTERMSIG(status);
							} else
								continue;
							outf_printf(outf, "[scrolllog] Child exited with state %d\n", status);
							if (status == 0) restart = 0;
							else restart = 1;
						}

						childpid  = 0;
						terminate = 1;
					} while(childpid != 0);
				}
				if(terminate == 1) {
					if(restart && (cmd != NULL)) {
						outf_printf(outf, "[scrolllog] Try to restart child (child terminated with exit code <> 0)\n");
						if(start_cmd() != 0) {
							outf_printf(outf, "[scrolllog] restart of child failed\n");
							exit(9);
						}
						terminate = 0;
					} else {
						terminate = 2;
						outf_printf(outf, "[scrolllog] I terminate (after cleaning up)\n");
						break;
					}
				}
				if(open_pipe()) {
					terminate = 2;
					outf_printf(outf, "\n");
					outf_printf(outf, "\n[scrolllog] ****************************************");
					outf_printf(outf, "\n[scrolllog] **                                    **");
					outf_printf(outf, "\n[scrolllog] ** Couldn't open/lock pipe            **");
					if(childpid) {
						outf_printf(outf, "\n[scrolllog] ** scrolllog terminates and sends a   **");
						outf_printf(outf, "\n[scrolllog] ** SIGTERM to its child               **");
						kill(childpid, SIGTERM);
						sleep(10);
					}
					outf_printf(outf, "\n[scrolllog] **                                    **");
					outf_printf(outf, "\n[scrolllog] ****************************************\n");
					return 10;
				}

				continue;
			}
			outf_puts(outf, buf);
			lineno++;
		}
		fclose(outf);

	}

	return status;
}

int main(int argc, char *argv[])
{
	int rc;

	getopts(argc, argv);
	if(getlognumber()) {
		fprintf(stderr, "Cannot determine actual log number\n");
		exit(4);
	}
	if(run_as_daemon) {
		if((rc = daemonize(outputfile)) != 0) {
			fprintf(stderr, "Error starting daemon process : %s\n", strerror(rc));
			exit(5);
		}
	} else {
		if(redirect(outputfile)) {
			exit(6);
		}
	}
	if(workdir) {
		if(chdir(workdir)) {
			perror("Error changing directory: ");
			exit(7);
		}
	}
	if(verbose) {
		print_config();
	}

	if(cmd != NULL) {
		sighandling();
		if(start_cmd() != 0) exit(9);
	}
	return process();
}
