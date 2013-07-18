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

CLASSES = ExitStateFilter.class Filter.class FutureFilter.class JobServerFilter.class JobStateFilter.class \
	NodeFilter.class OwnerFilter.class SeNameFilter.class SubmitUserFilter.class \
	RestartableFilter.class MasterFilter.class MasterIdFilter.class HistoryFilter.class \
	NamelistFilter.class MergedExitStateFilter.class UsageFilter.class \
	ParameterFilter.class JobidFilter.class WarningFilter.class


SUBDIRS = .

all: subdirs $(CLASSES)

new: clean all

subdirs: commonsubdirs

clean: commonclean
	rm -f *~

include $(SDMSHOME)/src/Makefile.CommonRules

