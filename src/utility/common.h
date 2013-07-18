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


#ifndef __DAEMONIZE_H__
#define __DAEMONIZE_H__

extern volatile pid_t childpid;

extern void exit_handler(int p);
extern void hup_handler(int p);
extern void chld_handler(int p);
extern int sighandling(void);
extern int reset_sighandling(void);
extern int redirect(char *outfile);
extern int daemonize(char *outfile);
extern int check_name(char *name, char **target);

#ifdef SOLARIS
#define MY_SIG_DFL	(void(*)(int))0
#define MY_SIG_IGN	(void (*)(int))1
#else
#define MY_SIG_DFL	SIG_DFL
#define MY_SIG_IGN	SIG_IGN
#endif

#endif
