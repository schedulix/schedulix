###############################################################################
#
# Makefile
#
# copyright (c) 2013 independIT integrative Technologies GmbH
# all rights reserved
#
###############################################################################

JAVAINC   = $(JAVAHOME)/include
MYSTRIP   = $(STRIP)

SUBDIRS = .

CLASSES = Jexecutor.class JProcessExecutor.class

.PHONY: all generate subdirs clean new

all: generate subdirs $(CLASSES)

generate: commongensubdirs

subdirs: commonsubdirs

clean: commonclean

new: clean all

include $(SDMSHOME)/src/Makefile.CommonRules

#
# End of File
#
