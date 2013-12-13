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
                        $(error UNKNOWN OPERATING SYSTEM $(OS))
                endif
        endif
endif


CLASSES = BICServer.class sdmsh.class 

SUBDIRS = server jobserver shell utility md5 SDMSApp tools demo 
JARDIRS = de

PROGRAM_VERSION = $(shell grep programVersion server/SystemEnvironment.java | sed 's/^[^"]*"\([^"]*\)".*$$/\1/')
BUILD_DATE = $(shell date '+%d.%m.%Y %H:%M')
COPYRIGHT = $(shell grep 'public static final String copyright' jobserver/Libjni.java | sed 's/^[^"]*"\([^"]*\)".*$$/\1/')
COMPANY = $(shell grep 'public static final String company' jobserver/Libjni.java | sed 's/^[^"]*"\([^"]*\)".*$$/\1/')

JAR_FILE = $(SDMSHOME)/lib/BICsuite.jar
MANIFEST = /tmp/sdms.manifest

ECHO = /bin/echo
ifeq ($(OSTYPE),sunos)
ECHOOPTS =
else
ECHOOPTS = -e
endif

all: prereq generate subdirs $(CLASSES) $(JAR_FILE)

new: clean all

jar:
	rm $(JAR_FILE)
	make $(JAR_FILE)

subdirs: commonsubdirs

prereq:
	cd $(SDMSHOME)/src; \
	mkdir -p de/independit/scheduler; \
	for fff in *; do \
		if [ "$${fff}" != "de" -a ! -e de/independit/scheduler/$${fff} ]; then \
			ln -s $(SDMSHOME)/src/$${fff} de/independit/scheduler/$${fff}; \
		fi; \
	done

generate: commongensubdirs

clean: commonclean
	rm -f $(JAR_FILE) $(MANIFEST)
	rm -f VERSION

$(JAR_FILE): BICServer.class sdmsh.class \
		$(shell find $(JARDIRS) -follow \( -type d -name test \) -prune -o -name \*.class -print) \
		$(shell find $(JARDIRS) -follow \( -type d -name test \) -prune -o -name parser.tables -print)
	cd $(SDMSHOME)/src; \
	for fff in *.class; do \
		if [ ! -e de/independit/scheduler/$${fff} ]; then \
			ln -s $(SDMSHOME)/src/$${fff} de/independit/scheduler/$${fff}; \
		fi; \
	done; \
	$(ECHO) $(ECHOOPTS) "Manifest-Version: 1.1\nProgram-Version: $(PROGRAM_VERSION)\nLevel: OPEN\nBuild-Date: $(BUILD_DATE)\nCopyright: $(COPYRIGHT) $(COMPANY)\nCompany: $(COMPANY)"; \
	$(ECHO) $(ECHOOPTS) "Manifest-Version: 1.1\nProgram-Version: $(PROGRAM_VERSION)\nLevel: OPEN\nBuild-Date: $(BUILD_DATE)\nCopyright: $(COPYRIGHT) $(COMPANY)\nCompany: $(COMPANY)" >$(MANIFEST); \
	$(JAR) $(JARCREATEFLAGS) $(MANIFEST) $(JAR_FILE) \
		de/independit/scheduler/BICServer.class \
		de/independit/scheduler/sdmsh.class \
		`find $(JARDIRS) -follow \( -type d -name test \) -prune -o -name \*.class -print` \
		`find $(JARDIRS) -follow \( -type d -name test \) -prune -o -name parser.tables -print`; \
	$(JAR) -i $(JAR_FILE); \
	touch $(JAR_FILE); \
	rm -f $(MANIFEST)

include $(SDMSHOME)/src/Makefile.CommonRules

#
#
