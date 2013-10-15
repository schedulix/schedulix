#
# Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH"
# 
# schedulix Enterprise Job Scheduling System
# 
# independIT Integrative Technologies GmbH [http://www.independit.de]
# mailto:contact@independit.de
# 
# This file is part of schedulix
# 
# schedulix is is free software: 
# you can redistribute it and/or modify it under the terms of the 
# GNU Affero General Public License as published by the 
# Free Software Foundation, either version 3 of the License, 
# or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.

LINUX=1

SUBDIRS = .

CFILES = scrolllog.c common.c
HFILES = scrolllog.h common.h
SLOBJS = scrolllog.o common.o
OBJS = $(SLOBJS) $(WDOBJS)

EXEC = $(SDMSHOME)/bin/scrolllog 

DISTRIB = $(EXEC)

.PHONY: all generate subdirs distrib clean new

all: subdirs distrib

generate:

subdirs: commonsubdirs

distrib: $(DISTRIB)

clean: commonclean
	-rm -f $(DISTRIB) *.o

new: clean all

include $(SDMSHOME)/src/Makefile.CommonRules

CXXFLAGS += -fno-exceptions -pedantic -Wall -Wshadow -Wpointer-arith \
	-Wwrite-strings -Wconversion -Wstrict-prototypes -Wmissing-prototypes \
	-Wmissing-declarations -Wnested-externs

ifdef LINUX
CXXFLAGS += -DLINUX -Winline
else
ifdef NETBSD
CXXFLAGS += -DNETBSD -Winline
else
ifdef SOLARIS
CXXFLAGS += -DSOLARIS
endif
endif
endif

.c.o:
	$(CC) $(CXXFLAGS) -c $<

$(SDMSHOME)/bin/scrolllog: $(SLOBJS)
	$(CC) $(SLOBJS) -o $@

