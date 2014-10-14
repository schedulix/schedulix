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

# test for "GNU Make"
ifneq (,)
This makefile requires GNU Make.
endif

ifndef OSTYPE
OSTYPE := $(shell uname | tr '[A-Z]' '[a-z]')
endif

ifeq ($(OSTYPE),linux)
        LINUX=1
else
        ifeq ($(OSTYPE),netbsdelf)
                NETBSD=1
        else
                ifeq ($(OSTYPE),sunos)
                        SOLARIS=1
                else
                        $(error UNKNOWN OPERATING SYSTEM $(OSTYPE))
                endif
        endif
endif

ifdef LINUX
JAVAINC   = $(JAVAHOME)/include
JAVAINCOS = $(JAVAINC)/linux
MYSTRIP   = $(STRIP)
else
ifdef SOLARIS
JAVAINC   = $(JAVAHOME)/include
JAVAINCOS = $(JAVAINC)/solaris
MYSTRIP   = /bin/true
else
ifdef NETBSD
JAVAINC   = $(JAVAHOME)/include
JAVAINCOS = $(JAVAINC)/solaris
MYSTRIP   = $(STRIP)
endif
endif
endif

SUBDIRS = jexecutor

CLASSES = JobServer.class \
	Config.class Descr.class EiThread.class Environment.class Feil.class \
	RepoIface.class Server.class Trace.class Utils.class \
	Notifier.class \
	WakeupThread.class HttpThread.class

BIN_PATH = $(SDMSHOME)/bin
LIB_PATH = $(SDMSHOME)/lib

JOBEXECUTOR = $(BIN_PATH)/jobexecutor

DISTRIB = $(JOBEXECUTOR)

.PHONY: all generate subdirs distrib clean new

all: subdirs distrib $(CLASSES)

generate:

subdirs: commonsubdirs

distrib: $(DISTRIB)

clean: commonclean
	-rm -f $(DISTRIB) *.o

new: clean all

include $(SDMSHOME)/src/Makefile.CommonRules

CXXFLAGS += -g -fno-exceptions -Wall -Wshadow \
	-Wpointer-arith -Wwrite-strings -Wconversion \
	-Woverloaded-virtual \
	-I$(JAVAINC) -I$(JAVAINCOS)

ifdef LINUX
CXXFLAGS   += -DLINUX -fPIC
LDLIBFLAGS += -shared
else
ifdef SOLARIS
CXXFLAGS   += -DSOLARIS
LDLIBFLAGS += -dy -G
else
ifdef NETBSD
CXXFLAGS   += -DNETBSD
LDLIBFLAGS += -shared
endif
endif
endif

jobexecutor.o: jobexecutor.c
	$(CXX) -c $(CXXFLAGS) $<

$(JOBEXECUTOR): jobexecutor.o
	$(CXX) -o $@ $<

#
# End of File
#
