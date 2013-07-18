#
# Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH"
# 
# BICsuite!OpenScheduler Enterprise Job Scheduling System
# 
# independIT Integrative Technologies GmbH [http://www.independit.de]
# mailto:contact@independit.de
# 
# This file is part of BICsuite!OpenScheduler
# 
# BICsuite!OpenScheduler is is free software: 
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

SUBDIRS = .

CLASSES = JobServer.class \
	Config.class Descr.class EiThread.class Environment.class Feil.class \
	RepoIface.class Server.class Trace.class Utils.class \
	Notifier.class \
	WakeupThread.class HttpThread.class

BIN_PATH = $(SDMSHOME)/bin
LIB_PATH = $(SDMSHOME)/lib

BIN_FILE = $(BIN_PATH)/jobserver
LIB_FILE = $(LIB_PATH)/libBICsuite.so

DISTRIB = $(BIN_FILE) $(LIB_FILE)

.PHONY: all subdirs distrib clean new

all: subdirs distrib $(CLASSES)

subdirs: commonsubdirs

distrib: $(DISTRIB)

clean: commonclean
	-rm -f $(DISTRIB) *.o jobserver_*.h

new: clean all

include $(SDMSHOME)/src/Makefile.CommonRules

CXXFLAGS += -fno-exceptions -Wall -Wshadow \
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

$(BIN_FILE): jobserver.o $(LIB_FILE)
	$(CXX) -L$(LIB_PATH) -lBICsuite -o $@ $< -lBICsuite

$(LIB_FILE): libcommon.o libosdepend.o libjni.o 
	$(LD) $(LDLIBFLAGS) -o $@ $^ -lc

jobserver.o: jobserver.cc libjobserver.h de_independit_scheduler_jobserver_Utils.h libosdepend.h feil.h

libcommon.o: libcommon.cc libjobserver.h de_independit_scheduler_jobserver_Utils.h libosdepend.h feil.h libcommon.h

libosdepend.o: libunix.cc libjobserver.h de_independit_scheduler_jobserver_Utils.h libosdepend.h feil.h libcommon.h
	$(CXX) $(CXXFLAGS) -c -o $@ $<

libjni.o: libjni.cc libjobserver.h de_independit_scheduler_jobserver_Utils.h libosdepend.h feil.h

de_independit_scheduler_jobserver_Utils.h: Utils.class
	$(JAVAH) -classpath $(SDMSHOME)/src -jni de.independit.scheduler.jobserver.Utils

