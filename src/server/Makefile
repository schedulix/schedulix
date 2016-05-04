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

CLASSES = UserConnection.class ListenThread.class WorkerThread.class \
	Server.class SchedulingThread.class TimerThread.class \
	SystemEnvironment.class ShutdownThread.class RenewTicketThread.class \
	ConnectionEnvironment.class GarbageThread.class InternalSession.class \
	OrdinaryListenThread.class

SUBDIRS = parser util repository exception output dump timer locking

all: generate subdirs $(CLASSES)

new: clean all

generate: commongensubdirs

subdirs: commonsubdirs

clean: commonclean

include $(SDMSHOME)/src/Makefile.CommonRules

