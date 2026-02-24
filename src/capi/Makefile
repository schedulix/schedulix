#
# Makefile
#
# Copyright (C) 2016 independIT Integrative Technologies GmbH
#

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
        ifneq ($(shell echo $(OSTYPE) | grep bsd),)
                BSD=1
        else
                ifeq ($(OSTYPE),sunos)
                        SOLARIS=1
                else
                        ifeq ($(OSTYPE),aix)
                                LINUX=1
                                AIX=1
                        else
                                $(error UNKNOWN OPERATING SYSTEM $(OSTYPE))
                        endif
                endif
        endif
endif


SUBDIRS = .

JSSTUB_OBJS=jsstub.o jsstubParseConfig.o jsstubDonut.o sdms_api.o
TEST_OBJS=sdms_api.o sdms_test.o
HFILES=sdms_api.h sdms_api_internal.h jsstub.h
JSSTUB=jsstub
SDMS_TEST=sdms_test

all: $(JSSTUB) $(SDMS_TEST)

clean: commonclean
	rm -f *.o $(JSSTUB) $(SDMS_TEST)

new: clean all

generate: commongensubdirs

subdirs: commonsubdirs

CXXFLAGS += -g -fno-exceptions -Wall -Wshadow -Wpointer-arith \
        -Wwrite-strings -Wstrict-prototypes \
        -Wmissing-declarations -Wnested-externs

ifdef LINUX
CXXFLAGS += -DLINUX -Winline
else
ifdef BSD
CXXFLAGS += -DBSD -Winline
else
ifdef SOLARIS
CXXFLAGS += -DSOLARIS
endif
endif
endif
ifdef AIX
CXXFLAGS += -DAIX
endif


jsstub.o: jsstub.c $(HFILES)
	$(CC) $(CXXFLAGS) -I . -c $< 
	
jsstubParseConfig.o: jsstubParseConfig.c $(HFILES)
	$(CC) $(CXXFLAGS) -I . -c $< 

jsstubDonut.o: jsstubDonut.c $(HFILES)
	$(CC) $(CXXFLAGS) -I . -c $< 
       
sdms_api.o: sdms_api.c $(HFILES)
	$(CC) $(CXXFLAGS) -I . -c $< 

sdms_test.o: sdms_test.c $(HFILES)
	$(CC) $(CXXFLAGS) -I . -c $< 

$(JSSTUB): $(JSSTUB_OBJS)
	$(CC) $(JSSTUB_OBJS) -o $(JSSTUB)

$(SDMS_TEST): $(TEST_OBJS)
	$(CC) $(TEST_OBJS) -o $(SDMS_TEST)

include $(SDMSHOME)/src/Makefile.CommonRules
