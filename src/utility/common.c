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
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <limits.h>
#include <string.h>
#include "common.h"

volatile pid_t childpid = 0;

int sighandling()
{
#define	Sigaction(a, b, c) if(sigaction((a), (b), (c)) == -1) return 1;
	struct sigaction sa;

	sa.sa_handler = MY_SIG_IGN;
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART;
	Sigaction(SIGINT,  &sa, NULL);
	Sigaction(SIGPIPE, &sa, NULL);
	Sigaction(SIGALRM, &sa, NULL);
	Sigaction(SIGTTIN, &sa, NULL);
	Sigaction(SIGTTOU, &sa, NULL);

	sa.sa_handler = exit_handler;
	Sigaction(SIGQUIT, &sa, NULL);
	Sigaction(SIGTERM, &sa, NULL);
	Sigaction(SIGUSR1, &sa, NULL);
	Sigaction(SIGUSR2, &sa, NULL);

	sa.sa_handler = hup_handler;
	Sigaction(SIGHUP,  &sa, NULL);

	sa.sa_flags |= SA_NOCLDSTOP;
	Sigaction(SIGCHLD, &sa, NULL);

	return 0;
}

int reset_sighandling()
{
	struct sigaction sa;

	sa.sa_handler = MY_SIG_DFL;
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART;
	Sigaction(SIGINT,  &sa, NULL);
	Sigaction(SIGPIPE, &sa, NULL);
	Sigaction(SIGALRM, &sa, NULL);
	Sigaction(SIGTTIN, &sa, NULL);
	Sigaction(SIGTTOU, &sa, NULL);
	Sigaction(SIGQUIT, &sa, NULL);
	Sigaction(SIGTERM, &sa, NULL);
	Sigaction(SIGUSR1, &sa, NULL);
	Sigaction(SIGUSR2, &sa, NULL);
	Sigaction(SIGHUP,  &sa, NULL);
	Sigaction(SIGCHLD, &sa, NULL);

#undef Sigaction

	return 0;
}

int check_name(char *name, char **target)
{
	char *curdir;

	if(*name != '/' && *name != '-') {
		curdir = (char *) malloc(PATH_MAX + 1 + strlen(name) + 1);
		if(curdir == NULL) {
			fprintf(stderr, "Error while allocating memory\n");
			return 1;
		}
		if(getcwd(curdir, PATH_MAX) == NULL) {
			exit(1);
		}
		sprintf(curdir + strlen(curdir), "/%s", name);
		*target = curdir;
	} else {
		*target = name;
	}
	return 0;
}

int redirect(char *outfile)
{
	int n;
	int flags = O_WRONLY|O_APPEND|O_SYNC;
	struct stat buf;

	if(strcmp(outfile, "-")) {
		if( stat(outfile, &buf) ) {
			if(errno == ENOENT) {
				flags = O_WRONLY|O_CREAT|O_SYNC;
			} else {
				exit(14);
			}
		}

		n = open(outfile, flags, S_IRUSR|S_IWUSR|S_IRGRP);
		if(n < 0) {
			perror(" error opening file ");
			exit(10);
		}
		if(dup2(n, fileno(stdout))<0) {
			perror(" error dupping stdout ");
			exit(11);
		}
		if(dup2(n, fileno(stderr))<0) {

			exit(12);
		}
		close(n);
	}

	return 0;
}

int daemonize(char *outfile)
{
	pid_t pid;

	if(outfile != NULL) {
		if(redirect(outfile)) return 1;
	}

	if((pid = fork()) != 0) {
		if(pid < 0) return -1;
		exit(0);
	}
	setsid();
	fclose(stdin);
	if(chdir("/") != 0) {
		;
	}

	if((pid = fork()) != 0) {
		if(pid < 0) return -1;
		exit(0);
	}

	return 0;
}

