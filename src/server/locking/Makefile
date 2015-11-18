#
# Makefile
#
# Copyright (C) 2000-2002 topIT Informationstechnologie GmbH
# Copyright (C) 2003-2015 independIT Integrative Technologies GmbH
#

CLASSES = DeadlockException.class SerializationException.class InterruptedLockException.class LockingSystem.class \
	LockingSystemSynchronized.class Makefile ObjectLock.class SyncLock.class NotMyDeadlockException.class

SUBDIRS = .

all: generate subdirs $(CLASSES)

new: clean all

generate: commongensubdirs

subdirs: commonsubdirs

clean: commonclean

include $(SDMSHOME)/src/Makefile.CommonRules

#
#
