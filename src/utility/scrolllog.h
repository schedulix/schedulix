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


#ifndef __SCROLLLOG_H__
#define __SCROLLLOG_H__

#ifndef NAME_MAX
#define NAME_MAX 256
#endif

extern void usage(char *argv[], char *msg);
extern int parseAndSetEnvironment(int argc, char *argv[], int *idx);
extern int check_basename(char *name);
extern int check_pipename(char *name);
extern void getopts(int argc, char *argv[]);
extern int getlognumber(void);
extern void print_config(void);
extern int start_cmd(void);
extern int process(void);
extern int mylock(int fd);
extern int open_pipe(void);

#endif
